package com.example.bozhilun.android.siswatch.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2018/12/5
 */
public class DateTimeUtils {


    static String currDate = WatchUtils.getCurrentDate();

    /**
     * 获取当前日期的年
     * @return
     */
    public static int getCurrYear(){
        return  Integer.valueOf(!WatchUtils.isEmpty(currDate) ? currDate.substring(0, 4).trim() : "");
    }

    /**
     * 获取指定日期的年
     * @return
     */
    public static int getCurrYear(String str){
        return  Integer.valueOf(!WatchUtils.isEmpty(str) ? str.substring(0, 4).trim() : "");
    }



    //获取当前日期的月
    public static int getCurrMonth(){
        return  Integer.valueOf(!WatchUtils.isEmpty(currDate) ? currDate.substring(5, 7).trim() : "");
    }


    //获取指定期的月
    public static int getCurrMonth(String str){

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH)+1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }


    //获取指定期的月
    public static int getCurrMonth(String str,String pattern){

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,Locale.CHINA);
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH)+1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }


    //获取当前日期的天
    public static int getCurrDay(){
        return  Integer.valueOf(!WatchUtils.isEmpty(currDate) ? currDate.substring(8, currDate.length()).trim() : "");
    }


    //获取指定日期的天
    public static int getCurrDay(String str){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    //获取指定日期的天
    public static int getCurrDay(String str,String pattern){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,Locale.CHINA);
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }


    /**
     * 获取指定日期的时分秒 tag: 0-小时；1-分；2-秒
     * @param str
     * @param pattern
     * @param tag
     * @return
     */
    public static int getStrDate(String str,String pattern,int tag){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,Locale.CHINA);
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            switch (tag){
                case 0:
                    return calendar.get(Calendar.HOUR_OF_DAY);

                case 1:
                    return calendar.get(Calendar.MINUTE);

                case 2:
                    return calendar.get(Calendar.SECOND);

            }

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

        return 0;
    }


}
