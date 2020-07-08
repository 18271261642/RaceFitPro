package com.example.bozhilun.android.w30s.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.example.bozhilun.android.R;

import java.util.List;


/**
 * Created by Admin
 * Date 2019/3/21
 */
public class W30CusSleepChartView extends View {

    private static final String TAG = "W30CusSleepChartView";

    //深睡颜色
    private int hightSleepColor;
    //浅睡颜色
    private int deepSleepColor;
    //清醒状态颜色
    private int awakeSleepColor;

    private Paint hightPaint;
    private Paint deepPaint;
    private Paint awakePaint;
    private Paint emptyPaint;

    //无数据时显示的颜色
    private int noDataColor ;

    //宽度
    private float width,height;

    //睡眠表示的数据源
    private List<Integer> sleepResultList;

    //线的画笔
    private Paint linPaint;
    //默认的seek起点位置
    private float seekX = 50;
    private String timeTxt = "";
    //是否显示标记线
    private boolean isSeekBarShow = false;


    public W30CusSleepChartView(Context context) {
        super(context);
    }

    public W30CusSleepChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public W30CusSleepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.W30CusSleepChartView);
        if(typedArray != null){
            hightSleepColor = typedArray.getColor(R.styleable.W30CusSleepChartView_w30s_lightSleepColor,0);
            deepSleepColor = typedArray.getColor(R.styleable.W30CusSleepChartView_w30s_deepSleepColor,0);
            awakeSleepColor = typedArray.getColor(R.styleable.W30CusSleepChartView_w30s_awakeSleepColor,0);
            noDataColor = typedArray.getColor(R.styleable.W30CusSleepChartView_w30s_sleepEmptyDataColor,0);
            typedArray.recycle();
        }

        initPath();

    }

    private void initPath() {
        hightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hightPaint.setColor(hightSleepColor);
        hightPaint.setAntiAlias(true);
        hightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        hightPaint.setStrokeWidth(1f);


        deepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deepPaint.setColor(deepSleepColor);
        deepPaint.setAntiAlias(true);
        deepPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        deepPaint.setStrokeWidth(1f);


        awakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        awakePaint.setColor(awakeSleepColor);
        awakePaint.setAntiAlias(true);
        awakePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        awakePaint.setStrokeWidth(1f);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(noDataColor);
        emptyPaint.setStrokeWidth(1f);
        emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        emptyPaint.setTextSize(dp2px(13f));

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(Color.WHITE);
        linPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linPaint.setStrokeWidth(2f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //坐标点平移
        canvas.translate(0,getHeight());
        canvas.save();

        /**
         * 状态说明
         * 0,4,1,5为清醒状态
         * 2 浅睡状态
         * 3，深睡状态
         *
         */
        if(sleepResultList != null && sleepResultList.size() > 0){
            //每个的宽度
            float mCurrentWidth = width/sleepResultList.size();
            for(int i = 0;i<sleepResultList.size();i++){
                //清醒状态
                if(sleepResultList.get(i) == 4 ||
                        sleepResultList.get(i) == 1 ||sleepResultList.get(i) == 5 ){

                    RectF rectF = new RectF(i*mCurrentWidth,-dp2px(160),
                            (i+1)*mCurrentWidth,0);
                    canvas.drawRect(rectF,awakePaint);
                }
                else if(sleepResultList.get(i) == 2){   //浅睡状态
                    RectF rectF = new RectF(i*mCurrentWidth,
                            -dp2px(130),(1+i)*mCurrentWidth,
                            0);
                    canvas.drawRect(rectF,hightPaint);
                }

                else if(sleepResultList.get(i) == 3){   //深睡状态
                    RectF rectF = new RectF(i*mCurrentWidth,-dp2px(80),(1+i)*mCurrentWidth,0);
                    canvas.drawRect(rectF,deepPaint);
                }

            }


            if(isSeekBarShow){
                //绘制一条白线
                RectF linRectF = new RectF(seekX * mCurrentWidth,-dp2px(160),seekX * mCurrentWidth+dp2px(2f),0);
                canvas.drawRect(linRectF,linPaint);

                linPaint.setTextSize(30f);
                if(seekX<=sleepResultList.size()/2){
                    linPaint.setTextAlign(Paint.Align.LEFT);
                }else{
                    linPaint.setTextAlign(Paint.Align.RIGHT);
                }

                //绘制显示的时间
                canvas.drawText(timeTxt,seekX<=sleepResultList.size()/2?seekX * mCurrentWidth+mCurrentWidth+10:seekX * mCurrentWidth-mCurrentWidth-10,
                        -dp2px(140),linPaint);
            }


        }else{
            drawEmptyTxt(canvas);
        }

    }

    //绘制滑动时显示的标线
    public void setSeekBarSchdue(int position){
        this.seekX = position;
        invalidate();
    }



    //绘制滑动时显示的时间
    public void setChooseTxt(String txt){
        this.timeTxt = txt;
    }

    //绘制无数据时显示的文字
    private void drawEmptyTxt(Canvas canvas) {
        int txtWidht = getTextWidth(emptyPaint,getResources().getString(R.string.nodata));
        if(sleepResultList == null || sleepResultList.isEmpty())

            canvas.drawText(getResources().getString(R.string.nodata),width / 2 -txtWidht/2, -height/ 2,emptyPaint);
    }


    public List<Integer> getSleepResultList() {
        return sleepResultList;
    }

    public void setSleepResultList(List<Integer> sleepResultList) {
        this.sleepResultList = sleepResultList;
        invalidate();
    }

    /**
     * dp转px
     */
    public int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    public int getNoDataColor() {
        return noDataColor;
    }

    public void setNoDataColor(int noDataColor) {
        this.noDataColor = noDataColor;
        //invalidate();
    }

    public boolean isSeekBarShow() {
        return isSeekBarShow;
    }

    public void setSeekBarShow(boolean seekBarShow) {
        isSeekBarShow = seekBarShow;
    }


    public void setSeekBarShow(boolean seekBarShow,int nor) {
        isSeekBarShow = seekBarShow;
        invalidate();
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
