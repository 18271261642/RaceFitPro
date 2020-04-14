package com.example.bozhilun.android.siswatch.data;

import android.util.Log;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import org.apache.commons.lang.StringUtils;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class BarXFormartValue implements IAxisValueFormatter {

    private List<String> xListValues ;
    private BarLineChartBase<?> chart;


    public BarXFormartValue(BarLineChartBase<?> chart, List<String> list) {
        this.chart = chart;
        this.xListValues = list;
    }

    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
        String temp = StringUtils.substringBefore(String.valueOf(v),".");
        //Log.e("XY","---tmp="+temp);
        if(xListValues != null){
            return xListValues.size() > Integer.valueOf(temp) ? xListValues.get(Integer.valueOf(temp)) : "";
        }else{
            return null;
        }

    }

}
