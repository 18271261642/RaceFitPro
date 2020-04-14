package com.example.bozhilun.android.b31.glossary;

import android.content.Context;

import com.example.bozhilun.android.R;


/**
 * Created by Administrator on 2017/9/19.
 */

public class HeartGlossary extends AGlossary {
    public HeartGlossary(Context context) {
        super(context);
    }

    @Override
    public void getGlossaryString() {
        head = getResoureStr(R.string.vpspo2h_toptitle_heart);
        groupString = getResoures(R.array.glossary_osahs);
        itemString = new String[][]{
                getResoures(R.array.glossary_heart_item_1),
                getResoures(R.array.glossary_heart_item_2),
                getResoures(R.array.glossary_heart_item_3),
        };    }

}
