package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class NewApplyFrendBean {


    /**
     * resultCode : 001
     * myApply : [{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/11/08/1541656649350.jpg","nickName":"测试①","sex":"M","weight":"60 kg","userId":"bcc676946b254e008b4050f6d11e8599","friendStatus":2,"phone":"Zza1@163.com","height":"170 cm"},{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/11/08/1541656775454.jpg","nickName":"测试②","sex":"M","weight":"60 kg","userId":"fb1285a0a59346ed95d5c35f8761ab85","friendStatus":2,"phone":"Zza2@163.com","height":"170 cm"},{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/11/08/1541656775454.jpg","nickName":"测试②","sex":"M","weight":"60 kg","userId":"fb1285a0a59346ed95d5c35f8761ab85","friendStatus":2,"phone":"Zza2@163.com","height":"170 cm"},{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/11/08/1541656775454.jpg","nickName":"测试②","sex":"M","weight":"60 kg","userId":"fb1285a0a59346ed95d5c35f8761ab85","friendStatus":3,"phone":"Zza2@163.com","height":"170 cm"},{"birthday":"1995-06-15","image":"http://47.90.83.197/image/2018/11/07/1541586772350.jpg","nickName":"手机","sex":"M","weight":"60 kg","userId":"0d56716e5629475882d4f4bfc7c51420","friendStatus":2,"phone":"14791685830","height":"170 cm"}]
     */

    private String resultCode;
    private List<MyApplyBean> myApply;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<MyApplyBean> getMyApply() {
        return myApply;
    }

    public void setMyApply(List<MyApplyBean> myApply) {
        this.myApply = myApply;
    }

    public static class MyApplyBean {
        /**
         * birthday : 2000-06-15
         * image : http://47.90.83.197/image/2018/11/08/1541656649350.jpg
         * nickName : 测试①
         * sex : M
         * weight : 60 kg
         * userId : bcc676946b254e008b4050f6d11e8599
         * friendStatus : 2
         * phone : Zza1@163.com
         * height : 170 cm
         */

        private String birthday;
        private String image;
        private String nickName;
        private String sex;
        private String weight;
        private String userId;
        private int friendStatus;
        private String phone;
        private String height;

        public MyApplyBean(String birthday, String image, String nickName, String sex, String weight, String userId, int friendStatus, String phone, String height) {
            this.birthday = birthday;
            this.image = image;
            this.nickName = nickName;
            this.sex = sex;
            this.weight = weight;
            this.userId = userId;
            this.friendStatus = friendStatus;
            this.phone = phone;
            this.height = height;
        }

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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getFriendStatus() {
            return friendStatus;
        }

        public void setFriendStatus(int friendStatus) {
            this.friendStatus = friendStatus;
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
    }
}
