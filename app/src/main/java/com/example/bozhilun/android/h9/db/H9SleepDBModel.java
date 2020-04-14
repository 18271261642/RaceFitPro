package com.example.bozhilun.android.h9.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class H9SleepDBModel {
    @Id(autoincrement = true)
    private Long id;
    private int dayCount;//今日一共几次睡眠，图标只显示最后一次的
    private String rec;//日期     如：2018-12-20
    private String dateTime;//日期     如：2018-12-20 10:00
    private String sleepTime;//日期     如：10:00
    private int sleepType;//类型
    private String devicesCode;//mac
    private String userId;//用户UserId
    @Generated(hash = 1562191612)
    public H9SleepDBModel(Long id, int dayCount, String rec, String dateTime,
            String sleepTime, int sleepType, String devicesCode, String userId) {
        this.id = id;
        this.dayCount = dayCount;
        this.rec = rec;
        this.dateTime = dateTime;
        this.sleepTime = sleepTime;
        this.sleepType = sleepType;
        this.devicesCode = devicesCode;
        this.userId = userId;
    }
    @Generated(hash = 1935534845)
    public H9SleepDBModel() {
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
    public String getSleepTime() {
        return this.sleepTime;
    }
    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }
    public int getSleepType() {
        return this.sleepType;
    }
    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
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
    public int getDayCount() {
        return this.dayCount;
    }
    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }
   
}
