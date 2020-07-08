package com.example.bozhilun.android.b18.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.b18.modle.B18AlarmDbManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 新增或修改闹钟
 * Created by Admin
 * Date 2019/11/14
 */
public class UpdateB18AlarmFragment extends LazyFragment {

    View view;
    @BindView(R.id.watch_edit_topTitleTv)
    TextView watchEditTopTitleTv;
    @BindView(R.id.watch_alarmTimePicker)
    TimePicker watchAlarmTimePicker;
    @BindView(R.id.watch_editRepeatSwit)
    SwitchCompat watchEditRepeatSwit;
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
    Unbinder unbinder;


    @BindView(R.id.repeatRel)
    RelativeLayout repeatRel;

    private int id;

    private Map<String,Boolean> weekMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_edit_watchalarm, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();


        B18AlarmBean b18AlarmBean = B18AlarmDbManager.getB18AlarmDbManager().getB18AlarmBean();
        if(b18AlarmBean != null)
            showAlarmData(b18AlarmBean);

        return view;
    }

    //显示闹钟
    private void showAlarmData(B18AlarmBean b18AlarmBean) {

        Log.e("ALARM","---------参数="+b18AlarmBean.toString());

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            watchAlarmTimePicker.setHour(b18AlarmBean.getHour());
            watchAlarmTimePicker.setMinute(b18AlarmBean.getMinute());
        }else{
            watchAlarmTimePicker.setCurrentHour(b18AlarmBean.getHour());
            watchAlarmTimePicker.setCurrentMinute(b18AlarmBean.getMinute());
        }
        boolean isMonday = b18AlarmBean.isOpenMonday();
        boolean isTuesday = b18AlarmBean.isOpenTuesday();
        boolean isWednesday = b18AlarmBean.isOpenWednesday();
        boolean isThursday = b18AlarmBean.isOpenThursday();
        boolean isFriday = b18AlarmBean.isOpenFriday();
        boolean isSaturday = b18AlarmBean.isOpenSaturday();
        boolean isSunday = b18AlarmBean.isOpenSunday();


        this.id = b18AlarmBean.getId();

        watchCB1.setChecked(isMonday);
        watchCB2.setChecked(isTuesday);
        watchCB3.setChecked(isWednesday);
        watchCB4.setChecked(isThursday);
        watchCB5.setChecked(isFriday);
        watchCB6.setChecked(isSaturday);
        watchCB7.setChecked(isSunday);




        weekMap.put("week_1",isMonday);
        weekMap.put("week_2",isTuesday);
        weekMap.put("week_3",isWednesday);
        weekMap.put("week_4",isThursday);
        weekMap.put("week_5",isFriday);
        weekMap.put("week_6",isSaturday);
        weekMap.put("week_7",isSunday);



    }

    private void initViews() {
        repeatRel.setVisibility(View.GONE);
        watchCB1.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB2.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB3.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB4.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB5.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB6.setOnCheckedChangeListener(onCheckedChangeListener);
        watchCB7.setOnCheckedChangeListener(onCheckedChangeListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.watch_edit_topCancleImg, R.id.watch_edit_topSureImg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.watch_edit_topCancleImg:
                getFragmentManager().popBackStack();
                break;
            case R.id.watch_edit_topSureImg:
                updateAlarmData();
                break;
        }
    }

    private void updateAlarmData(){


        B18AlarmBean b18AlarmBean = new B18AlarmBean();

        int alarmHour = 0;
        int alarmMine = 0;

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            alarmHour = watchAlarmTimePicker.getHour();
            alarmMine = watchAlarmTimePicker.getMinute();
            b18AlarmBean.setHour(alarmHour);
            b18AlarmBean.setMinute(alarmMine);
        }else{
            alarmHour = watchAlarmTimePicker.getCurrentHour();
            alarmMine = watchAlarmTimePicker.getCurrentMinute();
            b18AlarmBean.setHour(alarmHour);
            b18AlarmBean.setMinute(alarmMine);
        }

        if(isCommTime(alarmHour,alarmMine)){

            ToastUtil.showToast(getActivity(),"闹钟已存在，请选择其它时间！");
            return;
        }


        boolean isMonday = weekMap.get("week_1");
        boolean isTuesday = weekMap.get("week_2");
        boolean isWednesday = weekMap.get("week_3");
        boolean isThursday = weekMap.get("week_4");
        boolean isFriday = weekMap.get("week_5");
        boolean isSaturday = weekMap.get("week_6");
        boolean isSunday = weekMap.get("week_7");

        if(!isMonday && !isTuesday && !isWednesday && !isThursday && !isFriday && !isSaturday && !isSunday){
            int todayWeek = WatchUtils.getDataForWeek();
            switch (todayWeek){
                case 1:
                    isMonday = true;
                    break;
                case 2:
                    isTuesday = true;
                    break;
                case 3:
                    isWednesday = true;
                    break;
                case 4:
                    isThursday = true;
                    break;
                case 5:
                    isFriday = true;
                    break;
                case 6:
                    isSaturday = true;
                    break;
                case 7:
                    isSunday = true;
                    break;
            }

        }


        b18AlarmBean.setOpenMonday(isMonday);
        b18AlarmBean.setOpenTuesday(isTuesday);
        b18AlarmBean.setOpenWednesday(isWednesday);
        b18AlarmBean.setOpenThursday(isThursday);
        b18AlarmBean.setOpenFriday(isFriday);
        b18AlarmBean.setOpenSaturday(isSaturday);
        b18AlarmBean.setOpenSunday(isSunday);
        b18AlarmBean.setId(id);
        b18AlarmBean.setOpen(true);
        B18AlarmDbManager.getB18AlarmDbManager().updateForIdAlarm(b18AlarmBean);

        getFragmentManager().popBackStack();
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed())return;
            switch (buttonView.getId()){
                case R.id.watch_CB1:
                    weekMap.put("week_1",isChecked);
                    watchCB1.setChecked(isChecked);
                    break;
                case R.id.watch_CB2:
                    weekMap.put("week_2",isChecked);
                    watchCB2.setChecked(isChecked);
                    break;
                case R.id.watch_CB3:
                    weekMap.put("week_3",isChecked);
                    watchCB3.setChecked(isChecked);
                    break;
                case R.id.watch_CB4:
                    weekMap.put("week_4",isChecked);
                    watchCB4.setChecked(isChecked);
                    break;
                case R.id.watch_CB5:
                    weekMap.put("week_5",isChecked);
                    watchCB5.setChecked(isChecked);
                    break;
                case R.id.watch_CB6:
                    weekMap.put("week_6",isChecked);
                    watchCB6.setChecked(isChecked);
                    break;
                case R.id.watch_CB7:
                    weekMap.put("week_7",isChecked);
                    watchCB7.setChecked(isChecked);
                    break;
            }
        }
    };


    //判断是否有相同的时间
    private boolean isCommTime(int hour,int mine){
        List<B18AlarmBean> tmpList = B18AlarmDbManager.getB18AlarmDbManager().findAllAlarm();
        for(B18AlarmBean abd : tmpList){
            int tmpHour = abd.getHour();
            int tmpMine = abd.getMinute();
            if(hour == tmpHour && mine == tmpMine && abd.getId()!=id){
                return true;
            }

        }
        return false;
    }
}
