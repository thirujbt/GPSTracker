package com.gpsmobitrack.gpstracker.TrackingService;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpsmobitrack.gpstracker.InterfaceClass.TrackingInterface;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;


public class HandlerManager implements DownloadResultReceiver.Receiver{

	private static HandlerManager instance = null;
	public static String trackUserId = "";
	private Context context = null;
	private DownloadResultReceiver mReceiver;
	private TrackingInterface tinf;
	SharedPreferences pref;
	Editor editor;
	//double tempvalue = 0.0001;

	public static HandlerManager getInstance(final Context context) {

		if (instance == null) {
			instance = new HandlerManager();
			instance.context = context;
		}
		return instance;
	}

	public void setonInterface(TrackingInterface tinf){

		this.tinf = tinf;

	}

	public void startTrackLocation(String TrackFriendId ){

		trackUserId = TrackFriendId;
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		pref = context.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
		Calendar cal = Calendar.getInstance();
		Intent intent2 = new Intent(context, TrackFriendService.class);
		mReceiver = new DownloadResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		intent2.putExtra("TrackFriendId", TrackFriendId);
		intent2.putExtra("receiver", mReceiver);
		PendingIntent pintent = PendingIntent.getService(context, 0, intent2, 0);
		if(PendingIntent.getService(context, 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
			alarm.cancel(pintent);
		}
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (updateTime* 1000 * 60), pintent);

		final long Currenttime = System.currentTimeMillis();
		editor = pref.edit();
		editor.putLong("timevalue", Currenttime);
		editor.commit();

	}


	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub

		String serverResp = "";
		serverResp = resultData.getString("result");
		if(MyApplication.isActivityVisible() && MyApplication.getActivityName().equalsIgnoreCase(AppConstants.HOME_DETAIL_ACTIVITY)){

			if(tinf !=null){
				HomeDetailPage.goneBackground = false;
				tinf.interfaceResp(serverResp, AppConstants.SOURCE_DESTINATION_RESP);
			}

		} else {

			HomeDetailPage.goneBackground = true;
			try {

				JSONObject	jObjMain = new JSONObject(serverResp);
				String statusCode = jObjMain.getString(AppConstants.STATUS_CODE);

				if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {


					JSONObject mJsonObject = new JSONObject(serverResp);
					JSONObject mJsonObjectData = mJsonObject.getJSONObject("data");
					JSONArray mJsonArray = mJsonObjectData.getJSONArray("location");
					String lat = null;
					String lon = null;

					for(int i = 0; i < mJsonArray.length(); i ++) {
						JSONObject mJsonObjectLocationLatLon = mJsonArray.getJSONObject(i);
						lat = mJsonObjectLocationLatLon.getString("latitude");
						lon = mJsonObjectLocationLatLon.getString("longitude");
					}

					Double latitude = 0.0;
					Double longtitude = 0.0;

					if((!lat.isEmpty()) && (!lon.isEmpty())) {
						latitude = Double.parseDouble(lat);
						longtitude = Double.parseDouble(lon);
//						   /\
//						  /  \
//                       /    \                  
 //						/______\   
//						|      |
//                      |__||__| 
						/*----/ 
					   /-*---/ latitude = latitude - tempvalue
					  /--*--/ longtitude = longtitude + tempvalue;
					 /---*-/ tempvalue = tempvalue+tempvalue;
					/----*/
					
						if(HomeDetailPage.stalatitudeArry.size() > 0 && HomeDetailPage.stalongitudeArry.size() > 0) {

							if((HomeDetailPage.stalatitudeArry.get(HomeDetailPage.stalatitudeArry.size() - 1).doubleValue() == latitude) && 
									(HomeDetailPage.stalongitudeArry.get(HomeDetailPage.stalongitudeArry.size() - 1).doubleValue() == longtitude)) { 

								HomeDetailPage.stalatitudeArry.remove(HomeDetailPage.stalatitudeArry.size() -1);
								HomeDetailPage.stalongitudeArry.remove(HomeDetailPage.stalongitudeArry.size()-1);
								HomeDetailPage.stalatitudeArry.add(latitude);
								HomeDetailPage.stalongitudeArry.add(longtitude);

							} else {

								HomeDetailPage.stalatitudeArry.add(latitude);
								HomeDetailPage.stalongitudeArry.add(longtitude);
							}
						} else {
							HomeDetailPage.stalatitudeArry.add(latitude);
							HomeDetailPage.stalongitudeArry.add(longtitude);
						}

					} else {
						//city = "Not Available";
					}


				}  else if(statusCode .equalsIgnoreCase(AppConstants.NEW_FAILED)){

					//	showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					HomeDetailPage.isTrackingON = false;
				} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)){

					//		Utils.showToast("Invalid Track id or Destination");
					HomeDetailPage.isTrackingON = false;
 

				} else if(statusCode.equalsIgnoreCase(AppConstants.NOT_AVAILABLE_IN_TRACKLIST)) {

					//		Utils.showToast("Invalid Track id");
					HomeDetailPage.isTrackingON = false;


				} else if(statusCode.equalsIgnoreCase(AppConstants.NO_RECOD_FOUND_SRC)){

					//		Utils.showToast("No Record Found");
				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}