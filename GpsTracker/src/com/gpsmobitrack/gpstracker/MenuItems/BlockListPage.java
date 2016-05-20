package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.BlockListAdap;
import com.gpsmobitrack.gpstracker.Bean.UserDetail;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncJSON;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class BlockListPage extends Fragment implements AsyncResponse, OnClickListener {
	ListView blockListView;
	BlockListAdap adapBlockList;
	SharedPreferences pref;
	String statusCode,Message,status;
	ArrayList<UserDetail> blockArrList ;
	public static final int ASYNC_SEND_RESPONSE=1;
	Button selectAll,unSelectAll,unBlock;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.block_list_frag, container, false);
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		blockListView = (ListView)rootView.findViewById(R.id.block_list_view);
		selectAll = (Button) rootView.findViewById(R.id.select_btn_blockpage);
		unSelectAll = (Button) rootView.findViewById(R.id.unselect_btn_blockpage);
		unBlock = (Button) rootView.findViewById(R.id.unblock_btn_blockpage);
		selectAll.setOnClickListener(this);
		unSelectAll.setOnClickListener(this);
		unBlock.setOnClickListener(this);
		//setData();
		return rootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		sendRequest();
	}
	@SuppressWarnings("unchecked")
	private void sendRequest(){
		if(isInternetOn()){
			String url = AppConstants.BLOCK_LIST_URL;
			String userId = pref.getString("Userid", null);
			if(null != userId) {
				BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
				List<NameValuePair> reqReceivePageList = new ArrayList<NameValuePair>();
				reqReceivePageList.add(useridValue);
				if(url != null ) {
					new GpsAsyncTask(getActivity(), AppConstants.BLOCK_LIST_URL,AppConstants.BLOCK_LIST_RESP, this).execute(reqReceivePageList);	
				}
			}
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		if(RespValue == AppConstants.BLOCK_LIST_RESP){
			if(serverResp != null) {
				try {
					JSONObject mJsonObject = new JSONObject(serverResp);
					statusCode = mJsonObject.getString(AppConstants.STATUS_CODE);
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
						JSONObject mJData = mJsonObject.getJSONObject("data");
						JSONArray mJArrayTrack = mJData.getJSONArray("track");
						blockArrList = new ArrayList<UserDetail>();
						for(int i=0;i<mJArrayTrack.length();i++) {
							UserDetail mUserDetail = new UserDetail();
							JSONObject mJODetails = mJArrayTrack.getJSONObject(i);
							mUserDetail.setFirstName(mJODetails.getString("firstname"));
							mUserDetail.setEmailId(mJODetails.getString("emailid"));
							mUserDetail.setProfImgURL(mJODetails.getString("prof_image_path"));
							mUserDetail.setTrackUserId(mJODetails.getString("id"));
							blockArrList.add(mUserDetail);
						}
						adapBlockList = new BlockListAdap(getActivity(), blockArrList);
						blockListView.setAdapter(adapBlockList);
					} else if(statusCode.equalsIgnoreCase(AppConstants.NO_TRACK_USER)) {
						Utils.showToast("No users in blocked list");
					} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_USER_ID)) {
						//	Utils.showToast("User Id invalid");
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if(RespValue == AppConstants.UNBLOCK_RESP) {
			if(serverResp != null) {
				try {
					Object mObject = new JSONTokener(serverResp).nextValue();
					if(mObject instanceof JSONObject) {
						JSONObject mJsonObject = new JSONObject(serverResp);
						statusCode = mJsonObject.getString(AppConstants.STATUS_CODE);
						if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
							Utils.showToast("User Unblocked Successfully");
						} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)) {
							Utils.showToast("Requested User not in Blocked List");
						} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_USER_ID)) {
							Utils.showToast("User Id invalid");
						}
					} else if(mObject instanceof JSONArray) {
						JSONArray mJsonArray = new JSONArray(serverResp);
						List<UserDetail> mListDetails = adapBlockList.getSelectedItems();
						String temp_emailid = null;
						for(int i=0;i<mJsonArray.length();i++) {
							JSONObject mJsonObject = mJsonArray.getJSONObject(i);
							statusCode = mJsonObject.getString(AppConstants.STATUS_CODE);
							String message = mJsonObject.getString(AppConstants.MESSAGE);
							String[] val = message.split(" ");
							temp_emailid = val[0];
							if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
								adapBlockList.removeSelected(temp_emailid);
								Utils.showToast("User Unblocked Successfully");
								//sendRequest();
								//adapBlockList.notifyDataSetChanged();
							} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)) {
								Utils.showToast(temp_emailid+" not in Blocked List");
							} else if(statusCode.equalsIgnoreCase(AppConstants.INVALID_USER_ID)) {
								Utils.showToast("User Id invalid");
							}
						}
						//adapBlockList.notifyDataSetChanged();
						//adapBlockList.removedCheckedItems();
						//sendRequest();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public void onClick(View v) {
		if (v == selectAll) {
			if (blockArrList != null && blockArrList.size()!= 0) {
				if (adapBlockList != null) {
					adapBlockList.selectAll();
				}	
			} else {
				Utils.showToast("No Users in Blocked List");
			}
		} else if (v == unSelectAll) {
			if (blockArrList != null && blockArrList.size()!= 0) {
				if (adapBlockList != null) {
					adapBlockList.unselectAll();
				}
			}else {
				Utils.showToast("No Users in Blocked List");
			}
		} else if (v == unBlock) {
			if(isInternetOn()){
				if (blockArrList != null && blockArrList.size()!= 0) {
					if (adapBlockList != null) {
						List<UserDetail> selectedItems = adapBlockList
								.getSelectedItems();
						if (selectedItems.size() > 0) {
							/*for (UserDetail userDetail : selectedItems) {
							Log.e("User Details", "=" + userDetail.getFirstName());
						}*/
							String url = AppConstants.UNBLOCK_LIST_URL;
							int RespType = AppConstants.UNBLOCK_RESP;
							String userId = pref.getString("Userid", null);
							List<String> unBlockUserId = new ArrayList<String>();
							for (UserDetail userDetail : selectedItems) {
								unBlockUserId.add(userDetail.getTrackUserId());
							}
							JSONArray trackUserIdJson = new JSONArray(unBlockUserId);
							unBlockUserId.toString();
							JSONObject unBlockTrkUserJSON = new JSONObject();
							try {
								unBlockTrkUserJSON.put(AppConstants.AUTH_KEY, userId);
								unBlockTrkUserJSON.put(AppConstants.TRACK_USER_ID, trackUserIdJson);
								String jsonData = unBlockTrkUserJSON.toString();
								new GpsAsyncJSON(getActivity(), url, RespType, this).execute(jsonData);
							}catch(JSONException e) {
								e.printStackTrace();
							}
							//adapBlockList.removedCheckedItems();
						} else {
							Utils.showToast("Please select a user to unblock");
						}
					}
				}else {
					Utils.showToast("No Users in Blocked List");
				}
			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText){
		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.alert_dialog_main);
		final TextView alertTitle = (TextView)dialog.findViewById(R.id.alert_title);
		final TextView alertMsg = (TextView)dialog.findViewById(R.id.alert_msg);
		final EditText alertEditTxt = (EditText)dialog.findViewById(R.id.alert_edit_txt);
		Button okBtn = (Button) dialog.findViewById(R.id.alert_ok_btn);
		Button cancelBtn = (Button) dialog.findViewById(R.id.alert_cancel_btn);
		cancelBtn.setVisibility(View.GONE);
		alertTitle.setText(AlertTitle);
		alertMsg.setText(AlertText);
		alertEditTxt.setVisibility(View.GONE);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Clear  session
				dialog.dismiss();
				SessionManager session = new SessionManager(getActivity());					
				session.logoutUser(getActivity());
				//Load login 
				Intent LoginIntent = new Intent(getActivity(), Login.class);
				startActivity(LoginIntent);
				getActivity().finish();
			}});
		dialog.show();
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
