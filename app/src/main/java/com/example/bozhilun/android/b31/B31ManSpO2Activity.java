package com.example.bozhilun.android.b31;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.TmpCustomCircleProgressBar;
import com.example.bozhilun.android.b31.km.NohttpUtils;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISpo2hDataListener;
import com.veepoo.protocol.model.datas.Spo2hData;
import com.veepoo.protocol.model.enums.EDeviceStatus;
import com.veepoo.protocol.model.enums.ESPO2HStatus;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31血氧测试
 * Created by Admin
 * Date 2018/12/18
 */
public class B31ManSpO2Activity extends WatchBaseActivity {

    private static final String TAG = "B31ManSpO2Activity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b31MeaureSpo2ProgressView)
    TmpCustomCircleProgressBar b31MeaureSpo2ProgressView;
    @BindView(R.id.b31MeaureStartImg)
    ImageView b31MeaureStartImg;
    @BindView(R.id.showSpo2ResultTv)
    TextView showSpo2ResultTv;
    @BindView(R.id.spo2ShowGifImg)
    ImageView spo2ShowGifImg;


    //开始或者停止测量的标识
    private boolean isStart = false;

    private boolean isFirstMan = true;

    private NohttpUtils nohttpUtils;
    private String userId = null;
    private String deviceCode = null;

    //开始测量的当前时间
    long currTime;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("CheckResult")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                Spo2hData spo2hData = (Spo2hData) msg.obj;
                if (spo2hData == null)
                    return;
                if(spo2hData.getDeviceState() != EDeviceStatus.FREE || (spo2hData.getSpState() == ESPO2HStatus.CLOSE && !spo2hData.isChecking())){   //设置端正在使用测量功能，
                    showSpo2ResultTv.setText(WatchUtils.setBusyDesicStr());
                    isStart = true;
                    startOrStopManSpo2();
                    return;
                }

                if(spo2hData.getSpState() == ESPO2HStatus.OPEN && spo2hData.isChecking()){
                    b31MeaureSpo2ProgressView.setProgress(spo2hData.getCheckingProgress());
                }


                if (spo2hData.getCheckingProgress() == 0x00 && !spo2hData.isChecking()) {
                    int spo2Value = spo2hData.getValue();
                    b31MeaureSpo2ProgressView.setTmpTxt(spo2hData.getValue() + "%");
                    showSpo2ResultTv.setText(spo2Value == 0 ? "":verSpo2Status(spo2hData.getValue()));
                    b31MeaureSpo2ProgressView.setOxyDexcStr(spo2Value == 0 ? getResources().getString(R.string.calibrating):getResources().getString(R.string.string_spo2_concent));

                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    Glide.with(B31ManSpO2Activity.this).asGif().load(R.drawable.spgif).apply(options).into(spo2ShowGifImg);
                  // uploadSpo2Data(spo2hData);
                }

            }

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_man_spo2);
        ButterKnife.bind(this);

        isFirstMan = true;

        initViews();


        userId = (String) SharedPreferencesUtils.readObject(B31ManSpO2Activity.this, Commont.USER_ID_DATA);
        deviceCode = MyApp.getInstance().getMacAddress();
        nohttpUtils = NohttpUtils.getNoHttpUtils();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.spo2_test));
        //圆环背景色
        b31MeaureSpo2ProgressView.setInsideColor(Color.parseColor("#72CBEE"));
        //进度色
        b31MeaureSpo2ProgressView.setOutsideColor(Color.WHITE);
        spo2ShowGifImg.setImageResource(R.drawable.spgif);
        b31MeaureSpo2ProgressView.setMaxProgress(100);
        b31MeaureSpo2ProgressView.setOxyDexcStr(getResources().getString(R.string.spo2_calibration_pro));
        b31MeaureSpo2ProgressView.setOxyCh(true);
        b31MeaureSpo2ProgressView.setTmpTxt(null);

        commentB30TitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });

    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg,
            R.id.b31MeaureStartImg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B31ManSpO2Activity.this);
                break;
            case R.id.b31MeaureStartImg:    //开始或停止测量
                if (MyCommandManager.DEVICENAME == null) {
                    showSpo2ResultTv.setText(getResources().getString(R.string.device)+getResources().getString(R.string.string_not_coon));
                    return;
                }
                startOrStopManSpo2();
                break;
        }
    }

    private void startOrStopManSpo2() {
        if (!isStart) {   //开始测量
            showSpo2ResultTv.setText("环境校验准备中,请保持正确姿势");
            isStart = true;
            //当前时间
            b31MeaureSpo2ProgressView.setTmpTxt(null);
            b31MeaureStartImg.setImageResource(R.drawable.detect_sp_stop);
            b31MeaureSpo2ProgressView.stopAnim();
//            b31MeaureSpo2ProgressView.setScheduleDuring(4 * 1000);
//            b31MeaureSpo2ProgressView.setProgress(100);
            MyApp.getInstance().getVpOperateManager().startDetectSPO2H(iBleWriteResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2hData) {
                    Log.e(TAG, "----------spo2hData=" + spo2hData.toString());
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = spo2hData;
                    handler.sendMessage(message);
                }
            });

        } else {  //停止测量
            isStart = false;
            Glide.with(B31ManSpO2Activity.this).clear(spo2ShowGifImg);
            spo2ShowGifImg.setImageResource(R.drawable.spgif);
            b31MeaureStartImg.setImageResource(R.drawable.detect_sp_start);
            b31MeaureSpo2ProgressView.setOxyDexcStr(getResources().getString(R.string.spo2_calibration_pro));
            b31MeaureSpo2ProgressView.stopAnim(0);
            MyApp.getInstance().getVpOperateManager().stopDetectSPO2H(iBleWriteResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2hData) {

                }
            });
           // b31MeaureSpo2ProgressView.setTmpTxt("0%");

        }
    }


    @Override
    protected void onDestroy() {
        nohttpUtils.cancleHttpPost(0x01);
        if(isStart){
            MyApp.getInstance().getVpOperateManager().stopDetectSPO2H(iBleWriteResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2hData) {

                }
            });
        }
        super.onDestroy();
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    //判断血氧浓度是否正常
    //范围是[0-99], [0-79]=血氧远低于正常值，警告用户要重视,[80-89]=血氧浓度低，提醒用户重视，
    // [90-95]=血氧浓度偏低,[95-99]=血氧正常
    private String verSpo2Status(int spo2V){

        if(spo2V >= 95 && spo2V <=99){
            return getResources().getString(R.string.string_normal);
        }else if(spo2V >= 90 && spo2V <= 95){
            return getResources().getString(R.string.string_spo2_low);
        }else if(spo2V >= 80 && spo2V <= 89){
            return getResources().getString(R.string.string_spo2_lowest);
        }else if(spo2V <= 79){
            if(spo2V == 1){
                return getResources().getString(R.string.try_again);
            }else{
                return getResources().getString(R.string.string_spo2_no_normal);
            }

        }else{
            return null;
        }
    }



    //上传测量的血氧数据
    private void uploadSpo2Data(Spo2hData spo2hData) {
//
//        List<Map<String,Object>>  spo2List = new ArrayList<>();
//        Map<String,Object> map = new HashMap<>();
//        map.put("AccountId",userId);
//        map.put("DeviceCode",deviceCode);
//        map.put("Bo",spo2hData.getValue());
//        map.put("TestTime",WatchUtils.getCurrentDate1());
//        spo2List.add(map);
//        String spo2Url = KmConstance.uploadBloodOxygen();
//        String params = new Gson().toJson(spo2List);
//        Log.e(TAG,"---params="+params);
//        nohttpUtils.getModelRequestJSONObject(0x01,spo2Url,params,onResponseListener);


        //第一次上传
        if(isFirstMan){
            uploadSpo2(spo2hData);
        }else{
            currTime = System.currentTimeMillis();
            //保存的时间
            long savedTime = (long) SharedPreferencesUtils.getParam(B31ManSpO2Activity.this,"spo2_time",System.currentTimeMillis());
            //差值
            long differenceTime = currTime-savedTime;
            if(differenceTime/1000 <5)
                return;
            uploadSpo2(spo2hData);
        }


    }


    private void uploadSpo2(Spo2hData spo2hData){
        try {
            String url = Commont.FRIEND_BASE_URL + Commont.UPLOAD_HAND_HEART_AD_SPO2;
            Map<String,Object> spo2Map = new HashMap<>();
            spo2Map.put("userId",userId);
            spo2Map.put("pulse","0");
            spo2Map.put("bloodOxygen",spo2hData.getValue()+"%");
            String parms = new Gson().toJson(spo2Map);
            Log.e(TAG,"----parms="+parms);
            nohttpUtils.getModelRequestJSONObject(0x02,url,new Gson().toJson(spo2Map),onResponseListener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            Log.e(TAG,"------response="+what+"--="+response.get());
            if(what == 0x02){
                isFirstMan = false;
                SharedPreferencesUtils.setParam(B31ManSpO2Activity.this,"spo2_time",System.currentTimeMillis());
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e(TAG,"------failed="+response.getException());
        }

        @Override
        public void onFinish(int what) {

        }
    };

}
