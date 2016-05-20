package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Bean.ContactBean;
import com.gpsmobitrack.gpstracker.Utils.ImageLoader;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class InviteListAdapter extends BaseAdapter {

	LayoutInflater inflater;
	Context ctn;
	ArrayList<ContactBean> inviteList = new ArrayList<ContactBean>(), imgList;
	ImageLoader imgLoader;
	boolean[] mStateChecked;
	boolean isRemove;
	public static ArrayList<ContactBean> inviteRemoveList = new ArrayList<ContactBean>();
	public static ArrayList<String> reqSendRemoveImgList = new ArrayList<String>();
	
	public InviteListAdapter(Context context, ArrayList<ContactBean> rank, boolean isRemove){
		this.ctn = context;
		this.inviteList = rank;
		this.isRemove = isRemove;
		this.mStateChecked = new boolean[rank.size()]; 
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return inviteList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final TextView emailID;
		final CheckBox isInviteCheck;
		
		inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.invite_list_adapter, parent, false);
		
		emailID = (TextView)view.findViewById(R.id.txt_view_invite_page);
		isInviteCheck = (CheckBox)view.findViewById(R.id.invite_checkbox);
		
		emailID.setText(inviteList.get(position).getEmail());
		
		if(isRemove) isInviteCheck.setVisibility(View.VISIBLE);
		else isInviteCheck.setVisibility(View.GONE);
		
		isInviteCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isInviteCheck.isChecked()){
					if(!inviteRemoveList.contains(inviteList.get(position)))
						inviteRemoveList.add(inviteList.get(position));
					mStateChecked[position] = true;
				}else{
					Utils.printLog("Invite List", ""+inviteList);
					if(inviteRemoveList.contains(inviteList.get(position)))
						inviteRemoveList.remove(inviteList.get(position));
					mStateChecked[position] = false;
				}
				Utils.printLog("Invite List", ""+inviteList);
			}
		});
		
		isInviteCheck.setChecked(mStateChecked[position]);
		return view;
	}

}
