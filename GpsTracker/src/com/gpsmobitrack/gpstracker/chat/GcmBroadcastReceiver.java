package com.gpsmobitrack.gpstracker.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpsmobitrack.gpstracker.MenuItems.InvitePage;
import com.gpsmobitrack.gpstracker.MenuItems.SettingsPage.PurchaseStatus;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class  GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	public static final String SUBSCRIPTION_TYPE="Subscription";
	SessionManager session;
	Context ctx;
	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		ctx=context;
		try {

			if(intent.getExtras()!=null){
				if(intent.getExtras().containsKey("type")){

					if(intent.getExtras().getString("type").equalsIgnoreCase("Invite"))
					{
						if(intent.getExtras().containsKey("inviteCount")){

							GPSSharedPreference.setInviteCountSharePreference(Integer.parseInt(intent.getExtras().getString("inviteCount")));

							InvitePage.isInviteClicked=false;
						}
						if (intent.getExtras().containsKey(Config.EXTRA_MESSAGE)) {
							showInviteNotification(context,""+ intent.getExtras().getString(Config.EXTRA_MESSAGE),"Gps Tracker Invitation","Invitation");
						}
					}
					//Subscription Notification
					else if(intent.getExtras().getString("type").equalsIgnoreCase(SUBSCRIPTION_TYPE))
					{
						if(intent.getExtras().containsKey(AppConstants.SUBSCRIPTION_EXPIRATION_TYPE)){

							if(intent.getExtras().getInt(AppConstants.SUBSCRIPTION_EXPIRATION_TYPE)==0){
								showInviteNotification(context,""+intent.getExtras().getString(Config.EXTRA_MESSAGE),"Gps Tracker Subscription","Subscription");
							}
							else if(intent.getExtras().getInt(AppConstants.SUBSCRIPTION_EXPIRATION_TYPE)==1){
								SessionManager.setPurchaseSharePreference(PurchaseStatus.NORMAL_USER.getStatus());
								SharedPreferences  pref =ctx.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
								Editor editor = pref.edit();
								editor.putLong(AppConstants.FREQ_UPDATE_PREF,AppConstants.DEFAULT_TIME_INTERVAL );
								editor.commit();
								showInviteNotification(context,""+intent.getExtras().getString(Config.EXTRA_MESSAGE),"Gps Tracker Subscription","Subscription");
							}
						}

						//Need to implement update frequency


					}}


				else {
					if(intent.getExtras().containsKey(Config.EXTRA_MESSAGE)){



						GCMIntentService gcm = new GCMIntentService();
						session = new SessionManager(context);
						ComponentName comp = new ComponentName(context.getPackageName(), 
								GCMIntentService.class.getName());
						String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
						if(newMessage != null){
							String[] StringAll;
							StringAll = newMessage.split("\\^");
							String title = "";
							String imei  = "";
							String regidFrom = "";
							String regidTo = "";
							String finalmsg="";
							String finalTime="";
							int StringLength = StringAll.length;
							Utils.printLog(Config.TAG, ""+StringLength);

							if (StringLength > 0) {
								if(StringLength == 1) {

								} else {
									title   = StringAll[0];
									imei    = StringAll[1];
									newMessage = StringAll[2];
									regidFrom = StringAll[3];
									//regidTo = StringAll[4];
									String [] getMsg=newMessage.split("@");

									finalmsg=getMsg[0];
									String time=getMsg[1];
									

									//String timeNoon=getMsg[2];
									

									finalTime=time/*+" "+timeNoon*/;
								}
							}
							if(session.isLoggedIn()){
								String trackidCheck = StringAll[1];

								if(trackidCheck.equalsIgnoreCase(session.getUserTrackId())){
									String userValidate = StringAll[6];



									if(MyApplication.isActivityVisible() && MyApplication.getActivityName().equalsIgnoreCase(AppConstants.SHOW_MESSAGE_ACTIVITY) &&
											MyApplication.getChatUserID().equalsIgnoreCase(userValidate)) {
										//gcm.sendNotification("a", "b", "c", "d", "e");
										ShowMessage. addNewMessage(new Message(finalmsg, false, finalTime), false); // add the orignal message from server.

									} 


									else {
										startWakefulService(context, (intent.setComponent(comp)));
									}

								}
							}
						}		


					} 


				}

			}  }catch(Exception ex){}		
	}	
	private void showInviteNotification(Context context,String message,String title,String type){


		message=" "+message;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Intent intent = new Intent(context,MainFragmentMenu.class);
		intent.putExtra(AppConstants.GCM_INVITE_MAIN_FGMT_BUNDLE_KEY,message);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if(type.equalsIgnoreCase("Invitation")){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context)
			.setSmallIcon(R.drawable.icon_newdesign)
			.setContentTitle(title)
			.setStyle(           
					new NotificationCompat.BigTextStyle().bigText(message))
					.setAutoCancel(true).setContentText(message).setTicker(message);
			builder.setSound(uri);
			builder.setContentIntent(pendingIntent);

			notificationManager.notify(1, builder.build());	
		}
		else if(type.equalsIgnoreCase("Subscription")){
			if(message.contains("by")){
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
					//No need to do anything in message.
				}
				else{
					String brokenMessage[]=message.split("by");
					if(brokenMessage.length==2)
						message=brokenMessage[0]+"\n"+brokenMessage[1];
				}
			}
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context)
			.setSmallIcon(R.drawable.icon_newdesign)
			.setContentTitle(title)
			.setStyle(           
					new NotificationCompat.BigTextStyle().bigText(message))
					.setAutoCancel(true).setContentText(message).setTicker(message);
			builder.setSound(uri);
			if(type.equalsIgnoreCase("Invitation"))
				//builder.setContentIntent(pendingIntent);

				notificationManager.notify(1, builder.build());
		}
	}




}

