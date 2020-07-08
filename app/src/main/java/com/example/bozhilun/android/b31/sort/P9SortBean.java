package com.example.bozhilun.android.b31.sort;

/**
 * Created by Admin
 * Date 2020/5/5
 */
public class P9SortBean
{

    private String titleName;

    private int sortId;

    public P9SortBean(String titleName, int sortId) {
        this.titleName = titleName;
        this.sortId = sortId;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    @Override
    public String toString() {
        return "P9SortBean{" +
                "titleName='" + titleName + '\'' +
                ", sortId=" + sortId +
                '}';
    }
}
