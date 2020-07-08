package com.example.bozhilun.android.friend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.FrendDataBean;
import com.example.bozhilun.android.friend.bean.FrendMimiBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrendSettingActivity extends WatchBaseActivity implements RequestView, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.toolbar_normal)
    Toolbar toolbarNormal;
    @BindView(R.id.switch_step)
    ToggleButton switchStep;
    @BindView(R.id.switch_sleep)
    ToggleButton switchSleep;
    @BindView(R.id.switch_heart)
    ToggleButton switchHeart;
    private RequestPressent requestPressent;
    Intent intent = null;
    String applicant = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_setting_activity);
        ButterKnife.bind(this);


        intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfoShow(applicant);
    }

    private void init() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        barTitles.setText(getResources().getString(R.string.string_my_frend_privacy));
        toolbarNormal.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        toolbarNormal.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });//右边返回按钮点击事件

        switchStep.setOnCheckedChangeListener(this);
        switchSleep.setOnCheckedChangeListener(this);
        switchHeart.setOnCheckedChangeListener(this);
    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null || TextUtils.isEmpty(object.toString().trim())) return;
        Log.d("-----------隐私--", object.toString());
        Message message = new Message();
        message.what = what;
        message.obj = object;
        if (handler != null) handler.sendMessage(message);
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    FrendMimiBean frendMimiBean = new Gson().fromJson(message.obj.toString(), FrendMimiBean.class);
                    if (frendMimiBean != null) {
                        String resultCode = frendMimiBean.getResultCode();
                        if (!WatchUtils.isEmpty(resultCode) || resultCode.equals("001")) {
                            List<FrendMimiBean.EisListBean> eisList = frendMimiBean.getEisList();
                            for (FrendMimiBean.EisListBean eList :
                                    eisList) {
                                int setType = eList.getSetType();
                                switch (setType) {
                                    case 1://步数
                                        if (eList.getExhibition() == 1) {
                                            switchStep.setChecked(false);
                                        } else {
                                            switchStep.setChecked(true);
                                        }
                                        break;
                                    case 2://睡眠
                                        if (eList.getExhibition() == 1) {
                                            switchSleep.setChecked(false);
                                        } else {
                                            switchSleep.setChecked(true);
                                        }
                                        break;
                                    case 3://心率
                                        if (eList.getExhibition() == 1) {
                                            switchHeart.setChecked(false);
                                        } else {
                                            switchHeart.setChecked(true);
                                        }
                                        break;
                                    case 4://血压
                                        if (eList.getExhibition() == 1) {
                                        } else {
                                        }
                                        break;
                                    case 5://血氧
                                        break;
                                }
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    });


    /**
     * 获取信息展示设置
     *
     * @param applicant
     */
    public void getInfoShow(String applicant) {
        String sleepUrl = URLs.HTTPs + Commont.GETFrendDetailedIsVis;
        JSONObject sleepJson = new JSONObject();
        try {
            if (WatchUtils.isEmpty(applicant)) applicant = intent.getStringExtra("applicant");
            String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
            if (!TextUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            sleepJson.put("friendId", applicant);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendSettingActivity.this, sleepJson.toString(), 0);
        }
    }


    /**
     * 将 list 转换 为 JSONArray
     *
     * @param list
     * @return
     */
    public JSONArray ProLogListJson(List<FrendDataBean.FriendInfoBean.ExInfoSetListBean> list) {
        JSONArray json = new JSONArray();
        for (FrendDataBean.FriendInfoBean.ExInfoSetListBean pLog : list) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("exhibition", pLog.getExhibition());
                jo.put("friendId", pLog.getFriendId());
                jo.put("addTime", pLog.getAddTime());
                jo.put("updateTime", pLog.getUpdateTime());
                jo.put("id", pLog.getId());
                jo.put("setType", pLog.getSetType());
                jo.put("userId", pLog.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jo);
        }
        return json;
    }

    /**
     * 获取信息展示设置
     */
    public void setInfoShow(int type, int see) {
        String sleepUrl = URLs.HTTPs + Commont.FrendDetailedIsVis;
        JSONObject sleepJson = new JSONObject();

        try {

            if (WatchUtils.isEmpty(applicant)) applicant = intent.getStringExtra("applicant");
            String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
            List<FrendDataBean.FriendInfoBean.ExInfoSetListBean> exInfoSetListBeans = new ArrayList<>();
            FrendDataBean.FriendInfoBean.ExInfoSetListBean exInfoSetListBean = new FrendDataBean.FriendInfoBean.ExInfoSetListBean();
            exInfoSetListBean.setSetType(type);
            exInfoSetListBean.setExhibition(see);
            exInfoSetListBean.setFriendId(applicant);
            exInfoSetListBean.setUserId(userId);
            exInfoSetListBeans.add(exInfoSetListBean);
            if (!TextUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            sleepJson.put("friendId", applicant);
            JSONArray jsonArray = ProLogListJson(exInfoSetListBeans);
            sleepJson.put("ispList", jsonArray);
            Log.d("-----------朋友--", " 获取好友--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, sleepUrl, FrendSettingActivity.this, sleepJson.toString(), 0);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_step:
                if (b) {
                    setInfoShow(1, 0);
                } else {
                    setInfoShow(1, 1);
                }
                break;
            case R.id.switch_sleep:
                if (b) {
                    setInfoShow(2, 0);
                } else {
                    setInfoShow(2, 1);
                }
                break;
            case R.id.switch_heart:
                if (b) {
                    setInfoShow(3, 0);
                } else {
                    setInfoShow(3, 1);
                }
                break;
        }
    }
}
