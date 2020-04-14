package com.example.bozhilun.android.w30s.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.bozhilun.android.R;

public class HistoryCustomPopuWindow extends PopupWindow {
    private View window;
    private RelativeLayout OneM, TwoM, ThreeM, FourM, FiveM, SixM, SevenM, EightM, NineM, TenM, ElevenM, Twelve;

    public RelativeLayout getOneM() {
        return OneM;
    }


    public RelativeLayout getTwoM() {
        return TwoM;
    }


    public RelativeLayout getThreeM() {
        return ThreeM;
    }


    public RelativeLayout getFourM() {
        return FourM;
    }


    public RelativeLayout getFiveM() {
        return FiveM;
    }


    public RelativeLayout getSixM() {
        return SixM;
    }

    public RelativeLayout getSevenM() {
        return SevenM;
    }


    public RelativeLayout getEightM() {
        return EightM;
    }


    public RelativeLayout getNineM() {
        return NineM;
    }


    public RelativeLayout getTenM() {
        return TenM;
    }


    public RelativeLayout getElevenM() {
        return ElevenM;
    }


    public RelativeLayout getTwelve() {
        return Twelve;
    }


    /**
     * 显示PopupWindow：
     * <p>
     * showAsDropDown(View anchor)：相对某个控件的位置（正左下方），无偏移
     * <p>
     * showAsDropDown(View anchor, int xoff, int yoff)：相对某个控件的位置，有偏移
     * <p>
     * showAtLocation(View parent, int gravity, int x, int y)：相对于父控件的位置
     * （例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
     *
     * @param context
     * @param listener
     */
    public HistoryCustomPopuWindow(Activity context, View.OnClickListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.his_custom_motoh_testing, null);
        OneM = (RelativeLayout) window.findViewById(R.id.one_motoh);
        TwoM = (RelativeLayout) window.findViewById(R.id.two_motoh);
        ThreeM = (RelativeLayout) window.findViewById(R.id.three_motoh);
        FourM = (RelativeLayout) window.findViewById(R.id.four_motoh);
        FiveM = (RelativeLayout) window.findViewById(R.id.five_motoh);
        SixM = (RelativeLayout) window.findViewById(R.id.six_motoh);
        SevenM = (RelativeLayout) window.findViewById(R.id.senve_motoh);
        EightM = (RelativeLayout) window.findViewById(R.id.eight_motoh);
        NineM = (RelativeLayout) window.findViewById(R.id.niece_motoh);
        TenM = (RelativeLayout) window.findViewById(R.id.ten_motoh);
        ElevenM = (RelativeLayout) window.findViewById(R.id.ten_one_motoh);
        Twelve = (RelativeLayout) window.findViewById(R.id.ten_two_motoh);
        this.OneM.setOnClickListener(listener);
        this.TwoM.setOnClickListener(listener);
        this.ThreeM.setOnClickListener(listener);
        this.FourM.setOnClickListener(listener);
        this.FiveM.setOnClickListener(listener);
        this.SixM.setOnClickListener(listener);
        this.SevenM.setOnClickListener(listener);
        this.EightM.setOnClickListener(listener);
        this.NineM.setOnClickListener(listener);
        this.TenM.setOnClickListener(listener);
        this.ElevenM.setOnClickListener(listener);
        this.Twelve.setOnClickListener(listener);
        this.setHeight(500);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //点击外部消失
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow的View
        this.setContentView(window);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x70525252);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        window.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = window.findViewById(R.id.run_pop_data_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}