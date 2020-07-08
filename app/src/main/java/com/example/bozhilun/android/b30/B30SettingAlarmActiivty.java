package com.example.bozhilun.android.b30;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.ImageAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.settings.Alarm2Setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by An on 2018/9/13.
 */

public class B30SettingAlarmActiivty extends WatchBaseActivity {

    private static final String TAG = "B15PSettingAlarmActiivty";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.iv_alarm_type)
    ImageView iv_alarm_type;
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
    /**
     * 闹钟图标数组,资源ID
     */
    private final int[] alarmTypeImageList = {R.drawable.selected1, R.drawable.selected2,
            R.drawable.selected3, R.drawable.selected4, R.drawable.selected5, R.drawable.selected6,
            R.drawable.selected7, R.drawable.selected8, R.drawable.selected9, R.drawable.selected10,
            R.drawable.selected11, R.drawable.selected12, R.drawable.selected13, R.drawable.selected14,
            R.drawable.selected15, R.drawable.selected16, R.drawable.selected17, R.drawable.selected18,
            R.drawable.selected19, R.drawable.selected20};
    /**
     * 小时数据源
     */
    private ArrayList<String> hourList;
    /**
     * 小时包含分钟的数据源
     */
    private HashMap<String, ArrayList<String>> minuteMapList;
    /**
     * 闹钟详情(新增时为空,编辑时有)
     */
    private Alarm2Setting mAlarm2Setting;
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



        String week = getWeek(getTimeNow());
       // if (week == null || week.length() != 7) week = "0000000";
        for (int i = 0; i < 7; i++) {
            checkedItems[i] = week.charAt(i) == '1';
        }
        tv_alarm_type.setText(WatchUtils.obtainAlarmDate(this, week));

        initTimeData();
        initParams();
        gv_alarm_type.setAdapter(new ImageAdapter(this));
        gv_alarm_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAlarm2Setting.setScene(i + 1);// 存一下新场景
                iv_alarm_type.setImageResource(alarmTypeImageList[i]);
            }
        });
    }

    /**
     * 初始化时间相关
     */
    private void initTimeData() {
        hourList = new ArrayList<>();
        minuteMapList = new HashMap<>();
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
    }

    /**
     * 初化参数
     */
    private void initParams() {
        String param = getIntent().getStringExtra("param");
        if (!TextUtils.isEmpty(param)) {
            Log.d("----zza---", param);
            try {
                mAlarm2Setting = new Gson().fromJson(param, Alarm2Setting.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        if (mAlarm2Setting != null) {// 编辑
            editAlarm = true;
            initEditData();
        } else {// 新增
            mAlarm2Setting = new Alarm2Setting();
            mAlarm2Setting.setAlarmHour(8);
            mAlarm2Setting.setAlarmMinute(0);
            mAlarm2Setting.setScene(1);
            mAlarm2Setting.setUnRepeatDate(getTimeNow());
            mAlarm2Setting.setRepeatStatus(getWeek((getTimeNow())));
            mAlarm2Setting.setOpen(true);
            commentB30TitleTv.setText(R.string.new_alarm_clock);
        }
    }


    String getTimeNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);// HH:mm:ss
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
        Log.e("---zza---", mAlarm2Setting.getUnRepeatDate());
        if (mAlarm2Setting.getUnRepeatDate().equals("0000-00-00")) {
            initRepeat(mAlarm2Setting.getRepeatStatus());
        } else {

            String week = getWeek(getTimeNow());
            if (week == null || week.length() != 7) week = "0000000";
            for (int i = 0; i < 7; i++) {
                checkedItems[i] = week.charAt(i) == '1';
            }
            tv_alarm_type.setText(WatchUtils.obtainAlarmDate(this, week));
//            mAlarm2Setting.setUnRepeatDate(getTimeNow());
//            Log.d("---zza--a-", mAlarm2Setting.getUnRepeatDate());
        }
        iv_alarm_type.setImageResource(alarmTypeImageList[mAlarm2Setting.getScene()>0?mAlarm2Setting.getScene() - 1:0]);
    }

    /**
     * 初始化重复状态
     */
    private void initRepeat(String repeat) {
        if (repeat == null || repeat.length() != 7) repeat = "0000000";
        for (int i = 0; i < 7; i++) {
            checkedItems[i] = repeat.charAt(i) == '1';
        }
        tv_alarm_type.setText(WatchUtils.obtainAlarmDate(this, repeat));
    }

    @OnClick({R.id.commentB30BackImg, R.id.tv_alarm_time_layout, R.id.tv_alarm_type_layout, R.id.b30AlarmSaveBtn})
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
        }
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
        builder.setMultiChoiceItems(R.array.WeekItems, checkedItems, new DialogInterface
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
        Log.e(TAG,"--------result="+result);
        mAlarm2Setting.setRepeatStatus(result);
        tv_alarm_type.setText(WatchUtils.obtainAlarmDate(this, result));
    }

    /**
     * 保存闹钟(新增|编辑)
     */
    private void saveAlarm() {
        if (TextUtils.isEmpty(mAlarm2Setting.getRepeatStatus()) || mAlarm2Setting.getRepeatStatus().equals("0000000")) {
            mAlarm2Setting.setRepeatStatus("0000000");
            //mAlarm2Setting.setUnRepeatDate(WatchUtils.getCurrentDate());
            initRepeat(mAlarm2Setting.getRepeatStatus());
        }
        Log.d("---zza--", mAlarm2Setting.toString());


        if (editAlarm) {
            MyApp.getInstance().getVpOperateManager().modifyAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    Log.d("-----zza-修改闹钟-", alarmData2.toString());
                    closeLoadingDialog();
                    B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                    finish();
                }
            }, mAlarm2Setting);
        } else {
            MyApp.getInstance().getVpOperateManager().addAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    closeLoadingDialog();
                    Log.d("-----zza-新建闹钟-", alarmData2.toString());
                    B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                    finish();
                }
            }, mAlarm2Setting);
        }



//        MyApp.getInstance().getVpOperateManager().readAlarm2(new IBleWriteResponse() {
//            @Override
//            public void onResponse(int i) {
//
//            }
//        }, new IAlarm2DataListListener() {
//            @Override
//            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
//                List<Alarm2Setting> alarm2SettingList = alarmData2.getAlarm2SettingList();
//                Log.d("---zza--", alarm2SettingList.size()+"");
//
//                readAllAlarms(alarm2SettingList);
//
//            }
//        });
//
//        List<Alarm2Setting> listAlarm = MyApp.getInstance().getVpOperateManager().getAlarm2List();
//        readAllAlarms(listAlarm);



    }



    private void readAllAlarms(List<Alarm2Setting> alarm2SettingList){
        if (alarm2SettingList.size()>0){
            for (int i = 0; i < alarm2SettingList.size(); i++) {
                Alarm2Setting alarm2Setting = alarm2SettingList.get(i);

                //已经有的
                String repeatStatus = alarm2Setting.getRepeatStatus();
                if (repeatStatus == null || repeatStatus.length() != 7)
                    repeatStatus = "0000000";
                for (int j = 0; j < 7; j++) {
                    checkedItems[j] = repeatStatus.charAt(j) == '1';
                }
                String s = WatchUtils.obtainAlarmDate(MyApp.getContext(), repeatStatus);

                //新的
                String repeatStatus1 = mAlarm2Setting.getRepeatStatus();
                for (int j = 0; j < 7; j++) {
                    checkedItems[j] = repeatStatus1.charAt(j) == '1';
                }
                String s1 = WatchUtils.obtainAlarmDate(MyApp.getContext(), repeatStatus1);
                int alarmHour = mAlarm2Setting.getAlarmHour();
                int alarmMinute = mAlarm2Setting.getAlarmMinute();
                if (s.equals(s1) && alarmHour == alarm2Setting.getAlarmHour() && alarmMinute == alarm2Setting.getAlarmMinute()) {
                    closeLoadingDialog();
                    ToastUtil.showShort(B30SettingAlarmActiivty.this, getResources().getString(R.string.string_alarm_repeated));
                    return;
                }


                if (editAlarm) {
                    MyApp.getInstance().getVpOperateManager().modifyAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                        @Override
                        public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                            Log.d("-----zza-修改闹钟-", alarmData2.toString());
                            closeLoadingDialog();
                            B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                            finish();
                        }
                    }, mAlarm2Setting);
                } else {
                    MyApp.getInstance().getVpOperateManager().addAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                        @Override
                        public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                            closeLoadingDialog();
                            Log.d("-----zza-新建闹钟-", alarmData2.toString());
                            B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                            finish();
                        }
                    }, mAlarm2Setting);
                }

            }
        }else {

            if (editAlarm) {
                MyApp.getInstance().getVpOperateManager().modifyAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                    @Override
                    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                        Log.d("-----zza-修改闹钟-", alarmData2.toString());
                        closeLoadingDialog();
                        B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                        finish();
                    }
                }, mAlarm2Setting);
            } else {
                MyApp.getInstance().getVpOperateManager().addAlarm2(iBleWriteResponse, new IAlarm2DataListListener() {
                    @Override
                    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                        closeLoadingDialog();
                        Log.d("-----zza-新建闹钟-", alarmData2.toString());
                        B30SettingAlarmActiivty.this.setResult(Constant.RESULT_CODE);
                        finish();
                    }
                }, mAlarm2Setting);
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
        String Week = "0000000";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //1111111
        int wek = c.get(Calendar.DAY_OF_WEEK)-1;
        if(wek < 0){
            wek = 0;
        }
        if (wek == 0) {
//            Week += "星期日";
            Week = "1000000";
        }
        if (wek == 1) {
//            Week += "星期一";
            Week = "0000001";
        }
        if (wek == 2) {
//            Week += "星期二";
            Week = "0000010";
        }
        if (wek == 3) {
//            Week += "星期三";
            Week = "0000100";
        }
        if (wek == 4) {
//            Week += "星期四";
            Week = "0001000";
        }
        if (wek == 5) {
//            Week += "星期五";
            Week = "0010000";
        }
        if (wek == 6) {
//            Week += "星期六";
            Week = "0100000";
        }
        return Week;
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

}
