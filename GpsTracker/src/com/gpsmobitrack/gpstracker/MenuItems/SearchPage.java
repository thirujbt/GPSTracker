package com.gpsmobitrack.gpstracker.MenuItems;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncJSON;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSTracker;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Search Page for find our friends by email id and phone number or invite
 * friend by email id.
 *
 */
public class SearchPage extends Fragment implements OnClickListener, AsyncResponse {

	public static final int CONTACT_RESULT_RESP = 11000; 
	Button searchBtn,inviteuser,inviteusernonmap;
	AutoCompleteTextView searchAutoTxt;
	SharedPreferences pref;
	String statusResp,msgResp,userId,statusCode,contact;
	TextView userNametxt,locationTxt,relationTxt,userNametxtnew,userNametxtnewnonmap;
	ImageView mapImg,profileImg,profileImgnew,mapImgNew,profileImgnewnonmap;
	public ImageLoader1 imageLoader;
	DisplayImageOptions options;
	RelativeLayout relativeMain,relativeMainnew,relativemap;
	GPSTracker gps;
	DBHandler dbHandler;
	TextView dateTxt,timeTxt;
	String newInviteemail ;
	Button pickPhnNoBtn;
	boolean profilePrivacy;
	//Store datas for 
	private String contactPhoneNo = null;
	private String contactEmailAdrs = null;
	Fragment currenrFragment = null;
	MarkerOptions marker;
	MapView mMapView;
	private GoogleMap googleMap;
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
		//	.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		super.onCreate(savedInstanceState);
	}
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_page, null);

		gps = new GPSTracker(getActivity());
		dbHandler = new DBHandler(getActivity());
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		profilePrivacy=pref.getBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
		searchBtn = (Button)view.findViewById(R.id.searchbox_icon);
		inviteuser = (Button)view.findViewById(R.id.inviteuser);
		inviteusernonmap = (Button)view.findViewById(R.id.inviteuser_nonmap);
		searchAutoTxt = (AutoCompleteTextView)view.findViewById(R.id.search_edit_atxt);
		userNametxt = (TextView)view.findViewById(R.id.username_search);
		userNametxtnew = (TextView)view.findViewById(R.id.username_searchnew);
		userNametxtnewnonmap = (TextView)view.findViewById(R.id.username_searchnew_nonmap);
		locationTxt = (TextView)view.findViewById(R.id.user_location_txt);
		relationTxt = (TextView)view.findViewById(R.id.user_relation_txt);
		dateTxt = (TextView)view.findViewById(R.id.date_txt);
		timeTxt = (TextView)view.findViewById(R.id.time_txt);
		relativeMain = (RelativeLayout)view.findViewById(R.id.rr_main);
		relativeMainnew  = (RelativeLayout)view.findViewById(R.id.rr_mainnew);
		relativemap=(RelativeLayout)view.findViewById(R.id.rr_nonmap);
		profileImg = (ImageView)view.findViewById(R.id.profile_picture_search_page);
		profileImgnew = (ImageView)view.findViewById(R.id.profile_picture_search_pagenew);
		profileImgnewnonmap = (ImageView)view.findViewById(R.id.profile_picture_search_pagenew_nonmap);
		pickPhnNoBtn = (Button) view.findViewById(R.id.pick_phn_no_btn);

		imageLoader = new ImageLoader1(getActivity());
		relativeMain.setVisibility(View.INVISIBLE);
		searchBtn.setOnClickListener(SearchPage.this);
		inviteuser.setOnClickListener(SearchPage.this);
		inviteusernonmap.setOnClickListener(SearchPage.this);
		pickPhnNoBtn.setOnClickListener(this);

		searchAutoTxt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId,
					final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if(isInternetOn()){
						contact = searchAutoTxt.getText().toString().trim();
						String userMail = null;
						String userPhone = null;
						Cursor cursor = dbHandler.getProfile();
						if(cursor != null && cursor.getCount() > 0){
							cursor.moveToFirst();
							userMail = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID));
							userPhone = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHONE_NO));
						}
						if((contact != null )&& (!contact.isEmpty()) && (contact.length()>=6) ){
							if(contact.equalsIgnoreCase(userMail) || contact.equalsIgnoreCase(userPhone)){
								try{getOwnUserData();}catch(Exception ex){}
							} else if (Pattern.matches(AppConstants.EMAIL_ID_REGEX, contact)) {
								String url =AppConstants.SEARCH_URL;
								String userid = pref.getString("Userid", null);
								Utils.printLog("User Id",""+userid);
								BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
								BasicNameValuePair searchValue = new BasicNameValuePair(AppConstants.SEARCH, contact);
								List<NameValuePair> searchPageList = new ArrayList<NameValuePair>();
								searchPageList.add(useridValue);
								searchPageList.add(searchValue);
								new GpsAsyncTask(getActivity(), url,AppConstants.SEARCH_RESP, SearchPage.this).execute(searchPageList);
							} else if (!Pattern.matches(AppConstants.NON_ALPHABETS_REGERX, contact)){
								if(contact.length()<9){
									Utils.showToast("Enter Valid Phone no.");
								} else {
									String url =AppConstants.SEARCH_URL;
									String userid = pref.getString("Userid", null);
									Utils.printLog("User Id",""+userid);
									BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
									BasicNameValuePair searchValue = new BasicNameValuePair(AppConstants.SEARCH, contact);
									List<NameValuePair> searchPageList = new ArrayList<NameValuePair>();
									searchPageList.add(useridValue);
									searchPageList.add(searchValue);
									new GpsAsyncTask(getActivity(), url,AppConstants.SEARCH_RESP, SearchPage.this).execute(searchPageList);
								}
							} else {
								Utils.showToast("Enter Valid Email id or Phone no.");
							}
						}else{
							Utils.showToast("Enter Email id or Phone no.");
						}
					} else {
						Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
					}
				}
				return true;
			}
		});
		//Start AsyncTask for read user detail to auto complete search text view.  
		if(isInternetOn()){
			String url = AppConstants.TRACKLIST_URL;
			String userId = pref.getString(AppConstants.USER_ID_PREF, null);
			BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
			List<NameValuePair> trackPageList = new ArrayList<NameValuePair>();
			trackPageList.add(useridValue);
			Utils.printLog("Url", url);
			new GpsAsyncTask(getActivity(), url,AppConstants.SEARCH_USER_LIST_RESP, this).execute(trackPageList);
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONTACT_RESULT_RESP:
			// Result for phone contact
			//Utils.showToast("" + requestCode + "," + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				Uri returnUri = data.getData();
				Cursor cursor = getActivity().getContentResolver().query(
						returnUri, null, null, null, null);
				if (cursor.moveToNext()) {
					int columnIndex_ID = cursor
							.getColumnIndex(ContactsContract.Contacts._ID);
					String contactID = cursor.getString(columnIndex_ID);
					int columnIndex_HASPHONENUMBER = cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
					String stringHasPhoneNumber = cursor
							.getString(columnIndex_HASPHONENUMBER);
					if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
						Cursor cursorPhoneNo = getActivity()
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ "=" + contactID, null, null);
						// Get the first phone number
						if (cursorPhoneNo.moveToNext()) {
							int columnIndex_number = cursorPhoneNo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
							String stringPhNo = cursorPhoneNo.getString(columnIndex_number);
							stringPhNo = stringPhNo.replaceAll("\\s+",""); 
							searchAutoTxt.setText(stringPhNo);
							//Set global phone number for alert dialog
							contactPhoneNo = stringPhNo;
						}
						// Find Email Addresses
						contactEmailAdrs = null;
						Cursor cursorEmails = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID,null, null);
						while (cursorEmails.moveToNext()) 
						{
							//Set global email for alert dialog
							contactEmailAdrs = cursorEmails.getString(cursorEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						}
						Utils.printLog("Search Page", "="+contactEmailAdrs);
						cursorEmails.close();
					} else {
						Utils.showToast("NO Phone Number");
					}
				} else {
					Utils.showToast("No data");
				}
			}
			break;
		default:
			break;
		}
	}
	public String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
		if(cursor.moveToFirst()){;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.searchbox_icon){
			if(isInternetOn()){
				contact = searchAutoTxt.getText().toString().trim();
				String userMail = null;
				String userPhone = null;
				Cursor cursor = dbHandler.getProfile();
				if(cursor != null && cursor.getCount() > 0){
					cursor.moveToFirst();
					userMail = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID));
					userPhone = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHONE_NO));
				}
				if((contact != null )&& (!contact.isEmpty()) && (contact.length()>=6) ){
					if(contact.equalsIgnoreCase(userMail) || contact.equalsIgnoreCase(userPhone)){
						try{
							getOwnUserData();
						}catch(Exception ex){}
					} else if (Pattern.matches(AppConstants.EMAIL_ID_REGEX, contact)) {
						String url =AppConstants.SEARCH_URL;
						String userid = pref.getString("Userid", null);
						Utils.printLog("User Id",""+userid);
						BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
						BasicNameValuePair searchValue = new BasicNameValuePair(AppConstants.SEARCH, contact);
						List<NameValuePair> searchPageList = new ArrayList<NameValuePair>();
						searchPageList.add(useridValue);
						searchPageList.add(searchValue);
						new GpsAsyncTask(getActivity(), url,AppConstants.SEARCH_RESP, this).execute(searchPageList);
					} else if (!Pattern.matches(AppConstants.NON_ALPHABETS_REGERX, contact)){
						if(contact.length()<9){
							Utils.showToast("Enter Valid Phone no.");
						} else {
							String url =AppConstants.SEARCH_URL;
							String userid = pref.getString("Userid", null);
							Utils.printLog("User Id",""+userid);
							BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
							BasicNameValuePair searchValue = new BasicNameValuePair(AppConstants.SEARCH, contact);
							List<NameValuePair> searchPageList = new ArrayList<NameValuePair>();
							searchPageList.add(useridValue);
							searchPageList.add(searchValue);
							new GpsAsyncTask(getActivity(), url,AppConstants.SEARCH_RESP, this).execute(searchPageList);
						}
					} else {
						Utils.showToast("Enter Valid Email id or Phone no.");
					}
				}else{
					Utils.showToast("Enter Email id or Phone no.");
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}else if (v.getId() == R.id.inviteuser) {
			inviteUser(newInviteemail);
		}else if (v.getId() == R.id.inviteuser_nonmap) {
			inviteUser(newInviteemail);
		}else if(v.getId() == R.id.pick_phn_no_btn){
			//Open phone contact
			Intent contectIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(contectIntent, CONTACT_RESULT_RESP);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void getOwnUserData(){
		String userName = null,photoPath = null;
		Cursor cursor = dbHandler.getProfile();
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			do {
				userName = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_FIRST_NAME));
				photoPath = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHOTO));
			} while (cursor.moveToNext());
		}
		relativeMain.setVisibility(View.VISIBLE);
		relativeMainnew.setVisibility(View.GONE);
		relativemap.setVisibility(View.GONE);
		String city = "Not Available";
		Double lat = gps.getLatitude();
		Double lon = gps.getLongitude();
		if(lat != null && lon != null){
			if(!lat.equals("0.0") && !lon.equals("0.0")){
				Geocoder geo;
				List<Address> cityData = null;
				geo = new Geocoder(getActivity(), Locale.ENGLISH);
				try {
					cityData = geo.getFromLocation(lat,lon, 1);
					if (cityData != null && cityData.size() > 0) {
						Address addr = cityData.get(0);
						city = addr.getLocality();

						System.out.println("cityyyy.."+city);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				city = "Not Available";
			}
		}else{
			city = "Not Available";
		}
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		String date = sdfDate.format(calender.getTime());
		String time = sdfTime.format(calender.getTime());
		userNametxt.setText(userName);
		locationTxt.setText(city);
		relationTxt.setText("Self");
		dateTxt.setText(date);
		timeTxt.setText(time);
		searchAutoTxt.setText("");
		LatLng latlon=new LatLng(lat, lon);
		initilizeMap();
		centerInLoc(latlon);
		/*String mapImgURL = "http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+
				"&zoom=12&size=600x400&sensor=false&markers=color:blue|label:|"
				+lat+","+lon;*/
		Utils.printLog("photo", photoPath);
		loadProfileImage(photoPath, profileImg);
		//imageLoader.displayImage(mapImgURL, getActivity(), mapImg);
		hideSoftKeyboard(getActivity());
	}
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		String profileState=String.valueOf(profilePrivacy);
		if(serverResp != null){
			if(RespValue == AppConstants.SEARCH_RESP){
				try {
					Utils.printLog("Search Resp", ""+serverResp);
					JSONObject jObjServerResp = new JSONObject(serverResp);
					String statusMsg = jObjServerResp.getString(AppConstants.STATUS);
					String statusCode = jObjServerResp.getString(AppConstants.STATUS_CODE);
					if(statusMsg.equals("success")){
						if(statusCode.equals(AppConstants.NEW_SUCCESS)){
							relativeMain.setVisibility(View.VISIBLE);
							relativeMainnew.setVisibility(View.GONE);
							relativemap.setVisibility(View.GONE);
							String firstname = jObjServerResp.getString("firstname");
							String profImgURL = jObjServerResp.getString("prof_image_path");


							String relationship = jObjServerResp.getString("relationship");
							String latitude = jObjServerResp.getString("latitude");
							String longitude = jObjServerResp.getString("longitude");
							String date = jObjServerResp.getString("date");
							String time = jObjServerResp.getString("time");
							String city = null;
							if ((!latitude.isEmpty()) && (!longitude.isEmpty())){
								if((!latitude.equals("0.0") && (!longitude.equals("0.0")))){
									Geocoder geo;
									geo = new Geocoder(getActivity(), Locale.ENGLISH);
									List<Address> cityData;
									cityData = geo.getFromLocation((Double.parseDouble(latitude)),(Double.parseDouble(longitude)), 1);
									try {
										Address addr = cityData.get(0);
										city = addr.getLocality();
									} catch (Exception e) {
										city = "Not Available";
									}
								} else {
									city = "Not Available";
								}
							} else {
								city = "Not Available";
							}
							userNametxt.setText(firstname);
							locationTxt.setText(city);
							relationTxt.setText(relationship);
							dateTxt.setText(date);
							timeTxt.setText(time);
							searchAutoTxt.setText("");
							double lat=Double.parseDouble(latitude);
							double lon=Double.parseDouble(longitude);
							LatLng latlon=new LatLng(lat, lon);
							initilizeMap();
							centerInLoc(latlon);
							/*String mapImgURL = "http://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+
									"&zoom=12&size=600x400&sensor=false&markers=color:blue|label:|"
									+latitude+","+longitude;*/
							ImageLoader.getInstance().displayImage(profImgURL, profileImg,options, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri, View view) {
								}
								@Override
								public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								}
								@Override
								public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
									Utils.printLog("Load complete", "Load complete");
									final ImageView imageView = (ImageView) view;
									/*Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
									imageView.setImageBitmap(bitmap);*/

									ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
									observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
										@Override
										public boolean onPreDraw() {
											imageView.getViewTreeObserver().removeOnPreDrawListener(this);

											if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
												Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
												imageView.setImageBitmap(bitmap);
												//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
											}
											return true;
										}
									});
								}
							});
							//	ImageLoader.getInstance().displayImage(mapImgURL, mapImg);
						}
					}
					if(statusMsg.equals("failed")){
						relativeMain.setVisibility(View.GONE);
						relativemap.setVisibility(View.GONE);
						relativeMainnew.setVisibility(View.GONE);
						if(statusCode.equals(AppConstants.NO_RECORD_FOUND)){
							showSearchAlert();
						}else if(statusCode.equals(AppConstants.NOT_AVAILABLE_IN_TRACKLIST)){
							JSONObject jsonObj = new JSONObject();
							jsonObj = jObjServerResp.getJSONObject("data");
							if(jsonObj.getString("count").equalsIgnoreCase("0")){
								showSearchAlert();
							}else if(jsonObj.getString("profileStatus").equalsIgnoreCase("true")){
								if(profileState.equalsIgnoreCase("true")){
									relativeMainnew.setVisibility(View.VISIBLE);
									String firstnamenew=	jsonObj.getString("firstname");
									newInviteemail = jsonObj.getString("emailid");
									String profpath = jsonObj.getString("prof_image_path");
									String latitude = jsonObj.getString("latitude");
									String longitude = jsonObj.getString("longitude");
									userNametxtnew.setText(firstnamenew);
									searchAutoTxt.setText("");
									double lat=Double.parseDouble(latitude);
									double lon=Double.parseDouble(longitude);
									LatLng latlon=new LatLng(lat, lon);
									initilizeMapNew();
									centerInLoc(latlon);

									ImageLoader.getInstance().displayImage(profpath, profileImgnew,options, new SimpleImageLoadingListener() {
										@Override
										public void onLoadingStarted(String imageUri, View view) {
										}
										@Override
										public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
										}
										@Override
										public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
											Utils.printLog("Load complete", "Load complete");
											final ImageView imageView = (ImageView) view;
											/*Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
											imageView.setImageBitmap(bitmap);*/

											ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
											observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
												@Override
												public boolean onPreDraw() {
													imageView.getViewTreeObserver().removeOnPreDrawListener(this);

													if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
														Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
														imageView.setImageBitmap(bitmap);
														//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
													}
													return true;
												}
											});
										}
									});
								}else{
									relativemap.setVisibility(View.VISIBLE);
									relativeMainnew.setVisibility(View.GONE);
									String firstnamenew=	jsonObj.getString("firstname");
									newInviteemail = jsonObj.getString("emailid");
									String profpath = jsonObj.getString("prof_image_path");
									userNametxtnewnonmap.setText(firstnamenew);
									searchAutoTxt.setText("");
									ImageLoader.getInstance().displayImage(profpath, profileImgnewnonmap,options, new SimpleImageLoadingListener() {
										@Override
										public void onLoadingStarted(String imageUri, View view) {

										}
										@Override
										public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
										}
										@Override
										public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
											Utils.printLog("Load complete", "Load complete");
											final ImageView imageView = (ImageView) view;
											/*Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
											imageView.setImageBitmap(bitmap);*/

											ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
											observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
												@Override
												public boolean onPreDraw() {
													imageView.getViewTreeObserver().removeOnPreDrawListener(this);

													if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
														Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
														imageView.setImageBitmap(bitmap);
														//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
													}
													return true;
												}
											});
										}
									});
								}
							}else {
								relativemap.setVisibility(View.VISIBLE);
								relativeMainnew.setVisibility(View.GONE);
								String firstnamenew=	jsonObj.getString("firstname");
								newInviteemail = jsonObj.getString("emailid");
								String profpath = jsonObj.getString("prof_image_path");
								userNametxtnewnonmap.setText(firstnamenew);
								searchAutoTxt.setText("");
								ImageLoader.getInstance().displayImage(profpath, profileImgnewnonmap,options, new SimpleImageLoadingListener() {
									@Override
									public void onLoadingStarted(String imageUri, View view) {
									}
									@Override
									public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
									}
									@Override
									public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
										Utils.printLog("Load complete", "Load complete");
										final ImageView imageView = (ImageView) view;
										/*Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
										imageView.setImageBitmap(bitmap);*/

										ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
										observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
											@Override
											public boolean onPreDraw() {
												imageView.getViewTreeObserver().removeOnPreDrawListener(this);

												if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
													Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
													imageView.setImageBitmap(bitmap);
													//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
												}
												return true;
											}
										});
									}
								});
							}
						}else if (statusCode.equals(AppConstants.NEW_FAILED)) {
							showInvalidUserDialog(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
						}
					}
				} catch (JSONException e) {
					Utils.printLog("JSON Excep", ""+e);
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			} else if (RespValue == AppConstants.SEARCH_INVITE_RESP) {
				try {
					JSONArray array = new JSONArray(serverResp);
					JSONObject responseObject = array.getJSONObject(0);
					if(responseObject.has(AppConstants.MESSAGE)){
						//	Toast.makeText(getActivity(),responseObject.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
						//Utils.showToast(responseObject.getString(AppConstants.MESSAGE));
						Utils.showToast("Invite sent successfully");
					} else {
						//	Toast.makeText(getActivity(),responseObject.getString(AppConstants.INVITE_STATUS), Toast.LENGTH_SHORT).show();
						Utils.showToast(responseObject.getString(AppConstants.INVITE_STATUS));
					}
				} catch (JSONException e) {
					Utils.printLog("Json Exception",""+e);
				}
			}else if(RespValue == AppConstants.SEARCH_USER_LIST_RESP){
				//Get user data list for auto complete when searching email and phone number 
				try{
					JSONObject responseObject = new JSONObject(serverResp);
					String statusCode = responseObject.getString(AppConstants.STATUS_CODE);
					if (statusCode.equals(AppConstants.NEW_SUCCESS) || statusCode.equals(AppConstants.EMPTY_VALUE)) {
						boolean checkData = responseObject.isNull(AppConstants.DATA);
						List<String> searchUserList = new ArrayList<String>();
						if(!checkData){
							JSONObject dataObject = responseObject.getJSONObject(AppConstants.DATA);
							JSONArray jArrResp = dataObject.getJSONArray(AppConstants.TRACK);
							// JSONArray jArrResp = new JSONArray(serverResp);
							//List<UserDetail> userDetails = new ArrayList<UserDetail>();
							for (int i = 0; i < jArrResp.length(); i++) {
								JSONObject jObj = jArrResp.getJSONObject(i);
								searchUserList.add(jObj.getString(AppConstants.EMAIL_ID));
								String phno = jObj.getString(AppConstants.PHONE_NUMBER);
								if(phno !=null && !phno.trim().equals("")){
									searchUserList.add(phno);
								}
							}														
							ArrayAdapter<String> adapter = new ArrayAdapter<String>
							(getActivity(),R.layout.search_auto_comp_list_item,searchUserList);
							searchAutoTxt.setAdapter(adapter);
							adapter.notifyDataSetChanged();
						}else{
							//Utils.showToast("TrackList is empty");
						}
						//Add the current user details from db
						Cursor cursor = dbHandler.getProfile();
						if(cursor != null && cursor.getCount() > 0){
							cursor.moveToFirst();
							String userMail = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID));
							String userPhone = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHONE_NO));
							if(userMail!=null && !userMail.equals("")){
								searchUserList.add(userMail);
							}
							if(userPhone!=null && !userPhone.equals("")){
								searchUserList.add(userPhone);
							}
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.search_auto_comp_list_item,searchUserList);
						searchAutoTxt.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}else if(statusCode.equals(AppConstants.NEW_FAILED)){
						showInvalidUserDialog(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			Utils.showToast("No response from server");
		}
	}
	public void showInvalidUserDialog(String AlertTitle,String AlertText,final String StatusCode){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
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
				SessionManager session = new SessionManager(getActivity());
				session.logoutUser(getActivity());
				Intent LoginIntent = new Intent(getActivity(), Login.class);
				startActivity(LoginIntent);
				getActivity().finish();
			}});
		dialog.show();
	}
	private void showSearchAlert() {
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button inviteBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		alertTitle.setText(AppConstants.ALERT_TITLE_SEARCH);
		alertEditTxt.setHint(AppConstants.ALERT_HINT_SEARCH);
		inviteBtn.setBackgroundResource(R.drawable.invite_btn_alert);
		alertMsg.setText(AppConstants.ALERT_MSG_SEARCH_NOT_FOUND);
		if(contact != null){
			if(Utils.validEmail(contact)){
				//Set email address if  contact variable as email address 
				alertEditTxt.setText(contact);
			}else if(contact.equals(contactPhoneNo) && contactEmailAdrs!=null && !contactEmailAdrs.trim().equals("")){
				//Set email address if contactPhnoNo eq to contact
				Utils.printLog("Search PAge", "Con,Pno="+contact+", "+contactPhoneNo);
				alertEditTxt.setText(contactEmailAdrs);
			}
		}
		inviteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String inviteEmail = alertEditTxt.getText().toString();
				if(Utils.validEmail(inviteEmail)){
					if(isInternetOn()){
						inviteUser(inviteEmail);
					} else {
						Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
					}
					dialog.dismiss();
					searchAutoTxt.setText("");
				}
				else Utils.showToast("Enter valid E-mail");
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		alertEditTxt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					String inviteEmail = alertEditTxt.getText().toString();
					if(Utils.validEmail(inviteEmail)){
						if(isInternetOn()){
							inviteUser(inviteEmail);
						} else {
							Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
						}
						dialog.dismiss();
						searchAutoTxt.setText("");
					}
					else Utils.showToast("Enter valid E-mail");
				}
				return true;
			}
		});
		dialog.show();
	}



	private void inviteUser(String mailIdToInvite){
		String url = AppConstants.INVITE_URL;
		String userid = pref.getString(AppConstants.USER_ID_PREF, null);
		ArrayList<String> invitEmailList = new ArrayList<String>();
		ArrayList<String> invitPhonelist = new ArrayList<String>();
		invitEmailList.add(mailIdToInvite);
		JSONArray inviteListJson = new JSONArray(invitEmailList);
		JSONArray phoneListJson = new JSONArray(invitPhonelist);
		try {
			JSONObject sendInviteJSON = new JSONObject();
			sendInviteJSON.put(AppConstants.AUTH_KEY, userid);
			sendInviteJSON.put(AppConstants.EMAIL_ARRAY, inviteListJson);
			sendInviteJSON.put(AppConstants.PHONE_ARRAY,phoneListJson);
			String jsonData = sendInviteJSON.toString();
			String serverResp = new GpsAsyncJSON(getActivity(), url, AppConstants.SEARCH_INVITE_RESP,this).execute(jsonData).get();
			Utils.printLog("Search Invite Resp", ""+serverResp);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
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
	private void initilizeMap() {
		googleMap =  ( (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_search_page)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}
	private void initilizeMapNew() {
		googleMap =  ( (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_search_page_new)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}
	private void centerInLoc(LatLng myLaLn) {
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		CameraPosition camPos = new CameraPosition.Builder().target(myLaLn).zoom(15).bearing(45).tilt(0).build();
		CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
		googleMap.animateCamera(camUpd3);
		MarkerOptions markerOpts = new MarkerOptions().position(myLaLn);
		googleMap.addMarker(markerOpts);
	}

	@Override
	public void onDestroy() {
		if(dbHandler!=null){
			dbHandler.close();
		}
		super.onDestroy();
	}
	//Check Internet connection
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}


}
