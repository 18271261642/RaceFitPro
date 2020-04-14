package com.example.bozhilun.android.b30.b30minefragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.activity.NewLoginActivity;
import com.example.bozhilun.android.b30.B30DeviceActivity;
import com.example.bozhilun.android.b30.B30SysSettingActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.LocalizeTool;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/7/20.
 */

/**
 * B30 我的界面
 */
public class B30MineFragment extends LazyFragment implements RequestView {

    View b30MineView;
    Unbinder unbinder;

    @BindView(R.id.b30userImageHead)
    ImageView b30UserImageHead;
    @BindView(R.id.b30UserNameTv)
    TextView b30UserNameTv;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30MineDeviceTv)
    TextView b30MineDeviceTv;
    /**
     * 公制|英制
     */
    @BindView(R.id.b30MineUnitTv)
    TextView b30MineUnitTv;
    @BindView(R.id.b30MineSportGoalTv)
    TextView b30MineSportGoalTv;
    @BindView(R.id.b30MineSleepGoalTv)
    TextView b30MineSleepGoalTv;

    private RequestPressent requestPressent;

    private AlertDialog.Builder builder;
    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;

    /**
     * 本地化工具
     */
    private LocalizeTool mLocalTool;
    /**
     * 是否修改了个人中心的用户资料
     */
    private boolean updatePersonalInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b30MineView = inflater.inflate(R.layout.fragment_b30_mine_layout, container, false);
        unbinder = ButterKnife.bind(this, b30MineView);

        initViews();

        initData();

        return b30MineView;
    }

    private void initData() {
        mLocalTool = new LocalizeTool(getActivity());
        readCustomSetting();// 读取手环的自定义配置(是否打开公制)

        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        getUserData();

        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }

        for (int i = 1; i < 48; i++) {
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
        }

        //显示运动目标和睡眠目标
        int b30SportGoal = (int) SharedPreferencesUtils.getParam(getActivity(), "b30Goal", 0);
        b30MineSportGoalTv.setText(b30SportGoal + "");
        //睡眠
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30SleepGoal", "");
        if (!WatchUtils.isEmpty(b30SleepGoal)) {
            b30MineSleepGoalTv.setText(b30SleepGoal + "");
        }
    }

    private void getUserData() {
        String url = Commont.FRIEND_BASE_URL + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPressent.getRequestJSONObject(1, url, MyApp.getContext(), jsonObj.toString(), 1);
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.menu_settings));

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(getActivity() == null || getActivity().isFinishing())
            return;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (MyCommandManager.DEVICENAME != null) {
                String bleMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                b30MineDeviceTv.setText("B30 - " + bleMac);
            } else {
                b30MineDeviceTv.setText(getResources().getString(R.string.string_not_coon));//"未连接"
            }
        }
        if (updatePersonalInfo) {
            updatePersonalInfo = false;
            getUserData();
        }

        if (isSystem){
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            if (b30MineUnitTv!=null)b30MineUnitTv.setText(R.string.setkm);
            if (mLocalTool!=null)mLocalTool.putMetricSystem(true);
        }else {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
            if (b30MineUnitTv!=null)b30MineUnitTv.setText(R.string.setmi);
            if (mLocalTool!=null) mLocalTool.putMetricSystem(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (requestPressent != null) {
            requestPressent.detach();
        }

    }

    @Subscribe()
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals("personal_info_update")) {
            updatePersonalInfo = true;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.b30userImageHead, R.id.b30MineDeviceRel, R.id.b30MineSportRel,
            R.id.b30MineSleepRel, R.id.b30MineUnitRel, R.id.b30MineAboutRel, R.id.b30LogoutBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30userImageHead: //头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.b30MineDeviceRel: //设备
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        startActivity(new Intent(getActivity(), B30DeviceActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }

                break;
            case R.id.b30MineSportRel:  //运动目标
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        setSportGoal(); //设置运动目标
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineSleepRel:  //睡眠目标
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        setSleepGoal(); //设置睡眠目标
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineUnitRel:   //单位设置
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        showUnitDialog();
                    } else {
                        startActivity(new Intent(getActivity(), NewSearchActivity.class));
                        getActivity().finish();
                    }
                }
                break;
            case R.id.b30MineAboutRel:  //关于
                startActivity(new Intent(getActivity(), B30SysSettingActivity.class));
                break;
            case R.id.b30LogoutBtn: //退出登录
                longOutApp();
                break;
        }
    }

    private void longOutApp() {
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.exit_login))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (MyCommandManager.DEVICENAME != null) {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    if (state == -1) {
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(),  Commont.BLENAME, "");
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                                        MyApp.getInstance().setMacAddress(null);// 清空全局的
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        startActivity(new Intent(getActivity(), NewLoginActivity.class));
                                        getActivity().finish();
                                    }
                                }
                            });
                        } else {
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(MyApp.getContext(),  Commont.BLENAME, "");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                            MyApp.getInstance().setMacAddress(null);// 清空全局的
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "userInfo", "");
                            SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
//                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            startActivity(new Intent(getActivity(), NewLoginActivity.class));
                            getActivity().finish();

                        }


                    }
                }).show();

    }

    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30MineSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), "b30Goal", Integer.valueOf(profession.trim()));
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
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

    //设置睡眠目标
    private void setSleepGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30MineSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), "b30SleepGoal", profession.trim());
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
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

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



    void stuteCheck() {
        isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),  Commont.ISAutoHeart, true);//自动测量心率
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
        if (isDisconn){
            isDisAlert= EFunctionStatus.SUPPORT_OPEN;
        }else {
            isDisAlert = EFunctionStatus.SUPPORT_CLOSE;
        }
        //sos
        if (isSos) {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        } else {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        }
    }

    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0){
                            changeCustomSetting(true);
                        }else {
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

    /**
     * 改变手环的自定义数据
     *
     * @param isMetric 是否打开公制
     */
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

//                        Log.d("TAG", "----修改状态" + customSettingData.toString());
                        if (is24Hour) {
                            customSettingData.setIs24Hour(true);
                        } else {
                            customSettingData.setIs24Hour(false);
                        }
                        if (isSystem) {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_OPEN);
                        } else {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isAutomaticHeart) {
                            customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_OPEN);
                        } else {
                            customSettingData.setAutoHeartDetect(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isAutomaticBoold) {
                            customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_OPEN);
                        } else {
                            customSettingData.setAutoBpDetect(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isFindPhone) {
                            customSettingData.setFindPhoneUi(EFunctionStatus.SUPPORT_OPEN);
                        } else {
                            customSettingData.setFindPhoneUi(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isSystem) {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_OPEN);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        } else {
                            customSettingData.setMetricSystem(EFunctionStatus.SUPPORT_CLOSE);
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        }
                        if (isSos){
                            customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                        }else {
                            customSettingData.setSOS(EFunctionStatus.SUPPORT_CLOSE);
                        }
                        if (isDisconn){
                            customSettingData.setDisconnectRemind(EFunctionStatus.SUPPORT_OPEN);
                        }else {
                            customSettingData.setDisconnectRemind(EFunctionStatus.SUPPORT_CLOSE);
                        }


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
        if(getActivity() == null || getActivity().isFinishing())
            return;
        SharedPreferencesUtils.setParam(getActivity(),"utilsSP",isMetric);

        Intent intent = new Intent();
        intent.setAction("com.example.bozhilun.android.siswatch.run.UNIT");

        getActivity().sendBroadcast(intent);

        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        if (isSystem){
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            b30MineUnitTv.setText(R.string.setkm);
            mLocalTool.putMetricSystem(true);
        }else {
            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
            b30MineUnitTv.setText(R.string.setmi);
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

    /**
     * 读取手环的自定义数据
     */
    private void readCustomSetting() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readCustomSetting(new CustomSettingResponse(), new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {

                    if (customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN) {//查找手机
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFindPhone", true);
                    } else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFindPhone", false);
                    }
                    if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {//秒表
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30secondwatch", true);
                    } else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30secondwatch", false);
                    }
                    if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {//读取心率自动检测功能是否开启
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30AutoHeart", true);
                    } else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30AutoHeart", false);
                    }

                    if (customSettingData.getAutoBpDetect() == EFunctionStatus.SUPPORT_OPEN) {//读取血压自动检测功能是否开启
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30AutoBp", true);
                    } else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "b30AutoBp", false);
                    }
//                    Log.d("TAG","----------公英制---"+customSettingData.getMetricSystem());
                    if (customSettingData.getMetricSystem() == EFunctionStatus.SUPPORT_OPEN){
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isSystem", true);//是否为公制
                        b30MineUnitTv.setText(R.string.setkm);
                        mLocalTool.putMetricSystem(true);
                    }else {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isSystem", false);//是否为公制
                        b30MineUnitTv.setText(R.string.setmi);
                        mLocalTool.putMetricSystem(false);
                    }

//                    if (customSettingData.getDisconnectRemind() == EFunctionStatus.UNSUPPORT) {//判断是否支持断连提醒
//                        disconn_alarm.setVisibility(View.GONE);
//                    }

//                    if(customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN){//判断是否开启查找手机UI
//                        b30SwitchFindPhoneToggleBtn.setChecked(true);
//                    }else {
//                        b30SwitchFindPhoneToggleBtn.setChecked(false);
//                    }

                }
            });

        }

    }

    /**
     * 内部类,自定义设置输出结果
     */
    private class CustomSettingResponse implements IBleWriteResponse {
        @Override
        public void onResponse(int i) {
//            Log.d("bobo", "onResponse: " + i);
        }
    }

    /**
     * 内部类,自定义设置数据监听
     */
    private class CustomSettingListener implements ICustomSettingDataListener {
        @Override
        public void OnSettingDataChange(CustomSettingData data) {
            boolean isMetric = data.getMetricSystem() == EFunctionStatus.SUPPORT_OPEN;
            int textRes = isMetric ? R.string.setkm : R.string.setmi;
            b30MineUnitTv.setText(textRes);
            mLocalTool.putMetricSystem(isMetric);
        }
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (!WatchUtils.isEmpty(object.toString())) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                if (jsonObject.getString("resultCode").equals("001")) {
                    JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                    if (myInfoJsonObject != null) {
                        b30UserNameTv.setText("" + myInfoJsonObject.getString("nickName") + "");
                        String imgHead = myInfoJsonObject.getString("image");
                        if (!WatchUtils.isEmpty(imgHead)) {
                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true);
                            //头像
                            Glide.with(getActivity()).load(imgHead).apply(mRequestOptions).into(b30UserImageHead);    //头像
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

}
