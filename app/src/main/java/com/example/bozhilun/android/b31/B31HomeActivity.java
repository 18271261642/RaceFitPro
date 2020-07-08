package com.example.bozhilun.android.b31;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;
import android.view.KeyEvent;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30run.B36RunFragment;
import com.example.bozhilun.android.b30.service.VerB30PwdListener;
import com.example.bozhilun.android.b31.record.B31RecordFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.sos.SendSMSBroadCast;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.view.CusInputDialogView;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * B31的主activity
 * Created by Admin
 * Date 2018/12/17
 */
public class B31HomeActivity extends WatchBaseActivity implements  Rationale<List<String>> {


    @BindView(R.id.b31View_pager)
    NoScrollViewPager b31ViewPager;
    @BindView(R.id.b31BottomBar)
    BottomBar b31BottomBar;


    private List<Fragment> fragmentList = new ArrayList<>();

    //列设备验证密码提示框
    CusInputDialogView cusInputDialogView;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    if (MyApp.getInstance().getB30ConnStateService() != null) {
                        String bm = (String) SharedPreferencesUtils.readObject(B31HomeActivity.this, Commont.BLEMAC);//设备mac
                        if (!WatchUtils.isEmpty(bm))
                            MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                    }
                    break;
            }
        }
    };


    SendSMSBroadCast sendSMSBroadCast = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_home);
        ButterKnife.bind(this);


        initViews();

        initData();


    }

    private void initData() {
        //过滤器 注册短信发送广播
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Commont.SOS_SENDSMS_MESSAGE);
        mIntentFilter.addAction(Commont.SOS_SENDSMS_LOCATION);
        //创建广播接收者的对象
        sendSMSBroadCast = new SendSMSBroadCast();
        //注册广播接收者的对象
        this.registerReceiver(sendSMSBroadCast, mIntentFilter);


        registerReceiver(broadcastReceiver, new IntentFilter("com.example.bozhilun.android.siswatch.CHANGEPASS"));
        //注册挂断电话和静音的监听
//        MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(this);

        MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(MyApp.phoneSosOrDisPhone);


        verticalLocalPermiss();


        updateActiveStatus();
    }

    private void updateActiveStatus() {
//        ActiveManage activeManage = ActiveManage.getActiveManage();
//        activeManage.updateTodayActive(this);
    }


    //判断是否有位置的权限
    private void verticalLocalPermiss() {
        if(!AndPermission.hasPermissions(B31HomeActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
            AndPermission.with(B31HomeActivity.this)
                    .runtime()
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME == null || MyCommandManager.ADDRESS == null) {    //未连接
            reconnectDevice();// 重新连接
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        fragmentList.add(new B31RecordFragment());
        fragmentList.add(new B36RunFragment());
        fragmentList.add(new WatchMineFragment());
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        if (b31ViewPager != null) {
            b31ViewPager.setAdapter(fragmentPagerAdapter);
            b31ViewPager.setOffscreenPageLimit(0);
        }
        b31BottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.b30_tab_home: //首页
                        b31ViewPager.setCurrentItem(0, false);
                        break;
//                    case R.id.b30_tab_data: //数据
//                        b31ViewPager.setCurrentItem(1, false);
//                        break;
                    case R.id.b30_tab_set:  //开跑
                        b31ViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.b30_tab_my:   //我的
                        b31ViewPager.setCurrentItem(2, false);
                        break;
                }
            }
        });
    }


    /**
     * 重新连接设备
     */
    public void reconnectDevice() {
        if (MyCommandManager.ADDRESS == null) {    //未连接
            if (MyApp.getInstance().getB30ConnStateService() != null) {
                String bm = (String) SharedPreferencesUtils.readObject(B31HomeActivity.this, Commont.BLEMAC);//设备mac
                if (!WatchUtils.isEmpty(bm)) {
                    MyApp.getInstance().getB30ConnStateService().connectAutoConn(true);
                }
            } else {
                handler.sendEmptyMessageDelayed(1001, 3 * 1000);
            }
        }
    }


    /**
     * 启动上传数据的服务,用户连成功
     */
    public void startUploadDate() {
        try {
            String saveDeviceName = (String) SharedPreferencesUtils.readObject(this,Commont.BLENAME);
            ActiveManage.getActiveManage().updateUserDevice(this,saveDeviceName);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        if (cusInputDialogView != null)
            cusInputDialogView.cancel();
        if (sendSMSBroadCast!=null)unregisterReceiver(sendSMSBroadCast);
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


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals("com.example.bozhilun.android.siswatch.CHANGEPASS")) {
                showB30InputPwd();  //弹出输入密码的提示框
            }
        }
    };


    //提示输入密码
    private void showB30InputPwd() {
        if (cusInputDialogView == null) {
            cusInputDialogView = new CusInputDialogView(B31HomeActivity.this);
        }
        cusInputDialogView.show();
        cusInputDialogView.setCancelable(false);
        cusInputDialogView.setCusInputDialogListener(new CusInputDialogView.CusInputDialogListener() {
            @Override
            public void cusDialogCancle() {
                cusInputDialogView.dismiss();
                //断开连接
                MyApp.getInstance().getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                });
                //刷新搜索界面
                //handler.sendEmptyMessage(777);
            }

            @Override
            public void cusDialogSureData(String data) {
                MyApp.getInstance().getB30ConnStateService().continuteConn(data, new VerB30PwdListener() {
                    @Override
                    public void verPwdFailed() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showCusToast(B31HomeActivity.this, getResources().getString(R.string.miamacuo));
                            }
                        });

                        //ToastUtil.showLong(B31HomeActivity.this, getResources().getString(R.string.miamacuo));
                    }

                    @Override
                    public void verPwdSucc() {
                        cusInputDialogView.dismiss();
                    }
                });
            }
        });

    }


    @Override
    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, data);
        String message = getResources().getString(R.string.string_get_permission) + "\n" + permissionNames;

        new android.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }


    public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getDeclaredMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName());
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
