package com.china.infoway.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

	private static long startTime;
	private static long endTime;

	/**
	 * 绠楁硶璁℃椂寮�
	 */
	public static void algorithmStart() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * 杩斿洖绠楁硶娑堣�鐨勬椂闂达紙eg1.2绉掞級
	 * 
	 * @return
	 */
	public static String algorithmFinish() {
		// DecimalFormat df = new DecimalFormat("#.#");
		endTime = System.currentTimeMillis();
		return String.valueOf(((endTime - startTime) / 1000.0));
	}

	public static String Date2String(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
			return sdf.format(date);
		}
		return "";
	}

	public static Date String2Date(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		Date currentdate = null;
		try {
			currentdate = sdf.parse(date);
		} catch (ParseException e) {
		}
		return currentdate;
	}

	public static Date Date2Date(Date date) {
		return String2Date(Date2String(date));
	}

	public static String Date2String(Date date, String pattern) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
			return sdf.format(date);
		}
		return "";
	}

	public static Date String2Date(String date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
		Date currentdate = null;
		if (date == null || date.trim().equals("")) {
			return null;
		}
		try {
			currentdate = sdf.parse(date);
		} catch (ParseException e) {
		}
		return currentdate;
	}

	public static Date Date2Date(Date date, String pattern) {
		return String2Date(Date2String(date, pattern), pattern);
	}

	/**
	 * 鍒ゆ柇鏄惁绗﹀悎鏃ユ湡瑙勮寖
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static boolean isValidDate(String s, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (s == null || s.trim().equals("")) {
			return true;
		}
		try {
			return sdf.format(sdf.parse(s)).equals(s);
		} catch (Exception e) {
			return false;
		}
	}

	public static Long StringToLong(String s) {
		if ("".equals(s) || null == s) {
			return null;
		} else {
			return Long.parseLong(s);
		}
	}

	public static Integer StringToInt(String s) {
		if ("".equals(s) || null == s) {
			return null;
		} else {
			return Integer.parseInt(s);
		}
	}

	/**
	 * 鑾峰緱涓や釜鏃ユ湡涔嬮棿鐨勬墍鏈夋棩鏈�
	 */
	public static Date[] getDateArrays(Date start, Date end, int calendarType) {
		List<Date> ret = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		Date tmpDate = calendar.getTime();
		long endTime = end.getTime();
		while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
			ret.add(calendar.getTime());
			calendar.add(calendarType, 1);
			tmpDate = calendar.getTime();
		}
		Date[] dates = new Date[ret.size()];
		return (Date[]) ret.toArray(dates);
	}

	/**
	 * 鑾峰彇鏄庡ぉ鐨勬棩鏈�
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date getTomorrow(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date tomorrow = new Date(date.getTime() + 1000 * 60 * 60 * 24);
		String strDate = df.format(tomorrow);
		try {
			tomorrow = df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tomorrow;
	}
	
	public static Date getDefineDay(Date date, int num){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date defineDay = new Date(date.getTime() + 1000 * 60 * 60 * 24 * num);
		String strDate = df.format(defineDay);
		try {
			defineDay = df.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return defineDay;
	}

	/**
	 * 鑾峰緱浠婂ぉ鐨�0:00鍒�
	 */
	public static Date getZeroOfToday() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 鑾峰緱鏌愪竴澶╃殑00:00鍒�
	 */
	public static Date getZeroOfOneday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 鑾峰緱鏌愪竴澶╃殑00:01鍒�
	 */
	public static Date getFirstMinuteOfOneday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 鑾峰緱浠婂ぉ鐨�3:59鍒�
	 */
	public static Date getLatestTimeOfToday() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 鑾峰緱鏌愪竴澶╃殑23:59鍒�
	 */
	public static Date getLatestTimeOfOneday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 鑾峰彇i灏忔椂涔嬪悗鐨勬椂闂�
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateAfterHour(Date date, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
		return now.getTime();
	}

	/**
	 * 鑾峰彇i灏忔椂涔嬪墠鐨勬椂闂�
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateBeforeHour(Date date, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.HOUR, now.get(Calendar.HOUR) - hour);
		return now.getTime();
	}

	/**
	 * 鍒ゆ柇鏃ユ湡鏄惁鍦�涓棩鏈熶箣闂�
	 * 
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return 濡傛灉绛変簬寮�鏃堕棿鎴栬�缁撴潫鏃堕棿锛屼篃杩斿洖true
	 */
	public static boolean isDateDuringPeriod(Date date, Date startDate, Date endDate) {
		if (date.equals(startDate) || date.equals(endDate))
			return true;
		if (date.before(endDate) && date.after(startDate))
			return true;
		return false;
	}

	/**
	 * date1鏄惁姣攄ate2鏃�
	 * 
	 * @param date1
	 * @param date2
	 *            濡傛灉date1鏄痭ull
	 * @return
	 */
	public static boolean isBefore(Date date1, Date date2) {
		boolean isBefore = false;
		if (date1 != null && date2 == null) {
			isBefore = true;
		}

		if (date1 != null && date2 != null) {
			isBefore = date1.before(date2);
		}

		if (date1 == null && date2 != null) {
			isBefore = false;
		}

		if (date1 == null && date2 == null) {
			isBefore = false;
		}

		// try{
		// isBefore = date1.before(date2);
		// }catch(NullPointerException e){
		// return isBefore;
		// }
		return isBefore;
	}
	
	/**
	 * 鑾峰彇涓や釜鏃ユ湡涔嬮棿鐩稿樊灏忔椂鏁�
	 */
	public static double getDifferHour(Date start, Date end) {
		if (start == null || end == null) {
			return 0;
		}
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(start);
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(end);
		double hour = ((double) calendarEnd.getTimeInMillis() - (double) calendarStart.getTimeInMillis()) / (1000 * 60 * 60);
		return hour;
	}

	public static boolean isAfter(Date date1, Date date2) {
		boolean isAfter = false;
		if (date1 != null && date2 == null) {
			isAfter = true;
		}

		if (date1 != null && date2 != null) {
			isAfter = date1.after(date2);
		}

		if (date1 == null && date2 != null) {
			isAfter = false;
		}

		if (date1 == null && date2 == null) {
			isAfter = false;
		}
		//
		// try{
		// isAfter = date1.after(date2);
		// }catch(NullPointerException e){
		// return isAfter;
		// }
		return isAfter;
	}
	
	/**
	 * 鑾峰緱绗簩澶╃殑23:59鍒�
	 */
	public static Date getLatestTimeOfTomorrow() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND,59);
		calendar.set(Calendar.MILLISECOND,59);
		return calendar.getTime();
	}
}
