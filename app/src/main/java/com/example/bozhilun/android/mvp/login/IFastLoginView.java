package com.example.bozhilun.android.mvp.login;


import com.example.bozhilun.android.mvp.IBaseView;

/**
 * 手机号快捷登录
 * Created by Admin
 * Date 2020/5/14
 */
public interface IFastLoginView extends IBaseView {

    //选择区号
    String getPhoneAreaCode();

    //输入手机号码
    String getInputPhone();

    //获取手机号验证码
    String getPhoneCode();

    //三方登录参数
    String getThirdLogin();

    //登录成功
    void fastLoginSuccess(int code, String dataBean);

    //登录失败
    void fastLoginFail(String error);
}
