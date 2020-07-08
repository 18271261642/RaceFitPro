package com.example.bozhilun.android.b30.model;

import com.veepoo.protocol.model.datas.InsomniaTimeData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.util.c;

import java.util.List;

/**
 * 维亿魄B31s精准睡眠
 * Created by Admin
 * Date 2019/9/5
 */
public class CusVPSleepPrecisionData extends CusVPSleepData {

    //睡眠标志
    private int sleepTag;
    //起夜得分
    private int getUpScore;
    //深睡夜得分
    private int deepScore;
    //睡眠效率得分，起夜到深睡的效率得分
    private int sleepEfficiencyScore;
    //入睡效率得分，从开始睡眠到第一次进入深睡的效率
    private int fallAsleepScore;
    //睡眠时长得分
    private int sleepTimeScore;
    //退出睡眠的模式
    private int exitSleepMode;
    //深浅睡眠模式
    private int deepAndLightMode;
    //其他睡眠时间长，单位分钟--快速眼动时长
    private int otherDuration;
    //第一次进入深睡时间
    private int firstDeepDuration;
    //起夜总时长，单位分钟
    private int getUpDuration;
    //起夜到深睡时间的平均值
    private int getUpToDeepAve;
    //曲线上一个点代表的时间，单位秒，现在是60s
    private int onePointDuration;
    //睡眠类型
    private int accurateType;
    //失眠标志
    private int insomniaTag;
    //失眠得分
    private int insomniaScore;
    //失眠次数
    private int insomniaTimes;
    //失眠长度
    private int insomniaLength;
    //失眠列表
    List<InsomniaTimeData> insomniaBeanList;
    //失眠开始时间
    private String startInsomniaTime;
    //失眠结束时间
    private String stopInsomniaTime;
    //通过睡眠曲线3的个数来计算,单位是分钟
    private int insomniaDuration;
    //睡眠原始字符串
    private String sleepSourceStr;
    // 上一段标志位，默认为0(表示肯定没有上一段)，如果为1，则表示有上一段睡眠,否则表示没有上一段睡眠
    private int laster;
    // 下一段睡眠标志位，默认为255(表示不确定是否有下一段)，如果为1，则表示有下一段睡眠,否则表示没有下一段睡眠
    private int next;

//    //睡眠表现形式
//    private String sleepLine;
//
//    @Override
//    public String getSleepLine() {
//        return sleepLine;
//    }
//
//    @Override
//    public void setSleepLine(String sleepLine) {
//        this.sleepLine = sleepLine;
//    }

    public CusVPSleepPrecisionData() {

    }

    public void setPresicionDate(CusVPTimeData sleepTime, CusVPTimeData wakeTime) {
        this.sleepDown = sleepTime;
        this.sleepUp = wakeTime;
        String sleepDate = sleepTime.getDateForDb();
        String wakeDate = wakeTime.getDateForDb();
        if (sleepDate.equals(wakeDate)) {
            if (sleepTime.getHour() >= 20) {
                this.Date = c.a(sleepDate, 1);
            } else {
                this.Date = sleepDate;
            }
        } else {
            this.Date = wakeDate;
        }
    }

    /**
     * 获取睡眠标志
     *
     * @return
     */
    public int getSleepTag() {
        return sleepTag;
    }

    /**
     * 设置睡眠标志
     *
     * @param sleepTag
     */
    public void setSleepTag(int sleepTag) {
        this.sleepTag = sleepTag;
    }

    /**
     * 获取起夜得分
     *
     * @return
     */
    public int getGetUpScore() {
        return getUpScore;
    }

    /**
     * 设置起夜得分
     *
     * @param getUpScore
     */
    public void setGetUpScore(int getUpScore) {
        this.getUpScore = getUpScore;
    }

    /**
     * 获取深睡夜得分
     *
     * @return
     */
    public int getDeepScore() {
        return deepScore;
    }

    /**
     * 设置深睡夜得分
     *
     * @param deepScore
     */
    public void setDeepScore(int deepScore) {
        this.deepScore = deepScore;
    }

    /**
     * 获取睡眠效率得分
     *
     * @return
     */
    public int getSleepEfficiencyScore() {
        return sleepEfficiencyScore;
    }

    /**
     * 设置睡眠效率得分
     *
     * @param sleepEfficiencyScore
     */
    public void setSleepEfficiencyScore(int sleepEfficiencyScore) {
        this.sleepEfficiencyScore = sleepEfficiencyScore;
    }

    /**
     * 获取入睡效率得分
     *
     * @return
     */
    public int getFallAsleepScore() {
        return fallAsleepScore;
    }

    /**
     * 设置入睡效率得分
     *
     * @param fallAsleepScore
     */
    public void setFallAsleepScore(int fallAsleepScore) {
        this.fallAsleepScore = fallAsleepScore;
    }

    /**
     * 获取睡眠时长得分
     *
     * @return
     */
    public int getSleepTimeScore() {
        return sleepTimeScore;
    }

    /**
     * 设置睡眠时长得分
     *
     * @param sleepTimeScore
     */
    public void setSleepTimeScore(int sleepTimeScore) {
        this.sleepTimeScore = sleepTimeScore;
    }

    /**
     * 获取退出睡眠的模式
     *
     * @return
     */
    public int getExitSleepMode() {
        return exitSleepMode;
    }

    /**
     * 设置退出睡眠的模式
     *
     * @param exitSleepMode
     */
    public void setExitSleepMode(int exitSleepMode) {
        this.exitSleepMode = exitSleepMode;
    }

    /**
     * 获取深浅睡眠模式
     *
     * @return
     */
    public int getDeepAndLightMode() {
        return deepAndLightMode;
    }

    /**
     * 设置深浅睡眠模式
     *
     * @param deepAndLightMode
     */
    public void setDeepAndLightMode(int deepAndLightMode) {
        this.deepAndLightMode = deepAndLightMode;
    }

    /**
     * 获取其他睡眠时间长
     *
     * @return
     */
    public int getOtherDuration() {
        return otherDuration;
    }

    /**
     * 设置其他睡眠时间长
     *
     * @param otherDuration
     */
    public void setOtherDuration(int otherDuration) {
        this.otherDuration = otherDuration;
    }

    /**
     * 获取第一次进入深睡时间
     *
     * @return
     */
    public int getFirstDeepDuration() {
        return firstDeepDuration;
    }

    /**
     * 设置第一次进入深睡时间
     *
     * @param firstDeepDuration
     */
    public void setFirstDeepDuration(int firstDeepDuration) {
        this.firstDeepDuration = firstDeepDuration;
    }

    /**
     * 获取起夜总时长
     *
     * @return
     */
    public int getGetUpDuration() {
        return getUpDuration;
    }

    /**
     * 设置起夜总时长
     *
     * @param getUpDuration
     */
    public void setGetUpDuration(int getUpDuration) {
        this.getUpDuration = getUpDuration;
    }

    /**
     * 获取起夜到深睡时间的平均值
     *
     * @return
     */
    public int getGetUpToDeepAve() {
        return getUpToDeepAve;
    }

    /**
     * 设置起夜到深睡时间的平均值
     *
     * @param getUpToDeepAve
     */
    public void setGetUpToDeepAve(int getUpToDeepAve) {
        this.getUpToDeepAve = getUpToDeepAve;
    }

    /**
     * 获取曲线上一个点代表的时间
     *
     * @return
     */
    public int getOnePointDuration() {
        return onePointDuration;
    }

    /**
     * 设置曲线上一个点代表的时间
     *
     * @param onePointDuration
     */
    public void setOnePointDuration(int onePointDuration) {
        this.onePointDuration = onePointDuration;
    }

    /**
     * 获取睡眠类型
     *
     * @return
     */
    public int getAccurateType() {
        return accurateType;
    }

    /**
     * 设置睡眠类型
     *
     * @param accurateType
     */
    public void setAccurateType(int accurateType) {
        this.accurateType = accurateType;
    }

    /**
     * 获取失眠标志
     *
     * @return
     */
    public int getInsomniaTag() {
        return insomniaTag;
    }

    /**
     * 设置失眠标志
     *
     * @param insomniaTag
     */
    public void setInsomniaTag(int insomniaTag) {
        this.insomniaTag = insomniaTag;
    }

    /**
     * 获取失眠得分
     *
     * @return
     */
    public int getInsomniaScore() {
        return insomniaScore;
    }

    /**
     * 设置失眠得分
     *
     * @param insomniaScore
     */
    public void setInsomniaScore(int insomniaScore) {
        this.insomniaScore = insomniaScore;
    }

    /**
     * 获取失眠次数
     *
     * @return
     */
    public int getInsomniaTimes() {
        return insomniaTimes;
    }

    /**
     * 设置失眠次数
     *
     * @param insomniaTimes
     */
    public void setInsomniaTimes(int insomniaTimes) {
        this.insomniaTimes = insomniaTimes;
    }

    /**
     * 获取失眠长度
     *
     * @return
     */
    public int getInsomniaLength() {
        return insomniaLength;
    }

    /**
     * 设置失眠长度
     *
     * @param insomniaLength
     */
    public void setInsomniaLength(int insomniaLength) {
        this.insomniaLength = insomniaLength;
    }

    /**
     * 获取失眠列表
     *
     * @return
     */
    public List<InsomniaTimeData> getInsomniaBeanList() {
        return insomniaBeanList;
    }

    /**
     * 设置失眠列表
     *
     * @param insomniaBeanList
     */
    public void setInsomniaBeanList(List<InsomniaTimeData> insomniaBeanList) {
        this.insomniaBeanList = insomniaBeanList;
    }

    /**
     * 获取失眠开始时间
     *
     * @return
     */
    public String getStartInsomniaTime() {
        return startInsomniaTime;
    }

    /**
     * 设置失眠开始时间
     *
     * @param startInsomniaTime
     */
    public void setStartInsomniaTime(String startInsomniaTime) {
        this.startInsomniaTime = startInsomniaTime;
    }

    /**
     * 获取失眠结束时间
     *
     * @return
     */
    public String getStopInsomniaTime() {
        return stopInsomniaTime;
    }

    /**
     * 设置失眠结束时间
     *
     * @param stopInsomniaTime
     */
    public void setStopInsomniaTime(String stopInsomniaTime) {
        this.stopInsomniaTime = stopInsomniaTime;
    }

    /**
     * 获取通过睡眠曲线3的个数来计算
     *
     * @return
     */
    public int getInsomniaDuration() {
        return insomniaDuration;
    }

    /**
     * 设置通过睡眠曲线3的个数来计算
     *
     * @param insomniaDuration
     */
    public void setInsomniaDuration(int insomniaDuration) {
        this.insomniaDuration = insomniaDuration;
    }

    /**
     * 获取睡眠原始字符串，仅用于我司睡眠分析
     *
     * @return
     */
    public String getSleepSourceStr() {
        return sleepSourceStr;
    }

    /**
     * 设置睡眠原始字符串
     *
     * @param sleepSourceStr
     */
    public void setSleepSourceStr(String sleepSourceStr) {
        this.sleepSourceStr = sleepSourceStr;
    }

    /**
     * 获取上一段标志位,默认为0，如果为1，则表示有上一段睡眠,否则表示没有上一段睡眠
     *
     * @return
     */
    public int getLaster() {
        return laster;
    }

    /**
     * 设置上一段标志位,默认为0，如果为1，则表示有上一段睡眠,否则表示没有上一段睡眠
     *
     * @param laster
     */
    public void setLaster(int laster) {
        this.laster = laster;
    }

    /**
     * 获取下一段睡眠标志位,默认为255，如果为1，则表示有下一段睡眠,否则表示没有下一段睡眠
     *
     * @return
     */
    public int getNext() {
        return next;
    }

    /**
     * 设置下一段睡眠标志位,默认为255，如果为1，则表示有下一段睡眠,否则表示没有下一段睡眠
     *
     * @param next
     */
    public void setNext(int next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "SleepPrecisionData{" +
                ", date=" + Date +
                ", deepSleepTime=" + deepSleepTime +
                ", lowSleepTime=" + lowSleepTime +
                ", allSleepTime=" + allSleepTime +
                ", sleepDown=" + sleepDown +
                ", sleepUp=" + sleepUp +
                ", wakeCount=" + wakeCount +
                ", sleepQulity=" + sleepQulity +
                ", cali_flag=" + cali_flag +
                ", sleepTag=" + sleepTag +
                ", getUpScore=" + getUpScore +
                ", deepScore=" + deepScore +
                ", sleepEfficiencyScore=" + sleepEfficiencyScore +
                ", fallAsleepScore=" + fallAsleepScore +
                ", sleepTimeScore=" + sleepTimeScore +
                ", exitSleepMode=" + exitSleepMode +
                ", deepAndLightMode=" + deepAndLightMode +
                ", otherDuration=" + otherDuration +
                ", firstDeepDuration=" + firstDeepDuration +
                ", getUpDuration=" + getUpDuration +
                ", getUpToDeepAve=" + getUpToDeepAve +
                ", onePointDuration=" + onePointDuration +
                ", accurateType=" + accurateType +
                ", insomniaTag=" + insomniaTag +
                ", insomniaScore=" + insomniaScore +
                ", insomniaTimes=" + insomniaTimes +
                ", insomniaLength=" + insomniaLength +
                ", insomniaBeanList=" + insomniaBeanList +
                ", startInsomniaTime=" + startInsomniaTime +
                ", stopInsomniaTime=" + stopInsomniaTime +
                ", sleepLine length=" + ((sleepLine == null) ? 0 : sleepLine.length()) +
                ", insomniaDuration=" + insomniaDuration +
                ", sleepSourceStr length=" + ((sleepSourceStr == null) ? 0 : sleepSourceStr.length()) +
                ", laster=" + laster +
                ", next=" + next +
                '}';
    }
}
