package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.SmsPush;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.tjdL4.tjdmain.AppIC;
import com.tjdL4.tjdmain.contr.L4Command;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.appscomm.bluetooth.app.BluetoothSDK;


/**
 * Created by admin on 2016/9/30.
 * 短信
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSBroadcastReceiver";
    private static final String H9_NAME_TAG = "H9"; //H9手表
    private static final String H8_NAME_TAG = "bozlun";
    private static final String W30_NAME_TAG = "W30";

    private static MessageListener mMessageListener;

    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context contextS, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"--------短信广播="+action);
        try {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            Log.e(TAG, "-------收到了-短信----");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = smsMessage.getDisplayOriginatingAddress();    //
                String originatingAddress = smsMessage.getOriginatingAddress();
                String contentMSG = smsMessage.getMessageBody();
                long date = smsMessage.getTimestampMillis();
                Date timeDate = new Date(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(timeDate);
                Log.e(TAG, "----------------" + contentMSG + "===" + sender + "==" + originatingAddress);
                sendMsg(contentMSG, sender);
                //如果短信来自5556,不再往下传递
                if ("5556".equals(sender)) {
                    System.out.println(" abort ");
                    abortBroadcast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    String phoneName = "";
    String people = "";

    public void sendMsg(String msg, String sender) {
//        Log.e(TAG, "-------收到了-短信--内容--" + "------msg---" + msg + "--send-" + sender);
        String bleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if (!WatchUtils.isEmpty(bleName)) {
            String sNumber = callPhoneNumber(sender);
//            people = sender;
//            if (sender.length() == 11) {
//                phoneName = getPeopleNameFromPerson(sender);
//                //getPeople(sender.substring(0, sender.length()));//电话转联系人
//            } else if (sender.length() == 13) {
//                phoneName = getPeopleNameFromPerson(sender.substring(2,sender.length()));
//            } else if (sender.length() == 14) {
//                phoneName = getPeopleNameFromPerson(sender.substring(3,sender.length()));
//            }
////            String people = getPeople(sender.substring(3, sender.length()));//电话转联系人
//            if (WatchUtils.isEmpty(phoneName)) {
//                phoneName = people;
//            }
            if (bleName.equals(H8_NAME_TAG)) { //H8手表
                String h8OnorOff = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "messagealert", "");
                if (!WatchUtils.isEmpty(h8OnorOff) && h8OnorOff.equals("on")) {
                    EventBus.getDefault().post(new MessageEvent("smsappalert"));
                }
            }
//            else if (bleName.equals("HR")) { //B18I手环
////                boolean b18iOnorOff = (boolean) SharedPreferencesUtils.readObject(MyApp.getContext().getApplicationContext(), "SMS");
////                if (b18iOnorOff) {
////                    BluetoothSDK.sendSMS(B18iResultCallBack.getB18iResultCallBack(), "SMS" + "-" + sender, msg, new Date(), getNewSmsCount(MyApp.getContext()) + 1);
////                }
//            }
            //H9
            else if (bleName.equals(H9_NAME_TAG)) {
                boolean b18iOnorOff = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext().getApplicationContext(), "H9_SMS", false);
                Log.d(TAG, "---b18iOnorOff--" + b18iOnorOff);
                if (b18iOnorOff) {
                    if (msg.length() >= 15) {
                        msg = msg.substring(0, 15) + "...";
                    }
                    Log.d(TAG, "============" + people);
                    sendSmsCommands(sNumber, msg, B18iUtils.H9TimeData(), MsgCountPush.SMS_MSG_TYPE, 1);
                }
            }
//            else if (bleName.equals("B15P")) {
//                int pushMsg_Sms = AppIC.SData().getIntData("pushMsg_Sms");
//                if (pushMsg_Sms == 1) {   //B15P
//                    L4Command.SendSMSInstruction(sNumber, msg);
//                }
//            }
            //W30设备
            else if (bleName.equals(W30_NAME_TAG)) {

//            people = sender;
//            if (sender.length() == 11) {
//                phoneName = getPeopleNameFromPerson(sender);
//                //getPeople(sender.substring(0, sender.length()));//电话转联系人
//            } else if (sender.length() == 13) {
//                phoneName = getPeopleNameFromPerson(sender.substring(2,sender.length()));
//            } else if (sender.length() == 14) {
//                phoneName = getPeopleNameFromPerson(sender.substring(3,sender.length()));
//            }
////            String people = getPeople(sender.substring(3, sender.length()));//电话转联系人
//            if (WatchUtils.isEmpty(phoneName)) {
//                phoneName = people;
//            }
                boolean w30sswitch_msg = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Msg", true);
                if (w30sswitch_msg) {
//                    Log.e(TAG, "=====w30SBleName=======" + w30SBleName + "===" + w30sswitch_msg);
                    if (!WatchUtils.isEmpty(sNumber)) {
                        MyApp.getInstance().getmW30SBLEManage().notifacePhone(sNumber + ":" + msg, W30SBLEManage.NotifaceMsgMsg);
                    } else {
                        MyApp.getInstance().getmW30SBLEManage().notifacePhone(msg, W30SBLEManage.NotifaceMsgMsg);
                    }
                }
            }
        }


    }

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

    /**
     * 短信
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
//            Log.d(TAG, "---baseCommand--" + baseCommand.toString());
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
//            Log.e("HH9", "---onFail--");
        }
    };


    //发送广播
    public final static String ACTION_DATA = "msg";

    public void sendMsg(String key, String object, Context context) {
        Intent intet = new Intent(ACTION_DATA);
        intet.putExtra("pack", key);
        intet.putExtra("msg", object);
        context.sendBroadcast(intet);
    }

    public class MessageListenerImpl implements MessageListener {

        @Override
        public void OnReceived(String message) {

        }
    }

    // 回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }

    /**
     * 获取未读短信
     *
     * @return
     */
    private int getNewSmsCount(Context context) {
        int result = 0;
        Cursor csr = context.getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }


    public String getPeople(String mNumber) {
        String name = "";
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                /*ContactsContract.CommonDataKinds.Phone.NUMBER*/};
        if (projection.length > 0) {
            Cursor cursor = MyApp.getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + mNumber + "'",
                    null,
                    null);
            if (cursor == null) {
                return "";
            }
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                name = cursor.getString(nameFieldColumnIndex);
//                Log.i(TAG, "lanjianlong" + name + " .... " + nameFieldColumnIndex); // 这里提示 force close
                break;
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return name;
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

}