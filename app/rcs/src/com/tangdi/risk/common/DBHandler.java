package com.tangdi.risk.common;

import java.sql.ResultSet;
import java.sql.Statement;

import tangdi.engine.context.Log;

public class DBHandler {
	public static ResultSet execQrySql(Statement stmt,String sQry){
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sQry);
		} catch (Exception e) {
			Log.error(e, "execQrySql出现错误："+sQry);
		}
		return rs;
	}
}
