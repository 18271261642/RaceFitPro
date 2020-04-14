package com.example.bozhilun.android.siswatch.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.example.bozhilun.android.siswatch.WatchBaseActivity;

/**
 * Created by Administrator on 2017/9/28.
 */

/**
 * 更新的页面
 */
public class UpdateWebViewActivity extends WatchBaseActivity {

    String yybUrl = "https://a.app.qq.com/o/simple.jsp?pkgname" +
            "=com.example.bozhilun.android&channel=0002160650432d595942&fromcase=60001";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wb = new WebView(this);
        wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wb.getSettings().setJavaScriptEnabled(true);
       // String url = "http://app.qq.com/#id=detail&appid=1105653402";
        String url = getIntent().getStringExtra("updateUrl");
        if(!WatchUtils.isEmpty(url)){
            wb.loadUrl(url);
        }else{
            wb.loadUrl(yybUrl);   //应用宝更新地址
        }
        setContentView(wb);
    }

}
