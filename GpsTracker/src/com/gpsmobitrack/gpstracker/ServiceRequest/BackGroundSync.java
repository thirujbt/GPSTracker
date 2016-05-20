package com.gpsmobitrack.gpstracker.ServiceRequest;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.os.AsyncTask;


import com.gpsmobitrack.gpstracker.Utils.Utils;

public class BackGroundSync extends AsyncTask<List<NameValuePair>, Void, String> {

	Context ctn;
	String statusResp,msgResp,xml,url;
	//ProgressDialog alertProgressDialog = null;

	public BackGroundSync(Context context, String url){
		this.ctn = context;
		this.url = url;
	}

	protected void onPreExecute() {
		//Utils.printLog("Pre exe", "BackSync");
	}
	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(List<NameValuePair>... params) {
		// TODO Auto-generated method stub
		try{
			System.gc();
			//Log.e("URL", ""+"SAAAAAANJEEEEEEEEVVVVVVV");
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			ConnManagerParams.setTimeout(httpParams, 30000);
			HttpPost httpPost = new HttpPost(url);

			UrlEncodedFormEntity urlEncode = new UrlEncodedFormEntity(params[0]);
			httpPost.setEntity(urlEncode);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity htpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(htpEntity);   
			Utils.printLog("Response", ""+xml);

		}
		catch(Exception e){
			System.gc();
			Utils.printLog("Excep profile===", ""+e);
		}
 
		return xml;
	}

	protected void onPostExecute(String result) {
		Utils.printLog("Post exe", "BackSync");
	}

}
