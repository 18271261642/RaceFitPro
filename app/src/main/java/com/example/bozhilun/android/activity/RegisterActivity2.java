package com.example.bozhilun.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AreCodeBean;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.NetUtils;
import com.example.bozhilun.android.view.PhoneAreaCodeView;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.view.PrivacyActivity;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/4.
 * 注册页面
 */

public class RegisterActivity2 extends WatchBaseActivity implements RequestView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone_head)
    TextView tv_phone_head;
    @BindView(R.id.lv_register)
    ListView lv_register;
    @BindView(R.id.register_agreement_my)
    TextView registerAgreement;
    @BindView(R.id.username_input)
    TextInputLayout usernameInput;
    @BindView(R.id.textinput_password_regster)
    TextInputLayout textinputPassword;
    @BindView(R.id.code_et_regieg)
    EditText codeEt;
    @BindView(R.id.username_regsiter)
    EditText username;
    @BindView(R.id.password_logonregigter)
    EditText password;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.textinput_code)
    TextInputLayout textinput_code;

    //倒计时
    MyCountDownTimerUtils countTimeUtils;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //手机号区号
    private PhoneAreaCodeView phoneAreaCodeView;

    private RequestPressent requestPressent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter2);
        ButterKnife.bind(this);


        initViews();

        initData();

    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }


    protected void initViews() {
        tvTitle.setText(R.string.user_regsiter);
        usernameInput.setHint(getResources().getString(R.string.input_name));
        tv_phone_head.setText("+86");
        codeEt.setHintTextColor(getResources().getColor(R.color.white));
        //倒计时
        countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

        //初始化底部声明
        String INSURANCE_STATEMENT = getResources().getString(R.string.register_agreement);
        SpannableString spanStatement = new SpannableString(INSURANCE_STATEMENT);
        ClickableSpan clickStatement = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //跳转到协议页面
                startActivity(new Intent(RegisterActivity2.this, PrivacyActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        spanStatement.setSpan(clickStatement, 0, INSURANCE_STATEMENT.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStatement.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0,
                INSURANCE_STATEMENT.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        registerAgreement.setText(R.string.agree_agreement);
        registerAgreement.append(spanStatement);
        registerAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @OnClick({R.id.login_btn_reger, R.id.send_btn, R.id.login_btn_emil_reger, R.id.tv_phone_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_emil_reger://跳转到邮箱注册
                startActivity(new Intent(RegisterActivity2.this, RegisterActivity.class));
                break;
            case R.id.tv_phone_head:    //选择区号
                choosePhoneArea();
                break;
            case R.id.send_btn:
                //手机号
                final String phoneNum = username.getText().toString().trim();
                //国标码
                final String pCode = tv_phone_head.getText().toString().trim();
                if (WatchUtils.isEmpty(phoneNum)) {
                    Snackbar.make(view,getResources().getString(R.string.format_is_wrong),Snackbar.LENGTH_SHORT).show();
                    //ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.format_is_wrong));
                    return;
                }
                if (!NetUtils.isNetworkAvailable(this)) {
                    Snackbar.make(view,getResources().getString(R.string.wangluo),Snackbar.LENGTH_SHORT).show();
                    //ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.wangluo));
                    return;
                }
                //snedPhoneNumToServer(phoneNum.trim(),pCode);
                if (countTimeUtils == null)
                    countTimeUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

                countTimeUtils.start();
                snedPhoneNumToServer(phoneNum, pCode);

                break;
            case R.id.login_btn_reger:  //注册
                String phoneStr = username.getText().toString().trim();
                String verCode = codeEt.getText().toString().trim();
                String pwdTxt = password.getText().toString().trim();
                if (WatchUtils.isEmpty(verCode)) {
                    Snackbar.make(view,getResources().getString(R.string.string_code_null),Snackbar.LENGTH_SHORT).show();
                    //ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.string_code_null));
                    return;
                }
                if (WatchUtils.isEmpty(pwdTxt)) {
                    Snackbar.make(view,getResources().getString(R.string.input_password),Snackbar.LENGTH_SHORT).show();
                    //ToastUtil.showShort(RegisterActivity2.this, getResources().getString(R.string.input_password));
                    return;
                }

                registerRemote(phoneStr, verCode, pwdTxt);
        }
    }

    //选择区号
    private void choosePhoneArea() {
        phoneAreaCodeView = new PhoneAreaCodeView(RegisterActivity2.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                phoneAreaCodeView.dismiss();
                tv_phone_head.setText("+" + areCodeBean.getPhoneCode());
            }
        });
    }


    /**
     * 获取手机号验证码
     *
     * @param number 手机号
     * @param pCode  国标码
     */
    private void snedPhoneNumToServer(String number, String pCode) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", WatchUtils.removeStr(number));
        map.put("code", StringUtils.substringAfter(pCode, "+"));
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.GET_PHONE_VERCODE_URL, RegisterActivity2.this, mapjson, 1);
        }
    }


    //提交注册信息
    private void registerRemote(String phoneStr, String verCode, String pwd) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phoneStr);
        map.put("pwd", Md5Util.Md532(pwd));
        map.put("code", verCode);
        map.put("status", "0");
        map.put("type", "0");
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, Commont.FRIEND_BASE_URL + URLs.myHTTPs, RegisterActivity2.this, mapjson, 2);
        }

    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
       // Log.e("", "------obj=" + object.toString());
        if (object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            switch (what) {
                case 0x01:  //获取验证码返回
                    String msg = jsonObject.getString("msg");
                    String dataStr = jsonObject.getString("data");
                    ToastUtil.showToast(RegisterActivity2.this,WatchUtils.isEmpty(dataStr)? msg : dataStr);
                    break;
                case 0x02:  //注册返回
                    analysisRegiData(jsonObject);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {

    }

    //提交注册返回
    private void analysisRegiData(JSONObject jsonObject) {
        try {
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                String data = jsonObject.getString("data");
                UserInfoBean userInfoBean = new Gson().fromJson(data,UserInfoBean.class);
                if(userInfoBean != null){
                    Common.customer_id = userInfoBean.getUserid();
                    MobclickAgent.onProfileSignIn(Common.customer_id);
                    SharedPreferencesUtils.saveObject(RegisterActivity2.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                    startActivity(new Intent(RegisterActivity2.this, PersonDataActivity.class));
                    finish();
                }
            } else {
                ToastUtil.showToast(RegisterActivity2.this, jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyCountDownTimerUtils extends CountDownTimer {


        MyCountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendBtn.setClickable(false);
            sendBtn.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            sendBtn.setClickable(true);
            sendBtn.setText(getResources().getString(R.string.send_code));
        }
    }


}
