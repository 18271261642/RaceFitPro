package com.example.bozhilun.android.b36.calview;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.women.WomenDetailActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

/**
 * Created by Admin
 * Date 2018/11/23
 */
public class CusSimpleMonthView extends MonthView {

    private static final String TAG = "CusSimpleMonthView";

    private int mRadius;
    private Context mContext;

    //当前月的画笔
    private Paint currMonthPaint = new Paint();


    //排卵日的画笔
    private Paint ovulatePaint = new Paint();


    public CusSimpleMonthView(Context context) {
        super(context);
        this.mContext = context;
        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE,mSelectedPaint);
        //4.0以上硬件加速会导致无效
        mSelectedPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2-10;
        mSchemePaint.setStyle(Paint.Style.STROKE);
        mSchemePaint.setStrokeWidth(5f);

        //绘制排卵日的画笔
        ovulatePaint.setStyle(Paint.Style.FILL);
        ovulatePaint.setAntiAlias(true);
        ovulatePaint.setColor(Color.parseColor("#9B30FF"));
        ovulatePaint.setTextAlign(Paint.Align.CENTER);
        ovulatePaint.setTextSize(mOtherMonthTextPaint.getTextSize());
        ovulatePaint.setStrokeWidth(2);

        //绘制当前月的月经期的画笔

        currMonthPaint.setStyle(Paint.Style.FILL);
        currMonthPaint.setAntiAlias(true);
        currMonthPaint.setColor(Color.parseColor("#FF93A2"));
        currMonthPaint.setTextAlign(Paint.Align.CENTER);
        currMonthPaint.setTextSize(mOtherMonthTextPaint.getTextSize());
        currMonthPaint.setStrokeWidth(2);

    }

    @Override
    protected void onLoopStart(int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        //Log.e(TAG,"-----onDrawSelected----333--");
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        float baselineY = mTextBaseLine + y;
        mSchemePaint.setTextSize(mOtherMonthTextPaint.getTextSize());
        mSchemePaint.setStrokeWidth(mOtherMonthTextPaint.getStrokeWidth());
        mSchemePaint.setFakeBoldText(true);
        mSchemePaint.setTextAlign(Paint.Align.CENTER);
        mSchemePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawText(String.valueOf(calendar.getDay()),
                cx,
                baselineY,
                calendar.isCurrentDay() ? mOtherMonthTextPaint :
                        calendar.isCurrentMonth() ? mSchemePaint : mOtherMonthTextPaint);

        if(!WatchUtils.isEmpty(calendar.getScheme())){
            //Log.e(TAG,"---------scheme="+calendar.getScheme());

            //排卵日
            if(calendar.getScheme().equals(getResources().getString(R.string.b36_ovulation_day).trim())){
                //绘制一个背景圆
                canvas.drawCircle(cx,cy,mRadius,ovulatePaint);
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSchemePaint);
            }
            //排卵期
            if(calendar.getScheme().equals(getResources().getString(R.string.b36_ovulation_period).trim())){
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSchemePaint);
            }



            //月经长度
            String menseLength = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_MEN_LENGTH, "5");
            if(WatchUtils.isEmpty(menseLength))
                menseLength = "5";
            for(int i = 1;i<=Integer.valueOf(menseLength.trim());i++){
                if(calendar.getScheme().equals(getResources().getString(R.string.b36_period)+ " " + i + " "+getResources().getString(R.string.data_report_day))){
                    canvas.drawCircle(cx,cy,mRadius,currMonthPaint);
                }
            }



        }

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;



        if(!WatchUtils.isEmpty(calendar.getScheme())){
            //绘制排卵日的背景色和字体颜色
            if(calendar.getScheme().equals(getResources().getString(R.string.b36_ovulation_day).trim())){
                //绘制一个背景圆
                //canvas.drawCircle(cx,cy,mRadius,ovulatePaint);
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,mSchemePaint);
            }

            //baby出生日
            if(calendar.getScheme().equals("baby"+getResources().getString(R.string.birthday))){
                //绘制一个背景圆
                canvas.drawCircle(cx,cy,mRadius,ovulatePaint);
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,mSchemePaint);
            }

            //绘制排卵期
            if(calendar.getScheme().equals(getResources().getString(R.string.b36_ovulation_period).trim())){
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSchemePaint);
            }


        }


        if (isSelected) {   //选择
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        }
        else if (hasScheme) {   //标记
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemePaint : mOtherMonthTextPaint);

        }

        else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
