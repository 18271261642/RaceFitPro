package com.example.bozhilun.android.siswatch.bleus;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.siswatch.dataserver.UploadStepsPressent;
import com.example.bozhilun.android.siswatch.dataserver.UploadStepsView;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 上传步数
 */
public class UploadH8StepsService{

    private static volatile UploadH8StepsService uploadH8StepsService;
    private static UploadStepsPressent uploadStepsPressent;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    private Gson gson = new Gson();


    public static synchronized UploadH8StepsService getUploadH8StepsService(){
        if(uploadH8StepsService == null){
            synchronized (UploadH8StepsService.class){
                if(uploadH8StepsService== null){
                    uploadH8StepsService = new UploadH8StepsService();
                    uploadStepsPressent = new UploadStepsPressent();
                    uploadStepsPressent.attach(uploadStepsView);
                }
            }
        }
        return uploadH8StepsService;
    }


    public void syncUserStepsData(Map<String,Integer> stpMap){
        String height = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"userheight","");
        int userHeight = Integer.valueOf(height);   //身高
        String tagNum = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "settagsteps", "");
        int targetStep = Integer.valueOf(tagNum.trim());   //目标步数
        int targetStatus;   //是否达标
        //用户ID
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
        //设备地址
        String deviceCode = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);


        //当天的数据
        int todaySteps = stpMap.get("today");   //当天的步数
        //当天的路程
        double todayDisc = WatchUtils.getDistants(Integer.valueOf(todaySteps), WatchUtils.getStepLong(userHeight));
        //当天的卡路里
        double todayKcal = WatchUtils.mul(todayDisc, WatchUtils.kcalcanstanc);
        int todayStatus = todaySteps - targetStep;
        if(todayStatus >=0){
            targetStatus = 1;
        }else{
            targetStatus = 0;
        }
        //上传当天的数据
        uploadTodaySteps(userId,deviceCode,targetStatus,todaySteps,todayDisc,todayKcal, WatchUtils.getCurrentDate()); //上传当天的数据

        //昨天的数据
        int yestodaySteps = stpMap.get("yestoday");   //昨天的步数
        //昨天的路程
        double yestodayDisc = WatchUtils.getDistants(Integer.valueOf(yestodaySteps), WatchUtils.getStepLong(userHeight));
        //昨天的卡路里
        double yestodayKcal = WatchUtils.mul(yestodayDisc, WatchUtils.kcalcanstanc);
        int yestodayStatus = yestodaySteps - targetStep;
        if(yestodayStatus >=0){
            targetStatus = 1;
        }else{
            targetStatus = 0;
        }
        //上传昨天的数据
        uploadYesTodayStepsToServer(userId,deviceCode,yestodaySteps,yestodayDisc,yestodayKcal,targetStatus);


        //前天的数据
        int thirddaySteps = stpMap.get("qiantian");   //昨天的步数
        //昨天的路程
        double thirddayDisc = WatchUtils.getDistants(Integer.valueOf(thirddaySteps), WatchUtils.getStepLong(userHeight));
        //昨天的卡路里
        double thirddayKcal = WatchUtils.mul(thirddayDisc, WatchUtils.kcalcanstanc);
        int thirddayStatus = thirddaySteps - targetStep;
        if(thirddayStatus >=0){
            targetStatus = 1;
        }else{
            targetStatus = 0;
        }
        //上传前天数据
        uploadThirdStepsToServer(userId,deviceCode,thirddaySteps,thirddayDisc,thirddayKcal,targetStatus);


//        //前后天数据
//        int fourthdaySteps = stpMap.get("fourthDay");   //昨天的步数
//        //昨天的路程
//        double fourthdayDisc = WatchUtils.getDistants(Integer.valueOf(fourthdaySteps), WatchUtils.getStepLong(userHeight));
//        //昨天的卡路里
//        double fourthdayKcal = WatchUtils.mul(fourthdayDisc, WatchUtils.kcalcanstanc);
//        int fourthStatus = fourthdaySteps - targetStep;
//        if(fourthStatus >=0){
//            targetStatus = 1;
//        }else{
//            targetStatus = 0;
//        }
//        uploadForuthDaysToServier(userId,deviceCode,fourthdaySteps,fourthdayDisc,fourthdayKcal,targetStatus);
    }

    //上传当天的数据
    private void uploadTodaySteps(String userId, String deviceCode, int targetStatus, int todaySteps, double todayDisc, double todayKcal, String currentDate) {
        String syncUrl = SyncDbUrls.uploadCountStepUrl();
        try {
            List<Map<String,Object>> ltMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("userid", userId);
            map.put("stepnumber", todaySteps + "");
            map.put("date", currentDate);
            map.put("devicecode", deviceCode);
            map.put("count", "11");
            map.put("distance",todayDisc);
            map.put("calorie",todayKcal);
            map.put("reach",targetStatus+"");
            ltMap.add(map);
            uploadStepsPressent.pressUploadStepData(MyApp.getContext(), syncUrl,gson.toJson(ltMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    //上传前后天的数据
//    private void uploadForuthDaysToServier(String userId, String deviceCode, int fourthdaySteps, double fourthdayDisc, double fourthdayKcal, int targetStatus) {
//        String syncUrl = SyncDbUrls.uploadCountStepUrl();
//        try {
//            List<Map<String,Object>> ltMap = new ArrayList<>();
//            Map<String, Object> map = new HashMap<>();
//            map.put("userid", userId);
//            map.put("stepnumber", fourthdaySteps + "");
//            map.put("date", WatchUtils.obtainAroundDate(WatchUtils.obtainFormatDate(1),false));
//            map.put("devicecode", deviceCode);
//            map.put("count", "11");
//            map.put("distance",fourthdayDisc);
//            map.put("calorie",fourthdayKcal);
//            map.put("reach",targetStatus+"");
//            ltMap.add(map);
//            uploadStepsPressent.pressUploadStepData(MyApp.getContext(), syncUrl,gson.toJson(ltMap));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    //上传前天的数据
    private void uploadThirdStepsToServer(String userId, String deviceCode, int thirddaySteps, double thirddayDisc, double thirddayKcal,int targetStatus) {
        String syncUrl = SyncDbUrls.uploadCountStepUrl();
        try {
            List<Map<String,Object>> ltMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("userid", userId);
            map.put("stepnumber", thirddaySteps + "");
            map.put("date", WatchUtils.obtainFormatDate(2));
            map.put("devicecode", deviceCode);
            map.put("count", "11");
            map.put("distance",thirddayDisc);
            map.put("calorie",thirddayKcal);
            map.put("reach",targetStatus+"");
            ltMap.add(map);
            uploadStepsPressent.pressUploadStepData(MyApp.getContext(), syncUrl,gson.toJson(ltMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //上传昨天的数据
    private void uploadYesTodayStepsToServer(String userId,String deviceCode,int yestodaySteps, double yestodayDisc, double yestodayKcal, int targetStatus) {
        String syncUrl = SyncDbUrls.uploadCountStepUrl();
        try {
            List<Map<String,Object>> ltMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("userid", userId);
            map.put("stepnumber", yestodaySteps + "");
            map.put("date", WatchUtils.obtainFormatDate(1));
            map.put("devicecode", deviceCode);
            map.put("count", "11");
            map.put("distance",yestodayDisc);
            map.put("calorie",yestodayKcal);
            map.put("reach",targetStatus+"");
            ltMap.add(map);
            uploadStepsPressent.pressUploadStepData(MyApp.getContext(), syncUrl,gson.toJson(ltMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static UploadStepsView uploadStepsView = new UploadStepsView() {
        @Override
        public void uploadResultData(Object object) {

        }
    };

}
