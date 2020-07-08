package com.example.bozhilun.android.friend.mutilbind;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.TodayRankBean;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TodayRankAdapter extends RecyclerView.Adapter<TodayRankAdapter.ViewHodler> {
    private Context context;
    List<TodayRankBean.RankListBean> myfriends;

    public TodayRankAdapter(Context context, List<TodayRankBean.RankListBean> myfriends) {
        this.context = context;
        this.myfriends = myfriends;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.frend_rankings_list_item, parent, false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHodler holder, final int position) {
        try {
            final TodayRankBean.RankListBean myfriendsBean = myfriends.get(position);
            if (myfriendsBean != null) {
                String nickName = myfriendsBean.getUserName().trim();
                //昵称
                if (!TextUtils.isEmpty(nickName) && holder.userNames != null) {
                    holder.userNames.setText(nickName);
                }
                //头像
                Glide.with(context).load(myfriendsBean.getImg())
                        .error(R.mipmap.bg_img)
                        .into(holder.circleImageView);
                holder.frendSteps.setText(context.getResources().getString(R.string.step) + ":" + String.valueOf(myfriendsBean.getStepNumber()));//步数
                holder.rankNuber.setText(String.valueOf(position + 1));//排名
                holder.zanOclick.setVisibility(View.GONE);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return myfriends.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        // ---- 用户名---排名---好友步数---点赞以及点赞计数
        TextView userNames, rankNuber, frendSteps, zan_count;
        CircleImageView circleImageView;
        ImageView image_tautas;
        LinearLayout line_onclick, zanOclick;

        ViewHodler(View itemView) {
            super(itemView);

            userNames = itemView.findViewById(R.id.user_names);
            rankNuber = itemView.findViewById(R.id.text_rank_nuber);
            frendSteps = itemView.findViewById(R.id.frend_steps);
            zan_count = itemView.findViewById(R.id.zan_cont);
            zanOclick = itemView.findViewById(R.id.love_zan);
            image_tautas = itemView.findViewById(R.id.image_tautas);
            circleImageView = itemView.findViewById(R.id.imahe_list_heard);
            line_onclick = itemView.findViewById(R.id.line_onclick);

        }
    }
}