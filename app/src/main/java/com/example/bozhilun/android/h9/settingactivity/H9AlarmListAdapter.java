package com.example.bozhilun.android.h9.settingactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.AlarmTestBean;
import com.sdk.bluetooth.bean.RemindData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;

import java.util.ArrayList;
import java.util.List;

public class H9AlarmListAdapter extends BaseAdapter {
    List<RemindData> remindDataList;//闹钟数据源
    private LayoutInflater mInflater;//布局装载器对象
    private Context mContext;

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public H9AlarmListAdapter(Context context, List<RemindData> remindDataList) {
        this.remindDataList = remindDataList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return remindDataList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return remindDataList.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    //返回每一项的显示内容
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        alarmTestBeen = new ArrayList<>();
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
            convertView = mInflater.inflate(R.layout.h9_alarm_list_item, null);
            //对viewHolder的属性进行赋值
            viewHolder.image_type = (ImageView) convertView.findViewById(R.id.image_type);
            viewHolder.text_alarm_times = (TextView) convertView.findViewById(R.id.text_alarm_times);
            viewHolder.text_alarm_cycle = (TextView) convertView.findViewById(R.id.text_alarm_cycle);
            viewHolder.switch_alarm = (Switch) convertView.findViewById(R.id.switch_alarm);
            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出bean对象
        RemindData bean = remindDataList.get(position);

        //类型
        int type = bean.remind_type;
        setImageType(viewHolder, type);

        //时间
        viewHolder.text_alarm_times.setText((bean.remind_time_hours > 9 ? bean.remind_time_hours : "0" + bean.remind_time_hours)
                + ":" + (bean.remind_time_minutes > 9 ? bean.remind_time_minutes : "0" + bean.remind_time_minutes));

        //周期
        int week = Integer.parseInt(B18iUtils.toD(bean.remind_week, 2));
        Log.d("====", "星期值=====" + week);
        setAlarmCycle(viewHolder, week);

        //开关
        if (bean.remind_set_ok == 0) {// 0 关
            viewHolder.switch_alarm.setChecked(false);
        } else {    // 1 开
            viewHolder.switch_alarm.setChecked(true);
        }

        viewHolder.switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("--------开关：", compoundButton.isPressed() + "");
                if (compoundButton.isPressed()) {
                    switchListenter.OnSwitchListenter(b,position);
                }
            }
        });
        return convertView;
    }

    /**
     * 设置闹钟周期
     *
     * @param viewHolder
     * @param week
     */
    List<AlarmTestBean> alarmTestBeen;
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};

    private void setAlarmCycle(ViewHolder viewHolder, int week) {
        if ((week & weekArray[0]) == 1) {  //周一
            alarmTestBeen.add(new AlarmTestBean(1, mContext.getResources().getString(R.string.monday)));
        }
        if ((week & weekArray[1]) == 2) { //周二
            alarmTestBeen.add(new AlarmTestBean(2, mContext.getResources().getString(R.string.tuesday)));
        }
        if ((week & weekArray[2]) == 4) { //周三
            alarmTestBeen.add(new AlarmTestBean(4, mContext.getResources().getString(R.string.wednesday)));
        }
        if ((week & weekArray[3]) == 8) {  //周四
            alarmTestBeen.add(new AlarmTestBean(8, mContext.getResources().getString(R.string.thursday)));
        }
        if ((week & weekArray[4]) == 16) {  //周五
            alarmTestBeen.add(new AlarmTestBean(16, mContext.getResources().getString(R.string.friday)));
        }
        if ((week & weekArray[5]) == 32) {  //周六
            alarmTestBeen.add(new AlarmTestBean(32, mContext.getResources().getString(R.string.saturday)));
        }
        if ((week & weekArray[6]) == 64) {   //周日
            alarmTestBeen.add(new AlarmTestBean(64, mContext.getResources().getString(R.string.sunday)));
        }
        viewHolder.text_alarm_cycle.setText(getTestData(alarmTestBeen));
        alarmTestBeen.clear();
    }


    private String getTestData(List<AlarmTestBean> alarmTestBeen) {
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


    private SwitchListenter switchListenter;


    public void setSwitchListenter(SwitchListenter switchListenter) {
        this.switchListenter = switchListenter;
    }

    public interface SwitchListenter{
        void OnSwitchListenter(boolean isChecked,int postion);
    }

    /**
     * 根据状态设置闹钟的类型
     *
     * @param holder
     * @param remind_type
     */
    private void setImageType(ViewHolder holder, int remind_type) {
        if (remind_type == 0) {
            holder.image_type.setImageResource(R.mipmap.eat);
        } else if (remind_type == 1) {
            holder.image_type.setImageResource(R.mipmap.medicine);
        } else if (remind_type == 2) {
            holder.image_type.setImageResource(R.mipmap.dring);
        } else if (remind_type == 3) {
            holder.image_type.setImageResource(R.mipmap.sp);
        } else if (remind_type == 4) {
            holder.image_type.setImageResource(R.mipmap.awakes);
        } else if (remind_type == 5) {
            holder.image_type.setImageResource(R.mipmap.spr);
        } else if (remind_type == 6) {
            holder.image_type.setImageResource(R.mipmap.metting);
        } else if (remind_type == 7) {
            holder.image_type.setImageResource(R.mipmap.custom);
        }
    }


    // ViewHolder用于缓存控件，三个属性分别对应item布局文件的三个控件
    class ViewHolder {
        public ImageView image_type;
        public TextView text_alarm_times;
        public TextView text_alarm_cycle;
        public Switch switch_alarm;
    }
}
