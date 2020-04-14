package com.example.bozhilun.android.b15p.b15pdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class B15PAllStepDB {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private String stepDataTime;
    private int stepItemNumber;
    private int isUpdata;


    @Generated(hash = 158868833)
    public B15PAllStepDB(Long _id, String devicesMac, String stepDataTime,
            int stepItemNumber, int isUpdata) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.stepDataTime = stepDataTime;
        this.stepItemNumber = stepItemNumber;
        this.isUpdata = isUpdata;
    }

    @Generated(hash = 1598794904)
    public B15PAllStepDB() {
    }


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getDevicesMac() {
        return devicesMac;
    }

    public void setDevicesMac(String devicesMac) {
        this.devicesMac = devicesMac;
    }

    public String getStepDataTime() {
        return stepDataTime;
    }

    public void setStepDataTime(String stepDataTime) {
        this.stepDataTime = stepDataTime;
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
        return "B15PAllStepDB{" +
                "_id=" + _id +
                ", devicesMac='" + devicesMac + '\'' +
                ", stepDataTime='" + stepDataTime + '\'' +
                ", stepItemNumber=" + stepItemNumber +
                ", isUpdata=" + isUpdata +
                '}';
    }
}
