package com.example.bozhilun.android.siswatch.h8;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.KeyEvent;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.data.WatchH8DataFragment;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.utils.BlueAdapterUtils;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/7/17.
 */

/**
 * H8手表主页面
 */
public class H8HomeActivity extends WatchBaseActivity {

    private static final String TAG = "H8HomeActivity";


    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    @BindView(R.id.record_bottomsheet)
    BottomSheetLayout recordBottomsheet;


    private List<Fragment> watchfragments;


    private BluetoothAdapter bluetoothAdapter;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    if(MyApp.getInstance().h8BleManagerInstance().getH8BleService() != null){
                        MyApp.getInstance().h8BleManagerInstance().getH8BleService().autoConnByMac(true);
                    }
                    break;
            }

        }
    };






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_home);
        ButterKnife.bind(this);


        initViews();

        initData();



    }

    private void initData() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(!bluetoothAdapter.enable()){
            BlueAdapterUtils.getBlueAdapterUtils(H8HomeActivity.this).turnOnBlue(H8HomeActivity.this,10000,1000);
        }
    }

    //加载fragment
    private void initViews() {
        watchfragments = new ArrayList<>();
        watchfragments.add(new H8RecordFragment());  //记录
        watchfragments.add(new WatchH8DataFragment());
        watchfragments.add(new ChildGPSFragment());
        watchfragments.add(new WatchMineFragment());    //个人中心
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), watchfragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_data:     //数据
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_set:  //开跑
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_my:   //我的
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"-------onResume---"+(MyCommandManager.DEVICENAME));
        Log.e(TAG,"-----是否为null="+(MyApp.getInstance().h8BleManagerInstance().getH8BleService()==null));
        //判断是否连接
        if(MyCommandManager.DEVICENAME == null){    //未已连接
           if(MyApp.getInstance().h8BleManagerInstance().getH8BleService() != null){
               MyApp.getInstance().h8BleManagerInstance().getH8BleService().autoConnByMac(true);
           }else{
               MyApp.getInstance().h8BleManagerInstance().bindH8Service();
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       handler.sendEmptyMessage(1001);
                   }
               },3 * 1000 );
           }
        }


        //检查更新
//        UpdateManager updateManager = new UpdateManager(H8HomeActivity.this, URLs.HTTPs + URLs.getvision);
//        updateManager.checkForUpdate(true);

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
}
