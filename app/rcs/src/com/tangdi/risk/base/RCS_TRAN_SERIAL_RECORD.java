package com.tangdi.risk.base;
/*
 * @desc   映射数据库中的交易流水表
 * @author Jason Meng
 * */
public class RCS_TRAN_SERIAL_RECORD {
	public static final String TABLE_NAME    = "RCS_TRAN_SERIAL_RECORD";//表名
	public static final String ID            = "ID";                    //交易ID        
	public static final String TRAN_SOURCE   = "TRAN_SOURCE";           //交易来源      
	public static final String CLIENT_TYPE   = "CLIENT_TYPE";           //客户类型      
	public static final String USER_CODE     = "USER_CODE";             //用户编号 Default:-1  
	public static final String USER_NAME     = "USER_NAME";             //用户名称      
	public static final String BANK_CARD_NO  = "BANK_CARD_NO";          //银行卡号      
	public static final String BANK_DEP_NO   = "BANK_DEP_NO";           //发卡行机构代码
	public static final String BANK_CARD_LEN = "BANK_CARD_LEN";         //主卡号长度
	public static final String BANK_SIGN_VAL = "BANK_SIGN_VAL";         //发卡行标识取值
	public static final String CARD_FLG      = "CARD_FLG";              //卡种        Default:-1  
	public static final String COIN_FLG      = "COIN_FLG";              //币种          
	public static final String ID_NUMBER     = "ID_NUMBER";             //身份证号      
	public static final String MOBILE_PHONE  = "MOBILE_PHONE";          //手机号        
	public static final String TRAN_TYPE     = "TRAN_TYPE";             //交易类型      
	public static final String TRAN_CLIENT   = "TRAN_CLIENT";           //交易方式      
	public static final String TERM_ID       = "TERM_ID";               //终端号        
	public static final String TRAN_AMT      = "TRAN_AMT";              //交易金额      
	public static final String TRAN_STATUS   = "TRAN_STATUS";           //交易状态      
	public static final String COMP_CODE     = "COMP_CODE";             //商户编号      
	public static final String COMP_NAME     = "COMP_NAME";             //商户名称      
	public static final String REGDT_DAY     = "REGDT_DAY";             //交易登记日期  
	public static final String REGDT_TIME    = "REGDT_TIME";            //交易登记时间  
	public static final String TRAN_CODE     = "TRAN_CODE";             //交易流水号    
	public static final String TREM          = "TREM";                  //交易备注      
	
	//获得指定时间所有交易用户ID
	public static String getAllUserIDByDate(String startDate,String endDate){
		if(null == startDate){
			startDate = "2013-01-01";
		}
		if(null == endDate){
			endDate = "2013-01-01";
		}
		String 
		sRes = "SELECT DISTINCT("+USER_CODE+") as "+USER_CODE;
		sRes += " FROM "+TABLE_NAME;
		sRes += " WHERE ";
		sRes += "to_date("+REGDT_DAY+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
		sRes += " ORDER BY "+USER_CODE+" ASC ";
		return sRes;
	}
	
	//获得指定时间所有交易商户ID
	public static String getAllMerIDByDate(String startDate,String endDate){
		if(null == startDate){
			startDate = "2013-01-01";
		}
		if(null == endDate){
			endDate = "2013-01-01";
		}
		String 
		sRes = "SELECT DISTINCT("+COMP_CODE+") as "+COMP_CODE;
		sRes += " FROM "+TABLE_NAME;
		sRes += " WHERE ";
		sRes += "to_date("+REGDT_DAY+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
		sRes += " ORDER BY "+COMP_CODE+" ASC ";
		return sRes;
	}
	
	//获得用户商户信息
	public static String getAllUserCompInfo(String startDate,String endDate,String sSelectItem,String sCondition){
		String 
			sQrySql = "SELECT "+USER_CODE+","+COMP_CODE;
			if(null!=sSelectItem && !"".equals(sSelectItem)){
				sQrySql += ","+sSelectItem;
			}
			sQrySql += " FROM "+TABLE_NAME;
			sQrySql += " WHERE to_date("+REGDT_DAY+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd') ";
			if(null!=sCondition && !"".equals(sCondition)){
				sQrySql += " AND "+sCondition;
			}
			sQrySql += " GROUP BY "+USER_CODE+","+COMP_CODE;
			sQrySql += " ORDER BY "+USER_CODE+","+COMP_CODE;
		return sQrySql;
	}
}
