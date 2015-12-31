package com.tangdi.risk.rule;

public class RuleParam {
	private String RULE_CODE   = "";  //规则编号      
	private String PARAM_ID    = "";  //规则参数ID    
	private String PARAM_NAME  = "";  //参数名称      
	private String PARAM_VALUE = "";  //参数值        
	private String PARAM_TYPE  = "";  //参数类型      
	public String getRULE_CODE() {
		return RULE_CODE;
	}
	public void setRULE_CODE(String rULE_CODE) {
		RULE_CODE = rULE_CODE;
	}
	public String getPARAM_ID() {
		return PARAM_ID;
	}
	public void setPARAM_ID(String pARAM_ID) {
		PARAM_ID = pARAM_ID;
	}
	public String getPARAM_NAME() {
		return PARAM_NAME;
	}
	public void setPARAM_NAME(String pARAM_NAME) {
		PARAM_NAME = pARAM_NAME;
	}
	public String getPARAM_VALUE() {
		return PARAM_VALUE;
	}
	public void setPARAM_VALUE(String pARAM_VALUE) {
		PARAM_VALUE = pARAM_VALUE;
	}
	public String getPARAM_TYPE() {
		return PARAM_TYPE;
	}
	public void setPARAM_TYPE(String pARAM_TYPE) {
		PARAM_TYPE = pARAM_TYPE;
	}
}
