package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
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

    private float width;

    String colors[] = new String[]{"#50E9F7", "#4FC2F8" ,"#039BE6", "#FF669F", "#FF307E"};

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
            heartViewHeight = typedArray.getDimension(R.styleable.CusHeartSchedulView_heartViewHeight,DimenUtil.dp2px(context,45));
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
        width = getMeasuredWidth();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float avgWidth = width / 5;
        paint.setColor(Color.parseColor(colors[0]));
        canvas.drawLine(0,heartViewHeight,avgWidth,heartViewHeight,paint);
        //paint.reset();

        paint.setColor(Color.parseColor(colors[1]));
        canvas.drawLine(avgWidth,heartViewHeight,avgWidth*2,heartViewHeight,paint);
        //paint.reset();

        paint.setColor(Color.parseColor(colors[2]));
        canvas.drawLine(avgWidth*2,heartViewHeight,avgWidth*3,heartViewHeight,paint);
        //paint.reset();

        paint.setColor(Color.parseColor(colors[3]));
        canvas.drawLine(avgWidth*3,heartViewHeight,avgWidth*4,heartViewHeight,paint);
        //paint.reset();

        paint.setColor(Color.parseColor(colors[4]));
        canvas.drawLine(avgWidth*4,heartViewHeight,avgWidth*5,heartViewHeight,paint);
       // paint.reset();

    }
}
