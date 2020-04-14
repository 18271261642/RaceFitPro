package com.example.bozhilun.android.w30s.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.AlertService;
import com.example.bozhilun.android.R;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;

/**
 * 正常的系统前台进程，会在系统通知栏显示一个Notification通知图标
 * Intent whiteIntent = new Intent(getApplicationContext(), WhiteService.class);
 * startService(whiteIntent);
 *
 * @author clock
 * @since 2016-04-12
 */
public class WhiteService extends Service {

    private final static String TAG = WhiteService.class.getSimpleName();

    private final static int FOREGROUND_ID = 1000;

    @Override
    public void onCreate() {
        Log.i(TAG, "WhiteService->onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "WhiteService->onStartCommand");
        createNotification();
        toggleNotificationListenerService(true);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Notification
     */
    public void createNotification() {
        //使用兼容版本
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.beraceiocn);
        //设置通知栏横条的图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.beraceiocn));
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(false);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        //builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("RaceFitPro");
        builder.setContentText("RaceFitPro Service");
        //创建通知
        notification = builder.build();
        //设置为前台服务
        startForeground(FOREGROUND_ID, notification);
    }

    Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "WhiteService->onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "WhiteService->onDestroy");
        stopForeground(true);
        if (notification != null) {
            notification = null;
        }
        toggleNotificationListenerService(false);
        super.onDestroy();
    }

    /**
     * 被杀后再次启动时，监听不生效的问题
     */
    private void toggleNotificationListenerService(boolean isStatue) {

        if (isStatue) {
            Intent sevice = new Intent(this, AlertService.class);
            sevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startService(sevice);
        } else {
            W30SBLEManage.mW30SBLEServices.close();
        }

    }
}