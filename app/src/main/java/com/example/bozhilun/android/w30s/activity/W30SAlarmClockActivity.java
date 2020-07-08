package com.example.bozhilun.android.w30s.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.adapters.CommonRecyclerAdapter;
import com.example.bozhilun.android.w30s.adapters.MyViewHolder;
import com.example.bozhilun.android.w30s.bean.W30SAlarmClockBean;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.example.bozhilun.android.R;
import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/12 17:38
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SAlarmClockActivity extends WatchBaseActivity {
    private final int REQUEST_ALARM_CLOCK_NEW = 1;// 新建闹钟的requestCode
    private final int REQUEST_ALARM_CLOCK_EDIT = 2;// 修改闹钟的requestCode
    private final int HANDLER_RESULT = 0x01;//
    private static final long RESULT_TIME = 1000;
    private final int HANDLER_DELETE = 0x02;
    W30SAlarmClockBean w30SAlarmClockBean = new W30SAlarmClockBean();
    //标题头
    @BindView(R.id.bar_titles)
    TextView barTitles;
    //闹钟数量
    @BindView(R.id.text_w30s_alarm_number)
    TextView textW30sAlarmNumber;
    //无闹钟显示
    @BindView(R.id.rela_view)
    RelativeLayout relaView;
    //闹钟列表
    @BindView(R.id.alarm_rec)
    RecyclerView alarmRec;
    @BindView(R.id.image_add_remind)
    ImageView imageAddRemind;
    @BindView(R.id.text_ones)
    TextView textOnes;
    @BindView(R.id.text_twos)
    TextView textTwos;
    private List<W30SAlarmClockBean> allDataList;
    private MyAdapter myAdapter;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_RESULT:
                    mHandler.removeMessages(HANDLER_RESULT);
                    List<W30S_AlarmInfo> w30S_alarmInfos = new ArrayList<>();
                    for (int i = 0; i < allDataList.size(); i++) {
                        W30S_AlarmInfo w30S_alarmInfo = allDataList.get(i).w30AlarmInfoChange(i);
                        w30S_alarmInfos.add(w30S_alarmInfo);
                    }
                    Log.e("=========闹钟对象=======", w30S_alarmInfos.size() + "==" + w30S_alarmInfos.toString());
                    //MyApp.getInstance().getmW30SBLEManage().setAlarm(w30S_alarmInfos);

                    W37DataAnalysis.getW37DataAnalysis().setW37AlarmData(w30S_alarmInfos);


                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                    closeLoadingDialog();
                    break;
                case HANDLER_DELETE:
                    mHandler.removeMessages(HANDLER_DELETE);
                    init();
                    mHandler.sendEmptyMessageDelayed(HANDLER_RESULT, 1500);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock_w30s);
        ButterKnife.bind(this);
        if (!isZh(this)) {
            textOnes.setVisibility(View.GONE);
            textTwos.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.trim().endsWith("zh")
                || language.trim().equals("zh-rCN"))
            return true;
        else
            return false;
    }


    /**
     * 初始化默认设置
     */
    private void init() {
        barTitles.setText(getResources().getString(R.string.alarmclock));
        textW30sAlarmNumber.setVisibility(View.GONE);
        relaView.setVisibility(View.VISIBLE);
        alarmRec.setVisibility(View.GONE);

        //查询数据
        findAlarmClickData();
        //设置Adapter
        setDataAdapter();
    }

    private void setDataAdapter() {
        alarmRec.setLayoutManager(new LinearLayoutManager(W30SAlarmClockActivity.this, LinearLayoutManager.VERTICAL, false));
        myAdapter = new MyAdapter(W30SAlarmClockActivity.this, allDataList, R.layout.b18i_list_alarm_clock_item);
        alarmRec.setAdapter(myAdapter);
        myAdapter.setmOnItemListener(new CommonRecyclerAdapter.OnItemListener() {
            @Override
            public void onItemClickListener(View view, int position) {
//                Log.d("-----<<<W30>>>", "======编辑闹钟====onItemClickListener===========" + position + "");
                Intent intent = new Intent(W30SAlarmClockActivity.this, W30STimePicker.class);
                intent.putExtra("type", "change");
                intent.putExtra("ids", String.valueOf(allDataList.get(position).getId()));
                intent.putExtra("hour", String.valueOf(allDataList.get(position).getHour()));
                intent.putExtra("min", String.valueOf(allDataList.get(position).getMin()));
                intent.putExtra("datas", String.valueOf(allDataList.get(position).getDatas()));
                intent.putExtra("alarmWeek", allDataList.get(position).getAlarmWeek());
                // 开启编辑闹钟界面
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
                // 启动移动进入效果动画
                overridePendingTransition(R.anim.move_in_bottom, 0);
            }

            @Override
            public void onLongClickListener(View view, final int position) {
                new MaterialDialog.Builder(W30SAlarmClockActivity.this)
                        .title(getResources().getString(R.string.prompt))
                        .content(getResources().getString(R.string.deleda))
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                showLoadingDialog(getResources().getString(R.string.dlog));
//                                Log.d("-----<<<W30>>>", "=====删除闹钟=====onLongClickListener===========" + position + "");
                                //allDataList.get(position).delete();
                                //allDataList.remove(position);
                                LitePal.deleteAll(W30SAlarmClockBean.class, "id=?", allDataList.get(position).getId() + "");
                                myAdapter.notifyDataSetChanged();
                                mHandler.sendEmptyMessageDelayed(HANDLER_DELETE, RESULT_TIME);
                            }
                        }).show();
            }
        });
    }

    /**
     * 查询数据
     */
    private void findAlarmClickData() {
        if (allDataList != null) allDataList.clear();
        allDataList = LitePal.findAll(W30SAlarmClockBean.class);
        if (myAdapter != null) myAdapter.notifyDataSetChanged();
        if (allDataList != null && allDataList.size() > 0) {
            relaView.setVisibility(View.GONE);
            alarmRec.setVisibility(View.VISIBLE);
        } else {
            relaView.setVisibility(View.VISIBLE);
            alarmRec.setVisibility(View.GONE);
        }
        if (allDataList.size() >= 5) {
            imageAddRemind.setVisibility(View.GONE);
        } else {
            imageAddRemind.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.image_back, R.id.image_add_remind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.image_add_remind:
                Intent intent = new Intent(W30SAlarmClockActivity.this, W30STimePicker.class);
                intent.putExtra("type", "new");
                startActivityForResult(intent, 1);
                break;
        }
    }


    /**
     * 设置返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("============闹钟设置返回", requestCode + "===" + resultCode);
        showLoadingDialog(getResources().getString(R.string.dlog));
        try {
            if (data == null) {
                closeLoadingDialog();
                return;
            }
            Log.d("-----<<<W30>>>", "===返回===" + requestCode + "===" + resultCode);
            allDataList = LitePal.findAll(W30SAlarmClockBean.class);
            int h = data.getIntExtra("h", 12);
            int m = data.getIntExtra("m", 12);
            int week = data.getIntExtra("c", 0);
            boolean b = alarmClickIsNull(h, m, week);
            if (b) {
                Toast.makeText(this, getResources().getString(R.string.string_clock_setting) + "error", Toast.LENGTH_SHORT).show();
                return;
            }
            String stringWeek = StringWeek(week);
            Log.d("-----<<<W30>>>", "===开关，周一  ~~  周日===" +week+"==="+ stringWeek+"==="+W30BasicUtils.toD(stringWeek, 2));
            //二进制转十进制
            int integerWeek = Integer.parseInt(W30BasicUtils.toD(stringWeek, 2));
            Log.d("-----<<<W30>>>", "===integerWeek===" + integerWeek);
            switch (requestCode) {
                case REQUEST_ALARM_CLOCK_NEW:  //=========添加
                    int ID = 1;
                    if (allDataList != null) ID = allDataList.size() + 1;
//                    if (allDataList.size() > 0) {
//                        ID = (allDataList.get(allDataList.size() - 1).getId()) + 1;
//                    }
                    /**
                     private int id;
                     private int hour;
                     private int min;
                     private int datas;
                     private int alarmWeek;
                     private int status;
                     */
                    w30SAlarmClockBean = new W30SAlarmClockBean();
                    w30SAlarmClockBean.setId(ID);
                    w30SAlarmClockBean.setHour(h);
                    w30SAlarmClockBean.setMin(m);
                    w30SAlarmClockBean.setDatas(integerWeek);
                    w30SAlarmClockBean.setAlarmWeek(week);
                    w30SAlarmClockBean.setStatus(1);
                    w30SAlarmClockBean.save();
                    break;
                case REQUEST_ALARM_CLOCK_EDIT:  //========修改
                    int ids = Integer.parseInt(data.getStringExtra("ids"));
                    w30SAlarmClockBean = LitePal.find(W30SAlarmClockBean.class, ids);
                    w30SAlarmClockBean.setId(ids);
                    w30SAlarmClockBean.setHour(h);
                    w30SAlarmClockBean.setMin(m);
                    w30SAlarmClockBean.setStatus(1);
                    w30SAlarmClockBean.setDatas(integerWeek);
                    w30SAlarmClockBean.setAlarmWeek(week);
                    w30SAlarmClockBean.save();
                    break;
            }
            mHandler.sendEmptyMessageDelayed(HANDLER_DELETE, RESULT_TIME);

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public String StringWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
        String[] sateString = {"1", "0", "0", "0", "0", "0", "0", "0"};//开关，周一  ~~  周日
        //weekDay=01100011,//开关，周一  ~~  周日
        //周期
        if ((week & weekArray[0]) == 1) { //周一
            sateString[1] = "1";
        }
        if ((week & weekArray[1]) == 2) { //周二
            sateString[2] = "1";
        }
        if ((week & weekArray[2]) == 4) {  //周三
            sateString[3] = "1";
        }
        if ((week & weekArray[3]) == 8) {  //周四
            sateString[4] = "1";
        }
        if ((week & weekArray[4]) == 16) {  //周五
            sateString[5] = "1";
        }
        if ((week & weekArray[5]) == 32) {  //周六
            sateString[6] = "1";
        }
        if ((week & weekArray[6]) == 64) {   //周日
            sateString[7] = "1";
        }
//        //周期
//        if ((week & weekArray[1]) == 2) { //周一
//            sateString[1] = "1";
//        }
//        if ((week & weekArray[2]) == 4) { //周二
//            sateString[2] = "1";
//        }
//        if ((week & weekArray[3]) == 8) {  //周三
//            sateString[3] = "1";
//        }
//        if ((week & weekArray[4]) == 16) {  //周四
//            sateString[4] = "1";
//        }
//        if ((week & weekArray[5]) == 32) {  //周五
//            sateString[5] = "1";
//        }
//        if ((week & weekArray[6]) == 64) {  //周六
//            sateString[6] = "1";
//        }
//        if ((week & weekArray[0]) == 1) {   //周日
//            sateString[7] = "1";
//        }
        String stringData = "";
        String stringDatas = Arrays.toString(sateString);
        //Log.d("-----<<<W30>>>", "===stringDatas===" + stringDatas);
        String stringDatas2 = stringDatas.substring(1, stringDatas.length() - 1);
        //Log.d("-----<<<W30>>>", "===stringDatas2===" + stringDatas2);
        String[] split = stringDatas2.trim().split("[,]");
        for (int i = 0; i < split.length; i++) {
            stringData += split[i].trim();
        }
        return stringData;
    }

    /**
     * 判断闹钟是否回复
     *
     * @param h
     * @param m
     * @param week
     */
    private boolean alarmClickIsNull(int h, int m, int week) {
        int alarmClockMsg_h = (int) SharedPreferencesUtils.getParam(W30SAlarmClockActivity.this, "alarmClockMsg_H", 12);
        int alarmClockMsg_m = (int) SharedPreferencesUtils.getParam(W30SAlarmClockActivity.this, "alarmClockMsg_M", 12);
        int alarmClockMsg_week = (int) SharedPreferencesUtils.getParam(W30SAlarmClockActivity.this, "alarmClockMsg_WEEK", 127);

        if (alarmClockMsg_h == h && alarmClockMsg_m == m && alarmClockMsg_week == week) {
            return true;
        } else {
            SharedPreferencesUtils.setParam(W30SAlarmClockActivity.this, "alarmClockMsg_H", h);
            SharedPreferencesUtils.setParam(W30SAlarmClockActivity.this, "alarmClockMsg_M", m);
            SharedPreferencesUtils.setParam(W30SAlarmClockActivity.this, "alarmClockMsg_WEEK", week);
            return false;
        }

    }

    /**
     * rec---适配器
     */
    class MyAdapter extends CommonRecyclerAdapter<W30SAlarmClockBean> {

        public MyAdapter(Context context, List<W30SAlarmClockBean> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(MyViewHolder holder, final W30SAlarmClockBean item) {
            goneWeekType(holder);
            //时间
            int hour = item.getHour();
            int min = item.getMin();
            String h = "00";
            String m = "00";
            if (hour > 9) {
                h = String.valueOf(hour);
            } else {
                h = "0" + hour;
            }
            if (min > 9) {
                m = String.valueOf(min);
            } else {
                m = "0" + min;
            }
            holder.setText(R.id.text_hour, h + ":" + m);

            //状态
            int status = item.getStatus();
            if (status == 1) {
                holder.setChaeck(R.id.switch_alarm, true);
            } else {
                holder.setChaeck(R.id.switch_alarm, false);
            }
            //设置选择的周
            setCycle(holder, item.getAlarmWeek());

            Switch aSwitchView = holder.getView(R.id.switch_alarm);

            aSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    int sta = 1;
                    if (isChecked) {
                        sta = 1;
                    } else {
                        sta = 0;
                    }
                    Log.d("=======W30S======", item.getId() + "==" + item.getHour() + "==" + item.getMin() + "==" + item.getDatas() + "==" + item.getAlarmWeek());
                    w30SAlarmClockBean = LitePal.find(W30SAlarmClockBean.class, item.getId());
                    w30SAlarmClockBean.setId(item.getId());
                    w30SAlarmClockBean.setHour(item.getHour());
                    w30SAlarmClockBean.setMin(item.getMin());
                    w30SAlarmClockBean.setStatus(sta);
                    w30SAlarmClockBean.setDatas(item.getDatas());
                    w30SAlarmClockBean.setAlarmWeek(item.getAlarmWeek());
                    w30SAlarmClockBean.save();
                    mHandler.sendEmptyMessageDelayed(HANDLER_DELETE, RESULT_TIME);
                }
            });
        }

        /**
         * 周期默认
         *
         * @param viewHolder
         */
        private void goneWeekType(MyViewHolder viewHolder) {
            viewHolder.setViewVisibility(R.id.text_type, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_senven, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_one, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_two, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_three, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_four, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_five, View.GONE);
            viewHolder.setViewVisibility(R.id.text_type_six, View.GONE);
        }


        /**
         * 循环模式
         *
         * @param viewHolder
         */
        private void setCycle(MyViewHolder viewHolder, int week) {
            Log.d("-----<<<W30>>>", "==viewHolder=week===" + week);
            int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};
            if (week == 127) {
                viewHolder.setViewVisibility(R.id.text_type, View.VISIBLE);
                viewHolder.setText(R.id.text_type, getResources().getString(R.string.every_time));
                return;
            }
//            if ((week & weekArray[0]) == 1) {   //周日
//                viewHolder.setViewVisibility(R.id.text_type_senven, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_senven, getResources().getString(R.string.sunday));
//            }
//            if ((week & weekArray[1]) == 2) { //周一
//                viewHolder.setViewVisibility(R.id.text_type_one, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_one, getResources().getString(R.string.monday));
//            }
//            if ((week & weekArray[2]) == 4) { //周二
//                viewHolder.setViewVisibility(R.id.text_type_two, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_two, getResources().getString(R.string.tuesday));
//            }
//            if ((week & weekArray[3]) == 8) {  //周三
//                viewHolder.setViewVisibility(R.id.text_type_three, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_three, getResources().getString(R.string.wednesday));
//            }
//            if ((week & weekArray[4]) == 16) {  //周四
//                viewHolder.setViewVisibility(R.id.text_type_four, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_four, getResources().getString(R.string.thursday));
//            }
//            if ((week & weekArray[5]) == 32) {  //周五
//                viewHolder.setViewVisibility(R.id.text_type_five, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_five, getResources().getString(R.string.friday));
//            }
//            if ((week & weekArray[6]) == 64) {  //周六
//                viewHolder.setViewVisibility(R.id.text_type_six, View.VISIBLE);
//                viewHolder.setText(R.id.text_type_six, getResources().getString(R.string.saturday));
//            }

            if ((week & weekArray[0]) == 1) { //周一
                viewHolder.setViewVisibility(R.id.text_type_one, View.VISIBLE);
                viewHolder.setText(R.id.text_type_one, getResources().getString(R.string.monday));
            }
            if ((week & weekArray[1]) == 2) { //周二
                viewHolder.setViewVisibility(R.id.text_type_two, View.VISIBLE);
                viewHolder.setText(R.id.text_type_two, getResources().getString(R.string.tuesday));
            }
            if ((week & weekArray[2]) == 4) {  //周三
                viewHolder.setViewVisibility(R.id.text_type_three, View.VISIBLE);
                viewHolder.setText(R.id.text_type_three, getResources().getString(R.string.wednesday));
            }
            if ((week & weekArray[3]) == 8) {  //周四
                viewHolder.setViewVisibility(R.id.text_type_four, View.VISIBLE);
                viewHolder.setText(R.id.text_type_four, getResources().getString(R.string.thursday));
            }
            if ((week & weekArray[4]) == 16) {  //周五
                viewHolder.setViewVisibility(R.id.text_type_five, View.VISIBLE);
                viewHolder.setText(R.id.text_type_five, getResources().getString(R.string.friday));
            }
            if ((week & weekArray[5]) == 32) {  //周六
                viewHolder.setViewVisibility(R.id.text_type_six, View.VISIBLE);
                viewHolder.setText(R.id.text_type_six, getResources().getString(R.string.saturday));
            }
            if ((week & weekArray[6]) == 64) {   //周日
                viewHolder.setViewVisibility(R.id.text_type_senven, View.VISIBLE);
                viewHolder.setText(R.id.text_type_senven, getResources().getString(R.string.sunday));
            }
        }

    }

}
