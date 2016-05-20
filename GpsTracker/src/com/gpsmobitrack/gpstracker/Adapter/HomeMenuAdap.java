package com.gpsmobitrack.gpstracker.Adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.MenuItems.InvitePage;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpstracker.pro.R;

public class HomeMenuAdap extends BaseAdapter {
	
	Context ctn;
	List<Drawable> arrListImgs;
	int reqRecCount = 0;
	public HomeMenuAdap(Context ctn, List<Drawable> arrListImgs) {
		super();
		this.ctn = ctn;
		this.arrListImgs = arrListImgs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lvMenuItems.length;
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	
		LayoutInflater inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu_list_items, parent, false);
		
		//Request Received count text view
		TextView badgeTxt = (TextView) view.findViewById(R.id.req_rec_count_txt);
		
		//Request Received count need to display in invite item
		
		if (position == 2) {
			if(!InvitePage.isInviteClicked){
			if (GPSSharedPreference.getInviteCountSharePreference() <= 0) {
				badgeTxt.setVisibility(View.GONE);
			} else {
				badgeTxt.setVisibility(View.VISIBLE);
			    badgeTxt.setText("" + GPSSharedPreference.getInviteCountSharePreference());
			
			}
		}
		}
		
		//Set menu item image
		final ImageView listItemsImg = (ImageView)view.findViewById(R.id.menu_items_img);
		
		listItemsImg.setImageResource(lvMenuItems[position]);
		
		
		return view;
	}
	
	/**
	 * Set request received count and refresh the adaptor
	 * 
	 * @param count
	 */
	/*public void setBadgeCount(int count){
		reqRecCount = count;
		notifyDataSetChanged();
	}*/

	Integer[] lvMenuItems = {R.drawable.home_icon,R.drawable.search_icon,R.drawable.invite_icon,R.drawable.tracklist_icon,
			R.drawable.profile_icon,R.drawable.setting_icon,R.drawable.help_icon,R.drawable.logout_icon};
}
