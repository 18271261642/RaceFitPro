package com.example.bozhilun.android.h9.settingactivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.google.gson.Gson;
import com.sdk.bluetooth.bean.RemindData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;
import com.sdk.bluetooth.protocol.command.expands.RemindCount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class H9AlarmClockActivity extends WatchBaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, H9AlarmListAdapter.SwitchListenter {

    private static final String TAG = "H9AlarmClockActivity";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.prompt_layout_text)
    LinearLayout promptLayoutText;
    @BindView(R.id.list_alarm)
    ListView listAlarm;
    @BindView(R.id.btn_new_remind)
    Button btnNewRemind;
    private final int REQUEST_ALARM_CLOCK_NEW = 1;// 新建闹钟的requestCode
    private final int REQUEST_ALARM_CLOCK_EDIT = 2;// 修改旧闹钟的requestCode

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_alarm_clock_layout);
        ButterKnife.bind(this);

        barTitles.setText(getResources().getString(R.string.alarmclock));

        initView();
    }

    private void initView() {
        listAlarm.setOnItemClickListener(this);
        listAlarm.setOnItemLongClickListener(this);
    }

    @OnClick({R.id.image_back, R.id.btn_new_remind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_new_remind:
                Intent intent = new Intent(H9AlarmClockActivity.this, EditAddAlarmActivity.class);
                intent.putExtra("alarmType", "NEW");
                // 开启新建闹钟界面
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_NEW);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        readAlarmClock();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showLoadingDialog(getResources().getString(R.string.dlog));
        readAlarmClock();
//        switch (requestCode) {
//            case REQUEST_ALARM_CLOCK_NEW://添加新的闹钟
//                readAlarmClock();
//                break;
//            case REQUEST_ALARM_CLOCK_EDIT://修改旧的闹钟
//                readAlarmClock();
//                break;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getInstance()).clearCommandBlockingDeque();
    }

    /**
     * 读取闹钟数据
     */
    void readAlarmClock() {
        showLoadingDialog(getResources().getString(R.string.dlog));
        AppsBluetoothManager.getInstance(H9AlarmClockActivity.this)
                .sendCommand(new RemindCount(commandResultCallback, 1, 0));
    }

    List<RemindData> remindDataList = null;
    H9AlarmListAdapter alarmListAdapter = null;
    /**
     * 设备执行返回
     */
    BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof RemindCount) {
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置

                } else if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                    int remindCount = GlobalVarManager.getInstance().getRemindCount();
                    Log.d(TAG, "读取的闹钟的长度为：" + remindCount);
                    AppsBluetoothManager.getInstance(H9AlarmClockActivity.this)
                            .sendCommand(new RemindSetting(commandResultCallback, GlobalVarManager.getInstance().getRemindCount()));
                    setTipShow(remindCount);
                }
            } else if (baseCommand instanceof RemindSetting) {
                closeLoadingDialog();
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置
                    readAlarmClock();
                } else if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                    //查询到了闹钟的详细数据
                    remindDataList = GlobalDataManager.getInstance().getRemindDatas();
                    alarmListAdapter = new H9AlarmListAdapter(H9AlarmClockActivity.this, remindDataList);
                    alarmListAdapter.setSwitchListenter(H9AlarmClockActivity.this);
                    listAlarm.setAdapter(alarmListAdapter);
                    alarmListAdapter.notifyDataSetChanged();
                }


            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };


    /**
     * 读取闹钟成功，根据读取的长度设置闹钟界面提示i是否显示
     *
     * @param remindCount
     */
    private void setTipShow(int remindCount) {
        if (remindCount <= 0) {
            promptLayoutText.setVisibility(View.VISIBLE);//提示显示
            listAlarm.setVisibility(View.GONE);//闹钟列表隐藏
        } else {
            promptLayoutText.setVisibility(View.GONE);
            listAlarm.setVisibility(View.VISIBLE);
        }
        //最多七条数据，达到目标后不能再添加
        if (remindCount >= 7) {
            btnNewRemind.setVisibility(View.GONE);
        } else {
            btnNewRemind.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 列表点击
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (remindDataList != null && !remindDataList.isEmpty()) {
            RemindData remindData = remindDataList.get(i);
            if (remindData == null) return;
            Intent intent = new Intent(H9AlarmClockActivity.this, EditAddAlarmActivity.class);
            intent.putExtra("alarmType", "EDIT");
            intent.putExtra("remindData", new Gson().toJson(remindData));
            // 开启新建闹钟界面
            startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
        }
    }

    /**
     * 列表长按
     *
     * @param view
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (remindDataList != null && !remindDataList.isEmpty()) {
            RemindData remindData = remindDataList.get(i);
            if (remindData != null) {
                final int editHour = remindData.remind_time_hours;
                final int editMinutes = remindData.remind_time_minutes;
                final int editType = remindData.remind_type;
                final int alarmCycles = setWeek(remindData);
                final int alarmSwith = remindData.remind_set_ok;//闹钟开关
                new CommomDialog(H9AlarmClockActivity.this, R.style.dialog,
                        getResources().getString(R.string.string_delete_alarm),
                        new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    showLoadingDialog(getResources().getString(R.string.dlog));
                                    AppsBluetoothManager.getInstance(H9AlarmClockActivity.this)
                                            .sendCommand(new RemindSetting(commandResultCallback,
                                                    (byte) 0x02, (byte) editType, (byte) editHour, (byte) editMinutes,
                                                    (byte) alarmCycles, (byte) alarmSwith));
                                }
                                dialog.dismiss();
                            }
                        }).setTitle(getResources().getString(R.string.prompt)).show();
            }
        }
        return true;
    }


    /**
     * 周期计算
     *
     * @param remindData
     */
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};

    private int setWeek(RemindData remindData) {
        int alarmCycles = 0;
        //周期
        int week = Integer.parseInt(B18iUtils.toD(remindData.remind_week, 2));
        if ((week & weekArray[0]) == 1) {   //周日
            alarmCycles += 1;
        }
        if ((week & weekArray[1]) == 2) { //周一
            alarmCycles += 2;
        }
        if ((week & weekArray[2]) == 4) { //周二
            alarmCycles += 4;
        }
        if ((week & weekArray[3]) == 8) {  //周三
            alarmCycles += 8;
        }
        if ((week & weekArray[4]) == 16) {  //周四
            alarmCycles += 16;
        }
        if ((week & weekArray[5]) == 32) {  //周五
            alarmCycles += 32;
        }
        if ((week & weekArray[6]) == 64) {  //周六
            alarmCycles += 64;
        }

        return alarmCycles;
    }

    /**
     * 闹钟开关
     *
     * @param isChecked
     * @param postion
     */
    @Override
    public void OnSwitchListenter(boolean isChecked, int postion) {
        if (remindDataList != null && !remindDataList.isEmpty()) {
            RemindData remindData = remindDataList.get(postion);
            if (remindData != null) {
                showLoadingDialog(getResources().getString(R.string.dlog));
                final int editHour = remindData.remind_time_hours;
                final int editMinutes = remindData.remind_time_minutes;
                final int editType = remindData.remind_type;
                final int alarmCycles = setWeek(remindData);
                final int alarmSwith = remindData.remind_set_ok;//闹钟开关
                if (isChecked) {
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    11,
                                    (byte) 0x01,//修改
                                    (byte) editType,//类型：。。。"吃饭", "吃药", "喝水。。。
                                    (byte) editHour,//时
                                    (byte) editMinutes,//分
                                    (byte) alarmCycles,//周期
                                    (byte) alarmSwith,
                                    new byte[]{}, (byte) editType, (byte) editHour, (byte) editMinutes, (byte) alarmCycles, (byte) 0x01));//0关、1开
                } else {
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    11,
                                    (byte) 0x01,//修改
                                    (byte) editType,//类型：。。。"吃饭", "吃药", "喝水。。。
                                    (byte) editHour,//时
                                    (byte) editMinutes,//分
                                    (byte) alarmCycles,//周期
                                    (byte) alarmSwith,
                                    new byte[]{}, (byte) editType, (byte) editHour, (byte) editMinutes, (byte) alarmCycles, (byte) 0x00));//0关、1开
                }
            }
        }


    }
}
