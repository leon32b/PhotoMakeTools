package com.android.subaili.chujing.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.utils.Tools;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar_normal);
        toolbar.setOnClickListener(view -> finish());
        TextView textView = findViewById(R.id.tv_version);
        textView.setText("Version：" + Tools.getVerName(AboutActivity.this));
    }
}