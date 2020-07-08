package com.example.bozhilun.android.bzlmaps.gaodemaps;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AmapSportBean;
import com.example.bozhilun.android.bean.AmapTraceLocation;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/10/18
 */
public class AmapHistorySportActivity extends WatchBaseActivity implements LocationSource,
        AMapLocationListener, TraceListener {

    private static final String TAG = "AmapHistorySportActivit";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.amapMapView)
    MapView amapMapView;
    @BindView(R.id.amapSportStartBtn)
    Button amapSportStartBtn;
    //总里程
    @BindView(R.id.mapSportCountDisTv)
    TextView mapSportCountDisTv;
    //平均速度
    @BindView(R.id.mapSportAvgSpeedTv)
    TextView mapSportAvgSpeedTv;
    //当前时速
    @BindView(R.id.mapSportSpeeddTv)
    TextView mapSportSpeeddTv;
    //运动时长
    @BindView(R.id.mapSportCountTimeTv)
    TextView mapSportCountTimeTv;
    //卡路里
    @BindView(R.id.mapSportKcalTv)
    TextView mapSportKcalTv;

    private Marker startMark, endMark;
    Polyline polyline;

    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption = null;

    List<AmapTraceLocation> traceLocationList;

    private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<>();
    private int mSequenceLineID = 1000;
    private LBSTraceClient mTraceClient;

    private int mCoordinateType = LBSTraceClient.TYPE_AMAP;

    //轨迹修正
    PathSmoothTool mpathSmoothTool;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_sport_layout);
        ButterKnife.bind(this);
        amapMapView.onCreate(savedInstanceState);

        initViews();

        if (aMap == null) {
            aMap = amapMapView.getMap();
            setUpMap();
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        amapSportStartBtn.setVisibility(View.GONE);
        commentB30TitleTv.setText(getResources().getString(R.string.move_ment));
    }


    @Override
    protected void onStart() {
        super.onStart();

        findSavedSportData();

    }

    @SuppressLint("SetTextI18n")
    private void findSavedSportData() {

        String str = getIntent().getStringExtra("sport_str");
        if (str == null)
            return;
        AmapSportBean amapSportBean = new Gson().fromJson(str, AmapSportBean.class);
        commentB30TitleTv.setText(amapSportBean.getSportType() == 0 ? getResources().getString(R.string.outdoor_running) : getResources().getString(R.string.outdoor_cycling));
        //Log.e(TAG,"----查询数据库="+amapSportBean.toString());
        String traListStr = amapSportBean.getAmapTraceStr();
        if (WatchUtils.isEmpty(traListStr))
            return;
        traceLocationList = new Gson().fromJson(traListStr, new TypeToken<List<AmapTraceLocation>>() {
        }.getType());
        if (traceLocationList == null)
            return;

        try {
            boolean isUnit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            mapSportCountDisTv.setText(amapSportBean.getCountDisance() + (isUnit ? " km" : " mile"));
            mapSportAvgSpeedTv.setText(amapSportBean.getAvgSpeed() + (isUnit ? " km/h" : " mile/h"));
            mapSportCountTimeTv.setText(amapSportBean.getCountTime());
            mapSportKcalTv.setText(amapSportBean.getKcal() + " kcal");
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<LatLng> latLngList = getLanList(traceLocationList);

        //开始和结束的位置标记
        LatLng startLng = latLngList.get(0);
        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.fit_start_point))
                .position(startLng)
                .draggable(true);
        startMark = aMap.addMarker(markerOptions);
        startMark.setDraggable(false);


        LatLng endLng = latLngList.get(latLngList.size() - 1);
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.fit_end_point))
                .position(endLng)
                .draggable(true);
        endMark = aMap.addMarker(markerOption);
        endMark.setDraggable(false);

        //startTrace();


        mpathSmoothTool = new PathSmoothTool();
        //设置平滑等级
        mpathSmoothTool.setIntensity(4);
        List<LatLng> pathoptimizeList = mpathSmoothTool.pathOptimize(latLngList);
        if (pathoptimizeList == null)
            return;
        polyline = aMap.addPolyline(new PolylineOptions().addAll(pathoptimizeList).color(Color.GREEN).width(8f));

    }

    //开始轨迹纠偏
    private void startTrace() {
        if (mOverlayList.containsKey(mSequenceLineID)) {
            TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
            overlay.zoopToSpan();
            int status = overlay.getTraceStatus();
            String tipString = "";
            if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
                tipString = "该线路轨迹纠偏进行中...";
                //setDistanceWaitInfo(overlay);
            } else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
                //setDistanceWaitInfo(overlay);
                tipString = "该线路轨迹已完成";
            } else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
                tipString = "该线路轨迹失败";
            } else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
                tipString = "该线路轨迹纠偏已经开始";
            }
            Toast.makeText(this.getApplicationContext(), tipString,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        TraceOverlay mTraceOverlay = new TraceOverlay(aMap);
        mOverlayList.put(mSequenceLineID, mTraceOverlay);
        List<LatLng> mapList = getLanList(traceLocationList);
        mTraceOverlay.setProperCamera(mapList);
//        mResultShow.setText(mDistanceString);
//        mLowSpeedShow.setText(mStopTimeString);
        mTraceClient = new LBSTraceClient(AmapHistorySportActivity.this);
        mTraceClient.queryProcessedTrace(mSequenceLineID, getTraceLocationList(),
                mCoordinateType, this);


    }


    private List<TraceLocation> getTraceLocationList() {
        List<TraceLocation> lt = new ArrayList<>();
        for (AmapTraceLocation at : traceLocationList) {
            TraceLocation tt = new TraceLocation(at.getLatitude(), at.getLongitude(),
                    at.getBearing(), at.getSpeed(), at.getTime());
            lt.add(tt);
        }
        return lt;


    }

    private List<LatLng> getLanList(List<AmapTraceLocation> tl) {
        List<LatLng> latLngList = new ArrayList<>();
        for (AmapTraceLocation traceLocation : tl) {
            latLngList.add(new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude()));
        }
        return latLngList;
    }


    //设置属性
    private void setUpMap() {
        String language = Locale.getDefault().getLanguage();

        aMap.setMapLanguage(language.equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_gps_point));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
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


    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();

    }

    //定位成功回调
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(AmapHistorySportActivity.this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //定位参数
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setInterval(50 * 1000);
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
    }


    //轨迹纠偏失败
    @Override
    public void onRequestFailed(int lineID, String s) {
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
            //wsetDistanceWaitInfo(overlay);
        }
    }

    //轨迹纠偏进行中
    @Override
    public void onTraceProcessing(int lineID, int i1, List<LatLng> list) {
        if (list == null)
            return;
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
            overlay.add(list);
        }
    }

    //轨迹纠偏结束
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance,
                           int watingtime) {
        Toast.makeText(this.getApplicationContext(), "onFinished",
                Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
            overlay.setDistance(distance);
            overlay.setWaitTime(watingtime);
            //setDistanceWaitInfo(overlay);
        }
    }

}
