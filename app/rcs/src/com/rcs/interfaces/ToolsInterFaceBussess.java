package com.rcs.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

import com.util.DateUtil;
import com.util.RcsDefault;
 
@tangdi.engine.DB
public class ToolsInterFaceBussess {
	/**
	 * 返回该商户试用期天数
	 * @param compCode
	 * @return
	 */
	public static String getDaysComp(String compCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_COMP_CODE", compCode);
		
		String compSql ="select  to_date(COMP_REMOVE_REGISTER_DATE,'yyyy-mm-dd')-trunc(sysdate) as days from  RCS_TRAN_COMP_INFO  where COMP_CODE = #{TMP_COMP_CODE} ";
		
		Atc.ReadRecord(compSql);
		
		String days = Etf.getChildValue("DAYS");
		
		if(days == null) return "0";
		
		return days;
		
	}
	/**
	 * 返回该商户风险等级
	 * @param compCode
	 * @return
	 */
	public static String getRiskGrade(String compCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_COMP_CODE", compCode);
		
		String compSql = "select RISK_LEAVEL from RCS_TRAN_COMP_INFO where COMP_CODE = #{TMP_COMP_CODE}";
		
		Atc.ReadRecord(compSql);
		
		String riskLeavel = Etf.getChildValue("RISK_LEAVEL");
		
		if(riskLeavel == null) return "1";
		
		return riskLeavel;
	}
	/**
	 * 是否存在该用户
	 * @param userCode
	 * @return  存在返回true，不存在返回false
	 */
	public static boolean  isExistUser(String userCode) {
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		Log.info("检验用户:"+userCode+"是否存在");
		String userInfo = "select * from RCS_TRAN_USER_INFO where IS_USE='1' and  USER_CODE=#{TMP_USER_CODE}";
		int result1 = Atc.ReadRecord(userInfo);
		if(result1 == 2){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "用户:"+userCode+"不存在!");
			Log.info("检验此用户 不存在");
			return false;
		}else{
		    return true;
		}
	}
	
	
	
	
	/**
	 * 是否存在该企业
	 * @param userCode
	 * @return  存在返回true，不存在返回false
	 */
	public static boolean  isExistEnt(String userCode) {
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		Log.info("检验企业:"+userCode+"是否存在");
		String userInfo = "select * from RCS_TRAN_ENT_INFO where IS_USE='1' and  ENT_CODE=#{TMP_USER_CODE}";
		int result1 = Atc.ReadRecord(userInfo);
		if(result1 == 2){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "企业:"+userCode+"不存在!");
			Log.info("检验此企业 不存在");
			return false;
		}else{
		    return true;
		}
	}
	
	
	
	/**
	 * 是否存在该商户
	 * @param compCode
	 * @return 存在返回true，不存在返回false
	 */
	public static boolean isExistComp(String compCode) {
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_COMP_CODE", compCode);
		
		Log.info("检验商户是否存在");
		String compInfo = "select * from RCS_TRAN_COMP_INFO where IS_USE='1' and COMP_CODE=#{TMP_COMP_CODE}";
		int result = Atc.ReadRecord(compInfo);
		if(result == 2){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "商户信息表未查到该商户信息!");
			Log.info("检验此商户 不存在");
			return false;
		}else{
		    return true;
		}
	}
	
	/**
	 * 查看该用户名单状态
	 * @param userCode
	 * @return  0：白名单，1：灰名单，2：黑名单，3：红名单
	 */
	public static String getUserRiskFlag(String userCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		//若存在此用户 则一定有该用户名单状态
		String riskFlag = "0";
		String getUserRiskFlagSql = "select '2' RISK_FLAG from RCS_OFFENCE_INFO a,RCS_TRAN_USER_INFO b where a.paper_code=b.paper_code and b.IS_USE='1' and  USER_CODE=#{TMP_USER_CODE}";
		
		int result = Atc.ReadRecord(getUserRiskFlagSql);
		
		if(result!=-1){
			riskFlag = Etf.getChildValue("RISK_FLAG");
		}
		Log.info("查询用户："+userCode+"的名单状态为(0：白名单，1：灰名单，2：黑名单，3：红名单)："+riskFlag);
		return riskFlag;
	}
	
	
	
	/**
	 * 查看该企业名单状态
	 * @param userCode
	 * @return  0：白名单，1：灰名单，2：黑名单，3：红名单
	 */
	public static String getEntRiskFlag(String userCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		//若存在此用户 则一定有该企业名单状态
		String riskFlag = "0";
		String getUserRiskFlagSql = "select RISK_FLAG from RCS_TRAN_ENT_INFO  where IS_USE='1' and  USER_CODE=#{TMP_USER_CODE}";
		
		int result = Atc.ReadRecord(getUserRiskFlagSql);
		
		if(result!=-1){
			riskFlag = Etf.getChildValue("RISK_FLAG");
		}
		Log.info("查询企业："+userCode+"的名单状态为(0：白名单，1：灰名单，2：黑名单，3：红名单)："+riskFlag);
		return riskFlag;
	}
	
	
	
	
	/**
	 * 查看该商户名单状态
	 * @param userCode
	 * @return  0：白名单，1：灰名单，2：黑名单，3：红名单
	 */
	public static String getCompRiskFlag(String compCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_COMP_CODE", compCode);
		
		String riskFlag = "0";
		String getCompRiskFlagSql = "select '2' RISK_FLAG from RCS_OFFENCE_INFO a,RCS_TRAN_COMP_INFO b where a.paper_code=b.paper_code and b.IS_USE='1' and COMP_CODE=#{TMP_COMP_CODE}";
		
		int result = Atc.ReadRecord(getCompRiskFlagSql);
		
		if(result!=-1){
			riskFlag = Etf.getChildValue("RISK_FLAG");
		}
		Log.info("查询商户："+compCode+"的名单状态为(0：白名单，1：灰名单，2：黑名单，3：红名单)："+riskFlag);
		return riskFlag;
	}
	
	/**
	 * 查看该用户认证状态
	 * @param userCode
	 * @return  0：未认证，1 审核中 2：已通过,3未通过
	 */
	public static String getUserappFlag(String userCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		String userAppFlag = "";
		String getUserRiskFlagSql = "select USER_APP_FLAG from RCS_TRAN_USER_INFO where IS_USE='1' and USER_CODE=#{TMP_USER_CODE}";
		
		int result = Atc.ReadRecord(getUserRiskFlagSql);
		
		if(result!=-1){
			userAppFlag = Etf.getChildValue("USER_APP_FLAG");
		}
		return userAppFlag;
	}
	
	
	
	/**
	 * 查看该企业认证状态
	 * @param userCode
	 * @return  0：未认证，1 审核中 2：已通过,3未通过
	 */
	public static String getEntappFlag(String userCode){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_USER_CODE", userCode);
		
		String userAppFlag = "";
		String getUserRiskFlagSql = "select USER_APP_FLAG from RCS_TRAN_ENT_INFO where IS_USE='1' and USER_CODE=#{TMP_USER_CODE}";
		
		int result = Atc.ReadRecord(getUserRiskFlagSql);
		
		if(result!=-1){
			userAppFlag = Etf.getChildValue("USER_APP_FLAG");
		}
		return userAppFlag;
	}
	
	
	
	
	/**
	 * 查看该用户金额限制设置
	 * @param userCode   用户id
	 * @param riskFlag   该用户名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单 4 全部
	 * @param payType    支付方式 : 00全部  01网银、02终端、03消费卡、04虚拟账户、05网银、06快捷支付
	 * @return
	 */
	public static LimitUserObj getUserLimitAmt(String userCode, String riskFlag, String payType,String date,String tranType){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
		Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
		
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>进入ToolsInterFaceBussess.getUserLimitAmt方法中");
		//查看是否存在该设置  1此用户 2此名单状态 3 该时间段内 4 此支付方式
		//****1****.查询指定时间段的限额记录
		String sQueryLimit  = "select * from RCS_USER_LIMIT ";
			   sQueryLimit += " where IS_USE         = #{TMP_IS_USE} ";//启用的记录
			   sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
			   sQueryLimit += " and LIMIT_RISK_FLAG  = #{TMP_LIMIT_RISK_FLAG}";
			   sQueryLimit += " and TRAN_TYPE        = #{TMP_TRAN_TYPE}";
			   sQueryLimit += " and LIMIT_START_DATE != #{TMP_LIMIT_START_DATE}";//查询指定时间段数据 例如：--> 20130501至20130531
		//查询该用户是否存在指定用户的设置
		String sConSql  = sQueryLimit;
			  // sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
		         sConSql += " and LIMIT_USER_CODE  IN ";
		         sConSql +="  (select user_code from RCS_TRAN_USER_INFO  where paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_LIMIT_USER_CODE}))";
		
			   sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
		
	    Log.info("【ExecSql_1】 = " + sConSql);
		int iResult = Atc.ReadRecord(sConSql);
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			sConSql  = sQueryLimit;
			//sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
			sConSql += " and LIMIT_USER_CODE  IN ";
			sConSql +="  (select user_code from RCS_TRAN_USER_INFO  where paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_LIMIT_USER_CODE}))";
			
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式为全部
			
		    Log.info("【ExecSql_2】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}

		//该用户认证状态
		String userAppFlag = getUserappFlag(userCode);
		Log.info("由用户:"+userCode+"的认证状态："+userAppFlag+"转为实名未实名为：" +turnRcsAppFlag(userAppFlag)+"(0：未认证，1 审核中 2：已通过,3未通过 ==> 1：已认证(实名),2：未认证(非实名)");
	    if(iResult == 2){		
	    	//优化SQL添加至ETF
	    	Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
	    	
	    	sConSql  = sQueryLimit;
	    	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
	    	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式
		    
		    Log.info("【exictSql_3】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
	    }
	    if(iResult == 2){
	    	//优化SQL添加至ETF
	    	Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
	    	
	    	sConSql  = sQueryLimit;
	    	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
	    	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
	    	sConSql += " and TRAN_TYPE = #{TMP_TRAN_TYPE} ";
		    
		    Log.info("【exictSql_4】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
	    }
	    
	    //优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
		Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
	    
	    //****2****.查询永远时间段的限额记录
		sQueryLimit  = "select * from RCS_USER_LIMIT ";
		sQueryLimit += " where IS_USE         = #{TMP_IS_USE} ";//启用的记录
		sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
		sQueryLimit += " and LIMIT_RISK_FLAG  = #{TMP_LIMIT_RISK_FLAG}";
		sQueryLimit += " and TRAN_TYPE        = #{TMP_TRAN_TYPE}";
		sQueryLimit += " and LIMIT_START_DATE = #{TMP_LIMIT_START_DATE}";//查询永久时间段数据
		//查询该用户是否存在指定用户的设置  生效时间范围：永久的时间范围--> 19700101至29991231
		sConSql  = sQueryLimit;
		//sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
		sConSql += " and LIMIT_USER_CODE  IN ";
		sConSql +="  (select user_code from RCS_TRAN_USER_INFO  where paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_LIMIT_USER_CODE}))";
		
		
		sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
		
		if(iResult == 2){
			Log.info("【ExecSql_5】 = " + sConSql);
			iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			sConSql  = sQueryLimit;
			//sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
			sConSql += " and LIMIT_USER_CODE  IN ";
			sConSql +="  (select user_code from RCS_TRAN_USER_INFO  where paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_LIMIT_USER_CODE}))";
			
			
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式为全部
	
		    Log.info("【ExecSql_6】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
	
		//该用户认证状态
		userAppFlag = getUserappFlag(userCode);
		Log.info("由用户:"+userCode+"的认证状态："+userAppFlag+"转为实名未实名为：" +turnRcsAppFlag(userAppFlag)+"(0：未认证，1 审核中 2：已通过,3未通过 ==> 1：已认证(实名),2：未认证(非实名)");
		if(iResult == 2){		
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
			
			sConSql  = sQueryLimit;
		 	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
		 	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式
		    
		    Log.info("【exictSql_7】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		if(iResult == 2){		
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
			
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
			sConSql += " and TRAN_TYPE = #{TMP_TRAN_TYPE} ";
			    
			Log.info("【exictSql_8】 = " + sConSql);
			iResult = Atc.ReadRecord(sConSql);
		}
		
		//没有查到对应限额记录
		if(iResult == 2){
			Etf.setChildValue("NO_INFO", "没有对用户做限额设置");
			Log.info("没有对此用户做限额设置，并且没有对该类用户做限额设置");
			return null;
		}
	    
	    //封装进去
	    return packUserLimit(userCode,tranType,payType);
	}
	
	
	
	
	
	
	
	/**
	 * 查看企业金额限制设置
	 * @param userCode   用户id
	 * @param riskFlag   该用户名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单 4 全部
	 * @param payType    支付方式 : 00全部  01网银、02终端、03消费卡、04虚拟账户、05网银、06快捷支付
	 * @return
	 */
	public static LimitUserObj getEntLimitAmt(String userCode, String riskFlag, String payType,String date,String tranType){
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
		Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
		
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>进入ToolsInterFaceBussess.getUserLimitAmt方法中");
		//查看是否存在该设置  1此用户 2此名单状态 3 该时间段内 4 此支付方式
		//****1****.查询指定时间段的限额记录
		String sQueryLimit  = "select * from RCS_ENT_LIMIT ";
			   sQueryLimit += " where IS_USE         = #{TMP_IS_USE} ";//启用的记录
			   sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
			   sQueryLimit += " and LIMIT_RISK_FLAG  = #{TMP_LIMIT_RISK_FLAG}";
			   sQueryLimit += " and TRAN_TYPE        = #{TMP_TRAN_TYPE}";
			   sQueryLimit += " and LIMIT_START_DATE != #{TMP_LIMIT_START_DATE}";//查询指定时间段数据 例如：--> 20130501至20130531
		//查询该用户是否存在指定用户的设置
		String sConSql  = sQueryLimit;
			   sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
			   sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
		
	    Log.info("【ExecSql_1】 = " + sConSql);
		int iResult = Atc.ReadRecord(sConSql);
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式为全部
			
		    Log.info("【ExecSql_2】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}

		//该用户认证状态
		String userAppFlag = getEntappFlag(userCode);
		Log.info("由企业:"+userCode+"的认证状态："+userAppFlag+"转为实名未实名为：" +turnRcsAppFlag(userAppFlag)+"(0：未认证，1 审核中 2：已通过,3未通过 ==> 1：已认证(实名),2：未认证(非实名)");
	    if(iResult == 2){		
	    	//优化SQL添加至ETF
	    	Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
	    	
	    	sConSql  = sQueryLimit;
	    	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
	    	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式
		    
		    Log.info("【exictSql_3】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
	    }
	    if(iResult == 2){
	    	//优化SQL添加至ETF
	    	Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
	    	
	    	sConSql  = sQueryLimit;
	    	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
	    	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
	    	sConSql += " and TRAN_TYPE = #{TMP_TRAN_TYPE} ";
		    
		    Log.info("【exictSql_4】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
	    }
	    
	    //优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
		Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
	    
	    //****2****.查询永远时间段的限额记录
		sQueryLimit  = "select * from RCS_ENT_LIMIT ";
		sQueryLimit += " where IS_USE         = #{TMP_IS_USE} ";//启用的记录
		sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
		sQueryLimit += " and LIMIT_RISK_FLAG  = #{TMP_LIMIT_RISK_FLAG}";
		sQueryLimit += " and TRAN_TYPE        = #{TMP_TRAN_TYPE}";
		sQueryLimit += " and LIMIT_START_DATE = #{TMP_LIMIT_START_DATE}";//查询永久时间段数据
		//查询该用户是否存在指定用户的设置  生效时间范围：永久的时间范围--> 19700101至29991231
		sConSql  = sQueryLimit;
		sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
		sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
		
		if(iResult == 2){
			Log.info("【ExecSql_5】 = " + sConSql);
			iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_CODE" , userCode);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_USER_CODE  = #{TMP_LIMIT_USER_CODE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式为全部
	
		    Log.info("【ExecSql_6】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
	
		//该用户认证状态
		userAppFlag = getUserappFlag(userCode);
		Log.info("由企业:"+userCode+"的认证状态："+userAppFlag+"转为实名未实名为：" +turnRcsAppFlag(userAppFlag)+"(0：未认证，1 审核中 2：已通过,3未通过 ==> 1：已认证(实名),2：未认证(非实名)");
		if(iResult == 2){		
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
			
			sConSql  = sQueryLimit;
		 	sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
		 	sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";//支付方式
		    
		    Log.info("【exictSql_7】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		if(iResult == 2){		
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_USER_TYPE" , turnRcsAppFlag(userAppFlag));
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			Etf.setChildValue("TMP_TRAN_TYPE"       , tranType);
			
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_USER_TYPE  = #{TMP_LIMIT_USER_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
			sConSql += " and TRAN_TYPE = #{TMP_TRAN_TYPE} ";
			    
			Log.info("【exictSql_8】 = " + sConSql);
			iResult = Atc.ReadRecord(sConSql);
		}
		
		//没有查到对应限额记录
		if(iResult == 2){
			Etf.setChildValue("NO_INFO", "没有对用户做限额设置");
			Log.info("没有对此企业做限额设置，并且没有对该类企业做限额设置");
			return null;
		}
	    
	    //封装进去
	    return packUserLimit(userCode,tranType,payType);
	}
	
	
	
	
	
	
	
	// 封装用户限额即实际交易次数金额情况
	private static LimitUserObj packUserLimit(String userCode,String sTranType,String sPayType) {
		Log.info("执行方法 ToolsInterFaceBussess.packUserMap("+userCode+"): 封装用户限额即实际交易次数金额情况");
		
		String date 			  = DateUtil.getCurrentDateTime();
		LimitUserObj limitUserObj = new LimitUserObj();
		limitUserObj.setUserCode(userCode);
		
		//交易方式和支付方式应该采用限额对应的类型
		sTranType = Etf.getChildValue("TRAN_TYPE");
		sPayType  = Etf.getChildValue("LIMIT_BUS_CLIENT");
		
		//设置对应的数据库限额信息
		limitUserObj.setAttr("LIMIT_MIN_AMT");
		limitUserObj.setAttr("LIMIT_MAX_AMT");
		limitUserObj.setAttr("LIMIT_DAY_AMT");
		limitUserObj.setAttr("LIMIT_DAY_TIMES");
		limitUserObj.setAttr("LIMIT_MONTH_AMT");
		limitUserObj.setAttr("LIMIT_MONTH_TIMES");
		limitUserObj.setAttr("LIMIT_YEAR_AMT");
		limitUserObj.setAttr("LIMIT_YEAR_TIMES");
		Log.info("-----------------start--------------------------------");
		String bal=Etf.getChildValue("LIMIT_BALANCE_AMT");
		Log.info("执行方法 LIMIT_BALANCE_AMT="+bal);
		limitUserObj.setAttr("LIMIT_BALANCE_AMT");
		Log.info("-----------------end--------------------------------");
		
		
		//查询用户的实际交易金额次数
		limitUserObj.setAttr("REAL_LIMIT_DAY_AMT"    , get_UserDayAmt(userCode,date,sTranType,sPayType));
		limitUserObj.setAttr("REAL_LIMIT_DAY_TIMES"  , get_UserDayTimes(userCode,date,sTranType,sPayType));
		limitUserObj.setAttr("REAL_LIMIT_MONTH_AMT"  , get_UserMonthAmt(userCode,date,sTranType,sPayType));
		limitUserObj.setAttr("REAL_LIMIT_MONTH_TIMES", get_UserMonthTimes(userCode,date,sTranType,sPayType));
		limitUserObj.setAttr("REAL_LIMIT_YEAR_AMT"   , get_UserYearAmt(userCode,date,sTranType,sPayType));
		limitUserObj.setAttr("REAL_LIMIT_YEAR_TIMES" , get_UserYearTimes(userCode,date,sTranType,sPayType));
		
		return limitUserObj;
	}
	
	/**
	 * 查看该商户金额限制设置
	 * @param compCode   商户id
	 * @param riskFlag   该商户名单状态 0：白名单，1：灰名单，2：黑名单，3：红名单 4 全部
	 * @param payType    支付方式 : 00全部 01网银、02终端、03消费卡、04虚拟账户、05网银、06快捷支付
	 * @return
	 */
	public static LimitCompObj getCompLimitAmt(String compCode, String riskFlag, String payType,String date){
		//清掉Etf上的限额字段 避免用户的限额互相影响
		Log.info("查询商户限额前，先删掉Etf上的限额信息。");
		deleteEtfNode();
		
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
		Etf.setChildValue("TMP_LIMIT_COMP_CODE" , compCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		
		//查看是否存在该设置  1此用户 2此名单状态 3 该时间段内 4 此支付方式
		//****1****.查询指定时间段的限额记录
		String sQueryLimit  = "select * from RCS_COMP_LIMIT ";
			   sQueryLimit += " where IS_USE = #{TMP_IS_USE}";
			   sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
			   sQueryLimit += " and LIMIT_RISK_FLAG   = #{TMP_LIMIT_RISK_FLAG} ";
			   sQueryLimit += " and LIMIT_START_DATE != #{TMP_LIMIT_START_DATE}";//查询指定时间段数据 例如：--> 20130501至20130531
		
		//**查询条件：指定商户+X名单状态+指定支付方式
		String sConSql = sQueryLimit;
			   sConSql += " and LIMIT_COMP_CODE  = #{TMP_LIMIT_COMP_CODE}";
			   sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
		
	    Log.info("【ExecSql_1】 = " + sConSql);
		int iResult = Atc.ReadRecord(sConSql);
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_COMP_CODE" , compCode);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			//**查询条件：指定商户+X名单状态+全部支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_COMP_CODE  = #{TMP_LIMIT_COMP_CODE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部

		    Log.info("【ExecSql_2】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_TYPE"      , RcsDefault.LIMIT_TYPE_1);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
			
			//**查询条件：全部商户+X名单状态+指定支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_TYPE = #{TMP_LIMIT_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";

		    Log.info("【ExecSql_3】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_TYPE"      , RcsDefault.LIMIT_TYPE_1);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			//**查询条件：全部商户+X名单状态+全部支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_TYPE=#{TMP_LIMIT_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部

		    Log.info("【ExecSql_4】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		//优化SQL添加至ETF
		Etf.setChildValue("TMP_IS_USE"          , "1");
		Etf.setChildValue("TMP_DATE"            , date);
		Etf.setChildValue("TMP_LIMIT_RISK_FLAG" , riskFlag);
		Etf.setChildValue("TMP_LIMIT_START_DATE", "19700101");
		Etf.setChildValue("TMP_LIMIT_COMP_CODE" , compCode);
		Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
		
		//****2****.查询永远时间段的限额记录
		sQueryLimit  = "select * from RCS_COMP_LIMIT ";
		sQueryLimit += " where IS_USE = #{TMP_IS_USE}";
		sQueryLimit += " and to_date(#{TMP_DATE},'yyyy-MM-dd') BETWEEN to_date(LIMIT_START_DATE,'yyyy-MM-dd') AND to_date(LIMIT_END_DATE,'yyyy-MM-dd')";
		sQueryLimit += " and LIMIT_RISK_FLAG  = #{TMP_LIMIT_RISK_FLAG} ";
		sQueryLimit += " and LIMIT_START_DATE = #{TMP_LIMIT_START_DATE}";//查询永久时间段数据
	
		if(iResult == 2){
			//**查询条件：指定商户+X名单状态+指定支付方式
			sConSql = sQueryLimit;
			sConSql += " and LIMIT_COMP_CODE  = #{TMP_LIMIT_COMP_CODE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
			
			Log.info("【ExecSql_5】 = " + sConSql);
			iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_COMP_CODE" , compCode);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			//**查询条件：指定商户+X名单状态+全部支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_COMP_CODE  = #{TMP_LIMIT_COMP_CODE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
	
		    Log.info("【ExecSql_6】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_TYPE"      , RcsDefault.LIMIT_TYPE_1);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", payType);
			
			//**查询条件：全部商户+X名单状态+指定支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_TYPE=#{TMP_LIMIT_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT}";
	
		    Log.info("【ExecSql_7】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		if(iResult == 2){
			//优化SQL添加至ETF
			Etf.setChildValue("TMP_LIMIT_TYPE"      , RcsDefault.LIMIT_TYPE_1);
			Etf.setChildValue("TMP_LIMIT_BUS_CLIENT", RcsDefault.LIMIT_BUS_CLIENT_00);
			
			//**查询条件：全部商户+X名单状态+全部支付方式
			sConSql  = sQueryLimit;
			sConSql += " and LIMIT_TYPE=#{TMP_LIMIT_TYPE}";
			sConSql += " and LIMIT_BUS_CLIENT = #{TMP_LIMIT_BUS_CLIENT} ";//支付方式为全部
	
		    Log.info("【ExecSql_8】 = " + sConSql);
		    iResult = Atc.ReadRecord(sConSql);
		}
		
		if(2 == iResult){
			Etf.setChildValue("NO_INFO", "没有对商户做限额设置");
			Log.info("没有对该类商户做限额设置");
			return null;
		}
		
	    //封装进去
	    return packCompLimit(compCode,payType);
	}
	
	// 封装用户限额即实际交易次数金额情况
	private static LimitCompObj packCompLimit(String compCode,String sPayType) {
		Log.info("执行方法 ToolsInterFaceBussess.packCompLimit("+compCode+"): 封装商户限额即实际交易次数金额情况");
		
		String date 			  = DateUtil.getCurrentDateTime();
		LimitCompObj limitCompObj = new LimitCompObj();
		limitCompObj.setCompCode(compCode);
		
		//支付方式应该采用限额对应的类型
		sPayType  = Etf.getChildValue("LIMIT_BUS_CLIENT");
		
		//设置对应的数据库限额信息
		limitCompObj.setAttr("LIMIT_MIN_AMT");
		limitCompObj.setAttr("LIMIT_MAX_AMT");
		limitCompObj.setAttr("LIMIT_DAY_AMT");
		limitCompObj.setAttr("LIMIT_DAY_TIMES");
		limitCompObj.setAttr("LIMIT_MONTH_AMT");
		limitCompObj.setAttr("LIMIT_MONTH_TIMES");
		limitCompObj.setAttr("LIMIT_YEAR_AMT");
		limitCompObj.setAttr("LIMIT_YEAR_TIMES");
		
		//查询用户的实际交易金额次数
		limitCompObj.setAttr("REAL_LIMIT_DAY_AMT"    , get_CompDayAmt(compCode,date,sPayType));
		limitCompObj.setAttr("REAL_LIMIT_DAY_TIMES"  , get_CompDayTimes(compCode,date,sPayType));
		limitCompObj.setAttr("REAL_LIMIT_MONTH_AMT"  , get_CompMonthAmt(compCode,date,sPayType));
		limitCompObj.setAttr("REAL_LIMIT_MONTH_TIMES", get_CompMonthTimes(compCode,date,sPayType));
		limitCompObj.setAttr("REAL_LIMIT_YEAR_AMT"   , get_CompYearAmt(compCode,date,sPayType));
		limitCompObj.setAttr("REAL_LIMIT_YEAR_TIMES" , get_CompYearTimes(compCode,date,sPayType));
		
		return limitCompObj;
	}
	
	/**
	 * 比较该用户是否违反限额设置
	 * @param limitUser
	 * @return 违反返回 true，否则返回false
	 */
	public static boolean isOutLimitUser(LimitUserObj limitUserObj,String sAmt ){
		Log.info("执行方法 ToolsInterFaceBussess.compareComp("+limitUserObj+","+sAmt);
		
		String sUserCode = limitUserObj.getUserCode();
		double dAmt      = Double.parseDouble(sAmt);
		
		Double dLimitMinAmt     = limitUserObj.getAttr("LIMIT_MIN_AMT");
		Double dLimitMaxAmt     = limitUserObj.getAttr("LIMIT_MAX_AMT");
		Double dLimitDayAmt     = limitUserObj.getAttr("LIMIT_DAY_AMT");
		Double dLimitDayTimes   = limitUserObj.getAttr("LIMIT_DAY_TIMES");
		Double dLimitMonthAmt   = limitUserObj.getAttr("LIMIT_MONTH_AMT");
		Double dLimitMonthTimes = limitUserObj.getAttr("LIMIT_MONTH_TIMES");
		Double dLimitYearAmt    = limitUserObj.getAttr("LIMIT_YEAR_AMT");
		Double dLimitYearTimes  = limitUserObj.getAttr("LIMIT_YEAR_TIMES");
		
	
		
		
		//实际比对金额、次数应该加上当前的金额和次数
		Double dRLimitDayAmt     = limitUserObj.getAttr("REAL_LIMIT_DAY_AMT")+dAmt;
		Double dRLimitDayTimes   = limitUserObj.getAttr("REAL_LIMIT_DAY_TIMES")+1;
		Double dRLimitMonthAmt   = limitUserObj.getAttr("REAL_LIMIT_MONTH_AMT")+dAmt;
		Double dRLimitMonthTimes = limitUserObj.getAttr("REAL_LIMIT_MONTH_TIMES")+1;
		Double dRLimitYearAmt    = limitUserObj.getAttr("REAL_LIMIT_YEAR_AMT")+dAmt;
		Double dRLimitYearTimes  = limitUserObj.getAttr("REAL_LIMIT_YEAR_TIMES")+1;
		
		//提示信息去掉用户ID
		sUserCode = "";
		
		String sTemp = "日累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitDayAmt && dRLimitDayAmt>dLimitDayAmt){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "日累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitDayTimes && dRLimitDayTimes>dLimitDayTimes){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "月累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitMonthAmt && dRLimitMonthAmt>dLimitMonthAmt){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "月累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitMonthTimes && dRLimitMonthTimes>dLimitMonthTimes){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "年累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitYearAmt && dRLimitYearAmt>dLimitYearAmt){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "年累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitYearTimes && dRLimitYearTimes>dLimitYearTimes){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "单笔最小限额";
		if(-1!=dLimitMinAmt && Double.parseDouble(sAmt)<dLimitMinAmt){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "单笔最大限额";
		if(-1!=dLimitMaxAmt && Double.parseDouble(sAmt)>dLimitMaxAmt){
			Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
			return true;
		}
		
		
		sTemp = "余额限额";
		String sTranType = Etf.getChildValue("TRAN_TYPE");
		String sBalance= Etf.getChildValue("BALANCE");
		Double dLimitBananceAmt  = limitUserObj.getAttr("LIMIT_BALANCE_AMT");//
		if("1".equals(sTranType)&&StringUtils.isNotEmpty(sBalance)&&-1!=dLimitBananceAmt){//充值限额
			Double balance=Double.parseDouble(sBalance);
			if((balance+dAmt)>dLimitBananceAmt){
				Etf.setChildValue("NO_INFO", "用户:"+sUserCode+sTemp+"不通过");
				return true;
		    }
		}
		Log.info("用户:"+sUserCode+"未违反限额");
		return false;
	}
	
	/**
	 * 比较该商户是否违反限额设置
	 * @param limitUser
	 * @return 违反返回 true，否则返回false
	 */
	public static boolean isOutLimitComp(LimitCompObj limitCompObj,String sAmt ){
		Log.info("执行方法 ToolsInterFaceBussess.compareComp("+limitCompObj+", 此次交易金额:"+sAmt+"分");
		
		String sCompCode = limitCompObj.getCompCode();
		double dAmt      = Double.parseDouble(sAmt);
		
		Double dLimitMinAmt     = limitCompObj.getAttr("LIMIT_MIN_AMT");
		Double dLimitMaxAmt     = limitCompObj.getAttr("LIMIT_MAX_AMT");
		Double dLimitDayAmt     = limitCompObj.getAttr("LIMIT_DAY_AMT");
		Double dLimitDayTimes   = limitCompObj.getAttr("LIMIT_DAY_TIMES");
		Double dLimitMonthAmt   = limitCompObj.getAttr("LIMIT_MONTH_AMT");
		Double dLimitMonthTimes = limitCompObj.getAttr("LIMIT_MONTH_TIMES");
		Double dLimitYearAmt    = limitCompObj.getAttr("LIMIT_YEAR_AMT");
		Double dLimitYearTimes  = limitCompObj.getAttr("LIMIT_YEAR_TIMES");
		
		Double dRLimitDayAmt     = limitCompObj.getAttr("REAL_LIMIT_DAY_AMT")+dAmt;
		Double dRLimitDayTimes   = limitCompObj.getAttr("REAL_LIMIT_DAY_TIMES")+1;
		Double dRLimitMonthAmt   = limitCompObj.getAttr("REAL_LIMIT_MONTH_AMT")+dAmt;
		Double dRLimitMonthTimes = limitCompObj.getAttr("REAL_LIMIT_MONTH_TIMES")+1;
		Double dRLimitYearAmt    = limitCompObj.getAttr("REAL_LIMIT_YEAR_AMT")+dAmt;
		Double dRLimitYearTimes  = limitCompObj.getAttr("REAL_LIMIT_YEAR_TIMES")+1;
		
		//提示信息去掉商户ID
		sCompCode = "";
		
		String sTemp = "日累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitDayAmt && dRLimitDayAmt>dLimitDayAmt){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "日累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitDayTimes && dRLimitDayTimes>dLimitDayTimes){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "月累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitMonthAmt && dRLimitMonthAmt>dLimitMonthAmt){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "月累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitMonthTimes && dRLimitMonthTimes>dLimitMonthTimes){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "年累计金额";
		sTemp = "交易限额";
		if(-1!=dLimitYearAmt && dRLimitYearAmt>dLimitYearAmt){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "年累计次数";
		sTemp = "交易累计次数";
		if(-1!=dLimitYearTimes && dRLimitYearTimes>dLimitYearTimes){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "单笔最小限额";
		if(-1!=dLimitMinAmt && Double.parseDouble(sAmt)<dLimitMinAmt){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		sTemp = "单笔最大限额";
		if(-1!=dLimitMaxAmt && Double.parseDouble(sAmt)>dLimitMaxAmt){
			Etf.setChildValue("NO_INFO", "商户:"+sCompCode+sTemp+"不通过");
			return true;
		}
		
		Log.info("商户:"+sCompCode+"未违反限额");
		return false;
	}
	
	/**
	 * 设置用户剩余限额的返回信息
	 * @param limitUserObj
	 */
	public static void setRemainBalanceLimitUser(LimitUserObj limitUserObj,String sAmt ){
		double dAmt      = Double.parseDouble(sAmt);
		
		Double dLimitMinAmt     = limitUserObj.getAttr("LIMIT_MIN_AMT");
		Double dLimitMaxAmt     = limitUserObj.getAttr("LIMIT_MAX_AMT");
		Double dLimitDayAmt     = limitUserObj.getAttr("LIMIT_DAY_AMT");
		Double dLimitDayTimes   = limitUserObj.getAttr("LIMIT_DAY_TIMES");
		Double dLimitMonthAmt   = limitUserObj.getAttr("LIMIT_MONTH_AMT");
		Double dLimitMonthTimes = limitUserObj.getAttr("LIMIT_MONTH_TIMES");
		Double dLimitYearAmt    = limitUserObj.getAttr("LIMIT_YEAR_AMT");
		Double dLimitYearTimes  = limitUserObj.getAttr("LIMIT_YEAR_TIMES");
		
		//实际比对金额、次数应该加上当前的金额和次数
		Double dRLimitDayAmt     = limitUserObj.getAttr("REAL_LIMIT_DAY_AMT");
		Double dRLimitDayTimes   = limitUserObj.getAttr("REAL_LIMIT_DAY_TIMES");
		Double dRLimitMonthAmt   = limitUserObj.getAttr("REAL_LIMIT_MONTH_AMT");
		Double dRLimitMonthTimes = limitUserObj.getAttr("REAL_LIMIT_MONTH_TIMES");
		Double dRLimitYearAmt    = limitUserObj.getAttr("REAL_LIMIT_YEAR_AMT");
		Double dRLimitYearTimes  = limitUserObj.getAttr("REAL_LIMIT_YEAR_TIMES");
		
		//不设置限额次数时的值
		int iMax   = Integer.MAX_VALUE;
		
		//单笔最小限额
		if(-1 == dLimitMinAmt){
			Etf.setChildValue("RCS_LIMIT_USER_MIN_AMT", "-1");
		}else{
			Etf.setChildValue("RCS_LIMIT_USER_MIN_AMT", dLimitDayAmt+"");
		}
		
		//单笔最大限额
		if(-1 == dLimitMaxAmt){
			Etf.setChildValue("RCS_LIMIT_USER_MAX_AMT", iMax+"");
		}else{
			Etf.setChildValue("RCS_LIMIT_USER_MAX_AMT", dLimitMaxAmt+"");
		}
		
		String sTemp = "日累计金额";
		if(-1 == dLimitDayAmt){
			Etf.setChildValue("RCS_LIMIT_USER_DAY_AMT", iMax+"");
		}else{
			if(dRLimitDayAmt>=dLimitDayAmt){
				Etf.setChildValue("RCS_LIMIT_USER_DAY_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_DAY_AMT", dLimitDayAmt-dRLimitDayAmt+"");
			}
		}
		
		sTemp = "日累计次数";
		if(-1 == dLimitDayTimes){
			Etf.setChildValue("RCS_LIMIT_USER_DAY_TIMES", iMax+"");
		}else{
			if(dRLimitDayTimes>=dLimitDayTimes){
				Etf.setChildValue("RCS_LIMIT_USER_DAY_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_DAY_TIMES", dLimitDayTimes-dRLimitDayTimes+"");
			}
		}
		
		sTemp = "月累计金额";
		if(-1 == dLimitMonthAmt){
			Etf.setChildValue("RCS_LIMIT_USER_MONTH_AMT", iMax+"");
		}else{
			if(dRLimitMonthAmt>=dLimitMonthAmt){
				Etf.setChildValue("RCS_LIMIT_USER_MONTH_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_MONTH_AMT", dLimitMonthAmt-dRLimitMonthAmt+"");
			}
		}
		
		sTemp = "月累计次数";
		if(-1 == dLimitMonthTimes){
			Etf.setChildValue("RCS_LIMIT_USER_MONTH_TIMES", iMax+"");
		}else{
			if(dRLimitMonthTimes>=dLimitMonthTimes){
				Etf.setChildValue("RCS_LIMIT_USER_MONTH_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_MONTH_TIMES", dLimitMonthTimes-dRLimitMonthTimes+"");
			}
		}
		
		sTemp = "年累计金额";
		if(-1 == dLimitYearAmt){
			Etf.setChildValue("RCS_LIMIT_USER_YEAR_AMT", iMax+"");
		}else{
			if(dRLimitYearAmt>=dLimitYearAmt){
				Etf.setChildValue("RCS_LIMIT_USER_YEAR_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_YEAR_AMT", dLimitYearAmt-dRLimitYearAmt+"");
			}
		}
		
		sTemp = "年累计次数";
		if(-1 == dLimitYearTimes){
			Etf.setChildValue("RCS_LIMIT_USER_YEAR_TIMES", iMax+"");
		}else{
			if(dRLimitMonthTimes>=dLimitMonthTimes){
				Etf.setChildValue("RCS_LIMIT_USER_YEAR_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_USER_YEAR_TIMES", dLimitYearTimes-dRLimitYearTimes+"");
			}
		}
	}
	
	/**
	 * 设置商户剩余限额的返回信息
	 * @param limitCompObj
	 */
	public static void setRemainBalanceLimitComp(LimitCompObj limitCompObj,String sAmt ){
		double dAmt = Double.parseDouble(sAmt);
		
		Double dLimitMinAmt     = limitCompObj.getAttr("LIMIT_MIN_AMT");
		Double dLimitMaxAmt     = limitCompObj.getAttr("LIMIT_MAX_AMT");
		Double dLimitDayAmt     = limitCompObj.getAttr("LIMIT_DAY_AMT");
		Double dLimitDayTimes   = limitCompObj.getAttr("LIMIT_DAY_TIMES");
		Double dLimitMonthAmt   = limitCompObj.getAttr("LIMIT_MONTH_AMT");
		Double dLimitMonthTimes = limitCompObj.getAttr("LIMIT_MONTH_TIMES");
		Double dLimitYearAmt    = limitCompObj.getAttr("LIMIT_YEAR_AMT");
		Double dLimitYearTimes  = limitCompObj.getAttr("LIMIT_YEAR_TIMES");
		
		Double dRLimitDayAmt     = limitCompObj.getAttr("REAL_LIMIT_DAY_AMT");
		Double dRLimitDayTimes   = limitCompObj.getAttr("REAL_LIMIT_DAY_TIMES");
		Double dRLimitMonthAmt   = limitCompObj.getAttr("REAL_LIMIT_MONTH_AMT");
		Double dRLimitMonthTimes = limitCompObj.getAttr("REAL_LIMIT_MONTH_TIMES");
		Double dRLimitYearAmt    = limitCompObj.getAttr("REAL_LIMIT_YEAR_AMT");
		Double dRLimitYearTimes  = limitCompObj.getAttr("REAL_LIMIT_YEAR_TIMES");
		
		//不设置限额次数时的值
		int iMax   = Integer.MAX_VALUE;
		
		//单笔最小限额
		if(-1 == dLimitMinAmt){
			Etf.setChildValue("RCS_LIMIT_COMP_MIN_AMT", "-1");
		}else{
			Etf.setChildValue("RCS_LIMIT_COMP_MIN_AMT", dLimitDayAmt+"");
		}
		
		//单笔最大限额
		if(-1 == dLimitMaxAmt){
			Etf.setChildValue("RCS_LIMIT_COMP_MAX_AMT", iMax+"");
		}else{
			Etf.setChildValue("RCS_LIMIT_COMP_MAX_AMT", dLimitMaxAmt+"");
		}
		
		String sTemp = "日累计金额";
		if(-1 == dLimitDayAmt){
			Etf.setChildValue("RCS_LIMIT_COMP_DAY_AMT", iMax+"");
		}else{
			if(dRLimitDayAmt>=dLimitDayAmt){
				Etf.setChildValue("RCS_LIMIT_COMP_DAY_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_DAY_AMT", dLimitDayAmt-dRLimitDayAmt+"");
			}
		}
		
		sTemp = "日累计次数";
		if(-1 == dLimitDayTimes){
			Etf.setChildValue("RCS_LIMIT_COMP_DAY_TIMES", iMax+"");
		}else{
			if(dRLimitDayTimes>=dLimitDayTimes){
				Etf.setChildValue("RCS_LIMIT_COMP_DAY_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_DAY_TIMES", dLimitDayTimes-dRLimitDayTimes+"");
			}
		}
		
		sTemp = "月累计金额";
		if(-1 == dLimitMonthAmt){
			Etf.setChildValue("RCS_LIMIT_COMP_MONTH_AMT", iMax+"");
		}else{
			if(dRLimitMonthAmt>=dLimitMonthAmt){
				Etf.setChildValue("RCS_LIMIT_COMP_MONTH_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_MONTH_AMT", dLimitMonthAmt-dRLimitMonthAmt+"");
			}
		}
		
		sTemp = "月累计次数";
		if(-1 == dLimitMonthTimes){
			Etf.setChildValue("RCS_LIMIT_COMP_MONTH_TIMES", iMax+"");
		}else{
			if(dRLimitMonthTimes>=dLimitMonthTimes){
				Etf.setChildValue("RCS_LIMIT_COMP_MONTH_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_MONTH_TIMES", dLimitMonthTimes-dRLimitMonthTimes+"");
			}
		}
		
		sTemp = "年累计金额";
		if(-1 == dLimitYearAmt){
			Etf.setChildValue("RCS_LIMIT_COMP_YEAR_AMT", iMax+"");
		}else{
			if(dRLimitYearAmt>=dLimitYearAmt){
				Etf.setChildValue("RCS_LIMIT_COMP_YEAR_AMT", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_YEAR_AMT", dLimitYearAmt-dRLimitYearAmt+"");
			}
		}
		
		sTemp = "年累计次数";
		if(-1 == dLimitYearTimes){
			Etf.setChildValue("RCS_LIMIT_COMP_YEAR_TIMES", iMax+"");
		}else{
			if(dRLimitMonthTimes>=dLimitMonthTimes){
				Etf.setChildValue("RCS_LIMIT_COMP_YEAR_TIMES", "0");
			}else{
				Etf.setChildValue("RCS_LIMIT_COMP_YEAR_TIMES", dLimitYearTimes-dRLimitYearTimes+"");
			}
		}
	}
	
	/************************修改查询用户交易信息开始****************************/
	//查看该用户日累计金额
	private static String get_UserDayAmt(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,8));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += "  AND REGDT_DAY = #{TMP_REGDT_DAY}";
		   sQrySql += "  AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	    if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		  sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	    }
	 	
		/*String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY = #{TMP_REGDT_DAY}";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"日累计金额为"+sRes);
		return sRes;
	}
	
	//查看该用户日累计次数
	private static String get_UserDayTimes(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,8));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += " AND REGDT_DAY = #{TMP_REGDT_DAY}";
		   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	   if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	    }
		/*String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY = #{TMP_REGDT_DAY}";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"日累计次数为"+sRes);
		return sRes;
	}
	
	//查看该用户月累计金额
	private static String get_UserMonthAmt(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,6));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
		   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	    if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		   sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	     }
		/*String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"月累计金额为"+sRes);
		return sRes;
	}
	
	//查看该用户日累计次数
	private static String get_UserMonthTimes(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,6));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
		   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	   if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		  sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	    }
		/*String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"月累计次数为"+sRes);
		return sRes;
	}
	
	//查看该用户年累计金额
	private static String get_UserYearAmt(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,4));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD  t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
		   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	   if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		   sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	   }
		/*String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"年累计金额为"+sRes);
		return sRes;
	}
	
	//查看该用户年累计次数
	private static String get_UserYearTimes(String userCode,String time,String sTranType,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_USER_CODE"  , userCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,4));
		Etf.setChildValue("TMP_TRAN_TYPE"  , sTranType);
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
		   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD t,RCS_TRAN_USER_INFO i ";
		   sQrySql += " where  t.user_code=i.user_code";
		   sQrySql += "  and i.paper_code=(select paper_code from RCS_TRAN_USER_INFO where user_code=#{TMP_USER_CODE})";
		   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
		   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
	   if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
		   sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
	    }
		/*String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE USER_CODE = #{TMP_USER_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
			   sQrySql += " AND TRAN_TYPE = #{TMP_TRAN_TYPE}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}*/
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("用户:"+userCode+"年累计次数为"+sRes);
		return sRes;
	}
	/************************修改查询用户交易信息结束****************************/
	
	/************************修改查询商户交易信息开始****************************/
	//查看该商户日累计金额
	private static String get_CompDayAmt(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,8));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY = #{TMP_REGDT_DAY}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"日累计金额为"+sRes);
		return sRes;
	}
	
	//查看该商户日累计次数
	private static String get_CompDayTimes(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,8));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY = #{TMP_REGDT_DAY}";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"日累计次数为"+sRes);
		return sRes;
	}
	
	//查看该商户月累计金额
	private static String get_CompMonthAmt(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,6));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"月累计金额为"+sRes);
		return sRes;
	}
	
	//查看该商户月累计次数
	private static String get_CompMonthTimes(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,6));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += "FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += "WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,6)+"_%'";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"月累计次数为"+sRes);
		return sRes;
	}
	
	//查看该商户年累计金额
	private static String get_CompYearAmt(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,4));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(SUM(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += " FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += " WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"年累计金额为"+sRes);
		return sRes;
	}
	
	//查看该商户年累计金额
	private static String get_CompYearTimes(String compCode,String time,String sPayType){
		//设置至ETF上优化SQL
		Etf.setChildValue("TMP_COMP_CODE"  , compCode);
		Etf.setChildValue("TMP_REGDT_DAY"  , time.substring(0,4));
		Etf.setChildValue("TMP_TRAN_CLIENT", sPayType);
		
		String sRes    = null;
		String sQrySql = "SELECT NVL(COUNT(TRAN_AMT),0) AS SUM_NUM ";
			   sQrySql += " FROM RCS_TRAN_SERIAL_RECORD ";
			   sQrySql += " WHERE COMP_CODE = #{TMP_COMP_CODE}";
			   sQrySql += " AND REGDT_DAY like '"+time.substring(0,4)+"_%'";
		if(null != sPayType && !"0".equals(sPayType) && !"00".equals(sPayType)){
			sQrySql += " AND TRAN_CLIENT = #{TMP_TRAN_CLIENT}";
		}
		Atc.ReadRecord(sQrySql);
		sRes = Etf.getChildValue("SUM_NUM");
		Log.info("商户:"+compCode+"年累计次数为"+sRes);
		return sRes;
	}
	/************************修改查询商户交易信息结束****************************/
	
	//将该用户状态转换为风控表对应状态
	public static String turnRcsAppFlag(String userAppFlag) {
		String rt="";
		//0：未认证，1 审核中 2：已通过,3未通过 ==> 1：已认证（实名）,2：未认证（非实名）,3:指定用户
		if(userAppFlag != null && userAppFlag.equals("2")){
			rt = "1";//实名
		}else{
			rt = "2";//非实名
		}
		return rt;
	}
	
	//删除Etf节点
	public static void deleteEtfNode(){
		Etf.deleteChild("LIMIT_MIN_AMT");
		Etf.deleteChild("LIMIT_MAX_AMT");
		Etf.deleteChild("LIMIT_DAY_AMT");
		Etf.deleteChild("LIMIT_DAY_TIMES");
		Etf.deleteChild("LIMIT_MONTH_AMT");
		Etf.deleteChild("LIMIT_MONTH_TIMES");
		Etf.deleteChild("LIMIT_YEAR_AMT");
		Etf.deleteChild("LIMIT_YEAR_TIMES");
	}
	
	public static void main(String[] args){
		String str="-1";
		System.out.println(Double.parseDouble(str));
		Map<String,Double> map=new HashMap<String,Double>();
		map.put("a",(double)-1);
		System.out.println(map.get("a"));
		if(-1==Double.parseDouble("-1")){
			System.out.println("true");
		}
		
	}
	
}