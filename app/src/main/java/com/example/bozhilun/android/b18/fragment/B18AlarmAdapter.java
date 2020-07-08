package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;

import java.util.List;

/**
 * B18闹钟适配器
 * Created by Admin
 * Date 2019/11/14
 */
public class B18AlarmAdapter extends RecyclerView.Adapter<B18AlarmAdapter.B18AlarmViewHoler> {

    private Context mContext;
    private List<B18AlarmBean> list;

    private OnAlarmItemClickListener onAlarmItemClickListener;

    private OnAlarmItemToggCheckListener onAlarmItemToggCheckListener;

    public void setOnAlarmItemToggCheckListener(OnAlarmItemToggCheckListener onAlarmItemToggCheckListener) {
        this.onAlarmItemToggCheckListener = onAlarmItemToggCheckListener;
    }

    public OnAlarmItemClickListener getOnAlarmItemClickListener() {
        return onAlarmItemClickListener;
    }

    public void setOnAlarmItemClickListener(OnAlarmItemClickListener onAlarmItemClickListener) {
        this.onAlarmItemClickListener = onAlarmItemClickListener;
    }

    public B18AlarmAdapter(Context mContext, List<B18AlarmBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public B18AlarmViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b18_alarm_layout,viewGroup,false);
        return new B18AlarmViewHoler(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final B18AlarmViewHoler b18AlarmViewHoler, final int i) {
        int hour = list.get(i).getHour();
        int mine = list.get(i).getMinute();

        b18AlarmViewHoler.alarmTimeTv.setText((hour <= 9 ? 0+""+hour : hour) +":"+(mine<=9?0+""+mine : mine));
        b18AlarmViewHoler.weeksTv.setText(list.get(i).showAlarmWeeks(mContext));
        b18AlarmViewHoler.switchToggle.setImageResource(list.get(i).isOpen() ? R.mipmap.myvp_open : R.mipmap
                .myvp_close);
        b18AlarmViewHoler.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = b18AlarmViewHoler.getLayoutPosition();
                if(onAlarmItemClickListener != null)
                    onAlarmItemClickListener.onItemClick(position);
            }
        });

        b18AlarmViewHoler.switchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = b18AlarmViewHoler.getLayoutPosition();
                if(onAlarmItemToggCheckListener != null){
                    onAlarmItemToggCheckListener.onItemToggleCheck(position,list.get(i).isOpen());
                }
            }
        });

//        b18AlarmViewHoler.switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                int position = b18AlarmViewHoler.getLayoutPosition();
//                b18AlarmViewHoler.switchToggle.setChecked(isChecked);
//                if(onAlarmItemToggCheckListener != null){
//                    onAlarmItemToggCheckListener.onItemToggleCheck(position,isChecked);
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class B18AlarmViewHoler extends RecyclerView.ViewHolder{

        TextView alarmTimeTv,weeksTv;
        ImageView switchToggle;

        public B18AlarmViewHoler(@NonNull View itemView) {
            super(itemView);
            alarmTimeTv = itemView.findViewById(R.id.itemB18AlarmTimeTv);
            weeksTv = itemView.findViewById(R.id.itemB18AlarmWeeksTv);
            switchToggle = itemView.findViewById(R.id.itemB18AlarmSwitchToggle);
        }
    }


    interface OnAlarmItemClickListener{
        void onItemClick(int position);
    }

    interface OnAlarmItemToggCheckListener{
        void onItemToggleCheck(int position,boolean isChecked);
    }

}
