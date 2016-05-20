package com.pickzy.moresdk.moredb;

public class Paid_DB_Pojo {

	static String stringss;


	public Paid_DB_Pojo() {

	}

	public Paid_DB_Pojo( String strings) {
		stringss = strings;
	}


	public static String getStringss() {
		return stringss;
	}

	public static void setStringss(String stringss) {
		Paid_DB_Pojo.stringss = stringss;
	}
}