package com.example.bozhilun.android.w30s.bean;

public class W30HeartBean {
    private String times;
    private int heartValues;

    public W30HeartBean(String times, int heartValues) {
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

    @Override
    public String toString() {
        return "W30HeartBean{" +
                "times='" + times + '\'' +
                ", heartValues=" + heartValues +
                '}';
    }
}
