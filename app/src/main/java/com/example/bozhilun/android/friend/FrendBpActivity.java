package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.LogTestUtil;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30CusBloadView;
import com.example.bozhilun.android.friend.bean.BpBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.adapters.CommonRecyclerAdapter;
import com.example.bozhilun.android.w30s.adapters.MyViewHolder;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrendBpActivity extends WatchBaseActivity implements RequestView {
    @SuppressLint("SimpleDateFormat")
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private RequestPressent requestPressent;
    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    String applicant = "";
    @BindView(R.id.rateCurrdateTv)
    TextView rateCurrdateTv;
    @BindView(R.id.b30BpDetailRecyclerView)
    RecyclerView b30BpDetailRecyclerView;
    private MyAdapter b30HeartDetailAdapter;
    @BindView(R.id.b30DetailBloadView)
    B30CusBloadView b30DetailBloadView;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    /**
     * 列表数据源
     */
    private List<BpBean.BplistBean> dataList = new ArrayList<>();
    List<BpBean.BplistBean> friendBpBeanList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_bp_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        initHandler();
        initViews();
        initData();
    }


    private Handler mHandler;

    public void initHandler() {
        mHandler = new Handler(mCallback);
    }


    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    String res = message.obj.toString();
                    BpBean bpBean = new Gson().fromJson(res, BpBean.class);
                    if (bpBean != null) {
                        String resultCode = bpBean.getResultCode();
                        if (!WatchUtils.isEmpty(resultCode) && resultCode.equals("001")) {
                            friendBpBeanList = bpBean.getBplist();
                            if (friendBpBeanList != null && !friendBpBeanList.isEmpty()) {
                                if (b30DetailBloadView != null) {
//                                    b30DetailBloadView.setDataMap(obtainBloodMap(friendBpBeanList));
//                                    b30DetailBloadView.setScale(true);
                                }
                            }
                            showLit(friendBpBeanList);
                        }
                    }
                    break;
                case 0x02:
                    if (friendBpBeanList != null) {
                        friendBpBeanList.clear();
                    } else {
                        friendBpBeanList = new ArrayList<>();
                    }
                    if (b30DetailBloadView != null) {
//                        b30DetailBloadView.setDataMap(obtainBloodMap(friendBpBeanList));
//                        b30DetailBloadView.setScale(true);
                    }
                    showLit(friendBpBeanList);
                    break;
            }
            return false;
        }
    };

    /**
     * 列表现货i是
     *
     * @param friendBpBeanList
     */
    private void showLit(List<BpBean.BplistBean> friendBpBeanList) {
        if (friendBpBeanList == null) friendBpBeanList = new ArrayList<>();
        if (dataList == null) dataList = new ArrayList<>();
        else dataList.clear();
        for (int i = friendBpBeanList.size()-1; i >= 0; i--) {
            if (friendBpBeanList.get(i).getSystolic() > 0 && friendBpBeanList.get(i).getDiastolic() > 0) {
                dataList.add(friendBpBeanList.get(i));
                Log.e("--aa---列表", "时间：" + friendBpBeanList.get(i).getRtc()
                        + "  收缩压：" + friendBpBeanList.get(i).getSystolic() + "  舒张压：" + friendBpBeanList.get(i).getDiastolic());
            }
        }
        //升序排列
        Collections.sort(dataList, new Comparator<BpBean.BplistBean>() {
            @Override
            public int compare(BpBean.BplistBean o1, BpBean.BplistBean o2) {
                return o2.getRtc().compareTo(o1.getRtc());
            }
        });
        Log.e("-----", dataList.toString());
        b30HeartDetailAdapter.notifyDataSetChanged();
    }


    /**
     * 统计血压数据源
     *
     * @param bpData 手环源数据
     * @return Map结果: String:日期 Point:x低压_y高压
     */
    private Map<String, Point> obtainBloodMap(List<BpBean.BplistBean> bpData) {
        if (bpData == null || bpData.isEmpty()) return null;

        //升序排列
        Collections.sort(bpData, new Comparator<BpBean.BplistBean>() {
            @Override
            public int compare(BpBean.BplistBean o1, BpBean.BplistBean o2) {
                return o1.getRtc().substring(11, 13).compareTo(o2.getRtc().substring(11, 13));
            }
        });

        Map<String, Point> dataMap = new LinkedHashMap<>();
        for (int i = bpData.size()-1; i >= 0; i--) {
            String time = bpData.get(i).getRtc().substring(11, 16);
            Log.e("--aa---图表", "时间：" + time + "  收缩压：" + bpData.get(i).getSystolic() + "  舒张压：" + bpData.get(i).getDiastolic());
            Point point = new Point(bpData.get(i).getSystolic(), bpData.get(i).getDiastolic());
            dataMap.put(time, point);
        }
//        for (BpBean.BplistBean item : bpData) {
////            String time = item.getTime().getColck();
//            String time = item.getRtc().substring(11, 16);
//            Log.e("--aa---", "时间：" + time + "  收缩压：" + item.getDiastolic() + "  舒张压：" + item.getSystolic());
//            Point point = new Point(item.getDiastolic(), item.getSystolic());
//            dataMap.put(time, point);
//        }
        return dataMap;
    }

    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        currDay = df.format(new Date());
        commentB30TitleTv.setText(R.string.blood);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        b30BpDetailRecyclerView.setLayoutManager(layoutManager);
        b30BpDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        b30HeartDetailAdapter = new MyAdapter(this, dataList, R.layout.item_b30_step_detail_layout);
        b30BpDetailRecyclerView.setAdapter(b30HeartDetailAdapter);
    }

    /**
     * rec---适配器
     */
    class MyAdapter extends CommonRecyclerAdapter<BpBean.BplistBean> {

        public MyAdapter(Context context, List<BpBean.BplistBean> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(MyViewHolder holder, final BpBean.BplistBean item) {
            int systolic = item.getDiastolic();
            int diastolic = item.getSystolic();
            holder.setText(R.id.itemB30StepDetailTimeTv, item.getRtc().substring(11, 16));
            holder.setText(R.id.itemB30StepDetailKcalTv, item.getDiastolic() + "/" + item.getSystolic());

            /**
             * 收缩压（systolic）（高值）   舒张压（diastolic）（低值）
             *  if (systolic < 90 && diastolic < 60) {
             * frendBpAverage.setText("参考结果" + "  :  " + "低血压");
             *
             *} else if (systolic >= 90 && systolic < 120
             *     && diastolic >= 60 && diastolic < 80) {
             * frendBpAverage.setText("参考结果" + "  :  " + "理想血压");
             *
             *} else if (systolic >= 120 && systolic <= 139
             *     && diastolic >= 80 && diastolic <= 89) {
             * frendBpAverage.setText("参考结果" + "  :  " + "正常血压");
             *
             *} else if (systolic >= 140 && systolic <= 159
             *     && diastolic >= 90 && diastolic <= 99) {
             * frendBpAverage.setText("参考结果" + "  :  " + "轻度血压");
             *
             *} else if (systolic >= 160 && systolic <= 179
             *     && diastolic >= 100 && diastolic <= 109) {
             * frendBpAverage.setText("参考结果" + "  :  " + "中度血压");
             *} else if (systolic >= 180 && diastolic >= 110) {
             * frendBpAverage.setText("参考结果" + "  :  " + "重度血压");
             *} else if (systolic >= 140 && diastolic < 90) {
             * frendBpAverage.setText("参考结果" + "   :   " + "--");
             *} else {
             * frendBpAverage.setText("参考结果" + "   :   " + "--");
             *}
             */

//
//            if (item.getDiastolic() <= 120) {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);
//            } else if (item.getDiastolic() > 120 && item.getDiastolic() <= 140) {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_slight, FrendBpActivity.this);
//            } else if (item.getDiastolic() > 140 && item.getDiastolic() <= 150) {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_serious, FrendBpActivity.this);
//            } else {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_veryserious, FrendBpActivity.this);
//            }

            if (systolic < 90 && diastolic < 60) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);
            } else if (systolic >= 90 && systolic < 120
                    && diastolic >= 60 && diastolic < 80) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);

            } else if (systolic >= 120 && systolic <= 139
                    && diastolic >= 80 && diastolic <= 89) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);
            } else if (systolic >= 140 && systolic <= 159
                    && diastolic >= 90 && diastolic <= 99) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_slight, FrendBpActivity.this);
            } else if (systolic >= 160 && systolic <= 179
                    && diastolic >= 100 && diastolic <= 109) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_serious, FrendBpActivity.this);
            } else if (systolic >= 180 && diastolic >= 110) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_veryserious, FrendBpActivity.this);
            } else if (systolic >= 140 && diastolic < 90) {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);
            } else {
                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_norma, FrendBpActivity.this);
            }

            //血压值异常，没有佩戴测量或者是测量出错
//            else if (systolic >= 140 && diastolic < 90) {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_error, FrendBpActivity.this);
//            } else {
//                holder.setImageGlidInt(R.id.itemB30StepDetailImg, R.mipmap.b30_bloodpressure_detail_error, FrendBpActivity.this);
//            }
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
     * 查询好友日 血压详细数据
     */
    public void findFrendHeartItem(String rtc) {
        String sleepUrl = URLs.HTTPs + Commont.FrendBpToDayData;
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
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendBpActivity.this, sleepJson.toString(), 0);
        }
    }


    /**
     * 网络请求
     *
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (mHandler != null) mHandler.sendEmptyMessage(0x02);
        if (object != null || !TextUtils.isEmpty(object.toString().trim())) {
            LogTestUtil.e("-----------朋友--", "获取好友详日细血压返回--" + object.toString());
            Message message = new Message();
            message.what = what;
            message.arg1 = daystag;
            message.obj = object;
            if (mHandler != null) mHandler.sendMessage(message);
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

    @Override
    public void finish() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
