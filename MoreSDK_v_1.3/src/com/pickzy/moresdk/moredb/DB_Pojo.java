package com.pickzy.moresdk.moredb;

public class DB_Pojo {

	static String stringss;


	public DB_Pojo() {

	}

	public DB_Pojo( String strings) {
		stringss = strings;
	}


	public static String getStringss() {
		return stringss;
	}

	public static void setStringss(String stringss) {
		DB_Pojo.stringss = stringss;
	}
}