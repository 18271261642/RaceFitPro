package com.example.bozhilun.android.w30s.ota;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.bean.UpDataBean;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.example.bozhilun.android.R;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuServiceInitiator;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;


/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/4/12 10:19
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class NewW30sFirmwareUpgrade extends WatchBaseActivity implements RequestView {

    private static final String TAG = "NewW30sFirmwareUpgrade";

    public static final String UpData = "com.exalpme.bozhilun.android.w30s.ota";
    private static final String FileStringPath = "/storage/emulated/0/Android/com.bozlun.bozhilun.android/cache/W30V3.zip";

//    private static final String FileStringPath = Environment.getExternalStorageDirectory()+"/RaceFit/";//"/storage/emulated/0/Android/com.bozlun.bozhilun.android/cache/W30V3.zip";

    String upDataStringUrl = "";//固件升级链接
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.progress_number)
    TextView progressNumber;//下载升级进度说明
    @BindView(R.id.up_prooss)
    LinearLayout up_prooss;
    @BindView(R.id.btn_start_up)
    Button btnStartUp;//按钮
    @BindView(R.id.progressBar_upgrade)
    ProgressBar proBar;
    private RequestPressent requestPressent;
    private Handler mHandler = new Handler(new HandlerCallBack());
    private BluetoothAdapter bluetoothAdapter;
    private int upDataState = 0x01;
    private boolean isOKs = false;
    private boolean isUpDatas = false;
    private boolean isUpData = false;
    private int isOneUp = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_frrinware_upgrade);
        ButterKnife.bind(this);

        regeditMsg();
        init();
//
//        btnStartUp.setEnabled(false);//默认禁止点击
//        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
//        btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本

        Log.e(TAG,"------FileStringPath="+FileStringPath);
    }

    private void init() {
        barTitles.setText(getResources().getString(R.string.firmware_upgrade));
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        String mylanmac = (String) SharedPreferencesUtils.readObject(NewW30sFirmwareUpgrade.this, Commont.BLEMAC);
        if (!WatchUtils.isEmpty(mylanmac)) {
            SharedPreferencesUtils.setParam(NewW30sFirmwareUpgrade.this, "Up_Address", mylanmac);
        }
        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
        up_prooss.setVisibility(View.GONE);//默认隐藏

        bluetoothAdapter = MyApp.getInstance().getmW30SBLEManage().getmW30SBLEServices().getmBluetoothAdapter();
    }

    private void regeditMsg(){
        //注册空中升级的监听
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
        //注册连接状态的广播
        registerReceiver(mBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //当界面启动时就获取比较最新版本
        getNetWorke();
    }

    /**
     * getNetWorke()获取后台版本
     */
    public void getNetWorke(){
        try {
            String w30S_v = (String) SharedPreferencesUtils.getParam(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
            if (WatchUtils.isEmpty(w30S_v))
                return;
            String baseurl = URLs.HTTPs;
            JSONObject jsonObect = new JSONObject();
            jsonObect.put("clientType", "android");
            jsonObect.put("version", w30S_v);
            if (requestPressent != null) {
                //获取版本
                requestPressent.getRequestJSONObject(1, baseurl + URLs.getVersion, NewW30sFirmwareUpgrade.this, jsonObect.toString(), 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLeScanCallback != null && bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        if (mLeScanCallback2 != null && bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(mLeScanCallback2);
        }
        try{
            DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);//取消监听升级回调
            if(mBroadcastReceiver != null){
                unregisterReceiver(mBroadcastReceiver);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOneUp = 0;
    }


    /***********************************/
    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpData);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"-----ota--action-="+action);
            switch (action) {
                case UpData:

                    break;
                case W30SBLEServices.ACTION_GATT_CONNECTED:     //已经连接
                    isOKs = false;

                    if (mLeScanCallback2 != null && bluetoothAdapter != null) {
                        bluetoothAdapter.stopLeScan(mLeScanCallback2);

                        if (isOneUp == 2){
                            try {
                                MyApp.getInstance().getmW30SBLEManage().syncTime(new W30SBLEServices.CallDatasBackListenter() {
                                    @Override
                                    public void CallDatasBackListenter(W30SSportData w30SSportData) {

                                    }

                                    @Override
                                    public void CallDatasBackListenter(W30SSleepData w30SSleepData) {

                                    }

                                    @Override
                                    public void CallDatasBackListenter(W30SDeviceData w30SDeviceData) {

                                    }

                                    @Override
                                    public void CallDatasBackListenter(W30SHeartData w30SHeartData) {

                                    }

                                    @Override
                                    public void CallDatasBackListenterIsok() {

                                    }
                                });
                            }catch (Exception e){
                                e.getMessage();
                            }

                        }
                    }

                    if (isUpData) {
                        isUpData = false;
                        Log.e("-----------", "已经连接001");
                        up_prooss.setVisibility(View.VISIBLE);//默认隐藏
                        progressNumber.setText(getResources().getString(R.string.connted));
                        proBar.setIndeterminate(true);
                        btnStartUp.setEnabled(false);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                        mHandler.sendEmptyMessageDelayed(0x02, 3000);
                    }
                    break;
                case W30SBLEServices.ACTION_GATT_DISCONNECTED:
                    Log.e("-----------", "断开连接");

                    break;
            }
        }
    };

    /**************************/
    /**
     * 点击
     *
     * @param view
     */
    @OnClick({R.id.btn_start_up, R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_up:
                if (btnStartUp.isEnabled()) {
                    if (upDataState == 0x01) {
                        isUpData = true;
                        if (!WatchUtils.isEmpty(upDataStringUrl)) {
                            isOKs = true;
                            if (MyApp.getInstance().getmW30SBLEManage() != null) {
                                MyApp.getInstance().getmW30SBLEManage().upGradeDevice();
                            }
                            mHandler.sendEmptyMessageDelayed(0x01, 500);
                        }
                    } else if (upDataState == 0x02) {
                        //获取版本失败，重新获取
                        getNetWorke();
                    } else if (upDataState == 0x03) {
                        mHandler.sendEmptyMessageDelayed(0x02, 500);
                    }

                }
                break;
            case R.id.image_back:
                if (isOKs) {
                    System.out.println("升级状态禁止操作");
                    Toast.makeText(this, getResources().getString(R.string.string_w30s_up_statu), Toast.LENGTH_SHORT).show();
                } else {
                    if (isUpDatas) {
                        //Toast.makeText(this, "Please restart App", Toast.LENGTH_SHORT).show();
                        ToastUtil.showLong(this,"升级完成，请退出APP重新打开!");
                        dis();
                    } else {
                        finish();
                    }
                }

                break;
        }
    }


    /*********************获取数据，对比版本*****************************/
    /**
     * 获取数据，对比版本
     *
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        Log.e(TAG,"---------obj="+object);
        if (what != 1) return;
        if (object == null) return;
        UpDataBean upDataBean = new Gson().fromJson(object.toString(), UpDataBean.class);
        if (isOneUp==0){
            if (upDataBean.getResultCode().equals("010")) {
                //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                up_prooss.setVisibility(View.GONE);//默认隐藏
                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
            } else {
                String version = upDataBean.getVersion();
                String w30S_v = (String) SharedPreferencesUtils.getParam(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
                if(Integer.valueOf(version.trim()) > Integer.valueOf(w30S_v.trim())){
                    MyCommandManager.isOta = true;  //升级状态下
                    W30SBleUtils.isOtaConn = true;
                    upDataState = 0x01;
                    btnStartUp.setEnabled(true);
                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
                    up_prooss.setVisibility(View.VISIBLE);//默认隐藏
                    btnStartUp.setText(getResources().getString(R.string.string_w30s_upgrade));
                    progressNumber.setText(getResources().getString(R.string.string_w30s_upgradeable) + "_V" + version);//"可升级_V" + version
                    //获取下载链接
                    upDataStringUrl = upDataBean.getUrl().trim();
                    Log.e(TAG,"---upDataStringUrl--="+upDataStringUrl);
                    Aria.download(this)
                            .load(String.valueOf(upDataStringUrl))     //读取下载地址
                            .setFilePath(FileStringPath) //设置文件保存的完整路径
                            .ignoreFilePathOccupy()
                            .create();  //启动下载
                }else{
                    W30SBleUtils.isOtaConn = false;
                    btnStartUp.setEnabled(false);//默认禁止点击
                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                    up_prooss.setVisibility(View.GONE);//默认隐藏
                    btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
                }

            }
        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        W30SBleUtils.isOtaConn = false;
        e.getMessage();
        upDataState = 0x02;
        Toast.makeText(this, getResources().getString(R.string.get_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
        btnStartUp.setEnabled(true);
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
        up_prooss.setVisibility(View.GONE);//默认隐藏
        btnStartUp.setText(getResources().getString(R.string.string_w30s_reacquire_version));//"重新获取版本"
    }

    @Override
    public void closeLoadDialog(int what) {

    }

    /**************************************************/
    /**
     * 空中升级时的监听
     */
    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {


        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDeviceConnecting=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressNumber.setText(getResources().getString(R.string.string_w30s_device_connection));//"设备连接中..."
                        proBar.setIndeterminate(true);
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
        }

        //设备开始连接
        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDeviceConnected=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressNumber.setText(getResources().getString(R.string.connted));//"已经连接"
                        proBar.setIndeterminate(true);
                        btnStartUp.setEnabled(false);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                    }
                });

            } catch (Exception e) {
                e.getMessage();
            }

        }

        //升级准备开始的时候调用
        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDfuProcessStarting=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressNumber.setText(getResources().getString(R.string.string_w30s_prepare_updates));//"准备更新"
                        proBar.setIndeterminate(true);
                        isOKs = true;
                        btnStartUp.setEnabled(false);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        //设备开始升级
        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDfuProcessStarted=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        proBar.setIndeterminate(true);
                        isOKs = true;
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.e(TAG,"------升级监听--onEnablingDfuMode=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        proBar.setIndeterminate(true);
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        //升级过程中的回调
        @Override
        public void onProgressChanged(String deviceAddress, final int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.e(TAG,"------升级监听-onProgressChanged-="+deviceAddress+"-="+percent);
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (percent >= 100) {
                            progressNumber.setText(getResources().getString(R.string.upgrade_completed));
                        } else {
                            progressNumber.setText(getResources().getString(R.string.upgrade) + percent + "%");
                        }
                        isOKs = true;
                        proBar.setIndeterminate(false);
                        proBar.setProgress(percent);
                        btnStartUp.setEnabled(false);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        //固件验证
        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.e(TAG,"------升级监听--onFirmwareValidating=");
            Log.e("----", "固件验证" + deviceAddress);
        }

        //设备正在断开
        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDeviceDisconnecting=");
            Log.e("----", "设备正在断开" + deviceAddress);
//            try {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            } catch (Exception e) {
//                e.getMessage();
//            }
        }

        //设备已经断开
        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDeviceDisconnected=");
            Log.e("----", "设备已经断开" + deviceAddress);
//            try {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//
//            } catch (Exception e) {
//                e.getMessage();
//            }

        }

        //升级完成
        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDfuCompleted=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        W30SBleUtils.isOtaConn = false;
                        isUpDatas = true;
                        isOneUp ++;
                        //断开蓝牙
                        W30SBLEManage.mW30SBLEServices.disconnectBle();
                        //手动断开清楚mac数据
                        W30SBLEManage.mW30SBLEServices.disClearData();
                        bluetoothAdapter.startLeScan(mLeScanCallback2);

                        btnStartUp.setEnabled(false);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                        up_prooss.setVisibility(View.GONE);//默认隐藏
                        btnStartUp.setText(getResources().getString(R.string.latest_version));

                        Toast.makeText(NewW30sFirmwareUpgrade.this, "Upgrade success, need to restart App", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.e(TAG,"------升级监听--onDfuAborted=");
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("----", "DFU中止");
                        proBar.setIndeterminate(false);
                        isOKs = false;
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }

        //升级失败
        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.e(TAG,"------升级监听--onError=");
            Log.e("----", "ERROR--升级失败哦" + error + "message" + message);
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isUpDatas = true;
                        upDataState = 0x03;
                        Toast.makeText(NewW30sFirmwareUpgrade.this,
                                getResources().getString(R.string.string_w30s_upgrade_failed),
                                Toast.LENGTH_SHORT).show();//"升级失败哦"
                        btnStartUp.setEnabled(true);//默认禁止点击
                        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//默认不能点击的背景
                        up_prooss.setVisibility(View.GONE);//默认隐藏
                        btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));//"重新升级"
                        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
                        isOKs = false;
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }


        }
    };


    /**
     * 操作监听
     */
    private class HandlerCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            Log.e(TAG,"---hand-what-="+what);
            switch (what) {
                case 0x01:  //开始扫描
                    Log.e(TAG,"----hand=0x01=开始扫描了");
                    MyApp.getInstance().getmW30SBLEManage().openW30SBLEServices();
                    if(bluetoothAdapter != null){
                        Log.e(TAG,"---kais 扫描=--");
                        bluetoothAdapter.startLeScan(mLeScanCallback);
                    }else{
                        W30SBLEManage.getmBluetoothAdapter().startLeScan(mLeScanCallback);
                    }
                    mHandler.removeMessages(0x01);
                    break;
                case 0x02:
                    up_prooss.setVisibility(View.VISIBLE);//默认隐藏
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    isOKs = true;
                    progressNumber.setText(getResources().getString(R.string.string_w30s_up_zip));//"获取升级资源"
//                    isUpData = false;
//                    String up_address = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "Up_Address", "");
//                    String up_name = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "Up_Name", "");
                    File f = new File(FileStringPath);
                    if (f.exists()) {//判断是否有文件
                        Log.e(TAG, "-----有文件");
                        try {
                            Log.e(TAG, "----需要升级的MAC:" + deviceAddress);
                            if (!WatchUtils.isEmpty(deviceAddress)) {
                                Log.e(TAG,"----11111----");
                                DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)//设备地址
                                        .setDeviceName("W30")//设备名
                                        .setKeepBond(true);
                                boolean b = existsFile(FileStringPath);
                                if (b) {
                                    Log.e(TAG,"----2222----");
//                                starter.setZip(R.raw.w30s_v_30_5);
                                    starter.setZip(FileStringPath);//设置固件
                                } else {
                                    Toast.makeText(NewW30sFirmwareUpgrade.this,
                                            getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//"文件路径出错"
                                    if (!WatchUtils.isEmpty(upDataStringUrl)) {
                                        Log.e(TAG,"----3333----");
                                        Aria.download(this)
                                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                                .setFilePath(FileStringPath) //设置文件保存的完整路径
                                                .ignoreFilePathOccupy()
                                                .create();   //启动下载
                                    } else {
                                        Log.e(TAG,"----4444----");
                                        //获取网络的版本
                                        getNetWorke();
                                    }

                                }
                                Log.e(TAG,"----5555----");
                                starter.start(NewW30sFirmwareUpgrade.this, W30SDfuService.class);//开始升级
                            } else {
                                deviceAddress = (String) SharedPreferencesUtils.getParam(NewW30sFirmwareUpgrade.this, "UPM", "");
                                Log.e(TAG, "----设备地址空错");
                                mHandler.sendEmptyMessage(0x02);
                            }
                            Log.e(TAG,"----666----");
                            String time = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA)
                                    .format(new Date(f.lastModified()));
                            System.out.println("文件文件创建时间" + time);
                            System.out.println("文件大小:" + ShowLongFileSzie(f.length()));// 计算文件大小
                            // B,KB,MB,
                            //System.out.println("文件大小:" + fis.available() + "B");
                            System.out.println("文件名称：" + f.getName());
                            System.out.println("文件是否存在：" + f.exists());
                            System.out.println("文件的相对路径：" + f.getPath());
                            System.out.println("文件的绝对路径：" + f.getAbsolutePath());
                            System.out.println("文件可以读取：" + f.canRead());
                            System.out.println("文件可以写入：" + f.canWrite());
                            System.out.println("文件上级路径：" + f.getParent());
                            System.out.println("文件大小：" + f.length() + "B");
                            System.out.println("文件最后修改时间：" + new Date(f.lastModified()));
                            System.out.println("是否是文件类型：" + f.isFile());
                            System.out.println("是否是文件夹类型：" + f.isDirectory());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!WatchUtils.isEmpty(upDataStringUrl)) {
                            Toast.makeText(NewW30sFirmwareUpgrade.this,
                                    getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//OTA包不存在，正在下载
                            Aria.download(this)
                                    .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                    .setFilePath(FileStringPath) //设置文件保存的完整路径
                                    .ignoreFilePathOccupy()
                                    .create();
                        } else {
                            getNetWorke();
                        }

                    }
                    break;
                case 1001:
                    Log.e(TAG,"----hd-1001-找到了目标手表--");
                    BluetoothDevice bdevice = (BluetoothDevice) msg.obj;
                    if(bdevice != null){
                        if ("DfuTarg".equals(bdevice.getName().trim())) {
                            deviceAddress = bdevice.getAddress().trim();
                            Log.e("---------", "符合标准的设备" + bdevice.getAddress() + "====" + bdevice.getName() + "=-======" + deviceAddress);
                            if (W30SBLEManage.getInstance(NewW30sFirmwareUpgrade.this).getmW30SBLEServices() != null) {
                                Log.e(TAG,"--------1111111111--------");
                                bluetoothAdapter.stopLeScan(mLeScanCallback);
                                //W30SBLEManage.getmBluetoothAdapter().stopLeScan(mLeScanCallback);
                                if (one) {
                                    one = false;
                                    deviceAddress = bdevice.getAddress().trim();
                                    SharedPreferencesUtils.setParam(NewW30sFirmwareUpgrade.this, "UPM", bdevice.getAddress());
                                    W30SBLEManage.getInstance(NewW30sFirmwareUpgrade.this).getmW30SBLEServices().connectBle(bdevice.getName(),bdevice.getAddress());
                                    progressNumber.setText(getResources().getString(R.string.string_w30s_device_connection));//"设备连接中..."
                                    proBar.setIndeterminate(true);
                                }

                            }
                        }
                    }
                    break;
            }
            return false;
        }
    }


    /****
     * 计算文件大小
     *
     * @param length
     * @return
     */
    public String ShowLongFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean existsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 扫描
     */
    boolean one = true;
    String deviceAddress = "";
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("---------", "扫描到了" + device.getAddress() + "====" + device.getName());
            if(device != null && !WatchUtils.isEmpty(device.getName()) && !WatchUtils.isEmpty(device.getAddress())){
                if(device.getName().equals("DfuTarg")){

                    Message message = new Message();
                    message.obj = device;
                    message.what = 1001;
                    mHandler.sendMessage(message);
                }
            }

        }
    };


    BluetoothAdapter.LeScanCallback mLeScanCallback2 = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            Log.d("---------", "扫描到了" + device.getAddress() + "====" + device.getName());
            String up_address = (String) SharedPreferencesUtils.getParam(NewW30sFirmwareUpgrade.this, "Up_Address", "");
            String up_name = (String) SharedPreferencesUtils.getParam(NewW30sFirmwareUpgrade.this, "Up_Name", "");
            Log.e(TAG,"----up_address="+up_address+"---="+(up_address.trim().equals(device.getAddress().trim())));
            if(!WatchUtils.isEmpty(up_address)){
                if (up_address.trim().equals(device.getAddress().trim())) {
                    if (W30SBLEManage.mW30SBLEServices != null) {
                        isUpData = false;
                        W30SBLEManage.getmBluetoothAdapter().stopLeScan(mLeScanCallback);
                        W30SBLEManage.mW30SBLEServices.connectBle(device.getName(),device.getAddress());
                    }
                }
            }

        }
    };


    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isOKs) {
            System.out.println("升级状态禁止操作");
            Toast.makeText(this, getResources().getString(R.string.string_w30s_up_statu), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (isUpDatas) {
                dis();
                Toast.makeText(this, "Please restart App", Toast.LENGTH_SHORT).show();
                MyApp.getInstance().removeALLActivity();
                return false;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    public void dis() {
        //断开蓝牙
        W30SBLEManage.mW30SBLEServices.disconnectBle();
        //手动断开清楚mac数据
        W30SBLEManage.mW30SBLEServices.disClearData();
        W30SBLEManage.mW30SBLEServices.close();
        SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTime", "2017-11-02 15:00:00");
        SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTime", "2015-11-02 15:00:00");
        SharedPreferencesUtils.setParam(MyApp.getContext(), "upHeartTime", "2017-11-02 15:00:00");

        SharedPreferencesUtils.saveObject(MyApp.getContext(),  Commont.BLENAME, "");
        SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
        MyCommandManager.DEVICENAME = null;
        //finish();

        MyApp.getInstance().removeALLActivity();
    }
}
