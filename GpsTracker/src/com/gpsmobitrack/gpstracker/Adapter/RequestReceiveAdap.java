package com.gpsmobitrack.gpstracker.Adapter;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.Bean.ReqReceiveBean;
import com.gpsmobitrack.gpstracker.MenuItems.RequestReceivedFragment;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.GPSSharedPreference;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class RequestReceiveAdap extends BaseAdapter implements OnClickListener{

	LayoutInflater inflater;
	Context ctn;
	ArrayList<ReqReceiveBean> arrList;
	//boolean isAccept = true;
	String url = AppConstants.INVITE_ACCEPT_URL;
	//private String[] state = { "Relation","Father", "Mother", "Son", "Daughter", "Friend", "Others"};
	//int clickedPosition = -1;
	//HashMap<Integer, Boolean> checkStates = new HashMap<Integer, Boolean>();
	Fragment fragment;
	
	public RequestReceiveAdap(Context context, ArrayList<ReqReceiveBean> rank,Fragment fragment){
		this.ctn = context;
		this.arrList = rank;
		this.fragment = fragment;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrList.size();
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
		
		final Button acceptBtn,rejectBtn,blockBtn;
		final TextView mailIdTxt,nameTxt;
		inflater = (LayoutInflater)ctn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.request_receive_list_adap, parent, false);
		
		//RelativeLayout dummy = (RelativeLayout)view.findViewById(R.id.parentlay_req_receive_list_adap);
		//final LinearLayout acceptLay = (LinearLayout)view.findViewById(R.id.ll_buttons_req_receive_page);
		//final Spinner spinner = (Spinner)view.findViewById(R.id.spinner_req_receive_page);
		acceptBtn = (Button)view.findViewById(R.id.user_accept_req_receive_page);
		rejectBtn = (Button)view.findViewById(R.id.user_reject_req_receive_page);
		blockBtn = (Button)view.findViewById(R.id.user_block_req_receive_page);
		mailIdTxt = (TextView)view.findViewById(R.id.user_email_req_receive_page);
		nameTxt = (TextView)view.findViewById(R.id.user_name_req_receive_page);
		
	/*	ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(ctn, android.R.layout.simple_spinner_item, state);
		spinAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinAdap);*/
		
		mailIdTxt.setText(arrList.get(position).getEmailReqReceived());
		nameTxt.setText(arrList.get(position).getUserNameReqReceived());
		Utils.printLog("Adaptor", ""+arrList.size());
		acceptBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GPSSharedPreference.setInviteCountSharePreference(GPSSharedPreference.getInviteCountSharePreference()-1);
				Utils.printLog("Resp Accept","Accept"+position);	
				showRelationshipDialog(position,acceptBtn);
				//if(fragment!=null){
			//		Utils.printLog("Get Relation","="+spinner.getSelectedItem().toString());
			//		String relation = spinner.getSelectedItem().toString();
					//((RequestReceivedFragment) fragment).replayRequest(RequestReceivedFragment.ACCEPT,arrList.get(position).getUserIdReqReceived(),"friend");
				//}
			}
		});
		rejectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				GPSSharedPreference.setInviteCountSharePreference(GPSSharedPreference.getInviteCountSharePreference()-1);
				Utils.printLog("Resp Reject","Reject");		
				if(fragment!=null){
					((RequestReceivedFragment) fragment).replayRequest(AppConstants.REQ_RECVD_REJECT_RESP,arrList.get(position).getUserIdReqReceived(),null);
				}
			}
		});
		blockBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GPSSharedPreference.setInviteCountSharePreference(GPSSharedPreference.getInviteCountSharePreference()-1);
				Utils.printLog("Resp Block","Block");		
				if(fragment!=null){
					((RequestReceivedFragment) fragment).replayRequest(AppConstants.REQ_RECVD_BLOCK_RESP,arrList.get(position).getUserIdReqReceived(),null);
				}
			}
		});
		
		return view;
	}
	
	private void showRelationshipDialog(final int position,final View btn){
	//	btn.setBackgroundResource(R.drawable.accept_clicked_btn);
		
		final Dialog dialog = new Dialog(ctn, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		dialog.setContentView(R.layout.alert_relationship);
		final Spinner spinner = (Spinner) dialog.findViewById(R.id.relationship_spinner);
		ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(ctn, android.R.layout.simple_spinner_item, AppConstants.RELATIONSHIP_STATE);
		spinAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinAdap);
		Button ok = (Button) dialog.findViewById(R.id.alert_relationship_ok_btn);
		Button cancel = (Button) dialog.findViewById(R.id.alert_relationship_cancel_btn);
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fragment!=null){
				Utils.printLog("Get Relation",""+spinner.getSelectedItem().toString());
				String relation = spinner.getSelectedItem().toString();
				((RequestReceivedFragment) fragment).replayRequest(AppConstants.REQ_RECVD_ACCEPT_RESP,arrList.get(position).getUserIdReqReceived(),relation);
			}
				dialog.dismiss();
				
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		//		btn.setBackgroundResource(R.drawable.accept_btn);
				dialog.dismiss();
			}
		});
		
		dialog.show();
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
