package com.example.bozhilun.android.mvp.login;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.mvp.BaseMvpPresent;
import com.example.bozhilun.android.mvp.CommDataBackListener;
import com.example.bozhilun.android.mvp.CommDataBean;
import com.example.bozhilun.android.mvp.net.BaseResponse;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;


/**
 * Created by Admin
 * Date 2020/5/14
 */
public class IFastPresent extends BaseMvpPresent<IFastLoginView> {

    private IFastLoginModel iFastLoginModel;

    public IFastPresent() {
        iFastLoginModel = new IFastLoginImpl();
    }

    //获取验证码
    public void getPhoneCode(){
        if(view == null)
            return;
        if(WatchUtils.isEmpty(view.getInputPhone())){
            ToastUtil.showShort(MyApp.getContext(),"请输入手机号!");
            return;
        }
        iFastLoginModel.getFastLoginPhoneCode(view.getPhoneAreaCode(), view.getInputPhone(), new CommDataBackListener<String>() {
            @Override
            public void success(String commDataBean) {
                view.fastLoginSuccess(0x01,commDataBean);
            }

            @Override
            public void failed(String error) {
                view.fastLoginFail(error);
            }
        });
    }

    //快捷登录
    public void fastLogin(){
        if(view == null)
            return;
        if(WatchUtils.isEmpty(view.getInputPhone()) || WatchUtils.isEmpty(view.getPhoneCode())){
            return;
        }
        iFastLoginModel.fastLogin(view.getPhoneCode(), view.getInputPhone(), new CommDataBackListener<UserInfoBean>() {
            @Override
            public void success(UserInfoBean commDataBean) {
                view.fastLoginSuccess(0x02,new Gson().toJson(commDataBean));
            }

            @Override
            public void failed(String error) {
                view.fastLoginFail(error);
            }
        });
    }

    public void visitorLogin(){
        if(view == null)
            return;
        iFastLoginModel.visitorLogin(new CommDataBackListener<UserInfoBean>() {


            @Override
            public void success(UserInfoBean dataBean) {
                view.fastLoginSuccess(0x03,new Gson().toJson(dataBean));
            }

            @Override
            public void failed(String error) {
                view.fastLoginFail(error);
            }
        });
    }

    //三方登录
    public void thirdFastLogin(){
        if(view == null)
            return;
        if(view.getThirdLogin() == null)
            return;
        iFastLoginModel.thirdLogin(view.getThirdLogin(), new CommDataBackListener<UserInfoBean>() {
            @Override
            public void success(UserInfoBean userInfoBean) {
                view.fastLoginSuccess(0x04,new Gson().toJson(userInfoBean));
            }

            @Override
            public void failed(String error) {
                view.fastLoginFail(error);
            }
        });
    }

}
