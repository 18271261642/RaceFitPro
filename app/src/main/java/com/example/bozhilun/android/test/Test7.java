package com.example.bozhilun.android.test;

import java.util.Calendar;

/**
 * Created by Admin
 * Date 2019/12/3
 */
public class Test7 {

    public static void main(String[] arg){
        Calendar calendar = Calendar.getInstance();
        int weeks = calendar.get(Calendar.DAY_OF_WEEK);

        System.out.println("-----weeks="+weeks+"\n");
    }
}
