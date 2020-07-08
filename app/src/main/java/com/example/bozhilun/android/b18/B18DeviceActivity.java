package com.example.bozhilun.android.b18;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.fragment.B18DeviceFragment;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;


/**
 * Created by Admin
 * Date 2019/11/10
 */
public class B18DeviceActivity extends WatchBaseActivity {


    private FragmentTransaction fragmentTransaction;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b18_device_layout);



        initViews();


    }

    private void initViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.b18DeviceFragment,new B18DeviceFragment());
        fragmentTransaction.commit();
    }


}
