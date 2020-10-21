package com.android.subaili.chujing.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.android.subaili.chujing.model.AllVideoModel;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;

import java.util.ArrayList;

public class MediaStoreUpdateService extends Service {
    private Context mContext;
    private final static String TAG = "MediaStoreUpdateService";

    private static final String[] videoColumns = new String[] {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate...");
        mContext = this.getApplicationContext();
        VideoObserver mVideoObserver = new VideoObserver(new Handler());
        getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, mVideoObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle mBundle = intent.getExtras();
            if (mBundle != null) {
                int intAction = mBundle.getInt("mediastore_action");
                Log.d(TAG, "mediastore_action="+intAction);
                switch (intAction) {
                    case UtilData.ACTION_GETVIDEO:
                        GetAllVideoThread mGetAllVideoThread = new GetAllVideoThread(mContext);
                        mGetAllVideoThread.start();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private class GetAllVideoThread extends Thread {
        private Context context;

        public GetAllVideoThread(Context context) {
            this.context = context;
        }

        public void run() {
            UtilData.mAllVideoList.clear();
            UtilData.mAllVideoList = getAllVideo(context);
        }
    }

    private synchronized ArrayList<AllVideoModel> getAllVideo(Context context) {
        Log.d(TAG, "getAllVideo...");
        UtilData.UPDATE_VIDEO_FLAG = false;
        ArrayList<AllVideoModel> allvideos = new ArrayList<>();
        try (Cursor videoscursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                new String[]{"video/mp4"}, MediaStore.Video.Media.DATE_MODIFIED + " DESC")) {
            assert videoscursor != null;
            while (videoscursor.moveToNext()) {
                AllVideoModel allvideomodel = new AllVideoModel();
                allvideomodel.path = videoscursor.getString(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                if (!Tools.isExists(allvideomodel.path)) {
                    continue;
                }
                allvideomodel.name = videoscursor.getString(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                allvideomodel.resolution = videoscursor.getString(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                allvideomodel.size = videoscursor.getLong(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                allvideomodel.duration = videoscursor.getLong(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                allvideomodel.modified = Tools.getDateTime(videoscursor.getString(videoscursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)));
                allvideos.add(allvideomodel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        UtilData.UPDATE_VIDEO_FLAG = true;
        Log.d(TAG, "sendBroadcast ACTION_ALL_VIDEO_UPDATE");
        context.sendBroadcast(new Intent(UtilData.ACTION_ALL_VIDEO_UPDATE));
        return allvideos;
    }

    class VideoObserver extends ContentObserver {
        public VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            GetAllVideoThread mGetAllVideoThread = new GetAllVideoThread(mContext);
            mGetAllVideoThread.start();
        }
    }
}

