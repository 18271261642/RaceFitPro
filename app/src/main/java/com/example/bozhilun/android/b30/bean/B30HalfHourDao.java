package com.example.bozhilun.android.b30.bean;


import android.util.Log;

import com.example.bozhilun.android.b31.model.B31HRVBean;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.MyLogUtil;
import org.litepal.LitePal;
import java.util.List;

/**
 * 数据库操作: B30半小时数据源
 *
 * @author XuBo 2018-09-19
 */
public class B30HalfHourDao {

    private static final String TAG = "B30HalfHourDao";

    /**
     * 单例
     */
    private static B30HalfHourDao mInstance;

    private B30HalfHourDao() {
    }

    /**
     * 获取单例
     */
    public static B30HalfHourDao getInstance() {
        if (mInstance == null) {
            mInstance = new B30HalfHourDao();
        }
        return mInstance;
    }

    /**
     * 数据源类型: 步数数据
     */
    public static final String TYPE_STEP = "step";
    /**
     * 数据源类型: 运动数据
     */
    public static final String TYPE_SPORT = "sport";



    /**
     * 数据源类型: 心率数据
     */
    public static final String TYPE_RATE = "rate";
    /**
     * 数据源类型: 血压数据
     */
    public static final String TYPE_BP = "bp";
    /**
     * 数据源类型: 睡眠数据
     */
    public static final String TYPE_SLEEP = "sleep";

    //精准睡眠
    public static final String TYPE_PRECISION_SLEEP = "precision_sleep";


    //B16的汇总睡眠
    public static final String B18_COUNT_SLEEP = "b18_count_sleep";
    //B16详细睡眠
    public static final String B16_DETAIL_SLEEP = "b16_detail_sleep";
    //B16详细运动数据
    public static final String B16_DETAIL_SPORT = "b16_detail_sport";


    //XWatch汇总步数
    public static final String XWATCH_DAY_STEP = "x_watch_day_step";

    //XWatch步数详细数据
    public static final String XWATCH_DETAIL_SPORT = "x_watch_detail_sport";

    /**
     * 获取单条数据源
     *
     * @param address 手环MAC地址
     * @param date    日期
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 数据源Json字符串
     */
    private B30HalfHourDB getOriginData(String address, String date, String type) {
        try {
            String where = "address = ? and date = ? and type = ?";
            List<B30HalfHourDB> resultList = LitePal.where(where, address, date, type).limit(1).find
                    (B30HalfHourDB.class);// 一个类型,同一天只有一条数据
            return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查找单条数据源
     *
     * @param address 手环MAC地址
     * @param date    日期
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 数据源Json字符串
     */
    public String findOriginData(String address, String date, String type) {
        try {
            B30HalfHourDB result = getOriginData(address, date, type);
            return result == null ? null : result.getOriginData();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 保存(更新)单条数据源
     *
     * @param db 数据源实体类
     */
    public synchronized void saveOriginData(B30HalfHourDB db) {
        try {
            boolean result;
            String bMac = db.getAddress();
            String strDate = db.getDate();
            String type = db.getType();
            result = db.saveOrUpdate("address=? and date =? and type=?",bMac,strDate,type);
            Log.e("DB","--------数据存储="+result);
        }catch (Exception e){
          e.printStackTrace();
        }


    }


    /**
     * 根据类型查找所有没上传服务器的数据源,不分日期
     *
     * @param address 手环MAC地址
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 指定类型的, 没有上传服务器的所有数据源
     */
    public List<B30HalfHourDB> findNotUploadData(String address, String type) {
        String where = "upload = 0 and address = ? and type = ?";
//        String where = "address = ? and type = ?";
        return LitePal.where(where, address, type).find(B30HalfHourDB.class);
    }


    /**
     * 根据日期查询没有上传的数据,一个类型一天只有一条数据
     * @param bleMac mac地址
     * @param type 类型
     * @param dayStr 日期yyyy-mm-dd
     * @return
     */
    public List<B30HalfHourDB> findNotUpDataByDay(String bleMac,String type,String dayStr){
        try {
            List<B30HalfHourDB> stepDayList = LitePal.where("upload = 0 and address = ? and type = ? and date = ?",bleMac,type,dayStr).find(B30HalfHourDB.class);
            return stepDayList == null || stepDayList.isEmpty() ? null : stepDayList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }






    /**
     * 保存W30和W31的详细运动数据
     * @param dateStr 日期
     * @param bleMac mac地址
     * @param type 类型
     * @param stepStr 详情数据
     */
    public void saveW30SportData(String dateStr,String bleMac,String type,String stepStr){
        if(WatchUtils.isEmpty(dateStr) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(stepStr))
            return;
        Log.e(TAG,"--------saveW30SportData="+dateStr+"---type="+type +"----bleMac="+bleMac);
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        b30HalfHourDB.setDate(dateStr);
        b30HalfHourDB.setAddress(bleMac);
        b30HalfHourDB.setType(type);
        b30HalfHourDB.setOriginData(stepStr);

        List<B30HalfHourDB> saveW30SportBean = findW30SportData(dateStr,bleMac,type);
        if(saveW30SportBean == null){
            b30HalfHourDB.setUpload(0);  //0未上传 1 已上传
            boolean isSave = b30HalfHourDB.save();
            Log.e(TAG,"----11--isSave="+isSave);
        }else{
            String whereStr = "address = ? and date = ? and type = ?";
            b30HalfHourDB.setUpload(saveW30SportBean.get(0).getUpload());
            boolean isSave = b30HalfHourDB.saveOrUpdate(whereStr,bleMac,dateStr,type);
            Log.e(TAG,"----22---isSave="+isSave);
        }


    }


    /**
     * 根据指定日期查询w30保存的数据
     * @param dateStr 日期
     * @param bleMac 地址
     * @return B30HalfHourDB
     */
    public List<B30HalfHourDB> findW30SportData(String dateStr,String bleMac,String type){
        try {
            List<B30HalfHourDB> w30SportDbList = LitePal.where("address = ? and date = ? and type = ?",bleMac,dateStr,type).find(B30HalfHourDB.class);
            return w30SportDbList == null || w30SportDbList.isEmpty() ? null : w30SportDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     * W30保存睡眠详细数据
     * @param dateStr 日期
     * @param bleMac 地址
     * @param type 类型
     * @param sleepStr 睡眠详细数据
     */
    public void saveW30SleepDetail(String dateStr,String bleMac,String type,String sleepStr){
        if(WatchUtils.isEmpty(dateStr) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(sleepStr))
            return;
        Log.e(TAG,"---------w30睡眠="+dateStr+"---bleMac="+bleMac+"--type="+type);
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        b30HalfHourDB.setDate(dateStr);
        b30HalfHourDB.setAddress(bleMac);
        b30HalfHourDB.setType(type);
        b30HalfHourDB.setOriginData(sleepStr);
        //
         List<B30HalfHourDB> saveSleep = findW30SleepDetail(dateStr,bleMac,type);
         if(saveSleep == null){ //未保存过
             b30HalfHourDB.setUpload(0);
             boolean isSleepSave = b30HalfHourDB.save();
             Log.e(TAG,"-------11--睡眠--"+isSleepSave);
         }else{  //已经保存过
            b30HalfHourDB.setUpload(saveSleep.get(0).getUpload());
            boolean isSleepSave = b30HalfHourDB.saveOrUpdate("address = ? and date = ? and type = ?",bleMac,dateStr,type);
             Log.e(TAG,"-------11--睡眠--"+isSleepSave);
        }


    }

    /**
     * 根据指定日期查询睡眠数据，一天只有一条
     * @param dateStr 日期
     * @param bleMac mac地址
     * @param type 类型
     * @return
     */
    public List<B30HalfHourDB> findW30SleepDetail(String dateStr,String bleMac,String type){
        try {
            List<B30HalfHourDB> w30SleepDbList = LitePal.where("address = ? and date = ? and type = ?",bleMac,dateStr,type).find(B30HalfHourDB.class);
            return w30SleepDbList == null || w30SleepDbList.isEmpty() ? null : w30SleepDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    //保存心率详细数据
    public void saveW30HeartDetail(String dateStr,String bleMac,String type,String heartStr){
        if(WatchUtils.isEmpty(dateStr) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(heartStr))
            return;
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        b30HalfHourDB.setDate(dateStr);
        b30HalfHourDB.setAddress(bleMac);
        b30HalfHourDB.setType(type);
        b30HalfHourDB.setOriginData(heartStr);

        List<B30HalfHourDB> saveHeart = findW30HeartDetail(dateStr,bleMac,type);
        if(saveHeart == null){
            b30HalfHourDB.setUpload(0);
            boolean isHeartSave = b30HalfHourDB.save();
            Log.e(TAG,"------------111心率保存="+isHeartSave);
        }else{
            b30HalfHourDB.setUpload(saveHeart.get(0).getUpload());
            boolean isHeartSave = b30HalfHourDB.saveOrUpdate("address = ? and date = ? and type = ?",bleMac,dateStr,type);
            Log.e(TAG,"---------222---心率保存="+isHeartSave);
        }

    }


    /**
     * 根据指定日期查询心率详细数据
     * @param dateStr 日期
     * @param bleMac mac地址
     * @param type 类型
     * @return
     */
    public List<B30HalfHourDB> findW30HeartDetail(String dateStr,String bleMac,String type){
        Log.e(TAG,"------查询---------1111="+dateStr+"--="+bleMac+"---type="+type);
        try {
            List<B30HalfHourDB> w30HeartDbList = LitePal.where("address = ? and date = ? and type = ?",bleMac,dateStr,type).find(B30HalfHourDB.class);
            return w30HeartDbList == null || w30HeartDbList.isEmpty() ? null : w30HeartDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    //保存W37血压详细数据
    public void saveW37BloodDetail(String dateStr,String bleMac,String type,String heartStr){
        if(WatchUtils.isEmpty(dateStr) || WatchUtils.isEmpty(bleMac) || WatchUtils.isEmpty(heartStr))
            return;
        B30HalfHourDB b30HalfHourDB = new B30HalfHourDB();
        b30HalfHourDB.setDate(dateStr);
        b30HalfHourDB.setAddress(bleMac);
        b30HalfHourDB.setType(type);
        b30HalfHourDB.setOriginData(heartStr);

        List<B30HalfHourDB> saveHeart = findW37BloodDetail(dateStr,bleMac,type);
        if(saveHeart == null){
            b30HalfHourDB.setUpload(0);
            boolean isHeartSave = b30HalfHourDB.save();
            Log.e(TAG,"------------111心率保存="+isHeartSave);
        }else{
            b30HalfHourDB.setUpload(saveHeart.get(0).getUpload());
            boolean isHeartSave = b30HalfHourDB.saveOrUpdate("address = ? and date = ? and type = ?",bleMac,dateStr,type);
            Log.e(TAG,"---------222---心率保存="+isHeartSave);
        }

    }

    /**
     * 根据指定日期查询血压详细数据
     * @param dateStr 日期
     * @param bleMac mac地址
     * @param type 类型
     * @return
     */
    public List<B30HalfHourDB> findW37BloodDetail(String dateStr,String bleMac,String type ){
        Log.e(TAG,"------查询---------1111="+dateStr+"--="+bleMac+"---type="+type);
        try {
            List<B30HalfHourDB> w37BloodDbList = LitePal.where("address = ? and date = ? and type = ?",bleMac,dateStr,type).find(B30HalfHourDB.class);
            return w37BloodDbList == null || w37BloodDbList.isEmpty() ? null : w37BloodDbList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
