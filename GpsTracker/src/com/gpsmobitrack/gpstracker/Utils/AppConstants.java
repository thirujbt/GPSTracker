package com.gpsmobitrack.gpstracker.Utils;

public class AppConstants {

	/*dev url*/

	public static final String BASE_URL ="http://gps-dev.gpsmobitrack.com/pickzy_dev/service/";
	public static final String BASE_URL1 ="http://gps-dev.gpsmobitrack.com/pickzy_dev/service/pro/";
	public static final String YOUR_SERVER_URL = "http://gps-dev.gpsmobitrack.com/pickzy_dev/chat_message/";
	public static final String TERMS_CONDTIONS_URL_GPS = "http://gps-dev.gpsmobitrack.com/support-android.html";

	//http://gps-dev.gpsmobitrack.com/pickzy_dev/service/pzyinvite.php


	/*qa url*/
	
	/* public static final String BASE_URL = "http://gps-qa.gpsmobitrack.com/service/";
	 * public static final String YOUR_SERVER_URL = "http://gps-qa.gpsmobitrack.com/chat_message/";
	 * public static final String TERMS_CONDTIONS_URL_GPS = "http://gps-qa.gpsmobitrack.com/support-android.html";
	 */

	
	/*production url*/

	/*public static final String YOUR_SERVER_URL = "http://api.gpsmobitrack.com/chat_message/";
	  public static final String BASE_URL = "http://api.gpsmobitrack.com/service/";
	  public static final String TERMS_CONDTIONS_URL_GPS = "http://api.gpsmobitrack.com/support-android.html";
  */

	public static final boolean IS_TEST_MODE = true;

	public static final String TERMS_CONDTIONS_URL_TEST ="http://www.dvo.com/support-android.html";

	public static final long DEFAULT_TIME_INTERVAL = 5;


	//Ad Key

	public static final String BANNER_AD_KEY ="ca-app-pub-7389484032382043/7677450109";

	public static final String INTERSTIAL_AD_KEY ="ca-app-pub-7389484032382043/9154183304";


	// API Request URL

	
	public static final String ACTIVATE_MAIL_URL = AppConstants.BASE_URL1 + "pzyacitvate";


	public static final String LOGOUT_URL = AppConstants.BASE_URL + "pzylogout";

	public static final String APP_UPDATE_URL = AppConstants.BASE_URL +"pzyupdateapp";

	public static final String TRACKLIST_URL = AppConstants.BASE_URL + "pzytracklist";

	public static final String TRACKORDER_URL = AppConstants.BASE_URL + "pzytrkorder";

	public static final String REMOVE_USER_TRACKLIST_URL = AppConstants.BASE_URL + "pzyrmvtracklist";

	public static final String DEACTIVATE_URL = AppConstants.BASE_URL + "pzyusrdeactivate";

	public static final String INAPP_PURCHASE_UPD_URL = AppConstants.BASE_URL +"pzyinappstatusupt";

	//public static final String SEARCH_URL = AppConstants.BASE_URL + "pzysearch";

	public static final String SEARCH_URL = AppConstants.BASE_URL + "pzysearch_phase3";

	public static final String INVITE_URL = AppConstants.BASE_URL + "pzyinvite";

	public static final String REQ_SENT_LIST_URL = AppConstants.BASE_URL + "pzyreqsendlist";

	public static final String REQ_RECVD_LIST_URL = AppConstants.BASE_URL + "pzyreqreceived";

	public static final String INVIT_ACCPT_URL = AppConstants.BASE_URL + "pzyinvaccept";

	public static final String TRACK_USER_BLOCK_URL = AppConstants.BASE_URL + "pzytrkusrblock";

	public static final String PROFILE_IMAGE_URL = AppConstants.BASE_URL + "pzyprofileimage";

	public static final String CHANGE_PASSWORD_URL = AppConstants.BASE_URL + "pzychgpaswd";

	public static final String PROFILE_INFO_URL = AppConstants.BASE_URL + "pzyprofileinfo";

	public static final String PROFILE_UPDATE_URL = AppConstants.BASE_URL + "pzyupdateprofile";

	public static final String HOME_URL = AppConstants.BASE_URL + "pzyhome";

	public static final String USER_DETAIL_URL = AppConstants.BASE_URL + "pzyusrdetail";

	public static final String HISTORY_DATE_URL = AppConstants.BASE_URL + "pzyhistorydate";

	public static final String HISTORY_URL = AppConstants.BASE_URL + "pzyhistory";

	//public static final String SYNC_LOCATION_URL = AppConstants.BASE_URL + "pzysyncloc";

	public static final String SYNC_LOCATION_URL = AppConstants.BASE_URL + "pzysyncloc_phase3";

	public static final String INVITE_ACCEPT_URL = AppConstants.BASE_URL + "pzyinvaccept";

	
	
	
	
	public static final String REGISTER_URL = AppConstants.BASE_URL1 + "pzyregister";

	public static final String LOGIN_URL = AppConstants.BASE_URL1 + "pzylogin";
	 
	public static final String ACTIVATE = AppConstants.BASE_URL1 + "pzyacitvate";
	
	public static final String FORGOT_PASSWORD_URL = AppConstants.BASE_URL + "pzyforgotpaswd";

	public static final String TRACK_RELATIONSHIP_URL = AppConstants.BASE_URL +"pzysetTrackuserRelation";

	public static final String REQUEST_REJECT_URL = AppConstants.BASE_URL +"pzyrejectresponse"; 

	public static final String PROFILE_DELETE_IMAGE_URL = AppConstants.BASE_URL + "pzydelprofileimage";

	public static final String BLOCK_LIST_URL = AppConstants.BASE_URL+"pzyblocklist";

	public static final String REQUEST_BLOCK_URL = AppConstants.BASE_URL+"pzyinviteblockuser";

	public static final String REACTIVATE_ACCOUNT_URL = AppConstants.BASE_URL+"pzyactivateuser";

	public static final String SOURCE_DESTINATION_URL = AppConstants.BASE_URL+"pzysrcdest";

	public static final String UNBLOCK_LIST_URL = AppConstants.BASE_URL+"pzyunblockusrlist";
	
	public static final String IN_APP_STATUS_UPT_URL = AppConstants.BASE_URL1+"pzyinappstatusupt";
	
	
	//Alert Dialog Constants
	public static final String ALERT_TRACKLIST_RELATIONSHIP="Please select Relationship between you and";

	public static final String ALERT_TITLE ="Alert";

	public static final String ALERT_TITLE_CONFIRM ="Confirm Your Account";

	public static final String ALERT_MSG_TRACKING = "Are you sure want to stop current tracking?";

	public static final String ALERT_TITLE_CONFIRM_PASSWORD = "Confirm Password";

	public static final String ALERT_CONFIRM_MESSAGE ="An e-mail with a confirmation link has been sent. " +
			"Please check your e-mail and click that link to verify your e-mail ID. " +
			"click OK to continue.";
	public static final String ALERT_VERIFICATION_CONFIRM_MESSAGE = "Your e-mail id is not verified. Do you want to send verification link to your e-mail id ?";

	public static final String ALERT_FORGET_PASSWORD_LINK_MSG = "The password reset link has been sent to your mail ID";

	public static final String ALERT_MSG_OTHERDEVICE_LOGGED = "You have been logged in Other Device";

	public static final String ALERT_MSG_SEARCH_NOT_FOUND = "A particular user is not present in your Track List, " +
			"Invite user to track.";

	public static final String ALERT_DISABLE_TRACK_USER ="Switch On Background Service To Track the Mobile";

	public static final String ALERT_MSG_NOT_VERIFIED_USER = "Please verify your account to track user";

	public static final String ALERT_TITLE_FORGET_PASSWORD = "Forgot Password";

	public static final String ALERT_HINT_CONFIRM_PASSWORD = "Re-type Password";

	public static final String ALERT_TITLE_SEARCH = "Search Result";

	public static final String ALERT_HINT_SEARCH = "Enter Email ID";

	public static final String ALERT_TITLE_DEACTIVATE_ACCOUNT = "De-activate Account";

	public static final String ALERT_MSG_DEACTIVATE_ACCOUNT = "Do you want to De-activate Account?";

	public static final String ALERT_MSG_EXIT_APP = "Are you sure want to EXIT?";

	public static final String ALERT_ENABLE_GPS = "Please Switch ON GPS Satellites ";

	public static final String ALERT_TITLE_APP_UPDATE = "App Update";

	public static final String ALERT_MSG_APP_UPDATE = "New Update Available";

	public static final String ALERT_MSG_NO_UPDATE = "You are using Latest Version of App"; 

	public static final String ALERT_MSG_REACTIVATE = "Your account is deactivated. Do you want to activate?";

	public static final String ALERT_MSG_BACKGROUND_OFF  = "You will not able to Track Users Until you Switch back";

	// Added by thiru phase III

	public static final String ALERT_FB_LOGIN = "Sign Up";

	public static final String ALERT_MSG_PROFILE_PUBLIC_OFF="If you don't share your location with everyone,you won't be able to see other people's location";

	public static final String FNAME_EMPTY  = "Firstname should not be empty";

	public static final String INVALID_EMAIL  = "Enter valid email-id";

	public static final String EMAIL_EMPTY  = "Email should not be empty";

	public static final String PHONE_NO_EMPTY  = "Phone Number should not be empty";

	//API Interface Response Status Code
	public static final int SIGN_UP_RESP = 1000;

	public static final int SIGN_IN_RESP = 1001;

	public static final int FORGOT_PASSWORD_RESP = 1002;

	public static final int CHANGE_PASSWORD_RESP = 1003;

	public static final int HOME_TRACKCOUNT_RESP = 1004;

	public static final int HOME_PAGINATION_RESP = 1005;

	public static final int DEACTIVATE_ACCOUNT_RESP = 1006;

	public static final int LOG_OUT_USER_RESP  = 1007;

	public static final int PROFILE_LODE_RESP = 1008;

	public static final int PROFILE_SAVE_RESP = 1009;

	public static final int PROFILE_PSWD_CHNG_RESP =  1010;

	public static final int REQ_RECVD_RESP  = 1011;

	public static final int REQ_RECVD_ACCEPT_RESP  = 1012;

	public static final int REQ_RECVD_REJECT_RESP  = 1013;

	public static final int REQ_RECVD_BLOCK_RESP = 1014;

	public static final int SEARCH_RESP = 1015;

	public static final int SEARCH_INVITE_RESP = 1016;

	public static final int APP_UPDATE_RESP = 1017;

	public static final int TRACK_LIST_REMOVE_RESP = 1018;

	public static final int TRACK_LIST_ORDER_RESP = 1019;

	public static final int TRACK_LIST_RESP = 1020;

	public static final int PROFILE_DELETE_RESP = 1021;

	public static final int INVITE_REQUEST_RESP = 1022;

	public static final int USER_DETAIL_RESP = 1023;

	public static final int ACTIVATE_MAIL_RESP = 1024;

	public static final int CHANGE_RELATIONSHIP_RESP = 1025;

	public static final int REACTIVATE_ACCOUNT_RESP = 1026;

	public static final int TRACK_LIST_BLOCK_RESP = 1027;

	public static final int SOURCE_DESTINATION_RESP = 1028;

	public static final int BLOCK_LIST_RESP = 1029;

	public static final int UNBLOCK_RESP = 1030;

	public static final int SEND_MESSAGE = 1031;

	public static final int HISTORY_PAGE_RESP = 1032;

	public static final int TRACK_LIST_ORDER_RESPONSE = 1033;

	public static final int USER_DETAIL_RESP_TWO = 1034;

	public static final int IN_APP_PURCHASE_RESP= 1035;

	public static final int SEARCH_USER_LIST_RESP = 1036;	


	//	public static final int TEST_RESP = 1111;

	//New Api Response Codes
	public static final String SUBSCRIPTION_EXPIRATION_TYPE = "expire_status";
	public static final String NEW_SUCCESS = "200"; 

	public static final String NEW_FAILED ="404"; 

	public static final String INVALID_USER_ID = "404";

	public static final String MAIL_SEND_FAILED = "412";

	public static final String PASSWORD_UPDATE_FAILED = "412";

	public static final String INVITE_DEACTIVATE_USER = "412";

	public static final String NO_RECOD_FOUND_SRC = "412";

	public static final String ALREADY_REGISTERED = "409";

	public static final String INVALID_LOGIN = "409";

	public static final String INVALID_TRACKID_HISTORY ="409";

	public static final String IMAGE_UPLOAD_FAILED = "409";

	public static final String RELATION_UPDATE_FAILED = "409";

	public static final String RESET_PASSWORD_FAILED = "409";

	public static final String ALREADY_ACTIVATED_USER = "409";

	public static final String REJECT_FAILED  ="409";

	public static final String BLOCK_FAILED = "409";

	public static final String QUERY_FAILED = "409";

	public static final String INVALID_TRACK_ID = "409";

	public static final String INVITE_ALREADY_BLOCKED = "409";

	public static final String EMPTY_VALUE = "204";

	public static final String TRACK_USER_REMOVE_FAILED = "204";

	public static final String OLD_PASSWORD_INVALID= "204";

	public static final String NO_TRACK_USER = "204";

	public static final String INVALID_REQUEST_ID = "204";

	public static final String DEACTIVATION_FAILED = "204";

	public static final String INVITE_FAILED = "204";

	public static final String NO_HISTORYDATE_RECORD = "204";

	public static final String USER_NOT_ACTIVATED = "204";

	public static final String ACCOUNT_DEACTIVATED = "204";

	public static final String UPDATE_FAILED = "204";

	public static final String ALREADY_FRIEND = "204";

	public static final String INVALID_EMAIL_ID = "422";

	public static final String EMPTY_TRACKID_HISTORY = "400";

	public static final String TRACK_ORDER_FAILED = "400";


	//List Adapter Codes
	public static final String USER_LIST = "2001";

	public static final String TRACK_LIST = "2002";

	// Json Response String

	public static final String REQUEST_ID = "request_id";

	public static final String USER_ID = "userId";

	public static final String SUCCESS_MESSAGE_TAG = "success";

	public static final String IMAGE_URL = "imageUrl";

	public static final String INVITE_STATUS = "inv_status";

	public static final String MESSAGE = "message";

	public static final String EMAIL = "email";

	public static final String PASSWORD = "password";

	public static final String STATUS = "status";

	public static final String STATUS_DURATION = "duration";

	public static final String STATUS_CODE = "statusCode";

	public static final String USER_DETAILS = "userDetails";

	public static final String USER_NAME = "firstname";

	public static final String TOTAL_COUNT = "TotalCount";

	public static final String USER = "user";

	public static final String ID = "id";

	public static final String USER_LAST_NAME = "lastname";

	public static final String EMAIL_ID = "emailid";

	public static final String PROF_IMG = "prof_image_path";

	public static final String LONGITUDE = "longitude";

	public static final String TIME_INTERVAL = "timeInterval";

	public static final String LATITUDE = "latitude";

	public static final String RELATIONSHIP = "relationship";

	public static final String DATE= "date";

	public static final String TIME = "time";

	public static final String PHONE_NUMBER  = "phoneno";

	public static final String GENDER  = "gender";

	public static final String DOB  = "dob";

	public static final String COUNTRY  = "country";	

	public static final String STATE  = "state";	

	public static final String CITY  = "city";	

	public static final String DATA  = "data";

	public static final String TRACK  = "track";

	public static final String AUTH_KEY = "authKey";

	public static final String EMAIL_ARRAY = "emailArray";

	public static final String PHONE_ARRAY = "phoneArray";

	public static final String USER_KEY = "userKey";

	public static final String ACCEPTED = "accepted";

	public static final String REJECTED = "rejected";

	public static final String BLOCKED = "blocked";

	public static final String SEARCH = "search";

	public static final String PAGE = "page";

	public static final String ANDROID_VERSION = "androidVersion";

	public static final String TRACK_USER_ID = "request_id";

	public static final String VERIFIED_STATUS = "verified_status";

	public static final String DESTINATION = "destination";

	public static final String GCM_REGID = "gcm_regid";

	public static final String REG_ID = "regid";

	public static final String PURCHASE_STATUS = "purchaseStatus";	

	public static final String VERSION="pro_version";

	public static final String PRO="pro";


	// Added by thiru phase III

	public static final String PROFILE_PUBLIC="profileStatus";

	public static final String Access_Token_PREF = "FBAccessToken";

	//New API for Search result
	public static final String NO_RECORD_FOUND = "409";

	public static final String NOT_AVAILABLE_IN_TRACKLIST = "204";

	//Shared Prefrences Constants

	public static final String AUTH_KEY_PREF = "authKey";

	public static final String USER_ID_PREF = "Userid";

	public static final String GPS_TRACKER_PREF = "GpsTrackerPref";

	public static final String INVITE_COUNT = "InviteCountPref";

	public static final String FREQ_UPDATE_PREF = "freqUpdate";

	public static final String USER_NAME_PREF = "username"; 

	public static final String USER_VERIFIED_STATUS_PREF = "userVerifiedPref";

	public static final String IS_SERVICE_ENABLED_PREF = "isServiceOn";

	public static final String USER_KEY_PREF = "UserKey";

	public static final String USER_TYPE_PREF = "userTypePref";



	//sAdded by thiru phase III
	/**
	 * Profile is Public ON or OFF  
	 */
	public static final String IS_ENABLED_PROFILE_PRIVACY = "isPublicOn";

	//Shared Preferences Constants-GCM


	public static final String GCM_FROM_PREF = "fromGCM";

	public static final String GCM_REGID_PREF = "gcm_regid_key";




	//Intent Constants

	public static final String EMAIL_KEY_INTENT = "emailkey";

	public static final String USER_LIST_POSITION_INTENT = "userListPosition";

	public static final String TRACK_USERID_INTENT = "trackUserId";

	public static final String USER_FIRST_NAME = "userFirstName";

	public static final String GCM_STATUS_INTENT="GCMstatus";


	//Intent Constants-GCM

	public static final String GCM_REGID_INTENT = "gcm_regid_key";

	public static final String GCM_REGID_TOUSER = "regid_touser";

	public static final String GCM_TO_INTENT = "toGCM";

	public static final String GCM_TO_SHOW_MSG = "toShowMsg";

	public static final String GCM_EMAIL_TO_USER = "emailToUser";

	//Intent Constant - GCM Invite
	public static final String GCM_INVITE_MAIN_FGMT_BUNDLE_KEY = "gcm_invite_main_fgmt_bundle_key";
	public static final String GCM_INVITE_INVITE_PAGE_BUNDLE_KEY = "gcm_invite_invite_page_bundle_key";

	//Toast Message Constants

	public static final String TOAST_NO_RESPONSE = "No Response From Server";

	public static final String TOAST_REQUEST_IS_ACCEPTED= "Request is accepted";

	public static final String TOAST_REQUEST_REJECTED = "Request is Rejected";

	public static final String TOAST_BLOCKED_IS_ACCEPTED= "Blocked Successfully";

	public static final String TOAST_NO_INTERNET_CONNECTION ="No Internet Connection"; 

	//Activity Names
	public static final String SHOW_MESSAGE_ACTIVITY = "ShowMessage";

	public static final String HOME_DETAIL_ACTIVITY = "HomeDetail";

	public static final String EMPTY_ACTIVITY = "";

	//RelationShip
	public static final String[] RELATIONSHIP_STATE = {"Other","Father", "Mother", "Son", "Daughter", "Friend"};

	// Regex Pattern

	public static final String EMAIL_ID_REGEX  = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"; 

	public static final String NON_ALPHABETS_REGERX = "[a-zA-Z]";
}
