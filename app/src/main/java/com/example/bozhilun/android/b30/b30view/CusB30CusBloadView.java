package com.example.bozhilun.android.b30.b30view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import org.apache.commons.lang.StringUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/8/4.
 */

public class CusB30CusBloadView extends View {

    private static final String TAG = "CusB30CusBloadView";


    private int originX; // 原点x坐标

    private int originY; // 原点y坐标

    private int firstPointX; //第一个点x坐标

    private int firstMinX; // 移动时第一个点的最小x值

    private int firstMaxX; //移动时第一个点的最大x值

    private int intervalX = 80; // 坐标刻度的间隔

    private int intervalY = 80; // y轴刻度的间隔

    private float minValueY; // y轴最小值

    private float maxValueY = 0; // y轴最大值

    private int mWidth; // 控件宽度

    private int mHeight; // 控件高度

    private int paddingTop = 80;// 默认上下左右的padding

    private int paddingLeft = 120;

    private int paddingRight = 80;

    private int paddingDown = 80;

    private int scaleHeight = 10; // x轴刻度线高度

    private int textToXYAxisGap = 20; // xy轴的文字距xy线的距离

    private int leftRightExtra = intervalX / 3; //x轴左右向外延伸的长度

    private int lableCountY = 5; // Y轴刻度个数

    private int bigCircleR = 5;
    private int smallCircleR = 5;
    private int shortLine = 34; // 比例图线段长度

    private List<String> xValues = new ArrayList<>();   //x轴数据

    private List<Integer> yValues = new ArrayList<>();  //y轴数据
    private int backGroundColor = Color.parseColor("#FF8E0D"); // view的背景颜色


    private int xVSize = 0; //x轴的数据大小，也即是数据源的大小


    //手势识别
    private GestureDetector gestureDetector;
    //画笔
    private Paint paintWhite, paintBlue, paintRed, paintBack, paintText;

    //无数据时显示的画笔
    private Paint noDataPaint;


    //x轴的数据
    private List<Map<Integer, Integer>> mapList = new ArrayList<>();
    List<String> timeStrList = new ArrayList<>();
    private Map<String,Map<Integer,Integer>> tempMMap = new HashMap<>();




    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            firstPointX = paddingLeft;

            if(firstPointX == firstMinX){
                return;
            }else{
                firstMinX = mWidth - originX - (xValues.size() - 1) * intervalX - leftRightExtra;
                // 滑动时，第一个点x值最大为paddingLeft，在大于这个值就不能滑动了
                firstMaxX = firstPointX;
            }

            //Log.e(TAG, "--11-11---firstPointX=" + firstPointX + "---firstMinX=" + firstMinX + "---firstMaxX=" + firstMaxX);
            setBackgroundColor(backGroundColor);

        }
    };






    public CusB30CusBloadView(Context context) {
        super(context);
        initPaint();
        gestureDetector = new GestureDetector(context, new MyOnGestureListener());
    }

    public CusB30CusBloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        gestureDetector = new GestureDetector(context, new MyOnGestureListener());
    }

    public CusB30CusBloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        gestureDetector = new GestureDetector(context, new MyOnGestureListener());
    }


    private void initPaint() {

        paintWhite = new Paint(Paint.FILTER_BITMAP_FLAG);
        paintWhite.setStyle(Paint.Style.STROKE);
        paintWhite.setColor(Color.WHITE);


        paintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBlue.setColor(Color.WHITE);
        paintBlue.setStrokeWidth(6f);
        paintBlue.setStyle(Paint.Style.FILL_AND_STROKE);

        paintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBack.setColor(backGroundColor);
        paintBack.setStyle(Paint.Style.FILL);

        paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setColor(Color.RED);
        paintRed.setStrokeWidth(3f);
        paintRed.setStyle(Paint.Style.STROKE);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(dp2px(6));
        paintText.setStrokeWidth(2f);


        noDataPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        noDataPaint.setColor(Color.WHITE);
        noDataPaint.setTextSize(dp2px(15));


        yValues.add(30);
        yValues.add(80);
        yValues.add(130);
        yValues.add(180);
        yValues.add(230);
        yValues.add(240);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //Log.e(TAG, "------onLayout=" + changed);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            //Log.e(TAG, "--------mHeight=" + mHeight + "---mWidth=" + mWidth);
            originX = paddingLeft - leftRightExtra;
            originY = mHeight - paddingDown;
            firstPointX = paddingLeft;

            firstMinX = mWidth - originX - (xValues.size() - 1) * intervalX - leftRightExtra;

            // 滑动时，第一个点x值最大为paddingLeft，在大于这个值就不能滑动了
            firstMaxX = firstPointX;

            setBackgroundColor(backGroundColor);
        }

        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.e(TAG, "-----------onDraw-------");
        if (mapList.size() > 0) {
            drawX(canvas);
            drawBrokenLine(canvas);
            drawY(canvas);
        } else {
            canvas.drawText("No Data", mWidth / 2  - (getTextWidth(noDataPaint, "No Data")/2), mHeight / 2, noDataPaint);
        }

    }


    /**
     * 画x轴
     *
     * @param canvas
     */
    private void drawX(Canvas canvas) {
        Path path = new Path();
        path.moveTo(originX, originY);
        //Log.e(TAG, "----------xValues--size=" + xValues.size());
        for (int i = 0; i < xValues.size(); i++) {
            // x轴线
            path.lineTo(mWidth - paddingRight, originY);  // 写死不变
            // x轴箭头
            //canvas.drawLine(mWidth - paddingRight, originY, mWidth - paddingRight - 15, originY + 10, paintWhite);
            //canvas.drawLine(mWidth - paddingRight, originY, mWidth - paddingRight - 15, originY - 10, paintWhite);

            // x轴线上的刻度线
            canvas.drawLine(firstPointX + i * intervalX, originY, firstPointX + i * intervalX, originY - scaleHeight, paintWhite);
            // x轴上的文字
            canvas.drawText(xValues.get(i), firstPointX + i * intervalX - getTextWidth(paintText, "17.01") / 2,
                    originY + textToXYAxisGap + getTextHeight(paintText, "17.01"), paintText);
        }
        canvas.drawPath(path, paintWhite);

        // x轴虚线
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);

        Path path1 = new Path();
        DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        p.setPathEffect(dash);
        for (int i = 0; i < lableCountY; i++) {
            path1.moveTo(originX, mHeight - paddingDown - leftRightExtra - i * intervalY);
            path1.lineTo(mWidth - paddingRight, mHeight - paddingDown - leftRightExtra - i * intervalY);
        }
        canvas.drawPath(path1, p);
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    private void drawBrokenLine(Canvas canvas) {
        canvas.save();
        // y轴文字
        minValueY = yValues.get(0);
        for (int i = 0; i < yValues.size(); i++) {
            // 找出y轴的最大最小值
            if (yValues.get(i) > maxValueY) {
                maxValueY = yValues.get(i);
            }
            if (yValues.get(i) < minValueY) {
                minValueY = yValues.get(i);
            }
        }
        // 画折线
        float aver = ((lableCountY - 1) * intervalY / (maxValueY - minValueY));
        //aver = 1;
        if (mapList != null && mapList.size() > 0) {
            //Log.e(TAG, "-------mp-size=" + mapList.size());
            for (int i = 0; i < mapList.size(); i++) {
                Map<Integer, Integer> map = mapList.get(i);
                for (Map.Entry<Integer, Integer> mp : map.entrySet()) {
                    int lowV = mp.getKey();
                    int heightV = mp.getValue();
                    //低压的点
                    canvas.drawCircle(firstPointX + i * intervalX,
                            mHeight - paddingDown - leftRightExtra - lowV * aver + minValueY * aver, bigCircleR, paintBlue);

                    //高压的点
                    canvas.drawCircle(firstPointX + i * intervalX,
                            mHeight - paddingDown - leftRightExtra - heightV * aver + minValueY * aver, bigCircleR, paintBlue);

                    //连线
                    Path path1 = new Path();
                    path1.moveTo(firstPointX + i * intervalX,
                            mHeight - paddingDown - leftRightExtra - lowV * aver + minValueY * aver);
                    path1.lineTo(firstPointX + i * intervalX,
                            mHeight - paddingDown - leftRightExtra - heightV * aver + minValueY * aver);
                    canvas.drawPath(path1, paintBlue);
                    path1.close();

                }
            }

            //将折线超出x轴坐标的部分截取掉（左边）
            paintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            RectF rectF = new RectF(0, 0, originX, mHeight - 30);
            canvas.drawRect(rectF, paintBack);
            canvas.restore();

        }


    }

    /**
     * 画y轴
     *
     * @param canvas
     */
    private void drawY(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        path.moveTo(originX, originY);

        for (int i = 0; i < lableCountY; i++) {
            // y轴线
            if (i == 0) {
                path.lineTo(originX, mHeight - paddingDown - leftRightExtra);
            } else {
                path.lineTo(originX, mHeight - paddingDown - leftRightExtra - i * intervalY);
            }

            int lastPointY = mHeight - paddingDown - leftRightExtra - i * intervalY;
            if (i == lableCountY - 1) {
                int lastY = lastPointY - leftRightExtra - leftRightExtra / 2;
                // y轴最后一个点后，需要额外加上一小段，就是一个半leftRightExtra的长度
                canvas.drawLine(originX, lastPointY, originX, lastY, paintWhite);
                // y轴箭头
                //canvas.drawLine(originX, lastY, originX - 10, lastY + 15, paintWhite);
                //canvas.drawLine(originX, lastY, originX + 10, lastY + 15, paintWhite);
            }
        }
        canvas.drawPath(path, paintWhite);

        // y轴文字
        float space = (maxValueY - minValueY) / (lableCountY - 1);
        DecimalFormat decimalFormat = new DecimalFormat("0");
        List<String> yTitles = new ArrayList<>();
        for (int i = 0; i < lableCountY; i++) {
            String titleStr = decimalFormat.format(minValueY + (i * 50));
            String resultYStr = StringUtils.substringBefore(titleStr, ".");
            yTitles.add(resultYStr);
        }
        for (int i = 0; i < yTitles.size(); i++) {
            canvas.drawText(yTitles.get(i), originX - textToXYAxisGap - getTextWidth(paintText, "00"),
                    mHeight - paddingDown - leftRightExtra - i * intervalY + getTextHeight(paintText, "00") / 2, paintText);
        }
        // 截取折线超出部分（右边）
        paintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        RectF rectF = new RectF(mWidth - paddingRight, 0, mWidth, mHeight);
        canvas.drawRect(rectF, paintBack);
        canvas.restore();
    }


    /**
     * 手势事件
     */
    class MyOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) { // 按下事件
            return false;
        }

        // 按下停留时间超过瞬时，并且按下时没有松开或拖动，就会执行此方法
        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) { // 单击抬起
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1.getX() > originX && e1.getX() < mWidth - paddingRight && e1.getY() > paddingTop && e1.getY() < mHeight - paddingDown) {
                //注意：这里的distanceX是e1.getX()-e2.getX()
                distanceX = -distanceX;
                if (firstPointX + distanceX > firstMaxX) {
                    firstPointX = firstMaxX;
                } else if (firstPointX + distanceX < firstMinX) {
                    firstPointX = firstMinX;
                } else {
                    firstPointX = (int) (firstPointX + distanceX);
                }

                if(firstPointX == firstMinX){
                    return false;
                }
                invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        } // 长按事件

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (yValues.size() < 4) {
            return false;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void setXValues(List<String> values) {
        this.xValues = values;
    }

    public void setYValues(List<Integer> values) {
        this.yValues = values;
    }


    //设置数据源
    public void setResultMapData(List<Map<String, Map<Integer, Integer>>> resultMaps) {
        timeStrList.clear();
        mapList.clear();
        xValues.clear();
        tempMMap.clear();
        if (resultMaps != null && resultMaps.size() > 0) {

            for (int i = 0; i < resultMaps.size(); i++) {
                //遍历map
                for (Map.Entry<String, Map<Integer, Integer>> reMap : resultMaps.get(i).entrySet()) {
                    //时间
                    String bpDate = reMap.getKey();
                    tempMMap.put(bpDate,reMap.getValue());
                    if(!timeStrList.contains(bpDate))
                        timeStrList.add(bpDate);

                }
            }
            //排序时间
            Collections.sort(timeStrList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });

            for(int i = 0;i<timeStrList.size();i++){
                String timeStr = timeStrList.get(i);
                Map<Integer, Integer> tmpMap = tempMMap.get(timeStr);
                mapList.add(tmpMap);
                xValues.add(timeStr);

            }

        }

        //invalidate();
        handler.sendEmptyMessage(1001);
        postInvalidate();

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

    /**
     * 获取文字的高度
     *
     * @param paint
     * @param text
     * @return
     */
    private int getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
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

    public int getxVSize() {
        return xVSize;
    }

    public void setxVSize(int xVSize) {
        this.xVSize = xVSize;
    }
}
