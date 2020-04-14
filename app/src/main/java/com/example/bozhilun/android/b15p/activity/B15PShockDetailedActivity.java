package com.example.bozhilun.android.b15p.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/13 09:59
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B15PShockDetailedActivity extends WatchBaseActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.text_shock_time)
    TextView textShockTime;
    @BindView(R.id.text_start_shock_time)
    TextView textStartShockTime;
    @BindView(R.id.text_stop_shock_time)
    TextView textStopShockTime;
    @BindView(R.id.tixingjiange)
    LinearLayout tixingjiange;
    @BindView(R.id.kaishijieshu)
    LinearLayout kaishijieshu;
    @BindView(R.id.riqi)
    LinearLayout riqi;
    @BindView(R.id.shijian)
    LinearLayout shijian;
    @BindView(R.id.text_data_time)
    TextView textDataTime;
    @BindView(R.id.text_timers_time)
    TextView textTimersTime;
    private String type;
    private ArrayList<String> ShockTimeList;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_shock_detailed);
        ButterKnife.bind(this);


        getIntents();
        initTimeData();
    }


    private void getIntents() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        switch (type) {
            case "sedentary":
                barTitles.setText(getResources().getString(R.string.Sedentaryreminder));
                String intervalTime_sedentary = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "IntervalTime_sedentary", "01:00");
                textShockTime.setText(intervalTime_sedentary);
                String starTime_sedentary = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "starTime_sedentary", "06:00");
                String endTime_sedentary = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "endTime_sedentary", "22:00");
                textStartShockTime.setText(starTime_sedentary);
                textStopShockTime.setText(endTime_sedentary);
                break;
            case "dring":
                barTitles.setText(getResources().getString(R.string.string_water_clock));
                String intervalTime_dring = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "IntervalTime_dring", "01:00");
                textShockTime.setText(intervalTime_dring);
                String starTime_dring = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "starTime_dring", "06:00");
                String endTime_dring = (String) SharedPreferencesUtils.getParam(B15PShockDetailedActivity.this, "endTime_dring", "22:00");
                textStartShockTime.setText(starTime_dring);
                textStopShockTime.setText(endTime_dring);
                break;
        }
    }


    @OnClick({R.id.image_back, R.id.btn_save, R.id.text_shock_time_line,
            R.id.text_data_time, R.id.text_timers_time, R.id.text_start_shock_time, R.id.text_stop_shock_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_save:
                Intent intent = new Intent();
                switch (type) {
                    case "sedentary":
                        intent.putExtra("ShockTime", textShockTime.getText().toString().trim());
                        intent.putExtra("StartShockTime", textStartShockTime.getText().toString().trim());
                        intent.putExtra("StopShockTime", textStopShockTime.getText().toString().trim());
                        setResult(1, intent);
                        break;
                    case "dring":
                        intent.putExtra("ShockTime", textShockTime.getText().toString().trim());
                        intent.putExtra("StartShockTime", textStartShockTime.getText().toString().trim());
                        intent.putExtra("StopShockTime", textStopShockTime.getText().toString().trim());
                        setResult(2, intent);
                        break;
                }
                finish();
                break;
            case R.id.text_shock_time_line:
                setSlecteShockTime();
                break;
            case R.id.text_data_time:
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                // 日期对话框
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                        Log.d("=====", "您选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                        String m = "";
                        String d = "";
                        if ((month + 1) <= 9) {
                            m = "0" + (month + 1);
                        } else {
                            m = String.valueOf((month + 1));
                        }
                        if (dayOfMonth <= 9) {
                            d = "0" + dayOfMonth;
                        } else {
                            d = String.valueOf(dayOfMonth);
                        }
                        SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "IntervalTime_metting_data", year + "-" + m + "-" + d);
                        textDataTime.setText(year + "-" + m + "-" + d);
                    }
                    // 传入年份,传入月份,传入天数
                }, year, month, day).show();
                break;
            case R.id.text_timers_time:
                Calendar c1 = Calendar.getInstance();
                hour = c1.get(Calendar.HOUR_OF_DAY);
                minute = c1.get(Calendar.MINUTE);
                //弹出时间对话框
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("=====", "时间：" + hourOfDay + "：" + minute);
                        String h = "";
                        String m = "";
                        if (hourOfDay <= 9) {
                            h = "0" + hourOfDay;
                        } else {
                            h = String.valueOf(hourOfDay);
                        }
                        if (minute <= 9) {
                            m = "0" + minute;
                        } else {
                            m = String.valueOf(minute);
                        }
                        textTimersTime.setText(h + ":" + m);
                        SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "IntervalTime_metting_time", h + ":" + m);
                    }
                }, hour, minute, true).show();
                break;
            case R.id.text_start_shock_time:
                timeSlect(0);
                break;
            case R.id.text_stop_shock_time:
                timeSlect(0);
                break;
        }
    }

    private void timeSlect(int a) {
        switch (a) {
            case 0:
                //弹出时间对话框
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("===aa==", "时间：" + hourOfDay + "：" + minute);
                        String h = "";
                        String m = "";
                        if (hourOfDay <= 9) {
                            h = "0" + hourOfDay;
                        } else {
                            h = String.valueOf(hourOfDay);
                        }
                        if (minute <= 9) {
                            m = "0" + minute;
                        } else {
                            m = String.valueOf(minute);
                        }
                        textStartShockTime.setText(h + ":" + m);
                        timeSlect(1);
                        switch (type) {
                            case "sedentary":
                                SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "starTime_sedentary", h + ":" + m);
                                break;
                            case "dring":
                                SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "starTime_dring", h + ":" + m);
                                break;
                        }
                    }
                }, 0, 0, true);
                timePickerDialog.setTitle(getResources().getString(R.string.start_time));
                timePickerDialog.show();
                break;
            case 1:
                //弹出时间对话框
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this,
                        AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("===dd==", "时间：" + hourOfDay + "：" + minute);
                        String h = "";
                        String m = "";
                        if (hourOfDay <= 9) {
                            h = "0" + hourOfDay;
                        } else {
                            h = String.valueOf(hourOfDay);
                        }
                        if (minute <= 9) {
                            m = "0" + minute;
                        } else {
                            m = String.valueOf(minute);
                        }
                        textStopShockTime.setText(h + ":" + m);

                        switch (type) {
                            case "sedentary":
                                SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "endTime_sedentary", h + ":" + m);
                                break;
                            case "dring":
                                SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "endTime_dring", h + ":" + m);
                                break;
                        }
                    }
                }, 0, 0, true);
                timePickerDialog1.setCanceledOnTouchOutside(false);
                timePickerDialog1.setTitle(getResources().getString(R.string.end_time));
                timePickerDialog1.show();
                break;
        }

    }

    private void setSlecteShockTime() {
        ProvincePick starPopWin = new ProvincePick.Builder(this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                String showTime = province + ":" + city;

                Log.d("=========", showTime);
                textShockTime.setText(showTime);
                switch (type) {
                    case "sedentary":
                        SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "IntervalTime_sedentary", showTime);
                        break;
                    case "dring":
                        SharedPreferencesUtils.setParam(B15PShockDetailedActivity.this, "IntervalTime_dring", showTime);
                        break;
                }
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of obtainRepeat button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(30) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of obtainRepeat button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(this);
    }

    /**
     * 小时数据源
     */
    private ArrayList<String> hourList;

    /**
     * 小时包含分钟的数据源
     */
    private HashMap<String, ArrayList<String>> minuteMapList;


    /**
     * 初始化时间相关
     */
    private void initTimeData() {
        hourList = new ArrayList<>();
        minuteMapList = new HashMap<>();
        //分钟数据源
        ArrayList<String> minuteList = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            String minute = "0" + i;
            if (i == 0) {
                minute = "00";
            } else {
                minute = "" + i * 10;
            }
            minuteList.add(minute);
        }

        //小时数据
        for (int i = 0; i < 4; i++) {
            hourList.add("0" + i);
            minuteMapList.put("0" + i, minuteList);
        }
    }
}

