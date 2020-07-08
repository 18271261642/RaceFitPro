package com.example.bozhilun.android.friend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30BloadDataView;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.b30.view.DataMarkView;
import com.example.bozhilun.android.bean.AvgHeartRate;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.data.BarXFormartValue;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrendMoreDataActivity extends WatchBaseActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener, RequestView {

    @BindView(R.id.m_tablayout)
    TabLayout mTablayout;
    @BindView(R.id.toolbar_normal)
    Toolbar toolbarNormal;
    @BindView(R.id.stepDataValueTv)
    TextView stepDataValueTv;
    @BindView(R.id.stepDataChartView)
    BarChart stepDataChartView;
    @BindView(R.id.sleepDataValueTv)
    TextView sleepDataValueTv;
    @BindView(R.id.sleepDataChartView)
    BarChart sleepDataChartView;
    @BindView(R.id.heartDataValueTv)
    TextView heartDataValueTv;
    @BindView(R.id.heartDataChartView)
    BarChart heartDataChartView;
    @BindView(R.id.bloodDataValueTv)
    TextView bloodDataValueTv;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.charBloadView)
    B30BloadDataView charBloadView;
    private int pageNumber = 0;//记录当前页码
    private RequestPressent requestPressent;
    String applicant = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_more_data_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        init();

        initViews();
    }



    private void init() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);


        toolbarNormal.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        toolbarNormal.setNavigationOnClickListener(this);//右边返回按钮点击事件


        //Tab 分割线
        LinearLayout linearLayout = (LinearLayout) mTablayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        mTablayout.setVisibility(View.VISIBLE);
        mTablayout.addTab(mTablayout.newTab().setText("周"));//周
        mTablayout.addTab(mTablayout.newTab().setText("月"));//月
        mTablayout.addTab(mTablayout.newTab().setText("年"));//年
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.addOnTabSelectedListener(this);
    }


    /**
     * tab 切换
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pageNumber = tab.getPosition();
        if (WatchUtils.isEmpty(applicant)) return;
        if (pageNumber == 0) {//周
            getHistoryData(7);
        } else if (pageNumber == 1) {//月
            getHistoryData(30);
        } else {//年
            getHistoryData(365);
        }
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 切换数据(周月年)
     *
     * @param day 7_周 30_月 12_年
     */
    public void getHistoryData(int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String baseUrl = URLs.HTTPs;
            JSONObject jsonObect = new JSONObject();
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            jsonObect.put("userId", userId);
            if (TextUtils.isEmpty(applicant)) getIntent().getStringExtra("applicant");
            jsonObect.put("applicant", applicant);
            jsonObect.put("start", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate4()), day)));
            Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
            String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
            jsonObect.put("end", nextDay.substring(0, 10));
            Log.d("-----------朋友", "-------获取参数=" + jsonObect.toString());
            if (requestPressent != null) {
                //获取步数
                requestPressent.getRequestJSONObject(0x01, baseUrl + Commont.FrendStepData, MyApp.getContext(), jsonObect.toString(), day);
                //获取心率
                requestPressent.getRequestJSONObject(0x02, baseUrl + Commont.FrendHeartData, MyApp.getContext(), jsonObect.toString(), day);
                //获取睡眠
                requestPressent.getRequestJSONObject(0x03, baseUrl + Commont.FrendSleepData, MyApp.getContext(), jsonObect.toString(), day);
                //获取血压
//                requestPressent.getRequestJSONObject(0x04, baseUrl + "/data/getBloodPressureByTime", MyApp.getContext(), jsonObect.toString(), day);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 点击返回
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        finish();
    }


    /**
     * x轴数据
     */
    private List<String> stepXList;
    /**
     * 步数的相关
     */
    List<WatchDataDatyBean> stepList;
    private B30DataServer b30DataServer;
    /**
     * 步数数值
     */
    private List<Integer> mValues;
    List<BarEntry> pointbar;


    //睡眠相关
    /**
     * 睡眠的数值
     */
    private List<Float> sleepVlaues;
    /**
     * 睡眠X轴
     */
    private List<String> sleepXList;
    private List<BarEntry> sleepBarEntryList;


    /**
     * 心率数值
     */
    private List<Integer> heartValues;
    /**
     * 心率X轴数据
     */
    private List<String> heartXList;
    /**
     * 心率中间list
     */
    private List<String> tempHeartList;
    private Map<String, Integer> dayMap;
    private List<BarEntry> heartBarEntryList;


    private void initViews() {
        //步数的x轴数据
        stepXList = new ArrayList<>();
        //步数的数据
        pointbar = new ArrayList<>();

        //心率
        tempHeartList = new ArrayList<>();
        heartXList = new ArrayList<>();
        dayMap = new HashMap<>();
        heartValues = new ArrayList<>();
        heartBarEntryList = new ArrayList<>();

        //睡眠
        sleepVlaues = new ArrayList<>();
        sleepXList = new ArrayList<>();
        sleepBarEntryList = new ArrayList<>();

        mValues = new ArrayList<>();
    }

    /**
     * 数据请求一类的
     *
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null || TextUtils.isEmpty(object.toString().trim())) return;
        Message message = new Message();
        message.what = what;
        message.arg1 = daystag;
        message.obj = object;
        if (handler != null) handler.sendMessage(message);
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int code = message.arg1;
            switch (message.what) {
                case 0x01:
                    Log.d("-----------朋友--", "步数数据："+message.obj.toString());
                    stepXList.clear();
                    mValues.clear();
                    if (WatchUtils.isEmpty(message.obj.toString())) {
                        showStepsChat(mValues, stepXList);
                        break;
                    }
                    String daydata = null;
                    try {
                        JSONObject jsonObject = new JSONObject(message.obj.toString());
                        if (jsonObject.getString("resultCode").equals("001")) {
                            daydata = jsonObject.getString("friendStepNumber");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (daydata == null) {
                        showStepsChat(mValues, stepXList);
                        break;
                    }
                    if (WatchUtils.isEmpty(daydata) || daydata.equals("[]")) {
                        showStepsChat(mValues, stepXList);
                        break;
                    }

                    stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                    }.getType());
                    if (code == 365) { //年
                        /*用于计算年的步数*/
                        Map<String, Integer> stepSumMap = new HashMap<>();
                        /*用于计算年的步数的list*/
                        List<String> tempList = new ArrayList<>();
                        int sum = 0;
                        for (int i = 0; i < stepList.size(); i++) {
                            String dateStr = stepList.get(i).getRtc();
                            String strDate = dateStr.substring(2, 7);
                            if (stepSumMap.get(strDate) != null) {
                                sum += stepList.get(i).getStepNumber();
                            } else {
                                sum = stepList.get(i).getStepNumber();
                            }
                            stepSumMap.put(strDate, sum);
                        }
                        //遍历map
                        for (Map.Entry<String, Integer> entry : stepSumMap.entrySet()) {
                            tempList.add(entry.getKey().trim());
                        }
                        //升序排列
                        Collections.sort(tempList, new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });
                        for (int k = 0; k < tempList.size(); k++) {
                            mValues.add(stepSumMap.get(tempList.get(k)));
                            stepXList.add(tempList.get(k));
                        }
                    } else {
                        //获取值
                        for (WatchDataDatyBean stepNumber : stepList) {
                            mValues.add(stepNumber.getStepNumber());    //步数的数值显示
                            String dateStr = stepNumber.getRtc();
                            String rct = dateStr.substring(5, dateStr.length());
                            stepXList.add(rct);
                        }
                    }
                    showStepsChat(mValues, stepXList);
                    break;
                case 0x02:
                    Log.d("-----------朋友--", "心率："+message.obj.toString());

                    heartXList.clear();
                    heartValues.clear();
                    if (WatchUtils.isEmpty(message.obj.toString())) {
                        showHeartChart(heartValues, heartXList);
                        break;
                    }
                    //        Log.e(TAG, "----心率数据=" + heartStr);
                    String heartData = null;
                    try {
                        JSONObject jsonObject = new JSONObject(message.obj.toString());
                        if (jsonObject.getString("resultCode").equals("001")) {
                            heartData = jsonObject.getString("friendHeartRate");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (heartData == null) {
                        showHeartChart(heartValues, heartXList);
                        break;
                    }
                    if (WatchUtils.isEmpty(heartData) || heartData.equals("[]")) {
                        showHeartChart(heartValues, heartXList);
                        break;
                    }
                    List<AvgHeartRate> heartList = new Gson().fromJson(heartData, new TypeToken<List<AvgHeartRate>>() {
                    }.getType());
                    if (code == 365) {    //年
                        Map<String, Integer> heartMap = new HashMap<>();
                        int heartSum;
                        int Day = 0;
                        dayMap.clear();
                        for (int i = 0; i < heartList.size(); i++) {
                            String strDate = heartList.get(i).getRtc().substring(2, 7);
                            if (heartMap.get(strDate) != null) {
                                heartSum = heartList.get(i).getAvgHeartRate();
                                if (heartList.get(i).getAvgHeartRate() > 0) {
                                    dayMap.put(strDate, Day++);
                                }
                            } else {
                                heartSum = heartList.get(i).getAvgHeartRate();
                                Day = 0;
                                if (heartList.get(i).getAvgHeartRate() > 0) {
                                    dayMap.put(strDate, Day++);
                                }
                            }
                            heartMap.put(strDate, heartSum);
                        }
                        tempHeartList.clear();
                        for (Map.Entry<String, Integer> maps : heartMap.entrySet()) {
                            tempHeartList.add(maps.getKey().trim());
                        }
                        //排序时间
                        Collections.sort(tempHeartList, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareTo(o2);
                            }
                        });
                        for (int i = 0; i < tempHeartList.size(); i++) {
                            int integer = (int) heartMap.get(tempHeartList.get(i));
                            heartValues.add(integer);
                            heartXList.add(tempHeartList.get(i));
                        }
                    } else {  //周或者月
                        for (AvgHeartRate avgHeart : heartList) {
                            int avgHeartRate = avgHeart.getAvgHeartRate();
                            heartValues.add(avgHeartRate);
                            heartXList.add(avgHeart.getRtc().substring(5, avgHeart.getRtc().length()));
                        }
                    }
                    showHeartChart(heartValues, heartXList);
                    break;
                case 0x03:
                    Log.d("-----------朋友--", "睡眠："+message.obj.toString());
                    break;
            }
            return false;
        }
    });


    // 展示步数图表
    private void showStepsChat(List<Integer> mValues, List<String> xList) {

        pointbar.clear();
        for (int i = 0; i < mValues.size(); i++) {
            pointbar.add(new BarEntry(i, mValues.get(i)));
        }
//        Log.e(TAG, "----步数大小=" + mValues.size());
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色

        barDataSet.setHighLightColor(Color.WHITE);
//        barDataSet.setHighLightColor(Color.GREEN);
        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (mValues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }
        stepDataChartView.setData(bardata);

        stepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        stepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        stepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        stepDataChartView.setPinchZoom(false);
        stepDataChartView.setTouchEnabled(true);
        stepDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(stepDataChartView, xList);
        XAxis xAxis = stepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);

        xAxis.setEnabled(true);
        stepDataChartView.getDescription().setEnabled(false);


        DataMarkView dataMarkView = new DataMarkView(this, R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(stepDataChartView);
        stepDataChartView.setMarker(dataMarkView);


        stepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        stepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        stepDataChartView.getAxisLeft().setEnabled(false);
        stepDataChartView.getXAxis().setSpaceMax(0.5f);
        stepDataChartView.animateXY(1000, 2000);//设置动画
    }


    //展示心率图表
    private void showHeartChart(List<Integer> heartList, List<String> xlt) {
        heartBarEntryList.clear();
        for (int i = 0; i < heartList.size(); i++) {
            heartBarEntryList.add(new BarEntry(i, (int) heartList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(heartBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
//        barDataSet.setHighLightColor(Color.GREEN);
        barDataSet.setHighLightColor(Color.WHITE);

        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (heartList.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        heartDataChartView.setData(bardata);
        heartDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        heartDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        heartDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        heartDataChartView.getLegend().setEnabled(false);
        heartDataChartView.setTouchEnabled(true);
        heartDataChartView.setPinchZoom(false);
        heartDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(heartDataChartView, xlt);
        XAxis xAxis = heartDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);//设置X轴的高度
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);


        DataMarkView dataMarkView = new DataMarkView(FrendMoreDataActivity.this, R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(heartDataChartView);
        heartDataChartView.setMarker(dataMarkView);


        heartDataChartView.getDescription().setEnabled(false);

        heartDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        heartDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        heartDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        heartDataChartView.getAxisLeft().setEnabled(false);
        heartDataChartView.getXAxis().setSpaceMax(0.5f);
        heartDataChartView.animateXY(1000, 2000);//设置动画
    }

}
