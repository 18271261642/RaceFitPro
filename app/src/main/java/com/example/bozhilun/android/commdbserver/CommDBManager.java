package com.example.bozhilun.android.commdbserver;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.service.FriendsUploadServices;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.litepal.LitePal;

import java.util.List;


/**
 * Created by Admin
 * Date 2019/3/4
 * 数据库的管理 汇总的数据  eg:每天的总步数
 */
public class CommDBManager {

    private static final String TAG = "CommDBManager";

    //步数
    public static final String COMM_TYPE_STEP = "comm_step_type";
    //睡眠
    public static String COMM_TYPE_SLEEP = "comm_sleep_type";
    //心率
    public static String COMM_TYPE_HEART = "comm_heart_type";
    //血压
    public static String COMM_TYPE_BLOOD = "comm_blood_type";
    //血氧
    public static String COMM_TYPE_BLOODOXY = "comm_blood_oxy_type";


    private volatile static CommDBManager commDBManager = null;
    private String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);


    private CommDBManager() {
    }

    public static CommDBManager getCommDBManager() {
        if (commDBManager == null) {
            synchronized (CommDBManager.class) {
                if (commDBManager == null)
                    commDBManager = new CommDBManager();
            }
        }
        return commDBManager;
    }

    private String stepWhereStr = "userid = ? and devicecode = ? and dateStr = ?";

    /**
     * 保存总步数,一天只有一天数据
     *
     * @param
     * @param bleMac    地址
     * @param dataStr   日期
     * @param countStep 总步数
     */
    public void saveCommCountStepDate(String bleName, String bleMac, String dataStr, int countStep) {
        if (WatchUtils.isEmpty(bleName) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(dataStr))
            return;
//        Log.e(TAG, "-----------参数=" + bleName + "-=" + bleMac + "-=" + dataStr + "-=" + countStep);
        //保存时先查询是否存在，若存在判断保存的步数是否大于现在的步数，防止重复
        CommStepCountDb lt = findCountStepForUpload(bleMac, dataStr);
        if (lt != null) { //有数据
            int saveStepNumber = lt.getStepnumber();
//            Log.e(TAG, "---------saveStepNumber=" + saveStepNumber+"---="+lt.isUpload());
            if (saveStepNumber < countStep) {
                CommStepCountDb commStepCountDb = new CommStepCountDb();
                commStepCountDb.setUserid(userId);
                commStepCountDb.setCount(1);
                commStepCountDb.setStepnumber(countStep);
                commStepCountDb.setDateStr(dataStr);
                commStepCountDb.setDevicecode(bleMac);
                commStepCountDb.setBleName(bleName);
                commStepCountDb.setUpload(lt.isUpload());
//                Log.e(TAG, "-------1111--保存=" + commStepCountDb.toString());
                //有就修改，没有就保存
                boolean isSave = commStepCountDb.saveOrUpdate(stepWhereStr, userId, bleMac, dataStr);
//                Log.e(TAG, "----11------isSave=" + isSave);
            }
        } else {
            //没有保存过的，直接保存
            CommStepCountDb commStepCountDb = new CommStepCountDb();
            commStepCountDb.setUserid(userId);
            commStepCountDb.setCount(1);
            commStepCountDb.setStepnumber(countStep);
            commStepCountDb.setDateStr(dataStr);
            commStepCountDb.setDevicecode(bleMac);
            commStepCountDb.setBleName(bleName);
            commStepCountDb.setUpload(false);
            //int currStep = LitePal.where(whereStr).limit(0).find(CommStepCountDb.class).get(0).getStepnumber();
            //有就修改，没有就保存
            boolean isSave = commStepCountDb.save();
//            Log.e(TAG, "-----22-----isSave=" + isSave);
        }


    }

    /**
     * 查询当天的总步数数据是否已经上传
     *
     * @param bleMac     蓝牙地址
     * @param currDayStr 指定的日期
     * @return
     */
    public boolean isCountStepUpload(String bleMac, String currDayStr) {
        CommStepCountDb countDb = findCountStepForUpload(bleMac, currDayStr);
        if (countDb == null)
            return false;
        return countDb.isUpload();
    }


    /**
     * 总步数保存所有的数据，用于从后台下载数据后全部保存
     *
     * @param commStepCountDbList
     */
    public void saveAllCommCountStepDate(List<CommStepCountDb> commStepCountDbList) {
        LitePal.saveAll(commStepCountDbList);
    }

    /**
     * 步数查询需要上传的数据,一天只有一条步数
     *
     * @param bMac
     * @param dateStr 指定天数
     * @return
     */
    public CommStepCountDb findCountStepForUpload(String bMac, String dateStr) {
        List<CommStepCountDb> commStepCountDbList = LitePal.where(stepWhereStr, userId, bMac, dateStr).find(CommStepCountDb.class);
        if (commStepCountDbList == null || commStepCountDbList.isEmpty()) {
//            Log.e(TAG, "-----------为null了------");
            return null;
        } else {
            return commStepCountDbList.get(0);
        }
    }

    /**
     * 步数查询开始和结束日期的数据
     *
     * @param bMac
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return
     */
    public List<CommStepCountDb> findCountStepForUpload(String bMac, String startDay, String endDay) {
        try {
            //        Log.e(TAG, "------start=" + startDay + "--end=" + endDay);
            String where = "userid = ? and devicecode = ? and dateStr  between ? and ?";
            List<CommStepCountDb> commStepCountDbList = LitePal.where(where, userId, bMac, startDay, endDay).find(CommStepCountDb.class);
            return commStepCountDbList == null || commStepCountDbList.isEmpty() ? null : commStepCountDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public List<CommStepCountDb> findAllSteps(String bleMac) {

        String where = "userid = ? and devicecode = ?";

        List<CommStepCountDb> commStepCountDbList = LitePal.where(where, userId, bleMac).find(CommStepCountDb.class);
        return commStepCountDbList == null || commStepCountDbList.isEmpty() ? null : commStepCountDbList;


    }


    public void updateCountStepData(CommStepCountDb commStepCountDb, boolean isUpload) {
        commStepCountDb.setUpload(isUpload);
        commStepCountDb.save();
    }


    //*******************心率数据 ************************************


    /**
     * 保存心率数据
     *
     * @param bleName  蓝牙名字
     * @param bleMac   蓝牙mac地址
     * @param dataStr  日期
     * @param avgHeart 平均心率
     */
    public void saveCommHeartData(String bleName, String bleMac, String dataStr, int maxHeart, int mineHeart, int avgHeart) {
        if (WatchUtils.isEmpty(bleName) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(dataStr))
            return;
//        Log.e(TAG, "----------心率=" + bleName + "-bleMac=" + bleMac + "--dateStr=" + dataStr + "--maxHeart=" + maxHeart + "--minHeart=" + mineHeart + "--avgHeart=" + avgHeart);
        CommHeartDb commHeartDb = new CommHeartDb();
        commHeartDb.setBleName(bleName);
        commHeartDb.setUserid(userId);
        commHeartDb.setDevicecode(bleMac);
        commHeartDb.setAvgheartrate(avgHeart);
        commHeartDb.setDateStr(dataStr);
        commHeartDb.setMaxheartrate(maxHeart);
        commHeartDb.setMinheartrate(mineHeart);
        //先查询一下
        List<CommHeartDb> saveHeartListData = findCommHeartForUpload(bleMac, dataStr);
        if (saveHeartListData == null) {
            commHeartDb.setUpload(false);
            boolean isHeartSave = commHeartDb.save();
//            Log.e(TAG, "-----11---心率保存=" + isHeartSave);
        } else {
            commHeartDb.setUpload(saveHeartListData.get(0).isUpload());
            boolean isSave = commHeartDb.saveOrUpdate(stepWhereStr, userId, bleMac, dataStr);

//            Log.e(TAG, "----22----心率保存=" + isSave);

        }

    }


    /**
     * 批量保存心率数据
     *
     * @param heartList 心率的集合
     */
    public void saveAllCommHeartData(List<CommHeartDb> heartList) {
        LitePal.saveAll(heartList);
    }


    /**
     * 查询指定日期的心率数据,一天只保存了一条
     *
     * @param bMac    蓝牙mac地址
     * @param dateStr 指定的日期
     * @return
     */
    public List<CommHeartDb> findCommHeartForUpload(String bMac, String dateStr) {
        try {
            List<CommHeartDb> commHeartDbList = LitePal.where("userid = ? and devicecode = ? and dateStr = ?", userId, bMac, dateStr).find(CommHeartDb.class);
            //List<CommHeartDb> commHeartDbList = LitePal.findAll(CommHeartDb.class);

            return commHeartDbList == null || commHeartDbList.isEmpty() ? null : commHeartDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查询开始和结束日期期间内的心率数据
     *
     * @param bMac     蓝牙mac地址
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return
     */
    public List<CommHeartDb> findCommHeartForUpload(String bMac, String startDay, String endDay) {
        try {
            String where = "devicecode = ? and dateStr  between ? and ?";
            List<CommHeartDb> commHeartDbList = LitePal.where(where, bMac, startDay, endDay).find(CommHeartDb.class);
            return commHeartDbList == null || commHeartDbList.isEmpty() ? null : commHeartDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /****************睡眠********************/

    /**
     * 保存睡眠数据
     *
     * @param bleName   蓝牙名字
     * @param bleMac    蓝牙mac地址
     * @param dateStr   日期
     * @param deep      深睡时长
     * @param low       浅睡时长
     * @param sober     清醒时长
     * @param allSleep  所有睡眠时间
     * @param sleeptime 入睡时间
     * @param waketime  清醒时间
     * @param wakeCount 清醒次数
     */
    public synchronized void saveCommSleepDbData(String bleName, String bleMac, String dateStr, int deep,
                                    int low, int sober, int allSleep, String sleeptime, String waketime, int wakeCount) {
        if (WatchUtils.isEmpty(bleName) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(dateStr))
            return;
        CommSleepDb commSleepDb = new CommSleepDb();
        //先查询一下睡眠
        List<CommSleepDb> saveSleepList = findCommSleepForUpload(bleMac, dateStr);

        commSleepDb.setBleName(bleName);
        commSleepDb.setDevicecode(bleMac);
        commSleepDb.setDateStr(dateStr);
        commSleepDb.setUserid(userId);
        commSleepDb.setDeepsleep(deep);
        commSleepDb.setShallowsleep(low);   //浅睡
        commSleepDb.setSleeplen(allSleep);  //睡眠总时长
        commSleepDb.setSoberlen(sober);  //清醒时时长
        commSleepDb.setSleeptime(sleeptime);    //入睡时间
        commSleepDb.setWaketime(waketime);  //清醒时间
        commSleepDb.setWakecount(wakeCount);    //清醒次数

        if (saveSleepList == null) {
            commSleepDb.setUpload(false);
//            Log.e(TAG, "-----11----睡眠数据----=====" + commSleepDb.toString());
            boolean isSleepSave = commSleepDb.save();
//            Log.e(TAG, "-----11----睡眠保存=" + isSleepSave);
        } else {
            commSleepDb.setUpload(saveSleepList.get(0).isUpload());
//            Log.e(TAG, "------22---睡眠数据----=====" + commSleepDb.toString());
            boolean isSleepSave = commSleepDb.saveOrUpdate("userid = ? and devicecode = ? and dateStr = ?", userId, bleMac, dateStr);
//            Log.e(TAG, "------22---睡眠保存=" + isSleepSave);
        }

    }

    /**
     * 批量保存睡眠数据
     *
     * @param sleepDbList
     */
    public void saveAllCommSleepData(List<CommSleepDb> sleepDbList) {
        LitePal.saveAll(sleepDbList);
    }

    /**
     * 查询指定日期的睡眠数据
     *
     * @param bMac
     * @param dateStr
     * @return
     */
    public synchronized List<CommSleepDb> findCommSleepForUpload(String bMac, String dateStr) {
        try {
            List<CommSleepDb> commSleepDbList = LitePal.where("userid = ? and devicecode = ? and dateStr = ?", userId, bMac, dateStr).find(CommSleepDb.class);
            return commSleepDbList == null || commSleepDbList.isEmpty() ? null : commSleepDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 查询时间段的睡眠数据
     *
     * @param bMac
     * @param
     * @param
     * @return
     */
    public List<CommSleepDb> findCommSleepForUpload(String bMac, String startDay, String endDay) {
        try {
            String where = "devicecode = ? and dateStr  between ? and ?";
            List<CommSleepDb> commSleepDbList = LitePal.where("userid = ? and devicecode = ? and dateStr between ? and ?", userId, bMac, startDay, endDay).find(CommSleepDb.class);

            return commSleepDbList == null || commSleepDbList.isEmpty() ? null : commSleepDbList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /*********************血压****************/

    /**
     * 保存血压数据
     *
     * @param bleMac       地址
     * @param dateStr      日期
     * @param maxDiastolic 高压
     * @param minSystolic  低压
     * @param avgDiastolic 平均的高压
     * @param avgSystolic  平均的低压
     */
    public void saveCommBloodDb(String bleMac, String dateStr, int maxDiastolic, int minSystolic, int avgDiastolic, int avgSystolic) {
        if (WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(dateStr))
            return;
        try {
            CommBloodDb commBloodDb = new CommBloodDb();
            commBloodDb.setUserid(userId);
            commBloodDb.setRtc(dateStr);
            commBloodDb.setDevicecode(bleMac);
            commBloodDb.setMaxdiastolic(maxDiastolic); //舒张压
            commBloodDb.setMaxdiastolic(minSystolic);  //收缩压
            commBloodDb.setAvgdiastolic(avgDiastolic);  //平均的舒张压
            commBloodDb.setAvgsystolic(avgSystolic);    //平均收缩压
            List<CommBloodDb> saveCommBloodList = findCommBloodForUpload(bleMac, dateStr);
            if (saveCommBloodList == null) {
                commBloodDb.setUpload(false);
                boolean isSave = commBloodDb.save();
//            Log.e(TAG, "--------111血氧保存=" + isSave);
            } else {
                commBloodDb.setUpload(saveCommBloodList.get(0).isUpload());
                boolean isBloodSave = commBloodDb.saveOrUpdate("userid = ? and devicecode = ? and rtc = ?", userId, bleMac, dateStr);
                Log.e(TAG, "--------22血压保存=" + isBloodSave);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 查询指定日期的血压数据
     *
     * @param bleMac  地址
     * @param dateStr 日期
     * @return
     */
    public List<CommBloodDb> findCommBloodForUpload(String bleMac, String dateStr) {
        try {
            String whereStr = "userid = ? and devicecode = ? and rtc = ?";
            List<CommBloodDb> commBloodDbs = LitePal.where(whereStr, userId, bleMac, dateStr).find(CommBloodDb.class);
            return commBloodDbs == null || commBloodDbs.isEmpty() ? null : commBloodDbs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查询时间段的血压数据
     *
     * @param bleMac   地址
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return
     */
    public List<CommBloodDb> findCommBloodForUpload(String bleMac, String startDay, String endDay) {
        try {
            String where = "devicecode = ? and rtc between ? and ?";
            List<CommBloodDb> commBloodDbs = LitePal.where(where, bleMac, startDay, endDay).find(CommBloodDb.class);
            return commBloodDbs == null || commBloodDbs.isEmpty() ? null : commBloodDbs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 保存从服务器下载的数据
     *
     * @param bleMac
     * @param type
     * @param
     */
    public void saveAllCommDataForType(String bleMac, String type, List<CommDownloadDb> listStr) {
        if (WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(type) || listStr == null)
            return;
        //先查询一下
        List<CommDownloadDb> saveData = findCommDownloadDb(bleMac, type);
        if (saveData == null) {

            LitePal.saveAll(listStr);

        }


    }

    //查询保存的数据，一天只有一条
    public List<CommDownloadDb> findCommDownloadDb(String bleMac, String type) {
        try {
            String whereStr = "userId = ? and deviceCode = ? and commType = ?";
            List<CommDownloadDb> commDownloadDbList = LitePal.where(whereStr, userId, bleMac, type).find(CommDownloadDb.class);
            return commDownloadDbList == null || commDownloadDbList.isEmpty() ? null : commDownloadDbList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 根据开始和结束日期查询数据
     *
     * @param bleMac   mac地址
     * @param type     类型
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return
     */
    public List<CommDownloadDb> findCommDownloadDb(String bleMac, String type, String startDay, String endDay) {
        if (userId == null)
            return null;
        try {
            String whereStr = "userId = ? and deviceCode = ? and commType = ? and dateStr between ? and ?";
            List<CommDownloadDb> commDownloadDbList = LitePal.where(whereStr, userId, bleMac, type, startDay, endDay).find(CommDownloadDb.class);
            return commDownloadDbList == null || commDownloadDbList.isEmpty() ? null : commDownloadDbList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    //开启上传的服务
    public void startUploadDbService(Context context) {
        try {
            //上传缓存的汇总数据
            Intent intent = new Intent(context, UploadCommDbServices.class);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void downloadForCountStep(String startDay, String endDay) {
        UploadCommDbServices.downLoadCountStep(startDay, endDay);
    }


    public interface OnDownloadCountStepListener {
        void allCountStep(String str);

        void allHeartData(String heartStr);

        void allSleepData(String sleepStr);
    }


    /**
     * 设备回复出厂设置清除数据
     */
    public void setRebootDevices(String bMac) {
        try {
            LitePal.deleteAll(CommStepCountDb.class, "userid = ? and devicecode = ? and dateStr = ?", userId, bMac, WatchUtils.getCurrentDate());
            LitePal.deleteAll(CommSleepDb.class, "userid = ? and devicecode = ? and dateStr = ?", userId, bMac, WatchUtils.getCurrentDate());
            LitePal.deleteAll(CommHeartDb.class, "userid = ? and devicecode = ? and dateStr = ?", userId, bMac, WatchUtils.getCurrentDate());
            LitePal.deleteAll(B30HalfHourDB.class, "address = ? and date = ?", bMac, WatchUtils.getCurrentDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
