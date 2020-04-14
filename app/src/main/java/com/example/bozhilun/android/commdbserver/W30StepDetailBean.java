package com.example.bozhilun.android.commdbserver;

/**
 * Created by Admin
 * Date 2019/3/19
 * 步数详细内容bean
 */
public class W30StepDetailBean {

    private String stepDate;
    private String stepValue;

    public W30StepDetailBean() {
    }

    public W30StepDetailBean(String stepDate, String stepValue) {
        this.stepDate = stepDate;
        this.stepValue = stepValue;
    }

    public String getStepDate() {
        return stepDate;
    }

    public void setStepDate(String stepDate) {
        this.stepDate = stepDate;
    }

    public String getStepValue() {
        return stepValue;
    }

    public void setStepValue(String stepValue) {
        this.stepValue = stepValue;
    }

    @Override
    public String toString() {
        return "W30StepDetailBean{" +
                "stepDate='" + stepDate + '\'' +
                ", stepValue='" + stepValue + '\'' +
                '}';
    }
}
