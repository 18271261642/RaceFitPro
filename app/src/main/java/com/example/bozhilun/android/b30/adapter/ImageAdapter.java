package com.example.bozhilun.android.b30.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.bozhilun.android.R;

public class ImageAdapter extends BaseAdapter {

    /**
     * 闹钟图标数组,资源ID,未选中
     */
    private final int[] alarmTypeImageList = {R.drawable.unselected1, R.drawable.unselected2,
            R.drawable.unselected3, R.drawable.unselected4, R.drawable.unselected5, R.drawable.unselected6,
            R.drawable.unselected7, R.drawable.unselected8, R.drawable.unselected9, R.drawable.unselected10,
            R.drawable.unselected11, R.drawable.unselected12, R.drawable.unselected13, R.drawable.unselected14,
            R.drawable.unselected15, R.drawable.unselected16, R.drawable.unselected17, R.drawable.unselected18,
            R.drawable.unselected19, R.drawable.unselected20};

    private Context mContext;

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return alarmTypeImageList.length;
    }

    @Override
    public Object getItem(int position) {
        return alarmTypeImageList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        // 设置GridView的显示的个子的间距
        imageView.setPadding(10, 10, 10, 10);
        imageView.setImageResource(alarmTypeImageList[position]);
        return imageView;
    }

}