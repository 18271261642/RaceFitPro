package com.example.bozhilun.android.b31.glossary;

import android.content.Context;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/9/19.
 */

public abstract class AGlossary {
    public Context context;
    public String head = "";
    public String groupString[] = null;
    public String itemString[][] = null;

    public AGlossary(Context context) {
        this.context = context;
        getGlossaryString();
    }

    public abstract void getGlossaryString();

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String[] getGroupString() {
        if (groupString == null) {
            return new String[0];
        }
        return groupString;
    }

    public void setGroupString(String[] groupString) {
        this.groupString = groupString;
    }

    public String[][] getItemString() {
        if (itemString == null) {
            return new String[0][0];
        }
        return itemString;
    }

    public void setItemString(String[][] itemString) {
        this.itemString = itemString;
    }

    protected String getResoureStr(int id) {
        return context.getResources().getString(id);
    }

    protected String[] getResoures(int id) {
        String[] stringArray = context.getResources().getStringArray(id);
        return stringArray;
    }

    @Override
    public String toString() {
        return "AGlossary{" +
                "head='" + head + '\'' +
                ", groupString=" + Arrays.toString(groupString) +
                ", itemString=" + Arrays.toString(itemString) +
                '}';
    }

}
