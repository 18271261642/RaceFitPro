package com.example.bozhilun.android.h9.settingactivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18isystemic.B18ITargetSettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.ShockActivity;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.h9.utils.H9ContentUtils;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.sdk.bluetooth.bean.DeviceTimeFormat;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.Language;
import com.sdk.bluetooth.protocol.command.device.RestoreFactory;
import com.sdk.bluetooth.protocol.command.device.TimeSurfaceSetting;
import com.sdk.bluetooth.protocol.command.device.Unit;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class H9SettingsActivity extends WatchBaseActivity {

    private static final String TAG = "H9SettingsActivity";
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.rela_layout_title)
    Toolbar relaLayoutTitle;
    @BindView(R.id.radioGroup_time)
    RadioGroup radioGroupTime;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mi)
    RadioButton radioMi;
    @BindView(R.id.radioGroup_unti)
    RadioGroup radioGroupUnti;
    @BindView(R.id.radioGroup_language)
    RadioGroup radioGroupLanguage;
    @BindView(R.id.set_notifi_app)
    LinearLayout setNotifiApp;
    @BindView(R.id.targetSetting)
    LinearLayout targetSetting;
    @BindView(R.id.set_more_shock)
    LinearLayout setMoreShock;
    @BindView(R.id.set_clock)
    LinearLayout setClock;
    @BindView(R.id.set_findeDevice)
    LinearLayout setFindeDevice;
    @BindView(R.id.set_factory)
    LinearLayout setFactory;
    @BindView(R.id.set_unbind)
    LinearLayout setUnbind;
    @BindView(R.id.wx_sport)
    LinearLayout wxSport;
    @BindView(R.id.h_scroll)
    HorizontalScrollView mHorizontalScrollView;
    //久坐提醒
//    @BindView(R.id.switch_bright)
//    ToggleButton switchBright;
    @BindView(R.id.radio_T1)
    RadioButton radioT1;
    @BindView(R.id.radio_T2)
    RadioButton radioT2;
    @BindView(R.id.radio_T3)
    RadioButton radioT3;
    @BindView(R.id.radio_T4)
    RadioButton radioT4;
    @BindView(R.id.radio_T5)
    RadioButton radioT5;
    @BindView(R.id.radio_T6)
    RadioButton radioT6;
    @BindView(R.id.radio_T7)
    RadioButton radioT7;
    @BindView(R.id.radio_T8)
    RadioButton radioT8;
    @BindView(R.id.radio_T9)
    RadioButton radioT9;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_settings_activity);
        ButterKnife.bind(this);


        initView();
    }


    //初始化偏移量
    private int offset = 0;
    private int scrollViewWidth = 0;

    private void initView() {

        barTitles.setText(getResources().getString(R.string.menu_settings));
        showLoadingDialog(getResources().getString(R.string.dlog));

        radioGroupUnti.setOnCheckedChangeListener(new RadioCheckeListenter());//公英制单位
        radioGroupLanguage.setOnCheckedChangeListener(new RadioCheckeListenter());//公英制单位
        radioGroupTime.setOnCheckedChangeListener(new RadioCheckeListenter());//时间格式  表盘的九中显示格式
//        switchBright.setOnCheckedChangeListener(new CheckedListenter());//久坐提醒
        //久坐提醒开关
//        boolean h9_sedentary = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_SEDENTARY", false);//久坐提醒
//        switchBright.setChecked(h9_sedentary);

//
//        //公英制单位
//        int h9_utit = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 1);//公英制    公制0   英制1
//        if (h9_utit == 0) {
//            radioGroupUnti.check(R.id.radio_km);
//        } else {
//            radioGroupUnti.check(R.id.radio_mi);
//        }
//
//        //获取语言
//        // 语言类型 0x00：英文   0x01：中文
//        int h9_lanGuage = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_LanGuage", 0x00);//：中文
//        if (h9_lanGuage == 0) {
//            radioGroupLanguage.check(R.id.radio_en);
//        } else {
//            radioGroupLanguage.check(R.id.radio_zh);
//        }
//        setTimeType();

        //获取时间格式-------在线获取

//        /**
//         * 第一步   获取时间格式
//         */
//        AppsBluetoothManager.getInstance(MyApp.getInstance())
//                .sendCommand(new TimeSurfaceSetting(commandResultCallback));


        /**
         * 时间格式读本地的--------对用户设置不太明显的操作（可以从本地存储配置）
         */
        setTimeType();
        /**
         * 第二步   读取公英制（公英制对用户是很明显的操作，所以实时读取设备的）
         */
        AppsBluetoothManager.getInstance(MyApp.getInstance())
                .sendCommand(new Unit(commandResultCallback));//读取设备公英制
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getInstance()).clearCommandBlockingDeque();
    }

    void scr(int id) {
        RadioButton radioT = findViewById(id);
        //获取位置下标
        scrollViewWidth = mHorizontalScrollView.getWidth();
        if ((scrollViewWidth + offset) < radioT.getRight()) {//需要向右移动
            mHorizontalScrollView.smoothScrollBy(radioT.getRight() - (scrollViewWidth + offset), 0);
            offset += radioT.getRight() - (scrollViewWidth + offset);
        }

        if (offset > radioT.getLeft()) {//需要向左移动
            mHorizontalScrollView.smoothScrollBy(radioT.getLeft() - offset, 0);
            offset += radioT.getLeft() - offset;
        }
        mHorizontalScrollView.setFocusable(true);//可以在xml中配置
        mHorizontalScrollView.setFocusableInTouchMode(true);//可以在xml中配置
        mHorizontalScrollView.requestFocus();
    }

    void setTimeType() {
        //获取时间格式
        String timeFormat = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_TimeFormat", "T1");
        switch (timeFormat) {
            case "T1":
                radioGroupTime.check(R.id.radio_T1);
                scr(R.id.radio_T1);
                break;
            case "T2":
                radioGroupTime.check(R.id.radio_T2);
                scr(R.id.radio_T2);
                break;
            case "T3":
                radioGroupTime.check(R.id.radio_T3);
                scr(R.id.radio_T3);
                break;
            case "T4":
                radioGroupTime.check(R.id.radio_T4);
                scr(R.id.radio_T4);
                break;
            case "T5":
                radioGroupTime.check(R.id.radio_T5);
                scr(R.id.radio_T5);
                break;
            case "T6":
                radioGroupTime.check(R.id.radio_T6);
                scr(R.id.radio_T6);
                break;
            case "T7":
                radioGroupTime.check(R.id.radio_T7);
                scr(R.id.radio_T7);
                break;
            case "T8":
                radioGroupTime.check(R.id.radio_T8);
                scr(R.id.radio_T8);
                break;
            case "T9":
                radioGroupTime.check(R.id.radio_T9);
                scr(R.id.radio_T9);
                break;
        }
    }


    @OnClick({R.id.image_back, R.id.set_devices_time,
            R.id.set_notifi_app, R.id.targetSetting, R.id.set_more_shock,
            R.id.set_clock, R.id.set_findeDevice, R.id.set_factory, R.id.set_unbind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.set_devices_time://快速校针
                startActivity(new Intent(H9SettingsActivity.this, CorrectionTimeActivity.class));
                break;
            case R.id.set_notifi_app://消息提醒
                startActivity(new Intent(H9SettingsActivity.this, H9ReminderActivity.class));
                break;
            case R.id.targetSetting://目标设置
                startActivity(new Intent(H9SettingsActivity.this, B18ITargetSettingActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.set_more_shock://健康提醒----->>>开关设置
                startActivity(new Intent(H9SettingsActivity.this, H9SwitchActivity.class));
                break;
            case R.id.set_clock://闹钟设置
//                startActivity(new Intent(H9SettingsActivity.this,
//                        AlarmClockRemindActivity.class).putExtra("is18i", "H9"));
                startActivity(new Intent(H9SettingsActivity.this,
                        H9AlarmClockActivity.class));
                break;
            case R.id.set_findeDevice://查找手表---改为震动强度
                startActivity(new Intent(H9SettingsActivity.this, ShockActivity.class));
                break;
            case R.id.set_factory://恢复出厂
                new CommomDialog(H9SettingsActivity.this, R.style.dialog,
                        getResources().getString(R.string.string_alarm_resume_factory),
                        new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_DEVICES_SYS", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_TIME", "2018-12-25 12:20");//第一次同步电池电量时间
                                    SharedPreferencesUtils.saveObject(MyApp.getInstance(), "H9_BATTERY", 100);//电池电量值
                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                                            .sendCommand(new RestoreFactory(commandResultCallback));
                                }
                                dialog.dismiss();
                            }
                        }).setTitle(getResources().getString(R.string.prompt)).show();

                break;
            case R.id.set_unbind://解除绑定
                new CommomDialog(H9SettingsActivity.this, R.style.dialog,
                        getResources().getString(R.string.confirm_unbind_strap),
                        new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {

                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_DEVICES_SYS", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_TIME", "2018-12-25 12:20");//第一次同步电池电量时间
                                    SharedPreferencesUtils.saveObject(MyApp.getInstance(), "H9_BATTERY", 100);//电池电量值
                                    String defaultMac = BluetoothConfig.getDefaultMac(H9SettingsActivity.this);
                                    String devicesMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                                            .doUnbindDevice(!WatchUtils.isEmpty(defaultMac) ? devicesMac : defaultMac);
                                    H9ContentUtils.getContent(H9SettingsActivity.this).removeListenter();
                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this).clearBluetoothManagerDeviceConnectListeners();
                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this).clearBluetoothManagerScanListeners();
                                    BluetoothConfig.setDefaultMac(H9SettingsActivity.this, "");
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLENAME, "");
                                    MyCommandManager.DEVICENAME = null;
                                    startActivity(NewSearchActivity.class);

                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this).killCommandRunnable();
                                    AppsBluetoothManager.getInstance(H9SettingsActivity.this).startDiscovery();
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        }).setTitle(getResources().getString(R.string.prompt)).show();
                break;
        }
    }


    private class RadioCheckeListenter implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            View checkView = findViewById(checkedId);
            if (!checkView.isPressed()) {
                return;
            }
            showLoadingDialog(getResources().getString(R.string.dlog));
            Log.d("------点击状态--", radioGroup.isPressed() + "-----" + radioGroup.isClickable() + "====" + radioGroup.isActivated());
            switch (radioGroup.getId()) {
                case R.id.radioGroup_language:

                    if (checkedId == R.id.radio_zh) {
                        Log.d("--------", "zh");
                        AppsBluetoothManager.getInstance(MyApp.getInstance())
                                .sendCommand(new Language(commandResultCallback, (byte) 0x01));
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_LanGuage", 1);//：中文
                    } else if (checkedId == R.id.radio_en) {
                        Log.d("--------", "en");
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_LanGuage", 0);//：
                        AppsBluetoothManager.getInstance(MyApp.getInstance())
                                .sendCommand(new Language(commandResultCallback, (byte) 0x00));
                    }
                    break;
                case R.id.radioGroup_unti:
                    if (checkedId == R.id.radio_km) {
                        Log.d("--------", "km");
                        // 0位公制 1为英制
                        AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                                .sendCommand(new Unit(commandResultCallback, (byte) 0x00));
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_UTIT", 0);//公制
                    } else if (checkedId == R.id.radio_mi) {
                        Log.d("--------", "mi");
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_UTIT", 1);//英制
                        AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                                .sendCommand(new Unit(commandResultCallback, (byte) 0x01));
                    }
                    break;
                case R.id.radioGroup_time:
                    // 日期格式   /123468,10,11,13,18    5   7   9   12    14    15    16   17
                    // (0x00:不显示日期   0x01:yy/mm/dd   0x02:dd/mm/yy   0x03:mm/dd/yy
                    // 0x04:星期几/mm/dd  0x05:mm/dd  0x06:dd/mm  0x07星期几dd/mm/yy  0x08： 星期几dd/mm)
                    // 电池显示(0x00:不显示电池   0x01:显示电池)
                    DeviceTimeFormat deviceTimeFormat = new DeviceTimeFormat();
                    if (checkedId == R.id.radio_T1) {
                        deviceTimeFormat.setDateFormat((byte) 0x00);
                        deviceTimeFormat.setBatteryFormat((byte) 0x01);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T1");
                    } else if (checkedId == R.id.radio_T2) {
                        deviceTimeFormat.setDateFormat((byte) 0x04);
                        deviceTimeFormat.setBatteryFormat((byte) 0x01);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T2");
                    } else if (checkedId == R.id.radio_T3) {
                        deviceTimeFormat.setDateFormat((byte) 0x06);
                        deviceTimeFormat.setBatteryFormat((byte) 0x01);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T3");
                    } else if (checkedId == R.id.radio_T4) {
                        deviceTimeFormat.setDateFormat((byte) 0x08);
                        deviceTimeFormat.setBatteryFormat((byte) 0x01);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T4");
                    } else if (checkedId == R.id.radio_T5) {
                        deviceTimeFormat.setDateFormat((byte) 0x02);
                        deviceTimeFormat.setBatteryFormat((byte) 0x00);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T5");
                    } else if (checkedId == R.id.radio_T6) {
                        deviceTimeFormat.setDateFormat((byte) 0x04);
                        deviceTimeFormat.setBatteryFormat((byte) 0x00);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T6");
                    } else if (checkedId == R.id.radio_T7) {
                        deviceTimeFormat.setDateFormat((byte) 0x05);
                        deviceTimeFormat.setBatteryFormat((byte) 0x00);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T7");
                    } else if (checkedId == R.id.radio_T8) {
                        deviceTimeFormat.setDateFormat((byte) 0x06);
                        deviceTimeFormat.setBatteryFormat((byte) 0x00);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T8");
                    } else if (checkedId == R.id.radio_T9) {
                        deviceTimeFormat.setDateFormat((byte) 0x07);
                        deviceTimeFormat.setBatteryFormat((byte) 0x00);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T9");
                    }
                    AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                            .sendCommand(new TimeSurfaceSetting(commandResultCallback, deviceTimeFormat));
                    break;
            }

        }
    }


    BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof TimeSurfaceSetting) {//时间格式
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取时间格式（读取
                    Log.d(TAG, "-----读取时间格式成功:" + GlobalVarManager.getInstance().getUnit());

                    byte dateFormat = GlobalVarManager.getInstance().getDeviceTimeFormat().getDateFormat();
                    byte batteryFormat = GlobalVarManager.getInstance().getDeviceTimeFormat().getBatteryFormat();
                    if (dateFormat == 0x00 && batteryFormat == 0x01) {
                        radioGroupTime.check(R.id.radio_T1);
                        scr(R.id.radio_T1);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T1");
                    } else if (dateFormat == 0x04 && batteryFormat == 0x01) {
                        radioGroupTime.check(R.id.radio_T2);
                        scr(R.id.radio_T2);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T2");
                    } else if (dateFormat == 0x06 && batteryFormat == 0x01) {
                        radioGroupTime.check(R.id.radio_T3);
                        scr(R.id.radio_T3);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T3");
                    } else if (dateFormat == 0x08 && batteryFormat == 0x01) {
                        radioGroupTime.check(R.id.radio_T4);
                        scr(R.id.radio_T4);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T4");
                    } else if (dateFormat == 0x02 && batteryFormat == 0x00) {
                        radioGroupTime.check(R.id.radio_T5);
                        scr(R.id.radio_T5);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T5");
                    } else if (dateFormat == 0x04 && batteryFormat == 0x00) {
                        radioGroupTime.check(R.id.radio_T6);
                        scr(R.id.radio_T6);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T6");
                    } else if (dateFormat == 0x05 && batteryFormat == 0x00) {
                        radioGroupTime.check(R.id.radio_T7);
                        scr(R.id.radio_T7);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T7");
                    } else if (dateFormat == 0x06 && batteryFormat == 0x00) {
                        radioGroupTime.check(R.id.radio_T8);
                        scr(R.id.radio_T8);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T8");
                    } else if (dateFormat == 0x07 && batteryFormat == 0x00) {
                        radioGroupTime.check(R.id.radio_T9);
                        scr(R.id.radio_T9);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TimeFormat", "T9");
                    }

                    /**
                     * 第一步   读取公英制
                     */
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new Unit(commandResultCallback));//读取设备公英制
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    closeLoadingDialog();
                }
            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    byte unit = GlobalVarManager.getInstance().getUnit();
                    Log.d(TAG, "-----获取公英制成功:" + (unit == 1 ? "英制" : "公制"));
                    // 0位公制 1为英制
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_UTIT", (int) unit);//公英制
                    //公英制单位
                    if (unit == 0) {
                        radioGroupUnti.check(R.id.radio_km);
                    } else {
                        radioGroupUnti.check(R.id.radio_mi);
                    }

                    /**
                     * 第一步   读取语言
                     */
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new Language(commandResultCallback));//读取设备语言
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    closeLoadingDialog();
                }
            } else if (baseCommand instanceof Language) {
                closeLoadingDialog();
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    int language = GlobalVarManager.getInstance().getLanguage();
                    Log.d(TAG, "-----获取语言成功:" + (language == 1 ? "中文" : "英文"));
                    // 语言类型 0x00：英文   0x01：中文
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_LanGuage", language);//：中文

                    //获取语言
                    // 语言类型 0x00：英文   0x01：中文
                    if (language == 0) {
                        radioGroupLanguage.check(R.id.radio_en);
                    } else {
                        radioGroupLanguage.check(R.id.radio_zh);
                    }
                }
            } else if (baseCommand instanceof RestoreFactory) {//恢复出厂设置
                closeLoadingDialog();

                String defaultMac = BluetoothConfig.getDefaultMac(H9SettingsActivity.this);
                String devicesMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                AppsBluetoothManager.getInstance(H9SettingsActivity.this)
                        .doUnbindDevice(!WatchUtils.isEmpty(defaultMac) ? devicesMac : defaultMac);
                BluetoothConfig.setDefaultMac(H9SettingsActivity.this, "");
                SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLENAME, "");
                MyCommandManager.DEVICENAME = null;
                startActivity(NewSearchActivity.class);
                finish();
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            if (baseCommand instanceof TimeSurfaceSetting) {//时间格式
                setTimeType();
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取时间格式（读取
                    //读取公英制
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new Unit(commandResultCallback));//读取设备公英制
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置时间格式（写入
                    closeLoadingDialog();
                }
            } else if (baseCommand instanceof Unit) {//公英制/设置或者获取
                //公英制单位
                int h9_utit = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 1);//公英制    公制0   英制1
                if (h9_utit == 0) {
                    radioGroupUnti.check(R.id.radio_km);
                } else {
                    radioGroupUnti.check(R.id.radio_mi);
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    //读取语言
                    AppsBluetoothManager.getInstance(MyApp.getInstance())
                            .sendCommand(new Language(commandResultCallback));//读取设备语言
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    closeLoadingDialog();
                }
            } else if (baseCommand instanceof Language) {
                closeLoadingDialog();
                // 语言类型 0x00：英文   0x01：中文
                int h9_lanGuage = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_LanGuage", 0x00);//：中文
                if (h9_lanGuage == 0) {
                    radioGroupLanguage.check(R.id.radio_en);
                } else {
                    radioGroupLanguage.check(R.id.radio_zh);
                }
            } else if (baseCommand instanceof RestoreFactory) {//恢复出厂设置
                Log.d(TAG, "-----设备恢复出厂设置成功:断开去搜索界面");
                closeLoadingDialog();
                ToastUtil.showShort(H9SettingsActivity.this, "恢复出厂失败");
            }
        }
    };

//    /**
//     * 久坐提醒开关
//     */
//    private class CheckedListenter implements CompoundButton.OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//            if (compoundButton.isPressed()) {
//                switch (compoundButton.getId()) {
//                    case R.id.switch_bright:
//                        if (isChecked) {
//                            /**
//                             * 抬手亮屏开关
//                             *
//                             * @param value 1=开，0=关
//                             */
//                            AppsBluetoothManager.getInstance(MyApp.getContext())
//                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0A, (byte) 0x01));
//                        } else {
//                            AppsBluetoothManager.getInstance(MyApp.getContext())
//                                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0A, (byte) 0x00));
//                        }
//                        break;
//                }
//            }
//
//        }
//    }
}
