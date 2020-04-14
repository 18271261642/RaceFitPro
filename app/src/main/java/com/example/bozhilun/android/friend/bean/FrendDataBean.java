package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class FrendDataBean {
    /**
     * sleepDay : {"rtc":"2018-11-14","deepSleep":99,"shallowSleep":342,"wakeTime":"2018-11-14 07:35","addTime":"2018-11-14 08:15","soberLen":0,"sleepTime":"2018-11-14 23:00","sleepLen":441,"updateTime":"2018-11-14 21:09","id":91592,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7"}
     * deviceDataMovement : {"date":"2018-12-07","addTime":"2018-12-07 08:52","count":44,"updateTime":"2018-12-07 17:47","id":59484,"stepNumber":821,"deviceCode":"E9:84:FA:77:54:60","userId":"5c2b58f0681547a0801d4d4ac8465f82"}
     * heartRateDay : {"rtc":"2018-12-07","addTime":"2018-12-07 08:52","minHeartRate":55,"updateTime":"2018-12-07 17:47","id":164701,"deviceCode":"E9:84:FA:77:54:60","userId":"5c2b58f0681547a0801d4d4ac8465f82","maxHeartRate":103,"avgHeartRate":64}
     * resultCode : 001
     * friendInfo : {"birthday":"1994-07-05","image":"http://47.90.83.197/image/2018/12/06/1544087037017.jpg","nickName":"超级管理员","sex":"F","weight":"60","equipment":null,"userId":"5c2b58f0681547a0801d4d4ac8465f82","phone":"15916947377","exInfoSetList":[{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":1,"setType":1,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":2,"setType":2,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":3,"setType":3,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":4,"setType":4,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":5,"setType":5,"userId":"5c2b58f0681547a0801d4d4ac8465f82"}],"height":"170"}
     * BloodPressureDay : {"id":91592,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","rtc":"2018-11-14","systolic":99,"diastolic":342,"status":0,"addTime":"2018-11-14 08:15","updateTime":"2018-11-14 21:09"}
     */

    private SleepDayBean sleepDay;
    private DeviceDataMovementBean deviceDataMovement;
    private HeartRateDayBean heartRateDay;
    private String resultCode;
    private FriendInfoBean friendInfo;
    private BloodPressureDayBean BloodPressureDay;

    public SleepDayBean getSleepDay() {
        return sleepDay;
    }

    public void setSleepDay(SleepDayBean sleepDay) {
        this.sleepDay = sleepDay;
    }

    public DeviceDataMovementBean getDeviceDataMovement() {
        return deviceDataMovement;
    }

    public void setDeviceDataMovement(DeviceDataMovementBean deviceDataMovement) {
        this.deviceDataMovement = deviceDataMovement;
    }

    public HeartRateDayBean getHeartRateDay() {
        return heartRateDay;
    }

    public void setHeartRateDay(HeartRateDayBean heartRateDay) {
        this.heartRateDay = heartRateDay;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public FriendInfoBean getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(FriendInfoBean friendInfo) {
        this.friendInfo = friendInfo;
    }

    public BloodPressureDayBean getBloodPressureDay() {
        return BloodPressureDay;
    }

    public void setBloodPressureDay(BloodPressureDayBean BloodPressureDay) {
        this.BloodPressureDay = BloodPressureDay;
    }

    public static class SleepDayBean {
        /**
         * rtc : 2018-11-14
         * deepSleep : 99
         * shallowSleep : 342
         * wakeTime : 2018-11-14 07:35
         * addTime : 2018-11-14 08:15
         * soberLen : 0
         * sleepTime : 2018-11-14 23:00
         * sleepLen : 441
         * updateTime : 2018-11-14 21:09
         * id : 91592
         * deviceCode : W30_E3D9
         * userId : 8c4c511a45374bb595e6fdf30bb878b7
         */

        private String rtc;
        private int deepSleep;
        private int shallowSleep;
        private String wakeTime;
        private String addTime;
        private int soberLen;
        private String sleepTime;
        private int sleepLen;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String userId;

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

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
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
    }

    public static class DeviceDataMovementBean {
        /**
         * date : 2018-12-07
         * addTime : 2018-12-07 08:52
         * count : 44
         * updateTime : 2018-12-07 17:47
         * id : 59484
         * stepNumber : 821
         * deviceCode : E9:84:FA:77:54:60
         * userId : 5c2b58f0681547a0801d4d4ac8465f82
         */

        private String date;
        private String addTime;
        private int count;
        private String updateTime;
        private int id;
        private int stepNumber;
        private String deviceCode;
        private String userId;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
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

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
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
    }

    public static class HeartRateDayBean {
        /**
         * rtc : 2018-12-07
         * addTime : 2018-12-07 08:52
         * minHeartRate : 55
         * updateTime : 2018-12-07 17:47
         * id : 164701
         * deviceCode : E9:84:FA:77:54:60
         * userId : 5c2b58f0681547a0801d4d4ac8465f82
         * maxHeartRate : 103
         * avgHeartRate : 64
         */

        private String rtc;
        private String addTime;
        private int minHeartRate;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String userId;
        private int maxHeartRate;
        private int avgHeartRate;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public int getMinHeartRate() {
            return minHeartRate;
        }

        public void setMinHeartRate(int minHeartRate) {
            this.minHeartRate = minHeartRate;
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

        public int getMaxHeartRate() {
            return maxHeartRate;
        }

        public void setMaxHeartRate(int maxHeartRate) {
            this.maxHeartRate = maxHeartRate;
        }

        public int getAvgHeartRate() {
            return avgHeartRate;
        }

        public void setAvgHeartRate(int avgHeartRate) {
            this.avgHeartRate = avgHeartRate;
        }
    }

    public static class FriendInfoBean {
        /**
         * birthday : 1994-07-05
         * image : http://47.90.83.197/image/2018/12/06/1544087037017.jpg
         * nickName : 超级管理员
         * sex : F
         * weight : 60
         * equipment : null
         * userId : 5c2b58f0681547a0801d4d4ac8465f82
         * phone : 15916947377
         * exInfoSetList : [{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":1,"setType":1,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":2,"setType":2,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":3,"setType":3,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":4,"setType":4,"userId":"5c2b58f0681547a0801d4d4ac8465f82"},{"exhibition":1,"friendId":"0d56716e5629475882d4f4bfc7c51420","addTime":"2018-12-07 17:16:29","updateTime":null,"id":5,"setType":5,"userId":"5c2b58f0681547a0801d4d4ac8465f82"}]
         * height : 170
         */

        private String birthday;
        private String image;
        private String nickName;
        private String sex;
        private String weight;
        private Object equipment;
        private String userId;
        private String phone;
        private String height;
        private List<ExInfoSetListBean> myExInfoSetList;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public Object getEquipment() {
            return equipment;
        }

        public void setEquipment(Object equipment) {
            this.equipment = equipment;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public List<ExInfoSetListBean> getExInfoSetList() {
            return myExInfoSetList;
        }

        public void setExInfoSetList(List<ExInfoSetListBean> myExInfoSetList) {
            this.myExInfoSetList = myExInfoSetList;
        }

        public static class ExInfoSetListBean {
            /**
             * exhibition : 1
             * friendId : 0d56716e5629475882d4f4bfc7c51420
             * addTime : 2018-12-07 17:16:29
             * updateTime : null
             * id : 1
             * setType : 1
             * userId : 5c2b58f0681547a0801d4d4ac8465f82
             */

            private int exhibition;
            private String friendId;
            private String addTime;
            private Object updateTime;
            private int id;
            private int setType;
            private String userId;

            public int getExhibition() {
                return exhibition;
            }

            public void setExhibition(int exhibition) {
                this.exhibition = exhibition;
            }

            public String getFriendId() {
                return friendId;
            }

            public void setFriendId(String friendId) {
                this.friendId = friendId;
            }

            public String getAddTime() {
                return addTime;
            }

            public void setAddTime(String addTime) {
                this.addTime = addTime;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSetType() {
                return setType;
            }

            public void setSetType(int setType) {
                this.setType = setType;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }
    }


    public static class BloodPressureDayBean {

        /**
         * rtc : 2019-01-07
         * maxDiastolic : 107
         * avgSystolic : 79
         * addTime : 2019-01-07 08:40
         * updateTime : 2019-01-07 11:25
         * id : 27115
         * deviceCode : E7:A7:0F:11:BE:B5
         * userId : 0d56716e5629475882d4f4bfc7c51420
         * avgDiastolic : 118
         * minSystolic : 83
         */

        private String rtc;
        private int maxDiastolic;
        private int avgSystolic;
        private String addTime;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String userId;
        private int avgDiastolic;
        private int minSystolic;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getMaxDiastolic() {
            return maxDiastolic;
        }

        public void setMaxDiastolic(int maxDiastolic) {
            this.maxDiastolic = maxDiastolic;
        }

        public int getAvgSystolic() {
            return avgSystolic;
        }

        public void setAvgSystolic(int avgSystolic) {
            this.avgSystolic = avgSystolic;
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

        public int getAvgDiastolic() {
            return avgDiastolic;
        }

        public void setAvgDiastolic(int avgDiastolic) {
            this.avgDiastolic = avgDiastolic;
        }

        public int getMinSystolic() {
            return minSystolic;
        }

        public void setMinSystolic(int minSystolic) {
            this.minSystolic = minSystolic;
        }
    }

//    public static class BloodPressureDayBean {
//        /**
//         * id : 91592
//         * deviceCode : W30_E3D9
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         * rtc : 2018-11-14
//         * systolic : 99
//         * diastolic : 342
//         * status : 0
//         * addTime : 2018-11-14 08:15
//         * updateTime : 2018-11-14 21:09
//         */
//
//        private int id;
//        private String deviceCode;
//        private String userId;
//        private String rtc;
//        private int systolic;
//        private int diastolic;
//        private int status;
//        private String addTime;
//        private String updateTime;
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
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getRtc() {
//            return rtc;
//        }
//
//        public void setRtc(String rtc) {
//            this.rtc = rtc;
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
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//
//        public String getAddTime() {
//            return addTime;
//        }
//
//        public void setAddTime(String addTime) {
//            this.addTime = addTime;
//        }
//
//        public String getUpdateTime() {
//            return updateTime;
//        }
//
//        public void setUpdateTime(String updateTime) {
//            this.updateTime = updateTime;
//        }
//    }

//
//    /**
//     * sleepDay : {"rtc":"2018-11-14","deepSleep":99,"shallowSleep":342,"wakeTime":"2018-11-14 07:35","addTime":"2018-11-14 08:15","soberLen":0,"sleepTime":"2018-11-14 23:00","sleepLen":441,"updateTime":"2018-11-14 21:09","id":91592,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7"}
//     * deviceDataMovement : {"rtc":"2018-11-14","distance":"4.21","addTime":"2018-11-15 07:35:52","timeLen":0,"id":295257,"deviceCode":"W30_E3D9","stepNumber":5710,"calories":305,"userId":"8c4c511a45374bb595e6fdf30bb878b7","status":0,"target":0}
//     * heartRateDay : {"rtc":"2018-11-14","addTime":"2018-11-14 08:15","minHeartRate":50,"updateTime":"2018-11-14 21:09","id":136088,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7","maxHeartRate":109,"avgHeartRate":70}
//     * resultCode : 001
//     * friendInfo : {"birthday":"1992-12-25","image":"http://47.90.83.197/image/2018/09/28/1538096519895.jpg","nickName":"我难道不是你的小可爱？","sex":"M","weight":"70","userId":"8c4c511a45374bb595e6fdf30bb878b7","phone":"18738546101","height":"180"}
//     */
//
//    private SleepDayBean sleepDay;
//    private DeviceDataMovementBean deviceDataMovement;
//    private HeartRateDayBean heartRateDay;
//    private String resultCode;
//    private FriendInfoBean friendInfo;
//
//    public SleepDayBean getSleepDay() {
//        return sleepDay;
//    }
//
//    public void setSleepDay(SleepDayBean sleepDay) {
//        this.sleepDay = sleepDay;
//    }
//
//    public DeviceDataMovementBean getDeviceDataMovement() {
//        return deviceDataMovement;
//    }
//
//    public void setDeviceDataMovement(DeviceDataMovementBean deviceDataMovement) {
//        this.deviceDataMovement = deviceDataMovement;
//    }
//
//    public HeartRateDayBean getHeartRateDay() {
//        return heartRateDay;
//    }
//
//    public void setHeartRateDay(HeartRateDayBean heartRateDay) {
//        this.heartRateDay = heartRateDay;
//    }
//
//    public String getResultCode() {
//        return resultCode;
//    }
//
//    public void setResultCode(String resultCode) {
//        this.resultCode = resultCode;
//    }
//
//    public FriendInfoBean getFriendInfo() {
//        return friendInfo;
//    }
//
//    public void setFriendInfo(FriendInfoBean friendInfo) {
//        this.friendInfo = friendInfo;
//    }
//
//    public static class SleepDayBean {
//        /**
//         * rtc : 2018-11-14
//         * deepSleep : 99
//         * shallowSleep : 342
//         * wakeTime : 2018-11-14 07:35
//         * addTime : 2018-11-14 08:15
//         * soberLen : 0
//         * sleepTime : 2018-11-14 23:00
//         * sleepLen : 441
//         * updateTime : 2018-11-14 21:09
//         * id : 91592
//         * deviceCode : W30_E3D9
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         */
//
//        private String rtc;
//        private int deepSleep;
//        private int shallowSleep;
//        private String wakeTime;
//        private String addTime;
//        private int soberLen;
//        private String sleepTime;
//        private int sleepLen;
//        private String updateTime;
//        private int id;
//        private String deviceCode;
//        private String userId;
//
//        public String getRtc() {
//            return rtc;
//        }
//
//        public void setRtc(String rtc) {
//            this.rtc = rtc;
//        }
//
//        public int getDeepSleep() {
//            return deepSleep;
//        }
//
//        public void setDeepSleep(int deepSleep) {
//            this.deepSleep = deepSleep;
//        }
//
//        public int getShallowSleep() {
//            return shallowSleep;
//        }
//
//        public void setShallowSleep(int shallowSleep) {
//            this.shallowSleep = shallowSleep;
//        }
//
//        public String getWakeTime() {
//            return wakeTime;
//        }
//
//        public void setWakeTime(String wakeTime) {
//            this.wakeTime = wakeTime;
//        }
//
//        public String getAddTime() {
//            return addTime;
//        }
//
//        public void setAddTime(String addTime) {
//            this.addTime = addTime;
//        }
//
//        public int getSoberLen() {
//            return soberLen;
//        }
//
//        public void setSoberLen(int soberLen) {
//            this.soberLen = soberLen;
//        }
//
//        public String getSleepTime() {
//            return sleepTime;
//        }
//
//        public void setSleepTime(String sleepTime) {
//            this.sleepTime = sleepTime;
//        }
//
//        public int getSleepLen() {
//            return sleepLen;
//        }
//
//        public void setSleepLen(int sleepLen) {
//            this.sleepLen = sleepLen;
//        }
//
//        public String getUpdateTime() {
//            return updateTime;
//        }
//
//        public void setUpdateTime(String updateTime) {
//            this.updateTime = updateTime;
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
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//    }
//
//    public static class DeviceDataMovementBean {
//        /**
//         * rtc : 2018-11-14
//         * distance : 4.21
//         * addTime : 2018-11-15 07:35:52
//         * timeLen : 0
//         * id : 295257
//         * deviceCode : W30_E3D9
//         * stepNumber : 5710
//         * calories : 305
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         * status : 0
//         * target : 0
//         */
//
//        private String rtc;
//        private String distance;
//        private String addTime;
//        private int timeLen;
//        private int id;
//        private String deviceCode;
//        private int stepNumber;
//        private int calories;
//        private String userId;
//        private int status;
//        private int target;
//
//        public String getRtc() {
//            return rtc;
//        }
//
//        public void setRtc(String rtc) {
//            this.rtc = rtc;
//        }
//
//        public String getDistance() {
//            return distance;
//        }
//
//        public void setDistance(String distance) {
//            this.distance = distance;
//        }
//
//        public String getAddTime() {
//            return addTime;
//        }
//
//        public void setAddTime(String addTime) {
//            this.addTime = addTime;
//        }
//
//        public int getTimeLen() {
//            return timeLen;
//        }
//
//        public void setTimeLen(int timeLen) {
//            this.timeLen = timeLen;
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
//        public int getCalories() {
//            return calories;
//        }
//
//        public void setCalories(int calories) {
//            this.calories = calories;
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
//
//        public int getTarget() {
//            return target;
//        }
//
//        public void setTarget(int target) {
//            this.target = target;
//        }
//    }
//
//    public static class HeartRateDayBean {
//        /**
//         * rtc : 2018-11-14
//         * addTime : 2018-11-14 08:15
//         * minHeartRate : 50
//         * updateTime : 2018-11-14 21:09
//         * id : 136088
//         * deviceCode : W30_E3D9
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         * maxHeartRate : 109
//         * avgHeartRate : 70
//         */
//
//        private String rtc;
//        private String addTime;
//        private int minHeartRate;
//        private String updateTime;
//        private int id;
//        private String deviceCode;
//        private String userId;
//        private int maxHeartRate;
//        private int avgHeartRate;
//
//        public String getRtc() {
//            return rtc;
//        }
//
//        public void setRtc(String rtc) {
//            this.rtc = rtc;
//        }
//
//        public String getAddTime() {
//            return addTime;
//        }
//
//        public void setAddTime(String addTime) {
//            this.addTime = addTime;
//        }
//
//        public int getMinHeartRate() {
//            return minHeartRate;
//        }
//
//        public void setMinHeartRate(int minHeartRate) {
//            this.minHeartRate = minHeartRate;
//        }
//
//        public String getUpdateTime() {
//            return updateTime;
//        }
//
//        public void setUpdateTime(String updateTime) {
//            this.updateTime = updateTime;
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
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public int getMaxHeartRate() {
//            return maxHeartRate;
//        }
//
//        public void setMaxHeartRate(int maxHeartRate) {
//            this.maxHeartRate = maxHeartRate;
//        }
//
//        public int getAvgHeartRate() {
//            return avgHeartRate;
//        }
//
//        public void setAvgHeartRate(int avgHeartRate) {
//            this.avgHeartRate = avgHeartRate;
//        }
//    }
//
//    public static class FriendInfoBean {
//        /**
//         * birthday : 1992-12-25
//         * image : http://47.90.83.197/image/2018/09/28/1538096519895.jpg
//         * nickName : 我难道不是你的小可爱？
//         * sex : M
//         * weight : 70
//         * userId : 8c4c511a45374bb595e6fdf30bb878b7
//         * phone : 18738546101
//         * height : 180
//         */
//
//        private String birthday;
//        private String image;
//        private String nickName;
//        private String sex;
//        private int see;
//        private String weight;
//        private String userId;
//        private String phone;
//        private String height;
//
//        public int getSee() {
//            return see;
//        }
//
//        public void setSee(int see) {
//            this.see = see;
//        }
//
//        public String getBirthday() {
//            return birthday;
//        }
//
//        public void setBirthday(String birthday) {
//            this.birthday = birthday;
//        }
//
//        public String getImage() {
//            return image;
//        }
//
//        public void setImage(String image) {
//            this.image = image;
//        }
//
//        public String getNickName() {
//            return nickName;
//        }
//
//        public void setNickName(String nickName) {
//            this.nickName = nickName;
//        }
//
//        public String getSex() {
//            return sex;
//        }
//
//        public void setSex(String sex) {
//            this.sex = sex;
//        }
//
//        public String getWeight() {
//            return weight;
//        }
//
//        public void setWeight(String weight) {
//            this.weight = weight;
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
//        public String getPhone() {
//            return phone;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }
//
//        public String getHeight() {
//            return height;
//        }
//
//        public void setHeight(String height) {
//            this.height = height;
//        }
//    }


}
