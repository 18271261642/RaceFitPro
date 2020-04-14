
package com.example.bozhilun.android.b31.glossary;

import android.content.Context;

import com.example.bozhilun.android.R;

/**
 * Created by Administrator on 2017/9/19.
 */

public class SleepBreathBreakGlossary extends AGlossary {
    public SleepBreathBreakGlossary(Context context) {
        super(context);
    }

    @Override
    public void getGlossaryString() {
        head = getResoureStr(R.string.vpspo2h_breath_break);
        groupString = getResoures(R.array.glossary_sleep_breath_break_tip);
        itemString = new String[][]{
                getResoures(R.array.glossary_sleep_breath_break_tip_1),
                getResoures(R.array.glossary_sleep_breath_break_tip_2),
                getResoures(R.array.glossary_sleep_breath_break_tip_3),
                getResoures(R.array.glossary_sleep_breath_break_tip_4),
                getResoures(R.array.glossary_sleep_breath_break_tip_5),
                getResoures(R.array.glossary_sleep_breath_break_tip_6),
        };
    }

}
