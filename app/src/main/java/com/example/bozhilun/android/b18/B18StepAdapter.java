package com.example.bozhilun.android.b18;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/11/21
 */
public class B18StepAdapter extends RecyclerView.Adapter<B18StepAdapter.B18StepViewHolder>{

    private List<Map<String,Integer>> list;
    private Context mContext;

    public B18StepAdapter(List<Map<String,Integer>> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public B18StepAdapter.B18StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_b30_step_detail_layout,viewGroup,false);

        return new B18StepAdapter.B18StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(B18StepAdapter.B18StepViewHolder b18StepViewHolder, int i) {
        Map<String,Integer> map = list.get(i);
        for(Map.Entry<String,Integer> mm : map.entrySet()){
            if(mm.getValue() != 0){
                b18StepViewHolder.timeTv.setText(mm.getKey());
                b18StepViewHolder.stepTv.setText(mm.getValue()+"");
            }else{
                b18StepViewHolder.timeTv.setText(mm.getKey());
                b18StepViewHolder.stepTv.setText("--");
            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class B18StepViewHolder extends RecyclerView.ViewHolder{

        TextView timeTv,stepTv;

        public B18StepViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.itemB30StepDetailTimeTv);
            stepTv = itemView.findViewById(R.id.itemB30StepDetailKcalTv);
        }
    }
}
