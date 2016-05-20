package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Bean.UserDetail;
import com.gpsmobitrack.gpstracker.ImageLoaders.ImageLoader1;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TrackListAdapter extends ArrayAdapter<UserDetail> {

	Context ctn;
	LayoutInflater inflater;
	//List<String> arrList;
	List<UserDetail> userDetails;
	boolean[] mstateChecked;
	static CheckedTextView txtView;
	boolean isSelectAll,isUnselectAll;
	/*HashMap<String, Boolean> mapToCheckList = new HashMap<String, Boolean>();
	public static ArrayList<String> removeList = new ArrayList<String>();*/
	
	HashMap<UserDetail, Boolean> mapToCheckList = new HashMap<UserDetail, Boolean>();
	public static ArrayList<UserDetail> removeList = new ArrayList<UserDetail>();
	//public ImageLoader1 imageLoader;
	DisplayImageOptions options;
	
	public TrackListAdapter(Context context, int resource, List<UserDetail> objects, boolean selectall, boolean unselectall) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.ctn =context;
		this.userDetails = objects;
		this.isSelectAll = selectall;
		this.isUnselectAll = unselectall;
		this.mstateChecked = new boolean[userDetails.size()];
		//imageLoader = new ImageLoader1(context.getApplicationContext());
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
	//	.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userDetails.size();
	}

	private class ViewHolder{
		TextView nameTxt;
		TextView emailTxt;
		CheckBox isTrackListCheck;
		ImageView profileImage;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder;
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			inflater = (LayoutInflater) ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.trackpage_list_item, parent, false);
			
			viewHolder.nameTxt = (TextView)convertView.findViewById(R.id.txt_name_trackpage_list_item);
			viewHolder.emailTxt = (TextView)convertView.findViewById(R.id.txt_email_trackpage_list_item);
			viewHolder.isTrackListCheck = (CheckBox)convertView.findViewById(R.id.track_list_checkbox);
			viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.tracklist_user_img);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String upperString="";

		if(userDetails.get(position).getFirstName()!=null && !userDetails.get(position).getFirstName().equals("") ){
			upperString = userDetails.get(position).getFirstName().substring(0,1).toUpperCase() + userDetails.get(position).getFirstName().substring(1);
		}
		
		viewHolder.nameTxt.setText(upperString);
		viewHolder.emailTxt.setText(userDetails.get(position).getEmailId());
		final String imageUrl = userDetails.get(position).getProfImgURL();
		ImageLoader.getInstance().displayImage(imageUrl, viewHolder.profileImage,options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
				Utils.printLog("Load complete", "Load complete");
				final ImageView imageView = (ImageView) view;
				
				
				ViewTreeObserver observerProfileImg = imageView.getViewTreeObserver();
				observerProfileImg.addOnPreDrawListener(new OnPreDrawListener() {
					
					@Override
					public boolean onPreDraw() {
						imageView.getViewTreeObserver().removeOnPreDrawListener(this);
						
						if(imageUrl != null && ! imageUrl.equals("null")){
							Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, loadedImage.getWidth());
							imageView.setImageBitmap(bitmap);
							//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
						}
						return true;
					}
				});
			}
		});
		
		if(mapToCheckList.containsKey(userDetails.get(position))){
			
			viewHolder.isTrackListCheck.setChecked(true);
			
		}else{
			
			viewHolder.isTrackListCheck.setChecked(false);
			
		}
		viewHolder.isTrackListCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.printLog("Map value===", ""+mapToCheckList.containsKey(userDetails.get(position).toString()));
				if(viewHolder.isTrackListCheck.isChecked()){
				if(!mapToCheckList.containsKey(userDetails.get(position))){
					mapToCheckList.put(userDetails.get(position), true);
				notifyDataSetChanged();
				removeList.add(userDetails.get(position));
				}
				}else{
					mapToCheckList.remove(userDetails.get(position));
					notifyDataSetChanged();
					removeList.remove(userDetails.get(position));
				}
			}
		});
		
		if(isSelectAll){
			viewHolder.isTrackListCheck.setChecked(true);
			for(int i=0;i<userDetails.size();i++){
				removeList.add(userDetails.get(i));
				mapToCheckList.put(userDetails.get(i), true);
			}
			isSelectAll = false;
		}
		
		if(isUnselectAll){
			viewHolder.isTrackListCheck.setChecked(false);
			for(int i=0;i<userDetails.size();i++){
				removeList.remove(userDetails.get(i));
				mapToCheckList.remove(userDetails.get(i));
			}
			isUnselectAll = false;
		}
		return convertView;
	}

}
