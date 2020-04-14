package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchNotiBean;
import com.hplus.bluetooth.BleProfileManager;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.PhoneNamePush;
import com.sdk.bluetooth.utils.BackgroundThread;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.tjdL4.tjdmain.AppIC;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by admin on 2017/5/14.\
 * 6.0 广播接收来电
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastReceiver";
    private static final String H9_NAME_TAG = "H9";    //H9手表


    OnCallPhoneListener onClallListener;

    public void setOnClallListener(OnCallPhoneListener onClallListener) {
        this.onClallListener = onClallListener;
    }


    String phoneNumber = "";
    private String bleName;


    public PhoneBroadcastReceiver() {
        super();
        bleName = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLENAME);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        //未连接设备的状态
        if(MyCommandManager.DEVICENAME == null)
            return;
        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Log.e(TAG, "---------action---" + action);
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (WatchUtils.isEmpty(phoneNumber))
            return;
        Log.d(TAG, "---phoneNumber----" + phoneNumber);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (telephony == null)
            return;
        int state = telephony.getCallState();
        Log.d(TAG, "-----state-----" + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://"[Broadcast]等待接电话="

                if (!WatchUtils.isEmpty(phoneNumber) && !WatchUtils.isEmpty(bleName)) {
                    Log.d(TAG, "------收到了来电广播---" + phoneNumber);
                    if (bleName.equals("bozlun")) {
                        sendH8PhoneAlert();
                    }

                    if (bleName.equals(H9_NAME_TAG)) {
                        getPeople(phoneNumber, context);
                    }

                    if (bleName.equals("w30") || bleName.equals("W30") || bleName.equals("W31") || bleName.equals("W37")) {
                        boolean isOn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Phone", true);
                        if (isOn) {
                            sendPhoneAlertData(phoneNumber, "W30");
                        }
                    }
                    //维亿魄系列
                    if (WatchUtils.isVPBleDevice(bleName)) {   //B30手环
                        sendPhoneAlertData(phoneNumber, "B30");
                    }

                    //腾进达方案
                    Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
                    if (set.contains(bleName)) {
                        int pushMsg_call = AppIC.SData().getIntData("pushMsg_call");
                        if (pushMsg_call == 1) {
                            L4Command.SendCallInstruction(phoneNumber);
                        }
                    }

                    //B18
                    if(bleName.equals("B18") || bleName.equals("B16") || bleName.equals("B50")){
                        if(MyCommandManager.DEVICENAME != null){
                            boolean isB18 = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISPhone, false);
                            if(isB18)sendPhoneAlertData(phoneNumber, "B16");
                        }
                    }

                    if(bleName.equals("XWatch") || bleName.equals("SWatch")){   //xWatch
                        xWatchNoti();
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:// "[Broadcast]挂断电话
                Log.d(TAG, "------挂断电话--");
            case TelephonyManager.CALL_STATE_OFFHOOK://"[Broadcast]通话中="
                Log.d(TAG, "------通话中--");
                if (!WatchUtils.isEmpty(bleName)) {
                    if (bleName.equals("bozlun")) {
                        missCallPhone();    //挂断电话
                    }
                    if (bleName.equals("w30") || bleName.equals("W30") || bleName.equals("W31") || bleName.equals("W37")) {
                        disW30Phone();
                    }
                    if (WatchUtils.isVPBleDevice(bleName)) {
                        setB30DisPhone();
                    }

                    if(bleName.equals("B18") || bleName.equals("B16") || bleName.equals("B50"))
                        disB18PhoneAlert();
                }
                break;
        }
    }

    //XWatch提醒
    private void xWatchNoti(){
        if(MyCommandManager.DEVICENAME == null)
            return;
        if(MyCommandManager.DEVICENAME.equals("SWatch")){
            XWatchBleAnalysis.getW37DataAnalysis().setSWatchNoti(0);
        }else{
            XWatchBleAnalysis.getW37DataAnalysis().setDeviceNoti(0);
        }

    }



    //挂断B18电话
    private void disB18PhoneAlert() {
        BleProfileManager.getInstance().getCommandController().sendClearCall(1);
    }

    //发送电话号码
    private void sendPhoneAlertData(String phoneNumber, String tag) {

        //判断是否有读取联系人和通讯录的权限
        if (!AndPermission.hasPermissions(MyApp.getContext(), Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)) {
            AndPermission.with(MyApp.getContext()).runtime().permission(Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG).start();
        } else {
            //getPhoneContacts(phoneNumber, tag);
            findPhoneContactsByNumber(phoneNumber,tag);
        }


    }

    private void disW30Phone() {
        if (MyCommandManager.DEVICENAME == null)
            return;
      //  MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
       W37DataAnalysis.getW37DataAnalysis().disPhone();


    }

    private void sendH8PhoneAlert() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().h8BleManagerInstance().setPhoneAlert();
    }


    //挂断电话
    private void missCallPhone() {
        Log.e(TAG,"-----挂断电话--------");
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().h8BleManagerInstance().canclePhoneAlert();
        if (bleName != null && !TextUtils.isEmpty(bleName)) {
            if (H9_NAME_TAG.equals(bleName)) {   //H9挂掉电话
                sendPhoneCallCommands("", "", PhoneNamePush.HangUp_Call_type, MsgCountPush.HANGUP_CALL_TYPE, 0);

            }
        }
    }

    /**
     * 通过输入获取电话号码
     */
    public void getPeople(String nunber, Context context) {
        boolean w30sswitch_Phone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Phone", true);
        Log.d(TAG, "------收到了来电广播3---" + "=====" + w30sswitch_Phone);
        try {
            Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, nunber), new String[]{
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LABEL}, null, null, null);

            if (cursor != null && !WatchUtils.isEmpty("" + cursor.getCount() + "")) {
                if (cursor.getCount() == 0) {
//                    //没找到电话号码
//                    if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
//                        if (w30sswitch_Phone) {
//                            Log.d(TAG, "------收到了来电广播4---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + nunber);
//                            MyApp.getmW30SBLEManage().notifacePhone(nunber, 0x01);
//                        }
//                        return;
//                    }
                    if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals(H9_NAME_TAG))
                        phonesig(nunber, 1);
                } else if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals(H9_NAME_TAG))
                        phonesig(cursor.getString(2), 2);
                }
            } else {
                if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals(H9_NAME_TAG))
                    phonesig(phoneNumber, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送电话号码或者昵称的方法
    public void phonesig(String num, int id) {
//        Log.d(TAG, "------num---" + num + "--id-" + id);
        if (!WatchUtils.isEmpty(bleName)) {
            try {
                if (H9_NAME_TAG.equals(bleName)) { //H9手表
//                    Log.d(TAG, "-----h9--" + bleName);
                    /**
                     * 判断H9是否打开电话提醒
                     */
                    boolean income_call = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext().getApplicationContext(), "H9_INCOME_CALL", false);
                    if (income_call) {
                        if (id == 2) {    //有名字
//                            notifyInfo = new NotifyInfo(num,phoneNumber,"");
                            Log.d(TAG, "---11--h9--" + bleName);
                            sendPhoneCallCommands(num, phoneNumber, PhoneNamePush.Incoming_Call_type, MsgCountPush.ICOMING_CALL_TYPE, 1);
                        } else {
                            Log.d(TAG, "---22--h9--" + SharedPreferencesUtils.readObject(MyApp.getContext().getApplicationContext(), "mylanya"));
                            sendPhoneCallCommands(num, num, PhoneNamePush.Incoming_Call_type, MsgCountPush.ICOMING_CALL_TYPE, 1);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 推送来电
     *
     * @param name
     * @param incomingNumber
     * @param callType
     * @param countType
     */
    private void sendPhoneCallCommands(String name, String incomingNumber, byte callType, byte countType, long delayTime) {
        byte[] bName = new byte[0x00];
        if (incomingNumber != null) {
            bName = incomingNumber.getBytes();
            if (name != null) {
                bName = name.getBytes();
            }
        }
        PhoneNamePush phonePush = new PhoneNamePush(commandResultCallback, callType, bName);
        MsgCountPush countPush = new MsgCountPush(commandResultCallback, countType, (byte) 0x01);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        sendList.add(phonePush);
        sendList.add(countPush);
        if (delayTime > 0) {
            final ArrayList<BaseCommand> commands = sendList;
            BackgroundThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(commands);
                }
            }, delayTime);
        } else {
            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
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

    public interface OnCallPhoneListener {
        void callPhoneAlert(String phoneTag);
    }

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    boolean isPhone = true;


    //根据手机号码查询通讯录的联系人姓名
    private void findPhoneContactsByNumber(String phoneName,String tag){
        Log.e(TAG,"-----phoneName="+phoneName+"--tag="+tag);
        try {
            ContentResolver resolver = MyApp.getInstance().getContentResolver();
            if(resolver == null){
                sendCommVerticalPhone(tag,"",phoneName);
                return;
            }
            // 获取手机联系人
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
            if(phoneCursor == null){
                sendCommVerticalPhone(tag,"",phoneName);
                return;
            }
            while (phoneCursor.moveToNext()) {
                //手机通讯录中的电话号码
                String conNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if(!WatchUtils.isEmpty(conNumber)){
                    if (conNumber.contains("-")) {  //去除“-”
                        conNumber = conNumber.replace("-", "");
                    }
                    if (conNumber.contains(" ")) { //去除空格
                        conNumber = conNumber.replace(" ", "");
                    }
                    if(phoneName.equals(conNumber)){    //呼入的号码和通讯录中的号码相同
                        isPhone = false;
                        //联系人姓名
                        String contactNames = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX) + "";
                        if(WatchUtils.isEmpty(contactNames)) contactNames = "";
                        //退出来电提醒，联系人姓名和电话
                        sendCommVerticalPhone(tag,contactNames,phoneName);
                        return;
                    }

                }

            }
             if (isPhone) {
                sendCommVerticalPhone(tag, "", phoneName);
            }

            phoneCursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //判断设备，发送数据
    private void sendCommVerticalPhone(String tagName,String contentStr,String phoneStr){

        Log.e(TAG,"---name="+tagName+"---=联系人="+contentStr+"---phoneNumber="+phoneStr);


        if(tagName.equals("B30")){
            setB30PhoneMsg(contentStr,phoneStr);
        }else if(tagName.equals("W30")){
            W37DataAnalysis.getW37DataAnalysis().sendAppalertData(contentStr+" "+phoneStr, 0x01);
        }
        else if(tagName.equals("B16") || tagName.equals("B50")){
            if(BleProfileManager.getInstance().isConnected()){
                BleProfileManager.getInstance().getCommandController().sendIncomingNum(phoneStr);
                if(!WatchUtils.isEmpty(contentStr))
                  BleProfileManager.getInstance().getCommandController().sendIncomingName(contentStr);
            }
        }
    }


    //发送来电指令
    private void setB30PhoneMsg(String peopleName,String resultNumber) {
        if (MyCommandManager.DEVICENAME != null) {

            boolean callPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISPhone, true);//来电
            if (!callPhone)
                return;
            ContentSetting contentSetting = new ContentPhoneSetting(ESocailMsg.PHONE, WatchUtils.isEmpty(peopleName)?null:peopleName, resultNumber);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, contentSetting);
        }

    }

    /**
     * B30电话挂断
     */
    private void setB30DisPhone() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().offhookOrIdlePhone(iBleWriteResponse);
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };



}