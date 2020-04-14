package com.example.bozhilun.android.h9.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.sdk.bluetooth.bean.BluetoothScanDevice;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.interfaces.BluetoothManagerScanListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

public class H9ContentUtils
        implements BluetoothManagerScanListener, BluetoothManagerDeviceConnectListener {
    private static final String TAG = "H9ContentUtils";
    private static H9ContentUtils daoSession = null;
    private Context mContext = null;
    public final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
    boolean isSCanTrue = false;

    public static H9ContentUtils getContent(Context mContext) {
        if (daoSession == null) {
            synchronized (H9ContentUtils.class) {
                if (daoSession == null) {
                    daoSession = new H9ContentUtils(mContext);
                }
            }
        }
        return daoSession;
    }


    private H9ContentUtils(Context mContext) {

        this.mContext = mContext;
        //添加扫描监听
        AppsBluetoothManager.getInstance(mContext).addBluetoothManagerScanListeners(this);
        //添加链接监听
        AppsBluetoothManager.getInstance(mContext).addBluetoothManagerDeviceConnectListener(this);
    }

    public void removeListenter() {
        AppsBluetoothManager.getInstance(mContext).removeBluetoothManagerScanListeners(this);
        AppsBluetoothManager.getInstance(mContext).removeBluetoothManagerDeviceConnectListeners(this);
    }

    /**
     * 扫描
     *
     * @param scanDevice
     */
    @Override
    public void onDeviceFound(BluetoothScanDevice scanDevice) {
        if (mContext == null) mContext = MyApp.getContext();
        if (scanDevice != null && scanDevice.getBluetoothDevice() != null) {
            BluetoothDevice bluetoothDevice = scanDevice.getBluetoothDevice();
            String h9Mac = (String) SharedPreferencesUtils.readObject(mContext, Commont.BLEMAC);
            if (bluetoothDevice != null && !WatchUtils.isEmpty(bluetoothDevice.getAddress()) && bluetoothDevice.getAddress().equals(h9Mac)) {
                isSCanTrue = true;
                AppsBluetoothManager.getInstance(mContext).connectDevice(h9Mac);
                AppsBluetoothManager.getInstance(mContext).cancelDiscovery();//取消扫描
            }
        }
    }

    @Override
    public void onDeviceScanEnd() {
        if (mContext == null) mContext = MyApp.getContext();
        /**
         * 扫描停止了还没扫描链接到-----再次扫描来凝结
         */
        if (!firstInspect() && !isSCanTrue) {
            String h9Mac = (String) SharedPreferencesUtils.readObject(mContext, Commont.BLEMAC);
            Log.e("H9Home", "---h9mac--" + h9Mac);
            if (!WatchUtils.isEmpty(h9Mac)) {
                AppsBluetoothManager.getInstance(mContext).startDiscovery();
            }
        }
    }


    /**
     * 链接
     *
     * @param bluetoothDevice
     */
    @Override
    public void onConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onConnectFailed() {
        Log.e(TAG, "-------onConnectFailed-----连接失败-");
        if (mContext == null) mContext = MyApp.getContext();
        MyCommandManager.DEVICENAME = null;
        Intent intent = new Intent();
        intent.setAction(H9CONNECT_STATE_ACTION);
        intent.putExtra("h9constate", "disconn");
        mContext.sendBroadcast(intent);  //发送连接失败的广播
    }

    @Override
    public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
        Log.e(TAG, "-------onEnableToSendComand-----连接绑定成功-");
        if (mContext == null) mContext = MyApp.getContext();
        MyCommandManager.DEVICENAME = "H9";
        AppsBluetoothManager.getInstance(mContext).cancelDiscovery();//取消扫描
        BluetoothConfig.setDefaultMac(mContext, bluetoothDevice.getAddress());
        SharedPreferencesUtils.saveObject(mContext, Commont.BLENAME, "H9");
        SharedPreferencesUtils.saveObject(mContext, Commont.BLEMAC, bluetoothDevice.getAddress());


        Intent intent = new Intent();
        intent.setAction(H9CONNECT_STATE_ACTION);
        intent.putExtra("h9constate", "conn");
        mContext.sendBroadcast(intent);   //发送连接成功的广播
        isSCanTrue = false;
    }

    @Override
    public void onConnectDeviceTimeOut() {
        if (mContext == null) mContext = MyApp.getContext();
        MyCommandManager.DEVICENAME = null;
        Intent intent = new Intent();
        intent.setAction(H9CONNECT_STATE_ACTION);
        intent.putExtra("h9constate", "disconn");
        mContext.sendBroadcast(intent);  //发送连接失败的广播
        /**
         * 链接超时了，重新扫描链接
         */
        if (!firstInspect()) {
            isSCanTrue = false;
            String h9Mac = (String) SharedPreferencesUtils.readObject(mContext, Commont.BLEMAC);
            Log.e("H9Home", "---h9mac--" + h9Mac);
            if (!WatchUtils.isEmpty(h9Mac)) {

                AppsBluetoothManager.getInstance(mContext).startDiscovery();
            }
        }
    }


    /**
     * 检查链接状态
     */
    public boolean firstInspect() {
        boolean isConnted = false;
        if (MyCommandManager.DEVICENAME != null) {
            isConnted = true;
        }
        return isConnted;
    }

}
