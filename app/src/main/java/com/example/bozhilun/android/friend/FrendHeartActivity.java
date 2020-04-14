package com.example.bozhilun.android.friend;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.LogTestUtil;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.friend.bean.FrendHaretBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.adapters.CommonRecyclerAdapter;
import com.example.bozhilun.android.w30s.adapters.MyViewHolder;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
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
 * 好友心率详细数据
 */
public class FrendHeartActivity extends WatchBaseActivity implements RequestView {
    private RequestPressent requestPressent;
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
    @BindView(R.id.line_st)
    LinearLayout line_st;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    private MyAdapter b30HeartDetailAdapter;
    List<FrendHaretBean.FriendHeartRateBean> friendHeartRateBeanList = null;
    /**
     * 列表数据源
     */
    private List<FrendHaretBean.FriendHeartRateBean> dataList = new ArrayList<>();
    /**
     * 图表数据
     */
    List<Integer> allheartList = new ArrayList<>();
    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    String applicant = "";


    //根据日期排序的集合
    private List<String> timeList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_heart_detail_layout);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");

        initViews();
        initData();
    }

    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        line_st.setVisibility(View.GONE);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.heart_rate);
        currDay = df.format(new Date());
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        b30HeartDetailRecyclerView.setLayoutManager(layoutManager);
        b30HeartDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        b30HeartDetailAdapter = new MyAdapter(this, dataList, R.layout.item_b30_heart_detail_layout);
        b30HeartDetailRecyclerView.setAdapter(b30HeartDetailAdapter);
    }

    /**
     * rec---适配器
     */
    class MyAdapter extends CommonRecyclerAdapter<FrendHaretBean.FriendHeartRateBean> {

        public MyAdapter(Context context, List<FrendHaretBean.FriendHeartRateBean> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(MyViewHolder holder, final FrendHaretBean.FriendHeartRateBean item) {
            try {
                holder.setText(R.id.itemHeartDetailDateTv, item.getTime());
                holder.setText(R.id.itemHeartDetailValueTv, item.getHeartRate() + "");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.rateCurrDateLeft,
            R.id.rateCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(this);
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

    private void initData() {
        rateCurrdateTv.setText(currDay);
        findFrendHeartItem(currDay);
    }

    /**
     * 查询好友日 步数详细数据
     */
    public void findFrendHeartItem(String rtc) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendHeartToDayData;
        JSONObject sleepJson = new JSONObject();
        try {
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            if (!WatchUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            if (!WatchUtils.isEmpty(applicant)) sleepJson.put("applicant", applicant);
            sleepJson.put("rtc", rtc);
            Log.d("-----------朋友--", "获取好友详日细心率参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendHeartActivity.this, sleepJson.toString(), 0);
        }
    }


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object != null || !TextUtils.isEmpty(object.toString().trim()) || !object.toString().contains("<html>")) {
            LogTestUtil.e("-----------朋友--", "获取好友详日细心率返回--" + object.toString());
            try {
                JSONObject jsonObject = new JSONObject(object.toString());
                if(jsonObject.getInt("code") == 200){
                    String data = jsonObject.getString("data");
                    List<FrendHaretBean.FriendHeartRateBean> tmpList = new Gson().fromJson(data,
                            new TypeToken<List<FrendHaretBean.FriendHeartRateBean>>(){}.getType());
                    if(tmpList != null)
                        showChartAndList(tmpList);
                }else{
                    dataList.clear();
                    b30HeartDetailAdapter.notifyDataSetChanged();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    /**
     * 显示 图表 和 list
     *
     * @param heartList
     */
    void showChartAndList(List<FrendHaretBean.FriendHeartRateBean> heartList) {
        dataList.clear();
        allheartList.clear();
        timeList.clear();
        if (heartList == null || heartList.isEmpty()) {
            b30HeartDetailView.setRateDataList(allheartList);
            b30HeartDetailView.invalidate();
            b30HeartDetailAdapter.notifyDataSetChanged();
            return;
        }

        Map<String,String> heartMap = WatchUtils.setHalfDateMap();
        Log.e("好友","-----heartMap="+heartMap.size());
        for (FrendHaretBean.FriendHeartRateBean item :
                heartList) {
            heartMap.put(item.getTime(),item.getHeartRate()+"");
            //allheartList.add(item.getHeartRate());
            if (item.getHeartRate() > 0) {
                dataList.add(item);
            }
        }

        //遍历map
        for(Map.Entry<String,String> mp : heartMap.entrySet()){
            timeList.add(mp.getKey());
        }

        Collections.sort(timeList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for(int i = 0;i<timeList.size();i++){
            int heartV = Integer.valueOf(heartMap.get(timeList.get(i)));
            allheartList.add(heartV);
        }


        if (allheartList != null) {
            b30HeartDetailView.setCanvasBeanLin(true);
            b30HeartDetailView.setRateDataList(allheartList);
            b30HeartDetailView.invalidate();
        }
        if (dataList != null && !dataList.isEmpty()) {
            Collections.sort(dataList, new Comparator<FrendHaretBean.FriendHeartRateBean>() {
                @Override
                public int compare(FrendHaretBean.FriendHeartRateBean o1, FrendHaretBean.FriendHeartRateBean o2) {
                    return o2.getTime().compareTo(o1.getTime());
                }
            });
            b30HeartDetailAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
