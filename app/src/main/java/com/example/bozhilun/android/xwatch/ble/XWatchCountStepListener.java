package com.example.bozhilun.android.xwatch.ble;

/**
 * xWatch手表返回总步数，距离，卡路里信息
 * Created by Admin
 * Date 2020/2/20
 */
public interface XWatchCountStepListener {


    void backDeviceCountStep(int countStep,double kcalStr);

    void backDeviceDisance(double disance,int sportTime);
}
