package com.example.bozhilun.android.b15p.b15pdb;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class B15PBloodDB  {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private String bloodData;
    private String bloodTime;
    private int bloodNumberH;
    private int bloodNumberL;
    private int isUpdata;

    @Generated(hash = 1076682131)
    public B15PBloodDB(Long _id, String devicesMac, String bloodData,
            String bloodTime, int bloodNumberH, int bloodNumberL, int isUpdata) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.bloodData = bloodData;
        this.bloodTime = bloodTime;
        this.bloodNumberH = bloodNumberH;
        this.bloodNumberL = bloodNumberL;
        this.isUpdata = isUpdata;
    }

    @Generated(hash = 478437982)
    public B15PBloodDB() {
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

    public String getBloodData() {
        return bloodData;
    }

    public void setBloodData(String bloodData) {
        this.bloodData = bloodData;
    }

    public String getBloodTime() {
        return bloodTime;
    }

    public void setBloodTime(String bloodTime) {
        this.bloodTime = bloodTime;
    }

    public int getBloodNumberH() {
        return bloodNumberH;
    }

    public void setBloodNumberH(int bloodNumberH) {
        this.bloodNumberH = bloodNumberH;
    }

    public int getBloodNumberL() {
        return bloodNumberL;
    }

    public void setBloodNumberL(int bloodNumberL) {
        this.bloodNumberL = bloodNumberL;
    }

    public int getIsUpdata() {
        return isUpdata;
    }

    public void setIsUpdata(int isUpdata) {
        this.isUpdata = isUpdata;
    }

    @Override
    public String toString() {
        return "B15PBloodDB{" +
                "_id=" + _id +
                ", devicesMac='" + devicesMac + '\'' +
                ", bloodData='" + bloodData + '\'' +
                ", bloodTime='" + bloodTime + '\'' +
                ", bloodNumberH=" + bloodNumberH +
                ", bloodNumberL=" + bloodNumberL +
                ", isUpdata=" + isUpdata +
                '}';
    }
}
