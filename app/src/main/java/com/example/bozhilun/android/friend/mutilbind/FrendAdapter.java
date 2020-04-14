package com.example.bozhilun.android.friend.mutilbind;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bzlmaps.CommomDialog;
import com.example.bozhilun.android.friend.bean.FriendMyFriendListBean;
import com.example.bozhilun.android.friend.bean.MyFrendListBean;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FrendAdapter extends RecyclerView.Adapter<FrendAdapter.ViewHodler> {
    private Context context;
    List<FriendMyFriendListBean> myfriends;

    public FrendAdapter(Context context, List<FriendMyFriendListBean> myfriends) {
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
            final FriendMyFriendListBean myfriendsBean = myfriends.get(position);
            if (myfriendsBean != null) {
                String nickName = myfriendsBean.getNickname().trim();
                String phone = myfriendsBean.getPhone().trim();
                //昵称
                if (!TextUtils.isEmpty(nickName) && holder.userNames != null) {
                    holder.userNames.setText(nickName);
                } else if (!TextUtils.isEmpty(phone) && holder.userNames != null) {
                    holder.userNames.setText(phone);
                }
                //头像
                if (!TextUtils.isEmpty( myfriendsBean.getImage()) && holder.circleImageView != null) {
                    //头像
                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(context).load(myfriendsBean.getImage())
                            .apply(mRequestOptions).into(holder.circleImageView);    //头像

                } else {
                    //头像
                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(context).load(R.mipmap.bg_img)
                            .apply(mRequestOptions).into(holder.circleImageView);    //头像
                }
                holder.frendSteps.setText(context.getResources().getString(R.string.step) + ":" + myfriendsBean.getStepNumber());//步数
                holder.rankNuber.setText(String.valueOf(position+1));//排名
                holder.zan_count.setText(String.valueOf(myfriendsBean.getTodayThumbs()));//被赞次数


                int level = myfriendsBean.getLevel();
                if(level == 10){
                    holder.statusImg.setImageResource(R.mipmap.icon_star_special);
                }else if(level == 5){
                    holder.statusImg.setImageResource(R.mipmap.icon_star_star);
                }else{
                    holder.statusImg.setImageResource(R.mipmap.icon_empty_view);
                }


                int isThumbs = myfriendsBean.getIsThumbs();
                if (isThumbs == 0) {
                    holder.zanOclick.setEnabled(true);
                    holder.image_tautas.setBackgroundResource(R.mipmap.ic_on_like);
                } else {
                    holder.zanOclick.setEnabled(false);
                    holder.image_tautas.setBackgroundResource(R.mipmap.ic_un_like);
                }
                holder.viewst_view.setVisibility(View.GONE);
                holder.zanOclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        mOnItemListenter.ItemLoveOnClick(view, myfriendsBean.getUserid());
                    }
                });
                //item点击
                holder.line_onclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemListenter.ItemOnClick(view, myfriendsBean.getUserid(), myfriendsBean.getStepNumber(), myfriendsBean.getHeight(), position,myfriendsBean.getMac());
                    }
                });
                //item长按
                holder.line_onclick.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {

                        final String userName = TextUtils.isEmpty(myfriendsBean.getNickname()) ? myfriendsBean.getPhone() : myfriendsBean.getNickname();
                        new CommomDialog(context, R.style.dialog,
                                MyApp.getInstance().getResources().getString(R.string.string_ok_delete_frend)
                                        + userName + "？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    mOnItemListenter.ItemOnLongClick(view, myfriendsBean.getUserid());
                                    //myfriends.remove(position);
                                    //notifyDataSetChanged();
                                }
                                dialog.dismiss();
                            }
                        }).setTitle(MyApp.getInstance().getResources().getString(R.string.string_delete_frend)).show();

                        return false;
                    }
                });
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
        LinearLayout line_onclick, zanOclick,line_views;
        View viewst_view;

        ImageView statusImg;
        ViewHodler(View itemView) {
            super(itemView);

            line_views= itemView.findViewById(R.id.line_views);
            viewst_view = itemView.findViewById(R.id.st_view);
            userNames = itemView.findViewById(R.id.user_names);
            rankNuber = itemView.findViewById(R.id.text_rank_nuber);
            frendSteps = itemView.findViewById(R.id.frend_steps);
            zan_count = itemView.findViewById(R.id.zan_cont);
            zanOclick = itemView.findViewById(R.id.love_zan);
            image_tautas = itemView.findViewById(R.id.image_tautas);
            circleImageView = itemView.findViewById(R.id.imahe_list_heard);
            line_onclick = itemView.findViewById(R.id.line_onclick);
            statusImg = itemView.findViewById(R.id.itemFriendStatusImg);

        }
    }


    private OnItemListenter mOnItemListenter;

    public void setmOnItemListenter(OnItemListenter mOnItemListenter) {
        this.mOnItemListenter = mOnItemListenter;
    }

    public interface OnItemListenter {
        void ItemLoveOnClick(View view, String applicant);

        void ItemOnClick(View view, String applicant, int stepNumber, String frendHeight, int postion,String bleMac);

        void ItemOnClickMine(int postion);

        void ItemOnLongClick(View view, String applicant);
    }
}
