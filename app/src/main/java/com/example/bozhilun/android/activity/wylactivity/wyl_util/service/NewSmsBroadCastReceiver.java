package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.W37Constance;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.hplus.bluetooth.BleProfileManager;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.SmsPush;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.AppIC;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 短信接收广播
 * Created by Admin
 * Date 2019/6/26
 */
public class NewSmsBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "NewSmsBroadCastReceiver";


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String msgStr = (String) msg.obj;
            Log.e(TAG,"------msgStr="+msgStr);
            sendMsgToDevice(msgStr);
        }
    };

    private void sendMsgToDevice(String msgStr) {
        String saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if(WatchUtils.isEmpty(saveBleName))
            return;
        if(MyCommandManager.DEVICENAME == null)
            return;

        //华为和荣耀手机接收短信使用广播
        String deviceBrad = android.os.Build.BRAND;
        if(!WatchUtils.isEmpty(deviceBrad) && (deviceBrad.equals("HUAWEI") || deviceBrad.equals("Huawei") || deviceBrad.equals("HONOR"))){
            //B25,B15P
            Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
            int pushMsg_Sms = AppIC.SData().getIntData("pushMsg_Sms");
            if(set.contains(saveBleName)&&pushMsg_Sms==1){   //B15P
                L4Command.SendSMSInstruction("",msgStr);
            }
            //舟海
            if (saveBleName.equals("W30") || saveBleName.equals("W31") || saveBleName.equals("W37")) {  //W30
                sendMsgW30S(msgStr, W37Constance.NotifaceMsgMsg);
            }
            //维亿魄
            if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                boolean isMSG = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISMsm, true);
                if (isMSG) sendB30Mesage(ESocailMsg.SMS, "MMS", msgStr);
            }

            if (saveBleName.equals("bozlun")) {   //H8
                String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "msg");
                if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                    MyApp.getInstance().h8BleManagerInstance().setSMSAlert();
            }

            if (saveBleName.equals("H9")) {   //H9
                sendSmsCommands("", msgStr,
                        B18iUtils.H9TimeData(), MsgCountPush.SMS_MSG_TYPE, 1);
            }

            if( saveBleName.equals("B18") || saveBleName.equals("B16")){  //B18
                BleProfileManager.getInstance().getCommandController().sendSMSText(msgStr);
            }

            if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch") ){
                if(MyCommandManager.DEVICENAME == null)
                    return;
                if(MyCommandManager.DEVICENAME.equals("SWatch")){
                    XWatchBleAnalysis.getW37DataAnalysis().setSWatchNoti(100);
                }else{
                    XWatchBleAnalysis.getW37DataAnalysis().setDeviceNoti(1);
                }
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String format = intent.getStringExtra("format");
            if(bundle == null)
                return;

            Object[] object=(Object[]) bundle.get("pdus");
            if(object == null)
                return;
            StringBuilder sb=new StringBuilder();

            for(Object obs : object){
                byte[] pusMsg = (byte[]) obs;
                SmsMessage sms = SmsMessage.createFromPdu(pusMsg,format == null ? "":format);
                String mobile = sms.getOriginatingAddress();//发送短信的手机号
                String content = sms.getMessageBody();//短信内容
                sb.append(mobile +  content);

            }

            Message message = handler.obtainMessage();
            message.obj = sb.toString();
            message.what = 1001;
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendMsgW30S(String h9Msg, int type) {

        try {
            boolean w30sswitch_skype = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Skype", false);
            boolean w30sswitch_whatsApp = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_WhatsApp", false);
            boolean w30sswitch_facebook = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Facebook", false);
            boolean w30sswitch_linkendIn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_LinkendIn", false);
            boolean w30sswitch_twitter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Twitter", false);
            boolean w30sswitch_viber = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Viber", false);
            boolean w30sswitch_line = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_LINE", false);
            boolean w30sswitch_weChat = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_WeChat", false);
            boolean w30sswitch_qq = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_QQ", false);
            boolean w30sswitch_msg = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Msg", false);
            boolean w30sswitch_Phone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Phone", false);

            switch (type) {
                case W37Constance.NotifaceMsgQq:
                    if (w30sswitch_qq) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgWx:
                    if (w30sswitch_weChat) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgFacebook:
                    if (w30sswitch_facebook) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgTwitter:
                    if (w30sswitch_twitter) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgWhatsapp:
                    if (w30sswitch_whatsApp) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgViber:
                    if (w30sswitch_viber) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgSkype:
                    if (w30sswitch_skype) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgMsg:  //短信
                    if (w30sswitch_msg) sendW30SApplicationMsg(h9Msg, type);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * W30,W31的消息提醒
     *
     * @param h9Msg
     * @param type
     */
    public void sendW30SApplicationMsg(String h9Msg, int type) {
        String w30SBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if (!WatchUtils.isEmpty(w30SBleName)) {
            W37DataAnalysis.getW37DataAnalysis().sendAppalertData(h9Msg,type);
        }
    }

    //B30的短信
    private void sendB30Mesage(ESocailMsg b30msg, String appName, String context) {
        if (MyCommandManager.DEVICENAME != null) {
            ContentSetting msgConn = new ContentSmsSetting(b30msg, appName, context);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, msgConn);
        }

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    /**
     * H9 短信
     *
     * @param from
     * @param content
     * @param date      (格式为年月日‘T’时分秒)
     * @param countType
     * @param count
     */
    private void sendSmsCommands(String from, String content, String date, byte countType, int count) {
        SmsPush smsPushName = null, smsPushContent = null, smsPushDate = null;
        try {
            if (!TextUtils.isEmpty(from)) {
                byte[] bName = from.getBytes("utf-8");
                smsPushName = new SmsPush(commandResultCallback, SmsPush.SMS_NAME_TYPE, bName);
            }
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                smsPushContent = new SmsPush(commandResultCallback, SmsPush.SMS_CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                smsPushDate = new SmsPush(commandResultCallback, SmsPush.SMS_DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MsgCountPush countPush = new MsgCountPush(commandResultCallback, countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
//        Log.d(TAG, "smsPushName=" + smsPushName + "smsPushContent=" + smsPushContent + "smsPushDate=" + smsPushDate);
        if (smsPushName != null) {
            sendList.add(smsPushName);
        }
        if (smsPushContent != null) {
            sendList.add(smsPushContent);
        }
        if (smsPushDate != null) {
            sendList.add(smsPushDate);
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

        }

        @Override
        public void onFail(BaseCommand baseCommand) {

        }
    };


}
