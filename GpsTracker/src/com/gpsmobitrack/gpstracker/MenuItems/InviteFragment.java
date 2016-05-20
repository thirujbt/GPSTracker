package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gpsmobitrack.gpstracker.MainFragmentMenu;
import com.gpsmobitrack.gpstracker.Adapter.InviteListAdapter;
import com.gpsmobitrack.gpstracker.Bean.ContactBean;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncJSON;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpsmobitrack.gpstracker.database.DBHandler;
//import com.gpsmobitracker.gpstracker.InviteSocialNetworkFriends.FriendsInvite;
//import com.gpsmobitracker.gpstracker.InviteSocialNetworkFriends.InviteFriendsActivity;
import com.gpstracker.pro.R;

public class InviteFragment extends Fragment implements OnClickListener,AsyncResponse {

	ListView inviteEmailList;
	public static ArrayList<ContactBean> inviteArrList = new ArrayList<ContactBean>();
	public static ArrayList<ContactBean> inviteListLast = new ArrayList<ContactBean>();
	InviteListAdapter inviteAdap;
	Button addBtn,contactsBtn,removeBtn,inviteBtn,editBtn;
	SharedPreferences pref;
	DBHandler dbHandler;
	boolean isMoreInvite = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.invite_frag, container, false);
		dbHandler = new DBHandler(getActivity());
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		inviteEmailList = (ListView)rootView.findViewById(R.id.invite_email_listview);
		editBtn = (Button)rootView.findViewById(R.id.edit_btn_invite_page);
		addBtn = (Button)rootView.findViewById(R.id.add_btn_invite_page);
		contactsBtn = (Button)rootView.findViewById(R.id.contacts_btn_invite_page);
		removeBtn = (Button)rootView.findViewById(R.id.remove_btn_invite_page);
		inviteBtn = (Button)rootView.findViewById(R.id.invite_btn_invite_page);
		addBtn.setOnClickListener(InviteFragment.this);
		contactsBtn.setOnClickListener(InviteFragment.this);
		removeBtn.setOnClickListener(InviteFragment.this);
		inviteBtn.setOnClickListener(InviteFragment.this);
		editBtn.setOnClickListener(InviteFragment.this);
		return rootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		Utils.printLog("OnResume", ""+MainFragmentMenu.myList);
		if(inviteListLast.size() > 0){
			for(int i=0;i<inviteListLast.size();i++){
				if(!MainFragmentMenu.myList.contains(inviteListLast.get(i))) 
					MainFragmentMenu.myList.add(inviteListLast.get(i));
			}
		}
		if(MainFragmentMenu.myList != null) {
			for(int i=0;i<MainFragmentMenu.myList.size();i++){
				if(!checkArrayValue(MainFragmentMenu.myList.get(i).getEmail())){
					ContactBean conBean = new ContactBean();
					conBean.setEmail(MainFragmentMenu.myList.get(i).getEmail());
					conBean.setPhoneNo(MainFragmentMenu.myList.get(i).getPhoneNo());
					inviteArrList.add(conBean);
				}
			}
		}
		inviteListLast = inviteArrList;
		inviteAdap = new InviteListAdapter(getActivity(), inviteArrList, false);
		inviteEmailList.setAdapter(inviteAdap);
		inviteAdap.notifyDataSetChanged();
	}
	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.add_btn_invite_page){
			
			/*Intent fb=new Intent(getActivity(),FriendsInvite.class);
			getActivity().startActivity(fb);*/
			addContactAlert();
		}else if(v.getId() == R.id.contacts_btn_invite_page){
			Utils.printLog("In Invite", "Contacts");
			Intent i = new Intent(getActivity(), EmailListActivity.class);
			getActivity().startActivityForResult(i, 4);
		}else if (v.getId() == R.id.edit_btn_invite_page) {
			if (inviteArrList != null && inviteArrList.size() > 0) {
				Utils.printLog("Invite", "EditBTN");
				removeBtn.setVisibility(View.VISIBLE);
				editBtn.setVisibility(View.GONE);
				inviteAdap = new InviteListAdapter(getActivity(),inviteArrList, true);
				inviteEmailList.setAdapter(inviteAdap);
				inviteAdap.notifyDataSetChanged();
			} else {
				Utils.showToast("No contact to edit");
			}
		} else if (v.getId() == R.id.remove_btn_invite_page) {
			Utils.printLog("Invite", "Remove");
			removeBtn.setVisibility(View.GONE);
			editBtn.setVisibility(View.VISIBLE);
			if (!InviteListAdapter.inviteRemoveList.isEmpty()) {
				Utils.printLog("Inv","Inv"+InviteListAdapter.inviteRemoveList);
				for(int i=0;i<InviteListAdapter.inviteRemoveList.size();i++){
					if (checkArrayValue(InviteListAdapter.inviteRemoveList.get(i).getEmail())) {
						int index =	inviteArrList.indexOf(InviteListAdapter.inviteRemoveList.get(i));
						inviteArrList.remove(index);
						if(MainFragmentMenu.myList.size() > 0) MainFragmentMenu.myList.remove(index);
					}
				}
				InviteListAdapter.inviteRemoveList.clear();
			} else {
				Utils.showToast("No contact is selected");
			}
			inviteAdap = new InviteListAdapter(getActivity(), inviteArrList, false);
			inviteEmailList.setAdapter(inviteAdap);
		} else if (v.getId() == R.id.invite_btn_invite_page) {
			if(isInternetOn()){
				if (inviteArrList != null && inviteArrList.size() > 0) {
					Utils.printLog("Invite List", ""+inviteArrList);
					String url = AppConstants.INVITE_URL;
					
					String userid = pref.getString(AppConstants.USER_ID_PREF, null);
					ArrayList<String> invitEmailList = new ArrayList<String>();
					ArrayList<String> invitPhonelist = new ArrayList<String>();
					for (ContactBean ctn : inviteArrList) {
						invitEmailList.add(ctn.getEmail());
						invitPhonelist.add(ctn.getPhoneNo());
					}
					String emailId = null;
					Cursor cursor = dbHandler.getProfile();
					if (cursor != null && cursor.getCount() > 0) {
						cursor.moveToFirst();
						emailId = cursor.getString(cursor.getColumnIndex(DBHandler.PROFILE_COLUMN_EMAIL_ID));
					}
					if ((invitEmailList.size() == 1) && (invitEmailList.contains(emailId))){
						Utils.showToast("Unable to send request to own mail-id");
					} else {
						if(invitEmailList.contains(emailId)){
							invitEmailList.remove(emailId);
						}
						if(invitEmailList.size() > 1){
							isMoreInvite = true;
						}
						JSONArray inviteListJson = new JSONArray(invitEmailList);
						JSONArray phoneListJson = new JSONArray(invitPhonelist);
						String inviteData = inviteListJson.toString();
						Utils.printLog("JSON Array", ""+invitEmailList+"phone==>"+invitPhonelist);
						Utils.printLog("UserID in home", ""+pref.getString("Userid", null));
						try {
							JSONObject sendInviteJSON = new JSONObject();
							sendInviteJSON.put(AppConstants.AUTH_KEY, userid);
							sendInviteJSON.put(AppConstants.EMAIL_ARRAY, inviteListJson);
							sendInviteJSON.put(AppConstants.PHONE_ARRAY,phoneListJson);
							String jsonData = sendInviteJSON.toString();
							
							System.out.println("CHECKING----------------"+sendInviteJSON);
							new GpsAsyncJSON(getActivity(), url, AppConstants.INVITE_REQUEST_RESP, this).execute(jsonData);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					Utils.showToast("No mail id added for invite.");
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}
	}
	private void addContactAlert() {
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		alertTitle.setText(AppConstants.ALERT_HINT_SEARCH);
		alertMsg.setVisibility(View.GONE);
		alertEditTxt.setHint(AppConstants.ALERT_HINT_SEARCH);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mailid = alertEditTxt.getText().toString().trim();
				Utils.printLog("Mail  in add", mailid);
				boolean isEmailValid = Utils.validEmail(mailid);
				if((mailid != null) && isEmailValid){
					if (!checkArrayValue(mailid)) {
						ContactBean ct = new ContactBean();
						ct.setEmail(mailid);
						ct.setPhoneNo("0");
						inviteArrList.add(ct);
						if(MainFragmentMenu.myList == null){
							MainFragmentMenu.myList = new ArrayList<ContactBean>();
						}
						MainFragmentMenu.myList.add(ct);
					} else {
						if(mailid.isEmpty()) 
							Utils.showToast("No email id entered");
						else
							Utils.showToast("E-mail id Already Exists");
					}
				} else
					Utils.showToast("Not valid mail id");
				inviteAdap = new InviteListAdapter(getActivity(), inviteArrList, false);
				inviteEmailList.setAdapter(inviteAdap);
				inviteAdap.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		alertEditTxt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					String mailid = alertEditTxt.getText().toString().trim();
					Utils.printLog("Mail  in add", mailid);
					boolean isEmailValid = Utils.validEmail(mailid);
					if((mailid != null) && isEmailValid){
						if (!checkArrayValue(mailid)) {
							ContactBean ct = new ContactBean();
							ct.setEmail(mailid);
							ct.setPhoneNo("0");
							inviteArrList.add(ct);
							if(MainFragmentMenu.myList == null){
								MainFragmentMenu.myList = new ArrayList<ContactBean>();
							}
							MainFragmentMenu.myList.add(ct);
						} else {
							if(mailid.isEmpty()) 
								Utils.showToast("No email id entered");
							else
								Utils.showToast("E-mail id Already Exists");
						}
					} else
						Utils.showToast("Not valid mail id");
					inviteAdap = new InviteListAdapter(getActivity(), inviteArrList, false);
					inviteEmailList.setAdapter(inviteAdap);
					inviteAdap.notifyDataSetChanged();
					dialog.dismiss();
				}
				return false;
			}
		});
		dialog.show();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Utils.printLog("Request Code out Invite===", ""+requestCode);
	}
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		Utils.printLog("Req Send Resp2", "" + serverResp);
		//Utils.showToast("" + serverResp);
		try {
			if (serverResp != null) {
				JSONArray array = new JSONArray(serverResp);
				JSONObject responseObject = array.getJSONObject(0);
				Utils.printLog("Inv Resp", ""+responseObject);
				String statusCode = responseObject.getString(AppConstants.STATUS_CODE);
				switch(RespValue) {
				case AppConstants.INVITE_REQUEST_RESP:
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){
						Utils.showToast("Request Sent Successfully");
					} else if (statusCode.equalsIgnoreCase(AppConstants.INVITE_FAILED)) {
						if(inviteArrList.size()== 1)
							Utils.showToast("Unable to Send Requests");
					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {
						if(isMoreInvite) {
							Utils.showToast("Request Sent");
						}
						else {
							Utils.showToast("Invalid User id or User Already in your List");
						}
					}else if(statusCode.equalsIgnoreCase(AppConstants.INVITE_ALREADY_BLOCKED)) {
						if(isMoreInvite) {
							Utils.showToast("Request Sent");
						}
						else {
							Utils.showToast("Invite Already Blocked");
						}
						Utils.printLog("Invite", "Blocked");
					}else if (statusCode.equalsIgnoreCase(AppConstants.INVITE_DEACTIVATE_USER)) {
						if(isMoreInvite) {
							Utils.showToast("Request Sent");
						}
						else {
							Utils.showToast("User Deactivated");
						}
					}
				}
			} else {
				Utils.showToast("No Response from Server");
			}
			inviteArrList.clear();
			if ((MainFragmentMenu.myList != null) &&(MainFragmentMenu.myList.size() != 0)) {
				MainFragmentMenu.myList.clear();
			}
			inviteAdap = new InviteListAdapter(getActivity(), inviteArrList, false);
			inviteEmailList.setAdapter(inviteAdap);
			inviteAdap.notifyDataSetChanged();
		} catch (JSONException e) {
			Utils.printLog("Json Exception",""+e);
		}
	}
	public boolean checkArrayValue(String Value){
		for ( ContactBean conBaan : inviteArrList) {
			if(conBaan.getEmail().equalsIgnoreCase(Value))
				return true;
		}
		return false;
	}
	//Check Internet connection
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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