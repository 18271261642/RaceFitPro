package com.example.bozhilun.android.b31.km;

/**
 * 康美数据上传接口
 * Created by Admin
 * Date 2019/12/12
 */
public class KmConstance {

    private static String KM_BASE_URL = "http://www.dengfengsoftware.com:8034/v1/bozlun/";

    //账号ID及设备地址
    public static String uploadAccountDevice(){
        return KM_BASE_URL +"AddAccountDevice";
    }

    //上传步数数据
    public static String uploadWalkData(){
        return KM_BASE_URL + "AddWalkRecord?walkRecords";
    }

    //上传心率数据
    public static String uploadHeartData(){
        return KM_BASE_URL + "AddHeartBeat?breatheRates";
    }

    //上传血压数据
    public static String uploadBloodData(){
        return KM_BASE_URL + "AddBloodPressure?bloodPressures";
    }


    //上传血氧数据
    public static String uploadBloodOxygen(){
        return KM_BASE_URL +"AddBloodOxygen?bloodOxygens";
    }

    //上传呼吸率
    public static String uploadSpo2BreathRates(){
        return KM_BASE_URL + "AddBreatheRate?breatheRates";
    }


}
