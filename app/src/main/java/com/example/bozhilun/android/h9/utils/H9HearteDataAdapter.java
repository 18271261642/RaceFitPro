package com.example.bozhilun.android.h9.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.bean.HeartDataBean;

import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/11/1 08:51
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HearteDataAdapter extends BaseAdapter {
    private Context mContext;
    List<HeartDataBean.HeartRateBean> heartDataList;

    public H9HearteDataAdapter(Context mContext, List<HeartDataBean.HeartRateBean> heartDataList) {
        this.mContext = mContext;
        this.heartDataList = heartDataList;
    }

    @Override
    public int getCount() {
        Log.d("=====size====",heartDataList.size()+"");
        return heartDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return heartDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.h9_heartedata_item,parent,false);
            holder = new ViewHolder();
            holder.hearteTime = (TextView) convertView.findViewById(R.id.hearte_time);
            holder.hearteValue = (TextView) convertView.findViewById(R.id.hearte_value);
            holder.text_data = (TextView) convertView.findViewById(R.id.text_data);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int size = heartDataList.size();
        String rtc = heartDataList.get(position).getRtc();
        int heartRate = heartDataList.get(position).getHeartRate();
        Log.d("--->" + this.getClass(), "总共长度：" + size + "时间：" + rtc + "心率：" + heartRate);
        holder.text_data.setText(rtc.substring(0, 10));
        String substring = rtc.substring(11, 16);//2017-11-21 02:00
        holder.hearteTime.setText(substring);
        holder.hearteValue.setText(String.valueOf(heartRate)+"bpm");
        return convertView;
    }

    class ViewHolder {
        TextView hearteTime;
        TextView hearteValue, text_data;
    }
}
