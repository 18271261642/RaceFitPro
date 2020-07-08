package com.example.bozhilun.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.util.ToastUtil;


/**
 * dialog的输入框
 * Created by Admin
 * Date 2018/11/20
 */
public class CusInputDialogView extends Dialog implements View.OnClickListener {

    private Button cancleBtn,sureBtn;
    private TextInputEditText inputEditText;

    public CusInputDialogListener cusInputDialogListener;

    public void setCusInputDialogListener(CusInputDialogListener cusInputDialogListener) {
        this.cusInputDialogListener = cusInputDialogListener;
    }

    public CusInputDialogView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cus_input_dialog_view);


        initViews();

    }

    private void initViews() {
        cancleBtn = findViewById(R.id.inputDialogCancleBtn);
        sureBtn = findViewById(R.id.inputDialogSureBtn);
        inputEditText = findViewById(R.id.inputDialogEdit);
        cancleBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inputDialogCancleBtn:
                cancel();
                if(cusInputDialogListener != null)
                    cusInputDialogListener.cusDialogCancle();
                break;
            case R.id.inputDialogSureBtn:
                String inputMsg = inputEditText.getText().toString().trim();
                if(TextUtils.isEmpty(inputMsg))
                    return;
                if(inputMsg.length() != 4){
                    ToastUtil.showToast(MyApp.getContext(),"请输入4位密码!");
                    return;
                }
                if(cusInputDialogListener != null)
                    cusInputDialogListener.cusDialogSureData(inputMsg);
                break;
        }
    }

    public interface CusInputDialogListener{
        void cusDialogCancle();
        void cusDialogSureData(String data);
    }
}
