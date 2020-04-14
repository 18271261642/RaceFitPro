package com.example.bozhilun.android.b15p.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PDBCommont;
import com.example.bozhilun.android.b15p.b15pdb.B15PStepDB;
import com.example.bozhilun.android.b30.adapter.B30StepDetailAdapter;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.ctrls.DisEnergy;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.TimeData;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/4.
 */

public class B15PStepDetailActivity extends WatchBaseActivity {

    /**
     * 跳转到B30StepDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B15PStepDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.b30SportChartLin1)
    LinearLayout b30SportChartLin1;
    @BindView(R.id.b30StepDetailRecyclerView)
    RecyclerView b30StepDetailRecyclerView;
    @BindView(R.id.countStepTv)
    TextView countStepTv;
    @BindView(R.id.countDisTv)
    TextView countDisTv;
    @BindView(R.id.countKcalTv)
    TextView countKcalTv;
    @BindView(R.id.stepCurrDateTv)
    TextView stepCurrDateTv;


//    @BindView(R.id.detail_step)
//    LinearLayout detailStep;
//    @BindView(R.id.detail_dis)
//    LinearLayout detailDis;
//    @BindView(R.id.detail_kcl)
//    LinearLayout detailKcl;
//
//    @BindView(R.id.detail_step_img)
//    ImageView detail_stepIMG;
//    @BindView(R.id.detail_dis_img)
//    ImageView detail_disIMG;
//    @BindView(R.id.detail_kcl_img)
//    ImageView detail_kclIMG;
    private int st = 0;


    /**
     * 列表数据源
     */
    //private List<HalfHourSportData> dataList = new ArrayList<>();
    private List<CusVPHalfSportData> dataList = new ArrayList<>();
    /**
     * 列表适配器
     */
    private B30StepDetailAdapter b30StepDetailAdapter;
    /**
     * 步数数据
     */
    List<BarEntry> b30ChartList = new ArrayList<>();
    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    /**
     * Json工具类
     */
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_step_detail_layout);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.move_ment);
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin1.setBackgroundColor(Color.parseColor("#2594EE"));//6DCB99
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, true);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        b30StepDetailAdapter = new B30StepDetailAdapter(this, dataList);
        b30StepDetailRecyclerView.setAdapter(b30StepDetailAdapter);
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }


    String[] timeString = {"00","01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", };

    //private List<HalfHourSportData> halfHourSportData = null;

    private List<CusVPHalfSportData> halfHourSportData = null;

    private int step = 0;
    private String disValue = "0.0";
    private String calValue = "0.0";

    /**
     * 初始化数据
     */
    private void initData() {
        halfHourSportData = new ArrayList<>();
        stepCurrDateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        if (WatchUtils.isEmpty(mac))
            return;
        List<B15PStepDB> stepAllDatas = B15PDBCommont.getInstance().findStepAllDatas(mac, currDay);

        List<Integer> sportData = new ArrayList<>();
//        String dayTimes = WatchUtils.obtainFormatDate(currDay);
        if (stepAllDatas != null && !stepAllDatas.isEmpty()) {
            Log.e("===", "===步数==" + stepAllDatas.toString());
            int hourStep = 0;
            for (int i = 0; i < timeString.length; i++) {
                hourStep = 0;
                step = 0;
                for (int j = 0; j < stepAllDatas.size(); j++) {
                    B15PStepDB b15PStepDB = stepAllDatas.get(j);
                    if (b15PStepDB != null) {
                        String stepData = b15PStepDB.getStepData();
                        String stepTime = b15PStepDB.getStepTime();
                        //根据日期判断去当天的值
                        if (stepData.substring(0, 10).equals(currDay)) {
                            step = step + b15PStepDB.getStepItemNumber();
                            //Log.e("====", "===步数计算时间对比==" + stepData.substring(0, 10) + "  " + currDay + "  " + b15PStepDB.getStepItemNumber());
                            if (timeString[i].equals(stepTime.substring(0, 2))) {
                                hourStep += b15PStepDB.getStepItemNumber();
                            }
                        }
                    }
                }
                sportData.add(hourStep);
            }
        }

        Log.e("=====步数=", sportData.toString());
        halfHourSportData.clear();
        step = 0;
        for (int i = 0; i < sportData.size(); i++) {
//            B15PStepDB b15PStepDB = stepAllDatas.get(i);
            step += sportData.get(i);
            //TimeData timeData = new TimeData();
            CusVPTimeData timeData = new CusVPTimeData();

            timeData.setHour(Integer.valueOf(timeString[i]));
//            HalfHourSportData halfHourSportData1 = new HalfHourSportData(timeData,
//                    sportData.get(i), 0, 0, 0);

            CusVPHalfSportData halfHourSportData1 = new CusVPHalfSportData(timeData,sportData.get(i),0,0,0);

            halfHourSportData.add(halfHourSportData1);


        }


//        String stringStep =  B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao.TYPE_SPORT);
//        Log.e("===获取到的步数 ", (WatchUtils.isEmpty(stringStep) ? "苏数据" : stringStep));

//        halfHourSportData.clear();
//        step = 0;
//        if (!WatchUtils.isEmpty(stringStep)) {
//            List<Integer> sportData = JSON.parseObject(stringStep, new TypeReference<List<Integer>>() {
//            });// Json 转List
//            if (sportData != null && !sportData.isEmpty()) {
//                for (int i = 0; i < sportData.size(); i++) {
//                    step += sportData.get(i);
//                    TimeData timeData = new TimeData();
//                    timeData.setHour(Integer.valueOf(timeString[i]));
//                    HalfHourSportData halfHourSportData1 = new HalfHourSportData(timeData,
//                            sportData.get(i), 0, 0, 0);
//                    halfHourSportData.add(halfHourSportData1);
//                }
////        String sport = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
////                .TYPE_SPORT);
////        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
////        }.getType());
////        if (sportData == null) sportData = new ArrayList<>();
////        showBarChart(sportData);
////        String step = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
////                .TYPE_STEP);
////        countStepTv.setText(step == null ? "0" : step);// 本地步数
//
//            }
//
//        }


        showBarChart(halfHourSportData);

        showListData(halfHourSportData);
    }

    /**
     * 显示图表
     */
    private void showBarChart(List<CusVPHalfSportData> sportData) {
        List<Map<String, Integer>> listMap = new ArrayList<>();
        if (sportData != null && !sportData.isEmpty()) {
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);
                //TimeData tmpDate = sportData.get(k).getTime();

                CusVPTimeData tmpDate = sportData.get(k).getTime();

                int tmpIntDate = tmpDate.getHMValue();
                if (tmpIntDate == time) {
                    map.put("val", sportData.get(k).getStepValue());
                    if (k < sportData.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
            }
        }
        b30ChartList.clear();
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, Integer> tmpMap = listMap.get(i);
            b30ChartList.add(new BarEntry(i, tmpMap.get("val")));
        }
        initBarChart(b30ChartList);
        b30BarChart.invalidate();
    }

    /**
     * 步数图表展示
     */
    private void initBarChart(List<BarEntry> pointBar) {
        BarDataSet barDataSet = new BarDataSet(pointBar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColor(Color.WHITE);//设置第一组数据颜色

        Legend mLegend = b30BarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threeBarData = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threeBarData.add(barDataSet);

        BarData bardata = new BarData(threeBarData);
        bardata.setBarWidth(0.5f);  //设置柱子宽度

        b30BarChart.setData(bardata);
        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        b30BarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
        b30BarChart.getXAxis().setEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);
        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画
    }

    /**
     * 显示列表数据
     */
    //    private void showListData(List<HalfHourSportData> sportData) {
    @SuppressLint("SetTextI18n")
    private void showListData(List<CusVPHalfSportData> sportData) {
//        dataList.clear();
//        if (sportData == null && sportData.isEmpty()) return;
//        for (int i = 0; i < sportData.size(); i++) {
//            if (sportData.get(i).stepValue > 0) {
//                dataList.add(sportData.get(i));
//            }
//        }
//        b30StepDetailAdapter.notifyDataSetChanged();
//        double calValue = 0;
//        double disValue = 0;
//        if (dataList != null && !dataList.isEmpty()) {
//            for (HalfHourSportData item : dataList) {
//                calValue += item.getCalValue();
//                disValue += item.getDisValue();
//            }
//        }
//        calValue = (double) Math.round(calValue * 100) / 100;
        if (dataList != null) dataList.clear();
        else dataList = new ArrayList<>();
        if (sportData != null) dataList.addAll(sportData);
        if (!dataList.isEmpty()) {
            for (int i = dataList.size() - 1; i >= 0; i--) {
                if (dataList.get(i).getStepValue() <= 0) {
                    dataList.remove(i);
                }
            }
        }
        b30StepDetailAdapter.notifyDataSetChanged();


        if (WatchUtils.getCurrentDate().equals(currDay)) {
            String uHeight = (String) SharedPreferencesUtils.getParam(B15PStepDetailActivity.this, "userheight", "170");
            String uWeight = (String) SharedPreferencesUtils.getParam(B15PStepDetailActivity.this, "userweight", "60");
            double calorieWithSteps = 0;
            double distanceWithStep = 0;
            st = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "ALL_STEP_VALUE", 0);
            if (st != 0) {
                calorieWithSteps = (double) DisEnergy.getCalorieWithSteps(st, Integer.valueOf(uHeight), Integer.valueOf(uWeight));
                distanceWithStep = (double) DisEnergy.getDistanceWithStep(st, Integer.valueOf(uHeight));
            } else {
                //获取卡路里 ka
                //step：   步数
                //height：身高 cm
                //weight：体重

                if(uWeight.contains("k")){
                    uWeight = StringUtils.substringBefore(uWeight,"k");
                }

                calorieWithSteps = (double) DisEnergy.getCalorieWithSteps(step, Integer.valueOf(uHeight), Integer.valueOf(uWeight));
                distanceWithStep = (double) DisEnergy.getDistanceWithStep(step, Integer.valueOf(uHeight));
            }

//        BigDecimal bdK = new BigDecimal((double) (distanceWithStep / 1000.00));//转成 KM
            BigDecimal bdK = new BigDecimal((double) (distanceWithStep / 1.00));//M
            BigDecimal setScaleK = bdK.setScale(0, RoundingMode.DOWN);

            BigDecimal bdD = new BigDecimal((double) (calorieWithSteps / 1000.00));
            BigDecimal setScaleD = bdD.setScale(0, RoundingMode.DOWN);
//
            disValue = setScaleK.toString();//距离
            calValue = setScaleD.toString();//卡路里

            // Log.e("======", "距离  " + distanceWithStep + "m  --换算后-- " + disValue);
            // Log.e("======", "卡路里  " + calorieWithSteps + "m  --换算后-- " + calValue);

            boolean isMetric = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
            if (!isMetric) {
                BigDecimal bdDS = new BigDecimal((double) (Double.valueOf(disValue) * Constant.METRIC_M__MILE));
                BigDecimal setScaleDS = bdDS.setScale(0, RoundingMode.DOWN);
                disValue = setScaleDS.toString();
            }
//        disValue = Math.round(disValue * 100) / 100;
            Log.d("bobo", "calValue: " + calValue + ",disValue: " + disValue);
            String disStr = isMetric ? "m" : "mi";

            if (st != 0) {
                countStepTv.setText(st + "");// 本地步数
            } else {
                countStepTv.setText(step + "");// 本地步数
            }
            countDisTv.setText(disValue + disStr);
            countKcalTv.setText("" + calValue + "kcl");

        } else {
            int stepValue = 0;
            if (sportData != null) {
                for (CusVPHalfSportData item : sportData) {
                    stepValue += item.getStepValue();
                }
            }
            BigDecimal bdS = new BigDecimal((double) (stepValue / 1.00));
            countStepTv.setText(bdS.setScale(0, RoundingMode.DOWN).toString() + "");// 本地步数


            /**
             * 通过本地步数计算距离和卡路里
             */

            //高
            String uHeight = (String) SharedPreferencesUtils.getParam(B15PStepDetailActivity.this, "userheight", "170");
            //体重
            String uWeight = (String) SharedPreferencesUtils.getParam(B15PStepDetailActivity.this, "userweight", "60");
            if(uWeight.contains("k")){
                uWeight = StringUtils.substringBefore(uWeight,"k");
            }
            double calorieWithSteps = (double) DisEnergy.getCalorieWithSteps(stepValue, Integer.valueOf(uHeight), Integer.valueOf(uWeight));
            BigDecimal bdD = new BigDecimal((double) (calorieWithSteps / 1000.00));
            BigDecimal setScaleD = bdD.setScale(0, RoundingMode.DOWN);
            //卡路里
            countKcalTv.setText("" + setScaleD.setScale(0, RoundingMode.DOWN).toString() + "kcl");

            double distanceWithStep = (double) DisEnergy.getDistanceWithStep(stepValue, Integer.valueOf(uHeight));
            BigDecimal bdK = new BigDecimal((double) (distanceWithStep / 1.00));//M
            BigDecimal setScaleK = bdK.setScale(0, RoundingMode.DOWN);
            boolean isMetric = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);
            //距离
            if (!isMetric) {
                countDisTv.setText((setScaleK.doubleValue() * Constant.METRIC_M__MILE) + (isMetric ? "m" : "mi"));
            } else {
                countDisTv.setText(setScaleK.toString() + (isMetric ? "m" : "mi"));
            }
        }


    }
    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.stepCurrDateLeft,
            R.id.stepCurrDateRight
//            ,R.id.detail_step, R.id.detail_dis, R.id.detail_kcl
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B15PStepDetailActivity.this);
                break;
            case R.id.stepCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.stepCurrDateRight:   //切换下一天数据
                changeDayData(false);
                break;
            //下面是  步数详细中 步数、距离、卡路里切换
//            case R.id.detail_step:
//                Commont.TYPE_DATAS = 0;
//                if (b30StepDetailAdapter != null) b30StepDetailAdapter.notifyDataSetChanged();
//                SelectType(0);
//                break;
//            case R.id.detail_dis:
//                Commont.TYPE_DATAS = 1;
//                if (b30StepDetailAdapter != null) b30StepDetailAdapter.notifyDataSetChanged();
//                SelectType(1);
//                break;
//            case R.id.detail_kcl:
//                Commont.TYPE_DATAS = 2;
//                if (b30StepDetailAdapter != null) b30StepDetailAdapter.notifyDataSetChanged();
//                SelectType(2);
//                break;
        }
    }

//    void SelectType(int type){
//        switch (type){
//            case 0:
//                detailStep.setBackgroundColor(Color.parseColor("#207F6F"));
//                detailDis.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detailKcl.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detail_stepIMG.setVisibility(View.VISIBLE);
//                detail_disIMG.setVisibility(View.GONE);
//                detail_kclIMG.setVisibility(View.GONE);
//                break;
//            case 1:
//                detailStep.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detailDis.setBackgroundColor(Color.parseColor("#207F6F"));
//                detailKcl.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detail_stepIMG.setVisibility(View.GONE);
//                detail_disIMG.setVisibility(View.VISIBLE);
//                detail_kclIMG.setVisibility(View.GONE);
//                break;
//            case 2:
//                detailStep.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detailDis.setBackgroundColor(Color.parseColor("#6DCB99"));
//                detailKcl.setBackgroundColor(Color.parseColor("#207F6F"));
//                detail_stepIMG.setVisibility(View.GONE);
//                detail_disIMG.setVisibility(View.GONE);
//                detail_kclIMG.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        initData();
    }

}
