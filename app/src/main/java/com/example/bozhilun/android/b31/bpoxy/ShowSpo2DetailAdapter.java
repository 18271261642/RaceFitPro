package com.example.bozhilun.android.b31.bpoxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import org.apache.commons.lang.StringUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/1/5
 */
public class ShowSpo2DetailAdapter extends RecyclerView.Adapter<ShowSpo2DetailAdapter.Spo2ViewHolder> {

    private int spo2Tag;
    private List<Map<String, Float>> mapList;
    private Context mContext;
    private ShowDialogItemClickListener showDialogItemClickListener;

    public void setShowDialogItemClickListener(ShowDialogItemClickListener showDialogItemClickListener) {
        this.showDialogItemClickListener = showDialogItemClickListener;
    }

    public ShowSpo2DetailAdapter(List<Map<String, Float>> mapList, Context mContext) {
        this.mapList = mapList;
        this.mContext = mContext;
    }

    public void setSpowTag(int spowTag) {
        this.spo2Tag = spowTag;
    }

    @NonNull
    @Override
    public Spo2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_show_spo2_detail_layout,parent,false);
        Spo2ViewHolder holder = new Spo2ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Spo2ViewHolder holder, int position) {
        //时间
        float hrvTime = mapList.get(position).get("time");
        //去掉小数点后转int型
        String deciBefore = StringUtils.substringBefore(hrvTime+"",".");
        int countMine = Integer.valueOf(deciBefore.trim());
        //小时
        final int hour = (int) Math.floor(countMine/60);
        //分钟
        int mine = countMine % 60;
        holder.timeTv.setText("0"+hour+":"+(mine==0?"00":(mine<10?"0"+mine:mine)+""));   //展示时间
        DecimalFormat decimalFormat = new DecimalFormat("#");    //不保留小数
        if(spo2Tag == 0){
            float breahbreakvalue = mapList.get(position).get("breahbreakvalue");
            int times = Integer.valueOf(decimalFormat.format(breahbreakvalue));
            if(times == 0){
                holder.middleTv.setText("--");
            }else{
                holder.middleTv.setText(times+"");
            }
        }

        float aveValue = mapList.get(position).get("value");
        if(spo2Tag == 555){ //HRV
            holder.rightTv.setText(decimalFormat.format(aveValue)+" ms");
        }else{  //血氧的数据
            holder.rightTv.setText(decimalFormat.format(aveValue)+"");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showDialogItemClickListener != null){
                    int position = holder.getLayoutPosition();
                    showDialogItemClickListener.itemPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    class Spo2ViewHolder extends RecyclerView.ViewHolder{

        private TextView timeTv,middleTv,rightTv;

        public Spo2ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.itemSpo2timeTv);
            middleTv = itemView.findViewById(R.id.itemSpo2MiddleTv);
            rightTv = itemView.findViewById(R.id.itemSpo2RightTv);


        }
    }


    public interface ShowDialogItemClickListener{
        void itemPosition(int position);
    }

}
