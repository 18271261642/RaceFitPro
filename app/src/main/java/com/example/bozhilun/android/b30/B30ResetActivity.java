package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.enums.EPwdStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置设备密码
 */
public class B30ResetActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30OldPwdEdit)
    EditText b30OldPwdEdit;
    @BindView(R.id.b30NewPwdEdit)
    EditText b30NewPwdEdit;
    @BindView(R.id.b30AgainNewPwdEdit)
    EditText b30AgainNewPwdEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_reset);
        ButterKnife.bind(this);

        initViews();


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_reset_device_password));

    }

    @OnClick({R.id.commentB30BackImg, R.id.b30ResetPwdBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b30ResetPwdBtn:
                resetBlePwd();
                break;
        }
    }

    //重置设备密码
    private void resetBlePwd() {
        String oldPwd = b30OldPwdEdit.getText().toString().trim();
        final String newPwd = b30NewPwdEdit.getText().toString().trim();
        String againNewPwd = b30AgainNewPwdEdit.getText().toString().trim();
        //密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.DEVICESCODE, "0000");
        String newb30pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "newb30pwd", "0000");

        if (WatchUtils.isEmpty(oldPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_old_password));
            return;
        }
        if (WatchUtils.isEmpty(newPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_new_device_password));
            return;
        }
        if (WatchUtils.isEmpty(againNewPwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_writ_new_device_password));
            return;
        }

        if(!WatchUtils.isNumeric(oldPwd) || !WatchUtils.isNumeric(newPwd) || !WatchUtils.isNumeric(againNewPwd)){
            ToastUtil.showShort(B30ResetActivity.this,"请输入数字密码!");
            return;
        }


        if (!newPwd.equals(againNewPwd)){
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_two_passwords_are_different));
            return;
        }

        if (!oldPwd.equals(b30Pwd) && !oldPwd.equals(newb30pwd)) {
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_old_password_incorrect));
            return;
        }

        //长度只能等于4位
        if(againNewPwd.length() !=4){
            ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.input_new_password));
            return;
        }

        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().modifyDevicePwd(iBleWriteResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    Log.e("密码", "-----pwdData=" + pwdData.toString());
                    if (pwdData.getmStatus() == EPwdStatus.SETTING_SUCCESS) {
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "newb30pwd", newPwd);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.DEVICESCODE, newPwd);
                        ToastUtil.showShort(B30ResetActivity.this, getResources().getString(R.string.string_reset_password_successfully));
                        finish();
                    }
                }
            }, againNewPwd);
        }

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
