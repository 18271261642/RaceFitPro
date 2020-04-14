package com.example.bozhilun.android.bean;

import org.litepal.crud.LitePalSupport;

/**
 * GPS运动实体类
 * Created by Admin
 * Date 2019/10/18
 */
public class AmapSportBean extends LitePalSupport {

    //日期
    private String rtc;
    //yyyy-MM-dd HH:mm:ss
    private String detailTime;
    //mac
    private String bleMac;
    //userId
    private String userId;
    //卡路里
    private String kcal;
    //总里程
    private String countDisance;
    //总时间
    private String countTime;
    //平均速度
    private String avgSpeed;

    //类型 0是跑步；1是骑行
    private int sportType;

    //轨迹纠偏字符串
    private String amapTraceStr;


    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getCountDisance() {
        return countDisance;
    }

    public void setCountDisance(String countDisance) {
        this.countDisance = countDisance;
    }

    public String getCountTime() {
        return countTime;
    }

    public void setCountTime(String countTime) {
        this.countTime = countTime;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getAmapTraceStr() {
        return amapTraceStr;
    }

    public void setAmapTraceStr(String amapTraceStr) {
        this.amapTraceStr = amapTraceStr;
    }

    public String getDetailTime() {
        return detailTime;
    }

    public void setDetailTime(String detailTime) {
        this.detailTime = detailTime;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    @Override
    public String toString() {
        return "AmapSportBean{" +
                "rtc='" + rtc + '\'' +
                ", detailTime='" + detailTime + '\'' +
                ", bleMac='" + bleMac + '\'' +
                ", userId='" + userId + '\'' +
                ", kcal='" + kcal + '\'' +
                ", countDisance='" + countDisance + '\'' +
                ", countTime='" + countTime + '\'' +
                ", avgSpeed='" + avgSpeed + '\'' +
                ", sportType=" + sportType +
                ", amapTraceStr='" + amapTraceStr + '\'' +
                '}';
    }
}
