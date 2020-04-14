package com.example.bozhilun.android.w30s.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;


/**
 * Created by Administrator on 2018/7/10.
 */

public class W30sPhoneBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "W30sPhoneBroadCastRecei";
    String w30SBleName;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        w30SBleName = (String)  SharedPreferencesUtils.readObject(MyApp.getInstance(),  Commont.BLENAME);
        Log.e(TAG,"---------action-="+action+"--=w30SBleName="+w30SBleName);
        String resultData = this.getResultData();
        Log.e(TAG,"---------resultData-="+resultData);
        if(action != null){
            if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                // 去电，可以用定时挂断
                // 双卡的手机可能不走这个Action
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.e(TAG, "----PhoneStateReceiver EXTRA_PHONE_NUMBER: " + phoneNumber);
            } else {
                // 来电去电都会走
                // 获取当前电话状态
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.e(TAG, "-----PhoneStateReceiver onReceive state: " + state);

                // 获取电话号码
                String extraIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.e(TAG, "---PhoneStateReceiver onReceive extraIncomingNumber: " + extraIncomingNumber);

                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.e(TAG, "----PhoneStateReceiver onReceive endCall");

                }

                TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }


    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            Log.e(TAG,"------phoneStateListener-="+state+"---phoneNumber="+phoneNumber);
            switch(state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e(TAG, "---挂断");
                    handler.sendEmptyMessage(1002);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e(TAG, "---接听");
                    handler.sendEmptyMessage(1002);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "-----来电---"+phoneNumber);
                    if(!WatchUtils.isEmpty(phoneNumber) && !WatchUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")){
                        Message message = new Message();
                        message.what = 1001;
                        message.obj = phoneNumber;
                        handler.sendMessage(message);

                    }
                    break;
            }

        }
    };

   @SuppressLint("HandlerLeak")
   Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case 1001:   //来电
                   String phNumber = (String) msg.obj;
               Log.e(TAG,"-------hand--="+phNumber);
                  // MyApp.getmW30SBLEManage().notifacePhone(phNumber,0x01);
                   getPhoneContacts(phNumber);
                   break;
               case 1002:
                   Log.e(TAG,"------hand--挂断电话");
                   MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
                   break;

           }
       }
   };

    private void getNameByPhone(String phNumber){
        //Log.e(TAG,"---getNameByPhone-=="+phNumber);
        boolean isPhoneNum = true; //判断通讯录中是否保存了联系人
        //得到ContentResolver对象
        ContentResolver cr = MyApp.getInstance().getApplicationContext().getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor != null && !WatchUtils.isEmpty(cursor.getCount()+"") && cursor.getCount()>0){
//            for(int i = 0;i<cursor.getCount();i++){
//                cursor.moveToPosition(i);
//                //取得联系人名字
//                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
//                String contact = cursor.getString(nameFieldColumnIndex);
//                //Log.e(TAG,"---contact="+contact);
//                //取得电话号码
//                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
//
//
//
//
//
//            }
//



            //向下移动光标
            while(cursor.moveToNext())
            {
                //取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                String contact = cursor.getString(nameFieldColumnIndex);
                //Log.e(TAG,"---contact="+contact);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                if(phone != null && !WatchUtils.isEmpty(phone.getCount()+"") && phone.getCount()>0){
                    while(phone.moveToNext())
                    {
                        String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!WatchUtils.isEmpty(PhoneNumber)){
                            //格式化手机号
                            PhoneNumber = PhoneNumber.replace("-","");
                            PhoneNumber = PhoneNumber.replace(" ","");
                            // Log.e(TAG,"---PhoneNumber="+PhoneNumber);
                            if(phNumber.equals(PhoneNumber)){
                                isPhoneNum = false;
                                int nameFieldColumnIndexs = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                                String contacts = cursor.getString(nameFieldColumnIndexs)+"";
                                MyApp.getInstance().getmW30SBLEManage().notifacePhone(contacts,0x01);

                            }
                        }

                    }
                }
//                //通讯录中无保存联系人时，直接发送电话号码
//                if(isPhoneNum){
//                    MyApp.getmW30SBLEManage().notifacePhone(phNumber,0x01);
//                }

            }
        }
        //通讯录为空时直接发送电话号码
        if(isPhoneNum){
            MyApp.getInstance().getmW30SBLEManage().notifacePhone(phNumber,0x01);
        }
    }



    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };


    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts(String pName) {
        boolean isPhone = true;
        Log.e(TAG,"----pname-="+pName);
        ContentResolver resolver = MyApp.getInstance().getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                Log.e(TAG,"----ph--="+phoneNumber);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (!TextUtils.isEmpty(phoneNumber)){
                    isPhone = false;
                    phoneNumber = phoneNumber.replace("-","");
                    phoneNumber = phoneNumber.replace(" ","");
                    if(pName.equals(phoneNumber)){
                        String contactNames = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX)+"";
                        MyApp.getInstance().getmW30SBLEManage().notifacePhone(contactNames+pName,0x01);
                        return;
                    }
                }
                Log.e(TAG,"-----phoneNum-="+phoneNumber);
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                Log.e(TAG,"----contactName--="+contactName);

            }
            if(isPhone){
                MyApp.getInstance().getmW30SBLEManage().notifacePhone(pName,0x01);
            }

            phoneCursor.close();
        }else{
            MyApp.getInstance().getmW30SBLEManage().notifacePhone(pName,0x01);
        }
    }

}
