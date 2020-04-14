package com.example.bozhilun.android.b30.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Admin
 * Date 2019/1/15
 */
public class CusWrapListView extends ListView {
    public CusWrapListView(Context context) {
        super(context);
    }

    public CusWrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusWrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
