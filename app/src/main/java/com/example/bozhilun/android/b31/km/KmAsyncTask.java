package com.example.bozhilun.android.b31.km;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.commdbserver.detail.CommBloodDetailDb;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.example.bozhilun.android.w30s.ota.HttpResponseListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传康美的数据
 * Created by Admin
 * Date 2019/12/12
 */
public class KmAsyncTask extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "KmAsyncTask";

    String userId = null;
    String deviceCode = null;

    private Gson gson = new Gson();

    private NohttpUtils nohttpUtils;

    private String currDayStr = WatchUtils.getCurrentDate();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        nohttpUtils = NohttpUtils.getNoHttpUtils();
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
        deviceCode = MyApp.getInstance().getMacAddress();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(userId == null)
            return null;
        uploadKmData();

        uploadStepDetailToday(deviceCode);  //当天步数

        uploadHeartToday(deviceCode);

        uplaodBloodToday(deviceCode);   //当天血压

        return null;
    }



    //上传血压
    private void uplaodBloodToday(String deviceCode){
       try {
           List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(deviceCode, B30HalfHourDao.TYPE_BP, currDayStr);
           if (heartDayList == null){
               return;
           }
           B30HalfHourDB b30HalfHourDB = heartDayList.get(0);
           List<CusVPHalfHourBpData> bpData = gson.fromJson(b30HalfHourDB.getOriginData(), new TypeToken<List<CusVPHalfHourBpData>>() {
           }.getType());
           if(bpData == null)
               return;
           List<Map<String,Object>> bloodList = new ArrayList<>();
           for(CusVPHalfHourBpData cb : bpData){
               Map<String,Object> map = new HashMap<>();
               map.put("AccountId",userId);
               map.put("DeviceCode",deviceCode);
               map.put("Sbp",cb.getLowValue());
               map.put("Dbp",cb.getHighValue());
               map.put("TestTime",cb.getTime().getDateAndClockForSleepSecond());
               bloodList.add(map);
           }
           String bpUrl = KmConstance.uploadBloodData();
           nohttpUtils.getModelRequestJSONObject(0x03,bpUrl,gson.toJson(bloodList),objectOnResponseListener);
       }catch (Exception e){
           e.printStackTrace();
       }
    }



    //上传当天心率
    private void uploadHeartToday(String deviceCode){
        try {
            String heartUrl = KmConstance.uploadHeartData();
            List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(deviceCode, B30HalfHourDao.TYPE_RATE, currDayStr);
            if (heartDayList == null)
                return;
            B30HalfHourDB b30HalfHourDB = heartDayList.get(0);

            List<CusVPHalfRateData> cusRateData = gson.fromJson(b30HalfHourDB.getOriginData(),new TypeToken<List<CusVPHalfRateData>>() {
            }.getType());
            if(cusRateData == null)
                return;
            List<Map<String,Object>> heartLt = new ArrayList<>();
            for(CusVPHalfRateData cusVPHalfRateData : cusRateData){
                Map<String,Object> map = new HashMap<>();
                map.put("AccountId",userId);
                map.put("DeviceCode",deviceCode);
                map.put("Hb",cusVPHalfRateData.getRateValue());
                map.put("TestTime",cusVPHalfRateData.getTime().getDateAndClockForSleepSecond());
                heartLt.add(map);
            }
            nohttpUtils.getModelRequestJSONObject(0x02,heartUrl,gson.toJson(heartLt),objectOnResponseListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    //上传当天的步数
    private void uploadStepDetailToday(String deviceCode) {
        try {
            String walkUrl = KmConstance.uploadWalkData();
            List<B30HalfHourDB> stepDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(deviceCode,
                    B30HalfHourDao.TYPE_SPORT, WatchUtils.getCurrentDate());
            if(stepDayList == null)
                return;
            B30HalfHourDB b30HalfHourDB = stepDayList.get(0);
            List<CusVPHalfSportData> cusVPHalfSportDataList = gson.fromJson(b30HalfHourDB.getOriginData(),
                    new TypeToken<List<CusVPHalfSportData>>(){}.getType());
            if(cusVPHalfSportDataList == null)
                return;
            List<Map<String,Object>> walkList = new ArrayList<>();
            for(CusVPHalfSportData cusVPHalfSportData : cusVPHalfSportDataList){
                Map<String,Object> map = new HashMap<>();
                map.put("AccountId",userId);
                map.put("DeviceCode",deviceCode);
                map.put("Step",cusVPHalfSportData.getStepValue());
                map.put("TestTime",cusVPHalfSportData.getTime().getDateAndClockForSleepSecond());
                walkList.add(map);
            }
            //Log.e(TAG,"--------步数="+walkUrl+"--="+gson.toJson(walkList));
            nohttpUtils.getModelRequestJSONObject(0x01,walkUrl,gson.toJson(walkList),objectOnResponseListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //上传账号和设备码
    private void uploadKmData() {
        String url = KmConstance.uploadAccountDevice();

        Map<String,Object> map = new HashMap<>();
        map.put("AccountId",userId);
        map.put("DeviceCode",deviceCode);
        Map<String,Object> accountMap = new HashMap<>();
        accountMap.put("accountDevices",gson.toJson(map));
        Log.e(TAG,"-----map="+url+"---="+accountMap.toString());
        nohttpUtils.getModelRequestJSONObject(RequestMethod.GET,0x00, url, accountMap, objectOnResponseListener);

    }



    private OnResponseListener<JSONObject> objectOnResponseListener =new OnResponseListener<JSONObject>() {

        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            //Log.e(TAG,"---------what="+what+"--="+response.get().toString());

        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

}
