package com.example.bozhilun.android;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Process;
import android.util.Log;
import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.NewSmsBroadCastReceiver;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.PhoneSosOrDisPhone;
import com.example.bozhilun.android.db.DBManager;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.AlertService;
import com.example.bozhilun.android.b30.service.B30ConnStateService;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.siswatch.bleus.H8BleManagerInstance;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.w30s.ble.W37BleOperateManager;
import com.example.bozhilun.android.w30s.ble.W37ConnStatusService;
import com.hplus.bluetooth.BleProfileManager;
import com.mob.MobSDK;
import com.sdk.bluetooth.app.BluetoothApplicationContext;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tjdL4.tjdmain.L4M;
import com.umeng.commonsdk.UMConfigure;
import com.veepoo.protocol.VPOperateManager;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by thinkpad on 2016/7/20.
 */

public class MyApp extends LitePalApplication {


    //用于退出activity
    private List<AppCompatActivity> activities;

    //
    private com.yanzhenjie.nohttp.rest.RequestQueue noRequestQueue = null;


    /**
     * 手环MAC地址
     */
    private String macAddress;
    /**
     * 是否正在上传数据
     */
    private boolean uploadDate;
    private static MyApp instance = null;
    public static final String RefreshBroad = "com.example.bozhilun.android.RefreshBroad";
    public static boolean b15pIsFirstConntent = false;

    public static PhoneSosOrDisPhone phoneSosOrDisPhone;

    static {
        phoneSosOrDisPhone = new PhoneSosOrDisPhone();
    }

    public static MyApp getInstance() {
        return instance;
    }



    //接收短信的广播
    private NewSmsBroadCastReceiver newSmsBroadCastReceiver;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppisOne = true;
        AppisOneStar = true;
        activities = new ArrayList<>();


        //App初始启动是断开状态
        MyCommandManager.DEVICENAME = null;
        LitePal.initialize(instance);
        SharedPreferencesUtils.setParam(getContext(), Commont.BATTERNUMBER, 0);//电池电量默认清空
        /**
         * 第三方登陆分享+注册短信
         */
        MobSDK.init(this);

        CrashReport.initCrashReport(getApplicationContext(), "4713db8b55", true);

        initSDK();

        newSmsBroadCastReceiver = new NewSmsBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(newSmsBroadCastReceiver,intentFilter);

    }


    /**
     * 各类型设备服务启动以及 SDK等的初始化
     */
    private void initSDK() {
        /**
         * 减少启动时间---优化启动项
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    Thread.sleep(700);
                    Looper.prepare();
                    // 必须在调用任何统计SDK接口之前调用初始化函数
                    UMConfigure.init(instance, UMConfigure.DEVICE_TYPE_PHONE, null);

                    startW37Server();
                    /**
                     * 启动B30的服务
                     */
                    startB30Server();
                    //绑定通知的服务
                    bindAlertServices();    //绑定通知的服务

                    /**
                     * H8配对工具
                     */
                    HidUtil.getInstance(getContext());

                    /**
                     * H9手表初始化
                     */
                    BluetoothApplicationContext.getInstance().init(getContext());

                    startH9Server();

                    initNoHttpData();

                    setDatabase();
                    dbManager = DBManager.getInstance(getContext());

                    /**
                     * B15P 初始化
                     * 这种方式是不使用SDK中数据库的
                     */
                    L4M.InitData(instance, 1, 0);

                    //B18
                    new BleProfileManager.Builder()
                            .setReConnect(true)
                            .setScanTime(60)
                            .build(instance);

                    B18BleConnManager.getB18BleConnManager();

                    initCloudChannel();

                    Looper.loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }



    public boolean isOne = true;
    public boolean AppisOne = false;
    public boolean AppisOneStar = false;




    /***********************************  前台服务   **********************************************/
    /**
     * 前台服务
     */
    private void bindAlertServices() {
        Intent ints = new Intent(instance, AlertService.class);
        bindService(ints, alertConn, BIND_AUTO_CREATE);
    }

    //前台服务绑定
    private ServiceConnection alertConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("App", "-------------通知启动了--------");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("App", "-------------通知null--------");
        }
    };


    /***********************************  数据库   **********************************************/
    /**
     * ------------- 1
     * 数据库
     */

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    /**
     * -----  2
     * greenDao
     */
    private DBManager dbManager;

    public DBManager getDBManager() {
        return dbManager;
    }


    /***********************************  W30   **********************************************/


    W30SBLEManage mW30SBLEManage;

    public W30SBLEManage getmW30SBLEManage() {
        if (mW30SBLEManage == null) {
            return W30SBLEManage.getInstance(getContext());
        } else {
            return mW30SBLEManage;
        }
    }




    /***********************************  B30   **********************************************/
    /**
     * 启动B30的服务
     */
    //B30手环
    private VPOperateManager vpOperateManager;

    //B30手环的链接服务
    private B30ConnStateService b30ConnStateService;

    private W37ConnStatusService w37ConnStatusService = null;


    //舟海方案手环
    private W37BleOperateManager w37BleOperateManager;


    public B30DataServer getB30DataServer() {
        return B30DataServer.getB30DataServer();
    }

    //B30的服务
    public B30ConnStateService getB30ConnStateService() {
        if (b30ConnStateService == null) {
            startB30Server();
        }
        return b30ConnStateService;
    }


    public VPOperateManager getVpOperateManager() {

        if (vpOperateManager == null) {
            vpOperateManager = VPOperateManager.getMangerInstance(instance);

        }
        return vpOperateManager;
    }

    //启动B30的服务
    public void startB30Server() {
        Intent ints = new Intent(instance, B30ConnStateService.class);
        instance.bindService(ints, b30ServerConn, BIND_AUTO_CREATE);
    }


    public W37ConnStatusService getW37ConnStatusService(){
        if(w37ConnStatusService == null){
            startW37Server();
        }
        return w37ConnStatusService;
    }




    public W37BleOperateManager getW37BleOperateManager(){
        if(w37BleOperateManager == null)
            w37BleOperateManager = W37BleOperateManager.getBleOperateManagerInstance(this);
        return w37BleOperateManager;
    }

    //启动W37的服务
    public void startW37Server(){
        Intent intent = new Intent(instance,W37ConnStatusService.class);
        this.bindService(intent,w37ServiceConn,BIND_AUTO_CREATE);
    }


    //W系列服务
    private ServiceConnection w37ServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                if(service != null){
                    Log.e("APP","---W37-----启动W37服务了----");

                    W37ConnStatusService.W37LoadBuilder w37LoadBuilder = (W37ConnStatusService.W37LoadBuilder) service;

                    w37ConnStatusService = w37LoadBuilder.getW37ConSerevice();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    //B30的服务绑定
    private ServiceConnection b30ServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                if (service != null) {
                    B30ConnStateService.B30LoadBuilder b30LoadBuilder = (B30ConnStateService.B30LoadBuilder) service;

                    b30ConnStateService = b30LoadBuilder.getB30Service();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            b30ConnStateService = null;
        }
    };


    /***********************************  H8   **********************************************/
    //H8
    private H8BleManagerInstance h8BleManagerInstance;


    public H8BleManagerInstance h8BleManagerInstance() {
        if (h8BleManagerInstance == null) {
            h8BleManagerInstance = h8BleManagerInstance.getH8BleManagerInstance();
        }
        return h8BleManagerInstance;
    }


    RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getContext());
        }
        return requestQueue;
    }


    //请求队列，基于okhttp
    public com.yanzhenjie.nohttp.rest.RequestQueue getNoRequestQueue(){
        if(noRequestQueue == null){
            noRequestQueue = NoHttp.newRequestQueue(10);
        }
        return noRequestQueue;
    }




    /**
     * 添加Activity
     */
    public void addActivity(AppCompatActivity activity) {
        // 判断当前集合中不存在该Activity
        if (!activities.contains(activity)) {
            activities.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (AppCompatActivity activity : activities) {
            //unregisterReceiver(refreshBroadcastReceiver);
            activity.finish();
        }
    }


    /**
     * 全局手环MAC地址
     */
    public String getMacAddress() {
        if (macAddress == null)
            macAddress = (String) SharedPreferencesUtils.readObject(instance, Commont.BLEMAC);
        return macAddress;
    }

    /**
     * 全局手环MAC地址
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * 是否正在上传数据
     */
    public boolean isUploadDate() {
        return uploadDate;
    }

    /**
     * 是否正在上传数据
     */
    public void setUploadDate(boolean uploadDate) {
        this.uploadDate = uploadDate;
    }


    /*********H9***********/
    //启动H9的服务
    public void startH9Server() {

        SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_DEVICES_SYS", "2018-12-25 12:20");//获取设备数据  每3分钟后可以获取
        SharedPreferencesUtils.setParam(MyApp.getInstance(), "GET_TIME", "2018-12-25 12:20");//第一次同步电池电量时间
        SharedPreferencesUtils.saveObject(MyApp.getInstance(), "H9_BATTERY", 100);//电池电量值
//        Intent ints = new Intent(instance, BluetoothService.class);
//        instance.bindService(ints, h9ServerConn, BIND_AUTO_CREATE);
    }



    //初始化云推送通道
    private void initCloudChannel() {
        try {
            createNotificationChannel();
            PushServiceFactory.init(instance);
            CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.register(instance, new CommonCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.e("APP", "init cloudchannel success="+response);

                }
                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    Log.d("APP", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initNoHttpData(){
        NoHttp.initialize(InitializationConfig.newBuilder(this)
                //设置全局连接超时时间
                .connectionTimeout(30 * 1000)
                //服务器响应时间
                .readTimeout(10 * 1000)
                //重试次数
                .retry(1)
                //底层网络配置
                .networkExecutor(new OkHttpNetworkExecutor()).build());
    }




    //阿里云推送通知栏
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "11";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "RaceFitPro";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }


}
