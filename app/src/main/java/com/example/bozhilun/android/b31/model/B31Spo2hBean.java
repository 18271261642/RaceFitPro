package com.example.bozhilun.android.b31.model;


import org.litepal.crud.LitePalSupport;

/**
 * B31血氧的实体类
 * Created by Admin
 * Date 2018/12/24
 */
public class B31Spo2hBean extends LitePalSupport {

    /**
     * 日期
     */
    private String dateStr;

    /**
     * mac地址
     */
    private String bleMac;

    /**
     * 血氧的数据
     */
    private String spo2hOriginData;





    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }

    public String getSpo2hOriginData() {
        return spo2hOriginData;
    }

    public void setSpo2hOriginData(String spo2hOriginData) {
        this.spo2hOriginData = spo2hOriginData;
    }

    @Override
    public String toString() {
        return "B31Spo2hBean{" +
                "dateStr='" + dateStr + '\'' +
                ", bleMac='" + bleMac + '\'' +
                ", spo2hOriginData='" + spo2hOriginData + '\'' +
                '}';
    }
}
