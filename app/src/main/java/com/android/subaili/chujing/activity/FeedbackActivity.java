package com.android.subaili.chujing.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.utils.ADData;

public class FeedbackActivity extends AppCompatActivity {
    private final static String TXC_URL = "https://support.qq.com/product/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar_normal);
        toolbar.setOnClickListener(view -> finish());
        WebView webView = findViewById(R.id.wv_feedback);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String url = TXC_URL + ADData.TXC_APPID;
        webView.loadUrl(url);
    }
}