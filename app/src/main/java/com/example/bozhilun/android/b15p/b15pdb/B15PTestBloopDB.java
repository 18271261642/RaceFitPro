package com.example.bozhilun.android.b15p.b15pdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class B15PTestBloopDB {
    @Id(autoincrement = true)
    private Long _id;
    private String devicesMac;
    private int bloodNumberH;
    private int bloodNumberL;


    @Generated(hash = 787785704)
    public B15PTestBloopDB(Long _id, String devicesMac, int bloodNumberH,
            int bloodNumberL) {
        this._id = _id;
        this.devicesMac = devicesMac;
        this.bloodNumberH = bloodNumberH;
        this.bloodNumberL = bloodNumberL;
    }

    @Generated(hash = 2072324053)
    public B15PTestBloopDB() {
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

    @Override
    public String toString() {
        return "B15PTestBloopDB{" +
                "_id=" + _id +
                ", devicesMac='" + devicesMac + '\'' +
                ", bloodNumberH=" + bloodNumberH +
                ", bloodNumberL=" + bloodNumberL +
                '}';
    }
}
