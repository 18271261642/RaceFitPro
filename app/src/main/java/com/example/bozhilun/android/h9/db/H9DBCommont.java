package com.example.bozhilun.android.h9.db;

import android.util.Log;

import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.H9HeartDBModelDao;
import com.afa.tourism.greendao.gen.H9SleepDBModelDao;
import com.afa.tourism.greendao.gen.H9StepDBModelDao;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.bean.UpHeartBean;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.util.List;


public class H9DBCommont {
    private static volatile H9DBCommont h9DBCommont;
    private static DaoSession daoSession = null;

    public static DaoSession getDaoSession() {
        if (daoSession == null) return daoSession = MyApp.getInstance().getDaoSession();
        else return daoSession;
    }

    public H9DBCommont() {
        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
    }

    public static H9DBCommont getInstance() {
        if (h9DBCommont == null) {
            synchronized (H9DBCommont.class) {
                if (h9DBCommont == null) {
                    h9DBCommont = new H9DBCommont();
                }
            }
        }
        return h9DBCommont;
    }

    /**
     * 数据库保存步数占位的添加        添加占位主要为了没数据时补0
     *
     * @param key
     */
    public void stepDefulData(String key) {
        for (int i = 0; i < 24; i++) {
//            //            添加假数据
//            int heartvalue = 0;
//            switch (i) {
//                case 0:
//                    heartvalue = 0;
//                    break;
//                case 1:
//                    heartvalue = 0;
//                    break;
//                case 2:
//                    heartvalue = 92;
//                    break;
//                case 3:
//                    heartvalue = 0;
//                    break;
//                case 4:
//                    heartvalue = 0;
//                    break;
//                case 5:
//                    heartvalue = 0;
//                    break;
//                case 6:
//                    heartvalue = 0;
//                    break;
//                case 7:
//                    heartvalue = 140;
//                    break;
//                case 8:
//                    heartvalue = 972;
//                    break;
//                case 9:
//                    heartvalue = 48;
//                    break;
//                case 10:
//                    heartvalue = 108;
//                    break;
//                case 11:
//                    heartvalue = 0;
//                    break;
//                case 12:
//                    heartvalue = 0;
//                    break;
//                case 13:
//                    heartvalue = 0;
//                    break;
//                case 14:
//                    heartvalue = 0;
//                    break;
//                case 15:
//                    heartvalue = 0;
//                    break;
//                case 16:
//                    heartvalue = 0;
//                    break;
//                case 17:
//                    heartvalue = 0;
//                    break;
//                case 18:
//                    heartvalue = 0;
//                    break;
//                case 19:
//                    heartvalue = 0;
//                    break;
//                case 20:
//                    heartvalue = 0;
//                    break;
//                case 21:
//                    heartvalue = 0;
//                    break;
//                case 22:
//                    heartvalue = 0;
//                    break;
//                case 23:
//                    heartvalue = 0;
//                    break;
//                case 24:
//                    heartvalue = 0;
//                    break;
//            }
            Log.d("-G---步数", "----------步数占位时间==" + key + " " + (i > 9 ? i + "" : "0" + i) + ":00");
            saveStepToDB(0, key + " " + (i > 9 ? i + "" : "0" + i) + ":00");
        }
    }

    /**
     * 数据库保存心率占位的添加
     *
     * @param key
     */
    public void heartDefulData(String key) {
        for (int i = 0; i < 24; i++) {
//            //添加假数据
//            int heartvalue = 0;
//            switch (i) {
//                case 0:
//                    heartvalue = 71;
//                    break;
//                case 1:
//                    heartvalue = 83;
//                    break;
//                case 2:
//                    heartvalue = 73;
//                    break;
//                case 3:
//                    heartvalue = 69;
//                    break;
//                case 4:
//                    heartvalue = 57;
//                    break;
//                case 5:
//                    heartvalue = 58;
//                    break;
//                case 6:
//                    heartvalue = 71;
//                    break;
//                case 7:
//                    heartvalue = 85;
//                    break;
//                case 8:
//                    heartvalue = 81;
//                    break;
//                case 9:
//                    heartvalue = 80;
//                    break;
//                case 10:
//                    heartvalue = 80;
//                    break;
//                case 11:
//                    heartvalue = 83;
//                    break;
//                case 12:
//                    heartvalue = 82;
//                    break;
//                case 13:
//                    heartvalue = 81;
//                    break;
//                case 14:
//                    heartvalue = 86;
//                    break;
//                case 15:
//                    heartvalue = 84;
//                    break;
//                case 16:
//                    heartvalue = 83;
//                    break;
//                case 17:
//                    heartvalue = 0;
//                    break;
//                case 18:
//                    heartvalue = 0;
//                    break;
//                case 19:
//                    heartvalue = 0;
//                    break;
//                case 20:
//                    heartvalue = 0;
//                    break;
//                case 21:
//                    heartvalue = 0;
//                    break;
//                case 22:
//                    heartvalue = 0;
//                    break;
//                case 23:
//                    heartvalue = 0;
//                    break;
//                case 24:
//                    heartvalue = 0;
//                    break;
//            }
            Log.d("-G---心率", "----------心率占位时间==" + key + " " + (i > 9 ? i + "" : "0" + i) + ":00");
            saveHeartToDB(0, key + " " + (i > 9 ? i + "" : "0" + i) + ":00");
        }
    }


    /**
     * 上传心率占位的添加
     *
     * @param upHeartBeanList
     * @param userId
     * @param bleMac
     */
    public void updataHeartDeful(
            List<UpHeartBean> upHeartBeanList,
            List<Integer> heartList, String userId, String bleMac) {
        /**
         * 原来保存的心率数据为24条  心率图标需要48条，转下
         */
        for (int i = 1; i <= 48; i++) {
            double timesHour = (double) ((i - 1) * 0.5);
            int hours = 0;
            int mins = 0;
            String[] splitT = String.valueOf(timesHour).split("[.]");
            if (splitT.length >= 2) {
                hours = Integer.valueOf(splitT[0]);
                mins = Integer.valueOf(splitT[1]) * 60 / 10;
            } else {
                hours = Integer.valueOf(splitT[0]);
                mins = 0;
            }
            String timeHour = "";
            String timeMin = "";
            if (hours <= 9) {
                timeHour = "0" + hours;
            } else {
                timeHour = "" + hours;
            }
            if (mins <= 9) {
                timeMin = "0" + mins;
            } else {
                timeMin = "" + mins;
            }
            String upDataTime = timeHour + ":" + timeMin;
            //Log.d("-G---设置", "----------心率占位时间==" + upDataTime);
            heartList.add(0);
//            upHeartBeanList.add(new UpHeartBean(userId,
//                    bleMac, "00",
//                    "00", upDataTime,
//                    "0", "0", "00", "00"));

            UpHeartBean upHeartBean = new UpHeartBean(userId, bleMac,
                   0, 0,
                    0, 0,
                    0, WatchUtils.getCurrentDate()+" "+upDataTime,
                    0);
            upHeartBeanList.add(upHeartBean);
        }
    }

    /**
     * 保存睡眠数据到数据库
     *
     * @param newH9SleepDBModel
     */
    public void saveSleepToDB(H9SleepDBModel newH9SleepDBModel) {
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9SleepDBModelDao h9SleepDBModelDao = daoSession.getH9SleepDBModelDao();
        if (h9SleepDBModelDao == null) return;
        daoSession.getH9SleepDBModelDao().insert(newH9SleepDBModel);
    }

    /**
     * 保存睡眠数据到数据库
     *
     * @param sleep_type
     * @param sleep_time
     * @param dataTime
     */
    public void saveSleepToDB(int sleep_type, String sleep_time, String dataTime, String todayTime) {
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9SleepDBModelDao h9SleepDBModelDao = daoSession.getH9SleepDBModelDao();
        if (h9SleepDBModelDao == null) return;


        /**
         * 睡眠有多少拿多少
         */
        Log.d("TAG", "----睡眠数据 保存 == 新增" + todayTime + "==" + dataTime + "==" + sleep_time + "==" + sleep_type);
        H9SleepDBModel newH9SleepDBModel = new H9SleepDBModel();
        newH9SleepDBModel.setDateTime(dataTime);
        newH9SleepDBModel.setDevicesCode(mac);
        newH9SleepDBModel.setUserId(userId);
        newH9SleepDBModel.setSleepTime(sleep_time);
        newH9SleepDBModel.setSleepType(sleep_type);
        newH9SleepDBModel.setRec(todayTime);
        daoSession.getH9SleepDBModelDao().insert(newH9SleepDBModel);

//        H9SleepDBModel h9SleepDBModel = h9SleepDBModelDao.queryBuilder()
//                .where(H9SleepDBModelDao.Properties.DateTime.eq(dataTime),
//                        H9SleepDBModelDao.Properties.DevicesCode.eq(mac),
//                        H9SleepDBModelDao.Properties.UserId.eq(userId)).unique();
//
//        /**
//         * 2018-12-27 08:30:40.844 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:31====04
//         * 2018-12-27 08:30:40.895 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:33====04
//         * 2018-12-27 08:30:40.942 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:35====04
//         * 2018-12-27 08:30:40.990 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:35====04
//         * 2018-12-27 08:30:41.035 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:31====04
//         * 2018-12-27 08:30:41.077 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:33====04
//         * 2018-12-27 08:30:41.119 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:35====04
//         * 2018-12-27 08:30:41.162 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:35====04
//         * 2018-12-27 08:30:42.354 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:31====04
//         * 2018-12-27 08:30:42.401 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:33====04
//         * 2018-12-27 08:30:42.443 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:35====04
//         * 2018-12-27 08:30:42.487 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-26===2018-12-27 04:35====04
//         * 2018-12-27 08:30:42.544 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:31====04
//         * 2018-12-27 08:30:42.600 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:33====04
//         * 2018-12-27 08:30:42.644 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:35====04
//         * 2018-12-27 08:30:42.687 8709-8709/com.example.bozhilun.android D/H9NewRecordFragment: 读取的睡眠数据---2018-12-27===2018-12-27 04:35====04
//         */
//        /**
//         * 判断添加到数据哭
//         */
//        if (h9SleepDBModel != null) {
//            Log.d("TAG", "----睡眠数据 保存 == 更新" + todayTime + "==" + dataTime + "==" + sleep_time + "==" + sleep_type);
//            h9SleepDBModel.setDateTime(dataTime);
//            h9SleepDBModel.setDevicesCode(mac);
//            h9SleepDBModel.setUserId(userId);
//            h9SleepDBModel.setSleepTime(sleep_time);
//            h9SleepDBModel.setSleepType(sleep_type);
//            h9SleepDBModel.setRec(todayTime);
//            daoSession.getH9SleepDBModelDao().update(h9SleepDBModel);
//        } else {
//            Log.d("TAG", "----睡眠数据 保存 == 新增" + todayTime + "==" + dataTime + "==" + sleep_time + "==" + sleep_type);
//            H9SleepDBModel newH9SleepDBModel = new H9SleepDBModel();
//            newH9SleepDBModel.setDateTime(dataTime);
//            newH9SleepDBModel.setDevicesCode(mac);
//            newH9SleepDBModel.setUserId(userId);
//            newH9SleepDBModel.setSleepTime(sleep_time);
//            newH9SleepDBModel.setSleepType(sleep_type);
//            newH9SleepDBModel.setRec(todayTime);
//            daoSession.getH9SleepDBModelDao().insert(newH9SleepDBModel);
//        }
    }

    /**
     * 保存心率数据到数据库
     *
     * @param heartValue
     * @param dataTime
     */
    public void saveHeartToDB(int heartValue, String dataTime) {
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9HeartDBModelDao h9HeartDBModelDao = daoSession.getH9HeartDBModelDao();
        if (h9HeartDBModelDao == null) return;
        H9HeartDBModel h9HeartDBModelitem = h9HeartDBModelDao.queryBuilder().where(H9HeartDBModelDao.Properties.DateTime.eq(dataTime),
                H9HeartDBModelDao.Properties.UserId.eq(userId), H9HeartDBModelDao.Properties.DevicesCode.eq(mac)).unique();
        /**
         * 判断添加到数据哭
         */
        if (h9HeartDBModelitem == null) {
            Log.d("TAG", "----心率数据 保存 == 新增" + dataTime.substring(0, 10) + "===" + dataTime + "===" + heartValue);
            H9HeartDBModel newH9HeartDBModel = new H9HeartDBModel();
            newH9HeartDBModel.setHeartValue(0);
            newH9HeartDBModel.setDateTime(dataTime);
            newH9HeartDBModel.setRec(dataTime.substring(0, 10));
            newH9HeartDBModel.setDevicesCode(mac);
            newH9HeartDBModel.setUserId(userId);
            h9HeartDBModelDao.insert(newH9HeartDBModel);
        } else {
            if (heartValue > 0) {
                Log.d("TAG", "----心率数据 保存 == 更新" + dataTime.substring(0, 10) + "===" + dataTime + "===" + heartValue);
                h9HeartDBModelitem.setUserId(userId);
                h9HeartDBModelitem.setDevicesCode(mac);
                h9HeartDBModelitem.setRec(dataTime.substring(0, 10));
                h9HeartDBModelitem.setDateTime(dataTime);
                h9HeartDBModelitem.setHeartValue(heartValue);
                h9HeartDBModelDao.update(h9HeartDBModelitem);
            }

        }
    }

    /**
     * 保存步数数据到数据库
     *
     * @param sportValues
     * @param dataTime    timeToday + " " + hourMapKey + ":00"
     */
    public void saveStepToDB(int sportValues, String dataTime) {
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), "userId");
        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9StepDBModelDao h9StepDBModelDao = daoSession.getH9StepDBModelDao();
        if (h9StepDBModelDao == null) return;
        H9StepDBModel h9StepDBModel = h9StepDBModelDao.queryBuilder()
                .where(H9StepDBModelDao.Properties.DateTime.eq(dataTime),
                        H9StepDBModelDao.Properties.DevicesCode.eq(mac),
                        H9StepDBModelDao.Properties.UserId.eq(userId)).unique();
        /**
         * 判断添加到数据哭
         */
        if (h9StepDBModel != null) {
            if (sportValues > 0) {
                h9StepDBModel.setRec(dataTime.substring(0, 10));//2018-12-20
                h9StepDBModel.setDateTime(dataTime);
                h9StepDBModel.setDevicesCode(mac);
                h9StepDBModel.setUserId(userId);
                h9StepDBModel.setStepNumber(sportValues);
                Log.d("TAG", "----步数数据 保存 == 更新" + dataTime.substring(0, 10) + "==" + dataTime + "==" + sportValues);
                daoSession.getH9StepDBModelDao().update(h9StepDBModel);
            }
        } else {
            H9StepDBModel newH9StepDBModel = new H9StepDBModel();
            newH9StepDBModel.setRec(dataTime.substring(0, 10));//2018-12-20
            newH9StepDBModel.setDateTime(dataTime);
            newH9StepDBModel.setDevicesCode(mac);
            newH9StepDBModel.setUserId(userId);
            newH9StepDBModel.setStepNumber(sportValues);
            Log.d("TAG", "----步数数据 保存 == 新增" + dataTime.substring(0, 10) + "==" + dataTime + "==" + sportValues);
            daoSession.getH9StepDBModelDao().insert(newH9StepDBModel);
        }
    }


    /**
     * 读取步数数据
     *
     * @return
     */
    public List<H9StepDBModel> readDBStepTo() {
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9StepDBModelDao h9StepDBModelDao = daoSession.getH9StepDBModelDao();
        if (h9StepDBModelDao != null) {
            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
            List<H9StepDBModel> list = h9StepDBModelDao.queryBuilder().where(H9StepDBModelDao.Properties.DevicesCode.eq(mac)).list();
            return (list == null || list.isEmpty()) ? (h9StepDBModelDao.loadAll()) : list;
        } else {
            return null;
        }
    }

    /**
     * 读取心率数据
     *
     * @return
     */
    public List<H9HeartDBModel> readDBHeartTo() {
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9HeartDBModelDao h9HeartDBModelDao = daoSession.getH9HeartDBModelDao();
        if (h9HeartDBModelDao != null) {
            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
            List<H9HeartDBModel> list = h9HeartDBModelDao.queryBuilder().where(H9HeartDBModelDao.Properties.DevicesCode.eq(mac)).list();
            return (list == null || list.isEmpty()) ? (h9HeartDBModelDao.loadAll()) : list;
        } else {
            return null;
        }
    }


    /**
     * 读取心睡眠数据
     *
     * @return
     */
    public List<H9SleepDBModel> readDBSleepTo() {
        if (daoSession == null) MyApp.getInstance().getDaoSession();
        H9SleepDBModelDao h9SleepDBModelDao = daoSession.getH9SleepDBModelDao();
        if (h9SleepDBModelDao != null) {
            String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
            List<H9SleepDBModel> list = h9SleepDBModelDao.queryBuilder().where(H9SleepDBModelDao.Properties.DevicesCode.eq(mac)).list();
            return (list == null || list.isEmpty()) ? (h9SleepDBModelDao.loadAll()) : list;
        } else {
            return null;
        }
    }
}
