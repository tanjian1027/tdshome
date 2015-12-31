package com.tangdi.risk.pack;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tangdi.risk.base.RCS_CARD_TRAN_INFO;
import com.tangdi.risk.base.RCS_TRAN_COMP_INFO;
import com.tangdi.risk.base.RCS_TRAN_SERIAL_RECORD;
import com.tangdi.risk.base.RCS_USER_COMP_TRAN_INFO;
import com.tangdi.risk.batch.QueryTable;
import com.tangdi.risk.common.Tools;
import com.tangdi.risk.rule.CardUnion;
import com.tangdi.risk.rule.Mer;
import com.tangdi.risk.rule.SerialRecord;
import com.tangdi.risk.rule.User;
import com.tangdi.risk.rule.UserMerUnion;

public class PackMer {
	private static final int DAYS = 30;
	
	//获取封装用户信息列表
	public List<Mer> getMerList(Statement stmt,String sDate,String startUserID,String endUserID){
		List<Mer> merList       = new ArrayList<Mer>();
		Map<String, Mer> merMap = getMerMap(stmt,sDate,startUserID,endUserID);
		//将Map转化为List
		for(String idTemp : merMap.keySet()){
			merList.add(merMap.get(idTemp));
		}
		return merList;
	}
	
	//获取封装用户信息列表
	public Map<String, Mer> getMerMap(Statement stmt,String sDate,String startMerID,String endMerID){
		Map<String, Mer> merMap = new HashMap<String, Mer>();
		
		//如果传入的用户ID为空则封装所有用户的信息
		if(Tools.isNull(startMerID)){
			List<String> listMerID = QueryTable.getListMerID(stmt,sDate,sDate);
			if(listMerID.size() == 0){
				return merMap;
			}
			startMerID = listMerID.get(0);
			endMerID   = listMerID.get(listMerID.size()-1);
		}
		
		//计算前DAYS天前的日期
		String startDate = Tools.getDayByVal(sDate, -DAYS);
		String endDate   = sDate;

		// 计算本月起止日
		String sMonthFirstDay = Tools.getMonthFirstDay(sDate);
		String sMonthLastDay = Tools.getMonthLastDay(sDate);
		
		// 计算前三个月起日(包括本月)
		String sPre3MonthFirstDay = Tools.getPreNMonthFirstDay(sDate, 3);

		// 计算前六个月起日(包括本月)
		String sPre6MonthFirstDay = Tools.getPreNMonthFirstDay(sDate, 6);
	
		//计算上个月起止日
		String sPreMonthFirstDay = Tools.getPreMonthFirstDay(sDate);
		String sPreMonthLastDay  = Tools.getPreMonthLastDay(sDate);
		
		//统计上周信息
		String sPreWeekFirstDat = Tools.getPreWeekFirstDay(sDate);
		String sPreWeekLastDat  = Tools.getPreWeekLastDay(sDate);
		
		//计算上上个月起止日
		String sPre2MonthFirstDay = Tools.getPreMonthFirstDay(sPreMonthFirstDay);
		String sPre2MonthLastDay  = Tools.getPreMonthLastDay(sPreMonthFirstDay);
		
		//封装基本信息至Map中
		merMap = getMerMapBase(stmt,startMerID,endMerID,merMap);
		
		//封装用户商户汇总信息至列表
		merMap = getMerMapUnion(stmt,startDate,endDate,startMerID,endMerID,merMap);
		
		//封装交易流水信息至列表
		merMap = getUserMapSerialRecord(stmt,startDate,endDate,startMerID,endMerID,merMap);
		
		//封装卡汇总信息至列表
		merMap = getCardUnion(stmt,startDate,endDate,startMerID,endMerID,merMap);
		
		//封装当日累计笔数信息至属性Map
		merMap = getMerMapAttrPart4(stmt,sDate,sDate,startMerID,endMerID,merMap);
		
		//封装上周累计金额、累计成功交易金额、累计退款金额、累计笔数、累计成功交易笔数、累计退款笔数信息至属性Map
		merMap = getMerMapAttrPart5(stmt,sPreWeekFirstDat,sPreWeekLastDat,startMerID,endMerID,merMap);
		
		//封装上个月部分统计信息至属性Map
		merMap = getMerMapAttrPart(stmt,sPreMonthFirstDay,sPreMonthLastDay,startMerID,endMerID,merMap);
		
		//封装上上个月部分统计信息至属性Map
		merMap = getMerMapAttrPart2(stmt,sPre2MonthFirstDay,sPre2MonthLastDay,startMerID,endMerID,merMap);
		
		//封装前三月累计金额、笔数信息至列表
		merMap = getMerMapAttrPart3(stmt,sPre3MonthFirstDay,sMonthLastDay,startMerID,endMerID,merMap,3);
				
		//封装前六月累计金额、笔数信息至列表
		merMap = getMerMapAttrPart3(stmt,sPre6MonthFirstDay,sMonthLastDay,startMerID,endMerID,merMap,6);
		
		return merMap;
	}
	
	//封装商户基本信息至Map列表
	public Map<String, Mer> getMerMapBase(Statement stmt,String startMerID,String endMerID,Map<String, Mer> merMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_CODE                 +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_NAME                 +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_SHOT_NAME            +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_MCC                  +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_TYPE                 +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_CREATE_REGISTER_DATE +",";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_REMOVE_REGISTER_DATE +",";
			sQrySql += RCS_TRAN_COMP_INFO.RISK_FLAG                 +",";
			sQrySql += RCS_TRAN_COMP_INFO.RISK_LEAVEL               +",";
			sQrySql += RCS_TRAN_COMP_INFO.REGISTER_DATE             +",";
			sQrySql += RCS_TRAN_COMP_INFO.REGISTER_DATETIME         +",";
			sQrySql += RCS_TRAN_COMP_INFO.IS_USE                    +",";
			sQrySql += RCS_TRAN_COMP_INFO.CAD_BANK                  +",";
			sQrySql += RCS_TRAN_COMP_INFO.PAY_CAD                   +",";
			sQrySql += RCS_TRAN_COMP_INFO.REMARK                    +",";
			sQrySql += RCS_TRAN_COMP_INFO.TRAN_COMP_CODE            +",";
			sQrySql += RCS_TRAN_COMP_INFO.UPDATE_DATE               +",";
			sQrySql += RCS_TRAN_COMP_INFO.UPDATE_DATETIME           +" ";
			sQrySql += " FROM "+RCS_TRAN_COMP_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_TRAN_COMP_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
		
		ResultSet rs   = null;
		String sMerID  = null;
		Mer mer        = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_TRAN_COMP_INFO.COMP_CODE);
				mer    = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
				}
				mer.setCOMP_CODE                (rs.getString(RCS_TRAN_COMP_INFO.COMP_CODE                ));
				mer.setCOMP_NAME                (rs.getString(RCS_TRAN_COMP_INFO.COMP_NAME                ));
				mer.setCOMP_SHOT_NAME           (rs.getString(RCS_TRAN_COMP_INFO.COMP_SHOT_NAME           ));
				mer.setCOMP_MCC                 (rs.getString(RCS_TRAN_COMP_INFO.COMP_MCC                 ));
				mer.setCOMP_TYPE                (rs.getString(RCS_TRAN_COMP_INFO.COMP_TYPE                ));
				mer.setCOMP_CREATE_REGISTER_DATE(rs.getString(RCS_TRAN_COMP_INFO.COMP_CREATE_REGISTER_DATE));
				mer.setCOMP_REMOVE_REGISTER_DATE(rs.getString(RCS_TRAN_COMP_INFO.COMP_REMOVE_REGISTER_DATE));
				mer.setRISK_FLAG                (rs.getString(RCS_TRAN_COMP_INFO.RISK_FLAG                ));
				mer.setRISK_LEAVEL              (rs.getString(RCS_TRAN_COMP_INFO.RISK_LEAVEL              ));
				mer.setREGISTER_DATE            (rs.getString(RCS_TRAN_COMP_INFO.REGISTER_DATE            ));
				mer.setREGISTER_DATETIME        (rs.getString(RCS_TRAN_COMP_INFO.REGISTER_DATETIME        ));
				mer.setIS_USE                   (rs.getString(RCS_TRAN_COMP_INFO.IS_USE                   ));
				mer.setCAD_BANK                 (rs.getString(RCS_TRAN_COMP_INFO.CAD_BANK                 ));
				mer.setPAY_CAD                  (rs.getString(RCS_TRAN_COMP_INFO.PAY_CAD                  ));
				mer.setREMARK                   (rs.getString(RCS_TRAN_COMP_INFO.REMARK                   ));
				mer.setTRAN_COMP_CODE           (rs.getString(RCS_TRAN_COMP_INFO.TRAN_COMP_CODE           ));
				mer.setUPDATE_DATE              (rs.getString(RCS_TRAN_COMP_INFO.UPDATE_DATE              ));
				mer.setUPDATE_DATETIME          (rs.getString(RCS_TRAN_COMP_INFO.UPDATE_DATETIME          ));
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装用户商户汇总信息至Map列表
	public Map<String, Mer> getMerMapUnion(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
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
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " ORDER BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE+","+RCS_USER_COMP_TRAN_INFO.STAT_DATE;
		
		ResultSet rs                   = null;
		String sUserID                 = null;
		Mer mer                        = null;
		List<UserMerUnion> userMerList = null;
		UserMerUnion umu               = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer     = merMap.get(sUserID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				userMerList = mer.getUserMerList();
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
				mer.setUserMerList(userMerList);
				merMap.put(sUserID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装交易流水信息至Map列表
	public Map<String, Mer> getUserMapSerialRecord(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
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
			sQrySql += RCS_TRAN_SERIAL_RECORD.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_TRAN_SERIAL_RECORD.REGDT_DAY+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " ORDER BY "+RCS_TRAN_SERIAL_RECORD.COMP_CODE+",to_date("+RCS_TRAN_SERIAL_RECORD.REGDT_TIME+",'yyyyMMddhh24miss')";
		
		ResultSet rs                  = null;
		String sMerID                 = null;
		Mer mer                       = null;
		List<SerialRecord> recordList = null;
		SerialRecord serRecord        = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				mer     = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE));
				}
				recordList = mer.getRecordList();
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
				mer.setRecordList(recordList);
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装卡汇总信息至Map列表
	public Map<String, Mer> getCardUnion(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_CARD_TRAN_INFO.BANK_CARD_NO   +",";
			sQrySql += RCS_CARD_TRAN_INFO.CARD_FLG       +",";
			sQrySql += RCS_CARD_TRAN_INFO.COIN_FLG       +",";
			sQrySql += RCS_CARD_TRAN_INFO.TRAN_SOURCE    +",";
			sQrySql += RCS_CARD_TRAN_INFO.COMP_CODE      +",";
			sQrySql += RCS_CARD_TRAN_INFO.STAT_DATE      +",";
			sQrySql += RCS_CARD_TRAN_INFO.ALL_COUNT_DAY  +",";
			sQrySql += RCS_CARD_TRAN_INFO.ALL_AMOUNT_DAY +",";
			sQrySql += RCS_CARD_TRAN_INFO.MIN_AMOUNT_DAY +",";
			sQrySql += RCS_CARD_TRAN_INFO.MAX_AMOUNT_DAY +" ";
			sQrySql += " FROM "+RCS_CARD_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_CARD_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_CARD_TRAN_INFO.STAT_DATE+",'yyyyMMdd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " ORDER BY "+RCS_CARD_TRAN_INFO.COMP_CODE+",to_date("+RCS_CARD_TRAN_INFO.STAT_DATE+",'yyyyMMdd')";
		
		ResultSet rs             = null;
		String sMerID            = null;
		Mer mer                  = null;
		List<CardUnion> cardList = null;
		CardUnion cardUnion      = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_CARD_TRAN_INFO.COMP_CODE);
				mer     = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_CARD_TRAN_INFO.COMP_CODE));
				}
				cardList  = mer.getCardList();
				cardUnion = new CardUnion();
				cardUnion.setBANK_CARD_NO  (rs.getString(RCS_CARD_TRAN_INFO.BANK_CARD_NO  ));
				cardUnion.setCARD_FLG      (rs.getString(RCS_CARD_TRAN_INFO.CARD_FLG      ));
				cardUnion.setCOIN_FLG      (rs.getString(RCS_CARD_TRAN_INFO.COIN_FLG      ));
				cardUnion.setTRAN_SOURCE   (rs.getString(RCS_CARD_TRAN_INFO.TRAN_SOURCE   ));
				cardUnion.setCOMP_CODE     (rs.getString(RCS_CARD_TRAN_INFO.COMP_CODE     ));
				cardUnion.setSTAT_DATE     (rs.getString(RCS_CARD_TRAN_INFO.STAT_DATE     ));
				cardUnion.setALL_COUNT_DAY (rs.getString(RCS_CARD_TRAN_INFO.ALL_COUNT_DAY ));
				cardUnion.setALL_AMOUNT_DAY(rs.getString(RCS_CARD_TRAN_INFO.ALL_AMOUNT_DAY));
				cardUnion.setMIN_AMOUNT_DAY(rs.getString(RCS_CARD_TRAN_INFO.MIN_AMOUNT_DAY));
				cardUnion.setMAX_AMOUNT_DAY(rs.getString(RCS_CARD_TRAN_INFO.MAX_AMOUNT_DAY));
				cardList.add(cardUnion);
				mer.setCardList(cardList);
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装上个月累计金额、累计笔数、退款累计金额、退款累计笔数信息至列表
	public Map<String, Mer> getMerMapAttrPart(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY   +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY+"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY    +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE;
		
		ResultSet rs  = null;
		String sMerID = null;
		Mer mer       = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer    = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				mer.getAttrMap().put("ALL_AMOUNT_PRE_MONTH"    , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +"_SUM"));
				mer.getAttrMap().put("REFUND_AMOUNT_PRE_MONTH" , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +"_SUM"));
				mer.getAttrMap().put("SUCCESS_AMOUNT_PRE_MONTH", rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +"_SUM"));
				mer.getAttrMap().put("ALL_COUNT_PRE_MONTH"     , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +"_SUM"));
				mer.getAttrMap().put("REFUND_COUNT_PRE_MONTH"  , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +"_SUM"));
				mer.getAttrMap().put("SUCCESS_COUNT_PRE_MONTH" , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +"_SUM"));
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装上上个月累计金额、累计笔数
	public Map<String, Mer> getMerMapAttrPart2(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY   +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY+"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY    +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE;
		
		ResultSet rs  = null;
		String sMerID = null;
		Mer mer       = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer    = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				mer.getAttrMap().put("ALL_AMOUNT_PRE2_MONTH" , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM"));
				mer.getAttrMap().put("ALL_COUNT_PRE2_MONTH"  , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM"));
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装前X个月累计金额、累计笔数至列表
	public Map<String, Mer> getMerMapAttrPart3(Statement stmt,String startDate,String endDate,String startCompID,String endCompID,Map<String, Mer> merMap,int iNum){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startCompID+"' AND '"+endCompID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE;
		
		ResultSet rs   = null;
		String sCompID = null;
		Mer mer      = null;
		String sAmount = null;
		String sCount  = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sCompID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer    = merMap.get(sCompID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				
				sAmount = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY +"_SUM");
				sCount  = rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM");
				
				mer.getAttrMap().put("ALL_AMOUNT_PRE"+iNum+"_MONTH", sAmount);
				mer.getAttrMap().put("ALL_COUNT_PRE"+iNum+"_MONTH" , sCount );
				
				merMap.put(sCompID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装当日累计笔数
	public Map<String, Mer> getMerMapAttrPart4(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY   +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY+"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY    +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE;
		
		ResultSet rs  = null;
		String sMerID = null;
		Mer mer       = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer    = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				mer.getAttrMap().put("ALL_COUNT_DAY"  , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY  +"_SUM"));
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	//封装封装上周累计金额、累计成功交易金额、累计退款金额、累计笔数、累计成功交易笔数、累计退款笔数信息
	public Map<String, Mer> getMerMapAttrPart5(Statement stmt,String startDate,String endDate,String startMerID,String endMerID,Map<String, Mer> merMap){
		
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE             +",";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +") as "+RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY   +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY+"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +") as "+RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY    +"_SUM,";
			sQrySql += "SUM("+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +") as "+RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY +"_SUM ";
			sQrySql += " FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sQrySql += " WHERE ";
			sQrySql += RCS_USER_COMP_TRAN_INFO.COMP_CODE+" BETWEEN '"+startMerID+"' AND '"+endMerID+"'";
			sQrySql += " AND to_date("+RCS_USER_COMP_TRAN_INFO.STAT_DATE+",'yyyy-MM-dd') BETWEEN to_date('"+startDate+"','yyyy-MM-dd') AND to_date('"+endDate+"','yyyy-MM-dd')";
			sQrySql += " GROUP BY "+RCS_USER_COMP_TRAN_INFO.COMP_CODE;
		
		ResultSet rs  = null;
		String sMerID = null;
		Mer mer       = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerID = rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE);
				mer    = merMap.get(sMerID);
				if(null == mer){
					mer = new Mer();
					mer.setCOMP_CODE(rs.getString(RCS_USER_COMP_TRAN_INFO.COMP_CODE));
				}
				mer.getAttrMap().put("ALL_AMOUNT_PRE_WEEK"    , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +"_SUM"));
				mer.getAttrMap().put("SUCCESS_AMOUNT_PRE_WEEK", rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY    +"_SUM"));
				mer.getAttrMap().put("REFUND_AMOUNT_PRE_WEEK" , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY +"_SUM"));
				mer.getAttrMap().put("ALL_COUNT_PRE_WEEK"     , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +"_SUM"));
				mer.getAttrMap().put("SUCCESS_COUNT_PRE_WEEK" , rs.getString(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY     +"_SUM"));
				mer.getAttrMap().put("REFUND_COUNT_PRE_WEEK"  , rs.getString(RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY  +"_SUM"));
				merMap.put(sMerID, mer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merMap;
	}
	
	
	public static void main(String[] args) {
		PackMer pm = new PackMer();
		pm.getCardUnion(null,"2012-12-21","2012-12-21", "1", "10", null);
	}
}
