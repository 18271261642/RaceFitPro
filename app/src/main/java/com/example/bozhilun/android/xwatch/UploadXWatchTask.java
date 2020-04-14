package com.example.bozhilun.android.xwatch;

import android.os.AsyncTask;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b31.km.NohttpUtils;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.commdbserver.detail.CommStepDetailDb;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.xwatch.ble.XWatchStepBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2020/3/10
 */
public class UploadXWatchTask extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "UploadXWatchTask";

    private String userId = null;
    private String bleMac = null;
    private String currDayStr = WatchUtils.getCurrentDate();


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
        bleMac = MyApp.getInstance().getMacAddress();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(userId == null || bleMac == null)
            return null;
        //上传设备类型
        uploadDeviceType(userId,bleMac);

        updateTodayCountData(userId,bleMac);

        updateTodaySport(userId,bleMac);

        return null;
    }

    private void updateTodaySport(String userId, String bleMac) {
        try {
            String originDataStr = B30HalfHourDao.getInstance().findOriginData(bleMac, currDayStr, B30HalfHourDao.XWATCH_DETAIL_SPORT);
            Log.e(TAG, "---------详情步数=" + originDataStr);
            if (originDataStr == null)
                return;

            List<Integer> orginList = new Gson().fromJson(originDataStr, new TypeToken<List<Integer>>() {
            }.getType());
            if(orginList.isEmpty())
                return;
            //时间
            int startTime = 0;

            List<CommStepDetailDb> list = new ArrayList<>();
            for (int i = 0; i < orginList.size(); i += 2) {
                if (i + 1 <= orginList.size() - 1) {
                    int countTime = startTime += 30;
                    int halfHourStepCount = orginList.get(i) + orginList.get(i + 1);
                    CommStepDetailDb commStepDetailDb = new CommStepDetailDb();
                    commStepDetailDb.setUserid(userId);
                    commStepDetailDb.setDevicecode(bleMac);
                    commStepDetailDb.setRtc(currDayStr);
                    commStepDetailDb.setStepnumber(halfHourStepCount);
                    commStepDetailDb.setDistance("0");
                    //kcal是int类型
                    commStepDetailDb.setCalories("0");
                    commStepDetailDb.setStartdate(getTimeStr(countTime));
                    commStepDetailDb.setEnddate("11");
                    commStepDetailDb.setSpeed(1);
                    commStepDetailDb.setAction(1);
                    commStepDetailDb.setStatus(1);
                    list.add(commStepDetailDb);


                }
            }


            String url = SyncDbUrls.uploadDetailStepUrl();

            List<Map<String, Object>> jsonObjectList = new ArrayList<>();
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("deviceCode", bleMac);
            jsonObject.put("rtc", currDayStr);
            jsonObject.put("userId", userId);
            jsonObject.put("stepNumberList", list);
            jsonObjectList.add(jsonObject);
            String jsonStr = new Gson().toJson(jsonObjectList);

            NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x02,url,jsonStr,onResponseListener);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateTodayCountData(String userId, String bleMac) {
        try {
            String dayCountStepStr = B30HalfHourDao.getInstance().findOriginData(bleMac,currDayStr,B30HalfHourDao.XWATCH_DAY_STEP);
            if(dayCountStepStr == null)
                return;
            XWatchStepBean xWatchStepBean = new Gson().fromJson(dayCountStepStr,XWatchStepBean.class);
            if(xWatchStepBean == null)
                return;
            double xDisance = xWatchStepBean.getDisance();

            double xKcal = xWatchStepBean.getKcal();
            int stepNumber = xWatchStepBean.getStepNumber();

            //目标步数
            int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.SPORT_GOAL_STEP,8000);
            List<Map<String,Object>> mapList = new ArrayList<>();
            Map<String, Object> mp = new HashMap<>();
            mp.put("userid", userId);
            mp.put("stepnumber", stepNumber);
            mp.put("date", currDayStr);
            mp.put("devicecode", bleMac);
            mp.put("count", "1");
            mp.put("distance",xDisance);
            mp.put("calorie",xKcal);
            mp.put("reach",(goalStep<=Integer.valueOf(goalStep)?1:0)+"");
            mapList.add(mp);

            String stepUloadUrl = SyncDbUrls.uploadCountStepUrl();
            String jsonStr = new Gson().toJson(mapList);
            //Log.e(TAG,"-------步数参数="+stepUloadUrl+"--参数="+jsonStr);
            NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x01,stepUloadUrl,jsonStr,onResponseListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //上传设备类型
    private void uploadDeviceType(String userId, String bleMac) {
        String url = Commont.FRIEND_BASE_URL +"/user/changeEquipment";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("equipment","B16");
        map.put("mac",bleMac);
        NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x00,url,new Gson().toJson(map),onResponseListener);
    }



    private String getTimeStr(int countTime){
        int hour = countTime / 60;
        int mine = countTime % 60;

        return (String.valueOf(hour).length()<2?"0"+hour:hour)+":"+(mine==0?"00":mine);

    }

    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            Log.e(TAG,"-------what="+what+response.get().toString());
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e(TAG,"------falile="+what+"---="+response.getException().getMessage());
        }

        @Override
        public void onFinish(int what) {

        }
    };
}
