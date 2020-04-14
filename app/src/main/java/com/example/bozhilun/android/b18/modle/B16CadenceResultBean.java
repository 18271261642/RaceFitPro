package com.example.bozhilun.android.b18.modle;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2019/12/30
 */
public class B16CadenceResultBean extends LitePalSupport {

    private String bleMac;

    //日期 yyyy-MM-dd格式
    private String currDayStr;
    //详细时间，可用作key yyyy-MM-dd HH-mm-ss
    private String paramsDateStr;

    //运动时长 单位：秒
    private int sportDuration;

    //步数
    private int steps;

    //距离
    private int distance;

    //卡路里
    private int calorie;

    //运动详细点个数
    private int sportCount;

    //运动详细数据
    private String sportDetailStr;

    public B16CadenceResultBean() {
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }

    public String getCurrDayStr() {
        return currDayStr;
    }

    public void setCurrDayStr(String currDayStr) {
        this.currDayStr = currDayStr;
    }

    public String getParamsDateStr() {
        return paramsDateStr;
    }

    public void setParamsDateStr(String paramsDateStr) {
        this.paramsDateStr = paramsDateStr;
    }

    public int getSportDuration() {
        return sportDuration;
    }

    public void setSportDuration(int sportDuration) {
        this.sportDuration = sportDuration;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getSportCount() {
        return sportCount;
    }

    public void setSportCount(int sportCount) {
        this.sportCount = sportCount;
    }

    public String getSportDetailStr() {
        return sportDetailStr;
    }

    public void setSportDetailStr(String sportDetailStr) {
        this.sportDetailStr = sportDetailStr;
    }

    @Override
    public String toString() {
        return "B16CadenceResultBean{" +
                "bleMac='" + bleMac + '\'' +
                ", currDayStr='" + currDayStr + '\'' +
                ", paramsDateStr='" + paramsDateStr + '\'' +
                ", sportDuration=" + sportDuration +
                ", steps=" + steps +
                ", distance=" + distance +
                ", calorie=" + calorie +
                ", sportCount=" + sportCount +
                ", sportDetailStr='" + sportDetailStr + '\'' +
                '}';
    }
}
