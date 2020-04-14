package com.example.bozhilun.android.w30s.bean;

public class HeartBean {
    private String times;
    private int heartValues;

    public HeartBean(String times, int heartValues) {
        this.times = times;
        this.heartValues = heartValues;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public int getHeartValues() {
        return heartValues;
    }

    public void setHeartValues(int heartValues) {
        this.heartValues = heartValues;
    }
}
