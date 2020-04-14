package com.example.bozhilun.android.siswatch.h8;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.siswatch.GetWatchTimeActivity;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.siswatch.bleus.GetH8StepsListener;
import com.example.bozhilun.android.siswatch.bleus.H8BleConstances;
import com.example.bozhilun.android.siswatch.bleus.H8BleManagerInstance;
import com.example.bozhilun.android.siswatch.bleus.H8ConnstateListener;
import com.example.bozhilun.android.siswatch.bleus.UploadH8StepsService;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.littlejie.circleprogress.circleprogress.WaveProgress;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 主界面Fragment
 */
public class H8RecordFragment extends LazyFragment implements GetH8StepsListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "H8RecordFragment";


    View recordView;
    @BindView(R.id.new_h8_recordwatch_connectStateTv)
    TextView newH8RecordwatchConnectStateTv;
    @BindView(R.id.new_h8_recordtop_dateTv)
    TextView newH8RecordtopDateTv;
    @BindView(R.id.new_h8_recordShareImg)
    ImageView newH8RecordShareImg;
    @BindView(R.id.new_h8_record_titleLin)
    LinearLayout newH8RecordTitleLin;


    @BindView(R.id.recordwave_progress_bar)
    WaveProgress recordwaveProgressBar;
    @BindView(R.id.watch_recordTagstepTv)
    TextView watchRecordTagstepTv;
    @BindView(R.id.watch_recordKcalTv)
    TextView watchRecordKcalTv;
    @BindView(R.id.watch_recordMileTv)
    TextView watchRecordMileTv;
    @BindView(R.id.watch_record_swipe)
    SwipeRefreshLayout watchRecordSwipe;

    @BindView(R.id.homeFastStatusTv)
    TextView homeFastStatusTv;


    Unbinder unbinder;

    private H8BleManagerInstance h8BleManagerInstance;
    //计算卡里路常量
    double kcalcanstanc = 65.4;  //计算卡路里常量

    private UploadH8StepsService uploadH8StepsService;

    private int steps;

    //公制转英制常量
    private double unitCons = 0.6214;

    //设置默认显示的步数和目标步数
    String tagStep ;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:  //显示当天的步数
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        Map<String, Integer> todayStepMap = (Map<String, Integer>) msg.obj;
                        int todayStep = todayStepMap.get("today");
                        steps = todayStep;
                        recordwaveProgressBar.setMaxValue(Integer.valueOf(tagStep));
                        recordwaveProgressBar.setValue(todayStep);
                        showKcalAnKm(todayStep);    //显示卡路里和公里数
                        if (uploadH8StepsService != null)
                            uploadH8StepsService.syncUserStepsData(todayStepMap);

                    }

                    break;
                case 1002:
                    if (watchRecordSwipe != null) {
                        watchRecordSwipe.setRefreshing(false);

                    }
                    getStepsData();
                    break;
                case 1003:
                    showConnState(true);
                    break;
                case 1004:
                    showConnState(false);
                    break;
            }


        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        h8BleManagerInstance = h8BleManagerInstance.getH8BleManagerInstance();
        tagStep = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "settagsteps", "10000");
        if(WatchUtils.isEmpty(tagStep))
            tagStep = "10000";
        //注册链接状态的广播
        registerConnState();
        uploadH8StepsService = UploadH8StepsService.getUploadH8StepsService();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recordView = inflater.inflate(R.layout.fragment_watch_record, container, false);
        unbinder = ButterKnife.bind(this, recordView);

        initViews();

        initData();

        return recordView;
    }

    private void initData() {
        if (MyCommandManager.DEVICENAME != null) {    //已经连接
            //获取步数
            h8BleManagerInstance.getH8Steps(WatchConstants.watchSteps, this);
            showConnState(true);
        } else {
            showConnState(false);
        }
    }

    //显示状态
    private void showConnState(final boolean isConn) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (isConn) {
                newH8RecordwatchConnectStateTv.setText(getResources().getString(R.string.connted));
                newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                AnimationUtils.stopFlick(newH8RecordwatchConnectStateTv);
                homeFastStatusTv.setText(getResources().getString(R.string.more_opera));

            } else {
                newH8RecordwatchConnectStateTv.setText(getResources().getString(R.string.disconnted));
                newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                AnimationUtils.startFlick(newH8RecordwatchConnectStateTv);
                //watchRestateTv.setText(getResources().getString(R.string.bluetooth_disconnected));
                homeFastStatusTv.setText(getResources().getString(R.string.disconnted));
            }
        }

    }

    private void initViews() {
        newH8RecordtopDateTv.setText(WatchUtils.getCurrentDate());

        watchRecordTagstepTv.setText(getResources().getString(R.string.settarget_steps) + tagStep);
        recordwaveProgressBar.setMaxValue(Integer.valueOf(tagStep));
        recordwaveProgressBar.setValue(0);
        watchRecordSwipe.setOnRefreshListener(this);


        newH8RecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getActivity(), GetWatchTimeActivity.class));
                return true;
            }
        });

    }

    //注册链接状态的广播
    private void registerConnState() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(H8BleConstances.ACTION_GATT_CONNECTED);
            intentFilter.addAction(H8BleConstances.ACTION_GATT_DISCONNECTED);
            if (getActivity() != null) {
                getActivity().registerReceiver(broadcastReceiver, intentFilter);
                MyApp.getInstance().h8BleManagerInstance().getH8BleService().setH8ConnstateListener(h8ConnstateListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //显示卡路里和公里数
    private void showKcalAnKm(int todayStep) {
        if (getActivity() == null)
            return;
        //用户身高
        String userHeight = (String) SharedPreferencesUtils.getParam(getActivity(), "userheight", "");
        //距离
        double distants = WatchUtils.getDistants(todayStep, WatchUtils.getStepLong(Integer.parseInt(userHeight)));
        if (WatchUtils.judgeUnit(MyApp.getContext())) {
            watchRecordMileTv.setText(StringUtils.substringBefore(distants + "", ".")
                    + "." + StringUtils.substringAfter(distants + "", ".").substring(0, 1) + "km");   //公里数=步数 / 1000公里数

        } else {
            watchRecordMileTv.setText(WatchUtils.doubleunitToImperial(distants,0)+"mi");

        }

        watchRecordKcalTv.setText(StringUtils.substringBefore(String.valueOf(WatchUtils.mul(kcalcanstanc, distants)), ".") + "kcal"); //卡里路 =步数 / 1000 * 0.65 是卡里
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (broadcastReceiver != null && getActivity() != null)
                getActivity().unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick({R.id.new_h8_recordtop_dateTv,
            R.id.new_h8_recordShareImg,R.id.homeFastLin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_h8_recordtop_dateTv:

                break;
            case R.id.new_h8_recordShareImg: //分享
                Intent intent = new Intent(getActivity(), SharePosterActivity.class);
                intent.putExtra("stepNum", steps + "");
                startActivity(intent);
                break;
            case R.id.homeFastLin:
                if(MyCommandManager.DEVICENAME != null){
                    startActivity(new Intent(getActivity(), WatchDeviceActivity.class));
                }else{
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                    if (MyApp.getInstance().h8BleManagerInstance().getH8BleService() != null) {
                        MyApp.getInstance().h8BleManagerInstance().getH8BleService().autoConnByMac(false);
                    }
                }
                break;
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "----action-=" + action);
            if (action == null)
                return;
            if (action.equals(H8BleConstances.ACTION_GATT_CONNECTED)) {   //已经连接
                showConnState(true);
               // getStepsData();
                handler.sendEmptyMessage(1002);
            }

            if (action.equals(H8BleConstances.ACTION_GATT_DISCONNECTED)) {
                showConnState(false);
            }

        }
    };


    private H8ConnstateListener h8ConnstateListener = new H8ConnstateListener() {
        @Override
        public void h8ConnSucc() {
            Log.e(TAG,"------11111---连接成功了-=---===");
            handler.sendEmptyMessage(1002);
//            showConnState(true);
//            getStepsData();
        }

        @Override
        public void h8ConnFailed() {
            Log.e(TAG,"---1111---断开连接---");
//            if(getActivity() != null && !getActivity().isFinishing()){
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showConnState(false);
//                    }
//                });
//            }

        }
    };


    private void getStepsData() {
        MyApp.getInstance().h8BleManagerInstance().getH8Steps(WatchConstants.watchSteps, new GetH8StepsListener() {
            @Override
            public void getH8OnWeekSteps(Map<String, Integer> stepMap) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = stepMap;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void getH8OnWeekSteps(Map<String, Integer> stepMap) {
        Message message = handler.obtainMessage();
        message.what = 1001;
        message.obj = stepMap;
        handler.sendMessage(message);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                    Message message = new Message();
                    message.what = 1002;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
