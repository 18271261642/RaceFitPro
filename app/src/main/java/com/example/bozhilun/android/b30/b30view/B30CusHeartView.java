package com.example.bozhilun.android.b30.b30view;

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
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/1.
 */

public class B30CusHeartView extends View {

    private static final String TAG = "B30CusHeartView";

    //连线的paint
    private Paint linPain;
    //连线的path
    private Path linPath;
    //绘制日期的画笔
    private Paint txtPaint;
    //矩形
    private RectF rectF;
    private Paint recfPaint;
    private int recfColor;
    //画笔
    private Paint paint;
    //宽度
    private int width;
    //父控件的高度
    private float height;
    //点的颜色
    private int pointColor;
    //当前点的宽度
    private float mCurrentWidth;
    //点的半径
    private float pointRadio;
    //心率数据集合
    private List<Integer> rateDataList;

    //无数据时显示No data 的画笔
    private Paint emptyPaint;

    //线的颜色
    private int heartLineColor;

    //基准线的画笔
    private Paint benchPaint;


    //是否绘制基准线
    private boolean isCanvasBeanLin = false;


    /**
     * 画笔大小:时间,圆半径
     */
    private int timeStroke, radioStroke;

    private String[] timeStr = new String[]{"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "19:00", "23:59"};
    private float txtCurrentWidth;
    private List<Map<Float, Float>> listMap = new ArrayList<>();


    public B30CusHeartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public B30CusHeartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        rateDataList = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.B30CusHeartView);
        if (typedArray != null) {
            height = typedArray.getDimension(R.styleable.B30CusHeartView_parentHeight, dp2px(120));
            pointColor = typedArray.getColor(R.styleable.B30CusHeartView_pointColor, Color.WHITE);
            recfColor = typedArray.getColor(R.styleable.B30CusHeartView_recfColor, 0);
            heartLineColor = typedArray.getColor(R.styleable.B30CusHeartView_heartLineColor, Color.WHITE);
            timeStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusHeartView_timeStrokeHeart, sp2px(10));
            radioStroke = typedArray.getDimensionPixelSize(R.styleable.B30CusHeartView_radioStrokeHeart, DimenUtil.dp2px(context, 2));
            typedArray.recycle();
        }
        initPaint(context);
    }

    /**
     * sp转换px
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private void initPaint(Context mContext) {
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(pointColor);
        txtPaint.setTextSize(timeStroke);
        txtPaint.setStrokeWidth(8f);
        txtPaint.setTextAlign(Paint.Align.LEFT);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(pointColor);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        linPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPain.setStyle(Paint.Style.STROKE);
        linPain.setColor(heartLineColor);
        linPain.setStrokeWidth(DimenUtil.dp2px(mContext, 1.5f));


        recfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recfPaint.setStrokeWidth(height - 1);
        recfPaint.setColor(recfColor);
        recfPaint.setAntiAlias(true);

        linPath = new Path();

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setStrokeWidth(5f);
        emptyPaint.setColor(Color.parseColor("#61D9F5"));
        emptyPaint.setAntiAlias(true);
        emptyPaint.setTextSize(timeStroke);

        benchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        benchPaint.setStrokeWidth(2f);
        benchPaint.setStyle(Paint.Style.STROKE);
        benchPaint.setTextSize(15f);
        benchPaint.setColor(Color.WHITE);
        benchPaint.setAntiAlias(true);
        benchPaint.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        mCurrentWidth = (float) (width / 48);
        txtCurrentWidth = width / timeStr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, getHeight());
        canvas.save();
        drawEmptyTxt(canvas);
        //绘制日期
        drawTimeText(canvas);
        //画字
        if (rateDataList != null && rateDataList.size() > 0) {
            if (listMap != null)
                listMap.clear();
            if(isCanvasBeanLin){
                //绘制基准线10
                canvas.drawLine(0,(-10*2 - dp2px(30)),width,(-10*2 - dp2px(30)),benchPaint);
                canvas.drawText(10+"",0,(-10*2 - dp2px(35)),txtPaint);


                //绘制基准线60
                canvas.drawLine(0,(-60*2 - dp2px(30)),width,(-60*2 - dp2px(30)),benchPaint);
                canvas.drawText(60+"",0,(-60*2 - dp2px(35)),txtPaint);



                //绘制基准线 90 width-dp2px(15)
                canvas.drawLine(0,(-90*2 - dp2px(30)),width,(-90*2 - dp2px(30)),benchPaint);
                canvas.drawText(90+"",0,(-90*2 - dp2px(35)),txtPaint);


                //绘制基准线 120
                canvas.drawLine(0,(-120*2 - dp2px(30)),width,(-120*2 - dp2px(30)),benchPaint);
                canvas.drawText(120+"",0,(-120*2 - dp2px(35)),txtPaint);

            }


            //绘制连线
            for (int i = 0; i < 48; i++) {
                if (rateDataList.size() - 1 >= i) {
                    if (rateDataList.get(i) != 0) {
                        //canvas.drawCircle(i==0?dp2px(10):i*mCurrentWidth+dp2px(10),-rateDataList.get(i)-180,radioStroke,paint);
                        float pointX = (i == 0 ? dp2px(10) : i * mCurrentWidth + dp2px(10));
                        float porintY = (-rateDataList.get(i)*2 - dp2px(30));
                        Map<Float, Float> tmpMap = new HashMap<>();
                        tmpMap.put(pointX, porintY);
                        listMap.add(tmpMap);
                    }

                }

            }


            //连接点的线
            for (int i = 0; i < listMap.size(); i++) {
                for (Map.Entry<Float, Float> mp : listMap.get(i).entrySet()) {
                    if (i == 0) {
                        linPath.moveTo(mp.getKey(), mp.getValue());
                    } else {
                        linPath.lineTo(mp.getKey(), mp.getValue());
                    }
                }
            }
            canvas.drawPath(linPath, linPain);


            for (int i = 0; i < 48; i++) {
                if (rateDataList.size() - 1 >= i) {
                    if (rateDataList.get(i) != 0) {
                        canvas.drawCircle(i == 0 ? dp2px(10) : i * mCurrentWidth + dp2px(10),
                                (-rateDataList.get(i) * 2) - dp2px(30), radioStroke, paint);
                    }

                }

            }

        }


    }

    //绘制空数据时显示的文字
    private void drawEmptyTxt(Canvas canvas) {
        if (rateDataList == null || rateDataList.size() <= 0) {
            canvas.drawText("No Data", getWidth() / 2 - 40, -getHeight() / 2, emptyPaint);
        }
    }

    //画字
    private void drawTimeText(Canvas canvas) {
        for (int i = 0; i < timeStr.length; i++) {

            canvas.drawText(timeStr[i], txtCurrentWidth * i + dp2px(10), -10, txtPaint);
        }
    }

    public List<Integer> getRateDataList() {
        return rateDataList;
    }

    public void setRateDataList(List<Integer> rateDataList) {
        this.rateDataList = rateDataList;
        linPath.reset();
        invalidate();
    }

    public float getPointRadio() {
        return pointRadio;
    }

    public void setPointRadio(float pointRadio) {
        this.pointRadio = pointRadio;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public boolean isCanvasBeanLin() {
        return isCanvasBeanLin;
    }

    public void setCanvasBeanLin(boolean canvasBeanLin) {
        isCanvasBeanLin = canvasBeanLin;
    }
}
