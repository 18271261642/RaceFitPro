package com.example.bozhilun.android.commdbserver.detail;

/**
 * 步数详细数据
 * Created by Admin
 * Date 2019/5/11
 */
public class CommStepDetailDb {




    private String userid;

    private String rtc;

    private String devicecode;

    private int stepnumber;

    private int timelen;

    private String distance;

    private String calories;

    private int status;  //0室内 1室外

    private int action; // 0运动1跑步2骑行

    private int speed;

    private String startdate;   //开始日期，APP端规定为对应的时间点，eg:00:30

    private String enddate;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }

    public int getStepnumber() {
        return stepnumber;
    }

    public void setStepnumber(int stepnumber) {
        this.stepnumber = stepnumber;
    }

    public int getTimelen() {
        return timelen;
    }

    public void setTimelen(int timelen) {
        this.timelen = timelen;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }
}
