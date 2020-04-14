package com.example.bozhilun.android.b30.service;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPSleepData;
import com.example.bozhilun.android.b30.model.CusVPSleepPrecisionData;
import com.example.bozhilun.android.b30.model.CusVPTimeData;
import com.example.bozhilun.android.b31.bpoxy.B31SaveSpo2AsyncTask;
import com.example.bozhilun.android.b31.bpoxy.B31sSaveHrvAsyncTask;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.CommCalUtils;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.LocalizeTool;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.EPwdStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by Administrator on 2018/7/26.
 */

public class ConnBleHelpService {

    private static final String TAG = "ConnBleHelpService";
    private int count;

    //验证密码
    private ConnBleHelpListener connBleHelpListener;

    //设备数据
    private ConnBleMsgDataListener connBleMsgDataListener;



    //睡眠处理map
    private Map<String, CusVPSleepData> sleepMap = new HashMap<>();

    //精准睡眠处理的map
    //private Map<String,CusVPSleepPrecisionData> precisionSleepMap = new HashMap<>();

    private ConcurrentMap<String,CusVPSleepPrecisionData>  precisionSleepMap = new ConcurrentHashMap<>();

    //是否支持精准睡眠
    private boolean isSleepPrecisionData = false;

    //保存spo2的task
    private B31SaveSpo2AsyncTask b31SaveSpo2AsyncTask;
    //保存hrv的task
    private B31sSaveHrvAsyncTask b31sSaveHrvAsyncTask;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);

    //是否支持血氧
    private boolean isSupportSpo2 = false;

    /**
     * 转换工具
     */
    private Gson gson = new Gson();

    private static volatile ConnBleHelpService connBleHelpService;

    private ConnBleHelpService() {

    }

    public static ConnBleHelpService getConnBleHelpService() {
        if (connBleHelpService == null) {
            synchronized (ConnBleHelpService.class) {
                if (connBleHelpService == null) {
                    connBleHelpService = new ConnBleHelpService();
                }
            }
        }
        return connBleHelpService;
    }


    //    private Dialog dialog;
    //发送广播提示输入密码
    private void showLoadingDialog2(final String b30Mac) {
        Intent intent = new Intent();
        intent.setAction("com.example.bozhilun.android.siswatch.CHANGEPASS");
        intent.putExtra("b30ID", b30Mac);
        MyApp.getContext().sendBroadcast(intent);
    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //普通睡眠
            if (sleepMap != null && !sleepMap.isEmpty()) {
                for (Map.Entry<String, CusVPSleepData> mp : sleepMap.entrySet()) {

                    //保存详细数据 ，保存详细数据时日期会往后+ 一天
                    B30HalfHourDB db = new B30HalfHourDB();
                    db.setAddress(MyApp.getInstance().getMacAddress());
                    db.setDate(WatchUtils.obtainAroundDate(mp.getValue().getDate(), false));
                    db.setType(B30HalfHourDao.TYPE_SLEEP);
                    db.setOriginData(gson.toJson(mp.getValue()));
                    db.setUpload(0);
                    B30HalfHourDao.getInstance().saveOriginData(db);

                    //保存汇总数据
                    String bleName = "B31";
                    if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME))
                        bleName = MyCommandManager.DEVICENAME;
                    //保存睡眠数据
                    CusVPSleepData sleepData = mp.getValue();
                    //清醒时长=总的睡眠时长-深睡时长-清醒时长
                    int soberlen = sleepData.getAllSleepTime() - sleepData.getDeepSleepTime() - sleepData.getLowSleepTime();
                    CommDBManager.getCommDBManager().saveCommSleepDbData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()), sleepData.getDate(),
                            sleepData.getDeepSleepTime(), sleepData.getLowSleepTime(), soberlen, sleepData.getAllSleepTime(),
                            sleepData.getSleepDown().getDateAndClockForSleep(), sleepData.getSleepUp().getDateAndClockForSleep(),
                            sleepData.getWakeCount());

                }
            }


            //精准睡眠
            if(precisionSleepMap != null && !precisionSleepMap.isEmpty()){
                Log.e(TAG,"------精准睡眠的map="+precisionSleepMap.size());
               for(Map.Entry<String,CusVPSleepPrecisionData> mmp : precisionSleepMap.entrySet()){
                   //保存详细数据 ，保存详细数据时日期会往后+ 一天
                   Log.e(TAG,"------保存精准睡眠="+mmp.toString()+"--="+mmp.getValue().getSleepLine());
                   B30HalfHourDB db = new B30HalfHourDB();
                   db.setAddress(MyApp.getInstance().getMacAddress());
                   db.setDate(mmp.getKey());
                   db.setType(B30HalfHourDao.TYPE_PRECISION_SLEEP);
                   db.setOriginData(gson.toJson(mmp.getValue()));
                   db.setUpload(0);
                   B30HalfHourDao.getInstance().saveOriginData(db);
               }
            }


        }
    };


    public void doConnOperater(final String bMac) {
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        Log.e(TAG, "---------b20Pwd=" + b30Pwd);
        final boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制

        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                Log.e(TAG, "-----密码=" + i);
            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                Log.e(TAG, "---111--pwdData=" + pwdData.getmStatus() + "=" + pwdData.toString());
                SharedPreferencesUtils.setParam(MyApp.getContext(),Commont.DEVICE_VERSION_CODE_KEY,pwdData.getDeviceNumber()+"-"+pwdData.getDeviceVersion());
                //默认密码不正确，提醒用户输入密码
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    showLoadingDialog2(bMac);
                }

                //密码验证成功
                if (pwdData.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS) {
                    syncCommSettingData();
                }

                Log.e(TAG, "-------111checkPwd=" + (pwdData.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS));

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
                Log.e(TAG, "--111---functionDeviceSupportData--=" + functionDeviceSupportData.toString());

                Context context = MyApp.getContext();

                //版本协议
                int originProtcolVersion = functionDeviceSupportData.getOriginProtcolVersion();
                SharedPreferencesUtils.setParam(context,Commont.VP_DEVICE_VERSION,originProtcolVersion);

                isSupportSpo2 = functionDeviceSupportData.getSpo2H() == EFunctionStatus.SUPPORT;


                //B31带血压功能的标识
                SharedPreferencesUtils.setParam(context,Commont.IS_B31_HAS_BP_KEY,functionDeviceSupportData.getBp() == EFunctionStatus.SUPPORT);

                isSleepPrecisionData = functionDeviceSupportData.getPrecisionSleep() == EFunctionStatus.SUPPORT;
                //B31S支持亮度调节功能
                SharedPreferencesUtils.setParam(context,Commont.IS_B31S_LIGHT_KEY,functionDeviceSupportData.getScreenLight() == EFunctionStatus.SUPPORT);

                //B31S带精准睡眠功能的设备不支持疲劳度检测功能
                SharedPreferencesUtils.setParam(context,Commont.IS_B31S_FATIGUE_KEY,functionDeviceSupportData.getFatigue() == EFunctionStatus.SUPPORT);

                //设置支持的主题风格
                int deviceStyleCoount = functionDeviceSupportData.getScreenstyle();
                SharedPreferencesUtils.setParam(context,Commont.SP_DEVICE_STYLE_COUNT,deviceStyleCoount);

                //B31是否支持呼吸率
                SharedPreferencesUtils.setParam(context,Commont.IS_B31_HEART,functionDeviceSupportData.getBeathFunction() == EFunctionStatus.SUPPORT);


            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

            }
        }, WatchUtils.isEmpty(b30Pwd) ? "0000" : b30Pwd.trim(), is24Hour);


    }

    //验证设备密码
    public void doConnOperater(final String blePwd, final VerB30PwdListener verB30PwdListener) {
        // String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        final boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                //此方法调用 ，密码不正确
                if (pwdData.getmStatus() == EPwdStatus.CHECK_FAIL) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(),Commont.DEVICE_VERSION_CODE_KEY,pwdData.getDeviceNumber()+"-"+pwdData.getDeviceVersion());
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdFailed();
                }


                //验证密码成功
                if (pwdData.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS) {
                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, blePwd);
                    syncCommSettingData();
                    if (verB30PwdListener != null)
                        verB30PwdListener.verPwdSucc();
                }

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
                Log.e(TAG,"----------functionDeviceSupportData="+functionDeviceSupportData.toString());

                Context context = MyApp.getContext();

                //B31带血压功能的标识
                SharedPreferencesUtils.setParam(context,Commont.IS_B31_HAS_BP_KEY,functionDeviceSupportData.getBp() == EFunctionStatus.SUPPORT);
                //版本协议
                int originProtcolVersion = functionDeviceSupportData.getOriginProtcolVersion();
                SharedPreferencesUtils.setParam(context,Commont.VP_DEVICE_VERSION,originProtcolVersion);

                isSupportSpo2 = functionDeviceSupportData.getSpo2H() == EFunctionStatus.SUPPORT;

                isSleepPrecisionData = functionDeviceSupportData.getPrecisionSleep() == EFunctionStatus.SUPPORT;
                //B31S亮度调节功能
                SharedPreferencesUtils.setParam(context,Commont.IS_B31S_LIGHT_KEY,functionDeviceSupportData.getScreenLight() == EFunctionStatus.SUPPORT);

                //B31S带精准睡眠功能的设备不支持疲劳度检测功能
                SharedPreferencesUtils.setParam(context,Commont.IS_B31S_FATIGUE_KEY,functionDeviceSupportData.getFatigue() == EFunctionStatus.SUPPORT);
                //设置支持的主题风格
                int deviceStyleCoount = functionDeviceSupportData.getScreenstyle();
                SharedPreferencesUtils.setParam(context,Commont.SP_DEVICE_STYLE_COUNT,deviceStyleCoount);

                //B31是否支持呼吸率
                SharedPreferencesUtils.setParam(context,Commont.IS_B31_HEART,functionDeviceSupportData.getBeathFunction() == EFunctionStatus.SUPPORT);


            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                //Log.e(TAG, "-----functionSocailMsgData-=" + functionSocailMsgData);
            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                Log.e(TAG, "---2222--OnSettingDataChange-=" + customSettingData.toString());


            }
        }, blePwd, is24Hour);


    }

    //连接成功后设置数据
    private void syncCommSettingData() {
        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.BATTERNUMBER, 0);//每次连接清空电量
        //同步用户信息，设置目标步数
        setDeviceUserData();
        //设置语言，根据系统的语言设置
        ELanguage languageData ;
        String localelLanguage = Locale.getDefault().getLanguage();
        Log.e(TAG,"----------localelLanguage="+localelLanguage);
        if(!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")){    //中文
            Locale locales = MyApp.getInstance().getApplicationContext().getResources().getConfiguration().locale;
            String localCountry = locales.getCountry();
            if(localCountry.equals("TW")){  //繁体
                languageData = ELanguage.CHINA_TRADITIONAL;
            }else{
                languageData = ELanguage.CHINA;
            }

        }else{
            languageData = ELanguage.ENGLISH;
        }
        MyApp.getInstance().getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
            @Override
            public void onLanguageDataChange(LanguageData languageData) {
                if (connBleHelpListener != null) {
                    connBleHelpListener.connSuccState();
                }
            }
        }, languageData);



    }


    /**
     * 同步用户信息设置设备的目标步数，
     */
    public void setDeviceUserData() {
        //目标步数
        int sportGoal = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 0);
        PersonInfoData personInfoData = WatchUtils.getUserPerson(sportGoal);
        if (personInfoData == null)
            return;
        MyApp.getInstance().getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
            }
        }, new IPersonInfoDataListener() {
            @Override
            public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                //同步用户信息成功
                if (eOprateStauts == EOprateStauts.OPRATE_SUCCESS) {

                }
            }
        }, personInfoData);
    }


    /**
     * 获取手环基本数据
     */
    public void getDeviceMsgData() {
        // 获取步数,当天的步数
        MyApp.getInstance().getVpOperateManager().readSportStep(bleWriteResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                //保存当天总步数
                B30HalfHourDB db = new B30HalfHourDB();
                db.setAddress(MyApp.getInstance().getMacAddress());
                db.setDate(WatchUtils.getCurrentDate());
                db.setType(B30HalfHourDao.TYPE_STEP);
                db.setOriginData("" + sportData.getStep());
                B30HalfHourDao.getInstance().saveOriginData(db);

                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleSportData(sportData.getStep());
                }

                String bleName = "B31";
                if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
                //保存总步数
                CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, MyApp.getInstance().getMacAddress(), WatchUtils.getCurrentDate(), sportData.getStep());
            }
        });
        //电量
        MyApp.getInstance().getVpOperateManager().readBattery(bleWriteResponse, new IBatteryDataListener() {
            @Override
            public void onDataChange(BatteryData batteryData) {
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.getBleBatteryData(batteryData.getBatteryLevel());
                }
            }
        });

        //读取一下闹钟
        MyApp.getInstance().getVpOperateManager().readAlarm2(bleWriteResponse,   new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                //Log.e(TAG,"--------连接成功读取闹钟="+alarmData2.toString());
            }
        });
    }


    /**
     * 读取所有的原始数据(包括睡眠数据)
     *
     * @param today true_只加载今天数据 false_加载三天
     */
    public void readAllHealthData(final boolean today) {
        if (sleepMap != null)
            sleepMap.clear();
        if(precisionSleepMap != null)
            precisionSleepMap.clear();
        //单单读取睡眠的
        MyApp.getInstance().getVpOperateManager().readSleepData(bleWriteResponse, new ISleepDataListener() {
            @Override
            public void onSleepDataChange(SleepData sleepData) {
                if (sleepData == null)
                    return;
                //Log.e(TAG, "-----22----睡眠原始返回数据=" + sleepData.toString());
                //saveSleepData(sleepData);
                if(sleepData instanceof SleepPrecisionData && isSleepPrecisionData){
                    SleepPrecisionData sleepPrecisionData = (SleepPrecisionData) sleepData;
                    //Log.e(TAG,"----22--精准睡眠="+sleepPrecisionData.toString()+"--表示形式="+sleepPrecisionData.getSleepLine());
                    savePrecisionData(sleepPrecisionData);
                }else{
                    // 睡眠数据返回,会有多条数据
                    saveSleepData(sleepData);
                }

            }

            @Override
            public void onSleepProgress(float v) {
                //Log.e(TAG,"---------onSleepProgress="+v);
            }

            @Override
            public void onSleepProgressDetail(String s, int i) {
                //Log.e(TAG,"-------onSleepProgressDetail="+s+"--i="+i);
            }

            @Override
            public void onReadSleepComplete() {

                Log.e(TAG, "----------睡眠数据读取结束------=" + precisionSleepMap.size());
                 handler.sendEmptyMessageDelayed(1001,3 * 1000);

                 readAllDeviceData(today);
            }
        }, today ? 2 : 3);

        //读取健康数据，步数详情、心率、血压等

    }

    /**
     * //睡眠效率得分，起夜到深睡的效率得分
     *     private int sleepEfficiencyScore;
     *     //入睡效率得分，从开始睡眠到第一次进入深睡的效率
     *     private int fallAsleepScore;
     *     //睡眠时长得分
     *     private int sleepTimeScore;
     *     //退出睡眠的模式
     *     private int exitSleepMode;
     *     //深浅睡眠模式
     *     private int deepAndLightMode;
     *     //其他睡眠时间长，单位分钟
     *     private int otherDuration;
     *     //第一次进入深睡时间
     *     private int firstDeepDuration;
     *     //起夜总时长，单位分钟
     *     private int getUpDuration;
     *     //起夜到深睡时间的平均值
     *     private int getUpToDeepAve;
     *     //曲线上一个点代表的时间，单位秒，现在是60s
     *     private int onePointDuration;
     *     //睡眠类型
     *     private int accurateType;
     *     //失眠标志
     *     private int insomniaTag;
     *     //失眠得分
     *     private int insomniaScore;
     *     //失眠次数
     *     private int insomniaTimes;
     *     //失眠长度
     *     private int insomniaLength;
     *     //失眠列表
     *     List<InsomniaTimeData> insomniaBeanList;
     *     //失眠开始时间
     *     private String startInsomniaTime;
     *     //失眠结束时间
     *     private String stopInsomniaTime;
     *     //通过睡眠曲线3的个数来计算,单位是分钟
     *     private int insomniaDuration;
     *     //睡眠原始字符串
     *     private String sleepSourceStr;
     *     // 上一段标志位，默认为0(表示肯定没有上一段)，如果为1，则表示有上一段睡眠,否则表示没有上一段睡眠
     *     private int laster;
     *     // 下一段睡眠标志位，默认为255(表示不确定是否有下一段)，如果为1，则表示有下一段睡眠,否则表示没有下一段睡眠
     *     private int next;
     *
     *     //睡眠表现形式
     *     private String sleepLine;
     *
     */

    //保存精准睡眠
    private void savePrecisionData(SleepPrecisionData sleepPrecisionData) {
        try {
            if(sleepPrecisionData == null)
                return;
            Log.e(TAG,"--------精准睡眠原始数据="+sleepPrecisionData.toString()+"="+sleepPrecisionData.getSleepLine());
            TimeData downTimeData = sleepPrecisionData.getSleepDown();
            CusVPTimeData donwTime = new CusVPTimeData(downTimeData.getYear(),downTimeData.getMonth(),
                    downTimeData.getDay(),downTimeData.getHour(),downTimeData.getMinute(),
                    downTimeData.getSecond(),downTimeData.getWeekDay());

            TimeData upTimeData = sleepPrecisionData.getSleepUp();
            CusVPTimeData upTime = new CusVPTimeData(upTimeData.getYear(),upTimeData.getMonth(),
                    upTimeData.getDay(),upTimeData.getHour(),upTimeData.getMinute(),
                    upTimeData.getSecond(),upTimeData.getWeekDay());


            CusVPSleepPrecisionData cSleep = new CusVPSleepPrecisionData();

            cSleep.setDate(sleepPrecisionData.getDate());
            cSleep.setSleepDown(donwTime);
            cSleep.setSleepUp(upTime);
            cSleep.setAllSleepTime(sleepPrecisionData.getAllSleepTime());
            cSleep.setLowSleepTime(sleepPrecisionData.getLowSleepTime());
            cSleep.setDeepSleepTime(sleepPrecisionData.getDeepSleepTime());
            cSleep.setWakeCount(sleepPrecisionData.getWakeCount());
            cSleep.setSleepQulity(sleepPrecisionData.getSleepQulity());


            cSleep.setSleepTag(sleepPrecisionData.getSleepTag());
            cSleep.setGetUpScore(sleepPrecisionData.getGetUpScore());
            cSleep.setDeepScore(sleepPrecisionData.getDeepScore());
            cSleep.setSleepEfficiencyScore(sleepPrecisionData.getSleepEfficiencyScore());
            cSleep.setFallAsleepScore(sleepPrecisionData.getFallAsleepScore());
            cSleep.setSleepTimeScore(sleepPrecisionData.getSleepTimeScore());
            cSleep.setExitSleepMode(sleepPrecisionData.getExitSleepMode());
            cSleep.setDeepAndLightMode(sleepPrecisionData.getDeepAndLightMode());
            cSleep.setOtherDuration(sleepPrecisionData.getOtherDuration());
            cSleep.setFirstDeepDuration(sleepPrecisionData.getFirstDeepDuration());
            cSleep.setGetUpDuration(sleepPrecisionData.getGetUpDuration());
            cSleep.setGetUpToDeepAve(sleepPrecisionData.getGetUpToDeepAve());
            cSleep.setOnePointDuration(sleepPrecisionData.getOnePointDuration());
            cSleep.setAccurateType(sleepPrecisionData.getAccurateType());
            cSleep.setInsomniaTag(sleepPrecisionData.getInsomniaTag());
            cSleep.setInsomniaScore(sleepPrecisionData.getInsomniaScore());
            cSleep.setInsomniaTimes(sleepPrecisionData.getInsomniaTimes());
            cSleep.setInsomniaLength(sleepPrecisionData.getInsomniaLength());
            cSleep.setInsomniaBeanList(sleepPrecisionData.getInsomniaBeanList());
            cSleep.setStartInsomniaTime(sleepPrecisionData.getStartInsomniaTime());
            cSleep.setStopInsomniaTime(sleepPrecisionData.getStopInsomniaTime());
            cSleep.setInsomniaDuration(sleepPrecisionData.getInsomniaDuration());
            cSleep.setSleepSourceStr(sleepPrecisionData.getSleepSourceStr());
            cSleep.setLaster(sleepPrecisionData.getLaster());
            cSleep.setNext(sleepPrecisionData.getNext());
            cSleep.setSleepLine(sleepPrecisionData.getSleepLine());



            String dateStr = cSleep.getDate();
            Log.e(TAG,"---------dateStr="+dateStr);
            if(precisionSleepMap.get(dateStr) == null){
                precisionSleepMap.put(dateStr,cSleep);
            }else{
                //同一天的
                CusVPSleepPrecisionData tmpCusVpSleep = precisionSleepMap.get(dateStr);
                if(tmpCusVpSleep == null)
                    return;

                //入睡时间的分钟
                long tmpSleepDownTime = sdf.parse(tmpCusVpSleep.getSleepDown().getDateAndClockForSleepSecond()).getTime()/1000;
                long cSleepDownTime = sdf.parse(cSleep.getSleepDown().getDateAndClockForSleepSecond()).getTime()/1000;

//                int tmpSleepDownTime = tmpCusVpSleep.getSleepDown().getHMValue();
//                //同天分段的两段睡眠两个入睡时间是不相同的
//                int cSleepDownTime = cSleep.getSleepDown().getHMValue();

                //Log.e(TAG,"---------组合="+tmpSleepDownTime+"\n"+cSleepDownTime+"\n"+tmpCusVpSleep.toString());


                if(tmpSleepDownTime != cSleepDownTime){ //组合分段的睡眠数据
                    CusVPSleepPrecisionData resultSleepData = new CusVPSleepPrecisionData();
                    resultSleepData.setDate(dateStr);
                    //判断哪一个是最后的时间
                    //入睡时间
                    resultSleepData.setSleepDown(tmpSleepDownTime>cSleepDownTime?cSleep.getSleepDown():tmpCusVpSleep.getSleepDown());
                    //起床时间
                    resultSleepData.setSleepUp(tmpSleepDownTime>cSleepDownTime?tmpCusVpSleep.getSleepUp():cSleep.getSleepUp());
                    //所有睡眠时间
                    resultSleepData.setAllSleepTime(tmpSleepDownTime>cSleepDownTime?tmpCusVpSleep.getAllSleepTime():cSleep.getAllSleepTime());
                    //浅睡时间
                    resultSleepData.setLowSleepTime(tmpSleepDownTime>cSleepDownTime?tmpCusVpSleep.getLowSleepTime():cSleep.getLowSleepTime());
                    //深睡
                    resultSleepData.setDeepSleepTime(tmpSleepDownTime>cSleepDownTime?tmpCusVpSleep.getDeepSleepTime():cSleep.getDeepSleepTime());
                    //苏醒次数
                    resultSleepData.setWakeCount(tmpSleepDownTime>cSleepDownTime?tmpCusVpSleep.getWakeCount():cSleep.getWakeCount());
                    //睡眠质量
                    resultSleepData.setSleepQulity(tmpCusVpSleep.getSleepQulity()>=cSleep.getSleepQulity()?tmpCusVpSleep.getSleepQulity():cSleep.getSleepQulity());


                    resultSleepData.setSleepTag(tmpCusVpSleep.getSleepTag());
                    //起夜得分
                    resultSleepData.setGetUpScore(tmpCusVpSleep.getGetUpScore()>=cSleep.getGetUpScore()?tmpCusVpSleep.getGetUpScore():cSleep.getGetUpScore());
                    //深睡夜得分
                    resultSleepData.setDeepScore(tmpCusVpSleep.getDeepScore()>=cSleep.getDeepScore()?tmpCusVpSleep.getDeepScore():cSleep.getDeepScore());
                    //睡眠效率得分
                    resultSleepData.setSleepEfficiencyScore(tmpCusVpSleep.getSleepEfficiencyScore()>=cSleep.getSleepEfficiencyScore()?tmpCusVpSleep.getSleepEfficiencyScore():cSleep.getSleepEfficiencyScore());
                    //睡眠时长得分
                    resultSleepData.setSleepTimeScore(tmpCusVpSleep.getSleepTimeScore()>=cSleep.getSleepTimeScore()?tmpCusVpSleep.getSleepTimeScore():cSleep.getSleepTimeScore());
                    //设置退出睡眠模式
                    resultSleepData.setExitSleepMode(tmpCusVpSleep.getExitSleepMode()>=cSleep.getExitSleepMode()?tmpCusVpSleep.getExitSleepMode():cSleep.getExitSleepMode());
                    //入睡效率得分
                    resultSleepData.setFallAsleepScore(tmpCusVpSleep.getFallAsleepScore()>=cSleep.getFallAsleepScore()?tmpCusVpSleep.getFallAsleepScore():cSleep.getFallAsleepScore());
                    //获得深浅睡模式
                    resultSleepData.setDeepAndLightMode(tmpCusVpSleep.getDeepAndLightMode());
                    //其它睡眠时长
                    resultSleepData.setOtherDuration(tmpCusVpSleep.getOtherDuration()>=cSleep.getOtherDuration()?tmpCusVpSleep.getOtherDuration():cSleep.getOtherDuration());
                    resultSleepData.setGetUpToDeepAve(tmpCusVpSleep.getGetUpToDeepAve());
                    resultSleepData.setOnePointDuration(tmpCusVpSleep.getOnePointDuration());


                    resultSleepData.setAccurateType(tmpCusVpSleep.getAccurateType());
                    resultSleepData.setInsomniaTag(tmpCusVpSleep.getInsomniaTag());
                    resultSleepData.setInsomniaScore(tmpCusVpSleep.getInsomniaScore());
                    resultSleepData.setInsomniaTimes(tmpCusVpSleep.getInsomniaTimes());
                    resultSleepData.setInsomniaLength(tmpCusVpSleep.getInsomniaLength());
                    resultSleepData.setInsomniaBeanList(tmpCusVpSleep.getInsomniaBeanList());
                    resultSleepData.setStartInsomniaTime(tmpCusVpSleep.getStartInsomniaTime());
                    resultSleepData.setStopInsomniaTime(tmpCusVpSleep.getStopInsomniaTime());
                    resultSleepData.setInsomniaDuration(tmpCusVpSleep.getInsomniaDuration());
                    resultSleepData.setSleepSourceStr(tmpCusVpSleep.getSleepSourceStr());
                    resultSleepData.setLaster(tmpCusVpSleep.getLaster());
                    resultSleepData.setNext(tmpCusVpSleep.getNext());

                    String sleepLinStr1 = tmpCusVpSleep.getSleepLine();
                    String sleepLinStr2 = cSleep.getSleepLine();
                    resultSleepData.setSleepLine(tmpSleepDownTime>cSleepDownTime?(sleepLinStr2+sleepLinStr1):(sleepLinStr1+sleepLinStr2));
                    precisionSleepMap.put(dateStr,resultSleepData);

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //读取健康数据，运动，心率，血压
    private void readAllDeviceData(boolean today) {

        int vpOrignVersion = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.VP_DEVICE_VERSION,0);

        if(vpOrignVersion == 0){    //旧版
            MyApp.getInstance().getVpOperateManager().readAllHealthData(iAllHealthDataListener,today ? 2 : 3);
        }else{  //新版 B31s带精准睡眠功能的
            //MyApp.getInstance().getVpOperateManager().readOriginData(bleWriteResponse,iOriginProgressListener,1);
            new ReadAllDataAsync().execute();
        }
    }




    class ReadAllDataAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            if(!isSupportSpo2){
                new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                        .obtainFormatDate(0));// 更新最后更新数据的时间
                if (connBleMsgDataListener != null) {
                    connBleMsgDataListener.onOriginData();
                }

                Intent intent = new Intent();
                intent.setAction(WatchUtils.B31_SPO2_COMPLETE);
                MyApp.getContext().sendBroadcast(intent);

                return null;
            }


            MyApp.getInstance().getVpOperateManager().readOriginData(bleWriteResponse, new IOriginData3Listener() {
                @Override
                public void onOriginFiveMinuteListDataChange(List<OriginData3> list) {

                }

                @Override
                public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    saveHalfHourData(originHalfHourData);
                }

                @Override
                public void onOriginHRVOriginListDataChange(List<HRVOriginData> list) {
                    Log.e(TAG,"-------HRVOriginData--="+list.size()+"---1="+list.get(0).toString());
                    if(b31sSaveHrvAsyncTask != null && b31sSaveHrvAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
                        b31sSaveHrvAsyncTask.cancel(true);
                        b31sSaveHrvAsyncTask = null;
                        b31sSaveHrvAsyncTask = new B31sSaveHrvAsyncTask();
                    }else{
                        b31sSaveHrvAsyncTask = new B31sSaveHrvAsyncTask();
                    }
                    b31sSaveHrvAsyncTask.execute(list);
                }

                @Override
                public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> list) {
                    Log.e(TAG,"-------Spo2hOriginData--="+list.size()+"---1="+list.get(0).toString());
                    if(b31SaveSpo2AsyncTask != null && b31SaveSpo2AsyncTask.getStatus() == AsyncTask.Status.RUNNING){
                        b31SaveSpo2AsyncTask.cancel(true);
                        b31SaveSpo2AsyncTask = null;
                        b31SaveSpo2AsyncTask = new B31SaveSpo2AsyncTask();
                    }else{
                        b31SaveSpo2AsyncTask = new B31SaveSpo2AsyncTask();
                    }
                    b31SaveSpo2AsyncTask.execute(list);
                }

                @Override
                public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

                }

                @Override
                public void onReadOriginProgress(float v) {

                }

                @Override
                public void onReadOriginComplete() {
                    new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                            .obtainFormatDate(0));// 更新最后更新数据的时间
                    if (connBleMsgDataListener != null) {
                        connBleMsgDataListener.onOriginData();
                    }
                }
            }, 1);
            return null;
        }
    }

    /*
    private IOriginProgressListener iOriginProgressListener = new IOriginData3Listener() {
        @Override
        public void onOriginFiveMinuteListDataChange(List<OriginData3> list) {

        }

        @Override
        public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {
            //Log.e(TAG,"----originHalfHourData--="+originHalfHourData.toString());
            saveHalfHourData(originHalfHourData);
        }

        @Override
        public void onOriginHRVOriginListDataChange(List<HRVOriginData> list) {
            Log.e(TAG,"-------HRVOriginData--="+list.size()+"---1="+list.get(0).toString());
            if(b31sSaveHrvAsyncTask != null && b31sSaveHrvAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
                b31sSaveHrvAsyncTask.cancel(true);
                b31sSaveHrvAsyncTask = null;
                b31sSaveHrvAsyncTask = new B31sSaveHrvAsyncTask();
            }else{
                b31sSaveHrvAsyncTask = new B31sSaveHrvAsyncTask();
            }
            b31sSaveHrvAsyncTask.execute(list);
        }

        @Override
        public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> list) {
            Log.e(TAG,"-------Spo2hOriginData--="+list.size()+"---1="+list.get(0).toString());
            if(b31SaveSpo2AsyncTask != null && b31SaveSpo2AsyncTask.getStatus() == AsyncTask.Status.RUNNING){
                b31SaveSpo2AsyncTask.cancel(true);
                b31SaveSpo2AsyncTask = null;
                b31SaveSpo2AsyncTask = new B31SaveSpo2AsyncTask();
            }else{
                b31SaveSpo2AsyncTask = new B31SaveSpo2AsyncTask();
            }
            b31SaveSpo2AsyncTask.execute(list);

        }

        @Override
        public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

        }

        @Override
        public void onReadOriginProgress(float v) {

        }

        @Override
        public void onReadOriginComplete() {
            new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                    .obtainFormatDate(0));// 更新最后更新数据的时间
            if (connBleMsgDataListener != null) {
                connBleMsgDataListener.onOriginData();
            }
        }
    };*/


    /**
     * 协议版本为0的回调
     */
    private IAllHealthDataListener iAllHealthDataListener = new IAllHealthDataListener() {
        @Override
        public void onProgress(float v) {

        }

        @Override
        public void onSleepDataChange(SleepData sleepData) {

        }

        @Override
        public void onReadSleepComplete() {

        }

        @Override
        public void onOringinFiveMinuteDataChange(OriginData originData) {

        }

        @Override
        public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
            // 多少天就有多少条数据
            // 30分钟原始数据的回调，来自5分钟的原始数据，只是在内部进行了数据处理
            saveHalfHourData(originHalfHourData);
        }

        @Override
        public void onReadOriginComplete() {
            // 读取取运动,心率,血压数据结束
            new LocalizeTool(MyApp.getContext()).putUpdateDate(WatchUtils
                    .obtainFormatDate(0));// 更新最后更新数据的时间
            if (connBleMsgDataListener != null) {
                connBleMsgDataListener.onOriginData();
            }
        }
    };




    /**
     * 保存手环返回的睡眠数据到本地数据库
     *
     * @param sleepData 睡眠数据
     */
    private void saveSleepData(SleepData sleepData) {
        if (sleepData == null) return;

        /***
         * 睡眠数据
         *
         * @param cali_flag     睡眠定标值，目前这个值没有什么用
         * @param sleepQulity   睡眠质量
         * @param wakeCount     睡眠中起床的次数
         * @param deepSleepTime 深睡时长
         * @param lowSleepTime  浅睡时长
         * @param allSleepTime  睡眠总时长
         * @param sleepLine      获取睡眠曲线，主要用于更具象化的UI来显示睡眠状态（可参考我司APP,360应用市场搜索Hband），如果您睡眠界面对UI没有特殊要求，可不理会,睡眠曲线分为普通睡眠和精准睡眠，普通睡眠是一组由0,1,2组成的字符串，每一个字符代表时长为5分钟，其中0表示浅睡，1表示深睡，2表示苏醒,比如“201112”，长度为6，表示睡眠阶段共30分钟，头尾各苏醒5分钟，中间浅睡5分钟，深睡15分钟;若是精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒。
         * @param sleepDown     入睡时间
         * @param sleepUp       起床时间
         */
        TimeData downTimeData = sleepData.getSleepDown();
        CusVPTimeData donwTime = new CusVPTimeData(downTimeData.getYear(),downTimeData.getMonth(),
                downTimeData.getDay(),downTimeData.getHour(),downTimeData.getMinute(),
                downTimeData.getSecond(),downTimeData.getWeekDay());

        TimeData upTimeData = sleepData.getSleepUp();
        CusVPTimeData upTime = new CusVPTimeData(upTimeData.getYear(),upTimeData.getMonth(),
                upTimeData.getDay(),upTimeData.getHour(),upTimeData.getMinute(),
                upTimeData.getSecond(),upTimeData.getWeekDay());

        CusVPSleepData deviceSstr = new CusVPSleepData(sleepData.getDate(),sleepData.getCali_flag(),sleepData.getSleepQulity(),
                sleepData.getWakeCount(),sleepData.getDeepSleepTime(),sleepData.getLowSleepTime(),sleepData.getAllSleepTime(),
                sleepData.getSleepLine(),donwTime,upTime);




        //Log.e("-------设备睡眠数据---", gson.toJson(deviceSstr) + "");
        if (sleepMap.get(deviceSstr.getDate()) == null) {
            sleepMap.put(deviceSstr.getDate(), deviceSstr);
        } else {
            CusVPSleepData tempSleepData = sleepMap.get(deviceSstr.getDate());    //已经存在的数据
            Log.e(TAG, "------tempSleepData=" + tempSleepData.toString());
            if (tempSleepData.getDate().equals(deviceSstr.getDate())) {  //同一天的
                if (!tempSleepData.getSleepLine().equals(deviceSstr.getSleepLine())) {
                    //map 中已经保存的
                    //Log.e(TAG,"--------tempSleepData="+tempSleepData.toString());
                   // SleepData resultSlee = new SleepData();
                    CusVPSleepData resultSlee = new CusVPSleepData();


                    resultSlee.setDate(deviceSstr.getDate());    //日期
                    resultSlee.setCali_flag(0);
                    //睡眠质量，取最大值
                    resultSlee.setSleepQulity(tempSleepData.getSleepQulity() >= deviceSstr.getSleepQulity() ? tempSleepData.getSleepQulity() : deviceSstr.getSleepQulity());
                    //睡醒次数
                    resultSlee.setWakeCount(tempSleepData.getWakeCount() + deviceSstr.getWakeCount() + 1);
                    //深睡时间
                    resultSlee.setDeepSleepTime(tempSleepData.getDeepSleepTime() + deviceSstr.getDeepSleepTime());
                    //浅睡时间
                    resultSlee.setLowSleepTime(tempSleepData.getLowSleepTime() + deviceSstr.getLowSleepTime());
                    //入睡时间 比较时间大小
                    String time1 = tempSleepData.getSleepDown().getDateAndClockForSleepSecond();
                    String time2 = sleepData.getSleepDown().getDateAndClockForSleepSecond();
                    CusVPTimeData sleepDownTime = WatchUtils.comPariDateDetail(time2, time1) ? deviceSstr.getSleepDown() : tempSleepData.getSleepDown();
                    resultSlee.setSleepDown(sleepDownTime);
                    //清醒时间
                    String sleepUpStr1 = tempSleepData.getSleepUp().getDateAndClockForSleepSecond();
                    String sleepUpStr2 = sleepData.getSleepUp().getDateAndClockForSleepSecond();

                    CusVPTimeData sleepUpTime = WatchUtils.comPariDateDetail(sleepUpStr2, sleepUpStr1) ? tempSleepData.getSleepUp() : deviceSstr.getSleepUp();

                    resultSlee.setSleepUp(sleepUpTime);
                    //计算两段时间间隔，第二段的入睡时间-第一段的清醒时间
                    int sleepLen = WatchUtils.intervalTimeStr(sleepUpStr1, time2);
                    int sleepStatus = sleepLen / 5;
                    StringBuilder stringBuffer = new StringBuilder();
                    for (int i = 1; i <= sleepStatus; i++) {
                        stringBuffer.append("2");
                    }
                    //所有睡眠时间 = 几段的总的睡眠时间+清醒的时间
                    resultSlee.setAllSleepTime(Integer.valueOf(tempSleepData.getAllSleepTime()) + Integer.valueOf(deviceSstr.getAllSleepTime()) + (sleepStatus * 5));
                    resultSlee.setSleepLine(WatchUtils.comPariDateDetail(time1, time2) ?
                            (tempSleepData.getSleepLine() + stringBuffer + "" + deviceSstr.getSleepLine()) :
                            (deviceSstr.getSleepLine() + stringBuffer + "" + tempSleepData.getSleepLine()));
                    Log.e(TAG, "----------结果睡眠---=" + resultSlee.toString());
                    sleepMap.put(deviceSstr.getDate(), resultSlee);
                }

            }


        }


    }

    /**
     * 保存手环返回的健康数据(步数,运动,心率,血压)到本地数据库
     *
     * @param data 30分钟的数据源
     */
    private void saveHalfHourData(OriginHalfHourData data) {
        if (data == null) return;
        String mac = MyApp.getInstance().getMacAddress();
       // Log.e(TAG,"------sport------" + data.getHalfHourSportDatas());
        String dateSport = saveSportData(mac, data.getHalfHourSportDatas());
        saveStepData(mac, dateSport, data.getAllStep());
        saveRateData(mac, data.getHalfHourRateDatas());
        saveBpData(mac, data.getHalfHourBps());
    }

    /**
     * 保存手环返回的运动数据到本地数据库
     *
     * @param mac       手环MAC地址
     * @param sportData 当天所有30分钟运动数据
     * @return 日期(保存步数时用, 有运动数据才会有步数)
     */
    private String saveSportData(String mac, List<HalfHourSportData> sportData) {
        if (sportData == null || sportData.isEmpty()) return null;
        Log.e(TAG,"-------运动数据="+sportData.size());

        List<CusVPHalfSportData> cusVPHalfSportDataList = new ArrayList<>();
        for(HalfHourSportData halfSportData : sportData){
            CusVPTimeData cusVPTimeData = new CusVPTimeData(halfSportData.getTime().getYear(),
                    halfSportData.getTime().getMonth(),halfSportData.getTime().getDay(),
                    halfSportData.getTime().getHour(),halfSportData.getTime().getMinute(),
                    halfSportData.getTime().getSecond(),halfSportData.getTime().getWeekDay());
            CusVPHalfSportData cusVPHalfSportData = new CusVPHalfSportData(cusVPTimeData,halfSportData.getStepValue(),
                    halfSportData.getSportValue(),halfSportData.getDisValue(),halfSportData.getCalValue());
            cusVPHalfSportDataList.add(cusVPHalfSportData);
        }


        String date = sportData.get(0).getDate();
        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(mac);
        db.setDate(date);
        db.setType(B30HalfHourDao.TYPE_SPORT);
        db.setOriginData(gson.toJson(cusVPHalfSportDataList));
        db.setUpload(0);
        B30HalfHourDao.getInstance().saveOriginData(db);
        return date;
    }

    /**
     * 保存手环返回的心率数据到本地数据库
     *
     * @param mac      手环MAC地址
     * @param rateData 当天所有30分钟心率数据
     */
    private void saveRateData(String mac, List<HalfHourRateData> rateData) {
        if (rateData == null || rateData.isEmpty()) return;


        List<CusVPHalfRateData > cusVPHalfRateDataList = new ArrayList<>();
        for(HalfHourRateData lfRate : rateData){
            CusVPTimeData cusVPTimeData = new CusVPTimeData(lfRate.getTime().getYear(),lfRate.getTime().getMonth(),lfRate.getTime().getDay(),
                    lfRate.getTime().getHour(),lfRate.getTime().getMinute(),lfRate.getTime().getSecond(),lfRate.getTime().getWeekDay());
            CusVPHalfRateData cusVPHalfRateData1 = new CusVPHalfRateData(lfRate.getDate(),cusVPTimeData,lfRate.getRateValue(),lfRate.getEcgCount(),lfRate.getPpgCount());
            cusVPHalfRateDataList.add(cusVPHalfRateData1);
        }


        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(mac);
        db.setDate(cusVPHalfRateDataList.get(0).getDate());
        db.setType(B30HalfHourDao.TYPE_RATE);
        db.setOriginData(gson.toJson(rateData));
        db.setUpload(0);
        B30HalfHourDao.getInstance().saveOriginData(db);


        String bleName = "B31";
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
        //保存心率数据
        Integer[] heartStr = CommCalUtils.calHeartData(rateData);
        CommDBManager.getCommDBManager().saveCommHeartData(bleName, WatchUtils.getSherpBleMac(MyApp.getContext()),
                rateData.get(0).getDate(), heartStr[0], heartStr[1], heartStr[2]);


    }

    /**
     * 保存手环返回的血压数据到本地数据库
     *
     * @param mac    手环MAC地址
     * @param bpData 当天所有30分钟血压数据
     */
    private void saveBpData(String mac, List<HalfHourBpData> bpData) {
        if (bpData == null || bpData.isEmpty()) return;

        List<CusVPHalfHourBpData> cusBpList = new ArrayList<>();
        for(HalfHourBpData hb : bpData){
            TimeData timeData = hb.getTime();
            CusVPTimeData cusVPTimeData = new CusVPTimeData(timeData.getYear(),timeData.getMonth(),timeData.getDay(),timeData
            .getHour(),timeData.getMinute(),timeData.getSecond(),timeData.getWeekDay());

            CusVPHalfHourBpData cbBp = new CusVPHalfHourBpData(hb.getDate(),cusVPTimeData,hb.getHighValue(),hb.getLowValue());
            cusBpList.add(cbBp);

        }

        B30HalfHourDB db = new B30HalfHourDB();
        db.setAddress(mac);
        db.setDate(bpData.get(0).getDate());
        db.setType(B30HalfHourDao.TYPE_BP);
        db.setOriginData(gson.toJson(cusBpList));
        db.setUpload(0);
        //Log.e("-------血压数据", gson.toJson(bpData));
        B30HalfHourDao.getInstance().saveOriginData(db);

        //保存血压的数据
        Integer[] bloodStr = CommCalUtils.calBloodData(bpData);
        Log.e(TAG, "-------血压平均数据=" + Arrays.toString(bloodStr));
        CommDBManager.getCommDBManager().saveCommBloodDb(WatchUtils.getSherpBleMac(MyApp.getContext()), bpData.get(0).getDate(),
                bloodStr[0], bloodStr[1], bloodStr[2], bloodStr[3]);
    }

    /**
     * 保存手环返回的步数数据到本地数据库
     *
     * @param mac      手环MAC地址
     * @param date     日期(步数没有日期,用运动数据的日期)
     * @param stepCurr 当天全部步数
     */
    private void saveStepData(String mac, String date, int stepCurr) {
        // 当天步数数据要以首页单独获取到的步数为准,因为这里获取到的当天总步数,
        // 有时会大于首页获取的实时步数,所以当天的总步数这里不保存
        Log.e(TAG,"---保存手环返回的步数数据到本地数据库" + mac + "\n" + date + "\n" + stepCurr);
        if (date == null || date.equals(WatchUtils.getCurrentDate())) return;
        // 跟本地的对比一下,以防步数倒流
        String step = B30HalfHourDao.getInstance().findOriginData(mac, date, B30HalfHourDao.TYPE_STEP);
        int stepLocal = 0;
        try {
            if (TextUtils.isEmpty(step)) step = "0";
            stepLocal = Integer.parseInt(step);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String bleName = "B31";
        if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME)) bleName = MyCommandManager.DEVICENAME;
        Log.e(TAG, " B31  " + mac + "  " + date + "  " + stepCurr);
        //当天的汇总步数不在此保存，这里是根据详细步数累加步数，和返回的有出入
        if(!WatchUtils.isEquesValue(date)){
            //保存总步数
            CommDBManager.getCommDBManager().saveCommCountStepDate(bleName, mac, date, stepCurr);
            if (stepCurr > stepLocal) {
                B30HalfHourDB db = new B30HalfHourDB();
                db.setAddress(mac);
                db.setDate(date);
                db.setType(B30HalfHourDao.TYPE_STEP);
                db.setOriginData("" + stepCurr);
                db.setUpload(0);
                Log.e(TAG,"---------保存步数总数="+db.toString());
                B30HalfHourDao.getInstance().saveOriginData(db);
            }
        }

    }

    //设置密码回调
    public interface ConnBleHelpListener {
        void connSuccState();
    }

    //步数，电量回调
    public interface ConnBleMsgDataListener {
        /**
         * 电量
         */
        void getBleBatteryData(int batteryLevel);

        /**
         * 步数数据
         */
        void getBleSportData(int step);

        /**
         * 原始数据有更新到数据库的通知
         */
        void onOriginData();
    }


    public void setConnBleMsgDataListener(ConnBleMsgDataListener connBleMsgDataListener) {
        this.connBleMsgDataListener = connBleMsgDataListener;
    }

    public void setConnBleHelpListener(ConnBleHelpListener connBleHelpListener) {
        this.connBleHelpListener = connBleHelpListener;
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
//            Log.e(TAG, "------bleWriteResponse=" + i);
        }
    };
}
