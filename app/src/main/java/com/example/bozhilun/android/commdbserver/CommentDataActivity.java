package com.example.bozhilun.android.commdbserver;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

/**
 * Created by Admin
 * Date 2019/3/13
 * 用于加载Fragment的activity
 */
public class CommentDataActivity extends WatchBaseActivity {

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_data_layout);


        initViews();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.commDataContent,new CommDataFragment());
        fragmentTransaction.commit();


    }

    private void initViews() {

    }
}
