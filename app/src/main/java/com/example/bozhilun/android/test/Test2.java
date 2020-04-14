package com.example.bozhilun.android.test;

import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Admin
 * Date 2019/8/30
 */
public class Test2 {


    public static void main(String[] arg){
        List list;
        ArrayList arrayList;
        LinkedList linkedList;
        Map m;
        HashSet hashSet;

        Hashtable hashtable;
        HashMap hashMap;
        Gson gson;
//
        Error error;
        Exception exception;
        String astr = "this is a str!";
        int code = astr.hashCode();



        System.out.print("-------code="+code+"\n");

//       String sleepStr = "4444411121111121010000120010000000001100010101000101101111112212111111111111000001110001001000000101100002001001011101110101000001000100000121122221122222";
//        System.out.print("------长度="+sleepStr.length()+"\n");
//
//        String str2 = "11110101010001010101110011012112010112002110112122000001020002012000000000000001121122101011210111101100000112111201111100100000010000000100100000000000000111110111101111111111111112111111110002011002000000000000222222222112222";
//        System.out.print("------长度2="+str2.length()+"\n");



        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double times = 0.0994764;
        float lowTime = 0;
        float count = 382;
        float aa = lowTime / count;

        double bb = lowTime / count;
        //System.out.print("-----="+decimalFormat.format(times));

    }



    abstract  class A{

        abstract void doA();

        public void doSome(){
            int a = 1;
            int b = 2;
            int count = 0;
            count = a + b;

        }
    }

    interface B{

    }


    class TempBean{
        private int code;

        private String version;

        private int alertType;

        private Object data;

        private String alertMsg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getAlertType() {
            return alertType;
        }

        public void setAlertType(int alertType) {
            this.alertType = alertType;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getAlertMsg() {
            return alertMsg;
        }

        public void setAlertMsg(String alertMsg) {
            this.alertMsg = alertMsg;
        }


        @Override
        public String toString() {
            return "TempBean{" +
                    "code=" + code +
                    ", version='" + version + '\'' +
                    ", alertType=" + alertType +
                    ", data=" + data +
                    ", alertMsg='" + alertMsg + '\'' +
                    '}';
        }
    }
}
