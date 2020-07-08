package com.example.bozhilun.android.w30s.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.SportBeanNew;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.ScreenUtils;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

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

public class StepHistoryDataActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "StepHistoryDataActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.step_or_sleep_list)
    ListView stepOrSleepList;
    @BindView(R.id.image_data_type)
    ImageView imageDataType;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //步数的相关
    List<WatchDataDatyBean> stepList, stepListNew;
    @BindView(R.id.bar_mores)
    TextView barMores;
    private HistoryCustomPopuWindow customPopuWindow;//popWinow
    private StepDataAdapter stepDataAdapter;
    private RequestPressent requestPressent;
    String timeDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_sleep_or_step_history_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.sports_history));
//        barMores.setVisibility(View.GONE);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        timeDay = W30BasicUtils.getCurrentDate2().substring(5, 7);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStepData(WatchUtils.getCurrentDate());
        customPopuWindow = new HistoryCustomPopuWindow(StepHistoryDataActivity.this, listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (customPopuWindow != null && customPopuWindow.isShowing()) {
            customPopuWindow.dismiss();
        }
    }

    @OnClick({R.id.image_back, R.id.bar_mores})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                if (customPopuWindow == null) {
                    customPopuWindow = new HistoryCustomPopuWindow(StepHistoryDataActivity.this, listener);
                }
                setBack();//yyyy-MM-dd
                int screenWidth = ScreenUtils.getScreenWidth(StepHistoryDataActivity.this);
                int screenHeight = ScreenUtils.getScreenHeight(StepHistoryDataActivity.this);
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
//        String currentDate2 = W30BasicUtils.getCurrentDate2();
//        String substring = (String) SharedPreferencesUtils.getParam(StepHistoryDataActivity.this, "step_stuta", currentDate2.substring(5, 7));
        Log.d("------", timeDay);
        strlyChage();
        switch (timeDay) {
            case "01":
                timeDay = "01";
                customPopuWindow.getOneM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "02":
                timeDay = "02";
                customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "03":
                timeDay = "03";
                customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "04":
                timeDay = "04";
                customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "05":
                timeDay = "05";
                SharedPreferencesUtils.setParam(StepHistoryDataActivity.this, "step_stuta", "05");
                customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "06":
                timeDay = "06";
                customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "07":
                timeDay = "07";
                customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "08":
                timeDay = "08";
                customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "09":
                timeDay = "09";
                customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "10":
                timeDay = "10";
                customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "11":
                timeDay = "11";
                customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                break;
            case "12":
                timeDay = "12";
                SharedPreferencesUtils.setParam(StepHistoryDataActivity.this, "step_stuta", "12");
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
                    Log.d(TAG, "点击啦一月==" + currentDate2.substring(0, 4) + "-01-05");
                    getStepData(currentDate2.substring(0, 4) + "-01-05");
                    break;
                case R.id.two_motoh:
                    customPopuWindow.getTwoM().setBackgroundResource(R.drawable.text_history_selete);
                    Log.d(TAG, "点击啦二月==" + currentDate2.substring(0, 4) + "-02-05");
                    getStepData(currentDate2.substring(0, 4) + "-02-05");
                    break;
                case R.id.three_motoh:
                    customPopuWindow.getThreeM().setBackgroundResource(R.drawable.text_history_selete);
                    Log.d(TAG, "点击啦三月==" + currentDate2.substring(0, 4) + "-03-05");
                    getStepData(currentDate2.substring(0, 4) + "-03-05");
                    break;
                case R.id.four_motoh:
                    customPopuWindow.getFourM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-04-05");
                    break;
                case R.id.five_motoh:
                    customPopuWindow.getFiveM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-05-05");
                    break;
                case R.id.six_motoh:
                    customPopuWindow.getSevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-06-05");
                    break;
                case R.id.senve_motoh:
                    customPopuWindow.getSixM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-07-05");
                    break;
                case R.id.eight_motoh:
                    customPopuWindow.getEightM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-08-05");
                    break;
                case R.id.niece_motoh:
                    customPopuWindow.getNineM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-09-05");
                    break;
                case R.id.ten_motoh:
                    customPopuWindow.getTenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-10-05");
                    break;
                case R.id.ten_one_motoh:
                    customPopuWindow.getElevenM().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-11-05");
                    break;
                case R.id.ten_two_motoh:
                    customPopuWindow.getTwelve().setBackgroundResource(R.drawable.text_history_selete);
                    getStepData(currentDate2.substring(0, 4) + "-12-05");
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


    /**
     * 获取运动数据
     *
     * @param timeDatas
     */
    private void getStepData(String timeDatas) {
        if (stepList != null) {
            stepList.clear();
        }
        if (stepListNew != null) {
            stepListNew.clear();
        }
        if (stepDataAdapter != null) {
            stepDataAdapter.notifyDataSetChanged();
        }
        boolean isToMoth = false;//表识是否点击率本月的数据，默认不是
        if ((WatchUtils.getCurrentDate().substring(0, 7)).equals(timeDatas.substring(0, 7))) {
            isToMoth = true;//表识是点击率本月的数据
        }
        //选择了不是本月大妈是大于本月的时间时
        if (!(WatchUtils.getCurrentDate().substring(5, 7)).equals(timeDatas.substring(5, 7))
                && Integer.valueOf((WatchUtils.getCurrentDate().substring(5, 7))) < Integer.valueOf(timeDatas.substring(5, 7))) {
            if (imageDataType != null) imageDataType.setVisibility(View.VISIBLE);
            if (stepOrSleepList != null) stepOrSleepList.setVisibility(View.GONE);
            return;
        }
        //string类型转换为date类型
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

//        Date dateStart = W30BasicUtils.longToDate((long) Long.valueOf(supportBeginDayofMonth), "yyyy-MM-dd");
//        Date dateEnd = W30BasicUtils.longToDate((long) Long.valueOf(supportEndDayofMonth), "yyyy-MM-dd");

//        String s = W30BasicUtils.dateToString(dateStart, "yyyy-MM-dd");
        String s = W30BasicUtils.dateToString(dateStart, "yyyy/MM/dd");
        String e = W30BasicUtils.dateToString(dateEnd, "yyyy-MM-dd");
        String n = W30BasicUtils.dateToString(dateDay, "yyyy-MM-dd");

//        Log.d(TAG, "======AAA3===========月的第一天=" + s + "----月的最后一天==" + e + "=====日期" +  W30BasicUtils.dateToString(dateDay, "yyyy-MM-dd"));
//        //获取两个日期之间的间隔天数
//        int dayNumber = W30BasicUtils.getGapCount(dateStart, dateEnd);
//        int dayNumberS = W30BasicUtils.getGapCount(dateStart, dateDay);
//        Log.d(TAG, "=======今年" + timeDatas.substring(0, 7) + "月---一共" + dayNumber + "天");

        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(StepHistoryDataActivity.this, "userId"));
            jsonObect.put("deviceCode",  SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC));

//            if (isToMoth) {
//                jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumberS))));
//            } else {
//                jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), Math.abs(dayNumber))));
//            }
//            if (isToMoth) {
//                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
//                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
//                jsonObect.put("endDate", nextDay.substring(0, 10));
//            } else {
//                jsonObect.put("endDate", timetodateE.substring(0, 10));
//            }
            jsonObect.put("startDate", s);
            if (isToMoth) {
                Date dateBeforess = H9TimeUtil.getDateBefore(new Date(), 1);
                String nextDay = H9TimeUtil.getValidDateStr2(dateBeforess);
                jsonObect.put("endDate", nextDay.substring(0, 10));
            } else {
                jsonObect.put("endDate", e);
            }
            Log.d(TAG, "========请求字段==========" + jsonObect.toString() + "");

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(1, url, StepHistoryDataActivity.this, jsonObect.toString(), 0);
        }

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
    public List<SportBeanNew.DayBean> m2(List<SportBeanNew.DayBean> list) {
        Iterator<SportBeanNew.DayBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            SportBeanNew.DayBean integer = iterator.next();
            int stepNumber = integer.getStepNumber();//步数
            if (stepNumber <= 0) {
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
            SportBeanNew sportBeanNew = new Gson().fromJson(object.toString(), SportBeanNew.class);
            if (sportBeanNew != null) {
                String resultCode = sportBeanNew.getResultCode();
                if (!WatchUtils.isEmpty(resultCode) && resultCode.equals("001")) {
                    List<SportBeanNew.DayBean> sportpData = sportBeanNew.getDay();
                    if (m2(sportpData) != null && !m2(sportpData).isEmpty()) {
                        if (stepOrSleepList != null)
                            stepOrSleepList.setVisibility(View.VISIBLE);
                        if (imageDataType != null)
                            imageDataType.setVisibility(View.GONE);
                        stepDataAdapter = new StepDataAdapter(StepHistoryDataActivity.this, m2(sportpData));
                        stepOrSleepList.setAdapter(stepDataAdapter);
                        stepDataAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
        }
//
//        closeLoadingDialog();
//        imageDataType.setVisibility(View.VISIBLE);
//        stepOrSleepList.setVisibility(View.GONE);
//        if (object != null) {
//            try {
//                JSONObject jsonObject = new JSONObject(object.toString());
//                Log.d("----返回数据----", object.toString());
//                if (jsonObject.getString("resultCode").equals("001")) {
//                    String daydata = jsonObject.getString("day");
//                    if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
//                        stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
//                        }.getType());
//                        if (stepList == null || stepList.size() <= 0) {
//                            if (imageDataType != null) imageDataType.setVisibility(View.VISIBLE);
//                            if (stepOrSleepList != null) stepOrSleepList.setVisibility(View.GONE);
//                        } else {
//                            for (int i = 0; i < stepList.size(); i++) {
//                                if (stepList.get(i).getStepNumber() > 0) {
//                                    stepListNew.add(stepList.get(i));
//                                }
//                            }
//                            if (stepListNew == null || stepListNew.isEmpty()) {
//                                if (imageDataType != null)
//                                    imageDataType.setVisibility(View.VISIBLE);
//                                if (stepOrSleepList != null)
//                                    stepOrSleepList.setVisibility(View.GONE);
//                                return;
//                            }
//                            Collections.sort(stepListNew, new Comparator<WatchDataDatyBean>() {
//                                @Override
//                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
//                                    return o2.getRtc().compareTo(o1.getRtc());
//                                }
//                            });
//                            stepDataAdapter = new StepDataAdapter(StepHistoryDataActivity.this, stepListNew);
//                            if (imageDataType != null) imageDataType.setVisibility(View.GONE);
//                            if (stepOrSleepList != null) {
//                                stepOrSleepList.setVisibility(View.VISIBLE);
//                                stepOrSleepList.setAdapter(stepDataAdapter);
//                            }
//                        }
//
//                    } else {
//                        if (imageDataType != null) imageDataType.setVisibility(View.VISIBLE);
//                        if (stepOrSleepList != null) stepOrSleepList.setVisibility(View.GONE);
//                    }
//                } else {
//                    if (imageDataType != null) imageDataType.setVisibility(View.VISIBLE);
//                    if (stepOrSleepList != null) stepOrSleepList.setVisibility(View.GONE);
//                    Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (imageDataType != null) imageDataType.setVisibility(View.VISIBLE);
//            if (stepOrSleepList != null) stepOrSleepList.setVisibility(View.GONE);
//            Toast.makeText(StepHistoryDataActivity.this, getResources().getString(R.string.nodata), Toast.LENGTH_SHORT).show();
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


    public class StepDataAdapter extends BaseAdapter {
        private Context mContext;
        List<SportBeanNew.DayBean> sportpData ;
        private LayoutInflater layoutInflater;

        public StepDataAdapter(Context mContext, List<SportBeanNew.DayBean> stepDataList) {
            this.mContext = mContext;
            this.sportpData = stepDataList;
            layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return sportpData.size();
        }

        @Override
        public Object getItem(int position) {
            return sportpData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.w30s_step_data_item, parent, false);
                holder = new ViewHolder();
                holder.textStepData = (TextView) convertView.findViewById(R.id.text_step_data);
                holder.textStepMi = (TextView) convertView.findViewById(R.id.text_step_mi);
                holder.textStepKcl = (TextView) convertView.findViewById(R.id.text_step_kcl);
                holder.textStepNumber = (TextView) convertView.findViewById(R.id.text_step_number);
                holder.dataViews = convertView.findViewById(R.id.data_views);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (sportpData != null) {
                int size = sportpData.size();
                String rtc = sportpData.get(position).getRtc();
                int stepNumber = sportpData.get(position).getStepNumber();
                String calories = sportpData.get(position).getCalories();
                String distance = sportpData.get(position).getDistance();
//                Log.d("--->" + this.getClass(), "总共长度：" + size + "时间：" + rtc + "步数：" + stepNumber + "卡里路：" + calories + "距离：" + distance);
                String substring = rtc.substring(0, 10);//2017-11-21
                holder.textStepData.setText(substring);
                holder.textStepNumber.setText(stepNumber + "step");
                boolean w30sunit = (boolean)  SharedPreferencesUtils.getParam(StepHistoryDataActivity.this, "w30sunit", true);
                if (w30sunit) {
                    double setScale = (double) WatchUtils.div((double) Double.valueOf(distance.trim()), 1000, 1);
                    holder.textStepMi.setText(setScale + "km");
                } else {
                    int round = (int) Math.round(Double.valueOf(distance) * 3.28);
                    holder.textStepMi.setText(String.valueOf(WatchUtils.div(round, 1, 2)).split("[.]")[0] + " ft");
                }
                holder.textStepKcl.setText(calories + "kcl");
            }
            return convertView;
        }

        class ViewHolder {
            LinearLayout dataViews;
            TextView textStepData, textStepMi, textStepKcl, textStepNumber;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null) {
            requestPressent.detach();
        }
    }
}
