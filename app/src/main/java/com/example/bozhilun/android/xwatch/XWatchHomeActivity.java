package com.example.bozhilun.android.xwatch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.KeyEvent;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.example.bozhilun.android.xwatch.fragment.XWatchHomeFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin
 * Date 2020/2/15
 */
public class XWatchHomeActivity extends WatchBaseActivity {


    @BindView(R.id.xwatch_view_pager)
    NoScrollViewPager xwatchViewPager;
    @BindView(R.id.xwatch_bottomBar)
    BottomBar xwatchBottomBar;


    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentStatePagerAdapter fragmentPagerAdapter;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x01){
                if(MyApp.getInstance().getW37ConnStatusService() != null){
                    if(MyCommandManager.DEVICENAME == null){
                        MyApp.getInstance().getW37ConnStatusService().w37AutoBleDevice();
                    }

                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x_watch_layout);
        ButterKnife.bind(this);


        initViews();


    }

    private void initViews() {
        fragmentList.add(new XWatchHomeFragment());
        fragmentList.add(new ChildGPSFragment());
        fragmentList.add(new WatchMineFragment());
        fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList);
        xwatchViewPager.setAdapter(fragmentPagerAdapter);
        xwatchBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId){
                    case R.id.x_watch_tab_home:
                        xwatchViewPager.setCurrentItem(0);
                        break;
                    case R.id.x_watch_table_sport:
                        xwatchViewPager.setCurrentItem(1);
                        break;
                    case R.id.x_watch_tab_my:
                        xwatchViewPager.setCurrentItem(2);
                        break;
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        autoConnBle();
    }


    private void autoConnBle(){
        String saveBleMac = MyApp.getInstance().getMacAddress();
        if(MyCommandManager.DEVICENAME == null && !WatchUtils.isEmpty(saveBleMac)){
            if(MyApp.getInstance().getW37ConnStatusService() != null){
                MyApp.getInstance().getW37ConnStatusService().w37AutoBleDevice();
            }else{
                MyApp.getInstance().startW37Server();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x01);
                    }
                }, 3 * 1000);
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

}
