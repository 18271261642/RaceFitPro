package com.example.bozhilun.android.b31.glossary;

import android.content.Context;

import com.example.bozhilun.android.R;


/**
 * Created by Administrator on 2017/9/19.
 */

public class OsahsGlossary extends AGlossary {
    public OsahsGlossary(Context context) {
        super(context);
    }

    @Override
    public void getGlossaryString() {
        head = "OSAHS";
        groupString = getResoures(R.array.glossary_osahs);
        itemString = new String[][]{
                getResoures(R.array.glossary_osahs_item_1),
                getResoures(R.array.glossary_osahs_item_2),
                getResoures(R.array.glossary_osahs_item_3),
        };
    }

}
