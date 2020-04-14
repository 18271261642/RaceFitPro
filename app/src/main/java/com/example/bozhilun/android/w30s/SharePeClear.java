package com.example.bozhilun.android.w30s;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.commdbserver.CommDBManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.activity.MoreShockActivity;
import com.example.bozhilun.android.w30s.bean.W30SAlarmClockBean;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.suchengkeji.android.w30sblelibrary.bean.W30S_AlarmInfo;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/14 16:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SharePeClear {

    public static void clearDatas(Context context) {

        String sss = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
        if (!WatchUtils.isEmpty(sss)) {
            Log.e("-----", "恢复出厂----" + sss);
            CommDBManager.getCommDBManager().setRebootDevices(sss);
        }

        LitePal.deleteAll(W30SAlarmClockBean.class);//清楚闹钟数据库表
        //上传数据时间间隔
        SharedPreferencesUtils.setParam(context, "upSportTime", "2017-11-02 15:00:00");
        SharedPreferencesUtils.setParam(context, "upSleepTime", "2017-11-02 15:00:00");
        SharedPreferencesUtils.setParam(context, "upHeartTimetwo", "2017-11-02 15:00:00");
        SharedPreferencesUtils.setParam(context, "upHeartTimeone", B18iUtils.getSystemDataStart());
        //目标步数
        SharedPreferencesUtils.setParam(context, "w30stag", "10000");
        //目标睡眠
        SharedPreferencesUtils.setParam(context, "w30ssleep", "8");
        //公英制
        SharedPreferencesUtils.setParam(context, "w30sunit", true);
        //时间格式
        SharedPreferencesUtils.setParam(context, "w30stimeformat", true);
        //久坐提醒
        SharedPreferencesUtils.setParam(context, "w30sSedentaryRemind", false);
        SharedPreferencesUtils.setParam(context, "starTime_sedentary", "06:00");
        SharedPreferencesUtils.setParam(context, "endTime_sedentary", "22:00");
        SharedPreferencesUtils.setParam(context, "IntervalTime_sedentary", "1H");
        //吃药
        SharedPreferencesUtils.setParam(context, "w30sMedicineRemind", false);
        SharedPreferencesUtils.setParam(context, "starTime_medicine", "06:00");
        SharedPreferencesUtils.setParam(context, "endTime_medicine", "22:00");
        SharedPreferencesUtils.setParam(context, "IntervalTime_medicine", "4H");
        //喝水
        SharedPreferencesUtils.setParam(context, "w30sDringRemind", false);
        SharedPreferencesUtils.setParam(context, "starTime_dring", "06:00");
        SharedPreferencesUtils.setParam(context, "endTime_dring", "22:00");
        SharedPreferencesUtils.setParam(context, "IntervalTime_dring", "1H");
        //会议
        SharedPreferencesUtils.setParam(context, "w30sMettingRemind", false);

        //应用通知默认关
        SharedPreferencesUtils.setParam(context, "w30sswitch_Skype", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_WhatsApp", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_Facebook", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_LinkendIn", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_Twitter", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_Viber", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_LINE", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_WeChat", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_QQ", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_Msg", false);
        SharedPreferencesUtils.setParam(context, "w30sswitch_Phone", true);
        //免扰
        SharedPreferencesUtils.setParam(context, "w30sNodisturb", false);
        //抬手亮屏
        SharedPreferencesUtils.setParam(context, "w30sBrightScreen", true);
        //运动心率
        SharedPreferencesUtils.setParam(context, "w30sHeartRate", true);
    }

    static Context mContext;

    public static void sendCmdDatas(Context context) {
        mContext = context;
        //MyApp.getInstance().getmW30SBLEManage().setInitSet(d, e, a, b, c);
        W37DataAnalysis.getW37DataAnalysis().sendCmdDatas(mContext);

        handler.sendEmptyMessageDelayed(0x02, 1000);

    }


    static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    /**
                     * 久坐提醒
                     *
                     * @param startHour 开始-小时
                     * @param startMin  开始-分钟
                     * @param endHour   结束-小时
                     * @param endMin    结束-分钟
                     * @param period    间隔时间小时 = （固定写入1,2,3,4）
                     * @param enable    开关
                     * @return
                     */

                    String intervalTime_sedentary = (String) SharedPreferencesUtils.getParam(mContext, "IntervalTime_sedentary", "1H");
                    String starTime_sedentary = (String) SharedPreferencesUtils.getParam(mContext, "starTime_sedentary", "06:00");
                    String endTime_sedentary = (String) SharedPreferencesUtils.getParam(mContext, "endTime_sedentary", "22:00");
                    String substring2 = intervalTime_sedentary.substring(0, intervalTime_sedentary.length() - 1);
                    String[] splitSs = starTime_sedentary.split("[:]");
                    String[] splitEs = endTime_sedentary.split("[:]");
                    boolean w30sSedentaryRemind = (boolean) SharedPreferencesUtils.getParam(mContext, "w30sSedentaryRemind", false);
//                    MyApp.getInstance().getmW30SBLEManage().setSitNotification(Integer.valueOf(splitSs[0]), Integer.valueOf(splitSs[1]),
//                            Integer.valueOf(splitEs[0]), Integer.valueOf(splitEs[1]),
//                            Integer.valueOf(substring2), w30sSedentaryRemind);
                    W37DataAnalysis.getW37DataAnalysis().setLongDownSitNotiMsg(Integer.valueOf(splitSs[0]), Integer.valueOf(splitSs[1]),
                            Integer.valueOf(splitEs[0]), Integer.valueOf(splitEs[1]),
                            Integer.valueOf(substring2), w30sSedentaryRemind);


                    /**
                     * 喝水提醒
                     *
                     * @param startHour 开始-小时
                     * @param startMin  开始-分钟
                     * @param endHour   结束-小时
                     * @param endMin    结束-分钟
                     * @param period    间隔时间小时 = （固定写入1,2,3,4）
                     * @param enable    开关
                     * @return
                     */
                    String intervalTime_dring = (String) SharedPreferencesUtils.getParam(mContext, "IntervalTime_dring", "1H");
                    String starTime_dring = (String) SharedPreferencesUtils.getParam(mContext, "starTime_dring", "06:00");
                    String endTime_dring = (String) SharedPreferencesUtils.getParam(mContext, "endTime_dring", "22:00");
                    String substring1 = intervalTime_dring.substring(0, intervalTime_dring.length() - 1);
                    String[] splitSd = starTime_dring.split("[:]");
                    String[] splitEd = endTime_dring.split("[:]");
                    boolean w30sDringRemind = (boolean) SharedPreferencesUtils.getParam(mContext, "w30sDringRemind", false);
//                    MyApp.getInstance().getmW30SBLEManage().setDrinkingNotification(Integer.valueOf(splitSd[0]), Integer.valueOf(splitSd[1]),
//                            Integer.valueOf(splitEd[0]), Integer.valueOf(splitEd[1]), Integer.valueOf(substring1), w30sDringRemind);

                    W37DataAnalysis.getW37DataAnalysis().setDringNotiMsg(Integer.valueOf(splitSd[0]), Integer.valueOf(splitSd[1]),
                            Integer.valueOf(splitEd[0]), Integer.valueOf(splitEd[1]), Integer.valueOf(substring1), w30sDringRemind);

                    /**
                     * 吃药提醒
                     *
                     * @param startHour 开始-小时
                     * @param startMin  开始-分钟
                     * @param endHour   结束-小时
                     * @param endMin    结束-分钟
                     * @param period    间隔时间小时  = （固定写入4,6,8,12）
                     * @param enable    开关
                     * @return
                     */
                    String intervalTime_medicine = (String) SharedPreferencesUtils.getParam(mContext, "IntervalTime_medicine", "4H");
                    String starTime_medicine = (String) SharedPreferencesUtils.getParam(mContext, "starTime_medicine", "06:00");
                    String endTime_medicine = (String) SharedPreferencesUtils.getParam(mContext, "endTime_medicine", "22:00");
                    String substring = intervalTime_medicine.substring(0, intervalTime_medicine.length() - 1);
                    String[] splitSm = starTime_medicine.split("[:]");
                    String[] splitEm = endTime_medicine.split("[:]");
                    boolean w30sMedicineRemind = (boolean) SharedPreferencesUtils.getParam(mContext, "w30sMedicineRemind", false);
//                    MyApp.getInstance().getmW30SBLEManage().setMedicalNotification(Integer.valueOf(splitSm[0]), Integer.valueOf(splitSm[1]),
//                            Integer.valueOf(splitEm[0]), Integer.valueOf(splitEm[1]), Integer.valueOf(substring), w30sMedicineRemind);

                    W37DataAnalysis.getW37DataAnalysis().setMealcalNotiMsg(Integer.valueOf(splitSm[0]), Integer.valueOf(splitSm[1]),
                            Integer.valueOf(splitEm[0]), Integer.valueOf(splitEm[1]), Integer.valueOf(substring), w30sMedicineRemind);

                    /**
                     * 会议设置
                     *
                     * @param year   年
                     * @param month  月
                     * @param day    日
                     * @param hour   小时
                     * @param min    分钟
                     * @param enable 开关
                     * @return
                     */
                    String dateStr2 = W30BasicUtils.getDateStr2(W30BasicUtils.getCurrentDate2(), 1);
                    String datas = (String) SharedPreferencesUtils.getParam(mContext, "IntervalTime_metting_data", dateStr2);
                    String times = (String) SharedPreferencesUtils.getParam(mContext, "IntervalTime_metting_time", W30BasicUtils.getCurrentDate3().substring(0, W30BasicUtils.getCurrentDate3().length() - 3));
                    String[] year = datas.split("[-]");
                    String[] time = times.split("[:]");
                    int yearM = (int) Integer.valueOf(year[0].substring(2, 4));
                    int motohM = (int) Integer.valueOf(year[1]);
                    int dayM = (int) Integer.valueOf(year[2]);
                    int hourM = (int) Integer.valueOf(time[0]);
                    int minM = (int) Integer.valueOf(time[1]);
                    boolean w30sMettingRemind = (boolean) SharedPreferencesUtils.getParam(mContext, "w30sMettingRemind", false);
                   // MyApp.getInstance().getmW30SBLEManage().setMeetingNotification(yearM, motohM, dayM, hourM, minM, w30sMettingRemind);

                    W37DataAnalysis.getW37DataAnalysis().setMeetingNotiMsg(yearM, motohM, dayM, hourM, minM, w30sMettingRemind);
                    break;
                case 0x02:
                    /**
                     * 设置用户闹钟信息
                     */
                    List<W30SAlarmClockBean> mAlarmClock = LitePal.findAll(W30SAlarmClockBean.class);
                    if (mAlarmClock == null) mAlarmClock = new ArrayList<>();
                    Log.e("=========闹钟=======", mAlarmClock.size() + "==" + mAlarmClock.toString());
                    List<W30S_AlarmInfo> w30S_alarmInfos = new ArrayList<W30S_AlarmInfo>();
                    for (int i = 0; i < mAlarmClock.size(); i++) {
                        W30S_AlarmInfo w30S_alarmInfo = mAlarmClock.get(i).w30AlarmInfoChange(i);
                        w30S_alarmInfos.add(w30S_alarmInfo);
                    }
                    Log.e("=========闹钟对象=======", w30S_alarmInfos.size() + "==" + w30S_alarmInfos.toString());
                    //MyApp.getInstance().getmW30SBLEManage().setAlarm(w30S_alarmInfos);
                    W37DataAnalysis.getW37DataAnalysis().setW37AlarmData(w30S_alarmInfos);
                    handler.sendEmptyMessageDelayed(0x01, 2000);
                    break;
            }
            return false;
        }
    });
}
