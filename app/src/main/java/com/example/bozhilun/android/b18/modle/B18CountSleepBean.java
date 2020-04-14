package com.example.bozhilun.android.b18.modle;

/**
 * Created by Admin
 * Date 2019/11/13
 */
public class B18CountSleepBean {


    /**
     * year : 2019
     * month : 11
     * day : 9
     * fallSleepTime : 0
     * secondStageTime : 0
     * deepSleepTime : 264
     * shallowSleepTime : 153
     * awakeTime : 71
     * awakeCount : 0
     * startSleepHour : 0
     * startSleepMinute : 0
     */

    private int year;
    private int month;
    private int day;
    private int fallSleepTime;
    private int secondStageTime;
    private int deepSleepTime;
    private int shallowSleepTime;
    private int awakeTime;
    private int awakeCount;
    private int startSleepHour;
    private int startSleepMinute;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getFallSleepTime() {
        return fallSleepTime;
    }

    public void setFallSleepTime(int fallSleepTime) {
        this.fallSleepTime = fallSleepTime;
    }

    public int getSecondStageTime() {
        return secondStageTime;
    }

    public void setSecondStageTime(int secondStageTime) {
        this.secondStageTime = secondStageTime;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getShallowSleepTime() {
        return shallowSleepTime;
    }

    public void setShallowSleepTime(int shallowSleepTime) {
        this.shallowSleepTime = shallowSleepTime;
    }

    public int getAwakeTime() {
        return awakeTime;
    }

    public void setAwakeTime(int awakeTime) {
        this.awakeTime = awakeTime;
    }

    public int getAwakeCount() {
        return awakeCount;
    }

    public void setAwakeCount(int awakeCount) {
        this.awakeCount = awakeCount;
    }

    public int getStartSleepHour() {
        return startSleepHour;
    }

    public void setStartSleepHour(int startSleepHour) {
        this.startSleepHour = startSleepHour;
    }

    public int getStartSleepMinute() {
        return startSleepMinute;
    }

    public void setStartSleepMinute(int startSleepMinute) {
        this.startSleepMinute = startSleepMinute;
    }

    @Override
    public String toString() {
        return "B18CountSleepBean{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", fallSleepTime=" + fallSleepTime +
                ", secondStageTime=" + secondStageTime +
                ", deepSleepTime=" + deepSleepTime +
                ", shallowSleepTime=" + shallowSleepTime +
                ", awakeTime=" + awakeTime +
                ", awakeCount=" + awakeCount +
                ", startSleepHour=" + startSleepHour +
                ", startSleepMinute=" + startSleepMinute +
                '}';
    }
}
