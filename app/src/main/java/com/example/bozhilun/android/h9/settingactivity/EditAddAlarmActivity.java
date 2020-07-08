package com.example.bozhilun.android.h9.settingactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.sdk.bluetooth.bean.RemindData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;
import com.sdk.bluetooth.protocol.command.expands.RemindCount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAddAlarmActivity extends WatchBaseActivity {

    private static final String TAG = "EditAddAlarmActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.timer_set)
    TimePicker timerSet;
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
    @BindView(R.id.radioGroup_time)
    RadioGroup radioGroupTime;
    @BindView(R.id.img_sleclte_type)
    ImageView imgSleclteType;

    private Intent intent = null;
    private int alarmCycles = 0;//闹钟周期周期
    int alarmHour = 12;//闹钟时 默认 12
    int alarmMinute = 30;//闹钟分  默认30
    int alarmType = 0;//闹钟类型，默认 吃饭

    /**
     * 编辑修改时，旧的数据
     */
    private int alarmCyclesOld = 64;//闹钟周期周期
    int alarmHourOld = 12;//闹钟时 默认 12
    int alarmMinuteOld = 30;//闹钟分  默认30
    int alarmTypeOld = 0;//闹钟类型，默认 吃饭
    int alarmSwithOld = 0;//闹钟类型，默认 吃饭

    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_add_alarm_activity);
        ButterKnife.bind(this);
        intent = getIntent();

        timerSet.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);//禁止可输入切换
        //将时间选择器设置为24 小时制的
        timerSet.setIs24HourView(true);//是否显示24小时制？默认false
        getType();

        initView();
    }

    private void initView() {
        if (isZh(this)){
            checkboxDay.setTextSize(15f);
            checkboxOne.setTextSize(15f);
            checkboxTwo.setTextSize(15f);
            checkboxThree.setTextSize(15f);
            checkboxFour.setTextSize(15f);
            checkboxFive.setTextSize(15f);
            checkboxSix.setTextSize(15f);
        }else {
            checkboxDay.setTextSize(10f);
            checkboxOne.setTextSize(10f);
            checkboxTwo.setTextSize(10f);
            checkboxThree.setTextSize(10f);
            checkboxFour.setTextSize(10f);
            checkboxFive.setTextSize(10f);
            checkboxSix.setTextSize(10f);
        }

        timerSet.setOnTimeChangedListener(new ChangeLister());

        //周期选择
        checkboxDay.setOnCheckedChangeListener(new CheckLister());
        checkboxOne.setOnCheckedChangeListener(new CheckLister());
        checkboxTwo.setOnCheckedChangeListener(new CheckLister());
        checkboxThree.setOnCheckedChangeListener(new CheckLister());
        checkboxFour.setOnCheckedChangeListener(new CheckLister());
        checkboxFive.setOnCheckedChangeListener(new CheckLister());
        checkboxSix.setOnCheckedChangeListener(new CheckLister());
        //类型选择
        radioGroupTime.setOnCheckedChangeListener(new RadioCheckeListenter());
    }



    public boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.trim().endsWith("zh")
                || language.trim().equals("zh-rCN"))
            return true;
        else
            return false;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getInstance()).clearCommandBlockingDeque();
    }

    private void getType() {

        if (intent == null) intent = getIntent();
        String alarmTypes = intent.getStringExtra("alarmType");
        if (WatchUtils.isEmpty(alarmTypes)) finish();
        if (alarmTypes.equals("NEW")) {//新建闹钟
            barTitles.setText(getResources().getString(R.string.new_alarm_clock));

            //默认选中今天
//            String week = getWeek(getTimeNow());
//            setWeek(null, week);
        } else {//修改编辑闹钟
            barTitles.setText(getResources().getString(R.string.edit_alarm_clock));
            String remindDataJson = getIntent().getStringExtra("remindData");
            if (!WatchUtils.isEmpty(remindDataJson)) {
                Log.d(TAG, "---点击过来：" + new Gson().toJson(remindDataJson));
                RemindData remindData = new Gson().fromJson(remindDataJson, RemindData.class);
                //时间显示
                setTimePick(remindData);
                //周期显示
                setWeek(remindData, null);
                //类型显示
                settype(remindData);
                alarmSwithOld = remindData.remind_set_ok;//闹钟开关
            }

        }
    }


    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "1111111";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //1111111
        int wek = c.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d(TAG, "======" + wek);
        if (wek == 1) {
//            Week += "星期日";
            Week = "0100000";
        }
        if (wek == 2) {
//            Week += "星期一";
            Week = "0000001";
        }
        if (wek == 3) {
//            Week += "星期二";
            Week = "0000010";
        }
        if (wek == 4) {
//            Week += "星期三";
            Week = "0000100";
        }
        if (wek == 5) {
//            Week += "星期四";
            Week = "0001000";
        }
        if (wek == 6) {
//            Week += "星期五";
            Week = "0010000";
        }
        if (wek == 7) {
//            Week += "星期六";
            Week = "1000000";
        }
        return Week;
    }


    /**
     * 类型显示
     *
     * @param remindData
     */
    private void settype(RemindData remindData) {
        int edittype = remindData.remind_type;
        alarmType = edittype;
        alarmTypeOld = edittype;
        switch (edittype) {
            case 0:
                imgSleclteType.setImageResource(R.mipmap.eat);
                radioGroupTime.check(R.id.radio_T1);
                break;
            case 1:
                imgSleclteType.setImageResource(R.mipmap.medicine);
                radioGroupTime.check(R.id.radio_T2);
                break;
            case 2:
                imgSleclteType.setImageResource(R.mipmap.dring);
                radioGroupTime.check(R.id.radio_T3);
                break;
            case 3:
                imgSleclteType.setImageResource(R.mipmap.sp);
                radioGroupTime.check(R.id.radio_T4);
                break;
            case 4:
                imgSleclteType.setImageResource(R.mipmap.awakes);
                radioGroupTime.check(R.id.radio_T5);
                break;
            case 5:
                imgSleclteType.setImageResource(R.mipmap.spr);
                radioGroupTime.check(R.id.radio_T6);
                break;
            case 6:
                imgSleclteType.setImageResource(R.mipmap.metting);
                radioGroupTime.check(R.id.radio_T7);
                break;
            case 7:
                imgSleclteType.setImageResource(R.mipmap.custom);
                radioGroupTime.check(R.id.radio_T8);
                break;
        }
    }

    /**
     * 周期显示
     *
     * @param remindData
     */
    private void setWeek(RemindData remindData, String remind_weeks) {
        int week = 0;
        if (remindData == null) {
            Log.d(TAG, "============周期---" + remind_weeks);
            week = Integer.parseInt(B18iUtils.toD(remind_weeks, 2));
        } else {
            Log.d(TAG, "============周期---" + remindData.remind_week);
            week = Integer.parseInt(B18iUtils.toD(remindData.remind_week, 2));
        }
        Log.d(TAG, "============周期-A--" + week);
        //周期
        if ((week & weekArray[0]) == 1) {   //周一
            alarmCycles += 1;
            alarmCyclesOld += 1;
            checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxOne.setChecked(true);
        }
        if ((week & weekArray[1]) == 2) { //周二
            alarmCycles += 2;
            alarmCyclesOld += 2;
            checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxTwo.setChecked(true);
        }
        if ((week & weekArray[2]) == 4) { //周三
            alarmCycles += 4;
            alarmCyclesOld += 4;
            checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxThree.setChecked(true);
        }
        if ((week & weekArray[3]) == 8) {  //周四
            alarmCycles += 8;
            alarmCyclesOld += 8;
            checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxFour.setChecked(true);
        }
        if ((week & weekArray[4]) == 16) {  //周五
            alarmCycles += 16;
            alarmCyclesOld += 16;
            checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxFive.setChecked(true);
        }
        if ((week & weekArray[5]) == 32) {  //周六
            alarmCycles += 32;
            alarmCyclesOld += 32;
            checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxSix.setChecked(true);
        }
        if ((week & weekArray[6]) == 64) {  //周日
            alarmCycles += 64;
            alarmCyclesOld += 64;
            checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
            checkboxDay.setChecked(true);
        }
    }

    /**
     * 显示传过来的时间
     *
     * @param remindData
     */
    private void setTimePick(RemindData remindData) {
        int editHour = remindData.remind_time_hours;
        int editMinutes = remindData.remind_time_minutes;
        alarmHour = editHour;
        alarmMinute = editMinutes;
        alarmHourOld = editHour;
        alarmMinuteOld = editMinutes;
        //时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timerSet.setHour(editHour);
            timerSet.setMinute(editMinutes);
        } else {
            timerSet.setCurrentHour(editHour);
            timerSet.setCurrentMinute(editMinutes);
        }
    }

    @OnClick({R.id.image_back, R.id.image_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back://返回
                finish();
                break;
            case R.id.image_right://保存
                // callback
                // 提醒操作(0x00:新增 0x01:修改 0x02:删除 0x03:全部删除)
                // 新增或单条删除 提醒类型: 吃饭(00)/吃药(01)/喝水(02)/睡觉(03)/ 清醒(04)/ 运动(05)/会议(06)/自定义(07)
                // 新增或单条删除 提醒时
                // 新增或单条删除 提醒分
                // 新增或单条删除 提醒周期 二进制 01111111  分别代表(星期日~星期一)  比如只要星期三传入0x04,星期一传入0x01
                // 新增或单条删除 提醒开光状态 0关闭 1打开

                // 注意:同一个时间不能有重复的提醒，否则返回失败(提醒是按时间区分)
                // 例如 增加一个 星期三 2.10分 喝水的提醒  状态为关闭


                Log.d(TAG, "设置的闹钟----类型：" + alarmType + "  时间" + alarmHour + ":" + alarmMinute + "  周期：" + alarmCycles);
                if (intent == null) intent = getIntent();
                String alarmTypes = intent.getStringExtra("alarmType");
                showLoadingDialog(getResources().getString(R.string.dlog));


                Log.d(TAG, "星期值=====" + alarmCycles);
                if (alarmTypes.equals("NEW")) {//新建闹钟
                    if (alarmCycles == 0) alarmCycles = 127;
                    AppsBluetoothManager.getInstance(EditAddAlarmActivity.this)
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    (byte) 0x00, (byte) alarmType, (byte) alarmHour, (byte) alarmMinute, (byte) alarmCycles, (byte) 0x01));
                } else {
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    11,
                                    (byte) 0x01,//修改
                                    (byte) alarmTypeOld,//类型：。。。"吃饭", "吃药", "喝水。。。
                                    (byte) alarmHourOld,//时
                                    (byte) alarmMinuteOld,//分
                                    (byte) alarmCyclesOld,//周期
                                    (byte) alarmSwithOld,
                                    new byte[]{}, (byte) alarmType, (byte) alarmHour, (byte) alarmMinute, (byte) alarmCycles, (byte) 0x01));//0关、1开
                }
                break;
        }
    }


    String getTimeNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Log.d("---zza---", "Date获取当前日期时间" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }


    /**
     * 闹钟操作返回
     */
    BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof RemindCount) {
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置

                } else if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                    int remindCount = GlobalVarManager.getInstance().getRemindCount();
                    Log.d(TAG, "读取的闹钟的长度为：" + remindCount);
                }
            } else if (baseCommand instanceof RemindSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置
                    Log.d(TAG, "闹钟设置成功");
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                } else if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                    //查询到了闹钟的详细数据
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };


    /**
     * 时间选择器
     */
    private class ChangeLister implements TimePicker.OnTimeChangedListener {
        @Override
        public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
            alarmHour = hour;
            alarmMinute = minute;
        }
    }


    /**
     * 周期选择器
     * 提醒周期 二进制 01111111  分别代表(星期日~星期一)  比如只要星期三传入0x04,星期一传入0x01
     */
    private class CheckLister implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            Log.d(TAG, "---周期选择器" + compoundButton.isPressed());
            switch (compoundButton.getId()) {
                case R.id.checkbox_one:
                    if (isChecked) {
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles += 1;
                    } else {
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles -= 1;
                    }
                    break;
                case R.id.checkbox_two:
                    if (isChecked) {
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles += 2;
                    } else {
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles -= 2;
                    }
                    break;
                case R.id.checkbox_three:
                    if (isChecked) {
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles += 4;
                    } else {
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles -= 4;
                    }
                    break;
                case R.id.checkbox_four:
                    if (isChecked) {
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles += 8;
                    } else {
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles -= 8;
                    }
                    break;
                case R.id.checkbox_five:
                    if (isChecked) {
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles += 16;
                    } else {
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles -= 16;
                    }

                    break;
                case R.id.checkbox_six:
                    if (isChecked) {
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles += 32;
                    } else {
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles -= 32;
                    }
                    break;

                case R.id.checkbox_day:
                    if (isChecked) {
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_unselect);
                        alarmCycles += 64;
                    } else {
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
                        alarmCycles -= 64;
                    }
                    break;
            }
        }
    }


    /**
     * 类型选择器    分了两行
     * <p>
     * 吃饭(00)/吃药(01)/喝水(02)/睡觉(03)/ 清醒(04)/ 运动(05)/会议(06)/自定义(07)
     */
    private class RadioCheckeListenter implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {
            Log.d(TAG, "---周期选择器" + radioGroup.isPressed());
            switch (radioGroup.getId()) {
                case R.id.radioGroup_time:
                    switch (isChecked) {
                        case R.id.radio_T1:
                            alarmType = 0;
                            imgSleclteType.setImageResource(R.mipmap.eat);
                            break;
                        case R.id.radio_T2:
                            alarmType = 1;
                            imgSleclteType.setImageResource(R.mipmap.medicine);
                            break;
                        case R.id.radio_T3:
                            alarmType = 2;
                            imgSleclteType.setImageResource(R.mipmap.dring);
                            break;
                        case R.id.radio_T4:
                            alarmType = 3;
                            imgSleclteType.setImageResource(R.mipmap.sp);
                            break;
                        case R.id.radio_T5:
                            alarmType = 4;
                            imgSleclteType.setImageResource(R.mipmap.awakes);
                            break;
                        case R.id.radio_T6:
                            alarmType = 5;
                            imgSleclteType.setImageResource(R.mipmap.spr);
                            break;
                        case R.id.radio_T7:
                            alarmType = 6;
                            imgSleclteType.setImageResource(R.mipmap.metting);
                            break;
                        case R.id.radio_T8:
                            alarmType = 7;
                            imgSleclteType.setImageResource(R.mipmap.custom);
                            break;
                    }
                    break;
            }

        }
    }
}
