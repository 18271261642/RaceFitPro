package com.example.bozhilun.android.recommend;

/**
 * Created by Admin
 * Date 2020/4/1
 */
public class ImgBean {

    private String imgUrl;

    private String detailUrl;

    public ImgBean() {
    }

    public ImgBean(String imgUrl, String detailUrl) {
        this.imgUrl = imgUrl;
        this.detailUrl = detailUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    @Override
    public String toString() {
        return "ImgBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                '}';
    }
}
