package com.china.infoway.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TableProvider extends ContentProvider {
	// 数据库帮助类
	private DBHelper dbHelper;
	// Uri工具类
	private static final UriMatcher sUriMatcher;
	// 查询、更新条件
	private static final int AA = 1;
	private static final int AA_ID = 2;
	private Cursor cursor;
	// 查询列集合
	private static HashMap<String, String> tblProjectionMap;
	static {
		// Uri匹配工具类
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(MessageTables.AUTHORITY, "aa", AA);
		sUriMatcher.addURI(MessageTables.AUTHORITY, "aa/#", AA_ID);
		// 实例化查询列集合
		tblProjectionMap = new HashMap<String, String>();
		// 添加查询列
		tblProjectionMap.put(MessageTables._ID, MessageTables._ID);
		tblProjectionMap.put(MessageTables.TITLE, MessageTables.TITLE);
		tblProjectionMap.put(MessageTables.CONTENT, MessageTables.CONTENT);
		tblProjectionMap.put(MessageTables.TIME, MessageTables.TIME);
		tblProjectionMap.put(MessageTables.IMAGE, MessageTables.IMAGE);
		tblProjectionMap.put(MessageTables.MUSIC, MessageTables.MUSIC);
		tblProjectionMap.put(MessageTables.COUNT, MessageTables.COUNT);
	}

	// 创建是调用
	public boolean onCreate() {
		// 实例化数据库帮助类
		dbHelper = new DBHelper(getContext(), null, null, 0);
		return true;
	}

	// 添加方法
	public Uri insert(Uri uri, ContentValues values) {
		// 获得数据库实例
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// 插入数据，返回行ID
		long rowId = db.insert(DBHelper.TABLES_AA, null, values);
		// 如果插入成功返回uri
		if (rowId > 0) {
			Uri empUri = ContentUris.withAppendedId(MessageTables.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(empUri, null);
			return empUri;
		}
		return null;
	}

	// 获得类型
	public String getType(Uri uri) {
		return null;
	}

	public boolean deleteTb(String title) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from aa where message_title =" + title;
		try {
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			Log.e("delete err", "delete " + title + " err");
			return false;
		}
	}

	// 查询方法
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		// 查询所有
		case AA:
			qb.setTables(DBHelper.TABLES_AA);
			qb.setProjectionMap(tblProjectionMap);
			break;
		// 根据ID查询
		case AA_ID:
			qb.setTables(DBHelper.TABLES_AA);
			qb.setProjectionMap(tblProjectionMap);
			qb.appendWhere(MessageTables._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Uri错误！ " + uri);
		}

		// 使用默认排序
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = MessageTables.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		// 获得数据库实例
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 返回游标集合
		cursor = qb.query(db, projection, selection, selectionArgs, null, null,
				orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// 获得数据库实例
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int n = db.delete(DBHelper.TABLES_AA, selection, selectionArgs);
		System.out.println(n);
		return n;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
