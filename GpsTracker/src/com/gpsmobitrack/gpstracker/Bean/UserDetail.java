package com.gpsmobitrack.gpstracker.Bean;

public class UserDetail {

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTrackUserId() {
		return trackUserId;
	}

	public void setTrackUserId(String trackUserId) {
		this.trackUserId = trackUserId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getProfImgURL() {
		return profImgURL;
	}

	public void setProfImgURL(String profImgURL) {
		this.profImgURL = profImgURL;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMapImgURL() {
		return mapImgURL;
	}

	public void setMapImgURL(String mapImgURL) {
		this.mapImgURL = mapImgURL;
	}

	public String getRelationShip() {
		return relationShip;
	}

	public void setRelationShip(String relationShip) {
		this.relationShip = relationShip;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	public String getGcmRegId() {
		return gcmRegId;
	}

	public void setGcmRegId(String gcmRegId) {
		this.gcmRegId = gcmRegId;
	}

	private String trackUserId;
	private String firstName;
	private String gcmRegId;
	private String lastName;
	private String emailId;
	private String profImgURL;
	private String latitude;
	private String longitude;
	private String mapImgURL;
	private String relationShip;
	private String location;
	private String date;
	private String time;
	private String phoneNo;
	
}
