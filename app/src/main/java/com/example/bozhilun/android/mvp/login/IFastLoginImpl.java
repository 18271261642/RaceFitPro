package com.example.bozhilun.android.mvp.login;


import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.mvp.CommDataBackListener;
import com.example.bozhilun.android.mvp.CommDataBean;
import com.example.bozhilun.android.mvp.net.BaseObserver;
import com.example.bozhilun.android.mvp.net.BaseResponse;
import com.example.bozhilun.android.mvp.net.RetrofitFactory;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;

/**
 * Created by Admin
 * Date 2020/5/14
 */
public class IFastLoginImpl implements IFastLoginModel {


    @Override
    public void getFastLoginPhoneCode(String phoneAreaCode, String phoneStr, final CommDataBackListener<String> commDataBeanCommDataBackListener) {
        try {
            RetrofitFactory.getRetrofitFactory().fastLoginGetPhoneCode(phoneAreaCode, phoneStr, new BaseObserver<String>() {
                @Override
                protected void onSuccess(String data) {
                    commDataBeanCommDataBackListener.success(data);
                }

                @Override
                protected void onFailBack(String message) {
                    commDataBeanCommDataBackListener.failed(message);
                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //手机号快捷登录
    @Override
    public void fastLogin(String phoneAreaCode, String phoneStr, final CommDataBackListener<UserInfoBean> commDataBeanCommDataBackListener) {
        try {
            RetrofitFactory.getRetrofitFactory().fastLogin(phoneStr, phoneAreaCode, new BaseObserver<UserInfoBean>() {
                @Override
                protected void onSuccess(UserInfoBean data) {
                    commDataBeanCommDataBackListener.success(data);
                }

                @Override
                protected void onFailBack(String message) {
                    commDataBeanCommDataBackListener.failed(message);
                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //游客登录
    @Override
    public void visitorLogin(final CommDataBackListener<UserInfoBean> commDataBeanCommDataBackListener) {
        try {
            RetrofitFactory.getRetrofitFactory().vistorFastLogin(new BaseObserver<UserInfoBean>() {
                @Override
                protected void onSuccess(UserInfoBean data) {
                    commDataBeanCommDataBackListener.success(data);
                }

                @Override
                protected void onFailBack(String message) {
                    commDataBeanCommDataBackListener.failed(message);
                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //第三方登录
    @Override
    public void thirdLogin(String jsonParams, final CommDataBackListener<UserInfoBean> commDataBackListener) {
        try {
            RetrofitFactory.getRetrofitFactory().thirdLogin(jsonParams, new BaseObserver<UserInfoBean>() {
                @Override
                protected void onSuccess(UserInfoBean data) {
                    commDataBackListener.success(data);
                }

                @Override
                protected void onFailBack(String message) {
                    commDataBackListener.failed(message);
                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
