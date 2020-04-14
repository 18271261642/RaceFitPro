package com.example.bozhilun.android.b31.bpoxy.util;

/**
 * 时间制式的
 * Created by Administrator on 2018/12/21.
 */

public class TranStrUtil {
    public static String getSpo2hTimeString(int time, boolean is24hour) {
        int hour = time / 60;
        int minute = time % 60;
        if (is24hour) {
            return get2TimeStr(hour) + ":" + get2TimeStr(minute);
        } else {
            return get12Hour(hour) + ":" + get2TimeStr(minute) + getAPStr(hour);
        }
    }

    private static String getAPStr(int hour) {
        return hour > 12 ? "pm" : "am";
    }

    private static String get2TimeStr(int value) {
        return value >= 10 ? String.valueOf(value) : "0" + value;
    }

    private static String get12Hour(int hour) {
        return hour == 12 ? get2TimeStr(hour) : get2TimeStr(hour % 12);
    }
}
