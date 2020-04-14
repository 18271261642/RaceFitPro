package com.suchengkeji.android.w30sblelibrary.bean;


import org.litepal.crud.LitePalSupport;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * 闹钟信息
 * 注意：闹钟最多支持五个
 * AlarmId 有效范围为：（0,1,2,3,4）
 */
public class W30S_AlarmInfo extends LitePalSupport implements Serializable {

    private int AlarmId;
    private int AlarmtHour;
    private int AlarmtMin;
    private int AlarmtData;

    /**
     * @return ID
     */
    public int getAlarmId() {
        return AlarmId;
    }

    /**
     * @param alarmId ID
     */
    public void setAlarmId(int alarmId) {
        AlarmId = alarmId;
    }


    /**
     * @return 小时
     */
    public int getAlarmtHour() {
        return AlarmtHour;
    }

    /**
     * @param alarmtHour 小时
     */
    public void setAlarmtHour(int alarmtHour) {
        AlarmtHour = alarmtHour;
    }


    /**
     * @return 分钟
     */
    public int getAlarmtMin() {
        return AlarmtMin;
    }

    /**
     * @param alarmtMin 分钟
     */
    public void setAlarmtMin(int alarmtMin) {
        AlarmtMin = alarmtMin;
    }


    /**
     * @return 数据（包括开关和重复天数）
     */
    public int getAlarmtData() {
        return AlarmtData;
    }

    /**
     * @param alarmtData 数据（包括开关和重复天数）
     */
    public void setAlarmtData(int alarmtData) {
        //AlarmtData = alarmtData;
        AlarmtData = hnadleData(alarmtData);
    }

    public int hnadleData(int my_byte) {
        int result = 0;
        byte[] binStr = new byte[1];
        binStr[0] = (byte) my_byte;
        String byte_str = new BigInteger(1, binStr).toString(2);
        int lenght = byte_str.length();
        for (int j = 0; j < (8 - lenght); j++) {
            byte_str = "0" + byte_str;
        }
        for (int i = 0; i < byte_str.length(); i++) {
            if (Integer.valueOf(byte_str.substring(i, i + 1)) == 1) {
                if (i == 0) {
                    result += (int) Math.pow(2, 7);
                } else {
                    result += (int) Math.pow(2, (i - 1));
                }
            }
        }
        return result;
    }


    public W30S_AlarmInfo() {
        super();
    }

    /**
     * @param id   ID
     * @param hour 小时
     * @param min  分钟
     * @param data 数据（包括开关和重复天数）
     */
    public W30S_AlarmInfo(int id, int hour, int min, int data) {
        super();
        setAlarmId(id);
        setAlarmtHour(hour);
        setAlarmtMin(min);
        setAlarmtData(data);
    }

    /**
     * @param hour 小时
     * @param min  分钟
     * @param data 数据（包括开关和重复天数）
     */
    public W30S_AlarmInfo(int hour, int min, int data) {
        super();
        setAlarmtHour(hour);
        setAlarmtMin(min);
        setAlarmtData(data);
    }

    @Override
    public String toString() {
        return "TestAlarmInfo{" +
                "AlarmId=" + AlarmId +
                ", AlarmtHour=" + AlarmtHour +
                ", AlarmtMin=" + AlarmtMin +
                ", AlarmtData=" + AlarmtData +
                '}';
    }
}