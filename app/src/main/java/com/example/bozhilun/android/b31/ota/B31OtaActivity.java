package com.example.bozhilun.android.b31.ota;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.DfuService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.err.OadErrorState;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.oad.OnFindOadDeviceListener;
import com.veepoo.protocol.listener.oad.OnUpdateCheckListener;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.settings.OadSetting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuServiceInitiator;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by Admin
 * Date 2020/5/13
 */
public class B31OtaActivity extends WatchBaseActivity {

    private static final String TAG = "B31OtaActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b31OtaBtn)
    Button b31OtaBtn;

    OadSetting oadSetting = null;

    private String mOadFileName = "/storage/emulated/0/Android/data/com.example.bozhilun.android/files/b31.zip";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_ota_layout);
        ButterKnife.bind(this);

        initViews();


        if(MyCommandManager.DEVICENAME == null)
            return;
        final String bleMac = (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC);
        //设备设备版本
        MyApp.getInstance().getVpOperateManager().confirmDevicePwd(iBleWriteResponse, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                Log.e(TAG,"------设备版本="+pwdData.toString());

                oadSetting = new OadSetting(bleMac,pwdData.getDeviceVersion(),pwdData.getDeviceTestVersion(),pwdData.getDeviceNumber(),false);

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {

            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

            }
        },"0000", true);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        DfuServiceListenerHelper.registerProgressListener(this,dfuProgressListener);
    }

    @OnClick({R.id.commentB30BackImg, R.id.b31OtaBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                findOtaDevice();
                break;
            case R.id.b31OtaBtn:
                checkDevice();
                break;
        }
    }

    //发现ota模式下的设备
    private void findOtaDevice() {
        MyApp.getInstance().getVpOperateManager().findOadModelDevice(oadSetting, new OnFindOadDeviceListener() {
            @Override
            public void findOadDevice(String s) {
                Log.e(TAG,"-----finddevice="+s);
                startOta(s);
            }
        });
    }

    private void startOta(String mOadAddress){
        DfuServiceInitiator dfuServiceInitiator = new DfuServiceInitiator(mOadAddress)
                .setDeviceName("veepoo")
                .setKeepBond(false);
        dfuServiceInitiator.setZip(null, mOadFileName);
        dfuServiceInitiator.start(this, DfuService.class);
    }

    private void checkDevice(){
        MyApp.getInstance().getVpOperateManager().checkVersionAndFile(oadSetting, new OnUpdateCheckListener() {
            @Override
            public void onNetVersionInfo(int i, String s, String s1) {
                Log.e(TAG,"服务器版本信息,设备号=" + i + ",最新版本=" + s + ",升级描述=" + s);
            }

            @Override
            public void onDownLoadOadFile(float v) {
                Log.e(TAG,"----服务器下载进度="+v);
            }

            @Override
            public void onCheckFail(int endState) {
                switch (endState) {
                    case OadErrorState.UNCONNECT_NETWORK:
                        Log.e(TAG,"网络出错");
                        break;
                    case OadErrorState.UNCONNECT_SERVER:
                        Log.e(TAG,"服务器连接不上");
                        break;
                    case OadErrorState.SERVER_NOT_HAVE_NEW:
                        Log.e(TAG,"服务器无此版本");
                        break;
                    case OadErrorState.DEVICE_IS_NEW:
                        Log.e(TAG,"设备是最新版本");
                        break;
                    case OadErrorState.OAD_FILE_UNEXITS:
                        Log.e(TAG,"文件不存在");
                        break;
                    case OadErrorState.OAD_FILE_MD5_UNSAME:
                        Log.e(TAG,"文件md5不一致");
                        break;
                }
            }

            @Override
            public void onCheckSuccess(String s) {
                Log.e(TAG,"-------文件校验无误="+s);
            }

            @Override
            public void findOadDevice(String s) {
                Log.e(TAG,"-----找到OTAG模式下的设备了="+s);
            }
        });
    }



    private DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String s) {
            Log.e(TAG,"-----onDeviceConnecting-----");
        }

        @Override
        public void onDeviceConnected(String s) {
            Log.e(TAG,"----onDeviceConnected------");
        }

        @Override
        public void onDfuProcessStarting(String s) {
            Log.e(TAG,"---onDfuProcessStarting-------");
        }

        @Override
        public void onDfuProcessStarted(String s) {
            Log.e(TAG,"---onDfuProcessStarted-------");
        }

        @Override
        public void onEnablingDfuMode(String s) {
            Log.e(TAG,"----onEnablingDfuMode------");
        }

        @Override
        public void onProgressChanged(String s, int i, float v, float v1, int i1, int i2) {
            Log.e(TAG,"----onProgressChanged------");
        }

        @Override
        public void onFirmwareValidating(String s) {
            Log.e(TAG,"----onFirmwareValidating------");
        }

        @Override
        public void onDeviceDisconnecting(String s) {
            Log.e(TAG,"----onDeviceDisconnecting------");
        }

        @Override
        public void onDeviceDisconnected(String s) {
            Log.e(TAG,"----onDeviceDisconnected------");
        }

        @Override
        public void onDfuCompleted(String s) {
            Log.e(TAG,"-----onDfuCompleted-----");
        }

        @Override
        public void onDfuAborted(String s) {
            Log.e(TAG,"----onDfuAborted------");
        }

        @Override
        public void onError(String s, int i, int i1, String s1) {
            Log.e(TAG,"-----onError-----");
        }
    };






    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
