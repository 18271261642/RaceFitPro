package com.example.bozhilun.android.bzlmaps.gaodemaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afa.tourism.greendao.gen.SportMapsDao;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.BzlDragView;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.bzlmaps.MyCountTimer;
import com.example.bozhilun.android.bzlmaps.mapdb.LatLonBean;
import com.example.bozhilun.android.bzlmaps.mapdb.SportMaps;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BzlGaoDeActivity extends AppCompatActivity implements AMapLocationListener,
        LocationSource, Chronometer.OnChronometerTickListener, BzlDragView.BzlDragViewListenter {
    private final String TAG = "-------GPS>>>";
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private MapView mMapView = null;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;//实现定位蓝点
    private OnLocationChangedListener mListener;
    private final int MY_PERMISSION_REQUEST_CODE = 1;
    private boolean isPermit = true;//是否允许权限

    private TextView textTimeData = null;
    private Chronometer sportTime = null;
    private TextView sportDistance = null;//运动距离---KM
    private TextView sportShisu = null;//运动时速---KM/H
    private TextView sportSpeed = null;//运动配速
    private TextView sportKcal = null;//运动卡路里
    private TextView timeDowln = null;//倒计时
    private FrameLayout timr_frame;
    private int times = 0;//运动计时 s
    private BzlDragView bzlDragView;
    private TextView sport_distance_unti, util_shisu;//单位  km--ft=====km/h---FT/h


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_bzl_gao_de);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        initView();
    }

    private void initView() {

        bzlDragView = new BzlDragView(BzlGaoDeActivity.this, getResources().getString(R.string.star));
        bzlDragView.setBzlDragViewListenter(this);
        bzlDragView.setIsDrag(false);
        textTimeData = findViewById(R.id.text_time_data);
        sportTime = findViewById(R.id.sport_times);
//        sportTime.setOnChronometerTickListener(this);//设置计时器监听

        sportDistance = findViewById(R.id.sport_distance);
        sportShisu = findViewById(R.id.sport_shisu);
        sportSpeed = findViewById(R.id.sport_speed);
        sportKcal = findViewById(R.id.sport_kcal);
        timeDowln = findViewById(R.id.time_dowln);
        timr_frame = findViewById(R.id.timr_frame);
        sport_distance_unti = findViewById(R.id.sport_distance_unti);
        util_shisu = findViewById(R.id.util_shisu);
        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
            // 0位公制 1为英制
            if (h9_step_util == 0) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("MI");
            }

        } else {
            if (w30sunit) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("FT");
            }
        }
        if (myCountTimer == null) {
            myCountTimer =
                    new MyCountTimer(4000, 1000, timeDowln, getResources().getString(R.string.string_sport_run));
            //倒计时监听
            myCountTimer.setmCompletionTime(completionTime);
        }
    }


    private UiSettings mUiSettings;


    /***
     * *****************************************************************权限
     */
    private void checkPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }
        );
        // 如果这些权限全都拥有;

    }

    /**
     * 检查是否拥有指定的所有权限
     *
     * @param permissions
     * @return
     */
    private void checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                isPermit = false;
                //进入到这里代表没有权限.
                ActivityCompat.requestPermissions(this, new String[]{permission}, MY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    for (int grantResult :
                            grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.shouquanshib));
//                            finish();
                            isPermit = false;
                        } else {
                            ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.shouquancg));
                            isPermit = true;
                        }
                        return;
                    }
                }

                break;
        }
    }


    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            //初始化地图控制器对象
            aMap = mMapView.getMap();
        }

        //定位
        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        //mUiSettings.setScaleControlsEnabled(true);//设置是否显示比例尺
        //mUiSettings.setCompassEnabled(true);//设置是否显示指南针
        //mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//缩放按钮位置

        /**
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
         * //以下三种模式从5.1.0版本开始提供
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
         * myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
         */
        myLocationStyle = new MyLocationStyle();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_point);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)//定位、但不会移动到地图中心点，并且会跟随设备移动。
                .showMyLocation(true)//是否显示小蓝点
                .strokeColor(Color.TRANSPARENT)
                .radiusFillColor(Color.TRANSPARENT)
//                .strokeColor(Color.parseColor("#7d41b692"))
                .radiusFillColor(Color.parseColor("#3641b692"))
                .myLocationIcon(bitmap)
                .interval(1000);//定位时间间隔1秒
        aMap.setMyLocationStyle(myLocationStyle);

        //画线
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);
        AMapOptions options = new AMapOptions();
        options.scrollGesturesEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        gpsJSon = new com.alibaba.fastjson.JSONObject();
        /**查询当前的运动类型*/
        try {
            if (!TextUtils.isEmpty((String) SharedPreferencesUtils.readObject(BzlGaoDeActivity.this, "type"))) {
                if ("0".equals(SharedPreferencesUtils.readObject(BzlGaoDeActivity.this, "type"))) {
                    gpsJSon.put("type", 0);
                    sportMaps.setType(0);
                    latLonBean.setType(0);
//                    Title.setText(getResources().getString(R.string.outdoor_running));
                } else {
                    gpsJSon.put("type", 1);
                    sportMaps.setType(1);
                    latLonBean.setType(1);
//                    Title.setText(getResources().getString(R.string.outdoor_cycling));
                }
            } else {
                gpsJSon.put("type", 0);
                sportMaps.setType(0);
//                Title.setText(getResources().getString(R.string.outdoor_running));
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    //以前的定位点
    private LatLng oldLatLng;
    //现在定位点
    private LatLng curLatLng;
    //是否是第一次定位
    private boolean isFirstLatLng = true;
    private boolean isStart = false;//是否开始定位
    private boolean isEnd = false;//是否结束定位
    private double mLocatinLat;
    private double mLocationLon;
    private ArrayList<LatLng> mLocationList = new ArrayList<>();//定位坐标集合

    /**
     * 声明定位回调监听器
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                mLocatinLat = aMapLocation.getLatitude();
                mLocationLon = aMapLocation.getLongitude();

                //设置显示地图时间
                if (textTimeData != null) {
                    //获取地图返回的时间戳转换成时间设置显示
                    long time = aMapLocation.getTime();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    textTimeData.setVisibility(View.VISIBLE);
                    textTimeData.setText(format.format(new Date(time)).trim());
                }
                if (isStart) {
                    //判断是否是第一次定位
                    if (isFirstLatLng) {
                        isFirstLatLng = false;
                        //移动到定位点中心，并且缩放级别为18
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatinLat, mLocationLon), 18));
                        if (mLocationClient.isStarted()) {

                            //添加起点
                            startDisplayPerth(new LatLng(mLocatinLat, mLocationLon));

                            try {
                                com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                                beanList.put("lat", mLocatinLat);
                                beanList.put("lon", mLocationLon);
                                gpsMaps.add(beanList);
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                        mLocationList.add(new LatLng(mLocatinLat, mLocationLon));

                    } else {
                        if (oldLatLng == null) {
                            oldLatLng = new LatLng(mLocatinLat, mLocationLon);
                        } else {
                            //Log.e(TAG, aMapLocation.getLatitude() + "====" + aMapLocation.getLongitude());
                            float speed = aMapLocation.getSpeed();
                            //Toast.makeText(BzlGaoDeActivity.this, "" + speed, Toast.LENGTH_SHORT);
                            if (isStart) {
                                findBest(speed);
                            }
                        }
                    }
                } else {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatinLat, mLocationLon), 18));
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                Log.e(TAG, "location Error, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo());
            }
        }

    }


    private boolean mOver = false;

    private double currLength;
    private double mBestLat;
    private double mBestLon;
    private long lastTime = 0;
    private long currTime = 0;
    private long minusTime;
    private int errorCnt = 0;
    private LatLng currLa;
    private LatLng lastLa = new LatLng(0, 0);
    private LatLng overLa = new LatLng(0, 0);
    private double gpsDistance = 0.0;//距离

    @SuppressLint("SetTextI18n")
    private void findBest(float speed) {

        currLa = new LatLng(mLocatinLat, mLocationLon);

        currTime = System.currentTimeMillis();
        //Log.e(TAG, "test walk la is" + currLa + "");
        //Log.e(TAG, "test walk last is" + lastLa + "");
        currLength = AMapUtils.calculateLineDistance(lastLa, currLa);

        if (!lastLa.equals(currLa)) {
            minusTime = currTime - lastTime;

            //Log.e(TAG, "yhy time testzzz" + minusTime);

            if (currLength < ((minusTime + 1) / 1000) * 5) {
                errorCnt = 0;
                lastLa = currLa;
                lastTime = currTime;

                mBestLat = mLocatinLat;
                mBestLon = mLocationLon;
                curLatLng = new LatLng(mBestLat, mBestLon);
                //Log.e(TAG, "发>>>>>>>>>" + currLength);
                mLocationList.add(curLatLng);
                if (mLocationList.size() >= 2) {
                    DPoint dPoint1 = new DPoint(mLocationList.get(mLocationList.size() - 2).latitude, mLocationList.get(mLocationList.size() - 2).longitude);
                    DPoint dPoint2 = new DPoint(mLocationList.get(mLocationList.size() - 1).latitude, mLocationList.get(mLocationList.size() - 1).longitude);
                    gpsDistance += CoordinateConverter.calculateLineDistance(dPoint1, dPoint2) * 0.001;
                    //添加线
                    addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));

                    try {
                        com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                        beanList.put("lat", mLocatinLat);
                        beanList.put("lon", mLocationLon);
                        gpsMaps.add(beanList);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
                DecimalFormat decimalFormat = new DecimalFormat("######0.00");
                //距离
                if (sportDistance != null) {
                    boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                    int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
                    if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                            !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
                        // 0位公制 1为英制
                        if (h9_step_util == 0) {
                            sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance)));
                            sport_distance_unti.setText("KM");
                        } else {
                            //总运动距离
                            sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance * 0.0006214)));
                            sport_distance_unti.setText("MI");
                        }
                    } else {
                        //总公里数
                        if (w30sunit) {
                            sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance)));
                            sport_distance_unti.setText("KM");
                        } else {
                            //总运动距离
                            sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance * 3.28)));
                            sport_distance_unti.setText("FT");
                        }
                    }
                }
                //时速
                if (sportShisu != null) {
                    try {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                        Date dates = dff.parse(sportTime.getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTime(dates);

                        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
                        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                                !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
                            // 0位公制 1为英制
                            if (h9_step_util == 0) {
                                if (gpsDistance <= 0.0) {
                                    sportShisu.setText("0.0");
                                } else {
                                    sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(gpsDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                                }
                                util_shisu.setText("km/h");
                            } else {
                                //总运动距离
                                if (gpsDistance <= 0.0) {
                                    sportShisu.setText("0.0");
                                } else {
                                    sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(gpsDistance * 0.0006214)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                                }
                                util_shisu.setText("mi/h");
                            }
                        } else {
                            if (w30sunit) {
                                if (gpsDistance <= 0.0) {
                                    sportShisu.setText("0.0");
                                } else {
                                    sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(gpsDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                                }
                                util_shisu.setText("km/h");
                            } else {
                                //总运动距离
                                if (gpsDistance <= 0.0) {
                                    sportShisu.setText("0.0");
                                } else {
                                    sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(gpsDistance * 3.28)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                                }
                                util_shisu.setText("ft/h");
                            }
                        }
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }

                }
                //配速
                if (sportSpeed != null) {
                    //   *30/10  == *3.6
//                sportSpeed.setText(String.valueOf(decimalFormat.format(gpsDistance * 3.6)));
                    sportSpeed.setText(String.valueOf(speed));
                }
                //卡路里
                if (sportKcal != null) {
                    sportKcal.setText(String.valueOf(decimalFormat.format(gpsDistance * 65.4)) + "kcal");
                }
                //Log.e(TAG, "距离 == " + gpsDistance);
            } else if (minusTime >= 20000) {
                if (mOver) {

                    if (!overLa.equals(currLa)) {
                        errorCnt = 0;
                        lastLa = currLa;
                        lastTime = currTime;
                        //Log.e(TAG, " 确定大于距离>>>>>>>>>" + currLength);

                        mBestLat = mLocatinLat;
                        mBestLon = mLocationLon;

                        curLatLng = new LatLng(mBestLat, mBestLon);
                        mLocationList.add(curLatLng);
                        mOver = false;
                    } else {
                        errorCnt = 0;
                        lastTime = currTime;
                        mOver = false;
                    }
                } else {
                    if (currLength > ((minusTime + 1) / 1000) * 5) {
                        mOver = true;
                        overLa = currLa;
                        //Log.e(TAG, " 第一次大于距离" + currLength);
                    }
                }

            } else {
                errorCnt++;
                //Log.e(TAG, " +++++++++++++++++++++++++++++++++++++++++++++距离太大，是漂移，不发" + currLength + "定位是" + mLocatinLat + "^^^" + mLocationLon);
            }

        } else {
            //Log.e(TAG, " -------------------------------------------------距离太小，没有移动，不发");
            lastTime = currTime;
        }
    }

    private Marker starPerth, endPerth;

    /**
     * 添加 开始 标记
     */
    private void startDisplayPerth(LatLng latLng) {

        //每一次打点第一个的时候就是定位开始的时候
        //定位开始的时间
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        try {
            //得到开始时间
            gpsJSon.put("startTime", format.format(Calendar.getInstance().getTime()));
            sportMaps.setStartTime(format.format(Calendar.getInstance().getTime()));
        } catch (Exception E) {
            E.printStackTrace();
        }


        if (starPerth == null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_pistion);
            starPerth = aMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));//.title("start")
            starPerth.setDraggable(false); //设置不可移动
        }
    }


    /**
     * 添加 结束标记
     */
    private void endDisplayPerth(LatLng latLng) {
        if (endPerth == null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end_pistion);
            endPerth = aMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));//.title("end")
            endPerth.setDraggable(false); //设置不可移动
        }
    }


    /**
     * 绘制线 两点一线
     *
     * @param oldPointLatLng
     * @param newPointLatLng
     */
    void addPointLins(LatLng oldPointLatLng, LatLng newPointLatLng) {
        aMap.addPolyline(new PolylineOptions()
                .color(Color.RED)
                .width(10f)
                .geodesic(true)
                .add(oldPointLatLng, newPointLatLng));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        if(mMapView != null){
            mMapView.onResume();
        }

        if (isPermit) {
            init();
        }
        if (mLocationClient != null) mLocationClient.startLocation();
        if (bzlDragView != null) {
            if (!isEnd) bzlDragView.showDragCallView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        if(mMapView != null){
            mMapView.onPause();
        }

        if (bzlDragView != null) {
            bzlDragView.hideDragCallView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deactivate();
        if (mMapView != null) {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mMapView.onDestroy();

        }
        if (null != mLocationClient) {
            //销毁定位客户端，同时销毁本地定位服务。
            mLocationClient.onDestroy();
        }
        if (gpsMaps != null) {
            gpsMaps.clear();
            gpsMaps = null;
        }
        if (gpsJSon != null) {
            gpsJSon.clear();
            gpsJSon = null;
        }
        if (mMapView != null) {
            mMapView = null;
        }
        if (starPerth != null) {
            starPerth.remove();
            starPerth = null;
        }
        if (endPerth != null) {
            endPerth.remove();
            endPerth = null;
        }
    }


    /**
     * 定位监听------- 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            if (mLocationClient != null) {
                mLocationClient.startLocation();

            }
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
//            mLocationOption.setInterval(1000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        }
    }

    @Override
    public void deactivate() {

    }

    /**
     * 计时监听
     *
     * @param chronometer
     */
    @Override
    public void onChronometerTick(Chronometer chronometer) {
        if (mLocationClient.isStarted()) {
            times++;
            if (sportTime != null) sportTime.setText(FormatMiss(times));
        }
        try {//得到时间
            gpsJSon.put("timeLen", FormatMiss(times));
            sportMaps.setTimeLen(FormatMiss(times));
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    // 成员变量mToast
    private Toast mToast;

    /**
     * 按钮监听------包含开始暂停----下滑结束（下滑必须超出屏幕才会执行）
     */
    @Override
    public void OnBzlDragViewListenter() {

        if (mLocationList != null) {
            if (mLocationList.size() >= 2) {

                if (sportDistance != null) {
                    String discanceStr = sportDistance.getText().toString().trim();
                    if (TextUtils.isEmpty(discanceStr)
                            || discanceStr.equals("0.0")
                            || discanceStr.equals("0.00")
                            || discanceStr.equals("0.000")) {
//                        ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.string_no_sport));

                        // 显示Toast
                        if (mToast == null) {
                            mToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.string_no_sport), Toast.LENGTH_SHORT);
                        } else {
                            mToast.setText(getResources().getString(R.string.string_no_sport));
                            mToast.setDuration(Toast.LENGTH_SHORT);
                        }
                        mToast.show();
                        if (mHandler != null) mHandler.sendEmptyMessageAtTime(0x01, 5000);
                        return;
                    }
                }

                new CommomDialog(BzlGaoDeActivity.this, R.style.dialog, getResources().getString(R.string.save_record) + "?", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            if (mLocationList.get(mLocationList.size() - 1).longitude != 0 && mLocationList.get(mLocationList.size() - 1).latitude != 0) {
                                addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));
                                endDisplayPerth(mLocationList.get(mLocationList.size() - 1));//添加结束点
                            }
                            isEnd = true;
//                            sportTime.stop();//计时停止
                            if (mHandler != null) mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;
                            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                            isStart = false;
                            upDataGPS();
                            bzlDragView.hideDragCallView();
                        }
                        dialog.dismiss();
                        bzlDragView.setDraging(false);
                    }
                }).setTitle(getResources().getString(R.string.prompt)).show();
            } else {
                // 显示Toast
                if (mToast == null) {
                    mToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.string_no_sport), Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(getResources().getString(R.string.string_no_sport));
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
//                ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.string_no_sport));
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                if (mHandler != null) mHandler.sendEmptyMessageAtTime(0x01, 5000);
                if (mLocationList.size() < 1)
                    return;
//                addPointLins(mLocationList.get(mLocationList.size() - 1), mLocationList.get(mLocationList.size() - 1));
//                endDisplayPerth(mLocationList.get(mLocationList.size() - 1));//添加结束点
            }

        }

    }


    //    private DialogSubscriber dialogSubscriber;
//    private static String uil = "http://apis.berace.com.cn/watch/sport/upOutdoorSport";//上传轨迹
    List gpsMaps = new ArrayList();
    com.alibaba.fastjson.JSONObject gpsJSon;


    SportMaps sportMaps = new SportMaps();
    LatLonBean latLonBean = new LatLonBean();

    /**
     * 上传经纬度数据
     */
    public void upDataGPS() {


        try {
            //路程
            String discanceStr = sportDistance.getText().toString().trim();
            //速度
//            String shisuStr = sportShisu.getText().toString().trim();
            //取得时速
            String speedSStr = "0.0";
            if (sportDistance != null) {
                speedSStr = sportShisu.getText().toString().trim();
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            gpsJSon.put("rtc", df.format(new Date()));
//            gpsJSon.put("speed", speedSStr.equals("00'00") || TextUtils.isEmpty(speedSStr) ? "0.0" : Double.valueOf(speedSStr));
//            //取得公里
//            gpsJSon.put("distance", discanceStr.equals("0.00") ? "0.00" : Double.valueOf(Double.parseDouble(sportDistance.getText().toString())));
//            gpsJSon.put("userId", SharedPreferencesUtils.readObject(BzlGaoDeActivity.this, "userId"));
            DecimalFormat decimalFormat = new DecimalFormat("######0.00");
//            gpsJSon.put("calories", Double.valueOf(decimalFormat.format(gpsDistance * 65.4)));


            /****  保存数据库  ***/
            @SuppressLint("SimpleDateFormat") DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sportMaps.setRtc(df.format(new Date()));
            sportMaps.setSaveTime(df1.format(new Date()));
            sportMaps.setSpeed(speedSStr.equals("00'00") || TextUtils.isEmpty(speedSStr) ? "0.0" : "" + Double.valueOf(speedSStr));
            if (sportDistance != null) {
                sportMaps.setDistance(discanceStr.equals("0.00") ? "0.00" : "" + Double.valueOf(Double.parseDouble(sportDistance.getText().toString())));
            } else {
                sportMaps.setDistance("0.00");
            }

            //String bleMac = (String) SharedPreferenceUtil.get(BzlGaoDeActivity.this, Commont.BLEMAC, "");
            String bleMac = (String) SharedPreferencesUtils.readObject(BzlGaoDeActivity.this, Commont.BLEMAC);
            String userId = (String) SharedPreferencesUtils.readObject(BzlGaoDeActivity.this, "userId");
            if (WatchUtils.isEmpty(bleMac)) return;
            sportMaps.setUserId(WatchUtils.isEmpty(userId) ? "null" : userId);
            sportMaps.setCalories(Double.valueOf(decimalFormat.format(gpsDistance * 65.4)) + "");
            sportMaps.setMac(bleMac);
            sportMaps.setLatLons(new Gson().toJson(gpsMaps));

            List<SportMaps> sportMapsList = MyApp.getInstance().getDBManager().getDaoSession().getSportMapsDao().loadAll();
            if (sportMapsList == null || sportMapsList.size() <= 0) {
                MyApp.getInstance().getDBManager().getDaoSession().getSportMapsDao().insert(sportMaps);
            } else {
                SportMaps timedata = MyApp.getInstance().getDBManager().getDaoSession().getSportMapsDao().queryBuilder()
                        .where(SportMapsDao.Properties.SaveTime.eq(df1.format(new Date()))).unique();
                if (timedata == null) {
                    MyApp.getInstance().getDBManager().getDaoSession().getSportMapsDao().insert(sportMaps);
                }
            }

            latLonBean.setUserId(TextUtils.isEmpty(userId) ? "null" : userId);
            latLonBean.setRtc(df.format(new Date()));
            latLonBean.setSaveTime(df1.format(new Date()));
            latLonBean.setMac(bleMac);
            latLonBean.setLatLons(new Gson().toJson(gpsMaps));


            MyApp.getInstance().getDBManager().getDaoSession().getLatLonBeanDao().insert(latLonBean);


        } catch (Exception E) {
            E.printStackTrace();
        }

//        //判断网络是否连接
//        if (!ConnectManages.isNetworkAvailable(BzlGaoDeActivity.this)) {
//            ToastUtil.showToast(BzlGaoDeActivity.this, getResources().getString(R.string.string_not_net));
//        } else {
//            try {
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("outdoorSports", gpsJSon);
//                map.put("latLons", gpsMaps);
//                String mapjson = JSON.toJSONString(map);
//                //MyLogUtil.i("latLons" + mapjson);
//                dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, BzlGaoDeActivity.this);
//                OkHttpObservable.getInstance().getData(dialogSubscriber, uil, mapjson);
//            } catch (Exception E) {
//                E.printStackTrace();
//            }
//        }
    }

//
//    /**
//     * 网络请求返回
//     */
//    private SubscriberOnNextListener<String> subscriberOnNextListener = new SubscriberOnNextListener<String>() {
//        @Override
//        public void onNext(String result) {
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                String resultCode = jsonObject.getString("resultCode");
//                //MyLogUtil.i("response" + resultCode);
//                if ("001".equals(resultCode)) {
//                    ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.data_upload));
//                } else {
//                    ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.data_upload_fail));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };

    MyCountTimer myCountTimer = null;

    @Override
    public void OnClickBzlDragViewListenter() {
        if (!isStart) {//没开始时候开始
            isStart = true;
            if (timeDowln == null || timr_frame == null) return;
            timeDowln.setEnabled(false);
            timr_frame.setEnabled(false);
            timr_frame.setVisibility(View.VISIBLE);
            timeDowln.setVisibility(View.VISIBLE);//隐藏倒计时控件


            //三秒倒计时
            if (myCountTimer == null) {
                myCountTimer =
                        new MyCountTimer(4000, 1000, timeDowln, getResources().getString(R.string.string_sport_run));
                //倒计时监听
                myCountTimer.setmCompletionTime(completionTime);
            }
            myCountTimer.start();

        } else {
            ToastUtil.showShort(BzlGaoDeActivity.this, getResources().getString(R.string.string_stop_run_dowln));
        }
    }


    /**
     * 倒计时监听
     */
    MyCountTimer.CompletionTime completionTime = new MyCountTimer.CompletionTime() {
        @Override
        public void OnCompletionTime() {
            //倒计时完成时执行开始定位
            timeDowln.setVisibility(View.GONE);//隐藏倒计时控件
            timr_frame.setVisibility(View.GONE);

//            sportTime.start();//定位时间计时开始
//            sportTime.setBase(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime()从开机到现在的毫秒数（手机睡眠的时间也包括在内）

            if (mRunnable == null) {
                mRunnable = new MyRunnable();
            }
            if (mHandler != null) mHandler.postDelayed(mRunnable, 0);
            if (mLocationClient != null) mLocationClient.startLocation();//开始定位更新
            if (bzlDragView != null) {
                bzlDragView.setIsDrag(true);
                bzlDragView.setText(getResources().getString(R.string.stop));
            }

        }
    };

    /**
     * 监听Back键按下事件
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isStart) {
                new CommomDialog(BzlGaoDeActivity.this, R.style.dialog,
                        getResources().getString(R.string.string_out_gps), new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        try {
                            if (confirm) {
                                isEnd = true;
//                                if(sportTime != null && sportTime.isActivated()){
//                                    sportTime.stop();//计时停止
//                                }

                                if (mHandler != null) mHandler.removeCallbacks(mRunnable);
                                mRunnable = null;
                                if (mLocationClient!=null&&mLocationClient.isStarted()){
                                    //Log.e(TAG,"=体制定位前，判断师傅开启过定位=="+mLocationClient.isStarted());
                                    mLocationClient.stopLocation();//停止定位更新
                                }
                                isStart = false;
                                bzlDragView.hideDragCallView();
                                bzlDragView.setDraging(false);
                                finish();


                                if (mMapView != null) {
                                    //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
                                    mMapView.onDestroy();

                                }
//                                if (null != mLocationClient) {
//                                    //销毁定位客户端，同时销毁本地定位服务。
//                                    mLocationClient.onDestroy();
//                                }
//                                if (gpsMaps != null) {
//                                    gpsMaps.clear();
//                                    gpsMaps = null;
//                                }
//                                if (gpsJSon != null) {
//                                    gpsJSon.clear();
//                                    gpsJSon = null;
//                                }
//                                if (mMapView != null) {
//                                    mMapView = null;
//                                }
//                                if (starPerth != null) {
//                                    starPerth.remove();
//                                    starPerth = null;
//                                }
//                                if (endPerth != null) {
//                                    endPerth.remove();
//                                    endPerth = null;
//                                }

                            }

                            dialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).setTitle(getResources().getString(R.string.prompt)).show();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    MyRunnable mRunnable = null;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                if (bzlDragView != null) bzlDragView.setDraging(false);
            }
        }
    };

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            times++;
            if (times == 86401) times = 0;
            if (sportTime != null) sportTime.setText(FormatMiss(times));
            try {//得到时间
                Log.d("===========", FormatMiss(times));
                gpsJSon.put("timeLen", FormatMiss(times));
                sportMaps.setTimeLen(FormatMiss(times));
            } catch (Exception E) {
                E.printStackTrace();
            }
            if (mHandler != null) mHandler.postDelayed(this, 1000);
        }
    }


    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }
}
