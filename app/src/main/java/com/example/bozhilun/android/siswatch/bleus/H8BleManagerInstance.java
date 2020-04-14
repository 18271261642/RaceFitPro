package com.example.bozhilun.android.siswatch.bleus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;

public class H8BleManagerInstance {

    private static final String TAG = "H8BleManagerInstance";

    private static H8BleService h8BleService;
    private static volatile H8BleManagerInstance h8BleManagerInstance;



    public static synchronized H8BleManagerInstance getH8BleManagerInstance(){
        if(h8BleManagerInstance == null){
            synchronized (H8BleManagerInstance.class){
                if(h8BleManagerInstance == null)
                    h8BleManagerInstance =  new H8BleManagerInstance();
                bindH8Service();
            }
        }
        return h8BleManagerInstance;
    }


    public H8BleService getH8BleService() {
        if(h8BleService != null){
            return h8BleService;
        }else{
            return null;
        }

    }

    //断开连接
    public  void disConnH8(H8ConnstateListener h8ConnstateListener){
        if(h8BleService != null){
            h8BleService.setH8ConnstateListener(h8ConnstateListener);
             h8BleService.disconnect();

        }
    }

    //获取手表时间
    public void sendDataGetH8Time(byte[] data, GetH8TimeInterface getH8TimeInterface){
        if(h8BleService != null){
           // commH8Listener.setGetH8TimeInterface(getH8TimeInterface);
            h8BleService.setGetH8TimeInterface(getH8TimeInterface);
//            h8BleService.writeCharacteristicData(data);
            h8BleService.writeCharacteristic(data);
//            h8BleService.writeCharacteristicData(data);

        }
    }
    //获取闹钟
    public void sendDataGetH8Alarm(byte[] data,ParentH8TimeListener parentH8TimeListener){
        if(h8BleService != null){
            h8BleService.setParentH8TimeListener(parentH8TimeListener);
            //h8BleService.writeCharacteristicData(data);
           // h8BleService.writeCharacteristic(data);
            h8BleService.disconnect();
        }
    }

    //同步手表的时间
    public void syncH8Time(byte[] data){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(data);
            //h8BleService.writeCharacteristicData(data);
        }
    }

    //获取步数
    public void getH8Steps(byte[] data,GetH8StepsListener getH8StepsListener){
        if(h8BleService != null){
            h8BleService.setGetH8StepsListener(getH8StepsListener);
            h8BleService.writeCharacteristic(data);
        }
    }


    //获取节电时间
    public void getH8JieDianTime(byte[] data,GetH8JiedianListener getH8JiedianListener){
        if(h8BleService != null){
            h8BleService.setGetH8JiedianListener(getH8JiedianListener);
            h8BleService.writeCharacteristic(data);
        }
    }

    //设置节电时间
    public void setH8JieDianTime(int startHour, int startMinue, int endHour, int endMinue,SetH8JiedianListener setH8JiedianListener){
        if(h8BleService != null){
            h8BleService.setSetH8JiedianListener(setH8JiedianListener);
            h8BleService.writeCharacteristic(WatchConstants.settingJiedianTime(startHour,startMinue,endHour,endMinue));
        }
    }

    //短信提醒
    public void setSMSAlert(){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(WatchConstants.smsAlert());
        }
    }

    //APP消息提醒
    public void setAPPAlert(){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(WatchConstants.appalert());
        }
    }

    //电话提醒
    public void setPhoneAlert(){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(WatchConstants.phoneAlert(0));
        }
    }

    //取消电话提醒
    public void canclePhoneAlert(){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(WatchConstants.disPhoneAlert());
        }
    }

    //获取第一个闹钟
    public void getH8FirstAlarm(GetH8FirstAlarmListener getH8FirstAlarmListener){
        if(h8BleService != null){
            h8BleService.setGetH8FirstAlarmListener(getH8FirstAlarmListener);
            h8BleService.writeCharacteristic(WatchConstants.getWatchAlarmOne);
        }
    }

    //获取第二个闹钟
    public void getH8SecondAlarm(GetH8FirstAlarmListener getH8FirstAlarmListener){
        if(h8BleService != null){
            h8BleService.setGetH8FirstAlarmListener(getH8FirstAlarmListener);
            h8BleService.writeCharacteristic(WatchConstants.getWatchAlarmSecond);
        }
    }

    //获取第三个闹钟
    public void getH8ThirdAlarm(GetH8FirstAlarmListener getH8FirstAlarmListener){
        if(h8BleService != null){
            h8BleService.setGetH8FirstAlarmListener(getH8FirstAlarmListener);
            h8BleService.writeCharacteristic(WatchConstants.getWatchAlarmThird);
        }
    }

    //设置闹钟
    public void setH8Alarm(int alarmnum, int hour, int mine, int week,int onoroff ,ShowSetAlarmResultListener showSetAlarmResultListener){
        if(h8BleService != null){
            h8BleService.setShowSetAlarmResultListener(showSetAlarmResultListener);
            h8BleService.writeCharacteristic(WatchConstants.setWatchAlarm2(alarmnum,hour,mine,week,onoroff));
        }
    }

    //获取系统的类型
    public void getH8SysType(){
        if(h8BleService != null){
            h8BleService.writeCharacteristic(WatchConstants.getDeviceType());
        }
    }


    //绑定服务
     public static void bindH8Service(){
        Intent intent = new Intent(MyApp.getInstance(),H8BleService.class);
         MyApp.getInstance().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                h8BleService = ((H8BleService.H8LocalBinder) service).getH8Service();
                if(!h8BleService.initialize()){
                    Log.e(TAG,"------adapter未初始化----");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
