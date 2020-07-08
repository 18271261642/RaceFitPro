package com.example.bozhilun.android.xwatch.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CusStepDetailView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.view.RiseNumberTextView;
import com.example.bozhilun.android.view.CusScheduleView;
import com.example.bozhilun.android.w30s.ble.W37Constance;
import com.example.bozhilun.android.xwatch.UploadXWatchTask;
import com.example.bozhilun.android.xwatch.XWatchDeviceActivity;
import com.example.bozhilun.android.xwatch.XWatchIntelActivity;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchBleOperate;
import com.example.bozhilun.android.xwatch.ble.XWatchStepBean;
import com.example.bozhilun.android.xwatch.ble.XWatchSyncSuccListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/2/17
 */
public class XWatchHomeFragment extends LazyFragment {
    private static final String TAG = "XWatchHomeFragment";

    Unbinder unbinder;
    @BindView(R.id.h8Img11)
    ImageView ivTop;
    @BindView(R.id.new_h8_recordtop_dateTv)
    TextView b30TopDateTv;

    @BindView(R.id.new_h8_recordShareImg)
    ImageView new_h8_recordShareImg;

    //连接状态
    @BindView(R.id.new_h8_recordwatch_connectStateTv)
    TextView connStatusTv;

    @BindView(R.id.xWatchHomeSportTimeTv)
    TextView xWatchHomeSportTimeTv;

    @BindView(R.id.xWatchSportNumberTv)
    RiseNumberTextView xWatchSportNumberTv;
    @BindView(R.id.homeSportValueTv)
    TextView homeSportValueTv;
    @BindView(R.id.homeKcalValueTv)
    TextView homeKcalValueTv;
    @BindView(R.id.xWatchCusStepDView)
    CusStepDetailView cusStepDView;
    @BindView(R.id.xWatchSportMaxNumTv)
    TextView b30SportMaxNumTv;
    @BindView(R.id.xWatchHomeTodayImg)
    ImageView xWatchHomeTodayImg;
    @BindView(R.id.xWatchHomeYestdayImg)
    ImageView xWatchHomeYestdayImg;
    @BindView(R.id.xWatchHomeBeYestdayImg)
    ImageView xWatchHomeBeYestdayImg;
    private View homeView;

    @BindView(R.id.xWatchGoalTv)
    TextView xWatchGoalTv;

    @BindView(R.id.homeSportSchudeView)
    CusScheduleView homeSportSchudeView;

    @BindView(R.id.homeKcalSchudeView)
    CusScheduleView homeKcalSchudeView;

    @BindView(R.id.homeAlarmSchudeView)
    CusScheduleView homeAlarmSchudeView;

    @BindView(R.id.xWatchRefreshLayout)
    SmartRefreshLayout xWatchRefreshLayout;


    private XWatchBleAnalysis xWatchBleAnalysis;

    private Context mContext;
    private UploadXWatchTask xWatchTask = null;


    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {

                Map<String, Object> stepMap = (Map<String, Object>) msg.obj;
                int stepNumber = (int) stepMap.get("step");
                double disance = (double) stepMap.get("disance");
                double kcal = (double) stepMap.get("kcal");
                int sportTime = (int) stepMap.get("sport_time");
                if (xWatchSportNumberTv.isRunning())
                    xWatchSportNumberTv.setInteger(1, stepNumber);
                xWatchSportNumberTv.setInteger(1, stepNumber);
                xWatchSportNumberTv.setDuration(1500);
                xWatchSportNumberTv.start();

                homeSportValueTv.setText(disance / 100 + "");
                homeKcalValueTv.setText(kcal + "");
                xWatchHomeSportTimeTv.setText(sportTime + "");

            }
            if(msg.what ==0x02){
                if (xWatchRefreshLayout != null)
                    xWatchRefreshLayout.finishRefresh();
                updatePageData();

                try {
                    if (xWatchTask != null && xWatchTask.getStatus() == AsyncTask.Status.RUNNING) {
                        xWatchTask.cancel(true);
                        xWatchTask = new UploadXWatchTask();
                    } else {
                        xWatchTask = new UploadXWatchTask();
                    }
                    xWatchTask.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(msg.what == 0x03){
                if(xWatchRefreshLayout != null){
                    xWatchRefreshLayout.setEnableRefresh(true);
                    xWatchRefreshLayout.autoRefresh();
                }
            }

            if(msg.what == 0x04){
                if (xWatchRefreshLayout != null)
                    xWatchRefreshLayout.finishRefresh();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37Constance.X_WATCH_CONNECTED_ACTION);
        intentFilter.addAction(W37Constance.X_WATCH_DISCONN_ACTION);
        getmContext().registerReceiver(broadcastReceiver, intentFilter);
        xWatchBleAnalysis = XWatchBleAnalysis.getW37DataAnalysis();

        String saveDate = (String) SharedPreferencesUtils.getParam(getmContext(), "saveDate", "");
        if (WatchUtils.isEmpty(saveDate)) {
            SharedPreferencesUtils.setParam(getmContext(), "saveDate", System.currentTimeMillis() / 1000 + "");
        }

        //保存的时间 用于防止切换过快
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "save_curr_time", "");
        if (WatchUtils.isEmpty(tmpSaveTime))
            SharedPreferencesUtils.setParam(getmContext(), "save_curr_time", System.currentTimeMillis() / 1000 + "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_x_watch_home_layout, container, false);
        unbinder = ButterKnife.bind(this, homeView);

        initViews();


        return homeView;
    }

    private void initViews() {
        b30TopDateTv.setTextColor(Color.parseColor("#FF4186D9"));
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
        connStatusTv.setTextColor(Color.parseColor("#FF4186D9"));
        ivTop.setImageResource(verticalDevice() == 0?R.mipmap.icon_xwatch_home_top:R.mipmap.icon_s_watch_top);
        // new_h8_recordShareImg.setImageResource(R.mipmap.icon_x_watch_home_chart);


        if(MyCommandManager.DEVICENAME == null)
            xWatchRefreshLayout.setEnableRefresh(false);

        xWatchRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (MyCommandManager.DEVICENAME == null)
                    return;
                readSyncDevice(0);
                handler.sendEmptyMessageDelayed(0x04,8 * 1000);
            }
        });
    }

    private int verticalDevice(){
        String saveBleName = (String) SharedPreferencesUtils.readObject(getmContext(),Commont.BLENAME);
        if(saveBleName == null)
            return 0;
        return saveBleName.equals("SWatch")?1:0;
    }


    @Override
    public void onResume() {
        super.onResume();

        int sportGoal = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.SPORT_GOAL_STEP,10000);
        xWatchGoalTv.setText(getResources().getString(R.string.goal_step) + " "+sportGoal);

    }



    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            connStatusTv.setText(MyCommandManager.DEVICENAME != null ? getResources().getString(R.string.connted) : getResources().getString(R.string.disconnted));

            int curCode = (int) SharedPreferencesUtils.getParam(getmContext(), "curr_code", 0);
            clearDataStyle(curCode);//设置每次回主界面，返回数据不清空的
            long currentTime = System.currentTimeMillis() / 1000;
            //保存的时间
            String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "saveDate", currentTime + "");
            long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;

            if(MyCommandManager.DEVICENAME == null)
                return;
            if(WatchConstants.isScanConn){
                WatchConstants.isScanConn = false;
                handler.sendEmptyMessage(0x03);
            }
            if(diffTime >5){
                readSyncDevice(curCode);
            }

        }
    }


    //连接成功后同步数据
    private void readSyncDevice(int code){
        SharedPreferencesUtils.setParam(getmContext(), "saveDate", System.currentTimeMillis() / 1000 + "");
        XWatchBleOperate.getxWatchBleOperate().bleConnOperate(code, getmContext(), new XWatchSyncSuccListener() {
            @Override
            public void bleSyncComplete(byte[] data) {
                Log.e(TAG,"-------更新="+data[0]);
                handler.sendEmptyMessageDelayed(0x02,2 * 1000);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        try {
            if (broadcastReceiver != null)
                getmContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.new_h8_recordShareImg, R.id.cusStepViewLin,
            R.id.xWatchHomeTodayLin, R.id.xWatchHomeYesDayLin,
            R.id.xWatchHomeThirdDayLin,R.id.newH8RecordTopRel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_h8_recordShareImg:
                startActivity(new Intent(getContext(), XWatchIntelActivity.class));
                break;
            case R.id.cusStepViewLin:
                startActivity(new Intent(getContext(), XWatchSportDetailActivity.class));
                break;
            case R.id.newH8RecordTopRel:
                if(MyCommandManager.DEVICENAME == null){
                    MyApp.getInstance().getW37BleOperateManager().stopScan();
                    startActivity(new Intent(getmContext(), NewSearchActivity.class));
                    getActivity().finish();
                }else{
                    startActivity(new Intent(getmContext(), XWatchDeviceActivity.class));
                }
                break;
            case R.id.xWatchHomeTodayLin:   //今天
                clearDataStyle(0);
                break;
            case R.id.xWatchHomeYesDayLin:  //昨天
                clearDataStyle(1);
                break;
            case R.id.xWatchHomeThirdDayLin:    //前天
                clearDataStyle(2);
                break;
        }

    }


    //三天数据切换
    private void clearDataStyle(final int code) {
        long currentTime = System.currentTimeMillis() / 1000;   //当前时间
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "save_curr_time", "");
        long diffTime = (currentTime - Long.valueOf(tmpSaveTime));
        if (diffTime < 2)
            return;
        SharedPreferencesUtils.setParam(getmContext(), "save_curr_time", System.currentTimeMillis() / 1000 + "");
        //if (code == currDay) return;// 防重复点击
        if (xWatchHomeTodayImg != null) xWatchHomeTodayImg.setVisibility(View.INVISIBLE);
        if (xWatchHomeYestdayImg != null) xWatchHomeYestdayImg.setVisibility(View.INVISIBLE);
        if (xWatchHomeBeYestdayImg != null) xWatchHomeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code) {
            case 0: //今天
                if (xWatchHomeTodayImg != null) xWatchHomeTodayImg.setVisibility(View.VISIBLE);
                break;
            case 1: //昨天
                if (xWatchHomeYestdayImg != null) xWatchHomeYestdayImg.setVisibility(View.VISIBLE);
                break;
            case 2: //前天
                if (xWatchHomeBeYestdayImg != null) xWatchHomeBeYestdayImg.setVisibility(View.VISIBLE);
                break;
        }
        currDay = code;
        SharedPreferencesUtils.setParam(getmContext(), "curr_code", code);
        updatePageData();


    }

    //更新页面数据
    private void updatePageData() {
        String mac = WatchUtils.getSherpBleMac(getmContext());
        if (WatchUtils.isEmpty(mac))
            return;
        String date = WatchUtils.obtainFormatDate(currDay);

        updateCountStep(date,mac);

        updateDeviceDetailSport(date,mac);

    }

    //汇总的步数
    private void updateCountStep(String date, String mac) {
        try {
            String dayCountStepStr = B30HalfHourDao.getInstance().findOriginData(mac,date,B30HalfHourDao.XWATCH_DAY_STEP);
            if(dayCountStepStr == null){
                if (xWatchSportNumberTv.isRunning())
                    xWatchSportNumberTv.setInteger(1, 0);
                xWatchSportNumberTv.setInteger(1, 0);
                xWatchSportNumberTv.setDuration(1500);
                xWatchSportNumberTv.start();

                homeSportValueTv.setText("0.0");
                homeKcalValueTv.setText("0.0");
                xWatchHomeSportTimeTv.setText("0");
                return;
            }

            XWatchStepBean xWatchStepBean = new Gson().fromJson(dayCountStepStr,XWatchStepBean.class);

            if (xWatchSportNumberTv.isRunning())
                xWatchSportNumberTv.setInteger(1, xWatchStepBean.getStepNumber());
            xWatchSportNumberTv.setInteger(1, xWatchStepBean.getStepNumber());
            xWatchSportNumberTv.setDuration(1500);
            xWatchSportNumberTv.start();

            double xDisance = xWatchStepBean.getDisance();

            homeSportValueTv.setText(WatchUtils.div(xDisance, (double) 100,1)+"");
            homeKcalValueTv.setText(xWatchStepBean.getKcal()+"");
            xWatchHomeSportTimeTv.setText(xWatchStepBean.getPosrtTime()+"");


            setSportSchedule(xWatchStepBean.getStepNumber());


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void setSportSchedule(int currStep){
        int sportGoal = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.SPORT_GOAL_STEP,10000);
        homeSportSchudeView.setAllScheduleValue(sportGoal);
        homeKcalSchudeView.setAllScheduleValue(sportGoal);
        homeAlarmSchudeView.setAllScheduleValue(sportGoal);
        homeSportSchudeView.setCurrScheduleValue(currStep);
        homeKcalSchudeView.setCurrScheduleValue(currStep);
        homeAlarmSchudeView.setCurrScheduleValue(currStep);

        homeSportSchudeView.invalidate();
        homeKcalSchudeView.invalidate();
        homeAlarmSchudeView.invalidate();

    }



    //详细步数信息
    private void updateDeviceDetailSport(String dayStr, String bleMac) {
        try {
            String originDataStr = B30HalfHourDao.getInstance().findOriginData(bleMac, dayStr, B30HalfHourDao.XWATCH_DETAIL_SPORT);
            if (originDataStr == null){
                b30SportMaxNumTv.setText("0");
                cusStepDView.setSourList(new ArrayList<Integer>());
                return;
            }

            List<Integer> orginList = new Gson().fromJson(originDataStr, new TypeToken<List<Integer>>() {
            }.getType());

            String saveBleName = (String) SharedPreferencesUtils.readObject(getmContext(),Commont.BLENAME);
            if(saveBleName == null)
                return;
            List<Integer> halfList = new ArrayList<>();
            if(saveBleName.equals("XWatch")){
                for (int i = 0; i < orginList.size(); i += 2) {
                    if (i + 1 <= orginList.size() - 1) {
                        int halfHourStepCount = orginList.get(i) + orginList.get(i + 1);
                        halfList.add(halfHourStepCount);
                    }
                }
            }else{
                halfList.addAll(orginList);
            }

            b30SportMaxNumTv.setText(Collections.max(halfList) + "");
            cusStepDView.setSourList(halfList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(W37Constance.X_WATCH_CONNECTED_ACTION)) {   //连接成功
                MyCommandManager.DEVICENAME = intent.getStringExtra("bleName");
                connStatusTv.setText(getResources().getString(R.string.connted));
                handler.sendEmptyMessage(0x03);

            }
            if (action.equals(W37Constance.X_WATCH_DISCONN_ACTION)) {    //连接失败
                MyCommandManager.DEVICENAME = null;
                xWatchRefreshLayout.setEnableRefresh(false);
                connStatusTv.setText(getResources().getString(R.string.disconnted));
            }
        }
    };


}
