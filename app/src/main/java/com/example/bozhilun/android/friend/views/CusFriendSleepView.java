package com.example.bozhilun.android.friend.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友睡眠详细数据展示的view
 * Created by Admin
 * Date 2019/7/25
 */
public class CusFriendSleepView extends View {

    private static final String TAG = "CusFriendSleepView";


    //深睡的画笔
    private Paint deepPaint;
    //浅睡的画笔
    private Paint lowPaint;
    //清醒的画笔
    private Paint awakePaint;
    //无数据时的画笔
    private Paint emptyPaint;

    //view的宽度
    private float mWidth;
    //高度
    private float mHeight;

    //所有的时间
    private int allSleepTime = 0;

    //滑动时显示时间的paint
    private Paint seekBarPaint;

    //是否绘制滑动线
    private boolean isShowSeekLin = false;
    //滑动时显示的时间
    private String timeTxt = null;
    //seekBar默认显示的位置
    private float seekX = 50;

    //睡眠时间段
    private List<CusFriendBean> sleepList = new ArrayList<>();



    public CusFriendSleepView(Context context) {
        super(context);
        initAttr();
    }

    public CusFriendSleepView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        initAttr();
    }

    public CusFriendSleepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr();
    }

    private void initAttr() {
        initPaint();
    }


    private void initPaint(){
        deepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deepPaint.setAntiAlias(true);
        deepPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        deepPaint.setColor(Color.parseColor("#2D468A"));
        //deepPaint.setTextAlign(Paint.Align.CENTER);
        deepPaint.setDither(true);
        deepPaint.setStrokeWidth(1f);

        //Color.parseColor("#8898E6")
        lowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lowPaint.setStrokeWidth(1f);
        lowPaint.setDither(true);
        //lowPaint.setTextAlign(Paint.Align.CENTER);
        lowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lowPaint.setColor(Color.parseColor("#8898E6"));
        lowPaint.setAntiAlias(true);

        awakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //awakePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        awakePaint.setStrokeWidth(1f);
        awakePaint.setDither(true);
        awakePaint.setTextAlign(Paint.Align.CENTER);
        awakePaint.setColor(Color.parseColor("#fcd647"));
        awakePaint.setAntiAlias(true);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setStrokeWidth(2f);
        emptyPaint.setColor(Color.WHITE);
        emptyPaint.setTextSize(dp2px(15f));

        seekBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        seekBarPaint.setStrokeWidth(2f);
        seekBarPaint.setColor(Color.WHITE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,getHeight());
        canvas.save();
        if(sleepList.size() <=0){
            drawEmptyTxt(canvas);
        }else{
            /**
             * 1-清醒；2-浅睡；3-深睡
             */
            //每一分钟的宽度
            float mCurrWidth = mWidth / allSleepTime - 0.03f;
            //Log.e(TAG,"------mCurrWidth="+mCurrWidth+"---mw="+mWidth+"--all="+allSleepTime);
            float xCountSum = 0;
            for(int i = 0;i<sleepList.size();i++){
                //类型
                int sleepType = sleepList.get(i).getType();
                //时长
                float sleepTime = sleepList.get(i).getSleepTime() * mCurrWidth;
                xCountSum +=  sleepTime;
                if(sleepType == 1){     //清醒
                    RectF rectF = new RectF(xCountSum-sleepTime,-dp2px(140),xCountSum,0);
                    canvas.drawRect(rectF,awakePaint);
                    //Log.e(TAG,"---清醒----xCountSum--="+xCountSum+"---="+(xCountSum-sleepTime));
                }else if(sleepType == 2){   //浅睡
                    RectF rectF = new RectF(xCountSum-sleepTime,-dp2px(100),xCountSum,0);
                    canvas.drawRect(rectF,lowPaint);

                }else if(sleepType == 3){   //深睡
                    RectF rectF = new RectF(xCountSum-sleepTime,-dp2px(60),xCountSum,0);
                    canvas.drawRect(rectF,deepPaint);
                }

            }


            if(isShowSeekLin){
                //绘制一条白线
                RectF linRectF = new RectF(seekX * mCurrWidth,-dp2px(140),seekX * mCurrWidth+5,0);
                canvas.drawRect(linRectF,seekBarPaint);

                seekBarPaint.setTextSize(dp2px(10f));
                if(seekX<=allSleepTime/2){
                    seekBarPaint.setTextAlign(Paint.Align.LEFT);
                }else{
                    seekBarPaint.setTextAlign(Paint.Align.RIGHT);
                }
                if(timeTxt == null)return;
                //绘制显示的时间
                canvas.drawText(timeTxt,seekX<=allSleepTime/2?seekX * mCurrWidth+mCurrWidth+10:seekX * mCurrWidth-mCurrWidth-10,
                        -dp2px(140),seekBarPaint);
            }

        }

    }

    //绘制无数据显示
    private void drawEmptyTxt(Canvas canvas) {
        canvas.drawText(getResources().getString(R.string.nodata),mWidth / 2 - getTextWidth(emptyPaint,getResources().getString(R.string.nodata))/2,-mHeight / 2,emptyPaint);

    }

    //总的睡眠时间
    public void setAllSleepTime(int allSleepTime) {
        this.allSleepTime = allSleepTime;
    }

    //设置数据源
    public void setSleepList(List<CusFriendBean> mSleepList) {
        sleepList.clear();
        this.sleepList.addAll(mSleepList);
        invalidate();
    }

    public void setShowSeekLin(boolean showSeekLin) {
        isShowSeekLin = showSeekLin;
    }


    public void setShowSeekLin(boolean showSeekLin,int nor) {
        isShowSeekLin = showSeekLin;
        invalidate();
    }

    public void setTimeTxt(String timeTxt) {
        this.timeTxt = timeTxt;
    }


    public void setSeekX(float seekX) {
        this.seekX = seekX;
        invalidate();
    }

    /**
     * 获取文字的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    private float getTextWidth(Paint paint, String text) {
        return  paint.measureText(text);
    }

    /**
     * dp转px
     */
    public int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

}
