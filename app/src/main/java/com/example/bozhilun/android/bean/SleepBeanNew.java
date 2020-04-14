package com.example.bozhilun.android.bean;

import java.util.List;

public class SleepBeanNew {

    /**
     * sleepData : [{"rtc":"2018-11-01","deepSleep":509,"shallowSleep":45,"wakeTime":"2018-11-01 00:00","soberLen":-550,"sleepTime":"2018-10-31 00:00","sleepLen":4,"weekCount":"4"},{"rtc":"2018-11-02","shallowSleep":0,"soberLen":0,"sleepLen":0,"weekCount":"5"}]
     * resultCode : 001
     * avgSleep : {"avgShallowSleep":45,"avgSleepLen":4,"avgDeepSleep":509}
     */

    private String resultCode;
    private AvgSleepBean avgSleep;
    private List<SleepDataBean> sleepData;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public AvgSleepBean getAvgSleep() {
        return avgSleep;
    }

    public void setAvgSleep(AvgSleepBean avgSleep) {
        this.avgSleep = avgSleep;
    }

    public List<SleepDataBean> getSleepData() {
        return sleepData;
    }

    public void setSleepData(List<SleepDataBean> sleepData) {
        this.sleepData = sleepData;
    }

    public static class AvgSleepBean {
        /**
         * avgShallowSleep : 45
         * avgSleepLen : 4
         * avgDeepSleep : 509
         */

        private int avgShallowSleep;
        private int avgSleepLen;
        private int avgDeepSleep;

        public int getAvgShallowSleep() {
            return avgShallowSleep;
        }

        public void setAvgShallowSleep(int avgShallowSleep) {
            this.avgShallowSleep = avgShallowSleep;
        }

        public int getAvgSleepLen() {
            return avgSleepLen;
        }

        public void setAvgSleepLen(int avgSleepLen) {
            this.avgSleepLen = avgSleepLen;
        }

        public int getAvgDeepSleep() {
            return avgDeepSleep;
        }

        public void setAvgDeepSleep(int avgDeepSleep) {
            this.avgDeepSleep = avgDeepSleep;
        }
    }

    public static class SleepDataBean {
        /**
         * rtc : 2018-11-01
         * deepSleep : 509
         * shallowSleep : 45
         * wakeTime : 2018-11-01 00:00
         * soberLen : -550
         * sleepTime : 2018-10-31 00:00
         * sleepLen : 4
         * weekCount : 4
         */

        private String rtc;
        private int deepSleep;
        private int shallowSleep;
        private String wakeTime;
        private int soberLen;
        private String sleepTime;
        private int sleepLen;
        private String weekCount;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getDeepSleep() {
            return deepSleep;
        }

        public void setDeepSleep(int deepSleep) {
            this.deepSleep = deepSleep;
        }

        public int getShallowSleep() {
            return shallowSleep;
        }

        public void setShallowSleep(int shallowSleep) {
            this.shallowSleep = shallowSleep;
        }

        public String getWakeTime() {
            return wakeTime;
        }

        public void setWakeTime(String wakeTime) {
            this.wakeTime = wakeTime;
        }

        public int getSoberLen() {
            return soberLen;
        }

        public void setSoberLen(int soberLen) {
            this.soberLen = soberLen;
        }

        public String getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(String sleepTime) {
            this.sleepTime = sleepTime;
        }

        public int getSleepLen() {
            return sleepLen;
        }

        public void setSleepLen(int sleepLen) {
            this.sleepLen = sleepLen;
        }

        public String getWeekCount() {
            return weekCount;
        }

        public void setWeekCount(String weekCount) {
            this.weekCount = weekCount;
        }
    }
}
