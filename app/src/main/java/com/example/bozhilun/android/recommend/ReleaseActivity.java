package com.example.bozhilun.android.recommend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2020/4/10
 */
public class ReleaseActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.save_text)
    TextView saveText;
    @BindView(R.id.releaseRecyclerView)
    RecyclerView releaseRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_layout);
        ButterKnife.bind(this);


        initViews();


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        saveText.setVisibility(View.VISIBLE);
        saveText.setText("发表");

    }

    @OnClick({R.id.commentB30BackImg, R.id.save_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:

                break;
            case R.id.save_text:


                break;
        }
    }



    class ReleaseAdapter extends RecyclerView.Adapter<ReleaseAdapter.ReleaseViewHolder> {

        private List<String> imgList;

        public ReleaseAdapter(List<String> imgList) {
            this.imgList = imgList;
        }

        @NonNull
        @Override
        public ReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ReleaseViewHolder releaseViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ReleaseViewHolder extends RecyclerView.ViewHolder{



            public ReleaseViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
