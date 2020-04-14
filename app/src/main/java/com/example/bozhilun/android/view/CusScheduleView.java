package com.example.bozhilun.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.bozhilun.android.R;

/**
 * Created by Admin
 * Date 2020/3/4
 */
public class CusScheduleView extends View {

    //总的进度画笔
    private Paint allSchedulePaint;
    //当前进度画笔
    private Paint currSchedulePaint;

    //总的进度颜色
    private int allShceduleColor;
    //当前进度颜色
    private int currShceduleColor;



    //宽度
    private float mWidth,mHeight;


    private float allScheduleValue;
    private float currScheduleValue = 0;


    public CusScheduleView(Context context) {
        super(context);
    }

    public CusScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttar(context,attrs);
    }

    public CusScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttar(context,attrs);
    }

    private void initAttar(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusScheduleView);
        if(typedArray != null){
            allShceduleColor = typedArray.getColor(R.styleable.CusScheduleView_cus_all_schedule_color,Color.BLUE);
            currShceduleColor = typedArray.getColor(R.styleable.CusScheduleView_cus_curr_schedule_color,Color.BLUE);
            typedArray.recycle();
        }

        initPaint();
    }



    private void initPaint() {
        allSchedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        allSchedulePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        allSchedulePaint.setAntiAlias(true);
        allSchedulePaint.setColor(allShceduleColor);
        allSchedulePaint.setTextSize(2f);

        currSchedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currSchedulePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        currSchedulePaint.setColor(currShceduleColor);
        currSchedulePaint.setTextSize(2f);
        currSchedulePaint.setAntiAlias(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,mHeight);
        canvas.save();
        drawSchedule(canvas);
    }

    private void drawSchedule(Canvas canvas) {

        float currV = currScheduleValue >= allScheduleValue ? mWidth : currScheduleValue / allScheduleValue * mWidth;
        RectF rectF = new RectF(0,-mHeight,mWidth,0);
        canvas.drawRoundRect(rectF,mHeight,mHeight,allSchedulePaint);

        RectF currRectf = new RectF(0,-mHeight,currV,0);
        canvas.drawRoundRect(currRectf,mHeight,mHeight,currSchedulePaint);

    }


    public float getAllScheduleValue() {
        return allScheduleValue;
    }

    public void setAllScheduleValue(float allScheduleValue) {
        this.allScheduleValue = allScheduleValue;
    }

    public float getCurrScheduleValue() {
        return currScheduleValue;
    }

    public void setCurrScheduleValue(float currScheduleValue) {
        this.currScheduleValue = currScheduleValue;
    }
}
