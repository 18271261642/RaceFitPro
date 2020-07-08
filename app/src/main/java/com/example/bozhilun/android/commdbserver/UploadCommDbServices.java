package com.example.bozhilun.android.commdbserver;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b31.km.NohttpUtils;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.NetUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存数据上传和下载service，汇总的数据
 * Created by Admin
 * Date 2019/3/6
 */
public class UploadCommDbServices extends IntentService {

    private static final String TAG = "UploadCommDbServices";


    private String bleName = null;
    private Gson gson = new Gson();
    //bac地址
    private static String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());
    //userId
    private static String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
    //结束日期
    private String endDay = WatchUtils.getCurrentDate();  //昨天的，今天的数据今天不上传
    //开始日期
    private String startDay = WatchUtils.obtainFormatDate(2); //前天的


    //从服务器所下载的数据
    String downEndDay = WatchUtils.obtainFormatDate(1);
    String downStartDay = WatchUtils.obtainFormatDate(365);


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadCommDbServices() {
        super("UploadCommDbServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(userId != null && userId.equals("9278cc399ab147d0ad3ef164ca156bf0"))
            return;

        //上传当前设备类型
        uploadCurrDeviceType();

        //查询步数
        findAllUploadData();
        //查询心率
        findSaveHeartData();
        //睡眠
        findSleepUpload();
        //血压
        findBloodUpload();

    }

    //上传当前设备类型
    private void uploadCurrDeviceType() {
        String url = Commont.FRIEND_BASE_URL +"/user/changeEquipment";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("equipment", MyCommandManager.DEVICENAME);
        map.put("mac",bleMac);
        NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x00,url,new Gson().toJson(map),onResponseListener);
    }


    //查询所有需要上传的数据
    private void findAllUploadData() {
        //先查询下步数数据
        final List<CommStepCountDb> uploadStepList = CommDBManager.getCommDBManager().findCountStepForUpload(bleMac, startDay, endDay);
//        Log.e(TAG, "=== 开始查询步数 是否有 " + ((uploadStepList == null || uploadStepList.isEmpty()) ? "无" : "有"));
        if (uploadStepList != null && !uploadStepList.isEmpty()) {
            //Log.e(TAG,"---------间隔大小="+uploadStepList.size());
            //用户的身高
            String userHeight = (String) SharedPreferencesUtils.getParam(this, Commont.USER_HEIGHT, "170");
            if(WatchUtils.isEmpty(userHeight))
                userHeight = "170";
            int uHeight = Integer.valueOf(userHeight.trim());
            //目标步数
            int goalStep = (int) SharedPreferencesUtils.getParam(this,"b30Goal",8000);
            List<Map<String, String>> parmListMap = new ArrayList<>();
            for (CommStepCountDb countDb : uploadStepList) {
                //计算里程
                String dis = WatchUtils.getDistants(countDb.getStepnumber(),uHeight)+"";
                //卡路里
                String kcal = WatchUtils.getKcal(countDb.getStepnumber(),uHeight)+"";
                //Log.e(TAG,"--------查询需要上传的步数="+countDb.toString());

                if(countDb.getDateStr().equals(WatchUtils.getCurrentDate()) || countDb.getDateStr().equals(WatchUtils.obtainFormatDate(1))){
                    Map<String, String> mp = new HashMap<>();
                    mp.put("userid", countDb.getUserid());
                    mp.put("stepnumber", countDb.getStepnumber() + "");
                    mp.put("date", countDb.getDateStr());
                    mp.put("devicecode", countDb.getDevicecode());
                    mp.put("count", countDb.getCount() + "");
                    mp.put("distance",dis);
                    mp.put("calorie",kcal);
                    mp.put("reach",(goalStep<=countDb.getStepnumber()?1:0)+"");
                    parmListMap.add(mp);

                }

            }
            //Log.e(TAG,"--parmListMap--size="+parmListMap.size());
            if (parmListMap.isEmpty()){
                //downloadDb();
                return;
            }

            String stepUloadUrl = SyncDbUrls.uploadCountStepUrl();
            //Log.e(TAG,"-----------参数="+stepUloadUrl+gson.toJson(parmListMap));
            OkHttpTool.getInstance().doRequest(stepUloadUrl, gson.toJson(parmListMap), this, new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    if (WatchUtils.isEmpty(result))
                        return;
                    //Log.e(TAG,"----------步数上传result="+result);
                    if (WatchUtils.isNetRequestSuccess(result))
                        updateUploadCountSteps(uploadStepList);

                }
            });

        }else{
            downloadDb();
        }


    }


    private void downloadDb(){
        //先判断是否有网络，有网络就先删除再下载{}
        if (NetUtils.isConnected(MyApp.getContext())) {
            String whereStr = "userId = ? and deviceCode = ?";
            int code = LitePal.deleteAll(CommDownloadDb.class, whereStr, userId, bleMac);
//            Log.e(TAG, "----------删除的行数=" + code);
            downLoadCountStep(downStartDay, downEndDay);
        }
    }



    //上传传成功后修改数据
    private void updateUploadCountSteps(List<CommStepCountDb> uploadStepList) {
        for (CommStepCountDb countDb : uploadStepList) {
//            Log.e(TAG, "----------上传成功后修改数据=" + countDb.toString());
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpload", true);
            int count = LitePal.updateAll(CommStepCountDb.class, contentValues, "userid = ? and devicecode = ? and dateStr = ?",
                    countDb.getUserid(), countDb.getDevicecode(), countDb.getDateStr());
//            Log.e(TAG, "--------是否修改成功=" + count);
        }

        downloadDb();
    }


    //查找心率数据并上传
    private void findSaveHeartData() {
        final List<CommHeartDb> commHeartDbList = CommDBManager.getCommDBManager().findCommHeartForUpload(bleMac, WatchUtils.obtainFormatDate(2),
                WatchUtils.getCurrentDate());
//        Log.e(TAG, "=== 开始查询心率 是否有 " + ((commHeartDbList == null || commHeartDbList.isEmpty()) ? "无" : "有"));
        if (commHeartDbList == null || commHeartDbList.isEmpty())
            return;
        List<Map<String, String>> list = new ArrayList<>();
        for (CommHeartDb commHeartDb : commHeartDbList) {
//            Log.e(TAG, "---------遍历心率=" + commHeartDb.toString());
            Map<String, String> mp = new HashMap<>();
            if(commHeartDb.getDateStr().equals(WatchUtils.getCurrentDate())){
                mp.put("userid", commHeartDb.getUserid());
                mp.put("devicecode", commHeartDb.getDevicecode());
                mp.put("maxheartrate", commHeartDb.getMaxheartrate() + "");
                mp.put("minheartrate", commHeartDb.getMinheartrate() + "");
                mp.put("avgheartrate", commHeartDb.getAvgheartrate() + "");
                mp.put("rtc", commHeartDb.getDateStr());
                list.add(mp);
            }else{
                if (!commHeartDb.getDateStr().equals(WatchUtils.getCurrentDate()) && !commHeartDb.isUpload()) {
                    mp.put("userid", commHeartDb.getUserid());
                    mp.put("devicecode", commHeartDb.getDevicecode());
                    mp.put("maxheartrate", commHeartDb.getMaxheartrate() + "");
                    mp.put("minheartrate", commHeartDb.getMinheartrate() + "");
                    mp.put("avgheartrate", commHeartDb.getAvgheartrate() + "");
                    mp.put("rtc", commHeartDb.getDateStr());
                    list.add(mp);
                }
            }
        }
        if (list.isEmpty())
            return;
        String heartUrl = SyncDbUrls.uploadHeartUrl();
//        Log.e(TAG, "---------heart=" + gson.toJson(list));
        OkHttpTool.getInstance().doRequest(heartUrl, gson.toJson(list), this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "----------心率上传返回=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
                if (WatchUtils.isNetRequestSuccess(result))
                    updateHeartUpload(commHeartDbList);

            }
        });


    }


    //上传成后修改心率数据
    private void updateHeartUpload(List<CommHeartDb> commHeartDbList) {
        for (CommHeartDb commHeartDb : commHeartDbList) {
//            Log.e(TAG, "------上传成后修改=" + commHeartDb.toString());
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpload", true);
            int count = LitePal.updateAll(CommHeartDb.class, contentValues, "userid = ? and devicecode = ? and dateStr = ?"
                    , commHeartDb.getUserid(), commHeartDb.getDevicecode(), commHeartDb.getDateStr());
//            Log.e(TAG, "--------心率修改返回=" + count);
        }

    }


    //查询睡眠数据并上传
    public void findSleepUpload() {

        final List<CommSleepDb> commSleepDbList = CommDBManager.getCommDBManager().findCommSleepForUpload(bleMac, startDay, endDay);
        if (commSleepDbList == null || commSleepDbList.isEmpty()) {
            return;
        }

        List<Map<String, String>> sleepList = new ArrayList<>();
        for (CommSleepDb commSleepDb : commSleepDbList) {
            //Log.e(TAG, "---------commSleepDb=" + commSleepDb.toString());
            Map<String, String> mp = new HashMap<>();
            if(commSleepDb.getDateStr().equals(WatchUtils.obtainFormatDate(1))){    //睡眠汇总的数据保存的日期是到前一天
                mp.put("userid", commSleepDb.getUserid());
                mp.put("devicecode", commSleepDb.getDevicecode());
                mp.put("rtc", commSleepDb.getDateStr());
                mp.put("sleeplen", commSleepDb.getSleeplen() + "");
                mp.put("deepsleep", commSleepDb.getDeepsleep() + "");
                mp.put("shallowsleep", commSleepDb.getShallowsleep() + "");
                mp.put("soberlen", commSleepDb.getSoberlen() + "");
                mp.put("sleeptime", commSleepDb.getSleeptime());
                mp.put("waketime", commSleepDb.getWaketime());
                mp.put("wakecount", commSleepDb.getWakecount() + "");
                sleepList.add(mp);
            }else{
                if (!commSleepDb.getDateStr().equals(WatchUtils.obtainFormatDate(1)) && !commSleepDb.isUpload()) {
                    mp.put("userid", commSleepDb.getUserid());
                    mp.put("devicecode", commSleepDb.getDevicecode());
                    mp.put("rtc", commSleepDb.getDateStr());
                    mp.put("sleeplen", commSleepDb.getSleeplen() + "");
                    mp.put("deepsleep", commSleepDb.getDeepsleep() + "");
                    mp.put("shallowsleep", commSleepDb.getShallowsleep() + "");
                    mp.put("soberlen", commSleepDb.getSoberlen() + "");
                    mp.put("sleeptime", commSleepDb.getSleeptime());
                    mp.put("waketime", commSleepDb.getWaketime());
                    mp.put("wakecount", commSleepDb.getWakecount() + "");
                    sleepList.add(mp);
                }
            }

        }
        if (sleepList.isEmpty())
            return;
        String sleepParams = gson.toJson(sleepList);
        //Log.e(TAG,"---------汇总睡眠参数="+sleepParams);
        String sleepUrl = SyncDbUrls.uploadSleepUrl();
        OkHttpTool.getInstance().doRequest(sleepUrl, sleepParams, this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "-------睡眠返回=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
                if (WatchUtils.isNetRequestSuccess(result))
                    updateSleepUpload(commSleepDbList);
            }
        });
    }


    //上传成功后修改数据
    private void updateSleepUpload(List<CommSleepDb> commSleepDbList) {
        for (CommSleepDb commSleepDb : commSleepDbList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpload", true);
            int count = LitePal.updateAll(CommSleepDb.class, contentValues, "userid = ? and devicecode = ? and dateStr = ?",
                    commSleepDb.getUserid(), commSleepDb.getDevicecode(), commSleepDb.getDateStr());
//            Log.e(TAG, "------睡眠修改=" + count);
        }

    }


    //血压上传
    public void findBloodUpload() {
        final List<CommBloodDb> bloodDbList = CommDBManager.getCommDBManager().findCommBloodForUpload(bleMac, startDay, endDay);
        if (bloodDbList == null || bloodDbList.isEmpty()) {
            return;
        }

        List<Map<String, String>> bloodList = new ArrayList<>();

        for (CommBloodDb commBloodDb : bloodDbList) {
            Map<String, String> map = new HashMap<>();
            if(commBloodDb.getRtc().equals(WatchUtils.getCurrentDate()) || commBloodDb.getRtc().equals(WatchUtils.obtainFormatDate(1))){
               // Log.e(TAG,"---------血压="+commBloodDb.toString());
                map.put("userid", commBloodDb.getUserid());
                map.put("devicecode", commBloodDb.getDevicecode());
                map.put("maxdiastolic", commBloodDb.getMaxdiastolic() + "");
                map.put("minsystolic", commBloodDb.getMinsystolic() + "");
                map.put("avgdiastolic", commBloodDb.getAvgdiastolic() + "");
                map.put("avgsystolic", commBloodDb.getAvgsystolic() + "");
                map.put("rtc", commBloodDb.getRtc());
                bloodList.add(map);
            }else{
                if (!(commBloodDb.getRtc().equals(WatchUtils.getCurrentDate()) || commBloodDb.getRtc().equals(WatchUtils.obtainFormatDate(1))) && !commBloodDb.isUpload()) {
                    map.put("userid", commBloodDb.getUserid());
                    map.put("devicecode", commBloodDb.getDevicecode());
                    map.put("maxdiastolic", commBloodDb.getMaxdiastolic() + "");
                    map.put("minsystolic", commBloodDb.getMinsystolic() + "");
                    map.put("avgdiastolic", commBloodDb.getAvgdiastolic() + "");
                    map.put("avgsystolic", commBloodDb.getAvgsystolic() + "");
                    map.put("rtc", commBloodDb.getRtc());
                    bloodList.add(map);
                }
            }


        }
        if (bloodList.isEmpty())
            return;
        String bloodUrl = SyncDbUrls.uploadBloodUrl();
        String params = gson.toJson(bloodList);
        //Log.e(TAG, "----汇总血压-bloodUrl=" + bloodUrl + "--参数=" + params);
        OkHttpTool.getInstance().doRequest(bloodUrl, params, this, new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG, "---------血压上传=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
                if (WatchUtils.isNetRequestSuccess(result))
                    updateBloodUpload(bloodDbList);
            }
        });
    }


    //上传成功后修改血压的数据
    private void updateBloodUpload(List<CommBloodDb> bloodDbList) {
        for (CommBloodDb commBloodDb : bloodDbList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("isUpload", true);
            int count = LitePal.updateAll(CommBloodDb.class, contentValues, "userid = ? and devicecode = ? and rtc = ?",
                    commBloodDb.getUserid(), commBloodDb.getDevicecode(), commBloodDb.getRtc());
//            Log.e(TAG, "----------血压上传成功后修改=" + count);
        }
    }


    //从服务器下载所有的数据 downEndDay
    public static void downLoadCountStep(final String startDay, final String endDay) {
        if (WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(userId)) {
            return;
        }
        Gson gson = new Gson();
        String url = SyncDbUrls.downloadCountStepUrl();
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("startDate", startDay);
        map.put("endDate", endDay);
        map.put("deviceCode", bleMac);
        String commParams = gson.toJson(map);

        //步数
        OkHttpTool.getInstance().doRequest(url, commParams, "1", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
//                Log.e(TAG,"-------下载步数返回="+result);
                //downloadCountStepListener.allCountStep(result);
                if (WatchUtils.isEmpty(result))
                    return;
                analysisCountSteps(result, startDay, endDay);
            }
        });

        //心率
        String heartUrl = SyncDbUrls.downloadHeartUrl();
        OkHttpTool.getInstance().doRequest(heartUrl, commParams, "2", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                //Log.e(TAG,"--------心率返回="+result);
                if (WatchUtils.isEmpty(result))
                    return;
                analysisHearatData(result, startDay, endDay);

            }
        });

        //睡眠
        String sleepUrl = SyncDbUrls.downloadSleepUrl();
        OkHttpTool.getInstance().doRequest(sleepUrl, commParams, "3", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                //Log.e(TAG,"--------睡眠返回="+result);
                if (WatchUtils.isEmpty(result))
                    return;
                analysisSleepData(result, startDay, endDay);
            }
        });


        //血压
        String bloodUrl = SyncDbUrls.downloadBloodUrl();
        OkHttpTool.getInstance().doRequest(bloodUrl, commParams, "4", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                //Log.e(TAG, "--------血压返回=" + result);
                if (WatchUtils.isEmpty(result))
                    return;
                analysisBlood(result, startDay, endDay);
            }
        });


    }

    //血压
    private static void analysisBlood(String result, String startDay, String endDay) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("day"))
                return;
            String dayStr = jsonObject.getString("day");
            if (WatchUtils.isEmpty(dayStr))
                return;
            //上一天的日期
            String yesDay = WatchUtils.obtainFormatDate(0);
            Map<String, String> dateMap = CommCalUtils.bloodMap(startDay, endDay);
            for (int i = 0; i < dateMap.size(); i++) {
                yesDay = WatchUtils.obtainAroundDate(yesDay, true, 0);
                dateMap.put(yesDay, "0-0");
            }
            //遍历返回的数组
            JSONArray jsonArray = new JSONArray(dayStr);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                dateMap.put(jsonObject1.getString("rtc"), jsonObject1.getInt("avgdiastolic") + "-"
                        + jsonObject1.getInt("avgsystolic"));

            }

            List<CommDownloadDb> commDownloadDbList = new ArrayList<>();
            for (Map.Entry<String, String> mp : dateMap.entrySet()) {
                CommDownloadDb saveDat = new CommDownloadDb();
                saveDat.setUserId(userId);
                saveDat.setCommType(CommDBManager.COMM_TYPE_BLOOD);
                saveDat.setDeviceCode(bleMac);
                saveDat.setDateStr(mp.getKey());
                saveDat.setStepNumber(mp.getValue());
                commDownloadDbList.add(saveDat);
            }

            CommDBManager.getCommDBManager().saveAllCommDataForType(bleMac, CommDBManager.COMM_TYPE_BLOOD, commDownloadDbList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //睡眠
    private static void analysisSleepData(String result, String startDay, String endDay) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("day"))
                return;
            String dayStr = jsonObject.getString("day");
//            Log.e(TAG, "------sleep--dayStr=" + dayStr);
            if (WatchUtils.isEmpty(dayStr))
                return;
            //上一天的日期
            String yesDay = WatchUtils.obtainFormatDate(0);
            Map<String, String> dateMap = CommCalUtils.sleepMap(startDay, endDay);
            for (int i = 0; i < dateMap.size(); i++) {
                yesDay = WatchUtils.obtainAroundDate(yesDay, true, 0);
                dateMap.put(yesDay, "0");
            }
            //遍历返回的数组
            JSONArray jsonArray = new JSONArray(dayStr);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                dateMap.put(jsonObject1.getString("rtc"), jsonObject1.getInt("sleeplen") + "");

            }

            List<CommDownloadDb> commDownloadDbList = new ArrayList<>();
            for (Map.Entry<String, String> mp : dateMap.entrySet()) {
                CommDownloadDb saveDat = new CommDownloadDb();
                saveDat.setUserId(userId);
                saveDat.setCommType(CommDBManager.COMM_TYPE_SLEEP);
                saveDat.setDeviceCode(bleMac);
                saveDat.setDateStr(mp.getKey());
                saveDat.setStepNumber(mp.getValue());
                commDownloadDbList.add(saveDat);
            }

            CommDBManager.getCommDBManager().saveAllCommDataForType(bleMac, CommDBManager.COMM_TYPE_SLEEP, commDownloadDbList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //心率
    private static void analysisHearatData(String result, String startDay, String endDay) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("day"))
                return;
            String dayStr = jsonObject.getString("day");
//            Log.e(TAG, "--------dayStr=" + dayStr);
            if (WatchUtils.isEmpty(dayStr))
                return;
            //上一天的日期
            String yesDay = WatchUtils.obtainFormatDate(0);
            Map<String, String> dateMap = CommCalUtils.heartMap(startDay, endDay);
            for (int i = 0; i < dateMap.size(); i++) {
                yesDay = WatchUtils.obtainAroundDate(yesDay, true, 0);
                dateMap.put(yesDay, "0");
            }
            //遍历返回的数组
            JSONArray jsonArray = new JSONArray(dayStr);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                dateMap.put(jsonObject1.getString("rtc"), jsonObject1.getInt("avgheartrate") + "");

            }

            List<CommDownloadDb> commDownloadDbList = new ArrayList<>();
            for (Map.Entry<String, String> mp : dateMap.entrySet()) {
                CommDownloadDb saveDat = new CommDownloadDb();
                saveDat.setUserId(userId);
                saveDat.setCommType(CommDBManager.COMM_TYPE_HEART);
                saveDat.setDeviceCode(bleMac);
                saveDat.setDateStr(mp.getKey());
                saveDat.setStepNumber(mp.getValue());
                commDownloadDbList.add(saveDat);
            }

            CommDBManager.getCommDBManager().saveAllCommDataForType(bleMac, CommDBManager.COMM_TYPE_HEART, commDownloadDbList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //解析步数
    private static void analysisCountSteps(String result, String startDay, String endDay) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("day"))
                return;
            String dayStr = jsonObject.getString("day");
//            Log.e(TAG, "--------dayStr=" + dayStr);
            if (WatchUtils.isEmpty(dayStr))
                return;

            //上一天的日期
            String yesDay = WatchUtils.obtainFormatDate(0);
            Map<String, String> dateMap = CommCalUtils.countStepMap(startDay, endDay);
            for (int i = 0; i < dateMap.size(); i++) {
                yesDay = WatchUtils.obtainAroundDate(yesDay, true, 0);
                dateMap.put(yesDay, "0");
            }

            //遍历返回的数组
            JSONArray jsonArray = new JSONArray(dayStr);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                dateMap.put(jsonObject1.getString("date"), jsonObject1.getInt("stepnumber") + "");
            }

            List<CommDownloadDb> commDownloadDbList = new ArrayList<>();
            for (Map.Entry<String, String> mp : dateMap.entrySet()) {
                CommDownloadDb saveDat = new CommDownloadDb();
                saveDat.setUserId(userId);
                saveDat.setCommType(CommDBManager.COMM_TYPE_STEP);
                saveDat.setDeviceCode(bleMac);
                saveDat.setDateStr(mp.getKey());
                saveDat.setStepNumber(mp.getValue());
                commDownloadDbList.add(saveDat);
            }

            CommDBManager.getCommDBManager().saveAllCommDataForType(bleMac, CommDBManager.COMM_TYPE_STEP, commDownloadDbList);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
           // Log.e(TAG,"-------what="+what+response.get().toString());
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
           // Log.e(TAG,"------falile="+what+"---="+response.getException().getMessage());
        }

        @Override
        public void onFinish(int what) {

        }
    };


}
