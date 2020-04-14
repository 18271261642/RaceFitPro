package com.suchengkeji.android.w30sblelibrary;

import android.util.Log;

import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * 舟海设备蓝牙指令W30，W31，W37
 */

public class W30SBLEGattAttributes {
    //客户特征配置，助于收到设备通知
    static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // 系统=服务ID
    static final UUID UUID_SYSTEM_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    // 系统=写入数据ID
    public static final UUID UUID_SYSTEM_WRITE = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    // 系统=读取数据ID
    static final UUID UUID_SYSTEM_READ = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");

    //写入睡眠时间
    static final int BleWriteSleepTime = 80;
    private final static String TAG = "====>>>" + W30SBLEGattAttributes.class.getSimpleName();

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //================== 蓝牙KEY===========================

    // 蓝牙KEY
    static final int Key_Motion = 0x02;//运动步数
    static final int Key_Sleep = 0x03;//睡眠
    static final int Key_Complete = 0x04;//完成标志
    static final int Key_Photo = 0x06;//遥控拍照
    static final int Key_FindPhone = 0x07;
    static final int Key_DeviceInfo = 0x08;
    static final int Key_HangPhone = 0x0c;//测量心率
    static final int Key_MoHeart = 0x0d;//运动心率


//================================发送数据指令=====================

    /**
     * 同步时间
     * 通过这指令可以获取到，运动，心率，睡眠，设备信息等数据。
     *
     * @return
     */
    public static byte[] syncTime() {
        byte[] time = new byte[17];
        time[0] = (byte) 0xab;
        time[3] = (byte) 9;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",Locale.CHINA);
        Date tempDate = new Date(System.currentTimeMillis());
        String dateString = sdf.format(tempDate);
        char[] hexChars = dateString.toCharArray();
        int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]);
        int mon = charToByte(hexChars[4]) * 10 + charToByte(hexChars[5]);
        int day = charToByte(hexChars[6]) * 10 + charToByte(hexChars[7]);
        int hour = charToByte(hexChars[8]) * 10 + charToByte(hexChars[9]);
        int min = charToByte(hexChars[10]) * 10 + charToByte(hexChars[11]);
        int sec = charToByte(hexChars[12]) * 10 + charToByte(hexChars[13]);
        time[8] = 1;
        time[10] = 1;
        time[12] = 4;
        time[13] = (byte) ((year << 2) | (mon >> 2));
        time[14] = (byte) (((mon & 0x03) << 6) | (day << 1) | (hour >> 4));
        time[15] = (byte) (((hour & 0x0f) << 4) | (min >> 2));
        time[16] = (byte) (((min & 0x03) << 6) | (sec));
        return time;
    }


    /**
     * 找手环
     *
     * @return
     */
    //0x18
    public static byte[] findDeviceInstra() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;
        data[8] = 0x01;
        data[10] = 0x18;
        return data;
    }

    /**
     * 更新版本
     *
     * @return
     */
    public static byte[] upGradeDevice() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;
        data[8] = 0x01;
        data[10] = 0x0c;
        return data;
    }

    /**
     * 获取设备信息=电量.版本号
     * 息[-85, 0, 0, 5, 0, 0, 0, 0, 1, 0, 28, 0, 0]===
     *
     * @return
     */
    public static byte[] getDeviceInfo() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 5;
        data[8] = 1;
        data[10] = (byte) 0x1c;
        data[12] = 0;
        return data;
    }


    /**
     * 设置用户资料
     *
     * @param isMale 1:男性 ; 2:女性
     * @param age    年龄
     * @param hight  身高cm
     * @param weight 体重kg
     * @return
     */
    public static byte[] setUserProfile(int isMale, int age, int hight, int weight) {
        byte[] userInfo = new byte[16];
        userInfo[0] = (byte) 0xab;
        userInfo[3] = 8;
        userInfo[8] = 1;
        userInfo[10] = 4;
        userInfo[12] = 4;
        userInfo[13] = (byte) ((isMale << 7) | age);
        userInfo[14] = (byte) hight;
        userInfo[15] = (byte) weight;
        return userInfo;
    }


    /**
     * 单位设置
     *
     * @param value 0=英制，1=公制
     * @return
     */
    public static byte[] setUnit(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 8;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }

    /**
     * 设置手环显示的时间格式
     *
     * @param value 时间格式 0代表12小时制 1代表24小时制
     * @return
     */
    public static byte[] setTimeFormat(int value) {
        byte[] measure = new byte[14];
        measure[0] = (byte) 0xab;
        measure[3] = 5;
        measure[8] = 1;
        measure[10] = 9;
        measure[12] = 1;
        measure[13] = (byte) value;
        return measure;
    }


    /**
     * 运动心率开关
     *
     * @param value 1=开，0=关
     * @return
     */
    public static byte[] setWholeMeasurement(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 0x29;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }


    /**
     * 血压自动开关
     *
     * @param value 1=开，0=关
     * @return
     */
    public static byte[] setAutoBPSwitch(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 0x54;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }



    /**
     * 抬手亮屏开关
     *
     * @param value 1=开，0=关
     * @return
     */
    public static byte[] setTaiWan(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 0x0a;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }


    /**
     * 免打扰开关
     *
     * @param value 1=开，0=关
     * @return
     */
    //0x20
    public static byte[] setDoNotDistrub(int value) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 0x20;
        bright_screen[12] = 1;
        bright_screen[13] = (byte) value;
        return bright_screen;
    }


    /**
     * 设置Android手机语言类型英文
     *
     * @param value 1=开，0=关
     * @return
     */
    public static byte[] SendAnddroidLanguage(int value) {
        byte[] data = new byte[15];
        data[0] = (byte) 0xab;
        data[1] = (byte) 0x00;
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x07;

        data[4] = (byte) 0x00;
        data[5] = (byte) 0x00;
        data[6] = (byte) 0x00;
        data[7] = (byte) 0x00;

        data[8] = (byte) 0x01;
        data[9] = (byte) 0x00;
        data[10] = (byte) 0x1b;
        data[11] = (byte) 0x00;

        data[12] = (byte) 0x02;
        data[13] = (byte) 0x00;
        data[14] = (byte) value;
        return data;
    }


    /**
     * 恢复出厂设置
     *
     * @return
     */
    public static byte[] setReboot() {
        byte[] bind = new byte[13];
        bind[0] = (byte) 0xab;
        bind[3] = 5;
        bind[8] = 1;
        bind[10] = 6;
        bind[12] = 0;
        return bind;
    }

    /**
     * 吃药提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时  = （固定写入4,6,8,12）
     * @param enable    开关
     * @return
     */
    public static byte[] setMedicalNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {

        int value = 0;
        if (enable) {
            value = 0x01;
        } else {
            value = 0x00;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;
        bind[8] = 0x01;
        bind[10] = 0x15;
        bind[12] = 4;
        bind[13] = (byte) ((value << 7) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) (((endMin & 0x03) << 6) | ((period)));

        return bind;
    }


    /**
     * 久坐提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时 = （固定写入1,2,3,4）
     * @param enable    开关
     * @return
     */
    public static byte[] setSitNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {

        period = period - 1;

        int value = 0;
        if (enable) {
            value = 0x01;
        } else {
            value = 0x00;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;
        bind[8] = 0x01;
        bind[10] = 7;
        bind[12] = 4;
        bind[13] = (byte) ((value << 7) | ((period + 1) << 4) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) ((endMin & 0x03) << 6);

        return bind;
    }


    /**
     * 喝水提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时 = （固定写入1,2,3,4）
     * @param enable    开关
     * @return
     */
    public static byte[] setDrinkingNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {


        period = period - 1;

        int value = 0;

        if (enable) {
            value = 0x01;
        } else {
            value = 0x00;
        }

        byte[] bind = new byte[17];
        bind[0] = (byte) 0xab;
        bind[3] = 9;
        bind[8] = 0x01;
        bind[10] = 0x16;
        bind[12] = 4;
        bind[13] = (byte) ((value << 7) | ((period + 1) << 4) | (startHour >> 1));
        bind[14] = (byte) (((startHour & 0x01) << 7) | (startMin << 1) | (endHour >> 4));
        bind[15] = (byte) (((endHour & 0x0f) << 4) | (endMin >> 2));
        bind[16] = (byte) ((endMin & 0x03) << 6);

        return bind;
    }


    /**
     * 会议设置
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   小时
     * @param min    分钟
     * @param enable 开关
     * @return
     */
    public static byte[] setMeetingNotification(int year, int month, int day, int hour, int min, boolean enable) {
        int value = 0;
        if (enable) {
            value = 0x01;
        } else {
            value = 0x00;
        }
        byte[] alarm = new byte[18];
        alarm[0] = (byte) 0xab;
        alarm[3] = 10;
        alarm[8] = 1;
        alarm[10] = 0x17;
        alarm[12] = 5;
        alarm[13] = (byte) ((year << 2) | (month >> 2));
        alarm[14] = (byte) (((month & 0x03) << 6) | (day << 1) | (hour >> 4));
        alarm[15] = (byte) (((hour & 0xf) << 4) | (min >> 2));
        alarm[16] = (byte) (((min & 0x3) << 6));
        alarm[17] = (byte) ((byte) value << 7);
        return alarm;
    }


    /**
     * 设置闹钟
     *
     * @param clockItems 闹钟L身体
     * @return
     */
    public static byte[] setAlarm(List<W30S_AlarmInfo> clockItems) {
        for (int i = 0; i < clockItems.size(); i++) {
            String s = clockItems.get(i).toString();
            Log.d("------003-----", s);
        }
        int length = 13 + (5 * clockItems.size());
        byte[] alarm = new byte[length];
        alarm[0] = (byte) 0xab;
        alarm[3] = (byte) (5 + 5 * clockItems.size());
        alarm[8] = 1;
        alarm[10] = 2;
        alarm[12] = (byte) (5 * clockItems.size()); // 5/per alarm * 3

        for (int i = 0; i < clockItems.size(); i++) {
            int hour = clockItems.get(i).getAlarmtHour();
            int min = clockItems.get(i).getAlarmtMin();
            int id = clockItems.get(i).getAlarmId();
            int repeat = clockItems.get(i).getAlarmtData();

            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String dateTime = sdfTime.format(new Date(System
                    .currentTimeMillis()));
            int currentHour = Integer.parseInt(dateTime.split(":")[0]);
            int currentMin = Integer.parseInt(dateTime.split(":")[1]);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateString = sdf.format(new Date(System.currentTimeMillis()));
            if ((hour == currentHour && min < currentMin)
                    || (hour < currentHour)) {
                dateString = sdf.format(new Date(System.currentTimeMillis()
                        + 1000 * 60 * 60 * 24));
            } else {
                dateString = sdf.format(new Date(System.currentTimeMillis()));
            }
            char[] hexChars = dateString.toCharArray();
            int year = charToByte(hexChars[2]) * 10 + charToByte(hexChars[3]) - 1;
            int mon = charToByte(hexChars[4]) * 10 + charToByte(hexChars[5]);
            int day = charToByte(hexChars[6]) * 10 + charToByte(hexChars[7]);

            alarm[13 + i * 5] = (byte) ((year << 2) | (mon >> 2));
            alarm[14 + i * 5] = (byte) (((mon & 0x03) << 6) | (day << 1) | (hour >> 4));
            alarm[15 + i * 5] = (byte) (((hour & 0xf) << 4) | (min >> 2));
            alarm[16 + i * 5] = (byte) (((min & 0x3) << 6) | (id << 3));
            alarm[17 + i * 5] = (byte) repeat;
        }
        return alarm;

    }

    /**
     * 设置默认开关指令
     *
     * @param time      = 时钟设置 1=开，0=关
     * @param unit      = 单位设置 1=开，0=关
     * @param bright    = 抬腕亮屏 1=开，0=关
     * @param miandarao = 免打扰开关 1=开，0=关
     * @param woheart   = 运动心率开关 1=开，0=关
     * @return
     */
    public static byte[] setInitSet(int time, int unit, int bright, int miandarao, int woheart) {
        byte[] bright_screen = new byte[14];
        bright_screen[0] = (byte) 0xab;
        bright_screen[3] = 5;
        bright_screen[8] = 1;
        bright_screen[10] = 0x1e;
        bright_screen[12] = 1;
        int aa = 0;
        if (time == 1) {
            aa = aa | 0x80;
        }
        if (unit == 1) {
            aa = aa | 0x40;
        }
        if (bright == 1) {
            aa = aa | 0x20;
        }

        if (miandarao == 1) {
            aa = aa | 0x04;
        }

        if (woheart == 1) {
            aa = aa | 0x02;
        }

        bright_screen[13] = (byte) aa;

        return bright_screen;
    }

    /**
     * 设置目标步数
     *
     * @param TargetStep 目标步数
     * @return
     */
    public static byte[] setTargetStep(int TargetStep) {
        byte[] target = new byte[20];
        target[0] = (byte) 0xab;
        target[3] = 8;
        target[8] = 1;
        target[10] = 3;
        target[12] = 4;
        target[13] = (byte) (TargetStep >> 24);
        target[14] = (byte) ((TargetStep & 0x00ff0000) >> 16);
        target[15] = (byte) ((TargetStep & 0x0000ff00) >> 8);
        target[16] = (byte) (TargetStep & 0xff);
        return target;
    }

    /**
     * 推送内容到手环
     *
     * @param pn       推送内容
     * @param app_type 推送类型，参考上面写的【消息推送类型】
     * @return
     *
     */

    public static byte[] notifyMsg(String pn, int app_type)
            {

        pn = W30SBleUtils.Filtration(pn);
        byte[] hexChar = pn.getBytes();
        byte[] notifiCall = new byte[13 + 1 + hexChar.length];
        notifiCall[0] = (byte) 0xab;
        notifiCall[3] = (byte) (5 + hexChar.length + 1);
        notifiCall[8] = 2;
        notifiCall[10] = (byte) app_type;
        notifiCall[12] = (byte) (hexChar.length + 1);
        notifiCall[13] = (byte) (hexChar.length);
        for (int i = 0; i < hexChar.length; i++) {
            notifiCall[14 + i] = (byte) hexChar[i];
        }

        String str = "";
        for (int i = 0; i < notifiCall.length; i++) {
            String strTmp = Integer.toHexString(notifiCall[i]);
            if (strTmp.length() > 2) {
                strTmp = strTmp.substring(strTmp.length() - 2);
            } else if (strTmp.length() < 2) {
                strTmp = "0" + strTmp;
            }
            str = str + strTmp;
        }

        return notifiCall;
    }


    /**
     * 来电接通或挂断
     *
     * @return
     */
    public static byte[] notifyMsgClose() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;
        data[8] = 0x01;
        data[10] = (byte) 0x31;
        return data;
    }

    /**
     * 设备关机
     *
     * @return
     */
    static byte[] deviceClose() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;
        data[8] = 0x01;
        data[10] = (byte) 0x80;
        return data;
    }

    /**
     * 进入摇一摇拍照
     *
     * @return
     */
    public static byte[] intoShakePicture() {
        byte[] data = new byte[13];
        data[0] = (byte) 0xab;
        data[3] = 0x05;
        data[8] = 0x01;
        data[10] = (byte) 0x30;
        return data;
    }
}
