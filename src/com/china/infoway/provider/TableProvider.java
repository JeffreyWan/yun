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
	// ���ݿ������
	private DBHelper dbHelper;
	// Uri������
	private static final UriMatcher sUriMatcher;
	// ��ѯ����������
	private static final int AA = 1;
	private static final int AA_ID = 2;
	private Cursor cursor;
	// ��ѯ�м���
	private static HashMap<String, String> tblProjectionMap;
	static {
		// Uriƥ�乤����
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(MessageTables.AUTHORITY, "aa", AA);
		sUriMatcher.addURI(MessageTables.AUTHORITY, "aa/#", AA_ID);
		// ʵ������ѯ�м���
		tblProjectionMap = new HashMap<String, String>();
		// ��Ӳ�ѯ��
		tblProjectionMap.put(MessageTables._ID, MessageTables._ID);
		tblProjectionMap.put(MessageTables.TITLE, MessageTables.TITLE);
		tblProjectionMap.put(MessageTables.CONTENT, MessageTables.CONTENT);
		tblProjectionMap.put(MessageTables.TIME, MessageTables.TIME);
		tblProjectionMap.put(MessageTables.IMAGE, MessageTables.IMAGE);
		tblProjectionMap.put(MessageTables.MUSIC, MessageTables.MUSIC);
		tblProjectionMap.put(MessageTables.COUNT, MessageTables.COUNT);
	}

	// �����ǵ���
	public boolean onCreate() {
		// ʵ�������ݿ������
		dbHelper = new DBHelper(getContext(), null, null, 0);
		return true;
	}

	// ��ӷ���
	public Uri insert(Uri uri, ContentValues values) {
		// ������ݿ�ʵ��
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// �������ݣ�������ID
		long rowId = db.insert(DBHelper.TABLES_AA, null, values);
		// �������ɹ�����uri
		if (rowId > 0) {
			Uri empUri = ContentUris.withAppendedId(MessageTables.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(empUri, null);
			return empUri;
		}
		return null;
	}

	// �������
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

	// ��ѯ����
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		// ��ѯ����
		case AA:
			qb.setTables(DBHelper.TABLES_AA);
			qb.setProjectionMap(tblProjectionMap);
			break;
		// ����ID��ѯ
		case AA_ID:
			qb.setTables(DBHelper.TABLES_AA);
			qb.setProjectionMap(tblProjectionMap);
			qb.appendWhere(MessageTables._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Uri���� " + uri);
		}

		// ʹ��Ĭ������
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = MessageTables.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		// ������ݿ�ʵ��
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// �����α꼯��
		cursor = qb.query(db, projection, selection, selectionArgs, null, null,
				orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// ������ݿ�ʵ��
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
