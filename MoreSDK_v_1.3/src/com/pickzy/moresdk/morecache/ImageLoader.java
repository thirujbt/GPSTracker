package com.pickzy.moresdk.morecache;
import java.io.ByteArrayInputStream;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {
	Bitmap bitmap;
	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageLoader(Context context,String packagename) {
		fileCache = new FileCache(context,packagename);
		executorService = Executors.newFixedThreadPool(5);
	}
	public void DisplayImage(String url, ImageView imageView,String filename) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(filename);
		if (bitmap != null){
			imageView.setVisibility(0);
			imageView.setImageBitmap(bitmap);
		}
		else {
			queuePhoto(url, imageView,filename);
		}
	}
	private void queuePhoto(String url, ImageView imageView,String filename) {
		PhotoToLoad p = new PhotoToLoad(url, imageView,filename);
		executorService.submit(new PhotosLoader(p));
	}
	public Bitmap getBitmap(String url,String filename) {
		File f = fileCache.getFile(filename);
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn =(HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			ImageViewUtils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public void downloadBitmap(String url,String filename) {
		File f = fileCache.getFile(filename);
		Bitmap b = decodeFile(f);
		if (b == null)
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn =(HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			ImageViewUtils.CopyStream(is, os);
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void writeBitmap(String url,Bitmap bit) {
		File f = fileCache.getFile(url);
		try {
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 bit.compress(Bitmap.CompressFormat.PNG, 100, baos);
			 InputStream is = new ByteArrayInputStream(baos.toByteArray());
			 OutputStream os = new FileOutputStream(f);
			 ImageViewUtils.CopyStream(is, os);
			 os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = 1;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public String filename;
		public PhotoToLoad(String u, ImageView i,String filename) {
			url = u;
			imageView = i;
			this.filename=filename;
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
			Bitmap bmp = getBitmap(photoToLoad.url,photoToLoad.filename);
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
	// Used to display bitmap in the UI thread
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
			photoToLoad.imageView.setImageBitmap(bitmap);
			photoToLoad.imageView.setVisibility(0);
			}
		}
	}
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
	public void clearmemoryCache() {
		memoryCache.clear();

	}
	public void clearfileCache() {
		memoryCache.clear();
	}
}
