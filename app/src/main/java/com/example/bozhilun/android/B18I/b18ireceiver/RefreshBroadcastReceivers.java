package com.example.bozhilun.android.B18I.b18ireceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.bleutil.MyCommandManager;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/12/8 09:21
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class RefreshBroadcastReceivers extends BroadcastReceiver {

    public static MyCallBack myCallBack;

    public static void setMyCallBack(MyCallBack myCallBack) {
        try {
            RefreshBroadcastReceivers.myCallBack = myCallBack;
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public interface MyCallBack {
        void setMyCallBack(Message msg);
    }

    private static Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (myCallBack != null) myCallBack.setMyCallBack(msg);
            } catch (Exception e) {
                e.getMessage();
            }
            return false;
        }
    });


    public static final int MessageNumber = 0x0d;

    public static Handler getMyHandler() {
        return myHandler;
    }

    private static RefreshBroadcastReceivers mInstance;

    public static RefreshBroadcastReceivers getInstance() {
        if (mInstance == null) {
            synchronized (RefreshBroadcastReceivers.class) {
                if (mInstance == null) {
                    mInstance = new RefreshBroadcastReceivers();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("-------", "同步数据广播提醒来了");
        if (MyCommandManager.DEVICENAME != null && !MyCommandManager.DEVICENAME.equals("W30")) {
            myHandler.sendEmptyMessage(MessageNumber);
        }
    }
}