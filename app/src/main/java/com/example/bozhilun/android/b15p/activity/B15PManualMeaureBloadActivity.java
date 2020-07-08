package com.example.bozhilun.android.b15p.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PBloodDB;
import com.example.bozhilun.android.b15p.b15pdb.B15PDBCommont;
import com.example.bozhilun.android.b15p.b15pdb.B15PTestBloopDB;
import com.example.bozhilun.android.b30.adapter.B30BloadDetailAdapter;
import com.example.bozhilun.android.b30.b30view.CustomCircleProgressBar;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.Health_HeartBldPrs;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class B15PManualMeaureBloadActivity extends WatchBaseActivity {
    private static final String TAG = "B15PManualMeaureBloadAc";
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30MeaurePlaceHolderImg)
    ImageView b30MeaurePlaceHolderImg;

    @BindView(R.id.b30MeaureBloadProgressView)
    CustomCircleProgressBar b30MeaureBloadProgressView;
    @BindView(R.id.b30MeaureStartImg)
    ImageView b30MeaureStartImg;
    @BindView(R.id.b30DetailBloadRecyclerView)
    RecyclerView b30DetailBloadRecyclerView;
    //开始或者停止测量的标识
    private boolean isStart = false;

    private B30BloadDetailAdapter b30BloadDetailAdapter;
   // private List<HalfHourBpData> dataList;

    private List<CusVPHalfHourBpData> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b15p_test_bload_activity);
        ButterKnife.bind(this);

        initViews();


    }

    @Override
    protected void onResume() {
        super.onResume();

        showLoadingDialog(getResources().getString(R.string.dlog));
        handler.sendEmptyMessage(0x13);
    }

    private void initViews() {
        commentB30TitleTv.setText(R.string.blood_manual_test);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.GONE);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30DetailBloadRecyclerView.setLayoutManager(layoutManager);
        b30DetailBloadRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        dataList = new ArrayList<>();
        b30BloadDetailAdapter = new B30BloadDetailAdapter(B15PManualMeaureBloadActivity.this, dataList);
        b30DetailBloadRecyclerView.setAdapter(b30BloadDetailAdapter);

        b30MeaureBloadProgressView.setMaxProgress(100);
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.b30MeaureStartImg})
//, R.id.private_mode_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B15PManualMeaureBloadActivity.this);
                break;
//            case R.id.private_mode_setting:   //  私人模式设置
//                Intent intent = new Intent(this, PrivateBloadActivity.class);
//                startActivity(intent);
//                break;
            case R.id.b30MeaureStartImg:    //开始或者停止测量

                if (MyCommandManager.DEVICENAME != null) {
                    if (!isStart) {


                        testBlood();
                    } else {
                        stopMeaureBoload();
                    }
                } else {
                    ToastUtil.showShort(this, getResources().getString(R.string.disconnted));
                }

                break;
        }
    }


    /**
     * 手动测量血压
     */
    void testBlood() {
        Health_HeartBldPrs.Get_BldPrsMeasureResult(new Health_HeartBldPrs.BldPrsResultListener() {
            @Override
            public void OnErr(String EventStr, String ErrInfo) {
                if (EventStr.equals(Health_HeartBldPrs.START)) {
                    if (ErrInfo.equals(Health_HeartBldPrs.NOTSUPPORT)) {
                        Log.e(TAG, "====手环不支持血压测量");
                    }
                } else if (EventStr.equals(Health_HeartBldPrs.CONNECT)) {
                    switch (ErrInfo) {
                        case Health_HeartBldPrs.WRONGCONNECTION:
                            Log.e(TAG, "====蓝牙连接不正常");
                            break;
                        case Health_HeartBldPrs.ARESYNCHRONIZED:
                            Log.e(TAG, "====正在同步血压数据,稍后再试!");
                            break;
                        case Health_HeartBldPrs.CONNECTLATER:
                            Log.e(TAG, "====请稍后测量血压再试");
                            break;
                    }
                }
            }

            @Override
            public void OnOpen(String EventStr) {
                if (EventStr.equals(Health_HeartBldPrs.OPENSTART)) {
                    //测量开始
                    Log.e(TAG, "====血压测量开始");
                    b30MeaurePlaceHolderImg.setVisibility(View.GONE);
                    b30MeaureBloadProgressView.setVisibility(View.VISIBLE);
                    isStart = true;
                    b30MeaureStartImg.setImageResource(R.drawable.detect_bp_pause);
                    b30MeaureBloadProgressView.setTmpTxt(null);
                    b30MeaureBloadProgressView.setScheduleDuring(27 * 1000);
                    b30MeaureBloadProgressView.setProgress(100);
                } else if (EventStr.equals(Health_HeartBldPrs.OPENOK)) {
                    //打开测量成功，等待结果
                    //可以在界面做等待超时处理，超时后可以使用Health_HeartBldPrs.ForceClose_BldPrsMeasure();强制关闭
                    Log.e(TAG, "====打开血压测量成功，可以强制关闭 ");
                    handler.sendEmptyMessageAtTime(0x12, 40 * 1000);
                }
            }

            @Override
            public void OnClose(String EventStr) {
                if (EventStr.equals(Health_HeartBldPrs.CLOSE)) {
                    //测量关闭
                    Log.e(TAG, "====血压测量关闭 ");
                } else if (EventStr.equals(Health_HeartBldPrs.END)) {
                    //测量结束
                    Log.e(TAG, "====血压测量结束 ");
                }

            }

            @Override
            public void OnData(String EventStr, String DataInfo) {
                if (EventStr.equals(Health_HeartBldPrs.RESULTDATA)) {
                    //测量成果，返回数据
                    Log.e(TAG, "====血压测量成功，返回数据 " + DataInfo + "  " + EventStr);
                    stopMeaureBoload();


                    if (b30MeaureBloadProgressView != null) {
                        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_start);
                        b30MeaureBloadProgressView.stopAnim();
                    }
                    handler.sendEmptyMessage(0x11);

                } else if (EventStr.equals(Health_HeartBldPrs.FAIL)) {
                    //测量失败
                    Log.e(TAG, "====血压测量失败 " + DataInfo);
                    b30MeaureBloadProgressView.setTmpTxt("--/--");
                    stopMeaureBoload();
                }

            }
        });
    }


    private void update_View(String dateStr, boolean LoadDB) {
        if (LoadDB) {
            String tempAddr = L4M.GetConnectedMAC();
            if (tempAddr != null) {
                List<B15PTestBloopDB> testBloopAllDatas = B15PDBCommont.getInstance().findTestBloopAllDatas(tempAddr);
                if (testBloopAllDatas != null&&!testBloopAllDatas.isEmpty()) {
                    for (int i = 0; i < testBloopAllDatas.size(); i++) {
                        Log.e(TAG, "=======血压  血压 " + testBloopAllDatas.get(i).getBloodNumberH() + "/" + testBloopAllDatas.get(i).getBloodNumberL());
                        if (b30MeaureBloadProgressView != null)
                            b30MeaureBloadProgressView.setTmpTxt(testBloopAllDatas.get(i).getBloodNumberH() + "/" + testBloopAllDatas.get(i).getBloodNumberL());
                    }

                }
                List<B15PBloodDB> bloopAllDatas = B15PDBCommont.getInstance().findBloopAllDatas(tempAddr, dateStr);
                dataList.clear();
                if (bloopAllDatas != null && !bloopAllDatas.isEmpty()) {
                    Log.e(TAG, "====获取血压返回" + bloopAllDatas.toString());

                    try {
                        for (int i = 0; i < bloopAllDatas.size(); i++) {
                            B15PBloodDB b15PBloodDB = bloopAllDatas.get(i);


                            /**
                             *  public int year;
                             *     public int day;
                             *     public int month;
                             *     public int hour;
                             *     public int minute;
                             *     public int second;
                             *     public int weekDay;
                             */
                            TimeData timeData = new TimeData();
                            //[,2019-04-24 05:20:00,24]
                            String times = b15PBloodDB.getBloodData();
                            String bloodTime = b15PBloodDB.getBloodTime();
                            if (WatchUtils.isEmpty(times)) return;

                            int yearStr = DateTimeUtils.getCurrYear(times);
                            int monthStr = DateTimeUtils.getCurrMonth(times);
                            int dayStr = DateTimeUtils.getCurrDay(times);

                            int hourStr = Integer.valueOf(bloodTime.substring(0, 2).trim());
                            int mineStr = Integer.valueOf(bloodTime.substring(3, 5).trim());



                            timeData.setYear(Integer.valueOf(times.substring(0, 4).trim()));
                            timeData.setMonth(Integer.valueOf(times.substring(5, 7).trim()));
                            timeData.setDay(Integer.valueOf(times.substring(8, 10).trim()));
                            timeData.setHour(Integer.valueOf(bloodTime.substring(0, 2).trim()));
                            timeData.setMinute(Integer.valueOf(bloodTime.substring(3, 5).trim()));
                            timeData.setSecond(0);

                            CusVPTimeData cusVPTimeData = new CusVPTimeData(yearStr,monthStr,dayStr,hourStr,mineStr,0);

                            CusVPHalfHourBpData cusVPHalfHourBpData = new CusVPHalfHourBpData(cusVPTimeData,b15PBloodDB.getBloodNumberH(),b15PBloodDB.getBloodNumberL());

//                        HalfHourBpData halfHourBpData = new HalfHourBpData(timeData, b15PBloodDB.getBloodNumberH(), b15PBloodDB.getBloodNumberL());
//                        Log.e(TAG, "血压详细" + b15PBloodDB.getBloodData() + " - " + b15PBloodDB.getBloodNumberH() + " - " + b15PBloodDB.getBloodNumberL());
                            if (!dataList.contains(cusVPHalfHourBpData)) dataList.add(cusVPHalfHourBpData);
                        }
                        b30BloadDetailAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x11:
                    closeLoadingDialog();
                    String currentDate = WatchUtils.getCurrentDate();//今天
                    update_View(currentDate, true);
                    break;
                case 0x12:
                    Health_HeartBldPrs.ForceClose_BldPrsMeasure();
                    break;
                case 0x13:
                    L4Command.GetBloodPrs();   //刷新血压指令
                    if (handler!=null)handler.sendEmptyMessageDelayed(0x11,3000);
                    break;
            }
            return false;
        }
    });


    //停止测量
    private void stopMeaureBoload() {
        isStart = false;
        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_start);
        b30MeaureBloadProgressView.stopAnim();

        Health_HeartBldPrs.ForceClose_BldPrsMeasure();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopMeaureBoload();
    }

}
