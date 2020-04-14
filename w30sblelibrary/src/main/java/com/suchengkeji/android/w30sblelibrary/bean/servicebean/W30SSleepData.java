package com.suchengkeji.android.w30sblelibrary.bean.servicebean;

import java.io.Serializable;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/8 17:58
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SSleepData   implements Serializable {
    private String sleepData;//日期
    List<W30S_SleepDataItem> sleepDataList;//数据

    public W30SSleepData(String sleepData, List<W30S_SleepDataItem> sleepDataList) {
        this.sleepData = sleepData;
        this.sleepDataList = sleepDataList;
    }

    public String getSleepData() {
        return sleepData;
    }

    public void setSleepData(String sleepData) {
        this.sleepData = sleepData;
    }

    public List<W30S_SleepDataItem> getSleepDataList() {
        return sleepDataList;
    }

    public void setSleepDataList(List<W30S_SleepDataItem> sleepDataList) {
        this.sleepDataList = sleepDataList;
    }

    @Override
    public String toString() {
        return "W30SSleepData{" +
                "sleepData='" + sleepData + '\'' +
                ", sleepDataList=" + sleepDataList +
                '}';
    }
}
