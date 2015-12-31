package com.tangdi.risk.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tangdi.engine.context.Log;


public final class DateUtil {
	public static final String DATETIME_FORMAT_1 = "yyyyMMddHHmmss";
	public static final String DATETIME_FORMAT_4 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_FORMAT_2 = "yyyyMMdd";
	public static final String DATETIME_FORMAT_3 = "yyyy-MM-dd";
	public static final String TIME_FORMAT_1 = "MMddHHmm";
	public static final String TIME_FORMAT_2 = "HHmmss";

	public synchronized static String getSingeNo(){
		return System.currentTimeMillis()+"";
	}
	 
	
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
			Log.error(ex, "传入日期不合法："+date);
		}
		return d1.toString();
	}

	/**
	 * 说明：日期10位(带“-”或“/”)转换成8位(不带符号)
	 *       若输入为空或不是10位则返回当天日期(8位不带符号)
	 * @param date1
	 * @return
	 * @throws ParseException
	 */
	public static String formmat10to8(String date1){

		if (date1 == null) {
			return getTodayFormat(null);
		}
		
		if (date1.length() == 10
				&& (date1.contains("-") || date1.contains("/"))) {
			
			date1 = date1.replace("-", "");
			date1 = date1.replace("/", "");
			
		} else if (date1.length() == 8) {
			return date1;
		} else {
			return getTodayFormat(null);
		}

		return date1;
	}
	
	/**
	 * 返回两个时间差 单位秒
	 * 大时间 在前，小时间在后
	 * @param lastTime
	 * @param thisTime
	 * @return
	 * @throws ParseException
	 */
	public static Long getTimeR(String MaxlastTime, String MinthisTime){
		if(MaxlastTime == null || MaxlastTime.trim().length()==0){
			return 0L;
		}
		
		if(MinthisTime == null || MinthisTime.trim().length()==0){
			return 0L;
		}
		
		long a = 0L;
		try {
			a = new SimpleDateFormat(DATETIME_FORMAT_1).parse(MaxlastTime).getTime() / 1000;
		} catch (ParseException e) {
			Log.error(e, "DateUtil.getTimeR");
		}

		long b = 0L;
		try {
			b = new SimpleDateFormat(DATETIME_FORMAT_1).parse(MinthisTime).getTime() / 1000;
		} catch (ParseException e) {
			Log.error(e, "DateUtil.getTimeR");
		}
		return a - b;
	}
	
	/**
	 * 返回 nowTime + addTime 后的时间
	 * @param nowTime 14位有效数字
	 * @param addTime 秒
	 * @return
	 */
	public static String getTimeLater(String nowTime, String addTime) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT_1);
		long a = 0L;
		try {
			a = formatter.parse(nowTime).getTime();
		} catch (ParseException e) {
			Log.error(e, "DateUtil.getTimeLater");
		}

		a = a + Long.valueOf(addTime)*1000;

		Date d = new Date(a);
		formatter.format(d);

		return formatter.format(d);
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
			Log.error(e, "日期格式错误！");
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
	
	/**
	 * 将日期字符串转化成int型，用于数值比较
	 */
	public static String initDate(String date){
		String d = null;
		if (Tools.isNull(date)) {
			d = DateUtil.getTodayFormat(null);
		} else {
			d = date;
		}
		return d;
	}
	 

}