package com.example.bozhilun.android.b15p.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PAlarmSetting;
import com.example.bozhilun.android.b15p.common.B15PAlarmAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.google.gson.Gson;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.AlarmClock;
import com.tjdL4.tjdmain.contr.L4Command;
import com.tjdL4.tjdmain.utils.L4DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by An on 2018/9/13.
 */

public class B15PDeviceAlarmActivity extends WatchBaseActivity {
    private static final String TAG = "B15PDeviceAlarmActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30addAlarmBtn)
    Button b30addAlarmBtn;
    @BindView(R.id.lv_alarm)
    ListView lv_alarm;
    //    @BindView(R.id.ll_no_alarm_data)
//    LinearLayout ll_no_alarm_data;
    @BindView(R.id.text_al)
    TextView text_al;
    @BindView(R.id.text_al2)
    TextView text_al2;
    @BindView(R.id.text_av)
    View text_av;
    @BindView(R.id.rel_again_read)
    RelativeLayout rel_again_read;


    int alarmID = 0;//新建闹钟的ID

    /**
     * 手环闹钟列表
     */
    private List<B15PAlarmSetting> mAlarmList = new ArrayList<>();
    /**
     * 列表适配器
     */
    private B15PAlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_alarm);
        ButterKnife.bind(this);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(R.string.string_alarm_reminder);


        text_al.setVisibility(View.GONE);
        text_al2.setVisibility(View.GONE);
        text_av.setVisibility(View.GONE);
        b30addAlarmBtn.setEnabled(false);
        initData();
        setListeners();
    }


    @Override
    protected void onResume() {
        super.onResume();
        readAlarmFromBleAll();
    }

    private void initData() {
        alarmAdapter = new B15PAlarmAdapter(mAlarmList, this);
        lv_alarm.setAdapter(alarmAdapter);
        /**
         * 开关实践
         */
        alarmAdapter.setChangeCallBack(new B15PAlarmAdapter.AlarmCheckChange() {
            @Override
            public void onCheckChange(int id, boolean swit, int interval, byte[] weeks, int hours, int minutes) {

                showLoadingDialog(getResources().getString(R.string.menu_settings) + "...");
                setAlarm(id, swit, interval, weeks, hours, minutes);
            }

//            @Override
//            public void onCheckChange(final int position) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////
////
////                    }
////                });
//
//                if (!isCheck){
//                    isCheck = true;
//                    updateAlarm(position);
//                }
//            }
        });
    }

//    boolean isCheck = false;


    /**
     * 设置监听
     */
    private void setListeners() {
        lv_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(B15PDeviceAlarmActivity.this, B15PSettingAlarmActiivty.class);
                B15PAlarmSetting alarm2Setting = mAlarmList.get(i);
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
     * 删除闹钟
     */
    private void showDeleteAlarmDia(final int position) {
        final B15PAlarmSetting alarmSetting = mAlarmList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(B15PDeviceAlarmActivity.this)
                .setTitle(getResources().getString(R.string.string_res_alarm))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoadingDialog(getResources().getString(R.string.string_res_ing));
                        setAlarm(alarmSetting.getAlarmId(), false, 0, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, 0, 0);
                    }
                })
                .setNegativeButton(R.string.cancle, null);
        builder.show();
    }


    /**
     * 设置闹钟
     *
     * @param id
     * @param swit
     * @param interval
     * @param weeks
     * @param hours
     * @param minutes
     */
    void setAlarm(int id, boolean swit, int interval, byte[] weeks, int hours, int minutes) {
        if (mAlarmList != null) mAlarmList.clear();
        //设置数据对象
        AlarmClock.AlarmClockData myAlarmClockData = new AlarmClock.AlarmClockData();
        myAlarmClockData.clockId_int = id;    //clockId_int闹钟序号（id）
        myAlarmClockData.clock_switch = swit ? 1 : 0;   //clock_switch  开关 0关闭1打开
        myAlarmClockData.interval = interval;        //interval  重复间隔
        //周日  一 二 三 四 五 周六    选中 1 未选中0
        myAlarmClockData.weeks = weeks;        //weeks 重复模式
        myAlarmClockData.hours = hours;           //hour   时
        myAlarmClockData.minutes = minutes;       //minute 分
        //设置
        String ret = L4Command.AlarmClockSet(myAlarmClockData);   /*ret  返回值类型在文档最下面*/
        if (Commont.isDebug)Log.e(TAG, " 闹钟设置 " + ret);
        handler.sendEmptyMessageDelayed(0x01, 1000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    //if (handler!=null)handler.removeMessages(0x01);
                    //isCheck = false;
                    alarmAdapter.setCheck(false);
                    alarmAdapter.notifyDataSetChanged();
                    closeLoadingDialog();
                    break;
            }

            return false;
        }
    });

    /**
     * 从手环读取闹钟数据
     */
    private void readAlarmFromBleAll() {
        rel_again_read.setVisibility(View.GONE);
        //MyApp.getInstance().getVpOperateManager().readAlarm2(iBleWriteResponse, alarmDataListener);
        showLoadingDialog(getResources().getString(R.string.string_radeing));
        mAlarmList.clear();
        L4Command.AlarmClockGet(new L4M.BTResultListenr() {
            @Override
            public void On_Result(String TypeInfo, String StrData, Object DataObj) {
                final String tTypeInfo = TypeInfo;
                final String TempStr = StrData;
                final Object TempObj = DataObj;
                if (Commont.isDebug)Log.e(TAG, "inTempStr:" + TempStr);

                if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Commont.isDebug)Log.e(TAG, "=======读取超时");
                            rel_again_read.setVisibility(View.VISIBLE);
                        }
                    });

                    return;
                }
                if (tTypeInfo.equals(L4M.SetAlarmClock) && TempStr.equals(L4M.OK)) {
                    L4Command.AlarmClockGet(null);
                } else if (tTypeInfo.equals(L4M.GetAlarmClock) && TempStr.equals(L4M.Data)) {
                    AlarmClock.AlarmClockData myAlarmClockData = (AlarmClock.AlarmClockData) TempObj;

                    if (myAlarmClockData.clockId_int >= 0 && myAlarmClockData.clockId_int <= 4) {

                        if (Commont.isDebug) Log.e(TAG, "===闹钟数据 "
                                + myAlarmClockData.clockId_int
                                + "  " + myAlarmClockData.hours
                                + "  " + myAlarmClockData.minutes
                                + "  " + myAlarmClockData.week
                                + "  " + myAlarmClockData.weeks
                                + "  " + myAlarmClockData.clock_switch
                                + "  " + myAlarmClockData.interval
                                + "  " + StringWeek(myAlarmClockData.week)
                                + "  " + Arrays.toString(L4DateUtils.getBytes(myAlarmClockData.week)));


                        B15PAlarmSetting alarmSetting = new B15PAlarmSetting();
                        alarmSetting.setAlarmId(myAlarmClockData.clockId_int);
                        alarmSetting.setAlarmHour(myAlarmClockData.hours);
                        alarmSetting.setAlarmMinute(myAlarmClockData.minutes);
                        alarmSetting.setOpen(myAlarmClockData.clock_switch == 1);
                        alarmSetting.setWeek(myAlarmClockData.week);
                        alarmSetting.setInterval(myAlarmClockData.interval);
                        mAlarmList.add(alarmSetting);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mAlarmList.size() >= 5) {
                                //b30addAlarmBtn.setVisibility(View.GONE);
                            } else {
                                //b30addAlarmBtn.setVisibility(View.VISIBLE);
                                if (Commont.isDebug)Log .e(TAG, "=========可以新建的ID是" + mAlarmList.size());
                            }

                            alarmID = mAlarmList.size();


                            b30addAlarmBtn.setEnabled(true);
                            showAllAlarm(mAlarmList);
                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAlarmList != null) mAlarmList.clear();
        if (alarmAdapter != null) alarmAdapter.notifyDataSetChanged();
    }

    public String StringWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
//        String[] sateString = {"1", "0", "0", "0", "0", "0", "0", "0"};//开关，周一  ~~  周日
        String[] sateString = {"0", "0", "0", "0", "0", "0", "0", "0"};//周一  ~~  周日
        //weekDay=01100011,//开关，周一  ~~  周日
        //周期
        if ((week & weekArray[0]) == 1) { //周一
            sateString[0] = "1";
        }
        if ((week & weekArray[1]) == 2) { //周二
            sateString[1] = "1";
        }
        if ((week & weekArray[2]) == 4) {  //周三
            sateString[2] = "1";
        }
        if ((week & weekArray[3]) == 8) {  //周四
            sateString[3] = "1";
        }
        if ((week & weekArray[4]) == 16) {  //周五
            sateString[4] = "1";
        }
        if ((week & weekArray[5]) == 32) {  //周六
            sateString[5] = "1";
        }
        if ((week & weekArray[6]) == 64) {   //周日
            sateString[6] = "1";
        }
        String stringData = "";
        String stringDatas = Arrays.toString(sateString);
        //if (Commont.isDebug)Log.d("-----<<<W30>>>", "===stringDatas===" + stringDatas);
        String stringDatas2 = stringDatas.substring(1, stringDatas.length() - 1);
        //if (Commont.isDebug)Log.d("-----<<<W30>>>", "===stringDatas2===" + stringDatas2);
        String[] split = stringDatas2.trim().split("[,]");
        for (int i = 0; i < split.length; i++) {
            stringData += split[i].trim();
        }
        return stringData;
    }

    /**
     * 显示所有的闹钟数据
     *
     * @param alarmList 手环闹钟数据源
     */
    private void showAllAlarm(List<B15PAlarmSetting> alarmList) {
        if (Commont.isDebug)Log.e(TAG, "-------showAllAlarm=" + alarmList.size());
        if (alarmList == null || alarmList.isEmpty()) {
            rel_again_read.setVisibility(View.VISIBLE);
            lv_alarm.setVisibility(View.GONE);
//            ll_no_alarm_data.setVisibility(View.VISIBLE);
        } else {
            rel_again_read.setVisibility(View.GONE);
//            ll_no_alarm_data.setVisibility(View.GONE);
            lv_alarm.setVisibility(View.VISIBLE);
//            mAlarmList.clear();
//            mAlarmList.addAll(alarmList);
            if (alarmAdapter != null) alarmAdapter.notifyDataSetChanged();
        }
        closeLoadingDialog();
    }


    @OnClick({R.id.commentB30BackImg, R.id.b30addAlarmBtn, R.id.rel_again_read})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg://返回
                finish();
                break;
            case R.id.b30addAlarmBtn://添加闹钟,跳转设置闹钟页面
                Intent intent = new Intent(this, B15PSettingAlarmActiivty.class).putExtra("alarmID", alarmID);
                startActivityForResult(intent, 100);
                break;
            case R.id.rel_again_read:
                //重新读取闹钟
                readAlarmFromBleAll();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == Constant.RESULT_CODE) readAlarmFromBleAll();
    }


//    private static final String TAG = "B15PDeviceAlarmActivity";
//
//    @BindView(R.id.commentB30BackImg)
//    ImageView commentB30BackImg;
//    @BindView(R.id.commentB30TitleTv)
//    TextView commentB30TitleTv;
//    @BindView(R.id.b30addAlarmBtn)
//    Button b30addAlarmBtn;
//    @BindView(R.id.lv_alarm)
//    ListView lv_alarm;
//    @BindView(R.id.ll_no_alarm_data)
//    LinearLayout ll_no_alarm_data;
//    @BindView(R.id.text_al)
//    TextView text_al;
//    @BindView(R.id.text_al2)
//    TextView text_al2;
//    @BindView(R.id.text_av)
//    View text_av;
//
//    int alarmID = 0;//新建闹钟的ID
//
//    /**
//     * 手环闹钟列表
//     */
//    private List<B15PAlarmSetting> mAlarmList = new ArrayList<>();
//    /**
//     * 列表适配器
//     */
//    private B15PAlarmAdapter alarmAdapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_b30_device_alarm);
//        ButterKnife.bind(this);
//        commentB30BackImg.setVisibility(View.VISIBLE);
//        commentB30TitleTv.setText(R.string.string_alarm_reminder);
//
//
//        text_al.setVisibility(View.GONE);
//        text_al2.setVisibility(View.GONE);
//        text_av.setVisibility(View.GONE);
//        b30addAlarmBtn.setEnabled(false);
//        b30addAlarmBtn.setVisibility(View.GONE);
//        initData();
//        setListeners();
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        readAlarmFromBleAll();
//    }
//
//    private void initData() {
//        alarmAdapter = new B15PAlarmAdapter(mAlarmList, this);
//        lv_alarm.setAdapter(alarmAdapter);
//        /**
//         * 开关实践
//         */
//        alarmAdapter.setChangeCallBack(new B15PAlarmAdapter.AlarmCheckChange() {
//            @Override
//            public void onCheckChange(int id, boolean swit, int interval, byte[] weeks, int hours, int minutes) {
//
//                showLoadingDialog(getResources().getString(R.string.menu_settings)+"...");
//                setAlarm(id, swit, interval, weeks, hours, minutes);
//            }
//
////            @Override
////            public void onCheckChange(final int position) {
//////                runOnUiThread(new Runnable() {
//////                    @Override
//////                    public void run() {
//////
//////
//////                    }
//////                });
////
////                if (!isCheck){
////                    isCheck = true;
////                    updateAlarm(position);
////                }
////            }
//        });
//    }
//
////    boolean isCheck = false;
//
//
//    /**
//     * 设置监听
//     */
//    private void setListeners() {
//        lv_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(B15PDeviceAlarmActivity.this, B15PSettingAlarmActiivty.class);
//                B15PAlarmSetting alarm2Setting = mAlarmList.get(i);
//                String param = new Gson().toJson(alarm2Setting);
//                intent.putExtra("param", param);
//                startActivityForResult(intent, 100);// 单击跳编辑闹钟界面
//            }
//        });
//        lv_alarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                showDeleteAlarmDia(i);//长按删除闹钟
//                return true;
//            }
//        });
//
//    }
//
////    /**
////     * 开关闹钟
////     *
////     * @param position 闹钟列表下标
////     */
////    private void updateAlarm(int position) {
////        if (handler!=null)handler.sendEmptyMessage(0x02);
////        if (mAlarmList != null && mAlarmList.size() > 0) {
////            B15PAlarmSetting alarm2Setting = mAlarmList.get(position);
////            Log.d("----zza--要改变的-", alarm2Setting.toString());
////
////            byte[] bytes = byteWeek(alarm2Setting.getWeek());
////            Log.e(TAG, "改变的周期  " + Arrays.toString(bytes));
////            setAlarm(alarm2Setting.getAlarmId(),
////                    !alarm2Setting.isOpen(),
////                    alarm2Setting.getInterval(),
////                    bytes,
////                    alarm2Setting.getAlarmHour(), alarm2Setting.getAlarmMinute());
////        }
////    }
//
////
////    public byte[] byteWeek(int week) {
////        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
////        byte[] sateString = {0, 0, 0, 0, 0, 0, 0, 0};//周一  ~~  周日
////        //周期
////        if ((week & weekArray[0]) == 1) { //周一
////            sateString[0] = 1;
////        }
////        if ((week & weekArray[1]) == 2) { //周二
////            sateString[1] = 1;
////        }
////        if ((week & weekArray[2]) == 4) {  //周三
////            sateString[2] = 1;
////        }
////        if ((week & weekArray[3]) == 8) {  //周四
////            sateString[3] = 1;
////        }
////        if ((week & weekArray[4]) == 16) {  //周五
////            sateString[4] = 1;
////        }
////        if ((week & weekArray[5]) == 32) {  //周六
////            sateString[5] = 1;
////        }
////        if ((week & weekArray[6]) == 64) {   //周日
////            sateString[6] = 1;
////        }
////        return sateString;
////    }
//
//    /**
//     * 删除闹钟
//     */
//    private void showDeleteAlarmDia(final int position) {
//        final B15PAlarmSetting alarmSetting = mAlarmList.get(position);
//        AlertDialog.Builder builder = new AlertDialog.Builder(B15PDeviceAlarmActivity.this)
//                .setTitle(getResources().getString(R.string.string_res_alarm))
//                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        showLoadingDialog(getResources().getString(R.string.string_res_ing));
//                        setAlarm(alarmSetting.getAlarmId(), false, 0, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, 0, 0);
//                    }
//                })
//                .setNegativeButton(R.string.cancle, null);
//        builder.show();
//    }
//
//
//    /**
//     * 设置闹钟
//     *
//     * @param id
//     * @param swit
//     * @param interval
//     * @param weeks
//     * @param hours
//     * @param minutes
//     */
//    void setAlarm(int id, boolean swit, int interval, byte[] weeks, int hours, int minutes) {
//        if (mAlarmList != null) mAlarmList.clear();
//        //设置数据对象
//        AlarmClock.AlarmClockData myAlarmClockData = new AlarmClock.AlarmClockData();
//        myAlarmClockData.clockId_int = id;    //clockId_int闹钟序号（id）
//        myAlarmClockData.clock_switch = swit ? 1 : 0;   //clock_switch  开关 0关闭1打开
//        myAlarmClockData.interval = interval;        //interval  重复间隔
//        //周日  一 二 三 四 五 周六    选中 1 未选中0
//        myAlarmClockData.weeks = weeks;        //weeks 重复模式
//        myAlarmClockData.hours = hours;           //hour   时
//        myAlarmClockData.minutes = minutes;       //minute 分
//        //设置
//        String ret = L4Command.AlarmClockSet(myAlarmClockData);   /*ret  返回值类型在文档最下面*/
//        Log.e(TAG, " 闹钟设置 " + ret);
//        handler.sendEmptyMessageDelayed(0x01,1000);
//    }
//
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what){
//                case 0x01:
//                    //if (handler!=null)handler.removeMessages(0x01);
//                    //isCheck = false;
//                    alarmAdapter.setCheck(false);
//                    alarmAdapter.notifyDataSetChanged();
//                    closeLoadingDialog();
//                    break;
//            }
//
//            return false;
//        }
//    });
//
//    /**
//     * 从手环读取闹钟数据
//     */
//    private void readAlarmFromBleAll() {
//        //MyApp.getInstance().getVpOperateManager().readAlarm2(iBleWriteResponse, alarmDataListener);
//        showLoadingDialog(getResources().getString(R.string.string_radeing));
//        mAlarmList.clear();
//        L4Command.AlarmClockGet(new L4M.BTResultListenr() {
//            @Override
//            public void On_Result(String TypeInfo, String StrData, Object DataObj) {
//                final String tTypeInfo = TypeInfo;
//                final String TempStr = StrData;
//                final Object TempObj = DataObj;
//                Log.e(TAG, "inTempStr:" + TempStr);
//
//                if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
//                    return;
//                }
//                if (tTypeInfo.equals(L4M.SetAlarmClock) && TempStr.equals(L4M.OK)) {
//                    L4Command.AlarmClockGet(null);
//                } else if (tTypeInfo.equals(L4M.GetAlarmClock) && TempStr.equals(L4M.Data)) {
//                    AlarmClock.AlarmClockData myAlarmClockData = (AlarmClock.AlarmClockData) TempObj;
//
//                    if (myAlarmClockData.clockId_int >= 0 && myAlarmClockData.clockId_int <= 4) {
////                        if (myAlarmClockData.clock_switch != 0
////                                || myAlarmClockData.hours != 0
////                                || myAlarmClockData.minutes != 0
////                                || myAlarmClockData.week != 0
////                                || myAlarmClockData.interval != 0) {
////
////                            Log.e(TAG, "===闹钟数据 "
////                                    + myAlarmClockData.clockId_int
////                                    + "  " + myAlarmClockData.hours
////                                    + "  " + myAlarmClockData.minutes
////                                    + "  " + myAlarmClockData.week
////                                    + "  " + myAlarmClockData.weeks
////                                    + "  " + myAlarmClockData.clock_switch
////                                    + "  " + myAlarmClockData.interval
////                                    + "  " + StringWeek(myAlarmClockData.week)
////                                    + "  " + Arrays.toString(L4DateUtils.getBytes(myAlarmClockData.week)));
////
////
////                            B15PAlarmSetting alarmSetting = new B15PAlarmSetting();
////                            alarmSetting.setAlarmId(myAlarmClockData.clockId_int);
////                            alarmSetting.setAlarmHour(myAlarmClockData.hours);
////                            alarmSetting.setAlarmMinute(myAlarmClockData.minutes);
////                            alarmSetting.setOpen(myAlarmClockData.clock_switch == 1);
////                            alarmSetting.setWeek(myAlarmClockData.week);
////                            alarmSetting.setInterval(myAlarmClockData.interval);
////                            mAlarmList.add(alarmSetting);
////                        }
//
//                        Log.e(TAG, "===闹钟数据 "
//                                + myAlarmClockData.clockId_int
//                                + "  " + myAlarmClockData.hours
//                                + "  " + myAlarmClockData.minutes
//                                + "  " + myAlarmClockData.week
//                                + "  " + myAlarmClockData.weeks
//                                + "  " + myAlarmClockData.clock_switch
//                                + "  " + myAlarmClockData.interval
//                                + "  " + StringWeek(myAlarmClockData.week)
//                                + "  " + Arrays.toString(L4DateUtils.getBytes(myAlarmClockData.week)));
//
//
//                        B15PAlarmSetting alarmSetting = new B15PAlarmSetting();
//                        alarmSetting.setAlarmId(myAlarmClockData.clockId_int);
//                        alarmSetting.setAlarmHour(myAlarmClockData.hours);
//                        alarmSetting.setAlarmMinute(myAlarmClockData.minutes);
//                        alarmSetting.setOpen(myAlarmClockData.clock_switch == 1);
//                        alarmSetting.setWeek(myAlarmClockData.week);
//                        alarmSetting.setInterval(myAlarmClockData.interval);
//                        mAlarmList.add(alarmSetting);
//                    }
////                    if (myAlarmClockData.clockId_int == 4) //最多为5个闹钟所以最大值为==4
////                    {
////                        //闹钟设置时间
////                        Log.e(TAG, "=========最多为5个闹钟所以最大值为==4");
////                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mAlarmList.size() >= 5) {
//                                //b30addAlarmBtn.setVisibility(View.GONE);
//                            } else {
//                                //b30addAlarmBtn.setVisibility(View.VISIBLE);
//                                Log.e(TAG, "=========可以新建的ID是" + mAlarmList.size());
//                            }
//
//                            alarmID = mAlarmList.size();
//
//
//                            b30addAlarmBtn.setEnabled(true);
//                            showAllAlarm(mAlarmList);
//                        }
//                    });
//
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mAlarmList != null) mAlarmList.clear();
//        if (alarmAdapter != null) alarmAdapter.notifyDataSetChanged();
//    }
//
//    public String StringWeek(int week) {
//        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
////        String[] sateString = {"1", "0", "0", "0", "0", "0", "0", "0"};//开关，周一  ~~  周日
//        String[] sateString = {"0", "0", "0", "0", "0", "0", "0", "0"};//周一  ~~  周日
//        //weekDay=01100011,//开关，周一  ~~  周日
//        //周期
//        if ((week & weekArray[0]) == 1) { //周一
//            sateString[0] = "1";
//        }
//        if ((week & weekArray[1]) == 2) { //周二
//            sateString[1] = "1";
//        }
//        if ((week & weekArray[2]) == 4) {  //周三
//            sateString[2] = "1";
//        }
//        if ((week & weekArray[3]) == 8) {  //周四
//            sateString[3] = "1";
//        }
//        if ((week & weekArray[4]) == 16) {  //周五
//            sateString[4] = "1";
//        }
//        if ((week & weekArray[5]) == 32) {  //周六
//            sateString[5] = "1";
//        }
//        if ((week & weekArray[6]) == 64) {   //周日
//            sateString[6] = "1";
//        }
//        String stringData = "";
//        String stringDatas = Arrays.toString(sateString);
//        //Log.d("-----<<<W30>>>", "===stringDatas===" + stringDatas);
//        String stringDatas2 = stringDatas.substring(1, stringDatas.length() - 1);
//        //Log.d("-----<<<W30>>>", "===stringDatas2===" + stringDatas2);
//        String[] split = stringDatas2.trim().split("[,]");
//        for (int i = 0; i < split.length; i++) {
//            stringData += split[i].trim();
//        }
//        return stringData;
//    }
//
//    /**
//     * 显示所有的闹钟数据
//     *
//     * @param alarmList 手环闹钟数据源
//     */
//    private void showAllAlarm(List<B15PAlarmSetting> alarmList) {
//        Log.e(TAG, "-------showAllAlarm=" + alarmList.size());
//        if (alarmList == null || alarmList.isEmpty()) {
//            lv_alarm.setVisibility(View.GONE);
//            ll_no_alarm_data.setVisibility(View.VISIBLE);
//        } else {
//            ll_no_alarm_data.setVisibility(View.GONE);
//            lv_alarm.setVisibility(View.VISIBLE);
////            mAlarmList.clear();
////            mAlarmList.addAll(alarmList);
//            if (alarmAdapter != null) alarmAdapter.notifyDataSetChanged();
//        }
//        closeLoadingDialog();
//    }
//
//
//    @OnClick({R.id.commentB30BackImg, R.id.b30addAlarmBtn})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.commentB30BackImg://返回
//                finish();
//                break;
//            case R.id.b30addAlarmBtn://添加闹钟,跳转设置闹钟页面
//                Intent intent = new Intent(this, B15PSettingAlarmActiivty.class).putExtra("alarmID", alarmID);
//                startActivityForResult(intent, 100);
//                break;
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //if (resultCode == Constant.RESULT_CODE) readAlarmFromBleAll();
//    }
//

}