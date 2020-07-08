package com.example.bozhilun.android.h9.settingactivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class H9ReminderActivity
        extends WatchBaseActivity
        implements CompoundButton.OnCheckedChangeListener {

    private static final String BOZLUN_PACKNAME_EN = "com.bozlun.bozhilun.android";
    private static final String TAG = "H9ReminderActivity";
    @BindView(R.id.newShearchImg)
    ImageView newShearchImg;
    @BindView(R.id.newSearchTitleLeft)
    FrameLayout newSearchTitleLeft;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;
    @BindView(R.id.watch_msgOpenNitBtn)
    LinearLayout watchMsgOpenNitBtn;
    @BindView(R.id.switch_Skype)
    ToggleButton switchSkype;
    @BindView(R.id.switch_WhatsApp)
    ToggleButton switchWhatsApp;
    @BindView(R.id.switch_Facebook)
    ToggleButton switchFacebook;
    @BindView(R.id.switch_LinkendIn)
    ToggleButton switchLinkendIn;
    @BindView(R.id.switch_Twitter)
    ToggleButton switchTwitter;
    @BindView(R.id.switch_Viber)
    ToggleButton switchViber;
    @BindView(R.id.switch_LINE)
    ToggleButton switchLINE;
    @BindView(R.id.switch_WeChat)
    ToggleButton switchWeChat;
    @BindView(R.id.switch_QQ)
    ToggleButton switchQQ;
    @BindView(R.id.switch_Msg)
    ToggleButton switchMsg;
    @BindView(R.id.switch_Phone)
    ToggleButton switchPhone;
    @BindView(R.id.device_img3)
    ImageView deviceImg3;
    @BindView(R.id.watch_watchmsg_tv)
    TextView watchWatchmsgTv;
    @BindView(R.id.device_img)
    ImageView deviceImg;
    @BindView(R.id.watch_msgOpenAccessBtn)
    RelativeLayout watch_msgOpenAccessBtn;
    @BindView(R.id.text_linkendln)
    TextView textLinkendln;
    @BindView(R.id.text_viber)
    TextView textViber;
    @BindView(R.id.line_linkendln)
    LinearLayout lineLinkendln;
    @BindView(R.id.view_linkendln_line)
    View viewLinkendlnLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_reminder);
        ButterKnife.bind(this);

        newSearchTitleTv.setText(getResources().getString(R.string.string_application_reminding));
        watch_msgOpenAccessBtn.setVisibility(View.GONE);
        switchLinkendIn.setVisibility(View.GONE);
        lineLinkendln.setVisibility(View.GONE);
        viewLinkendlnLine.setVisibility(View.GONE);
        initSwitch();
    }


    @Override
    protected void onResume() {
        super.onResume();

                /**
         * 第一步获取开关
         */
        AppsBluetoothManager.getInstance(H9ReminderActivity.this).sendCommand(new SwitchSetting(commandResultCallback));
    }


    BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof SwitchSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    Log.d(TAG, "-----读取设备开关成功:"
                            + "防丢开关:" + GlobalVarManager.getInstance().isAntiLostSwitch()
                            + "\n 自动同步开关:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
                            + "\n 睡眠开关:" + GlobalVarManager.getInstance().isSleepSwitch()
                            + "\n 自动睡眠监测开关:" + GlobalVarManager.getInstance().isSleepStateSwitch()
                            + "\n 来电提醒开关:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
                            + "\n 未接来电提醒开关:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
                            + "\n 短信提醒开关:" + GlobalVarManager.getInstance().isSmsSwitch()
                            + "\n 社交提醒开关:" + GlobalVarManager.getInstance().isSocialSwitch()
                            + "\n 邮件提醒开关:" + GlobalVarManager.getInstance().isMailSwitch()
                            + "\n 日历开关:" + GlobalVarManager.getInstance().isCalendarSwitch()
                            + "\n 久坐提醒开关:" + GlobalVarManager.getInstance().isSedentarySwitch()
                            + "\n 超低功耗功能开关:" + GlobalVarManager.getInstance().isLowPowerSwitch()
                            + "\n 二次提醒开关:" + GlobalVarManager.getInstance().isSecondRemindSwitch()
                            + "\n 运动心率模式开关:" + GlobalVarManager.getInstance().isSportHRSwitch()
                            + "\n FACEBOOK开关:" + GlobalVarManager.getInstance().isFacebookSwitch()
                            + "\n TWITTER开关:" + GlobalVarManager.getInstance().isTwitterSwitch()
                            + "\n INSTAGRAM开关:" + GlobalVarManager.getInstance().isInstagamSwitch()
                            + "\n QQ开关:" + GlobalVarManager.getInstance().isQqSwitch()
                            + "\n WECHAT开关:" + GlobalVarManager.getInstance().isWechatSwitch()
                            + "\n WHATSAPP开关:" + GlobalVarManager.getInstance().isWhatsappSwitch()
                            + "\n LINE开关:" + GlobalVarManager.getInstance().isLineSwitch()
                            + "\n 心率自测:" + GlobalVarManager.getInstance().isAutoHeart()
                            + "\n 心率预警:" + GlobalVarManager.getInstance().isHeartAlarm());

                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_ANTI_SYNSC", GlobalVarManager.getInstance().isAutoSyncSwitch());//同步
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_ANTI_LOST", GlobalVarManager.getInstance().isAntiLostSwitch());//防止丢失
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_CALENDAR", GlobalVarManager.getInstance().isCalendarSwitch());//日历
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SEDENTARY", GlobalVarManager.getInstance().isSedentarySwitch());//久坐提醒

                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SKYPE", GlobalVarManager.getInstance().isSkypeSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_WHATSAPP", GlobalVarManager.getInstance().isWhatsappSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_FACEBOOK", GlobalVarManager.getInstance().isFacebookSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_TWTTER", GlobalVarManager.getInstance().isTwitterSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_INSTAGRAM", GlobalVarManager.getInstance().isInstagamSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_LINE", GlobalVarManager.getInstance().isLineSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_WECTH", GlobalVarManager.getInstance().isWechatSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_QQ", GlobalVarManager.getInstance().isQqSwitch());
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_SMS", GlobalVarManager.getInstance().isSmsSwitch());//短信
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_INCOME_CALL", GlobalVarManager.getInstance().isIncomePhoneSwitch());//来电


                    getSwitchState();
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof SwitchSetting) {
                getSwitchState();
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {

                }else if (baseCommand.getAction() == CommandConstant.ACTION_SET){
                    closeLoadingDialog();
                }
            }
        }
    };



    private void getSwitchState() {
        boolean w30sswitch_skype = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_SKYPE", false);
        boolean w30sswitch_whatsApp = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_WHATSAPP", false);
        boolean w30sswitch_facebook = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_FACEBOOK", false);
//        boolean w30sswitch_linkendIn = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "w30sswitch_LinkendIn", false);
        boolean w30sswitch_twitter = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_TWTTER", false);
        //instagram
        boolean w30sswitch_viber = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_INSTAGRAM", false);
        boolean w30sswitch_line = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_LINE", false);
        boolean w30sswitch_weChat = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_WECTH", false);
        boolean w30sswitch_qq = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_QQ", false);
        boolean w30sswitch_msg = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_SMS", false);
        boolean w30sswitch_Phone = (boolean) SharedPreferencesUtils.getParam(H9ReminderActivity.this, "H9_INCOME_CALL", true);
        switchSkype.setChecked(w30sswitch_skype);
        switchWhatsApp.setChecked(w30sswitch_whatsApp);
        switchFacebook.setChecked(w30sswitch_facebook);
//        switchLinkendIn.setChecked(w30sswitch_linkendIn);
        switchTwitter.setChecked(w30sswitch_twitter);
        switchViber.setChecked(w30sswitch_viber);
        switchLINE.setChecked(w30sswitch_line);
        switchWeChat.setChecked(w30sswitch_weChat);
        switchQQ.setChecked(w30sswitch_qq);
        switchMsg.setChecked(w30sswitch_msg);
        switchPhone.setChecked(w30sswitch_Phone);
    }

    private void initSwitch() {
        switchSkype.setOnCheckedChangeListener(this);
        switchWhatsApp.setOnCheckedChangeListener(this);
        switchFacebook.setOnCheckedChangeListener(this);
        switchLinkendIn.setOnCheckedChangeListener(this);
        switchTwitter.setOnCheckedChangeListener(this);
        switchViber.setOnCheckedChangeListener(this);
        switchLINE.setOnCheckedChangeListener(this);
        switchWeChat.setOnCheckedChangeListener(this);
        switchQQ.setOnCheckedChangeListener(this);
        switchMsg.setOnCheckedChangeListener(this);
        switchPhone.setOnCheckedChangeListener(this);
    }


    @OnClick({R.id.watch_msgOpenNitBtn, R.id.newSearchTitleLeft,
            R.id.watch_msgOpenAccessBtn, R.id.newSearchRightImg1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.watch_msgOpenNitBtn:
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 101);
                break;
            case R.id.watch_msgOpenAccessBtn:
                Intent ints = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(ints, 102);
                break;
            case R.id.newSearchRightImg1:   //进入权限界面
                String appPackName = getPackageName();
                String contentTx = "Please open the required permissions";
                Log.e("包名", "------appName=" + appPackName);
                if (appPackName != null && appPackName.equals(BOZLUN_PACKNAME_EN)) {
                    contentTx = "Please open the required permissions";
                } else {
                    contentTx = "请打开所需权限";
                }
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(contentTx)
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).show();

                break;
        }
    }


    private byte myByte = 0;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("--------点击事件--", buttonView.isPressed() + "");
        if (buttonView.isPressed()) {
            showLoadingDialog(getResources().getString(R.string.dlog));
            AppsBluetoothManager.getInstance(MyApp.getContext())
                    .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x07, (byte) 0x01));//社交
            if (isChecked) {
                myByte = 1;
            } else {
                myByte = 0;
            }
            switch (buttonView.getId()) {
                case R.id.switch_Skype:
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x15, myByte));//0x15    SKYPE开关
                    break;
                case R.id.switch_WhatsApp:
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x13, myByte));//0x13    WHATSAPP开关
                    break;
                case R.id.switch_Facebook:
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0e, (byte) 1)); // 0x0E    FACEBOOK开关
                    break;
                case R.id.switch_LinkendIn:
                    break;
                case R.id.switch_Twitter:
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0f, (byte) 1));// 0x0F    TWITTER开关
                    break;
                case R.id.switch_Viber:
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x10, (byte) 1));// 0x10    INSTAGRAM开关
                    break;
                case R.id.switch_LINE:
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x14, (byte) 1));//0x14    LINE开关
                    break;
                case R.id.switch_WeChat:    //微信
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x12, (byte) 1));//0x12    WECHAT开关
                    break;
                case R.id.switch_QQ:    //QQ
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x11, (byte) 1));//0x11    QQ开关
                    break;
                case R.id.switch_Msg:   //短信
                    if (!AndPermission.hasPermissions(H9ReminderActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS})) {
                        AndPermission.with(H9ReminderActivity.this)
                                .runtime()
                                .permission(Permission.READ_SMS, Permission.READ_SMS)
                                .start();
                    }
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x06, (byte) 1));// 0x06    短信提醒开关
                    break;
                case R.id.switch_Phone:     //来电
                    if (!AndPermission.hasPermissions(H9ReminderActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})) {
                        AndPermission.with(H9ReminderActivity.this)
                                .runtime()
                                .permission(Permission.CALL_PHONE, Permission.READ_PHONE_STATE, Permission.READ_CONTACTS)
                                .rationale(new Rationale<List<String>>() {
                                    @Override
                                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                        executor.execute();
                                    }
                                })
                                .start();
                    }
                    AppsBluetoothManager.getInstance(H9ReminderActivity.this).
                            sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x04, (byte) 1)); // 0x04    来电提醒开关
                    break;
            }
        }

    }
}
