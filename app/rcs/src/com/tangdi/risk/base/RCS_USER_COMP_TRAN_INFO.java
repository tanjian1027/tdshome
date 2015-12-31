package com.tangdi.risk.base;

/*
 * @desc   映射数据库中的用户商户交易汇总表
 * @author Jason Meng
 */
public class RCS_USER_COMP_TRAN_INFO {
	public static final String TABLE_NAME            = "RCS_USER_COMP_TRAN_INFO";//表名
	public static final String USER_CODE             = "USER_CODE";              //用户编码                
	public static final String COMP_CODE             = "COMP_CODE";              //商户编码                
	public static final String STAT_DATE             = "STAT_DATE";              //统计日期                
	public static final String ALL_AMOUNT_DAY        = "ALL_AMOUNT_DAY";         //当日累计金额            
	public static final String REFUND_AMOUNT_DAY     = "REFUND_AMOUNT_DAY";      //当日退款累计金额        
	public static final String SALE_AMOUNT_DAY       = "SALE_AMOUNT_DAY";        //当日消费累计金额        
	public static final String RECHARGE_AMOUNT_DAY   = "RECHARGE_AMOUNT_DAY";    //当日充值累计金额        
	public static final String TRANSFER_AMOUNT_DAY   = "TRANSFER_AMOUNT_DAY";    //当日转账累计金额        
	public static final String CASH_AMOUNT_DAY       = "CASH_AMOUNT_DAY";        //当日提现累计金额        
	public static final String PREDEPOSIT_AMOUNT_DAY = "PREDEPOSIT_AMOUNT_DAY";  //当日预授权完成累计金额  
	public static final String ALL_COUNT_DAY         = "ALL_COUNT_DAY";          //当日累计笔数            
	public static final String REFUND_COUNT_DAY      = "REFUND_COUNT_DAY";       //当日退款笔数            
	public static final String SALE_COUNT_DAY        = "SALE_COUNT_DAY";         //当日消费笔数            
	public static final String PREDEPOSIT_COUNT_DAY  = "PREDEPOSIT_COUNT_DAY";   //当日预授权完成总笔数    
	public static final String LESS_COUNT_DAY        = "LESS_COUNT_DAY";         //当日金额小于10元的笔数  
	public static final String CREDIT_COUNT_DAY      = "CREDIT_COUNT_DAY";       //当日贷记卡交易笔数      
	public static final String INT_COUNT_DAY         = "INT_COUNT_DAY";          //当日交易金额为整数的笔数
	public static final String AVER_AMOUNT_DAY       = "AVER_AMOUNT_DAY";        //当日单笔交易平均值      
	//public static final String AVER_AMOUNT_MONTH     = "AVER_AMOUNT_MONTH";      //月单笔交易平均值        
	public static final String TRAN_SOURCE           = "TRAN_SOURCE";            //交易来源                
	public static final String MAX_AMOUNT_DAY        = "MAX_AMOUNT_DAY";         //当天交易最大值
}
