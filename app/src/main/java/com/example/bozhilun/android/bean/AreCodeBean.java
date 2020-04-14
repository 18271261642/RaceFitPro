package com.example.bozhilun.android.bean;

/**
 * 注册时手机验证码区号
 * Created by Admin
 * Date 2019/6/28
 */
public class AreCodeBean {

    /**
     * Countries and Regions 	国家或地区 	国际域名缩写 	电话代码 	时差
     * Angola 	安哥拉 	AO 	244
     */


    /**
     * 国家英文代码
     */
    private String phoneRegious;

    /**
     * 国家名称
     */
    private String phoneCountry;

    /**
     * 国际域名缩写
     */
    private String phoneAreCode;

    /**
     * 区号
     */
    private String phoneCode;

    public String getPhoneRegious() {
        return phoneRegious;
    }

    public void setPhoneRegious(String phoneRegious) {
        this.phoneRegious = phoneRegious;
    }

    public String getPhoneCountry() {
        return phoneCountry;
    }

    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    public String getPhoneAreCode() {
        return phoneAreCode;
    }

    public void setPhoneAreCode(String phoneAreCode) {
        this.phoneAreCode = phoneAreCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    @Override
    public String toString() {
        return "AreCodeBean{" +
                "phoneRegious='" + phoneRegious + '\'' +
                ", phoneCountry='" + phoneCountry + '\'' +
                ", phoneAreCode='" + phoneAreCode + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                '}';
    }
}
