package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


//import com.facebook.Session;
import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.BackgroundService.BackgroundService;
import com.gpsmobitrack.gpstracker.CustomizedView.MySwitch;
import com.gpsmobitrack.gpstracker.Inappbilling.utilsl.IabHelper;
import com.gpsmobitrack.gpstracker.Inappbilling.utilsl.IabHelper.OnIabSetupFinishedListener;
import com.gpsmobitrack.gpstracker.Inappbilling.utilsl.IabResult;
import com.gpsmobitrack.gpstracker.Inappbilling.utilsl.Inventory;
import com.gpsmobitrack.gpstracker.Inappbilling.utilsl.Purchase;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncGet;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class SettingsPage extends Fragment implements OnClickListener,AsyncResponse, OnCheckedChangeListener,OnItemSelectedListener {

	public static final String TAG = "SettingsPage";
	TextView userName,deactivate,update,rating;
	//Button everyOneMin,everyTenMin,everyFiveMin;
	Spinner updateSpinner;
	MySwitch backUpdateToggle,publicPrivacy;
	SharedPreferences pref;
	Editor editor;
	SessionManager session;
	final int NO_UPDATE_INT = 6;
	final int UPDATE_INT = 5;
	String updateDurationText [] = {"1 Minute","2 Minutes","3 Minutes","5 Minutes","10 Minutes","15 Minutes","30 Minutes","60 Minutes"};
	int updateDurationValue [] = {1,2,3,5,10,15,30,60};
	String FBAccessToken;
	//Remove Ads textview
	TextView removeAdsTxt;
	String duration="";

	String purchaseStatus="";

	int selectposi;
	/**
	 * Enum for type of purchase by use
	 * 
	 */
	private enum PurchaseClicked{
		ADS,THREE_MIN,ONE_MIN,
		ONE_MIN_ONE_MONTH,ONE_MIN_THREE_MONTH,ONE_MIN_SIX_MONTH,ONE_MIN_ONE_YEAR,TWO_MIN_ONE_MONTH,TWO_MIN_THREE_MONTH,TWO_MIN_SIX_MONTH,TWO_MIN_ONE_YEAR,THREE_MIN_ONE_MONTH,THREE_MIN_THREE_MONTH,THREE_MIN_SIX_MONTH,THREE_MIN_ONE_YEAR,
	}
	/**
	 * Enum for user type
	 * 
	 * 0 -- Show ads , block 1 and 3 minutes update frequency.
	 * 
	 * 1 -- block 1 minutes update frequency but disable ads and 3 minute.
	 * 
	 * 2 -- unblock 1 and 3 minutes update frequency,disable showing ads.
	 * 
	 */
	public enum PurchaseStatus{
		NORMAL_USER(0),PARTIAL_ACCESS_USER(1),SEMI_FULL_ACCESS_USER(2),FULL_ACCESS_USER(3);
		private int status;
		PurchaseStatus(int status){
			this.status = status;
		}
		public int getStatus(){
			return status;
		}
	}
	// Product id for Ads
	private static final String SKU_ADS_THREE_MIN_PRO_ID ="partialpid"; //Modify with real product id
	// Product id for Update Frequency
	private static final String SKU_ONE_MIN_UPT_FRQ_PRO_ID ="fullpid"; //Modify with real product id

	private static final String SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH ="fullpidonemonths";
	private static final String SKU_ONE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH ="fullpidthreemonth";
	private static final String SKU_ONE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH ="fullpidsixmonth";
	private static final String SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR ="fullpidoneyear";

	private static final String SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_MONTH ="semipidonemonth";
	private static final String SKU_TWO_MIN_UPT_FRQ_PRO_ID_THREE_MONTH ="semipidthreemonth";
	private static final String SKU_TWO_MIN_UPT_FRQ_PRO_ID_SIX_MONTH ="semipidsixmonth";
	private static final String SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_YEAR ="semipidoneyear";

	private static final String SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH ="partialpidonemonth";
	private static final String SKU_THREE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH ="partialpidthreemonth";
	private static final String SKU_THREE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH ="partialpidsixmonth";
	private static final String SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR ="partialpidoneyear";


	// Encoded License key 
	//	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAge4mpCW+eEue1zyyHJqt3pwYcuvklElPwTsuXTmN7IyXLdL2jpf3W0ZK7P8R0n6LPuMiHKv01S8t/IoB61NRI5vsCy/IcwGCG8D6oaokxRsmCAO0kEMA7l28UPDdAFkOMrZ83KmDjCy/nBJkdFbj2Sm2dy2o6jvfQMraA7KqT/pE3Iyy7xWsChpsvc5LbXPFR8JB+yHglq9j6qPUedlXcUWFMOzHYscbTytct6YMi284cZ5pAIIwsscV8QBJh7yc8G/iu4qnFkHBtTowIay2boCStyNGGgZ/QSRAS6K7/PVo/C9x0UkyUBTBOvIff/8j60r6jZQhc8Y3K7wbWVTayQIDAQAB";//Modify with real encoded key
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw6Y86GkKcGZv1ab0KSWNQJPxKFpoXvYT3ozaglYwimbJemoL86NIMHghYqsYj4CTPF3nx3wUm6iSepfg84eMQsS6+6WrdblDe1IvqwGksn1hTrwsvcE8zSI22LYYeRNo3Y3Sut5RWXbSYL8+nUhG6lWimO0r5/91rJ+bUo6LvGFE+cfV/BFYGxVifN+IaSCes42xmAGto2b0c4jCuZwrjD+8KN8+9Kpw92K696Dg0YxNE4/K4iiBIqdxIJHrt+Je5OnchV+83Tp63UqsaMicmhwyzc3QRh2/Aq7JuucjLVZ46nX+45s64r2V6AuipXt8CnPl76lr00JcMzkKNghT7wIDAQAB";
	//User Status
	PurchaseStatus userType;
	//Type of Purchase clicked by user
	PurchaseClicked purchaseClicked;
	//The helper Object
	private IabHelper iabHelper;
	// (arbitrary) request code for the purchase flow
	public static final int RC_REQUEST = 10001;

                                       

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create Preference object

		//FBAccessToken="";
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		duration=GPSSharedPreference.getPurchaseDurationSharePreference();
		purchaseStatus=String.valueOf(SessionManager.getPurchaseSharePreference());
		Log.i("timeIntravel.......",purchaseStatus);
		//pref = MainFragmentMenu.this.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		//FBAccessToken=pref.getString(AppConstants.Access_Token_PREF, null);
		//setUser type from sharePreference 
		setUserType(getSharPrefUserType());
		iabHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		iabHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if(result.isFailure()){
					return;
				}
				iabHelper.queryInventoryAsync(queryInventoryFinishedListener);
			}
		});
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_page, null);
		userName = (TextView)view.findViewById(R.id.username_settings_page);
		updateSpinner = (Spinner) view.findViewById(R.id.update_spinner);
		deactivate = (TextView)view.findViewById(R.id.deactivate_txt);
		update = (TextView)view.findViewById(R.id.update_txt);
		rating = (TextView)view.findViewById(R.id.rating_txt);
		backUpdateToggle = (MySwitch)view.findViewById(R.id.backgound_update_toggle);
		publicPrivacy=(MySwitch)view.findViewById(R.id.profile_public_toggle);
		backUpdateToggle.setOnCheckedChangeListener(SettingsPage.this);
		updateSpinner.setOnItemSelectedListener(this);
		deactivate.setOnClickListener(SettingsPage.this);
		update.setOnClickListener(SettingsPage.this);
		rating.setOnClickListener(SettingsPage.this);
		backUpdateToggle.setOnClickListener(SettingsPage.this);
		publicPrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					editor = pref.edit();
					editor.putBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
					editor.commit();
				}else{
					showPublicAlert(AppConstants.ALERT_MSG_PROFILE_PUBLIC_OFF,AppConstants.ALERT_TITLE,7);
				}
			}
		});
		userName.setSelected(true);
		createUpdateSpinner();
		return view;
	}

	@Override
	public void onResume() {    
		super.onResume();
		//Set Remove Ads Text view Visibility
		editor = pref.edit();
		String username = pref.getString(AppConstants.USER_NAME_PREF, null);
		String upperString=" --- ";
		Utils.printLog("upperString", ""+username);
		if(username!=null && !username.equals("") ){
			upperString = username.substring(0,1).toUpperCase() + username.substring(1);
		}
		userName.setText(upperString);
		// Background updates
		boolean isToggleCheck = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
		if (isToggleCheck) {
			backUpdateToggle.setChecked(true);
			editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
			editor.commit();
		} else {
			backUpdateToggle.setChecked(false);
			editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, false);
			editor.commit();
		}
		long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
		// Profile public Privacy
		boolean isProfileCheck = pref.getBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
		if (isProfileCheck) {
			publicPrivacy.setChecked(true);
			editor.putBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
			editor.commit();

		} else {
			publicPrivacy.setChecked(false);
			editor.putBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, false);
			editor.commit();

		}
		// Set Spinner

		setSpinnerUpdateTime(updateTime);
		firstSelect = false;
		editor.putLong(AppConstants.FREQ_UPDATE_PREF, updateTime);
		editor.commit();
		/*Long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, 10);
		if(updateTime ==0){
			editor.putLong(AppConstants.FREQ_UPDATE_PREF, 10);
			editor.commit();
		}else{
			for(int i=0;i<updateDurationText.length;i++){
				if(updateDurationValue[i]==updateTime){
					updateSpinner.setSelection(i);
					break;
				}
			}
		}*/
	}
	/**
	 * Set Spinner Timing
	 * 
	 * @param updateTime
	 */
	private void setSpinnerUpdateTime(long updateTime){
		if(updateTime ==0){
			updateTime = AppConstants.DEFAULT_TIME_INTERVAL;
		}
		for(int i=0;i<updateDurationText.length;i++){
			if(updateDurationValue[i]==updateTime){
				updateSpinner.setSelection(i);
				break;
			}
		}
	}
	@Override
	public void onClick(View v) {
		if(isInternetOn()){
			if(v.getId() == R.id.deactivate_txt){
				showAlert(AppConstants.ALERT_MSG_DEACTIVATE_ACCOUNT, AppConstants.ALERT_TITLE_DEACTIVATE_ACCOUNT,0);
			}
			if(v.getId() == R.id.update_txt){
				new GpsAsyncGet(getActivity(), AppConstants.APP_UPDATE_URL, AppConstants.APP_UPDATE_RESP, this).execute();
			}
			if(v.getId() == R.id.rating_txt){
				final Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.gpstracker.pro&reviewId=0"));
				startActivity(intent);
			}			
			/*if(v == removeAdsTxt){
				particalPurchase();
				//Ads clicked by user
				purchaseClicked = PurchaseClicked.ADS;
			}*/
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	/**
	 * Create spinner for update duration
	 */
	private void createUpdateSpinner(){
		//ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(ctn, android.R.layout.simple_spinner_item, state);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.settings_spinner_view,updateDurationText);
		//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice);
		/*for(int i =0;i<updateDurationText.length;i++){
			arrayAdapter.add(updateDurationText[i]+" Min");
		}*/
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		updateSpinner.setAdapter(arrayAdapter);
	}

	private boolean firstSelect = false;
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long arg3) {

		selectposi=pos;

		if(!firstSelect) {
			firstSelect = true;
		} else {
			// boolean for start alarm service
			boolean startService = false;

			if ((updateDurationValue[selectposi] != updateDurationValue[0])&& (updateDurationValue[pos] != updateDurationValue[1])&& (updateDurationValue[pos] != updateDurationValue[2])) {// Other then 1 ,2, 3 minute
				startService = true;
			} 
			else if ((updateDurationValue[selectposi] == updateDurationValue[0]) && duration.equalsIgnoreCase("OneMonth") && duration.equalsIgnoreCase("ThreeMonth") && duration.equalsIgnoreCase("SixMonth") && duration.equalsIgnoreCase("OnYear")) {// Full access user
				startService = true;
			}else if ((updateDurationValue[selectposi] == updateDurationValue[1])&& duration.equalsIgnoreCase("OneMonth")&& duration.equalsIgnoreCase("ThreeMonth")&& duration.equalsIgnoreCase("SixMonth")&& duration.equalsIgnoreCase("OnYear")) { // Half access user
				startService = true;
			}else if ((updateDurationValue[selectposi] == updateDurationValue[2]) && duration.equalsIgnoreCase("OneMonth") && duration.equalsIgnoreCase("ThreeMonth") && duration.equalsIgnoreCase("SixMonth") && duration.equalsIgnoreCase("OnYear")) { // Half access user
				startService = true;
			}else {
				startService = false;
			}
			if (startService) {
				//Start alarm service
				startAlarmService(selectposi);
			} else {
				// Show purchase alert
				showFrequencyPurchaseDialog(updateDurationValue[selectposi]);
			}
		}

	}
	/**
	 * Start the alarm service 
	 * @param pos
	 */
	private void startAlarmService(int pos){
		editor.putLong(AppConstants.FREQ_UPDATE_PREF, updateDurationValue[pos]);
		editor.commit();
		int updateTimeint = updateDurationValue[pos];
		AlarmManager alarm = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		Intent intent2 = new Intent(getActivity(), BackgroundService.class);
		PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent2, 0);
		if(PendingIntent.getService(getActivity(), 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
			alarm.cancel(pintent);
		}
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), ( updateTimeint* 1000 * 60), pintent);
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	/**
	 *  Background updates show ON or OFF
	 */
	private void showAlert(String Message,String Title, final int alertCode){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		alertTitle.setText(Title);
		alertMsg.setText(Message);
		alertEditTxt.setVisibility(View.GONE);
		if(alertCode == 0 || alertCode == UPDATE_INT || alertCode == 7){
			cancelBtn.setVisibility(View.VISIBLE);
		} else {
			cancelBtn.setVisibility(View.GONE);
		}
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(alertCode == 7){
					backUpdateToggle.setChecked(true);
					editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
					editor.commit();
				}
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unused")
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(alertCode ==3||alertCode == 4) {

					session.logoutUser(getActivity());
					Intent i = new Intent(getActivity(),Login.class);
					startActivity(i);
					getActivity().finish();					
				} else if(alertCode ==0){
					deactivateAcc();
				} else if (alertCode == UPDATE_INT) {
					getActivity().finish();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=com.gpstracker.pro"));
					startActivity(intent);
				} else if(alertCode == 7) {
					backUpdateToggle.setChecked(false);
					editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, false);
					editor.commit();
					AlarmManager alarm = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
					Calendar cal = Calendar.getInstance();
					Intent intent2 = new Intent(getActivity(), BackgroundService.class);
					PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent2, 0);
					if(PendingIntent.getService(getActivity(), 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
						alarm.cancel(pintent);
					}
				}
			}
		});
		dialog.show();
	}
	/**
	 *  Profile is Public ON or OFF 
	 */
	private void showPublicAlert(String Message,String Title, final int alertCode){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		alertTitle.setText(Title);
		alertMsg.setText(Message);
		alertEditTxt.setVisibility(View.GONE);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(alertCode == 7){
					publicPrivacy.setChecked(true);
					editor.putBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, true);
					editor.commit();

				}
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(alertCode == 7) {
					publicPrivacy.setChecked(false);
					editor.putBoolean(AppConstants.IS_ENABLED_PROFILE_PRIVACY, false);
					editor.commit();

				}
			}
		});
		dialog.show();
	}
	@SuppressWarnings({ "unchecked"})
	public void deactivateAcc() {
		if(isInternetOn()){
			String userid = pref.getString(AppConstants.USER_ID_PREF, null);
			String url = AppConstants.DEACTIVATE_URL;
			if (userid != null) {
				BasicNameValuePair trackUserValue = new BasicNameValuePair(AppConstants.AUTH_KEY,userid);
				List<NameValuePair> detailPageList = new ArrayList<NameValuePair>();
				detailPageList.add(trackUserValue);
				new GpsAsyncTask(getActivity(), url, AppConstants.DEACTIVATE_ACCOUNT_RESP, this).execute(detailPageList);
			}
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		String statusCode,statusResp,msgResp;
		if (serverResp!= null) {
			switch (RespValue) {
			case AppConstants.APP_UPDATE_RESP:
				try {
					JSONObject jObjServerResp;
					jObjServerResp = new JSONObject(serverResp);
					statusCode = jObjServerResp.getString(AppConstants.STATUS_CODE);
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){
						if(jObjServerResp.has(AppConstants.ANDROID_VERSION)){
							String NewVersion = "";
							String versionName ="";
							NewVersion = jObjServerResp.getString(AppConstants.ANDROID_VERSION);
							try {
								versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
								if(!versionName.equalsIgnoreCase(NewVersion)){

									/*	System.out.println("inside if Android curr version......");
									String[] currVersion = versionName.split("\\.");
									String[] newVersion = NewVersion.trim().split("\\.");*/
									showAlert(AppConstants.ALERT_MSG_APP_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, UPDATE_INT);

									/*	if (Integer.parseInt(currVersion[0]) < Integer.parseInt(newVersion[0])) {
										showAlert(AppConstants.ALERT_MSG_APP_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, UPDATE_INT);
									} else if (Integer.parseInt(currVersion[1]) < Integer.parseInt(newVersion[1])) {
										showAlert(AppConstants.ALERT_MSG_APP_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, UPDATE_INT);
									} else if (Integer.parseInt(currVersion[2]) < Integer.parseInt(newVersion[2])) {
										showAlert(AppConstants.ALERT_MSG_APP_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, UPDATE_INT);
									} else {
										showAlert(AppConstants.ALERT_MSG_NO_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, NO_UPDATE_INT);										
									}*/
								} else {
									showAlert(AppConstants.ALERT_MSG_NO_UPDATE, AppConstants.ALERT_TITLE_APP_UPDATE, NO_UPDATE_INT);
								}
							} catch (android.content.pm.PackageManager.NameNotFoundException e) {
								versionName = "";
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AppConstants.DEACTIVATE_ACCOUNT_RESP:
				JSONObject jObjServerResp;
				try {
					jObjServerResp = new JSONObject(serverResp);
					statusCode = jObjServerResp.getString(AppConstants.STATUS_CODE);
					statusResp = jObjServerResp.getString(AppConstants.STATUS);
					msgResp = jObjServerResp.getString(AppConstants.MESSAGE);
					session = new SessionManager(getActivity());
					switch (statusCode) {
					case AppConstants.NEW_SUCCESS:
						showAlert("Account Deactivated Successfully",AppConstants.ALERT_TITLE, 4);
						break;
					case AppConstants.DEACTIVATION_FAILED:
						showAlert("Deactivation Failed",AppConstants.ALERT_TITLE, 2);
						break;
					case AppConstants.NEW_FAILED:
						showAlert("You have been Logged in Other Device",AppConstants.ALERT_TITLE, 3);
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AppConstants.IN_APP_PURCHASE_RESP:
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(serverResp);
					statusCode = jsonObject.getString(AppConstants.STATUS_CODE);
					msgResp = jsonObject.getString(AppConstants.MESSAGE);
					Utils.showToast(msgResp);					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			//backUpdateToggle.setBackgroundResource(R.drawable.on_tog_btn);
			editor = pref.edit();
			editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
			editor.commit();
		}else{
			showAlert(AppConstants.ALERT_MSG_BACKGROUND_OFF,AppConstants.ALERT_TITLE,7);
			//backUpdateToggle.setBackgroundResource(R.drawable.off_tog_btn);
			/*editor.putBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, false);
			editor.commit();*/
		}
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
	/**
	 * Get user type from SharedPreference
	 * 
	 * @return return user type
	 */
	private int getSharPrefUserType(){
		return SessionManager.getPurchaseSharePreference();
		//return pref.getInt(AppConstants.USER_TYPE_PREF, PurchaseStatus.NORMAL_USER.getStatus());
	}
	/**
	 * Set user type in shared preference
	 * 
	 * @param status Purchase status 
	 */
	private void setSharPrefUserType(PurchaseStatus status){		
		/*editor = pref.edit();
		editor.putInt(AppConstants.USER_TYPE_PREF, status.getStatus());
		editor.commit();*/
		SessionManager.setPurchaseSharePreference(status.getStatus());
	}
	/**
	 * Show frequency dialog
	 */
	private void showFrequencyPurchaseDialog(final int value) {
		final Dialog dialog = new Dialog(getActivity(),
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
		final RadioGroup   radioGroup = (RadioGroup)dialog.findViewById(R.id.myRadioGroup);

		final RadioButton   radioOneMonth = (RadioButton)dialog.findViewById(R.id.oneMonth);
		final RadioButton   radioThreeMonth = (RadioButton)dialog.findViewById(R.id.threeMonth);
		final RadioButton   radioSixMonth = (RadioButton)dialog.findViewById(R.id.sixMonth);
		final RadioButton   radioOneYear = (RadioButton)dialog.findViewById(R.id.oneYear);

		//long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
		radioGroup.setVisibility(View.VISIBLE);
		alertTitle.setText("Purchase Product");
		if (value == updateDurationValue[0]) {
			alertMsg.setText("Buy Update Frequency for 1 Minutes");
			//radioOneMonth.setChecked(true);
			if(userType == PurchaseStatus.FULL_ACCESS_USER){
				if(duration.equalsIgnoreCase("OneMonth")){
					radioOneMonth.setChecked(true);
				}else{
					radioOneMonth.setChecked(false);;
				}
				if(duration.equalsIgnoreCase("ThreeMonth")){
					radioThreeMonth.setChecked(true);
				}else{
					radioThreeMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("SixMonth")){
					radioSixMonth.setChecked(true);
				}else{
					radioSixMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("OneYear")){
					radioOneYear.setChecked(true);
				}else{
					radioOneYear.setChecked(false);
				}
			}
		} else if (value == updateDurationValue[1]) {
			alertMsg.setText("Buy Update Frequency for 2 Minutes");
			if(userType == PurchaseStatus.SEMI_FULL_ACCESS_USER){
				if(duration.equalsIgnoreCase("OneMonth")){
					radioOneMonth.setChecked(true);
				}else{
					radioOneMonth.setChecked(false);;
				}
				if(duration.equalsIgnoreCase("ThreeMonth")){
					radioThreeMonth.setChecked(true);
				}else{
					radioThreeMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("SixMonth")){
					radioSixMonth.setChecked(true);
				}else{
					radioSixMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("OneYear")){
					radioOneYear.setChecked(true);
				}else{
					radioOneYear.setChecked(false);
				}
			}
		} else if (value == updateDurationValue[2]) {
			alertMsg.setText("Buy Update Frequency for 3 Minutes");

			if(userType == PurchaseStatus.PARTIAL_ACCESS_USER){
				if(duration.equalsIgnoreCase("OneMonth")){
					radioOneMonth.setChecked(true);
				}else{
					radioOneMonth.setChecked(false);;
				}
				if(duration.equalsIgnoreCase("ThreeMonth")){
					radioThreeMonth.setChecked(true);
				}else{
					radioThreeMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("SixMonth")){
					radioSixMonth.setChecked(true);
				}else{
					radioSixMonth.setChecked(false);
				}
				if(duration.equalsIgnoreCase("OneYear")){
					radioOneYear.setChecked(true);
				}else{
					radioOneYear.setChecked(false);
				}
			}
		}
		alertEditTxt.setVisibility(View.GONE);

		radioOneMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (value == updateDurationValue[0]) {
					purchaseClicked = PurchaseClicked.ONE_MIN_ONE_MONTH;
				}else if (value == updateDurationValue[1]) {
					purchaseClicked = PurchaseClicked.TWO_MIN_ONE_MONTH;
				}else if (value == updateDurationValue[2]) {
					purchaseClicked = PurchaseClicked.THREE_MIN_ONE_MONTH;
				}
			}
		});
		radioThreeMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (value == updateDurationValue[0]) {
					purchaseClicked = PurchaseClicked.ONE_MIN_THREE_MONTH;
				}else if (value == updateDurationValue[1]) {
					purchaseClicked = PurchaseClicked.TWO_MIN_THREE_MONTH;
				}else if (value == updateDurationValue[2]) {
					purchaseClicked = PurchaseClicked.THREE_MIN_THREE_MONTH;
				}
			}
		});
		radioSixMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (value == updateDurationValue[0]) {
					purchaseClicked = PurchaseClicked.ONE_MIN_SIX_MONTH;
				} else if (value == updateDurationValue[1]) {
					purchaseClicked = PurchaseClicked.TWO_MIN_SIX_MONTH;
				}else if (value == updateDurationValue[2]) {
					purchaseClicked = PurchaseClicked.THREE_MIN_SIX_MONTH;
				}
			}
		});
		radioOneYear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (value == updateDurationValue[0]) {
					purchaseClicked = PurchaseClicked.ONE_MIN_ONE_YEAR;
				} else if (value == updateDurationValue[1]) {
					purchaseClicked = PurchaseClicked.TWO_MIN_ONE_YEAR;
				}else if (value == updateDurationValue[2]) {
					purchaseClicked = PurchaseClicked.THREE_MIN_ONE_YEAR;
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);

				// Set Spinner
				setSpinnerUpdateTime(updateTime);
				firstSelect = false;
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(radioOneMonth.isChecked() || radioThreeMonth.isChecked() || radioSixMonth.isChecked() || radioOneYear.isChecked()){
					// One min update
					if(value == updateDurationValue[0] && radioOneMonth.isChecked()){
						fullPurchaseOneMonth();
						purchaseClicked = PurchaseClicked.ONE_MIN_ONE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[0] && radioThreeMonth.isChecked()){
						fullPurchaseThreeMonth();
						purchaseClicked = PurchaseClicked.ONE_MIN_THREE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[0] && radioSixMonth.isChecked()){
						fullPurchaseSixMonth();
						purchaseClicked = PurchaseClicked.ONE_MIN_SIX_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[0] && radioOneYear.isChecked()){
						fullPurchaseOneYear();
						purchaseClicked = PurchaseClicked.ONE_MIN_ONE_YEAR;
						//startUpdatePurchaseStatus();
					}
					// Two min update
					else if(value == updateDurationValue[1] && radioOneMonth.isChecked()){
						semiparticalPurchaseOneMonth();
						purchaseClicked = PurchaseClicked.TWO_MIN_ONE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[1] && radioThreeMonth.isChecked()){
						semiparticalPurchaseThreeMonth();
						purchaseClicked = PurchaseClicked.TWO_MIN_THREE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[1] && radioSixMonth.isChecked()){
						semiparticalPurchaseSixMonth();
						purchaseClicked = PurchaseClicked.TWO_MIN_SIX_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[1] && radioOneYear.isChecked()){
						semiparticalPurchaseOneYear();
						purchaseClicked = PurchaseClicked.TWO_MIN_ONE_YEAR;
						//startUpdatePurchaseStatus();
					}
					// Three min update
					else if(value == updateDurationValue[2] && radioOneMonth.isChecked()){
						particalPurchaseOneMonth();
						purchaseClicked = PurchaseClicked.THREE_MIN_ONE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[2] && radioThreeMonth.isChecked()){
						particalPurchaseThreeMonth();
						purchaseClicked = PurchaseClicked.THREE_MIN_THREE_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[2] && radioSixMonth.isChecked()){
						particalPurchaseSixMonth();
						purchaseClicked = PurchaseClicked.THREE_MIN_SIX_MONTH;
						//startUpdatePurchaseStatus();
					}else if(value == updateDurationValue[2] && radioOneYear.isChecked()){
						particalPurchaseOneYear();
						purchaseClicked = PurchaseClicked.THREE_MIN_ONE_YEAR;
						//startUpdatePurchaseStatus();
					}

					long updateTime = pref.getLong(AppConstants.FREQ_UPDATE_PREF, AppConstants.DEFAULT_TIME_INTERVAL);
					// Set Spinner
					setSpinnerUpdateTime(updateTime);
					firstSelect = false;
					dialog.dismiss();
				}else{
					dialog.show();
					Utils.showToast("Select durations");
					//Toast.makeText(getActivity(), "Select durations", Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.show();
	}
	/**
	 * Match the int value with enum and set userType;
	 * 
	 * @param state 
	 */
	private PurchaseStatus setUserType(int state) {
		for (PurchaseStatus purchaseStatus : PurchaseStatus.values()) {
			PurchaseStatus status = purchaseStatus;
			if (status.getStatus() == state) {
				userType = status;
				break;
			}
		}
		return userType;
	}
	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if(iabHelper==null){
				return;
			}
			if(result.isFailure()){
				return;
			}
			Purchase adsPurchase = inventory.getPurchase(SKU_ADS_THREE_MIN_PRO_ID);
			if((adsPurchase!=null)&&(verifyDeveloperPayload(adsPurchase))){
				iabHelper.consumeAsync(adsPurchase,
						onConsumeFinishedListener);
				return;
			}
			Purchase adsUpdateFrq = inventory.getPurchase(SKU_ONE_MIN_UPT_FRQ_PRO_ID);
			if((adsUpdateFrq!=null)&&(verifyDeveloperPayload(adsUpdateFrq))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}	

			//One mins update
			Purchase updateOneMinOneMonth = inventory.getPurchase(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH);
			if((updateOneMinOneMonth!=null)&&(verifyDeveloperPayload(updateOneMinOneMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateOneMinThreeMonth = inventory.getPurchase(SKU_ONE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH);
			if((updateOneMinThreeMonth!=null)&&(verifyDeveloperPayload(updateOneMinThreeMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateOneMinSixMonth = inventory.getPurchase(SKU_ONE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH);
			if((updateOneMinSixMonth!=null)&&(verifyDeveloperPayload(updateOneMinSixMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateOneMinOneYear = inventory.getPurchase(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR);
			if((updateOneMinOneYear!=null)&&(verifyDeveloperPayload(updateOneMinOneYear))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			// Two mins update
			Purchase updateTwoMinOneMonth = inventory.getPurchase(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_MONTH);
			if((updateTwoMinOneMonth!=null)&&(verifyDeveloperPayload(updateTwoMinOneMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateTwoMinThreeMonth = inventory.getPurchase(SKU_TWO_MIN_UPT_FRQ_PRO_ID_THREE_MONTH);
			if((updateTwoMinThreeMonth!=null)&&(verifyDeveloperPayload(updateTwoMinThreeMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateTwoMinSixMonth = inventory.getPurchase(SKU_TWO_MIN_UPT_FRQ_PRO_ID_SIX_MONTH);
			if((updateTwoMinSixMonth!=null)&&(verifyDeveloperPayload(updateTwoMinSixMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateTwoMinOneYear = inventory.getPurchase(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_YEAR);
			if((updateTwoMinOneYear!=null)&&(verifyDeveloperPayload(updateTwoMinOneYear))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			// Three mins update
			Purchase updateThreeMinOneMonth = inventory.getPurchase(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH);
			if((updateThreeMinOneMonth!=null)&&(verifyDeveloperPayload(updateThreeMinOneMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateThreeMinThreeMonth = inventory.getPurchase(SKU_THREE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH);
			if((updateThreeMinThreeMonth!=null)&&(verifyDeveloperPayload(updateThreeMinThreeMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateThreeMinSixMonth = inventory.getPurchase(SKU_THREE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH);
			if((updateThreeMinSixMonth!=null)&&(verifyDeveloperPayload(updateThreeMinSixMonth))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
			Purchase updateThreeMinOneYear = inventory.getPurchase(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR);
			if((updateThreeMinOneYear!=null)&&(verifyDeveloperPayload(updateThreeMinOneYear))){
				iabHelper.consumeAsync(adsUpdateFrq, onConsumeFinishedListener);
				return;
			}
		}
	};
	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener onConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (iabHelper == null) {
				return;
			}
			if (result.isSuccess()) {
				// Call service
				if (purchase.getSku().equals(SKU_ADS_THREE_MIN_PRO_ID)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				} else if (purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				// One min update
				else if (purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				// Two min update 
				else if (purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_THREE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_SIX_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_YEAR)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				// Three min update
				else if (purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}
				else if (purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR)) {
					Utils.showToast("You have purchased for enable Update frequency from your app");
					//Call Web Service
					startUpdatePurchaseStatus();
				}


			} else {
			}
		}
	};
	/**
	 * Update purchase status in server
	 */
	@SuppressWarnings("unchecked")
	private void startUpdatePurchaseStatus(){
		int status=0;
		String duration="";
		/*if(purchaseClicked == PurchaseClicked.ONE_MIN){ //Start alarm and get status
			status = PurchaseStatus.FULL_ACCESS_USER.getStatus();
			startAlarmService(0);
			// Set Spinner
			setSpinnerUpdateTime(updateDurationValue[0]);
		}	

		else if(purchaseClicked == PurchaseClicked.TWO_MIN){//Start alarm and get status
		status = PurchaseStatus.SEMI_FULL_ACCESS_USER.getStatus();
		startAlarmService(1);
		// Set Spinner
		setSpinnerUpdateTime(updateDurationValue[1]);
		}

		 else if(purchaseClicked == PurchaseClicked.THREE_MIN){//Start alarm and get status
			 status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();
			 startAlarmService(1);
			 // Set Spinner
			 setSpinnerUpdateTime(updateDurationValue[2]);
		 }

		 else{// status
			 status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();			
		 }
		 */
		// One min update
		if(purchaseClicked == PurchaseClicked.ONE_MIN_ONE_MONTH){ //Start alarm and get status
			status = PurchaseStatus.FULL_ACCESS_USER.getStatus();
			duration="OneMonth";
			startAlarmService(0);
			// Set Spinner
			setSpinnerUpdateTime(updateDurationValue[0]);
			firstSelect = false;
		} else if(purchaseClicked == PurchaseClicked.ONE_MIN_THREE_MONTH){ //Start alarm and get status
			status = PurchaseStatus.FULL_ACCESS_USER.getStatus();
			duration="ThreeMonth";
			startAlarmService(0);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[0]);
			firstSelect = false;
		} else if(purchaseClicked == PurchaseClicked.ONE_MIN_SIX_MONTH){ //Start alarm and get status
			status = PurchaseStatus.FULL_ACCESS_USER.getStatus();
			duration="SixMonth";
			startAlarmService(0);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[0]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.ONE_MIN_ONE_YEAR){ //Start alarm and get status
			status = PurchaseStatus.FULL_ACCESS_USER.getStatus();
			duration="OneYear";
			startAlarmService(0);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[0]);
			firstSelect = false;
		}
		// Two min update
		else if(purchaseClicked == PurchaseClicked.TWO_MIN_ONE_MONTH){ //Start alarm and get status
			status = PurchaseStatus.SEMI_FULL_ACCESS_USER.getStatus();
			duration="OneMonth";
			startAlarmService(1);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[1]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.TWO_MIN_THREE_MONTH){ //Start alarm and get status
			status = PurchaseStatus.SEMI_FULL_ACCESS_USER.getStatus();
			duration="ThreeMonth";
			startAlarmService(1);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[1]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.TWO_MIN_SIX_MONTH){ //Start alarm and get status
			status = PurchaseStatus.SEMI_FULL_ACCESS_USER.getStatus();
			duration="SixMonth";
			startAlarmService(1);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[1]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.TWO_MIN_ONE_YEAR){ //Start alarm and get status
			status = PurchaseStatus.SEMI_FULL_ACCESS_USER.getStatus();
			duration="OneYear";
			startAlarmService(1);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[1]);
			firstSelect = false;
		}
		// Three min update
		else if(purchaseClicked == PurchaseClicked.THREE_MIN_ONE_MONTH){//Start alarm and get status
			status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();
			duration="OneMonth";
			startAlarmService(2);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[2]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.THREE_MIN_THREE_MONTH){//Start alarm and get status
			status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();
			duration="ThreeMonth";
			startAlarmService(2);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[2]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.THREE_MIN_SIX_MONTH){//Start alarm and get status
			status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();
			duration="SixMonth";
			startAlarmService(2);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[2]);
			firstSelect = false;
		}else if(purchaseClicked == PurchaseClicked.THREE_MIN_ONE_YEAR){//Start alarm and get status
			status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();
			duration="OneYear";
			startAlarmService(2);
			// Set Spinner

			setSpinnerUpdateTime(updateDurationValue[2]);
			firstSelect = false;
		} else{// status
			status = PurchaseStatus.PARTIAL_ACCESS_USER.getStatus();			
		}

		//Set share preference user type
		setSharPrefUserType(setUserType(status));
		//check for remove Textview Visibility

		// Set AuthKey and Status
		String userId = pref.getString("Userid", null);
		BasicNameValuePair userAuthKey = new BasicNameValuePair(AppConstants.AUTH_KEY,userId);
		BasicNameValuePair userPurchaseStatus = new BasicNameValuePair(AppConstants.STATUS,""+status);
		BasicNameValuePair userPurchaseDuration = new BasicNameValuePair(AppConstants.STATUS_DURATION,""+duration);

		//user version is modified------------------------------------------------------------ 
		BasicNameValuePair userversoin = new BasicNameValuePair(AppConstants.VERSION,""+AppConstants.PRO);



		List<NameValuePair> updatePurchaseStatusValues = new ArrayList<NameValuePair>();
		updatePurchaseStatusValues.add(userAuthKey);
		updatePurchaseStatusValues.add(userPurchaseStatus);
		updatePurchaseStatusValues.add(userPurchaseDuration);
		updatePurchaseStatusValues.add(userversoin);

		System.out.println("purchase........"+updatePurchaseStatusValues);
		//Start Web service
		new GpsAsyncTask(getActivity(), AppConstants.IN_APP_STATUS_UPT_URL, AppConstants.IN_APP_PURCHASE_RESP, SettingsPage.this).execute(updatePurchaseStatusValues);
	}
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener iabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			// if we were disposed of in the meantime, quit.
			if (iabHelper == null){
				return;
			}
			if (result.isFailure()) {
				return;
			}
			if(purchase.getSku().equals(SKU_ADS_THREE_MIN_PRO_ID) || purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID)){
				iabHelper.consumeAsync(purchase, onConsumeFinishedListener);
			}
			if(purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH) ||purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH) || purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH) || purchase.getSku().equals(SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR) || purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_MONTH)|| purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_THREE_MONTH)
					|| purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_SIX_MONTH) || purchase.getSku().equals(SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_YEAR) || purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH)|| purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH) || purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH) || purchase.getSku().equals(SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR)){
				iabHelper.consumeAsync(purchase, onConsumeFinishedListener);
			}

		}
	};
	/** Verifies the developer payload of a purchase. */
	@SuppressWarnings("unused")
	private boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		//Set Server code
		return true;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		} else {
		}
	}
	/**
	 *  Check user type and Open the in app purchase for Ads and Three Minute Frequency Update 
	 *  
	 */
	/*private void particalPurchase() {
		if (userType == PurchaseStatus.NORMAL_USER) {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_ADS_THREE_MIN_PRO_ID,
					RC_REQUEST, iabPurchaseFinishedListener, payload);
		} else {
			Utils.showToast("Already Purchased");
		}
	}*/
	/**
	 * Check user type and Open the in app purchase for Full purchase
	 * 
	 */
	/*private void fullPurchase() {
		if (!(userType == PurchaseStatus.FULL_ACCESS_USER)) {
			String payload = "";
			iabHelper.launchPurchaseFlow(getActivity(), SKU_ONE_MIN_UPT_FRQ_PRO_ID,
					RC_REQUEST, iabPurchaseFinishedListener, payload);
		} else {
			Utils.showToast("Already Purchased");
		}
	}
	 */

	// One min update frequency purchase
	private void fullPurchaseOneMonth() {

		if (purchaseStatus.equalsIgnoreCase("3") && duration.equalsIgnoreCase("OneMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(getActivity(), SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void fullPurchaseThreeMonth() {
		if (purchaseStatus.equalsIgnoreCase("3") && duration.equalsIgnoreCase("ThreeMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(getActivity(), SKU_ONE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void fullPurchaseSixMonth() {
		if (purchaseStatus.equalsIgnoreCase("3") && duration.equalsIgnoreCase("SixMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(getActivity(), SKU_ONE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void fullPurchaseOneYear() {
		if (purchaseStatus.equalsIgnoreCase("3") && duration.equalsIgnoreCase("OneYear")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(getActivity(), SKU_ONE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}

	//Two min update frequency purchase

	private void semiparticalPurchaseOneMonth() {
		if (purchaseStatus.equalsIgnoreCase("2") && duration.equalsIgnoreCase("OneMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);


		}
	}
	private void semiparticalPurchaseThreeMonth() {
		if (purchaseStatus.equalsIgnoreCase("2") && duration.equalsIgnoreCase("ThreeMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_TWO_MIN_UPT_FRQ_PRO_ID_THREE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void semiparticalPurchaseSixMonth() {
		if (purchaseStatus.equalsIgnoreCase("2") && duration.equalsIgnoreCase("SixMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_TWO_MIN_UPT_FRQ_PRO_ID_SIX_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void semiparticalPurchaseOneYear() {
		if (purchaseStatus.equalsIgnoreCase("2") && duration.equalsIgnoreCase("OneYear")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_TWO_MIN_UPT_FRQ_PRO_ID_ONE_YEAR,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	//Three min update frequency purchase
	private void particalPurchaseOneMonth() {
		if (purchaseStatus.equalsIgnoreCase("1") && duration.equalsIgnoreCase("OneMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {

			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);
		}
	}
	private void particalPurchaseThreeMonth() {
		if (purchaseStatus.equalsIgnoreCase("1") && duration.equalsIgnoreCase("ThreeMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_THREE_MIN_UPT_FRQ_PRO_ID_THREE_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void particalPurchaseSixMonth() {
		if (purchaseStatus.equalsIgnoreCase("1") && duration.equalsIgnoreCase("SixMonth")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {
			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_THREE_MIN_UPT_FRQ_PRO_ID_SIX_MONTH,
					RC_REQUEST, iabPurchaseFinishedListener, payload);

		}
	}
	private void particalPurchaseOneYear() {
		if (purchaseStatus.equalsIgnoreCase("1") && duration.equalsIgnoreCase("OneYear")) {
			Utils.showToast("Already Purchased");
			startAlarmService(selectposi);
		} else {

			String payload = "";
			iabHelper.launchPurchaseFlow(this.getActivity(), SKU_THREE_MIN_UPT_FRQ_PRO_ID_ONE_YEAR,
					RC_REQUEST, iabPurchaseFinishedListener, payload);
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (iabHelper != null) {
			iabHelper.dispose();
			iabHelper = null;
		}
	}
}
