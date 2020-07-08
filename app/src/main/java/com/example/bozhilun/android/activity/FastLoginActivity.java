package com.example.bozhilun.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AreCodeBean;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.mvp.BaseMvpActivity;
import com.example.bozhilun.android.mvp.login.IFastLoginView;
import com.example.bozhilun.android.mvp.login.IFastPresent;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.LoginListenter;
import com.example.bozhilun.android.util.ShareSDKUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.view.PhoneAreaCodeView;
import com.example.bozhilun.android.view.PrivacyActivity;
import com.example.bozhilun.android.view.PrivacyDialogView;
import com.example.bozhilun.android.view.PromptDialog;
import com.example.bozhilun.android.view.ShowPermissDialogView;
import com.example.bozhilun.android.view.UserProtocalActivity;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.apache.commons.lang.StringUtils;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 快捷登录，手机号+验证
 * Created by Admin
 * Date 2020/5/9
 */
public class FastLoginActivity extends BaseMvpActivity<IFastLoginView, IFastPresent> implements IFastLoginView {

    private static final String TAG = "FastLoginActivity";

    @BindView(R.id.fastLoginPhoneAreCodeTv)
    TextView fastLoginPhoneAreCodeTv;
    @BindView(R.id.fastLoginPhoneNumberEdit)
    AppCompatEditText fastLoginPhoneNumberEdit;
    @BindView(R.id.fastLoginPhoneCodeEdit)
    AppCompatEditText fastLoginPhoneCodeEdit;
    @BindView(R.id.fastLoginGetCodeTv)
    TextView fastLoginGetCodeTv;
    //用户协议
    @BindView(R.id.fastUserProtocalTv)
    TextView fastUserProtocalTv;
    //隐私政策
    @BindView(R.id.fastPrivacyTv)
    TextView fastPrivacyTv;
    @BindView(R.id.privacyCheckBox)
    CheckBox privacyCheckBox;
    @BindView(R.id.fastLoginEmailAccTv)
    TextView fastLoginEmailAccTv;
    @BindView(R.id.fastCoorLayout)
    CoordinatorLayout fastCoorLayout;

    //隐私政策弹窗
    private PrivacyDialogView privacyDialog;

    //手机号区号选择
    private PhoneAreaCodeView phoneAreaCodeView;
    //倒计时
    private MyCountDownTimerUtils myCountDownTimerUtils;
    //提示框
    private PromptDialog promptDialog;
    //shareSDk
    private ShareSDKUtils instance;
    //三方登录参数
    private String thirdParams = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_login_layout);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        instance = ShareSDKUtils.getInstance(loginListenter);

    }

    @Override
    protected IFastPresent createPresenter() {
        return new IFastPresent();
    }

    private void initViews() {
        fastPrivacyTv.setText("《" + getResources().getString(R.string.privacy) + "》");
        fastLoginEmailAccTv.setText(getResources().getText(R.string.user_emil_regsiter) + ">");
        privacyCheckBox.setChecked(true);

        showPrivacyDialog();
    }

    @OnClick({R.id.fastLoginPhoneAreaLin, R.id.fastLoginGetCodeTv,
            R.id.fastLoginBtn, R.id.fastQqImg, R.id.fastWxImg,
            R.id.fastAccountLoginTv, R.id.fastLoginVistTv,
            R.id.fastLoginEmailAccTv, R.id.fastUserProtocalTv, R.id.fastPrivacyTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fastUserProtocalTv:
                startActivity(UserProtocalActivity.class);
                break;
            case R.id.fastPrivacyTv:
                startActivity(PrivacyActivity.class);
                break;
            case R.id.fastLoginPhoneAreaLin:
                chooseAreaCode();
                break;
            case R.id.fastLoginGetCodeTv:
                sendPhoneAreaCode();
                break;
            case R.id.fastLoginBtn:
                fastLogin();
                break;
            case R.id.fastQqImg:    //QQ登录
                if (instance != null)
                    instance.loginShow(QQ.NAME);
                break;
            case R.id.fastWxImg:    //微信登录
                if (instance != null)
                    instance.loginShow(Wechat.NAME);
                break;
            case R.id.fastAccountLoginTv:   //账号密码登录
                startActivity(NewLoginActivity.class);
                break;
            case R.id.fastLoginVistTv:  //游客登录
                visitorLogin();
                break;
            case R.id.fastLoginEmailAccTv:  //邮箱用户
                startActivity(RegisterActivity.class);
                break;
        }
    }


    //游客登录
    private void visitorLogin() {
        if (promptDialog == null)
            promptDialog = new PromptDialog(this);
        promptDialog.show();
        promptDialog.setTitle(getResources().getString(R.string.prompt));
        promptDialog.setContent(getResources().getString(R.string.login_alert));
        promptDialog.setleftText(getResources().getString(R.string.cancle));
        promptDialog.setrightText(getResources().getString(R.string.confirm));
        promptDialog.setListener(new PromptDialog.OnPromptDialogListener() {
            @Override
            public void leftClick(int code) {
                promptDialog.dismiss();
            }

            @Override
            public void rightClick(int code) {
                promptDialog.dismiss();
                showLoadingDialog("loading...");
                mPresenter.visitorLogin();
            }
        });
    }

    private void sendPhoneAreaCode() {
        String phoneStr = fastLoginPhoneNumberEdit.getText().toString();
        if (WatchUtils.isEmpty(phoneStr)) {
            Snackbar.make(fastCoorLayout,getResources().getString(R.string.input_name),Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (myCountDownTimerUtils == null)
            myCountDownTimerUtils = new MyCountDownTimerUtils(60 * 1000, 1000);

        myCountDownTimerUtils.start();
        mPresenter.getPhoneCode();
    }

    private void fastLogin() {
        String phoneStr = fastLoginPhoneNumberEdit.getText().toString();
        String phoneCode = fastLoginPhoneCodeEdit.getText().toString();
        if (WatchUtils.isEmpty(phoneStr) || WatchUtils.isEmpty(phoneCode)) {
            Snackbar.make(fastCoorLayout,"请输入手机号和验证码",Snackbar.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog("loading...");
        mPresenter.fastLogin();

    }


    @Override
    public String getPhoneAreaCode() {
        return StringUtils.substringAfter(fastLoginPhoneAreCodeTv.getText().toString(), "+");
    }

    @Override
    public String getInputPhone() {
        return fastLoginPhoneNumberEdit.getText().toString();
    }

    @Override
    public String getPhoneCode() {
        return fastLoginPhoneCodeEdit.getText().toString();
    }

    //第三方登陆的参数
    @Override
    public String getThirdLogin() {
        return thirdParams;
    }

    @Override
    public void fastLoginSuccess(int code, String data) {
        closeLoadingDialog();
        Log.e(TAG, "------succ=" + code + "--data=" + data);
        if (code == 0x02 || code == 0x03 || code == 0x04) {   //登录成功了
            loginSucc(data);
            return;
        }
        Snackbar.make(fastCoorLayout,data,Snackbar.LENGTH_SHORT).show();
    }

    private void loginSucc(String data) {
        UserInfoBean userInfoBean = new Gson().fromJson(data, UserInfoBean.class);
        if (userInfoBean != null) {
            Log.e(TAG, "------userInfo=" + userInfoBean.getNickname());
            Common.customer_id = userInfoBean.getUserid();
            //保存userid
            SharedPreferencesUtils.saveObject(FastLoginActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
            SharedPreferencesUtils.saveObject(FastLoginActivity.this, "userInfo", data);
            SharedPreferencesUtils.saveObject(FastLoginActivity.this, Commont.USER_INFO_DATA, data);

            startActivity(NewSearchActivity.class);
            finish();
        }

    }

    @Override
    public void fastLoginFail(String error) {
        closeLoadingDialog();
        Log.e(TAG, "------error=" + error);
        Snackbar.make(fastCoorLayout,error,Snackbar.LENGTH_SHORT).show();
    }


    //选择区号
    private void chooseAreaCode() {
        if (phoneAreaCodeView == null)
            phoneAreaCodeView = new PhoneAreaCodeView(this);
        phoneAreaCodeView.show();
        phoneAreaCodeView.setPhoneAreaClickListener(new PhoneAreaCodeView.PhoneAreaClickListener() {
            @Override
            public void chooseAreaCode(AreCodeBean areCodeBean) {
                phoneAreaCodeView.dismiss();
                fastLoginPhoneAreCodeTv.setText("+" + areCodeBean.getPhoneCode());
            }
        });


    }


    private class MyCountDownTimerUtils extends CountDownTimer {

        MyCountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            fastLoginGetCodeTv.setClickable(false);
            fastLoginGetCodeTv.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            fastLoginGetCodeTv.setClickable(true);
            fastLoginGetCodeTv.setText(getResources().getString(R.string.send_code));
        }
    }


    private LoginListenter loginListenter = new LoginListenter() {
        @Override
        public void OnLoginListenter(Platform platform, HashMap<String, Object> hashMap) {
            String _id = platform.getDb().getUserId();
            thirdLogin(_id);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    };

    //QQ登录
    private void thirdLogin(String id) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String deviceId = pushService.getDeviceId();
        Map<String, Object> thirdMap = new HashMap<>();
        thirdMap.put("thirdId", id);
        thirdMap.put("thirdType", 1);
        thirdMap.put("deviceToken", "");
        thirdMap.put("deviceType", "android");
        thirdMap.put("language", "ch");
        thirdMap.put("deviceId", deviceId == null ? "" : deviceId);
        thirdParams = new Gson().toJson(thirdMap);
        mPresenter.thirdFastLogin();

    }


    public long exitTime; // 储存点击退出时间

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.showToast(FastLoginActivity.this, getResources().getString(R.string.string_double_out));
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    // 全局推出
                    MyApp.getInstance().removeALLActivity();
                    return true;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void showPrivacyDialog() {
        boolean isFirstPrivacy = (boolean) SharedPreferencesUtils.getParam(FastLoginActivity.this, "is_first", false);
        if (isFirstPrivacy)
            return;
        privacyDialog = new PrivacyDialogView(this);
        privacyDialog.show();
        privacyDialog.setCancelable(false);
        privacyDialog.setOnPirvacyClickListener(new PrivacyDialogView.OnPirvacyClickListener() {
            @Override
            public void disCancleView() {
                showPermissView();

            }

            @Override
            public void disAgreeView() {
                MyApp.getInstance().removeALLActivity();
            }
        });
    }

    private void showPermissView() {
        startActivityForResult(new Intent(FastLoginActivity.this, ShowPermissDialogView.class), 0x00);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AndPermission.with(this).runtime().permission(new String[]{Permission.WRITE_EXTERNAL_STORAGE,Permission.ACCESS_FINE_LOCATION}).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.deachView();
    }
}
