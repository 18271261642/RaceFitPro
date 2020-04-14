package com.suchengkeji.android.w30sblelibrary;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/6 15:19
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SBLEManage {
    private final static String TAG = "====>>>" + W30SBLEManage.class.getSimpleName();
    //    private Handler mHandler;
//    private static long SCAN_PERIOD = 10 * 1000;// 10秒后停止扫描。
    //private ScanLeDeviceCallListenter mScanLeDeviceCallListenter;
    private static W30SBLEManage INSTANCE = null;//单列模式
    private static Context mContext;
    public static W30SBLEServices mW30SBLEServices;
    private static BluetoothManager mBluetoothManager;
    private static BluetoothAdapter mBluetoothAdapter;

    //================== 消息推送类型===========================
    public static final int NotifaceMsgPhone = 0x01;//来电提醒
    public static final int NotifaceMsgQq = 0x02;//QQ
    public static final int NotifaceMsgWx = 0x03;//微信
    public static final int NotifaceMsgMsg = 0x04;//短消息
    public static final int NotifaceMsgSkype = 0x05;//Skype
    public static final int NotifaceMsgWhatsapp = 0x06;//Whatsapp
    public static final int NotifaceMsgFacebook = 0x07;//脸书
    public static final int NotifaceMsgLink = 0x08;//Link
    public static final int NotifaceMsgTwitter = 0x09;//推特
    public static final int NotifaceMsgViber = 0x0a;//Viber
    public static final int NotifaceMsgLine = 0x0b;//Line


    public W30SBLEServices getmW30SBLEServices() {
        if (mW30SBLEServices != null) {
            return mW30SBLEServices;
        } else {
            return null;
        }
    }

    /**
     * 获取蓝牙管理
     *
     * @return
     */
    public static BluetoothManager getmBluetoothManager() {
        if (mW30SBLEServices.getmBluetoothManager() != null) {
            return mW30SBLEServices.getmBluetoothManager();
        } else {
            return (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
    }


    public static BluetoothAdapter getmBluetoothAdapter() {
        if (mW30SBLEServices.getmBluetoothManager() != null) {
            return mW30SBLEServices.getmBluetoothAdapter();
        } else {
            return getmBluetoothManager().getAdapter();
        }
    }

    /**
     * 单列模式，确保唯一性
     *
     * @param context 上下文
     * @return 对象
     */
    public static W30SBLEManage getInstance(Context context) {
        mContext = context;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (INSTANCE == null) {
            synchronized (W30SBLEManage.class) {
                if (INSTANCE == null) INSTANCE = new W30SBLEManage();
            }
        }
        return INSTANCE;
    }


    /**
     * 获取上下文
     *
     * @return context
     */
    Context getContext() {
        return mContext;
    }

    /*********************      BLE指令         **********************/
    /**
     * 同步时间
     * 通过这指令可以获取到，运动，心率，睡眠，设备信息等数据。
     * 需要清空 mBtRecData 和 mNullBuffer 数组
     * <p>
     * //@param mBluetoothGatt
     */
    public void syncTime(W30SBLEServices.CallDatasBackListenter mCallDatasBackListenter) {
        try {
            //        清空 mBtRecData 和 mNullBuffer 数组
            W30SBLEServices.mBtRecData = new byte[400];
            W30SBLEServices.mNullBuffer = new byte[400];
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.syncTime());
            if (mCallDatasBackListenter != null)
                W30SBLEServices.setmCallDatasBackListenter(mCallDatasBackListenter);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void disPhoneCallData(W30SBLEServices.DisPhoneCallListener disPhoneCallListener) {
        if (disPhoneCallListener != null) {
            W30SBLEServices.setDisPhoneCallListener(disPhoneCallListener);
        }
    }


    /**
     * 找手环
     * 这里用找手环举个例子，其他设置单位，设置时间格式之类的，跟这个同理
     * <p>
     * //@param mBluetoothGatt
     */
    public void findDevice() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.findDeviceInstra());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 获取设备信息=电量.版本号
     * 需要清空 mBtRecData 和 mNullBuffer 数组
     */
    public void getDeviceInfo() {
        try {
            //清空 mBtRecData 和 mNullBuffer 数组
            W30SBLEServices.mBtRecData = new byte[400];
            W30SBLEServices.mNullBuffer = new byte[400];
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.getDeviceInfo());//upGradeDevice
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 固件更新
     */
    public void upGradeDevice() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.upGradeDevice());
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * 设置用户资料
     *
     * @param isMale 1:男性 ; 2:女性
     * @param age    年龄
     * @param hight  身高cm
     * @param weight 体重kg
     */
    public void setUserProfile(int isMale, int age, int hight, int weight) {
        try {
            //清空 mBtRecData 和 mNullBuffer 数组
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setUserProfile(isMale, age, hight, weight));
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * 单位设置
     *
     * @param value 0=英制，1=公制
     */
    public void setUnit(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setUnit(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置手环显示的时间格式
     *
     * @param value 时间格式 0代表12小时制 1代表24小时制
     */
    public void setTimeFormat(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setTimeFormat(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 运动心率开关
     *
     * @param value 1=开，0=关
     */
    public void setWholeMeasurement(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setWholeMeasurement(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 抬手亮屏开关
     *
     * @param value 1=开，0=关
     */
    public void setTaiWan(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setTaiWan(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 免打扰开关
     *
     * @param value 1=开，0=关
     */
    public void setDoNotDistrub(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setDoNotDistrub(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置Android手机语言类型英文
     *
     * @param value 1=开，0=关
     * @return
     */
    public void SendAnddroidLanguage(int value) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.SendAnddroidLanguage(value));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 恢复出厂设置
     */
    public void setReboot() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setReboot());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 吃药提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时  = （固定写入4,6,8,12）
     * @param enable    开关
     * @return
     */
    public void setMedicalNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {
        try {
            Log.e("======吃药 数据 ", "开始: " + startHour + "时" + startMin + "分  结束: " + endHour + "时" + endMin + "分  状态: " + enable);
            Log.e("======吃药  ", Arrays.toString(W30SBLEGattAttributes.setSitNotification(startHour, startMin, endHour, endMin, period, enable)));
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setMedicalNotification(startHour, startMin, endHour, endMin, period, enable));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 久坐提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时 = （固定写入1,2,3,4）
     * @param enable    开关
     * @return
     */
    public void setSitNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {
        try {
            Log.e("======久坐 数据 ", "开始: " + startHour + "时" + startMin + "分  结束: " + endHour + "时" + endMin + "分  状态: " + enable);
            Log.e("======久坐  ", Arrays.toString(W30SBLEGattAttributes.setSitNotification(startHour, startMin, endHour, endMin, period, enable)));
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setSitNotification(startHour, startMin, endHour, endMin, period, enable));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 喝水提醒
     *
     * @param startHour 开始-小时
     * @param startMin  开始-分钟
     * @param endHour   结束-小时
     * @param endMin    结束-分钟
     * @param period    间隔时间小时 = （固定写入1,2,3,4）
     * @param enable    开关
     * @return
     */
    public void setDrinkingNotification(int startHour, int startMin, int endHour, int endMin, int period, boolean enable) {
        try {
            Log.e("======喝水 数据 ", "开始: " + startHour + "时" + startMin + "分  结束: " + endHour + "时" + endMin + "分  状态: " + enable);
            Log.e("======喝水  ", Arrays.toString(W30SBLEGattAttributes.setSitNotification(startHour, startMin, endHour, endMin, period, enable)));
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setDrinkingNotification(startHour, startMin, endHour, endMin, period, enable));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 会议设置
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   小时
     * @param min    分钟
     * @param enable 开关
     * @return
     */
    public void setMeetingNotification(int year, int month, int day, int hour, int min, boolean enable) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setMeetingNotification(year, month, day, hour, min, enable));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置闹钟
     *
     * @param clockItems 闹钟L身体
     */
    public void setAlarm(List<W30S_AlarmInfo> clockItems) {
        try {
            for (int i = 0; i < clockItems.size(); i++) {
                String s = clockItems.get(i).toString();
                Log.d("------002-----", s);
            }
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setAlarm(clockItems));
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 设置默认开关指令
     *
     * @param time      = 时钟设置 1=开，0=关
     * @param unit      = 单位设置 1=开，0=关
     * @param bright    = 抬腕亮屏 1=开，0=关
     * @param miandarao = 免打扰开关 1=开，0=关
     * @param woheart   = 运动心率开关 1=开，0=关
     * @return
     */
    public void setInitSet(int time, int unit, int bright, int miandarao, int woheart) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setInitSet(time, unit, bright, miandarao, woheart));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置目标步数
     *
     * @param TargetStep 目标步数
     */
    public void setTargetStep(int TargetStep) {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.setTargetStep(TargetStep));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 发送QQ推送内容给手环
     * 这里举个例子，其他的微信，来电，短信之类的同理
     * <p>
     * //@param mBluetoothGatt
     *
     * @param pn = 具体内容--------W30SBLEGattAttributes.NotifaceMsgQq
     */
    public void notifacePhone(String pn, int notifaceType) {
        try {
            MmgRemind(pn, notifaceType);
        } catch (Exception e) {
            e.getMessage();
        }
    }


    /**
     * 来电接通或挂断
     */
    public void notifyMsgClose() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.notifyMsgClose());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 设备关机
     */
    public void deviceClose() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.deviceClose());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 进入摇一摇拍照
     */
    public void intoShakePicture() {
        try {
            mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.intoShakePicture());
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 消息提醒-写入
     *
     * @param pn       = 具体内容
     * @param app_type = 推送类型
     */
    private void MmgRemind(String pn, int app_type) {
        mW30SBLEServices.writeRXCharacteristic(W30SBLEGattAttributes.notifyMsg(pn, app_type));
    }


//    public boolean isDataReady = false;
//    private byte[] mNullBuffer = new byte[400];
//    public byte[] mBtRecData = new byte[400];
//    private int mRcvDataState = 0;
//    private int received_content_length = 0;
//    private int length_to_receive = 0;
//
//    /**
//     * 数据包组合
//     *
//     * @param data
//     */
//    public void processRcvData(byte[] data) {
//        switch (mRcvDataState) {
//            case 0:
//                if (data[0] == (byte) (0xab)) {
//                    received_content_length = 0;
//                    System.arraycopy(mNullBuffer, 0, mBtRecData,
//                            received_content_length, 100);
//                    System.arraycopy(data, 0, mBtRecData, received_content_length,
//                            data.length);
//                    received_content_length = data.length;
//                    int new_lenght = data[2] << 8 | data[3];
//                    length_to_receive = new_lenght + 8;
//                    length_to_receive -= data.length;
//                    if (length_to_receive <= 0) {
//                        mRcvDataState = 0;
//                        received_content_length = 0;
//                        isDataReady = true;
//                    } else {
//                        mRcvDataState = 1;
//                        isDataReady = false;
//                    }
//                }
//
//                break;
//            case 1:
//                System.arraycopy(data, 0, mBtRecData, received_content_length,
//                        data.length);
//                received_content_length += data.length;
//                length_to_receive -= data.length;
//                if (length_to_receive <= 0) {
//                    mRcvDataState = 0;
//                    isDataReady = true;
//
//
//                } else {
//                    isDataReady = false;
//                }
//                break;
//        }
//    }
    /****************************     启动打开蓝牙服务   *******************************/

    /**
     * 启动打开蓝牙服务
     */
    public void openW30SBLEServices() {
        try {
            //启动W30S蓝牙服务
            Intent gattServiceIntent = new Intent(getContext(), W30SBLEServices.class);
            getContext().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 关闭服务
     *
     * @param context
     */
    public void closeW30SBLEServices(Context context) {
        try {
            if (mW30SBLEServices != null) {
                mW30SBLEServices.close();
                getInstance(context);
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     * 解除服务
     */
    public void unbindW30Services(Context mContext) {
        if (mW30SBLEServices != null && mServiceConnection != null) {
            closeW30SBLEServices(mContext);
            mW30SBLEServices.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }

    /**
     * /**
     * 管理服务生命周期的代码。
     */
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mW30SBLEServices = ((W30SBLEServices.LocalBinder) service).getService();
            if (!mW30SBLEServices.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
//            try {
//                // 在成功启动初始化时自动连接到设备。
//                String mDeviceAddress = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
//                if (!TextUtils.isEmpty(mDeviceAddress)) {
//                    Log.d("---w30s-MAC---", mDeviceAddress);
//                    if (mW30SBLEServices != null) mW30SBLEServices.connect(mDeviceAddress);
//                }
//            } catch (Exception e) {
//                e.getMessage();
//            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mW30SBLEServices = null;
        }
    };


//    /****************************     扫描蓝牙以及扫描回调   *******************************/
//    /**
//     * 扫描或关闭
//     *
//     * @param scan_period     扫描时间
//     * @param enable          是否扫描
//     * @param mLeScanCallback 扫描回掉
//     *                        <p>
//     *                        如果是停止扫描，参数 1 和参数 3 可为空
//     */
//    public void scanLeDevice(long scan_period, final boolean enable, final BluetoothAdapter.LeScanCallback mLeScanCallback) {
//        try {
//            if (enable) {
//                if (mHandler == null) mHandler = new Handler();
//                if (scan_period >= SCAN_PERIOD) {
//                    SCAN_PERIOD = scan_period;
//                }
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (getmBluetoothAdapter() != null)
//                            getmBluetoothAdapter().stopLeScan(mLeScanCallback);
//                    }
//                }, SCAN_PERIOD);
//                if (getmBluetoothAdapter() != null)
//                    getmBluetoothAdapter().startLeScan(mLeScanCallback);
//
//            } else {
//                if (getmBluetoothAdapter() != null)
//                    getmBluetoothAdapter().stopLeScan(mLeScanCallback);
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//
//    }

//    public void scanLeDevice(long scan_period, final boolean enable, ScanLeDeviceCallListenter mScanLeDeviceCallListenter) {
//        try {
//            if (enable) {
//                if (mHandler == null) mHandler = new Handler();
//                if (scan_period >= SCAN_PERIOD) {
//                    SCAN_PERIOD = scan_period;
//                }
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (getmBluetoothAdapter() != null)
//                            getmBluetoothAdapter().stopLeScan(mLeScanCallback);
//                    }
//                }, SCAN_PERIOD);
//                if (getmBluetoothAdapter() != null) getmBluetoothAdapter().startLeScan(mLeScanCallback);
//
//            } else {
//                if (getmBluetoothAdapter() != null) getmBluetoothAdapter().stopLeScan(mLeScanCallback);
//            }
//            setmScanLeDeviceCallListenter(mScanLeDeviceCallListenter);
//        }catch (Exception e){
//            e.getMessage();
//        }
//
//    }

//    /**
//     * 设备扫描回调
//     */
//    BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    try {
//                        mScanLeDeviceCallListenter.ScanLeDeviceCallListenter(device, rssi, scanRecord);
//                    }catch (Exception e){
//                        e.getMessage();
//                    }
//
//                }
//            };
//
//    void setmScanLeDeviceCallListenter(ScanLeDeviceCallListenter mScanLeDeviceCallListenter) {
//        this.mScanLeDeviceCallListenter = mScanLeDeviceCallListenter;
//    }
//
//    public interface ScanLeDeviceCallListenter {
//        void ScanLeDeviceCallListenter(BluetoothDevice device, int rssi, byte[] scanRecord);
//    }
}
