package com.example.bozhilun.android.bean;

/**
 * Created by Administrator on 2018/7/26.
 */

public class UserInfoBean {


    /**
     * phone : 15916947377
     * nickname : 超级管理员
     * sex : F
     * birthday : 1994-10-10
     * height : 172
     * weight : 60
     * image : http://47.90.83.197/image/2019/06/22/1561167386104.jpg
     * userid : 5c2b58f0681547a0801d4d4ac8465f82
     * equipment : B30
     * mac : F0:1E:9B:12:36:85
     * code : 1
     * count : 1
     * stepNumber : 123
     * friendStatus : 1
     * isThumbs : 1
     * lastThumbsDay : 1
     * day : null
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
    private int isThumbs;
    private int lastThumbsDay;
    private Object day;
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

    public int getIsThumbs() {
        return isThumbs;
    }

    public void setIsThumbs(int isThumbs) {
        this.isThumbs = isThumbs;
    }

    public int getLastThumbsDay() {
        return lastThumbsDay;
    }

    public void setLastThumbsDay(int lastThumbsDay) {
        this.lastThumbsDay = lastThumbsDay;
    }

    public Object getDay() {
        return day;
    }

    public void setDay(Object day) {
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

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", image='" + image + '\'' +
                ", userid='" + userid + '\'' +
                ", equipment='" + equipment + '\'' +
                ", mac='" + mac + '\'' +
                ", code=" + code +
                ", count=" + count +
                ", stepNumber=" + stepNumber +
                ", friendStatus=" + friendStatus +
                ", isThumbs=" + isThumbs +
                ", lastThumbsDay=" + lastThumbsDay +
                ", day=" + day +
                ", see=" + see +
                ", hasBloodPressure=" + hasBloodPressure +
                ", todayThumbs=" + todayThumbs +
                ", rankNo=" + rankNo +
                ", exInfoSetList=" + exInfoSetList +
                '}';
    }
}
