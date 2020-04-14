package com.example.bozhilun.android.bzlmaps.mapdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;


@Entity
public class SportMaps {
    private String saveTime;
    private String mac;
    private String rtc;
    private String image;
    private String temp;
    private String distance;
    private String timeLen;
    private String description;
    private String calories;
    private int type;
    private String userId;
    private String speed;
    private String pm25;
    private String startTime;
    private int ids;
    private String latLons ;    //经纬度
    @Generated(hash = 1391399399)
    public SportMaps(String saveTime, String mac, String rtc, String image,
            String temp, String distance, String timeLen, String description,
            String calories, int type, String userId, String speed, String pm25,
            String startTime, int ids, String latLons) {
        this.saveTime = saveTime;
        this.mac = mac;
        this.rtc = rtc;
        this.image = image;
        this.temp = temp;
        this.distance = distance;
        this.timeLen = timeLen;
        this.description = description;
        this.calories = calories;
        this.type = type;
        this.userId = userId;
        this.speed = speed;
        this.pm25 = pm25;
        this.startTime = startTime;
        this.ids = ids;
        this.latLons = latLons;
    }
    @Generated(hash = 448604332)
    public SportMaps() {
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
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getTemp() {
        return this.temp;
    }
    public void setTemp(String temp) {
        this.temp = temp;
    }
    public String getDistance() {
        return this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getTimeLen() {
        return this.timeLen;
    }
    public void setTimeLen(String timeLen) {
        this.timeLen = timeLen;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCalories() {
        return this.calories;
    }
    public void setCalories(String calories) {
        this.calories = calories;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSpeed() {
        return this.speed;
    }
    public void setSpeed(String speed) {
        this.speed = speed;
    }
    public String getPm25() {
        return this.pm25;
    }
    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getIds() {
        return this.ids;
    }
    public void setIds(int ids) {
        this.ids = ids;
    }
    public String getLatLons() {
        return this.latLons;
    }
    public void setLatLons(String latLons) {
        this.latLons = latLons;
    }

    @Override
    public String toString() {
        return "SportMaps{" +
                "saveTime='" + saveTime + '\'' +
                ", mac='" + mac + '\'' +
                ", rtc='" + rtc + '\'' +
                ", image='" + image + '\'' +
                ", temp='" + temp + '\'' +
                ", distance='" + distance + '\'' +
                ", timeLen='" + timeLen + '\'' +
                ", description='" + description + '\'' +
                ", calories='" + calories + '\'' +
                ", type=" + type +
                ", userId='" + userId + '\'' +
                ", speed='" + speed + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", startTime='" + startTime + '\'' +
                ", ids=" + ids +
                ", latLons='" + latLons + '\'' +
                '}';
    }
}
