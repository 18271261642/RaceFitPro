package com.example.bozhilun.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.bozhilun.android.R;

/**
 * Created by Admin
 * Date 2020/1/11
 */
public class PrivacyDialogView extends Dialog implements View.OnClickListener {

    private Button cancleBtn,sureBtn;

    private OnPirvacyClickListener onPirvacyClickListener;


    private TextView privacyContentTv;

    private Context context;


    public void setOnPirvacyClickListener(OnPirvacyClickListener onPirvacyClickListener) {
        this.onPirvacyClickListener = onPirvacyClickListener;
    }

    public PrivacyDialogView(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_dialog_view);

        initViews();

        String str = "尊敬的用户，为了保护您的权利，请您在使用本软件之前先阅读 用户协议 和 隐私政策 以了解您的合法权益。";


        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan fc2 = new ForegroundColorSpan(Color.BLUE);
        spannableString.setSpan(foregroundColorSpan,28,33, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(fc2,35,40, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        privacyContentTv.setText(spannableString);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context,UserProtocalActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };


        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context,PrivacyActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan,28,33, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2,35,40, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        privacyContentTv.setText(spannableString);
        privacyContentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initViews() {
        cancleBtn = findViewById(R.id.privacyCancleBtn);
        sureBtn = findViewById(R.id.privacySureBtn);
        privacyContentTv = findViewById(R.id.privacyContentTv);
        cancleBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.privacyCancleBtn: //取消
                if(onPirvacyClickListener != null){
                    onPirvacyClickListener.disAgreeView();
                    cancel();
                }
                break;
            case R.id.privacySureBtn:
                if(onPirvacyClickListener != null){
                    onPirvacyClickListener.disCancleView();
                    cancel();
                }
                break;
        }
    }

    public interface OnPirvacyClickListener{
        void disCancleView();

        void disAgreeView();
    }
}
