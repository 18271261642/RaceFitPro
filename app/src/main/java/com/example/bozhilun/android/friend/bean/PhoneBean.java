package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class PhoneBean {


    /**
     * CheckRegister : [{"phone":"12345678910","isFriend":0,"user":null,"contacts":"大神志光"},{"phone":"14791665868","isFriend":0,"user":null,"contacts":"14791665868"},{"phone":"14791685830","isFriend":0,"user":{"birthday":"1995-06-15","image":"http://47.90.83.197/image/2018/11/24/1543019974622.jpg","nickName":"hello RaceFitPro","sex":"M","weight":"60 kg","equipment":"W30","userId":"0d56716e5629475882d4f4bfc7c51420","phone":"14791685830","height":"170 cm"},"contacts":"安！"},{"phone":"15591060973","isFriend":0,"user":{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/05/23/1527039867259.jpg","nickName":"我是谁(\u203a´ω`\u2039 )消瘦","sex":"M","weight":"60 kg","equipment":null,"userId":"5ec44cb29f584d60bfa6eb43064230cb","phone":"15591060973","height":"170 cm"},"contacts":"哈哈"},{"phone":"15591060973","isFriend":0,"user":{"birthday":"2000-06-15","image":"http://47.90.83.197/image/2018/05/23/1527039867259.jpg","nickName":"我是谁(\u203a´ω`\u2039 )消瘦","sex":"M","weight":"60 kg","equipment":null,"userId":"5ec44cb29f584d60bfa6eb43064230cb","phone":"15591060973","height":"170 cm"},"contacts":"555"},{"phone":"18681484832","isFriend":1,"user":{"birthday":"1988-01-01","image":"http://47.90.83.197/image/2018/11/24/1543027188415.jpg","nickName":"bzl1234深圳","sex":"F","weight":"59","equipment":null,"userId":"d770b07449a3499081671a32aee086ff","phone":"18681484832","height":"173"},"contacts":"hh"}]
     * resultCode : 001
     */

    private String resultCode;
    private List<CheckRegisterBean> CheckRegister;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<CheckRegisterBean> getCheckRegister() {
        return CheckRegister;
    }

    public void setCheckRegister(List<CheckRegisterBean> CheckRegister) {
        this.CheckRegister = CheckRegister;
    }

    public static class CheckRegisterBean {
        /**
         * phone : 12345678910
         * isFriend : 0
         * user : null
         * contacts : 大神志光
         */

        private String phone;
        private int isFriend;
        private Object user;
        private String contacts;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(int isFriend) {
            this.isFriend = isFriend;
        }

        public Object getUser() {
            return user;
        }

        public void setUser(Object user) {
            this.user = user;
        }

        public String getContacts() {
            return contacts;
        }

        public void setContacts(String contacts) {
            this.contacts = contacts;
        }
    }
}
