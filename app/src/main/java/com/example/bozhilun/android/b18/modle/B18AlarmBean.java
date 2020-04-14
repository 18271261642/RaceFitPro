package com.example.bozhilun.android.b18.modle;

import android.content.Context;

import com.example.bozhilun.android.R;

import org.litepal.crud.LitePalSupport;

/**
 * B18闹钟实体类
 * Created by Admin
 * Date 2019/11/14
 */
public class B18AlarmBean extends LitePalSupport {

    private int id;
    private boolean open;
    private int hour;
    private int minute;
    public boolean openSaturday;
    private boolean openSunday;
    private boolean openFriday;
    private boolean openThursday;
    private boolean openWednesday;
    private boolean openTuesday;
    private boolean openMonday;

    public B18AlarmBean() {
    }

    public B18AlarmBean(boolean open, int hour, int minute, boolean openSaturday, boolean openSunday, boolean openFriday, boolean openThursday, boolean openWednesday, boolean openTuesday, boolean openMonday) {
        this.open = open;
        this.hour = hour;
        this.minute = minute;
        this.openSaturday = openSaturday;
        this.openSunday = openSunday;
        this.openFriday = openFriday;
        this.openThursday = openThursday;
        this.openWednesday = openWednesday;
        this.openTuesday = openTuesday;
        this.openMonday = openMonday;
    }

    /**
     *  <string name="monday">周一</string>
     *     <string name="tuesday">周二</string>
     *     <string name="wednesday">周三</string>
     *     <string name="thursday">周四</string>
     *     <string name="friday">周五</string>
     *     <string name="saturday">周六</string>
     *     <string name="sunday">周日</string>
     * @param context
     * @return
     */

    public String showAlarmWeeks(Context context){
        StringBuilder stringBuilder = new StringBuilder();
        if(isOpenMonday() && isOpenTuesday() && isOpenWednesday()
                && isOpenThursday() && isOpenFriday() && isOpenSunday() && isOpenSaturday() )
            return context.getResources().getString(R.string.every_day);
        stringBuilder.append(isOpenMonday() ? context.getResources().getString(R.string.monday):"");
        stringBuilder.append(isOpenTuesday() ? context.getResources().getString(R.string.tuesday): "");
        stringBuilder.append(isOpenWednesday() ? context.getResources().getString(R.string.wednesday):"");
        stringBuilder.append(isOpenThursday() ? context.getResources().getString(R.string.thursday):"");
        stringBuilder.append(isOpenFriday() ? context.getResources().getString(R.string.friday):"");
        stringBuilder.append(isOpenSaturday()  ? context.getResources().getString(R.string.saturday):"");
        stringBuilder.append(isOpenSunday()? context.getResources().getString(R.string.sunday):"");
        return stringBuilder.toString();

    }


    public String setAlarmAnalysis(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0");
        stringBuilder.append(isOpenSaturday()?"1":"0");
        stringBuilder.append(isOpenFriday()?"1":"0");
        stringBuilder.append(isOpenThursday()?"1":"0");
        stringBuilder.append(isOpenWednesday()?"1":"0");
        stringBuilder.append(isOpenTuesday()?"1":"0");
        stringBuilder.append(isOpenMonday()?"1":"0");
        stringBuilder.append(isOpenSunday()?"1":"0");
        return stringBuilder.toString();
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isOpenSaturday() {
        return openSaturday;
    }

    public void setOpenSaturday(boolean openSaturday) {
        this.openSaturday = openSaturday;
    }

    public boolean isOpenSunday() {
        return openSunday;
    }

    public void setOpenSunday(boolean openSunday) {
        this.openSunday = openSunday;
    }

    public boolean isOpenFriday() {
        return openFriday;
    }

    public void setOpenFriday(boolean openFriday) {
        this.openFriday = openFriday;
    }

    public boolean isOpenThursday() {
        return openThursday;
    }

    public void setOpenThursday(boolean openThursday) {
        this.openThursday = openThursday;
    }

    public boolean isOpenWednesday() {
        return openWednesday;
    }

    public void setOpenWednesday(boolean openWednesday) {
        this.openWednesday = openWednesday;
    }

    public boolean isOpenTuesday() {
        return openTuesday;
    }

    public void setOpenTuesday(boolean openTuesday) {
        this.openTuesday = openTuesday;
    }

    public boolean isOpenMonday() {
        return openMonday;
    }

    public void setOpenMonday(boolean openMonday) {
        this.openMonday = openMonday;
    }

    @Override
    public String toString() {
        return "B18AlarmBean{" +
                "id=" + id +
                ", open=" + open +
                ", hour=" + hour +
                ", minute=" + minute +
                ", openSaturday=" + openSaturday +
                ", openSunday=" + openSunday +
                ", openFriday=" + openFriday +
                ", openThursday=" + openThursday +
                ", openWednesday=" + openWednesday +
                ", openTuesday=" + openTuesday +
                ", openMonday=" + openMonday +
                '}';
    }
}
