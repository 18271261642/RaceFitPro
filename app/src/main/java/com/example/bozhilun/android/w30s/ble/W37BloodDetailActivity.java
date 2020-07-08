package com.example.bozhilun.android.w30s.ble;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CusB30CusBloadView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.view.DateSelectDialogView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * W37血压详细页面
 * Created by Admin
 * Date 2019/7/16
 */
public class W37BloodDetailActivity extends WatchBaseActivity {

    private static final String TAG = "W37BloodDetailActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.bloadCurrDateTv)
    TextView bloadCurrDateTv;
    @BindView(R.id.cusB30BpView)
    CusB30CusBloadView cusB30BpView;
    @BindView(R.id.b30DetailLowestBloadTv)
    TextView b30DetailLowestBloadTv;
    @BindView(R.id.b30DetailHeightBloadTv)
    TextView b30DetailHeightBloadTv;
    @BindView(R.id.b30DetailLowestBloadDateTv)
    TextView b30DetailLowestBloadDateTv;
    @BindView(R.id.b30DetailHeightBloadDateTv)
    TextView b30DetailHeightBloadDateTv;
    @BindView(R.id.b30DetailBloadRecyclerView)
    RecyclerView b30DetailBloadRecyclerView;
    @BindView(R.id.bpDetailLin1)
    LinearLayout bpDetailLin1;
    @BindView(R.id.bpDetailLin2)
    LinearLayout bpDetailLin2;

    private W37BloodDetailAdapter w37BloodDetailAdapter;
    private List<Map<String, Map<Integer, Integer>>> linList;

    private String currDay = WatchUtils.getCurrentDate();

    private DateSelectDialogView dateSelectDialogView;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Map<String, Map<Integer, Integer>>> w37BpResultList = (List<Map<String, Map<Integer, Integer>>>) msg.obj;
            if (w37BpResultList.isEmpty()) {
                cusB30BpView.setxVSize(0);
                cusB30BpView.setResultMapData(new ArrayList<Map<String, Map<Integer, Integer>>>());
                linList.clear();
                w37BloodDetailAdapter.notifyDataSetChanged();
            } else {
                cusB30BpView.setxVSize(w37BpResultList.size());
                cusB30BpView.setResultMapData(w37BpResultList);
                linList.addAll(w37BpResultList);
                w37BloodDetailAdapter.notifyDataSetChanged();
            }


        }
    };


    private Gson gson = new Gson();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_bload_detail);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        bloadCurrDateTv.setText(currDay);
        linList.clear();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String bleMac = WatchUtils.getSherpBleMac(W37BloodDetailActivity.this);
                    if (WatchUtils.isEmpty(bleMac))
                        return;
                    List<Map<String, Map<Integer, Integer>>> w37BpResultList = new ArrayList<>();
                    List<B30HalfHourDB> w37BloodList = B30HalfHourDao.getInstance().findW37BloodDetail(currDay, bleMac, B30HalfHourDao.TYPE_BP);
                    if (w37BloodList == null) {
                        Message message = handler.obtainMessage();
                        message.obj = w37BpResultList;
                        message.what = 1004;
                        handler.sendMessage(message);
                    } else {
                        B30HalfHourDB b30HalfHourDB = w37BloodList.get(0);
                        List<Integer> bpInteList = gson.fromJson(b30HalfHourDB.getOriginData(), new TypeToken<List<Integer>>() {
                        }.getType());
                        List<Integer> tmpList = new ArrayList<>();
                        int timeStr = -5;
                        for (int k = 0; k < bpInteList.size(); k += 2) {
                            if (k <= bpInteList.size() - 2) {
                                Map<String, Map<Integer, Integer>> bpMap = new HashMap<>();
                                Map<Integer, Integer> tmpMap = new HashMap<>();
                                tmpMap.put(bpInteList.get(k), bpInteList.get(k + 1));
                                timeStr += 5;
                                tmpList.add(timeStr);
                                int hours = timeStr / 60;
                                int mines = timeStr % 60;
                                String keyBp = (hours < 10 ? "0" + hours : hours) + ":" + (mines < 10 ? "0" + mines : mines);
                                bpMap.put(keyBp, tmpMap);
                                w37BpResultList.add(bpMap);
                            }

                        }

                        //去0
                        List<Map<String, Map<Integer, Integer>>> tempLt = new ArrayList<>();
                        for (Map<String, Map<Integer, Integer>> vP : w37BpResultList) {
                            for (Map<Integer, Integer> bpM : vP.values()) {
                                for (Map.Entry<Integer, Integer> vM : bpM.entrySet()) {
                                    if (vM.getKey() != 0 & vM.getValue() != 0) {
                                        Map<String, Map<Integer, Integer>> bpMap = new HashMap<>();
                                        bpMap.putAll(vP);
                                        tempLt.add(bpMap);
                                    }
                                }
                            }
                        }


                        Message message = handler.obtainMessage();
                        message.obj = tempLt;
                        message.what = 1004;
                        handler.sendMessage(message);

                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.bloodpressure));
        bpDetailLin1.setVisibility(View.GONE);
        bpDetailLin2.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30DetailBloadRecyclerView.setLayoutManager(linearLayoutManager);
        linList = new ArrayList<>();
        w37BloodDetailAdapter = new W37BloodDetailAdapter(linList);
        b30DetailBloadRecyclerView.setAdapter(w37BloodDetailAdapter);

    }

    @OnClick({R.id.commentB30BackImg, R.id.bloadCurrDateLeft,
            R.id.bloadCurrDateRight,R.id.bloadCurrDateTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.bloadCurrDateLeft:    //前一天
                changeDayData(true);
                break;
            case R.id.bloadCurrDateRight:   //后一天
                changeDayData(false);
                break;
            case R.id.bloadCurrDateTv:
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
                initData();
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
        initData();
    }


    class W37BloodDetailAdapter extends RecyclerView.Adapter<W37BloodDetailAdapter.W37ViewHolder> {

        private List<Map<String, Map<Integer, Integer>>> listMap;

        public W37BloodDetailAdapter(List<Map<String, Map<Integer, Integer>>> listMap) {
            this.listMap = listMap;
        }

        @NonNull
        @Override
        public W37ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(W37BloodDetailActivity.this).inflate(R.layout.item_b30_step_detail_layout, viewGroup, false);
            return new W37ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull W37ViewHolder holder, int i) {


            Map<String, Map<Integer, Integer>> reMap = listMap.get(i);
            //遍历Map的key和value
            for (Map.Entry<String, Map<Integer, Integer>> mm : reMap.entrySet()) {
                holder.timeTv.setText(mm.getKey());
                //再遍历map得到高低压的值
                for (Map.Entry<Integer, Integer> mmV : mm.getValue().entrySet()) {
                    //高压值
                    int hightBp = mmV.getKey();
                    holder.kcalTv.setText(hightBp + "/" + mmV.getValue());
                    if (hightBp <= 120) {
                        holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_norma);
                    } else if (hightBp > 120 && hightBp <= 140) {
                        holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_slight);
                    } else if (hightBp > 140 && hightBp <= 150) {
                        holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_serious);
                    } else {
                        holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_veryserious);
                    }


                }
            }


        }

        @Override
        public int getItemCount() {
            return listMap.size();
        }

        class W37ViewHolder extends RecyclerView.ViewHolder {


            TextView timeTv, kcalTv;
            ImageView img;

            public W37ViewHolder(@NonNull View itemView) {
                super(itemView);
                timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
                kcalTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
                img = itemView.findViewById(R.id.itemB30StepDetailImg);
            }
        }
    }


}
