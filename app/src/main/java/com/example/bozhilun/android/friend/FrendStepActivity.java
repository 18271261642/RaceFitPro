package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.FrendStepBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 好友步数详细
 */
public class FrendStepActivity extends WatchBaseActivity implements RequestView {

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
    @BindView(R.id.line_dis)
    LinearLayout line_dis;
    @SuppressLint("SimpleDateFormat")
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    private RequestPressent requestPressent;
    //List<FrendStepBean.FriendStepNumberBean> friendStepNumber = null;

    /**
     * 列表数据源
     */
    // private List<FrendStepBean.FriendStepNumberBean> dataList = new ArrayList<>();


    //数据返回
    private List<StepDetailBean> commStepDetailDbList;
    private StepAdapter stepAdapter;


    /**
     * 列表适配器
     */
    //private MyAdapter b30StepDetailAdapter;
    /**
     * 步数数据
     */
    List<BarEntry> b30ChartList = new ArrayList<>();
    String applicant = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_step_detail_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        initHandler();
        initViews();
        initData();
    }

    private void initViews() {

        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        currDay = df.format(new Date());

        line_dis.setVisibility(View.GONE);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.move_ment);
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin1.setBackgroundColor(Color.parseColor("#2594EE"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, true);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        commStepDetailDbList = new ArrayList<>();
        stepAdapter = new StepAdapter(this);
        b30StepDetailRecyclerView.setAdapter(stepAdapter);


    }

    /**
     * 初始化数据
     */
    private void initData() {
        stepCurrDateTv.setText(currDay);
        findFrendStepItem(currDay);
    }


    /**
     * 查询好友日 步数详细数据
     */
    public void findFrendStepItem(String rtc) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendStepToDayData;
        JSONObject sleepJson = new JSONObject();
        try {
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            if (!WatchUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            if (!WatchUtils.isEmpty(applicant)) sleepJson.put("applicant", applicant);
            sleepJson.put("rtc", rtc);

            Log.d("-----------朋友--", "获取好友详日细步数参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendStepActivity.this, sleepJson.toString(), 0);
        }
    }


    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.stepCurrDateLeft,
            R.id.stepCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(this);
                break;
            case R.id.stepCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.stepCurrDateRight:   //切换下一天数据
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


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        analysisStepData(object);

    }


    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    //解析数据
    private void analysisStepData(Object object) {
        commStepDetailDbList.clear();
        b30ChartList.clear();
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(!jsonObject.has("code")){
                commStepDetailDbList.clear();
                stepAdapter.notifyDataSetChanged();

                initBarChart(b30ChartList);
                return;
            }
            if(jsonObject.getInt("code")  == 200) {
                String data = jsonObject.getString("data");
                if (!WatchUtils.isEmpty(data) && !data.equals("[]")) {
                    List<StepDetailBean> lt = new Gson().fromJson(data, new TypeToken<List<StepDetailBean>>() {
                    }.getType());
                    if (lt != null && !lt.isEmpty()) {
                        Collections.sort(lt, new Comparator<StepDetailBean>() {
                            @Override
                            public int compare(StepDetailBean o1, StepDetailBean o2) {
                                return o1.getStartDate().compareTo(o2.getStartDate());
                            }
                        });

                        commStepDetailDbList.addAll(lt);
                        stepAdapter.notifyDataSetChanged();

                        commStepDetailDbList.addAll(lt);
                        stepAdapter.notifyDataSetChanged();

                        //补0操作，
                        Map<String, String> maps = WatchUtils.setHalfDateMap();
                        for (StepDetailBean st : lt) {
                            maps.put(st.getStartDate(), st.getStepNumber() + "");
                        }

                        showChartViews(maps);
                    } else {
                        commStepDetailDbList.clear();
                        stepAdapter.notifyDataSetChanged();

                        initBarChart(b30ChartList);
                        if (b30BarChart != null) {
                            b30BarChart.setNoDataTextColor(Color.WHITE);
                            b30BarChart.invalidate();
                        }
                    }

                } else {
                    commStepDetailDbList.clear();
                    stepAdapter.notifyDataSetChanged();

                    initBarChart(b30ChartList);
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //显示图表
    private void showChartViews(Map<String,String>  maps){
        b30ChartList.clear();
        List<String> timeList = new ArrayList<>();
        for(Map.Entry<String,String> mp : maps.entrySet()){
            timeList.add(mp.getKey().trim());
        }
        Collections.sort(timeList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for(int i = 0;i<timeList.size();i++){
            b30ChartList.add(new BarEntry(i, Integer.valueOf(maps.get(timeList.get(i)))));
        }
        if (b30ChartList != null) {
            initBarChart(b30ChartList);
            b30BarChart.invalidate();
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


    private Handler mHandler;

    public void initHandler() {
//        HandlerThread handlerThread = new HandlerThread("FrendStep");
//        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper(), mCallback);//Three + handler
        mHandler = new Handler(mCallback);
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                switch (message.what) {
                    case 0x01:
                        String res = message.obj.toString();
                        FrendStepBean frendStepBean = new Gson().fromJson(res, FrendStepBean.class);
                        if (!WatchUtils.isEmpty(frendStepBean.getResultCode())
                                && frendStepBean.getResultCode().equals("001")) {
//                            friendStepNumber = frendStepBean.getFriendStepNumber();
//                            if (friendStepNumber != null && !friendStepNumber.isEmpty()) {
//                                showChartAndList(friendStepNumber);
//                            }
                        }
                        break;
                    case 0x02:
//                        if (friendStepNumber != null) {
//                            friendStepNumber.clear();
//                        } else {
//                            friendStepNumber = new ArrayList<>();
//                        }
//                        showChartAndList(friendStepNumber);
                        break;
                }
            }catch (Error error){
                error.printStackTrace();
            }

            return false;
        }
    };

    @Override
    public void finish() {
        if (mHandler!=null)mHandler.removeCallbacksAndMessages(null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mHandler!=null)mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }



    //适配器
    private class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

        private Context mContext;

        public StepAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_step_detail_layout,parent,false);
            return new StepViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
            String time = commStepDetailDbList.get(position).getStartDate();
            holder.timeTv.setText(time);
            holder.stepNumTv.setText(commStepDetailDbList.get(position).getStepNumber()+"");
        }

        @Override
        public int getItemCount() {
            return commStepDetailDbList.size();
        }

        class StepViewHolder extends RecyclerView.ViewHolder{
            TextView timeTv,stepNumTv;

            public StepViewHolder(View itemView) {
                super(itemView);
                timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
                stepNumTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);


            }
        }
    }


    class StepDetailBean{
        private int stepNumber;
        private String startDate;

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }
    }



}
