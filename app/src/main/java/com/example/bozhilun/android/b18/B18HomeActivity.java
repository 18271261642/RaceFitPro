package com.example.bozhilun.android.b18;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.KeyEvent;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b18.fragment.B18HomeFragment;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.hplus.bluetooth.BleProfileManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin
 * Date 2019/10/31
 */
public class B18HomeActivity extends WatchBaseActivity {

    private static final String TAG = "B18HomeActivity";


    @BindView(R.id.b18View_pager)
    NoScrollViewPager b18ViewPager;
    @BindView(R.id.b18BottomBar)
    BottomBar b18BottomBar;
    @BindView(R.id.b18Coordinator)
    CoordinatorLayout b18Coordinator;
    @BindView(R.id.b18_home_bottomsheet)
    BottomSheetLayout b18HomeBottomsheet;


    FragmentStatePagerAdapter fragmentPagerAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b18_home_layout);
        ButterKnife.bind(this);

        initViews();

        Log.e("B18", "-------isConn=" + BleProfileManager.getInstance().isConnected());

        initData();

    }

    private void initData() {

    }


    private void initViews() {
        fragmentList.add(new B18HomeFragment());
        fragmentList.add(new ChildGPSFragment());
//        fragmentList.add(new RecommendFragment());
        fragmentList.add(new WatchMineFragment());
        fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        if (b18ViewPager != null) {
            b18ViewPager.setAdapter(fragmentPagerAdapter);
            b18ViewPager.setCurrentItem(0);
        }

        if (b18BottomBar != null)
            b18BottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(int tabId) {
                    switch (tabId) {
                        case R.id.b30_tab_home: //首页
                            b18ViewPager.setCurrentItem(0, false);
                            break;
                        case R.id.b30_tab_set:  //开跑
                            b18ViewPager.setCurrentItem(1, false);
                            break;
                        case R.id.b30_tab_my:   //我的
                            b18ViewPager.setCurrentItem(2, false);
                            break;
                    }
                }

            });

    }




    public void upUserDevice(){
        try {
            String saveDeviceName = (String) SharedPreferencesUtils.readObject(this, Commont.BLENAME);
            ActiveManage.getActiveManage().updateUserDevice(this,saveDeviceName);

            ActiveManage.getActiveManage().updateUserMac(this,"B16", MyApp.getInstance().getMacAddress());

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}