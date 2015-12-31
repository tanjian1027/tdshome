package com.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import tangdi.atc.Atc;
import tangdi.atc.TdFile;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.util.ResourceLocator;

public class RcsUtils {
	/**
	 * 得到当前登录用户id
	 */
	public static String getUID(){
		String uId = Etf.getChildValue("SESSIONS.UID");
	    Log.info("当前登录用户id为 ："+uId);
		return uId;
	}
	/**
	 * 判空
	 */
	public static boolean isNotEmpty(String str){
		Log.info("str="+str);
		if(null == str){
			return false;
		}else if(str.trim().equals("") || str.trim().equals("null")){
			return false;
		}
		else{
			return true;
		} 
	}
	/**
	 * 获取当前路径
	 * @throws IOException 
	 */
	public static String getPath() throws IOException{
		File directory = new File("..");//设定为当前文件夹 
		String path =  directory.getCanonicalPath();
		//java项目所在路径
		String appPath = ((ResourceLocator) Msg.getInstance(ResourceLocator.class)).getRoot();
		
		Log.info("java项目所在路径 = "+appPath);// 应用级路径：/home/tdsadm/tdshome/app/rcs/
//		Log.info("获取标准的路径为 ="+path);//获取标准的路径 （ app级路径：/home/tdsadm）
//		Log.info("获取当前路径为 ="+directory.getAbsolutePath());//获取绝对路径 （/home/tdsadm/tdshome/..）
		
		return appPath.substring(0,appPath.indexOf("/app"))+"/";
	}
	/**
	 * 判断整数金额后几位是否相同 相同返回true  不相同返回false
	 * @param string  金额
	 * @param configLength 位数
	 * @return
	 */
	public boolean is_no(String string,int configLength){
		boolean b = false;
		Log.info("判断"+string+"的后"+configLength+"位是否一样");
		int strLength = string.length();
			
		if(strLength > (configLength+1)){
			String newStr = string.substring(strLength-(2+configLength),strLength-2);//后configLength位整数
			char newIndex=' ';
			List listOne = new ArrayList();
			for(int i=0;i< configLength;i++){
				char a =  newStr.charAt(i);
				listOne.add(a);
			}
			Log.info(" 比较数字：list = "+listOne.toString());
			
			String firstStr = listOne.get(0).toString();
			for(int i=0;i < listOne.size();i++){
				
				if(!firstStr.equals(listOne.get(i).toString())){//若不相等
					b = false;
					i = listOne.size();//当等于false时，则不再比较
				}else{
					b = true;
				}
			}
		}else{
			Log.info("该数字整数部分未达到"+configLength+"位,直接通过");
		    b = true;
		}
		return b;
	}
	
	/**
	 * 判断字符串中是否包含中文：有返回TRUE,否则FALSE
	 * @param str
	 * @return
	 */
	public static boolean checkHasChin(String str){		
		if(str==null) {
			return false;
		}
        Pattern pa = Pattern.compile("[\\u4E00-\\u9FA0]",Pattern.CANON_EQ); 
        Matcher mt = pa.matcher(str); 
		if(mt.find()){ 
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
	public static boolean checkIsNum(String str){		
		if(str==null) {
			return false;
		}
	    Pattern pattern = Pattern.compile("[0-9]*");
	    Matcher isNum = pattern.matcher(str);
	    if(isNum.matches()){
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
	public static boolean checkIsNumAndChar(String str){		
		if(str==null) {
			return false;
		}
	    Pattern pattern = Pattern.compile("[a-z0-9A-Z]*");
	    Matcher isNum = pattern.matcher(str);
	    if(isNum.matches()){
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
    	if(StringUtils.isEmpty(sAmt)) return sAmt;
    	String sStr="";
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
	 * 将货币分转化为元
	 * @param pageCode
	 * @throws Exception
	 */
    public static String turnAmtF2Y(String sAmt){
    	if(StringUtils.isEmpty(sAmt)) return sAmt;
    	String sStr="";
    	try{
    		double d = Double.parseDouble(sAmt)/100.00;
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        	nf.setGroupingUsed(false);
        	sStr = nf.format(d);
    	} catch(Exception e) {
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
		if(StringUtils.isEmpty(sDate)) return "";
		String rStr ="";
		if(sDate.length()==8) {
			rStr = sDate.substring(0,4)+"-"+sDate.substring(4,6)+"-"+sDate.substring(6,8);
		} else if(sDate.length()==14) {
			rStr = sDate.substring(0,4)+"-"+sDate.substring(4,2)+"-"+sDate.substring(6,2)+" "+
				sDate.substring(8,2)+":"+sDate.substring(10,2)+":"+sDate.substring(12,2);
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
		if(StringUtils.isEmpty(sNum)) return "";
		int len =sNum.length();
		char c;
		if(sNum.indexOf(".")==-1) {
			for(int i=0;i<len;i++) {
				c = sNum.charAt(0);
				if("0".equals(c+"") && sNum.length()>0) {
					sNum = sNum.substring(1, sNum.length());
				} else {
					break;
				}
				if(sNum.length()==0) {
					sNum="0";
					break;
				}
			}
		} else {
			String[] arr = sNum.split("\\.");
			if(StringUtils.isEmpty(arr[0])) return "0."+(StringUtils.isEmpty(arr[1])?"00":arr[1]);
			String str = arr[0];
			len = str.length();
			if(len>1) {				
				for(int i=0;i<len;i++) {
					c = str.charAt(0);
					if("0".equals(c+"") && str.length()>0) {
						str = str.substring(1, str.length());
					} else {
						break;
					}
					if(str.length()==0) {
						str="0";
						break;
					}
				}
				sNum = str+"."+arr[1];
			}
		}
	    return sNum;
	}
	
    /**
     * 根据传进来的str，返回X名单中文描述
     * @param str
     * @return
     */
	public static String turnXType(String str) {
		if(StringUtils.isEmpty(str)) return "白名单";
		int nums =Integer.parseInt(str);
		String rStr ="白名单";
		switch(nums){
			case 2: 
				rStr ="灰名单";
				break;
			case 3: 
				rStr ="黑名单";
				break;
			case 4: 
				rStr ="红名单";
				break;
			default: 
				break;		
		}
	    return rStr;
	}
	
    /**
     * 过滤特殊字符：type为1一个英文单引号替换成两个英文单引号;type为0：两个英文单引号替换成一个英文单引号
     * @param str
     * @param type:1和0
     * @return
     */
	public static void turnSpecialChar(String str,int type) throws Exception {
		if(StringUtils.isNotEmpty(str)) {
			str=str.toLowerCase();
			String[] arr = str.split(",");
			int len      = arr.length;
			String sEtf  = "";
			String str1  = "'";
			String str2  = "''";
			if(type==0) {
				str2 = "'";
				str1 = "''";
			}
			for(int i=0;i<len;i++) {
				sEtf = Etf.getChildValue(arr[i]);
				if(StringUtils.isNotEmpty(sEtf)) {
					sEtf = sEtf.replaceAll(str1, str2);
					Etf.setChildValue(arr[i], sEtf);
				}				
			}
		}		
	}
	
	/**
	 *根据运营商编码返回运营商全称 
	 */
	public static String getCompName(String compCode){
		String rt ="";
		if (compCode!=null) {
			if (compCode.endsWith("010003")) {
				rt = "腾讯科技";
			} else if (compCode.endsWith("010001")) {
				rt = "嘻唰唰有限公司";
			}else if (compCode.endsWith("010002")) {
				rt = "海科融通";
			}
		}else{
			rt = " ";
		}
		return rt;
	}
	/**
	 * 增加一条记录  客户X名单系统生成表 : RCS_X_LIST_SYS
	 * userType   0 更新用户   1 更新商户
	 * userCode   用户id
	 * xType      要设置的名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单
	 * casType    生成原因类型 1异常交易、2可疑交易，3预警交易、4涉恐犯罪
	 */
	public static int addXList(String userType,String userCode,String xType,String casType){
		String upSql ="insert into RCS_X_LIST_SYS(id,USER_TYPE,CLIENT_CODE,X_TYPE,CREATE_DATETIME) " +
				"values("+RcsDefault.ID_RCS_X_LIST_SYS+",'"+userType+"','"+userCode+"','"+xType+"',to_char(sysdate,'yyyyMMddHH24miss'))";
		int result = Atc.ExecSql(null, upSql);
		return result;
	}
	
	/**
	 * 增加一条记录  客户X名单系统生成表 : RCS_X_LIST_SYS
	 * userType   0 更新用户   1 更新商户
	 * userCode   用户id
	 * xType      要设置的名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单
	 * casType    生成原因类型 1异常交易、2可疑交易，3预警交易、4涉恐犯罪
	 */
	public static int addXList(String userType,String userCode,String xType,String casType,String sCreateName){
		return addXList(userType,userCode,xType,casType,sCreateName,"");
	}
	
	/**
	 * 增加一条记录  客户X名单系统生成表 : RCS_X_LIST_SYS
	 * userType   0 更新用户   1 更新商户
	 * userCode   用户id
	 * xType      要设置的名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单
	 * casType    生成原因类型 1异常交易、2可疑交易，3预警交易、4涉恐犯罪
	 * casBuf     生成原因备注
	 */
	public static int addXList(String userType,String userCode,String xType,String casType,String sCreateName,String sCasBuf){
		String upSql ="insert into RCS_X_LIST_SYS(id,USER_TYPE,CLIENT_CODE,X_TYPE,CREATE_NAME,CREATE_DATETIME,CAS_BUF) " +
				"values("+RcsDefault.ID_RCS_X_LIST_SYS+",'"+userType+"','"+userCode+"','"+xType+"','"+sCreateName+"',to_char(sysdate,'yyyyMMddHH24miss'),'"+sCasBuf+"')";
		int result = Atc.ExecSql(null, upSql);
		return result;
	}
	
	/**
	 * 修改某用户名单状态
	 * @param UCFlag    0 更新用户   1 更新商户
	 * @param userCode  用户id
	 * @param riskFlag  要设置的名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单
	 * @param leavel    要设置的等级 ,如果不需要设置等级可置为null
	 */
	public static void setUserRisk(String UCFlag,String userCode,String riskFlag, String leavel){
		if(!isCanUpdateUserRisk(UCFlag,userCode,riskFlag,leavel)){
			Log.info("由于操作员已手动设置X名单，程序不能自动更新X名单状态！");
			return;
		}
		
		if(UCFlag.endsWith("0")){
			String upSql="update RCS_TRAN_USER_INFO set RISK_FLAG='"+riskFlag.trim()+"'";
			if( null!= leavel){
				if(leavel.trim().length()>0){
					upSql +=",RISK_LEAVEL='"+leavel+"'";
				}
			}
			upSql+=" where USER_CODE='"+userCode+"'";
			Atc.ExecSql(null, upSql);
		}else{
			String upSql="update RCS_TRAN_COMP_INFO set RISK_FLAG='"+riskFlag.trim()+"'";
			if( null!= leavel){
				if(leavel.trim().length()>0){
					upSql +=",RISK_LEAVEL='"+leavel+"'";
				}
			}
			upSql+=" where COMP_CODE='"+userCode+"'";
			Atc.ExecSql(null, upSql);
		}
	}
	
	/**
	 * 检查是否可以更新用户X名单
	 * @param UCFlag    0 更新用户   1 更新商户
	 * @param userCode  用户id
	 * @param riskFlag  要设置的名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单
	 * @param leavel    要设置的等级 ,如果不需要设置等级可置为null
	 */
	public static boolean isCanUpdateUserRisk(String UCFlag,String userCode,String riskFlag, String leavel){
		String sQryHisSql  = "SELECT X_TYPE ";
		       sQryHisSql += "FROM RCS_X_LIST_SYS ";
		       sQryHisSql += "WHERE USER_TYPE = '"+UCFlag+"' ";
		       sQryHisSql += "AND CLIENT_CODE = '"+userCode+"' ";
		       sQryHisSql += "AND CREATE_NAME IS NOT NULL ";
		       sQryHisSql += "AND CREATE_DATETIME LIKE (";
		       sQryHisSql += "    SELECT MAX(CREATE_DATETIME) ";
		       sQryHisSql += "    FROM RCS_X_LIST_SYS ";
		       sQryHisSql += "    WHERE USER_TYPE = '"+UCFlag+"' ";
		       sQryHisSql += "    AND CLIENT_CODE = '"+userCode+"' ";
		       sQryHisSql += "    AND CREATE_NAME IS NOT NULL ";
		       sQryHisSql += ")";
		
		int iRes = Atc.ReadRecord(sQryHisSql);
		if(0 == iRes){
			String sOldRiskFlag = Etf.getChildValue("X_TYPE");
			if(isNotEmpty(sOldRiskFlag)){
				//操作员手动设置过该人员X名单状态
				int iOld = Integer.parseInt(sOldRiskFlag);
				int iNew = Integer.parseInt(riskFlag);
				Log.info("操作员设置X名单状态为：%s，程序要更新X名单状态为：%s。",getUserRiskFlag(sOldRiskFlag),getUserRiskFlag(riskFlag));
				//原来白名单
				if(iOld == 0){
					if(iNew<=iOld){
						return false;
					}
					//灰名单
				}else if(iOld == 1){
					if(iNew<=iOld){
						return false;
					}
					//黑名单
				}else if(iOld == 2){
					return false;
					//红名单
				}else if(iOld == 3){
					return false;
				}
			}
		}else if(2 == iRes){
			Log.info("操作员未设置X名单状态，程序要更新X名单状态为：%s。",getUserRiskFlag(riskFlag));
		}
		return true;
	}
	
	/**
	 * 枚举  用户认证状态  翻译0：未认证，1 审核中 2：已通过,3未通过
	 */
	public static String getUserFlag(String userAppFlag){
		if(null != userAppFlag){
			if(userAppFlag.equals("0")) return "未认证";
			if(userAppFlag.equals("1")) return "审核中";
			if(userAppFlag.equals("2")) return "已通过";
			if(userAppFlag.equals("3")) return "未通过";
			
		} 
		return "未知";
	}
	/**
	 * 枚举  名单状态  翻译0： 白名单 1：灰名单 2：黑名单 3：红名单
	 */
	public static String getUserRiskFlag(String userRiskFlag){
		if(null != userRiskFlag){
			if(userRiskFlag.equals("0")) return "白名单";
			if(userRiskFlag.equals("1")) return "灰名单";
			if(userRiskFlag.equals("2")) return "黑名单";
			if(userRiskFlag.equals("3")) return "红名单";
			if(userRiskFlag.equals("4")) return "全部";
		} 
		return "未知";
	}
	
	/**
	 * 枚举  名单状态  翻译0： 白名单 1：灰名单 2：黑名单 3：红名单
	 */
	public static String getPaperTypeText(String sPaperType){
		if(null != sPaperType){
			if(sPaperType.equals("0"))return"身份证";
			if(sPaperType.equals("1"))return"护照";
			if(sPaperType.equals("2"))return"武警身份证";
			if(sPaperType.equals("3"))return"回乡证";
			if(sPaperType.equals("4"))return"户口本";
			if(sPaperType.equals("9"))return"企业营业执照";
			if(sPaperType.equals("C"))return"台胞证";
			if(sPaperType.equals("D"))return"外国公民护照";
			if(sPaperType.equals("F"))return"临时身份证";
			if(sPaperType.equals("G"))return"警官证";
			if(sPaperType.equals("H"))return"士兵证";
			if(sPaperType.equals("I"))return"港澳身份证";
			if(sPaperType.equals("J"))return"勘察身份证";
			if(sPaperType.equals("Z"))return"其他";
		} 
		return "未知";
	}
	
	/**
	 * 获得用户对应的异常可疑交易
	 * @param sExceptID 异常交易ID
	 */
	public static Map<String, List<Element>> getExceptTrans(String sExceptID){
		Map<String, List<Element>> resMap = new HashMap<String, List<Element>>();
		//查询该异常记录对应的信息
		String sSql  = "SELECT ID,ENTITY_TYPE,USER_CODE,COMP_CODE,EXCEPT_TRAN_FLAG ";
			   sSql += "FROM RCS_EXCEPT_TRAN_INFO ";
			   sSql += "WHERE ID = '"+sExceptID+"'";
		
		String sEntifyType = "",
			   sUserCode   = "",
			   sCompCode   = "";
		int iRes = Atc.ReadRecord(sSql);
		//查询异常交易记录信息
		if(0==iRes){
			sEntifyType = Etf.getChildValue("ENTITY_TYPE");
			sUserCode   = Etf.getChildValue("USER_CODE");
			sCompCode   = Etf.getChildValue("COMP_CODE");
			
			//用户
			if(RcsDefault.IS_USER.equals(sEntifyType)){
				resMap = getExceptTransByUser(sUserCode,sEntifyType);
			}
			
			//商户
			if(RcsDefault.IS_COMP.equals(sEntifyType)){
				resMap = getExceptTransByUser(sCompCode,sEntifyType);
			}
		}
		return resMap;
	}
	
	/**
	 * 获得用户对应的异常可疑交易
	 * @param sExceptID 异常交易ID
	 */
	public static Map<String, List<Element>> getExceptTransByUser(String sUCode,String sEntifyType){
		Map<String, List<Element>> resMap = new HashMap<String, List<Element>>();
		List<Element> resList             = null;
		String sSql                       = null;
		String sExceptFlag                = null;
		//用户
		if(RcsDefault.IS_USER.equals(sEntifyType)){
			sSql  = "SELECT ID,ENTITY_TYPE,USER_CODE UCODE,EXCEPT_TRAN_FLAG ";
			sSql += "FROM RCS_EXCEPT_TRAN_INFO ";
			sSql += "WHERE USER_CODE = '"+sUCode+"'";
		}
		
		//商户
		if(RcsDefault.IS_COMP.equals(sEntifyType)){
			sSql  = "SELECT ID,ENTITY_TYPE,COMP_CODE UCODE,EXCEPT_TRAN_FLAG ";
			sSql += "FROM RCS_EXCEPT_TRAN_INFO ";
			sSql += "WHERE COMP_CODE = '"+sUCode+"'";
		}
		
		int iRes = Atc.PagedQuery("1", "100000", "REC", sSql);
		if(0 == iRes){
			List<Element> tempList = Msg.childs("REC");
			for(Element el:tempList){
				sExceptFlag = getElementTextByKey(el, "EXCEPT_TRAN_FLAG");
				resList     = resMap.get(sExceptFlag);
				if(null == resList){
					resList = new ArrayList<Element>();
				}
				resList.add(el);
				resMap.put(sExceptFlag, resList);
			}
		}
		return resMap;
	}
	
	/**
	 * 将前台传入的"＜＞＝"替换为"<>="
	 * @param sText 传入的文本
	 * */
	public static String getRepSafeFilterChar(String sText){
		if(null != sText){
			sText = sText.replaceAll("＜", "<");
			sText = sText.replaceAll("＞", ">");
			sText = sText.replaceAll("＝", "=");
		}
		return sText;
	}
	
	/**
	 * 是否为周日
	 * */
	public static boolean isEndWeek(Date date){
		boolean bRes = false;
		int iDay = date.getDay();
		if(0==iDay){
			bRes = true;
		}
		return bRes;
	}
	
	/**
	 * 是否为月末
	 * */
	public static boolean isEndMonth(Date date){
		boolean bRes = false;
		int iMonth1 = date.getMonth();
		
		Date date2  = new Date(date.getTime()+1*24*60*60*1000);
		int iMonth2 = date2.getMonth();
		
		if(iMonth1!=iMonth2){
			bRes = true;
		}
		
		return bRes;
	}
	
	/**
	 * 是否为季度末
	 * */
	public static boolean isEndMonth3(Date date){
		boolean bRes = false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String sStr          = sdf.format(date);
		
		if("03-31".equals(sStr) || "06-30".equals(sStr) || "09-30".equals(sStr) || "12-31".equals(sStr)){
			bRes = true;
		}
		
		return bRes;
	}
	
	/**
	 * 是否为年末
	 * */
	public static boolean isEndYear(Date date){
		boolean bRes = false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String sStr          = sdf.format(date);
		
		if("12-31".equals(sStr)){
			bRes = true;
		}
		
		return bRes;
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
	 * 获取Etf节点值 若不存在则返回默认值
	 * @param sName
	 * @param sDefault
	 * @return
	 */
	public static String getEtfVal(String sName,String sDefault){
		if(Etf.getChildValue(sName)==null){
			return sDefault;
		}
		return Etf.getChildValue(sName);
	}
	
	/**
	 * 获取公共参数
	 * @param sRootName 根节点名
	 * @param sNodeName 内部节点名
	 * @return
	 */
	public static String getCommParam(String sRootName,String sNodeName){
		String sRes = null;
		try {
			//读取配置文件
			int iResult = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", sRootName, null, null, Msg.getInstance(ResourceLocator.class));
			if(0 == iResult){
				sRes = Etf.getChildValue(sNodeName);
			}
		} catch (Exception e) {
			Log.error("读取参数出现错误:%s。", e);
		}
		return sRes;
	}
}
