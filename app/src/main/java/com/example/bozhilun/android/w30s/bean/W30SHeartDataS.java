package com.example.bozhilun.android.w30s.bean;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/14 09:56
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHeartDataS {
    private String time;
    private int value;

    public W30SHeartDataS() {
    }



    public W30SHeartDataS(String time, int value) {
        this.time = time;
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
