package com.example.bozhilun.android.w30s.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.bozhilun.android.R;

/**
 * @aboutContent: PopupWindow
 * @author： An
 * @crateTime: 2018/3/29 11:23
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class CustomPopuWindow extends PopupWindow {
    private View window;
    private ImageView imageRun, imageRiding;


    public ImageView getImageRiding() {
        return imageRiding;
    }

    public ImageView getImageRun() {
        return imageRun;
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
    public CustomPopuWindow(Activity context, View.OnClickListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.custom_dilog_testing, null);
        imageRun = (ImageView) window.findViewById(R.id.image_run);
        imageRiding = (ImageView) window.findViewById(R.id.image_riding);
        imageRun.setOnClickListener(listener);
        imageRiding.setOnClickListener(listener);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
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
//        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x70525252);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        window.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = window.findViewById(R.id.run_pop_layout).getTop();
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
