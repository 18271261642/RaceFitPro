package com.example.bozhilun.android.siswatch.bleus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.yanzhenjie.permission.AndPermission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * H8手表连接服务
 */
public class H8BleService extends Service {

    private static final String TAG = "H8BleService";
    public static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public int mConnectionState = STATE_DISCONNECTED;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress ;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private Map<String, BluetoothGattCharacteristic> mWriteCharacteristicMap = new HashMap<>();
    private BluetoothClient bluetoothClient;

    private BluetoothDevice autoPairDevice = null;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"----msg="+msg.what);
            switch (msg.what) {
                case H8BleConstances.SEARCH_REQUEST_CODE:   //搜索到设备了
                    handler.removeMessages(H8BleConstances.SEARCH_REQUEST_CODE);
                    //保存的mac地址
                    String saveMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                    if(WatchUtils.isEmpty(saveMac))
                        return;
                    SearchResult searchResult = (SearchResult) msg.obj;
                    if(searchResult == null)
                        return;
                    Log.e(TAG, "------搜索返回=" + searchResult.device.getAddress());
                    if (searchResult.device.getAddress().equals(saveMac)) {
                        Log.e(TAG, "----相等了----");
                        if (bluetoothClient != null) {
                            bluetoothClient.stopSearch();
                        }

                        autoPairDevice = searchResult.device;

                        //查看是否配对
                        BluetoothDevice pairDevice = HidUtil.getInstance(MyApp.getContext()).getPairDeviceByAddress(mBluetoothAdapter, saveMac);
                        if(pairDevice != null){ //已经配对
                            Log.e(TAG, "----pairDevice=" + pairDevice.getAddress());
                            //查看是否已经连接
                            boolean isPairConn = HidUtil.getInstance(MyApp.getContext()).isConnected(pairDevice);
                            Log.e(TAG,"------是否配对连接状态=="+isPairConn);
                            if(isPairConn){ //已经配对连接
                                connectBleByMac(saveMac);
                            }else{  //只配对 未连接
                                //去配对
                                HidUtil.getInstance(MyApp.getContext()).connect(pairDevice);
                            }
                        }else{  //没有配对
                            HidUtil.getInstance(MyApp.getContext()).pair(searchResult.device);
                        }

                        //connectBleByMac(saveMac);

                    }
                    break;
                case H8BleConstances.STARt_AUTO_CONN_CODE:  //开始自动重连
                    handler.removeMessages(H8BleConstances.STARt_AUTO_CONN_CODE);
                    initialize();
                    autoConnByMac(true);
                    break;
                case H8BleConstances.END_AUTO_CONN_CODE:  //停止自动重连
                    handler.removeMessages(H8BleConstances.END_AUTO_CONN_CODE);
                    autoConnByMac(false);
                    break;
                case 111:
                    //同步时间
                    String intentAction;
                    mConnectionState = STATE_CONNECTED;
                    intentAction = H8BleConstances.ACTION_GATT_CONNECTED;
                    broadcastUpdate(intentAction);
                    writeCharacteristic(WatchConstants.syncwatchtime());
                    break;
                case H8BleConstances.SYNC_H8_TIME_CODE:  //同步手表时间
                    handler.removeMessages(H8BleConstances.SYNC_H8_TIME_CODE);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(111);
                        }
                    }, 2 * 1000);


                    break;
                case H8BleConstances.SET_SYSTEM_TYPE_CODE:  //设置系统类型
                    handler.removeMessages(H8BleConstances.SET_SYSTEM_TYPE_CODE);
                    //writeCharacteristic(WatchConstants.getWatchTime());
                     writeCharacteristic(WatchConstants.getWatchTime());

                    break;
                case 888:
                    handler.removeMessages(888);
                    writeCharacteristic(WatchConstants.setDeviceType());
                    break;
                case 666:   //同步时间后返回
                    handler.removeMessages(666);
                    if(h8ConnstateListener != null){
                        h8ConnstateListener.h8ConnSucc();
                    }
                    break;

                case 0x01:
                    writeCharacteristic(WatchConstants.getWatchAlarmSecond);
                    break;
                case 0x02:
                    writeCharacteristic(WatchConstants.getWatchAlarmThird);
                    break;
                case 0x03:  //挂断电话
                    Log.e(TAG,"-----挂断电话----");
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            if(!AndPermission.hasPermissions(MyApp.getContext(), Manifest.permission.ANSWER_PHONE_CALLS))
                                return;
                            TelecomManager tm = (TelecomManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
                            if (tm != null) {
                                @SuppressLint("MissingPermission") boolean success = tm.endCall();
                            }

                        } else {
                            TelephonyManager tm = (TelephonyManager) MyApp.getContext()
                                    .getSystemService(Service.TELEPHONY_SERVICE);
                            PhoneUtils.endPhone(MyApp.getContext(), tm);
                            PhoneUtils.dPhone();
                            PhoneUtils.endCall(MyApp.getContext());
                        }

                    } catch (NoSuchMethodError e) {
                        e.printStackTrace();
                    }
                    break;

                case 0x08:
                    if(parentH8TimeListener != null){
                        parentH8TimeListener.backData(new byte[]{0x00});
                    }
                    break;
            }

        }
    };

    //连接状态
    private H8ConnstateListener h8ConnstateListener;


    //获取手表时间的接口
    private GetH8TimeInterface getH8TimeInterface;
    private ParentH8TimeListener parentH8TimeListener;
    //获取步数的接口
    private GetH8StepsListener getH8StepsListener;
    //获取节电接口
    private GetH8JiedianListener getH8JiedianListener;
    //设置节电的接口
    private SetH8JiedianListener setH8JiedianListener;
    //获取第一个闹钟
    private GetH8FirstAlarmListener getH8FirstAlarmListener;
    //设置闹钟是否成功
    private ShowSetAlarmResultListener showSetAlarmResultListener;

    public void setShowSetAlarmResultListener(ShowSetAlarmResultListener showSetAlarmResultListener) {
        this.showSetAlarmResultListener = showSetAlarmResultListener;
    }

    public void setGetH8FirstAlarmListener(GetH8FirstAlarmListener getH8FirstAlarmListener) {
        this.getH8FirstAlarmListener = getH8FirstAlarmListener;
    }

    public void setSetH8JiedianListener(SetH8JiedianListener setH8JiedianListener) {
        this.setH8JiedianListener = setH8JiedianListener;
    }

    public void setGetH8JiedianListener(GetH8JiedianListener getH8JiedianListener) {
        this.getH8JiedianListener = getH8JiedianListener;
    }

    public void setGetH8StepsListener(GetH8StepsListener getH8StepsListener) {
        this.getH8StepsListener = getH8StepsListener;
    }

    public void setGetH8TimeInterface(GetH8TimeInterface getH8TimeInterface) {
        this.getH8TimeInterface = getH8TimeInterface;

    }

    public void setParentH8TimeListener(ParentH8TimeListener parentH8TimeListener) {
        this.parentH8TimeListener = parentH8TimeListener;
    }

    public void setH8ConnstateListener(H8ConnstateListener h8ConnstateListener) {
        this.h8ConnstateListener = h8ConnstateListener;
    }

    /**
     * 自动连接
     * 1，搜索设备
     * 2，连接
     * @param isAuto
     */
    public void autoConnByMac(boolean isAuto) {
        if (isAuto) {
            //先获取配对的设备，查看是否已经配对连接了
            //保存的mac地址
            String saveMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
            if(mBluetoothAdapter == null)
                return;
            //获取配对的设备
            BluetoothDevice pairDevice = HidUtil.getInstance(MyApp.getContext()).getPairDeviceByAddress(mBluetoothAdapter, saveMac);
            if(pairDevice == null){
                searchBleDevice();
                return;
            }
            //查看是否已经连接
            boolean isPairConn = HidUtil.getInstance(MyApp.getContext()).isConnected(pairDevice);
            Log.e(TAG,"----isPairConn="+isPairConn);
            if(isPairConn){ //如果已经连接了，就直接连接
                connectBleByMac(saveMac);
            }else{
                HidUtil.getInstance(MyApp.getContext()).connect(pairDevice);
            }
            //searchBleDevice();

        } else {
            if (bluetoothClient != null)
                bluetoothClient.stopSearch();
        }

    }

    //搜索设备
    private void searchBleDevice(){
        //搜索蓝牙设备
        SearchRequest request = (new SearchRequest.Builder()).searchBluetoothLeDevice(Integer.MAX_VALUE, 1).build();
        bluetoothClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                if(searchResult != null){
                    Message message = handler.obtainMessage();
                    message.what = H8BleConstances.SEARCH_REQUEST_CODE;
                    message.obj = searchResult;
                    handler.sendMessage(message);
                }

            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }


    /**
     * 所有操作的回调
     */
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        /**
         * 连接状态改变
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG, "-----onConnectionStateChange-status=" + status + "---newState=" + newState);
            String intentAction;
            switch (newState) {
                case  BluetoothGatt.STATE_CONNECTED:    //连接成功
                    MyCommandManager.DEVICENAME = "bozlun";
                    if (bluetoothClient != null)
                        bluetoothClient.stopSearch();
                    Log.e(TAG, "-------连接成功-------");
//                    mConnectionState = STATE_CONNECTED;
//                    intentAction = H8BleConstances.ACTION_GATT_CONNECTED;
//                    broadcastUpdate(intentAction);
                    //开始发现服务
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //发现服务
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.discoverServices();
                    }

                    break;
                case BluetoothGatt.STATE_CONNECTING:    //正在连接
                    Log.e(TAG, "-------正在连接-------");
                    mConnectionState = BluetoothGatt.STATE_CONNECTING;
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:  //断开连接
                    MyCommandManager.DEVICENAME = null;
                    Log.e(TAG, "-------断开连接-------");
                    if (mBluetoothGatt != null) {
                        //mBluetoothGatt.disconnect();
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                    }
                    intentAction = H8BleConstances.ACTION_GATT_DISCONNECTED;
                    broadcastUpdate(intentAction);


                    //保存的mac地址
                    String saveMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                    //判断是手动断开还是非手动断开
                    if (!WatchUtils.isEmpty(saveMac)) {
                        Log.e(TAG, "------非手动断开----mac=" + saveMac);
                        //非手动断开，去重连
                        autoConnByMac( true);
                    }
                    handler.sendEmptyMessage(0x08);
                    break;

            }

        }

        //发现服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (gatt != null && status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "-----发现服务了---status=" + status+"--gatt="+gatt.getService(UUID.fromString(H8BleConstances.BZLUN_IKP_SERVER_UUID)));
                //设置通知
               // broadcastUpdate(H8BleConstances.ACTION_GATT_SERVICES_DISCOVERED);

                startNotification(gatt.getService(UUID.fromString(H8BleConstances.BZLUN_IKP_SERVER_UUID)));
            }

        }

        //读取特征
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e(TAG, "------onCharacteristicRead--=" + characteristic.getUuid() + "---status=" + status);
            broadcastUpdate(H8BleConstances.ACTION_DATA_AVAILABLE, characteristic,"read");
        }

        //写特征
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "----onCharacteristicWrite--=" + characteristic.getUuid());
        }

        //返回数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e(TAG, "-----返回数据=" + Arrays.toString(characteristic.getValue()));
            broadcastUpdate(H8BleConstances.ACTION_DATA_AVAILABLE, characteristic,"changed");

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };



    //连接
    public void connBle(BluetoothDevice bDvice){
        this.autoPairDevice = bDvice;
        //先判断是否绑定,绑定即配对
        if (bDvice.getBondState() == H8BleConstances.H8_BLE_BANDSTATE_CODE){
            //已经配对，判断是否连接
            if(HidUtil.getInstance(MyApp.getContext()).isConnected(bDvice)){
                //已经连接了
                connectBleByMac(bDvice.getAddress());
            }else{
                //已经配对，但未连接  去连接
                HidUtil.getInstance(MyApp.getContext()).connect(bDvice);
            }
        }else{
            //没有配对 去配对
            HidUtil.getInstance(MyApp.getContext()).pair(bDvice);
        }
    }



    //连接
    public boolean connectBleByMac(String address) {
        Log.e(TAG, "----connect----" + address);
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {

            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt != null && mBluetoothGatt.connect()) {
                Log.e(TAG,"--------连接了conn-----");
                mConnectionState = STATE_CONNECTING;
                return true;
            }
            else {
                return false;
            }

        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (mBluetoothGatt != null) {
           // mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback);
        Log.e(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        Log.e(TAG, "device.getBondState==" + device.getBondState());
        return true;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt = null;
    }


    /**
     * 初始化bleadapter
     *
     * @return
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    //设置通知
    private void displayGattServices(BluetoothGattService supportedGattServices) {
        Log.e(TAG,"------------tag-------");
        //BluetoothGattCharacteristic characteristic = null;
        //获取特征值
        if (supportedGattServices != null) {
            //获取写的characteristic
            List<BluetoothGattCharacteristic> gattCharacteristics = supportedGattServices.getCharacteristics();
            if (gattCharacteristics != null) {
                for (BluetoothGattCharacteristic bc : gattCharacteristics) {

                    Log.e(TAG, "---------bc值=" + bc.getUuid().toString());
                    if (bc.getUuid().toString().equals(H8BleConstances.BZLUN_IKP_WRITE_UUID)) {   //获取写的特征并保存
                        mWriteCharacteristicMap.clear();
                        mWriteCharacteristicMap.put(mBluetoothDeviceAddress, bc);

                    }
                }
                //通知的特征 getCharacter(H8BleConstances.BZLUN_IKP_SERVER_UUID,H8BleConstances.BZLUN_IKP_READ_UUID);//
                BluetoothGattCharacteristic characteristic =  gattCharacteristics.get(0);

                Log.e(TAG, "---characteristic-----" + characteristic.getUuid().toString());
                int charaProp = characteristic.getProperties();
                Log.e(TAG, "-----charaProp--=" + charaProp+"----="+(charaProp | BluetoothGattCharacteristic.PROPERTY_READ));
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    Log.e(TAG, "-----11---read=");
                    if (mNotifyCharacteristic != null) {
                        Log.e(TAG, "-----11-1--read=" + mNotifyCharacteristic.getUuid().toString());
                        setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    //readCharacteristic(H8BleConstances.BZLUN_IKP_SERVER_UUID, H8BleConstances.BZLUN_IKP_READ_UUID);
                    //Log.e(TAG, "-----11---readChart=" + characteristic.getUuid().toString());
                    //readCharacteristic(characteristic);
                    handler.sendEmptyMessage(H8BleConstances.SYNC_H8_TIME_CODE);
                }

                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    Log.e(TAG, "----22-----通知的特征=" + mNotifyCharacteristic.getUuid().toString());
                    setCharacteristicNotification(characteristic, true);
                }

            }
        }
    }


    //开启通知
    private void startNotification(BluetoothGattService supportedGattServices) {
        //获取写的characteristic
        List<BluetoothGattCharacteristic> gattCharacteristics = supportedGattServices.getCharacteristics();
        for(BluetoothGattCharacteristic bcs : gattCharacteristics){
            if (bcs.getUuid().toString().equals(H8BleConstances.BZLUN_IKP_WRITE_UUID)) {   //获取写的特征并保存
                mWriteCharacteristicMap.clear();
                mWriteCharacteristicMap.put(mBluetoothDeviceAddress, bcs);

            }
            if(bcs.getUuid().toString().equals(H8BleConstances.BZLUN_IKP_READ_UUID)){
                int charaProp = bcs.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    Log.e(TAG, "-----11-11--read=");
                    setCharacteristicNotification(bcs, false);
                   // readCharacteristic(bcs);


                }

                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                mNotifyCharacteristic = bcs;
                    Log.e(TAG, "----22111-----通知的特征=" + bcs.getUuid().toString());
                    setCharacteristicNotification(bcs, true);
                    handler.sendEmptyMessage(H8BleConstances.SYNC_H8_TIME_CODE);
                }
            }

        }


    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    //广播数据
    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic,String tag) {
        Log.e(TAG,"------Tag="+tag);
        byte[] bleData = characteristic.getValue();
        Log.e(TAG, "------broadcastUpdate=" + Arrays.toString(bleData));
        for(int i = 0;i<bleData.length;i++){
            //Log.e(TAG,"-------bleData="+bleData[i]+"--="+Customdata.byteToHex(bleData[i]));
        }

        if(bleData.length<=0)
            return;

        switch (bleData[2]){
            case 68:    //同步手表时间返回
                handler.sendEmptyMessage(H8BleConstances.SET_SYSTEM_TYPE_CODE);
                break;
            case 17:    //获取步数返回
                if(getH8StepsListener != null && bleData.length>10){
                    Map<String,Integer> steMap = new HashMap<>();
                    steMap.put("today", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[6])+ Customdata.byteToHex(bleData[5])));  //今天的数据
                    steMap.put("yestoday", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[8])+ Customdata.byteToHex(bleData[7]))); //昨天
                    steMap.put("qiantian", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[10])+ Customdata.byteToHex(bleData[9])));   //前天
                    steMap.put("fourthDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[12])+ Customdata.byteToHex(bleData[11]))); //大前天
                    steMap.put("fiveDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[14])+ Customdata.byteToHex(bleData[13])));   //前4天
                    steMap.put("sixthDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[16])+ Customdata.byteToHex(bleData[15])));  //前5天
                    steMap.put("seventhDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(bleData[18])+ Customdata.byteToHex(bleData[17])));    //前6天
                    getH8StepsListener.getH8OnWeekSteps(steMap);
                    handler.sendEmptyMessage(888);
                }
                break;
            case 102:   //节电时间返回
                if(getH8JiedianListener != null){
                    getH8JiedianListener.getJiedianTime(bleData);
                }

                break;
            case 107:   //设置节电时间返回
                if(setH8JiedianListener != null)
                    setH8JiedianListener.setH8JiedianTime(bleData);
                break;
            case 119:   //获取第一个闹钟返回
                if(getH8FirstAlarmListener != null)
                    getH8FirstAlarmListener.getFirstAlarm(bleData);
                handler.sendEmptyMessage(0x01); //获取第二个闹钟
                break;
            case 120:   //获取第二个闹钟返回
                if(getH8FirstAlarmListener != null)
                    getH8FirstAlarmListener.getSecondAlarm(bleData);
                handler.sendEmptyMessage(0x02);
                break;
            case 121:   //获取第三个闹钟返回
                if(getH8FirstAlarmListener != null)
                    getH8FirstAlarmListener.getThirdAlarm(bleData);
                break;
            case 96:    //设置闹钟返回
                if(showSetAlarmResultListener != null){
                    showSetAlarmResultListener.alarmResultData(true);
                }
                break;
            case 48:    //获取手表时间返回
                handler.sendEmptyMessage(666);
                if(getH8TimeInterface != null){

                    getH8TimeInterface.getH8TimeData(bleData);
                }
                break;
        }

        //挂断电话的指令返回
        if(bleData.length >4 && bleData[4] == -127){
            handler.sendEmptyMessage(0x03);
        }


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

    //读取数据的函数
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, "---读取特征=" + characteristic.getUuid().toString());
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 检索连接设备上受支持GATT服务的列表。
     *
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getServices();
    }

    //获取所有支持的服务
    public BluetoothGattService getSupportedGattServices(UUID uuid) {
        BluetoothGattService mBluetoothGattService;
        if (mBluetoothGatt == null) return null;
        mBluetoothGattService = mBluetoothGatt.getService(uuid);
        return mBluetoothGattService;
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
     * 启用或禁用对给定特性的通知
     *
     * @param characteristic
     * @param enabled
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        Log.e(TAG, "-----设置通知了=" + characteristic.getUuid() + "---enable=" + enabled);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
//        if (descriptor == null) return;
//        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        mBluetoothGatt.writeDescriptor(descriptor);

        //0x44 0x00
//        if (H8BleConstances.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }

//        if (H8BleConstances.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString("F0080003-0451-4000-B000-000000000000"));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }


        boolean isEnableNotification = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        if(isEnableNotification) {
            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
            if(descriptorList != null && descriptorList.size() > 0) {
                for(BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }

    }

    //写入指定的characteristic
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isWriteTrue = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.e(TAG, "-----isWriteTrue=" + isWriteTrue);
        if(!isWriteTrue)
            mBluetoothGatt.writeCharacteristic(characteristic);
    }


    //写入指定的characteristic
    public void writeCharacteristic(byte[] writedata) {
        Log.e(TAG,"-----写入的带数据="+Arrays.toString(writedata));
        if (mBluetoothAdapter == null || mBluetoothGatt == null || mBluetoothDeviceAddress == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //BluetoothGattCharacteristic bluetoothGattCharacteristic = getWriteCharacteristic(mBluetoothDeviceAddress);
//        BluetoothGattCharacteristic bluetoothGattCharacteristic = getCharacter(H8BleConstances.BZLUN_IKP_SERVER_UUID, H8BleConstances.BZLUN_IKP_WRITE_UUID);
//        if(bluetoothGattCharacteristic == null)
//            return;
//        boolean isWrite = getWriteCharacteristic(mBluetoothDeviceAddress).setValue(writedata);
////        BluetoothGattCharacteristic bbc = getWriteCharacteristic(mBluetoothDeviceAddress);
////        Log.e(TAG, "-----bbc=" + bbc.getUuid());
//        //boolean wri = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//        bluetoothGattCharacteristic.setValue(writedata);
//        writeCharacteristic(bluetoothGattCharacteristic);

        BluetoothGattCharacteristic bluetoothGattCharacteristic = getWriteCharacteristic(mBluetoothDeviceAddress);
        if(bluetoothGattCharacteristic == null)
            return;
        boolean isWrite = bluetoothGattCharacteristic.setValue(writedata);
        Log.e(TAG, "-----bbc=" +isWrite);
        if(!isWrite){
            bluetoothGattCharacteristic.setValue(writedata);
        }
       writeCharacteristic(bluetoothGattCharacteristic);
    }

    /**
     * 获取可写特征对象
     *
     * @param address 蓝牙地址
     * @return 可写特征对象
     */
    public BluetoothGattCharacteristic getWriteCharacteristic(String address) {

        if (mWriteCharacteristicMap != null) {
            return mWriteCharacteristicMap.get(address);
        }
        return null;

    }

    //写数据
    public void writeCharacteristicData(byte[] writeData) {
        Log.e(TAG, "----写入的数据=" + Arrays.toString(writeData));
        try {
            BluetoothGattCharacteristic wreteChart = getCharacter(H8BleConstances.BZLUN_IKP_SERVER_UUID, H8BleConstances.BZLUN_IKP_WRITE_UUID);
            Log.e(TAG, "-----wreiteChart=" + wreteChart.getUuid());
            if (wreteChart == null)
                return;
            boolean writeValue = wreteChart.setValue(writeData);
            Log.e(TAG, "-----writeValue=" + writeValue);
            writeCharacteristic(wreteChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 广播
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "-----action=" + action);
            if (action == null)
                return;
            //发现服务
            if (action.equals(H8BleConstances.ACTION_GATT_SERVICES_DISCOVERED)) {
//                startNotification();
               // startNotification(H8BleConstances.BZLUN_IKP_SERVER_UUID, H8BleConstances.BZLUN_IKP_SERVER_UUID);
                //displayGattServices(getSupportedGattServices(UUID.fromString(H8BleConstances.BZLUN_IKP_SERVER_UUID)));

                //startNotification(getSupportedGattServices(UUID.fromString(H8BleConstances.BZLUN_IKP_SERVER_UUID)));

            }
            //蓝牙打开或者关闭
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                String bN = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.BLENAME);
                Log.e(TAG,"------bleState="+bleState);
                switch (bleState) {
                    case BluetoothAdapter.STATE_TURNING_ON: //蓝牙打开 11
                        if(!WatchUtils.isEmpty(bN) && bN.equals("bozlun")){
                            if(mConnectionState != BluetoothGatt.STATE_CONNECTING){
                                handler.sendEmptyMessage(H8BleConstances.STARt_AUTO_CONN_CODE);

                            }
                        }

                        break;
                    case BluetoothAdapter.STATE_ON:
                        if(!WatchUtils.isEmpty(bN) && bN.equals("bozlun")){
                            if(mConnectionState != BluetoothGatt.STATE_CONNECTING){
                                handler.sendEmptyMessage(H8BleConstances.STARt_AUTO_CONN_CODE);

                            }
                        }
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:    //蓝牙关闭 13
                        handler.sendEmptyMessage(H8BleConstances.END_AUTO_CONN_CODE);
                        break;
                }
            }


            //配对状态
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {   //绑定状态的广播，配对
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, "------配对状态返回----" + bondState);
                switch (bondState) {
                    case BluetoothDevice.BOND_BONDED:   //已绑定 12
//                            Log.e(TAG, "-----111-----");
                        if (autoPairDevice != null) {
                            if (bd != null && bd.getName().equals(autoPairDevice.getName())) {
//                                    Log.e(TAG, "-----22-----");

                                HidUtil.getInstance(MyApp.getContext()).connect(autoPairDevice);
                            }
                        }
                        break;
                    case BluetoothDevice.BOND_BONDING:  //绑定中   11
                        if (autoPairDevice != null) {
                            if (bd != null && bd.getName()!= null && bd.getName().equals(autoPairDevice.getName())) {
                                    Log.e(TAG, "-----22-----");

                            }
                        }
                        break;
                    case BluetoothDevice.BOND_NONE: //绑定失败  10
                        if (autoPairDevice != null && autoPairDevice.getName() != null) {
                            if (bd != null && bd.getName() != null && bd.getName().equals(autoPairDevice.getName())) {

                                ToastUtil.showToast(MyApp.getContext(), "绑定失败,请重新绑定");
                            }
                        }
                        break;

                }
            }



            //H8手表的配对连接成功
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) { //连接状态的改变
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED);
                BluetoothDevice conBle = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String saveMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.BLEMAC);
                //0-1;0-2 3;0
               // Log.e(TAG, "-----连接状态----" + state + "--" + connState + "----conBle=" + conBle.getName());
                if(conBle == null)
                    return;
                if(conBle.getName() == null)
                    return;
                if(!conBle.getName().contains("H8") || !conBle.getName().contains("bozlun"))
                    return;
                switch (connState){
                    case BluetoothAdapter.STATE_DISCONNECTED: //断开连接成功 0
                        if(!WatchUtils.isEmpty(saveMac))
                            autoConnByMac(true);
                        break;
                    case BluetoothAdapter.STATE_CONNECTING://正在连接 1

                        break;
                    case BluetoothAdapter.STATE_CONNECTED: //连接成功 2
                        if (!WatchUtils.isEmpty(saveMac) && conBle.getAddress().equals(saveMac)) { //连接成功
                            connectBleByMac(saveMac);

                        }
                        break;
                }


            }


        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        regisBroadCast();
        bluetoothClient = new BluetoothClient(MyApp.getContext());
    }

    //注册广播
    private void regisBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(H8BleConstances.ACTION_GATT_CONNECTED);
        intentFilter.addAction(H8BleConstances.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(H8BleConstances.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(H8BleConstances.ACTION_DATA_AVAILABLE);
        //蓝牙打开或者关闭的action
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        //H8手表的配对和连接
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private final IBinder mBinder = new H8LocalBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    class H8LocalBinder extends Binder {
        H8BleService getH8Service() {
            return H8BleService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
