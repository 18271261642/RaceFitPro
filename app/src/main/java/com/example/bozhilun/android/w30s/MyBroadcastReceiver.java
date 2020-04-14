package com.example.bozhilun.android.w30s;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.VerifyUtil;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjd.tjdmain.icentre.ICC_Contents;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @aboutContent: W30S链接状态广播
 * @author： An
 * @crateTime: 2018/3/9 10:52
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String W30_CONNECTION_ACTION = "com.example.bozhilun.android.w30s.connect";
    public static final String W30_DISCONNECTION_ACTION = "com.example.bozhilun.android.w30s.disconnect";


    private final String TAG = "MyBroadcastReceiver";
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;

    private OnW30ConnstatusListener onW30ConnstatusListener;

    public void setOnW30ConnstatusListener(OnW30ConnstatusListener onW30ConnstatusListener) {
        this.onW30ConnstatusListener = onW30ConnstatusListener;
    }

    //    Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            try {
//                if (msg.what == 0x01) {
//                    if (mBluetoothStateListenter != null)
//                        mBluetoothStateListenter.BluetoothStateListenter();
//                    mHandler.removeMessages(0x01);
//                    mHandler.sendEmptyMessageDelayed(0x02, 500);
//                } else if (msg.what == 0x02) {
//                    if (mBluetoothStateListenter != null)
//                        mBluetoothStateListenter.BluetoothStateListenter();
//                    mHandler.removeMessages(0x02);
//                    mHandler.sendEmptyMessageDelayed(0x03, 500);
//                } else if (msg.what == 0x03) {
//                    if (mBluetoothStateListenter != null)
//                        mBluetoothStateListenter.BluetoothStateListenter();
//                    mHandler.removeMessages(0x03);
//                    mHandler.sendEmptyMessageDelayed(0x04, 500);
//                } else if (msg.what == 0x04) {
//                    if (mBluetoothStateListenter != null)
//                        mBluetoothStateListenter.BluetoothStateListenter();
//                    mHandler.removeMessages(0x04);
//                }
//            } catch (Exception e) {
//                e.getMessage();
//            }
//            return false;
//        }
//    });


    public MyBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;
        Log.e(TAG, "--------W30S链接状态广播===BROAD===" + action);
        switch (action) {
            case W30SBLEServices.ACTION_GATT_CONNECTED:

                try {
//                    MyCommandManager.DEVICENAME = "W30";
//                    syncUserInfoData(context);
//                    SharePeClear.sendCmdDatas(context);
//                    MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(0x01);
                    boolean zh = VerifyUtil.isZh(context);
                    if (!zh) {
                        Log.e(TAG, "======== 链接监听   -- 设置了英文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(0);
                    } else {
                        Log.e(TAG, "======= 链接监听   =设置了中文");
                        MyApp.getInstance().getmW30SBLEManage().SendAnddroidLanguage(1);
                    }
                    //连接成功
                    if (onW30ConnstatusListener != null)
                        onW30ConnstatusListener.OnConnstatus();

//                    String mylanmac = (String) SharedPreferenceUtil.get(context, "mylanmac", "");
//                    SharedPreferenceUtil.put(context, "W30S_MAC", mylanmac);


//                    SharePeClear.clearDatas(context);
//                    SharedPreferencesUtils.setParam(context, "W30S_MAC", (String) SharedPreferenceUtil.get(context, "mylanmac", ""));
//                    SharePeClear.clearDatas(context);//数据默认清除
//                    /**
//                     * 设置默认开关指令
//                     *
//                     * @param time      = 时钟设置 1=开，0=关
//                     * @param unit      = 单位设置 1=开，0=关
//                     * @param bright    = 抬腕亮屏 1=开，0=关
//                     * @param miandarao = 免打扰开关 1=开，0=关
//                     * @param woheart   = 运动心率开关 1=开，0=关
//                     * @return
//                     */
//                    MyApp.getmW30SBLEManage().setInitSet(1, 1, 1, 0, 1);
//                    syncUserInfoData(context);

                    //SharedPreferencesUtils.setParam(context, "W30S_MAC", (String) SharedPreferenceUtil.get(context, "mylanmac", ""));
//                    boolean w30stimeformat = (boolean) SharedPreferenceUtil.get(context, "w30stimeformat", true);
//
//                    boolean w30sunit = (boolean) SharedPreferenceUtil.get(context, "w30sunit", true);
//                    //抬手亮屏
//                    boolean w30sBrightScreen = (boolean) SharedPreferenceUtil.get(context, "w30sBrightScreen", true);
//                    //免扰
//                    boolean w30sNodisturb = (boolean) SharedPreferenceUtil.get(context, "w30sNodisturb", false);
//                    //运动心率
//                    boolean w30sHeartRate = (boolean) SharedPreferenceUtil.get(context, "w30sHeartRate", true);
//                    int a= 1;
//                    int b= 1;
//                    int c= 1;
//                    int d = 1;
//                    int e = 1;
//                    if (w30sBrightScreen){
//                        a = 1;
//                    }else {
//                        a = 0;
//                    }
//                    if (w30sNodisturb){
//                        b = 1;
//                    }else {
//                        b = 0;
//                    }
//                    if (w30sHeartRate){
//                        c = 1;
//                    }else {
//                        c = 0;
//                    }
//
//                    if (w30stimeformat){
//                        d = 1;
//                    }else {
//                        d = 0;
//                    }
//
//                    if (w30sunit){
//                        e = 1;
//                    }else {
//                        e = 0;
//                    }
//
//                    /**
//                     * 设置默认开关指令
//                     *
//                     * @param time      = 时钟设置 1=开，0=关
//                     * @param unit      = 单位设置 1=开，0=关
//                     * @param bright    = 抬腕亮屏 1=开，0=关
//                     * @param miandarao = 免打扰开关 1=开，0=关
//                     * @param woheart   = 运动心率开关 1=开，0=关
//                     * @return
//                     */
//                    MyApp.getmW30SBLEManage().setInitSet(d, e, a, b, c);
//                    List<W30SAlarmClockBean> mAlarmClock = DataSupport.findAll(W30SAlarmClockBean.class);
//                    if (mAlarmClock!=null){
//                        List<W30S_AlarmInfo> w30S_alarmInfos = new ArrayList<W30S_AlarmInfo>();
//                        for (int i = 0; i < mAlarmClock.size(); i++) {
//                            W30S_AlarmInfo w30S_alarmInfo = mAlarmClock.get(i).w30AlarmInfoChange();
//                            w30S_alarmInfos.add(w30S_alarmInfo);
//                        }
//                        MyApp.getmW30SBLEManage().setAlarm(w30S_alarmInfos);
//                    }

                    //mHandler.sendEmptyMessageDelayed(0x01, 500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case W30SBLEServices.ACTION_GATT_DISCONNECTED:
                try {
                    MyCommandManager.DEVICENAME = null;
                    if (onW30ConnstatusListener != null)
                        onW30ConnstatusListener.onDisConnstatus();

                    if (mBluetoothStateListenter != null)
                        mBluetoothStateListenter.BluetoothStateListenter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ICC_Contents.ToUi:
                try {
                    String msgData1 = intent.getStringExtra(ICC_Contents.ToUi_D1);
                    if (WatchUtils.isEmpty(msgData1))
                        return;

                    if (msgData1.contains("FindPhone_Ring")) {

                        Log.e(TAG, "叉着手还---" + msgData1);
                        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                        mMediaPlayer = new MediaPlayer();
                        AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.music);
                        try {
                            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                                    file.getLength());
                            mMediaPlayer.prepare();
                            file.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mMediaPlayer.setVolume(0.5f, 0.5f);
                        mMediaPlayer.setLooping(false);
                        mMediaPlayer.start();
                        if (mVibrator.hasVibrator()) {
                            //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                            mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case W30SBLEServices.ACTION_FINDE_AVAILABLE_DEVICE://着手机
                try {
                    if (!WatchUtils.isEmpty(intent.getAction()) && intent.getAction().equals(W30SBLEServices.ACTION_FINDE_AVAILABLE_DEVICE)) {
                        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                        mMediaPlayer = new MediaPlayer();
                        AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.music);
                        try {
                            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                                    file.getLength());
                            mMediaPlayer.prepare();
                            file.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mMediaPlayer.setVolume(0.5f, 0.5f);
                        mMediaPlayer.setLooping(false);
                        mMediaPlayer.start();
                        if (mVibrator.hasVibrator()) {
                            //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                            mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case W30SBLEServices.ACTION_CALLER_REJECTION://来电拒接
                try {
                    String w30SBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
                    if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
                        if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
                            rejectCall();
                            MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
                            MyApp.getInstance().getmW30SBLEManage().notifyMsgClose();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 通过代码挂断电话
     */
    public void rejectCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //同步用户信息
    private void syncUserInfoData(Context context) {
        String userData = (String) SharedPreferencesUtils.readObject(context, "saveuserinfodata");
        //Log.d("-----用户资料-----1----", "--------" + userData);
        if (!WatchUtils.isEmpty(userData)) {
            try {
                int weight;
                JSONObject jsonO = new JSONObject(userData);
                String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                String userAge = jsonO.getString("birthday");   //生日
                String userWeight = jsonO.getString("weight");  //体重
                String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
                //Log.d("-----用户资料-----2----", userWeight + "====" + tempWeight);
                if (tempWeight.contains(".")) {
                    weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim());
                } else {
                    weight = Integer.valueOf(tempWeight);
                }
                String userHeight = ((String) SharedPreferencesUtils.getParam(context, "userheight", "")).trim();
                int sex;
                if (userSex.equals("M")) {    //男
                    sex = 1;
                } else {
                    sex = 2;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight);


                /**
                 * 设置用户资料
                 *
                 * @param isMale 1:男性 ; 2:女性
                 * @param age    年龄
                 * @param hight  身高cm
                 * @param weight 体重kg
                 */
                SharedPreferencesUtils.setParam(context, "user_sex", sex);
                SharedPreferencesUtils.setParam(context, "user_age", age);
                SharedPreferencesUtils.setParam(context, "user_height", height);
                SharedPreferencesUtils.setParam(context, "user_weight", weight);
                //Log.d("-----用户资料-----2----", sex + "===" + age + "===" + height + "===" + weight);
                /**
                 * 设置用户资料
                 *
                 * @param isMale 1:男性 ; 2:女性
                 * @param age    年龄
                 * @param hight  身高cm
                 * @param weight 体重kg
                 */
                MyApp.getInstance().getmW30SBLEManage().setUserProfile(sex, age, height, weight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static BluetoothStateListenter mBluetoothStateListenter;

    public static void setmBluetoothStateListenter(BluetoothStateListenter mBluetoothStateListenter) {
        MyBroadcastReceiver.mBluetoothStateListenter = mBluetoothStateListenter;
    }

    public interface BluetoothStateListenter {
        void BluetoothStateListenter();
    }


    public interface OnW30ConnstatusListener {
        void OnConnstatus();

        void onDisConnstatus();
    }


}
