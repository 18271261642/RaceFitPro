package com.example.bozhilun.android.w30s.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.B18I.b18isystemic.B18ITargetSettingActivity;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.w30s.ble.BleDeviceDataListener;
import com.example.bozhilun.android.w30s.ble.CallDatasBackListenter;
import com.example.bozhilun.android.w30s.ble.W37BloodBean;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.w30s.ota.NewW30OTAActivity;
import com.example.bozhilun.android.w30s.ota.NewW31OTAActivity;
import com.example.bozhilun.android.w30s.ota.NewW37OTAActivity;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.W30SBLEGattAttributes;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.carema.W30sCameraActivity;
import com.example.bozhilun.android.w30s.ota.NewW30sFirmwareUpgrade;
import com.example.bozhilun.android.w30s.wxsport.WXSportActivity;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 功能设置界面
 * @author： 安
 * @crateTime: 2017/9/5 16:29
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class W30SSettingActivity extends WatchBaseActivity implements RequestView {
    private final String TAG = "W30SSettingActivity";
    private final int HandlerTime = 500;
    private final int ResetNUMBER = 201314;
    private final int ResetFactory = 131420;

    @BindView(R.id.switch_bright)
    ToggleButton switchBright;
    @BindView(R.id.switch_heart)
    ToggleButton switchHeart;
    @BindView(R.id.switch_nodisturb)
    ToggleButton switchNodisturb;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mi)
    RadioButton radioMi;
    @BindView(R.id.radioGroup_unti)
    RadioGroup radioGroupUnti;
    @BindView(R.id.radio_24)
    RadioButton radio24;
    @BindView(R.id.radio_12)
    RadioButton radio12;
    @BindView(R.id.radioGroup_time)
    RadioGroup radioGroupTime;

    @BindView(R.id.deviceNewVersionImg)
    ImageView deviceNewVersionImg;

    @BindView(R.id.w30_wx_sport)
    LinearLayout w30WxSport;

    //固件升级，W31不升级
    @BindView(R.id.set_updata)
    LinearLayout setUpdateLim;

    private W37DataAnalysis w37DataAnalysis = null;
    private RequestPressent requestPressent;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg == null) return;
                if (msg.what == ResetNUMBER || msg.what == ResetFactory) {
                    handler.removeMessages(ResetFactory);
                    handler.removeMessages(ResetNUMBER);
                    closeLoadingDialog();
                    SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLENAME, "");
                    SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLEMAC, "");
                    MyCommandManager.DEVICENAME = null;
                    removeAllActivity();
                    startActivity(NewSearchActivity.class);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    @BindView(R.id.bar_titles)
    TextView barTitles;


    String bleName = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_setting_layout);
        ButterKnife.bind(this);

        if(w37DataAnalysis == null)
            w37DataAnalysis = W37DataAnalysis.getW37DataAnalysis();
        initData();

    }

    private void initData() {
        barTitles.setText(getResources().getString(R.string.function_str));
        switchHeart.setOnCheckedChangeListener(new CheckedListenter());
        switchBright.setOnCheckedChangeListener(new CheckedListenter());
        switchNodisturb.setOnCheckedChangeListener(new CheckedListenter());
        radioGroupUnti.setOnCheckedChangeListener(new RadioCheckeListenter());
        radioGroupTime.setOnCheckedChangeListener(new RadioCheckeListenter());

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sunit", true);
        Log.e("====bb  ", "====" + w30sunit);

        bleName = WatchUtils.getSherpBleName(W30SSettingActivity.this);
        Log.e(TAG,"-------bleName="+bleName);
        if(!WatchUtils.isEmpty(bleName) ){
            if(bleName.equals("W30")){
                w30WxSport.setVisibility(View.VISIBLE);
            }

            getDeviceNewVersion(bleName);

        }

    }

    //获取是否有新固件
    private void getDeviceNewVersion(String bName){
        if(requestPressent != null){
            String url = Commont.FRIEND_BASE_URL+URLs.getVersion;
            String wVersion = (String) SharedPreferencesUtils.getParam(this,"w_version_code","0");
            if(WatchUtils.isEmpty(wVersion))
                wVersion = "0";
            String wType = "";
            Map<String,String>  mp = new HashMap<>();
            mp.put("version",wVersion);
            if(bName.equals("W30"))
                wType = "android";
            if(bName.equals("W31"))
                wType = "Android_W31";
            if(bName.equals("W37"))
                wType = "Android_W37";
            mp.put("clientType",wType);
            requestPressent.getRequestJSONObject(0x01,url,W30SSettingActivity.this,new Gson().toJson(mp),0x01);

        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }

    //判断是B18i还是H9
    private void whichDevice() {
        boolean w30sHeartRate = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sHeartRate", true);
        switchHeart.setChecked(w30sHeartRate);
        boolean w30sBrightScreen = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sBrightScreen", true);
        switchBright.setChecked(w30sBrightScreen);
        boolean w30sNodisturb = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sNodisturb", false);
        switchNodisturb.setChecked(w30sNodisturb);
        switchNodisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean w30sNodisturb = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sNodisturb", false);
                if (w30sNodisturb) {
                    showNormalDialog();
                }
            }
        });

        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sunit", true);
        Log.e("====aa  ", "====" + w30sunit);
        if (w30sunit) {
//            radioGroupUnti.check(R.id.radio_km);
            radioKm.setChecked(true);
//            radioMi.setChecked(false);
        } else {
//            radioKm.setChecked(false);
            radioMi.setChecked(true);
//            radioGroupUnti.check(R.id.radio_mi);
        }
        boolean timeformat = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30stimeformat", true);
        if (timeformat) {
            radio24.setChecked(true);
        } else {
            radio12.setChecked(true);
        }


    }


    private static final int REQUEST_REQDPHONE_STATE_CODE = 1001;
    private static final int REQUEST_OPENCAMERA_CODE = 1002;

    @OnClick({R.id.set_updata, R.id.set_findeDevice, R.id.set_photograph,
            R.id.image_back, R.id.set_notifi_app, R.id.set_clock, R.id.set_more_shock,
            R.id.targetSetting, R.id.set_factory, R.id.set_unbind, R.id.w30_wx_sport})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_updata://固件升级
                if (MyCommandManager.DEVICENAME != null) {
                    showLoadingDialog("Loading...");
                    //获取固件版本
                    W37DataAnalysis.getW37DataAnalysis().getDeviceInfo(W30SBLEGattAttributes.syncTime(), new BleDeviceDataListener() {
                        @Override
                        public void callBleDeviceData(W30SDeviceData deviceData) {
                            closeLoadingDialog();
                            Log.e(TAG,"--------device="+deviceData.toString());

                            if (AndPermission.hasPermissions(W30SSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                // startActivity(NewW30sFirmwareUpgrade.class);
                                if(bleName == null)
                                    return;
                                if(bleName.equals("W30")){
                                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "W30S_V", deviceData.getDeviceVersionNumber() + "");
                                    startActivity(NewW30OTAActivity.class);
                                }else if(bleName.equals("W31")){
                                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "W31S_V", deviceData.getDeviceVersionNumber() + "");
                                    startActivity(NewW31OTAActivity.class);
                                }
                                else if(bleName.equals("W37")){
                                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "W37S_V", deviceData.getDeviceVersionNumber() + "");
                                    startActivity(NewW37OTAActivity.class);
                                }

                            } else {
                                AndPermission.with(W30SSettingActivity.this)
                                        .runtime()
                                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .onGranted(new Action<List<String>>() {
                                            @Override
                                            public void onAction(List<String> data) {
//
                                            }
                                        })
                                        .onDenied(new Action<List<String>>() {
                                            @Override
                                            public void onAction(List<String> data) {

                                            }
                                        })
                                        .rationale(rationale)
                                        .start();
                            }
                        }
                    });
                }

                break;
            case R.id.image_back:
                finish();
                break;
            case R.id.set_findeDevice:  //查找手机

                w37DataAnalysis.setDeviceLanguage(this);
                //查找手机
                w37DataAnalysis.findDevice();

                break;
            case R.id.set_photograph://摇一摇拍照
                if (AndPermission.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
                    w37DataAnalysis.intoTakePhotoStatus();
                    //MyApp.getInstance().getmW30SBLEManage().intoShakePicture();//手表进入摇一摇拍照界面
                    //startActivity(NewW30sCameraActivity.class);
                    startActivity(W30sCameraActivity.class);
                } else {
                    AndPermission.with(this)
                            .runtime()
                            .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            .rationale(rationale)
                            .onGranted(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {
                                    w37DataAnalysis.intoTakePhotoStatus();
                                   // MyApp.getInstance().getmW30SBLEManage().intoShakePicture();//手表进入摇一摇拍照界面
                                    //startActivity(NewW30sCameraActivity.class);
                                    startActivity(W30sCameraActivity.class);
                                }
                            })
                            .start();
                }
                break;
            case R.id.set_notifi_app:
                //应用通知
                startActivity(W30SReminderActivity.class);
                break;
            case R.id.set_clock:
                //闹钟设置
//                startActivity(new Intent(W30SSettingActivity.this, W30SAlarmClockRemindActivity.class));
                startActivity(new Intent(W30SSettingActivity.this, W30SAlarmClockActivity.class));
                break;
            case R.id.set_more_shock:
                //设备提醒
                startActivity(MoreShockActivity.class);
                break;
            case R.id.targetSetting:
                //目标
                startActivity(new Intent(W30SSettingActivity.this, B18ITargetSettingActivity.class).putExtra("is18i", "W30S"));
                break;
            case R.id.set_factory:
                //恢复出厂
                new MaterialDialog.Builder(this)
                        .title(R.string.prompt)
                        .content(R.string.string_factory_setting)
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SharePeClear.clearDatas(W30SSettingActivity.this);
                                //重置W30S
                                showLoadingDialog(getResources().getString(R.string.dlog));
                                SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSportTime", "2017-11-02 15:00:00");
                                SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSleepTime", "2015-11-02 15:00:00");
                                SharedPreferencesUtils.setParam(MyApp.getInstance(), "upHeartTime", "2017-11-02 15:00:00");
                                //MyApp.getInstance().getmW30SBLEManage().setReboot();
                                w37DataAnalysis.setResetDevice();
                                handler.sendEmptyMessageDelayed(ResetNUMBER, HandlerTime);
                            }
                        }).show();
                break;
            case R.id.set_unbind:   //解绑
                //解绑
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.confirm_unbind_strap))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    showLoadingDialog(getResources().getString(R.string.dlog));
                                    //断开蓝牙
                                    W30SBleUtils.isOtaConn = false;
                                    MyApp.getInstance().getW37BleOperateManager().disBleDeviceByMac(WatchUtils.getSherpBleMac(W30SSettingActivity.this));
//                                    W30SBLEManage.mW30SBLEServices.disconnectBle();
//                                    //手动断开清楚mac数据
//                                    W30SBLEManage.mW30SBLEServices.disClearData();
                                    //SharePeClear.clearDatas(B18IAppSettingActivity.this);
                                    //W30SBLEManage.mW30SBLEServices.close();

                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSportTime", "2017-11-02 15:00:00");
                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSleepTime", "2015-11-02 15:00:00");
                                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upHeartTime", "2017-11-02 15:00:00");
//                                W30SBLEManage.mW30SBLEServices.disconnect();
//                                MyCommandManager.DEVICENAME = null;
//                                W30SBLEManage.mW30SBLEServices.disconnect();
//                                W30SBLEManage.mW30SBLEServices.close();
//                                SharePeClear.clearDatas(W30SSettingActivity.this);
                                    handler.sendEmptyMessageDelayed(ResetFactory, HandlerTime);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).show();
                break;
            case R.id.w30_wx_sport: //微信运动

                startActivity(WXSportActivity.class,new String[]{"bleName"},new String[]{"W30"});
                break;

        }
    }



    private class CheckedListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.switch_bright:
                    if (isChecked) {
                        /**
                         * 抬手亮屏开关
                         *
                         * @param value 1=开，0=关
                         */
                       // MyApp.getInstance().getmW30SBLEManage().setTaiWan(1);
                    } else {
                        //MyApp.getInstance().getmW30SBLEManage().setTaiWan(0);
                    }
                    w37DataAnalysis.setTurnHand(isChecked?1:0);
                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30sBrightScreen", isChecked);
                    break;
                case R.id.switch_heart:
                    if (isChecked) {
                        /**
                         * 运动心率开关
                         *
                         * @param value 1=开，0=关
                         */
                       // MyApp.getInstance().getmW30SBLEManage().setWholeMeasurement(1);
                    } else {
                        //MyApp.getInstance().getmW30SBLEManage().setWholeMeasurement(0);
                    }
                    w37DataAnalysis.setHeartSwitch(isChecked ? 1 : 0);
                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30sHeartRate", isChecked);
                    break;
                case R.id.switch_nodisturb:

                    if (isChecked) {
                        /**
                         * 免打扰开关
                         *
                         * @param value 1=开，0=关
                         */
                        //MyApp.getInstance().getmW30SBLEManage().setDoNotDistrub(1);
                    } else {
                       // MyApp.getInstance().getmW30SBLEManage().setDoNotDistrub(0);
                    }

                    w37DataAnalysis.setDeviceNoNoti(isChecked ? 1 : 0);
                    SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30sNodisturb", isChecked);
                    break;
            }
        }
    }


    private void showNormalDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.string_no_dialog))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApp.getInstance().getmW30SBLEManage().setDoNotDistrub(1);
                    }
                }).create().show();
    }


    private class RadioCheckeListenter implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            Log.e("====", group.isPressed() + "====");
            switch (group.getId()) {
                case R.id.radioGroup_unti:
                    if (checkedId == R.id.radio_km) {
//                        Log.d("--------", "KM");
                        w37DataAnalysis.setDeviceUnit(1);
                        //MyApp.getInstance().getmW30SBLEManage().setUnit(1);// 0=英制，1=公制
                        SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30sunit", true);
                        //运动界面公英制是依据B30中保存的公英制判断---所以在保存一次
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), Commont.ISSystem, true);//是否为公制

                        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sunit", true);
                        Log.e("====----b", "====" + w30sunit);
                    } else if (checkedId == R.id.radio_mi) {
//                        Log.d("--------", "mi");
                        //MyApp.getInstance().getmW30SBLEManage().setUnit(0);// 0=英制，1=公制
                        w37DataAnalysis.setDeviceUnit(0);
                        SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30sunit", false);
                        SharedPreferencesUtils.setParam(MyApp.getInstance(), Commont.ISSystem, false);//是否为公制

                        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(W30SSettingActivity.this, "w30sunit", true);
                        Log.e("====----a  ", "====" + w30sunit);
                    }
                    break;
                case R.id.radioGroup_time:
                    if (checkedId == R.id.radio_24) {
//                        Log.d("--------", "24");
                        //MyApp.getInstance().getmW30SBLEManage().setTimeFormat(1);
                        w37DataAnalysis.setDeviceTimeStyle(1);
                        SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30stimeformat", true);
                    } else if (checkedId == R.id.radio_12) {
//                        Log.d("--------", "12");
                        W37DataAnalysis.getW37DataAnalysis().setDeviceTimeStyle(0);
                        //MyApp.getInstance().getmW30SBLEManage().setTimeFormat(0);
                        SharedPreferencesUtils.setParam(W30SSettingActivity.this, "w30stimeformat", false);
                    }
                    break;
            }
        }
    }

    private Rationale rationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            AndPermission.with(W30SSettingActivity.this).runtime().setting().start();
            executor.execute();
        }
    };




    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(!jsonObject.has("code"))
                return;
            if(jsonObject.getInt("code") == 200)
                deviceNewVersionImg.setVisibility(View.VISIBLE);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

}
