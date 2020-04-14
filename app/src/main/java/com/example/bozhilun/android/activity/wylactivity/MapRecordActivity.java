package com.example.bozhilun.android.activity.wylactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.B30LatlonBean;
import com.example.bozhilun.android.util.Common;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/30.
 */

public class MapRecordActivity extends BaseActivity {

    private static final String TAG = "MapRecordActivity";


    @BindView(R.id.huwaiqixing_years)
    TextView Starttime;
    /**
     * 开始时间
     */
    @BindView(R.id.qichekong_qizhiliangyy)
    TextView Kongqi;
    /**
     * 空气质量
     */
    @BindView(R.id.qiche_kongqiyu)
    ImageView TianqiImage;
    /**
     * 天气图片
     */
    @BindView(R.id.qiche_wendu)
    TextView Wendu;
    /**
     * 温度
     */
    @BindView(R.id.test_chronometer_times)
    Chronometer Duration;
    /**
     * 总计时间
     */
    @BindView(R.id.test_full_kilometer)
    TextView Fullkilometer;
    /**
     * 总公里
     */
    @BindView(R.id.test_peisu)
    TextView Pace;
    /**
     * 配速
     */
    @BindView(R.id.test_xiaohao_kclal)
    TextView Consume;
    /**
     * 消耗
     */
    @BindView(R.id.qiche_mypmyy)
    LinearLayout linearLayoutONE;
    /**
     * p25视图
     */
    @BindView(R.id.qichemypm_www)
    LinearLayout linearLayoutTwo;
    /**
     * 温度视图
     */
    @BindView(R.id.test_huwaiqixing_ditut)
    RelativeLayout linearLayoutThere;
    /**
     * 温度视图
     */
    @BindView(R.id.qixingshu_waipao_bustar)
    LinearLayout Back;
    /**
     * 返回
     */

    @BindView(R.id.qixingfugai_hostory)
    ImageView fugaiwu;

    LocationSource.OnLocationChangedListener mListener;//定位监听
    MapView mapView;
    GoogleMap aaad;
    /**
     * 高德相关
     */
    Bitmap bm;
    AMap aMap;
    LatLng ll, oldll;
    // 自定义系统定位蓝点
    MyLocationStyle myLocationStyle = new MyLocationStyle();
    boolean isoneline = false;//是不是第一次划线
    @BindView(R.id.shuwaipao_buhuwai)
    TextView BIAOTI;
    /**
     * 高德地图 第一次加载的经纬度
     */
    double mlatitude;
    double mlongitude;

    @Override
    protected void initViews() {


    }

    @OnClick({R.id.qixingshu_waipao_bustar, R.id.huwaiq_ixingbubao_fengxiang})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qixingshu_waipao_bustar:
                finish();
                break;
            case R.id.huwaiq_ixingbubao_fengxiang:
                boolean zhon = getResources().getConfiguration().locale.getCountry().equals("CN");
                if (zhon) {
                    aMap.getMapScreenShot(new MapRecordActivity.Gaode()); /**高德地图截屏*/
                } else {
                    GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                        Bitmap bitmap;

                        public void onSnapshotReady(Bitmap snapshot) {
                            bitmap = snapshot;
                            try {
                                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "SDSDSdd.png");
                                boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                try {
                                    out.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (ifSuccess) {
                                    fugaiwu.setVisibility(View.VISIBLE);
                                    fugaiwu.setImageBitmap(bitmap);
                                    Date timedf = new Date();
                                    SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                                    String xXXXdf = formatdf.format(timedf);
                                    String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                                    ScreenShot.shoot(MapRecordActivity.this, new File(filePath));
                                    Common.showShare(MapRecordActivity.this, null, false, filePath);
                                } else {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    aaad.snapshot(callback);
                }
                break;

        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_maprecord;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = (MapView) findViewById(R.id.test_qixing_map);
        mapView.onCreate(savedInstanceState);
        linearLayoutONE.getBackground().setAlpha(120);
        linearLayoutTwo.getBackground().setAlpha(120);
        linearLayoutThere.getBackground().setAlpha(120);


        //取得从上一个Activity当中传递过来的Intent对象
        Intent _intent = getIntent();
        //从Intent当中根据key取得value
        if (_intent != null) {

            try {
                String value = _intent.getStringExtra("mapdata");//这里是经纬度
//                String value = value3.replace("\\", "");
//                Log.e(TAG, "----------value="+value+"\n"+new Gson().toJson(value));
                String value2 = _intent.getStringExtra("mapdata2");//这里是其他数据
                JSONObject JSONO = new JSONObject(value2);
                Starttime.setText(JSONO.optString("day").toString());
                if ("良".equals(JSONO.optString("description").toString().trim())) {
                    Kongqi.setText(getResources().getString(R.string.good));
                } else if ("轻度污染".equals(JSONO.optString("description").toString().trim())) {
                    Kongqi.setText(getResources().getString(R.string.mild_pollution));
                } else if ("中度污染".equals(JSONO.optString("description").toString().trim())) {
                    Kongqi.setText(getResources().getString(R.string.moderate_pollution));
                } else if ("重度污染".equals(JSONO.optString("description").toString().trim())) {
                    Kongqi.setText(getResources().getString(R.string.heavy_pollution));
                } else if ("严重污染".equals(JSONO.optString("description").toString().trim())) {
                    Kongqi.setText(getResources().getString(R.string.serious_pollution));
                }
                Wendu.setText(JSONO.optString("temp").toString());
                BIAOTI.setText(JSONO.optString("qixing").toString());
                Duration.setText(JSONO.optString("chixutime").toString());
                Fullkilometer.setText(JSONO.optString("zonggongli").toString());
                Pace.setText(getResources().getString(R.string.paces) + JSONO.optString("speed").toString());
                Consume.setText(getResources().getString(R.string.XIAOHAO) + JSONO.optString("kclal").toString());
                System.out.print("inmage" + JSONO.optString("image").toString());
                BitmapUtils bitmapUtils = new BitmapUtils(MapRecordActivity.this);
                BitmapDisplayConfig config = new BitmapDisplayConfig();
                // 设置图片的分辨率
                BitmapSize size = new BitmapSize(500, 500);
                config.setBitmapMaxSize(size);
                bitmapUtils.display(TianqiImage, JSONO.optString("image").toString());


                //解析地图轨迹
                List<B30LatlonBean> b30LatlonBeanList = new Gson().fromJson(value, new TypeToken<List<B30LatlonBean>>() {
                }.getType());
//                com.alibaba.fastjson.JSONArray Mapdata = com.alibaba.fastjson.JSONArray.parseArray(value);
//                JSONArray Mapdata = new JSONArray(value);

                // Log.d("===========-", b30LatlonBeanList.toString());
                for (int i = 0; i < b30LatlonBeanList.size(); i++) {
//                    JSONObject jo = (JSONObject) Mapdata.get(i);
//                    String rtc = jo.optString("lon").toString();//纬度
//                    String jindu = jo.optString("lat").toString();//经度
                    double rtc = b30LatlonBeanList.get(i).getLon();//纬度
                    double jindu = b30LatlonBeanList.get(i).getLat();//经度

                    // Log.e(TAG,"-------经纬度="+rtc+"--纬度="+jindu);


                    //MyLogUtil.i("response"+rtc+"rrrrrrrr"+jindu);
                    Log.d("===========-", rtc + "===" + jindu);
                    if (i == 0) {
                        if (aMap == null) {
                            aMap = mapView.getMap();
                            UiSettings uiSettings = aMap.getUiSettings();
                            uiSettings.setLogoBottomMargin(-70);//隐藏logo
                            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
                        }
                        //修改地图的中心点位置
                        CameraPosition cp = aMap.getCameraPosition();
                        CameraPosition cpNew = CameraPosition.fromLatLngZoom(new LatLng(Double.valueOf(jindu), Double.valueOf(rtc)), cp.zoom);
                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                        aMap.moveCamera(cu);
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                        oldll = new LatLng(rtc, jindu);
                        ll = oldll;
                        hua(1, jindu, rtc);
                    }
                    if (i == b30LatlonBeanList.size() - 1) {
                        hua(0, jindu, rtc);
                    }
                    ll = new LatLng(jindu, rtc);
                    if (oldll.latitude != 90.0) {
                        aMap.addPolyline((new PolylineOptions()).add(oldll, ll).color(Color.RED).width(10f).geodesic(true));
                    }
                    oldll = ll;
                }

            } catch (Exception E) {
                E.printStackTrace();
            }

        }
    }


    public Bitmap setImgSize(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    //画高德地图的起始点
    public void hua(int id, double jin, double wei) {
        com.amap.api.maps.model.MarkerOptions mo = new com.amap.api.maps.model.MarkerOptions();
        mo.position(new LatLng(jin, wei));
        mo.draggable(true);
        mo.setFlat(false);
        mo.autoOverturnInfoWindow(false);
        mo.zIndex(5f);
        mo.perspective(true);
        mo.period(1);
        // mo.icon(BitmapDescriptorFactory.defaultMarker());
        if (id == 1) {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.fit_start_point);
            mo.icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(setImgSize(bm,50,60)));
            aMap.addMarker(mo);
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.fit_end_point);
            mo.icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(setImgSize(bm,50,60)));
            aMap.addMarker(mo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapView != null){
            mapView.onDestroy();
        }

    }

    /**
     * 高德地图截屏
     */
    public class Gaode implements AMap.OnMapScreenShotListener {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            try {
                // 保存在SD卡根目录下，图片为png格式。
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "eeed.png");
                boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ifSuccess) {
                    fugaiwu.setVisibility(View.VISIBLE);
                    fugaiwu.setImageBitmap(bitmap);
                    Date timedf = new Date();
                    SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
                    String xXXXdf = formatdf.format(timedf);
                    String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                    ScreenShot.shoot(MapRecordActivity.this, new File(filePath));
                    Common.showShare(MapRecordActivity.this, null, false, filePath);
                } else {
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {
        }
    }

}
