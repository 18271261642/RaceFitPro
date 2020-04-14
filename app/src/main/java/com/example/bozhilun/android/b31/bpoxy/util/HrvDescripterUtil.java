package com.example.bozhilun.android.b31.bpoxy.util;

import android.content.Context;

import com.example.bozhilun.android.R;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HRV描述的工具类
 * Created by Administrator on 2018/3/19.
 */
public class HrvDescripterUtil {

    public HrvDescripterUtil(Context context) {
        initStr(context);
    }


    /***
     * 获取hrv的头部
     *
     * @return
     */
    public String[] getRepoTitle() {
        String[] titleArray = new String[5];
        titleArray[0] = mStrTitle_0;
        titleArray[1] = mStrTitle_2;
        titleArray[2] = mStrTitle_3;
        titleArray[3] = mStrTitle_4;
        titleArray[4] = mStrTitle_5;
        return titleArray;
    }

    /**
     * 通过遍历，获取hrv的具体内容
     *
     * @param value 传入数字
     * @return 返回内容
     */
    public String getRepoInfo(int value) {
        Map<Integer, String> infoMap = getInfoMap();
        Iterator<Map.Entry<Integer, String>> iterator = infoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> next = iterator.next();
            if (next.getKey() == value) return next.getValue();
        }
        return "";
    }

    /**
     * 获取hrv的等级[用星星表示]
     *
     * @param value 传入等级
     * @return 用星星表示的等级如三级[★★★✩✩]
     */
    public String getLevel(int value) {
        String start = "★";
        String unstart = "✩";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 5; i++) {
            if (i < getLevelInt(value)) {
                stringBuffer.append(start);
            } else {
                stringBuffer.append(unstart);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 获取hrv的头部，数字
     *
     * @param value 传入数字
     * @return 返回等级
     */
    public int getLevelInt(int value) {
        if ((value == 232) || (value == 234) || (value == 233) || (value == 235)
                || (value == 323) || (value == 314) || (value == 333) || (value == 334)
                || (value == 422) || (value == 423) || (value == 430)
                || (value == 532) || (value == 533) || (value == 540)) return 1;
        if ((value == 223) || (value == 231)
                || (value == 331) || (value == 332)
                || (value == 323) || (value == 314)
                || (value == 417) || (value == 419) || (value == 421)
                || (value == 522) || (value == 524) || (value == 526) || (value == 531)) return 2;
        if ((value == 213) || (value == 221) || (value == 222)
                || (value == 312) || (value == 321) || (value == 313)
                || (value == 411) || (value == 412) || (value == 413) || (value == 418)
                || (value == 521) || (value == 523) || (value == 525)) return 3;
        if ((value == 212)
                || (value == 322)
                || (value == 414) || (value == 416)
                || (value == 512) || (value == 513) || (value == 514)) return 4;
        if ((value == 211)
                || (value == 311)
                || (value == 415)
                || (value == 511)) return 5;
        return 0;
    }

    /**
     * 初始化map的赋值
     *
     * @return 返回map集合
     */
    public Map<Integer, String> getInfoMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(100, mStrInfo_1_00);
        map.put(211, mStrInfo_2_11);
        map.put(212, mStrInfo_2_12);
        map.put(213, mStrInfo_2_13);
        map.put(221, mStrInfo_2_21);
        map.put(222, mStrInfo_2_22);
        map.put(223, mStrInfo_2_23);
        map.put(231, mStrInfo_2_31);
        map.put(232, mStrInfo_2_32);
        map.put(233, mStrInfo_2_33);
        map.put(234, mStrInfo_2_34);
        map.put(235, mStrInfo_2_35);
        map.put(311, mStrInfo_3_11);
        map.put(312, mStrInfo_3_12);
        map.put(313, mStrInfo_3_13);
        map.put(314, mStrInfo_3_14);
        map.put(321, mStrInfo_3_21);
        map.put(322, mStrInfo_3_22);
        map.put(323, mStrInfo_3_23);
        map.put(331, mStrInfo_3_31);
        map.put(332, mStrInfo_3_32);
        map.put(333, mStrInfo_3_33);
        map.put(334, mStrInfo_3_34);
        map.put(411, mStrInfo_4_11);
        map.put(412, mStrInfo_4_12);
        map.put(413, mStrInfo_4_13);
        map.put(414, mStrInfo_4_14);
        map.put(415, mStrInfo_4_15);
        map.put(416, mStrInfo_4_16);
        map.put(417, mStrInfo_4_17);
        map.put(418, mStrInfo_4_18);
        map.put(419, mStrInfo_4_19);
        map.put(421, mStrInfo_4_21);
        map.put(422, mStrInfo_4_22);
        map.put(423, mStrInfo_4_23);
        map.put(430, mStrInfo_4_30);
        map.put(511, mStrInfo_5_11);
        map.put(512, mStrInfo_5_12);
        map.put(513, mStrInfo_5_13);
        map.put(514, mStrInfo_5_14);
        map.put(521, mStrInfo_5_21);
        map.put(522, mStrInfo_5_22);
        map.put(523, mStrInfo_5_23);
        map.put(524, mStrInfo_5_24);
        map.put(525, mStrInfo_5_25);
        map.put(526, mStrInfo_5_26);
        map.put(531, mStrInfo_5_31);
        map.put(532, mStrInfo_5_32);
        map.put(533, mStrInfo_5_33);
        map.put(540, mStrInfo_5_40);
        return map;
    }

    /**
     * 获取字符串
     *
     * @param context
     * @param id
     * @return
     */
    private String getStr(Context context, int id) {
        return context.getApplicationContext().getResources().getString(id);
    }

    String mStrTitle_0;
    String mStrTitle_2;
    String mStrTitle_3;
    String mStrTitle_4;
    String mStrTitle_5;
    String mStrInfo_1_00;
    String mStrInfo_2_11;
    String mStrInfo_2_12;
    String mStrInfo_2_13;
    String mStrInfo_2_21;
    String mStrInfo_2_22;
    String mStrInfo_2_23;
    String mStrInfo_2_31;
    String mStrInfo_2_32;
    String mStrInfo_2_33;
    String mStrInfo_2_34;
    String mStrInfo_2_35;
    String mStrInfo_3_11;
    String mStrInfo_3_12;
    String mStrInfo_3_13;
    String mStrInfo_3_14;
    String mStrInfo_3_21;
    String mStrInfo_3_22;
    String mStrInfo_3_23;
    String mStrInfo_3_31;
    String mStrInfo_3_32;
    String mStrInfo_3_33;
    String mStrInfo_3_34;
    String mStrInfo_4_11;
    String mStrInfo_4_12;
    String mStrInfo_4_13;
    String mStrInfo_4_14;
    String mStrInfo_4_15;
    String mStrInfo_4_16;
    String mStrInfo_4_17;
    String mStrInfo_4_18;
    String mStrInfo_4_19;
    String mStrInfo_4_21;
    String mStrInfo_4_22;
    String mStrInfo_4_23;
    String mStrInfo_4_30;
    String mStrInfo_5_11;
    String mStrInfo_5_12;
    String mStrInfo_5_13;
    String mStrInfo_5_14;
    String mStrInfo_5_21;
    String mStrInfo_5_22;
    String mStrInfo_5_23;
    String mStrInfo_5_24;
    String mStrInfo_5_25;
    String mStrInfo_5_26;
    String mStrInfo_5_31;
    String mStrInfo_5_32;
    String mStrInfo_5_33;
    String mStrInfo_5_40;

    /**
     * 初始化字符串
     *
     * @param context
     */
    private void initStr(Context context) {
        mStrTitle_0 = getStr(context, R.string.vphrv_repo_0);
        mStrTitle_2 = getStr(context, R.string.vphrv_repo_2);
        mStrTitle_3 = getStr(context, R.string.vphrv_repo_3);
        mStrTitle_4 = getStr(context, R.string.vphrv_repo_4);
        mStrTitle_5 = getStr(context, R.string.vphrv_repo_5);
        mStrInfo_1_00 = getStr(context, R.string.vphrv_repo_1_00);
        mStrInfo_2_11 = getStr(context, R.string.vphrv_repo_2_11);
        mStrInfo_2_12 = getStr(context, R.string.vphrv_repo_2_12);
        mStrInfo_2_13 = getStr(context, R.string.vphrv_repo_2_13);
        mStrInfo_2_21 = getStr(context, R.string.vphrv_repo_2_21);
        mStrInfo_2_22 = getStr(context, R.string.vphrv_repo_2_22);
        mStrInfo_2_23 = getStr(context, R.string.vphrv_repo_2_23);
        mStrInfo_2_31 = getStr(context, R.string.vphrv_repo_2_31);
        mStrInfo_2_32 = getStr(context, R.string.vphrv_repo_2_32);
        mStrInfo_2_33 = getStr(context, R.string.vphrv_repo_2_33);
        mStrInfo_2_34 = getStr(context, R.string.vphrv_repo_2_34);
        mStrInfo_2_35 = getStr(context, R.string.vphrv_repo_2_35);
        mStrInfo_3_11 = getStr(context, R.string.vphrv_repo_3_11);
        mStrInfo_3_12 = getStr(context, R.string.vphrv_repo_3_12);
        mStrInfo_3_13 = getStr(context, R.string.vphrv_repo_3_13);
        mStrInfo_3_14 = getStr(context, R.string.vphrv_repo_3_14);
        mStrInfo_3_21 = getStr(context, R.string.vphrv_repo_3_21);
        mStrInfo_3_22 = getStr(context, R.string.vphrv_repo_3_22);
        mStrInfo_3_23 = getStr(context, R.string.vphrv_repo_3_23);
        mStrInfo_3_31 = getStr(context, R.string.vphrv_repo_3_31);
        mStrInfo_3_32 = getStr(context, R.string.vphrv_repo_3_32);
        mStrInfo_3_33 = getStr(context, R.string.vphrv_repo_3_33);
        mStrInfo_3_34 = getStr(context, R.string.vphrv_repo_3_34);
        mStrInfo_4_11 = getStr(context, R.string.vphrv_repo_4_11);
        mStrInfo_4_12 = getStr(context, R.string.vphrv_repo_4_12);
        mStrInfo_4_13 = getStr(context, R.string.vphrv_repo_4_13);
        mStrInfo_4_14 = getStr(context, R.string.vphrv_repo_4_14);
        mStrInfo_4_15 = getStr(context, R.string.vphrv_repo_4_15);
        mStrInfo_4_16 = getStr(context, R.string.vphrv_repo_4_16);
        mStrInfo_4_17 = getStr(context, R.string.vphrv_repo_4_17);
        mStrInfo_4_18 = getStr(context, R.string.vphrv_repo_4_18);
        mStrInfo_4_19 = getStr(context, R.string.vphrv_repo_4_19);
        mStrInfo_4_21 = getStr(context, R.string.vphrv_repo_4_21);
        mStrInfo_4_22 = getStr(context, R.string.vphrv_repo_4_22);
        mStrInfo_4_23 = getStr(context, R.string.vphrv_repo_4_23);
        mStrInfo_4_30 = getStr(context, R.string.vphrv_repo_4_30);
        mStrInfo_5_11 = getStr(context, R.string.vphrv_repo_5_11);
        mStrInfo_5_12 = getStr(context, R.string.vphrv_repo_5_12);
        mStrInfo_5_13 = getStr(context, R.string.vphrv_repo_5_13);
        mStrInfo_5_14 = getStr(context, R.string.vphrv_repo_5_14);
        mStrInfo_5_21 = getStr(context, R.string.vphrv_repo_5_21);
        mStrInfo_5_22 = getStr(context, R.string.vphrv_repo_5_22);
        mStrInfo_5_23 = getStr(context, R.string.vphrv_repo_5_23);
        mStrInfo_5_24 = getStr(context, R.string.vphrv_repo_5_24);
        mStrInfo_5_25 = getStr(context, R.string.vphrv_repo_5_25);
        mStrInfo_5_26 = getStr(context, R.string.vphrv_repo_5_26);
        mStrInfo_5_31 = getStr(context, R.string.vphrv_repo_5_31);
        mStrInfo_5_32 = getStr(context, R.string.vphrv_repo_5_32);
        mStrInfo_5_33 = getStr(context, R.string.vphrv_repo_5_33);
        mStrInfo_5_40 = getStr(context, R.string.vphrv_repo_5_40);
    }
}
