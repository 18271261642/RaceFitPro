//package com.example.bozhilun.android.h9.fragment;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.afa.tourism.greendao.gen.DaoSession;
//import com.afa.tourism.greendao.gen.H9HeartDBModelDao;
//import com.afa.tourism.greendao.gen.H9SleepDBModelDao;
//import com.afa.tourism.greendao.gen.H9StepDBModelDao;
//import com.afa.tourism.greendao.gen.SportMapsDao;
//import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
//import com.example.bozhilun.android.Commont;
//import com.example.bozhilun.android.MyApp;
//import com.example.bozhilun.android.R;
//import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
//import com.example.bozhilun.android.bleutil.MyCommandManager;
//import com.example.bozhilun.android.bzlmaps.CommomDialog;
//import com.example.bozhilun.android.bzlmaps.mapdb.SportMaps;
//import com.example.bozhilun.android.h9.H9HomeActivity;
//import com.example.bozhilun.android.h9.db.H9DBCommont;
//import com.example.bozhilun.android.h9.db.H9HeartDBModel;
//import com.example.bozhilun.android.h9.db.H9SleepDBModel;
//import com.example.bozhilun.android.h9.db.H9StepDBModel;
//import com.example.bozhilun.android.h9.h9monitor.UpDatasBase;
//import com.example.bozhilun.android.h9.settingactivity.CorrectionTimeActivity;
//import com.example.bozhilun.android.h9.settingactivity.H9HearteDataActivity;
//import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
//import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
//import com.example.bozhilun.android.h9.utils.CusRefreshLayout;
//import com.example.bozhilun.android.h9.utils.Device_Time_Activity;
//import com.example.bozhilun.android.h9.utils.H9TimeUtil;
//import com.example.bozhilun.android.siswatch.NewSearchActivity;
//import com.example.bozhilun.android.siswatch.utils.WatchUtils;
//import com.example.bozhilun.android.util.AnimationUtils;
//import com.example.bozhilun.android.util.Common;
//import com.sdk.bluetooth.protocol.command.clear.ClearBloodData;
//import com.sdk.bluetooth.protocol.command.clear.ClearHeartData;
//import com.sdk.bluetooth.protocol.command.clear.ClearSleepData;
//import com.sdk.bluetooth.protocol.command.clear.ClearSportData;
//import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
//import com.example.bozhilun.android.util.ToastUtil;
//import com.example.bozhilun.android.w30s.activity.SleepHistoryActivity;
//import com.example.bozhilun.android.w30s.activity.StepHistoryDataActivity;
//import com.example.bozhilun.android.w30s.bean.UpHeartBean;
//import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
//import com.example.bozhilun.android.w30s.views.W30CusHeartView;
//import com.example.bozhilun.android.w30s.views.W30S_SleepChart;
//import com.littlejie.circleprogress.circleprogress.WaveProgress;
//import com.sdk.bluetooth.bean.HeartData;
//import com.sdk.bluetooth.bean.SleepData;
//import com.sdk.bluetooth.bean.SportsData;
//import com.sdk.bluetooth.manage.AppsBluetoothManager;
//import com.sdk.bluetooth.manage.GlobalDataManager;
//import com.sdk.bluetooth.manage.GlobalVarManager;
//import com.sdk.bluetooth.protocol.command.base.BaseCommand;
//import com.sdk.bluetooth.protocol.command.base.CommandConstant;
//import com.sdk.bluetooth.protocol.command.count.AllDataCount;
//import com.sdk.bluetooth.protocol.command.data.DeviceDisplaySportSleep;
//import com.sdk.bluetooth.protocol.command.data.GetHeartData;
//import com.sdk.bluetooth.protocol.command.data.GetSleepData;
//import com.sdk.bluetooth.protocol.command.data.GetSportData;
//import com.sdk.bluetooth.protocol.command.device.BatteryPower;
//import com.sdk.bluetooth.protocol.command.device.DateTime;
//import com.sdk.bluetooth.protocol.command.device.Language;
//import com.sdk.bluetooth.protocol.command.device.Unit;
//import com.sdk.bluetooth.protocol.command.setting.GoalsSetting;
//import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
//import com.sdk.bluetooth.protocol.command.user.UserInfo;
//import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
//
//import org.apache.commons.lang.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//
///**
// * @aboutContent:
// * @author： 安
// * @crateTime: 2017/9/27 16:29
// * @mailBox: an.****.life@gmail.com
// * @company: 东莞速成科技有限公司
// */
//
//public class H9RecordFragment extends Fragment implements W30CusHeartView.DataTypeListenter {
//
//    private final String TAG = "H9RecordFragment";
//    public static final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
//    //    @BindView(R.id.previousImage)
////    ImageView previousImage;
////    @BindView(R.id.nextImage)
////    ImageView nextImage;
//    View b18iRecordView;
//    Unbinder unbinder;
//    @BindView(R.id.b18i_viewpager)
//    ViewPager l38iViewpager;
//    @BindView(R.id.text_stute)
//    TextView textStute;
//    @BindView(R.id.batteryLayout)
//    LinearLayout batteryLayout;
//    private int PAGES = 0;//页码
//    @BindView(R.id.line_pontion)
//    LinearLayout linePontion;
//    private float GOAL = 7000;//默认目标
//    private float STEP = 0;//步数
//    @BindView(R.id.swipeRefresh)
//    CusRefreshLayout swipeRefresh;//刷新控件
//    //显示手表图标左上角
//    @BindView(R.id.batteryshouhuanImg)
//    ImageView shouhuanImg;
//    //显示连接状态的TextView
//    @BindView(R.id.battery_watch_connectStateTv)
//    TextView watchConnectStateTv;
//    //点击图标
//    @BindView(R.id.watch_poorRel)
//    RelativeLayout watchPoorRel;
//    //显示日期的TextView
//    @BindView(R.id.battery_watch_recordtop_dateTv)
//    TextView watchRecordtopDateTv;
//    //分享
//    @BindView(R.id.battery_watchRecordShareImg)
//    ImageView watchRecordShareImg;
//    //显示电量的图片
//    @BindView(R.id.batteryTopView)
//    ImageView watchTopBatteryImgView;
//    //显示电量
//    @BindView(R.id.batteryPowerTv)
//    TextView batteryPowerTv;
//    private boolean mReceiverTag = false;
//    int kmormi; //距离显示是公制还是英制   公制1   英制0
//    private H9DBCommont h9DBCommont = null;
//    private DaoSession daoSession = null;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context = getActivity();
//        regeditReceiver();
//    }
//
//    /**
//     * 切换语言上下文置空处理
//     */
//    /**********************************************/
//    private Context context;
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        saveStateToArguments();
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        saveStateToArguments();
//    }
//
//    private void saveStateToArguments() {
//        if (getView() != null) context = getActivity();
//    }
//
//    /**********************************************/
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        b18iRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
//        unbinder = ButterKnife.bind(this, b18iRecordView);
//        saveStateToArguments();
//        setDatas();
//
//        h9DBCommont = H9DBCommont.getInstance();
//        daoSession = H9DBCommont.getDaoSession();
//        initViews();
//        initStepList();//目标设置选择列表
//
//
//        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
//        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
//        if (!WatchUtils.isEmpty(userId) && !WatchUtils.isEmpty(mac)) {
//            readHeartH9DBDataToUI(userId, mac);
//            readStepUpdata(userId, mac);
//            readSleepUpdata();
//        }
//
////        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////        if (!WatchUtils.isEmpty(userId) && !WatchUtils.isEmpty(mac)) {
////            readHeartH9DBDataToUI(userId, mac);
////            readStepUpdata(userId, mac);
////            readSleepUpdata();
////        }
//
//
////        String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
////        if (!TextUtils.isEmpty(homeTime)) {
////            String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
////            int number = Integer.valueOf(timeDifference.trim());
////            int number2 = Integer.parseInt(timeDifference.trim());
////            if (number >= 2 || number2 >= 2) {
////                int nuber = Integer.valueOf(timeDifference.trim());
////                if (!timeDifference.trim().equals("1")) {
////                    getDatas();
////                    SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
////                }
////            }
////        } else {
////            getDatas();
////            SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
////        }
////        SynchronousData();//自动同步数据
////        timingDown();
//        //时间长按去查看设备时间
//
//        if (MyApp.getInstance().isOne) {
//            MyApp.getInstance().isOne = false;
//            getAllDatas();
//        } else {
//            getDatas();
//        }
//
//
//        return b18iRecordView;
//    }
//
//    private boolean isHidden = true;//离开界面隐藏显示同步数据的
//
//    @Override
//    public void onStart() {
////       Log.d(TAG, "---vvv----onStart-------");
//        super.onStart();
////        isHidden = true;
////        if (B18iCommon.ISCHECKTARGET) {
////            B18iCommon.ISCHECKTARGET = false;
//////            circleprogress.reset();
////            //获取目标
//////            getSportDatas();//获取运动数据
////        }
////        watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
////        if (MyCommandManager.DEVICENAME != null) {
////            watchConnectStateTv.setText("" + "connect" + "");
////            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
////            AnimationUtils.stopFlick(watchConnectStateTv);
////            batteryLayout.setVisibility(View.VISIBLE);
////        } else {
////            watchConnectStateTv.setText("" + "disconn.." + "");
////            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
////            AnimationUtils.startFlick(watchConnectStateTv);
////            batteryLayout.setVisibility(View.INVISIBLE);
////        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        watchRecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                startActivity(new Intent(getActivity(), Device_Time_Activity.class).putExtra("is18i", "H9"));
//                return false;
//            }
//        });
//////        Log.d(TAG, "---vvv----onResume-------");
////        watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
////        if (MyCommandManager.DEVICENAME != null) {
////            watchConnectStateTv.setText("" + "connect" + "");
////            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
////            AnimationUtils.stopFlick(watchConnectStateTv);
////
////            if (MyApp.getInstance().isOne) {
////                if (isHidden) {
////                    textStute.setText(getResources().getString(R.string.syncy_data));
////                    textStute.setVisibility(View.VISIBLE);
////                }
////                getDatas();
////                MyApp.getInstance().isOne = false;
////            }
//////            //获取电池电量并显示
//////            AppsBluetoothManager.getInstance(MyApp.context).sendCommand(new BatteryPower(commandResultCallback));//
//////            //获取所有运动总数局，运动、心率、血压等等
//////            AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new AllDataCount(commandResultCallback));
//////            //获取公里或者英里
//////            AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new Unit(commandResultCallback));
//////            AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new SportSleepMode(commandResultCallback));
//////            AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new GetSleepData(commandResultCallback, 0, 10, 10));
////        } else {
//////            Log.e(TAG, "----rrrrrrrrrrrrrrrrr-------断开连接-");
////            if (isHidden) {
////                textStute.setText(getResources().getString(R.string.disconnted));
////                textStute.setVisibility(View.VISIBLE);
////            }
////            watchConnectStateTv.setText("" + "disconn.." + "");
////            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
////            AnimationUtils.startFlick(watchConnectStateTv);
////        }
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
////        Log.d(TAG, "---vvv----onPause-------");
//        isHidden = false;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
////        Log.d(TAG, "---vvv----onStop-------");
//        isHidden = false;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//        saveStateToArguments();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        Log.d(TAG, "------------------------onDestroy---------------H9RecordFragment-");
//        if (mReceiverTag) {
//            mReceiverTag = false;
//            context.unregisterReceiver(broadReceiver);
//        }
//        if (recordwaveProgressBar != null) {
//            recordwaveProgressBar.removeAnimator();
//        }
//    }
//
//    private void regeditReceiver() {
//        if (!mReceiverTag) {
//            IntentFilter intFilter = new IntentFilter();
//            intFilter.addAction(H9CONNECT_STATE_ACTION);
//            mReceiverTag = true;
//            context.registerReceiver(broadReceiver, intFilter);
//        }
//    }
//
//
//    private void initViews() {
//        shouhuanImg.setImageResource(R.mipmap.h9);
////        previousImage.setVisibility(View.GONE);
////        nextImage.setVisibility(View.GONE);
//        //手动刷新
//        swipeRefresh.setOnRefreshListener(new RefreshListenter());
////        getDatas();//初次启动获取数据
//    }
//
////    Handler myHandler = new Handler(new Handler.Callback() {
////        @Override
////        public boolean handleMessage(Message msg) {
////            if (msg != null && msg.what == 1001) {
////                circleprogress.reset();
////                getDatas();
////                circleprogress.postInvalidate();
////                myHandler.removeMessages(1001);
////            }
////            return false;
////        }
////    });
//
//
//    Handler mHandler = new Handler(new Handler.Callback() {
//        @SuppressLint("SetTextI18n")
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what) {
//                case 0x01:
//                    /**
//                     * 电池电量获取成功 -- 设置显示
//                     */
//                    //Log.d(TAG, "-----H9-onSuccess-  BatteryPower:电池电量" + (int) message.obj + "%");
//                    setBatteryPowerShow((int) message.obj);   //显示电量
//                    break;
//                case 0x02:
////                    Log.d(TAG, "-----H9-onSuccess-  GoalsSetting:\n" + "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
////                            "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
////                            "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
////                            "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());
//
//                    GOAL = GlobalVarManager.getInstance().getStepGoalsValue();
//                    //可以不用刷新步数图，等待步数获取成功后再刷新即可
////                    if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
////                        recordwaveProgressBar.setMaxValue(GOAL);
////                        recordwaveProgressBar.setValue(STEP);
////                        recordwaveProgressBar.postInvalidate();
////                    }
////                    String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
////                    watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
////                    watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + GOAL);
//                    //Log.d(TAG, "-----H9-onSuccess-  目标：" + GOAL + " 步数：" + STEP);
//                    break;
//                case 0x03:
////                    Log.d(TAG, "-----H9-onSuccess-  DeviceDisplaySportSleep:" + "步数:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
////                            "\n 卡路里:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
////                            "\n 距离:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
////                            "\n 睡眠时间:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
//                    /**
//                     * 设置运动数据
//                     */
//                    STEP = GlobalVarManager.getInstance().getDeviceDisplayStep();//获取运动步数
//                    //刷新步数图
//                    if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
//                        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
//                        recordwaveProgressBar.setMaxValue(GOAL);//最大值
//                        //目标设置
//                        recordwaveProgressBar.setTagStepStr(context.getResources().getString(R.string.settarget_steps) + " " + tagGoal);
//                        recordwaveProgressBar.setValue(STEP);//步数
//                        recordwaveProgressBar.postInvalidate();
//
//
//                        double deviceDisplayCalorie = GlobalVarManager.getInstance().getDeviceDisplayCalorie();
//                        double deviceDisplayDistance = GlobalVarManager.getInstance().getDeviceDisplayDistance();
////                        CALORIES = GlobalVarManager.getInstance().getDeviceDisplayCalorie();
////                        DISTANCE = GlobalVarManager.getInstance().getDeviceDisplayDistance();
////                    String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
////                    watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
//                        //CALORIES = WatchUtils.div((double) deviceDisplayCalorie, 1000, 2);
//                        //DISTANCE = WatchUtils.div((double) deviceDisplayDistance, 1000, 2);
//
//                        if (deviceDisplayCalorie > 0) {
//                            t_kcal.setText("cal");
//                            BigDecimal decimal = new BigDecimal(deviceDisplayCalorie / 1000.0);
//                            BigDecimal setScale = decimal.setScale(0, BigDecimal.ROUND_DOWN);
//                            L38iCalT.setText(setScale.intValue() + "");
//                        }
//
//
//                        // 0位公制 1为英制
//                        if (kmormi == 0) {
//                            t_mi.setText("km");
//                            if (deviceDisplayDistance > 0) {
//                                BigDecimal decimal = new BigDecimal(deviceDisplayDistance / 1000.0);
//                                BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
//                                L38iDisT.setText(setScale.doubleValue() + "");
//                            }
//                        } else {
//                            t_mi.setText("mi");
//                            BigDecimal decimal = new BigDecimal(deviceDisplayDistance * 0.0006214);
//                            BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
//                            L38iDisT.setText(setScale.doubleValue() + "");
//                        }
//
//                        //1英里(mi)=1609.344米(m)
//                        String trim = new DecimalFormat("#.##").format(((double) STEP / (double) GOAL) * 100).trim();
//                        if (!WatchUtils.isEmpty(trim) && trim.contains(".")) {
//                            textStepReach.setText(trim.split("[.]")[0] + "%");//设置达标值
//                        } else {
//                            textStepReach.setText(trim + "%");//设置达标值
//                        }
//                    }
//
//
//
//                     //运动总步数和目标  先不考虑上传的问题
//                    String upStepTime = (String) SharedPreferencesUtils.getParam(context, "upStepTime", "");
//                    if (!TextUtils.isEmpty(upStepTime)) {
//                        String timeDifference = H9TimeUtil.getTimeDifference(upStepTime, B18iUtils.getSystemDataStart());
//                        if (!TextUtils.isEmpty(timeDifference)) {
//                            int number = Integer.valueOf(timeDifference.trim());
//                            int number2 = Integer.parseInt(timeDifference.trim());
//                            if (number >= 5 && number2 >= 5) {
//                                //上传运动数据到后台
//                                double deviceDisplayCalorie = GlobalVarManager.getInstance().getDeviceDisplayCalorie();
//                                double deviceDisplayDistance = GlobalVarManager.getInstance().getDeviceDisplayDistance();
//                                BigDecimal decimalKcl = new BigDecimal(deviceDisplayCalorie / 1000.0);
//                                BigDecimal setScaleKcl = decimalKcl.setScale(0, BigDecimal.ROUND_DOWN);
//
//                                BigDecimal decimalDis = new BigDecimal(deviceDisplayDistance / 1000.0);
//                                BigDecimal setScaleDis = decimalDis.setScale(2, BigDecimal.ROUND_DOWN);
//                                UpDatasBase.updateLoadSportToServer(GOAL, STEP, setScaleKcl.doubleValue(), setScaleDis.doubleValue(), "");
//                                SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
//                            }
//                        }
//                    } else {
//                        //上传运动数据到后台
//                        double deviceDisplayCalorie = GlobalVarManager.getInstance().getDeviceDisplayCalorie();
//                        double deviceDisplayDistance = GlobalVarManager.getInstance().getDeviceDisplayDistance();
//                        BigDecimal decimalKcl = new BigDecimal(deviceDisplayCalorie / 1000.0);
//                        BigDecimal setScaleKcl = decimalKcl.setScale(0, BigDecimal.ROUND_DOWN);
//
//                        BigDecimal decimalDis = new BigDecimal(deviceDisplayDistance / 1000.0);
//                        BigDecimal setScaleDis = decimalDis.setScale(2, BigDecimal.ROUND_DOWN);
//                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, setScaleKcl.doubleValue(), setScaleDis.doubleValue(), "");
//                        SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
//                    }
//                    break;
//                case 0x04:
//                    LinkedList<SportsData> sportsDatas = (LinkedList<SportsData>) message.obj;
//                    //LogTestUtil.e(TAG, "-----H9-onSuccess-  GetSportData:" + sportsDatas.toString());
//                    if (!sportsDatas.isEmpty()) {
////                        long today_sport_time_stamp = System.currentTimeMillis() / 1000;
////                        if (upHeartBeanList == null) {
////                            upHeartBeanList = new ArrayList<>();
////                        } else {
////                            upHeartBeanList.clear();
////                        }
////                        /**
////                         * 步数占位
////                         */
////                        for (int i = 1; i <= 24; i++) {
//////                            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
//////                            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
//////                            upHeartBeanList.add((i - 1), new UpHeartBean(userId, mac, "00",
//////                                    "0", (i > 9 ? i + "" : "0" + i) + ":00", "00", "0", "00", "00"));
////                            /**
////                             * 数据库占位
////                             */
////                            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
////                            h9DBCommont.saveStepToDB(0,
////                                    W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd") + " " + (i > 9 ? i + "" : "0" + i) + ":00");
////                        }
//
//                        /**
//                         * 将每天的保存一个字典
//                         */
//                        List<SportsData> daSportList = new ArrayList<>();
//                        Map<String, List<SportsData>> datSportMap = new LinkedHashMap<String, List<SportsData>>();//保存天list数据的map
//                        for (SportsData item : sportsDatas) {
//                            String trimDevices = W30BasicUtils.longToString(item.sport_time_stamp * 1000, "yyyy-MM-dd");
//                            //Log.e(TAG, "-------trimDevices  时间：" + W30BasicUtils.longToString(item.time_stamp * 1000,
//                            //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + datHeartMap.containsKey(trimDevices));
//                            if (datSportMap.containsKey(trimDevices)) {
//                                daSportList.add(item);
//                            } else {
//                                daSportList.clear();
//                                daSportList.add(item);
//                            }
//                            datSportMap.put(trimDevices, daSportList);
//                        }
//
////                        /**
////                         * 这里获取今天昨天前天的
////                         */
//                        //今天
////                        String timeToday = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
////                        //昨天
////                        String timeYesterday = WatchUtils.obtainAroundDate(timeToday, true);
////                        //前天
////                        String timeBeforeYesterday = WatchUtils.obtainAroundDate(timeYesterday, true);
//
//
//                        /**
//                         * 遍历天
//                         */
//                        for (String key : datSportMap.keySet()) {
////                            if (key.equals(timeToday)) {
//                            /**
//                             * 添加占位
//                             */
//                            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
//                            h9DBCommont.stepDefulData(key);
//
//                            List<SportsData> sportsData = datSportMap.get(key);
//                            //java.util.LinkedHashMap 可以按加入时的顺序遍历。
//                            Map<String, List<SportsData>> hourSportValueMap = new LinkedHashMap<String, List<SportsData>>();//保存小时list数据的map
//                            List<SportsData> hourHeartList = null;
//                            /**
//                             * 数据分为小时保存
//                             */
//                            for (SportsData item : sportsData) {
//                                String hourKey = W30BasicUtils.longToString(item.sport_time_stamp * 1000, "yyyy-MM-dd HH:mm").substring(11, 13);
//                                if (hourSportValueMap.containsKey(hourKey)) {
//                                    if (hourHeartList != null) hourHeartList.add(item);
//                                } else {
//                                    hourHeartList = new LinkedList<>();//list数据
//                                    hourHeartList.add(item);
//                                }
//                                hourSportValueMap.put(hourKey, hourHeartList);
//                            }
//
//                            /**
//                             * 去除 以时为 KEY的map
//                             */
//                            if (!hourSportValueMap.isEmpty()) {
//                                /**
//                                 * 遍历  小时 map中的键
//                                 */
//                                for (String hourMapKey : hourSportValueMap.keySet()) {
//                                    List<SportsData> hourSportDataLinkedList = hourSportValueMap.get(hourMapKey);//单个小时的心率数据集合
//                                    int sportValues = 0;
//                                    if (hourSportDataLinkedList != null && !hourSportDataLinkedList.isEmpty()) {
//                                        for (int j = 0; j < hourSportDataLinkedList.size(); j++) {
//                                            sportValues += hourSportDataLinkedList.get(j).sport_steps;
//                                        }
//                                    }
////                                    Log.d(TAG, "-----H9-onSuccess- 添加步数 时间B:" + (Integer.valueOf(hourMapKey)) + "===" + sportValues + "===" + timeToday + " " + hourMapKey + ":00");
////                                    String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////                                    String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////                                    upHeartBeanList.set((Integer.valueOf(hourMapKey)), new UpHeartBean(userId, mac, "00",
////                                            sportValues + "", hourMapKey + ":00",
////                                            "0", "0", "00", "00"));
//
//
//                                    Log.d(TAG, "==单天步数统计=" + key + " " + hourMapKey + ":00" + "------" + sportValues);
//                                    /**
//                                     * 数据库更新或者添加
//                                     */
//                                    if (h9DBCommont == null)
//                                        h9DBCommont = H9DBCommont.getInstance();
//                                    h9DBCommont.saveStepToDB(sportValues, key + " " + hourMapKey + ":00");
//                                }
//                            }
////                            }
//
//                        }
//
////                        for (int i = 1; i <= sportsDatas.size(); i++) {
////                            long sport_time_stamp = sportsDatas.get(i - 1).getSport_time_stamp();
////                            String rtc = W30BasicUtils.longToString(sport_time_stamp * 1000, "yyyy-MM-dd HH:mm");
////
////
////                            long sport_time_stampa = sportsDatas.get(i - 1).getSport_local_time_stamp();
////                            String rtc1 = W30BasicUtils.longToString(sport_time_stampa * 1000, "yyyy-MM-dd HH:mm");
////                            //Log.d(TAG, "-----H9-onSuccess- 步数 时间:" + rtc + "===" + rtc1 + "===" + sportsDatas.get(i - 1).getSport_steps());
//////                            String tim = objects.getData().trim().replace(" ", "") + " " + "01:00";
//////                            if (i <= 9) {
//////                                tim = objects.getData().trim().replace(" ", "") + " " + "0" + i + ":00";
//////                            } else {
//////                                tim = objects.getData().trim().replace(" ", "") + " " + i + ":00";
//////                            }
////                            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////                            String mac = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), Commont.BLEMAC, "");
////                            upHeartBeanList.add((i - 1), new UpHeartBean(userId, mac, "00", sportsDatas.get(i - 1).getSport_steps() + "", rtc, "00", "0", "00", "00"));
////                        }
//                        //上传运动详细数据
////                        if (upHeartBeanList != null) {
////                            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
////                            UpDatasBase.upAllDataSteps(jsonArray1);
////                        }
//                        //9.49  10.37   10.38  10.42 11.12  11.20
//
//
//                        /**
//                         * 读取步数上传
//                         */
//                        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
//                        String mac = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), Commont.BLEMAC, "");
//                        readStepUpdata(userId, mac);
//                    }
//                    break;
//                case 0x05:
//                    LinkedList<HeartData> heartDatas = (LinkedList<HeartData>) message.obj;
//                    //LogTestUtil.e(TAG, "-----H9-onSuccess-  GetHeartData:" + heartDatas.toString());
//                    if (!heartDatas.isEmpty()) {
////                        if (upHeartBeanList == null) {
////                            upHeartBeanList = new ArrayList<>();
////                        } else {
////                            upHeartBeanList.clear();
////                        }
////
////                        if (heartList != null) heartList.clear();
////                        if (heartList == null) heartList = new ArrayList<>();
////
////                        if (upHeartBeanList == null) {
////                            upHeartBeanList = new ArrayList<>();
////                        } else {
////                            upHeartBeanList.clear();
////                        }
////
////                        if (heartList != null) heartList.clear();
////                        if (heartList == null) heartList = new ArrayList<>();
//
////                        long today_sport_time_stamp = System.currentTimeMillis() / 1000;
////
////                        for (int i = 1; i <= 48; i++) {
////                            double timesHour = (double) ((i - 1) * 0.5);
////                            int hours = 0;
////                            int mins = 0;
////                            String[] splitT = String.valueOf(timesHour).split("[.]");
////                            if (splitT.length >= 2) {
////                                hours = Integer.valueOf(splitT[0]);
////                                mins = Integer.valueOf(splitT[1]) * 60 / 10;
////                            } else {
////                                hours = Integer.valueOf(splitT[0]);
////                                mins = 0;
////                            }
////                            String timeHour = "";
////                            String timeMin = "";
////                            if (hours <= 9) {
////                                timeHour = "0" + hours;
////                            } else {
////                                timeHour = "" + hours;
////                            }
////                            if (mins <= 9) {
////                                timeMin = "0" + mins;
////                            } else {
////                                timeMin = "" + mins;
////                            }
////                            String upDataTime = timeHour + ":" + timeMin;
////
////                            String trim = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
////                            final String tim = trim + " " + upDataTime;
////
////                            /**
////                             * 心率数据库占位
////                             */
////                            saveHeartToDB(0, tim);
////
////                            //Log.d(TAG, "-----H9-onSuccess- 占位心率 时间:" + tim + "===" + 0);
////                            //if (heartList == null) heartList = new ArrayList<>();
////                            //String systemTimer = B18iUtils.getSystemDatasss();
////                            //if (systemTimer.equals(trim)) {
////                            //    heartList.add(0);
////                            //}
////                            //String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////                            //String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////                            //upHeartBeanList.add((i - 1), new UpHeartBean(userId, mac, "00", "00", tim, "0", "0", "00", "00"));
////                        }
//
//                        /**
//                         * 将每天的保存一个字典
//                         */
//                        LinkedList<HeartData> daHeartList = new LinkedList<>();
//                        Map<String, LinkedList<HeartData>> datHeartMap = new LinkedHashMap<String, LinkedList<HeartData>>();//保存天list数据的map
//                        for (HeartData item : heartDatas) {
//                            String trimDevices = W30BasicUtils.longToString(item.time_stamp * 1000, "yyyy-MM-dd");
//                            //Log.e(TAG, "-------trimDevices  时间：" + W30BasicUtils.longToString(item.time_stamp * 1000,
//                            //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + datHeartMap.containsKey(trimDevices));
//                            if (datHeartMap.containsKey(trimDevices)) {
//                                daHeartList.add(item);
//                            } else {
//                                daHeartList.clear();
//                                daHeartList.add(item);
//                            }
//                            datHeartMap.put(trimDevices, daHeartList);
//                        }
//
//                        //Log.e(TAG, "一天的新路数据" + daHeartList.size() + "===" + daHeartList.toString());
//
////                        /**
////                         * 这里获取今天昨天前天的
////                         */
////                        //今天
////                        String timeToday = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
////                        //昨天
////                        String timeYesterday = WatchUtils.obtainAroundDate(timeToday, true);
////                        //前天
////                        String timeBeforeYesterday = WatchUtils.obtainAroundDate(timeYesterday, true);
//
//
//                        /**
//                         * 遍历天
//                         */
//                        for (String key : datHeartMap.keySet()) {
//
//
//                            //每次读取数据后只缓存三天，再多不要
////                            if (key.equals(timeToday)||key.equals(timeYesterday)||key.equals(timeYesterday)) {//判断今天,昨天，前天
//                            MyApp.getInstance().getDBManager().getDaoSession().getH9HeartDBModelDao().deleteAll();
//                            /**
//                             *每日的心率数据0占位
//                             */
//                            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
//                            h9DBCommont.heartDefulData(key);
//
//                            //Log.d(TAG, "day  要查询的 KEY ：" + timeToday);
//                            LinkedList<HeartData> dayHeartListItem = datHeartMap.get(key);//单个天的心率数据
//                            if (dayHeartListItem != null && !dayHeartListItem.isEmpty()) {
//                                //java.util.LinkedHashMap 可以按加入时的顺序遍历。
//                                Map<String, LinkedList<HeartData>> hourHeartValueMap = new LinkedHashMap<String, LinkedList<HeartData>>();//保存小时list数据的map
//                                LinkedList<HeartData> hourHeartList = null;
//                                //数据分为小时保存
//                                for (HeartData item : dayHeartListItem) {
//                                    String hourKey = W30BasicUtils.longToString(item.time_stamp * 1000, "yyyy-MM-dd HH:mm").substring(11, 13);
//                                    //Log.e(TAG, "-----2--trimDevices  时间：" + W30BasicUtils.longToString(item.time_stamp * 1000,
//                                    //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + hourHeartValueMap.containsKey(hourKey));
//                                    if (hourHeartValueMap.containsKey(hourKey)) {
//                                        if (hourHeartList != null)
//                                            hourHeartList.add(item);
//                                    } else {
//                                        //if (hourHeartList != null && !hourHeartList.isEmpty())
//                                        //    Log.e(TAG, "每时的数据" + hourKey + "===" + hourHeartList.size() + "===" + hourHeartList.toString());
//                                        hourHeartList = new LinkedList<>();//list数据
//                                        hourHeartList.add(item);
//                                    }
//                                    hourHeartValueMap.put(hourKey, hourHeartList);
//                                }
//
//
//                                /**
//                                 * 去除 以时为 KEY的map
//                                 */
//                                if (!hourHeartValueMap.isEmpty()) {
//                                    //int postions = 0;
//                                    //遍历map中的键
//                                    for (String hourMapKey : hourHeartValueMap.keySet()) {
//                                        // postions++;
//                                        //System.out.println("Key = " + hourMapKey+"------Value = " +hourHeartValueMap.get(hourMapKey));
//                                        //Log.d(TAG, "hour 要查询的 KEY ：" + hourMapKey);
//                                        LinkedList<HeartData> hourHeartDataLinkedList = hourHeartValueMap.get(hourMapKey);//单个小时的心率数据集合
//
//                                        //Log.d(TAG, "-----H9-onSuccess- 单个小时的心率数据集合:=\n时间:"
//                                        //        + hourMapKey
//                                        //        + "=\n长度:" + (hourHeartDataLinkedList == null ? "没有数据" : hourHeartDataLinkedList.size())
//                                        //        + "=\n数据:" + (hourHeartDataLinkedList == null ? "没有数据" : hourHeartDataLinkedList.toString()));
//                                        int valueHeart = 0;
//                                        if (hourHeartDataLinkedList != null && !hourHeartDataLinkedList.isEmpty()) {
//                                            int heartValues = 0;
//                                            int heartCount = 0;
//                                            for (int j = 0; j < hourHeartDataLinkedList.size(); j++) {
//                                                heartCount++;
//                                                //Log.d(TAG, j + "----" + hourKey + "的心率" + hourHeartDataLinkedList.get(j).heartRate_value + "-------" + hourHeartDataLinkedList.get(j).time_stamp);
//                                                heartValues += hourHeartDataLinkedList.get(j).heartRate_value;
//                                                //Log.d(TAG, j + "----" + hourKey + "的心率" + heartValues);
//                                            }
//                                            if (heartCount == 0) heartCount = 1;
//                                            valueHeart = heartValues / heartCount;
//                                            //Log.d(TAG, hourMapKey + "内的平均心率" + valueHeart);
//                                        }
////                                            heartList.set((Integer.valueOf(hourMapKey) * 2), valueHeart);
//                                            //Log.d(TAG, "-----H9-onSuccess- 添加心率 时间B:" +(Integer.valueOf(hourMapKey)*2)+"==="+ valueHeart + "===" + timeToday + " " + hourMapKey + ":00");
////                                            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////                                            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////                                            upHeartBeanList.set((Integer.valueOf(hourMapKey) * 2), new UpHeartBean(userId, mac, "00", "00", timeToday + " " + hourMapKey + ":00", valueHeart+"", "0", "00", "00"));
//                                        if (h9DBCommont == null)
//                                            h9DBCommont = H9DBCommont.getInstance();
//                                        h9DBCommont.saveHeartToDB(valueHeart, key + " " + hourMapKey + ":00");
//                                    }
//                                    //for (int i = 0; i < heartList.size(); i++) {
//                                    //    Log.d(TAG, "-----H9-onSuccess- 最新心率 时间B:" + heartList.size() + "===" + "===" + heartList.get(i) + "====" + upHeartBeanList.get(i).getDate());
//                                    //}
////
////                                        if (heartList != null && lineChart != null) {
////                                            if (getActivity() != null && !getActivity().isFinishing()) {
////                                                int allNumber = 0;
////                                                List<Integer> minHeart = new ArrayList<>();
////                                                for (int i = 0; i < heartList.size(); i++) {
////                                                    if (heartList.get(i) > 0) {
////                                                        allNumber += heartList.get(i);
////                                                        minHeart.add(heartList.get(i));
////                                                    }
////                                                }
////                                                //Log.d(TAG, "======心率总数居+++有效长度" + heartList.size());
////                                                int pjz = allNumber / (minHeart.size() == 0 ? 1 : minHeart.size());
////                                                int max = Collections.max(heartList);
////                                                int min = Collections.min(minHeart);
////                                                maxHeartTextNumber.setText(max + "");
////                                                zuidiHeartTextNumber.setText(min + "");
////                                                autoHeartTextNumber.setText(pjz + "");
////
////                                                lineChart.setRateDataList(heartList);
////                                                lineChart.invalidate();
////                                                if (SleepseekBarHared != null)
////                                                    SleepseekBarHared.setEnabled(true);
////                                            }
////                                        }
////
////
////                                        /**
////                                         * 上传心率详细数据
////                                         */
////                                        if (upHeartBeanList != null) {
////                                            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
////                                            UpDatasBase.upAllDataHearte(jsonArray1);
////                                        }
//
////                                    }
//                                }
//
//                            }
//                        }
//
//                        /**
//                         * 读取数据显示和上传
//                         */
//                        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
//                        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
//                        readHeartH9DBDataToUI(userId, mac);
//                    }
//                    break;
//                case 0x06:
//
//                    /*
//                      if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
//                        Log.d("=====AAA==清醒", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("2")) {  //潜睡状态
//                        Log.d("=====AAA==浅睡", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("3")) {  //深睡
//                        Log.d("=====AAA==深睡", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("88")) {  //深睡
//                        Log.d("=====AAA==自定义的起床状态", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, "--");
//                    }
//                     */
//                    // sleepData.sleep_type
//                    // 0：睡着
//                    // 1：浅睡
//                    // 2：醒着
//                    // 3：准备入睡
//                    // 4：退出睡眠
//                    // 16：进入睡眠模式
//                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
//                    LinkedList<SleepData> sleepDatas = (LinkedList<SleepData>) message.obj;
//                    if (sleepDatas != null && !sleepDatas.isEmpty()) {
//
//                        /**
//                         * 将每天的单独拿出来
//                         */
//                        LinkedList<SleepData> daSleepList = new LinkedList<>();
//                        Map<String, LinkedList<SleepData>> datSleepMap = new LinkedHashMap<>();//保存天list数据的map
//                        for (int i = 0; i < sleepDatas.size(); i++) {
//                            SleepData sleepData = sleepDatas.get(i);
//                            long sleep_time_stamp = sleepData.sleep_time_stamp;
//                            String trimDevices = W30BasicUtils.longToString(sleep_time_stamp * 1000, "yyyy-MM-dd");
//                            Log.d(TAG, "添加天的数据" + W30BasicUtils.longToString(sleep_time_stamp * 1000, "yyyy-MM-dd HH:mm:ss"));
//                            if (datSleepMap.containsKey(trimDevices)) {
//                                daSleepList.add(sleepData);
//                            } else {
//                                daSleepList.clear();
//                                daSleepList.add(sleepData);
//                            }
//                            datSleepMap.put(trimDevices, daSleepList);
//                        }
////                    /**
////                     * 这里获取今天昨天前天的
////                     */
////                    long today_sport_time_stamp = System.currentTimeMillis() / 1000;
////                    //今天
////                    String timeToday = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
////                    //昨天
////                    String timeYesterday = WatchUtils.obtainAroundDate(timeToday, true);
////                    //前天
////                    //String timeBeforeYesterday = WatchUtils.obtainAroundDate(timeYesterday, true);
//
//                        for (String todayKey : datSleepMap.keySet()) {
//                            //System.out.println("Key = " + todayKey + "------Value = " + datSleepMap.get(todayKey));
////                        if ( todayKey.equals(timeToday)) {//今天
//                            LinkedList<SleepData> sleepData = datSleepMap.get(todayKey);//今天
//                            if (sleepData != null && !sleepData.isEmpty()) {
//                                //LogTestUtil.e(TAG, "-----H9-onSuccess-  今天睡眠GetSleepData:" + sleepData.toString());
////                                List<W30S_SleepDataItem> sleepDataLists = new ArrayList<>();
//                                for (int i = 0; i < sleepData.size(); i++) {
//                                    int sleep_type = sleepData.get(i).sleep_type;
//                                    long sleep_time_stamp = sleepData.get(i).sleep_time_stamp;
//
//                                    String longSleepTime = W30BasicUtils.longToString(sleep_time_stamp * 1000, "yyyy-MM-dd HH:mm");
//                                    String sleep_time = W30BasicUtils.longToString(sleep_time_stamp * 1000, "HH:mm");
//                                    String hour_time = W30BasicUtils.longToString(sleep_time_stamp * 1000, "HH");
//                                    Log.d(TAG, "读取的睡眠数据---" + todayKey + "===" + longSleepTime + "====" + hour_time);
//                                    if (Integer.valueOf(hour_time) <= 10) {
//                                        Log.d(TAG, "保存的睡眠数据--B-" + sleep_time + "===" + sleep_type);
//                                        if (h9DBCommont == null)
//                                            h9DBCommont = H9DBCommont.getInstance();
//                                        h9DBCommont.saveSleepToDB(sleep_type, sleep_time, longSleepTime, todayKey);
//                                    }
//
////                                    W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
////                                    w30S_sleepDataItem.setStartTime(sleep_time);
//
//                                    //Log.d(TAG, "==========睡眠的：" + W30BasicUtils.longToString(sleep_time_stamp * 1000, "yyyy-MMM-ddd HH:mm") + "===" + sleep_type);
////                                    switch (sleep_type) {
////                                        case 0:
////                                            w30S_sleepDataItem.setSleep_type("3");
////                                            break;
////                                        case 1:
////                                            w30S_sleepDataItem.setSleep_type("2");
////                                            break;
////                                        case 2:
////                                            w30S_sleepDataItem.setSleep_type("0");
////                                            break;
////                                        case 3:
////                                            w30S_sleepDataItem.setSleep_type("4");
////                                            break;
////                                        case 4:
////                                            w30S_sleepDataItem.setSleep_type("0");
////                                            break;
////                                        case 16:
////                                            w30S_sleepDataItem.setSleep_type("5");
////                                            break;
////                                        case 17:
////                                            w30S_sleepDataItem.setSleep_type("3");
////                                            break;
////                                        case 18:
////                                            w30S_sleepDataItem.setSleep_type("1");
////                                            break;
////                                    }
////
////                                    sleepDataLists.add(w30S_sleepDataItem);
//                                    //int sleep_type,String sleep_time,String dataTime
//
//                                    //保存
////                                    Log.d(TAG, "保存的睡眠数据---" + sleep_time + "===" + sleep_type);
////                                    if (h9DBCommont == null)
////                                        h9DBCommont = H9DBCommont.getInstance();
////                                    h9DBCommont.saveSleepToDB(sleep_type, sleep_time, longSleepTime, todayKey);
//                                }
//
////                                //Log.d(TAG, "=========睡眠数据" + sleepDataLists.toString());
////                                if (getActivity() != null && !getActivity().isFinishing()) {
////                                    if (sleepDataLists.isEmpty()) {
////                                        w30S_sleepChart.setVisibility(View.GONE);
////                                        text_sleep_nodata.setVisibility(View.VISIBLE);
////                                    } else {
////                                        ALLTIME = 0;
////                                        DEEP = 0;
////                                        SHALLOW = 0;
////                                        AWAKE2 = 0;
////                                        AWAKE = 0;
////                                        for (int i = 0; i < sleepDataLists.size(); i++) {
////                                            String startTime = null;
////                                            String startTimeLater = null;
////                                            String sleep_type = null;
////                                            if (i >= (sleepDataLists.size() - 1)) {
////                                                startTime = sleepDataLists.get(i).getStartTime();
////                                                startTimeLater = sleepDataLists.get(i).getStartTime();
////                                                sleep_type = sleepDataLists.get(i).getSleep_type();
////                                            } else {
////                                                startTime = sleepDataLists.get(i).getStartTime();
////                                                startTimeLater = sleepDataLists.get(i + 1).getStartTime();
////                                                sleep_type = sleepDataLists.get(i).getSleep_type();
////                                            }
////                                            String[] starSplit = startTime.split("[:]");
////                                            String[] endSplit = startTimeLater.split("[:]");
////
////                                            int startHour = Integer.valueOf(starSplit[0]);
////                                            int endHour = Integer.valueOf(endSplit[0]);
////
////                                            int startMin = Integer.valueOf(starSplit[1]);
////                                            int endMin = (Integer.valueOf(endSplit[1]));
////                                            if (startHour > endHour) {
////                                                endHour = endHour + 24;
////                                            }
////                                            int all_m = (endHour - startHour) * 60 + (endMin - startMin);
////                                            if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
////                                                AWAKE2 += all_m;
////                                                ALLTIME += all_m;
////                                            } else if (sleep_type.equals("4")) {
////                                                AWAKE2 += all_m;
////                                                ALLTIME += all_m;
////                                                AWAKE++;
////                                            } else if (sleep_type.equals("2")) {
////                                                //潜水
////                                                SHALLOW += all_m;
////                                                ALLTIME += all_m;
////                                            } else if (sleep_type.equals("3")) {
////                                                //深水
////                                                DEEP += all_m;
////                                                ALLTIME += all_m;
////                                            }
////                                        }
////
////
////                                        w30S_sleepChart.setVisibility(View.VISIBLE);
////                                        text_sleep_nodata.setVisibility(View.GONE);
////                                        w30S_sleepChart.setBeanList(sleepDataLists);
////                                        //入睡时间
////                                        if (sleep_into_time != null) {
////                                            sleep_into_time.setVisibility(View.VISIBLE);
////                                            sleep_into_time.setText(sleepDataLists.get(0).getStartTime());
////                                        }
////                                        //醒来时间
////                                        if (sleep_out_time != null) {
////                                            sleep_out_time.setVisibility(View.VISIBLE);
////                                            sleep_out_time.setText(sleepDataLists.get(sleepDataLists.size() - 1).getStartTime());
////                                        }
////                                        //总睡眠设置为可拖动最大进度
////                                        if (SleepseekBar != null) {
////                                            SleepseekBar.setMax(ALLTIME);
////                                            SleepseekBar.setEnabled(true);
////                                        }
////                                        DecimalFormat df = new DecimalFormat("#.#");
////                                        df.setRoundingMode(RoundingMode.FLOOR);
////                                        String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).trim();
////                                        //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);
////                                        if (deepSleep != null) {
////                                            double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
////                                            deepSleep.setText(setScale + getResources().getString(R.string.hour));
////                                        }
////                                        if (shallowSleep != null) {
////                                            double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
////                                            double v = Double.valueOf(div3) - setScale;
////                                            double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
////                                            //double setScale = (double) WatchUtils.div((double) SHALLOW, 60, 1);
////                                            shallowSleep.setText(setScale1 + getResources().getString(R.string.hour));
////                                        }
////                                        if (awakeSleep != null) {
////                                            double setScale = (double) WatchUtils.div((double) AWAKE2, 60, 1);
////                                            awakeSleep.setText(setScale + getResources().getString(R.string.hour));
////                                        }
////                                        double hour = (double) (DEEP + SHALLOW) / (double) 60;
////                                        String format = new DecimalFormat("0.00").format(hour);
////                                        String[] split = format.split("[.]");
////                                        int integer = Integer.valueOf(split[0].trim());
////                                        String s1 = String.valueOf(((hour - integer) * 60));
////                                        String[] split1 = s1.split("[.]");
////                                        String a = "0";
////                                        if (split1[0] != null) {
////                                            a = split1[0].trim();
////                                        }
////                                        String w30ssleep = (String) SharedPreferencesUtils.getParam(getContext(), "w30ssleep", "8");
////                                        if (!WatchUtils.isEmpty(w30ssleep)) {
////                                            int standardSleepAll = Integer.valueOf(w30ssleep.trim()) * 60;
////                                            int allSleep = integer * 60 + Integer.valueOf(a);
////                                            double standardSleep = WatchUtils.div(allSleep, standardSleepAll, 2);
////                                            //int standar = (allSleep / standardSleepAll) * 100;
////                                            String strings = String.valueOf((standardSleep * 100));
////                                            if (textAllSleepData != null) {
////                                                textAllSleepData.setVisibility(View.VISIBLE);
////                                                if (textAllSleepData != null) {
////                                                    textAllSleepData.setVisibility(View.VISIBLE);
////                                                    if (strings.contains(".")) {
////                                                        textAllSleepData.setText(getResources().getString(R.string.sleep) + ":" + div3 + getResources().getString(R.string.hour)
////                                                                + "    " + getResources().getString(R.string.string_standar) + ":" + strings.split("[.]")[0] + "%"
////                                                                + "    " + getResources().getString(R.string.recovery_count_frequency) + ":" + AWAKE);
////                                                    } else {
////                                                        textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour)
////                                                                + "    " + getResources().getString(R.string.string_standar) + (standardSleep * 100) + "%"
////                                                                + "    " + getResources().getString(R.string.recovery_count) + AWAKE + getResources().getString(R.string.cishu));
////                                                    }
////                                                }
////                                            }
////                                        } else {
////                                            if (textAllSleepData != null)
////                                                textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour) + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
////                                        }
////
////
////                                        double v = SHALLOW + AWAKE2 + DEEP;
////                                        if (qianshuiT != null) {
////                                            double v1 = WatchUtils.div(SHALLOW, v, 2);
////                                            if (v1 > 0) {
////                                                qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
////                                            }
////                                        }
////                                        if (qingxingT != null) {
////                                            double v1 = WatchUtils.div(AWAKE2, v, 2);
////                                            if (v1 > 0) {
////                                                qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
////                                            }
////                                        }
////                                        if (shenshuiT != null) {
////                                            double v1 = WatchUtils.div(DEEP, v, 2);
////                                            if (v1 > 0) {
////                                                shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
////                                            }
////                                        }
////
////
////                                        String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
////                                        if (!TextUtils.isEmpty(homeTime)) {
////                                            String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
////                                            if (!WatchUtils.isEmpty(timeDifference)) {
////                                                int number = Integer.valueOf(timeDifference.trim());
////                                                int number2 = Integer.parseInt(timeDifference.trim());
////                                                if (Math.abs(number) >= 3600 || Math.abs(number2) >= 3600) {
////                                                    String starTime = sleepDataLists.get(0).getStartTime();
////                                                    String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
////                                                    Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
////                                                    UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
////                                                    SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
////                                                }
////                                            }
////                                        } else {
////                                            String starTime = sleepDataLists.get(0).getStartTime();
////                                            String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
////                                            Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
////                                            UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
////                                            SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
////
////                                        }
////
////                                    }
////                                }
//
//                            }
//
////                        }
//
//
////                    }
//
//
////                    /**
////                     * 将每天的保存一个字典
////                     */
////                    Map<String, LinkedList<SleepData>> datSleepMap = new LinkedHashMap<String, LinkedList<SleepData>>();//保存天list数据的map
////                    LinkedList<SleepData> sleepData = new LinkedList<>();
////                    for (SleepData item : sleepDatas) {
////                        String trimDevices = W30BasicUtils.longToString(item.sleep_time_stamp * 1000, "yyyy-MM-dd");
////                        Log.e(TAG, "-------trimDevices  睡眠时间：" + W30BasicUtils.longToString(item.sleep_time_stamp * 1000,
////                                "yyyy-MM-dd HH:mm:ss") + "  -- 睡眠类型：" + item.sleep_type + "=====是否包含在Map==" + datSleepMap.containsKey(trimDevices));
////                        if (datSleepMap.containsKey(trimDevices)) {
////                            sleepData.add(item);
////                        } else {
////                            sleepData.clear();
////                            sleepData.add(item);
////                        }
////                        datSleepMap.put(trimDevices, sleepData);
////                    }
////
////                    long today_sport_time_stamp = System.currentTimeMillis() / 1000;
////                    //今天
////                    String timeToday = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
////
////
////                    LinkedList<SleepData> sleepDataLinkedList = datSleepMap.get(timeToday);//单个天的心率数据
////                    for (int i = 0; i < sleepDataLinkedList.size(); i++) {
////                        Log.e(TAG, "--------今天睡眠数据" + sleepDataLinkedList.size()
////                                + "==" + W30BasicUtils.longToString(sleepDataLinkedList.get(i).sleep_time_stamp * 1000,
////                                "yyyy-MM-dd HH:mm:ss") + "==" + sleepDataLinkedList.get(i).sleep_type);
////                    }
////
////
////                    String timeYesterday = WatchUtils.obtainAroundDate(timeToday, true);
////                    LinkedList<SleepData> sleepDataLinkedList1 = datSleepMap.get(timeYesterday);//单个天的心率数据
////                    for (int i = 0; i < sleepDataLinkedList.size(); i++) {
////                        Log.e(TAG, "--------昨天睡眠数据" + sleepDataLinkedList.size()
////                                + "==" + W30BasicUtils.longToString(sleepDataLinkedList.get(i).sleep_time_stamp * 1000,
////                                "yyyy-MM-dd HH:mm:ss") + "==" + sleepDataLinkedList.get(i).sleep_type);
//                        }
//                        /**
//                         * 读取睡眠上传
//                         */
//                        readSleepUpdata();
//
////                    JSONArray jsonArray = ProLogListJson(sleepDatas);
////                    UpDatasBase.upDataTodaSleep(sleepDatas.getSleepData(), jsonArray);
//                    }
//
//
//
//                    break;
//                case 0x07:
//
//                    break;
//            }
//            return false;
//        }
//    });
//
//
////    虚拟睡眠
////    int[] styp = {16,3,3,18,16,3,1,0,1,0,1,0,10,1,0,1,0,1,2,2,18,16,3,3,18};
////    String[] times = {"2018-12-24 01:55","2018-12-24 01:55","2018-12-24 02:05",
////            "2018-12-24 02:05","2018-12-24 02:37","2018-12-24 02:37","2018-12-24 03:41",
////            "2018-12-24 03:56","2018-12-24 04:42","2018-12-24 04:54","2018-12-24 05:23",
////            "2018-12-24 05:35","2018-12-24 05:52","2018-12-24 06:04","2018-12-24 06:36",
////            "2018-12-24 06:48","2018-12-24 06:56","2018-12-24 07:08","2018-12-24 07:22",
////            "2018-12-24 07:35","2018-12-24 07:40","2018-12-24 07:40"};
////    void addSleep(){
////
////        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
////        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
////
////        for (int i = 0; i < styp.length; i++) {
////            if (h9DBCommont == null)
////                h9DBCommont = H9DBCommont.getInstance();
////            h9DBCommont.saveSleepToDB(styp[i], times[i].substring(11,16), times[i], times[i].substring(0,10));
////        }
////    }
//
//
//    /**
//     * 读取上传详细睡眠-----并且显示UI
//     */
//    private void readSleepUpdata() {
//
////
////        /**
////         * 这种方式也见鬼了 查询不到数据
////         */
////        List<H9SleepDBModel> h9SleepDBModelList = MyApp.getInstance().getDaoSession().getH9SleepDBModelDao()
////                .queryBuilder().where(H9SleepDBModelDao.Properties.DateTime.eq(WatchUtils.getCurrentDate()),
////                        H9SleepDBModelDao.Properties.DevicesCode.eq(mac),
////                        H9SleepDBModelDao.Properties.UserId.eq(userId)).list();
////
////        Log.d(TAG, "==========睡眠" + h9SleepDBModelList.size());
////        if (h9SleepDBModelList != null && !h9SleepDBModelList.isEmpty()) {
////            List<W30S_SleepDataItem> sleepDataLists = new ArrayList<>();
////            for (int i = 0; i < h9SleepDBModelList.size(); i++) {
////                H9SleepDBModel h9SleepDBModel = h9SleepDBModelList.get(i);
////                W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
////                w30S_sleepDataItem.setStartTime(h9SleepDBModel.getSleepTime());
////                switch (h9SleepDBModel.getSleepType()) {
////                    case 0:
////                        w30S_sleepDataItem.setSleep_type("3");
////                        break;
////                    case 1:
////                        w30S_sleepDataItem.setSleep_type("2");
////                        break;
////                    case 2:
////                        w30S_sleepDataItem.setSleep_type("0");
////                        break;
////                    case 3:
////                        w30S_sleepDataItem.setSleep_type("4");
////                        break;
////                    case 4:
////                        w30S_sleepDataItem.setSleep_type("0");
////                        break;
////                    case 16:
////                        w30S_sleepDataItem.setSleep_type("5");
////                        break;
////                    case 17:
////                        w30S_sleepDataItem.setSleep_type("3");
////                        break;
////                    case 18:
////                        w30S_sleepDataItem.setSleep_type("1");
////                        break;
////                }
////                sleepDataLists.add(w30S_sleepDataItem);
////            }
////        }
//
//
//        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
//        H9SleepDBModelDao h9SleepDBModelDao = daoSession.getH9SleepDBModelDao();
//        if (h9SleepDBModelDao == null) return;
//
//        String timeYesterday = WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(), true);
//        List<H9SleepDBModel> sleepDBModels = h9SleepDBModelDao.loadAll();
//        if (sleepDBModels != null && !sleepDBModels.isEmpty()) {
//            List<W30S_SleepDataItem> sleepDataLists = new ArrayList<>();
//            for (int i = 0; i < sleepDBModels.size(); i++) {
//
//                Log.d(TAG, "----今日睡眠数据 AAA" + sleepDBModels.get(i).getRec() + "===" + sleepDBModels.get(i).getSleepTime() + "===" + sleepDBModels.get(i).getSleepType());
//                //判断值拿今天的数据
//                if (sleepDBModels.get(i).getRec().equals(WatchUtils.getCurrentDate())
//                        || sleepDBModels.get(i).getRec().equals(timeYesterday)) {
//                    if (sleepDBModels.get(i).getRec().equals(WatchUtils.getCurrentDate())) {
//                        if (Integer.valueOf(sleepDBModels.get(i).getSleepTime().substring(0, 2)) >= 10)
//                            return;
//                    } else if (sleepDBModels.get(i).getRec().equals(timeYesterday)) {
//                        if (Integer.valueOf(sleepDBModels.get(i).getSleepTime().substring(0, 2)) <= 22
//                                || Integer.valueOf(sleepDBModels.get(i).getSleepTime().substring(0, 2)) == 0)
//                            return;
//                    } else if (sleepDBModels.size() <= 2) {
//                        return;
//                    }
//                    W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();
//                    w30S_sleepDataItem.setStartTime(sleepDBModels.get(i).getSleepTime());
////                      if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
////                        Log.d("=====AAA==清醒", time + "-------" + time1);
////                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
////                    } else if (type.equals("2")) {  //潜睡状态
////                        Log.d("=====AAA==浅睡", time + "-------" + time1);
////                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
////                    } else if (type.equals("3")) {  //深睡
////                        Log.d("=====AAA==深睡", time + "-------" + time1);
////                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
////                    } else if (type.equals("88")) {  //深睡
////                        Log.d("=====AAA==自定义的起床状态", time + "-------" + time1);
////                        mDataTypeListenter.OnDataTypeListenter(type, time, "--");
////                    }
//                    // sleepData.sleep_type
//                    // 0：睡着
//                    // 1：浅睡
//                    // 2：醒着
//                    // 3：准备入睡
//                    // 4：退出睡眠
//                    // 16：进入睡眠模式
//                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
//                    //Log.d(TAG,"----今日睡眠数据 "+sleepDBModels.get(i).getDateTime()+"==="+sleepDBModels.get(i).getSleepTime()+"==="+sleepDBModels.get(i).getSleepType());
//                    switch (sleepDBModels.get(i).getSleepType()) {
//                        case 0:
//                            w30S_sleepDataItem.setSleep_type("3");
//                            break;
//                        case 1:
//                            w30S_sleepDataItem.setSleep_type("2");
//                            break;
//                        case 2:
//                            w30S_sleepDataItem.setSleep_type("0");
//                            break;
//                        case 3:
//                            w30S_sleepDataItem.setSleep_type("4");
//                            break;
//                        case 4:
//                            w30S_sleepDataItem.setSleep_type("0");
//                            break;
//                        case 16:
//                            w30S_sleepDataItem.setSleep_type("5");
//                            break;
//                        case 17:
//                            w30S_sleepDataItem.setSleep_type("3");
//                            break;
//                        case 18:
//                            w30S_sleepDataItem.setSleep_type("1");
//                            break;
//                    }
//                    sleepDataLists.add(w30S_sleepDataItem);
//                }
//            }
//
//
//            /**
//             * 上传所有睡眠数据  -- json串
//             */
//            if (!sleepDataLists.isEmpty()) {
//                JSONArray jsonArray = ProLogList2Json(sleepDataLists);
//                Log.e(TAG, "=====要上传的睡眠" + jsonArray.toString());
//
//                AppsBluetoothManager.getInstance(MyApp.getInstance())
//                        .sendCommand(new ClearSleepData(ResultCallback));
//                UpDatasBase.upDataTodaSleep(WatchUtils.getCurrentDate(), jsonArray);
//
//            }
//
//            //显示UI
//            setSleepUI(sleepDataLists);
//        }
//
//
//    }
//
//
//    /**
//     * 计算所睡眠的数据
//     *
//     * @param sleepDataLists
//     */
//    private void setSleepUI(List<W30S_SleepDataItem> sleepDataLists) {
//        //Log.d(TAG, "=========睡眠数据" + sleepDataLists.toString());
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            if (sleepDataLists.isEmpty()) {
//                w30S_sleepChart.setVisibility(View.GONE);
//                text_sleep_nodata.setVisibility(View.VISIBLE);
//            } else {
//                ALLTIME = 0;
//                DEEP = 0;
//                SHALLOW = 0;
//                AWAKE2 = 0;
//                AWAKE = 0;
//                for (int i = 0; i < sleepDataLists.size(); i++) {
//                    String startTime = null;
//                    String startTimeLater = null;
//                    String sleep_type = null;
//                    if (i >= (sleepDataLists.size() - 1)) {
//                        startTime = sleepDataLists.get(i).getStartTime();
//                        startTimeLater = sleepDataLists.get(i).getStartTime();
//                        sleep_type = sleepDataLists.get(i).getSleep_type() + "";
//                    } else {
//                        startTime = sleepDataLists.get(i).getStartTime();
//                        startTimeLater = sleepDataLists.get(i + 1).getStartTime();
//                        sleep_type = sleepDataLists.get(i).getSleep_type() + "";
//                    }
//                    String[] starSplit = startTime.split("[:]");
//                    String[] endSplit = startTimeLater.split("[:]");
//
//                    int startHour = Integer.valueOf(starSplit[0]);
//                    int endHour = Integer.valueOf(endSplit[0]);
//
//                    int startMin = Integer.valueOf(starSplit[1]);
//                    int endMin = (Integer.valueOf(endSplit[1]));
//                    if (startHour > endHour) {
//                        endHour = endHour + 24;
//                    }
//                    int all_m = (endHour - startHour) * 60 + (endMin - startMin);
//                    if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
//                        AWAKE2 += all_m;
//                        ALLTIME += all_m;
//                    } else if (sleep_type.equals("4")) {
//                        AWAKE2 += all_m;
//                        ALLTIME += all_m;
//                        AWAKE++;
//                    } else if (sleep_type.equals("2")) {
//                        //潜水
//                        SHALLOW += all_m;
//                        ALLTIME += all_m;
//                    } else if (sleep_type.equals("3")) {
//                        //深水
//                        DEEP += all_m;
//                        ALLTIME += all_m;
//                    }
//                }
//
//
//                w30S_sleepChart.setVisibility(View.VISIBLE);
//                text_sleep_nodata.setVisibility(View.GONE);
//                w30S_sleepChart.setBeanList(sleepDataLists);
//                //入睡时间
//                if (sleep_into_time != null) {
//                    sleep_into_time.setVisibility(View.VISIBLE);
//                    sleep_into_time.setText(sleepDataLists.get(0).getStartTime());
//                }
//                //醒来时间
//                if (sleep_out_time != null) {
//                    sleep_out_time.setVisibility(View.VISIBLE);
//                    sleep_out_time.setText(sleepDataLists.get(sleepDataLists.size() - 1).getStartTime());
//                }
//                //总睡眠设置为可拖动最大进度
//                if (SleepseekBar != null) {
//                    SleepseekBar.setMax(ALLTIME);
//                    SleepseekBar.setEnabled(true);
//                }
//                DecimalFormat df = new DecimalFormat("#.#");
//                df.setRoundingMode(RoundingMode.FLOOR);
//                String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).trim();
//                //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);
//                if (deepSleep != null) {
//                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
//                    deepSleep.setText(setScale + getResources().getString(R.string.hour));
//                }
//                if (shallowSleep != null) {
//                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
//                    double v = Double.valueOf(div3) - setScale;
//                    double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
//                    //double setScale = (double) WatchUtils.div((double) SHALLOW, 60, 1);
//                    shallowSleep.setText(setScale1 + getResources().getString(R.string.hour));
//                }
//                if (awakeSleep != null) {
//                    double setScale = (double) WatchUtils.div((double) AWAKE2, 60, 1);
//                    awakeSleep.setText(setScale + getResources().getString(R.string.hour));
//                }
//                double hour = (double) (DEEP + SHALLOW) / (double) 60;
//                String format = new DecimalFormat("0.00").format(hour);
//                String[] split = format.split("[.]");
//                int integer = Integer.valueOf(split[0].trim());
//                String s1 = String.valueOf(((hour - integer) * 60));
//                String[] split1 = s1.split("[.]");
//                String a = "0";
//                if (split1[0] != null) {
//                    a = split1[0].trim();
//                }
//                String w30ssleep = (String) SharedPreferencesUtils.getParam(getContext(), "w30ssleep", "8");
//                if (!WatchUtils.isEmpty(w30ssleep)) {
//                    int standardSleepAll = Integer.valueOf(w30ssleep.trim()) * 60;
//                    int allSleep = integer * 60 + Integer.valueOf(a);
//                    double standardSleep = WatchUtils.div(allSleep, standardSleepAll, 2);
//                    //int standar = (allSleep / standardSleepAll) * 100;
//                    String strings = String.valueOf((standardSleep * 100));
//                    if (textAllSleepData != null) {
//                        textAllSleepData.setVisibility(View.VISIBLE);
//                        if (textAllSleepData != null) {
//                            textAllSleepData.setVisibility(View.VISIBLE);
//                            if (strings.contains(".")) {
//                                textAllSleepData.setText(getResources().getString(R.string.sleep) + ":" + div3 + getResources().getString(R.string.hour)
//                                        + "    " + getResources().getString(R.string.string_standar) + ":" + strings.split("[.]")[0] + "%"
//                                        + "    " + getResources().getString(R.string.recovery_count_frequency) + ":" + AWAKE);
//                            } else {
//                                textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour)
//                                        + "    " + getResources().getString(R.string.string_standar) + (standardSleep * 100) + "%"
//                                        + "    " + getResources().getString(R.string.recovery_count) + AWAKE + getResources().getString(R.string.cishu));
//                            }
//                        }
//                    }
//                } else {
//                    if (textAllSleepData != null)
//                        textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour) + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
//                }
//
//
//                double v = SHALLOW + AWAKE2 + DEEP;
//                if (qianshuiT != null) {
//                    double v1 = WatchUtils.div(SHALLOW, v, 2);
//                    if (v1 > 0) {
//                        qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                    }
//                }
//                if (qingxingT != null) {
//                    double v1 = WatchUtils.div(AWAKE2, v, 2);
//                    if (v1 > 0) {
//                        qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                    }
//                }
//                if (shenshuiT != null) {
//                    double v1 = WatchUtils.div(DEEP, v, 2);
//                    if (v1 > 0) {
//                        shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                    }
//                }
//
//
//                String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
//                if (!TextUtils.isEmpty(homeTime)) {
//                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                    if (!WatchUtils.isEmpty(timeDifference)) {
//                        int number = Integer.valueOf(timeDifference.trim());
//                        int number2 = Integer.parseInt(timeDifference.trim());
//                        if (Math.abs(number) >= 3600 || Math.abs(number2) >= 3600) {
//                            String starTime = sleepDataLists.get(0).getStartTime();
//                            String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
//                            //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
//                            UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
//                            SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
//                        }
//                    }
//                } else {
//                    String starTime = sleepDataLists.get(0).getStartTime();
//                    String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
//                    //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
//                    UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
//                    SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
//
//                }
//
//            }
//        }
//    }
//
//
//    /**
//     * 读取上传详细步数
//     *
//     * @param userId
//     * @param bleMac
//     */
//    private void readStepUpdata(String userId, String bleMac) {
//
////        /**
////         * 这种方式见鬼了 查询不到数据
////         */
////
////        List<H9StepDBModel> stepDBModelList = MyApp.getInstance().getDBManager().getDaoSession().getH9StepDBModelDao()
////                .queryBuilder().where(H9StepDBModelDao.Properties.DevicesCode.eq(bleMac),
////                        H9StepDBModelDao.Properties.UserId.eq(userId),
////                        H9StepDBModelDao.Properties.Rec.eq(WatchUtils.getCurrentDate())).list();
////
////
////        if (upHeartBeanList == null) upHeartBeanList = new ArrayList<>();
////        else upHeartBeanList.clear();
////        Log.d("-G---设置", "----------有数据==" + WatchUtils.getCurrentDate() + "=" + (stepDBModelList == null ? "Y" : "N") + "=====" + stepDBModelList.size());
////        if (!stepDBModelList.isEmpty()) {
////            Log.d("-G---设置", "----------有数据==" + WatchUtils.getCurrentDate() + "===长度==" + stepDBModelList.size());
////            for (int i = 1; i <= stepDBModelList.size(); i++) {
////                upHeartBeanList.add(new UpHeartBean(userId, bleMac, "00",
////                        stepDBModelList.get(i - 1).getStepNumber() + "", (i > 9 ? i + "" : "0" + i) + ":00",
////                        "00", "0", "00", "00"));
////                Log.d("-G---设置", "----------步数占位时间==" + (i > 9 ? i + "" : "0" + i) + ":00");
////            }
////            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
////            Log.d("-G---要上传的步数", jsonArray1.toString());
////            UpDatasBase.upAllDataSteps(jsonArray1);
////        }
//
//
//        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
//        H9StepDBModelDao h9StepDBModelDao = daoSession.getH9StepDBModelDao();
//        if (h9StepDBModelDao == null) return;
//
//        /**
//         * 这个比较麻烦不是指定查询---查询的是所有数据
//         */
//        List<H9StepDBModel> stepDBModels =
//                h9StepDBModelDao.loadAll();
//        if (upHeartBeanList == null) upHeartBeanList = new ArrayList<>();
//        else upHeartBeanList.clear();
//
//        int times = 0;
//        if (!stepDBModels.isEmpty()) {
//            times = 0;
//            for (int i = 0; i < stepDBModels.size(); i++) {
//                if (stepDBModels.get(i).getRec().equals(WatchUtils.getCurrentDate())) {//判断只传今天
//                    times++;
////                    upHeartBeanList.add(new UpHeartBean(userId, bleMac, "00",
////                            stepDBModels.get(i).getStepNumber() + "",
////                            WatchUtils.getCurrentDate() + " " + (times > 9 ? times + "" : "0" + times) + ":00",
////                            "00", "0", "00", "00"));
//                    UpHeartBean upHeartBean = new UpHeartBean(userId, bleMac,
//                            0 , 0,
//                            0, 0,
//                            0, WatchUtils.getCurrentDate() + " " + (times > 9 ? times + "" : "0" + times) + ":00",
//                            stepDBModels.get(i).getStepNumber() );
//                    upHeartBeanList.add(upHeartBean);
//                    //Log.d("-G---设置", "----------步数占位时间==" + (times > 9 ? times + "" : "0" + times) + ":00");
//                }
//            }
//
//
//            if (upHeartBeanList != null && !upHeartBeanList.isEmpty()) {
//                //删除设备上详细运动数据
//                AppsBluetoothManager.getInstance(MyApp.getInstance())
//                        .sendCommand(new ClearSportData(ResultCallback));
//
//                JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
//                //Log.d("-G---要上传的步数", jsonArray1.toString());
//                UpDatasBase.upAllDataSteps(jsonArray1);
//            }
//
//        }
//
//    }
//
//
//    /**
//     * 读取心率数据---设置UI
//     *
//     * @param userId
//     * @param bleMac
//     */
//    private void readHeartH9DBDataToUI(String userId, String bleMac) {
//        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
//        H9HeartDBModelDao h9HeartDBModelDao = daoSession.getH9HeartDBModelDao();
//        if (h9HeartDBModelDao == null) return;
//        List<H9HeartDBModel> sportMapsList = h9HeartDBModelDao.queryBuilder().where(H9HeartDBModelDao.Properties.Rec.eq(WatchUtils.getCurrentDate()),
//                H9HeartDBModelDao.Properties.UserId.eq(userId),
//                H9HeartDBModelDao.Properties.DevicesCode.eq(bleMac)).list();
//        Log.d(TAG, "==========心率" + WatchUtils.getCurrentDate() + "---" + sportMapsList.size() + "===" + sportMapsList.toString());
//        if (heartList == null) heartList = new ArrayList<>();
//        else heartList.clear();
//        if (upHeartBeanList == null) upHeartBeanList = new ArrayList<>();
//        else upHeartBeanList.clear();
//        List<Integer> minHeart = null;
//        if (!sportMapsList.isEmpty()) {
//            if (getActivity() != null && !getActivity().isFinishing()) {
//                int allNumber = 0;
//                minHeart = new ArrayList<>();
//
//
//                /**
//                 * 原来保存的心率数据为24条  心率图标需要48条，转下
//                 */
//                h9DBCommont.updataHeartDeful(upHeartBeanList, heartList, userId, bleMac);
//
//                Log.d(TAG, "----心率数据 AAAS" + sportMapsList.size());
//                for (int i = 0; i < sportMapsList.size(); i++) {
//
//                    Log.e(TAG, "----心率数据 AAA" + i + "==" + sportMapsList.get(i).getRec() + "===" + sportMapsList.get(i).getDateTime() + "===" + sportMapsList.get(i).getHeartValue());
//                    if (sportMapsList.get(i).getHeartValue() > 0) {
//                        allNumber += sportMapsList.get(i).getHeartValue();
//                        minHeart.add(sportMapsList.get(i).getHeartValue());
//                        if (i * 2 <= heartList.size()) {
//                            heartList.set(i * 2, sportMapsList.get(i).getHeartValue());
////                            upHeartBeanList.set(i * 2, new UpHeartBean(userId,
////                                    bleMac, "00",
////                                    "00", WatchUtils.getCurrentDate() + " " + (i > 9 ? i + "" : "0" + i) + ":00",
////                                    sportMapsList.get(i).getHeartValue() + "", "0", "00", "00"));
//
//                            UpHeartBean upHeartBean = new UpHeartBean(userId, bleMac,
//                                    sportMapsList.get(i).getHeartValue(), 0,
//                                    0, 0,
//                                    0, WatchUtils.getCurrentDate() + " " + (i > 9 ? i + "" : "0" + i) + ":00",
//                                    0);
//                            upHeartBeanList.set(i * 2,upHeartBean);
//                        }
//                    }
//                }
//
//
//                //Log.d(TAG, "======心率总数居+++有效长度" + heartList.size());
//                int pjz = allNumber / (minHeart.size() == 0 ? 1 : minHeart.size());
//                int max = Collections.max(minHeart);
//                int min = Collections.min(minHeart);
//                maxHeartTextNumber.setText(max + "");
//                zuidiHeartTextNumber.setText(min + "");
//                autoHeartTextNumber.setText(pjz + "");
//
//                lineChart.setRateDataList(heartList);
//                lineChart.invalidate();
//                if (SleepseekBarHared != null)
//                    SleepseekBarHared.setEnabled(true);
//            }
//        }
//
//
//        /**
//         * 上传心率详细数据
//         */
//        if (upHeartBeanList != null && !upHeartBeanList.isEmpty()) {
//            AppsBluetoothManager.getInstance(MyApp.getInstance())
//                    .sendCommand(new ClearHeartData(ResultCallback));
//            AppsBluetoothManager.getInstance(MyApp.getInstance())
//                    .sendCommand(new ClearBloodData(ResultCallback));
//
//            JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
//            Log.d("-G---要上传的心率", jsonArray1.toString());
//            UpDatasBase.upAllDataHearte(jsonArray1);
//        }
//
//    }
//
//
//    List<UpHeartBean> upHeartBeanList = null;
//
//    /**
//     * 将 list 转换 为 JSONArray
//     *
//     * @param list
//     * @return
//     */
//    public JSONArray ProLogListJson(List<UpHeartBean> list) {
//        JSONArray json = new JSONArray();
//        for (UpHeartBean pLog : list) {
//            JSONObject jo = new JSONObject();
//            try {
//                jo.put("userId", pLog.getUserId());
//                jo.put("deviceCode", pLog.getDeviceCode());
//                jo.put("systolic", pLog.getSystolic());
//                jo.put("stepNumber", pLog.getStepNumber());
//                jo.put("date", pLog.getDate());
//                jo.put("heartRate", pLog.getHeartRate());
//                jo.put("status", pLog.getStatus());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            json.put(jo);
//        }
//        return json;
//    }
//
//    /**
//     * 将 list 转换 为 JSONArray
//     *
//     * @param list
//     * @return
//     */
//    public JSONArray ProLogList2Json(List<W30S_SleepDataItem> list) {
//        JSONArray json = new JSONArray();
//        for (W30S_SleepDataItem pLog : list) {
//            JSONObject jo = new JSONObject();
//            try {
//                jo.put("sleep_type", pLog.getSleep_type());
//                jo.put("startTime", pLog.getStartTime());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            json.put(jo);
//        }
//        return json;
//    }
//
//    /**
//     * @param value  心率值
//     * @param times  时间
//     * @param toTime 是否已经测量
//     */
//    @Override
//    public void OnDataTypeListenter(int value, String times, boolean toTime) {
//
//        Log.d("-----------AA", "华动中" + value + "====times=" + times);
//        if (frameLayout != null) frameLayout.setVisibility(View.VISIBLE);
//        if (toTime) {
//            if (textView != null) textView.setText("  " + value);
//        } else {
//            if (textView != null)
//                textView.setText("  " + getResources().getString(R.string.nodata));
//        }
//
//        if (heart_times != null) heart_times.setText(times);
//    }
//
//
//    /**
//     * 手动刷新
//     */
//    private class RefreshListenter implements SwipeRefreshLayout.OnRefreshListener {
//        @Override
//        public void onRefresh() {
////            switch (PAGES) {
////                case 0:
////                    //获取运动数据
////                    break;
////                case 1:
////                    //获取心率数据
////                    break;
////                case 2:
////                    //获取睡眠数据
////                    break;
////            }
//
//
//            if (MyApp.getInstance().isOne) {
//                MyApp.getInstance().isOne = false;
//                getAllDatas();
//            } else {
//                getDatas();
//            }
//
//            swipeRefresh.setRefreshing(false);
//        }
//    }
//
////    /*************   --------折线图----------    *************/
////
////
////    private void initLineCharts(LinkedList<HeartData> hdt) {
////        Axis axisX = new Axis(getAxisValuesX());
////        axisX.setShowText(true);
////        axisX.setAxisLineColor(Color.parseColor("#43FFFFFF"));
////        axisX.setAxisLineWidth(0.5f);
////        axisX.setTextColor(Color.WHITE);
////        axisX.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(true).setShowText(true);
////        Axis axisY = new Axis(getAxisValuesY());
////        axisY.setShowText(false);
////        axisY.setTextColor(Color.WHITE);
////        axisY.setAxisLineWidth(0f);
////        axisY.setShowLines(false);
////        axisY.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(false).setShowText(true);
////        leafLineChart.setAxisX(axisX);
////        leafLineChart.setAxisY(axisY);
////        List<Line> lines = new ArrayList<>();
////        lines.add(getFoldLineTest(hdt));
////        leafLineChart.setChartData(lines);
////        leafLineChart.showWithAnimation(1000);
////        leafLineChart.show();
////        leafLineChart.invalidate();
////    }
//
////    /**
////     * X轴值
////     *
////     * @return
////     */
////    private List<B18iAxisValue> getAxisValuesX() {
////        List<B18iAxisValue> b18iAxisValues = new ArrayList<>();
////
////        for (int i = 1; i < 24; i++) {
////            B18iAxisValue value = new B18iAxisValue();
////            if (i % 3 != 0) {
////                value.setLabel("");
////            } else {
////                value.setLabel(i + "");
////            }
//////            if (i % 3 != 0) {
//////                value.setLabel("");
//////            } else {
//////                value.setLabel(i + "");
//////            }
////            b18iAxisValues.add(value);
////        }
//////        for (int i = 0; i <= 8; i++) {
//////            B18iAxisValue value = new B18iAxisValue();
//////            value.setLabel(i * 3 + "");
//////            b18iAxisValues.add(value);
//////        }
//////            value.setLabel(i * 3 + "");
//////        for (int i = 0; i < heartRateDatas.size(); i++) {
//////            long timestamp = heartRateDatas.get(i).timestamp;
//////            Date date = new Date(timestamp);
//////            SimpleDateFormat sd = new SimpleDateFormat("HH");
//////            String format = sd.format(date);
//////        }
////        return b18iAxisValues;
////    }
////
////    /**
////     * Y轴值
////     *
////     * @return
////     */
////    private List<B18iAxisValue> getAxisValuesY() {
////        List<B18iAxisValue> b18iAxisValues = new ArrayList<>();
////        for (int i = 0; i < 3; i++) {
////            B18iAxisValue value = new B18iAxisValue();
////            value.setLabel("  ");
//////            if (i != 0) {
//////                value.setLabel(String.valueOf((i) * 50));
//////            } else {
//////                value.setLabel(" ");
//////            }
////            b18iAxisValues.add(value);
////        }
////        return b18iAxisValues;
////    }
//
////    //心率返回集合
////    LinkedList<HeartData> heartDatas;
////
////    /**
////     * 设置值
////     *
////     * @return
////     */
////    private Line getFoldLineTest(LinkedList<HeartData> heartDatas) {
////
////        List<PointValue> pointValues = new ArrayList<>();
////        List<String> timeString = new ArrayList<>();
////        List<Integer> heartString = new ArrayList<>();
////        String systemTimer = B18iUtils.getSystemTimer();
////        String s = B18iUtils.interceptString(systemTimer, 0, 10);
////
////        if (heartDatas != null) {
////            for (int i = 0; i < heartDatas.size(); i++) {
////                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartDatas.get(i).time_stamp));//yyyy/MM/dd HH:mm:ss
////                if (s.equals(B18iUtils.interceptString(strTimes, 0, 10))) {
//////                    Log.d(TAG, heartDatas.get(i).toString());
////                    if (heartDatas.get(i) != null) {
////                        int avg = heartDatas.get(i).heartRate_value;
////                        String sysTim = B18iUtils.interceptString(
////                                B18iUtils.getStrTimes(String.valueOf(heartDatas.get(i).time_stamp).trim()), 11, 13);
////                        if (!timeString.contains(sysTim)) {
////                            timeString.add(sysTim);
////                            heartString.add(avg);
////                        }
////                        Collections.sort(timeString);
////                    } else {
////                        if (heartString != null) {
////                            heartString.clear();
////                        }
////                        if (timeString != null) {
////                            timeString.clear();
////                        }
////                        for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
////                            heartString.add(0);
////                            timeString.add(j + "");
////                        }
////                    }
////                }
////            }
////        } else {
////            for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
////                heartString.add(0);
////                timeString.add(j + "");
////            }
////        }
////        for (int i = 0; i < timeString.size(); i++) {
////            PointValue value = new PointValue();
////            value.setX(Integer.valueOf(timeString.get(i)) / 23f);
////            value.setY(Integer.valueOf(heartString.get(i)) / 150f);
////            pointValues.add(value);
////        }
////
////        Line line = new Line(pointValues);
////        line.setLineColor(Color.parseColor("#FFFFFF"))
////                .setLineWidth(2f)
////                .setHasPoints(true)//是否显示点
////                .setPointColor(Color.WHITE)
////                .setCubic(false)
////                .setPointRadius(2)
////                .setFill(true)
////                .setFillColor(Color.parseColor("#FFFFFF"))
////                .setHasLabels(true)
////                .setLabelColor(Color.parseColor("#FF00FF"));//0C33B5E5
////        return line;
////    }
//
//    /******************        ---------  扇形----------               **********************/
//    int AWAKE2 = 0;//清醒
//    int AWAKE = 0;//清醒次数
//    int AOYE = 0;//熬夜
//    int DEEP = 0;//深睡
//    int SHALLOW = 0;//浅睡
//    int ALLTIME = 0;//浅睡
//    int NowVale = 0;//实时心率
//    int NowValeSize = 0;//实时心率有效长度
//    private boolean fanRoateAniamtionStart;
//    private String timeFromMillisecondA = "0";
//    private String timeFromMillisecondS = "0";
//    private String timeFromMillisecondD = "0";
//
//
////    public void setH9PieCharts() {
////        AWAKE = 0;
////        DEEP = 0;
////        SHALLOW = 0;
////        int AllSleep = 0;
////        boolean isSleeped = false;
////        boolean isIntoSleeped = false;
//////        boolean isOutSleep = false;
//////        boolean isOutSleepMode = false;
//////        boolean isOutSleepAuto = false;
////        if (sleepDatas != null) {
////            //当天日期
////            String soberLenTime = "08:00";
////            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
////            String currentDay = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取
//////            String nextDay = B18iUtils.getNextNumberDay(1);//前一天时间
////            Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
////            String nextDay = H9TimeUtil.getValidDateStr(dateBefore);
////            int size = sleepDatas.size();
////            for (int i = 0; i < size; i++) {
////                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
////                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
////                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
////                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
////                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
////                        String timeDifference = "0";
////                        if (0 < i) {
////                            timeDifference = H9TimeUtil.getTimeDifference
////                                    (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i - 1).sleep_time_stamp * 1000))
////                                            , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        } else {
////                            timeDifference = "0";
////                        }
////
////                        int SLEEPWAKE = 0;//苏醒次数
////                        int sleep_type = sleepDatas.get(i).sleep_type;
////                        // 0：睡着// 1：浅睡// 2：醒着// 3：准备入睡// 4：退出睡眠// 16：进入睡眠模式//
////                        // 17：退出睡眠模式（本次睡眠非预设睡眠）
////                        // 18：退出睡眠模式（本次睡眠为预设睡眠）
////                        if (sleep_type == 0) {//--------》睡着
////                            Log.e(TAG, "睡着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            AllSleep += Integer.valueOf(timeDifference);
////                            DEEP += Integer.valueOf(timeDifference);//睡着的分钟数
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 1) {//--------》浅睡
////                            Log.e(TAG, "浅睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            AllSleep += Integer.valueOf(timeDifference);
////                            SHALLOW += Integer.valueOf(timeDifference);//浅睡的分钟数
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 2) {//--------》醒着
////                            Log.e(TAG, "醒着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 3) {//--------》准备入睡着
////                            Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            if (!isIntoSleeped) {
////                                isIntoSleeped = true;
////                                isSleeped = true;
////                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
////                            }
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 4) {//--------》退出睡眠
////                            Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            SLEEPWAKE++;
////                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                            textSleepTime.setText(soberLenTime);
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 16) {//--------》进入睡眠模式
////                            Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            if (!isSleeped) {
////                                isSleeped = true;
////                                isIntoSleeped = true;
////                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
////                            }
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 17) {//--------》退出睡眠模式（本次睡眠非预设睡眠）
////                            Log.e(TAG, "退出睡眠模式==0=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            SLEEPWAKE++;
////                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                            textSleepTime.setText(soberLenTime);
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        } else if (sleep_type == 18) {//--------》退出睡眠模式（本次睡眠为预设睡眠）
////                            Log.e(TAG, "退出睡眠模式==1=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                            SLEEPWAKE++;
////                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                            textSleepTime.setText(soberLenTime);
////                            Log.d(TAG, "===========" + SLEEPWAKE);
////                        }
////                        Log.d(TAG, DEEP + "----------222--------" + SHALLOW + "==============" + AllSleep + "===" + SLEEPWAKE);
////                        //---------入睡时间-----苏醒次数--------苏醒时间
//////                        TextView textSleepInto, textSleepWake, textSleepTime;
////                        textSleepWake.setText(String.valueOf(SLEEPWAKE));//苏醒次数
////                    }
////                }
////            }
////            Log.d(TAG, DEEP + "----------121112--------" + SHALLOW + "==============" + AllSleep);
////            timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));//时长
////            timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOW * 60), 3600, 2));//浅睡
////            timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEP * 60), 3600, 2));//深睡
////            AWAKE = AllSleep - (DEEP + SHALLOW);//清醒
////            awakeSleep.setText(timeFromMillisecondA);//时长
////            shallowSleep.setText(timeFromMillisecondS);
////            deepSleep.setText(timeFromMillisecondD);
////
////
//////            for (int i = 0; i < sleepDatas.size(); i++) {
//////                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
//////                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
//////                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
////////                Log.d(TAG, timeDay + "------------------" + timeH);
//////                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
//////                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
//////
//////
//////                        for (int j = 1; j < sleepDatas.size(); j++) {
//////                            String timeDifference = H9TimeUtil.getTimeDifference
//////                                    (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000))
//////                                            , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j).sleep_time_stamp * 1000)));
//////
//////                            AllSleep += Integer.valueOf(timeDifference) / sleepDatas.size();
//////                            Log.d(TAG, "----------dsd---------:   " + timeDifference + "===" + sleepDatas.get(j - 1).sleep_type + "===" + sleepDatas.get(j).sleep_type);
//////                            int SLEEPWAKE = 0;//苏醒次数
//////                            if (sleepDatas.get(j - 1).sleep_type == 0) {//--------》睡着
//////                                DEEP += Integer.valueOf(timeDifference) / sleepDatas.size();//睡着的分钟数
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 1) {//--------》浅睡
//////                                SHALLOW += Integer.valueOf(timeDifference) / sleepDatas.size();//浅睡的分钟数
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 2) {//--------》醒着
//////
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 3) {//--------》准备入睡着
//////                                Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//////                                if (!isSleeped) {
//////                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16));
//////                                    isSleeped = true;
//////                                }
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 4) {//--------》退出睡眠
//////                                Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//////                                SLEEPWAKE++;
//////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
//////                                textSleepTime.setText(soberLenTime);
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 16) {//--------》进入睡眠模式
//////                                Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//////                            } else if (sleepDatas.get(j - 1).sleep_type == 17
//////                                    || sleepDatas.get(j - 1).sleep_type == 18) {//--------》退出睡眠模式（本次睡眠非预设睡眠）--------》退出睡眠模式（本次睡眠为预设睡眠）
//////                                Log.e(TAG, "退出睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//////                                SLEEPWAKE++;
//////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
//////                                textSleepTime.setText(soberLenTime);
//////                            }
////////                            if (sleepDatas.get(sleepDatas.size()).sleep_type == 18){
////////                                SLEEPWAKE++;
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                            }
//////                            Log.e(TAG, "苏醒次数：" + SLEEPWAKE);
////////                        textSleepInto, textSleepWake, textSleepTime;
//////                            //---------入睡时间-----苏醒次数--------苏醒时间
//////                            textSleepWake.setText(String.valueOf(SLEEPWAKE));
//////                        }
//////                    }
//////                }
//////            }
//////            for (int ii = 0; ii < sleepDatas.size() - 1; ii++) {
//////                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(ii).sleep_time_stamp));//时间戳转换
//////                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
//////                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
//////                Log.d(TAG, timeDay + "------------------" + timeH);
//////                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
//////                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
//////                        String timeDifference = H9TimeUtil.getTimeDifference
//////                                (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000))
//////                                        , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//////                        Log.d(TAG,"----------dsd----s-----"+timeDifference);
//////                        //睡眠类型：0x00：睡着， 0x01：浅睡，
//////                        // 0x02：醒着，0x03：准备入睡，
//////                        // 0x10（16）： 进入睡眠模式；0x11（17）：退出睡眠模式
//////                        /**
//////                         // sleepData.sleep_type
//////                         // 0：睡着
//////                         // 1：浅睡
//////                         // 2：醒着
//////                         // 3：准备入睡
//////                         // 4：退出睡眠
//////                         // 16：进入睡眠模式
//////                         // 17：退出睡眠模式（本次睡眠非预设睡眠）
//////                         // 18：退出睡眠模式（本次睡眠为预设睡眠）
//////                         */
//////
//////                        Log.d(TAG, "---ssssssssss----" + sleepDatas.get(0).sleep_type + "===========" + sleepDatas.get(ii + 1).sleep_type);
//////                        int SLEEPWAKE = 0;//苏醒次数
//////                        if (sleepDatas.get(ii + 1).sleep_type == 0) {//--------》睡着
//////                            DEEP += Integer.valueOf(timeDifference);//睡着的分钟数
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 1) {//--------》浅睡
//////                            SHALLOW += Integer.valueOf(timeDifference);//浅睡的分钟数
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 2) {//--------》醒着
//////
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 3) {//--------》准备入睡着
//////                            Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//////                            if (!isIntoSleeped) {
//////                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16));
//////                                isSleeped = true;
//////                                isIntoSleeped = true;
//////                            }
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 4) {//--------》退出睡眠
//////                            Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//////                            SLEEPWAKE++;
//////                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16);
//////                            textSleepTime.setText(soberLenTime);
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 16) {//--------》进入睡眠模式
//////                            Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//////                            if (!isSleeped) {
//////                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16));
//////                                isSleeped = true;
//////                                isIntoSleeped = true;
//////                            }
//////                        } else if (sleepDatas.get(ii + 1).sleep_type == 17
//////                                || sleepDatas.get(ii + 1).sleep_type == 18) {//--------》退出睡眠模式（本次睡眠非预设睡眠）--------》退出睡眠模式（本次睡眠为预设睡眠）
//////                            Log.e(TAG, "退出睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//////                            SLEEPWAKE++;
//////                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16);
//////                            textSleepTime.setText(soberLenTime);
//////                        }
//////
//////
////////                        switch (sleepDatas.get(ii + 1).sleep_type) {
////////                            case 0:
////////                                DEEP += Integer.valueOf(timeDifference);
////////                                break;
////////                            case 1:
////////                                SHALLOW += Integer.valueOf(timeDifference);
////////                                break;
////////                            case 3://准备入睡
////////                                if (!isSleep) {
////////                                    isSleep = true;
////////                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16));
////////                                }
////////                                Log.e(TAG, "入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                break;
////////                            case 4://退出睡眠
////////                                SLEEPWAKE++;
////////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                                break;
////////                            case 17:
////////                                SLEEPWAKE++;
////////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                                break;
////////                            case 18:
////////                                SLEEPWAKE++;
////////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                                break;
////////                            case 2:
////////                                AWAKE += Integer.valueOf(timeDifference);
////////                                break;
////////                        }
//////
////////                            if (sleepDatas.get(ii + 1).sleep_type == 0) {
////////                                DEEP += Integer.valueOf(timeDifference);
////////                            } else if (sleepDatas.get(ii + 1).sleep_type == 1) {
////////                                SHALLOW += Integer.valueOf(timeDifference);
////////                            } else if (sleepDatas.get(ii + 1).sleep_type == 3) {
////////                                if (!isSleeped) {
////////                                    isSleeped = true;
////////                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16));
////////                                }
////////                                Log.e(TAG, "入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                            } else if (sleepDatas.get(ii + 1).sleep_type == 4) {
////////                                SLEEPWAKE++;
////////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                            } else if (sleepDatas.get(ii + 1).sleep_type == 17 || sleepDatas.get(ii + 1).sleep_type == 18) {
////////                                SLEEPWAKE++;
////////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////////                                textSleepTime.setText(soberLenTime);
////////                            }
//////                        Log.e(TAG, "苏醒次数：" + SLEEPWAKE);
////////                        textSleepInto, textSleepWake, textSleepTime;
//////                        //---------入睡时间-----苏醒次数--------苏醒时间
//////                        textSleepWake.setText(String.valueOf(SLEEPWAKE));
//////
//////                    }
//////                }
//////            }
////
//////            timeFromMillisecondA = String.valueOf(WatchUtils.div(((DEEP + SHALLOW) * 60), 3600, 2));//时长
//////            timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOW * 60), 3600, 2));//浅睡
//////            timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEP * 60), 3600, 2));//深睡
//////            AWAKE = 720 - (DEEP + SHALLOW);//清醒
//////            timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));
////////            timeFromMillisecondA = B18iUtils.getTimeFromMillisecond((long) (AWAKE * (60 * 1000)));
////////            timeFromMillisecondS = B18iUtils.getTimeFromMillisecond((long) (SHALLOW * (60 * 1000)));
////////            timeFromMillisecondD = B18iUtils.getTimeFromMillisecond((long) (DEEP * (60 * 1000)));
//////            awakeSleep.setText(timeFromMillisecondA);//时长
//////            shallowSleep.setText(timeFromMillisecondS);
//////            deepSleep.setText(timeFromMillisecondD);
//////            Log.d(TAG, "s---time--深睡，浅睡,清醒-  --- 分钟（全天睡眠按照12小时）-" + DEEP + "===" + SHALLOW + "===" + AWAKE);
////            if (DEEP > 0 || SHALLOW > 0) {
//////                if (B18iCommon.ISUPDATASLEEP) {//判断，20分钟钟只可上传一次
////                String upSleepTime = (String) SharedPreferencesUtils.getParam(context, "upSleepTime", "");
//////                SharedPreferences sps = MyApp.getInstance().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//////                String upSleepTime = sps.getString("sss", "");
////                if (!TextUtils.isEmpty(upSleepTime)) {
////                    String timeDifference = H9TimeUtil.getTimeDifference(upSleepTime, B18iUtils.getSystemDataStart());
////                    if (!TextUtils.isEmpty(timeDifference)) {
////                        int number = Integer.valueOf(timeDifference.trim());
////                        int number2 = Integer.parseInt(timeDifference.trim());
//////                        Log.e(TAG, "睡眠上传---------" + number + "--" + number2 + "==" + timeDifference.compareTo("5"));
////                        if (number >= 5 || number2 >= 5) {
////
////                            Log.d(TAG, "----清醒时间" + soberLenTime);
//////                            Log.e(TAG, "睡眠上传-----in----" + number + "===前几天时间" + H9TimeUtil.getDateBefore(new Date(), 7) + "前一天时间" + B18iUtils.getNextDay());
////                            UpDatasBase.upDataSleep(String.valueOf(DEEP), String.valueOf(SHALLOW), "");//上传睡眠数据
////                            SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//////                            SharedPreferences sp = MyApp.getInstance().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//////                            SharedPreferences.Editor editor = sp.edit();
//////                            editor.putString("sss", B18iUtils.getSystemDataStart());
////                        }
////                    }
////                } else {
////                    UpDatasBase.upDataSleep(String.valueOf(DEEP), String.valueOf(SHALLOW), "");//上传睡眠数据
////                    SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//////                    SharedPreferences sp = MyApp.getInstance().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//////                    SharedPreferences.Editor editor = sp.edit();
//////                    editor.putString("sss", B18iUtils.getSystemDataStart());
////                }
////
//////                    B18iCommon.ISUPDATASLEEP = false;
//////                }
////            }
////        }
////
////        if (AWAKE <= 0) {
////            AWAKE = 720;
////        }
////        if (DEEP <= 0) {
////            DEEP = 0;
////        }
////        if (SHALLOW <= 0) {
////            SHALLOW = 0;
////        }
////        pieChartView.setFanClickAbleData(
////                new double[]{DEEP, SHALLOW, AWAKE},
////                new int[]{Color.parseColor("#4CFFFFFF"), Color.parseColor("#7FFFFFFF"), Color.WHITE}, 0.08);
////        pieChartView.setIsFistOffSet(false);
//////        pieChartView.setOnFanClick(new OnFanItemClickListener() {
//////            @Override
//////            public void onFanClick(final FanItem fanItem) {
//////                if (!fanRoateAniamtionStart) {
//////                    float to;
//////                    float centre = (fanItem.getStartAngle() * 2 + fanItem.getAngle()) / 2;
//////                    if (centre >= 270) {
//////                        to = 360 - centre + 90;
//////                    } else {
//////                        to = 90 - centre;
//////                    }
//////                    RotateAnimation animation = new RotateAnimation(0, to, pieChartView.getFanRectF().centerX(), pieChartView.getFanRectF().centerY());
//////                    animation.setDuration(800);
//////                    animation.setAnimationListener(new Animation.AnimationListener() {
//////
//////                        @Override
//////                        public void onAnimationStart(Animation animation) {
//////                            fanRoateAniamtionStart = true;
//////                        }
//////
//////                        @Override
//////                        public void onAnimationEnd(Animation animation) {
//////                            pieChartView.setToFirst(fanItem);
//////                            pieChartView.clearAnimation();
//////                            pieChartView.invalidate();
//////                            fanRoateAniamtionStart = false;
////////                            Toast.makeText(getContext(), "当前选中:" + fanItem.getPercent() + "%", Toast.LENGTH_SHORT).show();
//////                            Log.e(TAG, "----------------当前选中:" + fanItem.getPercent() + "%");
//////                        }
//////
//////                        @Override
//////                        public void onAnimationRepeat(Animation animation) {
//////                        }
//////                    });
//////                    animation.setFillAfter(true);
//////                    pieChartView.startAnimation(animation);
//////                }
//////            }
//////        });
////
////    }
//
//
////    //    View ----- 中的子控件
////    private CircleProgress circleprogress;
////    private LeafLineChart leafLineChart;
////    private PieChartView pieChartView;
////    TextView L38iCalT, L38iDisT;
////    LinearLayout autoHeartText;//心率
////    TextView autoDatatext;//数据
////    //-----清醒状态-------浅睡状态----深睡状态------清醒==改==》时常---浅睡----深睡
////    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep;
////    //---------入睡时间-----苏醒次数--------苏醒时间
////    TextView textSleepInto, textSleepWake, textSleepTime;
//
//
//    TextView L38iCalT, L38iDisT, textStepReach, t_mi, t_kcal;
//    TextView qingxingT, qianshuiT, shenshuiT, textTypeData;
//    TextView autoHeartTextNumber, maxHeartTextNumber, zuidiHeartTextNumber;//心率
//    //TextView autoDatatext;//数据
//    ImageView StepImageData, autoDataImage, SleepDatas;
//    //-----清醒状态-------浅睡状态----深睡状态------清醒==改==》时常---浅睡----深睡
//    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep, textAllSleepData;
//    //---------入睡时间-----苏醒次数--------苏醒时间
//    TextView textSleepInto, textSleepWake, textSleepTime;
//    W30S_SleepChart w30S_sleepChart;
//    SeekBar SleepseekBar, SleepseekBarHared;
//    FrameLayout frameLayout;
//    LinearLayout line_heart_datas;
//    TextView textView, heart_times;
//    TextView text_sleep_type, text_sleep_start, text_sleep_lines, text_sleep_end, sleep_into_time, sleep_out_time, text_sleep_nodata;
//    LinearLayout line_time_star_end;
//    private W30CusHeartView lineChart;
//    //心率图标数据
//    List<Integer> heartList;
//
//    /**
//     * view pager数据
//     */
////    private double DISTANCE = 0.0;//距离
////    private double CALORIES = 0.0;//卡路里
////    private int AUTOHEART = 0;
//
//
//    WaveProgress recordwaveProgressBar;
//    TextView watchRecordTagstepTv;
//    //目标选择列表
//    ArrayList<String> daily_numberofstepsList;
//
//    private void initStepList() {
//        daily_numberofstepsList = new ArrayList<>();
//        for (int i = 1; i < 100; i++) {
//            daily_numberofstepsList.add(String.valueOf(i * 1000));
//        }
//
//    }
//
//    private void setDatas() {
//        View viewSteps = LayoutInflater.from(context).inflate(R.layout.fragment_watch_record_change, null);
//        recordwaveProgressBar = (WaveProgress) viewSteps.findViewById(R.id.recordwave_progress_bar);
//        L38iCalT = (TextView) viewSteps.findViewById(R.id.watch_recordKcalTv);
//        L38iDisT = (TextView) viewSteps.findViewById(R.id.watch_recordMileTv);
//        StepImageData = (ImageView) viewSteps.findViewById(R.id.stepData_imageView);
//        t_mi = (TextView) viewSteps.findViewById(R.id.t_mi);
//        t_kcal = (TextView) viewSteps.findViewById(R.id.t_kcal);
//
//        t_kcal.setText("cal");
//        // 0位公制 1为英制
//        kmormi = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);//公英制
//        // 0位公制 1为英制
//        if (kmormi == 0) {
//            t_mi.setText("km");
//        } else {
//            t_mi.setText("mi");
//        }
//        StepImageData.setVisibility(View.VISIBLE);
//        StepImageData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context,
//                        StepHistoryDataActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//        });
//        String w30stag = (String) SharedPreferencesUtils.getParam(context, "w30stag", "10000");
//        GOAL = Integer.valueOf(w30stag);
//        recordwaveProgressBar.setMaxValue(GOAL);
//        recordwaveProgressBar.setValue(STEP);
//        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
//        recordwaveProgressBar.setTagStepStr(getResources().getString(R.string.settarget_steps) + tagGoal);
//        textStepReach = (TextView) viewSteps.findViewById(R.id.text_step_reach);
//        watchRecordTagstepTv = (TextView) viewSteps.findViewById(R.id.watch_recordTagstepTv);
//        watchRecordTagstepTv.setVisibility(View.GONE);
//
//
//        View viewHeart = LayoutInflater.from(context).inflate(R.layout.b18i_leaf_linechart_view, null);
//        lineChart = (W30CusHeartView) viewHeart.findViewById(R.id.heart_chart);
//        line_heart_datas = (LinearLayout) viewHeart.findViewById(R.id.line_heart_datas);
//        textTypeData = (TextView) viewHeart.findViewById(R.id.data_type_text);
//        SleepseekBarHared = (SeekBar) viewHeart.findViewById(R.id.seek_bar_my_heart);
//        frameLayout = (FrameLayout) viewHeart.findViewById(R.id.frm_heard);
//        textView = (TextView) viewHeart.findViewById(R.id.heard_value);
//        heart_times = (TextView) viewHeart.findViewById(R.id.heart_times);
//        SleepseekBarHared.setEnabled(false);
//        SleepseekBarHared.setMax(Math.abs(51));
//        lineChart.setmDataTypeListenter(this);
//        lineChart.setType("H9");
//        SleepseekBarHared.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (lineChart != null) lineChart.setSeekBar((float) progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                boolean clickable = SleepseekBar.isClickable();
//                if (!clickable) {
//                    if (SleepseekBarHared != null) {
//                        SleepseekBarHared.setProgress(0);
//                        SleepseekBarHared.clearAnimation();
//                        SleepseekBarHared.invalidate();
//                    }
//                    if (lineChart != null) lineChart.setSeekBar(0);
//                    if (frameLayout != null) frameLayout.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        });
//
//        textTypeData.setVisibility(View.GONE);
//        viewHeart.findViewById(R.id.leaf_chart).setVisibility(View.GONE);
//        viewHeart.findViewById(R.id.heart_lines).setVisibility(View.GONE);
//        autoHeartTextNumber = (TextView) viewHeart.findViewById(R.id.autoHeart_text_number);//平均心率---可点击
//        maxHeartTextNumber = (TextView) viewHeart.findViewById(R.id.maxHeart_text_number);//最高心率---可点击
//        zuidiHeartTextNumber = (TextView) viewHeart.findViewById(R.id.zuidiHeart_text_number);//最低心率---可点击
//        viewHeart.findViewById(R.id.autoData_text).setVisibility(View.GONE);
//        //autoDatatext = (TextView) view2.findViewById(R.id.autoData_text);//数据---可点击
//        autoDataImage = (ImageView) viewHeart.findViewById(R.id.autoData_imageView);//历史数据---可点击
//        autoDataImage.setVisibility(View.VISIBLE);
//        autoDataImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context,
//                        H9HearteDataActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//        });
//        //点击是事件
//        line_heart_datas.setOnClickListener(new MyViewLister());
//
//
//        View viewSleep = LayoutInflater.from(context).inflate(R.layout.w30s_pie_chart_view, null);
//        w30S_sleepChart = (W30S_SleepChart) viewSleep.findViewById(R.id.sleep_chart);
//        text_sleep_nodata = (TextView) viewSleep.findViewById(R.id.text_sleep_nodata);
//        w30S_sleepChart.setVisibility(View.INVISIBLE);
//        text_sleep_nodata.setVisibility(View.VISIBLE);
//        SleepseekBar = (SeekBar) viewSleep.findViewById(R.id.seek_bar_my);
//        text_sleep_type = (TextView) viewSleep.findViewById(R.id.text_sleep_type);
//        text_sleep_start = (TextView) viewSleep.findViewById(R.id.text_sleep_start);
//        text_sleep_lines = (TextView) viewSleep.findViewById(R.id.text_sleep_lines);
//        text_sleep_end = (TextView) viewSleep.findViewById(R.id.text_sleep_end);
//        sleep_into_time = (TextView) viewSleep.findViewById(R.id.sleep_into_time);
//        sleep_out_time = (TextView) viewSleep.findViewById(R.id.sleep_out_time);
//        line_time_star_end = (LinearLayout) viewSleep.findViewById(R.id.line_time_star_end);
//        //line_time_star_end.setVisibility(View.GONE);
//        text_sleep_type.setText(" ");
//        text_sleep_start.setText(" ");
//        if (MyApp.getInstance().AppisOneStar) {
//            MyApp.getInstance().AppisOneStar = false;
//            text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));
//        } else {
//            text_sleep_lines.setText(" ");
//        }
//        text_sleep_end.setText(" ");
//        SleepseekBar.setEnabled(false);
//        SleepseekBar.setMax(Math.abs(500));
//        w30S_sleepChart.setmDataTypeListenter(new W30S_SleepChart.DataTypeListenter() {
//            @Override
//            public void OnDataTypeListenter(String type, String startTime, String endTime) {
//
//                if (text_sleep_lines != null) text_sleep_lines.setText(" -- ");
//                if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
//                    text_sleep_type.setText(getResources().getString(R.string.waking_state));
//                    text_sleep_start.setText(startTime);
//                    text_sleep_end.setText(endTime);
//                } else if (type.equals("2")) {  //潜睡状态
//                    text_sleep_type.setText(getResources().getString(R.string.shallow_sleep));
//                    text_sleep_start.setText(startTime);
//                    text_sleep_end.setText(endTime);
//                } else if (type.equals("3")) {  //深睡
//                    text_sleep_type.setText(getResources().getString(R.string.deep_sleep));
//                    text_sleep_start.setText(startTime);
//                    text_sleep_end.setText(endTime);
//                } else if (type.equals("88")) {
//                    Log.d("----------", "起床");
//                    text_sleep_type.setText(getResources().getString(R.string.getup));
//                    text_sleep_start.setText(startTime);
//                    text_sleep_end.setText(endTime);
//                }
//            }
//        });
//
//        SleepseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d("========", progress + "");
//                w30S_sleepChart.setSeekBar((float) progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                boolean clickable = SleepseekBar.isClickable();
//                if (!clickable) {
//                    SleepseekBar.setProgress(0);
//                    SleepseekBar.clearAnimation();
//                    SleepseekBar.invalidate();
//                    w30S_sleepChart.setSeekBar(0);
//                    if (text_sleep_type != null) text_sleep_type.setText(" ");
//                    if (text_sleep_start != null) text_sleep_start.setText(" ");
//                    if (text_sleep_lines != null) text_sleep_lines.setText(" ");
//                    if (text_sleep_end != null) text_sleep_end.setText(" ");
//                }
//
//            }
//        });
//        awakeState = (TextView) viewSleep.findViewById(R.id.awakeState);
//        shallowState = (TextView) viewSleep.findViewById(R.id.shallowState);
//        deepState = (TextView) viewSleep.findViewById(R.id.deepState);
//        textAllSleepData = (TextView) viewSleep.findViewById(R.id.text_all_sleep_data);
//        SleepDatas = (ImageView) viewSleep.findViewById(R.id.sleepData_imageView);
//        SleepDatas.setVisibility(View.VISIBLE);
//        SleepDatas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context,
//                        SleepHistoryActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
//            }
//        });
//        awakeSleep = (TextView) viewSleep.findViewById(R.id.awake_sleep);
//        shallowSleep = (TextView) viewSleep.findViewById(R.id.shallow_sleep);
//        deepSleep = (TextView) viewSleep.findViewById(R.id.deep_sleep);
//
//        qingxingT = (TextView) viewSleep.findViewById(R.id.w30_qingxing_text);
//        qianshuiT = (TextView) viewSleep.findViewById(R.id.w30_qianshui_text);
//        shenshuiT = (TextView) viewSleep.findViewById(R.id.w30_shenshui_text);
//
//        textSleepInto = (TextView) viewSleep.findViewById(R.id.text_sleep_into);//入睡时间
//        textSleepWake = (TextView) viewSleep.findViewById(R.id.text_sleep_wake);//苏醒次数
//        textSleepTime = (TextView) viewSleep.findViewById(R.id.text_sleep_time);//苏醒时间
//        awakeSleep.setText(timeFromMillisecondA);
//        shallowSleep.setText(timeFromMillisecondS);
//        deepSleep.setText(timeFromMillisecondD);
//        awakeState.setText(getResources().getString(R.string.string_qingxing));//清醒状态
//        shallowState.setText(getResources().getString(R.string.sleep_light));//浅睡眠
//        deepState.setText(getResources().getString(R.string.sleep_deep));//深睡眠
//        List<View> fragments = new ArrayList<>();
//        fragments.add(viewSteps);
//        fragments.add(viewHeart);
//        fragments.add(viewSleep);
//        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
//        l38iViewpager.setOffscreenPageLimit(3);
//        //l38iViewpager.setCurrentItem(3);
//        setLinePontion(fragments);
//        l38iViewpager.setAdapter(adapter);
//        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
//
////
////        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_watch_record_change, null, false);
////        recordwaveProgressBar = (WaveProgress) mView.findViewById(R.id.recordwave_progress_bar);
////        L38iCalT = (TextView) mView.findViewById(R.id.watch_recordKcalTv);
////        L38iDisT = (TextView) mView.findViewById(R.id.watch_recordMileTv);
//////        mView.findViewById(R.id.watch_lines).setVisibility(View.GONE);
//////        mView.findViewById(R.id.watch_record_swipe).setEnabled(false);
////        L38iCalT.setText("" + CALORIES + "");
////        L38iDisT.setText("" + DISTANCE + "");
////        if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
////            recordwaveProgressBar.setMaxValue(GOAL);
////            recordwaveProgressBar.setValue(STEP);
////        }
////        watchRecordTagstepTv = (TextView) mView.findViewById(R.id.watch_recordTagstepTv);
////        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
////        watchRecordTagstepTv.setText(getResources().getString(R.string.settarget_steps) + tagGoal);
//////        setTagSteps();
//////        View view1 = LayoutInflater.from(context).inflate(R.layout.b18i_circle_progress_view, null, false);
//////        circleprogress = (CircleProgress) view1.findViewById(R.id.circleprogress);
//////        L38iCalT = (TextView) view1.findViewById(R.id.l38i_recordKcalTv);
//////        L38iDisT = (TextView) view1.findViewById(R.id.l38i_recordMileTv);
//////        L38iCalT.setText("" + CALORIES + "");
//////        L38iDisT.setText("" + DISTANCE + "");
//////        circleprogress.reset();
//////        circleprogress.setMaxValue(GOAL);
//////        circleprogress.setValue(STEP);
//////        circleprogress.setPrecision(0);
////        View view2 = LayoutInflater.from(context).inflate(R.layout.b18i_leaf_linechart_view, null, false);
////        leafLineChart = (LeafLineChart) view2.findViewById(R.id.leaf_chart);
////        autoHeartText = (LinearLayout) view2.findViewById(R.id.line_heart_datas);//心率---可点击
////        autoDatatext = (TextView) view2.findViewById(R.id.autoData_text);//数据---可点击
//////        autoHeartText.setText(String.valueOf(AUTOHEART));
////        autoHeartText.setOnClickListener(new MyViewLister());
////        autoDatatext.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                startActivity(new Intent(context,
////                        H9HearteDataActivity.class)
////                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
////            }
////        });
////        View view3 = LayoutInflater.from(context).inflate(R.layout.b18i_pie_chart_view, null, false);
////        pieChartView = (PieChartView) view3.findViewById(R.id.pieChartView);
////        awakeState = (TextView) view3.findViewById(R.id.awakeState);
////        shallowState = (TextView) view3.findViewById(R.id.shallowState);
////        deepState = (TextView) view3.findViewById(R.id.deepState);
////        awakeSleep = (TextView) view3.findViewById(R.id.awake_sleep);
////        shallowSleep = (TextView) view3.findViewById(R.id.shallow_sleep);
////        deepSleep = (TextView) view3.findViewById(R.id.deep_sleep);
////        textSleepInto = (TextView) view3.findViewById(R.id.text_sleep_into);//入睡时间
////        textSleepWake = (TextView) view3.findViewById(R.id.text_sleep_wake);//苏醒次数
////        textSleepTime = (TextView) view3.findViewById(R.id.text_sleep_time);//苏醒时间
////        awakeSleep.setText(timeFromMillisecondA);
////        shallowSleep.setText(timeFromMillisecondS);
////        deepSleep.setText(timeFromMillisecondD);
////        awakeState.setText(getResources().getString(R.string.waking_state));//清醒状态
////        shallowState.setText(getResources().getString(R.string.shallow_sleep));//浅睡眠
////        deepState.setText(getResources().getString(R.string.deep_sleep));//深睡眠
////        List<View> fragments = new ArrayList<>();
////        fragments.add(mView);
//////        fragments.add(view1);
////        fragments.add(view2);
////        fragments.add(view3);
////        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
////        l38iViewpager.setCurrentItem(3);
////        setLinePontion(fragments);
////        l38iViewpager.setAdapter(adapter);
////        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
//    }
//
//
////    //设置目标步数
////    private void setTagSteps() {
////        ProfessionPick stepsnumber = new ProfessionPick.Builder(getContext(), new ProfessionPick.OnProCityPickedListener() {
////            @Override
////            public void onProCityPickCompleted(String profession) {
////                //设置步数
//////                Log.e("-----目标步数", profession + "");
////
////                int st = Integer.valueOf(profession) / 100;
////                // 50*100
////                AppsBluetoothManager.getInstance(MyApp.getInstance())
////                        .sendCommand(new GoalsSetting(commandResultCallback, (byte) 0, st, (byte) 0));//目标步数
////
////                //设置目标步数
////                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + profession);
////                recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
//////                SharedPreferencesUtils.setParam(getActivity(), "settagsteps", profession);
//////                recordwaveProgressBar.setValue(Float.valueOf((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")));
////            }
////        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
////                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
////                .btnTextSize(16) // button text size
////                .viewTextSize(25) // pick view text size
////                .colorCancel(Color.parseColor("#999999")) //color of cancel button
////                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
////                .setProvinceList(daily_numberofstepsList) //min year in loop
////                .dateChose(String.valueOf(GOAL)) // date chose when init popwindow
////                .build();
////        stepsnumber.showPopWin(getActivity());
////    }
//
//    /**
//     * 滑动小圆点
//     *
//     * @param fragments
//     */
//    private void setLinePontion(List<View> fragments) {
//        for (int i = 0; i < fragments.size(); i++) {
//            ImageView imageView = new ImageView(context);
//            imageView.setPadding(3, 0, 3, 0);
//            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
//            if (i == 0) {
//                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
//            }
//            imageView.setMaxHeight(1);
//            imageView.setMaxWidth(1);
//            imageView.setMinimumHeight(1);
//            imageView.setMinimumWidth(1);
//            linePontion.addView(imageView);
//        }
//    }
//
//    /**
//     * 内部Adapter
//     */
//    public class MyHomePagerAdapter extends PagerAdapter {
//        List<View> stringList;
//
//        public MyHomePagerAdapter(List<View> stringList) {
//            this.stringList = stringList;
//        }
//
//        @Override
//        public int getCount() {
//            return stringList.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(stringList.get(position));
//            return stringList.get(position);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(stringList.get(position));
//        }
//    }
//
//    /**
//     * ViewPager页面改变监听
//     */
//    private class PagerChangeLister implements ViewPager.OnPageChangeListener {
//        private List<View> fragments;
//
//        public PagerChangeLister(List<View> fragments) {
//            this.fragments = fragments;
//        }
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            PAGES = position;
//            PointSetting(position);
//            if (position == 1) {
//                if (lineChart != null) {
//                    lineChart.invalidate();
//                }
//            }
////            switch (position) {
////                case 0:
////                    circleprogress.reset();
////                    circleprogress.setMaxValue(GOAL);
////                    circleprogress.setValue(STEP);
////                    circleprogress.setPrecision(0);
////                    circleprogress.invalidate();
////                    break;
////                case 1:
////                    //获取心率数据
////                    initLineChart();
//////                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//////                            .sendCommand(new GetHeartData(commandResultCallback, 0, new Date().getTime() / 1000, (int) GlobalVarManager.getInstance().getHeartRateCount()));
////                    break;
////                case 2:
////                    //获取睡眠数据
////                    setPieChart();
////                    //扇形图
//////                    setPieCharts();
//////                    setH9PieCharts();
////                    break;
////            }
//        }
//
//        private void PointSetting(int position) {
//            l38iViewpager.setCurrentItem(position);
//            for (int j = 0; j < fragments.size(); j++) {
//                ImageView childAt1 = (ImageView) linePontion.getChildAt(j);
//                childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
//                childAt1.setMaxHeight(1);
//                childAt1.setMaxWidth(1);
////                childAt1.setAlpha(80);
//            }
//            ImageView childAt = (ImageView) linePontion.getChildAt(position);
//            childAt.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
//            childAt.setMaxHeight(1);
//            childAt.setMaxWidth(1);
////            childAt.setAlpha(225);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//        }
//    }
//
//
////    /**
////     * 获取心率数据
////     */
////    private void initLineChart() {  //System.currentTimeMillis()
////        AppsBluetoothManager.getInstance(context)
////                .sendCommand(new AllDataCount(commandResultCallback));//获取全部数据的条数
////    }
////
////    /**
////     * 获取睡眠数据
////     */
////    private void setPieChart() {
////        //获取睡眠条数
////        AppsBluetoothManager.getInstance(context)
////                .sendCommand(new SportSleepCount(new BaseCommand.CommandResultCallback() {
////                    @Override
////                    public void onSuccess(BaseCommand command) {
//////                        Log.d(TAG, "SleepCount:" + GlobalVarManager.getInstance().getSleepCount());
////                        // 获取睡眠数据详情需要传入睡眠数据条数，所以在获取睡眠数据详情之前必需先获取睡眠数据条数。
////                        // 如果睡眠条数为0，则睡眠详情数必定为0。所以如果获取到睡眠条数为0，则没有必要再去获取睡眠详情数据。
//////                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//////                                .sendCommand(new GetSleepData(commandResultCallback, 0, new Date().getTime() / 1000, (int) GlobalVarManager.getInstance().getSleepCount()));
////                        //获取睡眠数据
////                        AppsBluetoothManager.getInstance(context)
////                                .sendCommand(new GetSleepData(commandResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
////                    }
////
////                    @Override
////                    public void onFail(BaseCommand command) {
//////                        Log.d(TAG, "睡眠条数获取失败");
////                    }
////                }, 1, 0));
////    }
//
//    @OnClick({R.id.watch_poorRel, R.id.battery_watchRecordShareImg})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.watch_poorRel:    //点击是否连接
//                if (MyCommandManager.DEVICENAME != null) {    //已连接
////                    startActivity(new Intent(getActivity(), B18ISettingActivity.class).putExtra("is18i", "H9"));
//                } else {
//                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
//                    getActivity().finish();
//                }
//                break;
//            case R.id.battery_watchRecordShareImg:  //分享
//                startActivity(new Intent(context, SharePosterActivity.class).putExtra("is18i", "H9"));
//                break;
//        }
//    }
//
//    //分享
//    private void doShareClick() {
//        Date timedf = new Date();
//        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String xXXXdf = formatdf.format(timedf);
//        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
//        ScreenShot.shoot(getActivity(), new File(filePath));
//        Common.showShare(getActivity(), null, false, filePath);
//    }
//
//
////    private static Handler myHandler;
//
////    private static final int MessageNumber = 1008611;
//
////    /**
////     * 自动同步数据
////     */
////    public void SynchronousData() {
////        RefreshBroadcastReceivers.setMyCallBack(new RefreshBroadcastReceivers.MyCallBack() {
////            @Override
////            public void setMyCallBack(Message msg) {
////                if (msg.what == RefreshBroadcastReceivers.MessageNumber) {
////                    if (isHidden) {
////                        textStute.setText(context.getString(R.string.syncy_data));
////                        textStute.setVisibility(View.VISIBLE);
////                    }
////                    Log.d(TAG, "H9同步成功");
////                    getDatas();
////                    RefreshBroadcastReceivers.getMyHandler().removeMessages(RefreshBroadcastReceivers.MessageNumber);
////                }
////            }
////        });
//
////        myHandler = new Handler(new Handler.Callback() {
////            @Override
////            public boolean handleMessage(Message msg) {
////                if (msg.what == MessageNumber) {
////                    if (isHidden) {
////                        textStute.setText(context.getString(R.string.syncy_data));
////                        textStute.setVisibility(View.VISIBLE);
////                    }
////                    getDatas();
////                    myHandler.removeMessages(MessageNumber);
////                }
////                return false;
////            }
////        });
////    }
//
////    /**
////     * 接受同步广播提醒
////     */
////    public static class RefreshBroadcastReceiver extends B18IBroadcastReceiver {
////
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            super.onReceive(context, intent);
////            Log.e("-------", "同步数据广播提醒来了");
////            if (MyCommandManager.DEVICENAME != null) {
//////                myHandler.sendEmptyMessage(MessageNumber);
////                RefreshBroadcastReceivers.getMyHandler().sendEmptyMessage(MessageNumber);
////            }
////        }
////    }
//
//    private BroadcastReceiver broadReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action != null) {
//
//                try {
//                    String h9Redata = intent.getStringExtra("h9constate");
//                    if (!WatchUtils.isEmpty(h9Redata)) {
//                        if (h9Redata.equals("conn") && !getActivity().isFinishing()) {    //已链接
//                            MyCommandManager.DEVICENAME = "H9";
//                        } else {
//                            MyCommandManager.DEVICENAME = null;
//                        }
//                        getAllDatas();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
////                try {
////                    String h9Redata = intent.getStringExtra("h9constate");
////                    if (!WatchUtils.isEmpty(h9Redata)) {
////                        if (h9Redata.equals("conn") && !getActivity().isFinishing()) {    //已链接
////                            batteryLayout.setVisibility(View.VISIBLE);
////                            MyCommandManager.DEVICENAME = "H9";
////                            watchConnectStateTv.setText("" + "connect" + "");
////                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
////                            AnimationUtils.stopFlick(watchConnectStateTv);
////                            if (isHidden) {
////                                textStute.setText(getResources().getString(R.string.connted));
////                                textStute.setVisibility(View.INVISIBLE);
////                            }
////                            if (MyApp.getInstance().isOne) {
////                                MyApp.getInstance().isOne = false;
////                                textStute.setVisibility(View.VISIBLE);
////                                textStute.setText(getResources().getString(R.string.syncy_data));
////                                getAllDatas();
////                            }
////                        } else {
////                            MyCommandManager.DEVICENAME = null;
////                            batteryLayout.setVisibility(View.INVISIBLE);
////                            MyApp.getInstance().isOne = true;
////                            watchConnectStateTv.setText("" + "disconn.." + "");
////                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
////                            AnimationUtils.startFlick(watchConnectStateTv);
////                            if (isHidden) {
////                                textStute.setText(getResources().getString(R.string.disconnted));
////                                textStute.setVisibility(View.VISIBLE);
////                            }
////                        }
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//            }
//        }
//    };
//
//
////    /**
////     * 蓝牙回调
////     */
////    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
////        @Override
////        public void onSuccess(BaseCommand baseCommand) {
//////            Log.e(TAG, "-----baseCommand----" + baseCommand.toString());
////            if (baseCommand instanceof DeviceDisplaySportSleep) {//获取当天运动汇总
////                Log.d(TAG, "步数:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
////                        "\n 卡路里:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
////                        "\n 距离:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
////                        "\n 睡眠时间:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
////                STEP = GlobalVarManager.getInstance().getDeviceDisplayStep();
////                if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
////                    recordwaveProgressBar.setMaxValue(GOAL);
////                    recordwaveProgressBar.setValue(STEP);
////                }
////                String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
////                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
//////                circleprogress.reset();
//////                circleprogress.setMaxValue(GOAL);
//////                circleprogress.setValue(STEP);
//////                circleprogress.invalidate();
////                CALORIES = WatchUtils.div(Double.valueOf(GlobalVarManager.getInstance().getDeviceDisplayCalorie()), 1000, 2);
////                DISTANCE = WatchUtils.div(Double.valueOf(GlobalVarManager.getInstance().getDeviceDisplayDistance()), 1000, 2);
//////                double tempDis = DISTANCE;
//////                if(kmormi == 1){    //公制
//////                    L38iCalT.setText("" + tempDis + "");
//////                }else{  //英制
//////                    L38iCalT.setText("" + WatchUtils.kmToMi(tempDis) + "");
//////                }
////                L38iCalT.setText("" + CALORIES + "");
////                L38iDisT.setText("" + DISTANCE + "");
//////                if (B18iCommon.ISUPDATASPROT) {//判断，20分钟钟只可上传一次
//////                    //上传运动数据到后台
//////                    updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE);
//////                    B18iCommon.ISUPDATASPROT = false;
//////                }
////                String upStepTime = (String) SharedPreferencesUtils.getParam(context, "upStepTime", "");
//////                SharedPreferences sps = MyApp.getInstance().getSharedPreferences("stepdatas", Context.MODE_PRIVATE);
//////                String upStepTime = sps.getString("sss", "");
////                if (!TextUtils.isEmpty(upStepTime)) {
////                    String timeDifference = H9TimeUtil.getTimeDifference(upStepTime, B18iUtils.getSystemDataStart());
////                    if (!TextUtils.isEmpty(timeDifference)) {
////                        int number = Integer.valueOf(timeDifference.trim());
////                        int number2 = Integer.parseInt(timeDifference.trim());
//////                        Log.e(TAG, "步数上传---------" + number);
////                        if (number >= 5 && number2 >= 5) {
//////                            Log.e(TAG, "步数上传----in-----" + number);
////                            //上传运动数据到后台
////                            UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, "");
////                            SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
////                        }
////                    }
////                } else {
////                    //上传运动数据到后台
////                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, "");
////                    SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
////                }
////
////                //获取目标
////                AppsBluetoothManager.getInstance(context)
////                        .sendCommand(new GoalsSetting(new BaseCommand.CommandResultCallback() {
////                            @Override
////                            public void onSuccess(BaseCommand command) {
////                                Log.d(TAG, "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
////                                        "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
////                                        "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
////                                        "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());
////                                GOAL = GlobalVarManager.getInstance().getStepGoalsValue();
////                                if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
////                                    recordwaveProgressBar.setMaxValue(GOAL);
////                                    recordwaveProgressBar.setValue(STEP);
////                                }
////                                String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
////                                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
//////                                circleprogress.reset();
//////                                circleprogress.setMaxValue(GOAL);
//////                                circleprogress.setValue(STEP);
//////                                circleprogress.invalidate();
////                            }
////
////                            @Override
////                            public void onFail(BaseCommand command) {
////                                Log.d(TAG, "目标设置获取失败");
////                            }
////
////                        }));
////                //获取电池电量并显示
////            } else if (baseCommand instanceof GetSleepData) {//获取睡眠数据
////                if (isHidden) {
////                    textStute.setVisibility(View.GONE);
////                }
////                if (GlobalDataManager.getInstance().getSleepDatas() == null) {
//////                    Log.e(TAG, "-------睡眠数据为null");
////                    setH9PieCharts();
////                } else {
////                    sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
////                    String sleepStr = "";
////                    for (SleepData sleepData : GlobalDataManager.getInstance().getSleepDatas()) {
////                        sleepStr = sleepStr + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepData.sleep_time_stamp * 1000)) + " 类型:" + sleepData.sleep_type + "\n";
////                    }
////                    Log.d(TAG, "------睡眠数据---" + sleepStr);
////
////                    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
////                        @Override
////                        public void call(Subscriber<? super String> subscriber) {
//////                            setH9PieCharts();
//////                            subscriber.onNext("睡眠图填充完成--数据上传完成");
//////                            sleepDataCrrur(1);
//////                            subscriber.onNext("睡眠前一天上传完成");
//////                            sleepDataCrrur(2);
//////                            subscriber.onNext("睡眠前2天上传完成");
//////                            sleepDataCrrur(3);
//////                            subscriber.onNext("睡眠前3天上传完成");
//////                            sleepDataCrrur(4);
//////                            subscriber.onNext("睡眠前4天上传完成");
//////                            sleepDataCrrur(5);
//////                            subscriber.onNext("睡眠前5天上传完成");
////                            setH9PieCharts();
////                            subscriber.onNext("睡眠图填充完成--数据上传完成");
////                            //int numberDay,int numberDayEnd, LinkedList<SleepData> sleepDatas
////                            UpDatasBase.sleepDataCrrur(2, 1, sleepDatas);
////                            subscriber.onNext("睡眠前一天上传完成");
////                            UpDatasBase.sleepDataCrrur(3, 2, sleepDatas);
////                            subscriber.onNext("睡眠前2天上传完成");
////                            UpDatasBase.sleepDataCrrur(4, 3, sleepDatas);
////                            subscriber.onNext("睡眠前3天上传完成");
////                            UpDatasBase.sleepDataCrrur(5, 4, sleepDatas);
////                            subscriber.onNext("睡眠前4天上传完成");
////                            UpDatasBase.sleepDataCrrur(6, 5, sleepDatas);
////                            subscriber.onNext("睡眠前5天上传完成");
////                            subscriber.onCompleted();
////                        }
////                    });
////
////                    Observer<String> observer = new Observer<String>() {
////                        @Override
////                        public void onNext(String s) {
////                            Log.d(TAG, "Item: " + s);
////                        }
////
////                        @Override
////                        public void onCompleted() {
////                            Log.d(TAG, "Completed!");
////                        }
////
////                        @Override
////                        public void onError(Throwable e) {
////                            Log.d(TAG, "Error!");
////                        }
////                    };
////
////                    observable.subscribe(observer);
////
////                }
////            }// 获取心率
////            else if (baseCommand instanceof GetHeartData) {
//////                Log.e(TAG, "-----心率返回----" + Arrays.toString(GlobalDataManager.getInstance().getHeartDatas().toArray()));
////
////                heartDatas = GlobalDataManager.getInstance().getHeartDatas();
////                //折线图
////                initLineCharts(heartDatas);
////                String upHearteTime = (String) SharedPreferencesUtils.getParam(context, "upHearteTime", "");
////                if (!TextUtils.isEmpty(upHearteTime)) {
////                    String timeDifference = H9TimeUtil.getTimeDifference(upHearteTime, B18iUtils.getSystemDataStart());
//////                    Log.e(TAG, "心率--时间-------" + timeDifference);
////                    if (!TextUtils.isEmpty(timeDifference.trim())) {
////                        int number = Integer.valueOf(timeDifference.trim());
////                        int number2 = Integer.parseInt(timeDifference.trim());
//////                        Log.e(TAG, "心率---------" + number);
////                        if (number >= 5 || number2 >= 5) {
//////                            Log.e(TAG, "心率-----in----" + number);
////                            for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
////                                if (heartData != null) {
////                                    String stringTimer = B18iUtils.interceptString(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.time_stamp * 1000)), 0, 16);
//////                                    Log.d(TAG, stringTimer);
////                                    UpDatasBase.upDataHearte(String.valueOf(heartData.heartRate_value), stringTimer, "00");//上传心率
////                                }
////                            }
////                            SharedPreferencesUtils.setParam(context, "upHearteTime", B18iUtils.getSystemDataStart());
////                        }
////                    }
////                } else {
////                    for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
////                        if (heartData != null) {
////                            String stringTimer = B18iUtils.interceptString(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.time_stamp * 1000)), 0, 16);
//////                            Log.d(TAG, stringTimer);
////                            UpDatasBase.upDataHearte(String.valueOf(heartData.heartRate_value), stringTimer, "00");//上传心率
////                        }
////                    }
////                    SharedPreferencesUtils.setParam(context, "upHearteTime", B18iUtils.getSystemDataStart());
////                }
////
////            }//电量返回
////            else if (baseCommand instanceof BatteryPower) {
////                int battery = GlobalVarManager.getInstance().getBatteryPower();
////                setBatteryPowerShow(battery);   //显示电量
////                //获取公里或者英里
////                AppsBluetoothManager.getInstance(context).sendCommand(new Unit(commandResultCallback));
////            } else if (baseCommand instanceof AllDataCount) {   //所有条数
////                Log.e("H9", "---所有条数---" + "SportCount:" + GlobalVarManager.getInstance().getSportCount()
////                        + "\n SleepCount:" + GlobalVarManager.getInstance().getSleepCount()
////                        + "\n HeartRateCount:" + GlobalVarManager.getInstance().getHeartRateCount()
////                        + "\n BloodCount:" + GlobalVarManager.getInstance().getBloodCount());
////                if (GlobalVarManager.getInstance().getSportCount() > 0) {
////                    AppsBluetoothManager.getInstance(context)
////                            .sendCommand(new GetSportData(commandResultCallback, (int) GlobalVarManager.getInstance().getSportCount()));
////                }
////                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
////                    AppsBluetoothManager.getInstance(context)
////                            .sendCommand(new GetHeartData(commandResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
////                }
////            } else if (baseCommand instanceof Unit) {  //英制还是公里
////                kmormi = GlobalVarManager.getInstance().getUnit();
////            } else if (baseCommand instanceof AutoSleep) {
////                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
////                    Log.e("H9", "--------enter sleep:" + GlobalVarManager.getInstance().getEnterSleepHour() + "hour" +
////                            "\n enter sleep:" + GlobalVarManager.getInstance().getEnterSleepMin() + "min" +
////                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepHour() + "hour" +
////                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepMin() + "min" +
////                            "\n myremind sleep cycle:" + GlobalVarManager.getInstance().getRemindSleepCycle());
////                }
////                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
////                    Log.e("H9", "----success---");
////                }
////            } else if (baseCommand instanceof GetSportData) {//详细运动数据
//////                int step = 0;
//////                int calorie = 0;
//////                int distance = 0;
////                if (GlobalDataManager.getInstance().getSportsDatas() != null) {
////                    final LinkedList<SportsData> sportsDatas = GlobalDataManager.getInstance().getSportsDatas();
////                    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
////                        @Override
////                        public void call(Subscriber<? super String> subscriber) {
//////                            upSportDatasCrrur(sportsDatas, 1);
//////                            subscriber.onNext("运动前一天上传完成");
//////                            upSportDatasCrrur(sportsDatas, 2);
//////                            subscriber.onNext("运动前2天上传完成");
//////                            upSportDatasCrrur(sportsDatas, 3);
//////                            subscriber.onNext("运动前3天上传完成");
//////                            upSportDatasCrrur(sportsDatas, 4);
//////                            subscriber.onNext("运动前4天上传完成");
//////                            upSportDatasCrrur(sportsDatas, 5);
//////                            subscriber.onNext("运动前5天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 1, GOAL);
////                            subscriber.onNext("运动前一天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 2, GOAL);
////                            subscriber.onNext("运动前2天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 3, GOAL);
////                            subscriber.onNext("运动前3天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 4, GOAL);
////                            subscriber.onNext("运动前4天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 5, GOAL);
////                            subscriber.onNext("运动前5天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 6, GOAL);
////                            subscriber.onNext("运动前6天上传完成");
////                            UpDatasBase.upSportDatasCrrur(sportsDatas, 7, GOAL);
////                            subscriber.onNext("运动前7天上传完成");
////                            subscriber.onCompleted();
////                        }
////                    });
////
////                    Observer<String> observer = new Observer<String>() {
////                        @Override
////                        public void onNext(String s) {
////                            Log.d(TAG, "Item: " + s);
////                        }
////
////                        @Override
////                        public void onCompleted() {
////                            Log.d(TAG, "Completed!");
////                        }
////
////                        @Override
////                        public void onError(Throwable e) {
////                            Log.d(TAG, "Error!");
////                        }
////                    };
////
////                    observable.subscribe(observer);
////
////                }
////            } else if (baseCommand instanceof SportSleepMode) {
////                if (GlobalVarManager.getInstance().getSportSleepMode() == 0) {
////                    Log.d(TAG, "sport model");
////                } else {
////                    Log.d(TAG, "sleep model");
////                }
////            }
////        }
////
////        @Override
////        public void onFail(BaseCommand baseCommand) {
////            Log.e(TAG, "-----onFail---------获取失败");
////        }
////    };
//
//
////    /**
////     * 整理number天睡眠数据（准备上传）
////     *
////     * @param numberDay
////     */
////    public void sleepDataCrrur(int numberDay) {
////        int AWAKEDATAS = 0;
////        int DEEPDATAS = 0;
////        int SHALLOWDATAS = 0;
////
////        int AllSleep = 0;
////        boolean isSleeped = false;
////        boolean isIntoSleeped = false;
////        if (sleepDatas != null) {
////            //当天日期
////            String soberLenTime = "08:00";
////            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
////            String currentDay = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取
////            String nextDay = B18iUtils.getNextNumberDay(numberDay);//前一天时间
////            setSleepDatas(currentDay, nextDay, AllSleep, isSleeped, isIntoSleeped, SHALLOWDATAS, DEEPDATAS, AWAKEDATAS, soberLenTime);
////        }
////    }
////
////    /**
////     * 上传前number天睡眠数据
////     * @param currentDay
////     * @param nextDay
////     * @param AllSleep
////     * @param isSleeped
////     * @param isIntoSleeped
////     * @param SHALLOWDATAS
////     * @param DEEPDATAS
////     * @param AWAKEDATAS
////     * @param soberLenTime
////     */
////    private void setSleepDatas(String currentDay, String nextDay, int AllSleep, boolean isSleeped,
////                               boolean isIntoSleeped, int SHALLOWDATAS, int DEEPDATAS, int AWAKEDATAS, String soberLenTime) {
////        int size = sleepDatas.size();
////        for (int i = 0; i < size; i++) {
////            String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
////            String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
////            String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
////            if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
////                if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
////                    String timeDifference = "0";
////                    if (0 < i) {
////                        timeDifference = H9TimeUtil.getTimeDifference
////                                (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i - 1).sleep_time_stamp * 1000))
////                                        , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                    } else {
////                        timeDifference = "0";
////                    }
////
////                    int SLEEPWAKE = 0;//苏醒次数
////                    int sleep_type = sleepDatas.get(i).sleep_type;
////                    // 0：睡着// 1：浅睡// 2：醒着// 3：准备入睡// 4：退出睡眠// 16：进入睡眠模式//
////                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
////                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
////                    if (sleep_type == 0) {//--------》睡着
////                        Log.e(TAG, "睡着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        AllSleep += Integer.valueOf(timeDifference);
////                        DEEPDATAS += Integer.valueOf(timeDifference);//睡着的分钟数
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 1) {//--------》浅睡
////                        Log.e(TAG, "浅睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        AllSleep += Integer.valueOf(timeDifference);
////                        SHALLOWDATAS += Integer.valueOf(timeDifference);//浅睡的分钟数
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 2) {//--------》醒着
////                        Log.e(TAG, "醒着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 3) {//--------》准备入睡着
////                        Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        if (!isIntoSleeped) {
////                            isIntoSleeped = true;
////                            isSleeped = true;
//////                            textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
////                        }
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 4) {//--------》退出睡眠
////                        Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        SLEEPWAKE++;
////                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
//////                        textSleepTime.setText(soberLenTime);
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 16) {//--------》进入睡眠模式
////                        Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        if (!isSleeped) {
////                            isSleeped = true;
////                            isIntoSleeped = true;
//////                            textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
////                        }
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 17) {//--------》退出睡眠模式（本次睡眠非预设睡眠）
////                        Log.e(TAG, "退出睡眠模式==0=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        SLEEPWAKE++;
////                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
//////                        textSleepTime.setText(soberLenTime);
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    } else if (sleep_type == 18) {//--------》退出睡眠模式（本次睡眠为预设睡眠）
////                        Log.e(TAG, "退出睡眠模式==1=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
////                        SLEEPWAKE++;
////                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
//////                        textSleepTime.setText(soberLenTime);
////                        Log.d(TAG, "===========" + SLEEPWAKE);
////                    }
////                    Log.d(TAG, DEEPDATAS + "----------222--------" + SHALLOWDATAS + "==============" + AllSleep + "===" + SLEEPWAKE);
////                    //---------入睡时间-----苏醒次数--------苏醒时间
//////                        TextView textSleepInto, textSleepWake, textSleepTime;
//////                    textSleepWake.setText(String.valueOf(SLEEPWAKE));//苏醒次数
////                }
////            }
////        }
////        Log.d(TAG, DEEPDATAS + "----------121112--------" + SHALLOWDATAS + "==============" + AllSleep);
//////        timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));//时长
//////        timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOWDATAS * 60), 3600, 2));//浅睡
//////        timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEPDATAS * 60), 3600, 2));//深睡
////        AWAKEDATAS = AllSleep - (DEEPDATAS + SHALLOWDATAS);//清醒
////        Log.d(TAG, "睡眠----清醒" + AWAKEDATAS);
//////        awakeSleep.setText(timeFromMillisecondA);//时长
//////        shallowSleep.setText(timeFromMillisecondS);
//////        deepSleep.setText(timeFromMillisecondD);
////
////        if (DEEPDATAS > 0 || SHALLOWDATAS > 0) {
//////                if (B18iCommon.ISUPDATASLEEP) {//判断，20分钟钟只可上传一次
////            String upSleepTime = (String) SharedPreferencesUtils.getParam(context, "upSleepTime", "");
////            if (!TextUtils.isEmpty(upSleepTime)) {
////                String timeDifference = H9TimeUtil.getTimeDifference(upSleepTime, B18iUtils.getSystemDataStart());
////                if (!TextUtils.isEmpty(timeDifference)) {
////                    int number = Integer.valueOf(timeDifference.trim());
////                    int number2 = Integer.parseInt(timeDifference.trim());
//////                        Log.e(TAG, "睡眠上传---------" + number + "--" + number2 + "==" + timeDifference.compareTo("5"));
////                    if (number >= 5 || number2 >= 5) {
////
////                        Log.d(TAG, "----清醒时间" + soberLenTime);
//////                            Log.e(TAG, "睡眠上传-----in----" + number + "===前几天时间" + H9TimeUtil.getDateBefore(new Date(), 7) + "前一天时间" + B18iUtils.getNextDay());
////                        upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS));//上传睡眠数据
////                        SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
////                    }
////                }
////            } else {
////                upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS));//上传睡眠数据
////                SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
////            }
////        }
////    }
//
////    /**
////     * 心率数据上传
////     */
////    private void upDataHearte(String heartData, String stringTimer) {
////        try {
////            JSONObject map = new JSONObject();
////            map.put("userId", SharedPreferencesUtils.readObject(context, "userId"));
////            map.put("deviceCode", SharedPreferencesUtils.readObject(context, "mylanmac"));
////            map.put("systolic", "00");
////            map.put("stepNumber", "00");
////            map.put("date", stringTimer);
////            map.put("heartRate", heartData);
////            map.put("status", "0");
////            JSONObject mapB = new JSONObject();
////            JSONArray jsonArray = new JSONArray();
////            Object jsonArrayb = jsonArray.put(map);
////            mapB.put("data", jsonArrayb);
////            String mapjson = mapB.toString();
////            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
////                @Override
////                public void onNext(String s) {
////                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--心率数据上传--------" + s);
////                }
////            };
////            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, getActivity());
////            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
////        } catch (Exception E) {
////            E.printStackTrace();
////        }
////    }
//
////    /**
////     * 上传睡眠数据
////     *
////     * @param deepSleep
////     * @param shallowSleep
////     */
////    private void upDataSleep(String deepSleep, String shallowSleep) {
////        Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠上传--------" + shallowSleep + "==" + deepSleep);
////        try {
////            JSONObject map = new JSONObject();
////            String userId = (String) SharedPreferencesUtils.readObject(context, "userId");
////            String mylanmac = (String) SharedPreferencesUtils.readObject(context, "mylanmac");
////            Log.d(TAG, "==设备名称与MAC==" + userId + "==" + mylanmac);
////            map.put("userId", (String) SharedPreferencesUtils.readObject(context, "userId"));
////            Log.d(TAG, B18iUtils.getNextNumberDays(1) + "===" + B18iUtils.getNextNumberDays(0));
////            map.put("startTime", B18iUtils.getNextNumberDays(1));//
////            map.put("endTime", B18iUtils.getNextNumberDays(0));
////            map.put("count", "10");
////            map.put("deepLen", deepSleep);
////            map.put("shallowLen", shallowSleep);
////            map.put("deviceCode", (String) SharedPreferencesUtils.readObject(context, "mylanmac"));
////            map.put("sleepQuality", "6");
////            map.put("sleepLen", "4");
////            map.put("sleepCurveP", "5");
////            map.put("sleepCurveS", "8");
//////            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, context);
////
////            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
////                @Override
////                public void onNext(String s) {
////                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠数据上传--------" + s);
////                }
////            };
////            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, getActivity());
////            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSleep, map.toString());
////        } catch (Exception E) {
////            E.printStackTrace();
////        }
////    }
//
////    /**
////     * 前几天运动分配 （准备上传）
////     *
////     * @param sportsDatas
////     * @param numberDay
////     */
////    public void upSportDatasCrrur(LinkedList<SportsData> sportsDatas, int numberDay) {
////        int step = 0;
////        int calorie = 0;
////        String nextDay = B18iUtils.getNextNumberDay(numberDay);//前numberDay天时间
////        for (SportsData sportsData : sportsDatas) {
////            SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
////            int i = Integer.parseInt(String.valueOf(sportsData.getSport_time_stamp()));
////            String times = sdr.format(new Date(i * 1000L));
////            if (nextDay.equals(times)) {
////                step += sportsData.sport_steps;
////                calorie += sportsData.sport_cal;
////            }
////        }
////        Log.d("-------------TEMT", "Step:" + step + "step" + "Calorie:" + calorie + "cal");
////        int dis = 0;
////        String sex = "M";
////        String hight = "175";
////        if ("M".equals(sex)) {
////            dis = (int) (Integer.valueOf(hight) * 0.415 * step);
////        } else {
////            dis = (int) (Integer.valueOf(hight) * 0.413 * step);
////        }
////        updateLoadSportToServer2(GOAL, step, calorie, dis, nextDay);
////    }
////
////    /**
////     * 前几天运动上传
////     *
////     * @param goal
////     * @param step
////     * @param calories
////     * @param distance
////     * @param nextDay
////     */
////    public void updateLoadSportToServer2(float goal, float step, double calories, double distance, String nextDay) {
////        int state = 1;  //步数是否达标
////        if (goal - state >= 0) {  //达标
////            state = 0;
////        } else {
////            state = 1;
////        }
////        JSONObject stepJons = new JSONObject();
////        try {
////            stepJons.put("userId", SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId")); //用户ID
////            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getInstance(), "mylanmac")); //mac地址
////            stepJons.put("stepNumber", step);   //步数
////            stepJons.put("distance", distance);  //路程
////            stepJons.put("calories", calories);  //卡里路
////            stepJons.put("timeLen", "0");    //时长
////            stepJons.put("date", nextDay);   //data_time
////            stepJons.put("status", state);     //是否达标
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
////
////            @Override
////            public void onNext(String result) {
////                Log.e("H9", "---前几天步数数据返回--" + result);
////            }
////        }, MyApp.getInstance());
////        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
////    }
//
////    /**
////     * @param goal     目标步数
////     * @param step     手表步数
////     * @param calories 手表卡路里
////     * @param distance //手表公里
////     */
////    private void updateLoadSportToServer(float goal, float step, double calories, double distance) {
////        Log.e(TAG, "--aaaaaaaaaaaaaaaa---步数上传--------" + goal + "==" + step + "==" + calories + "==" + distance);
////        int state = 1;  //步数是否达标
////        if (goal - state >= 0) {  //达标
////            state = 0;
////        } else {
////            state = 1;
////        }
////        JSONObject stepJons = new JSONObject();
////        try {
////            stepJons.put("userId", SharedPreferencesUtils.readObject(context, "userId")); //用户ID
////            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(context, "mylanmac")); //mac地址
////            stepJons.put("stepNumber", step);   //步数
////            stepJons.put("distance", distance);  //路程
////            stepJons.put("calories", calories);  //卡里路
////            stepJons.put("timeLen", "0");    //时长
////            stepJons.put("date", WatchUtils.getCurrentDate());   //data_time
////            stepJons.put("status", state);     //是否达标
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        Log.e(TAG, "-----steJson-----" + stepJons.toString() + "--" + System.currentTimeMillis() / 1000 + "---" + new Date().getTime() / 1000);
////        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
////
////            @Override
////            public void onNext(String result) {
////                Log.e("H9", "---上次步数数据返回--" + result);
////            }
////        }, context);
////        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
////    }
//
//    //显示电量
////    private void setBatteryPowerShow(int battery) {
////        Log.e(TAG,"----------battery="+battery);
////        try {
////            watchTopBatteryImgView.setColor(R.color.black);
////            watchTopBatteryImgView.setPower(battery);
////            batteryPowerTv.setText(battery + "%");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////    }
//
//
//    //显示电量
//    private void setBatteryPowerShow(int battery) {
//        Log.e(TAG, "----------battery=" + battery);
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            try {
//                if (battery <= 100 && battery > 80) {
//                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
//                }
//                if (battery <= 80 && battery > 60) {
//                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
//                }
//                if (battery <= 60 && battery > 40) {
//                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
//                }
//                if (battery <= 40 && battery > 20) {
//                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
//                }
//                if (battery <= 20 && battery > 0) {
//                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
//                }
//                batteryPowerTv.setText(battery + "%");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private class MyViewLister implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
////            Intent intent = new Intent(getActivity(), HeartRateActivity.class).putExtra("is18i", "H9");
////            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(intent);
//            if (MyCommandManager.DEVICENAME != null) {    //已连接
//                startActivity(new Intent(context,
//                        H9HearteTestActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "H9"));
//            } else {
//                startActivity(new Intent(getActivity(), NewSearchActivity.class));
//                getActivity().finish();
//            }
//
//        }
//    }
//
//
//    /*************************       之后的获取     *******************************/
//
//
//    /**
//     * 获取数据
//     */
//    public void getDatas() {
//
//        boolean b = firstInspect();
//        Log.d(TAG, "===连接状态==" + firstInspect());
//        if (b) {
//            /**
//             * 获取数据
//             * 步骤 一
//             */
//            AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new BatteryPower(ResultCallback));
//        }
//
////        if (MyCommandManager.DEVICENAME != null) {
////
//////
//////            Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
//////                @Override
//////                public void call(Subscriber<? super String> subscriber) {
//////                    AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new BatteryPower(commandResultCallback));
//////                    subscriber.onNext("获取电量ok");
//////                    //获取目标
//////                    AppsBluetoothManager.getInstance(context)
//////                            .sendCommand(new GoalsSetting(new BaseCommand.CommandResultCallback() {
//////                                @Override
//////                                public void onSuccess(BaseCommand command) {
//////                                    Log.d(TAG, "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
//////                                            "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
//////                                            "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
//////                                            "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());
//////                                    GOAL = GlobalVarManager.getInstance().getStepGoalsValue();
//////                                    if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
//////                                        recordwaveProgressBar.setMaxValue(GOAL);
//////                                        watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + GOAL + "");
//////                                    }
//////
////////                        circleprogress.reset();
////////                        circleprogress.setMaxValue(GOAL);
////////                        circleprogress.postInvalidate();
//////                                }
//////
//////                                @Override
//////                                public void onFail(BaseCommand command) {
//////                                    Log.d(TAG, "目标设置获取失败");
//////                                }
//////                            }));
//////                    subscriber.onNext("获取目标ok");
//////                    AppsBluetoothManager.getInstance(context)
//////                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallback));//获取当天运动汇总
//////                    subscriber.onNext("获取目标ok");
//////                    //获取心率数据
//////                    initLineChart();
//////                    initLineCharts(heartDatas);
//////                    subscriber.onNext("获取心率数据ok");
//////                    //获取睡眠数据
//////                    setPieChart();
//////                    setH9PieCharts();
//////                    subscriber.onNext("获取睡眠数据ok");
//////                    subscriber.onCompleted();
//////                }
//////            });
//////
//////            Observer<String> observer = new Observer<String>() {
//////                @Override
//////                public void onNext(String s) {
//////                    Log.d(TAG, "Item: " + s);
//////                }
//////
//////                @Override
//////                public void onCompleted() {
//////                    Log.d(TAG, "Completed!");
//////                }
//////
//////                @Override
//////                public void onError(Throwable e) {
//////                    Log.d(TAG, "Error!");
//////                }
//////            };
//////
//////            observable.subscribe(observer);
////        } else {
////            if (getActivity() != null) {
////                textStute.setText(getResources().getString(R.string.disconnted));
////                textStute.setVisibility(View.VISIBLE);
////                watchConnectStateTv.setText("" + "disconn.." + "");
////                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
////                AnimationUtils.startFlick(watchConnectStateTv);
////            }
////
////        }
//////        else {
//////            myHandler.sendEmptyMessageDelayed(1001, 4000);
//////        }
//    }
//
//
//    BaseCommand.CommandResultCallback ResultCallback = new BaseCommand.CommandResultCallback() {
//        @Override
//        public void onSuccess(BaseCommand baseCommand) {
//
//            if (baseCommand instanceof BatteryPower) {//电池电量获取成功
//                int batteryPower = GlobalVarManager.getInstance().getBatteryPower();
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x01);
//                    message.obj = batteryPower;
//                    mHandler.sendMessage(message);
//                }
//
//                /**
//                 * 步骤 二
//                 * 获取单位（公英制）
//                 */
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setText(getResources().getString(R.string.syncy_data) + "读取公英制");
//                AppsBluetoothManager.getInstance(context).sendCommand(new Unit(ResultCallback));
//
//            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//
//                    Log.d(TAG, "-----H9-onSuccess-  Unit:" + GlobalVarManager.getInstance().getUnit());
//
//                    /**
//                     * 公英制获取成功 -- 设置
//                     */
//                    kmormi = GlobalVarManager.getInstance().getUnit();
//
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_UTIT", kmormi);//公英制
//                    /**
//                     * 步骤 三
//                     * 获取目标 （步数，卡路里，距离，睡眠）
//                     */
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取目标值");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GoalsSetting(ResultCallback));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----H9-onSuccess-  设置 Unit:" + GlobalVarManager.getInstance().getUnit());
//                }
//            } else if (baseCommand instanceof GoalsSetting) { //获取目标/设置目标
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x02);
//                        mHandler.sendMessage(message);
//                    }
//
//                    /**
//                     * 步骤 四
//                     * 获取当天运动汇总
//                     */
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取运动汇总");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new DeviceDisplaySportSleep(ResultCallback));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----H9-onSuccess-  设置 GoalsSetting:");
//                }
//            } else if (baseCommand instanceof DeviceDisplaySportSleep) {//获取当天运动汇总
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x03);
//                    mHandler.sendMessage(message);
//                }
//                /**
//                 * 步骤 五
//                 * 获取全部数据的条数
//                 */
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setText(getResources().getString(R.string.syncy_data) + "读取数据长度");
//                AppsBluetoothManager.getInstance(context)
//                        .sendCommand(new AllDataCount(ResultCallback));//获取全部数据的条数
//            } else if (baseCommand instanceof AllDataCount) {
//
//                Log.d(TAG, "-----H9-onSuccess-  DeviceDisplaySportSleep:" + "运动条数:" + GlobalVarManager.getInstance().getSportCount()
//                        + "\n 睡眠条数:" + GlobalVarManager.getInstance().getSleepCount()
//                        + "\n 心率条数:" + GlobalVarManager.getInstance().getHeartRateCount()
//                        + "\n 血压条数:" + GlobalVarManager.getInstance().getBloodCount());
//
//                /**
//                 * 步骤 六
//                 * 获取运动详细数据
//                 */
//                if (GlobalVarManager.getInstance().getSportCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取步数详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSportData(ResultCallback, (int) GlobalVarManager.getInstance().getSportCount()));
//                } else if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "步数为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetHeartData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "步数心率为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//
//            } else if (baseCommand instanceof GetSportData) {
//                LinkedList<SportsData> sportsDatas = GlobalDataManager.getInstance().getSportsDatas();
//                if (sportsDatas != null)
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x04);
//                        message.obj = sportsDatas;
//                        mHandler.sendMessage(message);
//                    }
//
//                /**
//                 * 步骤 七
//                 * 获取心率详细数据
//                 */
//                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetHeartData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "心率为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//            } else if (baseCommand instanceof GetHeartData) {
//                LinkedList<HeartData> heartDatas = GlobalDataManager.getInstance().getHeartDatas();
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x05);
//                    message.obj = heartDatas;
//                    mHandler.sendMessage(message);
//                }
//
//                /**
//                 * 步骤 八
//                 * 获取睡眠详细数据
//                 */
//                if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//
//            } else if (baseCommand instanceof GetSleepData) {
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setVisibility(View.INVISIBLE);
//                LinkedList<SleepData> sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
//                if (sleepDatas != null)
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x06);
//                        message.obj = sleepDatas;
//                        mHandler.sendMessage(message);
//                    }
//
//            } else if (baseCommand instanceof ClearSportData
//                    || baseCommand instanceof ClearHeartData
//                    || baseCommand instanceof ClearBloodData
//                    || baseCommand instanceof ClearSleepData) {
//                //删除运动数据
//                Log.d(TAG, "-----------清楚数据成功");
//            }
//
//
//        }
//
//        @Override
//        public void onFail(BaseCommand baseCommand) {
//
//
//            if (baseCommand instanceof BatteryPower) {//电池电量获取成功
//                AppsBluetoothManager.getInstance(context).sendCommand(new Unit(ResultCallback));
//            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GoalsSetting(ResultCallback));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----H9-onSuccess-  设置 Unit:" + GlobalVarManager.getInstance().getUnit());
//                }
//            } else if (baseCommand instanceof GoalsSetting) { //获取目标/设置目标
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new DeviceDisplaySportSleep(ResultCallback));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----H9-onSuccess-  设置 GoalsSetting:");
//                }
//            } else if (baseCommand instanceof DeviceDisplaySportSleep) {//获取当天运动汇总
//                AppsBluetoothManager.getInstance(context)
//                        .sendCommand(new AllDataCount(ResultCallback));//获取全部数据的条数
//            } else if (baseCommand instanceof AllDataCount) {
//                if (GlobalVarManager.getInstance().getSportCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取步数详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSportData(ResultCallback, (int) GlobalVarManager.getInstance().getSportCount()));
//                } else if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "步数为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetHeartData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "步数心率为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//
//            } else if (baseCommand instanceof GetSportData) {
//                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetHeartData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "心率为0-读取心率详细");
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//            } else if (baseCommand instanceof GetHeartData) {
//                if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    AppsBluetoothManager.getInstance(context)
//                            .sendCommand(new GetSleepData(ResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setVisibility(View.INVISIBLE);
//                }
//
//            } else if (baseCommand instanceof GetSleepData) {
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setVisibility(View.INVISIBLE);
//            }
//
//
//        }
//    };
//
//
//    /*************************       按顺序同意获取数据       *******************************/
//
//    /**
//     * 初次链接设备，设置和读取设备所有数据和状态
//     */
//    synchronized void getAllDatas() {
//        /**
//         * 同步用户信息
//         * 设置手表的语言
//         * 通知开关
//         * 电池电量
//         * 获取单位（公英制）
//         * 获取目标 （目标步数，目标卡路里，目标距离，目标睡眠）
//         * 获取当天运动汇总（步数，卡路里，距离。。。）
//         * 获取全部数据的条数
//         * 获取运动详细数据
//         * 获取心率详细数据
//         * 获取睡眠详细数据
//         * 读取设备时间
//         */
//
//
//        boolean b = firstInspect();
//        Log.d(TAG, "===连接状态==" + firstInspect());
//        if (b) {
//            /**
//             * 所有数据 第①步
//             *
//             *  ---------------同步用户信息
//             */
//            if (getActivity() != null && !getActivity().isFinishing())
//                textStute.setText(getResources().getString(R.string.syncy_data) + "同步用户信息");
//            syncUserInfoData();
//        }
//
//    }
//
//    /**
//     * 检查链接状态
//     */
//    boolean firstInspect() {
//        boolean isConnted = false;
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            textStute.setVisibility(View.VISIBLE);
//            watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());//顶部时间设置
//            if (MyCommandManager.DEVICENAME == null) {
//                watchConnectStateTv.setText(getResources().getString(R.string.disconnted));
//                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
//                AnimationUtils.startFlick(watchConnectStateTv);
//                batteryLayout.setVisibility(View.GONE);
//                isConnted = false;
//                textStute.setText("已断开,尝试连接中...");
//
//                //链接设备
//                H9HomeActivity.ConntentDevices();
//            } else {
//                watchConnectStateTv.setText(getResources().getString(R.string.connted));//链接状态设置
//                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
//                AnimationUtils.stopFlick(watchConnectStateTv);
//                batteryLayout.setVisibility(View.VISIBLE);
//                isConnted = true;
//                textStute.setText(getResources().getString(R.string.syncy_data));
//            }
//        }
//        return isConnted;
//    }
//
//    //同步用户信息
//    private void syncUserInfoData() {
//        String userData = (String) SharedPreferencesUtils.readObject(getActivity(), "saveuserinfodata");
//        if (!WatchUtils.isEmpty(userData)) {
//            try {
//                int weight;
//                JSONObject jsonO = new JSONObject(userData);
//                String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
//                String userAge = jsonO.getString("birthday");   //生日
//                String userWeight = jsonO.getString("weight");  //体重
//                String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
//                if (tempWeight.contains(".")) {
//                    weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim() + "0");
//                } else {
//                    weight = Integer.valueOf(tempWeight + "0");
//                }
//                String userHeight = ((String) SharedPreferencesUtils.getParam(getActivity(), "userheight", "")).trim();
//                int sex;
//                if (userSex.equals("M")) {    //男
//                    sex = 0;
//                } else {
//                    sex = 1;
//                }
//                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
//                int height = Integer.valueOf(userHeight);
//
//                Log.e(TAG, "---------H9--①设置用户信息 " + "性别：" + sex + "年龄：" + age + "身高：" + height + "体重：" + weight);
//                //同步用户信息
//                AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new UserInfo(commandResultCallbackAll,
//                        5, sex, age, height, weight));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    //设置手表的语言
//    private void setH9WatchLanguage() {
//        //根据系统语言设置手环的语言
//        String language = MyApp.getInstance().getResources().getConfiguration().locale.getLanguage();
//        if (!WatchUtils.isEmpty(language)) {
//            byte languageTag;
//            if (language.equals("zh")) {  //中文
//                languageTag = (byte) 0x01;
//            } else {
//                languageTag = (byte) 0x00;
//            }
//            Log.e(TAG, "---------H9--②设置设备语言 " + languageTag);
//            // 语言类型 0x00：英文   0x01：中文
//            AppsBluetoothManager.getInstance(MyApp.getInstance())
//                    .sendCommand(new Language(commandResultCallbackAll, languageTag));
//        }
//    }
//
//
//    /**
//     * 初次链接设备，设置和读取设备所有数据和状态回掉监听
//     */
//    BaseCommand.CommandResultCallback commandResultCallbackAll = new BaseCommand.CommandResultCallback() {
//        @Override
//        public void onSuccess(BaseCommand baseCommand) {
//
//            if (baseCommand instanceof UserInfo) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//用户信息（读取
//                    Log.d(TAG, "-----用户信息读取成功-");
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//用户信息（写入
//                    Log.d(TAG, "-----用户信息写入成功---开始写语言");
//                    /**
//                     * 所有数据第②步
//                     *  设备语言写入 中/英
//                     */
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "设置语言");
//                    setH9WatchLanguage();
//                }
//            } else if (baseCommand instanceof Language) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//设备语言（读取
//                    Log.d(TAG, "-----设备语言读取成功-");
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设备语言（写入
//                    Log.d(TAG, "-----设备语言写入成功:" + GlobalVarManager.getInstance().getLanguage() + "---开始读取设备开关");
//
//                    // 社交开关      短信开关      未接来电提醒开关  来电提醒开关  自动睡眠监测开关  睡眠开关      自动同步开关   防丢开关
//                    // TWITTER开关  FACEBOOK开关  运动心率模式开关  二次提醒开关  超低功耗功能开关  久坐提醒开关   日历提醒开关   邮件提醒开关
//                    // 预留         预留          SKYPE开关       LINE开关     WHATSAPP开关    QQ开关        INSTAGRM开关  FACEBOOK开关
////                    if (getActivity() != null && !getActivity().isFinishing())
////                        textStute.setText(getResources().getString(R.string.syncy_data) + "③开关设置");
//                    // 参数5:任意byte
//                    Log.e(TAG, "---------H9--④设置设备开关");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "设置开关");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new SwitchSetting(commandResultCallbackAll, 4,
//                                    (byte) 0x00,
//                                    "11011011,11111100,00111111", (byte) 0x01));
//
////                    Log.e(TAG, "---------H9--④读取设置设备开关");
////                    if (getActivity() != null && !getActivity().isFinishing())
////                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取开关");
////                    AppsBluetoothManager.getInstance(MyApp.getInstance())
////                            .sendCommand(new SwitchSetting(commandResultCallbackAll));//读取通知开关状态
//                }
//            } else if (baseCommand instanceof SwitchSetting) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//设备开关（读取
//                    Log.d(TAG, "-----读取设备开关成功:"
//                            + "防丢开关:" + GlobalVarManager.getInstance().isAntiLostSwitch()
//                            + "\n 自动同步开关:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
//                            + "\n 睡眠开关:" + GlobalVarManager.getInstance().isSleepSwitch()
//                            + "\n 自动睡眠监测开关:" + GlobalVarManager.getInstance().isSleepStateSwitch()
//                            + "\n 来电提醒开关:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
//                            + "\n 未接来电提醒开关:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
//                            + "\n 短信提醒开关:" + GlobalVarManager.getInstance().isSmsSwitch()
//                            + "\n 社交提醒开关:" + GlobalVarManager.getInstance().isSocialSwitch()
//                            + "\n 邮件提醒开关:" + GlobalVarManager.getInstance().isMailSwitch()
//                            + "\n 日历开关:" + GlobalVarManager.getInstance().isCalendarSwitch()
//                            + "\n 久坐提醒开关:" + GlobalVarManager.getInstance().isSedentarySwitch()
//                            + "\n 超低功耗功能开关:" + GlobalVarManager.getInstance().isLowPowerSwitch()
//                            + "\n 二次提醒开关:" + GlobalVarManager.getInstance().isSecondRemindSwitch()
//                            + "\n 运动心率模式开关:" + GlobalVarManager.getInstance().isSportHRSwitch()
//                            + "\n FACEBOOK开关:" + GlobalVarManager.getInstance().isFacebookSwitch()
//                            + "\n TWITTER开关:" + GlobalVarManager.getInstance().isTwitterSwitch()
//                            + "\n INSTAGRAM开关:" + GlobalVarManager.getInstance().isInstagamSwitch()
//                            + "\n QQ开关:" + GlobalVarManager.getInstance().isQqSwitch()
//                            + "\n WECHAT开关:" + GlobalVarManager.getInstance().isWechatSwitch()
//                            + "\n WHATSAPP开关:" + GlobalVarManager.getInstance().isWhatsappSwitch()
//                            + "\n LINE开关:" + GlobalVarManager.getInstance().isLineSwitch()
//                            + "\n 心率自测:" + GlobalVarManager.getInstance().isAutoHeart()
//                            + "\n 心率预警:" + GlobalVarManager.getInstance().isHeartAlarm() + "---开始读取设备电池电量 ");
//
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_ANTI_SYNSC", GlobalVarManager.getInstance().isAutoSyncSwitch());//同步
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_ANTI_LOST", GlobalVarManager.getInstance().isAntiLostSwitch());//防止丢失
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_CALENDAR", GlobalVarManager.getInstance().isCalendarSwitch());//日历
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SEDENTARY", GlobalVarManager.getInstance().isSedentarySwitch());//久坐提醒
//
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SKYPE", GlobalVarManager.getInstance().isSkypeSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_WHATSAPP", GlobalVarManager.getInstance().isWhatsappSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_FACEBOOK", GlobalVarManager.getInstance().isFacebookSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TWTTER", GlobalVarManager.getInstance().isTwitterSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_INSTAGRAM", GlobalVarManager.getInstance().isInstagamSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_LINE", GlobalVarManager.getInstance().isLineSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_WECTH", GlobalVarManager.getInstance().isWechatSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_QQ", GlobalVarManager.getInstance().isQqSwitch());
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SMS", GlobalVarManager.getInstance().isSmsSwitch());//短信
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_INCOME_CALL", GlobalVarManager.getInstance().isIncomePhoneSwitch());//来电
//
//                    //  SharedPreferencesUtils.setParam(MyApp.getInstance(), "MISS_CALL", GlobalVarManager.getInstance().isMissPhoneSwitch());//未接
//                    //  SharedPreferencesUtils.setParam(MyApp.getInstance(), "MAIL", GlobalVarManager.getInstance().isMailSwitch());//邮件
//                    //  SharedPreferencesUtils.setParam(MyApp.getInstance(), "SOCIAL", GlobalVarManager.getInstance().isSocialSwitch());//社交
//                    //  SharedPreferencesUtils.setParam(MyApp.getInstance(), "LINKENDIN", GlobalVarManager.getInstance().isLineSwitch());
//
//                    /**
//                     * 所有数据第⑤步
//                     *
//                     *  ---------------读取电池电量
//                     */
//                    Log.e(TAG, "---------H9--⑤获取设备电量");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取电量");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new BatteryPower(commandResultCallbackAll));
//
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设备开关（写入
//                    Log.d(TAG, "-----设置设备开关成功:----开始读取设备开关");
//                    Log.e(TAG, "---------H9--④读取设置设备开关");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "④开关读取");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new SwitchSetting(commandResultCallbackAll));//读取通知开关状态
//                }
//            } else if (baseCommand instanceof BatteryPower) {//电池电量获取成功
//                int batteryPower = GlobalVarManager.getInstance().getBatteryPower();
//                Log.d(TAG, "-----电池电量获取成功:" + batteryPower + "---开始读取设备单位");
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x01);
//                    message.obj = batteryPower;
//                    mHandler.sendMessage(message);
//                }
//
//                /**
//                 * 所有数据第⑥步
//                 * -----------读取设备单位 公英制
//                 */
//                Log.e(TAG, "---------H9--⑥获取设备单位公英制");
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setText(getResources().getString(R.string.syncy_data) + "获取公英制");
//                AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new Unit(commandResultCallbackAll));
//
//            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    Log.d(TAG, "-----读取设备单位成功:" + GlobalVarManager.getInstance().getUnit() + "---开始获取目标 ");
//                    kmormi = GlobalVarManager.getInstance().getUnit();
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_UTIT", GlobalVarManager.getInstance().getUnit());//公英制
//                    /**
//                     * 所有数据第⑦步
//                     * --------获取目标 （步数，卡路里，距离，睡眠）
//                     */
//                    Log.e(TAG, "---------H9--⑦获取目标值");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取目标设置");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GoalsSetting(commandResultCallbackAll));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----设置设备单位成功:" + GlobalVarManager.getInstance().getUnit());
//                }
//            } else if (baseCommand instanceof GoalsSetting) { //获取目标/设置目标
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    Log.d(TAG, "-----读取设备目标成功:---开始获取当天运动汇总 ");
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x02);
//                        mHandler.sendMessage(message);
//                    }
//
//                    /**
//                     * 所有数据第⑧步
//                     * -------获取当天运动汇总
//                     */
//                    Log.e(TAG, "---------H9--⑧获取数据汇总");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取运动汇总");
//                    AppsBluetoothManager.getInstance(MyApp.getContext())
//                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----设置设备目标成功:" + GlobalVarManager.getInstance().getStepGoalsValue());
//                }
//            } else if (baseCommand instanceof DeviceDisplaySportSleep) {
//                Log.d(TAG, "-----获取当天运动汇总成功:---开始获取全部数据的条数 ");
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x03);
//                    mHandler.sendMessage(message);
//                }
//                /**
//                 * 步骤 ⑨
//                 * -------获取全部数据的条数
//                 */
//                Log.e(TAG, "---------H9--⑨获取所有数据长度");
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setText(getResources().getString(R.string.syncy_data) + "获取数据条数");
//                AppsBluetoothManager.getInstance(context)
//                        .sendCommand(new AllDataCount(commandResultCallbackAll));//获取全部数据的条数
//            } else if (baseCommand instanceof AllDataCount) {
//
//                Log.d(TAG, "-----获取设备获取当天运动汇总成功:" + "运动条数:" + GlobalVarManager.getInstance().getSportCount()
//                        + "\n 睡眠条数:" + GlobalVarManager.getInstance().getSleepCount()
//                        + "\n 心率条数:" + GlobalVarManager.getInstance().getHeartRateCount()
//                        + "\n 血压条数:" + GlobalVarManager.getInstance().getBloodCount());
//
//                /**
//                 * 步骤 ⑩
//                 * 获取运动详细数据
//                 */
//
//                if (GlobalVarManager.getInstance().getSportCount() > 0) {
//                    Log.e(TAG, "---------H9--⑩根据步数长度获取步数详细数据");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取运动详细");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GetSportData(commandResultCallbackAll, (int) GlobalVarManager.getInstance().getSportCount()));
//                } else {
//                    Log.d(TAG, "---运动条数为0-- 获取心率");
//                    Log.e(TAG, "---------H9--⑩根据心率长度获取步数详细数据");
//                    if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                        if (getActivity() != null && !getActivity().isFinishing())
//                            textStute.setText(getResources().getString(R.string.syncy_data) + "无步数-获取心率详细");
//                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                    } else {
//                        Log.e(TAG, "---------H9--⑩根据睡眠长度获取步数详细数据");
//                        Log.d(TAG, "---心率条数为0-- 获取睡眠");
//                        if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                            if (getActivity() != null && !getActivity().isFinishing())
//                                textStute.setText(getResources().getString(R.string.syncy_data) + "无步数心率-获取睡眠详细");
//                            AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                    .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                        } else {
//                            Log.d(TAG, "---步数心率睡眠条数为0-- 获取设备时间");
//                            Log.e(TAG, "---------H9--⑩步数心率睡眠长度统一为0，获取设备时间");
//                            if (getActivity() != null && !getActivity().isFinishing())
//                                textStute.setText(getResources().getString(R.string.syncy_data) + "无数据-获取时间");
//                            AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                    .sendCommand(new DateTime(commandResultCallbackAll));
//                        }
//                    }
//                }
//            } else if (baseCommand instanceof GetSportData) {
//                LinkedList<SportsData> sportsDatas = GlobalDataManager.getInstance().getSportsDatas();
//                if (sportsDatas != null)
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x04);
//                        message.obj = sportsDatas;
//                        mHandler.sendMessage(message);
//                    }
//
//                /**
//                 * 步骤 ⑩①
//                 * 获取心率详细数据
//                 */
//                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    Log.e(TAG, "---------H9--⑩①根据心率长度获取步数详细数据");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取心率详细数据");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else {
//                    Log.e(TAG, "---------H9--⑩①根据睡眠长度获取步数详细数据");
//                    Log.d(TAG, "---心率条数为0-- 获取睡眠");
//                    if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                        if (getActivity() != null && !getActivity().isFinishing())
//                            textStute.setText(getResources().getString(R.string.syncy_data) + "无心率-获取睡眠详细");
//                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                    } else {
//                        Log.e(TAG, "---------H9--⑩①心率睡眠长度统一为0，获取设备时间");
//                        Log.d(TAG, "---心率睡眠条数为0-- 获取设备时间");
//                        if (getActivity() != null && !getActivity().isFinishing())
//                            textStute.setText(getResources().getString(R.string.syncy_data) + "无心率睡眠-获取时间");
//                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                .sendCommand(new DateTime(commandResultCallbackAll));
//                    }
//                }
//            } else if (baseCommand instanceof GetHeartData) {
//                LinkedList<HeartData> heartDatas = GlobalDataManager.getInstance().getHeartDatas();
//                if (mHandler != null) {
//                    Message message = mHandler.obtainMessage(0x05);
//                    message.obj = heartDatas;
//                    mHandler.sendMessage(message);
//                }
//                /**
//                 * 步骤 ⑩②
//                 * 获取睡眠详细数据
//                 */
//                if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    Log.e(TAG, "---------H9--⑩②根据睡眠长度获取步数详细数据");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "获取睡眠详细数据");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    Log.e(TAG, "---------H9--⑩②睡眠长度统一为0，获取设备时间");
//                    Log.d(TAG, "---睡眠条数为0-- 获取设备时间");
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        textStute.setText(getResources().getString(R.string.syncy_data) + "无睡眠-获取时间");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new DateTime(commandResultCallbackAll));
//                }
//            } else if (baseCommand instanceof GetSleepData) {
//
//                LinkedList<SleepData> sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
//                if (sleepDatas != null)
//                    if (mHandler != null) {
//                        Message message = mHandler.obtainMessage(0x06);
//                        message.obj = sleepDatas;
//                        mHandler.sendMessage(message);
//                    }
//                /**
//                 * 步骤 ⑩③
//                 * 获取设备时间
//                 */
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setText(getResources().getString(R.string.syncy_data) + "获取时间");
//                AppsBluetoothManager.getInstance(MyApp.getInstance())
//                        .sendCommand(new DateTime(commandResultCallbackAll));
//            } else if (baseCommand instanceof DateTime) {
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setVisibility(View.INVISIBLE);
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
//                    Log.d(TAG, "-----读取设备时间成功:" + GlobalVarManager.getInstance().getDeviceDateTime());
//                    Log.d(TAG, "-----查看手机本地时间:" + B18iUtils.getSystemDataStart());
//                    String deviceDateTime = GlobalVarManager.getInstance().getDeviceDateTime();
//                    String systemDataStart = B18iUtils.getSystemDataStart();
//                    if (!WatchUtils.isEmpty(deviceDateTime) && !WatchUtils.isEmpty(systemDataStart)) {
//                        String[] splitDevices = deviceDateTime.split("[ ]");
//                        String[] splitSystem = systemDataStart.split("[ ]");
//                        if (!WatchUtils.isEmpty(splitDevices[0]) && !WatchUtils.isEmpty(splitDevices[1])
//                                && !WatchUtils.isEmpty(splitSystem[0]) && !WatchUtils.isEmpty(splitSystem[1])) {
//
//                            Log.e(TAG, "-------天 d:" + splitDevices[0] + "  s:" + splitSystem[0]);
//                            if (!splitDevices[0].equals(splitSystem[0])) {
//                                ToastUtil.showShort(getActivity(), "时间不对，相差大于一天的错误,需要校正");
//                                new CommomDialog(getActivity(), R.style.dialog,
//                                        "时间相差大于一天的错误,去校正？",
//                                        new CommomDialog.OnCloseListener() {
//                                            @Override
//                                            public void onClick(Dialog dialog, boolean confirm) {
//                                                if (confirm) {
//                                                    startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
//                                                }
//                                                dialog.dismiss();
//                                            }
//                                        }).setTitle(getResources().getString(R.string.prompt)).show();
//                            } else {
//                                String hmsDevices = splitDevices[1];
//                                String hmsSystem = splitSystem[1];
//                                String[] splitHmsDevices = hmsDevices.split("[:]");
//                                String[] splitHmsSystem = hmsSystem.split("[:]");
//                                if (!WatchUtils.isEmpty(splitHmsDevices[0]) && !WatchUtils.isEmpty(splitHmsDevices[1])
//                                        && !WatchUtils.isEmpty(splitHmsSystem[0]) && !WatchUtils.isEmpty(splitHmsSystem[1])) {
//                                    int devHour = Integer.valueOf(splitHmsDevices[0]);
//                                    int sysHour = Integer.valueOf(splitHmsSystem[0]);
//
//                                    int devMou = Integer.valueOf(splitHmsDevices[1]);
//                                    int sysMou = Integer.valueOf(splitHmsSystem[1]);
//
//                                    Log.e(TAG, "-------时 d:" + devHour + "  s:" + sysHour);
//                                    Log.e(TAG, "-------分 d:" + devMou + "  s:" + sysMou);
//                                    if (devHour - sysHour != 0) {
//                                        ToastUtil.showShort(getActivity(), "时间不对，相差大于一小时的错误,需要校正");
//                                        new CommomDialog(getActivity(), R.style.dialog,
//                                                "时间相差大于一小时的错误,去校正？",
//                                                new CommomDialog.OnCloseListener() {
//                                                    @Override
//                                                    public void onClick(Dialog dialog, boolean confirm) {
//                                                        if (confirm) {
//                                                            startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
//                                                        }
//                                                        dialog.dismiss();
//                                                    }
//                                                }).setTitle(getResources().getString(R.string.prompt)).show();
//                                    } else {
//                                        if (Math.abs(devMou - sysMou) > 10) {
//                                            ToastUtil.showShort(getActivity(), "时间不对，相差大于十分钟的错误,需要校正");
//                                            new CommomDialog(getActivity(), R.style.dialog,
//                                                    "时间相差大于十分钟的错误,去校正？",
//                                                    new CommomDialog.OnCloseListener() {
//                                                        @Override
//                                                        public void onClick(Dialog dialog, boolean confirm) {
//                                                            if (confirm) {
//                                                                startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
//                                                            }
//                                                            dialog.dismiss();
//                                                        }
//                                                    }).setTitle(getResources().getString(R.string.prompt)).show();
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
//                    Log.d(TAG, "-----设置设备时间成功:" + GlobalVarManager.getInstance().getDeviceDateTime());
//                }
//            }
//
//        }
//
//        @Override
//        public void onFail(BaseCommand baseCommand) {
//            Log.d(TAG, "===========fail===" + baseCommand.toString());
//
//
//            if (baseCommand instanceof UserInfo) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//用户信息（读取
//                    Log.d(TAG, "-----用户信息读取失败-");
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//用户信息（写入
//                    Log.d(TAG, "-----用户信息写入失败---开始写语言");
//                    /**
//                     * 所有数据第②步
//                     *  设备语言写入 中/英
//                     */
//                    setH9WatchLanguage();
//                }
//            } else if (baseCommand instanceof Language) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//设备语言（读取
//                    Log.d(TAG, "-----设备语言读取失败-");
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设备语言（写入
//                    Log.d(TAG, "-----设备语言写入失败:" + GlobalVarManager.getInstance().getLanguage() + "---开始读取设备开关");
//                    /**
//                     * 所有数据第③步
//                     *  ------------设置设备开关
//                     */
////                    if (getActivity() != null && !getActivity().isFinishing())
////                        textStute.setText(getResources().getString(R.string.syncy_data) + "③开关设置");
////                    // 参数5:任意byte
////                    AppsBluetoothManager.getInstance(MyApp.getInstance())
////                            .sendCommand(new SwitchSetting(commandResultCallbackAll, 4,
////                                    (byte) 0x00,
////                                    "00001011,00110000,00000000", (byte) 0x01));
//                    Log.e(TAG, "---------H9--④读取设置设备开关");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new SwitchSetting(commandResultCallbackAll));//读取通知开关状态
//                }
//            } else if (baseCommand instanceof SwitchSetting) {
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//设备开关（读取
//
//                    /**
//                     * 所有数据第⑤步
//                     *
//                     *  ---------------读取电池电量
//                     */
//                    Log.e(TAG, "---------H9--⑤获取设备电量");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new BatteryPower(commandResultCallbackAll));
//
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设备开关（写入
////                    Log.d(TAG, "-----设置设备开关失败:----开始读取设备开关");
////                    Log.e(TAG, "---------H9--④读取设置设备开关");
////                    if (getActivity() != null && !getActivity().isFinishing())
////                        textStute.setText(getResources().getString(R.string.syncy_data) + "④开关读取");
////                    AppsBluetoothManager.getInstance(MyApp.getInstance())
////                            .sendCommand(new SwitchSetting(commandResultCallbackAll));//读取通知开关状态
//                }
//            } else if (baseCommand instanceof BatteryPower) {//电池电量获取成功
//                Log.d(TAG, "-----电池电量获取失败:---开始读取设备单位");
//
//                /**
//                 * 所有数据第⑥步
//                 * -----------读取设备单位 公英制
//                 */
//                Log.e(TAG, "---------H9--⑥获取设备单位公英制");
//                AppsBluetoothManager.getInstance(MyApp.getInstance()).sendCommand(new Unit(commandResultCallbackAll));
//
//            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    Log.d(TAG, "-----读取设备单位失败:" + GlobalVarManager.getInstance().getUnit() + "---开始获取目标 ");
//                    /**
//                     * 所有数据第⑦步
//                     * --------获取目标 （步数，卡路里，距离，睡眠）
//                     */
//                    Log.e(TAG, "---------H9--⑦获取目标值");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GoalsSetting(commandResultCallbackAll));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----设置设备单位失败:" + GlobalVarManager.getInstance().getUnit());
//                }
//            } else if (baseCommand instanceof GoalsSetting) { //获取目标/设置目标
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取目标（读取
//                    Log.d(TAG, "-----读取设备目标失败:---开始获取当天运动汇总 ");
//                    /**
//                     * 所有数据第⑧步
//                     * -------获取当天运动汇总
//                     */
//                    Log.e(TAG, "---------H9--⑧获取数据汇总");
//                    AppsBluetoothManager.getInstance(MyApp.getContext())
//                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
//                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置目标（写入
//                    Log.d(TAG, "-----设置设备目标失败:" + GlobalVarManager.getInstance().getStepGoalsValue());
//                }
//            } else if (baseCommand instanceof DeviceDisplaySportSleep) {
//                Log.d(TAG, "-----获取当天运动汇总失败:---开始获取全部数据的条数 ");
//                /**
//                 * 步骤 ⑨
//                 * -------获取全部数据的条数
//                 */
//                Log.e(TAG, "---------H9--⑨获取所有数据长度");
//                AppsBluetoothManager.getInstance(context)
//                        .sendCommand(new AllDataCount(commandResultCallbackAll));//获取全部数据的条数
//            } else if (baseCommand instanceof AllDataCount) {
//                Log.e(TAG, "---------H9--⑩步数心率睡眠长度获取失败，获取设备时间");
//                AppsBluetoothManager.getInstance(MyApp.getInstance())
//                        .sendCommand(new DateTime(commandResultCallbackAll));
//            } else if (baseCommand instanceof GetSportData) {
//                /**
//                 * 步骤 ⑩①
//                 * 获取心率详细数据
//                 */
//                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
//                    Log.e(TAG, "---------H9--⑩①根据心率长度获取步数详细数据");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                } else {
//                    Log.e(TAG, "---------H9--⑩①根据睡眠长度获取步数详细数据");
//                    Log.d(TAG, "---心率条数为0-- 获取睡眠");
//                    if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                    } else {
//                        Log.e(TAG, "---------H9--⑩①心率睡眠长度统一为0，获取设备时间");
//                        Log.d(TAG, "---心率睡眠条数为0-- 获取设备时间");
//                        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                                .sendCommand(new DateTime(commandResultCallbackAll));
//                    }
//                }
//            } else if (baseCommand instanceof GetHeartData) {
//                /**
//                 * 步骤 ⑩②
//                 * 获取睡眠详细数据
//                 */
//                if (GlobalVarManager.getInstance().getSleepCount() > 0) {
//                    Log.e(TAG, "---------H9--⑩②根据睡眠长度获取步数详细数据");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
//                } else {
//                    Log.e(TAG, "---------H9--⑩②睡眠长度统一为0，获取设备时间");
//                    Log.d(TAG, "---睡眠条数为0-- 获取设备时间");
//                    AppsBluetoothManager.getInstance(MyApp.getInstance())
//                            .sendCommand(new DateTime(commandResultCallbackAll));
//                }
//            } else if (baseCommand instanceof GetSleepData) {
//                /**
//                 * 步骤 ⑩③
//                 * 获取设备时间
//                 */
//                AppsBluetoothManager.getInstance(MyApp.getInstance())
//                        .sendCommand(new DateTime(commandResultCallbackAll));
//            } else if (baseCommand instanceof DateTime) {
//                if (getActivity() != null && !getActivity().isFinishing())
//                    textStute.setVisibility(View.INVISIBLE);
//            }
//
//
//        }
//    };
//}
