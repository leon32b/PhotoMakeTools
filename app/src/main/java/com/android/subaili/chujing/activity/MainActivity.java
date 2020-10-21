package com.android.subaili.chujing.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.adapter.AllVideoAdapter;
import com.android.subaili.chujing.service.MediaStoreUpdateService;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private long exitTime = 0;
    private AlertDialog dialog;
    private AllVideoAdapter mAllVideoAdapter;
    private static final int MSG_GET_ALLVIDEO = 0x100;
    private final static String TAG = "MainActivity";
    private static final String SP_IS_FIRST_ENTER_APP = "SP_IS_FIRST_ENTER_APP";
    private final CustomHandler mHandler = new CustomHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        ImageView mSetting = findViewById(R.id.iv_setting);
        mSetting.setOnClickListener(view -> startActivity(new Intent(mContext, SettingActivity.class)));
        RecyclerView mRecyclerAllVideo = findViewById(R.id.rv_allvideo);
        GridLayoutManager mAllVideolayoutManage = new GridLayoutManager(mContext, UtilData.VIDEO_ITEM_COUNT);

        mRecyclerAllVideo.setLayoutManager(mAllVideolayoutManage);
        mAllVideoAdapter = new AllVideoAdapter(mContext, UtilData.mAllVideoList);
        mRecyclerAllVideo.setAdapter(mAllVideoAdapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UtilData.ACTION_ALL_VIDEO_UPDATE);
        registerReceiver(mReceiver, filter);
        handleFirstEnterApp();
    }

    private void handleFirstEnterApp() {
        boolean firstEnterApp = isFirstEnterApp();
        if (firstEnterApp) {
            startDialog();
        } else {
            checkPermission();
        }
    }

    private void startDialog() {
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        dialog.setCancelable(false);
        final Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_initmate);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            TextView textView = window.findViewById(R.id.tv_1);
            TextView tvCancel= window.findViewById(R.id.tv_cancel);
            TextView tvAgree= window.findViewById(R.id.tv_agree);
            tvCancel.setOnClickListener(view ->startFinish());
            tvAgree.setOnClickListener(view -> enterApp());
            String str = getString(R.string.tip_agreement_privacy);
            textView.setText(str);
            textView.setTextColor(Color.WHITE);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(str);
            final int start = str.indexOf("《");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(mContext, AgreementActivity.class));
                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.main_button_red));
                }

            }, start, start + 6, 0);
            final int end = str.lastIndexOf("《");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(mContext, PrivacyActivity.class));
                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.main_button_red));
                }

            }, end, end + 6, 0);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb, TextView.BufferType.SPANNABLE);
        }
    }

    private void startFinish() {
        Tools.saveCustomData(mContext, SP_IS_FIRST_ENTER_APP, true);
        dialog.cancel();
        finish();
    }

    private void enterApp() {
        saveFirstEnterApp();
        dialog.cancel();
        checkPermission();
    }

    private boolean isFirstEnterApp() {
        return Tools.getCustomData(mContext, SP_IS_FIRST_ENTER_APP, true);
    }

    private void saveFirstEnterApp() {
        Tools.saveCustomData(mContext, SP_IS_FIRST_ENTER_APP, false);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UtilData.ACTION_ALL_VIDEO_UPDATE.equals(action)) {
            mHandler.obtainMessage(MSG_GET_ALLVIDEO).sendToTarget();
        }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mReceiver);
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.title_exit_app), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exitApp();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    private static class CustomHandler extends Handler {
        private WeakReference<MainActivity> mWeakReference;

        public CustomHandler(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity videoactivity = mWeakReference.get();
            if (msg.what == MSG_GET_ALLVIDEO) {
                videoactivity.mAllVideoAdapter.updateData(UtilData.mAllVideoList);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkPermission() {
        Log.d(TAG, "checkPermission...");
        if (Tools.isSDKVersionM(mContext)) {
            boolean isAllGranted = Tools.checkPermissionAllGranted(mContext, UtilData.PERMISSION_STRING);
            Log.d(TAG, "checkPermission,isAllGranted="+isAllGranted);
            if (!isAllGranted) {
                ActivityCompat.requestPermissions(this, UtilData.PERMISSION_STRING, 1);
            } else {
                startUpdateService();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                startUpdateService();
            } else {
                showWaringDialog();
            }
        }
    }

    private void startUpdateService() {
        if (!Tools.isRunService(mContext, MediaStoreUpdateService.class.getName())) {
            Log.d(TAG, "startUpdateService...");
            Intent intent = new Intent(MainActivity.this, MediaStoreUpdateService.class);
            intent.putExtra("mediastore_action", UtilData.ACTION_GETALL);
            startService(intent);
        }
    }

    private void showWaringDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_warning))
                .setMessage(getString(R.string.tip_permissions_set))
                .setNegativeButton(getString(R.string.text_cancel), (dialog, which) -> finish())
                .setPositiveButton(getString(R.string.text_ok), (dialog, which) -> checkPermission()).show();
    }
}