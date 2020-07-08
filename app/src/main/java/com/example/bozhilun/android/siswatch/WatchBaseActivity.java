package com.example.bozhilun.android.siswatch;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchBaseActivity extends AppCompatActivity {

    private MyApp myApp;
    private WatchBaseActivity watchBaseActivity;
    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideTitleStute();
        super.onCreate(savedInstanceState);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (myApp == null) {
            myApp = (MyApp) getApplication();
        }
        watchBaseActivity = this;
        addActivity();

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void hideTitleStute() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        MobclickAgent.onPause(this);
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    // 添加Activity方法
    public void addActivity() {
        myApp.addActivity(watchBaseActivity);// 调用myApplication的添加Activity方法
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
     * 通用的Intent跳转
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 跳转公共方法2 带参数
     *
     * @param cls
     */
    public void startActivity(Class<?> cls, String[] keys, String[] values) {
        Intent intent = new Intent(this, cls);
        int size = keys.length;
        for (int i = 0; i < size; i++) {
            intent.putExtra(keys[i], values[i]);
        }
        startActivity(intent);
    }

    /**
     * 进度条显示
     *
     * @param msg
     */
    private static int MSG_DISMISS_DIALOG = 101;

    public void showLoadingDialog(String msg) {
        if (!isFinishing()){
            if (dialog == null) {
                dialog = new Dialog(WatchBaseActivity.this, R.style.CustomProgressDialog);
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
            mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 10 * 1000);
        }

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            try {
                if (MSG_DISMISS_DIALOG == msg.what) {
                    if (null != dialog) {
                        if (dialog.isShowing()) {
                            Log.i("----", "handler get mesage");
                            dialog.dismiss();
                            if (mHandler != null) {
                                mHandler.removeMessages(MSG_DISMISS_DIALOG);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    //关闭进度条
    public void closeLoadingDialog() {
        if (dialog != null) {
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
