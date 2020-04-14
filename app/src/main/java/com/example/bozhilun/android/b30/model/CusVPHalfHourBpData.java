package com.example.bozhilun.android.b30.model;

/**
 * 再封装维亿魄半小时血压详细数据
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPHalfHourBpData {

    private String date;
    private CusVPTimeData time;
    private int highValue;
    private int lowValue;

    /**
     * 半小时的血压数据
     *
     * @param time
     * @param highValue
     * @param lowValue
     */
    public CusVPHalfHourBpData(CusVPTimeData time, int highValue, int lowValue) {
        this.date = time.getDateForDb();
        this.time = time;
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    public CusVPHalfHourBpData(String date, CusVPTimeData time, int highValue, int lowValue) {
        this.date = date;
        this.time = time;
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    /**
     * 获取血压所属的日期，格式为yyyy-mm-dd
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置血压所属的日期，格式为yyyy-mm-dd
     *
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取血压的具体时间，最多可以准确到分钟,10:00表示的是10:00-10:30这段区间的均值
     */
    public CusVPTimeData getTime() {
        return time;
    }

    /**
     * 设置血压的具体时间，最多可以准确到分钟,10:00表示的是10:00-10:30这段区间的均值
     */
    public void setTime(CusVPTimeData time) {
        this.time = time;
    }

    /**
     * 获取30分钟内的高压均值[60-300]
     */
    public int getHighValue() {
        return highValue;
    }

    /**
     * 设置30分钟内的高压均值[60-300]
     */
    public void setHighValue(int highValue) {
        this.highValue = highValue;
    }

    /**
     * 获取30分钟内的低压均值[20-200]
     */
    public int getLowValue() {
        return lowValue;
    }

    /**
     * 设置30分钟内的低压均值[20-200]
     */
    public void setLowValue(int lowValue) {
        this.lowValue = lowValue;
    }

    @Override
    public String toString() {
        return "CusVPHalfHourBpData{" +
                "date='" + date + '\'' +
                ", time=" + time +
                ", highValue=" + highValue +
                ", lowValue=" + lowValue +
                '}';
    }
}
