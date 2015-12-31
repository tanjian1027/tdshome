package com.rcs.interfaces;

import java.util.HashMap;
import java.util.Map;

import tangdi.engine.context.Etf;

public class LimitUserObj {
	private String  userCode 			= "";
	private Map<String, Double> attrMap = new HashMap<String, Double>();
	
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 * Set limit attr value.
	 * */
	public void setAttr(String sName,double dVal){
		attrMap.put(sName,dVal);
	}
	
	/**
	 * Set limit attr value.
	 * */
	public void setAttr(String sName,String sVal){
		if(null == sVal){
			setAttr(sName,-1);
		}else{
			setAttr(sName,Double.parseDouble(sVal));
		}
	}
	
	/**
	 * Set limit attr value.
	 * */
	public void setAttr(String sName){
		String sVal = Etf.getChildValue(sName);
		if(null == sVal){
			setAttr(sName,-1);
		}else{
			setAttr(sName,Double.parseDouble(sVal));
		}
	}
	
	/**
	 * @return if attr is null return -1.
	 * */
	public double getAttr(String sName){
		Double dRes = attrMap.get(sName);
		if(null == dRes){
			dRes = -1D;
		}
		return dRes;
	}
	
	@Override
	public String toString() {
		return "UserCode:"+userCode+" 属性信息:"+attrMap.toString();
	}
}
