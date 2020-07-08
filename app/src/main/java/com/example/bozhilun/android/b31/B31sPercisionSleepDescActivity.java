package com.example.bozhilun.android.b31;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31s精准睡眠描述
 * Created by Admin
 * Date 2019/9/26
 */
public class B31sPercisionSleepDescActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.percisionSleepDescRangeTv)
    TextView percisionSleepDescRangeTv;
    @BindView(R.id.percisionSleepDescValueTv)
    TextView percisionSleepDescValueTv;
    @BindView(R.id.percisionSleepDescStatusTv)
    TextView percisionSleepDescStatusTv;

    @BindView(R.id.percisionSleepDescTv)
    TextView percisionSleepDescTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31s_percision_sleep_desc_layout);
        ButterKnife.bind(this);

        initViews();


        String sleepDesc = getIntent().getStringExtra("sleepDesc");
        String valueStr = getIntent().getStringExtra("valueStr");
        String status = getIntent().getStringExtra("status");

        String code = getIntent().getStringExtra("code");
        switchCode(code);

        commentB30TitleTv.setText(sleepDesc);
        percisionSleepDescStatusTv.setText(status);
        percisionSleepDescValueTv.setText(valueStr);

    }

    private void switchCode(String code) {
        String str1 = null;
        String str2 = null;
        int statusCode = Integer.valueOf(code);
        switch (statusCode){
            case 0:

                break;
            case 1:     //睡眠时长
                str1 = "7-9小时";
                str2 = "睡眠不足时，容易疲劳、嗜睡、精力不集中，而长时间懒床不利于得到高质量的睡眠，睡眠时间不是睡眠质量的唯一标准，因人而异，应以睡醒后精神状态是否良好为主。";
                break;
            case 2:         //苏醒
                str1 = "0%-1%";
                str2 = "睡眠中苏醒过多或过长，导致睡眠质量下降，经常睡眠中断是失眠的常见症状。";
                break;
            case 3:     //失眠
                str1 = "0%";
                str2 = "失眠是指患者对睡眠时间和质量不满足并影响日间社会功能的一种主观体验，常见病症是入睡困难、睡眠质量下降和睡眠时间减少，记忆力、注意力下降等。";
                break;
            case 4:         //快速眼动
                str1 = "10%-30%";
                str2 = "快速眼动期内，人在睡眠中眼球在眼皮下快速的来回移动，该阶段的大部人人群都在做梦，过短会易疲劳、烦躁、，但身体会自动延长之后几次睡眠的快速眼动期来缓解，过长表现为多梦。";
                break;
            case 5:         //深睡
                str1 = "≥21%";
                str2 = "深睡是缓解疲劳、恢复精力的关键阶段，深睡时间越长，睡眠质量越好。";
                break;
            case 6:         //浅睡
                str1 = "0%-59%";
                str2 = "浅睡是相对于深睡的非快速眼动期睡眠，介于深睡和苏醒之间，占比过高会引起睡眠质量下降。";
                break;

            case 7:     //苏醒次数
                str1 = "";
                break;
        }

        percisionSleepDescRangeTv.setText("参考范围："+str1);
        percisionSleepDescTv.setText(str2);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }
}
