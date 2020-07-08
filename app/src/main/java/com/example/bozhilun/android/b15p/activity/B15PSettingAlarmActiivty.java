package com.example.bozhilun.android.b15p.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PAlarmSetting;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tjdL4.tjdmain.contr.AlarmClock;
import com.tjdL4.tjdmain.contr.L4Command;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by An on 2018/9/13.
 */

public class B15PSettingAlarmActiivty extends WatchBaseActivity {

    private final String TAG = "B15PSettingAlarm";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    //    @BindView(R.id.iv_alarm_type)
//    ImageView iv_alarm_type;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.tv_alarm_time)
    TextView tv_alarm_time;
    @BindView(R.id.tv_alarm_type)
    TextView tv_alarm_type;
    @BindView(R.id.gv_alarm_type)
    GridView gv_alarm_type;
    @BindView(R.id.b30AlarmSaveBtn)
    Button b30AlarmSaveBtn;
    @BindView(R.id.alarm_type_line)
    LinearLayout alarm_type_line;
    @BindView(R.id.tv_alarm_interval_layout)
    LinearLayout tv_alarm_interval_layout;
    @BindView(R.id.tv_alarm_interval)
    TextView tv_alarm_interval;//延时提醒

    /**
     * 小时数据源
     */
    private ArrayList<String> hourList;
    /**
     * 延时数据源
     */
    private ArrayList<String> delayedList;
    /**
     * 小时包含分钟的数据源
     */
    private HashMap<String, ArrayList<String>> minuteMapList;
    /**
     * 闹钟详情(新增时为空,编辑时有)
     */
    private B15PAlarmSetting mAlarm2Setting;
    /**
     * true_编辑闹钟 false_新增
     */
    private boolean editAlarm;
    /**
     * 保存选择的重复天数
     */
    private boolean[] checkedItems = {false, false, false, false, false, false, false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_setting_alarm);
        ButterKnife.bind(this);
        commentB30BackImg.setVisibility(View.VISIBLE);


        initTimeData();
        initParams();


        alarm_type_line.setVisibility(View.GONE);
        gv_alarm_type.setVisibility(View.GONE);
        tv_alarm_interval_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 设置被选中的周期
     *
     * @param week
     */
    void setCheckWeek(String week) {

        if (week == null || week.length() < 7) return;
        for (int i = 0; i < 7; i++) {
            checkedItems[i] = (week.charAt(i) == '1');
        }
//        Log.e(TAG, "  获取到的周期   " + week);
        if (tv_alarm_type != null)
            tv_alarm_type.setText(WatchUtils.obtainB15PAlarmDate(this, week));
//
        int weeks = IntWeek(week);
        if (mAlarm2Setting != null) {
            Log.e(TAG, "-----result=" + week + "    " + weeks);
            mAlarm2Setting.setWeek(weeks);
        }
    }

    /**
     * 初始化时间相关
     */
    private void initTimeData() {
        hourList = new ArrayList<>();
        minuteMapList = new HashMap<>();
        delayedList = new ArrayList<>();
        //分钟数据源
        ArrayList<String> minuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            String minute = "" + i;
            if (i < 10) minute = "0" + minute;
            minuteList.add(minute);
        }
        for (int i = 0; i <= 23; i++) {
            if (i < 10) {
                hourList.add("0" + i);
                minuteMapList.put("0" + i, minuteList);
            } else {
                hourList.add("" + i);
                minuteMapList.put("" + i, minuteList);
            }
        }

        for (int i = 0; i <= 3; i++) {
            delayedList.add((i * 10)  +" "+getResources().getString(R.string.minute));
        }
    }

    /**
     * 初化参数
     */
    private void initParams() {
        String param = getIntent().getStringExtra("param");
        if (!TextUtils.isEmpty(param)) {
            Log.d("----zza---", param);
            try {
                mAlarm2Setting = new Gson().fromJson(param, B15PAlarmSetting.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        if (mAlarm2Setting != null) {// 编辑
            editAlarm = true;
            initEditData();
            Log.e(TAG, "========编辑闹钟" + mAlarm2Setting.getAlarmId());
        } else {// 新增
            editAlarm = false;
            int alarmID = getIntent().getIntExtra("alarmID", 0);
            Log.e(TAG, "========新建闹钟" + alarmID);
            mAlarm2Setting = new B15PAlarmSetting();

            setCheckWeek(getWeek(getTimeNow()));
            mAlarm2Setting.setAlarmId(alarmID);
            mAlarm2Setting.setAlarmHour(8);
            mAlarm2Setting.setAlarmMinute(0);
            mAlarm2Setting.setInterval(0);
            mAlarm2Setting.setOpen(true);

            commentB30TitleTv.setText(R.string.new_alarm_clock);
        }


    }


    String getTimeNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Log.d("---zza---", "Date获取当前日期时间" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    /**
     * 初始化编辑相关
     */
    private void initEditData() {
        commentB30TitleTv.setText(R.string.edit_alarm_clock);
        int hour = mAlarm2Setting.getAlarmHour();
        int minute = mAlarm2Setting.getAlarmMinute();
        String hourStr = hour < 10 ? "0" + hour : "" + hour;
        String minuteStr = minute < 10 ? "0" + minute : "" + minute;
        String showTime = hourStr + ":" + minuteStr;
        tv_alarm_time.setText(showTime);// 时间

        int week = mAlarm2Setting.getWeek();
        String newWeek = WatchUtils.obtainB15PAlarmDate(this, StringWeek(week));
        Log.e(TAG, week + "   " + StringWeek(week) + "   " + newWeek);
        int interval = mAlarm2Setting.getInterval();
        tv_alarm_interval.setText(interval + getResources().getString(R.string.minute));
        setCheckWeek(StringWeek(week));
    }

    /**
     * 值转周
     *
     * @param week
     * @return
     */
    public String StringWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
//        String[] sateString = {"1", "0", "0", "0", "0", "0", "0", "0"};//开关，周一  ~~  周日
        String[] sateString = {"0", "0", "0", "0", "0", "0", "0"};//周一  ~~  周日
        //weekDay=01100011,//开关，周一  ~~  周日
        //周期
        if ((week & weekArray[0]) == 1) { //周一
            sateString[0] = "1";
        }
        if ((week & weekArray[1]) == 2) { //周二
            sateString[1] = "1";
        }
        if ((week & weekArray[2]) == 4) {  //周三
            sateString[2] = "1";
        }
        if ((week & weekArray[3]) == 8) {  //周四
            sateString[3] = "1";
        }
        if ((week & weekArray[4]) == 16) {  //周五
            sateString[4] = "1";
        }
        if ((week & weekArray[5]) == 32) {  //周六
            sateString[5] = "1";
        }
        if ((week & weekArray[6]) == 64) {   //周日
            sateString[6] = "1";
        }
        String stringData = "";
        String stringDatas = Arrays.toString(sateString);
        //Log.d("-----<<<W30>>>", "===stringDatas===" + stringDatas);
        String stringDatas2 = stringDatas.substring(1, stringDatas.length() - 1);
        //Log.d("-----<<<W30>>>", "===stringDatas2===" + stringDatas2);
        String[] split = stringDatas2.trim().split("[,]");
        for (int i = 0; i < split.length; i++) {
            stringData += split[i].trim();
        }
        return stringData;
    }


    @OnClick({R.id.commentB30BackImg, R.id.tv_alarm_time_layout, R.id.tv_alarm_type_layout, R.id.b30AlarmSaveBtn, R.id.tv_alarm_interval_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:// 返回
                finish();
                break;
            case R.id.tv_alarm_time_layout:// 显示时间选择器
                chooseDate();
                break;
            case R.id.tv_alarm_type_layout:// 重复类型（星期....）
                showMultiAlertDialog();
                break;
            case R.id.b30AlarmSaveBtn:// 保存闹钟
                showLoadingDialog("Loading...");
                saveAlarm();
                break;
            case R.id.tv_alarm_interval_layout:
                showDelayedTime();//设置延时提醒
                break;
        }
    }


    /**
     * 设置延时
     */
    private void showDelayedTime() {

        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B15PSettingAlarmActiivty.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        try {
                            Log.e(TAG, "=====" + profession);
                            tv_alarm_interval.setText(profession);
                            //String substring = profession.substring(0, profession.length() - 2);
                            String substring = StringUtils.substringBefore(profession," ").trim();
                            Log.e(TAG, "====== " + substring);
                            mAlarm2Setting.setInterval(Integer.valueOf(substring));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(delayedList) //min year in loop
                .dateChose(delayedList.get(0)) // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B15PSettingAlarmActiivty.this);
    }

    /**
     * 闹钟时间选择
     */
    private void chooseDate() {
        ProvincePick starPopWin = new ProvincePick.Builder(this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                String showTime = province + ":" + city;
                tv_alarm_time.setText(showTime);
                mAlarm2Setting.setAlarmHour(Integer.valueOf(province));
                mAlarm2Setting.setAlarmMinute(Integer.valueOf(city));
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
     * 选择重复星期几闹钟
     */
    public void showMultiAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_repeat_week);
        builder.setMultiChoiceItems(R.array.B15PWeekItems, checkedItems, new DialogInterface
                .OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                obtainRepeat();
            }
        });
        builder.setNegativeButton(R.string.cancle, null);
        builder.show();
    }

    /**
     * 计算重复情况: 1100000_周日至周一
     */
    private void obtainRepeat() {
        StringBuilder builder = new StringBuilder();
        for (boolean item : checkedItems) {
            builder.append(item ? "1" : "0");
        }
        String result = builder.toString();
        int week = IntWeek(result);
        Log.e(TAG, "-----result=" + result + "    " + week);
        mAlarm2Setting.setWeek(week);
        tv_alarm_type.setText(WatchUtils.obtainB15PAlarmDate(this, result));
    }


    /**
     * 周转值
     *
     * @param week
     * @return
     */
    public int IntWeek(String week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
        int allWeek = 0;
        if (WatchUtils.isEmpty(week) || week.length() < 7) return 0;
        if (week.charAt(0) == '1') {
            allWeek += weekArray[0];
        }
        if (week.charAt(1) == '1') {
            allWeek += weekArray[1];
        }
        if (week.charAt(2) == '1') {
            allWeek += weekArray[2];
        }
        if (week.charAt(3) == '1') {
            allWeek += weekArray[3];
        }
        if (week.charAt(4) == '1') {
            allWeek += weekArray[4];
        }
        if (week.charAt(5) == '1') {
            allWeek += weekArray[5];
        }
        if (week.charAt(6) == '1') {
            allWeek += weekArray[6];
        }
        Log.e(TAG, "-----result=A" + allWeek);
//        for (int i = 0; i < 7; i++) {
//            Log.e(TAG, "-----result=A" + week.charAt(i));
//            if (week.charAt(i) == '1') {
//                allWeek += weekArray[i];
//            }
//        }
        return allWeek;
    }


    /**
     * 保存闹钟(新增|编辑)
     */
    private void saveAlarm() {
        if (mAlarm2Setting == null){
            closeLoadingDialog();
            ToastUtil.showShort(B15PSettingAlarmActiivty.this, "设置出错");
            finish();
            return;
        }
        //设置数据对象
        AlarmClock.AlarmClockData myAlarmClockData = new AlarmClock.AlarmClockData();
        myAlarmClockData.clockId_int = mAlarm2Setting.getAlarmId();    //clockId_int闹钟序号（id）
        myAlarmClockData.clock_switch = mAlarm2Setting.isOpen() ? 1 : 0;   //clock_switch  开关 0关闭1打开
        myAlarmClockData.interval = mAlarm2Setting.getInterval();        //interval  重复间隔
        if (mAlarm2Setting.getWeek() <= 0) {
            closeLoadingDialog();
            ToastUtil.showShort(B15PSettingAlarmActiivty.this, "请选择重复周期");
            return;
        }
        //周日  一 二 三 四 五 周六    选中 1 未选中0
        myAlarmClockData.weeks = byteWeek(mAlarm2Setting.getWeek());        //weeks 重复模式
        myAlarmClockData.hours = mAlarm2Setting.getAlarmHour();           //hour   时
        myAlarmClockData.minutes = mAlarm2Setting.getAlarmMinute();       //minute 分
        //设置
        String ret = L4Command.AlarmClockSet(myAlarmClockData);   /*ret  返回值类型在文档最下面*/
        Log.e(TAG, " 闹钟设置 " + ret);
        if (ret.equals("OK")) {
            closeLoadingDialog();
            Log.d("-----zza--", editAlarm ? "编辑闹钟" : "新建闹钟");
            B15PSettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
            finish();
        }
    }

    /**
     * 值转 数组数据
     *
     * @param week
     * @return
     */
    public byte[] byteWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
        byte[] sateString = {0, 0, 0, 0, 0, 0, 0, 0};//周一  ~~  周日
        //周期
        if ((week & weekArray[0]) == 1) { //周一
            sateString[0] = 1;
        }
        if ((week & weekArray[1]) == 2) { //周二
            sateString[1] = 1;
        }
        if ((week & weekArray[2]) == 4) {  //周三
            sateString[2] = 1;
        }
        if ((week & weekArray[3]) == 8) {  //周四
            sateString[3] = 1;
        }
        if ((week & weekArray[4]) == 16) {  //周五
            sateString[4] = 1;
        }
        if ((week & weekArray[5]) == 32) {  //周六
            sateString[5] = 1;
        }
        if ((week & weekArray[6]) == 64) {   //周日
            sateString[6] = 1;
        }
        return sateString;
    }


    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "0000000";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int wek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (wek < 0) {
            wek = 0;
        }
        if (wek == 0) {
//            Week += "星期日";
            Week = "1000000";
        }
        if (wek == 1) {
//            Week += "星期一";
            Week = "0100000";
        }
        if (wek == 2) {
//            Week += "星期二";
            Week = "0010000";
        }
        if (wek == 3) {
//            Week += "星期三";
            Week = "0000100";
        }
        if (wek == 4) {
//            Week += "星期四";
            Week = "0000100";
        }
        if (wek == 5) {
//            Week += "星期五";
            Week = "0000010";
        }
        if (wek == 6) {
//            Week += "星期六";
            Week = "0000001";
        }
        return Week;
    }

}
