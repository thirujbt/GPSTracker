package com.gpsmobitrack.gpstracker.MenuItems;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



//import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.InterfaceClass.TrackingInterface;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.TrackingService.HandlerManager;
import com.gpsmobitrack.gpstracker.TrackingService.TrackFriendService;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSTracker;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.chat.Config;
import com.gpsmobitrack.gpstracker.chat.ShowMessage;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tyczj.mapnavigator.Navigator;

public class HomeDetailPage extends FragmentActivity implements AsyncResponse, OnClickListener ,TrackingInterface{

	private GoogleMap googleMap;
	MarkerOptions marker;
	String lat,log, regid, userFirstName;
	Double latitude,longtitude;
	String trackUserID;
	String photoPath, email;
	int userPosition;
	DBHandler dbHandler;
	GPSTracker gps;
	public static HomeDetailPage homeDetailPage;
	TextView userLocTxt,timeTxt,dateTxt,userNameTxt,relationShipTxt;
	SharedPreferences pref;
	Editor editor;
	ImageView sourceBtn,destinationBtn,backButton,profileImgBtn,chatBtn;
	DisplayImageOptions options;
	ArrayList<Double> latitudeArrayList = new ArrayList<Double>();
	ArrayList<Double> longtitudeArrayList = new ArrayList<Double>();
	public static ArrayList<Double> stalatitudeArry = new ArrayList<Double>();
	public static ArrayList<Double> stalongitudeArry = new ArrayList<Double>();
	HandlerManager handMang;
	Handler srcClickHandler;
	Timer srcClickTimer;
	int i;
	boolean clearMarker = false;
	boolean isSrcTimerON = false;
	boolean testBoolean = false;
	public static boolean isTrackingON = false;
	public static boolean goneBackground = false;
	String profileImage;
	//private InterstitialAd interstitialAd = null;
	//double tempvalue = 0.0001;
	//Share location
	ImageView shareLocImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.home_detail_page);
		homeDetailPage=HomeDetailPage.this;
		gps = new GPSTracker(HomeDetailPage.this);
		pref = HomeDetailPage.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		userLocTxt = (TextView)findViewById(R.id.user_location_txt);
		timeTxt = (TextView)findViewById(R.id.time_txt);
		dateTxt = (TextView)findViewById(R.id.date_txt);
		userNameTxt = (TextView)findViewById(R.id.username_txt);
		backButton = (ImageView)findViewById(R.id.back_btn);
		relationShipTxt = (TextView)findViewById(R.id.user_relationship_txt);
		profileImgBtn = (ImageView)findViewById(R.id.user_profile_img);
		sourceBtn = (ImageView)findViewById(R.id.source_btn);
		sourceBtn.setBackgroundResource(R.drawable.source_btn);
		destinationBtn = (ImageView)findViewById(R.id.destination_btn);
		shareLocImg = (ImageView) findViewById(R.id.share_location_img); 
		destinationBtn.setBackgroundResource(R.drawable.destination_click);
		chatBtn = (ImageView)findViewById(R.id.chat_btn);
		chatBtn.setOnClickListener(this);
		destinationBtn.setClickable(false);
		sourceBtn.setOnClickListener(this);
		destinationBtn.setOnClickListener(this);
		backButton.setOnClickListener(this);
		shareLocImg.setOnClickListener(this);
		try {
			// Loading map
			initilizeMap();
		} catch (Exception e) {
			Utils.printLog("Map Excep", ""+e);
			e.printStackTrace();
		}
	
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
		//.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==2){
			testBoolean =data.getBooleanExtra("gcmCallback", true); 
			Utils.printLog("tag", ""+testBoolean);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();

		System.out.println("on resume in homedetail page...............");

		MyApplication.activityResumed();
		MyApplication.setActivityName(AppConstants.HOME_DETAIL_ACTIVITY);
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		if (b != null) {
			trackUserID = b.getString("trackuserID");
			userPosition = b.getInt(AppConstants.USER_LIST_POSITION_INTENT);
			regid = pref.getString(AppConstants.GCM_REGID_PREF, null);
			boolean isFromGCM = b.getBoolean(AppConstants.GCM_STATUS_INTENT);
			Utils.printLog("Gcm Status1", ""+isFromGCM);
			b.putBoolean(AppConstants.GCM_STATUS_INTENT, false);
			//	boolean isFromGCM = pref.getBoolean(AppConstants.GCM_FROM_PREF, false);
			Utils.printLog("Gcm Status2", ""+b.getBoolean(AppConstants.GCM_STATUS_INTENT));
			if(isInternetOn()){
				if(isFromGCM && !testBoolean){

					email = b.getString(AppConstants.GCM_EMAIL_TO_USER);
					userFirstName = b.getString(AppConstants.USER_FIRST_NAME);
					profileImage=b.getString("profileImage");
					String msg = b.getString(Config.EXTRA_MESSAGE);
					String regIdToUser = pref.getString(AppConstants.GCM_REGID_PREF, null);
					Intent i = new Intent(HomeDetailPage.this, ShowMessage.class);
					i.putExtra("regid", regid);
					i.putExtra(AppConstants.GCM_REGID_TOUSER, regIdToUser);
					i.putExtra(AppConstants.USER_FIRST_NAME, userFirstName);
					i.putExtra(AppConstants.GCM_TO_INTENT, true);
					i.putExtra(AppConstants.GCM_TO_SHOW_MSG, msg);
					i.putExtra(AppConstants.GCM_EMAIL_TO_USER, email);
					i.putExtra("oppuser", trackUserID);
					i.putExtra("profileImage", profileImage);
					startActivityForResult(i, 2);
				} else {
					final long updateFrequency = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
					if(userPosition == 0){
						chatBtn.setVisibility(View.GONE);
						if(isTrackingON){
							if(goneBackground){
								handMang = HandlerManager.getInstance(getApplicationContext());
								handMang.setonInterface(this);
								isSrcTimerON = true;
								sourceBtn.setBackgroundResource(R.drawable.source_click);
								sourceBtn.setClickable(false);
								destinationBtn.setClickable(true);
								destinationBtn.setBackgroundResource(R.drawable.destination_btn);
								getOwnUserData();
								try {
									// Loading map
									if(stalatitudeArry.size()>0){
										if( stalatitudeArry.size()>=2 && stalongitudeArry.size()>=2){
											for(int i=0;i<stalatitudeArry.size()-1;i++){
												//drawMarker(new LatLng(latitudeList.get(i), longtitudeList.get(i)));
												LatLng start = new LatLng(stalatitudeArry.get(i),stalongitudeArry.get(i));
												LatLng end = new LatLng(stalatitudeArry.get(i+1),stalongitudeArry.get(i+1));
												if(i==0){
													centerInLoc(start, "Location");
												}
												centerInLoc(end, "Location");
												Navigator navigator = new Navigator(googleMap, start, end);
												navigator.findDirections(true);
											}
										} else if(stalatitudeArry.size() ==1 && stalongitudeArry.size() ==1){
											LatLng start = new LatLng(stalatitudeArry.get(0),stalongitudeArry.get(0));
											centerInLoc(start, "Location");
										} 
										if(latitudeArrayList.size()>0) {
											latitudeArrayList.clear();
											longtitudeArrayList.clear();
										}
										clearMarker = true;
										latitudeArrayList = (ArrayList<Double>)stalatitudeArry.clone();
										longtitudeArrayList = (ArrayList<Double>)stalongitudeArry.clone();
									}
								} catch (Exception e) {
									Utils.printLog("Map Excep history", ""+e);
									e.printStackTrace();
								}
							}
						} else {
							if(HomeDetailPage.stalatitudeArry != null){
								if(stalatitudeArry.size()>0){
									stalatitudeArry.clear();
									stalongitudeArry.clear();
								}
							}
							HandlerManager.trackUserId = "";
							getOwnUserData();
						}
					} else {
						chatBtn.setVisibility(View.VISIBLE);
						if(isTrackingON){
							if(goneBackground){
								handMang = HandlerManager.getInstance(getApplicationContext());
								handMang.setonInterface(this);
								isSrcTimerON = true;
								sourceBtn.setBackgroundResource(R.drawable.source_click);
								sourceBtn.setClickable(false);
								destinationBtn.setClickable(true);
								destinationBtn.setBackgroundResource(R.drawable.destination_btn);
								String userid = pref.getString(AppConstants.USER_ID_PREF, null);
								BasicNameValuePair trackUserValue = new BasicNameValuePair("id", trackUserID);
								BasicNameValuePair authKeyValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
								List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
								detailPageList.add(authKeyValue);
								detailPageList.add(trackUserValue);
								new GpsAsyncTask(HomeDetailPage.this, AppConstants.USER_DETAIL_URL, AppConstants.USER_DETAIL_RESP_TWO, HomeDetailPage.this).execute(detailPageList);
								try {
									// Loading map
									if(stalatitudeArry.size()>0){
										if( stalatitudeArry.size()>=2 && stalongitudeArry.size()>=2){
											for(int i=0;i<stalatitudeArry.size()-1;i++){
												//drawMarker(new LatLng(latitudeList.get(i), longtitudeList.get(i)));
												LatLng start = new LatLng(stalatitudeArry.get(i),stalongitudeArry.get(i));
												LatLng end = new LatLng(stalatitudeArry.get(i+1),stalongitudeArry.get(i+1));
												if(i==0){
													centerInLoc(start, "Location");
												}
												centerInLoc(end, "Location");
												Navigator navigator = new Navigator(googleMap, start, end);
												navigator.findDirections(true);
											}
										} else if(stalatitudeArry.size() ==1 && stalongitudeArry.size() ==1){
											LatLng start = new LatLng(stalatitudeArry.get(0),stalongitudeArry.get(0));
											centerInLoc(start, "Location");
										} 
										if(latitudeArrayList.size()>0) {
											latitudeArrayList.clear();
											longtitudeArrayList.clear();
										}
										clearMarker = true;
										latitudeArrayList = (ArrayList<Double>)stalatitudeArry.clone();
										longtitudeArrayList = (ArrayList<Double>)stalongitudeArry.clone();
									}
								} catch (Exception e) {
									Utils.printLog("Map Excep history", ""+e);
									e.printStackTrace();
								}
							}
						} else {
							if(HomeDetailPage.stalatitudeArry != null){
								if(stalatitudeArry.size()>0){
									stalatitudeArry.clear();
									stalongitudeArry.clear();
								}
							}
							HandlerManager.trackUserId = "";
							if(srcClickTimer == null){
								if(!isSrcTimerON){
									srcClickHandler = new Handler();
									srcClickTimer = new Timer();
									TimerTask timer = new TimerTask() {
										@Override
										public void run() {
											srcClickHandler.post(new Runnable() {
												@Override
												public void run() {
													String userid = pref.getString(AppConstants.USER_ID_PREF, null);
													BasicNameValuePair trackUserValue = new BasicNameValuePair("id", trackUserID);
													BasicNameValuePair authKeyValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
													List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
													detailPageList.add(authKeyValue);
													detailPageList.add(trackUserValue);
													new GpsAsyncTask(HomeDetailPage.this, AppConstants.USER_DETAIL_URL, AppConstants.USER_DETAIL_RESP, HomeDetailPage.this).execute(detailPageList);
												}
											});
										}
									};
									srcClickTimer.schedule(timer, 0, 1000 * 60 * updateFrequency);
								}
							}
						}
					}
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}
	}
	public void onPause() {
		super.onPause();
		MyApplication.activityPaused();
		MyApplication.setActivityName(AppConstants.EMPTY_ACTIVITY);
		handMang = HandlerManager.getInstance(getApplicationContext());
		handMang.setonInterface(null);
		if(isTrackingON){
			goneBackground = true;
		}
		Utils.printLog("on Pause", "On Pause");
	}
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public void onClick(View v) {
		if(v == backButton){
			if(srcClickTimer != null){
				srcClickTimer.cancel();
				srcClickHandler = null;
				srcClickTimer = null;
			}
			finish();
		} else if (v == sourceBtn) {
			if(isInternetOn()){
				if(!isTrackingON){
					if(srcClickTimer != null){
						srcClickTimer.cancel();
						srcClickHandler = null;
						srcClickTimer = null;
					}
					final String userid = pref.getString(AppConstants.USER_ID_PREF, null);
					final Calendar calender = Calendar.getInstance();
					final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
					final long updateFrequency = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
					String date = sdfDate.format(calender.getTime());
					String time = sdfTime.format(calender.getTime());
					if( userPosition == 0){
						trackUserID = pref.getString(AppConstants.USER_KEY_PREF, null);
					}
					BasicNameValuePair trackUserValue = new BasicNameValuePair("id", trackUserID);
					BasicNameValuePair authKeyValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
					BasicNameValuePair destinationValue = new BasicNameValuePair(AppConstants.DESTINATION, date+" "+time);
					List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
					detailPageList.add(authKeyValue);
					detailPageList.add(trackUserValue);
					detailPageList.add(destinationValue);
					new GpsAsyncTask(HomeDetailPage.this, AppConstants.SOURCE_DESTINATION_URL, AppConstants.SOURCE_DESTINATION_RESP, HomeDetailPage.this).execute(detailPageList);
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		} else if (v == destinationBtn) {
			isTrackingON = false;
			goneBackground = false;
			HandlerManager.trackUserId = "";
			handMang =HandlerManager.getInstance(getApplicationContext());
			handMang.setonInterface(null);
			AlarmManager alarm = (AlarmManager)HomeDetailPage.this.getSystemService(Context.ALARM_SERVICE);
			Intent intent2 = new Intent(HomeDetailPage.this, TrackFriendService.class);
			PendingIntent pintent = PendingIntent.getService(HomeDetailPage.this, 0, intent2, 0);
			if(PendingIntent.getService(HomeDetailPage.this, 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
				alarm.cancel(pintent);
			}
			destinationBtn.setBackgroundResource(R.drawable.destination_click);
			destinationBtn.setClickable(false);
			sourceBtn.setClickable(true);
			sourceBtn.setBackgroundResource(R.drawable.source_btn);
		} else if (v == chatBtn) {
			if(isInternetOn()){ 
				if(userPosition != 0){
					Intent i = new Intent(HomeDetailPage.this, ShowMessage.class);
					i.putExtra(AppConstants.REG_ID, regid);
					i.putExtra(AppConstants.USER_FIRST_NAME, userFirstName);
					i.putExtra(AppConstants.TRACK_USERID_INTENT, trackUserID);
					i.putExtra("toGCM", false);
					i.putExtra(AppConstants.GCM_EMAIL_TO_USER, email);
					i.putExtra("oppuser", trackUserID);
					i.putExtra("profileImage", profileImage);
					startActivity(i);
				} 
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}else if(v == shareLocImg){
			//shareLocation();
			shareLcon();
		}
	}
	private void shareLcon(){
		String city = "Not Available";		
		Double lat = gps.getLatitude();
		Double lon = gps.getLongitude();
		//LatLng start = new LatLng(lat, lon);
		if(lat != null && lon != null){
			if(!lat.equals("0.0") && !lon.equals("0.0")){
				Geocoder geo;
				List<Address> cityData = null;
				geo = new Geocoder(HomeDetailPage.this, Locale.ENGLISH);
				try {
					cityData = geo.getFromLocation(lat,lon, 1);
					if (cityData != null && cityData.size() > 0) {
						Address addr = cityData.get(0);
						city = addr.getLocality();
					}
				} catch (IOException e) {
					city = "Not Available";
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e){
				}
			}else{
				city = "Not Available";
			}
		}else{
			city = "Not Available";
		}
		String appURL = "https://play.google.com/store/apps/details?id=" + getPackageName();
		String mapImgURL = "http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+
				"&zoom=12&size=600x400&sensor=false&markers=color:blue|label:|"
				+lat+","+lon;
		ArrayList<String> filesToSend = new ArrayList<String>();
		filesToSend.add(appURL);
		filesToSend.add(mapImgURL);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.="+city);
		intent.putExtra(Intent.EXTRA_TEXT, appURL);
		intent.putExtra(Intent.EXTRA_TEXT, mapImgURL);
		intent.setType("text/plain"); /* This example is sharing jpeg images. */
		ArrayList<Uri> files = new ArrayList<Uri>();
		for(String path : filesToSend /* List of the files you want to send */) {
			File file = new File(path);	
			Uri uri = Uri.fromFile(file);
			files.add(uri);
		}
		//intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		startActivity( Intent.createChooser( intent,"GPS Phone Tracker Pro"));
	}
	@SuppressWarnings("unused")
	private void shareLocation(){
		String city = "Not Available";		
		Double lat = gps.getLatitude();
		Double lon = gps.getLongitude();
		//LatLng start = new LatLng(lat, lon);
		if(lat != null && lon != null){
			if(!lat.equals("0.0") && !lon.equals("0.0")){
				Geocoder geo;
				List<Address> cityData = null;
				geo = new Geocoder(HomeDetailPage.this, Locale.ENGLISH);
				try {
					cityData = geo.getFromLocation(lat,lon, 1);
					if (cityData != null && cityData.size() > 0) {
						Address addr = cityData.get(0);
						city = addr.getLocality();
					}
				} catch (IOException e) {
					city = "Not Available";
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e){
				}
			}else{
				city = "Not Available";
			}
		}else{
			city = "Not Available";
		}
		String appURL = "https://play.google.com/store/apps/details?id=" + getPackageName();
		String mapImgURL = "geo:" + lat + ","
				+lon + "?q=" + lat
				+ "," + lon;
		/*String mapImgURL = "http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+
				"&zoom=12&size=600x400&sensor=false&markers=color:blue|label:|"
				+lat+","+lon;*/
		/*	Intent shareIntent = new Intent();
	     shareIntent.setAction(Intent.ACTION_SEND);
	     shareIntent.putExtra(Intent.EXTRA_SUBJECT, "GPS Phone Tracker My location "+city);
	     shareIntent.putExtra(Intent.EXTRA_,mapImgURL);
	     shareIntent.setType("text/plain");
	     startActivity(shareIntent);*/
		//shareIntent.putExtra(Intent.EXTRA_TEXT, appURL);
		//shareIntent.putExtra(Intent.EXTRA_,mapImgURL);
		//shareIntent.putExtra(Intent.EXTRA_TEXT,mapImgURL);  //optional//use this when you want to send an image
		//shareIntent.setType("text/plain");
		/*startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                 Uri.parse(uri)));*/
		//startActivity(shareIntent);
		//shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(filePath)));  //optional//use this when you want to send an image
		//shareIntent.setType("image/jpeg");text/plain
		// shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		//startActivity(Intent.createChooser(shareIntent, "send"));
		// Double latitude = user_loc.getLatitude();
		//    Double longitude = user_loc.getLongitude();
		String uri = "http://maps.google.com/maps?saddr=" +lat+","+lon;
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String ShareSub = "Here is my location";
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	private void getOwnUserData(){
		String userName = null;
		dbHandler = new DBHandler(HomeDetailPage.this);
		Cursor cursor = dbHandler.getProfile();
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			do {
				userName = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_FIRST_NAME));
				photoPath = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHOTO));
			} while (cursor.moveToNext());
		}
		String city = null;
		Double lat = gps.getLatitude();
		Double lon = gps.getLongitude();
		LatLng start = new LatLng(lat, lon);
		try{if(!isTrackingON){
			centerInLoc(start,userName);
		}}catch(Exception ex){ex.printStackTrace();}
		if(lat != null && lon != null){
			if(!lat.equals("0.0") && !lon.equals("0.0")){
				Geocoder geo;
				List<Address> cityData = null;
				geo = new Geocoder(HomeDetailPage.this, Locale.ENGLISH);
				try {
					cityData = geo.getFromLocation(lat,lon, 1);
					Address addr = cityData.get(0);
					city = addr.getLocality();
				} catch (IOException e) {
					city = "Not Available";
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e){
				}
			}else{
				city = "Not Available";
			}
		}else{
			city = "Not Available";
		}
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		String date = sdfDate.format(calender.getTime());
		String time = sdfTime.format(calender.getTime());
		String upperString="";
		if(userName!=null && !userName.equalsIgnoreCase("") ){
			upperString = userName.substring(0,1).toUpperCase() + userName.substring(1);
		}
		userNameTxt.setText(upperString);
		userLocTxt.setText(city);
		timeTxt.setText(time);
		dateTxt.setText(date);
		relationShipTxt.setText("Self");
		loadProfileImage(photoPath, profileImgBtn);
		
	

	}
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap =  ( (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_detail_page)).getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			// check if map is created successfully or not
			if (googleMap == null) {
				Utils.showToast("Sorry! unable to create maps");
			}
		}
	}
	private void showAlert(String alertMsg, String title){
		final Dialog dialog = new Dialog(HomeDetailPage.this, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		dialog.setContentView(R.layout.alert_dialog);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_dialog_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_dialog_cancel_btn);
		cancelBtn.setVisibility(View.GONE);
		TextView titleTxt = (TextView) dialog.findViewById(R.id.alert_dialog_title);
		TextView msg = (TextView)dialog.findViewById(R.id.alert_dialog_txt);
		msg.setText(alertMsg);
		if(title.length() > 0){
			titleTxt.setText(Html.fromHtml("<b>" + title + "<b>"));
		}
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		Utils.printLog("Home Resp", "" + serverResp);
		try {
			if (serverResp != null) {
				JSONObject jObjMain = new JSONObject(serverResp);
				String statusCode = jObjMain.getString(AppConstants.STATUS_CODE);
				switch(RespValue) {
				case AppConstants.USER_DETAIL_RESP:
					if (statusCode.equals(AppConstants.NEW_SUCCESS)) {
						googleMap.clear();
						Utils.printLog("Log 1", "1");
						JSONObject jObj = jObjMain.getJSONObject("userdetails");
						userFirstName = jObj.getString("firstname");
						String lat = jObj.getString("latitude");
						String lon = jObj.getString("longitude");
						String date = jObj.getString("date");
						String time = jObj.getString("time");
						regid = jObj.getString("gcm_regid");
						email = jObj.getString("emailid");
						//	email = "muhsin.k201@pickzy.com";
						//String gcmRegid = jObj.getString("gcm_regid");
						String relationShip = jObj.getString(AppConstants.RELATIONSHIP);
						profileImage = jObj.getString(AppConstants.PROF_IMG);

						/*editor = pref.edit();
						editor.putString("profileImage", profileImage);
						editor.commit();*/

						String city = "Not Available";
						try {
							if ((!lat.isEmpty()) && (!lon.isEmpty())) {
								if(!lat.equals("0.0") && !lon.equals("0.0")){
									latitude = Double.parseDouble(lat);
									longtitude = Double.parseDouble(lon);
									LatLng Start = new LatLng(latitude, longtitude);
									centerInLoc(Start, userFirstName);
									Utils.printLog("LA , LO",lat+","+lon );
									Geocoder geo;
									List<Address> cityData;
									geo = new Geocoder(HomeDetailPage.this, Locale.ENGLISH);
									cityData = geo.getFromLocation((Double.parseDouble(lat)),(Double.parseDouble(lon)), 1);
									if (cityData != null && cityData.size() > 0) {
										Address addr = cityData.get(0);
										city = addr.getLocality();
									}
								}else{
									city = "Not Available";
								}
							}else{
								city = "Not Available";
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						Utils.printLog("City", "" + city);
						String upperString="";
						if(userFirstName!=null && !userFirstName.equalsIgnoreCase("") ){
							upperString = userFirstName.substring(0,1).toUpperCase() + userFirstName.substring(1);
						}
						userNameTxt.setText(upperString);
						userLocTxt.setText(city);
						timeTxt.setText(time);
						dateTxt.setText(date);
						relationShipTxt.setText(relationShip);
						loadProfileImage(profileImage, profileImgBtn);
					} else if(statusCode .equalsIgnoreCase(AppConstants.NEW_FAILED)){
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)){
						Utils.showToast("Unable to fetch Records.Please Try Later!!");
					}
					break;
				case AppConstants.SOURCE_DESTINATION_RESP:
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
						isSrcTimerON = true;
						String verifiedStatus = jObjMain.getString("verified_status");
						//		verifiedStatus = "1";
						if(verifiedStatus.equals("1")){
							sourceBtn.setBackgroundResource(R.drawable.source_click);
							sourceBtn.setClickable(false);
							destinationBtn.setClickable(true);
							destinationBtn.setBackgroundResource(R.drawable.destination_btn);
							if(isInternetOn()){ 
								if(!isTrackingON) {
									isTrackingON = true;
									handMang = HandlerManager.getInstance(getApplicationContext());
									handMang.startTrackLocation(trackUserID);
									handMang.setonInterface(this);
									if (!clearMarker) {
										googleMap.clear();
										Utils.printLog("Log 2", "2");
										clearMarker = true;
									}
								}
							} else {
								Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
							}
						} else {
							showAlert(AppConstants.ALERT_MSG_NOT_VERIFIED_USER, "Alert");
						}
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
						Geocoder geo;
						List<Address> cityData;
						geo = new Geocoder(HomeDetailPage.this,Locale.ENGLISH);
						try {
							cityData = geo.getFromLocation((Double.parseDouble(lat)),(Double.parseDouble(lon)), 1);
						} catch (Exception e) {
							cityData = null;
						}
						String city = "Not Available";
						if((!lat.isEmpty()) && (!lon.isEmpty())) {
							latitude = Double.parseDouble(lat);
							longtitude = Double.parseDouble(lon);
							/*
							latitude = latitude-tempvalue;
							longtitude = longtitude-tempvalue;
							tempvalue = tempvalue+tempvalue;*/
							if(latitudeArrayList.size() > 0 && longtitudeArrayList.size() > 0) {
								if((latitudeArrayList.get(latitudeArrayList.size() - 1).doubleValue() == latitude) && 
										(longtitudeArrayList.get(longtitudeArrayList.size() - 1).doubleValue() == longtitude)) { 
									latitudeArrayList.remove(latitudeArrayList.size() -1);
									longtitudeArrayList.remove(longtitudeArrayList.size()-1);
									latitudeArrayList.add(latitude);
									longtitudeArrayList.add(longtitude);
								} else{
									latitudeArrayList.add(latitude);
									longtitudeArrayList.add(longtitude);
								}
							} else {
								latitudeArrayList.add(latitude);
								longtitudeArrayList.add(longtitude);
							}
							if (cityData != null && cityData.size() > 0) {
								Address addr = cityData.get(0);
								city = addr.getLocality();
							}else{
								city = "Not Available";
							}
						}else{
							//city = "Not Available";
							Utils.printLog("LatLng", "Empty");
						}
						if(!clearMarker){
							googleMap.clear();
							Utils.printLog("Log 3", "3");
							clearMarker = true;
						}
						if(latitudeArrayList.size() > 1 && longtitudeArrayList.size() > 1) {
							for(int i=0;i<latitudeArrayList.size()-1;i++){
								//drawMarker(new LatLng(latitudeList.get(i), longtitudeList.get(i)));
								LatLng start = new LatLng(latitudeArrayList.get(i),
										longtitudeArrayList.get(i));
								LatLng end = new LatLng(latitudeArrayList.get(i+1),
										longtitudeArrayList.get(i+1));
								if(i==0){
									centerInLoc(start, city);
								}
								centerInLoc(end, city);
								Navigator navigator = new Navigator(googleMap, start, end);
								navigator.findDirections(true);
								Utils.printLog("LAT and LON history", latitudeArrayList.get(i) +"," + longtitudeArrayList.get(i));
							}
						}else if(latitudeArrayList.size() ==1 && longtitudeArrayList.size() == 1){
							LatLng start = new LatLng(latitudeArrayList.get(0),
									longtitudeArrayList.get(0));
							centerInLoc(start, city);
						}else {
							Utils.showToast("No location");
						}
					}  else if(statusCode .equalsIgnoreCase(AppConstants.NEW_FAILED)){
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)){
						Utils.showToast("Invalid Track id or Destination");
					} else if(statusCode.equalsIgnoreCase(AppConstants.NOT_AVAILABLE_IN_TRACKLIST)) {
						Utils.showToast("Invalid Track id");
					} else if(statusCode.equalsIgnoreCase(AppConstants.NO_RECOD_FOUND_SRC)){
						Utils.showToast("No Record Found");
					}
					if(latitudeArrayList.size()>0 && longtitudeArrayList.size()>0){
						if(stalatitudeArry.size()>0){
							stalatitudeArry.clear();
							stalongitudeArry.clear();
						}
						stalatitudeArry = (ArrayList<Double>)latitudeArrayList.clone();
						stalongitudeArry = (ArrayList<Double>)longtitudeArrayList.clone();
					}
					/*if (alertProgressDialog != null && alertProgressDialog.isShowing()) {
						alertProgressDialog.dismiss();
						alertProgressDialog = null;
					}
					 */
					break;
				case AppConstants.USER_DETAIL_RESP_TWO:
					if (statusCode.equals(AppConstants.NEW_SUCCESS)) {
						JSONObject jObj = jObjMain.getJSONObject("userdetails");
						userFirstName = jObj.getString("firstname");
						String lat = jObj.getString("latitude");
						String lon = jObj.getString("longitude");
						String date = jObj.getString("date");
						String time = jObj.getString("time");
						regid = jObj.getString("gcm_regid");
						email = jObj.getString("emailid");
						String relationShip = jObj.getString(AppConstants.RELATIONSHIP);
						profileImage = jObj.getString(AppConstants.PROF_IMG);

						/*editor = pref.edit();
						editor.putString("profileImage", profileImage);
						editor.commit();*/
						String city = "Not Available";
						try {
							if ((!lat.isEmpty()) && (!lon.isEmpty())) {
								if(!lat.equals("0.0") && !lon.equals("0.0")){
									latitude = Double.parseDouble(lat);
									longtitude = Double.parseDouble(lon);
									LatLng Start = new LatLng(latitude, longtitude);
									Utils.printLog("LA , LO",lat+","+lon );
									Geocoder geo;
									List<Address> cityData;
									geo = new Geocoder(HomeDetailPage.this, Locale.ENGLISH);
									cityData = geo.getFromLocation((Double.parseDouble(lat)),(Double.parseDouble(lon)), 1);
									if (cityData != null && cityData.size() > 0) {
										Address addr = cityData.get(0);
										city = addr.getLocality();
									}
								}else{
									city = "Not Available";
								}
							}else{
								city = "Not Available";
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						Utils.printLog("City", "" + city);
						String upperString="";
						if(userFirstName!=null && !userFirstName.equalsIgnoreCase("") ){
							upperString = userFirstName.substring(0,1).toUpperCase() + userFirstName.substring(1);
						}
						userNameTxt.setText(upperString);
						userLocTxt.setText(city);
						timeTxt.setText(time);
						dateTxt.setText(date);
						relationShipTxt.setText(relationShip);
						loadProfileImage(profileImage, profileImgBtn);
					} else if(statusCode .equalsIgnoreCase(AppConstants.NEW_FAILED)){
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)){
						Utils.showToast("Unable to fetch Records.Please Try Later!!");
					}
					break;
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_RESPONSE);
			}
		} catch (JSONException e) {
			Utils.printLog("JSON Excep", "" + e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	public void loadProfileImage(final String profileImgURL, ImageView view){
		ImageLoader.getInstance().displayImage(profileImgURL, view,options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}
			@Override
			public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
				final ImageView imageView = (ImageView) view;
				ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
				observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						imageView.getViewTreeObserver().removeOnPreDrawListener(this);
						if(profileImgURL != null && ! profileImgURL.equals("null") && !profileImgURL.equalsIgnoreCase("")){
							Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
						}
						return true;
					}
				});
			}
		});
	}
	public void onStop() {
		super.onStop();
	}
	public void onDestroy() {
		super.onDestroy();
		homeDetailPage=null;
		if(srcClickTimer != null){
			srcClickTimer.cancel();
			srcClickHandler = null;
			srcClickTimer = null;
		}
		destinationBtn.setBackgroundResource(R.drawable.destination_click);
		destinationBtn.setClickable(false);
		sourceBtn.setClickable(true);
		sourceBtn.setBackgroundResource(R.drawable.source_btn);
	}
	private void centerInLoc( LatLng myLaLn, String name) {
		//LatLng myLaLn = new LatLng(lat,lon);
		CameraPosition camPos = new CameraPosition.Builder().target(myLaLn).zoom(15).bearing(45).tilt(0).build();
		try{
			CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
			googleMap.animateCamera(camUpd3);
			MarkerOptions markerOpts = new MarkerOptions().position(myLaLn).title(name);
			googleMap.addMarker(markerOpts);    
		}
		catch(Exception ex){
			try{
				CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
				googleMap.animateCamera(camUpd3);
				MarkerOptions markerOpts = new MarkerOptions().position(myLaLn).title(name);
				googleMap.addMarker(markerOpts);    
			}catch(Exception exception){}
		}
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
		final Dialog dialog = new Dialog(HomeDetailPage.this, android.R.style.Theme_Translucent);
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
				//Clear  session
				SessionManager session = new SessionManager(HomeDetailPage.this);					
				session.logoutUser(HomeDetailPage.this);
				//Load login 
				Intent LoginIntent = new Intent(HomeDetailPage.this, Login.class);
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
	@Override
	public void interfaceResp(String serverResp, int RespValue) {
		if(isTrackingON){
			if(MyApplication.isActivityVisible() && MyApplication.getActivityName().equalsIgnoreCase(AppConstants.HOME_DETAIL_ACTIVITY)){
				Activity activity = (Activity) HomeDetailPage.this;
				if(!activity.isFinishing()){
					Utils.printLog("Log1","true");
					/*if (alertProgressDialog == null)
							alertProgressDialog = new Dialog(HomeDetailPage.this,android.R.style.Theme_Translucent);
						alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertProgressDialog.setContentView(R.layout.custom_progressbar);
						alertProgressDialog.setCancelable(false);
						alertProgressDialog.show();*/
				} else {
					Utils.printLog("Log1","false");
				}
				Utils.showToast("Refreshing Map");
				onProcessFinish(serverResp, RespValue);
				goneBackground = false;
			} else {
				goneBackground = true;
			}
		}
	}
	/**
	 * Get user type from SharedPreference
	 * 
	 * @return return user type
	 */
	@SuppressWarnings("unused")
	private int getSharPrefUserType(){
		/*Log.d("Home Details","UY="+pref.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus()));
		return pref.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus());*/
		return SessionManager.getPurchaseSharePreference();
	}
}