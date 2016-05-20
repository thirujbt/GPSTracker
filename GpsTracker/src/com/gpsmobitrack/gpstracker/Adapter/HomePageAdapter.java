package com.gpsmobitrack.gpstracker.Adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.Bean.UserDetail;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.MenuItems.HistoryPage;
import com.gpsmobitrack.gpstracker.MenuItems.HomeDetailPage;
import com.gpsmobitrack.gpstracker.TrackingService.HandlerManager;
import com.gpsmobitrack.gpstracker.TrackingService.TrackFriendService;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HomePageAdapter extends BaseAdapter {

	//LayoutInflater inflater;
	Context context;
	Boolean isSelect;
	String data;
	LayoutInflater  inflater;
	//public ImageLoader1 imageLoader;
	List<UserDetail> userDetailList;
	DisplayImageOptions options;
	String imageUrl ="";
	SharedPreferences pref;
	Editor editor;

	public HomePageAdapter(Context context, List<UserDetail> list ,String AdapterCode) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.userDetailList = list;
		pref = context.getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		editor = pref.edit();
		//imageLoader = new ImageLoader1(context.getApplicationContext());
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	private class ViewHolder{
		TextView usernameTxt;
		TextView userLocTxt;
		TextView relationTxt;
		TextView dateTimeTxt;
		ImageView profileImg;
		Button histortBtn;
		Button detailsBtn;
		@SuppressWarnings("unused")
		TextView gpsTrackerSym;
	}

	//	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;

		if(convertView == null){
			viewHolder = new ViewHolder();
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.home_page_list_item, parent, false);

			viewHolder.dateTimeTxt = (TextView)convertView.findViewById(R.id.more_btn_home);
			viewHolder.usernameTxt = (TextView) convertView.findViewById(R.id.username_home);
			viewHolder.profileImg = (ImageView) convertView.findViewById(R.id.user_profile_img_home);
			viewHolder.userLocTxt = (TextView)convertView.findViewById(R.id.location_home);
			viewHolder.histortBtn = (Button)convertView.findViewById(R.id.history_btn_home);
			viewHolder.detailsBtn = (Button)convertView.findViewById(R.id.detail_btn_home);
			viewHolder.relationTxt = (TextView)convertView.findViewById(R.id.relation_home);
			viewHolder.gpsTrackerSym = (TextView)convertView.findViewById(R.id.gpstrackerSymbol);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final UserDetail userDetailObj = userDetailList.get(position);

		String upperString="";

		if(userDetailObj.getFirstName()!=null && !userDetailObj.getFirstName().equals("") ){
			upperString = userDetailObj.getFirstName().substring(0,1).toUpperCase() + userDetailObj.getFirstName().substring(1);
		}
		viewHolder.usernameTxt.setText(upperString);
		viewHolder.userLocTxt.setText(userDetailObj.getLocation());
		viewHolder.relationTxt.setText(userDetailObj.getRelationShip());
		viewHolder.dateTimeTxt.setText(userDetailObj.getDate() + "  " + userDetailObj.getTime());
		/*if(userDetailObj.getTrackUserId().equalsIgnoreCase(HandlerManager.trackUserId)){

			if(HomeDetailPage.isTrackingON){

				viewHolder.gpsTrackerSym.setVisibility(View.VISIBLE);

			} else {

				viewHolder.gpsTrackerSym.setVisibility(View.GONE);
			}

		} else {

			viewHolder.gpsTrackerSym.setVisibility(View.GONE);

		}*/
		Utils.printLog("getHeight",""+viewHolder.profileImg.getHeight());
		Utils.printLog("getWidth",""+viewHolder.profileImg.getWidth());
		imageUrl = userDetailObj.getProfImgURL();

		ImageLoader.getInstance().displayImage(imageUrl, viewHolder.profileImg,options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

			}

			@SuppressWarnings("unused")
			@Override
			public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
				Utils.printLog("Load complete", "Load complete");
				final ImageView imageView = (ImageView) view;

				boolean bitmapexit =false;
				if(loadedImage != null){
					bitmapexit = true;	
				}
				if(imageUrl != null){
					Utils.printLog("log", ""+imageUrl);
				}

				String checkimage = imageUrl;

				ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
				observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {

					@Override
					public boolean onPreDraw() {
						imageView.getViewTreeObserver().removeOnPreDrawListener(this);

						if(imageUri != null && ! imageUri.equals("null") && loadedImage!=null && !imageUri.equalsIgnoreCase("")){
							Bitmap bitmap = ImageLoader1.getRoundCroppedBitmap(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
							//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
						}
						return true;
					}
				});
			}
		});

		viewHolder.histortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isInternetOn()){ 
					boolean isServiceOn = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
					if(isServiceOn){
						Intent historyIntent = new Intent(context,HistoryPage.class);
						historyIntent.putExtra("trackuserID", userDetailObj.getTrackUserId());
						historyIntent.putExtra("trackuserImageUrl", userDetailObj.getProfImgURL());
						historyIntent.putExtra("trackuserName", userDetailObj.getFirstName());
						historyIntent.putExtra(AppConstants.USER_LIST_POSITION_INTENT, position);
						context.startActivity(historyIntent);

					} else {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_DISABLE_TRACK_USER );
					}

				} else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}
			}
		});

		viewHolder.detailsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(isInternetOn()){
					boolean isServiceOn = pref.getBoolean(AppConstants.IS_SERVICE_ENABLED_PREF, true);
					if(isServiceOn){

						if(HomeDetailPage.isTrackingON){
							if(userDetailObj.getTrackUserId().equalsIgnoreCase(HandlerManager.trackUserId)){

								Intent detailIntent = new Intent(context,HomeDetailPage.class);
								detailIntent.putExtra("trackuserID", userDetailObj.getTrackUserId());
								detailIntent.putExtra(AppConstants.USER_LIST_POSITION_INTENT, position);
								detailIntent.putExtra(AppConstants.GCM_STATUS_INTENT, false);
								editor.putString(AppConstants.GCM_REGID_PREF, userDetailObj.getGcmRegId());
								//	editor.putBoolean(AppConstants.GCM_FROM_PREF, false);
								editor.commit();
								context.startActivity(detailIntent);

							} else {

								showTrackingAlert(userDetailObj,position);
							}


						} else {
							Intent detailIntent = new Intent(context,HomeDetailPage.class);
							detailIntent.putExtra("trackuserID", userDetailObj.getTrackUserId());
							detailIntent.putExtra(AppConstants.USER_LIST_POSITION_INTENT, position);
							detailIntent.putExtra(AppConstants.GCM_STATUS_INTENT, false);
							//detailIntent.putExtra(AppConstants.GCM_REGID_INTENT, userDetailObj.getGcmRegId());
							//detailIntent.putExtra("fromGCM", false);
							editor.putString(AppConstants.GCM_REGID_PREF, userDetailObj.getGcmRegId());
							//	editor.putBoolean(AppConstants.GCM_FROM_PREF, false);
							editor.commit();
							context.startActivity(detailIntent);

						}

					} else {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_DISABLE_TRACK_USER );
					}

				} else {
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}

			}
		});

		return convertView;
	}

	//Tracking Alert
	public void showTrackingAlert(final UserDetail userDetailObj, final int pos){

		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AppConstants.ALERT_TITLE);
		alertMsg.setText(AppConstants.ALERT_MSG_TRACKING);
		alertEditTxt.setVisibility(View.GONE);

		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Clear  session

				AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent intent2 = new Intent(context, TrackFriendService.class);
				PendingIntent pintent = PendingIntent.getService(context, 0, intent2, 0);
				if(PendingIntent.getService(context, 0, intent2, PendingIntent.FLAG_NO_CREATE) != null) {
					alarm.cancel(pintent);
				}
				if(HomeDetailPage.stalatitudeArry !=null){
					if(HomeDetailPage.stalatitudeArry.size()>0){

						HomeDetailPage.stalatitudeArry.clear();
						HomeDetailPage.stalongitudeArry.clear();
					}
				}


				HomeDetailPage.isTrackingON =false;
				HomeDetailPage.goneBackground = false;
				HandlerManager.trackUserId = "";
				HandlerManager hanManager = HandlerManager.getInstance(context);
				hanManager.setonInterface(null);
				dialog.dismiss();

				Intent detailIntent = new Intent(context,HomeDetailPage.class);
				detailIntent.putExtra("trackuserID", userDetailObj.getTrackUserId());
				detailIntent.putExtra(AppConstants.USER_LIST_POSITION_INTENT,pos);
				detailIntent.putExtra(AppConstants.GCM_STATUS_INTENT, false);
				editor.putString(AppConstants.GCM_REGID_PREF, userDetailObj.getGcmRegId());
				//	editor.putBoolean(AppConstants.GCM_FROM_PREF, false);
				editor.commit();
				context.startActivity(detailIntent);


			}});

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
		dialog.show();

	}

	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){

		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);

		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);
		//	cancelBtn.setVisibility(View.GONE);

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Clear  session
				dialog.dismiss();

				Activity parentAct = (FragmentActivity)context;
				if(parentAct instanceof MainFragmentMenu) {
					((MainFragmentMenu) parentAct).changeFragment();
				}

			}});
		dialog.show();
	}

	//Check Internet connection
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
}
