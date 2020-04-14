package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.LogTestUtil;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.FrendDataBean;
import com.example.bozhilun.android.friend.bean.FrendSleepBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.example.bozhilun.android.w30s.views.W30S_SleepChart;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrendSleepActivity extends WatchBaseActivity implements RequestView {
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.detailSleepQuitRatingBar)
    RatingBar detailSleepQuitRatingBar;
    @BindView(R.id.detailCusSleepView)
    W30S_SleepChart detailCusSleepView;
    @BindView(R.id.text_sleep_nodata)
    TextView text_sleep_nodata;
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
    private int AWAKE2 = 0;//清醒
    private int AWAKE = 0;//清醒次数
    private int AOYE = 0;//熬夜
    private int DEEP = 0;//深睡
    private int SHALLOW = 0;//浅睡
    private int ALLTIME = 0;//浅睡

    private List<W30S_SleepDataItem> beanList = new ArrayList<>();
    private Handler mHandler;
    private RequestPressent requestPressent;
    @SuppressLint("SimpleDateFormat")
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private String applicant = "";
    private String stringJson = "";
    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_sleep_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        stringJson = intent.getStringExtra("stringJson");
        initHandler();
        //initData();
        initViews();
        initData();
    }


    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        currDay = df.format(new Date());


        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        detailSleepQuitRatingBar.setMax(5);
        detailSleepQuitRatingBar.setRating(100);
    }

    private void initData() {
        sleepCurrDateTv.setText(currDay);
        findFrendStepItem(currDay);
    }


    /**
     * 显示图标
     *
     * @param w30S_sleepDataItems
     */
    private void showSleepChartView(List<W30S_SleepDataItem> w30S_sleepDataItems) {
        detailCusSleepView.setBeanList(w30S_sleepDataItems);
    }

    void starSet() {
        detailCusSleepView.setVisibility(View.GONE);
        text_sleep_nodata.setVisibility(View.VISIBLE);

        if (detailStartSleepTv != null) {
            detailStartSleepTv.setVisibility(View.VISIBLE);
            detailStartSleepTv.setText("--");
        }
        //醒来时间 //苏醒时间
        if (detailAwakeTimeTv != null) {
            detailAwakeTimeTv.setVisibility(View.VISIBLE);
            detailAwakeTimeTv.setText("--");
        }
        //深度睡眠
        if (detailDeepTv != null) {
            detailDeepTv.setText("--");
        }
        // 浅度睡眠
        if (detailHightSleepTv != null) {
            detailHightSleepTv.setText("--");
        }

        //苏醒次数
        if (detailAwakeNumTv != null) {
            detailAwakeNumTv.setText("--");
        }
        if (detailAllSleepTv != null) {
            detailAllSleepTv.setText("--");
        }
    }

    private void setTextData(List<W30S_SleepDataItem> w30S_sleepDataItems) {
        if(w30S_sleepDataItems.isEmpty() || w30S_sleepDataItems.size() == 0)
            return;
        SHALLOW = 0;
        DEEP = 0;
        AWAKE = 0;
        AOYE = 0;
        ALLTIME = 0;
        AWAKE2 = 0;

        // 2016-06-28 08:30
        String timeExpend = W30BasicUtils.getTimeExpend("2018-12-04 " + w30S_sleepDataItems.get(0).getStartTime(), "2018-12-04 " + w30S_sleepDataItems.get(w30S_sleepDataItems.size() - 1).getStartTime());
        Log.d("=========睡眠时间消耗", timeExpend);
        try {
            for (int i = 0; i < w30S_sleepDataItems.size(); i++) {
                String startTime = null;
                String startTimeLater = null;
                String sleep_type = null;
                if (i >= (w30S_sleepDataItems.size() - 1)) {
                    startTime = w30S_sleepDataItems.get(i).getStartTime();
                    startTimeLater = w30S_sleepDataItems.get(i).getStartTime();
                    sleep_type = w30S_sleepDataItems.get(i).getSleep_type();
                } else {
                    startTime = w30S_sleepDataItems.get(i).getStartTime();
                    startTimeLater = w30S_sleepDataItems.get(i + 1).getStartTime();
                    sleep_type = w30S_sleepDataItems.get(i).getSleep_type();
                }
                String[] starSplit = startTime.split("[:]");
                String[] endSplit = startTimeLater.split("[:]");

                int startHour = Integer.valueOf(starSplit[0]);
                int endHour = Integer.valueOf(endSplit[0]);

                int startMin = Integer.valueOf(starSplit[1]);
                int endMin = (Integer.valueOf(endSplit[1]));
                if (startHour > endHour) {
                    endHour = endHour + 24;
                }
                int all_m = (endHour - startHour) * 60 + (endMin - startMin);
                if (sleep_type.equals("0") || sleep_type.equals("1") || sleep_type.equals("5")) {
                    AWAKE2 += all_m;
                    ALLTIME += all_m;
                } else if (sleep_type.equals("4")) {
                    AWAKE2 += all_m;
                    ALLTIME += all_m;
                    AWAKE++;
                } else if (sleep_type.equals("2")) {
                    //潜水
                    SHALLOW += all_m;
                    ALLTIME += all_m;
                } else if (sleep_type.equals("3")) {
                    //深水
                    DEEP += all_m;
                    ALLTIME += all_m;
                }
            }

            //入睡时间
            if (detailStartSleepTv != null) {
                detailStartSleepTv.setVisibility(View.VISIBLE);
                detailStartSleepTv.setText(w30S_sleepDataItems.get(0).getStartTime());
            }
            //醒来时间 //苏醒时间
            if (detailAwakeTimeTv != null) {
                detailAwakeTimeTv.setVisibility(View.VISIBLE);
                detailAwakeTimeTv.setText(w30S_sleepDataItems.get(w30S_sleepDataItems.size() - 1).getStartTime());
            }

            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);

            Log.d("=========睡眠时间分中", (DEEP + SHALLOW) + "");
            String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).trim();
            BigDecimal allTimes = new BigDecimal((double)(DEEP + SHALLOW)/60.0).setScale(1, BigDecimal.ROUND_HALF_UP);
            BigDecimal deepTimes = new BigDecimal((double)DEEP/60.0).setScale(1, BigDecimal.ROUND_HALF_UP);
            BigDecimal shhallowTimes = new BigDecimal((double)SHALLOW/60.0).setScale(1, BigDecimal.ROUND_HALF_UP);
            //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);
            //深度睡眠
            if (detailDeepTv != null) {
//                double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
//                detailDeepTv.setText(setScale + getResources().getString(R.string.hour));
                detailDeepTv.setText(deepTimes + getResources().getString(R.string.hour));
            }
            // 浅度睡眠
            if (detailHightSleepTv != null) {
//                double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
//                double v = Double.valueOf(div3) - setScale;
//                double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
//                //double setScale = (double) WatchUtils.div((double) SHALLOW, 60, 1);
//                detailHightSleepTv.setText(setScale1 + getResources().getString(R.string.hour));
                detailHightSleepTv.setText(shhallowTimes + getResources().getString(R.string.hour));
            }

            //苏醒次数
            if (detailAwakeNumTv != null) {
                detailAwakeNumTv.setText(AWAKE + getResources().getString(R.string.cishu));
            }
//                                //清醒时长
//                                if (awakeSleep != null) {
//                                    double setScale = (double) WatchUtils.div((double) AWAKE2, 60, 1);
//                                    awakeSleep.setText(setScale + getResources().getString(R.string.hour));
//                                }

            //睡眠时长
            if (detailAllSleepTv != null) {
//                detailAllSleepTv.setText(div3 + getResources().getString(R.string.hour));
                detailAllSleepTv.setText(allTimes + getResources().getString(R.string.hour));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//        //入睡时间
//        Log.d("-----------朋友--", "入睡时间--" + w30S_sleepDataItems.get(0).getStartTime());
//        //醒来时间
//        Log.d("-----------朋友--", "入睡时间--" + w30S_sleepDataItems.get(w30S_sleepDataItems.size() - 1).getStartTime());
//
//
//        DecimalFormat df = new DecimalFormat("#.#");
//        df.setRoundingMode(RoundingMode.FLOOR);
//        String div3 = df.format((double) (DEEP + SHALLOW) / (double) 60).trim();
//
//        //double div3 = (double) WatchUtils.div((double) (DEEP + SHALLOW), 60, 1);
//
//        double setScale = (double) WatchUtils.div((double) DEEP, 60, 1);
//        Log.d("-----------朋友--", "深--" + setScale + getResources().getString(R.string.hour));
//
//
//        double v = Double.valueOf(div3) - setScale;
//        double setScale1 = (double) WatchUtils.div((double) v, 1, 1);
//        Log.d("-----------朋友--", "浅--" + setScale1 + getResources().getString(R.string.hour));
//
//
//        Log.d("-----------朋友--", "清--" + setScale + getResources().getString(R.string.hour));

//                        double hour = (double) (DEEP + SHALLOW) / (double) 60;
//                        String format = new DecimalFormat("0.00").format(hour);
//                        String[] split = format.split("[.]");
//                        int integer = Integer.valueOf(split[0].trim());
//                        String s1 = String.valueOf(((hour - integer) * 60));
//                        String[] split1 = s1.split("[.]");
//                        String a = "0";
//                        if (split1[0] != null) {
//                            a = split1[0].trim();
//                        }
//
//                        String w30ssleep = (String) SharedPreferenceUtil.get(getContext(), "w30ssleep", "8");
//                        if (!WatchUtils.isEmpty(w30ssleep)) {
//                            int standardSleepAll = Integer.valueOf(w30ssleep.trim()) * 60;
//                            int allSleep = integer * 60 + Integer.valueOf(a);
//                            double standardSleep = WatchUtils.div(allSleep, standardSleepAll, 2);
//                            //int standar = (allSleep / standardSleepAll) * 100;
//                            String strings = String.valueOf((standardSleep * 100));
//                            if (textAllSleepData != null) {
//                                textAllSleepData.setVisibility(View.VISIBLE);
//                                if (textAllSleepData != null) {
//                                    textAllSleepData.setVisibility(View.VISIBLE);
//                                    if (strings.contains(".")) {
//                                        textAllSleepData.setText(getResources().getString(R.string.sleep) + ":" + div3 + getResources().getString(R.string.hour)
//                                                + "    " + getResources().getString(R.string.string_standar) + ":" + strings.split("[.]")[0] + "%"
//                                                + "    " + getResources().getString(R.string.recovery_count_frequency) + ":" + AWAKE);
//                                    } else {
//                                        textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour)
//                                                + "    " + getResources().getString(R.string.string_standar) + (standardSleep * 100) + "%"
//                                                + "    " + getResources().getString(R.string.recovery_count) + AWAKE + getResources().getString(R.string.cishu));
//                                    }
//                                }
//                            }
//                        } else {
//                            if (textAllSleepData != null)
//                                textAllSleepData.setText(getResources().getString(R.string.string_today_sleep_all_time) + div3 + getResources().getString(R.string.hour) + "  " + getResources().getString(R.string.recovery_count) + ":" + AWAKE);
//                        }
//
//
//                        double v = SHALLOW + AWAKE2 + DEEP;
//                        if (qianshuiT != null) {
//                            double v1 = WatchUtils.div(SHALLOW, v, 2);
//                            if (v1 > 0) {
//                                qianshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                            }
//                        }
//                        if (qingxingT != null) {
//                            double v1 = WatchUtils.div(AWAKE2, v, 2);
//                            if (v1 > 0) {
//                                qingxingT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                            }
//                        }
//                        if (shenshuiT != null) {
//                            double v1 = WatchUtils.div(DEEP, v, 2);
//                            if (v1 > 0) {
//                                shenshuiT.setText(String.valueOf(v1 * 100).split("[.]")[0] + "%");
//                            }
//                        }
    }

    /**
     * 查询好友日 睡眠详细数据
     */
    public void findFrendStepItem(String rtc) {
        String sleepUrl = URLs.HTTPs + Commont.FrendSLeepToDayData;
        JSONObject sleepJson = new JSONObject();
        try {
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            if (!WatchUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            if (!WatchUtils.isEmpty(applicant)) sleepJson.put("applicant", applicant);
            sleepJson.put("rtc", rtc);

            //Log.d("-----------朋友--", "获取好友详睡眠参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendSleepActivity.this, sleepJson.toString(), 0);
        }
    }


    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.sleepCurrDateLeft, R.id.sleepCurrDateRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(this);
                break;
            case R.id.sleepCurrDateLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.sleepCurrDateRight:   //切换下一天数据
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
        //closeLoadingDialog();
        if (mHandler != null) mHandler.sendEmptyMessage(0x02);
        if (object == null || TextUtils.isEmpty(object.toString().trim())) return;
        Message message = new Message();
        message.what = what;
        message.arg1 = daystag;
        message.obj = object;
        if (mHandler != null) mHandler.sendMessage(message);
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    public void initHandler() {
        mHandler = new Handler(mCallback);
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                switch (message.what) {
                    case 0x01:

                        mHandler.removeMessages(0x01);
                        String res = message.obj.toString();
                        FrendSleepBean frendSleepBean = new Gson().fromJson(res, FrendSleepBean.class);
                        if (!WatchUtils.isEmpty(frendSleepBean.getResultCode())
                                && frendSleepBean.getResultCode().equals("001")) {
                            List<FrendSleepBean.SslistBean> sslist = frendSleepBean.getSslist();
                            if (beanList != null) {
                                beanList.clear();
                            } else {
                                beanList = new ArrayList<>();
                            }
                            if (sslist != null && !sslist.isEmpty()) {

                                for (FrendSleepBean.SslistBean item :
                                        sslist) {
                                    beanList.add(new W30S_SleepDataItem(
                                            item.getSleep_type() + "".trim(),
                                            item.getStartTime()));
                                }
                                if (!beanList.isEmpty() && beanList.size()>0) {
                                    detailCusSleepView.setVisibility(View.VISIBLE);
                                    text_sleep_nodata.setVisibility(View.GONE);
                                    showSleepChartView(beanList);
                                    setTextData(beanList);
                                    if (!WatchUtils.isEmpty(stringJson)){
                                        LogTestUtil.e("传过来的数据啊",stringJson);
                                        FrendDataBean.SleepDayBean sleepDayBean = new Gson().fromJson(stringJson, FrendDataBean.SleepDayBean.class);
                                        if (sleepDayBean!=null){
                                            String sleepTime = sleepDayBean.getSleepTime();
                                            String wakeTime = sleepDayBean.getWakeTime();
                                            double sleepLen = sleepDayBean.getSleepLen();
                                            double deepSleep = sleepDayBean.getDeepSleep();
                                            double shallowSleep = sleepDayBean.getShallowSleep();

                                            BigDecimal allTimes = new BigDecimal(sleepLen/60.0).setScale(1, BigDecimal.ROUND_DOWN);
                                            BigDecimal deepTimes = new BigDecimal(deepSleep/60.0).setScale(1, BigDecimal.ROUND_DOWN);
                                            BigDecimal shhallowTimes = new BigDecimal(shallowSleep/60.0).setScale(1, BigDecimal.ROUND_DOWN);
                                            if (detailDeepTv != null) {
                                                detailDeepTv.setText(deepTimes + getResources().getString(R.string.hour));
                                            }
                                            // 浅度睡眠
                                            if (detailHightSleepTv != null) {
                                                detailHightSleepTv.setText(shhallowTimes + getResources().getString(R.string.hour));
                                            }

                                            //苏醒次数
                                            if (detailAwakeNumTv != null) {
                                                detailAwakeNumTv.setText(AWAKE + getResources().getString(R.string.cishu));
                                            }
                                            //睡眠时长
                                            if (detailAllSleepTv != null) {
                                                detailAllSleepTv.setText(allTimes + getResources().getString(R.string.hour));
                                            }

                                            //入睡时间
                                            if (detailStartSleepTv != null&&!WatchUtils.isEmpty(sleepTime)) {
                                                detailStartSleepTv.setVisibility(View.VISIBLE);
                                                detailStartSleepTv.setText(sleepTime.trim().substring(11,16));//2018-12-10 15:44
                                            }
                                            //醒来时间 //苏醒时间
                                            if (detailAwakeTimeTv != null&&!WatchUtils.isEmpty(wakeTime)) {
                                                detailAwakeTimeTv.setVisibility(View.VISIBLE);
                                                detailAwakeTimeTv.setText(wakeTime.trim().substring(11,16));
                                            }
                                        }
                                    }

                                    closeLoadingDialog();

                                }
                            }
                        }
                        break;
                    case 0x02:
                        starSet();
                        break;
                }
            } catch (Error e) {
                e.printStackTrace();
            }

            return false;
        }
    };

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
