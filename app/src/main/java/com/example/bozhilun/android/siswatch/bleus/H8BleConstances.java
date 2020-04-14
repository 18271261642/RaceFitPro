package com.example.bozhilun.android.siswatch.bleus;


import java.util.UUID;

/**
 * H8蓝牙常量
 */
public class H8BleConstances {

    //连接成功
     public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.H8.ACTION_GATT_CONNECTED";
    //断开连接
     public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.H8.ACTION_GATT_DISCONNECTED";
    //发现服务
     final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.H8.ACTION_GATT_SERVICES_DISCOVERED";
    //返回数据
     final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.H8.ACTION_DATA_AVAILABLE";

    //H8手表服务的UUID
     static final String BZLUN_IKP_SERVER_UUID = "0000a800-0000-1000-8000-00805f9b34fb"; //server uuid
    //H8手表读的UUID
     static final String BZLUN_IKP_READ_UUID = "0000a801-0000-1000-8000-00805f9b34fb";    //read uuid
    //H8手表写的UUID
     static final String BZLUN_IKP_WRITE_UUID = "0000a802-0000-1000-8000-00805f9b34fb";   //write uuid
     private static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
     final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(HEART_RATE_MEASUREMENT);

     //自动重连搜索到设备的code
    static final int SEARCH_REQUEST_CODE = 1001;
    //开始自动连接
    static final int STARt_AUTO_CONN_CODE = 1002;
    //停止自动重连
    static final int END_AUTO_CONN_CODE = 1003;
    //连接成功第一步同步时间
    static final int SYNC_H8_TIME_CODE = 1004;
    //连接成功第二步设置系统类型
    static final int SET_SYSTEM_TYPE_CODE = 1005;
    //判断蓝牙设备是否配对code
    static final int H8_BLE_BANDSTATE_CODE = 12;

}
