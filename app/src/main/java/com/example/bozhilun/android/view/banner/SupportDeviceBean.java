package com.example.bozhilun.android.view.banner;

/**
 * Created by Admin
 * Date 2020/4/27
 */
public class SupportDeviceBean {


    /**
     * deviceName : W30
     * deviceUrl : http://122.51.27.32/data/img/W30.png
     * deviceDetailUrl :
     */

    private String deviceName;
    private String deviceUrl;
    private String deviceDetailUrl;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceUrl() {
        return deviceUrl;
    }

    public void setDeviceUrl(String deviceUrl) {
        this.deviceUrl = deviceUrl;
    }

    public String getDeviceDetailUrl() {
        return deviceDetailUrl;
    }

    public void setDeviceDetailUrl(String deviceDetailUrl) {
        this.deviceDetailUrl = deviceDetailUrl;
    }

    @Override
    public String toString() {
        return "SupportDeviceBean{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceUrl='" + deviceUrl + '\'' +
                ", deviceDetailUrl='" + deviceDetailUrl + '\'' +
                '}';
    }
}
