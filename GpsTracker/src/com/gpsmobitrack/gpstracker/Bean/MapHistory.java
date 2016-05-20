package com.gpsmobitrack.gpstracker.Bean;


public class MapHistory {
	
	private double latitude;
	private double longitude;
	private String date;
	private String time;
	private String city;
	
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double lat) {
		this.latitude = lat;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double lon) {
		this.longitude = lon;
	}
	
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
	
	public String getCityname() {
		return city;
	}
	public void setCityName(String city) {
		this.city = city;
	}
}