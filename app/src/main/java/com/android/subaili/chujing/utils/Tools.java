package com.android.subaili.chujing.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

public class Tools {
    public static String getFileName(String path, String type) {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date curDateTime = new Date(System.currentTimeMillis());
        String strName = formatDateTime.format(curDateTime);
        return String.format("%s%s%s%s", path, File.separator, strName, type);
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static ArrayList<String> GetPhotoFilePath(String fileDir) {
        ArrayList<String> pathList = new ArrayList<>();
        File file = new File(fileDir);
        File[] subFile = file.listFiles();
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                if (filename.trim().toLowerCase().endsWith(".jpg")) {
                    pathList.add(subFile[iFileLength].getAbsolutePath());
                }
            }
        }
        return pathList;
    }

    public static ArrayList<Bitmap> GetPhotoFileBitmap(String fileDir) {
        ArrayList<Bitmap> pathList = new ArrayList<>();
        File file = new File(fileDir);
        File[] subFile = file.listFiles();
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                if (filename.trim().toLowerCase().endsWith(".jpg")) {
                    Bitmap bm = BitmapFactory.decodeFile(subFile[iFileLength].getAbsolutePath());
                    pathList.add(bm);
                }
            }
        }
        return pathList;
    }

    public static String FormetFileSize(Context context, long fileS) {
        return Formatter.formatFileSize(context, fileS);
    }

    public static String getDateTime(String time) {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatDateTime.format(new Date(Long.parseLong(time)*1000L));
    }

    public static Date stringToDateTime(String date) throws ParseException {
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatDateTime.parse(date);
    }

    public static void scaleRect(RectF rect, float scale) {
        float w = rect.width();
        float h = rect.height();
        float newW = scale * w;
        float newH = scale * h;
        float dx = (newW - w) / 2;
        float dy = (newH - h) / 2;
        rect.left -= dx;
        rect.top -= dy;
        rect.right += dx;
        rect.bottom += dy;
    }

    public static void deleteAllFilesOfDir(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
    }

    public static void rotateRect(RectF rect, float center_x, float center_y,
                                  float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;
        float dx = newX - x;
        float dy = newY - y;
        rect.offset(dx, dy);
    }

    public static int dpToPx(Context context, int dp) {
        int px = Math.round(dp * getPixelScaleFactor(context));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        int dp = Math.round(px / getPixelScaleFactor(context));
        return dp;
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveBitmap(Bitmap bitmap, String filename) {
        File filePic;
        try {
            filePic = new File(filename);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static String getFileNameWithSuffix(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }


    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static float dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getRootDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return context.getCacheDir().getAbsolutePath();
        }
    }

    public static String getFormatName(String fileName) {
        fileName = fileName.trim();
        String s[] = fileName.split("\\.");
        if (s.length >= 2) {
            return s[s.length - 1];
        }
        return "mp4";
    }

    public static void saveCustomData(Context ctx, String key, String content) {
        SharedPreferences share = ctx.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, content);
        editor.commit();
    }

    public static String getCustomData(Context context, String key, String value) {
        SharedPreferences share = context.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        String data = share.getString(key, value);
        return data;
    }

    public static void saveCustomData(Context ctx, String key, int content) {
        SharedPreferences share = ctx.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, content);
        editor.commit();
    }

    public static int getCustomData(Context context, String key, int value) {
        SharedPreferences share = context.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        int data = share.getInt(key, value);
        return data;
    }

    public static void saveCustomData(Context ctx, String key, boolean content) {
        SharedPreferences share = ctx.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, content);
        editor.commit();
    }

    public static boolean getCustomData(Context context, String key, boolean value) {
        SharedPreferences share = context.getSharedPreferences(UtilData.SAVE_CUSTOM_DATA, Context.MODE_PRIVATE);
        boolean data = share.getBoolean(key, value);
        return data;
    }

    public static boolean checkPermissionAllGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRunService(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSDKVersionM (Context context) {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                return true;
            }
        }
        return false;
    }
}