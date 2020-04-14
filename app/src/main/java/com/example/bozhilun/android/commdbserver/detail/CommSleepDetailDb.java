package com.example.bozhilun.android.commdbserver.detail;

/**
 * 睡眠详细数据上传时需要用到的实体类
 * Created by Admin
 * Date 2019/5/13
 */
public class CommSleepDetailDb {


    private String userid;

    private String devicecode;

    private String sleepType;

    private String starttime;

    private String day;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }



    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSleepType() {
        return sleepType;
    }

    public void setSleepType(String sleepType) {
        this.sleepType = sleepType;
    }

    @Override
    public String toString() {
        return "CommSleepDetailDb{" +
                "userid='" + userid + '\'' +
                ", devicecode='" + devicecode + '\'' +
                ", sleepType=" + sleepType +
                ", starttime='" + starttime + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
