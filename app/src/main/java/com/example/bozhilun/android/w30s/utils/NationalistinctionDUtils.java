package com.example.bozhilun.android.w30s.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/5/17 11:10
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class NationalistinctionDUtils {

    /**
     * 判断国家是否是国内用户
     * 方法一
     *
     * @param context
     * @return
     */
    public static boolean isCN(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getSimCountryIso();
        boolean isCN = false;//判断是不是大陆
        if (!TextUtils.isEmpty(countryIso)) {
            countryIso = countryIso.toUpperCase(Locale.US);
            if (countryIso.contains("CN")) {
                isCN = true;
            }
        }
        return isCN;

    }


    /**
     * 方法二
     * 查询手机的 MCC+MNC
     *
     * @param c
     * @return
     */
    private static String getSimOperator(Context c) {
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return tm.getSimOperator();
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * 因为发现像华为Y300，联想双卡的手机，会返回 "null" "null,null" 的字符串
     */
    private static boolean isOperatorEmpty(String operator) {
        if (operator == null) {
            return true;
        }
        if (operator.equals("") || operator.toLowerCase(Locale.US).contains("null")) {
            return true;
        }


        return false;
    }


    /**
     * 判断是否是国内的 SIM 卡，优先判断注册时的mcc
     */
    public static boolean isChinaSimCard(Context c) {
        String mcc = getSimOperator(c);
        if (isOperatorEmpty(mcc)) {
            return false;
        } else {
            return mcc.startsWith("460");
        }
    }

    /**
     * 获取活动名
     * @param context
     * @return
     */
    public static String getRunningActivityName(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //完整类名
            String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            String contextActivity = runningActivity.substring(runningActivity.lastIndexOf(".") + 1);
            return contextActivity;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取报名
     * @param context
     * @return
     */
    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
//            String versionName = context.getPackageManager().getPackageInfo(
//                    pkName, 0).versionName;
//            int versionCode = context.getPackageManager()
//                    .getPackageInfo(pkName, 0).versionCode;
//            return pkName + "   " + versionName + "  " + versionCode;
            return pkName;
        } catch (Exception e) {
        }
        return null;
    }

}
