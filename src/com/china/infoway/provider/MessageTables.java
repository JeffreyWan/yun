package com.china.infoway.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * ��Ϣ������
 */
public final class MessageTables implements BaseColumns {
	// ��Ȩ����
	public static final String AUTHORITY = "com.amaker.provider.AA";
	// ����Uri
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/aa");
	// Ĭ��������
	public static final String DEFAULT_SORT_ORDER = "message_time DESC";
	// ���ֶγ���
	public static final String TITLE = "message_title";
	public static final String CONTENT = "message_content";
	public static final String TIME = "message_time";
	public static final String IMAGE = "message_img";
	public static final String MUSIC = "message_music";
	public static final String COUNT = "click_count";
}
