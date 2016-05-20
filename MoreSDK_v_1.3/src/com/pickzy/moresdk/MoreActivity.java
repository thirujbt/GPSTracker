package com.pickzy.moresdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pickzy.moresdk.moreadapter.MoreList2ndAdapter;
import com.pickzy.moresdk.moreadapter.MoreListAdapter;
import com.pickzy.moresdk.morecache.ImageLoader;
import com.pickzy.moresdk.moreviews.MoreLayout;
import com.pickzy.moresdk.moreviews.MoreListView;

public class MoreActivity extends Activity implements OnClickListener{
	private MoreLayout mainlayout,toplayout;
	private Display display;
	private int topbarheight,textsize,linedivider;
	private LinearLayout secondlayout;
	private MoreListView listview, listview2;
	private MoreListAdapter listadapter;
	private MoreList2ndAdapter listadapter2;
	private ImageLoader loader;
	private Drawable selected,unselected;
	private boolean fsel,psel;
	private TextView freetext,paidtext;
	public static int apptype;
	private boolean trigger=true;
	private Context context;
	private ProgressBar spinner;
	public static boolean loadingdata=false;
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	loader=new ImageLoader(this, this.getPackageName());
	display = getWindowManager().getDefaultDisplay();
	int diswidth=display.getWidth();
	int disheight=display.getHeight();
	fsel=true;
	psel=false;
	try{
		context=this;
	}catch(Exception cont){
		
	}
	try{
		spinner=new ProgressBar(this);
		}
		catch(Exception e){
			
		}
	/////////////Height and Width Calculation\\\\\
	topbarheight=(disheight/12);
	textsize=(topbarheight/2);
	linedivider=(topbarheight/10);
	/////////////Main Layout \\\\\\\\\\\\\\\\\\\\\
	mainlayout=new MoreLayout(this,diswidth,disheight);
	LayoutParams mainlayoutparams=(LayoutParams)mainlayout.getLayoutParams();
	mainlayoutparams.width=ViewGroup.LayoutParams.FILL_PARENT;
	mainlayoutparams.height=ViewGroup.LayoutParams.FILL_PARENT;
	mainlayout.setLayoutParams(mainlayoutparams);
	mainlayout.setOrientation(LinearLayout.VERTICAL);
	mainlayout.setBackgroundColor(Color.GRAY);
			////////////1'st Layout , MoreApps\\\\\\\
	toplayout=new MoreLayout(this,diswidth,(20));
	LayoutParams toplayoutparams=(LayoutParams)toplayout.getLayoutParams();
	toplayoutparams.width=ViewGroup.LayoutParams.FILL_PARENT;
	try{
		if(apptype>=3){
			toplayoutparams.height=(int)((topbarheight/1.5));
		}else{
			toplayoutparams.height=(int)((topbarheight/1.15));
		}
	}catch(Exception e){
		toplayoutparams.height=(int) ((topbarheight/1.5));
	}
	
	toplayoutparams.gravity=Gravity.CENTER_VERTICAL;
	toplayout.setLayoutParams(toplayoutparams);
	toplayout.setBackgroundColor(Color.DKGRAY);
	toplayout.setOrientation(LinearLayout.HORIZONTAL);
	
	TextView moreappstext=new TextView(this);
	LayoutParams moreappstextparams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	moreappstextparams.width=LayoutParams.FILL_PARENT;
	moreappstextparams.height=LayoutParams.FILL_PARENT;
	moreappstext.setLayoutParams(moreappstextparams);
	moreappstext.setText("MoreApps");
	moreappstext.setTextColor(Color.WHITE);
	try{
		if(apptype>=3){
			moreappstext.setTextSize((textsize/2));
		}else{
			moreappstext.setTextSize((int)(textsize/1.5));
		}
	}catch(Exception e){
		moreappstext.setTextSize((textsize));
	}
	moreappstext.setGravity(Gravity.CENTER);
	try{
		LayoutParams spinnerparams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		try{
			spinnerparams.width=(int) (topbarheight/1.5);
		}catch(Exception f){
			spinnerparams.width=LayoutParams.WRAP_CONTENT;
		}
		try{
			spinnerparams.height=(int) (topbarheight/1.5);
		}catch(Exception f1){
			spinnerparams.height=LayoutParams.FILL_PARENT;
		}
		
		spinner.setLayoutParams(spinnerparams);
			toplayout.addView(spinner);
			spinneroff();
	}catch(Exception loadi){
		
	}
	toplayout.addView(moreappstext);
			/////////////1'st Layout end\\\\\\\\\\\\\\\\\\\\\\\
			////////////2'nd Layout,MoreApps\\\\\\\
	selected=new BitmapDrawable(loader.getBitmap(MoreContants.main+"/appimages/tabSelect.png", "tabSelect.png"));
	unselected=new BitmapDrawable(loader.getBitmap(MoreContants.main+"/appimages/tabButton.png", "tabButton.png"));
	secondlayout=new LinearLayout(this);
	LayoutParams secondlayoutparams=new LayoutParams(LayoutParams.FILL_PARENT, topbarheight);
	secondlayoutparams.width=ViewGroup.LayoutParams.FILL_PARENT;
	secondlayoutparams.height=topbarheight;
	secondlayoutparams.gravity=Gravity.CENTER_VERTICAL;
	secondlayout.setLayoutParams(secondlayoutparams);
	secondlayout.setOrientation(LinearLayout.HORIZONTAL);

	freetext=new TextView(this);
	LayoutParams freetextparams=new LayoutParams(0, LayoutParams.FILL_PARENT);
	freetextparams.width=0;
	freetextparams.height=LayoutParams.FILL_PARENT;
	freetextparams.weight=1;
	freetext.setLayoutParams(freetextparams);
	freetext.setText("Free");
	freetext.setTextColor(Color.WHITE); 
	freetext.setTextSize(textsize);
	freetext.setGravity(Gravity.CENTER);
	freetext.setOnClickListener(this);
	try{
		freetext.setBackgroundDrawable(selected);
		}catch(Exception e){
			
		}
	
	paidtext=new TextView(this);
	LayoutParams paidtextparams=new LayoutParams(0, LayoutParams.FILL_PARENT);
	paidtextparams.width=0;
	paidtextparams.height=LayoutParams.FILL_PARENT ;
	paidtextparams.weight=1;
	paidtext.setLayoutParams(paidtextparams);
	paidtext.setText("Paid");
	paidtext.setTextColor(Color.WHITE);
	paidtext.setTextSize(textsize);
	paidtext.setGravity(Gravity.CENTER);
	paidtext.setOnClickListener(this);
	try{
		paidtext.setBackgroundDrawable(unselected);
		}catch(Exception e){
		}
//	secondlayout.setBackgroundDrawable(new BitmapDrawable(loader.getBitmap("http://betadev.pickzy.com/appimages/MoreButtonIcon.png", "MoreButtonIcon.png")));
	secondlayout.addView(freetext);
	secondlayout.addView(paidtext);
	/////////////2' nd Layout end\\\\\\\\\\\\\\\\\\\\\\\
	///////////////////listview\\\\\\\\
	listview=new MoreListView(this);
	LayoutParams listparams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	listview.setLayoutParams(listparams);
	listadapter=new MoreListAdapter(this);
	listview.setAdapter(listadapter);
	
	listview2=new MoreListView(this);
	LayoutParams listparams2=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	listview2.setLayoutParams(listparams2);
	listadapter2=new MoreList2ndAdapter(this);
	listview2.setAdapter(listadapter2);
	/////////////////set content view\\\\\\\\
	mainlayout.addView(toplayout);
try{	
	if(apptype>=3){
	mainlayout.addView(secondlayout);
	}
}catch(Exception e){
	
}
	mainlayout.addView(listview);
	try{	
		if(apptype>=3){
			mainlayout.addView(listview2);
			listview2.setVisibility(View.GONE);
		}
	}catch(Exception e){
		
	}
	setContentView(mainlayout);
	TriggerBackground();
	
	
}
private void TriggerBackground(){
	try{
	if(trigger==true){
		trigger=true;
		PZYAppPromoSDK.activitytrigger=true;
	}
	}catch(Exception triggerexcep){
		
	}
}
public void onDestroy(){
	super.onDestroy();
	trigger=false;
	try{
		PZYAppPromoSDK.activitytrigger=false;
	}catch(Exception trigg){
		
	}
}
@Override
public void onClick(View v) {
	if(v==freetext){
		if(fsel==true){
			
		}else{
			freetext.setBackgroundDrawable(selected);
			paidtext.setBackgroundDrawable(unselected);
			listview2.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			fsel=true;
			psel=false;
		}
	}else if(v==paidtext){
		if(psel==true){
			
		}else{
			listview.setVisibility(View.GONE);
			listview2.setVisibility(View.VISIBLE);
			freetext.setBackgroundDrawable(unselected);
			paidtext.setBackgroundDrawable(selected);
			fsel=false;
			psel=true;
		}
	}
}
private boolean isInternetOn(Context context1) {
	try{
	ConnectivityManager connec = (ConnectivityManager) context1
			.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
		return true;
	} 
	else 
	{
		return false;
	}
	}catch(Exception e){
		
	}
	return false;
}
	private void spinneroff(){
	if(loadingdata==false){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			public void run() {
				spinneroff();
				try{
				spinner.setVisibility(8);
				}catch(Exception visi){
					
				}
			}
		},2000);
	}else{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			public void run() {
				spinneroff();
			}
		},2000);
	}
	}
}
