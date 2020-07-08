package com.example.bozhilun.android.b31.bpoxy.uploadSpo2;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b31.model.B31HRVBean;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;

import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

/**
 * Created by Admin
 * Date 2019/8/22
 */
public class UploadSpo2AndHrvService extends IntentService  {

    private static final String TAG = "UploadSpo2AndHrvService";

    String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.USER_ID_DATA);
    String currDay = WatchUtils.getCurrentDate();

    private Gson gson = new Gson();

    String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:  //上传血氧的数据
                    ConcurrentMap concurrentMap = (ConcurrentMap) msg.obj;
                    List<UploadSpo2Bean> uploadSpo2BeanList = (List<UploadSpo2Bean>) concurrentMap.get("spo2Detail");
                    if(uploadSpo2BeanList == null)
                        return;
                   // List<UploadSpo2Bean> uploadSpo2BeanList = (List<UploadSpo2Bean>) msg.obj;
                    Map<String,Object> map = new HashMap<>();
                    map.put("list",uploadSpo2BeanList);
                    String uploadStr = gson.toJson(map);
                    Log.e(TAG,"-----uploadStr=");

                    String url = SyncDbUrls.uploadBloodOxyUrl();
                    OkHttpTool.getInstance().doRequest(url, uploadStr, "", new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {

                        }
                    });

                    //汇总的
                    List<Spo2hOriginData> upSpo2List = (List<Spo2hOriginData>) concurrentMap.get("spo2Count");
                    if(upSpo2List == null)
                        return;
                    uploadDaySpo2Data(upSpo2List);

                    break;
                case 0x12:  //上传HRV
                    ConcurrentMap<String,Object> hrvConMap = (ConcurrentMap<String, Object>) msg.obj;
                    List<UploadHrvBean> upHrvLt = (List<UploadHrvBean>) hrvConMap.get("hrvDetail");
                    if(upHrvLt == null || upHrvLt.isEmpty())
                        return;
                    Map<String,Object> hrvMap = new HashMap<>();
                    hrvMap.put("userId",userId);
                    hrvMap.put("date",currDay);
                    hrvMap.put("deviceCode",bleMac);
                    hrvMap.put("list",upHrvLt);
                    String hrvParams = gson.toJson(hrvMap);
                    OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadHrvDetailUrl(), hrvParams, "", new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {
                            Log.e(TAG,"-----上传HRV返回="+result);
                        }
                    });

                    List<HRVOriginData> hrvt = (List<HRVOriginData>) hrvConMap.get("hrvCount");
                    if(hrvt == null || hrvt.isEmpty())
                        return;
                    uploadDayHrvData(hrvt);
                    break;

            }
        }
    };



    private void uploadDayHrvData(List<HRVOriginData> hrvt){
        if(hrvt.size() == 0)return;
        try {
            HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
            int heartSocre = hrvScoreUtil.getSocre(hrvt);
            List<Map<String,String>> ltMap = new ArrayList<>();
            Map<String,String> hrvMap = new HashMap<>();
            hrvMap.put("userId",userId);
            hrvMap.put("deviceCode",bleMac);
            hrvMap.put("day",currDay);
            hrvMap.put("heartSocre",heartSocre+"");
            ltMap.add(hrvMap);
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("list",ltMap);
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadTodayCountHrv(), gson.toJson(resultMap), "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e(TAG,"-------上传当天hrv="+result);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //上传血氧的汇总数据
    private void uploadDaySpo2Data(List<Spo2hOriginData> spt) {
        if(spt.size() == 0)
            return;
        try {
            //处理血氧数据的工具类
            Spo2hOriginUtil spo2hOriginUtil = new Spo2hOriginUtil(spt);
            //获取血氧数据[最大，最小，平均]       *参考：取血氧最小值，低于95，则显示偏低，其他显示正常
            int[] onedayDataArrSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_SPO2H);


            String urlStr = SyncDbUrls.uploadTodayCountSpo2();

            List<Map<String,String>> mapList = new ArrayList<>();


            Map<String,String> pms = new HashMap<>();
            pms.put("userid",userId);
            pms.put("devicecode",bleMac);
            pms.put("rtc",currDay);
            pms.put("avgbloodoxygen",onedayDataArrSpo2h[2]+"");
            pms.put("minbloodoxygen",onedayDataArrSpo2h[1]+"");
            pms.put("maxbloodoxygen",onedayDataArrSpo2h[0]+"");
            mapList.add(pms);

            Map<String,Object> objMap = new HashMap<>();
            objMap.put("list",mapList);

            OkHttpTool.getInstance().doRequest(urlStr, gson.toJson(objMap), "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e(TAG,"------汇总spo2上传返回="+result);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public UploadSpo2AndHrvService() {
        super("UploadSpo2AndHrvService");
    }

    public UploadSpo2AndHrvService(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(WatchUtils.isEmpty(userId))
            return;
        if(bleMac == null)
            return;
        if(userId.equals("9278cc399ab147d0ad3ef164ca156bf0"))
            return;
        uploadSpo2Data();

        uploadHrvData();
    }

    //上传HRV
    private void uploadHrvData() {
        String where = "bleMac = ? and dateStr = ?";
        List<B31HRVBean> currHrvLt = LitePal.where(where,
                bleMac, currDay).find(B31HRVBean.class);
        if(currHrvLt == null || currHrvLt.isEmpty())
            return;
        List<UploadHrvBean> uploadHrvBeanList = new ArrayList<>();
        List<HRVOriginData> upHrvLt = new ArrayList<>();

        ConcurrentMap<String,Object> hrvMap = new ConcurrentHashMap<>();

        for(B31HRVBean b31HRVBean : currHrvLt){
            HRVOriginData hrvOriginData = gson.fromJson(b31HRVBean.getHrvDataStr(),HRVOriginData.class);
            upHrvLt.add(hrvOriginData);
            UploadHrvBean uploadHrvBean = new UploadHrvBean();
            uploadHrvBean.setAllCurrentPackNumber(hrvOriginData.getAllCurrentPackNumber());
            uploadHrvBean.setCurrentPackNumber(hrvOriginData.getCurrentPackNumber());
            uploadHrvBean.setMTime(hrvOriginData.getmTime().getDateAndClockForSleepSecond());
            uploadHrvBean.setHrvValue(hrvOriginData.getHrvValue()+"");
            uploadHrvBean.setTemp1(hrvOriginData.getTempOne()+"");
            uploadHrvBean.setUserId(userId);
            uploadHrvBean.setDate(hrvOriginData.getDate());
            uploadHrvBean.setDeviceCode(bleMac);
            uploadHrvBean.setTime(hrvOriginData.getmTime().getColck());
            String hts = hrvOriginData.getRate();
            uploadHrvBean.setHearts(hts.replaceAll(",",":"));
            uploadHrvBeanList.add(uploadHrvBean);

        }

        hrvMap.put("hrvCount",upHrvLt);
        hrvMap.put("hrvDetail",uploadHrvBeanList);

        Message message = handler.obtainMessage();
        message.obj = hrvMap;
        message.what = 0x12;
        handler.sendMessage(message);

    }


    //上传血氧
    private void uploadSpo2Data() {
        //当天的血氧数据
        String where = "bleMac = ? and dateStr = ?";
        final String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());
        final String currDayStr = WatchUtils.getCurrentDate();
        final String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),Commont.USER_ID_DATA);
        final List<B31Spo2hBean> currList = LitePal.where(where,bleMac,
                currDayStr).find(B31Spo2hBean.class);
        if(currList == null || currList.isEmpty())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<UploadSpo2Bean>  upList = new ArrayList<>();
                List<Spo2hOriginData> spo2List = new ArrayList<>();
                ConcurrentMap<String,Object> spo2Map = new ConcurrentHashMap<>();


                for(B31Spo2hBean b31Spo2hBean : currList){
                    Spo2hOriginData spo2hOriginData = gson.fromJson(b31Spo2hBean.getSpo2hOriginData(),Spo2hOriginData.class);
                    spo2List.add(spo2hOriginData);
                    UploadSpo2Bean uploadSpo2Bean = new UploadSpo2Bean();
                    uploadSpo2Bean.setUserId(userId);
                    uploadSpo2Bean.setMac(bleMac);
                    uploadSpo2Bean.setDate(currDayStr);
                    uploadSpo2Bean.setMTime(spo2hOriginData.getmTime().getDateAndClockForSleepSecond());
                    uploadSpo2Bean.setWeekday(0);
                    uploadSpo2Bean.setHeartValue(spo2hOriginData.getHeartValue());
                    uploadSpo2Bean.setSportValue(spo2hOriginData.getSportValue());
                    uploadSpo2Bean.setOxygenValue(spo2hOriginData.getOxygenValue());
                    uploadSpo2Bean.setApneaResult(spo2hOriginData.getApneaResult());
                    uploadSpo2Bean.setIsHypoxia(spo2hOriginData.getIsHypoxia());
                    uploadSpo2Bean.setHypopnea(spo2hOriginData.getHypopnea());
                    uploadSpo2Bean.setHypoxiaTime(spo2hOriginData.getHypoxiaTime());
                    uploadSpo2Bean.setCardiacLoad(spo2hOriginData.getCardiacLoad());
                    uploadSpo2Bean.setHrVariation(spo2hOriginData.gethRVariation());
                    uploadSpo2Bean.setStepValue(spo2hOriginData.getStepValue());
                    uploadSpo2Bean.setRespirationRate(spo2hOriginData.getRespirationRate());
                    uploadSpo2Bean.setTemp1(spo2hOriginData.getTemp1());
                    uploadSpo2Bean.setAllPackNumner(spo2hOriginData.getAllPackNumner());
                    uploadSpo2Bean.setCurrentPackNumber(spo2hOriginData.getCurrentPackNumber());
                    uploadSpo2Bean.setHrv(spo2hOriginData.gethRVariation());
                    uploadSpo2Bean.setTime(spo2hOriginData.getmTime().getColck());

                    upList.add(uploadSpo2Bean);

                }

                spo2Map.put("spo2Count",spo2List);
                spo2Map.put("spo2Detail",upList);

                Message message = handler.obtainMessage();
                message.obj = spo2Map;
                message.what = 0x11;
                handler.sendMessage(message);

            }
        }).start();
    }

}
