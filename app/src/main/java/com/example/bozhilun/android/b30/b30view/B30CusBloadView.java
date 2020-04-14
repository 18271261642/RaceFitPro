package com.example.bozhilun.android.b30.b30view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * B30,B31首页的血压图表显示，只显示最近的12条
 * Created by Administrator on 2018/8/4.
 */

public class B30CusBloadView extends View {

    //低压的画笔
    private Paint lowPaint;
    //高压的画笔
    private Paint highPaint;
    //连线的画笔
    private Paint linPaint;
    //绘制日期的画笔
    private Paint timePaint;
    //绘制无数据时显示的txt
    private Paint emptyPaint;


    //低压画笔的颜色
    private int lowColor;
    //高压画笔的颜色
    private int hightColor;
    //连线的画笔颜色
    private int linColor;
    //日期的画笔颜色
    private int timeColor;

    //画刻度尺的画笔
    private Paint scalePaint;

    /**
     * 是否绘制刻度和横线
     */
    private boolean isScale = false;

    /**
     * 画笔大小:线,时间,圆半径
     */
    private int linStroke, timeStroke, radioStroke;
    /**
     * 点列表
     */
    private SparseArray<Point> pointList = new SparseArray<>();
    /**
     * 左边空出来,右边空出来,可用宽度
     */
    private int valStart, valRight, valWidth;
    /**
     * 高度比例
     */
    private float ratio;


    float width, height;

    @SuppressLint("UseSparseArrays")
    private SparseArray<Integer> bpSparryLt = new SparseArray<>();


    /**
     * 时间刻度
     */
    private final String[] timeStr = new String[]{"00:00", "03:00", "06:00", "09:00", "12:00",
            "15:00", "18:00", "21:00", "23:59"};


    //血压的值map保存
    private List<Map<Integer, Integer>> bpMapV = new ArrayList<>();

    List<Map<String, Map<Integer, Integer>>> tempBpMap = new ArrayList<>();


    //时间的值
    private List<String> bpTimeV = new ArrayList<>();


    public B30CusBloadView(Context context) {
        super(context);
    }

    public B30CusBloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public B30CusBloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.B30CusBloadView);
        if (typedArray != null) {
            lowColor = typedArray.getColor(R.styleable.B30CusBloadView_lowPointColor, 0);
            hightColor = typedArray.getColor(R.styleable.B30CusBloadView_highPointColor, 0);
            linColor = typedArray.getColor(R.styleable.B30CusBloadView_linPaintColor, 0);
            timeColor = typedArray.getColor(R.styleable.B30CusBloadView_timeColor, 0);
            linStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_linStroke, dp2px(1));
            timeStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_timeStroke, sp2px(10));
            radioStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_radioStroke, dp2px(2));
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        valRight = dp2px(5);

        lowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lowPaint.setColor(lowColor);
        lowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        highPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highPaint.setColor(hightColor);
        highPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(linColor);
        linPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linPaint.setStrokeWidth(linStroke);

        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(timeColor);
        timePaint.setTextSize(timeStroke);
        timePaint.setTextAlign(Paint.Align.LEFT);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setTextAlign(Paint.Align.LEFT);
        emptyPaint.setColor(timeColor);
        emptyPaint.setTextSize(dp2px(8));
        emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStrokeWidth(linStroke);
        scalePaint.setColor(timeColor);
        scalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec),getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec));
        width = getMeasuredWidth();

        height = getMeasuredHeight();

        Log.e("BP", "----onMeasure------withd=" + width + "--height=" + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        Log.e("BP", "----onSizeChanged------withd=" + getWidth() + "----w=" + w);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, height);
        canvas.save();
        valStart = isScale ? dp2px(18) : dp2px(5);
        valWidth = getWidth() - valStart - valRight;
        ratio = (float) getHeight() / 230;

        drawScaleList(canvas);//绘制刻度和横线
        //drawTimeList(canvas);//绘制日期
        if (bpMapV != null && bpMapV.size() > 0) {
            drawBpDatas(canvas);
            //绘制时间
            drawBPTimes(canvas);
        } else {
//            canvas.translate(width / 2, -height / 2);
//            canvas.save();
            canvas.drawText(getResources().getString(R.string.nodata), width / 2 - (getTextWidth(emptyPaint, getResources().getString(R.string.nodata)) / 2), -height / 2, emptyPaint);
        }

    }

    //绘制时间
    private void drawBPTimes(Canvas canvas) {
        //间隔
        float mCurrWidth = width / 13;

        Collections.sort(bpTimeV, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        if (bpTimeV.size() >= 9) {
            int interval = bpTimeV.size() / 2;
            //Log.e("BP","------间隔="+interval);

            for (int i = 0; i < interval; i++) {
                String timeTxt = bpTimeV.get(i == 5 ? 11 : i * 2);
                canvas.drawText(timeTxt, mCurrWidth * (i == 5 ? 11 : i * 2) + (getTextWidth(timePaint, timeTxt) / 2) + dp2px(2), 0, timePaint);
            }

        } else {
            for (int i = 0; i < bpTimeV.size(); i++) {
                String timeTxt = bpTimeV.get(i);
                canvas.drawText(timeTxt, mCurrWidth * i + (getTextWidth(timePaint, timeTxt) / 2) + dp2px(2), 0, timePaint);
            }
        }


    }

    //绘制血压数据
    private void drawBpDatas(Canvas canvas) {
        try {
            //间隔
            float mCurrWidth = width / 13;
            //画圆点的半径
            float mCircleRaido = dp2px(3);
            //Log.e("BP","---------bpMapV="+bpMapV.size());
            for (int i = 0; i < bpMapV.size(); i++) {
                Map<Integer, Integer> tempMap = bpMapV.get(i);
                if (tempMap != null) {
                    for (Map.Entry<Integer, Integer> vMaps : tempMap.entrySet()) {

                        //高压值
                        int heightBpV = vMaps.getValue();
                        //低压值
                        int lowBpV = vMaps.getKey();
                        //x轴的值，低压和高压相等
                        float bpXvalue = mCurrWidth + mCurrWidth * i + dp2px(2);

                        canvas.drawCircle(bpXvalue, -lowBpV * ratio + timeStroke / 2, mCircleRaido, lowPaint);

                        canvas.drawCircle(bpXvalue, -heightBpV * ratio + timeStroke / 2, mCircleRaido, highPaint);

                        Path path = new Path();//绘制连线
                        path.moveTo(bpXvalue, -lowBpV * ratio + timeStroke / 2);
                        path.lineTo(bpXvalue, -heightBpV * ratio + timeStroke / 2);
                        path.close();
                        canvas.drawPath(path, linPaint);

                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 绘制横线和刻度
     */
    private void drawScaleList(Canvas canvas) {
        if (!isScale) return;
        for (int i = 0; i < 5; i++) {
            float height = -(30 + i * 50) * ratio + timeStroke / 2;
            canvas.drawLine(valStart, height, getWidth() - valRight, height, scalePaint);
            canvas.drawText(30 + i * 50 + "", 0, height + timeStroke / 2, timePaint);
        }
    }


    public void setScale(boolean scale) {
        isScale = scale;
        //invalidate();
    }

    //设置数据，最多有12条数据
    public void setBpVerticalMap(List<Map<String, Map<Integer, Integer>>> verticalMap) {
        try {
            tempBpMap.clear();
            bpTimeV.clear();
            bpMapV.clear();
            if (verticalMap.size() > 0) {
                //大于12条数据
                int mapSize = verticalMap.size();
                if (mapSize >= 12) {
                    for (int i = mapSize - 12; i < mapSize; i++) {
                        //遍历map
                        Map<String, Map<Integer, Integer>> map = verticalMap.get(i);
                        //Log.e("BP","--------map="+map.toString());
                        tempBpMap.add(map);
                        for (Map.Entry<String, Map<Integer, Integer>> mp : map.entrySet()) {
                            //日期
                            String homeBPDate = mp.getKey();
                            //Log.e("BP","---------homeBPDate="+homeBPDate);
                            bpTimeV.add(homeBPDate);

                        }

                    }

                    //排序
                    Collections.sort(bpTimeV, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    for (int i = tempBpMap.size() - 1; i >= 0; i--) {
                        Map<String, Map<Integer, Integer>> strMap = tempBpMap.get(i);
                        String timeStr = bpTimeV.get(i).trim();
                        Map<Integer, Integer> vMap = strMap.get(timeStr);
                        bpMapV.add(vMap);

                    }

                } else {
                    for (Map<String, Map<Integer, Integer>> mapMap : verticalMap) {
                        for (Map.Entry<String, Map<Integer, Integer>> mp : mapMap.entrySet()) {
                            String bpTime = mp.getKey();
                            bpTimeV.add(bpTime);
                        }
                    }

                    //排序
                    Collections.sort(bpTimeV, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });


                    for (int i = 0; i < verticalMap.size(); i++) {
                        Map<Integer, Integer> mmmP = verticalMap.get(i).get(bpTimeV.get(i));
                        bpMapV.add(mmmP);
                    }

                }

            }

            //Log.e("BP","-------bpTimeV-size="+bpTimeV.size());
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * dp转换px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * sp转换px
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }


    /**
     * 获取文字的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    private int getTextWidth(Paint paint, String text) {
        return (int) paint.measureText(text);
    }


}
