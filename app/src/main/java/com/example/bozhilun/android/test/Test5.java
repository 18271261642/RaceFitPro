package com.example.bozhilun.android.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2019/11/9
 */
public class Test5 {

    public static void main(String[] arg){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

        String startTime = "00:00:00";

        int currTime = 567; //分钟

        System.out.println("--------curr="+(currTime / 60 +":" + currTime % 60)+"\n");

        try {
            long startT = sdf.parse(startTime).getTime();
            long endT = sdf.parse("07:52:00").getTime();

            long intelL =  endT + startT;
            System.out.println("-----start="+startT+"--="+intelL+"---"+(476 * 60 * 1000)+"\n"+sdf.format(new Date(567 * 60 )));
            String resultT = sdf.format(new Date(intelL));
            System.out.println("---------result="+resultT+"\n");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
