package com.pickzy.moresdk;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pickzy.moresdk.moreadapter.MoreListAdapter;
import com.pickzy.moresdk.morecache.ImageLoader;
import com.pickzy.moresdk.moredb.DB_Pojo;
import com.pickzy.moresdk.moredb.DatabaseHandler;
import com.pickzy.moresdk.moredb.Nag_DB_Pojo;
import com.pickzy.moresdk.moredb.Nag_DatabaseHandler;
import com.pickzy.moresdk.moreviews.MoreLayout;
import com.pickzy.moresdk.moreviews.MoreTextView;

public class PZYAppPromoSDK {
	private XMLParser parser;
	private String xml;
	private Document doc;
	private NodeList nodelist,nodelist2;
	private int elementcount,elementcount2;
	private Context cont;
	public static String[] keys=null, strings=null;
	public static String[] passimage=null,passname=null,passdiscription=null,passlink=null,passstring=null,passappid=null;
	private int plistcount=0,nagplistcount=0;
	private DatabaseHandler db;
	private Nag_DatabaseHandler ndb;
	private String key="",packagename="";
	public static  boolean running=false;
	public boolean dbrunning=false;
	private static String[] nagkey=null,nagstring=null;
	public static String name="",link="",caption="",description="",picture="";
	private static int subscriptiontype,timestamp,locationenabled,moreappslisttype=1,currentlisttype;
	private static int calculator=0,calc=0;
	private static int appicon,appname,appid,appdiscription,applink;
	private ImageLoader loader;
	public static int appcount=0;
	private boolean appcountri=false;
	public static boolean activitytrigger=false;
	public static boolean startingactivity=false;
	private boolean localrunning=false;
    public static Dialog dialog;
    private ProgressBar spinner;
    private boolean nagshow=false;
    private Context nagcontext;
    private Dialog nagdialog;
    
	public PZYAppPromoSDK(Context context,String key,String packagename){
		cont=context; 
		appcounttrigger();
		try{
		int density= context.getResources().getDisplayMetrics().densityDpi;
		switch(density)
		{
		case DisplayMetrics.DENSITY_LOW:
//		   Toast.makeText(context, "LDPI", Toast.LENGTH_SHORT).show();
		    break;
		case DisplayMetrics.DENSITY_MEDIUM:
//		     Toast.makeText(context, "MDPI", Toast.LENGTH_SHORT).show();
		    break;
		case DisplayMetrics.DENSITY_HIGH:
//		    Toast.makeText(context, "HDPI", Toast.LENGTH_SHORT).show();
		    break;
		default :
//			Toast.makeText(context, "default", Toast.LENGTH_SHORT).show();
			break;
		}
		}catch(Exception densityException){
			
		}
		try{
			File buttondir=new File(android.os.Environment.getExternalStorageDirectory(),".pZyMore/"+cont.getPackageName());
			File bt1=new File(buttondir, "tabButton.png");
			File bt2=new File(buttondir, "tabSelect.png");
			File bt3=new File(buttondir, "MORE_tvc_backgroundLight.png");
			File bt4=new File(buttondir, "MORE_tvc_backgroundDark.png");
			File bt5=new File(buttondir, "btnplain.png");
			if(!bt1.exists()||!bt2.exists()||!bt3.exists()||!bt4.exists()||!bt5.exists()){
				DownloadImages download=new DownloadImages(context);
			}else{
			}
		}catch(Exception fileexception){
	DownloadImages download=new DownloadImages(context);
		}
		parser = new XMLParser();
		db=new DatabaseHandler(cont);
		ndb=new Nag_DatabaseHandler(cont);
		loader=new ImageLoader(cont, cont.getPackageName());
		this.key=key;
		MoreAppHits.key=key;
		MoreContants.key=key;
		NagHits.key=key;
		NagHits.packagename=packagename;
		this.packagename=packagename;
		
		MoreContants.packagename=packagename;
		running=false;
		dbrunning=false;
		try{
			plistcount=db.getAllContacts();
			try{
			if(plistcount>=1){
			int invcount=((plistcount-5)/5);
			timestamp=db.gettimestamp();
			appcount=invcount;
			}
			}catch(Exception j){
				
			}
			
		}catch(Exception e){
		}
		try{
			nagplistcount=ndb.getAllContacts();
			
		}catch(Exception e){
		}
		if (isInternetOn(context)){
			new MoreAppsData().execute("pickzy");
		} else {
			try{
				if(plistcount>=1){
					GetFreeValuesFromDatabase();
				}else{
					waitthread();
				}
			}catch(Exception e){
				waitthread();
			}
		}
		if (isInternetOn(cont)) {
			new NagScreen().execute("");
			}else{
				if(nagplistcount>=1){
					GetPaidValuesFromDatabase();
				}
			}
//		Activitytrigger();
		StartingActivity();
		try{
			spinner=new ProgressBar(cont);
			dialog=new Dialog(cont);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			try{
			spinner.setBackgroundColor(Color.LTGRAY);
			}catch(Exception color){
			}
			dialog.setContentView(spinner);
			dialog.setCancelable(true);
			}
			catch(Exception e){
				
			}
	}
	public void databaseexecuter(){
		try{
			plistcount=db.getAllContacts();
			try{
			if(plistcount>=1){
			int invcount=((plistcount-5)/5);
			appcount=invcount;
			}
			}catch(Exception j){
			}
			
		}catch(Exception e){
		}
		if(plistcount>=1){
			GetFreeValuesFromDatabase();
		}else{
			waitthread();
		}
	}
	public PZYAppPromoSDK() {

	}
	private void waitthread(){
		try{
		if (isInternetOn(cont)) {
			new MoreAppsData().execute("pickzy");
		}else{
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					waitthread();
				}
			}, 5000);
		}
		}catch(Exception e){
			waitthread();
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
	//Async Task //Api-3
	public class MoreAppsData extends AsyncTask<String, Integer, String> { //Api -3 //AsyncTask
		protected String doInBackground(String... params){
			try {
				if(!key.equals("")&&!packagename.equals("")){
				xml = parser.getXmlFromUrl(MoreContants.plist+"&usrlickey="+key+"&timestamp="+timestamp+"&appid="+packagename+"&imp=1&hit=1");
				if(xml!=null){
					try{
					}catch(Exception xmlexc){
					}
				running=true;
				doc=parser.getDomElement(xml);
				if(doc!=null){
				}else{
				}
				nodelist=doc.getElementsByTagName("key");
				elementcount=nodelist.getLength();
				nodelist2=doc.getElementsByTagName("string");
				elementcount2=nodelist2.getLength();
				keys=new String[elementcount];
				strings=new String[elementcount2];
				for (int i = 0; i <elementcount; i++) {
					Element e = (Element) nodelist.item(i);
					keys[i] =  parser.getElementValue(e).trim();
				}
				for (int i = 0; i <elementcount2; i++) {
					Element e = (Element) nodelist2.item(i);
					try{
					strings[i] =  parser.getElementValue(e).trim();
					}catch(Exception e2){
						strings[i] ="0";
					}
				}
				keynodeposition();
			}else{
				running=false;
				 waitthread();
			}
				}else{
					Toast.makeText(cont, "Please Enter AppId and PackageName", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				running=false;
			}
			
			return null;
		}
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	private  void deletedb(){
		try{
			db.DeleteRow("1");
			}catch(Exception e3){
			}
	}
	public class NagScreen extends AsyncTask<String, Integer, String> { //Api -3 //AsyncTask
		protected String doInBackground(String... params){
			try {
				if(!key.equals("")&&!packagename.equals("")){	
				xml = parser.getXmlFromUrl(MoreContants.nag+"&usrlickey="+key+"&appid="+packagename+"&imp=1&hit=1");
			if(xml!=null){
				Document Nagdoc=parser.getDomElement(xml);
				if(Nagdoc!=null){
				}else{
				}
				NodeList Nagnodelist=Nagdoc.getElementsByTagName("key");
				int nagelementcount=Nagnodelist.getLength();
				NodeList Nagnodelist2=Nagdoc.getElementsByTagName("string");
				int nagelementcount2=Nagnodelist2.getLength();
				nagkey=new String[nagelementcount];
				nagstring=new String[nagelementcount2];
				for (int i = 0; i <nagelementcount; i++) {
					Element e = (Element) Nagnodelist.item(i);
					nagkey[i] =  parser.getElementValue(e).trim();
				}
				for (int i = 0; i <nagelementcount2; i++) {
					Element e = (Element) Nagnodelist2.item(i);
					nagstring[i] =  parser.getElementValue(e).trim();
				}
				ndb.DeleteRow("1");
				WriteNagDB();
			}else{
				
			}
				}else{
					Toast.makeText(cont, "Please Enter AppId and PackageName", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				
			}
			
			return null;
		}
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	public void keynodeposition(){
		try{
		deletedb();
		}catch(Exception f){
			
		}
		int imagelink=0;
		int j=0;
		appicon=0;
		boolean iconboo=false;
		appname=0;
		boolean nameboo=false;
		appid=0;
		boolean appidboo=false;
		appdiscription=0;
		boolean disboo=false;
		applink=0;
		boolean linkboo=false;
		for(int i=0;i<elementcount;i++){
			if(keys[i].equals("ImageLinks")){
				imagelink=1;
				j=0;
			}
			if(imagelink==1){
				if(keys[i].equals("Filename")){
				if(appicon==0 && iconboo==false){
					appicon=j;
					iconboo=true;
				}
				}else if(keys[i].equalsIgnoreCase("AppId")){
					if(appid==0 && appidboo==false){
						appid=j;
						appidboo=true;
					}	
				}
				else if(keys[i].equals("Filechange")){
					
				}
				else if(keys[i].equals("Name")){
					if(appname==0 && nameboo==false){
						appname=j;
						nameboo=true;
					}	
				}else if(keys[i].equals("Description")){
					if(appdiscription==0 && disboo==false){
						appdiscription=j;
						disboo=true;
					}
				}else if(keys[i].equals("Link")){
					if(applink==0 && linkboo==false){
						applink=j;
						linkboo=true;
					}
				}
			}
			j=j+1;
			}
		applistdetails();
	}
	private void applistdetails(){
		int count=0;
		int j=0;
		for(int i=0;i<elementcount;i++){
			if(keys[i].equals("SubcriptionType")){
				try{
				subscriptiontype=Integer.parseInt(strings[i]);
			}catch(Exception e){
				subscriptiontype=0;
			}
			}else if(keys[i].equals("LocationEnabled")){
				try{
				locationenabled=Integer.parseInt(strings[i]);
			}catch(Exception e){
				locationenabled=0;
			}
			}
			else if(keys[i].equals("MoreAppsListType")){
				try{
				moreappslisttype=Integer.parseInt(strings[i]);
			}catch(Exception e){
				moreappslisttype=0;
			}
			}
			else if(keys[i].equals("TimeStamp")){
				try{
					timestamp=Integer.parseInt(strings[i]);
				}catch(Exception e){
					timestamp=0;
				}
			}
			else if(keys[i].equals("CurrentListType")){
				try{
				currentlisttype=Integer.parseInt(strings[i]);
				}catch(Exception e){
					currentlisttype=0;
				}
			}
			if(keys[i].equals("Filename")){
				if(count<=1){
					calc=j;
					j=0;
					count=count+1;
				}
				else if(count>=2){
					break;
				}
			}
			j=j+1;
		}
		calculator=0;
		boolean calculated=false;
		for(int i=0;i<elementcount2;){
			if(strings[i].endsWith(".png")){
				calculated=true;
				break;
			}else{
				if(calculated==false){
				calculator=calculator+1;
				i=i+1;
				}
			}
		}
//		moreappslisttype=3;
		if(moreappslisttype>=3){
			MoreSecond getpaid=new MoreSecond(cont,key,packagename);
		}
		passvalues();
	}
	private void passvalues(){
		int passstringcount=elementcount2-calculator;
		Log.e("calculator", ""+calculator);
		Log.e("passstringcount", ""+passstringcount);
		int passkeycount=(passstringcount/6);
		Log.e("passkeycount", ""+passkeycount);
		appcount=passkeycount;
		passname=new String[passkeycount];
		passappid=new String[passkeycount];
		passimage=new String[passkeycount];
		passdiscription=new String[passkeycount];
		passlink=new String[passkeycount];
		passstring=new String[passstringcount];
		for(int i=0;i<passstringcount;i++){
			passstring[i]=strings[i+(calculator)];
		}
		int j=0;
		for(int i=appicon-1;i<passstringcount;){
			passimage[j]=passstring[i];
			i=i+calc;
			j+=1;
		}
		int k=0;
		for(int i=appname-1;i<passstringcount;){
			passname[k]=passstring[i];
			i=i+calc;
			k+=1;
		}
		int l=0;
		for(int i=appdiscription-1;i<passstringcount;){
			passdiscription[l]=passstring[i];
			i=i+calc;
			l+=1;
		}
		int m=0;
		for(int i=applink-1;i<passstringcount;){
			passlink[m]=passstring[i];
			i=i+calc;
			m+=1;
		}
		int n=0;
		for(int i=appid-1;i<passstringcount;){
			passappid[n]=passstring[i];
			i=i+calc;
			n+=1;
		}
		if (isInternetOn(cont)){
			try{
				if(appcountri==false){
				// appcounttrigger();
//				Activitytrigger();
				}
				}catch(Exception e4){
					
				}
		}
//		int adddetecter=0;
//		try{
//		for(int i=0;i<passname.length;i++){
//			if(passname[i].equals("AdBanner1")||passname[i].equals("AdBanner2")){
//				adddetecter=adddetecter+1;
//				}
//		}
//		if(adddetecter>=0){
//			String []pass_image=new String[(passname.length)-adddetecter];
//			String []pass_name=new String[(passname.length)-adddetecter];
//			String []pass_discription=new String[(passname.length)-adddetecter];
//			String []pass_link=new String[(passname.length)-adddetecter];
//			String []pass_appid=new String[(passname.length)-adddetecter];
//			int kl=0;
//			int positioncheck1=0,positioncheck2=0;
//			boolean pcheck1=false,pcheck2=false;
//			for(int i=0;i<((passname.length));i++){
//				if(!passname[i].equals("AdBanner1")&&!passname[i].equals("AdBanner2")){
//					pass_name[kl]=passname[i];
//					kl=kl+1;
//				}else{
//					if(positioncheck1==0 && pcheck1==false){
//						positioncheck1=i;
//						pcheck1=true;
//					}else{
//						positioncheck2=i;
//						pcheck2=true;
//					}
//				}
//			}
//			kl=0;
//			for(int i=0;i<passname.length;i++){
//				if(i==positioncheck1 && pcheck1==true ||i==positioncheck2 && pcheck2==true){
//					
//				}else{
//					pass_image[kl]=passimage[i];
//					pass_discription[kl]=passdiscription[i];
//					pass_link[kl]=passlink[i];
//					pass_appid[kl]=passappid[i];
//					kl=kl+1;
//				}
//			}
//			MoreListAdapter.image=pass_image;
//			MoreListAdapter.name=pass_name;
//			MoreListAdapter.discription=pass_discription;
//			MoreListAdapter.links=pass_link;
//			MoreListAdapter.appid=pass_appid;
//			MoreListAdapter.count=pass_name.length;
//			MoreActivity.apptype=moreappslisttype;
//			MoreActivity.loadingdata=false;	
//			
//			pass_image=null;
//			pass_name=null;
//			pass_discription=null;
//			pass_link=null;
//			pass_appid=null;
//			
//		}else{
//			MoreListAdapter.image=passimage;
//			MoreListAdapter.name=passname;
//			MoreListAdapter.discription=passdiscription;
//			MoreListAdapter.links=passlink;
//			MoreListAdapter.count= passkeycount;
//			MoreListAdapter.appid=passappid;
//			MoreActivity.apptype=moreappslisttype;
//			MoreActivity.loadingdata=false;
//		}
//		}catch(Exception adddetector){
//			MoreListAdapter.image=passimage;
//			MoreListAdapter.name=passname;
//			MoreListAdapter.discription=passdiscription;
//			MoreListAdapter.links=passlink;
//			MoreListAdapter.count=passkeycount;
//			MoreListAdapter.appid=passappid;
//			MoreActivity.apptype=moreappslisttype;
//			MoreActivity.loadingdata=false;
//		}
		MoreListAdapter.image=passimage;
		MoreListAdapter.name=passname;
		MoreListAdapter.discription=passdiscription;
		MoreListAdapter.links=passlink;
		MoreListAdapter.count=passkeycount;
		MoreListAdapter.appid=passappid;
		MoreActivity.apptype=moreappslisttype;
		MoreActivity.loadingdata=false;
		if(localrunning==true){
			dialog.dismiss();
			localrunning=false;
			Intent moreactivity=new Intent(cont.getApplicationContext(),MoreActivity.class);
			cont.startActivity(moreactivity);
		}
		WriteDB();
	}
	private void WriteDB(){
		try{
		int dbcount2=db.getContactsCount();
		int k=0;
		try{
		 k=((passimage.length*5)+6);
		}catch(Exception g){
			
		}
		if(dbcount2>=k){
			db.DeleteRow("1");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable(){
				public void run(){
					WriteDB();	
				}
				},3000);
		}else{
			for(int i=0;i<passimage.length;i++){
					db.addContact(new DB_Pojo( passimage[i]));
			}
			for(int i=0;i<passname.length;i++){
				db.addContact(new DB_Pojo( passname[i]));
			}
			for(int i=0;i<passdiscription.length;i++){
				db.addContact(new DB_Pojo( passdiscription[i]));
			}
			for(int i=0;i<passlink.length;i++){
				db.addContact(new DB_Pojo( passlink[i]));
			}
			for(int i=0;i<passappid.length;i++){
				db.addContact(new DB_Pojo( passappid[i]));
			}
			db.addContact(new DB_Pojo(""+subscriptiontype ));
			db.addContact(new DB_Pojo(""+locationenabled ));
			db.addContact(new DB_Pojo( ""+moreappslisttype));
			db.addContact(new DB_Pojo( ""+currentlisttype));
			db.addContact(new DB_Pojo( ""+timestamp));
			try{
			int dbtotal=((passimage.length*5)+5);
			int dbcount=db.getContactsCount();
			if(dbtotal<dbcount){
				try{
					deletedb();
					}catch(Exception f){
						
					}
				WriteDB();
			}
			}catch(Exception e){
				
			}
			
		}
		}catch(Exception f){
			WriteDB();
		}
		try{
			if(MoreButton.processindicator==true){
				MoreButton.stopprocess();
			}
		}catch(Exception process){
			
		}
	}
	private void GetFreeValuesFromDatabase(){
		dbrunning=true;
		int count=db.getAllContacts();
		strings=DatabaseHandler.Strings;
		int indi=((count-5)/5);
		appcount=indi;
		int j=0;
		for(int i=0;i<indi;i++){
			passimage[j]=strings[i];
			j+=1;
		}
		int k=0;
		for(int i=indi;i<(indi*2);i++){
			passname[k]=strings[i];
			k+=1;
		}
		int l=0;
		for(int i=(indi+indi);i<(indi*3);i++){
			passdiscription[l]=strings[i];
			l+=1;
		}
		int m=0;
		for(int i=(indi+indi+indi);i<(indi*4);i++){
			passlink[m]=strings[i];
			m+=1;
		}
		int n=0;
		for(int i=(indi*4);i<(indi*5);i++){
			passappid[n]=strings[i];
			n+=1;
		}
		subscriptiontype=Integer.parseInt(strings[count-4]);
		locationenabled=Integer.parseInt(strings[count-3]);
		moreappslisttype=Integer.parseInt(strings[count-2]);
		currentlisttype=Integer.parseInt(strings[count-1]);
		timestamp=Integer.parseInt(strings[count-1]);
		int adddetecter=0;
		try{
		for(int i=0;i<passname.length;i++){
			if(passname[i].equals("AdBanner1")||passname[i].equals("AdBanner2")){
				adddetecter=adddetecter+1;
				}
		}
		if(adddetecter>=0){
			String []pass_image=new String[(passname.length)-adddetecter];
			String []pass_name=new String[(passname.length)-adddetecter];
			String []pass_discription=new String[(passname.length)-adddetecter];
			String []pass_link=new String[(passname.length)-adddetecter];
			String []pass_appid=new String[(passname.length)-adddetecter];
			int kl=0;
			int positioncheck1=0,positioncheck2=0;
			boolean pcheck1=false,pcheck2=false;
			for(int i=0;i<((passname.length));i++){
				if(!passname[i].equals("AdBanner1")&&!passname[i].equals("AdBanner2")){
					pass_name[kl]=passname[i];
					kl=kl+1;
				}else{
					if(positioncheck1==0 && pcheck1==false){
						positioncheck1=i;
						pcheck1=true;
					}else{
						positioncheck2=i;
						pcheck2=true;
					}
				}
			}
			kl=0;
			for(int i=0;i<passname.length;i++){
				if(i==positioncheck1 && pcheck1==true ||i==positioncheck2 && pcheck2==true){
					
				}else{
					pass_image[kl]=passimage[i];
					pass_discription[kl]=passdiscription[i];
					pass_link[kl]=passlink[i];
					pass_appid[kl]=passappid[i];
					kl=kl+1;
				}
			}
			MoreListAdapter.image=pass_image;
			MoreListAdapter.name=pass_name;
			MoreListAdapter.discription=pass_discription;
			MoreListAdapter.links=pass_link;
			MoreListAdapter.appid=pass_appid;
			MoreListAdapter.count=pass_name.length;
			MoreActivity.apptype=moreappslisttype;
			MoreActivity.loadingdata=false;	
			
			pass_image=null;
			pass_name=null;
			pass_discription=null;
			pass_link=null;
			pass_appid=null;
			
		}else{
			MoreListAdapter.image=passimage;
			MoreListAdapter.name=passname;
			MoreListAdapter.discription=passdiscription;
			MoreListAdapter.links=passlink;
			MoreListAdapter.count= indi;
			MoreListAdapter.appid=passappid;
			MoreActivity.apptype=moreappslisttype;
			MoreActivity.loadingdata=false;
		}
		}catch(Exception adddetector){
			MoreListAdapter.image=passimage;
			MoreListAdapter.name=passname;
			MoreListAdapter.discription=passdiscription;
			MoreListAdapter.links=passlink;
			MoreListAdapter.count=indi;
			MoreListAdapter.appid=passappid;
			MoreActivity.apptype=moreappslisttype;
			MoreActivity.loadingdata=false;
		}
		if(moreappslisttype>=3){
			MoreSecond getpaid=new MoreSecond(cont,key,packagename);
		}
		if(nagplistcount>=1){
			GetPaidValuesFromDatabase();
		}
		db.close();
		try{
			appcounttrigger();
		}catch(Exception e){
			
		}
		try{
			if(MoreButton.processindicator==true){
				MoreButton.stopprocess();
			}
			if(localrunning==true){
				dialog.dismiss();
				localrunning=false;
				Intent moreactivity=new Intent(cont.getApplicationContext(),MoreActivity.class);
				cont.startActivity(moreactivity);
			}
		}catch(Exception process){
			
		}
	}
	private void WriteNagDB(){
		for(int i=0;i<nagstring.length;i++){
			ndb.addContact(new Nag_DB_Pojo(nagstring[i] ));
			Log.e("Writing nag db", "writing nag db");
		}
		try
		{
			Log.e("try calling showNagScreen ", "calling showNagScreen");
			if(nagshow==true){
				Log.e("calling showNagScreen ", "calling showNagScreen");
				showNagScreen(nagcontext);
				Log.e("calling showNagScreen ", "calling showNagScreen");
				}else{
				}
		}catch(Exception f){
			Log.e("Exception", f.getMessage());
		}
		ndb.close();
	}
	private void GetPaidValuesFromDatabase(){
		nagstring=Nag_DatabaseHandler.Strings;
		try{
			ndb.close();
			if(nagshow==true){
				showNagScreen(nagcontext);
				}
			}catch(Exception f){
				
			}
	}
	public void showMoreApps(){
		try{
		if(passimage!=null){
		Intent moreactivity=new Intent(cont.getApplicationContext(),MoreActivity.class);
		cont.startActivity(moreactivity);
		}else{
			if(	running==false){
			if (isInternetOn(cont)) {
				dialog.show();
				localrunning=true;
				new MoreAppsData().execute("pickzy");
			} else {
				try{
				if(plistcount>=1){
					if(dbrunning==false){
						GetFreeValuesFromDatabase();
					}
				}else{
					waitthread();
				}
				}catch(Exception e){
					waitthread();
				}
			}
		}
		}
		}catch(Exception e){
			
		}
	}
	public void showNagScreen( Context cont1){
		nagshow=true;
		Log.e("showNagScreen", "setting true ");
		nagdialog=new Dialog(cont1);
		try{
		if(nagstring!=null){
			Log.e("showNagScreen", "nagstring not null ");
		MoreLayout mainnaglayout=new MoreLayout(cont1,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		LayoutParams params10=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params10.width=LayoutParams.FILL_PARENT;
		params10.height=LayoutParams.FILL_PARENT;
		mainnaglayout.setLayoutParams(params10);
		mainnaglayout.setOrientation(LinearLayout.VERTICAL);
		///
		MoreLayout mainnagtitle=new MoreLayout(cont1,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		LayoutParams params=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.width=450;
		params.gravity=Gravity.CENTER;
		params.rightMargin=10;
		params.leftMargin=10;
		///
		TextView nagtitle=new TextView(cont1);
		nagtitle.setLayoutParams(params);
		nagtitle.setText(nagstring[0]);
		nagtitle.setTextSize(18);
		nagtitle.setTextColor(Color.BLACK);
		nagtitle.setGravity(Gravity.CENTER);
		try{
		SpannableString string=new SpannableString (nagstring[0]);
		string.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, (nagstring[0].length()),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		nagtitle.setText(string);
		mainnagtitle.addView(nagtitle);
		}catch(Exception e){
			
		}
		//Title End
		TextView nagcontent=new TextView(cont1);
		LayoutParams params2=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params2.gravity=Gravity.CENTER;
		params2.topMargin=10;
		nagcontent.setLayoutParams(params2);
		nagcontent.setText(nagstring[1]);
		nagcontent.setTextSize(15);
		nagcontent.setTextColor(Color.WHITE);
		nagcontent.setGravity(Gravity.CENTER);
		//Content end
		LayoutParams params3=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		LinearLayout buttonlayout=new LinearLayout(cont1);
		params3.gravity=Gravity.CENTER;
		params3.topMargin=10;
		params3.bottomMargin=10;
		buttonlayout.setLayoutParams(params3);
		buttonlayout.setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutParams params4=new LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params4.width=0;
		params4.height=LayoutParams.WRAP_CONTENT;
		params4.weight=1;
		params4.rightMargin=10;
		params4.leftMargin=10;
		MoreLayout cancellayout=new MoreLayout(cont1,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		cancellayout.setLayoutParams(params4);
		LayoutParams cancellayoutparams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		cancellayoutparams.gravity=Gravity.CENTER;
		///
		TextView cancelbutton=new TextView(cont1);
		cancelbutton.setLayoutParams(cancellayoutparams);
		cancelbutton.setText(nagstring[3]);
		cancelbutton.setTextSize(18);
		cancelbutton.setTextColor(Color.WHITE);
		cancelbutton.setGravity(Gravity.CENTER);
		cancellayout.addView(cancelbutton);
		cancellayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				nagdialog.dismiss();
			}
		});
		cancellayout.setBackgroundDrawable(new BitmapDrawable(loader.getBitmap(MoreContants.appimages+"btnPlain.png", "btnplain.png")));
		MoreLayout oklayout=new MoreLayout(cont1,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		oklayout.setLayoutParams(params4);
		LayoutParams oklayoutparams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		oklayoutparams.gravity=Gravity.CENTER;
		//
		TextView okbutton=new TextView(cont1);
		okbutton.setLayoutParams(oklayoutparams);
		okbutton.setText(nagstring[4]);
		okbutton.setTextSize(18);
		okbutton.setTextColor(Color.WHITE);
		okbutton.setGravity(Gravity.CENTER);
		oklayout.addView(okbutton);
		oklayout.setBackgroundDrawable(new BitmapDrawable(loader.getBitmap(MoreContants.appimages+"btnPlain.png", "btnplain.png")));
		oklayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				nagdialog.dismiss();
				if (isInternetOn(cont)) {
					NagHits naghit=new NagHits();
					cont.startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse(nagstring[2])));
				}
			}
		});
		
		buttonlayout.addView(cancellayout);
		buttonlayout.addView(oklayout);
		//End
		mainnaglayout.addView(mainnagtitle);
		mainnaglayout.addView(nagcontent);
		mainnaglayout.addView(buttonlayout);
		mainnaglayout.setBackgroundColor(Color.GRAY);
		nagdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nagdialog.setContentView(mainnaglayout);
		nagdialog.setCancelable(false);
		nagdialog.show();
		nagshow=false;
		}else{
			nagcontext=cont1;
			nagshow=true;
			Log.e("showNagScreen", "nag null ");
			if (isInternetOn(cont)) {
			new NagScreen().execute("");
			}else{
				nagplistcount=ndb.getAllContacts();
				if(nagplistcount>=1){
					GetPaidValuesFromDatabase();
				}
			}
		}
		}catch(Exception e){
			Log.e("Exception", "Nag catch "+e.getMessage());
		}
	}
	public void showFullBannerAdd(){
		
	}
	public void facebookpublish( Activity a){
		try{
			if(name.equals("")){
				Toast.makeText(a,"Developer Error :Enter Name To Publish", Toast.LENGTH_SHORT).show();
			}
			else if(link.equals("")){
				Toast.makeText(a, "Developer Error :Enter link To Publish", Toast.LENGTH_SHORT).show();
			}
			else if(caption.equals("")){
				Toast.makeText(a, "Developer Error :Enter caption To Publish", Toast.LENGTH_SHORT).show();
			}
			else if(description.equals("")){
				Toast.makeText(a, "Developer Error :Enter description To Publish", Toast.LENGTH_SHORT).show();
			}
			else if(picture.equals("")){
				Toast.makeText(a, "Developer Error :Enter picture To Publish",Toast.LENGTH_SHORT).show();
			}
			else{
				try {
//					MoreSocial.name=name;
//					MoreSocial.link=link;
//					MoreSocial.description=description;
//					MoreSocial.caption=caption;
//					MoreSocial.picture=picture;
//					MoreSocial.publishStory(a);
				} catch (Exception e) {
				}
			}
		}catch(Exception e){
			Toast.makeText(a, "Null error", Toast.LENGTH_SHORT).show();
		}
	}
	private void appcounttrigger(){
		appcountri=true;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if(isInternetOn(cont)){
				new background().execute("pickzy");
				}
			}
		},5000);
	}
//	private void Activitytrigger(){
//		Log.e("Activity 1", "Activity Trigger");
//		if(activitytrigger==true){
//			Log.e("Activity true", "Activity Trigger");
//		Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			public void run() {
//				Log.e("Activity 2", "Activity Trigger");
//				if(isInternetOn(cont)){
//					Log.e("Activity 3", "Activity Trigger");
//					new MoreAppsData().execute("pickzy");
//				}
//				Activitytrigger();
//			}
//		},3000);
//		}else{
//			Log.e("Activity false", "Activity Trigger");
//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable(){
//				public void run() {
//			Activitytrigger();
//				}
//			},2000);
//		}
//	}
	private void StartingActivity(){
		if(startingactivity==true){
				if(isInternetOn(cont)){
					new MoreAppsData().execute("pickzy");
					MoreActivity.loadingdata=true;
					startingactivity=false;
				}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
					public void run() {
						StartingActivity();
					}
				},2000);
		}else{
			Handler handler = new Handler();
			handler.postDelayed(new Runnable(){
				public void run() {
					StartingActivity();
				}
			},2000);
		}
	}
	public class background extends AsyncTask<String, Integer, String>{
		protected String doInBackground(String... params) {
			try{
				int newapp=0;
			XMLParser	parser2 = new XMLParser();
			String xml = parser2.getXmlFromUrl(MoreContants.main+"/apps/androidapi.php?act=newapp&test=0&usrlickey="+key+"&appid="+packagename+"&uqid=&os=1&listtype="+moreappslisttype+"&cnt="+appcount);
			try{
			 newapp=Integer.parseInt(xml);
			}catch(Exception integer){
				newapp=0;
			}
			try{
			if(MoreTextView.count>=newapp){
			if(MoreButton.notified=false){
				
			}else{
				MoreTextView.count=newapp;
			}
				}
			}catch(Exception notifyexception){
				MoreTextView.count=newapp;
			}
			try{
			if(newapp>=2){
//				if (isInternetOn(cont)){
//					new MoreAppsData().execute("pickzy");
//				}
			}
			}catch(Exception f){
				
			}
			}catch(Exception e){
			}
			return null;
		}
	}
}
