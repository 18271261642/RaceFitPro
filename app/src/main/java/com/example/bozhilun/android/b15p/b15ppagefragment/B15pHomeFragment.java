package com.example.bozhilun.android.b15p.b15ppagefragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
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
import com.example.bozhilun.android.b15p.activity.B15PDeviceActivity;
import com.example.bozhilun.android.b15p.activity.B15PHeartDetailActivity;
import com.example.bozhilun.android.b15p.activity.B15PManualMeaureBloadActivity;
import com.example.bozhilun.android.b15p.activity.B15PSleepDetailActivity;
import com.example.bozhilun.android.b15p.activity.B15PStepDetailActivity;
import com.example.bozhilun.android.b15p.b15pdb.B15PSleepBean;
import com.example.bozhilun.android.b15p.common.B15PContentState;
import com.example.bozhilun.android.b15p.interfaces.ConntentStuteListenter;
import com.example.bozhilun.android.b15p.interfaces.FindDBListenter;
import com.example.bozhilun.android.b15p.interfaces.SycnDataToDBListenter;
import com.example.bozhilun.android.b30.ManualMeaureHeartActivity;
import com.example.bozhilun.android.b30.b30view.B15PCusSleepView;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.CommentDataActivity;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.BracltBatLevel;
import com.tjdL4.tjdmain.contr.L4Command;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class B15pHomeFragment extends LazyFragment
        implements ConntentStuteListenter, OnRefreshListener, SycnDataToDBListenter.SycnDevDataProgress {
    //, SycnChangeUIListenter {
    public final String TAG = "B15pHomeFragment";


    View rootView;
    Unbinder unbinder;
    //血压图标    血压测试、心率测试 按钮显示和隐藏
    @BindView(R.id.b30HomeB30Lin)
    LinearLayout b30HomeB30Lin;
    //电量
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30connectStateTv)
    TextView b30ConnectStateTv;
    @BindView(R.id.b30HomeSwipeRefreshLayout)
    SmartRefreshLayout b30HomeSwipeRefreshLayout;


    //电量图片
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView batteryWatchRecordShareImg;
    @BindView(R.id.watch_record_titleLin)
    LinearLayout watchRecordTitleLin;
    //波浪形进度条
    @BindView(R.id.b30ProgressBar)
    WaveProgress b30ProgressBar;
    //目标步数显示
    @BindView(R.id.b30GoalStepTv)
    TextView b30GoalStepTv;
    //日期
    @BindView(R.id.b30_top_dateTv)
    TextView b30TopDateTv;
    //设备图标
    @BindView(R.id.iv_top)
    ImageView ivTop;
    @BindView(R.id.homeFastStatusTv)
    TextView homeFastStatusTv;


    //链接状态返回
    private B15PContentState b15PContentState;
    //默认步数
    int defaultSteps = 0;
    /**
     * 目标步数
     */
    int goalStep;


    //运动图表最大步数
    @BindView(R.id.sycn_stute)
    TextView sycnStute;
    @BindView(R.id.homeFastLin)
    ConstraintLayout homeFastLin;
    private Context mContext;
    private String saveBleName, saveBleMac;
    private SycnDataToDBListenter sycnDataToDBListenter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        b15PContentState = B15PContentState.getInstance();
        //设置连接状态返回的监听
        b15PContentState.setB15PContentState(this);
        super.onCreate(savedInstanceState);

        try {
            /**
             * 设置监听----同步数据保存本地数据库
             */

            sycnDataToDBListenter = SycnDataToDBListenter.getSycnDataToDBListenter();
            L4M.SetResultToDBListener(sycnDataToDBListenter);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        //b15p 有血压   血压测试和心率测试，所以显示
//        b30HomeB30Lin.setVisibility(View.VISIBLE);
//        batteryWatchRecordShareImg.setVisibility(View.GONE);//原来的分享---》》现在是数据面板


        goalStep = (int) SharedPreferencesUtils.getParam(getmContext(), "b30Goal", 8000);
        saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        saveBleMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_b15p_home_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        init(rootView);

        initData();
        return rootView;
    }

    private Handler b15Handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (getActivity() != null && !getActivity().isFinishing()) {
                switch (message.what) {
                    case 0x00:
                        //同步语言
                        getLanguage();
                        break;
                    case 0x01://上传数据
                        //开始上传本地缓存的数据
                        if (Commont.isDebug) Log.e(TAG, " ------- 开始上传本地缓存的数据");
                        CommDBManager.getCommDBManager().startUploadDbService(getmContext());
                        break;
                    case 0x03://同步一些基本配置
                        if (mBTBattResultListenr != null) L4Command.BatLevel(mBTBattResultListenr);
                        /**
                         * 去上传数据
                         */
                        b15Handler.sendEmptyMessageDelayed(0x01, 1000);
                        //同步时间保持一下，下次计算超过5 分钟再次从设备同步
                        SharedPreferencesUtils.setParam(getmContext(), "saveDate", (System.currentTimeMillis() / 1000) + "");
                        //更新完成的状态
                        setSysTextStute(false, getResources().getString(R.string.confirm_compelte));

                        if (b30HomeSwipeRefreshLayout != null)
                            b30HomeSwipeRefreshLayout.finishRefresh();
                        break;
                    case 0x11://获取设备数据----------》》》 此处数据是数据库里面已经保存的数据
                        if (b15Handler.hasMessages(0x11)) b15Handler.removeMessages(0x11);
                        if (b15Handler.hasMessages(0xaa)) b15Handler.removeMessages(0xaa);
                        setSysTextStute(true, getResources().getString(R.string.syncy_data));
                        if (!WatchUtils.isEmpty(saveBleMac)) {
                            String[] Allsteps = {"all_step", saveBleMac, WatchUtils.obtainFormatDate(currDay)};
                            new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
                                @Override
                                public void updataAllStepDataToUIListenter(int integer) {
                                    defaultSteps = integer;
                                    SharedPreferencesUtils.setParam(MyApp.getContext(), "ALL_STEP_VALUE", defaultSteps);
                                    //更新步数进度圆环
                                    if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
                                        b30ProgressBar.setMaxValue(goalStep);
                                        b30ProgressBar.setValue(defaultSteps);
                                        b30ProgressBar.invalidate();
                                    }

                                    b15Handler.sendEmptyMessageDelayed(0x22, 100);
                                }

                                @Override
                                public void updataAllStepNullDataToUIListenter() {
                                    defaultSteps = 0;
                                    //更新步数进度圆环
                                    if (getActivity() != null && !getActivity().isFinishing() && b30ProgressBar != null) {
                                        b30ProgressBar.setMaxValue(goalStep);
                                        b30ProgressBar.setValue(defaultSteps);
                                        b30ProgressBar.invalidate();
                                    }
                                    b15Handler.sendEmptyMessageDelayed(0x22, 100);
                                    super.updataAllStepNullDataToUIListenter();
                                }
                            }).execute(Allsteps);
                        }
                        break;
                    case 0x22:
                        if (!WatchUtils.isEmpty(saveBleMac)) {
                            String[] steps = {"step", saveBleMac, WatchUtils.obtainFormatDate(currDay)};
                            new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
                                @Override
                                public void updataStepDataToUIListenter(List<Integer> ts) {
                                    if (getActivity() != null && !getActivity().isFinishing()) {
                                        showSportStepData(ts);

                                        b15Handler.sendEmptyMessageDelayed(0x33, 100);
                                    }

                                }
                            }).execute(steps);
                        }
                        break;
                    case 0x33:
                        if (!WatchUtils.isEmpty(saveBleMac)) {
                            String[] steps = {"heart", saveBleMac, WatchUtils.obtainFormatDate(currDay)};
                            new FindDBListenter(new FindDBListenter.ChangeDBListenter<Integer>() {
                                @Override
                                public void updataHeartDataToUIListenter(List<Integer> ts, String latelyValues) {
                                    if (getActivity() != null && !getActivity().isFinishing()) {
                                        if (ts != null && !ts.isEmpty()) {
                                            //设置最近心率----这里拿的是最后一条心率的数据和时间
                                            if (!WatchUtils.isEmpty(latelyValues)) {
                                                String[] split = latelyValues.split("[#]");
                                                if (!WatchUtils.isEmpty(split[0])) {
                                                    if (lastTimeTv != null)
                                                        lastTimeTv.setText(split[0]);
                                                }
                                                if (!WatchUtils.isEmpty(split[1])) {
                                                    if (b30HeartValueTv != null)
                                                        b30HeartValueTv.setText("  " + split[1] + "bpm");
                                                }
                                            }
                                            if (b30CusHeartView != null)
                                                b30CusHeartView.setRateDataList(ts);
                                        } else {
                                            if (b30CusHeartView != null)
                                                b30CusHeartView.setRateDataList(heartList);
                                        }

                                        b15Handler.sendEmptyMessageDelayed(0x44, 100);

                                    }

                                }
                            }).execute(steps);
                        }
                        break;
                    case 0x44:
                        /**
                         * 最后一次同步睡眠数据时候，有睡眠数据是，顺便模拟了SPO和HRV
                         */
                        if (!WatchUtils.isEmpty(saveBleMac)) {
                            String[] steps = {"sleep", saveBleMac, WatchUtils.obtainFormatDate(currDay)};
                            new FindDBListenter(new FindDBListenter.ChangeDBListenter<W30S_SleepDataItem>() {

                                @Override
                                public void updataSleepDataToUIListenter(List<W30S_SleepDataItem> sleepDataList) {

                                    if (getActivity() != null && !getActivity().isFinishing()) {
                                        String dateStr = WatchUtils.obtainFormatDate(currDay);
                                        boolean isStart = true;
                                        if (!sleepDataList.isEmpty() && sleepDataList.size() > 2) {

                                            AWAKE = 0;//清醒的次数
                                            //ALLTIME = 0;//睡眠总时间
                                            //DEEP = 0;//深睡
                                            //SHALLOW = 0;//浅睡
                                            startSleepTime = "";
                                            endSleepTime = "";
                                            StringBuilder strSleep = new StringBuilder("");

                                            //是否第一次次进入睡眠状态
                                            //目的是为了处理睡眠入睡之前大部分的清醒睡眠
                                            boolean isIntoSleep = false;

                                            for (int i = 0; i < sleepDataList.size() - 1; i++) {
                                                String startTime = null;
                                                String startTimeLater = null;
                                                String sleep_type = null;
                                                //if (Commont.isDebug)Log.e(TAG, "===睡眠=C= " + sleepDataList.get(i).getStartTime() + "======" +
                                                //       (sleepDataList.get(i).getSleep_type().equals("0") ? "===清醒" : sleepDataList.get(i).getSleep_type().equals("1") ? "---->>>浅睡" : "===>>深睡"));

                                                if (i == 0)
                                                    startSleepTime = sleepDataList.get(i).getStartTime();

                                                if (i >= (sleepDataList.size() - 1)) {
                                                    startTime = sleepDataList.get(i).getStartTime();
                                                    startTimeLater = sleepDataList.get(i).getStartTime();
                                                    sleep_type = sleepDataList.get(i).getSleep_type();
                                                } else {
                                                    startTime = sleepDataList.get(i).getStartTime();
                                                    startTimeLater = sleepDataList.get(i + 1).getStartTime();
                                                    sleep_type = sleepDataList.get(i).getSleep_type();
                                                }
                                                String[] starSplit = startTime.split("[:]");
                                                String[] endSplit = startTimeLater.split("[:]");

                                                int startHour = Integer.valueOf(!TextUtils.isEmpty(starSplit[0].replace(",", "")) ? starSplit[0].replace(",", "") : "0");
                                                int endHour = Integer.valueOf(!TextUtils.isEmpty(endSplit[0].replace(",", "")) ? endSplit[0].replace(",", "") : "0");

                                                int startMin = Integer.valueOf(!TextUtils.isEmpty(starSplit[1].replace(",", "")) ? starSplit[1].replace(",", "") : "0");
                                                int endMin = (Integer.valueOf(!TextUtils.isEmpty(endSplit[1].replace(",", "")) ? endSplit[1].replace(",", "") : "0"));
                                                if (startHour > endHour) {
                                                    endHour = endHour + 24;
                                                }
                                                int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                                                //B15P元数据   清醒  0    浅睡 1   深睡 2
                                                //图标绘制时    浅睡  0    深睡 1   清醒 2
                                                if (sleep_type.equals("0")) {
                                                    endSleepTime = sleepDataList.get(i).getStartTime();
                                                    if (isIntoSleep) {
                                                        /**
                                                         * 去除返回连续一起的清醒，出现多次累积清醒情况
                                                         */
                                                        if (!sleepDataList.get(i + 1).getSleep_type().equals("0")) {
                                                            AWAKE++;
                                                        }
                                                        for (int j = 1; j <= all_m; j++) {
                                                            strSleep.append("2");
                                                        }
                                                    }
                                                } else if (sleep_type.equals("1")) {
                                                    if (isStart) {
                                                        isStart = false;
                                                        startSleepTime = sleepDataList.get(i).getStartTime();
                                                    }
                                                    endSleepTime = sleepDataList.get(i + 1).getStartTime();
                                                    isIntoSleep = true;
                                                    //潜水
                                                    //SHALLOW = SHALLOW + all_m;
                                                    //ALLTIME = ALLTIME + all_m;
                                                    if (Commont.isDebug)
                                                        Log.e(TAG, "====1===" + all_m);
                                                    for (int j = 1; j <= all_m; j++) {
                                                        strSleep.append("0");
                                                    }
                                                } else if (sleep_type.equals("2")) {
                                                    if (isStart) {
                                                        isStart = false;
                                                        startSleepTime = sleepDataList.get(i).getStartTime();
                                                    }
                                                    endSleepTime = sleepDataList.get(i + 1).getStartTime();
                                                    isIntoSleep = true;
                                                    //深水
                                                    //DEEP = DEEP + all_m;
                                                    //ALLTIME = ALLTIME + all_m;
                                                    if (Commont.isDebug)
                                                        Log.e(TAG, "====2===" + all_m);
                                                    for (int j = 1; j <= all_m; j++) {
                                                        strSleep.append("1");
                                                    }
                                                }

                                            }
                                            //if (Commont.isDebug)Log.e(TAG, "===睡眠=D=" + strSleep.toString());
                                            if (!TextUtils.isEmpty(strSleep)) {

                                                //if (Commont.isDebug)Log.e(TAG, strSleep.toString().length() + " 睡眠 \n" + strSleep.toString());
                                                /**
                                                 * 显示睡眠图标
                                                 */
                                                showSleepData(strSleep.toString(), dateStr);
                                            }
                                        } else {
                                            if (b30CusSleepView != null) {

                                                b30CusSleepView.setSeekBarShow(false);
                                                b30CusSleepView.setSleepList(new ArrayList<Integer>());
                                            }
                                        }

                                        //结尾的一些配置获取
                                        b15Handler.sendEmptyMessageDelayed(0x03, 100);
                                    }
                                }

                            }).execute(steps);
                        }
                        break;
                    //----------------------------------------------另外一种方法获取睡眠数据
                    case 0x55://步数
                        L4Command.GetPedo1();
                        b15Handler.sendEmptyMessageDelayed(0x66, 10 * 1000);//步数获取超时就获取睡眠
                        break;
                    case 0x66://睡眠
                        if (b15Handler.hasMessages(0x66)) b15Handler.removeMessages(0x66);
                        L4Command.GetSleep1();     //睡眠
                        b15Handler.sendEmptyMessageDelayed(0x77, 10 * 1000);//睡眠获取超时就获取心率
                        break;
                    case 0x77://心率
                        if (b15Handler.hasMessages(0x77)) b15Handler.removeMessages(0x77);
                        L4Command.GetHeart1();
                        if (b15Handler.hasMessages(0x11)) b15Handler.removeMessages(0x11);
                        b15Handler.sendEmptyMessageDelayed(0x11, 12 * 1000);//心率获取超时就获取现在有的数据
                        break;
                    case 0xaa://处理自动刷新或者刷新超时
                        b15Handler.sendEmptyMessage(0x11);
                        break;
                    //------------------------------------------第三种拿取数据的方法
                    case 0xa1://获取步数今天数据
                        if (b15Handler.hasMessages(0xaa)) b15Handler.removeMessages(0xaa);
                        b15Handler.sendEmptyMessageDelayed(0xaa, 25 * 1000);//心率获取超时就获取现在有的数据
                        L4Command.CommPedoTime(0, 3000);
                        break;
                    case 0xa2://获取步数昨天数据
                        L4Command.CommPedoTime(1, 3000);
                        break;
                    case 0xb1://获取睡眠今天数据
                        L4Command.CommSleepTime(0, 3000);
                        break;
                    case 0xb2://获取睡眠昨天数据
                        L4Command.CommSleepTime(1, 3000);
                        break;
                    case 0xcc://获取心率今天数据
                        L4Command.GetHeart1();
                        break;
                }
            }
            return false;
        }
    });


    /**
     * 初始
     *
     * @param rootView
     */
    @SuppressLint("SetTextI18n")
    private void init(View rootView) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            b30TopDateTv.setText(WatchUtils.getCurrentDate());
            b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));
            //设置刷新监听
            if (b30HomeSwipeRefreshLayout != null)
                b30HomeSwipeRefreshLayout.setOnRefreshListener(this);
//            //步数进度圆环UI同步
            if (b30ProgressBar != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        b30ProgressBar.setMaxValue(goalStep);
                        b30ProgressBar.setValue(defaultSteps);
                    }
                });
            }


//            /**
//             * 测试获取数据入口
//             */
//            b30TopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    //startActivity(new Intent(getActivity(), B15PIntervalActivity.class));
//                    return true;
//                }
//            });

        }

    }


    /**
     * 同步获取数据有三种方式，刷新一次改变一次方式，为了避免获取不到数据
     * <p>
     * 手动刷新
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (Commont.isDebug)
            Log.e(TAG, "手动刷新 ----- getBleDevicesDatas()   读取设备数据  " + Commont.RefreshType);
        //设置每次回主界面，返回数据不清空的
        clearDataStyle(0, false);
//        MyApp.b15pIsFirstConntent = true;//第一次链接同步后改变第一次链接之后的状态

        if (Commont.RefreshType == 0) {
            Commont.RefreshType++;
            if (sycnDataToDBListenter == null)
                sycnDataToDBListenter = SycnDataToDBListenter.getSycnDataToDBListenter();
            sycnDataToDBListenter.IsonRefresh = true;
            b15Handler.sendEmptyMessageDelayed(0xa1, 100);
        } else if (Commont.RefreshType == 1) {
            Commont.RefreshType++;
            typeprogress = 0;
            b15Handler.sendEmptyMessage(0x55);
        } else {
            Commont.RefreshType = 0;
            L4M.SysnALLData();
        }
        if (b15Handler.hasMessages(0xaa)) b15Handler.removeMessages(0xaa);
        b15Handler.sendEmptyMessageDelayed(0xaa, 20 * 1000);
    }


    /**
     * 顶部图标显示
     */
    void showTopImage() {
        saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if (!WatchUtils.isEmpty(saveBleName)) {
            if (saveBleName.equals("F6")) {
                ivTop.setImageResource(R.mipmap.img_wirte_f6);
            } else if (saveBleName.equals("B25")) {
                ivTop.setImageResource(R.mipmap.ic_b52_home);
            } else if (saveBleName.equals("B15P")) {
                ivTop.setImageResource(R.mipmap.ic_b15p_home);
            } else {
                ivTop.setImageResource(R.mipmap.ic_b52_home);
            }
        }
    }

    /**
     * 显示电量
     *
     * @param batteryLevel
     */
    void showBatterStute(int batteryLevel) {
        if (getActivity() == null || getActivity().isFinishing()) return;
        try {
            /**
             * 1.电量显示为0-15为空格，15-25为一格电，25-50为两格电，50-75为三格电，75-100显示满电
             */
            if (batteryTopImg == null) return;
            if (batteryLevel > 0 && batteryLevel <= 15) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
            } else if (batteryLevel > 15 && batteryLevel <= 25) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (batteryLevel > 25 && batteryLevel <= 50) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (batteryLevel > 50 && batteryLevel <= 75) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (batteryLevel > 75 && batteryLevel <= 100) {
                batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            if (batteryPowerTv != null) batteryPowerTv.setText("" + batteryLevel + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context getmContext() {
        return mContext == null ? MyApp.getContext() : mContext;
    }


    private void initData() {
        sleepList = new ArrayList<>();
        heartList = new ArrayList<>();
        heartListNew = new ArrayList<>();

        b30ChartList = new ArrayList<>();
        tmpB30StepList = new ArrayList<>();
        tmpIntegerList = new ArrayList<>();
    }


    @Override
    public void onStart() {
        if (sycnDataToDBListenter == null)
            sycnDataToDBListenter = SycnDataToDBListenter.getSycnDataToDBListenter();
        if (sycnDataToDBListenter != null) sycnDataToDBListenter.setmSycnDevDataProgress(this);
        if (b15PContentState != null) b15PContentState.isConnect();
        registerListenter();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            setSysTextStute(true, getResources().getString(R.string.syncy_data));
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                long currentTime = System.currentTimeMillis() / 1000;
                //保存的时间
                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "saveDate", currentTime + "");
                long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
                if (Commont.isDebug)
                    Log.e(TAG, "onFragmentVisibleChange: autoRefresh()   " + diffTime);
                showTopImage();
                //设置每次回主界面，返回数据不清空的
                clearDataStyle(0, false);

                //是搜索进来的
                if (WatchConstants.isScanConn) {
                    if (Commont.isDebug)
                        Log.e(TAG, "onFragmentVisibleChange: autoRefresh()   是搜索进来的 " + diffTime);
                    WatchConstants.isScanConn = false;
                    //读取设备数据
                    //b15Handler.sendEmptyMessage(0x11);
                    if (b30HomeSwipeRefreshLayout != null) b30HomeSwipeRefreshLayout.autoRefresh();
                } else {  //不是搜索进来的
                    if (Commont.isDebug)
                        Log.e(TAG, "onFragmentVisibleChange: autoRefresh()   不是搜索进来的 " + diffTime);
                    if (diffTime > 5) {// 大于五分钟没更新再取数据
                        if (Commont.isDebug)
                            Log.e(TAG, "onFragmentVisibleChange: autoRefresh()   不是搜索进来的 " + diffTime);
                        //读取设备数据
                        //b15Handler.sendEmptyMessage(0x11);
                        if (b30HomeSwipeRefreshLayout != null) {
                            Commont.RefreshType = 1;
                            b30HomeSwipeRefreshLayout.autoRefresh();
                        } else {
                            Commont.RefreshType = 1;
                            b15Handler.sendEmptyMessageDelayed(0x55, 100);
                        }
                    } else {
                        b15Handler.sendEmptyMessage(0x11);
                    }
                }
            }
        }
    }


    /**
     * 链接状态返回
     *
     * @param state 0 :未连接  1:正在连接  2 :已连接
     */
    @Override
    public void b15p_Connection_State(int state) {
        switch (state) {
            case 0:
                if (Commont.isDebug) Log.d(TAG, "--B15P--未连接");
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.disconnted));
                }
                //未连接 设备的时候  判断曾经是否有过连接历史，并且非手动断开的----自动扫描去链接
                if (!WatchUtils.isEmpty(saveBleName) && !WatchUtils.isEmpty(saveBleMac)) {
                    if (Commont.isDebug) Log.d(TAG, "--B15P--有链接历史，并且非手动断开----- 去扫描连接");
                    b15PContentState.bleSeachDevices();
                }
                break;
            case 1:
                if (Commont.isDebug) Log.d(TAG, "--B15P--正在链接");
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.disconnted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.disconnted));
                }
                break;
            case 2:
                if (Commont.isDebug) Log.d(TAG, "--B15P--已连接");
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (b30ConnectStateTv != null)
                        b30ConnectStateTv.setText(getResources().getString(R.string.connted));
                    if (sycnStute != null)
                        sycnStute.setText(getResources().getString(R.string.connted));
                    int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 50);
                    //设置每次会来电电池电量
                    showBatterStute(param);
                }
                if (MyApp.b15pIsFirstConntent || Commont.HasOTA) {
                    MyApp.b15pIsFirstConntent = false;//第一次链接同步后改变第一次链接之后的状态
                    Commont.HasOTA = false;
                    showTopImage();
                    typeprogress = 0;
//                    L4M.SysnALLData();
//                    b15Handler.sendEmptyMessageDelayed(0x55, 100);
                    if (b30HomeSwipeRefreshLayout != null) {
                        Commont.RefreshType = 1;
                        b30HomeSwipeRefreshLayout.autoRefresh();
                    } else {
                        Commont.RefreshType = 1;
                        b15Handler.sendEmptyMessageDelayed(0x55, 100);
                    }
                    setSysTextStute(true, getResources().getString(R.string.syncy_data));
                }
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterListenter();
        if (sycnDataToDBListenter != null) sycnDataToDBListenter = null;
        //L4M.SetResultListener(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (b30ProgressBar != null) {
            b30ProgressBar.removeAnimator();
        }
        if (unbinder != null) unbinder.unbind();
    }


    /**
     * 显示同步状态的提
     *
     * @param isShow
     */
    void setSysTextStute(boolean isShow, String type) {
        if (sycnStute != null) {
            if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                if (isShow) {
                    sycnStute.setVisibility(View.VISIBLE);
                    if (homeFastStatusTv != null)
                        homeFastStatusTv.setText(getResources().getString(R.string.syncy_data));
                    if (homeFastLin != null)
                        homeFastLin.setBackgroundColor(Color.parseColor("#E63F52"));//红
//                    if (tipText != null) tipText.setVisibility(View.VISIBLE);
//                    if (moreSetting != null)
//                        moreSetting.setBackgroundColor(Color.parseColor("#E63F52"));//红
                } else {
                    sycnStute.setVisibility(View.GONE);
                    if (homeFastStatusTv != null)
                        homeFastStatusTv.setText(getResources().getString(R.string.more_opera));
                    if (homeFastLin != null)
                        homeFastLin.setBackgroundColor(Color.parseColor("#55C1E9"));
//                    if (tipText != null) tipText.setVisibility(View.GONE);
//                    if (moreSetting != null)
//                        moreSetting.setBackgroundColor(Color.parseColor("#e54c988c"));//绿
                }
                sycnStute.setText(type);
                //sycnStute.setText(getResources().getString(R.string.syncy_data));
            } else {
                sycnStute.setVisibility(View.VISIBLE);
//                if (moreSetting != null)
//                    moreSetting.setBackgroundColor(Color.parseColor("#E63F52"));//红
//                if (tipText != null) tipText.setVisibility(View.VISIBLE);
                sycnStute.setText(getResources().getString(R.string.disconnted));
                if (homeFastStatusTv != null)
                    homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
                if (homeFastLin != null)
                    homeFastLin.setBackgroundColor(Color.parseColor("#E63F52"));//红
            }

        }
    }


    /**
     * 获取并设置语言
     */
    void getLanguage() {
        String localelLanguage = Locale.getDefault().getLanguage();
        String param = (String) SharedPreferencesUtils.getParam(getmContext(), "Languages", "EN");
        if (Commont.isDebug)
            Log.e(TAG, "----------localelLanguage=" + localelLanguage + "   " + param);
        if (!WatchUtils.isEmpty(param)) {
            if (!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {
                if (Commont.isDebug) Log.e(TAG, "======   " + localelLanguage + "    设置中文 ");
                L4Command.LanguageZH();//中文
            } else {
                if (Commont.isDebug) Log.e(TAG, "======   " + localelLanguage + "    设置英文 ");
                L4Command.LanguageEN();//英文
            }
        }
    }


    /**
     * 设置点量回调
     */
    L4M.BTResultListenr mBTBattResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                if (Commont.isDebug)
                    Log.e(TAG, "--------------==  电池电量超时 ~~~~~~~~~~~~~~ 跳过去执行下一个指令");

                b15Handler.sendEmptyMessageDelayed(0x00, 250); //同步语言
                return;
            }
            if (TypeInfo.equals(L4M.GetBatLevel) && StrData.equals(L4M.Data)) {
                BracltBatLevel.BatLevel myBatLevel = (BracltBatLevel.BatLevel) DataObj;
                int batlevel = myBatLevel.batlevel;
                SharedPreferencesUtils.setParam(getmContext(), Commont.BATTERNUMBER, batlevel);
                if (Commont.isDebug) Log.e(TAG, "==  获取到的电量  " + batlevel);
                int param = (int) SharedPreferencesUtils.getParam(getmContext(), Commont.BATTERNUMBER, 50);
                //设置每次会来电电池电量
                showBatterStute(param);

                b15Handler.sendEmptyMessageDelayed(0x00, 250); //同步语言
            }
        }
    };

    /**
     * 设置语言回调
     */
    L4M.BTResultListenr mBTResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                if (Commont.isDebug)
                    Log.e(TAG, "--------------==  设置语言超时 ~~~~~~~~~~~~~~ 跳过去执行下一个指令");
                return;
            }
            if (TypeInfo.equals(L4M.SetLanguage) && StrData.equals(L4M.OK)) {
                if (StrData.equals("OK")) {
                    String param = (String) SharedPreferencesUtils.getParam(getmContext(), "Languages", "EN");
                    SharedPreferencesUtils.setParam(MyApp.getContext(), "Languages", (!WatchUtils.isEmpty(param) && param.equals("ZH")) ? "ZH" : "EN");
                }
            }
        }
    };


    /**
     * 注册
     */
    void registerListenter() {
        L4Command.BatLevel(mBTBattResultListenr);
        L4M.SetResultListener(mBTResultListenr);

        //同步进度
        Dev.SetUpdateUiListener(Dev.L4UI_PageDATA_HEALTH, myUpDateUiCb);
        Dev.EnUpdateUiListener(myUpDateUiCb, 1);
    }


    /**
     * 反注册
     */
    void unRegisterListenter() {
        Dev.EnUpdateUiListener(myUpDateUiCb, 0);//同步进度
    }


    int typeprogress = 0;
    List<String> typSync = new ArrayList<>();
    Dev.UpdateUiListenerImpl myUpDateUiCb = new Dev.UpdateUiListenerImpl() {
        @Override
        public void UpdateUi(int ParaA, String StrData) {
            if (Commont.isDebug) Log.e(TAG, "L4UI_DATA_SyncProgress =" + StrData + "====" + ParaA);
            if (Commont.RefreshType == 1) {
                if (ParaA == Dev.L4UI_DATA_SyncProgress && StrData.equals("1")) {
                    typSync.clear();
                } else if (ParaA == Dev.L4UI_DATA_SyncProgress && StrData.equals("100")) {
                    typSync.add("100");
                    if (typSync.size() >= 2) {
                        typeprogress++;
                        switch (typeprogress) {
                            case 1:
                                if (Commont.isDebug)
                                    Log.e("L4UI_DATA_SyncProgress", "步数获取完成------去获取睡眠");

                                b15Handler.sendEmptyMessageDelayed(0x66, 200);
                                break;
                            case 2:
                                if (Commont.isDebug)
                                    Log.e("L4UI_DATA_SyncProgress", "睡眠获取完成------去获取心率");
                                b15Handler.sendEmptyMessageDelayed(0x77, 200);
                                break;
                            case 3:
                                if (Commont.isDebug)
                                    Log.e("L4UI_DATA_SyncProgress", "心率获取完成------去获取所有数据显示");
                                b15Handler.sendEmptyMessageDelayed(0x11, 200);
                                break;
                        }
                    }
                }
            } else if (Commont.RefreshType == 2) {
                if (ParaA == Dev.L4UI_DATA_SyncProgress && StrData.equals("1")) {
                    typSync.clear();
                } else if (ParaA == Dev.L4UI_DATA_SyncProgress && StrData.equals("100")) {
                    typSync.add("100");
                    /**
                     * 去上传数据
                     */
                    b15Handler.sendEmptyMessageDelayed(0x11, 1000);
//                    if (typSync.size() >= 2) {
//                        /**
//                         * 去上传数据
//                         */
//                        b15Handler.sendEmptyMessageDelayed(0x11, 1000);
//                    }
                }
            }


        }
    };


    /**
     * 获取每个数据的进度和类型返回
     *
     * @param progress 进度
     * @param type     类型  PEDO_TIME_HISTORY（步数详细数据）
     * SLEEP_TIME_HISTORY（睡眠详细数据）
     * HEART_HISTORY_111(心率详细数据)
     */
    int datIndex = 0;

    @Override
    public void OnProgressListenter(int progress, String type) {
        if (sycnDataToDBListenter == null)
            sycnDataToDBListenter = SycnDataToDBListenter.getSycnDataToDBListenter();
        if (sycnDataToDBListenter.IsonRefresh) {
            if (progress == 100 || progress == 0) {
                switch (type) {
                    case "PEDO_TIME_HISTORY":
                        datIndex++;
                        if (datIndex == 1) {
                            Log.e(TAG, "--   类型 =--  " + type + "  ---   进度  " + progress + "    当天步数数据获取完成，获取昨天步数数据   " + datIndex);
                            b15Handler.sendEmptyMessageDelayed(0xa2, 100);//当天步数数据获取完成，获取昨天步数数据
                        } else if (datIndex == 2) {
                            Log.e(TAG, "--   类型 =--  " + type + "  ---   进度  " + progress + "    昨天步数数据获取完成，获取当天睡眠数据   " + datIndex);
                            datIndex = 0;
                            b15Handler.sendEmptyMessageDelayed(0xb1, 100);//昨天步数数据获取完成，获取当天睡眠数据
                        }
                        break;
                    case "SLEEP_TIME_HISTORY":
                        datIndex++;
                        if (datIndex == 1) {
                            Log.e(TAG, "--   类型 =--  " + type + "  ---   进度  " + progress + "   当天睡眠数据获取完成，获取昨天睡眠数据   " + datIndex);
                            b15Handler.sendEmptyMessageDelayed(0xb2, 100);//当天睡眠数据获取完成，获取昨天睡眠数据
                        } else if (datIndex == 2) {
                            Log.e(TAG, "--   类型 =--  " + type + "  ---   进度  " + progress + "    昨天睡眠数据获取完成，获取当天心率数据   " + datIndex);
                            datIndex = 0;
                            b15Handler.sendEmptyMessageDelayed(0xcc, 100);//昨天睡眠数据获取完成，获取当天心率数据
                        }
                        break;
                    case "HEART_HISTORY_111":
                        Log.e(TAG, "--   类型 =--  " + type + "  ---   进度  " + progress + "    心率数据获取完成，同步更新UI   " + datIndex);
                        datIndex = 0;
                        sycnDataToDBListenter.IsonRefresh = false;
                        b15Handler.sendEmptyMessageDelayed(0x11, 100);//昨天心率数据获取完成，同步更新UI
                        break;
                }
            }


        }

    }


    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private int currDay = 0;

    @OnClick({R.id.b30SportChartLin1, R.id.b30BarChart, R.id.b30CusHeartLin,
            R.id.b30CusBloadLin, R.id.b30MeaureHeartImg, R.id.b30MeaureBloadImg,
            R.id.b30SleepLin, R.id.homeTodayTv, R.id.homeYestTodayTv, R.id.homeBeYestdayTv,
            R.id.battery_watchRecordShareImg,
            R.id.homeFastLin})
//, R.id.b36WomenStatusLin, R.id.b36WomenPrivacyImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b30SportChartLin1: // 运动数据详情
            case R.id.b30BarChart: // 运动数据详情
                B15PStepDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusHeartLin:   //心率详情
                B15PHeartDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30CusBloadLin:   //血压详情
                //B30BloadDetailActivity.startAndParams(getActivity(), WatchUtils.obtainFormatDate(currDay));
                break;
            case R.id.b30MeaureHeartImg:    //手动测量心率
                startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class)
                        .putExtra("what", "b15p"));
                break;
            case R.id.b30MeaureBloadImg:    //手动测量血压
//                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                startActivity(new Intent(getActivity(), B15PManualMeaureBloadActivity.class));
                break;
            case R.id.b30SleepLin:      //睡眠详情
                B15PSleepDetailActivity.startAndParams(getmContext(), WatchUtils.obtainFormatDate
                        (currDay));
                break;
            case R.id.homeTodayTv:  //今天
                clearDataStyle(0, true);
                //mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.homeYestTodayTv:  //昨天
                clearDataStyle(1, true);
                //mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.homeBeYestdayTv:  //前天
                clearDataStyle(2, true);
                //mHandler.sendEmptyMessage(0x00);
                break;
            case R.id.battery_watchRecordShareImg:  //分享----现在是数据面板
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                startActivity(new Intent(getmContext(), CommentDataActivity.class));
                break;

            case R.id.homeFastLin://直接跳转到我的设备
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) {
                        getActivity().startActivity(new Intent(getActivity(), B15PDeviceActivity.class));
                    } else {
                        /**
                         *手动断开  b15p
                         */
                        if (!WatchUtils.isEmpty(L4M.GetConnectedMAC())) {
                            try {
                                if (L4M.Get_Connect_flag() == 2) Dev.RemoteDev_CloseManual();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (b15PContentState != null) b15PContentState.stopSeachDevices();//停止扫描
                        }

                        getActivity().startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }

                }

                break;
        }
    }

    @BindView(R.id.homeTodayImg)
    ImageView homeTodayImg;
    @BindView(R.id.homeYestdayImg)
    ImageView homeYestdayImg;
    @BindView(R.id.homeBeYestdayImg)
    ImageView homeBeYestdayImg;

    /**
     * 当前显示哪天的数据(0_今天 1_昨天 2_前天)
     */
    private void clearDataStyle(final int code, boolean getData) {
        if (code == currDay) return;// 防重复点击
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
        /**
         * 在设置设置改变建厅里面获取数据是的方法的时间
         * sycnDataListenterStep
         * sycnDataListenterSleep
         * sycnDataListenterHeart
         */
//        String date = WatchUtils.obtainFormatDate(currDay);
//        sycnDataListenterStep.setDateStr(date);
//        sycnDataListenterSleep.setDateStr(date);
//        sycnDataListenterHeart.setDateStr(date);
        //handler.sendEmptyMessageDelayed(0x11, 200);

        tableClear();
        setSysTextStute(true, getResources().getString(R.string.syncy_data));
        if (getData) b15Handler.sendEmptyMessage(0x11);
    }

    /**
     * 数据表清空
     */
    void tableClear() {
        showSportStepData(null);
        if (b30CusSleepView != null) {
            b30CusSleepView.setSeekBarShow(false);
            b30CusSleepView.setSleepList(new ArrayList<Integer>());
        }
        if (b30CusHeartView != null) b30CusHeartView.setRateDataList(null);
    }


    //运动图表最大步数
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    //运动图表
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.b30StartEndTimeTv)
    TextView b30StartEndTimeTv;

    //睡眠图表
    @BindView(R.id.b30CusSleepView)
    B15PCusSleepView b30CusSleepView;

    //心率图标
    @BindView(R.id.b30HomeHeartChart)
    B30CusHeartView b30CusHeartView;
    //最后一次时间
    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    //心率值
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;

    //步数数据
    List<BarEntry> b30ChartList;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;
    //设置步数图表的临时数据
    private List<BarEntry> tmpB30StepList;
    /**
     * 展示睡眠图表
     */
    //展示睡眠数据的集合
    private List<Integer> sleepList;

    //展示心率数据的集合
    List<Integer> heartList;
    List<Integer> heartListNew;
    //    int ALLTIME = 0;//睡眠总时间
    int AWAKE = 0;//清醒的次数
    //    int DEEP = 0;//深睡
//    int SHALLOW = 0;//浅睡
    String startSleepTime = "00:00";//入睡时间
    String endSleepTime = "00:00";//起床时间


    /**
     * 展示步数的图表，计算数据
     */
    private void showSportStepData(List<Integer> sportData) {


        if (getActivity() == null || getActivity().isFinishing()) return;
        if (b30ChartList != null) b30ChartList.clear();
        if (tmpIntegerList != null) tmpIntegerList.clear();
        if (tmpB30StepList != null) tmpB30StepList.clear();

        if (sportData != null && !sportData.isEmpty()) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 24; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 60;
                map.put("time", time);
                map.put("val", sportData.get(i));
                listMap.add(map);
            }

//           if (Commont.isDebug) Log.e(TAG, "  " + listMap.toString());
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> tmpMap = listMap.get(i);
                if (tmpB30StepList != null) tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
                if (tmpIntegerList != null) tmpIntegerList.add(tmpMap.get("val"));
            }
            if (b30ChartList != null) b30ChartList.addAll(tmpB30StepList);
            if (b30SportMaxNumTv != null)
                b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
            initBarChart(b30ChartList);
            if (b30BarChart != null) {
                b30BarChart.setNoDataTextColor(Color.WHITE);
                b30BarChart.invalidate();
            }
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


    /**
     * 展示睡眠图表
     */
    private void showSleepData(String sleepLin, String dateStr) {
        if (getActivity() == null || getActivity().isFinishing()) return;

        if (Commont.isDebug) Log.e(TAG, "======== 处理前的睡眠数据 " + sleepLin);
        /**
         *
         * 开始自定义处理睡眠数据
         * ----------------------------------------------
         */
        //----------------------------------------------
        StringBuilder stringBuilder = new StringBuilder(sleepLin);
        int addChartCount = 0;
        List<B15PSleepBean> b15PSleepBeanList = new ArrayList<>();
        for (int i = 0; i < (sleepLin.length() - 1); i++) {
            char charAtS = sleepLin.charAt(i);
            char charAtE = sleepLin.charAt(i + 1);
            if ((charAtS == '0' && charAtE == '1')
                    || (charAtS == '0' && charAtE == '2')
                    || (charAtS == '1' && charAtE == '0')
                    || (charAtS == '1' && charAtE == '2')
                    || (charAtS == '2' && charAtE == '0')
                    || (charAtS == '2' && charAtE == '1')) {
                addChartCount++;
                stringBuilder.insert(i + addChartCount, "P");
            }
        }
        if (Commont.isDebug) Log.e(TAG, "======== 处理后的睡眠数据 " + stringBuilder.toString());
        if (!WatchUtils.isEmpty(stringBuilder.toString())) {
            String[] split = stringBuilder.toString().split("[P]");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    if (Commont.isDebug)
                        Log.e(TAG, "======== 处理后的睡眠数据---分 " + i + "    " + split[i]);
                    String sleeps = split[i];
                    B15PSleepBean b15PSleepBean = new B15PSleepBean(sleeps.substring(0, 1), sleeps, sleeps.length());
                    b15PSleepBeanList.add(b15PSleepBean);
                }
            }
        }


        /**
         * 处理睡眠中出现 两段清醒中夹杂睡眠数据，并且中间的睡眠小于两段清醒的数据（后面的清醒前移，中间的睡眠后移动）
         */
        for (int i = 0; i < b15PSleepBeanList.size(); i++) {
            if (i > 1 && i < b15PSleepBeanList.size() - 1) {
                B15PSleepBean b15PSleepBeanA = b15PSleepBeanList.get(i - 1);
                B15PSleepBean b15PSleepBeanB = b15PSleepBeanList.get(i);
                B15PSleepBean b15PSleepBeanC = b15PSleepBeanList.get(i + 1);


                /**
                 * 判断处理，睡眠中出现两边清醒活动大于中间睡眠数据的
                 */
                if ((b15PSleepBeanB.getType().equals("0") || b15PSleepBeanB.getType().equals("1"))
                        && b15PSleepBeanA.getType().equals("2")
                        && b15PSleepBeanC.getType().equals("2")
                        && (b15PSleepBeanB.getCount() < b15PSleepBeanA.getCount() || b15PSleepBeanB.getCount() < b15PSleepBeanC.getCount())) {
                    //后面的一段多插入一下
                    b15PSleepBeanList.add(i, b15PSleepBeanC);
                    //在删除后面的清醒
                    b15PSleepBeanList.remove(i + 2);
                    if (Commont.isDebug) Log.e(TAG, "======== 处理睡眠数据---    满足处理条件  ");
                }
            }

        }


        if (Commont.isDebug) Log.e(TAG, "======== 处理数据前 " + b15PSleepBeanList.toString());
        /**
         * 处理第一次睡眠出现长时间清醒
         */
        // 浅睡  0    深睡 1   清醒 2
//        boolean isOneQingxing = true;//是否是第一次苏醒
        int isOneQingxing = 0;//是否是第一次苏醒
        for (int i = 0; i < b15PSleepBeanList.size(); i++) {
            if (isOneQingxing < 2 && b15PSleepBeanList.get(i).getType().equals("2") && b15PSleepBeanList.get(i).getCount() >= 15) {
//                isOneQingxing = false;
                isOneQingxing++;

                try {
                    /**
                     * 原始数据被动改变，起始时间需要重新计算一下
                     */
                    startSleepTime = getTime(startSleepTime, b15PSleepBeanList.get(i).getCount());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                b15PSleepBeanList.add(0, b15PSleepBeanList.get(i));
                b15PSleepBeanList.remove(i + 1);
                if (Commont.isDebug) Log.e(TAG, "======== 处理数据     满足第二次处理条件");
            }
        }
        if (Commont.isDebug) Log.e(TAG, "======== 处理数据后 " + b15PSleepBeanList.toString());


        /**
         * 睡眠数据从新拼接
         */
        StringBuilder strB = new StringBuilder("");
        AWAKE = 0;
        for (int i = 0; i < b15PSleepBeanList.size(); i++) {
            B15PSleepBean b15PSleepBean = b15PSleepBeanList.get(i);

            //在这里重新计算下清醒次数，因为原始数据已经被改变
            if (i != 0 && (i + 1) < b15PSleepBeanList.size()) {
                if (b15PSleepBean.getType().equals("2") && !b15PSleepBeanList.get(i + 1).getType().equals("2")) {
                    AWAKE++;
                }
            }

            strB.append(b15PSleepBeanList.get(i).getSleep());
        }


        if (Commont.isDebug)
            Log.e(TAG, "======== 处理后的睡眠数据 结束 - " + AWAKE + "   ==    " + strB.toString());
        //--------------------------------------------------------------------
        /**
         * ----------------------------------------------
         */

        //重新赋值睡眠
        sleepLin = strB.toString();


        /**
         * 去除前后面一直都是清醒的状态
         */
        if (Commont.isDebug) Log.e(TAG, " 睡眠     " + sleepLin);
        boolean isSleepS = true;
        boolean isSleepE = true;
        int strS = 0;//截取前面的位数
        int strE = sleepLin.length();//截取后面的位数

        //深睡、潜睡、清醒 每一个出现的次数
        int numberS = 0;
        int numberT = 0;
        int numberQ = 0;
        //B15P元数据   清醒  0    浅睡 1   深睡 2

        //图标绘制时    浅睡  0    深睡 1   清醒 2
        // 循环遍历每个字符，判断是否是字符 a ，如果是，累加次数
        for (int i = 0; i < sleepLin.length(); i++) {
            char charAt = sleepLin.charAt(i);
            char charAtE = sleepLin.charAt(sleepLin.length() - 1 - i);
            //睡眠前多余的清醒
            if (isSleepS) {
                if (charAt == '0') {
                    isSleepS = false;
                } else if (charAt == '1') {
                    isSleepS = false;
                } else if (charAt == '2') {
                    strS++;
                }
            }
            //睡眠后多余的清醒
            if (isSleepE) {
                if (charAtE == '0') {
                    isSleepE = false;
                } else if (charAtE == '1') {
                    isSleepE = false;
                } else if (charAtE == '2') {
                    strE--;
                }
            }

            // 获取每个字符，判断是否是字符a
            if (charAt == '0') {
                // 累加统计次数
                numberT++;
            } else if (charAt == '1') {
                // 累加统计次数
                numberS++;
            } else if (charAt == '2') {
                // 累加统计次数
                numberQ++;
            }
        }
        sleepLin = sleepLin.substring(strS, strE);
        if (Commont.isDebug) Log.e(TAG, " 睡眠2     " + sleepLin);

        int numberAllSleepTime = numberS + numberT;
        if (b30StartEndTimeTv != null)
            b30StartEndTimeTv.setText(startSleepTime + "-" + endSleepTime);
        DecimalFormat formater = new DecimalFormat("#0.0");

        formater.setRoundingMode(RoundingMode.HALF_UP);
        String S = formater.format((double) numberS / 60.0);

        formater.setRoundingMode(RoundingMode.FLOOR);
        String Q = formater.format((double) numberT / 60.0);
        String Z = formater.format((double) numberAllSleepTime / 60.0);

        if (Commont.isDebug) Log.e(TAG,
                "睡眠段总时长： " + sleepLin.length() + "分钟 == " + formater.format((double) sleepLin.length() / 60.0) +
                        "小时  实际睡眠时长： " + numberAllSleepTime + "分钟 == " + Z +
                        "小时  深睡： " + numberS + "分钟 == " + S +
                        "小时  浅睡： " + numberT + "分钟 == " + Q +
                        "小时  清醒次数： " + AWAKE + " 次");

//        //图标绘制时    浅睡  0    深睡 1   清醒 2
//        int xing = WatchUtils.countStr(sleepLin, '2');
//        int qian = WatchUtils.countStr(sleepLin, '0');
//        int shen = WatchUtils.countStr(sleepLin, '1');
//        int allSleep = shen + qian;
//        if (b30StartEndTimeTv != null)
//            b30StartEndTimeTv.setText(startSleepTime + "-" + endSleepTime);
////       if (Commont.isDebug) Log.e(TAG, "===  " + xing + "  " + qian + "   " + shen + "  " + allSleep);
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
//        if (Commont.isDebug)Log.e(TAG,
//                "睡眠段总时长： " + sleepLin.length() + "分钟 == " + formater.format((double) sleepLin.length() / 60.0) +
//                        "小时  实际睡眠时长： " + allSleep + "分钟 == " + Z +
//                        "小时  深睡： " + shen + "分钟 == " + S +
//                        "小时  浅睡： " + qian + "分钟 == " + Q +
//                        "小时  清醒次数： " + AWAKE + " 次");

        //保存睡眠数据
        //SleepData sleepData = mp.getValue();
        //清醒时长=总的睡眠时长-深睡时长-清醒时长
        //int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
        /**
         * 保存睡眠数据
         *
         * @param bleName   蓝牙名字
         * @param bleMac    蓝牙mac地址
         * @param dateStr   日期
         * @param deep      深睡时长
         * @param low       浅睡时长
         * @param sober     清醒时长
         * @param allSleep  所有睡眠时间
         * @param sleeptime 入睡时间
         * @param waketime  清醒时间
         * @param wakeCount 清醒次数
         */
        CommDBManager.getCommDBManager().saveCommSleepDbData((WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName()),
                WatchUtils.getSherpBleMac(getmContext()),
                dateStr,
                numberS,
                numberT,
                numberQ,
                numberAllSleepTime + numberQ,
                startSleepTime,
                endSleepTime,
                AWAKE);
//      if (Commont.isDebug)Log.e(TAG, "====" + AWAKE + "   " + SHALLOW + "  " + DEEP);


        sleepList.clear();
        if (!WatchUtils.isEmpty(sleepLin)) {
            if (WatchUtils.isEmpty(sleepLin) || sleepLin.length() < 2) {
                if (b30CusSleepView != null) b30CusSleepView.setSleepList(new ArrayList<Integer>());
                return;
            }
//            for (int i = 0; i < sleepLin.length(); i++) {
//                if (i <= sleepLin.length() - 1) {
//                    int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
//                    sleepList.add(subStr);
//                }
//            }

            for (int i = 0; i < sleepLin.length(); i++) {
                int ch = Integer.valueOf(String.valueOf(sleepLin.charAt(i)));
                if (Commont.isDebug) Log.e(TAG, "=====   " + ch);
                sleepList.add(ch);
            }


            //B15P元数据   清醒  0    浅睡 1   深睡 2
            //图标绘制时    浅睡  0    深睡 1   清醒 2

            sleepList.add(0, 2);
//            sleepList.add(0);
            sleepList.add(2);
        } else {
            if (b30StartEndTimeTv != null) b30StartEndTimeTv.setText("");
        }
        if (sleepList != null && !sleepList.isEmpty()) {
            if (b30CusSleepView != null) {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(sleepList);
            }
        } else {
            if (b30CusSleepView != null) {
                b30CusSleepView.setSeekBarShow(false);
                b30CusSleepView.setSleepList(new ArrayList<Integer>());
            }
        }
    }


    /**
     * 一个时间点加上一段分钟数得到新的时间点
     *
     * @param date       时分
     * @param handleTime 处理时间：分钟
     * @return
     * @throws ParseException
     */
    public String getTime(String date, int handleTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINA);
        Date now = sdf.parse(date);
        Date afterDate = new Date(now.getTime() + handleTime * 60 * 1000);
        return sdf.format(afterDate);
    }

}
