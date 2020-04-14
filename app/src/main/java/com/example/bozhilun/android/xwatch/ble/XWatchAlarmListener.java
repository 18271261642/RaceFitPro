package com.example.bozhilun.android.xwatch.ble;

import com.example.bozhilun.android.b18.modle.B18AlarmBean;

/**
 * Created by Admin
 * Date 2020/2/22
 */
public interface XWatchAlarmListener {

    void backAlarmOne(B18AlarmBean b18AlarmBean1);

    void backAlarmSecond(B18AlarmBean b18AlarmBean2);

    void backAlarmThird(B18AlarmBean b18AlarmBean3);
}
