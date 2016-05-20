package com.gpsmobitrack.gpstracker.Bean;

public class UserProfile {
	
	public String getAuthKey() {
		return authKey;
	}
	
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	public String getDOB() {
		return dob;
	}
	
	public void setDOB(String dob) {
		this.dob = dob;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
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
	
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
		
	}
	
	
	private String authKey;
	private String firstName;
	private String lastName;
	private String emailId;
	private String phoneNo;
	private String dob;
	private String gender;
	private String country;
	private String state;
	private String profImgURL;
	
	
}
