package com.example.bozhilun.android.recommend;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Admin
 * Date 2020/4/1
 */
public class ImgBannerHolder extends ImageLoader
{
    private static final String TAG = "ImgBannerHolder";

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

        //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ImgBean imgBean = (ImgBean) path;
        String urlPath = imgBean.getImgUrl();

        Glide.with(context).load(urlPath).into(imageView);    //头像
    }

}
