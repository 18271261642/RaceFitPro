package com.example.bozhilun.android.h9.settingactivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.bean.HeartDataBean;
import com.example.bozhilun.android.h9.utils.H9HearteDataAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

public class H9HearteDataActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "H9HearteDataActivity";
    @BindView(R.id.heartedata_list)
    ListView heartedataList;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.image_data_type)
    ImageView imageDataType;
    private RequestPressent requestPressent;
    List<HeartDataBean.HeartRateBean> heartNewDataList = new ArrayList<>();
    List<String> stringDataList = new ArrayList<>();
    H9HearteDataAdapter dataAdapter = null;
    String is18i;
    @BindView(R.id.bar_mores)
    TextView barMores;
    private String systemDatasss;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_hearte_data_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.heart_rate));
        is18i = getIntent().getStringExtra("is18i");
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }


    /**
     * 获取心率数据
     */
    private void getHeartData(String time) {
        if (heartNewDataList != null || stringDataList != null) {
            heartNewDataList.clear();
            stringDataList.clear();
            if (dataAdapter != null) {
                dataAdapter.notifyDataSetChanged();
            }
        }
//        initLineCharts();
//        getAxisLables();
//        getAxisPoints();
//        initLineChart();
//        leafLineChart.postInvalidate();
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        JSONObject jsonObect = new JSONObject();
        map.put("userId", (String) SharedPreferencesUtils.readObject(this, "userId"));
        map.put("deviceCode", (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC));
        map.put("date", time);
        String mapjson = gson.toJson(map);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(502, URLs.HTTPs + URLs.getHeartD, H9HearteDataActivity.this, mapjson, 0);
        }
    }


//    /**
//     * 解析心率数据
//     *
//     * @param heartRates
//     */
//    List<HeartDataBean.HeartRateBean> heartRateList;
//
//    private List<HeartDataBean.HeartRateBean> getHeartDataList(String heartRates) {
//        if (heartRates == null) {
//            return null;
//        }
//        HeartDataBean heartDataBean = new Gson().fromJson(heartRates, HeartDataBean.class);
//        heartRateList = heartDataBean.getHeartRate();
//        Collections.sort(heartRateList, new Comparator<HeartDataBean.HeartRateBean>() {
//            @Override
//            public int compare(HeartDataBean.HeartRateBean watchDataDatyBean, HeartDataBean.HeartRateBean t1) {
//                return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
//            }
//        });
//        return heartRateList;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        systemDatasss = B18iUtils.getSystemDatasss();
        Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
        String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
        getHeartData(nextDay.substring(0, 10));
//        getHeartData(B18iUtils.getSystemDatasss());
    }

//    /*************   --------折线图----------    *************/
//
////    private String[] StringDate = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10",
////            "10.5", "11", "11.5", "12", "12.5", "13", "13.5", "14", "14.5", "15", "15.5", "16", "16.5",
////            "17", "17.5", "18", "18.5", "19", "19.5", "20", "20.5", "21", "21.5", "22", "22.5", "23"};//X轴的标注
//    private List<lecho.lib.hellocharts.model.PointValue> mPointValues = new ArrayList<>();
//    private List<AxisValue> mAxisValues = new ArrayList<>();
//
//
//    private void initLineChart() {
//        lecho.lib.hellocharts.model.Line line = new lecho.lib.hellocharts.model.Line(mPointValues).setColor(Color.WHITE).setCubic(false);  //折线的颜色
//        List<lecho.lib.hellocharts.model.Line> lines = new ArrayList<lecho.lib.hellocharts.model.Line>();
//        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
//        line.setCubic(false);//曲线是否平滑
//        line.setFilled(true);//是否填充曲线的面积
//        line.setPointRadius((int) 2);
//        line.setStrokeWidth((int) 1);
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
//        lecho.lib.hellocharts.model.Axis axisX = new lecho.lib.hellocharts.model.Axis(); //X轴
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
//        lecho.lib.hellocharts.model.Axis axisY = new lecho.lib.hellocharts.model.Axis();  //Y轴
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
//        //data.setValueLabelBackgroundEnabled(false);
//        data.setValueLabelsTextColor(Color.WHITE);//此处设置坐标点旁边的文字颜色
//
//        //设置行为属性，支持缩放、滑动以及平移
//        lineChart.setPaddingRelative(0, 30, 0, 0);
//        lineChart.setInteractive(true);
//        lineChart.setZoomType(ZoomType.HORIZONTAL);
//        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
//        lineChart.setLineChartData(data);
//        lineChart.setVisibility(View.VISIBLE);
//
//
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
////        Viewport v = new Viewport(lineChart.getMaximumViewport());
////        v.left = 0;
////        v.right = 16;
////        lineChart.setCurrentViewport(v);
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
////            for (int i = 0; i < 48; i++) {
//////                Log.d("========", StringDate[i] + "");
////                mAxisValues.add(new AxisValue(i).setLabel(StringDate[i]));
////            }
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
////            if (heartNewDataList != null && heartNewDataList.size() > 0) {
////                for (int i = 0; i < heartNewDataList.size(); i++) {
////                    String time = heartNewDataList.get(i).getRtc();//2017-11-01 00:00
//////                    String dateToString = W30BasicUtils.getDateToString(time, "yyyy/MM/dd HH:mm:ss");
////                    mAxisValues.add(new AxisValue(i).setLabel(time.substring(11, 13)));
////                    //图表的每个点的显示
////                    mPointValues.add(new lecho.lib.hellocharts.model.PointValue(i, heartNewDataList.get(i).getHeartRate()));
//////                    Log.d(TAG, "=========X,Y数据" + dateToString.substring(11, 13) + "===" + heartDatas.get(i).getValue());
////                }
////            }
//
//            if (heartNewDataList != null && heartNewDataList.size() > 0) {
//                for (int i = 0; i <= heartNewDataList.size(); i++) {
//                    if (i == heartNewDataList.size()) {
//                        mAxisValues.add(new AxisValue(i).setLabel(""));
//                        //图表的每个点的显示
//                        mPointValues.add(new lecho.lib.hellocharts.model.PointValue(i, 0));
//                    } else {
//                        String rtc = heartNewDataList.get(i).getRtc();//2017-04-02 12:30
//                        mAxisValues.add(new AxisValue(i).setLabel(rtc.substring(11, 16)));
//                        //图表的每个点的显示
//                        mPointValues.add(new lecho.lib.hellocharts.model.PointValue(i, heartNewDataList.get(i).getHeartRate()));
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//
//    }
///***********************/
//    private void initLineCharts() {
//        Axis axisX = new Axis(getAxisValuesX());
//        axisX.setShowText(true);
//        axisX.setAxisLineColor(Color.parseColor("#43FFFFFF"));
//        axisX.setAxisLineWidth(0.5f);
//        axisX.setTextColor(Color.WHITE);
//        axisX.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(true).setShowText(true);
//        Axis axisY = new Axis(getAxisValuesY());
//        axisY.setShowText(false);
//        axisY.setTextColor(Color.WHITE);
//        axisY.setAxisLineWidth(0f);
//        axisY.setShowLines(false);
//        axisY.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(false).setShowText(true);
//        leafLineChart.setAxisX(axisX);
//        leafLineChart.setAxisY(axisY);
//        List<Line> lines = new ArrayList<>();
//        lines.add(getFoldLineTest());
//        leafLineChart.setChartData(lines);
//        leafLineChart.showWithAnimation(1000);
//        leafLineChart.show();
//        leafLineChart.invalidate();
//    }
//
//    /**
//     * X轴值
//     *
//     * @return
//     */
//    private List<B18iAxisValue> getAxisValuesX() {
//        List<B18iAxisValue> b18iAxisValues = new ArrayList<>();
//
//        for (int i = 1; i < 24; i++) {
//            B18iAxisValue value = new B18iAxisValue();
//            if (i % 3 != 0) {
//                value.setLabel("");
//            } else {
//                value.setLabel(i + "");
//            }
////            if (i % 3 != 0) {
////                value.setLabel("");
////            } else {
////                value.setLabel(i + "");
////            }
//            b18iAxisValues.add(value);
//        }
////        for (int i = 0; i <= 8; i++) {
////            B18iAxisValue value = new B18iAxisValue();
////            value.setLabel(i * 3 + "");
////            b18iAxisValues.add(value);
////        }
////            value.setLabel(i * 3 + "");
////        for (int i = 0; i < heartRateDatas.size(); i++) {
////            long timestamp = heartRateDatas.get(i).timestamp;
////            Date date = new Date(timestamp);
////            SimpleDateFormat sd = new SimpleDateFormat("HH");
////            String format = sd.format(date);
////        }
//        return b18iAxisValues;
//    }
//
//    /**
//     * Y轴值
//     *
//     * @return
//     */
//    private List<B18iAxisValue> getAxisValuesY() {
//        List<B18iAxisValue> b18iAxisValues = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            B18iAxisValue value = new B18iAxisValue();
//            value.setLabel("  ");
////            if (i != 0) {
////                value.setLabel(String.valueOf((i) * 50));
////            } else {
////                value.setLabel(" ");
////            }
//            b18iAxisValues.add(value);
//        }
//        return b18iAxisValues;
//    }
//
//
//    /**
//     * 设置值
//     *
//     * @return
//     */
//    private Line getFoldLineTest() {
//
//        List<PointValue> pointValues = new ArrayList<>();
//        List<String> timeString = new ArrayList<>();
//        List<Integer> heartString = new ArrayList<>();
//
//        if (heartDataList != null) {
//            for (int i = 0; i < heartDataList.size(); i++) {
//                if (heartDataList.get(i) != null) {
//                    int avg = heartDataList.get(i).getHeartRate();
//                    String sysTim = B18iUtils.interceptString(heartDataList.get(i).getRtc(), 11, 13);
//                    if (!timeString.contains(sysTim)) {
//                        timeString.add(sysTim);
//                        heartString.add(avg);
//                    }
//                    //                Collections.sort(timeString);
//                } else {
//                    if (heartString != null) {
//                        heartString.clear();
//                    }
//                    if (timeString != null) {
//                        timeString.clear();
//                    }
//                    for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(B18iUtils.getSystemTimer(), 11, 13)); j++) {
//                        heartString.add(0);
//                        timeString.add(j + "");
//                    }
//                }
//            }
//        } else {
//            for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(B18iUtils.getSystemTimer(), 11, 13)); j++) {
//                heartString.add(0);
//                timeString.add(j + "");
//            }
//        }
//
//        for (int i = 0; i < timeString.size(); i++) {
//            PointValue value = new PointValue();
//            value.setX(Integer.valueOf(timeString.get(i)) / 23f);
//            value.setY(Integer.valueOf(heartString.get(i)) / 150f);
//            pointValues.add(value);
//        }
//
//        Line line = new Line(pointValues);
//        line.setLineColor(Color.parseColor("#FFFFFF"))
//                .setLineWidth(2f)
//                .setHasPoints(true)//是否显示点
//                .setPointColor(Color.WHITE)
//                .setCubic(false)
//                .setPointRadius(2)
//                .setFill(true)
//                .setFillColor(Color.parseColor("#FFFFFF"))
//                .setHasLabels(true)
//                .setLabelColor(Color.parseColor("#FF00FF"));//0C33B5E5
//        return line;
//    }

    private void setSlecteDateTime() {
        View view = LayoutInflater.from(H9HearteDataActivity.this).inflate(R.layout.h9_pop_date_item, null, false);
        PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setContentView(view);
        //设置pop数据
        setPopContent(popupWindow, view);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹出框消失.  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));//new BitmapDrawable()
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置可以点击
        popupWindow.setTouchable(true);
        //从顶部显示
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.TOP, 0, 0);
    }

    private void setPopContent(final PopupWindow popupWindow, View view) {
        view.findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView viewById = (TextView) view.findViewById(R.id.bar_titles);
        viewById.setText(getResources().getString(R.string.history_times));
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.h9_calender);
        calendarView.setEnabled(false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("----选择的日期是-----", year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                String m = "1";
                String d = "1";
                if ((month + 1) <= 9) {
                    m = "0" + (month + 1);
                } else {
                    m = "" + (month + 1);
                }
                if (dayOfMonth <= 9) {
                    d = "0" + dayOfMonth;
                } else {
                    d = "" + dayOfMonth;
                }
                String times = year + "-" + m + "-" + d;
                if (systemDatasss.trim().equals(times.trim())) {
                    getHeartData(nextDay);
                } else {
                    getHeartData(times);
                }
                popupWindow.dismiss();
            }
        });
    }

    @OnClick({R.id.image_back, R.id.bar_mores})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                setSlecteDateTime();
                break;
        }
    }


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog("Loging...");
    }


    /**
     * 去除空数据
     *
     * @param list
     * @return
     */
    public List<HeartDataBean.HeartRateBean> m2(List<HeartDataBean.HeartRateBean> list) {
        Iterator<HeartDataBean.HeartRateBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            HeartDataBean.HeartRateBean integer = iterator.next();

            int heartRate = integer.getHeartRate();
            if (heartRate <= 0) {
                //list.remove(temp);// 出现java.util.ConcurrentModificationException
                iterator.remove();// 推荐使用
            }
        }
        return list;
    }

    @Override
    public void successData(int what, Object result, int daystag) {

        //关闭加载
        closeLoadingDialog();
        if (heartedataList != null)
            heartedataList.setVisibility(View.GONE);
        if (imageDataType != null)
            imageDataType.setVisibility(View.VISIBLE);
        //判断返回是否为空
        if (result != null || !WatchUtils.isEmpty(result.toString())) {
            HeartDataBean heartDataBean = new Gson().fromJson(result.toString(), HeartDataBean.class);
            if (heartDataBean != null) {
                String resultCode = heartDataBean.getResultCode();
                if (!WatchUtils.isEmpty(resultCode) && resultCode.equals("001")) {
                    List<HeartDataBean.HeartRateBean> heartRate = heartDataBean.getHeartRate();
                    if (m2(heartRate) != null && !m2(heartRate).isEmpty()) {
                        //升序排列
                        Collections.sort(m2(heartRate), new Comparator<HeartDataBean.HeartRateBean>() {
                            @Override
                            public int compare(HeartDataBean.HeartRateBean o1, HeartDataBean.HeartRateBean o2) {
                                return o1.getRtc().compareTo(o2.getRtc());
                            }
                        });
                        if (heartedataList != null)
                            heartedataList.setVisibility(View.VISIBLE);
                        if (imageDataType != null)
                            imageDataType.setVisibility(View.GONE);
                        dataAdapter = new H9HearteDataAdapter(H9HearteDataActivity.this, m2(heartRate));
                        heartedataList.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(H9HearteDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(H9HearteDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(H9HearteDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(H9HearteDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
        }




//
//
//        closeLoadingDialog();
//        if (result == null) {
//            if (imageDataType!=null)imageDataType.setVisibility(View.VISIBLE);
//            if (heartedataList!=null)heartedataList.setVisibility(View.GONE);
//            return;
//        }
//        if (heartNewDataList == null) heartNewDataList = new ArrayList<>();
//        if (stringDataList == null) stringDataList = new ArrayList<>();
//        if (what == 502) {
//            HeartDataBean heartDataBean = new Gson().fromJson(result.toString(), HeartDataBean.class);
//            String resultCode = heartDataBean.getResultCode();
//            if (resultCode.equals("001")) {
//                if (heartNewDataList != null) heartNewDataList.clear();
//                if (stringDataList != null) stringDataList.clear();
//                List<HeartDataBean.HeartRateBean> heartDataList = heartDataBean.getHeartRate();
//                if (heartDataList ==null||heartDataList.isEmpty()){
//                    if (imageDataType!=null)imageDataType.setVisibility(View.VISIBLE);
//                    if (heartedataList!=null)heartedataList.setVisibility(View.GONE);
//                    return;
//                }
//                for (int i = 0; i < heartDataList.size(); i++) {
//                    //2018-04-03 02:00
//                    String substring = heartDataList.get(i).getRtc().substring(11, 16);
//                    if (!stringDataList.contains(substring)) {
//                        HeartDataBean.HeartRateBean heartRateBean = heartDataList.get(i);
//                        heartNewDataList.add(heartRateBean);
//                        stringDataList.add(substring);
//                    }
//                }
//
//                //升序排列
//                Collections.sort(heartNewDataList, new Comparator<HeartDataBean.HeartRateBean>() {
//                    @Override
//                    public int compare(HeartDataBean.HeartRateBean o1, HeartDataBean.HeartRateBean o2) {
//                        return o1.getRtc().compareTo(o2.getRtc());
//                    }
//                });
//                if (heartNewDataList.size() > 0) {
//                    if (imageDataType!=null)imageDataType.setVisibility(View.GONE);
//                    if (heartedataList!=null)heartedataList.setVisibility(View.VISIBLE);
//                    dataAdapter = new H9HearteDataAdapter(H9HearteDataActivity.this, heartNewDataList);
//                    heartedataList.setAdapter(dataAdapter);
//                    dataAdapter.notifyDataSetChanged();
//                } else {
//                    imageDataType.setVisibility(View.VISIBLE);
//                }
//
//            }
//
//        }else {
//            if (imageDataType!=null)imageDataType.setVisibility(View.VISIBLE);
//            if (heartedataList!=null)heartedataList.setVisibility(View.GONE);
//        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
        e.getMessage();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }
}
