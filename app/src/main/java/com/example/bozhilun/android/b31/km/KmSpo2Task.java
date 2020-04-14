package com.example.bozhilun.android.b31.km;

import android.os.AsyncTask;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import org.json.JSONObject;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下康美血氧和呼吸率的task
 * Created by Admin
 * Date 2019/12/13
 */
public class KmSpo2Task extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "KmSpo2Task";


    private String deviceCode = null;
    private String currDay = WatchUtils.getCurrentDate();
    private String userId = null;

    private Gson gson = new Gson();

    private NohttpUtils nohttpUtils;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        deviceCode = MyApp.getInstance().getMacAddress();
        userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
        nohttpUtils = NohttpUtils.getNoHttpUtils();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if(deviceCode == null || userId == null)
            return null;
        findSpo2Data(deviceCode);

        return null;
    }

    private void findSpo2Data(String deviceCode) {
        String where = "bleMac = ? and dateStr = ?";
        List<B31Spo2hBean> currList = LitePal.where(where,deviceCode,
                currDay).find(B31Spo2hBean.class);
        if(currList == null)
            return;
        //血氧
        List<Map<String,Object>> spo2List = new ArrayList<>();

        //呼吸率
        List<Map<String,Object>> breatheRateList = new ArrayList<>();


        for(B31Spo2hBean b31Spo2hBean : currList){
            Spo2hOriginData spo2hOriginData = gson.fromJson(b31Spo2hBean.getSpo2hOriginData(),Spo2hOriginData.class);
            Map<String,Object> map = new HashMap<>();
            map.put("AccountId",userId);
            map.put("DeviceCode",deviceCode);
            map.put("Bo",spo2hOriginData.getOxygenValue());
            map.put("TestTime",spo2hOriginData.getmTime().getDateAndClockForSleepSecond());
            spo2List.add(map);



            Map<String,Object> map2 = new HashMap<>();
            map2.put("AccountId",userId);
            map2.put("DeviceCode",deviceCode);
            map2.put("Value",spo2hOriginData.getRespirationRate());
            map2.put("TestTime",spo2hOriginData.getmTime().getDateAndClockForSleepSecond());
            breatheRateList.add(map2);


        }

        //上传血氧
        if(spo2List.size()>0){
            String spo2Url = KmConstance.uploadBloodOxygen();
            nohttpUtils.getModelRequestJSONObject(0x01,spo2Url,gson.toJson(spo2List),onResponseListener);
        }

        if(breatheRateList.size()>0){
            String breathRatesUrl = KmConstance.uploadSpo2BreathRates();
            nohttpUtils.getModelRequestJSONObject(0x02,breathRatesUrl,gson.toJson(breatheRateList),onResponseListener);

        }

    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(nohttpUtils != null)
            nohttpUtils.cancleHttpPost();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            //Log.e(TAG,"------what="+what+"---response="+response.get());
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };
}
