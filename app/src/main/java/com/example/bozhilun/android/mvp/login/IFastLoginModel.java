package com.example.bozhilun.android.mvp.login;

import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.mvp.CommDataBackListener;
import com.example.bozhilun.android.mvp.CommDataBean;
import com.example.bozhilun.android.mvp.net.BaseResponse;

/**
 * Created by Admin
 * Date 2020/5/14
 */
public interface IFastLoginModel {

    void getFastLoginPhoneCode(String phoneAreaCode, String phoneStr, CommDataBackListener<String> commDataBeanCommDataBackListener);

    void fastLogin(String phoneAreaCode, String phoneStr, CommDataBackListener<UserInfoBean> commDataBeanCommDataBackListener);

    void visitorLogin(CommDataBackListener<UserInfoBean> commDataBeanCommDataBackListener);

    void thirdLogin(String jsonParams ,CommDataBackListener<UserInfoBean> commDataBackListener);
}
