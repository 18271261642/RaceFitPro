package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class FrendSleepBean {
    /**
     * resultCode : 001
     * sslist : [{"addTime":"2018-11-23 09:36:28","sleep_type":0,"startTime":"23:00","updateTime":"2018-11-23 10:03:24","id":361,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"23:43","updateTime":"2018-11-23 10:03:24","id":362,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":4,"startTime":"00:14","updateTime":"2018-11-23 10:03:24","id":363,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"00:16","updateTime":"2018-11-23 10:03:24","id":364,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":3,"startTime":"00:26","updateTime":"2018-11-23 10:03:24","id":365,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"00:48","updateTime":"2018-11-23 10:03:24","id":366,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":3,"startTime":"01:56","updateTime":"2018-11-23 10:03:24","id":367,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"02:42","updateTime":"2018-11-23 10:03:24","id":368,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":3,"startTime":"02:56","updateTime":"2018-11-23 10:03:24","id":369,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"03:12","updateTime":"2018-11-23 10:03:24","id":370,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":3,"startTime":"03:40","updateTime":"2018-11-23 10:03:24","id":371,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"03:58","updateTime":"2018-11-23 10:03:24","id":372,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":3,"startTime":"06:13","updateTime":"2018-11-23 10:03:24","id":373,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":2,"startTime":"06:37","updateTime":"2018-11-23 10:03:24","id":374,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"},{"addTime":"2018-11-23 09:36:28","sleep_type":5,"startTime":"07:34","updateTime":"2018-11-23 10:03:24","id":375,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","day":"2018-11-23"}]
     */

    private String resultCode;
    private List<SslistBean> sslist;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<SslistBean> getSslist() {
        return sslist;
    }

    public void setSslist(List<SslistBean> sslist) {
        this.sslist = sslist;
    }

    public static class SslistBean {
        /**
         * addTime : 2018-11-23 09:36:28
         * sleep_type : 0
         * startTime : 23:00
         * updateTime : 2018-11-23 10:03:24
         * id : 361
         * deviceCode : W30_E3D9
         * userId : 8c4c511a45374bb595e6fdf30bb878b7
         * day : 2018-11-23
         */

        private String addTime;
        private int sleep_type;
        private String startTime;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String userId;
        private String day;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public int getSleep_type() {
            return sleep_type;
        }

        public void setSleep_type(int sleep_type) {
            this.sleep_type = sleep_type;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
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

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
