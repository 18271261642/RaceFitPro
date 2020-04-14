package com.example.bozhilun.android.commdbserver;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2019/2/28
 * 血压上传
 */
public class CommBloodDb extends LitePalSupport {

    /**
     * "userid": "userId",
     * 		"devicecode": "devicecode",
     * 		"maxdiastolic": 133,
     * 		"minsystolic": 65,
     * 		"avgdiastolic": 75,
     * 		"avgsystolic": 46,
     * 		"rtc": "2019-02-13"
     */

    /**
     * userId
     */
    private String userid;

    /**
     * mac地址
     */
    private String devicecode;

    /**
     * 日期 yyyy-MM-dd HH:mm:ss
     */
    private String rtc;


    /**
     * 高压值
     */
    private int maxdiastolic;

    /**
     * 低压值
     */
    private int minsystolic;

    /**
     * 平均值舒张压
     */
    private int avgdiastolic;

    /**
     * 平均收缩压
     */
    private int avgsystolic;

    /**
     * 是否已上传
     * @return
     */
    private boolean isUpload;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getMaxdiastolic() {
        return maxdiastolic;
    }

    public void setMaxdiastolic(int maxdiastolic) {
        this.maxdiastolic = maxdiastolic;
    }

    public int getMinsystolic() {
        return minsystolic;
    }

    public void setMinsystolic(int minsystolic) {
        this.minsystolic = minsystolic;
    }

    public int getAvgdiastolic() {
        return avgdiastolic;
    }

    public void setAvgdiastolic(int avgdiastolic) {
        this.avgdiastolic = avgdiastolic;
    }

    public int getAvgsystolic() {
        return avgsystolic;
    }

    public void setAvgsystolic(int avgsystolic) {
        this.avgsystolic = avgsystolic;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "CommBloodDb{" +
                "userid='" + userid + '\'' +
                ", devicecode='" + devicecode + '\'' +
                ", rtc='" + rtc + '\'' +
                ", maxdiastolic=" + maxdiastolic +
                ", minsystolic=" + minsystolic +
                ", avgdiastolic=" + avgdiastolic +
                ", avgsystolic=" + avgsystolic +
                ", isUpload=" + isUpload +
                '}';
    }
}
