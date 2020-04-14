package com.example.bozhilun.android.b30.b30homefragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.B30BloadDetailActivity;
import com.example.bozhilun.android.b30.B30DeviceActivity;
import com.example.bozhilun.android.b30.B30HeartDetailActivity;
import com.example.bozhilun.android.b30.B30HomeActivity;
import com.example.bozhilun.android.b30.B30SleepDetailActivity;
import com.example.bozhilun.android.b30.B30StepDetailActivity;
import com.example.bozhilun.android.b30.ManualMeaureBloadActivity;
import com.example.bozhilun.android.b30.ManualMeaureHeartActivity;
import com.example.bozhilun.android.b30.b30view.B30CusBloadView;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.b30.b30view.B30CusSleepView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPSleepData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.b30.service.ConnBleHelpService;
import com.example.bozhilun.android.b30.women.WomenDetailActivity;
import com.example.bozhilun.android.b31.InternalTestActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommentDataActivity;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.LocalizeTool;
import com.github.mikephil.charting.data.Entry;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
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
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.TimeData;
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
 * B30和B36的主页面
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeFragment extends LazyFragment implements ConnBleHelpService.ConnBleMsgDataListener {

    private static final String TAG = "B30HomeFragment";

    View b30HomeFragment;
    @BindView(R.id.b30ProgressBar)
    WaveProgress b30ProgressBar;
    Unbinder unbinder;
    //日期
    @BindView(R.id.b30_top_dateTv)
    TextView b30TopDateTv;
    //电量
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30connectStateTv)
    TextView b30ConnectStateTv;

    //目标步数
    @BindView(R.id.b30GoalStepTv)
    TextView b30GoalStepTv;
    //运动步数的chart
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    //步数数据
    List<BarEntry> b30ChartList;
    //血压
    @BindView(R.id.bloadLastTimeTv)
    TextView bloadLastTimeTv;
    @BindView(R.id.b30BloadValueTv)
    TextView b30BloadValueTv;
    /**
     * 血压图表
     */
    @BindView(R.id.b30HomeBloadChart)
    B30CusBloadView b30HomeBloadChart;
    //睡眠图表
    @BindView(R.id.b30CusSleepView)
    B30CusSleepView b30CusSleepView;
    @BindView(R.id.b30StartEndTimeTv)
    TextView b30StartEndTimeTv;
    @BindView(R.id.b30HomeSwipeRefreshLayout)
    SmartRefreshLayout b30HomeSwipeRefreshLayout;
    @BindView(R.id.homeTodayTv)
    TextView homeTodayTv;
    @BindView(R.id.homeTodayImg)
    ImageView homeTodayImg;
    @BindView(R.id.homeYestTodayTv)
    TextView homeYestTodayTv;
    @BindView(R.id.iv_top)
    ImageView iv_top;
    @BindView(R.id.homeYestdayImg)
    ImageView homeYestdayImg;
    @BindView(R.id.homeBeYestdayTv)
    TextView homeBeYestdayTv;
    @BindView(R.id.homeBeYestdayImg)
    ImageView homeBeYestdayImg;

    //B30的血压和手动测量界面
    @BindView(R.id.b30HomeB30Lin)
    LinearLayout b30HomeB30Lin;
    //B36的显示 女性功能的布局
    @BindView(R.id.b36WomenStatusLin)
    LinearLayout b36WomenStatusLin;
    //状态1
    @BindView(R.id.status1Tv)
    TextView status1Tv;
    //状态2
    @BindView(R.id.status2Tv)
    TextView status2Tv;
    //状态3
    @BindView(R.id.status3Tv)
    TextView status3Tv;
    //B36女性状态显示的背景
    @BindView(R.id.homeB36StatusLin)
    ImageView homeB36StatusLin;
    //B36女性功能隐私图片
    @BindView(R.id.b36WomenPrivacyImg)
    ImageView b36WomenPrivacyImg;
    /**
     * 日期的集合
     */
    private List<String> b30BloadList;
    /**
     * 一对高低血压集合
     */
    private List<Map<Integer, Integer>> bloadListMap;


    //血压的集合map，key : 时间；value : 血压值map
    private List<Map<String,Map<Integer,Integer>>> resultBpMapList;


    private List<BarEntry> tmpB30StepList;
    @BindView(R.id.b30SportChartLin1)
    LinearLayout b30SportChartLin1;
    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;

    //心率图标
    @BindView(R.id.b30HomeHeartChart)
    B30CusHeartView b30CusHeartView;
    //最后一次时间
    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    //心率值
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;
    //心率图标数据
    List<Integer> heartList;
    //最大步数
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;

    private ConnBleHelpService connBleHelpService;
    /**
     * 上下文
     */
    private Context mContext;

    @BindView(R.id.homeFastStatusTv)
    TextView homeFastStatusTv;

    /**
     * 目标步数
     */
    int goalStep;
    //默认步数
    int defaultSteps = 0;

    private List<Integer> sleepList;
    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;
    /**
     * Json帮助工具
     */
    private Gson gson;
    /**
     * 本地化工具类
     */
    private LocalizeTool mLocalTool;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (getActivity() != null && !getActivity().isFinishing() && b30HomeSwipeRefreshLayout != null) {
//                if(msg.what != 1000){
//                    b30HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
//                }
//
//            }
            switch (msg.what) {
                case 1000:
                    if (mHandler != null) mHandler.removeMessages(1001);// 正常关闭就移除延时口令

                    updatePageData();
                    B30HomeActivity activity = (B30HomeActivity) getActivity();
                    if (activity != null) activity.startUploadDate();// 上传数据
                    if (b30HomeSwipeRefreshLayout != null)
                        b30HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
                    break;
                case 1001:
                    Log.d("bobo", "handleMessage: 请求超过默认秒数");
                    if (b30HomeSwipeRefreshLayout != null)
                        b30HomeSwipeRefreshLayout.finishRefresh();// 停止下拉刷新
                    break;
                case 1002://步数
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        List<CusVPHalfSportData> sportData = (List<CusVPHalfSportData>) msg.obj;
                        if (b30SportMaxNumTv != null)
                            b30SportMaxNumTv.setText("0" + getResources().getString(R.string.steps));
                        showSportStepData(sportData);//展示步数的图表
                    }
                    break;
                case 1003://心率
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (lastTimeTv != null)
                            lastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + "--:--");
                        if (b30HeartValueTv != null)
                            b30HeartValueTv.setText("0 bpm");
                        List<CusVPHalfRateData> rateData = (List<CusVPHalfRateData>) msg.obj;
                        showSportHeartData(rateData);//展示心率的图表
                    }
                    break;
                case 1004://血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (bloadLastTimeTv != null)
                            bloadLastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + "--");
                        //最近时间的血压高低值
                        if (b30BloadValueTv != null)
                            b30BloadValueTv.setText("-:--" + "mmhg");
                        List<CusVPHalfHourBpData> bpData = (List<CusVPHalfHourBpData>) msg.obj;
                        showBloodData(bpData);//展示血压的图表
                    }
                    break;
                case 1005:  //睡眠
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (b30StartEndTimeTv != null)
                            b30StartEndTimeTv.setText("--:--");
                        CusVPSleepData sleepData = (CusVPSleepData) msg.obj;
                        showSleepData(sleepData);//展示睡眠的图表
                    }
                    break;
                case 0x80:
                    B30HomeActivity activitys = (B30HomeActivity) getActivity();
                    if (activitys != null) activitys.startUploadDate();// 上传数据
                    break;
            }
        }
    };


    private String bleName;    //蓝牙名称
    //B36女性功能是否开启隐私
    boolean womenPrivacy = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        getmContext().registerReceiver(broadcastReceiver, intentFilter);
        if (connBleHelpService == null) {
            connBleHelpService = ConnBleHelpService.getConnBleHelpService();
        }
        bleName = (String) SharedPreferencesUtils.readObject(getActivity(), Commont.BLENAME);
        connBleHelpService.setConnBleMsgDataListener(this);
        //目标步数
        goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 8000);
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
        b30HomeFragment = inflater.inflate(R.layout.fragment_b30_home_layout, container, false);
        unbinder = ButterKnife.bind(this, b30HomeFragment);

        initViews();
        initData();
        return b30HomeFragment;
    }

    private void initData() {
        if (b30GoalStepTv != null)
            b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));

        //运动图表
        b30ChartList = new ArrayList<>();
        tmpB30StepList = new ArrayList<>();
        tmpIntegerList = new ArrayList<>();
        //心率图表
        heartList = new ArrayList<>();
        //血压图表
        resultBpMapList = new ArrayList<>();
        b30BloadList = new ArrayList<>();
        bloadListMap = new ArrayList<>();
        sleepList = new ArrayList<>();
        gson = new Gson();
        mLocalTool = new LocalizeTool(getmContext());

        //updatePageData();
    }


    private void initViews() {

        //if (b30TopDateTv != null) b30TopDateTv.setText("    ");
        //判断设置目标步数和当前步数
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            if (b30TopDateTv != null) b30TopDateTv.setText(WatchUtils.getCurrentDate());
            b30ProgressBar.setMaxValue(goalStep);
            b30ProgressBar.setValue(defaultSteps);

        }
        if (b30SportChartLin1 != null)
            b30SportChartLin1.setBackgroundColor(getResources().getColor(R.color.b30_sport));
        if (iv_top != null) iv_top.setImageResource(verB30OrB36Ble());

        if (MyCommandManager.DEVICENAME == null) {
            if (b30HomeSwipeRefreshLayout != null)
                b30HomeSwipeRefreshLayout.setEnableRefresh(false);
        }
        if (b30HomeSwipeRefreshLayout != null)
            b30HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    Log.d(TAG, "-----B30手动刷新   onRefresh: getBleMsgData()");
                    getBleMsgData();
                }
            });


        //判断是B30还是B36 ,B36无血压功能
        verIsTureB36();


        b30TopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getmContext(),InternalTestActivity.class));
                return true;
            }
        });
    }


    private void verIsTureB36() {
        try {
            if (WatchUtils.isEmpty(bleName))
                bleName = (String) SharedPreferencesUtils.readObject(getActivity(), Commont.BLENAME);
            if (bleName.equals("B36") ||bleName.equals("B36M")) {
                b30HomeB30Lin.setVisibility(View.GONE);
                if (WatchUtils.isB36SexWomen(getActivity())) {
                    b36WomenStatusLin.setVisibility(View.VISIBLE);
                    womenPrivacy = (boolean) SharedPreferencesUtils.getParam(getmContext(), "b36_women_privacy", false);
                    if (womenPrivacy) {       //开启了隐私 ic_b36_home_woman_rec
                        b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec_pri));
                    } else {  //关闭了隐私
                        b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec));
                    }
                    tunrOnOrOffPrivacy(womenPrivacy);
                    showB36WomenStatus();

                }

              //  UpDatasBase.chageDevicesNames("B36");//更改设备类型
            } else {
                //UpDatasBase.chageDevicesNames("B30");//更改设备类型
                b30HomeB30Lin.setVisibility(View.VISIBLE);
                b36WomenStatusLin.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //显示女性生理周期的状态
    private void showB36WomenStatus() {
        int womenStatus = (int) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_PHYSIOLOGY_STATUS, 0);
        String currStatus = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_SAVE_CURRDAY_STATUS, "无数据");
        if (WatchUtils.isEmpty(currStatus))
            currStatus = verLanguage();
//        Log.e(TAG, "------womenStatus=" + womenStatus + "--currStatus=" + currStatus);
        switch (womenStatus) {
            case 0:     //月经期
            case 3:     //宝妈期

//                Log.e(TAG, "-------currStatus=" + currStatus);
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

    //判断是B30或者是B36
    private int verB30OrB36Ble() {
        String bName = (String) SharedPreferencesUtils.readObject(MyApp.getInstance().getApplicationContext(), Commont.BLENAME);
        if (WatchUtils.isEmpty(bName))
            return R.mipmap.ic_b30_top;
        if (getActivity() != null && !getActivity().isFinishing()) {
            if(bName.equals("B30"))
                return R.mipmap.ic_b30_top;
            if(bName.equals("B36"))
                return R.mipmap.ic_b36_top;
            if(bName.equals("B36M"))
                return R.mipmap.ic_b36m_home_top;
            if(bName.equals("Ringmii"))
                return R.mipmap.hx_home;
        }
        return R.mipmap.ic_b30_top;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            int curCode = (int) SharedPreferencesUtils.getParam(getmContext(), "curr_code", 0);
            clearDataStyle(curCode);//设置每次回主界面，返回数据不清空的
            if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                long currentTime = System.currentTimeMillis() / 1000;
                //保存的时间
                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "saveDate", currentTime + "");
                long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
                if (WatchConstants.isScanConn) {  //是搜索进来的
                    WatchConstants.isScanConn = false;
                    //getBleMsgData();
                    if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.autoRefresh();
                    Log.d("bobo", "onFragmentVisibleChange: autoRefresh()");
                } else {  //不是搜索进来的
                    if (diffTime > 20) {// 大于五分钟没更新再取数据
                        getBleMsgData();
                        if (b30HomeSwipeRefreshLayout != null)
                            b30HomeSwipeRefreshLayout.autoRefresh();
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    /* 这里会有断连的情况 */
    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.ADDRESS != null) {    //已连接
            if (b30ConnectStateTv != null)
                b30ConnectStateTv.setText(getResources().getString(R.string.connted));
            homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
            int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 0);
//            if (param > 0) {
//                showBatterStute(param);
//            }
            showBatterStute(param);
        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (b30ConnectStateTv != null)
                    b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                B30HomeActivity activity = (B30HomeActivity) getActivity();
                if (activity != null) activity.reconnectDevice();// 重新连接
            }
        }
        //判断是否是B36
        verIsTureB36();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null) {
                getmContext().unregisterReceiver(broadcastReceiver);
            }
            if (b30ProgressBar != null) {
                b30ProgressBar.removeAnimator();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 电量更新 */
    @Override
    public void getBleBatteryData(int batteryLevel) {
        SharedPreferencesUtils.setParam(getmContext(), Commont.BATTERNUMBER, batteryLevel);//保存下电量
        showBatterStute(batteryLevel);
    }

    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    void showBatterStute(int batteryLevel) {
        try {
            if (batteryTopImg == null) return;
            if (batteryLevel >= 0 && batteryLevel == 1) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel == 2) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel == 3) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel == 4) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel * 25 + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 手环实时步数有更新 */
    @Override
    public void getBleSportData(final int step) {
//        Log.e(TAG, "---------手环步数=" + step);
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    b30ProgressBar.setMaxValue(goalStep);
                    b30ProgressBar.setValue(step);
                }
            });

        }
    }

    /* 原始数据有更新 */
    @Override
    public void onOriginData() {
        mHandler.sendEmptyMessage(1000);// 步数和健康数据都取到了,就关闭刷新条
    }

    /**
     * 更新页面数据
     */
    private void updatePageData() {
        String mac = MyApp.getInstance().getMacAddress();
        if (WatchUtils.isEmpty(mac)) return;
        String date = WatchUtils.obtainFormatDate(currDay);
        updateStepData(mac, date);
        updateSportData(mac, date);
        updateRateData(mac, date);
        updateBpData(mac, date);
        updateSleepData(mac, WatchUtils.obtainFormatDate(currDay));
    }

    /**
     * 取出本地步数数据,并显示
     */
    private void updateStepData(String mac, String date) {
        String step = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                .TYPE_STEP);
        if (WatchUtils.isEmpty(step))
            step = 0 + "";
        int stepLocal = 0;
        try {
            stepLocal = Integer.valueOf(step);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        defaultSteps = stepLocal;
        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
            b30ProgressBar.setMaxValue(goalStep);
            b30ProgressBar.setValue(defaultSteps);
        }

    }

    /**
     * 取出本地运动数据
     */
    private void updateSportData(String mac, String date) {
        try {
            String sport = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                    .TYPE_SPORT);
//        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
//        }.getType());

            List<CusVPHalfSportData> sportData = gson.fromJson(sport,new TypeToken<List<CusVPHalfSportData>>(){}.getType());

            Message message = new Message();
            message.what = 1002;
            message.obj = sportData;
            mHandler.sendMessage(message);//发送消息，展示步数图标
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 取出本地心率数据
     */
    private void updateRateData(String mac, String date) {
        try {
            String rate = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                    .TYPE_RATE);
//        List<HalfHourRateData> rateData = gson.fromJson(rate, new TypeToken<List<HalfHourRateData>>() {
//        }.getType());

            List<CusVPHalfRateData> rateData = gson.fromJson(rate,new TypeToken<List<CusVPHalfRateData>>() {
            }.getType());

            Message message = new Message();
            message.what = 1003;
            message.obj = rateData;
            mHandler.sendMessage(message);//发送消息，展示心率的图表
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 取出本地血压数据
     */
    private void updateBpData(String mac, String date) {
        try {
            String bp = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                    .TYPE_BP);
//        List<HalfHourBpData> bpData = gson.fromJson(bp, new TypeToken<List<HalfHourBpData>>() {
//        }.getType());

            List<CusVPHalfHourBpData> bpData = gson.fromJson(bp, new TypeToken<List<CusVPHalfHourBpData>>() {
            }.getType());

            Message message = new Message();
            message.what = 1004;
            message.obj = bpData;
            mHandler.sendMessage(message);//发送消息，展示血压的图表
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 取出本地睡眠数据
     */
    private void updateSleepData(String mac, String date) {
        try {
            Log.d("-------数据时间----", date);
            String sleep = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao
                    .TYPE_SLEEP);
            //SleepData sleepData = gson.fromJson(sleep, SleepData.class);

            CusVPSleepData sleepData = gson.fromJson(sleep,CusVPSleepData.class);

            if (sleepData != null) {
                Log.e(TAG, "---------睡眠=" + sleepData.toString());
            }

            Message message = new Message();
            message.what = 1005;
            message.obj = sleepData;
            mHandler.sendMessage(message);//发送消息，展示睡眠的图表
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 展示睡眠图表
     */
    private void showSleepData(CusVPSleepData sleepData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        try {
            sleepList.clear();
            if (sleepData != null) {
                if (b30StartEndTimeTv != null)
                    b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() + "-" + sleepData.getSleepUp().getColck());
                String sleepLin = sleepData.getSleepLine();
//            Log.e(TAG, "-----睡眠的长度=" + sleepLin + "---=length=" + sleepLin.length());
                if (!WatchUtils.isEmpty(sleepLin) || sleepLin.length() > 2) {
                    if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
                    for (int i = 0; i < sleepLin.length(); i++) {
                        if (i <= sleepLin.length() - 1) {
                            int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                            sleepList.add(subStr);
                        }
                    }
                    sleepList.add(0, 2);
                    sleepList.add(0);
                    sleepList.add(2);
                }
            } else {
                if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
            }
            if (b30CusSleepView != null) {
                if (sleepList != null && !sleepList.isEmpty()) {
                    b30CusSleepView.setSeekBarShow(false);
                    b30CusSleepView.setSleepList(sleepList);
                } else {
                    b30CusSleepView.setSeekBarShow(false);
                    b30CusSleepView.setSleepList(new ArrayList<Integer>());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 展示血压图表
     */
    @SuppressLint("SetTextI18n")
    private void showBloodData(List<CusVPHalfHourBpData> bpData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        try {
            b30BloadList.clear();
            bloadListMap.clear();
            resultBpMapList.clear();
            if (bpData != null && !bpData.isEmpty()) {
                //获取日期
                for (CusVPHalfHourBpData halfHourBpData : bpData) {
                    Map<String,Map<Integer,Integer>> mapMap = new HashMap<>();

                    b30BloadList.add(halfHourBpData.getTime().getColck());// 时:分
                    Map<Integer, Integer> mp = new HashMap<>();
                    mp.put(halfHourBpData.getLowValue(), halfHourBpData.getHighValue());
                    bloadListMap.add(mp);

                    mapMap.put(halfHourBpData.getTime().getColck(),mp);
                    resultBpMapList.add(mapMap);
                }
                //最近一次的血压数据
                CusVPHalfHourBpData lastHalfHourBpData = bpData.get(bpData.size() - 1);
                if (lastHalfHourBpData != null) {
                    if (bloadLastTimeTv != null)
                        bloadLastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + lastHalfHourBpData.getTime().getColck());
                    //最近时间的血压高低值
                    if (b30BloadValueTv != null)
                        b30BloadValueTv.setText(lastHalfHourBpData.getHighValue() + "/" + lastHalfHourBpData.getLowValue() + "mmhg");
                }
            }
            if (b30HomeBloadChart != null)
                b30HomeBloadChart.setBpVerticalMap(resultBpMapList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 展示心率图表
     */
    private void showSportHeartData(List<CusVPHalfRateData> rateData) {
        try {
            if (getActivity() == null || getActivity().isFinishing()) return;
            heartList.clear();
            if (rateData != null && !rateData.isEmpty()) {
                List<Map<String, Integer>> listMap = new ArrayList<>();
                int k = 0;
                for (int i = 0; i < 48; i++) {
                    Map<String, Integer> map = new HashMap<>();
                    int time = i * 30;
                    map.put("time", time);
                    CusVPTimeData tmpDate = rateData.get(k).getTime();
                    int tmpIntDate = tmpDate.getHMValue();

                    if (tmpIntDate == time) {
                        map.put("val", rateData.get(k).getRateValue());
                        if (k < rateData.size() - 1) {
                            k++;
                        }
                    } else {
                        map.put("val", 0);
                    }
                    listMap.add(map);
                }
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Integer> map = listMap.get(i);
                    heartList.add(map.get("val"));
                }
                CusVPHalfRateData lastHalfHourRateData = rateData.get(rateData.size() - 1);
                if (lastHalfHourRateData != null) {
                    if (lastTimeTv != null)
                        lastTimeTv.setText(getResources().getString(R.string.string_recent) + " " + lastHalfHourRateData.getTime().getColck());
                    if (b30HeartValueTv != null)
                        b30HeartValueTv.setText(lastHalfHourRateData.getRateValue() + " bpm");
                }
                if (b30CusHeartView != null) b30CusHeartView.setRateDataList(heartList);
            } else {
                if (b30CusHeartView != null) b30CusHeartView.setRateDataList(heartList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 展示步数的图表
     */
    private void showSportStepData(List<CusVPHalfSportData> sportData) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        if (b30ChartList != null) b30ChartList.clear();
        if (tmpIntegerList != null) tmpIntegerList.clear();
        if (tmpB30StepList != null) tmpB30StepList.clear();

        if (sportData != null && !sportData.isEmpty()) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);

                CusVPTimeData tmpDate = sportData.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();
                if (tmpIntDate == time) {
                    map.put("val", sportData.get(k).getStepValue());
                    if (k < sportData.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
            }

            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> tmpMap = listMap.get(i);
                if (tmpB30StepList != null) tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
                if (tmpIntegerList != null) tmpIntegerList.add(tmpMap.get("val"));
            }
            if (b30ChartList != null) b30ChartList.addAll(tmpB30StepList);
            if (b30SportMaxNumTv != null)
                b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
            initBarChart(b30ChartList);
            if (b30BarChart != null) b30BarChart.invalidate();
        } else {
            initBarChart(b30ChartList);
            if (b30BarChart != null) {
                b30BarChart.setNoDataTextColor(Color.WHITE);
                b30BarChart.invalidate();
            }
        }
    }

    //步数图表展示
    @SuppressWarnings("deprecation")
    private void initBarChart(List<BarEntry> pointbar) {
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
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
        bardata.setBarWidth(0.5f);  //设置柱子宽度

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

    @OnClick({R.id.b30SportChartLin1, R.id.b30BarChart, R.id.b30CusHeartLin,
            R.id.b30CusBloadLin, R.id.b30MeaureHeartImg, R.id.b30MeaureBloadImg,
            R.id.b30SleepLin, R.id.homeTodayTv, R.id.homeYestTodayTv, R.id.homeBeYestdayTv,
            R.id.battery_watchRecordShareImg, R.id.b36WomenStatusLin, R.id.b36WomenPrivacyImg,
            R.id.homeFastLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b30SportChartLin1: // 运动数据详情
            case R.id.b30BarChart: // 运动数据详情
                B30StepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusHeartLin:   //心率详情
                B30HeartDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusBloadLin:   //血压详情
                B30BloadDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30MeaureHeartImg:    //手动测量心率
                startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class));
                break;
            case R.id.b30MeaureBloadImg:    //手动测量血压
                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                break;
            case R.id.b30SleepLin:      //睡眠详情
                B30SleepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate
                        (currDay));
                break;
            case R.id.homeTodayTv:  //今天
                clearDataStyle(0);
                break;
            case R.id.homeYestTodayTv:  //昨天
                clearDataStyle(1);
                break;
            case R.id.homeBeYestdayTv:  //前天
                clearDataStyle(2);
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                startActivity(new Intent(getmContext(), CommentDataActivity.class));
                break;
            case R.id.b36WomenStatusLin:    //女性功能
                startActivity(new Intent(getActivity(), WomenDetailActivity.class));
                break;
            case R.id.b36WomenPrivacyImg:       //隐私是否开启
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

            case R.id.homeFastLin:  //快捷进入设置
                if(MyCommandManager.DEVICENAME != null){
                    startActivity(new Intent(getmContext(),B30DeviceActivity.class));
                }else{
                    if(MyApp.getInstance().getB30ConnStateService() != null)
                      MyApp.getInstance().getB30ConnStateService().stopAutoConn();
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                }
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

    /**
     * 获取手环的数据
     */
//    Runnable runnable;

    private void getBleMsgData() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        clearDataStyle(0);//设置每次回主界面
        SharedPreferencesUtils.setParam(getmContext(), "saveDate", System.currentTimeMillis() / 1000 + "");
        connBleHelpService.getDeviceMsgData();
        /**
         * 连接的成功时只读取昨天一天的数据，刷新时再读取前3天的数据
         */
        String date = mLocalTool.getUpdateDate();// 最后更新总数据的日期
        Log.e(TAG, "-----最后更新总数据的日期--date=" + date);
        if (WatchUtils.isEmpty(date))
            date = WatchUtils.obtainFormatDate(1);  //如果是空的话表示第一次读取
        long delayMillis = 60 * 1000;// 默认超时时间
        //connBleHelpService.readAllHealthData(false);// 刷新三天数据
        if (date.equals(WatchUtils.obtainFormatDate(0))) {
            connBleHelpService.readAllHealthData(true);// 刷新当天    == =刷新3天数据
        } else {
            connBleHelpService.readAllHealthData(false);// 刷新3天数据   === 刷新昨天天数据
        }
        mHandler.sendEmptyMessageDelayed(1001, delayMillis);


    }


    private void clearDataStyle(final int code) {
        long currentTime = System.currentTimeMillis() / 1000;   //当前时间
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "save_curr_time", "");
        long diffTime = (currentTime - Long.valueOf(tmpSaveTime));
        if (diffTime < 2)
            return;
        SharedPreferencesUtils.setParam(getmContext(), "save_curr_time", System.currentTimeMillis() / 1000 + "");

        //if (code == currDay) return;// 防重复点击
        if (homeTodayImg != null) homeTodayImg.setVisibility(View.INVISIBLE);
        if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.INVISIBLE);
        if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code) {
            case 0: //今天
                if (homeTodayImg != null) homeTodayImg.setVisibility(View.VISIBLE);
                break;
            case 1: //昨天
                if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.VISIBLE);
                break;
            case 2: //前天
                if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.VISIBLE);
                break;
        }
        currDay = code;
        //保存当前选择的下标
        SharedPreferencesUtils.setParam(getmContext(), "curr_code", code);
        updatePageData();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(WatchUtils.B30_CONNECTED_ACTION)) { //连接
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textConn = getResources().getString(R.string.connted);
                        homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
                        if (b30ConnectStateTv != null) b30ConnectStateTv.setText(textConn);
                        if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                            mHandler.sendEmptyMessage(0x80);
                            if (b30HomeSwipeRefreshLayout != null) {
                                b30HomeSwipeRefreshLayout.setEnableRefresh(true);
                                b30HomeSwipeRefreshLayout.autoRefresh();
                            }
                        }
                    }
                }
                if (action.equals(WatchUtils.B30_DISCONNECTED_ACTION)) {  //断开
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        String textDis = getResources().getString(R.string.disconnted);
                        homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                        if (b30HomeSwipeRefreshLayout != null)
                            b30HomeSwipeRefreshLayout.setEnableRefresh(false);
                        if (b30ConnectStateTv != null) b30ConnectStateTv.setText(textDis);
                    }
                }
            }
        }
    };


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

}
