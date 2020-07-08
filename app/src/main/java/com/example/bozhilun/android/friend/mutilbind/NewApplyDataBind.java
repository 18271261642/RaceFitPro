package com.example.bozhilun.android.friend.mutilbind;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.NewApplyFrendBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 我的申请记录
 */
public class NewApplyDataBind extends ItemViewBinder<NewApplyFrendBean.MyApplyBean, NewApplyDataBind.ViewHodler> {
    @NonNull
    @Override
    protected ViewHodler onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.apply_frend_find_item, null);
        return new ViewHodler(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHodler holder, @NonNull final NewApplyFrendBean.MyApplyBean item) {
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

        switch (item.getFriendStatus()) {
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
        if (!TextUtils.isEmpty(item.getSex())) {
            if (item.getSex().equals("M") || item.getSex().equals("男")) {
                holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nan));
            } else {
                holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.sex_nv));
            }
        } else {
            //性别未知
            holder.frendSex.setText(MyApp.getInstance().getResources().getString(R.string.string_sex_null));
        }
        if (!TextUtils.isEmpty(item.getBirthday())) {
            int ageFromBirthTime = WatchUtils.getAgeFromBirthTime(item.getBirthday());
            holder.frendBirthday.setText("" + ageFromBirthTime);
        }

        holder.find_frend_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final String userName = TextUtils.isEmpty(item.getNickName()) ? item.getPhone() : item.getNickName();
                mOnLongDeletelister.OnLongDeletelister(item.getUserId(),userName);
//                new CommomDialog(MyApp.getInstance(), R.style.dialog, "确定申请好友 " + userName + "记录？", new CommomDialog.OnCloseListener() {
//                    @Override
//                    public void onClick(Dialog dialog, boolean confirm) {
//                        if (confirm) {
//                            mOnLongDeletelister.OnLongDeletelister(item.getUserId());
//                        }
//                        dialog.dismiss();
//                    }
//                }).setTitle("删除好友").show();
                return false;
            }
        });
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        // ---- 用户名---排名---性别---年龄---申请状态
        TextView userNames, rankNuber, frendSex, frendBirthday, frend_friend_status;
        CircleImageView circleImageView;
        LinearLayout find_frend_item;

        public ViewHodler(View itemView) {
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

    private OnLongDeletelister mOnLongDeletelister;

    public void setmOnLongDeletelister(OnLongDeletelister mOnLongDeletelister) {
        this.mOnLongDeletelister = mOnLongDeletelister;
    }

    public interface OnLongDeletelister {
        void OnLongDeletelister(String applicant,String userName);
    }
}
