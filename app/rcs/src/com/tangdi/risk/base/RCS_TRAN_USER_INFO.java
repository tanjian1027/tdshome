package com.tangdi.risk.base;

public class RCS_TRAN_USER_INFO {
	public static final String TABLE_NAME        = "RCS_TRAN_USER_INFO"; //表名
	public static final String USER_CODE         = "USER_CODE";          //用户编码                                                    
	public static final String USER_NAME         = "USER_NAME";          //用户名称                                                    
	public static final String USER_APP_FLAG     = "USER_APP_FLAG";      //认证状态          0：未认证，1 审核中 2：已通过,,3未通过    
	public static final String IS_TYPE           = "IS_TYPE";            //是否试用期        1：试用期，0：非试用期                    
	public static final String RISK_FLAG         = "RISK_FLAG";          //X名单状态         0：白名单，1：灰名单，2：黑名单，3：红名单
	public static final String RISK_LEAVEL       = "RISK_LEAVEL";        //用户等级          1：一级，2：二级，3：三级，4:四级         
	public static final String PAPER_TYPE        = "PAPER_TYPE";         //证件名称                                                    
	public static final String PAPER_CODE        = "PAPER_CODE";         //证件号                                                      
	public static final String PAPER_REM_DATE    = "PAPER_REM_DATE";     //证件到期日期                                                
	public static final String REGISTER_DATE     = "REGISTER_DATE";      //注册日期                                                    
	public static final String REGISTER_DATETIME = "REGISTER_DATETIME";  //注册时间                                                    
	public static final String IS_USE            = "IS_USE";             //是否可用          1可用，0 不可用                           
	public static final String TRAN_USER_CODE    = "TRAN_USER_CODE";     //支付平台用户编码  支付平台唯一标识，保持和支付平台关联      
	public static final String UPDATE_DATE       = "UPDATE_DATE";        //支付平台修改日期  支付平台修改日期                          
	public static final String UPDATE_DATETIME   = "UPDATE_DATETIME";    //支付平台修改时间  支付平台修改时间                          
	public static final String USER_ADDRESS      = "USER_ADDRESS";       //客户地址
	
	//获得指定时间所有交易用户ID
	public static String getAllUserIDByDate(){
		String 
		sRes = "SELECT DISTINCT("+USER_CODE+") as "+USER_CODE;
		sRes += " FROM "+TABLE_NAME;
		sRes += " ORDER BY "+USER_CODE+" ASC ";
		return sRes;
	}
}
