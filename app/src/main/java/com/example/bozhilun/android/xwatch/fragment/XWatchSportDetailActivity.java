package com.example.bozhilun.android.xwatch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18StepAdapter;
import com.example.bozhilun.android.b30.b30view.CusStepDetailView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * xWatch步数详情页面
 * Created by Admin
 * Date 2020/3/3
 */
public class XWatchSportDetailActivity extends WatchBaseActivity {


    @BindView(R.id.xWatchTitleTv)
    TextView xWatchTitleTv;
    @BindView(R.id.stepCurrDateTv)
    TextView stepCurrDateTv;

    @BindView(R.id.xWatchChartTopRel)
    RelativeLayout xWatchChartTopRel;

    @BindView(R.id.xWatchCusStepDView)
    CusStepDetailView cusStepDView;
    @BindView(R.id.xWatchSportDetailRecyclerView)
    RecyclerView xWatchSportDetailRecyclerView;


    //图表的数据源
    private List<Integer> resultList;

    //列表的数据源
    private List<Map<String,Integer>> mapLists;
    private B18StepAdapter b18StepAdapter;

    private String currDay = WatchUtils.getCurrentDate();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x_watch_sport_detail_layout);
        ButterKnife.bind(this);


        initViews();

        readB18Steps(currDay);
    }


    private void readB18Steps(String dayStr){
        resultList.clear();
        mapLists.clear();
        stepCurrDateTv.setText(dayStr);
        String bleMac = MyApp.getInstance().getMacAddress();
        Log.e("BB","------bmm="+bleMac);
        if(bleMac == null)
            return;
        resultList.clear();
        mapLists.clear();
        try {
            String originDataStr = B30HalfHourDao.getInstance().findOriginData(bleMac, dayStr, B30HalfHourDao.XWATCH_DETAIL_SPORT);
            Log.e("BB","-------xWatch="+originDataStr);
            if (originDataStr == null){
                cusStepDView.setSourList(resultList);
                b18StepAdapter.notifyDataSetChanged();
                return;
            }
            List<Integer> orginList = new Gson().fromJson(originDataStr, new TypeToken<List<Integer>>() {
            }.getType());
            String saveBleName = (String) SharedPreferencesUtils.readObject(XWatchSportDetailActivity.this, Commont.BLENAME);
            if(saveBleName == null)
                return;
            List<Integer> halfList = new ArrayList<>();
            if(saveBleName.equals("XWatch")){
                for (int i = 0; i < orginList.size(); i += 2) {
                    if (i + 1 <= orginList.size() - 1) {
                        int halfHourStepCount = orginList.get(i) + orginList.get(i + 1);
                        halfList.add(halfHourStepCount);
                    }
                }
            }else{
                halfList.addAll(orginList);
            }
            resultList.addAll(halfList);
            cusStepDView.setSourList(resultList);
            List<Map<String,Integer>> mapList = new ArrayList<>();
            for(int k = 0;k<halfList.size();k++){
                Map<String,Integer> map = new HashMap<>();
                map.put(WatchUtils.timeStr[k],halfList.get(k));
                mapList.add(map);
            }

            mapLists.addAll(mapList);
            b18StepAdapter.notifyDataSetChanged();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initViews() {
        xWatchTitleTv.setText(getResources().getString(R.string.step));
        xWatchChartTopRel.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xWatchSportDetailRecyclerView.setLayoutManager(linearLayoutManager);
        xWatchSportDetailRecyclerView.addItemDecoration(new DividerItemDecoration(XWatchSportDetailActivity .this, DividerItemDecoration.VERTICAL));
        resultList = new ArrayList<>();
        mapLists = new ArrayList<>();
        b18StepAdapter = new B18StepAdapter(mapLists,XWatchSportDetailActivity.this);
        xWatchSportDetailRecyclerView.setAdapter(b18StepAdapter);



    }

    @OnClick({R.id.xWatchBackImg, R.id.stepCurrDateLeft,
            R.id.stepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.xWatchBackImg:
                finish();
                break;
            case R.id.stepCurrDateLeft:
                changeDayData(true);
                break;
            case R.id.stepCurrDateRight:
                changeDayData(false);
                break;
        }
    }


    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        readB18Steps(currDay);
    }

}
