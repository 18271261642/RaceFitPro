package com.example.bozhilun.android.friend.views;

/**
 * 好友睡眠实体类
 * Created by Admin
 * Date 2019/7/25
 */
public class CusFriendBean {

    //睡眠类型
    private int type;

    //时长
    private int sleepTime;

    public CusFriendBean(int type, int sleepTime) {
        this.type = type;
        this.sleepTime = sleepTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "CusFriendBean{" +
                "type=" + type +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
