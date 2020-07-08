package com.example.bozhilun.android.b30.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPSleepData;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.commdbserver.detail.CommBloodDetailDb;
import com.example.bozhilun.android.commdbserver.detail.CommHeartDetailDb;
import com.example.bozhilun.android.commdbserver.detail.CommSleepDetailDb;
import com.example.bozhilun.android.commdbserver.detail.CommStepDetailDb;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.apache.commons.lang.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 上传好友详细数据的service,维亿魄系列使用，详细数据
 * Created by Admin
 * Date 2019/5/5
 */
public class FriendsUploadServices extends IntentService {

    private static final String TAG = "FriendsUploadServices";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);

    private Gson gson = new Gson();

    String currDayStr = WatchUtils.getCurrentDate();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param
     */
    public FriendsUploadServices() {
        super("FriendsUploadServices");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());
        Log.e(TAG, "---------启动详细数据上传-----" + bleMac);
        if (WatchUtils.isEmpty(bleMac))
            return;
        if (WatchUtils.isEmpty(userId))
            return;

        //上传当天的详细数据
        uploadTodayDatas(bleMac);

        //上传昨天和前天的详细步数数据
        uploadStepDetailByDay(bleMac, B30HalfHourDao.TYPE_SPORT, 1);
        //上传昨天和前天的详细睡眠数据
        uploadSleepDetailByDay(bleMac, B30HalfHourDao.TYPE_SLEEP, 0);
        //上传心率详细数据
        uploadHeartDetailByDay(bleMac, B30HalfHourDao.TYPE_RATE, 1);
        //上传血压的详细数据
        uploadBloodDetailByDay(bleMac, B30HalfHourDao.TYPE_BP, 1);
    }

    //上传当天的数据
    private void uploadTodayDatas(String bleMac){
        uploadStepDetailToday(bleMac);  //当天步数
        uploadHeartDetailToDay(bleMac);   //上传当天的心率数据
        uploadSleepDetailToday(bleMac);  //当天睡眠详细数据
        uploadBloodDetailToday(bleMac);  //上传当天血压睡眠数据
    }


    //上传当天的步数详细数据
    private void uploadStepDetailToday(final String bleMac) {
        //Log.e(TAG, "----------上传今天的数据---");
        List<B30HalfHourDB> stepDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac,
                B30HalfHourDao.TYPE_SPORT, WatchUtils.getCurrentDate());
        if (stepDayList != null) {
            B30HalfHourDB b30HalfHourDB = stepDayList.get(0);
            List<CusVPHalfSportData> cusVPHalfSportDataList = gson.fromJson(b30HalfHourDB.getOriginData(),
                    new TypeToken<List<CusVPHalfSportData>>(){}.getType());

            List<CommStepDetailDb> upStepList = new ArrayList<>();

            //计算累计的总步数和总卡路里，里程
            int countStep = 0;
            double countKcal = 0.0;
            double countDis = 0.0;

            //目标步数
            int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 8000);


            for (CusVPHalfSportData stepLt : cusVPHalfSportDataList) {
                countStep += stepLt.getStepValue();
                countDis += stepLt.getDisValue();
                countKcal += stepLt.getCalValue();
                CommStepDetailDb commStepDetailDb = new CommStepDetailDb();
                commStepDetailDb.setUserid(userId);
                commStepDetailDb.setDevicecode(bleMac);
                commStepDetailDb.setRtc(currDayStr);
                commStepDetailDb.setStepnumber(stepLt.stepValue);
                commStepDetailDb.setDistance(stepLt.getDisValue() + "");
                //kcal是int类型
                commStepDetailDb.setCalories(StringUtils.substringBefore(String.valueOf(stepLt.getCalValue()), ".") + "");
                commStepDetailDb.setStartdate(stepLt.getTime().getColck());
                commStepDetailDb.setEnddate("11");
                commStepDetailDb.setSpeed(1);
                commStepDetailDb.setAction(1);
                commStepDetailDb.setStatus(1);
                upStepList.add(commStepDetailDb);

            }

            //汇总的数据
            List<Map<String,String>> countList = new ArrayList<>();
            Map<String, String> params = new HashMap<>();
            params.put("userid", userId);
            params.put("stepnumber", countStep+"");
            params.put("date", currDayStr);
            params.put("devicecode", bleMac);
            params.put("count", "111");
            params.put("distance",countDis+"");
            params.put("calorie",countKcal+"");
            params.put("reach",(goalStep<=countStep?1:0)+"");
            countList.add(params);

            Log.e(TAG,"-------当天汇总步数="+gson.toJson(countList));
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadCountStepUrl(), gson.toJson(countList), "55", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    //Log.e(TAG, "-------上传是否达标=" + result);
                }
            });

            //详细步数的上传参数
            List<Map<String, Object>> jsonObjectList = new ArrayList<>();
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("deviceCode", bleMac);
            jsonObject.put("rtc", currDayStr);
            jsonObject.put("userId", userId);
            jsonObject.put("stepNumberList", upStepList);
            jsonObjectList.add(jsonObject);
            String jsonStr = gson.toJson(jsonObjectList);
            // Log.e(TAG, "-----当天步数详细数据jsonStr=" + jsonStr);
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailStepUrl(), jsonStr, "11",
                    new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {
                            //Log.e(TAG, "--------步数当天详细数据上传=" + result);


                        }
                    });


        }
    }


    //上传当天心率的详细数据
    private void uploadHeartDetailToDay(final String bleMac) {
        List<CommHeartDetailDb> commHeartDetailDbsLt = new ArrayList<>();
        List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, B30HalfHourDao.TYPE_RATE, currDayStr);
        if (heartDayList == null)
            return;
        B30HalfHourDB b30HalfHourDB = heartDayList.get(0);

        List<CusVPHalfRateData> cusRateData = gson.fromJson(b30HalfHourDB.getOriginData(),new TypeToken<List<CusVPHalfRateData>>() {
        }.getType());

        if (cusRateData == null || cusRateData.isEmpty()) {
            uploadSleepDetailToday(bleMac);
            return;
        }
        for (CusVPHalfRateData hourRateData : cusRateData) {
            CommHeartDetailDb commHeartDetailDb = new CommHeartDetailDb();
            commHeartDetailDb.setUserid(userId);
            commHeartDetailDb.setDevicecode(bleMac);
            commHeartDetailDb.setHeartrate(hourRateData.getRateValue());
            commHeartDetailDb.setStatus(0); //0为自动测量，1为手动测量
            commHeartDetailDb.setRtc(currDayStr);
            commHeartDetailDb.setTime(hourRateData.getTime().getColck());
            commHeartDetailDbsLt.add(commHeartDetailDb);
        }


        //详细心率的上传参数
        List<Map<String,Object>> hetLt = new ArrayList<>();
        Map<String, Object> detailHeartMap = new HashMap<>();
        detailHeartMap.put("deviceCode", bleMac);
        detailHeartMap.put("rtc", currDayStr);
        detailHeartMap.put("userId", userId);
        detailHeartMap.put("heartRateList", commHeartDetailDbsLt);
        hetLt.add(detailHeartMap);
        String jsonStr = gson.toJson(hetLt);
        //Log.e(TAG,"-------jsonStr="+jsonStr);
        OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailHeartUrl(), jsonStr, "33", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                //Log.e(TAG, "-----------上传心率详细数据返回=" + result);

            }
        });

    }


    //上传当天睡眠的详细数据
    private void uploadSleepDetailToday(final String bleMac) {
        /**
         *维亿魄系列手环详细的数据保存数据库时是往后+了一天，为何IOS统一，上传时再往后减一天
         */
        String dayStr = WatchUtils.getCurrentDate();
        List<B30HalfHourDB> sleepDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, B30HalfHourDao.TYPE_SLEEP, dayStr);
        String uploadDayStr = WatchUtils.obtainAroundDate(dayStr, true);
        if (sleepDayList != null) {
            B30HalfHourDB b30HalfHourDB = sleepDayList.get(0);
            List<CommSleepDetailDb> commSleepDetailDbList = new ArrayList<>();
            try {
                //睡眠一天只有一条数据
                CusVPSleepData sleepData = gson.fromJson(b30HalfHourDB.getOriginData(), CusVPSleepData.class);
                //睡眠的表现形式
                String sleepStr = sleepData.getSleepLine();
                Log.e(TAG, "-------sleepStr=" + sleepStr);
                //入睡时间
                String startSleepDate = sleepData.getSleepDown().getDateAndClockForSleepSecond();
                long longStartDate = sdf.parse(startSleepDate).getTime() / 1000;
                /**
                 * 012 0-浅睡；1-深睡；2-清醒
                 */
                for (int i = 0; i < sleepStr.length(); i++) {
                    CommSleepDetailDb commSleepDetailDb = new CommSleepDetailDb();
                    commSleepDetailDb.setDay(uploadDayStr);
                    commSleepDetailDb.setDevicecode(bleMac);
                    commSleepDetailDb.setUserid(userId);
                    String slType = sleepStr.charAt(i) + "";
                   // Log.e(TAG, "----slType=" + slType);
                    int changeType = Integer.valueOf(slType);
                    int resultType = 0;
                    switch (changeType) {
                        case 0:
                            resultType = 2;
                            break;
                        case 1:
                            resultType = 3;
                            break;
                        case 2:
                            resultType = 1;
                            break;
                    }
                    //Log.e(TAG, "-------转换后的数据=" + resultType);
                    commSleepDetailDb.setSleepType(resultType + "");
                    //时间
                    commSleepDetailDb.setStarttime(WatchUtils.getLongToDate("HH:mm", (longStartDate + (5 * i * 60)) * 1000));
                    //Log.e(TAG, "---------commSleepDetailDb=" + commSleepDetailDb.toString());
                    commSleepDetailDbList.add(commSleepDetailDb);

                }


                //详细睡眠的上传参数
                List<Map<String,Object>> slLt = new ArrayList<>();
                Map<String, Object> detailSleepMap = new HashMap<>();
                detailSleepMap.put("deviceCode", bleMac);
                detailSleepMap.put("rtc", uploadDayStr);
                detailSleepMap.put("userId", userId);
                detailSleepMap.put("sleepSlotList", commSleepDetailDbList);
                slLt.add(detailSleepMap);

                String jsonStr = gson.toJson(slLt);
               // Log.e(TAG, "-----当天睡眠详细数据参数jsonStr=" + jsonStr);
                OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailSleepUrl(), jsonStr, "22",
                        new OkHttpTool.HttpResult() {
                            @Override
                            public void onResult(String result) {
                               // Log.e(TAG, "--------睡眠详细数据上传=" + result);

                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //上传当天血压的详细数据，不做限制，可上传多次
    private void uploadBloodDetailToday(final String bleMac) {
        List<CommBloodDetailDb> commBloodDetailDbList = new ArrayList<>();
        String dayStr = WatchUtils.getCurrentDate();
        List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, B30HalfHourDao.TYPE_BP, dayStr);
        if (heartDayList == null){
            //Log.e(TAG,"------上传当天血压详细数据为null了--");
            return;
        }

        B30HalfHourDB b30HalfHourDB = heartDayList.get(0);
        List<CusVPHalfHourBpData> bpData = gson.fromJson(b30HalfHourDB.getOriginData(), new TypeToken<List<CusVPHalfHourBpData>>() {
        }.getType());

        for (CusVPHalfHourBpData hourBpData : bpData) {
            CommBloodDetailDb commBloodDetailDb = new CommBloodDetailDb();
            commBloodDetailDb.setUserid(userId);
            commBloodDetailDb.setRtc(dayStr);
            commBloodDetailDb.setTime(hourBpData.getTime().getColck());
            commBloodDetailDb.setDiastolic(hourBpData.getLowValue()); //舒张压，低压
            commBloodDetailDb.setSystolic(hourBpData.getHighValue());
            commBloodDetailDb.setDevicecode(bleMac);
            commBloodDetailDbList.add(commBloodDetailDb);
        }


        //详细血压的上传参数
        List<Map<String,Object>> bpLt = new ArrayList<>();
        Map<String, Object> detailBppMap = new HashMap<>();
        detailBppMap.put("deviceCode", bleMac);
        detailBppMap.put("rtc", dayStr);
        detailBppMap.put("userId", userId);
        detailBppMap.put("bloodPressureList", commBloodDetailDbList);
        bpLt.add(detailBppMap);

        String jsonStr = gson.toJson(bpLt);
        OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailBloodUrl(), jsonStr, "44", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                //Log.e(TAG, "-----------上传血压详细数据返回=" + result);

            }
        });

    }


    //上传步数的详细数据
    private void uploadStepDetailByDay(final String bleMac, final String type, final int position) {
        if (position == 3)
            return;
        String dayStr = WatchUtils.obtainFormatDate(position);
        List<B30HalfHourDB> stepDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, type, dayStr);

        if (stepDayList != null) {
            B30HalfHourDB b30HalfHourDB = stepDayList.get(0);
            //Log.e(TAG, "-------上传的标识=" + b30HalfHourDB.getUpload());
            //未上传的
            if (b30HalfHourDB.getUpload() == 0) { //0是未上传的
                List<CusVPHalfSportData> halfHourSportDataList = gson.fromJson(b30HalfHourDB.getOriginData(),
                        new TypeToken<List<CusVPHalfSportData>>(){}.getType());
                List<CommStepDetailDb> upStepList = new ArrayList<>();
                for (CusVPHalfSportData stepLt : halfHourSportDataList) {
                    CommStepDetailDb commStepDetailDb = new CommStepDetailDb();
                    commStepDetailDb.setUserid(userId);
                    commStepDetailDb.setDevicecode(bleMac);
                    commStepDetailDb.setRtc(dayStr);
                    commStepDetailDb.setStepnumber(stepLt.stepValue);
                    commStepDetailDb.setDistance(stepLt.getDisValue() + "");
                    commStepDetailDb.setCalories(StringUtils.substringBefore(String.valueOf(stepLt.getCalValue()), ".") + "");
                    commStepDetailDb.setStartdate(stepLt.getTime().getColck());
                    commStepDetailDb.setEnddate("11");
                    commStepDetailDb.setSpeed(1);
                    commStepDetailDb.setAction(1);
                    commStepDetailDb.setStatus(1);
                    upStepList.add(commStepDetailDb);
                }

                //详细步数的上传参数
                List<Map<String,Object>> sleepLt = new ArrayList<>();
                Map<String, Object> detailStepMap = new HashMap<>();
                detailStepMap.put("deviceCode", bleMac);
                detailStepMap.put("rtc", dayStr);
                detailStepMap.put("userId", userId);
                detailStepMap.put("stepNumberList", upStepList);
                sleepLt.add(detailStepMap);

                String jsonStr = gson.toJson(sleepLt);
                //Log.e(TAG, "-----jsonStr=" + jsonStr);
                OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailStepUrl(), jsonStr, "11",
                        new OkHttpTool.HttpResult() {
                            @Override
                            public void onResult(String result) {
                                //Log.e(TAG, "--------步数详细数据上传=" + position + "----" + result);
                                if (WatchUtils.isNetRequestSuccess(result)) { //上传成功
                                    updateStepDetailStatus(bleMac, type, position);
                                }

                            }
                        });
            }

        }


    }


    //步数上传成功后修改状态
    private void updateStepDetailStatus(String mac, String type, int posi) {
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        String whereStr = "address = ? and date = ? and type = ?";
        String day = WatchUtils.obtainFormatDate(posi);
        b30HalfHourDB.setUpload(1);
        boolean isSave = b30HalfHourDB.saveOrUpdate(whereStr, mac, day, type);
        //Log.e(TAG, "-------修改步数返回=" + isSave);

        //后一天
        int currPosition = posi + 1;
        uploadStepDetailByDay(mac, type, currPosition);

    }


    //上传睡眠的详细数据
    private void uploadSleepDetailByDay(final String bleMac, final String type, final int position) {
        try {
            if (position == 2)
                return;
            String dayStr = WatchUtils.obtainFormatDate(position);
            //往前推一天
            String upDayStr = WatchUtils.obtainAroundDate(dayStr, true);
            String  sleepDataStr = B30HalfHourDao.getInstance().findOriginData(bleMac, upDayStr, type);
            if(sleepDataStr == null)
                return;
            CusVPSleepData sleepData = gson.fromJson(sleepDataStr,CusVPSleepData.class);
            if(sleepData == null)
                return;
            List<CommSleepDetailDb> commSleepDetailDbList = new ArrayList<>();
            //睡眠的表现形式
            String sleepStr = sleepData.getSleepLine();
            //入睡时间
            String startSleepDate = sleepData.getSleepDown().getDateAndClockForSleepSecond();
            long longStartDate = sdf.parse(startSleepDate).getTime() / 1000;
            /**
             * 012 0-浅睡；1-深睡；2-清醒
             */
            for (int i = 0; i < sleepStr.length(); i++) {
                CommSleepDetailDb commSleepDetailDb = new CommSleepDetailDb();
                commSleepDetailDb.setDay(upDayStr);
                commSleepDetailDb.setDevicecode(bleMac);
                commSleepDetailDb.setUserid(userId);
                String slType = sleepStr.charAt(i) + "";
                int changeType = Integer.valueOf(slType);
                int resultType = 0;
                switch (changeType) {
                    case 0:
                        resultType = 2;
                        break;
                    case 1:
                        resultType = 3;
                        break;
                    case 2:
                        resultType = 1;
                        break;
                }
                commSleepDetailDb.setSleepType(resultType + "");
                //时间
                commSleepDetailDb.setStarttime(WatchUtils.getLongToDate("HH:mm", (longStartDate + (5 * i * 60)) * 1000));
                //Log.e(TAG, "---------commSleepDetailDb=" + commSleepDetailDb.toString());
                commSleepDetailDbList.add(commSleepDetailDb);

            }

            //详细睡眠的上传参数
            List<Map<String,Object>> sleepLt = new ArrayList<>();
            Map<String, Object> detailSleepMap = new HashMap<>();
            detailSleepMap.put("deviceCode", bleMac);
            detailSleepMap.put("rtc", upDayStr);
            detailSleepMap.put("userId", userId);
            detailSleepMap.put("sleepSlotList", commSleepDetailDbList);
            sleepLt.add(detailSleepMap);
            String jsonStr = gson.toJson(sleepLt);
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailSleepUrl(), jsonStr, "22",
                    new OkHttpTool.HttpResult() {
                        @Override
                        public void onResult(String result) {
                            Log.e(TAG, "--------睡眠详细数据上传=" + result);
                            //上传成功后修改是否上传的标识
                            updateSleepDetailStatus(bleMac, type, position);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //睡眠上传成功后修改数据
    private void updateSleepDetailStatus(String bleMac, String type, int position) {
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        String whereStr = "address = ? and date = ? and type = ?";
        String day = WatchUtils.obtainFormatDate(position);
        b30HalfHourDB.setUpload(1);
        boolean isSave = b30HalfHourDB.saveOrUpdate(whereStr, bleMac, day, type);
        //Log.e(TAG, "-------修改步数返回=" + isSave);

        int currPosition = position + 1;
        uploadSleepDetailByDay(bleMac, type, currPosition);

    }


    //上传心率的详细数据
    private void uploadHeartDetailByDay(final String bleMac, final String type, final int position) {
        if (position == 3)
            return;
        List<CommHeartDetailDb> commHeartDetailDbsLt = new ArrayList<>();
        String dayStr = WatchUtils.obtainFormatDate(position);
        List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, type, dayStr);
        if (heartDayList == null)
            return;
        B30HalfHourDB b30HalfHourDB = heartDayList.get(0);
        if (b30HalfHourDB.getUpload() == 0) {
            List<CusVPHalfRateData> hourRateDataList = gson.fromJson(b30HalfHourDB.getOriginData(),new TypeToken<List<CusVPHalfRateData>>() {
            }.getType());

            for (CusVPHalfRateData hourRateData : hourRateDataList) {
                CommHeartDetailDb commHeartDetailDb = new CommHeartDetailDb();
                commHeartDetailDb.setUserid(userId);
                commHeartDetailDb.setDevicecode(bleMac);
                commHeartDetailDb.setHeartrate(hourRateData.getRateValue());
                commHeartDetailDb.setStatus(0); //0为自动测量，1为手动测量
                commHeartDetailDb.setRtc(dayStr);
                commHeartDetailDb.setTime(hourRateData.getTime().getColck());
                commHeartDetailDbsLt.add(commHeartDetailDb);
            }

            //详细心率的上传参数
            List<Map<String,Object>> hertLt = new ArrayList<>();
            Map<String, Object> detailHeartMap = new HashMap<>();
            detailHeartMap.put("deviceCode", bleMac);
            detailHeartMap.put("rtc", dayStr);
            detailHeartMap.put("userId", userId);
            detailHeartMap.put("heartRateList", commHeartDetailDbsLt);
            hertLt.add(detailHeartMap);
            String jsonStr = gson.toJson(hertLt);
            //Log.e(TAG,"-------jsonStr="+jsonStr);
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailHeartUrl(), jsonStr, "33", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    //Log.e(TAG, "-----------上传心率详细数据返回=" + result);
                    if (WatchUtils.isNetRequestSuccess(result))
                        updateHeartDetailStatus(bleMac, type, position);
                }
            });


        }

    }

    //心率详细数据上传成功后修改数据
    private void updateHeartDetailStatus(String bleMac, String type, int position) {
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        String whereStr = "address = ? and date = ? and type = ?";
        String day = WatchUtils.obtainFormatDate(position);
        b30HalfHourDB.setUpload(1);
        boolean isSave = b30HalfHourDB.saveOrUpdate(whereStr, bleMac, day, type);
        //Log.e(TAG, "-------修改心率返回=" + isSave);

        int currPosition = position + 1;
        uploadHeartDetailByDay(bleMac, type, currPosition);

    }


    //上传血压的详细数据
    private void uploadBloodDetailByDay(final String bleMac, final String type, final int position) {
        if (position == 3)
            return;
        List<CommBloodDetailDb> commBloodDetailDbList = new ArrayList<>();
        String dayStr = WatchUtils.obtainFormatDate(position);
        List<B30HalfHourDB> heartDayList = B30HalfHourDao.getInstance().findNotUpDataByDay(bleMac, type, dayStr);
        if (heartDayList == null)
            return;
        B30HalfHourDB b30HalfHourDB = heartDayList.get(0);
        if (b30HalfHourDB.getUpload() == 0) {

            List<CusVPHalfHourBpData> bpData = gson.fromJson(b30HalfHourDB.getOriginData(), new TypeToken<List<CusVPHalfHourBpData>>() {
            }.getType());

            for (CusVPHalfHourBpData hourBpData : bpData) {
                CommBloodDetailDb commBloodDetailDb = new CommBloodDetailDb();
                commBloodDetailDb.setUserid(userId);
                commBloodDetailDb.setRtc(dayStr);
                commBloodDetailDb.setTime(hourBpData.getTime().getColck());
                commBloodDetailDb.setDiastolic(hourBpData.getLowValue()); //舒张压，低压
                commBloodDetailDb.setSystolic(hourBpData.getHighValue());
                commBloodDetailDb.setDevicecode(bleMac);
                commBloodDetailDbList.add(commBloodDetailDb);
            }


            //详细血压的上传参数
            List<Map<String,Object>> bpLt = new ArrayList<>();
            Map<String, Object> detailBppMap = new HashMap<>();
            detailBppMap.put("deviceCode", bleMac);
            detailBppMap.put("rtc", dayStr);
            detailBppMap.put("userId", userId);
            detailBppMap.put("bloodPressureList", commBloodDetailDbList);
            bpLt.add(detailBppMap);
            String jsonStr = gson.toJson(bpLt);
            //Log.e(TAG, "-------jsonStr=" + jsonStr);
            OkHttpTool.getInstance().doRequest(SyncDbUrls.uploadDetailBloodUrl(), jsonStr, "44", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    //Log.e(TAG, "-----------上传血压详细数据返回=" + result);
                    if (WatchUtils.isNetRequestSuccess(result))
                        updateBloodDetailStatus(bleMac, type, position);
                }
            });

        }
    }

    //上传血压成功后修改标识
    private void updateBloodDetailStatus(String bleMac, String type, int position) {
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        String whereStr = "address = ? and date = ? and type = ?";
        String day = WatchUtils.obtainFormatDate(position);
        b30HalfHourDB.setUpload(1);
        boolean isSave = b30HalfHourDB.saveOrUpdate(whereStr, bleMac, day, type);
        //Log.e(TAG, "-------修改血压返回=" + isSave);

        int currPosition = position + 1;
        uploadBloodDetailByDay(bleMac, type, currPosition);
    }

}
