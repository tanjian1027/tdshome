package com.tangdi.risk.rule;

public class ExceptTranDetail {
	private String ID        = "";  //异常交易ID
	private String TRAN_CODE = "";  //交易流水号
	
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	public String getTRAN_CODE() {
		return TRAN_CODE;
	}
	public void setTRAN_CODE(String tran_code) {
		TRAN_CODE = tran_code;
	}
}
