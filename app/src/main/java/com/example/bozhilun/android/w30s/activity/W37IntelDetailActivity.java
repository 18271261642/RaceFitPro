package com.example.bozhilun.android.w30s.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.AllBackDataListener;
import com.example.bozhilun.android.w30s.ble.CallDatasBackListenter;
import com.example.bozhilun.android.w30s.ble.W37BloodBean;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.W30SBLEGattAttributes;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * W30,W31,W37内部测试页面
 * Created by Admin
 * Date 2019/7/1
 */
public class W37IntelDetailActivity extends WatchBaseActivity {

    private static final String TAG = "W37IntelDetailActivity";


    @BindView(R.id.showW37ResultTv)
    TextView showW37ResultTv;


    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.inputDataEdit)
    EditText inputEdit;

    private String bleMac = null;
    private Gson gson = new Gson();


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showW37ResultTv.setText("");
            switch (msg.what){
                case 0x01:  //设备中的所有数据
                    showW37ResultTv.setText(stringBuilder.toString());
                    break;
                case 1001:  //运动数据
                    String sportDetail = (String) msg.obj;
                    showW37ResultTv.setText(sportDetail);
                    break;
                case 1002:  //睡眠数据
                    String sleepDetail = (String) msg.obj;
                    showW37ResultTv.setText(sleepDetail);
                    break;
                case 1003:  //心率数据
                    String heartDetail = (String) msg.obj;
                    showW37ResultTv.setText(heartDetail);
                    break;

            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intel_w37_layout);
        ButterKnife.bind(this);

        initViews();

        bleMac = (String) SharedPreferencesUtils.readObject(W37IntelDetailActivity.this,Commont.BLEMAC);

        String path = Environment.getDownloadCacheDirectory().getPath();
        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+getPackageName();
        Log.e(TAG,"----path="+path+"---path2="+path2);



    }

    private void initViews() {
        commentB30TitleTv.setText("内部测试使用");
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.w37IntelHeartBtn, R.id.w37IntelClearBtn,
            R.id.commentB30BackImg,R.id.w37DbSportBtn,
            R.id.w37DbHeartBtn,R.id.w37DbSleepBtn,
            R.id.w37AllBtn,R.id.sendInputBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.w37IntelHeartBtn:
                if(WatchUtils.isEmpty(bleMac))
                    return;
                showW37ResultTv.setText("查询中...");
                //MyApp.getInstance().getmW30SBLEManage().syncTime(callDatasBackListenter);
                W37DataAnalysis.getW37DataAnalysis().analysW37BackData(W30SBLEGattAttributes.syncTime(),callDatasBackListenter);
                break;
            case R.id.w37IntelClearBtn:
                stringBuilder.reverse();
                showW37ResultTv.setText("");
                break;
            case R.id.w37DbSportBtn:    //数据库步数数据
                findW37SportDb();
                break;
            case R.id.w37DbHeartBtn:    //数据库心率数据
                findW37HeartDb();
                break;
            case R.id.w37DbSleepBtn:    //数据库睡眠数据
                findW37SleepDb();
                break;
            case R.id.w37AllBtn:    //所有的原始数据
                W37DataAnalysis.getW37DataAnalysis().analysW37BackData(W30SBLEGattAttributes.syncTime(),allBackDataListener);
                break;
            case R.id.sendInputBtn: //输入指令
//                String inputDa = inputEdit.getText().toString().trim();
//                if(WatchUtils.isEmpty(inputDa))
//                    return;
                //W37DataAnalysis.getW37DataAnalysis().sendAppalertData("this is Line 消息", W30SBLEManage.NotifaceMsgLine);

                W37DataAnalysis.getW37DataAnalysis().sendAppalertData("110", 0x01);

                break;
        }
    }


    private AllBackDataListener allBackDataListener = new AllBackDataListener() {
        @Override
        public void allDataBack(String str) {
            showW37ResultTv.setText(str);
        }
    };




    //查询睡眠数据
    private void findW37SleepDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> tmpMap = new HashMap<>();
                List<B30HalfHourDB> w30SleepList = B30HalfHourDao.getInstance().findW30SleepDetail(WatchUtils.getCurrentDate(), bleMac, B30HalfHourDao.TYPE_SLEEP);
                if (w30SleepList == null) {
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = "睡眠数据为空";
                    handler.sendMessage(message);
                    return;
                }
                //解析所需数据
                String sleepStr = w30SleepList.get(0).getOriginData();
                //Map<String, String> allMap = gson.fromJson(sleepStr, Map.class);
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.obj = sleepStr;
                handler.sendMessage(message);

            }
        }).start();

    }



    //查询心率数据
    private void findW37HeartDb(){
        if(bleMac == null){
            showW37ResultTv.setText("Mac地址为空");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<B30HalfHourDB> w30HeartList = B30HalfHourDao.getInstance().findW30HeartDetail(WatchUtils.getCurrentDate(),
                        bleMac, B30HalfHourDao.TYPE_RATE);
                if (w30HeartList == null) {
                    Message message = handler.obtainMessage();
                    message.what = 1003;
                    message.obj = "心率数据为空";
                    handler.sendMessage(message);
                } else {
                    String heartStr = w30HeartList.get(w30HeartList.size()-1).getOriginData();
                    Map<String, String> htMap = gson.fromJson(heartStr, Map.class);
                    //Log.e(TAG, "----------htMap=" + htMap.toString());

                    String htStr = htMap.get(WatchUtils.getCurrentDate());

//                    List<W30HeartBean> heartBeanList = gson.fromJson(htStr, new TypeToken<List<W30HeartBean>>() {
//                    }.getType());
//
//                    for (W30HeartBean w30HeartBean : heartBeanList) {
//                        // Log.e(TAG,"-------w30HeartBean="+w30HeartBean.toString());
//                        resultHeartList.add(w30HeartBean.getHeartValues());
//                    }

                    Message message = handler.obtainMessage();
                    message.what = 1003;
                    message.obj = htStr;
                    handler.sendMessage(message);


                }
            }
        }).start();



    }





    //查询数据库中步数的数据
    private void findW37SportDb(){
        if(bleMac == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<B30HalfHourDB> w30List = B30HalfHourDao.getInstance().findW30SportData(WatchUtils.getCurrentDate(), bleMac, B30HalfHourDao.TYPE_SPORT);
                if (w30List == null) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = "数据为空";
                    handler.sendMessage(message);
                    return;
                }

                //解析所需数据
                String stepStr = w30List.get(0).getOriginData();
                Map<String, String> allMap = gson.fromJson(stepStr, Map.class);
                String stepDetail = allMap.get("stepDetail");

                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = stepDetail;
                handler.sendMessage(message);
            }
        }).start();
    }




    /**
     * 查询手环所有的数据
     */
    StringBuilder stringBuilder = new StringBuilder();


    private CallDatasBackListenter callDatasBackListenter = new CallDatasBackListenter() {
        @Override
        public void callDatasBackSportListenter(W30SSportData sportData) {
            Log.e(TAG, "-------sportData=" + sportData.toString());
            stringBuilder.append("运动原始数据=" + sportData.toString());
            stringBuilder.append("\n");
        }

        @Override
        public void callDatasBackSleepListenter(W30SSleepData sleepData) {
            Log.e(TAG, "-------sleepData=" + sleepData.toString());
            stringBuilder.append("睡眠原始数据=" + sleepData.toString());
            stringBuilder.append("\n");
        }

        @Override
        public void callDatasBackDeviceDataListenter(W30SDeviceData deviceData) {
            Log.e(TAG, "------deviceData=" + deviceData.toString());
            stringBuilder.append("设备原始数据=" + deviceData.toString());
            stringBuilder.append("\n");
        }

        @Override
        public void callDatasBackHeartListenter(W30SHeartData heartData) {
            Log.e(TAG, "------heartData=" + heartData.toString());
            stringBuilder.append("心率原始数据=" + heartData.toString());
            stringBuilder.append("\n");
        }

        @Override
        public void callDatasBackBloodListener(W37BloodBean w37BloodBean) {
            stringBuilder.append("血压原始数据="+w37BloodBean.toString());
            stringBuilder.append("\n");
        }

        @Override
        public void callDatasBackListenterIsok() {
            handler.sendEmptyMessage(0x01);
        }
    };

}
