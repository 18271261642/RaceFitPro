package com.example.bozhilun.android.h9.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.b30.view.DataMarkView;
import com.example.bozhilun.android.bean.AvgHeartRate;
import com.example.bozhilun.android.bean.NewsSleepBean;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.data.BarXFormartValue;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.NetUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * H9数据界面
 */
@SuppressWarnings("deprecation")
public class H9NewDataFragment extends LazyFragment implements B30DataServer.B30DataServerListener {

    private static final String TAG = "B30DataFragment";
    private Runnable runnableStep = null;
    private Runnable runnableSleep = null;
    private Runnable runnableHeart = null;
    View dataView;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    Unbinder unbinder;
    @BindView(R.id.b30DataWeekTv)
    TextView b30DataWeekTv;
    @BindView(R.id.b30DataMonthTv)
    TextView b30DataMonthTv;
    @BindView(R.id.b30DataYearTv)
    TextView b30DataYearTv;
    /**
     * 步数的图表
     */
    @BindView(R.id.stepDataChartView)
    BarChart stepDataChartView;
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
    /**
     * 睡眠图表
     */
    @BindView(R.id.sleepDataChartView)
    BarChart sleepDataChartView;

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
     * 心率图表
     */
    @BindView(R.id.heartDataChartView)
    BarChart heartDataChartView;
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
    //    private Map<String, Integer> dayMap;
    private List<BarEntry> heartBarEntryList;

    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e(TAG, "----onCreate---");
        b30DataServer = MyApp.getInstance().getB30DataServer();
        b30DataServer.setB30DataServerListener(this);
//        MyLogUtil.e("---------------数据---onCreate");
        String saveDate = (String) SharedPreferencesUtils.getParam(getActivity(), "saveDate", "");
        if (WatchUtils.isEmpty(saveDate)) {
            SharedPreferencesUtils.setParam(getActivity(), "saveDate", System.currentTimeMillis() / 1000 + "");
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataView = inflater.inflate(R.layout.fragment_h9_new_data, container, false);
        unbinder = ButterKnife.bind(this, dataView);
//        Log.e(TAG, "----onCreateView---");


        initViews();
        initData();


//        MyLogUtil.e("---------------数据---onCreate");
        return dataView;


    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.data));
    }

    @Override
    public void onResume() {
        super.onResume();
//        MyLogUtil.e("---------------数据---onResume");
    }

    private void initData() {
        //步数的x轴数据
        stepXList = new ArrayList<>();
        //步数的数据
        pointbar = new ArrayList<>();

        //心率
        tempHeartList = new ArrayList<>();
        heartXList = new ArrayList<>();
//        dayMap = new HashMap<>();
        heartValues = new ArrayList<>();
        heartBarEntryList = new ArrayList<>();

        //睡眠
        sleepVlaues = new ArrayList<>();
        sleepXList = new ArrayList<>();
        sleepBarEntryList = new ArrayList<>();

        mValues = new ArrayList<>();

        gson = new Gson();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
//        Log.e(TAG, "----isVisible---" + isVisible);
        //当前时间
        long currentTime = System.currentTimeMillis() / 1000;
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getActivity(), "saveDate", "");
        long diffTime = (currentTime - Long.valueOf(tmpSaveTime));
        Log.e(TAG, "----------diffTime=" + diffTime);
        if (isVisible && diffTime >= 10) {  //30s
            SharedPreferencesUtils.setParam(getActivity(), "saveDate", System.currentTimeMillis() / 1000 + "");
            setClearStyle(0);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
//        Log.e(TAG, "---onDestroyView----");

    }

    @OnClick({R.id.b30DataWeekTv, R.id.b30DataMonthTv, R.id.b30DataYearTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30DataWeekTv:    //日
                setClearStyle(0);
                break;
            case R.id.b30DataMonthTv:   //月
                setClearStyle(1);
                break;
            case R.id.b30DataYearTv:    //年
                setClearStyle(2);
                break;
        }
    }

    /**
     * 切换数据(周月年)
     *
     * @param code 0_周 1_月 2_年
     */
    private void setClearStyle(int code) {
        if (b30DataWeekTv != null) {
            b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataWeekTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataMonthTv != null) {
            b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataMonthTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataYearTv != null) {
            b30DataYearTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataYearTv.setTextColor(Color.parseColor("#333333"));
        }

        if (getActivity() == null || getActivity().isFinishing())
            return;
        if (!NetUtils.checkEnable(getActivity())) return;
        switch (code) {
            case 0:
                b30DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_one);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(7);
                }

                break;
            case 1:
                b30DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(30);
                }
                break;
            case 2:
                b30DataYearTv.setTextColor(getResources().getColor(R.color.white));
                b30DataYearTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(365);
                }
                break;
        }
    }


    @Override
    public void showProDialog(int what) {
        showLoadingDialog("Loading...");
    }

    @Override
    public void closeProDialog(int what) {
        closeLoadingDialog();
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01://显示步数
                    if (mValues != null && sleepXList != null)
                        showStepsChat(mValues, stepXList);
                    break;
                case 0x02:
                    if (heartValues != null && heartXList != null)
                        showHeartChart(heartValues, heartXList);
                    break;
                case 0x03:
                    if (sleepVlaues != null && sleepXList != null)
                        showSleepChart(sleepVlaues, sleepXList);
                    break;
                case 0x04:
                    break;
            }
            return false;
        }
    });


    // 步数返回
    @Override
    public void b30StepData(final String stepStr, final int code) {
        if (runnableStep != null)
            mHandler.removeCallbacks(runnableStep);
        runnableStep = new Runnable() {
            @Override
            public void run() {
                stepXList.clear();
                mValues.clear();
                if (WatchUtils.isEmpty(stepStr)) {
                    showStepsChat(mValues, stepXList);
                    return;
                }
                MyLogUtil.d("----步数返回-", stepStr);
                String daydata = null;
                try {
                    JSONObject jsonObject = new JSONObject(stepStr);
                    if (jsonObject.getString("resultCode").equals("001")) {
                        daydata = jsonObject.getString("day");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (daydata == null) {
                    showStepsChat(mValues, stepXList);
                    return;
                }
                if (WatchUtils.isEmpty(daydata) || daydata.equals("[]")) {
                    showStepsChat(mValues, stepXList);
                    return;
                }

                stepList = gson.fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                }.getType());
                if (code == 365) { //年
                    /*用于计算年的步数*/
                    Map<String, Integer> stepSumMap = new HashMap<>();
                    Map<String, Integer> countMap = new HashMap<>();
                    /*用于计算年的步数的list*/
                    List<String> tempList = new ArrayList<>();
                    int sum = 0;
                    int count = 0;

                    Log.d("======数据长度", stepList.size() + "===" + stepList.toString());
                    for (int i = 0; i < stepList.size(); i++) {
                        String dateStr = stepList.get(i).getRtc();
                        String strDate = dateStr.substring(2, 7);
                        if (stepSumMap.get(strDate) != null) {

                            if (stepList.get(i).getStepNumber() != 0) {
                                count++;
                                sum += stepList.get(i).getStepNumber();
                            }
                        } else {

                            if (stepList.get(i).getStepNumber() != 0) {
                                count = 1;
                                sum = stepList.get(i).getStepNumber();
                            }

                        }
                        stepSumMap.put(strDate, sum);
                        countMap.put(strDate, count);
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
                        int tmpVSum = countMap.get(tempList.get(k));
                        int resultStep = stepSumMap.get(tempList.get(k)) / (tmpVSum == 0 ? 1 : tmpVSum);
                        mValues.add(resultStep);
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
                mHandler.sendEmptyMessage(0x01);
//                if (getActivity() != null && !getActivity().isFinishing()) {
//
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            showStepsChat(mValues, stepXList);
////                        }
////                    });
//                }

            }
        };
        mHandler.post(runnableStep);

    }

    // 展示步数图表
    private void showStepsChat(List<Integer> mValues, List<String> xList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        pointbar.clear();
        for (int i = 0; i < mValues.size(); i++) {
            pointbar.add(new BarEntry(i, mValues.get(i)));
        }
//        Log.e(TAG, "----步数大小=" + mValues.size());
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
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
        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(stepDataChartView);
        stepDataChartView.setMarker(dataMarkView);
        stepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        stepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        stepDataChartView.getAxisLeft().setEnabled(false);
        stepDataChartView.getXAxis().setSpaceMax(0.5f);
        stepDataChartView.animateXY(1000, 2000);//设置动画
    }

    // 心率返回
    @Override
    public void b30HeartData(final String heartStr, final int code) {
        if (runnableHeart != null)
            mHandler.removeCallbacks(runnableHeart);
        runnableHeart = new Runnable() {
            @Override
            public void run() {
                heartXList.clear();
                heartValues.clear();
                if (WatchUtils.isEmpty(heartStr)) {
                    showHeartChart(mValues, heartXList);
                    return;
                }
                Log.e(TAG, "----心率数据=" + heartStr);
                String heartData = null;
                try {
                    JSONObject jsonObject = new JSONObject(heartStr);
                    if (jsonObject.getString("resultCode").equals("001")) {
                        heartData = jsonObject.getString("heartRate");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (heartData == null) {
                    showHeartChart(mValues, heartXList);
                    return;
                }
                if (WatchUtils.isEmpty(heartData) || heartData.equals("[]")) {
                    showHeartChart(heartValues, heartXList);
                    return;
                }
                List<AvgHeartRate> heartList = gson.fromJson(heartData, new TypeToken<List<AvgHeartRate>>() {
                }.getType());
                if (code == 365) {    //年
                    Map<String, Integer> heartMap = new HashMap<>();
                    Map<String, Integer> countMap = new HashMap<>();
                    int heartSum = 0;
                    int Day = 0;
                    for (int i = 0; i < heartList.size(); i++) {
                        String strDate = heartList.get(i).getRtc().substring(2, 7);//2017-11-21
                        if (heartMap.get(strDate) != null) {

                            if (heartList.get(i).getAvgHeartRate() > 0) {
                                Day++;
                                heartSum += heartList.get(i).getAvgHeartRate();
                            }
                        } else {
                            if (heartList.get(i).getAvgHeartRate() > 0) {
                                Day = 1;
                                heartSum = heartList.get(i).getAvgHeartRate();
                            }
                        }
                        heartMap.put(strDate, heartSum);
                        countMap.put(strDate, Day);
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
                        int heartCount = (int) countMap.get(tempHeartList.get(i));
                        heartValues.add((int) integer / (heartCount == 0 ? 1 : heartCount));
                        heartXList.add(tempHeartList.get(i));
                    }
                } else {  //周或者月
                    for (AvgHeartRate avgHeart : heartList) {
                        int avgHeartRate = avgHeart.getAvgHeartRate();
                        heartValues.add(avgHeartRate);
                        heartXList.add(avgHeart.getRtc().substring(5, avgHeart.getRtc().length()));
                    }
                }
                mHandler.sendEmptyMessage(0x02);
//                if (getActivity() != null && !getActivity().isFinishing()) {
//
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            showHeartChart(heartValues, heartXList);
////                        }
////                    });
//                }
            }
        };
        mHandler.post(runnableHeart);
    }

    //展示心率图表
    private void showHeartChart(List<Integer> heartList, List<String> xlt) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        heartBarEntryList.clear();
        for (int i = 0; i < heartList.size(); i++) {
            heartBarEntryList.add(new BarEntry(i, (int) heartList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(heartBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
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


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
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

    //睡眠返回
    @Override
    public void b30SleepData(final String sleepStr, final int code) {
        if (runnableSleep != null)
            mHandler.removeCallbacks(runnableSleep);
        runnableSleep = new Runnable() {
            @Override
            public void run() {
                sleepVlaues.clear();
                sleepXList.clear();

                if (WatchUtils.isEmpty(sleepStr)) {
                    showSleepChart(sleepVlaues, sleepXList);
                    return;
                }
                Log.d("----睡眠数据返回-", sleepStr);
                String sleepData = null;
                try {
                    JSONObject jsonObject = new JSONObject(sleepStr);
                    if (jsonObject.getString("resultCode").equals("001")) {
                        sleepData = jsonObject.getString("sleepData");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sleepData == null) {
                    showSleepChart(sleepVlaues, sleepXList);
                    return;
                }
                if (WatchUtils.isEmpty(sleepData) || sleepData.equals("[]")) {
                    showSleepChart(sleepVlaues, sleepXList);
                    return;
                }
                List<NewsSleepBean> sleepBeanList = gson.fromJson(sleepData, new TypeToken<List<NewsSleepBean>>() {
                }.getType());
                if (code == 365) {    //年
                    /*保存计算睡眠年的数据*/
                    Map<String, Float> sumSleepMap = new HashMap<>();
                    Map<String, Integer> countMap = new HashMap<>();
                    int count = 0;
                    float sleepSum = 0;
                    for (int i = 0; i < sleepBeanList.size(); i++) {
                        String strDate = sleepBeanList.get(i).getRtc().substring(2, 7);// 月份
                        if (sumSleepMap.get(strDate) != null) {
                            int shallowSleep = sleepBeanList.get(i).getShallowSleep();
                            int deepSleep = sleepBeanList.get(i).getDeepSleep();
                            sleepSum += (shallowSleep + deepSleep);
                            if (shallowSleep > 0 || deepSleep > 0) {
                                count++;
                            }
                        } else {
                            int shallowSleep = sleepBeanList.get(i).getShallowSleep();
                            int deepSleep = sleepBeanList.get(i).getDeepSleep();
                            sleepSum = (shallowSleep + deepSleep);
                            if (shallowSleep > 0 || deepSleep > 0) {
                                count = 1;
                            }
                        }
                        sumSleepMap.put(strDate, sleepSum);
                        countMap.put(strDate, count);
                    }
                    List<String> tempSleepList = new ArrayList<>();
                    //遍历map
                    for (Map.Entry<String, Float> maps : sumSleepMap.entrySet()) {
                        tempSleepList.add(maps.getKey().trim());
                    }
                    //升序排列
                    Collections.sort(tempSleepList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    for (int k = 0; k < tempSleepList.size(); k++) {
                        float values = (float) sumSleepMap.get(tempSleepList.get(k)) / (float) 60;// 按小时算
                        int sleepCount = countMap.get(tempSleepList.get(k));
                        sleepVlaues.add(values / (sleepCount == 0 ? 1 : sleepCount));
                        sleepXList.add(tempSleepList.get(k));
                    }
                } else {// 周或月
                    for (NewsSleepBean sleepBean : sleepBeanList) {
                        int shallowSleep = sleepBean.getShallowSleep();
                        int deepSleep = sleepBean.getDeepSleep();
                        float sleepSum = (float) (shallowSleep + deepSleep) / (float) 60;// 按小时算
                        sleepVlaues.add(sleepSum);
                        sleepXList.add(sleepBean.getRtc().substring(5, sleepBean.getRtc().length()));
                    }
                }
                mHandler.sendEmptyMessage(0x03);
//                if (getActivity() != null && !getActivity().isFinishing()) {
//
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            showSleepChart(sleepVlaues, sleepXList);
////                        }
////                    });
//                }
            }
        };
        mHandler.post(runnableSleep);

    }

    //展示睡眠图表
    private void showSleepChart(List<Float> sleepVlaues, List<String> sleepXList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        sleepBarEntryList.clear();
        for (int i = 0; i < sleepVlaues.size(); i++) {
            sleepBarEntryList.add(new BarEntry(i, sleepVlaues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(sleepBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
//        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = sleepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (sleepVlaues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        sleepDataChartView.setData(bardata);
        sleepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        sleepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        sleepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        sleepDataChartView.setTouchEnabled(true);
        sleepDataChartView.setPinchZoom(false);
        sleepDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(sleepDataChartView, sleepXList);
        XAxis xAxis = sleepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        sleepDataChartView.getDescription().setEnabled(false);


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 0);
        dataMarkView.setChartView(sleepDataChartView);
        sleepDataChartView.setMarker(dataMarkView);


        sleepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        sleepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        sleepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        sleepDataChartView.getAxisLeft().setEnabled(false);
        sleepDataChartView.getXAxis().setSpaceMax(0.5f);
        sleepDataChartView.animateXY(1000, 2000);//设置动画
    }


    //血压返回
    @Override
    public void b30BloadData(final String bloadStr, final int code) {
        Log.d("----血压返回---", !WatchUtils.isEmpty(bloadStr) ? "血压 null" : bloadStr);
    }


}