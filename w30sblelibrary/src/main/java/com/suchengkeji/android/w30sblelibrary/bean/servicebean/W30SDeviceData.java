package com.suchengkeji.android.w30sblelibrary.bean.servicebean;


import java.io.Serializable;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/8 18:05
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SDeviceData  implements Serializable {
    private int DevicePower;//设备电量
    private int DeviceType;//设备类型
    private int DeviceVersionNumber;//设备版本

    public W30SDeviceData(int devicePower, int deviceType, int deviceVersionNumber) {
        DevicePower = devicePower;
        DeviceType = deviceType;
        DeviceVersionNumber = deviceVersionNumber;
    }

    public int getDevicePower() {
        return DevicePower;
    }

    public void setDevicePower(int devicePower) {
        DevicePower = devicePower;
    }

    public int getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(int deviceType) {
        DeviceType = deviceType;
    }

    public int getDeviceVersionNumber() {
        return DeviceVersionNumber;
    }

    public void setDeviceVersionNumber(int deviceVersionNumber) {
        DeviceVersionNumber = deviceVersionNumber;
    }

    @Override
    public String toString() {
        return "W30SDeviceData{" +
                "DevicePower=" + DevicePower +
                ", DeviceType=" + DeviceType +
                ", DeviceVersionNumber=" + DeviceVersionNumber +
                '}';
    }
}
