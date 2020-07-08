package com.example.bozhilun.android.recommend;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.view.CustomerWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2020/4/2
 */
public class BannerDetailActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.bannWebView)
    CustomerWebView bannWebView;
    @BindView(R.id.wbProgressBar)
    ProgressBar wbProgressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_banner_layout);
        ButterKnife.bind(this);

        initViews();

        String url = getIntent().getStringExtra("img_url");
        if (url == null)
            return;
        bannWebView.loadUrl(url);
        bannWebView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        bannWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                wbProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

    }


    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("详情");
        WebSettings webSettings = bannWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }


    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }
}
