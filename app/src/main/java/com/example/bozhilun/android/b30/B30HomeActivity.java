package com.example.bozhilun.android.b30;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30homefragment.B30HomeFragment;
import com.example.bozhilun.android.b30.b30run.B36RunFragment;
import com.example.bozhilun.android.b30.service.FriendsUploadServices;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.sos.SendSMSBroadCast;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeActivity extends WatchBaseActivity {


    @BindView(R.id.b30View_pager)
    NoScrollViewPager b30ViewPager;
    @BindView(R.id.b30BottomBar)
    BottomBar b30BottomBar;

    private List<Fragment> b30FragmentList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    if (MyApp.getInstance().getB30ConnStateService() != null) {
                        String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this, Commont.BLEMAC);//设备mac
                        if (!TextUtils.isEmpty(bm))
                            MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                    }
                    break;
            }
        }
    };

    SendSMSBroadCast sendSMSBroadCast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_home);
        ButterKnife.bind(this);
        initViews();
//        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //过滤器 注册短信发送广播
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Commont.SOS_SENDSMS_MESSAGE);
        mIntentFilter.addAction(Commont.SOS_SENDSMS_LOCATION);
        //创建广播接收者的对象
        sendSMSBroadCast = new SendSMSBroadCast();
        //注册广播接收者的对象
        this.registerReceiver(sendSMSBroadCast, mIntentFilter);

        //初始设置挂断电话胡回调监听
        MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(MyApp.phoneSosOrDisPhone);

        updateActiveStatus();
    }

    private void updateActiveStatus() {
        ActiveManage activeManage = ActiveManage.getActiveManage();
        activeManage.updateTodayActive(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME == null || MyCommandManager.ADDRESS == null) {    //未连接
            reconnectDevice();// 重新连接
        }else{
            upUserDevice();
        }
    }


    public void upUserDevice(){
        try {
            String saveDeviceName = (String) SharedPreferencesUtils.readObject(this,Commont.BLENAME);
            ActiveManage.getActiveManage().updateUserDevice(this,saveDeviceName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void initViews() {
        b30FragmentList.add(new B30HomeFragment());
        b30FragmentList.add(new B36RunFragment());   //跑步
        b30FragmentList.add(new WatchMineFragment());
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), b30FragmentList);
        if (b30ViewPager != null) {
            b30ViewPager.setAdapter(fragmentPagerAdapter);
            b30ViewPager.setOffscreenPageLimit(0);
        }
        if (b30BottomBar != null) b30BottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if (b30ViewPager == null) return;
                switch (tabId) {
                    case R.id.b30_tab_home: //首页
                        b30ViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.b30_tab_set:  //开跑
                        b30ViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.b30_tab_my:   //我的
                        b30ViewPager.setCurrentItem(2, false);
                        break;
                }
            }
        });
    }

    /**
     * 重新连接设备B30
     */
    public void reconnectDevice() {
        if (MyCommandManager.DEVICENAME == null) {    //未连接
            if (MyApp.getInstance().getB30ConnStateService() != null) {
                String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this, Commont.BLEMAC);//设备mac
                if (!WatchUtils.isEmpty(bm)) {
                    MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                }
            } else {
                handler.sendEmptyMessageDelayed(1001, 3 * 1000);
            }
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

    /**
     * 启动上传数据的服务
     */
    public void startUploadDate() {
        try {
            CommDBManager.getCommDBManager().startUploadDbService(this);
            //上传缓存的详细数据
            Intent intent1 = new Intent(this, FriendsUploadServices.class);
            startService(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendSMSBroadCast!=null)unregisterReceiver(sendSMSBroadCast);
    }


}
