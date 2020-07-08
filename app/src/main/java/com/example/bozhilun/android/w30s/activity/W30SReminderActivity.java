package com.example.bozhilun.android.w30s.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.MessageHelpActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.VerifyUtil;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/16 11:55
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SReminderActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


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
    @BindView(R.id.watch_msgOpenAccessBtn)
    RelativeLayout watch_msgOpenAccessBtn;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_reminder);
        ButterKnife.bind(this);

        newSearchTitleTv.setText(getResources().getString(R.string.string_application_reminding));
        watch_msgOpenAccessBtn.setVisibility(View.GONE);
        newSearchRightImg1.setVisibility(View.VISIBLE);
        getSwitchState();
        initSwitch();
    }

    private void getSwitchState() {
        boolean w30sswitch_skype = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Skype", false);
        boolean w30sswitch_whatsApp = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_WhatsApp", false);
        boolean w30sswitch_facebook = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Facebook", false);
        boolean w30sswitch_linkendIn = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_LinkendIn", false);
        boolean w30sswitch_twitter = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Twitter", false);
        boolean w30sswitch_viber = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Viber", false);
        boolean w30sswitch_line = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_LINE", false);
        boolean w30sswitch_weChat = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_WeChat", false);
        boolean w30sswitch_qq = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_QQ", false);
        boolean w30sswitch_msg = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Msg", false);
        boolean w30sswitch_Phone = (boolean) SharedPreferencesUtils.getParam(W30SReminderActivity.this, "w30sswitch_Phone", false);
        switchSkype.setChecked(w30sswitch_skype);
        switchWhatsApp.setChecked(w30sswitch_whatsApp);
        switchFacebook.setChecked(w30sswitch_facebook);
        switchLinkendIn.setChecked(w30sswitch_linkendIn);
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
                startActivity(MessageHelpActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_Skype:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Skype", isChecked);
                break;
            case R.id.switch_WhatsApp:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_WhatsApp", isChecked);
                break;
            case R.id.switch_Facebook:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Facebook", isChecked);
                break;
            case R.id.switch_LinkendIn:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_LinkendIn", isChecked);
                break;
            case R.id.switch_Twitter:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Twitter", isChecked);
                break;
            case R.id.switch_Viber:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Viber", isChecked);
                break;
            case R.id.switch_LINE:
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_LINE", isChecked);
                break;
            case R.id.switch_WeChat:    //微信
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_WeChat", isChecked);
                break;
            case R.id.switch_QQ:    //QQ
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_QQ", isChecked);
                break;
            case R.id.switch_Msg:   //短信
                if (!AndPermission.hasPermissions(W30SReminderActivity.this, new String[]{Manifest.permission.READ_CONTACTS,})) {
                    AndPermission.with(W30SReminderActivity.this)
                            .runtime()
                            .permission(Permission.READ_SMS, Permission.READ_CONTACTS)
                            .start();
                }
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Msg", isChecked);
                break;
            case R.id.switch_Phone:     //来电
                if (!AndPermission.hasPermissions(W30SReminderActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_PHONE_STATE})) {
                    AndPermission.with(W30SReminderActivity.this)
                            .runtime()
                            .permission( Permission.CALL_PHONE, Permission.READ_PHONE_STATE,
                                    Permission.READ_CONTACTS, Permission.READ_CALL_LOG)
                            .rationale(new Rationale<List<String>>() {
                                @Override
                                public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                    executor.execute();
                                }
                            })
                            .start();
                }
                boolean zh = VerifyUtil.isZh(W30SReminderActivity.this);
                if (!zh) {
                    Log.e("====", "========来电开关   -- 设置了英文");
                    MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(0);
                } else {
                    Log.e("====", "========来电开关 设置了中文");
                    MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(1);
                }
                SharedPreferencesUtils.setParam(W30SReminderActivity.this, "w30sswitch_Phone", isChecked);
                break;
        }
    }

    /**
     * 动态申请权限回调
     */
//    private PermissionListener permissionListener = new PermissionListener() {
//        @Override
//        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
//            switch (requestCode) {
//
//            }
//        }
//
//        @Override
//        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
//            switch (requestCode) {
//                case REQD_MSG_CONTENT_CODE:
//
//                    break;
//            }
//            AndPermission.hasAlwaysDeniedPermission(W30SReminderActivity.this, deniedPermissions);
//        }
//    };
}
