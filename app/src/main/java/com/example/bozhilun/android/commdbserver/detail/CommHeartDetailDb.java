package com.example.bozhilun.android.commdbserver.detail;

/**
 * 心率详细数据上传
 * Created by Admin
 * Date 2019/5/13
 */
public class CommHeartDetailDb {

    /**
     * {
     * 	"heartrate": 222,
     * 	"status": 1,				//0为自动测量 1为手动测量
     * 	"devicecode": "abvc",
     * 	"rtc": "2019-04-25",
     * 	"time": "01:30",
     * 	"userid": "abc"
     * }
     */

    private String userid;

    private String devicecode;

    private String rtc;

    private int status;

    private int heartrate;

    private String time;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(int heartrate) {
        this.heartrate = heartrate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CommHeartDetailDb{" +
                "userid='" + userid + '\'' +
                ", devicecode='" + devicecode + '\'' +
                ", rtc='" + rtc + '\'' +
                ", status=" + status +
                ", heartrate=" + heartrate +
                ", time='" + time + '\'' +
                '}';
    }
}
