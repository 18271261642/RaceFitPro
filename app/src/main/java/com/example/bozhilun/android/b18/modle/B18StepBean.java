package com.example.bozhilun.android.b18.modle;

/**
 * Created by Admin
 * Date 2019/11/11
 */
public class B18StepBean
{


    /**
     * heart : 0
     * step : 0
     * sequence : 81
     * DBP : 0
     * SBP : 0
     */

    private int heart;
    private int step;
    private int sequence;
    private int DBP;
    private int SBP;

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getDBP() {
        return DBP;
    }

    public void setDBP(int DBP) {
        this.DBP = DBP;
    }

    public int getSBP() {
        return SBP;
    }

    public void setSBP(int SBP) {
        this.SBP = SBP;
    }

    @Override
    public String toString() {
        return "B18StepBean{" +
                "heart=" + heart +
                ", step=" + step +
                ", sequence=" + sequence +
                ", DBP=" + DBP +
                ", SBP=" + SBP +
                '}';
    }
}
