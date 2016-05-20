package com.gpsmobitrack.gpstracker.MenuItems;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;



//import com.google.android.gms.ads.AdView;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.HomePageAdapter;
import com.gpsmobitrack.gpstracker.Bean.UserDetail;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
//import com.gpsmobitrack.gpstracker.MenuItems.SettingsPage.PurchaseStatus;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpsmobitrack.gpstracker.Utils.GPSTracker;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;

public class HomePage extends Fragment implements AsyncResponse {

	ListView homepageList;
	List<UserDetail> userDetail = new ArrayList<UserDetail>();
	ArrayList<String> usernameList,mapImgList,cityList,relationList;
	HomePageAdapter homePageAdap;
	String statusResp,msgResp,userId,statusCode,UserKey;
	SharedPreferences pref;
	Editor editor;
	GPSTracker gps;
	SessionManager session;
	static public HomePage homePage;
	int pageNo=1;
	int totalCount=0;
	View footerView;
	DBHandler dbHandler;
	private int MINIMUM_PAGE_COUNT=10;
	LinearLayout AdViewParent;
	//private AdView adView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_page, null);
		homePage=this;
		gps = new GPSTracker(getActivity());
		session = new SessionManager(getActivity());
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		homepageList = (ListView)view.findViewById(R.id.home_page_listView);
		AdViewParent = (LinearLayout)view.findViewById(R.id.adViewparent);
		usernameList = new ArrayList<String>();
		mapImgList = new ArrayList<String>();
		sendHomeRequest(AppConstants.HOME_TRACKCOUNT_RESP);
		setFooterView();
		return view;
	}
	public void onResume(){
		super.onResume();
	}
	private void setFooterView() {
		footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_footer,null,false);
		Button loadMore = (Button) footerView.findViewById(R.id.load_more_footer_btn);
		loadMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				pageNo= pageNo + 1;
				Utils.printLog("PgNo",""+pageNo);
				sendHomeRequest(AppConstants.HOME_PAGINATION_RESP);
			}
		});
		homepageList.addFooterView(footerView);
	}
	public void homeRefresh(int respCode,int pageNumber){
		pageNo = pageNumber;
		Utils.printLog("homepagelist", ""+homepageList.getFooterViewsCount());
		/*if(homepageList.getFooterViewsCount()<=0){
			homepageList.addFooterView(footerView);
		}*/
		sendHomeRequest(respCode);
	}
	@SuppressWarnings({ "unchecked", "unused" })
	private void sendHomeRequest(int respCode){
		String url = AppConstants.HOME_URL;
		String userid = pref.getString(AppConstants.USER_ID_PREF, null);
		String usernameHome = pref.getString(AppConstants.USER_NAME_PREF, null);
		Utils.printLog("UserID in home", ""+pref.getString(AppConstants.USER_ID_PREF, null));
		BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userid);
		BasicNameValuePair pageValue = new BasicNameValuePair(AppConstants.PAGE, ""+pageNo);
		List<NameValuePair> homePageList = new ArrayList<NameValuePair>();
		homePageList.add(useridValue);
		homePageList.add(pageValue);
		if(isInternetOn()){
			try {
				if(session.isLoggedIn()) {
					new GpsAsyncTask(this.getActivity(), url,respCode, HomePage.this).execute(homePageList);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	public void getOwnUserData(boolean isFriendExist){
		userDetail = new ArrayList<UserDetail>();
		UserDetail detail = new UserDetail();
		String userName = null,photoPath = null,emailID = null;
		dbHandler = new DBHandler(getActivity());
		Cursor cursor = null;
		if(dbHandler!=null)
			cursor = dbHandler.getProfile();
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			do {
				userName = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_FIRST_NAME));
				emailID = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID));
				photoPath = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHOTO));
			} while (cursor.moveToNext());
		}
		String city = null;
		Double lat = gps.getLatitude();
		Double lon = gps.getLongitude();
		if(lat != null && lon != null){
			if(!lat.equals("0.0") && !lon.equals("0.0")){
				Geocoder geo;
				List<Address> cityData = null;
				geo = new Geocoder(getActivity(), Locale.ENGLISH);
				try {
					cityData = geo.getFromLocation(lat,lon, 1);
					Address addr = cityData.get(0);
					city = addr.getLocality();
				} catch (IOException e) {
					city = "Not Available";
					e.printStackTrace();
				} catch (Exception e){
				}
			}else{
				city = "Not Available";
			}
		}else{
			city = "Not Available";
		}
		try{
			Calendar calender = Calendar.getInstance();
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			String date = sdfDate.format(calender.getTime());
			String time = sdfTime.format(calender.getTime());
			String id  = pref.getString(AppConstants.USER_KEY_PREF, null);
			if(userName!=null)
				detail.setFirstName(userName);
			if(photoPath!=null)
				detail.setProfImgURL(photoPath);
			detail.setLocation(city);
			detail.setDate(date);
			detail.setTime(time);
			detail.setRelationShip("Self");
			detail.setTrackUserId(id);
			detail.setLatitude(String.valueOf(lat));
			detail.setLongitude(String.valueOf(lon));
			detail.setEmailId(emailID);
			userDetail.add(detail);
		}catch(Exception ex){}
		if(!isFriendExist){
			homePageAdap = new HomePageAdapter(getActivity(), userDetail,"");
			homepageList.setAdapter(homePageAdap);
			homePageAdap.notifyDataSetChanged();
		}
		editor = pref.edit();
		editor.putString("photoPath", photoPath);
		editor.commit();
	}
	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		Utils.printLog("Serresp Home", ""+serverResp);
		try{
			if(serverResp != null){
				JSONArray jArray = new JSONArray(serverResp);
				for(int i=0;i<jArray.length();i++){
					JSONObject jObj = jArray.getJSONObject(i);
					String statusMsg = jObj.getString(AppConstants.STATUS);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					//Check for purchase status for each loop
					if (jObj.has(AppConstants.PURCHASE_STATUS)) {
						//Purchase status
						int purchaseStatus = Integer.parseInt(jObj.getString(AppConstants.PURCHASE_STATUS));
						//Set purchase user type
						setSharPrefUserType(purchaseStatus); 
						//Checks for purchase detail
						/*if(!(purchaseStatus == PurchaseStatus.NORMAL_USER.getStatus())){
							AdViewParent.setVisibility(View.GONE);
						}*/
					}
					if (jObj.has(AppConstants.STATUS_DURATION)) {
						//Purchase status
						String purchaseDuration = jObj.getString(AppConstants.STATUS_DURATION);
						
						GPSSharedPreference.setPurchaseDurationSharePreference(purchaseDuration);
						
						//Set purchase user type
						//setSharPrefUserType(purchaseStatus); 
						//Checks for purchase detail
						/*if(!(purchaseStatus == PurchaseStatus.NORMAL_USER.getStatus())){
							AdViewParent.setVisibility(View.GONE);
						}*/
					}
					if (jObj.has("inviteCount")) {
						GPSSharedPreference.setInviteCountSharePreference(Integer.parseInt(jObj.getString("inviteCount")));
						int ii=GPSSharedPreference.getInviteCountSharePreference();
						int j=GPSSharedPreference.getInviteCountSharePreference();
						Log.e("", "");
					}
					JSONArray userJsonArray = new JSONArray();
					if (jObj.has(AppConstants.TOTAL_COUNT)) {
						totalCount = jObj.getInt(AppConstants.TOTAL_COUNT);
						userJsonArray = jObj.getJSONArray(AppConstants.USER);
						Utils.printLog("Total out", ""+totalCount);
					}
					switch (statusCode) {
					case AppConstants.NEW_SUCCESS:
						extractValues(userJsonArray,"",RespValue);
						break;
					case AppConstants.NO_TRACK_USER:
						getOwnUserData(false);
						homepageList.removeFooterView(footerView);
						break;
					case AppConstants.NEW_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
						break;
					default:
						break;
					}
				}
			} else {
				Utils.showToast("No response from server");
			}
		}
		catch (JSONException e) {
			Utils.printLog("JSON Excep", ""+e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Set user type in shared preference
	 * 
	 * @param status
	 */
	private void setSharPrefUserType(int status){		
		/*Editor editor = pref.edit();
		editor.putInt(AppConstants.USER_TYPE_PREF, status);
		editor.commit();*/
		SessionManager.setPurchaseSharePreference(status);
	}
	/**
	 * Get user type from SharedPreference
	 * 
	 * @return return user type
	 */
	@SuppressWarnings("unused")
	private int getSharPrefUserType(){
		/*Log.d("Home","UY="+pref.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus()));
		return pref.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus());*/
		return SessionManager.getPurchaseSharePreference();
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
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
				//Crear  session
				dialog.dismiss();
				SessionManager session = new SessionManager(getActivity());					
				session.logoutUser(getActivity());
				//Load login 
				Intent LoginIntent = new Intent(getActivity(), Login.class);
				startActivity(LoginIntent);
				getActivity().finish();
			}});
		dialog.show();
	}
	public void extractValues(JSONArray jsoArr ,String CountStr,int respValue){
		//Reset list if refresh clicked
		if(respValue == AppConstants.HOME_TRACKCOUNT_RESP){
			getOwnUserData(true);
		}
		if(CountStr != null || CountStr != ""){
		}
		try {
			for(int j=0;j<jsoArr.length();j++){
				UserDetail detail = new UserDetail();
				JSONObject jObjUser;
				jObjUser = jsoArr.getJSONObject(j);
				String id = jObjUser.getString(AppConstants.ID);
				String username = jObjUser.getString(AppConstants.USER_NAME);
				String longitude = jObjUser.getString(AppConstants.LONGITUDE);
				String latitude = jObjUser.getString(AppConstants.LATITUDE);
				String relationship = jObjUser.getString(AppConstants.RELATIONSHIP);
				String profileImg = jObjUser.getString(AppConstants.PROF_IMG);
				String date = jObjUser.getString(AppConstants.DATE);
				String time = jObjUser.getString(AppConstants.TIME);
				String emailid = jObjUser.getString(AppConstants.EMAIL_ID);
				String lastname = jObjUser.getString(AppConstants.USER_LAST_NAME);
				String gcmRegId = jObjUser.getString(AppConstants.GCM_REGID);
				String city = null;
				String mapURL = null;
				Utils.printLog("Total", ""+totalCount);
				if((!latitude.isEmpty()) && (!longitude.isEmpty())){
					if(!latitude.equalsIgnoreCase("0.0") && (!longitude.equalsIgnoreCase("0.0"))){
						Geocoder geo;
						List<Address> cityData;
						geo = new Geocoder(getActivity(), Locale.ENGLISH);
						try {
							cityData = geo.getFromLocation((Double.parseDouble(latitude)),(Double.parseDouble(longitude)), 1);
							if(cityData!=null && cityData.size() >0){
								Address addr = cityData.get(0);
								city = addr.getLocality();
							}
							mapURL = "http://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+
									"&zoom=12&size=1000x400&sensor=false&markers=color:blue|label:|"+latitude+","+longitude;
						} catch (IOException e) {
							city = "Not Available";
							e.printStackTrace();
						} catch(IndexOutOfBoundsException e){

						}
					}else{
						city = "Not Available";
					}
				}else{
					city = "Not Available"; 
					latitude = null;
					longitude = null;
				}
				detail.setTrackUserId(id);
				detail.setFirstName(username);
				detail.setLatitude(latitude);
				detail.setLongitude(longitude);
				detail.setLocation(city);
				detail.setRelationShip(relationship);
				detail.setProfImgURL(profileImg);
				detail.setMapImgURL(mapURL);
				detail.setDate(date);
				detail.setTime(time);
				detail.setLastName(lastname);
				detail.setEmailId(emailid);
				if(gcmRegId != null) detail.setGcmRegId(gcmRegId);
				userDetail.add(detail);
				Utils.printLog("City", ""+city);
				Utils.printLog("GCM ID Home==",""+gcmRegId);
			}
			if(respValue == AppConstants.HOME_PAGINATION_RESP){
				if(userDetail.size() >= totalCount){
					homepageList.removeFooterView(footerView);
				} 
				int listPosition = homepageList.getLastVisiblePosition();
				homePageAdap = new HomePageAdapter(getActivity(), userDetail,"");
				homepageList.setAdapter(homePageAdap);
				homepageList.setSelection(listPosition);
			} else {
				homePageAdap = new HomePageAdapter(getActivity(), userDetail,"");
				homepageList.setAdapter(homePageAdap);
			}
			homePageAdap.notifyDataSetChanged();
			if(pageNo == 1 && totalCount <= MINIMUM_PAGE_COUNT){
				homepageList.removeFooterView(footerView);
			}
		} catch (JSONException e) {
			Utils.printLog("Ex1",""+e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Utils.printLog("Ex1",""+e);
			e.printStackTrace();
		} 
	}
	@Override
	public void onDestroy() {
		homePage=null;
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
}