package com.example.bozhilun.android.b15p.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PDBCommont;
import com.example.bozhilun.android.b15p.b15pdb.B15PHeartDB;
import com.example.bozhilun.android.b30.ManualMeaureHeartActivity;
import com.example.bozhilun.android.b30.adapter.B30HeartDetailAdapter;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * B30心率详情界面
 */
public class B15PHeartDetailActivity extends WatchBaseActivity {
    private static final String TAG = "B15PHeartDetailActivity";

    /**
     * 跳转到B30HeartDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B15PHeartDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.line_st)
    LinearLayout line_st;
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
    //private List<HalfHourRateData> halfHourRateDatasList;
    //private List<HalfHourRateData> halfHourRateDatasList2;


    private List<CusVPHalfRateData> halfHourRateDatasList;
    private List<CusVPHalfRateData> halfHourRateDatasList2;

    //    private List<HalfHourSportData> halfHourSportDataList;
    private B30HeartDetailAdapter b30HeartDetailAdapter;

    /**
     * 心率图标数据
     */
    List<Integer> heartList;
    List<Integer> heartListNew;

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
        line_st.setVisibility(View.GONE);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.heart_rate);
        //commentB30ShareImg.setVisibility(View.VISIBLE);


        if (WatchUtils.isB36Device(B15PHeartDetailActivity.this)) {
            commentB30ShareImg.setBackground(getResources().getDrawable(R.drawable.ic_action_new));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30HeartDetailRecyclerView.setLayoutManager(layoutManager);
        b30HeartDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        halfHourRateDatasList = new ArrayList<>();
        halfHourRateDatasList2 = new ArrayList<>();
        //halfHourSportDataList = new ArrayList<>();
        b30HeartDetailAdapter = new B30HeartDetailAdapter(halfHourRateDatasList, null, this);
        b30HeartDetailRecyclerView.setAdapter(b30HeartDetailAdapter);

        heartList = new ArrayList<>();
        heartListNew = new ArrayList<>();

        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
    }



    String[] timeString = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    String[] timeString2 = {"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30",
            "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30",
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"};

    private void initData() {
        rateCurrdateTv.setText(currDay);
        String mac = MyApp.getInstance().getMacAddress();
        if (WatchUtils.isEmpty(mac))
            return;

        List<B15PHeartDB> heartAllDatas = B15PDBCommont.getInstance().findHeartAllDatas(mac, currDay);


        heartList.clear();
        heartListNew.clear();
        halfHourRateDatasList.clear();
        halfHourRateDatasList2.clear();
        if (heartAllDatas != null && !heartAllDatas.isEmpty()) {
//                        Map<String, Integer> heartMapFa = new HashMap<>();//用于保存每个小时
//                        Map<String, Integer> heartMap = new LinkedHashMap<>();
//                        /**
//                         * 设置长度为48的空的数据组
//                         */
//                        for (int i = 0; i < timeString.length; i++) {
//                            heartMap.put(timeString[i], 0);
//                        }
            int allValues1 = 0;
            int valueCount1 = 0;

            int allValues2 = 0;
            int valueCount2 = 0;


            Map<String, Integer> heartMapHM = new LinkedHashMap<>();
            Map<String, Integer> heartMapHMTime = new HashMap<>();//用于保存每个小时
            Map<String, Integer> heartMapFa = new HashMap<>();//用于保存每个小时

            for (int i = 0; i < timeString2.length; i++) {
                heartMapHM.put(timeString2[i], 0);
            }

            for (int i = 0; i < heartAllDatas.size(); i++) {
                B15PHeartDB b15PHeartDB = heartAllDatas.get(i);
                if (b15PHeartDB != null) {
                    String heartData = b15PHeartDB.getHeartData();//2019-10-10
                    String heartTime = b15PHeartDB.getHeartTime();//12:10:10

                    if (currDay.equals(heartData.substring(0, 10))) {
                        int heartNumber = b15PHeartDB.getHeartNumber();//心率值

                        String[] split = heartTime.split("[:]");
                        Log.e(TAG, "===== heartTime " + heartTime + "   " +heartNumber);
                        if (heartMapFa.containsKey(split[0])){

                            //判断---心率之取当天数据，获取数据（mac 时间）返回本身就是当天的，不用判断也可以
                            if (heartMapHMTime.containsKey(split[0] + ":00")
                                    &&(double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {

                                valueCount1++;//1小时测量的次数---用于计算前半小时内的平均数
                                allValues1 = allValues1 + heartNumber;//前半小时内的所有数据累加
                                heartMapHMTime.put(split[0] + ":00", 0);

                                if (allValues1 > 0) {
                                    heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);
                                } else {
                                    heartMapHM.put(split[0] + ":00", 0);
                                }
                                Log.e(TAG, "===== 正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);

                            } else if (heartMapHMTime.containsKey(split[0] + ":30")
                                    &&(double) (Double.valueOf(split[1]) / 60.0) > 0.5) {

                                valueCount2++;//1小时测量的次数---用于计算后半小时内的平均数
                                allValues2 = allValues2 + heartNumber;//后半小时内的所有数据累加
                                heartMapHMTime.put(split[0] + ":30", 0);

                                if (allValues2 > 0) {
                                    heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);
                                } else {
                                    heartMapHM.put(split[0] + ":30", 0);
                                }
                                Log.e(TAG, "===== 半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                            } else {
                                Log.e(TAG, "===== 不包含 2 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
                                if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
                                    heartMapHMTime.put(split[0] + ":00", 0);

                                    allValues1 = heartNumber;
                                    valueCount1 = 1;
                                    heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);

                                    Log.e(TAG, "===== 添加正点 2 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
                                } else {//后半小时
                                    heartMapHMTime.put(split[0] + ":30", 0);

                                    allValues2 = heartNumber;
                                    valueCount2 = 1;
                                    heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);

                                    Log.e(TAG, "===== 添加半点 2  " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                                }

                            }



                        }else {
                            heartMapFa.put(split[0],0);

                            Log.e(TAG, "===== 不包含 " + split[0] + "  " + (double) (Double.valueOf(split[1]) / 60.0));
                            if ((double) (Double.valueOf(split[1]) / 60.0) <= 0.5) {//分除60 小于等于0.5 那就是 前半小时
                                heartMapHMTime.put(split[0] + ":00", 0);

                                allValues1 = heartNumber;
                                valueCount1 = 1;
                                heartMapHM.put(split[0] + ":00", (int) allValues1 / valueCount1);

                                Log.e(TAG, "===== 添加正点 " + split[0] + ":00   " + (int) allValues1 / valueCount1);
                            } else {//后半小时
                                heartMapHMTime.put(split[0] + ":30", 0);

                                allValues2 = heartNumber;
                                valueCount2 = 1;
                                heartMapHM.put(split[0] + ":30", (int) allValues2 / valueCount2);

                                Log.e(TAG, "===== 添加半点 " + split[0] + ":30   " + (int) allValues2 / valueCount2);
                            }
                        }
                    }


                }
            }

            Log.e(TAG, "=AAS==SB=心率真实值设置完成 " + heartMapHM.size() + "   " + heartMapHM.toString());


            List<Integer> heartValue = new ArrayList<>();
            for (String key : heartMapHM.keySet()) {
                heartValue.add(heartMapHM.get(key));
            }
            Log.e(TAG, "====B=aaa " + heartValue.size());
            Log.e(TAG, "=====aaa " + heartValue.toString());
            List<Map<String, Integer>> listMap = new ArrayList<>();
            List<Map<String, Integer>> listMapNew = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                Map<String, Integer> mapNew = new HashMap<>();
                mapNew.put("time", i * 60);
                mapNew.put("val", heartValue.get(i));
                listMapNew.add(mapNew);
            }
            for (int i = 0; i < 48; i++) {
                int time = 30;
                Map<String, Integer> map = new HashMap<>();
                if (i % 2 == 0) {//0  2  4  6      30   120   240  360
                    time = i * 60;
                } else { // 1 3 5 7    60  180  300  420
                    time = i * 30;
                }
                map.put("val", heartValue.get(i));
                map.put("time", time);
                listMap.add(map);
            }
            Log.e(TAG, "=====bbbb " + listMap.toString());
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> map = listMap.get(i);
                heartList.add(map.get("val"));
            }
            Log.e(TAG, "=====cccc " + listMapNew.toString());
            for (int i = 0; i < listMapNew.size(); i++) {
                Map<String, Integer> map = listMapNew.get(i);
                heartListNew.add(map.get("val"));
            }

//            if (b30HeartDetailView != null)
//                b30HeartDetailView.setRateDataList(heartList);


            Log.e(TAG, "=====" + heartList.size() + "==" + heartList.toString());
            for (int i = 0; i < heartList.size(); i++) {
               // TimeData timeData = new TimeData();
                CusVPTimeData cusVPTimeData = new CusVPTimeData();
                if (i % 2 == 0) {
//                    timeData.setHour(Integer.valueOf(timeString[i / 2]));
//                    timeData.setMinute(0);
                    cusVPTimeData.setHour(Integer.valueOf(timeString[i/2]));
                    cusVPTimeData.setMinute(0);
                } else {
//                    timeData.setHour(Integer.valueOf(timeString[(i - 1) / 2]));
//                    timeData.setMinute(30);

                    cusVPTimeData.setHour(Integer.valueOf(timeString[(i - 1) / 2]));
                    cusVPTimeData.setMinute(0);
                }
//                timeData.setHour(Integer.valueOf(timeString[i]));
                //HalfHourRateData hourRateData = new HalfHourRateData(timeData, heartList.get(i));

                CusVPHalfRateData halfRateData = new CusVPHalfRateData(cusVPTimeData,heartList.get(i));

                //halfHourRateDatasList2.add(hourRateData);

                halfHourRateDatasList2.add(halfRateData);
//
//                if (hourRateData.getRateValue() != 0)
//                    halfHourRateDatasList.add(hourRateData);

                if(halfRateData.getRateValue() != 0)
                    halfHourRateDatasList.add(halfRateData);
            }


        }

//
//        if (heartAllDatas != null && !heartAllDatas.isEmpty()) {
//            Log.e(TAG, "===心率表==" + heartAllDatas.toString());
////            String dayTimes = WatchUtils.obtainFormatDate(currDay);
//            Map<String, Integer> heartMapFa = new HashMap<>();
//            Map<String, Integer> heartMap = new LinkedHashMap<>();
//            int allValues1 = 0;
////            int allValues2 = 0;
//            int valueCount1 = 0;
////            int valueCount2 = 0;
//
//            /**
//             * 设置长度为48的空的数据组
//             */
//            for (int i = 0; i < timeString.length; i++) {
//                heartMap.put(timeString[i], 0);
//            }
//            Log.e(TAG, "====bb = " + heartMap.size());
//            Log.e(TAG, "====bbA = " + heartMap.toString());
//
//            heartList.clear();
//            heartListNew.clear();
//            halfHourRateDatasList.clear();
//            halfHourRateDatasList2.clear();
//
//            for (int j = 0; j < heartAllDatas.size(); j++) {
//
//                B15PHeartDB b15PHeartDB = heartAllDatas.get(j);
//                if (b15PHeartDB != null) {
//                    String heartData = b15PHeartDB.getHeartData();
//                    String heartTime = b15PHeartDB.getHeartTime();
//                    //Log.e(TAG, "====进入循环，有数据 = " + heartData + "   " + dayTimes);
//                    // 判断只是取今天的心率数据
//                    if (currDay.equals(heartData.substring(0, 10))) {
//                        int heartNumber = b15PHeartDB.getHeartNumber();
//
//                        //Log.e(TAG, "====进入循环内，有数据 = " + heartData.substring(11, 13) + "   " + dayTimes + "  " + heartNumber);
//                        //2019-04-22 13:59:38
//
//                        ////2019-04-22 13:59:38 ------13:00
////                        if (heartMapFa.containsKey(heartData.substring(11, 13) + ":00")
////                                || heartMapFa.containsKey(heartData.substring(11, 13) + ":30")) {
////
////                            //2019-04-22 13:59:38 ----- 59
////                            String substring = heartData.substring(14, 16).trim();
////                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
////                                valueCount1++;//半小时测量的次数---用于计算半小时内的平均数
////                                allValues1 = allValues1 + heartNumber;//半小时内的所有数据累加
////                                if (allValues1 > 0) {
////                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) allValues1 / valueCount1);
////                                } else {
////                                    heartMap.put(heartData.substring(11, 13) + ":00", (int) 0);
////                                }
////                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
////                            } else {//该小时 30 分钟以后
////                                valueCount2++;//半小时测量的次数---用于计算半小时内的平均数
////                                allValues2 = allValues2 + heartNumber;//半小时内的所有数据累加
////                                if (allValues2 > 0) {
////                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) allValues2 / valueCount2);
////                                } else {
////                                    heartMap.put(heartData.substring(11, 13) + ":30", (int) 0);
////                                }
////                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
////                            }
////                        } else {
////                            String substring = heartData.substring(14, 16).trim();
////                            if (Integer.valueOf(substring) / 60 >= 0.5) {//该小时 30 分钟以前
////                                valueCount1 = 1;
////                                allValues1 = heartNumber;
////                                heartMap.put(heartData.substring(11, 13) + ":00", allValues1);
////                                heartMapFa.put(heartData.substring(11, 13) + ":00", 0);
////                            } else {//该小时 30 分钟以后
////                                valueCount2 = 1;
////                                allValues2 = heartNumber;
////                                heartMap.put(heartData.substring(11, 13) + ":30", allValues2);
////                                heartMapFa.put(heartData.substring(11, 13) + ":30", 0);
////                            }
////                        }
//
//                        //13:59:38
//                        if (heartMapFa.containsKey(heartTime.substring(0, 2))) {
//                            valueCount1++;//半小时测量的次数---用于计算半小时内的平均数
//                            allValues1 = allValues1 + heartNumber;//半小时内的所有数据累加
//                            heartMapFa.put(heartTime.substring(0, 2), 0);
//                            if (allValues1 > 0) {
//                                heartMap.put(heartTime.substring(0, 2), (int) allValues1 / valueCount1);
//                            } else {
//                                heartMap.put(heartTime.substring(0, 2), (int) 0);
//                            }
//                        } else {
//                            valueCount1 = 1;
//                            allValues1 = heartNumber;
//                            heartMap.put(heartTime.substring(0, 2), allValues1);
//                            heartMapFa.put(heartTime.substring(0, 2), 0);
//                        }
//
//                        Log.e(TAG, "=AAS===心率真实值设置完成 " + heartMap.size() + "   " + heartMap.toString());
//
//                    }
//                }
//            }
//
//            List<Integer> heartValue = new ArrayList<>();
//            for (String key : heartMap.keySet()) {
//                heartValue.add(heartMap.get(key));
//            }
//            Log.e(TAG, "====B=aaa " + heartValue.size());
//            Log.e(TAG, "=====aaa " + heartValue.toString());
//            List<Map<String, Integer>> listMap = new ArrayList<>();
//            List<Map<String, Integer>> listMapNew = new ArrayList<>();
//            for (int i = 0; i < 24; i++) {
//                Map<String, Integer> mapNew = new HashMap<>();
//                mapNew.put("time", i * 60);
//                mapNew.put("val", heartValue.get(i));
//                listMapNew.add(mapNew);
//            }
//            for (int i = 0; i < 48; i++) {
//                int time = 30;
//                Map<String, Integer> map = new HashMap<>();
//
//
//                if (i % 2 == 0) {//0  2  4  6      30   120   240  360
//                    time = i * 60;
//                    map.put("val", heartValue.get(i / 2));
//                } else { // 1 3 5 7    60  180  300  420
//                    time = i * 30;
//                    map.put("val", 0);
//                }
//                map.put("time", time);
//                listMap.add(map);
//            }
//            Log.e(TAG, "=====bbbb " + listMap.toString());
//            for (int i = 0; i < listMap.size(); i++) {
//                Map<String, Integer> map = listMap.get(i);
//                heartList.add(map.get("val"));
//            }
//            Log.e(TAG, "=====cccc " + listMapNew.toString());
//            for (int i = 0; i < listMapNew.size(); i++) {
//                Map<String, Integer> map = listMapNew.get(i);
//                heartListNew.add(map.get("val"));
//            }
//
////            if (b30HeartDetailView != null)
////                b30HeartDetailView.setRateDataList(heartList);
//
//
//            Log.e(TAG, "=====" + heartList.size() + "==" + heartList.toString());
//            for (int i = 0; i < heartList.size(); i++) {
//                TimeData timeData = new TimeData();
//                if (i % 2 == 0) {
//                    timeData.setHour(Integer.valueOf(timeString[i / 2]));
//                    timeData.setMinute(0);
//                } else {
//                    timeData.setHour(Integer.valueOf(timeString[(i - 1) / 2]));
//                    timeData.setMinute(30);
//                }
////                timeData.setHour(Integer.valueOf(timeString[i]));
//                HalfHourRateData hourRateData = new HalfHourRateData(timeData, heartList.get(i));
//                halfHourRateDatasList2.add(hourRateData);
//                if (hourRateData.getRateValue() != 0)
//                    halfHourRateDatasList.add(hourRateData);
//            }
//        }
//////////***********/

//
//        String rate = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
//                .TYPE_RATE);
////        Log.e("===获取到的心率2 ", (WatchUtils.isEmpty(rate) ? "苏数据" : rate));
//        heartList.clear();
//        halfHourRateDatasList.clear();
//        halfHourRateDatasList2.clear();
//        if (!WatchUtils.isEmpty(rate)) {
//            List<Integer> rateData = gson.fromJson(rate, new TypeToken<List<Integer>>() {
//            }.getType());
////            halfHourSportDataList.clear();
//            if (rateData != null && !rateData.isEmpty()) {
//                for (int i = 0; i < rateData.size(); i++) {
//                    TimeData timeData = new TimeData();
//                    timeData.setHour(Integer.valueOf(timeString[i]));
//                    HalfHourRateData hourRateData = new HalfHourRateData(timeData, rateData.get(i));
//                    halfHourRateDatasList2.add(hourRateData);
//                    if (hourRateData.getRateValue() != 0)
//                        halfHourRateDatasList.add(hourRateData);
//                }
//            }
//
//
//            List<Map<String, Integer>> listMap = new ArrayList<>();
//            if (halfHourRateDatasList2 != null && !halfHourRateDatasList2.isEmpty()) {
//                int k = 0;
//                for (int i = 0; i < 48; i++) {
//                    Map<String, Integer> map = new HashMap<>();
//                    int time = i * 30;
//                    map.put("time", time);
//                    TimeData tmpDate = halfHourRateDatasList2.get(k).getTime();
//                    int tmpIntDate = tmpDate.getHMValue();
//
//                    if (tmpIntDate == time) {
//                        map.put("val", halfHourRateDatasList2.get(k).getRateValue());
//                        if (k < halfHourRateDatasList2.size() - 1) {
//                            k++;
//                        }
//                    } else {
//                        map.put("val", 0);
//                    }
//                    listMap.add(map);
//                }
//                Collections.sort(halfHourRateDatasList, new Comparator<HalfHourRateData>() {
//                    @Override
//                    public int compare(HalfHourRateData o1, HalfHourRateData o2) {
//                        return o2.getTime().getColck().compareTo(o1.getTime().getColck());
//                    }
//                });
//                Collections.sort(halfHourRateDatasList2, new Comparator<HalfHourRateData>() {
//                    @Override
//                    public int compare(HalfHourRateData o1, HalfHourRateData o2) {
//                        return o2.getTime().getColck().compareTo(o1.getTime().getColck());
//                    }
//                });
//
////                Collections.sort(sportData, new Comparator<HalfHourSportData>() {
////                    @Override
////                    public int compare(HalfHourSportData o1, HalfHourSportData o2) {
////                        return o2.getTime().getColck().compareTo(o1.getTime().getColck());
////                    }
////                });
////                halfHourRateDatasList.addAll(rateData);
////                halfHourSportDataList.addAll(sportData);
//            }
//            for (int i = 0; i < listMap.size(); i++) {
//                Map<String, Integer> map = listMap.get(i);
//                heartList.add(map.get("val"));
//            }
//        }
//

        //圆点的半径
//        b30HeartDetailView.setPointRadio(5);
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
                if (WatchUtils.isB36Device(B15PHeartDetailActivity.this)) {
                    startActivity(ManualMeaureHeartActivity.class);
                } else {
                    WatchUtils.shareCommData(B15PHeartDetailActivity.this);
                }

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
