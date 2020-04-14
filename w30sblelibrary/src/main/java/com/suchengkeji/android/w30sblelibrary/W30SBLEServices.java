package com.suchengkeji.android.w30sblelibrary;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.suchengkeji.android.w30sblelibrary.bean.W30S_SleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils.isOtaConn;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/6 11:49
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SBLEServices extends Service {
    private final static String TAG = "----手表服务：" + W30SBLEServices.class.getSimpleName();
    private final IBinder mLocalBinder = new LocalBinder();
    //获取蓝牙管理，通过BluetoothManager获取BluetoothAdapter
    private BluetoothManager mBluetoothManager = null;
    //一个Android系统只有一个BluetoothAdapter ，通过BluetoothManager 获取
    private BluetoothAdapter mBluetoothAdapter = null;
    //设备地址
    public String mBluetoothDeviceAddress = null;
    private BluetoothGatt mBluetoothGatt = null;
    private final Object mLocker = new Object();

   // private BluetoothClient bluetoothClient;

    private String mBluetoothDevieName = null;


    public BluetoothManager getmBluetoothManager() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return null;
            }
            return mBluetoothManager;
        } else {
            return mBluetoothManager;
        }
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * 蓝牙连接状态（默认 断开状态  BluetoothGatt.STATE_DISCONNECTED = 0）
     * <p>
     * ***  The profile is in disconnected state
     * public static final int STATE_DISCONNECTED = 0;//断开状态
     * <p>
     * ***The profile is in connecting state
     * public static final int STATE_CONNECTING = 1;//状态连接（正在连接）
     * <p>
     * ***  The profile is in connected state
     * public static final int STATE_CONNECTED = 2;//连接状态
     * <p>
     * *** The profile is in disconnecting state
     * public static final int STATE_DISCONNECTING = 3;//状态断开(正在断开)
     */
    public int mConnectionState = BluetoothGatt.STATE_DISCONNECTED;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED_W30S";//连接
    static final String ACTION_GATT_CONNECTING = "com.example.bluetooth.le.ACTION_GATT__CONNECTING_W30S";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_W30S";//断开
    public final static String ACTION_GATT_FINED_SERVICES = "com.example.bluetooth.le.ACTION_GATT_FINED_SERVICES_W30S";//发现
    //public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_W30S";//可用
    //public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA_W30S";//数据
    // 断开蓝牙后5秒后重新发送连接请求   1s = 1000ms
    //private final long CONNECTE_PERIOD = 6 * 1000;
    //private final int DIS_PERIOD = 404;
    //private int CONNCONT = 200;//自动连接的次数
//    Handler mHandler = new Handler(new ReconnectCallback());
    //Handler mHandler = new Handler(new BleHandler());

    public final static String ACTION_DATA_AVAILABLE_SPORT = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_SPORT_W30S";
    public final static String ACTION_DATA_AVAILABLE_SLEEP = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_SLEEP_W30S";
    public final static String ACTION_DATA_AVAILABLE_DEVICE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_DEVICE_W30S";
    public final static String ACTION_DATA_AVAILABLE_HEATE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_HEATE_W30S";

    public final static String ACTION_FINDE_AVAILABLE_DEVICE = "com.example.bluetooth.le.ACTION_FINDE_AVAILABLE_DEVICE_W30S";
    public final static String ACTION_CAMERA_AVAILABLE_DEVICE = "com.example.bluetooth.le.ACTION_CAMERA_AVAILABLE_DEVICE_W30S";
    public final static String ACTION_CALLER_REJECTION = "com.example.bluetooth.le.ACTION_CALLER_REJECTION_W30S";

    private BlueStateReceiver blueStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("====service=====", "onCreate");
        registerReceiver(bleStateReceiver, regInteFilter());
        initialize();

        regeditBackService();

    }

    protected IntentFilter regInteFilter() {
        IntentFilter intentFilter = new IntentFilter();
        //蓝牙打开或关闭状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //挂断电话广播
        intentFilter.addAction("com.suchengkeji.android.w30sblelibrary.disPhone");
        return intentFilter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "------onStartCommand----");
        return START_STICKY;
    }

    //启动前台服务
    private void regeditBackService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "11";
            String channelName = "channel_name";
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this);
//            builder.setSmallIcon(R.drawable.ic_noti_s);
            builder.setSmallIcon(R.drawable.ic_noti_s);
            builder.setContentText("RaceFitPro");
            builder.setContentTitle("RaceFitPro");
            //创建通知时指定channelID
            builder.setChannelId(channelID);
            Notification notification = builder.build();

//            // 通知行为（点击后能进入应用界面）
//            Intent intent = new Intent(this, B30HomeActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(11, notification);
            startForeground(11, notification);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                //Notification.Builder builder = new Notification.Builder(this,11);
                builder.setSmallIcon(R.drawable.ic_noti_s);
                builder.setContentText("RaceFitPro");
                builder.setContentTitle("RaceFitPro");
                // 设置通知的点击行为：自动取消/跳转等
                builder.setAutoCancel(false);
                startForeground(11, builder.build());
            }


        }


//        //>18，弹出通知
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT<=Build.VERSION_CODES.N_MR1){
//            Notification.Builder builder = new Notification.Builder(this);
//            builder.setSmallIcon(R.drawable.ic_noti_s);
//            builder.setContentTitle("RaceFit Pro");
//            builder.setContentText("RaceFit Pro");
//            // 设置通知的点击行为：自动取消/跳转等
//            builder.setAutoCancel(false);
//            startForeground(11,builder.build());
//
//        }
////        else{
////            startForeground(11,new Notification());
////        }

    }


    @Override
    public void onDestroy() {
        mNewHandler.sendEmptyMessage(ServiceDestruction);
        Log.e(TAG, "---------service--ondestory");
        super.onDestroy();
        unregisterReceiver(bleStateReceiver);

        //如果服务被杀死，移除通知，并重写启动服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(11);

            Intent its = new Intent(getApplicationContext(), W30SBLEServices.class);
            startService(its);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("====service=====", "onBind");
        return mLocalBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //使用一个给定的设备后，你应该确保bluetoothgatt（）调用
        //这样资源才能得到适当的清理。在这个特定的例子中，（）是
        //当UI从服务断开时调用。
        //close();
        mNewHandler.sendEmptyMessage(ServiceDestruction);
        return super.onUnbind(intent);
    }

    /**
     * 在使用一个给定的设备后，应用程序必须调用此方法以确保资源被正确释放
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    /**
     * 获取此服务
     * Get this service
     */
    public class LocalBinder extends Binder {
        public W30SBLEServices getService() {
            return W30SBLEServices.this;
        }
    }

    /**
     * 初始化本地蓝牙适配器的引用。
     *
     * @return
     */
    public boolean initialize() {
        mBluetoothDeviceAddress = (String)SharedPreferencesUtils.readObject(getApplicationContext(), "mylanmac");
        //mBluetoothDeviceAddress = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanmac", "");
        //API 18级及以上，就可以通过蓝牙适配器
        if (mBluetoothManager == null) {
            //通过BluetoothManager来获取BluetoothAdapter
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "无法初始化bluetoothmanager.");
                return false;
            }
        }
        //一个Android系统只有一个BluetoothAdapter ，通过BluetoothManager 获取
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "无法获得一个BluetoothAdapter.");
            return false;
        }
        return true;
    }

    //所有操作回调
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

        //当连接状态发生改变的时候
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            synchronized (mLocker) {
                Log.e(TAG, "-----onConnectionStateChange--=连接状态发生改变=" + newState + gatt.getDevice().getAddress());
                String inActions = "";
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTED://已经连接
                        inActions = ACTION_GATT_CONNECTED;
                        //broadcastUpdate(inActions);
                        Log.e(TAG, "------------已经连接");
                        mConnectionState = BluetoothGatt.STATE_CONNECTED;
                        automaticallyReconnect(false);//体制循环自动连接
                        BluetoothDevice device = gatt.getDevice();
                        if (!TextUtils.isEmpty(device.getAddress()) ) {
                            Log.e("==========蓝牙名字",mBluetoothDevieName);
                            if(!isOtaConn){     //不是升级状态时保存地址，
                                SharedPreferencesUtils.saveObject(getApplicationContext(), "mylanmac", device.getAddress().trim());
                                SharedPreferencesUtils.saveObject(getApplicationContext(), "mylanya", mBluetoothDevieName.equals("NX4")?"W37":mBluetoothDevieName);
                            }

                            // 将Mac地址
                            //SharedPreferenceUtil.put(getApplicationContext(), "mylanmac", device.getAddress().trim());
                            // name
                            //SharedPreferenceUtil.put(getApplicationContext(), "mylanya", "W30");

                            Intent intent = new Intent(inActions);
                            intent.putExtra("bName",mBluetoothDevieName);
                            sendBroadcast(intent);


                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mBluetoothGatt != null) {
                            //发现服务
                            mBluetoothGatt.discoverServices();
                            Log.i(TAG, "试图启动服务发现" + mBluetoothGatt.discoverServices());
                        } else {
                            //disconnectBle();
                            String mBluetoothDeviceAddress = (String) SharedPreferencesUtils.readObject(getApplicationContext(), "mylanmac");
//                            String mBluetoothDeviceAddress = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanmac", "");


//                            if (!TextUtils.isEmpty(mBluetoothDeviceAddress)) {
//                                connect(mBluetoothDeviceAddress);
//                            }
                        }
                        break;
                    case BluetoothGatt.STATE_CONNECTING://正在连接
                        automaticallyReconnect(false);
                        mConnectionState = BluetoothGatt.STATE_CONNECTING;
                        broadcastUpdate(ACTION_GATT_CONNECTING);
                        Log.e(TAG, "-----------正在连接");
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED: //连接断开
                        inActions = ACTION_GATT_DISCONNECTED;
                        broadcastUpdate(inActions);
                        mConnectionState = BluetoothGatt.STATE_DISCONNECTED;
                        Log.d(TAG, "------------断开");
                        if (mBluetoothGatt != null) {
                            mBluetoothGatt.close();
                            mBluetoothGatt = null;
                        }
//                        String mBluetoothDeviceAddress = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanmac", "");
                        String mBluetoothDeviceAddress = (String) SharedPreferencesUtils.readObject(getApplicationContext(), "mylanmac");
                        if (!TextUtils.isEmpty(mBluetoothDeviceAddress)) {
                            Log.e(TAG, "--------非主动断开=" + initialize());
                            if (initialize()) {

                                mNewHandler.sendEmptyMessage(UnknownDisconnected);//非手动断开
                            }

                        } else {
                            Log.e(TAG, "-----主动断开---");
                            mNewHandler.sendEmptyMessage(KnownDisconnection);//手动断开
                        }
                        break;
                }

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (gatt != null && status == BluetoothGatt.GATT_SUCCESS) { //找到服务了
                Log.i(TAG, "--------onServicesDiscovered-----------是否找到服务时");
                //在这里可以对服务进行解析，寻找到你需要的服务
                startNotification(String.valueOf(W30SBLEGattAttributes.UUID_SYSTEM_SERVICE), String.valueOf(W30SBLEGattAttributes.UUID_SYSTEM_WRITE));
                broadcastUpdate(ACTION_GATT_FINED_SERVICES);
            } else {
                Log.w(TAG, "onServicesDiscovered received(收到的服务发现): " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            synchronized (mLocker) {
                Log.i(TAG, "-------------------设备发出通知");
                //W30S
                ReadingBleData(characteristic);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e(TAG,"------onCharacteristicRead="+gatt.getDevice().getAddress()+"----chart="+Arrays.toString(characteristic.getValue()));
        }
    };

    /**
     * 广播~链接状态
     *
     * @param action
     */
    private void broadcastUpdate(final String action) {
        Log.e(TAG, "----服务-action-=" + action);
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    public static CallDatasBackListenter mCallDatasBackListenter;

    public static void setmCallDatasBackListenter(CallDatasBackListenter mCallDatasBackListenter) {
        W30SBLEServices.mCallDatasBackListenter = mCallDatasBackListenter;
    }

    public interface CallDatasBackListenter {

        void CallDatasBackListenter(W30SSportData sportData);

        void CallDatasBackListenter(W30SSleepData sleepData);

        void CallDatasBackListenter(W30SDeviceData deviceData);

        void CallDatasBackListenter(W30SHeartData heartData);

        void CallDatasBackListenterIsok();//数据返回完成
    }


    public static DisPhoneCallListener disPhoneCallListener;

    public static void setDisPhoneCallListener(DisPhoneCallListener disPhoneCallListener) {
        W30SBLEServices.disPhoneCallListener = disPhoneCallListener;
    }

    public interface DisPhoneCallListener {  //挂断电话的接口
        void disCallPhone(int disV);

    }

    /**
     * 写指令到蓝牙设备
     *
     * @param value
     * @return
     */
    boolean writeRXCharacteristic(final byte[] value) {
        if (value == null || value.length <= 0) return false;
        boolean status = false;
        if (mBluetoothGatt == null)
            return false;
        final BluetoothGattService RxService = mBluetoothGatt
                .getService(W30SBLEGattAttributes.UUID_SYSTEM_SERVICE);
        if (RxService == null) {
            return status;
        }
        final BluetoothGattCharacteristic bluetoothGattCharacteristic = RxService
                .getCharacteristic(W30SBLEGattAttributes.UUID_SYSTEM_WRITE);
        if (bluetoothGattCharacteristic == null) {
            return status;
        }
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                getCharacter(String.valueOf(W30SBLEGattAttributes.UUID_SYSTEM_SERVICE),
//                        String.valueOf(W30SBLEGattAttributes.UUID_SYSTEM_WRITE));
        int length = value.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < 20) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = value[i + copy_size];
                }
                bluetoothGattCharacteristic.setValue(val);
                status = mBluetoothGatt
                        .writeCharacteristic(bluetoothGattCharacteristic);
            } else {
                byte[] val = new byte[20];
                for (int i = 0; i < 20; i++) {
                    val[i] = value[i + copy_size];
                }
                bluetoothGattCharacteristic.setValue(val);
                status = mBluetoothGatt
                        .writeCharacteristic(bluetoothGattCharacteristic);
            }
            copy_size += 20;
            length -= 20;
            try {
                Thread.sleep(W30SBLEGattAttributes.BleWriteSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    /**
     * 开启通知
     */
    private void startNotification(String serviceUUID, String charaterUUID) {
        List<BluetoothGattService> supportedGattServices = getSupportedGattServices();
        //循环遍历服务以及每个服务下面的各个特征，判断读写，通知属性
        for (BluetoothGattService gattService : supportedGattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                int charaProp = characteristic.getProperties();
                List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                Log.e(TAG, "--------onServicesDiscovered-----------descriptors长度" + descriptors.size());
                for (int i = 0; i < descriptors.size(); i++) {
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid());
                        Log.e("nihao", "gattCharacteristic的属性为:  可读");
                        setCharacteristicNotification(characteristic, false);
                    }
                    readCharacteristic(String.valueOf(serviceUUID), String.valueOf(charaterUUID));
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid());
                        Log.e("nihao", "gattCharacteristic的属性为:  可写");
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid() + characteristic);
                        Log.e("nihao", "gattCharacteristic的属性为:  具备通知属性");
                        setCharacteristicNotification(characteristic, true);
                    }
                }

            }
        }

//        Log.e(TAG, "--------onServicesDiscovered-----------找到服务了" + serviceUUID + "==" + charaterUUID);
//        BluetoothGattCharacteristic characteristic = getCharacter(serviceUUID, charaterUUID);
//        if (characteristic != null) {
//            Log.e(TAG, "--------onServicesDiscovered-----------characteristic不空" + serviceUUID + "==" + charaterUUID);
//            int charaProp = characteristic.getProperties();
//            Log.e(TAG, "--------onServicesDiscovered-----------charaProp" + charaProp);
//            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
//            Log.e(TAG, "--------onServicesDiscovered-----------descriptors长度" + descriptors.size());
//            for (int i = 0; i < descriptors.size(); i++) {
//                Log.e(TAG, descriptors.size() + "========" + descriptors.get(i).getUuid() + "=======" + Arrays.toString(descriptors.get(i).getValue()));
//
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                    Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid());
//                    Log.e("nihao", "gattCharacteristic的属性为:  可读");
//                    setCharacteristicNotification(characteristic, false);
//                }
//                readCharacteristic(String.valueOf(serviceUUID), String.valueOf(charaterUUID));
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                    Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid());
//                    Log.e("nihao", "gattCharacteristic的属性为:  可写");
//                }
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                    Log.e("nihao", "gattCharacteristic的UUID为:" + characteristic.getUuid() + characteristic);
//                    Log.e("nihao", "gattCharacteristic的属性为:  具备通知属性");
//                    setCharacteristicNotification(characteristic, true);
//                }
//            }
//
//        }


//        if (characteristic != null) {
//            int mProperties = characteristic.getProperties();
//            if ((mProperties | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {// 如果有一个活跃的通知上的特点,明确第一所以不更新用户界面上的数据字段。
//                if (characteristic != null) {
//                    setCharacteristicNotification(characteristic, false);
//                }
//                readCharacteristic(String.valueOf(serviceUUID), String.valueOf(charaterUUID));
//                if ((mProperties | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                    setCharacteristicNotification(characteristic, true);
//                }
//            }
//        }
    }


    /**
     * 读
     *
     * @param service_uuid
     * @param cha_uuid
     */
    private void readCharacteristic(String service_uuid, String cha_uuid) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;
        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getCharacter(service_uuid, cha_uuid);
        if (mBluetoothGattCharacteristic != null)
            mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
    }

    /**
     * 启用或禁用对给定特性的通知
     *
     * @param characteristic
     * @param enabled
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        //这里可以加入判断对指定的UUID值进行订阅
//        if (String.valueOf(W30SBLEGattAttributes.UUID_SYSTEM_SERVICE).equals(characteristic.getUuid())) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(W30SBLEGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor == null) return;
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * 获取特征值
     */
    private BluetoothGattCharacteristic getCharacter(String serviceUUID, String characterUUID) {
        if (mBluetoothGatt == null) {
            return null;
        }
        Log.e("error", "设备名称：" + mBluetoothGatt.getDevice().getAddress());
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            return service.getCharacteristic(UUID.fromString(characterUUID));
        }
        return null;
    }

    /**
     * 检索连接设备上受支持GATT服务的列表。
     *
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }


    /**
     * 读取蓝牙数据
     * 在【onCharacteristicChanged】里，调用
     *
     * @param characteristic
     */
    void ReadingBleData(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(
                W30SBLEGattAttributes.UUID_SYSTEM_READ)) {
            byte[] btData = characteristic.getValue();

            Log.e(TAG,"--------读取蓝牙数据="+Arrays.toString(btData));

            Log.e(TAG, "-------===ReadingBleData===" + characteristic.getUuid() + "=======" + Arrays.toString(btData));
            if (btData.length > 0)
                processRcvData(btData);
//            if (W30SBLEManage.getInstance(getApplicationContext()).isDataReady)
//                parseRcvData(W30SBLEManage.getInstance(getApplicationContext()).mBtRecData);

            if (btData.length > 10 && btData[10] == 12) {
                Log.e(TAG, "---------挂断电话-=" + btData[10]);
                if (disPhoneCallListener != null) {
                    disPhoneCallListener.disCallPhone(12);
                }
            }

            if (isDataReady)
                parseRcvData(mBtRecData);
        }
    }


    public static boolean isDataReady = false;
    public static byte[] mNullBuffer = new byte[400];
    public static byte[] mBtRecData = new byte[400];
    private static int mRcvDataState = 0;
    private static int received_content_length = 0;
    private static int length_to_receive = 0;

    /**
     * 数据包组合
     *
     * @param data
     */
    public void processRcvData(byte[] data) {
        switch (mRcvDataState) {
            case 0:
                if (data[0] == (byte) (0xab)) {
                    received_content_length = 0;
                    System.arraycopy(mNullBuffer, 0, mBtRecData,
                            received_content_length, 100);
                    System.arraycopy(data, 0, mBtRecData, received_content_length,
                            data.length);
                    received_content_length = data.length;
                    int new_lenght = data[2] << 8 | data[3];
                    length_to_receive = new_lenght + 8;
                    length_to_receive -= data.length;
                    if (length_to_receive <= 0) {
                        mRcvDataState = 0;
                        received_content_length = 0;
                        isDataReady = true;
                    } else {
                        mRcvDataState = 1;
                        isDataReady = false;
                    }
                }

                break;
            case 1:
                System.arraycopy(data, 0, mBtRecData, received_content_length,
                        data.length);
                received_content_length += data.length;
                length_to_receive -= data.length;
                if (length_to_receive <= 0) {
                    mRcvDataState = 0;
                    isDataReady = true;


                } else {
                    isDataReady = false;
                }
                break;
        }
    }

    /****************************     启动打开蓝牙服务   *******************************/


    //解析数据
    private void parseRcvData(byte[] data) {
        if (data[0] != (byte) 0xab)
            return;
        if (data[8] != 3)
            return;
        Log.d("======W30=BACK======", data[10] + "");
        switch (data[10]) {
            case W30SBLEGattAttributes.Key_Motion: { // 返回运动数据
                boolean checkData = checkData(data);
                Log.e("===========W30-Data=", "Key_Motion");
                //身高，需要根据用户数据修改
                int user_height = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "user_height",170);
                //int user_height = (int) SharedPreferenceUtil.get(getApplicationContext(), "user_height", 170);
                //int user_height = 170;
                //体重，需要根据用户数据修改
                //int user_weight = (int) SharedPreferenceUtil.get(getApplicationContext(), "user_weight", 65);
                int user_weight = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "user_weight",65);
                //int user_weight = 65;
                Log.d("-----用户资料-----2----", user_height + "===" + user_weight);
                if (checkData) {
                    HandleMotion(data, user_height, user_weight);
                    Log.d(TAG, "-parseRcvData-------返回运动数据");
                }
            }
            break;
            case W30SBLEGattAttributes.Key_Sleep: { //睡眠数据
                Log.e("===========W30-Data=", "Key_Sleep");
                boolean checkData = checkData(data);
                if (checkData) {
                    HandleSleep(data);
                    //broadcastUpdateSleep(ACTION_DATA_AVAILABLE_SLEEP, data);
                    Log.d(TAG, "-parseRcvData-------睡眠数据");
                }
            }
            break;
            case W30SBLEGattAttributes.Key_Complete: {//完成;
                Log.e("===========W30-Data=", "Key_Complete");
                System.out.println("数据返回完成");
                Log.d(TAG, "-parseRcvData-------数据返回完成");
                mCallDatasBackListenter.CallDatasBackListenterIsok();
            }
            break;
            case W30SBLEGattAttributes.Key_Photo: {// 遥控拍照
                Log.e("===========W30-Data=", "Key_Photo");
                System.out.println("遥控拍照");
                Log.d(TAG, "-parseRcvData-------遥控拍照");
                broadcastUpdate(ACTION_CAMERA_AVAILABLE_DEVICE);
            }
            break;
            case W30SBLEGattAttributes.Key_FindPhone: { //找手机
                Log.e("===========W30-Data=", "Key_FindPhone");
                System.out.println("找手机");
                Log.d(TAG, "-parseRcvData-------找手机");
                broadcastUpdate(ACTION_FINDE_AVAILABLE_DEVICE);
            }
            break;
            case W30SBLEGattAttributes.Key_DeviceInfo: {//设备信息
                Log.e("===========W30-Data=", "Key_DeviceInfo");
                Log.d(TAG, "-parseRcvData-------设备信息");
                HandleDevice(data);
                // broadcastUpdateDevice(ACTION_DATA_AVAILABLE_DEVICE, data);
            }
            break;
            case W30SBLEGattAttributes.Key_HangPhone: {  //运动心率
                Log.e("===========W30-Data=", "Key_HangPhone");
                Log.d(TAG, "-parseRcvData-------运动心率");
                System.out.println("来电拒接");
                broadcastUpdate(ACTION_CALLER_REJECTION);
            }
            break;
            case W30SBLEGattAttributes.Key_MoHeart: { //运动心率
                Log.e("===========W30-Data=", "Key_MoHeart");
                boolean checkData = checkData(data);
                if (checkData) {
                    HandleMoHeart(data);
                    //broadcastUpdateHeate(ACTION_DATA_AVAILABLE_HEATE, data);
                    Log.d(TAG, "-parseRcvData-------运动心率");
                }
            }
            break;
        }
    }


    /**
     * 校验收到数据是否完整 = 外部调用
     *
     * @param mBtRecData
     * @return
     */
    private boolean checkData(byte[] mBtRecData) {
        byte checkNum = mBtRecData[4];
        int start = 13;
        byte crc = 0;
        int length = (mBtRecData[3] & 0xFF) - 5;
        for (int i = start; i < start + length; i++) {
            crc = getCheckNum(mBtRecData[i], crc);
        }
        if (checkNum == crc) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验收到数据是否完整 = 内部调用
     *
     * @param value
     * @param crc
     * @return
     */
    private byte getCheckNum(byte value, byte crc) {
        byte polynomial = (byte) 0x97;
        crc ^= value;

        for (int i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0) {
                crc <<= 1;
                crc ^= polynomial;
            } else {
                crc <<= 1;
            }
        }
        return crc;
    }


    //==================================解析数据===============================

    /**
     * 解析运动数据
     *
     * @param data
     */
    void HandleMotion(byte[] data, int user_height, int user_weight) {
        Log.d(TAG, "解析运动数据");
        String date = W30SBleUtils.getDate(data);
        int sport_item_count = data[16] & 0x1F;
        int sport_data_pos = 17;
        int sportStep = 0;
        List sport_data = new ArrayList();
        for (int i = 0; i < sport_item_count; i++) {
            int step = (int) ((((int) data[sport_data_pos] & 0xff) << 8) | data[sport_data_pos + 1] & 0xff);
            sport_data.add(step);
            sportStep += step;
            sport_data_pos += 2;
        }
        float Calory = 0;
        float Distance = 0;
        Calory = W30SBleUtils.getCalory((float) user_height, (float) user_weight, sportStep);
        Distance = W30SBleUtils.getDistance((float) user_height, sportStep);
        Log.d(TAG, "解析运动数据 日期 =  " + date);
        Log.d(TAG, "解析运动数据 步数 =  " + sportStep);
        Log.d(TAG, "解析运动数据 卡路里 =  " + Calory);
        Log.d(TAG, "解析运动数据 距离 =  " + Distance);
        Log.d(TAG, "解析运动数据 数据 =  " + sport_data.toString());
        W30SSportData w30SSportData = new W30SSportData(date, sportStep, Calory, Distance, sport_data);
        mCallDatasBackListenter.CallDatasBackListenter(w30SSportData);
        //broadcastUpdateSport(ACTION_DATA_AVAILABLE_SPORT, w30SSportData);
    }

    /**
     * 解析睡眠数据
     *
     * @param data
     */
    void HandleSleep(byte[] data) {
        Log.d(TAG, "解析睡眠数据");
        int sleep_count_item = data[15];
        int sleep_data_pos = 16;
        String sleep_date = "";
        List<W30S_SleepData> w30SSleepDataList = new ArrayList<W30S_SleepData>();
        W30S_SleepData mW30SSleepData = null;
        sleep_date = W30SBleUtils.getBeforeDay(W30SBleUtils.getDate(data));
        int end_hour0 = (int) (data[sleep_data_pos] & 0xf8) >> 3;
        if (end_hour0 >= 23 || end_hour0 < 8) {
            mW30SSleepData = new W30S_SleepData("0", "23:00");
            w30SSleepDataList.add(mW30SSleepData);
        }
        for (int i = 0; i < sleep_count_item; i++) {
            byte cca1[] = new byte[2];
            cca1[0] = data[sleep_data_pos + 2 * i];
            cca1[1] = data[sleep_data_pos + 2 * i + 1];
            int start_hour, start_min;
            int sleep_type;
            start_hour = (int) (data[sleep_data_pos + 2 * i] & 0xf8) >> 3;
            start_min = (int) (data[sleep_data_pos + 2 * i] & 0x7) << 3
                    | (int) (data[sleep_data_pos + 2 * i + 1] & 0xE0) >> 5;
            String sHour, sMin;
            if (start_hour < 10) sHour = "0" + start_hour;
            else sHour = "" + start_hour;
            if (start_min < 10) sMin = "0" + start_min;
            else sMin = "" + start_min;
            sleep_type = data[sleep_data_pos + i * 2 + 1] & 0x0F;
            mW30SSleepData = new W30S_SleepData(String.valueOf(sleep_type), (sHour + ":" + sMin));
            w30SSleepDataList.add(mW30SSleepData);
        }
        Log.d(TAG, "解析睡眠数据 = 日期 = " + sleep_date);
        Log.d(TAG, "解析睡眠数据 = 数据 = " + w30SSleepDataList.toString());
        List<W30S_SleepDataItem> w30SSleepDataItems = new ArrayList<>();
        for (int i = 0; i < w30SSleepDataList.size(); i++) {
            W30S_SleepDataItem w30SSleepDataItem = new W30S_SleepDataItem();
            w30SSleepDataItem.setSleep_type(w30SSleepDataList.get(i).getSleep_type());
            w30SSleepDataItem.setStartTime(w30SSleepDataList.get(i).getStartTime());
            w30SSleepDataItems.add(w30SSleepDataItem);
        }
//        W30SSleepData sleepData = new W30SSleepData(sleep_date, w30SSleepDataList);
        W30SSleepData sleepData = new W30SSleepData(sleep_date, w30SSleepDataItems);
        mCallDatasBackListenter.CallDatasBackListenter(sleepData);
    }

    /**
     * 解析设备信息
     *
     * @param data
     */
    void HandleDevice(byte[] data) {
        Log.d(TAG, "解析设备信息");
        int DevicePower = (int) data[13];
        int DeviceType = (int) data[14];
        int DeviceVersionNumber = (int) data[15];
        Log.d(TAG, "解析设备信息 = 设备电量 = " + DevicePower);
        Log.d(TAG, "解析设备信息 = 设备类型 = " + DeviceType);
        Log.d(TAG, "解析设备信息 = 设备版本 = " + DeviceVersionNumber);
        W30SDeviceData deviceData = new W30SDeviceData(DevicePower, DeviceType, DeviceVersionNumber);
        mCallDatasBackListenter.CallDatasBackListenter(deviceData);
    }

    /**
     * 解析运动心率数据
     *
     * @param data
     */
    void HandleMoHeart(byte[] data) {
        Log.d(TAG, "解析运动心率数据");
        String date = W30SBleUtils.getDate(data);
        int heart_data_pos = 17;
        int heart_item_count = 288;
        List wo_heart_data = new ArrayList();
        for (int i = 0; i < heart_item_count; i++) {
            int heart_value = data[heart_data_pos + i] & 0xFF;
            wo_heart_data.add(heart_value);
        }
        Log.d(TAG, "解析运动心率 数据 =  " + wo_heart_data.toString() + "=====" + wo_heart_data.size());
        Log.d(TAG, "解析运动心率 日期 =  " + date);
        W30SHeartData heartData = new W30SHeartData(date, wo_heart_data);
        mCallDatasBackListenter.CallDatasBackListenter(heartData);
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    private final int UnknownDisconnected = 0x01;//非手动断开
    private final int KnownDisconnection = 0x02;//手动断开
    private final int SuccessfulLink = 0x03;//链接成功
    private final int CircularLinks = 0x04;//循环链接
    private final int ServiceDestruction = 0x05;//服务销毁
    private final int ClearRunna = 0x06;//清楚东西

    private Handler mNewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.e(TAG, "------handmsg-=" + msg.what);
            switch (msg.what) {
                case UnknownDisconnected://非手动断开自动重连
                    Log.e(TAG, "------非手动断开自动重连-");
                   // automaticallyReconnect(true);
                    break;
                case KnownDisconnection://手动断开
                    disClearData();
                   // automaticallyReconnect(false);
                    disconnectBle();
                    close();
                    break;
                case CircularLinks:
                    Log.e(TAG, "-----z----自动重连-");
                   // automaticallyReconnect(true);
                    break;
                case ServiceDestruction://服务销毁
                    automaticallyReconnect(false);
                    disconnectBle();
                    close();
                    break;
                case ClearRunna:
                    automaticallyReconnect(false);
                    mNewHandler.removeCallbacks(runnable);
                    if (mBluetoothAdapter != null && mLeScanCallback != null) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    break;
                case 1001:
                    BluetoothDevice bd = (BluetoothDevice) msg.obj;
                    if(bd != null){
                        synchronized (mLocker) {
                            String mDeviceAddress = (String) SharedPreferencesUtils.readObject(getApplicationContext(), "mylanmac");
                            //String mDeviceAddress = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanmac", "");
                            Log.e(TAG, "-------搜索-hand=" + mDeviceAddress + "----是否相等=" + (bd.getAddress().equals(mDeviceAddress.trim())) + "--搜索的地址=" + bd.getAddress().trim());
                            if (!TextUtils.isEmpty(mDeviceAddress) && bd.getAddress().trim().equals(mDeviceAddress.trim())) {
                                Log.e(TAG, "------1001---coonn---");
                                if(bd.getName().length()>=3){
                                    //停止扫描
                                    mNewHandler.sendEmptyMessage(ClearRunna);
                                    connect(bd.getName().substring(0,3),mDeviceAddress);
                                }

                            }
                        }
                    }

                    break;
            }
            return false;
        }
    });


    /**
     * 循环自动重连
     */
    Runnable runnable = null;

    public void automaticallyReconnect(final boolean isAuto) {
        Log.e(TAG, "------isAuto-=" + isAuto);
        String bn = (String) SharedPreferencesUtils.readObject(getApplicationContext(), "mylanmac");
        //String bn = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanmac", "");
        if (!TextUtils.isEmpty(bn)) {

            synchronized (mLocker) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mNewHandler != null)
                            mNewHandler.removeMessages(CircularLinks);
                        if (isAuto) {
                            if (initialize()) {
                             //   sanningBle(true);
                            }

                        } else {
                            //sanningBle(false);
//                        if (runnable != null) {
//                            mNewHandler.sendEmptyMessage(ClearRunna);
//                        }
                        }
                    }
                };
                mNewHandler.postDelayed(runnable, 2 * 1000);
            }
        }

    }

    /**
     * 是否开始扫描
     *
     * @param
     */
    public void sanningBle(boolean status) {
        Log.e(TAG, "----是否开始扫描-=" + status);
        if (status) {
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                Log.e(TAG, "-----开始扫描了-----");
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                Log.e(TAG, "---mBluetoothAdapter == null --开始扫描了-----");
                if(mBluetoothManager!=null){
                    Log.e(TAG, "-----开始扫描了-----");
                    mBluetoothAdapter = mBluetoothManager.getAdapter();
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }else {
                    mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = mBluetoothManager.getAdapter();
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }
        } else {
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
        // mNewHandler.sendEmptyMessageAtTime(ClearRunna, 5000);
    }

    //扫描返回
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG, "------搜索-=" + device.getName() + "-=mac=" + device.getAddress());
            Message message = new Message();
            message.what = 1001;
            message.obj = device;
            mNewHandler.sendMessage(message);

        }
    };


    public void connect(final String bleName, final String address) {
        Log.e(TAG, "-----conn-address-=" + address);
        synchronized (mLocker) {
            //disconnectBle();
            mNewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    automaticallyReconnect(false);
                    if (mConnectionState == 1 || mConnectionState == 2) {
                        return;
                    }
                    Log.e(TAG, "----开始连接了---");
                    boolean isCon = connectBle(bleName,address);
                    Log.e(TAG, "----isCon=" + isCon);
                    if (!isCon) {
                        mNewHandler.sendEmptyMessage(CircularLinks);
                    }
                }
            }, 3000);
        }
    }


    /**
     * 链接蓝牙
     *
     * @param address
     * @return
     */
    public boolean connectBle(String bleName ,String address) {
        Log.e(TAG,"---------连接="+bleName);
        this.mBluetoothDevieName = bleName;
        if (mBluetoothAdapter == null || address == null)
            return false;
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        if (mBluetoothGatt != null) {
            // disconnectBle();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        return true;
    }

    /**
     * 断开现有连接或取消挂起的连接。断开连接的结果是异步报告的
     */
    public void disconnectBle() {
        mNewHandler.sendEmptyMessage(ClearRunna);
        try {
            if (mBluetoothAdapter == null || mBluetoothGatt == null)
                return;
            mBluetoothGatt.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 手动断开清楚数据
     */
    public void disClearData() {
        SharedPreferencesUtils.saveObject(getApplicationContext(), "mylanmac", "");
        SharedPreferencesUtils.saveObject(getApplicationContext(), "mylanya", "");
        // 将Mac地址
        //SharedPreferenceUtil.put(getApplicationContext(), "mylanmac", "");
        // name
        //SharedPreferenceUtil.put(getApplicationContext(), "mylanya", "");
    }

    /**
     * 接收蓝牙打开或关闭的广播
     */
    private BroadcastReceiver bleStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ation = intent.getAction();
            Log.e(TAG, "----action-=" + ation);
            if (ation != null) {
                if (ation.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    Log.e(TAG, "-----bleState--=" + bleState);
                    switch (bleState) {
                        case BluetoothAdapter.STATE_TURNING_ON: //蓝牙打开 11
                            String w30Name = (String)SharedPreferencesUtils.readObject(getApplicationContext(), "mylanya");
                            //String w30Name = (String) SharedPreferenceUtil.get(getApplicationContext(), "mylanya", "");
                            if (!TextUtils.isEmpty(w30Name) && (w30Name.equals("W30")||w30Name.equals("W31") || w30Name.equals("W37"))) {
                                if (!isOtaConn && mConnectionState != BluetoothGatt.STATE_CONNECTED) {
                                    mBluetoothManager = getmBluetoothManager();
                                    mNewHandler.sendEmptyMessage(UnknownDisconnected);//非手动断开
                                }
                            }

                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:    //蓝牙关闭 13
                            if (!isOtaConn) {
                                automaticallyReconnect(false);
                            }
                            break;
                    }

                }
            }
        }
    };

}

