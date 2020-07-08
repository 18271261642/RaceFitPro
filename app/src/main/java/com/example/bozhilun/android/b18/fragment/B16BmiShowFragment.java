package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.ComputeBMIActivity;
import com.example.bozhilun.android.b18.modle.B16CadenceBean;
import com.example.bozhilun.android.b18.modle.B16CadenceResultBean;
import com.example.bozhilun.android.b18.modle.B16DbManager;
import com.example.bozhilun.android.b18.modle.CusLinView;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.data.BarXFormartValue;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2019/12/31
 */
public class B16BmiShowFragment extends LazyFragment {

    private static final String TAG = "B16BmiShowFragment";

    View view;
    @BindView(R.id.b16BmiLinChart)
    LineChart b16BmiLinChart;
    Unbinder unbinder;
    @BindView(R.id.b16CusLinView)
    CusLinView b16CusLinView;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.b16BmiDateTv)
    TextView b16BmiDateTv;

    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    @BindView(R.id.b16BmiViewPager)
    ViewPager b16BmiViewPager;
    //燃脂时间
    @BindView(R.id.b16BmiSportTimeTv)
    TextView b16BmiSportTimeTv;
    //脂肪消耗
    @BindView(R.id.b16BmiSportKgTv)
    TextView b16BmiSportKgTv;

    @BindView(R.id.b16linPointLin)
    LinearLayout b16linPointLin;


    private String bleMac = null;

    private Gson gson = new Gson();

    //折线图的数据
    private ArrayList<Entry> entryArrayList;
    LineDataSet lineDataSet;

    //步频最大值
    private float maxStep = 0;

    private String currDay = WatchUtils.getCurrentDate();

    private List<B16CadenceResultBean> pagerList;
    private B16CurrSportPagerAdapter pagerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleMac = MyApp.getInstance().getMacAddress();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b16_show_bmi_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();


        findHistoryData(currDay);


        return view;
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setImageResource(R.mipmap.icon_bmi_compute);
        commentB30TitleTv.setText(getResources().getString(R.string.string_bmi_title));
        entryArrayList = new ArrayList<>();
        b16CusLinView.setTxt(true);
        pagerList = new ArrayList<>();
    }


    //查询数据库中的数据
    private void findHistoryData(String dayStr) {
        entryArrayList.clear();
        pagerList.clear();
        b16BmiDateTv.setText(dayStr);
        try {
            List<B16CadenceResultBean> resultBeanList = B16DbManager.getB16DbManager().findB16CadenceHistory(bleMac, dayStr);

            if (resultBeanList == null) {
                showNoData();
                return;
            }
            pagerList.addAll(resultBeanList);
            //pagerAdapter.notifyDataSetChanged();
//
            for (B16CadenceResultBean bc : resultBeanList) {
                Log.e(TAG, "------bc=" + bc.toString());


            }

            analysisAllSportData(resultBeanList);


            selectPosition(0);

            pagerAdapter = new B16CurrSportPagerAdapter(resultBeanList, getActivity(), 0);
            b16BmiViewPager.setAdapter(pagerAdapter);
            b16BmiViewPager.setCurrentItem(0);
            b16BmiViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int position) {
                    selectPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //处理汇总数据显示
    @SuppressLint("SetTextI18n")
    private void analysisAllSportData(List<B16CadenceResultBean> resultBeanList) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            //当天所有运动时间
            int allSportDayTime = 0;

            //当天总的有效卡路里 大于120步频
            float allCountKcal = 0f;
            //总的有效运动时长
            int allCountTime = 0;


            //当天所有运动消耗卡路里
            int allKcalDay = 0;
            for(B16CadenceResultBean resultBean : resultBeanList){
                //卡路里
                int kcal = resultBean.getCalorie();
                allKcalDay +=kcal;
                //间隔
                int sportDuration = resultBean.getSportDuration();
                //个数
                int sportCount = resultBean.getSportCount();
                //总的时间
                int allSportCount = sportCount * sportDuration;
                allSportDayTime += allSportCount;
                //消耗卡路里
                List<B16CadenceBean.DetailDataBean> detailDataBeanList = gson.fromJson(resultBean.getSportDetailStr(), new TypeToken<List<B16CadenceBean.DetailDataBean>>() {
                }.getType());

                //间隔时间
                float tmpLevelTime = Float.valueOf(sportDuration) / 60;
                //有效的个数
                int tempValidCount = 0;

                //有效的卡路里
                int tempValidKcal = 0;


                for(B16CadenceBean.DetailDataBean bb : detailDataBeanList){

                    //步频
                    float stepGa = bb.getStep() / tmpLevelTime;
                    if(stepGa >= 120){
                        tempValidKcal += bb.getCalorie();
                        tempValidCount++;
                    }

                }
                //计算燃烧的脂肪
                float currFatV = tempValidKcal * 1000 / 7700;
                allCountKcal +=currFatV;

                //总的有效运动时长
                allCountTime +=tmpLevelTime*tempValidCount;

            }
            b16BmiSportTimeTv.setText(allCountTime+getResources().getString(R.string.minute));
            b16BmiSportKgTv.setText(decimalFormat.format(allCountKcal/1000)+"克");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //无数据时显示
    private void showNoData(){
        b16BmiSportTimeTv.setText("--");
        b16BmiSportKgTv.setText("--");
        b16BmiLinChart.setData(null);
        b16BmiLinChart.invalidate();
        B16CadenceResultBean tmpB = new B16CadenceResultBean();
        pagerList.add(tmpB);
        if (pagerAdapter == null)
            pagerAdapter = new B16CurrSportPagerAdapter(pagerList, getActivity(), 1);
        pagerAdapter = new B16CurrSportPagerAdapter(pagerList, getActivity(), 1);
        b16BmiViewPager.setAdapter(pagerAdapter);
    }




    //处理数据
    private void selectPosition(int position) {
        try {
            entryArrayList.clear();
            B16CadenceResultBean bb = pagerList.get(position);
            float sportSecond = Float.valueOf(bb.getSportDuration()) / 60f; //分钟
            String sportStr = bb.getSportDetailStr();
            List<B16CadenceBean.DetailDataBean> detailDataBeanList = gson.fromJson(sportStr, new TypeToken<List<B16CadenceBean.DetailDataBean>>() {
            }.getType());
            for (int i = 0; i < detailDataBeanList.size(); i++) {
                //步频
                float stepGa = detailDataBeanList.get(i).getStep() / sportSecond;
                if (maxStep <= stepGa)
                    maxStep = stepGa;
                entryArrayList.add(new Entry(i, stepGa));
            }
            initLinChartData(bb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //实例化图表
    private void initLinChartData(B16CadenceResultBean b16CadenceResultBean) {
        if (entryArrayList.size() <= 0)
            return;
        // B16CadenceResultBean bb = pagerList.get(pagerList.size()-1);
        float sportSecond = Float.valueOf(b16CadenceResultBean.getSportDuration()) / 60f; //分钟
        DecimalFormat decimalFormat = new DecimalFormat("#");
        List<String> xVlist = new ArrayList<>();
        for (int i = 0; i < entryArrayList.size(); i++) {
            float tmpV = i * sportSecond;
            xVlist.add(decimalFormat.format(tmpV) + "min");
        }

        //x轴
        XAxis xAxis = b16BmiLinChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);


        IAxisValueFormatter iAxisValueFormatter = new BarXFormartValue(b16BmiLinChart, xVlist);
        xAxis.setValueFormatter(iAxisValueFormatter);

        //Y轴
        YAxis leftY = b16BmiLinChart.getAxisLeft();
        leftY.setDrawAxisLine(false);
        leftY.setDrawLabels(true);

        YAxis rightY = b16BmiLinChart.getAxisRight();
        rightY.setEnabled(false);

        //保证Y轴从0开始，不然会上移一点
        leftY.setAxisMinimum(0f);
        leftY.setAxisMaximum(maxStep + 100);
        leftY.setLabelCount(3);
        leftY.setGranularity(1f);

        leftY.setEnabled(false);


        //设置图形的基本属性
        /***图表设置***/
        b16BmiLinChart.setDrawGridBackground(false); //是否展示网格线
        b16BmiLinChart.setDrawBorders(false); //是否显示边界
        b16BmiLinChart.setDragEnabled(false); //是否可以拖动
        b16BmiLinChart.setScaleEnabled(false); // 是否可以缩放
        b16BmiLinChart.setTouchEnabled(false); //是否有触摸事件

        b16BmiLinChart.setClipValuesToContent(true);

        //设置XY轴动画效果
        b16BmiLinChart.getDescription().setEnabled(false);


        Legend legend = b16BmiLinChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setYOffset(-5);
        b16BmiLinChart.setData(new LineData());
        //legend.setEnabled(false);


        //设置数据
        lineDataSet = new LineDataSet(entryArrayList, "");
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setForm(Legend.LegendForm.LINE);
        lineDataSet.setColor(Color.parseColor("#FA9850")); //设置该线的颜色
        lineDataSet.setCircleColor(Color.parseColor("#40e0d0")); //设置节点圆圈颜色
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false); //设置是否显示点的坐标值
        lineDataSet.setValueTextColor(Color.parseColor("#5abdfe"));
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);// 设置平滑曲线

        //是否填充
        lineDataSet.setDrawFilled(true);


        //线的集合（可单条或多条线）
        List<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(lineDataSet);
        //把要画的所有线(线的集合)添加到LineData里
        LineData lineData = new LineData(dataSets);

        b16BmiLinChart.setDrawGridBackground(false);
        //把最终的数据setData
        b16BmiLinChart.setData(lineData);
        b16BmiLinChart.isAnimationCacheEnabled();
        //动画
        b16BmiLinChart.animateX(1000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.commentB30BackImg, R.id.b16BmiDateLeftImg,
            R.id.b16BmiDateRightImg,R.id.commentB30ShareImg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                getActivity().finish();
                break;
            case R.id.b16BmiDateLeftImg:
                changeDayData(true);
                break;
            case R.id.b16BmiDateRightImg:
                changeDayData(false);
                break;
            case R.id.commentB30ShareImg:
                startActivity(new Intent(getActivity(),ComputeBMIActivity.class));
                break;
        }
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        findHistoryData(currDay);
    }
}
