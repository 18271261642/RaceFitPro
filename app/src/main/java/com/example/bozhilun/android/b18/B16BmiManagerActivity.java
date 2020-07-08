package com.example.bozhilun.android.b18;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.fragment.B16BmiShowFragment;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

/**
 * Created by Admin
 * Date 2019/12/31
 */
public class B16BmiManagerActivity extends WatchBaseActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b16_bmi_layout);


        if(fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        if(fragmentTransaction == null)
            fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.b16BmiContent,new B16BmiShowFragment());
        fragmentTransaction.commit();

    }
}
