package com.suchengkeji.android.w30sblelibrary.bean;


import org.litepal.crud.LitePalSupport;
import java.io.Serializable;

/**
 * 睡眠数据信息
 */
public class W30S_SleepData extends LitePalSupport implements Serializable{

    private String sleep_type;
    private String startTime;

    /**
     * @return 睡眠类型（0x00:熬夜，0x01:进入睡眠，0x02:浅睡眠，0x03:熟睡，0x04:睡醒，0x05:退出睡眠）
     */
    public String getSleep_type() {
        return sleep_type;
    }

    /**
     * @param sleep_type 睡眠类型（0x00:熬夜，0x01:进入睡眠，0x02:浅睡眠，0x03:熟睡，0x04:睡醒，0x05:退出睡眠）
     */
    public void setSleep_type(String sleep_type) {
        this.sleep_type = sleep_type;
    }

    /**
     * @return 开始时间
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 开始时间
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    /**
     * @param type 睡眠类型
     * @param time 开始时间
     */
    public W30S_SleepData(String type, String time) {
        super();
        setSleep_type(type);
        setStartTime(time);
    }


    @Override
    public String toString() {
        return "W30S_SleepData{" +
                "sleep_type='" + sleep_type + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }

    public W30S_SleepData() {
        super();
    }
}