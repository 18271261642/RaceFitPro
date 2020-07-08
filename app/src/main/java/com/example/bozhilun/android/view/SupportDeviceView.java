package com.example.bozhilun.android.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.view.banner.SupportDeviceBean;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin
 * Date 2020/4/27
 */
public class SupportDeviceView extends WatchBaseActivity implements RequestView {

    private RecyclerView supportDeviceRecyclerView;
    private Context mContext;

    private List<SupportDeviceBean> supportList;
    private SupportDeviceAdapter adapter;

    private RequestPressent requestPressent;


//    public SupportDeviceView(@NonNull Context context) {
//        super(context);
//        this.mContext = context;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_device_view);
        
        
        initViews();

        getDeviceUrl();
        
    }

    private void getDeviceUrl() {

        String url = "http://122.51.27.32/data/support.json";
        if(requestPressent == null)
            requestPressent = new RequestPressent();
        requestPressent.attach(this);
        requestPressent.getRequestJSONObject(0x01,url,this,0);
//        OkHttpTool.getInstance().doRequest(url, "", null, new OkHttpTool.HttpResult() {
//            @Override
//            public void onResult(String result) {
//                Log.e("Dialog","------result="+result);
//                analysisData(result);
//            }
//        });

    }

    private void analysisData(String result) {
        try {
            Log.e("Dialog","------result="+result);
            List<SupportDeviceBean> list = new Gson().fromJson(result,new TypeToken<List<SupportDeviceBean>>(){}.getType());
            if(list == null)
                return;
            supportList.clear();
            supportList.addAll(list);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initViews() {
        supportDeviceRecyclerView = findViewById(R.id.supportDeviceRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2);
        //gridLayoutManager.setSpanCount(2);
        supportDeviceRecyclerView.setLayoutManager(gridLayoutManager);

        supportList = new ArrayList<>();

        adapter = new SupportDeviceAdapter(supportList);
        supportDeviceRecyclerView.setAdapter(adapter);

    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(what == 0x01){
            if(object == null)
                return;
            analysisData((String) object);
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }


    private class SupportDeviceAdapter extends RecyclerView.Adapter<SupportDeviceAdapter.SuppViewHolder> {


        private List<SupportDeviceBean> beanList;

        public SupportDeviceAdapter(List<SupportDeviceBean> beanList) {
            this.beanList = beanList;
        }

        @NonNull
        @Override
        public SuppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(SupportDeviceView.this).inflate(R.layout.item_support_device,viewGroup,false);
            SuppViewHolder holder = new SuppViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SuppViewHolder suppViewHolder, int i) {
            if(beanList.isEmpty())
                return;
            ImageView imageView = suppViewHolder.img;
            Glide.with(SupportDeviceView.this).load(beanList.get(i).getDeviceUrl()).into(imageView);
            suppViewHolder.nameTv.setText(beanList.get(i).getDeviceName());
        }

        @Override
        public int getItemCount() {
            return beanList.size();
        }

        class SuppViewHolder extends RecyclerView.ViewHolder{

            ImageView img;
            TextView nameTv;

            public SuppViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.itemSupportImg);
                nameTv = itemView.findViewById(R.id.itemSupportTvName);
            }
        }
    }

}
