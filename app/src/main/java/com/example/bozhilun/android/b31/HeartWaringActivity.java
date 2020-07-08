package com.example.bozhilun.android.b31;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHeartWaringDataListener;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.settings.HeartWaringSetting;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 心率报警
 * Created by Admin
 * Date 2020/4/17
 */
public class HeartWaringActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.heartWaringValueTv)
    TextView heartWaringValueTv;
    @BindView(R.id.heartWaringToggleBtn)
    ToggleButton heartWaringToggleBtn;

    private ArrayList<String> heartList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_waring_layout);
        ButterKnife.bind(this);

        initViews();

        readHeartWaring();
    }

    private void readHeartWaring() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().getVpOperateManager().readHeartWarning(iBleWriteResponse, new IHeartWaringDataListener() {
            @Override
            public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                if(heartWaringData == null)
                    return;
                Log.e("11","-------读取心率报警="+heartWaringData.toString());
                heartWaringToggleBtn.setChecked(heartWaringData.isOpen());
                heartWaringValueTv.setText(heartWaringData.getHeartHigh()+"");
            }
        });
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("心率报警");
        heartWaringToggleBtn.setOnCheckedChangeListener(this);
        heartList = new ArrayList<>();
        for (int i = 70; i < 180; i++) {
            heartList.add("" + i++);
        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.heartWaringRel,
            R.id.heartWaringBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.heartWaringRel:
                chooseLongTime();
                break;
            case R.id.heartWaringBtn:
                saveHeartWaring();
                break;
        }
    }

    private void saveHeartWaring() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        final String heartValue = heartWaringValueTv.getText().toString();
        if (WatchUtils.isEmpty(heartValue))
            return;
        boolean isOpen = heartWaringToggleBtn.isChecked();
        int heightV = Integer.valueOf(heartValue.trim());
        HeartWaringSetting heartWaringData = new HeartWaringSetting(heightV, 70, isOpen);
        MyApp.getInstance().getVpOperateManager().settingHeartWarning(iBleWriteResponse, new IHeartWaringDataListener() {
            @Override
            public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                Log.e("11","--------设置心率报警="+heartWaringData.toString());
                ToastUtil.showToast(HeartWaringActivity.this,"success");
            }
        }, heartWaringData);


    }


    //设置时长
    private void chooseLongTime() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(HeartWaringActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                heartWaringValueTv.setText(profession);

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(heartList) //min year in loop
                .dateChose("70") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(HeartWaringActivity.this);
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed())
            return;
        if(buttonView.getId() == R.id.heartWaringToggleBtn){
            heartWaringToggleBtn.setChecked(isChecked);
        }
    }
}
