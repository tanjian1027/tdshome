package com.tangdi.risk.base;

import java.util.HashMap;
import java.util.Map;

public class User {
	private String id ;
	private Map<String,Mer> merMap = new HashMap<String,Mer>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Mer> getMerMap() {
		return merMap;
	}
	public void setMerMap(Map<String, Mer> merMap) {
		this.merMap = merMap;
	}
	@Override
	public String toString() {
		return "User_"+id+"对应商户:"+merMap;
	}
}
