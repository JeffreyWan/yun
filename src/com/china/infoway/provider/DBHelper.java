package com.china.infoway.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ���ݿ⹤����
 */
public class DBHelper extends SQLiteOpenHelper{
    // ���ݿ����Ƴ���
    public static final String DATABASE_NAME = "aa.db";
    // ���ݿ�汾����
    public static final int DATABASE_VERSION = 1;
    // �����Ƴ���
    public static final String TABLES_AA = "aa";
	public static String COUNT;
	// ���췽��
	public DBHelper(Context context,String name,
			CursorFactory factory, int version) {
		// �������ݿ�
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}

	// ����ʱ����
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

	// �汾����ʱ����
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ɾ����
		db.execSQL("DROP TABLE IF EXISTS aa");
		java.lang.System.out.print("DROP TABLE IF EXISTS aa");
		onCreate(db);
	}
	



}
