package com.example.bozhilun.android.siswatch.mine;

/**
 * Created by Admin
 * Date 2019/6/4
 */
public class NotiMsgBean {


    /**
     * id : 4
     * title : 新产品上市
     * day : 2019-06-04
     * content : W37，B37重磅上线，请点击以下链接查看
     * url : http://www.baidu.com
     */

    private String title;
    private String day;
    private String content;
    private String url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "NotiMsgBean{" +
                "title='" + title + '\'' +
                ", day='" + day + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
