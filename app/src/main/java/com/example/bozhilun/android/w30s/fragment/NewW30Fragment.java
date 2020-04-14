package com.example.bozhilun.android.w30s.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.service.FriendsUploadServices;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommConstant;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.CommStepCountDb;
import com.example.bozhilun.android.commdbserver.CommentDataActivity;
import com.example.bozhilun.android.commdbserver.W30StepDetailBean;
import com.example.bozhilun.android.commdbserver.detail.UploadW30DetailService;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.W30SHomeActivity;
import com.example.bozhilun.android.w30s.activity.W30DetailHeartActivity;
import com.example.bozhilun.android.w30s.activity.W30DetailSleepActivity;
import com.example.bozhilun.android.w30s.activity.W30DetailSportActivity;
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
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
 * Date 2019/3/19
 */
public class NewW30Fragment extends BaseFragment {

    private static final String TAG = "NewW30Fragment";

    View w30View;

    //显示连接手环的图片
    @BindView(R.id.iv_top)
    ImageView ivTop;
    //连接状态
    @BindView(R.id.b30connectStateTv)
    TextView b30connectStateTv;
    //电池表示图片
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    //电量显示
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30_top_dateTv)
    TextView commentB30TitleTv;
    //进度条
    @BindView(R.id.W30ProgressBar)
    WaveProgress W30ProgressBar;
    //目标步数
    @BindView(R.id.w30GoalStepTv)
    TextView w30GoalStepTv;
    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;
    //步数的图表
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.w30_heart_chart)
    W30CusHeartView w30HeartChart;
    @BindView(R.id.w30_sleep_chart)
    W30CusSleepChartView w30SleepChart;
    @BindView(R.id.w30StartEndTimeTv)
    TextView w30StartEndTimeTv;
    Unbinder unbinder;


    @BindView(R.id.w30HomeSwipeRefreshLayout)
    SmartRefreshLayout w30HomeSwipeRefreshLayout;
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;

    private Context mContext;

    private Gson gson = new Gson();

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

    W30CallBackDataListener callDatasBackListenter;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:  //防止为正确获取数据时一直显示的问题
                    if (w30HomeSwipeRefreshLayout != null && w30HomeSwipeRefreshLayout.isEnableLoadMore())
                        w30HomeSwipeRefreshLayout.finishRefresh();

                    break;
                case 0x01:  //开始获取数据
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

                    if (callDatasBackListenter == null) {
                        callDatasBackListenter = new W30CallBackDataListener();
                    }
                    MyCommandManager.DEVICENAME = "W30";
                    syncUserInfoData(getmContext());
                    SharePeClear.sendCmdDatas(getmContext());
                    boolean zh = VerifyUtil.isZh(getmContext());
                    if (!zh){
                        //Log.e(TAG,"========搜索  -- 设置了英文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(0);
                    }else {
                        //Log.e(TAG,"========搜索  -- 设置了中文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(1);
                    }


                    MyApp.getInstance().getmW30SBLEManage().syncTime(callDatasBackListenter);
                    break;
                case 0x02:      //更新所有数据
                    if(getActivity() != null && !getActivity().isFinishing()){
                        if (w30HomeSwipeRefreshLayout != null)
                            w30HomeSwipeRefreshLayout.finishRefresh();

                        updateAllDeviceData();

                        W30SHomeActivity w30SHomeActivity = (W30SHomeActivity) getActivity();
                        if(w30SHomeActivity != null){
                            w30SHomeActivity.startW30UploadServer();
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
                        if(heartValueList.size()>0){
                            w30HeartChart.setRateDataList(heartValueList);
                            lastTimeTv.setText(getResources().getString(R.string.zuigaoxinlv)+": ");
                            b30HeartValueTv.setText(Collections.max(heartValueList)+"");
                        }else{
                            w30HeartChart.setRateDataList(heartValueList);
                            lastTimeTv.setText(getResources().getString(R.string.zuigaoxinlv)+": ");
                            b30HeartValueTv.setText("--");
                        }

                    }
                    break;

            }


        }
    };

    private void startUploadData() {
        try {
            //开始上传本地数据
            //开始上传本地的数据
            CommDBManager.getCommDBManager().startUploadDbService(getmContext());

            Intent intent = new Intent(getmContext(),UploadW30DetailService.class);
            getmContext().startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        getmContext().registerReceiver(broadcastReceiver, addW30IntentFilter());
        String goalStepStr = (String) SharedPreferencesUtils.getParam(getmContext(),"w30stag","10000");
        if(WatchUtils.isEmpty(goalStepStr))
            goalStepStr = "10000";
        goalStep = Integer.valueOf(goalStepStr);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        w30View = LayoutInflater.from(getmContext()).inflate(R.layout.fragment_new_w30_record, container, false);
        unbinder = ButterKnife.bind(this, w30View);

        initViews();


        commentB30TitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG,"-----------设置目标步数=");
                startActivity(new Intent(getmContext(),W37IntelDetailActivity.class));
                return true;
            }
        });

        return w30View;
    }

    private void initViews() {
        commentB30TitleTv.setText(WatchUtils.getCurrentDate());
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
        if(MyCommandManager.DEVICENAME == null){
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


        commentB30TitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent = new Intent(getmContext(), FriendsUploadServices.class);
//                getmContext().startService(intent);
                return true;
            }
        });
    }


    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            updateAllDeviceData();
        }
    }

    private void updateAllDeviceData() {
//        if (MyCommandManager.DEVICENAME == null)
//            return;
        String bleMac = WatchUtils.getSherpBleMac(getmContext());
        if (WatchUtils.isEmpty(bleMac))
            return;
        //更新步数详情数据
        updateSportData(bleMac);

        //更新睡眠详情
        updateSleepData(bleMac);

        //更新心率详情
        updateHeartData(bleMac);


    }


    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null) {    //已连接
            if (getActivity() != null && !getActivity().isFinishing())
                b30connectStateTv.setText(getResources().getString(R.string.connted));

        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing())
                b30connectStateTv.setText(getResources().getString(R.string.disconnted));
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null)
            getmContext().unregisterReceiver(broadcastReceiver);
    }

    @OnClick({R.id.b30SportChartLin1, R.id.w30HeartLin,
            R.id.w30SleepLin, R.id.battery_watchRecordShareImg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.battery_watchRecordShareImg: //数据汇总图表
                startActivity(new Intent(getmContext(), CommentDataActivity.class));
                break;
            case R.id.b30SportChartLin1:    //运动点击
                startActivity(new Intent(getmContext(), W30DetailSportActivity.class));
                break;
            case R.id.w30HeartLin:  //心率
                startActivity(new Intent(getmContext(), W30DetailHeartActivity.class));
                break;
            case R.id.w30SleepLin:  //睡眠
                startActivity(new Intent(getmContext(), W30DetailSleepActivity.class));
                break;
        }
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
            } else if(bName.equals("W37")){
                return R.mipmap.icon_w37_home_top;
            }
            else {
                return 0;
            }
        }
        return 0;
    }

    //W30的连接和断开连接action
    private IntentFilter addW30IntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_FINED_SERVICES);

        return intentFilter;
    }

    //连接状态的广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "----w30----action=" + action);
            if (action == null)
                return;
            if (action.equals(W30SBLEServices.ACTION_GATT_FINED_SERVICES)) {   //连接成功
                if (getActivity() != null && !getActivity().isFinishing())
                    b30connectStateTv.setText(getResources().getString(R.string.connted));
                handler.sendEmptyMessage(0x01);
            }
            if (action.equals(W30SBLEServices.ACTION_GATT_DISCONNECTED)) {    //断开连接
                if (getActivity() != null && !getActivity().isFinishing())
                    b30connectStateTv.setText(getResources().getString(R.string.disconnted));
            }
        }
    };


    //手环数据返回
    class W30CallBackDataListener implements W30SBLEServices.CallDatasBackListenter {
        //回调运动数据
        @Override
        public void CallDatasBackListenter(final W30SSportData sportData) {
            if (sportData == null)
                return;
            Log.e(TAG,"--------保存日期="+sportData.getData());
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

        //回调睡眠数据
        @Override
        public void CallDatasBackListenter(W30SSleepData sleepData) {
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
                sleepResultList.add(0,1);
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
                CommDBManager.getCommDBManager().saveCommSleepDbData(WatchUtils.getSherpBleName(MyApp.getContext()),WatchUtils.getSherpBleMac(MyApp.getContext()),
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

        //回调设备信息数据
        @Override
        public void CallDatasBackListenter(W30SDeviceData deviceData) {
            if (deviceData == null)
                return;
            Log.e(TAG, "-------deviceData=" + deviceData.toString());
            SharedPreferencesUtils.setParam(getmContext(), "w30_battery", deviceData.getDevicePower());
        }

        //回调心率数据
        @Override
        public void CallDatasBackListenter(W30SHeartData heartData) {
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

            int maxHeart = wo_heart_data2==null?0:(int) Collections.max(wo_heart_data2);
            int minHeart = wo_heart_data2==null?0:(int) Collections.min(wo_heart_data2);


            //保存心率汇总数据
            //保存心率
            CommDBManager.getCommDBManager().saveCommHeartData(WatchUtils.getSherpBleName(MyApp.getContext()),WatchUtils.getSherpBleMac(MyApp.getContext()),
                    WatchUtils.getCurrentDate(),maxHeart,minHeart,70);

        }

        //over了，
        @Override
        public void CallDatasBackListenterIsok() {
            Log.e(TAG, "--------CallDatasBackListenterIsok---");
            handler.sendEmptyMessage(0x02);
        }
    }


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


    //查询步数并显示
    private void updateSportData(final String bleMac) {
        CommStepCountDb countDb = CommDBManager.getCommDBManager().findCountStepForUpload(bleMac, WatchUtils.getCurrentDate());
        if (countDb == null) {
            defaultSteps = 0;
        } else {
            defaultSteps = countDb.getStepnumber();
        }

        final List<W30StepDetailBean> emptyList = new ArrayList<>();
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
    }


    //查询睡眠并显示
    private void updateSleepData(final String bleMac) {
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
    }


    //查询心率并显示
    private void updateHeartData(final String bleMac) {
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
                    String heartStr = w30HeartList.get(w30HeartList.size()-1).getOriginData();
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


    //同步用户信息
    private void syncUserInfoData(Context context) {
        String userData = (String) SharedPreferencesUtils.readObject(context, "saveuserinfodata");
        //Log.d("-----用户资料-----1----", "--------" + userData);
        if (!WatchUtils.isEmpty(userData)) {
            try {
                int weight;
                JSONObject jsonO = new JSONObject(userData);
                String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                String userAge = jsonO.getString("birthday");   //生日
                String userWeight = jsonO.getString("weight");  //体重
                String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
                //Log.d("-----用户资料-----2----", userWeight + "====" + tempWeight);
                if (tempWeight.contains(".")) {
                    weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim());
                } else {
                    weight = Integer.valueOf(tempWeight);
                }
                String userHeight = ((String) SharedPreferencesUtils.getParam(context, "userheight", "")).trim();
                int sex;
                if (userSex.equals("M")) {    //男
                    sex = 1;
                } else {
                    sex = 2;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight);


                /**
                 * 设置用户资料
                 *
                 * @param isMale 1:男性 ; 2:女性
                 * @param age    年龄
                 * @param hight  身高cm
                 * @param weight 体重kg
                 */
                SharedPreferencesUtils.setParam(context, "user_sex", sex);
                SharedPreferencesUtils.setParam(context, "user_age", age);
                SharedPreferencesUtils.setParam(context, "user_height", height);
                SharedPreferencesUtils.setParam(context, "user_weight", weight);
                //Log.d("-----用户资料-----2----", sex + "===" + age + "===" + height + "===" + weight);
                /**
                 * 设置用户资料
                 *
                 * @param isMale 1:男性 ; 2:女性
                 * @param age    年龄
                 * @param hight  身高cm
                 * @param weight 体重kg
                 */
                MyApp.getInstance().getmW30SBLEManage().setUserProfile(sex, age, height, weight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
