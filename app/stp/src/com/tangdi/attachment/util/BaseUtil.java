package com.tangdi.attachment.util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

/**
 * 系统公用函数管理
 * 棠棣科技
 * @author Administrator
 */
public class BaseUtil {

	public static final String DATETIME_FORMAT_1 = "yyyyMMddHHmmss";
	public static final String DATETIME_FORMAT_4 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_FORMAT_2 = "yyyyMMdd";
	public static final String DATETIME_FORMAT_3 = "yyyy-MM-dd";
	public static final String TIME_FORMAT_1 = "MMddHHmm";
	public static final String TIME_FORMAT_2 = "HHmmss";

	/**
	 * 获取yyyyMMddHHmmss的当时时间格式
	 * @return
	 */
	public static String getCurrentDateTime() {
		return formatCurrentDateTime("yyyyMMddHHmmss");
	}

	/**
	 * 获取yyyyMMdd的当时时间格式
	 * @return
	 */
	public static String getCurrentDate() {
		return formatCurrentDateTime("yyyyMMdd");
	}

	/**
	 * 获取yyyy-MM-dd的当时时间格式
	 * @return
	 */
	public static String getCurrentDate2() {
		return formatCurrentDateTime("yyyy-MM-dd");
	}

	/**
	 * 获取MMddHHmm的当时时间格式
	 * @return
	 */
	public static String getCurrentTime() {
		return formatCurrentDateTime("MMddHHmm");
	}

	/**
	 * 获取HHmmss的当时时间格式
	 * @return
	 */
	public static String getCurrentTime2() {
		return formatCurrentDateTime("HHmmss");
	}

	/**
	 * 根据传进来的格式，将时间进行格式化并返回
	 * @return
	 */
	public static String formatCurrentDateTime(String formatText) {
		Calendar calendar = Calendar.getInstance();
		return convertDateToStr(calendar.getTime(), formatText);
	}

	/**
	 * 将日期格式进行转化，返回字符串格式
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String convertDateToStr(Date date, String pattern) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 将日期格式进行转化，返回字符串格式
	 * @param date1
	 * @return
	 */
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

	/**
	 * 将日期格式进行转化，返回字符串格式
	 * @param date
	 * @return
	 */
	public static String formatDateTOD3(String date) {
		Date d1 = new Date();
		try {
			if (StringUtils.isNotEmpty(date)) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				d1 = df.parse(date);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return d1.toString();
	}

	/**
	 * 将10位日期格式转化为8位并返回
	 * @param date1
	 * @return
	 */
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
	 * 将20位日期格式转化为14位并返回
	 * @param date1
	 * @return
	 */
	public static String formmat20to14(String date1) {
		if (date1 == null) {
			return date1;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = null;
		try {
			date2 = formatter.parse(date1);
		} catch (ParseException e) {
			return date1;
		}
		formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String date3 = formatter.format(date2);
		return date3;
	}
	
	/**
	 * 将字符转换为日期格式
	 */
	public static Date string2Date(String date,String formatDate){
		DateFormat df = new SimpleDateFormat(formatDate);  
		Date date1 =  null;
		try { 
			date1 = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}
	
	/**
	 * 比较日期大小
	 * @param beforeDate1
	 * @param afterDate2
	 * @return
	 */
	public static boolean compareDate(String beforeDate, String afterDate){
		boolean flag = false;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try{
			Date dt1 = df.parse(beforeDate);
            Date dt2 = df.parse(afterDate);
            int dd = dt1.compareTo(dt2);
            if (dd>=0) {
            	flag = true;
            } else{
                flag = false;
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 判断字符串中是否包含中文：有返回TRUE,否则FALSE
	 * @param str
	 * @return
	 */
	public static boolean checkHasChin(String str) {
		if (str == null) {
			return false;
		}
		Pattern pa = Pattern.compile("[\\u4E00-\\u9FA0]", Pattern.CANON_EQ);
		Matcher mt = pa.matcher(str);
		if (mt.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串中是否由数字组成：有返回TRUE,否则FALSE
	 * @param str
	 * @return
	 */
	public static boolean checkIsNum(String str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串中是否由数字和字符组成：有返回TRUE,否则FALSE
	 * @param str
	 * @return
	 */
	public static boolean checkIsNumAndChar(String str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("[a-z0-9A-Z]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将货币元转化为分
	 * @param pageCode
	 * @throws Exception
	 */
	public static String turnAmtY2F(String sAmt) throws Exception {
		if (StringUtils.isEmpty(sAmt))
			return sAmt;
		String sStr = "";
		try {
			double d = Double.parseDouble(sAmt) * 100;
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			sStr = nf.format(d);
		} catch (Exception e) {
			sStr = sAmt;
		}
		return sStr;
	}

	/**
	 * 将货币分转化为元
	 * @param pageCode
	 * @throws Exception
	 */
	public static String turnAmtF2Y(String sAmt) throws Exception {
		if (StringUtils.isEmpty(sAmt))
			return sAmt;
		String sStr = "";
		try {
			double d = Double.parseDouble(sAmt) / 100.00;
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			sStr = nf.format(d);
		} catch (Exception e) {
			sStr = sAmt;
		}
		return sStr;
	}

	/**
	 * 根据Element的节点名称，获取对应的值
	 * @param em
	 * @param key
	 * @return
	 */
	public static String getElementTextByKey(Element em, String key) {
		Element e = em.element(key);
		if (e == null)
			return "";
		return e.getText();
	}

	/**
	 * 日期格式转化：8位变10位（如20120101，2012-01-01），14位变19位（如20120101080808，2012-01-01 08:08:08）
	 * @param sDate
	 * @return
	 */
	public static String turnDateFormat(String sDate) {
		if (StringUtils.isEmpty(sDate))
			return "";
		String rStr = "";
		if (sDate.length() == 8) {
			rStr = sDate.substring(0, 4) + "-" + sDate.substring(4, 6) + "-"
					+ sDate.substring(6, 8);
		} else if (sDate.length() == 14) {
			rStr = sDate.substring(0, 4) + "-" + sDate.substring(4, 2) + "-"
					+ sDate.substring(6, 2) + " " + sDate.substring(8, 2) + ":"
					+ sDate.substring(10, 2) + ":" + sDate.substring(12, 2);
		} else {
			rStr = sDate;
		}
		return rStr;
	}

	/**
	 * 实数format:将前面多余的零去掉
	 * @param sNum
	 * @return
	 */
	public static String formatDigits(String sNum) {
		if (StringUtils.isEmpty(sNum))
			return "";
		int len = sNum.length();
		char c;
		if (sNum.indexOf(".") == -1) {
			for (int i = 0; i < len; i++) {
				c = sNum.charAt(0);
				if ("0".equals(c + "") && sNum.length() > 0) {
					sNum = sNum.substring(1, sNum.length());
				} else {
					break;
				}
				if (sNum.length() == 0) {
					sNum = "0";
					break;
				}
			}
		} else {
			String[] arr = sNum.split("\\.");
			if (StringUtils.isEmpty(arr[0]))
				return "0." + (StringUtils.isEmpty(arr[1]) ? "00" : arr[1]);
			String str = arr[0];
			len = str.length();
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					c = str.charAt(0);
					if ("0".equals(c + "") && str.length() > 0) {
						str = str.substring(1, str.length());
					} else {
						break;
					}
					if (str.length() == 0) {
						str = "0";
						break;
					}
				}
				sNum = str + "." + arr[1];
			}
		}
		return sNum;
	}

	/**
	 * 过滤特殊字符：type为1一个英文单引号替换成两个英文单引号;type为0：两个英文单引号替换成一个英文单引号
	 * @param str
	 * @param type:1和0
	 * @return
	 */
	public static void turnSpecialChar(String str, int type) throws Exception {
		if (StringUtils.isNotEmpty(str)) {
			str = str.toLowerCase();
			String[] arr = str.split(",");
			int len = arr.length;
			String sEtf = "";
			String str1 = "'";
			String str2 = "''";
			if (type == 0) {
				str2 = "'";
				str1 = "''";
			}
			for (int i = 0; i < len; i++) {
				sEtf = Etf.getChildValue(arr[i]);
				if (StringUtils.isNotEmpty(sEtf)) {
					sEtf = sEtf.replaceAll(str1, str2);
					Etf.setChildValue(arr[i], sEtf);
				}
			}
		}
	}

	/**
	 * @author Jason
	 * @desc 查询不符合流通状态的卡片
	 * @param sCardNoBegin 起始卡号
	 * @param sCardNoEnd 终止卡号
	 * @param sStoreStatus 状态值 e.g:"1,2"
	 * */
	public static String getCardNoNotInStoreOrgId(String sCardNoBegin,String sCardNoEnd,String sStoreOrgId){
		String sRes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CARD_NO as TEMP_CARD_NO ");
		sb.append("FROM FK_CARD_INF ");
		sb.append("WHERE CARD_NO BETWEEN '"+sCardNoBegin+"' AND '"+sCardNoEnd+"' ");
		sb.append("AND ORG_ID_STORE != '"+sStoreOrgId+"' ");
		int iResult = Atc.PagedQuery("1", "1000", "REC_TEMP3", sb.toString());
		if(0 == iResult){
			List<Element> recList = Etf.childs("REC_TEMP3");
			String sCardNoTemp    = null;
			for(Element el:recList){
				if(null != el.selectSingleNode("TEMP_CARD_NO")){
					sCardNoTemp = el.selectSingleNode("TEMP_CARD_NO").getText();
					if(null == sRes){
						sRes = sCardNoTemp;
					}else{
						sRes += ","+sCardNoTemp;
					}
				}
			}
		}
		return sRes;
	} 
	
	/**
	 * 作用 ： 去掉前边数字及下划线
	 * @param url
	 * 0_20130809123956_IMG_0701.jpg;1_20130809123956_IMG_0701.jpg;
	 */
	public static String getURL(String url) {
		String result = "";
		if (url != null && url.length() > 2) {
			String[] urls = url.split(";");
			for (int i = 0; i < urls.length; i++) {
				if(urls[i].length()>2){
					String subString = urls[i].substring(0,2);
					if(subString.contains("_")){
						result += urls[i].substring(2); 
					}else{
						result += urls[i];
					}
				}else{
					result += urls[i];
				}
				if (i != (urls.length - 1)) {
					result += ";";
				}
			}
			return result;
		} else {
			return "";
		}
	}
	/**
	 * 获取公共信息表中财务日期
	 * @return
	 * 		如果公共信息表中有财务日期则返回财务日期；
	 * 		如果没有查询到数据 或者系统错误 则返回null 
	 */
	public static String getActDate(){
		String sql = "select ACTDAT from PUBPLTINF where SYSID='000001'";
		Log.info("查询的sql=" + sql );
		int r =  Atc.ReadRecord(sql);
		if( r == 0){
			String actDate = Etf.getChildValue("ACTDAT");
			return StringUtils.isEmpty(actDate) ? null : actDate;
		}else if(r == 2 ){
			Atc.error("300000", "查询系统财务日期错误");
			return null;
		}else{
			Atc.error("300000", "系统错误");
			return null;
		}
	}
	
	/**
	 * @author Jason
	 * @desc 获得表对应的ID值Seq
	 * @param sSeqName eg:FK_CARD_INF_ID
	 * @return 通过GetSeqNo获得的ID值
	 * */
	public static String getIdBySeqName(String sSeqName){
		return getIdBySeqName(sSeqName,null,true);
	}
	
	/**
	 * @author TROY
	 * @desc 获得表对应的ID值Seq
	 * @param sSeqName eg:FK_CARD_INF_ID
	 * @return 通过GetSeqNo获得的ID值
	 * */
	public static String getIdBySeqName(String sSeqName,String sLen,boolean isZero){
		if(StringUtils.isEmpty(sLen))  sLen = "8";
		String sRes     = "";
		String sKeyName = "KEYNAM";
		String sKeyVal  = sSeqName;
		String sSeqNam  = "KEYVAL";
		String sTblName = "STPSEQREC";
		String sCircledString = "9";
		int iRes = Atc.GetSeqNo(sKeyName, sKeyVal, sSeqNam, sTblName, sLen, sCircledString, null);
		if(0==iRes){
			if(isZero){
				sRes = Etf.getChildValue("KEYVAL");
			}else{
				BigInteger bi = new BigInteger(Etf.getChildValue("KEYVAL"));
				sRes = bi.toString();
			}
		}
		return sRes;
	}
	
	/**
	 * @author Jason
	 * @desc 拼装插入SQL语句
	 * @param sSql eg:INSERT INTO TABLE() VALUES()
	 * @param sCol eg:NAME
	 * @param sVal eg:Jason
	 * @return INSERT INTO TABLE(NAME,) VALUES('Jason',)
	 * 添加列和值
	 * */
	public static String addColAndVal(String sSql,String sCol,String sVal){
		//替换sql中的第一个")" 为 COL,)
		sSql = sSql.replaceFirst("\\)", sCol+",)");
		//对非空字段添加单引号
		if(null!=sVal){
			sVal = "'"+sVal+"'";
		}
		//替换sql中的最后一个")" 为 VAL,)
		sSql = sSql.substring(0, sSql.length()-1)+sVal+",)";
		return sSql;
	}
	
}
