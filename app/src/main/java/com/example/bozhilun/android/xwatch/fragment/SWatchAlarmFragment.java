package com.example.bozhilun.android.xwatch.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.view.LazyFragment;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/4/9
 */
public class SWatchAlarmFragment extends LazyFragment implements CompoundButton.OnCheckedChangeListener {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;

    @BindView(R.id.sWatchAlarmTimeTv)
    TextView sWatchAlarmTimeTv;

    @BindView(R.id.sWatchAlarmWeeksTv)
    TextView sWatchAlarmWeeksTv;

    @BindView(R.id.sWatchAlarmSwitchToggle)
    ToggleButton sWatchAlarmSwitchToggle;

    @BindView(R.id.sWatchAlarmItem)
    ConstraintLayout sWatchAlarmItem;

    Unbinder unbinder;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private B18AlarmBean resultBean = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_s_watch_alarm_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        String sWatchStr = (String) SharedPreferencesUtils.getParam(getActivity(), "s_watch_alarm", "");
        if (WatchUtils.isEmpty(sWatchStr)) {
            B18AlarmBean b18AlarmBean = new B18AlarmBean(true, 0, 0, false, false, false, false, false, false, false);
            sWatchStr = new Gson().toJson(b18AlarmBean);
            SharedPreferencesUtils.setParam(getActivity(), "s_watch_alarm", sWatchStr);
        }

        resultBean = new Gson().fromJson(sWatchStr, B18AlarmBean.class);
        showAlarmSet(resultBean);


    }

    private void showAlarmSet(B18AlarmBean savedAlarmStr) {
        Log.e("TAG","------save="+savedAlarmStr.toString());
        //时间
        int hour = savedAlarmStr.getHour();
        int mine = savedAlarmStr.getMinute();
        sWatchAlarmTimeTv.setText((hour < 10 ? "0" + hour : hour) + ":" + (mine < 10 ? "0" + mine : mine));
        sWatchAlarmWeeksTv.setText(savedAlarmStr.showAlarmWeeks(getActivity()));
        sWatchAlarmSwitchToggle.setChecked(savedAlarmStr.isOpen());

        XWatchBleAnalysis.getW37DataAnalysis().setSWatchAlarm(savedAlarmStr);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.alarmclock));
        sWatchAlarmSwitchToggle.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed())
            return;
        if (buttonView.getId() == R.id.sWatchAlarmSwitchToggle) {
            sWatchAlarmSwitchToggle.setChecked(isChecked);
            resultBean.setOpen(isChecked);
            SharedPreferencesUtils.setParam(getActivity(),"s_watch_alarm",new Gson().toJson(resultBean));
            showAlarmSet(resultBean);

        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.sWatchAlarmItem})
    public void onClick(View view) {
        fragmentManager = getFragmentManager();
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                fragmentManager.popBackStack();
                break;
            case R.id.sWatchAlarmItem:
                XWatchBleAnalysis.getW37DataAnalysis().setB18AlarmBean(resultBean);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.xWatchDeviceFrameLayout, new XWatchUpdateAlarmFragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
