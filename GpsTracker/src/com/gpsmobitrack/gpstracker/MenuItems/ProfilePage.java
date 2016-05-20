package com.gpsmobitrack.gpstracker.MenuItems;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Base64;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import eu.janmuller.android.simplecropimage.CropImage;

public class ProfilePage extends Fragment implements OnClickListener, AsyncResponse {

	EditText firstNameTxt,lastNameTxt,phoneTxt,stateTxt,countryTxt;//dobYearTxt,dobMonthTxt,dobDateTxt,
	RadioGroup genderRadioGroup;
	RadioButton selectedGenderBtn,maleBtn,femaleBtn;
	Button saveBtn,imgUploadBtn;
	ImageView imgViewUpload;
	ImageButton dobImgBtn;
	TextView changePassTxt,imgUploadTxt,dobTxt,emailStatusTxt,emailTxt;
	View view;
	String statusResp,msgResp,statusCode,serverRespImg;;
	SharedPreferences pref;
	String FBAccessToken;
	Editor editor;
	DisplayImageOptions options;
	Bitmap bitmapOrg;
	public ImageLoader1 imageLoader;
	public static String uploadFileName;
	public static Uri uploadFileNameUri;
	//public boolean isImgSelect = false;
	private static int GALLERY_RES_CODE=5;
	private static int CAMERA_RES_CODE=6;
	//private static int CROP_IMAGE_RESPONSE_CODE=100;
	public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
	File camProfileImage;
	DBHandler dbHandler;
	Calendar calendarDatePicker;

	private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			if (view.isShown()) {
				Calendar calendarTemp = Calendar.getInstance();
				calendarTemp.set(selectedYear, selectedMonth, selectedDay);
				Date todaysDate = new Date(System.currentTimeMillis());
				if (calendarTemp.getTime().compareTo(todaysDate) <= 0) {
					calendarDatePicker.set(selectedYear, selectedMonth,selectedDay);
					String dobString = "" + selectedYear + "-"+ new DecimalFormat("00").format(selectedMonth + 1)
							+ "-" + new DecimalFormat("00").format(selectedDay);
					dobTxt.setText(dobString);
				} else {
					Utils.showToast("Date should not be greater than today's date.");
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		dbHandler = new DBHandler(getActivity());
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.uploadimg_btn)
		.showImageForEmptyUri(R.drawable.uploadimg_btn)
		.showImageOnFail(R.drawable.uploadimg_btn)
		//	.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.profile_page, null);
		initComponents();
		imageLoader = new ImageLoader1(getActivity());
		if(isInternetOn()) {
			getProfileDetails();	
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
		return view;
	}
	public void onResume(){
		super.onResume();
	}
	private void createDialog(){
		if(calendarDatePicker == null){
			calendarDatePicker = Calendar.getInstance();
		}
		int year  = calendarDatePicker.get(Calendar.YEAR);
		int month = calendarDatePicker.get(Calendar.MONTH);
		int day   = calendarDatePicker.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog datePickerDialog= new DatePickerDialog(getActivity(), pickerListener, year, month,day);
		datePickerDialog.setCancelable(false);  
		datePickerDialog.show();
	}
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void onClick(View v) {
		try{
			if(v == imgViewUpload){
				if(isInternetOn()){
					showProfileImageOptionDialog();
				} else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}
			}
			if(v == dobImgBtn){
				createDialog();
			}
			if(v == changePassTxt){
				showChangePassAlert();
			}
			if(v == emailStatusTxt){
				boolean verified_Status = false;
				String verified_StatusString = pref.getString(AppConstants.USER_VERIFIED_STATUS_PREF, null);
				if(verified_StatusString != null & verified_StatusString.length()>0 ){

					if(verified_StatusString.equalsIgnoreCase("1")){

						verified_Status = true;
					} else {

						verified_Status = false;
					}
				} else {
					verified_Status =false;
				}
				if(!verified_Status){
					showAlert(AppConstants.ALERT_VERIFICATION_CONFIRM_MESSAGE, "Alert", true);
				}
			}
			if(v == saveBtn){
				if(isInternetOn()){
					if(profileValidation()){
						String byteStr = null;
						int selectedId = genderRadioGroup.getCheckedRadioButtonId();
						selectedGenderBtn = (RadioButton) view.findViewById(selectedId);
						Utils.printLog("Gender", "" + selectedGenderBtn.getText());
						String act = "updateProfile";
						String userId = pref.getString("Userid", null);
						String firstname = firstNameTxt.getText().toString().trim();
						String lastname = lastNameTxt.getText().toString().trim();
						String phoneno = phoneTxt.getText().toString().trim();
						String emailid = emailTxt.getText().toString().trim();
						String gender = selectedGenderBtn.getText().toString().trim();
						String dob = dobTxt.getText().toString(); 
						String state = stateTxt.getText().toString().trim();
						String country = countryTxt.getText().toString().trim();
						String url = AppConstants.PROFILE_UPDATE_URL;
						// String profileImg = uploadFileNameUri.toString();
						Utils.printLog("UP=",""+uploadFileName);
						/*if(uploadFileName != null && !uploadFileName.equals("")){
					Bitmap bm = BitmapFactory.decodeFile(uploadFileName);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bm.compress(CompressFormat.JPEG, 75, bos);
					byte[] byteData = bos.toByteArray();
					byteStr = Base64.encodeBytes(byteData);
				}*/
						BasicNameValuePair firstNameValue = new BasicNameValuePair("firstname",firstname);
						BasicNameValuePair lastNameValue = new BasicNameValuePair("lastname",lastname);
						BasicNameValuePair phonenoValue = new BasicNameValuePair("phone",phoneno);
						BasicNameValuePair genderValue = new BasicNameValuePair("gender",gender);
						BasicNameValuePair ageValue = new BasicNameValuePair("dob",dob);
						BasicNameValuePair stateValue = new BasicNameValuePair("state",state);
						BasicNameValuePair countryValue = new BasicNameValuePair("country",country);
						BasicNameValuePair userIdValue = new BasicNameValuePair("userId",userId);
						BasicNameValuePair userId2Value = new BasicNameValuePair(AppConstants.AUTH_KEY,userId);
						List<NameValuePair> updateProfileValues = new ArrayList<NameValuePair>();
						updateProfileValues.add(firstNameValue);
						updateProfileValues.add(lastNameValue);
						updateProfileValues.add(phonenoValue);
						updateProfileValues.add(genderValue);
						updateProfileValues.add(ageValue);
						updateProfileValues.add(stateValue);
						updateProfileValues.add(countryValue);
						updateProfileValues.add(userIdValue);
						updateProfileValues.add(userId2Value);
						new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_SAVE_RESP, this).execute(updateProfileValues);
					}
				} else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}
			}}catch(Exception ex){}
	}
	/**
	 * Validation for profile form.
	 * 
	 * @return true if entered values are correct otherwise false.
	 */
	private boolean profileValidation() {
		String firstname = firstNameTxt.getText().toString().trim();
		String lastname = lastNameTxt.getText().toString().trim();
		String phoneno = phoneTxt.getText().toString().trim();
		String emailid = emailTxt.getText().toString().trim();
		String dob = dobTxt.getText().toString();
		String state = stateTxt.getText().toString().trim();
		String country = countryTxt.getText().toString().trim();
		int selectedId = genderRadioGroup.getCheckedRadioButtonId();

		if (firstname.equals("")) {
			showToast("First name should not be empty");
			return false;
		}
		if (lastname.equals("")) {
			showToast("Last name should not be empty");
			return false;
		}
		if (phoneno.equals("")) {
			showToast("Phone Number should not be empty");
			return false;
		}else if(phoneno.length() <= 9){
			showToast("Enter valid Phone Number");
			return false;
		}
		if (emailid.equals("")) {
			showToast("Email id should not be empty");
			return false;
		}else if(!Utils.validEmail(emailid)){
			showToast("Enter valid Email-id");
			return false;
		}
		if (selectedId == -1) {
			showToast("Please Select gender");
			return false;
		}
		if(dob.equals("")){
			showToast("DOB should not be empty");
			return false;
		}
		if (state.equals("")) {
			showToast("State should not be empty");
			return false;
		}
		if(state.matches(".*\\d.*")){
			showToast("State should not contain numbers");
			return false;
		} 
		if (country.equals("")) {
			showToast("Country should not be empty");
			return false;
		}
		if(country.matches(".*\\d.*")){
			showToast("Country should not contain numbers");
			return false;
		} 
		return true;
	}
	/**
	 * Shows the toast
	 * 
	 * @param text
	 */
	private void showToast(String text) {
		Utils.showToast(text);
	}
	/**
	 * Show dialog box for profile image option
	 */
	private void showProfileImageOptionDialog(){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		dialog.setContentView(R.layout.profile_image_option_alert);
		Button camBtn = (Button) dialog.findViewById(R.id.profile_img_opt_cam_btn);
		Button galleryBtn = (Button) dialog.findViewById(R.id.profile_img_opt_gallery_btn);
		Button removeBtn = (Button) dialog.findViewById(R.id.profile_img_opt_remove_btn);
		Button cancelBtn  = (Button)dialog.findViewById(R.id.profile_img_opt_cancel_btn);
		if(uploadFileName!=null && !uploadFileName.equals("null")){
			Utils.printLog("Tag=", "true");
			removeBtn.setVisibility(View.VISIBLE);
		}
		removeBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				//uploadFileName=null;
				//imgViewUpload.setImageResource(R.drawable.uploadimg_btn);
				//isImgSelect = false;
				String url = AppConstants.PROFILE_DELETE_IMAGE_URL;
				String userId = pref.getString("Userid", null);
				BasicNameValuePair authKey = new BasicNameValuePair(AppConstants.AUTH_KEY,userId);
				List<NameValuePair> updateProfileImg = new ArrayList<NameValuePair>();
				updateProfileImg.add(authKey);
				if(isInternetOn()){
					new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_DELETE_RESP, ProfilePage.this).execute(updateProfileImg);
				} else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}
				dialog.dismiss();
			}
		});
		camBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH);
				Date date = new Date();
				String fileName = dateFormat.format(date);
				System.out.println(dateFormat.format(date)); 
				camProfileImage = new File(android.os.Environment
						.getExternalStorageDirectory(), fileName+".jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(camProfileImage)); 
				/*   Log.w("File Name1", ""+camProfileImage.getPath());
                Log.w("File Name2", ""+camProfileImage.getName());
                Log.w("File Name3", ""+camProfileImage.getAbsoluteFile());*/
				startActivityForResult(intent, CAMERA_RES_CODE);
				//Toast.makeText(getActivity(), "Cam", Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
		galleryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK);
				i.setType("image/*");
				startActivityForResult(i, GALLERY_RES_CODE);
				dialog.dismiss();
				//Toast.makeText(getActivity(), "Gallery", Toast.LENGTH_LONG).show();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	public void initComponents(){
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, 0);
		editor = pref.edit();
		FBAccessToken="";
		FBAccessToken=pref.getString(AppConstants.Access_Token_PREF, null);
		firstNameTxt = (EditText)view.findViewById(R.id.firstname_edit_txt);
		lastNameTxt = (EditText)view.findViewById(R.id.lastname_edit_txt);
		emailStatusTxt = (TextView)view.findViewById(R.id.email_verify_status);
		/*dobDateTxt = (EditText)view.findViewById(R.id.age_date_edit_txt);
		dobMonthTxt = (EditText)view.findViewById(R.id.age_month_edit_txt);
		dobYearTxt = (EditText)view.findViewById(R.id.age_year_edit_txt);*/
		phoneTxt = (EditText)view.findViewById(R.id.phoneno_edit_txt);
		emailTxt = (TextView)view.findViewById(R.id.emailid_Textview);
		stateTxt = (EditText)view.findViewById(R.id.state_edit_txt);
		countryTxt = (EditText)view.findViewById(R.id.country_edit_txt);
		imgUploadTxt = (TextView)view.findViewById(R.id.img_upload_txt);
		genderRadioGroup = (RadioGroup)view.findViewById(R.id.gender_group);
		int selectedId = genderRadioGroup.getCheckedRadioButtonId();
		selectedGenderBtn = (RadioButton)view.findViewById(selectedId);
		changePassTxt = (TextView)view.findViewById(R.id.change_password_txt);

		if(FBAccessToken!=null && !FBAccessToken.isEmpty()){
			changePassTxt.setVisibility(View.GONE);

		}

		saveBtn = (Button)view.findViewById(R.id.save_btn);
		imgViewUpload = (ImageView)view.findViewById(R.id.profile_img_view);
		maleBtn = (RadioButton) view.findViewById(R.id.gender_male);
		femaleBtn =  (RadioButton) view.findViewById(R.id.gender_female);
		dobTxt = (TextView) view.findViewById(R.id.dob_txt);
		dobImgBtn = (ImageButton) view.findViewById(R.id.dob_img_btn);
		changePassTxt.setText(Html.fromHtml("<u>Change Password</u>"));
		imgViewUpload.setOnClickListener(ProfilePage.this);
		changePassTxt.setOnClickListener(ProfilePage.this);
		saveBtn.setOnClickListener(ProfilePage.this);
		dobImgBtn.setOnClickListener(ProfilePage.this);
		emailStatusTxt.setOnClickListener(ProfilePage.this);
		emailTxt.setSelected(true);
		countryTxt.setOnEditorActionListener(new OnEditorActionListener() {
			@SuppressWarnings({ "unused", "unchecked" })
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					if(isInternetOn()){
						if(profileValidation()){
							String byteStr = null;
							int selectedId = genderRadioGroup.getCheckedRadioButtonId();
							selectedGenderBtn = (RadioButton) view.findViewById(selectedId);
							Utils.printLog("Gender", "" + selectedGenderBtn.getText());
							String act = "updateProfile";
							String userId = pref.getString("Userid", null);
							String firstname = firstNameTxt.getText().toString().trim();
							String lastname = lastNameTxt.getText().toString().trim();
							String phoneno = phoneTxt.getText().toString().trim();
							String emailid = emailTxt.getText().toString().trim();
							String gender = selectedGenderBtn.getText().toString().trim();
							String dob = dobTxt.getText().toString(); 
							String state = stateTxt.getText().toString().trim();
							String country = countryTxt.getText().toString().trim();
							String url = AppConstants.PROFILE_UPDATE_URL;
							// String profileImg = uploadFileNameUri.toString();
							Utils.printLog("UP=",""+uploadFileName);
							/*if(uploadFileName != null && !uploadFileName.equals("")){
							Bitmap bm = BitmapFactory.decodeFile(uploadFileName);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							bm.compress(CompressFormat.JPEG, 75, bos);
							byte[] byteData = bos.toByteArray();
							byteStr = Base64.encodeBytes(byteData);
						}*/
							BasicNameValuePair firstNameValue = new BasicNameValuePair("firstname",firstname);
							BasicNameValuePair lastNameValue = new BasicNameValuePair("lastname",lastname);
							BasicNameValuePair phonenoValue = new BasicNameValuePair("phone",phoneno);
							BasicNameValuePair genderValue = new BasicNameValuePair("gender",gender);
							BasicNameValuePair ageValue = new BasicNameValuePair("dob",dob);
							BasicNameValuePair stateValue = new BasicNameValuePair("state",state);
							BasicNameValuePair countryValue = new BasicNameValuePair("country",country);
							BasicNameValuePair userIdValue = new BasicNameValuePair("userId",userId);
							BasicNameValuePair userId2Value = new BasicNameValuePair(AppConstants.AUTH_KEY,userId);
							List<NameValuePair> updateProfileValues = new ArrayList<NameValuePair>();
							updateProfileValues.add(firstNameValue);
							updateProfileValues.add(lastNameValue);
							updateProfileValues.add(phonenoValue);
							updateProfileValues.add(genderValue);
							updateProfileValues.add(ageValue);
							updateProfileValues.add(stateValue);
							updateProfileValues.add(countryValue);
							updateProfileValues.add(userIdValue);
							updateProfileValues.add(userId2Value);
							new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_SAVE_RESP, ProfilePage.this).execute(updateProfileValues);
						}
					} else {
						Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
					}
				}
				return false;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public void getProfileDetails(){
		String userId = pref.getString(AppConstants.USER_ID_PREF, null);
		String url =AppConstants.PROFILE_INFO_URL;
		//BasicNameValuePair useridValue = new BasicNameValuePair("userId", userId);
		BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
		List<NameValuePair> profilePageList = new ArrayList<NameValuePair>();
		profilePageList.add(useridValue);
		new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_LODE_RESP, this).execute(profilePageList);
		//getDataFromDB();
	}
	@SuppressWarnings("unused")
	private void getDataFromDB(){
		//Get from DB
		Cursor cursor=dbHandler.getProfile();
		Utils.printLog("Cursor",""+cursor);
		if(cursor!=null && cursor.getCount() > 0){
			cursor.moveToFirst();
			Utils.printLog("Cursor Row Count",""+cursor.getCount());
			firstNameTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_FIRST_NAME)));
			lastNameTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_LAST_NAME)));
			phoneTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHONE_NO)));
			emailTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID)));
			String gender = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_GENDER)); 
			if(gender.equals("Male")){
				maleBtn.setChecked(true);
			}else{
				femaleBtn.setChecked(true);
			}
			dobTxt.setText(""+cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_DOB)));
			stateTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_STATE)));
			countryTxt.setText(cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_COUNTRY)));
			Utils.printLog("Photo path",""+cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHOTO)));
			String photoPath = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_PHOTO));
			if(photoPath != null && !photoPath.equals("")){
				uploadFileName = photoPath;
				File file=new File(uploadFileName);
				//imgViewUpload.setImageBitmap(decodeFile(file));
			}
			if(!cursor.isClosed()){
				cursor.close();
			}
		}
	}
	/**
	 * Display alert dialog for given message
	 * 
	 * @param alertMsg
	 * @param title
	 * @param showYesNoButton
	 */
	private void showAlert(String alertMsg, String title,final boolean showYesNoButton){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		dialog.setContentView(R.layout.alert_dialog);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_dialog_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_dialog_cancel_btn);
		if(showYesNoButton){			
			cancelBtn.setVisibility(View.VISIBLE);		
			okBtn.setBackgroundResource(R.drawable.yes_btn_exit);
			cancelBtn.setBackgroundResource(R.drawable.no_btn_exit);
		}
		TextView titleTxt = (TextView) dialog.findViewById(R.id.alert_dialog_title);
		TextView msg = (TextView)dialog.findViewById(R.id.alert_dialog_txt);
		msg.setText(alertMsg);
		if(title.length() > 0){
			titleTxt.setText(Html.fromHtml("<b>" + title + "<b>"));
		}
		okBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings({ "unused", "unchecked" })
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(showYesNoButton){
					//send mail
					String userId = pref.getString(AppConstants.USER_ID_PREF, null);
					String url =AppConstants.ACTIVATE_MAIL_URL;
					String emailID=emailTxt.getText()+"";
					if(!emailID.equals("")) {
						BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.EMAIL,emailID);
						List<NameValuePair> profilePageList = new ArrayList<NameValuePair>();
						profilePageList.add(useridValue);
						if(isInternetOn()){
							new GpsAsyncTask(getActivity(), url,AppConstants.ACTIVATE_MAIL_RESP, ProfilePage.this).execute(profilePageList);
						} else {
							Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
						}
					} else {
						Toast.makeText(getActivity(), "Invalid Email id.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void showChangePassAlert() {
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_change_password);
		final EditText oldPassTxt = (EditText)dialog.findViewById(R.id.old_password_edit_txt);
		final EditText newPassTxt = (EditText)dialog.findViewById(R.id.new_password_edit_txt);
		final EditText reTypeNewPassTxt = (EditText)dialog.findViewById(R.id.retype_new_password_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
		oldPassTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
		newPassTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
		reTypeNewPassTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		reTypeNewPassTxt.setOnEditorActionListener(new OnEditorActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId ==EditorInfo.IME_ACTION_DONE ){
					String currentPass = pref.getString(SessionManager.KEY_PASSWORD, "");
					String userId = pref.getString("Userid", null);
					String oldPass = oldPassTxt.getText().toString().trim();
					String newPass = newPassTxt.getText().toString().trim();
					String reTypePass = reTypeNewPassTxt.getText().toString().trim();
					String url = AppConstants.CHANGE_PASSWORD_URL;
					Utils.printLog("Change pass values", ""+userId);
					if(oldPass != null && oldPass.length()>0 && newPass!=null &&
							newPass.length()>0 && reTypePass!=null && reTypePass.length()>0){
						if(oldPass !=null && oldPass.length()>5 && oldPass.equalsIgnoreCase(currentPass)){
							if(newPass!=null && newPass.length()>5){
								if(!currentPass.equals(newPass)){
									if(newPass.equals(reTypePass)){
										BasicNameValuePair oldPassValue = new BasicNameValuePair("oldPassword", oldPass);
										BasicNameValuePair newPassValue = new BasicNameValuePair("newPassword", newPass);
										BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
										List<NameValuePair> changePasswordValues = new ArrayList<NameValuePair>();
										changePasswordValues.add(oldPassValue);
										changePasswordValues.add(newPassValue);
										changePasswordValues.add(useridValue);
										dialog.dismiss();
										if(isInternetOn()){
											new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_PSWD_CHNG_RESP, ProfilePage.this).execute(changePasswordValues);
										} else {
											Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
										}
									} else {
										Utils.showToast("Passwords does not match");
									}
								}else {
									Utils.showToast("New password and old password should not be same");
								}
							} else {
								Utils.showToast("Password must be atleast six characters");
							}
						} else {
							Utils.showToast("Invalid old password");
						}
					} else {
						Utils.showToast("Enter Details");	
					}
				}
				return true;
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				String currentPass = pref.getString(SessionManager.KEY_PASSWORD, "");
				String userId = pref.getString("Userid", null);
				String oldPass = oldPassTxt.getText().toString().trim();
				String newPass = newPassTxt.getText().toString().trim();
				String reTypePass = reTypeNewPassTxt.getText().toString().trim();
				String url = AppConstants.CHANGE_PASSWORD_URL;
				Utils.printLog("Change pass values", ""+userId);
				if(oldPass != null && oldPass.length()>0 && newPass!=null &&
						newPass.length()>0 && reTypePass!=null && reTypePass.length()>0){
					if(oldPass !=null && oldPass.length()>5 && oldPass.equalsIgnoreCase(currentPass)){
						if(newPass!=null && newPass.length()>5){
							if(!currentPass.equals(newPass)){
								if(newPass.equals(reTypePass)){
									BasicNameValuePair oldPassValue = new BasicNameValuePair("oldPassword", oldPass);
									BasicNameValuePair newPassValue = new BasicNameValuePair("newPassword", newPass);
									BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
									List<NameValuePair> changePasswordValues = new ArrayList<NameValuePair>();
									changePasswordValues.add(oldPassValue);
									changePasswordValues.add(newPassValue);
									changePasswordValues.add(useridValue);
									dialog.dismiss();
									if(isInternetOn()){
										new GpsAsyncTask(getActivity(), url,AppConstants.PROFILE_PSWD_CHNG_RESP, ProfilePage.this).execute(changePasswordValues);
									} else {
										Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
									}
								} else {
									Utils.showToast("Passwords does not match");
								}
							}else {
								Utils.showToast("New password and old password should not be same");
							}
						} else {
							Utils.showToast("Password must be atleast six characters");
						}
					} else {
						Utils.showToast("Invalid old password");
					}
				} else {
					Utils.showToast("Enter Details");	
				}
			}
		});
		dialog.show();
	}
	class ImgUploadAsync extends AsyncTask<List<NameValuePair>, Void, String>{
		Dialog alertProgressDialog = null;
		@Override
		protected void onPreExecute() {
			if (alertProgressDialog == null){
				alertProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
			}
			alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertProgressDialog.setContentView(R.layout.custom_progressbar);
			alertProgressDialog.setCancelable(false);
			alertProgressDialog.show();
			super.onPreExecute();
		}
		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(List<NameValuePair>... params) {
			try{
				String imgUploadURL = AppConstants.PROFILE_IMAGE_URL;
				HttpClient httpClient = new DefaultHttpClient();
				HttpParams httpParams = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
				HttpConnectionParams.setSoTimeout(httpParams, 30000);
				ConnManagerParams.setTimeout(httpParams, 30000);
				HttpPost httpPost = new HttpPost(imgUploadURL);
				UrlEncodedFormEntity urlEncode = new UrlEncodedFormEntity(params[0]);
				httpPost.setEntity(urlEncode);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				Utils.printLog("Response Code", ""+httpResponse.getStatusLine().getStatusCode());
				HttpEntity htpEntity = httpResponse.getEntity();
				String xml = EntityUtils.toString(htpEntity);
				Utils.printLog("Response", ""+xml);
				return xml;
			}
			catch(Exception e){
				Utils.printLog("Excep profile===", ""+e);
			}
			return null;
		}
		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(String serverResp) {
			super.onPostExecute(serverResp);
			try{
				if(serverResp != null){
					Utils.printLog("Response Post_exe", ""+serverResp);
					JSONObject jObj = new JSONObject(serverResp);
					String statusMsg = jObj.getString(AppConstants.STATUS);
					String statusCodeImg = jObj.getString(AppConstants.STATUS_CODE);
					if(statusCodeImg.equals(AppConstants.NEW_SUCCESS)){
						String imageUrl = jObj.getString(AppConstants.IMAGE_URL);
						ImageLoader.getInstance().displayImage(imageUrl, imgViewUpload,options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
							}
							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							}
							@Override
							public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
								Utils.printLog("Load complete", "Load complete");
								final ImageView imageView = (ImageView) view;
								/*Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
								imageView.setImageBitmap(bitmap);
								//Close the progress bar*/

								ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
								observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
									@Override
									public boolean onPreDraw() {
										imageView.getViewTreeObserver().removeOnPreDrawListener(this);

										if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
											Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());

											bitmap=Bitmap.createScaledBitmap(bitmap, 110, 110, true);
											imageView.setImageBitmap(bitmap);
											//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
										}
										return true;
									}
								});


							}
						});
						dbHandler.updateProfileImage(imageUrl);
						if (alertProgressDialog != null && alertProgressDialog.isShowing()) {
							alertProgressDialog.dismiss();
							alertProgressDialog = null;
						}
						Utils.showToast("Image Uploaded Successfully");
					} else if (statusCodeImg == AppConstants.NEW_FAILED) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					} else if (statusCodeImg == AppConstants.IMAGE_UPLOAD_FAILED){
						Utils.showToast("Image Upload Failed");
					}
				} else {
					Utils.showToast("No Response From Server");
				}
			}
			catch(Exception e){
			}
		}
	}
	@SuppressWarnings("unused")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == GALLERY_RES_CODE) {
				if (data != null) {
					Uri uploadFileNameUri = data.getData();
					if (uploadFileNameUri != null) {
						try{
							Cursor cur = getActivity().getContentResolver().query(uploadFileNameUri,new String[]{ MediaColumns.DATA }, null, null,null);
							if(cur!=null){
								cur.moveToFirst();
								uploadFileName = cur.getString(0);
							}
							else{
								Utils.showToast("File is corrupted.");	
							}
							if(uploadFileName!=null && ! uploadFileName.isEmpty()&& !uploadFileName.equalsIgnoreCase("null")){
								Utils.printLog("Upload file", uploadFileName);
								imgUploadTxt.setText(uploadFileName);
								camProfileImage = new File(uploadFileName);
								if(camProfileImage!=null && camProfileImage.exists()){
									String uri = Uri.fromFile(camProfileImage).toString();	
									//decode for file name space problem
									String decoded = Uri.decode(uri);
									Utils.printLog("uri", "" + decoded);
									// Upload image to server
									ExifInterface ei;
									float angleValue = 0 ;
									try {
										ei = new ExifInterface(uploadFileName);
										int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
												ExifInterface.ORIENTATION_NORMAL);
										switch (orientation) {
										case ExifInterface.ORIENTATION_ROTATE_90:
											angleValue = 90;
											break;
										case ExifInterface.ORIENTATION_ROTATE_180:
											angleValue =  180;
											break;
										case ExifInterface.ORIENTATION_ROTATE_270:
											angleValue = 270;
											break;
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									startCropImage();
								}
							}
							else{
								Utils.showToast("File is corrupted.");	
							}
						}
						catch(Exception ex){ex.printStackTrace();}
					}
				}
			} else if (requestCode == CAMERA_RES_CODE) {
				if (camProfileImage != null && camProfileImage.exists()) {
					Utils.printLog("Data", "" + camProfileImage);
					startCropImage();
					uploadFileName = camProfileImage.getPath();
					String uri = Uri.fromFile(camProfileImage).toString();
					//decode for file name space problem
					String decoded = Uri.decode(uri);
					Utils.printLog("uri", "" + decoded);
					ExifInterface ei;
					float angleValue = 0 ;
					try {
						ei = new ExifInterface(uploadFileName);
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL);
						switch (orientation) {
						case ExifInterface.ORIENTATION_ROTATE_90:
							angleValue = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							angleValue =  180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							angleValue = 270;
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			else if(requestCode==REQUEST_CODE_CROP_IMAGE){
				String path = data.getStringExtra(CropImage.IMAGE_PATH);
				if (path == null) {

					return;
				}
				//bitmap = BitmapFactory.decodeFile(camProfileImage.getPath());
				if (camProfileImage != null && camProfileImage.exists()) {
					Utils.printLog("Data", "" + camProfileImage);
					uploadFileName = camProfileImage.getPath();
					String uri = Uri.fromFile(camProfileImage).toString();
					//decode for file name space problem
					String decoded = Uri.decode(uri);
					Utils.printLog("uri", "" + decoded);
					ExifInterface ei;
					float angleValue = 0 ;
					try {
						ei = new ExifInterface(uploadFileName);
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL);
						switch (orientation) {
						case ExifInterface.ORIENTATION_ROTATE_90:
							angleValue = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							angleValue =  180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							angleValue = 270;
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					loadImage(decoded,angleValue);	
				}
				if (data != null) {
					Uri uploadFileNameUri = data.getData();
					if (uploadFileNameUri != null) {
						try{
							Cursor cur = getActivity().getContentResolver().query(uploadFileNameUri,new String[]{ MediaColumns.DATA }, null, null,null);
							if(cur!=null){
								cur.moveToFirst();
								uploadFileName = cur.getString(0);
							}
							else{
								Utils.showToast("File is corrupted.");	
							}
							if(uploadFileName!=null && ! uploadFileName.isEmpty()&& !uploadFileName.equalsIgnoreCase("null")){
								Utils.printLog("Upload file", uploadFileName);
								imgUploadTxt.setText(uploadFileName);
								camProfileImage = new File(uploadFileName);
								if(camProfileImage!=null && camProfileImage.exists()){
									String uri = Uri.fromFile(camProfileImage).toString();	
									//decode for file name space problem
									String decoded = Uri.decode(uri);
									Utils.printLog("uri", "" + decoded);
									// Upload image to server
									ExifInterface ei;
									float angleValue = 0 ;
									try {
										ei = new ExifInterface(uploadFileName);
										int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
												ExifInterface.ORIENTATION_NORMAL);
										switch (orientation) {
										case ExifInterface.ORIENTATION_ROTATE_90:
											angleValue = 90;
											break;
										case ExifInterface.ORIENTATION_ROTATE_180:
											angleValue =  180;
											break;
										case ExifInterface.ORIENTATION_ROTATE_270:
											angleValue = 270;
											break;
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									loadImage(decoded,angleValue);	
								}
							}
							else{
								Utils.showToast("File is corrupted.");	
							}
						}
						catch(Exception ex){ex.printStackTrace();}
					}
				}


			}
		}
	}


	private void startCropImage() {

		Intent intent = new Intent(getActivity(), CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH,  camProfileImage.getPath());
		/*intent.putExtra(CropImage.OUTPUT_X, 100);
	        intent.putExtra(CropImage.OUTPUT_Y, 100);*/
		intent.putExtra(CropImage.ASPECT_X, 3);
		intent.putExtra(CropImage.ASPECT_Y, 3);
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra(CropImage.CIRCLE_CROP, new String(""));
		intent.putExtra(CropImage.RETURN_DATA, false);
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}

	public Bitmap callPhotoUploadMethod(String imagePath1 , float angle) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath1, o);
		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;
		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmapOrg = BitmapFactory.decodeFile(imagePath1, o2);
		if(angle != 0){
			bitmapOrg = rotateImage(bitmapOrg, angle);
		}
		return bitmapOrg;
	}		
	private Bitmap rotateImage(Bitmap source, float angle) {
		Bitmap bitmap = null;
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		try {
			bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
					matrix, true);
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
		return bitmap;
	}
	@SuppressWarnings("unchecked")
	private void loadImage(String uri , float angle) {
		String userId = pref.getString("Userid", null);
		String byteStr = null;
		Utils.printLog("UploadFileName=", "" + uploadFileName);
		if (uploadFileName != null && !uploadFileName.equals("")) {
			try {
				Bitmap bm = callPhotoUploadMethod(uploadFileName,angle);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bm.compress(CompressFormat.JPEG, 75, bos);
				byte[] byteData = bos.toByteArray();
				Utils.printLog("Size=", "" + bos.size());
				byteStr = Base64.encodeBytes(byteData);
				Utils.printLog("byteStr=", "" + byteStr);
				BasicNameValuePair imgValue = new BasicNameValuePair("file",byteStr);
				BasicNameValuePair imgUserValue = new BasicNameValuePair("userId", userId);
				List<NameValuePair> updateProfileImg = new ArrayList<NameValuePair>();
				updateProfileImg.add(imgValue);
				updateProfileImg.add(imgUserValue);
				if(isInternetOn()){
					new ImgUploadAsync().execute(updateProfileImg);
				}else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);	
				}
			} catch (OutOfMemoryError e) {
				Utils.showToast("Image size is too large");
			}
		}
	}
	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		if(serverResp != null){
			if(RespValue == AppConstants.PROFILE_LODE_RESP){
				try{
					JSONObject jObj = new JSONObject(serverResp);
					String statusCode = jObj.getString(AppConstants.STATUS_CODE);
					if(statusCode.equals(AppConstants.NEW_SUCCESS)){
						String upperString=jObj.getString(AppConstants.USER_NAME).equals("null") ? "":""+jObj.getString(AppConstants.USER_NAME);
						if(upperString!=null && !upperString.equals("") ){
							upperString = upperString.substring(0,1).toUpperCase() + upperString.substring(1);
						}
						firstNameTxt.setText(upperString);
						lastNameTxt.setText(jObj.getString(AppConstants.USER_LAST_NAME).equals("null") ? "":""+jObj.getString(AppConstants.USER_LAST_NAME));
						phoneTxt.setText(jObj.getString(AppConstants.PHONE_NUMBER).equals("null") ? "":""+jObj.getString(AppConstants.PHONE_NUMBER));
						emailTxt.setText(jObj.getString(AppConstants.EMAIL_ID).equals("null") ? "":""+jObj.getString(AppConstants.EMAIL_ID));
						boolean verified_Status = jObj.getString(AppConstants.VERIFIED_STATUS).equalsIgnoreCase("0") ? false : true;
						String userVerifiedStatus = jObj.getString(AppConstants.VERIFIED_STATUS);
						editor.putString(AppConstants.USER_VERIFIED_STATUS_PREF, userVerifiedStatus);
						editor.commit();
						Drawable statusImage = null;
						if(verified_Status) {
							//statusImage = R.drawable.req_btn_acpt;
							emailStatusTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.accept_clicked_btn, 0);
							emailStatusTxt.setText("Verified");
							//(R.drawable.req_btn_acpt, null, null, null);
						} else {
							emailStatusTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.reject_btn_clicked, 0);
							emailStatusTxt.setText("Not Verified");
						}
						String gender = jObj.getString(AppConstants.GENDER); 
						if (gender != null && !gender.equals("null")) {
							if (gender.equals("male")) {
								maleBtn.setChecked(true);
							} else {
								femaleBtn.setChecked(true);
							}
						}
						String dob = jObj.getString(AppConstants.DOB);
						if (dob != null &&  !dob.equals("null")) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd", Locale.ENGLISH);
							calendarDatePicker = Calendar.getInstance();
							try {
								calendarDatePicker.setTime(dateFormat.parse(dob));
								int month = (calendarDatePicker.get(Calendar.MONTH) + 1);
								String dobString = ""
										+ calendarDatePicker.get(Calendar.YEAR)
										+ "-"
										+ new DecimalFormat("00").format(month)
										+ "-"
										+ new DecimalFormat("00")
								.format(calendarDatePicker
										.get(Calendar.DATE));
								dobTxt.setText(dobString);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						stateTxt.setText(jObj.getString(AppConstants.STATE).equals("null") ? "":""+jObj.getString(AppConstants.STATE));
						countryTxt.setText(jObj.getString(AppConstants.COUNTRY).equals("null") ? "":""+jObj.getString(AppConstants.COUNTRY));
						Utils.printLog("Photo path",""+jObj.getString(AppConstants.PROF_IMG));
						//String photoPath = jObj.getString(AppConstants.PROF_IMG).equals("null") ? "":""+jObj.getString(AppConstants.PROF_IMG);
						String photoPath = jObj.getString(AppConstants.PROF_IMG);
						uploadFileName = photoPath;
						ImageLoader.getInstance().displayImage(photoPath, imgViewUpload,options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
							}
							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							}
							@Override
							public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
								Utils.printLog("Load complete", "Load complete");
								final ImageView imageView = (ImageView) view;
								/*Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
								imageView.setImageBitmap(bitmap);	*/

								ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
								observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {

									@Override
									public boolean onPreDraw() {
										imageView.getViewTreeObserver().removeOnPreDrawListener(this);

										if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
											Bitmap bitmap = ImageLoader1.getRoundCroppedBitmapimg(loadedImage, imageView.getWidth());
											imageView.setImageBitmap(bitmap);
											//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
										}
										return true;
									}
								});
							}
						});
					} else if(statusCode.equals(AppConstants.NEW_FAILED)) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					}
				} catch(Exception e) {

				}
			}else if(RespValue == AppConstants.PROFILE_SAVE_RESP){
				JSONObject jObj;
				try {
					jObj = new JSONObject(serverResp);
					String statusMsg = jObj.getString(AppConstants.STATUS);
					String statusCode = jObj.getString(AppConstants.STATUS_CODE);
					String msg = jObj.getString(AppConstants.MESSAGE);
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){
						String firstName = firstNameTxt.getText().toString().trim();
						dbHandler.updateFirstName(firstName);
						editor.putString(AppConstants.USER_NAME_PREF, firstName);
						editor.commit();
						Utils.showToast("Updated Successfully");
					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					}else if (statusCode.equalsIgnoreCase(AppConstants.UPDATE_FAILED)) {
						Utils.showToast("Failed");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if(RespValue == AppConstants.PROFILE_PSWD_CHNG_RESP) {
				JSONObject jObj;
				try {
					jObj = new JSONObject(serverResp);
					String statusMsg = jObj.getString(AppConstants.STATUS);
					String statusCode = jObj.getString(AppConstants.STATUS_CODE);
					String msg = jObj.getString(AppConstants.MESSAGE);
					switch (statusCode) {
					case AppConstants.NEW_SUCCESS  :
						Utils.showToast("Password Updated Successfully");
						break;
					case AppConstants.PASSWORD_UPDATE_FAILED  :
						Utils.showToast("Password Update Failed");
						break;
					case AppConstants.OLD_PASSWORD_INVALID  :
						Utils.showToast("Old Password Invalid");
						break;
					case AppConstants.NEW_FAILED  :
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if(RespValue == AppConstants.PROFILE_DELETE_RESP){
				JSONObject jObj;
				try {
					jObj = new JSONObject(serverResp);
					String statusMsg = jObj.getString(AppConstants.STATUS);
					String statusCode = jObj.getString(AppConstants.STATUS_CODE);
					String msg = jObj.getString(AppConstants.MESSAGE);
					if(statusMsg.equals("success")){
						if(statusCode.equals(AppConstants.NEW_SUCCESS)){
							uploadFileName=null;
							imgViewUpload.setImageResource(R.drawable.uploadimg_btn);
							//Update database
							dbHandler.updateProfileImage("");
							Utils.showToast(msg);
						}
					}else{
						Utils.showToast(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if(RespValue == AppConstants.ACTIVATE_MAIL_RESP){
				JSONObject jObj;
				try {
					jObj = new JSONObject(serverResp);
					String msg = jObj.getString(AppConstants.MESSAGE);
					String statusCode = jObj.getString(AppConstants.STATUS_CODE);
					if(statusCode.equals(AppConstants.NEW_SUCCESS)){
						Toast.makeText(getActivity(), "Mail sent successfully to your mail id.", Toast.LENGTH_LONG).show();
					}else if(statusCode.equals(AppConstants.NEW_FAILED)){
						//Code to change emailStatusTextView
						editor.putString(AppConstants.USER_VERIFIED_STATUS_PREF, "true");
						editor.commit();
						emailStatusTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.req_btn_acpt, 0);
						emailStatusTxt.setText("Verified");
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}else if(statusCode.equals(AppConstants.INVALID_EMAIL_ID)){
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			Utils.showToast("No response from server");
		}
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
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
				//Clear  session
				dialog.dismiss();
				SessionManager session = new SessionManager(getActivity());					
				session.logoutUser(getActivity());
				//Load login 
				Intent LoginIntent = new Intent(getActivity(), Login.class);
				startActivity(LoginIntent);
				getActivity().finish();
			}});
		dialog.show();
	}
	/*	public void showAlertVerifiedStatus(){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
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
			}
		});
		dialog.show();
	}*/
	@Override
	public void onDestroy() {
		if(dbHandler!=null){
			dbHandler.close();
		}
		super.onDestroy();
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

}