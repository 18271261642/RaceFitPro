package com.example.bozhilun.android.bzlmaps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bzlmaps.sos.GPSGaoDeUtils;
import com.example.bozhilun.android.bzlmaps.sos.GPSGoogleUtils;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.VerifyUtil;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.data.IDeviceControlPhone;


import java.util.ArrayList;
import java.util.List;

public class PhoneSosOrDisPhone implements IDeviceControlPhone {

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    handler.removeMessages(0x01);
                    String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personOne", "");
                    String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personTwo", "");
                    String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personThree", "");

                    if (!TextUtils.isEmpty(stringpersonOne)) {
                        call(stringpersonOne);
                    } else {
                        if (!TextUtils.isEmpty(stringpersonTwo)) {
                            call(stringpersonTwo);
                        } else {
                            if (!TextUtils.isEmpty(stringpersonThree)) {
                                call(stringpersonThree);
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void rejectPhone() {
        try {
            TelephonyManager tm = (TelephonyManager) MyApp.getContext()
                    .getSystemService(Service.TELEPHONY_SERVICE);
            PhoneUtils.endPhone(MyApp.getContext(), tm);
            PhoneUtils.dPhone();
            PhoneUtils.endCall(MyApp.getContext());
            Log.d("call---", "rejectPhone: " + "电话被挂断了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cliencePhone() {
        Log.e("PhoneSOS","------cliencePhone-----");
        //正常模式静音
        if (WatchUtils.getPhoneStatus() == AudioManager.RINGER_MODE_NORMAL) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), "phone_status", true);
            AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
                Log.d("call---", "RINGING 已被静音");
            }
        }
    }

    @Override
    public void knocknotify(int i) {
    }

    @Override
    public void sos() {
        //判断当前是否处于SOS当中
        if (!Commont.IS_RING_SOS) {
            Commont.IS_RING_SOS = true;
            //获取SOS开关状态
            boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos
            //获取已经添加的SOS 紧急联系人
            String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personOne", "");
            String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personTwo", "");
            String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personThree", "");
            if ((!TextUtils.isEmpty(stringpersonOne)
                    || !TextUtils.isEmpty(stringpersonTwo)
                    || !TextUtils.isEmpty(stringpersonThree))
                    && isSos) {
                Log.e("===", "======SOS 条件已经满足 ---------   开始重置发短信的次数、以及开始定位");
                Commont.SENDMESSGE_COUNT = 0;
                Commont.SENDPHONE_COUNT = 0;
                Commont.FIRST_SMS = true;
                Commont.FIRST_GPS = true;
                getLanguage();

                Log.e("===", "======SOS 条件已经满足 ---------  5 秒后拨打电话");
//                handler.sendEmptyMessageAtTime(0x01, 5000);
                if (handler!=null)handler.sendEmptyMessageDelayed(0x01,3000);
            } else {
                //SOS 的开关未打开，或者打开开惯了却没有联系人
                Commont.IS_RING_SOS = false;//将SOS状态重置
                ToastUtil.showShort(MyApp.getContext(), MyApp.getInstance().getResources().getString(R.string.string_sos_tip));
            }
        }
    }

    GPSGoogleUtils instance;

    private void getLanguage() {
        boolean zh = VerifyUtil.isZh(MyApp.getInstance());
        Log.e("===", "======SOS zh ---------  zh  "+zh);
        if (zh) {
            boolean zhonTW = MyApp.getInstance().getResources().getConfiguration().locale.getCountry().equals("TW");
            boolean zhonHK = MyApp.getInstance().getResources().getConfiguration().locale.getCountry().equals("HK");
            Log.e("===", "======SOS zhonTW||zhonHK ---------  zhonTW||zhonHK  "+zhonTW+"   "+zhonHK);
            if (zhonTW || zhonHK) {//台湾或者香港
                instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
                getGpsGoogle();
            } else {//大陆
                GPSGaoDeUtils.getInstance(MyApp.getInstance());
            }
        } else {
            instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
            getGpsGoogle();
        }
    }


    void getGpsGoogle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean b = instance.startLocationUpdates(MyApp.getInstance());
                if (!b) {
                    getGpsGoogle();
                }
            }
        }, 3000);
    }


    /**
     * 打电话
     *
     * @param tel
     */
    //点击事件调用的类
    protected void call(final String tel) {
        try {
            if (ActivityCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showShort(MyApp.getContext(), MyApp.getInstance().getResources().getString(R.string.string_sos_tip));
                return;
            }
            Log.e("ppppp ", "拨打电话");
            Uri uri = Uri.parse("tel:" + tel);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApp.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限 false-表示权限已开启
     */
    List<String> mPermissionList = null;

    //4、权限判断和申请
    private boolean initPermission(Context mContexts) {
        boolean isOk = false;
        if (mPermissionList == null) {
            mPermissionList = new ArrayList<>();
        } else mPermissionList.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (int i = 0; i < permissionsREAD.length; i++) {
            if (ContextCompat.checkSelfPermission(mContexts, permissionsREAD[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissionsREAD[i]);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            isOk = false;
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.e("=======", "权限请求完成A");
            isOk = true;
        }
        return isOk;
    }


    /**
     * 读写权限 自己可以添加需要判断的权限
     */
    public static String[] permissionsREAD = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,//
            Manifest.permission.READ_CONTACTS,//
            Manifest.permission.READ_CALL_LOG,//
            Manifest.permission.USE_SIP
    };

    @Override
    public void nextMusic() {

    }

    @Override
    public void previousMusic() {

    }

    @Override
    public void pauseAndPlayMusic() {

    }


}
