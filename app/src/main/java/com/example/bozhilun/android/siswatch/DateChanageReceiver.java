package com.example.bozhilun.android.siswatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;

/**
 * Created by Administrator on 2017/8/4.
 */

/**
 * 接收时间变化的广播
 */
public class DateChanageReceiver extends BroadcastReceiver {

    private static final String TIME_CHANAGE_ACTION = Intent.ACTION_TIME_CHANGED;
    private static final String DATE_CHANAGE_ACTION = Intent.ACTION_DATE_CHANGED;
    private static final String TIMEZONE_CHANGED_ACTION = Intent.ACTION_TIMEZONE_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            //时间变化
            if(TIME_CHANAGE_ACTION.equals(action)
                    ||DATE_CHANAGE_ACTION.equals(action)
                    || TIMEZONE_CHANGED_ACTION.equals(action) ){
                //同步手表的时间
                syncH8Time();

            }

        }
    }
    //同步时间
    private void syncH8Time() {
        if(MyCommandManager.DEVICENAME != null){
            MyApp.getInstance().h8BleManagerInstance().syncH8Time(WatchConstants.syncwatchtime());
        }
    }
}
