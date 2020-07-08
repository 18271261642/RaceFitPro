package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/4.
 */
public class B30BloadDataView extends View {

    private static final String TAG = "B30CusBloadView";

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

    //画横线的画笔
    private Paint horiPaint;

    //画刻度尺的画笔
    private Paint scalePaint;

    private int horiColor;
    //高度
    private int height;
    //宽度
    private int width;

    //当前点的宽度
    private float mCurrentWidth;
    //当前时间的宽度
    private float mTimeCurrentWidth;

    private float mTxtCurrentWidth;

    //是否绘制刻度和横线
    private boolean isScal = false;

    /**
     * 画笔大小:线,时间,圆半径
     */
    private int linStroke, timeStroke, radioStroke;


    private Paint txtPaint;
    private String txtStr;
    private float txtX, txtY;


    //时间点
    private List<String> timeList = new ArrayList<>();
    //点的集合
    private List<SparseIntArray> mapList = new ArrayList<>();

    private String[] timeStr = new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "23:59"};

    public B30BloadDataView(Context context) {
        super(context);
    }

    public B30BloadDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public B30BloadDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            linStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_linStroke, 2);
            timeStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_timeStroke, 20);
            radioStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusBloadView_radioStroke, 4);
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        lowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lowPaint.setColor(lowColor);
        lowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //linPaint.setTextAlign(Paint.Align.CENTER);

        highPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highPaint.setColor(hightColor);
        highPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        highPaint.setTextAlign(Paint.Align.LEFT);

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(linColor);
        linPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linPaint.setStrokeWidth(linStroke);


        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(timeColor);
        timePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timePaint.setTextSize(timeStroke);
        timePaint.setTextAlign(Paint.Align.LEFT);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
        emptyPaint.setTextSize(timeStroke);

        horiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        horiPaint.setStrokeWidth(linStroke);
        horiPaint.setColor(Color.WHITE);
        horiPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStrokeWidth(linStroke);
        scalePaint.setColor(Color.WHITE);
        scalePaint.setTextSize(timeStroke);
        scalePaint.setStyle(Paint.Style.FILL_AND_STROKE);


        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setStrokeWidth(10f);
        txtPaint.setAntiAlias(true);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(20f);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = getHeight();
        width = getWidth();
        //Log.e(TAG, "----onMeasure---=" + height + "--wi=" + width);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //Log.e(TAG, "----onSizeChanged=" + w + "--h=" + h + "--oldw=" + oldh + "-oldh=" + oldh);
        height = getHeight();
        width = getWidth();
        //Log.e(TAG, "-----onSize=" + height + "--wi=" + width);
        if (mapList != null && mapList.size() > 0) {
            mCurrentWidth = width / mapList.size();
            //Log.e(TAG, "----mCurrentWidth=" + mCurrentWidth);
        }
        if (timeList != null && timeList.size() > 0) {
            mTimeCurrentWidth = width / timeList.size();
            //Log.e(TAG, "---mTimeCurrentWidth-" + mTimeCurrentWidth);
        }
        mTxtCurrentWidth = width / timeStr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, getHeight());
        canvas.save();
        //Log.e(TAG, "-----onDraw----");
        //绘制横线
        //drawHorizonLin(canvas);
        //绘制坐标
        // drawScale(canvas);
        //绘制日期
        drawTimeLin(canvas);
        //绘制点
        drawListPoints(canvas);
        //绘制无数据
        drawEmptyTxt(canvas);

        drawDataTxt(canvas);


    }

    //绘制显示的数字
    private void drawDataTxt(Canvas canvas) {
        canvas.drawText(getTxtStr(), getTxtX(), getTxtY(), txtPaint);
    }

    private void drawScale(Canvas canvas) {
        for (int i = 1; i <= 5; i++) {
            canvas.drawText(30 * i + "", 10, -i * getHeight() / 5, scalePaint);
        }
    }


    private void drawEmptyTxt(Canvas canvas) {
        if (mapList == null || mapList.size() == 0) {
            canvas.translate(getWidth() / 2, -getHeight() / 2);
            canvas.drawText("No Data", 0, 0, emptyPaint);
        }
    }


    //绘制点
    private void drawListPoints(Canvas canvas) {
        if (mapList == null || mapList.isEmpty()) return;
        boolean big = mapList.size() < 15;
        float mCirRadio = big ? radioStroke * 2 : radioStroke;//圆的半径
        linPaint.setStrokeWidth(big ? linStroke * 2 : linStroke);// 画笔大小
//        float start = getWidth() / mapList.size();
        float start = getWidth() / (mapList.size() + 1);
        //绘制连线
        Path path = new Path();
        if (mapList != null && mapList.size() > 0) {
            for (int i = 0; i < mapList.size(); i++) {
//            for (int i = 0; i < mapList.size(); i++) {
                SparseIntArray intArray = mapList.get(i);
//                float xPoint = i * getWidth() / mapList.size() + start;
                float xPoint = start + start * i - mCirRadio;
//                float xPoint = start * i - mCirRadio / 2;
                if ((-intArray.keyAt(0)) != 0) {//判断，去除为0是所绘制的点

                    if (intArray.keyAt(0) > intArray.valueAt(0)) {
//                        //绘制低压的点
//                        canvas.drawCircle(xPoint, -intArray.valueAt(0) - 100, mCirRadio, lowPaint);//180
//                        //绘制高压的点
//                        canvas.drawCircle(xPoint, -intArray.keyAt(0) - 230, mCirRadio, highPaint);
//
//                        path.moveTo(xPoint, -intArray.valueAt(0) - 100);
//                        path.lineTo(xPoint, -intArray.keyAt(0) - 230);
//                        canvas.drawPath(path, linPaint);
                        //不正常的值过滤
                        if (!isNormalL(intArray.keyAt(0),intArray.valueAt(0)))return;
                        //绘制低压的点
                        canvas.drawCircle(xPoint, -intArray.valueAt(0) - 150, mCirRadio, lowPaint);//180
                        //绘制高压的点
                        canvas.drawCircle(xPoint, -intArray.keyAt(0) - 150, mCirRadio, highPaint);

                        path.moveTo(xPoint, -intArray.valueAt(0) - 150);
                        path.lineTo(xPoint, -intArray.keyAt(0) - 150);
                        canvas.drawPath(path, linPaint);
                    } else {
//                        //绘制低压的点
//                        canvas.drawCircle(xPoint, -intArray.keyAt(0) - 100, mCirRadio, lowPaint);//180
//                        //绘制高压的点
//                        canvas.drawCircle(xPoint, -intArray.valueAt(0) - 230, mCirRadio, highPaint);
//
//                        path.moveTo(xPoint, -intArray.keyAt(0) - 100);
//                        path.lineTo(xPoint, -intArray.valueAt(0) - 230);
//                        canvas.drawPath(path, linPaint);

                        //不正常的值过滤
                        if (!isNormalL(intArray.valueAt(0),intArray.keyAt(0)))return;

                        //绘制低压的点
                        canvas.drawCircle(xPoint, -intArray.keyAt(0) - 150, mCirRadio, lowPaint);//180
                        //绘制高压的点
                        canvas.drawCircle(xPoint, -intArray.valueAt(0) - 150, mCirRadio, highPaint);

                        path.moveTo(xPoint, -intArray.keyAt(0) - 150);
                        path.lineTo(xPoint, -intArray.valueAt(0) - 150);
                        canvas.drawPath(path, linPaint);
                    }

                }
            }
        }
        path.close();
    }

    /**
     * 是否为正常的值
     * @param Hvalue
     * @param Lvalue
     * @return
     */
    boolean isNormalL(int Hvalue,int Lvalue){
        boolean normal = true;
        if (Hvalue < 100){
            normal = false;
        }

        if (Hvalue > 240){
            normal = false;
        }

        if (Lvalue > 100){
            normal = false;
        }
        return normal;
    }



    //绘制日期
    private void drawTimeLin(Canvas canvas) {
        if (timeList == null || timeList.isEmpty()) return;
        if (timeList.size() < 9) {
            for (int i = 0; i < timeList.size(); i++) {
                canvas.drawText(timeList.get(i), i * getWidth() / timeList.size() + 20, -15, timePaint);//Y -15 解决底部日期靠底部
            }
        } else if (timeList.size() <= 30 && timeList.size() > 8) {
            for (int i = 0; i < timeList.size(); i++) {
                if (i / 5 == 0) {
                    canvas.drawText(timeList.get(i), i * getWidth() * 5 / timeList.size(), -20, timePaint);
                }
            }
        }

    }

    public List<String> getTimeList() {
        return timeList;
    }

    /**
     * 刷新页面
     *
     * @param mapList  血压平均数据源
     * @param timeList 日期列表(传7个数据进来)
     */
    public void updateView(List<SparseIntArray> mapList, List<String> timeList) {
        this.mapList.clear();
        this.mapList.addAll(mapList);
        this.timeList.clear();
        this.timeList.addAll(timeList);
        setTxtStr("");
        invalidate();
    }

    public void setTimeList(ArrayList<String> timeList) {
        this.timeList = timeList;
        invalidate();
    }

    public List<SparseIntArray> getMapList() {
        return mapList;
    }

    public void setMapList(List<SparseIntArray> mapList) {
        this.mapList = mapList;
        invalidate();
    }

    public boolean isScal() {
        return isScal;
    }

    public void setScal(boolean scal) {
        isScal = scal;
        invalidate();
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }


    public String getTxtStr() {
        return txtStr == null ? "" : txtStr;
    }

    public void setTxtStr(String txtStr) {
        this.txtStr = txtStr;
    }

    public float getTxtX() {
        return txtX;
    }

    public void setTxtX(float txtX) {
        this.txtX = txtX;
    }

    public float getTxtY() {
        return txtY;
    }

    public void setTxtY(float txtY) {
        this.txtY = txtY;
    }

    int startX;
    int startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mapList == null || mapList.isEmpty()) return true;
        boolean big = mapList.size() < 15;
        float mCirRadio = big ? radioStroke * 2 : radioStroke;//圆的半径
        linPaint.setStrokeWidth(big ? linStroke * 2 : linStroke);// 画笔大小
//        float start = getWidth() / mapList.size();
        float start = getWidth() / (mapList.size() + 1);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.startX = (int) event.getRawX();
                this.startY = (int) event.getRawY();
                for (int i = 0; i < mapList.size(); i++) {
                    SparseIntArray intArray = mapList.get(i);
//                    float xPoint = i * getWidth() / mapList.size() + start;
                    float xPoint = start + start * i - mCirRadio;
                    if (startX - 20 > xPoint - start / 2 && startX - 20 < xPoint + startX / 2) {
//                    if (startX > xPoint - start && startX < xPoint + start) {
//                        -intArray.keyAt(0) - 180
//                        -intArray.valueAt(0) - 180
                        int hiBload = intArray.valueAt(0);
                        int lowBload = intArray.keyAt(0);
                        if (hiBload == 0 || lowBload == 0) {
                            setTxtStr("");
                            setTxtY(0);
                        }
                        int max = 170;
                        if (hiBload > lowBload) {
                            setTxtStr(hiBload == 0 ? "" : hiBload + "/" + lowBload + "");
                            setTxtY(-intArray.valueAt(0) - max);
                        } else if (hiBload < lowBload) {
                            setTxtStr(lowBload == 0 ? "" : lowBload + "/" + hiBload + "");
                            setTxtY(-intArray.keyAt(0) - max);
                        }
                        setTxtX(xPoint - getTextWidth(txtPaint, getTxtStr()) / 2);


                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
//                setTxtStr("");
//                setTxtY(0);
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                setTxtStr("");
//                setTxtY(0);
//                invalidate();
                break;
        }
        return true;
    }


    /**
     * 精确计算文字宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}