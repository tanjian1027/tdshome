package com.tangdi.risk.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import tangdi.atc.Atc;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;

import com.tangdi.risk.base.Mer;
import com.tangdi.risk.base.RCS_CURRCONT;
import com.tangdi.risk.base.RCS_TRAN_COMP_INFO;
import com.tangdi.risk.base.RCS_TRAN_SERIAL_RECORD;
import com.tangdi.risk.base.RCS_TRAN_USER_INFO;
import com.tangdi.risk.base.RCS_USER_COMP_TRAN_INFO;
import com.tangdi.risk.base.User;
import com.tangdi.risk.common.DateUtil;
import com.tangdi.risk.common.RcsDefault;
import com.tangdi.risk.common.Tools;
import com.tangdi.risk.rule.ExceptTran;
import com.tangdi.risk.rule.ExceptTranDetail;
import com.util.RcsUtils;

public class QueryTable {
	public static Connection conn = null;
	static {
		
	}
	
	private static final int RANGE     = 1000;     //每次处理的用户数量
	private static final String COUNT  = "COUNT";  //累计笔数
	private static final String AMOUNT = "AMOUNT"; //累计金额
	
	//获取当日累计金额、笔数、最大交易金额
	public static Map<String, User> getTranAmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String 
			sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT+", MAX("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT+"_MAX",
			sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'",
			sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		String sAmountMAX       = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId    = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId     = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount     = rs.getString(COUNT);
				sAmount    = rs.getString(AMOUNT);
				sAmountMAX = rs.getString(AMOUNT+"_MAX");
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY , sCount);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY, sAmount);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.MAX_AMOUNT_DAY, sAmountMAX);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "");
		}
		return mapUser;
	}
	
	//获取当日充值累计金额、笔数
	public static Map<String, User> getTranType1AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '1' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.RECHARGE_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType1AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日消费累计金额、笔数
	public static Map<String, User> getTranType2AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '2' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.SALE_COUNT_DAY , sCount);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.SALE_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType2AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日预授权完成累计金额、笔数
	public static Map<String, User> getTranType4AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '4' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_COUNT_DAY , sCount);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日转账累计金额、笔数
	public static Map<String, User> getTranType6AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '6' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.TRANSFER_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日提现累计金额、笔数
	public static Map<String, User> getTranType7AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '7' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.CASH_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日退款累计金额、笔数
	public static Map<String, User> getTranType8AmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_TYPE+" = '8' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY , sCount);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY, sAmount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日金额小于X元累计金额、笔数
	public static Map<String, User> getTranAmtLessAmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId,double dAmt){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+" <= "+dAmt*100;
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.LESS_COUNT_DAY , sCount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日贷记卡累计金额、笔数
	public static Map<String, User> getTranCreditAmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND "+RCS_TRAN_SERIAL_RECORD.CARD_FLG+" = '"+RcsDefault.CARD_FLG_01+"' ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.CREDIT_COUNT_DAY , sCount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获取当日金额整数累计金额、笔数
	public static Map<String, User> getTranIntAmountAndCount(Map<String, User> mapUser,Statement stmt,String sDate,String startUserId,String endUserId,int iAmt){
		String sSelectItem = " COUNT(*) as "+COUNT+", SUM("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+") as "+AMOUNT;
		String sCondition  = RCS_TRAN_SERIAL_RECORD.USER_CODE+" >= '"+startUserId+"' AND "+RCS_TRAN_SERIAL_RECORD.USER_CODE+" < '"+endUserId+"'";
			   sCondition  += " AND MOD("+RCS_TRAN_SERIAL_RECORD.TRAN_AMT+","+iAmt+") = 0 ";
		String sQrySql     = RCS_TRAN_SERIAL_RECORD.getAllUserCompInfo(sDate, sDate, sSelectItem, sCondition);
		ResultSet rs 			= null;
		User user    			= null;
		Mer mer		 			= null;
		Map<String, Mer> mapMer = null;
		String sUserId          = null;
		String sMerId           = null;
		String sCount           = null;
		String sAmount          = null;
		try {
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sUserId = rs.getString(RCS_TRAN_SERIAL_RECORD.USER_CODE);
				sMerId  = rs.getString(RCS_TRAN_SERIAL_RECORD.COMP_CODE);
				sCount  = rs.getString(COUNT);
				sAmount = rs.getString(AMOUNT);
				user = mapUser.get(sUserId);
				if(null == user){
					user = new User();
					user.setId(sUserId);
					mapUser.put(sUserId, user);
				}
				mapMer = user.getMerMap();
				mer    = mapMer.get(sMerId);
				if(null == mer){
					mer = new Mer();
				}
				mer.setId(sMerId);
				mer.getBaseInfo().put(RCS_USER_COMP_TRAN_INFO.INT_COUNT_DAY , sCount);
				
				user.getMerMap().put(sMerId,mer);
				mapUser.put(sUserId, user);
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getTranType4AmountAndCount:"+sUserId);
		}
		return mapUser;
	}
	
	//获得所有用户ID
	public static List<String> getListUserID(Statement stmt,String startDate,String endDate){
		List<String> listRes = new ArrayList<String>();
		//1.查找所有用户ID
		String sQrySql = RCS_TRAN_USER_INFO.getAllUserIDByDate();
		ResultSet rs   = null;
		try {
			long lCount      = 0;
			String sUserCode = null;
			rs   = stmt.executeQuery(sQrySql);
			//添加默认用户ID:"-1"，在遍历用户时使用
			listRes.add(RcsDefault.USER_CODE_DEFAULT);
			while(rs.next()){
				sUserCode = rs.getString(RCS_TRAN_USER_INFO.USER_CODE);
				if(null == sUserCode){
					continue;
				}
				if(lCount%RANGE == 0){
					listRes.add(sUserCode);
				}
				lCount++;
			}
			if(lCount%RANGE != 0 && listRes.size()>0){
				listRes.add(sUserCode);
			}
			if(listRes.size()>0){
				String sLast = listRes.get(listRes.size()-1);
				sLast += "0";
				if(listRes.size()==1){
					listRes.add(sLast);
				}else{
					listRes.set(listRes.size()-1, sLast);
				}
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getListUserID:"+startDate);
		}
		return listRes;
	}
	
	//获得所有商户ID
	public static List<String> getListMerID(Statement stmt,String startDate,String endDate){
		List<String> listRes = new ArrayList<String>();
		//1.查找所有商户ID
		String sQrySql = RCS_TRAN_COMP_INFO.getAllMerIDByDate();
		ResultSet rs   = null;
		try {
			long lCount     = 0;
			String sMerCode = null;
			rs   = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sMerCode = rs.getString(RCS_TRAN_COMP_INFO.COMP_CODE);
				if(null == sMerCode){
					continue;
				}
				if(lCount%RANGE == 0){
					listRes.add(sMerCode);
				}
				lCount++;
			}
			if(lCount%RANGE != 0 && listRes.size()>0){
				listRes.add(sMerCode);
			}
			
			if(listRes.size()>0){
				String sLast = listRes.get(listRes.size()-1);
				sLast += "0";
				if(listRes.size()==1){
					listRes.add(sLast);
				}else{
					listRes.set(listRes.size()-1, sLast);
				}
				
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getListMerID:"+startDate);
		}
		return listRes;
	}
	
	//执行用户商户信息查询
	public static void exec(Statement stmt,String startDate,String endDate){
		try {
			List<String> listUserID = getListUserID(stmt,startDate,endDate);
			String startID    = null;
			String endID      = null;
			//清空当日对应的信息
			delDataRCS_USER_COMP_TRAN_INFOByDate(stmt,startDate);
			for(int i=0;i<listUserID.size();i++){
				startID = listUserID.get(i);
				if((i+1)==listUserID.size()){
					endID = startID;
				}else{
					endID = listUserID.get(i+1);
				}
				Map<String, User> mapUser = new HashMap<String, User>();
				mapUser = getTranAmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType1AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType2AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType4AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType6AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType7AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranType8AmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranAmtLessAmountAndCount(mapUser,stmt,startDate,startID,endID,10);
				mapUser = getTranCreditAmountAndCount(mapUser,stmt,startDate,startID,endID);
				mapUser = getTranIntAmountAndCount(mapUser,stmt,startDate,startID,endID,1000);
				
				//保存数据至数据库
				saveDataTo_RCS_USER_COMP_TRAN_INFO(mapUser,stmt,startDate);
			}
			
			/**  汇总卡汇总信息 start  */
			
			Log.info("汇总卡汇总信息 start  ");
			
			String nowDate = DateUtil.formmat10to8(startDate);
			Log.info("卡汇总信息统计日期为："+nowDate);
			
			//首先删除汇总表信息数据
			Atc.ExecSql(null,"delete from RCS_CARD_TRAN_INFO where STAT_DATE ="+nowDate);
			//插入动作sql
			String insertSql = " insert into RCS_CARD_TRAN_INFO(BANK_CARD_NO, CARD_FLG,COIN_FLG,TRAN_SOURCE, COMP_CODE, STAT_DATE, ALL_COUNT_DAY, ALL_AMOUNT_DAY,MIN_AMOUNT_DAY,MAX_AMOUNT_DAY)                                                                                                                                                         ";
				   insertSql +=" select distinct t. BANK_CARD_NO,t.CARD_FLG,t.COIN_FLG,t.TRAN_SOURCE,t.COMP_CODE,t.regdt_day ,m.ALL_COUNT_DAY,n.ALL_AMOUNT_DAY,n.MIN_AMOUNT_DAY ,n.MAX_AMOUNT_DAY   from RCS_TRAN_SERIAL_RECORD t                                                                                                                          ";
				   insertSql +=" inner  join                                                                                                                                                                                                                                                                                                    ";
				   insertSql +=" ( select  BANK_CARD_NO,COMP_CODE,CARD_FLG,COIN_FLG,TRAN_SOURCE, count(a.BANK_CARD_NO) as ALL_COUNT_DAY  from RCS_TRAN_SERIAL_RECORD a   where  a.REGDT_DAY= '"+nowDate+"'  and  a.tran_source='01'  group by  BANK_CARD_NO,COMP_CODE,CARD_FLG,COIN_FLG,TRAN_SOURCE ) m                                                            ";
				   insertSql +=" on t. BANK_CARD_NO=m.BANK_CARD_NO and t.COMP_CODE=m.COMP_CODE  and t.TRAN_SOURCE=m.TRAN_SOURCE  and t.CARD_FLG = m.CARD_FLG                                                                                                                                                                                    ";
				   insertSql +=" inner join                                                                                                                                                                                                                                                                                                     ";
				   insertSql +=" ( select  BANK_CARD_NO,COMP_CODE,CARD_FLG,TRAN_SOURCE,sum(a.TRAN_AMT) as ALL_AMOUNT_DAY,min(a.tran_amt) as MIN_AMOUNT_DAY ,max(a.tran_amt) as MAX_AMOUNT_DAY  from RCS_TRAN_SERIAL_RECORD a   where  a.REGDT_DAY= '"+nowDate+"' and a.tran_source='01'   group by  BANK_CARD_NO,COMP_CODE,CARD_FLG,TRAN_SOURCE ) n";
				   insertSql +=" on t. BANK_CARD_NO=n.BANK_CARD_NO and  t.COMP_CODE=n.COMP_CODE  and t.TRAN_SOURCE = n.TRAN_SOURCE and t.CARD_FLG = n.CARD_FLG                                                                                                                                                                                  ";
				   insertSql +=" where  t.REGDT_DAY= '"+nowDate+"'                                                                                                                                                                                                                                                                              ";
				 //insertSql +=" where  t.REGDT_DAY= '"+nowDate+"'  and t.tran_source='"+RcsDefault.TRAN_SOURCE_01+"'                                                                                                                                                                                                                           ";
				   
	        Log.info("跑批程序，数据准备卡汇总信息打印sql = " + insertSql);
	        
	        int countResult = Atc.ExecSql(null, insertSql);
			if(countResult == -1) {
				Log.info("跑批出错，数据准备卡汇总信息出错~!");
			} 
			Log.info("汇总卡汇总信息 end  ");
			
			/** 汇总卡汇总信息 end */
			
		} catch (Exception e) {
			Log.error(e, "QueryTable.exec:"+startDate);
		}
	}
	
	//清除对应日期的记录
	public static void delDataRCS_USER_COMP_TRAN_INFOByDate(Statement stmt,String sDate){
		//先删除当天的记录
		String 
			sDelSql = "DELETE FROM "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME;
			sDelSql += " WHERE "+RCS_USER_COMP_TRAN_INFO.STAT_DATE+" = '"+sDate+"'";
		try {
			stmt.execute(sDelSql);
		} catch (Exception e) {
			Log.error(e, "QueryTable.delDataRCS_USER_COMP_TRAN_INFOByDate:"+sDate);
		}
	}
	
	//将信息保存至数据库
	public static void saveDataTo_RCS_USER_COMP_TRAN_INFO(Map<String, User> mapUser,Statement stmt,String sDate){
		try {
			//将mapUser信息保存至RCS_USER_COMP_TRAN_INFO
			String 
				sInsertSql            = "INSERT INTO "+RCS_USER_COMP_TRAN_INFO.TABLE_NAME+"(";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.USER_CODE            +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.COMP_CODE            +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.STAT_DATE            +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY       +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY    +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.SALE_AMOUNT_DAY      +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.RECHARGE_AMOUNT_DAY  +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.TRANSFER_AMOUNT_DAY  +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.CASH_AMOUNT_DAY      +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_AMOUNT_DAY+",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY        +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY     +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.SALE_COUNT_DAY       +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_COUNT_DAY +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.LESS_COUNT_DAY       +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.CREDIT_COUNT_DAY     +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.INT_COUNT_DAY        +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.AVER_AMOUNT_DAY      +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.TRAN_SOURCE          +",";
				sInsertSql            += RCS_USER_COMP_TRAN_INFO.MAX_AMOUNT_DAY       +" ";
				sInsertSql            += ")";
				sInsertSql            += " VALUES";
				
			Set<String> userSet       = mapUser.keySet();
			Set<String> merSet        = null;
			Iterator<String> userIdIt = userSet.iterator();
			Iterator<String> merIdIt  = null;
			String sUserID			  = null;
			String sMerID             = null;
			String sTempSql           = null;
			User user				  = null;
			Mer mer					  = null;
			Map<String, Mer> merMap   = null;
			int iCount                = 0;
			while(userIdIt.hasNext()){
				sUserID  = userIdIt.next();
				user     = mapUser.get(sUserID);
				merMap   = user.getMerMap();
				merSet   = merMap.keySet();
				merIdIt  = merSet.iterator();
				while(merIdIt.hasNext()){
					sTempSql = sInsertSql;
					sMerID   = merIdIt.next();
					mer      = merMap.get(sMerID);
					
					sTempSql += "(";
					sTempSql += "'"+sUserID+"',";
					sTempSql += "'"+sMerID+"',";
					sTempSql += "'"+sDate+"',";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY       )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.REFUND_AMOUNT_DAY    )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.SALE_AMOUNT_DAY      )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.RECHARGE_AMOUNT_DAY  )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.TRANSFER_AMOUNT_DAY  )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.CASH_AMOUNT_DAY      )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_AMOUNT_DAY)+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY        )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.REFUND_COUNT_DAY     )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.SALE_COUNT_DAY       )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.PREDEPOSIT_COUNT_DAY )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.LESS_COUNT_DAY       )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.CREDIT_COUNT_DAY     )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.INT_COUNT_DAY        )+",";
					sTempSql += Tools.getAveValue(mer.getBaseInfo().get(RCS_USER_COMP_TRAN_INFO.ALL_AMOUNT_DAY),mer.getBaseInfo().get(RCS_USER_COMP_TRAN_INFO.ALL_COUNT_DAY))+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.TRAN_SOURCE          )+",";
					sTempSql += getMapValue(mer.getBaseInfo(),RCS_USER_COMP_TRAN_INFO.MAX_AMOUNT_DAY       )+" ";
					sTempSql += ")";
					
					stmt.addBatch(sTempSql);
					iCount++;
				}
				if(iCount>=RANGE){
					stmt.executeBatch();
					iCount = 0;
				}
			}
			if(iCount!=0){
				stmt.executeBatch();
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.saveDataTo_RCS_USER_COMP_TRAN_INFO:"+sDate);
		}
	}
	
	//通过Map获得插入数据库的文本数据
	private static String getMapValue(Map<String, String> map,String key){
		String sRes = "NULL";
		sRes = map.get(key);
		if(null != sRes){
			sRes = "'"+sRes+"'";
		}
		return sRes;
	}
	
	/**
	 * 插入异常信息
	 * 异常主表    : RCS_EXCEPT_TRAN_INFO
	 * 异常明细表：RCS_EXCEPT_TRAN_DETAIL_INFO
	 * 
	 * @param exceptTranList 入参：异常对象(List)
	 */
	public static void insertExcept(List<ExceptTran> exceptTranList){
		//当天日期
		String date =  DateUtil.getTodayFormat(null);
		
		//删除当天记录
		Atc.ExecSql(null, "delete from RCS_EXCEPT_TRAN_INFO where id like '%"+date+"_%'");
		Atc.ExecSql(null, "delete from RCS_EXCEPT_TRAN_DETAIL_INFO where id like '%"+date+"_%'");
		//得到初始值
		String L = Tools.getId(null);
		for(ExceptTran exceptTran :exceptTranList){
			L = Tools.getId(L);
			String  id = date  + L;//Id 为当天日期+"条数id"
			
			String sqlcmd ="insert into RCS_EXCEPT_TRAN_INFO values('" +
						id +"','"+                              //交易ID	ID
						exceptTran.getENTITY_TYPE()+"','"+      //主体类型	ENTITY_TYPE
						exceptTran.getTRAN_SOURCE()+"','"+      //交易来源	TRAN_SOURCE
						exceptTran.getUSER_CODE()+"','"+        //用户编号	USER_CODE
						exceptTran.getCOMP_CODE()+"','"+        //商户编号	COMP_CODE
						exceptTran.getBANK_CARD_NO()+"','"+     //卡号	BANK_CARD_NO
						exceptTran.getBANK_DEP_NO()+"','"+      //发卡行机构代码	BANK_DEP_NO
						exceptTran.getBANK_SIGN_VAL()+"','"+    //发卡行标识取值	BANK_SIGN_VAL
						exceptTran.getREGDT_DAY()+"','"+        //登录日期	REGDT_DAY
						exceptTran.getWARN_TYPE()+"','"+        //异常交易类型	WARN_TYPE
						exceptTran.getRULE_CODE()+"','"+        //违反规则编号	RULE_CODE
						exceptTran.getEXCEPT_TRAN_FLAG()+"','"+ //交易状态	EXCEPT_TRAN_FLAG
						exceptTran.getUPDATE_NAME()+"','"+      //维护人	UPDATE_NAME
						exceptTran.getUPDATE_DATE()+"','"+      //维护日期	UPDATE_DATE
						exceptTran.getUPDATE_DATETIME()+"','"+  //维护时间	UPDATE_DATETIME
						exceptTran.getEXCEPT_TRAN_ADD()+"')";   //交易备注	EXCEPT_TRAN_ADD
			
			//插入主表
			int resultE = Atc.ExecSql(null, sqlcmd);
			if(resultE == -1 ){
				Log.info("插入异常主表失败");
				return;
			}
			
			for (ExceptTranDetail exceptTranDetail : exceptTran.getDetailList()) {
				String sqlcmdDetal = "insert into RCS_EXCEPT_TRAN_DETAIL_INFO values('"+
						id + "','" +                           // 交易ID ID
						exceptTranDetail.getTRAN_CODE() + "')"; // 交易流水号

				//插入明细表
				int resultD = Atc.ExecSql(null, sqlcmdDetal);
				if(resultD == -1){
					Log.info("插入异常明细表失败");
					return;
				}
			}
		}
	}
	
	/**
	 * 插入X名单信息
	 * 
	 * @param exceptTranList 入参：异常对象(List)
	 */
	public static void insertXName(String sDate){
		//查找出当日所有属于白名单中的异常交易用户 将其放入灰名单
		String sSql  = "SELECT except_tran.USER_CODE UCODE,except_tran.ENTITY_TYPE ";
			   sSql += "FROM RCS_EXCEPT_TRAN_INFO except_tran,RCS_TRAN_USER_INFO userInfo ";
			   sSql += "WHERE except_tran.USER_CODE = userInfo.USER_CODE ";
			   sSql += "AND userInfo.RISK_FLAG = '0' ";
			   sSql += "AND except_tran.ENTITY_TYPE = '"+RcsDefault.ENTITY_TYPE_0+"' ";
			   sSql += "AND except_tran.REGDT_DAY LIKE '"+sDate+"' ";
			   sSql += "AND except_tran.EXCEPT_TRAN_FLAG = '"+RcsDefault.EXCEPT_TRAN_FLAG_0+"' ";
			   sSql += "AND except_tran.USER_CODE != '-1' ";
			   sSql += "GROUP BY except_tran.USER_CODE,except_tran.ENTITY_TYPE";
			   sSql += " UNION ";
			   sSql += "SELECT except_tran.COMP_CODE UCODE,except_tran.ENTITY_TYPE ";
			   sSql += "FROM RCS_EXCEPT_TRAN_INFO except_tran,RCS_TRAN_COMP_INFO userInfo ";
			   sSql += "WHERE except_tran.COMP_CODE = userInfo.COMP_CODE ";
			   sSql += "AND userInfo.RISK_FLAG = '0' ";
			   sSql += "AND except_tran.ENTITY_TYPE = '"+RcsDefault.ENTITY_TYPE_0+"' ";
			   sSql += "AND except_tran.REGDT_DAY LIKE '"+sDate+"' ";
			   sSql += "AND except_tran.EXCEPT_TRAN_FLAG = '"+RcsDefault.EXCEPT_TRAN_FLAG_0+"' ";
			   sSql += "GROUP BY except_tran.COMP_CODE,except_tran.ENTITY_TYPE";
	    
		int iRes = Atc.PagedQuery("1", "100000", "REC", sSql);
		if(0 == iRes){
			List<Element> tempList = Msg.childs("REC");
			String sUCode          = null;
			String sEntifyType     = null;
			for(Element el:tempList){
				sUCode      = RcsUtils.getElementTextByKey(el, "UCODE"      );
				sEntifyType = RcsUtils.getElementTextByKey(el, "ENTITY_TYPE");
				//设置用户/商户X名单状态
				RcsUtils.setUserRisk(sEntifyType, sUCode, "1", null);
				//记录X名单历史
				RcsUtils.addXList(sEntifyType,sUCode,"1","1");
			}
		}
	}
	
	//获得所有IP
	public static List<String> getListIp(Statement stmt,String startDate,String endDate){
		List<String> listRes = new ArrayList<String>();
		//1.查找所有日期范围内的IP
		String sQrySql = RCS_CURRCONT.getAllIpByDate(startDate,endDate);
		ResultSet rs   = null;
		try {
			long lCount = 0;
			String sIp  = null;
			Log.info("****查询数据SQL：%s", sQrySql);
			rs   = stmt.executeQuery(sQrySql);
			//添加默认IP:"-1"，在遍历IP时使用
			listRes.add(RcsDefault.IP_DEFAULT);
			while(rs.next()){
				sIp = rs.getString(RCS_CURRCONT.REQIP);
				if(null == sIp){
					continue;
				}
				if(lCount%RANGE == 0){
					listRes.add(sIp);
				}
				lCount++;
			}
			if(lCount%RANGE != 0 && listRes.size()>0){
				listRes.add(sIp);
			}
			if(listRes.size()>0){
				String sLast = listRes.get(listRes.size()-1);
				sLast += "";
				if(listRes.size()==1){
					listRes.add(sLast);
				}else{
					listRes.set(listRes.size()-1, sLast);
				}
			}
		} catch (Exception e) {
			Log.error(e, "QueryTable.getListIp:"+startDate);
		}
		return listRes;
	}
}
