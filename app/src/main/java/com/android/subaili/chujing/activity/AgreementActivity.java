package com.android.subaili.chujing.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.subaili.chujing.R;


public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agreement);
        Toolbar toolbar = findViewById(R.id.toolbar_normal);
        toolbar.setOnClickListener(view -> finish());
        WebView webView = findViewById(R.id.wv_agreement);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/html/agreement.html");
    }
}