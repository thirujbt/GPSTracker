package com.gpsmobitrack.gpstracker.chat;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	static DBHandler mDbHandler;
	SharedPreferences pref;
	Editor editor;

	public GCMIntentService() {
		super("GcmIntentService");
		
	}

	public static final String TAG = "GCMNotificationIntentService";

	@SuppressWarnings("unused")
	@Override
	protected void onHandleIntent(Intent intent) {
		Utils.showToast("New Invite");
		pref = GCMIntentService.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		mDbHandler = new DBHandler(this);
		editor = pref.edit();
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);	
		String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
	
		String title = "";
		String imei  = "";
		String regidFrom = "";
		String regidTo = "";
		String trackUserId = "";
		String emailToUser = "";
		String parentID = "";
		
		if(newMessage != null){
		String[] StringAll;
		StringAll = newMessage.split("\\^");


		int StringLength = StringAll.length;
		Utils.printLog(Config.TAG, ""+StringLength);
		if (StringLength > 0) {
			if(StringLength == 1) {
				
			} else {
				title   = StringAll[0];
				imei    = StringAll[1];
				newMessage = StringAll[2];
				regidFrom = StringAll[3];
				regidTo = StringAll[4];
				emailToUser = StringAll[5];
				trackUserId = StringAll[6];
				parentID = StringAll[7];
				
			}
		}
		}

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(), title, regidTo, trackUserId, emailToUser ,parentID);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString(), title, regidTo, trackUserId, emailToUser,parentID);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				/*if(MyApplication.isActivityVisible()) {
					//displayMessage(this, extras.get(Config.EXTRA_MESSAGE).toString());
				} else {
					sendNotification(""
							+ extras.get(Config.EXTRA_MESSAGE));
				}*/
				displayMessage(this, extras.get(Config.EXTRA_MESSAGE).toString());
				sendNotification(newMessage, title, regidTo, trackUserId, emailToUser,parentID);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	public void sendNotification(String msg, String username, String toUser, String trackUserId, String emailToUser ,String partID) {

		/*int min = 1;
		int max = 100;

		Random r = new Random();
		int notificationIdRandom = r.nextInt(max - min + 1) + min;
		int oldnotificationNumber = pref.getInt("notificationNumber", 0);

		while(notificationIdRandom == oldnotificationNumber){
			
			notificationIdRandom = r.nextInt(max - min + 1) + min;
		} 
		editor.putInt("notificationNumber", notificationIdRandom);
		editor.commit();*/
		
		Log.e("LOG RECEIVE MSG",msg);
	
		String [] getMsg=msg.split("@");
		
		String finalmsg=getMsg[0];
		String time=getMsg[1];
		//String timeNoon=getMsg[2];
		String profileImage=getMsg[2];
		
		String finalTime=time/*+" "+timeNoon*/;
		int notificatinID = Integer.parseInt(partID);

		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		

		Intent myintent = new Intent(this,HomeDetailPage.class);
		myintent.putExtra(Config.EXTRA_MESSAGE, finalmsg);
		myintent.putExtra("trackuserID", trackUserId);
		myintent.putExtra(AppConstants.GCM_EMAIL_TO_USER, emailToUser);
		myintent.putExtra(AppConstants.USER_LIST_POSITION_INTENT, 1);
		myintent.putExtra(AppConstants.USER_FIRST_NAME, username);
		myintent.putExtra("profileImage", profileImage);
		myintent.putExtra(AppConstants.GCM_STATUS_INTENT, true);
		editor.putString(AppConstants.GCM_REGID_PREF, toUser);
	//	editor.putBoolean(AppConstants.GCM_FROM_PREF, true);
		editor.commit();
		
		mDbHandler.storeChatMsg("Demo6", finalTime, finalmsg, emailToUser, false);
		PendingIntent contentIntent = PendingIntent.getActivity(this, notificatinID,
				myintent, PendingIntent.FLAG_UPDATE_CURRENT);

		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon_newdesign)
				.setContentTitle(username)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(finalmsg))
				.setAutoCancel(true)
				.setTicker(finalmsg)
				.setContentText(finalmsg);

		mBuilder.setContentIntent(contentIntent);
		mBuilder.setSound(uri);
		mNotificationManager.notify(notificatinID, mBuilder.build());
	}

	static void displayMessage(Context context, String message) {
		Utils.printLog(Config.TAG, message);
		Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	//70;
	
}
