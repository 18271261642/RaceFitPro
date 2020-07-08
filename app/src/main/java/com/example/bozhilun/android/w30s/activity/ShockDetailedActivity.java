package com.example.bozhilun.android.w30s.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;

import java.util.ArrayList;
import java.util.Calendar;

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

public class ShockDetailedActivity extends WatchBaseActivity {

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
        setContentView(R.layout.activity_shock_detailed);
        ButterKnife.bind(this);


        getIntents();
    }


    private void getIntents() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        switch (type) {
            case "sedentary":
                barTitles.setText(getResources().getString(R.string.Sedentaryreminder));
                initShockTimeList(type);
                String intervalTime_sedentary = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "IntervalTime_sedentary", "1H");
                textShockTime.setText(intervalTime_sedentary);
                String starTime_sedentary = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "starTime_sedentary", "06:00");
                String endTime_sedentary = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "endTime_sedentary", "22:00");
                textStartShockTime.setText(starTime_sedentary);
                textStopShockTime.setText(endTime_sedentary);
                break;
            case "dring":
                barTitles.setText(getResources().getString(R.string.string_water_clock));
                initShockTimeList(type);
                String intervalTime_dring = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "IntervalTime_dring", "1H");
                textShockTime.setText(intervalTime_dring);
                String starTime_dring = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "starTime_dring", "06:00");
                String endTime_dring = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "endTime_dring", "22:00");
                textStartShockTime.setText(starTime_dring);
                textStopShockTime.setText(endTime_dring);
                break;
            case "medicine":
                barTitles.setText(getResources().getString(R.string.string_drug_reminding));
                initShockTimeList(type);
                String intervalTime_medicine = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "IntervalTime_medicine", "4H");
                textShockTime.setText(intervalTime_medicine);
                String starTime_medicine = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "starTime_medicine", "06:00");
                String endTime_medicine = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "endTime_medicine", "22:00");
                textStartShockTime.setText(starTime_medicine);
                textStopShockTime.setText(endTime_medicine);
                break;
            case "metting":
                barTitles.setText(getResources().getString(R.string.string_conference_reminding));
                tixingjiange.setVisibility(View.GONE);
                kaishijieshu.setVisibility(View.GONE);
                riqi.setVisibility(View.VISIBLE);
                shijian.setVisibility(View.VISIBLE);
                initShockTimeList(type);
                break;
        }
    }


    private void initShockTimeList(String type) {
        ShockTimeList = new ArrayList<>();
        switch (type) {
            case "sedentary":
                for (int i = 1; i < 5; i++) {
                    ShockTimeList.add(i + "H");
                }
                break;
            case "dring":
                for (int i = 1; i <= 6; i++) {
                    if (6 % i == 0 || 6 % i == 2 || 6 % i == 1) {
                        ShockTimeList.add(i + "H");
                    }
                }
                break;
            case "medicine":
                for (int i = 4; i <= 12; i++) {
                    if (12 % i == 0 || 12 % i == 4) {
                        ShockTimeList.add(i + "H");
                    }
                }
                break;
            case "metting":
//                Calendar c = Calendar.getInstance();
//                year = c.get(Calendar.YEAR);
//                month = c.get(Calendar.MONTH);
//                day = c.get(Calendar.DAY_OF_MONTH);

//                String m = "";
//                String d = "";
//                if ((month + 1) <= 9) {
//                    m = "0" + (month + 1);
//                } else {
//                    m = String.valueOf((month + 1));
//                }
//                if (day <= 9) {
//                    d = "0" + day;
//                } else {
//                    d = String.valueOf(day);
//                }

                String dateStr2 = W30BasicUtils.getDateStr2(W30BasicUtils.getCurrentDate2(), 1);
                String datas = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "IntervalTime_metting_data", dateStr2);
                String times = (String) SharedPreferencesUtils.getParam(ShockDetailedActivity.this, "IntervalTime_metting_time", W30BasicUtils.getCurrentDate3().substring(0, W30BasicUtils.getCurrentDate3().length() - 3));
                textDataTime.setText(datas);
//                hour = c.get(Calendar.HOUR_OF_DAY);
//                minute = c.get(Calendar.MINUTE);
//                String h = "";
//                String mm = "";
//                if (hour <= 9) {
//                    h = "0" + hour;
//                } else {
//                    h = String.valueOf(hour);
//                }
//                if (minute <= 9) {
//                    mm = "0" + minute;
//                } else {
//                    mm = String.valueOf(minute);
//                }
                textTimersTime.setText(times);
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
                    case "medicine":
                        intent.putExtra("ShockTime", textShockTime.getText().toString().trim());
                        intent.putExtra("StartShockTime", textStartShockTime.getText().toString().trim());
                        intent.putExtra("StopShockTime", textStopShockTime.getText().toString().trim());
                        setResult(3, intent);
                        break;
                    case "metting":
                        intent.putExtra("DataShockTime", textDataTime.getText().toString().trim());
                        intent.putExtra("TimeShockTime", textTimersTime.getText().toString().trim());
                        setResult(4, intent);
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
                        SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "IntervalTime_metting_data", year + "-" + m + "-" + d);
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
                        SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "IntervalTime_metting_time", h + ":" + m);
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
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "starTime_sedentary", h + ":" + m);
                                break;
                            case "dring":
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "starTime_dring", h + ":" + m);
                                break;
                            case "medicine":
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "starTime_medicine", h + ":" + m);
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
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "endTime_sedentary", h + ":" + m);
                                break;
                            case "dring":
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "endTime_dring", h + ":" + m);
                                break;
                            case "medicine":
                                SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "endTime_medicine", h + ":" + m);
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
        ProfessionPick stepsnumber = new ProfessionPick.Builder(this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                Log.d("=========", profession);
                textShockTime.setText(profession);
                switch (type) {
                    case "sedentary":
                        SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "IntervalTime_sedentary", profession);
                        break;
                    case "dring":
                        SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "IntervalTime_dring", profession);
                        break;
                    case "medicine":
                        SharedPreferencesUtils.setParam(ShockDetailedActivity.this, "IntervalTime_medicine", profession);
                        break;
                }
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#222222")) //color of cancel button
                .colorConfirm(getResources().getColor(R.color.new_colorAccent))//color of confirm button
                .setProvinceList(ShockTimeList) //min year in loop
                .dateChose("1H") // date chose when init popwindow
                .build();
        stepsnumber.showPopWin(this);
    }
}

