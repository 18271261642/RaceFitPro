package com.example.bozhilun.android.b30;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.B30HeartDetailAdapter;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * B30心率详情界面
 */
public class B30HeartDetailActivity extends WatchBaseActivity {

    /**
     * 跳转到B30HeartDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B30HeartDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30HeartDetailView)
    B30CusHeartView b30HeartDetailView;
    @BindView(R.id.b30HeartDetailRecyclerView)
    RecyclerView b30HeartDetailRecyclerView;
    @BindView(R.id.rateCurrdateTv)
    TextView rateCurrdateTv;
//    private List<HalfHourRateData> halfHourRateDatasList;
//    private List<HalfHourSportData> halfHourSportDataList;


    private List<CusVPHalfRateData> halfHourRateDatasList;
    private List<CusVPHalfSportData> halfHourSportDataList;

    private B30HeartDetailAdapter b30HeartDetailAdapter;

    /**
     * 心率图标数据
     */
    List<Integer> heartList;

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
        setContentView(R.layout.activity_b30_heart_detail_layout);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.heart_rate);
        commentB30ShareImg.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30HeartDetailRecyclerView.setLayoutManager(layoutManager);
        b30HeartDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        halfHourRateDatasList = new ArrayList<>();
        halfHourSportDataList = new ArrayList<>();
        b30HeartDetailAdapter = new B30HeartDetailAdapter(halfHourRateDatasList, halfHourSportDataList, this);
        b30HeartDetailRecyclerView.setAdapter(b30HeartDetailAdapter);

        heartList = new ArrayList<>();
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }

    private void initData() {
        rateCurrdateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        if(WatchUtils.isEmpty(mac))
            return;
        String rate = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_RATE);
//        List<HalfHourRateData> rateData = gson.fromJson(rate, new TypeToken<List<HalfHourRateData>>() {
//        }.getType());


        List<CusVPHalfRateData> rateData = gson.fromJson(rate, new TypeToken<List<CusVPHalfRateData>>() {
        }.getType());


        String sport = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                .TYPE_SPORT);
//        List<HalfHourSportData> sportData = gson.fromJson(sport, new TypeToken<List<HalfHourSportData>>() {
//        }.getType());

        List<CusVPHalfSportData> sportData = gson.fromJson(sport, new TypeToken<List<CusVPHalfSportData>>() {
        }.getType());


        halfHourRateDatasList.clear();
        halfHourSportDataList.clear();
//        MyLogUtil.d("------------", rateData.size() + "========" + sportData.size());

        List<Map<String, Integer>> listMap = new ArrayList<>();
        if (rateData != null && !rateData.isEmpty()) {
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);
                CusVPTimeData tmpDate = rateData.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();

                if (tmpIntDate == time) {
                    map.put("val", rateData.get(k).getRateValue());
                    if (k < rateData.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
            }
            Collections.sort(rateData, new Comparator<CusVPHalfRateData>() {
                @Override
                public int compare(CusVPHalfRateData o1, CusVPHalfRateData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });

            Collections.sort(sportData, new Comparator<CusVPHalfSportData>() {
                @Override
                public int compare(CusVPHalfSportData o1, CusVPHalfSportData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });
            halfHourRateDatasList.addAll(rateData);
            halfHourSportDataList.addAll(sportData);
        }
        heartList.clear();
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, Integer> map = listMap.get(i);
            heartList.add(map.get("val"));
        }
        //圆点的半径
//        b30HeartDetailView.setPointRadio(5);
        //绘制基准线
        b30HeartDetailView.setCanvasBeanLin(true);
        b30HeartDetailView.setRateDataList(heartList);

        b30HeartDetailAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.rateCurrDateLeft,
            R.id.rateCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(B30HeartDetailActivity.this);
                break;
            case R.id.rateCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.rateCurrDateRight:   //切换下一天数据
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
        initData();
    }

}
