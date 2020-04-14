package com.example.bozhilun.android.b30;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.example.bozhilun.android.b30.view.B30DeviceAlarmActivity;
import com.example.bozhilun.android.b36.B36LightActivity;
import com.example.bozhilun.android.b36.B36SwitchActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.LocalizeTool;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.carema.W30sCameraActivity;
import com.example.bozhilun.android.w30s.wxsport.WXSportActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.listener.data.IScreenLightListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.datas.ScreenLightData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.model.settings.ScreenSetting;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/4.
 */

/**
 * 设备页面
 */
public class B30DeviceActivity extends WatchBaseActivity implements Rationale<List<String>> {

    private static final String TAG = "B30DeviceActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.longSitToggleBtn)
    ToggleButton longSitToggleBtn;
    @BindView(R.id.turnWristToggleBtn)
    ToggleButton turnWristToggleBtn;
    @BindView(R.id.privateBloadToggleBtn)
    ToggleButton privateBloadToggleBtn;
    //显示运动目标
    @BindView(R.id.b30DeviceSportGoalTv)
    TextView b30DeviceSportGoalTv;
    //睡眠目标
    @BindView(R.id.b30DeviceSleepGoalTv)
    TextView b30DeviceSleepGoalTv;
    //单位显示
    @BindView(R.id.b30DeviceUnitTv)
    TextView b30DeviceUnitTv;
    //血压私人模式的布局 B36无血压功能
    @BindView(R.id.b30DevicePrivateBloadRel)
    RelativeLayout b30DevicePrivateBloadRel;
    //B36的屏幕亮度调节,B30无此功能
    @BindView(R.id.b30DeviceLightRel)
    RelativeLayout b30DeviceLightRel;
    //微信运动
    @BindView(R.id.wxSportRel)
    RelativeLayout wxSportRel;

    //倒计时B36无此功能
    @BindView(R.id.b30DeviceCounDownRel)
    RelativeLayout b30DeviceCounDownRel;

    @BindView(R.id.deviceVersionTv)
    TextView deviceVersionTv;

    /**
     * 本地化工具
     */
    private LocalizeTool mLocalTool;

    /**
     * 私人血压数据
     */
    private BpSettingData bpData;

    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;


    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_layout);
        ButterKnife.bind(this);

        initViews();

        initData();

        //  Log.e(TAG,"-----111----b333="+(WatchUtils.isB36Device(B30DeviceActivity.this)));
    }

    private void initData() {
        mLocalTool = new LocalizeTool(this);
        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }

        for (int i = 1; i < 48; i++) {
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
        }


        //显示运动目标和睡眠目标
        int b30SportGoal = (int) SharedPreferencesUtils.getParam(B30DeviceActivity.this, "b30Goal", 0);
        b30DeviceSportGoalTv.setText(b30SportGoal + "");
        //睡眠
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
        if (!WatchUtils.isEmpty(b30SleepGoal)) {
            b30DeviceSleepGoalTv.setText(b30SleepGoal + "");
        }

        //读取是否转腕亮屏
        if (MyCommandManager.DEVICENAME != null) {
            if (!WatchUtils.isB36Device(B30DeviceActivity.this)) {
                //读取私人血压
                MyApp.getInstance().getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                    @Override
                    public void onDataChange(BpSettingData bpSettingData) {
                        readDetectBp(bpSettingData);
                    }
                });

//                ScreenSetting screenSetting = new ScreenSetting(22,0,
//                        0,7,2,4);
//                MyApp.getInstance().getVpOperateManager().settingScreenLight(iBleWriteResponse, new IScreenLightListener() {
//                    @Override
//                    public void onScreenLightDataChange(ScreenLightData screenLightData) {
//
//                    }
//                },screenSetting );
            }

        }
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        commentB30BackImg.setVisibility(View.VISIBLE);
        Log.e(TAG,"---------b333="+(WatchUtils.isB36Device(B30DeviceActivity.this)));
        if (!WatchUtils.isB36Device(B30DeviceActivity.this)) {
            b30DevicePrivateBloadRel.setVisibility(View.VISIBLE);
            b30DeviceLightRel.setVisibility(View.GONE);
           // wxSportRel.setVisibility(View.VISIBLE);
        } else {
            b30DevicePrivateBloadRel.setVisibility(View.GONE);
            b30DeviceLightRel.setVisibility(View.VISIBLE);
            //wxSportRel.setVisibility(View.GONE);
            b30DeviceCounDownRel.setVisibility(View.GONE);
        }
        longSitToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        turnWristToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        privateBloadToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());


        String deviceVersion = (String) SharedPreferencesUtils.getParam(B30DeviceActivity.this,Commont.DEVICE_VERSION_CODE_KEY,"0-0");
        if(WatchUtils.isEmpty(deviceVersion))
            deviceVersion = "0-0";
        deviceVersionTv.setText(deviceVersion);
    }

    /**
     * 读取手环私人血压模式是否打开
     */
    private void readDetectBp(BpSettingData bpSettingData) {
        bpData = bpSettingData;
        boolean privateBlood = bpSettingData.getModel() == EBPDetectModel.DETECT_MODEL_PRIVATE;
        privateBloadToggleBtn.setChecked(privateBlood);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isSystem) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            if (b30DeviceUnitTv != null) b30DeviceUnitTv.setText(R.string.setkm);
            if (mLocalTool != null) mLocalTool.putMetricSystem(true);
        } else {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
            if (b30DeviceUnitTv != null) b30DeviceUnitTv.setText(R.string.setmi);
            if (mLocalTool != null) mLocalTool.putMetricSystem(false);
        }


    }

    @OnClick({R.id.commentB30BackImg, R.id.b30DeviceMsgRel, R.id.b30DeviceAlarmRel,
            R.id.b30DeviceLongSitRel, R.id.b30DeviceWristRel, R.id.b30DevicePrivateBloadRel,
            R.id.b30DeviceSwitchRel, R.id.b30DevicePtoRel, R.id.b30DeviceResetRel,
            R.id.b30DeviceStyleRel, R.id.b30DevicedfuRel, R.id.b30DeviceClearDataRel,
            R.id.b30DisConnBtn, R.id.wxSportRel, R.id.b30DeviceSportRel, R.id.b30DeviceSleepRel,
            R.id.b30DeviceUnitRel, R.id.b30DeviceLightRel,R.id.b30DeviceCounDownRel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30DeviceMsgRel:  //消息提醒
                startActivity(B30MessAlertActivity.class);
                break;
            case R.id.b30DeviceAlarmRel:    //闹钟设置
                startActivity(B30DeviceAlarmActivity.class);
                break;
            case R.id.b30DeviceLongSitRel:  //久坐提醒
                startActivity(B30LongSitSetActivity.class);
                break;
            case R.id.b30DeviceWristRel:    //翻腕亮屏
                startActivity(B30TrunWristSetActivity.class);
                break;
            case R.id.b30DevicePrivateBloadRel: //血压私人模式
                startActivity(PrivateBloadActivity.class);
                break;
            case R.id.b30DeviceSwitchRel:   //开关设置
                if (WatchUtils.isB36Device(B30DeviceActivity.this)) {
                    startActivity(B36SwitchActivity.class);
                } else {
                    startActivity(B30SwitchSetActivity.class);
                }

                break;
            case R.id.b30DevicePtoRel:  //拍照
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                        .rationale(this)//添加拒绝权限回调
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                // data.get(0);
//                                startActivity(CameraActivity.class);
                                startActivity(W30sCameraActivity.class);
//                                startActivity(new Intent(getActivity(),W30sCameraActivity.class));
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
                                    showSettingDialog(B30DeviceActivity.this, data);
                                }
                            }
                        }).start();

                break;
//            case R.id.b30DeviceCounDownRel: //倒计时
//                startActivity(B30CounDownActivity.class);
//                break;
            case R.id.b30DeviceResetRel:    //重置设备密码
                startActivity(B30ResetActivity.class);
                break;
            case R.id.b30DeviceStyleRel:    //主题风格
                startActivity(B30ScreenStyleActivity.class);
                break;
            case R.id.b30DevicedfuRel:  //固件升级
                startActivity(B30DufActivity.class);
                break;
            case R.id.b30DeviceClearDataRel:    //清除数据
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.string_is_clear_data))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MyApp.getInstance().getVpOperateManager().clearDeviceData(iBleWriteResponse);
                            }
                        }).show();
                break;
            case R.id.wxSportRel:   //微信运动
                String bleName = WatchUtils.getSherpBleName(B30DeviceActivity.this);
                if(WatchUtils.isEmpty(bleName))
                    return;
                startActivity(WXSportActivity.class, new String[]{"bleName"}, new String[]{bleName});
                break;
            case R.id.b30DisConnBtn:    //断开连接
                disB30Conn();
                break;
            case R.id.b30DeviceSportRel:    //运动目标
                setSportGoal();
                break;
            case R.id.b30DeviceSleepRel:    //睡眠目标
                setSleepGoal();
                break;
            case R.id.b30DeviceUnitRel:     //单位设置
                showUnitDialog();
                break;
            case R.id.b30DeviceLightRel:    //亮度调节
                startActivity(B36LightActivity.class);
                break;
            case R.id.b30DeviceCounDownRel:     //倒计时
                startActivity(B30CounDownActivity.class);
                break;
        }
    }


    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(B30DeviceActivity.this);
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            changeCustomSetting(true);
                        } else {
                            changeCustomSetting(false);
                        }
//                        changeCustomSetting(i == 0);
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
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30DeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30DeviceSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30DeviceActivity.this);
    }


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30DeviceActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30DeviceSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "b30Goal", Integer.valueOf(profession.trim()));
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sportGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30DeviceActivity.this);
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
                        if (MyCommandManager.DEVICENAME != null) {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    Log.e(TAG, "----state=" + state);
                                    if (state == -1) {
                                        MyCommandManager.DEVICENAME = null;
                                        MyCommandManager.ADDRESS = null;
                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLENAME, null);
                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLEMAC, null);
                                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                                        MyApp.getInstance().setMacAddress(null);// 清空全局
                                        MyApp.getInstance().getB30ConnStateService().stopAutoConn();
                                        startActivity(NewSearchActivity.class);
                                        finish();

                                    }
                                }
                            });
                        } else {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLENAME, null);
                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLEMAC, null);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
                            MyApp.getInstance().setMacAddress(null);// 清空全局
                            MyApp.getInstance().getB30ConnStateService().stopAutoConn();
                            startActivity(NewSearchActivity.class);
                            finish();
                        }
                        new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                                .obtainFormatDate(1));// 同时把数据更新时间清楚更新最后更新数据的时间
//                        MyCommandManager.DEVICENAME = null;
//                        MyCommandManager.ADDRESS = null;
//                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLENAME, null);
//                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, Commont.BLEMAC, null);
//                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                        MyApp.getInstance().setMacAddress(null);// 清空全局
//                        startActivity(NewSearchActivity.class);
//                        finish();

                    }
                }).show();
    }


    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;
//                context.getString("Please give us permission in the settings:\\n\\n%1$s", TextUtils.join("\n", permissionNames));

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
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        //Toast.makeText(MyApp.getContext(),"用户从设置页面返回。", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
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


    //开关按钮点击监听
    private class ToggleClickListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (MyCommandManager.DEVICENAME != null && buttonView.isPressed()) {
                switch (buttonView.getId()) {
                    case R.id.longSitToggleBtn: //久坐设置
                        setLongSit(isChecked);
                        break;
                    case R.id.turnWristToggleBtn:   //转腕亮屏
                        setNightTurnWriste(isChecked);
                        break;
                    case R.id.privateBloadToggleBtn:    // 血压私人模式
                        int high = bpData == null ? 120 : bpData.getHighPressure();// 判断一下,防空
                        int low = bpData == null ? 60 : bpData.getLowPressure();// 给个默认值
                        BpSetting bpSetting = new BpSetting(isChecked, high, low);
                        MyApp.getInstance().getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                            @Override
                            public void onDataChange(BpSettingData bpSettingData) {
                                readDetectBp(bpSettingData);
                            }
                        }, bpSetting);
                        break;

                }
            }

        }
    }

    //设置转腕亮屏
    private void setNightTurnWriste(boolean isOn) {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().settingNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {

                }
            }, isOn);
        }
    }

    //设置久坐提醒
    private void setLongSit(boolean isOpen) {
        Log.e(TAG, "-----isOpen=" + isOpen);
        MyApp.getInstance().getVpOperateManager().settingLongSeat(iBleWriteResponse,
                new LongSeatSetting(8, 0, 19, 0, 60, isOpen),
                new ILongSeatDataListener() {
                    @Override
                    public void onLongSeatDataChange(LongSeatData longSeatData) {
                        Log.d(TAG, "-----设置久坐=" + longSeatData.toString());
                    }
                });
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    EFunctionStatus isFindePhone = EFunctionStatus.SUPPORT_CLOSE;//控制查找手机UI
    EFunctionStatus isStopwatch = EFunctionStatus.SUPPORT_CLOSE;////秒表
    EFunctionStatus isWear = EFunctionStatus.SUPPORT_CLOSE;//佩戴检测
    EFunctionStatus isCallPhone = EFunctionStatus.SUPPORT_CLOSE;//来电
    EFunctionStatus isHelper = EFunctionStatus.SUPPORT_CLOSE;//SOS 求救
    EFunctionStatus isDisAlert = EFunctionStatus.SUPPORT_CLOSE;//断开
    boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
    boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
    boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
    boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
    boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, true);//秒表
    boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
    boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, true);//查找手机
    boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);//来电
    boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, true);//断开连接提醒
    boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos


    private void changeCustomSetting(final boolean isMetric) {
        SharedPreferencesUtils.setParam(MyApp.getContext(), "isSystem", isMetric);//是否为公制
        stuteCheck();

        showLoadingDialog(getResources().getString(R.string.dlog));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtils.setParam(MyApp.getContext(), "isSystem", true);//是否为公制
                MyApp.getInstance().getVpOperateManager().changeCustomSetting(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {
                        closeLoadingDialog();
                    }
                }, new ICustomSettingDataListener() {
                    @Override
                    public void OnSettingDataChange(CustomSettingData customSettingData) {
                    }
                }, new CustomSetting(true,//isHaveMetricSystem
                        isMetric, //isMetric
                        is24Hour,//is24Hour
                        isAutomaticHeart, //isOpenAutoHeartDetect
                        isAutomaticBoold,//isOpenAutoBpDetect
                        EFunctionStatus.UNSUPPORT,//isOpenSportRemain
                        EFunctionStatus.UNSUPPORT,//isOpenVoiceBpHeart
                        isFindePhone,//isOpenFindPhoneUI
                        isStopwatch,//isOpenStopWatch
                        EFunctionStatus.UNSUPPORT,//isOpenSpo2hLowRemind
                        isWear,//isOpenWearDetectSkin
                        isCallPhone,//isOpenAutoInCall
                        EFunctionStatus.UNKONW,//isOpenAutoHRV
                        isDisAlert,//isOpenDisconnectRemind
                        isHelper));//isOpenSOS


            }
        }, 1000);

        SharedPreferencesUtils.setParam(B30DeviceActivity.this, "utilsSP", isMetric);

        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        if (isSystem) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            b30DeviceUnitTv.setText(R.string.setkm);
            mLocalTool.putMetricSystem(true);
        } else {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
            b30DeviceUnitTv.setText(R.string.setmi);
            mLocalTool.putMetricSystem(false);
        }

        //readCustomSetting();// 读取手环的自定义配置(是否打开公制)
//        MyApp.getVpOperateManager().changeCustomSetting(new CustomSettingResponse(), new CustomSettingListener(),
//                new CustomSetting(
//                        true,
//                        isMetric,
//                        true,
//                        true,
//                        true));
    }


    void stuteCheck() {
        isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
        isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, true);
        isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);
        isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, true);
        CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);
        isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, true);
        isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos

        //查找手机
        if (isFindPhone) {
            isFindePhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isFindePhone = EFunctionStatus.SUPPORT_CLOSE;
        }

        //秒表
        if (isSecondwatch) {
            isStopwatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isStopwatch = EFunctionStatus.SUPPORT_CLOSE;
        }

        //佩戴检测
        if (isWearCheck) {
            isWear = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isWear = EFunctionStatus.SUPPORT_CLOSE;
        }
        //来电
        if (CallPhone) {
            isCallPhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isCallPhone = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断开
        if (isDisconn) {
            isDisAlert = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isDisAlert = EFunctionStatus.SUPPORT_CLOSE;
        }
        //sos
        if (isSos) {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        } else {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        }
    }


}
