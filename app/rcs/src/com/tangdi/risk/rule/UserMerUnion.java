package com.tangdi.risk.rule;

public class UserMerUnion {
	public String USER_CODE             = "";  //用户编码                
	public String COMP_CODE             = "";  //商户编码                
	public String STAT_DATE             = "";  //统计日期                
	public String ALL_AMOUNT_DAY        = "";  //当日累计金额            
	public String REFUND_AMOUNT_DAY     = "";  //当日退款累计金额        
	public String SALE_AMOUNT_DAY       = "";  //当日消费累计金额        
	public String RECHARGE_AMOUNT_DAY   = "";  //当日充值累计金额        
	public String TRANSFER_AMOUNT_DAY   = "";  //当日转账累计金额        
	public String CASH_AMOUNT_DAY       = "";  //当日提现累计金额        
	public String PREDEPOSIT_AMOUNT_DAY = "";  //当日预授权完成累计金额  
	public String ALL_COUNT_DAY         = "";  //当日累计笔数            
	public String REFUND_COUNT_DAY      = "";  //当日退款笔数            
	public String SALE_COUNT_DAY        = "";  //当日消费笔数            
	public String PREDEPOSIT_COUNT_DAY  = "";  //当日预授权完成总笔数    
	public String LESS_COUNT_DAY        = "";  //当日金额小于10元的笔数  
	public String CREDIT_COUNT_DAY      = "";  //当日贷记卡交易笔数      
	public String INT_COUNT_DAY         = "";  //当日交易金额为整数的笔数
	public String AVER_AMOUNT_DAY       = "";  //当日单笔交易平均值      
	public String AVER_AMOUNT_MONTH     = "";  //月单笔交易平均值        
	public String TRAN_SOURCE           = "";  //交易来源                
	public String MAX_AMOUNT_DAY        = "";  //当日交易最大金额
	public String getMAX_AMOUNT_DAY() {
		return MAX_AMOUNT_DAY;
	}
	public void setMAX_AMOUNT_DAY(String max_amount_day) {
		MAX_AMOUNT_DAY = max_amount_day;
	}
	public String getUSER_CODE() {
		return USER_CODE;
	}
	public void setUSER_CODE(String user_code) {
		USER_CODE = user_code;
	}
	public String getCOMP_CODE() {
		return COMP_CODE;
	}
	public void setCOMP_CODE(String comp_code) {
		COMP_CODE = comp_code;
	}
	public String getSTAT_DATE() {
		return STAT_DATE;
	}
	public void setSTAT_DATE(String stat_date) {
		STAT_DATE = stat_date;
	}
	public String getALL_AMOUNT_DAY() {
		return ALL_AMOUNT_DAY;
	}
	public void setALL_AMOUNT_DAY(String all_amount_day) {
		ALL_AMOUNT_DAY = all_amount_day;
	}
	public String getREFUND_AMOUNT_DAY() {
		return REFUND_AMOUNT_DAY;
	}
	public void setREFUND_AMOUNT_DAY(String refund_amount_day) {
		REFUND_AMOUNT_DAY = refund_amount_day;
	}
	public String getSALE_AMOUNT_DAY() {
		return SALE_AMOUNT_DAY;
	}
	public void setSALE_AMOUNT_DAY(String sale_amount_day) {
		SALE_AMOUNT_DAY = sale_amount_day;
	}
	public String getRECHARGE_AMOUNT_DAY() {
		return RECHARGE_AMOUNT_DAY;
	}
	public void setRECHARGE_AMOUNT_DAY(String recharge_amount_day) {
		RECHARGE_AMOUNT_DAY = recharge_amount_day;
	}
	public String getTRANSFER_AMOUNT_DAY() {
		return TRANSFER_AMOUNT_DAY;
	}
	public void setTRANSFER_AMOUNT_DAY(String transfer_amount_day) {
		TRANSFER_AMOUNT_DAY = transfer_amount_day;
	}
	public String getCASH_AMOUNT_DAY() {
		return CASH_AMOUNT_DAY;
	}
	public void setCASH_AMOUNT_DAY(String cash_amount_day) {
		CASH_AMOUNT_DAY = cash_amount_day;
	}
	public String getPREDEPOSIT_AMOUNT_DAY() {
		return PREDEPOSIT_AMOUNT_DAY;
	}
	public void setPREDEPOSIT_AMOUNT_DAY(String predeposit_amount_day) {
		PREDEPOSIT_AMOUNT_DAY = predeposit_amount_day;
	}
	public String getALL_COUNT_DAY() {
		return ALL_COUNT_DAY;
	}
	public void setALL_COUNT_DAY(String all_count_day) {
		ALL_COUNT_DAY = all_count_day;
	}
	public String getREFUND_COUNT_DAY() {
		return REFUND_COUNT_DAY;
	}
	public void setREFUND_COUNT_DAY(String refund_count_day) {
		REFUND_COUNT_DAY = refund_count_day;
	}
	public String getSALE_COUNT_DAY() {
		return SALE_COUNT_DAY;
	}
	public void setSALE_COUNT_DAY(String sale_count_day) {
		SALE_COUNT_DAY = sale_count_day;
	}
	public String getPREDEPOSIT_COUNT_DAY() {
		return PREDEPOSIT_COUNT_DAY;
	}
	public void setPREDEPOSIT_COUNT_DAY(String predeposit_count_day) {
		PREDEPOSIT_COUNT_DAY = predeposit_count_day;
	}
	public String getLESS_COUNT_DAY() {
		return LESS_COUNT_DAY;
	}
	public void setLESS_COUNT_DAY(String less_count_day) {
		LESS_COUNT_DAY = less_count_day;
	}
	public String getCREDIT_COUNT_DAY() {
		return CREDIT_COUNT_DAY;
	}
	public void setCREDIT_COUNT_DAY(String credit_count_day) {
		CREDIT_COUNT_DAY = credit_count_day;
	}
	public String getINT_COUNT_DAY() {
		return INT_COUNT_DAY;
	}
	public void setINT_COUNT_DAY(String int_count_day) {
		INT_COUNT_DAY = int_count_day;
	}
	public String getAVER_AMOUNT_DAY() {
		return AVER_AMOUNT_DAY;
	}
	public void setAVER_AMOUNT_DAY(String aver_amount_day) {
		AVER_AMOUNT_DAY = aver_amount_day;
	}
	public String getAVER_AMOUNT_MONTH() {
		return AVER_AMOUNT_MONTH;
	}
	public void setAVER_AMOUNT_MONTH(String aver_amount_month) {
		AVER_AMOUNT_MONTH = aver_amount_month;
	}
	public String getTRAN_SOURCE() {
		return TRAN_SOURCE;
	}
	public void setTRAN_SOURCE(String tran_source) {
		TRAN_SOURCE = tran_source;
	}
}
