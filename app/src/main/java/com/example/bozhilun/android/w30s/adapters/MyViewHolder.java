package com.example.bozhilun.android.w30s.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.R;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2018/1/5 17:40
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public MyViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    // 用来存放子View减少findViewById的次数
    private SparseArray<View> mViews;


    /**
     * 设置TextView文本
     */
    public MyViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     *
     */
    public MyViewHolder setChaeck(int viewId, boolean text) {
        Switch tv = getView(viewId);
        tv.setChecked(text);
        return this;
    }

    /**
     * 通过id获取view
     */
    public <T extends View> T getView(int viewId) {
        // 先从缓存中找
        View view = mViews.get(viewId);
        if (view == null) {
            // 直接从ItemView中找
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置View的Visibility
     */
    public MyViewHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    /**
     * 设置ImageView的资源
     */
    public MyViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以直接写死
     */
    public MyViewHolder setImageByUrl(int viewId, HolderImageLoader imageLoader) {
        ImageView imageView = getView(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null!");
        }
        imageLoader.displayImage(imageView.getContext(), imageView, imageLoader.getImagePath());
        return this;
    }

    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以直接写死
     */
    public MyViewHolder setImageGlid(int viewId, String imageLoader,Context context) {
        ImageView imageView = getView(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null!");
        }
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        //头像
        Glide.with(context).load(imageLoader).apply(mRequestOptions).into(imageView);    //头像
        return this;
    }

    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以直接写死
     */
    public MyViewHolder setImageGlidInt(int viewId, int imageLoader,Context context) {
        ImageView imageView = getView(viewId);
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        //头像
        Glide.with(context).load(imageLoader).apply(mRequestOptions).into(imageView);    //头像
        return this;
    }

    public MyViewHolder setImageGlidNo(int viewId,Context context) {
        ImageView imageView = getView(viewId);
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        //头像
        Glide.with(context).load(R.mipmap.bg_img).apply(mRequestOptions).into(imageView);    //头像
        return this;
    }

    /**
     * 图片加载，这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以不写这个类
     */
    public static abstract class HolderImageLoader {
        private String mImagePath;

        public HolderImageLoader(String imagePath) {
            this.mImagePath = imagePath;
        }

        public String getImagePath() {
            return mImagePath;
        }

        public abstract void displayImage(Context context, ImageView imageView, String imagePath);
    }
}
