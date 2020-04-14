package com.example.bozhilun.android.b31.model;


import org.litepal.crud.LitePalSupport;


/**
 * Created by Admin
 * Date 2018/12/21
 */
public class B31HRVBean extends LitePalSupport {

    /**
     * 日期yyyy-MM-dd格式
     */
    private String dateStr;

    /**
     * 设备的mac地址
     */
    private String bleMac;

    //HRV的日期yyyy-MM-dd-HH:mm:ss
    //private String currHrvDate;

    /**
     * 设备的数据 com.veepoo.protocol.model.datas.HRVOriginData
     * HRV的对象转换成json格式的字符串
     */
    private String  hrvDataStr;


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

    public String getHrvDataStr() {
        return hrvDataStr;
    }

    public void setHrvDataStr(String hrvDataStr) {
        this.hrvDataStr = hrvDataStr;
    }


    @Override
    public String toString() {
        return "B31HRVBean{" +
                "dateStr='" + dateStr + '\'' +
                ", bleMac='" + bleMac + '\'' +
                ", hrvDataStr='" + hrvDataStr + '\'' +
                '}';
    }
}
