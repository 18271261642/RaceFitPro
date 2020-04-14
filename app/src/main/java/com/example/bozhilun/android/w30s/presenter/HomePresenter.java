package com.example.bozhilun.android.w30s.presenter;

import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.MyBroadcastReceiver;
import com.example.bozhilun.android.w30s.fragment.W30SRecordFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/5/8 10:33
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class HomePresenter {
    private static final String TAG = "HomePresenter";
    private W30SRecordFragment w30SRecordFragment;

    public HomePresenter(W30SRecordFragment w30SRecordFragment) {
        this.w30SRecordFragment = w30SRecordFragment;
    }

    public void changeUI() {
        try {
            MyBroadcastReceiver.setmBluetoothStateListenter(new MyBroadcastReceiver.BluetoothStateListenter() {
                @Override
                public void BluetoothStateListenter() {
                    w30SRecordFragment.changeUI();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 单日最高步数
     */
    public void changeHttpsDataUI() {
        integerList = new ArrayList<>();
        masMap = new HashMap<>();
        getAllChartData();
    }


    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private void getAllChartData() {
        subscriberOnNextListener = new SubscriberOnNextListener() {
            @Override
            public void onNext(Object o) {
                String res = o.toString();
                Log.d("------firstDayOfMonth", res + "");
                analysisData(res);
            }
        };
        String myInfoUrl = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonObect = new JSONObject();
        Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
        String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);//yyyy-MM-dd
        Date firstDayOfMonth = getFirstDayOfMonth(Integer.valueOf(nextDay.substring(0, 4)), Integer.valueOf(nextDay.substring(5, 7)));
        String firstDayOfMonthData = H9TimeUtil.getValidDateStr2(firstDayOfMonth);//yyyy-MM-dd

        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(w30SRecordFragment.getContext(), "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(w30SRecordFragment.getContext(), Commont.BLEMAC));
            //jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), week)));
            //jsonObect.put("endDate", WatchUtils.getCurrentDate());
            jsonObect.put("startDate", firstDayOfMonthData);
            jsonObect.put("endDate", nextDay.substring(0, 10));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, w30SRecordFragment.getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, myInfoUrl, jsonObect.toString());

    }


    //步数的相关
    List<WatchDataDatyBean> stepList;
    private List<Integer> integerList;  //记录步数
    private Map<Integer, Integer> masMap;

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
                    w30SRecordFragment.changeHttpsDataUI(maxStep, stepList.get(positi).getCalories(), stepList.get(positi).getDistance(), stepList.get(positi).getRtc());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定年月的月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH + 1);
        }
        calendar.set(year, month - 1, 1);
        return calendar.getTime();
    }
}
