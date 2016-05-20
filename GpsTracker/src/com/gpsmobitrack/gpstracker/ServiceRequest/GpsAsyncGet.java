package com.gpsmobitrack.gpstracker.ServiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpstracker.pro.R;

public class GpsAsyncGet extends AsyncTask<String, String, String> {

	Context ctn;
	Dialog alertProgressDialog = null;
	String url,xml;
	StringBuffer sb;
	int RespInt;
	public AsyncResponse responseInterface ;

	public GpsAsyncGet(Context context, String url, int respInt, AsyncResponse respAsyn){
		this.ctn = context;
		this.url = url;
		this.responseInterface = respAsyn;
		this.RespInt = respInt;
	}

	protected void onPreExecute() {
		if (alertProgressDialog == null){

			alertProgressDialog = new Dialog(ctn, android.R.style.Theme_Translucent);
			alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertProgressDialog.setContentView(R.layout.custom_progressbar);
			alertProgressDialog.setCancelable(false);
			alertProgressDialog.show();

		}
	}

	@Override
	protected String doInBackground(String... uri) {
		System.gc();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			//TODO Handle problems..
		} catch (IOException e) {
			//TODO Handle problems..
		}
		System.gc();
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		//Do anything with response..
		responseInterface.onProcessFinish(result , RespInt);
		if (alertProgressDialog != null && alertProgressDialog.isShowing()) {
			alertProgressDialog.dismiss();
			alertProgressDialog = null;
		}
	}
}