package com.example.bozhilun.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.example.bozhilun.android.Commont;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

/**
 * 本地存储工具类,用于存储手环相关参数
 *
 * @author XuBo
 */
public class LocalizeTool {

    /**
     * 本地存储
     */
    private SharedPreferences sharedPre;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 在XML文件中的字段:是否打开公制
     */
    private final String METRIC_SYSTEM = "metric_system_";
    /**
     * 最后更新总数据的日期
     */
    private final String UPDATE_DATE = "update_date_";
    //血氧和HRV最后更新日期
    private final String SPO2_HRV_UPDATE_DATE = "spo2_update_data_";

    public LocalizeTool(Context context) {
        sharedPre = context.getSharedPreferences("bracelet_info", Context.MODE_PRIVATE);
        mContext = context;
    }

    /**
     * 获取本地MAC
     */
    private String getDeviceCode() {
        return (String) SharedPreferencesUtils.readObject(mContext, Commont.BLEMAC);
    }

    /**
     * 存储是否打开公制
     */
    public void putMetricSystem(boolean able) {
        String deviceCode = getDeviceCode();
        if (TextUtils.isEmpty(deviceCode)) return;
        Editor editor = sharedPre.edit();
        editor.putBoolean(METRIC_SYSTEM + deviceCode, able);
        editor.apply();
    }

    /**
     * 取出本地的是否打开公制
     */
    public boolean getMetricSystem() {
        return sharedPre.getBoolean(METRIC_SYSTEM + getDeviceCode(), true);
    }

    /**
     * 存储最后更新总数据日期
     */
    public void putUpdateDate(String date) {
        String deviceCode = getDeviceCode();
        if (TextUtils.isEmpty(deviceCode)) return;
        Editor editor = sharedPre.edit();
        editor.putString(UPDATE_DATE + deviceCode, date);
        editor.apply();
    }

    /**
     * 存储最后更新总数据日期
     */
    public void putSpo2AdHRVUpdateDate(String date) {
        String deviceCode = getDeviceCode();
        if (TextUtils.isEmpty(deviceCode)) return;
        Editor editor = sharedPre.edit();
        editor.putString(SPO2_HRV_UPDATE_DATE + deviceCode, date);
        editor.apply();
    }



    /**
     * 取出本地的最后更新总数据日期
     */
    public String getUpdateDate() {
        return sharedPre.getString(UPDATE_DATE + getDeviceCode(), "");
    }


    /**
     * 取出本地的最后更新总数据日期
     */
    public String getSpo2AdHRVUpdateDate() {
        return sharedPre.getString(SPO2_HRV_UPDATE_DATE + getDeviceCode(), "");
    }


}
