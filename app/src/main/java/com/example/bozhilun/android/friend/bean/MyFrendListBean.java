package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class MyFrendListBean {


    /**
     * myfriends : [{"birthday":"1992-12-25","image":"http://47.90.83.197/image/2018/11/24/1543020102182.png","todayThumbs":1,"addTime":"2017-05-09 14:34:35","nickName":"我难道不是你的小可爱？","sex":"M","isThumbs":1,"weight":"70","equipment":"W30","updateTime":"2018-12-05 11:12","lon":"113.721745","deviceCode":"W30_E3D9","type":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","phone":"18738546101","id":3478,"stepNumber":7219,"pwd":"e10adc3949ba59abbe56e057f20f883e","lat":"22.990020","height":"180","status":0}]
     * myInfo : {"birthday":"1995-06-15","image":"http://47.90.83.197/image/2018/11/24/1543019974622.jpg","todayThumbs":0,"nickName":"hello RaceFitPro","sex":"M","weight":"60 kg","equipment":"W30","userId":"0d56716e5629475882d4f4bfc7c51420","phone":"14791685830","stepNumber":3522,"height":"170 cm","rankNo":2}
     * resultCode : 001
     */

    private MyInfoBean myInfo;
    private String resultCode;
    private List<MyfriendsBean> myfriends;

    public MyInfoBean getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MyInfoBean myInfo) {
        this.myInfo = myInfo;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<MyfriendsBean> getMyfriends() {
        return myfriends;
    }

    public void setMyfriends(List<MyfriendsBean> myfriends) {
        this.myfriends = myfriends;
    }

    public static class MyInfoBean {
        /**
         * birthday : 1995-06-15
         * image : http://47.90.83.197/image/2018/11/24/1543019974622.jpg
         * todayThumbs : 0
         * nickName : hello RaceFitPro
         * sex : M
         * weight : 60 kg
         * equipment : W30
         * userId : 0d56716e5629475882d4f4bfc7c51420
         * phone : 14791685830
         * stepNumber : 3522
         * height : 170 cm
         * rankNo : 2
         */

        private String birthday;
        private String image;
        private int todayThumbs;
        private String nickName;
        private String sex;
        private String weight;
        private String equipment;
        private String userId;
        private String phone;
        private int stepNumber;
        private String height;
        private int rankNo;

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

        public int getTodayThumbs() {
            return todayThumbs;
        }

        public void setTodayThumbs(int todayThumbs) {
            this.todayThumbs = todayThumbs;
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

        public String getEquipment() {
            return equipment;
        }

        public void setEquipment(String equipment) {
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

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public int getRankNo() {
            return rankNo;
        }

        public void setRankNo(int rankNo) {
            this.rankNo = rankNo;
        }
    }

    public static class MyfriendsBean {
        /**
         * birthday : 1992-12-25
         * image : http://47.90.83.197/image/2018/11/24/1543020102182.png
         * todayThumbs : 1
         * addTime : 2017-05-09 14:34:35
         * nickName : 我难道不是你的小可爱？
         * sex : M
         * isThumbs : 1
         * weight : 70
         * equipment : W30
         * updateTime : 2018-12-05 11:12
         * lon : 113.721745
         * deviceCode : W30_E3D9
         * type : 0
         * userId : 8c4c511a45374bb595e6fdf30bb878b7
         * phone : 18738546101
         * id : 3478
         * stepNumber : 7219
         * pwd : e10adc3949ba59abbe56e057f20f883e
         * lat : 22.990020
         * height : 180
         * status : 0
         */

        private String birthday;
        private String image;
        private int todayThumbs;
        private String addTime;
        private String nickName;
        private String sex;
        private int isThumbs;
        private String weight;
        private String equipment;
        private String updateTime;
        private String lon;
        private String deviceCode;
        private int type;
        private String userId;
        private String phone;
        private int id;
        private int stepNumber;
        private String pwd;
        private String lat;
        private String height;
        private int status;

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

        public int getTodayThumbs() {
            return todayThumbs;
        }

        public void setTodayThumbs(int todayThumbs) {
            this.todayThumbs = todayThumbs;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
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

        public int getIsThumbs() {
            return isThumbs;
        }

        public void setIsThumbs(int isThumbs) {
            this.isThumbs = isThumbs;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getEquipment() {
            return equipment;
        }

        public void setEquipment(String equipment) {
            this.equipment = equipment;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
