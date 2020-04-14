package com.example.bozhilun.android.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CusB30CusBloadView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
 * 查看好友血压详细页面
 * Created by Admin
 * Date 2019/5/18
 */
public class NewFriendBpActivity extends WatchBaseActivity implements RequestView {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.bloadCurrDateTv)
    TextView bloadCurrDateTv;
    @BindView(R.id.friendCusB30BpView)
    CusB30CusBloadView friendCusB30BpView;
    @BindView(R.id.friendDetailBloadRecyclerView)
    RecyclerView friendDetailBloadRecyclerView;

    private RequestPressent requestPressent ;
    private List<FriendBpDB> friendList = null;

    private FriendBpAdapter friendBpAdapter;

    String applicant = "";  //好友的ID
    String currDay = WatchUtils.getCurrentDate();

    //血压图表显示的数据源
    private List<Map<String,Map<Integer,Integer>>> cusResultMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_bp_layout);
        ButterKnife.bind(this);


        initViews();
        applicant = getIntent().getStringExtra("applicant");
        Log.e("血压","-------applicant="+applicant);
        getBpData(currDay);

    }

    private void getBpData(String currDay) {
        bloadCurrDateTv.setText(currDay);

        findFriendBpData(currDay);
    }

    //查询好友的数据
    private void findFriendBpData(String currDay) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendBpToDayData;
        JSONObject sleepJson = new JSONObject();
        try {
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            if (!WatchUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            if (!WatchUtils.isEmpty(applicant)) sleepJson.put("applicant", applicant);
            sleepJson.put("rtc", currDay);
            Log.d("-----------朋友--", "获取好友详血压参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, NewFriendBpActivity.this, sleepJson.toString(), 0);
        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.bloodpressure));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        friendDetailBloadRecyclerView.setLayoutManager(linearLayoutManager);
        friendList = new ArrayList<>();
        friendBpAdapter = new FriendBpAdapter(friendList);
        friendDetailBloadRecyclerView.setAdapter(friendBpAdapter);

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        cusResultMap = new ArrayList<>();
    }

    @OnClick({R.id.commentB30BackImg, R.id.bloadCurrDateLeft, R.id.bloadCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:  //返回
                finish();
                break;
            case R.id.bloadCurrDateLeft:    //前一天的
                changeDayData(true);
                break;
            case R.id.bloadCurrDateRight:   //后一天的
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
        getBpData(currDay);
    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        analysisFriendBpData(object);
    }



    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
        ToastUtil.showShort(NewFriendBpActivity.this,e.getMessage()+"");
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    //解析好友的血压数据
    private void analysisFriendBpData(Object object) {
        Log.e("TAG","----------血压="+object.toString());
        friendList.clear();
        cusResultMap.clear();
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(jsonObject.getInt("code") == 200){
                String data = jsonObject.getString("data");
                List<FriendBpDB> tempLt = new Gson().fromJson(data,new TypeToken<List<FriendBpDB>>(){}.getType());
                if(tempLt != null && !tempLt.isEmpty()){

                    friendList.addAll(tempLt);
                    Collections.sort(friendList, new Comparator<FriendBpDB>() {
                        @Override
                        public int compare(FriendBpDB o1, FriendBpDB o2) {
                            return o1.getTime().compareTo(o2.getTime());
                        }
                    });
                    friendBpAdapter.notifyDataSetChanged();

                    for(FriendBpDB friendBpDB : friendList){

                        // Log.e("TAG","--------friendBpDB="+friendBpDB.toString());
                        Map<Integer, Integer> mp = new ArrayMap<>();
                        mp.put(friendBpDB.diastolic,friendBpDB.systolic);
                        Map<String,Map<Integer,Integer>> mMap = new HashMap<>();
                        mMap.put(friendBpDB.getTime(),mp);
                        cusResultMap.add(mMap);
                    }

                    friendCusB30BpView.setxVSize(friendList.size());
                    friendCusB30BpView.setResultMapData(cusResultMap);

                }else{

                    friendBpAdapter.notifyDataSetChanged();
                    friendCusB30BpView.setxVSize(friendList.size());
                    friendCusB30BpView.setResultMapData(cusResultMap);
                }


            }
            else{
                friendBpAdapter.notifyDataSetChanged();
                friendCusB30BpView.setxVSize(friendList.size());
                friendCusB30BpView.setResultMapData(cusResultMap);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //adapter
    class FriendBpAdapter extends RecyclerView.Adapter<FriendBpAdapter.FriendBpViewHolder> {

        private List<FriendBpDB> list;

        public FriendBpAdapter(List<FriendBpDB> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public FriendBpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_b30_step_detail_layout, parent, false);

            return new FriendBpViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendBpViewHolder holder, int position) {
            holder.timeTv.setText(list.get(position).getTime());
            holder.kcalTv.setText(list.get(position).getSystolic() + "/" + list.get(position).getDiastolic());

            int hightBp = list.get(position).getSystolic();
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

        @Override
        public int getItemCount() {
            return list.size();
        }

        class FriendBpViewHolder extends RecyclerView.ViewHolder {

            TextView timeTv, kcalTv;
            ImageView img;

            public FriendBpViewHolder(View itemView) {
                super(itemView);
                timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
                kcalTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
                img = itemView.findViewById(R.id.itemB30StepDetailImg);
            }
        }
    }


    class FriendBpDB {


        /**
         * rtc : 2019-05-18
         * systolic : 105
         * diastolic : 70
         * addTime : 2019-05-18 10:49:01
         * updateTime : 111
         * id : 7628053
         * deviceCode : D1:70:BB:0D:0D:09
         * time : 00:00
         * userId : 9db53cd27bdf4327b0e8dd01148e1711
         * status : 0
         */

        private String rtc;
        private int systolic;
        private int diastolic;
        private String addTime;
        private String updateTime;
        private int id;
        private String deviceCode;
        private String time;
        private String userId;
        private int status;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getSystolic() {
            return systolic;
        }

        public void setSystolic(int systolic) {
            this.systolic = systolic;
        }

        public int getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(int diastolic) {
            this.diastolic = diastolic;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "FriendBpDB{" +
                    "rtc='" + rtc + '\'' +
                    ", systolic=" + systolic +
                    ", diastolic=" + diastolic +
                    ", addTime='" + addTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", id=" + id +
                    ", deviceCode='" + deviceCode + '\'' +
                    ", time='" + time + '\'' +
                    ", userId='" + userId + '\'' +
                    ", status=" + status +
                    '}';
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }
}
