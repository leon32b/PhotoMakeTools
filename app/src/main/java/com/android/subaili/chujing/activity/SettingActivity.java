package com.android.subaili.chujing.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    private final static String WEIBO_UID = "1437934394";
    private final static String QQGROUP_KEY = "2lLTZv4vkKsHIGGdN5B7B9aLolkZZyxW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.toolbar_normal);
        toolbar.setOnClickListener(view -> finish());
    }

    public void onClearClick(View view) {
        Tools.deleteAllFilesOfDir(new File(Objects.requireNonNull(getExternalFilesDir(UtilData.VIDEO_PATH)).getAbsolutePath()));
        Tools.deleteAllFilesOfDir(new File(Objects.requireNonNull(getExternalFilesDir(UtilData.PREVIEW_PATH)).getAbsolutePath()));
        Tools.deleteAllFilesOfDir(new File(Objects.requireNonNull(getExternalFilesDir(UtilData.STICKER_PATH)).getAbsolutePath()));
        Toast.makeText(SettingActivity.this, getString(R.string.text_clean_success), Toast.LENGTH_SHORT).show();
    }

    public void onVersionClick(View view) {
    }

    public void onRemarkClick(View view) {
        try {
            String str = "market://details?id=com.android.subaili.gifmaketool";
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            startActivity(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.tip_openmarket_error), Toast.LENGTH_SHORT).show();
            String url = "https://appgallery1.huawei.com/#/";
            openLinkBySystem(url);
        }
    }

    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void onRoastClick(View view) {
        startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
    }

    public void onAboutClick(View view) {
        startActivity(new Intent(SettingActivity.this, AboutActivity.class));
    }

    public void onAgreementClick(View view) {
        startActivity(new Intent(SettingActivity.this, AgreementActivity.class));
    }

    public void onPrivacyClick(View view) {
        startActivity(new Intent(SettingActivity.this, PrivacyActivity.class));
    }

    public void btnWeiboClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        boolean weiboInstalled = isPackageInstalled();
        if (weiboInstalled) {
            intent.setData(Uri.parse("sinaweibo://userinfo?uid=" + WEIBO_UID));
        } else {
            intent.setData(Uri.parse("http://weibo.cn/qr/userinfo?uid=" + WEIBO_UID));
        }
        startActivity(intent);
    }

    public boolean isPackageInstalled() {
        PackageManager packageManager = getPackageManager();
        if (packageManager == null)
            return false;
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for(PackageInfo info : packageInfoList) {
            if (info.packageName.equals("com.sina.weibo"))
                return true;
        }
        return false;
    }

    public void btnQQgroup(View view) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"+QQGROUP_KEY));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(SettingActivity.this, getString(R.string.tip_qqgroup_error), Toast.LENGTH_SHORT).show();
        }
    }
}