package com.example.bozhilun.android.test;

import java.util.Arrays;

/**
 * Created by Admin
 * Date 2019/12/25
 */
public class Test10 {

    public static void main(String[] arg){
       int count = 90;

       float level = 30f / 60f;
       System.out.println("-----level="+level);


        short a = 0xab;

        byte[] byteStr = new byte[]{(byte) 0xab, (byte) a};

        System.out.println("--------byte="+ Arrays.toString(byteStr)+"\n");

    }
}
