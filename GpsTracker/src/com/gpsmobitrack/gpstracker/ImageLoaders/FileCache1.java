package com.gpsmobitrack.gpstracker.ImageLoaders;

import java.io.File;

import android.content.Context;

public class FileCache1 {
	private File cacheDir;

	public FileCache1(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))

			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "GPS");

		else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
	//	Log.e("File Name ====>", ""+f);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

}

