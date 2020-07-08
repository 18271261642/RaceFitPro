package com.example.bozhilun.android.b18.modle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.bozhilun.android.B18I.b18iutils.MiscUtil;
import com.example.bozhilun.android.R;


/**
 * Created by Admin
 * Date 2020/1/3
 */
public class CusLinView extends View {

    private static final String TAG = "CusLinView";

    //划线
    private Paint linPaint;

    //字
    private Paint txtPaint;

    //标线的颜色
    private int cadenceColor;

    private float width,height;

    //最大值
    private float maxValue;

    //是否显示步频
    private boolean isTxt = false;



    public CusLinView(Context context) {
        super(context);
    }

    public CusLinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public CusLinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusLinView);
        if(typedArray != null){
            cadenceColor = typedArray.getColor(R.styleable.CusLinView_cus_lin_view_color, Color.parseColor("#FA9850"));
            typedArray.recycle();
        }

        initPaint();

    }

    private void initPaint() {
        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setStrokeWidth(2f);
        linPaint.setStyle(Paint.Style.STROKE);
        linPaint.setTextSize(15f);
        linPaint.setColor(cadenceColor);
        linPaint.setAntiAlias(true);
        linPaint.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setStrokeWidth(2f);
        txtPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));
        txtPaint.setColor(cadenceColor);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //Log.e(TAG,"----height--="+height+"---="+ MiscUtil.dipToPx(getContext(),120));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,height);

        if(isTxt){
            canvas.drawText("步频",0f,-MiscUtil.dipToPx(getContext(),100f),txtPaint);
            canvas.drawText("120",width- MiscUtil.getTextWidth(txtPaint,"120"),-MiscUtil.dipToPx(getContext(),100f),txtPaint);
        }

        canvas.drawLine(0f,-MiscUtil.dipToPx(getContext(),100f),width,-MiscUtil.dipToPx(getContext(),100f),linPaint);

    }


    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }


    public boolean isTxt() {
        return isTxt;
    }

    public void setTxt(boolean txt) {
        isTxt = txt;
    }
}
