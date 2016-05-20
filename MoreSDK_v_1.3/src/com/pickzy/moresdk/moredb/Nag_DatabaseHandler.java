package com.pickzy.moresdk.moredb;

import android.content.ContentValues;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Nag_DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "pickzymoreappsNaG";
	private static final String TABLE_DETAILS = "Nagapplist";
	private static final String KEY_ID = "id";
	private static final String STRINGS = "string";
	
	public static String[]  Strings;
	public Nag_DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DETAILS +"("
				+ KEY_ID + " INTEGER PRIMARY KEY,"+  STRINGS +" TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
		onCreate(db);
	}
	public void addContact(Nag_DB_Pojo contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STRINGS, contact.getStringss()); // Contact Name
		db.insert(TABLE_DETAILS, null, values);
		db.close();// Closing database connection
	}
	public void DeleteRow(String row){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DETAILS, row, null);
//		db.delete(TABLE_DETAILS, TIME + "=?", new String[] { row });
	}
	public Nag_DB_Pojo getContact(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DETAILS, new String[] { KEY_ID,
				STRINGS}, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Nag_DB_Pojo contact = new Nag_DB_Pojo(cursor.getString(1));
		return contact;
	}
	public int getAllContacts(){
		int count =getContactsCount();
		String selectQuery = "SELECT  * FROM " + TABLE_DETAILS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Strings = new String[count];
		int i=0;
		int j=0;
		if (cursor.moveToFirst()) {
			do{
				Strings[i] = cursor.getString(1);
				j=i;
				i++;
			}while (cursor.moveToNext());
		}
		return i;
	}
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_DETAILS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		return cursor.getCount();
	}
}
