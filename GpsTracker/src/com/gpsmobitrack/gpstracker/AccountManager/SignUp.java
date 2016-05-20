package com.gpsmobitrack.gpstracker.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSTracker;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.chat.Config;
import com.gpsmobitrack.gpstracker.chat.GcmBroadcastReceiver;
import com.gpsmobitrack.gpstracker.chat.ServerUtilities;
import com.gpsmobitrack.gpstracker.chat.ShowMessage;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;

public class SignUp extends Activity implements OnClickListener, AsyncResponse, OnEditorActionListener {

	EditText EmailSignTxt,PhoneTxt,PasswordSignTxt,UserNameTxt;
	Button CreateAccount;
	TextView TermsTxt;
	GPSTracker gps;
	ProgressDialog alertProgressDialog = null;
	String statusResp,msgResp,userId,statusCode,UserKey;
	SessionManager session;
	String emailtoAsyn, passwordtoAsyn,nametoAsyn;
	DBHandler dbHandler;
	SharedPreferences pref;
	Editor editor;

	//GCM Code
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
	//GCM code ends

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_page);
		initComponents();
	}

	public void onResume(){
		super.onResume();
		pref = SignUp.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		editor = pref.edit();

		if(!isInternetOn()){
			Utils.showToast("No Internet connection");
		}
		if(session.isLoggedIn()){
			finish();
		}
		if(!gps.canGetLocation()){
			LocationManager locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
			final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!gpsEnabled) {
				Utils.showSettingsAlert(SignUp.this);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//ServerUtilities.unregister(SignUp.this, regid);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == TermsTxt) {
			if(isInternetOn()){
				Intent intentTerms = new Intent(SignUp.this, TermsAndConditions.class);
				startActivity(intentTerms);
			} else {

				Utils.showToast("No Internet Connection");
			}

		}
		if(v == CreateAccount){

			if(isInternetOn()){
				String email = EmailSignTxt.getText().toString().trim();
				String phoneno = PhoneTxt.getText().toString().trim();
				String password = PasswordSignTxt.getText().toString().trim();
				String username = UserNameTxt.getText().toString().trim();

				if ((email.equals("")) && (phoneno.equals("")) && (password.equals("")) && (username.equalsIgnoreCase(""))){
					Utils.showToast("Enter Details");
				} else {
					if(email.equals("")) {
						Utils.showToast("Enter Email-id");
					} else {
						if(Utils.validEmail(email) != true) {
							Utils.showToast("Enter Valid Email-id");
						}
					}
					if (phoneno.equals("")) {
						Utils.showToast("Enter Phone no");
					} else {
						if(phoneno.length() <= 9) {
							Utils.showToast("Enter Valid Phone no");
						}
					}
					if (password.equals("")){
						Utils.showToast("Enter Password");
					} else {
						if(password.length()<6) {
							Utils.showToast("Password must be atleast 6 characters");
						}
					}
					if(username.equalsIgnoreCase("")){
						Utils.showToast("Enter UserName");
					} 
				}
				if((!email.equals("")) && (!phoneno.equals("")) && (!password.equals("")) && (Utils.validEmail(email) == true)
						&& (!(phoneno.length() <= 9))&& (password.length()>=6) && (username.length()!=0)){

					//GCM Code
					mHandleMessageReceiver = new GcmBroadcastReceiver();
					mContext = SignUp.this;
					registerReceiver(mHandleMessageReceiver, 
							new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
					context = getApplicationContext();
					regid = getRegistrationId(context);
					if(regid.length() == 0) {
						registerBackground();
					}

					//GCM code ends

					showConfirmPassAlert();
				}
			} else {
				Utils.showToast("No Internet Connection");
			}
		}
	}

	//View Components
	public void initComponents(){

		session = new SessionManager(SignUp.this);
		gps = new GPSTracker(SignUp.this);

		EmailSignTxt = (EditText)findViewById(R.id.email_signup_txt);
		UserNameTxt  = (EditText)findViewById(R.id.username_txt);
		PhoneTxt = (EditText)findViewById(R.id.phone_txt);
		PasswordSignTxt = (EditText)findViewById(R.id.pass_signup_txt);
		TermsTxt = (TextView)findViewById(R.id.terms_cond_txt);
		CreateAccount = (Button)findViewById(R.id.create_account_btn);

		PasswordSignTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
		CreateAccount.setOnClickListener(SignUp.this);
		TermsTxt.setOnClickListener(SignUp.this);

		TermsTxt.setText(Html.fromHtml("<u>" + "Terms and Conditions" + "<u>"));
		PasswordSignTxt.setOnEditorActionListener(this);
	}

	//Alert Dialog Confirm Password
	private void showConfirmPassAlert() {
		// TODO Auto-generated method stub

		final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AppConstants.ALERT_TITLE_CONFIRM_PASSWORD);
		alertEditTxt.setHint(AppConstants.ALERT_HINT_CONFIRM_PASSWORD);
		alertEditTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
		alertMsg.setVisibility(View.GONE);

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		okBtn.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isInternetOn()) {


					String passwordStr = PasswordSignTxt.getText().toString().trim();
					String conformPassword = alertEditTxt.getText().toString().trim();
					if(passwordStr.equals(conformPassword)){
						String email = EmailSignTxt.getText().toString().trim();
						emailtoAsyn = email;
						String phoneno = PhoneTxt.getText().toString().trim();
						String password = PasswordSignTxt.getText().toString().trim();
						String userName = UserNameTxt.getText().toString().trim();

						passwordtoAsyn = password;
						String latitude = String.valueOf(gps.getLatitude());
						String longitute = String.valueOf(gps.getLongitude());
						String phoneModel = android.os.Build.MODEL;
						String osVersion = android.os.Build.VERSION.RELEASE;
						String url = AppConstants.REGISTER_URL;

						BasicNameValuePair emailValue= new BasicNameValuePair("email", email);
						BasicNameValuePair phoneValue= new BasicNameValuePair("phone", phoneno);
						BasicNameValuePair passwordValue= new BasicNameValuePair("password", password);
						BasicNameValuePair latValue = new BasicNameValuePair("latitude", latitude);
						BasicNameValuePair longValue= new BasicNameValuePair("longitude", longitute);
						BasicNameValuePair phoneModelValue = new BasicNameValuePair("device_model", phoneModel);
						BasicNameValuePair phoneOsValue= new BasicNameValuePair("device_os_version", osVersion);
						BasicNameValuePair firstName= new BasicNameValuePair("firstname", userName);
						BasicNameValuePair lastName= new BasicNameValuePair("lastname", "");
						BasicNameValuePair gcmRegid= new BasicNameValuePair(AppConstants.GCM_REGID, regid);

						//Modified ----------------------------------------------------------------------------
						BasicNameValuePair version = new BasicNameValuePair(AppConstants.VERSION, AppConstants.PRO);


						List<NameValuePair> registerValues = new ArrayList<NameValuePair>();
						registerValues.add(emailValue);
						registerValues.add(phoneValue);
						registerValues.add(passwordValue);
						registerValues.add(latValue);
						registerValues.add(longValue);
						registerValues.add(phoneModelValue);
						registerValues.add(phoneOsValue);
						registerValues.add(firstName);
						registerValues.add(lastName);
						registerValues.add(gcmRegid);
						registerValues.add(version);

						new GpsAsyncTask(SignUp.this, url, AppConstants.SIGN_UP_RESP, SignUp.this).execute(registerValues);
						dialog.dismiss();

					} else {
						Utils.showToast("Password does not match");
						dialog.setCancelable(false);
					}

				} else {

					Utils.showToast("No Internet connection");
				}

			}
		});

		alertEditTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId == EditorInfo.IME_ACTION_DONE ){

					if (isInternetOn()) {


						String passwordStr = PasswordSignTxt.getText().toString().trim();
						String conformPassword = alertEditTxt.getText().toString().trim();
						if(passwordStr.equals(conformPassword)){
							String email = EmailSignTxt.getText().toString().trim();
							emailtoAsyn = email;
							String phoneno = PhoneTxt.getText().toString().trim();
							String password = PasswordSignTxt.getText().toString().trim();
							String userName = UserNameTxt.getText().toString().trim();

							passwordtoAsyn = password;
							String latitude = String.valueOf(gps.getLatitude());
							String longitute = String.valueOf(gps.getLongitude());
							String phoneModel = android.os.Build.MODEL;
							String osVersion = android.os.Build.VERSION.RELEASE;
							String url = AppConstants.REGISTER_URL;

							BasicNameValuePair emailValue = new BasicNameValuePair("email", email);
							BasicNameValuePair phoneValue = new BasicNameValuePair("phone", phoneno);
							BasicNameValuePair passwordValue = new BasicNameValuePair("password", password);
							BasicNameValuePair latValue = new BasicNameValuePair("latitude", latitude);
							BasicNameValuePair longValue = new BasicNameValuePair("longitude", longitute);
							BasicNameValuePair phoneModelValue = new BasicNameValuePair("device_model", phoneModel);
							BasicNameValuePair phoneOsValue = new BasicNameValuePair("device_os_version", osVersion);
							BasicNameValuePair firstName = new BasicNameValuePair("firstname", userName);
							BasicNameValuePair lastName = new BasicNameValuePair("lastname", "");
							BasicNameValuePair gcmRegid = new BasicNameValuePair(AppConstants.GCM_REGID, regid);
					
							//Modified ----------------------------------------------------------------------------
							BasicNameValuePair version = new BasicNameValuePair(AppConstants.VERSION, AppConstants.PRO);
						
							List<NameValuePair> registerValues = new ArrayList<NameValuePair>();
							registerValues.add(emailValue);
							registerValues.add(phoneValue);
							registerValues.add(passwordValue);
							registerValues.add(latValue);
							registerValues.add(longValue);
							registerValues.add(phoneModelValue);
							registerValues.add(phoneOsValue);
							registerValues.add(firstName);
							registerValues.add(lastName);
							registerValues.add(gcmRegid);
							registerValues.add(version);
						
							
							new GpsAsyncTask(SignUp.this, url, AppConstants.SIGN_UP_RESP, SignUp.this).execute(registerValues);
							dialog.dismiss();
						} else {
							Utils.showToast("Password does not match");
							dialog.setCancelable(false);
						}
					} else {
						Utils.showToast("No Internet connection");
					}
				}
				return false;
			}
		});
		dialog.show();
	}

	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText,final String StatusCode){

		final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
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
				// TODO Auto-generated method stub
				if(StatusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)||StatusCode.equalsIgnoreCase(AppConstants.MAIL_SEND_FAILED)){
					dialog.dismiss();
					session.createLoginSession(emailtoAsyn, passwordtoAsyn, userId);
					Intent fragmentIntent = new Intent(SignUp.this,MainFragmentMenu.class);
					startActivity(fragmentIntent);
					finish();

				} else {

					dialog.dismiss();
				}
			}
		});

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

	//API Response
	@Override
	public void onProcessFinish(String serverResp,int RespValue ) {

		try{
			if(serverResp != null){

				JSONObject jObj = null;

				try {
					jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					statusResp = jObj.getString(AppConstants.STATUS);
					msgResp = jObj.getString(AppConstants.MESSAGE);
					if(jObj.has(AppConstants.AUTH_KEY)){
						userId = jObj.getString(AppConstants.AUTH_KEY);

					}
					if(jObj.has(AppConstants.USER_KEY)){

						UserKey = jObj.getString(AppConstants.USER_KEY);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				switch (statusCode) {

				case AppConstants.NEW_SUCCESS:

					if(jObj.has(AppConstants.AUTH_KEY)){
						editor.putString(AppConstants.USER_ID_PREF, userId);
						editor.commit();
					}

					if(jObj.has(AppConstants.USER_KEY)){
						editor.putString(AppConstants.USER_KEY_PREF, UserKey);
						editor.commit();
					}

					String userNameDB =UserNameTxt.getText().toString();
					String phoneNoDB =PhoneTxt.getText().toString();
					String emailIdDB =  EmailSignTxt.getText().toString();
					editor.putString(AppConstants.USER_NAME_PREF, userNameDB);
					editor.commit();
					dbHandler = new DBHandler(SignUp.this);
					dbHandler.insertProfile(userNameDB, null,phoneNoDB ,emailIdDB, null, null, null, null, null);

					showSingleTextAlert(AppConstants.ALERT_TITLE_CONFIRM,AppConstants.ALERT_CONFIRM_MESSAGE,AppConstants.NEW_SUCCESS);

					break;

				case AppConstants.MAIL_SEND_FAILED:

					if(jObj.has(AppConstants.AUTH_KEY)){
						editor.putString(AppConstants.USER_ID_PREF, userId);
						editor.commit();
					}
					showSingleTextAlert(AppConstants.ALERT_TITLE,msgResp,AppConstants.MAIL_SEND_FAILED);
					break;

				case AppConstants.NEW_FAILED:
					showSingleTextAlert(AppConstants.ALERT_TITLE,msgResp,AppConstants.NEW_FAILED);
					break;

				case AppConstants.ALREADY_REGISTERED:
					showSingleTextAlert(AppConstants.ALERT_TITLE,msgResp,AppConstants.ALREADY_REGISTERED);
					break;

				case AppConstants.EMPTY_VALUE:
					showSingleTextAlert(AppConstants.ALERT_TITLE,msgResp,AppConstants.EMPTY_VALUE);
					break;

				default:
					break;
				}

			} else {

				Utils.showToast("No Response From Server");
			}
			}catch(Exception ex){}
	}



	//GCM methods

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(ShowMessage.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime =
				prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
		return System.currentTimeMillis() > expirationTime;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void setRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
		String email = EmailSignTxt.getText().toString().trim();
		registered = ServerUtilities.register(context, email, regid);
	}

	private void registerBackground() {
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
				Utils.printLog("GCM Login", msg);
			}
		}.execute(null, null, null);
	}
	//GCM Method ends

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_DONE) {

			if(isInternetOn()){
				String email = EmailSignTxt.getText().toString().trim();
				String phoneno = PhoneTxt.getText().toString().trim();
				String password = PasswordSignTxt.getText().toString().trim();
				String username = UserNameTxt.getText().toString().trim();

				if ((email.equals("")) && (phoneno.equals("")) && (password.equals("")) && (username.equalsIgnoreCase(""))){
					Utils.showToast("Enter Details");
				} else {
					if(email.equals("")) {
						Utils.showToast("Enter Email-id");
					} else {
						if(Utils.validEmail(email) != true) {
							Utils.showToast("Enter Valid Email-id");
						}
					}
					if (phoneno.equals("")) {
						Utils.showToast("Enter Phone no");
					} else {
						if(phoneno.length() <= 9) {
							Utils.showToast("Enter Valid Phone no");
						}
					}
					if (password.equals("")){
						Utils.showToast("Enter Password");
					} else {
						if(password.length()<6) {
							Utils.showToast("Password must be atleast 6 characters");
						}
					}
					if(username.equalsIgnoreCase("")){
						Utils.showToast("Enter UserName");
					} 
				}
				if((!email.equals("")) && (!phoneno.equals("")) && (!password.equals("")) && (Utils.validEmail(email) == true)
						&& (!(phoneno.length() <= 9))&& (password.length()>=6) && (username.length()!=0)){

					//GCM Code
					mHandleMessageReceiver = new GcmBroadcastReceiver();
					mContext = SignUp.this;
					registerReceiver(mHandleMessageReceiver, 
							new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
					context = getApplicationContext();
					regid = getRegistrationId(context);
					if(regid.length() == 0) {
						registerBackground();
					}
					//GCM code ends
					showConfirmPassAlert();
				}
			} else {
				Utils.showToast("No Internet Connection");
			}

		}
		return true;
	}
}
