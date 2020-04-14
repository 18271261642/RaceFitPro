package com.example.bozhilun.android.b30.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.SkinColorBean;

import java.util.List;
import java.util.Map;

/**
 * 肤色选择的适配器
 */
public class B30SkinColorAdapter extends BaseAdapter {

    private OnCheckSkinItem onCheckSkinItem;

    public void setOnCheckSkinItem(OnCheckSkinItem onCheckSkinItem) {
        this.onCheckSkinItem = onCheckSkinItem;
    }

    private Context mContext;
    private List<SkinColorBean> list;
    private int selectId = -1;
    LayoutInflater layoutInflater;

    public B30SkinColorAdapter(List<SkinColorBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_b30_skin_color,parent,false);
            holder.img = convertView.findViewById(R.id.itemSkinImg);
            holder.checkBox = convertView.findViewById(R.id.itemSkinCheckBox);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setImageResource(list.get(position).getImgId());
        holder.checkBox.setChecked(list.get(position).isChecked());


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCheckSkinItem != null){
                    onCheckSkinItem.checkItemPosition(position);
                }
                if(holder.checkBox.isChecked()){    //已经选择了
                    selectId = position;
                    list.get(position).setChecked(true);

                }else{  //未选择
                    selectId = -1;
                }

                for(int j = 0;j<list.size();j++){
                    if(selectId != j){
                        list.get(j).setChecked(false);
                    }
                }
                notifyDataSetChanged();

            }
        });

        return convertView;
    }

    public class ViewHolder{
        ImageView img;
        CheckBox checkBox;
    }


    public void setCheckItemPosition(int position){

    }


    public interface OnCheckSkinItem{
        void checkItemPosition(int position);
    }
}
