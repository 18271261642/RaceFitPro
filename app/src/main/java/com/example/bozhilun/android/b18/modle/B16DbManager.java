package com.example.bozhilun.android.b18.modle;

import android.util.Log;
import org.litepal.LitePal;
import java.util.List;


/**
 * Created by Admin
 * Date 2019/12/30
 */
public class B16DbManager {

    private static  B16DbManager b16DbManager = null;

    private B16DbManager() {
    }

    public static B16DbManager getB16DbManager(){
        synchronized (B16DbManager.class){
            if(b16DbManager == null){
                b16DbManager = new B16DbManager();
            }
        }

        return b16DbManager;
    }


    //保存换算步频的数据
    public void saveB16Cadence(B16CadenceResultBean b16CadenceBean){
        String where = "bleMac = ? and paramsDateStr = ?";
        boolean isSaved = b16CadenceBean.saveOrUpdate(where,b16CadenceBean.getBleMac(),b16CadenceBean.getParamsDateStr());
        Log.e("B16","----------是否保存成功="+isSaved);

    }


    //查询历史数据，一天可能会有多条数据
    public List<B16CadenceResultBean> findB16CadenceHistory(String bleMac,String dayStr){
        String where = "bleMac = ? and currDayStr = ?";
        List<B16CadenceResultBean> tmpList = LitePal.where(where,bleMac,dayStr).find(B16CadenceResultBean.class);
        //List<B16CadenceResultBean> tmpList = LitePal.findAll(B16CadenceResultBean.class);
        return tmpList == null || tmpList.isEmpty() ? null : tmpList;
    }




}
