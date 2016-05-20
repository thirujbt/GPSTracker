package com.gpsmobitrack.gpstracker.Bean;

public class ReqSendBean {
	
	public String getReqSendID() {
		return reqSendID;
	}
	
	public void setReqSendID(String reqSendID) {
		this.reqSendID = reqSendID;
	}
	
	public String getReqSendUserId() {
		return reqSendUserId;
	}
	
	public void setReqSendUserId(String reqSendUserId) {
		this.reqSendUserId = reqSendUserId;
	}
	
	public String getInviteSendEmailId() {
		return inviteSendEmailId;
	}
	
	public void setInviteSendEmailId(String inviteSendEmailId) {
		this.inviteSendEmailId = inviteSendEmailId;
	}
	
	public String getInviteSendStatus() {
		return inviteSendStatus;
	}
	
	public void setInviteSendStatus(String inviteSendStatus) {
		this.inviteSendStatus = inviteSendStatus;
	}
	
	private String reqSendID;
	private String reqSendUserId;
	private String inviteSendEmailId;
	private String inviteSendStatus;

}
