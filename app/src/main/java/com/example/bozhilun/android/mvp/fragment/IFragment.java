package com.example.bozhilun.android.mvp.fragment;

import android.os.Bundle;

/**
 * Created by Admin
 * Date 2020/5/22
 */
public interface IFragment {

    int getLayout();

    void initView();

    void initData(Bundle savedInstanceState);
}
