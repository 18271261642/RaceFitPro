package com.example.bozhilun.android.w30s.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Administrator on 2018/7/3.
 */

public class ScreenReceiverUtils {

    private static final String TAG = "ScreenReceiverUtils";

    private Context mContext;
    private SreenBroadcastReceiver mScreenReceiver;
    public SreenStateListener sreenStateListener;

    public ScreenReceiverUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void setSreenStateListener(SreenStateListener sreenStateListener) {
        this.sreenStateListener = sreenStateListener;
        this.mScreenReceiver = new SreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver,filter);

    }

    public void stopScreenReceiverListener(){
        mContext.unregisterReceiver(mScreenReceiver);
    }

    //广播
    private class SreenBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"----recever="+action);
            if(action != null && sreenStateListener != null){
                if(action.equals(Intent.ACTION_SCREEN_ON)){ //开屏
                    sreenStateListener.onSreenOn();
                }
                if(action.equals(Intent.ACTION_SCREEN_OFF)){    //关屏
                    sreenStateListener.onSreenOff();
                }
                if(action.equals(Intent.ACTION_USER_PRESENT)){  //解锁
                    sreenStateListener.onUserPresent();
                }
            }
        }
    }

    // 监听sreen状态对外回调接口
    public interface SreenStateListener {
        void onSreenOn();
        void onSreenOff();
        void onUserPresent();
    }
}
