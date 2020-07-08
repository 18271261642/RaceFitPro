package com.example.bozhilun.android.bzlmaps.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;

public class SendSMSBroadCast extends BroadcastReceiver {
    String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personOne", "");
    String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personTwo", "");
    String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personThree", "");

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("----------AA       是A  ", ""+action);
        if (!WatchUtils.isEmpty(action)) {
            if (WatchUtils.isEmpty(stringpersonOne))
                stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personOne", "");
            if (WatchUtils.isEmpty(stringpersonTwo))
                stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personTwo", "");
            if (WatchUtils.isEmpty(stringpersonThree))
                stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personThree", "");

            switch (action) {
                case Commont.SOS_SENDSMS_MESSAGE:
                    String msm = intent.getStringExtra("msm");
                    Log.e("----------AA", msm);
                    if (Commont.FIRST_SMS){
                        Commont.FIRST_SMS = false;
                        Message message_msm = Message.obtain();
                        message_msm.what = 0xaa;
                        message_msm.obj = msm;
                        if (mHandler != null) mHandler.sendMessage(message_msm);
                    }
                    break;
                case Commont.SOS_SENDSMS_LOCATION:
                    String gps = intent.getStringExtra("gps");
                    if (Commont.FIRST_GPS){
                        Commont.FIRST_GPS = false;
                        Log.e("----------AA", gps);
                        Message message_gps = Message.obtain();
                        message_gps.what = 0xbb;
                        message_gps.obj = gps;
                        if (mHandler != null)mHandler.sendMessage(message_gps);
                    }
                    break;
            }
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0xaa:
                    String sms = (String) message.obj;
                    if (!WatchUtils.isEmpty(stringpersonOne)) {
//                            Log.d("----------AA", "发送给手机一");
                        sendMS(stringpersonOne, sms);
                    }
                    if (!WatchUtils.isEmpty(stringpersonTwo)) {
//                            Log.d("----------AA", "发送给手机二");
                        sendMS(stringpersonTwo, sms);
                    }
                    if (!WatchUtils.isEmpty(stringpersonThree)) {
//                            Log.d("----------AA", "发送给手机三");
                        sendMS(stringpersonThree, sms);
                    }
                    break;
                case 0xbb:
                    String gps = (String) message.obj;
                    if (!WatchUtils.isEmpty(stringpersonOne)) {
//                            Log.d("----------AA", "发送给手机一");
                        sendMS(stringpersonOne, gps);
                    }
                    if (!WatchUtils.isEmpty(stringpersonTwo)) {
//                            Log.d("----------AA", "发送给手机二");
                        sendMS(stringpersonTwo, gps);
                    }
                    if (!WatchUtils.isEmpty(stringpersonThree)) {
//                            Log.d("----------AA", "发送给手机三");
                        sendMS(stringpersonThree, gps);
                    }
                    break;
            }
            return false;
        }
    });


    /**
     * 根据短信长度分段发送短信
     *
     * @param phone
     * @param message
     */
    void sendMS(String phone, String message) {
        if (message.length() > 70) {
            ArrayList<String> msgs = SmsManager.getDefault().divideMessage(message);
            for (String msg : msgs)
                if (msg != null)
                    SmsManager.getDefault().sendTextMessage(phone, null, msg, null, null);
        } else SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
    }
}