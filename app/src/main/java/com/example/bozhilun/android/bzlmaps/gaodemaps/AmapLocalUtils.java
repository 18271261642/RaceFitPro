package com.example.bozhilun.android.bzlmaps.gaodemaps;

import android.content.Context;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;

/**
 * Created by Admin
 * Date 2020/4/26
 */
public class AmapLocalUtils {

    private AmapLocalMsgListener amapLocalMsgListener;

    public void setAmapLocalMsgListener(AmapLocalMsgListener amapLocalMsgListener) {
        this.amapLocalMsgListener = amapLocalMsgListener;
    }

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    public AmapLocalUtils(Context context) {
        mlocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(aMapLocationListener);
        //设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位时间间隔
        mLocationOption.setInterval(10 * 1000);
        mlocationClient.setLocationOption(mLocationOption);
        //开始定位
        mlocationClient.startLocation();
    }



    //停止定位
    public void stopLocal(){
        if(mlocationClient != null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption.clone();
    }



    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation == null)
                return;
            if(aMapLocation.getErrorCode() == 0){
                if(amapLocalMsgListener != null)
                    amapLocalMsgListener.getLocalData(aMapLocation.getCity(),new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
            }
        }
    };

    public interface AmapLocalMsgListener{
        void getLocalData(String cityStr, LatLng latLng);
    }

    //高德地图经纬度转百度经纬度
    public double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

}
