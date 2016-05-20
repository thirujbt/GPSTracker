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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class UserTrackAsync extends AsyncTask<List<NameValuePair>, Void, String> {

	Context ctn;
	String xml,url;
	int RespInt;
	Dialog alertProgressDialog = null;
	public AsyncResponse responseInterface ;
	Activity activity;
	public UserTrackAsync(Context context, String url, int respInt, AsyncResponse respAsyn){
		activity = (Activity) context;
		this.ctn = context;
		this.url = url;
		this.responseInterface = respAsyn;
		this.RespInt = respInt;
	}
	
	protected void onPreExecute() {
		
		if(!activity.isFinishing()){
			
		
		
		if (alertProgressDialog == null)
			alertProgressDialog = new Dialog(ctn, android.R.style.Theme_Translucent);
		//alertProgressDialog.setMessage("Please Wait");
		//alertProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertProgressDialog.setContentView(R.layout.custom_progressbar);
		alertProgressDialog.setCancelable(false);
		//alertProgressDialog.setIndeterminate(true);
		alertProgressDialog.show();
		}
		Utils.printLog("Async profile==", "Pre Execute");
	}
	@SuppressWarnings("unchecked")
	@Override
	protected String doInBackground(List<NameValuePair>... params) {
		// TODO Auto-generated method stub
		Utils.printLog("Params===", ""+params[0]);
		System.gc();
		try{
		HttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		ConnManagerParams.setTimeout(httpParams, 30000);
		HttpPost httpPost = new HttpPost(url);
		
		UrlEncodedFormEntity urlEncode = new UrlEncodedFormEntity(params[0]);
		httpPost.setEntity(urlEncode);
		
		HttpResponse httpResponse = httpClient.execute(httpPost);
		Utils.printLog("Response Code", ""+httpResponse.getStatusLine().getStatusCode());
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
		Utils.printLog("Async profile", "Post Exe");
		Utils.printLog("String Result", ""+result);
		Utils.printLog("asyn",""+RespInt);
		responseInterface.onProcessFinish(result , RespInt);
		if (alertProgressDialog != null && alertProgressDialog.isShowing()&& !activity.isFinishing()) {
			alertProgressDialog.dismiss();
			alertProgressDialog = null;
		}
		
	}

}
