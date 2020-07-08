package com.example.bozhilun.android.b30.adapter;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;

import java.util.List;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 运动步数详情adapter
 */
public class B30StepDetailAdapter extends RecyclerView.Adapter<B30StepDetailAdapter.B30StepDetailViewHolder>{

    private Context mContext;
    //private List<HalfHourSportData> list;
    private List<CusVPHalfSportData> list;
//    private int flagCode;

    public B30StepDetailAdapter(Context mContext, List<CusVPHalfSportData> list) {
        this.mContext = mContext;
        this.list = list;
//        this.flagCode = code;
    }

    @NonNull
    @Override
    public B30StepDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_step_detail_layout,parent,false);
        B30StepDetailViewHolder holder = new B30StepDetailViewHolder(view);
        return holder;
    }

    //{date='2018-08-17', allPackage=65, packageNumber=44, mTime=TimeData [2018-08-17 14:25:00],
    // rateValue=77, sportValue=34, stepValue=0, highValue=0, lowValue=0, wear=63, tempOne=4, tempTwo=-1,
    // calValue=0.0, disValue=0.0, calcType=2}
    @Override
    public void onBindViewHolder(@NonNull B30StepDetailViewHolder holder, int position) {
        //时间
        holder.timeTv.setText(list.get(position).getTime().getColck());
        holder.kcalTv.setText(list.get(position).getStepValue()+"");
//        if(flagCode == 1){  //步数
//            holder.kcalTv.setText(list.get(position).getStepValue()+"");
//        }else if(flagCode == 2){    //里程
//            holder.kcalTv.setText(list.get(position).getDisValue()+"");
//        }else if(flagCode == 3){    //卡路里
//            holder.kcalTv.setText(list.get(position).getCalValue()+"");
//        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class B30StepDetailViewHolder extends RecyclerView.ViewHolder{

        TextView timeTv,kcalTv;
        ImageView img;

        public B30StepDetailViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
            kcalTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
            img = itemView.findViewById(R.id.itemB30StepDetailImg);
        }
    }
}
