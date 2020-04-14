package com.example.bozhilun.android.siswatch.utils;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bozhilun.android.siswatch.bleus.H8BleConstances;
import com.example.bozhilun.android.w30s.ble.W37Constance;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;

/**
 * Created by Administrator on 2017/10/31.
 */

public class BlueAdapterUtils {

    private Context mContext;
    private static BlueAdapterUtils blueAdapterUtils;


    public static BlueAdapterUtils getBlueAdapterUtils(Context mContext) {
        if (blueAdapterUtils == null) {
            synchronized (BlueAdapterUtils.class) {
                blueAdapterUtils = new BlueAdapterUtils(mContext);
            }
        }
        return blueAdapterUtils;
    }

    private BlueAdapterUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 请求打开蓝牙
     *
     * @param activity     activiy
     * @param visiableTime 可见时间
     * @param requestCode  返回码
     */
    public void turnOnBlue(Activity activity, int visiableTime, int requestCode) {
        try {
            // 请求打开 Bluetooth
            Intent requestBluetoothOn = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn
                    .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // 设置 Bluetooth 设备可见时间
            requestBluetoothOn.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    visiableTime);
            // 请求开启 Bluetooth
            activity.startActivityForResult(requestBluetoothOn,
                    requestCode);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 注册扫描的广播
     *
     * @return
     */
    public IntentFilter scanIntFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.bozhilun.android.h9.connstate");
        intentFilter.addAction("com.example.bozhilun.android.siswatch.CHANGEPASS");
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B31_CONNECTED_ACTION);
        intentFilter.addAction(H8BleConstances.ACTION_GATT_CONNECTED);

        //舟海方案手环
        intentFilter.addAction(W37Constance.W37_DISCONNECTED_ACTION);
        intentFilter.addAction(W37Constance.W37_CONNECTED_ACTION);

        //xwatch
        intentFilter.addAction(W37Constance.X_WATCH_DISCONN_ACTION);
        intentFilter.addAction(W37Constance.X_WATCH_CONNECTED_ACTION);

        //SWatch
        intentFilter.addAction(W37Constance.S_WATCH_CONNECTED_ACTION);
        intentFilter.addAction(W37Constance.S_WATCH_DISCONN_ACTION);

        //B18
        intentFilter.addAction(WatchUtils.B18_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B18_DISCONN_ACTION);
        return intentFilter;
    }

}
