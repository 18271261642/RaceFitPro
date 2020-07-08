package com.example.bozhilun.android.xwatch.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.w30s.ble.WriteBackDataListener;
import com.example.bozhilun.android.xwatch.ble.XWatchAlarmBackListener;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/2/20
 */
public class XWatchAlarmFragment extends LazyFragment {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.xWatchAlarmTimeTv1)
    TextView xWatchAlarmTimeTv1;
    @BindView(R.id.xWatchAlarmWeeksTv1)
    TextView xWatchAlarmWeeksTv1;
    @BindView(R.id.xWatchAlarmSwitchToggle1)
    ToggleButton xWatchAlarmSwitchToggle1;
    @BindView(R.id.xWatchAlarmTimeTv2)
    TextView xWatchAlarmTimeTv2;
    @BindView(R.id.xWatchAlarmWeeksTv2)
    TextView xWatchAlarmWeeksTv2;
    @BindView(R.id.xWatchAlarmSwitchToggle2)
    ToggleButton xWatchAlarmSwitchToggle2;
    @BindView(R.id.xWatchAlarmTimeTv3)
    TextView xWatchAlarmTimeTv3;
    @BindView(R.id.xWatchAlarmWeeksTv3)
    TextView xWatchAlarmWeeksTv3;
    @BindView(R.id.xWatchAlarmSwitchToggle3)
    ToggleButton xWatchAlarmSwitchToggle3;
    Unbinder unbinder;

    private XWatchBleAnalysis xWatchBleAnalysis;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private List<B18AlarmBean> alarmList;


    private B18AlarmBean firstBean = null;
    private B18AlarmBean secondBean = null;
    private B18AlarmBean thirdBean = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (xWatchBleAnalysis == null)
            xWatchBleAnalysis = XWatchBleAnalysis.getW37DataAnalysis();
        alarmList = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_x_watch_alarm_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        readDeviceAlarm();
    }


    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.alarm_clock));

        xWatchAlarmSwitchToggle1.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchAlarmSwitchToggle2.setOnCheckedChangeListener(onCheckedChangeListener);
        xWatchAlarmSwitchToggle3.setOnCheckedChangeListener(onCheckedChangeListener);

    }

    private void readDeviceAlarm() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        if(getActivity() == null || getActivity().isFinishing())
            return;
        alarmList.clear();
        showLoadingDialog("Loading...");
        xWatchBleAnalysis.readDeviceAlarm(0,new XWatchAlarmBackListener() {
            @Override
            public void backAlarmOne(B18AlarmBean b18AlarmBean1) {
                try {
                    firstBean = b18AlarmBean1;

                    //时间
                    int hour = b18AlarmBean1.getHour();
                    int mine = b18AlarmBean1.getMinute();

                    String bxHour = Integer.toHexString(hour);
                    int tmpHour = Integer.valueOf(bxHour);

                    String bxMine = Integer.toHexString(mine);
                    int tmpMine = Integer.valueOf(bxMine);

                    xWatchAlarmTimeTv1.setText((tmpHour<10?"0"+tmpHour : tmpHour)+":"+(tmpMine<10?"0"+tmpMine:tmpMine));
                    //周
                    xWatchAlarmWeeksTv1.setText(b18AlarmBean1.showAlarmWeeks(getContext()));
                    //开关
                    xWatchAlarmSwitchToggle1.setChecked(b18AlarmBean1.isOpen());


                    readAlarmSecond();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    private void readAlarmSecond(){
        xWatchBleAnalysis.readDeviceAlarm(1, new XWatchAlarmBackListener() {
            @Override
            public void backAlarmOne(B18AlarmBean b18AlarmBean1) {
                try {
                    secondBean = b18AlarmBean1;
                    //时间
                    int hour = b18AlarmBean1.getHour();
                    int mine = b18AlarmBean1.getMinute();


                    String bxHour = Integer.toHexString(hour);
                    int tmpHour = Integer.valueOf(bxHour);

                    String bxMine = Integer.toHexString(mine);
                    int tmpMine = Integer.valueOf(bxMine);

                    xWatchAlarmTimeTv2.setText((tmpHour<10?"0"+tmpHour : tmpHour)+":"+(tmpMine<10?"0"+tmpMine:tmpMine));
                    //周
                    xWatchAlarmWeeksTv2.setText(b18AlarmBean1.showAlarmWeeks(getContext()));
                    //开关
                    xWatchAlarmSwitchToggle2.setChecked(b18AlarmBean1.isOpen());

                    readThirdAlarm();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }



    private void readThirdAlarm(){
        xWatchBleAnalysis.readDeviceAlarm(2, new XWatchAlarmBackListener() {
            @Override
            public void backAlarmOne(B18AlarmBean b18AlarmBean1) {
                try {
                    closeLoadingDialog();
                    thirdBean = b18AlarmBean1;
                    //时间
                    int hour = b18AlarmBean1.getHour();
                    int mine = b18AlarmBean1.getMinute();

                    String bxHour = Integer.toHexString(hour);
                    int tmpHour = Integer.valueOf(bxHour);

                    String bxMine = Integer.toHexString(mine);
                    int tmpMine = Integer.valueOf(bxMine);

                    xWatchAlarmTimeTv3.setText((tmpHour<10?"0"+tmpHour : tmpHour)+":"+(tmpMine<10?"0"+tmpMine:tmpMine));
                    //周
                    xWatchAlarmWeeksTv3.setText(b18AlarmBean1.showAlarmWeeks(getContext()));
                    //开关
                    xWatchAlarmSwitchToggle3.setChecked(b18AlarmBean1.isOpen());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.commentB30BackImg, R.id.xWatchAlarmItem1,
            R.id.xWatchAlarmItem2, R.id.xWatchAlarmItem3})
    public void onClick(View view) {
        fragmentManager = getFragmentManager();
        if(fragmentManager == null)
            return;
        if(MyCommandManager.DEVICENAME== null)
            return;
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                fragmentManager.popBackStack();
                break;
            case R.id.xWatchAlarmItem1:
                chooseItem(firstBean);
                break;
            case R.id.xWatchAlarmItem2:
                chooseItem(secondBean);
                break;
            case R.id.xWatchAlarmItem3:
                chooseItem(thirdBean);
                break;
        }
    }

    private void chooseItem(B18AlarmBean bb){
        xWatchBleAnalysis.setB18AlarmBean(bb);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.xWatchDeviceFrameLayout, new XWatchUpdateAlarmFragment())
                .addToBackStack(null)
                .commit();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed())
                return;
            if(firstBean == null || secondBean == null || thirdBean == null)
                return;
            switch (buttonView.getId()){
                case R.id.xWatchAlarmSwitchToggle1:
                   firstBean.setOpen(isChecked);
                    setAlarmTime(0,firstBean);
                    break;
                case R.id.xWatchAlarmSwitchToggle2:
                   secondBean.setOpen(isChecked);
                    setAlarmTime(1,secondBean);
                    break;
                case R.id.xWatchAlarmSwitchToggle3:
                    thirdBean.setOpen(isChecked);
                    setAlarmTime(2,thirdBean);
                    break;
            }
        }
    };


    private void setAlarmTime(int alarmId,B18AlarmBean bbean){
        xWatchBleAnalysis.setAlarmData(alarmId, bbean.isOpen(), bbean.getHour(), bbean.getMinute(), bbean.isOpenSunday(),
                bbean.isOpenMonday(), bbean.isOpenTuesday(), bbean.isOpenWednesday(), bbean.isOpenThursday(), bbean.isOpenFriday(), bbean.isOpenSaturday(), new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {

                    }
                });


    }
}
