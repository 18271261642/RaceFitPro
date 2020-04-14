package com.example.bozhilun.android.xwatch.ble;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2020/2/22
 */
public class XWatchNotiBean {

    /**
     * Bit0：1：来电提醒使能，0：来电提醒关闭
     * Bit1：1：短信提醒使能，0：短信提醒关闭
     * Bit2：1：QQ提醒使能，  0: QQ提醒关闭
     * Bit3：1：微信提醒使能，0: 微信提醒关闭
     * Bit4  1: Twitter提醒使能，0：Twitter提醒关闭
     * Bit5：1：Facebook提醒使能，0：Facebook提醒关闭
     * Bit6：1:Whatsapp提醒使能，0：Whatsapp提醒关闭
     * Bit7：1：Skype提醒使能，0：Skype提醒关闭
     */

    private List<Boolean> booleanList = new ArrayList<>();

    public List<Boolean> getBooleanList() {
        booleanList.clear();
        booleanList.add(isPhoneNoti());
        booleanList.add(isMsgNoti());
        booleanList.add(isQQNoti());
        booleanList.add(isWechatNoti());
        booleanList.add(isTwitterNoti());
        booleanList.add(isFaceBookNoti());
        booleanList.add(isWhatsappNoti());
        booleanList.add(isSkypeNoti());

        return booleanList;
    }


    private boolean isPhoneNoti;
    private boolean isMsgNoti;
    private boolean isQQNoti;
    private boolean isWechatNoti;
    private boolean isTwitterNoti;
    private boolean isFaceBookNoti;
    private boolean isWhatsappNoti;
    private boolean isSkypeNoti;

    public boolean isPhoneNoti() {
        return isPhoneNoti;
    }

    public void setPhoneNoti(boolean phoneNoti) {
        isPhoneNoti = phoneNoti;
    }

    public boolean isMsgNoti() {
        return isMsgNoti;
    }

    public void setMsgNoti(boolean msgNoti) {
        isMsgNoti = msgNoti;
    }

    public boolean isQQNoti() {
        return isQQNoti;
    }

    public void setQQNoti(boolean QQNoti) {
        isQQNoti = QQNoti;
    }

    public boolean isWechatNoti() {
        return isWechatNoti;
    }

    public void setWechatNoti(boolean wechatNoti) {
        isWechatNoti = wechatNoti;
    }

    public boolean isTwitterNoti() {
        return isTwitterNoti;
    }

    public void setTwitterNoti(boolean twitterNoti) {
        isTwitterNoti = twitterNoti;
    }

    public boolean isFaceBookNoti() {
        return isFaceBookNoti;
    }

    public void setFaceBookNoti(boolean faceBookNoti) {
        isFaceBookNoti = faceBookNoti;
    }

    public boolean isWhatsappNoti() {
        return isWhatsappNoti;
    }

    public void setWhatsappNoti(boolean whatsappNoti) {
        isWhatsappNoti = whatsappNoti;
    }

    public boolean isSkypeNoti() {
        return isSkypeNoti;
    }

    public void setSkypeNoti(boolean skypeNoti) {
        isSkypeNoti = skypeNoti;
    }

    @Override
    public String toString() {
        return "XWatchNotiBean{" +
                "isPhoneNoti=" + isPhoneNoti +
                ", isMsgNoti=" + isMsgNoti +
                ", isQQNoti=" + isQQNoti +
                ", isWechatNoti=" + isWechatNoti +
                ", isTwitterNoti=" + isTwitterNoti +
                ", isFaceBookNoti=" + isFaceBookNoti +
                ", isWhatsappNoti=" + isWhatsappNoti +
                ", isSkypeNoti=" + isSkypeNoti +
                '}';
    }
}
