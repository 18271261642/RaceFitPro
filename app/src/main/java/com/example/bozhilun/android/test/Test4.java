package com.example.bozhilun.android.test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Admin
 * Date 2019/10/24
 */
public class Test4 {

    public static void main(String[] arg){
        String str1 = "11111111111111111101102001021000000000200101002200222200200002222200000002100000000000000000000000000000001011211100000000221212222222222";
        System.out.print("------str1长度="+str1.length()+"\n");

        String str2 = "444444411000010222221222222222";
        System.out.print("------str2长度="+str2.length()+"\n");

        String str3 = "4444444110000110100120000000000021211110001102222222221021000121012100212222222222222";
        System.out.print("------str3长度="+str3.length()+"\n");


        String str4 = "444444411211000111212222222222";
        System.out.print("-------str4长度="+str4.length()+"\n");

        String str5 = "444444411201100112222222222222";
        System.out.print("-------str5="+str5.length()+"\n");

        ConcurrentMap<String,Object> concurrentMap = new ConcurrentHashMap<>();
    }
}
