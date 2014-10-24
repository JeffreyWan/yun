package com.china.infoway.utils;

public class Constants {

	 public static final String URL = "http://123.57.39.40/www.yundingwei.com/";
//	 public static final String URL = "http://192.168.0.109/www.yundingwei.com/";
//	public static final String URL = "http://192.168.0.105:88/";
	public static final String PicURL = URL + "Public/Upload/Image/";
	public static final String VoiURL = URL + "Public/Upload/Video/";

	public static final String Uri = URL + "u.php?m=Api";
	public static final String informationUri = Uri
			+ "&a=report&work_addressid=";
	public static final String imageActionUrl = Uri + "&a=uploadImage";
	public static final String musicActionUrl = Uri + "&a=uploadVideo";
	public static final String loginUri = Uri + "&a=Login";
	public static final String verUrl = Uri + "&a=checkVersion";
	public static final String URIAPI = Uri + "&a=getMessage";
	public static final String picAndVoiUrl = Uri + "&a=upload";
	public static final String downloadUri = URL + "Public/Upload/Down/yun.apk";
	public static final String positionUri = Uri
			+ "&a=position&a_infophone=";
	public static final String registerUri = Uri
			+ "bind.insert.php?bind_phone=";
	public static final String passwordUri = Uri + "&a=updatePwd&phone=";
	public static final String setTimeUri = Uri + "&a=setTime&phone=";
	public static final int Success = 0;
	public static final int Failure = 1;
	public static final int CheckVersion = 2;
	
	public static final String PREFS_NAME = "phone_state";
	
	public static final String PREFS_KEY_PHONE_NUMBER = "phone_number";
	public static final String PREFS_KEY_PHONE_PASSWORD = "phone_password";
	public static final String PREFS_KEY_AUTO_LOGIN = "auto_login";
	public static final String PREFS_KEY_SAVE_PASSWORD = "save_password";
	public static final String PREFS_KEY_COMPANY_NAME = "company_name";
}
