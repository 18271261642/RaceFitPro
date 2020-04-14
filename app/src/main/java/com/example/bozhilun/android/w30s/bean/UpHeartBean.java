package com.example.bozhilun.android.w30s.bean;

public class UpHeartBean {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 手环MAC地址
     */
    private String deviceCode;
    /**
     * 心率
     */
    private int heartRate;
    /**
     * 血氧
     */
    private int bloodOxygen;
    /**
     * 低压
     */
    private int systolic;
    /**
     * 高压
     */
    private int diastolic;
    /**
     * 是否合格,1
     */
    private int status;
    /**
     * yyyy-MM-dd HH:mm
     */
    private String date;

    /**
     * 步数
     */
    private int stepNumber;

    public UpHeartBean(String userId, String deviceCode,
                       int heartRate,
                       int bloodOxygen, int systolic, int diastolic,
                       int status, String date, int stepNumber) {
        this.userId = userId;
        this.deviceCode = deviceCode;
        this.heartRate = heartRate;
        this.bloodOxygen = bloodOxygen;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.status = status;
        this.date = date;
        this.stepNumber = stepNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
}
