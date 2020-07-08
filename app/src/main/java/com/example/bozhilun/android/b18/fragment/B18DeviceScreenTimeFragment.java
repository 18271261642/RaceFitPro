package com.example.bozhilun.android.b18.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.b18.B18Constance;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 亮屏时间
 * Created by Admin
 * Date 2019/11/14
 */
public class B18DeviceScreenTimeFragment extends LazyFragment {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.save_text)
    TextView saveText;
    @BindView(R.id.b18ScreenTimeTv)
    TextView b18ScreenTimeTv;
    Unbinder unbinder;

    private ArrayList<String> longTimeLit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b18_screen_time_layout, container, false);
        unbinder = ButterKnife.bind(this, view);


        initViews();

        initData();

        return view;
    }

    private void initData() {
        longTimeLit = new ArrayList<>();
        for(int i = 1;i<=6;i++){
            longTimeLit.add(i*5+"s");
        }
        String intervalTime = (String) SharedPreferencesUtils.getParam(getActivity(), B18Constance.B18_SCREEN_TIME,"5s");
        if(WatchUtils.isEmpty(intervalTime))
            intervalTime  = "5s";
        b18ScreenTimeTv.setText(intervalTime);
        if(MyCommandManager.DEVICENAME != null)
            setScreenTime(intervalTime);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("屏保时间");
        b18ScreenTimeTv.setText("5s");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30LongSitStartRel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                getFragmentManager().popBackStack();
                break;
            case R.id.b30LongSitStartRel:
                chooseLongTime();
                break;
        }
    }


    //设置时长
    private void chooseLongTime() {
        ProfessionPick professionPick = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                b18ScreenTimeTv.setText(profession);
                SharedPreferencesUtils.setParam(getActivity(), B18Constance.B18_SCREEN_TIME,profession);
                setScreenTime(profession);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longTimeLit) //min year in loop
                .dateChose("5s") // date chose when init popwindow
                .build();
        professionPick.showPopWin(getActivity());
    }

    //设置屏保时间
    private void setScreenTime(String profession) {
        int timeV = Integer.valueOf(StringUtils.substringBefore(profession,"s"));
        B18BleConnManager.getB18BleConnManager().setScreenTime(timeV);
    }

}
