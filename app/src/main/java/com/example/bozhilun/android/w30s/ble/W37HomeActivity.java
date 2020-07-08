package com.example.bozhilun.android.w30s.ble;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.detail.UploadW30DetailService;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.fragment.W30SMineFragment;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * W37的主页面
 * Created by Admin
 * Date 2019/7/5
 */
public class W37HomeActivity extends WatchBaseActivity {


    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    @BindView(R.id.record_h18ibottomsheet)
    CoordinatorLayout recordH18ibottomsheet;


    private List<Fragment> h18iFragmentList = new ArrayList<>();

    private String w30SBleName = null;
    //已经连接过到蓝牙mac
    String mylanmac = null;

    ActiveManage activeManage;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 111:
                    if(MyApp.getInstance().getW37ConnStatusService() != null){
                        if(MyCommandManager.DEVICENAME == null){
                            MyApp.getInstance().getW37ConnStatusService().w37AutoBleDevice();
                        }

                    }

                    break;
            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_home);
        ButterKnife.bind(this);

        initViews();

        initData();

        updateActiveStatus();
    }

    private void updateActiveStatus() {
        activeManage = ActiveManage.getActiveManage();
        activeManage.updateTodayActive(this);
    }

    private void initData() {
        mylanmac = (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC);
        w30SBleName = (String) SharedPreferencesUtils.readObject(this,  Commont.BLENAME);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W37BleOperateManager.W37_FIND_PHONE_ACTION);
        intentFilter.addAction("aabbcc");
        registerReceiver(broadcastReceiver,intentFilter);


    }

    private void initViews() {
        h18iFragmentList.add(new W37HomeFragment()); //记录
        h18iFragmentList.add(new ChildGPSFragment());   //跑步
        h18iFragmentList.add(new WatchMineFragment());   //我的
        if (h18iFragmentList == null) return;
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(),
                h18iFragmentList);
        h18iViewPager.setAdapter(fragmentPagerAdapter);
        h18iBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        h18iViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:  //开跑
                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_my:   //我的
                        h18iViewPager.setCurrentItem(2);//4
                        break;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        isAutoConnBle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void isAutoConnBle(){
        //未连接
        if(MyCommandManager.DEVICENAME == null && !WatchUtils.isEmpty(mylanmac)){
            if(MyApp.getInstance().getW37ConnStatusService() != null){
                MyApp.getInstance().getW37ConnStatusService().w37AutoBleDevice();
            }else{
                MyApp.getInstance().startW37Server();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(111);
                    }
                }, 3 * 1000);
            }
        }else{
            updateDevice();
        }
    }


    public void updateDevice(){
//        try {
//            String saveDeviceName = (String) SharedPreferencesUtils.readObject(this,Commont.BLENAME);
//            ActiveManage.getActiveManage().updateUserDevice(this,saveDeviceName);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

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

    //启动上传数据
    public void startW30UploadServer(){
        try {
            //开始上传本地数据
            //开始上传本地的数据
            CommDBManager.getCommDBManager().startUploadDbService(W37HomeActivity.this);

            Intent intent = new Intent(W37HomeActivity.this,UploadW30DetailService.class);
            W37HomeActivity.this.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("","---------aaacdtion="+action);
            if(action == null)
                return;
            if(action.equals("aabbcc")){
                Log.e("","------22---查找手机了----");
            }
        }
    };
}
