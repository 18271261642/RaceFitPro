package com.example.bozhilun.android.b31.bpoxy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bozhilun.android.B18I.b18iutils.Constant;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.bpoxy.markview.SPMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_LOWSPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_SPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_LOWSPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_SPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_LOWSPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_SPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.*;

/**
 * Created by Administrator on 2018/12/20.
 */

public class VpSpo2hUtil {
    private static final String TAG = VpSpo2hUtil.class.getSimpleName();
    //处理血氧数据的工具类
    Spo2hOriginUtil spo2hOriginUtil;
    //早上0到8点的数据
    List<Spo2hOriginData> moringData;


    LineChart mChartViewSpo2h;
    SPMarkerView mMarkviewSpo2h;
    ChartViewUtil mChartUtilSpo2h;

    LineChart mChartViewHeart;
    SPMarkerView mMarkviewHeart;
    ChartViewUtil mChartUtilHeart;

    LineChart mChartViewSleep;
    SPMarkerView mMarkviewSleep;
    ChartViewUtil mChartUtilSleep;

    LineChart mChartViewBreath;
    SPMarkerView mMarkviewBeath;
    ChartViewUtil mChartUtilBreath;


    LineChart mChartViewLowspo2h;
    SPMarkerView mMarkviewLowspo2h;
    ChartViewUtil mChartUtilLowspo2h;


    boolean mModelIs24 = true;

    public VpSpo2hUtil() {

    }

    public void setLinearChart(LineChart chartViewSpo2h,
                               LineChart chartViewHeart,
                               LineChart chartViewSleep,
                               LineChart chartViewBreath,
                               LineChart chartViewLowspo2h) {

        this.mChartViewSpo2h = chartViewSpo2h;
        this.mChartViewHeart = chartViewHeart;
        this.mChartViewSleep = chartViewSleep;
        this.mChartViewBreath = chartViewBreath;
        this.mChartViewLowspo2h = chartViewLowspo2h;
    }



    public void setMarkView(Context context, int layoutId) {
        initMarkView(context, layoutId);
    }

    public void setModelIs24(boolean modelIs24) {
        this.mModelIs24 = modelIs24;
    }


    private void initMarkView(Context context, int layoutId) {
        mMarkviewSpo2h = new SPMarkerView(context, layoutId, mModelIs24, CHART_MIDDLE_SPO2H, TYPE_SPO2H);
        mMarkviewHeart = new SPMarkerView(context, layoutId, mModelIs24, CHART_MIDDLE_HEART, TYPE_HEART);
        mMarkviewSleep = new SPMarkerView(context, layoutId, mModelIs24, CHART_MIDDLE_SLEEP, TYPE_SLEEP);
        mMarkviewBeath = new SPMarkerView(context, layoutId, mModelIs24, CHART_MIDDLE_BREATH, TYPE_BREATH);
        mMarkviewLowspo2h = new SPMarkerView(context, layoutId, mModelIs24, CHART_MIDDLE_LOWSPO2H, TYPE_LOWSPO2H);
        initSpo2hChatUitl();
    }

    private void initSpo2hChatUitl() {
        String strNoData = "No data";
        mChartUtilSpo2h = new ChartViewUtil(mChartViewSpo2h, mMarkviewSpo2h, mModelIs24, CHART_MAX_SPO2H, CHART_MIN_SPO2H, strNoData, TYPE_SPO2H);
        mChartUtilHeart = new ChartViewUtil(mChartViewHeart, mMarkviewHeart, mModelIs24, CHART_MAX_HEART, CHART_MIN_HEART, strNoData, TYPE_HEART);
        mChartUtilSleep = new ChartViewUtil(mChartViewSleep, mMarkviewSleep, mModelIs24, CHART_MAX_SLEEP, CHART_MIN_SLEEP, strNoData, TYPE_SLEEP);
        mChartUtilBreath = new ChartViewUtil(mChartViewBreath, mMarkviewBeath, mModelIs24, CHART_MAX_BREATH, CHART_MIN_BREATH, strNoData, TYPE_BREATH);
        mChartUtilLowspo2h = new ChartViewUtil(mChartViewLowspo2h, mMarkviewLowspo2h, mModelIs24, CHART_MAX_LOWSPO2H, CHART_MIN_LOWSPO2H, strNoData, TYPE_LOWSPO2H);
    }

    public void setData(List<Spo2hOriginData> originSpo2hList) {
        if (originSpo2hList == null || originSpo2hList.isEmpty()) {
            moringData = new ArrayList<>();
        } else {
            moringData = getMoringData(originSpo2hList);
        }
    }

    public void showAllChartView() {
        showSpo2hView();
        showHeartView();
        showSleepView();
        showBreath();
        showLowSpo2h();
    }

    private void showNullChartView() {
        showNullSpo2hView();
        showNullHeartView();
        showNullSleepView();
        showNullBreathView();
        showNullLowSpo2hView();
    }


    /**
     * 获取最大、最小、平均、参考
     */
    public void getDataArray() {
        if (moringData == null || moringData.isEmpty()) {
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取低氧数据[最大，最小，平均]       *参考：取低氧最大值，大于20，则显示偏高，其他显示正常
        int[] onedayDataArrLowSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_LOWSPO2H);
        Log.e(TAG, "showLowSpo2h [最大，最小，平均]: " + Arrays.toString(onedayDataArrLowSpo2h));

        //获取呼吸率数据[最大，最小，平均]     *参考：取呼吸率最大值，低于18，则显示偏低，其他显示正常
        int[] onedayDataArrLowBreath = spo2hOriginUtil.getOnedayDataArr(TYPE_BREATH);
        Log.e(TAG, "showBreath [最大，最小，平均]: " + Arrays.toString(onedayDataArrLowBreath));

        //获取睡眠数据[最大，最小，平均]       *参考：取睡眠活动荷最大值，大于70，则显示偏高，其他显示正常
        int[] onedayDataArrSleep = spo2hOriginUtil.getOnedayDataArr(TYPE_SLEEP);
        Log.e(TAG, "showSleep [最大，最小，平均]: " + Arrays.toString(onedayDataArrSleep));

        //获取心脏负荷数据[最大，最小，平均]   *参考：取心脏负荷最大值，大于40，则显示偏高，其他显示正常
        int[] onedayDataArrHeart = spo2hOriginUtil.getOnedayDataArr(TYPE_HEART);
        Log.e(TAG, "showHeartView [最大，最小，平均]: " + Arrays.toString(onedayDataArrHeart));

        //获取血氧数据[最大，最小，平均]       *参考：取血氧最小值，低于95，则显示偏低，其他显示正常
        int[] onedayDataArrSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_SPO2H);
        Log.e(TAG, "showSpo2hView [最大，最小，平均]: " + Arrays.toString(onedayDataArrSpo2h));
    }


    /**
     * 呼吸暂停列表
     */
    public void getApnea() {
        if (moringData == null || moringData.isEmpty()) {
            Log.e(TAG,"---------return掉了----");
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        List<Map<String, Float>> apneaList = spo2hOriginUtil.getApneaList();
        for (int i = 0; i < apneaList.size(); i++) {
            Map<String, Float> data = apneaList.get(i);
            float time = data.get("time");
            float value = data.get("value");
            Log.e(TAG, "showApnea: 频现时间值:" + TranStrUtil.getSpo2hTimeString((int) time, mModelIs24) + ",次数值=" + value);
        }

    }


    /**
     * 获取OSAHS结果
     *
     * @return
     */
    public String getOsahs(Context context) {
        if (moringData == null || moringData.isEmpty()) {
            return "--";
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        int osahs = spo2hOriginUtil.getIsHypoxia();
        if (osahs >= 3) {
            return context.getResources().getString(R.string.severe);
        } else if (osahs >= 2) {
            return context.getResources().getString(R.string.moderate);
        } else if (osahs >= 1) {
            return context.getResources().getString(R.string.vpspo2h_state_little);
        } else {
            return context.getResources().getString(R.string.vpspo2h_state_normal);
        }
    }




    /**
     * 展示低血氧的图表
     */
    public void showLowSpo2h() {
        if (moringData == null || moringData.isEmpty()) {
            showNullChartView();
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取处理完的低氧数据
        List<Map<String, Float>> tenMinuteDataLowSpo2h = spo2hOriginUtil.getTenMinuteData(TYPE_LOWSPO2H);
        mChartUtilLowspo2h.updateChartView(tenMinuteDataLowSpo2h);
        mMarkviewLowspo2h.setData(tenMinuteDataLowSpo2h);
    }

    /**
     * 展示呼吸率的图表
     */
    public void showBreath() {
        if (moringData == null || moringData.isEmpty()) {
            showNullChartView();
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取处理完的呼吸率数据
        List<Map<String, Float>> tenMinuteDataBreath = spo2hOriginUtil.getTenMinuteData(TYPE_BREATH);
        mChartUtilBreath.updateChartView(tenMinuteDataBreath);
        mMarkviewBeath.setData(tenMinuteDataBreath);
    }

    /**
     * 展示睡眠的图表
     */
    public void showSleepView() {
        if (moringData == null || moringData.isEmpty()) {
            showNullChartView();
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取处理完的睡眠数据
        List<Map<String, Float>> tenMinuteDataSleep = spo2hOriginUtil.getTenMinuteData(TYPE_SLEEP);
        //更新睡眠数据的图表
        mChartUtilSleep.updateChartView(tenMinuteDataSleep);
        //更新睡眠数据的MarkView
        mMarkviewSleep.setData(tenMinuteDataSleep);
    }

    /**
     * 展示心脏负荷的图表
     */
    public void showHeartView() {
        if (moringData == null || moringData.isEmpty()) {
            showNullChartView();
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取处理完的心脏负荷数据
        List<Map<String, Float>> tenMinuteDataHeart = spo2hOriginUtil.getTenMinuteData(TYPE_HEART);
        //更新心脏负荷的图表
        mChartUtilHeart.updateChartView(tenMinuteDataHeart);
        //更新心脏负荷的MarkView
        mMarkviewHeart.setData(tenMinuteDataHeart);
    }

    /**
     * 展示血氧数据的图表
     */
    public void showSpo2hView() {
        if (moringData == null || moringData.isEmpty()) {
            showNullChartView();
            return;
        }
        if (spo2hOriginUtil == null) {
            spo2hOriginUtil = new Spo2hOriginUtil(moringData);
        }
        //获取处理完的血氧数据
        List<Map<String, Float>> tenMinuteDataBreathBreak = spo2hOriginUtil.getTenMinuteData(TYPE_BEATH_BREAK);
        List<Map<String, Float>> tenMinuteDataSpo2h = spo2hOriginUtil.getTenMinuteData(TYPE_SPO2H);
        //更新血氧数据的图表
        mChartUtilSpo2h.setBeathBreakData(tenMinuteDataBreathBreak);
        mChartUtilSpo2h.updateChartView(tenMinuteDataSpo2h);
        //更新血氧数据的MarkView
        mMarkviewSpo2h.setData(tenMinuteDataSpo2h);
        mMarkviewSpo2h.setBeathBreakData(tenMinuteDataBreathBreak);
    }


    private void showNullSpo2hView() {
        if (mChartUtilSpo2h != null) {
            mChartUtilSpo2h.updateChartView(Collections.EMPTY_LIST);
        }
    }

    private void showNullLowSpo2hView() {
        if (mChartUtilLowspo2h != null) {
            mChartUtilLowspo2h.updateChartView(Collections.EMPTY_LIST);
        }
    }

    private void showNullBreathView() {
        if (mChartUtilBreath != null) {
            mChartUtilBreath.updateChartView(Collections.EMPTY_LIST);
        }
    }

    private void showNullSleepView() {
        if (mChartUtilSleep != null) {
            mChartUtilSleep.updateChartView(Collections.EMPTY_LIST);
        }
    }

    private void showNullHeartView() {
        if (mChartUtilHeart != null) {
            mChartUtilHeart.updateChartView(Collections.EMPTY_LIST);
        }
    }


    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<Spo2hOriginData> getMoringData(List<Spo2hOriginData> originSpo2hList) {
        List<Spo2hOriginData> moringData = new ArrayList<>();
        if(originSpo2hList == null || originSpo2hList.isEmpty())
            return moringData;
        for (Spo2hOriginData spo2hOriginData : originSpo2hList) {
            if (spo2hOriginData.getmTime().getHMValue() < 8 * 60) {
                moringData.add(spo2hOriginData);
            }
        }
        return moringData;
    }

}
