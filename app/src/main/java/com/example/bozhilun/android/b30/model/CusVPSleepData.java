package com.example.bozhilun.android.b30.model;


import com.veepoo.protocol.util.b;
import com.veepoo.protocol.util.c;

/**
 * 对接维亿魄的睡眠实体类
 * Created by Admin
 * Date 2019/8/16
 */
public class CusVPSleepData implements Comparable<Object>{

    /**
     * 日期 yyyy-MM-dd格式
     */
    public String Date;
    public int cali_flag;
    public int sleepQulity;
    public int wakeCount;
    public int deepSleepTime;
    public int lowSleepTime;
    public int allSleepTime;
    public String sleepLine;
    public CusVPTimeData sleepDown;
    public CusVPTimeData sleepUp;


    public CusVPSleepData() {
    }

    /***
     * 睡眠数据
     *
     * @param cali_flag     睡眠定标值，目前这个值没有什么用
     * @param sleepQulity   睡眠质量
     * @param wakeCount     睡眠中起床的次数
     * @param deepSleepTime 深睡时长
     * @param lowSleepTime  浅睡时长
     * @param allSleepTime  睡眠总时长
     * @param sleepLine      获取睡眠曲线，主要用于更具象化的UI来显示睡眠状态（可参考我司APP,360应用市场搜索Hband），如果您睡眠界面对UI没有特殊要求，可不理会,睡眠曲线分为普通睡眠和精准睡眠，普通睡眠是一组由0,1,2组成的字符串，每一个字符代表时长为5分钟，其中0表示浅睡，1表示深睡，2表示苏醒,比如“201112”，长度为6，表示睡眠阶段共30分钟，头尾各苏醒5分钟，中间浅睡5分钟，深睡15分钟;若是精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒。
     * @param sleepDown     入睡时间
     * @param sleepUp       起床时间
     */
    public CusVPSleepData(String date, int cali_flag, int sleepQulity, int wakeCount, int deepSleepTime, int lowSleepTime, int allSleepTime, String sleepLine, CusVPTimeData sleepDown, CusVPTimeData sleepUp) {
        Date = date;
        this.cali_flag = cali_flag;
        this.sleepQulity = sleepQulity;
        this.wakeCount = wakeCount;
        this.deepSleepTime = deepSleepTime;
        this.lowSleepTime = lowSleepTime;
        this.allSleepTime = allSleepTime;
        this.sleepLine = sleepLine;
        this.sleepDown = sleepDown;
        this.sleepUp = sleepUp;
    }

    public static String getCusVPSleepDate(CusVPTimeData time) {
        String date = time.getDateForDb();
        if (time.getHour() < 8) {
            date = c.a(date, -1);
        }

        return date;
    }



    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getCali_flag() {
        return cali_flag;
    }

    public void setCali_flag(int cali_flag) {
        this.cali_flag = cali_flag;
    }

    public int getSleepQulity() {
        return sleepQulity;
    }

    public void setSleepQulity(int sleepQulity) {
        this.sleepQulity = sleepQulity;
    }

    public int getWakeCount() {
        return wakeCount;
    }

    public void setWakeCount(int wakeCount) {
        this.wakeCount = wakeCount;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getLowSleepTime() {
        return lowSleepTime;
    }

    public void setLowSleepTime(int lowSleepTime) {
        this.lowSleepTime = lowSleepTime;
    }

    public int getAllSleepTime() {
        return allSleepTime;
    }

    public void setAllSleepTime(int allSleepTime) {
        this.allSleepTime = allSleepTime;
    }

    public String getSleepLine() {
        return sleepLine;
    }

    public void setSleepLine(String sleepLine) {
        this.sleepLine = sleepLine;
    }

    public CusVPTimeData getSleepDown() {
        return sleepDown;
    }

    public void setSleepDown(CusVPTimeData sleepDown) {
        this.sleepDown = sleepDown;
    }

    public CusVPTimeData getSleepUp() {
        return sleepUp;
    }

    public void setSleepUp(CusVPTimeData sleepUp) {
        this.sleepUp = sleepUp;
    }

    @Override
    public int compareTo(Object another) {
        int state = 0;
        String time = this.sleepDown.getDateAndClockForDb();
        if (another instanceof CusVPSleepData) {
            CusVPSleepData sleepData = (CusVPSleepData)another;
            state = time.compareTo(sleepData.getSleepDown().getDateAndClockForDb());
        }

        return state;
    }

    @Override
    public String toString() {
        return "CusVPSleepData{" +
                "Date='" + Date + '\'' +
                ", cali_flag=" + cali_flag +
                ", sleepQulity=" + sleepQulity +
                ", wakeCount=" + wakeCount +
                ", deepSleepTime=" + deepSleepTime +
                ", lowSleepTime=" + lowSleepTime +
                ", allSleepTime=" + allSleepTime +
                ", sleepLine='" + sleepLine + '\'' +
                ", sleepDown=" + sleepDown.toString() +
                ", sleepUp=" + sleepUp.toString() +
                '}';
    }
}
