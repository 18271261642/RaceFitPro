package com.example.bozhilun.android.b30.bean;


import org.litepal.crud.LitePalSupport;

import java.util.Date;


/**
 * Created by Administrator on 2018/8/17.
 */

public class B30Bean extends LitePalSupport {
    private int id;
    /**
     * 自定义参数,日期
     */
    private Date date;
    /**
     * 当前手环步数的汇总(四个参数)
     */
    private String sportDataStr;
    /**
     * 五分钟/条的原始数据
     */
    private String originDataStr;

    /**
     * 30分钟一条的原始数据(返回30分钟的原始数据，数据来自5分钟原始数据，只是在内部进行了处理后返回)
     */
    private String halfHourDataStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 自定义参数,日期
     */
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 当前手环步数的汇总(四个参数)
     */
    public String getSportDataStr() {
        return sportDataStr;
    }

    public void setSportDataStr(String sportDataStr) {
        this.sportDataStr = sportDataStr;
    }

    /**
     * 五分钟/条的原始数据
     */
    public String getOriginDataStr() {
        return originDataStr;
    }

    public void setOriginDataStr(String originDataStr) {
        this.originDataStr = originDataStr;
    }

    /**
     * 30分钟一条的原始数据(返回30分钟的原始数据，数据来自5分钟原始数据，只是在内部进行了处理后返回)
     */
    public String getHalfHourDataStr() {
        return halfHourDataStr;
    }

    public void setHalfHourDataStr(String halfHourDataStr) {
        this.halfHourDataStr = halfHourDataStr;
    }

    @Override
    public String toString() {
        return "B30Bean{" +
                "id=" + id +
                ", date=" + date +
                ", sportDataStr='" + sportDataStr + '\'' +
                ", originDataStr='" + originDataStr + '\'' +
                ", halfHourDataStr='" + halfHourDataStr + '\'' +
                '}';
    }
}
