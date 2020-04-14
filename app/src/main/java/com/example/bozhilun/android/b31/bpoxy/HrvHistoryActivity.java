package com.example.bozhilun.android.b31.bpoxy;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.bpoxy.markview.SPMarkerView;
import com.example.bozhilun.android.b31.bpoxy.util.ChartViewUtil;
import com.example.bozhilun.android.b31.bpoxy.util.HrvDescripterUtil;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHRVOriginDataListener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.vp.cso.hrvreport.JNIChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_HRV;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;


public class HrvHistoryActivity extends WatchBaseActivity {
    private static final String TAG = HrvHistoryActivity.class.getSimpleName();
    LineChart mChartViewHRV, mChartViewHRVHome;
    ListView mListView;
    SPMarkerView mMarkviewHrv;
    //24小时制显示时间
    boolean mModelIs24 = false;
    String strNoData = "无数据";
    JNIChange mJNIChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vphrv_activity_history);
        mJNIChange = new JNIChange();
        initChartView();
        readHrv();
        updateList(new int[]{0, 11, 32, 23, 14, 0});
    }


    private void initChartView() {
        mChartViewHRV = (LineChart) findViewById(R.id.block_chartview_hrv);
        mChartViewHRVHome = (LineChart) findViewById(R.id.block_chartview_hrv_home);
        mListView = (ListView) findViewById(R.id.lorenz_list_descripe);
        mMarkviewHrv = new SPMarkerView(getApplicationContext(), R.layout.vpspo2h_markerview, mModelIs24, CHART_MIDDLE_HRV, TYPE_HRV);
    }


    List<HRVOriginData> originHRVList = new ArrayList<>();

    public void readHrv() {
        VPOperateManager.getMangerInstance(HrvHistoryActivity.this).readHRVOrigin(new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        }, new IHRVOriginDataListener() {
            @Override
            public void onReadOriginProgress(float progress) {
                Log.i(TAG, "onReadOriginProgress: progress=" + progress);
            }

            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {

            }

            @Override
            public void onHRVOriginListener(HRVOriginData hrvOriginData) {
                originHRVList.add(hrvOriginData);
            }

            @Override
            public void onDayHrvScore(int day, String date, int hrvSocre) {

            }

            @Override
            public void onReadOriginComplete() {
                updateHrvView();
            }
        }, 1);
    }

    private void updateHrvView() {
        List<HRVOriginData> data0to8 = getMoringData(originHRVList);
        HRVOriginUtil mHrvOriginUtil = new HRVOriginUtil(data0to8);
        List<Map<String, Float>> tenMinuteData = mHrvOriginUtil.getTenMinuteData();
        //历史界面
        showHistoryView(tenMinuteData);
        //主界面
        showHomeView(tenMinuteData);
        //分析报告界面
        showResult(originHRVList);
    }


    private void showResult(List<HRVOriginData> originHRVList) {
        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        double[] lorenData = hrvScoreUtil.getLorenData(originHRVList);
        if (lorenData == null || lorenData.length < 1500) {
            return;
        }
        int[] bufferdata = new int[lorenData.length];
        for (int i = 0; i < bufferdata.length; i++) {
            bufferdata[i] = (int) lorenData[i];
        }
        int[] result = null;
        try {
            result = mJNIChange.hrvAnalysisReport(bufferdata, bufferdata.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            return;
        }
        updateList(result);
    }

    private void updateList(int[] result) {
        setListViewHeightBasedOnChildren(mListView);
        List<Map<String, Object>> repoListData = getRepoListData(result);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, repoListData,
                R.layout.vpspo2h_item_lorendes, new String[]{"title", "info", "level"},
                new int[]{R.id.loren_descripe_title, R.id.loren_descripe_info, R.id.loren_descripe_level});
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(null);
        mListView.setFocusable(false);
    }


    private void showHistoryView(List<Map<String, Float>> tenMinuteData) {
        ChartViewUtil chartViewUtil = new ChartViewUtil(mChartViewHRV, mMarkviewHrv, mModelIs24,
                CHART_MAX_HRV, CHART_MIN_HRV, strNoData, TYPE_HRV);
        chartViewUtil.updateChartView(tenMinuteData);
        mMarkviewHrv.setData(tenMinuteData);
    }

    private void showHomeView(List<Map<String, Float>> tenMinuteData) {
        ChartViewUtil chartViewUtilHome = new ChartViewUtil(mChartViewHRVHome, null, mModelIs24,
                CHART_MAX_HRV, CHART_MIN_HRV, strNoData, TYPE_HRV);
        mChartViewHRVHome.getAxisLeft().removeAllLimitLines();
        mChartViewHRVHome.getAxisLeft().setDrawLabels(false);
        chartViewUtilHome.updateChartView(tenMinuteData);
        LineData data = mChartViewHRVHome.getData();
        LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
        if (dataSetByIndex != null) {
            dataSetByIndex.setDrawFilled(false);
            dataSetByIndex.setColor(Color.parseColor("#ff5232"));
        }
    }

    private List<Map<String, Object>> getRepoListData(int[] data) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (data == null || data.length == 0) {
            return list;
        }
        HrvDescripterUtil hrv = new HrvDescripterUtil(getApplicationContext());
        String[] repoTitle = hrv.getRepoTitle();
        for (int i = 1; i < data.length - 1; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            int mapInt = (i + 1) * 100 + +data[i];
            map.put("title", repoTitle[i]);
            map.put("info", hrv.getRepoInfo(mapInt));
            map.put("level", hrv.getLevel(mapInt));
            list.add(map);
        }
        return list;
    }

    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<HRVOriginData> getMoringData(List<HRVOriginData> originSpo2hList) {
        List<HRVOriginData> moringData = new ArrayList<>();
        for (HRVOriginData HRVOriginData : originSpo2hList) {
            if (HRVOriginData.getmTime().getHMValue() < 8 * 60) {
                moringData.add(HRVOriginData);
            }
        }
        return moringData;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter;
        listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


}

