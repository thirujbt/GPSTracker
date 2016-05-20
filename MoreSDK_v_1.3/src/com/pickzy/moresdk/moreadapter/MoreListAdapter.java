package com.pickzy.moresdk.moreadapter;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.*;

import com.pickzy.moresdk.MoreAppHits;
import com.pickzy.moresdk.MoreContants;
import com.pickzy.moresdk.morecache.ImageLoader;

public class MoreListAdapter extends BaseAdapter {
	public static String[] image, name, discription, links, appid;
	public static int count;
	private ImageLoader loader;
	private Context context;
	private Bitmap blite, bdark;
	private Drawable lite, dark;

	public MoreListAdapter(Context conte) {
		context = conte;
		loader = new ImageLoader(conte, conte.getPackageName());
		blite = loader.getBitmap(MoreContants.main
				+ "/appimages/MORE_tvc_backgroundLight.png",
				"MORE_tvc_backgroundLight.png");
		bdark = loader.getBitmap(MoreContants.main
				+ "/appimages/MORE_tvc_backgroundDark.png",
				"MORE_tvc_backgroundDark.png");
		lite = new BitmapDrawable(blite);
		dark = new BitmapDrawable(bdark);
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LinearLayout main = new LinearLayout(context);
		if (!name[position].equals("AdBanner1")
				&& !name[position].equals("AdBanner2")) {
			LayoutParams mainparams = new LayoutParams(
					LayoutParams.FILL_PARENT, 90);
			main.setLayoutParams(mainparams);
			// ////////
			LinearLayout appicon = new LinearLayout(context);
			android.widget.LinearLayout.LayoutParams iconparams = new android.widget.LinearLayout.LayoutParams(
					75, 75);
			iconparams.width = 75;
			iconparams.height = 75;
			iconparams.gravity = Gravity.CENTER;
			appicon.setLayoutParams(iconparams);
			appicon.setOrientation(LinearLayout.VERTICAL);
			// //////////
			ImageView icon = new ImageView(context);
			android.widget.LinearLayout.LayoutParams iconp = new android.widget.LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			iconp.gravity = Gravity.CENTER;
			iconp.leftMargin = 5;
			iconp.rightMargin = 5;
			iconp.topMargin = 2;
			iconp.bottomMargin = 2;
			icon.setLayoutParams(iconp);
			loader.DisplayImage(MoreContants.main + "/apps/icons/"
					+ image[position], icon, image[position]);
			// ////////////
			appicon.addView(icon);
			// ///////////// app contents\\\\\\
			LinearLayout appcontent = new LinearLayout(context);
			android.widget.LinearLayout.LayoutParams contentparams = new android.widget.LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 100);
			contentparams.width = LayoutParams.FILL_PARENT;
			contentparams.height = LayoutParams.FILL_PARENT;
			appcontent.setOrientation(LinearLayout.VERTICAL);
			appcontent.setLayoutParams(contentparams);
			// /////////
			TextView appname = new TextView(context);
			android.widget.LinearLayout.LayoutParams appnameparams = new android.widget.LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			appname.setLayoutParams(appnameparams);
			appname.setText(name[position]);
			appname.setTextSize(16);
			appnameparams.leftMargin = 2;
			appname.setMaxLines(1);
			appname.setTextColor(Color.BLACK);
			try {
				SpannableString string = new SpannableString(name[position]);
				string.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						0, (name[position].length()),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				appname.setText(string);
			} catch (Exception e) {
			}
			// ///
			TextView appdiscription = new TextView(context);
			appdiscription.setLayoutParams(appnameparams);
			appdiscription.setText(discription[position]);
			appdiscription.setTextColor(Color.BLACK);
			appdiscription.setMaxLines(2);
			// //////
			appcontent.addView(appname);
			appcontent.addView(appdiscription);
			// ////////\\\\\\\\\\\\\\\
			main.addView(appicon);
			main.addView(appcontent);

			main.setOnClickListener(ItenClickListener(position));
			if (position % 2 == 0) {
				main.setBackgroundColor(Color.DKGRAY);
				try {
					main.setBackgroundDrawable(dark);
				} catch (Exception e) {
				}
			} else {
				main.setBackgroundColor(Color.LTGRAY);
				try {
					main.setBackgroundDrawable(lite);
				} catch (Exception e) {
				}
			}
		} else {
			try {
				//context,appid[position]);
				/*
				LinearLayout layout1 = new LinearLayout(context);
				LayoutParams mainparams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				layout1.setLayoutParams(mainparams);
				com.google.ads.AdView adView1 = new com.google.ads.AdView(
						(Activity) context, com.google.ads.AdSize.BANNER,
						appid[position]);
				layout1.addView(adView1);
				com.google.ads.AdRequest request = new com.google.ads.AdRequest();
				request.setTesting(false);
				adView1.loadAd(request);
				*/
								
				LinearLayout layout1 = new LinearLayout(context);
				LayoutParams mainparams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				layout1.setLayoutParams(mainparams);
				
				com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(context);
			    adView.setAdSize(AdSize.BANNER);
			    adView.setAdUnitId(appid[position]);
			    layout1.addView(adView);
			    AdRequest adRequestInt = new AdRequest.Builder()
			        .build();
			    adView.loadAd(adRequestInt);
				
				main.addView(layout1);
			} catch (Exception e) {
				LayoutParams mainparams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				main.setLayoutParams(mainparams);
			} catch (NoClassDefFoundError err) {
				LayoutParams mainparams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				main.setLayoutParams(mainparams);
			} catch (Error err) {
				LayoutParams mainparams = new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				main.setLayoutParams(mainparams);
			}
			// main.invalidate();
		}
		return main;
	}

	private OnClickListener ItenClickListener(final int position) {
		OnClickListener ItenClickListener = new OnClickListener() {
			public void onClick(View view) {

				try {
					MoreAppHits.appid = appid[position];
					MoreAppHits hits = new MoreAppHits(appid[position]);
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(links[position])));
				} catch (Exception e) {

				}
			}
		};
		return ItenClickListener;
	}
}
