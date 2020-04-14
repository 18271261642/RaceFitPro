package com.example.bozhilun.android.adpter;

import android.view.KeyEvent;
import android.view.View;

import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * 第三方登录Web界面Logo或者头隐藏
 * 不用java代码调用，manifest已经配置
 */
public class MyAdapter extends AuthorizeAdapter {
    @Override
    public void onCreate() {
        super.onCreate();
//        hideShareSDKLogo();
//        disablePopUpAnimation();
        TitleLayout llTitle = getTitleLayout();
        llTitle.setVisibility(View.GONE);
//        llTitle.getChildAt(1).setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent event) {
        getActivity().finish();
        return super.onKeyEvent(keyCode, event);
    }
}
