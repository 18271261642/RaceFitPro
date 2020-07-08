package com.example.bozhilun.android.b30.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.B30SettingAlarmActiivty;
import com.example.bozhilun.android.b30.adapter.B30AlarmAdapter;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.google.gson.Gson;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.settings.Alarm2Setting;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by An on 2018/9/13.
 */

public class B30DeviceAlarmActivity extends WatchBaseActivity {

    private static final String TAG = "B30DeviceAlarmActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30addAlarmBtn)
    Button b30addAlarmBtn;
    @BindView(R.id.lv_alarm)
    ListView lv_alarm;
    @BindView(R.id.ll_no_alarm_data)
    LinearLayout ll_no_alarm_data;

    /**
     * 手环闹钟列表
     */
    private List<Alarm2Setting> mAlarmList = new ArrayList<>();
    /**
     * 列表适配器
     */
    private B30AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_alarm);
        ButterKnife.bind(this);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.string_alarm_reminder);
        b30addAlarmBtn.setVisibility(View.VISIBLE);

        initData();
        setListeners();


    }


    @Override
    protected void onResume() {
        super.onResume();
        readAlarmFromBleAll();
    }

    private void initData() {
        alarmAdapter = new B30AlarmAdapter(this, mAlarmList);
        lv_alarm.setAdapter(alarmAdapter);
        alarmAdapter.setChangeCallBack(alarmCheckChange);

    }

    /**
     * 设置监听
     */
    private void setListeners() {
        lv_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(B30DeviceAlarmActivity.this, B30SettingAlarmActiivty.class);
                Alarm2Setting alarm2Setting = mAlarmList.get(i);
                String param = new Gson().toJson(alarm2Setting);
                intent.putExtra("param", param);
                startActivityForResult(intent, 100);// 单击跳编辑闹钟界面
            }
        });
        lv_alarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                showDeleteAlarmDia(i);//长按删除闹钟
                return true;
            }
        });

    }

    /**
     * 开关闹钟
     *
     * @param position 闹钟列表下标
     */
    private void updateAlarm(int position) {
        Alarm2Setting alarm2Setting = mAlarmList.get(position);
        Log.d("----zza--要改变的-", alarm2Setting.toString());
        boolean open = alarm2Setting.isOpen();
        alarm2Setting.setOpen(!open);
        MyApp.getInstance().getVpOperateManager().modifyAlarm2(iBleWriteResponse, alarmDataListener, alarm2Setting);
    }

    /**
     * 删除闹钟
     */
    private void showDeleteAlarmDia(final int position) {
        final Alarm2Setting alarm2Setting = mAlarmList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(B30DeviceAlarmActivity.this)
                .setTitle(R.string.deleda)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyApp.getInstance().getVpOperateManager().deleteAlarm2(iBleWriteResponse, alarmDataListener, alarm2Setting);
                    }
                })
                .setNegativeButton(R.string.cancle, null);
        builder.show();
    }

    /**
     * 从手环读取闹钟数据
     */
    private void readAlarmFromBleAll() {
        try {
            if(MyCommandManager.DEVICENAME == null)
                return;
            List<Alarm2Setting> readList = MyApp.getInstance().getVpOperateManager().getAlarm2List();
            showAllAlarm(readList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示所有的闹钟数据
     *
     * @param alarmList 手环闹钟数据源
     */
    private void showAllAlarm(List<Alarm2Setting> alarmList) {
        if (alarmList == null || alarmList.isEmpty()) {
            lv_alarm.setVisibility(View.GONE);
            ll_no_alarm_data.setVisibility(View.VISIBLE);
        } else {
            ll_no_alarm_data.setVisibility(View.GONE);
            lv_alarm.setVisibility(View.VISIBLE);
            mAlarmList.clear();
            mAlarmList.addAll(alarmList);
            alarmAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30addAlarmBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg://返回
                finish();
                break;
            case R.id.b30addAlarmBtn://添加闹钟,跳转设置闹钟页面
                Intent intent = new Intent(this, B30SettingAlarmActiivty.class);
                startActivityForResult(intent, 100);
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
            Log.e(TAG,"------i="+i);




        }
    };

    private IAlarm2DataListListener alarmDataListener = new IAlarm2DataListListener() {
        @Override
        public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
            Log.e("B31DeviceActivity","---------读取闹钟返回="+alarmData2.toString());
            showAllAlarm(alarmData2.getAlarm2SettingList());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == Constant.RESULT_CODE) readAlarmFromBleAll();
    }


    private B30AlarmAdapter.AlarmCheckChange alarmCheckChange = new B30AlarmAdapter.AlarmCheckChange() {
        @Override
        public void onCheckChange(int position) {
            updateAlarm(position);
        }
    };

}
