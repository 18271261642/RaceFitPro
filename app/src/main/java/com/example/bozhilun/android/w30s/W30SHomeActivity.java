package com.example.bozhilun.android.w30s;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.b30run.ChildGPSFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.commdbserver.detail.UploadW30DetailService;
import com.example.bozhilun.android.siswatch.utils.BlueAdapterUtils;
import com.example.bozhilun.android.siswatch.utils.PhoneStateListenerInterface;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.w30s.ble.W37HomeFragment;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.w30s.fragment.W30SMineFragment;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/5 16:54
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHomeActivity extends WatchBaseActivity implements PhoneStateListenerInterface {
    private final String TAG = "W30SHomeActivity";
    private List<Fragment> h18iFragmentList = new ArrayList<>();
    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    MyBroadcastReceiver myBroadcastReceivers = null;
    public BluetoothAdapter bluetoothAdapter;
    private String w30SBleName;
    private String phoneNumber;

    //已经连接过到蓝牙mac
    String mylanmac;


    DisPhoneCallBack disPhoneCallBack = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "--------home-w30-=onCreate---");
        setContentView(R.layout.activity_w30s_home);
        ButterKnife.bind(this);
        mylanmac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        w30SBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),  Commont.BLENAME);
        initViews();
        initBuleAdapter();

        myBroadcastReceivers = new MyBroadcastReceiver();
        disPhoneCallBack = new DisPhoneCallBack();
    }


    //判断蓝牙是否打开，未打开强制打开
    private void initBuleAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.enable()) {
            BlueAdapterUtils.getBlueAdapterUtils(W30SHomeActivity.this).turnOnBlue(W30SHomeActivity.this, 10000, 1000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (myBroadcastReceivers == null)
                myBroadcastReceivers = new MyBroadcastReceiver();
            //注册监听电话状态变化的监听
//            MyApp.getCustomPhoneStateListener().setPhoneStateListenerInterface(this);
            //注册连接状态的广播
            registerReceiver(myBroadcastReceivers, makeGattUpdateIntentFilter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_FINDE_AVAILABLE_DEVICE);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (myBroadcastReceivers != null) {
                unregisterReceiver(myBroadcastReceivers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isAutoConnBle();
    }


    private void isAutoConnBle(){
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
        }
    }



    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        //h18iFragmentList.add(new NewW30Fragment()); //记录
        h18iFragmentList.add(new W37HomeFragment()); //记录
        h18iFragmentList.add(new ChildGPSFragment());   //跑步
        h18iFragmentList.add(new W30SMineFragment());   //我的
        if (h18iFragmentList == null) return;
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
    public void callPhoneData(int flag, String phoneNumber) {
        Log.e(TAG, "----falgCallPhone=" + flag + "--num=" + phoneNumber + "--=w30SBleName=" + w30SBleName);
        try {
            this.phoneNumber = phoneNumber;
            if (!W30SHomeActivity.this.isFinishing() && W30SBLEManage.mW30SBLEServices != null) {
                switch (flag) {
                    case 0: //挂断
                        isNo = true;
                        if (w30SBleName != null) {
                            missCallPhone();    //挂断电话
                        }

                        break;
                    case 2: //接通
                        isNo = true;
                        if (w30SBleName != null) {
                            missCallPhone();    //挂断电话
                        }
                        break;
                    case 1: //来电
                        // Log.e(TAG,"---case1来电--="+phoneNumber);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //  handler.sendEmptyMessage(555);
                            }
                        }, 1000);
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    boolean isNo = true;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 555:
                    //Log.e(TAG,"------hand555---="+phoneNumber);
                    if (w30SBleName != null) {
                        getPeople(phoneNumber, MyApp.getContext());
                    }
                    break;
                case 111:   //开始自动连接
                    if (MyCommandManager.DEVICENAME == null && !WatchUtils.isEmpty(mylanmac)) {
                        if(MyApp.getInstance().getW37ConnStatusService() != null){
                            MyApp.getInstance().getW37ConnStatusService().w37AutoBleDevice();
                        }
                    }
                    MyApp.getInstance().getmW30SBLEManage().disPhoneCallData(disPhoneCallBack);
                    break;
                case 122:
                    String pNum = (String) msg.obj;
                    //Log.e(TAG,"---hand122="+pNum);
                    boolean zh = VerifyUtil.isZh(W30SHomeActivity.this);
                    if (!zh){
                        Log.e(TAG,"========主界面 A  -- 设置了英文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(0);
                    }else {
                        Log.e(TAG,"========主界面 A设置了中文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(1);
                    }
                    MyApp.getInstance().getmW30SBLEManage().disPhoneCallData(disPhoneCallBack);
                    if (!WatchUtils.isEmpty(pNum)) {
                        boolean isW30Phone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "w30sswitch_Phone", true);
                        if (isW30Phone) {
                            getNameByPhone(pNum);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    //挂断电话
    private void missCallPhone() {
        if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName)) {
            if (MyCommandManager.DEVICENAME != null) {
                MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
                MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
            }
        }
    }

    /**
     * 通过输入获取电话号码
     */
    public void getPeople(String nunber, Context context) {
        Message message = new Message();
        message.what = 122;
        message.obj = nunber;
        handler.sendMessage(message);

    }


    private class DisPhoneCallBack implements W30SBLEServices.DisPhoneCallListener {
        @Override
        public void disCallPhone(int disV) {
            Log.e(TAG, "---11--dev---" + disV);
            TelephonyManager tm = (TelephonyManager) W30SHomeActivity.this
                    .getSystemService(Service.TELEPHONY_SERVICE);
            PhoneUtils.endPhone(W30SHomeActivity.this, tm);
            PhoneUtils.dPhone();
            PhoneUtils.endCall(MyApp.getContext());
            PhoneUtils.endcall();
        }
    }

    private void getNameByPhone(String phNumber) {
        Log.e(TAG,"---getNameByPhone-=="+phNumber);
        boolean isPhoneNum = true; //判断通讯录中是否保存了联系人
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        if (cr == null) return;
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            //向下移动光标
            while (cursor.moveToNext()) {
                //取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                String contact = cursor.getString(nameFieldColumnIndex);
                //Log.e(TAG,"---contact="+contact);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (WatchUtils.isEmpty(ContactId)) return;
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                if (phone != null) {
                    while (phone.moveToNext()) {
                        String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!WatchUtils.isEmpty(PhoneNumber)) {
                            Log.e(TAG,"---------获取的电话号码="+PhoneNumber);
                            //格式化手机号
                            if(phoneNumber.contains("-"))
                              PhoneNumber = PhoneNumber.replace("-", "");
                            if(PhoneNumber.contains(" "))
                              PhoneNumber = PhoneNumber.replace(" ", "");
                            // Log.e(TAG,"---PhoneNumber="+PhoneNumber);
                            if (phNumber.equals(PhoneNumber)) {
                                isPhoneNum = false;
                                int nameFieldColumnIndexs = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                                String contacts = cursor.getString(nameFieldColumnIndexs) + "";
                                MyApp.getInstance().getmW30SBLEManage().notifacePhone(contacts + PhoneNumber, 0x01);

                            }
                        }

                    }
                }
                //通讯录中无保存联系人时，直接发送电话号码
//                if(isPhoneNum){
//                    MyApp.getmW30SBLEManage().notifacePhone(phNumber,0x01);
//                }

            }
            cursor.close();
        }
        //通讯录为空时直接发送电话号码
        if (isPhoneNum) {
            MyApp.getInstance().getmW30SBLEManage().notifacePhone(phNumber, 0x01);
        }
    }

    //启动上传数据
    public void startW30UploadServer(){
        try {
            //开始上传本地数据
            //开始上传本地的数据
            CommDBManager.getCommDBManager().startUploadDbService(W30SHomeActivity.this);

            Intent intent = new Intent(W30SHomeActivity.this,UploadW30DetailService.class);
            W30SHomeActivity.this.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
