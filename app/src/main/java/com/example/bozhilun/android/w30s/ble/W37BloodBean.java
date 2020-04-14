package com.example.bozhilun.android.w30s.ble;

import java.util.List;

/**
 * W37血压数据实体类
 * Created by Admin
 * Date 2019/7/16
 */
public class W37BloodBean {

    private String dateStr;

    private List<Integer> bloodList;

    public W37BloodBean(String dateStr, List<Integer> bloodList) {
        this.dateStr = dateStr;
        this.bloodList = bloodList;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public List<Integer> getBloodList() {
        return bloodList;
    }

    public void setBloodList(List<Integer> bloodList) {
        this.bloodList = bloodList;
    }

    @Override
    public String toString() {
        return "W37BloodBean{" +
                "dateStr='" + dateStr + '\'' +
                ", bloodList=" + bloodList +
                '}';
    }
}
