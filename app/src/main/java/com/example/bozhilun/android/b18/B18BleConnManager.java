package com.example.bozhilun.android.b18;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b18.modle.B16CadenceBean;
import com.example.bozhilun.android.b18.modle.B16CadenceResultBean;
import com.example.bozhilun.android.b18.modle.B16DbManager;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.b18.modle.B18CountSleepBean;
import com.example.bozhilun.android.b18.modle.B18SleepChartBean;
import com.example.bozhilun.android.b18.modle.B18StepBean;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hplus.bluetooth.BleProfileManager;
import com.hplus.bluetooth.bean.AlarmClockBean;
import com.hplus.bluetooth.command.OnResponseListener;
import com.hplus.bluetooth.connect.ConnectListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by Admin
 * Date 2019/11/10
 */
public class B18BleConnManager  {

    private static final String TAG = "B18BleConnManager";

    private static volatile  B18BleConnManager b18BleConnManager;

    private static Gson gson = new Gson();

    private static List<B18StepBean> resultStepList = new ArrayList<>();
    private static List<Integer> halfHourStepList = new ArrayList<>();


    private static String bleMac = MyApp.getInstance().getMacAddress();

    //判断是否可以同步数据
    public static boolean isAllowSyncData = false;

    //是否是ota升级
    public static boolean isOta = false;

    private B18BleConnManager() {
    }

    public static B18BleConnManager getB18BleConnManager(){
        if(b18BleConnManager == null){
            Class var = B18BleConnManager.class;
            synchronized (B18BleConnManager.class){
                if(b18BleConnManager == null){
                    b18BleConnManager = new B18BleConnManager();
                    BleProfileManager.getInstance().getConnectController().addConnectListener(connectListener);
                    BleProfileManager.getInstance().getCommandController().addResponseListener(onResponseListener);
                }

            }
        }
        return b18BleConnManager;
    }


    /**
     * 连接设备
     * @param bluetoothDevice ble对象
     */
    public void connB18Device(BluetoothDevice bluetoothDevice){
        BleProfileManager.getInstance().getConnectController().connectDevice(bluetoothDevice);
    }

    //断开连接
    public void disConnB18Device(Context mContext){
        MyApp.getInstance().setMacAddress("");
        SharedPreferencesUtils.saveObject(mContext, Commont.BLEMAC,"");
        SharedPreferencesUtils.saveObject(mContext,Commont.BLENAME,"");
        BleProfileManager.getInstance().getConnectController().disConnectDevice();

    }

    public void clearDeviceData(){
        BleProfileManager.getInstance().getCommandController().clearHistoryData();
    }

    /**
     * 同步信息
     */
    public static void syncB18DeviceData(){
        if(!BleProfileManager.getInstance().isConnected())
            return;
        //同步日期
        BleProfileManager.getInstance().getCommandController().sendDateCommand();
        //同步时间
        BleProfileManager.getInstance().getCommandController().sendTimeCommand();
        //同步星期
        BleProfileManager.getInstance().getCommandController().setWeekCommand(WatchUtils.getDataForWeek()==7?0:WatchUtils.getDataForWeek());

        //同步语言
        BleProfileManager.getInstance().getCommandController().sendLanguageCommand();

        //获取当天的详细运动数据
        resultStepList.clear();
        BleProfileManager.getInstance().getCommandController().sendAllDayTenMinuteDataCommand();

        //获取汇总睡眠
        BleProfileManager.getInstance().getCommandController().getSleepDataCommand();
    }


    //获取睡眠
    public void getDeviceSleepData(){
        BleProfileManager.getInstance().getCommandController().getSleepChart();
    }

    //设置久坐提醒
    public  void setLongSitNoti(boolean isOn,int inter,int startHour,int startMine,int endHour,int endMine){
        BleProfileManager.getInstance().getCommandController().sendSedentaryCommand(isOn,inter,startHour,startMine,endHour,endMine);
    }


    //设置转腕亮屏开关
    public void setTurnWristStatus(boolean isOn){
        BleProfileManager.getInstance().getCommandController().sendTurnWristScreenCommand(isOn);
    }


    //设置闹钟
    public void setAllAlarm(List<B18AlarmBean> b18AlarmBeanList){
        if(!BleProfileManager.getInstance().isConnected())
            return;
        List<AlarmClockBean> alarmClockBeanList = new ArrayList<>();
        for(B18AlarmBean b18AlarmBean : b18AlarmBeanList){
            AlarmClockBean alarmClockBean = new AlarmClockBean(b18AlarmBean.isOpen(),b18AlarmBean.getHour(),b18AlarmBean.getMinute()
                    ,b18AlarmBean.isOpenSaturday(),b18AlarmBean.isOpenSunday(),b18AlarmBean.isOpenFriday(),b18AlarmBean.isOpenThursday()
                    ,b18AlarmBean.isOpenWednesday(),b18AlarmBean.isOpenTuesday(),b18AlarmBean.isOpenMonday());
            Log.e(TAG,"---------设置闹钟="+gson.toJson(alarmClockBean));
            alarmClockBeanList.add(alarmClockBean);
        }

        BleProfileManager.getInstance().getCommandController().setAlarmClock(alarmClockBeanList);
    }

    //设置屏保时间
    public void setScreenTime(int intelV){
        BleProfileManager.getInstance().getCommandController().setScreenSaveCommand(intelV);
    }


    //设置女性生理周期数据
    public void setWomenData(int year,int month,int day,int menesInterval,int menseLength){
        BleProfileManager.getInstance().getCommandController().setMenstrualPeriod(year,month,day,menesInterval,menseLength);
    }

    /**
     * 数据返回
     */
    private static OnResponseListener onResponseListener = new OnResponseListener() {
        @Override
        public void onResponse(String s) {
            Log.e(TAG,"--------response="+s);
            analysisResponseData(s);
        }
    };

    private static void analysisResponseData(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if(!jsonObject.has("type"))
                return;
            int type = jsonObject.getInt("type");
            switch (type){
                case 0x01:
                break;
                case 0x08:  //获取设备版本信息，此标识可用于表示已经连接成功，可以收发数据了
                    if(!isOta){
                        isAllowSyncData = true;
                        //设置移动设备类型
                        BleProfileManager.getInstance().getCommandController().setPhoneType(true);
                        syncB18DeviceData();
                    }

                    analysisDeviceVersion(jsonObject);

                    break;
                case 0x02:  //汇总睡眠数据
                    analysisCountSleep(jsonObject);
                    break;
                case 0x05:  //返回运动的条数
                    //getStepsCount(jsonObject);
                    break;
                case 0x06:  //步数详细返回
                    analysisStepsData(jsonObject,type);
                    break;
                case 10:    //睡眠图表数据
                    operateSleepData(jsonObject);
                    break;
                case 11:    //计算步频
                    analysysStepCadence(jsonObject);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //计算步频的数据
    private static void analysysStepCadence(JSONObject jsonObject) {
        try {
            String dateStr = jsonObject.getString("data");
            B16CadenceBean b16CadenceBean = gson.fromJson(dateStr,B16CadenceBean.class);
            if(b16CadenceBean == null)
                return;
            //判断总的运动步数和距离是否为0，为0就表示无运动数据了
            if(b16CadenceBean.getCalorie() == 0 && b16CadenceBean.getStep() == 0 && b16CadenceBean.getDistance() == 0)
                return;
            //间隔
            int intevalTime = b16CadenceBean.getIntervalTime();
            //转换为分钟
            //int intevalMineTime = intevalTime ;

            //日期
            int year = b16CadenceBean.getYear();
            int month = b16CadenceBean.getMonth();
            int day = b16CadenceBean.getDay();
            int hour = b16CadenceBean.getHour();
            int minute = b16CadenceBean.getMinute();
            int second = b16CadenceBean.getSecond();

            //日期
            String dayStr = year+"-"+(month<=9?"0"+month:month)+"-"+(day<=9?"0"+day:day);

            //日期参数
            String paramsDayStr = dayStr +" "+ (hour<=9?"0"+hour:hour)+"-"+(minute<=9?"0"+minute:minute) +"-"+second;


            //详细的数据
            List<B16CadenceBean.DetailDataBean> detailDataBeanList = b16CadenceBean.getDetailData();
            List<B16CadenceBean.DetailDataBean> tmpList = new ArrayList<>();
            for(B16CadenceBean.DetailDataBean bd : detailDataBeanList){
                if(bd.getStep() != 0 && bd.getDistance() != 0 && bd.getCalorie() != 0)
                    tmpList.add(bd);
            }

            //总的运动数据条数
            int sportCount = tmpList.size();

            B16CadenceResultBean b16CadenceResultBean = new B16CadenceResultBean();
            b16CadenceResultBean.setBleMac(bleMac);
            b16CadenceResultBean.setSteps(b16CadenceBean.getCalorie());
            b16CadenceResultBean.setDistance(b16CadenceBean.getDistance());
            b16CadenceResultBean.setCalorie(b16CadenceBean.getCalorie());
            b16CadenceResultBean.setCurrDayStr(dayStr);
            b16CadenceResultBean.setParamsDateStr(paramsDayStr);
            b16CadenceResultBean.setSportCount(sportCount);
            b16CadenceResultBean.setSportDuration(intevalTime);
            b16CadenceResultBean.setSportDetailStr(gson.toJson(tmpList));

            Log.e(TAG,"---------步频数据="+b16CadenceResultBean.toString());

            B16DbManager.getB16DbManager().saveB16Cadence(b16CadenceResultBean);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void analysisDeviceVersion(JSONObject jsonObject) {
        try {
            JSONObject json = jsonObject.optJSONObject("data");
            int b18Version = json.getInt("version");
            SharedPreferencesUtils.setParam(MyApp.getContext(),"B18_version",b18Version);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //处理汇总睡眠
    private static void analysisCountSleep(JSONObject jsonObject) {
        try {
            if(!jsonObject.getBoolean("response"))return;

            String dataStr = jsonObject.getString("data");
            if(dataStr == null)
                return;
            B18CountSleepBean b18CountSleepBean = gson.fromJson(dataStr,B18CountSleepBean.class);
            int year = b18CountSleepBean.getYear();
            if(year == 0){
                operateStepData();  //处理步数详细数据
                return;
            }

            int day = b18CountSleepBean.getDay();
            String dayStr = year +"-" +b18CountSleepBean.getMonth() + "-" + (day<=9?"0"+day:day);
            B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
            b30HalfHourDB.setDate(dayStr);
            b30HalfHourDB.setType(B30HalfHourDao.B18_COUNT_SLEEP);
            b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
            b30HalfHourDB.setOriginData(gson.toJson(b18CountSleepBean));
            B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //保存睡眠详细数据
    private static void operateSleepData(JSONObject jsonObject){
        try {
            if(!jsonObject.getBoolean("response")){ //可表示睡眠详细数据已获取完成
                //通知界面更新
                sendBroadData(WatchUtils.B18_UPDATE_HOME_DATA,"1");
                return;
            }

            String dataStr = jsonObject.getString("data");
            B18SleepChartBean b18SleepChartBean = gson.fromJson(dataStr,B18SleepChartBean.class);
            if(b18SleepChartBean == null)return;
            int year = b18SleepChartBean.getYear();
            int month = b18SleepChartBean.getMonth();
            int day = b18SleepChartBean.getDay();



            String sleepDay = year +"-" +month + "-"+(day<=9?"0"+day:day);
            Log.e(TAG,"--------详细睡眠="+sleepDay);
            B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
            b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
            b30HalfHourDB.setType(B30HalfHourDao.B16_DETAIL_SLEEP);
            b30HalfHourDB.setDate(sleepDay);
            b30HalfHourDB.setUpload(0);
            b30HalfHourDB.setOriginData(gson.toJson(b18SleepChartBean));
            B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //处理保存运动详细数据
    private static void operateStepData(){
        if(resultStepList.isEmpty())return;
        halfHourStepList.clear();
        int leftOverCount = 0;
        for(int i = 0;i<resultStepList.size();i+=3){
            if(i+3 <= resultStepList.size()-1){
                int threeStepCount = resultStepList.get(i).getStep()+resultStepList.get(i+1).getStep()
                        +resultStepList.get(i+2).getStep();
                halfHourStepList.add(threeStepCount);
            }else{
                leftOverCount +=resultStepList.get(i).getStep();
                halfHourStepList.add(leftOverCount);
            }

        }

        Log.e(TAG,"-------处理完大小="+halfHourStepList.size());

        Log.e(TAG,"-------gson.toJson(halfHourStepList)="+gson.toJson(halfHourStepList));

        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        b30HalfHourDB.setDate(WatchUtils.getCurrentDate());
        b30HalfHourDB.setType(B30HalfHourDao.B16_DETAIL_SPORT);
        b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
        b30HalfHourDB.setOriginData(gson.toJson(halfHourStepList));
        b30HalfHourDB.setUpload(0);
        B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);

        halfHourStepList.clear();
        resultStepList.clear();

        //开始获取详细睡眠数据
        BleProfileManager.getInstance().getCommandController().getSleepChart();
    }



    //处理步数详细返回
    private static void analysisStepsData(JSONObject jsonObject,int typeCode){
        try {
            String dataStr = jsonObject.getString("data");
            List<B18StepBean> b18StepBeans = gson.fromJson(dataStr,new TypeToken<List<B18StepBean>>(){}.getType());
            resultStepList.addAll(b18StepBeans);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 连接状态
     */
    private static ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onDeviceNoSupport(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void onConnectSuccess(final BluetoothDevice bluetoothDevice) {
            Log.e(TAG,"-----------onConnectSuccess-----------");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyCommandManager.DEVICENAME = "B16";
                    MyApp.getInstance().setMacAddress(bluetoothDevice.getAddress());
                    SharedPreferencesUtils.saveObject(MyApp.getContext(),Commont.BLEMAC,bluetoothDevice.getAddress());
                    SharedPreferencesUtils.saveObject(MyApp.getContext(),Commont.BLENAME,"B16");
                    sendBroadData(WatchUtils.B18_CONNECTED_ACTION,bluetoothDevice.getAddress());
                }
            }, 500L);

        }

        @Override
        public void onConnecting(BluetoothDevice bluetoothDevice) {
            Log.e(TAG,"-----------onConnecting-----------");
        }

        @Override
        public void disConnect(BluetoothDevice bluetoothDevice) {
            Log.e(TAG,"-----------disConnect-----------");
            isAllowSyncData = false;
            sendBroadData(WatchUtils.B18_DISCONN_ACTION,bluetoothDevice.getAddress());
        }

        @Override
        public void connectFail(BluetoothDevice bluetoothDevice, int i) {
            Log.e(TAG,"-----------connectFail-----------");
            isAllowSyncData = false;
        }
    };


    /**
     * 发送广播
     * @param
     */
    private static void sendBroadData(String actionStr,String str){
        Intent intent = new Intent();
        intent.setAction(actionStr);
        intent.putExtra("B18Params",str);
        MyApp.getInstance().getApplicationContext().sendBroadcast(intent);

    }

}
