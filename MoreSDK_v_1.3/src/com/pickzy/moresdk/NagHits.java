package com.pickzy.moresdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pickzy.moresdk.PZYAppPromoSDK.MoreAppsData;

import android.os.AsyncTask;
import android.util.Log;

public class NagHits {
	public static String key,packagename;
public NagHits(){
	try{
	if(key.equals("")){
		key=MoreContants.key;
	}
	}catch(Exception e){
		
	}
	new hitposter().execute("pickzy");
	
}
private class hitposter extends AsyncTask<String, Integer, String>{

	@Override
	protected String doInBackground(String... params) {
		
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient(); //Api -1 ->DefaultHttpClient
				HttpGet httpget=new HttpGet(MoreContants.main+"/apps/androidapi.php?act=naghits&usrlickey="+key+"&appid="+packagename+"&imp=1&hit=1&uqid=&os=1");//Api -1 -> HttpGet
				HttpResponse httpResponse = httpClient.execute(httpget);//Api -1 ->HttpResponse
				Log.e("Posting nag key", key);
				Log.e("Posting nag key", key);
				Log.e("Posting nag key", key);
				Log.e("Posting nag hit", "Posting hit");
				Log.e("Posting nag hit", "Posting hit");
				Log.e("Posting nag hit", "Posting hit");
			} catch (UnsupportedEncodingException e) {
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
		return null;
	}
}
}