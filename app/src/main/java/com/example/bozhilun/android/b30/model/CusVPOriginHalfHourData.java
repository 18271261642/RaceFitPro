package com.example.bozhilun.android.b30.model;

import java.util.List;

/**
 * 半小时汇总详细数据
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPOriginHalfHourData {

    List<CusVPHalfRateData> halfHourRateDatas;
    List<CusVPHalfHourBpData> halfHourBps;
    List<CusVPHalfSportData> halfHourSportDatas;
    int allStep;


    public List<CusVPHalfRateData> getHalfHourRateDatas() {
        return halfHourRateDatas;
    }

    public void setHalfHourRateDatas(List<CusVPHalfRateData> halfHourRateDatas) {
        this.halfHourRateDatas = halfHourRateDatas;
    }

    public List<CusVPHalfHourBpData> getHalfHourBps() {
        return halfHourBps;
    }

    public void setHalfHourBps(List<CusVPHalfHourBpData> halfHourBps) {
        this.halfHourBps = halfHourBps;
    }

    public List<CusVPHalfSportData> getHalfHourSportDatas() {
        return halfHourSportDatas;
    }

    public void setHalfHourSportDatas(List<CusVPHalfSportData> halfHourSportDatas) {
        this.halfHourSportDatas = halfHourSportDatas;
    }

    public int getAllStep() {
        return allStep;
    }

    public void setAllStep(int allStep) {
        this.allStep = allStep;
    }

    @Override
    public String toString() {
        return "CusVPOriginHalfHourData{" +
                "halfHourRateDatas=" + halfHourRateDatas +
                ", halfHourBps=" + halfHourBps +
                ", halfHourSportDatas=" + halfHourSportDatas +
                ", allStep=" + allStep +
                '}';
    }
}
