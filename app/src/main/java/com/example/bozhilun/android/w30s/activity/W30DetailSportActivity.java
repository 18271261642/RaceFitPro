package com.example.bozhilun.android.w30s.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.commdbserver.W30StepDetailBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * W30,W31的详细步数
 * Created by Admin
 * Date 2019/3/19
 */
public class W30DetailSportActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;


    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.b30SportChartLin1)
    LinearLayout b30SportChartLin1;
    @BindView(R.id.b30StepDetailRecyclerView)
    RecyclerView b30StepDetailRecyclerView;

    @BindView(R.id.stepCurrDateTv)
    TextView stepCurrDateTv;
    @BindView(R.id.countStepTv)
    TextView countStepTv;
    @BindView(R.id.countDisTv)
    TextView countDisTv;
    @BindView(R.id.countKcalTv)
    TextView countKcalTv;

    private List<W30StepDetailBean> listMap;
    //adapter
    private W30DetailSportAdapter w30DetailSportAdapter;

    //日期，默认当天
    private String currDay = WatchUtils.getCurrentDate();

    /**
     * 图表步数数据
     */
    List<BarEntry> b30ChartList = new ArrayList<>();
    private Gson gson = new Gson();


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1001:
                    List<W30StepDetailBean> lt = (List<W30StepDetailBean>) msg.obj;
                    if (lt == null || lt.isEmpty()) {
                        listMap.clear();
                        w30DetailSportAdapter.notifyDataSetChanged();
                        b30BarChart.invalidate();

                        countStepTv.setText("--");
                        countDisTv.setText("--");
                        countKcalTv.setText("--");

                        return;
                    }

                    listMap.addAll(lt);
                    w30DetailSportAdapter.notifyDataSetChanged();

                    for (int i = 0; i < lt.size(); i++) {
                        b30ChartList.add(new BarEntry(i, Float.valueOf(lt.get(i).getStepValue())));
                    }
                    initBarChart(b30ChartList);
                    break;
                case 1002:
                    Map<String, String> stepMap = (Map<String, String>) msg.obj;
                    if(stepMap == null || stepMap.isEmpty())
                        return;
                    countStepTv.setText(stepMap.get("count_step")+"");
                    countDisTv.setText(StringUtils.substringBefore(stepMap.get("count_discance"),".")+" m");
                    countKcalTv.setText(StringUtils.substringBefore(stepMap.get("count_kcal"),".")+" kcal");
                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_step_detail_layout);
        ButterKnife.bind(this);

        initViews();

        findSportFromDb(currDay);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.move_ment);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin1.setBackgroundColor(Color.parseColor("#2594EE"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, true);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listMap = new ArrayList<>();
        w30DetailSportAdapter = new W30DetailSportAdapter(listMap);
        b30StepDetailRecyclerView.setAdapter(w30DetailSportAdapter);

    }

    @OnClick({R.id.commentB30BackImg, R.id.stepCurrDateLeft,
            R.id.stepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.stepCurrDateLeft:     //上一天
                changeDayData(true);
                break;
            case R.id.stepCurrDateRight:   //下一天
                changeDayData(false);
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
        findSportFromDb(currDay);
    }


    //查询数据
    private void findSportFromDb(final String dateStr) {
        try {
            stepCurrDateTv.setText(dateStr);
            listMap.clear();
            b30ChartList.clear();
            final String bleMac = WatchUtils.getSherpBleMac(W30DetailSportActivity.this);
            if (WatchUtils.isEmpty(bleMac))
                return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<B30HalfHourDB> w30List = B30HalfHourDao.getInstance().findW30SportData(dateStr, bleMac,B30HalfHourDao.TYPE_SPORT);
                    if (w30List == null) {
                        Message message = handler.obtainMessage();
                        message.what = 1001;
                        message.obj = listMap;
                        handler.sendMessage(message);
                        return;
                    }

                    //解析所需数据
                    String stepStr = w30List.get(0).getOriginData();
                    Map<String, String> allMap = gson.fromJson(stepStr, Map.class);
                    //详细数据
                    String stepDetailStr = allMap.get("stepDetail");

                    List<W30StepDetailBean> tempList = new Gson().fromJson(stepDetailStr, new TypeToken<List<W30StepDetailBean>>() {
                    }.getType());

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = tempList;
                    handler.sendMessage(message);

                    Map<String, String> stepMap = new HashMap<>();
                    stepMap.put("count_step", allMap.get("countSteps"));
                    stepMap.put("count_discance", allMap.get("countDiscance"));
                    stepMap.put("count_kcal", allMap.get("countKcal"));

                    Message message1 = handler.obtainMessage();
                    message1.what = 1002;
                    message1.obj = stepMap;
                    handler.sendMessage(message1);


                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }
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


    class W30DetailSportAdapter extends RecyclerView.Adapter<W30DetailSportAdapter.W30ViewHolder> {

        private List<W30StepDetailBean> list;

        public W30DetailSportAdapter(List<W30StepDetailBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public W30ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(W30DetailSportActivity.this).inflate(R.layout.item_b30_step_detail_layout,
                    parent, false);
            return new W30ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull W30ViewHolder holder, int position) {
            //int steps = list.get(position).getStepValue();
            holder.timeTv.setText(list.get(position).getStepDate() + "");
            holder.kcalTv.setText(list.get(position).getStepValue() + "");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class W30ViewHolder extends RecyclerView.ViewHolder {

            TextView timeTv, kcalTv;
            ImageView img;

            public W30ViewHolder(View itemView) {
                super(itemView);
                timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
                kcalTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
                img = itemView.findViewById(R.id.itemB30StepDetailImg);
            }
        }


    }

}
