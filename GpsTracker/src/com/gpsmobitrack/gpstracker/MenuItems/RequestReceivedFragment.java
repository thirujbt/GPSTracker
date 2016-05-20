package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.RequestReceiveAdap;
import com.gpsmobitrack.gpstracker.Bean.ReqReceiveBean;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class RequestReceivedFragment extends Fragment implements AsyncResponse {

	ListView reqReceiveListView;
	RequestReceiveAdap adapReqReceive;
	SharedPreferences pref;
	String statusCode,Message,status;
	ArrayList<ReqReceiveBean> reqReceiveList ;
	public static final int ASYNC_SEND_RESPONSE=1;
	String userToRemove = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.request_received_frag, container, false);
		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		reqReceiveListView = (ListView)rootView.findViewById(R.id.req_receive_list);
		return rootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		sendRequest();
	}
	@SuppressWarnings("unchecked")
	private void sendRequest(){
		if(isInternetOn()) {
			String url = AppConstants.REQ_RECVD_LIST_URL;
			String userId = pref.getString(AppConstants.USER_ID_PREF, null);
			BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
			List<NameValuePair> reqReceivePageList = new ArrayList<NameValuePair>();
			reqReceivePageList.add(useridValue);

			new GpsAsyncTask(getActivity(), url,AppConstants.REQ_RECVD_RESP, this).execute(reqReceivePageList);

		} else { 
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}

	@SuppressWarnings("unchecked")
	public void replayRequest(int type,String responseUserId,String relation){
		String url = null;
		userToRemove = responseUserId;
		List<NameValuePair> reqReceivePageList = new ArrayList<NameValuePair>();
		int RespType = 0;
		switch (type) {
		case AppConstants.REQ_RECVD_ACCEPT_RESP:
			url = AppConstants.INVITE_ACCEPT_URL;
			RespType = AppConstants.REQ_RECVD_ACCEPT_RESP;
			BasicNameValuePair status = new BasicNameValuePair("invite_status",AppConstants.ACCEPTED);
			BasicNameValuePair resUserId = new BasicNameValuePair("request_id",responseUserId);
			BasicNameValuePair relationValue = new BasicNameValuePair("relationship",relation);
			reqReceivePageList.add(relationValue);
			reqReceivePageList.add(resUserId);
			reqReceivePageList.add(status);
			break;
		case AppConstants.REQ_RECVD_BLOCK_RESP:
			url = AppConstants.REQUEST_BLOCK_URL;
			RespType = AppConstants.REQ_RECVD_BLOCK_RESP;
			BasicNameValuePair blockresUserId = new BasicNameValuePair(AppConstants.REQUEST_ID,responseUserId);
			reqReceivePageList.add(blockresUserId);
			break;
		case AppConstants.REQ_RECVD_REJECT_RESP:
			url = AppConstants.REQUEST_REJECT_URL;
			RespType = AppConstants.REQ_RECVD_REJECT_RESP;
			BasicNameValuePair rejectresUserId = new BasicNameValuePair("request_id",responseUserId);
			reqReceivePageList.add(rejectresUserId);
			break;
		default:
			break;
		}
		if(isInternetOn()){
			if (url != null) {
				String userId = pref.getString("Userid", null);
				Utils.printLog("RRF2",""+userId);
				BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY,userId);
				reqReceivePageList.add(useridValue);
				new GpsAsyncTask(getActivity(), url, RespType, this)
				.execute(reqReceivePageList);
			}
		} else {
			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}
	}
	//Alert Dialog with Single Button
	public void showSingleTextAlert(String AlertTitle,String AlertText,final String StatusCode){

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
				if(statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)){
					Intent LoginIntent = new Intent(getActivity(), Login.class);
					startActivity(LoginIntent);
				} else {
					dialog.dismiss();
				}
			}});
		dialog.show();
	}
	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		if (RespValue == AppConstants.REQ_RECVD_RESP) {
			reqReceiveList = new ArrayList<ReqReceiveBean>();
			try {
				if (serverResp != null) {
					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					Message = jObj.getString(AppConstants.MESSAGE);
					status = jObj.getString(AppConstants.STATUS);
					switch (statusCode) {
					case AppConstants.NEW_SUCCESS :
						if(jObj.has(AppConstants.DATA)){
							JSONArray jsoARR = jObj.getJSONArray(AppConstants.DATA);
							for (int i = 0; i < jsoARR.length(); i++) {
								JSONObject jARRObj = jsoARR.getJSONObject(i);
								ReqReceiveBean receiveBeanObj = new ReqReceiveBean();
								String id = jARRObj.getString("id");
								String username = jARRObj.getString("firstname");
								String emailId = jARRObj.getString("emailid");
								String userPhone = jARRObj.getString("phoneno");
								String lastName = jARRObj.getString("lastname");
								receiveBeanObj.setEmailReqReceived(emailId);
								receiveBeanObj.setPhoneNoReqReceived(userPhone);
								receiveBeanObj.setUserIdReqReceived(id);
								receiveBeanObj.setUserNameReqReceived(username);
								receiveBeanObj.setLastNameReqReceived(lastName);
								reqReceiveList.add(receiveBeanObj);
							}
						}
						break;
					case AppConstants.NO_TRACK_USER :
						Utils.showToast("No New Requests");
						break;
					case AppConstants.NEW_FAILED :
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
						break;
					default:
						break;
					}
					adapReqReceive = new RequestReceiveAdap(getActivity(),reqReceiveList, RequestReceivedFragment.this);
					reqReceiveListView.setAdapter(adapReqReceive);
				} else {
					Utils.showToast("No response from server");
				}	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if(RespValue == AppConstants.REQ_RECVD_ACCEPT_RESP) {
			if (serverResp != null) {
				try {
					Utils.printLog("RRF, serverResp=",""+serverResp);
					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					Utils.printLog("Status Code", "" + statusCode);
					String message = jObj.getString(AppConstants.MESSAGE);
					if(statusCode.equals(AppConstants.NEW_SUCCESS)){
						if((userToRemove != null) && (!userToRemove.isEmpty())){
							for(int i=0;i<reqReceiveList.size();i++){
								if(reqReceiveList.get(i).getUserIdReqReceived() == userToRemove){
									reqReceiveList.remove(i);
								}
							}
							adapReqReceive = new RequestReceiveAdap(getActivity(),reqReceiveList, RequestReceivedFragment.this);
							reqReceiveListView.setAdapter(adapReqReceive);
						}
						Utils.showToast(AppConstants.TOAST_REQUEST_IS_ACCEPTED);
						sendRequest();
					} else if (statusCode.equals(AppConstants.QUERY_FAILED)) {
						Utils.showToast("Unable to accept request Please try later");
					} else if (statusCode.equals(AppConstants.NEW_FAILED)) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
					} else if (statusCode.equalsIgnoreCase(AppConstants.ALREADY_FRIEND)){
						if((userToRemove != null) && (!userToRemove.isEmpty())){
							for(int i=0;i<reqReceiveList.size();i++){
								if(reqReceiveList.get(i).getUserIdReqReceived() == userToRemove){
									reqReceiveList.remove(i);
								}
							}
							adapReqReceive = new RequestReceiveAdap(getActivity(),reqReceiveList, RequestReceivedFragment.this);
							reqReceiveListView.setAdapter(adapReqReceive);
						}
						Utils.showToast("Already in Friend List");
						sendRequest();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Utils.showToast("No response from server");
			}
		} else if(RespValue == AppConstants.REQ_RECVD_BLOCK_RESP){
			if (serverResp != null) {
				try {
					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					Utils.printLog("Status Code", "" + statusCode);
					String message = jObj.getString(AppConstants.MESSAGE);
					if(statusCode.equals(AppConstants.NEW_SUCCESS)){
						if((userToRemove != null) && (!userToRemove.isEmpty())){
							for(int i=0;i<reqReceiveList.size();i++){
								if(reqReceiveList.get(i).getUserIdReqReceived() == userToRemove){
									reqReceiveList.remove(i);
								}
							}
							adapReqReceive = new RequestReceiveAdap(getActivity(),reqReceiveList, RequestReceivedFragment.this);
							reqReceiveListView.setAdapter(adapReqReceive);
						}
						Utils.showToast(AppConstants.TOAST_BLOCKED_IS_ACCEPTED);
					} else if ((statusCode.equalsIgnoreCase(AppConstants.BLOCK_FAILED ))|| (statusCode.equalsIgnoreCase(AppConstants.INVALID_REQUEST_ID))) {
						Utils.showToast("Unable to Block Request. Please try later");
					} else if (statusCode.equals(AppConstants.NEW_FAILED)) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
					} 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Utils.showToast("No response from server");
			}
		}else if(RespValue == AppConstants.REQ_RECVD_REJECT_RESP){
			if (serverResp != null) {
				try {
					JSONObject jObj = new JSONObject(serverResp);
					statusCode = jObj.getString(AppConstants.STATUS_CODE);
					Utils.printLog("Status Code", "" + statusCode);
					String message = jObj.getString(AppConstants.MESSAGE);
					if(statusCode.equals(AppConstants.NEW_SUCCESS)){
						if((userToRemove != null) && (!userToRemove.isEmpty())){
							for(int i=0;i<reqReceiveList.size();i++){
								if(reqReceiveList.get(i).getUserIdReqReceived() == userToRemove){
									reqReceiveList.remove(i);
								}
							}
							adapReqReceive = new RequestReceiveAdap(getActivity(),reqReceiveList, RequestReceivedFragment.this);
							reqReceiveListView.setAdapter(adapReqReceive);
						}
						Utils.showToast(AppConstants.TOAST_REQUEST_REJECTED);
					} else if ((statusCode.equals(AppConstants.REJECT_FAILED ))|| (statusCode.equalsIgnoreCase(AppConstants.INVALID_REQUEST_ID))) {
						Utils.showToast("Unable to Reject Request. Please try later");
					} else if (statusCode.equals(AppConstants.NEW_FAILED)) {
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED , statusCode);
					} 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Utils.showToast("No response from server");
			}
		}
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