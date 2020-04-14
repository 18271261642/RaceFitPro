package com.example.bozhilun.android.w30s.carema;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PreviewActivity extends WatchBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //设置当前窗体为全屏显示-----------------定义全屏参数
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);

        ImageView id_iv_preview_photo = (ImageView) this.findViewById(R.id.id_iv_preview_photo);
        ImageView id_iv_ok = (ImageView) this.findViewById(R.id.id_iv_ok);

        Intent intent = this.getIntent();
        String filePath = null;
        if (intent != null) {
            filePath = intent.getStringExtra("filePath");
            Bitmap bitmapByUrl = getBitmapByUrl(filePath);
            id_iv_preview_photo.setScaleType(ImageView.ScaleType.MATRIX);
//            id_iv_preview_photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay()
                    .getMetrics(displayMetrics);
            float scalew = (float) displayMetrics.widthPixels / (float) bitmapByUrl.getWidth();
            Matrix matrix = new Matrix();
            id_iv_preview_photo.setAdjustViewBounds(true);
            if (displayMetrics.widthPixels < bitmapByUrl.getWidth()) {
                matrix.postScale(scalew, scalew);
            } else {
                matrix.postScale(1 / scalew, 1 / scalew);
            }
            id_iv_preview_photo.setMaxWidth(displayMetrics.widthPixels);
            float ss = displayMetrics.heightPixels > bitmapByUrl.getHeight() ? displayMetrics.heightPixels : bitmapByUrl.getHeight();
            id_iv_preview_photo.setMaxWidth((int) ss);

            id_iv_preview_photo.setImageBitmap(bitmapByUrl);
        } else {
//            Toast.makeText(this, "图片加载错误", Toast.LENGTH_SHORT).show();
        }

        id_iv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 根据图片路径获取本地图片的Bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapByUrl(String url) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(url);
            bitmap = BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bitmap = null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fis = null;
            }
        }

        return bitmap;
    }
}
