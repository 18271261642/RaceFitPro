package com.example.bozhilun.android.w30s.ota;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.ble.W37Constance;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuServiceInitiator;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;


/**
 * W37OTA固件升级
 * Created by Admin
 * Date 2019/5/21
 */
public class NewW37OTAActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "NewW37OTAActivity";
    public static final String UpData = "com.exalpme.bozhilun.android.w30s.ota";
    //下载升级包的保存路径，完整的路径，不能是文件夹的路径
    //private String downLoadSaveUrl = "/storage/emulated/0/Android/data/com.bozlun.bozhilun.android/cache/W37/W37V3.zip";


    private String downLoadSaveUrl = "/storage/emulated/0/Android/data/com.example.bozhilun.android/cache/W37/W37Dfu.zip";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.progressNumberTv)
    TextView progressNumberTv;
    @BindView(R.id.otaProgressBar)
    ProgressBar otaProgressBar;
    @BindView(R.id.up_prooss)
    LinearLayout upProoss;
    @BindView(R.id.otaStartBtn)
    Button otaStartBtn;


    private RequestPressent requestPressent;
    private BluetoothAdapter bluetoothAdapter;
    String deviceAddress = null;

    private boolean isOtaSuccess = false;   //是否升级完成

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:      //扫描到设备了
                    BluetoothDevice bdevice = (BluetoothDevice) msg.obj;
                    if(bdevice == null)
                        return;
                    Log.e(TAG,"-------bdDevice="+bdevice.getName()
                            +"--add="+bdevice.getAddress());
                    if("DfuTarg".equals(bdevice.getName().trim())){
                        String bleBac = bdevice.getAddress().trim();
                        deviceAddress = bleBac;
                        SharedPreferencesUtils.setParam(NewW37OTAActivity.this, "UPM", bdevice.getAddress());
                        if(bluetoothAdapter != null)
                            bluetoothAdapter.stopLeScan(mLeScanCallback);
                        //开始连接
//                        if(MyApp.getInstance().getmW30SBLEManage() != null){
//                            progressNumberTv.setText(getResources().getString(R.string.auto_upgrade));
//                            MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices().connectBle("W30",bleBac);
//
//                        }

                        if(MyApp.getInstance().getW37ConnStatusService() != null){
                            progressNumberTv.setText(getResources().getString(R.string.auto_upgrade));
                            MyApp.getInstance().getW37ConnStatusService().connW37ByMac(bleBac,"W37");
                        }

                    }
                    break;

                case 1002:  //连接成功，开始校验升级文件
                    Log.e(TAG,"----1002--deviceAddress="+deviceAddress+"---="+downLoadSaveUrl);
                    if(deviceAddress == null)
                        return;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)//设备地址
                                    .setDeviceName("W37")//设备名
                                    .setDisableNotification(true)
                                    .setKeepBond(true);
                            starter.setZip(downLoadSaveUrl);
                            starter.start(NewW37OTAActivity.this, W30SDfuService.class);//开始升级
                        }
                    });

                    break;
                case 1003:  //开始扫描
                    //开始扫描
                    if(bluetoothAdapter != null)
                        bluetoothAdapter.startLeScan(mLeScanCallback);
                    progressNumberTv.setText(getResources().getString(R.string.auto_upgrade));
                    break;

                case 1111:

                    //断开蓝牙
//                    W30SBLEManage.mW30SBLEServices.disconnectBle();
//                    //手动断开清楚mac数据
//                    W30SBLEManage.mW30SBLEServices.disClearData();
                    //SharePeClear.clearDatas(B18IAppSettingActivity.this);
                    //W30SBLEManage.mW30SBLEServices.close();
                  //  MyApp.getInstance().getW37BleOperateManager().disBleDeviceByMac();

                    SharedPreferencesUtils.saveObject(MyApp.getInstance(),Commont.BLEMAC,"");
                    SharedPreferencesUtils.saveObject(MyApp.getInstance(),Commont.BLENAME,"");

                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSportTime", "2017-11-02 15:00:00");
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upSleepTime", "2015-11-02 15:00:00");
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "upHeartTime", "2017-11-02 15:00:00");

                    startActivity(NewSearchActivity.class);
                    finish();
                    break;
            }

        }
    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30_ota_layout);
        ButterKnife.bind(this);


        initViews();
        bluetoothAdapter = getmBluetoothManager().getAdapter();



        getDeviceVersion(); //对比固件包

    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.firmware_upgrade));
        commentB30BackImg.setVisibility(View.VISIBLE);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37Constance.W37_DISCONNECTED_ACTION);
        intentFilter.addAction(W37Constance.W37_CONNECTED_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);

        //String deviceVersion = (String) SharedPreferencesUtils.getParam(NewW30OTAActivity.this, "W30S_V", "20");
        progressNumberTv.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();
        //注册空中升级的监听
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @OnClick({R.id.commentB30BackImg, R.id.otaStartBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.otaStartBtn:  //升级按钮

                File file = new File(downLoadSaveUrl);
                if (!file.isFile())
                    return;
                Log.e(TAG, "---------文件的地址=" + file.getName() + "----=file=" + file.getAbsolutePath());
                //发送升级的指令
                //MyApp.getInstance().getmW30SBLEManage().upGradeDevice();
                W37DataAnalysis.getW37DataAnalysis().sendOtaNoti();
                W30SBleUtils.isOtaConn = true;
                otaStartBtn.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                //延迟3s
                handler.sendEmptyMessageDelayed(1003, 3 * 1000);

                break;
        }
    }


   //升级的监听
    private DfuProgressListener dfuProgressListener = new DfuProgressListener() {

        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.e(TAG,"---------onDeviceConnecting="+deviceAddress);
            progressNumberTv.setText(getResources().getString(R.string.string_w30s_device_connection));//"设备连接中..."
            otaProgressBar.setMax(100);
            otaProgressBar.setIndeterminate(true);

        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.e(TAG,"--------onDeviceConnected="+deviceAddress);

        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Log.e(TAG,"---------onDfuProcessStarting="+deviceAddress);
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.e(TAG,"---------onDfuProcessStarted="+deviceAddress);
            otaProgressBar.setIndeterminate(true);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.e(TAG,"---------onEnablingDfuMode="+deviceAddress);
            otaProgressBar.setIndeterminate(true);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.e(TAG,"---------onProgressChanged="+deviceAddress+"---percent="+partsTotal+"---speed="+speed+"--avgSpeed="+avgSpeed+"--currentPart="+currentPart+"--partsTotal="+partsTotal);
            if (percent >= 100) {
                progressNumberTv.setText(getResources().getString(R.string.upgrade_completed));
            } else {
                progressNumberTv.setText(getResources().getString(R.string.upgrade) + percent + "%");
            }
            otaProgressBar.setIndeterminate(false);
            otaProgressBar.setProgress(percent);
            otaStartBtn.setEnabled(false);//默认禁止点击
            otaStartBtn.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
            otaStartBtn.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"


        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.e(TAG,"-----验证固件----onFirmwareValidating="+deviceAddress);

        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.e(TAG,"---------onDeviceDisconnecting="+deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.e(TAG,"---------onDeviceDisconnected="+deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.e(TAG,"-----升级完成----onDfuCompleted="+deviceAddress);
            isOtaSuccess = true;
            W30SBleUtils.isOtaConn = false;
            progressNumberTv.setText(getResources().getString(R.string.upgrade_completed));
            otaStartBtn.setText(getResources().getString(R.string.upgrade_completed));//"升级中禁止操作"
            handler.sendEmptyMessageDelayed(1111,2 * 1000);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.e(TAG,"---升级终止------onDfuAborted="+deviceAddress);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.e(TAG,"---------onError="+deviceAddress+"--error="+error+"--errorType="+errorType+"--message="+message);


        }
    };



    //连接状态的广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            Log.e(TAG,"------OTA--action="+action);
            if(action.equals(W37Constance.W37_CONNECTED_ACTION)){   //已经连接
                if(!isOtaSuccess){
                    progressNumberTv.setText(getResources().getString(R.string.auto_upgrade));
                    handler.sendEmptyMessage(1002);
                }

            }

            if(action.equals(W37Constance.W37_DISCONNECTED_ACTION)){    //断开连接
                //progressNumberTv.setText("连接断开");
                if(isOtaSuccess){
                    String saveMac = WatchUtils.getSherpBleMac(NewW37OTAActivity.this);
                   // MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices().connectBle("W30",saveMac);

                    MyApp.getInstance().getW37ConnStatusService().connW37ByMac(saveMac,"W37");

                }
            }

        }
    };




    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object == null)
            return;
        Log.e(TAG, "---------网络返回=" + object.toString());
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if (!jsonObject.has("code")) {
                noUpdate();
                return;
            }

            if (jsonObject.getInt("code") == 200) {
                JSONObject dataJsono = jsonObject.getJSONObject("data");
                if (dataJsono != null) {
                    String version = dataJsono.getString("version");
                    String deviceVersion = (String) SharedPreferencesUtils.getParam(NewW37OTAActivity.this, "W37S_V", "20");
                    int deviceV = Integer.valueOf(deviceVersion.trim());
                    if (Integer.valueOf(version) > deviceV) {
                        String downUrl = dataJsono.getString("url");
                        otaStartBtn.setEnabled(true);
                        otaStartBtn.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
                        upProoss.setVisibility(View.VISIBLE);//默认隐藏
                        otaStartBtn.setText(getResources().getString(R.string.string_w30s_upgrade));
                        progressNumberTv.setText(getResources().getString(R.string.string_w30s_upgradeable) + "_V" + version);//"可升级_V" + version

                        //有更新包时直接下载
                        downloadUpdatePack(downUrl);

                    } else {
                        noUpdate();
                    }

                }
            }else{
                noUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    //不用升级
    private void noUpdate(){
        otaStartBtn.setEnabled(false);
        otaStartBtn.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//能点击的背景
        upProoss.setVisibility(View.GONE);//默认隐藏
        otaStartBtn.setText(getResources().getString(R.string.latest_version));
    }

    //下载
    private void downloadUpdatePack(String douwnlUrl){

        File historyFile = new File(douwnlUrl);
//        if(historyFile.exists())
//            historyFile.delete();

        Log.e(TAG,"------histTory="+historyFile.exists()+"--="+historyFile.isFile());

        Aria.download(this)
                .load(douwnlUrl)     //读取下载地址
                .setFilePath(downLoadSaveUrl)//设置文件保存的完整路径
                .ignoreFilePathOccupy()
                .create();  //启动下载
    }



    /**
     * 扫描
     */

    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //Log.d("---------", "扫描到了" + device.getAddress() + "====" + device.getName());
            if(device != null && !WatchUtils.isEmpty(device.getName()) && !WatchUtils.isEmpty(device.getAddress())){
                if(device.getName().equals("DfuTarg")){
                    Message message = new Message();
                    message.obj = device;
                    message.what = 1001;
                    handler.sendMessage(message);
                }
            }

        }
    };

    //请求固件版本
    private void getDeviceVersion(){
        if(requestPressent != null){
            String w30S_v = (String) SharedPreferencesUtils.getParam(NewW37OTAActivity.this, "W37S_V", "20");
            if (WatchUtils.isEmpty(w30S_v))
                return;
            String baseUrl = Commont.FRIEND_BASE_URL;
            Map<String,String> jsonObect = new HashMap<>();
            jsonObect.put("clientType", "Android_W37");
            jsonObect.put("version", w30S_v);
            if (requestPressent != null) {
                //获取版本
                requestPressent.getRequestJSONObject(0x01, baseUrl + URLs.getVersion, NewW37OTAActivity.this, new Gson().toJson(jsonObect), 0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dfuProgressListener != null)
            DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);//取消监听升级回调
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();

        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    private BluetoothManager mBluetoothManager = null;

    //获取bluetooth
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

}
