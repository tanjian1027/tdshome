package com.tangdi.risk.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangdi.risk.common.Tools;

public class User {
	private String USER_CODE               = "";  //用户编码        
	private String USER_NAME               = "";  //用户名称        
	private String USER_APP_FLAG           = "";  //认证状态        
	private String IS_TYPE                 = "";  //是否试用期      
	private String RISK_FLAG               = "";  //X名单状态       
	private String RISK_LEAVEL             = "";  //用户等级        
	private String PAPER_TYPE              = "";  //证件名称        
	private String PAPER_CODE              = "";  //证件号          
	private String PAPER_REM_DATE          = "";  //证件到期日期    
	private String REGISTER_DATE           = "";  //注册日期        
	private String REGISTER_DATETIME       = "";  //注册时间        
	private String IS_USE                  = "";  //是否可用        
	private String TRAN_USER_CODE          = "";  //支付平台用户编码
	private String UPDATE_DATE             = "";  //支付平台修改日期
	private String UPDATE_DATETIME         = "";  //支付平台修改时间
	private List<UserMerUnion> userMerList = new ArrayList<UserMerUnion>(); //对应用户商户汇总列表
	private List<SerialRecord> recordList  = new ArrayList<SerialRecord>(); //对应交易列表
	private Map<String, String> attrMap    = new HashMap<String, String>(); //存放属性的Map
	
	//定义User的attrMap属性的Key包含内容
	public class UserAttr{
		public final String ALL_AMOUNT_PRE_DAY      = "";  //前日累计金额
		public final String ALL_AMOUNT_PRE_WEEK     = "";  //上周累计金额
		public final String REFUND_AMOUNT_PRE_WEEK  = "";  //上周累计退款金额
		public final String ALL_AMOUNT_PRE_MONTH    = "";  //上月累计金额
		public final String REFUND_AMOUNT_PRE_MONTH = "";  //上月累计退款金额
		public final String ALL_AMOUNT_PRE3_MONTH   = "";  //前三月累计金额
		public final String ALL_AMOUNT_PRE6_MONTH   = "";  //前六月累计金额
		public final String ALL_COUNT_PRE_WEEK   	= "";  //上周累计笔数
		public final String REFUND_COUNT_PRE_WEEK   = "";  //上周累计退款笔数
		public final String ALL_COUNT_PRE_MONTH     = "";  //上月累计笔数
		public final String REFUND_COUNT_PRE_MONTH  = "";  //上月累计退款笔数
		public final String ALL_COUNT_PRE3_MONTH    = "";  //前三月累计笔数
		public final String ALL_COUNT_PRE6_MONTH    = "";  //前六月累计笔数
		public final String AVER_AMOUNT_MONTH       = "";  //当月交易平均值(该字段不在属性中了，改由封装时计算)
	}
	
	public String getUSER_CODE() {
		return USER_CODE;
	}
	public void setUSER_CODE(String user_code) {
		USER_CODE = user_code;
	}
	public String getUSER_NAME() {
		return USER_NAME;
	}
	public void setUSER_NAME(String user_name) {
		USER_NAME = user_name;
	}
	public String getUSER_APP_FLAG() {
		return USER_APP_FLAG;
	}
	public void setUSER_APP_FLAG(String user_app_flag) {
		USER_APP_FLAG = user_app_flag;
	}
	public String getIS_TYPE() {
		return IS_TYPE;
	}
	public void setIS_TYPE(String is_type) {
		IS_TYPE = is_type;
	}
	public String getRISK_FLAG() {
		return RISK_FLAG;
	}
	public void setRISK_FLAG(String risk_flag) {
		RISK_FLAG = risk_flag;
	}
	public String getRISK_LEAVEL() {
		return RISK_LEAVEL;
	}
	public void setRISK_LEAVEL(String risk_leavel) {
		RISK_LEAVEL = risk_leavel;
	}
	public String getPAPER_TYPE() {
		return PAPER_TYPE;
	}
	public void setPAPER_TYPE(String paper_type) {
		PAPER_TYPE = paper_type;
	}
	public String getPAPER_CODE() {
		return PAPER_CODE;
	}
	public void setPAPER_CODE(String paper_code) {
		PAPER_CODE = paper_code;
	}
	public String getPAPER_REM_DATE() {
		return PAPER_REM_DATE;
	}
	public void setPAPER_REM_DATE(String paper_rem_date) {
		PAPER_REM_DATE = paper_rem_date;
	}
	public String getREGISTER_DATE() {
		return REGISTER_DATE;
	}
	public void setREGISTER_DATE(String register_date) {
		REGISTER_DATE = register_date;
	}
	public String getREGISTER_DATETIME() {
		return REGISTER_DATETIME;
	}
	public void setREGISTER_DATETIME(String register_datetime) {
		REGISTER_DATETIME = register_datetime;
	}
	public String getIS_USE() {
		return IS_USE;
	}
	public void setIS_USE(String is_use) {
		IS_USE = is_use;
	}
	public String getTRAN_USER_CODE() {
		return TRAN_USER_CODE;
	}
	public void setTRAN_USER_CODE(String tran_user_code) {
		TRAN_USER_CODE = tran_user_code;
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
	public List<UserMerUnion> getUserMerList() {
		return userMerList;
	}
	public void setUserMerList(List<UserMerUnion> userMerList) {
		this.userMerList = userMerList;
	}
	public List<SerialRecord> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<SerialRecord> recordList) {
		this.recordList = recordList;
	}
	public Map<String, String> getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}
	
	//设置属性值
	public void setAttrVal(String sKey,String sVal){
		Map<String, String> attrMap = getAttrMap();
		attrMap.put(sKey, sVal);
	}
	
	//获得属性值
	public String getAttrVal(String sKey){
		String sRes = getAttrMap().get(sKey);
		if(Tools.isNull(sRes)){
			sRes = "0";
		}
		return sRes;
	}
}
