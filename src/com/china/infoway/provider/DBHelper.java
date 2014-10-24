package com.china.infoway.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库工具类
 */
public class DBHelper extends SQLiteOpenHelper{
    // 数据库名称常量
    public static final String DATABASE_NAME = "aa.db";
    // 数据库版本常量
    public static final int DATABASE_VERSION = 1;
    // 表名称常量
    public static final String TABLES_AA = "aa";
	public static String COUNT;
	// 构造方法
	public DBHelper(Context context,String name,
			CursorFactory factory, int version) {
		// 创建数据库
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}

	// 创建时调用
	public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLES_AA + " ("
                + MessageTables._ID + " integer,"
                + MessageTables.COUNT + " integer,"
                + MessageTables.TITLE + " varchar(50),"
                + MessageTables.CONTENT + " varchar(200),"
                + MessageTables.IMAGE + " varchar(200),"
                + MessageTables.MUSIC + " varchar(200),"
                + MessageTables.TIME + " varchar(200) primary key"
                + ");");
	}

	// 版本更新时调用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 删除表
		db.execSQL("DROP TABLE IF EXISTS aa");
		java.lang.System.out.print("DROP TABLE IF EXISTS aa");
		onCreate(db);
	}
	



}
