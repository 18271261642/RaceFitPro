package com.example.bozhilun.android.friend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/7/18
 */
public class Test {


    public static void main(String[] arg){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINA);


       String timeStr = "23:35";
        try {
            long longStr = sdf.parse(timeStr).getTime();
            System.out.print("-------longStr="+longStr+"\n");

            System.out.print("------aa="+getLongToDate("HH:mm",longStr)+"\n");
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }



    public static String getLongToDate(String format, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(date);
    }


}
