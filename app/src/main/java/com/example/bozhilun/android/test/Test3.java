package com.example.bozhilun.android.test;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;

/**
 * Created by Admin
 * Date 2019/10/19
 */
public class Test3 {

    public static void main(String[] arg){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double str = 0.0043196543;

        String tmp1 = decimalFormat.format(str);

        String deepPercentV = StringUtils.substringAfter(tmp1,".");


        System.out.print("--------str="+tmp1+"--=");
    }
}
