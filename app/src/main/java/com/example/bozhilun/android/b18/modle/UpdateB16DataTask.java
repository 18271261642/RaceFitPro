package com.example.bozhilun.android.b18.modle;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b31.km.NohttpUtils;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.commdbserver.detail.CommStepDetailDb;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 上传B16步数详细数据
 * Created by Admin
 * Date 2019/12/21
 */
public class UpdateB16DataTask extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "UpdateB16DataTask";

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

    //上传设备类型
    private void uploadDeviceType(String userId, String bleMac) {
        String url = Commont.FRIEND_BASE_URL +"/user/changeEquipment";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("equipment","B16");
        map.put("mac",bleMac);
        NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x00,url,new Gson().toJson(map),onResponseListener);
    }

    //上传当天的汇总数据
    private void updateTodayCountData(String userId, String bleMac) {
        String currStr = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"b16_curr_sport","");
        if(WatchUtils.isEmpty(currStr))
            return;
        String currSteps = StringUtils.substringBefore(currStr,"-");
        String tempStr = StringUtils.substringAfter(currStr,"-");
        String currDis = StringUtils.substringBefore(tempStr,"|");
        String currCal = StringUtils.substringAfter(tempStr,"|");

        //目标步数
        int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.SPORT_GOAL_STEP,8000);
        List<Map<String,Object>> mapList = new ArrayList<>();
        Map<String, Object> mp = new HashMap<>();
        mp.put("userid", userId);
        mp.put("stepnumber", currSteps);
        mp.put("date", currDayStr);
        mp.put("devicecode", bleMac);
        mp.put("count", "1");
        mp.put("distance",currDis);
        mp.put("calorie",currCal);
        mp.put("reach",(goalStep<=Integer.valueOf(goalStep)?1:0)+"");
        mapList.add(mp);

        String stepUloadUrl = SyncDbUrls.uploadCountStepUrl();
        String jsonStr = new Gson().toJson(mapList);
        //Log.e(TAG,"-------步数参数="+stepUloadUrl+"--参数="+jsonStr);
        NohttpUtils.getNoHttpUtils().getModelRequestJSONObject(0x01,stepUloadUrl,jsonStr,onResponseListener);


    }


    //上传详细数据
    private void updateTodaySport(String userId, String bleMac) {
        try {
            List<Integer> resultList = new ArrayList<>();
            String setpStr = B30HalfHourDao.getInstance().findOriginData(bleMac, WatchUtils.getCurrentDate(), B30HalfHourDao.B16_DETAIL_SPORT);
            if (WatchUtils.isEmpty(setpStr)){
                return;
            }
            List<Integer> listInteger = new Gson().fromJson(setpStr, new TypeToken<List<Integer>>() {
            }.getType());
            resultList.addAll(listInteger);
            if (listInteger.size() <= 48) {
                for (int i = 0; i < 48 - listInteger.size(); i++) {
                    resultList.add(0);
                }
            }else{
                resultList = resultList.subList(0,48);
            }

            List<CommStepDetailDb> list = new ArrayList<>();

            //时间
            int startTime = 0;

            for(int k = 0;k<resultList.size();k++){
                int countTime = startTime +=30;
                CommStepDetailDb commStepDetailDb = new CommStepDetailDb();
                commStepDetailDb.setUserid(userId);
                commStepDetailDb.setDevicecode(bleMac);
                commStepDetailDb.setRtc(currDayStr);
                commStepDetailDb.setStepnumber(resultList.get(k));
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

        } catch (Exception e) {
            e.printStackTrace();
        }
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
