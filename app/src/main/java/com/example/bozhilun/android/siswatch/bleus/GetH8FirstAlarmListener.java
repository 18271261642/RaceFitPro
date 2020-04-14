package com.example.bozhilun.android.siswatch.bleus;

/**
 * 获取所有闹钟的接口
 */
public interface GetH8FirstAlarmListener {

    void getFirstAlarm(byte[] data);

    void getSecondAlarm(byte[] data);

    void getThirdAlarm(byte[] data);
}
