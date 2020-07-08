package com.example.bozhilun.android.b18.fragment;

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
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B16BmiManagerActivity;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.b18.B18DeviceActivity;
import com.example.bozhilun.android.b18.B18HomeActivity;
import com.example.bozhilun.android.b18.B18InternalActivity;
import com.example.bozhilun.android.b18.B18SleepDetailActivity;
import com.example.bozhilun.android.b18.B18StepDetailActivity;
import com.example.bozhilun.android.b18.modle.B16CadenceBean;
import com.example.bozhilun.android.b18.modle.B16CadenceResultBean;
import com.example.bozhilun.android.b18.modle.B16DbManager;
import com.example.bozhilun.android.b18.modle.B18CountSleepBean;
import com.example.bozhilun.android.b18.modle.B18SleepChartBean;
import com.example.bozhilun.android.b18.modle.UpdateB16DataTask;
import com.example.bozhilun.android.b30.b30view.CusStepDetailView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.women.WomenDetailActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.friend.views.CusFriendBean;
import com.example.bozhilun.android.friend.views.CusFriendSleepView;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.view.BatteryView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hplus.bluetooth.BleProfileManager;
import com.hplus.bluetooth.command.OnResponseListener;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * B18首页面
 * Created by Admin
 * Date 2019/11/9
 */
public class B18HomeFragment extends LazyFragment {

    private static final String TAG = "B18HomeFragment";

    View view;
    @BindView(R.id.b18ProgressBar)
    WaveProgress b18ProgressBar;
    Unbinder unbinder;

    @BindView(R.id.homeBatteryStatusTv)
    TextView homeBatteryStatusTv;

    @BindView(R.id.homeTypeImg)
    ImageView homeTypeImg;

    @BindView(R.id.homeBatteryView)
    BatteryView homeBatteryView;

    @BindView(R.id.batteryDateTv)
    TextView batteryDateTv;

    @BindView(R.id.homeFastStatusTv)
    TextView homeFastStatusTv;

    @BindView(R.id.b18GoalStepTv)
    TextView b18GoalStepTv;

    //步数图表
    @BindView(R.id.cusStepDView)
    CusStepDetailView cusStepDView;
    //最大步数
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;

    @BindView(R.id.b16BmiStatusTv)
    TextView b16BmiStatusTv;

    //步数数据源
    List<Integer> resultStepList = new ArrayList<>();

    @BindView(R.id.cusFriendSleepView)
    CusFriendSleepView cusFriendSleepView;
    //睡眠起始时间
    @BindView(R.id.w30StartEndTimeTv)
    TextView w30StartEndTimeTv;

    @BindView(R.id.b18HomeSwipeRefreshLayout)
    SmartRefreshLayout b18HomeSwipeRefreshLayout;

    //运动目标
    int sportGoal = 0;
    @BindView(R.id.status1Tv)
    TextView status1Tv;
    @BindView(R.id.status2Tv)
    TextView status2Tv;
    @BindView(R.id.status3Tv)
    TextView status3Tv;
    @BindView(R.id.homeB36StatusLin)
    ImageView homeB36StatusLin;

    //B36女性功能隐私图片
    @BindView(R.id.b36WomenPrivacyImg)
    ImageView b36WomenPrivacyImg;

    //BMI图表
    @BindView(R.id.b16BmiLinChart)
    LineChart b16BmiLinChart;

    private Context mContext;

    @BindView(R.id.b36WomenLin)
    LinearLayout b36WomenLin;

    private UpdateB16DataTask updateB16DataTask = null;


    private String currDay = WatchUtils.getCurrentDate();


    //BMI图表数据
    private ArrayList<Entry> b16EntryList ;
    private LineDataSet lineDataSet;
    //步频最大值
    private float maxStep = 0;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    try {
                        B18HomeActivity activity = (B18HomeActivity) getActivity();
                        Log.e(TAG,"------------msg=====");
                        if(activity != null)
                            activity.upUserDevice();
                        JSONObject dataJson = (JSONObject) msg.obj;
                        int realStep = dataJson.getInt("realStep");
                        int batteryValue = dataJson.getInt("batteryValue");
                        if (getActivity() != null && !getActivity().isFinishing()) {

                            b18ProgressBar.setMaxValue(sportGoal);
                            b18ProgressBar.setValue(realStep);

                            showBatterStute(batteryValue);


                            int distance = dataJson.getInt("realDistance");
                            int calorie = dataJson.getInt("realCalorie");


                            SharedPreferencesUtils.setParam(getmContext(), "b16_curr_sport", realStep + "-" + distance + "|" + calorie);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x02:
                    if (b18HomeSwipeRefreshLayout != null) {
                        b18HomeSwipeRefreshLayout.finishRefresh();
                    }

                    try {
                        if (updateB16DataTask != null && updateB16DataTask.getStatus() == AsyncTask.Status.RUNNING) {
                            updateB16DataTask.cancel(true);
                            updateB16DataTask = null;
                            updateB16DataTask = new UpdateB16DataTask();
                        } else {
                            updateB16DataTask = new UpdateB16DataTask();
                        }
                        updateB16DataTask.execute();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case 0x03:
                    MyCommandManager.DEVICENAME = "B16";


                    break;
            }

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B18_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B18_DISCONN_ACTION);
        intentFilter.addAction(WatchUtils.B18_UPDATE_HOME_DATA);
        getmContext().registerReceiver(broadcastReceiver, intentFilter);
        BleProfileManager.getInstance().getCommandController().addResponseListener(onResponseListener);
        //运动目标
        sportGoal = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.SPORT_GOAL_STEP, 0);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b18_home__ayout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initWomen() {
        womenPrivacy = (boolean) SharedPreferencesUtils.getParam(getmContext(), "b36_women_privacy", false);
        b36WomenPrivacyImg.setBackground(getResources().getDrawable(womenPrivacy ? R.mipmap.ic_b36_home_woman_rec_pri : R.mipmap.ic_b36_home_woman_rec));
        tunrOnOrOffPrivacy(womenPrivacy);
        showB36WomenStatus();
    }

    private void initViews() {
        batteryDateTv.setText(WatchUtils.getCurrentDate());
        //homeTypeImg.setImageResource(R.mipmap.icon_b16_top);

        b18ProgressBar.setMaxValue(sportGoal);
        b18ProgressBar.setValue(0f);

        if (!B18BleConnManager.isAllowSyncData)
            b18HomeSwipeRefreshLayout.setEnableRefresh(false);

        b18HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                syncDeviceData();
            }
        });

        batteryDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getmContext(), B18InternalActivity.class));
                return true;
            }
        });


        String sex = (String) SharedPreferencesUtils.getParam(getmContext(), Commont.USER_SEX, "M");
        if (WatchUtils.isEmpty(sex))
            sex = "M";
        b36WomenLin.setVisibility(sex.equals("M") ? View.GONE : View.VISIBLE);

        b16EntryList = new ArrayList<>();

    }

    private void syncDeviceData() {
        //判断是否可以更新
        if (!B18BleConnManager.isAllowSyncData)
            return;
        b18HomeSwipeRefreshLayout.setEnableRefresh(true);
        B18BleConnManager.getB18BleConnManager().syncB18DeviceData();

    }


    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        sportGoal = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.SPORT_GOAL_STEP, 0);
        b18GoalStepTv.setText(getResources().getString(R.string.goal_step) + " : " + sportGoal);

        initWomen();
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (!isVisible)
            return;
        Log.e(TAG, "-----onFragmentVisibleChange----");
        homeBatteryStatusTv.setText(BleProfileManager.getInstance().isConnected() ?
                getResources().getText(R.string.connted) : getResources().getText(R.string.disconnted));

        homeFastStatusTv.setTextColor(BleProfileManager.getInstance().isConnected() ? ContextCompat.getColor(getmContext(), R.color.white) :
                ContextCompat.getColor(getmContext(), R.color.red));
        homeFastStatusTv.setText(BleProfileManager.getInstance().isConnected() ?
                getResources().getText(R.string.more_opera) : getResources().getString(R.string.disconnted));
        updateAllHealthData();


    }

    private void updateAllHealthData() {
        String bleMac = MyApp.getInstance().getMacAddress();
        if (bleMac == null)
            return;
        //步数详细数据
        updateSportData(bleMac);
        //睡眠详细数据
        updateSleepDetail(bleMac);

        updateB16BmiData(bleMac);
    }

    private void updateB16BmiData(String bleMac) {
        b16EntryList.clear();
        try {
            List<B16CadenceResultBean> resultBeanList = B16DbManager.getB16DbManager().findB16CadenceHistory(bleMac, currDay);
            if (resultBeanList == null)
                return;
            float sportSecond = Float.valueOf(resultBeanList.get(resultBeanList.size()-1).getSportDuration()) / 60f; //分钟
            String sportStr = resultBeanList.get(resultBeanList.size()-1).getSportDetailStr();
            List<B16CadenceBean.DetailDataBean> detailDataBeanList = new Gson().fromJson(sportStr, new TypeToken<List<B16CadenceBean.DetailDataBean>>() {
            }.getType());
            for (int i = 0; i < detailDataBeanList.size(); i++) {
                //步频
                float stepGa = detailDataBeanList.get(i).getStep() / sportSecond;
                if (maxStep <= stepGa)
                    maxStep = stepGa;
                b16EntryList.add(new Entry(i, stepGa));
            }
            initLinChartData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //更新睡眠图表
    private void updateSleepDetail(String bleMac) {
        try {
            String sleepStr = B30HalfHourDao.getInstance().findOriginData(bleMac, currDay, B30HalfHourDao.B16_DETAIL_SLEEP);
            //Log.e(TAG,"--------sleepStr="+sleepStr);
            if (sleepStr == null) {
                w30StartEndTimeTv.setText("--");
                return;
            }
            String countSleepStr = B30HalfHourDao.getInstance().findOriginData(bleMac, currDay, B30HalfHourDao.B18_COUNT_SLEEP);
            if (countSleepStr == null)
                return;
            B18CountSleepBean b18CountSleepBean = new Gson().fromJson(countSleepStr, B18CountSleepBean.class);
            B18SleepChartBean b18SleepChartBean = new Gson().fromJson(sleepStr, B18SleepChartBean.class);
            showB18SleepView(b18CountSleepBean, b18SleepChartBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    private void showB18SleepView(B18CountSleepBean b18CountSleepBean, B18SleepChartBean b18SleepChartBean) {
        try {
            int sleepHour = b18CountSleepBean.getStartSleepHour();
            int sleepMine = b18CountSleepBean.getStartSleepMinute();
            //睡眠时长
            int countTime = b18CountSleepBean.getDeepSleepTime() + b18CountSleepBean.getShallowSleepTime() + b18CountSleepBean.getAwakeTime();
            //清醒时间
            int sleepDownMine = b18CountSleepBean.getStartSleepHour() * 60 + b18CountSleepBean.getStartSleepMinute();
            //入睡时间+总的睡眠时长，判断是否大于24小时，大于24小时就跨天，否则反之
            int tmpResultMine = countTime + sleepDownMine;

            int sleepUpMine = 1440 >= tmpResultMine ? tmpResultMine : 1440 - tmpResultMine;

            int tmpSleepUpHour = sleepUpMine / 60;

            tmpSleepUpHour = Math.abs(tmpSleepUpHour);

            int tmpSleepUpMine = sleepUpMine % 60;
            tmpSleepUpMine = Math.abs(tmpSleepUpMine);

            String sleepUpTime = (tmpSleepUpHour <= 9 ? "0" + tmpSleepUpHour : tmpSleepUpHour) + ":" + (tmpSleepUpMine <= 9 ? 0 + "" + tmpSleepUpMine : tmpSleepUpMine);
            w30StartEndTimeTv.setText((sleepHour <= 9 ? "0" + sleepHour : sleepHour) + ":" + (sleepMine <= 9 ? "0" + sleepMine : sleepMine) + "-" + sleepUpTime);


            cusFriendSleepView.setAllSleepTime(b18CountSleepBean.getDeepSleepTime() + b18CountSleepBean.getShallowSleepTime() + b18CountSleepBean.getAwakeTime());
            List<CusFriendBean> cusFriendBeanList = new ArrayList<>();
            for (B18SleepChartBean.DetailDataBean dataBean : b18SleepChartBean.getDetailData()) {
                cusFriendBeanList.add(new CusFriendBean(dataBean.getSleepType(), dataBean.getSleepTime()));
            }
            cusFriendBeanList.add(0, new CusFriendBean(1, 1));
            cusFriendBeanList.add(new CusFriendBean(1, 1));
            cusFriendSleepView.setSleepList(cusFriendBeanList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateSportData(String bleMac) {
        resultStepList.clear();
        try {
            String b30HalfHourDBStr = B30HalfHourDao.getInstance().findOriginData(bleMac, currDay, B30HalfHourDao.B16_DETAIL_SPORT);
            if (b30HalfHourDBStr == null) {
                b30SportMaxNumTv.setText("0");
                cusStepDView.setSourList(resultStepList);
                return;
            }

            List<Integer> listInteger = new Gson().fromJson(b30HalfHourDBStr, new TypeToken<List<Integer>>() {
            }.getType());

            resultStepList.addAll(listInteger);
            if (listInteger.size() <= 48) {
                for (int i = 0; i < 48 - listInteger.size(); i++) {
                    resultStepList.add(0);
                }
            } else {
                resultStepList.subList(0, 48);
            }
            b30SportMaxNumTv.setText(Collections.max(resultStepList) + "");
            cusStepDView.setSourList(resultStepList);

        } catch (Exception e) {
            e.printStackTrace();
        }
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


    private OnResponseListener onResponseListener = new OnResponseListener() {
        @Override
        public void onResponse(String s) {
            //Log.e(TAG, "--------response=" + s);
            if (s == null)
                return;
            analysisData(s);
        }
    };

    private void analysisData(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            int type = jsonObject.getInt("type");
            switch (type) {
                case 1: //步数
                    JSONObject dataJson = jsonObject.getJSONObject("data");

                    Message message = handler.obtainMessage();
                    message.what = 0x01;
                    message.obj = dataJson;
                    handler.sendMessage(message);

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    private void showBatterStute(int batteryLevel) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        try {
            int batterys = 0;
            if (batteryLevel >= 0 && batteryLevel < 20) {
                batterys = 0;
                // batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
            } else if (batteryLevel >= 20 && batteryLevel < 40) {
                batterys = 25;
                //batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel >= 40 && batteryLevel < 60) {
                batterys = 50;
                // batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel >= 60 && batteryLevel < 80) {
                batterys = 75;
                // batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel >= 80) {
                batterys = batteryLevel;
                //batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            homeBatteryView.setPower(batterys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            Log.e(TAG, "-----action=" + action);
            if (action.equals(WatchUtils.B18_CONNECTED_ACTION)) { //连接成功
                handler.sendEmptyMessage(0x03);
                homeBatteryStatusTv.setText(getResources().getText(R.string.connted));
                homeFastStatusTv.setTextColor(ContextCompat.getColor(getmContext(), R.color.white));
                homeFastStatusTv.setText(getResources().getText(R.string.more_opera));
            }
            if (action.equals(WatchUtils.B18_DISCONN_ACTION)) {   //已断开
                MyCommandManager.DEVICENAME = null;
                homeBatteryStatusTv.setText(getResources().getText(R.string.disconnted));
                homeFastStatusTv.setTextColor(ContextCompat.getColor(getmContext(), R.color.red));
                homeFastStatusTv.setText(getResources().getText(R.string.disconnted));
            }

            if (action.equals(WatchUtils.B18_UPDATE_HOME_DATA)) { //更新页面数据
                handler.sendEmptyMessage(0x02);
                updateAllHealthData();

            }
        }
    };

    //B36女性功能是否开启隐私
    boolean womenPrivacy = false;

    @OnClick({R.id.homeFastLin, R.id.cusStepViewLin,
            R.id.b18HomeSleepLin, R.id.b36WomenLin,
            R.id.b36WomenPrivacyImg, R.id.b16BmiLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeFastLin:
                try {
                    if (BleProfileManager.getInstance().isConnected()) {
                        startActivity(new Intent(getmContext(), B18DeviceActivity.class));
                    } else {
                        BleProfileManager.getInstance().getScannerController().stopScan();
                        BleProfileManager.getInstance().getConnectController().disConnectDevice();
                        SharedPreferencesUtils.saveObject(getmContext(), Commont.BLENAME, "");
                        SharedPreferencesUtils.saveObject(getmContext(), Commont.BLEMAC, "");
                        startActivity(new Intent(getmContext(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cusStepViewLin:   //步数
                startActivity(new Intent(getmContext(), B18StepDetailActivity.class));
                break;
            case R.id.b18HomeSleepLin:  //睡眠图表点击
                startActivity(new Intent(getmContext(), B18SleepDetailActivity.class));
                break;
            case R.id.b36WomenLin:  //女性图表点击
                startActivity(new Intent(getmContext(), WomenDetailActivity.class));
                break;
            case R.id.b36WomenPrivacyImg:   //是否开启隐私
                if (womenPrivacy) {       //开启了隐私
                    womenPrivacy = false;
                    b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec));

                } else {  //关闭了隐私 ic_b36_home_woman_rec
                    womenPrivacy = true;
                    b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec_pri));
                }
                SharedPreferencesUtils.setParam(getmContext(), "b36_women_privacy", womenPrivacy);
                tunrOnOrOffPrivacy(womenPrivacy);
                break;
            case R.id.b16BmiLayout: //BMI
                startActivity(new Intent(getmContext(), B16BmiManagerActivity.class));
                break;
        }
    }


    private void tunrOnOrOffPrivacy(boolean isOn) {
        if (isOn) {
            status1Tv.setVisibility(View.INVISIBLE);
            status2Tv.setVisibility(View.INVISIBLE);
            status3Tv.setVisibility(View.INVISIBLE);
        } else {
            status1Tv.setVisibility(View.VISIBLE);
            status2Tv.setVisibility(View.VISIBLE);
            status3Tv.setVisibility(View.VISIBLE);
        }

    }


    //显示女性生理周期的状态
    private void showB36WomenStatus() {
        int womenStatus = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.WOMEN_PHYSIOLOGY_STATUS, 0);
        String currStatus = (String) SharedPreferencesUtils.getParam(getmContext(), Commont.WOMEN_SAVE_CURRDAY_STATUS, "无数据");
        if (WatchUtils.isEmpty(currStatus))
            currStatus = verLanguage();
//        Log.e(TAG, "------womenStatus=" + womenStatus + "--currStatus=" + currStatus);
        switch (womenStatus) {
            case 0:     //月经期
            case 3:     //宝妈期
                status1Tv.setText("");
                status2Tv.setText(currStatus);
                status3Tv.setText("");
                break;
            case 1:     //备孕期

                //根据状态判断
                //怀孕概率  排卵期
                if (currStatus.equals(getResources().getString(R.string.b36_ovulation_day).trim())) {
                    homeB36StatusLin.setBackground(getResources().getDrawable(R.drawable.womendetail_ovulation));
                    status1Tv.setText(getResources().getString(R.string.b36_pregnancy_probability));
                    status2Tv.setText("32%");
                    status3Tv.setText(currStatus);

                }

                //排卵期前5天
                for (int i = 1; i <= 5; i++) {
                    if (currStatus.equals(getResources().getString(R.string.b36_ovulation_period) + " " + i + " " + getResources().getString(R.string.data_report_day))) {
                        homeB36StatusLin.setBackground(getResources().getDrawable(R.drawable.womendetail_period_mense_before));
                        status1Tv.setText(getResources().getString(R.string.b36_pregnancy_probability));
                        status2Tv.setText((10 + (i * 5)) + "%");
                        status3Tv.setText(currStatus);

                    }
                }

                //排卵期第7天
                if (currStatus.equals(getResources().getString(R.string.b36_ovulation_period) + " " + 7 + " " + getResources().getString(R.string.data_report_day))) {
                    status2Tv.setText(27 + "%");
                    commPerStatus(currStatus);
                }
                //排卵期第8天
                if (currStatus.equals(getResources().getString(R.string.b36_ovulation_period) + " " + 8 + " " + getResources().getString(R.string.data_report_day))) {
                    status2Tv.setText(22 + "%");
                    commPerStatus(currStatus);
                }
                //排卵期第9天
                if (currStatus.equals(getResources().getString(R.string.b36_ovulation_period) + " " + 9 + " " + getResources().getString(R.string.data_report_day))) {
                    status2Tv.setText(18 + "%");
                    commPerStatus(currStatus);
                }
                //排卵期第10天
                if (currStatus.equals(getResources().getString(R.string.b36_ovulation_period) + " " + 10 + " " + getResources().getString(R.string.data_report_day))) {
                    status2Tv.setText(15 + "%");
                    commPerStatus(currStatus);
                }

                //怀孕概率 月经期和安全期
                if (currStatus.contains(getResources().getString(R.string.b36_period)) || currStatus.contains(getResources().getString(R.string.b36_safety_day))) {
                    homeB36StatusLin.setBackground(getResources().getDrawable(R.drawable.womendetail_period_mense_after));
                    status1Tv.setText(getResources().getString(R.string.b36_pregnancy_probability));
                    status2Tv.setText("<1%");
                    status3Tv.setText(currStatus);
                }


                break;
            case 2:     //怀孕期
                homeB36StatusLin.setBackground(getResources().getDrawable(R.drawable.womendetail_period_preing));
                //获取预产日期
                String perDay = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.BABY_BORN_DATE, WatchUtils.getCurrentDate());
                //计算预产期还有多少天
                int dirrDay = WatchUtils.intervalTime(WatchUtils.getCurrentDate(), perDay);
                if (dirrDay > 0) {
                    status1Tv.setText(getResources().getString(R.string.data_report_day));
                    status2Tv.setText(dirrDay + "");
                    status3Tv.setText(getResources().getString(R.string.b36_baby_born_days));
                } else if (dirrDay == 0) {
                    status1Tv.setText("");
                    status2Tv.setText("baby" + getResources().getString(R.string.birthday));
                    status3Tv.setText("");
                }

                break;
        }

    }

    private void commPerStatus(String currStatus) {
        homeB36StatusLin.setBackground(getResources().getDrawable(R.drawable.womendetail_period_mense_after));
        status1Tv.setText(getResources().getString(R.string.b36_pregnancy_probability));
        status3Tv.setText(currStatus);
    }


    //判断系统语言
    private String verLanguage() {
        //语言
        String locals = Locale.getDefault().getLanguage();
        //国家
        String country = Locale.getDefault().getCountry();
        if (!WatchUtils.isEmpty(locals)) {
            if (locals.equals("zh")) {
                if (country.equals("TW") || country.equals("HK")) {
                    return "無數據";
                } else {
                    return "无数据";
                }

            } else {
                return "No Data";
            }
        } else {
            return "No Data";
        }

    }



    //实例化图表
    private void initLinChartData() {
        if (b16EntryList.size() <= 0)
            return;
        //x轴
        XAxis xAxis = b16BmiLinChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);

        //Y轴
        YAxis leftY = b16BmiLinChart.getAxisLeft();
        leftY.setDrawAxisLine(false);
        leftY.setDrawLabels(true);

        YAxis rightY = b16BmiLinChart.getAxisRight();
        rightY.setEnabled(false);

        //保证Y轴从0开始，不然会上移一点
        leftY.setAxisMinimum(0f);
        leftY.setAxisMaximum(maxStep + 100);
        leftY.setLabelCount(3);
        leftY.setGranularity(1f);

        leftY.setEnabled(false);


        //设置图形的基本属性
        /***图表设置***/
        b16BmiLinChart.setDrawGridBackground(false); //是否展示网格线
        b16BmiLinChart.setDrawBorders(false); //是否显示边界
        b16BmiLinChart.setDragEnabled(false); //是否可以拖动
        b16BmiLinChart.setScaleEnabled(false); // 是否可以缩放
        b16BmiLinChart.setTouchEnabled(false); //是否有触摸事件

        b16BmiLinChart.setClipValuesToContent(true);

        //设置XY轴动画效果
        b16BmiLinChart.getDescription().setEnabled(false);


        Legend legend = b16BmiLinChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setYOffset(-5);
        b16BmiLinChart.setData(new LineData());
        //legend.setEnabled(false);


        //设置数据
        lineDataSet = new LineDataSet(b16EntryList, "");
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setForm(Legend.LegendForm.LINE);
        lineDataSet.setColor(Color.parseColor("#FA9850")); //设置该线的颜色
        lineDataSet.setCircleColor(Color.parseColor("#40e0d0")); //设置节点圆圈颜色
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false); //设置是否显示点的坐标值
        lineDataSet.setValueTextColor(Color.parseColor("#5abdfe"));
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);// 设置平滑曲线

        //是否填充
        lineDataSet.setDrawFilled(true);


        //线的集合（可单条或多条线）
        List<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(lineDataSet);
        //把要画的所有线(线的集合)添加到LineData里
        LineData lineData = new LineData(dataSets);

        b16BmiLinChart.setDrawGridBackground(false);
        //把最终的数据setData
        b16BmiLinChart.setData(lineData);
        b16BmiLinChart.isAnimationCacheEnabled();
        //动画
        b16BmiLinChart.animateX(1000);


    }
}
