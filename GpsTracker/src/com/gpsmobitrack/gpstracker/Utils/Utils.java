package com.gpsmobitrack.gpstracker.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsmobitrack.gpstracker.MyApplication;
import com.gpstracker.pro.R;

public class Utils {

	//Email Validation
	public static boolean validEmail(String email) {

		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}


	public static void copyStream(final InputStream is, final OutputStream os) {
		final int buffer_size = 1024;
		try {
			final byte[] bytes = new byte[buffer_size];
			for (;;) {
				final int count = is.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				os.write(bytes, 0, count);
			}
		} catch (final Exception ex) {
		}
	}



	//Show Alert When GPS not Enabled
	public static void showSettingsAlert(final Context cont){	
		final Dialog dialog = new Dialog(cont, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		alertEditTxt.setVisibility(View.GONE);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AppConstants.ALERT_TITLE);
		alertMsg.setText(AppConstants.ALERT_ENABLE_GPS);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				cont.startActivity(settingsIntent);
				dialog.dismiss();

			}
		});

		dialog.show();
	}
	

	@SuppressLint("DefaultLocale")
	public static void printLog(String Tag,String Msg){
		
		if(AppConstants.IS_TEST_MODE){
		}
	}


	public static void showToast(String Msg){

		LayoutInflater li = (LayoutInflater) MyApplication.getAppContext()
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = li.inflate(R.layout.customtoast, null);  

		TextView msgTxt = (TextView) layout.findViewById(R.id.custom_toast_message);
		msgTxt.setText(Msg);
		Toast toast = new Toast(MyApplication.getAppContext());  
		toast.setDuration(Toast.LENGTH_LONG);  
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);  
		toast.setView(layout);//setting the view of custom toast layout  
		toast.show();  
	}

}
