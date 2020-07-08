package com.example.bozhilun.android.friend;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.views.MyApplyRecordListener;
import com.example.bozhilun.android.friend.views.NewFriendApplyListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.friend.bean.FriendApplyBean;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 查看好友申请和自己的申请记录
 * Created by Admin
 * Date 2019/7/11
 */
public class NewFriendApplyActivity extends WatchBaseActivity implements RequestView, NewFriendApplyListener,MyApplyRecordListener  {

    private static final String TAG = "NewFriendApplyActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.friendApplyRecyclerView)
    RecyclerView friendApplyRecyclerView;
    @BindView(R.id.friendMyApplyRecordRecyclerView)
    RecyclerView friendMyApplyRecordRecyclerView;


    //好友申请的数据，新的朋友申请
    private List<FriendApplyBean.DataBean> applyList;
    private FriendApplyAdapter friendApplyAdapter;

    //好友申请记录
    private List<FriendApplyBean.DataBean> myApplyList;
    private FriendMyApplyAdapter myApplyAdapter;


    private RequestPressent requestPressent;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_apply_layout);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        myApplyList.clear();
        applyList.clear();
        String userId = (String) SharedPreferencesUtils.readObject(NewFriendApplyActivity.this,Commont.USER_ID_DATA);
        if(WatchUtils.isEmpty(userId))
            return;
        //申请的好友
        findNewApplyFrend(userId);
        //申请记录
        findMineApplyHistory(userId);
    }


    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_frend_apply));
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        //好友申请
        LinearLayoutManager lin1 = new LinearLayoutManager(this);
        lin1.setOrientation(LinearLayoutManager.VERTICAL);
        friendApplyRecyclerView.setLayoutManager(lin1);
        applyList = new ArrayList<>();
        friendApplyAdapter = new FriendApplyAdapter(applyList);
        friendApplyRecyclerView.setAdapter(friendApplyAdapter);
        friendApplyAdapter.setNewFriendApplyListener(this);


        LinearLayoutManager lin2 = new LinearLayoutManager(this);
        lin2.setOrientation(LinearLayoutManager.VERTICAL);
        friendMyApplyRecordRecyclerView.setLayoutManager(lin2);
        myApplyList = new ArrayList<>();
        myApplyAdapter = new FriendMyApplyAdapter(myApplyList);
        friendMyApplyRecordRecyclerView.setAdapter(myApplyAdapter);
        myApplyAdapter.setMyApplyRecordListener(this);
    }


    /**
     * 查找朋友的新申请
     *
     * @param userId
     */
    public void findNewApplyFrend(String userId) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FindNewFrend;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            Log.e(TAG, " 查找朋友的新申请参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, NewFriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 查找我的申请历史
     *
     * @param userId
     */
    public void findMineApplyHistory(String userId) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FindApplyHistory;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            Log.e(TAG, " 查找我的申请历史参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, sleepUrl, NewFriendApplyActivity.this, sleepJson.toString(), 0);
        }
    }




    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        Log.e(TAG,"------what="+what+"--="+object.toString());
        switch (what){
            case 0x01:  //新好友的申请
                newFriendApplyData(object.toString());
                break;
            case 0x02:  //申请记录
                analysisMyApplyRecord(object.toString());
                break;
            case 0x03:  //同意或拒绝好友的申请
                agreeOrRefuseApply(object.toString());
                break;
            case 0x04:  //删除我的申请记录
                analysDetelApplyRecord(object.toString());
                break;
        }
    }




    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    //删除我的申请记录
    private void analysDetelApplyRecord(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            ToastUtil.showToast(NewFriendApplyActivity.this,jsonObject.getString("msg"));
            initData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //同意或者拒绝申请
    private void agreeOrRefuseApply(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            ToastUtil.showToast(NewFriendApplyActivity.this,jsonObject.getString("msg"));

            initData();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //我的申请记录
    private void analysisMyApplyRecord(String str) {
        myApplyList.clear();
        FriendApplyBean friendApplyBean = new Gson().fromJson(str,FriendApplyBean.class);
        if(friendApplyBean != null && friendApplyBean.getCode() == 200){
            List<FriendApplyBean.DataBean> tmpList = friendApplyBean.getData();
            myApplyList.addAll(tmpList);
            myApplyAdapter.notifyDataSetChanged();
        }else{
            myApplyList.clear();
            myApplyAdapter.notifyDataSetChanged();
            ToastUtil.showToast(this,friendApplyBean.getMsg()+"");
        }
    }



    //新好友的申请
    private void newFriendApplyData(String str){
        FriendApplyBean friendApplyBean = new Gson().fromJson(str,FriendApplyBean.class);
        if(friendApplyBean != null && friendApplyBean.getCode() == 200){
            List<FriendApplyBean.DataBean> tmpList = friendApplyBean.getData();
            applyList.addAll(tmpList);
            friendApplyAdapter.notifyDataSetChanged();
        }else{
            applyList.clear();
            friendApplyAdapter.notifyDataSetChanged();
            ToastUtil.showToast(this,friendApplyBean.getMsg()+"");
        }

    }

    /**
     * 删除我对好友的申请记录
     *
     * @param userId
     * @param applicant
     */
    public void deleteApplyFrend(String userId, String applicant) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.DeleteApplyFrend;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("applicant", applicant);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x04, sleepUrl, NewFriendApplyActivity.this, jsonObject.toString(), 0);
        }
    }

    //好友申请同意
    @Override
    public void agreeFriendApply(int postion) {
        if(applyList.isEmpty())
            return;
        friendApplyAgreeOrRefuse(applyList.get(postion).getUserid(),2);
    }

    //好友申请拒绝
    @Override
    public void refuseFriendApply(int position) {
        friendApplyAgreeOrRefuse(applyList.get(position).getUserid(),3);
    }

    //好友申请删除记录
    @Override
    public void deleteFriendApply(int positon) {

    }


    //我的申请记录，长按删除记录
    @Override
    public void deleteMyApplyRecord(final int positon) {
        new AlertDialog.Builder(NewFriendApplyActivity.this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.string_ok_delete_frend))
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String uId = (String) SharedPreferencesUtils.readObject(NewFriendApplyActivity.this,Commont.USER_ID_DATA);

                        deleteApplyFrend(uId,myApplyList.get(positon).getUserid());
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }



    /**
     * 同意或者驳回申请
     *
     * 2--通过；3--拒绝
     */
    public void friendApplyAgreeOrRefuse(String applicant, int status) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FindReturnApply;
        JSONObject josn = new JSONObject();
        try {
            josn.put("userId", SharedPreferencesUtils.readObject(NewFriendApplyActivity.this,Commont.USER_ID_DATA));
            josn.put("applicant", applicant);
            josn.put("status", status);
            Log.d(TAG, " 同意或者驳回申请参数--" + josn.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x03, sleepUrl, NewFriendApplyActivity.this, josn.toString(), 0);
        }
    }


    //好友申请的adapter，新的朋友申请
    class FriendApplyAdapter extends RecyclerView.Adapter<FriendApplyAdapter.FriendApplyViewHolder>{

        private NewFriendApplyListener newFriendApplyListener;

        public void setNewFriendApplyListener(NewFriendApplyListener newFriendApplyListener) {
            this.newFriendApplyListener = newFriendApplyListener;
        }

        private List<FriendApplyBean.DataBean> appList;

        public FriendApplyAdapter(List<FriendApplyBean.DataBean> appList) {
            this.appList = appList;
        }

        @NonNull
        @Override
        public FriendApplyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(NewFriendApplyActivity.this).inflate(R.layout.frend_apply_find_item,viewGroup,false);

            return new FriendApplyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FriendApplyViewHolder holder, int i) {
            FriendApplyBean.DataBean db = appList.get(i);
            holder.userNames.setText(db.getNickname()+"");
            //性别
            if (db.getSex().equals("M") || db.getSex().equals("男")) {
                holder.frendSex.setText(getResources().getString(R.string.sex_nan));
            } else {
                holder.frendSex.setText(getResources().getString(R.string.sex_nv));
            }

            //年龄
            int ageFromBirthTime = WatchUtils.getAgeFromBirthTime(db.getBirthday());
            holder.frendBirthday.setText("" + ageFromBirthTime);
            //头像
            Glide.with(MyApp.getInstance()).load( db.getImage())
                    .into(holder.circleImageView);

            //同意申请
            holder.btnAddFrend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newFriendApplyListener != null){
                        int positon = holder.getLayoutPosition();
                        newFriendApplyListener.agreeFriendApply(positon);
                    }
                }
            });

            //拒绝申请
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newFriendApplyListener != null){
                        int positon = holder.getLayoutPosition();
                        newFriendApplyListener.refuseFriendApply(positon);
                    }
                }
            });

            //长按删除记录
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(newFriendApplyListener != null){
                        int positon = holder.getLayoutPosition();
                        newFriendApplyListener.deleteFriendApply(positon);
                    }

                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return appList.size();
        }

        class FriendApplyViewHolder extends RecyclerView.ViewHolder{
            // ---- 用户名---排名---好友步数---点赞以及点赞计数
            TextView userNames, rankNuber, frendSex, frendBirthday;
            Button btnAddFrend, btnDelete;
            CircleImageView circleImageView;
            CheckBox mine_detailed_information;


            public FriendApplyViewHolder(@NonNull View itemView) {
                super(itemView);
                userNames = itemView.findViewById(R.id.user_names);
                rankNuber = itemView.findViewById(R.id.text_rank_nuber);
                frendSex = itemView.findViewById(R.id.frend_sex);
                btnAddFrend = itemView.findViewById(R.id.btn_find);
                btnDelete = itemView.findViewById(R.id.btn_delete);
                frendBirthday = itemView.findViewById(R.id.frend_birthday);
                mine_detailed_information = itemView.findViewById(R.id.mine_detailed_information);
                circleImageView = itemView.findViewById(R.id.imahe_list_heard);
            }
        }



    }

    //好友申请，我的申请记录
    class FriendMyApplyAdapter extends RecyclerView.Adapter<FriendMyApplyAdapter.MyApplyViewHolder>{

        private MyApplyRecordListener myApplyRecordListener;

        public void setMyApplyRecordListener(MyApplyRecordListener myApplyRecordListener) {
            this.myApplyRecordListener = myApplyRecordListener;
        }

        private List<FriendApplyBean.DataBean> myLt;

        public FriendMyApplyAdapter(List<FriendApplyBean.DataBean> myLt) {
            this.myLt = myLt;
        }


        @NonNull
        @Override
        public MyApplyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(NewFriendApplyActivity.this).inflate(R.layout.apply_frend_find_item,viewGroup,false);
            return new MyApplyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyApplyViewHolder holder, int i) {
            FriendApplyBean.DataBean myApplyBean = myLt.get(i);

            if (!TextUtils.isEmpty(myApplyBean.getNickname())) {
                holder.userNames.setText(myApplyBean.getNickname());
            } else if (!TextUtils.isEmpty(myApplyBean.getPhone()) && holder.userNames != null) {
                holder.userNames.setText(myApplyBean.getPhone());
            }
            if (!TextUtils.isEmpty(myApplyBean.getImage()) && holder.circleImageView != null) {
                Glide.with(MyApp.getInstance()).load( myApplyBean.getImage())
                        .into(holder.circleImageView);
            } else {
                Glide.with(MyApp.getInstance()).load(R.mipmap.bg_img).into(holder.circleImageView);
            }

            switch (myApplyBean.getFriendStatus()) {
                case 1:
                    //待验证
                    holder.frend_friend_status.setText(MyApp.getInstance().getResources().getString(R.string.string_wite_verified));
                    break;
                case 2:
                    //已通过
                    holder.frend_friend_status.setText(MyApp.getInstance().getResources().getString(R.string.string_wite_passed));
                    break;
                case 3:
                    //已拒绝
                    holder.frend_friend_status.setText(MyApp.getInstance().getResources().getString(R.string.string_wite_refused));
                    break;
            }
            if (!TextUtils.isEmpty(myApplyBean.getSex())) {
                if (myApplyBean.getSex().equals("M") || myApplyBean.getSex().equals("男")) {
                    holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nan));
                } else {
                    holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nv));
                }
            } else {
                //性别未知
                holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.string_sex_null));
            }
            if (!TextUtils.isEmpty(myApplyBean.getBirthday())) {
                int ageFromBirthTime = WatchUtils.getAgeFromBirthTime(myApplyBean.getBirthday());
                holder.frendBirthday.setText("" + ageFromBirthTime);
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(myApplyRecordListener != null){
                        int position = holder.getLayoutPosition();
                        myApplyRecordListener.deleteMyApplyRecord(position);
                    }

                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return myLt.size();
        }

        class MyApplyViewHolder extends RecyclerView.ViewHolder{

            TextView userNames, rankNuber, frendSex, frendBirthday, frend_friend_status;
            CircleImageView circleImageView;
            LinearLayout find_frend_item;

            public MyApplyViewHolder(@NonNull View itemView) {
                super(itemView);
                find_frend_item = itemView.findViewById(R.id.find_frend_item);
                userNames = itemView.findViewById(R.id.user_names);
                rankNuber = itemView.findViewById(R.id.text_rank_nuber);
                frendSex = itemView.findViewById(R.id.frend_sex);
                frendBirthday = itemView.findViewById(R.id.frend_birthday);
                frend_friend_status = itemView.findViewById(R.id.frend_friend_status);
                circleImageView = itemView.findViewById(R.id.imahe_list_heard);
            }
        }
    }


}
