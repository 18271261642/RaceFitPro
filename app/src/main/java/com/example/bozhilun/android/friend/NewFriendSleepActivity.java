package com.example.bozhilun.android.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.commdbserver.CommSleepDb;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.friend.views.CusFriendBean;
import com.example.bozhilun.android.friend.views.CusFriendSleepView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.example.bozhilun.android.w30s.views.W30S_SleepChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看好友睡眠详细数据
 * Created by Admin
 * Date 2019/5/17
 */
public class NewFriendSleepActivity extends WatchBaseActivity implements RequestView {

    private static final String TAG = "NewFriendSleepActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.detailCusSleepView)
    W30S_SleepChart detailCusSleepView;
    @BindView(R.id.detailAllSleepTv)
    TextView detailAllSleepTv;
    @BindView(R.id.detailAwakeNumTv)
    TextView detailAwakeNumTv;
    @BindView(R.id.detailStartSleepTv)
    TextView detailStartSleepTv;
    @BindView(R.id.detailAwakeTimeTv)
    TextView detailAwakeTimeTv;
    @BindView(R.id.detailDeepTv)
    TextView detailDeepTv;
    @BindView(R.id.detailHightSleepTv)
    TextView detailHightSleepTv;
    @BindView(R.id.sleepCurrDateTv)
    TextView sleepCurrDateTv;

    @BindView(R.id.cusFriendSleepView)
    CusFriendSleepView cusFriendSleepView;
    @BindView(R.id.friendSleepSeekBar)
    SeekBar friendSleepSeekBar;
    @BindView(R.id.friendStartSleepTv)
    TextView friendStartSleepTv;
    @BindView(R.id.friendEndSleepTv)
    TextView friendEndSleepTv;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);


    private RequestPressent requestPressent;
    private String currDay = WatchUtils.getCurrentDate();
    //好友的id
    private String applicant = "";
    //好友的设备地址
    private String friendBleMac = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_sleep_activity);
        ButterKnife.bind(this);


        initViews();
        Intent intent = getIntent();
        applicant = intent.getStringExtra("applicant");
        friendBleMac = intent.getStringExtra("friendBleMac");
        initData();


    }

    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
//        commentB30ShareImg.setVisibility(View.VISIBLE);


    }

    private void initData() {
        sleepCurrDateTv.setText(currDay);
        findFrendStepItem(currDay);
    }

    /**
     * 查询好友日 睡眠详细数据
     */
    public void findFrendStepItem(String rtc) {
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendSLeepToDayData;
        JSONObject sleepJson = new JSONObject();
        try {

            if (!WatchUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            if (!WatchUtils.isEmpty(applicant)) sleepJson.put("applicant", applicant);
            sleepJson.put("rtc", WatchUtils.obtainAroundDate(rtc, true));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        //获取汇总的睡眠数据，总睡眠时长等
        Map<String, String> map = new HashMap<>();
        map.put("userId", applicant + "");
        map.put("startDate", WatchUtils.obtainAroundDate(rtc, true, 0));
        map.put("endDate", WatchUtils.obtainAroundDate(rtc, true, 0));
        map.put("deviceCode", friendBleMac + "");
        String commParams = new Gson().toJson(map);


        if (requestPressent != null) {
            //获取睡眠详细信息
            requestPressent.getRequestJSONObject(0x01, sleepUrl, NewFriendSleepActivity.this, sleepJson.toString(), 0);
            //获取汇总的睡眠信息
            requestPressent.getRequestJSONObject(0x02, SyncDbUrls.downloadSleepUrl(), NewFriendSleepActivity.this, commParams, 1);
        }
    }


    @OnClick({R.id.commentB30BackImg, R.id.sleepCurrDateLeft,
            R.id.sleepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.sleepCurrDateLeft:    //前一天
                changeDayData(true);
                break;
            case R.id.sleepCurrDateRight:   //后一天
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
        if (WatchUtils.isEmpty(object + "") || object.toString().contains("<html>"))
            return;
        if (what == 0x01) {   //详细睡眠
            analysisDetailSleepData(object);
        } else if (what == 0x02) { //汇总睡眠
            analysisCountSleep(object);
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

    //解析汇总睡眠
    private void analysisCountSleep(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            String dayStr = jsonObject.getString("day");
            List<CommSleepDb> commSleepDbList = new Gson().fromJson(dayStr, new TypeToken<List<CommSleepDb>>() {
            }.getType());
            if (commSleepDbList == null || commSleepDbList.isEmpty()) {
                setNoDataShow();
                return;
            }

            CommSleepDb commSleepDb = commSleepDbList.get(0);
            //Log.e(TAG,"------commSleepDb="+commSleepDb.toString());
            //总睡眠时长
            int allSleepL = commSleepDb.getSleeplen();
            String re = (allSleepL / 60) + "H" +
                    (allSleepL % 60) + "m";
            detailAllSleepTv.setText(re);

            //苏醒次数
            detailAwakeNumTv.setText(commSleepDb.getWakecount() + "");
            //入睡时间
            detailStartSleepTv.setText(commSleepDb.getSleeptime());
            friendStartSleepTv.setText(commSleepDb.getSleeptime());
            //清醒时间
            detailAwakeTimeTv.setText(commSleepDb.getWaketime());
            friendEndSleepTv.setText(commSleepDb.getWaketime());
            //深度睡眠
            String deepSL = commSleepDb.getDeepsleep() / 60 + "H" + commSleepDb.getDeepsleep() % 60 + "m";
            detailDeepTv.setText(deepSL);
            //浅睡
            String lowSL = commSleepDb.getShallowsleep() / 60 + "H" + commSleepDb.getShallowsleep() % 60 + "m";
            detailHightSleepTv.setText(lowSL);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析详细睡眠数据
    private void analysisDetailSleepData(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if (!jsonObject.has("code")) {
                setNoDataList();
                return;
            }

            int resultCode = jsonObject.getInt("code");
            if (resultCode == 200) {
                String slListStr = jsonObject.getString("data");
                if (!WatchUtils.isEmpty(slListStr) && !slListStr.equals("[]")) {
                    List<FriendDetailSleepDb> sleepList = new Gson().fromJson(slListStr, new TypeToken<List<FriendDetailSleepDb>>() {
                    }.getType());

                    List<CusFriendBean> cusList = new ArrayList<>();
                    int countSleepSum = 0;
                    if (sleepList != null && !sleepList.isEmpty()) {
                        //sleepList.remove(0);
                        for (int i = 0; i < sleepList.size(); i++) {
                            //开始日期
                            String startDateStr = sleepList.get(i).getStartTime();
                            long startLongDate = sdf.parse(startDateStr).getTime();
                            //后一个日期
                            if (i + 1 < sleepList.size()) {
                                //结束日期
                                String endDateStr = sleepList.get(i + 1).getStartTime();
                                long nextLongDate = sdf.parse(endDateStr).getTime();

                                //差值
                                int differenceV = (int) ((nextLongDate - startLongDate) / (60 * 1000));
                                int sleepType = sleepList.get(i).getSleepType();
                                int sleepTime = (differenceV < 0 ? (differenceV + 24 * 60) : differenceV);
                                countSleepSum += sleepTime;

                                CusFriendBean cusFriendBean = new CusFriendBean(sleepType, sleepTime);

                                cusList.add(cusFriendBean);
                            }

                        }

                        cusList.add(0, new CusFriendBean(1, 4));
                        cusList.add(new CusFriendBean(1, 4));
                        cusFriendSleepView.setAllSleepTime(countSleepSum+8);
                        cusFriendSleepView.setSleepList(cusList);

                        //showSeekBarSchView(sleepList, countSleepSum +3);

                    } else {
                        setNoDataList();
                    }

                } else {
                    setNoDataList();
                }

            } else {
                setNoDataList();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSeekBarSchView(List<FriendDetailSleepDb> cusLists, final int count) {
        Log.e(TAG, "--------count=" + count);
        try {
            //开始时间
            final String seekStartTime = cusLists.get(0).getStartTime();
            //结束时间
            String seekEndTime = cusLists.get(cusLists.size() - 2).getStartTime();
            //Log.e(TAG, "-------开始时间=" + seekStartTime + "--=" + seekEndTime);
            friendSleepSeekBar.setMax(count);
            friendSleepSeekBar.setProgress(-2);
            cusFriendSleepView.setShowSeekLin(false);
            friendSleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == count)
                        return;
                    //Log.e(TAG, "---progress=" + progress);
                    //每滑动一个单位的时间
                    try {
                        long currTime = sdf.parse(seekStartTime).getTime()  + progress * 1000 * 60;
                        //时间
                        String currIntTime = WatchUtils.getLongToDate("HH:mm", currTime);
                        cusFriendSleepView.setTimeTxt(currIntTime);
                        cusFriendSleepView.setSeekX(progress);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    cusFriendSleepView.setShowSeekLin(true, 0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setNoDataList() {
        cusFriendSleepView.setAllSleepTime(0);
        cusFriendSleepView.setSleepList(new ArrayList<CusFriendBean>());
    }


    //无数据时显示
    private void setNoDataShow() {

        //总睡眠时长
        detailAllSleepTv.setText("--");

        //苏醒次数
        detailAwakeNumTv.setText("--");
        //入睡时间
        detailStartSleepTv.setText("--");
        //清醒时间
        detailAwakeTimeTv.setText("--");
        //深度睡眠
        detailDeepTv.setText("--");
        //浅睡
        detailHightSleepTv.setText("--");

        friendStartSleepTv.setText("");
        friendEndSleepTv.setText("");


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null)
            requestPressent.detach();
    }


    class FriendDetailSleepDb {


        /**
         * userId : 8c4c511a45374bb595e6fdf30bb878b7
         * deviceCode : F0:62:B1:55:AA:9B
         * sleepType : 2
         * startTime : 23:35
         * day : 2019-07-25
         * addTime : 2019-07-26 08:33:18
         * updateTime : 2019-07-26 08:33:18
         */

        private String userId;
        private String deviceCode;
        private int sleepType;
        private String startTime;
        private String day;
        private String addTime;
        private String updateTime;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public int getSleepType() {
            return sleepType;
        }

        public void setSleepType(int sleepType) {
            this.sleepType = sleepType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
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
    }


}