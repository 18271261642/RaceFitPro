package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class NewFrendApplyBean {
    /**
     * resultCode : 001
     * applyList : [{"birthday":"1995-06-15","image":null,"nickName":"an","sex":"M","weight":"60 kg","userId":"0d56716e5629475882d4f4bfc7c51420","phone":"14791685830","height":"170 cm"},{"birthday":"1995-06-15","image":null,"nickName":"an","sex":"M","weight":"60 kg","userId":"0d56716e5629475882d4f4bfc7c51420","phone":"14791685830","height":"170 cm"}]
     */

    private String resultCode;
    private List<ApplyListBean> applyList;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<ApplyListBean> getApplyList() {
        return applyList;
    }

    public void setApplyList(List<ApplyListBean> applyList) {
        this.applyList = applyList;
    }

    public static class ApplyListBean {
        /**
         * birthday : 1995-06-15
         * image : null
         * nickName : an
         * sex : M
         * weight : 60 kg
         * userId : 0d56716e5629475882d4f4bfc7c51420
         * phone : 14791685830
         * height : 170 cm
         */

        private String birthday;
        private Object image;
        private String nickName;
        private String sex;
        private String weight;
        private String userId;
        private String phone;
        private String height;

        public ApplyListBean(String birthday, Object image, String nickName, String sex, String weight, String userId, String phone, String height) {
            this.birthday = birthday;
            this.image = image;
            this.nickName = nickName;
            this.sex = sex;
            this.weight = weight;
            this.userId = userId;
            this.phone = phone;
            this.height = height;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public Object getImage() {
            return image;
        }

        public void setImage(Object image) {
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
