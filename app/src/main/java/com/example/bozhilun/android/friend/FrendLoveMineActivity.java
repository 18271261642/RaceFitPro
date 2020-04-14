package com.example.bozhilun.android.friend;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.LoveMeBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.adapters.CommonRecyclerAdapter;
import com.example.bozhilun.android.w30s.adapters.MyViewHolder;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrendLoveMineActivity
        extends WatchBaseActivity implements RequestView {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.toolbar_normal)
    Toolbar toolbarNormal;
    @BindView(R.id.rec_list_loveme)
    RecyclerView recListLoveme;
    private RequestPressent requestPressent;

    private List<LoveMeBean.FriendListBean> list;
    private MyAdapter myAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_loveme_activity);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoveMe();
    }

    private void init() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        barTitles.setText(getResources().getString(R.string.string_love_mine_frend));
        toolbarNormal.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        toolbarNormal.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });//右边返回按钮点击事件

        recListLoveme.setLayoutManager(new GridLayoutManager(this, 3));
        list = new ArrayList<>();
        myAdapter = new MyAdapter(FrendLoveMineActivity.this,list,R.layout.loveme_item);
        recListLoveme.setAdapter(myAdapter);


    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if(WatchUtils.isEmpty(object+"") || object.toString().contains("<html>") )
            return;
        try {
            list.clear();
            JSONObject jsonObject = new JSONObject(object.toString());
            if(jsonObject.getInt("code") == 200){
                String data = jsonObject.getString("data");
                if(!WatchUtils.isEmpty(data) && !data.equals("[]")){
                    List<LoveMeBean.FriendListBean> tmpList = new Gson().fromJson(data,
                            new TypeToken<List<LoveMeBean.FriendListBean>>(){}.getType());
                    list.addAll(tmpList);
                    myAdapter.notifyDataSetChanged();
                }
            }else{
                myAdapter.notifyDataSetChanged();
                ToastUtil.showToast(FrendLoveMineActivity.this,jsonObject.getString("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    /**
     * 返回今日已赞我的好友
     */
    void getLoveMe() {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.TodayLoveMe;
        JSONObject sleepJson = new JSONObject();
        try {
            String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
            if (!TextUtils.isEmpty(userId)) sleepJson.put("userId", userId);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendLoveMineActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * rec---适配器
     */
    class MyAdapter extends CommonRecyclerAdapter<LoveMeBean.FriendListBean> {

        public MyAdapter(Context context, List<LoveMeBean.FriendListBean> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(MyViewHolder holder, final LoveMeBean.FriendListBean item) {
            holder.setImageGlidNo(R.id.image_user, FrendLoveMineActivity.this);
            if (!WatchUtils.isEmpty(item.getImage())) {
                holder.setImageGlid(R.id.image_user, item.getImage(), FrendLoveMineActivity.this);
            }
            if (!WatchUtils.isEmpty(item.getNickname()))
                holder.setText(R.id.text_names, item.getNickname());

//            holder.setText(R.id.itemHeartDetailDateTv, item.getRtc().substring(11, 16));
//            holder.setText(R.id.itemHeartDetailValueTv, item.getHeartRate() + "");
        }
    }


}
