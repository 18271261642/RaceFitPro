package com.example.bozhilun.android.siswatch;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.commdbserver.ActiveManage;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/1.
 */

public class GetUserInfoActivity extends WatchBaseActivity implements RequestView {


    private RequestPressent requestPressent;
    private ActiveManage activeManage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        sendRecordApp();

        updateDevice();

    }

    private void updateDevice() {
        if(activeManage == null)
            activeManage = ActiveManage.getActiveManage();
        activeManage.updateUserDevice(this,"");
    }

    //获取用户信息
    private void sendRecordApp() {
        String userId = (String) SharedPreferencesUtils.readObject(GetUserInfoActivity.this,"userId");
        if(userId == null)
            return;
        if(requestPressent != null){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",userId);
                requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.getUserInfo,
                        this,jsonObject.toString(),1);

            }catch (Exception e){
                e.printStackTrace();
            }

        }


        //用户进入了此界面
//        if(requestPressent != null){
//            Map<String,String> param = new HashMap<>();
//            param.put("userId",userId);
//            param.put("appVersion",WatchUtils.getVersionName(this)+"");
//            param.put("phoneType",Build.BRAND+"-"+Build.MODEL);
//            param.put("systemLanguage",WatchUtils.getSystemLanguage());
//            param.put("region",Locale.getDefault().getCountry()+"");
//            param.put("equipment","");
//            Log.e("USER","-----param="+param.toString());
//            requestPressent.getRequestJSONObject(0x02,Commont.FRIEND_BASE_URL+"/user/inSearch",this,new Gson().toJson(param),2);
//
//        }



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
        Log.e("USER","------what="+what+"---obj="+object.toString());
        if(object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(!jsonObject.has("code"))
                return;
            if(jsonObject.getInt("code") == 200){
                if(what == 0x01){
                    analysisUserInfo(jsonObject);
                }
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


    private void analysisUserInfo(JSONObject jsonObject) {
        try {
            String data = jsonObject.getString("data");
            UserInfoBean userInfoBean = new Gson().fromJson(data,UserInfoBean.class);
            if(userInfoBean != null){
                SharedPreferencesUtils.saveObject(GetUserInfoActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                //保存一下用户性别
                SharedPreferencesUtils.setParam(GetUserInfoActivity.this,Commont.USER_SEX,userInfoBean.getSex());
                String height = userInfoBean.getHeight();
                if (height.contains("cm")) {
                    String newHeight = height.substring(0, height.length() - 2);
                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, Commont.USER_HEIGHT, newHeight.trim());
                } else {
                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, Commont.USER_HEIGHT, height.trim());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
