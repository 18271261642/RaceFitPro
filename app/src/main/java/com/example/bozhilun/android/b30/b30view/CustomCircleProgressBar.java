package com.example.bozhilun.android.b30.b30view;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.example.bozhilun.android.R;

import static android.graphics.Paint.Style.STROKE;

/**
 *
 */

public class CustomCircleProgressBar extends View {

    private static final String TAG = "CustomCircleProgressBar";

    private int outsideColor;    //进度的颜色
    private float outsideRadius;    //外圆半径大小
    private int insideColor;    //背景颜色
    private int progressTextColor;   //圆环内文字颜色
    private float progressTextSize;    //圆环内文字大小
    private float progressWidth;    //圆环的宽度
    private int maxProgress;    //最大进度
    private float progress;    //当前进度
    private int direction;    //进度从哪里开始(设置了4个值,上左下右)

    private Paint paint;
    private String progressText;     //圆环内文字
    private Rect rect;
    private String tmpTxt = null;

    private Paint txtPaint; //绘制圆环内文字的画笔

    //绘制圆环内文字 血压浓度 的画笔
    private Paint oxyChromPaint;
    //描述文字
    private String oxyDexcStr ;
    //是否绘制血氧浓度文字
    private boolean isOxyCh = false;

    //是否绘制中间进度值
    private boolean isCanvasV = true;


    //设置进度的时间 单位：毫秒
    private int scheduleDuring ;

    private ValueAnimator animator;

    enum DirectionEnum {
        LEFT(0, 180.0f),
        TOP(1, 270.0f),
        RIGHT(2, 0.0f),
        BOTTOM(3, 90.0f);

        private final int direction;
        private final float degree;

        DirectionEnum(int direction, float degree) {
            this.direction = direction;
            this.degree = degree;
        }

        public int getDirection() {
            return direction;
        }

        public float getDegree() {
            return degree;
        }

        public boolean equalsDescription(int direction) {
            return this.direction == direction;
        }

        public static DirectionEnum getDirection(int direction) {
            for (DirectionEnum enumObject : values()) {
                if (enumObject.equalsDescription(direction)) {
                    return enumObject;
                }
            }
            return RIGHT;
        }

        public static float getDegree(int direction) {
            DirectionEnum enumObject = getDirection(direction);
            if (enumObject == null) {
                return 0;
            }
            return enumObject.getDegree();
        }
    }

    public CustomCircleProgressBar(Context context) {
        this(context, null);
    }

    public CustomCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context,attrs,0);
    }



    public CustomCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs,defStyleAttr);

    }


    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircleProgressBar, defStyleAttr, 0);
        if(a != null){
            outsideColor = a.getColor(R.styleable.CustomCircleProgressBar_outside_color, ContextCompat.getColor(getContext(), R.color.colorPrimary));
            outsideRadius = a.getDimension(R.styleable.CustomCircleProgressBar_outside_radius, DimenUtil.dp2px(getContext(), 60.0f));
            insideColor = a.getColor(R.styleable.CustomCircleProgressBar_inside_color, ContextCompat.getColor(getContext(), R.color.white));
            progressTextColor = a.getColor(R.styleable.CustomCircleProgressBar_progress_text_color, ContextCompat.getColor(getContext(), R.color.white));
            progressTextSize = a.getDimension(R.styleable.CustomCircleProgressBar_progress_text_size, DimenUtil.dp2px(getContext(), 30f));
            progressWidth = a.getDimension(R.styleable.CustomCircleProgressBar_progress_width, DimenUtil.dp2px(getContext(), 10.0f));
            progress = a.getFloat(R.styleable.CustomCircleProgressBar_progress, 50.0f);
            maxProgress = a.getInt(R.styleable.CustomCircleProgressBar_max_progress, 100);
            direction = a.getInt(R.styleable.CustomCircleProgressBar_direction, 3);

            a.recycle();
        }

        paint = new Paint();

        initPaint(context);
    }

    private void initPaint(Context mContext) {
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(progressTextColor);
        txtPaint.setTextSize(dp2px(mContext,20f));
        txtPaint.setStrokeWidth(5f);


        oxyChromPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oxyChromPaint.setColor(Color.WHITE);
        oxyChromPaint.setTextAlign(Paint.Align.CENTER);
        oxyChromPaint.setTextSize(dp2px(mContext,16f));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int circlePoint = getWidth() / 2;
        //第一步:画背景(即内层圆)
        paint.setColor(insideColor); //设置圆的颜色
        paint.setStyle(STROKE); //设置空心
        paint.setStrokeWidth(progressWidth); //设置圆的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(circlePoint, circlePoint, outsideRadius, paint); //画出圆

        //第二步:画进度(圆弧)
        paint.setColor(outsideColor);  //设置进度的颜色
        RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius, circlePoint + outsideRadius, circlePoint + outsideRadius);  //用于定义的圆弧的形状和大小的界限
        canvas.drawArc(oval, DirectionEnum.getDegree(direction), 360 * (progress / maxProgress), false, paint);  //根据进度画圆弧

        //第三步:画圆环内百分比文字
        if(isCanvasV){
            rect = new Rect();
            progressText = getProgressText();
            txtPaint.getTextBounds(progressText, 0, progressText.length(), rect);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  //获得文字的基准线

            canvas.drawText(progressText, getMeasuredWidth() / 2 - rect.width() / 2, baseline, txtPaint);

        }


        if(isOxyCh){
            Rect rect = new Rect();
            oxyChromPaint.getTextBounds(oxyDexcStr,0,oxyDexcStr.length(),rect);
            Paint.FontMetricsInt fontMetrics = oxyChromPaint.getFontMetricsInt();
            int baseline = (getMeasuredHeight()/2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  //获得文字的基准线
            //+ MiscUtil.getTextWidth(oxyChromPaint,oxyDexcStr)/2
            canvas.drawText(oxyDexcStr,getWidth() / 2 ,baseline+110,oxyChromPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int) ((2 * outsideRadius) + progressWidth);
        }
        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) ((2 * outsideRadius) + progressWidth);
        }
        setMeasuredDimension(width, height);
    }

    //中间的进度百分比
    public String getProgressText() {

      return tmpTxt != null ?tmpTxt : (int) ((progress / maxProgress) * 100) + "%";

    }

    public boolean isCanvasV() {
        return isCanvasV;
    }

    public void setCanvasV(boolean canvasV) {
        isCanvasV = canvasV;
    }

    public String getTmpTxt() {
        return tmpTxt;
    }

    public void setTmpTxt(String tmpTxt) {
        this.tmpTxt = tmpTxt;
        invalidate();
    }

    public boolean isOxyCh() {
        return isOxyCh;
    }

    public void setOxyCh(boolean oxyCh) {
        isOxyCh = oxyCh;
    }

    //设置中间的文字
    public void setProgressText(String progressText) {
        this.progressText = progressText;
        invalidate();
    }


    public String getOxyDexcStr() {
        return oxyDexcStr;
    }

    public void setOxyDexcStr(String oxyDexcStr) {
        this.oxyDexcStr = oxyDexcStr;
    }

    public int getOutsideColor() {
        return outsideColor;
    }

    public void setOutsideColor(int outsideColor) {
        this.outsideColor = outsideColor;
    }

    public float getOutsideRadius() {
        return outsideRadius;
    }

    public void setOutsideRadius(float outsideRadius) {
        this.outsideRadius = outsideRadius;
    }

    public int getInsideColor() {
        return insideColor;
    }

    public void setInsideColor(int insideColor) {
        this.insideColor = insideColor;
    }

    public int getProgressTextColor() {
        return progressTextColor;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
    }

    public float getProgressTextSize() {
        return progressTextSize;
    }

    public void setProgressTextSize(float progressTextSize) {
        this.progressTextSize = progressTextSize;
    }

    public float getProgressWidth() {
        return progressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.progressWidth = progressWidth;
    }

    public synchronized int getMaxProgress() {
        return maxProgress;
    }

    public int getScheduleDuring() {
        return scheduleDuring;
    }



    public synchronized void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            //此为传递非法参数异常
            throw new IllegalArgumentException("maxProgress should not be less than 0");
        }
        this.maxProgress = maxProgress;
    }

    public synchronized float getProgress() {
        return progress;
    }



    //加锁保证线程安全,能在线程中使用
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress should not be less than 0");
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        startAnim(progress,scheduleDuring);
    }

    //设置总共的持续时间
    public void setScheduleDuring(int scheduleDuring) {
        this.scheduleDuring = scheduleDuring;
    }

    //开始进度
    private void startAnim(float startProgress,int dur) {
        animator = ObjectAnimator.ofFloat(0, startProgress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CustomCircleProgressBar.this.progress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setStartDelay(500);
        animator.setDuration(dur * 2);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    //停止进度
    public void stopAnim(){
        if(animator != null){
            animator.pause();
            startAnim(0f,0);
            invalidate();
        }
    }

    //暂停进度
    public void stopAnim(int nor){
        if(animator != null){
            animator.pause();
            invalidate();
        }
    }

    /**
     * dp转px
     */
    private static int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
