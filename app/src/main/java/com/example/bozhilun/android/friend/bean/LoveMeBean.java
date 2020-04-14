package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class LoveMeBean {
    /**
     * resultCode : 001
     * friendList : [{"birthday":"1992-12-25","image":"http://47.90.83.197/image/2018/11/24/1543020102182.png","addTime":"2017-05-09 14:34:35","nickName":"我难道不是你的小可爱？","sex":"M","weight":"70","equipment":"W30","updateTime":"2018-12-05 11:12","lon":"113.721745","type":0,"userId":"8c4c511a45374bb595e6fdf30bb878b7","phone":"18738546101","id":3478,"pwd":"e10adc3949ba59abbe56e057f20f883e","lat":"22.990020","height":"180","status":0}]
     */

    private String resultCode;
    private List<FriendListBean> friendList;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<FriendListBean> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<FriendListBean> friendList) {
        this.friendList = friendList;
    }

    public static class FriendListBean {

        /**
         * phone : aabbcc@163.com
         * nickname : 123456
         * sex : F
         * birthday : 2000-02-01
         * height : 170
         * weight : 61
         * image : http://47.90.83.197/image/2019/06/20/1561019605227.jpg
         * userid : 94f3b4bb5cb846ffb3aab0527f34bf6c
         * equipment : B36
         * mac : DC:62:31:85:19:E5
         * code : 2
         * count : 2
         * stepNumber : 123
         * friendStatus : 1
         * isThumbs :
         * lastThumbsDay :
         * day :
         * see : null
         * hasBloodPressure : null
         * todayThumbs : null
         * rankNo : null
         * exInfoSetList : null
         */

        private String phone;
        private String nickname;
        private String sex;
        private String birthday;
        private String height;
        private String weight;
        private String image;
        private String userid;
        private String equipment;
        private String mac;
        private int code;
        private int count;
        private int stepNumber;
        private int friendStatus;
        private String isThumbs;
        private String lastThumbsDay;
        private String day;
        private Object see;
        private Object hasBloodPressure;
        private Object todayThumbs;
        private Object rankNo;
        private Object exInfoSetList;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getEquipment() {
            return equipment;
        }

        public void setEquipment(String equipment) {
            this.equipment = equipment;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public int getFriendStatus() {
            return friendStatus;
        }

        public void setFriendStatus(int friendStatus) {
            this.friendStatus = friendStatus;
        }

        public String getIsThumbs() {
            return isThumbs;
        }

        public void setIsThumbs(String isThumbs) {
            this.isThumbs = isThumbs;
        }

        public String getLastThumbsDay() {
            return lastThumbsDay;
        }

        public void setLastThumbsDay(String lastThumbsDay) {
            this.lastThumbsDay = lastThumbsDay;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public Object getSee() {
            return see;
        }

        public void setSee(Object see) {
            this.see = see;
        }

        public Object getHasBloodPressure() {
            return hasBloodPressure;
        }

        public void setHasBloodPressure(Object hasBloodPressure) {
            this.hasBloodPressure = hasBloodPressure;
        }

        public Object getTodayThumbs() {
            return todayThumbs;
        }

        public void setTodayThumbs(Object todayThumbs) {
            this.todayThumbs = todayThumbs;
        }

        public Object getRankNo() {
            return rankNo;
        }

        public void setRankNo(Object rankNo) {
            this.rankNo = rankNo;
        }

        public Object getExInfoSetList() {
            return exInfoSetList;
        }

        public void setExInfoSetList(Object exInfoSetList) {
            this.exInfoSetList = exInfoSetList;
        }
    }
}
