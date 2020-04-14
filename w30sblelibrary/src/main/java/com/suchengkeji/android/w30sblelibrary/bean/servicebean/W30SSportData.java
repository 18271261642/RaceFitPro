package com.suchengkeji.android.w30sblelibrary.bean.servicebean;

import java.io.Serializable;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/8 17:17
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SSportData  implements Serializable {
    private String data;//日期
    private int sportStep;//步数
    private float Calory;//卡路里
    private float Distance;//距离
    private List sport_data;//数据

    public W30SSportData(String data, int sportStep, float calory, float distance, List sport_data) {
        this.data = data;
        this.sportStep = sportStep;
        Calory = calory;
        Distance = distance;
        this.sport_data = sport_data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getSportStep() {
        return sportStep;
    }

    public void setSportStep(int sportStep) {
        this.sportStep = sportStep;
    }

    public float getCalory() {
        return Calory;
    }

    public void setCalory(float calory) {
        Calory = calory;
    }

    public float getDistance() {
        return Distance;
    }

    public void setDistance(float distance) {
        Distance = distance;
    }

    public List getSport_data() {
        return sport_data;
    }

    public void setSport_data(List sport_data) {
        this.sport_data = sport_data;
    }

    @Override
    public String toString() {
        return "W30SSportData{" +
                "data='" + data + '\'' +
                ", sportStep=" + sportStep +
                ", Calory=" + Calory +
                ", Distance=" + Distance +
                ", sport_data=" + sport_data +
                '}';
    }
}
