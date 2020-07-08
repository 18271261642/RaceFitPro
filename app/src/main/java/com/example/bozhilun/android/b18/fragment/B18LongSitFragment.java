package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.b18.B18Constance;
import com.example.bozhilun.android.b18.modle.B18LongSitBean;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 久坐提醒
 * Created by Admin
 * Date 2019/11/14
 */
public class B18LongSitFragment extends LazyFragment {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;

    @BindView(R.id.showB18LongSitStartTv)
    TextView showB18LongSitStartTv;

    @BindView(R.id.b18LongSitStartRel)
    RelativeLayout b18LongSitStartRel;

    @BindView(R.id.showB30LongSitEndTv)
    TextView showB30LongSitEndTv;

    @BindView(R.id.b30LongSitEndRel)
    RelativeLayout b30LongSitEndRel;
    @BindView(R.id.show18LongSitTv)
    TextView show18LongSitTv;
    @BindView(R.id.b18LongSitTimeRel)
    RelativeLayout b18LongSitTimeRel;
    Unbinder unbinder;
    @BindView(R.id.b18LongSitToggleBtn)
    ToggleButton b18LongSitToggleBtn;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    ArrayList<String> longTimeLit;

    ArrayList<String> longSitHourList;//久坐结束时间

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b18_long_sit_layout, container, false);
        unbinder = ButterKnife.bind(this, view);


        initViews();

        initData();

        setLongData();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setLongData() {
        String longSitData = (String) SharedPreferencesUtils.getParam(getActivity(), B18Constance.B18_LONG_SIT_KEY,"");
        if(WatchUtils.isEmpty(longSitData)){
            B18LongSitBean b18LongSitBean = new B18LongSitBean(false,0,0,0,0,0);
            String str = new Gson().toJson(b18LongSitBean);
            longSitData = str;
            SharedPreferencesUtils.setParam(getActivity(),B18Constance.B18_LONG_SIT_KEY,str);

            B18BleConnManager.getB18BleConnManager().setLongSitNoti(false,0,0,0,0,0);
        }

        B18LongSitBean showB = new Gson().fromJson(longSitData,B18LongSitBean.class);
        b18LongSitToggleBtn.setChecked(showB.isOpen());
        showB18LongSitStartTv.setText((showB.getStartHour() <= 9 ? "0" + showB.getStartHour() : showB.getStartHour()) + ":" + (showB.getStartMine() <= 9 ? "0" + showB.getStartMine() : showB.getStartMine()));
        showB30LongSitEndTv.setText((showB.getEndHour() <= 9 ? "0" + showB.getEndHour() : showB.getEndHour()) + ":" + (showB.getEndMine() <= 9 ? "0" + showB.getEndMine() : showB.getEndMine()));
        show18LongSitTv.setText(showB.getIntevel()+"mine");


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

        for (int i = 30; i <= 120; i++) {
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
        commentB30TitleTv.setText(getResources().getString(R.string.Sedentaryreminder));
        b18LongSitToggleBtn.setOnCheckedChangeListener(onCheckedChangeListener);
       // show18LongSitTv.setText("30mine");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.commentB30BackImg, R.id.b18LongSitStartRel,
            R.id.b30LongSitEndRel, R.id.b18LongSitTimeRel,
            R.id.b18LongSitSaveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                getFragmentManager().popBackStack();
                break;
            case R.id.b18LongSitStartRel:   //选择开始日期
                chooseStartEndDate(0);
                break;
            case R.id.b30LongSitEndRel:     //选择结束日期
                chooseStartEndDate(1);
                break;
            case R.id.b18LongSitTimeRel:    //时长
                chooseLongTime();
                break;
            case R.id.b18LongSitSaveBtn:    //保存设置
                //开始时间
                String startTime = showB18LongSitStartTv.getText().toString();
                String endTime = showB30LongSitEndTv.getText().toString();
                String intervalTieme = show18LongSitTv.getText().toString();
                if(startTime.equals("00:00") || endTime.equals("00:00") || intervalTieme.equals("0mine"))
                    return;
                setDeviceLongData(startTime,endTime,intervalTieme);
                break;
        }
    }

    //设置提醒
    private void setDeviceLongData(String startTime, String endTime,String intervalTieme) {
        try {
            boolean isOn = b18LongSitToggleBtn.isChecked();
            int startHour = Integer.valueOf(StringUtils.substringBefore(startTime,":"));
            int startMine = Integer.valueOf(StringUtils.substringAfter(startTime,":"));
            int endHour = Integer.valueOf(StringUtils.substringBefore(endTime,":"));
            int endMine = Integer.valueOf(StringUtils.substringAfter(endTime,":"));
            int interval = Integer.valueOf(StringUtils.substringBefore(intervalTieme,"m").trim());
            B18BleConnManager.getB18BleConnManager().setLongSitNoti(isOn,interval,startHour,startMine,endHour,endMine);

            B18LongSitBean b18LongSitBean = new B18LongSitBean(isOn,startHour,startMine,endHour,endMine,interval);
            String str = new Gson().toJson(b18LongSitBean);
            SharedPreferencesUtils.setParam(getActivity(),B18Constance.B18_LONG_SIT_KEY,str);

            ToastUtil.showToast(getActivity(),getString(R.string.settings_success));
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //选择时间
    private void chooseStartEndDate(final int code) {
        ProvincePick starPopWin = new ProvincePick.Builder(getActivity(), new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (code == 0) {  //开始时间
                    showB18LongSitStartTv.setText(province + ":" + city);
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
        starPopWin.showPopWin(getActivity());
    }


    //设置时长
    private void chooseLongTime() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                show18LongSitTv.setText(profession + "mine");
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
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed())
                return;
            b18LongSitToggleBtn.setChecked(isChecked);
        }
    };

}
