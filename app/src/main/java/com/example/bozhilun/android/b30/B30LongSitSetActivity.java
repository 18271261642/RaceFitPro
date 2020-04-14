package com.example.bozhilun.android.b30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.enums.ELongSeatStatus;
import com.veepoo.protocol.model.settings.LongSeatSetting;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * 久坐提醒设置时间
 */
public class B30LongSitSetActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener{


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.showB30LongSitStartTv)
    TextView showB30LongSitStartTv;
    @BindView(R.id.showB30LongSitEndTv)
    TextView showB30LongSitEndTv;
    @BindView(R.id.showB30LongSitTv)
    TextView showB30LongSitTv;
    @BindView(R.id.longSitToggleBtn)
    ToggleButton longSitToggleBtn;


    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    ArrayList<String> longTimeLit;

    boolean isToggChecked = false;
    int hour;
    int min;
    int startHour;
    int startMin;

    ArrayList<String> longSitHourList;//久坐结束时间
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_long_sitset);
        ButterKnife.bind(this);

        initViews();
        initData();
        //读取久坐提醒
        readLongSitData();

    }

    private void readLongSitData() {



        //读取久坐提醒
        MyApp.getInstance().getVpOperateManager().readLongSeat(iBleWriteResponse, new ILongSeatDataListener() {
            @Override
            public void onLongSeatDataChange(LongSeatData longSeatData) {

                isToggChecked = longSeatData.isOpen();
                longSitToggleBtn.setChecked(longSeatData.isOpen());

                hour = longSeatData.getEndHour();
                min = longSeatData.getEndMinute();

                startHour = longSeatData.getStartHour();
                startMin = longSeatData.getStartMinute();

                String startHourStr = "";
                String startMinStr = "";

                String minStr = "";
                String hourStr = "";

                if(startHour < 10){
                    startHourStr = "0"+ startHour;
                }else {
                    startHourStr = ""+startHour;
                }

                if(startMin < 10){
                    startMinStr = "0"+ startMin;
                }else {
                    startMinStr = ""+startMin;
                }

                if(min < 10){
                    minStr  = "0"+ min;
                }else {
                    minStr = ""+min;
                }

                if(hour < 10){
                    hourStr = "0"+hour;
                }else {
                    hourStr = ""+hour;
                }

                showB30LongSitStartTv.setText(startHourStr + ":" + startMinStr);
                showB30LongSitEndTv.setText(hourStr + ":" + minStr);
                showB30LongSitTv.setText(longSeatData.getThreshold() + "min");
            }
        });
    }

    private void initData() {
        hourList = new ArrayList<>();

        minuteList = new ArrayList<>();

        minuteMapList = new HashMap<>();


        longTimeLit = new ArrayList<>();

        longSitHourList = new ArrayList<>();

        for (int i = 8; i <= 18; i ++) {
            if(i < 10){
                longSitHourList.add("0"+i);
            }else {
                longSitHourList.add(i + "");
            }

        }

        for (int i = 30; i <= 240; i++) {
            longTimeLit.add(i + "");
        }

        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                minuteList.add("00");
            } else if (i < 10) {
                minuteList.add("0" + i);
            } else {
                minuteList.add(i + "");
            }
        }
        for (int i = 8; i <= 18; i++) {
            if (i < 10) {
                hourList.add("0" + i + "");
                minuteMapList.put("0" + i + "", minuteList);

            } else {
                hourList.add(i + "");
                minuteMapList.put(i + "", minuteList);

            }
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_sedentary_setting));//"久坐设置"
        longSitToggleBtn.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30LongSitStartRel, R.id.b30LongSitEndRel,
            R.id.b30LongSitTimeRel, R.id.b30LongSitSaveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30LongSitStartRel:   //开始时间
                chooseStartEndDate(0);
                break;
            case R.id.b30LongSitEndRel: //结束时间
                chooseStartEndDate(1);
                //setLongSitHour();
                break;
            case R.id.b30LongSitTimeRel:    //时长
                chooseLongTime();
                break;
            case R.id.b30LongSitSaveBtn:    //保存
                saveLongSitData();
                break;
        }
    }

    private void saveLongSitData() {
        if (MyCommandManager.DEVICENAME != null) {
            String startD = showB30LongSitStartTv.getText().toString().trim();
            if(WatchUtils.isEmpty(startD))
                return;
            int startHour = Integer.valueOf(StringUtils.substringBefore(startD, ":").trim());
            int startMine = Integer.valueOf(StringUtils.substringAfter(startD, ":").trim());
            String endD = showB30LongSitEndTv.getText().toString().trim();
            int endHour = Integer.valueOf(StringUtils.substringBefore(endD, ":").trim());
            int endMine = Integer.valueOf(StringUtils.substringAfter(endD, ":").trim());
            //时长
            String longD = showB30LongSitTv.getText().toString().trim();
            int longTime = Integer.valueOf(StringUtils.substringBefore(longD, "min").trim());
            MyApp.getInstance().getVpOperateManager().settingLongSeat(iBleWriteResponse, new LongSeatSetting(startHour, startMine, endHour, endMine, longTime, isToggChecked), new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeatData) {
                    Log.e("久坐", "----longSeatData=" + longSeatData.toString());
                    if (longSeatData.getStatus() == ELongSeatStatus.OPEN_SUCCESS || longSeatData.getStatus() == ELongSeatStatus.CLOSE_SUCCESS) {
                        Toast.makeText(B30LongSitSetActivity.this,  getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }
            });
        }
    }

    //设置时长
    private void chooseLongTime() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30LongSitSetActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                showB30LongSitTv.setText(profession + "min");
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longTimeLit) //min year in loop
                .dateChose("30") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    //TODO 设置久坐hour部分
    //设置久坐结束时间
    private void setLongSitHour() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30LongSitSetActivity.this,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        showB30LongSitEndTv.setText(profession+":00");
                       // SharedPreferencesUtils.setParam(getActivity(), "b30SleepGoal", profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longSitHourList) //min year in loop
                .dateChose("8") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30LongSitSetActivity.this);
    }



    //选择时间
    private void chooseStartEndDate(final int code) {
        ProvincePick starPopWin = new ProvincePick.Builder(B30LongSitSetActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (code == 0) {  //开始时间
                    showB30LongSitStartTv.setText(province + ":" + city);
                }
                else if (code == 1) {    //结束时间
                    showB30LongSitEndTv.setText(province + ":" + city);

                }

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.longSitToggleBtn:
                isToggChecked = isChecked;
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
