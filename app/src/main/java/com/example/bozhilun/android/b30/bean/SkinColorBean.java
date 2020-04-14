package com.example.bozhilun.android.b30.bean;

public class SkinColorBean {

    private int imgId;
    private boolean isChecked;

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "SkinColorBean{" +
                "imgId=" + imgId +
                ", isChecked=" + isChecked +
                '}';
    }
}
