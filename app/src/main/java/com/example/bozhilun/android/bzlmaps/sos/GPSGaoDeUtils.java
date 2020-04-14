package com.example.bozhilun.android.bzlmaps.sos;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2018/4/17.
 * 获取用户的地理位置
 */
public class GPSGaoDeUtils {

    private static GPSGaoDeUtils instance;
    private Context mContext;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    private GPSGaoDeUtils(Context context) {
        this.mContext = context;

        mlocationClient = new AMapLocationClient(mContext);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();


        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //经纬度
                        double latitude = amapLocation.getLatitude();
                        double longitude = amapLocation.getLongitude();

                        //详细地址
                        String dizhi = amapLocation.getCountry()//国
                                + " " + amapLocation.getProvince()//省
                                + " " + amapLocation.getCity()//城
                                + " " + amapLocation.getDistrict()//区
                                + " " + amapLocation.getStreet()//街
                                + " " + amapLocation.getStreetNum();//街道门牌号信息
                        if (WatchUtils.isEmpty(dizhi)) dizhi = amapLocation.getAddress();


                        //预编的紧急内容
                        String stringpersonContent = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personContent", "");
                        if (WatchUtils.isEmpty(stringpersonContent))
                            stringpersonContent = mContext.getResources().getString(R.string.string_emergency_gelp);



                        Commont.SENDMESSGE_COUNT++;
                        Log.e("----------AA", "计算发送次数  " + Commont.SENDMESSGE_COUNT);
                        if (Commont.SENDMESSGE_COUNT == 1) {
                            Intent intent_message = new Intent();
                            intent_message.setAction(Commont.SOS_SENDSMS_MESSAGE);
                            intent_message.putExtra("msm", stringpersonContent.trim() + "  " + latitude + "," + longitude);
                            Log.e("----------AA", "拿到位置-- - 位置广播已经发送  去发短信  msm");
                            mContext.sendBroadcast(intent_message);
                        } else if (Commont.SENDMESSGE_COUNT == 2) {
                            Intent intent_location = new Intent();
                            intent_location.setAction(Commont.SOS_SENDSMS_LOCATION);
                            intent_location.putExtra("gps", dizhi.trim());
                            Log.e("----------AA", "拿到位置-- - 位置广播已经发送  去发短信  gps");
                            mContext.sendBroadcast(intent_location);
                        } else {
                            stopGPS();
                            destroyGPS();
                        }
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        });
    }




    /**
     * 停止定位
     */
    void stopGPS() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            mlocationClient = null;
        }
        if (instance != null) instance = null;

    }


    /**
     * 销毁定位
     */
    void destroyGPS() {
        if (mlocationClient != null) {
            mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mlocationClient = null;
        }
    }

    public static GPSGaoDeUtils getInstance(Context context) {
        return new GPSGaoDeUtils(context);
    }

}