package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class FrendStepBean {
    /**
     * resultCode : 001
     * friendStepNumber : [{"rtc":"2018-11-26 01:00","stepNumber":3350}]
     */

    private String resultCode;
    private List<FriendStepNumberBean> friendStepNumber;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<FriendStepNumberBean> getFriendStepNumber() {
        return friendStepNumber;
    }

    public void setFriendStepNumber(List<FriendStepNumberBean> friendStepNumber) {
        this.friendStepNumber = friendStepNumber;
    }

    public static class FriendStepNumberBean {
        /**
         * rtc : 2018-11-26 01:00
         * stepNumber : 3350
         */

        private String rtc;
        private int stepNumber;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }
    }
//    /**
//     * resultCode : 001
//     * friendStepNumber : [{"date":"2018-11-21 00:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962389,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 00:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004488,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 00:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059309,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 01:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962390,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 01:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004490,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 01:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059319,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 02:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962391,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 02:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004492,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 02:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059329,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 03:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962392,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 03:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004494,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 03:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059337,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 04:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962393,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 04:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004496,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 04:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059347,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 05:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962394,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 05:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004498,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 05:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059357,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 06:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962395,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 06:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004500,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 06:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059368,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 07:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962396,"deviceCode":"W30_E3D9","stepNumber":725,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 07:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004502,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 07:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059377,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 08:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962397,"deviceCode":"W30_E3D9","stepNumber":1080,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 08:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004504,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 08:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059385,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 09:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059395,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 09:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962398,"deviceCode":"W30_E3D9","stepNumber":942,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 09:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004507,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 10:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059402,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 10:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962399,"deviceCode":"W30_E3D9","stepNumber":185,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 10:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004513,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 11:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059410,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 11:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962400,"deviceCode":"W30_E3D9","stepNumber":38,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 11:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004519,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 12:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059417,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 12:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962401,"deviceCode":"W30_E3D9","stepNumber":809,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 12:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004525,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 13:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059425,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 13:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962402,"deviceCode":"W30_E3D9","stepNumber":67,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 13:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004530,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 14:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962403,"deviceCode":"W30_E3D9","stepNumber":350,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 14:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059433,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 14:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004533,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 15:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962404,"deviceCode":"W30_E3D9","stepNumber":26,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 15:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059442,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 15:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004537,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 16:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962405,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 16:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059450,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 16:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8004540,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 17:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058389,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 17:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962406,"deviceCode":"W30_E3D9","stepNumber":219,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 17:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059458,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 18:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058393,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 18:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962407,"deviceCode":"W30_E3D9","stepNumber":122,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 18:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059466,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 19:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058398,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 19:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962408,"deviceCode":"W30_E3D9","stepNumber":249,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 19:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059474,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 20:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058401,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 20:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962409,"deviceCode":"W30_E3D9","stepNumber":1491,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 20:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059482,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 21:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058406,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 21:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962410,"deviceCode":"W30_E3D9","stepNumber":1288,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 21:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059488,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 22:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058410,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 22:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962411,"deviceCode":"W30_E3D9","stepNumber":66,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 22:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059494,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 23:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":7962412,"deviceCode":"W30_E3D9","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 23:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8058414,"deviceCode":"CD:1C:1E:09:CD:11","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1},{"date":"2018-11-21 23:00","bloodOxygen":0,"systolic":0,"diastolic":0,"heartRate":0,"id":8059500,"deviceCode":"F0:1E:9B:12:36:85","stepNumber":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":1}]
//     */
//
//    private String resultCode;
//    private List<FriendStepNumberBean> friendStepNumber;
//
//    public String getResultCode() {
//        return resultCode;
//    }
//
//    public void setResultCode(String resultCode) {
//        this.resultCode = resultCode;
//    }
//
//    public List<FriendStepNumberBean> getFriendStepNumber() {
//        return friendStepNumber;
//    }
//
//    public void setFriendStepNumber(List<FriendStepNumberBean> friendStepNumber) {
//        this.friendStepNumber = friendStepNumber;
//    }
//
//    public static class FriendStepNumberBean {
//        /**
//         * date : 2018-11-21 00:00
//         * bloodOxygen : 0
//         * systolic : 0
//         * diastolic : 0
//         * heartRate : 0
//         * id : 7962389
//         * deviceCode : W30_E3D9
//         * stepNumber : 0
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         * status : 1
//         */
//
//        private String date;
//        private int bloodOxygen;
//        private int systolic;
//        private int diastolic;
//        private int heartRate;
//        private int id;
//        private String deviceCode;
//        private int stepNumber;
//        private String userId;
//        private int status;
//
//        public String getDate() {
//            return date;
//        }
//
//        public void setDate(String date) {
//            this.date = date;
//        }
//
//        public int getBloodOxygen() {
//            return bloodOxygen;
//        }
//
//        public void setBloodOxygen(int bloodOxygen) {
//            this.bloodOxygen = bloodOxygen;
//        }
//
//        public int getSystolic() {
//            return systolic;
//        }
//
//        public void setSystolic(int systolic) {
//            this.systolic = systolic;
//        }
//
//        public int getDiastolic() {
//            return diastolic;
//        }
//
//        public void setDiastolic(int diastolic) {
//            this.diastolic = diastolic;
//        }
//
//        public int getHeartRate() {
//            return heartRate;
//        }
//
//        public void setHeartRate(int heartRate) {
//            this.heartRate = heartRate;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getDeviceCode() {
//            return deviceCode;
//        }
//
//        public void setDeviceCode(String deviceCode) {
//            this.deviceCode = deviceCode;
//        }
//
//        public int getStepNumber() {
//            return stepNumber;
//        }
//
//        public void setStepNumber(int stepNumber) {
//            this.stepNumber = stepNumber;
//        }
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//    }

}
