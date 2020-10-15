package com.android.subaili.chujing.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.subaili.chujing.R;
import com.android.subaili.chujing.adapter.VideoToPhotoAdapter;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import codepig.ffmpegcldemo.FFmpegKit;

public class VideoToPhotoActivity extends AppCompatActivity {
    private int mVideoLengthTime;
    private String mCurrentVideoPath;
    private Context mContext;
    private ImageView mImageView;
    private SeekBar mSeekBar;
    private ProgressDialog mProgressDialog;
    private VideoToPhotoAdapter mVideoToPhotoAdapter;
    private static final int MSG_VIDEOTOPIC_DONE = 0x100;
    private static final int MSG_CUTVIDEO_DONE = 0x200;
    private final CustomHandler mHandler = new CustomHandler(this);
    private static ArrayList<Bitmap> mBitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videotophoto);
        mContext = VideoToPhotoActivity.this;
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        mVideoLengthTime = (int)intent.getLongExtra("time", 0)/1000;
        ImageView mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(view -> finish());
        TextView mNext = findViewById(R.id.tv_next);
        mNext.setOnClickListener(view -> {

        });
        mImageView = findViewById(R.id.iv_image);
        RecyclerView mRecyclerView = findViewById(R.id.rv_videotophoto);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mVideoToPhotoAdapter = new VideoToPhotoAdapter(mBitmapList);
        mRecyclerView.setAdapter(mVideoToPhotoAdapter);
        mSeekBar = findViewById(R.id.seekbar);

        ImageView mprevious = findViewById(R.id.iv_previous);
        mprevious.setOnClickListener(v -> {

        });
        ImageView mrotate = findViewById(R.id.iv_rotate);
        mrotate.setOnClickListener(v -> {

        });
        ImageView mnext = findViewById(R.id.iv_next);
        mnext.setOnClickListener(v -> {

        });

        mBitmapList.clear();
        showProgressDialog(getString(R.string.tip_extract));
        VideoToPicThread mVideoToPicThread = new VideoToPicThread(VideoToPhotoActivity.this, path);
        mVideoToPicThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void updateVideoToPic() {
        mProgressDialog.dismiss();
        mVideoToPhotoAdapter.notifyDataSetChanged();
        mImageView.setImageBitmap(mBitmapList.get(0));
        Bitmap bitmap = bg2WhiteBitmap(mBitmapList.get(0));
        mSeekBar.setThumb(new BitmapDrawable(bitmap));
    }

    private Bitmap bg2WhiteBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width+10, height+10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 5, 5, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawRect(0, 0, width+10, height+10, paint);
        return newBitmap;
    }

    private static class CustomHandler extends Handler {
        private WeakReference<VideoToPhotoActivity> mWeakReference;

        public CustomHandler(VideoToPhotoActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoToPhotoActivity videotophotoactivity = mWeakReference.get();
            switch (msg.what) {
                case MSG_VIDEOTOPIC_DONE:
                    videotophotoactivity.updateVideoToPic();
                    break;
                case MSG_CUTVIDEO_DONE:
                    videotophotoactivity.mProgressDialog.dismiss();
                    videotophotoactivity.startToVideoEdit();
                    break;
            }
        }
    }

    private void startToVideoEdit() {
//        Intent intent = new Intent(mContext, VideoEditActivity.class);
//        intent.putExtra("path", mCurrentVideoPath);
//        mContext.startActivity(intent);
    }

    @SuppressLint("DefaultLocale")
    private String getFormatTime (int time) {
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = time / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static class VideoCutThread extends Thread {
        private String start;
        private String length;
        private WeakReference<VideoToPhotoActivity> mWeakReference;

        public VideoCutThread(VideoToPhotoActivity activity, String start, String length) {
            this.start = start;
            this.length = length;
            mWeakReference = new WeakReference<>(activity);
        }
        
        public void run() {
            VideoToPhotoActivity videocutactivity = mWeakReference.get();
            videocutactivity.doVideoCut(start, length);
        }
    }

    private void doVideoCut(String start, String length) {
        final String cutpath = Objects.requireNonNull(getExternalFilesDir(UtilData.VIDEO_PATH)).getAbsolutePath() + "/" + System.currentTimeMillis()+ ".mp4";
        String[] commands = cutVideo(mCurrentVideoPath, cutpath, start, length);
        FFmpegKit.execute(commands, new FFmpegKit.KitInterface() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onEnd(int result) {
                mCurrentVideoPath = cutpath;
                mHandler.obtainMessage(MSG_CUTVIDEO_DONE).sendToTarget();
            }
        });
    }

    private String[] cutVideo(String videoUrl, String outputUrl, String start, String length) {
        int index = 0;
        final int count = 12;
        String[] commands = new String[count];
        commands[index++] = "ffmpeg";
        commands[index++] = "-ss";
        commands[index++] = start;
        commands[index++] = "-t";
        commands[index++] = length;
        commands[index++] = "-i";
        commands[index++] = videoUrl;
        commands[index++] = "-vcodec";
        commands[index++] = "copy";
        commands[index++] = "-acodec";
        commands[index++] = "copy";
        commands[index++] = outputUrl;
        return commands;
    }
	
    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(mContext, R.style.CustomDialog);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.view_progress);
        TextView tv = mProgressDialog.findViewById(R.id.tv_message);
        tv.setText(message);
    }

    private void doVideoToPic (String path) {
        try {
            if (Tools.isExists(path)) {
                File filevideo = mContext.getExternalFilesDir(UtilData.VIDEO_PATH);
                assert filevideo != null;
                Tools.deleteAllFilesOfDir(filevideo);
                if (!filevideo.exists()) {
                    filevideo.mkdir();
                }
                String strType = Tools.getFormatName(path);
                String strname = System.currentTimeMillis() + "." + strType;
                mCurrentVideoPath = filevideo + "/" + strname;
                if (Tools.copyFile(path, mCurrentVideoPath)) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    String frame = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
                    long lngtime = mVideoLengthTime*1000*1000;
                    int step = (int)lngtime/15;
                    for (int i = 1; i <= 15; i++) {
                        long count = i*step;
                        if (count > lngtime) {
                            count = lngtime;
                        }
                        Bitmap bm = retriever.getFrameAtTime(count, MediaMetadataRetriever.OPTION_CLOSEST);
                        mBitmapList.add(bm);
                    }
                }
                mHandler.obtainMessage(MSG_VIDEOTOPIC_DONE).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class VideoToPicThread extends Thread {
        private String path;
        private WeakReference<VideoToPhotoActivity> mWeakReference;

        public VideoToPicThread(VideoToPhotoActivity activity, String path) {
            this.path = path;
            mWeakReference = new WeakReference<>(activity);
        }

        public void run() {
            VideoToPhotoActivity videotophotoactivity = mWeakReference.get();
            videotophotoactivity.doVideoToPic(path);
        }
    }
}