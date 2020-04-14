package com.example.bozhilun.android.commdbserver;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2019/2/28
 * 睡眠上传
 */
public class CommSleepDb extends LitePalSupport {

    /**
     * userId
     */
    private String userid;

    /**
     * mac地址
     */
    private String devicecode;

    /**
     * 日期 yyyy-MM-dd
     */
    private String dateStr;

    /**
     * 清醒时长 分钟
     */
    private int soberlen;

    /**
     * 深睡时长 分钟
     */
    private int deepsleep;

    /**
     * 浅睡时长  分钟
     */
    private int shallowsleep;

    /**
     * 睡眠总时长
     */
    private int sleeplen;

    /**
     * 入睡时间 yyyy-MM-dd HH:mm:ss
     */
    private String sleeptime;

    /**
     * 清醒时间 yyyy-MM-dd HH:mm:ss
     */
    private String waketime;

    /**
     * 设备名称
     */
    private String bleName;

    /**
     * 清醒次数
     */
    private int wakecount;

    /**
     * 是否已经上传
     * @return
     */
    private boolean isUpload;

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

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public int getSoberlen() {
        return soberlen;
    }

    public void setSoberlen(int soberlen) {
        this.soberlen = soberlen;
    }

    public int getDeepsleep() {
        return deepsleep;
    }

    public void setDeepsleep(int deepsleep) {
        this.deepsleep = deepsleep;
    }

    public int getShallowsleep() {
        return shallowsleep;
    }

    public void setShallowsleep(int shallowsleep) {
        this.shallowsleep = shallowsleep;
    }

    public int getSleeplen() {
        return sleeplen;
    }

    public void setSleeplen(int sleeplen) {
        this.sleeplen = sleeplen;
    }

    public String getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(String sleeptime) {
        this.sleeptime = sleeptime;
    }

    public String getWaketime() {
        return waketime;
    }

    public void setWaketime(String waketime) {
        this.waketime = waketime;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public int getWakecount() {
        return wakecount;
    }

    public void setWakecount(int wakecount) {
        this.wakecount = wakecount;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "CommSleepDb{" +
                "userid='" + userid + '\'' +
                ", devicecode='" + devicecode + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", soberlen=" + soberlen +
                ", deepsleep=" + deepsleep +
                ", shallowsleep=" + shallowsleep +
                ", sleeplen=" + sleeplen +
                ", sleeptime='" + sleeptime + '\'' +
                ", waketime='" + waketime + '\'' +
                ", bleName='" + bleName + '\'' +
                ", wakecount=" + wakecount +
                ", isUpload=" + isUpload +
                '}';
    }
}
