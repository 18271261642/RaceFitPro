package com.example.bozhilun.android.h9.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class H9HeartDBModel {
    @Id(autoincrement = true)
    private Long id;
    private String rec;//日期     如：2018-12-20
    private String dateTime;//日期     如：2018-12-20 10:00
    private int heartValue;//心率
    private String devicesCode;//mac
    private String userId;//用户UserId
    @Generated(hash = 1561442579)
    public H9HeartDBModel(Long id, String rec, String dateTime, int heartValue,
            String devicesCode, String userId) {
        this.id = id;
        this.rec = rec;
        this.dateTime = dateTime;
        this.heartValue = heartValue;
        this.devicesCode = devicesCode;
        this.userId = userId;
    }
    @Generated(hash = 356190118)
    public H9HeartDBModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDateTime() {
        return this.dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public int getHeartValue() {
        return this.heartValue;
    }
    public void setHeartValue(int heartValue) {
        this.heartValue = heartValue;
    }
    public String getDevicesCode() {
        return this.devicesCode;
    }
    public void setDevicesCode(String devicesCode) {
        this.devicesCode = devicesCode;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRec() {
        return this.rec;
    }
    public void setRec(String rec) {
        this.rec = rec;
    }
}
