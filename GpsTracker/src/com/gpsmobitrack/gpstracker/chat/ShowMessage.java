package com.gpsmobitrack.gpstracker.chat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.graphics.Bitmap;

public class ShowMessage extends Activity implements AsyncResponse{
	//public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	//private static final String PROPERTY_APP_VERSION = "appVersion";
	//private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME ="onServerExpirationTimeMs";
	/**
	 * Default lifespan (7 days) of a reservation until it is considered expired.
	 */
	//public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	/**
	 * You must use your own project ID instead.
	 */
	//String SENDER_ID = Config.GOOGLE_SENDER_ID;

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMDemo";
	TextView mDisplay,footTextiew, usernameTxt;
	Button btnSend, backBtn;
	//GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	//Context context;
	//String regid;
	//static boolean registered = false;


	String name;

	static String emailToUser;
	String message;
	String UserDeviceIMEI = "";
	static ArrayList<Message> messages;
	GcmBroadcastReceiver mHandleMessageReceiver;
	static AwesomeAdapter adapterList;
	static ListView msgListView;
	static View footerView;
	//static Context mContext;
	static boolean flag;
	String toUser, userFirstName;
	SharedPreferences pref;
	//Editor editor;
	String fromUser,trackUserId;
	String oppUserid;
	static DBHandler mDbHandler;
	static Cursor cursor;
	public static final String CHAT_DATE_TIME ="date_time";
	public static final String CHAT_NAME = "chat_name";
	public static final String CHAT_MSG = "msg";
	public static final String CHAT_EMAIL_ID = "email_id";
	public static final String CHAT_IS_MINE = "ismine";

	ImageView profileImgBtn;
	DisplayImageOptions options;
	String profileImage,ownProfileImage;
	/*//GCM declaration
	GcmBroadcastReceiver mHandleMessageReceiver;
	static Context mContext;
	Context context;
	String regid;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
			"onServerExpirationTimeMs";
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	GoogleCloudMessaging gcm;
	String SENDER_ID = Config.GOOGLE_SENDER_ID;
	static boolean registered = false;
	//GCM declaration ends
	 */
	/*@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent.getExtras() != null) {
			Log.e("Check Here", intent.getExtras().getString(Config.EXTRA_MESSAGE));
			addNewMessage(new Message(intent.getExtras().getString(Config.EXTRA_MESSAGE), false));
		}
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_activity);
		getWindow().setGravity(Gravity.TOP);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		mDbHandler = new DBHandler(ShowMessage.this);

		pref = ShowMessage.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		//	editor = pref.edit();

		 ownProfileImage=pref.getString("photoPath", null);
		usernameTxt = (TextView)findViewById(R.id.chat_username);
		mDisplay = (TextView) findViewById(R.id.text);         
		msgListView = (ListView) findViewById(R.id.listview);
		profileImgBtn = (ImageView)findViewById(R.id.chat_user_profile_img);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.chat_user_default_image)
		.showImageForEmptyUri(R.drawable.chat_user_default_image)
		.showImageOnFail(R.drawable.chat_user_default_image)
		//.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		setFooterView();
		//if(messages==null)
		messages = new ArrayList<Message>();

		adapterList = new AwesomeAdapter(this, messages);
		msgListView.setAdapter(adapterList);
		footerView.setVisibility(View.GONE);
		mHandleMessageReceiver = new GcmBroadcastReceiver();
		usernameTxt.setSelected(true);
		//mContext = ShowMessage.this;
		registerReceiver(mHandleMessageReceiver, new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
		//context = getApplicationContext();
		/*regid = getRegistrationId(context);
		Log.e("Check RegId", "Previous RegId "+regid);
		if(regid.length() == 0) { 
			registerBackground();
		}
		gcm = GoogleCloudMessaging.getInstance(this);*/

		msgListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideKeyboard(v);
				return false;
			}
		});

		Intent mIntent = getIntent();
		if(mIntent.getExtras() != null) {
			message = mIntent.getExtras().getString(Config.EXTRA_MESSAGE);
			if(message != null) {
				footerView.setVisibility(View.GONE);
				addNewMessage(new Message(message, false, Config.mFormat.format(new Date())), false);

			}
		}
		final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
		fromUser = prefs.getString(PROPERTY_REG_ID, "");
		trackUserId = pref.getString(AppConstants.USER_KEY_PREF, null);
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		if(b != null){
			toUser = b.getString("regid");
			boolean isFromGCM = b.getBoolean(AppConstants.GCM_TO_INTENT, false);
			emailToUser = b.getString(AppConstants.GCM_EMAIL_TO_USER);
			userFirstName = b.getString(AppConstants.USER_FIRST_NAME);
			oppUserid = b.getString("oppuser");
			profileImage=b.getString("profileImage");
			if(userFirstName!= null){
				String upperString = userFirstName.substring(0, 1).toUpperCase() + userFirstName.substring(1);
				usernameTxt.setText(upperString);
			}


			//getData from database
			if(emailToUser != null){
				cursor = mDbHandler.getChatMsg(emailToUser);
				//mDbHandler.deleteChatHistoryByUser(emailToUser);

				if(cursor!=null && cursor.getCount() >0 ) {
					if(cursor.moveToFirst()) {
						do{
							String msg = cursor.getString(cursor.getColumnIndex(CHAT_MSG));
							int intIsMine = cursor.getInt(cursor.getColumnIndex(CHAT_IS_MINE));
							boolean isMine = (intIsMine == 1) ? true:false;
							String date_time = cursor.getString(cursor.getColumnIndex(CHAT_DATE_TIME));
							addNewMessage(new Message(msg, isMine, date_time), false);
						}while(cursor.moveToNext());
					}
				}
			} 

			//trackUserId = b.getString(AppConstants.TRACK_USERID_INTENT);
			if(isFromGCM){
				Intent mIntent1 = getIntent();
				if(mIntent1.getExtras() != null) {
					toUser = b.getString(AppConstants.GCM_REGID_TOUSER);
					message = mIntent1.getExtras().getString(AppConstants.GCM_TO_SHOW_MSG);
					profileImage=b.getString("profileImage");
					if(message != null) {
						addNewMessage(new Message(message, false, Config.mFormat.format(new Date())), false);
					}
				}
				/*	editor.putBoolean(AppConstants.GCM_FROM_PREF, false);
				editor.commit();*/
			}else{

			}
		}
		loadProfileImage(profileImage, profileImgBtn);
	}

	// Create a broadcast receiver to get message and show on screen 

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyApplication.activityResumed();
		MyApplication.setActivityName(AppConstants.SHOW_MESSAGE_ACTIVITY);
		MyApplication.setChatUserID(oppUserid);
		footerView.setVisibility(View.GONE);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		/*MyApplication.activityPaused();
		MyApplication.setActivityName(AppConstants.EMPTY_ACTIVITY);
		MyApplication.setChatUserID("");
		 */
	}


	static void addNewMessage(Message m, boolean isMsgSent) {
		Utils.printLog(TAG, m.toString());
		/*if(!flag) {
			messages = new ArrayList<Message>();
			adapterList = new AwesomeAdapter(mContext, messages);
			msgListView.setAdapter(adapterList);
			flag = true;
		}*/
		for(int i=messages.size()-1;i>=0;i--){
			if(messages.get(i).getMessage().equals(m.getMessage()))
				return;
		}

		if(isMsgSent){
			footerView.setVisibility(View.VISIBLE);
		}else{
			footerView.setVisibility(View.GONE);
		}

		messages.add(m);
		adapterList.notifyDataSetChanged();
		msgListView.getLastVisiblePosition();
		msgListView.setSelection(messages.size()-1);

		if(MyApplication.isActivityVisible() && MyApplication.getActivityName().equalsIgnoreCase(AppConstants.SHOW_MESSAGE_ACTIVITY)) {
			mDbHandler.storeChatMsg("Demo6", m.getDateTime(), m.getMessage(), emailToUser, m.getMine());
		}

	}


	/* private final BroadcastReceiver mHandleMessageReceiver =
	            new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	            //mDisplay.append(newMessage + "\n");

	            Utils.printLog(TAG, "Message: "+newMessage);
	            Utils.showToast(newMessage);
	        }
	    };*/

	/**
	 * Gets the current registration id for application on GCM service.
	 * <p>
	 * If result is empty, the registration has failed.
	 *
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	/*private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.v(TAG, "Registration not found.");
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			Log.v(TAG, "App version changed or registration expired.");
			return "";
		}
		return registrationId;
	}*/

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */

	/**
	 * Stores the registration id, app versionCode, and expiration time in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration id
	 */
	/*private void setRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.v(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

		Log.v(Config.TAG, "Setting registration expiry time to " +
				new Timestamp(expirationTime));
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
		registered = ServerUtilities.register(context, "venkatesh.n@pickzy.com", regid);
	}*/


	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration id, app versionCode, and expiration time in the application's
	 * shared preferences.
	 */
	/*private void registerBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration id=" + regid;

					Log.e(TAG,msg);

					// You should send the registration ID to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.

					// For this demo: we don't need to send it because the device will send
					// upstream messages to a server that echo back the message using the
					// 'from' address in the message.

					// Save the regid - no need to register again.
					setRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//mDisplay.append(msg + "\n");
				Utils.printLog(TAG, msg);
			}
		}.execute(null, null, null);
	}*/

	private void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager)ShowMessage.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		intent.putExtra("gcmCallback",true);
		setResult(2,intent);
		finish();
		super.onBackPressed();
	}

	public void onClick(final View view) {
		if(view == findViewById(R.id.back_btn_chat)){
			Intent intent=new Intent();
			intent.putExtra("gcmCallback",true);
			setResult(2,intent);
			finish();//finishing activity
		}

		if (view == findViewById(R.id.sendBtn)) {
			String msg = mDisplay.getText().toString();
			if((!mDisplay.getText().toString().isEmpty()) && (msg.trim().length() > 0)){

				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
				String strDate = sdf.format(c.getTime());
				String[] gettime=strDate.split(" ");
				String time=gettime[1];
				String[] timesplit=time.split(":");
				String time1=timesplit[0];
				String time2=timesplit[1];
				String timenoon=gettime[2];
				String finaltime=time1+":"+time2+" "+timenoon;

				addNewMessage(new Message(msg, true, finaltime), true);
				footerView.setVisibility(View.VISIBLE);
				mDisplay.setText("");
				String serverURL = Config.YOUR_SERVER_URL+"sendpush.php";
				//String id = Integer.toString(msgId.incrementAndGet());
				/*String to = "APA91bEHKh0UbMWg_R71BXNjRy--" +
					"7M6-2hrhno2kAlE784Fok5RPeWuFc4d9lfZtGdWA-" +
					"LgElbbHnSDgvOEK1ZTfoYdAXpMR-DYOCyIlAKzND1Ir3C5j" +
					"QZgI5h8YgkAzG2WLYtFKvI9PSqPI-JbidDWQiWy0G0pbsg";*/


				//toUser = "APA91bG6TRCU3tk3JmQ91zNVy1hJaTOlmrv-NvfuWKM0Ek3Ixm5DZ_P0NqIVH8sVG3vD550Ujc9vntexGbdtYkixBZRVYt2ImGRxnwGgkoEnRjc9BQpDjxUqx6ToHN1BX-WB523uWMm34POxhvG4W16dOJK2rDBAag";
				//String from = "APA91bGTvSk7YnxCciAtnxYTWR0NpUqNsU9KLQc5HxN8IRMAPhIGkhApduP35eSwxWN5-qr0G3dL4xC7tGei3z9fn0R4Vobav-ZOCvfD1R_VLLXcLPUVnsOYFWs3UBm3s5dRG_sMpqY6tZMv65g0YUrzGeOeAlM37A";
				String email = pref.getString("email", null);

				/*new SendMessageInBackground(AppConstants.SEND_MESSAGE,ShowMessage.this).
			execute(serverURL,"1234567890",msg,"1234567890",
					to,getRegistrationId(ShowMessage.this),"venkatesh.n@pickzy.com");*/

				

				msg=msg+"@"+finaltime+"@"+ownProfileImage;

				new SendMessageInBackground(AppConstants.SEND_MESSAGE,ShowMessage.this).
				execute(serverURL,"1234567891",msg,oppUserid,toUser,fromUser,email,trackUserId,"empty");

			} else {
				Utils.showToast("Enter any Text");
			}
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
							Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimgchat(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
						}
						return true;
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.activityPaused();
		MyApplication.setActivityName(AppConstants.EMPTY_ACTIVITY);
		MyApplication.setChatUserID("");

		unregisterReceiver(mHandleMessageReceiver);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	/*private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}*/

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(ShowMessage.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	/**
	 * Checks if the registration has expired.
	 *
	 * <p>To avoid the scenario where the device sends the registration to the
	 * server but the server loses it, the app developer may choose to re-register
	 * after REGISTRATION_EXPIRY_TIME_MS.
	 *
	 * @return true if the registration has expired.
	 */
	/*private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime =
				prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
		return System.currentTimeMillis() > expirationTime;
	}*/

	private void setFooterView() {
		footerView = ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_chat_listview,null,false);
		footTextiew = (TextView) footerView.findViewById(R.id.footer_chat_txt);
		footTextiew.setText("Sending...");
		msgListView.addFooterView(footerView);
	}

	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		// TODO Auto-generated method stub
		if(RespValue == AppConstants.SEND_MESSAGE) {
			footTextiew.setText("Sent");
		}
	}

	/*@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.e("HasFocusOut", ""+hasFocus);
		if(hasFocus){
			Log.e("HasFocus", ""+hasFocus);
			msgListView.setStackFromBottom(true);
			//msgListView.setSelection(msgListView.getCount());
			//hideKeyboard(v);
		}
	}*/
}
