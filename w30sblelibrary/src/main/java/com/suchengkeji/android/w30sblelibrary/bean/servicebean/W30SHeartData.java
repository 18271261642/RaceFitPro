package com.suchengkeji.android.w30sblelibrary.bean.servicebean;

import java.io.Serializable;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/8 18:08
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHeartData implements Serializable {
    private String date;//日期
    private List wo_heart_data;//数据

    public W30SHeartData(String date, List wo_heart_data) {
        this.date = date;
        this.wo_heart_data = wo_heart_data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List getWo_heart_data() {
        return wo_heart_data;
    }

    public void setWo_heart_data(List wo_heart_data) {
        this.wo_heart_data = wo_heart_data;
    }

    @Override
    public String toString() {
        return "W30SHeartData{" +
                "date='" + date + '\'' +
                ", wo_heart_data=" + wo_heart_data +
                '}';
    }
}
