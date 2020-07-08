package com.example.bozhilun.android.view;


import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 隐私政策
 * Created by Administrator on 2018/7/12.
 */

public class PrivacyActivity extends WatchBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.privacyWb)
    CustomerWebView privacyWb;
    private WebView webView;

    private WebSettings webSettings;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_layout);
        ButterKnife.bind(this);

        initViews();

        Locale locales = getResources().getConfiguration().locale;

        String locale = Locale.getDefault().getLanguage();
        Log.e("YUYAN","---------locale--"+locale);
        if(!WatchUtils.isEmpty(locale)){
            if("zh".equals(locale)){    //中文
                if("TW".equals(locales.getCountry())){  //中文繁体
                    url = "file:///android_asset/privacy_tw.html";
                }else{  //中文简体
                    url = "file:///android_asset/htmlsrc/privacy_txt.html";
                }

            }else if("en".equals(locale)){  //英文
                url = "file:///android_asset/privacy_en.html";
            }
            else{  //英文
                url = "file:///android_asset/privacy_en.html";
            }
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }
        });
        webView.loadUrl(url);

    }

    private void initViews() {
        webView = findViewById(R.id.privacyWb);
        tvTitle.setText(getResources().getString(R.string.privacy));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
    }
}
