package com.example.bozhilun.android.b11;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.LazyFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/6/15
 */
public class B11RecordFragment extends LazyFragment {

    View b11View;
    Unbinder unbinder;
    @BindView(R.id.sendByteDataBtn)
    TextView sendByteDataBtn;
    @BindView(R.id.backByteDataBtn)
    TextView backByteDataBtn;


    private B11BleOperate b11BleOperate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b11BleOperate = B11BleOperate.getB11BleOperate();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b11View = inflater.inflate(R.layout.fragment_b11_record_layout, container, false);
        unbinder = ButterKnife.bind(this, b11View);


        return b11View;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b11SyncTimeBtn, R.id.b11SetLanguageBtn,
            R.id.b11SetGoalBtn, R.id.b11SetHandBtn, R.id.b11Btn1,
            R.id.b11Btn2, R.id.b11Btn3,R.id.b11Btn4,R.id.b11Btn5})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b11SyncTimeBtn:
                b11BleOperate.syncB11Time(muiltByteDataListener);
                break;
            case R.id.b11SetLanguageBtn:
                b11BleOperate.syncSystemLanguage(0x01,muiltByteDataListener);
                break;
            case R.id.b11SetGoalBtn:
                b11BleOperate.syncSportGoal(10000, muiltByteDataListener);
                break;
            case R.id.b11SetHandBtn:
                b11BleOperate.setTurnWrist(true,true, muiltByteDataListener);
                break;

            case R.id.b11Btn1:
                b11BleOperate.syncUserInfoToDevice(0,25,60,175,60,muiltByteDataListener);
                break;
            case R.id.b11Btn2:
                b11BleOperate.setAllDayDetectHeart(true,30,muiltByteDataListener);
                break;
            case R.id.b11Btn3:
                b11BleOperate.setFindPhone(0x00, muiltByteDataListener);
                break;
            case R.id.b11Btn4:
                b11BleOperate.setTimeType(0x00,muiltByteDataListener);
                break;
            case R.id.b11Btn5:
                b11BleOperate.setMetric(0x01,muiltByteDataListener);
                break;
        }
    }

    private MuiltByteDataListener muiltByteDataListener = new MuiltByteDataListener() {
        @Override
        public void sendBackByteData(byte[] sendByte, byte[] backByte) {
            sendByteDataBtn.setText("发送的指令:"+Arrays.toString(sendByte));

            backByteDataBtn.setText("返回的指令:"+Arrays.toString(backByte));
        }
    };
}
