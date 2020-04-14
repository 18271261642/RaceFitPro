package com.example.bozhilun.android.b15p.b15pdb;


import android.util.Log;

import com.afa.tourism.greendao.gen.B15PAllStepDBDao;
import com.afa.tourism.greendao.gen.B15PBloodDBDao;
import com.afa.tourism.greendao.gen.B15PHeartDBDao;
import com.afa.tourism.greendao.gen.B15PSleepDBDao;
import com.afa.tourism.greendao.gen.B15PTestBloopDBDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.B15PStepDBDao;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;


import org.greenrobot.greendao.query.Query;

import java.util.List;

public class B15PDBCommont {
    private static volatile B15PDBCommont b15PDBCommont;
    private DaoSession daoSession = null;
    private static final String TAG = "B15PDBCommont";

    private DaoSession getDaoSession() {
        if (daoSession == null) return daoSession = MyApp.getInstance().getDaoSession();
        else return daoSession;
    }

    private B15PDBCommont() {
        if (daoSession == null) daoSession = MyApp.getInstance().getDaoSession();
    }

    public static B15PDBCommont getInstance() {
        if (b15PDBCommont == null) {
            synchronized (B15PDBCommont.class) {
                if (b15PDBCommont == null) {
                    b15PDBCommont = new B15PDBCommont();
                }
            }
        }
        return b15PDBCommont;
    }


    /**
     * 查当天汇总步数
     *
     * @param mac
     * @param timeDate
     * @return
     */
    public List<B15PAllStepDB> findAllStepAllDatas(String mac, String timeDate) {
        if (Commont.isDebug) Log.e(TAG, "查询汇总步数  " + mac + "   " + timeDate);
        B15PAllStepDBDao b15PStepDBDao = getDaoSession().getB15PAllStepDBDao();
        if (b15PStepDBDao == null) return null;
        List<B15PAllStepDB> list = null;
        try {
            list = b15PStepDBDao.queryBuilder().where(
                    B15PAllStepDBDao.Properties.DevicesMac.eq(mac),
                    B15PAllStepDBDao.Properties.StepDataTime.eq(timeDate)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List<B15PStepDB> list = LitePal.where("devicesMac = ? and stepData = ?", mac, timeDate).find(B15PStepDB.class);
//        if (list == null) list = LitePal.findAll(B15PStepDB.class);
//        if (Commont.isDebug) Log.e(TAG,list.toString());
        return list;
    }

    /**
     * 查步数
     *
     * @param mac
     * @param timeDate
     * @return
     */
    public List<B15PStepDB> findStepAllDatas(String mac, String timeDate) {
        if (Commont.isDebug)Log.e(TAG, "查询步数  " + mac + "   " + timeDate);
        B15PStepDBDao b15PStepDBDao = getDaoSession().getB15PStepDBDao();
        if (b15PStepDBDao == null) return null;
        List<B15PStepDB> list = null;
        try {
            list = b15PStepDBDao.queryBuilder().where(
                    B15PStepDBDao.Properties.DevicesMac.eq(mac),
                    B15PStepDBDao.Properties.StepData.eq(timeDate)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        List<B15PStepDB> list = LitePal.where("devicesMac = ? and stepData = ?", mac, timeDate).find(B15PStepDB.class);
//        if (list == null) list = LitePal.findAll(B15PStepDB.class);
//        if (Commont.isDebug) Log.e(TAG,list.toString());
        return list;
    }

    /**
     * 查睡眠
     *
     * @param mac
     * @param timeDate
     * @return
     */
    public List<B15PSleepDB> findSleepAllDatas(String mac, String timeDate) {
        if (Commont.isDebug)Log.e(TAG, "查询睡眠  " + mac + "   " + timeDate);
        B15PSleepDBDao b15PSleepDBDao = getDaoSession().getB15PSleepDBDao();
        if (b15PSleepDBDao == null) return null;
//        String oldDay = WatchUtils.obtainAroundDate(timeDate, true);
//        List<B15PSleepDB> lists = b15PSleepDBDao.queryBuilder().where(B15PSleepDBDao.Properties.DevicesMac.eq(mac),
//                B15PSleepDBDao.Properties.SleepData.eq(timeDate)).list();
//        if (Commont.isDebug) Log.e(TAG, "=========获取的睡眠  " + (lists == null ? "获取的睡眠为空" : lists.toString()));
        List<B15PSleepDB> list = null;
        try {
            list = b15PSleepDBDao.queryBuilder().where(B15PSleepDBDao.Properties.DevicesMac.eq(mac)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        List<B15PSleepDB> list = LitePal.where("devicesMac = ? and sleepData = ?", mac, timeDate).find(B15PSleepDB.class);
//        if (list == null) list = LitePal.findAll(B15PSleepDB.class);
//        if (Commont.isDebug)Log.e(TAG,list.toString());
        return list;
    }


    /**
     * 查心率
     *
     * @param mac
     * @param timeDate
     * @return
     */
    public List<B15PHeartDB> findHeartAllDatas(String mac, String timeDate) {
        if (Commont.isDebug)Log.e(TAG, "查询心率  " + mac + "   " + timeDate);
        B15PHeartDBDao b15PHeartDBDao = getDaoSession().getB15PHeartDBDao();
        if (b15PHeartDBDao == null) return null;
        List<B15PHeartDB> list = null;
        try {
            list = b15PHeartDBDao.queryBuilder().where(
                    B15PHeartDBDao.Properties.DevicesMac.eq(mac),
                    B15PHeartDBDao.Properties.HeartData.eq(timeDate)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        List<B15PHeartDB> list = LitePal.where("devicesMac = ? and heartData = ?", mac, timeDate).find(B15PHeartDB.class);
//        //if (list == null) list = LitePal.findAll(B15PHeartDB.class);
//        if (Commont.isDebug) Log.e(TAG,"-----心率读取----"+list.toString());
        return list;
    }


    public B15PHeartDB findHeartItemDatas(String mac, String date, String time) {
        if (Commont.isDebug) Log.e(TAG, "查询心率  " + mac + "   " + date + "  " + time);
        B15PHeartDBDao b15PHeartDBDao = getDaoSession().getB15PHeartDBDao();
        if (b15PHeartDBDao == null) return null;
        B15PHeartDB unique = null;
        try {
            unique = b15PHeartDBDao.queryBuilder().where(
                    B15PHeartDBDao.Properties.DevicesMac.eq(mac),
                    B15PHeartDBDao.Properties.HeartData.eq(date),
                    B15PHeartDBDao.Properties.HeartTime.eq(time)
            ).unique();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return unique;
    }

    /**
     * 查血压
     *
     * @param mac
     * @param timeDate
     * @return
     */
    public List<B15PBloodDB> findBloopAllDatas(String mac, String timeDate) {
        if (Commont.isDebug) Log.e(TAG, "查询血压  " + mac + "   " + timeDate);
        B15PBloodDBDao b15PBloodDBDao = getDaoSession().getB15PBloodDBDao();
        if (b15PBloodDBDao == null) return null;

        List<B15PBloodDB> list = null;
        try {
            list = b15PBloodDBDao.queryBuilder().where(
                    B15PBloodDBDao.Properties.DevicesMac.eq(mac),
                    B15PBloodDBDao.Properties.BloodData.eq(timeDate)).list();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List<B15PBloodDB> list = LitePal.where("devicesMac = ? and bloodData = ?", mac, timeDate).find(B15PBloodDB.class);
//        if (list == null) list = LitePal.findAll(B15PBloodDB.class);
//        if (Commont.isDebug) Log.e(TAG,list.toString());
        return list;
    }


    /**
     * 查血压
     *
     * @param mac
     * @return
     */
    public List<B15PTestBloopDB> findTestBloopAllDatas(String mac) {
        B15PTestBloopDBDao b15PTestBloopDBDao = getDaoSession().getB15PTestBloopDBDao();
        if (b15PTestBloopDBDao == null) return null;
        List<B15PTestBloopDB> list = null;
        try {
            list = b15PTestBloopDBDao.loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List<B15PTestBloopDB> list = LitePal.where("devicesMac = ?", mac).find(B15PTestBloopDB.class);
//        if (list == null) list = LitePal.findAll(B15PTestBloopDB.class);

        return list;
    }

    //--------------------------------------------------------------
    /****************************************************************/
    //--------------------------------------------------------------

    /**
     * 保存汇总步数数据到数据库
     *
     * @param sportValues
     * @param dataTime    timeToday + " " + hourMapKey + ":00"
     */
    public void saveAllStepToDB(String mac, String dataTime, int sportValues) {

        //TStrData:[,2019-05-13,164]
        B15PAllStepDBDao b15PAllStepDBDao = getDaoSession().getB15PAllStepDBDao();
        if (b15PAllStepDBDao != null) {
            B15PAllStepDB b15PStepDB = b15PAllStepDBDao.queryBuilder()
                    .where(
                            B15PAllStepDBDao.Properties.StepDataTime.eq(dataTime.substring(0, 10)),
                            B15PAllStepDBDao.Properties.DevicesMac.eq(mac)).unique();
            if (Commont.isDebug) Log.e(TAG, "AllStep保存前查询  " + mac + "  " + dataTime.substring(0, 10) + "  " + (b15PStepDB == null ? dataTime + "  点的步数数据没有" : b15PStepDB.toString()));
            if (b15PStepDB != null) {
                if (sportValues > 0) {
                    b15PStepDB.setDevicesMac(mac);
                    //[,2019-04-22 26]
                    b15PStepDB.setStepDataTime(dataTime.substring(0, 10));
                    b15PStepDB.setStepItemNumber(sportValues);
                    b15PStepDB.setIsUpdata(0);
                    // if (Commont.isDebug)Log.e(TAG, "==== 更新 日步数详细 " + b15PStepDB.toString());
                    b15PDBCommont.getDaoSession().getB15PAllStepDBDao().update(b15PStepDB);
                }
            } else {
                B15PAllStepDB newb15PStepDB = new B15PAllStepDB();
                newb15PStepDB.setStepDataTime(dataTime.substring(0, 10));
                newb15PStepDB.setDevicesMac(mac);
                newb15PStepDB.setStepItemNumber(sportValues);
                newb15PStepDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 新增 日步数详细 " + newb15PStepDB.toString());
                b15PDBCommont.getDaoSession().getB15PAllStepDBDao().insert(newb15PStepDB);
            }
        }
    }

    /**
     * 保存步数数据到数据库
     *
     * @param sportValues
     * @param dataTime    timeToday + " " + hourMapKey + ":00"
     */
    public void saveStepToDB(String mac, String dataTime, int sportValues) {

        //2019-04-27 08:10:00   47
        B15PStepDBDao b15PStepDBDao = getDaoSession().getB15PStepDBDao();
        if (b15PStepDBDao != null) {
            B15PStepDB b15PStepDB = b15PStepDBDao.queryBuilder()
                    .where(
                            B15PStepDBDao.Properties.StepData.eq(dataTime.substring(0, 10)),
                            B15PStepDBDao.Properties.StepTime.eq(dataTime.substring(11, 16)),
                            B15PStepDBDao.Properties.DevicesMac.eq(mac)).unique();
            if (Commont.isDebug) Log.e(TAG, "Step保存前查询  " + mac + "  " + dataTime.substring(0, 10) + "  " + dataTime.substring(11, 16) + "  " + (b15PStepDB == null ? dataTime + "  点的步数数据没有" : b15PStepDB.toString()));
            if (b15PStepDB != null) {
                if (sportValues > 0) {
                    b15PStepDB.setDevicesMac(mac);
                    //[,2019-04-22 04:00:00,26]
                    b15PStepDB.setStepData(dataTime.substring(0, 10));
                    b15PStepDB.setStepTime(dataTime.substring(11, 16));
                    b15PStepDB.setStepItemNumber(sportValues);
                    b15PStepDB.setIsUpdata(0);
                    // if (Commont.isDebug)Log.e(TAG, "==== 更新 日步数详细 " + b15PStepDB.toString());
                    b15PDBCommont.getDaoSession().getB15PStepDBDao().update(b15PStepDB);
                }
            } else {
                B15PStepDB newb15PStepDB = new B15PStepDB();
                newb15PStepDB.setStepData(dataTime.substring(0, 10));
                newb15PStepDB.setDevicesMac(mac);
                newb15PStepDB.setStepTime(dataTime.substring(11, 16));
                newb15PStepDB.setStepItemNumber(sportValues);
                newb15PStepDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 新增 日步数详细 " + newb15PStepDB.toString());
                b15PDBCommont.getDaoSession().getB15PStepDBDao().insert(newb15PStepDB);
            }
        }
    }


    public void saveSleepToDB(String mac, String dataTime, String sleepType) {


        B15PSleepDBDao b15PSleepDBDao = getDaoSession().getB15PSleepDBDao();
        if (b15PSleepDBDao != null) {
            B15PSleepDB b15PSleepDB = b15PSleepDBDao.queryBuilder()
                    .where(B15PSleepDBDao.Properties.SleepData.eq(dataTime.substring(0, 10)),
                            B15PSleepDBDao.Properties.SleepTime.eq(dataTime.substring(11, 16)),
                            B15PSleepDBDao.Properties.DevicesMac.eq(mac)).unique();
            if (Commont.isDebug)Log.e(TAG, "Sleep保存前查询  " + mac + "  " + dataTime.substring(0, 10) + "  " + dataTime.substring(11, 16) + "  " + (b15PSleepDB == null ? dataTime + "  点的睡眠数据没有" : b15PSleepDB.toString()));
            if (b15PSleepDB != null) {
                b15PSleepDB.setDevicesMac(mac);
                //[,2019-04-22 04:00:00,26]
                b15PSleepDB.setSleepData(dataTime.substring(0, 10));
                b15PSleepDB.setSeepTime(dataTime.substring(11, 16));
                b15PSleepDB.setSleepType(sleepType);
                b15PSleepDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日睡眠详细 " + b15PSleepDB.toString());
                b15PDBCommont.getDaoSession().getB15PSleepDBDao().update(b15PSleepDB);
            } else {
                B15PSleepDB newb15PSleepDB = new B15PSleepDB();
                newb15PSleepDB.setDevicesMac(mac);
                newb15PSleepDB.setSleepData(dataTime.substring(0, 10));
                newb15PSleepDB.setSeepTime(dataTime.substring(11, 16));
                newb15PSleepDB.setSleepType(sleepType);
                newb15PSleepDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日睡眠详细 " + newb15PSleepDB.toString());
                b15PDBCommont.getDaoSession().getB15PSleepDBDao().insert(newb15PSleepDB);
            }
        }
    }


    public void saveHeartToDB(String mac, String dataTime, int heartValue) {

        B15PHeartDBDao b15PHeartDBDao = getDaoSession().getB15PHeartDBDao();
        if (b15PHeartDBDao != null) {
            B15PHeartDB b15PHeartDB = b15PHeartDBDao.queryBuilder()
                    .where(
                            B15PHeartDBDao.Properties.HeartData.eq(dataTime.substring(0, 10)),
                            B15PHeartDBDao.Properties.HeartTime.eq(dataTime.substring(11, 16)),
                            B15PHeartDBDao.Properties.DevicesMac.eq(mac)).unique();
            if (Commont.isDebug)Log.e(TAG, "Heart保存前查询  " + mac + "  " + dataTime.substring(0, 10) + "  " + dataTime.substring(11, 16) + "  " + (b15PHeartDB == null ? dataTime + "  点的心率数据没有" : b15PHeartDB.toString()));
            if (b15PHeartDB != null) {
                b15PHeartDB.setDevicesMac(mac);
                //[,2019-04-22 04:00:00,26]
                b15PHeartDB.setHeartData(dataTime.substring(0, 10));
                b15PHeartDB.setHeartTime(dataTime.substring(11, 16));
                b15PHeartDB.setHeartNumber(heartValue);
                b15PHeartDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日心率详细 " + b15PHeartDB.toString());
                b15PDBCommont.getDaoSession().getB15PHeartDBDao().update(b15PHeartDB);
            } else {
                B15PHeartDB newb15PHeartDB = new B15PHeartDB();
                newb15PHeartDB.setDevicesMac(mac);
                newb15PHeartDB.setHeartData(dataTime.substring(0, 10));
                newb15PHeartDB.setHeartTime(dataTime.substring(11, 16));
                newb15PHeartDB.setHeartNumber(heartValue);
                newb15PHeartDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日心率详细 " + newb15PHeartDB.toString());
                b15PDBCommont.getDaoSession().getB15PHeartDBDao().insert(newb15PHeartDB);
            }
        }
    }


    public void saveBloopToDB(String mac, String dataTime, int bloopValueH, int bloopValueL) {

        B15PBloodDBDao b15PBloodDBDao = getDaoSession().getB15PBloodDBDao();
        if (b15PBloodDBDao != null) {
            B15PBloodDB b15PBloodDB = b15PBloodDBDao.queryBuilder()
                    .where(
                            B15PBloodDBDao.Properties.BloodData.eq(dataTime.substring(0, 10)),
                            B15PBloodDBDao.Properties.BloodTime.eq(dataTime.substring(11, 16)),
                            B15PBloodDBDao.Properties.DevicesMac.eq(mac)).unique();
            if (Commont.isDebug)Log.e(TAG, "Bloop保存前查询  " + mac + "  " + dataTime.substring(0, 10) + "  " + dataTime.substring(11, 16) + "  " + (b15PBloodDB == null ? dataTime + "  点的血压数据没有" : b15PBloodDB.toString()));
            if (b15PBloodDB != null) {
                b15PBloodDB.setDevicesMac(mac);
                //[,2019-04-22 04:00:00,26]
                b15PBloodDB.setBloodData(dataTime.substring(0, 10));
                b15PBloodDB.setBloodTime(dataTime.substring(11, 16));
                b15PBloodDB.setBloodNumberH(bloopValueH);
                b15PBloodDB.setBloodNumberL(bloopValueL);
                b15PBloodDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日步数详细 " + b15PBloodDB.toString());
                b15PDBCommont.getDaoSession().getB15PBloodDBDao().update(b15PBloodDB);
            } else {
                B15PBloodDB newb15PBloopDB = new B15PBloodDB();
                newb15PBloopDB.setDevicesMac(mac);
                newb15PBloopDB.setBloodData(dataTime.substring(0, 10));
                newb15PBloopDB.setBloodTime(dataTime.substring(11, 16));
                newb15PBloopDB.setBloodNumberH(bloopValueH);
                newb15PBloopDB.setBloodNumberL(bloopValueL);
                newb15PBloopDB.setIsUpdata(0);
                // if (Commont.isDebug)Log.e(TAG, "==== 更新 日步数详细 " + newb15PBloopDB.toString());
                b15PDBCommont.getDaoSession().getB15PBloodDBDao().insert(newb15PBloopDB);
            }
        }
    }


    /**
     * 保存当前测试点额血压数据
     *
     * @param mac
     * @param bloopValueH
     * @param bloopValueL
     */
    public void saveTestBloopToDB(String mac, int bloopValueH, int bloopValueL) {
        if (Commont.isDebug)Log.e(TAG, "保存测试的血压  " + mac + "  " + bloopValueH + "  " + bloopValueL);
        B15PTestBloopDBDao b15PTestBloopDBDao = getDaoSession().getB15PTestBloopDBDao();
        if (b15PTestBloopDBDao != null) {
            List<B15PTestBloopDB> b15PTestBloopDBS = b15PTestBloopDBDao.loadAll();
            if (b15PTestBloopDBS != null) b15PTestBloopDBS.removeAll(b15PTestBloopDBS);

            B15PTestBloopDB newB15PTestBloopDB = new B15PTestBloopDB();
            newB15PTestBloopDB.setDevicesMac(mac);
            newB15PTestBloopDB.setBloodNumberH(bloopValueH);
            newB15PTestBloopDB.setBloodNumberL(bloopValueL);
            b15PTestBloopDBDao.save(newB15PTestBloopDB);
        } else {
            B15PTestBloopDB newB15PTestBloopDB = new B15PTestBloopDB();
            newB15PTestBloopDB.setDevicesMac(mac);
            newB15PTestBloopDB.setBloodNumberH(bloopValueH);
            newB15PTestBloopDB.setBloodNumberL(bloopValueL);
            b15PDBCommont.getDaoSession().getB15PTestBloopDBDao().insert(newB15PTestBloopDB);
        }
    }


    /**
     * 清楚某一天的所有数据
     *
     * @param mac
     * @param timeDate
     */
    public void clearTodayDatas(String mac, String timeDate) {

        if (Commont.isDebug)Log.e(TAG, "====   开始OTA  清除数据库");
        deleteTodayStepData(mac, timeDate);
        deleteTodayAllStepData(mac, timeDate);
        deleteTodaySleepData(mac, timeDate);
        deleteTodaySleepData(mac, WatchUtils.obtainAroundDate(timeDate, true));
        deleteTodayHeartData(mac, timeDate);
        deleteTodayBloodData(mac);
    }

    /**
     * 删除某一天步数数据
     *
     * @param mac
     * @param timeDate
     */
    private void deleteTodayStepData(String mac, String timeDate) {
        B15PStepDBDao b15PStepDBDao = getDaoSession().getB15PStepDBDao();
        if (b15PStepDBDao == null) return;
        Query<B15PStepDB> query = b15PStepDBDao.queryBuilder()
                .where(B15PStepDBDao.Properties.DevicesMac.eq(mac),
                        B15PStepDBDao.Properties.StepData.eq(timeDate)).build();
        if (query == null) return;
        List<B15PStepDB> list = query.list();
        if (list != null && list.size() > 0) {
            for (B15PStepDB foodBean : list) {
                b15PStepDBDao.delete(foodBean);
            }
        }
    }

    /**
     * 删除某一天汇总步数数据
     *
     * @param mac
     * @param timeDate
     */
    private void deleteTodayAllStepData(String mac, String timeDate) {
        B15PAllStepDBDao b15PAllStepDBDao = getDaoSession().getB15PAllStepDBDao();
        if (b15PAllStepDBDao == null) return;
        Query<B15PAllStepDB> query = b15PAllStepDBDao.queryBuilder()
                .where(B15PAllStepDBDao.Properties.DevicesMac.eq(mac),
                        B15PAllStepDBDao.Properties.StepDataTime.eq(timeDate)).build();
        if (query == null) return;
        List<B15PAllStepDB> list = query.list();
        if (list != null && list.size() > 0) {
            for (B15PAllStepDB foodBean : list) {
//                String stepDataTime = foodBean.getStepDataTime();
//                if (stepDataTime.equals(WatchUtils.getCurrentDate()))
                b15PAllStepDBDao.delete(foodBean);
            }
        }
    }

    /**
     * 删除某一天睡眠数据
     *
     * @param mac
     * @param timeDate
     */
    private void deleteTodaySleepData(String mac, String timeDate) {
        B15PSleepDBDao b15PSleepDBDao = getDaoSession().getB15PSleepDBDao();
        if (b15PSleepDBDao == null) return;
        Query<B15PSleepDB> query = b15PSleepDBDao.queryBuilder()
                .where(B15PSleepDBDao.Properties.DevicesMac.eq(mac),
                        B15PSleepDBDao.Properties.SleepData.eq(timeDate)).build();
        if (query == null) return;
        List<B15PSleepDB> list = query.list();
        if (list != null && list.size() > 0) {
            for (B15PSleepDB foodBean : list) {
//                String stepDataTime = foodBean.getStepDataTime();
//                if (stepDataTime.equals(WatchUtils.getCurrentDate()))
                b15PSleepDBDao.delete(foodBean);
            }
        }
    }

    /**
     * 删除某一天心率数据
     *
     * @param mac
     * @param timeDate
     */
    private void deleteTodayHeartData(String mac, String timeDate) {
        B15PHeartDBDao b15PHeartDBDao = getDaoSession().getB15PHeartDBDao();
        if (b15PHeartDBDao == null) return;
        Query<B15PHeartDB> query = b15PHeartDBDao.queryBuilder()
                .where(B15PHeartDBDao.Properties.DevicesMac.eq(mac),
                        B15PHeartDBDao.Properties.HeartData.eq(timeDate)).build();
        if (query == null) return;
        List<B15PHeartDB> list = query.list();
        if (list != null && list.size() > 0) {
            for (B15PHeartDB foodBean : list) {
//                String stepDataTime = foodBean.getStepDataTime();
//                if (stepDataTime.equals(WatchUtils.getCurrentDate()))
                b15PHeartDBDao.delete(foodBean);
            }
        }
    }

    /**
     * 删除某一天血压数据
     *
     * @param mac
     */
    private void deleteTodayBloodData(String mac) {
        B15PTestBloopDBDao b15PTestBloopDBDao = getDaoSession().getB15PTestBloopDBDao();
        if (b15PTestBloopDBDao == null) return;
        Query<B15PTestBloopDB> query = b15PTestBloopDBDao.queryBuilder()
                .where(B15PTestBloopDBDao.Properties.DevicesMac.eq(mac)).build();
        if (query == null) return;
        List<B15PTestBloopDB> list = query.list();
        if (list != null && list.size() > 0) {
            for (B15PTestBloopDB foodBean : list) {
                b15PTestBloopDBDao.delete(foodBean);
            }
        }
    }

}
