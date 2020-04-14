package com.example.bozhilun.android.b31;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CustomCircleProgressBar;
import com.example.bozhilun.android.b31.model.ManfatiBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IFatigueDataListener;
import com.veepoo.protocol.model.datas.FatigueData;
import com.veepoo.protocol.model.enums.EDeviceStatus;

import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 疲劳度测试页面
 * Created by Admin
 * Date 2018/12/18
 */
public class B31ManFatigueActivity extends WatchBaseActivity {

    private static final String TAG = "B31ManFatigueActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    @BindView(R.id.b31MeaureFaitProgressView)
    CustomCircleProgressBar b31MeaureFaitProgressView;
    @BindView(R.id.b31FaitImg)
    ImageView b31FaitImg;
    @BindView(R.id.b31FatigueFab)
    ImageView b31FatigueFab;
    //未开始测量时显示的
    @BindView(R.id.b31FaitNoManLin)
    LinearLayout b31FaitNoManLin;
    //测试时的布局
    @BindView(R.id.b31FaitManLin)
    LinearLayout b31FaitManLin;
    //显示测试的进度
    @BindView(R.id.fatiCurrTv)
    TextView fatiCurrTv;
    @BindView(R.id.showFaitResultImg)
    ImageView showFaitResultImg;
    @BindView(R.id.showFaitResultTv)
    TextView showFaitResultTv;
    @BindView(R.id.manFatigueListView)
    ListView manFatigueListView;
    //疲劳度建议
    @BindView(R.id.showFaitSuggestTv)
    TextView showFaitSuggestTv;

    private List<ManfatiBean> manfatiBeanList;
    private TempFatiAdapter tempFatiAdapter;


    //开始或者停止测量的标识
    private boolean isStart = false;

    private AlertDialog.Builder alert;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    handler.removeMessages(1001);
                    DecimalFormat decimalFormat = new DecimalFormat("#");    //不保留小数
                    fatiCurrTv.setText(((int) b31MeaureFaitProgressView.getProgress()) + "%");
                    FatigueData fatigueData = (FatigueData) msg.obj;
                    if (fatigueData == null)
                        return;
                    if(fatigueData.getDeviceState() != EDeviceStatus.FREE){
                        showFaitResultTv.setText(WatchUtils.setBusyDesicStr());
                        isStart = false;
                        stopManFait();
                        return;
                    }

                    showFaitResultData(fatigueData);
                    break;
                case 0x01:
                    handler.removeMessages(0x01);
                    readLocalDBData();
                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_man_fatigue_layout);
        ButterKnife.bind(this);

        initViews();

        initData();


        readLocalDBData();


    }

    private void readLocalDBData() {
        try {
            manfatiBeanList.clear();
            List<ManfatiBean> beanList = LitePal.findAll(ManfatiBean.class);
            if (beanList != null) {
                Collections.sort(beanList, new Comparator<ManfatiBean>() {
                    @Override
                    public int compare(ManfatiBean o1, ManfatiBean o2) {
                        return o2.getTimeStr().compareTo(o1.getTimeStr());
                    }
                });
                manfatiBeanList.addAll(beanList);
                tempFatiAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        manfatiBeanList = new ArrayList<>();
        tempFatiAdapter = new TempFatiAdapter(manfatiBeanList);
        manFatigueListView.setAdapter(tempFatiAdapter);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.fatigue_test));
        //不绘制中间的进度值显示数值
        b31MeaureFaitProgressView.setCanvasV(false);
        showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_bg));

    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg,
            R.id.b31FatigueFab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B31ManFatigueActivity.this);
                break;
            case R.id.b31FatigueFab:    //开始和暂停测试
                if (MyCommandManager.DEVICENAME == null) {
                    showFaitResultTv.setText(getResources().getString(R.string.device)+getResources().getString(R.string.string_not_coon));
                    return;
                }

                startOrEndManFait();

                break;
        }
    }


    //显示测量的结果
    private void showFaitResultData(FatigueData fatigueData) {

        if (fatigueData.getProgress() == 100) {   //测量完成了
            isStart = false;
            int faitValue = fatigueData.getValue();
            //0=测试无效，1=不疲劳，2=轻度疲劳，3=一般疲劳，4=重度疲劳
            String currTime = WatchUtils.getLongToDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());

            stopManFait();
            String showFaitResultStr = "";
            String showSuggestStr = "";
            switch (faitValue) {
                case 0: //无效
                    showFaitResultStr = getResources().getString(R.string.test_invalid);
                    showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_bg));

                    break;
                case 1: //状态良好
                    showFaitResultStr = getResources().getString(R.string.fatigue_normal);
                    showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_level_1));
                    showSuggestStr = getResources().getString(R.string.fatigue_noral_suggest);
                    break;
                case 2: //轻度疲劳
                    showFaitResultStr = getResources().getString(R.string.fatigue_mild);
                    showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_level_2));
                    showSuggestStr = getResources().getString(R.string.fatigue_mild_suggest);
                    break;
                case 3: //中度疲劳
                    showFaitResultStr = getResources().getString(R.string.fatigue_moderate);
                    showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_level_3));
                    showSuggestStr = getResources().getString(R.string.fatigue_moderate_suggest);
                    break;
                case 4: //中度疲劳
                    showFaitResultStr = getResources().getString(R.string.fatigue_severe);
                    showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_level_4));
                    showSuggestStr = getResources().getString(R.string.fatigue_sebere_suggest);
                    break;
            }

            showFaitResultTv.setText(showFaitResultStr);
            showFaitSuggestTv.setText(showSuggestStr);

            if (faitValue != 0) {
                //是否保存数据库中
                alertIsSave(faitValue, currTime);
            }

        }
    }


    //开始或结束测量
    private void startOrEndManFait() {
        if (!isStart) {   //开始测量
            isStart = true;
            b31FatigueFab.setImageResource(R.drawable.detect_ftg_stop);
            b31FaitNoManLin.setVisibility(View.GONE);
            b31FaitManLin.setVisibility(View.VISIBLE);
            startManFait();

        } else {  //停止测量
            isStart = false;
            stopManFait();
            //在测试中点击停止测量时停止测量
            MyApp.getInstance().getVpOperateManager().stopDetectFatigue(iBleWriteResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {

                }
            });

            showFaitResultTv.setText(getResources().getString(R.string.fatigue_no_test_desc));
        }

    }

    //停止测量
    private void stopManFait() {
        b31FatigueFab.setImageResource(R.drawable.detect_ftg_start);
        b31FaitManLin.setVisibility(View.GONE);
        b31FaitNoManLin.setVisibility(View.VISIBLE);
        showFaitResultImg.setBackground(getResources().getDrawable(R.drawable.ftg_bg));
        //停止播放gif动画
        Glide.with(B31ManFatigueActivity.this).clear(b31FaitImg);
    }

    //开始测试了
    private void startManFait() {
        b31MeaureFaitProgressView.setTmpTxt(null);
        b31MeaureFaitProgressView.setScheduleDuring(60 * 1000);
        b31MeaureFaitProgressView.setProgress(100);
        //开始动画效果
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(B31ManFatigueActivity.this).asGif().load(R.drawable.ftggif).apply(options).into(b31FaitImg);
        MyApp.getInstance().getVpOperateManager().startDetectFatigue(iBleWriteResponse, new IFatigueDataListener() {
            @Override
            public void onFatigueDataListener(FatigueData fatigueData) {
                Log.e(TAG, "---------fatigueData=" + fatigueData.toString());
                Message message = handler.obtainMessage();
                message.obj = fatigueData;
                message.what = 1001;
                handler.sendMessage(message);
            }
        });

    }


    private void alertIsSave(final int status, final String currTime) {
        alert = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.measurement_record) + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveToLoclDb(status, currTime);
                        handler.sendEmptyMessage(0x01);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alert.create().show();
    }

    private void saveToLoclDb(int status, String currTime) {
        ManfatiBean manfatiBean = new ManfatiBean();
        manfatiBean.setTimeStr(currTime);
        manfatiBean.setStatus(status);
        manfatiBean.save();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStart) {
            //在测试中点击停止测量时停止测量
            MyApp.getInstance().getVpOperateManager().stopDetectFatigue(iBleWriteResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {

                }
            });
        }
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    class TempFatiAdapter extends BaseAdapter {

        private List<ManfatiBean> list;
        private LayoutInflater layoutInflater;

        public TempFatiAdapter(List<ManfatiBean> list) {
            this.list = list;
            layoutInflater = LayoutInflater.from(B31ManFatigueActivity.this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_b31_fait_childe_layout, parent, false);
                holder = new ViewHolder();
                holder.dateTv = convertView.findViewById(R.id.childTimeTv);
                holder.statusImg = convertView.findViewById(R.id.childStatusImg);
                holder.statusTv = convertView.findViewById(R.id.childStatusTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.dateTv.setText(list.get(position).getTimeStr());
            int status = list.get(position).getStatus();
            String statusStr = null;
            int imgId = 0x00;
            switch (status) {
                case 0: //无效
                    statusStr = getResources().getString(R.string.test_invalid);
                    imgId = R.drawable.ftg_level_tea_1;
                    break;
                case 1:
                    statusStr = getResources().getString(R.string.fatigue_normal);
                    imgId = R.drawable.ftg_level_tea_1;
                    break;
                case 2:
                    statusStr = getResources().getString(R.string.fatigue_mild);
                    imgId = R.drawable.ftg_level_tea_2;
                    break;
                case 3:
                    statusStr = getResources().getString(R.string.fatigue_moderate);
                    imgId = R.drawable.ftg_level_tea_3;
                    break;
                case 4:
                    statusStr = getResources().getString(R.string.fatigue_severe);
                    imgId = R.drawable.ftg_level_tea_4;
                    break;
            }
            holder.statusTv.setText(statusStr);
            holder.statusImg.setImageDrawable(getResources().getDrawable(imgId));

            return convertView;
        }

        class ViewHolder {
            TextView dateTv, statusTv;
            ImageView statusImg;
        }
    }
}
