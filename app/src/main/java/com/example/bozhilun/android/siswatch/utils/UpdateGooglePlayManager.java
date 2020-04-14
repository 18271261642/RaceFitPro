package com.example.bozhilun.android.siswatch.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 跳转至GooglePlay
 * Created by Admin
 * Date 2019/8/26
 */
public class UpdateGooglePlayManager implements RequestView {

    private RequestPressent requestPressent;

    private AlertDialog.Builder alert;
    private Context context;


    public UpdateGooglePlayManager() {
    }



    public void checkForUpdate(Context mContext){
        this.context = mContext;
        if(requestPressent == null)
          requestPressent = new RequestPressent();
        requestPressent.attach(this);

        Map<String,String> maps = new HashMap<>();
        maps.put("appName","com.bozlun.bozhilun.android");
        maps.put("version",WatchUtils.getVersionCode(mContext)+"");
        Log.e("Google","-----maps="+maps.toString());
        requestPressent.getRequestJSONObject(0x01,Commont.FRIEND_BASE_URL + URLs.getvision,mContext,new Gson().toJson(maps),1);

    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, final int daystag) {
        Log.e("Google","-------what="+what+"---obj="+object.toString());
        if(object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(!jsonObject.has("code"))
                return;
            if(jsonObject.getInt("code") == 200){
                alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getResources().getString(R.string.prompt));
                alert.setMessage(context.getResources().getString(R.string.newversion));
                alert.setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        WatchUtils.gotoGooglePlay(context,"com.bozlun.bozhilun.android");
                    }
                }).setNegativeButton(context.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
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
}
