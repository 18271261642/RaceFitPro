package com.example.bozhilun.android.xwatch.ble;

/**
 * Created by Admin
 * Date 2020/2/22
 */
public class XWatchStepBean {

    private int stepNumber;

    private double disance;

    private double kcal;

    private int posrtTime;

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public double getDisance() {
        return disance;
    }

    public void setDisance(double disance) {
        this.disance = disance;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public int getPosrtTime() {
        return posrtTime;
    }

    public void setPosrtTime(int posrtTime) {
        this.posrtTime = posrtTime;
    }

    @Override
    public String toString() {
        return "XWatchStepBean{" +
                "stepNumber=" + stepNumber +
                ", disance=" + disance +
                ", kcal=" + kcal +
                ", posrtTime=" + posrtTime +
                '}';
    }
}
