package com.example.bozhilun.android.w30s.utils.httputils;

import android.content.Context;

import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import org.json.JSONException;


/**
 * Created by Administrator on 2018/4/3.
 */

public class RequestPressent {

    private RequestView requestView;

    private RequestModel requestModel;

    public RequestPressent() {
        requestModel = new RequestModel();
    }

    public void getRequestJSONObject(final int what, String url, Context mContext, String jsonMap, final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null)
                requestModel.getJSONObjectModelData(url, mContext, jsonMap, new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String s) {
                        if (!WatchUtils.isEmpty(s)) {
                            if (requestView != null) {
                                requestView.successData(what, s, days);
                            }
                        }
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
        }
    }


    public void getRequestJSONObject(final int what, String url, Context mContext ,final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null)
                requestModel.getJSONObjectModelData(url, mContext, new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String s) {
                        if (!WatchUtils.isEmpty(s)) {
                            if (requestView != null) {
                                requestView.successData(what, s, days);
                            }
                        }
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
        }
    }



    //绑定
    public void attach(RequestView requestView) {
        this.requestView = requestView;
    }

    //解除绑定
    public void detach() {
        if (requestView != null) {
            requestView = null;
        }
    }
}
