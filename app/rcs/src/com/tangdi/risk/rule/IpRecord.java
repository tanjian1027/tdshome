package com.tangdi.risk.rule;

public class IpRecord {
	private String PRI_ID          = "";//主键
	private String USER_CODE       = "";//用户\商户ID
	private String CURENT_DATE     = "";//操作日期
	private String PAY_TYPE        = "";//操作类型
	private String PAY_TYPE_RESULT = "";//操作结果
	private String TCODE           = "";
	private String TCODE_RESULT    = "";
	private String STATUS          = "";
	private String PRDNUMBER       = "";
	private String SOURCE          = "";
	private String REQIP           = "";
	private String IPCOUNTRY       = "";//国家
	private String IPAREA          = "";//地区
	private String IPREGION        = "";//省
	private String IPCITY          = "";//市
	private String IPCOUNTY        = "";//县
	private String IPISP           = "";//运营商
	private String USER_ADDRESS    = "";//注册地址
	public String getUSER_ADDRESS() {
		return USER_ADDRESS;
	}
	public void setUSER_ADDRESS(String user_address) {
		USER_ADDRESS = user_address;
	}
	public String getPRI_ID() {
		return PRI_ID;
	}
	public void setPRI_ID(String pri_id) {
		PRI_ID = pri_id;
	}
	public String getUSER_CODE() {
		return USER_CODE;
	}
	public void setUSER_CODE(String user_code) {
		USER_CODE = user_code;
	}
	public String getCURENT_DATE() {
		return CURENT_DATE;
	}
	public void setCURENT_DATE(String curent_date) {
		CURENT_DATE = curent_date;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pay_type) {
		PAY_TYPE = pay_type;
	}
	public String getPAY_TYPE_RESULT() {
		return PAY_TYPE_RESULT;
	}
	public void setPAY_TYPE_RESULT(String pay_type_result) {
		PAY_TYPE_RESULT = pay_type_result;
	}
	public String getTCODE() {
		return TCODE;
	}
	public void setTCODE(String tcode) {
		TCODE = tcode;
	}
	public String getTCODE_RESULT() {
		return TCODE_RESULT;
	}
	public void setTCODE_RESULT(String tcode_result) {
		TCODE_RESULT = tcode_result;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String status) {
		STATUS = status;
	}
	public String getPRDNUMBER() {
		return PRDNUMBER;
	}
	public void setPRDNUMBER(String prdnumber) {
		PRDNUMBER = prdnumber;
	}
	public String getSOURCE() {
		return SOURCE;
	}
	public void setSOURCE(String source) {
		SOURCE = source;
	}
	public String getREQIP() {
		return REQIP;
	}
	public void setREQIP(String reqip) {
		REQIP = reqip;
	}
	public String getIPCOUNTRY() {
		return IPCOUNTRY;
	}
	public void setIPCOUNTRY(String ipcountry) {
		IPCOUNTRY = ipcountry;
	}
	public String getIPAREA() {
		return IPAREA;
	}
	public void setIPAREA(String iparea) {
		IPAREA = iparea;
	}
	public String getIPREGION() {
		return IPREGION;
	}
	public void setIPREGION(String ipregion) {
		IPREGION = ipregion;
	}
	public String getIPCITY() {
		return IPCITY;
	}
	public void setIPCITY(String ipcity) {
		IPCITY = ipcity;
	}
	public String getIPCOUNTY() {
		return IPCOUNTY;
	}
	public void setIPCOUNTY(String ipcounty) {
		IPCOUNTY = ipcounty;
	}
	public String getIPISP() {
		return IPISP;
	}
	public void setIPISP(String ipisp) {
		IPISP = ipisp;
	}
}
