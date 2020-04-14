package com.example.bozhilun.android.w30s.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.R;

public class CommomDialog extends Dialog implements View.OnClickListener {
    private TextView cue_head;
    private TextView hint_content;

    private Context mContext;
    private String content;
    private String title;
    private OnCloseListener listener;
    //private String positiveName;//是
    //private String negativeName;//否

    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

//    public CommomDialog(Context context, int themeResId, String content) {
//        super(context, themeResId);
//        this.mContext = context;
//        this.content = content;
//    }
//
//    public CommomDialog(Context context, int themeResId, String content, OnCloseListener listener) {
//        super(context, themeResId);
//        this.mContext = context;
//        this.content = content;
//        this.listener = listener;
//    }
//
//    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//        this.mContext = context;
//    }

    public CommomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 通过主题样式来控制标题栏
     * @param context
     * @param theme
     */
    public CommomDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        //加载布局文件
        //this.setContentView(LayoutInflater.from(context).inflate(R.layout.lib_update_app_dialog, null));
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setListener(OnCloseListener listener) {
        this.listener = listener;
    }


//    public CommomDialog setPositiveButton(String name){
//        this.positiveName = name;
//        return this;
//    }
//
//    public CommomDialog setNegativeButton(String name){
//        this.negativeName = name;
//        return this;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_update_app_dialog);
        initView();
        //点击window外的区域 是否消失
        this.setCanceledOnTouchOutside(false);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.8f);
        lp.width = (int) (displayMetrics.widthPixels * 0.8f);
        dialogWindow.setAttributes(lp);
    }

    private void initView() {
        cue_head = (TextView) findViewById(R.id.cue_head);
        hint_content = (TextView) findViewById(R.id.hint_content);
        findViewById(R.id.dialog_confirm).setOnClickListener(this);
        findViewById(R.id.dialog_cancel).setOnClickListener(this);
        if (!WatchUtils.isEmpty(title)) {
            cue_head.setText(title);
        }
        if (!WatchUtils.isEmpty(content)) {
            hint_content.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel://取消
                if (listener != null) {
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.dialog_confirm://确认
                if (listener != null) {
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}