package com.example.bozhilun.android.b31.bpoxy.uploadSpo2;

/**
 * 上传HRV数据的bean
 * Created by Admin
 * Date 2019/8/23
 */
public class UploadHrvBean {


    /**
     * allCurrentPackNumber : 32
     * currentPackNumber : 53
     * mTime : 2019-08-23 05:11:54
     * hrvValue : 44
     * temp1 : 32
     * hearts : 02:88:64:13:11
     * time : 00:01
     * userId : userId
     * date : 2019-08-23
     * deviceCode : deviceCode
     */

    private int allCurrentPackNumber;
    private int currentPackNumber;
    private String mTime;
    private String hrvValue;
    private String temp1;
    private String hearts;
    private String time;
    private String userId;
    private String date;
    private String deviceCode;

    public int getAllCurrentPackNumber() {
        return allCurrentPackNumber;
    }

    public void setAllCurrentPackNumber(int allCurrentPackNumber) {
        this.allCurrentPackNumber = allCurrentPackNumber;
    }

    public int getCurrentPackNumber() {
        return currentPackNumber;
    }

    public void setCurrentPackNumber(int currentPackNumber) {
        this.currentPackNumber = currentPackNumber;
    }

    public String getMTime() {
        return mTime;
    }

    public void setMTime(String mTime) {
        this.mTime = mTime;
    }

    public String getHrvValue() {
        return hrvValue;
    }

    public void setHrvValue(String hrvValue) {
        this.hrvValue = hrvValue;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getHearts() {
        return hearts;
    }

    public void setHearts(String hearts) {
        this.hearts = hearts;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    @Override
    public String toString() {
        return "UploadHrvBean{" +
                "allCurrentPackNumber=" + allCurrentPackNumber +
                ", currentPackNumber=" + currentPackNumber +
                ", mTime='" + mTime + '\'' +
                ", hrvValue='" + hrvValue + '\'' +
                ", temp1='" + temp1 + '\'' +
                ", hearts='" + hearts + '\'' +
                ", time='" + time + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                '}';
    }
}
