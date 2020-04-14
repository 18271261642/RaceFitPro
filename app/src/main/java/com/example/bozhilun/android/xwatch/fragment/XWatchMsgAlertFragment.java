package com.example.bozhilun.android.xwatch.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.MessageHelpActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.w30s.ble.WriteBackDataListener;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchNotiBean;
import com.example.bozhilun.android.xwatch.ble.XWatchNotiListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 消息提醒
 * Created by Admin
 * Date 2020/2/21
 */
public class XWatchMsgAlertFragment extends LazyFragment {

    View view;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;
    @BindView(R.id.xWatchSkypeTogg)
    ToggleButton xWatchSkypeTogg;
    @BindView(R.id.xWatchWhatsAppTogg)
    ToggleButton xWatchWhatsAppTogg;
    @BindView(R.id.xWatchFacebookTogg)
    ToggleButton xWatchFacebookTogg;
    @BindView(R.id.xWatchTwitterTogg)
    ToggleButton xWatchTwitterTogg;
    @BindView(R.id.xWatchWechatTogg)
    ToggleButton xWatchWechatTogg;
    @BindView(R.id.xWatchQQTogg)
    ToggleButton xWatchQQTogg;
    @BindView(R.id.xWatchMessageTogg)
    ToggleButton xWatchMessageTogg;
    @BindView(R.id.xWatchPhoneTogg)
    ToggleButton xWatchPhoneTogg;
    Unbinder unbinder;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private XWatchBleAnalysis xWatchBleAnalysis;

    private XWatchNotiBean xWatchNoti = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            if(MyCommandManager.DEVICENAME == null)
                return;
            if(MyCommandManager.DEVICENAME.equals("SWatch"))
                return;
            xWatchBleAnalysis.setDeviceNotiStatus(xWatchNoti, new WriteBackDataListener() {
                @Override
                public void backWriteData(byte[] data) {

                }
            });

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xWatchBleAnalysis = XWatchBleAnalysis.getW37DataAnalysis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_x_watch_msg_alert_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        initData();

        readDeviceNotiSiwtch();

        return view;
    }

    private void initData() {
        boolean isPhone = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISPhone, false);
        xWatchPhoneTogg.setChecked(isPhone);
        boolean isMsg = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISMsm, false);
        xWatchMessageTogg.setChecked(isMsg);
        boolean isQQ = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISQQ, false);
        xWatchQQTogg.setChecked(isMsg);

        boolean isWechat = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISWechart, false);
        boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISTwitter, false);
        boolean isFacebook = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISFacebook, false);
        boolean isWhatapp = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISWhatsApp, false);
        boolean isSkype = (boolean) SharedPreferencesUtils.getParam(getActivity(), Commont.ISSkype, false);
        xWatchWechatTogg.setChecked(isWechat);
        xWatchTwitterTogg.setChecked(isTwitter);
        xWatchFacebookTogg.setChecked(isFacebook);
        xWatchWhatsAppTogg.setChecked(isWhatapp);
        xWatchSkypeTogg.setChecked(isSkype);



    }

    private void readDeviceNotiSiwtch() {
        if(MyCommandManager.DEVICENAME == null)
            return;
        if(MyCommandManager.DEVICENAME.equals("SWatch")){
            xWatchNoti = new XWatchNotiBean();
            return;
        }
        /**
         * Bit0：1：来电提醒使能，0：来电提醒关闭
         * Bit1：1：短信提醒使能，0：短信提醒关闭
         * Bit2：1：QQ提醒使能，  0: QQ提醒关闭
         * Bit3：1：微信提醒使能，0: 微信提醒关闭
         * Bit4  1: Twitter提醒使能，0：Twitter提醒关闭
         * Bit5：1：Facebook提醒使能，0：Facebook提醒关闭
         * Bit6：1:Whatsapp提醒使能，0：Whatsapp提醒关闭
         * Bit7：1：Skype提醒使能，0：Skype提醒关闭
         */

        xWatchBleAnalysis.getDeviceNotiStatus(new XWatchNotiListener() {
            @Override
            public void backDeviceNotiStatus(XWatchNotiBean xWatchNotiBean) {
                xWatchNoti = xWatchNotiBean;

                SharedPreferencesUtils.setParam(getActivity(), Commont.ISPhone, xWatchNotiBean.isPhoneNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISMsm, xWatchNotiBean.isMsgNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISQQ, xWatchNotiBean.isQQNoti());

                SharedPreferencesUtils.setParam(getActivity(), Commont.ISWechart, xWatchNotiBean.isWechatNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISTwitter, xWatchNotiBean.isTwitterNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISFacebook, xWatchNotiBean.isFaceBookNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISWhatsApp, xWatchNotiBean.isWhatsappNoti());
                SharedPreferencesUtils.setParam(getActivity(), Commont.ISSkype, xWatchNotiBean.isSkypeNoti());


                xWatchPhoneTogg.setChecked(xWatchNotiBean.isPhoneNoti());
                xWatchMessageTogg.setChecked(xWatchNotiBean.isMsgNoti());
                xWatchQQTogg.setChecked(xWatchNotiBean.isQQNoti());
                xWatchWechatTogg.setChecked(xWatchNotiBean.isWechatNoti());
                xWatchTwitterTogg.setChecked(xWatchNotiBean.isTwitterNoti());
                xWatchFacebookTogg.setChecked(xWatchNotiBean.isFaceBookNoti());
                xWatchWhatsAppTogg.setChecked(xWatchNotiBean.isWhatsappNoti());
                xWatchSkypeTogg.setChecked(xWatchNotiBean.isSkypeNoti());
            }
        });
    }

    private void initViews() {
        newSearchTitleTv.setText(getResources().getString(R.string.Messagealert));
        newSearchRightImg1.setVisibility(View.VISIBLE);


        xWatchPhoneTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchMessageTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchQQTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchWechatTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchTwitterTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchFacebookTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchWhatsAppTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchSkypeTogg.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1,
            R.id.xWatchMsgOpenNitBtn})
    public void onClick(View view) {
        fragmentManager = getFragmentManager();
        if(fragmentManager == null)
            return;
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                fragmentManager.popBackStack();
                break;
            case R.id.newSearchRightImg1:
                startActivity(new Intent(getActivity(), MessageHelpActivity.class));
                break;
            case R.id.xWatchMsgOpenNitBtn:
                Intent intents = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intents, 1001);
                break;
        }
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed())
                return;
            if(xWatchNoti == null)
                return;
            showLoadingDialog("Loading...");
            switch (buttonView.getId()){
                case R.id.xWatchPhoneTogg:  //phone
                    requestPermiss(Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG);
                    xWatchNoti.setPhoneNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISPhone, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchMessageTogg:    //msg
                    requestPermiss(Manifest.permission.READ_SMS);
                    xWatchNoti.setMsgNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISMsm, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchQQTogg:     //QQ
                    xWatchNoti.setQQNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISQQ, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchWechatTogg: //微信
                    xWatchNoti.setWechatNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISWechart, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchTwitterTogg:    //twitter
                    xWatchNoti.setTwitterNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISTwitter, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchFacebookTogg:   //facebook
                    xWatchNoti.setFaceBookNoti(isChecked);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISFacebook, isChecked);
                    handler.sendEmptyMessage(0x01);
                    break;
                case R.id.xWatchWhatsAppTogg:
                    xWatchNoti.setWhatsappNoti(isChecked);
                    handler.sendEmptyMessage(0x01);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISWhatsApp, isChecked);
                    break;
                case R.id.xWatchSkypeTogg:
                    xWatchNoti.setSkypeNoti(isChecked);
                    handler.sendEmptyMessage(0x01);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.ISSkype, isChecked);
                    break;
            }
        }
    };


    private void requestPermiss(String...permiss){
        AndPermission.with(XWatchMsgAlertFragment.this).runtime().permission(permiss).start();
    }
}
