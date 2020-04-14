package com.example.bozhilun.android.xwatch.ble;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2020/2/22
 */
public class XWatchSportDetailBean extends LitePalSupport {

    private String bleMac ;

    private String dayStr;

    private String sportDetailStr;

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getSportDetailStr() {
        return sportDetailStr;
    }

    public void setSportDetailStr(String sportDetailStr) {
        this.sportDetailStr = sportDetailStr;
    }

    @Override
    public String toString() {
        return "XWatchSportDetailBean{" +
                "bleMac='" + bleMac + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", sportDetailStr='" + sportDetailStr + '\'' +
                '}';
    }
}
