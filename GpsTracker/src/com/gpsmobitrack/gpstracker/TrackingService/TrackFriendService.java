package com.gpsmobitrack.gpstracker.TrackingService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;


public class TrackFriendService extends IntentService {

	SharedPreferences pref;
	Editor editor;
	String trackUserID;
	ResultReceiver receiver;
	public AsyncResponse asyncInterface ;
	Dialog alertProgressDialog = null;
	String xml;


	public TrackFriendService() {
		super("");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		pref = TrackFriendService.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		boolean isServiceOn = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
		long StoredTime = 0;
		StoredTime = pref.getLong("timevalue", 0);
		final long Currenttime = System.currentTimeMillis();
		final long difftime = Currenttime - StoredTime;
		if (difftime >= (24 * 3600000)) {
			if(HomeDetailPage.stalatitudeArry != null){

				if(HomeDetailPage.stalatitudeArry.size() >0){
					HomeDetailPage.stalatitudeArry.clear();
					HomeDetailPage.stalongitudeArry.clear();
				}
			}
		}
		if(isServiceOn){

			if(isInternetOn()){

				final String userid = pref.getString(AppConstants.USER_ID_PREF, null);
				final Calendar calender = Calendar.getInstance();
				final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
				final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");


				receiver = intent.getParcelableExtra("receiver");
				trackUserID = intent.getStringExtra("TrackFriendId");

				String date = sdfDate.format(calender.getTime());
				String time = sdfTime.format(calender.getTime());

				BasicNameValuePair trackUserValue = new BasicNameValuePair(AppConstants.ID, trackUserID);
				BasicNameValuePair authKeyValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
				BasicNameValuePair destinationValue = new BasicNameValuePair(AppConstants.DESTINATION, date+" "+time);
				List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
				detailPageList.add(authKeyValue);
				detailPageList.add(trackUserValue);
				detailPageList.add(destinationValue);


				try{
					HttpClient httpClient = new DefaultHttpClient();
					HttpParams httpParams = httpClient.getParams();
					HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
					HttpConnectionParams.setSoTimeout(httpParams, 30000);
					ConnManagerParams.setTimeout(httpParams, 30000);
					HttpPost httpPost = new HttpPost(AppConstants.SOURCE_DESTINATION_URL);

					UrlEncodedFormEntity urlEncode = new UrlEncodedFormEntity(detailPageList);
					httpPost.setEntity(urlEncode);

					HttpResponse httpResponse = httpClient.execute(httpPost);
					Utils.printLog("Response Code", ""+httpResponse.getStatusLine().getStatusCode());
					HttpEntity htpEntity = httpResponse.getEntity();
					xml = EntityUtils.toString(htpEntity);
					Utils.printLog("Response", ""+xml);

				}
				catch(Exception e){
					Utils.printLog("Excep profile===", ""+e);
				}

				Bundle bundle = new Bundle();
				if(xml!= null && xml.length()>0){
					bundle.putString("result", xml);
					if(receiver != null){
						
						receiver.send( AppConstants.SOURCE_DESTINATION_RESP, bundle);
					}
				}

			} else {

				Utils.showToast("No Internet Connection");

			}
		} else {


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
