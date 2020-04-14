package com.example.bozhilun.android.bean;

/**
 * //g轨迹纠偏高德所需数据
 * Created by Admin
 * Date 2019/10/18
 */
public class AmapTraceLocation {

    //纬度
    private double latitude;
    //经度
    private double longitude;
    //速度
    private float speed;
    //方向
    private float bearing;
    //时间
    private long time;


    public AmapTraceLocation(double latitude, double longitude, float speed, float bearing, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.bearing = bearing;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
