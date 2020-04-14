package com.example.bozhilun.android.w30s.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.bean.UpHeartBean;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.bean.W30SHeartDataS;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.w30s.views.W30CusHeartView;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 心率数据
 * @author： 安
 * @crateTime: 2017/10/31 16:50
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHearteDataActivity extends WatchBaseActivity {
    private static final String TAG = "W30SHearteDataActivity";
    //    private String[] StringDate = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};//X轴的标注
//    private List<PointValue> mPointValues = new ArrayList<>();
//    private List<AxisValue> mAxisValues = new ArrayList<>();
//    @BindView(R.id.leaf_chart)
//    LeafLineChart leafLineChart;
    @BindView(R.id.heartedata_list)
    ListView listView;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    W30sHearteDataAdapter dataAdapter = null;
    String is18i;
    @BindView(R.id.bar_mores)
    TextView barMores;
    @BindView(R.id.heart_chart)
    W30CusHeartView lineChart;
    @BindView(R.id.image_data_type)
    ImageView image_data_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_hearte_data_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.string_harete_data));
//        leafLineChart.setVisibility(View.GONE);
        barMores.setVisibility(View.GONE);
        is18i = getIntent().getStringExtra("is18i");
        if (heartList == null) heartList = new ArrayList<>();
        if (lineChart != null) lineChart.setRateDataList(heartList);
//        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
//            @Override
//            public void onNext(String result) {
//                Log.e("------返回--", result);
//                if (result != null) {
//                    HeartDataBean heartDataBean = new Gson().fromJson(result, HeartDataBean.class);
//                    List<HeartDataBean.HeartRateBean> heartDataList = heartDataBean.getHeartRate();
//                    if (heartNewDataList != null) heartNewDataList.clear();
//                    for (int i = 0; i < heartDataList.size(); i++) {
//                        if (heartNewDataList != null) {
//                            //2018-04-03 02:00
//                            String substring = heartDataList.get(i).getRtc().substring(11, 16);
//                            if (!heartNewDataList.contains(substring)) {
//                                heartNewDataList.add(heartDataList.get(i));
//                            }
//                        }
//                    }
//
//                    //升序排列
//                    Collections.sort(heartNewDataList, new Comparator<HeartDataBean.HeartRateBean>() {
//                        @Override
//                        public int compare(HeartDataBean.HeartRateBean o1, HeartDataBean.HeartRateBean o2) {
//                            return o1.getRtc().compareTo(o2.getRtc());
//                        }
//                    });
//
//                    if (heartNewDataList != null) {
//                        dataAdapter = new H9HearteDataAdapter(W30SHearteDataActivity.this, heartNewDataList);
////                    for (int i = 0; i < heartDataList.size(); i++) {
////                        HeartDataBean.HeartRateBean heartRateBean = heartDataList.get(i);
////                        Log.d("-----数据-ssss---", heartRateBean.getRtc() + "===" + heartRateBean.getHeartRate());
////                    }
//                        heartedataList.setAdapter(dataAdapter);
//                        dataAdapter.notifyDataSetChanged();
//                    }
//
////                    heartDataList = getHeartDataList(result);
////                    if (heartDataList != null) {
////                        dataAdapter = new H9HearteDataAdapter(H9HearteDataActivity.this, heartDataList);
//////                    for (int i = 0; i < heartDataList.size(); i++) {
//////                        HeartDataBean.HeartRateBean heartRateBean = heartDataList.get(i);
//////                        Log.d("-----数据-ssss---", heartRateBean.getRtc() + "===" + heartRateBean.getHeartRate());
//////                    }
////                        heartedataList.setAdapter(dataAdapter);
////                        dataAdapter.notifyDataSetChanged();
////                    }
//                }
//                //initLineCharts();
//                getAxisLables();
//                getAxisPoints();
//                initLineChart();
//                leafLineChart.postInvalidate();
//            }
//        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog(getResources().getString(R.string.dlog));
        getW30sData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeLoadingDialog();
    }

//    //心率返回集合
//    List<W30SHeartDataS> heartDatas;
//    List<W30SHeartDataS> heartDatasList;

    //心率图标数据
    List<Integer> heartList;
    //心率返回集合
    List<W30SHeartDataS> heartDatas = new ArrayList<>();
    List<Integer> heartDatasMaxOrLad = new ArrayList<>();
    String oldDatas = "0";

    public void getW30sData() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getmW30SBLEManage().syncTime(new W30SBLEServices.CallDatasBackListenter() {
                @Override
                public void CallDatasBackListenter(final W30SSportData objects) {
                    try {
                        Log.d(TAG, "解析运动数据 日期 =  " + objects.getData());
                        Log.d(TAG, "解析运动数据 步数 =  " + objects.getSportStep());
                        Log.d(TAG, "解析运动数据 卡路里 =  " + objects.getCalory());
                        Log.d(TAG, "解析运动数据 距离 =  " + objects.getDistance());
                        Log.d(TAG, "解析运动数据 数据 =  " + objects.getSport_data().toString());
                    } catch (Exception e) {
                        e.getMessage();
                    }

                }

                @Override
                public void CallDatasBackListenter(W30SSleepData sleepDatas) {
                    Log.d(TAG, "解析睡眠数据 = 日期 = " + sleepDatas.getSleepData());
                    Log.d(TAG, "解析睡眠数据 = 数据 = " + sleepDatas.getSleepDataList().toString());
                }

                @Override
                public void CallDatasBackListenter(final W30SDeviceData objects) {
                    Log.d(TAG, "解析设备信息 = 设备电量 = " + objects.getDevicePower());
                    Log.d(TAG, "解析设备信息 = 设备类型 = " + objects.getDeviceType());
                    Log.d(TAG, "解析设备信息 = 设备版本 = " + objects.getDeviceVersionNumber());
                }

                @Override
                public void CallDatasBackListenter(W30SHeartData objects) {
                    if (objects == null) return;
                    Log.d(TAG, "解析运动心率 日期 =  " + objects.getDate());
                    Log.d(TAG, "解析运动心率 数据 =  " + objects.getWo_heart_data().toString());
                    try {
                        if (heartList != null) heartList.clear();
                        final List wo_heart_data = objects.getWo_heart_data();
                        if (wo_heart_data != null || wo_heart_data.size() > 0) {
                            if (heartDatas != null) {
                                heartDatas.clear();
                            }
                            if (heartDatasMaxOrLad != null) {
                                heartDatasMaxOrLad.clear();
                            }
                            //循环添加---求最高最低和平均值
                            for (int i = 0; i < wo_heart_data.size(); i++) {
                                if (!WatchUtils.isEmpty(wo_heart_data.get(i).toString().trim()) && Integer.valueOf(wo_heart_data.get(i).toString().trim()) > 0) {
                                    //Log.d("===================添加=", wo_heart_data.get(i).toString().trim());
                                    heartDatasMaxOrLad.add(Integer.valueOf(wo_heart_data.get(i).toString().trim()));
                                }
                            }
                            List<Integer> mouch = new ArrayList<>();
                            for (int i = 0; i < wo_heart_data.size(); i += 2) {
                                mouch.add((int) wo_heart_data.get(i));
                            }
                            if (!mouch.isEmpty()) {


                                //Log.d("--------AAA--", "" + mouch.size());
                                for (int i = 1; i <= 48; i++) {
                                    String datas = "";
                                    int heardAllNumber = 0;
                                    int ValeCont = 0;
                                    for (int j = (3 * i) - 3; j <= (3 * i) - 1; j++) {
                                        if ((int) mouch.get(j) > 0) {
                                            ValeCont++;
                                        }
                                        heardAllNumber += (int) mouch.get(j);
                                    }

                                    if (ValeCont == 0) {
                                        ValeCont = 1;
                                    }
                                    datas = String.valueOf((heardAllNumber / ValeCont)).split("[.]")[0];//取每半小时的平均心率
                                    if (Integer.valueOf(datas) < 50) {
                                        datas = "0";
                                    }
                                    double timesHour = (double) ((i - 1) * 0.5);
                                    int hours = 0;
                                    int mins = 0;
                                    String[] splitT = String.valueOf(timesHour).split("[.]");
                                    if (splitT.length >= 2) {
                                        hours = Integer.valueOf(splitT[0]);
                                        mins = Integer.valueOf(splitT[1]) * 60 / 10;
                                    } else {
                                        hours = Integer.valueOf(splitT[0]);
                                        mins = 0;
                                    }
                                    String timeHour = "";
                                    String timeMin = "";
                                    if (hours <= 9) {
                                        timeHour = "0" + hours;
                                    } else {
                                        timeHour = "" + hours;
                                    }
                                    if (mins <= 9) {
                                        timeMin = "0" + mins;
                                    } else {
                                        timeMin = "" + mins;
                                    }
                                    String upDataTime = timeHour + ":" + timeMin;
                                    String trim = objects.getDate().trim();
                                    final String tim = trim + " " + upDataTime;
                                    Log.d("--------AAA-时间-", tim + "=====" + upDataTime);
                                    Log.d("--------AAA-心率数据-", datas);
                                    if (heartList == null) heartList = new ArrayList<>();
                                    heartList.add(Integer.valueOf(datas));

                                    String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
                                    String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);


                                    long lon = W30BasicUtils.getStringToDate(tim, "yyyy-MM-dd HH:mm");
                                    W30SHeartDataS heartData = new W30SHeartDataS(String.valueOf(lon), Integer.valueOf(datas));
                                    heartDatas.add(heartData);
                                }


                            }

                        }


//
//                        Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 0);
//                        String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                        String das = objects.getDate();
//                        boolean isSharpe = true;
//                        if (das.equals(nextDay)) {
//                            isSharpe = true;
//                        } else {
//                            isSharpe = false;
//                        }
//
//                        String homeTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "upHeartTime", "2017-11-02 15:00:00");
//                        String heartHour = "";
//                        final List wo_heart_data = objects.getWo_heart_data();
//                        if (wo_heart_data == null || wo_heart_data.size() <= 0) return;
//                        if (heartDatas != null) {
//                            heartDatas.clear();
//                        }
//                        if (heartDatasMaxOrLad != null) {
//                            heartDatasMaxOrLad.clear();
//                        }
//                        //循环添加---求最高最低和平均值
//                        for (int i = 0; i < wo_heart_data.size(); i++) {
//                            if (!WatchUtils.isEmpty(wo_heart_data.get(i).toString().trim()) && Integer.valueOf(wo_heart_data.get(i).toString().trim()) > 0) {
//                                //Log.d("===================添加=", wo_heart_data.get(i).toString().trim());
//                                heartDatasMaxOrLad.add(Integer.valueOf(wo_heart_data.get(i).toString().trim()));
//                            }
//                        }
//
//
//                        String dataTime;
//                        String datasDDD = "";
//                        boolean isS = false;
//                        for (int i = 1; i <= 48; i++) {
//                            String datas = "";
//                            int xl = 0;
//                            int ValeCont = 0;
//                            for (int j = (6 * i) - 6; j <= (6 * i) - 1; j++) {
//                                if ((int) wo_heart_data.get(j) > 0) {
//                                    ValeCont++;
//                                }
//                                xl += (int) wo_heart_data.get(j);
//                            }
//                            if (ValeCont == 0) {
//                                ValeCont = 1;
//                            }
//                            datas = String.valueOf((xl / ValeCont)).split("[.]")[0];//取每半小时的平均心率
//
////                                Log.d("====assssss==", "=======" + i + "================" + xl + "===" + xl / ValeCont + "=======================" + datas);
//                            String[] split = objects.getDate().trim().split("[-]");
//                            String times = "";
//                            for (int j = 0; j < split.length; j++) {
//                                times += split[j] + "/";
//                            }
//                            double timesHour = (double) ((i - 1) * 0.5);
//                            int hours = 0;
//                            int mins = 0;
//                            String[] splitT = String.valueOf(timesHour).split("[.]");
//                            if (splitT.length >= 2) {
//                                hours = Integer.valueOf(splitT[0]);
//                                mins = Integer.valueOf(splitT[1]) * 60 / 10;
//                            } else {
//                                hours = Integer.valueOf(splitT[0]);
//                                mins = 0;
//                            }
//                            String timeHour = "";
//                            String timeMin = "";
//                            if (hours <= 9) {
//                                timeHour = "0" + hours;
//                            } else {
//                                timeHour = "" + hours;
//                            }
//                            if (mins <= 9) {
//                                timeMin = "0" + mins;
//                            } else {
//                                timeMin = "" + mins;
//                            }
//                            heartList.add(Integer.valueOf(datas));
//                            if (Integer.valueOf(datas) > 0) {
//                                String upDataTime = timeHour + ":" + timeMin;
//                                String tim = times.substring(0, times.length() - 1).trim() + " " + upDataTime;
//                                long lon = W30BasicUtils.getStringToDate(tim, "yyyy/MM/dd HH:mm");
//                                W30SHeartDataS heartData = new W30SHeartDataS();
//                                heartData.setTime(lon);
//                                heartData.setValue(Integer.valueOf(datas));
//                                heartDatas.add(heartData);
//                            }
//                        }
//
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (lineChart != null) {
                                    lineChart.setVisibility(View.VISIBLE);
                                    lineChart.setRateDataList(heartList);
                                }

                                if (heartDatas != null && !heartDatas.isEmpty()) {
                                    if (image_data_type != null)
                                        image_data_type.setVisibility(View.GONE);
                                    if (listView != null) {
                                        listView.setVisibility(View.VISIBLE);
                                        Log.d("========", heartDatas.toString());
                                        dataAdapter = new W30sHearteDataAdapter(W30SHearteDataActivity.this, heartDatas);
                                        listView.setAdapter(dataAdapter);
                                        dataAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (image_data_type != null)
                                        image_data_type.setVisibility(View.VISIBLE);
                                    if (listView != null)
                                        listView.setVisibility(View.GONE);
                                }
                                closeLoadingDialog();
                                lineChart.postInvalidate();

                            }
                        });

                    } catch (Error error) {
                    }
                }

                @Override
                public void CallDatasBackListenterIsok() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            getAxisLables();
////                            getAxisPoints();
////                            initLineChart();
//
//
////                            if (lineChart!=null)
//
//                            if (heartDatas != null && heartDatas.size() > 0) {
//                                dataAdapter = new W30sHearteDataAdapter(W30SHearteDataActivity.this, heartDatas);
//                                listView.setAdapter(dataAdapter);
//                                dataAdapter.notifyDataSetChanged();
//                            }
//                            closeLoadingDialog();
//                            lineChart.postInvalidate();
//                        }
//                    });
                }
            });
        }
    }

//
//    /*************   --------折线图----------    *************/
//    private void initLineChart() {
//        Line line = new Line(mPointValues).setColor(Color.WHITE).setCubic(false);  //折线的颜色
//        List<Line> lines = new ArrayList<Line>();
//        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
//        line.setCubic(false);//曲线是否平滑
//        line.setFilled(true);//是否填充曲线的面积
//        line.setPointRadius((int) 3);
//        line.setStrokeWidth((int) 2);
//        line.setHasLabels(false);//曲线的数据坐标是否加上备注
//        line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
//        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
//        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
//        lines.add(line);
//        LineChartData data = new LineChartData();
//        data.setLines(lines);
//
//
//        //坐标轴
//        Axis axisX = new Axis(); //X轴
//        //对x轴，数据和属性的设置
//        axisX.setTextSize(8);//设置字体的大小
//        axisX.setHasTiltedLabels(false);//x坐标轴字体是斜的显示还是直的，true表示斜的
//        axisX.setTextColor(Color.WHITE);  //设置字体颜色
//        axisX.setLineColor(Color.WHITE);
//        axisX.setValues(mAxisValues); //设置x轴各个坐标点名称
//        //axisX.setName("");  //表格名称
//        axisX.setTextSize(10);//设置字体大小
//        //axisX.setMaxLabelChars(24);  //最多几个X轴坐标
//        data.setAxisXBottom(axisX); //x 轴在底部
////      data.setAxisXTop(axisX);  //x 轴在顶部
//
//        Axis axisY = new Axis();  //Y轴
////        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
//        axisY.setHasLines(true);
//        axisY.setLineColor(Color.parseColor("#30FFFFFF"));
//        List<AxisValue> values = new ArrayList<>();
////        for(int i = 0; i < 100; i+= 50){
//        for (int i = 0; i < 16; i++) {
//            int i1 = i * 20;
//            AxisValue value = new AxisValue(i1);
//            String label = "" + i1;
//            value.setLabel(label);
//            values.add(value);
//        }
//        axisY.setValues(values);
//        axisY.setName("");//y轴标注
//        axisY.setTextColor(Color.WHITE);  //设置字体颜色
//        axisY.setTextSize(7);//设置字体大小
//        //axisY.setMaxLabelChars(6);//max label length, for example 60
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
////      data.setAxisYRight(axisY);  //y轴设置在右边
//
//
//        data.setValueLabelBackgroundColor(getResources().getColor(R.color.colorAccent));//此处设置坐标点旁边的文字背景
//        data.setValueLabelBackgroundEnabled(false);
//        data.setValueLabelsTextColor(Color.WHITE);//此处设置坐标点旁边的文字颜色
//
//        //设置行为属性，支持缩放、滑动以及平移
////        lineChart.setInteractive(true);
////        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
////        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
////        lineChart.setLineChartData(data);
////        lineChart.setVisibility(View.VISIBLE);
//
//        //设置行为属性，支持缩放、滑动以及平移
//        lineChart.setPaddingRelative(0, 30, 0, 0);
//        lineChart.setInteractive(false);
////        lineChart.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
////        lineChart.setMaxZoom((float) 3);//缩放比例
//        lineChart.setLineChartData(data);
//        lineChart.setVisibility(View.VISIBLE);
//        /**注：下面的7，10只是代表一个数字去类比而已
//         * 尼玛搞的老子好辛苦！！！见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
//         * 下面几句可以设置X轴数据的显示个数（x轴0-7个数据），当数据点个数小于（29）的时候，缩小到极致hellochart默认的是所有显示。当数据点个数大于（29）的时候，
//         * 若不设置axisX.setMaxLabelChars(int count)这句话,则会自动适配X轴所能显示的尽量合适的数据个数。
//         * 若设置axisX.setMaxLabelChars(int count)这句话,
//         * 33个数据点测试，若 axisX.setMaxLabelChars(10);里面的10大于v.right= 7; 里面的7，则
//         刚开始X轴显示7条数据，然后缩放的时候X轴的个数会保证大于7小于10
//         若小于v.right= 7;中的7,反正我感觉是这两句都好像失效了的样子 - -!
//         * 并且Y轴是根据数据的大小自动设置Y轴上限
//         * 若这儿不设置 v.right= 7; 这句话，则图表刚开始就会尽可能的显示所有数据，交互性太差
//         */
//        Viewport v = new Viewport(lineChart.getMaximumViewport());
//        v.left = 0;
//        v.right = 24;
//        lineChart.setCurrentViewport(v);
//        lineChart.postInvalidate();
//    }
//
//    /**
//     * X 轴的显示
//     */
//    private void getAxisLables() {
//        try {
//            if (mPointValues != null) mPointValues.clear();
//            if (mAxisValues != null) mAxisValues.clear();
//            for (int i = 0; i < StringDate.length; i++) {
//                Log.d("========", StringDate[i] + "");
//                mAxisValues.add(new AxisValue(i).setLabel(StringDate[i]));
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }
//
//    /**
//     * 图表的每个点的显示
//     */
//    private void getAxisPoints() {
//        try {
//            if (heartDatas != null && heartDatas.size() > 0) {
//                for (int i = 0; i < heartDatas.size(); i++) {
//                    long time = heartDatas.get(i).getTime();
//                    String dateToString = W30BasicUtils.getDateToString(time, "yyyy/MM/dd HH:mm:");
//                    //mAxisValues.add(new AxisValue(i).setLabel(dateToString.substring(11, 13)));
//                    //图表的每个点的显示
////                mAxisValues.add(new AxisValue(i).setLabel(dateToString.substring(11, 13)));
//                    int value = heartDatas.get(i).getValue();
//                    String currentDate1 = W30BasicUtils.getCurrentDate1();//yyyy-MM-dd HH:mm:ss
//                    String substring = dateToString.substring(11, 13);
//                    String substring1 = currentDate1.substring(11, 13);
//                    if (Integer.valueOf(substring) <= Integer.valueOf(substring1)) {
//                        if (value <= 0) {
//                            value = 60;
//                        }
//                    }
//                    mPointValues.add(new PointValue(i,value));
//                    Log.d(TAG, "=========X,Y数据" + dateToString.substring(11, 13) + "===" + heartDatas.get(i).getValue());
//                }
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }

    @OnClick({R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    class W30sHearteDataAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private Context mContext;
        private List<W30SHeartDataS> w30SHeartDataSList;

        public W30sHearteDataAdapter(Context mContext, List<W30SHeartDataS> w30SHeartDataSList) {
            this.mContext = mContext;
            if (w30SHeartDataSList != null || !w30SHeartDataSList.isEmpty()) {
                Collections.sort(w30SHeartDataSList, new Comparator<W30SHeartDataS>() {
                    @Override
                    public int compare(W30SHeartDataS o1, W30SHeartDataS o2) {
                        String time = o1.getTime();
                        String time1 = o2.getTime();
                        if (WatchUtils.isEmpty(time) || WatchUtils.isEmpty(time1)) {
                            return 0;
                        }
                        return time.compareTo(time1);
                    }
                });
            }
            this.w30SHeartDataSList = w30SHeartDataSList;
            layoutInflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return w30SHeartDataSList.size();
        }

        @Override
        public Object getItem(int position) {
            return w30SHeartDataSList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.w30s_heartedata_item, parent, false);
                holder = new ViewHolder();
                holder.hearteTime = (TextView) convertView.findViewById(R.id.hearte_time);
                holder.hearteValue = (TextView) convertView.findViewById(R.id.hearte_value);
                holder.text_data = (TextView) convertView.findViewById(R.id.text_data);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int size = w30SHeartDataSList.size();
            W30SHeartDataS w30SHeartDataS = w30SHeartDataSList.get(position);
            String time = w30SHeartDataS.getTime();
            String rtc = W30BasicUtils.getDateToString(Long.valueOf(time), "yyyy-MM-dd HH:mm");
            int heartRate = w30SHeartDataS.getValue();
            holder.text_data.setVisibility(View.GONE);
            holder.hearteTime.setText(rtc.substring(11, 16));
            if (heartRate > 0) {
                Log.d(TAG, "--->总共长度：" + size + "时间：" + rtc + "心率：" + heartRate);
//            holder.text_data.setText(rtc.substring(0, 10));
                holder.hearteValue.setText(String.valueOf(heartRate) + "bpm");
            } else {
                holder.hearteValue.setText("-- bpm");
            }
            return convertView;
        }

        class ViewHolder {
            TextView hearteTime;
            TextView hearteValue, text_data;
        }
    }

}
