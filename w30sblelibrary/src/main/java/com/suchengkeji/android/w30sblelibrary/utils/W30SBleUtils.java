package com.suchengkeji.android.w30sblelibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @aboutContent:* 蓝牙工具类
 * 包含
 * 1.过滤推送数据
 * 2.解析日期
 * 3.获取卡路里
 * 4.解析距离
 * 4.获取当前日期的前一天
 * @author： An
 * @crateTime: 2018/3/6 14:24
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class W30SBleUtils {

    //是否在ota升级状态
    public static boolean isOtaConn = false;

    /**
     * 过滤推送数据
     *
     * @param
     * @return
     */
    public static String Filtration(String str) {
        String result = "";
        int lenght = 14;
        char c[] = str.toCharArray();

        for (int i = 0; i < c.length; i++) {

            byte[] hexCharaaa = String.valueOf(c[i]).getBytes();
            lenght += hexCharaaa.length;
            if (lenght > 250) {
                break;
            }
            result += String.valueOf(c[i]);

        }
        return result;

    }

    /**
     * 解析日期
     *
     * @param data
     * @return
     */
    public static String getDate(byte[] data) {
        int Ayear, Amon, Aday;
        Ayear = ((data[13] & 0x7E) >> 1);
        Amon = ((data[13] & 0x1) << 3) | ((data[14] & 0xE0) >> 5);
        Aday = data[14] & 0x1F;
        String AmonStr, AdayStr;
        if (Aday < 10) {
            AdayStr = "0" + Aday;
        } else {
            AdayStr = "" + Aday;
        }
        if (Amon < 10) {
            AmonStr = "0" + Amon;
        } else {
            AmonStr = "" + Amon;
        }

        return "20" + Ayear + "-" + AmonStr + "-" + AdayStr;
    }


    /**
     * 获取卡路里
     *
     * @param height    身高
     * @param weight    体重
     * @param sportStep 步数
     * @return
     */
    public static float getCalory(float height, float weight, int sportStep) {
        float bleCalory = 0;

        bleCalory = (float) (weight * 1.036 * height * 0.41 * sportStep * 0.00001);

        return bleCalory;
    }

    /**
     * 获取距离
     *
     * @param height    身高
     * @param sportStep 步数
     * @return
     */
    public static float getDistance(float height, int sportStep) {
        float bleDistance = 0;
        bleDistance = (float) (height * 41 * sportStep * 0.0001);
//        bleDistance = (float) (height * 41 * sportStep * 0.00001);
//        bleDistance = (float) (height * 41 * sportStep * 0.00001 * 10 * 0.001);
        return bleDistance;
    }


    /**
     * 获取当前日期的前一天
     *
     * @param time
     * @return
     */
    public static String getBeforeDay(String time) {


        String result = "";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            result = dateFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}

