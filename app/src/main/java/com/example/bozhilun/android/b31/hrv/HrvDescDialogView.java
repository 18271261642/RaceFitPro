package com.example.bozhilun.android.b31.hrv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;

/**
 * Created by Admin
 * Date 2018/12/26
 */
public class HrvDescDialogView extends AlertDialog implements View.OnClickListener {

    private TextView titleTv,descTv;
    private ImageView cancleImg,descImg;

    private HrvDescDialogListener hrvDescDialogListener;

    public HrvDescDialogView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hrv_desc_dialog_layout);


        initViews();

    }


    //设置标题
    public void setHrvDescTitleTxt(String txt){
        titleTv.setText(txt);
    }


    //设置图片
    public void setHrvDescImg(Drawable imgResource){
        descImg.setBackground(imgResource);
    }


    //设置显示的内容
    public void setHrvDescContent(String contentTxt){
        descTv.setText(contentTxt);
    }



    private void initViews() {
        titleTv = findViewById(R.id.hrvDescTitleTv);
        descTv = findViewById(R.id.hrvDescShowTv);
        cancleImg = findViewById(R.id.hrvDescCancleImg);
        descImg = findViewById(R.id.hrvDescShowImg);
        cancleImg.setOnClickListener(this);
//        cusRateTextView = findViewById(R.id.hrdDescCusTv);
//        //cusRateTextView.setmDegrees(90);
//        cusRateTextView.setText("RRN+1(ms)");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hrvDescCancleImg:
                if(hrvDescDialogListener != null){
                    hrvDescDialogListener.cancleDialog();
                }
                cancel();
                break;
        }
    }

    public interface HrvDescDialogListener{
        void cancleDialog();
    }

    public void setHrvDescDialogListener(HrvDescDialogListener hrvDescDialogListener) {
        this.hrvDescDialogListener = hrvDescDialogListener;
    }
}
