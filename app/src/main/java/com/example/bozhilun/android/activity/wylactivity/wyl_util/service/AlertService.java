package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.W37Constance;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.hplus.bluetooth.BleProfileManager;
import com.sdk.bluetooth.protocol.command.push.CalanderPush;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.SmsPush;
import com.sdk.bluetooth.protocol.command.push.SocialPush;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.tjdL4.tjdmain.AppIC;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;
import com.veepoo.protocol.model.settings.ContentSocailSetting;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * 提醒服务  MyNotificationListenerService
 * 通过通知获取APP消息内容，需要打开通知功能
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AlertService extends MyNotificationListenerService {
    private static final String TAG = "AlertService";
    private static final String H8_NAME_TAG = "bozlun";

    //QQ
    private static final String QQ_PACKAGENAME = "com.tencent.mobileqq";
    //QQ急速版
    private static final String QQ_FAST_PACK_NAME = "com.tencent.qqlite";


    //微信
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    //微博
    private static final String WEIBO_PACKAGENAME = "com.sina.weibo";
    //Facebook
    private static final String FACEBOOK_PACKAGENAME = "com.facebook.katana";

    private static final String FACEBOOK_PACKAGENAME1 = "com.facebook.orca";
    //twitter
    private static final String TWITTER_PACKAGENAME = "com.twitter.android";
    //Whats
    private static final String WHATS_PACKAGENAME = "com.whatsapp";
    //viber
    private static final String VIBER_PACKAGENAME = "com.viber.voip";
    //instagram
    private static final String INSTANRAM_PACKAGENAME = "com.instagram.android";
    //日历
    private static final String CALENDAR_PACKAGENAME = "com.android.calendar";
    //信息 三星手机信息
    private static final String SAMSUNG_MSG_PACKNAME = "com.samsung.android.messaging";
    private static final String SAMSUNG_MSG_SRVERPCKNAME = "com.samsung.android.communicationservice";
    private static final String MSG_PACKAGENAME = "com.android.mms";//短信系统短信包名
    private static final String SYS_SMS = "com.android.mms.service";//短信 --- vivo Y85A
    private static final String XIAOMI_SMS_PACK_NAME = "com.xiaomi.xmsf";



    private static final String SKYPE_PACKAGENAME = "com.skype.raider";
    private static final String SKYPE_PACKNAME = "com.skype.rover";
    //line
    private static final String LINE_PACKAGENAME = "jp.naver.line.android";
    private static final String LINE_LITE_PACK_NAME = "com.linecorp.linelite";


    //谷歌邮箱
    private static final String GMAIL_PACKAGENAME = "com.google.android.gm";
    //Snapchat：
    private static final String SNAP_PACKAGENAME = "com.snapchat.android";

    private String newmsg = "";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);

    }


    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    //当系统收到新的通知后出发回调
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            //获取应用包名
            String packageName = sbn.getPackageName();
            //Log.d(TAG, "=====kkkk===" + sbn.toString());
            Log.e(TAG, "------通知包名=" + packageName);
            //获取notification对象
            Notification notification = sbn.getNotification();
            if (notification == null) return;
            if (MyCommandManager.DEVICENAME == null)
                return;
            String msgCont = null;
            Bundle extras = notification.extras;
            String content = "null";
            String title = "null";
            if (extras != null) {
                // 获取通知标题
                title = extras.getString(Notification.EXTRA_TITLE, "");
                // 获取通知内容
                content = extras.getString(Notification.EXTRA_TEXT, "");
                msgCont = title + content;
            }

            Log.e(TAG, "-----title-+content=" + title + "--==" + content);

            //获取消息内容----标题加内容
            CharSequence tickerText = notification.tickerText;
            if (tickerText != null) {
                msgCont = tickerText.toString();
            }

            if (WatchUtils.isEmpty(msgCont))
                return;
            if(msgCont.contains("正在运行"))
                return;
            String saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
            Log.e(TAG, "-----msgCont----=" + msgCont + "---saveBleName=" + saveBleName);
            if (WatchUtils.isEmpty(saveBleName)) {
                return;
            }

            Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
            boolean other = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISOhter, false);
            boolean isW30Device = ( saveBleName.equals("W30") || saveBleName.equals("W31") || saveBleName.equals("W37"));
            boolean isB16Device = (saveBleName.equals("B18") || saveBleName.contains("B16"));

            if (packageName.equals(LINE_PACKAGENAME) || packageName.equals(LINE_LITE_PACK_NAME)) {  //line
                int pushMsg_Line = AppIC.SData().getIntData("pushMsg_Line");
                if (set.contains(saveBleName) && pushMsg_Line == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(6, title, content);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_line = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_LINE", false);
                    if (h9_line) sendMessH9(SocialPush.LINE, msgCont, (byte) 0x0F);
                }
                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgLine);
                }

                boolean isLine = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISLINE, false);

                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    if (isLine) sendB30Msg(ESocailMsg.LINE, "Line", msgCont);
                }

                if(isB16Device){  //B18
                    if(isLine)sendB18MsgType("line",msgCont);

                }
                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    if(isLine)sendXWatcsgType(100);
                }
                return;
            }

            if (packageName.equals(QQ_PACKAGENAME) || packageName.equals(QQ_FAST_PACK_NAME)) {   //QQ
                int pushMsg_qq = AppIC.SData().getIntData("pushMsg_QQ");
                if (set.contains(saveBleName) && pushMsg_qq == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(1, title, content);
                }
                if (saveBleName.equals("H8")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("qq", msgCont);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_qq = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_QQ", false);
                    if (h9_qq) sendMessH9(SocialPush.QQ, msgCont, (byte) 0x08);
                }
                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgQq);
                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isQQ = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISQQ, false);
                    if (isQQ) sendB30Msg(ESocailMsg.QQ, "QQ", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isQQ = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISQQ, false);
                    if(isQQ)sendB18MsgType("isQQ",msgCont);

                }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    boolean isQQ = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISQQ, false);
                    if(isQQ)
                        sendXWatcsgType(2);
                }

                return;
            }

            if (packageName.equals(WECHAT_PACKAGENAME)) {   //微信
                int pushMsg_Wx = AppIC.SData().getIntData("pushMsg_Wx");
                if (set.contains(saveBleName) && pushMsg_Wx == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(2, title, content);
                }
                if (saveBleName.equals("H8") || saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("weChat", msgCont);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_wecth = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_WECTH", false);
                    if (h9_wecth)
                        sendMessH9(SocialPush.WECHAT, msgCont, (byte) 0x09);
                }
                if (isW30Device) {  //W30,W31
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgWx);
                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isWechart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWechart, false);
                    if (isWechart) sendB30Msg(ESocailMsg.WECHAT, "Wechat", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isWechart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWechart, false);
                    if(isWechart)sendB18MsgType("wechat",msgCont);

                }
                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){   //Xwatch
                    boolean isWechart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWechart, false);
                    if(isWechart)sendXWatcsgType(3);
                }
                return;
            }
            if (packageName.equals(FACEBOOK_PACKAGENAME) || packageName.equals(FACEBOOK_PACKAGENAME1)) {    //facebook
                int pushMsg_FaceBook = AppIC.SData().getIntData("pushMsg_FaceBook");
                if (set.contains(saveBleName) && pushMsg_FaceBook == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(3, title, content);
                }
                if (saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("facebook", msgCont);

                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_facebook = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_FACEBOOK", false);
                    if (h9_facebook)
                        sendMessH9(SocialPush.FACEBOOK, msgCont, (byte) 0x0A);
                }
                if (isW30Device) {  //W30,W31
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgFacebook);
                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isFacebook = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFacebook, false);
                    if (isFacebook) sendB30Msg(ESocailMsg.FACEBOOK, "FaceBook", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isFacebook = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFacebook, false);
                    if(isFacebook)sendB18MsgType("facebook",msgCont);

                }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    boolean isFacebook = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFacebook, false);
                    if(isFacebook)sendXWatcsgType(5);
                }

                return;
            }
            if (packageName.equals(TWITTER_PACKAGENAME)) {  //twitter

                int pushMsg_TwitTer = AppIC.SData().getIntData("pushMsg_TwitTer");
                if (set.contains(saveBleName) && pushMsg_TwitTer == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(4, title, content);
                }
                if (saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("twitter", msgCont);

                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_twtter = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_TWTTER", false);
                    if (h9_twtter)
                        sendMessH9(SocialPush.TWITTER, msgCont, (byte) 0x0B);
                }
                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgTwitter);

                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISTwitter, false);
                    if (isTwitter) sendB30Msg(ESocailMsg.TWITTER, "Twitter", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISTwitter, false);
                    if(isTwitter)sendB18MsgType("twitter",msgCont);

                }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISTwitter, false);
                    if(isTwitter)
                        sendXWatcsgType(4);
                }

                return;

            }

            if (packageName.equals(WHATS_PACKAGENAME)) {    //WhatsApp
                int pushMsg_WhatsApp = AppIC.SData().getIntData("pushMsg_WhatsApp");
                if (set.contains(saveBleName) && pushMsg_WhatsApp == 1) {   //B15P
                    //pushtype 1 qq 2 微信 3 facebook 4 twitter 5 whatsapp  6 line 7 kakaoTalk
                    L4Command.SendPushContent(5, title, content);
                }
                if (saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("whatsApp", msgCont);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_whatsapp = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_WHATSAPP", false);
                    if (h9_whatsapp)
                        sendMessH9(SocialPush.WHATSAPP, msgCont, (byte) 0x0C);
                }
                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgWhatsapp);

                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isWhats = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWhatsApp, false);
                    if (isWhats) sendB30Msg(ESocailMsg.WHATS, "Whats", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isWhats = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWhatsApp, false);
                    if(isWhats)sendB18MsgType("whats",msgCont);

                }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    boolean isWhats = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWhatsApp, false);
                    if(isWhats)sendXWatcsgType(6);
                }

                return;

            }

            if (packageName.equals(INSTANRAM_PACKAGENAME)) {    //Instanram

                if (saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("instanram", msgCont);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_instagram = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_INSTAGRAM", false);
                    if (h9_instagram)
                        sendMessH9(SocialPush.INSTAGRAM, msgCont, (byte) 0x0F);
                }

                boolean isInstagram = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISInstagram, false);

                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    if (isInstagram)
                        sendB30Msg(ESocailMsg.INSTAGRAM, "Instagram", msgCont);
                }

                if(isB16Device){  //B18
                    if(isInstagram)sendB18MsgType("instagram",msgCont);

                }
                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    if(isInstagram)sendXWatcsgType(102);
                }
                return;

            }

            if (packageName.equals(VIBER_PACKAGENAME)) {    //Viber

                if (saveBleName.equals("bozlun")) {   //H8
                    String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber");
                    if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                        sendTo("viber", msgCont);
                }
                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_instagram = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_INSTAGRAM", false);
                    if (h9_instagram)
                        sendMessH9(SocialPush.INSTAGRAM, msgCont, (byte) 0x0F);
                }

                boolean isViber = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISViber, true);

                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgViber);
                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    sendB30Mesage(ESocailMsg.SMS, "Viber", msgCont);
                }


                if(isB16Device){  //B18
                    if(isViber)sendB18MsgType("viber",msgCont);
                }
                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    if(isViber)sendXWatcsgType(101);
                }
                return;

            }
            if (packageName.equals(MSG_PACKAGENAME) || packageName.equals(SYS_SMS)
                    || packageName.equals(SAMSUNG_MSG_PACKNAME)
                    || packageName.equals(SAMSUNG_MSG_SRVERPCKNAME) ) {   //短信

                //华为和荣耀手机接收短信使用广播
                String deviceBrad = android.os.Build.BRAND;
                if(!WatchUtils.isEmpty(deviceBrad) && (deviceBrad.equals("HUAWEI") || deviceBrad.equals("Huawei") || deviceBrad.equals("HONOR"))){
                    return;
                }
                    int pushMsg_Sms = AppIC.SData().getIntData("pushMsg_Sms");
                    if (set.contains(saveBleName) && pushMsg_Sms == 1) {   //B15P
                        L4Command.SendSMSInstruction(title, content);
                    }
                    if (saveBleName.equals("bozlun")) {   //H8
                        String h8Switch = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "msg");
                        if (!WatchUtils.isEmpty(h8Switch) && h8Switch.equals("0"))
                            sendTo("mms", msgCont);
                    }
                    if (saveBleName.equals("H9")) {   //H9
                        String msg = "";
                        String[] split = msgCont.split("[:]");
                        if (split != null) {
                            String people = split[0];
                            if (!WatchUtils.isEmpty(people)) {
                                msg = msgCont.substring(people.length(), msgCont.length());
                                if (!WatchUtils.isEmpty(msg))
                                    sendSmsCommands(people, msg,
                                            B18iUtils.H9TimeData(), MsgCountPush.SMS_MSG_TYPE, 1);
                            }
                        }
                    }
                    if (isW30Device) {  //W30
                        sendMsgW30S(msgCont, W37Constance.NotifaceMsgMsg);
                    }
                    if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                        boolean isMSG = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISMsm, true);
                        if (isMSG) sendB30Mesage(ESocailMsg.SMS, "MMS", msgCont);
                    }

                    if(isB16Device){  //B18
                        boolean isMSG = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISMsm, true);
                        if(isMSG)sendB18MsgType("msg",msgCont);

                    }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){   //Xwatch
                    boolean isMSG = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISMsm, true);
                    if(isMSG)sendXWatcsgType(1);
                }

                    return;

            }


            if (packageName.equals(SKYPE_PACKAGENAME) || packageName.equals(SKYPE_PACKNAME)) {  //Skype


                if (saveBleName.equals("H9")) {   //H9
                    boolean h9_skype = (boolean) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_SKYPE", false);
                    if (h9_skype)
                        sendMessH9(SocialPush.SKYPE, msgCont, (byte) 0x0F);
                }
                if (isW30Device) {  //W30
                    sendMsgW30S(msgCont, W37Constance.NotifaceMsgSkype);
                }
                if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                    boolean isSkype = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSkype, false);
                    if (isSkype) sendB30Msg(ESocailMsg.SKYPE, "Skype", msgCont);
                }

                if(isB16Device){  //B18
                    boolean isSkype = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSkype, false);
                    if(isSkype)sendB18MsgType("skype",msgCont);

                }

                if(saveBleName.equals("XWatch") || saveBleName.equals("SWatch")){
                    boolean isSkype = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSkype, false);
                    if(isSkype)sendXWatcsgType(7);
                }

                return;

            }
            if (packageName.equals(GMAIL_PACKAGENAME)) {
                boolean isGmail = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISGmail, false);

                if(isB16Device){  //B18
                    if(isGmail)sendB18MsgType("gmail",msgCont);
                }

                if(WatchUtils.isVPBleDevice(saveBleName)){
                    if (isGmail) sendB30Msg(ESocailMsg.GMAIL, "Gmail", msgCont);
                }

                return;
            }
            if (packageName.equals(SNAP_PACKAGENAME)) {
                boolean isSnapchart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSnapchart, false);

                if(isB16Device){  //B18
                    if(isSnapchart)sendB18MsgType("skype",msgCont);

                }

                if(WatchUtils.isVPBleDevice(saveBleName)){
                    if (isSnapchart)  sendB30Msg(ESocailMsg.SNAPCHAT, "Snapchart", msgCont);
                }

                return;

            }

            if (WatchUtils.isVPBleDevice(saveBleName)) {
                if(other) sendB30Msg(ESocailMsg.OTHER, "", msgCont);
            }
            if(isB16Device){  //B18
                if(other)sendB18MsgType("other",msgCont);

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }



    //xWatch推送
    private void sendXWatcsgType(int type){
        String saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if(WatchUtils.isEmpty(saveBleName))
            return;
        if(saveBleName.equals("SWatch")){
            XWatchBleAnalysis.getW37DataAnalysis().setSWatchNoti(type);
        }else{
            XWatchBleAnalysis.getW37DataAnalysis().setDeviceNoti(type);
        }

    }





    //B18消息提醒类型
    private void sendB18MsgType(String msgType,String msg) {
        if(MyCommandManager.DEVICENAME == null)
            return;
        if(msgType.equals("msg")){  //短信
            BleProfileManager.getInstance().getCommandController().sendSMSText(msg);
        }else{
            BleProfileManager.getInstance().getCommandController().sendNotificationData(msg);
        }

    }

    //编码
    public static String stringtoUnicode(String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }


    //推送维亿魄系列设备的消息提醒
    private void sendB30Msg(ESocailMsg b30msg, String appName, String context) {
        Log.e(TAG, "------name=" + MyCommandManager.DEVICENAME);
        if (MyCommandManager.DEVICENAME != null) {
            ContentSocailSetting contentSocailSetting = new ContentSocailSetting(b30msg, appName, context);
            //ContentSetting contentSetting = new ContentSocailSetting(b30msg, 0, 20, appName, context);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, contentSocailSetting);
        }

    }


    //B30的短信
    private void sendB30Mesage(ESocailMsg b30msg, String appName, String context) {
        if (MyCommandManager.DEVICENAME != null) {
            ContentSetting msgConn = new ContentSmsSetting(b30msg, appName, context);

            // ContentSetting contentSetting = new ContentSmsSetting(b30msg, 0, 20, appName, context);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, msgConn);
        }

    }


    /**
     * 日历消息推送
     *
     * @param newmsg
     */
    private void sendCalendarsH9(String newmsg) {
        String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if (!TextUtils.isEmpty(mylanya) && mylanya.equals("H9")) {
            String[] strings = B18iUtils.stringToArray(String.valueOf(newmsg));//分割出name
            String title = strings[0];
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < strings.length; i++) {
                sb.append(strings[i]);
            }
            Log.d(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
            sendCalendar(title + "" + sb.toString(), B18iUtils.H9TimeData(), MsgCountPush.SCHEDULE_TYPE, 1);
            return;
        }
    }


    /**
     * H9发送社交消息
     */
    private void sendMessH9(byte socal, String newmsg, byte countType) {
        try {
            String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
            if (!TextUtils.isEmpty(mylanya) && mylanya.equals("H9")) {
                if (TextUtils.isEmpty(newmsg)) {
                    sendSocialCommands(getResources().getString(R.string.news),
                            getResources().getString(R.string.messages), B18iUtils.H9TimeData(), socal, 1, countType);
                    return;
                }
                String[] strings = B18iUtils.stringToArray(String.valueOf(newmsg));//分割出name
                String title = strings[0];
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i < strings.length; i++) {
                    sb.append(strings[i]);
                }
                Log.e(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
                sendSocialCommands(title, sb.toString(), B18iUtils.H9TimeData(), socal, 1, countType);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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


    String phoneName = "";
    String people = "";

    public String callPhoneNumber(String sender) {
        people = sender;
        if (sender.length() == 11) {
            phoneName = getPeopleNameFromPerson(sender);
            //getPeople(sender.substring(0, sender.length()));//电话转联系人
        } else if (sender.length() == 13) {
            phoneName = getPeopleNameFromPerson(sender.substring(2, sender.length()));
        } else if (sender.length() == 14) {
            phoneName = getPeopleNameFromPerson(sender.substring(3, sender.length()));
        }
//            String people = getPeople(sender.substring(3, sender.length()));//电话转联系人
        if (WatchUtils.isEmpty(phoneName)) {
            phoneName = people;
        }
        return phoneName;
    }

    // 通过address手机号关联Contacts联系人的显示名字
    private String getPeopleNameFromPerson(String address) {
        if (address == null || address == "") {
            return "( no address )\n";
        }

        String strPerson = "null";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤
        Cursor cursor = MyApp.getContext().getContentResolver().query(uri_Person, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int index_PeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String strPeopleName = cursor.getString(index_PeopleName);
            strPerson = strPeopleName;
        }
        cursor.close();

        return strPerson;
    }


    //H8手表发送指令
    public static void sendTo(String apptags, String msg) {
        Log.e(TAG, "------msg----" + msg + "----" + apptags);
        String bleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if (!WatchUtils.isEmpty(bleName) && bleName.equals(H8_NAME_TAG)) {
            if (apptags.equals("phone")) {    //电话
                MyApp.getInstance().h8BleManagerInstance().setPhoneAlert();
            } else if (apptags.equals("mms")) {    //短信
                MyApp.getInstance().h8BleManagerInstance().setSMSAlert();
            } else {
                MyApp.getInstance().h8BleManagerInstance().setAPPAlert();
            }
        }
    }

    /**
     * 分包发送数据
     *
     * @param bs
     * @param currentPack
     * @return
     */


    private byte[] getContent(byte[] bs, int currentPack) {
        byte[] xxx = new byte[20];
        xxx[0] = Integer.valueOf(0xc2).byteValue();
        xxx[1] = Integer.valueOf(00).byteValue();
        //获取总包数 = total+1
        int total = bs.length / 14;
        if (total > 3) {
            xxx[2] = Integer.valueOf(0x0D).byteValue();
            xxx[3] = Integer.valueOf(0x04).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(0x01).byteValue();
            System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, 14 * currentPack - 1);
        } else {
            if (currentPack * 14 > bs.length) {
                xxx[2] = Integer.valueOf(bs.length - (currentPack - 1) * 14).byteValue();
            } else {
                xxx[2] = Integer.valueOf(0x0D).byteValue();
            }
            xxx[3] = Integer.valueOf(total + 1).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(01).byteValue();
            if (bs.length > 14 * currentPack) {
                System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, 14);
            } else {
                System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, bs.length - (14 * (currentPack - 1)));
            }
        }
        return xxx;
    }

    /**
     * 判断数组的包数，，，
     *
     * @param current
     * @param total
     * @return
     */
    private boolean isExit(int current, int total) {
        float a = (float) total / 14;
        //超过4包就退出
        if (current > 4) {
            return false;
        }
        //不足4包的时候，当已发送完就退出
        if (current >= a + 1) {
            return false;
        }

        return true;
    }


    //当系统通知被删掉后出发回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    /**
     * H9推送日程消息
     *
     * @param content   内容
     * @param date      时间
     * @param countType
     * @param count     目前日程titile 固定为Schedule
     */
    public void sendCalendar(String content, String date, byte countType, int count) {
        CalanderPush calanderPushTitle = null, calendarPushContent = null, calendarPushDate = null;
        try {
            calanderPushTitle = new CalanderPush(commandResultCallbackAll);
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                calendarPushContent = new CalanderPush(commandResultCallbackAll, CalanderPush.CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                calendarPushDate = new CalanderPush(commandResultCallbackAll, CalanderPush.DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MsgCountPush countPush = new MsgCountPush(commandResultCallbackAll, countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        Log.i(TAG, "calanderPushTitle=" + calanderPushTitle + "calendarPushContent=" + calendarPushContent + "calendarPushDate=" + calendarPushDate);
        sendList.add(calanderPushTitle);
        if (calendarPushContent != null) {
            sendList.add(calendarPushContent);
        }
        if (calendarPushDate != null) {
            sendList.add(calendarPushDate);
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
    }

    /**
     * h9推送社交消息
     *
     * @param from       联系人
     * @param content    内容
     * @param date       data_time  (格式为年月日‘T’时分秒)
     * @param socialType 类型  FACEBOOK TWITTER INSTAGRAM QQ WECHAT WHATSAPP LINE SKYPE
     * @param count      数量
     */
    private void sendSocialCommands(String from, String content, String date, byte socialType, int count, byte countType) {
        SocialPush pushType = null, pushName = null, pushContent = null, pushDate = null;
        try {
            // 发送社交内容
            // 1、社交平台(QQ等)
            // 2、名称
            // 3、内容
            // 4、时间
            // 5、推送条数

            pushType = new SocialPush(commandResultCallbackAll, socialType);

            if (!TextUtils.isEmpty(from)) {
                byte[] bName = from.getBytes("utf-8");
                pushName = new SocialPush(commandResultCallbackAll, SocialPush.NAME_TYPE, bName);
            }
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                pushContent = new SocialPush(commandResultCallbackAll, SocialPush.CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                pushDate = new SocialPush(commandResultCallbackAll, SocialPush.DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        MsgCountPush countPush = new MsgCountPush(commandResultCallbackAll, (byte) 0x08, (byte) count);
        MsgCountPush countPush = new MsgCountPush(commandResultCallbackAll, (byte) countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        sendList.add(pushType);
        if (pushName != null) {
            sendList.add(pushName);
        }
        if (pushContent != null) {
            sendList.add(pushContent);
        }
        if (pushDate != null) {
            sendList.add(pushDate);
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
    }

    private void sendMsgW30S(String h9Msg, int type) {

        try {
            Log.e(TAG, "----w30s---" + newmsg + "---w30sMsg-=" + h9Msg + "---=" + type);
            boolean w30sswitch_skype = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Skype", false);
            boolean w30sswitch_whatsApp = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_WhatsApp", false);
            boolean w30sswitch_facebook = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Facebook", false);
            boolean w30sswitch_linkendIn = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_LinkendIn", false);
            boolean w30sswitch_twitter = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Twitter", false);
            boolean w30sswitch_viber = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Viber", false);
            boolean w30sswitch_line = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_LINE", false);
            boolean w30sswitch_weChat = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_WeChat", false);
            boolean w30sswitch_qq = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_QQ", false);
            boolean w30sswitch_msg = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Msg", false);
            boolean w30sswitch_Phone = (boolean) SharedPreferencesUtils.getParam(AlertService.this, "w30sswitch_Phone", false);

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
                case W37Constance.NotifaceMsgLine: //line
                    if (w30sswitch_line) sendW30SApplicationMsg(h9Msg, type);
                    break;
                case W37Constance.NotifaceMsgLink: //linked
                    if (w30sswitch_linkendIn) sendW30SApplicationMsg(h9Msg, type);
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
            W37DataAnalysis.getW37DataAnalysis().sendAppalertData(h9Msg, type);
        }
    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

        }

        @Override
        public void onFail(BaseCommand baseCommand) {

        }
    };


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    BaseCommand.CommandResultCallback commandResultCallbackAll = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

        }

        @Override
        public void onFail(BaseCommand baseCommand) {

        }
    };

}
