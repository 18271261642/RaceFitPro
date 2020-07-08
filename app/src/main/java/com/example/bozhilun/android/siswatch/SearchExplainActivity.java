package com.example.bozhilun.android.siswatch;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.MessageHelpActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.view.CustomerWebView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/3.
 */

public class SearchExplainActivity extends WatchBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_explainWV)
    CustomerWebView searchExplainWV;
    WebSettings webSettings;
    String url = "";
    @BindView(R.id.moreHelpTv)
    TextView moreHelpTv;

    @BindView(R.id.handLocalTv)
    TextView handLocalTv;

    boolean isGet = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0x01);
            getLocalPermiss();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_explain);
        ButterKnife.bind(this);

        initViews();

        Locale locales = getResources().getConfiguration().locale;
        String country = locales.getCountry();
        if (!WatchUtils.isEmpty(country)) {
            if (country.equals("CN")) {
                url = "file:///android_asset/search_explain_zh.html";
            } else {
                url = "file:///android_asset/search_explain_en.html";
            }
        } else {
            url = "file:///android_asset/search_explain_en.html";
        }
        searchExplainWV.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }
        });
        searchExplainWV.loadUrl(url);


        getLocalPermiss();

    }

    private void getLocalPermiss() {
        if(!AndPermission.hasPermissions(this, Permission.ACCESS_FINE_LOCATION)){
            isGet = false;
            handLocalTv.setText("点击获取");
        }else{
            isGet = true;
            handLocalTv.setText("已获取");
        }
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.help));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webSettings = searchExplainWV.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);

        moreHelpTv.setText(getResources().getString(R.string.abour)+">>");

    }

    @OnClick({R.id.moreHelpTv,R.id.handLocalTv})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.moreHelpTv:
                startActivity(MessageHelpActivity.class);
                break;
            case R.id.handLocalTv:
                requsetPermiss();
                break;

        }

    }

    private void requsetPermiss() {
        if(isGet){
         AndPermission.with(this)
                 .runtime().setting().start(1001);
            return;
        }
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        handler.sendEmptyMessage(0x01);
                    }
                }).start();
    }
}
