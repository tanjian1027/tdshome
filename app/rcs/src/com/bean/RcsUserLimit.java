package com.bean;

public class RcsUserLimit {

	@Override
	public String toString() {
		return "RcsUserLimit [id=" + id + ", limitusertype=" + limitusertype
				+ ", limitusercode=" + limitusercode + ", limitbuscode="
				+ limitbuscode + ", limitbusclient=" + limitbusclient
				+ ", limitminamt=" + limitminamt + ", limitmaxamt="
				+ limitmaxamt + ", limitdaytimes=" + limitdaytimes
				+ ", limitdayamt=" + limitdayamt + ", limitmonthtimes="
				+ limitmonthtimes + ", limitmonthamt=" + limitmonthamt
				+ ", limitstartdate=" + limitstartdate + ", limitenddate="
				+ limitenddate + ", createname=" + createname + ", createdate="
				+ createdate + ", createdatetime=" + createdatetime
				+ ", updatename=" + updatename + ", updatedate=" + updatedate
				+ ", updatedatetime=" + updatedatetime + ", isuse=" + isuse
				+ ", limitaddinfo=" + limitaddinfo + ", limitcardtype="
				+ limitcardtype + "]";
	}

	private String id;
	private String limitRiskFlag;
	private String limitusertype;
	private String limitusercode;
	private String limitbuscode;
	private String limitbusclient;
	private String limitminamt;
	private String limitmaxamt;
	private String limitdaytimes;
	private String limitdayamt;
	private String limitmonthtimes;
	private String limitmonthamt;
	private String limitstartdate;
	private String limitenddate;
	private String createname;
	private String createdate;
	private String createdatetime;
	private String updatename;
	private String updatedate;
	private String updatedatetime;
	private String isuse;
	private String limitaddinfo;
	private String compname;
	
	/**
	 * @return the limitRiskFlag
	 */
	public String getLimitRiskFlag() {
		return limitRiskFlag;
	}

	/**
	 * @param limitRiskFlag the limitRiskFlag to set
	 */
	public void setLimitRiskFlag(String limitRiskFlag) {
		this.limitRiskFlag = limitRiskFlag;
	}

	//卡种
	private String limitcardtype;

	/**
	 * @return the compname
	 */
	public String getCompname() {
		return compname;
	}

	/**
	 * @param compname the compname to set
	 */
	public void setCompname(String compname) {
		this.compname = compname;
	}

	public String getLimitcardtype() {
		return limitcardtype;
	}

	public void setLimitcardtype(String limitcardtype) {
		this.limitcardtype = limitcardtype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLimitusertype() {
		return limitusertype;
	}

	public void setLimitusertype(String limitusertype) {
		this.limitusertype = limitusertype;
	}

	public String getLimitusercode() {
		return limitusercode;
	}

	public void setLimitusercode(String limitusercode) {
		this.limitusercode = limitusercode;
	}

	public String getLimitbuscode() {
		return limitbuscode;
	}

	public void setLimitbuscode(String limitbuscode) {
		this.limitbuscode = limitbuscode;
	}

	public String getLimitbusclient() {
		return limitbusclient;
	}

	public void setLimitbusclient(String limitbusclient) {
		this.limitbusclient = limitbusclient;
	}

	public String getLimitminamt() {
		return limitminamt;
	}

	public void setLimitminamt(String limitminamt) {
		this.limitminamt = limitminamt;
	}

	public String getLimitmaxamt() {
		return limitmaxamt;
	}

	public void setLimitmaxamt(String limitmaxamt) {
		this.limitmaxamt = limitmaxamt;
	}

	public String getLimitdaytimes() {
		return limitdaytimes;
	}

	public void setLimitdaytimes(String limitdaytimes) {
		this.limitdaytimes = limitdaytimes;
	}

	public String getLimitdayamt() {
		return limitdayamt;
	}

	public void setLimitdayamt(String limitdayamt) {
		this.limitdayamt = limitdayamt;
	}

	public String getLimitmonthtimes() {
		return limitmonthtimes;
	}

	public void setLimitmonthtimes(String limitmonthtimes) {
		this.limitmonthtimes = limitmonthtimes;
	}

	public String getLimitmonthamt() {
		return limitmonthamt;
	}

	public void setLimitmonthamt(String limitmonthamt) {
		this.limitmonthamt = limitmonthamt;
	}

	public String getLimitstartdate() {
		return limitstartdate;
	}

	public void setLimitstartdate(String limitstartdate) {
		this.limitstartdate = limitstartdate;
	}

	public String getLimitenddate() {
		return limitenddate;
	}

	public void setLimitenddate(String limitenddate) {
		this.limitenddate = limitenddate;
	}

	public String getCreatename() {
		return createname;
	}

	public void setCreatename(String createname) {
		this.createname = createname;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	public String getUpdatename() {
		return updatename;
	}

	public void setUpdatename(String updatename) {
		this.updatename = updatename;
	}

	public String getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

	public String getUpdatedatetime() {
		return updatedatetime;
	}

	public void setUpdatedatetime(String updatedatetime) {
		this.updatedatetime = updatedatetime;
	}

	public String getIsuse() {
		return isuse;
	}

	public void setIsuse(String isuse) {
		this.isuse = isuse;
	}

	public String getLimitaddinfo() {
		return limitaddinfo;
	}

	public void setLimitaddinfo(String limitaddinfo) {
		this.limitaddinfo = limitaddinfo;
	}
}
