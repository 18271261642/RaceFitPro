package com.example.bozhilun.android.bzlmaps.gaodemaps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AmapSportBean;
import com.example.bozhilun.android.bean.AmapTraceLocation;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;
import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * GPS运动页面
 * Created by Admin
 * Date 2019/10/18
 */
public class AmapSportActivity extends WatchBaseActivity implements LocationSource,
        AMapLocationListener {

    private static final String TAG = "AmapSportActivity";

    @BindView(R.id.amapMapView)
    MapView amapMapView;

    @BindView(R.id.mapSportSpeeddTv)
    TextView mapSportSpeeddTv;
    //总时间
    @BindView(R.id.mapSportCountTimeTv)
    TextView mapSportCountTimeTv;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    //开始或停止的按钮
    @BindView(R.id.amapSportStartBtn)
    Button amapSportStartBtn;
    //总里程
    @BindView(R.id.mapSportCountDisTv)
    TextView mapSportCountDisTv;
    //平均速度
    @BindView(R.id.mapSportAvgSpeedTv)
    TextView mapSportAvgSpeedTv;
    //卡路里
    @BindView(R.id.mapSportKcalTv)
    TextView mapSportKcalTv;


    //mark
    private Marker startMark, endMark;

    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption = null;

    //开始或停止
    private boolean startOrEnd = false;

    private List<LatLng> resultList = new ArrayList<>();
    //轨迹纠偏
    private List<AmapTraceLocation> tempTraceList = new ArrayList<>();

    //运动类型 0-跑步；1-骑行
    private int sportType ;

    //距离
    private double currDisance = 0.0;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    int count = 0;
    private TimeThread timeThread;

    double tempDisance = 0.0;

    private AlertDialog.Builder alertDialog;


    boolean isUnit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:  //计时
                    count ++;
                    int hour = (int) (count / 3600);
                    int mine = (int) (count / 60);
                    int s = (int) (count % 60);

                    mapSportCountTimeTv.setText((hour<10?("0"+hour):hour)+":"+(mine<10?("0"+mine):mine)+":"+(s<10?("0"+s):s));
                    break;
                case 1002:
                    //速度
                    mapSportSpeeddTv.setText((float)msg.obj+" m/s");
                    if(resultList.size()>=2){
                        LatLng latLng2 = resultList.get(resultList.size()-2);
                        DPoint dPoint2 = new DPoint(latLng2.latitude,latLng2.longitude);
                        LatLng latLng1 = resultList.get(resultList.size()-1);
                        DPoint dPoint1 = new DPoint(latLng1.latitude,latLng1.longitude);
                        double currDis =  CoordinateConverter.calculateLineDistance(dPoint2,dPoint1);
                        tempDisance = currDis;
                        currDisance +=currDis;
                    }
                    //总里程 kmToMi
                    String dis = isUnit ? (decimalFormat.format(currDisance / 1000) +" km") : (decimalFormat.format(WatchUtils.kmToMi(currDisance/1000)) + " mile");
                    mapSportCountDisTv.setText(dis);

                    //平均速度 = 总距离 / 总时间
                    if(tempDisance != 0.0){
                        //当前时速
                        mapSportSpeeddTv.setText(decimalFormat.format( currDisance / 1) +" m/s");
                        //平均速度
                        mapSportAvgSpeedTv.setText(decimalFormat.format((currDisance/1000) / (count * 3600)) +" m/s");
                    }

                    //卡路里
                    mapSportKcalTv.setText(decimalFormat.format((currDisance/1000) * 65.4) +" kcal");
                    break;
            }
        }
    };







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_sport_layout);
        ButterKnife.bind(this);
        amapMapView.onCreate(savedInstanceState);

        initViews();

        sportType = getIntent().getIntExtra("sport_type",0);

        if (aMap == null) {
            aMap = amapMapView.getMap();
            setUpMap(aMap);
        }

    }

    //设置界面相关属性
    private void setUpMap(AMap mAmap) {
        String language = Locale.getDefault().getLanguage();

        mAmap.setMapLanguage(language.equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_gps_point));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.getUiSettings().setZoomControlsEnabled(false);
        mAmap.setLocationSource(this);// 设置定位监听
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(amapMapView != null)
            amapMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (amapMapView != null)
            amapMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (amapMapView != null)
            amapMapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (amapMapView != null)
            amapMapView.onDestroy();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.move_ment));
    }

    @OnClick({R.id.commentB30BackImg, R.id.mapSportCountTimeTv,
            R.id.amapSportStartBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                alertStopSport();
                break;
            case R.id.mapSportCountTimeTv:
                String userId = (String) SharedPreferencesUtils.readObject(AmapSportActivity.this,Commont.USER_ID_DATA);
                LitePal.deleteAll(AmapSportBean.class,"userId = ?",userId);
                break;
            case R.id.amapSportStartBtn:    //开始或停止的按钮
                startOrEndSport();
                break;
        }
    }

    private void alertStopSport(){
        if(!startOrEnd){
            finish();
            return;
        }


        alertDialog = new AlertDialog.Builder(AmapSportActivity.this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.save_record)+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startOrEndSport();

                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();

    }



    //开始或停止运动
    private void startOrEndSport() {
        if (!startOrEnd) {    //开始
            startOrEnd = true;
            amapSportStartBtn.setText(getResources().getString(R.string.suspend));
            if (mlocationClient != null)
                mlocationClient.startLocation();
            timeThread = new TimeThread();
            timeThread.start();
        } else {  //停止
            startOrEnd = false;
            amapSportStartBtn.setText(getResources().getString(R.string.star));
            if (mlocationClient != null)
                mlocationClient.stopLocation();
            addEndMark();
            saveSportData();
        }
    }


    //保存运动数据
    private void saveSportData() {
        if (tempTraceList.size() == 0)
            return;
        String bleMac = WatchUtils.getSherpBleMac(AmapSportActivity.this);
        String userId = (String) SharedPreferencesUtils.readObject(AmapSportActivity.this,Commont.USER_ID_DATA);
        if(WatchUtils.isEmpty(userId))
            return;
        //卡路里
        String saveKcal = mapSportKcalTv.getText().toString();
        //总里程
        String countDis = mapSportCountDisTv.getText().toString().trim();
        String tmpCountDis = StringUtils.substringBefore(countDis," ");

        if(tmpCountDis.equals("0"))
            return;
        //总时间
        String countTime = mapSportCountTimeTv.getText().toString().trim();
        //平均配速
        String avgSpeed = mapSportAvgSpeedTv.getText().toString();
        String tmpAvgSpeed = StringUtils.substringBefore(avgSpeed," ").trim();


        AmapSportBean amapSportBean = new AmapSportBean();
         amapSportBean.setRtc(WatchUtils.getCurrentDate());
         amapSportBean.setDetailTime(WatchUtils.getCurrentDate2());
         amapSportBean.setBleMac(bleMac);
         amapSportBean.setUserId(userId);
         amapSportBean.setKcal(StringUtils.substringBefore(saveKcal," ").trim());
         amapSportBean.setCountDisance(isUnit ? tmpCountDis : WatchUtils.miToKm(Double.valueOf(tmpCountDis))+"");
         amapSportBean.setCountTime(StringUtils.substringBefore(countTime," "));
         amapSportBean.setAvgSpeed(isUnit ? tmpAvgSpeed : WatchUtils.miToKm(Double.valueOf(tmpAvgSpeed))+"");
         amapSportBean.setAmapTraceStr(new Gson().toJson(tempTraceList));
         amapSportBean.setSportType(sportType);
         amapSportBean.save();
    }


    //定位成功回调
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() != 0) {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                ToastUtil.showToast(AmapSportActivity.this, errText);
                return;
            }
            if (!mlocationClient.isStarted())
                return;
            mListener.onLocationChanged(aMapLocation);
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

            //开始运动
            if (startOrEnd) {
                resultList.add(latLng);
                addStartMark();
                //绘制轨迹
                aMap.addPolyline((new PolylineOptions()).addAll(resultList).width(8f).color(Color.BLUE));
                //记录轨迹和轨迹纠偏数据
                AmapTraceLocation amapTraceLocation = new AmapTraceLocation(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude(), aMapLocation.getSpeed(), aMapLocation.getBearing(),
                        aMapLocation.getTime());
                tempTraceList.add(amapTraceLocation);


                Message message = handler.obtainMessage();
                message.what = 1002;
                message.obj = aMapLocation.getSpeed();
                handler.sendMessage(message);

            }


        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(AmapSportActivity.this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //定位参数
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setInterval(1000 * 10);
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }
    }

    //销毁定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        tempTraceList.clear();

    }

    //开始运动的标记
    private void addStartMark() {
        if (resultList.size() > 0) {
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_pistion))
                    .position(resultList.get(0))
                    .draggable(true);
            startMark = aMap.addMarker(markerOptions);
        }

    }

    //结束运动的标记
    private void addEndMark() {
        if (resultList.size() > 0) {
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_pistion))
                    .position(resultList.get(resultList.size() - 1))
                    .draggable(true);
            endMark = aMap.addMarker(markerOptions);
        }
    }

    //记录运动时间
    private class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (startOrEnd){
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(1001);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                alertStopSport();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
