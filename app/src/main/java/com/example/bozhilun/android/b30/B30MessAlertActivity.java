package com.example.bozhilun.android.b30;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.android.internal.telephony.ITelephony;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.MessageHelpActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30消息提醒页面
 */
public class B30MessAlertActivity extends WatchBaseActivity {

    private static final String TAG = "B30MessAlertActivity";

    @BindView(R.id.b30SkypeTogg)
    ToggleButton b30SkypeTogg;
    @BindView(R.id.b30WhatsAppTogg)
    ToggleButton b30WhatsAppTogg;
    @BindView(R.id.b30FacebookTogg)
    ToggleButton b30FacebookTogg;
    @BindView(R.id.b30LinkedTogg)
    ToggleButton b30LinkedTogg;
    @BindView(R.id.b30TwitterTogg)
    ToggleButton b30TwitterTogg;
    @BindView(R.id.b30LineTogg)
    ToggleButton b30LineTogg;
    @BindView(R.id.b30WechatTogg)
    ToggleButton b30WechatTogg;
    @BindView(R.id.b30QQTogg)
    ToggleButton b30QQTogg;
    @BindView(R.id.b30MessageTogg)
    ToggleButton b30MessageTogg;
    @BindView(R.id.b30PhoneTogg)
    ToggleButton b30PhoneTogg;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;


    @BindView(R.id.b30InstagramTogg)
    ToggleButton b30InstagramTogg;
    @BindView(R.id.b30GmailTogg)
    ToggleButton b30GmailTogg;
    @BindView(R.id.b30SnapchartTogg)
    ToggleButton b30SnapchartTogg;
    @BindView(R.id.b30OhterTogg)
    ToggleButton b30OhterTogg;

    @BindView(R.id.google_gmail)
    LinearLayout google_gmail;
    @BindView(R.id.google_gmail_line)
    View google_gmail_line;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_msgalert);
        ButterKnife.bind(this);

        initViews();

        //申请权限
        requestPermiss();

        getPhoneStatus();
        //读取社交消息设置
        readSocialMsg();

        //getDoNotDisturb();

    }


    private void getPhoneStatus() {
        AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringMode = audioManager.getRingerMode();
            //audioManager.getStreamVolume()
            Log.e(TAG, "-------手环模式=" + ringMode);
            switch (ringMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //普通模式
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //振动模式
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //静音模式
                    break;
            }

        }
    }


    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            MyApp.getInstance().getApplicationContext().startActivity(intent);
        }

    }


    //申请电话权限
    private void requestPermiss() {
        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG})) {
            AndPermission.with(B30MessAlertActivity.this)
                    .runtime()
                    .permission(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)
//                            ,Manifest.permission.WRITE_CALL_LOG)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                            executor.execute();
                        }
                    })
                    .start();
        }

        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS})) {
            AndPermission.with(B30MessAlertActivity.this)
                    .runtime()
                    .permission(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG)//,Manifest.permission.WRITE_CALL_LOG)
                    .start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 跳转到通知使用权
     *
     * @param context
     * @return
     */
    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void readSocialMsg() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                    Log.e(TAG, "----读取=" + functionSocailMsgData.toString() + functionSocailMsgData.getPhone() + EFunctionStatus.SUPPORT_OPEN);

                    if (functionSocailMsgData.getSkype() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SkypeTogg.setChecked(true);
//                        isSkype = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, true);
                    } else {
                        b30SkypeTogg.setChecked(false);
//                        isSkype = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, false);
                    }

                    if (functionSocailMsgData.getWhats() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WhatsAppTogg.setChecked(true);
//                        isWhats = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, true);
                    } else {
                        b30WhatsAppTogg.setChecked(false);
//                        isWhats = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, false);
                    }

                    if (functionSocailMsgData.getFacebook() == EFunctionStatus.SUPPORT_OPEN) {
                        b30FacebookTogg.setChecked(true);
//                        isFaceBook = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, true);
                    } else {
                        b30FacebookTogg.setChecked(false);
//                        isFaceBook = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, false);
                    }
                    if (functionSocailMsgData.getLinkin() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LinkedTogg.setChecked(true);
//                        isLinkin = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, true);
                    } else {
                        b30LinkedTogg.setChecked(false);
//                        isLinkin = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, false);
                    }
                    if (functionSocailMsgData.getTwitter() == EFunctionStatus.SUPPORT_OPEN) {
                        b30TwitterTogg.setChecked(true);
//                        isTwitter = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, true);
                    } else {
                        b30TwitterTogg.setChecked(false);
//                        isTwitter = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, false);
                    }
                    //viber


                    if (functionSocailMsgData.getLine() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LineTogg.setChecked(true);
//                        isLine = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, true);
                    } else {
                        b30LineTogg.setChecked(false);
//                        isLine = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, false);
                    }
                    if (functionSocailMsgData.getWechat() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WechatTogg.setChecked(true);
//                        isWeChat = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, true);
                    } else {
                        b30WechatTogg.setChecked(false);
//                        isWeChat = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, false);
                    }
                    if (functionSocailMsgData.getQq() == EFunctionStatus.SUPPORT_OPEN) {
                        b30QQTogg.setChecked(true);
//                        isQQ = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, true);
                    } else {
                        b30QQTogg.setChecked(false);
//                        isQQ = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, false);
                    }
                    if (functionSocailMsgData.getMsg() == EFunctionStatus.SUPPORT_OPEN) {
                        b30MessageTogg.setChecked(true);
//                        isMsg = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, true);
                    } else {
                        b30MessageTogg.setChecked(false);
//                        isMsg = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, false);
                    }
                    if (functionSocailMsgData.getPhone() == EFunctionStatus.SUPPORT_OPEN) {
                        b30PhoneTogg.setChecked(true);
//                        isOpenPhone = true;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, true);
                    } else {
                        b30PhoneTogg.setChecked(false);
//                        isOpenPhone = false;
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, false);
                    }

                    if (functionSocailMsgData.getInstagram() == EFunctionStatus.SUPPORT_OPEN) {
                        b30InstagramTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, true);
                    } else {
                        b30InstagramTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, false);
                    }

                    if (functionSocailMsgData.getGmail() == EFunctionStatus.SUPPORT_OPEN) {
                        b30GmailTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, true);
                    } else if (functionSocailMsgData.getGmail() == EFunctionStatus.UNSUPPORT) {
                        google_gmail.setVisibility(View.GONE);
                        google_gmail_line.setVisibility(View.GONE);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, false);
                    } else {
                        b30GmailTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, false);
                    }

                    if (functionSocailMsgData.getSnapchat() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SnapchartTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, true);
                    } else {
                        b30SnapchartTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, false);
                    }

                    if (functionSocailMsgData.getOther() == EFunctionStatus.SUPPORT_OPEN) {
                        b30OhterTogg.setChecked(true);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, true);
                    } else {
                        b30OhterTogg.setChecked(false);
                        SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, false);
                    }
                }
            });
        }
    }

    private void initViews() {
        newSearchTitleTv.setText(getResources().getString(R.string.string_ocial_message));//社交小心哦
        newSearchRightImg1.setVisibility(View.VISIBLE);
        b30SkypeTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WhatsAppTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30FacebookTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LinkedTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30TwitterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LineTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WechatTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30QQTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30MessageTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30PhoneTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());


        b30InstagramTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30GmailTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30SnapchartTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30OhterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1, R.id.msgOpenNitBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.newSearchRightImg1:
                startActivity(MessageHelpActivity.class);
                break;
            case R.id.msgOpenNitBtn:    //打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 1001);
                break;
        }
    }

    //监听
    private class ToggCheckChanageListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            switch (buttonView.getId()) {
                case R.id.b30SkypeTogg: //skype
//                    isSkype = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30WhatsAppTogg:  //whatspp
//                    isWhats = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30FacebookTogg:  //facebook
//                    isFaceBook = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30LinkedTogg:    //linked
//                    isLinkin = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30TwitterTogg:   //twitter
//                    isTwitter = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30LineTogg:  //line
//                    isLine = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30WechatTogg:    //wechat
//                    isWeChat = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30QQTogg:    //qq
//                    isQQ = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30MessageTogg:   //msg
                    requestPermiss();
//                    isMsg = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30PhoneTogg: //phone
                    requestPermiss();
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, isChecked);
//                    isOpenPhone = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISCallPhone, isChecked);
//                    MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(new IDeviceControlPhone() {
//                        @Override
//                        public void nextMusic() {
//
//                        }
//
//                        @Override
//                        public void previousMusic() {
//
//                        }
//
//                        @Override
//                        public void pauseAndPlayMusic() {
//
//                        }
//
//                        @Override
//                        public void rejectPhone() {//TODO 挂电话还没弄好
//
//                            try {
//                                Method method = Class.forName("android.os.ServiceManager")
//                                        .getMethod("getService", String.class);//getSystemService内部就是调用了ServiceManager的getService方法。
//                                IBinder binder = (IBinder) method.invoke(null,
//                                        new Object[]{TELEPHONY_SERVICE});
//                                ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
//                                iTelephony.endCall();
//                                Log.d("call---", "rejectPhone: " + "电话被挂断了");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//
//                        @Override
//                        public void cliencePhone() {
//                            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                            if (audioManager != null) {
//                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                                audioManager.getStreamVolume(AudioManager.STREAM_RING);
//                                Log.d("call---", "RINGING 已被静音");
//                            }
//                        }
//
//                        @Override
//                        public void knocknotify(int i) {
//
//                        }
//
//                        @Override
//                        public void sos() {
//
//                        }
//
////                        @Override
////                        public void knocknotify(int i) {
////
////                        }
////
////                        @Override
////                        public void sos() {
////
////                        }
//                    });

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);

                    break;
                /**
                 * b30InstagramTogg
                 * b30GmailTogg
                 * b30SnapchartTogg
                 * b30OhterTogg
                 */
                case R.id.b30InstagramTogg:
//                    isb30Instagram = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30GmailTogg:
//                    isb30Gmail = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30SnapchartTogg:
//                    isb30Snapchart = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30OhterTogg:
//                    isb30Ohter = isChecked;

                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, isChecked);
                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;

            }


        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x88:
                    closeLoadingDialog();
                    setSocailMsg();
                    break;
            }
            return false;
        }
    });


    private void setSocailMsg() {
        boolean ISSkype = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISSkype, false);
        boolean ISWhatsApp = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISWhatsApp, false);
        boolean ISFacebook = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISFacebook, false);
        boolean ISLinkendln = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISLinkendln, false);
        boolean ISTwitter = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISTwitter, false);
        boolean ISLINE = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISLINE, false);
        boolean ISWechart = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISWechart, false);
        boolean ISQQ = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISQQ, false);
        boolean ISMsm = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISMsm, false);
        boolean ISPhone = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISPhone, false);
        boolean ISInstagram = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISInstagram, false);
        boolean ISGmail = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISGmail, false);
        boolean ISSnapchart = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISSnapchart, false);
        boolean ISOhter = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISOhter, false);
        FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
        //电话提醒
        if (ISPhone) {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
        }
        //短信
        if (ISMsm) {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
        }
        //微信
        if (ISWechart) {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_CLOSE);
        }
        //QQ
        if (ISQQ) {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_CLOSE);
        }
        //新浪 不支持
        socailMsgData.setSina(EFunctionStatus.UNSUPPORT);
        //facebook
        if (ISFacebook) {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (ISTwitter) {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_CLOSE);
        }
        //flicker  不支持
        socailMsgData.setFlickr(EFunctionStatus.UNSUPPORT);
        if (ISLinkendln) {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (ISWhatsApp) {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_CLOSE);
        }
        if (ISLINE) {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_CLOSE);
        }

        //instagram
        //socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        //snapchat
        //socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        if (ISSkype) {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_CLOSE);
        }
        //gmail
        ///socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);

        //instagram
        if (ISInstagram) {
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_CLOSE);
        }
        //Gmail
        if (ISGmail) {
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_CLOSE);
        }
        //Snapchart
        if (ISSnapchart) {
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_CLOSE);
        }
        // 其他
        if (ISOhter) {
            socailMsgData.setOther(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setOther(EFunctionStatus.SUPPORT_CLOSE);
        }

        Log.e(TAG, "-------------socailMsgData=" + socailMsgData.toString());

        MyApp.getInstance().getVpOperateManager().settingSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                Log.d(TAG, "-----设置-=" + functionSocailMsgData.toString());
            }
        }, socailMsgData);

    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    public boolean isSupportOpen(EFunctionStatus data, FunctionSocailMsgData functionSocailMsgData) {
        List<EFunctionStatus> list = new ArrayList<>();
        list.add(functionSocailMsgData.getPhone());
        if (list.contains(data) && data == EFunctionStatus.SUPPORT_OPEN) {
            return true;
        } else {
            return false;
        }

    }

}
