package com.example.bozhilun.android.b36;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IScreenLightListener;
import com.veepoo.protocol.model.datas.ScreenLightData;
import com.veepoo.protocol.model.settings.ScreenSetting;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * B36屏幕亮度调节
 * Created by Admin
 * Date 2018/11/14
 */
public class B36LightActivity extends WatchBaseActivity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "B36LightActivity";


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.privateBloadToggleBtn)
    ToggleButton privateBloadToggleBtn;
    @BindView(R.id.b36LightSeekBar)
    SeekBar b36LightSeekBar;
    @BindView(R.id.b36IsAutoLin)
    LinearLayout b36IsAutoLin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b36_light);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        //读取屏幕亮度
        MyApp.getInstance().getVpOperateManager().readScreenLight(iBleWriteResponse, new IScreenLightListener() {
            @Override
            public void onScreenLightDataChange(ScreenLightData screenLightData) {
                Log.e(TAG,"----读取屏幕亮度="+screenLightData.toString());
                setUiData(screenLightData);
            }
        });

    }

    private void setUiData(ScreenLightData screenLightData) {

        b36LightSeekBar.setMax(screenLightData.getScreenSetting().getMaxLevel());   //设置最大亮度
        b36LightSeekBar.setProgress(screenLightData.getScreenSetting().getLevel()); //设置当前的进度显示
        //判断是否是自动设置 1-自动设置；2-手动
        int autoData = screenLightData.getScreenSetting().getAuto();
        if (autoData == 1) {
            privateBloadToggleBtn.setChecked(true);
            b36IsAutoLin.setVisibility(View.INVISIBLE);
        } else {
            privateBloadToggleBtn.setChecked(false);
            b36IsAutoLin.setVisibility(View.VISIBLE);
        }


    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.b36_light_adju));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        b36LightSeekBar.setOnSeekBarChangeListener(this);
        privateBloadToggleBtn.setOnCheckedChangeListener(this);

    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

       setLightLevel(progress,2);
    }



    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //设置亮度
    private void setLightLevel(int progress,int auto) {
        ScreenSetting screenSetting = new ScreenSetting(0,0,
                23,59,progress,progress,auto);
        MyApp.getInstance().getVpOperateManager().settingScreenLight(iBleWriteResponse, new IScreenLightListener() {
            @Override
            public void onScreenLightDataChange(ScreenLightData screenLightData) {

            }
        },screenSetting );
    }

    //开关
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.isPressed()){
            if(isChecked){
                b36IsAutoLin.setVisibility(View.INVISIBLE);
                setLightLevel(b36LightSeekBar.getProgress(),1);
            }else{
                b36IsAutoLin.setVisibility(View.VISIBLE);
                setLightLevel(b36LightSeekBar.getProgress(),2);
            }
        }
    }
}
