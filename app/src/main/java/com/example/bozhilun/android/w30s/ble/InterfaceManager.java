package com.example.bozhilun.android.w30s.ble;

/**
 * Created by Admin
 * Date 2019/7/3
 */
public class InterfaceManager {
    /**
     * 连接状态
     */
   BleConnStatusListener bleConnStatusListener;

    /**
     * 写入数据返回
     */
   WriteBackDataListener writeBackDataListener;
    /**
     * 设备，步数，心率，睡眠数据回调
     */
   CallDatasBackListenter callDatasBackListenter;

    /**
     * 返回设备信息
     */
    BleDeviceDataListener bleDeviceDataListener;


    W37TakePhoneInterface w37TakePhoneInterface;

    //所有数据
    AllBackDataListener allBackDataListener;


    public void setAllBackDataListener(AllBackDataListener allBackDataListener) {
        this.allBackDataListener = allBackDataListener;
    }

    public void setWriteBackDataListener(WriteBackDataListener writeBackDataListener) {
        this.writeBackDataListener = writeBackDataListener;
    }

    public void setBleConnStatusListener(BleConnStatusListener bleConnStatusListener) {
        this.bleConnStatusListener = bleConnStatusListener;
    }

    public void setCallDatasBackListenter(CallDatasBackListenter callDatasBackListenter) {
        this.callDatasBackListenter = callDatasBackListenter;
    }

    public void setBleDeviceDataListener(BleDeviceDataListener bleDeviceDataListener) {
        this.bleDeviceDataListener = bleDeviceDataListener;
    }

    public void setW37TakePhoneInterface(W37TakePhoneInterface w37TakePhoneInterface) {
        this.w37TakePhoneInterface = w37TakePhoneInterface;
    }

}
