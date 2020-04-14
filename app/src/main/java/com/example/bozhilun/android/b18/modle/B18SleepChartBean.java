package com.example.bozhilun.android.b18.modle;

import java.util.List;

/**
 * Created by Admin
 * Date 2019/11/13
 */
public class B18SleepChartBean {


    /**
     * year : 2019
     * month : 11
     * day : 9
     * startHour : 0
     * startMinute : 0
     * status : 1
     * detailData : [{"sleepType":3,"sleepTime":40},{"sleepType":2,"sleepTime":3},{"sleepType":3,"sleepTime":42},{"sleepType":2,"sleepTime":17},{"sleepType":1,"sleepTime":64},{"sleepType":2,"sleepTime":26},{"sleepType":3,"sleepTime":16},{"sleepType":2,"sleepTime":13},{"sleepType":3,"sleepTime":10},{"sleepType":2,"sleepTime":17},{"sleepType":3,"sleepTime":46},{"sleepType":2,"sleepTime":48},{"sleepType":3,"sleepTime":36},{"sleepType":2,"sleepTime":21},{"sleepType":3,"sleepTime":56},{"sleepType":1,"sleepTime":7},{"sleepType":2,"sleepTime":8},{"sleepType":3,"sleepTime":18}]
     */

    private int year;
    private int month;
    private int day;
    private int startHour;
    private int startMinute;
    private int status;
    private List<DetailDataBean> detailData;

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

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DetailDataBean> getDetailData() {
        return detailData;
    }

    public void setDetailData(List<DetailDataBean> detailData) {
        this.detailData = detailData;
    }

    public static class DetailDataBean {
        /**
         * sleepType : 3
         * sleepTime : 40
         */

        private int sleepType;
        private int sleepTime;

        public int getSleepType() {
            return sleepType;
        }

        public void setSleepType(int sleepType) {
            this.sleepType = sleepType;
        }

        public int getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(int sleepTime) {
            this.sleepTime = sleepTime;
        }
    }

    @Override
    public String toString() {
        return "B18SleepChartBean{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", status=" + status +
                ", detailData=" + detailData +
                '}';
    }
}
