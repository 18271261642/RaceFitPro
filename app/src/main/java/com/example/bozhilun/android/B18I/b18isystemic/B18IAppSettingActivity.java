package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.FeedbackActivity;
import com.example.bozhilun.android.activity.ModifyPasswordActivity;
import com.example.bozhilun.android.activity.NewLoginActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.utils.NationalistinctionDUtils;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.device.RestoreFactory;
import com.umeng.analytics.MobclickAgent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 设置
 * @author： 安
 * @crateTime: 2017/9/19 14:11
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18IAppSettingActivity extends WatchBaseActivity {

    private final String TAG = "B18IAppSettingActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    private int ResetNUMBER = 0x66;
    @BindView(R.id.version_tv)
    TextView versionTv;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ResetNUMBER) {
                closeLoadingDialog();
                SharePeClear.clearDatas(B18IAppSettingActivity.this);
                mHandler.removeMessages(ResetNUMBER);
                startActivity(new Intent(B18IAppSettingActivity.this, NewSearchActivity.class));
                finish();
            } else if (msg.what == 0x88) {
                removeAllActivity();
                startActivity(new Intent(B18IAppSettingActivity.this, NewLoginActivity.class));
//                startActivity(new Intent(B18IAppSettingActivity.this, LoginActivity.class));
                finish();
            }
            return false;
        }
    });


    /**
     * 退出提示窗口
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_appsetting_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.menu_settings));
        whichDevice();//判断是B18i还是H9
        try {
            String versionName = getVersionName();
            if (!WatchUtils.isEmpty(versionName)) {
                versionTv.setText(versionName);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
        updateApp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
    }

    @OnClick({R.id.image_back, R.id.btn_exit, R.id.reset_device, R.id.line_help, R.id.line_above, R.id.change_pass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.change_pass:  //修改密码
                SharedPreferences share = getSharedPreferences("Login_id", 0);
                String userId = (String) SharedPreferencesUtils.readObject(B18IAppSettingActivity.this, "userId");
                if (!WatchUtils.isEmpty(userId) && userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {
                    ToastUtil.showToast(B18IAppSettingActivity.this, getResources().getString(R.string.noright));
                } else {
                    int isoff = share.getInt("id", 0);
                    if (isoff == 0) {
                        startActivity(new Intent(B18IAppSettingActivity.this, ModifyPasswordActivity.class));
                    } else {
                        ToastUtil.showToast(B18IAppSettingActivity.this, getResources().getString(R.string.string_third_login_changepass));
                    }
                }
                break;
            case R.id.btn_exit://退出
                new MaterialDialog.Builder(this)
                        .title(R.string.exit_login)
                        .content(R.string.confrim_exit)
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                new MaterialDialog.Builder(B18IAppSettingActivity.this)
                                        .title(getResources().getString(R.string.prompt))
                                        .content(getResources().getString(R.string.string_disconnected_ok))
                                        .positiveText(getResources().getString(R.string.confirm))
                                        .negativeText(getResources().getString(R.string.cancle))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                if (is18i.equals("W30S")) {
                                                    //断开蓝牙
                                                    if (MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices() != null) {
                                                        //手动断开清楚mac数据
                                                        MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices().disClearData();
                                                        MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices().disconnectBle();
                                                    }
                                                    //SharePeClear.clearDatas(B18IAppSettingActivity.this);
                                                    //W30SBLEManage.mW30SBLEServices.close();
                                                } else if (is18i.equals("H9")) {
                                                    String stringMac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
                                                    if (!WatchUtils.isEmpty(stringMac)) {
                                                        AppsBluetoothManager.getInstance(B18IAppSettingActivity.this).doUnbindDevice(stringMac);
                                                        AppsBluetoothManager.getInstance(B18IAppSettingActivity.this).clearMMacAddress();
                                                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_DEVICES_SYS", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
                                                        SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_TIME", "2018-12-25 12:20");//第一次同步电池电量时间
                                                        SharedPreferencesUtils.saveObject(MyApp.getInstance(), "H9_BATTERY", 100);//电池电量值
                                                        SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLEMAC, "");
                                                        SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLENAME, "");
                                                    }
                                                }
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTime", "2017-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTime", "2015-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upHeartTime", "2017-11-02 15:00:00");
                                                dialog.dismiss();
                                                MyCommandManager.deviceDisconnState = true;
                                                // 删除本地的mac地址
                                                Log.d("--SDK中的--mac---", BluetoothConfig.getDefaultMac(MyApp.getContext()));
                                                BluetoothConfig.setDefaultMac(MyApp.getContext(), "");
                                                String sss = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                                                Log.d("--SDK中的--mac--111111-", BluetoothConfig.getDefaultMac(MyApp.getContext()));
                                                AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(sss);
                                                MyApp.getInstance().getDaoSession().getStepBeanDao().deleteAll();//清空数据库
                                                SharedPreferencesUtils.saveObject(B18IAppSettingActivity.this, Commont.BLENAME, null);
                                                SharedPreferencesUtils.saveObject(B18IAppSettingActivity.this, Commont.BLEMAC, null);
                                                SharedPreferencesUtils.saveObject(B18IAppSettingActivity.this, "userId", null);
                                                MyCommandManager.ADDRESS = null;
                                                MyCommandManager.DEVICENAME = null;
                                                Common.customer_id = null;
                                                MobclickAgent.onProfileSignOff();
                                                mHandler.sendEmptyMessageDelayed(0x88, 500);
                                            }
                                        }).show();

                            }
                        }).show();
                break;
            case R.id.reset_device://重置
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    switch (is18i) {
                        case "B18i":

                            break;
                        case "H9":
                            new MaterialDialog.Builder(this)
                                    .title(R.string.prompt)
                                    .content(R.string.reset_device + "?")
                                    .positiveText(getResources().getString(R.string.confirm))
                                    .negativeText(getResources().getString(R.string.cancle))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (is18i.equals("B18i")) {
                                                BluetoothSDK.restoreFactory(resultCallBack);
                                            } else {
                                                //重置H9
                                                showLoadingDialog(getResources().getString(R.string.dlog));
                                                //上传数据时间间隔
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTime", "2017-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTime", "2015-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upHeartTime", "2017-11-02 15:00:00");
                                                AppsBluetoothManager.getInstance(MyApp.getContext())
                                                        .sendCommand(new RestoreFactory(commandResultCallback));
                                            }
                                        }
                                    }).show();
                            break;
                        case "B15P":

                            break;
                        case "W30S":
                            new MaterialDialog.Builder(this)
                                    .title(R.string.prompt)
                                    .content(R.string.reset_device + "?")
                                    .positiveText(getResources().getString(R.string.confirm))
                                    .negativeText(getResources().getString(R.string.cancle))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (is18i.equals("W30S")) {
                                                //重置W30S
                                                SharePeClear.clearDatas(B18IAppSettingActivity.this);
                                                showLoadingDialog(getResources().getString(R.string.dlog));
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTime", "2017-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTime", "2015-11-02 15:00:00");
                                                SharedPreferencesUtils.setParam(MyApp.getContext(), "upHeartTime", "2017-11-02 15:00:00");
                                                MyApp.getInstance().getmW30SBLEManage().setReboot();
                                                mHandler.sendEmptyMessageDelayed(ResetNUMBER, 500);
                                            }
                                        }
                                    }).show();
                            break;
                    }

                } else {
//                    Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                    startActivity(NewSearchActivity.class);
                    finish();
                }
                break;
            case R.id.line_help://帮助 -----改---->>>> 意见反馈
                //startActivity(AboutActivity.class);
                startActivity(FeedbackActivity.class);
                break;
            case R.id.line_above://关于-----改---->>>> App版本
//                //startActivity(new Intent(B18IAppSettingActivity.this, AboutActivity.class));

                updateApp();
                break;
        }
    }


    //更新APP
    private void updateApp() {
        String appInfo = NationalistinctionDUtils.getAppInfo(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof RestoreFactory) {
                Log.d(TAG, "H9恢复出厂设置成功");
                closeLoadingDialog();
                startActivity(new Intent(B18IAppSettingActivity.this, NewSearchActivity.class));
                finish();
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            Toast.makeText(B18IAppSettingActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
            closeLoadingDialog();
        }
    };

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_RESTORE_FACTORY:
                    Log.e(TAG, "B18i重置设备成功");
                    startActivity(new Intent(B18IAppSettingActivity.this, NewSearchActivity.class));
                    finish();
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };


    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }
}
