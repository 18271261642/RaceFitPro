package com.example.bozhilun.android.xwatch;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.WriteBackDataListener;
import com.example.bozhilun.android.xwatch.ble.XWatchAlarmBackListener;
import com.example.bozhilun.android.xwatch.ble.XWatchAlarmListener;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchCountStepListener;
import com.example.bozhilun.android.xwatch.ble.XWatchSportDetailListener;
import com.example.bozhilun.android.xwatch.ble.XWatchSyncSuccListener;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2020/2/19
 */
public class XWatchIntelActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.xWatchShowTv)
    TextView xWatchShowTv;

    private XWatchBleAnalysis xWatchBleAnalysis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x_watch_intel_layout);
        ButterKnife.bind(this);

        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("内部调试页面");

        xWatchBleAnalysis = XWatchBleAnalysis.getW37DataAnalysis();

    }

    @OnClick({R.id.commentB30BackImg, R.id.getDeviceTimeBtn,
            R.id.syncDeviceTimeBtn, R.id.getUserInfoBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.getDeviceTimeBtn: //获取手表时间

                xWatchBleAnalysis.getWatchTime(new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {
                        StringBuilder stringBuilder = new StringBuilder();
                        xWatchShowTv.setText(stringBuilder.append(Arrays.toString(data))+"\n");
                        for(byte da : data){
                            stringBuilder.append(Customdata.byteToHex(da));
                        }
                        xWatchShowTv.setText(stringBuilder.toString());
                    }
                });
                break;
            case R.id.syncDeviceTimeBtn:

                xWatchBleAnalysis.setUserInfoToDevice(1, 25, 175, 65, new XWatchSyncSuccListener() {
                    @Override
                    public void bleSyncComplete(byte[] data) {
                        xWatchShowTv.setText("设置用户信息返回="+Arrays.toString(data));
                    }
                });


                break;
            case R.id.getUserInfoBtn:
//                xWatchBleAnalysis.getUserInfoFormDevice(new WriteBackDataListener() {
//                    @Override
//                    public void backWriteData(byte[] data) {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        xWatchShowTv.setText(stringBuilder.append(Arrays.toString(data))+"\n");
//                        for(byte da : data){
//                            stringBuilder.append(Customdata.byteToHex(da)+" ");
//                        }
//                        xWatchShowTv.setText(stringBuilder.toString());
//                    }
//                });


                xWatchBleAnalysis.openOrCloseCamera(0);


                break;
        }
    }
}
