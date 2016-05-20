package com.gpsmobitrack.gpstracker.MenuItems;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.gpsmobitrack.gpstracker.Adapter.RequestSendAdap;
import com.gpsmobitrack.gpstracker.Bean.ReqSendBean;
import com.gpsmobitrack.gpstracker.InterfaceClass.AsyncResponse;
import com.gpsmobitrack.gpstracker.ServiceRequest.GpsAsyncTask;
import com.gpsmobitrack.gpstracker.Utils.AppConstants;
import com.gpsmobitrack.gpstracker.Utils.Utils;
import com.gpstracker.pro.R;

public class RequestSendFragment extends Fragment implements OnClickListener, AsyncResponse {

	ListView reqSendListView;
	RequestSendAdap adapReqSend;
	List<ReqSendBean> reqSendList = new ArrayList<ReqSendBean>();
	ArrayList<String> userIdReqsendArr,userImgReqsendArr;
	Button selectBtn,unselectBtn,removeBtn;
	SharedPreferences pref;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.request_send_frag, container, false);
		pref = getActivity().getSharedPreferences("GpsTrackerPref", Context.MODE_PRIVATE);
		reqSendListView = (ListView)rootView.findViewById(R.id.req_send_list);
		selectBtn = (Button)rootView.findViewById(R.id.select_btn_reqsend_page);
		unselectBtn = (Button)rootView.findViewById(R.id.unselect_btn_reqsend_page);
		removeBtn = (Button)rootView.findViewById(R.id.remove_btn_reqsend_page);
		userIdReqsendArr = new ArrayList<String>();
		userImgReqsendArr = new ArrayList<String>();
		selectBtn.setOnClickListener(RequestSendFragment.this);
		unselectBtn.setOnClickListener(RequestSendFragment.this);
		removeBtn.setOnClickListener(RequestSendFragment.this);

		userIdReqsendArr.add("venkatesh.n@pickzy.com");
		userIdReqsendArr.add("qwerty@pickzy.com");
		userIdReqsendArr.add("venkat@pickzy.com");
		userIdReqsendArr.add("venkatesh.n@pickzy.com");
		userIdReqsendArr.add("qwerty@pickzy.com");
		userIdReqsendArr.add("venkat@pickzy.com");
		userIdReqsendArr.add("venkatesh.n@pickzy.com");
		userIdReqsendArr.add("qwerty@pickzy.com");
		userIdReqsendArr.add("venkat@pickzy.com");

		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		userImgReqsendArr.add("http://maps.googleapis.com/maps/api/staticmap?center=40.702147,-74.015794" +
				"&zoom=12&size=400x400&sensor=false&markers=color:blue|label:P|40.702147,-74.015794");
		if(adapReqSend !=null){
			reqSendListView.setAdapter(adapReqSend);
		}else{
			adapReqSend = new RequestSendAdap(getActivity(), userIdReqsendArr, userImgReqsendArr, false, false);
			reqSendListView.setAdapter(adapReqSend);
		}
		return rootView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		super.onResume();
		String url = AppConstants.REQ_SENT_LIST_URL;
		String userId = pref.getString("Userid", null);
		BasicNameValuePair useridValue = new BasicNameValuePair("userId", userId);
		List<NameValuePair> reqSendPageList = new ArrayList<NameValuePair>();
		reqSendPageList.add(useridValue);
		new GpsAsyncTask(getActivity(), url,0, this).execute(reqSendPageList);
	}

	@Override
	public void onClick(View v) {
		if(v == selectBtn){
			adapReqSend = new RequestSendAdap(getActivity(), userIdReqsendArr, userImgReqsendArr, true, false);
			reqSendListView.setAdapter(adapReqSend);
		}
		if(v == unselectBtn){
			adapReqSend = new RequestSendAdap(getActivity(), userIdReqsendArr, userImgReqsendArr, false, true);
			reqSendListView.setAdapter(adapReqSend);
		}
		if(v == removeBtn){
			for(int i=0;i < RequestSendAdap.reqSendRemoveIdList.size();i++){
				userIdReqsendArr.remove(RequestSendAdap.reqSendRemoveIdList.get(i));
			}
			for(int i=0;i < RequestSendAdap.reqSendRemoveImgList.size();i++){
				userImgReqsendArr.remove(RequestSendAdap.reqSendRemoveImgList.get(i));
			}
			adapReqSend = new RequestSendAdap(getActivity(), userIdReqsendArr, userImgReqsendArr, false, false);
			reqSendListView.setAdapter(adapReqSend);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onProcessFinish(String serverResp, int RespValue) {
		try{
			Utils.printLog("Req Send Resp", ""+serverResp);
			if(serverResp != null){
				JSONObject jObj = new JSONObject(serverResp);
				String statusCode = jObj.getString("statusCode");
				if(statusCode.equals("200")){
					String reqSendData = jObj.getString("data");
					JSONArray jDataArray = new JSONArray(reqSendData);
					for(int i=0;i<jDataArray.length();i++){
						ReqSendBean reqSendObject = new ReqSendBean();
						JSONObject dataJObj = jDataArray.getJSONObject(i);
						String id = dataJObj.getString("id");
						String userID = dataJObj.getString("userid");
						String inviteEmail = dataJObj.getString("invite_emailid");
						String inviteStatus = dataJObj.getString("invite_status");
						String createdOn = dataJObj.getString("createdOn");
						String modifiedOn = dataJObj.getString("modifiedOn");

						reqSendObject.setReqSendID(id);
						reqSendObject.setInviteSendEmailId(inviteEmail);
						reqSendObject.setReqSendUserId(userID);

						reqSendList.add(reqSendObject);
					}
				}
				else{
					//statusCode failed
				}
			}
			else {
				//Toast.makeText(getActivity(), "No response from server", Toast.LENGTH_SHORT).show();
				Utils.showToast("No response from server");
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
}