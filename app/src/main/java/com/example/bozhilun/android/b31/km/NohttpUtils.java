package com.example.bozhilun.android.b31.km;

import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/12/13
 */
public class NohttpUtils {

    private static volatile NohttpUtils nohttpUtils;


    public NohttpUtils() {
    }


    public static NohttpUtils getNoHttpUtils(){
        if(nohttpUtils == null){
            synchronized (NohttpUtils.class){
                nohttpUtils = new NohttpUtils();
            }
        }
       return nohttpUtils;
    }


    /**
     *
     * @param what  int类型的标识
     * @param url   请求地址
     * @param params    参数
     * @param onResponseListener    回调
     */
    public void getModelRequestJSONObject(int what, String url, Map<String,Object> params, OnResponseListener<JSONObject> onResponseListener){
        Request<JSONObject> jsonObjectRequest = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
        //遍历map，将参数添加至requst中
        if(params != null && !params.isEmpty()){
            for(Map.Entry<String,Object> map : params.entrySet()){
                jsonObjectRequest.add(map.getKey(),map.getValue()+"");
            }
        }
        MyApp.getInstance().getNoRequestQueue().add(what,jsonObjectRequest,onResponseListener);
    }

    /**
     *
     * @param what  int类型的标识
     * @param url   请求地址
     * @param params    参数
     * @param onResponseListener    回调
     */
    public void getModelRequestJSONObject(RequestMethod requestMethod,int what, String url, Map<String,Object> params, OnResponseListener<JSONObject> onResponseListener){
       try {
           NoHttp.initialize(MyApp.getInstance());
           Request<JSONObject> jsonObjectRequest = NoHttp.createJsonObjectRequest(url, requestMethod);
           //遍历map，将参数添加至requst中
           if(params != null && !params.isEmpty()){
               for(Map.Entry<String,Object> map : params.entrySet()){
                   jsonObjectRequest.add(map.getKey(),map.getValue()+"");
               }
           }
           MyApp.getInstance().getNoRequestQueue().add(what,jsonObjectRequest,onResponseListener);
       }catch (ExceptionInInitializerError | Exception exceptionInInitializerError){
           exceptionInInitializerError.printStackTrace();
       }

    }


    /**
     *
     * @param what  int类型的标识
     * @param url   请求地址
     * @param jsonStr json格式字符串
     * @param onResponseListener    回调
     */
    public void getModelRequestJSONObject(int what, String url, String jsonStr, OnResponseListener<JSONObject> onResponseListener){
        try {
            NoHttp.initialize(MyApp.getInstance());
            Request<JSONObject> jsonObjectRequest = NoHttp.createJsonObjectRequest(url, RequestMethod.POST);
            //jsonObjectRequest.setDefineRequestBody(jsonStr,"application/json");
            jsonObjectRequest.setDefineRequestBodyForJson(jsonStr);
            MyApp.getInstance().getNoRequestQueue().add(what,jsonObjectRequest,onResponseListener);
        }catch (ExceptionInInitializerError | Exception exceptionInInitializerError){
            exceptionInInitializerError.printStackTrace();
        }
    }




    /**
     * 请求StringRequest
     * @param
     * @param
     */
    public void getModelRequestString(int what, String url, Map<String,Object> params, OnResponseListener<String> onResponseListener){
        Request<String> stringRequest = NoHttp.createStringRequest(url,RequestMethod.POST);
        //遍历map，将参数添加至requst中
        if(!params.isEmpty()){
            for(Map.Entry<String,Object> map : params.entrySet()){
                stringRequest.add(map.getKey(),map.getValue()+"");
                Log.e("rrr","-----mm="+map.getKey()+"-="+map.getValue());
            }
        }
        MyApp.getInstance().getNoRequestQueue().add(what,stringRequest,onResponseListener);
    }

    /**
     * 取消网络请求
     * @param what
     */
    public void cancleHttpPost(int what){
        MyApp.getInstance().getNoRequestQueue().cancelBySign(what);
    }

    /**
     * 取消网络请求
     * @param
     */
    public void cancleHttpPost(){
        MyApp.getInstance().getNoRequestQueue().cancelAll();
    }



}
