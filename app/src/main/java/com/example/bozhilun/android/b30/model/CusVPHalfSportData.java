package com.example.bozhilun.android.b30.model;

/**
 * 再封装维亿魄半小时运动详细数据
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPHalfSportData {
    public String date;
    public CusVPTimeData time;
    public int stepValue;
    public int sportValue;
    double disValue;
    double calValue;

    /**
     * 半小时的计步数据
     *
     * @param time       计步的具体时间
     * @param stepValue  30分钟内的总计步数
     * @param sportValue 30分钟内的总运动量
     */
    public CusVPHalfSportData(CusVPTimeData time, int stepValue, int sportValue, double disValue, double calValue) {
        this.date = time.getDateForDb();
        this.time = time;
        this.stepValue = stepValue;
        this.sportValue = sportValue;
        this.disValue = disValue;
        this.calValue = calValue;
    }

    /**
     * 获取计步所属的日期，格式为yyyy-mm-dd
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * 设置计步所属的日期，格式为yyyy-mm-dd
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取计步的具体时间，最多可以准确到分钟,10:00表示的是10:00-10:30这段区间的均值
     */
    public CusVPTimeData getTime() {
        return time;
    }

    /**
     * 设置计步的具体时间，最多可以准确到分钟,10:00表示的是10:00-10:30这段区间的均值
     */
    public void setTime(CusVPTimeData time) {
        this.time = time;
    }

    /**
     * 获取30分钟内的总计步数
     */
    public int getStepValue() {
        return stepValue;
    }

    /**
     * 设置30分钟内的总计步数
     */
    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }

    /**
     * 获取30分钟内的总运动量
     */

    public int getSportValue() {
        return sportValue;
    }

    /**
     * 设置30分钟内的总运动量
     */
    public void setSportValue(int sportValue) {
        this.sportValue = sportValue;
    }

    /**
     * 获取30分钟内的总距离
     */
    public double getDisValue() {
        return disValue;
    }

    /**
     * 设置30分钟内的总距离
     */
    public void setDisValue(double disValue) {
        this.disValue = disValue;
    }

    /**
     * 获取30分钟内的总卡路里
     */
    public double getCalValue() {
        return calValue;
    }

    /**
     * 设置30分钟内的总卡路里
     */
    public void setCalValue(double calValue) {
        this.calValue = calValue;
    }

    @Override
    public String toString() {
        return "CusVPHalfSportData{" +
                "date='" + date + '\'' +
                ", time=" + time +
                ", stepValue=" + stepValue +
                ", sportValue=" + sportValue +
                ", disValue=" + disValue +
                ", calValue=" + calValue +
                '}';
    }
}
