package com.tangdi.risk.base;

public class RCS_CURRCONT {
	public static final String TABLE_NAME     = "RCS_CURRCONT";    //表名
	public static final String PRI_ID          = "PRI_ID";         //主键
	public static final String USER_CODE       = "USER_CODE";      //用户\商户ID
	public static final String CURENT_DATE     = "CURENT_DATE";    //操作日期
	public static final String PAY_TYPE        = "PAY_TYPE";       //操作类型
	public static final String PAY_TYPE_RESULT = "PAY_TYPE_RESULT";//操作结果
	public static final String TCODE           = "TCODE";
	public static final String TCODE_RESULT    = "TCODE_RESULT";
	public static final String STATUS          = "STATUS";
	public static final String PRDNUMBER       = "PRDNUMBER";
	public static final String SOURCE          = "SOURCE";
	public static final String REQIP           = "REQIP";
	public static final String IPCOUNTRY       = "IPCOUNTRY";//国家
	public static final String IPAREA          = "IPAREA";   //地区
	public static final String IPREGION        = "IPREGION"; //省
	public static final String IPCITY          = "IPCITY";   //市
	public static final String IPCOUNTY        = "IPCOUNTY"; //县
	public static final String IPISP           = "IPISP";    //运营商
	
	/**
	 * 获得指定时间所有交易IP
	 * @param sStartDate 传入的日期 格式yyyy-MM-dd
	 * @param sEndDate   传入的日期 格式yyyy-MM-dd
	 * */
	public static String getAllIpByDate(String sStartDate,String sEndDate){
		sStartDate += "000000";
		sEndDate   += "235959";
		String 
			sRes = "SELECT DISTINCT("+REQIP+") as "+REQIP;
			sRes += " FROM "+TABLE_NAME;
			sRes += " WHERE CURENT_DATE BETWEEN '"+sStartDate+"' AND '"+sEndDate+"'";
			sRes += " ORDER BY "+REQIP+" ASC ";
		return sRes;
	}
}
