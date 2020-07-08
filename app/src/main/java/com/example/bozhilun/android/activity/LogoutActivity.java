package com.example.bozhilun.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2020/1/14
 */
public class LogoutActivity extends WatchBaseActivity implements RequestView {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.logoutAccountEdit)
    EditText logoutAccountEdit;
    @BindView(R.id.accountVerCodeEdit)
    EditText accountVerCodeEdit;

    private RequestPressent requestPressent;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_layout);
        ButterKnife.bind(this);

        initViews();

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("注销账号");
    }

    @OnClick({R.id.commentB30BackImg, R.id.logoutAccountBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.logoutAccountBtn:     //注销账号
                String accountStr = logoutAccountEdit.getText().toString();
                String accountPwdStr = accountVerCodeEdit.getText().toString();
                if(WatchUtils.isVPBleDevice(accountStr) || WatchUtils.isEmpty(accountPwdStr))
                    return;
                logoutAccount(accountStr,accountPwdStr);
                break;
        }
    }

    //注销账号
    private void logoutAccount(final String accountStr, final String accountPwdStr) {
        alert = new AlertDialog.Builder(LogoutActivity.this);
        alert.setTitle(getResources().getString(R.string.complete))
                .setMessage("是否确认注销账号?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        submitLogout(accountStr,accountPwdStr);
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void submitLogout(String accountStr, String accountPwdStr) {
        if(requestPressent != null){
            String url = Commont.FRIEND_BASE_URL + Commont.CANCEL_ACCOUNT;
            Map<String,Object> map = new HashMap<>();
            map.put("phone",accountStr);
            map.put("pwd", Md5Util.Md532(accountPwdStr));
            requestPressent.getRequestJSONObject(0x01,url,LogoutActivity.this,new Gson().toJson(map),0x01);

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(jsonObject.getInt("code") == 200){
                ToastUtil.showToast(LogoutActivity.this,"注销成功!");
                startActivity(FastLoginActivity.class);
                finish();
            }else {
                ToastUtil.showToast(LogoutActivity.this,jsonObject.getString("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }
}
