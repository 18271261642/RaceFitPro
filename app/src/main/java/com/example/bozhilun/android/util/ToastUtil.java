package com.example.bozhilun.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.R;

/**
 * Created by thinkpad on 2017/1/5.
 */

public class ToastUtil {

    private static Toast mToast;

    //自定义的Toast
    private static Toast cusToast;

    /**
     * Toast 提示
     * @param mContext
     * @param msg
     */
    public static void showToast(Context mContext,String msg){
        if(mToast == null){
            mToast = Toast.makeText(mContext,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();

    }


    // 短时间显示Toast信息
    public static void showShort(Context context, String info) {
        showToast(context,info);
    }

    // 长时间显示Toast信息
    public static void showLong(Context context, String info) {
        if(mToast == null){
            mToast = Toast.makeText(context,info,Toast.LENGTH_LONG);
        }else{
            mToast.setText(info);
        }
        mToast.show();
    }

    public static void showCusToast(Activity activity,String msg){
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View toastView = layoutInflater.inflate(R.layout.cus_toast_layout,null);
        TextView cusToastTv = toastView.findViewById(R.id.cusToastTv);
        cusToastTv.setText(msg);
//        if(cusToast == null){
//            cusToast = new Toast(activity);
//        }
        Toast cusToast = new Toast(activity.getApplicationContext());
        cusToast.setGravity(Gravity.CENTER,12,20);
        cusToast.setDuration(2 * 1000);
        cusToast.setView(toastView);
        cusToast.show();
    }

}
