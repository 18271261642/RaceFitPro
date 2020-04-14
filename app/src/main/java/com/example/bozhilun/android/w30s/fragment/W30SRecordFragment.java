package com.example.bozhilun.android.w30s.fragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.CommentDataActivity;
import com.example.bozhilun.android.h9.h9monitor.UpDatasBase;
import com.example.bozhilun.android.h9.settingactivity.H9HearteDataActivity;
import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.h9.utils.VerticalSwipeRefreshLayout;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.w30s.activity.W30DetailSportActivity;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.activity.SleepHistoryActivity;
import com.example.bozhilun.android.w30s.activity.StepHistoryDataActivity;
import com.example.bozhilun.android.w30s.activity.W30SHearteDataActivity;
import com.example.bozhilun.android.w30s.activity.W30SSettingActivity;
import com.example.bozhilun.android.w30s.bean.UpHeartBean;
import com.example.bozhilun.android.w30s.presenter.HomePresenter;
import com.example.bozhilun.android.w30s.bean.W30SHeartDataS;
import com.example.bozhilun.android.w30s.views.W30CusHeartView;
import com.example.bozhilun.android.w30s.views.W30S_SleepChart;
import com.example.bozhilun.android.R;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @aboutContent: 联系人界面
 * @author： An
 * @crateTime: 2018/3/5 17:04
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SRecordFragment extends BaseFragment implements  SeekBar.OnSeekBarChangeListener {//implements GetMaxStepServer.GetMaxStepForYearListener {
    private final String TAG = "W30SRecordFragment";
    boolean onesApp = true;
    //    @BindView(R.id.previousImage)DataTypeListenter
//    ImageView previousImage;
    @BindView(R.id.b18irecordFm)
    LinearLayout b18irecordFm;
    @BindView(R.id.scroll_home)
    ScrollView scroll_home;
    //    @BindView(R.id.nextImage)
//    ImageView nextImage;
    View b18iRecordView;
    Unbinder unbinder;
    //    @BindView(R.id.b18i_viewpager)
//    ViewPager l38iViewpager;
    @BindView(R.id.text_stute)
    TextView textStute;
    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;
    //    private int PAGES = 0;//页码
//    @BindView(R.id.line_pontion)
//    LinearLayout linePontion;
    private float GOAL = 10000;//默认目标
    private float STEP = 0;//步数
    @BindView(R.id.swipeRefresh)
    VerticalSwipeRefreshLayout swipeRefresh;//刷新控件
    //显示手表图标左上角
    @BindView(R.id.batteryshouhuanImg)
    ImageView shouhuanImg;
    //显示连接状态的TextView
    @BindView(R.id.battery_watch_connectStateTv)
    TextView watchConnectStateTv;
    //点击图标
    @BindView(R.id.watch_poorRel)
    RelativeLayout watchPoorRel;
    //显示日期的TextView
    @BindView(R.id.battery_watch_recordtop_dateTv)
    TextView watchRecordtopDateTv;
    //分享
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView watchRecordShareImg;
    //显示电量的图片
//    @BindView(R.id.batteryTopView)
//    BatteryView watchTopBatteryImgView;
    @BindView(R.id.batteryTopView)
    ImageView watchTopBatteryImgView;
    //显示电量
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    int kmormi; //距离显示是公制还是英制
    CallDataBackListe callDataBackListe;
    private boolean isOneCreate = false;
    private boolean isOneonResume = false;
    private boolean isHaertNull = true;

    //private GetMaxStepServer getMaxStepServer;


    //心率的最大值自小值和平均值
    private int heartMaxValue = 0;
    private int heartMineValue = 0;
    private int avgHeartValue = 0;






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        //getMaxStepServer = new GetMaxStepServer(getActivity());
        //getMaxStepServer.setGetMaxStepForYearListener(this);
        if (mHandler != null) mHandler.sendEmptyMessageDelayed(0x03, 1000);
    }

    HomePresenter homePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
        unbinder = ButterKnife.bind(this, b18iRecordView);
        isOneCreate = true;
        //stuteLister();
        homePresenter = new HomePresenter(this);
        homePresenter.changeUI();
        homePresenter.changeHttpsDataUI();

        saveStateToArguments();
        initStepList();
        setDatas(b18iRecordView);
        initViews();
        CALORIES = 0;
        DISTANCE = 0;


        return b18iRecordView;
    }


    public void changeUI() {
        //Log.d(TAG, "000-----回掉过来的链接状态");
        isOneCreate = true;
        isOneonResume = true;
        onesApp = true;
        if (MyCommandManager.DEVICENAME == null) {
            //Log.d(TAG, "001-----回掉过来的链接状态");
            if (callDataBackListe != null) {
                callDataBackListe = null;
            }
        } else {
            //Log.d(TAG, "002-----回掉过来的链接状态");
            isStuta();
            getDatas();
        }
    }


    //本月中的最大步数和距离卡路里
    public void changeHttpsDataUI(int maxStep, String kacl, String disc, String dateStr) {
        String discNew = "";
        Log.e("----changeHttpsDataUI--", maxStep + "===" + kacl + "===" + disc + "===" + dateStr);
        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(getContext(), "w30sunit", true);
        if (disc.contains(".")) {
            String[] split = disc.split(".");
            if (split.length != 0 && !WatchUtils.isEmpty(split[0])) {
                discNew = split[0].trim();
            }
        } else {
            discNew = dateStr.trim();
        }
        if (watchRecordTagstepTv != null) {
            if (w30sunit) {
                watchRecordTagstepTv.setText(getResources().getString(R.string.string_one_day_record) + ":" + dateStr + "  " +
                        maxStep + getResources().getString(R.string.steps)
                        + "  " + discNew + "m");
            } else {
                if (!WatchUtils.isEmpty(discNew)) {
                    int round = (int) Math.floor(Integer.valueOf(discNew) * 3.28);
                    watchRecordTagstepTv.setText(getResources().getString(R.string.string_one_day_record) + ":" + dateStr + "  " +
                            maxStep + getResources().getString(R.string.steps) + "  "
                            + round + "FT");
                }

            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveStateToArguments();
    }

//    public void stuteLister() {
//        MyBroadcastReceiver.setmBluetoothStateListenter(new MyBroadcastReceiver.BluetoothStateListenter() {
//            @Override
//            public void BluetoothStateListenter() {
//                Log.d(TAG, "000-----回掉过来的链接状态");
//                isOneCreate = true;
//                isOneonResume = true;
//                try {
//                    if (MyCommandManager.DEVICENAME == null) {
//                        Log.d(TAG, "001-----回掉过来的链接状态");
//                        if (callDataBackListe != null) {
//                            callDataBackListe = null;
//                        }
//                    } else {
//                        Log.d(TAG, "002-----回掉过来的链接状态");
//                        isStuta();
//                        getDatas();
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//            }
//        });
//    }


    @Override
    public void onStart() {
        super.onStart();
        isHidden = true;
        regsBroad();
        if (isHidden) {
            mystute();
            if (onesApp) {
                onesApp = false;
                isStuta();
            }
            getDatas();
            if (recordwaveProgressBar != null) {
                recordwaveProgressBar.setMaxValue(GOAL);
                recordwaveProgressBar.setValue(STEP);
                recordwaveProgressBar.postInvalidate();
            }
        }
    }


    public void mystute() {
        if (watchRecordtopDateTv != null)
            watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
        if (MyCommandManager.DEVICENAME == null) {
            if (textStute != null) {
                textStute.setText(getResources().getString(R.string.disconnted));
                textStute.setVisibility(View.VISIBLE);
            }
            if (watchConnectStateTv != null) {
                watchConnectStateTv.setText(getResources().getString(R.string.disconnted));
                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                AnimationUtils.startFlick(watchConnectStateTv);
            }
            if (batteryLayout != null) {
                batteryLayout.setVisibility(View.GONE);
            }
        } else {
            if (watchConnectStateTv != null) {
                watchConnectStateTv.setText(getResources().getString(R.string.connted));
                watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                AnimationUtils.stopFlick(watchConnectStateTv);
            }
            if (batteryLayout != null) {
                batteryLayout.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isOneonResume = true;
        isHidden = true;
        if (textAllSleepData != null) {
            textAllSleepData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pageIsOne = 0;
        isHidden = false;
        isHaertNull = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        pageIsOne = 0;
        isHidden = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageIsOne = 0;
        isHaertNull = true;
        getContext().unregisterReceiver(mBroadcastReceiver);
        unbinder.unbind();
        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 切换语言上下文置空处理
     */
    /**********************************************/
    private Context context;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null) context = getActivity();
    }

    /**********************************************/
    public void isStuta() {
        if (isHidden) {
            if (watchRecordtopDateTv != null)
                watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
            if (swipeRefresh != null) {
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
            if (MyCommandManager.DEVICENAME == null) {
                if (textStute != null) {
                    textStute.setText(getResources().getString(R.string.disconnted));
                    textStute.setVisibility(View.VISIBLE);
                }
                if (watchConnectStateTv != null) {
                    watchConnectStateTv.setText(getResources().getString(R.string.disconnted));
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    AnimationUtils.startFlick(watchConnectStateTv);
                }
                if (batteryLayout != null) {
                    batteryLayout.setVisibility(View.GONE);
                }
                synchronized (this) {
                    mHandler.sendEmptyMessageDelayed(0x04, 10000);
                }
            } else {
                if (watchConnectStateTv != null) {
                    watchConnectStateTv.setText(getResources().getString(R.string.connted));
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                    AnimationUtils.stopFlick(watchConnectStateTv);
                }
                if (textStute != null) {
                    textStute.setText(getResources().getString(R.string.syncy_data));
                    textStute.setVisibility(View.VISIBLE);
                }
                if (batteryLayout != null) {
                    batteryLayout.setVisibility(View.VISIBLE);
                }
                synchronized (this) {
                    mHandler.sendEmptyMessageDelayed(0x04, 5000);
                }
            }
        }
    }


    private int pageIsOne = 0;//界面第一次创建

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            Log.d("===================", "isVisible");
            if (pageIsOne == 1) {
//                //getDatas();
//                try {
////                    String w30S_p = (String) SharedPreferencesUtils.getParam(context, "W30S_P", "");
////                    if (!WatchUtils.isEmpty(w30S_p)) {
////                        setBatteryPowerShow(Integer.valueOf(w30S_p));   //显示电量
////                    }
//                    isHidden = true;
//                    if (MyCommandManager.DEVICENAME == null) return;
//                    String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
//                    if (!TextUtils.isEmpty(homeTime)) {
//                        String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                        int number = Integer.valueOf(timeDifference.trim());
//                        int number2 = Integer.parseInt(timeDifference.trim());
//                        if (number >= 2 || number2 >= 2) {
//                            //if (!timeDifference.trim().equals("1")) {
//                            //showLoadingDialog(getResources().getString(R.string.dlog));
//                            getDatas();
//                            SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
//                            //}
//                        }
//                    } else {
//                        getDatas();
//                        closeLoadingDialog();
//                        SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
//                    }
//                    if (isOnOnCreate) {
//                        isOnOnCreate = false;
//                        stateHandler.sendEmptyMessageDelayed(1000211, 1500);
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }

//                try {
//                    isHidden = true;
//                    String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
//                    if (MyCommandManager.DEVICENAME != null) {
//                        if (!TextUtils.isEmpty(homeTime)) {
//                            String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                            int number = Integer.valueOf(timeDifference.trim());
//                            int number2 = Integer.parseInt(timeDifference.trim());
//                            if (number >= 2 || number2 >= 2) {
//                                isStuta();
//                                getDatas();
//                                SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
//                            }
//                        } else {
//                            isStuta();
//                            getDatas();
//                            //closeLoadingDialog();
//                            SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }
            }
        } else {
            pageIsOne = 1;
            Log.d("===================", "No isVisible");
            isHidden = false;
            isHaertNull = true;
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        Log.d("===================", "onFragmentFirstVisible");
        pageIsOne = 1;
        isHaertNull = true;
    }


    private boolean isHidden = true;//离开界面隐藏显示同步数据的


    /**
     * 注册广播，监听刷新
     */
    private void regsBroad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyApp.RefreshBroad);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);
    }


    //同步用户信息
    private void syncUserInfoData() {
        try {
            String userData = (String) SharedPreferencesUtils.readObject(getContext(), "saveuserinfodata");
//        Log.d("-----用户资料-----AAA----", "--------" + userData);
            if (!WatchUtils.isEmpty(userData)) {
                try {
                    int weight;
                    JSONObject jsonO = new JSONObject(userData);
                    String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                    String userAge = jsonO.getString("birthday");   //生日
                    String userWeight = jsonO.getString("weight");  //体重
                    String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
//                Log.d("-----用户资料-----BBB----", userWeight + "====" + tempWeight);
                    if (tempWeight.contains(".")) {
                        weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim());
                    } else {
                        weight = Integer.valueOf(tempWeight);
                    }
                    String userHeight = ((String) SharedPreferencesUtils.getParam(getContext(), "userheight", "")).trim();
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
                    SharedPreferencesUtils.setParam(getContext(), "user_sex", sex);
                    SharedPreferencesUtils.setParam(getContext(), "user_age", age);
                    SharedPreferencesUtils.setParam(getContext(), "user_height", height);
                    SharedPreferencesUtils.setParam(getContext(), "user_weight", weight);
//                Log.d("-----用户资料-----CCC---", sex + "===" + age + "===" + height + "===" + weight);
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
        } catch (Exception e) {
            e.getMessage();
        }

    }

//    int heightY;
//    int scrollY = 1;

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        if (shouhuanImg != null)
            if (verticalBleName() != 0) shouhuanImg.setImageResource(verticalBleName());
//        if (previousImage != null) previousImage.setVisibility(View.GONE);
//        if (nextImage != null) nextImage.setVisibility(View.GONE);
        //手动刷新
//        if (swipeRefresh != null) swipeRefresh.setOnRefreshListener(new RefreshListenter());
        if (swipeRefresh != null && scroll_home != null) {
            swipeRefresh.setScrollUpChild(scroll_home);
            //手动刷新
            swipeRefresh.setOnRefreshListener(new RefreshListenter());
        }
        if (scroll_home != null) {
            scroll_home.setOnTouchListener(new TouchListenerImpl());
            scroll_home.getChildAt(0).getMeasuredHeight();
        }
    }


    int isWhat = 0;//判断  scroll_home 的偏移量


    private class TouchListenerImpl implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
                    int scrollY = view.getScrollY();
                    int height = view.getHeight();
                    int scrollViewMeasuredHeight = scroll_home.getChildAt(0).getMeasuredHeight();
                    int sc = scrollViewMeasuredHeight - height;

                    if (scrollY >= 0 && scrollY <= sc / 3) {
                        //Log.e("=========", "滑动到了顶端 "+scrollY);
                        isWhat = 0;
                    } else if (scrollY > (sc / 3 * 2) && (scrollY +height) <= scrollViewMeasuredHeight) {
                        //Log.e("=========", "滑动到了底部 "+scrollY+"="+height);
                        isWhat = 2;
                    } else {
                        isWhat = 1;
                        //Log.e("=========", "滑动到中间 "+scrollY+"="+height);
                    }
//                    if (scrollY == 0) {
//                        System.out.println("滑动到了顶端 view.getScrollY()=" + scrollY);
//                        Log.e("=========", "滑动到了顶端 "+scrollY);
//                    } else if ((scrollY + height) == scrollViewMeasuredHeight) {
//                        System.out.println("滑动到了底部 scrollY=" + scrollY);
//                        System.out.println("滑动到了底部 height=" + height);
//                        Log.e("=========", "滑动到了底部 "+scrollY+"="+height);
//                        System.out.println("滑动到了底部 scrollViewMeasuredHeight=" + scrollViewMeasuredHeight);
//                    }else {
//                        Log.e("=========", "滑动到中间 "+scrollY+"="+height);
//                    }
                    break;

                default:
                    break;
            }
            return false;
        }

    }

    //目标选择列表
    private ArrayList<String> daily_numberofstepsList;

    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }
    }

    private int verticalBleName() {
        String bName = (String) SharedPreferencesUtils.readObject(getContext(), Commont.BLENAME);
        SharedPreferencesUtils.readObject(getContext(), "mylanya");
        Log.e("==========名字=", bName);
        if (!WatchUtils.isEmpty(bName)) {
            if (bName.equals("W30")) {
                return R.mipmap.image_w30s;
            } else if (bName.equals("W31")) {
                return R.mipmap.icon_w31_home_top;
            } else {
                return 0;
            }
        }
        return 0;
    }


    /**
     * 初次获取数据
     */
    public void getDatas() {
        synchronized (this) {
            try {
                if (textAllSleepData != null) textAllSleepData.setVisibility(View.VISIBLE);
                DataAcy();
                if (isOneCreate) {
                    isOneCreate = false;
                    if (MyCommandManager.DEVICENAME != null) {
                        syncUserInfoData();
                        SharePeClear.sendCmdDatas(context);
                    }
                }
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            } catch (Exception e) {
                e.getMessage();
            }
        }

    }

    void DataAcy() {
        synchronized (MyApp.getContext()) {
            try {
                if (MyCommandManager.DEVICENAME != null) {
//                Log.d("======c========", "获取数据开始啦");
                    if (callDataBackListe != null) {
                        callDataBackListe = null;
                    } else {
                        callDataBackListe = new CallDataBackListe();
                    }
                    if (callDataBackListe == null) return;
                    MyApp.getInstance().getmW30SBLEManage().syncTime(callDataBackListe);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

    }




    List<UpHeartBean> upHeartBeanList = null;
    Handler mHandler = new Handler(new Handlers());


    /**
     * 将 list 转换 为 JSONArray
     *
     * @param list
     * @return
     */
    public JSONArray ProLogList2Json(List<W30S_SleepDataItem> list) {
        JSONArray json = new JSONArray();
        for (W30S_SleepDataItem pLog : list) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("sleep_type", pLog.getSleep_type());
                jo.put("startTime", pLog.getStartTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jo);
        }
        return json;
    }

    /**
     * 将 list 转换 为 JSONArray
     *
     * @param list
     * @return
     */
    public JSONArray ProLogListJson(List<UpHeartBean> list) {
        JSONArray json = new JSONArray();
        for (UpHeartBean pLog : list) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("userId", pLog.getUserId());
                jo.put("deviceCode", pLog.getDeviceCode());
                jo.put("systolic", pLog.getSystolic());
                jo.put("stepNumber", pLog.getStepNumber());
                jo.put("date", pLog.getDate());
                jo.put("heartRate", pLog.getHeartRate());
                jo.put("status", pLog.getStatus());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jo);
        }
        return json;
    }

    /**
     * 手动刷新
     */
    private class RefreshListenter implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {

            Log.d(TAG, "手动刷新");
            isStuta();
            getDatas();
        }
    }


    //心率返回集合
    //List<W30SHeartDataS> heartDatas = new ArrayList<>();
    List<Integer> heartDatasMaxOrLad = new ArrayList<>();

    /******************        ---------  扇形----------               **********************/
    int AWAKE2 = 0;//清醒
    int AWAKE = 0;//清醒次数
    int AOYE = 0;//熬夜
    int DEEP = 0;//深睡
    int SHALLOW = 0;//浅睡
    int ALLTIME = 0;//所有睡眠时间
    int NowVale = 0;//实时心率
    int NowValeSize = 0;//实时心率有效长度
    private boolean fanRoateAniamtionStart;
    private String timeFromMillisecondA = "0";
    private String timeFromMillisecondS = "0";
    private String timeFromMillisecondD = "0";


    //    View ----- 中的子控件
//    private PieChartView pieChartView;//睡眠饼状图（已舍弃）
    TextView L38iCalT, L38iDisT, textStepReach, t_mi;
    TextView qingxingT, qianshuiT, shenshuiT;//, textTypeData;
    TextView autoHeartTextNumber, maxHeartTextNumber, zuidiHeartTextNumber;//心率
    //TextView autoDatatext;//数据
    ImageView StepImageData, autoDataImage, SleepDatas;
    //-----清醒状态-------浅睡状态----深睡状态------清醒==改==》时常---浅睡----深睡
    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep, textAllSleepData;
    //---------入睡时间-----苏醒次数--------苏醒时间
    TextView textSleepInto, textSleepWake, textSleepTime;
    W30S_SleepChart w30S_sleepChart;
    SeekBar SleepseekBar, seekBarHared;
    FrameLayout frameLayout;
    LinearLayout line_heart_datas;
    TextView textView, heart_times, heart_more;
    TextView text_sleep_type, text_sleep_start, text_sleep_lines, text_sleep_end, sleep_into_time, sleep_out_time, text_sleep_nodata;
    LinearLayout line_time_star_end;
    /**
     * view pager数据
     */
    private int DISTANCE = 0;//距离
    private int CALORIES = 0;//卡路里
    WaveProgress recordwaveProgressBar;
    TextView watchRecordTagstepTv;

    private void setDatas(View view) {

//        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_watch_record_change, null);
        recordwaveProgressBar = (WaveProgress) view.findViewById(R.id.recordwave_progress_bar);
        L38iCalT = (TextView) view.findViewById(R.id.watch_recordKcalTv);
        L38iDisT = (TextView) view.findViewById(R.id.watch_recordMileTv);

        t_mi = (TextView) view.findViewById(R.id.t_mi);
        StepImageData = (ImageView) view.findViewById(R.id.stepData_imageView);

//        StepImageData.setVisibility(View.VISIBLE);
        StepImageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        StepHistoryDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        String w30stag = (String) SharedPreferencesUtils.getParam(context, "w30stag", "10000");
        GOAL = Integer.valueOf(w30stag);
        recordwaveProgressBar.setMaxValue(GOAL);
        recordwaveProgressBar.setValue(STEP);
        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
        recordwaveProgressBar.setTagStepStr(getResources().getString(R.string.settarget_steps) + tagGoal);

        textStepReach = (TextView) view.findViewById(R.id.text_step_reach);


        watchRecordTagstepTv = (TextView) view.findViewById(R.id.watch_recordTagstepTv);
        watchRecordTagstepTv.setVisibility(View.GONE);

//        View view2 = LayoutInflater.from(context).inflate(R.layout.b18i_leaf_linechart_view, null);
//        lineChart = (LineChartView) view2.findViewById(R.id.heart_chart);
        lineChart = (W30CusHeartView) view.findViewById(R.id.heart_chart);
        line_heart_datas = (LinearLayout) view.findViewById(R.id.line_heart_datas);
//        textTypeData = (TextView) view.findViewById(R.id.data_type_text);
        seekBarHared = (SeekBar) view.findViewById(R.id.seek_bar_my_heart);
        frameLayout = (FrameLayout) view.findViewById(R.id.frm_heard);
        textView = (TextView) view.findViewById(R.id.heard_value);
        heart_times = (TextView) view.findViewById(R.id.heart_times);
        heart_more = (TextView) view.findViewById(R.id.heart_more);
        seekBarHared.setEnabled(false);
        seekBarHared.setMax(Math.abs(51));


        lineChart.setmDataTypeListenter(new W30CusHeartView.DataTypeListenter() {
            /**
             * @param value  心率值
             * @param times  时间
             * @param toTime 是否已经测量
             */
            @Override
            public void OnDataTypeListenter(int value, String times, boolean toTime) {

                //Log.e("-----------AA", "华动中" + value + "====times=" + times);
                //if (frameLayout != null) frameLayout.setVisibility(View.VISIBLE);
                if (heart_more != null) heart_more.setVisibility(View.GONE);
                if (heart_times != null) {
                    heart_times.setVisibility(View.VISIBLE);
                    heart_times.setText(times);
                }
                if (textView != null) {
                    textView.setVisibility(View.VISIBLE);
                    if (toTime) {
                        textView.setText("  " + value);
                    } else {
                        textView.setText("  " + getResources().getString(R.string.nodata));
                    }
                }


            }
        });//图表数据
        seekBarHared.setOnSeekBarChangeListener(this);

//
//
//        seekBarHared.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
//                    if (seekBarHared != null) {
//                        seekBarHared.setProgress(0);
//                        seekBarHared.clearAnimation();
//                        seekBarHared.invalidate();
//                    }
//                    if (lineChart != null) lineChart.setSeekBar(0);
//                    //if (frameLayout != null) frameLayout.setVisibility(View.INVISIBLE);
//                    if (heart_more != null) heart_more.setVisibility(View.VISIBLE);
//                    if (heart_times != null) {
//                        heart_times.setVisibility(View.GONE);
//                    }
//                    if (textView != null) {
//                        textView.setVisibility(View.GONE);
//                    }
//                    return;
//                }
//
//            }
//        });

//        textTypeData.setVisibility(View.GONE);
//        view.findViewById(R.id.leaf_chart).setVisibility(View.GONE);
//        view.findViewById(R.id.heart_lines).setVisibility(View.GONE);
        autoHeartTextNumber = (TextView) view.findViewById(R.id.autoHeart_text_number);//平均心率---可点击
        maxHeartTextNumber = (TextView) view.findViewById(R.id.maxHeart_text_number);//最高心率---可点击
        zuidiHeartTextNumber = (TextView) view.findViewById(R.id.zuidiHeart_text_number);//最低心率---可点击
        view.findViewById(R.id.autoData_text).setVisibility(View.GONE);
        //autoDatatext = (TextView) view2.findViewById(R.id.autoData_text);//数据---可点击
        autoDataImage = (ImageView) view.findViewById(R.id.autoData_imageView);//历史数据---可点击
//        autoDataImage.setVisibility(View.VISIBLE);
        autoDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        H9HearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        //点击是事件
        line_heart_datas.setOnClickListener(new MyViewLister());
//        View view3 = LayoutInflater.from(context).inflate(R.layout.w30s_pie_chart_view, null);
//        pieChartView = (PieChartView) view3.findViewById(R.id.pieChartView);//睡眠饼状图（已经舍弃）
        w30S_sleepChart = (W30S_SleepChart) view.findViewById(R.id.sleep_chart);
        text_sleep_nodata = (TextView) view.findViewById(R.id.text_sleep_nodata);
        w30S_sleepChart.setVisibility(View.INVISIBLE);
        text_sleep_nodata.setVisibility(View.VISIBLE);
        SleepseekBar = (SeekBar) view.findViewById(R.id.seek_bar_my_sleep);
        text_sleep_type = (TextView) view.findViewById(R.id.text_sleep_type);
        text_sleep_start = (TextView) view.findViewById(R.id.text_sleep_start);
        text_sleep_lines = (TextView) view.findViewById(R.id.text_sleep_lines);
        text_sleep_end = (TextView) view.findViewById(R.id.text_sleep_end);
        sleep_into_time = (TextView) view.findViewById(R.id.sleep_into_time);
        sleep_out_time = (TextView) view.findViewById(R.id.sleep_out_time);
        line_time_star_end = (LinearLayout) view.findViewById(R.id.line_time_star_end);
        //line_time_star_end.setVisibility(View.GONE);
        text_sleep_type.setText(" ");
        text_sleep_start.setText(" ");
        text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));
//        if (MyApp.getInstance().AppisOneStar) {
//            MyApp.getInstance().AppisOneStar = false;
//            text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));
//        } else {
//            text_sleep_lines.setText(" ");
//        }
        text_sleep_end.setText(" ");
        SleepseekBar.setEnabled(false);
        SleepseekBar.setMax(Math.abs(500));
        w30S_sleepChart.setmDataTypeListenter(new W30S_SleepChart.DataTypeListenter() {
            @Override
            public void OnDataTypeListenter(String type, String startTime, String endTime) {

                if (text_sleep_lines != null) text_sleep_lines.setText(" -- ");
                if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                    text_sleep_type.setText(getResources().getString(R.string.waking_state));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                } else if (type.equals("2")) {  //潜睡状态
                    text_sleep_type.setText(getResources().getString(R.string.shallow_sleep));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                } else if (type.equals("3")) {  //深睡
                    text_sleep_type.setText(getResources().getString(R.string.deep_sleep));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                } else if (type.equals("88")) {
                    //Log.d("----------", "起床");
                    text_sleep_type.setText(getResources().getString(R.string.getup));
                    text_sleep_start.setText(startTime);
                    text_sleep_end.setText(endTime);
                }
            }
        });

        SleepseekBar.setOnSeekBarChangeListener(this);
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
//                    if (text_sleep_lines != null)
//                        text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));//text_sleep_lines.setText(" ");
//                    if (text_sleep_end != null) text_sleep_end.setText(" ");
//                }
//            }
//        });
        awakeState = (TextView) view.findViewById(R.id.awakeState);
        shallowState = (TextView) view.findViewById(R.id.shallowState);
        deepState = (TextView) view.findViewById(R.id.deepState);
        textAllSleepData = (TextView) view.findViewById(R.id.text_all_sleep_data);
        SleepDatas = (ImageView) view.findViewById(R.id.sleepData_imageView);
//        SleepDatas.setVisibility(View.VISIBLE);
        SleepDatas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        SleepHistoryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            }
        });
        awakeSleep = (TextView) view.findViewById(R.id.awake_sleep);
        shallowSleep = (TextView) view.findViewById(R.id.shallow_sleep);
        deepSleep = (TextView) view.findViewById(R.id.deep_sleep);

        qingxingT = (TextView) view.findViewById(R.id.w30_qingxing_text);
        qianshuiT = (TextView) view.findViewById(R.id.w30_qianshui_text);
        shenshuiT = (TextView) view.findViewById(R.id.w30_shenshui_text);

        textSleepInto = (TextView) view.findViewById(R.id.text_sleep_into);//入睡时间
        textSleepWake = (TextView) view.findViewById(R.id.text_sleep_wake);//苏醒次数
        textSleepTime = (TextView) view.findViewById(R.id.text_sleep_time);//苏醒时间
        awakeSleep.setText(timeFromMillisecondA);
        shallowSleep.setText(timeFromMillisecondS);
        deepSleep.setText(timeFromMillisecondD);
        awakeState.setText(getResources().getString(R.string.string_qingxing));//清醒状态
        shallowState.setText(getResources().getString(R.string.sleep_light));//浅睡眠
        deepState.setText(getResources().getString(R.string.sleep_deep));//深睡眠
//        List<View> fragments = new ArrayList<>();
//        fragments.add(view);
//        fragments.add(view);
//        fragments.add(view);
//        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
//        l38iViewpager.setOffscreenPageLimit(3);
        //l38iViewpager.setCurrentItem(3);
//        setLinePontion(fragments);
//        l38iViewpager.setAdapter(adapter);
//        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        //Log.e("=========","华东啦 "+progress);
        switch (seekBar.getId()){
            case R.id.seek_bar_my_heart:
                if (lineChart != null) lineChart.setSeekBar((float) progress);
                //Log.e("========心率=","华东啦 "+progress);
                break;
            case R.id.seek_bar_my_sleep:
                w30S_sleepChart.setSeekBar((float) progress);
                //Log.e("========睡眠=","华东啦 "+progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.seek_bar_my_heart:
                //Log.e("========心率=","华东啦结束 ");
                boolean clickable = SleepseekBar.isClickable();
                if (!clickable) {
                    if (seekBarHared != null) {
                        seekBarHared.setProgress(0);
                        seekBarHared.clearAnimation();
                        seekBarHared.invalidate();
                    }
                    if (lineChart != null) lineChart.setSeekBar(0);
                    //if (frameLayout != null) frameLayout.setVisibility(View.INVISIBLE);
                    if (heart_more != null) heart_more.setVisibility(View.VISIBLE);
                    if (heart_times != null) {
                        heart_times.setVisibility(View.GONE);
                    }
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.seek_bar_my_sleep:
                boolean clickable1 = SleepseekBar.isClickable();
                //Log.e("========睡眠=","华东啦结束 ");
                if (!clickable1) {
                    SleepseekBar.setProgress(0);
                    SleepseekBar.clearAnimation();
                    SleepseekBar.invalidate();
                    w30S_sleepChart.setSeekBar(0);
                    if (text_sleep_type != null) text_sleep_type.setText(" ");
                    if (text_sleep_start != null) text_sleep_start.setText(" ");
                    if (text_sleep_lines != null)
                        text_sleep_lines.setText(getResources().getString(R.string.string_selecte_sleep_stuta));//text_sleep_lines.setText(" ");
                    if (text_sleep_end != null) text_sleep_end.setText(" ");
                }
                break;
        }

    }







    private W30CusHeartView lineChart;
    //心率图标数据
    List<Integer> heartList;

//    /**
//     * 滑动小圆点
//     *
//     * @param fragments
//     */
//
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
//            recordwaveProgressBar.postInvalidate();
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            PAGES = position;
//            if (position == 2) {
//                if (textStute != null) {
//                    int visibility = textStute.getVisibility();
//                    if (visibility == 0x00000008) {
//                        if (textAllSleepData != null) {
//                            textAllSleepData.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            }
//            PointSetting(position);
//        }
//
//        private void PointSetting(int position) {
//            l38iViewpager.setCurrentItem(position);
//            if (isOneonResume) {
//                isOneonResume = false;
//                //isStuta();
//                getDatas();
//            }
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


    @OnClick({R.id.watch_poorRel, R.id.battery_watchRecordShareImg, R.id.history_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_poorRel:    //点击是否连接
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    startActivity(new Intent(getActivity(), W30SSettingActivity.class).putExtra("is18i", "W30S"));
                } else {
                    startActivity(new Intent(getContext(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                if (getActivity() == null || getActivity().isFinishing())
                    return;
//                Intent intent = new Intent(getActivity(), SharePosterActivity.class);
//                intent.putExtra("is18i", "W30S");
//                intent.putExtra("stepNum", STEP + "");
//                startActivity(intent);
                startActivity(new Intent(getActivity(),W30DetailSportActivity.class));
                //startActivity(new Intent(getActivity(),CommentDataActivity.class));
                //startActivity(new Intent(context, SharePosterActivity.class).putExtra("is18i", "W30S"));
                break;
            case R.id.history_data:

                if (isWhat ==0) {
                    startActivity(new Intent(context,
                            StepHistoryDataActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
                } else if (isWhat==1) {
                    startActivity(new Intent(context,
                            H9HearteDataActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
                } else if (isWhat==2)  {
                    startActivity(new Intent(context,
                            SleepHistoryActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
                }
//                Log.e("==========", scrollY + "===" + heightY);
//                if (scrollY <= heightY) { //1

//                } else if (heightY < scrollY && scrollY <= heightY * 2) { //2

//                } else if (heightY * 2 < scrollY && scrollY <= heightY * 3) { //3

//                }
                break;
//            case R.id.recordwave_progress_bar:
//                startActivity(new Intent(context,
//                        StepHistoryDataActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
//                break;
//            case R.id.re_xin:
//                startActivity(new Intent(context,
//                        H9HearteDataActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
//                break;
//            case R.id.fr_shui:
//                startActivity(new Intent(context,
//                        SleepHistoryActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
//                break;
        }
    }


    //显示电量
    private void setBatteryPowerShow(int battery) {
        try {
//            Log.e(TAG, "----------battery=" + battery);
            int batterys = 0;
            if (battery >= 0 && battery < 20) {
                batterys = 0;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
            } else if (battery >= 20 && battery < 40) {
                batterys = 25;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
            } else if (battery >= 40 && battery < 60) {
                batterys = 50;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
            } else if (battery >= 60 && battery < 80) {
                batterys = 75;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
            } else if (battery >= 80) {
                batterys = 100;
                watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
            }
            batteryPowerTv.setText(batterys + "%");
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private class MyViewLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), HeartRateActivity.class).putExtra("is18i", "H9");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            if (MyCommandManager.DEVICENAME != null && !MyCommandManager.DEVICENAME.equals("W30") && !MyCommandManager.DEVICENAME.equals("W31")) {
                startActivity(new Intent(context,
                        H9HearteTestActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S"));
            } else {//if (MyCommandManager.DEVICENAME != null && ( MyCommandManager.DEVICENAME.equals("W30")|| MyCommandManager.DEVICENAME.equals("W31")))
                startActivity(new Intent(context,
                        W30SHearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "W30S_HEART"));
            }
        }
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.e(TAG, "-------广播----action=" + action);
            try {
                switch (action) {
                    case MyApp.RefreshBroad:
                        if (MyCommandManager.DEVICENAME != null && (MyCommandManager.DEVICENAME.equals("W30") || MyCommandManager.DEVICENAME.equals("W31"))) {
                            if (isHidden) {
                                //Log.d(TAG, "W30S同步成功");
                                //isStuta();
                                getDatas();
                            }
                        }
                        break;
                    case W30SBLEServices.ACTION_GATT_CONNECTED:     //连接成功
                        //Log.d("----------w30--", "ACTION_GATT_CONNECTED");
                        String bName = (String) SharedPreferencesUtils.readObject(getContext(), Commont.BLENAME);
                        MyCommandManager.DEVICENAME = !WatchUtils.isEmpty(bName) ? "W30" : bName;

                        mHandler.sendEmptyMessage(0x60);
                        onesApp = true;
                        mHandler.sendEmptyMessageDelayed(0x02, 1500);
                        break;
                    case W30SBLEServices.ACTION_GATT_DISCONNECTED:  //断开连接
                        //Log.d("----------w30--", "ACTION_GATT_DISCONNECTED");
                        MyCommandManager.DEVICENAME = null;
                        mHandler.sendEmptyMessageDelayed(0x02, 1500);
                        break;
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    };


    public class Handlers implements Handler.Callback {


        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x60:
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (shouhuanImg != null)
                            if (verticalBleName() != 0)
                                shouhuanImg.setImageResource(verticalBleName());
                    }

                    break;
                case 0x01:
                    if (isHidden) {
                        mHandler.removeMessages(0x01);
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            if (textStute != null)
                                textStute.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case 0x02:
                    mHandler.removeMessages(0x02);
                    isStuta();
                    getDatas();
                    break;
                case 0x03:
                    if (MyCommandManager.DEVICENAME != null) {
                        mHandler.removeMessages(0x03);
                        //isStuta();
                        getDatas();
                    } else {
                        mHandler.sendEmptyMessageDelayed(0x03, 1000);
                    }
                    break;
                case 0x04:
                    mHandler.removeMessages(0x04);
                    if (isHidden) {
                        if (textStute != null) {
                            int visibility = textStute.getVisibility();
                            //Log.d("=========", visibility + "");
                            if (visibility == 0x00000000) {
                                getDatas();
                                mHandler.sendEmptyMessageDelayed(0x04, 5000);
                            } else {
                                mHandler.removeMessages(0x04);
                            }
                        }
                    }
                    break;
                case 0x05:
                    getDatas();
                    isHaertNull = false;
                    break;
                case 1001:  //睡眠
                    try {
                        //Log.e(TAG, "-------hand1001---");
                        List<W30S_SleepDataItem> sleepDataList = (List<W30S_SleepDataItem>) msg.obj;
                        //Log.e(TAG, "------hand--size=" + sleepDataList.size());

                        if (isHidden) {
                            if (sleepDataList == null && sleepDataList.size() <= 0) {
                                w30S_sleepChart.setVisibility(View.GONE);
                                text_sleep_nodata.setVisibility(View.VISIBLE);
                            } else {
                                //Log.e("=========A===", sleepDataList.size() + "");
                                sleepDataList.remove(0);
                                w30S_sleepChart.setVisibility(View.VISIBLE);
                                text_sleep_nodata.setVisibility(View.GONE);
                                //Log.e("=========B===", sleepDataList.size() + "");
                                w30S_sleepChart.setBeanList(sleepDataList);
                                //入睡时间
                                if (sleep_into_time != null) {
                                    sleep_into_time.setVisibility(View.VISIBLE);
                                    if (sleepDataList.get(0) != null)
                                        sleep_into_time.setText(sleepDataList.get(0).getStartTime());
                                }
                                //醒来时间
                                if (sleep_out_time != null) {
                                    sleep_out_time.setVisibility(View.VISIBLE);
                                    sleep_out_time.setText(sleepDataList.get(sleepDataList.size() - 1).getStartTime());
                                }
                                //总睡眠设置为可拖动最大进度
                                if (SleepseekBar != null) {
                                    SleepseekBar.setMax(ALLTIME);
                                    SleepseekBar.setEnabled(true);
                                }
                                DecimalFormat df = new DecimalFormat("#.#");
                                df.setRoundingMode(RoundingMode.FLOOR);
                                String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).replace(",", "").trim();
                                //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);
                                if (deepSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
                                    deepSleep.setText(setScale + getResources().getString(R.string.hour));
                                }
                                if (shallowSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
                                    if (!WatchUtils.isEmpty(div3)) {
                                        double v = Double.valueOf(div3.replace(",", "").replace(" ", "")) - setScale;
                                        double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
                                        //double setScale = (double) WatchUtils.div((double) SHALLOW, 60, 1);
                                        shallowSleep.setText(setScale1 + getResources().getString(R.string.hour));
                                    }
                                }
                                if (awakeSleep != null) {
                                    double setScale = (double) WatchUtils.div((double) AWAKE2, 60, 1);
                                    awakeSleep.setText(setScale + getResources().getString(R.string.hour));
                                }
                                double hour = (double) (DEEP + SHALLOW) / (double) 60;
                                String format = new DecimalFormat("0.00").format(hour);
                                String[] split = format.split("[.]");
                                int integer = Integer.valueOf(split[0].replace(",", "").trim());
                                String s1 = String.valueOf(((hour - integer) * 60));
                                String[] split1 = s1.split("[.]");
                                String a = "0";
                                if (split1[0] != null) {
                                    a = split1[0].trim();
                                }
                                String w30ssleep = (String) SharedPreferencesUtils.getParam(getContext(), "w30ssleep", "8");
                                if (!WatchUtils.isEmpty(w30ssleep)) {
                                    int standardSleepAll = Integer.valueOf(w30ssleep.replace(",", "").trim()) * 60;
                                    int allSleep = integer * 60 + Integer.valueOf(a.replace(",", ""));
                                    double standardSleep = WatchUtils.div(allSleep, standardSleepAll, 2);
                                    //int standar = (allSleep / standardSleepAll) * 100;
                                    String strings = String.valueOf((standardSleep * 100));
                                    if (textAllSleepData != null) {
                                        textAllSleepData.setVisibility(View.VISIBLE);
                                        if (textAllSleepData != null) {
                                            textAllSleepData.setVisibility(View.VISIBLE);
                                            if (strings.contains(".")) {
                                                textAllSleepData.setText(getResources().getString(R.string.sleep) + ":" + div3 + getResources().getString(R.string.hour)
                                                        + "    " + getResources().getString(R.string.string_standar) + ":" + strings.split("[.]")[0] + "%"
                                                        + "    " + getResources().getString(R.string.recovery_count_frequency) + ":" + AWAKE);
                                            } else {
                                                textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour)
                                                        + "    " + getResources().getString(R.string.string_standar) + (standardSleep * 100) + "%"
                                                        + "    " + getResources().getString(R.string.recovery_count) + AWAKE + getResources().getString(R.string.cishu));
                                            }
                                        }
                                    }
                                } else {
                                    if (textAllSleepData != null)
                                        textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour) + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
                                }


                                double v = SHALLOW + AWAKE2 + DEEP;
                                if (qianshuiT != null) {
                                    double v1 = WatchUtils.div(SHALLOW, v, 2);
                                    if (v1 > 0) {
                                        qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }
                                if (qingxingT != null) {
                                    double v1 = WatchUtils.div(AWAKE2, v, 2);
                                    if (v1 > 0) {
                                        qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }
                                if (shenshuiT != null) {
                                    double v1 = WatchUtils.div(DEEP, v, 2);
                                    if (v1 > 0) {
                                        shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
                                    }
                                }


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
                                        DEEP,
                                        SHALLOW,
                                        AWAKE2,
                                        (int)v,
                                        sleep_into_time.getText().toString(),
                                        sleep_out_time.getText().toString(),
                                        AWAKE);


                            }
                            //setH9PieCharts(AWAKE2, DEEP, SHALLOW);
                        }
                    } catch (Error error) {
                    }


                    break;
                case 0x98:
                    W30SSportData objects1 = (W30SSportData) msg.obj;
                    if (objects1 != null || !WatchUtils.isEmpty(objects1.toString())) {
                        String data = objects1.getData();
                        float calory = objects1.getCalory();
                        float distance = objects1.getDistance();
                        int sportStep = objects1.getSportStep();
                        UpDatasBase.updateLoadSportToServer(GOAL, sportStep, calory, distance, data);
                    }
                    break;
                case 0x99:
                    W30SSportData objects = (W30SSportData) msg.obj;
                    if (objects != null || !WatchUtils.isEmpty(objects.toString())) {
                        List sport_data = objects.getSport_data();
                        if (sport_data != null && !sport_data.isEmpty()) {
                            if (upHeartBeanList == null) {
                                upHeartBeanList = new ArrayList<>();
                            } else {
                                upHeartBeanList.clear();
                            }
                            for (int i = 1; i <= sport_data.size(); i++) {
                                String tim = objects.getData().trim().replace(" ", "") + " " + "01:00";

                                if (i <= 9) {
                                    tim = objects.getData().trim().replace(" ", "") + " " + "0" + i + ":00";
                                } else {
                                    tim = objects.getData().trim().replace(" ", "") + " " + i + ":00";
                                }
                                String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
                                String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
//                                upHeartBeanList.add((i - 1), new UpHeartBean(userId, mac, "00", sport_data.get(i - 1) + "", tim, "00", "0", "00", "00"));

                                UpHeartBean upHeartBean = new UpHeartBean(userId, mac,
                                        0, 0,
                                        0, 0,
                                        0, tim,
                                        (int) sport_data.get(i - 1));
                                upHeartBeanList.add((i - 1), upHeartBean);
                            }

                            if (upHeartBeanList != null&&!upHeartBeanList.isEmpty()) {
                                JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
                                UpDatasBase.upAllDataSteps(jsonArray1);
                            }
                        }
                    }
                    break;
                case 0x88:
                    W30SSleepData sleepDatas = (W30SSleepData) msg.obj;
                    JSONArray jsonArray = ProLogList2Json(sleepDatas.getSleepDataList());
                    UpDatasBase.upDataTodaSleep(sleepDatas.getSleepData(), jsonArray);
                    break;
                case 0x77:

                    //开始上传
                    CommDBManager.getCommDBManager().startUploadDbService(getContext());

                    List<UpHeartBean> upHeartBeanList = (List<UpHeartBean>) msg.obj;
                    if (upHeartBeanList != null && !upHeartBeanList.isEmpty()) {
                        JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
                        UpDatasBase.upAllDataHearte(jsonArray1);//上传心率}
                    }
                    break;
            }
            return false;
        }

    }

    private class CallDataBackListe implements W30SBLEServices.CallDatasBackListenter {

        @Override
        public void CallDatasBackListenter(final W30SSportData objects) {
            if (objects == null || WatchUtils.isEmpty(objects.toString())) return;

            Log.e(TAG,"-----------W30SSportData="+objects.toString());

            //2018-03-09===0.0===0.0===0
            //Log.d(TAG, "解析运动数据 日期 =  " + objects.getData());
            //Log.d(TAG, "解析运动数据 步数 =  " + objects.getSportStep());
            //Log.d(TAG, "解析运动数据 卡路里 =  " + objects.getCalory());
            //Log.d(TAG, "解析运动数据 距离 =  " + objects.getDistance());
            //Log.d(TAG, "解析运动数据 数据 =  " + objects.getSport_data().toString());

            STEP = 0;
            //转为Kc保留两位
            CALORIES = 0;
            DISTANCE = 0;
            CALORIES = (int) objects.getCalory();
            DISTANCE = (int) objects.getDistance();
            STEP = objects.getSportStep();
            //保存步数
            CommDBManager.getCommDBManager().saveCommCountStepDate(WatchUtils.getSherpBleName(MyApp.getContext()),
                    WatchUtils.getSherpBleMac(MyApp.getContext()),objects.getData(),((int)STEP));



            //将步数的数组转换成date:HH:mm,value:0x00 格式的数据

            List<Map<String,String>> changeListMap = new ArrayList<>();
            for(int i = 0;i<objects.getSport_data().size();i++){
                Map<String,String> mp = new HashMap<>();
                int sportDate = i+1;
                mp.put("stepDate",sportDate<10?("0"+sportDate+":00"):sportDate+":00");
                mp.put("stepValue",objects.getSport_data().get(i)+"");
                changeListMap.add(mp);
            }
            B30HalfHourDao.getInstance().saveW30SportData(objects.getData(),WatchUtils.getSherpBleMac(getActivity()),
                    B30HalfHourDao.TYPE_STEP,new Gson().toJson(changeListMap));






//                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 0);
//                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                final String sportData = objects.getData();
//                boolean isSharpe = true;
//                if (sportData.equals(nextDay)) {
//                    isSharpe = true;
//                } else {
//                    isSharpe = false;
//                }
//                String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSportTime", "2017-11-02 15:00:00");
//                int sportStep = objects.getSportStep();
//                String calory = String.valueOf(objects.getCalory());
//                String distance = String.valueOf(objects.getDistance());
//
//                if (sportStep >= 0) {
//                    STEP = objects.getSportStep();
//                }
//                if (!WatchUtils.isEmpty(calory)) {
//                    CALORIES = Integer.valueOf(calory.split("[.]")[0]);
//                }
//                if (!WatchUtils.isEmpty(distance)) {
//                    DISTANCE = Integer.valueOf(distance.split("[.]")[0]);
//                }
//                SharedPreferencesUtils.setParam(getContext(), "step_number", STEP + "");
//
//                if (!TextUtils.isEmpty(homeTime)) {
//                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                    int number = Integer.valueOf(timeDifference.trim());
//                    int number2 = Integer.parseInt(timeDifference.trim());
//                    if (Math.abs(number) >= 180 || Math.abs(number2) >= 180) {
//
//                        //上传运动数据
//                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
////                                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, "2018-11-23");
//                        if (isSharpe) {
//                            SharedPreferencesUtils.setParam(getActivity(), "upSportTime", B18iUtils.getSystemDataStart());
//                        }
//                    }
//                } else {
//                    //上传运动数据
//                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
////                                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, "2018-11-23");
//                    if (isSharpe) {
//                        SharedPreferencesUtils.setParam(getActivity(), "upSportTime", B18iUtils.getSystemDataStart());
//                    }
//
//                }

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHidden) {
                            String w30stag = (String) SharedPreferencesUtils.getParam(context, "w30stag", "10000");
                            GOAL = Integer.valueOf(w30stag);
//                            double v = (double) STEP / (double) GOAL;
//                            String s = String.valueOf(v * 100).split("[.]")[0] + "%";
//                            if (textStepReach != null) textStepReach.setText(s);

                            String trim = new DecimalFormat("#.##").format(((double) STEP / (double) GOAL) * 100).trim();
                            if (!WatchUtils.isEmpty(trim) && trim.contains(".")) {
                                textStepReach.setText(trim.split("[.]")[0] + "%");//设置达标值
                            } else {
                                textStepReach.setText(trim + "%");//设置达标值
                            }

                            String data = objects.getData();
                            if (!TextUtils.isEmpty(data)) {
                                if (watchRecordtopDateTv != null)
                                    watchRecordtopDateTv.setText(data);
                                if (L38iCalT != null)
                                    L38iCalT.setText("" + CALORIES + "");
                                boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(getContext(), "w30sunit", true);
//                                if (w30sunit) {
//                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_mi);// 获取res下的图片drawable
//                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
//                                    if (L38iDisT != null)
//                                        L38iDisT.setCompoundDrawables(null, top, null, null);
//                                    if (L38iDisT != null)
//                                        L38iDisT.setText("" + DISTANCE + "");
//                                } else {
//                                    float distances = objects.getDistance();
//                                    int round = (int) Math.floor(distances * 3.28);
//                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_unti_ft);// 获取res下的图片drawable
//                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
//                                    if (L38iDisT != null)
//                                        L38iDisT.setCompoundDrawables(null, top, null, null);
//                                    if (L38iDisT != null)
//                                        L38iDisT.setText(String.valueOf(round).split("[.]")[0]);
//                                }

                                if (w30sunit) {
                                    t_mi.setText("m");
                                    BigDecimal decimal = new BigDecimal(DISTANCE);
                                    BigDecimal setScale = decimal.setScale(0, BigDecimal.ROUND_DOWN);
                                    L38iDisT.setText(setScale.intValue() + "");
                                } else {
                                    t_mi.setText("ft");
                                    BigDecimal decimal = new BigDecimal(Math.floor(DISTANCE * 3.28));
                                    BigDecimal setScale = decimal.setScale(0, BigDecimal.ROUND_DOWN);
                                    L38iDisT.setText(setScale.intValue() + "");
                                }

                                if (recordwaveProgressBar != null) {
                                    recordwaveProgressBar.postInvalidate();
                                    recordwaveProgressBar.setValue(STEP);
                                    recordwaveProgressBar.postInvalidate();
                                }
                            }
                        }
                    }
                });
            }

            /**
             * 发送消息上传数据
             */
            Message message = mHandler.obtainMessage();
            message.what = 0x99;
            message.obj = objects;
            mHandler.sendMessage(message);

            Message message1 = mHandler.obtainMessage();
            message1.what = 0x98;
            message1.obj = objects;
            mHandler.sendMessage(message1);


//            if (mHandler != null) {
//                final String finalTimes = times;
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 1; i <= sport_data.size(); i++) {
//
//                            String tim = finalTimes.substring(0, finalTimes.length() - 1).trim() + " " + "01:00";
//                            if (i <= 9) {
//                                tim = finalTimes.substring(0, finalTimes.length() - 1).trim() + " " + "0" + i + ":00";
//                            } else {
//                                tim = finalTimes.substring(0, finalTimes.length() - 1).trim() + " " + i + ":00";
//                            }
//                            Log.d("===========步数", i + "------" + tim + "--" + sport_data.get(i - 1) + "");
//                            UpDatasBase.upDataStep("00", tim, sport_data.get(i - 1) + "");//上传心率
//                        }
//                    }
//                });
//            }

//            Message message = mHandler.obtainMessage();
//            message.what = 0x06;
//            message.obj = objects;
//            if (mHandler != null) mHandler.sendMessage(message);
//            try {
//                STEP = 0;
//                //转为Kc保留两位
//                CALORIES = 0;
//                DISTANCE = 0;
//                //2018-03-09===0.0===0.0===0
//                Log.d(TAG, "解析运动数据 日期 =  " + objects.getData());
//                Log.d(TAG, "解析运动数据 步数 =  " + objects.getSportStep());
//                Log.d(TAG, "解析运动数据 卡路里 =  " + objects.getCalory());
//                Log.d(TAG, "解析运动数据 距离 =  " + objects.getDistance());
//                Log.d(TAG, "解析运动数据 数据 =  " + objects.getSport_data().toString());
//
//
//                List sport_data = objects.getSport_data();
//                ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
//                for (int i = 1; i <= sport_data.size(); i++) {
//                    String tmH = "01:00";
//                    if (i <= 9) {
//                        tmH = "0" + i + ":00";
//                    } else {
//                        tmH = i + ":00";
//                    }
//                    Map<String, String> listMap = new HashMap<String, String>();
//                    listMap.put(tmH, sport_data.get(i).toString());
//                    arrayList.add(listMap);
//                }
//                String res = new Gson().toJson(arrayList);
//                Log.d(TAG, "----josn返回--" + res);
//
//                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 0);
//                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                String sportData = objects.getData();
//                boolean isSharpe = true;
//                if (objects.equals(nextDay)) {
//                    isSharpe = true;
//                } else {
//                    isSharpe = false;
//                }
//                String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSportTime", "2017-11-02 15:00:00");
//                int sportStep = objects.getSportStep();
//                String calory = String.valueOf(objects.getCalory());
//                String distance = String.valueOf(objects.getDistance());
//
//                if (sportStep >= 0) {
//                    STEP = objects.getSportStep();
//                }
//                if (!WatchUtils.isEmpty(calory)) {
//                    CALORIES = Integer.valueOf(calory.split("[.]")[0]);
//                }
//                if (!WatchUtils.isEmpty(distance)) {
//                    DISTANCE = Integer.valueOf(distance.split("[.]")[0]);
//                }
//                SharedPreferencesUtils.setParam(getContext(), "step_number", STEP + "");
//
//
//                if (!TextUtils.isEmpty(homeTime)) {
//                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                    int number = Integer.valueOf(timeDifference.trim());
//                    int number2 = Integer.parseInt(timeDifference.trim());
//                    if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
//                        //上传运动数据
//                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
////                        Log.d("====解析运动数据=isSharpe=1==", isSharpe + "=========" + number);
//                    }
//                } else {
//                    //上传运动数据
//                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
//                }
//                if (isSharpe) {
//                    SharedPreferencesUtils.setParam(getActivity(), "upSportTime", B18iUtils.getSystemDataStart());
//                }
////                                String upSportTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSportTime", "2017-11-02 15:00:00");
////                                if (!TextUtils.isEmpty(upSportTime)) {
////                                    String timeDifference = H9TimeUtil.getTimeDifferencesec(upSportTime, B18iUtils.getSystemDataStart());
////                                    int number = Integer.valueOf(timeDifference.trim());
////                                    int number2 = Integer.parseInt(timeDifference.trim());
////                                    if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
////                                        //上传运动数据
////                                        UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
////                                        Log.d("====解析运动数据=isSharpe=1==", isSharpe + "========="+number);
////                                        if (isSharpe) {
////                                            SharedPreferencesUtils.setParam(getActivity(),"upSportTime",B18iUtils.getSystemDataStart());
////                                        }
////                                    }
////                                } else {
////                                    //上传运动数据
////                                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE, sportData);
//////                                Log.d("====解析运动数据=isSharpe=2==", isSharpe + "");
////                                    if (isSharpe) {
////                                        SharedPreferencesUtils.setParam(getActivity(),"upSportTime",B18iUtils.getSystemDataStart());
////                                    }
////                                }
//                if (getActivity() == null) {
//                    return;
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isHidden) {
//
//                            String w30stag = (String) SharedPreferencesUtils.getParam(context, "w30stag", "10000");
//                            GOAL = Integer.valueOf(w30stag);
//                            double v = (double) STEP / (double) GOAL;
//                            String s = String.valueOf(v * 100).split("[.]")[0] + "%";
//                            if (textStepReach != null) textStepReach.setText(s);
//                            String data = objects.getData();
//                            if (!TextUtils.isEmpty(data)) {
//                                if (watchRecordtopDateTv != null)
//                                    watchRecordtopDateTv.setText(data);
//                                if (L38iCalT != null)
//                                    L38iCalT.setText("" + CALORIES + "");
//                                boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(getContext(), "w30sunit", true);
//                                if (w30sunit) {
//                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_mi);// 获取res下的图片drawable
//                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
//                                    if (L38iDisT != null)
//                                        L38iDisT.setCompoundDrawables(null, top, null, null);
//                                    //L38iDisT.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
//
//                                    if (L38iDisT != null)
//                                        L38iDisT.setText("" + DISTANCE + "");
//                                } else {
//                                    //image_w30s_unti_ft
//                                    float distances = objects.getDistance();
////                                                int round = (int) Math.round();
//                                    int round = (int) Math.floor(distances * 3.28);
////                                                Log.d("------round------", round + "");
//                                    Drawable top = getActivity().getResources().getDrawable(R.mipmap.image_w30s_unti_ft);// 获取res下的图片drawable
//                                    top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
//                                    if (L38iDisT != null)
//                                        L38iDisT.setCompoundDrawables(null, top, null, null);
//                                    //L38iDisT.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
//
//                                    if (L38iDisT != null)
//                                        L38iDisT.setText(String.valueOf(round).split("[.]")[0]);
//                                }
//
//                                if (recordwaveProgressBar != null) {
//                                    recordwaveProgressBar.postInvalidate();
//                                    recordwaveProgressBar.setValue(STEP);
//                                    recordwaveProgressBar.postInvalidate();
//                                }
//                            }
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                e.getMessage();
//            }

        }


        @Override
        public void CallDatasBackListenter(W30SSleepData sleepDatas) {
            if (sleepDatas == null || WatchUtils.isEmpty(sleepDatas.toString())) return;
            //Log.d(TAG, "解析睡眠数据 = 日期 = " + sleepDatas.getSleepData());
            //Log.d(TAG, "解析睡眠数据 = 数据 = " + sleepDatas.getSleepDataList().toString());
            Log.e(TAG,"----------W30SSleepData="+sleepDatas.toString());

            Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
            String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
            final String sleepData = sleepDatas.getSleepData();
            SHALLOW = 0;
            DEEP = 0;
            AWAKE = 0;
            AOYE = 0;
            ALLTIME = 0;
            AWAKE2 = 0;
            boolean isSharpe = true;
            if (sleepData.equals(nextDay)) {
                isSharpe = true;
            } else {
                isSharpe = false;
            }
            List<W30S_SleepDataItem> sleepDataLists = sleepDatas.getSleepDataList();

            if (sleepDataLists == null || sleepDataLists.isEmpty()) return;
            boolean firstALLTIME = false;
            for (int i = 0; i < sleepDataLists.size(); i++) {
                String startTime = null;
                String startTimeLater = null;
                String sleep_type = null;
                if (i >= (sleepDataLists.size() - 1)) {
                    startTime = sleepDataLists.get(i).getStartTime();
                    startTimeLater = sleepDataLists.get(i).getStartTime();
                    sleep_type = sleepDataLists.get(i).getSleep_type();
                } else {
                    startTime = sleepDataLists.get(i).getStartTime();
                    startTimeLater = sleepDataLists.get(i + 1).getStartTime();
                    sleep_type = sleepDataLists.get(i).getSleep_type();
                }
                String[] starSplit = startTime.split("[:]");
                String[] endSplit = startTimeLater.split("[:]");

                int startHour = Integer.valueOf(!WatchUtils.isEmpty(starSplit[0].replace(",", "")) ? starSplit[0].replace(",", "") : "0");
                int endHour = Integer.valueOf(!WatchUtils.isEmpty(endSplit[0].replace(",", "")) ? endSplit[0].replace(",", "") : "0");

                int startMin = Integer.valueOf(!WatchUtils.isEmpty(starSplit[1].replace(",", "")) ? starSplit[1].replace(",", "") : "0");
                int endMin = (Integer.valueOf(!WatchUtils.isEmpty(endSplit[1].replace(",", "")) ? endSplit[1].replace(",", "") : "0"));
                if (startHour > endHour) {
                    endHour = endHour + 24;
                }
                int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
                    AWAKE2 += all_m;
                    if (!firstALLTIME) {
                        firstALLTIME = true;
                    } else {
                        ALLTIME += all_m;
                    }

                } else if (sleep_type.equals("4")) {
                    AWAKE2 += all_m;
                    ALLTIME += all_m;
                    AWAKE++;
                } else if (sleep_type.equals("2")) {
                    //潜水
                    SHALLOW += all_m;
                    ALLTIME += all_m;
                } else if (sleep_type.equals("3")) {
                    //深水
                    DEEP += all_m;
                    ALLTIME += all_m;
                }
            }


            if (getActivity() != null) {

                //官方建议我们写成：（这样的话，可以由系统自己负责message的创建和销毁）
                Message message = mHandler.obtainMessage();
//                            Message message = new Message();
                message.obj = sleepDataLists;
                message.what = 1001;
                mHandler.sendMessage(message);
            }


            String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
            if (!TextUtils.isEmpty(homeTime)) {
                String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
                if (!WatchUtils.isEmpty(timeDifference)) {
                    int number = Integer.valueOf(timeDifference.trim());
                    int number2 = Integer.parseInt(timeDifference.trim());
                    if (Math.abs(number) >= 3600 || Math.abs(number2) >= 3600) {
                        String starTime = sleepDataLists.get(0).getStartTime();
                        String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
                        Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
                        UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
                        if (isSharpe) {
                            SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
                        }
                    }
                }
            } else {
                String starTime = sleepDataLists.get(0).getStartTime();
                String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
                //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
                UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
                if (isSharpe) {
                    SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
                }

            }







            /**
             * 发送消息上传数据详细
             */
            Message message = mHandler.obtainMessage();
            message.obj = sleepDatas;
            message.what = 0x88;
            mHandler.sendMessage(message);


            //解析睡眠数据 = 数据 = [W30S_SleepData{sleep_type='0', startTime='23:00'}, W30S_SleepData{sleep_type='1', startTime='23:53'}, W30S_SleepData{sleep_type='2', startTime='00:13'}, W30S_SleepData{sleep_type='4', startTime='00:16'}, W30S_SleepData{sleep_type='2', startTime='00:18'}, W30S_SleepData{sleep_type='3', startTime='00:39'}, W30S_SleepData{sleep_type='2', startTime='00:58'}, W30S_SleepData{sleep_type='3', startTime='01:08'}, W30S_SleepData{sleep_type='2', startTime='01:23'}, W30S_SleepData{sleep_type='3', startTime='01:34'}, W30S_SleepData{sleep_type='2', startTime='01:54'}, W30S_SleepData{sleep_type='3', startTime='02:23'}, W30S_SleepData{sleep_type='2', startTime='02:50'}, W30S_SleepData{sleep_type='3', startTime='03:00'}, W30S_SleepData{sleep_type='2', startTime='03:24'}, W30S_SleepData{sleep_type='3', startTime='04:13'}, W30S_SleepData{sleep_type='2', startTime='04:31'}, W30S_SleepData{sleep_type='5', startTime='07:56'}]

//            try {
//                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
//                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                String sleepData = sleepDatas.getSleepData();
//                SHALLOW = 0;
//                DEEP = 0;
//                AWAKE = 0;
//                AOYE = 0;
//                ALLTIME = 0;
//                AWAKE2 = 0;
//                boolean isSharpe = true;
//                if (sleepData.equals(nextDay)) {
//                    //Log.d("====解析睡眠数据====", sleepDatas.getSleepDataList().toString());
//                    isSharpe = true;
//                } else {
//                    isSharpe = false;
//                }
//                final List<W30S_SleepDataItem> sleepDataList = sleepDatas.getSleepDataList();
//
//                for (int i = 0; i < sleepDataList.size(); i++) {
//                    String startTime = null;
//                    String startTimeLater = null;
//                    String sleep_type = null;
//                    if (i >= (sleepDataList.size() - 1)) {
//                        startTime = sleepDataList.get(i).getStartTime();
//                        startTimeLater = sleepDataList.get(i).getStartTime();
//                        sleep_type = sleepDataList.get(i).getSleep_type();
//                    } else {
//                        startTime = sleepDataList.get(i).getStartTime();
//                        startTimeLater = sleepDataList.get(i + 1).getStartTime();
//                        sleep_type = sleepDataList.get(i).getSleep_type();
//                    }
////                String timeExpend = W30BasicUtils.getTimeExpend(yearTime + " " + startTime, yearTime + " " + startTimeLater);
//                    String[] starSplit = startTime.split("[:]");
//                    String[] endSplit = startTimeLater.split("[:]");
//
//                    int startHour = Integer.valueOf(starSplit[0]);
//                    int endHour = Integer.valueOf(endSplit[0]);
//
//                    int startMin = Integer.valueOf(starSplit[1]);
//                    int endMin = (Integer.valueOf(endSplit[1]));
//                    //Log.d("----------------", "开始时：" + startHour + "结束时：" + endHour + "开始分：" + startMin + "结束分：" + endMin);
//                    //String timeExpend = "";
//                    //Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
//                    //String yearTime = W30BasicUtils.dateToString(dateBefore, "yyyy-MM-dd");
//                    //Date dateBeforeNew = H9TimeUtil.getDateBefore(new Date(), 0);
//                    //String yearTimeNew = W30BasicUtils.dateToString(dateBeforeNew, "yyyy-MM-dd");
//                    if (startHour > endHour) {
//                        endHour = endHour + 24;
//                    }
//                    //timeExpend = W30BasicUtils.getTimeExpend(yearTimeNew + " " + startTime, yearTimeNew + " " + startTimeLater);
//                    //String[] split = timeExpend.split("[:]");
//                    //double all_m = Math.abs(Integer.valueOf(split[0])) * 60 + Math.abs(Integer.valueOf(split[1]));
//                    int all_m = (endHour - startHour) * 60 + (endMin - startMin);
//                    //Log.e("----------timeExpend--------", all_m + "-----type---" + sleep_type);
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
//                String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
//                if (!TextUtils.isEmpty(homeTime)) {
//                    String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                    if (!WatchUtils.isEmpty(timeDifference)) {
//                        int number = Integer.valueOf(timeDifference.trim());
//                        int number2 = Integer.parseInt(timeDifference.trim());
//                        if (Math.abs(number) >= 7200 || Math.abs(number2) >= 7200) {
//                            UpDatasBase.upDataSleep(DEEP + "", SHALLOW + "", sleepData);//上传睡眠数据
//                        }
//                    }
//                } else {
//                    UpDatasBase.upDataSleep(DEEP + "", SHALLOW + "", sleepData);//上传睡眠数据
//                }
//                if (isSharpe) {
//                    SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
//                }
//
//                if (getActivity() == null) {
//                    Log.e(TAG, "------睡眠--null---");
//                    return;
//                }
//
//                Message message = new Message();
//                message.obj = sleepDataList;
//                message.what = 1001;
//                mHandler.sendMessage(message);
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        Log.e(TAG,"----1nul-------睡眠-----");
////                        Log.e(TAG, DEEP + "===" + SHALLOW);
////  //                      Log.d("--------->>>==", "浅睡：" + SHALLOW + "===" + "深睡：" + DEEP + "===" + "清醒：" + (AWAKE + AOYE) +
//////                                            "入睡时间：" + finalSleepInto + "苏醒次数：" + finalSleepWakeCount + "苏醒时间：" + finalSleepWakeTime + "===所有时间：" + ALLTIME);
////
////                    }
////                });
//            } catch (Exception e) {
//                e.getMessage();
//            }


        }

        @Override
        public void CallDatasBackListenter(final W30SDeviceData objects) {
            if (objects == null) return;
//                        Log.d(TAG, "解析设备信息 = 设备电量 = " + objects.getDevicePower());
//                        Log.d(TAG, "解析设备信息 = 设备类型 = " + objects.getDeviceType());
//                        Log.d(TAG, "解析设备信息 = 设备版本 = " + objects.getDeviceVersionNumber());
            SharedPreferencesUtils.setParam(context, "W30S_V", String.valueOf(objects.getDeviceVersionNumber()));
            SharedPreferencesUtils.setParam(context, "W30S_P", String.valueOf(objects.getDeviceVersionNumber()));
            if (getActivity() == null) {
                return;
            }
            if (isHidden)
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setBatteryPowerShow(objects.getDevicePower());   //显示电量
                        }
                    });

        }


        @Override
        public void CallDatasBackListenter(W30SHeartData objects) {
            if (objects == null) return;
            Log.e(TAG, "解析运动心率 日期 =  " + objects.getDate());
            Log.e(TAG, "解析运动心率 数据 =  " + objects.getWo_heart_data().toString());

            if (heartList != null) heartList.clear();
            isHaertNull = false;
            final List wo_heart_data = objects.getWo_heart_data();
            if (wo_heart_data != null || wo_heart_data.size() > 0) {
//                if (heartDatas != null) {
//                    heartDatas.clear();
//                }
                if (heartDatasMaxOrLad != null) {
                    heartDatasMaxOrLad.clear();
                }
                //循环添加---求最高最低和平均值
                for (int i = 0; i < wo_heart_data.size(); i++) {
                    if (!WatchUtils.isEmpty(wo_heart_data.get(i).toString().trim()) && Integer.valueOf(wo_heart_data.get(i).toString().trim()) > 0) {
                        //Log.d("===================添加=", wo_heart_data.get(i).toString().trim());
                        heartDatasMaxOrLad.add(Integer.valueOf(wo_heart_data.get(i).toString().trim()));
                    }
                }

                List<Integer> mouch = new ArrayList<>();
                for (int i = 0; i < wo_heart_data.size(); i += 2) {
                    mouch.add((int) wo_heart_data.get(i));
                }
                if (!mouch.isEmpty()) {
                    if (upHeartBeanList == null) {
                        upHeartBeanList = new ArrayList<>();
                    } else {
                        upHeartBeanList.clear();
                    }
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
                        String trim = objects.getDate().trim();
                        final String tim = trim + " " + upDataTime;
                        //Log.d("--------AAA-时间-", tim + "=====" + upDataTime);
                        //Log.d("--------AAA-心率数据-", datas);
                        if (heartList == null) heartList = new ArrayList<>();
                        heartList.add(Integer.valueOf(datas));

                        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
                        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
//                        upHeartBeanList.add((i - 1), new UpHeartBean(userId, mac, "00", "00", tim, datas, "0", "00", "00"));


                        UpHeartBean upHeartBean = new UpHeartBean(userId, mac,
                                Integer.valueOf(datas), 0,
                                0, 0,
                                0, tim,
                                0);
                        upHeartBeanList.add((i - 1), upHeartBean);
                    }


                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAdded()) {
                                    NowVale = 0;
                                    NowValeSize = 0;
                                    if (heartList != null && heartList.size() > 0) {

                                        if (getActivity() != null && !getActivity().isFinishing()) {
                                            if (lineChart != null) {
                                                lineChart.setRateDataList(heartList);
                                                if (seekBarHared != null)
                                                    seekBarHared.setEnabled(true);
                                            }
                                            for (int i = 0; i < heartList.size(); i++) {
                                                if (heartList.get(i) > 0) {
                                                    NowVale += heartList.get(i);
                                                    NowValeSize++;
                                                }
                                            }
                                            if (maxHeartTextNumber != null && heartDatasMaxOrLad.size() > 0) {
                                                int max = Collections.max(heartDatasMaxOrLad);
                                                heartMaxValue = max;    //心率最大值
                                                maxHeartTextNumber.setText(max + "bpm");
                                            }
                                            /**
                                             * 这里有毒，平均值和最低值出来位置啥都是相反的---------现在正常了，说不定有有毒了
                                             */
                                            if (zuidiHeartTextNumber != null && heartDatasMaxOrLad.size() > 0) {
                                                int min = Collections.min(heartDatasMaxOrLad);
                                                heartMineValue = min;   //心率最小值
                                                zuidiHeartTextNumber.setText(min + "bpm");
                                            }

                                            if (NowVale > 0 && heartList.size() > 1) {
                                                double div3 = WatchUtils.div((double) NowVale, NowValeSize, 1);
                                                if (autoHeartTextNumber != null) {
                                                    if (String.valueOf(div3).contains(".")) {
                                                        String s = String.valueOf(div3).split("[.]")[0];
                                                        autoHeartTextNumber.setText(s + "bpm");
                                                        avgHeartValue = Integer.valueOf(s); //平均心率值
                                                    } else {
                                                        autoHeartTextNumber.setText(div3 + "bpm");
                                                        avgHeartValue = (int) div3; //平均心率值
                                                    }
                                                }
                                            }



                                            //保存心率
                                            CommDBManager.getCommDBManager().saveCommHeartData(WatchUtils.getSherpBleName(MyApp.getContext()),WatchUtils.getSherpBleMac(MyApp.getContext()),
                                                    WatchUtils.getCurrentDate(),heartMaxValue,heartMineValue,avgHeartValue);

                                        }
                                    }

                                }
                            }
                        });
                    }

                    /**
                     * 上传数据
                     */
                    Message message = mHandler.obtainMessage();
                    message.obj = upHeartBeanList;
                    message.what = 0x77;
                    mHandler.sendMessage(message);

                }
            }


        }

        @Override
        public void CallDatasBackListenterIsok() {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //closeLoadingDialog();
                    if (isHaertNull) {
                        mHandler.sendEmptyMessage(0x05);
                        return;
                    }
                    mHandler.sendEmptyMessage(0x01);
//                    stateHandler.sendEmptyMessageDelayed(222222, 6000);
                }
            });
        }
    }

}