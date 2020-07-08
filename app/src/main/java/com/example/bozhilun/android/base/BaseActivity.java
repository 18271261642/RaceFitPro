package com.example.bozhilun.android.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.util.ViewUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thinkpad on 2016/10/20.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected abstract void initViews();

    protected abstract int getContentViewId();

    private MyApp myApp;
    private BaseActivity baseActivity;

    private Dialog dialog; //进度条




    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1001){
                handler.removeMessages(1001);
                if(!isFinishing() && dialog != null){
                    dialog.dismiss();
                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        hideTitleStute();
        setContentView(getContentViewId());
//        setStatusBarColor();
        ButterKnife.bind(this);
        setupToolbar();
        initViews();
        if (myApp == null) {
            myApp = (MyApp) getApplication();
        }
        baseActivity = this;
        addActivity();
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(" ");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getToolbarClick();
                }
            });
        }
    }

    public void hideTitleStute() {
    }

    public void setStatusBarColor() {
        if (getStatusBarColor() != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int tintColor = getStatusBarColor();
                ViewUtils.setTranslucentStatus(this, true);
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                // enable status bar tint
                tintManager.setStatusBarTintEnabled(true);
                // enable navigation bar tint
                tintManager.setNavigationBarTintEnabled(true);
                if (tintColor != 0) {
                    tintManager.setTintColor(ContextCompat.getColor(this, tintColor));
                } else {
                    tintManager.setTintColor(ContextCompat.getColor(this, R.color.new_colorAccent));
                }
            }
        }
    }

    protected void getToolbarClick() {
        finish();
    }

    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 添加Activity方法
    public void addActivity() {
        myApp.addActivity(baseActivity);// 调用myApplication的添加Activity方法
    }

    /**
     * 销毁所有activity
     *
     * @param
     */
    public void removeAllActivity() {
        myApp.removeALLActivity();  //调用Application的方法销毁所有Activity
    }

    /**
     * 进度条显示
     *
     * @param msg
     */
    public void showLoadingDialog(String msg) {
        if (isFinishing())
            return;
        if (dialog == null) {
            dialog = new Dialog(BaseActivity.this, R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.setCancelable(true);
            dialog.show();
        } else {
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            dialog.setCancelable(true);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.show();
        }

        handler.sendEmptyMessageDelayed(1001, 10 * 1000);

    }

    //关闭进度条
    public void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 输入框获取焦点时自动弹出软键盘，
     * 点击屏幕的其它任何位置，软件盘消失
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
