package com.example.bozhilun.android.b31.bpoxy;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.view.CustomerWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2018/12/29
 */
public class ShowSpo2DescActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.showSpo2DescWebView)
    CustomerWebView showSpo2DescWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_show_spo2desc);
        ButterKnife.bind(this);


        initViews();

        String desc = getIntent().getStringExtra("spo2_desc");
        //String url = "http://www.baidu.com";
        showSpo2DescWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        showSpo2DescWebView.loadUrl(getLoadUrl(desc));

    }


    private String getLoadUrl(String desc){
        String url = null;
        if(desc.equals("desc_osah")){
            url =  "file:///android_asset/htmlsrc/osahs_zh.html";
            commentB30TitleTv.setText("OSAH");
        }else if(desc.equals("desc_stop")){
            commentB30TitleTv.setText("呼吸暂停");
            url = "file:///android_asset/htmlsrc/breath_zh.html";
        }else if(desc.equals("desc_spo2")){
            commentB30TitleTv.setText("血氧");
            url = "file:///android_asset/htmlsrc/spo2_zh.html";
        }else if(desc.equals("desc_rate")){
            commentB30TitleTv.setText("呼吸率");
            url = "file:///android_asset/htmlsrc/respiratory_rate.html";
        }else if(desc.equals("desc_low_o2")){
            commentB30TitleTv.setText("低氧时间");
            url = "file:///android_asset/htmlsrc/low_o2_zh.html";
        }else if(desc.equals("desc_heart_load")){
            commentB30TitleTv.setText("心脏负荷");
            url = "file:///android_asset/htmlsrc/heart_load_zh.html";
        }else if(desc.equals("desc_sleep_activity")){
            commentB30TitleTv.setText("睡眠活动幅度");
            url = "file:///android_asset/htmlsrc/sleep_activity_zh.html";
        }
        else if(desc.equals("desc_spo2_comm")){     //呼吸暂停常识
            commentB30TitleTv.setText("呼吸暂停");
            url = "file:///android_asset/htmlsrc/breath_comm_knowle.html";
        }

        return url;

    }



    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        WebSettings webSettings = showSpo2DescWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);


    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }
}
