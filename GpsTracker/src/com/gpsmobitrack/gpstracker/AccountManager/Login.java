package com.gpsmobitrack.gpstracker.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


/*import com.facebook.Request;
 import com.facebook.Response;
 import com.facebook.Session;
 import com.facebook.SessionState;
 import com.facebook.android.Facebook;
 import com.facebook.model.GraphUser;
 import com.facebook.widget.LoginButton;*/
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.Adapter.InviteListAdapter;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.MenuItems.InviteFragment;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.TrackingService.HandlerManager;
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

public class Login extends Activity implements OnClickListener, AsyncResponse {

	Button SignupBtn, LoginBtn;
	TextView ForgetPasswordTxt, SignUpTxt;
	EditText EmailTxt, PasswordTxt;
	String statusResp, msgResp, userId, statusCode, username, UserKey,
			userVerifiedStatus;
	String authKey, lastname, emailid, phoneno, gender, dob, country, state,
			prof_image_path, timeInterval;
	SessionManager session;
	SharedPreferences pref;
	String FBAccessToken;
	Editor editor;
	String emailId, password;
	GPSTracker gps;
	DBHandler dbHandler;
	public static String uploadFileName;
	Bitmap bitmapOrg;
	// Session fbSession;
	// GCM declaration

	// private static String APP_ID = "1602305300004608";

	GcmBroadcastReceiver mHandleMessageReceiver;
	static Context mContext;
	Context context;
	String regid;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	GoogleCloudMessaging gcm;
	String SENDER_ID = Config.GOOGLE_SENDER_ID;
	static boolean registered = false;
	// Facebook facebook;
	Calendar calendarDatePicker;
	EditText alertDOB;
	String fullName, firstName, fblastName, fbdob, fbgender, email,
			fb_prof_image_path, phoneNo, fbState, fbCountry, fbPssword,
			fbuserId;

	// LoginButton authButton;
	/*
	 * private final List<String> permissions; 
	 * public Login() { permissions =
	 * Arrays.asList("public_profile","email","user_birthday","user_location",
	 * "user_about_me"); }
	 */

	// GCM declaration ends
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);

		gps = new GPSTracker(Login.this);
		pref = Login.this
				.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		fullName = firstName = fblastName = fbdob = fbgender = email = fb_prof_image_path = fbState = fbCountry = fbuserId = fbPssword = phoneNo = "";
		// FBAccessToken="";
		mHandleMessageReceiver = new GcmBroadcastReceiver();
		mContext = Login.this;
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));
		context = getApplicationContext();

		//

		regid = getRegistrationId(context);
		if (regid.length() == 0) {
			registerBackground();
		}

		// GCM id code ends
		initComponents();
		resetDatas();
		/*
		 * authButton = (LoginButton)findViewById(R.id.authButton);
		 * authButton.setReadPermissions(permissions);
		 */
	}

	public void onResume() {
		super.onResume();
		pref = Login.this
				.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		editor = pref.edit();
		if (session.isLoggedIn()) {
			finish();
		}
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			Utils.showSettingsAlert(Login.this);
		}
		/*
		 * Session session = Session.getActiveSession(); if (session != null
		 * &&(session.isOpened() || session.isClosed()) ) {
		 * onSessionStateChange(session, session.getState(), null); }
		 */
	}

	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { super.onActivityResult(requestCode, resultCode, data);
	 * Session.getActiveSession().onActivityResult(this, requestCode,
	 * resultCode, data); }
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	

	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/*
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState); Session session =
	 * Session.getActiveSession(); Session.saveSession(session, outState); }
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {

		if (v == ForgetPasswordTxt) {
			showForgetPassAlert();
		}
		if (v == SignUpTxt) {
			Intent signUpIntent = new Intent(Login.this, SignUp.class);
			startActivity(signUpIntent);
		}
		if (v == LoginBtn) {
			if (isInternetOn()) {
				if (regid.length() == 0) {
					registerBackground();
				}
				emailId = EmailTxt.getText().toString().trim();
				password = PasswordTxt.getText().toString().trim();

				if ((emailId.equals("")) && (password.equals(""))) {
					Utils.showToast("Enter Details");
				} else {
					if (emailId.equals(""))
						Utils.showToast("Enter email-id");
					else if (!Utils.validEmail(emailId))
						Utils.showToast("Enter valid email-id");
					else if ((password.equals("")))
						Utils.showToast("Enter password");
				}
				if ((!emailId.equals("")) && (!password.equals(""))
						&& (Utils.validEmail(emailId))) {
					String url = AppConstants.LOGIN_URL;
					String emailtoAsyn = EmailTxt.getText().toString();
					String passwordtoAsyn = PasswordTxt.getText().toString();
					BasicNameValuePair emailNameValue = new BasicNameValuePair(
							AppConstants.EMAIL, emailtoAsyn);
					BasicNameValuePair passwordNameValue = new BasicNameValuePair(
							AppConstants.PASSWORD, passwordtoAsyn);
					BasicNameValuePair gcmRegIdNameValue = new BasicNameValuePair(
							AppConstants.GCM_REGID, regid);
					// Modified--------------------------------------------------------------
					BasicNameValuePair version = new BasicNameValuePair(
							AppConstants.VERSION, AppConstants.PRO);

					List<NameValuePair> loginValues = new ArrayList<NameValuePair>();
					loginValues.add(emailNameValue);
					loginValues.add(passwordNameValue);
					loginValues.add(gcmRegIdNameValue);
					loginValues.add(version);

					Log.e("TEST", "" + loginValues);

					new GpsAsyncTask(Login.this, url,
							AppConstants.SIGN_IN_RESP, this)
							.execute(loginValues);
				}
			} else {
				Utils.showToast("No Internet Connection");
			}
		}
	}

	// View Components
	public void initComponents() {

		session = new SessionManager(this);
		EmailTxt = (EditText) findViewById(R.id.email_txt);
		PasswordTxt = (EditText) findViewById(R.id.password_txt);
		SignUpTxt = (TextView) findViewById(R.id.Sign_UP_Text2);
		LoginBtn = (Button) findViewById(R.id.login_logpage_btn);
		ForgetPasswordTxt = (TextView) findViewById(R.id.forget_password);
		

		PasswordTxt.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		ForgetPasswordTxt.setOnClickListener(this);
		SignUpTxt.setOnClickListener(this);
		LoginBtn.setOnClickListener(this);
		ForgetPasswordTxt.setText(Html.fromHtml("<u>" + "Forgot Password ?"
				/*+ "</u> "*/));
		PasswordTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean onEditorAction(final TextView v, final int actionId,
					final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (isInternetOn()) {
						if (regid.length() == 0) {
							registerBackground();
						}
						emailId = EmailTxt.getText().toString().trim();
						password = PasswordTxt.getText().toString().trim();
						if ((emailId.equals("")) && (password.equals(""))) {
							Utils.showToast("Enter Details");
						} else {
							if (emailId.equals(""))
								Utils.showToast("Enter email-id");
							else if (!Utils.validEmail(emailId))
								Utils.showToast("Enter valid email-id");
							else if ((password.equals("")))
								Utils.showToast("Enter password");
						}
						if ((!emailId.equals("")) && (!password.equals(""))
								&& (Utils.validEmail(emailId))) {
							String url = AppConstants.LOGIN_URL;
							String emailtoAsyn = EmailTxt.getText().toString();
							String passwordtoAsyn = PasswordTxt.getText()
									.toString();
							BasicNameValuePair emailNameValue = new BasicNameValuePair(
									AppConstants.EMAIL, emailtoAsyn);
							BasicNameValuePair passwordNameValue = new BasicNameValuePair(
									AppConstants.PASSWORD, passwordtoAsyn);
							BasicNameValuePair gcmRegIdNameValue = new BasicNameValuePair(
									AppConstants.GCM_REGID, regid);
						
							//Modified ----------------------------------------------------------------------------
							BasicNameValuePair version = new BasicNameValuePair(AppConstants.VERSION, AppConstants.PRO);
							
							List<NameValuePair> loginValues = new ArrayList<NameValuePair>();
							loginValues.add(emailNameValue);
							loginValues.add(passwordNameValue);
							loginValues.add(gcmRegIdNameValue);
							loginValues.add(version);
						
							new GpsAsyncTask(Login.this, url,
									AppConstants.SIGN_IN_RESP, Login.this)
									.execute(loginValues);
						}
					} else {
						Utils.showToast("No Internet Connection");
					}
				}
				return true;
			}
		});
	}

	// Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle, String AlertText,
			final String StatusCode) {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView) dialog
				.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView) dialog
				.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText) dialog
				.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		cancelBtn.setVisibility(View.GONE);

		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);

		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * if(!email.isEmpty()){ dialog.dismiss(); addDetails(); } else{
				 */
				dialog.dismiss();
				// }

			}
		});

		dialog.show();
	}

	public void showSingleTextAlertInvalidLogin(String AlertTitle,
			String AlertText, final String StatusCode) {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView) dialog
				.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView) dialog
				.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText) dialog
				.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		cancelBtn.setVisibility(View.GONE);
		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);

		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Session session = Session.getActiveSession();
				 * session.closeAndClearTokenInformation(); email="";
				 */
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	public void showSingleTextAlertFBSingUp(String AlertTitle,
			String AlertText, final String StatusCode) {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView) dialog
				.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView) dialog
				.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText) dialog
				.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		cancelBtn.setVisibility(View.GONE);

		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);

		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (StatusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)
						|| StatusCode
								.equalsIgnoreCase(AppConstants.MAIL_SEND_FAILED)) {
					dialog.dismiss();
					pref = Login.this.getSharedPreferences(
							AppConstants.GPS_TRACKER_PREF, 0);
					String userId = pref.getString(AppConstants.USER_ID_PREF,
							null);
					session.createLoginSession(email, fbPssword, userId);
					Intent fragmentIntent = new Intent(Login.this,
							MainFragmentMenu.class);
					startActivity(fragmentIntent);
					finish();
				} else {
					/*
					 * Session session = Session.getActiveSession();
					 * session.closeAndClearTokenInformation(); email="";
					 */
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	// Forgot Password Alert
	private void showForgetPassAlert() {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView) dialog
				.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView) dialog
				.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText) dialog
				.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AppConstants.ALERT_TITLE_FORGET_PASSWORD);
		alertMsg.setVisibility(View.GONE);

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				if (isInternetOn()) {
					String email = alertEditTxt.getText().toString().trim();
					if (email != null && !email.equalsIgnoreCase("")
							&& email.length() > 0) {
						if (Utils.validEmail(email)) {
							String url = AppConstants.FORGOT_PASSWORD_URL;
							BasicNameValuePair emailNameValue = new BasicNameValuePair(
									AppConstants.EMAIL, email);
							List<NameValuePair> forgotPasswordValues = new ArrayList<NameValuePair>();
							forgotPasswordValues.add(emailNameValue);
							new GpsAsyncTask(Login.this, url,
									AppConstants.FORGOT_PASSWORD_RESP,
									Login.this).execute(forgotPasswordValues);
							dialog.dismiss();
						} else {
							Utils.showToast("Enter Valid E-mail Id");
						}
					} else {
						Utils.showToast("Enter E-mail Id");
					}
				} else {
					Utils.showToast("No Internet Connection");

				}
			}
		});

		alertEditTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (isInternetOn()) {
						String email = alertEditTxt.getText().toString().trim();
						if (email != null && !email.equalsIgnoreCase("")
								&& email.length() > 0) {
							if (Utils.validEmail(email)) {
								String url = AppConstants.FORGOT_PASSWORD_URL;

								BasicNameValuePair emailNameValue = new BasicNameValuePair(
										AppConstants.EMAIL, email);
								List<NameValuePair> forgotPasswordValues = new ArrayList<NameValuePair>();
								forgotPasswordValues.add(emailNameValue);

								new GpsAsyncTask(Login.this, url,
										AppConstants.FORGOT_PASSWORD_RESP,
										Login.this)
										.execute(forgotPasswordValues);
								dialog.dismiss();

							} else {
								Utils.showToast("Enter Valid E-mail Id");
							}
						} else {
							Utils.showToast("Enter E-mail Id");
						}
					} else {
						Utils.showToast("No Internet Connection");
					}
				}
				return false;
			}
		});
		dialog.show();
	}

	private void showActivateAlert() {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView) dialog
				.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView) dialog
				.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText) dialog
				.findViewById(R.id.alert_edit_txt);
		Button yesBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button noBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AppConstants.ALERT_TITLE);
		alertMsg.setText(AppConstants.ALERT_MSG_REACTIVATE);
		alertEditTxt.setVisibility(View.GONE);
		yesBtn.setBackgroundResource(R.drawable.yes_btn_exit);
		noBtn.setBackgroundResource(R.drawable.no_btn_exit);

		yesBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {

				if (!email.isEmpty()) {
					if (isInternetOn()) {
						String url = AppConstants.REACTIVATE_ACCOUNT_URL;
						int RespType = AppConstants.REACTIVATE_ACCOUNT_RESP;
						BasicNameValuePair emailNameValue = new BasicNameValuePair(
								AppConstants.EMAIL, email);
						BasicNameValuePair passwordNameValue = new BasicNameValuePair(
								AppConstants.PASSWORD, fbuserId);

						List<NameValuePair> loginValues = new ArrayList<NameValuePair>();
						loginValues.add(emailNameValue);
						loginValues.add(passwordNameValue);

						new GpsAsyncTask(Login.this, url, RespType, Login.this)
								.execute(loginValues);

						dialog.dismiss();
					} else {

						Utils.showToast("No Internet Connection");
					}

				} else {
					if (isInternetOn()) {
						String url = AppConstants.REACTIVATE_ACCOUNT_URL;
						int RespType = AppConstants.REACTIVATE_ACCOUNT_RESP;
						String emailtoAsyn = EmailTxt.getText().toString();
						String passwordtoAsyn = PasswordTxt.getText()
								.toString();
						BasicNameValuePair emailNameValue = new BasicNameValuePair(
								AppConstants.EMAIL, emailtoAsyn);
						BasicNameValuePair passwordNameValue = new BasicNameValuePair(
								AppConstants.PASSWORD, passwordtoAsyn);

						List<NameValuePair> loginValues = new ArrayList<NameValuePair>();
						loginValues.add(emailNameValue);
						loginValues.add(passwordNameValue);

						new GpsAsyncTask(Login.this, url, RespType, Login.this)
								.execute(loginValues);
						emailId = emailtoAsyn;
						password = passwordtoAsyn;

						dialog.dismiss();
					} else {

						Utils.showToast("No Internet Connection");
					}
				}

			}
		});
		noBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * if(!email.isEmpty()){ Session session =
				 * Session.getActiveSession();
				 * session.closeAndClearTokenInformation(); dialog.dismiss(); }
				 */
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	// Check Internet connection
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

	private void onSuccessLogin(JSONObject mJsonObject) {
		try {
			if (mJsonObject.has(AppConstants.AUTH_KEY)) {
				userId = mJsonObject.getString(AppConstants.AUTH_KEY);
				editor.putString(AppConstants.USER_ID_PREF, userId);
				editor.commit();
			}

			if (mJsonObject.has(AppConstants.USER_DETAILS)) {

				JSONObject jObjUserDetail = mJsonObject
						.getJSONObject(AppConstants.USER_DETAILS);
				userVerifiedStatus = jObjUserDetail
						.getString(AppConstants.VERIFIED_STATUS);
				if (jObjUserDetail.has(AppConstants.USER_KEY)) {
					UserKey = jObjUserDetail.getString(AppConstants.USER_KEY);
					Utils.printLog("UserKey",
							jObjUserDetail.getString(AppConstants.USER_KEY));
					editor.putString(AppConstants.USER_KEY_PREF, UserKey);
					editor.commit();
				}
				// Purchase status
				String purStat = jObjUserDetail
						.getString(AppConstants.PURCHASE_STATUS);
				int purchaseStatus = 0;
				if (purStat != null && purStat.length() > 0
						&& !purStat.equalsIgnoreCase("null")) {

					purchaseStatus = Integer.parseInt(jObjUserDetail
							.getString(AppConstants.PURCHASE_STATUS));
				}
				// Set purchase user type
				setSharPrefUserType(purchaseStatus);

				username = jObjUserDetail.getString(AppConstants.USER_NAME);
				editor.putString(AppConstants.USER_VERIFIED_STATUS_PREF,
						userVerifiedStatus);
				editor.putString(AppConstants.USER_NAME_PREF, username);
				editor.commit();
				lastname = jObjUserDetail
						.getString(AppConstants.USER_LAST_NAME);
				emailid = jObjUserDetail.getString(AppConstants.EMAIL_ID);
				phoneno = jObjUserDetail.getString(AppConstants.PHONE_NUMBER);
				gender = jObjUserDetail.getString(AppConstants.GENDER);
				dob = jObjUserDetail.getString(AppConstants.DOB);
				country = jObjUserDetail.getString(AppConstants.COUNTRY);
				state = jObjUserDetail.getString(AppConstants.STATE);
				prof_image_path = jObjUserDetail
						.getString(AppConstants.PROF_IMG);
				if (jObjUserDetail.has(AppConstants.TIME_INTERVAL)) {
					timeInterval = jObjUserDetail
							.getString(AppConstants.TIME_INTERVAL);

					System.out.println("Time intrevel........." + timeInterval);
					long updateTime = AppConstants.DEFAULT_TIME_INTERVAL;
					if (timeInterval != null && !timeInterval.equals("")) {
						try {
							updateTime = Long.parseLong(timeInterval);
						} catch (Exception e) {
							updateTime = AppConstants.DEFAULT_TIME_INTERVAL;
						}
					}
					editor.putLong(AppConstants.FREQ_UPDATE_PREF, updateTime);
					editor.commit();
				}
				Utils.printLog("Time login", timeInterval);
				dbHandler = new DBHandler(Login.this);
				dbHandler.insertProfile(username, lastname, phoneno, emailid,
						gender, dob, state, country, prof_image_path);
				session.createLoginSession(emailId, password, userId);
			}

			Intent fragmentIntent = new Intent(Login.this,
					MainFragmentMenu.class);
			startActivity(fragmentIntent);
			finish();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set user type in shared preference
	 * 
	 * @param status
	 */
	private void setSharPrefUserType(int status) {
		/*
		 * Log.d("Login","UY="+status); Editor editor = pref.edit();
		 * editor.putInt(AppConstants.USER_TYPE_PREF, status); editor.commit();
		 */
		SessionManager.setPurchaseSharePreference(status);
	}

	// Server API Response
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {

		try {
			if (serverResp != null) {

				if (RespValue == AppConstants.SIGN_IN_RESP) {

					Utils.printLog("Response in login", "" + serverResp);
					JSONObject jObjServerResp = new JSONObject(serverResp);
					statusCode = jObjServerResp
							.getString(AppConstants.STATUS_CODE);
					statusResp = jObjServerResp.getString(AppConstants.STATUS);
					msgResp = jObjServerResp.getString(AppConstants.MESSAGE);
					switch (statusCode) {

					case AppConstants.NEW_SUCCESS:
						onSuccessLogin(jObjServerResp);
						break;

					case AppConstants.NEW_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE, msgResp,
								AppConstants.NEW_FAILED);
						break;

					case AppConstants.INVALID_LOGIN:
						// showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_REACTIVATE,AppConstants.USER_DEACTIVATED);

						if (!email.isEmpty()) {
							showSingleTextAlertInvalidLogin(
									AppConstants.ALERT_TITLE,
									"You are already registered in GPS Tracker.Log in with GPS Tracker",
									AppConstants.NEW_FAILED);
						} else {
							showSingleTextAlertInvalidLogin(
									AppConstants.ALERT_TITLE, msgResp,
									AppConstants.NEW_FAILED);
						}
						break;

					case AppConstants.ACCOUNT_DEACTIVATED:
						showActivateAlert();
						break;

					default:
						break;
					}

				} else if (RespValue == AppConstants.FORGOT_PASSWORD_RESP) {

					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					statusResp = jObj.getString(AppConstants.STATUS);
					msgResp = jObj.getString(AppConstants.MESSAGE);

					switch (statusCode) {
					case AppConstants.NEW_SUCCESS:
						showSingleTextAlert(
								AppConstants.ALERT_TITLE,
								"Temporary Password has been sent to your E-mail Id",
								AppConstants.NEW_SUCCESS);
						break;

					case AppConstants.MAIL_SEND_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE, msgResp,
								AppConstants.MAIL_SEND_FAILED);
						break;

					case AppConstants.RESET_PASSWORD_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE, msgResp,
								AppConstants.RESET_PASSWORD_FAILED);
						break;

					case AppConstants.NEW_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE,
								"The E-mail Id is not Registered",
								AppConstants.NEW_FAILED);
						break;

					default:
						break;
					}
				} else if (RespValue == AppConstants.REACTIVATE_ACCOUNT_RESP) {

					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					statusResp = jObj.getString(AppConstants.STATUS);
					msgResp = jObj.getString(AppConstants.MESSAGE);

					switch (statusCode) {
					case AppConstants.NEW_SUCCESS:
						onSuccessLogin(jObj);
						break;

					case AppConstants.ALREADY_ACTIVATED_USER:
						showSingleTextAlert(AppConstants.ALERT_TITLE, msgResp,
								AppConstants.RESET_PASSWORD_FAILED);
						break;

					case AppConstants.NEW_FAILED:
						showSingleTextAlert(AppConstants.ALERT_TITLE, msgResp,
								AppConstants.NEW_FAILED);
						break;

					default:
						break;
					}
				} else if (RespValue == AppConstants.SIGN_UP_RESP) {
					JSONObject jObj = null;
					try {
						jObj = new JSONObject(serverResp);
						statusCode = jObj.getString(AppConstants.STATUS_CODE);
						statusResp = jObj.getString(AppConstants.STATUS);
						msgResp = jObj.getString(AppConstants.MESSAGE);
						if (jObj.has(AppConstants.AUTH_KEY)) {
							userId = jObj.getString(AppConstants.AUTH_KEY);

						}
						if (jObj.has(AppConstants.USER_KEY)) {

							UserKey = jObj.getString(AppConstants.USER_KEY);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					switch (statusCode) {

					case AppConstants.NEW_SUCCESS:

						if (jObj.has(AppConstants.AUTH_KEY)) {
							editor.putString(AppConstants.USER_ID_PREF, userId);
							editor.commit();
						}
						if (jObj.has(AppConstants.USER_KEY)) {
							editor.putString(AppConstants.USER_KEY_PREF,
									UserKey);
							editor.commit();
						}
						editor.putString(AppConstants.USER_NAME_PREF, firstName);
						editor.commit();
						dbHandler = new DBHandler(Login.this);
						dbHandler.insertProfile(firstName, null, phoneNo,
								email, null, null, null, null, null);
						showSingleTextAlertFBSingUp(
								AppConstants.ALERT_TITLE_CONFIRM,
								AppConstants.ALERT_CONFIRM_MESSAGE,
								AppConstants.NEW_SUCCESS);
						break;

					case AppConstants.MAIL_SEND_FAILED:

						if (jObj.has(AppConstants.AUTH_KEY)) {
							editor.putString(AppConstants.USER_ID_PREF, userId);
							editor.commit();
						}
						showSingleTextAlertFBSingUp(AppConstants.ALERT_TITLE,
								msgResp, AppConstants.MAIL_SEND_FAILED);
						break;

					case AppConstants.NEW_FAILED:
						showSingleTextAlertFBSingUp(AppConstants.ALERT_TITLE,
								msgResp, AppConstants.NEW_FAILED);
						break;

					case AppConstants.ALREADY_REGISTERED:
						showSingleTextAlertFBSingUp(AppConstants.ALERT_TITLE,
								msgResp, AppConstants.ALREADY_REGISTERED);
						break;

					case AppConstants.EMPTY_VALUE:
						showSingleTextAlertFBSingUp(AppConstants.ALERT_TITLE,
								msgResp, AppConstants.EMPTY_VALUE);
						break;

					default:
						break;
					}
				}

			} else {
				// Pending alert implementation
				Utils.showToast("No Response From Server");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// GCM methods

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(ShowMessage.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME,
				-1);
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
		long expirationTime = System.currentTimeMillis()
				+ REGISTRATION_EXPIRY_TIME_MS;
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
		String email = EmailTxt.getText().toString().trim();
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

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
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
				// mDisplay.append(msg + "\n");
				Utils.printLog("GCM Login", msg);
			}
		}.execute(null, null, null);
	}

	private void resetDatas() {
		if (MainFragmentMenu.myList != null) {
			if (MainFragmentMenu.myList.size() > 0) {
				MainFragmentMenu.myList.clear();
			}
		}
		if (InviteFragment.inviteArrList != null) {
			if (InviteFragment.inviteArrList.size() > 0) {
				InviteFragment.inviteArrList.clear();
			}
		}
		if (InviteFragment.inviteListLast != null) {
			if (InviteFragment.inviteListLast.size() > 0) {
				InviteFragment.inviteListLast.clear();
			}
		}
		if (InviteListAdapter.inviteRemoveList != null) {
			if (InviteListAdapter.inviteRemoveList.size() > 0) {
				InviteListAdapter.inviteRemoveList.clear();
			}
		}
		if (HomeDetailPage.stalatitudeArry != null) {

			if (HomeDetailPage.stalatitudeArry.size() > 0) {
				HomeDetailPage.stalatitudeArry.clear();
			}
		}
		if (HomeDetailPage.stalongitudeArry != null) {
			if (HomeDetailPage.stalongitudeArry.size() > 0) {
				HomeDetailPage.stalongitudeArry.clear();
			}
		}
		HomeDetailPage.isTrackingON = false;
		HomeDetailPage.goneBackground = false;
		HandlerManager.trackUserId = "";
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	
	 /* private void onSessionStateChange(final Session session, SessionState
	  state,Exception exception) { if (state.isOpened()) { String token=
	  session.getAccessToken(); editor = pref.edit();
	  editor.putString(AppConstants.Access_Token_PREF, token); editor.commit();
	  Request request = Request.newMeRequest(session, new
	  Request.GraphUserCallback() {
	  
	  @Override public void onCompleted(GraphUser user, Response response) { if
	  (session == Session.getActiveSession()) { if (user != null) {
	  fbuserId=user.getId(); Log.i("FBuserID", fbuserId);
	  if(user.getName()!=null){ fullName=user.getName();
	  }if(user.getFirstName()!=null){ firstName=user.getFirstName();
	  }if(user.getLastName()!=null){ fblastName=user.getLastName();
	  }if(user.getBirthday()!=null){ fbdob=user.getBirthday();
	  }if(user.asMap().get("gender").toString()!=null){
	  fbgender=user.asMap().get("gender").toString();
	  
	  }if((String) user.getProperty("email")!=null){ email=(String)
	  user.getProperty("email"); }
	  
	  Log.i("FBuserEmail", email); Log.i("FBuserFirstname", firstName);
	  
	  try { URL imgUrl = new URL("http://graph.facebook.com/" + user.getId() +
	  "/picture?type=large"); fb_prof_image_path=imgUrl.toString(); } catch
	  (MalformedURLException e) { e.printStackTrace(); } FBLoginAPI(); } } }
	  }); Request.executeBatchAsync(request); Log.i("Facebook Login",
	  "Logged in..."); } else if (state.isClosed()) { Log.i("Facebook Logout",
	  "Logged out..."); email=""; String token=""; editor = pref.edit();
	  editor.putString(AppConstants.Access_Token_PREF, token); editor.commit();
	  
	  } }*/
	  
	  public void addDetails(){
	  
	  final Dialog dialog = new Dialog(Login.this,
	  android.R.style.Theme_Translucent);
	  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	  dialog.setCancelable(false);
	  dialog.setContentView(R.layout.alert_dialog_fb_login); final TextView
	  alertTitle = (TextView)dialog.findViewById(R.id.alert_title_fb); alertDOB
	  = (EditText)dialog.findViewById(R.id.alert_edit_txt_dob); final EditText
	  alertPhno = (EditText)dialog.findViewById(R.id.alert_edit_txt_phno);
	  final EditText alertPwd =
	  (EditText)dialog.findViewById(R.id.alert_edit_txt_pwd); final EditText
	  alertState = (EditText)dialog.findViewById(R.id.alert_edit_txt_state);
	  final EditText alertCountry =
	  (EditText)dialog.findViewById(R.id.alert_edit_txt_country); final
	  EditText alertFName =
	  (EditText)dialog.findViewById(R.id.alert_edit_txt_fname); final EditText
	  alertLName = (EditText)dialog.findViewById(R.id.alert_edit_txt_lname);
	  final EditText alertEmailID =
	  (EditText)dialog.findViewById(R.id.alert_edit_txt_emailid); final
	  EditText alertGender =
	  (EditText)dialog.findViewById(R.id.alert_edit_txt_gender); Button okBtn =
	  (Button) dialog.findViewById(R.id.alert_ok_btn_fb); Button cancelBtn =
	  (Button) dialog.findViewById(R.id.alert_cancel_btn_fb);
	  alertTitle.setText(AppConstants.ALERT_FB_LOGIN);
	  alertPwd.setVisibility(View.GONE); alertState.setVisibility(View.GONE);
	  alertCountry.setVisibility(View.GONE);
	  alertLName.setVisibility(View.GONE);
	  alertGender.setVisibility(View.GONE); alertDOB.setVisibility(View.GONE);
	  
	  if(firstName!=null&& !firstName.isEmpty()){
	  alertFName.setVisibility(View.GONE); } if(email!=null&&
	  !email.isEmpty()){ alertEmailID.setVisibility(View.GONE); }
	  
	  cancelBtn.setOnClickListener(new OnClickListener() {
	 /* 
	  @Override public void onClick(View v) { Session session =
	  Session.getActiveSession(); session.closeAndClearTokenInformation();
	  email=""; dialog.dismiss(); } }); okBtn.setOnClickListener(new
	  OnClickListener() {*/
	  
	  @Override public void onClick(View v) {
	  
	  if(firstName.isEmpty()) firstName=alertFName.getText().toString().trim();
	  if(phoneNo.equals("")) phoneNo=alertPhno.getText().toString().trim();
	  if(email.isEmpty()) email=alertEmailID.getText().toString().trim();
	  if(validateDataFields()){ dialog.dismiss(); sendInformationsToServer(); }
	  
	  }}); dialog.show(); }
	  
	  private boolean validateDataFields(){ if(firstName.isEmpty()){
	  Utils.showToast(AppConstants.FNAME_EMPTY); return false; }else
	  if(email.isEmpty()){ Utils.showToast(AppConstants.EMAIL_EMPTY); return
	  false; }else if(!Utils.validEmail(email)) {
	  Utils.showToast(AppConstants.INVALID_EMAIL); return false; }else
	  if(phoneNo.isEmpty()){ Utils.showToast(AppConstants.PHONE_NO_EMPTY);
	  return false; } return true;
	  
	  }
	  
	  @SuppressWarnings("unchecked") private void sendInformationsToServer(){
	  if(isInternetOn()){ if(regid.length() == 0) { registerBackground(); }
	  String latitude = String.valueOf(gps.getLatitude()); String longitute =
	  String.valueOf(gps.getLongitude()); String phoneModel =
	  android.os.Build.MODEL; String osVersion =
	  android.os.Build.VERSION.RELEASE; String url = AppConstants.REGISTER_URL;
	  
	  BasicNameValuePair emailValue = new BasicNameValuePair("email", email);
	  BasicNameValuePair phoneValue = new BasicNameValuePair("phone", phoneNo);
	  BasicNameValuePair passwordValue = new BasicNameValuePair("password",
	  fbuserId); BasicNameValuePair latValue = new
	  BasicNameValuePair("latitude", latitude); BasicNameValuePair longValue =
	  new BasicNameValuePair("longitude", longitute); BasicNameValuePair
	  phoneModelValue = new BasicNameValuePair("device_model", phoneModel);
	  BasicNameValuePair phoneOsValue = new
	  BasicNameValuePair("device_os_version", osVersion); BasicNameValuePair
	  FbFirstName = new BasicNameValuePair("firstname", firstName);
	  BasicNameValuePair lastName = new BasicNameValuePair("lastname",
	  fblastName); BasicNameValuePair gcmRegid = new
	
	  BasicNameValuePair(AppConstants.GCM_REGID, regid); List<NameValuePair>
	  registerValues = new ArrayList<NameValuePair>();
	  registerValues.add(emailValue); registerValues.add(phoneValue);
	  registerValues.add(passwordValue); registerValues.add(latValue);
	  registerValues.add(longValue); registerValues.add(phoneModelValue);
	  registerValues.add(phoneOsValue); registerValues.add(FbFirstName);
	  registerValues.add(lastName); registerValues.add(gcmRegid);
	  System.out.println(registerValues);
	  
	  new GpsAsyncTask(Login.this, url, AppConstants.SIGN_UP_RESP,
	  Login.this).execute(registerValues); } else{
	  Utils.showToast("No Internet Connection"); } }
	  
	  
	  @SuppressWarnings("unchecked") public void FBLoginAPI(){
	  
	  String url =AppConstants.LOGIN_URL; BasicNameValuePair emailNameValue =
	  new BasicNameValuePair(AppConstants.EMAIL, email); BasicNameValuePair
	  passwordNameValue = new BasicNameValuePair(AppConstants.PASSWORD,
	  fbuserId); BasicNameValuePair gcmRegIdNameValue = new
	  BasicNameValuePair(AppConstants.GCM_REGID, regid); List<NameValuePair>
	  loginValues = new ArrayList<NameValuePair>();
	  loginValues.add(emailNameValue); loginValues.add(passwordNameValue);
	  loginValues.add(gcmRegIdNameValue); System.out.println(loginValues); new
	  GpsAsyncTask(Login.this, url,AppConstants.SIGN_IN_RESP,
	  this).execute(loginValues); }
	 }
