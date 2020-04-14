package com.example.bozhilun.android.test;

import android.os.Environment;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;

/**
 * Created by Admin
 * Date 2020/3/19
 */
public class Test22 {

    public static void main(String[] arg){

        String str = "这是 这是";
        System.out.println("-------str="+str.length());

        byte a= 0x01;
        byte b = 0x14;
        byte c = 0x04;
        byte d = 0x09;
        byte e = 0x2e;

        System.out.println("---------a="+((a+b+c+d+e)&0xff));


        String str2 = "11111111";
        System.out.println("----value="+ WatchUtils.bitToByte(str2));


    }
}
