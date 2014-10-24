package com.china.infoway.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PhoneInfoHelper extends SQLiteOpenHelper {

	// 定义常量来保存表名和字段名
	public static final String PHONEINFO_DATA_NAME = "phoneinfo.db";
	public static final int PHONEINFO_DATA_VERSION = 1;
	public static final String PHONEINFO_TABLE_NAME = "phoneinfo";
	public static final String PHONEINFO_COL_ID = "id";
	public static final String PHONEINFO_COL_PHONENNUM = "phonenum";
	public static final String PHONEINFO_COL_AM_START = "am_start";
	public static final String PHONEINFO_COL_AM_END = "am_end";
	public static final String PHONEINFO_COL_AM_TIME = "am_time";
	public static final String PHONEINFO_COL_PM_START = "pm_start";
	public static final String PHONEINFO_COL_PM_END = "pm_end";
	public static final String PHONEINFO_COL_PM_TIME = "pm_time";
	public static final String CHREATE_TIME = "create_time";

	public PhoneInfoHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, PHONEINFO_DATA_NAME, null, PHONEINFO_DATA_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table phoneinfo("
				+ "_id integer primary key autoincrement,"
				+ "phonenum text,am_start text,am_end text,"
				+ "am_time text,pm_start text,pm_end text, pm_time text, create_time text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + PHONEINFO_TABLE_NAME;
		db.execSQL(sql);
	}

}
