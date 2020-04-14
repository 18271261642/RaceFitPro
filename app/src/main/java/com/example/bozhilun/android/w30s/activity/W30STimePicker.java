package com.example.bozhilun.android.w30s.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 设置闹钟界面
 * @author： 安
 * @crateTime: 2017/9/7 16:38
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30STimePicker extends WatchBaseActivity {

    @BindView(R.id.timer_set)
    TimePicker timerSet;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.checkbox_day)
    CheckBox checkboxDay;
    @BindView(R.id.checkbox_one)
    CheckBox checkboxOne;
    @BindView(R.id.checkbox_two)
    CheckBox checkboxTwo;
    @BindView(R.id.checkbox_three)
    CheckBox checkboxThree;
    @BindView(R.id.checkbox_four)
    CheckBox checkboxFour;
    @BindView(R.id.checkbox_five)
    CheckBox checkboxFive;
    @BindView(R.id.checkbox_six)
    CheckBox checkboxSix;
    private int H, M;
    private boolean TYPE = true;
    private int NUMBER = 0;


    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};
    Intent intent;

    String ids, type, alarmHour, alarmMine, datas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_time_picker_layout);
        ButterKnife.bind(this);
        timerSet.setIs24HourView(true);//是否显示24小时制？默认false
        intent = getIntent();
        type = intent.getStringExtra("type");
        setTitles(type);
        setCheckBoxClick();
    }

    private void setCheckBoxClick() {
        timerSet.setOnTimeChangedListener(new ChangeLister());
        checkboxDay.setOnCheckedChangeListener(new CheckLister());
        checkboxOne.setOnCheckedChangeListener(new CheckLister());
        checkboxTwo.setOnCheckedChangeListener(new CheckLister());
        checkboxThree.setOnCheckedChangeListener(new CheckLister());
        checkboxFour.setOnCheckedChangeListener(new CheckLister());
        checkboxFive.setOnCheckedChangeListener(new CheckLister());
        checkboxSix.setOnCheckedChangeListener(new CheckLister());
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerSet.setIs24HourView(true);//是否显示24小时制？默认false
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("------", NUMBER + "");
    }

    /**
     * 判断设置title
     */
    private void setTitles(String type) {

        setBack();
        if (!WatchUtils.isEmpty(type)) {
            if (type.equals("new")) {
                TYPE = true;
                barTitles.setText(getResources().getString(R.string.new_alarm_clock));
            } else {
                TYPE = false;
                barTitles.setText(getResources().getString(R.string.edit_alarm_clock));
                // remindData = (W30S_AlarmInfo) intent.getExtras().getSerializable("remindData");
                ids = intent.getStringExtra("ids");
                datas = intent.getStringExtra("datas");
                alarmHour = intent.getStringExtra("hour");
                alarmMine = intent.getStringExtra("min");
                initData(getIntent().getIntExtra("alarmWeek", 0));
            }
        }

    }

    void setBack() {
        checkboxDay.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxOne.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxThree.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxFour.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxFive.setBackgroundResource(R.drawable.b18i_text_unselect);
        checkboxSix.setBackgroundResource(R.drawable.b18i_text_unselect);
    }


    private void initData(int week) {
        //时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timerSet.setHour(Integer.valueOf(alarmHour));
            timerSet.setMinute(Integer.valueOf(alarmMine));
        } else {
            timerSet.setCurrentMinute(Integer.valueOf(alarmMine));
            timerSet.setCurrentHour(Integer.valueOf(alarmHour));
        }
        int alarmtData = Integer.valueOf(datas);
        String alarmtDataString = Integer.toBinaryString(alarmtData);
        String substring = alarmtDataString.substring(1, alarmtDataString.length());
        //二进制转十进制
//        int week = Integer.valueOf(substring, 2);

        //周期
        //int week = Integer.parseInt(B18iUtils.toD(remindData.remind_week, 2));
//        if ((week & weekArray[0]) == 1) {   //周日
//            NUMBER += 1;
//            checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxDay.setChecked(true);
//        }
//        if ((week & weekArray[1]) == 2) { //周一
//            NUMBER += 2;
//            checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxOne.setChecked(true);
//        }
//        if ((week & weekArray[2]) == 4) { //周二
//            NUMBER += 4;
//            checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxTwo.setChecked(true);
//        }
//        if ((week & weekArray[3]) == 8) {  //周三
//            NUMBER += 8;
//            checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxThree.setChecked(true);
//        }
//        if ((week & weekArray[4]) == 16) {  //周四
//            NUMBER += 16;
//            checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxFour.setChecked(true);
//        }
//        if ((week & weekArray[5]) == 32) {  //周五
//            NUMBER += 32;
//            checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxFive.setChecked(true);
//        }
//        if ((week & weekArray[6]) == 64) {  //周六
//            NUMBER += 64;
//            checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
//            checkboxSix.setChecked(true);
//        }



        if ((week & weekArray[0]) == 1) { //周一
            NUMBER += 1;
            checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxOne.setChecked(true);
        }
        if ((week & weekArray[1]) == 2) { //周二
            NUMBER += 2;
            checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxTwo.setChecked(true);
        }
        if ((week & weekArray[2]) == 4) {  //周三
            NUMBER += 4;
            checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxThree.setChecked(true);
        }
        if ((week & weekArray[3]) == 8) {  //周四
            NUMBER += 8;
            checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxFour.setChecked(true);
        }
        if ((week & weekArray[4]) == 16) {  //周五
            NUMBER += 16;
            checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxFive.setChecked(true);
        }
        if ((week & weekArray[5]) == 32) {  //周六
            NUMBER += 32;
            checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxSix.setChecked(true);
        }
        if ((week & weekArray[6]) == 64) {   //周日
            NUMBER += 64;
            checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxDay.setChecked(true);
        }
    }


    @OnClick({R.id.image_back, R.id.image_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_right://
                if (MyCommandManager.DEVICENAME == null) {
                    Toast.makeText(W30STimePicker.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT);
                    NUMBER = 0;
                } else {
                    if (NUMBER <= 0) {
                        Log.e("B18ITimePicker", "未选择周期默认设置全周");
                        Toast.makeText(this, getResources().getString(R.string.select_repeat_week), Toast.LENGTH_SHORT).show();
                        return;
                        //NUMBER = 127;
                    }
                    Intent intent = new Intent();
                    setHN(intent);
                    if (TYPE) {
                        setResult(1, intent);
                    } else {
                        setResult(2, intent);
                    }
                }
                finish();
                break;
            case R.id.image_back:
                NUMBER = 0;
                finish();
                break;
        }
    }


    /**
     * 传值
     *
     * @param intent
     */
    private void setHN(Intent intent) {
        if (H <= 0 && M <= 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                H = timerSet.getHour();
                M = timerSet.getMinute();
            } else {
                H = timerSet.getCurrentHour();
                M = timerSet.getCurrentMinute();
            }
        }
        if (H >= 0) {
            intent.putExtra("h", H);
        }
        if (M >= 0) {
            intent.putExtra("m", M);
        }
        intent.putExtra("c", NUMBER);
        if (!type.equals("new")) {
            intent.putExtra("ids", ids);
        }
    }

    private class ChangeLister implements TimePicker.OnTimeChangedListener {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            Log.e("--------tttt", hourOfDay + "===" + minute +
                    "=========" + view.getCurrentHour() + "=====" + view.getCurrentMinute() + "=====" + view.getBaseline());
            H = hourOfDay;
            M = minute;
        }
    }


    private class CheckLister implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e("---------", buttonView.getId() + "====" + isChecked);
//            switch (buttonView.getId()) {
//                case R.id.checkbox_day:
//                    if (isChecked) {
//                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
//                        NUMBER += 1;
//                    } else {
//                        NUMBER -= 1;
//                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_one:
//                    if (isChecked) {
//                        NUMBER += 2;
//                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
//                    } else {
//                        NUMBER -= 2;
//                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_two:
//                    if (isChecked) {
//                        NUMBER += 4;
//                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
//                    } else {
//                        NUMBER -= 4;
//                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_three:
//                    if (isChecked) {
//                        NUMBER += 8;
//                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
//                    } else {
//                        NUMBER -= 8;
//                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_four:
//                    if (isChecked) {
//                        NUMBER += 16;
//                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
//                    } else {
//                        NUMBER -= 16;
//                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_five:
//                    if (isChecked) {
//                        NUMBER += 32;
//                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
//                    } else {
//                        NUMBER -= 32;
//                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//                case R.id.checkbox_six:
//                    if (isChecked) {
//                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
//                        NUMBER += 64;
//                    } else {
//                        NUMBER -= 64;
//                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_unselect);
//                    }
//                    break;
//            }
            switch (buttonView.getId()) {
                case R.id.checkbox_one:
                    if (isChecked) {
                        NUMBER += 1;
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 1;
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_two:
                    if (isChecked) {
                        NUMBER += 2;
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 2;
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_three:
                    if (isChecked) {
                        NUMBER += 4;
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 4;
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_four:
                    if (isChecked) {
                        NUMBER += 8;
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 8;
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_five:
                    if (isChecked) {
                        NUMBER += 16;
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 16;
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_six:
                    if (isChecked) {
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
                        NUMBER += 32;
                    } else {
                        NUMBER -= 32;
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_day:
                    if (isChecked) {
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
                        NUMBER += 64;
                    } else {
                        NUMBER -= 64;
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
            }
        }
    }
}
