package com.example.bozhilun.android.friend.mutilbind;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.NewFrendApplyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 新的朋友申请
 */
public class NewFrendApplyDataBind extends ItemViewBinder<NewFrendApplyBean.ApplyListBean, NewFrendApplyDataBind.ViewHodler> implements View.OnClickListener {
    @NonNull
    @Override
    protected ViewHodler onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.frend_apply_find_item, null);
        return new ViewHodler(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHodler holder, @NonNull NewFrendApplyBean.ApplyListBean item) {

        frendUserID = item.getUserId();
        if (!TextUtils.isEmpty(item.getNickName().trim()) && holder.userNames != null) {
            holder.userNames.setText(item.getNickName().trim());
        } else if (!TextUtils.isEmpty(item.getPhone()) && holder.userNames != null) {
            holder.userNames.setText(item.getPhone());
        }
        if (!TextUtils.isEmpty((String) item.getImage()) && holder.circleImageView != null) {
            Glide.with(MyApp.getInstance()).load((String) item.getImage())
                    .into(holder.circleImageView);
        } else {
            Glide.with(MyApp.getInstance()).load(R.mipmap.bg_img).into(holder.circleImageView);
        }
        if (!TextUtils.isEmpty(item.getSex())) {
            if (item.getSex().equals("M") || item.getSex().equals("男")) {
                holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nan));
            } else {
                holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nv));
            }
        } else {
            holder.frendSex.setText("");
        }
        if (!TextUtils.isEmpty(item.getBirthday())) {
            int ageFromBirthTime = WatchUtils.getAgeFromBirthTime(item.getBirthday());
            holder.frendBirthday.setText("" + ageFromBirthTime);
        }
        holder.btnAddFrend.setOnClickListener(this);
        holder.btnDelete.setOnClickListener(this);

        checked = holder.mine_detailed_information.isChecked();
    }

    boolean checked = true;
    private String frendUserID = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find://同意添加
                if (!TextUtils.isEmpty(frendUserID)){
                    mButtonOnClickLister.OnButtonOnClickLister(frendUserID, 2,checked);
                }
                break;
            case R.id.btn_delete://拒绝
                if (!TextUtils.isEmpty(frendUserID)){
                    mButtonOnClickLister.OnButtonOnClickLister(frendUserID, 3, checked);
                }
                break;
        }
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        // ---- 用户名---排名---好友步数---点赞以及点赞计数
        TextView userNames, rankNuber, frendSex, frendBirthday;
        Button btnAddFrend, btnDelete;
        CircleImageView circleImageView;
        CheckBox mine_detailed_information;

        public ViewHodler(View itemView) {
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


    private ButtonOnClickLister mButtonOnClickLister;

    public void setmButtonOnClickLister(ButtonOnClickLister mButtonOnClickLister) {
        this.mButtonOnClickLister = mButtonOnClickLister;
    }

    public interface ButtonOnClickLister {
        void OnButtonOnClickLister(String frendUserId, int isStute, boolean checked);
    }
}
