package com.example.bozhilun.android.test;

/**
 * Created by Admin
 * Date 2020/3/2
 */
public class Test11 {

    public static void main(String[] arg){
        int year = 2020-2000;

        int yearbcd = ((year / 10) << 4 & 0xf0) + ((year % 10) & 0x0f);

        int month =3;

        int monthbcd = ((month / 10) << 4 & 0xf0) + ((month % 10) & 0x0f);

        int day = 2;

        int daybcd = ((day / 10) << 4 & 0xf0) + ((day % 10) & 0x0f);

        int hour = 10;

        int hourbcd = ((hour / 10) << 4 & 0xf0) + ((hour % 10) & 0x0f);

        int minute = 14;

        int minutebcd = ((minute / 10) << 4 & 0xf0) + ((minute % 10) & 0x0f);

        int second = 50;

        int secondbcd = ((second / 10) << 4 & 0xf0) + ((second % 10) & 0x0f);


        System.out.println("--------打印---y="+yearbcd+"m="+monthbcd+"--d="+daybcd+"-h="+hourbcd+"--se="+secondbcd);




        int goal = 10000;
        int byte4 = goal>>4;

        System.out.println("--------bbbb="+byte4);

        int hightV = goal / 65536;
        int lowV = goal % 65536;

        int fistV = (goal >>24) & 0xFF;
        int secondv = goal >> 16 & 0xFF;
        int third = secondv >> 8 & 0xFF;
        int fourth = secondv & 0xFF;

        System.out.println("----2222---fff="+fistV+"--2="+secondv+"--3="+third+"--4="+fourth);




        int sportBB = goal / 16777216;
        int sportCC = goal / 65536;
        int sportDD = goal / 256;
        int sportEE = goal % 256;


        System.out.println("--------sportBB="+sportBB+"--c="+sportCC+"-=d="+sportDD+"-=E="+sportEE);

    }

}
