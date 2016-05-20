package com.gpsmobitrack.gpstracker.BackgroundService;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gpsmobitrack.gpstracker.Utils.AppConstants;
public class NetworkReceiver extends BroadcastReceiver {

	SharedPreferences pref;
	Context ctn;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctn = context;

		if(isInternetOn()){
			pref = context.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
			long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Calendar cal = Calendar.getInstance();
			Intent i = new Intent(context,BackgroundService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0, i, 0);
			if(PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null){
				am.cancel(pIntent);
			}
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (1000 * 60 * updateTime), pIntent);

		}
	}

	//Check Internet connection
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) ctn.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}

}
