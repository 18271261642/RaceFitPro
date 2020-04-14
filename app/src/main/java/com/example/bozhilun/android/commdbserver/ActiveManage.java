package com.example.bozhilun.android.commdbserver;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/7/24
 */
public class ActiveManage  {

    private static final String TAG = "ActiveManage";


    private static ActiveManage activeManage;


    public static ActiveManage getActiveManage(){
        synchronized (ActiveManage.class){
            if(activeManage == null){
                activeManage = new ActiveManage();
            }
        }
        return activeManage;
    }

    private ActiveManage() {
    }

    //上传活跃度
    public void updateTodayActive(final Context context){
        String url = Commont.FRIEND_BASE_URL+"/user/loginInDay";
        boolean isSave = (boolean) SharedPreferencesUtils.getParam(context,WatchUtils.getCurrentDate(),false);
        if(!isSave){
            Map<String,String> map = new HashMap<>();
            map.put("userId", (String) SharedPreferencesUtils.readObject(context,Commont.USER_ID_DATA));
            OkHttpTool.getInstance().doRequest(url, new Gson().toJson(map), "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    if(result == null)return;
                    Log.e("ActiveManage","---------result="+result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(!jsonObject.has("code"))
                            return;
                        if(jsonObject.getInt("code") == 200){
                            SharedPreferencesUtils.setParam(context,WatchUtils.getCurrentDate(),true);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }

    }


    //上传连接成功
    public void updateUserDevice(Context context,String deviceStr){
        String userId = (String) SharedPreferencesUtils.readObject(context,Commont.USER_ID_DATA);
        if(WatchUtils.isEmpty(userId))
            return;
        try {
            String appStr = "";
            String packName = context.getPackageName();
            if(!WatchUtils.isEmpty(packName)){
                if(packName.equals("com.example.bozhilun.android")){
                    appStr = "zh";
                }else{
                    appStr = "en";
                }
            }

            Map<String,String> param = new HashMap<>();
            param.put("userId",userId);
            param.put("appVersion",appStr+"-"+WatchUtils.getVersionCode(context)+"");
            param.put("phoneType",Build.BRAND+"-"+Build.MODEL);
            param.put("systemLanguage",WatchUtils.getSystemLanguage());
            param.put("region",Locale.getDefault().getCountry()+"");
            param.put("equipment",deviceStr);
            Log.e("判断连接","--------参数="+param.toString()+"--时间="+WatchUtils.getCurrentDate1());
            OkHttpTool.getInstance().doRequest(Commont.FRIEND_BASE_URL + "/user/inSearch", new Gson().toJson(param), "", new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e("判断连接","-------是否连接="+result);
                }
            });




        }catch (Exception e){
            e.printStackTrace();
        }

    }



    //更新用户设备类型和mac
    public void updateUserMac(Context context,String deviceName,String deviceMac){
        String userId = (String) SharedPreferencesUtils.readObject(context,Commont.USER_ID_DATA);
        if(userId == null)
            return;
        try {
          String url = Commont.FRIEND_BASE_URL + Commont.CHANGE_DEVICE_TYPE;
          Map<String,String> map = new HashMap<>();
          map.put("userId",userId);
          map.put("equipment",deviceName);
          map.put("mac",deviceMac);
          OkHttpTool.getInstance().doRequest(url, new Gson().toJson(map), "", new OkHttpTool.HttpResult() {
              @Override
              public void onResult(String result) {
                  Log.e(TAG,"-----更改设备="+result);
              }
          });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
