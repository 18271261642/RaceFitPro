package com.example.bozhilun.android.h9.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.DaoSession;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.LogTestUtil;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.h9.db.H9DBCommont;
import com.example.bozhilun.android.h9.db.H9HeartDBModel;
import com.example.bozhilun.android.h9.db.H9SleepDBModel;
import com.example.bozhilun.android.h9.db.H9StepDBModel;
import com.example.bozhilun.android.h9.settingactivity.CorrectionTimeActivity;
import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.h9.utils.Device_Time_Activity;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.h9.utils.VerticalSwipeRefreshLayout;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.w30s.bean.UpHeartBean;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.w30s.views.W30CusHeartView;
import com.example.bozhilun.android.w30s.views.W30S_SleepChart;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.sdk.bluetooth.bean.HeartData;
import com.sdk.bluetooth.bean.SleepData;
import com.sdk.bluetooth.bean.SportsData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.clear.ClearBloodData;
import com.sdk.bluetooth.protocol.command.clear.ClearHeartData;
import com.sdk.bluetooth.protocol.command.clear.ClearSleepData;
import com.sdk.bluetooth.protocol.command.clear.ClearSportData;
import com.sdk.bluetooth.protocol.command.count.AllDataCount;
import com.sdk.bluetooth.protocol.command.data.DeviceDisplaySportSleep;
import com.sdk.bluetooth.protocol.command.data.GetHeartData;
import com.sdk.bluetooth.protocol.command.data.GetSleepData;
import com.sdk.bluetooth.protocol.command.data.GetSportData;
import com.sdk.bluetooth.protocol.command.device.BatteryPower;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/27 16:29
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9NewRecordFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener, W30CusHeartView.DataTypeListenter, SeekBar.OnSeekBarChangeListener, W30S_SleepChart.DataTypeListenter {
    private final String TAG = "H9NewRecordFragment";
    Unbinder unbinder;
    private H9DBCommont h9DBCommont = null;
    private DaoSession daoSession = null;
    //心率，步数上传序言转换的对象
    List<UpHeartBean> upHeartBeanList = null;
    //心率图标数据
    List<Integer> heartList;
    private String userId = "";
    private String mac = "";

    //显示手表图标左上角
    @BindView(R.id.batteryshouhuanImg)
    ImageView shouhuanImg;
    //显示连接状态的TextView
    @BindView(R.id.battery_watch_connectStateTv)
    TextView watchConnectStateTv;
    //刷新控件
    @BindView(R.id.swipeRefresh)
    VerticalSwipeRefreshLayout swipeRefresh;
    //刷新提示
    @BindView(R.id.text_stute)
    TextView textStute;
    //显示日期的TextView
    @BindView(R.id.battery_watch_recordtop_dateTv)
    TextView watchRecordtopDateTv;
    //电量图标和点量值的容器
    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;
    //显示电量的图片
    @BindView(R.id.batteryTopView)
    ImageView watchTopBatteryImgView;
    //显示电量值
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.scroll_home)
    ScrollView scroll_home;
    //心率图标
    @BindView(R.id.heart_chart)
    W30CusHeartView heartChart;
    //睡眠图标
    @BindView(R.id.sleep_chart)
    W30S_SleepChart w30S_sleepChart;
    //睡眠无数据小提示
    @BindView(R.id.text_sleep_nodata)
    TextView text_sleep_nodata;
    //深睡眠
    @BindView(R.id.deep_sleep)
    TextView deepSleep;
    //浅睡眠
    @BindView(R.id.shallow_sleep)
    TextView shallowSleep;
    //清醒睡眠
    @BindView(R.id.awake_sleep)
    TextView awakeSleep;
    //入睡时间
    @BindView(R.id.sleep_into_time)
    TextView sleepIntoTime;
    //起床时间
    @BindView(R.id.sleep_out_time)
    TextView sleepOutTime;


    //步数圆环
    @BindView(R.id.recordwave_progress_bar)
    WaveProgress recordwaveProgressBar;

    //卡路里
    @BindView(R.id.watch_recordKcalTv)
    TextView L38iCalT;
    //距离值
    @BindView(R.id.watch_recordMileTv)
    TextView L38iDisT;
    //步数达标值
    @BindView(R.id.text_step_reach)
    TextView textStepReach;
    //控制心率图华东
    @BindView(R.id.seek_bar_my_heart)
    SeekBar seekBarHeart;
    //心率滑动时值显示
    @BindView(R.id.frm_heard)
    RelativeLayout frm_heard;
    //显示的心率值
    @BindView(R.id.heard_value)
    TextView heardValue;
    //显示的心率时间
    @BindView(R.id.heart_times)
    TextView heartTimes;
    //最高心率
    @BindView(R.id.maxHeart_text_number)
    TextView maxHeart;
    //平均心率
    @BindView(R.id.autoHeart_text_number)
    TextView autoHeart;
    //最低心率
    @BindView(R.id.zuidiHeart_text_number)
    TextView minHeart;


    @BindView(R.id.seek_bar_my_sleep)
    SeekBar seekBarSleep;
    //睡眠类型  深睡  浅睡  清醒
    @BindView(R.id.text_sleep_type)
    TextView textSleepType;
    //开始时间
    @BindView(R.id.text_sleep_start)
    TextView textSleepStart;
    //开始到结束的线
    @BindView(R.id.text_sleep_lines)
    TextView textSleepLines;
    //结束时间
    @BindView(R.id.text_sleep_end)
    TextView textSleepEnd;
    //公英制
    private int h9_step_util = 0;
    boolean isIntoSleep = false;//判断是否进入过睡眠


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h9_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        initContextAndDB();

        initChartViews();

        userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
        mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        synchronization(false);
        return view;
    }

    /**
     * 步数图，心率图，睡眠图 基本设置
     */
    private void initChartViews() {
        if (getActivity() == null || getActivity().isFinishing()) return;
        if (L38iDisT != null) {
            h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
            // 0位公制 1为英制
            if (h9_step_util == 0) {
                L38iDisT.setText("0.0km");
            } else {
                L38iDisT.setText("0.0mi");
            }
        }
        if (L38iCalT != null) L38iCalT.setText("0.0cal");
        if (textStepReach != null) textStepReach.setText("0.00");
        if (recordwaveProgressBar != null) {
            //步数
            String w30stag = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "w30stag", "10000");
//            int h9_step_gaol = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_GAOL", 10000);
            recordwaveProgressBar.setMaxValue(Integer.valueOf(w30stag.trim().replace(" ", "")));
            recordwaveProgressBar.setValue(0);
            recordwaveProgressBar.setTagStepStr(getResources().getString(R.string.settarget_steps) + w30stag);
            recordwaveProgressBar.invalidate();
        }


        //心率
        if (seekBarHeart != null && heartChart != null) {
            seekBarHeart.setEnabled(false);
            seekBarHeart.setMax(Math.abs(51));
            heartChart.setmDataTypeListenter(this);
            seekBarHeart.setOnSeekBarChangeListener(this);
            heartChart.setType("H9");//默认是W30那种 48个心率值，此处做了处理，H9只有24点，所以设置类型
        }


        if (textSleepLines != null && seekBarSleep != null && w30S_sleepChart != null) {
            //睡眠
            textSleepLines.setText("");
            seekBarSleep.setEnabled(false);
            seekBarSleep.setMax(Math.abs(500));
            w30S_sleepChart.setmDataTypeListenter(this);
            seekBarSleep.setOnSeekBarChangeListener(this);
        }


        watchRecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getActivity(), Device_Time_Activity.class).putExtra("is18i", "H9"));
                return false;
            }
        });
    }


    /**
     * 同步数据
     *
     * @param isMust 是否是手动更新，是的话不需要判断时间直接更新
     */
    private void synchronization(boolean isMust) {
        /**
         * 检查链接状态  获取数据
         */
        if (firstInspect()) {

            if (isMust) {
                getDevicesData();
            } else {
                String currentDate = WatchUtils.getCurrentDate1();
                String get_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "GET_DEVICES_SYS", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                int sysTimes = H9TimeUtil.getTimeDifferenceMu(get_time, currentDate);
                if (sysTimes > 5) {
                    getDevicesData();
                } else {
                    // --------  ① 设置电池电量（不需要每次都同步电电量）
                    int h9_battery = (int) SharedPreferencesUtils.readObject(MyApp.getInstance(), "H9_BATTERY");
                    setBatteryPowerShow(h9_battery);
                    if (mHandler != null) mHandler.sendEmptyMessage(0x05);
                }
            }

        }

    }


    /**
     * 初始化
     */
    private void initContextAndDB() {
        //链接状态广播监听
        regeditReceiver();
        if (swipeRefresh != null && scroll_home != null) {
            swipeRefresh.setScrollUpChild(scroll_home);
            //手动刷新
            swipeRefresh.setOnRefreshListener(this);
        }

        //数据库
        h9DBCommont = H9DBCommont.getInstance();
        daoSession = H9DBCommont.getDaoSession();
        //左上角设备图标
        if (shouhuanImg != null) shouhuanImg.setImageResource(R.mipmap.h9);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
        if (commandResultBroadReceiver != null)
            getActivity().unregisterReceiver(commandResultBroadReceiver);
    }


    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.line_heart_datas, R.id.battery_watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.line_heart_datas:    //点击是否连接
                if (MyCommandManager.DEVICENAME != null) {    //已连接-----去心率测试
                    startActivity(new Intent(getActivity(),
                            H9HearteTestActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "H9"));
                } else {
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                Intent intent = new Intent(getActivity(), SharePosterActivity.class);
                intent.putExtra("is18i", "H9");
                int deviceDisplaySteps = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_NUMBER", 0);
                intent.putExtra("stepNum", deviceDisplaySteps + "");
                startActivity(intent);
                break;
        }
    }

    /**
     * 注册链接监听广播
     */
    private void regeditReceiver() {
        IntentFilter intFilter = new IntentFilter();
        intFilter.addAction("com.example.bozhilun.android.h9.connstate");
        getActivity().registerReceiver(commandResultBroadReceiver, intFilter);
    }

    /**
     * 检查链接状态
     */
    boolean firstInspect() {
        boolean isConnted = false;
        if (getActivity() != null && !getActivity().isFinishing()) {
            //顶部刷新提示显示
            if (textStute != null) textStute.setVisibility(View.VISIBLE);
            //顶部时间设置
            if (watchRecordtopDateTv != null)
                watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
            //判断是否连接
            if (MyCommandManager.DEVICENAME == null) {
                //没链接---改变状态显示
                if (watchConnectStateTv != null)
                    watchConnectStateTv.setText(getResources().getString(R.string.disconnted));
                if (watchConnectStateTv != null)
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                if (watchConnectStateTv != null) AnimationUtils.startFlick(watchConnectStateTv);
                if (batteryLayout != null) batteryLayout.setVisibility(View.GONE);
                isConnted = false;
                if (textStute != null)
                    textStute.setText(getResources().getString(R.string.disconnted));


                H9HomeActivity.ConntentDevices();
            } else {
                //已经连接
                if (watchConnectStateTv != null)
                    watchConnectStateTv.setText(getResources().getString(R.string.connted));//链接状态设置
                if (watchConnectStateTv != null)
                    watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                if (watchConnectStateTv != null) AnimationUtils.stopFlick(watchConnectStateTv);
                if (batteryLayout != null) batteryLayout.setVisibility(View.VISIBLE);
                isConnted = true;
                if (textStute != null)
                    textStute.setText(getResources().getString(R.string.syncy_data));
            }
        }
        return isConnted;
    }


    /**
     * 获取设备数据
     */
    void getDevicesData() {
        //2018-12-25 12:20
        String currentDate = WatchUtils.getCurrentDate1();
        String get_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "GET_TIME", "2018-12-25 12:20");//获取设备电池电量间隔时间   每十分钟后可以获取
        int timeDifferenceMu = H9TimeUtil.getTimeDifferenceMu(get_time, currentDate);
        Log.d(TAG, "--------时间差--分钟" + timeDifferenceMu + "==" + (timeDifferenceMu > 10 ? "大于十分钟同步是获取电量" : "小于十分钟同步是不获取电量"));
        if (timeDifferenceMu > 10) {
            // --------  ① 获取电池电量（正常顺序）
            SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_TIME", currentDate);
            AppsBluetoothManager.getInstance(MyApp.getInstance())
                    .sendCommand(new BatteryPower(commandResultCallbackAll));
        } else {
            // --------  ① 设置电池电量（不需要每次都同步电电量）
            int h9_battery = (int) SharedPreferencesUtils.readObject(MyApp.getInstance(), "H9_BATTERY");
            setBatteryPowerShow(h9_battery);
            // --------  ① 获取所有数据条数
            AppsBluetoothManager.getInstance(MyApp.getInstance())
                    .sendCommand(new AllDataCount(commandResultCallbackAll));
        }
    }

    /**
     * 手动刷新回掉
     */
    @Override
    public void onRefresh() {
        synchronization(true);//同步数据
        if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
    }


    private int sportCount = 0;//步数长度
    private int sleepCount = 0;//睡眠长度
    private int heartRateCount = 0;//心率长度

    /**
     * 设备命令回掉
     */
    BaseCommand.CommandResultCallback commandResultCallbackAll = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof BatteryPower) {//电池电量获取成功
                int batteryPower = GlobalVarManager.getInstance().getBatteryPower();
                SharedPreferencesUtils.saveObject(MyApp.getInstance(), "H9_BATTERY", batteryPower);
                // //显示电量
                setBatteryPowerShow(batteryPower);

                // --------  ②获取所有数据条数
                AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new AllDataCount(commandResultCallbackAll));
            } else if (baseCommand instanceof AllDataCount) {//所有的运动条数获取成功
                //步数数据长度
                sportCount = (int) GlobalVarManager.getInstance().getSportCount();
                //睡眠数据长度
                sleepCount = (int) GlobalVarManager.getInstance().getSleepCount();
                //心率数据长度
                heartRateCount = (int) GlobalVarManager.getInstance().getHeartRateCount();

                if (sportCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetSportData(commandResultCallbackAll, sportCount));
                    return;
                } else if (sleepCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, sleepCount));
                    return;
                } else if (heartRateCount > 0) {

                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, heartRateCount));
                    return;
                } else {
                    //运动条数都为0  就获取当天运动汇总
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof GetSportData) {//运动详细
                LinkedList<SportsData> sportsDatas = GlobalDataManager.getInstance().getSportsDatas();
                if (sportsDatas != null)
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage(0x01);
                        message.obj = sportsDatas;
                        mHandler.sendMessage(message);
                    }


                if (sleepCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, sleepCount));
                    return;
                } else if (heartRateCount > 0) {

                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, heartRateCount));
                    return;
                } else {
                    //运动条数都为0  就获取当天运动汇总
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof GetSleepData) {//睡眠详细
                LinkedList<SleepData> sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
                if (sleepDatas != null)
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage(0x02);
                        message.obj = sleepDatas;
                        mHandler.sendMessage(message);
                    }

                if (heartRateCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, heartRateCount));
                    return;
                } else {
                    //运动条数都为0  就获取当天运动汇总
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof GetHeartData) {//心率详细
                LinkedList<HeartData> heartDatas = GlobalDataManager.getInstance().getHeartDatas();
                if (heartDatas != null)
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage(0x03);
                        message.obj = heartDatas;
                        mHandler.sendMessage(message);
                    }

                //运动条数都为0  就获取当天运动汇总
                AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
            } else if (baseCommand instanceof DeviceDisplaySportSleep) {//当天运动汇总

                Log.e(TAG, "当天运动汇总-----Step:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
                        "\n Calorie:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
                        "\n Distance:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
                        "\n Sleep time:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
                if (mHandler != null) {
                    Message message = mHandler.obtainMessage(0x04);
                    mHandler.sendMessage(message);
                }
            } else if (baseCommand instanceof ClearSportData
                    || baseCommand instanceof ClearHeartData
                    || baseCommand instanceof ClearBloodData
                    || baseCommand instanceof ClearSleepData) {
                //删除运动数据
                Log.d(TAG, "-----------清楚数据成功");


                String currentDate = WatchUtils.getCurrentDate1();

                SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_DEVICES_SYS", currentDate);//获取设备数据  每3分钟后可以获取

                String devices_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "DEVICES_TIME", "2018-12-25 12:20");//获取设备时间间隔时间   每两个小时后可以获取
                /**
                 * 计算相差时间---分钟
                 */

                int devicesTimeDifferenceMu = H9TimeUtil.getTimeDifferenceMu(devices_time, currentDate);
                Log.d(TAG, "--------时间差--分钟" + devicesTimeDifferenceMu + "==" + (devicesTimeDifferenceMu > 120 ? "大于120分钟同步是获取设备时间" : "小于120分钟同步是不获取设备时间"));
                if (devicesTimeDifferenceMu > 120) {
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "DEVICES_TIME", currentDate);//获取设备时间间隔时间   每两个小时后可以获取
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DateTime(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof DateTime) {
                DevicesTimeIsChange(baseCommand);
            }

        }

        @Override
        public void onFail(BaseCommand baseCommand) {

            if (baseCommand instanceof BatteryPower) {//电池电量获取成功

                // -----电池电量获取失败---  ① 获取所有数据条数
                AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new AllDataCount(commandResultCallbackAll));
            } else if (baseCommand instanceof AllDataCount) {//所有的运动条数获取成功


                //运动条数获取失败  就获取当天运动汇总
                AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
            } else if (baseCommand instanceof GetSportData) {//运动详细


                if (sleepCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetSleepData(commandResultCallbackAll, 0, 0, sleepCount));
                    return;
                } else if (heartRateCount > 0) {

                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, heartRateCount));
                    return;
                } else {
                    //运动条数都为0  就获取当天运动汇总
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof GetSleepData) {//睡眠详细

                if (heartRateCount > 0) {
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new GetHeartData(commandResultCallbackAll, 0, 0, heartRateCount));
                    return;
                } else {
                    //运动条数都为0  就获取当天运动汇总
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
                }
            } else if (baseCommand instanceof GetHeartData) {//心率详细

                //运动条数都为0  就获取当天运动汇总
                AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new DeviceDisplaySportSleep(commandResultCallbackAll));
            } else if (baseCommand instanceof DeviceDisplaySportSleep) {//当天运动汇总
                if (mHandler != null) {
                    Message message = mHandler.obtainMessage(0x05);
                    mHandler.sendMessage(message);
                }

            } else if (baseCommand instanceof ClearSportData
                    || baseCommand instanceof ClearHeartData
                    || baseCommand instanceof ClearBloodData
                    || baseCommand instanceof ClearSleepData) {
                //删除运动数据
                Log.d(TAG, "-----------清楚数据失败");

                String currentDate = WatchUtils.getCurrentDate1();
                String devices_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "DEVICES_TIME", "2018-12-20 12:20");//获取设备时间间隔时间   每两个小时后可以获取
                /**
                 * 计算相差时间---分钟
                 */

                int devicesTimeDifferenceMu = H9TimeUtil.getTimeDifferenceMu(devices_time, currentDate);
                Log.d(TAG, "--------时间差--分钟" + devicesTimeDifferenceMu + "==" + (devicesTimeDifferenceMu > 120 ? "大于120分钟同步是获取设备时间" : "小于120分钟同步是不获取设备时间"));
                if (devicesTimeDifferenceMu > 120) {
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "DEVICES_TIME", currentDate);
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new DateTime(commandResultCallbackAll));
                }
            }

        }
    };


    boolean isTimeChage = false;

    /**
     * 检查设备时间是否正确
     *
     * @param baseCommand
     */
    private void DevicesTimeIsChange(BaseCommand baseCommand) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (textStute != null) textStute.setVisibility(View.INVISIBLE);
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                Log.d(TAG, "-----读取设备时间成功:" + GlobalVarManager.getInstance().getDeviceDateTime());
                Log.d(TAG, "-----查看手机本地时间:" + B18iUtils.getSystemDataStart());
                String deviceDateTime = GlobalVarManager.getInstance().getDeviceDateTime();
                String systemDataStart = B18iUtils.getSystemDataStart();
                if (!WatchUtils.isEmpty(deviceDateTime) && !WatchUtils.isEmpty(systemDataStart)) {
                    String[] splitDevices = deviceDateTime.split("[ ]");
                    String[] splitSystem = systemDataStart.split("[ ]");
                    if (!WatchUtils.isEmpty(splitDevices[0]) && !WatchUtils.isEmpty(splitDevices[1])
                            && !WatchUtils.isEmpty(splitSystem[0]) && !WatchUtils.isEmpty(splitSystem[1])) {

                        Log.e(TAG, "-------天 d:" + splitDevices[0] + "  s:" + splitSystem[0]);
                        //获取设备的时间为    2017-1-2    获取本地的为    2017-01-20   所以去掉里面包含的0在判断
                        if (!splitDevices[0].replace("0", "").equals(splitSystem[0].replace("0", ""))) {

                            if (!isTimeChage) {
                                isTimeChage = true;
                                //ToastUtil.showShort(getActivity(), "时间不对，相差大于一天的错误,需要校正");
                                new CommomDialog(getActivity(), R.style.dialog,
                                        getResources().getString(R.string.string_alarm_time_difference),
                                        new CommomDialog.OnCloseListener() {
                                            @Override
                                            public void onClick(Dialog dialog, boolean confirm) {
                                                if (confirm) {
                                                    startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
                                                }
                                                isTimeChage = false;
                                                dialog.dismiss();
                                            }
                                        }).setTitle(getResources().getString(R.string.prompt)).show();
                            }
                        } else {
                            String hmsDevices = splitDevices[1];
                            String hmsSystem = splitSystem[1];
                            String[] splitHmsDevices = hmsDevices.split("[:]");
                            String[] splitHmsSystem = hmsSystem.split("[:]");
                            if (!WatchUtils.isEmpty(splitHmsDevices[0]) && !WatchUtils.isEmpty(splitHmsDevices[1])
                                    && !WatchUtils.isEmpty(splitHmsSystem[0]) && !WatchUtils.isEmpty(splitHmsSystem[1])) {
                                int devHour = Integer.valueOf(splitHmsDevices[0]);
                                int sysHour = Integer.valueOf(splitHmsSystem[0]);

                                int devMou = Integer.valueOf(splitHmsDevices[1]);
                                int sysMou = Integer.valueOf(splitHmsSystem[1]);

                                Log.e(TAG, "-------时 d:" + devHour + "  s:" + sysHour);
                                Log.e(TAG, "-------分 d:" + devMou + "  s:" + sysMou);
                                if (devHour - sysHour != 0) {

                                    if (!isTimeChage) {
                                        isTimeChage = true;
                                        //ToastUtil.showShort(getActivity(), "时间不对，相差大于一小时的错误,需要校正");
                                        new CommomDialog(getActivity(), R.style.dialog,
                                                getResources().getString(R.string.string_alarm_time_difference_h),
                                                new CommomDialog.OnCloseListener() {
                                                    @Override
                                                    public void onClick(Dialog dialog, boolean confirm) {
                                                        if (confirm) {
                                                            startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
                                                        }
                                                        isTimeChage = false;
                                                        dialog.dismiss();
                                                    }
                                                }).setTitle(getResources().getString(R.string.prompt)).show();
                                    }

                                } else {
                                    if (Math.abs(devMou - sysMou) > 10) {
                                        if (!isTimeChage) {
                                            isTimeChage = true;
                                            //ToastUtil.showShort(getActivity(), "时间不对，相差大于十分钟的错误,需要校正");
                                            new CommomDialog(getActivity(), R.style.dialog,
                                                    getResources().getString(R.string.string_alarm_time_difference_m),
                                                    new CommomDialog.OnCloseListener() {
                                                        @Override
                                                        public void onClick(Dialog dialog, boolean confirm) {
                                                            if (confirm) {
                                                                startActivity(new Intent(getActivity(), CorrectionTimeActivity.class));
                                                            }
                                                            isTimeChage = false;
                                                            dialog.dismiss();
                                                        }
                                                    }).setTitle(getResources().getString(R.string.prompt)).show();
                                        }

                                    }
                                }
                            }

                        }
                    }
                }
            } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                Log.d(TAG, "-----设置设备时间成功:" + GlobalVarManager.getInstance().getDeviceDateTime());
            }
        }

    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01://运动
                    LinkedList<SportsData> sportsDatas = (LinkedList<SportsData>) message.obj;
                    if (!sportsDatas.isEmpty()) {

                        /**
                         * 将每天的保存一个字典
                         */
                        LinkedList<SportsData> daSportList = new LinkedList<>();
                        Map<String, LinkedList<SportsData>> datSportMap = new LinkedHashMap<String, LinkedList<SportsData>>();//保存天list数据的map
                        for (SportsData item : sportsDatas) {
                            String trimDevices = W30BasicUtils.longToString(item.sport_time_stamp * 1000, "yyyy-MM-dd");
                            //Log.e(TAG, "-------trimDevices  时间：" + W30BasicUtils.longToString(item.time_stamp * 1000,
                            //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + datHeartMap.containsKey(trimDevices));
                            if (datSportMap.containsKey(trimDevices)) {
                                daSportList.add(item);
                            } else {
                                daSportList.clear();
                                daSportList.add(item);
                            }
                            datSportMap.put(trimDevices, daSportList);
                        }
                        //今天
//                        String timeToday = W30BasicUtils.longToString(today_sport_time_stamp * 1000, "yyyy-MM-dd");
//                        //昨天
//                        String timeYesterday = WatchUtils.obtainAroundDate(timeToday, true);
//                        //前天
//                        String timeBeforeYesterday = WatchUtils.obtainAroundDate(timeYesterday, true);


                        /**
                         * 遍历天
                         */
                        for (String key : datSportMap.keySet()) {
                            /**
                             * 添加占位
                             */
                            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
                            h9DBCommont.stepDefulData(key);

                            LinkedList<SportsData> sportsData = datSportMap.get(key);
                            //java.util.LinkedHashMap 可以按加入时的顺序遍历。
                            Map<String, LinkedList<SportsData>> hourSportValueMap = new LinkedHashMap<String, LinkedList<SportsData>>();//保存小时list数据的map
                            LinkedList<SportsData> hourHeartList = null;
                            /**
                             * 数据分为小时保存
                             */
                            for (SportsData item : sportsData) {
                                String hourKey = W30BasicUtils.longToString(item.sport_time_stamp * 1000, "yyyy-MM-dd HH:mm").substring(11, 13);
                                if (hourSportValueMap.containsKey(hourKey)) {
                                    if (hourHeartList != null) hourHeartList.add(item);
                                } else {
                                    hourHeartList = new LinkedList<>();//list数据
                                    hourHeartList.add(item);
                                }
                                hourSportValueMap.put(hourKey, hourHeartList);
                            }

                            /**
                             * 去除 以时为 KEY的map
                             */
                            if (!hourSportValueMap.isEmpty()) {
                                /**
                                 * 遍历  小时 map中的键
                                 */
                                for (String hourMapKey : hourSportValueMap.keySet()) {
                                    LinkedList<SportsData> hourSportDataLinkedList = hourSportValueMap.get(hourMapKey);//单个小时的心率数据集合
                                    int sportValues = 0;
                                    if (hourSportDataLinkedList != null && !hourSportDataLinkedList.isEmpty()) {
                                        for (int j = 0; j < hourSportDataLinkedList.size(); j++) {
                                            sportValues += hourSportDataLinkedList.get(j).sport_steps;
                                        }
                                    }
                                    /**
                                     * 数据库更新或者添加
                                     */
                                    if (h9DBCommont == null)
                                        h9DBCommont = H9DBCommont.getInstance();
                                    h9DBCommont.saveStepToDB(sportValues, key + " " + hourMapKey + ":00");
                                }
                            }
                        }

//                        /**
//                         * 读取步数上传
//                         */
//                        readStepUpdata(userId, mac);
                    }
                    break;
                case 0x02://睡眠
                    LinkedList<SleepData> sleepDatas = (LinkedList<SleepData>) message.obj;
                    if (sleepDatas != null && !sleepDatas.isEmpty()) {

                        for (int i = 0; i < sleepDatas.size(); i++) {
                            SleepData sleepData = sleepDatas.get(i);
                            if (sleepData != null) {
                                int sleepType = sleepData.sleep_type;
                                long sleepTime = sleepData.sleep_time_stamp;
                                String stringSleepTime = W30BasicUtils.longToString(sleepTime * 1000, "yyyy-MM-dd HH:mm");

                                H9SleepDBModel newH9SleepDBModel = new H9SleepDBModel();
                                newH9SleepDBModel.setDateTime(stringSleepTime);
                                newH9SleepDBModel.setDevicesCode(mac);
                                newH9SleepDBModel.setUserId(userId);
                                newH9SleepDBModel.setSleepTime(stringSleepTime.substring(11, 16));
                                newH9SleepDBModel.setSleepType(sleepType);
                                newH9SleepDBModel.setRec(stringSleepTime.substring(0, 10));
                                LogTestUtil.e(TAG, "进入数据库：" + stringSleepTime + "=" + sleepType);
                                if (h9DBCommont == null)
                                    h9DBCommont = H9DBCommont.getInstance();
                                h9DBCommont.saveSleepToDB(newH9SleepDBModel);
                            }
                        }
//
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
//
//
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
//                        // sleepData.sleep_type
//                        // 0：睡着
//                        // 1：浅睡
//                        // 2：醒着
//                        // 3：准备入睡
//                        // 4：退出睡眠
//                        // 16：进入睡眠模式
//                        // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                        // 18：退出睡眠模式（本次睡眠为预设睡眠）
//
////                        /**
////                         * 遍历所有天数
////                         */
//
////                        for (String dayKey : datSleepMap.keySet()) {
////                            LogTestUtil.e(TAG, "便利获取的睡眠数据===天：" + dayKey);
////
////                            LinkedList<SleepData> sleepDataList = datSleepMap.get(dayKey);//获取天的数据
////
////                            if (sleepDataList != null && !sleepDataList.isEmpty()) {
////                                for (int i = 0; i < sleepDataList.size(); i++) {
////                                    SleepData sleepData = sleepDataList.get(i);
////                                    if (sleepData != null) {
////                                        int sleepType = sleepData.sleep_type;
////                                        long sleepTime = sleepData.sleep_time_stamp;
////                                        String stringSleepTime = W30BasicUtils.longToString(sleepTime * 1000, "yyyy-MM-dd HH:mm");
////
////                                        H9SleepDBModel newH9SleepDBModel = new H9SleepDBModel();
////                                        newH9SleepDBModel.setDateTime(dayKey+" "+stringSleepTime.substring(11, 16));
////                                        newH9SleepDBModel.setDevicesCode(mac);
////                                        newH9SleepDBModel.setUserId(userId);
////                                        newH9SleepDBModel.setSleepTime(stringSleepTime.substring(11, 16));
////                                        newH9SleepDBModel.setSleepType(sleepType);
////                                        newH9SleepDBModel.setRec(dayKey);
////                                        LogTestUtil.e(TAG, "进入数据库：" + dayKey + "=" + stringSleepTime + "=" + sleepType);
////                                        if (h9DBCommont == null)
////                                            h9DBCommont = H9DBCommont.getInstance();
////                                        h9DBCommont.saveSleepToDB(newH9SleepDBModel);
////                                    }
////
////                                }
////
////
////                            }
////                        }
//
//
////                        for (String todayKey : datSleepMap.keySet()) {
////                            //System.out.println("Key = " + todayKey + "------Value = " + datSleepMap.get(todayKey));
//////                        if ( todayKey.equals(timeToday)) {//今天
////                            LinkedList<SleepData> sleepData = datSleepMap.get(todayKey);//今天
////                            if (sleepData != null && !sleepData.isEmpty()) {
////                                LogTestUtil.e(TAG, "-----H9-onSuccess-  今天睡眠GetSleepData:" + sleepData.toString());
//////                                List<W30S_SleepDataItem> sleepDataLists = new ArrayList<>();
////                                for (int i = 0; i < sleepData.size(); i++) {
////                                    int sleep_type = sleepData.get(i).sleep_type;
////                                    long sleep_time_stamp = sleepData.get(i).sleep_time_stamp;
////
////                                    String longSleepTime = W30BasicUtils.longToString(sleep_time_stamp * 1000, "yyyy-MM-dd HH:mm");
////                                    String sleep_time = W30BasicUtils.longToString(sleep_time_stamp * 1000, "HH:mm");
////                                    String hour_time = W30BasicUtils.longToString(sleep_time_stamp * 1000, "HH");
////                                    Log.d(TAG, "读取的睡眠数据--A-" + todayKey + "===" + longSleepTime + "====" + hour_time);
////                                    if ((todayKey.equals(WatchUtils.getCurrentDate()) && Integer.valueOf(hour_time) <= 10) &&
////                                            todayKey.equals(WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(), true)) && Integer.valueOf(hour_time) >= 20) {
////                                        Log.d(TAG, "保存的睡眠数据--B-" + sleep_time + "===" + sleep_type);
////                                        if (h9DBCommont == null)
////                                            h9DBCommont = H9DBCommont.getInstance();
////                                        h9DBCommont.saveSleepToDB(sleep_type, sleep_time, longSleepTime, todayKey);
////                                    }
////
////                                }
////
////                            }
////
////                        }

                    }
                    break;
                case 0x03://心率
                    LinkedList<HeartData> heartDatas = (LinkedList<HeartData>) message.obj;
                    if (!heartDatas.isEmpty()) {
                        /**
                         * 将每天的保存一个字典
                         */
                        LinkedList<HeartData> daHeartList = new LinkedList<>();
                        Map<String, LinkedList<HeartData>> datHeartMap = new LinkedHashMap<String, LinkedList<HeartData>>();//保存天list数据的map
                        for (HeartData item : heartDatas) {
                            String trimDevices = W30BasicUtils.longToString(item.time_stamp * 1000, "yyyy-MM-dd");
                            //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + datHeartMap.containsKey(trimDevices));
                            if (datHeartMap.containsKey(trimDevices)) {
                                daHeartList.add(item);
                            } else {
                                daHeartList.clear();
                                daHeartList.add(item);
                            }
                            datHeartMap.put(trimDevices, daHeartList);
                        }

                        /**
                         * 遍历天
                         */
                        for (String dayKey : datHeartMap.keySet()) {

                            //每次读取数据后只缓存三天，再多不要
                            MyApp.getInstance().getDBManager().getDaoSession().getH9HeartDBModelDao().deleteAll();
                            /**
                             *每日的心率数据0占位
                             */
                            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
                            h9DBCommont.heartDefulData(dayKey);

                            //Log.d(TAG, "day  要查询的 KEY ：" + timeToday);
                            LinkedList<HeartData> dayHeartListItem = datHeartMap.get(dayKey);//单个天的心率数据
                            if (dayHeartListItem != null && !dayHeartListItem.isEmpty()) {
                                //java.util.LinkedHashMap 可以按加入时的顺序遍历。
                                Map<String, LinkedList<HeartData>> hourHeartValueMap = new LinkedHashMap<String, LinkedList<HeartData>>();//保存小时list数据的map
                                LinkedList<HeartData> hourHeartList = null;
                                //数据分为小时保存
                                for (HeartData item : dayHeartListItem) {
                                    String hourKey = W30BasicUtils.longToString(item.time_stamp * 1000, "yyyy-MM-dd HH:mm").substring(11, 13);
                                    //Log.e(TAG, "-----2--trimDevices  时间：" + W30BasicUtils.longToString(item.time_stamp * 1000,
                                    //        "yyyy-MM-dd HH:mm:ss") + "  -- 心率：" + item.heartRate_value + "=====是否包含在Map==" + hourHeartValueMap.containsKey(hourKey));
                                    if (hourHeartValueMap.containsKey(hourKey)) {
                                        if (hourHeartList != null)
                                            hourHeartList.add(item);
                                    } else {
                                        //if (hourHeartList != null && !hourHeartList.isEmpty())
                                        //    Log.e(TAG, "每时的数据" + hourKey + "===" + hourHeartList.size() + "===" + hourHeartList.toString());
                                        hourHeartList = new LinkedList<>();//list数据
                                        hourHeartList.add(item);
                                    }
                                    hourHeartValueMap.put(hourKey, hourHeartList);
                                }


                                /**
                                 * 去除 以时为 KEY的map
                                 */
                                if (!hourHeartValueMap.isEmpty()) {
                                    //int postions = 0;
                                    //遍历map中的键
                                    for (String hourMapKey : hourHeartValueMap.keySet()) {
                                        // postions++;
                                        //System.out.println("Key = " + hourMapKey+"------Value = " +hourHeartValueMap.get(hourMapKey));
                                        Log.d(TAG, "hour 要查询的 KEY ：" + hourMapKey);
                                        LinkedList<HeartData> hourHeartDataLinkedList = hourHeartValueMap.get(hourMapKey);//单个小时的心率数据集合

                                        Log.d(TAG, "-----H9-onSuccess- 单个小时的心率数据集合:=\n时间:"
                                                + hourMapKey
                                                + "=\n长度:" + (hourHeartDataLinkedList == null ? "没有数据" : hourHeartDataLinkedList.size())
                                                + "=\n数据:" + (hourHeartDataLinkedList == null ? "没有数据" : hourHeartDataLinkedList.toString()));
                                        int valueHeart = 0;
                                        if (hourHeartDataLinkedList != null && !hourHeartDataLinkedList.isEmpty()) {
                                            int heartValues = 0;
                                            int heartCount = 0;
                                            for (int j = 0; j < hourHeartDataLinkedList.size(); j++) {
                                                heartCount++;
                                                Log.d(TAG, j + "----" + hourMapKey + "的心率" + hourHeartDataLinkedList.get(j).heartRate_value + "-------" + hourHeartDataLinkedList.get(j).time_stamp);
                                                heartValues += hourHeartDataLinkedList.get(j).heartRate_value;
                                                Log.d(TAG, j + "----" + hourMapKey + "的心率" + heartValues);
                                            }
                                            if (heartCount == 0) heartCount = 1;
                                            valueHeart = heartValues / heartCount;
                                            Log.d(TAG, hourMapKey + "内的平均心率" + valueHeart);
                                        }
                                        if (h9DBCommont == null)
                                            h9DBCommont = H9DBCommont.getInstance();
                                        h9DBCommont.saveHeartToDB(valueHeart, dayKey + " " + hourMapKey + ":00");
                                    }
                                }

                            }
                        }
                    }


                    break;
                case 0x04://运动汇总

                    int deviceDisplayStep = GlobalVarManager.getInstance().getDeviceDisplayStep();//获取运动步数
                    double deviceDisplayCalorie = GlobalVarManager.getInstance().getDeviceDisplayCalorie();//卡路里
                    double deviceDisplayDistance = GlobalVarManager.getInstance().getDeviceDisplayDistance();//距离
                    /**
                     * 设置步数
                     */
                    setStepNumber(deviceDisplayStep, deviceDisplayCalorie, deviceDisplayDistance);

                    /**
                     * 读取步数数据上传
                     */
                    readStepUpdata(userId, mac, true);
                    /**
                     * 读取心率数据显示和上传
                     */
                    readHeartH9DBDataToUI(userId, mac, true);
                    /**
                     * 读取睡眠上传
                     */
                    readSleepUpdata(true);

                    if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
                        textStute.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 0x05:

                    /**
                     * 设置步数
                     */
                    try {
                        int deviceDisplaySteps = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_NUMBER", 0);
                        String deviceDisplayCalories = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_CAL", "0");
                        String deviceDisplayDistances = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_DIS", "0");
                        if (!TextUtils.isEmpty(deviceDisplayCalories.trim().replace(" ", ""))
                                && !TextUtils.isEmpty(deviceDisplayDistances.trim().replace(" ", ""))) {
                            setStepNumber(deviceDisplaySteps, Double.valueOf(deviceDisplayCalories.trim().replace(" ", ""))
                                    , Double.valueOf(deviceDisplayDistances.trim().replace(" ", "")));
                        }


                    } catch (Error error) {
                    }

                    /**
                     * 读取步数数据上传
                     */
                    readStepUpdata(userId, mac, false);
                    /**
                     * 读取心率数据显示和上传
                     */
                    readHeartH9DBDataToUI(userId, mac, false);
                    /**
                     * 读取睡眠上传
                     */
                    readSleepUpdata(false);

                    if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
                        textStute.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
            return false;
        }
    });


    /**
     * 设置运动步数
     */
    private void setStepNumber(int deviceDisplayStep, double deviceDisplayCalorie, double deviceDisplayDistance) {
        try {
            if (getActivity() != null && !getActivity().isFinishing() && recordwaveProgressBar != null) {
                textStute.setVisibility(View.INVISIBLE);

//                int h9_step_gaol = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_STEP_GAOL", 10000);
                String w30stag = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "w30stag", "10000");
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_STEP_NUMBER", deviceDisplayStep);
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_STEP_CAL", deviceDisplayCalorie + "");
                SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_STEP_DIS", deviceDisplayDistance + "");


                Log.d(TAG, "步数：" + deviceDisplayStep + "  距离：" + deviceDisplayDistance + "  卡路里：" + deviceDisplayCalorie);
                //刷新步数图
                String tagGoal = StringUtils.substringBefore(w30stag + "", ".");
                recordwaveProgressBar.setMaxValue(Integer.valueOf(w30stag.trim().replace(" ", "")));//最大值
                //目标设置
                recordwaveProgressBar.setTagStepStr(getActivity().getResources().getString(R.string.settarget_steps) + " " + tagGoal);
                recordwaveProgressBar.setValue(deviceDisplayStep);//步数
                recordwaveProgressBar.invalidate();


                if (deviceDisplayCalorie > 0) {
                    BigDecimal decimal = new BigDecimal(deviceDisplayCalorie / 1000.0);
                    BigDecimal setScale = decimal.setScale(0, BigDecimal.ROUND_DOWN);
                    L38iCalT.setText(setScale.intValue() + "cal");
                }


                h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
                // 0位公制 1为英制
                if (h9_step_util == 0) {
                    if (deviceDisplayDistance > 0) {
                        BigDecimal decimal = new BigDecimal(deviceDisplayDistance / 1000.0);
                        BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                        L38iDisT.setText(setScale.doubleValue() + "km");
                    }
                } else {
                    BigDecimal decimal = new BigDecimal(deviceDisplayDistance * 0.0006214);
                    BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                    L38iDisT.setText(setScale.doubleValue() + "mi");
                }

                //1英里(mi)=1609.344米(m)
                String trim = new DecimalFormat("#.##").format(((double) deviceDisplayStep / (double) Integer.valueOf(w30stag.trim().replace(" ", ""))) * 100).trim();
                if (!WatchUtils.isEmpty(trim) && trim.contains(".")) {
                    textStepReach.setText(trim.split("[.]")[0] + "%");//设置达标值
                } else {
                    textStepReach.setText(trim + "%");//设置达标值
                }


                /**
                 * 没上传后等待5分钟才可以再次上传
                 */
                String currentDate = WatchUtils.getCurrentDate1();
                String get_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "upStepTime", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                int sysTimes = H9TimeUtil.getTimeDifferenceMu(get_time, currentDate);
                if (sysTimes > 5) {
                    //上传运动数据到后台
                    BigDecimal decimalKcl = new BigDecimal(deviceDisplayCalorie / 1000.0);
                    BigDecimal setScaleKcl = decimalKcl.setScale(0, BigDecimal.ROUND_DOWN);
                    BigDecimal decimalDis = new BigDecimal(deviceDisplayDistance / 1000.0);
                    BigDecimal setScaleDis = decimalDis.setScale(2, BigDecimal.ROUND_DOWN);
                    //UpDatasBase.updateLoadSportToServer(Integer.valueOf(w30stag.trim().replace(" ", "")), deviceDisplayStep, setScaleKcl.doubleValue(), setScaleDis.doubleValue(), "");
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upStepTime", currentDate);
                }
            }
        } catch (Error error) {
        }

    }


    /**
     * 读取上传详细步数
     *
     * @param userId
     * @param bleMac
     * @param isClear
     */
    private void readStepUpdata(String userId, String bleMac, boolean isClear) {
        try {
            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
            List<H9StepDBModel> stepDBModels = h9DBCommont.readDBStepTo();
            if (upHeartBeanList == null) upHeartBeanList = new ArrayList<>();
            else upHeartBeanList.clear();

            int times = 0;
            int stepNumber = 0;
            if (!stepDBModels.isEmpty()) {
                times = 0;
                for (int i = 0; i < stepDBModels.size(); i++) {
                    if (stepDBModels.get(i).getRec().equals(WatchUtils.getCurrentDate())) {//判断只传今天
                        times++;
                        stepNumber += stepDBModels.get(i).getStepNumber();
//                        upHeartBeanList.add(new UpHeartBean(userId, bleMac, "00",
//                                stepDBModels.get(i).getStepNumber() + "",
//                                WatchUtils.getCurrentDate() + " " + (times > 9 ? times + "" : "0" + times) + ":00",
//                                "00", "0", "00", "00"));
                        UpHeartBean upHeartBean = new UpHeartBean(userId, bleMac,
                                0, 0,
                                0, 0,
                                0, WatchUtils.getCurrentDate() + " " + (times > 9 ? times + "" : "0" + times) + ":00",
                                stepDBModels.get(i).getStepNumber());
                        upHeartBeanList.add(upHeartBean);
                        //Log.d("-G---设置", "----------步数占位时间==" + (times > 9 ? times + "" : "0" + times) + ":00");
                    }
                }

                if (upHeartBeanList != null && !upHeartBeanList.isEmpty()) {
                    //删除设备上详细运动数据
                    //这里要先同步在清除数据不然就。。。
                    if (isClear)
                        AppsBluetoothManager.getInstance(MyApp.getInstance())
                                .sendCommand(new ClearSportData(commandResultCallbackAll));

                    JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
                    //Log.d("-G---要上传的步数", jsonArray1.toString());
                    //UpDatasBase.upAllDataSteps(jsonArray1);
                }

            }

        } catch (Error error) {
        }

    }


    /**
     * 读取上传详细睡眠-----并且显示UI
     *
     * @param isClear 是否清空设备数据
     */
    private void readSleepUpdata(boolean isClear) {
//        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
//        H9SleepDBModelDao h9SleepDBModelDao = daoSession.getH9SleepDBModelDao();
//        if (h9SleepDBModelDao == null) return;
//
//        String timeYesterday = WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(), true);
//        List<H9SleepDBModel> sleepDBModels = h9SleepDBModelDao.loadAll();

        if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
        List<H9SleepDBModel> sleepDBModels = h9DBCommont.readDBSleepTo();
        String timeYesterday = WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(), true);
        if (sleepDBModels != null) {
            List<W30S_SleepDataItem> sleepBeanItemModels = new ArrayList<>();
            for (int i = 0; i < sleepDBModels.size(); i++) {
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
                // sleepData.sleep_type
                // 0：睡着
                // 1：浅睡
                // 2：醒着
                // 3：准备入睡
                // 4：退出睡眠
                // 16：进入睡眠模式
                // 17：退出睡眠模式（本次睡眠非预设睡眠）
                // 18：退出睡眠模式（本次睡眠为预设睡眠）
                if (sleepDBModels.get(i).getRec().equals(WatchUtils.getCurrentDate())
                        || sleepDBModels.get(i).getRec().equals(timeYesterday)) {//判断只拿今天和昨天的数据数据

                    H9SleepDBModel h9SleepDBModel = sleepDBModels.get(i);
                    if (h9SleepDBModel != null) {
                        int sleepType = h9SleepDBModel.getSleepType();
                        String sleepTime = h9SleepDBModel.getSleepTime();
                        W30S_SleepDataItem w30S_sleepDataItem = new W30S_SleepDataItem();

                        w30S_sleepDataItem.setStartTime(sleepTime);

                        if (sleepType == 3 || sleepType == 16) {//进入睡眠时间
                            isIntoSleep = true;
                            sleepBeanItemModels.clear();
                            w30S_sleepDataItem.setSleep_type("1");
                        } else if (sleepType == 4 || sleepType == 17 || sleepType == 18) {
                            w30S_sleepDataItem.setSleep_type("4");
                        } else if (sleepType == 2) {
                            w30S_sleepDataItem.setSleep_type("1");
                        } else if (sleepType == 1) {
                            w30S_sleepDataItem.setSleep_type("2");
                        } else if (sleepType == 0) {
                            w30S_sleepDataItem.setSleep_type("3");
                        }
                        Log.d(TAG, "-------睡眠对象转换  sleepTime:" + sleepTime + "  sleepType:" + sleepType);
                        if (isIntoSleep) sleepBeanItemModels.add(w30S_sleepDataItem);
                    }


                    Log.d(TAG, "----今日睡眠数据 AAA" + sleepDBModels.get(i).getRec() + "===" + sleepDBModels.get(i).getSleepTime() + "===" + sleepDBModels.get(i).getSleepType());

                }
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
            }

            isIntoSleep = false;//恢复未进入过睡眠的状态，方便下次读取加载

            /**
             * 上传所有睡眠数据  -- json串
             */
            if (!sleepBeanItemModels.isEmpty()) {
                JSONArray jsonArray = ProLogList2Json(sleepBeanItemModels);
                Log.e(TAG, "=====要上传的睡眠" + jsonArray.toString());
                if (isClear) AppsBluetoothManager.getInstance(MyApp.getInstance())
                        .sendCommand(new ClearSleepData(commandResultCallbackAll));
                //UpDatasBase.upDataTodaSleep(WatchUtils.getCurrentDate(), jsonArray);


                //显示UI
                setSleepUI(sleepBeanItemModels);
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getInstance()).clearCommandBlockingDeque();
    }

    int AWAKE2 = 0;//清醒
    int AWAKE = 0;//清醒次数
    int AOYE = 0;//熬夜
    int DEEP = 0;//深睡
    int SHALLOW = 0;//浅睡
    int ALLTIME = 0;//浅睡

    /**
     * 计算所睡眠的数据
     *
     * @param sleepDataLists
     */
    private void setSleepUI(List<W30S_SleepDataItem> sleepDataLists) {
        try {
            //Log.d(TAG, "=========睡眠数据" + sleepDataLists.toString());
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (sleepDataLists.isEmpty()) {
                    if (w30S_sleepChart != null) w30S_sleepChart.setVisibility(View.GONE);
                    if (text_sleep_nodata != null) text_sleep_nodata.setVisibility(View.VISIBLE);
                } else {
                    ALLTIME = 0;
                    DEEP = 0;
                    SHALLOW = 0;
                    AWAKE2 = 0;
                    AWAKE = 0;
                    for (int i = 0; i < sleepDataLists.size(); i++) {
                        String startTime = null;
                        String startTimeLater = null;
                        String sleep_type = null;
                        if (i >= (sleepDataLists.size() - 1)) {
                            startTime = sleepDataLists.get(i).getStartTime();
                            startTimeLater = sleepDataLists.get(i).getStartTime();
                            sleep_type = sleepDataLists.get(i).getSleep_type() + "";
                        } else {
                            startTime = sleepDataLists.get(i).getStartTime();
                            startTimeLater = sleepDataLists.get(i + 1).getStartTime();
                            sleep_type = sleepDataLists.get(i).getSleep_type() + "";
                        }
                        String[] starSplit = startTime.split("[:]");
                        String[] endSplit = startTimeLater.split("[:]");

                        int startHour = Integer.valueOf(starSplit[0]);
                        int endHour = Integer.valueOf(endSplit[0]);

                        int startMin = Integer.valueOf(starSplit[1]);
                        int endMin = (Integer.valueOf(endSplit[1]));
                        if (startHour > endHour) {
                            endHour = endHour + 24;
                        }
                        int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                        if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
                            AWAKE2 += all_m;
                            ALLTIME += all_m;
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

                    if (!sleepDataLists.isEmpty()) {
                        if (w30S_sleepChart != null && text_sleep_nodata != null) {
                            w30S_sleepChart.setBeanList(sleepDataLists);
                            w30S_sleepChart.setVisibility(View.VISIBLE);
                            text_sleep_nodata.setVisibility(View.GONE);
                        }

                        ////总睡眠设置为可拖动最大进度
                        if (seekBarSleep != null) {
                            seekBarSleep.setMax(ALLTIME);
                            seekBarSleep.setEnabled(true);
                        }

                    }
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
                    LogTestUtil.e(TAG, "睡眠统计----" + "入睡时间：" + sleepDataLists.get(0).getStartTime());
                    LogTestUtil.e(TAG, "睡眠统计----" + "醒来时间：" + sleepDataLists.get(sleepDataLists.size() - 1).getStartTime());

                    if (sleepIntoTime != null)
                        sleepIntoTime.setText(sleepDataLists.get(0).getStartTime());
                    if (sleepOutTime != null)
                        sleepOutTime.setText(sleepDataLists.get(sleepDataLists.size() - 1).getStartTime());

                    double setScaleA = (double) WatchUtils.div((double) AWAKE2, 60, 1);
                    LogTestUtil.e(TAG, "睡眠统计----" + "清醒：" + setScaleA);
                    if (awakeSleep != null)
                        awakeSleep.setText(setScaleA + getResources().getString(R.string.hour));

                    double setScaleD = (double) WatchUtils.div((double) DEEP, 60, 1);
                    LogTestUtil.e(TAG, "睡眠统计----" + "深睡：" + setScaleD);
                    if (deepSleep != null)
                        deepSleep.setText(setScaleD + getResources().getString(R.string.hour));

                    DecimalFormat df = new DecimalFormat("#.#");
                    String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).trim();
                    double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
                    double v = Double.valueOf(div3) - setScale;
                    double setScaleS = (double) WatchUtils.div((double) v, 1, 1);
                    LogTestUtil.e(TAG, "睡眠统计----" + "浅睡：" + setScaleS);
                    if (shallowSleep != null)
                        shallowSleep.setText(setScaleS + getResources().getString(R.string.hour));

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


                    /**
                     * 没上传后等待5分钟才可以再次上传
                     */
                    String currentDate = WatchUtils.getCurrentDate1();
                    String get_time = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "upSleepTime", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                    int sysTimes = H9TimeUtil.getTimeDifferenceMu(get_time, currentDate);
                    if (sysTimes > 120) {
                        //上传运动数据到后台
                        String starTime = sleepDataLists.get(0).getStartTime();
                        String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
                        //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
                        //UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
                        SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", currentDate);
                    }

//                    String homeTime = (String) SharedPreferencesUtils.getParam(getActivity(), "upSleepTime", "2017-11-02 15:00:00");
//                    if (!TextUtils.isEmpty(homeTime)) {
//                        String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
//                        if (!WatchUtils.isEmpty(timeDifference)) {
//                            int number = Integer.valueOf(timeDifference.trim());
//                            int number2 = Integer.parseInt(timeDifference.trim());
//                            if (Math.abs(number) >= 3600 || Math.abs(number2) >= 3600) {
//                                String starTime = sleepDataLists.get(0).getStartTime();
//                                String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
//                                //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
//                                UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
//                                SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
//                            }
//                        }
//                    } else {
//                        String starTime = sleepDataLists.get(0).getStartTime();
//                        String endTime = sleepDataLists.get(sleepDataLists.size() - 1).getStartTime();
//                        //Log.d(TAG, "解析睡眠数据 = 起始 = " + starTime + "----" + endTime);
//                        UpDatasBase.upDevicesDataSleep(DEEP + "", SHALLOW + "", starTime, endTime);//上传睡眠数据
//                        SharedPreferencesUtils.setParam(getActivity(), "upSleepTime", B18iUtils.getSystemDataStart());
//
//                    }
//
                }
            }
        } catch (Error e) {
        }

    }

    /**
     * 读取心率数据---设置UI
     *
     * @param userId
     * @param bleMac
     * @param isClear
     */
    private void readHeartH9DBDataToUI(String userId, String bleMac, boolean isClear) {
        try {
//        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
//        H9HeartDBModelDao h9HeartDBModelDao = daoSession.getH9HeartDBModelDao();
//        if (h9HeartDBModelDao == null) return;
//        Log.e(TAG, "=====查询条件==" + WatchUtils.getCurrentDate() + "==" + userId + "==" + bleMac);
//        List<H9HeartDBModel> h9HeartDBModelList = h9HeartDBModelDao.loadAll();
            if (h9DBCommont == null) h9DBCommont = H9DBCommont.getInstance();
            List<H9HeartDBModel> h9HeartDBModelList = h9DBCommont.readDBHeartTo();
            if (h9HeartDBModelList != null && !h9HeartDBModelList.isEmpty()) {
                Log.e(TAG, "=====查询所得到的数据长度==" + h9HeartDBModelList.size());
                if (heartList == null) heartList = new ArrayList<>();
                else heartList.clear();
                if (upHeartBeanList == null) upHeartBeanList = new ArrayList<>();
                else upHeartBeanList.clear();

                /**
                 * 原来保存的心率数据为24条  心率图标需要48条，转下
                 */
                h9DBCommont.updataHeartDeful(upHeartBeanList, heartList, userId, bleMac);
                int heartIndex = 0;
                int unZero = 0;
                int unZeroDatas = 0;
                for (int i = 0; i < h9HeartDBModelList.size(); i++) {
                    H9HeartDBModel h9HeartDBModel = h9HeartDBModelList.get(i);
                    if (h9HeartDBModel != null) {
                        if (h9HeartDBModel.getRec().equals(WatchUtils.getCurrentDate())) {//判断只拿今天数据
                            if (heartIndex < 24) {
                                if (h9HeartDBModel.getHeartValue() > 0) {
                                    unZero++;
                                    unZeroDatas += h9HeartDBModel.getHeartValue();
                                }
                                // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
                                //25 26 26 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48
                                //0 2 4 6 8 10 12 14 16 18 20 22 24 26 28 30 32 34 36 38 40 42 44 46 48
                                heartList.set(heartIndex * 2, h9HeartDBModel.getHeartValue());
//                                upHeartBeanList.set(heartIndex * 2, new UpHeartBean(userId,
//                                        bleMac, "00",
//                                        "00", WatchUtils.getCurrentDate() + " " + (heartIndex > 9 ? heartIndex + "" : "0" + heartIndex) + ":00",
//                                        h9HeartDBModel.getHeartValue() + "", "0", "00", "00"));
                                UpHeartBean upHeartBean = new UpHeartBean(userId, bleMac,
                                        h9HeartDBModel.getHeartValue(), 0,
                                        0, 0,
                                        0, WatchUtils.getCurrentDate() + " " + (heartIndex > 9 ? heartIndex + "" : "0" + heartIndex) + ":00",
                                        0);
                                upHeartBeanList.set(heartIndex * 2, upHeartBean);
                                heartIndex++;
                            }
                        }
                    }
                }

                if (heartList != null&& !heartList.isEmpty() && Collections.max(heartList) > 0) {
                    Log.e(TAG, "=====查询所得到的数据长度==" + heartList.toString());
                    if (seekBarHeart != null) seekBarHeart.setEnabled(true);//设置可滑动
                    if (heartChart != null) {
                        heartChart.setRateDataList(heartList);
                        heartChart.invalidate();
                    }
                }


                /**
                 * 上传心率详细数据
                 */
                if (upHeartBeanList != null && !upHeartBeanList.isEmpty() && Collections.max(heartList) > 0) {
                    if (isClear) {
                        AppsBluetoothManager.getInstance(MyApp.getInstance())
                                .sendCommand(new ClearHeartData(commandResultCallbackAll));
                        AppsBluetoothManager.getInstance(MyApp.getInstance())
                                .sendCommand(new ClearBloodData(commandResultCallbackAll));
                    }


                    JSONArray jsonArray1 = ProLogListJson(upHeartBeanList);
                    Log.d("-G---要上传的心率", jsonArray1.toString());
                    //UpDatasBase.upAllDataHearte(jsonArray1);


                    Log.d(TAG, "======心率总数居+++有效长度" + heartList.size());
                    int auto = unZeroDatas / unZero;
                    int max = Collections.max(heartList);
                    int min = getSmall(heartList);
                    if (maxHeart != null) maxHeart.setText(max + "bpm");//最高
                    if (autoHeart != null) autoHeart.setText(auto + "bpm");//平均
                    if (minHeart != null) minHeart.setText((min == 0 ? "0.0" : min) + "bpm");//最低
                }

            }

        } catch (Error error) {
        }
    }


    /**
     * 遍历获取最小值
     *
     * @param list
     * @return
     */
    int getSmall(List<Integer> list) {
        List<Integer> min = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) > 0) {
                    min.add(list.get(i));
                }
            }
        }
        if (min.isEmpty()) {
            return 0;
        } else {
            return Collections.min(min);
        }
    }

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
     * 显示电量
     *
     * @param battery
     */
    private void setBatteryPowerShow(int battery) {
        Log.e(TAG, "----------battery=" + battery);
        if (getActivity() != null && !getActivity().isFinishing() && watchTopBatteryImgView != null) {
            try {
                if (battery <= 100 && battery > 80) {
                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
                }
                if (battery <= 80 && battery > 60) {
                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
                }
                if (battery <= 60 && battery > 40) {
                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
                }
                if (battery <= 40 && battery > 20) {
                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
                }
                if (battery <= 20 && battery > 0) {
                    watchTopBatteryImgView.setBackground(getResources().getDrawable(R.mipmap.image_battery_one));
                }
                if (batteryPowerTv != null) batteryPowerTv.setText(battery + "%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 链接监听广播
     */
    BroadcastReceiver commandResultBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String h9Redata = intent.getStringExtra("h9constate");
                if (!WatchUtils.isEmpty(h9Redata)) {
                    if (h9Redata.equals("conn") && !getActivity().isFinishing()) {    //已链接
                        MyCommandManager.DEVICENAME = "H9";
                    } else {
                        MyCommandManager.DEVICENAME = null;
                    }
                    //同步数据
                    synchronization(true);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 心率图 滑动监听 返回值
     *
     * @param value  心率值
     * @param times  时间
     * @param toTime 是否已经测量
     */
    @Override
    public void OnDataTypeListenter(int value, String times, boolean toTime) {
        Log.d("-----------AA", "华动中" + value + "====times=" + times);
        if (frm_heard != null) frm_heard.setVisibility(View.VISIBLE);
        if (toTime) {
            if (heardValue != null) heardValue.setText("  " + value);
        } else {
            if (heardValue != null)
                heardValue.setText("  " + getResources().getString(R.string.nodata));
        }
        if (heartTimes != null) heartTimes.setText(times);
    }


    /**
     * 睡眠图 滑动监听 返回值
     *
     * @param type
     * @param startTime
     * @param endTime
     */
    @Override
    public void OnDataTypeListenter(String type, String startTime, String endTime) {
        try {
            if (textSleepLines != null) textSleepLines.setText(" -- ");
            if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                if (textSleepType != null)
                    textSleepType.setText(getResources().getString(R.string.waking_state));
                if (textSleepStart != null) textSleepStart.setText(startTime);
                if (textSleepEnd != null) textSleepEnd.setText(endTime);
            } else if (type.equals("2")) {  //潜睡状态
                if (textSleepType != null)
                    textSleepType.setText(getResources().getString(R.string.shallow_sleep));
                if (textSleepStart != null) textSleepStart.setText(startTime);
                if (textSleepEnd != null) textSleepEnd.setText(endTime);
            } else if (type.equals("3")) {  //深睡
                if (textSleepType != null)
                    textSleepType.setText(getResources().getString(R.string.deep_sleep));
                if (textSleepStart != null) textSleepStart.setText(startTime);
                if (textSleepEnd != null) textSleepEnd.setText(endTime);
            } else if (type.equals("88")) {
                Log.d("----------", "起床");
                if (textSleepType != null)
                    textSleepType.setText(getResources().getString(R.string.getup));
                if (textSleepStart != null) textSleepStart.setText(startTime);
                if (textSleepEnd != null) textSleepEnd.setText(endTime);
            }
        } catch (Error error) {
        }
    }

    /**
     * 心率进度条拖动监听
     *
     * @param seekBar
     * @param progress
     * @param b
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seek_bar_my_heart:
                //H9 心率没有半点，所以不让用户滑动到半点的位置,并且大于48点之后的就不用去标记了-----多个点多一次VIEW界面刷新
                if (heartChart != null && progress <= 48 && progress % 2 != 0)
                    heartChart.setSeekBar((float) progress);
                break;
            case R.id.seek_bar_my_sleep:
                if (w30S_sleepChart != null) w30S_sleepChart.setSeekBar((float) progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (heartChart != null) {
            boolean clickableHeart = heartChart.isClickable();
            if (!clickableHeart) {
                if (seekBarHeart != null) {
                    seekBarHeart.setProgress(0);
                    seekBarHeart.clearAnimation();
                    seekBarHeart.invalidate();
                }
                if (heartChart != null) heartChart.setSeekBar(0);
                if (frm_heard != null) frm_heard.setVisibility(View.INVISIBLE);
            }
        }

        if (w30S_sleepChart != null) {
            boolean clickableSleep = w30S_sleepChart.isClickable();
            if (!clickableSleep) {
                if (seekBarSleep != null) {
                    seekBarSleep.setProgress(0);
                    seekBarSleep.clearAnimation();
                    seekBarSleep.invalidate();
                }
                if (w30S_sleepChart != null) w30S_sleepChart.setSeekBar(0);
                if (textSleepType != null) textSleepType.setText(" ");
                if (textSleepStart != null) textSleepStart.setText(" ");
                if (textSleepLines != null) textSleepLines.setText(" ");
                if (textSleepEnd != null) textSleepEnd.setText(" ");
            }
        }

    }


}
