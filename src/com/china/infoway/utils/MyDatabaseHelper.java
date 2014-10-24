package com.china.infoway.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	// ���峣��������������ֶ���
	public static final String DATA_NAME = "myNote.db";
	public static final int DATA_VERSION = 1;
	public static final String TABLE_NAME1 = "note";
	public static final String COL_ID = "id";
	public static final String COL_COMPANYNAME = "company_name";
	public static final String COL_CONTACTPERSON = "contact_person";
	public static final String COL_CONTACTPHONE = "contact_phone";
	public static final String COL_PENDINGTHINGS = "pending_things";
	public static final String COL_WORKRESULTS = "work_results";
	public static final String COL_REMARK = "other_remark";
	public static final String COL_IMAGE = "image";
	public static final String COL_VOICE = "voice";
	public static final String COL_TIME = "duartime";
	public static final String COL_LATITUDE = "latitude";// γ��
	public static final String COL_LONGITUDE = "longitude";// ����
	public static final String COL_TIME2 = "date_time";
	public static final String COL_COUNT = "btn_count";// ��¼"�ύ"��ť������Ĵ���
	public static final String COL_STATUE="statue";//ǩ���ɹ����������ص�ֵ
	public static final String COL_ADDR = "addr";
	
	// ����ʵ�ָù�����
	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATA_NAME, null, DATA_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		// ������
		System.out.println("onCreate......");
		Log.i("dbCreate", "create...");
		// ִ�н������
		String sql = "create table note("
				+ "_id integer primary key autoincrement,"
				+ "company_name text,contact_person text,contact_phone text,pending_things text,"
				+ "work_results text,other_remark text,image text,voice text,duartime text,latitude text," +
				"longitude text,date_time text,btn_count integer,statue integer, addr text)";
		db.execSQL(sql);
		
		/*// ִ�н������
		String sql2 = "create table message("
				+ "_id integer primary key autoincrement,"
				+ "message_title text,message_content text,message_time text,message_img text,"
				+ "message_music text,click_count integer)";
		db.execSQL(sql2);*/
		
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME1;
		db.execSQL(sql);
		
		/*String sql2 = "DROP TABLE IF EXISTS  message" ;
		db.execSQL(sql2);*/
	}
}
