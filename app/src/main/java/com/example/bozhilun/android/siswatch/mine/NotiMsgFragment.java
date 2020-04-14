package com.example.bozhilun.android.siswatch.mine;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消息通知显示页面
 * Created by Admin
 * Date 2019/6/4
 */
public class NotiMsgFragment extends WatchBaseActivity implements RequestView {


    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.notiMsgRecyclerView)
    RecyclerView notiMsgRecyclerView;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;


    private List<NotiMsgBean> beanList;
    private NotiMsgAdapter notiMsgAdapter;

    private RequestPressent requestPressent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noti_msg_layout);
        ButterKnife.bind(this);

        initViews();

        getMsgData();


    }


    private void getMsgData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        String url = "http://47.90.83.197:9080/WatchWeb/notice/getNotice";
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(1, url, this, 1);

        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.msg_center));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notiMsgRecyclerView.setLayoutManager(linearLayoutManager);
        beanList = new ArrayList<>();
        notiMsgAdapter = new NotiMsgAdapter(beanList);
        notiMsgRecyclerView.setAdapter(notiMsgAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null)
            requestPressent.detach();
    }


    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        beanList.clear();
        notiMsgAdapter.notifyDataSetChanged();
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if (jsonObject.getInt("code") == 200) {
                String str = jsonObject.getString("data");
                List<NotiMsgBean> tmpList = new Gson().fromJson(str, new TypeToken<List<NotiMsgBean>>() {
                }.getType());
                if (tmpList != null && !tmpList.isEmpty()) {
                    beanList.addAll(tmpList);
                    notiMsgAdapter.notifyDataSetChanged();
                }

            } else {
                ToastUtil.showToast(NotiMsgFragment.this, jsonObject.getString("msg") + "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
        ToastUtil.showToast(NotiMsgFragment.this, e.getMessage() + "");
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    //adapter
    class NotiMsgAdapter extends RecyclerView.Adapter<NotiMsgAdapter.NotiViewHolder> {


        List<NotiMsgBean> list;

        public NotiMsgAdapter(List<NotiMsgBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NotiMsgFragment.this).inflate(R.layout.item_noti_msg_layout, parent, false);
            return new NotiViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
            holder.titleTv.setText(list.get(position).getTitle());
            holder.timeTv.setText(list.get(position).getDay());
            holder.contentTv.setText(list.get(position).getContent());
            String urlStr = list.get(position).getUrl();
            if (!WatchUtils.isEmpty(urlStr)) {
                holder.urlTv.setText(urlStr);
            } else {
                holder.urlTv.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class NotiViewHolder extends RecyclerView.ViewHolder {

            TextView titleTv, timeTv, contentTv, urlTv;

            public NotiViewHolder(View itemView) {
                super(itemView);
                titleTv = itemView.findViewById(R.id.itemNotiMsgTitleTv);
                timeTv = itemView.findViewById(R.id.itemNotiMsgTimeTv);
                contentTv = itemView.findViewById(R.id.itemNotiMsgContentTv);
                urlTv = itemView.findViewById(R.id.itemNotiMsgLinkTv);


            }
        }


    }


}
