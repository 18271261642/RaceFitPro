package com.example.bozhilun.android.b31;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.B30CounDownActivity;
import com.example.bozhilun.android.b30.B30DufActivity;
import com.example.bozhilun.android.b30.B30LongSitSetActivity;
import com.example.bozhilun.android.b30.B30MessAlertActivity;
import com.example.bozhilun.android.b30.B30ResetActivity;
import com.example.bozhilun.android.b30.B30ScreenStyleActivity;
import com.example.bozhilun.android.b30.B30TrunWristSetActivity;
import com.example.bozhilun.android.b30.PrivateBloadActivity;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.view.B30DeviceAlarmActivity;
import com.example.bozhilun.android.b31.model.B31HRVBean;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.LocalizeTool;
import com.example.bozhilun.android.w30s.carema.W30sCameraActivity;
import com.example.bozhilun.android.w30s.wxsport.WXSportActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31的设备页面
 * Created by Admin
 * Date 2018/12/18
 */
public class B31DeviceActivity extends WatchBaseActivity implements Rationale<List<String>> {

    private static final String TAG = "B31DeviceActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b31DeviceSportGoalTv)
    TextView b31DeviceSportGoalTv;
    @BindView(R.id.b31DeviceSleepGoalTv)
    TextView b31DeviceSleepGoalTv;
    @BindView(R.id.b31DeviceUnitTv)
    TextView b31DeviceUnitTv;

    @BindView(R.id.b31DeviceStyleRel)
    RelativeLayout b31DeviceStyleRel;


    @BindView(R.id.b31sPrivateBloadToggleBtn)
    ToggleButton b31sPrivateBloadToggleBtn;
    //B31s,500S的私人血压模式
    @BindView(R.id.b31sDevicePrivateBloadRel)
    RelativeLayout b31sDevicePrivateBloadRel;


    //微信运动
    @BindView(R.id.wxSportRel)
    RelativeLayout wxSportRel;
    @BindView(R.id.DeviceVersionTv)
    TextView DeviceVersionTv;
    //心率报警
    @BindView(R.id.b31sDeviceHeartAlarmRel)
    RelativeLayout b31sDeviceHeartAlarmRel;
    /**
     * 私人血压数据
     */
    private BpSettingData bpData;


    private AlertDialog.Builder builder;

    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_device_layout);
        ButterKnife.bind(this);

        initViews();
        initData();


    }

    private void initData() {

        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }

        for (int i = 1; i < 48; i++) {
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
        }

        if (MyCommandManager.DEVICENAME != null) {

            boolean isB31HasBp = (boolean) SharedPreferencesUtils.getParam(B31DeviceActivity.this, Commont.IS_B31_HAS_BP_KEY, false);
            b31sDevicePrivateBloadRel.setVisibility(isB31HasBp ? View.VISIBLE : View.GONE);
            if (isB31HasBp) {
                //读取私人血压
                MyApp.getInstance().getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                    @Override
                    public void onDataChange(BpSettingData bpSettingData) {
                        readDetectBp(bpSettingData);
                    }
                });
            }

        }
        //显示运动目标和睡眠目标
        int b30SportGoal = (int) SharedPreferencesUtils.getParam(B31DeviceActivity.this, "b30Goal", 0);
        b31DeviceSportGoalTv.setText(b30SportGoal + "");
        //睡眠
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
        b31DeviceSleepGoalTv.setText(b30SleepGoal + "");
        //公英制
        boolean isMeter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "isSystem", false);//是否为公制
        b31DeviceUnitTv.setText(isMeter ? getResources().getString(R.string.setkm) : getResources().getString(R.string.setmi));
    }


    /**
     * 读取手环私人血压模式是否打开
     */
    private void readDetectBp(BpSettingData bpSettingData) {
        bpData = bpSettingData;
        boolean privateBlood = bpSettingData.getModel() == EBPDetectModel.DETECT_MODEL_PRIVATE;
        Log.e(TAG, "----私人血压模式= " + MyCommandManager.DEVICENAME
                + " == " + bpData
                + " == " + privateBlood);
        if (b31sPrivateBloadToggleBtn != null) b31sPrivateBloadToggleBtn.setChecked(privateBlood);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        b31sPrivateBloadToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());

//        //是否支持心率预警
//        boolean isSupportHeartWaring = (boolean) SharedPreferencesUtils.getParam(B31DeviceActivity.this,Commont.IS_SUPPORT_HEART_WARING,false);
//        b31sDeviceHeartAlarmRel.setVisibility(isSupportHeartWaring ? View.VISIBLE : View.GONE);

        //B31带血压功能的标识
        boolean isSupportBp = (boolean) SharedPreferencesUtils.getParam(B31DeviceActivity.this,Commont.IS_B31_HAS_BP_KEY,false);
        b31sDevicePrivateBloadRel.setVisibility(isSupportBp?View.VISIBLE:View.GONE);


        if (MyCommandManager.DEVICENAME != null) {

            String versionCode = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICE_VERSION_CODE_KEY, "0-0");
            if (WatchUtils.isEmpty(versionCode))
                versionCode = "0-0";
            DeviceVersionTv.setText(versionCode);

            if (MyCommandManager.DEVICENAME.equals("B31") || MyCommandManager.DEVICENAME.equals("B31S") || MyCommandManager.DEVICENAME.equals("500S")) {
                wxSportRel.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.b31DeviceMsgRel,
            R.id.b31DeviceAlarmRel, R.id.b31DeviceLongSitRel,
            R.id.b31DeviceWristRel,
            R.id.img233, R.id.b31DeviceSportRel,
            R.id.b31DeviceSleepRel, R.id.b31DeviceUnitRel,
            R.id.b31DeviceSwitchRel, R.id.b31DevicePtoRel,
            R.id.b31DeviceResetRel, R.id.b31DeviceStyleRel,
            R.id.b31DeviceDfuRel, R.id.b31DeviceClearDataRel,
            R.id.wxSportRel, R.id.b31DisConnBtn, R.id.b31DeviceCounDownRel,
            R.id.b31sDevicePrivateBloadRel,R.id.b31sDeviceHeartAlarmRel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b31DeviceMsgRel:  //消息提醒
                startActivity(B30MessAlertActivity.class);
                break;
            case R.id.b31DeviceAlarmRel:    //闹钟设置
                startActivity(B30DeviceAlarmActivity.class);
                break;
            case R.id.b31DeviceLongSitRel:  //久坐提醒
                startActivity(B30LongSitSetActivity.class);
                break;
            case R.id.b31DeviceWristRel:    //转腕亮屏
                startActivity(B30TrunWristSetActivity.class);
                break;
            case R.id.b31DeviceSportRel:    //运动目标
                setSportGoal();
                break;
            case R.id.b31DeviceSleepRel:    //睡眠目标
                setSleepGoal();
                break;
            case R.id.b31sDevicePrivateBloadRel:    //设置私人血压值
                startActivity(PrivateBloadActivity.class);
                break;
            case R.id.b31DeviceUnitRel:     //单位设置
                showUnitDialog();
                break;
            case R.id.b31sDeviceHeartAlarmRel:  //心率报警
                startActivity(HeartWaringActivity.class);
                break;
            case R.id.b31DeviceSwitchRel:   //开关设置
                startActivity(B31SwitchActivity.class);
                break;
            case R.id.b31DeviceCounDownRel:     //倒计时
                startActivity(B30CounDownActivity.class);
                break;
            case R.id.b31DevicePtoRel:      //拍照
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                        .rationale(this)//添加拒绝权限回调
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                startActivity(W30sCameraActivity.class);
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                /**
                                 * 当用户没有允许该权限时，回调该方法
                                 */
                                Toast.makeText(MyApp.getContext(), getString(R.string.string_no_permission), Toast.LENGTH_SHORT).show();
                                /**
                                 * 判断用户是否点击了禁止后不再询问，AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)
                                 */
                                if (AndPermission.hasAlwaysDeniedPermission(MyApp.getContext(), data)) {
                                    //true，弹窗再次向用户索取权限
                                    showSettingDialog(MyApp.getContext(), data);
                                }
                            }
                        }).start();

                break;
            case R.id.b31DeviceResetRel:    //重置设备密码
                startActivity(B30ResetActivity.class);
                break;
            case R.id.b31DeviceStyleRel:    //界面风格
                startActivity(B30ScreenStyleActivity.class);
                break;
            case R.id.b31DeviceDfuRel:      //固件更新
                startActivity(B30DufActivity.class);
                break;
            case R.id.b31DeviceClearDataRel:    //清除数据

                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.string_is_clear_data))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                clearDeviceData();
                            }
                        }).show();
                break;
            case R.id.wxSportRel:       //微信运动
                String bleName = WatchUtils.getSherpBleName(B31DeviceActivity.this);
                if (WatchUtils.isEmpty(bleName))
                    return;
                startActivity(WXSportActivity.class, new String[]{"bleName"}, new String[]{bleName});
                break;
            case R.id.b31DisConnBtn:    //断开连接
                disB30Conn();

                break;
        }
    }


    private void clearDeviceData(){
        MyApp.getInstance().getVpOperateManager().clearDeviceData(iBleWriteResponse);
        //汇总步数 B30HalfHourDao.TYPE_STEP
        //详细步数 B30HalfHourDao.TYPE_SPORT
        //详细心率 B30HalfHourDao.TYPE_RATE
        //详细血压 B30HalfHourDao.TYPE_BP
        //详细睡眠 B30HalfHourDao.TYPE_SLEEP
        try {
            String bleMac = MyApp.getInstance().getMacAddress();
            if(bleMac == null)
                return;
            String dayStr = WatchUtils.getCurrentDate();
            LitePal.deleteAll(B30HalfHourDB.class,"address = ? and date = ? and type = ?" ,bleMac,dayStr, B30HalfHourDao.TYPE_STEP);

            LitePal.deleteAll(B30HalfHourDB.class,"address = ? and date = ? and type = ?" ,bleMac,dayStr, B30HalfHourDao.TYPE_SPORT);

            LitePal.deleteAll(B30HalfHourDB.class,"address = ? and date = ? and type = ?" ,bleMac,dayStr, B30HalfHourDao.TYPE_RATE);

            LitePal.deleteAll(B30HalfHourDB.class,"address = ? and date = ? and type = ?" ,bleMac,dayStr, B30HalfHourDao.TYPE_BP);

            LitePal.deleteAll(B30HalfHourDB.class,"address = ? and date = ? and type = ?" ,bleMac,WatchUtils.obtainAroundDate(dayStr,true), B30HalfHourDao.TYPE_SLEEP);
            //HRV
            LitePal.deleteAll(B31HRVBean.class, "dateStr=? and bleMac=?", dayStr, bleMac);
            //血氧
            LitePal.deleteAll(B31Spo2hBean.class, "dateStr=? and bleMac=?", dayStr, bleMac);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(B31DeviceActivity.this);
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SharedPreferencesUtils.setParam(B31DeviceActivity.this, Commont.ISSystem, i == 0);//是否为公制
                        b31DeviceUnitTv.setText(getResources().getString(i == 0 ? R.string.setkm : R.string.setmi));
                        setCusSetting(i == 0);
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        }).show();
    }


    //设置睡眠目标
    private void setSleepGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B31DeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b31DeviceSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B31DeviceActivity.this, "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B31DeviceActivity.this);
    }


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B31DeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b31DeviceSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B31DeviceActivity.this, "b30Goal", Integer.valueOf(profession.trim()));

                        setDeviceSportGoal();


                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sportGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B31DeviceActivity.this);
    }

    //设置运动目标
    private void setDeviceSportGoal() {
        MyApp.getInstance().getB30ConnStateService().setUserInfoToDevice();
    }


    @Override
    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, data);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new android.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
               .start(1001);
    }


    //断开连接
    private void disB30Conn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.string_devices_is_disconnected))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLENAME, "");
                        SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLEMAC, "");
                        if (MyCommandManager.DEVICENAME != null) {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    Log.e("断开", "---------state=" + state);
                                    if (state == -1) {
                                        SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLENAME, "");
                                        SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLEMAC, "");
                                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                                        MyApp.getInstance().setMacAddress(null);// 清空全局
                                        startActivity(NewSearchActivity.class);
                                        finish();

                                    }
                                }
                            });
                        } else {
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLENAME, "");
                            SharedPreferencesUtils.saveObject(B31DeviceActivity.this, Commont.BLEMAC, "");
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                            MyApp.getInstance().setMacAddress(null);// 清空全局
                            startActivity(NewSearchActivity.class);
                            finish();
                        }
                    }
                }).show();
    }


    //开关按钮点击监听
    private class ToggleClickListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            Log.e("===onCheckedChanged===", buttonView.isPressed() + "");
            if (!buttonView.isPressed())
                return;
            if (MyCommandManager.DEVICENAME == null)
                return;
            switch (buttonView.getId()) {
                case R.id.b31sPrivateBloadToggleBtn:    // 血压私人模式
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setttingPrivateBp(isChecked);
                        }
                    });
                    break;
            }

        }
    }

    private void setttingPrivateBp(boolean isChecked) {
        int high = bpData == null ? 120 : bpData.getHighPressure();// 判断一下,防空
        int low = bpData == null ? 80 : bpData.getLowPressure();// 给个默认值
        final BpSetting bpSetting = new BpSetting(isChecked, high, low);

        Log.e(TAG, "--------设置私人血压数据=" + bpSetting.toString());
        MyApp.getInstance().getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
            @Override
            public void onDataChange(BpSettingData bpSettingData) {
                Log.e(TAG, "---------设置私人血压返回=" + bpSetting.toString());
                //readDetectBp(bpSettingData);
            }
        }, bpSetting);
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    /**
     * 公英制
     *
     * @param isUnit true为公制，false为英制
     */
    private void setCusSetting(boolean isUnit) {
        //运动过量提醒 B31不支持
        EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
        //血压/心率播报 B31不支持
        EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
        //查找手表  B31不支持
        EFunctionStatus isOpenFindPhoneUI = EFunctionStatus.UNSUPPORT;
        //秒表功能  支持
        EFunctionStatus isOpenStopWatch;
        //低压报警 支持
        EFunctionStatus isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_OPEN;
        //肤色功能 支持
        EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

        //自动接听来电 不支持
        EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
        //自动检测HRV 支持
        EFunctionStatus isOpenAutoHRV = EFunctionStatus.SUPPORT_OPEN;
        //断连提醒 支持
        EFunctionStatus isOpenDisconnectRemind;
        //SOS  不支持
        EFunctionStatus isOpenSOS = EFunctionStatus.UNSUPPORT;


        //保存的状态
        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
        boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, false);//秒表
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, false);//查找手机
        boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, false);//来电
        boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒
        boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos


        //秒表功能
        if (isSecondwatch) {
            isOpenStopWatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenStopWatch = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if (isDisconn) {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }


        CustomSetting customSetting = new CustomSetting(true, isUnit, is24Hour, isAutomaticHeart,
                false, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);

        MyApp.getInstance().getVpOperateManager().changeCustomSetting(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                // Log.e(TAG, "----customSettingData=" + customSettingData.toString());
            }
        }, customSetting);
    }


}
