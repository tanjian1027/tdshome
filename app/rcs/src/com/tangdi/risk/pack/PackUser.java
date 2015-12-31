package com.tangdi.risk.pack;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangdi.risk.base.RCS_CURRCONT;
import com.tangdi.risk.base.RCS_TRAN_SERIAL_RECORD;
import com.tangdi.risk.base.RCS_TRAN_USER_INFO;
import com.tangdi.risk.base.RCS_USER_COMP_TRAN_INFO;
import com.tangdi.risk.batch.QueryTable;
import com.tangdi.risk.common.Tools;
import com.tangdi.risk.rule.SerialRecord;
import com.tangdi.risk.rule.User;
import com.tangdi.risk.rule.UserMerUnion;

public class PackUser {
	private static final int DAYS = 30;
	
	//获取封装用户信息列表
	public List<User> getUserList(Statement stmt,String sDate,String startUserID,String endUserID){
		List<User> userList       = new ArrayList<User>();
		Map<String, User> userMap = getUserMap(stmt,sDate,startUserID,endUserID);
		//将Map转化为List
		for(String idTemp : userMap.keySet()){
			userList.add(userMap.get(idTemp));
		}
		return userList;
	}
	
	//获取封装用户信息Map
	public Map<String, User> getUserMap(Statement stmt,String sDate,String startUserID,String endUserID){
		Map<String, User> userMap = new HashMap<String, User>();
		
		//如果传入的用户ID为空则封装所有用户的信息
		if(Tools.isNull(startUserID)){
			List<String> listUserID = QueryTable.getListUserID(stmt,sDate,sDate);
			if(listUserID.size() == 0){
				return userMap;
			}
			startUserID = listUserID.get(0);
			endUserID   = listUserID.get(listUserID.size()-1);
		}
		
		//计算前DAYS天前的日期
		String startDate = Tools.getDayByVal(sDate, -DAYS);
		String endDate   = sDate;
		
		//计算上个月起止日
		String sPreMonthFirstDay = Tools.getPreMonthFirstDay(sDate);
		String sPreMonthLastDay  = Tools.getPreMonthLastDay(sDate);
		
		//计算本月起止日
		String sMonthFirstDay = Tools.getMonthFirstDay(sDate);
		String sMonthLastDay  = Tools.getMonthLastDay(sDate);
		
		//计算前三个月起日(包括本月)
		String sPre3MonthFirstDay = Tools.getPreNMonthFirstDay(sDate, 3);
		
		//计算前六个月起日(包括本月)
		String sPre6MonthFirstDay = Tools.getPreNMonthFirstDay(sDate, 6);
		
		//封装基本信息至Map中
		userMap = getUserMapBase(stmt,startUserID,endUserID,userMap);
		
		//封装用户商户汇总信息至列表
		userMap = getUserMapUnion(stmt,startDate,endDate,startUserID,endUserID,userMap);
		
		//封装交易流水信息至列表
		userMap = getUserMapSerialRecord(stmt,startDate,endDate,startUserID,endUserID,userMap);
		
		//封装上月累计金额、笔数信息 累计退款金额、笔数信息
		userMap = getUserMapAttrPart(stmt,sPreMonthFirstDay,sPreMonthLastDay,startUserID,endUserID,userMap);
		
		//封装本月单笔交易平均值
		userMap = getUserMapAttrPart2(stmt,sMonthFirstDay,sMonthLastDay,startUserID,endUserID,userMap);
		
		//封装前三月累计金额、笔数信息至列表
		userMap = getUserMapAttrPart3(stmt,sPre3MonthFirstDay,sMonthLastDay,startUserID,endUserID,userMap,3);
		
		//封装前六月累计金额、笔数信息至列表
		userMap = getUserMapAttrPart3(stmt,sPre6MonthFirstDay,sMonthLastDay,startUserID,endUserID,userMap,6);
		
		return userMap;
	}
	
	//封装用户基本信息至列表
	public Map<String, User> getUserMapBase(Statement stmt,String startUserID,String endUserID,Map<String, User> userMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_TRAN_USER_INFO.USER_CODE         +",";
			sQrySql += RCS_TRAN_USER_INFO.USER_NAME         +",";
			sQrySql += RCS_TRAN_USER_INFO.USER_APP_FLAG     +",";
			sQrySql += RCS_TRAN_USER_INFO.IS_TYPE           +",";
			sQrySql += RCS_TRAN_USER_INFO.RISK_FLAG         +",";
			sQrySql += RCS_TRAN_USER_INFO.RISK_LEAVEL       +",";
			sQrySql += RCS_TRAN_USER_INFO.PAPER_TYPE        +",";
			sQrySql += RCS_TRAN_USER_INFO.PAPER_CODE        +",";
			sQrySql += RCS_TRAN_USER_INFO.PAPER_REM_DATE    +",";
			sQrySql += RCS_TRAN_USER_INFO.REGISTER_DATE     +",";
			sQrySql += RCS_TRAN_USER_INFO.REGISTER_DATETIME +",";
			sQrySql += RCS_TRAN_USER_INFO.IS_USE            +",";
			sQrySql += RCS_TRAN_USER_INFO.TRAN_USER_CODE    +",";
			sQrySql += RCS_TRAN_USER_INFO.UPDATE_DATE       +",";
			sQrySql += RCS_TRAN_USER_INFO.UPDATE_DATETIME   +" ";
			sQrySql += " FROM "+RCS_TRAN_USER_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_TRAN_USER_INFO.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
		
		ResultSet rs   = null;
		String sUserID = null;
		User user      = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_TRAN_USER_INFO.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
				}
				user.setUSER_CODE        (rs.getString(RCS_TRAN_USER_INFO.USER_CODE        ));
				user.setUSER_NAME        (rs.getString(RCS_TRAN_USER_INFO.USER_NAME        ));
				user.setUSER_APP_FLAG    (rs.getString(RCS_TRAN_USER_INFO.USER_APP_FLAG    ));
				user.setIS_TYPE          (rs.getString(RCS_TRAN_USER_INFO.IS_TYPE          ));
				user.setRISK_FLAG        (rs.getString(RCS_TRAN_USER_INFO.RISK_FLAG        ));
				user.setRISK_LEAVEL      (rs.getString(RCS_TRAN_USER_INFO.RISK_LEAVEL      ));
				user.setPAPER_TYPE       (rs.getString(RCS_TRAN_USER_INFO.PAPER_TYPE       ));
				user.setPAPER_CODE       (rs.getString(RCS_TRAN_USER_INFO.PAPER_CODE       ));
				user.setPAPER_REM_DATE   (rs.getString(RCS_TRAN_USER_INFO.PAPER_REM_DATE   ));
				user.setREGISTER_DATE    (rs.getString(RCS_TRAN_USER_INFO.REGISTER_DATE    ));
				user.setREGISTER_DATETIME(rs.getString(RCS_TRAN_USER_INFO.REGISTER_DATETIME));
				user.setIS_USE           (rs.getString(RCS_TRAN_USER_INFO.IS_USE           ));
				user.setTRAN_USER_CODE   (rs.getString(RCS_TRAN_USER_INFO.TRAN_USER_CODE   ));
				user.setUPDATE_DATE      (rs.getString(RCS_TRAN_USER_INFO.UPDATE_DATE      ));
				user.setUPDATE_DATETIME  (rs.getString(RCS_TRAN_USER_INFO.UPDATE_DATETIME  ));
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return userMap;
	}
	
	//封装用户商户汇总信息至列表
	public Map<String, User> getUserMapUnion(Statement stmt,String startDate,String endDate,String startUserID,String endUserID,Map<String, User> userMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE             +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.STAT_DATE             +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY        +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY     +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.SALE_AMOUNT_DAY       +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.RECHARGE_AMOUNT_DAY   +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.TRANSFER_AMOUNT_DAY   +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.CASH_AMOUNT_DAY       +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_AMOUNT_DAY +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY         +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY      +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.SALE_COUNT_DAY        +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_COUNT_DAY  +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.LESS_COUNT_DAY        +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.CREDIT_COUNT_DAY      +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.INT_COUNT_DAY         +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.AVER_AMOUNT_DAY       +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.TRAN_SOURCE           +",";
			sQrySql += RCS_USER_COMP_TRAN_INFO.MAX_AMOUNT_DAY        +" ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " ORDER BY "+RCS_USER_COMP_TRAN_INFO.USER_CODE+","+RCS_USER_COMP_TRAN_INFO.COMP_CODE+","+RCS_USER_COMP_TRAN_INFO.STAT_DATE;
		
		ResultSet rs                   = null;
		String sUserID                 = null;
		User user                      = null;
		List<UserMerUnion> userMerList = null;
		UserMerUnion umu               = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
					user.setUSER_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE));
				}
				userMerList = user.getUserMerList();
				umu         = new UserMerUnion();
				umu.setUSER_CODE            (rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE            ));
				umu.setCOMP_CODE            (rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE            ));
				umu.setSTAT_DATE            (rs.getString(RCS_USER_COMP_TRAN_INFO.STAT_DATE            ));
				umu.setALL_AMOUNT_DAY       (rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY       ));
				umu.setREFUND_AMOUNT_DAY    (rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY    ));
				umu.setSALE_AMOUNT_DAY      (rs.getString(RCS_USER_COMP_TRAN_INFO.SALE_AMOUNT_DAY      ));
				umu.setRECHARGE_AMOUNT_DAY  (rs.getString(RCS_USER_COMP_TRAN_INFO.RECHARGE_AMOUNT_DAY  ));
				umu.setTRANSFER_AMOUNT_DAY  (rs.getString(RCS_USER_COMP_TRAN_INFO.TRANSFER_AMOUNT_DAY  ));
				umu.setCASH_AMOUNT_DAY      (rs.getString(RCS_USER_COMP_TRAN_INFO.CASH_AMOUNT_DAY      ));
				umu.setPREDEPOSIT_AMOUNT_DAY(rs.getString(RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_AMOUNT_DAY));
				umu.setALL_COUNT_DAY        (rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY        ));
				umu.setREFUND_COUNT_DAY     (rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY     ));
				umu.setSALE_COUNT_DAY       (rs.getString(RCS_USER_COMP_TRAN_INFO.SALE_COUNT_DAY       ));
				umu.setPREDEPOSIT_COUNT_DAY (rs.getString(RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_COUNT_DAY ));
				umu.setLESS_COUNT_DAY       (rs.getString(RCS_USER_COMP_TRAN_INFO.LESS_COUNT_DAY       ));
				umu.setCREDIT_COUNT_DAY     (rs.getString(RCS_USER_COMP_TRAN_INFO.CREDIT_COUNT_DAY     ));
				umu.setINT_COUNT_DAY        (rs.getString(RCS_USER_COMP_TRAN_INFO.INT_COUNT_DAY        ));
				umu.setAVER_AMOUNT_DAY      (rs.getString(RCS_USER_COMP_TRAN_INFO.AVER_AMOUNT_DAY      ));
				umu.setTRAN_SOURCE          (rs.getString(RCS_USER_COMP_TRAN_INFO.TRAN_SOURCE          ));
				umu.setMAX_AMOUNT_DAY       (rs.getString(RCS_USER_COMP_TRAN_INFO.MAX_AMOUNT_DAY       ));
				userMerList.add(umu);
				user.setUserMerList(userMerList);
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userMap;
	}
	
	//封装交易流水信息至列表
	public Map<String, User> getUserMapSerialRecord(Statement stmt,String startDate,String endDate,String startUserID,String endUserID,Map<String, User> userMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_TRAN_SERIAL_RECORD.ID            +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_SOURCE   +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.CLIENT_TYPE   +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.USER_CODE     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.USER_NAME     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.BANK_CARD_NO  +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.BANK_DEP_NO   +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.BANK_CARD_LEN +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.BANK_SIGN_VAL +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.CARD_FLG      +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.COIN_FLG      +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.ID_NUMBER     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.MOBILE_PHONE  +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_TYPE     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_CLIENT   +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TERM_ID       +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_AMT      +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_STATUS   +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.COMP_CODE     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.COMP_NAME     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.REGDT_DAY     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.REGDT_TIME    +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TRAN_CODE     +",";
			sQrySql += RCS_TRAN_SERIAL_RECORD.TREM          +" ";
			sQrySql += " FROM "+RCS_TRAN_SERIAL_RECORD.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_TRAN_SERIAL_RECORD.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
			sQrySql += " AND length("+RCS_TRAN_SERIAL_RECORD.REGDT_DAY+") = 8 ";
			sQrySql += " AND to_date("+RCS_TRAN_SERIAL_RECORD.REGDT_DAY+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " ORDER BY "+RCS_TRAN_SERIAL_RECORD.USER_CODE+",to_date("+RCS_TRAN_SERIAL_RECORD.REGDT_TIME+",'yyyyMMddhh24miss')";
		
		ResultSet rs                  = null;
		String sUserID                = null;
		User user                     = null;
		List<SerialRecord> recordList = null;
		SerialRecord serRecord        = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
					user.setUSER_CODE(rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE));
				}
				recordList = user.getRecordList();
				serRecord  = new SerialRecord();
				serRecord.setID           (rs.getString(RCS_TRAN_SERIAL_RECORD.ID           ));
				serRecord.setTRAN_SOURCE  (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_SOURCE  ));
				serRecord.setCLIENT_TYPE  (rs.getString(RCS_TRAN_SERIAL_RECORD.CLIENT_TYPE  ));
				serRecord.setUSER_CODE    (rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE    ));
				serRecord.setUSER_NAME    (rs.getString(RCS_TRAN_SERIAL_RECORD.USER_NAME    ));
				serRecord.setBANK_CARD_NO (rs.getString(RCS_TRAN_SERIAL_RECORD.BANK_CARD_NO ));
				serRecord.setBANK_DEP_NO  (rs.getString(RCS_TRAN_SERIAL_RECORD.BANK_DEP_NO  ));
				serRecord.setBANK_CARD_LEN(rs.getString(RCS_TRAN_SERIAL_RECORD.BANK_CARD_LEN));
				serRecord.setBANK_SIGN_VAL(rs.getString(RCS_TRAN_SERIAL_RECORD.BANK_SIGN_VAL));
				serRecord.setCARD_FLG     (rs.getString(RCS_TRAN_SERIAL_RECORD.CARD_FLG     ));
				serRecord.setCOIN_FLG     (rs.getString(RCS_TRAN_SERIAL_RECORD.COIN_FLG     ));
				serRecord.setID_NUMBER    (rs.getString(RCS_TRAN_SERIAL_RECORD.ID_NUMBER    ));
				serRecord.setMOBILE_PHONE (rs.getString(RCS_TRAN_SERIAL_RECORD.MOBILE_PHONE ));
				serRecord.setTRAN_TYPE    (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_TYPE    ));
				serRecord.setTRAN_CLIENT  (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_CLIENT  ));
				serRecord.setTERM_ID      (rs.getString(RCS_TRAN_SERIAL_RECORD.TERM_ID      ));
				serRecord.setTRAN_AMT     (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_AMT     ));
				serRecord.setTRAN_STATUS  (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_STATUS  ));
				serRecord.setCOMP_CODE    (rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE    ));
				serRecord.setCOMP_NAME    (rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_NAME    ));
				serRecord.setREGDT_DAY    (rs.getString(RCS_TRAN_SERIAL_RECORD.REGDT_DAY    ));
				serRecord.setREGDT_TIME   (rs.getString(RCS_TRAN_SERIAL_RECORD.REGDT_TIME   ));
				serRecord.setTRAN_CODE    (rs.getString(RCS_TRAN_SERIAL_RECORD.TRAN_CODE    ));
				serRecord.setTREM         (rs.getString(RCS_TRAN_SERIAL_RECORD.TREM         ));
				recordList.add(serRecord);
				user.setRecordList(recordList);
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userMap;
	}
	
	//封装上个月累计金额、累计笔数、退款累计金额、退款累计笔数信息至列表
	public Map<String, User> getUserMapAttrPart(Statement stmt,String startDate,String endDate,String startUserID,String endUserID,Map<String, User> userMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY   +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY+"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY    +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.USER_CODE;
		
		ResultSet rs                   = null;
		String sUserID                 = null;
		User user                      = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
					user.setUSER_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE));
				}
				user.getAttrMap().put("ALL_AMOUNT_PRE_MONTH"    , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +"_SUM"));
				user.getAttrMap().put("REFUND_AMOUNT_PRE_MONTH" , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +"_SUM"));
				user.getAttrMap().put("ALL_COUNT_PRE_MONTH"     , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +"_SUM"));
				user.getAttrMap().put("REFUND_COUNT_PRE_MONTH"  , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +"_SUM"));
				
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userMap;
	}

	//封装上月交易平均值至列表
	public Map<String, User> getUserMapAttrPart2(Statement stmt,String startDate,String endDate,String startUserID,String endUserID,Map<String, User> userMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.USER_CODE;
		
		ResultSet rs   = null;
		String sUserID = null;
		User user      = null;
		String sAmount = null;
		String sCount  = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
					user.setUSER_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE));
				}
				
				sAmount = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM");
				sCount  = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM");
				
				user.getAttrMap().put("AVER_AMOUNT_MONTH", Tools.getAveValue(sAmount, sCount));
				
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userMap;
	}

	//封装前X个月累计金额、累计笔数至列表
	public Map<String, User> getUserMapAttrPart3(Statement stmt,String startDate,String endDate,String startUserID,String endUserID,Map<String, User> userMap,int iNum){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.USER_CODE+" BETWEEN '"+startUserID+"' AND '"+endUserID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.USER_CODE;
		
		ResultSet rs   = null;
		String sUserID = null;
		User user      = null;
		String sAmount = null;
		String sCount  = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE);
				user    = userMap.get(sUserID);
				if(null == user){
					user = new User();
					user.setUSER_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.USER_CODE));
				}
				
				sAmount = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM");
				sCount  = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM");
				
				user.getAttrMap().put("ALL_AMOUNT_PRE"+iNum+"_MONTH", sAmount);
				user.getAttrMap().put("ALL_COUNT_PRE"+iNum+"_MONTH" , sCount );
				
				userMap.put(sUserID, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userMap;
	}
}
