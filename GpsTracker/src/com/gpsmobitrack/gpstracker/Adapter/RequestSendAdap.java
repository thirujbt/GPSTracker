package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Utils.ImageLoader;
import com.gpstracker.pro.R;

public class RequestSendAdap extends BaseAdapter {

	LayoutInflater inflater;
	Context ctn;
	ArrayList<String> reqSendList, imgList;
	ImageLoader imgLoader;
	boolean[] mStateChecked;
	boolean isSelectAll,isUnselectAll;
	public static ArrayList<String> reqSendRemoveIdList = new ArrayList<String>();
	public static ArrayList<String> reqSendRemoveImgList = new ArrayList<String>();
	
	public RequestSendAdap(Context context, ArrayList<String> rank, ArrayList<String> imgListm, boolean selectall, boolean unselectall){
		this.ctn = context;
		this.reqSendList = rank;
		this.imgList = imgListm;
		this.mStateChecked = new boolean[rank.size()]; 
		this.isSelectAll = selectall;
		this.isUnselectAll = unselectall;
		imgLoader = new ImageLoader(ctn);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return reqSendList.size();
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

	@SuppressWarnings("unused")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final TextView emailID;
		final ImageView userImg;
		final CheckBox isReqSendCheck;
		
		inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.request_send_list_adap, parent, false);
		
		emailID = (TextView)view.findViewById(R.id.txt_view_req_send_page);
		userImg = (ImageView)view.findViewById(R.id.profile_img_req_send_page);
		isReqSendCheck = (CheckBox)view.findViewById(R.id.req_sent_checkbox);
		
		emailID.setText(reqSendList.get(position));
		//imgLoader.DisplayImage(imgList.get(position), userImg);
		
		isReqSendCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isReqSendCheck.isChecked()){
					//isReqSendCheck.setChecked(true);
					reqSendRemoveIdList.add(reqSendList.get(position));
					mStateChecked[position] = true;
				}else{
					//isReqSendCheck.setChecked(false);
					reqSendRemoveIdList.remove(reqSendList.get(position));
					mStateChecked[position] = false;
				}
			}
		});
		
		if(isSelectAll){
			//isReqSendCheck.setChecked(true);
			for(int i=0;i<reqSendList.size();i++){
				reqSendRemoveIdList.add(reqSendList.get(i));
				mStateChecked[i] = true;
				//mapToCheckList.put(arrList.get(i).toString(), true);
			}
			for(int i=0;i<imgList.size();i++){
				reqSendRemoveImgList.add(imgList.get(i));
				//mapToCheckList.put(arrList.get(i).toString(), true);
			}
			isSelectAll = false;
		}
		
		if(isUnselectAll){
			//isReqSendCheck.setChecked(false);
			for(int i=0;i<reqSendList.size();i++){
				mStateChecked[i] = false;
				reqSendRemoveIdList.remove(reqSendList.get(i));
				//mapToCheckList.remove(arrList.get(i).toString());
			}
			for(int i=0;i<imgList.size();i++){
				reqSendRemoveImgList.remove(imgList.get(i));
				//mapToCheckList.put(arrList.get(i).toString(), true);
			}
			isUnselectAll = false;
		}
		isReqSendCheck.setChecked(mStateChecked[position]);
		return view;
	}

}
