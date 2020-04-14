package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class FrendHaretBean {
    /**
     * friendHeartRate : [{"rtc":"2018-11-26 00:00","heartRate":78},{"rtc":"2018-11-26 00:05","heartRate":65},{"rtc":"2018-11-26 01:00","heartRate":0},{"rtc":"2018-11-26 07:30","heartRate":0},{"rtc":"2018-11-26 07:50","heartRate":65},{"rtc":"2018-11-26 11:26","heartRate":0},{"rtc":"2018-11-26 14:09","heartRate":78},{"rtc":"2018-11-26 16:30","heartRate":65}]
     * resultCode : 001
     */

    private String resultCode;
    private List<FriendHeartRateBean> friendHeartRate;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<FriendHeartRateBean> getFriendHeartRate() {
        return friendHeartRate;
    }

    public void setFriendHeartRate(List<FriendHeartRateBean> friendHeartRate) {
        this.friendHeartRate = friendHeartRate;
    }

    public static class FriendHeartRateBean {
        /**
         * rtc : 2018-11-26 00:00
         * heartRate : 78
         */

        private String time;
        private int heartRate;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }
    }




}
