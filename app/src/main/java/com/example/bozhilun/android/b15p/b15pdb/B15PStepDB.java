package com.example.bozhilun.android.b15p.b15pdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class B15PStepDB {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private String stepData;
    private String stepTime;
    private int stepItemNumber;
    private int isUpdata;

    @Generated(hash = 1357827274)
    public B15PStepDB(Long _id, String devicesMac, String stepData, String stepTime,
            int stepItemNumber, int isUpdata) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.stepData = stepData;
        this.stepTime = stepTime;
        this.stepItemNumber = stepItemNumber;
        this.isUpdata = isUpdata;
    }

    @Generated(hash = 1789943282)
    public B15PStepDB() {
    }

    public String getDevicesMac() {
        return devicesMac;
    }

    public void setDevicesMac(String devicesMac) {
        this.devicesMac = devicesMac;
    }

    public String getStepData() {
        return stepData;
    }

    public void setStepData(String stepData) {
        this.stepData = stepData;
    }

    public String getStepTime() {
        return stepTime;
    }

    public void setStepTime(String stepTime) {
        this.stepTime = stepTime;
    }

    public int getStepItemNumber() {
        return stepItemNumber;
    }

    public void setStepItemNumber(int stepItemNumber) {
        this.stepItemNumber = stepItemNumber;
    }

    public int getIsUpdata() {
        return isUpdata;
    }

    public void setIsUpdata(int isUpdata) {
        this.isUpdata = isUpdata;
    }

    @Override
    public String toString() {
        return "B15PStepDB{" +
                "devicesMac='" + devicesMac + '\'' +
                ", stepData='" + stepData + '\'' +
                ", stepTime='" + stepTime + '\'' +
                ", stepItemNumber=" + stepItemNumber +
                ", isUpdata=" + isUpdata +
                '}';
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }
}
