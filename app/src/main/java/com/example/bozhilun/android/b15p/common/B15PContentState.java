package com.example.bozhilun.android.b15p.common;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b15p.interfaces.ConntentStuteListenter;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;

public class B15PContentState {
    public String TAG = "B15PContentState";

    //将同步内容下方到if内部，提高了执行的效率，不必每次获取对象时都进行同步，只有第一次才同步，创建了以后就没必要了。
    private static volatile B15PContentState instance = null;
    public static int heartRe = 0;//累计心率刷新次数，两次之后还为空那么就模拟数据
    private ConntentStuteListenter b15PContentState;//对外链接状态的接口
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private boolean isContentioning = false;//是否在连接中
    private boolean isManualDis = false;//是否是手动断开设备


    public void setB15PContentState(ConntentStuteListenter b15PContentState) {
        this.b15PContentState = b15PContentState;
    }

    /**
     * 设置是否是手动断开
     * @param manualDis
     */
    public void setManualDis(boolean manualDis) {
        isManualDis = manualDis;
    }

    /**
     * 双锁单利
     *
     * @return
     */
    public static B15PContentState getInstance() {
        if (instance == null) {
            synchronized (B15PContentState.class) {
                if (instance == null) {
                    instance = new B15PContentState();
                }
            }
        }
        return instance;
    }

    private B15PContentState() {
        isNull();
    }

    void isNull() {
        if (bluetoothManager == null) bluetoothManager =
                (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    /**
     * 判断返回设备是否链接
     */
    public void bleIsContent() {
        //0 :未连接  1:正在连接  2 :已连接
        //正在连接
        if (L4M.Get_Connect_flag() != 2) {
            if (Commont.isDebug)Log.e(TAG, "   设备没连接？  " + L4M.Get_Connect_flag());
            MyCommandManager.DEVICENAME = "";
            //未连接状态
            if (L4M.Get_Connect_flag() == 0) {
                //扫描链接蓝牙
                bleSeachDevices();
            } else {//连接中
                //只要没链接成功，就一直扫描链接状态
                isConnect();
            }
        } else {
            isContentioning = false;
            MyCommandManager.DEVICENAME = (WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName());
        }
    }


    /**
     * 链接设备
     */
    private void bleContentdev(final BluetoothDevice bluetoothDevice) {
        Dev.Try_Connect(bluetoothDevice, new Dev.ConnectReslutCB() {
            @Override
            public void OnConnectReslut(boolean CntExists, BluetoothDevice inBluetoothDevice) {
                if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter == null)
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.stopLeScan(leScanCallback);
                if (handler != null) if (handler.hasMessages(0x01))handler.removeMessages(0x01);//取消延时的问题
                if (Commont.isDebug)Log.e(TAG, " B15P  =OnConnectReslut=" + CntExists + "===" + inBluetoothDevice.toString());
                //如果已经有设备 先断开再连接
                if (CntExists) {
                    if (bluetoothAdapter != null)
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    if (!WatchUtils.isEmpty(L4M.GetConnectedMAC())){
                        try {
                            if (L4M.Get_Connect_flag() == 2)  Dev.RemoteDev_CloseManual();
                            Dev.Cache_Connect(bluetoothDevice);
                        }catch (Exception e){e.printStackTrace();}

                    }

                }
            }

            /**
             *
             * @param Event  SameDevice 同一个设备   NewDevice 不同的设备
             * @param ConnectInfo mac
             */
            @Override
            public void OnNewDev(String Event, String ConnectInfo) {

                if (Commont.isDebug)Log.e(TAG, " B15P ==OnNewDev==" + Event + "==" + ConnectInfo);

            }
        });
    }


    /**
     * 扫描设备
     */
    public void bleSeachDevices() {
        //判断是否是手动断开
        if (!isManualDis){
            if (bluetoothManager == null)
                bluetoothManager = (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (Commont.isDebug)Log.d(TAG, "=====开始扫描");
            bluetoothAdapter.startLeScan(leScanCallback);
            handler.sendEmptyMessageDelayed(0x01, 20 * 1000);
        }
    }


    /**
     * 停止扫描设备
     */
    public void stopSeachDevices() {
        if (bluetoothManager == null)
            bluetoothManager = (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Commont.isDebug)Log.d(TAG, "=====停止扫描");
        bluetoothAdapter.stopLeScan(leScanCallback);
        if (handler != null) if (handler.hasMessages(0x01))handler.removeMessages(0x01);//取消延时的问题
    }


    /**
     * 蓝牙扫描回掉
     */
    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (bluetoothDevice != null && !isContentioning) {
                if (L4M.Get_Connect_flag() != 2){
                    if (Commont.isDebug)Log.e(TAG, "===B====" + bluetoothDevice.getAddress() + "      " + bluetoothDevice.getName());
                    String blrMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
                    if (!WatchUtils.isEmpty(blrMac) && bluetoothDevice.getAddress().equals(blrMac)) {
                        if (Commont.isDebug) Log.d(TAG, "=====开始扫描链接");
                        bleContentdev(bluetoothDevice);
                        isContentioning = true;
                    }
                }else {
                    stopSeachDevices();
                }
            }
        }
    };


    public L4M.BTStReceiver getDataReceiver() {
        return DataReceiver;
    }

    private L4M.BTStReceiver DataReceiver = new L4M.BTStReceiver() {
        @Override
        public void OnRec(String InfType, String Info) {
            if (Info.contains("Connecting")) {
                isConnect();
            } else if (Info.contains("BT_BLE_Connected")) {
                MyApp.b15pIsFirstConntent = true;//是第一次链接
                isConnect();
            } else if (Info.contains("close")) {
                isConnect();
            }
        }
    };


    public void isConnect() {

        //正在连接
        if (L4M.Get_Connect_flag() == 1) {
            if (Commont.isDebug)Log.e(TAG, "--B15P--正在链接");
            MyCommandManager.DEVICENAME = null;
            b15PContentState.b15p_Connection_State(1);
        }
        //已连接
        else if (L4M.Get_Connect_flag() == 2) {
            String s = L4M.GetConnectedMAC();
            isContentioning = false;
            SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLEMAC, s);
            if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null)
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.stopLeScan(leScanCallback);
            if (Commont.isDebug)Log.e(TAG, "--B15P--已连接 " + "====" + s + "------" + L4M.GetConnecteddName());
            MyCommandManager.DEVICENAME = (WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName());
            setManualDis(false);//每次连接成功设置为默认，防止自动断开时候不可以扫描连接
            b15PContentState.b15p_Connection_State(2);
        }
        //未连接
        else {
            isContentioning = false;
            if (Commont.isDebug) Log.e(TAG, "--B15P--未连接");
            MyCommandManager.DEVICENAME = null;
            b15PContentState.b15p_Connection_State(0);
        }

//        /**
//         * @param state 0 :未连接  1:正在连接  2 :已连接
//         */
//        b15PContentState.b15p_Connection_State(L4M.Get_Connect_flag());
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    //扫描链接了20秒还没结果
                    handler.removeMessages(0x01);
                    bleSeachDevices();
                    break;
            }
            return false;
        }
    });
//    public String TAG = "B15PContentState";
//
//    //将同步内容下方到if内部，提高了执行的效率，不必每次获取对象时都进行同步，只有第一次才同步，创建了以后就没必要了。
//    private static volatile B15PContentState instance = null;
//    private ConntentStuteListenter b15PContentState;//对外链接状态的接口
//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothManager bluetoothManager;
//    public boolean isContentioning = false;//是否在连接中
//
//
//    public void setB15PContentState(ConntentStuteListenter b15PContentState) {
//        this.b15PContentState = b15PContentState;
//    }
//
//    /**
//     * 双锁单利
//     *
//     * @return
//     */
//    public static B15PContentState getInstance() {
//        if (instance == null) {
//            synchronized (B15PContentState.class) {
//                if (instance == null) {
//                    instance = new B15PContentState();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private B15PContentState() {
//        isNull();
//    }
//
//    void isNull() {
//        if (bluetoothManager == null) bluetoothManager =
//                (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
//        if (bluetoothManager != null) {
//            bluetoothAdapter = bluetoothManager.getAdapter();
//            if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        }
//    }
//
//    /**
//     * 判断返回设备是否链接
//     */
//    public void bleIsContent() {
//        //正在连接
//        if (L4M.Get_Connect_flag() != 2) {
//            MyCommandManager.DEVICENAME = "";
//            //未连接状态
//            if (L4M.Get_Connect_flag() == 0) {
//                //扫描链接蓝牙
//                bleSeachDevices();
//            } else {//连接中
//                //只要没链接成功，就一直扫描链接状态
//                isConnect();
//            }
//        } else {
//            isContentioning = false;
//            MyCommandManager.DEVICENAME = !WatchUtils.isEmpty(L4M.GetConnecteddName()) ?L4M.GetConnecteddName(): "B15P";
//        }
//    }
//
//
//    /**
//     * 链接设备
//     */
//    private void bleContentdev(final BluetoothDevice bluetoothDevice) {
//        Dev.Try_Connect(bluetoothDevice, new Dev.ConnectReslutCB() {
//            @Override
//            public void OnConnectReslut(boolean CntExists, BluetoothDevice inBluetoothDevice) {
//                if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
//                if (bluetoothAdapter == null)
//                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                bluetoothAdapter.stopLeScan(leScanCallback);
//                if (handler != null) handler.removeMessages(0x01);//取消延时的问题
//                Log.e(TAG, " B15P  =OnConnectReslut=" + CntExists + "===" + inBluetoothDevice.toString());
//                //如果已经有设备 先断开再连接
//                if (CntExists) {
//                    if (bluetoothAdapter != null)
//                        bluetoothAdapter.stopLeScan(leScanCallback);
//                    Dev.RemoteDev_CloseManual();
//                    Dev.Cache_Connect(bluetoothDevice);
//                }
//            }
//
//            /**
//             *
//             * @param Event  SameDevice 同一个设备   NewDevice 不同的设备
//             * @param ConnectInfo mac
//             */
//            @Override
//            public void OnNewDev(String Event, String ConnectInfo) {
//
//                Log.e(TAG, " B15P ==OnNewDev==" + Event + "==" + ConnectInfo);
//
//            }
//        });
//    }
//
//
//    /**
//     * 扫描设备
//     */
//    public void bleSeachDevices() {
//        if (bluetoothManager == null)
//            bluetoothManager = (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
//        if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
//        if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        Log.d(TAG, "=====开始扫描");
//        bluetoothAdapter.startLeScan(leScanCallback);
//        handler.sendEmptyMessageDelayed(0x01, 20 * 1000);
//    }
//
//
//    /**
//     * 蓝牙扫描回掉
//     */
//    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//            //Log.e(TAG, "===A====" + bluetoothDevice.getAddress() + "      " + bluetoothDevice.getName());
//            if (bluetoothDevice != null && !isContentioning) {
//                Log.e(TAG, "===B====" + bluetoothDevice.getAddress() + "      " + bluetoothDevice.getName());
//                String blrMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
//                if (!WatchUtils.isEmpty(blrMac) && bluetoothDevice.getAddress().equals(blrMac)) {
//                    Log.d(TAG, "=====开始扫描链接");
//                    bleContentdev(bluetoothDevice);
//                    isContentioning = true;
//                }
//            }
//        }
//    };
//
//
//    public L4M.BTStReceiver getDataReceiver() {
//        return DataReceiver;
//    }
//
//    private L4M.BTStReceiver DataReceiver = new L4M.BTStReceiver() {
//        @Override
//        public void OnRec(String InfType, String Info) {
//            if (Info.contains("Connecting")) {
//                isConnect();
//            } else if (Info.contains("BT_BLE_Connected")) {
//                MyApp.b15pIsFirstConntent = true;//是第一次链接
//                isConnect();
//            } else if (Info.contains("close")) {
//                isConnect();
//            }
//        }
//    };
//
//
//    public void isConnect() {
//
//        //正在连接
//        if (L4M.Get_Connect_flag() == 1) {
//            Log.e(TAG, "--B15P--正在链接");
//            MyCommandManager.DEVICENAME = null;
//            b15PContentState.b15p_Connection_State(1);
//        }
//        //已连接
//        else if (L4M.Get_Connect_flag() == 2) {
//            String s = L4M.GetConnectedMAC();
//            isContentioning = false;
//            SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLEMAC, s);
//            if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
//            if (bluetoothAdapter == null)
//                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            bluetoothAdapter.stopLeScan(leScanCallback);
//            Log.e(TAG, "--B15P--已连接 " + "====" + s + "------" + L4M.GetConnecteddName());
//            MyCommandManager.DEVICENAME = !WatchUtils.isEmpty(L4M.GetConnecteddName()) ?L4M.GetConnecteddName(): "B15P";
//            b15PContentState.b15p_Connection_State(2);
//        }
//        //未连接
//        else {
//            isContentioning = false;
//            Log.e(TAG, "--B15P--未连接");
//            MyCommandManager.DEVICENAME = null;
//            b15PContentState.b15p_Connection_State(0);
//        }
//
////        /**
////         * @param state 0 :未连接  1:正在连接  2 :已连接
////         */
////        b15PContentState.b15p_Connection_State(L4M.Get_Connect_flag());
//    }
//
//
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what) {
//                case 0x01:
//                    //扫描链接了20秒还没结果
//                    handler.removeMessages(0x01);
//                    bleSeachDevices();
//                    break;
//            }
//            return false;
//        }
//    });
}
