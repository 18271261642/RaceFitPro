package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class BpBean {
    /**
     * resultCode : 001
     * bplist : [{"rtc":"2019-01-07 08:30","systolic":81,"diastolic":122,"addTime":"2019-01-07 08:40:18","updateTime":"2019-01-07 14:58:34","id":2045815,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 09:00","systolic":79,"diastolic":118,"addTime":"2019-01-07 09:25:52","updateTime":"2019-01-07 14:58:34","id":2047265,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 10:00","systolic":78,"diastolic":117,"addTime":"2019-01-07 11:15:59","updateTime":"2019-01-07 14:58:34","id":2054145,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 09:30","systolic":83,"diastolic":124,"addTime":"2019-01-07 11:15:59","updateTime":"2019-01-07 14:58:34","id":2054146,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 11:00","systolic":80,"diastolic":120,"addTime":"2019-01-07 11:15:59","updateTime":"2019-01-07 14:58:34","id":2054147,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 10:30","systolic":71,"diastolic":107,"addTime":"2019-01-07 11:15:59","updateTime":"2019-01-07 14:58:34","id":2054148,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 12:00","systolic":76,"diastolic":114,"addTime":"2019-01-07 13:23:15","updateTime":"2019-01-07 14:58:34","id":2057998,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 11:30","systolic":82,"diastolic":123,"addTime":"2019-01-07 13:23:15","updateTime":"2019-01-07 14:58:34","id":2057999,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 13:00","systolic":90,"diastolic":135,"addTime":"2019-01-07 13:23:15","updateTime":"2019-01-07 14:58:34","id":2058000,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 12:30","systolic":83,"diastolic":125,"addTime":"2019-01-07 13:23:15","updateTime":"2019-01-07 14:58:34","id":2058001,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 13:30","systolic":76,"diastolic":114,"addTime":"2019-01-07 14:58:34","updateTime":null,"id":2064417,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 14:00","systolic":74,"diastolic":111,"addTime":"2019-01-07 14:58:34","updateTime":null,"id":2064418,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0},{"rtc":"2019-01-07 14:30","systolic":80,"diastolic":120,"addTime":"2019-01-07 14:58:34","updateTime":null,"id":2064419,"deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","status":0}]
     */

    private String resultCode;
    private List<BplistBean> bplist;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<BplistBean> getBplist() {
        return bplist;
    }

    public void setBplist(List<BplistBean> bplist) {
        this.bplist = bplist;
    }

    public static class BplistBean {
        /**
         * rtc : 2019-01-07 08:30
         * systolic : 81
         * diastolic : 122
         * addTime : 2019-01-07 08:40:18
         * updateTime : 2019-01-07 14:58:34
         * id : 2045815
         * deviceCode : E7:A7:0F:11:BE:B5
         * userId : 0d56716e5629475882d4f4bfc7c51420
         * status : 0
         */

        private String rtc;
        private int systolic;
        private int diastolic;
        private String addTime;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String userId;
        private int status;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getSystolic() {
            return systolic;
        }

        public void setSystolic(int systolic) {
            this.systolic = systolic;
        }

        public int getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(int diastolic) {
            this.diastolic = diastolic;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "BplistBean{" +
                    "rtc='" + rtc + '\'' +
                    ", systolic=" + systolic +
                    ", diastolic=" + diastolic +
                    ", addTime='" + addTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", id=" + id +
                    ", deviceCode='" + deviceCode + '\'' +
                    ", userId='" + userId + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
