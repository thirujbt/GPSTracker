package com.gpsmobitrack.gpstracker.database;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gpsmobitrack.gpstracker.chat.Config;

public class DBHandler extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "UserProfile.db";
	public static final String PROFILE_TABLE_NAME = "profile";
	public static final String PROFILE_COLUMN_FIRST_NAME = "first_name";
	public static final String PROFILE_COLUMN_LAST_NAME = "last_name";
	public static final String PROFILE_COLUMN_PHONE_NO = "phone_no";
	public static final String PROFILE_COLUMN_EMAIL_ID = "email_id";
	public static final String PROFILE_COLUMN_GENDER = "gender";
	public static final String PROFILE_COLUMN_DOB = "dob";
	public static final String PROFILE_COLUMN_STATE = "state";
	public static final String PROFILE_COLUMN_COUNTRY = "country";
	public static final String PROFILE_COLUMN_PHOTO = "photo";
	public static final int DATABASE_VERSION = 1;
	public static final String CHAT_TABLE_NAME = "chat";
	public static final String CHAT_DATE_TIME ="date_time";
	public static final String CHAT_NAME = "chat_name";
	public static final String CHAT_MSG = "msg";
	public static final String CHAT_EMAIL_ID = "email_id";
	public static final String CHAT_IS_MINE = "ismine";
	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + PROFILE_TABLE_NAME + " ("
				+ PROFILE_COLUMN_FIRST_NAME + " text, "
				+ PROFILE_COLUMN_LAST_NAME + " text, "
				+ PROFILE_COLUMN_PHONE_NO + " text, "
				+ PROFILE_COLUMN_EMAIL_ID + " text, "
				+ PROFILE_COLUMN_GENDER + " text, "
				+ PROFILE_COLUMN_DOB + " text, "
				+ PROFILE_COLUMN_STATE + " text, "
				+ PROFILE_COLUMN_COUNTRY + " text, "
				+ PROFILE_COLUMN_PHOTO + " text)");
		db.execSQL("create table "+ CHAT_TABLE_NAME +" ("
				+ CHAT_NAME + " text, "
				+ CHAT_EMAIL_ID + " text, "
				+ CHAT_DATE_TIME + " text, "
				+ CHAT_IS_MINE + " int, "
				+ CHAT_MSG + " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+PROFILE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+CHAT_TABLE_NAME);
		onCreate(db);
	}

	public void deleteProfile() {
		SQLiteDatabase database = this.getWritableDatabase();
		database.delete(PROFILE_TABLE_NAME, null, null);
		database.delete(CHAT_TABLE_NAME, null, null);
	}

	public boolean updateProfile(String firstName, String lastName,
			String phoneNo, String emailId, String gender, String dob,
			String state, String country, String photo) {

		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_COLUMN_FIRST_NAME, firstName);
		contentValues.put(PROFILE_COLUMN_LAST_NAME, lastName);
		contentValues.put(PROFILE_COLUMN_PHONE_NO, phoneNo);
		contentValues.put(PROFILE_COLUMN_EMAIL_ID, emailId);
		contentValues.put(PROFILE_COLUMN_GENDER, gender);
		contentValues.put(PROFILE_COLUMN_DOB, dob);
		contentValues.put(PROFILE_COLUMN_STATE, state);
		contentValues.put(PROFILE_COLUMN_COUNTRY, country);
		contentValues.put(PROFILE_COLUMN_PHOTO, photo);
		database.update(PROFILE_TABLE_NAME, contentValues, null, null);
		return true;
	}

	public boolean updateFirstName(String firstName){

		SQLiteDatabase database = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_COLUMN_FIRST_NAME, firstName);
		database.update(PROFILE_TABLE_NAME, contentValues, null, null);
		return true;
	}


	public boolean updateProfileImage(String photo) {

		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_COLUMN_PHOTO, photo);
		database.update(PROFILE_TABLE_NAME, contentValues, null, null);
		return true;
	}

	public boolean insertProfile(String firstName, String lastName,
			String phoneNo, String emailId, String gender, String dob,
			String state, String country, String photo) {

		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_COLUMN_FIRST_NAME, firstName);
		contentValues.put(PROFILE_COLUMN_LAST_NAME, lastName);
		contentValues.put(PROFILE_COLUMN_PHONE_NO, phoneNo);
		contentValues.put(PROFILE_COLUMN_EMAIL_ID, emailId);
		contentValues.put(PROFILE_COLUMN_GENDER, gender);
		contentValues.put(PROFILE_COLUMN_DOB, dob);
		contentValues.put(PROFILE_COLUMN_STATE, state);
		contentValues.put(PROFILE_COLUMN_COUNTRY, country);
		contentValues.put(PROFILE_COLUMN_PHOTO, photo);
		database.insert(PROFILE_TABLE_NAME, null, contentValues);
		return true;
	}

	public Cursor getProfile() {
		Cursor cursor=null;
		try{
			if(this!=null){
		SQLiteDatabase database = this.getReadableDatabase();
		
		cursor = database.rawQuery(
				"select * from " + PROFILE_TABLE_NAME, null);
		}}
		catch(Exception ex){}
		return cursor;
	
	}
	public boolean storeChatMsg(String name,String date_time, String msg, String email, boolean isMine) {
		SQLiteDatabase mDatabase = this.getWritableDatabase();
		ContentValues mValues = new ContentValues();
		mValues.put(CHAT_NAME, name);
		mValues.put(CHAT_EMAIL_ID, email);
		mValues.put(CHAT_DATE_TIME,date_time);
		mValues.put(CHAT_MSG, msg);
		mValues.put(CHAT_IS_MINE, (isMine) ? 1 : 0);
		long res = mDatabase.insert(CHAT_TABLE_NAME, null, mValues);
		if(res > 0) 
			return true;  
		else 
			return false;
	}

	public Cursor getChatMsg(String emailid) {
		SQLiteDatabase mDatabase = this.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("select "+CHAT_MSG+", "+CHAT_DATE_TIME +", "+CHAT_IS_MINE +" from "
				+CHAT_TABLE_NAME+ " WHERE "+ CHAT_EMAIL_ID+" = ? ", new String[] {emailid});
		return cursor;
	}

	public String getDateTime() {
		Date mDate = new Date();
		Date newDate = new Date(mDate.getTime() - 2 * 24 * 3600 * 1000);
		return Config.mFormat.format(newDate);
	}

	@SuppressWarnings("unused")
	public void deleteChatHistoryByUser(String emailid) {
		SQLiteDatabase mDatabase = this.getReadableDatabase();
		Cursor cursor = mDatabase.rawQuery("delete from "
				+CHAT_TABLE_NAME+ " WHERE "+ CHAT_EMAIL_ID+" = ?", new String[] {emailid});
		long res = mDatabase.delete(CHAT_TABLE_NAME, CHAT_EMAIL_ID+" = ?", new String[] {emailid});

	}

	@SuppressWarnings("unused")
	public void deleteChatHistoryByExpired() {
		SQLiteDatabase mDatabase = this.getReadableDatabase();
		/*Cursor cursor = mDatabase.rawQuery("delete from "
				+CHAT_TABLE_NAME+ " WHERE "+ CHAT_DATE_TIME+" <= ?", new String[] {getDateTime()});*/
		long res = mDatabase.delete(CHAT_TABLE_NAME, CHAT_DATE_TIME+" = ?", new String[] {getDateTime()});
	}

	@SuppressWarnings("unused")
	public void deleteChatHistory() {
		SQLiteDatabase mDatabase = this.getReadableDatabase();
		/*Cursor cursor = mDatabase.rawQuery("delete from "
				+CHAT_TABLE_NAME+ " WHERE "+ CHAT_DATE_TIME+" <= ?", new String[] {getDateTime()});*/
		long res = mDatabase.delete(CHAT_TABLE_NAME, null, null);
	}

}
