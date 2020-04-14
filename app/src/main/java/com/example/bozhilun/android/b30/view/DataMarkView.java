package com.example.bozhilun.android.b30.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.apache.commons.lang.StringUtils;

/**
 * chart点击显示数值
 */
public class DataMarkView extends MarkerView {
    private static final String TAG = "DataMarkView";

    private TextView showTv;

    private int flagCode;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public DataMarkView(Context context, int layoutResource,int code) {
        super(context, layoutResource);
        this.flagCode = code;
        showTv = findViewById(R.id.markViewShowTv);

    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(e instanceof BarEntry){
            BarEntry valueBar = (BarEntry) e;
            try {
                if(flagCode == 0){  //睡眠
                    //小数点之前的数据
                    String beforeD = StringUtils.substringBefore(valueBar.getY()+"",".");
                    //小数点后数据
                    String afterD = StringUtils.substringAfter(valueBar.getY()+"",".");
                    //小数点后一位
                    String firstD = afterD.substring(0,1);

                    showTv.setText(beforeD+"."+firstD);
                }else{
                    showTv.setText(""+StringUtils.substringBefore(valueBar.getY()+"","."));
                }

            }catch (Exception es){
                es.printStackTrace();
            }

        }

        super.refreshContent(e, highlight);

    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
