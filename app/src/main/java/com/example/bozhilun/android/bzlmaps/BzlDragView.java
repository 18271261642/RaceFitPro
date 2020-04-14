package com.example.bozhilun.android.bzlmaps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.bozhilun.android.R;

public class BzlDragView {
    private static final int MOVE_LENGH = 200;

    private boolean isDrag = true;
    private boolean isDraging = false;
    private int screenHeight;
    private int screenWidth;
    private Button iv_drag;
    private Activity activity;
    private String text;
    private BzlDragViewListenter bzlDragViewListenter;


    public interface BzlDragViewListenter {
        //拖动监听
        void OnBzlDragViewListenter();

        //点击监听
        void OnClickBzlDragViewListenter();
    }

    public void setBzlDragViewListenter(BzlDragViewListenter bzlDragViewListenter) {
        this.bzlDragViewListenter = bzlDragViewListenter;
    }

    public void setIsDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    public void setDraging(boolean draging) {
        isDraging = draging;
    }

    public void setText(String text) {
        this.text = text;
        if (iv_drag != null) iv_drag.setText(text);
    }

    public BzlDragView(Activity activity, String text) {
        this.activity = activity;
        this.screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        this.screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        LayoutInflater factory = LayoutInflater.from(activity);
        View layout = factory.inflate(R.layout.bzl_draglayout, null);
        decorView.addView(layout);
        this.iv_drag = (Button) layout.findViewById(R.id.run_start);
        iv_drag.setText(text);
    }


    /**
     * 显示可拖动的客服电话图标
     */
    @SuppressLint("ClickableViewAccessibility")
    public void showDragCallView() {

        this.iv_drag.setVisibility(View.VISIBLE);

        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        final int width = metric.widthPixels; // 屏幕宽度（像素）
        final int height = metric.heightPixels; // 屏幕高度（像素）

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        iv_drag.measure(w, h);

        final int viewheight = iv_drag.getMeasuredHeight();
        final int viewwidth = iv_drag.getMeasuredWidth();


        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.iv_drag.getLayoutParams();
        params.leftMargin = width / 2 - viewheight / 2;
        params.rightMargin = width / 2 - viewheight / 2;
//        params.topMargin = height - height / 3 - viewwidth / 2;
        params.topMargin = height - height / 3;
        this.iv_drag.setLayoutParams(params);


        this.iv_drag.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            long downTime;
            long upTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指第一次触摸到屏幕
//                        iv_drag.setBackgroundResource(R.drawable.googleg_standard_color_18);
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指移动
                        if (!isDrag) {
                            break;
                        }
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        int dx = newX - this.startX;
                        int dy = newY - this.startY;

                        //禁止上拖拽
                        if (dy > 5) {
                            break;
                        }

                        //禁止下拖拽
                        if (dy < -5) {
                            break;
                        }

                        //此处加数值是为了用户手势不一定垂直一条线
                        //禁止左拖拽
//                        if (dx > 0) {
//                            break;
//                        }
                        //禁止左拖拽
                        if (dx < 0) {
                            break;
                        }

                        // 计算出来控件原来的位置
                        int l = iv_drag.getLeft();
                        int r = iv_drag.getRight();
                        int t = iv_drag.getTop();
                        int b = iv_drag.getBottom();

                        int newt = t + dy;
                        int newb = b + dy;
                        int newl = l + dx;
                        int newr = r + dx;


                        if ((newl < 0) || (newt < 0) || (newr > screenWidth)
                                || (newb > screenHeight)) {
                            if (!isDraging){
                                isDraging = true;
                                bzlDragViewListenter.OnBzlDragViewListenter();
                            }

                        }

                        // 更新iv在屏幕的位置.
                        iv_drag.layout(newl, newt, newr, newb);
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP: // 手指离开屏幕的一瞬间

                        int lastx = iv_drag.getLeft();
                        int lasty = iv_drag.getTop();
                        upTime = System.currentTimeMillis();
                        if (Math.abs(lastx - startX) < MOVE_LENGH
                                && Math.abs(lasty - startY) < MOVE_LENGH
                                && (upTime - downTime) < 200) {
                            bzlDragViewListenter.OnClickBzlDragViewListenter();
                        }

                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) iv_drag.getLayoutParams();
                        params1.leftMargin = width / 2 - viewheight / 2;
                        params1.rightMargin = width / 2 - viewheight / 2;
//                        params1.topMargin = height - height / 3 - viewwidth / 2;
                        params.topMargin = height - height / 3;
                        iv_drag.setLayoutParams(params1);
                        break;
                }
                return true;
            }
        });
    }

    /**
     *
     */
    public void hideDragCallView() {

        this.iv_drag.setVisibility(View.GONE);
    }
}
