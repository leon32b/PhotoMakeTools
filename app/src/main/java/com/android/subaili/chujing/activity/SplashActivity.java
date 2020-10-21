package com.android.subaili.chujing.activity;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.config.TTAdManagerHolder;
import com.android.subaili.chujing.service.MediaStoreUpdateService;
import com.android.subaili.chujing.utils.ADData;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {
    private Context mContext;
    private boolean mForceGoMain;
    private boolean isStorageGranted;
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    private final static String TAG = "SplashActivity";
    private final static String ADSWITCH_URL = "http://121.41.47.196:8080/subaili/ADSwitch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        if (Tools.isSDKVersionM(mContext)) {
            isStorageGranted = Tools.checkPermissionAllGranted(mContext, UtilData.PERMISSION_STRING);
        } else {
            isStorageGranted = true;
        }
        Log.d(TAG, "isStorageGranted="+isStorageGranted);
        if (isStorageGranted) {
            startUpdateService();
        }
        boolean adswitch = Tools.getCustomData(mContext, UtilData.TTAD_SWITCH_KEY, false);
        Log.d(TAG, "adswitch="+adswitch);
        if (!adswitch) {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(ADSWITCH_URL)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String jsondata = Objects.requireNonNull(response.body()).string();
                    if (!jsondata.isEmpty()) {
                        Log.d(TAG, "jsondata=" + jsondata);
                        try {
                            JSONObject jsonObject = new JSONObject(jsondata);
                            if (jsonObject.getInt("code") == 200) {
                                boolean adswitch = jsonObject.getBoolean("switch");
                                Log.d(TAG, "adswitch=" + adswitch);
                                Tools.saveCustomData(mContext, UtilData.TTAD_SWITCH_KEY, adswitch);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "jsondata=empty");
                    }
                }
            });
        }
        if (ADData.AD_SWITCH) {
            mSplashContainer = findViewById(R.id.splash_container);
            mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
            loadSplashAd();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ADData.AD_SWITCH) {
            if (mForceGoMain) {
                goToMainActivity();
            }
        } else {
            goToMainActivity();
        }
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    private void loadSplashAd() {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(ADData.TTAD_SPLASH_CODEID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, String.valueOf(message));
                goToMainActivity();
            }

            @Override
            @MainThread
            public void onTimeout() {
                Log.d(TAG,"开屏广告加载超时");
                goToMainActivity();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                View view = ad.getSplashView();
                if (mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    mSplashContainer.addView(view);
                } else {
                    goToMainActivity();
                }
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        goToMainActivity();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        goToMainActivity();
                    }
                });
                if(ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                Log.d(TAG,"下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG,"下载暂停...");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG,"下载失败...");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            Log.d(TAG,"下载完成...");
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            Log.d(TAG,"安装完成...");
                        }
                    });
                }
            }
        }, ADData.AD_TIME_OUT);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        if (mSplashContainer != null) {
            mSplashContainer.removeAllViews();
        }
        this.finish();
    }

    private void startUpdateService() {
        Log.d(TAG,"startUpdateService...");
        Intent intent = new Intent(SplashActivity.this, MediaStoreUpdateService.class);
        intent.putExtra("mediastore_action", UtilData.ACTION_GETVIDEO);
        startService(intent);
    }
}