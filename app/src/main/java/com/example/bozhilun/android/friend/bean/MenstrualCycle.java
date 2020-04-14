package com.example.bozhilun.android.friend.bean;

/**
 * 女性生理期bean
 * Created by Admin
 * Date 2020/3/2
 */
public class MenstrualCycle {


    /**
     * id : 1
     * userId : 5c2b58f0681547a0801d4d4ac8465f82
     * cycleDays : 25
     * openReminder : 1
     * lastMensesDay : 2020-02-25
     * durationDays : 5
     * nextPlanDay : 11
     * cycleStatus : 2
     */

    private int id;
    private String userId;
    private int cycleDays;
    private int openReminder;
    private String lastMensesDay;
    private int durationDays;
    private String nextPlanDay;
    private int cycleStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(int cycleDays) {
        this.cycleDays = cycleDays;
    }

    public int getOpenReminder() {
        return openReminder;
    }

    public void setOpenReminder(int openReminder) {
        this.openReminder = openReminder;
    }

    public String getLastMensesDay() {
        return lastMensesDay;
    }

    public void setLastMensesDay(String lastMensesDay) {
        this.lastMensesDay = lastMensesDay;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public String getNextPlanDay() {
        return nextPlanDay;
    }

    public void setNextPlanDay(String nextPlanDay) {
        this.nextPlanDay = nextPlanDay;
    }

    public int getCycleStatus() {
        return cycleStatus;
    }

    public void setCycleStatus(int cycleStatus) {
        this.cycleStatus = cycleStatus;
    }

    @Override
    public String toString() {
        return "MenstrualCycle{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", cycleDays=" + cycleDays +
                ", openReminder=" + openReminder +
                ", lastMensesDay='" + lastMensesDay + '\'' +
                ", durationDays=" + durationDays +
                ", nextPlanDay='" + nextPlanDay + '\'' +
                ", cycleStatus=" + cycleStatus +
                '}';
    }
}
