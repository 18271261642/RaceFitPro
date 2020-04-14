package com.example.bozhilun.android.siswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.bleus.GetH8JiedianListener;
import com.example.bozhilun.android.siswatch.bleus.ParentH8TimeListener;
import com.example.bozhilun.android.siswatch.bleus.SetH8JiedianListener;
import com.example.bozhilun.android.siswatch.h8.H8AlarmActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchDeviceActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "WatchDeviceActivity";


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.watch_mynaozhongRel)
    RelativeLayout watchMynaozhongRel;
    @BindView(R.id.watch_mymsgRel)
    RelativeLayout watchMymsgRel;

    String mDevicename, mDeviceAddress;
    @BindView(R.id.watch_mycaozuoRel)
    RelativeLayout watchMycaozuoRel;
    @BindView(R.id.watchDeviceTagShowTv)
    TextView watchDeviceTagShowTv;

    //节电模式开关
    @BindView(R.id.watch_message_jiedianSwitch)
    ToggleButton watchMessageJiedianSwitch;
    //来电提醒开关
    @BindView(R.id.watch_message_callphoneSwitch)
    ToggleButton watchMessageCallphoneSwitch;
    //节电模式开始时间
    @BindView(R.id.watch_jiedian_starttimeTv)
    TextView watchJiedianStarttimeTv;
    //节电模式结束时间
    @BindView(R.id.watch_jiedian_endtimeTv)
    TextView watchJiedianEndtimeTv;

    private String starHour, starMinute;//开始时间
    private String entHour, entMinute; //结束时间string

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    //间隔时间
    private ArrayList<String> jiangeTimeList;

    ArrayList<String> daily_numberofstepsList;
    String bleMac;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_device);
        ButterKnife.bind(this);
        //EventBus.getDefault().register(this);

        registerReceiver(broadcastReceiver, new IntentFilter(WatchUtils.WACTH_DISCONNECT_BLE_ACTION));   //注册接收蓝牙服务断开连接的广播
        initViews();


        initJiedianData();
        initData();

        getH8JiedianTime();


    }

    //获取节电时间
    private void getH8JiedianTime() {
        //获取节电时间
        MyApp.getInstance().h8BleManagerInstance().getH8JieDianTime(WatchConstants.getjiedianTime(), new GetH8JiedianListener() {
            @Override
            public void getJiedianTime(byte[] data) {
                Log.e(TAG, "--------返回节电时间=" + Arrays.toString(data));

                //显示节电的开始时间和结束时间，
                String newStartjiedianHour, newStartjiedianMine, newjiedianEndHour, newjiedianEndMine;
                int startjiedianHour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
                int startjiedianMine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
                int endjiedianHour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]));
                int endjiedianMine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[9]));
                if (startjiedianHour <= 9) {
                    newStartjiedianHour = String.valueOf(0) + startjiedianHour;
                } else {
                    newStartjiedianHour = startjiedianHour + "";
                }
                if (startjiedianMine <= 9) {
                    newStartjiedianMine = String.valueOf(0) + startjiedianMine;
                } else {
                    newStartjiedianMine = startjiedianMine + "";
                }

                if (endjiedianHour <= 9) {
                    newjiedianEndHour = String.valueOf(0) + endjiedianHour;
                } else {
                    newjiedianEndHour = endjiedianHour + "";
                }
                if (endjiedianMine <= 9) {
                    newjiedianEndMine = String.valueOf(0) + endjiedianMine;
                } else {
                    newjiedianEndMine = endjiedianMine + "";
                }

                final String jiedianTimeData = newStartjiedianHour + ":" + newStartjiedianMine + "-" + newjiedianEndHour + ":" + newjiedianEndMine;
                Log.e(TAG, "-----节电=" + jiedianTimeData);
                WatchDeviceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        watchJiedianStarttimeTv.setText(StringUtils.substringBefore(jiedianTimeData, "-"));
                        watchJiedianEndtimeTv.setText(StringUtils.substringAfter(jiedianTimeData, "-"));
                    }
                });

            }
        });


    }

    private void initJiedianData() {
        //提醒间隔时间
        jiangeTimeList = new ArrayList<>();
        jiangeTimeList.add("0" + "s");
        jiangeTimeList.add("5" + "s");
        jiangeTimeList.add("10" + "s");
        jiangeTimeList.add("30" + "s");
        jiangeTimeList.add("60" + "s");

        minuteMapList = new HashMap<>();
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                minuteList.add("00 m");
            } else if (i < 10) {
                minuteList.add("0" + i + " m");
            } else {
                minuteList.add(i + " m");
            }
        }
        for (int i = 0; i < 24; i++) {
            if (i == 0) {
                hourList.add("00 h");
                minuteMapList.put("00 h", minuteList);
            } else if (i < 10) {
                hourList.add("0" + i + " h");
                minuteMapList.put("0" + i + " h", minuteList);
            } else {
                hourList.add(i + " h");
                minuteMapList.put(i + " h", minuteList);
            }
        }

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.device));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (null != SharedPreferencesUtils.readObject(WatchDeviceActivity.this,  Commont.BLENAME)) {
            mDevicename = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this,  Commont.BLENAME);
            mDeviceAddress = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this, "mylanmac");

        }

        watchMessageJiedianSwitch.setOnCheckedChangeListener(this);
        watchMessageCallphoneSwitch.setOnCheckedChangeListener(this);
    }

    private void initData() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }
        bleMac = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this, Commont.BLEMAC);
        String tagStp = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "settagsteps", "");
        if (!WatchUtils.isEmpty(tagStp)) {
            watchDeviceTagShowTv.setText(tagStp);
        }

        //来电提醒按钮状态
        String laidianPhone = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "laidianphone", "on");
        Log.e(TAG, "-------laidianPhone=" + laidianPhone);
        if (laidianPhone != null) {
            if ("on".equals(laidianPhone)) {
                watchMessageCallphoneSwitch.setChecked(true);
            } else {
                watchMessageCallphoneSwitch.setChecked(false);
            }
        }
        //节电模式
        String jiedian = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", "");
        Log.e(TAG, "-------jiedian=" + jiedian);
        if (jiedian != null) {
            if ("on".equals(jiedian)) {
                watchMessageJiedianSwitch.setChecked(true);
            } else {
                watchMessageJiedianSwitch.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.watchUnpairRel, R.id.watch_mynaozhongRel, R.id.watch_mymsgRel,
            R.id.watch_mycaozuoRel, R.id.watch_targetRel,
            R.id.watch_message_jiedianLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watchUnpairRel:   //解除绑定
                doUnpairDisconn();  //解除绑定
                break;
            case R.id.watch_mynaozhongRel:  //闹钟提醒
                //startActivity(WatchAlarmActivity.class);
                startActivity(H8AlarmActivity.class);
                break;
            case R.id.watch_mymsgRel:       //消息提醒
//                Log.e(TAG, "-----辅助功能----" + WatchUtils.isNotificationEnabled(WatchDeviceActivity.this) + "---" + !WatchUtils.isAccessibilitySettingsOn(this));
                startActivity(WatchMessageActivity.class);
                break;
            case R.id.watch_mycaozuoRel:    //操作说明
                startActivity(WatchOperationActivity.class);
                break;
            case R.id.watch_targetRel:  //目标设置
                ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(WatchDeviceActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置步数
//                        watchRecordTagstepTv.setText("目标步数 " + profession);
//                        recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
                        watchDeviceTagShowTv.setText(profession);
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "settagsteps", profession);
                        // recordwaveProgressBar.setValue(Float.valueOf((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")));

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_numberofstepsList) //min year in loop
                        .dateChose("10000") // date chose when init popwindow
                        .build();
                dailyumberofstepsPopWin.showPopWin(WatchDeviceActivity.this);
                break;
            case R.id.watch_message_jiedianLin: //节电模式
                if (SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", "") != null &&
                        "on".equals(SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", ""))) {
                    showJieDianStartTime(); //节电模式开始时间
                }
                break;
        }
    }

    //解除绑定
    private void doUnpairDisconn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.confirm_unbind_strap))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (MyCommandManager.DEVICENAME != null) {
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), Commont.BLEMAC, "");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(),  Commont.BLENAME, "");
//                            MyApp.h8BleManagerInstance().disConnH8(new H8ConnstateListener() {
//                                @Override
//                                public void h8ConnSucc() {
//                                    Log.e(TAG,"-----------断开连接成功-----");
//                                    startActivity(NewSearchActivity.class);
//                                    finish();
//                                }
//
//                                @Override
//                                public void h8ConnFailed() {
//
//                                }
//                            });
                            MyApp.getInstance().h8BleManagerInstance().sendDataGetH8Alarm(new byte[]{0x00}, new ParentH8TimeListener() {
                                @Override
                                public void backData(byte[] data) {
                                    Log.e(TAG, "---------断开=" + Arrays.toString(data));
                                    startActivity(NewSearchActivity.class);
                                    finish();
                                }
                            });
                        }


                    }
                }).show();

    }


    //首先显示开始时间
    private void showJieDianStartTime() {
        ProvincePick starPopWin = new ProvincePick.Builder(WatchDeviceActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                starHour = province.substring(0, province.length() - 2);
                starMinute = city.substring(0, city.length() - 2);
                watchJiedianStarttimeTv.setText(starHour + ":" + starMinute);
                showEndTime();  //选择开始时间后显示结束

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(WatchDeviceActivity.this);
    }

    //显示结束时间
    private void showEndTime() {
        ProvincePick starPopWin = new ProvincePick.Builder(WatchDeviceActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                entHour = province.substring(0, province.length() - 2);
                entMinute = city.substring(0, city.length() - 2);
                watchJiedianEndtimeTv.setText(entHour + ":" + entMinute);
                MyApp.getInstance().h8BleManagerInstance().setH8JieDianTime(Integer.valueOf(starHour), Integer.valueOf(starMinute), Integer.valueOf(entHour), Integer.valueOf(entMinute), new SetH8JiedianListener() {
                    @Override
                    public void setH8JiedianTime(byte[] data) {
                        Log.e(TAG, "-----设置节电时间=" + Arrays.toString(data));
                    }
                });


            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(WatchDeviceActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(WatchDeviceActivity.this, WatchMessageActivity.class));
    }
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(MessageEvent event) {
//        String result = event.getMessage();
//        if (result != null) {
//            if(result.equals("msgJiedian")){
//                String timeData = (String) event.getObject();
//                watchJiedianStarttimeTv.setText(StringUtils.substringBefore(timeData,"-"));
//                watchJiedianEndtimeTv.setText(StringUtils.substringAfter(timeData,"-"));
//            }
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
        unregisterReceiver(broadcastReceiver);

    }

    /**
     * H8断开连接接收广播，只断开连接，不需要解除配对
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "-------action---" + action);
            if (!WatchUtils.isEmpty(action)) {
                if (action.equals(WatchUtils.WACTH_DISCONNECT_BLE_ACTION)) {
                    String bleState = intent.getStringExtra("bledisconn");
                    if (!WatchUtils.isEmpty(bleState) && bleState.equals("bledisconn")) { //断开连接
                        closeLoadingDialog();
                        MyCommandManager.deviceDisconnState = true;
                        MyCommandManager.ADDRESS = null;
                        MyCommandManager.DEVICENAME = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", "");
                        MyApp.getInstance().setMacAddress(null);// 清空全局
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", null);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                }
            }
        }
    };

    /**
     * 节点模式和来电提醒的开关点击事件
     *
     * @param buttonView
     * @param isChecked
     */
    @SuppressLint("InlinedApi")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.watch_message_jiedianSwitch:  //节电模式
                if (watchMessageJiedianSwitch.isPressed()) {
                    if (isChecked) {
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "jiedianstate", "on");
                    } else {
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "jiedianstate", "off");
                    }
                }

                break;
            case R.id.watch_message_callphoneSwitch:    //来电提醒
                if (!watchMessageCallphoneSwitch.isPressed())
                    return;
                boolean isPer = AndPermission.hasPermissions(WatchDeviceActivity.this, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS);
                if (!isPer) {
                    AndPermission.with(WatchDeviceActivity.this)
                            .runtime()
                            .permission(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_SETTINGS)
                            .rationale(new Rationale<List<String>>() {
                                @Override
                                public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                    executor.execute();
                                }
                            })
                            .onGranted(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {//
                                    boolean isPerSucc = AndPermission.hasAlwaysDeniedPermission(WatchDeviceActivity.this, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS);
                                    //Log.e(TAG, "-------isPerSucc=" + isPerSucc);
                                    if (isPerSucc) {
                                        watchMessageCallphoneSwitch.setChecked(true);
                                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "on");
                                    }
                                }
                            })
                            .onDenied(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {
                                   // Log.e(TAG, "-------onDenied=" + data.size());
                                    boolean isPers = AndPermission.hasAlwaysDeniedPermission(WatchDeviceActivity.this, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS);
                                    //Log.e(TAG,"---isPers="+isPers);
                                    if (!isPers) {
                                        watchMessageCallphoneSwitch.setChecked(false);
                                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "off");
                                    }else{
                                        watchMessageCallphoneSwitch.setChecked(true);
                                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "on");
                                    }
                                }
                            })
                            .start();
                } else {
                    if (isChecked) {
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "on");
                    } else {
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "off");
                    }
                }

                break;
        }
    }
}
