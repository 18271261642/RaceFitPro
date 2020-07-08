package com.example.bozhilun.android.adpter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AreCodeBean;

import java.util.List;

/**
 * 手机号区号选择adapter
 * Created by An on 2018/9/11.
 */

public class PhoneAdapter extends BaseAdapter {

    List<AreCodeBean>phoneHeadList;
    Context context;
    LayoutInflater layoutInflater;

    public PhoneAdapter(List<AreCodeBean> phoneHeadList, Context context) {
        this.phoneHeadList = phoneHeadList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return phoneHeadList.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneHeadList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.item_phone_head_layout,parent,false);
            holder.tv1 = convertView.findViewById(R.id.itemPhoneRegionsTv);
            holder.tv2 = convertView.findViewById(R.id.itemPhoneCountryTv);
            holder.tv3 = convertView.findViewById(R.id.itemPhoneCodeTv);
            holder.tv4 = convertView.findViewById(R.id.tv_phone_head);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AreCodeBean areCodeBean = phoneHeadList.get(position);
        //国家英文名称
        holder.tv1.setText(areCodeBean.getPhoneRegious()+"");
        holder.tv2.setText(areCodeBean.getPhoneCountry()+"");
        holder.tv3.setText(areCodeBean.getPhoneAreCode()+"");
        holder.tv4.setText(areCodeBean.getPhoneCode()+"");
        return convertView;
    }

    class ViewHolder{
        TextView tv1,tv2,tv3,tv4;
    }
}
