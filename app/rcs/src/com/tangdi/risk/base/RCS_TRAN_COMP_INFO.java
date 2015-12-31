package com.tangdi.risk.base;

public class RCS_TRAN_COMP_INFO {
	public static final String TABLE_NAME                = "RCS_TRAN_COMP_INFO";        //表名
	public static final String COMP_CODE                 = "COMP_CODE";                 //商户编码                                                         
	public static final String COMP_NAME                 = "COMP_NAME";                 //商户名称                                                         
	public static final String COMP_SHOT_NAME            = "COMP_SHOT_NAME";            //商户简称                                                         
	public static final String COMP_MCC                  = "COMP_MCC";                  //商户MCC值                                                        
	public static final String COMP_TYPE                 = "COMP_TYPE";                 //商户类型         商户是团购，网游，医疗等类型，便于批量商户的设置
	public static final String COMP_CREATE_REGISTER_DATE = "COMP_CREATE_REGISTER_DATE"; //合同签订日期                                                     
	public static final String COMP_REMOVE_REGISTER_DATE = "COMP_REMOVE_REGISTER_DATE"; //合同到期日期                                                     
	public static final String RISK_FLAG                 = "RISK_FLAG";                 //名单状态         0：白名单，1：灰名单，2：黑名单，3：红名单      
	public static final String RISK_LEAVEL               = "RISK_LEAVEL";               //商户等级         1：一级，2：二级，3：三级，4:四级              
	public static final String REGISTER_DATE             = "REGISTER_DATE";             //注册日期                                                         
	public static final String REGISTER_DATETIME         = "REGISTER_DATETIME";         //注册时间                                                         
	public static final String IS_USE                    = "IS_USE";                    //是否可用         1可用，0 不可用                                 
	public static final String CAD_BANK                  = "CAD_BANK";                  //开户行                                                           
	public static final String PAY_CAD                   = "PAY_CAD";                   //支付账号                                                         
	public static final String REMARK                    = "REMARK";                    //商户描述         商户描述                                        
	public static final String TRAN_COMP_CODE            = "TRAN_COMP_CODE";            //支付平台用户编码 支付平台唯一标识，保持和支付平台关联            
	public static final String UPDATE_DATE               = "UPDATE_DATE";               //支付平台修改日期 支付平台修改日期                                
	public static final String UPDATE_DATETIME           = "UPDATE_DATETIME";           //支付平台修改时间 支付平台修改时间                                
	public static final String COMP_ADDRESS              = "COMP_ADDRESS";              //客户地址
	
	//获得指定时间所有交易商户ID
	public static String getAllMerIDByDate(){
		String 
		sRes = "SELECT DISTINCT("+COMP_CODE+") as "+COMP_CODE;
		sRes += " FROM "+TABLE_NAME;
		sRes += " ORDER BY "+COMP_CODE+" ASC ";
		return sRes;
	}
}
