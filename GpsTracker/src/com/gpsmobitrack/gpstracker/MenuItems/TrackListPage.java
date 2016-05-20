package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gpsmobitrack.gpstracker.AccountManager.Login;
import com.gpsmobitrack.gpstracker.Adapter.TrackListAdapter;
import com.gpsmobitrack.gpstracker.Bean.UserDetail;
import com.gpsmobitrack.gpstracker.DragSortLibs.DragSortListView;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncJSON;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.SessionManager;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class TrackListPage extends Fragment implements OnClickListener, AsyncResponse {

	TrackListAdapter adapTrackList;
	DragSortListView listforDrag;
	List<UserDetail> userDetails;
	Button selectBtnTrackpage,unselectBtnTrackpage,removeBtnTrackpage,blockBtnTrackpage;
	SharedPreferences pref;

	public static final String TRACK_KEY="track_key1";

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				Utils.printLog("Drg", "Drg="+from+", ="+to);
				UserDetail item = adapTrackList.getItem(from);
				adapTrackList.remove(item);
				adapTrackList.insert(item, to);
				listforDrag.moveCheckState(from, to);

				List<String> trackUserId = new ArrayList<String>();
				List<String> trackUserOrder = new ArrayList<String>();
				List<String> trackEmail = new ArrayList<String>();
				int i=1;
				for (UserDetail userDetail : userDetails) {
					Utils.printLog("Datas", ""+userDetail.getEmailId());
					trackEmail.add(userDetail.getEmailId());
					trackUserId.add(userDetail.getTrackUserId());
					trackUserOrder.add(""+i);
					i++;
				}
				//Send Request
				String url = AppConstants.TRACKORDER_URL;
				String userId = pref.getString(AppConstants.USER_ID_PREF, null);
				JSONArray trackUserIdJson = new JSONArray(trackUserId);
				JSONArray trackUserOrderJson = new JSONArray(trackUserOrder);


				JSONObject sendInviteJSON = new JSONObject();
				try {
					sendInviteJSON.put(AppConstants.AUTH_KEY, userId);
					sendInviteJSON.put("track_userid", trackUserIdJson);
					sendInviteJSON.put("order",trackUserOrderJson);
					String jsonData = sendInviteJSON.toString();
					if(isInternetOn()){
						new GpsAsyncJSON(getActivity(), url, AppConstants.TRACK_LIST_ORDER_RESPONSE, TrackListPage.this).execute(jsonData);

					} else {

						Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (trackEmail.size() > 0) {
					setSortedTrackList(trackEmail);
				}
			}
		}
	};

	private void setSortedTrackList(List<String> trackEmail){
		Editor editor = pref.edit();
		JSONArray jsonArray = new JSONArray(trackEmail);
		Utils.printLog("Track","Json="+jsonArray.toString());
		editor.putString(TRACK_KEY, jsonArray.toString());
		editor.commit();
	}

	private List<String> getSortedTrackList(){

		String trackOrder = pref.getString(TRACK_KEY, null);
		Utils.printLog("Track Order","Order ="+trackOrder);
		if(trackOrder == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		try {
			JSONArray jsonArray2 = new JSONArray(trackOrder);
			for (int i = 0; i < jsonArray2.length(); i++) {
				Utils.printLog("String",""+jsonArray2.getString(i));
				list.add(jsonArray2.getString(i));
			}
		} catch (JSONException e) {
			Utils.printLog("Exception",""+e);
		}
		return list;
	}

	private DragSortListView.RemoveListener onRemove =
			new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			Utils.printLog("Remove", "Rmv="+which);
			UserDetail item = adapTrackList.getItem(which);
			adapTrackList.remove(item);
			listforDrag.removeCheckState(which);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.track_list_frag, container, false);

		pref = getActivity().getSharedPreferences(AppConstants.GPS_TRACKER_PREF, Context.MODE_PRIVATE);
		listforDrag = (DragSortListView)rootView.findViewById(R.id.drop_list_track_page);
		//arrayList = new ArrayList<String>();
		selectBtnTrackpage = (Button)rootView.findViewById(R.id.select_btn_trackpage);
		unselectBtnTrackpage = (Button)rootView.findViewById(R.id.unselect_btn_trackpage);
		removeBtnTrackpage = (Button)rootView.findViewById(R.id.remove_btn_trackpage);
		blockBtnTrackpage = (Button) rootView.findViewById(R.id.block_btn_trackpage);
		blockBtnTrackpage.setOnClickListener(this);
		selectBtnTrackpage.setOnClickListener(this);
		unselectBtnTrackpage.setOnClickListener(this);
		removeBtnTrackpage.setOnClickListener(this);

		listforDrag.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				showRelationshipDialog(position);
			}
		});


		return rootView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(isInternetOn()){

			String url = AppConstants.TRACKLIST_URL;
			String userId = pref.getString(AppConstants.USER_ID_PREF, null);

			BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
			List<NameValuePair> trackPageList = new ArrayList<NameValuePair>();
			trackPageList.add(useridValue);

			Utils.printLog("Url", url);

			new GpsAsyncTask(getActivity(), url,AppConstants.TRACK_LIST_RESP, this).execute(trackPageList);

		} else {

			Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
		}

	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == selectBtnTrackpage){
			if (userDetails != null && userDetails.size() > 0) {
				adapTrackList = new TrackListAdapter(getActivity(),
						R.layout.trackpage_list_item, userDetails, true, false);
				listforDrag.setAdapter(adapTrackList);
				listforDrag.setDropListener(onDrop);
			}else {

				Utils.showToast("TrackList is empty");
			}
		}

		if(v == unselectBtnTrackpage){
			if (userDetails != null && userDetails.size() > 0) {
				adapTrackList = new TrackListAdapter(getActivity(),
						R.layout.trackpage_list_item, userDetails, false, true);
				listforDrag.setAdapter(adapTrackList);
				listforDrag.setDropListener(onDrop);
			} else {

				Utils.showToast("TrackList is empty");
			}
		}

		if(v == removeBtnTrackpage){

			if(isInternetOn()){
				if (userDetails != null && userDetails.size() > 0) {
					if(TrackListAdapter.removeList != null && TrackListAdapter.removeList.size()>0){
						List<String> trackUserId = new ArrayList<String>();

						for (int i = 0; i < TrackListAdapter.removeList.size(); i++) {
							trackUserId.add(TrackListAdapter.removeList.get(i).getTrackUserId());
						}

						//Send Request
						String userId = pref.getString(AppConstants.USER_ID_PREF, null);
						JSONArray trackUserIdJson = new JSONArray(trackUserId);
						String trackUserIdData = trackUserIdJson.toString();

						JSONObject removeTrkUserJSON = new JSONObject();

						try {
							removeTrkUserJSON.put(AppConstants.AUTH_KEY, userId);
							removeTrkUserJSON.put(AppConstants.REQUEST_ID, trackUserIdJson);
							String jsonData = removeTrkUserJSON.toString();

							new GpsAsyncJSON(getActivity(), AppConstants.REMOVE_USER_TRACKLIST_URL, AppConstants.TRACK_LIST_REMOVE_RESP, this).execute(jsonData).get();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

						Utils.showToast("Nothing Selected");
					} 

				} else {

					Utils.showToast("TrackList is empty");
				}

			} else {
				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}
		}

		if(v == blockBtnTrackpage){
			//Block need to implement

			if(isInternetOn()){

				//Using Multiple Request Id
				if (userDetails != null && userDetails.size() > 0) {
					if(TrackListAdapter.removeList != null && TrackListAdapter.removeList.size()>0){
						List<String> trackUserId = new ArrayList<String>();
						for(int i = 0; i < TrackListAdapter.removeList.size(); i++) {
							trackUserId.add(TrackListAdapter.removeList.get(i).getTrackUserId());
						}
						JSONArray trackUserIdJson = new JSONArray(trackUserId);
						trackUserId.toString();
						String userId = pref.getString(AppConstants.USER_ID_PREF, null);

						JSONObject blockTrkUserJSON = new JSONObject();
						try {
							blockTrkUserJSON.put(AppConstants.AUTH_KEY, userId);
							blockTrkUserJSON.put(AppConstants.TRACK_USER_ID, trackUserIdJson);
							String jsonData = blockTrkUserJSON.toString();
							new GpsAsyncJSON(getActivity(), AppConstants.TRACK_USER_BLOCK_URL, AppConstants.TRACK_LIST_BLOCK_RESP, this).execute(jsonData);

						}catch(JSONException e) {
							e.printStackTrace();
						}

					} else {

						Utils.showToast("Nothing Selected");
					} 

				} else {

					Utils.showToast("TrackList is empty");
				}
			} else {

				Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void changeRelationShip(String relation,String trackUserId){
		String url = AppConstants.TRACK_RELATIONSHIP_URL;
		String userId = pref.getString(AppConstants.USER_ID_PREF, null);
		BasicNameValuePair useridValue = new BasicNameValuePair(AppConstants.AUTH_KEY, userId);
		BasicNameValuePair relationshipValue = new BasicNameValuePair(AppConstants.RELATIONSHIP, relation);
		BasicNameValuePair requestValue = new BasicNameValuePair(AppConstants.REQUEST_ID, trackUserId);

		List<NameValuePair> trackPageList = new ArrayList<NameValuePair>();
		trackPageList.add(useridValue);
		trackPageList.add(relationshipValue);
		trackPageList.add(requestValue);

		new GpsAsyncTask(getActivity(), url,AppConstants.CHANGE_RELATIONSHIP_RESP, this).execute(trackPageList);
	}

	private void showRelationshipDialog(final int position){

		final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		dialog.setContentView(R.layout.alert_relationship);
		final Spinner spinner = (Spinner) dialog.findViewById(R.id.relationship_spinner);
		TextView message = (TextView) dialog.findViewById(R.id.alert_relationship_txt);
		message.setText(AppConstants.ALERT_TRACKLIST_RELATIONSHIP+" "+userDetails.get(position).getFirstName());
		ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, AppConstants.RELATIONSHIP_STATE);
		spinAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinAdap);
		Button ok = (Button) dialog.findViewById(R.id.alert_relationship_ok_btn);
		Button cancel = (Button) dialog.findViewById(R.id.alert_relationship_cancel_btn);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.printLog("Get Relation",""+spinner.getSelectedItem().toString());
				String relation = spinner.getSelectedItem().toString();
				if(isInternetOn()){
					changeRelationShip(relation,userDetails.get(position).getTrackUserId());
				}
				else { 
					Utils.showToast(AppConstants.TOAST_NO_INTERNET_CONNECTION);
				}

				dialog.dismiss();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		String relation = userDetails.get(position).getRelationShip();
		for(int i=0;i<AppConstants.RELATIONSHIP_STATE.length;i++){
			if(relation.equalsIgnoreCase(AppConstants.RELATIONSHIP_STATE[i]))
			{
				spinner.setSelection(i);
				break;
			}
		}
		dialog.show();
	}

	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		// TODO Auto-generated method stub
		Utils.printLog("Req Send Resp2", ""+serverResp);

		if (serverResp != null) {
			if (RespValue == AppConstants.TRACK_LIST_RESP) {
				try{
					JSONObject responseObject = new JSONObject(serverResp);
					String statusCode = responseObject.getString(AppConstants.STATUS_CODE);
					if (statusCode.equals(AppConstants.NEW_SUCCESS)) {

						boolean checkData = responseObject.isNull(AppConstants.DATA);
						Utils.printLog("Data in tracklist", ""+checkData);

						if(!checkData){
							JSONObject dataObject = responseObject.getJSONObject(AppConstants.DATA);
							JSONArray jArrResp = dataObject.getJSONArray(AppConstants.TRACK);
							// JSONArray jArrResp = new JSONArray(serverResp);
							userDetails = new ArrayList<UserDetail>();
							for (int i = 0; i < jArrResp.length(); i++) {
								JSONObject jObj = jArrResp.getJSONObject(i);
								UserDetail userDetail = new UserDetail();
								userDetail.setTrackUserId(jObj.getString(AppConstants.ID));
								userDetail.setFirstName(jObj.getString(AppConstants.USER_NAME));
								userDetail.setLastName(jObj.getString(AppConstants.USER_LAST_NAME));
								userDetail.setPhoneNo(jObj.getString(AppConstants.PHONE_NUMBER));
								userDetail.setEmailId(jObj.getString(AppConstants.EMAIL_ID));
								userDetail.setProfImgURL(jObj.getString(AppConstants.PROF_IMG));
								userDetail.setLatitude(jObj.getString(AppConstants.LATITUDE));
								userDetail.setLongitude(jObj.getString(AppConstants.LONGITUDE));
								userDetail.setRelationShip(jObj.getString(AppConstants.RELATIONSHIP));
								userDetails.add(userDetail);
							}

							//Get storted track data					
							List<String> trackSortedList = getSortedTrackList();
							if(userDetails!=null && userDetails.size() >0 ){
								//Order track list
								if (trackSortedList != null
										&& trackSortedList.size() > 0 && userDetails.size() == trackSortedList.size()) { // error index
									for (int i = 0; i < trackSortedList.size(); i++) {
										for (int j = 0; j < userDetails.size(); j++) {
											if (userDetails.get(j).getEmailId()
													.equals(trackSortedList.get(i))) {
												Collections.swap(userDetails, i, j);
												break;
											}
										}
									}
								}
								Utils.printLog("USerDetails",""+userDetails);
								adapTrackList = new TrackListAdapter(getActivity(),R.layout.trackpage_list_item, userDetails, false, false);
								listforDrag.setAdapter(adapTrackList);
								listforDrag.setDropListener(onDrop);
								listforDrag.setRemoveListener(onRemove);
							}else{
								Utils.showToast("TrackList is empty");
							}
						}else{
							Utils.showToast("TrackList is empty");
						}

					}else if(statusCode.equals(AppConstants.EMPTY_VALUE)){
						Utils.showToast(responseObject.getString(AppConstants.MESSAGE));
					}else if(statusCode.equals(AppConstants.NEW_FAILED)){
						
						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED );
					}

				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(RespValue == AppConstants.CHANGE_RELATIONSHIP_RESP){
				try {
					JSONObject responseObject = new JSONObject(serverResp);
					String statusCode = responseObject.getString(AppConstants.STATUS_CODE);		

					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){
						Utils.showToast("Relationship Updated Successfully");

					} else if (statusCode.equalsIgnoreCase(AppConstants.RELATION_UPDATE_FAILED)) {

						Utils.showToast("Unable to update Relationship");
					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {

						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
						
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(RespValue == AppConstants.TRACK_LIST_ORDER_RESP) {
				Utils.showToast(serverResp);
			}else if(RespValue == AppConstants.TRACK_LIST_BLOCK_RESP) {
				try {
					JSONArray mJsonArray = new JSONArray(serverResp);
					String statusCode = null;
					for(int i=0;i<mJsonArray.length();i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						statusCode = mJsonObject.getString(AppConstants.STATUS_CODE);
					}
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)) {
						for (int i = 0; i < TrackListAdapter.removeList.size(); i++) {
							userDetails.remove(TrackListAdapter.removeList.get(i));
						}

						adapTrackList = new TrackListAdapter(getActivity(),
								R.layout.trackpage_list_item, userDetails, false, false);
						listforDrag.setAdapter(adapTrackList);
						listforDrag.setDropListener(onDrop);
						if(TrackListAdapter.removeList.size()>1){
							Utils.showToast("Users Blocked Successfully");

						}else {
							Utils.showToast("User Blocked Successfully");
						}

						if(TrackListAdapter.removeList.size()>0){

							TrackListAdapter.removeList.clear();
						}

					} else if (statusCode.equalsIgnoreCase(AppConstants.INVALID_TRACK_ID)) {

						Utils.showToast("Invalid user");

					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {
						if(TrackListAdapter.removeList.size()>0){

							TrackListAdapter.removeList.clear();
						}

						Utils.showToast("Invalid userid");
					}

					if(TrackListAdapter.removeList.size()>0){

						TrackListAdapter.removeList.clear();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if(RespValue == AppConstants.TRACK_LIST_REMOVE_RESP){
				try {

					//New Work for JSONArray Response
					JSONArray responseArray = new JSONArray(serverResp);
					String statusCode = null;
					for(int i=0;i<responseArray.length();i++) {
						JSONObject mJsonObject = new JSONObject();
						mJsonObject = responseArray.getJSONObject(i);
						statusCode = mJsonObject.getString(AppConstants.STATUS_CODE);
					}
					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){

						for (int i = 0; i < TrackListAdapter.removeList.size(); i++) {
							userDetails.remove(TrackListAdapter.removeList.get(i));
						}

						adapTrackList = new TrackListAdapter(getActivity(),R.layout.trackpage_list_item, userDetails, false, false);
						listforDrag.setAdapter(adapTrackList);
						listforDrag.setDropListener(onDrop);
						if(TrackListAdapter.removeList.size()>1){
							Utils.showToast("Users Removed Successfully");

						}else {
							Utils.showToast("User Removed Successfully");
						}

						if(TrackListAdapter.removeList.size()>0){

							TrackListAdapter.removeList.clear();
						}

					} else if (statusCode.equalsIgnoreCase(AppConstants.TRACK_USER_REMOVE_FAILED)) {

						Utils.showToast("Unable to remove user");

					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {
						if(TrackListAdapter.removeList.size()>0){

							TrackListAdapter.removeList.clear();
						}

						Utils.showToast("Invalid userid");
					}


					if(TrackListAdapter.removeList.size()>0){

						TrackListAdapter.removeList.clear();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(RespValue == AppConstants.TRACK_LIST_ORDER_RESPONSE){

				try {
					JSONObject responseObject = new JSONObject(serverResp);
					String statusCode = responseObject.getString(AppConstants.STATUS_CODE);		

					if(statusCode.equalsIgnoreCase(AppConstants.NEW_SUCCESS)){
						Utils.showToast("Track Order Updated Successfully");

					} else if (statusCode.equalsIgnoreCase(AppConstants.TRACK_ORDER_FAILED)) {

						Utils.showToast("Unable to Update Track Order");
					}else if (statusCode.equalsIgnoreCase(AppConstants.NEW_FAILED)) {

						showSingleTextAlert(AppConstants.ALERT_TITLE,AppConstants.ALERT_MSG_OTHERDEVICE_LOGGED);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		} else {
			Utils.showToast("No response from server");
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
				// TODO Auto-generated method stub

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
