package com.example.bozhilun.android.w30s.ble;

/**
 * Created by Admin
 * Date 2019/7/4
 */
public class W37Constance {

    //消息提醒标签
    public static final int NotifaceMsgPhone = 0x01;//来电提醒
    public static final int NotifaceMsgQq = 0x02;//QQ
    public static final int NotifaceMsgWx = 0x03;//微信
    public static final int NotifaceMsgMsg = 0x04;//短消息
    public static final int NotifaceMsgSkype = 0x05;//Skype
    public static final int NotifaceMsgWhatsapp = 0x06;//Whatsapp
    public static final int NotifaceMsgFacebook = 0x07;//脸书
    public static final int NotifaceMsgLink = 0x08;//Link
    public static final int NotifaceMsgTwitter = 0x09;//推特
    public static final int NotifaceMsgViber = 0x0a;//Viber
    public static final int NotifaceMsgLine = 0x0b;//Line





    //连接成功的action
    public static final String W37_CONNECTED_ACTION = "com.example.bozhilun.android.w30s.ble.w37.connected";

    //断开连接
    public static final String W37_DISCONNECTED_ACTION = "com.example.bozhilun.android.w30s.ble.w37.disconnected";


    //xwatch连接成功的action
    public static final String X_WATCH_CONNECTED_ACTION = "com.example.bozhilun.android.x_watch_connected";
    //xwatch断连连接的action
    public static final String X_WATCH_DISCONN_ACTION = "com.example.bozhilun.android.x_watch_disconnected";

    //SWatch连接成功的action
    public static final String S_WATCH_CONNECTED_ACTION = "com.example.bozhilun.android.s_watch_connected";
    //SWatch断开连接的action
    public static final String S_WATCH_DISCONN_ACTION = "com.example.bozhilun.android.s_watch_disconnected";



    //B11体温手环
    public static final String B11_WATCH_CONNECTED_ACTION = "com.example.bozhilun.android.b11_connected";
    public static final String B11_WATCH_DISCON_ACTION = "com.example.bozhilun.android.b11_disconnected";

}
