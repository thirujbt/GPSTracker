package com.gpsmobitrack.gpstracker;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.R;
import android.app.Application;
import android.content.Context;

import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@ReportsCrashes(formKey = "", mailTo = "thirunavukkarasu.b@pickzy.com", customReportContent = { ReportField.APP_VERSION_CODE,
		ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
		ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.untitled)

public class MyApplication extends Application{

	private static Context context;
	private static boolean activityVisible;
	private static String activityname = "";
	private static String CUId = "";

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
		if(AppConstants.IS_TEST_MODE){
			ACRA.init(this);
		}
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}

	public static boolean isActivityVisible() {
		return activityVisible;
	}  

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}

	public static void setActivityName(String actName){
		activityname = actName;
	}

	public static String getActivityName(){
		return activityname;
	}

	public static void setChatUserID(String cuId ){
		CUId = cuId;
	}

	public static String getChatUserID(){
		return CUId;
	}
}