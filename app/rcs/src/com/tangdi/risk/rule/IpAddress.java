package com.tangdi.risk.rule;

import java.util.ArrayList;
import java.util.List;

public class IpAddress {
	private String IP      = "";//ip地址
	private List<IpRecord> recordList  = new ArrayList<IpRecord>(); //对应交易列表
	public String getIP() {
		return IP;
	}
	public void setIP(String ip) {
		IP = ip;
	}
	public List<IpRecord> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<IpRecord> recordList) {
		this.recordList = recordList;
	}
}
