package com.example.bozhilun.android.siswatch;

/**
 * Created by Administrator on 2017/9/8.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bleus.ShowSetAlarmResultListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑H8闹钟页面
 */
public class WatchEditAlarmActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "WatchEditAlarmActivity";

    //时间选择器
    @BindView(R.id.watch_alarmTimePicker)
    TimePicker watchAlarmTimePicker;
    @BindView(R.id.watch_chooseDateRel)
    RelativeLayout watchChooseDateRel;

    PopupWindow popupWindow;
    int monday = 0;
    int tues = 0;
    int wednes = 0;
    int thurs = 0;
    int fri = 0;
    int sat = 0;
    int week = 0;

    List<Integer> integerList = new ArrayList<>();
    @BindView(R.id.watch_edit_topCancleImg)
    ImageView watchEditTopCancleImg;
    @BindView(R.id.watch_edit_topTitleTv)
    TextView watchEditTopTitleTv;
    @BindView(R.id.watch_edit_topSureImg)
    ImageView watchEditTopSureImg;

    Map<String, Integer> maps = new HashMap<>();
    @BindView(R.id.watch_editRepeatSwit)
    SwitchCompat watchEditRepeatSwit;


    int repeat = 0; //重复开关值 0,1 0为关1为开

    String alarmTag = "";
    String alarmTimes;
    String alarmRepeat;
    int weekNum;

    @BindView(R.id.watch_CB1)
    CheckBox watchCB1;
    @BindView(R.id.watch_CB2)
    CheckBox watchCB2;
    @BindView(R.id.watch_CB3)
    CheckBox watchCB3;
    @BindView(R.id.watch_CB4)
    CheckBox watchCB4;
    @BindView(R.id.watch_CB5)
    CheckBox watchCB5;
    @BindView(R.id.watch_CB6)
    CheckBox watchCB6;
    @BindView(R.id.watch_CB7)
    CheckBox watchCB7;

    int checkedState = 0;
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_watchalarm);
        ButterKnife.bind(this);

        initViews();

        alarmTag = getIntent().getStringExtra("alarmTag");
        alarmTimes = getIntent().getStringExtra("times");
        alarmRepeat = getIntent().getStringExtra("wekrepeat");
        weekNum = getIntent().getIntExtra("weekrepeat",0);
        Log.e("AAA", "-------alarmTimes-" + alarmTimes+"--"+alarmTag+"--weekNum--"+weekNum);
        if(!WatchUtils.isEmpty(alarmTag) && !WatchUtils.isEmpty(alarmTimes) &&! WatchUtils.isEmpty(weekNum+"")){
            setVerticalView(alarmTag,alarmTimes,alarmRepeat);
        }

    }

    //设置页面信息
    private void setVerticalView(String alarmTag, String alarmTimes, String alarmRepeat) {
        if(weekNum  >127){
            watchEditRepeatSwit.setChecked(true);
            repeat = 1;
        }else{
            watchEditRepeatSwit.setChecked(false);
            repeat = 0;
        }
            if ((weekNum & weekArray[0]) == 1) {   //周日
                watchCB7.setChecked(true);
                week = 1;
                maps.put("week", week);
            }
            if ((weekNum & weekArray[1]) == 2) { //周一
                watchCB1.setChecked(true);
                monday = 2;
                maps.put("monday", monday);
                checkedState = 1;
            }
            if ((weekNum & weekArray[2]) == 4) { //周二
                watchCB2.setChecked(true);
                tues = 4;
                maps.put("tues", tues);
            }
            if ((weekNum & weekArray[3]) == 8) {  //周三
                watchCB3.setChecked(true);
                wednes = 8;
                maps.put("wednes", wednes);
            }
            if ((weekNum & weekArray[4]) == 16) {  //周四
                watchCB4.setChecked(true);
                thurs = 16;
                maps.put("thurs", thurs);
            }
            if ((weekNum & weekArray[5]) == 32) {  //周五
                watchCB5.setChecked(true);
                fri = 32;
                maps.put("fri", fri);
            }
            if ((weekNum & weekArray[6]) == 64) {  //周六
                watchCB6.setChecked(true);
                sat = 64;
                maps.put("sat", sat);
            }
    }

    /**
     * 判断系统版本
     */
    private void initViews() {
        if(!WatchUtils.isEmpty(getIntent().getStringExtra("times"))){
            //判断系统版本
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                watchAlarmTimePicker.setIs24HourView(true);     //24小时制
                watchAlarmTimePicker.setHour(Integer.valueOf(StringUtils.substringBefore(getIntent().getStringExtra("times"), ":")));
                watchAlarmTimePicker.setMinute(Integer.valueOf(StringUtils.substringAfter(getIntent().getStringExtra("times"), ":")));
            }else{
                watchAlarmTimePicker.setIs24HourView(true);     //24小时制
                watchAlarmTimePicker.setCurrentHour(Integer.valueOf(StringUtils.substringBefore(getIntent().getStringExtra("times"), ":")));
                watchAlarmTimePicker.setCurrentMinute(Integer.valueOf(StringUtils.substringAfter(getIntent().getStringExtra("times"), ":")));
            }
        }

        watchEditRepeatSwit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    repeat = 1;
                } else {
                    repeat = 0;
                }
            }
        });

        watchCB1.setOnCheckedChangeListener(this);
        watchCB2.setOnCheckedChangeListener(this);
        watchCB3.setOnCheckedChangeListener(this);
        watchCB4.setOnCheckedChangeListener(this);
        watchCB5.setOnCheckedChangeListener(this);
        watchCB6.setOnCheckedChangeListener(this);
        watchCB7.setOnCheckedChangeListener(this);

    }


    private String getListData(List<Integer> integerList) {
        int sum = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < integerList.size(); i++) {
            sb.append(integerList.get(i));
            sb.append(",");
            int value = integerList.get(i);
            sum = sum | value;

        }
        Log.e(TAG, "---sum--" + sum);
        if(repeat == 1){
            sum = sum+128;
        }
        Log.e(TAG, "---sum-22-" + sum);
        return String.valueOf(sum);

    }

    private String testMaps(Map<String, Integer> maps) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Integer>> entis = maps.entrySet();
        for (Map.Entry<String, Integer> enty : entis) {
            sb.append(enty.getValue());
            sb.append("-");
            integerList.add(enty.getValue());
        }
        return sb.toString();
    }


    @OnClick({R.id.watch_edit_topCancleImg, R.id.watch_edit_topSureImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_edit_topCancleImg:  //取消
                finish();
                break;
            case R.id.watch_edit_topSureImg:    //确认
                showLoadingDialog("loading...");
                Log.e(TAG, "-----msps--" + maps.toString() + "----" + testMaps(maps));
                getListData(integerList);   //获取sum值
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){ //>5.0
                    if (alarmTag.equals("alarm1")) {
                        setAlarmData(0x01,1,getListData(integerList),watchAlarmTimePicker.getHour(),
                                watchAlarmTimePicker.getMinute());


                    } else if (alarmTag.equals("alarm2")) {
                        setAlarmData(0x02,1,getListData(integerList),watchAlarmTimePicker.getHour(),
                                watchAlarmTimePicker.getMinute());

                    } else if (alarmTag.equals("alarm3")) {
                        setAlarmData(0x03,1,getListData(integerList),watchAlarmTimePicker.getHour(),
                                watchAlarmTimePicker.getMinute());
                    } else {
                        return;
                    }
                }else{
                    if (alarmTag.equals("alarm1")) {
                        setAlarmData(0x01,1,getListData(integerList),
                                watchAlarmTimePicker.getCurrentHour(),watchAlarmTimePicker.getCurrentMinute());

                    } else if (alarmTag.equals("alarm2")) {
                        setAlarmData(0x02,1,getListData(integerList),watchAlarmTimePicker.getCurrentHour(),
                                watchAlarmTimePicker.getCurrentMinute());
                    } else if (alarmTag.equals("alarm3")) {
                        setAlarmData(0x03,1,getListData(integerList),watchAlarmTimePicker.getCurrentHour(),
                                watchAlarmTimePicker.getCurrentMinute());

                    } else {
                        return;
                    }
                }

                break;
        }
    }

    private void setAlarmData(int code,int repeat, String sum, int hour, int minute) {
        switch (code){
            case 0x01:  //第一个闹钟
                MyApp.getInstance().h8BleManagerInstance().setH8Alarm(1,hour,minute,Integer.valueOf(sum),repeat,
                        showSetAlarmResultListener);
                break;
            case 0x02:  //第二个闹钟
                MyApp.getInstance().h8BleManagerInstance().setH8Alarm(2,hour,minute,Integer.valueOf(sum),
                        repeat,showSetAlarmResultListener);
                break;
            case 0x03:  //第三个闹钟
                MyApp.getInstance().h8BleManagerInstance().setH8Alarm(3,hour,minute,Integer.valueOf(sum),
                        repeat,showSetAlarmResultListener);
                break;
        }

    }

    private ShowSetAlarmResultListener showSetAlarmResultListener = new ShowSetAlarmResultListener() {
        @Override
        public void alarmResultData(boolean result) {
            if(result){
                closeLoadingDialog();
                Intent intent = new Intent();
                intent.putExtra("tag","settag");
                setResult(1001,intent);
                finish();
            }

        }
    };


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.watch_CB1:    //周一
                if (b) {
                    monday = 2;
                    maps.put("monday", monday);
                    checkedState = 1;
                } else {
                    monday = 0;
                    maps.remove("monday");
                    checkedState = 0;
                }
                break;
            case R.id.watch_CB2:    //周二
                if (b) {
                    tues = 4;
                    maps.put("tues", tues);
                } else {
                    tues = 0;
                    maps.remove("tues");
                }
                break;
            case R.id.watch_CB3:    //周三
                if (b) {
                    wednes = 8;
                    maps.put("wednes", wednes);
                } else {
                    wednes = 0;
                    maps.remove("wednes");
                }
                break;
            case R.id.watch_CB4:    //周四
                if (b) {
                    thurs = 16;
                    maps.put("thurs", thurs);
                } else {
                    thurs = 0;
                    maps.remove("thurs");
                }
                break;
            case R.id.watch_CB5:    //周五
                if (b) {
                    fri = 32;
                    maps.put("fri", fri);
                } else {
                    fri = 0;
                    maps.remove("fri");
                }
                break;
            case R.id.watch_CB6:    //周六
                if (b) {
                    sat = 64;
                    maps.put("sat", sat);

                } else {
                    sat = 0;
                    maps.remove("sat");
                }
                break;
            case R.id.watch_CB7:    //周日
                if (b) {
                    week = 1;
                    maps.put("week", week);
                } else {
                    week = 0;
                    maps.remove("week");
                }
                break;
        }
    }

}
