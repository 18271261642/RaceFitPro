package com.example.bozhilun.android.b31.hrv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.veepoo.protocol.model.datas.HRVOriginData;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2018/12/26
 */
public class HrvListDataAdapter extends RecyclerView.Adapter<HrvListDataAdapter.HrvViewHolder> {

    private List<Map<String,Float>> list;
    private Context mContext;

    private HrvItemClickListener hrvItemClickListener;

    public void setHrvItemClickListener(HrvItemClickListener hrvItemClickListener) {
        this.hrvItemClickListener = hrvItemClickListener;
    }

    public HrvListDataAdapter(List<Map<String,Float>> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HrvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hrv_list_data,parent,false);
        HrvViewHolder hrvViewHolder = new HrvViewHolder(view);
        return hrvViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final HrvViewHolder holder, int position) {
        float hrvTime = list.get(position).get("time");
        //去掉小数点后转int型
        String deciBefore = StringUtils.substringBefore(hrvTime+"",".");
        int countMine = Integer.valueOf(deciBefore.trim());
        //小时
        int hour = (int) Math.floor(countMine/60);
        //分钟
        int mine = countMine % 60;
        float hrvValue = list.get(position).get("value");
        holder.dateTv.setText("0"+hour+":"+(mine==0?"00":(mine<10?"0"+mine:mine)+""));   //展示时间
        holder.valueTv.setText((int)hrvValue+"ms");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hrvItemClickListener != null){
                    int position = holder.getLayoutPosition();
                    hrvItemClickListener.hrvItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HrvViewHolder extends RecyclerView.ViewHolder{

        private TextView dateTv,valueTv;

         HrvViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.itemHrvDataDateTv);
            valueTv = itemView.findViewById(R.id.itemHrvDataValueTv);
        }
    }

    public interface HrvItemClickListener{
        void hrvItemClick(int position);
    }


}
