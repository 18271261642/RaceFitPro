package com.example.bozhilun.android.b31.hrv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.bozhilun.android.R;

/**
 * Created by Admin
 * Date 2018/12/26
 */
@SuppressLint("AppCompatCustomView")
public class CusRateTextView extends TextView {

    //旋转的角度
    private int cusDegrees = 0;
    private int DEFAULT_DEGREES = 0;


    public CusRateTextView(Context context) {
        super(context);
    }

    public CusRateTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTypeArray(attrs,context);
    }

    private void initTypeArray(AttributeSet attrs,Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusRateTextView);
        cusDegrees = typedArray.getInteger(R.styleable.CusRateTextView_cus_tv_degree,
                DEFAULT_DEGREES);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(cusDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();

    }

    public void setmDegrees(int mDegrees) {
        this.cusDegrees = mDegrees;
        invalidate();
    }
}
