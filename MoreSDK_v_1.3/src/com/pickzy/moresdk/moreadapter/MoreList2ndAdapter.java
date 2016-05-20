package com.pickzy.moresdk.moreadapter;


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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pickzy.moresdk.MoreAppHits;
import com.pickzy.moresdk.MoreContants;
import com.pickzy.moresdk.morecache.ImageLoader;


public class MoreList2ndAdapter extends BaseAdapter {
public static String[] image,name,discription,links,appid;
public static int count=0;
private ImageLoader loader;
private Context context;
private Bitmap blite,bdark;
private Drawable lite,dark;

	public MoreList2ndAdapter(Context conte){
		context=conte;
		loader=new ImageLoader(conte, conte.getPackageName());
		blite=loader.getBitmap(MoreContants.main+"/appimages/MORE_tvc_backgroundLight.png", "MORE_tvc_backgroundLight.png");
		bdark=loader.getBitmap(MoreContants.main+"/appimages/MORE_tvc_backgroundDark.png", "MORE_tvc_backgroundDark.png");
		lite=new BitmapDrawable(blite);
		dark=new BitmapDrawable(bdark);
	}
	@Override
	public int getCount() {
		Log.e("Count", ""+count);
	
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
		LinearLayout main=new LinearLayout(context);
		LayoutParams mainparams = new LayoutParams(LayoutParams.FILL_PARENT,90);
		main.setLayoutParams(mainparams);
		//////////
		LinearLayout appicon=new LinearLayout(context);
		android.widget.LinearLayout.LayoutParams iconparams = new android.widget.LinearLayout.LayoutParams(75,75);
		iconparams.width=85;
		iconparams.height=LayoutParams.FILL_PARENT;
		appicon.setLayoutParams(iconparams);
		appicon.setOrientation(LinearLayout.VERTICAL);
		////////////
		ImageView icon=new ImageView(context);
		android.widget.LinearLayout.LayoutParams iconp = new android.widget.LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		iconp.gravity=Gravity.CENTER;
		iconp.leftMargin=5;
		iconp.rightMargin=5;
		iconp.topMargin=2;
		iconp.bottomMargin=2;
		icon.setLayoutParams(iconp);
		loader.DisplayImage(MoreContants.main+"/apps/icons/"+image[position], icon,image[position]);
		//////////////
		appicon.addView(icon);
		/////////////// app contents\\\\\\
		LinearLayout appcontent=new LinearLayout(context);
		android.widget.LinearLayout.LayoutParams contentparams = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,100);
		contentparams.width=LayoutParams.FILL_PARENT;
		contentparams.height=LayoutParams.FILL_PARENT;
		appcontent.setOrientation(LinearLayout.VERTICAL);
		appcontent.setLayoutParams(contentparams);
				///////////
		TextView appname=new TextView(context);
		android.widget.LinearLayout.LayoutParams appnameparams = new android.widget.LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		appname.setLayoutParams(appnameparams);
		appname.setText(name[position]);
		appname.setTextSize(16);
		appnameparams.leftMargin=2;
		appname.setMaxLines(1);
		appname.setTextColor(Color.BLACK);
		try{
			SpannableString string=new SpannableString (name[position]);
			string.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, (name[position].length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			appname.setText(string);
			}catch(Exception e){
				
			}
					/////
		TextView appdiscription=new TextView(context);
		appdiscription.setLayoutParams(appnameparams);
		appdiscription.setText(discription[position]);
		appdiscription.setTextColor(Color.BLACK);
		appdiscription.setMaxLines(2);
		////////
		appcontent.addView(appname);
		appcontent.addView(appdiscription);
		//////////\\\\\\\\\\\\\\\
		main.addView(appicon);
		main.addView(appcontent);
		Log.e("position", ""+position);
		try{
		main.setOnClickListener(ItenClickListener(position));
		}catch(Exception e){
			
		}
		if(position%2==0){
			Log.e("even", "even");
			Log.e("even", "even");
			main.setBackgroundColor(Color.DKGRAY);
			main.setBackgroundDrawable(dark);
		}else{
			Log.e("odd", "odd");
			Log.e("odd", "odd");
			main.setBackgroundColor(Color.LTGRAY);
			main.setBackgroundDrawable(lite);
		}
		return main;
	}
	private OnClickListener ItenClickListener(final int position) {
		OnClickListener ItenClickListener = new OnClickListener() {
			public void onClick(View view) {
				MoreAppHits.appid=appid[position];
				try{
				MoreAppHits hits=new MoreAppHits(appid[position]);
				context.startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse(links[position])));
				}catch(Exception e){
					
				}
			}
		};
		return ItenClickListener;
	}
}
