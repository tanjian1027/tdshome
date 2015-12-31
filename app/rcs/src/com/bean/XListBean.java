package com.bean;

/**
 * 用户X名单BEAN
 * @author linys
 */
public class XListBean {
	/**用户名称*/
	private String user_name;
	private String comp_name;
	/**用户名称*/
	private String user_code;
	private String comp_code;
	/**认证状态*/
	private String user_app_flag;
	private String comp_app_flag;
	/**证件类型*/
	private String paper_type;
	/**证件号码*/
	private String paper_code;
	/**注册日期*/
	private String register_date;
	/**历史进入次数*/
	private String nums;
	/**系统X名单类型*/
	private String sys_xtype;
	/**手工设置X名单类型*/
	private String user_xtype;
	/**客户最终名单状态**/
	private String xtypelast;
	/**手工设置日期*/
	private String update_date;
	/**商户类型名称*/
	private String comp_type_name;
	/**开户行*/
	private String cad_bank;
	/**开户帐号*/
	private String pay_cad;
	
	public String getComp_type_name() {
		return comp_type_name;
	}
	public void setComp_type_name(String comp_type_name) {
		this.comp_type_name = comp_type_name;
	}
	public String getCad_bank() {
		return cad_bank;
	}
	public void setCad_bank(String cad_bank) {
		this.cad_bank = cad_bank;
	}
	public String getPay_cad() {
		return pay_cad;
	}
	public void setPay_cad(String pay_cad) {
		this.pay_cad = pay_cad;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_app_flag() {
		return user_app_flag;
	}
	public void setUser_app_flag(String user_app_flag) {
		this.user_app_flag = user_app_flag;
	}
	public String getPaper_type() {
		return paper_type;
	}
	public void setPaper_type(String paper_type) {
		this.paper_type = paper_type;
	}
	public String getPaper_code() {
		return paper_code;
	}
	public void setPaper_code(String paper_code) {
		this.paper_code = paper_code;
	}
	public String getRegister_date() {
		return register_date;
	}
	public void setRegister_date(String register_date) {
		this.register_date = register_date;
	}
	public String getNums() {
		return nums;
	}
	public void setNums(String nums) {
		this.nums = nums;
	}
	public String getSys_xtype() {
		return sys_xtype;
	}
	public void setSys_xtype(String sys_xtype) {
		this.sys_xtype = sys_xtype;
	}
	public String getUser_xtype() {
		return user_xtype;
	}
	public void setUser_xtype(String user_xtype) {
		this.user_xtype = user_xtype;
	}
	public String getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
	/**
	 * @return the xtypelast
	 */
	public String getXtypelast() {
		return xtypelast;
	}
	/**
	 * @param xtypelast the xtypelast to set
	 */
	public void setXtypelast(String xtypelast) {
		this.xtypelast = xtypelast;
	}
	/**
	 * @return the user_code
	 */
	public String getUser_code() {
		return user_code;
	}
	/**
	 * @param user_code the user_code to set
	 */
	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}
	/**
	 * @return the comp_name
	 */
	public String getComp_name() {
		return comp_name;
	}
	/**
	 * @param comp_name the comp_name to set
	 */
	public void setComp_name(String comp_name) {
		this.comp_name = comp_name;
	}
	/**
	 * @return the comp_code
	 */
	public String getComp_code() {
		return comp_code;
	}
	/**
	 * @param comp_code the comp_code to set
	 */
	public void setComp_code(String comp_code) {
		this.comp_code = comp_code;
	}
	/**
	 * @return the comp_app_flag
	 */
	public String getComp_app_flag() {
		return comp_app_flag;
	}
	/**
	 * @param comp_app_flag the comp_app_flag to set
	 */
	public void setComp_app_flag(String comp_app_flag) {
		this.comp_app_flag = comp_app_flag;
	}

}
