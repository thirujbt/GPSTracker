package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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

public class BlockListAdap extends BaseAdapter {

	LayoutInflater inflater;
	Context ctn;
	ArrayList<UserDetail> arrList;
	HashMap<UserDetail, Boolean> mapToCheckList = new HashMap<UserDetail, Boolean>();
	DisplayImageOptions options;

	public BlockListAdap(Context ctn, ArrayList<UserDetail> arrList){
		this.ctn = ctn;
		this.arrList = arrList;
		if(arrList != null && arrList.size() >0 ){
			for (UserDetail userDetail : arrList) {
				mapToCheckList.put(userDetail, false);
			}
		}
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
		return arrList.size();
	}

	@Override
	public Object getItem(int position) {

		return arrList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if(convertView == null){
			inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.block_list_adap, parent, false);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.block_list_checkbox);
			holder.emailTxt = (TextView) convertView.findViewById(R.id.txt_email_blockpage_list_item);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.txt_name_blockpage_list_item);
			holder.profileImg = (ImageView) convertView.findViewById(R.id.blocklist_user_img);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final UserDetail detail = (UserDetail) getItem(position);

		String upperString="";

		if(detail.getFirstName()!=null && !detail.getFirstName().equals("") ){
			upperString = detail.getFirstName().substring(0,1).toUpperCase() + detail.getFirstName().substring(1);
		}
		
		holder.nameTxt.setText(upperString);
		holder.emailTxt.setText(""+detail.getEmailId());
		//holder.profileImg.setImageResource(R.drawable.default_image);
		final String imageUrl = detail.getProfImgURL();
		ImageLoader.getInstance().displayImage(imageUrl, holder.profileImg,options, new SimpleImageLoadingListener() {
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

						if(imageUrl != null && ! imageUrl.equals("null") && loadedImage!=null){
							Bitmap bitmap = ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
							imageView.setImageBitmap(bitmap);
							//ImageLoader1.getCroppedBitmap(loadedImage, imageView.getWidth());
						}
						return true;
					}
				});
			}
		});

		if(mapToCheckList.get(detail)){
			holder.checkBox.setChecked(true);
		}else{
			holder.checkBox.setChecked(false);
		}

		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(holder.checkBox.isChecked()){
					mapToCheckList.put(detail, true);
				}else{
					mapToCheckList.put(detail, false);
				}

			}
		});

		return convertView;
	}
	private class ViewHolder{
		TextView nameTxt;
		TextView emailTxt;
		ImageView profileImg;
		CheckBox checkBox;
	}

	public void selectAll(){
		for (UserDetail userDetail : arrList) {
			mapToCheckList.put(userDetail, true);
			notifyDataSetChanged();
		}
	}

	public void unselectAll(){
		for (UserDetail userDetail : arrList) {
			mapToCheckList.put(userDetail, false);
			notifyDataSetChanged();
		}
	}

	public List<UserDetail> getSelectedItems(){
		List<UserDetail> selectedItems = new ArrayList<UserDetail>();
		for (UserDetail detail : mapToCheckList.keySet()) {
			if(mapToCheckList.get(detail)){
				selectedItems.add(detail);
			}
		}
		return selectedItems;
	}

	public void removeSelected(String temp_emailid) { 
		UserDetail mDetail = null;
		String emailid=temp_emailid;
		for(UserDetail detail : mapToCheckList.keySet()) {
			if(mapToCheckList.get(detail)) {
				if(emailid.equals(detail.getEmailId())) {
					//location = mapToCheckList.keySet();
					mDetail = detail;
				}
			}
		}
		if(null != mDetail) {
			arrList.remove(mDetail);
			mapToCheckList.remove(mDetail);
		}
		notifyDataSetChanged();

	}

	public void removedCheckedItems(){
		List<UserDetail> removeList = new ArrayList<UserDetail>();
		for (UserDetail detail : mapToCheckList.keySet()) {
			if(mapToCheckList.get(detail)){
				removeList.add(detail);
			}
		}

		for (UserDetail userDetail : removeList) {
			arrList.remove(userDetail);
			mapToCheckList.remove(userDetail);
		}
		notifyDataSetChanged();
	}

}
