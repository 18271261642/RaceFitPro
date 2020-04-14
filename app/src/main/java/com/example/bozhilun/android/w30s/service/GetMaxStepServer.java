package com.example.bozhilun.android.w30s.service;

import android.content.Context;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/5/8.
 */

public class GetMaxStepServer {

    private Context mContext;
    //步数的相关
    List<WatchDataDatyBean> stepList;
    private List<Integer> integerList;  //记录步数

    private Map<Integer, Integer> masMap;

    private SubscriberOnNextListener subscriberOnNextListener;
    private CommonSubscriber commonSubscriber;

    public GetMaxStepServer(Context mContext) {
        this.mContext = mContext;
        integerList = new ArrayList<>();
        masMap = new HashMap<>();
    }

    public GetMaxStepForYearListener getMaxStepForYearListener;

    public void setGetMaxStepForYearListener(GetMaxStepForYearListener getMaxStepForYearListener) {
        this.getMaxStepForYearListener = getMaxStepForYearListener;
    }

    public void getDataFromServer() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                Log.e("计算", "----后台返回=" + s);
                if (!WatchUtils.isEmpty(s))
                    analysisData(s);

            }
        };
        try {
            JSONObject jsonObect = new JSONObject();
            jsonObect.put("userId", SharedPreferencesUtils.readObject(mContext, "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(mContext, Commont.BLEMAC));
            jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 365)));
            jsonObect.put("endDate", WatchUtils.getCurrentDate());
            commonSubscriber = new CommonSubscriber(subscriberOnNextListener, mContext);
            OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObect.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void analysisData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.getString("resultCode").equals("001")) {
                String daydata = jsonObject.getString("day");
                if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
                    stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                    }.getType());
                    //获取所有的步数集合
                    for (int i = 0; i < stepList.size(); i++) {
                        masMap.put(stepList.get(i).getStepNumber(), i);
                    }
                    //遍历map
                    for (Map.Entry<Integer, Integer> mp : masMap.entrySet()) {
                        integerList.add(mp.getKey());
                    }
                    //最大值步数
                    int maxStep = Collections.max(integerList);
                    //对应下标
                    int positi = masMap.get(maxStep);
                    if (getMaxStepForYearListener != null) {
                        getMaxStepForYearListener.reabackData(maxStep, stepList.get(positi).getCalories(), stepList.get(positi).getDistance(),stepList.get(positi).getRtc());
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface GetMaxStepForYearListener {
        void reabackData(int maxStep, String kacl, String disc,String dateStr);
    }

}
