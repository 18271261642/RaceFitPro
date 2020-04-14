package com.example.bozhilun.android.w30s.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.w30s.utils.W30BasicUtils;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Features:
 * Founder: An
 * Create time: 2018/5/2.
 */
public class W30S_SleepChart extends View {
    private static final String TAG = "W30S_SleepChart";
    private int AD = 0;
    private int pos = 0;
    private Paint paint;
    private List<W30S_SleepDataItem> beanList = new ArrayList<>();
    private float startX = 0;

    private int defaultHeight = 160;

    float mWidth = 0f;
    float mHeight = 0f;

    public W30S_SleepChart(Context context) {
        super(context);
        intt();
        init();
    }

    public W30S_SleepChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        intt();
        init();
    }


    private void intt() {
//        if (beanList != null) {
//            beanList.clear();
//            beanList = null;
//        }
//        if (getBeanList() == null) {
//            beanList = new ArrayList<>();
//        } else {
//            beanList = getBeanList();
//        }
    }


    public List<W30S_SleepDataItem> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<W30S_SleepDataItem> w30S_sleepDataItems) {
        beanList.clear();
        this.beanList = w30S_sleepDataItems;
        invalidate();
    }


    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);//FILL_AND_STROKE  FILL
        paint.setStrokeWidth(0.1f);
        paint.setDither(true);
        paint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getHeight();
        Log.e(TAG,"----------mWidth="+mWidth+"---mHeight="+mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        paint.setColor(Color.parseColor("#fcd647"));
//        Rect rectFdd = new Rect(getWidth() - 10, 0, (int) getWidth(), getHeight());
//        canvas.drawRect(rectFdd, paint);
//        if (beanList != null)
//            Log.e(TAG, "----beanList=" + beanList.size());
        if (beanList == null || beanList.size() <= 0) return;
        if (paint == null) init();
        AD = 0;
        pos = 0;
        for (int i = 0; i < beanList.size() - 1; i++) {
            String time = beanList.get(i).getStartTime();
            String time1 = beanList.get(i + 1).getStartTime();
            String[] split = time.split("[:]");
            String[] split1 = time1.split("[:]");
            int HH = Integer.valueOf(split[0]);
            int HH1 = Integer.valueOf(split1[0]);
            //如果前一个时间大于第二个时间，那么就是晚上十二点之前的睡眠，为了激素按后面的时间和前面的时间的时间差+上24小时
            if (HH > HH1) {
                HH1 += 24;
            }
            //计算拿到后面时间点到前面一个时间点的 小时 时间差
            Integer integer = HH1 - HH;
            //计算拿到后面时间点到前面一个时间点的 分钟 时间差
            int MM = Integer.valueOf(split1[1]) - Integer.valueOf(split[1]);
            //所有睡眠的数据时间----从开始到结束的总的时间（清醒，深，浅，。。。都包括在内）
            AD += (integer * 60 + MM);
        }
        //根据设备屏幕的宽度和所有睡眠时间 划分屏幕
        float it =  mWidth /  AD;

        Log.e(TAG,"----------it="+it+"---AD="+AD);


        //startX 是滑动时传过来的值    ，startX*it = 每一个值得startX 在屏幕上走动的距离
        double v = startX * (mWidth /  AD);
        Log.e("=======清醒", AD + "-------" + it);
        for (int i = 0; i < beanList.size(); i++) {
            String time = null;
            String time1 = null;
            String type = null;
            //当 i >= (beanList.size() - 1)时，我们自己给最后添加一个默认的起床时间数据----此事件之后就是起床
            if (i >= (beanList.size() - 1)) {
                time = beanList.get(i).getStartTime();

                long longStart = W30BasicUtils
                        .stringToLong(beanList.get(i).getStartTime(), "HH:mm") + (10 * 60 * 1000);
                time1 = W30BasicUtils.longToString(longStart, "HH:mm");
//                time1 = beanList.get(i).getStartTime();
//                type = beanList.get(i).getSleep_type();
                type = "88";
            } else {
                time = beanList.get(i).getStartTime();
                time1 = beanList.get(i + 1).getStartTime();
                type = beanList.get(i).getSleep_type() + "";
            }

            String[] split = time.split("[:]");
            String[] split1 = time1.split("[:]");

            int HH = Integer.valueOf(split[0]);
            int HH1 = Integer.valueOf(split1[0]);
            if (HH > HH1) {
                HH1 += 24;
            }
            Integer integer = HH1 - HH;
            int MM = Integer.valueOf(split1[1]) - Integer.valueOf(split[1]);
            //拿到所有时间
            int all = (integer * 60 + MM);
            //根据所有时间计算  startX可以走 的总点数
            float paintWidth = (all * it);


            Log.e(TAG,"----------paintWidth="+paintWidth);



//            int paintWidth = (int) ((double) all / (double) AD * getWidth());
            Log.e("------------", v + "========" + type);
//            if (v > 900) { //起床状态
//                //Log.d("=====AAA==清醒", time + "-------" + time1);
//                mDataTypeListenter.OnDataTypeListenter("88", time1, "--");
//            }

            //根据 每个类型的区域判断 startX每次走动的区域的值
//            if (v >= pos && v < (pos + paintWidth)) {
//                if (v > 0) {
//                    if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
//                        //Log.d("=====AAA==清醒", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("2")) {  //潜睡状态
//                        //Log.d("=====AAA==浅睡", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("3")) {  //深睡
//                        //Log.d("=====AAA==深睡", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
//                    } else if (type.equals("88")) {  //起床
//                        //Log.d("=====AAA==自定义的起床状态", time + "-------" + time1);
//                        mDataTypeListenter.OnDataTypeListenter(type, time, "--");
//                    }
//
//                }
//            }

//            int integerWi = (int) paintWidth;
//
//            if(type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")){
//                paint.setColor(Color.parseColor("#fcd647"));
//
//                @SuppressLint("DrawAllocation")
//                Rect rectF1 = new Rect(i*integerWi, (int)mHeight, (i+1)*integerWi, 0);
//                canvas.drawRect(rectF1, paint);
//            }else if (type.equals("2")) {  //潜睡状态
//                paint.setColor(Color.parseColor("#8EA5F8"));
//                @SuppressLint("DrawAllocation")
//                Rect rectF2 = new Rect(i*integerWi, (int)300, (i+1)*integerWi, 0);
//                canvas.drawRect(rectF2, paint);
//
//            }else if (type.equals("3")) {  //深睡
//                paint.setColor(Color.parseColor("#B292D5"));
//                @SuppressLint("DrawAllocation")
//                Rect rectF3 = new Rect(i*integerWi, (int)150, (i+1)*integerWi, 0);
//                canvas.drawRect(rectF3, paint);
//                pos = (int) (pos + paintWidth);
//                Log.e(TAG,"-----------------3-----------pos="+pos);
//                //Log.d("=====BBB==深睡", time + "-------" + time1);
//            }else if (type.equals("88")) {  //起床
//                paint.setColor(Color.parseColor("#fcd647"));
//                @SuppressLint("DrawAllocation")
//                Rect rectF4 = new Rect(i*integerWi, (int)200, (i+1)*integerWi, 0);
//                canvas.drawRect(rectF4, paint);
//                pos = (int) (pos + paintWidth);
//                Log.e(TAG,"-----------------4-----------pos="+pos);
//                //Log.d("=====BBB==起床", time + "-------" + time1);
//            }













            //绘制类型区域----一个类型一片区域 88 为自定义类型
            if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                paint.setColor(Color.parseColor("#fcd647"));
                @SuppressLint("DrawAllocation")
                 Rect rectF1 = new Rect(pos, 0, (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF1, paint);
                pos = (int) (pos + paintWidth);
                Log.e(TAG,"-----------------1-----------pos="+pos);
                //Log.d("=====BBB==清醒", time + "-------" + time1);
            } else if (type.equals("2")) {  //潜睡状态
                paint.setColor(Color.parseColor("#8EA5F8"));
                @SuppressLint("DrawAllocation")
                Rect rectF2 = new Rect(pos, (getHeight() / 3), (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF2, paint);
                pos = (int) (pos + paintWidth);
                Log.e(TAG,"-----------------2-----------pos="+pos);
                //Log.d("=====BBB==浅睡", time + "-------" + time1);
            } else if (type.equals("3")) {  //深睡
                paint.setColor(Color.parseColor("#B292D5"));
                @SuppressLint("DrawAllocation")
                Rect rectF3 = new Rect(pos, (getHeight() / 2), (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF3, paint);
                pos = (int) (pos + paintWidth);
                Log.e(TAG,"-----------------3-----------pos="+pos);
                //Log.d("=====BBB==深睡", time + "-------" + time1);
            } else if (type.equals("88")) {  //起床
                paint.setColor(Color.parseColor("#fcd647"));
                @SuppressLint("DrawAllocation")
                Rect rectF4 = new Rect(pos, 0, (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF4, paint);
                pos = (int) (pos + paintWidth);
                Log.e(TAG,"-----------------4-----------pos="+pos);
                //Log.d("=====BBB==起床", time + "-------" + time1);
            }

        }

        //每次更新这条线 ------ 代表的是 startX每次走动到那一块
        paint.setColor(Color.WHITE);
        Rect rectM = new Rect((int) v - 2, 0, (int) v + 2, getHeight());
        canvas.drawRect(rectM, paint);
    }


    public void setSeekBar(float p) {
        if (p >= 0) {
            startX = p;
            invalidate();
        }
    }


    private DataTypeListenter mDataTypeListenter;

    public void setmDataTypeListenter(DataTypeListenter mDataTypeListenter) {
        this.mDataTypeListenter = mDataTypeListenter;
    }

    public DataTypeListenter getmDataTypeListenter() {
        return mDataTypeListenter;
    }

    public interface DataTypeListenter {
        void OnDataTypeListenter(String statue, String startTime, String endTime);
    }

}
