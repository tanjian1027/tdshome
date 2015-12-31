package com.tangdi.risk.pack;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangdi.risk.base.RCS_CURRCONT;
import com.tangdi.risk.base.RCS_TRAN_USER_INFO;
import com.tangdi.risk.batch.QueryTable;
import com.tangdi.risk.common.Tools;
import com.tangdi.risk.rule.IpAddress;
import com.tangdi.risk.rule.IpRecord;

public class PackIp {
	//获取封装Ip信息列表
	public List<IpAddress> getIpList(Statement stmt,String sDate,String startIp,String endIp){
		List<IpAddress> ipList       = new ArrayList<IpAddress>();
		Map<String, IpAddress> ipMap = getIpMap(stmt,sDate,startIp,endIp);
		//将Map转化为List
		for(String idTemp : ipMap.keySet()){
			ipList.add(ipMap.get(idTemp));
		}
		return ipList;
	}
	
	//获取封装用户信息Map
	public Map<String, IpAddress> getIpMap(Statement stmt,String sDate,String startIp,String endIp){
		Map<String, IpAddress> ipMap = new HashMap<String, IpAddress>();
		
		//如果传入的IP为空则封装当日所有IP的信息
		if(Tools.isNull(startIp)){
			List<String> listIp = QueryTable.getListIp(stmt,sDate,sDate);
			if(listIp.size() == 0){
				return ipMap;
			}
			startIp = listIp.get(0);
			endIp   = listIp.get(listIp.size()-1);
		}
		
		//计算前DAYS天前的日期
		String startDate = sDate;
		String endDate   = sDate;
		
		//封装IP交易流水信息至列表
		ipMap = getIpMapSerialRecord(stmt,startDate,endDate,startIp,endIp,ipMap);
		
		return ipMap;
	}
	
	//封装交易流水信息至列表
	public Map<String, IpAddress> getIpMapSerialRecord(Statement stmt,String startDate,String endDate,String startIp,String endIp,Map<String, IpAddress> ipMap){
		startDate = startDate+" 00:00:00";
		endDate   = endDate  +" 23:59:59";
		String 
			sQrySql = "SELECT ";
			sQrySql += "a."+RCS_CURRCONT.PRI_ID          +",";
			sQrySql += "a."+RCS_CURRCONT.USER_CODE       +",";
			sQrySql += "a."+RCS_CURRCONT.CURENT_DATE     +",";
			sQrySql += "a."+RCS_CURRCONT.PAY_TYPE        +",";
			sQrySql += "a."+RCS_CURRCONT.PAY_TYPE_RESULT +",";
			sQrySql += "a."+RCS_CURRCONT.TCODE           +",";
			sQrySql += "a."+RCS_CURRCONT.TCODE_RESULT    +",";
			sQrySql += "a."+RCS_CURRCONT.STATUS          +",";
			sQrySql += "a."+RCS_CURRCONT.PRDNUMBER       +",";
			sQrySql += "a."+RCS_CURRCONT.SOURCE          +",";
			sQrySql += "a."+RCS_CURRCONT.REQIP           +",";
			sQrySql += "a."+RCS_CURRCONT.IPCOUNTRY       +",";
			sQrySql += "a."+RCS_CURRCONT.IPAREA          +",";
			sQrySql += "a."+RCS_CURRCONT.IPREGION        +",";
			sQrySql += "a."+RCS_CURRCONT.IPCITY          +",";
			sQrySql += "a."+RCS_CURRCONT.IPCOUNTY        +",";
			sQrySql += "a."+RCS_CURRCONT.IPISP           +",";
			sQrySql += "b."+RCS_TRAN_USER_INFO.USER_ADDRESS+" ";
			sQrySql += " FROM "+RCS_CURRCONT.TABLE_NAME+" a ";
			sQrySql += " LEFT JOIN "+RCS_TRAN_USER_INFO.TABLE_NAME+" b ";
			sQrySql += " ON a."+RCS_CURRCONT.USER_CODE+" = b."+RCS_TRAN_USER_INFO.USER_CODE;
			sQrySql += " WHERE a."+RCS_CURRCONT.REQIP+" BETWEEN '"+startIp+"' AND '"+endIp+"'";
			sQrySql += " AND to_date(a."+RCS_CURRCONT.CURENT_DATE+",'yyyyMMddhh24miss') BETWEEN to_date('"+startDate+"','yyyyMMdd hh24:mi:ss') AND to_date('"+endDate+"','yyyyMMdd hh24:mi:ss')";
			sQrySql += " ORDER BY a."+RCS_CURRCONT.REQIP+",to_date(a."+RCS_CURRCONT.CURENT_DATE+",'yyyyMMddhh24miss')";
		
		ResultSet rs              = null;
		String sIp                = null;
		IpAddress ipAddress       = null;
		List<IpRecord> recordList = null;
		IpRecord ipRecord        = null;
		try {
			tangdi.engine.context.Log.info("执行SQL：%s", sQrySql);
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sIp       = rs.getString(RCS_CURRCONT.REQIP);
				ipAddress = ipMap.get(sIp);
				if(null == ipAddress){
					ipAddress = new IpAddress();
					ipAddress.setIP(rs.getString(RCS_CURRCONT.REQIP));
				}
				recordList = ipAddress.getRecordList();
				ipRecord  = new IpRecord();
				ipRecord.setPRI_ID         (rs.getString(RCS_CURRCONT.PRI_ID         ));
				ipRecord.setUSER_CODE      (rs.getString(RCS_CURRCONT.USER_CODE      ));
				ipRecord.setCURENT_DATE    (rs.getString(RCS_CURRCONT.CURENT_DATE    ));
				ipRecord.setPAY_TYPE       (rs.getString(RCS_CURRCONT.PAY_TYPE       ));
				ipRecord.setPAY_TYPE_RESULT(rs.getString(RCS_CURRCONT.PAY_TYPE_RESULT));
				ipRecord.setTCODE          (rs.getString(RCS_CURRCONT.TCODE          ));
				ipRecord.setTCODE_RESULT   (rs.getString(RCS_CURRCONT.TCODE_RESULT   ));
				ipRecord.setSTATUS         (rs.getString(RCS_CURRCONT.STATUS         ));
				ipRecord.setPRDNUMBER      (rs.getString(RCS_CURRCONT.PRDNUMBER      ));
				ipRecord.setSOURCE         (rs.getString(RCS_CURRCONT.SOURCE         ));
				ipRecord.setREQIP          (rs.getString(RCS_CURRCONT.REQIP          ));
				ipRecord.setIPCOUNTRY      (rs.getString(RCS_CURRCONT.IPCOUNTRY      ));
				ipRecord.setIPAREA         (rs.getString(RCS_CURRCONT.IPAREA         ));
				ipRecord.setIPREGION       (rs.getString(RCS_CURRCONT.IPREGION       ));
				ipRecord.setIPCITY         (rs.getString(RCS_CURRCONT.IPCITY         ));
				ipRecord.setIPCOUNTY       (rs.getString(RCS_CURRCONT.IPCOUNTY       ));
				ipRecord.setIPISP          (rs.getString(RCS_CURRCONT.IPISP          ));
				ipRecord.setUSER_ADDRESS   (rs.getString(RCS_TRAN_USER_INFO.USER_ADDRESS));
				recordList.add(ipRecord);
				ipAddress.setRecordList(recordList);
				ipMap.put(sIp, ipAddress);
			}
			tangdi.engine.context.Log.info("Map结果：%s", ipMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipMap;
	}
}
