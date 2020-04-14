package com.example.bozhilun.android.b31.bpoxy.uploadSpo2;

/**
 * Created by Admin
 * Date 2019/8/22
 */
public class UploadSpo2Bean {


    /**
     * userId : userId
     * mac : mac
     * date : 2019-08-15
     * mTime : 2019-08-15 02:04:33
     * weekday : 7
     * heartValue : 80
     * sportValue : 10
     * oxygenValue : 20
     * apneaResult : 1
     * isHypoxia : 30
     * hypoxiaTime : 2
     * hypopnea : 22
     * cardiacLoad : 21
     * hrVariation : 52
     * stepValue : 0
     * respirationRate : 2
     * temp1 : 1
     * allPackNumner : 420
     * currentPackNumber : 1
     * hrv : 52
     * time : 00:01
     */

    private String userId;
    private String mac;
    private String date;
    private String mTime;
    private int weekday;
    private int heartValue;
    private int sportValue;
    private int oxygenValue;
    private int apneaResult;
    private int isHypoxia;
    private int hypoxiaTime;
    private int hypopnea;
    private int cardiacLoad;
    private int hrVariation;
    private int stepValue;
    private int respirationRate;
    private int temp1;
    private int allPackNumner;
    private int currentPackNumber;
    private int hrv;
    private String time;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMTime() {
        return mTime;
    }

    public void setMTime(String mTime) {
        this.mTime = mTime;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getHeartValue() {
        return heartValue;
    }

    public void setHeartValue(int heartValue) {
        this.heartValue = heartValue;
    }

    public int getSportValue() {
        return sportValue;
    }

    public void setSportValue(int sportValue) {
        this.sportValue = sportValue;
    }

    public int getOxygenValue() {
        return oxygenValue;
    }

    public void setOxygenValue(int oxygenValue) {
        this.oxygenValue = oxygenValue;
    }

    public int getApneaResult() {
        return apneaResult;
    }

    public void setApneaResult(int apneaResult) {
        this.apneaResult = apneaResult;
    }

    public int getIsHypoxia() {
        return isHypoxia;
    }

    public void setIsHypoxia(int isHypoxia) {
        this.isHypoxia = isHypoxia;
    }

    public int getHypoxiaTime() {
        return hypoxiaTime;
    }

    public void setHypoxiaTime(int hypoxiaTime) {
        this.hypoxiaTime = hypoxiaTime;
    }

    public int getHypopnea() {
        return hypopnea;
    }

    public void setHypopnea(int hypopnea) {
        this.hypopnea = hypopnea;
    }

    public int getCardiacLoad() {
        return cardiacLoad;
    }

    public void setCardiacLoad(int cardiacLoad) {
        this.cardiacLoad = cardiacLoad;
    }

    public int getHrVariation() {
        return hrVariation;
    }

    public void setHrVariation(int hrVariation) {
        this.hrVariation = hrVariation;
    }

    public int getStepValue() {
        return stepValue;
    }

    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }

    public int getRespirationRate() {
        return respirationRate;
    }

    public void setRespirationRate(int respirationRate) {
        this.respirationRate = respirationRate;
    }

    public int getTemp1() {
        return temp1;
    }

    public void setTemp1(int temp1) {
        this.temp1 = temp1;
    }

    public int getAllPackNumner() {
        return allPackNumner;
    }

    public void setAllPackNumner(int allPackNumner) {
        this.allPackNumner = allPackNumner;
    }

    public int getCurrentPackNumber() {
        return currentPackNumber;
    }

    public void setCurrentPackNumber(int currentPackNumber) {
        this.currentPackNumber = currentPackNumber;
    }

    public int getHrv() {
        return hrv;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UploadSpo2Bean{" +
                "userId='" + userId + '\'' +
                ", mac='" + mac + '\'' +
                ", date='" + date + '\'' +
                ", mTime='" + mTime + '\'' +
                ", weekday=" + weekday +
                ", heartValue=" + heartValue +
                ", sportValue=" + sportValue +
                ", oxygenValue=" + oxygenValue +
                ", apneaResult=" + apneaResult +
                ", isHypoxia=" + isHypoxia +
                ", hypoxiaTime=" + hypoxiaTime +
                ", hypopnea=" + hypopnea +
                ", cardiacLoad=" + cardiacLoad +
                ", hrVariation=" + hrVariation +
                ", stepValue=" + stepValue +
                ", respirationRate=" + respirationRate +
                ", temp1=" + temp1 +
                ", allPackNumner=" + allPackNumner +
                ", currentPackNumber=" + currentPackNumber +
                ", hrv=" + hrv +
                ", time='" + time + '\'' +
                '}';
    }
}
