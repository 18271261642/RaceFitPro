package com.example.bozhilun.android.mvp;



/**
 * Created by Admin
 * Date 2020/5/14
 */
public interface CommDataBackListener<T> {

    void success(T t);

    void failed(String error);
}
