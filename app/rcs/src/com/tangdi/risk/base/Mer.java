package com.tangdi.risk.base;

import java.util.HashMap;
import java.util.Map;

public class Mer {
	private String id ;
	private Map<String, String> baseInfo = new HashMap<String, String>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, String> getBaseInfo() {
		return baseInfo;
	}
	public void setBaseInfo(Map<String, String> baseInfo) {
		this.baseInfo = baseInfo;
	}
	@Override
	public String toString() {
		String sStr = "Mer_"+id+"对应基本值:";
		sStr += baseInfo;
		return sStr;
	}
}
