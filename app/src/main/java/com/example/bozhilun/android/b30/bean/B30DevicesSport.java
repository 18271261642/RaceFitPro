package com.example.bozhilun.android.b30.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class B30DevicesSport {
    String date;
    private String startTime;
    private String stopTime;
    private int sportTime;
    private int stepCount;
    private int sportCount;
    private double kcals;
    private double distance;
    private int recordCount;
    private int pauseCount;
    private int pauseTime;
    private int crc;
    private int peisu;
    private int oxsporttimes;
    private int averRate;
    String username;
    String address;
    @Generated(hash = 867650147)
    public B30DevicesSport(String date, String startTime, String stopTime,
            int sportTime, int stepCount, int sportCount, double kcals,
            double distance, int recordCount, int pauseCount, int pauseTime,
            int crc, int peisu, int oxsporttimes, int averRate, String username,
            String address) {
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.sportTime = sportTime;
        this.stepCount = stepCount;
        this.sportCount = sportCount;
        this.kcals = kcals;
        this.distance = distance;
        this.recordCount = recordCount;
        this.pauseCount = pauseCount;
        this.pauseTime = pauseTime;
        this.crc = crc;
        this.peisu = peisu;
        this.oxsporttimes = oxsporttimes;
        this.averRate = averRate;
        this.username = username;
        this.address = address;
    }
    @Generated(hash = 344064068)
    public B30DevicesSport() {
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStopTime() {
        return this.stopTime;
    }
    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
    public int getSportTime() {
        return this.sportTime;
    }
    public void setSportTime(int sportTime) {
        this.sportTime = sportTime;
    }
    public int getStepCount() {
        return this.stepCount;
    }
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
    public int getSportCount() {
        return this.sportCount;
    }
    public void setSportCount(int sportCount) {
        this.sportCount = sportCount;
    }
    public double getKcals() {
        return this.kcals;
    }
    public void setKcals(double kcals) {
        this.kcals = kcals;
    }
    public double getDistance() {
        return this.distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public int getRecordCount() {
        return this.recordCount;
    }
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public int getPauseCount() {
        return this.pauseCount;
    }
    public void setPauseCount(int pauseCount) {
        this.pauseCount = pauseCount;
    }
    public int getPauseTime() {
        return this.pauseTime;
    }
    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }
    public int getCrc() {
        return this.crc;
    }
    public void setCrc(int crc) {
        this.crc = crc;
    }
    public int getPeisu() {
        return this.peisu;
    }
    public void setPeisu(int peisu) {
        this.peisu = peisu;
    }
    public int getOxsporttimes() {
        return this.oxsporttimes;
    }
    public void setOxsporttimes(int oxsporttimes) {
        this.oxsporttimes = oxsporttimes;
    }
    public int getAverRate() {
        return this.averRate;
    }
    public void setAverRate(int averRate) {
        this.averRate = averRate;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "B30DevicesSport{" +
                "date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", sportTime=" + sportTime +
                ", stepCount=" + stepCount +
                ", sportCount=" + sportCount +
                ", kcals=" + kcals +
                ", distance=" + distance +
                ", recordCount=" + recordCount +
                ", pauseCount=" + pauseCount +
                ", pauseTime=" + pauseTime +
                ", crc=" + crc +
                ", peisu=" + peisu +
                ", oxsporttimes=" + oxsporttimes +
                ", averRate=" + averRate +
                ", username='" + username + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
