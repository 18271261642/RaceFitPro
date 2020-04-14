package com.example.bozhilun.android.xwatch.ble;


import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.WriteBackDataListener;
import com.google.gson.Gson;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2020/2/19
 */
public class XWatchBleAnalysis {

    private static final String TAG = "XWatchBleAnalysis";


    private volatile static XWatchBleAnalysis xWatchBleAnalysis;


    public static XWatchBleAnalysis getW37DataAnalysis() {
        if (xWatchBleAnalysis == null) {
            synchronized (XWatchBleAnalysis.class) {
                if (xWatchBleAnalysis == null)
                    xWatchBleAnalysis = new XWatchBleAnalysis();
            }
        }
        return xWatchBleAnalysis;
    }

    private XWatchBleAnalysis() {

    }


    private B18AlarmBean b18AlarmBean = null;

    public B18AlarmBean getB18AlarmBean() {
        return b18AlarmBean;
    }

    public void setB18AlarmBean(B18AlarmBean b18AlarmBean) {
        this.b18AlarmBean = b18AlarmBean;
    }

    //同步手表时间
    public void syncWatchTime(final XWatchSyncSuccListener xWatchSyncSuccListener){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currTime = calendar.get(Calendar.MINUTE);
        int currSecond = calendar.get(Calendar.SECOND);

        String tmpYear = new BigInteger(String.valueOf(year-2000),16).toString(10);
        String tmpMonth = new BigInteger(String.valueOf(month),16).toString(10);//16进制的s转换为10进制
        String tmpDay = new BigInteger(String.valueOf(day),16).toString(10);//16进制的s转换为10进制


        String s0 = new BigInteger(String.valueOf(currHour),16).toString(10);//16进制的s转换为10进制

        String s1 = new BigInteger(String.valueOf(currTime),16).toString(10);

        String s2 = new BigInteger(String.valueOf(currSecond),16).toString(10);

        int yearByte = Integer.valueOf(tmpYear);
        int monthByte = Integer.valueOf(tmpMonth);
        int dayByte = Integer.valueOf(tmpDay);

        int hourByte = Integer.valueOf(s0);
        int mineByte = Integer.valueOf(s1);
        int secondByte = Integer.valueOf(s2);



        // 0x01 AA BB CC DD EE FF 00 00 00 00 00 00 00 00 ChkSum
        byte[] sync_watch_time = new byte[]{0x01, (byte) (yearByte), (byte) monthByte, (byte) dayByte, (byte) hourByte, (byte) mineByte, (byte) secondByte,0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) (((1+yearByte)+monthByte+dayByte+ hourByte+  mineByte+  secondByte)&0xff)};
        //SWatch指令
        byte[] s_watch_time = new byte[]{0x01, (byte) year, (byte) month, (byte) day, (byte) currHour, (byte) currTime, (byte) currSecond,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00, (byte) ((0x01+ year+ month+ day+ currHour+ currTime+ currSecond)&0xff)};

        String bName = MyCommandManager.DEVICENAME;

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(bName.equals("XWatch")?sync_watch_time:s_watch_time, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(xWatchSyncSuccListener != null)
                    xWatchSyncSuccListener.bleSyncComplete(data);
            }
        });
    }


    //获取手环时间
    public void getWatchTime(WriteBackDataListener writeBackDataListener){
        //0x41 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
        byte[] get_watch_time = new byte[]{0x41,0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x41&0xff};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(get_watch_time,writeBackDataListener);
    }


    //设置手表时间模式 0-12小时;1-24小时
    // 0x37 AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void setWatchTimeType(int timeType){
        byte[] timeTypeByte = new byte[]{0x37, (byte) timeType, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x37+timeType)&0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(timeTypeByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }


    //读取手环时间模式
    // 0x38 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getWatchTimeType(final XWatchTimeModelListener xWatchTimeModelListener){
        byte[] timeType = new byte[]{0x38, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00,0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x38};

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(timeType, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data[0] == 56){
                    if(xWatchTimeModelListener != null)
                        xWatchTimeModelListener.deviceeTimeModel(data[1]);
                }
            }
        });

    }


    //读取固件版本号
    // 0x27 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getDeviceVersion(WriteBackDataListener writeBackDataListener){
        byte[] versionByte = new byte[]{0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x27};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(versionByte,writeBackDataListener);
    }

    //设置距离单位 AA 为单位:00表示为 KM(千米）,01表示为 MILE(英里)
    // 0x0F AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void setDeviceKmUnit(int unitType){
        byte[] kmUint = new byte[]{0x0F, (byte) unitType, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x0f+unitType) & 0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(kmUint, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //获取距离单位
    // 0x4F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getDeviceKmUnit(final XWatchUnitListener xWatchUnitListener){
        byte[] kmUnit = new byte[]{0x4F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x4F};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(kmUnit, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data[0] == 79){
                    if(xWatchUnitListener != null)
                        xWatchUnitListener.backDeviceUnit(data[1]);
                }
            }
        });
    }

    //设置用户个人信息
    /**
     * 0x02 AA BB CC DD 00 00 00 00 00 00 00 00 00 00 ChkSum
     * 功能：   向手环写入用户个人信息，这些信息跟步行距离和卡路里计算相关。
     * 描述：   AA:性别（0表示女性，1表示男性），BB:年龄，CC:身高，DD:体重
     */
    public void setUserInfoToDevice(int sex,int age,int height,int weight,XWatchSyncSuccListener xWatchSyncSuccListener){
        byte[] userInfo = new byte[]{0x02, (byte) sex, (byte) age, (byte) height, (byte) weight, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x02+sex+age+height+weight)&0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(userInfo, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //获取用户个人信息
    //0x42 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getUserInfoFormDevice(WriteBackDataListener writeBackDataListener){
        byte[] userInfo = new byte[]{0x42, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x42};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(userInfo,writeBackDataListener);
    }

    //获取用户步数目标
    // 0x4B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getDeviceSportGoal(final XWatchGoalListener xWatchGoalListener){
        byte[] stepGoal = new byte[]{0x4B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x4B };
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(stepGoal, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data[0] == 75){ // BB * 16777216 + CC * 65536 + DD * 256 + EE
                    int stepGoal = data[2] * 16777216 + data[3] * 65536 + data[4] * 256 + (data[5] & 0xFF);
                    if(xWatchGoalListener != null)
                        xWatchGoalListener.backDeviceGoal(stepGoal);
                }
            }
        });
    }

    //设置用户步数目标
    // 0x0B AA BB CC DD EE 00 00 00 00 00 00 00 00 ChkSum
    public void setDeviceSportGoal(int goal){
        int sportBB = (goal >>24) & 0xFF;
        int sportCC = (goal >>16) & 0xFF;
        int sportDD = (goal >>8) & 0xFF;
        int sportEE = goal & 0xFF;

        byte[] goalByte = new byte[]{0x0B,0x00, (byte) sportBB, (byte) sportCC, (byte) sportDD, (byte) sportEE,0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,(byte) ((byte) (0x0B+sportBB+sportCC+sportDD+sportEE) & 0xFF)};

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(goalByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"------设置步数目标="+Arrays.toString(data));
            }
        });
    }


    //读取闹钟数据
    // 0x24 AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void readDeviceAlarm(int alarmId,final XWatchAlarmBackListener xWatchAlarmBackListener){
        byte[] alarmByte = new byte[]{ 0x24, (byte) alarmId, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x24+alarmId) & 0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(alarmByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"-----------闹钟="+Arrays.toString(data));
                if(data[0] == 36){
                    B18AlarmBean b18AlarmBean = new B18AlarmBean();
                    b18AlarmBean.setId(data[1]);
                    b18AlarmBean.setOpen(data[2] != 0);
                    b18AlarmBean.setHour(data[3]);
                    b18AlarmBean.setMinute(data[4]);
                    b18AlarmBean.setOpenSunday(data[5] != 0);
                    b18AlarmBean.setOpenMonday(data[6] != 0);
                    b18AlarmBean.setOpenTuesday(data[7] != 0);
                    b18AlarmBean.setOpenWednesday(data[8] != 0);
                    b18AlarmBean.setOpenThursday(data[9] != 0);
                    b18AlarmBean.setOpenFriday(data[10] != 0);
                    b18AlarmBean.setOpenSaturday(data[11] != 0);
                    if(xWatchAlarmBackListener != null)
                        xWatchAlarmBackListener.backAlarmOne(b18AlarmBean);
                }
            }
        });
    }



    //设置闹钟数据
    // 0x23 AA BB CC DD EE FF GG HH II JJ KK 00 00 00 ChkSum
    public void setAlarmData(int alarmId,boolean isSwitch,int hour,int mine,boolean sunday,boolean monday,boolean tuesday,
                             boolean wednesday,boolean thursday,boolean friday,boolean saturday,WriteBackDataListener writeBackDataListener){
        byte[] alarmByte = new byte[]{0x23, (byte) alarmId, (byte) (isSwitch ? 1 : 0), (byte) hour, (byte) mine, (byte) (sunday?1:0), (byte) (monday?1:0), (byte) (tuesday?1:0), (byte) (wednesday?1:0), (byte) (thursday?1:0), (byte) (friday?1:0), (byte) (saturday?1:0), 0x00,
                0x00, 0x00, (byte) ((byte) (0x23+ alarmId+ (isSwitch ? 1 : 0)+ hour+ mine+ (sunday?1:0)+ (monday?1:0) + (tuesday?1:0) + (wednesday?1:0) + (thursday?1:0)+ (friday?1:0)+ (saturday?1:0))& 0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(alarmByte,writeBackDataListener);
    }


    //读取所有闹钟
    public void readAllDeviceAlarm(final XWatchAlarmListener xWatchAlarmListener){
        final byte[] alarmByte1 = new byte[]{ 0x24, (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) 0x24 & 0xFF)};

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(alarmByte1);

//        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(alarmByte1, new WriteBackDataListener() {
//            @Override
//            public void backWriteData(byte[] data) {
//                Log.e(TAG,"---------1--闹钟="+Arrays.toString(data));
//            }
//        });


        byte[] alarmByte2 = new byte[]{ 0x24, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x24+1) & 0xFF)};

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceNoBack(alarmByte2);

//        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(alarmByte2, new WriteBackDataListener() {
//            @Override
//            public void backWriteData(byte[] data) {
//                Log.e(TAG,"--------2---闹钟="+Arrays.toString(data));
//
//            }
//        });


        byte[] alarmByte3 = new byte[]{ 0x24, (byte) 0x02, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x24+2) & 0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(alarmByte3, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"--------3---闹钟="+Arrays.toString(data));


                if(data[0] == 36 && data[1] ==0){
                    B18AlarmBean b18AlarmBean = new B18AlarmBean();
                    b18AlarmBean.setId(0);
                    b18AlarmBean.setOpen(data[2] != 0);
                    b18AlarmBean.setHour(data[3]);
                    b18AlarmBean.setMinute(data[4]);
                    b18AlarmBean.setOpenSunday(data[5] != 0);
                    b18AlarmBean.setOpenMonday(data[6] != 0);
                    b18AlarmBean.setOpenTuesday(data[7] != 0);
                    b18AlarmBean.setOpenWednesday(data[8] != 0);
                    b18AlarmBean.setOpenThursday(data[9] != 0);
                    b18AlarmBean.setOpenFriday(data[10] != 0);
                    b18AlarmBean.setOpenSaturday(data[11] != 0);
                    if(xWatchAlarmListener != null)
                        xWatchAlarmListener.backAlarmOne(b18AlarmBean);
                }


                if(data[0] == 36 && data[1] == 1){  //第二个闹钟
                    B18AlarmBean b18AlarmBean = new B18AlarmBean();
                    b18AlarmBean.setId(1);
                    b18AlarmBean.setOpen(data[2] != 0);
                    b18AlarmBean.setHour(data[3]);
                    b18AlarmBean.setMinute(data[4]);
                    b18AlarmBean.setOpenSunday(data[5] != 0);
                    b18AlarmBean.setOpenMonday(data[6] != 0);
                    b18AlarmBean.setOpenTuesday(data[7] != 0);
                    b18AlarmBean.setOpenWednesday(data[8] != 0);
                    b18AlarmBean.setOpenThursday(data[9] != 0);
                    b18AlarmBean.setOpenFriday(data[10] != 0);
                    b18AlarmBean.setOpenSaturday(data[11] != 0);

                    if(xWatchAlarmListener != null)
                        xWatchAlarmListener.backAlarmSecond(b18AlarmBean);
                }


                if(data[0] == 36 && data[1] == 2){  //第三个闹钟
                    B18AlarmBean b18AlarmBean = new B18AlarmBean();
                    b18AlarmBean.setId(2);
                    b18AlarmBean.setOpen(data[2] != 0);
                    b18AlarmBean.setHour(data[3]);
                    b18AlarmBean.setMinute(data[4]);
                    b18AlarmBean.setOpenSunday(data[5] != 0);
                    b18AlarmBean.setOpenMonday(data[6] != 0);
                    b18AlarmBean.setOpenTuesday(data[7] != 0);
                    b18AlarmBean.setOpenWednesday(data[8] != 0);
                    b18AlarmBean.setOpenThursday(data[9] != 0);
                    b18AlarmBean.setOpenFriday(data[10] != 0);
                    b18AlarmBean.setOpenSaturday(data[11] != 0);
                    if(xWatchAlarmListener != null)
                        xWatchAlarmListener.backAlarmThird(b18AlarmBean);
                }
            }
        });


    }




    //读取某天的运动数据 0-当天；1昨天，2-前天
    //0x07 AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getSomeDayForDevice(int day, final XWatchCountStepListener xWatchCountStepListener){
        byte[] dayStep = new byte[]{0x07, (byte) day, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x07+day) & 0xFF)};
        final DecimalFormat decimalFormat = new DecimalFormat("#.#");
        final XWatchStepBean xWatchStepBean = new XWatchStepBean();
        final B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();

        final String bleName = MyCommandManager.DEVICENAME;

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(dayStep, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if (data[0] == 7 && data[1] == 0) {    //第一条回复 当天的 = FF * 65536 + GG * 256 + HH
                    //当天的总步数
                    int countStep = (data[6] & 0xFF) * 65536 + (data[7] * 256) + (data[8] & 0xFF) ;
                    //计算卡路里
                    double kcalStr = (data[12] & 0xFF) * 65536 + data[13] * 256 + (data[14] & 0xFF) ;

                    String resultKcal = decimalFormat.format(bleName.equals("XWatch") ? WatchUtils.div(kcalStr,100,1) : kcalStr );
                    Log.e(TAG,"------步数="+countStep+"--=卡路里="+kcalStr);
                    if(xWatchCountStepListener != null){
                        xWatchCountStepListener.backDeviceCountStep(countStep,Double.valueOf(resultKcal));
                    }

                    String dayStrs = null;
                    if(bleName.equals("XWatch")){
                        //日期
                        String yearStr = Integer.toHexString(data[3]);
                        String moneth = Integer.toHexString(data[4]);
                        String dayStr = Integer.toHexString(data[5]);

                        int tmpYear = Integer.valueOf(yearStr);
                        int tmpMonth = Integer.valueOf(moneth);
                        int tmpDay = Integer.valueOf(dayStr);

                        dayStrs = (tmpYear+2000)+"-"+(tmpMonth<10?"0"+tmpMonth:tmpMonth)+"-"+(tmpDay<10?"0"+tmpDay:tmpDay);
                    }else{
                        dayStrs = WatchUtils.getCurrentDate();
                    }


                    xWatchStepBean.setKcal(kcalStr);
                    xWatchStepBean.setStepNumber(countStep);

                    b30HalfHourDB.setDate(dayStrs);
                    b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
                    b30HalfHourDB.setType(B30HalfHourDao.XWATCH_DAY_STEP);
                }

                if(data[0] == 7 && data[1] == 1){   //返回距离信息 单位 km
                    double todayDisance =  (data[12] & 0xFF) * 65536 + data[7]  * 256 + (data[8] & 0xFF)  ;

                    //运动时间
                    int sportTime = data[9] * 256 + (data[10] & 0xFF);

                    xWatchStepBean.setDisance(todayDisance);
                    xWatchStepBean.setPosrtTime(sportTime);


                    b30HalfHourDB.setOriginData(new Gson().toJson(xWatchStepBean));

                    Log.e(TAG,"--------保存步数="+b30HalfHourDB.toString());

                    B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);


                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    String resultTodayDisance = decimalFormat.format(todayDisance);
                    if(xWatchCountStepListener != null){
                        xWatchCountStepListener.backDeviceDisance(Double.valueOf(resultTodayDisance),sportTime);
                    }
                }
            }
        });
    }


    //获取某天的详细运动数据
    // 0x43 AA 00 00 00 00 00 00 00 00 00 00 00 00 00 CRC  功
    public void getSomeDayDetailSport(final int day, final XWatchSportDetailListener xWatchSportDetailListener){
        byte[] dayByte = new byte[]{0x43 , (byte) day, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x43 + day) & 0xFF)};

        final List<Integer> stepList = new ArrayList<>();
        final Map<String,List<Integer>> stepDetailMap = new HashMap<>();

        final String bleName = MyCommandManager.DEVICENAME;

        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(dayByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"-------详细数据="+ Arrays.toString(data));

                if(bleName.equals("SWatch")){
                    if(data[0] == 67 && data[1] == -16){

                        //下标
                        int position = data[2];

                        int currStep1 = (data[3] & 0xff) * 256 + (data[4] & 0xff);
                        int currStep2 = (data[5] & 0xff) * 256 + (data[6] & 0xff);
                        int currStep3 = (data[7] & 0xff) * 256 + (data[8] & 0xff);
                        int currStep4 = (data[9] & 0xff) * 256 + (data[10] & 0xff);
                        int currStep5 = (data[11] & 0xff) * 256 + (data[12] & 0xff);
                        int currStep6 = (data[13] & 0xff) * 256 + (data[14] & 0xff);
                        stepList.add(currStep1);
                        stepList.add(currStep2);
                        stepList.add(currStep3);
                        stepList.add(currStep4);
                        stepList.add(currStep5);
                        stepList.add(currStep6);


                        if(position == 8){  //返回完了
                            B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
                            b30HalfHourDB.setDate(WatchUtils.getCurrentDate());
                            b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
                            b30HalfHourDB.setType(B30HalfHourDao.XWATCH_DETAIL_SPORT);
                            b30HalfHourDB.setOriginData(new Gson().toJson(stepList));
                            B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);
                            if(xWatchSportDetailListener != null)
                                xWatchSportDetailListener.backDeviceSportDetail(stepDetailMap);
                        }
                    }
                }else{
                    if(data[0] == 67 && data[1] == -16){
                        //日期
                        String yearStr = Integer.toHexString(data[2]);
                        String moneth = Integer.toHexString(data[3]);
                        String dayStr = Integer.toHexString(data[4]);

                        int tmpYear = Integer.valueOf(yearStr);
                        int tmpMonth = Integer.valueOf(moneth);
                        int tmpDay = Integer.valueOf(dayStr);

                        //下标
                        int position = data[5];

                        //步数
                        int currSport = data[9] & 0xFF + data[10] * 256;
                        stepList.add(currSport);
                        String dayStrs = (tmpYear+2000)+"-"+(tmpMonth<10?"0"+tmpMonth:tmpMonth)+"-"+(tmpDay<10?"0"+tmpDay:tmpDay);
                        stepDetailMap.put(dayStrs,stepList);
                        if(position == 95){     //同步完了
                            if(xWatchSportDetailListener != null)
                                xWatchSportDetailListener.backDeviceSportDetail(stepDetailMap);

                            B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
                            b30HalfHourDB.setDate(dayStrs);
                            b30HalfHourDB.setAddress(MyApp.getInstance().getMacAddress());
                            b30HalfHourDB.setType(B30HalfHourDao.XWATCH_DETAIL_SPORT);
                            b30HalfHourDB.setOriginData(new Gson().toJson(stepList));
                            B30HalfHourDao.getInstance().saveOriginData(b30HalfHourDB);

                        }

                    }
                    if(data[0] == 67 && data[1] == -1){ //无数据
                        if(xWatchSportDetailListener != null)
                            xWatchSportDetailListener.backDeviceSportDetail(stepDetailMap);
                    }
                }
            }
        });
    }


    //读取手表中消息提醒开关状态
    // 0x61 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void getDeviceNotiStatus(final XWatchNotiListener xWatchNotiListener){
        byte[] notiByte = new byte[]{0x61, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x61};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(notiByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"----------获取通知返回="+Arrays.toString(data));
                if(data[0] == 97){
                    byte noti = data[1];
                    XWatchNotiBean xWatchNotiBean = new XWatchNotiBean();
                    xWatchNotiBean.setPhoneNoti(((noti>>0)&0x01)==1);
                    xWatchNotiBean.setMsgNoti(((noti>>1)&0x01)==1);
                    xWatchNotiBean.setQQNoti(((noti>>2)&0x01)==1);
                    xWatchNotiBean.setWechatNoti(((noti>>3)&0x01)==1);
                    xWatchNotiBean.setTwitterNoti(((noti>>4)&0x01)==1);
                    xWatchNotiBean.setFaceBookNoti(((noti>>5)&0x01)==1);
                    xWatchNotiBean.setWhatsappNoti(((noti>>6)&0x01)==1);
                    xWatchNotiBean.setSkypeNoti(((noti>>7)&0x01)==1);
                    if(xWatchNotiListener != null)
                        xWatchNotiListener.backDeviceNotiStatus(xWatchNotiBean);
                }
            }
        });
    }


    //设置开关状态
    // 0x60 AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void setDeviceNotiStatus(XWatchNotiBean xWatchNotiBean,WriteBackDataListener writeBackDataListener){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            List<Boolean> boList = xWatchNotiBean.getBooleanList();
            for(int i = boList.size()-1;i>=0;i--){
                Boolean isNoti = boList.get(i);
                stringBuilder.append(isNoti?1:0);
            }
            String bytStr = stringBuilder.toString();

            byte notiByte = WatchUtils.bitToByte(bytStr);
            byte[] resultByte = new byte[]{0x60, notiByte, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x60+notiByte) & 0xff)};
            MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(resultByte, writeBackDataListener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //SWatch推送通知
    //0x4D AA 00 00 00 00 00 00 00 00 00 00 00 00 00 ChkSum
    public void setDeviceNoti(int type){
        byte[] notiByte = new byte[]{0x4D, (byte) type, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) ((byte) (0x4D+type)&0xFF)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(notiByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }



    //SWatch推送通知 type=0时为来电提醒，其它为APP消息提醒
    public void setSWatchNoti(int type){
        byte notiByte = WatchUtils.bitToByte("11111111");
        byte notiByte2 = WatchUtils.bitToByte("11110000");
        byte[] s_watch_byte = new byte[]{0x03,notiByte,notiByte2,type ==0 ?0x01 : WatchUtils.bitToByte("00000010"),0x00,0x00,0x00,0x00,
                0x00, 0x00,0x00,0x00, 0x00,0x00,0x00, (byte) ((0x03+notiByte+notiByte2+(type ==0 ?0x01 : WatchUtils.bitToByte("00000010"))) & 0xff)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(s_watch_byte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    /**
     * SWatch【0x04AA00 000000 00000000 00000000 00CS】
     * 1启动拍照；
     * 0关闭拍照
     */
    public void openOrCloseCamera(int type){
        byte[] cameraByte = new byte[]{0x04, (byte) type,0x00, 0x00,0x00,0x00,
                0x00,0x00,0x00,0x00, 0x00,0x00,0x00,0x00, 0x00, (byte) ((0x04+type)&0xff)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(cameraByte, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }


    /**
     * 设置SWatch闹钟
     * @param savedAlarmStr 实体类
     */
    public void setSWatchAlarm(B18AlarmBean savedAlarmStr){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currTime = calendar.get(Calendar.MINUTE);
        int currSecond = calendar.get(Calendar.SECOND);
        //小时
        int alarmHour = savedAlarmStr.getHour();
        //分钟
        int alarmMine = savedAlarmStr.getMinute();
        //开关
        boolean isOpen = savedAlarmStr.isOpen();
        //星期
        byte weekByte = WatchUtils.bitToByte(savedAlarmStr.setAlarmAnalysis());

        //SWatch指令 01 25 04 0D 0E 07 1D 00 0E 08 01 7F 00 00 00 FF
        byte[] s_watch_time = new byte[]{0x01, 0x25, (byte) month, (byte) day, (byte) currHour,
                (byte) currTime, (byte) currSecond,0x00, (byte) alarmHour, (byte) alarmMine, (byte) (isOpen?1:0),weekByte,0x00,0x00,0x00,
                (byte) ((0x01+ 0x25+ month+ day+ currHour+ currTime+ currSecond+(isOpen?1:0)+weekByte)&0xff)};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(s_watch_time, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });

    }


    /**
     * SWatch查找手表
     * 【0x090000000000000000000000000000CS
     */
    public void findSWatchDevice(){
        byte[] findWatch = new byte[]{0x09,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x09};
        MyApp.getInstance().getW37BleOperateManager().writeBleDataToDeviceForXWatch(findWatch, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }


    /**
     * Bit0：1：来电提醒使能，0：来电提醒关闭
     * Bit1：1：短信提醒使能，0：短信提醒关闭
     * Bit2：1：QQ提醒使能，  0: QQ提醒关闭
     * Bit3：1：微信提醒使能，0: 微信提醒关闭
     * Bit4  1: Twitter提醒使能，0：Twitter提醒关闭
     * Bit5：1：Facebook提醒使能，0：Facebook提醒关闭
     * Bit6：1:Whatsapp提醒使能，0：Whatsapp提醒关闭
     * Bit7：1：Skype提醒使能，0：Skype提醒关闭
     *
     * =0：为来电提醒
     * =1 为短信提醒
     * =2 为QQ提醒
     * =3 为微信提醒
     * =4: Twitter提醒
     * =5：Facebook提醒
     * =6: Whatsapp提醒
     * =7：Skype提醒
     */
}
