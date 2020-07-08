package com.example.bozhilun.android.w30s.activity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.w30s.carema.AlbumPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/3/31.
 */

public class ShowAlbumActivity extends WatchBaseActivity {


    FloatingActionButton fb;
    private ViewPager viewPager;

    int currPosition;
    private AlbumPagerAdapter albumPagerAdapter;
    private List<String> urlList;
    private TextView showCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showalbum);

        initViews();


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ShowAlbumActivity.this).title(R.string.prompt)
                        .content(getResources().getString(R.string.deleda))
                        .positiveText(R.string.confirm)
                        .negativeText(R.string.cancle)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                deleteFile(new File(urlList.get(currPosition)));
                                finish();
                            }
                        }).show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        //返回的图片下标
        int imgPosition = getIntent().getIntExtra("imgPosition",0);
        //所有的集合
        ArrayList<String> imgList = getIntent().getStringArrayListExtra("imgUrl");
        if(imgList != null && imgList.size()>0){
            urlList.addAll(imgList);
            showCount.setText(imgPosition+1+"/"+urlList.size());
            viewPager.setCurrentItem(imgPosition);
            albumPagerAdapter.notifyDataSetChanged();
        }

    }

    private void initViews() {
        //showPhotoView = (PhotoView) findViewById(R.id.showPhotoView);
        showCount = findViewById(R.id.view_pager_showNumTv);
        viewPager = findViewById(R.id.my_viewPager);
        fb = (FloatingActionButton) findViewById(R.id.albumDeleteFloat);
        urlList = new ArrayList<>();
        albumPagerAdapter = new AlbumPagerAdapter(urlList,ShowAlbumActivity.this);
        viewPager.setAdapter(albumPagerAdapter);

        //showCount.setText(currPosition+1+"/"+urlList.size());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currPosition = position;
                showCount.setText(currPosition+1+"/"+urlList.size());
            }
        });

    }


    public void deleteFile(File file) {
        // TODO Auto-generated method stub
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                DeleteFile(f);
            }
            file.delete();
        }
    }

    private void DeleteFile(File f) {
        // TODO Auto-generated method stub
        f.delete();
    }
}
