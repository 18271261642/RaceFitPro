package com.example.bozhilun.android.w30s.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @aboutContent: 通用--RecyclerAdapter
 * @author： 安
 * @crateTime: 2018/1/5 17:36
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    //数据
    protected List<T> mData;
    // 布局
    private int mLayoutId;
    private OnItemListener mOnItemListener;


    public CommonRecyclerAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    public CommonRecyclerAdapter(Context context, List<T> data, int layoutId, OnItemListener mOnItemListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
        this.mLayoutId = layoutId;
        this.mOnItemListener = mOnItemListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 先inflate数据
        View itemView = mInflater.inflate(mLayoutId, parent, false);
        // 返回ViewHolder
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // 设置点击和长按事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemListener != null)
                    mOnItemListener.onItemClickListener(v, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemListener != null)
                    mOnItemListener.onLongClickListener(v, holder.getAdapterPosition());
                return false;
            }
        });
        if (mData.get(position) != null) {
            convert(holder, mData.get(position));
        }
    }

    /**
     * 利用抽象方法回传出去，每个不一样的Adapter去设置
     *
     * @param item 当前的数据
     */
    public abstract void convert(MyViewHolder holder, T item);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /***************
     * 设置条目点击和长按事件
     *********************/
    public void setmOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    public interface OnItemListener {
        void onItemClickListener(View view, int position);

        void onLongClickListener(View view, int position);
    }
}
