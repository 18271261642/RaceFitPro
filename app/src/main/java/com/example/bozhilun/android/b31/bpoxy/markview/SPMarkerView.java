package com.example.bozhilun.android.b31.bpoxy.markview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.bpoxy.util.TranStrUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import java.util.List;
import java.util.Map;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.*;


/**
 * MarkView
 * Created by timaimee on 2017/6/26.
 */
public class SPMarkerView extends MarkerView {
    List<Map<String, Float>> listData, beathBreakData;
    private TextView tvValue;
    private TextView tvTime;
    private boolean is24Modle = false;
    private float middleValue;
    private ESpo2hDataType type;
    private float yValue = 0;
    float timeValue = 0;
    //图表中的圆点颜色，从左到右依次是1，2，3
    int color_dot_spo2h_1;
    int color_dot_spo2h_2;
    int color_dot_heart_1;
    int color_dot_heart_2;
    int color_dot_heart_3;
    int color_dot_sleep_1;
    int color_dot_sleep_2;
    int color_dot_sleep_3;
    int color_dot_beath_1;
    int color_dot_beath_2;
    int color_dot_lowsp_1;
    int color_dot_lowsp_2;

    public SPMarkerView(Context context, int layoutResource, boolean is24Modle, float middleValue, ESpo2hDataType type) {
        super(context, layoutResource);
        this.is24Modle = is24Modle;
        this.middleValue = middleValue;
        this.type = type;
        getColor(context);
        tvValue = (TextView) findViewById(R.id.markview_tv_value);
        tvTime = (TextView) findViewById(R.id.markview_tv_time);
    }

    private void getColor(Context context) {
        color_dot_spo2h_1 = context.getResources().getColor(R.color.spo2h_dot_spo2h_1);
        color_dot_spo2h_2 = context.getResources().getColor(R.color.spo2h_dot_spo2h_2);
        color_dot_heart_1 = context.getResources().getColor(R.color.spo2h_dot_heart_1);
        color_dot_heart_2 = context.getResources().getColor(R.color.spo2h_dot_heart_2);
        color_dot_heart_3 = context.getResources().getColor(R.color.spo2h_dot_heart_3);
        color_dot_sleep_1 = context.getResources().getColor(R.color.spo2h_dot_sleep_1);
        color_dot_sleep_2 = context.getResources().getColor(R.color.spo2h_dot_sleep_2);
        color_dot_sleep_3 = context.getResources().getColor(R.color.spo2h_dot_sleep_3);
        color_dot_beath_1 = context.getResources().getColor(R.color.spo2h_dot_beath_1);
        color_dot_beath_2 = context.getResources().getColor(R.color.spo2h_dot_beath_2);
        color_dot_lowsp_1 = context.getResources().getColor(R.color.spo2h_dot_lowsp_1);
        color_dot_lowsp_2 = context.getResources().getColor(R.color.spo2h_dot_lowsp_2);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvValue.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            timeValue = (float) e.getData();
            yValue = e.getY();
            setTimeValue();
            getContectValue();
        }

        super.refreshContent(e, highlight);
    }

    private void setTimeValue() {
        String timeStr = TranStrUtil.getSpo2hTimeString((int) timeValue, is24Modle);
        tvTime.setTextColor(color_dot_lowsp_1);
        tvTime.setText(timeStr);
    }

    private void getContectValue() {
        if (listData == null || listData.isEmpty()) {
            return;
        }
        tvValue.setText(Utils.formatNumber(yValue, 0, true));
        tvValue.setTextColor(getRoundColor(type, yValue, timeValue));
        for (int i = 0; i < listData.size(); i++) {
            float time = listData.get(i).get("time");
            if (time == timeValue) {
                float value = listData.get(i).get("value");
                tvValue.setText((int) value + "");
            }
        }
    }

    @Override
    public MPPointF getOffset() {

        MPPointF mpPointF;
        if (yValue < middleValue) {
            //向上显示
            mpPointF = new MPPointF(-(getWidth() / 2), -getHeight() / 10 * 12);
        } else {
            //向下显示
            mpPointF = new MPPointF(-(getWidth() / 2), getHeight() / 8 * 2);
        }
        return mpPointF;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStrokeWidth(3f);
        canvas.drawCircle(posX, posY, 12, circlePaint);
        circlePaint.setColor(getRoundColor(type, yValue, timeValue));
        canvas.drawCircle(posX, posY, 8, circlePaint);
    }

    public void setData(List<Map<String, Float>> listData) {
        this.listData = listData;
    }

    public void setBeathBreakData(List<Map<String, Float>> beathBreakData) {
        this.beathBreakData = beathBreakData;
    }

    private int getRoundColor(ESpo2hDataType type, float vlaue, float timeValue) {
        if (type == TYPE_HEART) {
            if (vlaue < 20) {
                return color_dot_heart_1;
            } else if (vlaue < 40) {
                return color_dot_heart_2;
            } else {
                return color_dot_heart_3;
            }
        } else if (type == TYPE_SLEEP) {
            if (vlaue < 20) {
                return color_dot_sleep_1;
            } else if (vlaue < 50) {
                return color_dot_sleep_2;
            } else {
                return color_dot_sleep_3;
            }
        } else if (type == TYPE_BREATH) {
            if (vlaue < 26) {
                return color_dot_beath_1;
            } else {
                return color_dot_beath_2;
            }
        } else if (type == TYPE_LOWSPO2H) {
            if (vlaue < 100) {
                return color_dot_lowsp_1;
            } else {
                return color_dot_lowsp_2;
            }
        } else if (type == TYPE_SPO2H) {
            if (!beathBreakContainSpo2h(timeValue)) {
                return color_dot_lowsp_1;
            } else {
                return color_dot_spo2h_2;
            }
        } else if (type == TYPE_HRV) {
            return color_dot_lowsp_1;
        }
        return 0;
    }

    /**
     * 血氧的点，呼吸暂停有数显示红色
     *
     * @param timeValue
     * @return
     */
    public boolean beathBreakContainSpo2h(float timeValue) {
        if (beathBreakData != null && !beathBreakData.isEmpty()) {
            for (int i = 0; i < beathBreakData.size(); i++) {
                float time = beathBreakData.get(i).get("time");
                if (time == timeValue) {
                    float value = beathBreakData.get(i).get("value");
                    if (value != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}

