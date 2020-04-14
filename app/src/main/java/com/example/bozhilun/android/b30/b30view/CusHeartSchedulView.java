package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.R;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 心率进度
 */
public class CusHeartSchedulView extends View {

    private Paint paint;
    private int countColor;
    private int currentColor;
    private float heartViewHeight;

    private Paint currPaint;

    private int height;
    private int width;

    public CusHeartSchedulView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public CusHeartSchedulView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusHeartSchedulView);
        if(typedArray != null){
            countColor = typedArray.getColor(R.styleable.CusHeartSchedulView_countColor,0);
            currentColor = typedArray.getColor(R.styleable.CusHeartSchedulView_currentColor,0);
            heartViewHeight = typedArray.getDimension(R.styleable.CusHeartSchedulView_heartViewHeight,DimenUtil.dp2px(context,40));
            typedArray.recycle();
        }
        initPaint();

    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(heartViewHeight);
        paint.setColor(countColor);

        currPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currPaint.setAntiAlias(true);
        currPaint.setStrokeWidth(heartViewHeight);
        currPaint.setColor(currentColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getHeight();
        width = getWidth();
        Log.e("11111","-----111="+height+"--w="+width+"---h="+getMeasuredHeight()+"--w="+getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = getHeight();
        width = getWidth();
        Log.e("111","-----heartViewHeight="+heartViewHeight+"--height="+height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制总的进度
        canvas.drawLine(0,heartViewHeight,getWidth(),heartViewHeight,paint);
        //绘制当前进度
        canvas.drawLine(0,heartViewHeight,getWidth()-80,heartViewHeight,currPaint);
    }
}
