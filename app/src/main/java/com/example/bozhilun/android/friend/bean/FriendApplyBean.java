package com.example.bozhilun.android.friend.bean;

import java.util.List;

/**
 * Created by Admin
 * Date 2019/7/11
 */
public class FriendApplyBean {


    /**
     * code : 200
     * msg : null
     * data : [{"phone":"33333333@163.com","nickname":"123","sex":"M","birthday":"2000-06-15","height":"170 cm","weight":"60 kg","image":"http://47.90.83.197/image/2017/08/16/1502886184823.jpg","userid":"e028098595dc49909f908cb8f0b4b2a1","equipment":"","mac":"","code":"","count":"","stepNumber":123,"friendStatus":0,"isThumbs":"","lastThumbsDay":"","day":"","see":"","hasBloodPressure":"","todayThumbs":"","rankNo":"","exInfoSetList":""}]
     */

    private int code;
    private Object msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * phone : 33333333@163.com
         * nickname : 123
         * sex : M
         * birthday : 2000-06-15
         * height : 170 cm
         * weight : 60 kg
         * image : http://47.90.83.197/image/2017/08/16/1502886184823.jpg
         * userid : e028098595dc49909f908cb8f0b4b2a1
         * equipment :
         * mac :
         * code :
         * count :
         * stepNumber : 123
         * friendStatus : 0
         * isThumbs :
         * lastThumbsDay :
         * day :
         * see :
         * hasBloodPressure :
         * todayThumbs :
         * rankNo :
         * exInfoSetList :
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
        private String code;
        private String count;
        private int stepNumber;
        private int friendStatus;
        private String isThumbs;
        private String lastThumbsDay;
        private String day;
        private String see;
        private String hasBloodPressure;
        private String todayThumbs;
        private String rankNo;
        private String exInfoSetList;

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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
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

        public String getSee() {
            return see;
        }

        public void setSee(String see) {
            this.see = see;
        }

        public String getHasBloodPressure() {
            return hasBloodPressure;
        }

        public void setHasBloodPressure(String hasBloodPressure) {
            this.hasBloodPressure = hasBloodPressure;
        }

        public String getTodayThumbs() {
            return todayThumbs;
        }

        public void setTodayThumbs(String todayThumbs) {
            this.todayThumbs = todayThumbs;
        }

        public String getRankNo() {
            return rankNo;
        }

        public void setRankNo(String rankNo) {
            this.rankNo = rankNo;
        }

        public String getExInfoSetList() {
            return exInfoSetList;
        }

        public void setExInfoSetList(String exInfoSetList) {
            this.exInfoSetList = exInfoSetList;
        }
    }
}
