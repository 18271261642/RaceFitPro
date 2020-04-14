package com.example.bozhilun.android.siswatch.h8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.WatchEditAlarmActivity;
import com.example.bozhilun.android.siswatch.bean.AlarmTestBean;
import com.example.bozhilun.android.siswatch.bleus.GetH8FirstAlarmListener;
import com.example.bozhilun.android.siswatch.bleus.ShowSetAlarmResultListener;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * H8闹钟
 */
public class H8AlarmActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {
    private static final int WATCH_EDIT_REQUEST_CODE = 1001;
    private static final String TAG = "H8AlarmActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.WatchAlarmDialogTv)
    TextView WatchAlarmDialogTv;
    //第一个闹钟的时间
    @BindView(R.id.watch_alarm_oneTv)
    TextView watchAlarmOneTv;
    //第一个闹钟是否重复
    @BindView(R.id.firstalarmRepeatTv)
    TextView firstalarmRepeatTv;
    //第一个闹钟周显示
    @BindView(R.id.firstalarmWeekShowTv)
    TextView firstalarmWeekShowTv;
    @BindView(R.id.watch_alarm_oneLin)
    LinearLayout watchAlarmOneLin;
    //第一个闹钟的开关
    @BindView(R.id.watch_alarm_one_switch)
    ToggleButton watchAlarmOneSwitch;
    //第二个闹钟
    @BindView(R.id.watch_alarm_twoTv)
    TextView watchAlarmTwoTv;
    //第二个闹钟是否重复
    @BindView(R.id.secondalarmRepeatTv)
    TextView secondalarmRepeatTv;
    //第二个闹钟的周显示
    @BindView(R.id.secondalarmWeekShowTv)
    TextView secondalarmWeekShowTv;
    @BindView(R.id.watch_alarm_twoLin)
    LinearLayout watchAlarmTwoLin;
    //第二个闹钟的开关
    @BindView(R.id.watch_alarm_two_switch)
    ToggleButton watchAlarmTwoSwitch;
    //第三个闹钟
    @BindView(R.id.watch_alarm_thirdTv)
    TextView watchAlarmThirdTv;
    //第三个闹钟是否重复
    @BindView(R.id.thirdalarmRepeatTv)
    TextView thirdalarmRepeatTv;
    //第三个闹钟的周
    @BindView(R.id.thirdalarmWeekShowTv)
    TextView thirdalarmWeekShowTv;
    @BindView(R.id.watch_alarm_thirdLin)
    LinearLayout watchAlarmThirdLin;
    //第三个闹钟的开关
    @BindView(R.id.watch_alarm_third_switch)
    ToggleButton watchAlarmThirdSwitch;

    @BindView(R.id.watch_test_showView)
    TextView watchTestShowView;
    @BindView(R.id.alarmSwipeRefresh)
    SwipeRefreshLayout alarmSwipeRefresh;


    private List<AlarmTestBean> alarmTestBeen;
    String alarmrepeat; //重复
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};
    Map<String, String> weekMaps = new HashMap<>();

    int week1repeat;
    int week2repeat;
    int week3repeat;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:  //第一个闹钟返回
                    Log.e(TAG, "----da=" + Arrays.toString((byte[]) msg.obj));
                    byte[] firstData = (byte[]) msg.obj;
                    showFirstAlarm(firstData);
                    break;
                case 0x02:  //第二个闹钟
                    byte[] secondData = (byte[]) msg.obj;
                    showSecondAlarm(secondData);
                    break;
                case 0x03:  //第三个闹钟
                    closeLoadingDialog();
                    byte[] thirdData = (byte[]) msg.obj;
                    showThirdAlarm(thirdData);
                    break;
                case 0x04:
                    getAlarms();
                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_alarm);
        ButterKnife.bind(this);

        initViews();

        getAlarms();


    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    //获取闹钟
    private void getAlarms() {
        if (MyCommandManager.DEVICENAME == null)
            return;

        /**
         * new GetH8FirstAlarmListener() {
         *             @Override
         *             public void getFirstAlarm(byte[] data) {
         *                 Log.e(TAG, "-----第一个闹钟=" + Arrays.toString(data));
         *                 Message message = handler.obtainMessage();
         *                 message.what = 1001;
         *                 message.obj = data;
         *                 handler.sendMessage(message);
         *             }
         *         }
         */
        showLoadingDialog("loading...");
        MyApp.getInstance().h8BleManagerInstance().getH8FirstAlarm(new GetH8FirstAlarmListener() {
            @Override
            public void getFirstAlarm(byte[] data) {
                Log.e(TAG, "-----第一个闹钟=" + Arrays.toString(data));
                Message message = handler.obtainMessage();
                message.obj = data;
                message.what = 0x01;
                handler.sendMessage(message);
            }

            @Override
            public void getSecondAlarm(byte[] data) {
                Log.e(TAG, "-----第2个闹钟=" + Arrays.toString(data));
                Message message = handler.obtainMessage();
                message.obj = data;
                message.what = 0x02;
                handler.sendMessage(message);
            }

            @Override
            public void getThirdAlarm(byte[] data) {
                Log.e(TAG, "-----第3个闹钟=" + Arrays.toString(data));
                Message message = handler.obtainMessage();
                message.obj = data;
                message.what = 0x03;
                handler.sendMessage(message);
            }
        });
    }


    //显示第一个闹钟
    private void showFirstAlarm(byte[] firstData) {
        watchAlarmOneTv.setText(getReorganizationDate(firstData));
        //是否重复
        int repeatDta = Customdata.hexStringToAlgorism(Customdata.byteToHex(firstData[8]));
        week1repeat = repeatDta;
        watchAlarmOneSwitch.setChecked(getSwitchOn(firstData[9]));
        vertRepeatData(repeatDta, 1);

    }

    //第二个闹钟
    private void showSecondAlarm(byte[] secondData) {
        watchAlarmTwoTv.setText(getReorganizationDate(secondData));
        //是否重复
        int repeatDta = Customdata.hexStringToAlgorism(Customdata.byteToHex(secondData[8]));
        week2repeat = repeatDta;
        watchAlarmTwoSwitch.setChecked(getSwitchOn(secondData[9]));
        vertRepeatData(repeatDta, 2);
    }


    //显示第三个闹钟
    private void showThirdAlarm(byte[] thirdData) {
        watchAlarmThirdTv.setText(getReorganizationDate(thirdData));
        //是否重复
        int repeatDta = Customdata.hexStringToAlgorism(Customdata.byteToHex(thirdData[8]));
        //开关
        int switchs = thirdData[9];
        watchAlarmThirdSwitch.setChecked(getSwitchOn(switchs));
        week3repeat = repeatDta;
        vertRepeatData(repeatDta, 3);

    }

    //开关是否打开
    private boolean getSwitchOn(int v) {
        if (v == 1) {   //1开;0关
            return true;
        } else {
            return false;
        }
    }


    //返回重组的时间
    private String getReorganizationDate(byte[] data) {
        int whour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
        int wmine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
        String newwhour, newwmine;
        if (whour <= 9) {
            newwhour = String.valueOf(0) + String.valueOf(whour);
        } else {
            newwhour = whour + "";
        }
        if (wmine <= 9) {
            newwmine = String.valueOf(0) + String.valueOf(wmine);
        } else {
            newwmine = wmine + "";
        }

        return newwhour + ":" + newwmine;
    }


    //判断是否重复和周
    private void vertRepeatData(int repeat, int num) {
        alarmTestBeen = new ArrayList<>();
        Collections.sort(alarmTestBeen, new Comparator<AlarmTestBean>() {
            @Override
            public int compare(AlarmTestBean demobean, AlarmTestBean t1) {

                return demobean.getIdnum().compareTo(t1.getIdnum());
            }
        });
        int weekRepeat = repeat;
        Log.e("闹钟", "---weekRepeat--" + weekRepeat);
        Log.e("闹钟", "--周--111=" + weekMaps.toString());
        if (weekRepeat > 128) {   //大于128说明是重复
            int newWeekRepeat = weekRepeat - 128;
            alarmrepeat = getResources().getString(R.string.repeat);
            if ((newWeekRepeat & weekArray[0]) == 1) {   //周日
                weekMaps.put("week1", getResources().getString(R.string.sunday));
                alarmTestBeen.add(new AlarmTestBean(1, getResources().getString(R.string.sunday)));
            }
            if ((newWeekRepeat & weekArray[1]) == 2) { //周一
                weekMaps.put("week2", getResources().getString(R.string.monday));
                alarmTestBeen.add(new AlarmTestBean(2, getResources().getString(R.string.monday)));
            }
            if ((newWeekRepeat & weekArray[2]) == 4) { //周二
                weekMaps.put("week3", getResources().getString(R.string.tuesday));
                alarmTestBeen.add(new AlarmTestBean(4, getResources().getString(R.string.tuesday)));
            }
            if ((newWeekRepeat & weekArray[3]) == 8) {  //周三
                weekMaps.put("week4", getResources().getString(R.string.wednesday));
                alarmTestBeen.add(new AlarmTestBean(8, getResources().getString(R.string.wednesday)));
            }
            if ((newWeekRepeat & weekArray[4]) == 16) {  //周四
                weekMaps.put("week5", getResources().getString(R.string.thursday));
                alarmTestBeen.add(new AlarmTestBean(16, getResources().getString(R.string.thursday)));
            }
            if ((newWeekRepeat & weekArray[5]) == 32) {  //周五
                weekMaps.put("week6", getResources().getString(R.string.friday));
                alarmTestBeen.add(new AlarmTestBean(32, getResources().getString(R.string.friday)));
            }
            if ((newWeekRepeat & weekArray[6]) == 64) {  //周六
                weekMaps.put("week7", getResources().getString(R.string.saturday));
                alarmTestBeen.add(new AlarmTestBean(64, getResources().getString(R.string.saturday)));
            }
        } else {    //不重复
            alarmrepeat = "";
            if ((weekRepeat & weekArray[0]) == 1) {   //周日
                weekMaps.put("week1", getResources().getString(R.string.sunday));
                alarmTestBeen.add(new AlarmTestBean(1, getResources().getString(R.string.sunday)));
            }
            if ((weekRepeat & weekArray[1]) == 2) { //周一
                weekMaps.put("week2", getResources().getString(R.string.monday));
                alarmTestBeen.add(new AlarmTestBean(2, getResources().getString(R.string.monday)));
            }
            if ((weekRepeat & weekArray[2]) == 4) { //周二
                weekMaps.put("week3", getResources().getString(R.string.tuesday));
                alarmTestBeen.add(new AlarmTestBean(4, getResources().getString(R.string.tuesday)));
            }
            if ((weekRepeat & weekArray[3]) == 8) {  //周三
                weekMaps.put("week4", getResources().getString(R.string.wednesday));
                alarmTestBeen.add(new AlarmTestBean(8, getResources().getString(R.string.wednesday)));
            }
            if ((weekRepeat & weekArray[4]) == 16) {  //周四
                weekMaps.put("week5", getResources().getString(R.string.thursday));
                alarmTestBeen.add(new AlarmTestBean(16, getResources().getString(R.string.thursday)));
            }
            if ((weekRepeat & weekArray[5]) == 32) {  //周五
                weekMaps.put("week6", getResources().getString(R.string.friday));
                alarmTestBeen.add(new AlarmTestBean(32, getResources().getString(R.string.friday)));
            }
            if ((weekRepeat & weekArray[6]) == 64) {  //周六
                weekMaps.put("week7", getResources().getString(R.string.saturday));
                alarmTestBeen.add(new AlarmTestBean(64, getResources().getString(R.string.saturday)));
            }

        }

        if (num == 1) {   //第一个闹钟
            if (weekRepeat == 255) {  //每天重复
                firstalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                firstalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    firstalarmWeekShowTv.setText(getResources().getString(R.string.every_time));
                } else {
                    firstalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                firstalarmRepeatTv.setText(alarmrepeat + "");

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        } else if (num == 2) { //第二个闹钟
            if (weekRepeat == 255) {  //每天重复
                secondalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                secondalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    secondalarmWeekShowTv.setText(getResources().getString(R.string.every_time));   //每天
                } else {
                    secondalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                secondalarmRepeatTv.setText(alarmrepeat + "");
                //secondalarmWeekShowTv.setText(getWeekMap(weekMaps));

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        } else {  //第三个闹钟
            if (weekRepeat == 255) {  //每天重复
                thirdalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                thirdalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    thirdalarmWeekShowTv.setText(getResources().getString(R.string.every_time));
                } else {
                    thirdalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                thirdalarmRepeatTv.setText(alarmrepeat + "");
                //thirdalarmWeekShowTv.setText(getWeekMap(weekMaps));

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        }

    }


    private String getListData(List<AlarmTestBean> alarmTestBeen) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < alarmTestBeen.size(); i++) {
            sb.append(alarmTestBeen.get(i).getWeeks());
            sb.append(",");
        }
        if (sb.toString().length() > 1) {
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return null;
        }

    }

    private void initViews() {
        alarmSwipeRefresh.setEnabled(false);
        tvTitle.setText(getResources().getString(R.string.alarmclock));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        watchAlarmOneSwitch.setOnCheckedChangeListener(this);
        watchAlarmTwoSwitch.setOnCheckedChangeListener(this);
        watchAlarmThirdSwitch.setOnCheckedChangeListener(this);
    }


    @OnClick({R.id.watch_test_showView, R.id.watch_alarm_oneLin, R.id.watch_alarm_twoLin, R.id.watch_alarm_thirdLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_alarm_oneLin:
                Intent intent = new Intent(H8AlarmActivity.this, WatchEditAlarmActivity.class);
                intent.putExtra("alarmTag", "alarm1");
                intent.putExtra("times", watchAlarmOneTv.getText().toString().trim());
                intent.putExtra("wekrepeat", alarmrepeat);
                intent.putExtra("weekrepeat", week1repeat);
                startActivityForResult(intent, WATCH_EDIT_REQUEST_CODE);
                break;
            case R.id.watch_alarm_twoLin:
                Intent intents = new Intent(H8AlarmActivity.this, WatchEditAlarmActivity.class);
                intents.putExtra("alarmTag", "alarm2");
                intents.putExtra("times", watchAlarmTwoTv.getText().toString().trim());
                intents.putExtra("wekrepeat", alarmrepeat);
                intents.putExtra("weekrepeat", week2repeat);
                startActivityForResult(intents, WATCH_EDIT_REQUEST_CODE);
                break;
            case R.id.watch_alarm_thirdLin:
                Intent intent3 = new Intent(H8AlarmActivity.this, WatchEditAlarmActivity.class);
                intent3.putExtra("alarmTag", "alarm3");
                intent3.putExtra("times", watchAlarmThirdTv.getText().toString().trim());
                intent3.putExtra("wekrepeat", alarmrepeat);
                intent3.putExtra("weekrepeat", week3repeat);
                startActivityForResult(intent3, WATCH_EDIT_REQUEST_CODE);
                break;
        }
    }

    /**
     * 设置闹钟的开关
     *
     * @param code    闹钟下标
     * @param hour    小时
     * @param mine    分钟
     * @param switchs 开关 1开；0关
     * @param week    周
     */
    private void setAlarmOnOrOff(int code, int hour, int mine, int week, int switchs) {
        //Log.e(TAG,"----code="+code+"--hour="+hour+"--mine="+mine+"--week="+week+"--switch="+switchs);
        if (MyCommandManager.DEVICENAME == null)
            return;
        MyApp.getInstance().h8BleManagerInstance().setH8Alarm(code, hour, mine, week,switchs ,new ShowSetAlarmResultListener() {
            @Override
            public void alarmResultData(boolean result) {
                Log.e(TAG, "----设置闹钟是否成功==" + result);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WATCH_EDIT_REQUEST_CODE) {
            if (data != null) {
                handler.sendEmptyMessage(0x04);
            }
        }
    }

    //开关
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.watch_alarm_one_switch:   //第一个闹钟
                if (buttonView.isPressed()) {
                    String wtime = watchAlarmOneTv.getText().toString().trim();
                    int switchs;
                    int hour = Integer.valueOf(StringUtils.substringBefore(wtime, ":"));
                    int mine = Integer.valueOf(StringUtils.substringAfter(wtime, ":"));
                    if (isChecked) {
                        switchs = 1;
                    } else {
                        switchs = 0;
                    }
                    setAlarmOnOrOff(1, hour, mine, week1repeat, switchs);

                }

                break;
            case R.id.watch_alarm_two_switch:   //第二个闹钟
                if (buttonView.isPressed()) {
                    String wtime2 = watchAlarmTwoTv.getText().toString().trim();
                    int switchs2;
                    int hours = Integer.valueOf(StringUtils.substringBefore(wtime2, ":"));
                    int mines = Integer.valueOf(StringUtils.substringAfter(wtime2, ":"));
                    if (isChecked) {
                        switchs2 = 1;
                    } else {
                        switchs2 = 0;
                    }
                    setAlarmOnOrOff(2, hours, mines, week2repeat, switchs2);
                }

                break;
            case R.id.watch_alarm_third_switch:   //第三个闹钟
                if (buttonView.isPressed()) {
                    String wtimes = watchAlarmThirdTv.getText().toString().trim();
                    int switchs3;
                    int hour3 = Integer.valueOf(StringUtils.substringBefore(wtimes, ":"));
                    int mine3 = Integer.valueOf(StringUtils.substringAfter(wtimes, ":"));
                    if (isChecked) {
                        switchs3 = 1;
                    } else {
                        switchs3 = 0;
                    }
                    setAlarmOnOrOff(3, hour3, mine3, week3repeat, switchs3);
                }

                break;
        }
    }

}
