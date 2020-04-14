package com.example.bozhilun.android.b30.service;

/**
 * 验证B30系列设备密码的接口
 * Created by Admin
 * Date 2018/11/20
 */
public interface VerB30PwdListener {

    /**
     * 密码验证失败
     */
    void verPwdFailed();

    /**
     * 密码验证成功
     */
    void verPwdSucc();
}
