package com.example.bozhilun.android.w30s.ble;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.bozhilun.android.b30.b30view.B30CusBloadView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommConstant;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.CommStepCountDb;
import com.example.bozhilun.android.commdbserver.CommentDataActivity;
import com.example.bozhilun.android.commdbserver.W30StepDetailBean;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.activity.W30DetailHeartActivity;
import com.example.bozhilun.android.w30s.activity.W30DetailSleepActivity;
import com.example.bozhilun.android.w30s.activity.W30DetailSportActivity;
import com.example.bozhilun.android.w30s.activity.W30SSettingActivity;
import com.example.bozhilun.android.w30s.activity.W37IntelDetailActivity;
import com.example.bozhilun.android.w30s.bean.W30HeartBean;
import com.example.bozhilun.android.w30s.views.W30CusHeartView;
import com.example.bozhilun.android.w30s.views.W30CusSleepChartView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suchengkeji.android.w30sblelibrary.W30SBLEGattAttributes;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2019/7/4
 */
public class W37HomeFragment extends BaseFragment {

    private static final String TAG = "W37HomeFragment";

    View w37View;
    @BindView(R.id.iv_top)
    ImageView ivTop;
    //连接状态
    @BindView(R.id.b30connectStateTv)
    TextView b30connectStateTv;
    //显示电量的图片
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    //显示具体电量
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30_top_dateTv)
    TextView b30TopDateTv;
    //圆形进度条
    @BindView(R.id.W30ProgressBar)
    WaveProgress W30ProgressBar;
    //目标步数
    @BindView(R.id.w30GoalStepTv)
    TextView w30GoalStepTv;
    //步数最大值
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    //步数的图表
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;

    //血压图表 W37有血压
    @BindView(R.id.w37BloodLin)
    LinearLayout w37BloodLin;
    //W37的血压图表
    @BindView(R.id.b30HomeBloadChart)
    B30CusBloadView b30HomeBloadChart;


    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;
    @BindView(R.id.w30_heart_chart)
    W30CusHeartView w30HeartChart;
    @BindView(R.id.w30StartEndTimeTv)
    TextView w30StartEndTimeTv;
    @BindView(R.id.w30_sleep_chart)
    W30CusSleepChartView w30SleepChart;
    @BindView(R.id.w30HomeSwipeRefreshLayout)
    SmartRefreshLayout w30HomeSwipeRefreshLayout;
    Unbinder unbinder;
    //最近一次血压时间
    @BindView(R.id.bloadLastTimeTv)
    TextView bloadLastTimeTv;
    //最近一次血压值
    @BindView(R.id.b30BloadValueTv)
    TextView b30BloadValueTv;

    //状态
    @BindView(R.id.homeFastStatusTv)
    TextView homeFastStatusTv;

    private Context mContext;
    private W37DataAnalysis w37DataAnalysis;
    private Gson gson = new Gson();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);

    //默认步数
    int defaultSteps = 0;
    /**
     * 目标步数
     */
    int goalStep = 8000;
    /**
     * 图表步数数据
     */
    List<BarEntry> b30ChartList = new ArrayList<>();



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:  //超时
                    if (w30HomeSwipeRefreshLayout != null) {
                        w30HomeSwipeRefreshLayout.setEnableRefresh(false);
                    }
                    break;
                case 0x01:  //连接成功
                    if (w30HomeSwipeRefreshLayout != null) {
                        w30HomeSwipeRefreshLayout.setEnableRefresh(true);
                        w30HomeSwipeRefreshLayout.autoRefresh();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x00);
                            }
                        }, 10 * 1000);
                    }

                    MyCommandManager.DEVICENAME = "W30";
                    //同步步数
                    w37DataAnalysis.syncUserInfoData(getmContext());
                    //设置语言
                    w37DataAnalysis.setDeviceLanguage(getmContext());
                    //获取数据
                    readDataFromDevice();
                    break;
                case 0x02:  //刷新数据
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (w30HomeSwipeRefreshLayout != null)
                            w30HomeSwipeRefreshLayout.finishRefresh();

                        updateAllDeviceData();

                        //开启上传数据
                        W37HomeActivity w37HomeActivity = (W37HomeActivity) getActivity();
                        if (w37HomeActivity != null) {
                            w37HomeActivity.startW30UploadServer();
                            w37HomeActivity.updateDevice();
                        }

                    }
                    break;
                case 1001:  //显示步数
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        int battery = (int) SharedPreferencesUtils.getParam(getmContext(), "w30_battery", 0);

                        showBatterStute(battery);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                W30ProgressBar.setMaxValue(goalStep);
                                W30ProgressBar.setValue(defaultSteps);
                            }
                        });
                        List<W30StepDetailBean> lt = (List<W30StepDetailBean>) msg.obj;

                        List<Integer> maxSteps = new ArrayList<>();
                        if (lt == null || lt.isEmpty()) {
                            for (int i = 0; i < 24; i++) {
                                maxSteps.add(0);
                                b30ChartList.add(new BarEntry(i, 0));
                            }
                            initBarChart(b30ChartList);
                            return;
                        }

                        for (int i = 0; i < lt.size(); i++) {
                            maxSteps.add(Integer.valueOf(lt.get(i).getStepValue()));
                            b30ChartList.add(new BarEntry(i, Float.valueOf(lt.get(i).getStepValue())));
                        }
                        b30SportMaxNumTv.setText(Collections.max(maxSteps) + "");
                        initBarChart(b30ChartList);
                    }
                    break;
                case 1002:      //睡眠解析
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        Map<String, String> allSleepMap = (Map<String, String>) msg.obj;
                        if (allSleepMap == null || allSleepMap.isEmpty()) {
                            w30SleepChart.setNoDataColor(Color.parseColor("#6074BF"));
                            w30SleepChart.setSleepResultList(new ArrayList<Integer>());
                            //w30SleepChart.invalidate();
                            w30StartEndTimeTv.setText("--:--");
                            return;
                        }
                        //睡觉表示的字符串
                        String sleepStr = allSleepMap.get("sleepResultStr");
                        List<Integer> sleepList = gson.fromJson(sleepStr, new TypeToken<List<Integer>>() {
                        }.getType());
                        w30SleepChart.setSleepResultList(sleepList);
                        w30StartEndTimeTv.setText(allSleepMap.get("startSleepDate") + "--" + allSleepMap.get("endSleepDate"));
                    }

                    break;
                case 1003:      //心率
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<Integer> heartValueList = (List<Integer>) msg.obj;
                        if (heartValueList.size() > 0) {
                            w30HeartChart.setRateDataList(heartValueList);
                            lastTimeTv.setText(getResources().getString(R.string.zuigaoxinlv) + ": ");
                            b30HeartValueTv.setText(Collections.max(heartValueList) + "");
                        } else {
                            w30HeartChart.setRateDataList(heartValueList);
                            lastTimeTv.setText(getResources().getString(R.string.zuigaoxinlv) + ": ");
                            b30HeartValueTv.setText("--");
                        }
                    }
                    break;
                case 1004:  //血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<Map<String, Map<Integer, Integer>>> w37BpResultList = (List<Map<String, Map<Integer, Integer>>>) msg.obj;
                        b30HomeBloadChart.setScale(false);
                        b30HomeBloadChart.setBpVerticalMap(w37BpResultList);

                        //获取最近一次的血压数据
                        if(w37BpResultList.isEmpty()){
                            bloadLastTimeTv.setText(getResources().getString(R.string.string_recent)+": --");
                            b30BloadValueTv.setText("--");
                        }else{
                            Map<String,Map<Integer,Integer>> lastMap = w37BpResultList.get(w37BpResultList.size()-1);
                            for(Map.Entry<String,Map<Integer,Integer>> mm : lastMap.entrySet()){
                                bloadLastTimeTv.setText(getResources().getString(R.string.string_recent)+":"+mm.getKey());
                                for(Map.Entry<Integer,Integer> bpMM : mm.getValue().entrySet()){
                                    b30BloadValueTv.setText(bpMM.getKey()+"/"+bpMM.getValue());
                                }

                            }

                        }

                    }

                    break;

                case 88:

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
        if (w37DataAnalysis == null)
            w37DataAnalysis = W37DataAnalysis.getW37DataAnalysis();
        String goalStepStr = (String) SharedPreferencesUtils.getParam(getmContext(), "w30stag", "10000");
        if (WatchUtils.isEmpty(goalStepStr))
            goalStepStr = "10000";
        goalStep = Integer.valueOf(goalStepStr);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37Constance.W37_CONNECTED_ACTION);
        intentFilter.addAction(W37Constance.W37_DISCONNECTED_ACTION);
        getmContext().registerReceiver(broadcastReceiver, intentFilter);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        w37View = inflater.inflate(R.layout.fragment_new_w30_record, container, false);
        unbinder = ButterKnife.bind(this, w37View);


        initViews();
        initData();


        return w37View;
    }

    private void initViews() {
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
        if (w30GoalStepTv != null)
            w30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));
        if (verticalBleName() != 0)
            ivTop.setImageResource(verticalBleName());

        //进度圆显示默认的步数
        if (getActivity() != null && !getActivity().isFinishing() && W30ProgressBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    W30ProgressBar.setMaxValue(goalStep);
                    W30ProgressBar.setValue(defaultSteps);
                }
            });
        }

        //没有连接是禁止刷新
        if (MyCommandManager.DEVICENAME == null) {
            w30HomeSwipeRefreshLayout.setEnableRefresh(false);
        }

        //刷新
        w30HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (MyCommandManager.DEVICENAME == null)
                    return;
                handler.sendEmptyMessage(0x01);
            }
        });

        //初始化一遍图表，
        b30BarChart.invalidate();

        b30TopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getmContext(), W37IntelDetailActivity.class));
                return true;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null) {    //已连接
            if (getActivity() != null && !getActivity().isFinishing()){
                b30connectStateTv.setText(getResources().getString(R.string.connted));
                homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
            }

        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing()){
                b30connectStateTv.setText(getResources().getString(R.string.disconnted));
                homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
            }
            try {
                W37HomeActivity w37HomeActivity = (W37HomeActivity) getActivity();
                if (w37HomeActivity != null)
                    w37HomeActivity.isAutoConnBle();
            }catch (Exception e){
               e.printStackTrace();
            }

        }


    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            if (WatchConstants.isScanConn) {
                WatchConstants.isScanConn = false;
                handler.sendEmptyMessage(0x01);
                return;
            }
            updateAllDeviceData();
        }

    }


    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null)
                getmContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //从数据库中查询所有的数据
    private void updateAllDeviceData() {
        String bleMac = WatchUtils.getSherpBleMac(getmContext());
        if (WatchUtils.isEmpty(bleMac))
            return;
        //更新步数详情数据
        updateSportData(bleMac);

        //更新睡眠详情
        updateSleepData(bleMac);

        //更新心率详情
        updateHeartData(bleMac);

        //更新血压数据
        updateW37BloodData(bleMac);

    }

    //更新血压数据
    private void updateW37BloodData(final String bleMac) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<Map<String, Map<Integer, Integer>>> w37BpResultList = new ArrayList<>();
                    List<B30HalfHourDB> w37BloodList = B30HalfHourDao.getInstance().findW37BloodDetail(WatchUtils.getCurrentDate(), bleMac, B30HalfHourDao.TYPE_BP);
                    if (w37BloodList == null) {
                        Message message = handler.obtainMessage();
                        message.obj = w37BpResultList;
                        message.what = 1004;
                        handler.sendMessage(message);
                    } else {
                        B30HalfHourDB b30HalfHourDB = w37BloodList.get(0);
                        //Log.e(TAG, "--------血压b30HalfHourDB=" + b30HalfHourDB.toString());
                        List<Integer> bpInteList = gson.fromJson(b30HalfHourDB.getOriginData(), new TypeToken<List<Integer>>() {
                        }.getType());
                        List<Integer> tmpList = new ArrayList<>();
                        int timeStr = -5;
                        for (int k = 0; k < bpInteList.size(); k += 2) {
                            if (k <= bpInteList.size() - 2) {
                                Map<String, Map<Integer, Integer>> bpMap = new HashMap<>();
                                Map<Integer, Integer> tmpMap = new HashMap<>();
                                tmpMap.put(bpInteList.get(k), bpInteList.get(k + 1));
                                timeStr += 5;
                                tmpList.add(timeStr);
                                int hours = timeStr / 60;
                                int mines = timeStr % 60;
                                String keyBp = (hours < 10 ? "0" + hours : hours) + ":" + (mines < 10 ? "0" + mines : mines);
                                bpMap.put(keyBp, tmpMap);
                                w37BpResultList.add(bpMap);
                            }

                        }

                        //去0
                        List<Map<String, Map<Integer, Integer>>> tempLt = new ArrayList<>();
                        for (Map<String, Map<Integer, Integer>> vP : w37BpResultList) {
                            for (Map<Integer, Integer> bpM : vP.values()) {
                                for (Map.Entry<Integer, Integer> vM : bpM.entrySet()) {
                                    if (vM.getKey() != 0 & vM.getValue() != 0) {
                                        Map<String, Map<Integer, Integer>> bpMap = new HashMap<>();
                                        bpMap.putAll(vP);
                                        tempLt.add(bpMap);
                                    }
                                }
                            }
                        }


                        Message message = handler.obtainMessage();
                        message.obj = tempLt;
                        message.what = 1004;
                        handler.sendMessage(message);

                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //查询心率并显示
    private void updateHeartData(final String bleMac) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Integer> resultHeartList = new ArrayList<>();
                    List<B30HalfHourDB> w30HeartList = B30HalfHourDao.getInstance().findW30HeartDetail(WatchUtils.getCurrentDate(),
                            bleMac, B30HalfHourDao.TYPE_RATE);
                    if (w30HeartList == null) {
                        Message message = handler.obtainMessage();
                        message.what = 1003;
                        message.obj = resultHeartList;
                        handler.sendMessage(message);
                    } else {
                        String heartStr = w30HeartList.get(w30HeartList.size() - 1).getOriginData();
                        Map<String, String> htMap = gson.fromJson(heartStr, Map.class);
                        //Log.e(TAG, "----------htMap=" + htMap.toString());

                        String htStr = htMap.get(WatchUtils.getCurrentDate());

                        List<W30HeartBean> heartBeanList = gson.fromJson(htStr, new TypeToken<List<W30HeartBean>>() {
                        }.getType());

                        for (W30HeartBean w30HeartBean : heartBeanList) {
                            // Log.e(TAG,"-------w30HeartBean="+w30HeartBean.toString());
                            resultHeartList.add(w30HeartBean.getHeartValues());
                        }

                        Message message = handler.obtainMessage();
                        message.what = 1003;
                        message.obj = resultHeartList;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //查询睡眠并显示
    private void updateSleepData(final String bleMac) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> tmpMap = new HashMap<>();
                    List<B30HalfHourDB> w30SleepList = B30HalfHourDao.getInstance().findW30SleepDetail(WatchUtils.getCurrentDate(), bleMac, B30HalfHourDao.TYPE_SLEEP);
                    if (w30SleepList == null) {
                        Message message = handler.obtainMessage();
                        message.what = 1002;
                        message.obj = tmpMap;
                        handler.sendMessage(message);
                        return;
                    }
                    //解析所需数据
                    String sleepStr = w30SleepList.get(0).getOriginData();
                    Map<String, String> allMap = gson.fromJson(sleepStr, Map.class);
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = allMap;
                    handler.sendMessage(message);

                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //查询步数并显示
    private void updateSportData(final String bleMac) {
        b30ChartList.clear();
        CommStepCountDb countDb = CommDBManager.getCommDBManager().findCountStepForUpload(bleMac, WatchUtils.getCurrentDate());
        if (countDb == null) {
            defaultSteps = 0;
        } else {
            defaultSteps = countDb.getStepnumber();
        }

        final List<W30StepDetailBean> emptyList = new ArrayList<>();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<B30HalfHourDB> w30List = B30HalfHourDao.getInstance().findW30SportData(WatchUtils.getCurrentDate(), bleMac, B30HalfHourDao.TYPE_SPORT);
                    if (w30List == null) {
                        Message message = handler.obtainMessage();
                        message.what = 1001;
                        message.obj = emptyList;
                        handler.sendMessage(message);
                        return;
                    }

                    //解析所需数据
                    String stepStr = w30List.get(0).getOriginData();
                    Map<String, String> allMap = gson.fromJson(stepStr, Map.class);
                    String stepDetail = allMap.get("stepDetail");

                    List<W30StepDetailBean> tempList = new Gson().fromJson(stepDetail, new TypeToken<List<W30StepDetailBean>>() {
                    }.getType());
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = tempList;
                    handler.sendMessage(message);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 步数图表展示
     */
    private void initBarChart(List<BarEntry> pointBar) {

        BarDataSet barDataSet = new BarDataSet(pointBar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        //barDataSet.setColor(Color.parseColor("#fa8072"));//设置第一组数据颜色
        barDataSet.setColor(Color.parseColor("#88d785"));//设置第一组数据颜色

        if (b30BarChart == null) return;
        Legend mLegend = b30BarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threeBarData = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threeBarData.add(barDataSet);

        BarData bardata = new BarData(threeBarData);
        bardata.setBarWidth(0.3f);  //设置柱子宽度

        b30BarChart.setData(bardata);
        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        b30BarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
        b30BarChart.getXAxis().setEnabled(false);
        b30BarChart.setPinchZoom(false);
        b30BarChart.setScaleEnabled(false);
        b30BarChart.setTouchEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);

        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画
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
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
            } else if (batteryLevel >= 20 && batteryLevel < 40) {
                batterys = 25;
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel >= 40 && batteryLevel < 60) {
                batterys = 50;
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel >= 60 && batteryLevel < 80) {
                batterys = 75;
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel >= 80) {
                batterys = 100;
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            batteryPowerTv.setText(batterys + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //从设备中读取数据
    private void readDataFromDevice() {
        if (w37DataAnalysis != null) {
            w37DataAnalysis.analysW37BackData(W30SBLEGattAttributes.syncTime(), new CallDatasBackListenter() {
                @Override
                public void callDatasBackSportListenter(W30SSportData sportData) {
                    Log.e(TAG, "----sportData----=" + sportData.toString());
                    if (sportData == null)
                        return;
                    Log.e(TAG, "--------保存日期=" + sportData.getData());
                    defaultSteps = sportData.getSportStep();

                    //保存步数,总步数
                    CommDBManager.getCommDBManager().saveCommCountStepDate(WatchUtils.getSherpBleName(MyApp.getContext()),
                            WatchUtils.getSherpBleMac(MyApp.getContext()), sportData.getData(), (sportData.getSportStep()));

                    Map<String, String> allDataMap = new HashMap<>();
                    allDataMap.put("countSteps", sportData.getSportStep() + "");
                    allDataMap.put("countDiscance", sportData.getDistance() + "");
                    allDataMap.put("countKcal", sportData.getCalory() + "");

                    /**
                     * 步数的详细数据
                     */
                    //将步数的数组转换成date:HH:mm,value:0x00 格式的数据
                    List<Map<String, String>> changeListMap = new ArrayList<>();
                    for (int i = 0; i < sportData.getSport_data().size(); i++) {
                        Map<String, String> mp = new HashMap<>();
                        int sportDate = i + 1;
                        mp.put("stepDate", sportDate < 10 ? ("0" + sportDate + ":00") : sportDate + ":00");
                        mp.put("stepValue", sportData.getSport_data().get(i) + "");
                        changeListMap.add(mp);
                    }

                    allDataMap.put("stepDetail", gson.toJson(changeListMap));
                    B30HalfHourDao.getInstance().saveW30SportData(sportData.getData(), WatchUtils.getSherpBleMac(getActivity()),
                            B30HalfHourDao.TYPE_SPORT, gson.toJson(allDataMap));

                }

                @Override
                public void callDatasBackSleepListenter(W30SSleepData sleepData) {
                    Log.e(TAG, "----sleepData----=" + sleepData.toString());
                    if (sleepData == null)
                        return;
                    //Log.e(TAG, "-------sleepData=" + sleepData.toString());
                    try {
                        List<W30S_SleepDataItem> sleepDataItemList = sleepData.getSleepDataList();
                        Map<String, String> sleepDateMap = new HashMap<>();
                        //深睡的总时间
                        int deepCount = 0;
                        //浅睡的总时间
                        int lowCount = 0;
                        //苏醒次数
                        int awakeCount = 0;
                        //组建睡眠表示的数据
                        List<Integer> sleepResultList = new ArrayList<>();
                        sleepDataItemList.remove(0); //移除第一个元素
                        for (int i = 0; i < sleepDataItemList.size(); i++) {
                            //开始的日期
                            //开始日期
                            String startDateStr = sleepDataItemList.get(i).getStartTime();
                            long startLongDate = sdf.parse(startDateStr).getTime();

                            if (i + 1 < sleepDataItemList.size()) {   //后一个日期
                                //结束日期
                                String endDateStr = sleepDataItemList.get(i + 1).getStartTime();
                                long nextLongDate = sdf.parse(endDateStr).getTime();

                                int differenceV = (int) ((nextLongDate - startLongDate) / (60 * 1000));
                                //Log.e(TAG, "----------差值=" + sleepDataItemList.get(i).getSleep_type() + "--" + (differenceV < 0 ? (differenceV + 24 * 60) : differenceV));

                                int sleepType = Integer.valueOf(sleepDataItemList.get(i).getSleep_type());
                                int sleepTime = (differenceV < 0 ? (differenceV + 24 * 60) : differenceV);

                                if (sleepType == 3) { //深睡
                                    deepCount += sleepTime;
                                } else if (sleepType == 2) {    //浅睡
                                    lowCount += sleepTime;
                                }
                                if (sleepType == 4) { //苏醒次数
                                    awakeCount++;
                                }

                                /**
                                 * 状态说明
                                 * 0,4,1,5为清醒状态
                                 * 2 浅睡状态
                                 * 3，深睡状态
                                 *
                                 */
                                for (int n = 0; n < sleepTime; n++) {
                                    if (sleepType == 1) {
                                        sleepResultList.add(1);
                                    } else if (sleepType == 2) {   //浅睡
                                        sleepResultList.add(2);
                                    } else if (sleepType == 3) {   //深睡
                                        sleepResultList.add(3);
                                    } else if (sleepType == 4) {
                                        sleepResultList.add(4);
                                    } else if (sleepType == 5) {
                                        sleepResultList.add(5);
                                    }
                                }

                            }
                        }
                        /**
                         * 整理保存数据库中的睡眠数据,详细数据
                         */
                        //入睡时间
                        sleepDateMap.put(CommConstant.W30_SLEEP_START_DATE, sleepDataItemList.get(0).getStartTime() + "");
                        //起床时间
                        sleepDateMap.put(CommConstant.W30_SLEEP_END_DATE, sleepDataItemList.get(sleepDataItemList.size() - 1).getStartTime() + "");
                        //深睡总时长
                        sleepDateMap.put(CommConstant.W30_SLEEP_DEEP_COUNT_TIME, deepCount + "");
                        //浅睡总时长
                        sleepDateMap.put(CommConstant.W30_SLEEP_LOW_COUNT_TIME, lowCount + "");
                        //苏醒次数
                        sleepDateMap.put(CommConstant.W30_SLEEP_AWAKE_COUNT, awakeCount + "");
                        //总的睡眠时间=深睡+浅睡
                        sleepDateMap.put(CommConstant.W30_SLEEP_COUNT_ALL_TIME, (deepCount + lowCount) + "");
                        //Log.e(TAG, "---------深睡时间=" + deepCount + "---浅睡时间=" + lowCount);
                        sleepResultList.add(0, 1);
                        sleepResultList.add(1); //最后添加一个
                        //Log.e(TAG, "----------整理的睡眠数据=" + sleepResultList);

                        sleepDateMap.put(CommConstant.W30_SLEEP_RESULT_SHOW, gson.toJson(sleepResultList));

                        B30HalfHourDao.getInstance().saveW30SleepDetail(sleepData.getSleepData(),
                                WatchUtils.getSherpBleMac(getmContext()), B30HalfHourDao.TYPE_SLEEP, gson.toJson(sleepDateMap));


                        //保存汇总数据
                        /**
                         * 保存睡眠数据
                         * @param bleName
                         * @param bleMac
                         * @param dataStr 日期
                         * @param deep 深睡时长
                         * @param low 浅睡时长
                         * @param sober 清醒时长
                         * @param allSleep 所有睡眠时间
                         * @param sleeptime 入睡时间
                         * @param waketime  清醒时间
                         * @param wakeCount 清醒次数
                         */

                        //保存睡眠
                        CommDBManager.getCommDBManager().saveCommSleepDbData(WatchUtils.getSherpBleName(MyApp.getContext()), WatchUtils.getSherpBleMac(MyApp.getContext()),
                                WatchUtils.obtainFormatDate(1),
                                deepCount,
                                lowCount,
                                0,
                                (deepCount + lowCount),
                                sleepDataItemList.get(0).getStartTime(),
                                sleepDataItemList.get(sleepDataItemList.size() - 1).getStartTime(),
                                awakeCount);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void callDatasBackDeviceDataListenter(W30SDeviceData deviceData) {
                    Log.e(TAG, "----deviceData----=" + deviceData.toString());
                    SharedPreferencesUtils.setParam(getmContext(), "w30_battery", deviceData.getDevicePower());

                    //保存设备的版本号
                    SharedPreferencesUtils.setParam(getmContext(),"w_version_code",String.valueOf(deviceData.getDeviceVersionNumber()));

                }

                @Override
                public void callDatasBackHeartListenter(W30SHeartData heartData) {
                    Log.e(TAG, "----heartData----=" + heartData.toString());
                    if (heartData == null)
                        return;
                    Map<String, String> heartMap = getHeartList(heartData);
                    //Log.e(TAG,"-------heartMap="+heartMap.toString());
                    if (heartMap.isEmpty())
                        return;
                    //保存心率详细数据
                    B30HalfHourDao.getInstance().saveW30HeartDetail(heartData.getDate(), WatchUtils.getSherpBleMac(getmContext()),
                            B30HalfHourDao.TYPE_RATE, gson.toJson(heartMap));

                    //最高心率
                    List wo_heart_data2 = heartData.getWo_heart_data();

                    int maxHeart = wo_heart_data2 == null ? 0 : (int) Collections.max(wo_heart_data2);
                    int minHeart = wo_heart_data2 == null ? 0 : (int) Collections.min(wo_heart_data2);


                    //保存心率汇总数据
                    //保存心率
                    CommDBManager.getCommDBManager().saveCommHeartData(WatchUtils.getSherpBleName(MyApp.getContext()), WatchUtils.getSherpBleMac(MyApp.getContext()),
                            WatchUtils.getCurrentDate(), maxHeart, minHeart, 70);

                }

                //W37血压数据返回
                @Override
                public void callDatasBackBloodListener(W37BloodBean w37BloodBean) {
                    if (w37BloodBean == null)
                        return;
                    Log.e(TAG, "-----血压=" + w37BloodBean.toString());
                    List<Integer> blList = w37BloodBean.getBloodList();
                    if (blList == null || blList.isEmpty())
                        return;

                    //保存血压汇总数据
                    CommDBManager.getCommDBManager().saveCommBloodDb(WatchUtils.getSherpBleMac(getmContext()),w37BloodBean.getDateStr(),120,80,120,80);

                    //保存W37血压详细数据
                    B30HalfHourDao.getInstance().saveW37BloodDetail(w37BloodBean.getDateStr(),
                            WatchUtils.getSherpBleMac(getmContext()), B30HalfHourDao.TYPE_BP, gson.toJson(w37BloodBean.getBloodList()));
                }

                //设备的数据同步完了
                @Override
                public void callDatasBackListenterIsok() {
                    handler.sendEmptyMessage(0x02);
                }
            });
        }

    }


    private void initData() {


    }

    //判断是哪款手环
    private int verticalBleName() {
        String bName = (String) SharedPreferencesUtils.readObject(getmContext(), Commont.BLENAME);
        Log.e("==========名字=", bName);
        if (!WatchUtils.isEmpty(bName)) {
            if (bName.equals("W30")) {
                return R.mipmap.image_w30s;
            } else if (bName.equals("W31")) {
                return R.mipmap.icon_w31_home_top;
            } else if (bName.equals("W37")) {
                w37BloodLin.setVisibility(View.VISIBLE);
                return R.mipmap.icon_w37_home_top;
            } else {
                return 0;
            }
        }
        return 0;
    }


    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b30SportChartLin1, R.id.w30HeartLin,
            R.id.w30SleepLin, R.id.battery_watchRecordShareImg,
            R.id.w37BloodLin,R.id.homeFastLin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.battery_watchRecordShareImg:  //图表
                startActivity(new Intent(getmContext(), CommentDataActivity.class));
                break;
            case R.id.b30SportChartLin1:   //步数图表
                startActivity(new Intent(getmContext(), W30DetailSportActivity.class));
                break;
            case R.id.w30HeartLin:  //点击心率图表
                startActivity(new Intent(getmContext(), W30DetailHeartActivity.class));
                break;
            case R.id.w30SleepLin:  //点击睡眠图表
                startActivity(new Intent(getmContext(), W30DetailSleepActivity.class));
                break;
            case R.id.w37BloodLin:  //血压
                startActivity(new Intent(getmContext(), W37BloodDetailActivity.class));
                break;
            case R.id.homeFastLin:  //快捷操作
                if(MyCommandManager.DEVICENAME != null){
                    SharePeClear.sendCmdDatas(getContext());
                    startActivity(new Intent(getActivity(), W30SSettingActivity.class).putExtra("is18i", "W30S"));
                }else{
                    try {
                        String saveMac = WatchUtils.getSherpBleMac(getContext());
                        MyApp.getInstance().getW37BleOperateManager().stopScan();
                        if(!WatchUtils.isEmpty(saveMac))
                            MyApp.getInstance().getW37BleOperateManager().disBleDeviceByMac(saveMac);
                        startActivity(new Intent(getContext(), NewSearchActivity.class));
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }


    //监听连接状态的广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            Log.e(TAG, "---------Home-=" + action);
            if (action.equals(W37Constance.W37_CONNECTED_ACTION)) {   //连接成功

                if (getActivity() != null && !getActivity().isFinishing()){
                    MyCommandManager.DEVICENAME = "W30";
                    b30connectStateTv.setText(getResources().getString(R.string.connted));
                    homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
                }

                handler.sendEmptyMessage(0x01);
            }
            if (action.equals(W37Constance.W37_DISCONNECTED_ACTION)) {    //断开连接
                //handler.sendEmptyMessage(88);
                MyCommandManager.DEVICENAME = null;
                if (getActivity() != null && !getActivity().isFinishing()){
                    b30connectStateTv.setText(getResources().getString(R.string.disconnted));
                    homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                }

            }
        }
    };

    Map<String, String> heartMap;
    List<W30HeartBean> listValues;

    Map<String, String> getHeartList(W30SHeartData objects) {
        if (listValues != null) listValues.clear();
        if (heartMap != null) heartMap.clear();
        String timeString = objects.getDate().trim();
        List wo_heart_data = objects.getWo_heart_data();

        if (wo_heart_data != null || wo_heart_data.size() > 0) {

            List<Integer> mouch = new ArrayList<>();
            for (int i = 0; i < wo_heart_data.size(); i += 2) {
                mouch.add((int) wo_heart_data.get(i));
            }
            if (!mouch.isEmpty()) {
                //Log.d("--------AAA--", "" + mouch.size());
                for (int i = 1; i <= 48; i++) {
                    String datas = "";
                    int heardAllNumber = 0;
                    int ValeCont = 0;
                    for (int j = (3 * i) - 3; j <= (3 * i) - 1; j++) {
                        if ((int) mouch.get(j) > 0) {
                            ValeCont++;
                        }
                        heardAllNumber += (int) mouch.get(j);
                    }

                    if (ValeCont == 0) {
                        ValeCont = 1;
                    }
                    datas = String.valueOf((heardAllNumber / ValeCont)).split("[.]")[0];//取每半小时的平均心率
                    if (Integer.valueOf(datas) < 50) {
                        datas = "0";
                    }

                    double timesHour = (double) ((i - 1) * 0.5);
                    int hours = 0;
                    int mins = 0;
                    String[] splitT = String.valueOf(timesHour).split("[.]");
                    if (splitT.length >= 2) {
                        hours = Integer.valueOf(splitT[0]);
                        mins = Integer.valueOf(splitT[1]) * 60 / 10;
                    } else {
                        hours = Integer.valueOf(splitT[0]);
                        mins = 0;
                    }
                    String timeHour = "";
                    String timeMin = "";
                    if (hours <= 9) {
                        timeHour = "0" + hours;
                    } else {
                        timeHour = "" + hours;
                    }
                    if (mins <= 9) {
                        timeMin = "0" + mins;
                    } else {
                        timeMin = "" + mins;
                    }
                    String upDataTime = timeHour + ":" + timeMin;
                    if (listValues == null) listValues = new ArrayList<>();
                    W30HeartBean heartBean = new W30HeartBean(upDataTime, Integer.valueOf(datas));
                    listValues.add(heartBean);
                }

            }
        }
        if (listValues == null || listValues.isEmpty()) return null;
        String str = gson.toJson(listValues); // List转json
        if (heartMap == null) heartMap = new HashMap<>();
        heartMap.put(timeString, str);
        return heartMap;
    }


}
