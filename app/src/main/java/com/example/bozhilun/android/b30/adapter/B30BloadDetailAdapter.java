package com.example.bozhilun.android.b30.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import java.util.List;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 血压详情adapter
 */
public class B30BloadDetailAdapter extends RecyclerView.Adapter<B30BloadDetailAdapter.B30StepDetailViewHolder>{

    private Context mContext;
    private List<CusVPHalfHourBpData> list;

    public B30BloadDetailAdapter(Context mContext, List<CusVPHalfHourBpData> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public B30StepDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_step_detail_layout,parent,false);
        B30StepDetailViewHolder holder = new B30StepDetailViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull B30StepDetailViewHolder holder, int position) {
        holder.timeTv.setText(list.get(position).getTime().getColck());
        holder.kcalTv.setText(list.get(position).getHighValue()+"/"+list.get(position).getLowValue());

        if (list.get(position).getHighValue()<=120){
            holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_norma);
        }else if (list.get(position).getHighValue()>120 &&list.get(position).getHighValue()<=140){
            holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_slight);
        }else if (list.get(position).getHighValue()>140&&list.get(position).getHighValue() <=150){
            holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_serious);
        }else{
            holder.img.setImageResource(R.mipmap.b30_bloodpressure_detail_veryserious);
        }

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
