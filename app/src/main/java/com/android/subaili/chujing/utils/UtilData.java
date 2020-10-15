package com.android.subaili.chujing.utils;

import android.Manifest;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.android.subaili.chujing.model.AllVideoModel;

import java.util.ArrayList;

public class UtilData {
    public static boolean UPDATE_VIDEO_FLAG;
    public static boolean UPDATE_PHOTO_FLAG;
    public static boolean UPDATE_GIF_FLAG;
	public static final int CUTVIDEO_DEFAULT_LENGTH = 15;
    public static final int VIDEO_ITEM_COUNT = 3;
    public static final int GIF_ITEM_COUNT = 3;
    public static final int PHOTO_ITEM_COUNT= 4;
    public static final int FPS_DEFAULT = 4;
    public static final int CANVAS_DEFAULT = 0;
    public static final int QUALITY_DEFAULT = 1;
    public static final int RESOLUTION_DEFAULT = 5;
    public static final int PHOTO_SELECT_MAX = 50;
    public static final int GIF_TO_VIDEO = 1;
    public static final int GIF_TO_PHOTO = 2;
    public static final int GIF_TO_COMPRESS = 3;
    public static final int GIF_TYPE = 1;
    public static final int PHOTO_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int ACTION_GETALL = 1;
    public static final int ACTION_GETVIDEO = 2;
    public static final int ACTION_GETPHOTO = 3;
    public static final int ACTION_GETGIF = 4;
    public static final String CURRENT_FPS_KEY = "current_fps";
    public static final String CURRENT_QUALITY_KEY = "current_quality";
    public static final String CURRENT_RESOLUTION_KEY = "current_Resolution";
    public static final String CURRENT_CANVAS_KEY = "current_canvas";
    public static final String TTAD_SWITCH_KEY = "ttad_switch";
	public static String SAVE_CUSTOM_DATA = "save_custom_data";
    public static ArrayList<AllVideoModel> mAllVideoList = new ArrayList<>();
    public static ArrayList<Integer> mListColor = new ArrayList<>();
    public static ArrayList<String> mListFps = new ArrayList<>();
	public static ArrayList<Drawable> mListCanvas = new ArrayList<>();
    public static ArrayList<Drawable> mListFont = new ArrayList<>();
    public static ArrayList<Drawable> mListFontSelect = new ArrayList<>();
    public static ArrayList<Typeface> mListTypeface = new ArrayList<>();
    public static final String PREVIEW_PATH = "/gifmaketool/preview";
    public static final String STICKER_PATH = "/gifmaketool/sticker";
    public static final String VIDEO_PATH = "/gifmaketool/video";
    public static final String GIF_PATH = "/gifmaketool/gif";
    public static final String FILE_TYPE_GIF = ".gif";
	public static final String FILE_TYPE_PHOTO = ".jpg";
	public static final String FILE_TYPE_VIDEO = ".mp4";
    public static final String ACTION_ALL_PHOTO_UPDATE = "android.intent.subaili.action.ALL_PHOTO_UPDATE";
    public static final String ACTION_ALL_GIF_UPDATE = "android.intent.subaili.action.ALL_GIF_UPDATE";
    public static final String ACTION_ALL_VIDEO_UPDATE = "android.intent.subaili.action.ALL_VIDEO_UPDATE";
    public static final String[] ALL_PERMISSION_STRING = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final String[] STORAGE_PERMISSION_STRING = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}