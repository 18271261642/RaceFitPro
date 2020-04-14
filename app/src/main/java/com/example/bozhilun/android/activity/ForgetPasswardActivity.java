package com.example.bozhilun.android.activity;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.PhoneAdapter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.AreCodeBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.NetUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.view.PhoneAreaCodeView;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;


/**
 * Created by admin on 2017/4/21.
 * 忘记密码
 */

public class ForgetPasswardActivity extends BaseActivity implements RequestView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_phone_head)
    TextView tv_phone_head;
    @BindView(R.id.username_forget)
    EditText username;//用户名
    @BindView(R.id.password_forget)
    EditText password;//密码
    @BindView(R.id.send_btn_forget)
    Button sendBtn;//发送按钮
    @BindView(R.id.code_et_forget)
    EditText yuanzhengma;//验证码
    @BindView(R.id.username_input_forget)
    TextInputLayout textInputLayoutname;
    @BindView(R.id.forget_pwd_user_text)
    TextView forget_pwd_user_text;
    @BindView(R.id.forget_pwd_email_text)
    TextView forget_pwd_email_text;
    @BindView(R.id.forget_pwd_user_line)
    View forget_pwd_user_line;
    @BindView(R.id.forget_pwd_email_line)
    View forget_pwd_email_line;

    @BindView(R.id.forgetPhoneAreLin)
    LinearLayout forgetPhoneAreLin;


    private RequestPressent requestPressent;
    MyCountDownTimerUtils countDownTimerUtils;

    //判断是手机用户或者邮箱用户
    private boolean isPhone = true;

    //国家区号
    PhoneAreaCodeView phoneAreaCodeView;


    /**
     * true_邮箱找回 false_手机找回
     */
    private boolean isEmail;
    /**
     * 蓝和黑
     */
    private int colorBlue, colorBlack;
    /**
     * 用户名左边图片(邮箱用)
     */
    private Drawable leftDrawable;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);
            finish();

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void initViews() {
        tvTitle.setText(R.string.forget_password);
        initData();
    }

    private void initData() {
        colorBlue = ContextCompat.getColor(this, R.color.new_colorAccent);
        colorBlack = ContextCompat.getColor(this, R.color.black_9);
        leftDrawable = getResources().getDrawable(R.mipmap.yonghuming_dianji);

    }

    @OnClick({R.id.login_btn__forget, R.id.send_btn_forget,
            R.id.forget_pwd_user, R.id.forget_pwd_email
    ,R.id.forgetPhoneAreLin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_btn_forget:      //发送验证码
                String inputData = username.getText().toString().trim();
                if(WatchUtils.isEmpty(inputData)){
                    ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.input_email));
                    return;
                }
                if(!NetUtils.isNetworkAvailable(this)){
                    ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.wangluo));
                    return;
                }
                if(countDownTimerUtils == null)
                    countDownTimerUtils = new MyCountDownTimerUtils(60 * 1000,1000);
                if(isPhone){    //是手机用户
                    countDownTimerUtils.start();
                    sendPhoneCode(inputData,tv_phone_head.getText().toString().trim());
                }else{      //是邮箱用户
                    String emailUrl = Commont.FRIEND_BASE_URL + URLs.sendEmail;
                    if(!isEmail(inputData)){
                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.tianxie));
                        return;
                    }

                    countDownTimerUtils.onFinish();
                    countDownTimerUtils.start();
                    sendEmailVerCode(inputData,emailUrl);

                }

                break;
            case R.id.login_btn__forget:        //提交
                String uName = username.getText().toString().trim();    //手机号或者邮箱
                String uPwd = password.getText().toString().trim();     //密码
                String uVCode = yuanzhengma.getText().toString().trim();    //验证码
                if(isPhone){    //手机号
                    if (WatchUtils.isEmpty(uName)) {    //账号为空
                        ToastUtil.showToast(ForgetPasswardActivity.this, getResources().getString(R.string.input_email));
                        return;
                    }
                    if(WatchUtils.isEmpty(uPwd)){   //密码为空
                        ToastUtil.showToast(ForgetPasswardActivity.this,
                                getResources().getString(R.string.input_password));
                        return;
                    }
                    if(uPwd.length() < 6){  //密码长度小于6位
                        ToastUtil.showShort(ForgetPasswardActivity.this,
                                getResources().getString(R.string.not_b_less));
                        return;
                    }
                    if(WatchUtils.isEmpty(uVCode)){ //验证码为空
                        ToastUtil.showShort(ForgetPasswardActivity.this,
                                getResources().getString(R.string.yonghuzdffhej));
                        return;
                    }
                    //提交
                    if(requestPressent != null){
                        String subUrl = Commont.FRIEND_BASE_URL + URLs.SUB_GET_BACK_PWD_URL;
                        Map<String,String> maps = new HashMap<>();
                        maps.put("phone",uName);
                        maps.put("code",uVCode);
                        maps.put("pwd",Md5Util.Md532(uPwd));
                        requestPressent.getRequestJSONObject(2,subUrl,
                                ForgetPasswardActivity.this,new Gson().toJson(maps),2);
                    }

                }else{  //邮箱
                    if(WatchUtils.isEmpty(uName)){ //邮箱为空
                        ToastUtil.showToast(ForgetPasswardActivity.this,getResources().getString(R.string.input_email));
                        return;
                    }
//                    if (!VerifyUtil.checkEmail(uName)) {   //邮箱格式错误
//                        ToastUtil.showShort(this, getResources().getString(R.string.mailbox_format_error));
//                        return;
//                    }
                    if (WatchUtils.isEmpty(uPwd)){      //密码为空
                        ToastUtil.showShort(this, getResources().getString(R.string.input_password));
                        return;
                    }
                    if (uPwd.length() < 6){     //密码长度小于6位
                        ToastUtil.showShort(this, getResources().getString(R.string.not_b_less));
                        return;
                    }
                    if (WatchUtils.isEmpty(uVCode)) {   //验证码位空
                        ToastUtil.showShort(this, getResources().getString(R.string.input_code));
                        return;
                    }
                    //提交信息
                    if(requestPressent != null){
                        String subUrl = Commont.FRIEND_BASE_URL + URLs.xiugaimima;
                        Map<String,String> maps = new HashMap<>();
                        maps.put("phone",uName);
                        maps.put("code",uVCode);
                        maps.put("pwd",Md5Util.Md532(uPwd));
                        requestPressent.getRequestJSONObject(0x02,subUrl,
                                ForgetPasswardActivity.this,new Gson().toJson(maps),2);
                    }
                }
                break;
            case R.id.forgetPhoneAreLin:    //手机号注册时选择区号
                choosePhoneAre();
                break;
            case R.id.forget_pwd_user:  //手机
                isPhone = true;
                changeModel(false);
                break;
            case R.id.forget_pwd_email: //邮箱
                isPhone = false;
                changeModel(true);
                break;

        }
    }

    private void choosePhoneAre() {
        phoneAreaCodeView = new PhoneAreaCodeView(ForgetPasswardActivity.this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                phoneAreaCodeView.dismiss();
                tv_phone_head.setText("+" + areCodeBean.getPhoneCode());
            }
        });
    }


    //发送邮箱验证码
    private void sendEmailVerCode(String inputData, String emailUrl) {
        if(requestPressent != null){
            Map<String,String> mps = new HashMap<>();
            mps.put("phone",inputData);
            requestPressent.getRequestJSONObject(0x03,emailUrl,
                    ForgetPasswardActivity.this,new Gson().toJson(mps),3);
        }
    }

    //发送手机号码
    private void sendPhoneCode(String phoneNum, String pCode) {
        if(requestPressent != null){
            String url = Commont.FRIEND_BASE_URL + URLs.GET_BACK_PWD_PHOE_CODE_URL;
            Map<String,String> mps = new HashMap<>();
            mps.put("phone",WatchUtils.removeStr(phoneNum));
            mps.put("code",StringUtils.substringAfter(pCode,"+"));
            requestPressent.getRequestJSONObject(0x01,url,ForgetPasswardActivity.this,new Gson().toJson(mps),1);
        }

    }

    /**
     * 切换找回方式
     * @param email true_邮箱找回 false_手机找回
     */
    private void changeModel(boolean email) {
        //if (email == isEmail) return;
        forget_pwd_user_text.setTextColor(email ? colorBlack : colorBlue);
        forget_pwd_email_text.setTextColor(email ? colorBlue : colorBlack);
        forget_pwd_user_line.setVisibility(email ? View.GONE : View.VISIBLE);
        forget_pwd_email_line.setVisibility(email ? View.VISIBLE : View.GONE);
        forgetPhoneAreLin.setVisibility(email ? View.GONE : View.VISIBLE);
        username.setCompoundDrawablesWithIntrinsicBounds(email ? leftDrawable : null, null, null, null);
        if (email) {
            textInputLayoutname.setHint(getResources().getString(R.string.input_email));
        } else {
            textInputLayoutname.setHint(getResources().getString(R.string.input_name));
        }
        isEmail = email;
    }


    /*
 * 是否email
 */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mymobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mymobiles)) return false;
        else return mymobiles.matches(telRegex);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_forgetpassward;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  handler.removeCallbacksAndMessages(null);
        if(requestPressent != null)
            requestPressent.detach();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog("loading...");
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if(what == 1){  //发送手机验证码
            try {
                JSONObject jsonObject = new JSONObject((String)object);
                ToastUtil.showToast(ForgetPasswardActivity.this,jsonObject.getString("data"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(what == 2){     //找回密码提交成功返回
            try {
                JSONObject jsoO = new JSONObject((String)object);
                int resultCode = jsoO.getInt("code");
                if(resultCode == 200){   //提交成功
                    ToastUtil.showToast(ForgetPasswardActivity.this, getResources().getString(R.string.change_password));
                    finish();
                }else{
                    ToastUtil.showToast(ForgetPasswardActivity.this,jsoO.getString("msg"));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        else if(what == 3){     //发送邮箱验证码
            try {
                JSONObject js = new JSONObject((String)object);
                if(js.has("code")){
                    if(js.getInt("code") == 200){
                        ToastUtil.showToast(ForgetPasswardActivity.this,getResources().getString(R.string.yanzhengma));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    private class MyCountDownTimerUtils extends CountDownTimer {


        public MyCountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendBtn.setClickable(false);
            sendBtn.setText(millisUntilFinished/1000+"s");
        }

        @Override
        public void onFinish() {
            sendBtn.setClickable(true);
            sendBtn.setText(getResources().getString(R.string.send_code));
        }


    }

    private void clearCountTime(){
        if(countDownTimerUtils != null){
            countDownTimerUtils.cancel();
        }
    }



}
