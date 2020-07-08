package com.example.bozhilun.android.mvp;

import android.app.Dialog;

/**
 * Created by Admin
 * Date 2020/5/14
 */
public interface IBaseView {

    Dialog showLoadDialog();

    void cancleDialog();
}
