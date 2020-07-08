package com.example.bozhilun.android.w30s.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.PhoneUtile;
import com.example.bozhilun.android.util.VerifyUtil;
import com.suchengkeji.android.w30sblelibrary.W30SBLEGattAttributes;
import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;
import com.suchengkeji.android.w30sblelibrary.bean.W30S_SleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SDeviceData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SHeartData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30SSportData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.W30SBleUtils;
import com.yanzhenjie.permission.AndPermission;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Admin
 * Date 2019/7/4
 */
public class W37DataAnalysis {
    private static final String TAG = "W37DataAnalysis";


    private volatile static W37DataAnalysis w37DataAnalysis;

    //开始拍照的指令
    public static final String W37_CAMERA_TAKE_PHOTO = "com.example.bozhilun.android.w30s.ble.take_photo";
    //退出拍照的指令
    public static final String W37_CAMERA_DIS_TAKE_PHOTO = "com.example.bozhilun.android.w30s.ble.dis_take_photo";

    public static boolean isDataReady = false;
    public static byte[] mNullBuffer = new byte[600];
    public static byte[] mBtRecData = new byte[600];
    private static int mRcvDataState = 0;
    private static int received_content_length = 0;
    private static int length_to_receive = 0;

    // 蓝牙KEY
    private static final int Key_Motion = 0x02;//运动步数
    private static final int Key_Sleep = 0x03;//睡眠
    private static final int Key_Complete = 0x04;//完成标志
    private static final int WKey_Photo = 0x06;//遥控拍照
    private static final int Key_FindPhone = 0x07;
    private static final int Key_DeviceInfo = 0x08;
    private static final int Key_HangPhone = 0x0c;//测量心率
    private static final int Key_MoHeart = 0x0d;//运动心率
    private static final int key_Blood = 0x1d;   //血压


    private Vibrator mVibrator;

    private MediaPlayer mMediaPlayer;


    private InterfaceManager interfaceManager = new InterfaceManager();




    public static W37DataAnalysis getW37DataAnalysis() {
        if (w37DataAnalysis == null) {
            synchronized (W37DataAnalysis.class) {
                if (w37DataAnalysis == null)
                    w37DataAnalysis = new W37DataAnalysis();
            }
        }
        return w37DataAnalysis;
    }


    public W37DataAnalysis() {

    }


    /**
     * 设置语言
     * @param context
     */
    public void setDeviceLanguage(Context context){
        boolean zh = VerifyUtil.isZh(context);
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.SendAnddroidLanguage(zh ? 1 : 0), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //同步用户信息
    public void syncUserInfoData(Context context) {
        String userData = (String) SharedPreferencesUtils.readObject(context, "saveuserinfodata");
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
                if(WatchUtils.isEmpty(userHeight))
                    userHeight = "170";
                if(userHeight.contains("cm"))
                    userHeight = StringUtils.substringBefore(userHeight,"cm");
                int sex;
                if (userSex.equals("M")) {    //男
                    sex = 1;
                } else {
                    sex = 2;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight.trim());


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
                MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setUserProfile(sex, age, height, weight), new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {

                    }
                });
               // MyApp.getInstance().getmW30SBLEManage().setUserProfile(sex, age, height, weight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //APP消息推送
    public void sendAppalertData(String contxt,int appType){
        byte[] notiMsg = W30SBLEGattAttributes.notifyMsg(contxt, appType);
        int length = notiMsg.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < 20) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = notiMsg[i + copy_size];
                }
                MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(val, new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {
                        Log.e(TAG,"------app消息通知="+Arrays.toString(data));
                    }
                });
            } else {
                byte[] val = new byte[20];
                for (int i = 0; i < 20; i++) {
                    val[i] = notiMsg[i + copy_size];
                }
                MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(val, new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {
                        Log.e(TAG,"------app消息通知="+Arrays.toString(data));
                    }
                });
            }
            copy_size += 20;
            length -= 20;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //设置目标步数
    public void setW37GoalStep(int goalStep){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setTargetStep(goalStep), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    /**
     * 接入摇一摇拍照
     */
    public void intoTakePhotoStatus(){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.intoShakePicture(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }


    //设置默认的指令
    public  void sendCmdDatas(Context context) {
        //时间格式
        boolean w30stimeformat = (boolean) SharedPreferencesUtils.getParam(context, "w30stimeformat", true);
        //单位
        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(context, "w30sunit", true);
        //抬手亮屏
        boolean w30sBrightScreen = (boolean) SharedPreferencesUtils.getParam(context, "w30sBrightScreen", true);
        //免扰
        boolean w30sNodisturb = (boolean) SharedPreferencesUtils.getParam(context, "w30sNodisturb", false);
        //运动心率
        boolean w30sHeartRate = (boolean) SharedPreferencesUtils.getParam(context, "w30sHeartRate", true);
        int a = 1;
        int b = 0;
        int c = 1;
        int d = 1;
        int e = 1;
        if (w30sBrightScreen) {
            a = 1;
        } else {
            a = 0;
        }
        if (w30sNodisturb) {
            b = 1;
        } else {
            b = 0;
        }
        if (w30sHeartRate) {
            c = 1;
        } else {
            c = 0;
        }
        if (w30stimeformat) {
            d = 1;
        } else {
            d = 0;
        }
        if (w30sunit) {
            e = 1;
        } else {
            e = 0;
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
        Log.e("-----zza=====   ", w30sunit + "");
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setInitSet(d,e,a,b,c));
    }


    //设置闹钟
    public void setW37AlarmData(List<W30S_AlarmInfo> clockItems){
        byte[] alarmData = W30SBLEGattAttributes.setAlarm(clockItems);
        int length = alarmData.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < 20) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = alarmData[i + copy_size];
                }
                MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(val, new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {

                    }
                });
            } else {
                byte[] val = new byte[20];
                for (int i = 0; i < 20; i++) {
                    val[i] = alarmData[i + copy_size];
                }
                MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(val, new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {

                    }
                });
            }
            copy_size += 20;
            length -= 20;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //会议提醒
    public void setMeetingNotiMsg(int year, int month, int day, int hour, int min, boolean enable){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setMeetingNotification(year,  month,  day,  hour, min,  enable), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //喝水提醒
    public void setDringNotiMsg(int startHour, int startMin, int endHour, int endMin, int period, boolean enable){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setDrinkingNotification(startHour, startMin, endHour, endMin, period, enable), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //久坐提醒
    public void setLongDownSitNotiMsg(int startHour, int startMin, int endHour, int endMin, int period, boolean enable){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setSitNotification(startHour, startMin, endHour, endMin, period, enable));
    }

    //吃药提醒
    public void setMealcalNotiMsg(int startHour, int startMin, int endHour, int endMin, int period, boolean enable){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setMedicalNotification( startHour,  startMin,  endHour,  endMin,  period,  enable));
    }

    //恢复出厂设置
    public void setResetDevice(){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setReboot());
    }

    //设置抬手亮屏的开关1=开，0=关
    public void setTurnHand(int value){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setTaiWan(value), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //运动心率的开关 1=开，0=关
    public void setHeartSwitch(int value){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setWholeMeasurement(value), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //设置手环显示的时间格式 时间格式 0代表12小时制 1代表24小时制
    public void setDeviceTimeStyle(int value){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(W30SBLEGattAttributes.setTimeFormat(value), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //单位设置 0=英制，1=公制
    public void setDeviceUnit(int value){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setUnit(value));
    }

    //找设备
    public void findDevice(){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.findDeviceInstra());
    }

    //免打扰开关
    public void setDeviceNoNoti(int value){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.setDoNotDistrub(value));
    }


    //来电接通或挂断电话
    public void disPhone(){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.notifyMsgClose());
    }


    //调用代码挂断电话
    public  void disCallPhone(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if(!AndPermission.hasPermissions(MyApp.getContext(), Manifest.permission.ANSWER_PHONE_CALLS))
                    return;
                TelecomManager tm = (TelecomManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    @SuppressLint("MissingPermission") boolean success = tm.endCall();
                }

            } else {
                TelephonyManager tm = (TelephonyManager) MyApp.getContext()
                        .getSystemService(Service.TELEPHONY_SERVICE);
                PhoneUtils.endPhone(MyApp.getContext(), tm);
                PhoneUtils.dPhone();
                PhoneUtils.endCall(MyApp.getContext());
            }

        } catch (Exception e){
            e.printStackTrace();
        }catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }



    //发送开始固件升级的指令
    public void sendOtaNoti(){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(W30SBLEGattAttributes.upGradeDevice());
    }


    //写入指令，之定义的指令
    public void sendInputData(byte[] inputData,WriteBackDataListener writeBackDataListener){
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(inputData,writeBackDataListener);
    }




    //获取并解析运动数据
    public void analysW37BackData(byte[] writeData, final AllBackDataListener allBackDataListener) {
        interfaceManager.setAllBackDataListener(allBackDataListener);
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(writeData, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if (data.length > 0) {
                    processRcvData(data);
                }
                if (isDataReady)
                    parseRcvData(mBtRecData, allBackDataListener);
            }
        });
    }

    private StringBuilder stringBuilder = new StringBuilder();

    private void parseRcvData(byte[] data,AllBackDataListener allBackDataListener){
        String dataStr = Arrays.toString(data);
        Log.e(TAG,"--------所有原始数据="+dataStr);
        stringBuilder.append(dataStr+"\n");
        if(allBackDataListener != null){
            allBackDataListener.allDataBack(stringBuilder.toString());
        }

    }


    /**
     * 获取设备信息
     * @param writeData
     * @param bleDeviceDataListener
     */
    public void getDeviceInfo(byte[] writeData, final BleDeviceDataListener bleDeviceDataListener){
        interfaceManager.setBleDeviceDataListener(bleDeviceDataListener);
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(writeData, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if (data.length > 0) {
                    processRcvData(data);
                }
                if (isDataReady)
                    parseRcvData2(mBtRecData,bleDeviceDataListener);
            }
        });
    }



    //获取并解析运动数据
    public void analysW37BackData(byte[] writeData, final CallDatasBackListenter callDatasBackListenter) {
        interfaceManager.setCallDatasBackListenter(callDatasBackListenter);
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDevice(writeData, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if (data.length > 0) {
                    processRcvData(data);
                }
                if (isDataReady)
                    parseRcvData(mBtRecData, callDatasBackListenter);
            }
        });
    }



    //解析数据
    private void parseRcvData2(byte[] data, BleDeviceDataListener bleDeviceDataListener) {
        Log.e(TAG,"----------返回所有数据="+Arrays.toString(data));
        if (data[0] != (byte) 0xab)
            return;
        if (data[8] != 3)
            return;
        Log.e(TAG, "-------data[10]="+data[10] + "----data[8]="+data[8]);
        switch (data[10]) {
            case Key_DeviceInfo: {//设备信息
                Log.e("===========W30-Data=", "Key_DeviceInfo");
                Log.d(TAG, "-parseRcvData-------设备信息");
                analysDeviceData2(data, bleDeviceDataListener);
            }
            break;
        }
    }


    //解析数据
    private void parseRcvData(byte[] data, CallDatasBackListenter callDatasBackListenter) {
        Log.e(TAG,"----------返回所有数据="+Arrays.toString(data));
        if (data[0] != (byte) 0xab)
            return;
        if (data[8] != 3)
            return;
        Log.e(TAG, "-------data[10]="+data[10] + "----data[8]="+data[8]);
        switch (data[10]) {

            case key_Blood:  //W37血压功能，每5分钟一个点 W30，W31无此功能
                boolean checkDatas = checkData(data);
                Log.e(TAG,"---------这是血压数据="+checkDatas);
                String date = W30SBleUtils.getDate(data);
                Log.e(TAG,"--血压----data="+date);
                analysisBloodData(data,callDatasBackListenter);

                break;
            case Key_Motion: { // 返回运动数据
                boolean checkData = checkData(data);
                if (checkData) {
                    HandleMotion(data, 175, 60, callDatasBackListenter);
                    Log.d(TAG, "-parseRcvData-------返回运动数据");
                }
            }
            break;
            case Key_Sleep: { //睡眠数据
                boolean checkData = checkData(data);
                if (checkData) {
                    analysSleepData(data, callDatasBackListenter);
                    Log.d(TAG, "-parseRcvData-------睡眠数据");
                }
            }
            break;
            case Key_Complete: {//完成;
                Log.e("===========W30-Data=", "Key_Complete");
                callDatasBackListenter.callDatasBackListenterIsok();
            }
            break;
            case WKey_Photo: {// 遥控拍照
                Log.e("===========W30-Data=", "Key_Photo");
                Log.e(TAG, "-parseRcvData-------遥控拍照");
                //broadcastUpdate(ACTION_CAMERA_AVAILABLE_DEVICE);
                if(interfaceManager.w37TakePhoneInterface != null)
                 interfaceManager.w37TakePhoneInterface.takeW37Phone();
            }
            break;
            case Key_FindPhone: { //找手机
                Log.e("===========W30-Data=", "Key_FindPhone");
                System.out.println("找手机");
                Log.e(TAG, "-parseRcvData-------找手机");
                findPhone();
            }
            break;
            case Key_DeviceInfo: {//设备信息
                Log.e("===========W30-Data=", "Key_DeviceInfo");
                Log.d(TAG, "-parseRcvData-------设备信息");
                analysDeviceData(data, callDatasBackListenter);
            }
            break;
            case Key_HangPhone: {  //来电拒接
                Log.e("===========W30-Data=", "Key_HangPhone");
                Log.d(TAG, "-parseRcvData-------来电拒接");
                System.out.println("来电拒接");
            }
            break;
            case Key_MoHeart: { //运动心率
                Log.e("===========W30-Data=", "Key_MoHeart");
                boolean checkData = checkData(data);
                if (checkData) {
                    analysHeartData(data, callDatasBackListenter);
                    Log.d(TAG, "-parseRcvData-------运动心率");
                }
            }
            break;
        }
    }

    //解析血压数据
    private void analysisBloodData(byte[] data,CallDatasBackListenter callDatasBackListenter) {
        String dateStr = W30SBleUtils.getDate(data);
        int blood_data_pos = 17;
        int blood_item_count = 288 * 2;
        List<Integer> wo_blood_data = new ArrayList();
        for (int i = 0; i < blood_item_count; i++) {
            int heart_value = data[blood_data_pos + i] & 0xFF;
            wo_blood_data.add(heart_value);
        }

        W37BloodBean w37BloodBean = new W37BloodBean(dateStr,wo_blood_data);
        Log.e(TAG,"----------血压数据="+w37BloodBean.toString());
        if(callDatasBackListenter != null)
            callDatasBackListenter.callDatasBackBloodListener(w37BloodBean);
    }

    //查找手机
    private void findPhone(){
        try {
            mVibrator = (Vibrator) MyApp.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            mMediaPlayer = new MediaPlayer();
            AssetFileDescriptor file = MyApp.getContext().getResources().openRawResourceFd(R.raw.music);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 解析心率数据
     *
     * @param data
     * @param callDatasBackListenter
     */
    private void analysHeartData(byte[] data, CallDatasBackListenter callDatasBackListenter) {
        Log.e(TAG, "解析运动心率数据");
        String date = W30SBleUtils.getDate(data);
        int heart_data_pos = 17;
        int heart_item_count = 288;
        List wo_heart_data = new ArrayList();
        for (int i = 0; i < heart_item_count; i++) {
            int heart_value = data[heart_data_pos + i] & 0xFF;
            wo_heart_data.add(heart_value);
        }
        Log.e(TAG, "解析运动心率 数据 =  " + wo_heart_data.toString() + "=====" + wo_heart_data.size());
        Log.e(TAG, "解析运动心率 日期 =  " + date);
        W30SHeartData heartData = new W30SHeartData(date, wo_heart_data);
        if (callDatasBackListenter != null)
            callDatasBackListenter.callDatasBackHeartListenter(heartData);
    }


    /**
     * 解析设备信息
     *
     * @param
     */
    private void analysDeviceData2(byte[] data, BleDeviceDataListener bleDeviceDataListener) {
        Log.e(TAG, "解析设备信息");
        int DevicePower = (int) data[13];
        int DeviceType = (int) data[14];
        int DeviceVersionNumber = (int) data[15];
        Log.d(TAG, "解析设备信息 = 设备电量 = " + DevicePower);
        Log.d(TAG, "解析设备信息 = 设备类型 = " + DeviceType);
        Log.d(TAG, "解析设备信息 = 设备版本 = " + DeviceVersionNumber);
        W30SDeviceData deviceData = new W30SDeviceData(DevicePower, DeviceType, DeviceVersionNumber);
        bleDeviceDataListener.callBleDeviceData(deviceData);

    }


    /**
     * 解析设备信息
     *
     * @param data
     * @param callDatasBackListenter
     */
    private void analysDeviceData(byte[] data, CallDatasBackListenter callDatasBackListenter) {
        Log.e(TAG, "解析设备信息");
        int DevicePower = (int) data[13];
        int DeviceType = (int) data[14];
        int DeviceVersionNumber = (int) data[15];
        Log.d(TAG, "解析设备信息 = 设备电量 = " + DevicePower);
        Log.d(TAG, "解析设备信息 = 设备类型 = " + DeviceType);
        Log.d(TAG, "解析设备信息 = 设备版本 = " + DeviceVersionNumber);
        W30SDeviceData deviceData = new W30SDeviceData(DevicePower, DeviceType, DeviceVersionNumber);
        callDatasBackListenter.callDatasBackDeviceDataListenter(deviceData);

    }


    /**
     * 解析睡眠数据
     *
     * @param data
     */
    private void analysSleepData(byte[] data, CallDatasBackListenter callDatasBackListenter) {
        Log.e(TAG, "解析睡眠数据");
        int sleep_count_item = data[15];
        int sleep_data_pos = 16;
        String sleep_date = "";
        List<W30S_SleepData> w30SSleepDataList = new ArrayList<W30S_SleepData>();
        W30S_SleepData mW30SSleepData = null;
        sleep_date = W30SBleUtils.getBeforeDay(W30SBleUtils.getDate(data));
        int end_hour0 = (int) (data[sleep_data_pos] & 0xf8) >> 3;
        if (end_hour0 >= 23 || end_hour0 < 8) {
            mW30SSleepData = new W30S_SleepData("0", "23:00");
            w30SSleepDataList.add(mW30SSleepData);
        }
        for (int i = 0; i < sleep_count_item; i++) {
            byte cca1[] = new byte[2];
            cca1[0] = data[sleep_data_pos + 2 * i];
            cca1[1] = data[sleep_data_pos + 2 * i + 1];
            int start_hour, start_min;
            int sleep_type;
            start_hour = (int) (data[sleep_data_pos + 2 * i] & 0xf8) >> 3;
            start_min = (int) (data[sleep_data_pos + 2 * i] & 0x7) << 3
                    | (int) (data[sleep_data_pos + 2 * i + 1] & 0xE0) >> 5;
            String sHour, sMin;
            if (start_hour < 10) sHour = "0" + start_hour;
            else sHour = "" + start_hour;
            if (start_min < 10) sMin = "0" + start_min;
            else sMin = "" + start_min;
            sleep_type = data[sleep_data_pos + i * 2 + 1] & 0x0F;
            mW30SSleepData = new W30S_SleepData(String.valueOf(sleep_type), (sHour + ":" + sMin));
            w30SSleepDataList.add(mW30SSleepData);
        }
        Log.d(TAG, "解析睡眠数据 = 日期 = " + sleep_date);
        Log.d(TAG, "解析睡眠数据 = 数据 = " + w30SSleepDataList.toString());
        List<W30S_SleepDataItem> w30SSleepDataItems = new ArrayList<>();
        for (int i = 0; i < w30SSleepDataList.size(); i++) {
            W30S_SleepDataItem w30SSleepDataItem = new W30S_SleepDataItem();
            w30SSleepDataItem.setSleep_type(w30SSleepDataList.get(i).getSleep_type());
            w30SSleepDataItem.setStartTime(w30SSleepDataList.get(i).getStartTime());
            w30SSleepDataItems.add(w30SSleepDataItem);
        }

        W30SSleepData sleepData = new W30SSleepData(sleep_date, w30SSleepDataItems);
        callDatasBackListenter.callDatasBackSleepListenter(sleepData);

    }


    /**
     * 解析运动数据
     *
     * @param data
     */
    private void HandleMotion(byte[] data, int user_height, int user_weight, CallDatasBackListenter callDatasBackListenter) {
        Log.d(TAG, "解析运动数据");
        String date = W30SBleUtils.getDate(data);
        int sport_item_count = data[16] & 0x1F;
        int sport_data_pos = 17;
        int sportStep = 0;
        List sport_data = new ArrayList();
        for (int i = 0; i < sport_item_count; i++) {
            int step = (int) ((((int) data[sport_data_pos] & 0xff) << 8) | data[sport_data_pos + 1] & 0xff);
            sport_data.add(step);
            sportStep += step;
            sport_data_pos += 2;
        }
        float Calory = 0;
        float Distance = 0;
        Calory = getCalory((float) user_height, (float) user_weight, sportStep);
        Distance = getDistance((float) user_height, sportStep);
        Log.e(TAG, "解析运动数据 日期 =  " + date);
        Log.e(TAG, "解析运动数据 步数 =  " + sportStep);
        Log.e(TAG, "解析运动数据 卡路里 =  " + Calory);
        Log.e(TAG, "解析运动数据 距离 =  " + Distance);
        Log.e(TAG, "解析运动数据 数据 =  " + sport_data.toString());
        W30SSportData w30SSportData = new W30SSportData(date, sportStep, Calory, Distance, sport_data);
        callDatasBackListenter.callDatasBackSportListenter(w30SSportData);


    }


    /**
     * 数据包组合
     *
     * @param data
     */
    public void processRcvData(byte[] data) {
        switch (mRcvDataState) {
            case 0:
                if (data[0] == (byte) (0xab)) {
                    received_content_length = 0;
                    System.arraycopy(mNullBuffer, 0, mBtRecData,
                            received_content_length, 100);
                    System.arraycopy(data, 0, mBtRecData, received_content_length,
                            data.length);
                    received_content_length = data.length;
                    int new_lenght = data[2] << 8 | data[3];
                    length_to_receive = new_lenght + 8;
                    length_to_receive -= data.length;
                    if (length_to_receive <= 0) {
                        mRcvDataState = 0;
                        received_content_length = 0;
                        isDataReady = true;
                    } else {
                        mRcvDataState = 1;
                        isDataReady = false;
                    }

                }

                break;
            case 1:

                Log.e(TAG,"----------received_content_length="+received_content_length+"---mBtRecData="+mBtRecData.length+"--length_to_receive="+length_to_receive+"---data.length="+data.length);
                System.arraycopy(data, 0, mBtRecData, received_content_length,
                        data.length);
                received_content_length += data.length;
                length_to_receive -= data.length;
                if (length_to_receive <= 0) {
                    mRcvDataState = 0;
                    isDataReady = true;
                } else {
                    isDataReady = false;
                }



//                if(received_content_length < mBtRecData.length){
//                    System.arraycopy(data, 0, mBtRecData, received_content_length,
//                            data.length);
//                    received_content_length += data.length;
//                    length_to_receive -= data.length;
//                    if (length_to_receive <= 0) {
//                        mRcvDataState = 0;
//                        isDataReady = true;
//                    } else {
//                        isDataReady = false;
//                    }
//                }
                break;
        }
    }


    /**
     * 校验收到数据是否完整 = 外部调用
     *
     * @param mBtRecData
     * @return
     */
    private boolean checkData(byte[] mBtRecData) {
        byte checkNum = mBtRecData[4];
        int start = 13;
        byte crc = 0;
        int length = (mBtRecData[3] & 0xFF) - 5;
        for (int i = start; i < start + length; i++) {
            crc = getCheckNum(mBtRecData[i], crc);
        }
        if (checkNum == crc) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验收到数据是否完整 = 内部调用
     *
     * @param value
     * @param crc
     * @return
     */
    private byte getCheckNum(byte value, byte crc) {
        byte polynomial = (byte) 0x97;
        crc ^= value;

        for (int i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0) {
                crc <<= 1;
                crc ^= polynomial;
            } else {
                crc <<= 1;
            }
        }
        return crc;
    }

    /**
     * 获取卡路里
     *
     * @param height    身高
     * @param weight    体重
     * @param sportStep 步数
     * @return
     */
    public static float getCalory(float height, float weight, int sportStep) {
        float bleCalory = 0;

        bleCalory = (float) (weight * 1.036 * height * 0.41 * sportStep * 0.00001);

        return bleCalory;
    }

    /**
     * 获取距离
     *
     * @param height    身高
     * @param sportStep 步数
     * @return
     */
    public static float getDistance(float height, int sportStep) {
        float bleDistance = 0;
        bleDistance = (float) (height * 41 * sportStep * 0.0001);
        return bleDistance;
    }


}
