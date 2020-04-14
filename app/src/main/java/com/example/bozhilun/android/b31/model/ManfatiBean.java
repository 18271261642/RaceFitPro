package com.example.bozhilun.android.b31.model;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2019/1/8
 */
public class ManfatiBean extends LitePalSupport {

    /**
     * 日期
     */
    private String timeStr;


    private int status;

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ManfatiBean{" +
                "timeStr='" + timeStr + '\'' +
                ", status=" + status +
                '}';
    }
}
