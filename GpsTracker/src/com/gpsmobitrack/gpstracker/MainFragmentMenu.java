package com.gpsmobitrack.gpstracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import com.facebook.Session;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.HomeMenuAdap;
import com.gpsmobitrack.gpstracker.BackgroundService.BackgroundService;
import com.gpsmobitrack.gpstracker.Bean.ContactBean;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.MenuItems.Help;
import com.gpsmobitrack.gpstracker.MenuItems.HomePage;
import com.gpsmobitrack.gpstracker.MenuItems.InvitePage;
import com.gpsmobitrack.gpstracker.MenuItems.ProfilePage;
import com.gpsmobitrack.gpstracker.MenuItems.SearchPage;
import com.gpsmobitrack.gpstracker.MenuItems.SettingsPage;
import com.gpsmobitrack.gpstracker.MenuItems.TrackListFragment;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class MainFragmentMenu extends FragmentActivity implements OnClickListener,AsyncResponse {

	@SuppressWarnings("unused")
	private static final String TAG= "Main Fragment Activity";
	private DrawerLayout mDrawerLayout;
	SessionManager session;
	Button menuBtn;
	ListView menuList;
	TextView fragMenuTitleTxt;
	ArrayList<Drawable> menuImgList;
	HomeMenuAdap menuAdapter;
	Button refreshImgView;
	HomePage homeFragment;
	Editor editor;
	public static ArrayList<ContactBean> myList =new ArrayList<ContactBean>();
	String[] lvMenuItems = {"GPS Tracker","Search","Invite","TrackList","Profile","Settings","Help","Logout"};
	Fragment currenrFragment = null;
	SharedPreferences pref;
	//	String FBAccessToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_menu);
		refreshImgView = (Button) findViewById(R.id.refresh_btn);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuImgList = new ArrayList<Drawable>();
		session = new SessionManager(MainFragmentMenu.this);
		menuList = (ListView)findViewById(R.id.left_drawer);
		fragMenuTitleTxt = (TextView)findViewById(R.id.fragment_menu_title);
		menuBtn = (Button)findViewById(R.id.menu_btn);
		menuBtn.setOnClickListener(MainFragmentMenu.this);
		pref = MainFragmentMenu.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		//	FBAccessToken=pref.getString(AppConstants.Access_Token_PREF, null);
		session.checkLogin();
		if(!session.isLoggedIn()){
			finish();
		}
		startBackgroundService();

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(event.getAction() == MotionEvent.ACTION_UP){
					menuAdapter.notifyDataSetChanged();
				}

				try{hideSoftKeyboard(MainFragmentMenu.this);}catch(Exception ex){}
				return false;
			}
		});

		//Divide Slide Menu
		int width = getResources().getDisplayMetrics().widthPixels/2;
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuList.getLayoutParams();
		params.width = width;
		menuList.setLayoutParams(params);
		menuAdapter = new HomeMenuAdap(MainFragmentMenu.this, menuImgList);

		menuList.setAdapter(menuAdapter);

		menuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//		onMenuItemClick(parent, view, position, id);
				onMenuItemClick(position);
			}
		});


		// Invite click from notification

		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			//Load request receive from notification click

			String message = intent.getExtras().getString(
					AppConstants.GCM_INVITE_MAIN_FGMT_BUNDLE_KEY);

			// Load invite page
			loadReqRecFgmt(message);

		} else {

			//Normal loading from application icon click			
			FragmentManager fm = MainFragmentMenu.this.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			homeFragment = new HomePage();
			ft.add(R.id.content_frame, homeFragment);
			ft.commit();

			//Refresh the home fragment
			refreshImgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(isInternetOn()){
						if(homeFragment!=null){
							homeFragment.homeRefresh(AppConstants.HOME_TRACKCOUNT_RESP,1);
						}
					} else {

						Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
					}
				}
			});

		}




		Utils.printLog("Main frag", "On create");
	}

	@Override
	protected void onNewIntent(Intent intent) {		
		super.onNewIntent(intent);
		// New intent received from invite
		if(intent!=null && intent.getExtras()!=null){
			String message = intent.getExtras().getString(AppConstants.GCM_INVITE_MAIN_FGMT_BUNDLE_KEY);

			//Load invite page
			loadReqRecFgmt(message);
		}else {
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.menu_btn){
			menuAdapter.notifyDataSetChanged();
			drawerToggle();
			hideSoftKeyboard(this);
		}
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	private void drawerToggle(){
		if(mDrawerLayout.isDrawerOpen(menuList)){
			mDrawerLayout.closeDrawer(menuList);
		}else{
			mDrawerLayout.openDrawer(menuList);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Utils.printLog("MainFrag", "OnPause");
	}

	public void onResume(){
		super.onResume();

		Utils.printLog("Main frag", "background");
		if(!session.isLoggedIn()){
			finish();
		}

		InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
		if(imm.isAcceptingText()) imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.alert_dialog_main);
			final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
			final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
			final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
			Button yesBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
			Button noBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

			alertTitle.setText(AppConstants.ALERT_TITLE);
			alertMsg.setText(AppConstants.ALERT_MSG_EXIT_APP);
			alertEditTxt.setVisibility(View.GONE);
			yesBtn.setBackgroundResource(R.drawable.yes_btn_exit);
			noBtn.setBackgroundResource(R.drawable.no_btn_exit);

			yesBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					finish();
				}
			});
			noBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Load the request received fragment when invite notification clicked
	 * 
	 * @param msg
	 */
	private void loadReqRecFgmt(String msg) {

		//Check for internet connection
		if(!isInternetOn()){
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			return;
		}

		FragmentManager fm = MainFragmentMenu.this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		mDrawerLayout.closeDrawer(menuList);

		// Invite fragment set message in bundle
		Fragment fragment = new InvitePage();
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.GCM_INVITE_INVITE_PAGE_BUNDLE_KEY, msg);
		fragment.setArguments(bundle);

		// Load Invite page
		currenrFragment = fragment;
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
		fragMenuTitleTxt.setText(lvMenuItems[2]);
	}

	private void onMenuItemClick(int position) {
		String selectedItem = lvMenuItems[position];
		String currentItem = fragMenuTitleTxt.getText().toString();
		Utils.printLog("Current,selected item", currentItem + "," + selectedItem);
		if (selectedItem.compareTo(currentItem) == 0) {
			mDrawerLayout.closeDrawer(menuList);
			return;
		}
		mDrawerLayout.closeDrawer(menuList);
		FragmentManager fm = MainFragmentMenu.this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = null;
		refreshImgView.setVisibility(View.INVISIBLE);
		if (selectedItem.compareTo("GPS Tracker") == 0) {
			refreshImgView.setVisibility(View.VISIBLE);
			homeFragment = new HomePage();
			fragment =homeFragment;
		}else if (selectedItem.compareTo("Profile") == 0) {
			fragment = new ProfilePage();
		}
		else if (selectedItem.compareTo("Search") == 0) {
			fragment = new SearchPage();
		}
		else if (selectedItem.compareTo("Invite") == 0) {
			fragment = new InvitePage();
		}
		else if (selectedItem.compareTo("TrackList") == 0) {
			fragment = new TrackListFragment();
		}
		else if (selectedItem.compareTo("Settings") == 0) {
			fragment = new SettingsPage();
		}

		else if (selectedItem.compareTo("Help") == 0) {
			fragment = new Help();
		}

		else if(selectedItem.compareTo("Logout") == 0){
			InvitePage.isInviteClicked=false;
			logoutUser();
		}

		if (fragment != null) {
			currenrFragment = fragment;
			ft.replace(R.id.content_frame, fragment);
			ft.commit();
			fragMenuTitleTxt.setText(selectedItem);


		}
	}

	public void changeFragment(){

		onMenuItemClick(5);
	}

	@SuppressWarnings("unchecked")
	private void logoutUser(){
		SharedPreferences pref;
		pref = getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		String userid = pref.getString(AppConstants.USER_ID_PREF, null);
		String url = AppConstants.LOGOUT_URL;
		if (userid != null) {
			BasicNameValuePair trackUserValue = new BasicNameValuePair(AppConstants.AUTH_KEY,userid);
			List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
			detailPageList.add(trackUserValue);
			new GpsAsyncTask(MainFragmentMenu.this, url, AppConstants.LOG_OUT_USER_RESP, this).execute(detailPageList);
		}
	}

	@SuppressWarnings("unchecked")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SettingsPage.RC_REQUEST){
			if(currenrFragment!=null){ // Call for in app purchase
				((SettingsPage)(currenrFragment)).onActivityResult(requestCode, resultCode, data);
			}
		} else if(requestCode == 4){

			Bundle b = new Bundle();
			if(b != null){
				try {
					Utils.printLog("Main Frag list", ""+myList);
					myList = (ArrayList<ContactBean>) data.getSerializableExtra(AppConstants.EMAIL_KEY_INTENT);
				}
				catch(NullPointerException e){
					Utils.printLog("Excep Main frag", ""+e);
				}
			}
		}


		Utils.printLog("Main", "Fragment");
	}

	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		String statusCode = "0";
		String statusResp,msgResp;
		if (serverResp!= null) {
			JSONObject jObjServerResp;
			try {
				jObjServerResp = new JSONObject(serverResp);
				statusCode = jObjServerResp.getString(AppConstants.STATUS_CODE);
				statusResp = jObjServerResp.getString(AppConstants.STATUS);
				msgResp = jObjServerResp.getString(AppConstants.MESSAGE);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (session == null) {
			session = new SessionManager(this);
		}

		switch (statusCode) {
		case AppConstants.NEW_SUCCESS:
		case AppConstants.NEW_FAILED:
		default:

			/*	if(FBAccessToken!=null && !FBAccessToken.isEmpty()){
				Session session = Session.getActiveSession();
				session.closeAndClearTokenInformation();
				}*/

			session.logoutUser(MainFragmentMenu.this);
			Intent i = new Intent(MainFragmentMenu.this,Login.class);
			startActivity(i);
			finish();			
			break;
		}

	}
	/*
	Start BackGround Service*/
	public void startBackgroundService(){

		Utils.printLog("Service Called", "Started");
		SharedPreferences pref = getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
		editor = pref.edit();
		editor.putLong(AppConstants.FREQ_UPDATE_PREF, updateTime);
		editor.commit();
		boolean isServiceOn = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);

		if(isServiceOn){
			AlarmManager alarm = (AlarmManager)MainFragmentMenu.this.getSystemService(Context.ALARM_SERVICE);
			Calendar cal = Calendar.getInstance();
			Intent intent2 = new Intent(MainFragmentMenu.this, BackgroundService.class);
			PendingIntent pintent = PendingIntent.getService(MainFragmentMenu.this, 0, intent2, 0);
			if(PendingIntent.getService(MainFragmentMenu.this, 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
				alarm.cancel(pintent);
			}
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (updateTime * 1000 * 60), pintent);
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
}