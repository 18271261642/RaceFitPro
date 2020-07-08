package com.example.bozhilun.android.test;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Arrays;

/**
 * Created by Admin
 * Date 2020/3/25
 */
public class Test0 {


   public static void main(String[] arg){
       pushMsgNotify(0,"张三");
   }


    //推送消息提醒信息
     static void pushMsgNotify(int type,String msgContent){
        int msgLength = msgContent.length();
        byte[] msgByte = WatchUtils.chaiFenDataIntTo2Byte(msgLength);

        System.out.println("-----msgByte="+ Arrays.toString(msgByte));

        byte[] msgContentByte = msgContent.getBytes();

        System.out.println("-----msgC="+ Arrays.toString(msgContentByte));

        byte crcByte = (byte) ((byte) (0x86 + 0x41 + 0x03 + msgByte[0] + msgByte[1] + msgContentByte.length) & 0xff);

        System.out.println("-----crcByte="+ crcByte);

        byte[] notifyByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x41, (byte) (0x03+msgContentByte.length), (byte) type,msgByte[0],msgByte[1], (byte) msgContentByte.length};


        byte[] b5 = new byte[]{0x00,0x01,0x02,0x03};


        //setDeviceByteData(notifyByte);
    }

}



