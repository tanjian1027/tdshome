package com.util;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.excelutils.ExcelUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import tangdi.atc.Atc;
import tangdi.atc.DbAtcUtil;
import tangdi.atc.TdFile;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.util.ResourceLocator;

import com.tangdi.dict.DictFunc;

/**
 * 系统公用函数管理
 * 棠棣科技
 * @author Administrator
 */
public class Utils {

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
	public static boolean compareDate0(String beforeDate, String afterDate){
		boolean flag = false;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try{
			Date dt1 = df.parse(beforeDate);
            Date dt2 = df.parse(afterDate);
            int dd = dt1.compareTo(dt2);
            if (dd>0) {
            	flag = true;
            } else{
                flag = false;
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean compareDate1(String beforeDate, String afterDate){
		boolean flag = false;
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try{
			Date dt1 = df.parse(beforeDate);
            Date dt2 = df.parse(afterDate);
            int dd = dt1.compareTo(dt2);
            if (dd>0) {
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
		} else if (sDate.length() == 6) {
			rStr = sDate.substring(0, 4) + "-" + sDate.substring(4, 6);
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
	 * 数据库执行中特大字符处理
	 * @param sql：原执行sql语句
	 * @param content：特大字符ETF树节点名称
	 * @param sTable:处理的表名
	 * @param upDateCondition：INSERT时产生的唯一条件
	 * @return
	 */
	public static int BigExecSql(String sql, String content, String sTable,
			String upDateCondition) {
		int result = 0;
		if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(content)
				|| StringUtils.isEmpty(sTable))
			return result;
		int maxLen = 2000;
		int rowLen = 2000;
		String allSql = DbAtcUtil.buildSql(sql);
		int len = allSql.length();
		if (len < maxLen) {
			result = Atc.ExecSql(null, sql);
		} else {
			String sContent = DbAtcUtil.buildSql("#{" + content.toUpperCase()
					+ "}");
			String sStr = Etf.getChildValue(content.toUpperCase());
			String sStrF = "";
			boolean b = false;
			String sSql = "";
			String sWhere = upDateCondition;
			if (StringUtils.isEmpty(sWhere)) {
				int pos = sql.toUpperCase().indexOf("WHERE");
				if (pos > 0) {
					sWhere = sql.substring(pos, sql.length());
				}
			}
			if (StringUtils.isEmpty(sWhere))
				return result;
			if (sWhere.toUpperCase().indexOf("WHERE") == -1) {
				sWhere = " WHERE " + sWhere;
			}
			int strLen;
			while (StringUtils.isNotEmpty(sStr)) {
				if (sStr.length() > rowLen) {
					strLen = rowLen;
				} else {
					strLen = sStr.length();
				}
				sStrF = sStr.substring(0, strLen);
				Etf.setChildValue("BIGEXECSQLSTR", sStrF);
				if (b) {
					sSql = "update " + sTable + " set " + content + "="
							+ content + "||#{BIGEXECSQLSTR} " + sWhere;
				} else {
					sSql = allSql.replace(sContent, "#{BIGEXECSQLSTR}");
				}
				result = Atc.ExecSql(null, sSql);
				if (result < 0) {
					break;
				}
				b = true;
				sStr = sStr.substring(strLen, sStr.length());
			}
		}
		return result;
	}
	
	/**
	 * excelMode 模板  此名字要和模板名字一致
	 * fileName  生成新文件，可以随便起名
	 */
	public static void FtpPut(String excelMode, String fileName) throws Exception {
		//java项目所在路径
		String appPath = ((ResourceLocator) Msg
				.getInstance(ResourceLocator.class)).getRoot()
				+ SystemUtils.FILE_SEPARATOR;
		//模板文件路径
		String config = appPath + "dat/" + excelMode + ".xls";
		//文件生成件
		String localDir = "/home/payment/resin/webapps/pay/dat/download/"; 
		ExcelUtils.export(config, new FileOutputStream(localDir + fileName));//模板文件生成到-->
	}
	
	/**
	 * 组装发卡方和机构权限SQL
	 * @param sIssueTableReName:发卡方Id过滤字段所在表 重定义后的简称
	 * @param issueDefaultColName：发卡方Id过滤字段所在表的发卡方id字段列名,默认ISSUE_ID
	 * @param bOrg：是否判断到机构权限
	 * @param sOrgTableReName: 机构Id过滤字段所在表 重定义后的简称
	 * @param orgDefaultColName：机构Id过滤字段所在表的机构id字段列名,默认ORG_ID
	 * @throws Exception
	 */
//	public static String assembIssueLimSql(String sIssueTableReName,String issueDefaultColName,boolean bOrg,String sOrgTableReName,String orgDefaultColName) throws Exception {
//		String backSql ="";
//		String SSISSUEID = Etf.getChildValue("SESSIONS.ISSUEID");
//		if(Default.ISSUSE_ID.equals(SSISSUEID)) return backSql;
//		if (StringUtils.isEmpty(sIssueTableReName)) {
//			sIssueTableReName = StringUtils.isEmpty(Etf.getChildValue("ISSUETABLERENAME")) ? "" : Etf.getChildValue("ISSUETABLERENAME")+".";
//		} else {
//			sIssueTableReName +=".";
//		}
//		issueDefaultColName  = StringUtils.isEmpty(issueDefaultColName) ? "ISSUE_ID" : issueDefaultColName;
//		backSql = " and "+ sIssueTableReName+issueDefaultColName+"='"+SSISSUEID+"' ";
//		if(bOrg && Etf.getChildValue("SESSIONS.ORGTYPE").equals(Default.ORG_TYPE_1)) {
//			if (StringUtils.isEmpty(sOrgTableReName)) {
//				sOrgTableReName = StringUtils.isEmpty(Etf.getChildValue("ORGTABLERENAME")) ? "" : Etf.getChildValue("ORGTABLERENAME")+".";
//			} else {
//				sOrgTableReName +=".";
//			}
//			orgDefaultColName  = StringUtils.isEmpty(orgDefaultColName) ? "ORG_ID" : orgDefaultColName;
//			backSql += " and "+ sOrgTableReName+orgDefaultColName+"='"+Etf.getChildValue("SESSIONS.ORGID")+"' ";
//		}
//		Log.info("组装发卡方和机构权限SQL=【%s】", backSql);
//		return backSql;
//	}
	
	/**
	 * 组装发卡方和机构权限SQL--重载方法   add by troy!
	 * @param sIssueTableReName:发卡方Id过滤字段所在表 重定义后的简称
	 * @param issueDefaultColName：发卡方Id过滤字段所在表的发卡方id字段列名,默认ISSUE_ID
	 * @param bOrg：是否判断到机构权限
	 * @param sOrgTableReName: 机构Id过滤字段所在表 重定义后的简称
	 * @param orgDefaultColName：机构Id过滤字段所在表的机构id字段列名,默认ORG_ID
	 * @param notOnly:判断是否支持多发卡方
	 * @throws Exception
	 */
//	public static String assembIssueLimSql(String sIssueTableReName,String issueDefaultColName,boolean bOrg,String sOrgTableReName,String orgDefaultColName,boolean notOnly) throws Exception {
//		String backSql ="";
//		String SSISSUEID = Etf.getChildValue("SESSIONS.ISSUEID");
//		if(Default.ISSUSE_ID.equals(SSISSUEID)) return backSql;
//		if (StringUtils.isEmpty(sIssueTableReName)) {
//			sIssueTableReName = StringUtils.isEmpty(Etf.getChildValue("ISSUETABLERENAME")) ? "" : Etf.getChildValue("ISSUETABLERENAME")+".";
//		} else {
//			sIssueTableReName +=".";
//		}
//		issueDefaultColName  = StringUtils.isEmpty(issueDefaultColName) ? "ISSUE_ID" : issueDefaultColName;
//		if(notOnly){
//			backSql = " and "+ sIssueTableReName+issueDefaultColName+" like '%"+SSISSUEID+"%' ";
//		}else{
//			backSql = " and "+ sIssueTableReName+issueDefaultColName+"='"+SSISSUEID+"' ";
//		}
//		
//		if(bOrg && Etf.getChildValue("SESSIONS.ORGTYPE").equals(Default.ORG_TYPE_1)) {
//			if (StringUtils.isEmpty(sOrgTableReName)) {
//				sOrgTableReName = StringUtils.isEmpty(Etf.getChildValue("ORGTABLERENAME")) ? "" : Etf.getChildValue("ORGTABLERENAME")+".";
//			} else {
//				sOrgTableReName +=".";
//			}
//			orgDefaultColName  = StringUtils.isEmpty(orgDefaultColName) ? "ORG_ID" : orgDefaultColName;
//			backSql += " and "+ sOrgTableReName+orgDefaultColName+"='"+Etf.getChildValue("SESSIONS.ORGID")+"' ";
//		}
//		Log.info("组装发卡方和机构权限SQL=【%s】", backSql);
//		return backSql;
//	}
	
	/**
	 * 通过卡号获取序号
	 * @param CARD_NO
	 * @throws Exception
	 */
//	public static String getCardAccNoByNo(String CARD_NO) throws Exception {
//		Log.info("通过卡号获取序号开始");
//		if (StringUtils.isEmpty(CARD_NO)) return "";
//		String sql = "select t.CARD_ACC_NO from FK_CARD_INF t " 
//				+ " where t.CARD_NO = '"+ CARD_NO +"' "+Utils.assembIssueLimSql("t","ISSUE_ID",false,"","");
//		Log.info("查询的SQL=【%s】", sql);
//		int result = Atc.ReadRecord(sql);
//		String str="";
//		if (result == 0) {
//			str = StringUtils.isEmpty(Etf.getChildValue("CARD_ACC_NO"))?"":Etf.getChildValue("CARD_ACC_NO");
//		} else {
//			str = "";
//		}
//		Log.info("通过卡号获取序号结束");
//		return str;
//	}
	
	/**
	 * 通过序号获取卡号
	 * @param CARD_ACC_NO
	 * @throws Exception
	 */
//	public static String getCardNoByAccNo(String CARD_ACC_NO) throws Exception {
//		Log.info("通过序号获取卡号开始");
//		if (StringUtils.isEmpty(CARD_ACC_NO)) return "";
//		String sql = "select t.CARD_NO from FK_CARD_INF t " 
//				+ " where t.CARD_ACC_NO = '"+ CARD_ACC_NO +"' "+Utils.assembIssueLimSql("t","ISSUE_ID",false,"","");
//		Log.info("查询的SQL=【%s】", sql);
//		int result = Atc.ReadRecord(sql);
//		String str="";
//		if (result == 0) {
//			str = StringUtils.isEmpty(Etf.getChildValue("CARD_NO"))?"":Etf.getChildValue("CARD_NO");
//		} else {
//			str = "";
//		}
//		Log.info("通过序号获取卡号结束");
//		return str;
//	}
	
	/**
	 * 通过卡号和功能名称，判断该卡是否拥有该功能
	 * @param cardNo:卡号
	 * @param funcName:功能名称
	 * @throws Exception
	 */
	public static boolean getCardHasFunc(String cardNo,String funcName) throws Exception {
		Log.info("通过卡号获取序号开始");
		if (StringUtils.isEmpty(cardNo) || StringUtils.isEmpty(funcName)) return false;
//		String sql = "select CARD_ACC_NO from FK_CARD_INF " 
//				+ " where CARD_NO = '"+ CARD_NO +"' ";
//		Log.info("查询的SQL=【%s】", sql);
//		int result = Atc.ReadRecord(sql);
//		String str="";
//		if (result == 0) {
//			str = Etf.getChildValue("CARD_ACC_NO");
//		} 
		Log.info("通过卡号获取序号结束");
		return true;
	}
	
	/**
	 * 通过卡号检查该卡状态，并返回
	 * @param cardNo:卡号
	 * @throws Exception
	 */
//	public static String getCardStatus(String cardNo) throws Exception {
//		Log.info("通过卡号检查该卡状态开始");
//		if (StringUtils.isEmpty(cardNo)) return "-1";
//		String sql = "select t.STATUS from FK_CARD_INF t " 
//				+ " where t.CARD_NO = '"+ cardNo +"' "+Utils.assembIssueLimSql("t","ISSUE_ID",false,"","");
//		Log.info("查询的SQL=【%s】", sql);
//		int result = Atc.ReadRecord(sql);
//		String str="";
//		if (result == 0) {
//			str = Etf.getChildValue("STATUS");
//		} else {
//			str = "-1";
//		}
//		Log.info("通过卡号检查该卡状态结束");
//		return str;
//	}
	
	
	/**
	 * 通过商户编号检查该商户状态，并返回
	 * @param merId:商户编码
	 * @throws Exception
	 */
//	public static String getMerStatus(String merId) throws Exception {
//		Log.info("通过商户编号检查该商户状态开始");
//		if (StringUtils.isEmpty(merId)) return "-1";
//		String sql = "select t.STATUS from PMMERINF t " 
//				+ " where t.MERCID = '"+ merId +"' "+Utils.assembIssueLimSql("t","ISSUE_ID",false,"","");
//		Log.info("查询的SQL=【%s】", sql);
//		int result = Atc.ReadRecord(sql);
//		String str="";
//		if (result == 0) {
//			str = Etf.getChildValue("STATUS");
//		} else {
//			str = "-1";
//		}
//		Log.info("通过商户编号检查该商户状态结束");
//		return str;
//	}
	
	/**
	 * 根据起始卡号获取终止序号
	 * @param cardNo
	 * @param saleNum
	 */
//	public static String calcEndSerNo(String cardNo,String saleNum) throws Exception{
//		String serNoEnd = "";
//		String SFSWITCH = Default.getFlag(Default.exceptFS,"exceptFS");
//		Default.getNotShowInSeqNum(cardNo);
//		if(SFSWITCH.equals("1")&&StringUtils.isNotEmpty(Etf.getChildValue("NOTSHOWINSEQNUM"))){
//			String cardSeqnum = Etf.getChildValue("card_seqnum");
//			String serNoEndTmp = serNoEnd;
//			for(int i=0;i<Integer.parseInt(saleNum);i++){
//				serNoEndTmp = Default.strAddNum(cardSeqnum, i+"", "0");
//				int dd = (int)Default.getJumpNum(serNoEndTmp, Etf.getChildValue("NOTSHOWINSEQNUM"));
//				serNoEndTmp = Default.strAddNum(serNoEndTmp, dd+"", "0");
//				cardSeqnum = Default.strAddNum(cardSeqnum, dd+"", "0");
//			}
//			serNoEnd = Etf.getChildValue("card_acc_no").substring(0,Etf.getChildValue("card_acc_no").length()-serNoEndTmp.length())+serNoEndTmp;
//		}else{
//		if(StringUtils.isNotEmpty(cardNo) && StringUtils.isNotEmpty(saleNum)){
//			cardNo = cardNo.substring(0, cardNo.length()-1);
//			BigInteger iCardNo = new BigInteger(cardNo);
//			BigInteger iSaleNum = new BigInteger(saleNum);
//			iSaleNum = iSaleNum.subtract(new BigInteger("1"));
//			serNoEnd = iCardNo.add(iSaleNum).toString();
//			//---201405---卡号自定义时 前面为0时，上面的转换会把它去掉，现在加上。
//			serNoEnd = beforeZero(cardNo)+serNoEnd;
//			}
//		}
//		return serNoEnd;
//	}
	/**
	 * 获取字符串前面的0
	 * @param cardNo
	 * @return
	 */
	public static String beforeZero(String cardNo){
		String index = "";
		if(cardNo.startsWith("0")){
			for(int i=0;i<cardNo.length();i++){
				char c = cardNo.charAt(i);
				if(c=='0'){
					index = index+"0";
				}else{
					break;
				}
			}
		}
		return index;
	}
	
	/**
	 * 根据起始序号-终止序号+数量判断卡是否存在，是否属于同一卡种:-1系统错误，0正常，1卡号不存在，2不在同一卡种。
	 * @param cardStartNo:起始序号
	 * @param cardEndNo:终止序号
	 * @param num:数量
	 */
	public static int checkcEndSerNo(String cardStartNo,String cardEndNo,int num) throws Exception{
		int backType = 0;
		if(StringUtils.isEmpty(cardStartNo) || StringUtils.isEmpty(cardEndNo))  return 1;
		String sql = "select count(*) as NUMS from FK_CARD_INF t "
						+ " where CARD_SEQNUM between '"+cardStartNo+"' and '"+cardEndNo+"' ";
		Log.info("查询同一父节点下数据字典名称信息是否已经存在sql=%s", sql);
		int result = Atc.ReadRecord(sql.toString());
		String count = "";
		int iCount;
		if (result == 0) {
			count = Etf.getChildValue("NUMS");
			iCount = Integer.parseInt(count);
			if (iCount < num) {
				return 1;//卡号有不存在
			}
		} else if(result==-1) {
			return -1;//系统错误
		} else {
			return 1;//卡号有不存在
		}
		sql = "select count(distinct CARD_KIND_ID) as NUMS from FK_CARD_INF t "
						+ " where CARD_SEQNUM between '"+cardStartNo+"' and '"+cardEndNo+"' ";
		Log.info("查询同一父节点下数据字典名称信息是否已经存在sql=%s", sql);
		result = Atc.ReadRecord(sql.toString());
		if (result == 0) {
			count = Etf.getChildValue("NUMS");
			iCount = Integer.parseInt(count);
			if (iCount >1) {
				return 2;//卡号有不存在
			}
		} else if(result==-1) {
			return -1;//系统错误
		} else {
			Log.info("系统错误：卡种不存在");
			return -1;//系统错误：卡种不存在
		}
		return backType;
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
				BigInteger bi = new BigInteger(Etf.getChildValue("KEYVAL")) ;
				sRes = bi.toString();
			}
		}
		return sRes;
	}
	
	/**
	 * 获得Session中的值
	 * */
	public static String getSessionByName(String sName){
		String sRes = Etf.getChildValue("SESSIONS."+sName.toUpperCase());
		return sRes;
	}
	
	//获得ORGID
	public static String getSessionOrgId(){
		return getSessionByName("ORGID");
	}
	
	//获得UID
	public static String getSessionUid(){
		return getSessionByName("UID");
	}
	
	//获得ISSUEID
	public static String getSessionIssueId(){
		return getSessionByName("ISSUEID");
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
	
	/**
	 * @author Jason
	 * @desc 对返回前台页面的列表查询，字典数据进行格式化
	 * @param 同Atc.PageQuery
	 * */
	public static int PagedQuery(String pageNum,String sNumPerPage,String sRecName,String sSql){
		int iResult = Atc.PagedQuery(pageNum, sNumPerPage, sRecName,sSql);
		//将查询的数据字典进行更新
		formatDict2Etf(sRecName);
		return iResult;
	}
	
	/**
	 * @author Jason
	 * @desc 格式化查询的字典节点内容
	 * @param sRec Etf上的数组节点
	 * 1.获得Etf上查询的数组节点
	 * 2.遍历所有记录，将含有_CC_的节点内容进行，数据字典替换
	 * */
	public static void formatDict2Etf(String sRec){
		Element el            = Etf.peek();
		if(null==el){
			return;
		}
		Node recNode          = el.selectSingleNode(sRec);
		if(null==el.selectSingleNode(sRec)){
			return;
		}
		List<Node> childNode  = recNode.selectNodes("*");
		List<Element> elfList = Etf.childs(sRec);
		String sNodeName  = null;
		String sName      = null;
		String sVal       = null;
		String sCn        = null;
		for(Element etf:elfList){
			for(Node node:childNode){
				sNodeName = node.getName();
				if(null == sNodeName || sNodeName.toUpperCase().indexOf("_CC_")==-1){
    				continue;
    			}
				if(null == etf.selectSingleNode(sNodeName)){
					continue;
				}
				sName = getDictName(sNodeName);
				sVal = etf.selectSingleNode(sNodeName).getText();
				sCn  = DictFunc.getDictNameChildVal(sName, sVal);
				if(null != sCn){
					etf.selectSingleNode(sNodeName).setText(sCn);
				}
			}
		}
	}
	
	/**
	 * @author Jason
	 * @desc 获得列名中的数据字典DICT_CODE
	 * @param sColName eg:STATUS_CC_ZJLX 
	 * @return ZJLX
	 * */
	private static String getDictName(String sColName){
		String sRes = "";
		if(null==sColName){
			return sRes;
		}
		if(sColName.toUpperCase().indexOf("_CC_")!=-1){
			String[] arr = sColName.toUpperCase().split("_CC_");
			sRes = arr[arr.length-1].trim();
		}
		return sRes;
	}
	
	/**
	 * 作用： 修改上传的文件增加时间戳 eg：test.txt ---> 20130808182758_test.txt
	 * @param filePath  源文件路径       eg: D:\\test\\
	 * @param oldname   原文件名字       eg：test.txt
	 * @return 1:正确    0 错误
	 */
	public static int setFerDateTimeOfFile(String filePath, String oldName) {
		File file = new File(filePath + oldName);
		String nowTime = Utils.getCurrentDateTime();
		if(file.exists()){
			String newName = nowTime + "_" + oldName;
			if (!oldName.equals(newName)) {
				File newFile = new File(filePath + "/" + newName);
				file.renameTo(newFile);
				//用于页面显示调用
				Etf.setChildValue("PATHRENAME", oldName+";"+newName);
				Etf.setChildValue("TMPFILENAME",newName);
			}
			return 1 ;
		}else{
			Log.info("上传文件不存在，未修改");
			return 0 ;
		}
	}
	
	/**
	 * @author Jason
	 * @desc 查询不符合状态的卡片
	 * @param sCardNoBegin 起始卡号
	 * @param sCardNoEnd 终止卡号
	 * @param arrStatus 状态值 e.g:"1","2"
	 * */
	public static String getCardNoNotInStatus(String sCardNoBegin,String sCardNoEnd,String... arrStatus){
		String sRes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CARD_NO as TEMP_CARD_NO ");
		sb.append("FROM FK_CARD_INF ");
		sb.append("WHERE CARD_NO BETWEEN '"+sCardNoBegin+"' AND '"+sCardNoEnd+"' ");
		for(String sStatus:arrStatus){
			sb.append("AND STATUS != "+sStatus+" ");
		}
		int iResult = Atc.PagedQuery("1", "1000", "REC_TEMP1", sb.toString());
		if(0 == iResult){
			List<Element> recList = Etf.childs("REC_TEMP1");
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
	 * @author Jason
	 * @desc 查询不符合流通状态的卡片
	 * @param sCardNoBegin 起始卡号
	 * @param sCardNoEnd 终止卡号
	 * @param arrStoreStatus 状态值 e.g:"1","2"
	 * */
	public static String getCardNoNotInStoreStatus(String sCardNoBegin,String sCardNoEnd,String... arrStoreStatus){
		String sRes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CARD_NO as TEMP_CARD_NO ");
		sb.append("FROM FK_CARD_INF ");
		sb.append("WHERE CARD_NO BETWEEN '"+sCardNoBegin+"' AND '"+sCardNoEnd+"' ");
		for(String sStoreStatus:arrStoreStatus){
			sb.append("AND STORE_STATUS not in("+sStoreStatus+") ");
		}
		int iResult = Atc.PagedQuery("1", "1000", "REC_TEMP2", sb.toString());
		if(0 == iResult){
			List<Element> recList = Etf.childs("REC_TEMP2");
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
	 * 获取当前时间 年 月 日 时 分 秒
	 * @return
	 */
	public static String getNowFullTime(){
		Calendar now = Calendar.getInstance();  
        String s = now.get(Calendar.YEAR)+"年"+(now.get(Calendar.MONTH) + 1)+"月"
        +now.get(Calendar.DAY_OF_MONTH)+"日 "+now.get(Calendar.HOUR_OF_DAY)+"时"
        +now.get(Calendar.MINUTE)+"分"+now.get(Calendar.SECOND)+"秒";
        return s;
	}
	
	/**
	 * 根据卡种，充值金额计算出返还金额 数组0位 实收款 1位赠送款
	 * @param card_kind_id
	 * @param bal
	 * @return
	 */
	public static String[] getZsMoney(String card_kind_id,String bal){
		String[] res = new String[2];
		String today = getCurrentDate2();
		String sql = "select t.* from card_recharge_rule t where t.card_kind_id = '"+card_kind_id+"'"
				+" and '"+today+"' >= t.start_time and '"+today+"' <= t.end_time and t.status = '0'";
		int result = Atc.ReadRecord(sql);
		if( result == 0){
			String rule_type = Etf.getChildValue("RULE_TYPE");
			String rule_value = Etf.getChildValue("RULE_VALUE");
			String charge_value = Etf.getChildValue("CHARGE_VALUE");
			if("1".equals(rule_type)){
				double b = Double.parseDouble(charge_value)*100;
				String s1 = (Math.floor(Double.parseDouble(bal)/b))*Double.parseDouble(rule_value)*100+"";
				res[0] = bal;
				res[1] = s1;
			}else if("2".equals(rule_type)){
				String s1 = Double.parseDouble(bal)*Double.parseDouble(rule_value)+"";
				String s2 = Double.parseDouble(bal) - Double.parseDouble(bal)*Double.parseDouble(rule_value)+"";
				res[0] = s1;
				res[1] = s2;
			}
		}else if(result == 2 ){
			Log.info("系统无此卡种充值规则");
			res[0] = bal;
			res[1] = "0";
		}else{
			Atc.error("300000", "系统错误");
		}
		return res;
	}
	
	
	
	/**
	 * tds端生成的文件put到resin 端
	 */
	public static void putToPay(String fileName){
		try {
			int iRes = TdFile.ReadXmlConfig("etc/public/STP_CONFIG.XML", "FtpPutPay", null, null, Msg.getInstance(ResourceLocator.class));
			if(-1==iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "参数错误");
				return;
			}else if(2==iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "取XML配置父节点失败！");
				return;
			}else if(0!=iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "系统错误！");
				return;
			}
			
		 String ipAddr=Etf.getChildValue("IpAdr");
		 String port=Etf.getChildValue("Port");
		 String userName=Etf.getChildValue("UsrNam");
		 String userPwd=Etf.getChildValue("UsrPwd");
		 String objDir=Etf.getChildValue("ObjDir");
		 String lclDir=Etf.getChildValue("LclDir");
		 
		 Atc.FtpPut(ipAddr,userName, userPwd,
				 objDir, fileName, lclDir, fileName,port);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String readStpConfig(String nodeName,String subNodeName){
		
		int iRes;
		try {
			iRes = TdFile.ReadXmlConfig("etc/public/STP_CONFIG.XML", nodeName, null, null, Msg.getInstance(ResourceLocator.class));
			if(-1==iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "参数错误");
				return null;
			}else if(2==iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "取XML配置父节点失败！");
				return null;
			}else if(0!=iRes){
				Etf.setChildValue("RSPCOD", "000001");
				Etf.setChildValue("RSPMSG", "系统错误！");
				return null;
			}
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Etf.getChildValue(subNodeName);
		
		
	}
	
	
	
	
}
