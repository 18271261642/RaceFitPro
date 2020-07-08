package com.example.bozhilun.android.wxapi;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by wyl on 2017/2/6.
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler , RequestView {

    private static final String TAG = "WXEntryActivity";

    private IWXAPI iwxapi;

    private RequestPressent requestPressent;

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG,"---baseReq="+baseReq.toString());
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;
            //获取微信传回的code
            String code = newResp.token;
            Log.e(TAG,"------code--"+code);

            String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxd9dd17f96d73d54a&secret=41ecaeaefdddd6f1eebb2542eb27f458&code=" + code + "&grant_type=authorization_code";
            if(requestPressent != null){
                requestPressent.getRequestJSONObject(0x01,tokenUrl,WXEntryActivity.this,1);
            }



/**
 * 通过这个获取信息
 * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 * 获得opid和access_token
 *0da2a2a2c3e35d25460c8f6bb01cd876
 */
            //判断网络是否连接
            boolean is = ConnectManages.isNetworkAvailable(WXEntryActivity.this);
          /*  if (is) {
                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxd9dd17f96d73d54a&secret=41ecaeaefdddd6f1eebb2542eb27f458&code=" + code + "&grant_type=authorization_code", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response == null)
                                    return;
                                Log.e(TAG,"-----response----"+response.toString());
                                try {
                                    JSONObject RES = new JSONObject(response.toString());
                                    if(!RES.has("access_token"))
                                        return;
                                    String ACCESS_TOKEN = RES.getString("access_token");
                                    String OPENID = RES.getString("openid");
                                    *//**
                                     * 获取用户信息
                                     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                                     *//*
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://api.weixin.qq.com/sns/userinfo?access_token=" + ACCESS_TOKEN + "&openid=" + OPENID, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    //Log.e(TAG,"------用户信息---"+response.toString());
                                                    *//**
                                                     * 获取用户信息
                                                     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                                                     *//*
                                                    try {
                                                        JSONObject xinxi = new JSONObject(response.toString());
                                                        JSONObject weixin = new JSONObject();
                                                        weixin.put("thirdId", xinxi.getString("openid"));
                                                        weixin.put("thirdType", "3");
                                                        weixin.put("image", xinxi.getString("headimgurl"));
                                                        if (xinxi.getString("sex").equals(1)) {
                                                            weixin.put("sex", "M");//男
                                                        } else {
                                                            weixin.put("sex", "F");//女
                                                        }
                                                        weixin.put("nickName", xinxi.getString("nickname"));
                                                        *//**
                                                         * 提交到服务器
                                                         *//*
                                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, Commont.FRIEND_BASE_URL + URLs.disanfang, weixin,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject jsonObject) {
                                                                            try {
                                                                                if (!jsonObject.has("code"))
                                                                                    return;
                                                                                if (jsonObject.getInt("code") == 200) {
                                                                                    String userStr = jsonObject.getString("data");
                                                                                    if (userStr != null) {
                                                                                        UserInfoBean userInfoBean = new Gson().fromJson(userStr, UserInfoBean.class);
                                                                                        Common.customer_id = userInfoBean.getUserid();
                                                                                        MobclickAgent.onProfileSignIn("WX", userInfoBean.getUserid());
                                                                                        //保存userid
                                                                                        SharedPreferencesUtils.saveObject(WXEntryActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                                                                                        SharedPreferencesUtils.saveObject(WXEntryActivity.this, "userInfo", userStr);
                                                                                        SharedPreferencesUtils.saveObject(WXEntryActivity.this, Commont.USER_INFO_DATA, userStr);

                                                                                        startActivity(new Intent(WXEntryActivity.this, NewSearchActivity.class));
                                                                                        finish();
                                                                                    }

                                                                                }
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }) {
                                                            @Override
                                                            public Map<String, String> getHeaders() {
                                                                HashMap<String, String> headers = new HashMap<>();
                                                                headers.put("Accept", "application/json");
                                                                headers.put("Content-Type", "application/json; charset=UTF-8");

                                                                return headers;
                                                            }
                                                        };
                                                        requestQueue.add(jsonRequest);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Override
                                        public Map<String, String> getHeaders() {
                                            HashMap<String, String> headers = new HashMap<>();
                                            headers.put("Accept", "application/json");
                                            headers.put("Content-Type", "application/json; charset=UTF-8");
                                            return headers;
                                        }
                                    };
                                    requestQueue.add(jsonRequest);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        return headers;
                    }
                };
                requestQueue.add(jsonRequest);
            } else {
                Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
            }*/
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"-----oncratewx---");
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        //注册API
        iwxapi = WXAPIFactory.createWXAPI(this, "wxd9dd17f96d73d54a",true);
        iwxapi.handleIntent(getIntent(), this);


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent,this);

    }




    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        Log.e(TAG,"--------what="+what+"---obj="+object.toString());
        if(what == 0x01){   //获取token
            try {
                JSONObject RES = new JSONObject(object.toString());
                if (!RES.has("access_token")){
                    finish();
                    return;
                }

                String access_token = RES.getString("access_token");
                String openid = RES.getString("openid");

                //获取用户信息
                getWxUsrInfo(access_token,openid);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(what == 0x02){   //返回用户信息
            analysisWxUser(object);
        }
        if(what == 0x03){   //用户第三方登录返回
            analysisUserInfoData(object);
        }
    }




    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    //获取微信用户信息
    private void getWxUsrInfo(String access_token, String openid) {
        String userStrUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
        if(requestPressent != null)
            requestPressent.getRequestJSONObject(0x02,userStrUrl,WXEntryActivity.this,2);

    }

    //解析微信登录的用户信息
    private void analysisWxUser(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            String openIdStr = jsonObject.getString("openid");

            CloudPushService pushService = PushServiceFactory.getCloudPushService();
             String deviceId = pushService.getDeviceId();


            Map<String,Object> map = new HashMap<>();
            map.put("thirdId",openIdStr);
            map.put("thirdType",1);
            map.put("deviceToken","");
            map.put("deviceType","android");
            map.put("language","ch");
            map.put("deviceId",deviceId == null ? "" : deviceId);

            String url = Commont.FRIEND_BASE_URL + URLs.disanfang;
            String parmas = new Gson().toJson(map);
            requestPressent.getRequestJSONObject(0x03,url,WXEntryActivity.this,parmas,3);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //解析第三方登录成功返回的数据
    private void analysisUserInfoData(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                String userStr = jsonObject.getString("data");
                if (userStr != null) {
                    UserInfoBean userInfoBean = new Gson().fromJson(userStr, UserInfoBean.class);
                    Common.customer_id = userInfoBean.getUserid();
                    //保存userid
                    SharedPreferencesUtils.saveObject(WXEntryActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                    SharedPreferencesUtils.saveObject(WXEntryActivity.this, "userInfo", userStr);
                    SharedPreferencesUtils.saveObject(WXEntryActivity.this, Commont.USER_INFO_DATA, userStr);

                    startActivity(new Intent(WXEntryActivity.this, NewSearchActivity.class));
                    finish();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
