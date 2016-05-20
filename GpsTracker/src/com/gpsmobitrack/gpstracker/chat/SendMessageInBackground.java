package com.gpsmobitrack.gpstracker.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.app.Activity;
import android.os.AsyncTask;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;

public class SendMessageInBackground extends AsyncTask<String, Void, String> {

	// Required initialization
	@SuppressWarnings("unused")
	private String Error = null;
	String data  = ""; 
	int sizeData = 0;
	Activity mActivity;
	int respType;
	AsyncResponse mAsyncResponse;
	public SendMessageInBackground(int respValue,Activity activity) {
		super();
		mActivity = activity;
		respType = respValue;
		mAsyncResponse = (AsyncResponse) mActivity;
	}

	protected void onPreExecute() {

	}
	// Call after onPreExecute method
	protected String doInBackground(String... params) {
		/************ Make Post Call To Web Server ***********/
		BufferedReader reader=null;
		String Content = "";
		// Send data 
		try{
			// Defined URL  where to send data
			URL url = new URL(params[0]);
			// Set Request parameter
			if(!params[1].equals(""))
				data +="" + URLEncoder.encode("data1", "UTF-8") + "="+params[1].toString();
			if(!params[2].equals(""))
				data +="&" + URLEncoder.encode("data2", "UTF-8") + "="+params[2].toString();	
			if(!params[3].equals(""))
				data +="&" + URLEncoder.encode("data3", "UTF-8") + "="+params[3].toString();	
			if(!params[4].equals(""))
				data +="&" + URLEncoder.encode("data4", "UTF-8") + "="+params[4].toString();
			if(!params[5].equals(""))
				data +="&" + URLEncoder.encode("data5", "UTF-8") + "="+params[5].toString();
			if(!params[6].equals(""))
				data +="&" + URLEncoder.encode("data6", "UTF-8") + "="+params[6].toString();
			if(!params[7].equals(""))
				data +="&" + URLEncoder.encode("data7", "UTF-8") + "="+params[7].toString();
			if(!params[8].equals(""))
				data +="&" + URLEncoder.encode("data8", "UTF-8") + "="+params[8].toString();
			// Send POST data request
			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true); 
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
			wr.write( data ); 
			wr.flush(); 
			// Get the server response 
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while((line = reader.readLine()) != null){
				// Append server response in string
				sb.append(line + "\n");
			}
			// Append Server Response To Content String 
			Content = sb.toString();
		}
		catch(Exception ex){
			Error = ex.getMessage();
		}
		finally{
			try{
				reader.close();
			}
			catch(Exception ex) {}
		}
		/*****************************************************/
		return Content;
	}
	protected void onPostExecute(String Result) {
		// NOTE: You can call UI Element here.
		// Close progress dialog
		// Dialog.dismiss();
		//addNewMessage(new Message(msg, true, false));
		/* footerView.setVisibility(View.GONE);
        if (Error != null) {
        	Toast.makeText(getBaseContext(), "Error: "+Error, Toast.LENGTH_LONG).show();  
        } else {
        	// Show Response Json On Screen (activity)
        	// Toast.makeText(getBaseContext(), "Message sent."+Result, Toast.LENGTH_LONG).show();  
         }*/
		mAsyncResponse.onProcessFinish("", respType);
	}
}