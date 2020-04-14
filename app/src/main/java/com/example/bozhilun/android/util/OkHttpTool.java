package com.example.bozhilun.android.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 通过OkHttp3获取服务端数据工具类
 *
 * @author XuBo
 */
public class OkHttpTool {

    /**
     * 单例实例
     */
    private static OkHttpTool mInstance;
    /**
     * OkHttp请求类
     */
    private OkHttpClient mHttpClient;
    /**
     * 请求session
     */
    private String mSession;

    /**
     * 私有化构造
     */
    private OkHttpTool() {
//        mHttpClient = new OkHttpClient();
        // 要配什么参数(如:超时)在这里配
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(20, TimeUnit.SECONDS);//默认10秒太少,上传手环数据时有超时的情况
        builder.writeTimeout(20, TimeUnit.SECONDS);
        mHttpClient = builder.build();
    }

    /**
     * 获取单例实例
     */
    public static OkHttpTool getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpTool();
        }
        return mInstance;
    }

    /**
     * 提交数据格式_JSON
     */
    private final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 网络数据异步请求,直接以格式化的Json字符串传递,POST请求
     *
     * @param path     请求地址
     * @param json     格式化的Json字符
     * @param tag      请求的标志(取消请求时用到)
     * @param callBack 结果回调实例
     */
    public void doRequest(String path, String json, Object tag, HttpResult callBack) {
        Request.Builder builder = new Request.Builder().tag(tag);
        if (!TextUtils.isEmpty(mSession)) builder.addHeader("cookie", mSession);
        RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, json);
        builder.post(body);
        builder.url(path);
        httpRequest(builder.build(), callBack);
    }

    /**
     * 网络数据异步请求
     *
     * @param path     请求地址
     * @param params   参数Map
     * @param tag      请求的标志(取消请求时用到)
     * @param callBack 结果回调实例
     * @param isGet    true_get请求 false_post请求
     */
    public void doRequest(String path, Map<String, String> params, Object tag, HttpResult callBack, boolean isGet) {
        Request.Builder builder = new Request.Builder().tag(tag);
        if (!TextUtils.isEmpty(mSession)) builder.addHeader("cookie", mSession);
        if (isGet) {
            try {
                path = path + spliceParamsByGet(params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject json = new JSONObject(params);
            RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, json.toString());
            builder.post(body);
        }
        builder.url(path);
        httpRequest(builder.build(), callBack);
    }

    /**
     * 拼接参数_Get方式
     *
     * @param params 参数集合
     * @return 字符串
     * @throws UnsupportedEncodingException 转码出错异常
     */
    private String spliceParamsByGet(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            builder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));// 这里一定要,不然提交的会是乱码
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);// 去掉最后一个'&'
        }
        return builder.toString();
    }

    /**
     * 执行OkHttp请求
     *
     * @param request  OkHttp的请求参数
     * @param callBack 请求结果回调实例
     */
    private void httpRequest(Request request, final HttpResult callBack) {
        Call call = mHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (e instanceof SocketException) {
                    Log.d("bobo", "call.cancel()");
                } else {
                    callBack.onResult(null);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                callBack.onResult(body == null ? null : body.string());
                handlerSession(response);
            }
        });
    }

    /**
     * 处理session
     *
     * @param response 请求
     */
    private void handlerSession(Response response) {
        List<String> cookies = response.headers().values("Set-Cookie");
        if (cookies != null && !cookies.isEmpty()) {
            String session = cookies.get(0);
            mSession = session.substring(0, session.indexOf(";"));
        }
    }

    /**
     * 根据标志取消请求
     *
     * @param tag 标志_可以是Activity或Fragment
     */
    public void cancelCall(Object tag) {
        Dispatcher dispatcher = mHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * Http请求结果回调接口
     */
    public interface HttpResult {
        /**
         * 请求结果
         */
        void onResult(String result);
    }

}
