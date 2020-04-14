package com.example.bozhilun.android.b30.bean;

import com.lidroid.xutils.db.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 数据库的实体类: B30半小时数据源
 *
 * @author XuBo 2018-09-19
 */
public class B30HalfHourDB extends LitePalSupport {

    /**
     * 手环MAC地址
     */
    private String address;
    /**
     * 日期
     */
    private String date;
    /**
     * 类型(参考com.example.bozhilun.android.b30.bean.B30HalfHourDao里面的类型)
     */
    private String type;
    /**
     * 源数据(JSON格式)
     */
    private String originData;
    /**
     * 这个数据是否已经上传到服务器(0_未上传 1_已上传)
     */
    @Column(defaultValue = "0")
    private int upload;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }


    @Override
    public String toString() {
        return "B30HalfHourDB{" +
                "address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", originData='" + originData + '\'' +
                ", upload=" + upload +
                '}';
    }
}
