package com.gpsmobitrack.gpstracker.Bean;

import java.io.Serializable;

public class ContactBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6364143880515277508L;
	private String name;
	private String phoneNo;
	private String email;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
