package com.tangdi.risk.rule;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	private String RULE_CODE          = "";  //规则编号    
	private String RULE_NAME          = "";  //规则名称    
	private String RULE_TYPE          = "";  //规则类型    
	private String IS_ONLINE          = "";  //是否线上    
	private String EXCP_TYPE          = "";  //异常类型    
	private String COM_TYPE_NO        = "";  //商户业务类型
	private String RULE_DES           = "";  //规则描述    
	private String RULE_LEVEL         = "";  //规则等级    
	private String RULE_LEVEL_ITEM    = "";  //一般预警项  
	private String RULE_START_DATE    = "";  //生效日期    
	private String RULE_END_DATE      = "";  //失效日期    
	private String CREATE_NAME        = "";  //创建人      
	private String CREATE_DATE        = "";  //创建日期    
	private String CREATE_DATETIME    = "";  //创建时间    
	private String UPDATE_NAME        = "";  //维护人      
	private String UPDATE_DATE        = "";  //维护日期    
	private String UPDATE_DATETIME    = "";  //维护时间    
	private String RULE_VER           = "";  //规则版本号  
	private String BELONGSCOMPANY     = "";  //所属公司编码
	private String EXEC_RATE          = "";  //执行频率 
	private String IS_USE             = "";  //是否可用
	
	private List<RuleParam> paramList = new ArrayList<RuleParam>(); //规则对应参数列表
	public String getRULE_CODE() {
		return RULE_CODE;
	}
	public void setRULE_CODE(String rULE_CODE) {
		RULE_CODE = rULE_CODE;
	}
	public String getRULE_NAME() {
		return RULE_NAME;
	}
	public void setRULE_NAME(String rULE_NAME) {
		RULE_NAME = rULE_NAME;
	}
	public String getRULE_TYPE() {
		return RULE_TYPE;
	}
	public void setRULE_TYPE(String rULE_TYPE) {
		RULE_TYPE = rULE_TYPE;
	}
	public String getIS_ONLINE() {
		return IS_ONLINE;
	}
	public void setIS_ONLINE(String iS_ONLINE) {
		IS_ONLINE = iS_ONLINE;
	}
	public String getEXCP_TYPE() {
		return EXCP_TYPE;
	}
	public void setEXCP_TYPE(String eXCP_TYPE) {
		EXCP_TYPE = eXCP_TYPE;
	}
	public String getCOM_TYPE_NO() {
		return COM_TYPE_NO;
	}
	public void setCOM_TYPE_NO(String cOM_TYPE_NO) {
		COM_TYPE_NO = cOM_TYPE_NO;
	}
	public String getRULE_DES() {
		return RULE_DES;
	}
	public void setRULE_DES(String rULE_DES) {
		RULE_DES = rULE_DES;
	}
	public String getRULE_LEVEL() {
		return RULE_LEVEL;
	}
	public void setRULE_LEVEL(String rULE_LEVEL) {
		RULE_LEVEL = rULE_LEVEL;
	}
	public String getRULE_LEVEL_ITEM() {
		return RULE_LEVEL_ITEM;
	}
	public void setRULE_LEVEL_ITEM(String rULE_LEVEL_ITEM) {
		RULE_LEVEL_ITEM = rULE_LEVEL_ITEM;
	}
	public String getRULE_START_DATE() {
		return RULE_START_DATE;
	}
	public void setRULE_START_DATE(String rULE_START_DATE) {
		RULE_START_DATE = rULE_START_DATE;
	}
	public String getRULE_END_DATE() {
		return RULE_END_DATE;
	}
	public void setRULE_END_DATE(String rULE_END_DATE) {
		RULE_END_DATE = rULE_END_DATE;
	}
	public String getCREATE_NAME() {
		return CREATE_NAME;
	}
	public void setCREATE_NAME(String cREATE_NAME) {
		CREATE_NAME = cREATE_NAME;
	}
	public String getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	public String getCREATE_DATETIME() {
		return CREATE_DATETIME;
	}
	public void setCREATE_DATETIME(String cREATE_DATETIME) {
		CREATE_DATETIME = cREATE_DATETIME;
	}
	public String getUPDATE_NAME() {
		return UPDATE_NAME;
	}
	public void setUPDATE_NAME(String uPDATE_NAME) {
		UPDATE_NAME = uPDATE_NAME;
	}
	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}
	public String getUPDATE_DATETIME() {
		return UPDATE_DATETIME;
	}
	public void setUPDATE_DATETIME(String uPDATE_DATETIME) {
		UPDATE_DATETIME = uPDATE_DATETIME;
	}
	public String getRULE_VER() {
		return RULE_VER;
	}
	public void setRULE_VER(String rULE_VER) {
		RULE_VER = rULE_VER;
	}
	public String getBELONGSCOMPANY() {
		return BELONGSCOMPANY;
	}
	public void setBELONGSCOMPANY(String bELONGSCOMPANY) {
		BELONGSCOMPANY = bELONGSCOMPANY;
	}
	public String getIS_USE() {
		return IS_USE;
	}
	public void setIS_USE(String iS_USE) {
		IS_USE = iS_USE;
	}
	public List<RuleParam> getParamList() {
		return paramList;
	}
	public void setParamList(List<RuleParam> paramList) {
		this.paramList = paramList;
	}
	public String getEXEC_RATE() {
		return EXEC_RATE;
	}
	public void setEXEC_RATE(String exec_rate) {
		EXEC_RATE = exec_rate;
	}
}
