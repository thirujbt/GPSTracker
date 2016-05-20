
package com.gpsmobitrack.gpstracker.ImageLoaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;

import com.gpsmobitrack.gpstracker.Utils.Utils;


public class ImageLoader1 {

	MemoryCache1 memoryCache = new MemoryCache1();

	FileCache1 fileCache;

	ExecutorService executorService;

	public static boolean isImageRounded;

	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

	public ImageLoader1(Context context) {

		fileCache = new FileCache1(context);
		executorService = Executors.newFixedThreadPool(5);

	}

	//Display rounded image
	public void displayImageRounded(String url,Activity activity,ImageView imageView,int radius){
		isImageRounded = true;
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
		{
			Bitmap cropedBitmap = getCroppedBitmap(bitmap, radius);
			imageView.setImageBitmap(cropedBitmap);
		}
		else {
			queuePhoto(url, activity, imageView);
			imageView.setImageBitmap(null);
		}
	}

	//Crop rounded image
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
				sbmp.getWidth() / 2+0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		return output;
		
	}

	//Crop new rounded image
			@SuppressWarnings("unused")
			public static Bitmap getRoundCroppedBitmap(Bitmap bmp, int radius) {
				Bitmap sbmp;
				if(bmp.getWidth() != radius || bmp.getHeight() != radius)
					sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
				else
					sbmp = bmp;
				
			/*	Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
						sbmp.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);

				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setDither(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(Color.parseColor("#BAB399"));
				canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
						sbmp.getWidth() / 2+0.1f, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(sbmp, rect, rect, paint);*/
				
				 Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
				 
		            Canvas canvas = new Canvas(output);
		 
		            final int color = 0xff424242;
		            final Paint paint = new Paint();
		            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		 
		            paint.setAntiAlias(true);
		            paint.setFilterBitmap(true);
		            paint.setDither(true);
		            canvas.drawARGB(0, 0, 0, 0);
		        //    paint.setColor(color);
		            paint.setColor(Color.parseColor("#BAB399"));
		 
		            //--CROP THE IMAGE
		            canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, sbmp.getWidth() / 2 - 1, paint);
		            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		            canvas.drawBitmap(sbmp, rect, rect, paint);
				
				
		             final Paint paint2 = new Paint();
		             paint2.setAntiAlias(true);
		             paint2.setColor(Color.parseColor("#01C2D4"));
		             paint2.setStrokeWidth(3);
		             paint2.setStyle(Paint.Style.STROKE);
		             canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, (float) (sbmp.getWidth() / 2 - Math.ceil(5 / 2)), paint2);
		         

				return output;
			}
			
			public static Bitmap getRoundCroppedBitmapimgchat(Bitmap bmp, int radius) {
				Bitmap sbmp;
				if(bmp.getWidth() != radius || bmp.getHeight() != radius)
					sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
				else
					sbmp = bmp;
				 Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
				 
		            Canvas canvas = new Canvas(output);
		            final Paint paint = new Paint();
		            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		            paint.setAntiAlias(true);
		            paint.setFilterBitmap(true);
		            paint.setDither(true);
		            canvas.drawARGB(0, 0, 0, 0);
		            paint.setColor(Color.parseColor("#BAB399"));
		 
		            //--CROP THE IMAGE
		            canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, sbmp.getWidth() / 2 - 1, paint);
		            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		            canvas.drawBitmap(sbmp, rect, rect, paint);
				
		             final Paint paint2 = new Paint();
		             paint2.setAntiAlias(true);
		             paint2.setColor(Color.parseColor("#01C2D4"));
		             paint2.setStrokeWidth(3);
		             paint2.setStyle(Paint.Style.STROKE);
		             canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, (float) (sbmp.getWidth() / 2 - Math.ceil(5 / 2)), paint2);
		
				return output;
			}
			
			
			public static Bitmap getRoundCroppedBitmapimg(Bitmap bmp, int radius) {
				Bitmap sbmp;
				if(bmp.getWidth() != radius || bmp.getHeight() != radius)
					sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
				else
					sbmp = bmp;
				 Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
				 
		            Canvas canvas = new Canvas(output);
		            final Paint paint = new Paint();
		            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		            paint.setAntiAlias(true);
		            paint.setFilterBitmap(true);
		            paint.setDither(true);
		            canvas.drawARGB(0, 0, 0, 0);
		            paint.setColor(Color.parseColor("#BAB399"));
		 
		            //--CROP THE IMAGE
		            canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, sbmp.getWidth() / 2 - 1, paint);
		            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		            canvas.drawBitmap(sbmp, rect, rect, paint);
				
		             final Paint paint2 = new Paint();
		             paint2.setAntiAlias(true);
		             paint2.setColor(Color.parseColor("#FFFFFF"));
		             paint2.setStrokeWidth(3);
		             paint2.setStyle(Paint.Style.STROKE);
		             canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, (float) (sbmp.getWidth() / 2 - Math.ceil(5 / 2)), paint2);
		
				return output;
			}
			
			

	public void displayImage(String url, Activity activity, ImageView imageView) {

		// 	Utils.printLog("url", "" + url);
		isImageRounded = false;
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, activity, imageView);
			imageView.setImageBitmap(null);
		}
	}

	private void queuePhoto(String url, Activity activity, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String url) {
		try {
			File f = fileCache.getFile(url);
			Bitmap b = decodeFile(f);
			// from SD cache
			if (b != null)
				return b;
			// from web
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.copyStream( is, os);
			os.close();
			bitmap =  decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			return null;
		} catch (Error er) {
			return null;
		}
	}
	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = 1;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (Exception e) {
		}
		return null;
	}
	private class PhotoToLoad {
		public String url;

		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);


			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null){
				//set rounded bit map
				if(isImageRounded){
					Utils.printLog("ImageLoader", "Rounded");
					photoToLoad.imageView.setImageBitmap(getCroppedBitmap(bitmap, photoToLoad.imageView.getWidth()));
				}
				else{
					Utils.printLog("ImageLoader", "Normal");
					photoToLoad.imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	public void clearCache() {
	}
}
