package com.example.bozhilun.android.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2019/11/22
 */
public class Test6 {

    public static void main(String[] arg){
        String str = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,456,794,124,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,456,794,124,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,456,794,124,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,456,794,124,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,456,794,124,0,0,0,0";
        System.out.println("--------aa="+str.length()+"\n");
        List<Integer> integerList = new ArrayList<>();
        for(int i = 0;i<str.length();i++){
            char currStr = str.charAt(i);
            integerList.add(Integer.valueOf(currStr));
        }
        System.out.println("------length="+integerList.size()+"\n");

        List<Integer> tmpList = integerList.subList(0,48);
        System.out.println("-----22-length="+tmpList.size()+"\n");

    }
}
