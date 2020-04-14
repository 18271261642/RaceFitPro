package com.example.bozhilun.android.b30.model;

/**
 * 再封装维亿魄半小时心率详细数据
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPHalfRateData {

    private String date;
    private CusVPTimeData time;
    private int rateValue;
    private int ecgCount = 0;
    private int ppgCount = 0;

    /**
     * 心率数据
     *
     * @param time 所属日期，日期格式为 yyyy-mm-dd
     * @param rate 30分钟内心率均值,范围[30-200]
     */
    public CusVPHalfRateData(CusVPTimeData time, int rate) {
        this.time = time;
        this.date = time.getDateForDb();
        rateValue = rate;
    }

    public CusVPHalfRateData(String date, CusVPTimeData time, int rateValue, int ecgCount, int ppgCount) {
        this.date = date;
        this.time = time;
        this.rateValue = rateValue;
        this.ecgCount = ecgCount;
        this.ppgCount = ppgCount;
    }

    public int getEcgCount() {
        return ecgCount;
    }

    public void setEcgCount(int ecgCount) {
        this.ecgCount = ecgCount;
    }

    public int getPpgCount() {
        return ppgCount;
    }

    public void setPpgCount(int ppgCount) {
        this.ppgCount = ppgCount;
    }

    /**
     * 获取所属日期，日期格式为 yyyy-mm-dd
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置所属日期，日期格式为 yyyy-mm-dd
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值
     *
     * @return
     */
    public CusVPTimeData getTime() {
        return time;
    }

    /**
     * 设置具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值
     */
    public void setTime(CusVPTimeData time) {
        this.time = time;
    }

    /**
     * 获取30分钟内心率均值,范围[30-200]
     *
     * @return
     */
    public int getRateValue() {
        return rateValue;
    }

    /**
     * 设置30分钟内心率均值,范围[30-200]
     */
    public void setRateValue(int rateValue) {
        this.rateValue = rateValue;
    }

}
