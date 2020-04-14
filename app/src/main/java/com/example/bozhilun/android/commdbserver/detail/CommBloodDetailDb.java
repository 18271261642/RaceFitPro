package com.example.bozhilun.android.commdbserver.detail;

/**
 * 血压详细数据
 * Created by Admin
 * Date 2019/5/14
 */
public class CommBloodDetailDb {


    /**
     * {
     * 	"systolic": 222,    //高压
     * 	"diastolic": 222,   ///低压
     * 	"status": 1,		//0为自动测量 1为手动测量
     * 	"devicecode": "abvc",
     * 	"rtc": "2019-04-25",    //日期 yyyy-MM-dd格式
     * 	"time": "00:30",        //时间点 HH:mm格式
     * 	"userid": "abc"         //userID
     * }
     */


    private String userid;

    private String rtc;

    private int status;

    private int diastolic;

    private int systolic;

    private String devicecode;

    private String time;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public String getDevicecode() {
        return devicecode;
    }

    public void setDevicecode(String devicecode) {
        this.devicecode = devicecode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CommBloodDetailDb{" +
                "userid='" + userid + '\'' +
                ", rtc='" + rtc + '\'' +
                ", status=" + status +
                ", diastolic=" + diastolic +
                ", systolic=" + systolic +
                ", devicecode='" + devicecode + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
