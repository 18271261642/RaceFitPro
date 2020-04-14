package com.example.bozhilun.android.b18.modle;

/**
 * B18久坐提醒的实体类，gson.tojson后保存在sp中
 * Created by Admin
 * Date 2019/11/22
 */
public class B18LongSitBean {

    //是否开启
    private boolean isOpen;
    //开始时间
    private int startHour;
    private int startMine;

    //结束时间
    private int endHour;
    private int endMine;
    //提醒间隔
    private int intevel;

    public B18LongSitBean() {
    }

    /**
     *
     * @param isOpen 是否开启
     * @param startHour 开始小时
     * @param startMine 开始分钟
     * @param endHour 结束小时
     * @param endMine 结束分钟
     * @param intevel 间隔
     */
    public B18LongSitBean(boolean isOpen, int startHour, int startMine, int endHour, int endMine, int intevel) {
        this.isOpen = isOpen;
        this.startHour = startHour;
        this.startMine = startMine;
        this.endHour = endHour;
        this.endMine = endMine;
        this.intevel = intevel;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMine() {
        return startMine;
    }

    public void setStartMine(int startMine) {
        this.startMine = startMine;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMine() {
        return endMine;
    }

    public void setEndMine(int endMine) {
        this.endMine = endMine;
    }

    public int getIntevel() {
        return intevel;
    }

    public void setIntevel(int intevel) {
        this.intevel = intevel;
    }

    @Override
    public String toString() {
        return "B18LongSitBean{" +
                "isOpen=" + isOpen +
                ", startHour=" + startHour +
                ", startMine=" + startMine +
                ", endHour=" + endHour +
                ", endMine=" + endMine +
                ", intevel=" + intevel +
                '}';
    }
}
