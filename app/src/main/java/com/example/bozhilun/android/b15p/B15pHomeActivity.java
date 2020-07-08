package com.example.bozhilun.android.b15p;

import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b15p.b15ppagefragment.B15pHomeFragment;
import com.example.bozhilun.android.b15p.common.B15PContentState;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.w30s.MyBroadcastReceiver;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.tjd.tjdmain.icentre.ICC_Contents;
import com.tjdL4.tjdmain.L4M;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class B15pHomeActivity extends WatchBaseActivity {
    private final String TAG = "B15P---主页";
    private L4M.BTStReceiver dataReceiver = null;
    @BindView(R.id.b30View_pager)
    NoScrollViewPager viewPager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private List<Fragment> h18iFragmentList;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.b30_tab_home:
                    viewPager.setCurrentItem(0, false);
                    return true;
                case R.id.b30_tab_set:
                    viewPager.setCurrentItem(1, false);
                    return true;
                case R.id.b30_tab_my:
                    viewPager.setCurrentItem(2, false);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b15p_home_activity);
        ButterKnife.bind(this);

        init();
        setFragment();
        //操作状态结果监听
        L4M.SetResultListener(btResultListenr);
        /**
         * 注册链接状态监听
         */
//        L4M.registerBTStReceiver(this, B15PContentState.getInstance().getDataReceiver());
        dataReceiver = B15PContentState.getInstance().getDataReceiver();
        if (dataReceiver != null) L4M.registerBTStReceiver(this, dataReceiver);


        //IMain_Ctr.getMe().IMainService_Init();

        myBroadcastReceivers = new MyBroadcastReceiver();
    }


    MyBroadcastReceiver myBroadcastReceivers = null;

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (myBroadcastReceivers == null)
                myBroadcastReceivers = new MyBroadcastReceiver();
            IntentFilter IntentFilter_a = new IntentFilter();
            IntentFilter_a.addAction(ICC_Contents.ToUi);
            registerReceiver(myBroadcastReceivers, IntentFilter_a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        B15PContentState.getInstance().stopSeachDevices();//停止扫描
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            /**
             * 反注册链接状态监听
             */
//            L4M.unregisterBTStReceiver(this, B15PContentState.getInstance().getDataReceiver());
            if (dataReceiver != null)
                L4M.unregisterBTStReceiver(this, dataReceiver);

            if (myBroadcastReceivers != null) {
                unregisterReceiver(myBroadcastReceivers);
            }
            L4M.SetOnRSISICallsback(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加布局
     */
    private void setFragment() {
        if (h18iFragmentList == null) h18iFragmentList = new ArrayList<>();
//        h18iFragmentList.add(new B15pHomeFragment());
        h18iFragmentList.add(new B15pHomeFragment());
//        h18iFragmentList.add(new W30sNewRunFragment());   //跑步
        h18iFragmentList.add(new ChildGPSFragment());   //跑步
        h18iFragmentList.add(new WatchMineFragment());
        FragmentStatePagerAdapter fragmentPagerAdapter =
                new FragmentAdapter(getSupportFragmentManager(), h18iFragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
    }


    /**
     * navigation 初始
     */
    private void init() {
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        Log.e(TAG, "-------- 长度   " + navigation.getChildCount() + "");
        if (navigation.getChildCount() > 3) BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.txt_black),//没选中
                getResources().getColor(R.color.new_colorAccent)//选中
        };
        ColorStateList csl = new ColorStateList(states, colors);
        navigation.setItemTextColor(csl);
        navigation.setItemIconTintList(csl);

    }


    /**
     * 操作结果
     */
    L4M.BTResultListenr btResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            final String tTypeInfo = TypeInfo;
            final String TempStr = StrData;
            final Object TempObj = DataObj;

            Log.e("====On_Result=", "inTempStr:" + TempStr);
            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //找手机。。。。
                    if (tTypeInfo.equals(L4M.FindDev) && TempStr.equals(L4M.OK)) {

                    }
                }
            });

        }
    };


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

}
