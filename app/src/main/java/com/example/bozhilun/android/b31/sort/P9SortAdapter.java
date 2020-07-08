package com.example.bozhilun.android.b31.sort;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import java.util.List;

/**
 * Created by Admin
 * Date 2020/5/5
 */
public class P9SortAdapter extends RecyclerView.Adapter<P9SortAdapter.P9ViewHolder> {

    private List<P9SortBean> p9SortBeanList;
    private Context mContext;


    public P9SortAdapter(List<P9SortBean> p9SortBeanList, Context mContext) {
        this.p9SortBeanList = p9SortBeanList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public P9ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_p9_sort_layout,viewGroup,false);
        P9ViewHolder holder = new P9ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull P9ViewHolder p9ViewHolder, int i) {
        p9ViewHolder.itemName.setText(p9SortBeanList.get(i).getTitleName());

    }

    @Override
    public int getItemCount() {
        return p9SortBeanList.size();
    }

    class P9ViewHolder extends RecyclerView.ViewHolder{

        TextView itemName;
        LinearLayout sortItemLayout;

        public P9ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemP9SortTv);
            sortItemLayout = itemView.findViewById(R.id.sortItemLayout);
        }
    }
}
