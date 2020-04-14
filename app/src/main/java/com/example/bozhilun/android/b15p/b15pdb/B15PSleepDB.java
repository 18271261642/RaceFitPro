package com.example.bozhilun.android.b15p.b15pdb;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class B15PSleepDB  {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private String sleepData;
    private String sleepTime;
    private String startTime;
    private String endTime;
    private String sleepType;
    private int isUpdata;


    @Generated(hash = 1625280120)
    public B15PSleepDB(Long _id, String devicesMac, String sleepData,
            String sleepTime, String startTime, String endTime, String sleepType,
            int isUpdata) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.sleepData = sleepData;
        this.sleepTime = sleepTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sleepType = sleepType;
        this.isUpdata = isUpdata;
    }

    @Generated(hash = 270520)
    public B15PSleepDB() {
    }


    public String getDevicesMac() {
        return devicesMac;
    }

    public void setDevicesMac(String devicesMac) {
        this.devicesMac = devicesMac;
    }

    public String getSleepData() {
        return sleepData;
    }

    public void setSleepData(String sleepData) {
        this.sleepData = sleepData;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSeepTime(String seepTime) {
        this.sleepTime = seepTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSleepType() {
        return sleepType;
    }

    public void setSleepType(String sleepType) {
        this.sleepType = sleepType;
    }

    public int getIsUpdata() {
        return isUpdata;
    }

    public void setIsUpdata(int isUpdata) {
        this.isUpdata = isUpdata;
    }

    @Override
    public String toString() {
        return "B15PSleepDB{" +
                "devicesMac='" + devicesMac + '\'' +
                ", sleepData='" + sleepData + '\'' +
                ", seepTime='" + sleepTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", sleepType='" + sleepType + '\'' +
                ", isUpdata=" + isUpdata +
                '}';
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }
}
