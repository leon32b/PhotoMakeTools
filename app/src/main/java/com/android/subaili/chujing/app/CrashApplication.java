package com.android.subaili.chujing.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.subaili.chujing.config.TTAdManagerHolder;
import com.android.subaili.chujing.utils.ADData;
import com.android.subaili.chujing.utils.Tools;
import com.android.subaili.chujing.utils.UtilData;

public class CrashApplication extends Application {
	private final static String TAG = "CrashApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Context mContext = getApplicationContext();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(mContext);
		getTTAdValue(mContext);
		boolean adswitch = Tools.getCustomData(mContext, UtilData.TTAD_SWITCH_KEY, false);
		if (adswitch) {
			ADData.AD_SWITCH = true;
			TTAdManagerHolder.init(mContext);
		} else {
			ADData.AD_SWITCH = false;
		}
	}

	private void getTTAdValue(Context context) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		ADData.TTAD_APPID = appInfo.metaData.getString("TTAD_APPID").replace("L", "");
		ADData.TTAD_SPLASH_CODEID = appInfo.metaData.getString("TTAD_SPLASH_CODEID").replace("L", "");
		ADData.TTAD_640x100_CODEID = appInfo.metaData.getString("TTAD_640x100_CODEID").replace("L", "");
		Log.d(TAG, "TTAD_APPID="+ADData.TTAD_APPID);
		Log.d(TAG, "TTAD_SPLASH_CODEID="+ADData.TTAD_SPLASH_CODEID);
		Log.d(TAG, "TTAD_640x100_CODEID="+ADData.TTAD_640x100_CODEID);
	}
}