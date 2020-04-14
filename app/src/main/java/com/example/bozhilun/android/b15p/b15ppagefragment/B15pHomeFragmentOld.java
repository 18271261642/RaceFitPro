//package com.example.bozhilun.android.b15p.b15ppagefragment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.example.bozhilun.android.Commont;
//import com.example.bozhilun.android.MyApp;
//import com.example.bozhilun.android.R;
//import com.example.bozhilun.android.b15p.activity.B15PHeartDetailActivity;
//import com.example.bozhilun.android.b15p.activity.B15PManualMeaureBloadActivity;
//import com.example.bozhilun.android.b15p.activity.B15PSleepDetailActivity;
//import com.example.bozhilun.android.b15p.activity.B15PStepDetailActivity;
//import com.example.bozhilun.android.b15p.b15pdb.B15PDBCommont;
//import com.example.bozhilun.android.b15p.common.B15PContentState;
//import com.example.bozhilun.android.b15p.interfaces.ConntentStuteListenter;
//import com.example.bozhilun.android.b15p.interfaces.FindDBListenter;
//import com.example.bozhilun.android.b15p.interfaces.SycnDataToDBListenter;
//import com.example.bozhilun.android.b30.ManualMeaureHeartActivity;
//import com.example.bozhilun.android.b30.b30view.B15PCusSleepView;
//import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
//import com.example.bozhilun.android.bleutil.MyCommandManager;
//import com.example.bozhilun.android.commdbserver.CommDBManager;
//import com.example.bozhilun.android.commdbserver.CommentDataActivity;
//import com.example.bozhilun.android.siswatch.LazyFragment;
//import com.example.bozhilun.android.siswatch.utils.WatchUtils;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
//import com.littlejie.circleprogress.circleprogress.WaveProgress;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
//import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
//import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
//import com.tjdL4.tjdmain.Dev;
//import com.tjdL4.tjdmain.L4M;
//import com.tjdL4.tjdmain.contr.BracltBatLevel;
//import com.tjdL4.tjdmain.contr.L4Command;
//
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//
//public class B15pHomeFragment extends LazyFragment
//        implements ConntentStuteListenter {
//    //, SycnChangeUIListenter {
//    public final String TAG = "B15pHomeFragment";
//    View rootView;
//    Unbinder unbinder;
//    //血压图标    血压测试、心率测试 按钮显示和隐藏
//    @BindView(R.id.b30HomeB30Lin)
//    LinearLayout b30HomeB30Lin;
//    //电量
//    @BindView(R.id.batteryTopImg)
//    ImageView batteryTopImg;
//    @BindView(R.id.batteryPowerTv)
//    TextView batteryPowerTv;
//    @BindView(R.id.b30connectStateTv)
//    TextView b30ConnectStateTv;
//    @BindView(R.id.b30HomeSwipeRefreshLayout)
//    SmartRefreshLayout b30HomeSwipeRefreshLayout;
//
//
//    //电量图片
//    @BindView(R.id.battery_watchRecordShareImg)
//    ImageView batteryWatchRecordShareImg;
//    @BindView(R.id.watch_record_titleLin)
//    LinearLayout watchRecordTitleLin;
//    //波浪形进度条
//    @BindView(R.id.b30ProgressBar)
//    WaveProgress b30ProgressBar;
//    //目标步数显示
//    @BindView(R.id.b30GoalStepTv)
//    TextView b30GoalStepTv;
//    //日期
//    @BindView(R.id.b30_top_dateTv)
//    TextView b30TopDateTv;
//    //设备图标
//    @BindView(R.id.iv_top)
//    ImageView ivTop;
//    //链接状态返回
//    private B15PContentState b15PContentState;
//    //默认步数
//    int defaultSteps = 0;
//    int defaultStepsY = 0;//昨天步数
//    int defaultStepsTY = 0;//前天步数
//    /**
//     * 目标步数
//     */
//    int goalStep;
//
//    private B15PDBCommont b15PDBCommont;//b15P数据库工具
//
//
//    //运动图表最大步数
//    @BindView(R.id.sycn_stute)
//    TextView sycnStute;
//    private Context mContext;
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        b15PContentState = B15PContentState.getInstance();
//        b15PContentState.setB15PContentState(this);
//        b15PContentState.bleIsContent();
//        registerListenter();
//
////        //b15p 有血压   血压测试和心率测试，所以显示
////        b30HomeB30Lin.setVisibility(View.VISIBLE);
////        batteryWatchRecordShareImg.setVisibility(View.GONE);//原来的分享---》》现在是数据面板
//
//
//        goalStep = (int) SharedPreferencesUtils.getParam(getmContext(), "b30Goal", 0);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_b15p_home_layout, container, false);
//        unbinder = ButterKnife.bind(this, rootView);
//
//
//        init(rootView);
//        try {
//            /**
//             * 设置监听----同步数据保存本地数据库
//             */
//            L4M.SetResultToDBListener(SycnDataToDBListenter.getSycnDataToDBListenter());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        initData();
//        return rootView;
//    }
//
//
//    /**
//     * 初始
//     *
//     * @param rootView
//     */
//    private void init(View rootView) {
//
////        //b15p 有血压   血压测试和心率测试，所以显示
////        b30HomeB30Lin.setVisibility(View.VISIBLE);
////        batteryWatchRecordShareImg.setVisibility(View.GONE);//原来的分享---》》现在是数据面板
//
//        b30TopDateTv.setText(WatchUtils.getCurrentDate());
//        if (b30GoalStepTv != null)
//            b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));
//
//        ivTop.setImageResource(R.mipmap.ic_home_top_b31);
//        setImageType();
//        //进度圆显示默认的步数
//        if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    b30ProgressBar.setMaxValue(goalStep);
//                    b30ProgressBar.setValue(defaultSteps);
//                }
//            });
//        }
//
//        if (b30HomeSwipeRefreshLayout != null)
//            b30HomeSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//                @Override
//                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                    Log.d(TAG, "手动刷新 ----- getBleDevicesDatas()   读取设备数据");
//                    mHandler.sendEmptyMessageDelayed(0x01, 200);//第一次链接自动获取数据
//
//                }
//            });
//
//    }
//
//    private Context getmContext() {
//        return mContext == null ? MyApp.getContext() : mContext;
//    }
//
//    private void initData() {
//        sleepList = new ArrayList<>();
//        heartList = new ArrayList<>();
//        heartListNew = new ArrayList<>();
//
//        b30ChartList = new ArrayList<>();
//        tmpB30StepList = new ArrayList<>();
//        tmpIntegerList = new ArrayList<>();
//
//        L4M.SetResultListener(mBTResultListenr);
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (b15PContentState != null) b15PContentState.isConnect();
//    }
//
//    @Override
//    protected void onFragmentVisibleChange(boolean isVisible) {
//        super.onFragmentVisibleChange(isVisible);
//        if (isVisible) {
//            if (MyCommandManager.DEVICENAME != null) {
////                long currentTime = System.currentTimeMillis() / 1000;
////                保存的时间
////                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "saveDate", currentTime + "");
////                long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
//                setImageType();
////                if (WatchConstants.isScanConn) {  //是搜索进来的
////                    WatchConstants.isScanConn = false;
////                    getBleDevicesDatas();
////                    if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.autoRefresh();
////                    Log.d("bobo", "onFragmentVisibleChange: autoRefresh()");
////                } else {  //不是搜索进来的
////                    Log.e(TAG, "====不是搜索进来的 " + diffTime);
////                    if (diffTime > 8) {// 大于五分钟没更新再取数据
////                        getBleDevicesDatas();
////                        if (b30HomeSwipeRefreshLayout != null)
////                            b30HomeSwipeRefreshLayout.autoRefresh();
////                    }
////                }
//            }
//        }
//    }
//
//
//    void setImageType() {
//        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
//            if (MyCommandManager.DEVICENAME.length() > 1 && !MyCommandManager.DEVICENAME.equals("F6")) {
//                if (MyCommandManager.DEVICENAME.length() >= 3 && MyCommandManager.DEVICENAME.substring(0, 3).equals("B25")) {
//                    ivTop.setImageResource(R.mipmap.ic_b52_home);
//                } else if (MyCommandManager.DEVICENAME.length() >= 4 && MyCommandManager.DEVICENAME.substring(0, 4).equals("B15P")) {
//                    ivTop.setImageResource(R.mipmap.ic_b15p_home);
//                } else {
//                    if (MyCommandManager.DEVICENAME.substring(0, 1).equals("B")) {
//                        ivTop.setImageResource(R.mipmap.ic_series_w_b);
//                    } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("L")) {
//                        ivTop.setImageResource(R.mipmap.ic_series_w_l);
//                    } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("F")) {
//                        ivTop.setImageResource(R.mipmap.ic_series_w_f);
//                    } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("M")) {
//                        ivTop.setImageResource(R.mipmap.ic_series_w_m);
//                    } else if (MyCommandManager.DEVICENAME.substring(0, 1).equals("W")) {
//                        ivTop.setImageResource(R.mipmap.ic_series_w_w);
//                    }
//                }
//            } else {
//                ivTop.setImageResource(R.mipmap.img_wirte_f6);
//            }
//        }
//    }
//
//
//    /**
//     * 链接状态返回
//     *
//     * @param state 0 :未连接  1:正在连接  2 :已连接
//     */
//    @Override
//    public void b15p_Connection_State(int state) {
//        if (getActivity() != null && !getActivity().isFinishing()) {
//
//            switch (state) {
//                case 0:
//                    Log.d(TAG, "--B15P--未连接");
//                    if (b30ConnectStateTv != null)
//                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
//                    if (sycnStute != null)
//                        sycnStute.setText(getResources().getString(R.string.disconnted));
//                    //未连接----自动扫描去链接
//                    b15PContentState.bleSeachDevices();
//                    break;
//                case 1:
//                    Log.d(TAG, "--B15P--正在链接");
//                    if (b30ConnectStateTv != null)
//                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
//                    if (sycnStute != null)
//                        sycnStute.setText(getResources().getString(R.string.disconnted));
//                    break;
//                case 2:
//                    Log.d(TAG, "--B15P--已连接");
//
//
//                    if (b30ConnectStateTv != null)
//                        b30ConnectStateTv.setText(getResources().getString(R.string.connted));
//                    if (sycnStute != null)
//                        sycnStute.setText(getResources().getString(R.string.connted));
//
//                    int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 0);
//                    if (param > 0) {
//                        //设置每次会来电电池电量
//                        showBatterStute(param);
//                    }
//
//                    //设置每次回主界面，返回数据不清空的
//                    clearDataStyle(0);
//
//
//                    Log.d(TAG, "--是否是第一次链接   " + MyApp.b15pIsFirstConntent);
//                    if (MyApp.b15pIsFirstConntent) {
//                        MyApp.b15pIsFirstConntent = false;//第一次链接同步后改变第一次链接之后的状态
//
//                        setImageType();
//                        // 第一次链接自动获取数据
//                        // 先获根据系统语言设置语言------之后去获取电池电量
//                        mHandler.sendEmptyMessageDelayed(0x01, 5000);
//                    } else {
//
//                        long currentTime = System.currentTimeMillis() / 1000;
//                        //保存的时间
//                        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getmContext(), "saveDate", currentTime + "");
//                        long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
//                        Log.e(TAG, "不是第一次链接--- 有 " + diffTime + "分钟没同步了");
//                        if (diffTime > 5) {// 大于2分钟没更新再取数据
//                            mHandler.sendEmptyMessageDelayed(0x01, 100);
//                        } else {
//                            /**
//                             *
//                             * 如果不是第一次同步每次回来主动从数据库中获取一次
//                             * sycnDataListenterStep
//                             * sycnDataListenterSleep
//                             * sycnDataListenterHeart
//                             *
//                             * Dev.L4UI_PageDATA_PEDO
//                             * Dev.L4UI_PageDATA_SLEEP
//                             * Dev.L4UI_PageDATA_HEARTRATE
//                             * @param type
//                             */
//                            Log.e(TAG, "不是第一次同步每次回来主动从数据库中获取一次");
//                            mHandler.sendEmptyMessage(0x00);
//                        }
//                    }
//                    break;
//            }
//
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unRegisterListenter();
//
//        L4M.SetResultListener(null);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (b30ProgressBar != null) {
//            b30ProgressBar.removeAnimator();
//        }
//        if (unbinder != null) unbinder.unbind();
//    }
//
//
//    /**
//     * 显示同步状态的提
//     *
//     * @param isShow
//     */
//    void setSysTextStute(boolean isShow) {
//        if (sycnStute != null) {
//            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
//                if (isShow) {
//                    sycnStute.setVisibility(View.VISIBLE);
//                    sycnStute.setText(getResources().getString(R.string.syncy_data));
//                } else {
//                    sycnStute.setVisibility(View.GONE);
//                }
//            } else {
//                sycnStute.setVisibility(View.VISIBLE);
//                sycnStute.setText(getResources().getString(R.string.disconnted));
//            }
//
//        }
//    }
//
//
//    void getLanguage() {
//        L4M.SetResultListener(mBTResultListenr);
//        String localelLanguage = Locale.getDefault().getLanguage();
//
//        String param = (String) SharedPreferencesUtils.getParam(getmContext(), "Languages", "EN");
//
//        Log.e(TAG, "----------localelLanguage=" + localelLanguage + "   " + param);
//        if (!WatchUtils.isEmpty(param)) {
//            if (!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {
//                Log.e(TAG, "======   " + localelLanguage + "    设置中文 ");
//                L4Command.LanguageZH();//中文
//            } else {
//                Log.e(TAG, "======   " + localelLanguage + "    设置英文 ");
//                L4Command.LanguageEN();//英文
//            }
//        }
//    }
//
//    L4M.BTResultListenr mBTResultListenr = new L4M.BTResultListenr() {
//        @Override
//        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
//
//            if (TypeInfo.equals(L4M.SetLanguage) && StrData.equals(L4M.OK)) {
//                if (StrData.equals("OK")) {
//                    String param = (String) SharedPreferencesUtils.getParam(getmContext(), "Languages", "EN");
//                    SharedPreferencesUtils.setParam(MyApp.getContext(), "Languages", (!WatchUtils.isEmpty(param) && param.equals("ZH")) ? "ZH" : "EN");
//                }
//            }
//        }
//    };
//
//
//    /**
//     * 地一步
//     * 获取设备电池电量
//     */
//    void getBatter() {
//
//        setSysTextStute(true);
//
//
//        int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 0);
//        if (param > 0) {
//            //设置每次会来电电池电量
//            showBatterStute(param);
//        }
//        mHandler.sendEmptyMessage(0x02);
////   L4Command.BatLevel(btResultListenr);
//    }
//
//
//    L4M.BTResultListenr btResultListenr = new L4M.BTResultListenr() {
//        @Override
//        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
//            final String tTypeInfo = TypeInfo;
//            final String TempStr = StrData;
//            final Object TempObj = DataObj;
//            //Log.d(TAG, "电池电量    获取有返回了" + tTypeInfo + "  " + TempStr + "  " + TempObj);
//
//            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
//                Log.e(TAG, "--------------==  获取电量超时 ~~~~~~~~~~~~~~ 跳过去执行下一个指令");
////                mHandler.sendEmptyMessageDelayed(0x02, 100);
//                mHandler.sendEmptyMessageDelayed(0xd9, 200);
//                return;
//            }
//
//            if (tTypeInfo.equals(L4M.GetBatLevel) && TempStr.equals(L4M.Data)) {
//                BracltBatLevel.BatLevel myBatLevel = (BracltBatLevel.BatLevel) TempObj;
//                int batlevel = myBatLevel.batlevel;
//                SharedPreferencesUtils.setParam(getmContext(), Commont.BATTERNUMBER, batlevel);
//                Log.e(TAG, "==  获取到的电量  " + batlevel);
//
//
//                int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 0);
//                if (param > 0) {
//                    //设置每次会来电电池电量
//                    showBatterStute(param);
//                }
//
////                mHandler.sendEmptyMessageDelayed(0x02, 100);
//                mHandler.sendEmptyMessageDelayed(0xd9, 200);
//            }
//
//        }
//    };
//
//    /**
//     * 注册
//     */
//    void registerListenter() {
//        //同步进度
//        Dev.SetUpdateUiListener(Dev.L4UI_PageDATA_HEALTH, myUpDateUiCb);
//        Dev.EnUpdateUiListener(myUpDateUiCb, 1);
//    }
//
//
//    /**
//     * 反注册
//     */
//    void unRegisterListenter() {
//        Dev.EnUpdateUiListener(myUpDateUiCb, 0);//同步进度
//    }
//
//
////
////    L4M.BTResultToDBListenr btResultToDBListenr = new L4M.BTResultToDBListenr() {
////        @Override
////        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
////            Log.d(TAG, "==TypeInfo:" + TypeInfo + "  StrData:" + StrData);
////            //接收健康数据后处理....
////            //TypeInfo 类型
////            //HEART_NOW         --当前测量的心率值
////            //BLDPRESS_NOW      --当前测量的血压值
////            //HEART_HISTORY     --历史测量的心率值
////            //BLDPRESS_HISTORY  --历史测量的血压值
////
////            //PEDO_DAY          --天记步数据
////            //PEDO_TIME_HISTORY --时间段记步数据
////
////            //SLEEP_DAY         --天睡眠数据
////            //SLEEP_TIME_HISTORY--天睡眠时间段数
////
////            /**
////             * 0_今天 1_昨天 2_前天
////             */
////            String t = WatchUtils.obtainFormatDate(0);
////            String y = WatchUtils.obtainFormatDate(1);
////            String q = WatchUtils.obtainFormatDate(2);
////            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////
////            if (WatchUtils.isEmpty(mac)) return;
////            if (TypeInfo.equals("PEDO_DAY")) {
////                Log.e(TAG, "==== 步数日 " + StrData);
////                //[,2019-04-22,2697]
////                mHandler.sendEmptyMessageDelayed(0x04, 2000);//获取睡眠
////            }
////            if (TypeInfo.equals("PEDO_TIME_HISTORY")) {
////                //[,2019-04-19 20:00:00,58]
////                Log.e(TAG, "==== 日步数详细 " + StrData);
////
////                String[] split = StrData.substring(2, StrData.length() - 1).split(",");
////                if (split[0].substring(0, 10).equals(t)
////                        || split[0].substring(0, 10).equals(y)
////                        || split[0].substring(0, 10).equals(q)) {
////
////                    if (!WatchUtils.isEmpty(mac))
////                        b15PDBCommont.saveStepToDB(mac, split[0], Integer.valueOf(split[1]));
////                }
////
////            }
////
////            if (TypeInfo.equals("SLEEP_DAY")) {
////                Log.e(TAG, "==== 睡眠日 " + StrData);
////                mHandler.sendEmptyMessageDelayed(0x05, 2000);//获取心率
////            }
////            if (TypeInfo.equals("SLEEP_TIME_HISTORY")) {
////                //[,2019-04-22 03:29:00,1]
////                Log.e(TAG, "==== 日睡眠详细 " + StrData);
////                String[] split = StrData.substring(2, StrData.length() - 1).split(",");
////                if (split[0].substring(0, 10).equals(t)
////                        || split[0].substring(0, 10).equals(y)
////                        || split[0].substring(0, 10).equals(q)
////                        || split[0].substring(0, 10).equals(WatchUtils.obtainAroundDate(q, true))) {
////
////                    if (!WatchUtils.isEmpty(mac))
////                        b15PDBCommont.saveSleepToDB(mac, split[0], split[1]);
////                }
////
////            }
////
////            if (TypeInfo.equals("HEART_HISTORY")) {
////                //[,1999-11-30 00:00:00,0]
////                Log.e(TAG, "==== 心率详细 " + StrData);
////                String[] split = StrData.substring(2, StrData.length() - 1).split(",");
////                if (split[0].substring(0, 10).equals(t)
////                        || split[0].substring(0, 10).equals(y)
////                        || split[0].substring(0, 10).equals(q)) {
////
////                    if (!WatchUtils.isEmpty(mac))
////                        b15PDBCommont.saveHeartToDB(mac, split[0], Integer.valueOf(split[1]));
////                    if (b30HomeSwipeRefreshLayout != null && b30HomeSwipeRefreshLayout.isShown())
////                        b30HomeSwipeRefreshLayout.finishRefresh();
////                    setSysTextStute(false);
////                }
////            }
////
////            if (TypeInfo.equals("BLDPRESS_HISTORY")) {
////                Log.e(TAG, "==== 血压详细 " + StrData);
////                String[] split = StrData.substring(2, StrData.length() - 1).split(",");
////                if (split[0].substring(0, 10).equals(t)
////                        || split[0].substring(0, 10).equals(y)
////                        || split[0].substring(0, 10).equals(q)) {
////
////                    if (!WatchUtils.isEmpty(mac))
////                        b15PDBCommont.saveBloopToDB(mac, split[0], Integer.valueOf(split[1]));
////                }
////
////            }
////
////
////        }
////    };
//
//    int type = 0;
//    List<String> typSync = new ArrayList<>();
//    Dev.UpdateUiListenerImpl myUpDateUiCb = new Dev.UpdateUiListenerImpl() {
//        @Override
//        public void UpdateUi(int ParaA, String StrData) {
//            int TempPara = ParaA;
//            String TempStrData = StrData;
//
//
//            Log.e(TAG, "L4UI_DATA_SyncProgress =" + TempStrData + "====" + TempPara);
//
//            if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("1")) {
//                typSync.clear();
//            } else if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("100")) {
//
//                typSync.add("100");
//                if (typSync.size() >= 2) {
//                    type++;
//                    switch (type) {
//                        case 1:
//                            mHandler.removeMessages(0x77);//取消 77 步数超时等待处理
//
//                            Log.e("L4UI_DATA_SyncProgress", "步数获取完成------去获取睡眠");
//                            mHandler.sendEmptyMessageDelayed(0x04, 500);
//                            break;
//                        case 2:
//                            mHandler.removeMessages(0x88);//取消 88 睡眠超时处理
//
//                            Log.e("L4UI_DATA_SyncProgress", "睡眠获取完成------去获取心率");
//                            mHandler.sendEmptyMessageDelayed(0x05, 500);
//
////                            mHandler.sendEmptyMessageDelayed(0x55, 15 * 1000);
//                            break;
//                        case 3:
//                            mHandler.removeMessages(0x99);//取消 99 心率超时处理
//
//                            Log.e("L4UI_DATA_SyncProgress", "心率获取完成------去获取所有数据显示");
//                            mHandler.sendEmptyMessageDelayed(0x00, 100);
//                            break;
//                    }
//                }
//            } else if (TempPara == Dev.L4UI_DATA_SyncProgress && TempStrData.equals("-100")) {
//
//            }
//        }
//    };
//
//    String mac = (String) SharedPreferencesUtils.readObject(getmContext(), Commont.BLEMAC);
//    Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            mac = (String) SharedPreferencesUtils.readObject(getmContext(), Commont.BLEMAC);
//            switch (message.what) {
//                case 0xd9://获取电量完成了 --- 下来获取语言
//                    getLanguage();
//                    break;
//                case 0x55:
//                    /**
//                     * 同步完成上传数据
//                     */
//                    startUploadDBService();
//                    break;
//                /**
//                 * 步数  睡眠  心率 的超时处理
//                 */
//                case 0x77://步数12秒等待超时
//                    if (mHandler != null) {
//                        mHandler.removeMessages(0x77);//删除超时等待
//                        Log.e("L4UI_DATA_SyncProgress", "步数获取超时------去获取睡眠");
//
//                        /**
//                         * 从数据库中读步数
//                         */
////                        mHandler.sendEmptyMessage(0x11);
//                        mHandler.sendEmptyMessage(0x00);
//
//                        mHandler.sendEmptyMessageDelayed(0x04, 500);//读取设备中的下一个睡眠数据
//                    }
//                    break;
//                case 0x88://睡眠12秒等待超时
//                    if (mHandler != null) {
//                        mHandler.removeMessages(0x88);//删除超时等待
//                        Log.e("L4UI_DATA_SyncProgress", "睡眠获取超时------去获取心率");
//
//                        /**
//                         * 从数据库中读睡眠
//                         */
////                        mHandler.sendEmptyMessage(0x12);
//                        mHandler.sendEmptyMessage(0x00);
//
//                        mHandler.sendEmptyMessageDelayed(0x05, 500);//读取设备中的下一个心率数据
//                    }
//                    break;
//                case 0x99://心率12秒等待超时
//                    if (mHandler != null) {
//                        mHandler.removeMessages(0x99);//删除超时等待
//                        //mHandler.sendEmptyMessage(0x00);//获取数据库中的心率数据
//
//                        Log.e("L4UI_DATA_SyncProgress", "心率获取超时------去获取数据库");
//                        /**
//                         * 从数据库中读心率
//                         */
////                        mHandler.sendEmptyMessage(0x13);
//                        mHandler.sendEmptyMessage(0x00);
//
//                    }
//                    break;
//                case 0x00:
//                    if (mHandler != null) {
//
//                        //分别读取数据空中的数据
//                        mHandler.sendEmptyMessageDelayed(0x11, 0);
//                        mHandler.sendEmptyMessageDelayed(0x12, 0);
//                        mHandler.sendEmptyMessageDelayed(0x13, 0);
//                    }
//                    break;
//                /**
//                 * 从设备获取数据
//                 */
//                case 0x01:// 先获根据系统语言设置语言
//                    Log.d(TAG, "手动刷新 ----- 先获根据系统语言设置语言");
//                    getBatter();
//                    break;
//                case 0x02://同步数据
//                    //链接成功了
//                    if (MyCommandManager.DEVICENAME != null) {
//                        if (getActivity() != null && !getActivity().isFinishing()) {
//                            if (b30HomeSwipeRefreshLayout != null)
//                                b30HomeSwipeRefreshLayout.setEnableRefresh(true);
//                        }
//
//                        int bt = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 0);
//                        showBatterStute(bt);
//                        getBleDevicesDatas();
//                    }
//                    break;
//                case 0x03://获取步数
//                    mHandler.sendEmptyMessageDelayed(0x77, 12 * 1000);//77 步数6秒超时处理
//                    L4Command.GetPedo1();
////                    L4Command.CommPedoTime(currDay, 3000);
//                    //获取步数+睡眠
////                    L4Command.YsnALLData();
//                    break;
//                case 0x04://获取睡眠
//                    mHandler.sendEmptyMessageDelayed(0x88, 12 * 1000);//88 睡眠超时处理
//                    L4Command.GetSleep1();     //睡眠
////                    L4Command.CommSleepTime(currDay,5000);
//                    break;
//                case 0x05://获取心率
//                    mHandler.sendEmptyMessageDelayed(0x99, 12 * 1000);//99 心率超时处理
//                    L4Command.GetHeart1();
//                    break;
//                case 0x11:
//                    if (!WatchUtils.isEmpty(mac)) {
////                        Log.e(TAG, "  mac  " + mac);
//
//                        String[] Allsteps = {"all_step", mac, WatchUtils.obtainFormatDate(currDay)};
//                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
//
//                            @Override
//                            public void updataAllStepDataToUIListenter(int integer) {
//                                defaultSteps = integer;
//                                if (getActivity() != null && !getActivity().isFinishing()) {
//                                    if (b30ProgressBar != null) {
//                                        b30ProgressBar.setMaxValue(goalStep);
//                                        b30ProgressBar.setValue(defaultSteps);
//                                        SharedPreferencesUtils.setParam(MyApp.getContext(), "ALL_STEP_VALUE", defaultSteps);
//                                    }
//                                }
//
//                            }
//                        }).execute(Allsteps);
//
//                        String[] steps = {"step", mac, WatchUtils.obtainFormatDate(currDay)};
//                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
////                            @Override
////                            public void updataDataToUIListenter(List<Object> stepAllDatas) {
////
////                                Log.e(TAG, stepAllDatas.toString());
////                                //setStep(stepAllDatas);
////
////                            }
//
//                            @Override
//                            public void updataStepDataToUIListenter(List<Integer> ts) {
//
////                                defaultSteps = 0;
////                                for (int i = 0; i < ts.size(); i++) {
////                                    defaultSteps += ts.get(i);
////                                }
////
////                                if (b30ProgressBar != null) {
////                                    b30ProgressBar.setMaxValue(goalStep);
////                                    b30ProgressBar.setValue(defaultSteps);
////                                }
//                                showSportStepData(ts);
//                            }
//                        }).execute(steps);
//
//
//                    }
//                    break;
//                case 0x12:
//                    if (!WatchUtils.isEmpty(mac)) {
//
//                        String[] steps = {"sleep", mac, WatchUtils.obtainFormatDate(currDay)};
//                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<W30S_SleepDataItem>() {
//
//                            @Override
//                            public void updataSleepDataToUIListenter(List<W30S_SleepDataItem> sleepDataList) {
//
//                                if (getActivity() != null && !getActivity().isFinishing()) {
//
//
//                                    String dateStr = WatchUtils.obtainFormatDate(currDay);
//                                    if (!sleepDataList.isEmpty() && sleepDataList.size() > 3) {
////                        ALLTIME = 0;//睡眠总时间
//                                        AWAKE = 0;//清醒的次数
////                        DEEP = 0;//深睡
////                        SHALLOW = 0;//浅睡
//                                        startSleepTime = "";
//                                        endSleepTime = "";
//                                        StringBuilder strSleep = new StringBuilder("");
//
//                                        //是否第一次次进入睡眠状态
//                                        //目的是为了处理睡眠入睡之前大部分的清醒睡眠
//                                        boolean isIntoSleep = false;
//
//                                        for (int i = 0; i < sleepDataList.size() - 1; i++) {
//                                            String startTime = null;
//                                            String startTimeLater = null;
//                                            String sleep_type = null;
////                                Log.e(TAG, "===睡眠=C= " + sleepDataList.get(i).getStartTime() + "======" +
////                                        (sleepDataList.get(i).getSleep_type().equals("0") ? "===清醒" : sleepDataList.get(i).getSleep_type().equals("1") ? "---->>>浅睡" : "===>>深睡"));
//
//                                            if (i == 0)
//                                                startSleepTime = sleepDataList.get(i).getStartTime();
//                                            endSleepTime = sleepDataList.get(i).getStartTime();
//                                            if (i >= (sleepDataList.size() - 1)) {
//                                                startTime = sleepDataList.get(i).getStartTime();
//                                                startTimeLater = sleepDataList.get(i).getStartTime();
//                                                sleep_type = sleepDataList.get(i).getSleep_type();
//                                            } else {
//                                                startTime = sleepDataList.get(i).getStartTime();
//                                                startTimeLater = sleepDataList.get(i + 1).getStartTime();
//                                                sleep_type = sleepDataList.get(i).getSleep_type();
//                                            }
//                                            String[] starSplit = startTime.split("[:]");
//                                            String[] endSplit = startTimeLater.split("[:]");
//
//                                            int startHour = Integer.valueOf(!TextUtils.isEmpty(starSplit[0].replace(",", "")) ? starSplit[0].replace(",", "") : "0");
//                                            int endHour = Integer.valueOf(!TextUtils.isEmpty(endSplit[0].replace(",", "")) ? endSplit[0].replace(",", "") : "0");
//
//                                            int startMin = Integer.valueOf(!TextUtils.isEmpty(starSplit[1].replace(",", "")) ? starSplit[1].replace(",", "") : "0");
//                                            int endMin = (Integer.valueOf(!TextUtils.isEmpty(endSplit[1].replace(",", "")) ? endSplit[1].replace(",", "") : "0"));
//                                            if (startHour > endHour) {
//                                                endHour = endHour + 24;
//                                            }
//                                            int all_m = (endHour - startHour) * 60 + (endMin - startMin);
//                                            //B15P元数据   清醒  0    浅睡 1   深睡 2
//                                            //图标绘制时    浅睡  0    深睡 1   清醒 2
//                                            if (sleep_type.equals("0")) {
//                                                if (isIntoSleep) {
//                                                    AWAKE++;
////                                Log.e(TAG, "====0===" + all_m);
//                                                    for (int j = 1; j <= all_m; j++) {
//                                                        strSleep.append("2");
//                                                    }
//                                                }
//                                            } else if (sleep_type.equals("1")) {
//                                                isIntoSleep = true;
//                                                //潜水
////                                SHALLOW = SHALLOW + all_m;
////                                ALLTIME = ALLTIME + all_m;
////                                Log.e(TAG, "====1===" + all_m);
//                                                for (int j = 1; j <= all_m; j++) {
//                                                    strSleep.append("0");
//                                                }
//                                            } else if (sleep_type.equals("2")) {
//                                                isIntoSleep = true;
//                                                //深水
////                                DEEP = DEEP + all_m;
////                                ALLTIME = ALLTIME + all_m;
////                                Log.e(TAG, "====2===" + all_m);
//                                                for (int j = 1; j <= all_m; j++) {
//                                                    strSleep.append("1");
//                                                }
//                                            }
//
//                                        }
//
////                            Log.e(TAG, "===睡眠=D=" + strSleep.toString());
//                                        if (!TextUtils.isEmpty(strSleep)) {
//
////                                Log.e(TAG, strSleep.toString().length() + " 睡眠 \n" + strSleep.toString());
//                                            /**
//                                             * 显示睡眠图标
//                                             */
//                                            showSleepData(strSleep.toString(), dateStr);
//                                        }
//                                    } else {
//                                        if (b30CusSleepView != null) {
//
//                                            b30CusSleepView.setSeekBarShow(false);
//                                            b30CusSleepView.setSleepList(new ArrayList<Integer>());
//                                        }
//                                    }
//                                }
//                            }
//
//                        }).execute(steps);
//
//                    }
//
//                    break;
//                case 0x13:
//
//                    if (!WatchUtils.isEmpty(mac)) {
//                        String[] steps = {"heart", mac, WatchUtils.obtainFormatDate(currDay)};
//                        new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
////                            @Override
////                            public void updataDataToUIListenter(List<Object> heartAllDatas) {
//////                                 setHeart(heartAllDatas);
////                            }
//
//                            @Override
//                            public void updataHeartDataToUIListenter(List<Integer> ts, String latelyValues) {
//
//                                if (getActivity() != null && !getActivity().isFinishing()) {
//                                    /**
//                                     * 去上传数据
//                                     */
//                                    mHandler.sendEmptyMessageDelayed(0x55, 1000);
//
//                                    if (ts != null && !ts.isEmpty()) {
//
//                                        //设置最近心率----这里拿的是最后一条心率的数据和时间
//                                        if (!WatchUtils.isEmpty(latelyValues)) {
//                                            String[] split = latelyValues.split("[#]");
//                                            if (!WatchUtils.isEmpty(split[0])) {
//                                                lastTimeTv.setText(split[0]);
//                                            }
//                                            if (!WatchUtils.isEmpty(split[1])) {
//                                                b30HeartValueTv.setText("  " + split[1] + "bpm");
//                                            }
//                                        }
//                                        if (b30CusHeartView != null)
//                                            b30CusHeartView.setRateDataList(ts);
//                                    } else {
//                                        if (b30CusHeartView != null)
//                                            b30CusHeartView.setRateDataList(heartList);
//                                    }
//
//
//                                    if (b30HomeSwipeRefreshLayout != null)
//                                        b30HomeSwipeRefreshLayout.finishRefresh();
//
//                                    L4Command.BatLevel(btResultListenr);
////                                    getLanguage();
//                                    setSysTextStute(false);
//                                }
//
//                            }
//                        }).execute(steps);
//
//                    }
//
//                    break;
//            }
//            return false;
//        }
//    });
//
//
//    /**
//     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
//     */
//    private int currDay = 0;
//
//    @OnClick({R.id.b30SportChartLin1, R.id.b30BarChart, R.id.b30CusHeartLin,
//            R.id.b30CusBloadLin, R.id.b30MeaureHeartImg, R.id.b30MeaureBloadImg,
//            R.id.b30SleepLin, R.id.homeTodayTv, R.id.homeYestTodayTv, R.id.homeBeYestdayTv,
//            R.id.battery_watchRecordShareImg})//, R.id.b36WomenStatusLin, R.id.b36WomenPrivacyImg})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.b30SportChartLin1: // 运动数据详情
//            case R.id.b30BarChart: // 运动数据详情
//                B15PStepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
//                break;
//            case R.id.b30CusHeartLin:   //心率详情
//                B15PHeartDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
//                break;
//            case R.id.b30CusBloadLin:   //血压详情
//                //B30BloadDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
//                break;
//            case R.id.b30MeaureHeartImg:    //手动测量心率
//                startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class)
//                        .putExtra("what", "b15p"));
//                break;
//            case R.id.b30MeaureBloadImg:    //手动测量血压
////                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
//                startActivity(new Intent(getActivity(), B15PManualMeaureBloadActivity.class));
//                break;
//            case R.id.b30SleepLin:      //睡眠详情
//                B15PSleepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate
//                        (currDay));
//                break;
//            case R.id.homeTodayTv:  //今天
//                clearDataStyle(0);
//                mHandler.sendEmptyMessage(0x00);
//                break;
//            case R.id.homeYestTodayTv:  //昨天
//                clearDataStyle(1);
//                mHandler.sendEmptyMessage(0x00);
//                break;
//            case R.id.homeBeYestdayTv:  //前天
//                clearDataStyle(2);
//                mHandler.sendEmptyMessage(0x00);
//                break;
//            case R.id.battery_watchRecordShareImg:  //分享----现在是数据面板
//                if (getActivity() == null || getActivity().isFinishing())
//                    return;
////                Intent intent = new Intent(getActivity(), SharePosterActivity.class);
////                intent.putExtra("is18i", "B36");
////                intent.putExtra("stepNum", defaultSteps + "");
////                startActivity(intent);
//                startActivity(new Intent(getActivity(), CommentDataActivity.class));
//                break;
////            case R.id.b36WomenStatusLin:    //女性功能
////                startActivity(new Intent(getActivity(), WomenDetailActivity.class));
////                break;
////            case R.id.b36WomenPrivacyImg:       //隐私是否开启
////                if (womenPrivacy) {       //开启了隐私
////                    womenPrivacy = false;
////                    b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec));
////
////                } else {  //关闭了隐私 ic_b36_home_woman_rec
////                    womenPrivacy = true;
////                    b36WomenPrivacyImg.setBackground(getResources().getDrawable(R.mipmap.ic_b36_home_woman_rec_pri));
////                }
////                SharedPreferencesUtils.setParam(getActivity(), "b36_women_privacy", womenPrivacy);
////                tunrOnOrOffPrivacy(womenPrivacy);
////
////                break;
//        }
//    }
//
//
//    /**
//     * 显示电量
//     *
//     * @param batteryLevel
//     */
//    void showBatterStute(int batteryLevel) {
//        try {
//            if (batteryTopImg == null) return;
//            if (batteryLevel >= 0 && batteryLevel < 25) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
//            } else if (batteryLevel >= 25 && batteryLevel < 50) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
//            } else if (batteryLevel >= 50 && batteryLevel < 75) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
//            } else if (batteryLevel >= 75 && batteryLevel <= 100) {
//                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
//            }
//            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel + "%");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @BindView(R.id.homeTodayImg)
//    ImageView homeTodayImg;
//    @BindView(R.id.homeYestdayImg)
//    ImageView homeYestdayImg;
//    @BindView(R.id.homeBeYestdayImg)
//    ImageView homeBeYestdayImg;
//
//    /**
//     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
//     */
//    private void clearDataStyle(final int code) {
//        if (code == currDay) return;// 防重复点击
//        if (homeTodayImg != null) homeTodayImg.setVisibility(View.INVISIBLE);
//        if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.INVISIBLE);
//        if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.INVISIBLE);
//        switch (code) {
//            case 0: //今天
//                if (homeTodayImg != null) homeTodayImg.setVisibility(View.VISIBLE);
//                break;
//            case 1: //昨天
//                if (homeYestdayImg != null) homeYestdayImg.setVisibility(View.VISIBLE);
//                break;
//            case 2: //前天
//                if (homeBeYestdayImg != null) homeBeYestdayImg.setVisibility(View.VISIBLE);
//                break;
//        }
//        currDay = code;
//        /**
//         * 在设置设置改变建厅里面获取数据是的方法的时间
//         * sycnDataListenterStep
//         * sycnDataListenterSleep
//         * sycnDataListenterHeart
//         */
//        String date = WatchUtils.obtainFormatDate(currDay);
////        sycnDataListenterStep.setDateStr(date);
////        sycnDataListenterSleep.setDateStr(date);
////        sycnDataListenterHeart.setDateStr(date);
//        //handler.sendEmptyMessageDelayed(0x11, 200);
//
//        mHandler.sendEmptyMessage(0x00);
//    }
//
//
//    /**
//     * 第二步
//     * 链接成功了读取设备数据
//     */
//    void getBleDevicesDatas() {
//        if (MyCommandManager.DEVICENAME == null) {
//            if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.finishRefresh();
//            return;
//        }
//        try {
//            //保存的时间
//            SharedPreferencesUtils.setParam(MyApp.getContext(), "saveDate", (System.currentTimeMillis() / 1000) + "");
//            mHandler.sendEmptyMessage(0x03);//获取步数
//        } catch (Error e) {
//        }
//    }
//
//
//    //开始上传本地缓存的数据
//    private void startUploadDBService() {
//        CommDBManager.getCommDBManager().startUploadDbService(MyApp.getContext());
//    }
//
//
//    //运动图表最大步数
//    @BindView(R.id.b30SportMaxNumTv)
//    TextView b30SportMaxNumTv;
//    //运动图表
//    @BindView(R.id.b30BarChart)
//    BarChart b30BarChart;
//    @BindView(R.id.b30StartEndTimeTv)
//    TextView b30StartEndTimeTv;
//
//    //睡眠图表
//    @BindView(R.id.b30CusSleepView)
//    B15PCusSleepView b30CusSleepView;
//
//    //心率图标
//    @BindView(R.id.b30HomeHeartChart)
//    B30CusHeartView b30CusHeartView;
//    //最后一次时间
//    @BindView(R.id.lastTimeTv)
//    TextView lastTimeTv;
//    //心率值
//    @BindView(R.id.b30HeartValueTv)
//    TextView b30HeartValueTv;
//
//    //步数数据
//    List<BarEntry> b30ChartList;
//    //用于计算最大步数
//    private List<Integer> tmpIntegerList;
//    //设置步数图表的临时数据
//    private List<BarEntry> tmpB30StepList;
//    /**
//     * 展示睡眠图表
//     */
//    //展示睡眠数据的集合
//    private List<Integer> sleepList;
//
//    //展示心率数据的集合
//    List<Integer> heartList;
//    List<Integer> heartListNew;
//    //    int ALLTIME = 0;//睡眠总时间
//    int AWAKE = 0;//清醒的次数
//    //    int DEEP = 0;//深睡
////    int SHALLOW = 0;//浅睡
//    String startSleepTime = "00:00";//入睡时间
//    String endSleepTime = "00:00";//起床时间
//
//
//    /**
//     * 展示步数的图表，计算数据
//     */
//    private void showSportStepData(List<Integer> sportData) {
//
//
//        if (getActivity() == null || getActivity().isFinishing()) return;
//        if (b30ChartList != null) b30ChartList.clear();
//        if (tmpIntegerList != null) tmpIntegerList.clear();
//        if (tmpB30StepList != null) tmpB30StepList.clear();
//
//        if (sportData != null && !sportData.isEmpty()) {
//            List<Map<String, Integer>> listMap = new ArrayList<>();
//            int k = 0;
//            for (int i = 0; i < 24; i++) {
//                Map<String, Integer> map = new HashMap<>();
//                int time = i * 60;
//                map.put("time", time);
//                map.put("val", sportData.get(i));
//                listMap.add(map);
//            }
//
////            Log.e(TAG, "  " + listMap.toString());
//            for (int i = 0; i < listMap.size(); i++) {
//                Map<String, Integer> tmpMap = listMap.get(i);
//                if (tmpB30StepList != null) tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
//                if (tmpIntegerList != null) tmpIntegerList.add(tmpMap.get("val"));
//            }
//            if (b30ChartList != null) b30ChartList.addAll(tmpB30StepList);
//            if (b30SportMaxNumTv != null)
//                b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
//            initBarChart(b30ChartList);
//            if (b30BarChart != null) b30BarChart.invalidate();
//        } else {
//            initBarChart(b30ChartList);
//            if (b30BarChart != null) {
//                b30BarChart.setNoDataTextColor(Color.WHITE);
//                b30BarChart.invalidate();
//            }
//        }
//
//    }
//
//    //步数图表展示
//    @SuppressWarnings("deprecation")
//    private void initBarChart(List<BarEntry> pointbar) {
//        BarDataSet barDataSet = new BarDataSet(pointbar, "");
//        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
//        //barDataSet.setColor(Color.parseColor("#fa8072"));//设置第一组数据颜色
//        barDataSet.setColor(Color.parseColor("#88d785"));//设置第一组数据颜色
//
//        if (b30BarChart == null) return;
//        Legend mLegend = b30BarChart.getLegend();
//        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
//        mLegend.setFormSize(15.0f);// 字体
//        mLegend.setTextColor(Color.BLUE);// 颜色
//        mLegend.setEnabled(false);
//
//        ArrayList<IBarDataSet> threeBarData = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
//        threeBarData.add(barDataSet);
//
//        BarData bardata = new BarData(threeBarData);
//        bardata.setBarWidth(0.5f);  //设置柱子宽度
//
//        b30BarChart.setData(bardata);
//        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
//        b30BarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
//        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
//
//        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
//        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
//        b30BarChart.getXAxis().setEnabled(false);
//        b30BarChart.setPinchZoom(false);
//        b30BarChart.setScaleEnabled(false);
//        b30BarChart.setTouchEnabled(false);
//
//        b30BarChart.getDescription().setEnabled(false);
//
//        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
//        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
//        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
//        b30BarChart.getAxisLeft().setEnabled(false);
//
//        b30BarChart.getXAxis().setSpaceMax(0.5f);
//        b30BarChart.animateXY(1000, 2000);//设置动画
//    }
//
//
//    /**
//     * 展示睡眠图表
//     */
//    private void showSleepData(String sleepLin, String dateStr) {
//        if (getActivity() == null || getActivity().isFinishing()) return;
//        //图标绘制时    浅睡  0    深睡 1   清醒 2
//        int xing = WatchUtils.countStr(sleepLin, '2');
//        int qian = WatchUtils.countStr(sleepLin, '0');
//        int shen = WatchUtils.countStr(sleepLin, '1');
//        int allSleep = shen + qian;
//        if (b30StartEndTimeTv != null)
//            b30StartEndTimeTv.setText(startSleepTime + "-" + endSleepTime);
////        Log.e(TAG, "===  " + xing + "  " + qian + "   " + shen + "  " + allSleep);
//        DecimalFormat formater = new DecimalFormat("#0.0");
//
//        formater.setRoundingMode(RoundingMode.HALF_UP);
//        String S = formater.format((double) shen / 60.0);
//
//
//        formater.setRoundingMode(RoundingMode.FLOOR);
//        String Q = formater.format((double) qian / 60.0);
//        String Z = formater.format((double) allSleep / 60.0);
//
//        Log.e(TAG,
//                "睡眠段总时长： " + sleepLin.length() + "分钟 == " + formater.format((double) sleepLin.length() / 60.0) +
//                        "小时  实际睡眠时长： " + allSleep + "分钟 == " + Z +
//                        "小时  深睡： " + shen + "分钟 == " + S +
//                        "小时  浅睡： " + qian + "分钟 == " + Q +
//                        "小时  清醒次数： " + AWAKE + " 次");
//
//        //保存睡眠数据
//        //SleepData sleepData = mp.getValue();
//        //清醒时长=总的睡眠时长-深睡时长-清醒时长
//        //int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
//        /**
//         * 保存睡眠数据
//         *
//         * @param bleName   蓝牙名字
//         * @param bleMac    蓝牙mac地址
//         * @param dateStr   日期
//         * @param deep      深睡时长
//         * @param low       浅睡时长
//         * @param sober     清醒时长
//         * @param allSleep  所有睡眠时间
//         * @param sleeptime 入睡时间
//         * @param waketime  清醒时间
//         * @param wakeCount 清醒次数
//         */
//        CommDBManager.getCommDBManager().saveCommSleepDbData((!WatchUtils.isEmpty( MyCommandManager.DEVICENAME)? MyCommandManager.DEVICENAME:"B15P"),
//                WatchUtils.getSherpBleMac(MyApp.getContext()),
//                dateStr,
//                shen,
//                qian,
//                xing,
//                allSleep + xing,
//                startSleepTime,
//                endSleepTime,
//                AWAKE);
////      Log.e(TAG, "====" + AWAKE + "   " + SHALLOW + "  " + DEEP);
//
//
//        sleepList.clear();
//        if (!WatchUtils.isEmpty(sleepLin)) {
//            if (WatchUtils.isEmpty(sleepLin) || sleepLin.length() < 2) {
//                if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
//                return;
//            }
//            for (int i = 0; i < sleepLin.length(); i++) {
//                if (i <= sleepLin.length() - 1) {
//                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
//                    sleepList.add(subStr);
//                }
//            }
//            sleepList.add(0, 2);
//            sleepList.add(0);
//            sleepList.add(2);
//        } else {
//            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
//        }
//        if (sleepList != null && !sleepList.isEmpty()) {
//            if (b30CusSleepView != null) {
//                b30CusSleepView.setSeekBarShow(false);
//                b30CusSleepView.setSleepList(sleepList);
//            }
//        } else {
//            if (b30CusSleepView != null) {
//                b30CusSleepView.setSeekBarShow(false);
//                b30CusSleepView.setSleepList(new ArrayList<Integer>());
//            }
//        }
//    }
//
//
//}
