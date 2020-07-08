package com.example.bozhilun.android.w30s.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.view.DateSelectDialogView;
import com.example.bozhilun.android.w30s.bean.W30HeartBean;
import com.example.bozhilun.android.w30s.views.W30CusHeartView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * W30心率详情页面
 * Created by Admin
 * Date 2019/3/20
 */
public class W30DetailHeartActivity extends WatchBaseActivity {

    private static final String TAG = "W30DetailHeartActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.rateCurrdateTv)
    TextView rateCurrdateTv;

    @BindView(R.id.w30_detail_heart_chart)
    W30CusHeartView w30DetailHeartChart;
    @BindView(R.id.w30HeartDetailRecyclerView)
    RecyclerView w30HeartDetailRecyclerView;

    private List<W30HeartBean> heartBeanList;
    private W30HeartDetailAdapter adapter;

    private Gson gson = new Gson();

    private String currDay = WatchUtils.getCurrentDate();

    private DateSelectDialogView dateSelectDialogView;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:  //数据为空
                    List<Integer> heartValueList = (List<Integer>) msg.obj;
                    w30DetailHeartChart.setRateDataList(heartValueList);
                    heartBeanList.clear();
                    adapter.notifyDataSetChanged();
                    break;
                case 1002:
                    List<W30HeartBean> htBeanList = (List<W30HeartBean>) msg.obj;
                    if(htBeanList == null || htBeanList.isEmpty()){
                        w30DetailHeartChart.setRateDataList(new ArrayList<Integer>());

                        heartBeanList.clear();
                        adapter.notifyDataSetChanged();
                    }else{
                        heartBeanList.clear();
                        List<Integer> htList = new ArrayList<>();
                        for (W30HeartBean w30HeartBean : htBeanList) {
                            htList.add(w30HeartBean.getHeartValues());
                        }
                        w30DetailHeartChart.setShowStandard(true);
                        w30DetailHeartChart.setRateDataList(htList);

                        heartBeanList.addAll(htBeanList);
                        adapter.notifyDataSetChanged();
                    }

                    break;
            }

        }
    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30_detail_heart_layout);
        ButterKnife.bind(this);

        initViews();

        findHeartDetailFromDb(currDay);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_harete_data));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        w30HeartDetailRecyclerView.setLayoutManager(linearLayoutManager);

        heartBeanList = new ArrayList<>();
        adapter = new W30HeartDetailAdapter(heartBeanList);
        w30HeartDetailRecyclerView.setAdapter(adapter);

    }




    private void findHeartDetailFromDb(final String currDay){
        rateCurrdateTv.setText(currDay);
        final String bleMac = WatchUtils.getSherpBleMac(this);
        if(WatchUtils.isEmpty(bleMac))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> resultHeartList = new ArrayList<>();
                List<B30HalfHourDB> w30HeartList = B30HalfHourDao.getInstance().findW30HeartDetail(currDay,
                        bleMac, B30HalfHourDao.TYPE_RATE);
                if (w30HeartList == null) {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = resultHeartList;
                    handler.sendMessage(message);
                } else {
                    //一天只有一条数据
                    String heartStr = w30HeartList.get(0).getOriginData();
                    //Log.e(TAG, "---11-----heartStr=" + heartStr);
                    Map<String, String> htMap = gson.fromJson(heartStr, Map.class);
                    //Log.e(TAG, "----------htMap=" + htMap.toString());

                    String htStr = htMap.get(currDay);
                    //Log.e(TAG, "---22-----htStr=" + htStr);
                    List<W30HeartBean> heartBeanList = gson.fromJson(htStr, new TypeToken<List<W30HeartBean>>() {
                    }.getType());

                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = heartBeanList;
                    handler.sendMessage(message);


                }
            }

        }).start();
    }



    @OnClick({R.id.rateCurrDateLeft, R.id.rateCurrDateRight,
            R.id.commentB30BackImg,R.id.rateCurrdateTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.rateCurrDateLeft: //上一天
                changeDayData(true);
                break;
            case R.id.rateCurrDateRight:    //下一天
                changeDayData(false);
                break;
            case R.id.rateCurrdateTv:
                chooseDate();
                break;
        }
    }
    private void chooseDate() {
        dateSelectDialogView = new DateSelectDialogView(this);
        dateSelectDialogView.show();
        dateSelectDialogView.setOnDateSelectListener(new DateSelectDialogView.OnDateSelectListener() {
            @Override
            public void selectDateStr(String str) {
                dateSelectDialogView.dismiss();
                currDay = str;
                findHeartDetailFromDb(str);
            }
        });
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
        findHeartDetailFromDb(currDay);
    }


    //adapter
    class W30HeartDetailAdapter extends RecyclerView.Adapter<W30HeartDetailAdapter.DetailHeartViewHolder> {

        private List<W30HeartBean> list;

        public W30HeartDetailAdapter(List<W30HeartBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public DetailHeartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(W30DetailHeartActivity.this).inflate(R.layout.item_b30_heart_detail_layout,parent,false);
            return new DetailHeartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailHeartViewHolder holder, int position) {
            int heartValue = list.get(position).getHeartValues();
            holder.dateTv.setText(list.get(position).getTimes());
            holder.valueTv.setText(heartValue==0?"--":heartValue+"");

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class DetailHeartViewHolder extends RecyclerView.ViewHolder{

            TextView dateTv, valueTv;

            public DetailHeartViewHolder(View itemView) {
                super(itemView);
                dateTv = itemView.findViewById(R.id.itemHeartDetailDateTv);
                valueTv = itemView.findViewById(R.id.itemHeartDetailValueTv);
            }
        }
    }
}
