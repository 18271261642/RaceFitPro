package com.suchengkeji.android.w30sblelibrary.bean.servicebean;


import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/15 09:36
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30S_SleepDataItem extends LitePalSupport implements Serializable {
    private String sleep_type;
    private String startTime;

    public W30S_SleepDataItem() {
    }

    public W30S_SleepDataItem(String sleep_type, String startTime) {
        this.sleep_type = sleep_type;
        this.startTime = startTime;
    }

    public String getSleep_type() {
        return sleep_type;
    }

    public void setSleep_type(String sleep_type) {
        this.sleep_type = sleep_type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "W30S_SleepData{" +
                "sleep_type='" + sleep_type + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
