package com.example.bozhilun.android.w30s.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Admin
 * Date 2019/7/3
 */
public class W37BleOperateManager {

    //开始拍照的指令
    public static final String W37_CAMERA_TAKE_PHOTO = "com.example.bozhilun.android.w30s.ble.take_photo";
    //退出拍照的指令
    public static final String W37_CAMERA_DIS_TAKE_PHOTO = "com.example.bozhilun.android.w30s.ble.dis_take_photo";
    public static final String W37_FIND_PHONE_ACTION = "com.example.bozhilun.android.w30s.ble.find_phone";


    //XWatch拍照指令
    public static final String X_WATCH_TAKE_PHOTO = "com.example.bozhilun.android.x_watch_take_photo";



    private static final String TAG = "W37BleOperateManager";

    private static final String SAVE_BLE_MAC_KEY = "w37_ble_mac";

    // 舟海系列系统=服务ID
    private UUID UUID_SYSTEM_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    // 系统=写入数据ID
    private UUID UUID_SYSTEM_WRITE = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    // 系统=读取数据ID
    private UUID UUID_SYSTEM_READ = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");


    //xwatch
    private UUID X_WATCH_UUID_SYSTEM_SERVICED = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID X_WATCH_UUID_SYSTEM_WRITE = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    private UUID X_WATCH_UUID_SYSTEM_NOTI = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb"); //

    //SWatch
    private UUID S_WATCH_UUID_SYSTEM_SERVICE = UUID.fromString("d973f2e0-b19e-12e8-9e96-0800200c9a66");
    private UUID S_WATCH_UUID_SYSTEM_WRITE = UUID.fromString("d973f2e2-b19e-12e8-9e96-0800200c9a66");
    private UUID S_WATCH_UUID_SYSTEM_NOTI = UUID.fromString("d973f2e1-b19e-12e8-9e96-0800200c9a66");


    //B11 UUID
    //server_uuid
    private UUID B11_SYSTEM_SERVER_UUID = UUID.fromString("C2E6FAB0-E966-1000-8000-BEF9C223DF6A");
    private UUID B11_SYSTEM_WRITE_UUID = UUID.fromString("C2E6FAB2-E966-1000-8000-BEF9C223DF6A");
    private UUID B11_SYSTEM_NOTIFY_UUID = UUID.fromString("C2E6FAB1-E966-1000-8000-BEF9C223DF6A");
    private UUID B11_CONTROL_UUID = UUID.fromString("C2E6FAB3-E966-1000-8000-BEF9C223DF6A");




    private static W37BleOperateManager bleOperateManager;
    private static BluetoothClient bluetoothClient;
    private static InterfaceManager interfaceManager = new InterfaceManager();
    private static Context mContext;

    private Vibrator mVibrator;

    private MediaPlayer mMediaPlayer;



    public static W37BleOperateManager getBleOperateManagerInstance(Context context){
        context = context.getApplicationContext();
        mContext = context;
        if(bleOperateManager == null){
            synchronized (W37BleOperateManager.class){
                if(bleOperateManager == null){
                    bleOperateManager = new W37BleOperateManager();
                    bluetoothClient = new BluetoothClient(context);
                }
            }

        }
        return bleOperateManager;
    }


    public W37BleOperateManager() {
    }

    /**
     * 搜索设备
     * @param searchResponse 回调
     * @param duration 搜索时间
     * @param times 搜索次数
     *   eg:duration=10 * 1000;times=1 表示：10s搜索一次，每次10s
     */
    public void startScanBleDevice(final SearchResponse searchResponse, int duration, int times){
        final SearchRequest searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(duration,times)
                .build();
        bluetoothClient.search(searchRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                searchResponse.onSearchStarted();
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                searchResponse.onDeviceFounded(searchResult);
            }

            @Override
            public void onSearchStopped() {
                searchResponse.onSearchStopped();
            }

            @Override
            public void onSearchCanceled() {
                searchResponse.onSearchCanceled();
            }
        });

    }


    /**
     * 停止搜索
     */
    public void stopScan(){
        if(bluetoothClient != null){
            bluetoothClient.stopSearch();
        }
    }




    //根据Mac地址连接蓝牙设备
    public void connBleDeviceByMac(String mac,String bleName,ConnStatusListener connStatusListener){
        connBleDevice(mac,bleName,connStatusListener);
    }


    private synchronized void connBleDevice(final String bleMac, final String bleName, final ConnStatusListener connectResponse){
        //BleSpUtils.put(context,"ble_mac",bleMac);
        Log.e(TAG,"-------w37ble--mac="+bleMac+"--=name="+bleName);
        bluetoothClient.registerConnectStatusListener(bleMac,connectStatusListener);
        BleConnectOptions options = (new com.inuker.bluetooth.library.connect.options.BleConnectOptions.Builder()).setConnectRetry(3).setConnectTimeout(30000).setServiceDiscoverRetry(3).setServiceDiscoverTimeout(20000).build();
        bluetoothClient.connect(bleMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(final int code, final BleGattProfile data) {

                if(code == 0){  //连接成功了，开始设置通知
                    //判断是否是OTA升级状态，是OTA状态不保存地址
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            if(!W30SBleUtils.isOtaConn){
                                BleSpUtils.put(mContext,SAVE_BLE_MAC_KEY,bleMac);
                                MyCommandManager.DEVICENAME = bleName;
                                Log.e(TAG,"----------需要保存的mac="+bleName+"--bleMac="+bleMac);
                                MyApp.getInstance().setMacAddress(bleMac);
                                SharedPreferencesUtils.saveObject(mContext,Commont.BLEMAC,bleMac);
                                SharedPreferencesUtils.saveObject(mContext,Commont.BLENAME,bleName);
                            }
                            if(bleName.equals("XWatch")){
                                setNotiData(bleMac, X_WATCH_UUID_SYSTEM_SERVICED,X_WATCH_UUID_SYSTEM_NOTI ,connectResponse);
                            }
                            else if(bleName.equals("SWatch")){
                                setNotiData(bleMac,S_WATCH_UUID_SYSTEM_SERVICE,S_WATCH_UUID_SYSTEM_NOTI,connectResponse);
                            }else if(bleName.equals("L890")){
                                setNotiData(bleMac,B11_SYSTEM_SERVER_UUID,B11_CONTROL_UUID,connectResponse);
                            }
                            else{
                                setNotiData(bleMac, UUID_SYSTEM_SERVICE, UUID_SYSTEM_READ,connectResponse);
                            }

                        }
                    }, 2000L);
                    connectResponse.connStatus(code);
                }
            }
        });

    }


    //断开连接
    public void disBleDeviceByMac(String mac){
        bluetoothClient.stopSearch();
        if(bluetoothClient != null)
            bluetoothClient.disconnect(mac);
        SharedPreferencesUtils.saveObject(mContext,Commont.BLEMAC,"");
        SharedPreferencesUtils.saveObject(mContext,Commont.BLENAME,"");
    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:
                    handler.removeMessages(0x01);
                    findPhone();
                    break;
                case 0x05:
                    handler.removeMessages(0x05);
                    //W37DataAnalysis.getW37DataAnalysis().disCallPhone();
                    break;
            }

        }
    };




    //设置通知
    private synchronized void setNotiData(String bleMac, UUID serUUID, UUID notiUUID, final ConnStatusListener connStatusListener){
        bluetoothClient.notify(bleMac, serUUID, notiUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, final byte[] value) {
                 Log.e(TAG,"---111-----设置通知="+Arrays.toString(value));
                if(interfaceManager.writeBackDataListener != null){
                    interfaceManager.writeBackDataListener.backWriteData(value);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (value.length > 10 && (value[10] == 6)) {    //舟海拍照的指令
                            Log.e(TAG, "----111---收到手环返回-----");
                            Intent intent = new Intent();
                            intent.setAction(W37_CAMERA_TAKE_PHOTO);
                            mContext.sendBroadcast(intent);
                        }

                       if( value.length > 10 && (value[10] == 7)){   //舟海找手机的指令
                            Log.e(TAG, "----22---查找手机返回-----");
                            handler.sendEmptyMessage(0x01);
                        }

                       if(value.length>2 && (value[0] == 76 && value[1] == 1)){     //xWatch 拍照指令
                           Intent intent = new Intent();
                           intent.setAction(X_WATCH_TAKE_PHOTO);
                           mContext.sendBroadcast(intent);
                       }

                       if(value.length>4 && (value[0] == -85 && value[3] == 5)){    //舟海系列挂断电话的指令
                           handler.sendEmptyMessage(0x05);
                       }

                        if(value.length>2 && (value[0] == 25 && value[1] == 0)){ //SWatch查找手机的指令
                            handler.sendEmptyMessage(0x01);
                        }

                        if(value.length>2 && (value[0] == 20 && value[1] == 1)){    //SWatch拍照指令
                            Intent intent = new Intent();
                            intent.setAction(X_WATCH_TAKE_PHOTO);
                            mContext.sendBroadcast(intent);
                        }

                    }
                }, 1000L);

            }

            @Override
            public void onResponse(int code) {
                Log.e(TAG,"--------code="+code);
                connStatusListener.setNotiStatus(code);

            }
        });
    }


    //注册广播，舟海手环拍照时使用
    public void registerW37CameraListener(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37_CAMERA_TAKE_PHOTO);
        intentFilter.addAction(W37_CAMERA_DIS_TAKE_PHOTO);
        mContext.registerReceiver(broadcastReceiver,intentFilter);

    }

    //卸载广播
    public void unRegisterW37CameraListener(){
        try {
            if(broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //写入数据
    public void writeBleDataToDevice2(byte[] data,WriteBackDataListener writeBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        Log.e(TAG,"------写入数据="+bleMac+"-----数据="+Arrays.toString(data));
        if(WatchUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,UUID_SYSTEM_SERVICE,UUID_SYSTEM_WRITE,data,bleWriteResponse);
    }



    //写入B11体温手环数据
    public synchronized void writeB11BleDataToDevice(byte[] data,WriteBackDataListener writeBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        Log.e(TAG,"------写入数据="+bleMac+"-----数据="+Arrays.toString(data));
        if(WatchUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,B11_SYSTEM_SERVER_UUID,B11_SYSTEM_WRITE_UUID,data,bleWriteResponse);
    }




    //写入数据 舟海系列手环
    public synchronized void writeBleDataToDevice(byte[] data,WriteBackDataListener writeBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        Log.e(TAG,"------写入数据="+bleMac+"-----数据="+Arrays.toString(data));
        if(WatchUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,UUID_SYSTEM_SERVICE,UUID_SYSTEM_WRITE,data,bleWriteResponse);
    }



    //写入数据 XWatch
    public synchronized void writeBleDataToDeviceForXWatch(byte[] data,WriteBackDataListener writeBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        Log.e(TAG,"------写入数据="+bleMac+"-----数据="+Arrays.toString(data));
        if(WatchUtils.isEmpty(bleMac))
            return;
        String bName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.BLENAME);
        if(WatchUtils.isEmpty(bName))
            return;
        Log.e(TAG,"------写入的马刺的转="+bName);
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        if(bName.equals("SWatch")){
            Log.e(TAG,"------111--=====");
            bluetoothClient.writeNoRsp(bleMac, S_WATCH_UUID_SYSTEM_SERVICE, S_WATCH_UUID_SYSTEM_WRITE,data,bleWriteResponse);
        }else{
            bluetoothClient.write(bleMac,X_WATCH_UUID_SYSTEM_SERVICED ,X_WATCH_UUID_SYSTEM_WRITE ,data,bleWriteResponse);
        }

    }




    //写入数据，无返回值的
    public synchronized void writeBleDataToDeviceNoBack(byte[] data){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(WatchUtils.isEmpty(bleMac))
            return;
        bluetoothClient.write(bleMac,UUID_SYSTEM_SERVICE,UUID_SYSTEM_WRITE,data,bleWriteResponse);
    }


    /**
     * 注册链接监听
     * @param bleConnStatusListener
     */
    public void registerBleConnstatus(BleConnStatusListener bleConnStatusListener){
        interfaceManager.setBleConnStatusListener(bleConnStatusListener);
    }




    //监听蓝牙连接状态的监听
    private BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if(interfaceManager.bleConnStatusListener != null){
                interfaceManager.bleConnStatusListener.onConnectStatusChanged(mac==null?"mac":mac,status);
            }
        }
    };

    private BleWriteResponse bleWriteResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    //查找手机
    private void findPhone(){
        try {
            mVibrator = (Vibrator) MyApp.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            mMediaPlayer = new MediaPlayer();
            AssetFileDescriptor file = MyApp.getContext().getResources().openRawResourceFd(R.raw.music);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                        file.getLength());
                mMediaPlayer.prepare();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setVolume(0.5f, 0.5f);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
            if (mVibrator.hasVibrator()) {
                //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
