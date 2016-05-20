package com.pickzy.moresdk;

import org.w3c.dom.Document;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.pickzy.moresdk.moreadapter.MoreList2ndAdapter;
import com.pickzy.moresdk.moredb.Paid_DB_Pojo;
import com.pickzy.moresdk.moredb.Paid_DatabaseHandler;

public class MoreSecond {
	private String key,packagename,xml;
	private XML2Parser parser;
	private Document doc;
	private NodeList nodelist,nodelist2;
	private int elementcount,elementcount2;
	private String[] keys,strings;
	private Context cont;
	private int appicon,appname,appid,appdiscription,applink;
	private int subscriptiontype,locationenabled,moreappslisttype,currentlisttype;
	private int calculator=0,calc=0;
	public static String[] passimage,passname,passdiscription,passlink,passstring,passappid;
	private Paid_DatabaseHandler db;
	private int plistcount;
	public static int appcount=0;

public MoreSecond(Context context,String appkey,String appPackagename){
	cont=context;
	key=appkey;
	packagename=appPackagename;
	parser=new XML2Parser();
	db=new Paid_DatabaseHandler(cont);
	try{
		plistcount=db.getContactsCount();
	}catch(Exception e){
	}
	if(isInternetOn(cont)){
		new MoreAppsData().execute("");
	}else{
		if(plistcount>=1){
			GetFreeValuesFromDatabase();
		}
	}
}

public class MoreAppsData extends AsyncTask<String, Integer, String> { //Api -3 //AsyncTask
	protected String doInBackground(String... params){
		try {
			xml = parser.getXmlFromUrl(MoreContants.main+"/apps/androidapi.php?act=plist&usrlickey="+key+"&appid="+packagename+"&imp=1&hit=1&listtype=2");
			if(xml!=null){
				Log.e("second xml", xml);
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
					strings[i] =  "0";
				}
				
			}
			keynodeposition();
			applistdetails();
			passvalues();   

		}else{
		}
		} catch (Exception e) {
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
private boolean isInternetOn(Context context1) {
	ConnectivityManager connec = (ConnectivityManager) context1
			.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
			|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
		return true;
	} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
			|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
		return false;
	}
	return false;
}
private void keynodeposition(){
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
				Log.e("appicon", ""+appicon);
				iconboo=true;
			}
			}else if(keys[i].equalsIgnoreCase("AppId")){
				if(appid==0 && appidboo==false){
					appid=j;
					Log.e("appid", ""+appid);
					appidboo=true;
				}	
			}
			else if(keys[i].equals("Filechange")){
				
			}
			else if(keys[i].equals("Name")){
				if(appname==0 && nameboo==false){
					appname=j;
					Log.e("appname", ""+appname);
					nameboo=true;
				}	
			}else if(keys[i].equals("Description")){
				if(appdiscription==0 && disboo==false){
					appdiscription=j;
					Log.e("appdiscription", ""+appdiscription);
					disboo=true;
				}
			}else if(keys[i].equals("Link")){
				if(applink==0 && linkboo==false){
					applink=j;
					Log.e("applink", ""+applink);
					linkboo=true;
				}
			}
		}
		j=j+1;
		}
	
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
				count=count+1;
				j=0;
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
}
private void passvalues(){
	Log.e("elementcount2", ""+elementcount2);
	Log.e("calculator", ""+calculator);
	int passstringcount=elementcount2-calculator;
	Log.e("passstringcount", ""+passstringcount);
	int passkeycount=(passstringcount/6);
	Log.e("calc", ""+calc);
	Log.e("passkeycount", ""+passkeycount);
	PZYAppPromoSDK.appcount=PZYAppPromoSDK.appcount+passkeycount;
	appcount=passkeycount;
	passname=new String[passkeycount];
	passappid=new String[passkeycount];
	passimage=new String[passkeycount];
	passdiscription=new String[passkeycount];
	passlink=new String[passkeycount];
	passstring=new String[passstringcount];
	for(int i=0;i<passstringcount;i++){
		passstring[i]=strings[i+calculator];
		Log.e("passstring", passstring[i]);
	}
	int j=0;
	for(int i=appicon-1;i<passstringcount;){
		passimage[j]=passstring[i];
		Log.e("passimage", passimage[j]);
		i=i+6;
		if(i>=passstringcount)
			break;
		j+=1;
	}
	int k=0;
	for(int i=appname-1;i<passstringcount;){
		passname[k]=passstring[i];
		Log.e("passname", passname[k]);
		i=i+6;
		if(i>=passstringcount)
			break;
		k+=1;
	}
	int l=0;
	for(int i=appdiscription-1;i<passstringcount;){
		passdiscription[l]=passstring[i];
		Log.e("passdiscription", passdiscription[l]);
		i=i+6;
		if(i>=passstringcount)
			break;
		l+=1;
	}
	int m=0;
	for(int i=applink-1;i<passstringcount;){
		passlink[m]=passstring[i];
		Log.e("passlink", passlink[m]);
		i=i+6;
		if(i>=passstringcount)
			break;
		m+=1;
	}
	int n=0;
	for(int i=appid-1;i<passstringcount;){
		passappid[n]=passstring[i];
		Log.e("passappid", passappid[n]);
		i=i+6;
		if(i>=passstringcount)
			break;
		n+=1;
	}
	db.DeleteRow("1");
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
		MoreList2ndAdapter.image=pass_image;
		MoreList2ndAdapter.name=pass_name;
		MoreList2ndAdapter.discription=pass_discription;
		MoreList2ndAdapter.links=pass_link;
		MoreList2ndAdapter.appid=pass_appid;
		MoreList2ndAdapter.count=pass_name.length;
		
		pass_image=null;
		pass_name=null;
		pass_discription=null;
		pass_link=null;
		pass_appid=null;
		
	}else{
		MoreList2ndAdapter.image=passimage;
		MoreList2ndAdapter.name=passname;
		MoreList2ndAdapter.discription=passdiscription;
		MoreList2ndAdapter.links=passlink;
		MoreList2ndAdapter.count= passkeycount;
		MoreList2ndAdapter.appid=passappid;
	}
	}catch(Exception adddetector){
		MoreList2ndAdapter.image=passimage;
		MoreList2ndAdapter.name=passname;
		MoreList2ndAdapter.discription=passdiscription;
		MoreList2ndAdapter.links=passlink;
		MoreList2ndAdapter.count=passkeycount;
		MoreList2ndAdapter.appid=passappid;

	}
	
	WriteDB();
}
private void WriteDB(){
		for(int i=0;i<passimage.length;i++){
			db.addContact(new Paid_DB_Pojo( passimage[i]));
		}
		for(int i=0;i<passname.length;i++){
			db.addContact(new Paid_DB_Pojo( passname[i]));
		}
		for(int i=0;i<passdiscription.length;i++){
			db.addContact(new Paid_DB_Pojo( passdiscription[i]));
		}
		for(int i=0;i<passlink.length;i++){
			db.addContact(new Paid_DB_Pojo( passlink[i]));
		}
		for(int i=0;i<passappid.length;i++){
			db.addContact(new Paid_DB_Pojo( passappid[i]));
		}
		db.addContact(new Paid_DB_Pojo(""+subscriptiontype ));
		db.addContact(new Paid_DB_Pojo(""+locationenabled ));
		db.addContact(new Paid_DB_Pojo( ""+moreappslisttype));
		db.addContact(new Paid_DB_Pojo( ""+currentlisttype));
}
private void GetFreeValuesFromDatabase(){
	int count=db.getAllContacts();
	strings=Paid_DatabaseHandler.Strings;
	int indi=((count-4)/5);
	PZYAppPromoSDK.appcount=PZYAppPromoSDK.appcount+indi;
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
		MoreList2ndAdapter.image=pass_image;
		MoreList2ndAdapter.name=pass_name;
		MoreList2ndAdapter.discription=pass_discription;
		MoreList2ndAdapter.links=pass_link;
		MoreList2ndAdapter.appid=pass_appid;
		MoreList2ndAdapter.count=pass_name.length;
		
		pass_image=null;
		pass_name=null;
		pass_discription=null;
		pass_link=null;
		pass_appid=null;
		
	}else{
		MoreList2ndAdapter.image=passimage;
		MoreList2ndAdapter.name=passname;
		MoreList2ndAdapter.discription=passdiscription;
		MoreList2ndAdapter.links=passlink;
		MoreList2ndAdapter.count= indi;
		MoreList2ndAdapter.appid=passappid;
	}
	}catch(Exception adddetector){
		MoreList2ndAdapter.image=passimage;
		MoreList2ndAdapter.name=passname;
		MoreList2ndAdapter.discription=passdiscription;
		MoreList2ndAdapter.links=passlink;
		MoreList2ndAdapter.count=indi;
		MoreList2ndAdapter.appid=passappid;

	}
}
}
