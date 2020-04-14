package com.example.bozhilun.android.bzlmaps.mapdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LatLonBean {
    @Id
    private int type;
    private String saveTime;
    private String mac;
    private String rtc;
    private String userId;
    private String latLons;
    @Generated(hash = 1023704645)
    public LatLonBean(int type, String saveTime, String mac, String rtc,
            String userId, String latLons) {
        this.type = type;
        this.saveTime = saveTime;
        this.mac = mac;
        this.rtc = rtc;
        this.userId = userId;
        this.latLons = latLons;
    }
    @Generated(hash = 639019188)
    public LatLonBean() {
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getSaveTime() {
        return this.saveTime;
    }
    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getRtc() {
        return this.rtc;
    }
    public void setRtc(String rtc) {
        this.rtc = rtc;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getLatLons() {
        return this.latLons;
    }
    public void setLatLons(String latLons) {
        this.latLons = latLons;
    }

    @Override
    public String toString() {
        return "LatLonBean{" +
                "type=" + type +
                ", saveTime='" + saveTime + '\'' +
                ", mac='" + mac + '\'' +
                ", rtc='" + rtc + '\'' +
                ", userId='" + userId + '\'' +
                ", latLons='" + latLons + '\'' +
                '}';
    }
}
