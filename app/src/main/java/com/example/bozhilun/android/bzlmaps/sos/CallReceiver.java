package com.example.bozhilun.android.bzlmaps.sos;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.PhoneUtile;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.d("----------AA", "onIncomingCallReceived");//来电（接收方）
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Log.d("----------AA", "onIncomingCallAnswered");//来电接通（接收方）
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

        Log.d("----------AA", "onIncomingCallEnded");//接通后通话结束（接收方）
    }


    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        if (!WatchUtils.isEmpty(number)) Log.d("----------AA", "去点" + number);//去点未接通（发送方）
    }


    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
//        这里处理，挂断的相关操作，（发送方）
        Log.d("----------AA", "未通或挂断 onOutgoingCallEnded");//去点未接通（发送方）
        try {
            if (!WatchUtils.isEmpty(number)) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALL_LOG)
                        == PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG )
//                            == PackageManager.PERMISSION_GRANTED
                ) {
                    number = number.trim().replace(" ", "");
                    Commont.SENDPHONE_COUNT++;
                    getCallLogState(ctx, number);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d("----------AA", "onMissedCall");//来电未接通（接收方）
    }


    /**
     * 查询最后一次的通话时长---- 确定通话是否成功
     *
     * @param context
     * @param number
     * @return
     */
    private void getCallLogState(Context context, String number) {
        try {
            if (WatchUtils.isEmpty(number)) return;
            ContentResolver cr = context.getContentResolver();
            PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG);
            final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION},
                    CallLog.Calls.NUMBER + "=?",
                    new String[]{number},
                    CallLog.Calls.DATE + " desc");
            int i = 0;
            if (cursor != null)
                while (cursor.moveToNext()) {
                    if (i == 0) {//第一个记录 也就是当前这个电话的记录
                        int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
                        long durationTime = cursor.getLong(durationIndex);
                        if (durationTime > 0) {
                            Log.d("----------AA", "第一次查询 接通了   时长= " + durationTime);
                        } else {
                            Log.d("----------AA", "第一次查询 没接通");
                        }
                    }
                    i++;
                }
            if (cursor != null) cursor.close();

            Message message = new Message();
            message.what = 0x01;
            message.obj = number;
            if (handler != null) handler.sendMessageDelayed(message, 5000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 查询最后一次的通话时长---- 确定通话是否成功
     *
     * @param context
     * @param number
     * @return
     */
    private boolean getCallLogStateBoolean(Context context, String number) {
        Log.d("----------AA", "小米手机第一次查询不对，所以查询两次，根据第二次为标准");
        try {
            if (WatchUtils.isEmpty(number)) return false;
            boolean isLink = false;
            ContentResolver cr = context.getContentResolver();
            PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG);
            final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION},
                    CallLog.Calls.NUMBER + "=?",
                    new String[]{number},
                    CallLog.Calls.DATE + " desc");
            int i = 0;
            if(cursor == null)
                return false;
            while (cursor.moveToNext()) {
                if (i == 0) {//第一个记录 也就是当前这个电话的记录
                    int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
                    long durationTime = cursor.getLong(durationIndex);
                    if (durationTime > 0) {
                        Log.d("----------AA", "第二次查询 接通了   时长= " + durationTime);
                        isLink = true;
                    } else {
                        Log.d("----------AA", "第二次查询 这是else里");
                        isLink = false;
                    }
                }
                i++;
            }
            cursor.close();
            return isLink;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    //点击事件调用的类
    protected void call(final String tel) {
        try {
            //直接拨打
            Log.d("GPS", "call:" + tel);
            Uri uri = Uri.parse("tel:" + tel);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(MyApp.getInstance(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            MyApp.getInstance().startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


//    //标记位置防止多次调用onchange
//    public int getLastCallId(Context context) {
//        try {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                return -1;
//            }
//            Cursor cur = context.getContentResolver().query(outSMSUri, null, null, null, CallLog.Calls.DATE + " desc");
//            cur.moveToFirst();
//            int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
//            return lastMsgId;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return -1;
//    }

//
////    private static volatile int initialPos;
//    private static final Uri outSMSUri = CallLog.Calls.CONTENT_URI;
//    protected void queryLastCall(Context context, String address) {
//        try {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            Cursor cur = context.getContentResolver().query(outSMSUri, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);//CallLog.Calls.DATE + " desc");
//            if (cur.moveToNext()) {
////                if (initialPos != getLastCallId(context)) {
//                    if (!TextUtils.isEmpty(address)) {
//                        if (cur.getString(cur.getColumnIndex("number")).contains(address)) {
//                            int _id = cur.getInt(cur.getColumnIndex("_id"));
//                            int type = cur.getInt(cur.getColumnIndex("type"));//通话类型，1 来电 .INCOMING_TYPE；2 已拨 .OUTGOING_；3 未接 .MISSED_
//                            String number = cur.getString(cur.getColumnIndex("number"));// 电话号码
//                            int duration = cur.getInt(cur.getColumnIndex("duration"));//通话时长，单位：秒
//                            String last_modified = cur.getString(cur.getColumnIndex("last_modified"));
//
//                            String msgObj = "\nID：" + _id + "\n类型：" + type + "\n号码：" + number + "\n时长：" + duration + "====" + last_modified;
//                            Log.e("----------AA","查到的数据"+ msgObj);
//
//                            if (type == 2) {
//                                if (duration > 0) {
//                                    Log.e("----------AA", "大于");
//                                } else {
//
//                                    Log.e("----------AA", "小于");
//                                }
//                            }
//
//                        }
//                    }
////                    initialPos = getLastCallId(context);
////                }
//
//            }
//            cur.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos
            String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personOne", "").toString().trim().replace(" ", "");
            String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personTwo", "").toString().trim().replace(" ", "");
            String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personThree", "").toString().trim().replace(" ", "");
            boolean callLogStateBoolean = false;
            switch (message.what) {
                case 0x01:
                    String number1 = message.obj.toString();
                    if (!WatchUtils.isEmpty(number1)) {
                        Log.d("----------AA", "消息指" + number1);
                        handler.removeMessages(0x01);

                        if (PhoneUtile.SIMTYPE(MyApp.getInstance()) == 0) {// 国内  移动，联通
                            callLogStateBoolean = getCallLogStateBoolean(MyApp.getInstance(), number1);
                            if (!callLogStateBoolean) {//没接通状态
                                handler.obtainMessage(0x02, number1).sendToTarget();
                            } else {//接通后，后面的不在重复
                                Commont.SENDPHONE_COUNT = 4;
                                //Commont.isGPSed = false;
                                Commont.IS_RING_SOS = false;
                            }

                        } else {//---其他
                            handler.obtainMessage(0x02, number1).sendToTarget();
                        }
                    }

                    break;
                case 0x02:
                    String number = message.obj.toString();
                    if (!WatchUtils.isEmpty(number)) {
                        Log.d("----------AA", stringpersonOne + "-----" + stringpersonTwo + "=======" + stringpersonThree);
                        handler.removeMessages(0x02);
                        if (isSos && (stringpersonOne.equals(number)
                                || stringpersonTwo.equals(number)
                                || stringpersonThree.equals(number))) {
                            Log.d("----------AA", "没打通啊" + Commont.SENDPHONE_COUNT);//去点未接通
                            if (Commont.SENDPHONE_COUNT <= 2) {
                                if (stringpersonOne.equals(number)
                                        && stringpersonTwo.equals(number)
                                        && stringpersonThree.equals(number)) {
                                    call(stringpersonOne);
                                } else if (stringpersonOne.equals(number)
                                        && stringpersonTwo.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonThree)) {
                                        call(stringpersonThree);
                                    } else if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    }
                                } else if (stringpersonOne.equals(number)
                                        && stringpersonThree.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonTwo)) {
                                        call(stringpersonTwo);
                                    } else if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    }
                                } else if (stringpersonTwo.equals(number)
                                        && stringpersonThree.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    } else if (!WatchUtils.isEmpty(stringpersonThree)) {
                                        call(stringpersonThree);
                                    }
                                } else if (stringpersonOne.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonTwo)) {
                                        call(stringpersonTwo);
                                    } else if (!WatchUtils.isEmpty(stringpersonThree)) {
                                        call(stringpersonThree);
                                    } else if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    }
                                } else if (stringpersonTwo.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonThree)) {
                                        call(stringpersonThree);
                                    } else if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    } else if (!WatchUtils.isEmpty(stringpersonTwo)) {
                                        call(stringpersonTwo);
                                    }
                                } else if (stringpersonThree.equals(number)) {
                                    if (!WatchUtils.isEmpty(stringpersonOne)) {
                                        call(stringpersonOne);
                                    } else if (!WatchUtils.isEmpty(stringpersonTwo)) {
                                        call(stringpersonTwo);
                                    } else if (!WatchUtils.isEmpty(stringpersonThree)) {
                                        call(stringpersonThree);
                                    }
                                }
                            } else {
                                Commont.SENDPHONE_COUNT = 4;
                                //Commont.isGPSed = false;
                                Commont.IS_RING_SOS = false;
                            }
                        }
                    }

                    break;
            }
            return false;
        }
    });

}