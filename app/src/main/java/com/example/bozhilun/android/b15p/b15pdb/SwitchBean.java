package com.example.bozhilun.android.b15p.b15pdb;

public class SwitchBean {
    private String switchName;
    private boolean isOpen;

    public SwitchBean(String switchName, boolean isOpen) {
        this.switchName = switchName;
        this.isOpen = isOpen;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "SwitchBean{" +
                "switchName='" + switchName + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
