package com.example.bozhilun.android.b15p.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PAlarmSetting;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Arrays;
import java.util.List;

public class B15PAlarmAdapter extends BaseAdapter {
    private List<B15PAlarmSetting> dataList;
    private Context mContext;


    /**
     * 闹钟开关回调事件实例
     */
    private AlarmCheckChange mCallBack;

    /**
     * 设置闹钟开关回调事件实例
     */
    public void setChangeCallBack(AlarmCheckChange callBack) {
        this.mCallBack = callBack;
    }

    public B15PAlarmAdapter(List<B15PAlarmSetting> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_alarm_type = view.findViewById(R.id.iv_alarm_type);
            viewHolder.tv_time = view.findViewById(R.id.tv_alarm_time);
            viewHolder.date = view.findViewById(R.id.tv_alarm_date);
            viewHolder.check = view.findViewById(R.id.b30alarmToggleBtn);
            viewHolder.singleImg = view.findViewById(R.id.singleAlarmImg);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        B15PAlarmSetting setting = dataList.get(position);
        int hour = setting.getAlarmHour();
        int minute = setting.getAlarmMinute();
        String hourStr = hour < 10 ? "0" + hour : "" + hour;
        String minuteStr = minute < 10 ? "0" + minute : "" + minute;
        String showTime = hourStr + ":" + minuteStr;
        viewHolder.tv_time.setText(showTime);

//            if(setting.getRepeatStatus().equals("0000000") && !setting.getUnRepeatDate().equals("0000-00-00")){ //单次的闹钟
//                viewHolder.check.setVisibility(View.GONE);
//                viewHolder.singleImg.setVisibility(View.VISIBLE);
//            }else{
        viewHolder.singleImg.setVisibility(View.GONE);
        viewHolder.check.setVisibility(View.VISIBLE);
        viewHolder.check.setImageResource(setting.isOpen() ? R.mipmap.myvp_open : R.mipmap
                .myvp_close);
        viewHolder.iv_alarm_type.setImageResource(R.mipmap.crtur_time_image);
//            }

        String content = WatchUtils.obtainB15PAlarmDate(mContext, StringWeek(setting.week));
        Log.e("====",StringWeek(setting.week)+"     "+setting.week+"   "+content);
        viewHolder.date.setText(content);
//            int sceneIndex = setting.getScene() > 0 && setting.getScene() <= 20 ? setting.getScene()
//                    - 1 : 0;// 做个预防
//            viewHolder.iv_alarm_type.setImageResource(alarmTypeImageList[sceneIndex]);
        viewHolder.check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!isCheck){
                    setCheck(true);
                    B15PAlarmSetting alarm2Setting = dataList.get(position);
                    Log.d("----zza--要改变的-", alarm2Setting.toString());
                    byte[] bytes = byteWeek(alarm2Setting.getWeek());
                    if (mCallBack != null) mCallBack.onCheckChange(alarm2Setting.getAlarmId(),
                            !alarm2Setting.isOpen(),
                            alarm2Setting.getInterval(),
                            bytes,
                            alarm2Setting.getAlarmHour(), alarm2Setting.getAlarmMinute());
                    notifyDataSetChanged();
                }

            }
        });
        return view;
    }

    boolean isCheck = false;

    public void setCheck(boolean check) {
        isCheck = check;
    }

//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what){
//                case 0x01:
//                    if (handler!=null)handler.removeMessages(0x01);
//                    isCheck = false;
//                    break;
//            }
//
//            return false;
//        }
//    });

    public byte[] byteWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
        byte[] sateString = {0, 0, 0, 0, 0, 0, 0, 0};//周一  ~~  周日
        //周期
        if ((week & weekArray[0]) == 1) { //周一
            sateString[0] = 1;
        }
        if ((week & weekArray[1]) == 2) { //周二
            sateString[1] = 1;
        }
        if ((week & weekArray[2]) == 4) {  //周三
            sateString[2] = 1;
        }
        if ((week & weekArray[3]) == 8) {  //周四
            sateString[3] = 1;
        }
        if ((week & weekArray[4]) == 16) {  //周五
            sateString[4] = 1;
        }
        if ((week & weekArray[5]) == 32) {  //周六
            sateString[5] = 1;
        }
        if ((week & weekArray[6]) == 64) {   //周日
            sateString[6] = 1;
        }
        return sateString;
    }

    private class ViewHolder {
        ImageView iv_alarm_type;
        TextView tv_time, date;
        ImageView check, singleImg;
    }

    public String StringWeek(int week) {
        int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};//周一  ~~  周日
//        String[] sateString = {"1", "0", "0", "0", "0", "0", "0", "0"};//开关，周一  ~~  周日
        String[] sateString = {"0", "0", "0", "0", "0", "0", "0"};//周一  ~~  周日
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
     * 闹钟开关回调事件
     */
    public interface AlarmCheckChange {
        void onCheckChange(int id, boolean swit, int interval, byte[] weeks, int hours, int minutes);
    }
}