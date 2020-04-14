package com.example.bozhilun.android.b30.model;

import android.text.format.Time;
import java.util.Calendar;

/**
 * 对接维亿魄的timeData
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPTimeData {

    public int year;
    public int day;
    public int month;
    public int hour;
    public int minute;
    public int second;
    public int weekDay;

    public CusVPTimeData() {
    }

    public CusVPTimeData(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public CusVPTimeData(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CusVPTimeData(int month, int day, int hour, int minute) {
        this(Calendar.getInstance().get(1), month, day, hour, minute);
    }

    public CusVPTimeData(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public CusVPTimeData(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public CusVPTimeData(int year, int month, int day, int hour, int minute, int second, int week) {
        this(year, month, day, hour, minute);
        this.second = second;
        this.weekDay = week;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getWeekDay() {
        return this.weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getColck() {
        return getTwoStr(this.getHour()) + ":" + getTwoStr(this.getMinute());
    }

    public String getColckAndSecond() {
        return getTwoStr(this.getHour()) + ":" + getTwoStr(this.getMinute()) + ":" + getTwoStr(this.getSecond());
    }

    public String getColck12() {
        int hour = this.getHour();
        String amOrPm = "am";
        if (this.getHour() < 12) {
            amOrPm = "am";
        } else {
            amOrPm = "pm";
        }

        if (this.getHour() == 0) {
            hour = 12;
        }

        if (this.getHour() > 12) {
            hour %= 12;
        }

        return getTwoStr(hour) + ":" + getTwoStr(this.getMinute()) + amOrPm;
    }

    public String getColckForADC() {
        return getTwoStr(this.getHour()) + "_" + getTwoStr(this.getMinute());
    }

    public String getDate() {
        return getTwoStr(this.getMonth()) + "-" + getTwoStr(this.getDay());
    }

    public String getDateADC() {
        return getTwoStr(this.getMonth()) + "_" + getTwoStr(this.getDay());
    }

    public static String getTwoStr(int count) {
        String countStr = count + "";
        if (countStr.length() < 2) {
            countStr = "0" + countStr;
        }

        return countStr;
    }

    public int getHMValue() {
        int value = 0;
        value = this.hour * 60 + this.minute;
        return value;
    }


    public String getDateForSleepshow() {
        return this.getDate() + " " + this.getColck();
    }

    public String getDateForSleepshow12() {
        return this.getDate() + " " + this.getColck12();
    }

    public String getDateForDb() {
        int year = this.getYear();
        if (year == 0) {
            year = Calendar.getInstance().get(1);
        }

        return year + "-" + this.getDate();
    }

    public String getDateForADC() {
        int year = this.getYear();
        if (year == 0) {
            year = Calendar.getInstance().get(1);
        }

        return year + "_" + this.getDateADC();
    }

    public String getNullDateForDb() {
        return Calendar.getInstance().get(1) + "-255-255";
    }

    public String getDateAndClockForDb() {
        return this.getDateForDb() + "-" + this.getColck();
    }

    public String getDateAndClockAndSecondForDb() {
        return this.getDateForDb() + "-" + this.getColckAndSecond();
    }

    public String getDateAndClockForADC() {
        return this.getDateForADC() + "_" + this.getColckForADC();
    }

    public String getDateAndClockForSleep() {
        return this.getDateForDb() + " " + this.getColck();
    }

    public String getDateAndClockForSleepSecond() {
        return this.getDateForDb() + " " + this.getColckAndSecond();
    }

    public static int getSysMonth() {
        Time time = new Time();
        time.setToNow();
        int month = time.month + 1;
        return month;
    }

    public static int getSysYear() {
        Time time = new Time();
        time.setToNow();
        int year = time.year;
        return year;
    }

    public static int getSysDay() {
        Time time = new Time();
        time.setToNow();
        int year = time.monthDay;
        return year;
    }

    public static int getSysHour() {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        return hour;
    }

    public static int getSysMiute() {
        Time time = new Time();
        time.setToNow();
        int minute = time.minute;
        return minute;
    }

    public static int getSysSecond() {
        Time time = new Time();
        time.setToNow();
        int second = time.second;
        return second;
    }

    public void setCurrentTime() {
        Calendar instance = Calendar.getInstance();
        this.year = instance.get(1);
        this.month = instance.get(2) + 1;
        this.day = instance.get(5);
        this.hour = instance.get(11);
        this.minute = instance.get(12);
        this.second = instance.get(13);
    }

    @Override
    public String toString() {
        return "CusVPTimeData{" +
                "year=" + year +
                ", day=" + day +
                ", month=" + month +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", weekDay=" + weekDay +
                '}';
    }
}
