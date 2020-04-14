package com.example.bozhilun.android.w30s.ble;

/**
 * Created by Admin
 * Date 2019/7/1
 */
public interface ConnStatusListener {

    void connStatus(int status);

    void setNotiStatus(int code);
}
