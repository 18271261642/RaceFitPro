package com.example.bozhilun.android.w30s.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchSyncSuccListener;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Admin
 * Date 2019/7/4
 */
public class W37ConnStatusService extends Service {

    private static final String TAG = "W37ConnStatusService";

    private W37LoadBuilder w37LoadBuilder = new W37LoadBuilder();

    private InterfaceManager interfaceManager = new InterfaceManager();

    private String saveLocalBleMac = null;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:  //搜索设备
                    if(saveLocalBleMac == null)
                        saveLocalBleMac = (String) SharedPreferencesUtils.readObject(W37ConnStatusService.this,Commont.BLEMAC);
                    if(WatchUtils.isEmpty(saveLocalBleMac))
                        return;
                    SearchResult searchResult = (SearchResult) msg.obj;
                    if(searchResult == null)
                        return;
                    if(searchResult.getAddress() == null || searchResult.getName() == null)
                        return;
                    if(!WatchUtils.isEmpty(searchResult.getAddress().trim()) && searchResult.getAddress().trim().equals(saveLocalBleMac)){
                        Log.e(TAG,"------w37相等了-----");
                        MyApp.getInstance().getW37BleOperateManager().stopScan();
                        connW37ByMac(searchResult.getAddress().trim(),searchResult.getName().equals("XWatch")
                                ? "XWatch" : (searchResult.getName().equals("SWatch")
                                ? "SWatch":searchResult.getName().substring(0,3).trim()));  //开始连接
                    }
                    break;
                case 0x02:  //断开了连接，判断是否需要自动重连
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    MyCommandManager.DEVICENAME = null;
                    handler.removeMessages(0x02);
                    if(W30SBleUtils.isOtaConn)
                        return;
                    String saveBleMac = (String) SharedPreferencesUtils.readObject(getApplicationContext(),Commont.BLEMAC);
                    if(WatchUtils.isEmpty(saveBleMac))
                        return;
                    //开始自动重连
                    w37AutoBleDevice();
                    break;

            }
        }
    };




    @Override
    public IBinder onBind(Intent intent) {
        return w37LoadBuilder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        saveLocalBleMac = (String) SharedPreferencesUtils.readObject(W37ConnStatusService.this,Commont.BLEMAC);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37Constance.W37_CONNECTED_ACTION);
        intentFilter.addAction(W37Constance.W37_DISCONNECTED_ACTION);

        //xwatch
        intentFilter.addAction(W37Constance.X_WATCH_CONNECTED_ACTION);
        intentFilter.addAction(W37Constance.W37_DISCONNECTED_ACTION);

        //SWatch
        intentFilter.addAction(W37Constance.S_WATCH_DISCONN_ACTION);
        intentFilter.addAction(W37Constance.S_WATCH_CONNECTED_ACTION);

        //蓝牙打开或关闭
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);


    }


    /**
     * 连接设备
     * @param mac
     */
    public void connBleForSearch(final String mac, final String bleName){
        MyApp.getInstance().getW37BleOperateManager().registerBleConnstatus(bleConnStatusListener);
        MyApp.getInstance().getW37BleOperateManager().connBleDeviceByMac(mac,bleName, new ConnStatusListener() {
            @Override
            public void connStatus(int status) {

            }

            @Override
            public void setNotiStatus(int code) {
                MyCommandManager.DEVICENAME = bleName;
                MyCommandManager.ADDRESS = mac;
                if(bleName.equals("XWatch") || bleName.equals("SWatch")){
                    xWatchSyncTime(mac,bleName);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("bleName",bleName);
                    intent.setAction(W37Constance.W37_CONNECTED_ACTION);
                    sendBroadcast(intent);
                }

                ActiveManage.getActiveManage().updateUserMac(W37ConnStatusService.this,bleName.equals("XWatch")?"XWatch":bleName,mac);
            }
        });
    }




    /**
     * 连接设备，用于自动连接使用
     * @param mac
     */
    public void connW37ByMac(final String mac, final String bleName){
        MyApp.getInstance().getW37BleOperateManager().registerBleConnstatus(bleConnStatusListener);
        MyApp.getInstance().getW37BleOperateManager().connBleDeviceByMac(mac, bleName,new ConnStatusListener() {
            @Override
            public void connStatus(int status) {    //连接成功

            }

            @Override
            public void setNotiStatus(int code) {   //设置通知成功
                MyCommandManager.DEVICENAME = bleName;
                MyCommandManager.ADDRESS = mac;

                if(bleName.equals("XWatch") || bleName.equals("SWatch")){
                    xWatchSyncTime(mac,bleName);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("bleName",mac);
                    intent.setAction(W37Constance.W37_CONNECTED_ACTION);
                    sendBroadcast(intent);
                }
            }
        });
    }



    private void xWatchSyncTime(final String mac, final String bleName){
        XWatchBleAnalysis.getW37DataAnalysis().syncWatchTime(new XWatchSyncSuccListener() {
            @Override
            public void bleSyncComplete(byte[] data) {
                Intent intent = new Intent();
                intent.putExtra("bleName",bleName);
                intent.setAction(W37Constance.X_WATCH_CONNECTED_ACTION);
                sendBroadcast(intent);
            }
        });
    }


    /**
     * 用于自动连接，根据本地保存的Mac地址搜索
     */
    public void w37AutoBleDevice(){
        MyApp.getInstance().getW37BleOperateManager().startScanBleDevice(new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                Message message = handler.obtainMessage();
                message.what = 0x01;
                message.obj = searchResult;
                handler.sendMessage(message);
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        }, Integer.MAX_VALUE,2);
    }


    /**
     * 监听蓝牙连接状态
     */
    private BleConnStatusListener bleConnStatusListener = new BleConnStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            Log.e(TAG,"-----连接状态="+status);
            switch (status){
                case 16:    //连接成功
                    MyApp.getInstance().getW37BleOperateManager().stopScan();
                    break;
                case 32:    //连接断开
                    MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                    MyCommandManager.DEVICENAME = null;
                    Intent intent = new Intent();
                    intent.setAction(W37Constance.W37_DISCONNECTED_ACTION);
                    intent.setAction(W37Constance.X_WATCH_DISCONN_ACTION);
                    sendBroadcast(intent);
                    handler.sendEmptyMessage(0x02);
                    break;
            }
        }
    };



    public class W37LoadBuilder extends Binder{
        public W37ConnStatusService getW37ConSerevice(){
            return W37ConnStatusService.this;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.e(TAG, "------bleState=" + bleState);
                String b30Name = (String) SharedPreferencesUtils.readObject(MyApp.getInstance().getApplicationContext(), Commont.BLENAME);
                String bMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);

                Log.e(TAG,"---------w37==="+b30Name+"--mac="+bMac);

                if (WatchUtils.isEmpty(bMac) || WatchUtils.isEmpty(b30Name))
                    return;
                if (!WatchUtils.isZhouHaiDevice(b30Name))
                    return;
                switch (bleState) {
                    case BluetoothAdapter.STATE_TURNING_ON: //蓝牙打开 11
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isZhouHaiDevice(b30Name)) {
                            handler.sendEmptyMessage(0x02);
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isZhouHaiDevice(b30Name)) {
                            handler.sendEmptyMessage(0x02);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:    //蓝牙关闭 13
                        if (!WatchUtils.isEmpty(b30Name) && WatchUtils.isZhouHaiDevice(b30Name)) {
                            //handler.sendEmptyMessage(H8BleConstances.END_AUTO_CONN_CODE);
                            //handler.sendEmptyMessage(AUTO_CONN_REQUEST_CODE);
                            MyCommandManager.ADDRESS = null;// 断开连接了就设置为null
                            MyCommandManager.DEVICENAME = null;
                        }

                        break;
                }
            }
        }
    };
}
