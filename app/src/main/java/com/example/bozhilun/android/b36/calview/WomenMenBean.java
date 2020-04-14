package com.example.bozhilun.android.b36.calview;

/**
 * Created by Admin
 * Date 2018/11/29
 */
public class WomenMenBean {

    private int id;
    private int beginTime; //月经开始
    private int endTime; //月经结束时间
    private int year;       //年份
    private int date; //月份
    private int cycle; //月经周期
    private int durationDay; //月经天数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(int durationDay) {
        this.durationDay = durationDay;
    }

    @Override
    public String toString() {
        return "WomenMenBean{" +
                "id=" + id +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", year=" + year +
                ", date=" + date +
                ", cycle=" + cycle +
                ", durationDay=" + durationDay +
                '}';
    }
}
