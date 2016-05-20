package com.gpsmobitrack.gpstracker.chat;

/**
 * Message is a Custom Object to encapsulate message information/fields
 * 
 * @author Adil Soomro
 *
 */
public class Message {
	/**
	 * The content of the message
	 */
	String message;
	/**
	 * boolean to determine, who is sender of this message
	 */
	boolean isMine;
	/**
	 * boolean to determine, whether the message is a status message or not.
	 * it reflects the changes/updates about the sender is writing, have entered text etc
	 */
	boolean isStatusMessage;
	
	String mDate_Time;
	
	/**
	 * Constructor to make a Message object
	 */
	public Message(String message, boolean isMine, String date_time) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
		this.mDate_Time = date_time;
	}
	/**
	 * Constructor to make a status Message object
	 * consider the parameters are swaped from default Message constructor,
	 *  not a good approach but have to go with it.
	 */
	public Message(boolean status, String message) {
		super();
		this.message = message;
		this.isMine = false;
		this.isStatusMessage = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public boolean getMine() {
		return isMine;
	}
	public boolean isStatusMessage() {
		return isStatusMessage;
	}
	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	public void setDateTime(String date_time) {
		this.mDate_Time = date_time;
	}
	
	public String getDateTime() {
		return this.mDate_Time;
	}
	
	
	
	
}
