package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.AccountManager.Login;
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

public class HistoryPage extends Activity implements OnItemClickListener , AsyncResponse, OnClickListener{

	ListView historyList;
	String trackUserID,statusCode,statusMsg,profileUrl,userName;
	ArrayList<String> dateList;
	ImageView profileImg,backBtn;
	DisplayImageOptions options;
	TextView trackUserName;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_page);
		historyList = (ListView)findViewById(R.id.history_listview);
		profileImg = (ImageView)findViewById(R.id.user_profile_img);
		trackUserName = (TextView) findViewById(R.id.username_txt);
		backBtn = (ImageView)findViewById(R.id.back_btn);
		dateList = new ArrayList<String>();
		historyList.setOnItemClickListener(HistoryPage.this);
		backBtn.setOnClickListener(HistoryPage.this);
		pref = HistoryPage.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
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
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	protected void onResume() {
		super.onResume();
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		if(b != null){
			trackUserID = b.getString("trackuserID");
			profileUrl = b.getString("trackuserImageUrl");
			userName = b.getString("trackuserName");
			String upperString="";
			if(userName!=null && !userName.equals("") ){
				upperString = userName.substring(0,1).toUpperCase() + userName.substring(1);
			}
			trackUserName.setText(userName);
		}
		Utils.printLog("Image","Img="+profileUrl);
		ImageLoader.getInstance().displayImage(profileUrl, profileImg,options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}
			@Override
			public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
				Utils.printLog("Load complete", "Load complete");
				final ImageView imageView = (ImageView) view;
				ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
				observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						imageView.getViewTreeObserver().removeOnPreDrawListener(this);
						if(profileUrl != null && ! profileUrl.equals("null") && !profileUrl.equalsIgnoreCase("") && loadedImage!=null){
							Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
						}
						return true;
					}
				});
			}
		});
		if(isInternetOn()){
			String url = AppConstants.HISTORY_DATE_URL;
			String userid = pref.getString(AppConstants.USER_ID_PREF, null);
			BasicNameValuePair trackUserValue = new BasicNameValuePair("id", trackUserID);
			BasicNameValuePair authKeyValue = new BasicNameValuePair(AppConstants.AUTH_KEY,userid);
			List<NameValuePair> historyPageList = new ArrayList<NameValuePair>();
			historyPageList.add(trackUserValue);
			historyPageList.add(authKeyValue);
			new GpsAsyncTask(HistoryPage.this, url, AppConstants.HISTORY_PAGE_RESP, this).execute(historyPageList);
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
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String historyDate = historyList.getItemAtPosition(position).toString();
		Utils.printLog("History Date", ""+historyDate);
		if(isInternetOn()){
			Intent i = new Intent(HistoryPage.this, HistoryMapPage.class);
			i.putExtra("historydate", historyDate);
			i.putExtra("trackuserID", trackUserID);
			i.putExtra("trackuserImageUrl", profileUrl);		
			i.putExtra("trackuserName", userName);
			startActivity(i);
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}		
	}
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		try{
			Utils.printLog("History date Resp", ""+serverResp);
			if(serverResp != null){
				if(RespValue == AppConstants.HISTORY_PAGE_RESP){
					JSONObject jObjServerResp = new JSONObject(serverResp);
					statusMsg = jObjServerResp.getString(AppConstants.STATUS);
					statusCode = jObjServerResp.getString(AppConstants.STATUS_CODE);
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
						if(jObjServerResp.has(AppConstants.DATE)){
							String date = jObjServerResp.getString(AppConstants.DATE);
							JSONArray dateJArray = new JSONArray(date);
							for(int i=0;i<dateJArray.length();i++){
								if(!dateList.contains(dateJArray.getString(i))) dateList.add(dateJArray.getString(i));
							}
							Utils.printLog("DateList", ""+dateList);
						}
						ArrayAdapter<String> dateAdap = new ArrayAdapter<String>(HistoryPage.this, R.layout.txt_view,dateList);
						historyList.setAdapter(dateAdap);
					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)){
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					}else if (statusCode.equalsIgnoreCase(AppConstants.NO_HISTORYDATE_RECORD)) {
						Utils.showToast("No Record Found");
					}else if ((statusCode.equalsIgnoreCase(AppConstants.EMPTY_TRACKID_HISTORY)) || 
							(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACKID_HISTORY))) {
						Utils.showToast("Unable to fetch the Records. Try Later!!");
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
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
		final Dialog dialog = new Dialog(HistoryPage.this, android.R.style.Theme_Translucent);
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
				SessionManager session = new SessionManager(HistoryPage.this);					
				session.logoutUser(HistoryPage.this);
				//Load login 
				Intent LoginIntent = new Intent(HistoryPage.this, Login.class);
				startActivity(LoginIntent);
				finish();
			}});
		dialog.show();
	}
}