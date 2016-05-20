package com.gpsmobitrack.gpstracker.MenuItems;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Bean.MapHistory;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tyczj.mapnavigator.Navigator;

public class HistoryMapPage extends FragmentActivity  implements AsyncResponse, OnClickListener{

	private GoogleMap googleMap;
	MarkerOptions marker;
	String lat,log,profileUrl,userName;
	Double latitude,longtitude;
	//	ArrayList<Double> latitudeList,longtitudeList;
	String trackUserID,historyDate,statusCode,statusMsg;
	int userPosition;
	TextView userLocTxt,timeTxt,dateTxt,titleNameTxt;
	SharedPreferences pref;
	ImageView profileImgBtn,backBtn;
	DisplayImageOptions options;
	ArrayList<MapHistory> mapHistoryList = new ArrayList<MapHistory>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_map_view);
		pref = HistoryMapPage.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		titleNameTxt = (TextView) findViewById(R.id.username_txt);
		dateTxt = (TextView)findViewById(R.id.history_user_date_txt);
		profileImgBtn = (ImageView)findViewById(R.id.user_profile_img);
		backBtn = (ImageView)findViewById(R.id.back_btn);
		//	latitudeList = new ArrayList<Double>();
		//	longtitudeList = new ArrayList<Double>();
		backBtn.setOnClickListener(this);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
		//	.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		if(b != null){
			trackUserID = b.getString("trackuserID");
			historyDate = b.getString("historydate");
			profileUrl = b.getString("trackuserImageUrl");
			userName = b.getString("trackuserName");
			String upperString="";
			if(userName!=null && !userName.equals("") ){
				upperString = userName.substring(0,1).toUpperCase() + userName.substring(1);
			}
			titleNameTxt.setText(upperString);
			dateTxt.setText(historyDate);
			userPosition = b.getInt(AppConstants.USER_LIST_POSITION_INTENT);
		}
		ImageLoader.getInstance().displayImage(profileUrl, profileImgBtn,options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}
			@Override
			public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
				Utils.printLog("Load complete1", "Load complete");
				final ImageView imageView = (ImageView) view;
				boolean checkbit = false;
				if(loadedImage != null){
					checkbit = true;
				}
				Utils.printLog("values", ""+profileUrl+",,"+""+checkbit);
				ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
				observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						imageView.getViewTreeObserver().removeOnPreDrawListener(this);
						if(profileUrl != null && ! profileUrl.equals("null") && loadedImage != null && !profileUrl.equalsIgnoreCase("")){
							Utils.printLog("History Map Page", "History in bitmap");
							Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
						}
						return true;
					}
				});
			}
		});
		if(isInternetOn()){
			String url = AppConstants.HISTORY_URL;
			Utils.printLog("History Map", historyDate + "," + trackUserID);
			String userid = pref.getString(AppConstants.USER_ID_PREF, null);
			BasicNameValuePair userKeyValue = new BasicNameValuePair("userKey", trackUserID);
			BasicNameValuePair authKeyValue = new BasicNameValuePair("authKey", userid);
			BasicNameValuePair userDateValue = new BasicNameValuePair("date", historyDate);
			List<NameValuePair> historyMapList = new ArrayList<NameValuePair>();
			historyMapList.add(userKeyValue);
			historyMapList.add(userDateValue);
			historyMapList.add(authKeyValue);
			new GpsAsyncTask(HistoryMapPage.this, url,0, this).execute(historyMapList);
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	@Override
	public void onClick(View v) {
		if(v == backBtn){
			finish();
		}
	}
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap =  ( (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_history_page)).getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			// check if map is created successfully or not
			if (googleMap == null) {
				Utils.showToast("Sorry! unable to create maps");
			}
		}
	}
	@SuppressWarnings("unused")
	private void drawMarker(LatLng point){
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();
		// Setting latitude and longitude for the marker
		markerOptions.position(point);
		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
	}
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		try {
			if (serverResp != null) {
				JSONObject jObj = new JSONObject(serverResp);
				String statusCode = jObj.getString(AppConstants.STATUS_CODE);
				if (statusCode.equals(AppConstants.NEW_SUCCESS)) {
					JSONArray historyJArray = jObj.getJSONArray("history");
					Utils.printLog("History tag", "" + historyJArray);
					for (int i = 0; i < historyJArray.length(); i++) {
						JSONObject historyjObj = historyJArray.getJSONObject(i);
						String lat = historyjObj.getString("latitude");
						String lon = historyjObj.getString("longitude");
						String time = historyjObj.getString("time");
						String date = historyjObj.getString("date");
						String city = "Not available";
						double latdouble = historyjObj.getDouble("latitude");
						double londouble = historyjObj.getDouble("longitude");
						Utils.printLog("latitude", ""+lat+""+lon);
						if ((!lat.isEmpty()) && (!lon.isEmpty())) {
							latitude = Double.parseDouble(lat);
							longtitude = Double.parseDouble(lon);
							try {
								Geocoder geo;
								List<Address> cityData;
								geo = new Geocoder(HistoryMapPage.this,Locale.ENGLISH);
								cityData = geo.getFromLocation((Double.parseDouble(lat)),(Double.parseDouble(lon)), 1);
								Address addr = cityData.get(0);
								city = addr.getLocality();
							} catch (Exception e) {
								city = "Not Available";
							}
							MapHistory mapHistoryObj = new MapHistory();
							mapHistoryObj.setLatitude(latdouble);
							mapHistoryObj.setLongitude(londouble);
							mapHistoryObj.setTime(time);
							mapHistoryObj.setDate(date);
							mapHistoryObj.setCityName(city);
							mapHistoryList.add(mapHistoryObj);
						}
						Utils.printLog("City history" + i, "" + city);
					}
					try {
						// Loading map
						initilizeMap();
						if( mapHistoryList.size()>=2){
							for(int i=0;i<mapHistoryList.size()-1;i++){
								//drawMarker(new LatLng(latitudeList.get(i), longtitudeList.get(i)));
								LatLng start = new LatLng(mapHistoryList.get(i).getLatitude(),mapHistoryList.get(i).getLongitude());
								LatLng end = new LatLng(mapHistoryList.get(i+1).getLatitude(),mapHistoryList.get(i+1).getLongitude());
								if(i==0){
									centerInLoc(start, mapHistoryList.get(i).getCityname());
								}
								centerInLoc(end, mapHistoryList.get(i).getCityname());
								Navigator navigator = new Navigator(googleMap, start, end);
								navigator.findDirections(true);
								//	Utils.printLog("LAT and LON history", latitudeList.get(i) +"," + longtitudeList.get(i));
							}
						} else if(mapHistoryList.size() ==1 ){
							LatLng start = new LatLng(mapHistoryList.get(0).getLatitude(),mapHistoryList.get(0).getLongitude());
							centerInLoc(start, mapHistoryList.get(0).getCityname());
						} else {
							Utils.showToast("No location");
						}
					} catch (Exception e) {
						Utils.printLog("Map Excep history", ""+e);
						e.printStackTrace();
					}
				} else if (statusCode.equals(AppConstants.NEW_FAILED)) {
					showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
				} else if (statusCode.equalsIgnoreCase(AppConstants.EMPTY_TRACKID_HISTORY)) {
					Utils.showToast("Unable to fetch the Records. Try Later!!");
				} else if (statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACKID_HISTORY)) {
					Utils.showToast("Unable to fetch the Records. Try Later!!");
				} else if (statusCode.equalsIgnoreCase(AppConstants.NO_HISTORYDATE_RECORD)){
					Utils.showToast("No Record Found");
				}
			} else {
				Utils.showToast("No response from server");
			}
		} catch (JSONException e) {
			Utils.printLog("JSON Excep", "" + e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	private void centerInLoc(LatLng myLaLn,String name) {
		CameraPosition camPos = new CameraPosition.Builder().target(myLaLn).zoom(15).bearing(45).tilt(0).build();
		CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
		googleMap.animateCamera(camUpd3);
		MarkerOptions markerOpts = new MarkerOptions().position(myLaLn).title(name);
		googleMap.addMarker(markerOpts);
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
		final Dialog dialog = new Dialog(HistoryMapPage.this, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		cancelBtn.setVisibility(View.GONE);
		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//Crear  session
				SessionManager session = new SessionManager(HistoryMapPage.this);					
				session.logoutUser(HistoryMapPage.this);
				//Load login 
				Intent LoginIntent = new Intent(HistoryMapPage.this, Login.class);
				startActivity(LoginIntent);
				finish();
			}});
		dialog.show();
	}
	//Check Internet connection
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
}