package com.example.bozhilun.android.w30s.ble;

import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;

/**
 * Created by Admin
 * Date 2019/7/5
 */
public interface BleDeviceDataListener {

    void callBleDeviceData(W30SDeviceData deviceData);
}
