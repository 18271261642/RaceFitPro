package com.example.bozhilun.android.h9.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class H9StepDBModel {
    @Id(autoincrement = true)
    private Long id;
    private String rec;//日期     如：2018-12-20
    private String dateTime;//日期     如：2018-12-20 10:00
    private int stepNumber;//步数
    private String devicesCode;//mac
    private String userId;//用户UserId
    @Generated(hash = 1415524595)
    public H9StepDBModel(Long id, String rec, String dateTime, int stepNumber,
            String devicesCode, String userId) {
        this.id = id;
        this.rec = rec;
        this.dateTime = dateTime;
        this.stepNumber = stepNumber;
        this.devicesCode = devicesCode;
        this.userId = userId;
    }
    @Generated(hash = 1670505418)
    public H9StepDBModel() {
    }
    public String getDateTime() {
        return this.dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public int getStepNumber() {
        return this.stepNumber;
    }
    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRec() {
        return this.rec;
    }
    public void setRec(String rec) {
        this.rec = rec;
    }
}
