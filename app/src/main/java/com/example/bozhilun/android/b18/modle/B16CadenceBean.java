package com.example.bozhilun.android.b18.modle;

import java.util.List;

/**
 * B16用于计算步频的数据,设备返回的原始数据结构
 * Created by Admin
 * Date 2019/12/30
 */
public class B16CadenceBean {


    /**
     * year : 2019
     * month : 12
     * day : 30
     * hour : 8
     * minute : 17
     * second : 19
     * sportType : 0
     * distance : 55
     * calorie : 45
     * step : 920
     * risingHeight : 0
     * fallHeight : 0
     * risingTime : 0
     * fallTime : 0
     * maxHeight : 0
     * minHeight : 0
     * maxTemperature : 0
     * minTemperature : 0
     * intervalTime : 30
     * lastTime : 0
     * detailData : [{"heart":0,"calorie":1,"step":40,"distance":20},{"heart":0,"calorie":2,"step":45,"distance":20},{"heart":0,"calorie":4,"step":53,"distance":46},{"heart":0,"calorie":2,"step":63,"distance":28},{"heart":0,"calorie":2,"step":52,"distance":28}]
     */

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int sportType;
    private int distance;
    private int calorie;
    private int step;
    private int risingHeight;
    private int fallHeight;
    private int risingTime;
    private int fallTime;
    private int maxHeight;
    private int minHeight;
    private int maxTemperature;
    private int minTemperature;
    private int intervalTime;
    private int lastTime;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getSportType() {
        return sportType;
    }

    public void setSportType(int sportType) {
        this.sportType = sportType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getRisingHeight() {
        return risingHeight;
    }

    public void setRisingHeight(int risingHeight) {
        this.risingHeight = risingHeight;
    }

    public int getFallHeight() {
        return fallHeight;
    }

    public void setFallHeight(int fallHeight) {
        this.fallHeight = fallHeight;
    }

    public int getRisingTime() {
        return risingTime;
    }

    public void setRisingTime(int risingTime) {
        this.risingTime = risingTime;
    }

    public int getFallTime() {
        return fallTime;
    }

    public void setFallTime(int fallTime) {
        this.fallTime = fallTime;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public List<DetailDataBean> getDetailData() {
        return detailData;
    }

    public void setDetailData(List<DetailDataBean> detailData) {
        this.detailData = detailData;
    }

    @Override
    public String toString() {
        return "B16CadenceBean{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", sportType=" + sportType +
                ", distance=" + distance +
                ", calorie=" + calorie +
                ", step=" + step +
                ", risingHeight=" + risingHeight +
                ", fallHeight=" + fallHeight +
                ", risingTime=" + risingTime +
                ", fallTime=" + fallTime +
                ", maxHeight=" + maxHeight +
                ", minHeight=" + minHeight +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", intervalTime=" + intervalTime +
                ", lastTime=" + lastTime +
                ", detailData=" + detailData +
                '}';
    }

    public static class DetailDataBean {
        /**
         * heart : 0
         * calorie : 1
         * step : 40
         * distance : 20
         */

        private int heart;
        private int calorie;
        private int step;
        private int distance;

        public int getHeart() {
            return heart;
        }

        public void setHeart(int heart) {
            this.heart = heart;
        }

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "DetailDataBean{" +
                    "heart=" + heart +
                    ", calorie=" + calorie +
                    ", step=" + step +
                    ", distance=" + distance +
                    '}';
        }
    }
}
