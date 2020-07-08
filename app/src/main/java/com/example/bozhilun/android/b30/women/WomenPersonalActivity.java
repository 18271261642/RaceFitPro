package com.example.bozhilun.android.b30.women;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

/**
 * Created by Admin
 * Date 2018/11/28
 */
public class WomenPersonalActivity extends WatchBaseActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_personal);

        initData();


    }

    private void initData() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.womenCont,new WomenPersonFragment());
        fragmentTransaction.commit();

    }
}
