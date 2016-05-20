package com.gpsmobitrack.gpstracker.ServiceRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class GpsAsyncJSON extends AsyncTask<String, Void, String>{

	Context ctn;
	Dialog alertProgressDialog = null;
	String url,xml;
	StringBuffer sb;
	int RespInt;
	public AsyncResponse responseInterface ;

	public GpsAsyncJSON(Context context, String url, int respInt, AsyncResponse respAsyn){
		this.ctn = context;
		this.url = url;
		this.responseInterface = respAsyn;
		this.RespInt = respInt;
	}

	protected void onPreExecute() {
		if (alertProgressDialog == null)
			alertProgressDialog = new Dialog(ctn, android.R.style.Theme_Translucent);
		alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertProgressDialog.setContentView(R.layout.custom_progressbar);
		alertProgressDialog.setCancelable(false);
		alertProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		System.gc();
		Utils.printLog("Invite params", ""+params[0]);
		Utils.printLog("Invite URL", ""+url);
		try{
			HttpPost httpPost = new HttpPost(url);
			StringEntity entity = new StringEntity(params[0]);
			entity.setContentType("application/json;charset=UTF-8");
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(entity); 

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 50000);
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),50000); 
			HttpResponse httpResp = httpClient.execute(httpPost);
			Utils.printLog("Response Code", ""+httpResp.getStatusLine().getStatusCode());

			Utils.printLog("Response===", ""+httpResp.getEntity());
			InputStream in = httpResp.getEntity().getContent();
			//  BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			//  String line = "";
			//   while((line = reader.readLine()) != null){
			//     sb.append(line);
			//   }
			//xml = sb.toString(); 


			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(in), 65728);
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
					Utils.printLog("Line", "="+line);

				}
				xml = sb.toString(); 
			}

			catch (IOException e) { e.printStackTrace(); }
			catch (Exception e) { e.printStackTrace(); }


			/*StringEntity se = new StringEntity( params[0]); 

	    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setEntity(se);
		httpPost.setEntity(new ByteArrayEntity(params[0].getBytes("UTF8")));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();*/
			//xml = EntityUtils.toString(httpEntity);
			Utils.printLog("Invite Asyn res", ""+sb);
		}
		catch(IOException e){
			System.gc();
			Utils.printLog("Invite Exccep==", ""+e);
		}

		return xml;
	}

	protected void onPostExecute(String result) {
		responseInterface.onProcessFinish(result , RespInt);
		if (alertProgressDialog != null && alertProgressDialog.isShowing()) {
			alertProgressDialog.dismiss();
			alertProgressDialog = null;
		}
	}



}
