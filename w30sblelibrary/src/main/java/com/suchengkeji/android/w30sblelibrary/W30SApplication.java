package com.suchengkeji.android.w30sblelibrary;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/6 14:53
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SApplication extends Application {
    private final static String TAG = "====>>>" + W30SApplication.class.getSimpleName();
    static W30SBLEManage instance;

    public static W30SBLEManage getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //启动W30S蓝牙服务
        instance = W30SBLEManage.getInstance(getApplicationContext());
        instance.openW30SBLEServices();

    }
}
