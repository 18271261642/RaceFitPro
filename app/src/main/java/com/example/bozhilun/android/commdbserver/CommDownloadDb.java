package com.example.bozhilun.android.commdbserver;

import org.litepal.crud.LitePalSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/3/13
 * 保存从后台获取的数据
 */
public class CommDownloadDb extends LitePalSupport {

    /**
     * userId
     */
    private String userId;

    /**
     * 设备地址
     */
    private String deviceCode;

    /**
     * 类型
     */
    private String commType;

    /**
     * 日期
     *
     */
    private String dateStr;

    /**
     * 步数
     */
    private String stepNumber;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    @Override
    public String toString() {
        return "CommDownloadDb{" +
                "userId='" + userId + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", commType='" + commType + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", stepNumber='" + stepNumber + '\'' +
                '}';
    }
}
