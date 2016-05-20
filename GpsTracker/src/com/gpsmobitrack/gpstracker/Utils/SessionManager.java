package com.gpsmobitrack.gpstracker.Utils;

import java.util.HashMap;

import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.InviteListAdapter;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.MenuItems.InviteFragment;
import com.gpsmobitrack.gpstracker.MenuItems.SettingsPage.PurchaseStatus;
import com.gpsmobitrack.gpstracker.TrackingService.HandlerManager;
import com.gpsmobitrack.gpstracker.database.DBHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class SessionManager {

	SharedPreferences pref;
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_USERID = "Userid";

	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createLoginSession( String email,String password, String userid){
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_PASSWORD, password);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_USERID, userid);
		editor.commit();
	}	

	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			Intent i = new Intent(_context, Login.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(i);
		}
	}

	public String getUserTrackId(){

		return pref.getString(AppConstants.USER_KEY_PREF,"");
	}

	public HashMap<String, String> getUserDetails(){

		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		return user;
	}

	public void logoutUser(Context context){
		// Clearing all data from Shared Preferences
		DBHandler dbhandler = new DBHandler(context);
		dbhandler.deleteProfile();
		editor.clear();
		editor.commit();
		resetDatas();
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
	private void resetDatas() {
		if(MainFragmentMenu.myList != null){
			if(MainFragmentMenu.myList.size()>0){
				MainFragmentMenu.myList.clear();
			}	
		}


		if(InviteFragment.inviteArrList != null){

			if(InviteFragment.inviteArrList.size()>0){
				InviteFragment.inviteArrList.clear();
			}
		}

		if(InviteFragment.inviteListLast != null){
			if(InviteFragment.inviteListLast.size()>0){
				InviteFragment.inviteListLast.clear();
			}

		}

		if(InviteListAdapter.inviteRemoveList != null){


			if(InviteListAdapter.inviteRemoveList.size()>0){
				InviteListAdapter.inviteRemoveList.clear();
			}
		}


		if(HomeDetailPage.stalatitudeArry != null){

			if(HomeDetailPage.stalatitudeArry.size()>0){

				HomeDetailPage.stalatitudeArry.clear();
			}

		}

		if(HomeDetailPage.stalongitudeArry != null){
			if(HomeDetailPage.stalongitudeArry.size()>0){

				HomeDetailPage.stalongitudeArry.clear();
			}
		}


		HomeDetailPage.isTrackingON =false;
		HomeDetailPage.goneBackground =false;
		HandlerManager.trackUserId ="";

	}

	public static void setPurchaseSharePreference(int state){
		SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppConstants.USER_TYPE_PREF, state);
		editor.commit();

	}

	public static int getPurchaseSharePreference(){
		SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		return sharedPreferences.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus());
	}


}
