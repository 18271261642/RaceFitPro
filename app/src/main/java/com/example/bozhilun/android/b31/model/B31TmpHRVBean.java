package com.example.bozhilun.android.b31.model;

import com.veepoo.protocol.model.datas.HRVOriginData;

/**
 * Created by Admin
 * Date 2018/12/22
 */
public class B31TmpHRVBean {


    private String mTmpTime;

    private HRVOriginData hrvOriginData;


    public String getmTmpTime() {
        return mTmpTime;
    }

    public void setmTmpTime(String mTmpTime) {
        this.mTmpTime = mTmpTime;
    }

    public HRVOriginData getHrvOriginData() {
        return hrvOriginData;
    }

    public void setHrvOriginData(HRVOriginData hrvOriginData) {
        this.hrvOriginData = hrvOriginData;
    }

    @Override
    public String toString() {
        return "B31TmpHRVBean{" +
                "mTmpTime='" + mTmpTime + '\'' +
                ", hrvOriginData=" + hrvOriginData +
                '}';
    }
}
