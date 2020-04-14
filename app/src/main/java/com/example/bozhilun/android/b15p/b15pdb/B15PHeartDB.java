package com.example.bozhilun.android.b15p.b15pdb;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class B15PHeartDB {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private String heartData;
    private String heartTime;
    private int heartNumber;
    private int isUpdata;

    @Generated(hash = 286164747)
    public B15PHeartDB(Long _id, String devicesMac, String heartData,
            String heartTime, int heartNumber, int isUpdata) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.heartData = heartData;
        this.heartTime = heartTime;
        this.heartNumber = heartNumber;
        this.isUpdata = isUpdata;
    }

    @Generated(hash = 1386176623)
    public B15PHeartDB() {
    }

    public String getDevicesMac() {
        return devicesMac;
    }

    public void setDevicesMac(String devicesMac) {
        this.devicesMac = devicesMac;
    }

    public String getHeartData() {
        return heartData;
    }

    public void setHeartData(String heartData) {
        this.heartData = heartData;
    }

    public String getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(String heartTime) {
        this.heartTime = heartTime;
    }

    public int getHeartNumber() {
        return heartNumber;
    }

    public void setHeartNumber(int heartNumber) {
        this.heartNumber = heartNumber;
    }

    public int getIsUpdata() {
        return isUpdata;
    }

    public void setIsUpdata(int isUpdata) {
        this.isUpdata = isUpdata;
    }

    @Override
    public String toString() {
        return "B15PHeartDB{" +
                "devicesMac='" + devicesMac + '\'' +
                ", heartData='" + heartData + '\'' +
                ", heartTime='" + heartTime + '\'' +
                ", heartNumber=" + heartNumber +
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
