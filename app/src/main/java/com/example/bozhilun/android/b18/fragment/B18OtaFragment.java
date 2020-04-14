package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.hplus.bluetooth.BleProfileManager;
import com.hplus.bluetooth.command.OnResponseListener;
import com.hplus.bluetooth.dfu.DfuFileStatusCallback;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by Admin
 * Date 2019/12/11
 */
public class B18OtaFragment extends LazyFragment {

    private static final String TAG = "B18OtaFragment";

    private String downLoadSaveUrl = Environment.getExternalStorageDirectory().getPath()+"/B18/b18.zip";

    View view;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    //进度
    @BindView(R.id.progress_number)
    TextView progressNumber;
    @BindView(R.id.progressBar_upgrade)
    ProgressBar progressBarUpgrade;
    @BindView(R.id.up_prooss)
    LinearLayout upProoss;
    @BindView(R.id.btn_start_up)
    Button btnStartUp;
    Unbinder unbinder;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String downUrl = (String) msg.obj;
            if(WatchUtils.isEmpty(downUrl))
                return;
            //版本名称
            String versionS = StringUtils.substringAfter(downUrl,"v");
            String tmpVs = StringUtils.substringBefore(versionS,"z");

            progressNumber.setText(getResources().getString(R.string.string_w30s_upgradeable)+": v"+tmpVs);

            btnStartUp.setEnabled(true);
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
            btnStartUp.setText(getResources().getString(R.string.string_w30s_upgrade));
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B18BleConnManager.isOta = true;
        DfuServiceListenerHelper.registerProgressListener(getActivity(), mDFUProgressListener);
        BleProfileManager.getInstance().getCommandController().addResponseListener(onResponseListener);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.w30s_frrinware_upgrade, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        initData();

        return view;
    }

    private void initData() {
        if(MyCommandManager.DEVICENAME == null)
            return;
        //获取设备版本
        BleProfileManager.getInstance().getCommandController().getDeviceVersionCommand();


    }

    private void initViews() {
        barTitles.setText(getResources().getString(R.string.firmware_upgrade));
        progressNumber.setText(getResources().getString(R.string.latest_version));

        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
        btnStartUp.setText(getResources().getString(R.string.latest_version));

        progressBarUpgrade.setMax(100);
        progressBarUpgrade.setProgress(-1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        B18BleConnManager.isOta = false;
        try {
            DfuServiceListenerHelper.unregisterProgressListener(getActivity(),mDFUProgressListener);
            BleProfileManager.getInstance().getCommandController().removeResponseListener(onResponseListener);
        }catch (Exception e){
            e.printStackTrace();
        }

//        Aria.download(this)
//                .stopAllTask();
    }

    @OnClick({R.id.image_back, R.id.btn_start_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                if(getActivity() == null)
                    return;
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.btn_start_up: //更新
                File savedFile = new File(downLoadSaveUrl);
                if(!savedFile.isFile())
                    return;
                Log.e(TAG,"---------filePath="+downLoadSaveUrl+"---name="+savedFile.getName());
              //  otaOperate();
                BleProfileManager.getInstance().getDfuController()
                        .startFirmwareUpgrade(downLoadSaveUrl);
                btnStartUp.setText(getResources().getString(R.string.upgrade));
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                break;
        }
    }



    private DfuProgressListener mDFUProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.e(TAG,"onDeviceConnecting" + deviceAddress);
            progressNumber.setText(getResources().getString(R.string.string_w30s_device_connection));
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.e(TAG,"onDeviceConnected = " + deviceAddress);
            progressNumber.setText(getResources().getString(R.string.connted)+"...");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Log.e(TAG,"onDfuProcessStarting = " + deviceAddress);
            progressNumber.setText(getResources().getString(R.string.string_ota_dev_ready));
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.e(TAG,"onDfuProcessStarted = " + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.e(TAG,"onEnablingDfuMode = " + deviceAddress);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.e(TAG,"onProgressChanged = " + percent);
            progressNumber.setText(getResources().getString(R.string.upgrade)+": "+percent+"%");
            progressBarUpgrade.setProgress(percent);
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.e(TAG,"onFirmwareValidating = " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.e(TAG,"onDeviceDisconnecting = " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.e(TAG,"onDeviceDisconnected = " + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.e(TAG,"onDfuCompleted = " + deviceAddress);
            progressNumber.setText(getResources().getString(R.string.string_ota_uccess));
            btnStartUp.setText(getResources().getString(R.string.string_ota_uccess));
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.e(TAG,"onDfuAborted = " + deviceAddress);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.e(TAG,"onError = " + deviceAddress);
            progressNumber.setText(getResources().getString(R.string.string_updata_error_tryup));
        }
    };







    private OnResponseListener onResponseListener = new OnResponseListener() {
        @Override
        public void onResponse(String s) {
            Log.e(TAG,"-------s="+s);
            if(WatchUtils.isEmpty(s))
                return;
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean isResponse = jsonObject.getBoolean("response");
                if(!isResponse)
                    return;
                int type = jsonObject.getInt("type");
                if(type == 8){
                    JSONObject json = jsonObject.optJSONObject("data");
                    int productNumber = json.getInt("productNumber");
                    int functionNumber = json.getInt("functionNumber");
                    int version = json.optInt("version");

                    getServerVersion(version);


                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //获取网络上的版本号
    private void getServerVersion(final int versionCode) {
        String b18Url = Commont.FRIEND_BASE_URL + URLs.getVersion;
        Map<String,String> urlMap = new HashMap<>();
        urlMap.put("clientType","Android_B18");
        urlMap.put("version",versionCode+"");
        OkHttpTool.getInstance().doRequest(b18Url, new Gson().toJson(urlMap), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"---------请求fanh ="+result);
                if(WatchUtils.isEmpty(result))
                    return;
                analysisServer(result,versionCode);
            }
        });

    }




    private void analysisServer(String result,int deviceVersion) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(!jsonObject.has("code"))
                return;
            if(jsonObject.getInt("code") == 200){
                JSONObject dataJsono = jsonObject.getJSONObject("data");
                String versionStr = dataJsono.getString("version");
                if(Integer.valueOf(versionStr) > deviceVersion){    //可以升级固件了
                    String downUrl = dataJsono.getString("url");

                    Message message = handler.obtainMessage();
                    message.what = 0x01;
                    message.obj = downUrl;
                    handler.sendMessage(message);

//                    File file = new File(downLoadSaveUrl);
//                    if(file.exists())file.delete();


                    Aria.download(this)
                            .load(downUrl)     //读取下载地址
                            .setFilePath(downLoadSaveUrl)//设置文件保存的完整路径
                            .ignoreFilePathOccupy()
                            .create();   //启动下载
                    return;

                }
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
                btnStartUp.setText(getResources().getString(R.string.latest_version));

                return;
            }

            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_on));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
