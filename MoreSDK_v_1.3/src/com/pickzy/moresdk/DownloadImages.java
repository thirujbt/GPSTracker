package com.pickzy.moresdk;

import android.content.Context;
import android.os.AsyncTask;

import com.pickzy.moresdk.morecache.ImageLoader;

public class DownloadImages {
Context context;
private ImageLoader loader;
	public DownloadImages(Context context){
		this.context=context;
		loader=new ImageLoader(context, context.getPackageName());
		new backgroundtask().execute("Dounloadimages");
	}
	
	public class backgroundtask extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			try{
			loader.downloadBitmap (MoreContants.main+"/appimages/tabButton.png", "tabButton.png");
			loader.downloadBitmap(MoreContants.main+"/appimages/tabSelect.png", "tabSelect.png");
			loader.downloadBitmap(MoreContants.main+"/appimages/btnPlain.png", "btnplain.png");
			loader.downloadBitmap(MoreContants.main+"/appimages/MORE_tvc_backgroundLight.png", "MORE_tvc_backgroundLight.png");
			loader.downloadBitmap(MoreContants.main+"/appimages/MORE_tvc_backgroundDark.png", "MORE_tvc_backgroundDark.png");
			}catch(Exception e){
			}
			return null;
		}
		
	}
}
