package com.example.bozhilun.android.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 计算BMI指数
 * Created by Admin
 * Date 2020/1/15
 */
public class ComputeBMIActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.bmiHeightEdit)
    EditText bmiHeightEdit;
    @BindView(R.id.bmiWeightEdit)
    EditText bmiWeightEdit;

    @BindView(R.id.computeBmiResult)
    TextView computeBmiResult;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compute_bmi_layout);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("BMI");


    }

    @OnClick({R.id.commentB30BackImg, R.id.computeBmiBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.computeBmiBtn:
                String heightStr = bmiHeightEdit.getText().toString();
                String weightStr = bmiWeightEdit.getText().toString();
                if (WatchUtils.isEmpty(heightStr) || WatchUtils.isEmpty(weightStr))
                    return;
                computeBmi(heightStr, weightStr);
                break;
        }
    }

    private void computeBmi(String heightStr, String weightStr) {
        try {
            float height = Float.valueOf(heightStr.trim());
            float weight = Float.valueOf(weightStr.trim());

            float resultValue = weight / (height * height);

            computeBmiResult.setText(resultValue + "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
