package com.example.bozhilun.android.b30.b30run;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30DevicesSport;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import java.util.List;

/**
 * Created by Admin
 * Date 2018/12/14
 */
public class DeviceSportAdapter extends BaseAdapter {

    private List<B30DevicesSport> sportList;
    private Context mContext;
    LayoutInflater layoutInflater;

    public DeviceSportAdapter(List<B30DevicesSport> sportList, Context mContext) {
        this.sportList = sportList;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sportList.size();
    }

    @Override
    public Object getItem(int position) {
        return sportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.devices_sport_item,parent,false);
            holder.kcalTxt = convertView.findViewById(R.id.text_kcal);
            holder.heartTxt = convertView.findViewById(R.id.text_heart);
            holder.stepTxt = convertView.findViewById(R.id.text_step);
            holder.dateTxt = convertView.findViewById(R.id.text_data_time);
            holder.txtTimes = convertView.findViewById(R.id.text_times);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        B30DevicesSport devicesSport = sportList.get(position);
        holder.kcalTxt.setText(devicesSport.getKcals()+ mContext.getResources().getString(R.string.km_cal));
        holder.heartTxt.setText(devicesSport.getAverRate()+"bpm");
        holder.stepTxt.setText(devicesSport.getStepCount()+mContext.getResources().getString(R.string.daily_numberofsteps_default));
        holder.dateTxt.setText(devicesSport.getStartTime());
        holder.txtTimes.setText(WatchUtils.secToTime(devicesSport.getSportTime()));

        return convertView;
    }


    class ViewHolder{
        TextView kcalTxt,heartTxt,stepTxt,dateTxt,txtTimes;
    }
}
