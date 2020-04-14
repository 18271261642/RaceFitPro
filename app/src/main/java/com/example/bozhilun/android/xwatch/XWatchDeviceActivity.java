package com.example.bozhilun.android.xwatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.xwatch.fragment.XWatchDeviceFragment;

/**
 * Created by Admin
 * Date 2020/2/20
 */
public class XWatchDeviceActivity extends WatchBaseActivity {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x_watch_device_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.xWatchDeviceFrameLayout,new XWatchDeviceFragment());
        fragmentTransaction.commit();
    }
}
