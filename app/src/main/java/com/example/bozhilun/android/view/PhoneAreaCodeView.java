package com.example.bozhilun.android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.PhoneAdapter;
import com.example.bozhilun.android.bean.AreCodeBean;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 * 国家手机号的区号
 * Created by Admin
 * Date 2019/6/28
 */
public class PhoneAreaCodeView extends AlertDialog implements RequestView,AdapterView.OnItemClickListener {

    private static final String TAG = "PhoneAreaCodeView";

    private RequestPressent requestPressent;
    private ListView listView;
    private Context mContext;

    private List<AreCodeBean> list;
    private PhoneAdapter phoneAdapter;

    private Dialog dialog;


    public PhoneAreaClickListener phoneAreaClickListener;

    public void setPhoneAreaClickListener(PhoneAreaClickListener phoneAreaClickListener) {
        this.phoneAreaClickListener = phoneAreaClickListener;
    }

    public PhoneAreaCodeView(Context context) {
        super(context);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_area_code_view_layout);


        initViews();

        getAreaData();
    }

    private void getAreaData() {
        if(requestPressent != null){
            String url = "http://api.berace.com.cn/areaCode.json";
            requestPressent.getRequestJSONObject(0x01,url,mContext,1);
        }

    }

    private void initViews() {
        listView = findViewById(R.id.phoneAreaCodeListView);
        list = new ArrayList<>();
        phoneAdapter = new PhoneAdapter(list,mContext);
        listView.setAdapter(phoneAdapter);
        if(requestPressent == null)
            requestPressent = new RequestPressent();
        requestPressent.attach(this);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void showLoadDialog(int what) {
        if(dialog == null){
            dialog = new Dialog(mContext);
        }
        dialog.show();
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(dialog != null)
            dialog.dismiss();
        if(object == null)
            return;
        try {
            List<AreCodeBean> tempList = new Gson().fromJson(object.toString(),
                    new TypeToken<List<AreCodeBean>>(){}.getType());
            list.addAll(tempList);
            phoneAdapter.notifyDataSetChanged();

        }catch (Exception e){
          e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        if(dialog != null)
            dialog.dismiss();
        ToastUtil.showToast(mContext,e.getMessage()+"");
    }

    @Override
    public void closeLoadDialog(int what) {
        if(dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(phoneAreaClickListener != null){
            phoneAreaClickListener.chooseAreaCode(list.get(position));
        }
    }


    public interface PhoneAreaClickListener{
        void chooseAreaCode(AreCodeBean areCodeBean);
    }


//    public void showLoadingDialog(String msg) {
//        if (!isFinishing()) {
//            if (dialog == null) {
//                dialog = new Dialog(mContext, R.style.CustomProgressDialog);
//                dialog.setContentView(R.layout.pro_dialog_layout_view);
//                TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
//                tv.setText(msg + "");
//                dialog.setCancelable(true);
//                dialog.show();
//            } else {
//                dialog.setContentView(R.layout.pro_dialog_layout_view);
//                dialog.setCancelable(true);
//                TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
//                tv.setText(msg + "");
//                dialog.show();
//            }
//
//        }
//    }
    }
