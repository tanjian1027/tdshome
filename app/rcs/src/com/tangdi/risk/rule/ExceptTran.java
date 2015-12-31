package com.tangdi.risk.rule;

import java.util.ArrayList;
import java.util.List;

public class ExceptTran {
	private String ID                         = "";  //交易ID        
	private String ENTITY_TYPE                = "";  //主体类型      
	private String TRAN_SOURCE                = "";  //交易来源      
	private String USER_CODE                  = "";  //用户编号      
	private String COMP_CODE                  = "";  //商户编号      
	private String BANK_CARD_NO               = "";  //卡号          
	private String BANK_DEP_NO                = "";  //发卡行机构代码
	private String BANK_SIGN_VAL              = "";  //发卡行标识取值
	private String REGDT_DAY                  = "";  //登录日期      
	private String WARN_TYPE                  = "";  //异常交易类型  
	private String RULE_CODE                  = "";  //违反规则编号  
	private String EXCEPT_TRAN_FLAG           = "";  //交易状态      
	private String UPDATE_NAME                = "";  //维护人        
	private String UPDATE_DATE                = "";  //维护日期      
	private String UPDATE_DATETIME            = "";  //维护时间      
	private String EXCEPT_TRAN_ADD            = "";  //交易备注      
	private List<ExceptTranDetail> detailList = new ArrayList<ExceptTranDetail>();
	
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	public String getENTITY_TYPE() {
		return ENTITY_TYPE;
	}
	public void setENTITY_TYPE(String entity_type) {
		ENTITY_TYPE = entity_type;
	}
	public String getTRAN_SOURCE() {
		return TRAN_SOURCE;
	}
	public void setTRAN_SOURCE(String tran_source) {
		TRAN_SOURCE = tran_source;
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
	public String getBANK_CARD_NO() {
		return BANK_CARD_NO;
	}
	public void setBANK_CARD_NO(String bank_card_no) {
		BANK_CARD_NO = bank_card_no;
	}
	public String getBANK_DEP_NO() {
		return BANK_DEP_NO;
	}
	public void setBANK_DEP_NO(String bank_dep_no) {
		BANK_DEP_NO = bank_dep_no;
	}
	public String getBANK_SIGN_VAL() {
		return BANK_SIGN_VAL;
	}
	public void setBANK_SIGN_VAL(String bank_sign_val) {
		BANK_SIGN_VAL = bank_sign_val;
	}
	public String getREGDT_DAY() {
		return REGDT_DAY;
	}
	public void setREGDT_DAY(String regdt_day) {
		REGDT_DAY = regdt_day;
	}
	public String getWARN_TYPE() {
		return WARN_TYPE;
	}
	public void setWARN_TYPE(String warn_type) {
		WARN_TYPE = warn_type;
	}
	public String getRULE_CODE() {
		return RULE_CODE;
	}
	public void setRULE_CODE(String rule_code) {
		RULE_CODE = rule_code;
	}
	public String getEXCEPT_TRAN_FLAG() {
		return EXCEPT_TRAN_FLAG;
	}
	public void setEXCEPT_TRAN_FLAG(String except_tran_flag) {
		EXCEPT_TRAN_FLAG = except_tran_flag;
	}
	public String getUPDATE_NAME() {
		return UPDATE_NAME;
	}
	public void setUPDATE_NAME(String update_name) {
		UPDATE_NAME = update_name;
	}
	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String update_date) {
		UPDATE_DATE = update_date;
	}
	public String getUPDATE_DATETIME() {
		return UPDATE_DATETIME;
	}
	public void setUPDATE_DATETIME(String update_datetime) {
		UPDATE_DATETIME = update_datetime;
	}
	public String getEXCEPT_TRAN_ADD() {
		return EXCEPT_TRAN_ADD;
	}
	public void setEXCEPT_TRAN_ADD(String except_tran_add) {
		EXCEPT_TRAN_ADD = except_tran_add;
	}
	public List<ExceptTranDetail> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<ExceptTranDetail> detailList) {
		this.detailList = detailList;
	}
}
