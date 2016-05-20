package com.gpsmobitrack.gpstracker.BackgroundService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.gpsmobitrack.gpstracker.ServiceRequest.BackGroundSync;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSTracker;
import com.gpsmobitrack.gpstracker.Utils.Utils;


public class BackgroundService extends IntentService {

	public static long refreshtime;
	public static String phonenum, email, refresh;
	public static Location location = null;
	public boolean dataexist =false;
	GPSTracker gps;
	SharedPreferences pref; 
	Editor editor;
	boolean profilePrivacy;
	public BackgroundService() {
		super("BackgroundService");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {
		Utils.printLog("CLASS", "BackgroundService");
		pref = BackgroundService.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		profilePrivacy=pref.getBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
		System.out.println("Checking profile value..."+profilePrivacy);
		boolean isServiceOn = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
		Utils.printLog("SERVICE", ""+isServiceOn);
		if(isServiceOn){
			gps = new GPSTracker(BackgroundService.this);
			Utils.printLog("Gps Get Location", ""+gps.canGetLocation());
			if(gps.canGetLocation()){
				String userId = pref.getString(AppConstants.USER_ID_PREF, null);
				if(!((gps.getLatitude())== 0)&&!((gps.getLongitude())== 0)){
					String lat = String.valueOf(gps.getLatitude());
					String lon = String.valueOf(gps.getLongitude());
					String url = AppConstants.SYNC_LOCATION_URL;
					System.out.println("backgroud url.."+url);
					final Calendar calender = Calendar.getInstance();
					final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
					String date = sdfDate.format(calender.getTime());
					String time = sdfTime.format(calender.getTime());
					long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
					String timeInterval = String.valueOf(updateTime);
					String profileState=String.valueOf(profilePrivacy);

					BasicNameValuePair userIdValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
					BasicNameValuePair latitudeValue = new BasicNameValuePair(AppConstants.LATITUDE, lat);
					BasicNameValuePair longitudeValue = new BasicNameValuePair(AppConstants.LONGITUDE, lon);
					BasicNameValuePair timeIntervalValue = new BasicNameValuePair(AppConstants.TIME_INTERVAL,timeInterval);
					BasicNameValuePair timeValue = new BasicNameValuePair("time",date+" "+time);
					BasicNameValuePair profilePublic = new BasicNameValuePair(AppConstants.PROFILE_PUBLIC,profileState);

					List<NameValuePair> updateServerList = new ArrayList<NameValuePair>();
					updateServerList.add(userIdValue);
					updateServerList.add(latitudeValue);
					updateServerList.add(longitudeValue);
					updateServerList.add(timeValue);
					updateServerList.add(timeIntervalValue);
					updateServerList.add(profilePublic);
					System.out.println("Background test..."+updateServerList);
					Utils.printLog("INTERNET", ""+isInternetOn());
					if(isInternetOn()){
						new BackGroundSync(BackgroundService.this, url).execute(updateServerList);
					}
				}
			}
		} else {

			/*AlarmManager alarm = (AlarmManager)BackgroundService.this.getSystemService(Context.ALARM_SERVICE);

			Intent intent2 = new Intent(BackgroundService.this, BackgroundService.class);
			PendingIntent pintent = PendingIntent.getService(BackgroundService.this, 0, intent2, 0);
			if(PendingIntent.getService(BackgroundService.this, 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
				alarm.cancel(pintent);
			}*/


		}
	}

	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			return false;
		}

		return false;
	}

	public void onDestroy() {
		super.onDestroy();
	}

}
