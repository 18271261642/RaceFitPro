package com.example.bozhilun.android.test;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2019/12/16
 */
public class Test9 {


    public static void main(String[] arg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);

        long str1 = 1577156229339L;

        long currTime = System.currentTimeMillis();
        System.out.println("-----currTime="+currTime+"\n"+(currTime-str1)/1000/60+"\n"+sdf.format(new Date()));
    }


}



