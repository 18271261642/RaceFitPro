package com.example.bozhilun.android.w30s.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.SleepBeanNew;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.NewsSleepBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.ScreenUtils;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/4/3 18:26
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SleepHistoryActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "SleepHistoryActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.step_or_sleep_list)
    ListView stepOrSleepList;
    @BindView(R.id.bar_mores)
    TextView barMores;
    @BindView(R.id.image_data_type)
    ImageView imageDataType;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //    SubscriberOnNextListener subscriberOnNextListener;
//    CommonSubscriber commonSubscriber;
    private List<NewsSleepBean> newsSleepBeanList, NewnewsSleepBeanList;  //数据源
    private HistoryCustomPopuWindow customPopuWindow;//popWinow
    private SleepDataAdapter sleepDataAdapter;
    private RequestPressent requestPressent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_sleep_or_step_history_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.sleep_data_history));
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
//        barMores.setVisibility(View.GONE);
    }


    @OnClick({R.id.image_back, R.id.bar_mores})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                //setSlecteDateTime();
                if (customPopuWindow == null) {
                    customPopuWindow = new HistoryCustomPopuWindow(SleepHistoryActivity.this, listener);
                }
                setBack();//yyyy-MM-dd
                int screenWidth = ScreenUtils.getScreenWidth(SleepHistoryActivity.this);
                int screenHeight = ScreenUtils.getScreenHeight(SleepHistoryActivity.this);
                int width = screenWidth / 8 * 7;
                int height = screenHeight / 3;
                customPopuWindow.setWidth(width);
                customPopuWindow.setHeight(height);
                customPopuWindow.showAtLocation(barMores,
                        Gravity.CENTER_HORIZONTAL, 0, -(height / 3 * 2));
                break;
        }
    }

    private void setBack() {
        String currentDate2 = W30BasicUtils.getCurrentDate2();
        String substring = (String) SharedPreferencesUtils.getParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", currentDate2.substring(5, 7));
        strlyChage();
        switch (substring) {
            case "01":
                SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "01");
                customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "02":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "02");
                customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "03":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "03");
                customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "04":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "04");
                customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "05":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "05");
                customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "06":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "06");
                customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "07":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "07");
                customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "08":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "08");
                customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "09":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "09");
                customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "10":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "10");
                customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "11":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "11");
                customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "12":
               SharedPreferencesUtils.setParam(SleepHistoryActivity.this, "w30s_Sleep_Stuta", "12");
                customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_selete);
                break;
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customPopuWindow.dismiss();
            strlyChage();
            String currentDate2 = W30BasicUtils.getCurrentDate2();//2017-12:15
            switch (v.getId()) {
                case R.id.one_motoh:
                    customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-01-05");
                    break;
                case R.id.two_motoh:
                    customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-02-05");
                    break;
                case R.id.three_motoh:
                    customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-03-05");
                    break;
                case R.id.four_motoh:
                    customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-04-05");
                    break;
                case R.id.five_motoh:
                    customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-05-05");
                    break;
                case R.id.six_motoh:
                    customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-06-05");
                    break;
                case R.id.senve_motoh:
                    customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-07-05");
                    break;
                case R.id.eight_motoh:
                    customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-08-05");
                    break;
                case R.id.niece_motoh:
                    customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-09-05");
                    break;
                case R.id.ten_motoh:
                    customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-10-05");
                    break;
                case R.id.ten_one_motoh:
                    customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-11-05");
                    break;
                case R.id.ten_two_motoh:
                    customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_selete);
                    getSleepData(currentDate2.substring(0, 4) + "-12-05");
                    break;
            }
        }
    };

    public void strlyChage() {
        customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_unselete);
        customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_unselete);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customPopuWindow = new HistoryCustomPopuWindow(SleepHistoryActivity.this, listener);
        String currentDate2 = W30BasicUtils.getCurrentDate2();
        getSleepData(currentDate2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (customPopuWindow != null && customPopuWindow.isShowing()) {
            customPopuWindow.dismiss();
        }
    }

    /**
     * 获取睡眠数据
     *
     * @param timeDatas
     */
    private void getSleepData(String timeDatas) {

//        Log.d("---timeData----", timeDatas);
        if (newsSleepBeanList != null) {
            newsSleepBeanList.clear();
        }
        if (NewnewsSleepBeanList != null) {
            NewnewsSleepBeanList.clear();
        }
        if (sleepDataAdapter != null) {
            sleepDataAdapter.notifyDataSetChanged();
        }
        boolean isToMoth = false;
        if ((WatchUtils.getCurrentDate().substring(0, 7)).equals(timeDatas.substring(0, 7))) {
            isToMoth = true;
        }
        if (!(WatchUtils.getCurrentDate().substring(5, 7)).equals(timeDatas.substring(5, 7))
                && Integer.valueOf((WatchUtils.getCurrentDate().substring(5, 7))) < Integer.valueOf(timeDatas.substring(5, 7))) {
            return;
        }


        Date dateString = W30BasicUtils.stringToDate(timeDatas, "yyyy-MM-dd");
        //根据提供的年月日获取该月份的第一天
        String supportBeginDayofMonth = W30BasicUtils.getSupportBeginDayofMonth(dateString);
        //根据提供的年月获取该月份的最后一天
        String supportEndDayofMonth = W30BasicUtils.getSupportEndDayofMonth(dateString);

        String timetodateS = W30BasicUtils.timesW30s(supportBeginDayofMonth);
        String timetodateE = W30BasicUtils.timesW30s(supportEndDayofMonth);

        Date dateStart = W30BasicUtils.stringToDate(timetodateS, "yyyy-MM-dd HH:mm:ss");
        Date dateEnd = W30BasicUtils.stringToDate(timetodateE, "yyyy-MM-dd HH:mm:ss");
        Date dateDay = W30BasicUtils.stringToDate(W30BasicUtils.getCurrentDate2(), "yyyy-MM-dd");
//        Log.d("----timeData----", WatchUtils.getCurrentDate().substring(0, 7));

//        String s = W30BasicUtils.dateToString(dateStart, "yyyy-MM-dd");
        String s = W30BasicUtils.dateToString(dateStart, "yyyy/MM/dd");
        String e = W30BasicUtils.dateToString(dateEnd, "yyyy-MM-dd");
        String n = W30BasicUtils.dateToString(dateDay, "yyyy-MM-dd");

//        //tring类型转换为date类型
//        Date dateString = W30BasicUtils.stringToDate(timeDatas, "yyyy-MM-dd");
//        //根据提供的年月日获取该月份的第一天
//        String supportBeginDayofMonth = W30BasicUtils.getSupportBeginDayofMonth(dateString);
//        //根据提供的年月获取该月份的最后一天
//        String supportEndDayofMonth = W30BasicUtils.getSupportEndDayofMonth(dateString);
//
//        String timetodateS = W30BasicUtils.timesW30s(supportBeginDayofMonth);
//        String timetodateE = W30BasicUtils.timesW30s(supportEndDayofMonth);
//
//        Date dateStart = W30BasicUtils.stringToDate(timetodateS, "yyyy-MM-dd HH:mm:ss");
//        Date dateEnd = W30BasicUtils.stringToDate(timetodateE, "yyyy-MM-dd HH:mm:ss");
//        Date dateDay = W30BasicUtils.stringToDate(W30BasicUtils.getCurrentDate2(), "yyyy-MM-dd");
//        Log.d(TAG, "=================第一天=" + dateStart + "----最后一天==" + dateEnd);
        //获取两个日期之间的间隔天数
        int dayNumber = W30BasicUtils.getGapCount(dateStart, dateEnd);

        int dayNumberS = W30BasicUtils.getGapCount(dateStart, dateDay);
//        Log.d(TAG, "========距离多少==========" + dayNumber + "");
        String endtime = W30BasicUtils.dateToString(dateEnd, "yyyy-MM-dd");

        String sleepUrl = URLs.HTTPs + "/sleep/getSleepByTime";
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", SharedPreferencesUtils.readObject(SleepHistoryActivity.this, "userId"));
            sleepJson.put("deviceCode", (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC));
//            if (isToMoth) {
//                sleepJson.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumberS))));
//            } else {
//                sleepJson.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumber))));
//            }
//            if (isToMoth) {
//                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
//                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                sleepJson.put("endDate", nextDay.substring(0,10));
////                sleepJson.put("endDate", WatchUtils.getCurrentDate());
//            } else {
//                sleepJson.put("endDate", endtime);
//            }

            sleepJson.put("startDate", s);
            if (isToMoth) {
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                sleepJson.put("endDate", nextDay.substring(0, 10));
            } else {
                sleepJson.put("endDate", e);
            }

            Log.d("-------获取参数S=",sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(1, sleepUrl, SleepHistoryActivity.this, sleepJson.toString(), 0);
        }


//
//
//        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
//            @Override
//            public void onNext(String result) {
//                W30BasicUtils.e(TAG, "----睡眠返回----" + result);
//
//            }
//        };
//        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, SleepHistoryActivity.this);
//        OkHttpObservable.getInstance().getData(commonSubscriber, sleepUrl, sleepJson.toString());

    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }


    /**
     * 去除空数据
     *
     * @param list
     * @return
     */
    public List<SleepBeanNew.SleepDataBean> m2(List<SleepBeanNew.SleepDataBean> list) {
        Iterator<SleepBeanNew.SleepDataBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            SleepBeanNew.SleepDataBean integer = iterator.next();

            int deepSleep = integer.getDeepSleep();//深
            int shallowSleep = integer.getShallowSleep();//浅
            int sleepLen = integer.getSleepLen();//醒
            if (deepSleep <= 0 && shallowSleep <= 0) {
                //list.remove(temp);// 出现java.util.ConcurrentModificationException
                iterator.remove();// 推荐使用
            }
        }
        return list;
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        //关闭加载
        closeLoadingDialog();
        if (stepOrSleepList != null)
            stepOrSleepList.setVisibility(View.GONE);
        if (imageDataType != null)
            imageDataType.setVisibility(View.VISIBLE);
        //判断返回是否为空
        if (object != null || !WatchUtils.isEmpty(object.toString())) {
            SleepBeanNew sleepBeanNew = new Gson().fromJson(object.toString(), SleepBeanNew.class);
            if (sleepBeanNew != null) {
                String resultCode = sleepBeanNew.getResultCode();
                if (!WatchUtils.isEmpty(resultCode) && resultCode.equals("001")) {
                    List<SleepBeanNew.SleepDataBean> sleepData = sleepBeanNew.getSleepData();
                    if (m2(sleepData) != null && !m2(sleepData).isEmpty()) {
                        if (stepOrSleepList != null)
                            stepOrSleepList.setVisibility(View.VISIBLE);
                        if (imageDataType != null)
                            imageDataType.setVisibility(View.GONE);
                        sleepDataAdapter = new SleepDataAdapter(SleepHistoryActivity.this, m2(sleepData));
                        stepOrSleepList.setAdapter(sleepDataAdapter);
                        sleepDataAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
        }


//        closeLoadingDialog();
//        if (stepOrSleepList != null)
//            stepOrSleepList.setVisibility(View.GONE);
//        if (imageDataType != null)
//            imageDataType.setVisibility(View.VISIBLE);
//        if (object != null) {
//            try {

//                Log.d("-------------------ss",object.toString());
//                JSONObject sleepJson = new JSONObject(object.toString());
//                Log.d("-------------------ss1",sleepJson.toString());
//                if (sleepJson.getString("resultCode").equals("001")) {
//                    String sleepData = sleepJson.getString("sleepData");
//                    if (sleepData != null && !sleepData.equals("[]")) {
//                        newsSleepBeanList = new Gson().fromJson(sleepData, new TypeToken<List<NewsSleepBean>>() {
//                        }.getType());
//
//                        Log.d("-----------", newsSleepBeanList.size() + "==\n" + newsSleepBeanList.toString());
//                        if (newsSleepBeanList != null && newsSleepBeanList.size() > 0) {
//
////                            if (stepOrSleepList != null)
////                                stepOrSleepList.setVisibility(View.VISIBLE);
////                            if (imageDataType != null)
////                                imageDataType.setVisibility(View.GONE);
//                            for (int i = 0; i < newsSleepBeanList.size(); i++) {
////                                if (newsSleepBeanList.get(i).getShallowSleep() > 0 ||
////                                        newsSleepBeanList.get(i).getSleepLen() > 0 ||
////                                        newsSleepBeanList.get(i).getDeepSleep() > 0) {
////                                    NewnewsSleepBeanList.add(newsSleepBeanList.get(i));
////                                }
//                                NewnewsSleepBeanList.add(newsSleepBeanList.get(i));
//                            }
////                            if (NewnewsSleepBeanList == null || NewnewsSleepBeanList.isEmpty()) {
////                                if (imageDataType != null)
////                                    imageDataType.setVisibility(View.VISIBLE);
////                                if (stepOrSleepList != null)
////                                    stepOrSleepList.setVisibility(View.GONE);
////                                return;
////                            }
//
//                            sleepDataAdapter = new SleepDataAdapter(SleepHistoryActivity.this, newsSleepBeanList);
//                            stepOrSleepList.setAdapter(sleepDataAdapter);
//                            sleepDataAdapter.notifyDataSetChanged();
//                        }
//
//                    } else {
////                        imageDataType.setVisibility(View.VISIBLE);
////                        stepOrSleepList.setVisibility(View.GONE);
//                    }
//                } else {
////                    imageDataType.setVisibility(View.VISIBLE);
////                    stepOrSleepList.setVisibility(View.GONE);
//                    Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


//        } else {
//            imageDataType.setVisibility(View.VISIBLE);
//            stepOrSleepList.setVisibility(View.GONE);
//        Toast.makeText(SleepHistoryActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        e.getMessage();
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    public class SleepDataAdapter extends BaseAdapter {
        private Context mContext;
        List<SleepBeanNew.SleepDataBean> sleepData;
        private LayoutInflater layoutInflater;

        public SleepDataAdapter(Context mContext, List<SleepBeanNew.SleepDataBean> sleepBeanList) {
            this.mContext = mContext;
            this.sleepData = sleepBeanList;
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return sleepData.size();
        }

        @Override
        public Object getItem(int position) {
            return sleepData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.w30s_sleep_data_item, parent, false);
                holder = new ViewHolder();
                holder.textSleepData = (TextView) convertView.findViewById(R.id.text_sleep_data);
                holder.textSleepDeep = (TextView) convertView.findViewById(R.id.text_sleep_deep);
                holder.textSleepLight = (TextView) convertView.findViewById(R.id.text_sleep_light);
                holder.textSleepSober = (TextView) convertView.findViewById(R.id.text_sleep_sober);
                holder.textSleepTimeCount = (TextView) convertView.findViewById(R.id.text_sleep_time_count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int size = sleepData.size();
            String rtc = sleepData.get(position).getRtc();
            int deepSleep = sleepData.get(position).getDeepSleep();
            int shallowSleep = sleepData.get(position).getShallowSleep();
            int sleepLen = sleepData.get(position).getSleepLen();
            int soberLen = sleepData.get(position).getSoberLen();
            String weekCount = sleepData.get(position).getWeekCount();
//            Log.d("--->" + this.getClass(), "总共长度：" + size + "时间：" + rtc + "深睡：" + deepSleep + "浅睡：" + shallowSleep + "清醒：" + soberLen + "====" + sleepLen + "====" + weekCount);
            String substring = rtc.substring(0, 10);//2017-11-21
            holder.textSleepData.setText(substring);
            holder.textSleepDeep.setText(deepSleep + "min");
            holder.textSleepLight.setText(shallowSleep + "min");
            holder.textSleepSober.setText(soberLen + "min");
            holder.textSleepTimeCount.setText((deepSleep + shallowSleep) + "min");
            return convertView;
        }

        class ViewHolder {
            TextView textSleepData, textSleepDeep, textSleepLight, textSleepSober, textSleepTimeCount;
        }
    }

}
