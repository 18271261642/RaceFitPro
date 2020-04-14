package com.example.bozhilun.android.test;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.android.volley.RequestQueue;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Admin
 * Date 2019/8/6
 */
public class Test1 {


    public static void main(String[] arg){


        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Response response = (Response) msg.obj;
                try {
                    byte[] bodyByte = response.body().bytes();



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };


        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("").build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Message message = handler.obtainMessage();
                    message.obj = response;
                    message.what = 1001;
                    handler.sendMessage(message);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    public static void byteToFile(byte[] bytes, String path)
    {
        try
        {
            // 根据绝对路径初始化文件
            File localFile = new File(path);
            if (!localFile.exists())
            {
                localFile.createNewFile();
            }
            // 输出流
            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String removeStr(String str){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<str.length();i++){
            char tmpStr = str.charAt(i);
            if(!(tmpStr+"").equals("-"))
                stringBuilder.append(tmpStr);
        }
        return stringBuilder.toString();
    }


}
