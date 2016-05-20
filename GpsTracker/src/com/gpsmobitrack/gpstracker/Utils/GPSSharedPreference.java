package com.gpsmobitrack.gpstracker.Utils;

import android.content.SharedPreferences;

import com.gpsmobitrack.gpstracker.MyApplication;



public class GPSSharedPreference {
	
	public static void setInviteCountSharePreference(int count){
		
		SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.INVITE_COUNT, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("inviteCount", count);
		editor.commit();
		
	}
	
	public static int getInviteCountSharePreference(){
		SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.INVITE_COUNT, 0);
	
		return sharedPreferences.getInt("inviteCount",0);
	}
	
	
	  public static void setPurchaseDurationSharePreference(String duration){
			
			SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("duration", duration);
			editor.commit();
			
		}
		
		public static String getPurchaseDurationSharePreference(){
			SharedPreferences sharedPreferences = MyApplication.getAppContext().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		
			return sharedPreferences.getString("duration",null);
		}
}
