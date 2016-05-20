package com.gpsmobitrack.gpstracker.chat;

import java.text.SimpleDateFormat;

import com.gpsmobitrack.gpstracker.Utils.AppConstants;

public interface Config {

	// When you are using two simulator for testing this application.
	// Make SECOND_SIMULATOR value true when opening/installing application in second simulator
	// Actually we are validating/saving device data on IMEI basis.
	// if it is true IMEI number change for second simulator

	static final boolean SECOND_SIMULATOR = false;

	// CONSTANTS

	// Server Url absolute url where php files are placed.
	//static final String YOUR_SERVER_URL   =  "http://10.0.0.56/webservice/chat_message/";

	//static final String YOUR_SERVER_URL   =  "http://gpsservice.pickzy.com/pickzy_demo/chat_message/";

	static final String YOUR_SERVER_URL  = AppConstants.YOUR_SERVER_URL;

	// Google project id
	static final String GOOGLE_SENDER_ID = "455461920149"; 
	//static final String GOOGLE_SENDER_ID = "623960543860";
	//static final String GOOGLE_SENDER_ID = "403588680916"; // new sender id

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Android Example";

	// Broadcast reciever name to show gcm registration messages on screen 
	static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
			"com.androidexample.gcm.DISPLAY_REGISTRATION_MESSAGE";

	// Broadcast reciever name to show user messages on screen
	// static final String DISPLAY_MESSAGE_ACTION =
	//    "com.androidexample.gcm.DISPLAY_MESSAGE";

	static final String DISPLAY_MESSAGE_ACTION = 
			"com.example.gpstracker.DISPLAY_MESSAGE";


	// Parse server message with this name
	static final String EXTRA_MESSAGE = "message";
	

	static final String REGID_TO_USER = "regid_touser";

	SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yy HH:mm");




}
