package com.example.bozhilun.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.LoginListenter;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.ShareSDKUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.view.PrivacyActivity;
import com.example.bozhilun.android.view.PrivacyDialogView;
import com.example.bozhilun.android.view.PromptDialog;
import com.example.bozhilun.android.view.ShowPermissDialogView;
import com.example.bozhilun.android.view.UserProtocalActivity;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登录页面
 * Created by thinkpad on 2017/3/3.
 */

public class NewLoginActivity extends WatchBaseActivity implements LoginListenter, RequestView {

    private static final String TAG = "NewLoginActivity";


    @BindView(R.id.logo_img)
    ImageView logoImg;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password_logon)
    EditText passwordLogon;
    @BindView(R.id.login_visitorTv)
    TextView loginVisitorTv;
    @BindView(R.id.forget_tv)
    TextView forgetTv;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.login_linearlayout)
    LinearLayout loginLinearlayout;
    @BindView(R.id.xinlang_iv)
    RelativeLayout xinlangIv;
    @BindView(R.id.qq_iv)
    RelativeLayout qqIv;
    @BindView(R.id.weixin_iv)
    RelativeLayout weixinIv;
    @BindView(R.id.ll_bottom_tabaa)
    LinearLayout llBottomTabaa;
    @BindView(R.id.fecebook_longin)
    RelativeLayout fecebookLongin;
    @BindView(R.id.google_longin)
    RelativeLayout googleLongin;
    @BindView(R.id.twitter_longin)
    RelativeLayout twitterLongin;
    @BindView(R.id.ll_bottom_tabguowai)
    LinearLayout llBottomTabguowai;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;


    private ShareSDKUtils instance;
    private BluetoothAdapter bluetoothAdapter;  //蓝牙适配器


    private UpdateManager updateManager = null;

    private RequestPressent requestPressent;

    private PrivacyDialogView privacyDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        instance = ShareSDKUtils.getInstance(this);

        initViews();

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    private void requestPermiss() {
        //请求读写SD卡的权限
        AndPermission.with(NewLoginActivity.this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION,Permission.WRITE_EXTERNAL_STORAGE)
                .start();

        //判断蓝牙是否开启
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
//        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
//            turnOnBlue();
//        }
    }

    private void turnOnBlue() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                1111);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                1112);
    }

    @SuppressLint({"SetTextI18n", "NewApi"})
    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getText(R.string.user_login));
        boolean lauage = getResources().getConfiguration().locale.getCountry().equals("CN");
        if (lauage) {
            llBottomTabaa.setVisibility(View.VISIBLE);
            llBottomTabguowai.setVisibility(View.GONE);
        } else {
            llBottomTabaa.setVisibility(View.GONE);
            llBottomTabguowai.setVisibility(View.VISIBLE);
        }
        //if (loginWaveView != null) loginWaveView.startMove();  //波浪线贝塞尔曲线
        //版本更新
        updateManager = new UpdateManager(NewLoginActivity.this, Commont.FRIEND_BASE_URL + URLs.getvision);
        updateManager.checkForUpdate(true);

        //appVersionTv.setText(getResources().getString(R.string.app_version)+BuildConfig.VERSION_NAME);


//        boolean isFirstPrivacy = (boolean) SharedPreferencesUtils.getParam(NewLoginActivity.this, "is_first", false);
//        if (isFirstPrivacy)
//            return;
//        privacyDialog = new PrivacyDialogView(this);
//        privacyDialog.show();
//        privacyDialog.setCancelable(false);
//        privacyDialog.setOnPirvacyClickListener(new PrivacyDialogView.OnPirvacyClickListener() {
//            @Override
//            public void disCancleView() {
//                showPermissView();
//
//            }
//
//            @Override
//            public void disAgreeView() {
//                MyApp.getInstance().removeALLActivity();
//            }
//        });

    }


    private void showPermissView() {
        startActivityForResult(new Intent(NewLoginActivity.this, ShowPermissDialogView.class), 0x00);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x00) {
            requestPermiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (loginWaveView != null) loginWaveView.stopMove();  //波浪线贝塞尔曲线
        if (requestPressent != null)
            requestPressent.detach();
        if (handler != null) handler = null;
        isShouQuanOnClick = false;
    }


    @OnClick({R.id.login_visitorTv, R.id.forget_tv, R.id.login_btn,
            R.id.register_btn, R.id.xinlang_iv, R.id.qq_iv,
            R.id.weixin_iv, R.id.fecebook_longin, R.id.google_longin,
            R.id.twitter_longin, R.id.userProtocalTv,
            R.id.appVersionTv,R.id.commentB30BackImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.userProtocalTv:   //用户协议
                startActivity(UserProtocalActivity.class);
                break;
            case R.id.appVersionTv:     //隐私政策
                startActivity(PrivacyActivity.class);
                break;
            case R.id.login_visitorTv://游客
                final PromptDialog pd = new PromptDialog(NewLoginActivity.this);
                pd.show();
                pd.setTitle(getResources().getString(R.string.prompt));
                pd.setContent(getResources().getString(R.string.login_alert));
                pd.setleftText(getResources().getString(R.string.cancle));
                pd.setrightText(getResources().getString(R.string.confirm));
                pd.setListener(new PromptDialog.OnPromptDialogListener() {
                    @Override
                    public void leftClick(int code) {
                        pd.dismiss();
                    }

                    @Override
                    public void rightClick(int code) {
                        pd.dismiss();
                        Gson gson = new Gson();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("phone", "bozlun888@gmail.com");
                        map.put("pwd", Md5Util.Md532("e10adc3949ba59abbe56e057f20f883e"));
                        String mapjson = gson.toJson(map);
//                        Log.e("msg", "-mapjson-" + mapjson);
                        if (requestPressent != null) {
                            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.logon, NewLoginActivity.this, mapjson, 1);
                        }

                    }
                });
                break;
            case R.id.forget_tv://忘记密码
                startActivity(new Intent(NewLoginActivity.this, ForgetPasswardActivity.class));
                break;
            case R.id.login_btn://登陆
                //登录时判断
                String pass = passwordLogon.getText().toString();
                String usernametxt = username.getText().toString();
                if(WatchUtils.isEmpty(usernametxt)){
                    Snackbar.make(view,getResources().getString(R.string.input_name),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(WatchUtils.isEmpty(pass)){
                    Snackbar.make(view, getResources().getString(R.string.input_password),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                loginRemote(usernametxt, pass);
                break;
            case R.id.register_btn://注册
                startActivity(new Intent(NewLoginActivity.this, RegisterActivity2.class));
                break;
            case R.id.xinlang_iv://新浪
                if (instance != null) {
                    isShouQuanOnClick = true;
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    instance.loginShow(SinaWeibo.NAME);
                }
                break;
            case R.id.qq_iv://qq
                if (instance != null) {
                    isShouQuanOnClick = true;
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    instance.loginShow(QQ.NAME);
                }
                break;
            case R.id.weixin_iv://微信
                if (instance != null) {
                    isShouQuanOnClick = true;
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    instance.loginShow(Wechat.NAME);
                }
                break;
            case R.id.fecebook_longin://facebook
                if (instance != null) {
                    isShouQuanOnClick = true;
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    instance.loginShow(Facebook.NAME);
                }
                break;
            case R.id.google_longin://google+
                if (instance != null) {
                    isShouQuanOnClick = true;
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    instance.loginShow(GooglePlus.NAME);
                }
                break;
            case R.id.twitter_longin://twitter
//                if (instance != null) instance.loginShow(Twitter.NAME);
                break;
        }
    }


    boolean isShouQuanOnClick = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (isShouQuanOnClick)
            showLoadingDialog(getResources().getString(R.string.dlog));
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            isShouQuanOnClick = false;
            switch (message.what) {
                case 0x01:
                    closeLoadingDialog();
                    showLoadingDialog(getResources().getString(R.string.user_login) + "...");
                    //ToastUtil.showShort(MyApp.getInstance(), MyApp.getInstance().getResources().getString(R.string.shouquancg));
                    break;
                case 0x02:
                    //ToastUtil.showShort(MyApp.getInstance(), MyApp.getInstance().getResources().getString(R.string.shouquanshib));
                    closeLoadingDialog();
                    break;
                case 0x03:
                    ToastUtil.showShort(MyApp.getInstance(), MyApp.getInstance().getResources().getString(R.string.cancle));
                    closeLoadingDialog();
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onStop() {
        super.onStop();
        try {
            closeLoadingDialog();
        } catch (Error error) {
            error.printStackTrace();
        }
    }


    //用户手机登录
    private void loginRemote(String uName, String uPwd) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        String deviceId = pushService.getDeviceId();
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", uName);
        map.put("pwd", Md5Util.Md532(uPwd));
        map.put("deviceToken", "");
        map.put("deviceId", deviceId);
        map.put("deviceType", "android");
        map.put("language", "zh-CN");

        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.logon, NewLoginActivity.this, mapjson, 1);
        }
    }


    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 清除掉所有特殊字符
        String regEx = "[-`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_♚\uD83D\uDC8E]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    @Override
    public void OnLoginListenter(Platform platform, HashMap<String, Object> hashMap) {
        Log.e(TAG, "---------OnLoginListenter=" + platform.toString() + "------=" + hashMap.toString());

        if (handler != null) handler.sendEmptyMessage(0x01);
        String userId = platform.getDb().getUserId();//获取用户账号
        String userName = platform.getDb().getUserName();//获取用户名字
        String userIcon = platform.getDb().getUserIcon();//获取用户头像
        String userGender = platform.getDb().getUserGender(); //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
        upUserData(userId, userName, userIcon, userGender);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        //Log.e(TAG,"----------error="+platform.toString()+"---throw="+throwable.toString());
        if (handler != null) handler.sendEmptyMessage(0x02);
//        try {
//
//            isShouQuanOnClick = false;
//            Toast.makeText(MyApp.getInstance(), MyApp.getInstance().getResources().getString(R.string.shouquanshib), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            //解决在子线程中调用Toast的异常情况处理
//            Looper.prepare();
//            Toast.makeText(MyApp.getInstance(), MyApp.getInstance().getResources().getString(R.string.shouquanshib), Toast.LENGTH_SHORT).show();
//            Looper.loop();
//        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
        if (handler != null) handler.sendEmptyMessage(0x03);
    }


    /**
     * 根据返回注册账号
     *
     * @param userId
     * @param userName
     * @param userIcon
     * @param userGender
     */
    private void upUserData(String userId, String userName, String userIcon, String userGender) {

        try {
//            userName = StringFilter(userName);//过滤昵称字符
//            if (TextUtils.isEmpty(userName)) userName = "racefitpro";
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("thirdId", userId);
//            jsonObject.put("nickName", userName);
//            jsonObject.put("thirdType", 4);//QQ登录
//            jsonObject.put("image", userIcon);//头像地址
//
//            jsonObject.put("sex", "M");//默认为男，根据下面在判断
//            //性别
//            if (userGender.trim().equals("男") || userGender.trim().equalsIgnoreCase("M")) {
//                jsonObject.put("sex", "M");
//            } else {
//                jsonObject.put("sex", "F");
//            }


            CloudPushService pushService = PushServiceFactory.getCloudPushService();
            String deviceId = pushService.getDeviceId();


            Map<String, Object> map = new HashMap<>();
            map.put("thirdId", userId);
            map.put("thirdType", 1);
            map.put("deviceToken", "");
            map.put("deviceType", "android");
            map.put("language", "ch");
            map.put("deviceId", deviceId == null ? "" : deviceId);

            String url = Commont.FRIEND_BASE_URL + URLs.disanfang;
            if (requestPressent != null) {
                requestPressent.getRequestJSONObject(0x01, url, NewLoginActivity.this, new Gson().toJson(map), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        Log.e(TAG, "---------succ=" + what + "---obj=" + object);
        if (object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        switch (what) {
            case 0x01:  //手机号登录
                loginForUserPhone(object.toString(), daystag);
                break;
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    //用户输入账号登录
    private void loginForUserPhone(String result, int tag) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                String userStr = jsonObject.getString("data");
                if (userStr != null) {
                    UserInfoBean userInfoBean = new Gson().fromJson(userStr, UserInfoBean.class);
                    Common.customer_id = userInfoBean.getUserid();
                    if (tag == 2) {   //第三方登录统计
                        MobclickAgent.onProfileSignIn("QQ", userInfoBean.getUserid());
                    } else {
                        //账号登录统计
                        MobclickAgent.onProfileSignIn(userInfoBean.getUserid());
                    }
                    //保存userid
                    SharedPreferencesUtils.saveObject(NewLoginActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                    SharedPreferencesUtils.saveObject(NewLoginActivity.this, "userInfo", userStr);
                    SharedPreferencesUtils.saveObject(NewLoginActivity.this, Commont.USER_INFO_DATA, userStr);

                    startActivity(new Intent(NewLoginActivity.this, NewSearchActivity.class));
                    finish();
                }

            } else {
                ToastUtil.showToast(NewLoginActivity.this, jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
