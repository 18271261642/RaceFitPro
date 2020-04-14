package com.example.bozhilun.android.b30.women;


import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.push.MyMessageIntentService;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.google.gson.Gson;
import com.hplus.bluetooth.BleProfileManager;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.WomenSetting;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置手环提醒，经期，备孕期，和宝妈期
 * Created by Admin
 * Date 2018/12/7
 */
public class B36SetWomenDataServer {

    private static final String TAG = "B36SetWomenDataServer";

    public B36SetWomenDataServer() {

    }

    //设置数据同步至手环
    static void setB30WomenData(int statusCode) {
        if (MyCommandManager.DEVICENAME == null)
            return;
        if (!MyCommandManager.DEVICENAME.equals("B36") && !MyCommandManager.DEVICENAME.equals("B16") &&!MyCommandManager.DEVICENAME.equals("B50"))
            return;
        //baby的性别
        String baby_sex = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_BABY_SEX, "M");
        if (WatchUtils.isEmpty(baby_sex))
            baby_sex = "M";
        //最后一次月经的时间
        String lastMenDate = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
        if (WatchUtils.isEmpty(lastMenDate))
            lastMenDate = WatchUtils.getCurrentDate();

        int year = DateTimeUtils.getCurrYear(lastMenDate);
        int month = DateTimeUtils.getCurrMonth(lastMenDate);
        int day = DateTimeUtils.getCurrDay(lastMenDate);

        //最后一次月经日
        TimeData lastTimeD = new TimeData(year, month, day);

        //baby的生日
        String baby_birth = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_BABY_BIRTHDAY, WatchUtils.getCurrentDate());
        if (WatchUtils.isEmpty(baby_birth))
            baby_birth = WatchUtils.getCurrentDate();
        int birthYear = DateTimeUtils.getCurrYear(baby_birth);
        int birthMonth = DateTimeUtils.getCurrMonth(baby_birth);
        int birthDay = DateTimeUtils.getCurrDay(baby_birth);

        //月经间隔长度
        String menesInterval = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_MEN_INTERVAL, "28");
        if (WatchUtils.isEmpty(menesInterval))
            menesInterval = "28";
        //月经持续长度
        String menseLength = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_MEN_LENGTH, "8");
        if (WatchUtils.isEmpty(menseLength))
            menseLength = "8";

        //怀孕期 宝宝出生日
        String babyMens = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.BABY_BORN_DATE,WatchUtils.getCurrentDate());
        if(WatchUtils.isEmpty(babyMens))
            babyMens = WatchUtils.getCurrentDate();
        int babyYear = DateTimeUtils.getCurrYear(babyMens);
        int babyMonth = DateTimeUtils.getCurrMonth(babyMens);
        int babyDay = DateTimeUtils.getCurrDay(babyMens);




        WomenSetting womenSetting = null;

        switch (statusCode) {
            case 0:     //月经期
            case 1:     //备孕期
                womenSetting = new WomenSetting(EWomenStatus.MENES,
                        Integer.valueOf(menseLength.trim()), Integer.valueOf(menesInterval.trim()), lastTimeD);
//                womenSetting = new WomenSetting(EWomenStatus.PREREADY,
//                        Integer.valueOf(menseLength.trim()), Integer.valueOf(menesInterval.trim()),
//                        lastTimeD, ESex.MAN, new TimeData(DateTimeUtils.getCurrYear(), DateTimeUtils.getCurrMonth(), DateTimeUtils.getCurrDay()));
                break;
            case 2:     //怀孕期
                womenSetting = new WomenSetting(EWomenStatus.PREING,lastTimeD,new TimeData(babyYear,babyMonth,babyDay));
                break;
            case 3:     //宝妈期

                womenSetting = new WomenSetting(EWomenStatus.MAMAMI,
                        Integer.valueOf(menseLength.trim()),
                        Integer.valueOf(menesInterval.trim())
                        , lastTimeD, baby_sex.equals("男") ? ESex.MAN : ESex.WOMEN, new TimeData(birthYear, birthMonth, birthDay));
                break;
        }



        submitWomenData(statusCode,lastMenDate,menesInterval,menseLength);


        if(MyCommandManager.DEVICENAME.equals("B36")){
            boolean isB36Noti = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS_B36_JINGQI_NOTI, false);
            if (isB36Noti) {
                MyApp.getInstance().getVpOperateManager().settingWomenState(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new IWomenDataListener() {
                    @Override
                    public void onWomenDataChange(WomenData womenData) {
                        Log.e(TAG, "--------设置手环数据=" + womenData.toString());
                    }
                }, womenSetting);
            }
        }

        if(MyCommandManager.DEVICENAME.equals("B18") || MyCommandManager.DEVICENAME.equals("B50") || MyCommandManager.DEVICENAME.equals("B16")){
            if(!BleProfileManager.getInstance().isConnected())
                return;
            B18BleConnManager.getB18BleConnManager().setWomenData(year,month,day,Integer.valueOf(menesInterval),Integer.valueOf(menseLength));

        }

    }

    //提交数据
    private static void submitWomenData(int statusCode, String lastMenDate, String menesInterval, String menseLength) {
        try {
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.USER_ID_DATA);
            boolean isPush = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.IS_WOMEN_PUSH,true);
           String url = Commont.FRIEND_BASE_URL +  Commont.UPLOAD_WOMEN_MENSTRUAL;
            Map<String,Object> maps = new HashMap<>();
            maps.put("userId",userId);
            maps.put("lastMensesDay",lastMenDate);
            maps.put("cycleDays",menesInterval);
            maps.put("durationDays",menseLength);
            maps.put("cycleStatus",statusCode);
            maps.put("openReminder",isPush ? 1 : 2);   //1开启；2关闭
            String params = new Gson().toJson(maps);
            Log.e(TAG,"----------提交生理期数据="+url+"--="+params);
            OkHttpTool.getInstance().doRequest(url, params, "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e(TAG,"----------提交生理期数据返回="+result);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    static void setDevicePushNoti(boolean isPush,int statusCode){
        try {
            if(MyCommandManager.DEVICENAME == null)
                return;
            //最后一次月经的时间
            String lastMenDate = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
            if (WatchUtils.isEmpty(lastMenDate))
                lastMenDate = WatchUtils.getCurrentDate();
            //月经周期长度
            String menesCycle = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_MEN_INTERVAL, "28");
            if (WatchUtils.isEmpty(menesCycle))
                menesCycle = "28";

            //月经持续长度
            String menseLength = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_MEN_LENGTH, "8");
            if (WatchUtils.isEmpty(menseLength))
                menseLength = "8";

            String url = Commont.FRIEND_BASE_URL +  Commont.UPDATE_WOMEN_MENSTRUAL;
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.USER_ID_DATA);
            if(userId == null)
                return;
            Map<String,Object> maps = new HashMap<>();
            maps.put("userId",userId);
            maps.put("lastMensesDay",lastMenDate);
            maps.put("cycleDays",menesCycle);
            maps.put("durationDays",menseLength);
            maps.put("cycleStatus",statusCode);
            maps.put("openReminder",isPush ? 1 : 2);   //1开启；2关闭
            String params = new Gson().toJson(maps);
            Log.e(TAG,"----------修改生理期数据="+url+"--="+params);
            OkHttpTool.getInstance().doRequest(url, params, "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e(TAG,"----------修改生理期数据返回="+result);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
