package com.tangdi.risk.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mer {
	private String COMP_CODE                 = "";  //商户编码        
	private String COMP_NAME                 = "";  //商户名称        
	private String COMP_SHOT_NAME            = "";  //商户简称        
	private String COMP_MCC                  = "";  //商户MCC值       
	private String COMP_TYPE                 = "";  //商户类型        
	private String COMP_CREATE_REGISTER_DATE = "";  //合同签订日期    
	private String COMP_REMOVE_REGISTER_DATE = "";  //合同到期日期    
	private String RISK_FLAG                 = "";  //名单状态        
	private String RISK_LEAVEL               = "";  //商户等级        
	private String REGISTER_DATE             = "";  //注册日期        
	private String REGISTER_DATETIME         = "";  //注册时间        
	private String IS_USE                    = "";  //是否可用        
	private String CAD_BANK                  = "";  //开户行          
	private String PAY_CAD                   = "";  //支付账号        
	private String REMARK                    = "";  //商户描述        
	private String TRAN_COMP_CODE            = "";  //支付平台用户编码
	private String UPDATE_DATE               = "";  //支付平台修改日期
	private String UPDATE_DATETIME           = "";  //支付平台修改时间
	
	private List<UserMerUnion> userMerList   = new ArrayList<UserMerUnion>(); //对应用户商户汇总列表
	private List<CardUnion> cardList         = new ArrayList<CardUnion>();    //对应卡汇总列表
	private List<SerialRecord> recordList    = new ArrayList<SerialRecord>(); //对应交易列表
	private Map<String, String> attrMap      = new HashMap<String, String>(); //存放属性的Map
	
	//定义Mer的attrMap属性的Key包含内容
	public class MerAttr{
		/**上周累计金额*/
		public final static String ALL_AMOUNT_PRE_WEEK      = "";
		/**上周累计成功交易金额*/
		public final static String SUCCESS_AMOUNT_PRE_WEEK  = "";
		/**上周累计退款金额*/
		public final static String REFUND_AMOUNT_PRE_WEEK   = "";
		/**上月累计金额*/
		public final static String ALL_AMOUNT_PRE_MONTH     = "";
		/**上月累计退款金额*/
		public final static String REFUND_AMOUNT_PRE_MONTH  = "";
		/**上月累计成功交易金额*/
		public final static String SUCCESS_AMOUNT_PRE_MONTH = "";
		/**上上月累计交易金额*/
		public final static String ALL_AMOUNT_PRE2_MONTH    = "";
		/**当日累计笔数*/
		public final static String ALL_COUNT_DAY            = "";
		/**上周累计笔数*/
		public final static String ALL_COUNT_PRE_WEEK       = "";
		/**上周累计成功交易笔数*/
		public final static String SUCCESS_COUNT_PRE_WEEK   = "";
		/**上周累计退款笔数*/
		public final static String REFUND_COUNT_PRE_WEEK    = "";
		/**上月累计笔数*/
		public final static String ALL_COUNT_PRE_MONTH      = "";
		/**上月累计退款笔数*/
		public final static String REFUND_COUNT_PRE_MONTH   = "";
		/**上月累计成功交易笔数*/
		public final static String SUCCESS_COUNT_PRE_MONTH  = "";
		/**上上月累计交易笔数*/
		public final static String ALL_COUNT_PRE2_MONTH     = "";
		/**前三月累计笔数*/
		public final static String ALL_COUNT_PRE3_MONTH    = "";  
		/**前六月累计笔数*/
		public final static String ALL_COUNT_PRE6_MONTH    = "";
	}
	
	public String getCOMP_CODE() {
		return COMP_CODE;
	}
	public void setCOMP_CODE(String comp_code) {
		COMP_CODE = comp_code;
	}
	public String getCOMP_NAME() {
		return COMP_NAME;
	}
	public void setCOMP_NAME(String comp_name) {
		COMP_NAME = comp_name;
	}
	public String getCOMP_SHOT_NAME() {
		return COMP_SHOT_NAME;
	}
	public void setCOMP_SHOT_NAME(String comp_shot_name) {
		COMP_SHOT_NAME = comp_shot_name;
	}
	public String getCOMP_MCC() {
		return COMP_MCC;
	}
	public void setCOMP_MCC(String comp_mcc) {
		COMP_MCC = comp_mcc;
	}
	public String getCOMP_TYPE() {
		return COMP_TYPE;
	}
	public void setCOMP_TYPE(String comp_type) {
		COMP_TYPE = comp_type;
	}
	public String getCOMP_CREATE_REGISTER_DATE() {
		return COMP_CREATE_REGISTER_DATE;
	}
	public void setCOMP_CREATE_REGISTER_DATE(String comp_create_register_date) {
		COMP_CREATE_REGISTER_DATE = comp_create_register_date;
	}
	public String getCOMP_REMOVE_REGISTER_DATE() {
		return COMP_REMOVE_REGISTER_DATE;
	}
	public void setCOMP_REMOVE_REGISTER_DATE(String comp_remove_register_date) {
		COMP_REMOVE_REGISTER_DATE = comp_remove_register_date;
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
	public String getCAD_BANK() {
		return CAD_BANK;
	}
	public void setCAD_BANK(String cad_bank) {
		CAD_BANK = cad_bank;
	}
	public String getPAY_CAD() {
		return PAY_CAD;
	}
	public void setPAY_CAD(String pay_cad) {
		PAY_CAD = pay_cad;
	}
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String remark) {
		REMARK = remark;
	}
	public String getTRAN_COMP_CODE() {
		return TRAN_COMP_CODE;
	}
	public void setTRAN_COMP_CODE(String tran_comp_code) {
		TRAN_COMP_CODE = tran_comp_code;
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
	public List<CardUnion> getCardList() {
		return cardList;
	}
	public void setCardList(List<CardUnion> cardList) {
		this.cardList = cardList;
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
}
