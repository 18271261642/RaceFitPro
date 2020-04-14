package com.example.bozhilun.android.b30.b30view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.List;

/**
 * Created by Administrator on 2018/8/16.
 */

public class B15PCusSleepView extends View {

    private static final String TAG = "B30CusSleepView";
    //深睡颜色
    private int hightSleepColor;
    //浅睡颜色
    private int deepSleepColor;
    //清醒状态颜色
    private int awakeSleepColor;

    private float sleepHeight;

    private Paint hightPaint;
    private Paint deepPaint;
    private Paint awakePaint;
    private Paint emptyPaint;

    //无数据时的颜色
    private int noDataColor;

    //线的画笔
    private Paint linPaint;

    private float width;

    private List<Integer> sleepList;
    /**
     * 画笔大小:空字符串
     */
    private int sleepEmptyData;

    //默认的seek起点位置
    private float seekX = 50;
    private String timeTxt = "";

    //是否显示标记线
    private boolean isSeekBarShow = false;


    //#fcd647 清醒  潜水 #a6a8ff 深睡 #b592d6
    public B15PCusSleepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public B15PCusSleepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.B30CusSleepView);
        if(typedArray != null){
            hightSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_lightSleepColor,0);
            deepSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_deepSleepColor,0);
            awakeSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_awakeSleepColor,0);
            sleepHeight = typedArray.getDimension(R.styleable.B30CusSleepView_sleepViewHeight,DimenUtil.dp2px(context,180));
            sleepEmptyData = typedArray.getDimensionPixelSize(R.styleable.B30CusSleepView_sleepEmptyData,10);
            noDataColor = typedArray.getColor(R.styleable.B30CusSleepView_b30SleepNoDataColor,Color.WHITE);
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
        emptyPaint.setStrokeWidth(2f);
        emptyPaint.setTextSize(dp2px(10f));

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(Color.WHITE);
        linPaint.setStrokeWidth(2f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        Log.e(TAG,"---width-="+width);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //坐标点平移
        canvas.translate(0,getHeight());
       // canvas.rotate(270);
        canvas.save();
        if(sleepList != null && sleepList.size()>0){
           float mCurrentWidth = width/sleepList.size();
            //Log.e(TAG,"---size=-"+sleepList.size()+"-mCurrentWidth="+mCurrentWidth);
            for(int i = 0;i<sleepList.size();i++){
                if(sleepList.get(i) == 0){  //浅睡
                    RectF rectF = new RectF(i*mCurrentWidth,
                            -dp2px(130),(1+i)*mCurrentWidth,
                            0);
                    canvas.drawRect(rectF,hightPaint);
                }
                else if(sleepList.get(i) == 1){    //深睡
                    RectF rectF = new RectF(i*mCurrentWidth,-dp2px(80),(1+i)*mCurrentWidth,0);
                    canvas.drawRect(rectF,deepPaint);

                }else if(sleepList.get(i) == 2){    //清醒
                    RectF rectF = new RectF(i*mCurrentWidth,-dp2px(160),
                            (i+1)*mCurrentWidth,0);
                    canvas.drawRect(rectF,awakePaint);

                }

            }

            if(isSeekBarShow){
                //绘制一条白线
                RectF linRectF = new RectF(seekX * mCurrentWidth,-dp2px(160),seekX * mCurrentWidth+10,0);
                canvas.drawRect(linRectF,linPaint);

                linPaint.setTextSize(30f);
                if(seekX<=sleepList.size()/2){
                    linPaint.setTextAlign(Paint.Align.LEFT);
                }else{
                    linPaint.setTextAlign(Paint.Align.RIGHT);
                }

                //绘制显示的时间
                canvas.drawText(timeTxt,seekX<=sleepList.size()/2?seekX * mCurrentWidth+mCurrentWidth+10:seekX * mCurrentWidth-mCurrentWidth-10,
                        -dp2px(140),linPaint);
            }
        }else{
            drawEmptyTxt(canvas);
        }

    }

    //是否显示标线
    public void setSeekBarShow(boolean seekBarShow) {
        isSeekBarShow = seekBarShow;
    }

    //是否显示标线
    public void setSeekBarShow(boolean seekBarShow,int nor) {
        isSeekBarShow = seekBarShow;
        invalidate();
    }

    //绘制文字
    public void setSleepDateTxt(String txt){
        this.timeTxt = txt;
    }



    //显示标线
    public void setSeekBarSchdue(int position){
        seekX = position;
        invalidate();

    }

    public void drawEmptyTxt(Canvas canvas) {
        if(sleepList == null || sleepList.size()<=0){
            canvas.drawText("No Data",width/2-(getTextWidth(emptyPaint,"No Data")/2),-getHeight()/2,emptyPaint);
        }

    }

    public List<Integer> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<Integer> sleepList) {
        this.sleepList = sleepList;
        invalidate();
    }

    /**
     * dp转px
     */
    public int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
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
