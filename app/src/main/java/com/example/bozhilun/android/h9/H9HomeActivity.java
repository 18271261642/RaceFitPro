package com.example.bozhilun.android.h9;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.IdRes;
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
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.fragment.H9MineFragment;
import com.example.bozhilun.android.h9.fragment.H9NewDataFragment;
import com.example.bozhilun.android.h9.fragment.H9NewRecordFragment;
import com.example.bozhilun.android.h9.utils.H9ContentUtils;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent: 主页，承载四个fragment的Activity
 * @author： 安
 * @crateTime: 2017/9/27 16:27
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HomeActivity extends WatchBaseActivity {
    private final String TAG = "----->>>" + this.getClass().toString();
    //监听连接状态广播ACTION
    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    @BindView(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    @BindView(R.id.record_h18ibottomsheet)
    BottomSheetLayout recordH18ibottomsheet;
    private List<Fragment> h18iFragmentList = new ArrayList<>();


    /**
     * @param type 连接 conn   断开 disconn
     */
    void sendBroadcast(String type) {
        Intent intent = new Intent();
        intent.setAction("com.example.bozhilun.android.h9.connstate");
        intent.putExtra("h9constate", type);
        sendBroadcast(intent);  //发送连接成功的广播
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h38i_home);
        ButterKnife.bind(this);
        //注册连接状态的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(h9Receiver, intentFilter);
        initViews();
        //UpDatasBase.chageDevicesNames("H9");//更改设备类型

        H9ContentUtils.getContent(this);
    }


    /**
     * 检查链接状态
     */
    boolean firstInspect() {
        boolean isConnted = false;
        if (MyCommandManager.DEVICENAME != null) {
            isConnted = true;
        }
        return isConnted;
    }


    /**
     * 扫描设备---------扫描时会重新连接
     */

    public static void ConntentDevices() {
        AppsBluetoothManager.getInstance(MyApp.getInstance()).startDiscovery();
    }



    @Override
    protected void onStart() {
        super.onStart();
        //H9ContentUtils.getContent(MyApp.getContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(h9Receiver);
    }


    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        h18iFragmentList.add(new H9NewRecordFragment()); //记录
//        h18iFragmentList.add(new H9RecordFragment()); //记录
//        h18iFragmentList.add(new NewH9RecordFragment()); //记录
//        h18iFragmentList.add(new H9DataFragment());   //数据
//        h18iFragmentList.add(new NewsH9DataFragment());   //数据
        h18iFragmentList.add(new H9NewDataFragment());   //数据
        // h18iFragmentList.add(new RunningFragment());    //跑步
//        h18iFragmentList.add(new WatchRunFragment());   //跑步
//        h18iFragmentList.add(new W30sNewRunFragment());   //跑步ChildGPSFragment
        h18iFragmentList.add(new ChildGPSFragment());   //跑步
        h18iFragmentList.add(new H9MineFragment());   //我的
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), h18iFragmentList);
        h18iViewPager.setAdapter(fragmentPagerAdapter);
        h18iBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        h18iViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:  //开跑
                        h18iViewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_data:     //数据
                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_my:   //我的
                        h18iViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override//返回键拦截
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


    /**
     * 监听手表蓝牙状态连接状态的广播
     */
    private BroadcastReceiver h9Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WatchUtils.isEmpty(intent.getAction())) return;
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e(TAG, "onReceive-------打开状态--STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e(TAG, "onReceive-----打开----STATE_ON");
                            if (!firstInspect()) {
                                String h9Mac = (String) SharedPreferencesUtils.readObject(H9HomeActivity.this, Commont.BLEMAC);
                                Log.e(TAG, "H9Home" + "---h9mac--" + h9Mac);
                                if (!WatchUtils.isEmpty(h9Mac)) {

                                    H9HomeActivity.ConntentDevices();
                                }
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e(TAG, "onReceive-----关闭状态----STATE_TURNING_OFF");
                            MyCommandManager.DEVICENAME = null;
                            sendBroadcast("disconn");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.e(TAG, "onReceive----关闭-----STATE_OFF");
                            break;
                    }
                    break;
            }

        }
    };

}
