package com.example.bozhilun.android.b18;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
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
 * B18的步数详情
 * Created by Admin
 * Date 2019/11/21
 */
public class B18StepDetailActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.stepCurrDateTv)
    TextView stepCurrDateTv;
    @BindView(R.id.cusStepDView)
    CusStepDetailView cusStepDView;
    @BindView(R.id.cusStepViewLin)
    ConstraintLayout cusStepViewLin;
    @BindView(R.id.b18ChartTopRel)
    RelativeLayout b18ChartTopRel;
    @BindView(R.id.b18StepRecyclerView)
    RecyclerView b18StepRecyclerView;

    //图表的数据源
    private List<Integer> resultList;

    //列表的数据源
    private List<Map<String,Integer>> mapLists;
    private B18StepAdapter b18StepAdapter;


    private String currDay = WatchUtils.getCurrentDate();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b18_step_detail_layout);
        ButterKnife.bind(this);

        initViews();

        readB18Steps(currDay);


    }

    private void readB18Steps(String dayStr) {
        resultList.clear();
        mapLists.clear();
        stepCurrDateTv.setText(dayStr);
        String bleMac = MyApp.getInstance().getMacAddress();
        Log.e("BB","------bmm="+bleMac);
        if(bleMac == null)
            return;
        String bleName = (String) SharedPreferencesUtils.readObject(B18StepDetailActivity.this, Commont.BLENAME);
        if(bleName.equals("XWatch")){
            readXWatchDeviceSport(bleMac,dayStr);
        }else{
            readB18DeviceSport(bleMac,dayStr);
        }

    }

    //XWatch的步数
    private void readXWatchDeviceSport(String mac,String day){
        resultList.clear();
        mapLists.clear();
        try {
            String originDataStr = B30HalfHourDao.getInstance().findOriginData(mac, day, B30HalfHourDao.XWATCH_DETAIL_SPORT);
            Log.e("BB","-------xWatch="+originDataStr);
            if (originDataStr == null){
                cusStepDView.setSourList(resultList);
                b18StepAdapter.notifyDataSetChanged();
                return;
            }
            List<Integer> orginList = new Gson().fromJson(originDataStr, new TypeToken<List<Integer>>() {
            }.getType());
            List<Integer> halfList = new ArrayList<>();



            for (int i = 0; i < orginList.size(); i += 2) {
                if (i + 1 <= orginList.size() - 1) {
                    int halfHourStepCount = orginList.get(i) + orginList.get(i + 1);
                    halfList.add(halfHourStepCount);

                }
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




    //B16设备
    private void readB18DeviceSport(String mac,String day){
        try {
            String setpStr = B30HalfHourDao.getInstance().findOriginData(mac, day, B30HalfHourDao.B16_DETAIL_SPORT);
            if (WatchUtils.isEmpty(setpStr)){
                cusStepDView.setSourList(resultList);
                b18StepAdapter.notifyDataSetChanged();
                return;
            }
            List<Integer> listInteger = new Gson().fromJson(setpStr, new TypeToken<List<Integer>>() {
            }.getType());
            resultList.addAll(listInteger);
            if (listInteger.size() <= 48) {
                for (int i = 0; i < 48 - listInteger.size(); i++) {
                    resultList.add(0);
                }
            }else{
                resultList = resultList.subList(0,48);
            }
            cusStepDView.setSourList(resultList);
            List<Map<String,Integer>> mapList = new ArrayList<>();
            for(int i = 0;i<resultList.size();i++){
                Map<String,Integer> map = new HashMap<>();
                map.put(WatchUtils.timeStr[i],resultList.get(i));
                mapList.add(map);
            }
            mapLists.addAll(mapList);
            b18StepAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.step));
        b18ChartTopRel.setVisibility(View.GONE);
        cusStepDView.setBackgroundColor(Color.parseColor("#2594EE"));
        cusStepViewLin.setBackgroundColor(Color.parseColor("#2594EE"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b18StepRecyclerView.setLayoutManager(linearLayoutManager);
        b18StepRecyclerView.addItemDecoration(new DividerItemDecoration(B18StepDetailActivity.this,DividerItemDecoration.VERTICAL));
        resultList = new ArrayList<>();
        mapLists = new ArrayList<>();
        b18StepAdapter = new B18StepAdapter(mapLists,B18StepDetailActivity.this);
        b18StepRecyclerView.setAdapter(b18StepAdapter);


    }

    @OnClick({R.id.commentB30BackImg, R.id.stepCurrDateLeft,
            R.id.stepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
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
