package com.example.bozhilun.android.b30.b30view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.R;

import java.util.List;

/**
 * 维亿魄系列手环睡眠图表，0001111200形式表示睡眠，一个节点5分钟
 * Created by Administrator on 2018/8/16.
 */

public class B30CusSleepView extends View {

    private static final String TAG = "B30CusSleepView";
    //深睡颜色
    private int hightSleepColor;
    //浅睡颜色
    private int deepSleepColor;
    //清醒状态颜色
    private int awakeSleepColor;

    //失眠状态的颜色
    private int insomniaColor;
    //快速眼动的颜色
    private int deepAndLightColor;



    //无数据是显示的文字颜色
    private int noDataColor;

    private float sleepHeight;
    //浅睡的画笔
    private Paint hightPaint;
    //深睡的画笔
    private Paint deepPaint;
    //清醒的画笔
    private Paint awakePaint;
    //无数据时的画笔
    private Paint emptyPaint;

    //失眠的画笔
    private Paint insomniaPaint;
    //快速眼动的画笔
    private Paint deepAndLightPaint;

    //精准睡眠手指滑动的虚线画笔
    private Paint preicisionLinPatin;



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

    //精准睡眠的类型，深睡、浅睡、清醒等
    private String precisionType = "";

    //是否显示标记线
    private boolean isSeekBarShow = false;

    //是否是精准睡眠
    private boolean isPrecisionSleep = false;
    //是否显示精准睡眠的滑动标线
    private boolean isPrecisionSleepLin = false;






    //#fcd647 清醒  潜水 #a6a8ff 深睡 #b592d6
    public B30CusSleepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public B30CusSleepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            sleepEmptyData = typedArray.getDimensionPixelSize(R.styleable.B30CusSleepView_sleepEmptyData,dp2px(15));
            noDataColor = typedArray.getColor(R.styleable.B30CusSleepView_b30SleepNoDataColor,Color.parseColor("#6074BF"));
            insomniaColor = typedArray.getColor(R.styleable.B30CusSleepView_b31sInsomniaColor,Color.YELLOW);
            deepAndLightColor = typedArray.getColor(R.styleable.B30CusSleepView_b31sDeepAndLightColor,Color.parseColor("#15E090"));

            typedArray.recycle();
        }
        initPath();

    }

    private void initPath() {
        hightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hightPaint.setColor(hightSleepColor);
        hightPaint.setAntiAlias(true);
        hightPaint.setDither(true);
        hightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        hightPaint.setStrokeWidth(1f);


        deepPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        deepPaint.setColor(deepSleepColor);
        deepPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        deepPaint.setAntiAlias(true);
        deepPaint.setDither(true);
        deepPaint.setStrokeWidth(1f);


        awakePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        awakePaint.setColor(awakeSleepColor);
        awakePaint.setAntiAlias(true);
        awakePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        awakePaint.setDither(true);
        awakePaint.setStrokeWidth(1f);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(noDataColor);
        emptyPaint.setStrokeWidth(1f);
        emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        emptyPaint.setTextSize(sleepEmptyData);

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(Color.WHITE);
        linPaint.setStrokeWidth(1f);


        insomniaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        insomniaPaint.setColor(insomniaColor);
        insomniaPaint.setStrokeWidth(1f);
        insomniaPaint.setAntiAlias(true);
        insomniaPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        deepAndLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deepAndLightPaint.setColor(deepAndLightColor);
        deepAndLightPaint.setAntiAlias(true);
        deepAndLightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        deepAndLightPaint.setStrokeWidth(1f);

        preicisionLinPatin = new Paint(Paint.ANTI_ALIAS_FLAG);
        preicisionLinPatin.setColor(Color.WHITE);
        preicisionLinPatin.setStrokeWidth(1f);
        preicisionLinPatin.setStyle(Paint.Style.FILL_AND_STROKE);
        preicisionLinPatin.setAntiAlias(true);
        preicisionLinPatin.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //width = getMeasuredWidth();
        Log.e(TAG,"---width-="+width+"---getHeight="+getHeight()+"---dp2="+dp2px(1));

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //坐标点平移
        canvas.translate(0,getHeight());
       // canvas.rotate(270);
        canvas.save();

        if(isPrecisionSleep){   //精准睡眠
            //精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，
            // 其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒。
            if(sleepList != null && sleepList.size() >0){
                float mCurrentWidth = width/sleepList.size();
                for(int i = 0;i<sleepList.size();i++){
                    int posionValue = sleepList.get(i);
                    switch (posionValue){
                        case 0:     //深睡   #10A1FF
                            RectF rectF = new RectF(i*mCurrentWidth,
                                    -dp2px(35),(1+i)*mCurrentWidth,
                                    0);
                            canvas.drawRect(rectF,deepPaint);
                            break;
                        case 1:     //浅睡 #29EAE3
                            RectF lowRectF = new RectF(i*mCurrentWidth,
                                    -dp2px(35 * 2),(1+i)*mCurrentWidth,
                                    -dp2px(35));
                            canvas.drawRect(lowRectF,hightPaint);
                            break;
                        case 2:     //快速眼动  #FF5BB2
                            RectF deepAndLightRectF = new RectF(i*mCurrentWidth,
                                    -dp2px(35 * 3),(1+i)*mCurrentWidth,
                                    -dp2px(35 * 2));
                            canvas.drawRect(deepAndLightRectF,deepAndLightPaint);
                            break;
                        case 3:     //失眠
                            RectF insomniaRectF = new RectF(i*mCurrentWidth,
                                    -dp2px(35 * 4),(1+i)*mCurrentWidth,
                                    -dp2px(35 * 3));
                            canvas.drawRect(insomniaRectF,insomniaPaint);
                            break;
                        case 4:     //苏醒    #FFE329
                            RectF awakeRectF = new RectF(i*mCurrentWidth,
                                    -dp2px(35*5),(1+i)*mCurrentWidth,
                                    -dp2px(35*4));
                            canvas.drawRect(awakeRectF,awakePaint);
                            break;
                    }

                }

                if(isPrecisionSleepLin){

                    //绘制一条白线
                    canvas.drawLine(seekX * mCurrentWidth ,0,seekX *mCurrentWidth,-dp2px(150),preicisionLinPatin);


                    linPaint.setTextSize(30f);
                    if(seekX<=sleepList.size()/2){
                        linPaint.setTextAlign(Paint.Align.LEFT);
                    }else{
                        linPaint.setTextAlign(Paint.Align.RIGHT);
                    }

                    //绘制显示的时间
                    canvas.drawText(precisionType+"\n"+timeTxt,seekX<=sleepList.size()/2?seekX * mCurrentWidth+mCurrentWidth+10:seekX * mCurrentWidth-mCurrentWidth-10,
                            -dp2px(120),linPaint);


                }

            }else{
                drawEmptyTxt(canvas);
            }


        }else{
            if(sleepList != null && sleepList.size()>0){
                float mCurrentWidth = width/sleepList.size();
                //Log.e(TAG,"---size=-"+sleepList.size()+"-mCurrentWidth="+mCurrentWidth);
                for(int i = 0;i<sleepList.size();i++){
                    if(sleepList.get(i) == 0){  //浅睡
                        RectF rectF = new RectF(i*mCurrentWidth,
                                -dp2px(100),(1+i)*mCurrentWidth,
                                0);
                        canvas.drawRect(rectF,hightPaint);
                    }
                    else if(sleepList.get(i) == 1){    //深睡
                        RectF rectF = new RectF(i*mCurrentWidth,-dp2px(60),(1+i)*mCurrentWidth,0);
                        canvas.drawRect(rectF,deepPaint);

                    }else if(sleepList.get(i) == 2){    //清醒
                        RectF rectF = new RectF(i*mCurrentWidth,-dp2px(140),
                                (i+1)*mCurrentWidth,0);
                        canvas.drawRect(rectF,awakePaint);

                    }

                }

                if(isSeekBarShow){
                    //绘制一条白线
                    RectF linRectF = new RectF(seekX * mCurrentWidth,-dp2px(140),seekX * mCurrentWidth+10,0);
                    canvas.drawRect(linRectF,linPaint);

                    linPaint.setTextSize(30f);
                    if(seekX<=sleepList.size()/2){
                        linPaint.setTextAlign(Paint.Align.LEFT);
                    }else{
                        linPaint.setTextAlign(Paint.Align.RIGHT);
                    }

                    //绘制显示的时间
                    canvas.drawText(timeTxt,seekX<=sleepList.size()/2?seekX * mCurrentWidth+mCurrentWidth+10:seekX * mCurrentWidth-mCurrentWidth-10,
                            -dp2px(120),linPaint);
                }

            }else{
                drawEmptyTxt(canvas);
            }
        }

    }

    public void setPrecisionSleep(boolean precisionSleep) {
        isPrecisionSleep = precisionSleep;
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

    public void setPrecisionType(String precisionType) {
        this.precisionType = precisionType;
    }

    //是否显示精准睡眠的滑动标线
    public void setPrecisionSleepLin(boolean precisionSleepLin) {
        isPrecisionSleepLin = precisionSleepLin;
    }

    //是否显示精准睡眠的滑动标线
    public void setPrecisionSleepLin(boolean precisionSleepLin,int nor) {
        isPrecisionSleepLin = precisionSleepLin;
        invalidate();
    }


    public void setPreicisionLinSchdue(int position){
        seekX = position;
        invalidate();
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
     * @param
     *
     * @return
     */
    private int getTextWidth(Paint paint, String text) {
        return (int) paint.measureText(text);
    }
}
