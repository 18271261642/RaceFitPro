package com.example.bozhilun.android.bean;

import java.util.List;

public class SportBeanNew {
    /**
     * resultCode : 001
     * day : [{"rtc":"2018-10-01","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-02","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-03","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-04","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-05","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-06","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-07","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-08","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-09","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-10","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-11","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-12","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-13","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-14","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-15","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-16","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-17","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-18","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-19","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-20","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-21","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-22","distance":"0","stepNumber":0,"calories":0},{"rtc":"2018-10-23","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-24","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-25","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-26","distance":"660","stepNumber":948,"calories":47},{"rtc":"2018-10-27","distance":"3869","stepNumber":5551,"calories":280},{"rtc":"2018-10-28","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-29","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-30","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2018-10-31","distance":"0","stepNumber":0,"calories":"0"}]
     */

    private String resultCode;
    private List<DayBean> day;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<DayBean> getDay() {
        return day;
    }

    public void setDay(List<DayBean> day) {
        this.day = day;
    }

    public static class DayBean {
        /**
         * rtc : 2018-10-01
         * distance : 0
         * stepNumber : 0
         * calories : 0
         */

        private String rtc;
        private String distance;
        private int stepNumber;
        private String calories;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getCalories() {
            return calories;
        }

        public void setCalories(String calories) {
            this.calories = calories;
        }
    }
}
