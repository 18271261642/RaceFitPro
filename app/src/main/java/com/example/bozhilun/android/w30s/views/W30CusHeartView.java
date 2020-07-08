package com.example.bozhilun.android.w30s.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.DimenUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class W30CusHeartView extends View {

    private static final String TAG = "B30CusHeartView";

    private String TYPE = "W30";
    //连线的paint
    private Paint linPain;
    //绘制日期的画笔
    private Paint txtPaint;
    //矩形
    private RectF rectF;
    private Paint recfPaint;
    private int recfColor;
    //画笔
    private Paint paint;
    //宽度
    private float width;
    private int mheight;
    //父控件的高度
    private float height;
    //点的颜色
    private int pointColor;
    //当前点的宽度
    private float mCurrentWidth;
    //点的半径
    private float pointRadio;

    //无数据时显示文字颜色
    private int noDataColor;

    //心率数据集合
    private List<Integer> rateDataList;

    //无数据时显示No data 的画笔
    private Paint emptyPaint;
    private Path mPath;// 路径对象

    /**
     * 画笔大小:时间,圆半径
     */
    private int timeStroke, radioStroke;

    private float txtCurrentWidth;
//    private List<Map<Float, Float>> listMap = new ArrayList<>();
//    private Path path;


    //基准线的画笔
    private Paint benchPaint;
    //是否绘制平均基准线
    private boolean isShowStandard = false;




    public W30CusHeartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public W30CusHeartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.W30CusHeartView);
        if (typedArray != null) {
            height = typedArray.getDimension(R.styleable.W30CusHeartView_w30s_parentHeight, dp2px(120));
            pointColor = typedArray.getColor(R.styleable.W30CusHeartView_w30s_pointColor, 0);
            recfColor = typedArray.getColor(R.styleable.W30CusHeartView_w30s_recfColor, 0);
            timeStroke = typedArray.getDimensionPixelSize(R.styleable.W30CusHeartView_w30s_timeStrokeHeart, sp2px(10));
            noDataColor = typedArray.getColor(R.styleable.W30CusHeartView_w30s_noDataColor,0);
            radioStroke = typedArray.getDimensionPixelSize(R.styleable.W30CusHeartView_w30s_radioStrokeHeart, DimenUtil.dp2px(context,2));
            typedArray.recycle();
        }
        initPaint();
    }

    /**
     * sp转换px
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private void initPaint() {
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(pointColor);
        txtPaint.setTextSize(timeStroke);
        txtPaint.setStrokeWidth(8f);
        txtPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(pointColor);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        linPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPain.setStyle(Paint.Style.STROKE);
        linPain.setColor(pointColor);
        linPain.setStrokeWidth(3f);

        recfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recfPaint.setStrokeWidth(height - 1);
        recfPaint.setColor(recfColor);
        recfPaint.setAntiAlias(true);

//        path = new Path();

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setStrokeWidth(5f);
        emptyPaint.setColor(noDataColor);
        emptyPaint.setAntiAlias(true);
        emptyPaint.setTextSize(timeStroke);

        // 实例化路径
        mPath = new Path();

        benchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        benchPaint.setStrokeWidth(1f);
        benchPaint.setStyle(Paint.Style.STROKE);
        benchPaint.setTextSize(15f);
        benchPaint.setColor(Color.WHITE);
        //benchPaint.setAntiAlias(true);
        DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 10);
        benchPaint.setPathEffect(dash);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(300, heightMeasureSpec);
        int width = measureSize(600, widthMeasureSpec);
        setMeasuredDimension(width, height);

        this.mheight = height;
        this.width = width;
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }


    boolean isStart = false;
    int allNumber = 0;
    int allNumberSize = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            double v = startX * (getWidth() / 50) - 2;//
            canvas.translate(0, getHeight());
            canvas.save();

            mCurrentWidth = (float) (width / 50);
            txtCurrentWidth = width / timeStr.length;

            if ((getRateDataList() == null || getRateDataList().size() <= 0)) {
                drawEmptyTxt(canvas);
            } else {

                if(isShowStandard){
                    emptyPaint.setTextSize(dp2px(10));
                    //绘制标线10
                    canvas.drawLine(0,-dp2px(10) * 2,width,-dp2px(10)*2 ,benchPaint);
                    canvas.drawText("10",0,-dp2px(10) * 2,emptyPaint);


                    //绘制标线60
                    canvas.drawLine(0,-dp2px(60),width,-dp2px(60),benchPaint);
                    canvas.drawText("60",0,-dp2px(60),emptyPaint);


                    //绘制标线90
                    canvas.drawLine(0,-dp2px(90),width,-dp2px(90) ,benchPaint);
                    canvas.drawText("90",0,-dp2px(90) ,emptyPaint);


                    //绘制标线120
                    canvas.drawLine(0,-dp2px(120),width,-dp2px(120) ,benchPaint);
                    canvas.drawText("120",0,-dp2px(120),emptyPaint);


                    //绘制标线140
                    canvas.drawLine(0,-dp2px(140),width,-dp2px(140),benchPaint);
                    canvas.drawText("140",0,-dp2px(140),emptyPaint);
                }


                allNumberSize = 0;
                allNumber = 0;//每次进入循环时清空下
                //循环绘制点和点的连线路径
                for (int i = 0; i < 48; i++) {

                    if (getRateDataList().size() - 1 >= i) {
                        if (getRateDataList() != null && getRateDataList().get(i) != null && getRateDataList().get(i) != 0) {
                            allNumberSize++;
                            allNumber += getRateDataList().get(i);//求出所有心率总数
                            canvas.drawCircle(mCurrentWidth * i + mCurrentWidth - radioStroke / 2,
                                    -dp2px(getRateDataList().get(i)  ), radioStroke, paint);
                            try {
                                if (!isStart) {
                                    if (getRateDataList().size() > 0) {
                                        // 定义路径的起点
                                        mPath.moveTo((int) (mCurrentWidth * i + mCurrentWidth - radioStroke / 2), -dp2px(getRateDataList().get(i)));
                                        isStart = true;
                                    }
                                } else {
                                    if (getRateDataList().size() > 0) {
                                        mPath.lineTo((int) (mCurrentWidth * i + mCurrentWidth - radioStroke / 2), -dp2px(getRateDataList().get(i) ));
                                    }
                                }
                            } catch (Error error) {
                                error.printStackTrace();
                            }

                        }
                    }

                }

                /**
                 * 根据进度值返回时间和值
                 */
                if (startX != 0) {
                    if ((v - (20 * (int) startX)) == -2 || (v - (20 * (int) startX)) == startX * 5 + (startX - 1) * 2) {//(v - (20 * (int) startX)) == startX*5+(startX-1)*2兼容乐视手机
                        if ((int) startX > 0 && (int) startX <= 48 && (int) startX <= getRateDataList().size()) {
                            if (!getTYPE().equals("W30")) {//此处针对于 H9只能获取24个心率点做处理，让滑动到半点的时候不会走回掉方法
                                if (startX % 2 != 0) {//判断只取整数点数据
                                    if (getRateDataList().get((int) startX - 1) == 0) {
                                        if (mDataTypeListenter != null)
                                            mDataTypeListenter.OnDataTypeListenter(getRateDataList().get((int) startX - 1), StringDate[(int) startX], false);
                                    } else {
                                        if (mDataTypeListenter != null)
                                            mDataTypeListenter.OnDataTypeListenter(getRateDataList().get((int) startX - 1), StringDate[(int) startX], true);
                                    }
                                }
                            } else {
                                if (getRateDataList().get((int) startX - 1) == 0) {
                                    if (mDataTypeListenter != null)
                                        mDataTypeListenter.OnDataTypeListenter(getRateDataList().get((int) startX - 1), StringDate[(int) startX], false);
                                } else {
                                    if (mDataTypeListenter != null)
                                        mDataTypeListenter.OnDataTypeListenter(getRateDataList().get((int) startX - 1), StringDate[(int) startX], true);
                                }
                            }

                        }
                    }
                }

//                if(isShowStandard){     //是否绘制基准线
//                    /**
//                     * 绘制平均基准线和值
//                     */
//                    if (allNumber > 0 && allNumberSize > 0) {
//                        int hearteAverage = allNumber / allNumberSize;//心率平均数
//                        linPain.setColor(pointColor);
//                        linPain.setStrokeWidth(1f);
//
//                        //平均值基准线
//                        canvas.drawLine((float) (mCurrentWidth * 0 + mCurrentWidth - radioStroke / 2),
//                                (float) (-hearteAverage - 250),
//                                (float) (mCurrentWidth * 48 + mCurrentWidth - radioStroke / 2),
//                                (float) (-hearteAverage - 250), linPain);
//
//                        emptyPaint.setTextSize(sp2px(12));
//                        //平均值
//                        //int textWidth = getTextWidth(emptyPaint, getResources().getString(R.string.nodata));
////                    canvas.drawText(hearteAverage + "",
////                            (float) (mCurrentWidth * 48 + mCurrentWidth),
////                            (float) (-hearteAverage - 250), emptyPaint);
//                        canvas.drawText(hearteAverage + "",
//                                (float) (mCurrentWidth * 48),
//                                (float) (-hearteAverage - 250), emptyPaint);
//                        invalidate();
//                    }
//                }


                //将点的连线路径绘制
                linPain.setStrokeWidth(3f);
                linPain.setColor(pointColor);
                canvas.drawPath(mPath, linPain);
                isStart = false;
                drawTimeText(canvas);
//
//                //绘制一个可以跟随手指滑动的线
//                linPain.setColor(Color.WHITE);
//                Rect rectM = new Rect((int) v - 1, -25, (int) v + 1, -getHeight());
//                canvas.drawRect(rectM, linPain);

            }

        } catch (Error error) {
            error.printStackTrace();
        }

    }

    private DataTypeListenter mDataTypeListenter;

    public void setmDataTypeListenter(DataTypeListenter mDataTypeListenter) {
        this.mDataTypeListenter = mDataTypeListenter;
    }

    public DataTypeListenter getmDataTypeListenter() {
        return mDataTypeListenter;
    }

    public interface DataTypeListenter {
        /**
         * @param value  心率值
         * @param times  时间
         * @param toTime 是否已经测量
         */
        void OnDataTypeListenter(int value, String times, boolean toTime);
    }


    private String[] StringDate = {
            "", "00:00", "00:30",
            "01:00", "01:30",
            "02:00", "02:30",
            "03:00", "03:30",
            "04:00", "04:30",
            "05:00", "05:30",
            "06:00", "06:30",
            "07:00", "07:30",
            "08:00", "08:30",
            "09:00", "09:30",
            "10:00", "10:30",
            "11:00", "11:30",
            "12:00", "12:30",
            "13:00", "13:30",
            "14:00", "14:30",
            "15:00", "15:30",
            "16:00", "16:30",
            "17:00", "17:30",
            "18:00", "18:30",
            "19:00", "19:30",
            "20:00", "20:30",
            "21:00", "21:30",
            "22:00", "22:30",
            "23:00", "23:30", "24:00"};//X轴的标注

    private String[] timeStr = new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "24:00"};
    private float startX;

    public void setSeekBar(float p) {
        if (p >= 0) {
            this.startX = p;
            Log.d("-----", this.startX + "===" + p);
            invalidate();
        }
    }

    public void setType(String type) {
        this.TYPE = type;
    }

    public String getTYPE() {
        return TYPE;
    }

    //绘制空数据时显示的文字
    private void drawEmptyTxt(Canvas canvas) {
        if (emptyPaint != null) emptyPaint.setTextSize(sp2px(15));
        int textWidth = getTextWidth(emptyPaint, getResources().getString(R.string.nodata));
        canvas.drawText(getResources().getString(R.string.nodata), width / 2 - textWidth / 2 , -height / 2- dp2px(20), emptyPaint);
    }

    //画字
    private void drawTimeText(Canvas canvas) {
        for (int i = 0; i < timeStr.length; i++) {
            emptyPaint.setTextSize(timeStroke);
            canvas.drawText(timeStr[i], txtCurrentWidth * i + txtCurrentWidth / 2, -10, txtPaint);
        }
    }


    public List<Integer> getRateDataList() {
        if (rateDataList == null) return null;
        return rateDataList;
    }

    public void setRateDataList(List<Integer> rateDataList) {
        this.rateDataList = rateDataList;
        mPath.reset();
//        this.rateDataList = m2(rateDataList);
//
//        List<Integer> list = new ArrayList<>();
//
//        for (int i = 0; i <=48; i++) {
//            list.add(85);
//        }
//        this.rateDataList=list;

        invalidate();
    }


    public List<Integer> m2(List<Integer> list) {
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 0) {
                //list.remove(temp);// 出现java.util.ConcurrentModificationException
                iterator.remove();// 推荐使用
            }
        }
        return list;
    }

    public float getPointRadio() {
        return pointRadio;
    }

    public void setPointRadio(float pointRadio) {
        this.pointRadio = pointRadio;
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

    public boolean isShowStandard() {
        return isShowStandard;
    }

    public void setShowStandard(boolean showStandard) {
        isShowStandard = showStandard;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected float dp2px(int dpVal) {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }



}
