package com.example.bozhilun.android.mvp.login;

/**
 * Created by Admin
 * Date 2020/5/15
 */
public class UserLogParamsBean {

    private String phone;

    private String pwd;

    public UserLogParamsBean(String phone, String pwd) {
        this.phone = phone;
        this.pwd = pwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
