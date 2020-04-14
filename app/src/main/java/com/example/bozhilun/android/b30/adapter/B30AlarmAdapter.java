package com.example.bozhilun.android.b30.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.model.settings.Alarm2Setting;

import java.util.List;

/**
 * B30手环闹钟列表适配器
 */
public class B30AlarmAdapter extends BaseAdapter {

    private List<Alarm2Setting> dataList;
    private Context mContext;
    /**
     * 闹钟开关回调事件实例
     */
    private AlarmCheckChange mCallBack;
    /**
     * 闹钟图标数组,资源ID
     */
    private final int[] alarmTypeImageList = {R.drawable.selected1, R.drawable.selected2,
            R.drawable.selected3, R.drawable.selected4, R.drawable.selected5, R.drawable.selected6,
            R.drawable.selected7, R.drawable.selected8, R.drawable.selected9, R.drawable.selected10,
            R.drawable.selected11, R.drawable.selected12, R.drawable.selected13, R.drawable.selected14,
            R.drawable.selected15, R.drawable.selected16, R.drawable.selected17, R.drawable.selected18,
            R.drawable.selected19, R.drawable.selected20};

    public B30AlarmAdapter(Context context, List<Alarm2Setting> data) {
        mContext = context;
        dataList = data;
    }

    /**
     * 设置闹钟开关回调事件实例
     */
    public void setChangeCallBack(AlarmCheckChange callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_layout, viewGroup,false);
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
        Alarm2Setting setting = dataList.get(position);
        int hour = setting.getAlarmHour();
        int minute = setting.getAlarmMinute();
        String hourStr = hour < 10 ? "0" + hour : "" + hour;
        String minuteStr = minute < 10 ? "0" + minute : "" + minute;
        String showTime = hourStr + ":" + minuteStr;
        viewHolder.tv_time.setText(showTime);

        if(setting.getRepeatStatus().equals("0000000") && !setting.getUnRepeatDate().equals("0000-00-00")){ //单次的闹钟
            viewHolder.check.setVisibility(View.GONE);
            viewHolder.singleImg.setVisibility(View.VISIBLE);
        }else{
            viewHolder.singleImg.setVisibility(View.GONE);
            viewHolder.check.setVisibility(View.VISIBLE);
            viewHolder.check.setImageResource(setting.isOpen() ? R.mipmap.myvp_open : R.mipmap
                    .myvp_close);
        }
        viewHolder.date.setText(WatchUtils.obtainAlarmDate(mContext, setting.getRepeatStatus()));
        int sceneIndex = setting.getScene() > 0 && setting.getScene() <= 20 ? setting.getScene()
                - 1 : 0;// 做个预防
        viewHolder.iv_alarm_type.setImageResource(alarmTypeImageList[sceneIndex]);
        viewHolder.check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mCallBack != null) mCallBack.onCheckChange(position);
            }
        });
        return view;
    }

    private class ViewHolder {
        ImageView iv_alarm_type;
        TextView tv_time, date;
        ImageView check,singleImg;
    }

    /**
     * 闹钟开关回调事件
     */
    public interface AlarmCheckChange {
        void onCheckChange(int position);
    }

}
