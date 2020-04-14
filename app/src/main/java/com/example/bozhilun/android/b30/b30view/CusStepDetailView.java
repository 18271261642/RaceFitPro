package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import com.example.bozhilun.android.B18I.b18iutils.MiscUtil;
import com.example.bozhilun.android.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin
 * Date 2019/11/12
 */
public class CusStepDetailView extends View {

    private static final String TAG = "CusStepDetailView";


    //柱子的画笔
    private Paint pillarPaint;

    //空数据的画笔
    private Paint emptyPaint;


    //柱子的颜色
    private int pillarColor;
    //空数据的颜色
    private int emptyColor;


    //宽度
    private float mWidth,mHeight;

    //数据源
    private List<Integer> sourList = new ArrayList<>();


    public CusStepDetailView(Context context) {
        super(context);
    }

    public CusStepDetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public CusStepDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CusStepDetailView);
        if(typedArray != null){
            pillarColor = typedArray.getColor(R.styleable.CusStepDetailView_cus_steps_pillar_color, Color.GREEN);
            emptyColor = typedArray.getColor(R.styleable.CusStepDetailView_cus_steps_empty_color,Color.WHITE);

            typedArray.recycle();
        }

        initPaint(context);
    }

    private void initPaint(Context context) {
        pillarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pillarPaint.setAntiAlias(true);
        pillarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pillarPaint.setColor(pillarColor);
        pillarPaint.setStrokeWidth(MiscUtil.dipToPx(context,2f));

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setAntiAlias(true);
        emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        emptyPaint.setColor(emptyColor);
        emptyPaint.setTextSize(MiscUtil.dipToPx(context,15f));
        emptyPaint.setStrokeWidth(1f);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() ;
        mHeight = getMeasuredHeight();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,mHeight);
        canvas.save();
        if(sourList.isEmpty()){
            canvas.drawText(getResources().getString(R.string.nodata),mWidth / 2 -(getTextWidth(emptyPaint,getResources().getString(R.string.nodata))/2),- mHeight / 2 + MiscUtil.measureTextHeight(emptyPaint)/2,emptyPaint);

        }else{
            drawStepView(canvas);
        }

    }

    private void drawStepView(Canvas canvas) {
        try {
            float mSignWidth = mWidth / 48 ;
            //获取数据中的最大值
            int maxV = Collections.max(sourList);

            //系数
            float modulsV = maxV / mHeight;
            modulsV = modulsV + 1;

            for(int i = 0;i<48;i++){
                RectF rectF = new RectF(mSignWidth * i + mSignWidth , -(sourList.get(i) / modulsV), mSignWidth * i + mSignWidth  + dp2px(2), 0);
                canvas.drawRect(rectF, pillarPaint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置数据源
     * @param sourLists
     */
    public void setSourList(List<Integer> sourLists) {
        sourList.clear();
        this.sourList.addAll(sourLists);
        invalidate();
    }


    private float dp2px(int dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }


    /**
     * 获取文字的宽度
     *
     * @param
     *
     * @return
     */
    private float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }
}
