package com.example.bozhilun.android.b11;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.w30s.fragment.W30SMineFragment;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin
 * Date 2020/6/15
 */
public class B11HomeActivity extends WatchBaseActivity {


    @BindView(R.id.b11NoScrollViewPager)
    NoScrollViewPager b11NoScrollViewPager;
    @BindView(R.id.b11HomeBottomBar)
    BottomBar b11HomeBottomBar;


    private List<Fragment> b11ListFragment;
    FragmentStatePagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b11_home);
        ButterKnife.bind(this);


        initViews();

    }

    private void initViews() {
        if(b11ListFragment == null)
            b11ListFragment = new ArrayList<>();
        b11ListFragment.add(new B11RecordFragment());
        b11ListFragment.add(new ChildGPSFragment());
        b11ListFragment.add(new WatchMineFragment());

        fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(),b11ListFragment);
        if(b11NoScrollViewPager != null){
            b11NoScrollViewPager.setAdapter(fragmentPagerAdapter);
            b11NoScrollViewPager.setOffscreenPageLimit(0);
        }
        b11HomeBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId){
                    case R.id.x_watch_tab_home:
                        b11NoScrollViewPager.setCurrentItem(0,false);
                        break;
                    case R.id.x_watch_table_sport:
                        b11NoScrollViewPager.setCurrentItem(1,false);
                        break;
                    case R.id.x_watch_tab_my:
                        b11NoScrollViewPager.setCurrentItem(2,false);
                        break;
                }
            }
        });

    }
}
