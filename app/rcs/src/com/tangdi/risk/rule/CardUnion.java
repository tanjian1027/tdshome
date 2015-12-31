package com.tangdi.risk.rule;

public class CardUnion {
	private String BANK_CARD_NO    = ""; //卡号        
	private String CARD_FLG        = ""; //卡种        
	private String COIN_FLG        = ""; //币种
	private String TRAN_SOURCE     = ""; //交易来源    
	private String COMP_CODE       = ""; //商户号      
	private String STAT_DATE       = ""; //统计日期    
	private String ALL_COUNT_DAY   = ""; //当日交易次数
	private String ALL_AMOUNT_DAY  = ""; //当日交易金额
	private String MIN_AMOUNT_DAY  = ""; //当日交易最小金额
	private String MAX_AMOUNT_DAY  = ""; //当日交易最大金额
	
	public String getBANK_CARD_NO() {
		return BANK_CARD_NO;
	}
	public void setBANK_CARD_NO(String bank_card_no) {
		BANK_CARD_NO = bank_card_no;
	}
	public String getCARD_FLG() {
		return CARD_FLG;
	}
	public void setCARD_FLG(String card_flg) {
		CARD_FLG = card_flg;
	}
	public String getCOIN_FLG() {
		return COIN_FLG;
	}
	public void setCOIN_FLG(String coin_flg) {
		COIN_FLG = coin_flg;
	}
	public String getTRAN_SOURCE() {
		return TRAN_SOURCE;
	}
	public void setTRAN_SOURCE(String tran_source) {
		TRAN_SOURCE = tran_source;
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
	public String getALL_COUNT_DAY() {
		return ALL_COUNT_DAY;
	}
	public void setALL_COUNT_DAY(String all_count_day) {
		ALL_COUNT_DAY = all_count_day;
	}
	public String getALL_AMOUNT_DAY() {
		return ALL_AMOUNT_DAY;
	}
	public void setALL_AMOUNT_DAY(String all_amount_day) {
		ALL_AMOUNT_DAY = all_amount_day;
	}
	public String getMIN_AMOUNT_DAY() {
		return MIN_AMOUNT_DAY;
	}
	public void setMIN_AMOUNT_DAY(String min_amount_day) {
		MIN_AMOUNT_DAY = min_amount_day;
	}
	public String getMAX_AMOUNT_DAY() {
		return MAX_AMOUNT_DAY;
	}
	public void setMAX_AMOUNT_DAY(String max_amount_day) {
		MAX_AMOUNT_DAY = max_amount_day;
	}
}
