package com.example.bozhilun.android.h9.settingactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.setting.AutoSleep;
import com.sdk.bluetooth.protocol.command.setting.HeartRateAlarm;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class H9SwitchActivity extends WatchBaseActivity {

    private static final String TAG = "H9SwitchActivity";
    @BindView(R.id.switch_fangdiu)
    ToggleButton switchFangdiu;
    @BindView(R.id.switch_heart)
    ToggleButton switchHeart;
    @BindView(R.id.line_heart_auto)
    LinearLayout lineHeartAuto;
    @BindView(R.id.auto_heart_line)
    LinearLayout autoHeartLine;
    @BindView(R.id.switch_nodisturb)
    ToggleButton switchNodisturb;
    @BindView(R.id.line_sleep)
    LinearLayout lineSleep;
    @BindView(R.id.auto_sleep_line)
    LinearLayout autoSleepLine;
    @BindView(R.id.start_sleep)
    TextView startSleep;
    @BindView(R.id.end_sleep)
    TextView endSleep;
    @BindView(R.id.hight_heart)
    TextView hightHeart;
    @BindView(R.id.low_heart)
    TextView lowHeart;
    @BindView(R.id.switch_must_sleep)
    ToggleButton switchMustSleep;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_swtich_setting_activity);
        ButterKnife.bind(this);

        newSearchTitleTv.setText(getResources().getString(R.string.string_switch_setting));
        addHeartDatas();
        initView();
    }

    private void initView() {

        switchHeart.setOnCheckedChangeListener(new CheckedListenter());//心率自测和预警
        switchFangdiu.setOnCheckedChangeListener(new CheckedListenter());//防止丢失
        switchMustSleep.setOnCheckedChangeListener(new CheckedListenter());//强制睡眠
        switchNodisturb.setOnCheckedChangeListener(new CheckedListenter());//自动心率检测---预设警告
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog(getResources().getString(R.string.dlog));

        /**
         * 第一步
         */
        AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new SwitchSetting(commandResultCallback));
    }

    @OnClick({R.id.line_gaoya, R.id.line_diya,
            R.id.line_rushui, R.id.line_qingxing,
            R.id.newSearchTitleLeft, R.id.line_heart_auto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.line_gaoya://预测高压警告
                showHeartPop();
                break;
            case R.id.line_diya://预测低压警告
                showHeartPop();
                break;
            case R.id.line_rushui://预测睡眠时间
                showPop(true);
                break;
            case R.id.line_qingxing://预设起床时时间
                showPop(false);
                break;
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.line_heart_auto://去心率手动检测界面
                startActivity(new Intent(H9SwitchActivity.this,
                        H9HearteTestActivity.class));
                break;
        }
    }

    /**
     * 开关
     */
    private class CheckedListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.isPressed()) {
                switch (buttonView.getId()) {
                    case R.id.switch_fangdiu:
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        /**
                         * 防丢开关
                         * 参数一  callback
                         * 参数二  长度固定为3
                         * 参数三  模式 0x01
                         * 参数四  开关类型参照以上类型...
                         * 参数五  1:表示开，0:表示关
                         * @param value 1=开，0=关
                         */
                        if (isChecked) {
                            AppsBluetoothManager.getInstance(MyApp.getContext()).
                                    sendCommand(new SwitchSetting(commandResultCallback, 3,
                                            (byte) 0x01, (byte) 0, (byte) 0x01));
                        } else {
                            AppsBluetoothManager.getInstance(MyApp.getContext()).
                                    sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0, (byte) 0));
                        }

                        break;
                    case R.id.switch_heart:
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        int h9_heartautoh = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_HEARTAUTOH", 120);
                        int h9_heartautol = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_HEARTAUTOL", 70);

                        // 是否预警
                        // 预警最大值
                        // 预警最小值
                        // 是否自动检测
                        // 检测周期间隔 （自动监控 单位必须为5分钟）
                        HeartRateAlarm setHeartRateAlarm =
                                new HeartRateAlarm(commandResultCallback, false, (byte) h9_heartautoh, (byte) h9_heartautol,
                                        isChecked, (byte) 10);
                        AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(setHeartRateAlarm);
                        break;
                    case R.id.switch_must_sleep:
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        /**
                         * 强制睡眠
                         *
                         * @param value 1=开，0=关
                         */
                        if (isChecked) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0x02, (byte) 0x01));
                        } else {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0x02, (byte) 0x00));
                        }
                        break;
                    case R.id.switch_nodisturb:
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        /**
                         * 自动睡眠检测额
                         *
                         * @param value 1=开，0=关
                         */
                        if (isChecked) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0x03, (byte) 0x01));
                        } else {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0x03, (byte) 0x00));
                        }
                        break;
                }
            }

        }
    }


    BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof SwitchSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    boolean sleepStateSwitch = GlobalVarManager.getInstance().isSleepStateSwitch();//预设睡眠
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SLEEPSTATE", sleepStateSwitch);
                    switchNodisturb.setChecked(sleepStateSwitch);//预设睡眠状态
                    if (sleepStateSwitch) {
                        //autoSleepLine.setVisibility(View.VISIBLE);
                        /**
                         * 只要开关打开就设置睡眠时间
                         */
                        setAoutSleep();
                    } else {
                        AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new HeartRateAlarm(commandResultCallback));
                    }
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    closeLoadingDialog();
//                    boolean sleepStateSwitch = GlobalVarManager.getInstance().isSleepStateSwitch();//预设睡眠
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SLEEPSTATE", !sleepStateSwitch);
//                    switchNodisturb.setChecked(!sleepStateSwitch);//预设睡眠状态
//                    if (!sleepStateSwitch) {
//
//                        //autoSleepLine.setVisibility(View.VISIBLE);
//                        /**
//                         * 只要开关打开就设置睡眠时间
//                         */
//                        setAoutSleep();
//                    } else {
//                        AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new HeartRateAlarm(commandResultCallback));
//                    }
                }
            } else if (baseCommand instanceof HeartRateAlarm) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    boolean autoHeart = GlobalVarManager.getInstance().isAutoHeart();//心率自测
                    boolean heartAlarm = GlobalVarManager.getInstance().isHeartAlarm();//心率预警
                    int highHeartLimit = GlobalVarManager.getInstance().getHighHeartLimit();
                    int lowHeartLimit = GlobalVarManager.getInstance().getLowHeartLimit();
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOSWITCH", autoHeart);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOALARMSWITCH", heartAlarm);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOH", highHeartLimit);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOL", lowHeartLimit);
                    //判断心率预警是否打开
//                    if (heartAlarm) {
//                        autoHeartLine.setVisibility(View.VISIBLE);
//                    } else {
//                        autoHeartLine.setVisibility(View.GONE);
//                    }
                    setHeartAutoTimes(heartAlarm);
                    switchHeart.setChecked(autoHeart);//自动心率检测状态
                    closeLoadingDialog();
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
//                    boolean autoHeart = GlobalVarManager.getInstance().isAutoHeart();//心率自测
//                    boolean heartAlarm = GlobalVarManager.getInstance().isHeartAlarm();//心率预警
//                    int highHeartLimit = GlobalVarManager.getInstance().getHighHeartLimit();
//                    int lowHeartLimit = GlobalVarManager.getInstance().getLowHeartLimit();
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOSWITCH", !autoHeart);
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOALARMSWITCH", !heartAlarm);
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOH", highHeartLimit);
//                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOL", lowHeartLimit);
//                    //判断心率预警是否打开
////                    if (!heartAlarm) {
////                        autoHeartLine.setVisibility(View.VISIBLE);
////                    } else {
////                        autoHeartLine.setVisibility(View.GONE);
////                    }
//                    setHeartAutoTimes(!heartAlarm);
//                    switchHeart.setChecked(!autoHeart);//自动心率检测状态
                    closeLoadingDialog();
                }


            } else if (baseCommand instanceof AutoSleep) {
                AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new HeartRateAlarm(commandResultCallback));
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            if (baseCommand instanceof SwitchSetting) {

                boolean sleepStateSwitch = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_SLEEPSTATE", false);//预设睡眠
                switchNodisturb.setChecked(sleepStateSwitch);//预设睡眠状态
                if (sleepStateSwitch) {
                    //autoSleepLine.setVisibility(View.VISIBLE);
                    /**
                     * 只要开关打开就设置睡眠时间
                     */
                    setAoutSleep();
                } else {
                    AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new HeartRateAlarm(commandResultCallback));
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    closeLoadingDialog();
                }
            } else if (baseCommand instanceof AutoSleep) {
                AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(new HeartRateAlarm(commandResultCallback));
            } else if (baseCommand instanceof HeartRateAlarm) {
                closeLoadingDialog();
                boolean autoHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_HEARTAUTOSWITCH", false);//心率自测
                boolean heartAlarm = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_HEARTAUTOALARMSWITCH", false);//心率预警
                //判断心率预警是否打开
                if (heartAlarm) {
                    autoHeartLine.setVisibility(View.VISIBLE);
                } else {
                    autoHeartLine.setVisibility(View.GONE);
                }
                setHeartAutoTimes(heartAlarm);
                switchHeart.setChecked(autoHeart);//自动心率检测状态
            }
        }
    };


    /**
     * 心率预警时间读取显示
     *
     * @param heartAlarm
     */
    private void setHeartAutoTimes(boolean heartAlarm) {
        if (heartAlarm) {
            int lowHeartLimit = GlobalVarManager.getInstance().getLowHeartLimit();
            int highHeartLimit = GlobalVarManager.getInstance().getHighHeartLimit();

            Log.d(TAG, "-------心率预警 -- " + highHeartLimit + "=======" + lowHeartLimit);
            SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOH", highHeartLimit);
            SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_HEARTAUTOL", lowHeartLimit);
            lowHeart.setText(lowHeartLimit + "");
            hightHeart.setText(highHeartLimit + "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getInstance()).clearCommandBlockingDeque();
    }

    /**
     * 睡眠预警时间读取显示
     */
    private void setSleepAutoTimes() {
        String mHour = "";
        String mMin = "";
        String mHours = "";
        String mMins = "";
        if (GlobalVarManager.getInstance().getEnterSleepHour() <= 9) {
            mHour = "0" + GlobalVarManager.getInstance().getEnterSleepHour();
        } else {
            mHour = "" + GlobalVarManager.getInstance().getEnterSleepHour();
        }
        if (GlobalVarManager.getInstance().getEnterSleepMin() <= 9) {
            mMin = "0" + GlobalVarManager.getInstance().getEnterSleepMin();
        } else {
            mMin = "" + GlobalVarManager.getInstance().getEnterSleepMin();
        }
        if (GlobalVarManager.getInstance().getQuitSleepHour() <= 9) {
            mHours = "0" + GlobalVarManager.getInstance().getQuitSleepHour();
        } else {
            mHours = "" + GlobalVarManager.getInstance().getQuitSleepHour();
        }
        if (GlobalVarManager.getInstance().getQuitSleepMin() <= 9) {
            mMins = "0" + GlobalVarManager.getInstance().getQuitSleepMin();
        } else {
            mMins = "" + GlobalVarManager.getInstance().getQuitSleepMin();
        }
        startSleep.setText(mHour + ":" + mMin);
        endSleep.setText(mHours + ":" + mMins);

    }


    String a, b, c, d;

    /**
     * 进入此界面只要判断睡眠预设石达开状态就设置预设睡眠时间时间
     * 睡眠预设默认设置   下午 10:00 --  上午  08:00
     */
    void setAoutSleep() {
        String eH = (String) SharedPreferencesUtils.getParam(H9SwitchActivity.this, "eH", "22");
        String eM = (String) SharedPreferencesUtils.getParam(H9SwitchActivity.this, "eM", "00");
        String qH = (String) SharedPreferencesUtils.getParam(H9SwitchActivity.this, "qH", "08");
        String qM = (String) SharedPreferencesUtils.getParam(H9SwitchActivity.this, "qM", "00");

        if (TextUtils.isEmpty(eH)
                && TextUtils.isEmpty(eM)
                && TextUtils.isEmpty(qH)
                && TextUtils.isEmpty(qM)) {
            finish();
            return;
        }
        showLoadingDialog(getResources().getString(R.string.dlog));
        if (TextUtils.isEmpty(a)) {
            a = "00";
        }
        if (TextUtils.isEmpty(b)) {
            b = "00";
        }
        if (TextUtils.isEmpty(c)) {
            c = "00";
        }
        if (TextUtils.isEmpty(d)) {
            d = "00";
        }
        if (TextUtils.isEmpty(eH)) {
            eH = a;
        }
        if (TextUtils.isEmpty(eM)) {
            eM = b;
        }
        if (TextUtils.isEmpty(qH)) {
            qH = c;
        }
        if (TextUtils.isEmpty(qM)) {
            qM = d;
        }
        int eh = Integer.valueOf(eH);
        int em = Integer.valueOf(eM);
        int qh = Integer.valueOf(qH);
        int qm = Integer.valueOf(qM);
        showLoadingDialog(getResources().getString(R.string.dlog));
        Log.d(TAG, "预设睡眠时间为 ：" + eh + "-" + em + " -- " + qh + "-" + qm);
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new AutoSleep(commandResultCallback,
                        (byte) eh,
                        (byte) em,
                        (byte) qh,
                        (byte) qm, (byte) 7F));

    }


    ArrayList<String> hourList = new ArrayList<>();
    ArrayList<String> mounList = new ArrayList<>();
    HashMap<String, ArrayList<String>> pickList = new HashMap<>();

    ArrayList<String> hegList = new ArrayList<>();
    ArrayList<String> lowhourList = new ArrayList<>();
    HashMap<String, ArrayList<String>> picHeartkList = new HashMap<>();

    public void addHeartDatas() {
        for (int i = 0; i <= 60; i++) {
            mounList.add(i > 9 ? i + "" : "0" + i);
        }
        for (int i = 0; i <= 24; i++) {
            hourList.add(i > 9 ? i + "" : "0" + i);
            pickList.put((i > 9 ? i + "" : "0" + i), mounList);
        }

        for (int i = 31; i <= 230; i++) {
            lowhourList.add((i + ""));
        }
        for (int i = 30; i <= 220; i++) {
            hegList.add(i + "");
            picHeartkList.put((i + ""), lowhourList);
        }
    }


    void showPop(final boolean isWhat) {
        ProvincePick starPopWin = new ProvincePick.Builder(H9SwitchActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                Log.e("------===", province + "==" + city + "==" + dateDesc);
                if (isWhat) {
//                    SharedPreferencesUtils.setParam(H9SwitchActivity.this, "eH", province.substring(0, province.length() - 2));
//                    SharedPreferencesUtils.setParam(H9SwitchActivity.this, "eM", city.substring(0, city.length() - 2));
//                    sleepText.setText(province.substring(0, province.length() - 2) + ":" + city.substring(0, city.length() - 2));
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "eH", province);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "eM", city);
                    startSleep.setText(province + ":" + city);
                    setAoutSleep();
                } else {
//                    SharedPreferencesUtils.setParam(H9SwitchActivity.this, "qH", province.substring(0, province.length() - 2));
//                    SharedPreferencesUtils.setParam(H9SwitchActivity.this, "qM", city.substring(0, city.length() - 2));
//                    sleepText2.setText(province.substring(0, province.length() - 2) + ":" + city.substring(0, city.length() - 2));
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "qH", province);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "qM", city);
                    endSleep.setText(province + ":" + city);
                    setAoutSleep();
                }

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(pickList) // max year in loop
                .build();
        starPopWin.showPopWin(H9SwitchActivity.this);
    }

    void showHeartPop() {

        ProvincePick starPopWin = new ProvincePick.Builder(H9SwitchActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                Log.e("------===", province + "==" + city + "==" + dateDesc);
                hightHeart.setText(province);
                lowHeart.setText(city);

                // 是否预警
                // 预警最大值
                // 预警最小值
                // 是否自动检测
                // 检测周期间隔 （自动监控 单位必须为5分钟）
                int h = Integer.valueOf(province);
                int l = Integer.valueOf(city);
                HeartRateAlarm setHeartRateAlarm =
                        new HeartRateAlarm(commandResultCallback, true, (byte) h, (byte) l,
                                true, (byte) 10);
                AppsBluetoothManager.getInstance(H9SwitchActivity.this).sendCommand(setHeartRateAlarm);

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(lowhourList) //min year in loop
                .setCityList(picHeartkList) // max year in loop
                .build();
        starPopWin.showPopWin(H9SwitchActivity.this);
    }


}
