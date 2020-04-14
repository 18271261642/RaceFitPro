package com.example.bozhilun.android.w30s.bean;

import android.util.Log;
import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;
import org.litepal.crud.LitePalSupport;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/20 08:49
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SAlarmClockBean extends LitePalSupport {

    private int id;
    private int hour;
    private int min;
    private int datas;
    private int alarmWeek;
    private int status;
    public List<W30S_AlarmInfo> alarmInfoList;

    public List<W30S_AlarmInfo> getAlarmInfoList() {
        return alarmInfoList;
    }

    public void setAlarmInfoList(List<W30S_AlarmInfo> alarmInfoList) {
        this.alarmInfoList = alarmInfoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getDatas() {
        return datas;
    }

    public void setDatas(int datas) {
        this.datas = datas;
    }

    public int getAlarmWeek() {
        return alarmWeek;
    }

    public void setAlarmWeek(int alarmWeek) {
        this.alarmWeek = alarmWeek;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AlarmClockBean{" +
                "id=" + id +
                ", hour=" + hour +
                ", min=" + min +
                ", datas=" + datas +
                ", alarmWeek=" + alarmWeek +
                ", status=" + status +
                '}';
    }

    public W30S_AlarmInfo w30AlarmInfoChange(int ids) {
        W30S_AlarmInfo w30S_alarmInfo = new W30S_AlarmInfo();
        w30S_alarmInfo.setAlarmId(ids);
        String alarmtDataString = Integer.toBinaryString(datas);
        String s = status + alarmtDataString.substring(1, alarmtDataString.length());
        //二进制转十进制
        Integer integer = Integer.valueOf(s, 2);
        w30S_alarmInfo.setAlarmtData(integer);
        w30S_alarmInfo.setAlarmtHour(hour);
        w30S_alarmInfo.setAlarmtMin(min);
        Log.d("-----<<<W30>>>", "------我的-闹钟数据-------" + "\n   id:" + id + "\n   hour:" + hour + "\n    min:" + min + "\n   data" + integer);
        return w30S_alarmInfo;
    }

}
