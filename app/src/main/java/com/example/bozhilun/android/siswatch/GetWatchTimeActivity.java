package com.example.bozhilun.android.siswatch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.bleus.GetH8TimeInterface;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/26.
 */

public class GetWatchTimeActivity extends WatchBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.showWatchTimeTv)
    TextView showWatchTimeTv;
    @BindView(R.id.showAnalysisTimeTv)
    TextView showAnalysisTimeTv;
    @BindView(R.id.showSysTimeTv)
    TextView showSysTimeTv;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    showSysTimeTv.setText(sdf.format(new Date(System.currentTimeMillis())));
                    break;
                case 1002:
                    byte[] timeData = (byte[]) msg.obj;
                    showWatchTimeTv.setText(Customdata.bytes2HexString(timeData) + "-" + Arrays.toString(timeData));
                    showAnalysisTimeTv.setText(String.valueOf(20) + timeData[6] + "-" + timeData[7] + "-"
                            + timeData[8] + " " + timeData[9] + ":" + timeData[10] + ":" + timeData[11]);
                    MyApp.getInstance().h8BleManagerInstance().getH8SysType();
                    break;
                case 1003:

                    break;
            }
        }
    };
    @BindView(R.id.searchWatchBtn)
    Button searchWatchBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_watchtime);
        ButterKnife.bind(this);


        initViews();
        new TimeThread().start();   //获取当前系统时间
        //MyApp.getWatchBluetoothService().setTimeInterface(this);

        initData();


    }

    private void initData() {
        if(MyCommandManager.DEVICENAME != null){
            MyApp.getInstance().h8BleManagerInstance().sendDataGetH8Time(WatchConstants.getWatchTime(), new GetH8TimeInterface() {
                @Override
                public void getH8TimeData(byte[] timeStr) {
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = timeStr;
                    handler.sendMessage(message);

                }
            });
        }
    }


    @OnClick(R.id.searchWatchBtn)
    public void onViewClicked() {
       if(MyCommandManager.DEVICENAME != null){
           MyApp.getInstance().h8BleManagerInstance().setPhoneAlert();
       }
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = 1001;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private void initViews() {
        tvTitle.setText("获取手表的时间");
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
