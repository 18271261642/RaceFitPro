package com.example.bozhilun.android.b31.bpoxy.util;

import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.example.bozhilun.android.b31.bpoxy.enums.Constants;
import com.example.bozhilun.android.b31.bpoxy.markview.SPMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import java.util.List;
import java.util.Map;

import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_LOWSPO2H;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_SPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BREATH;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HEART;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_LOWSPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SLEEP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;
import static com.veepoo.protocol.util.Spo2hOriginUtil.TIME_FLAG;

/**
 * 血氧数据图表的工具，包含的功能有(图表的属性设置，数据加载，ESpo2hDataType不同数据类型的差异化处理)
 * Created by timaimee on 2017/7/26.
 */
public class ChartViewUtil {
    public static final boolean IS_STARTWITH_TODAY = true;
    public static final int YESTERDAY_HOUR_START = 22;
    public static final int TODAY_HOUR_START = 0;
    public static final int TODAY_HOUR_END = 8;
    List<Map<String, Float>> beathBreakData;

    LineChart chartView;
    SPMarkerView markerView;
    boolean modelIs24 = false;
    private float maxvalue = 100;
    private float minvalue = 0;
    private String strNodata;
    private ESpo2hDataType type;
    int spo2hDotColor = Color.RED;

    //x轴的颜色
    private int xColor = Color.WHITE;
    //无数据显示的颜色
    private int noDataColor = Color.WHITE;



    public ChartViewUtil(LineChart chartView, SPMarkerView markerView, boolean modelIs24,
                         float maxvalue, float minvalue, String strNodata, ESpo2hDataType type) {
        this.chartView = chartView;
        this.modelIs24 = modelIs24;
        this.markerView = markerView;
        this.maxvalue = maxvalue;
        this.minvalue = minvalue;
        this.strNodata = strNodata;
        this.type = type;
        initChartView();
        addLimmitLines(type);
    }


    private void initChartView() {
        setChartView();
    }

    public void setSpo2hDotColor(int spo2hDotColor) {
        this.spo2hDotColor = spo2hDotColor;
    }

    private void setChartView() {
        chartView.setDrawGridBackground(false);
        chartView.setNoDataText(strNodata);
        chartView.setNoDataTextColor(noDataColor);
        chartView.getDescription().setEnabled(false);
        chartView.setTouchEnabled(true);
        chartView.setExtraRightOffset(5f);
        chartView.setScaleEnabled(false);
        chartView.setPinchZoom(false);
        if (markerView != null) {
            chartView.setMarker(markerView);
        }
        setXview();
        setYview();
        Legend l = chartView.getLegend();
        l.setForm(Legend.LegendForm.NONE);
        l.setYOffset(-5);
        chartView.setData(new LineData());
    }

    public int getxColor() {
        return xColor;
    }

    public void setxColor(int xColor) {
        this.xColor = xColor;
        setXview();

    }

    public int getNoDataColor() {
        return noDataColor;
    }

    public void setNoDataColor(int noDataColor) {
        this.noDataColor = noDataColor;
        setChartView();
    }

    private void setXview() {
        XAxis xAxis = chartView.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(6, true);
        xAxis.setYOffset(10);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setCenterAxisLabels(false);
        xAxis.setTextColor(xColor);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xPosition, AxisBase axis) {
                int valueInt = (int) xPosition;
                return getXTimeByPosition(valueInt);
            }
        });
    }


    private void setYview() {
        YAxis leftAxis = chartView.getAxisLeft();
        leftAxis.setAxisMaximum(maxvalue);
        leftAxis.setLabelCount(5, true);
        leftAxis.setAxisMinimum(minvalue);
        leftAxis.setGranularity(3f);

        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawLimitLinesBehindData(false);

        chartView.getAxisRight().setEnabled(false);
    }

    public void drawYLable(boolean isDrawLabel, int lableCount) {
        YAxis leftAxis = chartView.getAxisLeft();
        leftAxis.setDrawGridLines(isDrawLabel);
        leftAxis.setLabelCount(lableCount, true);
        leftAxis.setDrawLabels(isDrawLabel);
    }

    private void addLimmitLines(ESpo2hDataType tag) {
        switch (tag) {
            case TYPE_SPO2H:
                addLimmitLine(new int[]{88, 91, 94, 97, (int) CHART_MAX_SPO2H}, TYPE_SPO2H, 5);
                break;
            case TYPE_HEART:
                addLimmitLine(new int[]{0, 20, 40, (int) CHART_MAX_HEART}, TYPE_HEART, 8);
                break;
            case TYPE_SLEEP:
                addLimmitLine(new int[]{0, 20, 50, (int) CHART_MAX_SLEEP}, TYPE_SLEEP, 9);
                break;
            case TYPE_BREATH:
                addLimmitLine(new int[]{0, 26, (int) CHART_MAX_BREATH}, TYPE_BREATH, 3);
                break;
            case TYPE_LOWSPO2H:
                addLimmitLine(new int[]{0, 100, (int) CHART_MAX_LOWSPO2H}, TYPE_LOWSPO2H, 4);
                break;
            case TYPE_HRV:
                addLimmitLine(new int[]{0, 110, 210, CHART_MAX_HRV}, TYPE_HRV, 22);
                break;
        }
    }

    public void addLimmitLine(int[] lines, final ESpo2hDataType tag, int count) {
        if (lines == null || lines.length == 0) {
            return;
        }
        YAxis leftAxis = chartView.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(count, true);
        leftAxis.removeAllLimitLines();
        for (int i = 0; i < lines.length; i++) {
            LimitLine limmitLine = getLimmitLine(lines[i]);
            leftAxis.addLimitLine(limmitLine);
        }
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getLeftYText(tag, (int) value);
            }
        });
    }

    /**
     * 获取左侧Y轴的文本
     *
     * @param ESpo2hDataType
     * @param value
     * @return
     */
    private String getLeftYText(ESpo2hDataType ESpo2hDataType, int value) {
        switch (ESpo2hDataType) {
            case TYPE_HEART:
                if (value == 0 || value == 20 || value == 40 || value == 70) {
                    return String.valueOf(value);
                } else {
                    return "";
                }
            case TYPE_SLEEP:
                if (value == 0 || value == 20 || value == 50 || value == 80) {
                    return String.valueOf(value);
                } else {
                    return "";
                }
            case TYPE_BREATH:
                if (value == 0 || value == 50) {
                    return String.valueOf(value);
                } else if (value == 25) {
                    return String.valueOf(value + 1);
                } else {
                    return "";
                }
            case TYPE_LOWSPO2H:
                if (value == 0 || value == 100 || value == 300) {
                    return String.valueOf(value);
                } else {
                    return "";
                }
            case TYPE_SPO2H:
                if (value == 100 || value == 97 || value == 94 || value == 91) {
                    return String.valueOf(value);
                } else if (value == 88) {
                    return String.valueOf(70);
                } else {
                    return "";
                }
            case TYPE_HRV:
                if (value == 0 || value == 210 || value == 110) {
                    return String.valueOf(value);
                } else {
                    return "";
                }
        }

        return "";
    }

    private LimitLine getLimmitLine(int value) {
        LimitLine limitLine = new LimitLine(value, "");
        limitLine.setLineWidth(1f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextSize(10f);
        limitLine.setLineColor(Color.argb(70, 255, 255, 255));
        limitLine.setTextColor(Color.RED);
        return limitLine;

    }

    private LineDataSet createSet(int[] colors) {
        LineDataSet set1 = new LineDataSet(null, "");
        set1.setDrawIcons(false);
        set1.setColor(Color.WHITE);
        set1.setCircleRadius(2f);
        if (type == TYPE_SPO2H) {
            set1.setCircleColors(colors);
            set1.setDrawCircles(true);
        } else {
            set1.setCircleColors(Color.WHITE);
            set1.setDrawCircles(false);
        }
        set1.setCubicIntensity(0.2f);
        set1.setDrawHighlightIndicators(false);
        set1.setHighlightLineWidth(2f);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set1.setLineWidth(2.0f);
        set1.setFillColor(Color.argb(255, 255, 255, 255));
        set1.setFillAlpha(40);
        set1.setDrawFilled(true);
        set1.setDrawCircleHole(false);
        set1.setFormLineWidth(1f);
        set1.setDrawValues(false);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(8.f);
        return set1;

    }

    /**
     * 设置对应的呼吸暂停数据
     *
     * @param beathBreakData
     */
    public void setBeathBreakData(List<Map<String, Float>> beathBreakData) {
        this.beathBreakData = beathBreakData;
    }

    /***
     * 更新ChartView
     * @param listData
     */
    public void updateChartView(final List<Map<String, Float>> listData) {
        LineData data = chartView.getData();
        ILineDataSet set = null;
        if (data == null) {
            data = new LineData();
            chartView.setData(data);
        } else {
            set = data.getDataSetByIndex(0);
            if (set != null) {
                data.removeDataSet(0);
            }
        }
        if (listData == null || listData.isEmpty()) {
            chartView.setData(null);
            chartView.invalidate();
            return;
        }
        /*设置circles的colors*/
        int X_MAX_COUNT = getXmaxcount();
        int colors[] = new int[X_MAX_COUNT];
        /*因为点要进行移动，所以找到最小的点，一会要进行相减*/
        set = createSet(colors);
        data.addDataSet(set);
        float valueList[] = new float[X_MAX_COUNT];
        initData(valueList);
        float times[] = new float[X_MAX_COUNT];
        for (int i = 0; i < listData.size(); i++) {
            float hValue = listData.get(i).get("value");
            float time = listData.get(i).get("time");
            if (IS_STARTWITH_TODAY && time > (TODAY_HOUR_END - TODAY_HOUR_START) * 60) continue;
            if (!IS_STARTWITH_TODAY && (time >= TODAY_HOUR_END * 60) && time < YESTERDAY_HOUR_START * 60)
                continue;
            int xPosition = getXpositionByTime((int) time);
            valueList[xPosition] = hValue;
            times[xPosition] = time;

            //在图表上的显示，如果超过最大值，就显示在最大值的位置
            if (type == TYPE_SLEEP && hValue > Constants.CHART_MAX_SLEEP) {
                valueList[xPosition] = Constants.CHART_MAX_SLEEP;
            }
            if (type == TYPE_HEART && hValue > Constants.CHART_MAX_HEART) {
                valueList[xPosition] = Constants.CHART_MAX_HEART;
            }
            if (type == TYPE_SPO2H) {
                if (hValue < 91) {
                    valueList[xPosition] = changeToBig(hValue);
                }
            }

        }
        for (int i = 0; i < valueList.length; i++) {
            Entry e = new Entry(i, valueList[i]);
            e.setData(times[i]);
            //其他的值是可以为0的
            if (type == TYPE_SPO2H) {
                if (valueList[i] > 0) {
                    data.addEntry(e, 0);
                }
            } else {
                if (valueList[i] >= 0) {
                    data.addEntry(e, 0);
                }
            }
        }
        setSpace(X_MAX_COUNT, valueList);
        chartView.setData(data);
        /*设置血氧呼吸暂停的点*/
        setBreathDot(data);
        data.notifyDataChanged();
        chartView.animateX(500);
        chartView.fitScreen();
        chartView.notifyDataSetChanged();
        chartView.invalidate();
    }

    private void setSpace(int x_MAX_COUNT, float[] valueList) {
        int minSpace = getMinSpace(valueList);
        chartView.getXAxis().setSpaceMin(minSpace);
        int maxSpace = getMaxSpace(x_MAX_COUNT, valueList);
        if (minSpace == maxSpace) {
            chartView.getXAxis().setSpaceMax(0);
        } else {
            chartView.getXAxis().setSpaceMax(x_MAX_COUNT - maxSpace);
        }
    }

    private int getMaxSpace(int x_MAX_COUNT, float[] valueList) {
        int maxspace = x_MAX_COUNT;
        for (int i = valueList.length - 1; i > 0; i--) {
            if (valueList[i] > -1) {
                maxspace = i;
                break;
            }
        }
        return maxspace;
    }

    private int getMinSpace(float[] valueList) {
        int minspace = 0;
        for (int i = 0; i < valueList.length; i++) {
            if (valueList[i] > -1) {
                minspace = i;
                break;
            }
        }
        return minspace;
    }

    private void setBreathDot(LineData data) {
        if (type == TYPE_SPO2H) {
            LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSetByIndex != null) {
                int entryCount = dataSetByIndex.getEntryCount();
                int setColor[] = new int[entryCount];
                for (int i = 0; i < entryCount; i++) {
                    setColor[i] = Color.TRANSPARENT;
                    Entry entryForIndex = dataSetByIndex.getEntryForIndex(i);
                    if (entryForIndex != null) {
                        try {
                            float timeValue = (float) entryForIndex.getData();
                            if (beathBreakContainSpo2h(timeValue)) {
                                setColor[i] = spo2hDotColor;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                }
                dataSetByIndex.setCircleColors(setColor);
            }
        }
    }

    /**
     * 初使化为-1
     *
     * @param data
     */
    private void initData(float[] data) {
        if (data == null || data.length == 0) {

        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = -1;
            }
        }
    }

    /**
     * 调整血氧低于91的显示
     *
     * @param value
     * @return
     */
    public float changeToBig(float value) {
        float v = value;
        if (value < 91) {
            v = (value - 70) / (91 - 70) * (91 - 88) + 88;
        }
        return v;
    }

    /**
     * 通过对应时间来获取横坐标X的位置
     *
     * @param time 时间
     * @return
     */
    private int getXpositionByTime(int time) {
        if (IS_STARTWITH_TODAY) {
            return time / TIME_FLAG;
        } else {
            return (time + 1440 - YESTERDAY_HOUR_START * 60) % 1440 / TIME_FLAG;
        }
    }

    /**
     * 通过横坐标X的位置来获取对应的时间
     *
     * @param xPosition
     * @return
     */
    private String getXTimeByPosition(int xPosition) {
        int time;
        if (IS_STARTWITH_TODAY) {
            /*只存0点到8点的数据*/
            time = xPosition * 10;
        } else {
            /*只存22点到8点的数据*/
            time = (xPosition * TIME_FLAG + YESTERDAY_HOUR_START * 60) % (24 * 60);
        }
        return TranStrUtil.getSpo2hTimeString(time, modelIs24);

    }

    /**
     * 获取X轴的最大个数
     *
     * @return
     */
    private int getXmaxcount() {
        if (IS_STARTWITH_TODAY) {
            return (TODAY_HOUR_END - TODAY_HOUR_START) * (60 / TIME_FLAG);
        } else {
            return TODAY_HOUR_END * (60 / TIME_FLAG) + (24 - YESTERDAY_HOUR_START) * (60 / TIME_FLAG);
        }
    }


    /**
     * 血氧的点，呼吸暂停有数显示红色
     *
     * @param timeValue
     * @return
     */
    public boolean beathBreakContainSpo2h(float timeValue) {
        if (beathBreakData != null && !beathBreakData.isEmpty()) {
            for (int i = 0; i < beathBreakData.size(); i++) {
                float time = beathBreakData.get(i).get("time");
                if (time == timeValue) {
                    float value = beathBreakData.get(i).get("value");
                    if (value != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
