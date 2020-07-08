package com.example.bozhilun.android.b36;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICheckWearDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.datas.CheckWearData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CheckWearSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B36的开关设置
 * Created by Admin
 * Date 2018/11/14
 */
public class B36SwitchActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "B36SwitchActivity";


    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b36CheckWearToggleBtn)
    ToggleButton b36CheckWearToggleBtn;
    @BindView(R.id.b36AutoHeartToggleBtn)
    ToggleButton b36AutoHeartToggleBtn;
    @BindView(R.id.b36SecondToggleBtn)
    ToggleButton b36SecondToggleBtn;
    @BindView(R.id.b36SwitchDisAlertTogg)
    ToggleButton b36SwitchDisAlertTogg;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;

    @BindView(R.id.b36SwitchTimeTypeTogg)
    ToggleButton b36SwitchTimeTypeTogg;
    @BindView(R.id.b36is24HourRel)
    RelativeLayout b36is24HourRel;

    //断连提醒
    @BindView(R.id.b36DisconnRel)
    RelativeLayout b36DisconnRel;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setSwitchCheck();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b36_switch);
        ButterKnife.bind(this);


        initViews();
        initData();

    }

    private void initData() {
        //佩戴检测
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        b36CheckWearToggleBtn.setChecked(isWearCheck);
        MyApp.getInstance().getVpOperateManager().readCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                Log.e(TAG, "----customSettingData=" + customSettingData.toString());
                if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {
                    b36AutoHeartToggleBtn.setChecked(true);
                } else {
                    b36AutoHeartToggleBtn.setChecked(false);
                }

                //秒表
                if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {
                    b36SecondToggleBtn.setChecked(true);
                } else {
                    b36SecondToggleBtn.setChecked(false);
                }


                //断链提醒
                if (customSettingData.getDisconnectRemind() == EFunctionStatus.SUPPORT_OPEN) {
                    b36SwitchDisAlertTogg.setChecked(true);
                } else {
                    b36SwitchDisAlertTogg.setChecked(false);
                }

                if(customSettingData.getDisconnectRemind() == EFunctionStatus.UNSUPPORT){
                    b36DisconnRel.setVisibility(View.GONE);
                }else{
                    b36DisconnRel.setVisibility(View.VISIBLE);
                }

                b36SwitchTimeTypeTogg.setChecked(customSettingData.isIs24Hour());
//                //24小时制
//                if (customSettingData.is24Hour()) {
//                    b36is24HourRel.setVisibility(View.VISIBLE);
//                    b36SwitchTimeTypeTogg.setChecked(customSettingData.isIs24Hour());
//                }

            }
        });
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.string_switch_setting));
        commentB30BackImg.setVisibility(View.VISIBLE);
        b36CheckWearToggleBtn.setOnCheckedChangeListener(this);
        b36AutoHeartToggleBtn.setOnCheckedChangeListener(this);
        b36SecondToggleBtn.setOnCheckedChangeListener(this);
        b36SwitchDisAlertTogg.setOnCheckedChangeListener(this);
        b36SwitchTimeTypeTogg.setOnCheckedChangeListener(this);

    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed())
            return;
        switch (buttonView.getId()) {
            case R.id.b36CheckWearToggleBtn:    //佩戴检测
                b36CheckWearToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(B36SwitchActivity.this, Commont.ISWearcheck, isChecked);
                CheckWearSetting checkWearSetting = new CheckWearSetting();
                checkWearSetting.setOpen(isChecked);
                MyApp.getInstance().getVpOperateManager().setttingCheckWear(iBleWriteResponse, new ICheckWearDataListener() {
                    @Override
                    public void onCheckWearDataChange(CheckWearData checkWearData) {
                        Log.e(TAG, "----佩戴检测=" + checkWearData.toString());
                    }
                }, checkWearSetting);
                break;
            case R.id.b36AutoHeartToggleBtn:    //自动心率检测
                b36AutoHeartToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(B36SwitchActivity.this, Commont.ISAutoHeart, isChecked);
                handler.sendEmptyMessage(0x01);
                break;
            case R.id.b36SecondToggleBtn:       //秒表功能
                b36SecondToggleBtn.setChecked(isChecked);
                SharedPreferencesUtils.setParam(B36SwitchActivity.this, Commont.ISSecondwatch, isChecked);
                handler.sendEmptyMessage(0x01);
                break;
            case R.id.b36SwitchDisAlertTogg:    //断连提醒
                b36SwitchDisAlertTogg.setChecked(isChecked);
                SharedPreferencesUtils.setParam(B36SwitchActivity.this, Commont.ISDisAlert, isChecked);
                handler.sendEmptyMessage(0x01);
                break;
            case R.id.b36SwitchTimeTypeTogg:    //24小时制
                b36SwitchTimeTypeTogg.setChecked(isChecked);
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.IS24Hour, isChecked);//是否为24小时制
                handler.sendEmptyMessage(0x01);
                break;
        }

    }

    private void setSwitchCheck() {
        showLoadingDialog("loading...");

        //运动过量提醒 B36不支持
        EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
        //血压/心率播报 B36不支持
        EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
        //查找手表  B36不支持
        EFunctionStatus isOpenFindPhoneUI = EFunctionStatus.UNSUPPORT;
        //秒表功能  支持
        EFunctionStatus isOpenStopWatch;
        //低压报警 不支持
        EFunctionStatus isOpenSpo2hLowRemind = EFunctionStatus.UNSUPPORT;
        //肤色功能 支持
        EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

        //自动接听来电 不支持
        EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
        //自动检测HRV 不支持
        EFunctionStatus isOpenAutoHRV = EFunctionStatus.UNSUPPORT;
        //断连提醒 支持
        EFunctionStatus isOpenDisconnectRemind;
        //SOS  不支持
        EFunctionStatus isOpenSOS = EFunctionStatus.UNSUPPORT;


        //保存的状态
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


        CustomSetting customSetting = new CustomSetting(true, isSystem, is24Hour, isAutomaticHeart,
                false, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch, isOpenSpo2hLowRemind,
                isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind, isOpenSOS);


        MyApp.getInstance().getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                closeLoadingDialog();
                Log.e(TAG, "----customSettingData=" + customSettingData.toString());
            }
        }, customSetting);


    }
}
