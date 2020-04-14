package com.example.bozhilun.android.w30s.carema;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;


import java.util.List;

/**
 * 相册的adapter
 */
public class AlbumPagerAdapter extends PagerAdapter {


    private List<String> imageUrls;
    private Context mContext;

    public AlbumPagerAdapter(List<String> imageUrls, Context mContext) {
        this.imageUrls = imageUrls;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

  /*  @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_UNCHANGED;
    }*/

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = imageUrls.get(position);
        PhotoView photoView = new PhotoView(mContext);
        photoView.enable();     //设置开启缩放
        photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        container.addView(photoView);
        //Glide加载本地的图片
        Glide.with(mContext)
                .load(url)
                .into(photoView);

        return photoView;
    }
}
