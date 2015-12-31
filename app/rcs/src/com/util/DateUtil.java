package com.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	public static final String DATETIME_FORMAT_1 = "yyyyMMddHHmmss";
	public static final String DATETIME_FORMAT_4 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_FORMAT_2 = "yyyyMMdd";
	public static final String DATETIME_FORMAT_3 = "yyyy-MM-dd";
	public static final String TIME_FORMAT_1 = "MMddHHmm";
	public static final String TIME_FORMAT_2 = "HHmmss";

	public static final String NUMPERPAGE = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
	public static final String PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");

	public static String getCurrentDateTime() {
		return formatCurrentDateTime("yyyyMMddHHmmss");
	}

	public static String getCurrentDate() {
		return formatCurrentDateTime("yyyyMMdd");
	}

	public static String getCurrentDate2() {
		return formatCurrentDateTime("yyyy-MM-dd");
	}

	public static String getCurrentTime() {
		return formatCurrentDateTime("MMddHHmm");
	}

	public static String getCurrentTime2() {
		return formatCurrentDateTime("HHmmss");
	}

	public static String formatCurrentDateTime(String formatText) {
		Calendar calendar = Calendar.getInstance();
		return convertDateToStr(calendar.getTime(), formatText);
	}

	public static String convertDateToStr(Date date, String pattern) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String convertDateFormat(String date1) {
		if (date1 == null) {
			return date1;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date2 = null;
		try {
			date2 = formatter.parse(date1);
		} catch (ParseException e) {
			return date1;
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date3 = formatter.format(date2);
		return date3;
	}

	public static String formatDateTOD3(String date) {
		Date d1 = new Date();
		try {
			if ((date != null) || (!date.equals(""))) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				d1 = df.parse(date);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return d1.toString();
	}

	public static String formmat10to8(String date1) {

		if (date1 == null) {
			return date1;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date2 = null;
		try {
			date2 = formatter.parse(date1);
		} catch (ParseException e) {
			return date1;
		}
		formatter = new SimpleDateFormat("yyyyMMdd");
		String date3 = formatter.format(date2);
		return date3;
	}

	/**
	 * 返回两个时间差 单位秒
	 * 
	 * @param lastTime
	 * @param thisTime
	 * @return
	 * @throws ParseException
	 */
	public static Long getTimeR(String lastTime, String thisTime)
			throws ParseException {

		Long a = new SimpleDateFormat(DATETIME_FORMAT_1).parse(lastTime)
				.getTime() / 1000;

		Long b = new SimpleDateFormat(DATETIME_FORMAT_1).parse(thisTime)
				.getTime() / 1000;

		return a - b;
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @param str1
	 *            时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2
	 *            时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}

	/**
	 * @param str 定义今天日期格式  如【yyyyMMddHHmmss】
	 * @return
	 */
	public static String getTodayFormat(String str) {
		// 定义返回日期格式
		if (str == null || "".equals(str.trim())){
			str = DATETIME_FORMAT_2;
		}
		
		Date d = new Date();
		
		SimpleDateFormat formatter = new SimpleDateFormat(str);
		
		return formatter.format(d);
	}
}