package com.tangdi.risk.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/*
 * @desc   : 风控中需要的一些公共方法
 * @author : Jason Meng
 * @date   : 2012-12-11
 * */
public class Tools {
	/**
	 * Id生成器
	 * @return
	 */
	 public static String getId(String L){
		 String id = "";
		 if(L != null && L.length()==12){
			 Long rL = Long.valueOf(L)+1;
			 int rLleng = (rL+"").length();
			 if(rLleng < 12){
				 //前补0
				 for(int i=0;i<(12-rLleng);i++){
					 id += "0";
				 }
			 }
			 return id+rL;
		 }else{
			 return "000000000000";
		 }
	 }
	  
	/*
	 * @desc  : 通过卡号获取 卡类型 
	 * @param : sCardNo--卡号
	 * @return: 卡类型 1：国内借机卡 2：国内贷记卡 3：国外借记卡 4：国外贷记卡
	 * */
	public static String getCardType(String sCardNo){
		return null;
	}
	
	/*
	 * @desc  : 通过商户号获得商户类型 
	 * @param : sMerNo--商户号
	 * @return: 商户类型(待定)
	 * */
	public static String getMerType(String sMerNo){
		return null;
	}
	
	/*
	 * @desc  : 格式化当前日期  保证构造日期的时候参数正确
	 * @param : 日期
	 * @return: 格式化的日期
	 * */
	public static String getFormatDate(String sDay){
		String sRes = null;
		if(null == sDay){
			Calendar calendar   = Calendar.getInstance();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
			sRes = sd.format(calendar.getTime());
		}else{
			if(sDay.length() == 8){
				sDay = sDay.substring(0, 4)+"/"+sDay.substring(4, 6)+"/"+sDay.substring(6, 8);
			}
			sRes = sDay.replaceAll("-", "/");
		}
		return sRes;
	}
	
	/*
	 * @desc  : 通过日期获得指定间隔天数日期
	 * @param : sDay 日期
	 * @param : iVal 间隔天数
	 * @return: 传入日期的下一天 格式yyyy-MM-dd
	 * */
	public static String getDayByVal(String sDay,int iVal){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.DAY_OF_YEAR, iVal);
		return sd.format(cal.getTime());
	}
	public static String getDayByValCross(String sDay,int iVal){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.DAY_OF_YEAR, iVal);
		return sd.format(cal.getTime());
	}
	
	//获得对应日期的下一日
	public static String getNextDay(String sDay){
		return getDayByVal(sDay,1);
	}
	
	//获得对应日期的上一日
	public static String getPreDay(String sDay){
		return getDayByVal(sDay,-1);
	}
	//获得对应日期的上一日格式为2015/04/10
		public static String getDayByValCross(String sDay){
			return getDayByValCross(sDay,-1);
		}
	//获得对应日期的上周第一天
	public static String getPreWeekFirstDay(String sDay){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		if(cal.get(Calendar.DAY_OF_WEEK)==1){
			cal.set(Calendar.DAY_OF_WEEK, 1);
			cal.add(Calendar.DAY_OF_YEAR, -13);
		}else{
			cal.set(Calendar.DAY_OF_WEEK, 1);
			cal.add(Calendar.DAY_OF_YEAR, -6);
		}
		return sd.format(cal.getTime());
	}
	
	//获得对应日期的上周最后一天
	public static String getPreWeekLastDay(String sDay){
		sDay = getPreWeekFirstDay(sDay);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.DAY_OF_YEAR, 6);
		return sd.format(cal.getTime());
	}
	
	//获得上月第一天
	public static String getPreMonthFirstDay(String sDay){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sd.format(cal.getTime());
	}
	
	//获得上月最后一天
	public static String getPreMonthLastDay(String sDay){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return sd.format(cal.getTime());
	}
	
	//获得本月第一天
	public static String getMonthFirstDay(String sDay){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sd.format(cal.getTime());
	}
	 
	
	//获得本月最后一天
	public static String getMonthLastDay(String sDay){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sDay = getFormatDate(sDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(sDay));
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return sd.format(cal.getTime());
	}
	
	//获取指定前N个月第一天
	public static String getPreNMonthFirstDay(String sDay,int iNum){
		String sRes = getMonthFirstDay(sDay);
		for(int i=0;i<iNum;i++){
			sRes = getPreMonthFirstDay(sRes);
		}
		return sRes;
	}
	
	//将空置换为指定字符
	public static String repNull(String sStr,String sRep){
		if(null==sStr || "".equals(sRep)){
			sStr = sRep;
		}
		return sStr;
	}
	
	//判断字符串是否为空
	public static boolean isNull(String sStr){
		if(null==sStr || "".equals(sStr.trim())){
			return true;
		}
		return false;
	}
	
	//查询日期是否在界定日期范围内
	public static boolean isBetweenDate(String startDate,String endDate,String compareDate){
		startDate   = getFormatDate(startDate);
		endDate     = getFormatDate(endDate);
		compareDate = getFormatDate(compareDate);
		
		Date startD   = new Date(startDate);
		Date endD     = new Date(endDate);
		Date compareD = new Date(compareDate);
		
		if(compareD.getTime()>=startD.getTime() && compareD.getTime()<=endD.getTime()){
			return true;
		}
		return false;
	}
	
	/***
	 *  通过总值和次数获得平均值
	 * @param sAmount
	 * @param sCount   
	 * @return
	 */
	public static String getAveValue(String sAmount,String sCount){
		String sRes = "0";
		if(null == sCount || "".equals(sCount) || Double.valueOf(sCount)== 0){
			return sRes;
		}
		if(null == sAmount || "".equals(sAmount)){
			return sRes;
		}
		DecimalFormat df = new DecimalFormat("0.0");
		
		return df.format(Double.parseDouble(sAmount)/Double.parseDouble(sCount));
	}
	 
	//判断是否为整数
	public static boolean isNumeric(String sNum){
		if(isCorrectAmt(sNum)){
			double dNum = Double.parseDouble(sNum);
			if(Math.floor(dNum) != dNum){
				return false;
			}
		}
		return true;
	}
	//判断是否为合法金额
	public static boolean isCorrectAmt(String sNum){
		if(isNull(sNum)){
			return false;
		}
		Pattern pattern = Pattern.compile("^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$"); 
		return pattern.matcher(sNum).matches(); 
	}
	
	//获得当前日期至N天前日期Set
	public static Set<String> getDateSetByN(String sDate,int iDays){
		Set<String> resSet = new TreeSet<String>();
		for(int i=0;i< iDays;i++){  
			resSet.add(getDayByVal(sDate,-i));
			System.out.println(getDayByVal(sDate,-i));
		}
		return resSet;
		
	}
	
	//判断传入的交易类型是否包含该交易类型
	public static boolean isContainsType(String sAllTypes,String sSingleType){
		if(Tools.isNull(sAllTypes)||Tools.isNull(sSingleType)){
			return false;
		}
		if(!sAllTypes.startsWith(",")){
			sAllTypes = ","+sAllTypes;
		}
		if(!sAllTypes.endsWith(",")){
			sAllTypes = sAllTypes+",";
		}
		if(sAllTypes.indexOf(","+sSingleType+",")!=-1){
			return true;
		}
		return false;
	}
	
	//显示空间信息
	public static void showSpace(){
		Runtime run = Runtime.getRuntime(); 

		long max    = run.maxMemory(); 
		long total  = run.totalMemory(); 
		long free   = run.freeMemory(); 
		long usable = max - total + free; 
	}
	
	/**
	 * 将货币元转化为分
	 * @param pageCode
	 * @throws Exception
	 */
    public static String turnAmtY2F(String sAmt){
    	if(sAmt == null || sAmt.trim().equals("")) return "0";
    	String sStr="0";
    	try{
    		double d = Double.parseDouble(sAmt)*100;
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        	nf.setGroupingUsed(false);
        	sStr = nf.format(d);
    	} catch(Exception e) {
    		sStr = sAmt;
    	}
    	return sStr;
    }
    
    /**
	 * 判断IP是否为境外
	 * @param sIpCountry
	 * @throws Exception
	 */
    public static boolean isOverseas(String sIpCountry){
    	boolean bRes = false;
    	if(!"中国".equals(sIpCountry)){
    		bRes = true;
    	}
    	return bRes;
    }
   
 
  
}
