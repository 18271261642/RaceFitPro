package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * Created by thinkpad on 2017/3/9.
 * 意见反馈
 */

public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.question_detail_tv)
    TextView questionDetailTv;
    @BindView(R.id.password_feed)
    EditText password;
    @BindView(R.id.contact_info_tv)
    TextView contactInfoTv;
    @BindView(R.id.leave_phone_et)
    EditText leavePhoneEt;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.feedback);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if(WatchUtils.isEmpty(result))
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(!jsonObject.has("code"))
                        return;
                    if(jsonObject.getInt("code") == 200){
                        ToastUtil.showShort(FeedbackActivity.this, getString(R.string.submit_success));
                        finish();
                        closeKeyboard();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String describe_problem = password.getText().toString();
        String confrimPass = leavePhoneEt.getText().toString();
        if(!WatchUtils.isEmpty(describe_problem) && !WatchUtils.isEmpty(confrimPass)){
            submitFeekback(describe_problem, confrimPass);
        }else{
            ToastUtil.showShort(this, getString(R.string.leave_contact));
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isMobile(String number) {
        String num = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return number.matches(num);
    }

    public boolean isEmail(String strEmail) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(strEmail);
        return matcher.matches();
    }

    private void submitFeekback(String content, String contact) {
        try {

            String deviceStr = Build.BRAND+"-"+Build.MODEL;
            String bleName = (String) SharedPreferencesUtils.readObject(this,Commont.BLENAME);
            Gson gson = new Gson();
            HashMap<String, Object> map = new HashMap<>();
            String userId = (String) SharedPreferencesUtils.readObject(this,Commont.BLEMAC);
            map.put("userId", userId);
            map.put("content", bleName+deviceStr+content);
            map.put("contact", contact);
            String mapjson = gson.toJson(map);
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, FeedbackActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber, Commont.FRIEND_BASE_URL + URLs.yijian, mapjson);
        }catch (Exception error){
            error.printStackTrace();
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeKeyboard();
    }

    /**
     * 关闭软键盘
     */
    public void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        hideKeyboard();

        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


}
