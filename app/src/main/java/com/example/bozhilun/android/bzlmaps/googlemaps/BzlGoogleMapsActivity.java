package com.example.bozhilun.android.bzlmaps.googlemaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afa.tourism.greendao.gen.SportMapsDao;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMapException;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.BzlDragView;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.bzlmaps.MyCountTimer;
import com.example.bozhilun.android.bzlmaps.PermissionUtils;
import com.example.bozhilun.android.bzlmaps.mapdb.LatLonBean;
import com.example.bozhilun.android.bzlmaps.mapdb.SportMaps;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BzlGoogleMapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback, Chronometer.OnChronometerTickListener, BzlDragView.BzlDragViewListenter {
    private final String TAG = "-------GPS>>>";
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    /**
     * 用来判断用户在连接上Google Play services之前是否有请求地址的操作
     */
    private boolean mAddressRequested;
    private LocationRequest mLocationRequest;

    /**
     * 地图上锚点
     */
    private Marker starPerth, endPerth;
    private LatLng perthLatLng;


    private Chronometer sportTimes;//运动计时器

    private TextView textTimeData;
    private TextView sportDistance;//运动距离---KM
    private TextView sportShisu;//运动时速---KM/H
    private TextView sportSpeed;//运动配速
    private TextView sportKcal;//运动卡路里
    private TextView timeDowln;//倒计时
    private FrameLayout timr_frame;

    private boolean isStart = false;//是否开始定位
    private double gpsDistance = 0.0;//距离
    private boolean isEnd = false;//是否结束定位
    private int times = 0;//运动计时 s
    private BzlDragView bzlDragView;
    private TextView sport_distance_unti, util_shisu;//单位  km--ft=====km/h---FT/h


    //    private DialogSubscriber dialogSubscriber;
    private static String uil = "http://apis.berace.com.cn/watch/sport/upOutdoorSport";//上传轨迹
    List gpsMaps = new ArrayList();
    com.alibaba.fastjson.JSONObject gpsJSon;
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
//                    ToastUtil.showShort(BzlGoogleMapsActivity.this, getResources().getString(R.string.data_upload));
//                } else {
//                    ToastUtil.showShort(BzlGoogleMapsActivity.this, getResources().getString(R.string.data_upload_fail));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };


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
            String speedSStr = "0.0";
            if (sportShisu != null) {
                //取得时速
                speedSStr = sportShisu.getText().toString().trim();
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
//            gpsJSon.put("rtc", df.format(new Date()));
//            gpsJSon.put("speed", speedSStr.equals("00'00") || TextUtils.isEmpty(speedSStr) ? "0.0" : Double.valueOf(speedSStr));
//            取得公里
//            gpsJSon.put("distance", discanceStr.equals("0.00") || TextUtils.isEmpty(discanceStr) ? "0.00" : Double.valueOf(Double.parseDouble(sportDistance.getText().toString())));
//            gpsJSon.put("userId", SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "userId"));
            DecimalFormat decimalFormat = new DecimalFormat("######0.00");
//            gpsJSon.put("calories", Double.valueOf(decimalFormat.format(gpsDistance * 65.4)));


            /****  保存数据库  ***/
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
            sportMaps.setRtc(df.format(new Date()));
            sportMaps.setSaveTime(df1.format(new Date()));
            sportMaps.setSpeed(speedSStr.equals("00'00") || TextUtils.isEmpty(speedSStr) ? "0.0" : "" + Double.valueOf(speedSStr));
            if (textTimeData != null)
                if (sportDistance != null) {
                    sportMaps.setDistance(discanceStr.equals("0.00") ? "0.00" : "" + Double.valueOf(Double.parseDouble(sportDistance.getText().toString())));
                } else {
                    sportMaps.setDistance("0.00");
                }
            else {
                sportMaps.setDistance("0.00");
            }


//            String userId = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "userId");
//            String mac = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, Commont.BLEMAC);
//            if (MyCommandManager.DEVICENAME.equals("W30")) {
//                mac = (String) SharedPreferenceUtil.get(BzlGoogleMapsActivity.this, Commont.BLEMAC, "");
//                userId = (String) SharedPreferencesUtils.getParam(BzlGoogleMapsActivity.this, "userId", "");
//            } else {
//                userId = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "userId");
//                mac = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, Commont.BLEMAC);
//            }
            //String bleMac = (String) SharedPreferenceUtil.get(BzlGoogleMapsActivity.this, Commont.BLEMAC, "");
            String bleMac = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, Commont.BLEMAC);
            String userId = (String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "userId");
            if (WatchUtils.isEmpty(bleMac)) return;
            sportMaps.setUserId(WatchUtils.isEmpty(userId) ? "null" : userId);
            sportMaps.setCalories(Double.valueOf(decimalFormat.format(gpsDistance * 65.4)) + "");
            sportMaps.setMac(bleMac);

            sportMaps.setUserId(TextUtils.isEmpty(userId) ? "null" : userId);
            sportMaps.setCalories(Double.valueOf(decimalFormat.format(gpsDistance * 65.4)) + "");
            sportMaps.setMac(bleMac);

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

            //System.out.print("Mgps" + gpsJSon.toString());
        } catch (Exception E) {
            E.printStackTrace();
        }

//        //判断网络是否连接
//        if (!ConnectManages.isNetworkAvailable(BzlGoogleMapsActivity.this)) {
//            ToastUtil.showToast(BzlGoogleMapsActivity.this, getResources().getString(R.string.string_not_net));
//        } else {
//            try {
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("outdoorSports", gpsJSon);
//                map.put("latLons", gpsMaps);
//                String mapjson = JSON.toJSONString(map);
//                //MyLogUtil.i("latLons" + mapjson);
//                dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, BzlGoogleMapsActivity.this);
//                OkHttpObservable.getInstance().getData(dialogSubscriber, uil, mapjson);
//            } catch (Exception E) {
//                E.printStackTrace();
//            }
//        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blz_activity_google_maps);

        //接收FetchAddressIntentService返回的结果
        mResultReceiver = new AddressResultReceiver(new Handler());
        //创建GoogleAPIClient实例
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        //取得SupportMapFragment,并在地图准备好后调用onMapReady
//        SupportMapFragment mapFragment = findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsMaps != null) {
            gpsMaps.clear();
            gpsMaps = null;
        }
        if (gpsJSon != null) {
            gpsJSon.clear();
            gpsJSon = null;
        }
        if (mMap != null) {
            mMap.clear();
            mMap = null;
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

    private void init() {
        bzlDragView = new BzlDragView(this, getResources().getString(R.string.star));
        bzlDragView.setBzlDragViewListenter(this);
        bzlDragView.setIsDrag(false);
        sportTimes = findViewById(R.id.sport_times);
//        sportTimes.setOnChronometerTickListener(this);//设置计时器监听
        textTimeData = findViewById(R.id.text_time_data);
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
        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("H9"))||
                !TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("W06X")){
            // 0位公制 1为英制
            if (h9_step_util == 0) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("MI");
            }

        }else {
            if (w30sunit) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("FT");
            }
        }

        if (myCountTimer == null && timeDowln != null) {
            myCountTimer =
                    new MyCountTimer(4000, 1000, timeDowln, getResources().getString(R.string.string_sport_run));
            //倒计时监听
            myCountTimer.setmCompletionTime(completionTime);
        }
        gpsJSon = new com.alibaba.fastjson.JSONObject();
        /**查询当前的运动类型*/
        try {
            if (!TextUtils.isEmpty((String) SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "type"))) {
                if ("0".equals(SharedPreferencesUtils.readObject(BzlGoogleMapsActivity.this, "type"))) {
                    gpsJSon.put("type", 0);
                    sportMaps.setType(0);
//                    Title.setText(getResources().getString(R.string.outdoor_running));
                } else {
                    gpsJSon.put("type", 1);
                    sportMaps.setType(1);
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


    @SuppressLint("RestrictedApi")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);//不显示定位按钮
        uiSettings.setCompassEnabled(false);//设置是否显示指南针
//        uiSettings.setZoomControlsEnabled(false);//无缩放按钮
//        uiSettings.setZoomGesturesEnabled(false);//无放大按钮
//        uiSettings.setRotateGesturesEnabled(false);//禁止地图旋转
        mMap.setTrafficEnabled(true);
        mMap.setMinZoomPreference(6f);
        mMap.setMaxZoomPreference(15f);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        enableMyLocation();
    }

    /**
     * 检查是否已经连接到 Google Play services
     */
    private void checkIsGooglePlayConn() {
//        Log.i(TAG, "checkIsGooglePlayConn-->" + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        mAddressRequested = true;
    }

    /**
     * 如果取得了权限,显示地图定位层
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * 启动地址搜索Service
     */
    protected void startIntentService(LatLng latLng) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LATLNG_DATA_EXTRA, latLng);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            //Toast.makeText(getApplicationContext(), "Permission to access the location is missing.", Toast.LENGTH_LONG).show();
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * 将地图视角切换到定位的位置
     */
    private void initCamera(final LatLng sydney) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMap != null)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));
                    }
                });
            }
        }).start();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bzlDragView != null) {
            if (!isEnd) bzlDragView.showDragCallView();
        }
    }

    protected void onStart() {
        if (mGoogleApiClient != null) mGoogleApiClient.connect();// 连接Google Play服务
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();// 停止位置更新
        if (bzlDragView != null) {
            bzlDragView.hideDragCallView();
        }
    }

    /**
     * 位置监听
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                // 精确度越小越准，单位：米
                if (location.getAccuracy() > 100) {
                    return;
                }
                mLastLocation = location;
                updateUI();//更新UI
            } catch (Exception error) {
                error.printStackTrace();
            }

        }
    };


    /**
     * 更新UI
     */
    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (isStart) {
            if (mLastLocation == null) return;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Accuracy :").append(mLastLocation.getAccuracy()).append("\n")
                    .append("Altitude :").append(mLastLocation.getAltitude()).append("\n")
                    .append("Bearing :").append(mLastLocation.getBearing()).append("\n")
                    .append("ElapsedRealtimeNanos :").append(mLastLocation.getElapsedRealtimeNanos()).append("\n")
                    .append("Latitude :").append(mLastLocation.getLatitude()).append("\n")
                    .append("Longitude :").append(mLastLocation.getLongitude()).append("\n")
                    .append("Provider :").append(mLastLocation.getProvider()).append("\n")
                    .append("Speed :").append(mLastLocation.getSpeed()).append("\n")
                    .append("Time :").append(mLastLocation.getTime()).append("\n");
//
//            Log.e(TAG, "--------UI--" + stringBuilder.toString());

            mLocatinLat = mLastLocation.getLatitude();
            mLocationLon = mLastLocation.getLongitude();

            //设置显示地图时间
            if (textTimeData != null) {
                //获取地图返回的时间戳转换成时间设置显示
                long time = mLastLocation.getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                textTimeData.setVisibility(View.VISIBLE);
                textTimeData.setText(format.format(new Date(time)).trim());
            }
            if (isStart) {
                //判断是否是第一次定位
                if (isFirstLatLng) {
                    isFirstLatLng = false;
                    //移动到定位点中心，并且缩放级别为18
                    initCamera(new LatLng(mLocatinLat, mLocationLon));
                    //添加起点
                    startDisplayPerth(new LatLng(mLocatinLat, mLocationLon));
                    mLocationList.add(new LatLng(mLocatinLat, mLocationLon));
                    try {
                        com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                        beanList.put("lat", mLocatinLat);
                        beanList.put("lon", mLocationLon);
                        gpsMaps.add(beanList);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                } else {
                    if (oldLatLng == null) {
                        oldLatLng = new LatLng(mLocatinLat, mLocationLon);
                    } else {
//                        Log.e(TAG, mLastLocation.getLatitude() + "====" + mLastLocation.getLongitude());
                        float speed = mLastLocation.getSpeed();
                        //移动到定位点中心，并且缩放级别为18
                        initCamera(new LatLng(mLocatinLat, mLocationLon));
                        if (isStart) {
                            findBest(speed);
                        }
                    }
                }
            } else {
                initCamera(new LatLng(mLocatinLat, mLocationLon));
            }

        }


        if (textTimeData != null) {
            //获取地图返回的时间戳转换成时间设置显示
            long time = mLastLocation.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            textTimeData.setVisibility(View.VISIBLE);
            textTimeData.setText(format.format(new Date(time)).trim());
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
    private double mLocatinLat;
    private double mLocationLon;

    //以前的定位点
    private LatLng oldLatLng;
    //现在定位点
    private LatLng curLatLng;
    //是否是第一次定位
    private boolean isFirstLatLng = true;
    private ArrayList<LatLng> mLocationList = new ArrayList<>();//定位坐标集合

    /**
     * 高德地图两点之间的距离的算法
     *
     * @param var0
     * @param var1
     * @return
     */
    public float calculateLineDistance(LatLng var0, LatLng var1) {
        if (var0 != null && var1 != null) {
            try {
                double var2 = 0.01745329251994329D;
                double var4 = var0.longitude;
                double var6 = var0.latitude;
                double var8 = var1.longitude;
                double var10 = var1.latitude;
                var4 *= 0.01745329251994329D;
                var6 *= 0.01745329251994329D;
                var8 *= 0.01745329251994329D;
                var10 *= 0.01745329251994329D;
                double var12 = Math.sin(var4);
                double var14 = Math.sin(var6);
                double var16 = Math.cos(var4);
                double var18 = Math.cos(var6);
                double var20 = Math.sin(var8);
                double var22 = Math.sin(var10);
                double var24 = Math.cos(var8);
                double var26 = Math.cos(var10);
                double[] var28 = new double[3];
                double[] var29 = new double[3];
                var28[0] = var18 * var16;
                var28[1] = var18 * var12;
                var28[2] = var14;
                var29[0] = var26 * var24;
                var29[1] = var26 * var20;
                var29[2] = var22;
                double var30 = Math.sqrt((var28[0] - var29[0]) * (var28[0] - var29[0]) + (var28[1] - var29[1]) * (var28[1] - var29[1]) + (var28[2] - var29[2]) * (var28[2] - var29[2]));
                return (float) (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
            } catch (Throwable var32) {
                var32.printStackTrace();
                return 0.0F;
            }
        } else {
            try {
                throw new AMapException("非法坐标值");
            } catch (AMapException var33) {
                var33.printStackTrace();
                return 0.0F;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void findBest(float speed) {
        try {
            currLa = new LatLng(mLocatinLat, mLocationLon);

            currTime = System.currentTimeMillis();
//            Log.e(TAG, "test walk la is" + currLa + "");
//            Log.e(TAG, "test walk last is" + lastLa + "");
            currLength = calculateLineDistance(lastLa, currLa);

            if (!lastLa.equals(currLa)) {
                minusTime = currTime - lastTime;

//                Log.e(TAG, "yhy time testzzz" + minusTime);

                if (currLength < ((minusTime + 1) / 1000) * 5) {
                    errorCnt = 0;
                    lastLa = currLa;
                    lastTime = currTime;

                    mBestLat = mLocatinLat;
                    mBestLon = mLocationLon;
                    curLatLng = new LatLng(mBestLat, mBestLon);
//                    Log.e(TAG, "发>>>>>>>>>" + currLength);
                    if (mLocationList == null) mLocationList = new ArrayList<>();
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
                            if (gpsMaps == null) gpsMaps = new ArrayList();
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
                        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("H9"))||
                                !TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("W06X")){
                            // 0位公制 1为英制
                            if (h9_step_util == 0) {
                                sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance)));
                                sport_distance_unti.setText("KM");
                            } else {
                                sportDistance.setText(String.valueOf(decimalFormat.format(gpsDistance*0.0006214)));
                                sport_distance_unti.setText("MI");
                            }

                        }else {
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

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                        Date dates = dff.parse(sportTimes.getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTime(dates);

                        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);
                        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("H9"))||
                                !TextUtils.isEmpty(MyCommandManager.DEVICENAME)&&MyCommandManager.DEVICENAME.equals("W06X")){
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
                        }else {
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





                    }
                    //配速
                    if (sportSpeed != null) {
                        //   *30/10  == *3.6
//                sportSpeed.setText(String.valueOf(decimalFormat.format(gpsDistance * 3.6)));
                        sportSpeed.setText(String.valueOf(speed));
//                        SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
//                        Date dates = dff.parse(sportTimes.getText().toString());
//                        Calendar c = Calendar.getInstance();
//                        c.setTime(dates);
//                        sportSpeed.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(gpsDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                    }
                    //卡路里
                    if (sportKcal != null) {
                        sportKcal.setText(String.valueOf(decimalFormat.format(gpsDistance * 65.4)) + "kcal");
                    }

//                    Log.e(TAG, "距离 == " + gpsDistance);
                } else if (minusTime >= 20000) {
                    if (mOver) {

                        if (!overLa.equals(currLa)) {
                            errorCnt = 0;
                            lastLa = currLa;
                            lastTime = currTime;
//                            Log.e(TAG, " 确定大于距离>>>>>>>>>" + currLength);

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
//                            Log.e(TAG, " 第一次大于距离" + currLength);
                        }
                    }

                } else {
                    errorCnt++;
//                    Log.e(TAG, " +++++++++++++++++++++++++++++++++++++++++++++距离太大，是漂移，不发" + currLength + "定位是" + mLocatinLat + "^^^" + mLocationLon);
                }

            } else {
//                Log.e(TAG, " -------------------------------------------------距离太小，没有移动，不发");
                lastTime = currTime;
            }

        } catch (ParseException e) {

            e.printStackTrace();
        }

    }


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
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_start_pistion);
            starPerth = mMap.addMarker(new MarkerOptions().draggable(false).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLng));//.title("start")
            starPerth.setDraggable(false); //设置不可移动
        }
    }


    /**
     * 添加 结束标记
     */
    private void endDisplayPerth(LatLng latLng) {
        if (endPerth == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_end_pistion);
            endPerth = mMap.addMarker(new MarkerOptions().draggable(false).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(latLng));//.title("end")
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
        mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .width(10f)
                .geodesic(true)
                .add(oldPointLatLng, newPointLatLng));
    }


    /**
     * 大头针  点击监听
     *
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        perthLatLng = marker.getPosition();
        startIntentService(perthLatLng);
    }


    /**
     * 计时监听
     *
     * @param chronometer
     */
    @Override
    public void onChronometerTick(Chronometer chronometer) {
        times++;
        sportTimes.setText(FormatMiss(times));
        try {//得到时间
            gpsJSon.put("timeLen", FormatMiss(times));
            sportMaps.setTimeLen(FormatMiss(times));
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    class AddressResultReceiver extends ResultReceiver {
        private String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
//                Log.i(TAG, "mAddressOutput-->" + mAddressOutput);
            }

        }
    }


    /**
     * GoogleApiClient.ConnectionCallbacks,
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.i(TAG, "--onConnected--");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(getApplicationContext(), "Permission to access the location is missing.", Toast.LENGTH_LONG).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

//        Log.i(TAG, "--onConnected-经纬度-" + mLastLocation.toString());
        if (mLastLocation != null) {
            initCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));//将地图视角切换到定位的位置
            if (!Geocoder.isPresent()) {
                //Toast.makeText(this, "No geocoder available", Toast.LENGTH_LONG).show();
                return;
            }
            // 启动位置更新
            startLocationUpdates();
            if (mAddressRequested) {
                startIntentService(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * 停止定位更新
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    /**
     * 启用定位更新
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(BzlGoogleMapsActivity.this, "没定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
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
//                        ToastUtil.showShort(BzlGoogleMapsActivity.this, getResources().getString(R.string.string_no_sport));

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
                new CommomDialog(BzlGoogleMapsActivity.this, R.style.dialog, getResources().getString(R.string.save_record) + "?", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            if (mLocationList.get(mLocationList.size() - 1).longitude != 0 && mLocationList.get(mLocationList.size() - 1).latitude != 0) {
                                addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));
                                endDisplayPerth(mLocationList.get(mLocationList.size() - 1));//添加结束点
                            }
                            isEnd = true;
//                            sportTimes.stop();//计时停止
                            if (mHandler != null) mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;
                            stopLocationUpdates();//停止定位更新
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

                //ToastUtil.showShort(BzlGoogleMapsActivity.this, getResources().getString(R.string.string_no_sport));
                startLocationUpdates();//开始定位更新
                if (mHandler != null) mHandler.sendEmptyMessageAtTime(0x01, 5000);
                if (mLocationList.size() < 1)
                    return;
//                addPointLins(mLocationList.get(mLocationList.size() - 1), mLocationList.get(mLocationList.size() - 1));
//                endDisplayPerth(mLocationList.get(mLocationList.size() - 1));//添加结束点
            }

        }


    }

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
            ToastUtil.showShort(BzlGoogleMapsActivity.this, getResources().getString(R.string.string_stop_run_dowln));
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

    /***
     * 倒计时结束毁掉
     */
    MyCountTimer.CompletionTime completionTime = new MyCountTimer.CompletionTime() {
        @Override
        public void OnCompletionTime() {
            if (timeDowln == null) return;
            //倒计时完成时执行开始定位
            timeDowln.setVisibility(View.GONE);//隐藏倒计时控件
            timr_frame.setVisibility(View.GONE);
//            sportTimes.start();//定位时间计时开始
//            sportTimes.setBase(SystemClock.elapsedRealtime());//SystemClock.elapsedRealtime()从开机到现在的毫秒数（手机睡眠的时间也包括在内）

            if (mRunnable == null) {
                mRunnable = new MyRunnable();
            }
            if (mHandler != null) mHandler.postDelayed(mRunnable, 0);

            startLocationUpdates();//开始定位更新
            if (mGoogleApiClient != null) mGoogleApiClient.reconnect();//重新获取位置
            if (bzlDragView != null) {
                bzlDragView.setText(getResources().getString(R.string.stop));
                bzlDragView.setIsDrag(true);
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
                new CommomDialog(BzlGoogleMapsActivity.this, R.style.dialog,
                        getResources().getString(R.string.string_out_gps), new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            isEnd = true;
                            //sportTimes.stop();//计时停止
                            if (mHandler != null) mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;
                            stopLocationUpdates();//停止定位更新
                            isStart = false;
                            bzlDragView.hideDragCallView();
                            bzlDragView.setDraging(false);
                            finish();
                        }
                        dialog.dismiss();
                    }
                }).setTitle(getResources().getString(R.string.prompt)).show();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            times++;
            if (times == 86401) times = 0;
            if (sportTimes != null) sportTimes.setText(FormatMiss(times));
            try {//得到时间
                gpsJSon.put("timeLen", FormatMiss(times));
                sportMaps.setTimeLen(FormatMiss(times));
            } catch (Exception E) {
                E.printStackTrace();
            }
            if (mHandler != null) mHandler.postDelayed(this, 1000);
        }
    }

    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss){
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;
    }
}