package com.example.bozhilun.android.w30s.utils.httputils;

/**
 * Created by Administrator on 2018/4/3.
 */

public interface RequestView {

    void showLoadDialog(int what);
    void successData(int what, Object object, int daystag);
    void failedData(int what, Throwable e);
    void closeLoadDialog(int what);
}
