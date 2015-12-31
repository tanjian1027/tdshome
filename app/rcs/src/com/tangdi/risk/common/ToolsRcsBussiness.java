package com.tangdi.risk.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.tangdi.risk.rule.SerialRecord;

/**
 * 风控规则引擎业务逻辑计算
 * @author gengzhe
 *
 */
public class ToolsRcsBussiness {
	
	
	/**
	 * 平均值
	 */
	public static String getAvg(List<SerialRecord> recordList){
		
		Long sumAmt = 0L;
		for (SerialRecord serialRecord : recordList) {
			sumAmt += Long.valueOf(serialRecord.getTRAN_AMT());
		}
		String avg = Tools.getAveValue(sumAmt.toString(), recordList.size() + "");
		
		return avg;
	}
	
	/**
	 * 取流水信息表中第一笔交易金额
	 * @param recordList         流水
	 * @return
	 */
	public static Long getFirstAmt(List<SerialRecord> recordList){
		if(recordList.size()<=0 || Tools.isNull(recordList.get(0).getTRAN_AMT())){
			return 0L;
		}
		//按时间排序
		Comparator comp = new Mycomparator();
		Collections.sort(recordList,comp);
		
		Long firstAmt =  Long.valueOf(recordList.get(0).getTRAN_AMT());
		return firstAmt;
	}
	
	
	/**
	 * 取流水信息表中总金额
	 * @param recordList         流水
	 * @return
	 */
	public static String getAllAmt(List<SerialRecord> recordList){
		
		if(recordList.size()<=0 || Tools.isNull(recordList.get(0).getTRAN_AMT())){
			return "0";
		}
		Long sumAmt = 0L;
		for(SerialRecord serialRecord : recordList){
			sumAmt  +=  Long.valueOf(serialRecord.getTRAN_AMT());
		}
		return sumAmt+"";
	}
	
	
	/**
	 * 效验流水信息中是否存在大于configAmt的交易
	 * @param recordList 流水表list
	 * @param configAmt  金额上限(分)
	 * @return
	 */
	public static boolean existConfigAmt(List<SerialRecord> recordList,String configAmt){
		for(SerialRecord serialRecord :recordList){
			if(Long.valueOf(serialRecord.getTRAN_AMT()) >=Long.valueOf(configAmt)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 取同卡bin信息
	 * 把所给list流水信息中同卡bin放入key =发卡行机构代码+主帐号长度+发卡行标识取值的map中
	 * @param recordList 流水信息
	 * @return
	 */
	public static Map<String, List<SerialRecord>> getSameCardBinMap(List<SerialRecord> recordList) {
		// 存放同卡bin信息
		Map<String, List<SerialRecord>> sameCardBinMap = new HashMap<String, List<SerialRecord>>();

		if (recordList == null) {
			return sameCardBinMap;
		}
		
		for (SerialRecord serialRecord : recordList) {

			String BANK_DEP_NO = serialRecord.getBANK_DEP_NO();//
			String BANK_CARD_LEN = serialRecord.getBANK_CARD_LEN();
			String BANK_SIGN_VAL = serialRecord.getBANK_SIGN_VAL();
			
			if(Tools.isNull(BANK_DEP_NO) || Tools.isNull(BANK_CARD_LEN) ||Tools.isNull(BANK_SIGN_VAL)){
				continue;
			}
			
			// map的key
			String keyName = BANK_DEP_NO + BANK_CARD_LEN + BANK_SIGN_VAL;

			if (sameCardBinMap.containsKey(keyName)) {
				sameCardBinMap.get(keyName).add(serialRecord);
			} else {
				ArrayList<SerialRecord> mapCard_list = new ArrayList<SerialRecord>();
				mapCard_list.add(serialRecord);
				sameCardBinMap.put(keyName, mapCard_list);
			}
		}
		return sameCardBinMap;
	}
	
	/**
	 * 取同卡bin信息,可限制借贷标志
	 * 把所给list流水信息中同卡bin放入key =发卡行机构代码+主帐号长度+发卡行标识取值的map中
	 * @param recordList    流水信息
	 * @param configCardFlg 借贷标志 01 借记卡、02贷记卡
	 * @return
	 */
	public static Map<String, List<SerialRecord>> getSameCardBinMap_and_cardFlg(List<SerialRecord> recordList,String configCardFlg){
		
		// 存放同卡bin信息
		Map<String,List<SerialRecord>> sameCardBinMap = new HashMap<String,List<SerialRecord>>();
		
		if (recordList == null) {
			return sameCardBinMap;
		}
		
		for (SerialRecord serialRecord : recordList) {

			if (serialRecord.getCARD_FLG().endsWith(configCardFlg)) {// 借贷标志

				String BANK_DEP_NO = serialRecord.getBANK_DEP_NO();//
				String BANK_CARD_LEN = serialRecord.getBANK_CARD_LEN();
				String BANK_SIGN_VAL = serialRecord.getBANK_SIGN_VAL();
				
				if(Tools.isNull(BANK_DEP_NO) || Tools.isNull(BANK_CARD_LEN) ||Tools.isNull(BANK_SIGN_VAL)){
					continue;
				}
				
				// map的key
				String keyName = BANK_DEP_NO + "_" + BANK_CARD_LEN + "_"
						+ BANK_SIGN_VAL;

				if (sameCardBinMap.containsKey(keyName)) {
					sameCardBinMap.get(keyName).add(serialRecord);
				} else {
					ArrayList<SerialRecord> mapCard_list = new ArrayList<SerialRecord>();
					mapCard_list.add(serialRecord);
					sameCardBinMap.put(keyName, mapCard_list);
				}
			}
		}
		return sameCardBinMap;
	}
	
	
	/**
	 * 取同卡bin信息,可限制内外币标志
	 * 把所给list流水信息中同卡bin放入key =发卡行机构代码+主帐号长度+发卡行标识取值的map中
	 * @param recordList    流水信息
	 * @param configCoinFlg 0000 人民币，0001外币
	 * @return
	 */
	public static Map<String, List<SerialRecord>> getSameCardBinMap_and_coinFlg(List<SerialRecord> recordList,String configCoinFlg){
		
		// 存放同卡bin信息
		Map<String,List<SerialRecord>> sameCardBinMap = new HashMap<String,List<SerialRecord>>();
		
		if (recordList == null) {
			return sameCardBinMap;
		}
		
		for (SerialRecord serialRecord : recordList) {

			if (serialRecord.getCOIN_FLG().endsWith(configCoinFlg)) {// 借贷标志

				String BANK_DEP_NO = serialRecord.getBANK_DEP_NO();//
				String BANK_CARD_LEN = serialRecord.getBANK_CARD_LEN();
				String BANK_SIGN_VAL = serialRecord.getBANK_SIGN_VAL();
				
				if(Tools.isNull(BANK_DEP_NO) || Tools.isNull(BANK_CARD_LEN) ||Tools.isNull(BANK_SIGN_VAL)){
					continue;
				}
				// map的key
				String keyName = BANK_DEP_NO + "_" + BANK_CARD_LEN + "_"
						+ BANK_SIGN_VAL;

				if (sameCardBinMap.containsKey(keyName)) {
					sameCardBinMap.get(keyName).add(serialRecord);
				} else {
					ArrayList<SerialRecord> mapCard_list = new ArrayList<SerialRecord>();
					mapCard_list.add(serialRecord);
					sameCardBinMap.put(keyName, mapCard_list);
				}
			}
		}
		return sameCardBinMap;
	}

	/**
	 * 取同卡号信息
	 * @param recordList
	 * @return
	 */
	public static Map<String, List<SerialRecord>> getSameCardNoMap(List<SerialRecord> recordList) {
		// 存放同卡bin信息
		Map<String, List<SerialRecord>> sameCardNoMap = new HashMap<String, List<SerialRecord>>();

		if (recordList == null) {
			return sameCardNoMap;
		}
		
		for (SerialRecord serialRecord : recordList) {
			if (!Tools.isNull(serialRecord.getBANK_CARD_NO())) {
				// 流水表中卡号
				String cardNo_in_serialRecord = serialRecord.getBANK_CARD_NO();

				if (sameCardNoMap.containsKey(cardNo_in_serialRecord)) {
					sameCardNoMap.get(cardNo_in_serialRecord).add(serialRecord);
				} else {
					ArrayList<SerialRecord> mapCard_list = new ArrayList<SerialRecord>();
					mapCard_list.add(serialRecord);
					sameCardNoMap.put(cardNo_in_serialRecord, mapCard_list);
				}
			}
		}
		return sameCardNoMap;
	}
	

	/**
	 * 过滤锁定日期的流水信息
	 * @param recordList  流水表信息
	 * @param startDate   开始日期
	 * @param endDate     结束日期
	 * @return
	 */
	public static List<SerialRecord> getList_in_DateList(List<SerialRecord> recordList,String startDate,String endDate){
		
		List<SerialRecord> recordListNew = new ArrayList<SerialRecord>();
		
		if(recordList.size()==0 || Tools.isNull(startDate) || Tools.isNull(endDate)){
			return recordListNew;
		}
		
		for (SerialRecord serialRecord : recordList) {
			
			// 日期不能为空
			if (Tools.isNull(serialRecord.getREGDT_DAY())) {
				return null;
			} else {
				Long regdtDay = Long.valueOf(serialRecord.getREGDT_DAY());
				Long sDate = Long.valueOf(startDate);
				Long eDate = Long.valueOf(endDate);
				if (regdtDay >= sDate && regdtDay <= eDate) {// 借贷标志
					recordListNew.add(serialRecord);
				}
			}
		}
		return recordListNew;
	}
	
	/**
	 * 过滤锁定日期的特定类型的流水信息
	 * @param recordList  流水表信息
	 * @param startDate   开始日期
	 * @param endDate     结束日期
	 * @return
	 */
	//liulin 20150422 start 
	public static List<SerialRecord> getList_in_DateListByType(List<SerialRecord> recordList,String startDate,String endDate,String sTypes){
		
		List<SerialRecord> recordListNew = new ArrayList<SerialRecord>();
		
		if(recordList.size()==0 || Tools.isNull(startDate) || Tools.isNull(endDate)){
			return recordListNew;
		}
		
		for (SerialRecord serialRecord : recordList) {
			// 日期不能为空
			if (Tools.isNull(serialRecord.getREGDT_DAY())) {
				return null;
			} else {
				Long regdtDay = Long.valueOf(serialRecord.getREGDT_DAY());
				String sTranType =serialRecord.getTRAN_TYPE();
				Long sDate = Long.valueOf(startDate);
				Long eDate = Long.valueOf(endDate);
				if (regdtDay >= sDate && regdtDay <= eDate&&Tools.isContainsType(sTypes, sTranType)) {// 借贷标志
					recordListNew.add(serialRecord);
				}
			}
		}
		return recordListNew;
	}
	//liulin 20150422 end
	
	/**
	 * 过滤出流水信息中小于configAmt的交易
	 * @param recordList 流水表list
	 * @param configAmt  金额下限(分)
	 * @return
	 */
	public static List<SerialRecord> getConfigAmtDownList(List<SerialRecord> recordList,String configAmt){
		List<SerialRecord> recordListNew = new ArrayList<SerialRecord>();
		
		if(recordList.size()==0 ){
			return recordListNew;
		}
		
		for(SerialRecord serialRecord :recordList){
			if(Long.valueOf(serialRecord.getTRAN_AMT()) <= Long.valueOf(configAmt)){
				recordListNew.add(serialRecord);
			}
		}
		return recordListNew;
	}
	
	
	/**
	 * 过滤出流水信息中大于configAmt的交易
	 * @param recordList 流水表list
	 * @param configAmt  金额上限(分)
	 * @return
	 */
	public static List<SerialRecord> getConfigAmtUpList(List<SerialRecord> recordList,String configAmt){
		List<SerialRecord> recordListNew = new ArrayList<SerialRecord>();
		
		if(recordList.size()==0 ){
			return recordListNew;
		}
		
		for(SerialRecord serialRecord :recordList){
			if(Long.valueOf(serialRecord.getTRAN_AMT()) >= Long.valueOf(configAmt)){
				recordListNew.add(serialRecord);
			}
		}
		return recordListNew;
	}
	
	
	/**
	 * 过滤出流水信息中借、贷记信息
	 * @param recordList     流水表list
	 * @param configCardFlg  借、贷记标志 :00 借记卡、01贷记卡
	 * @return
	 */
	public static List<SerialRecord> getCardFlgList(List<SerialRecord> recordList,String configCardFlg){
		List<SerialRecord> recordListNew = new ArrayList<SerialRecord>();
		if(recordList == null ){
			return recordListNew;
		}
		if(!RcsDefault.CARD_FLG_01.equals(configCardFlg) && !RcsDefault.CARD_FLG_01.equals(configCardFlg)){
			return recordList;
		}
		for(SerialRecord serialRecord :recordList){
			if(configCardFlg.equals(serialRecord.getCARD_FLG()) ){
				recordListNew.add(serialRecord);
			}
		}
		return recordListNew;
	}
	
	
	/**
	 * 过滤出流水信息中内、外币信息
	 * @param recordList     流水表list
	 * @param configCoinFlg  币种:0000 人民币  ,0001 外币
	 * @return
	 */
	public static List<SerialRecord> getCoinFlgList(List<SerialRecord> recordList,String configCoinFlg){
		
		List<SerialRecord> recordListNew =  new ArrayList<SerialRecord>();
		
		if(recordList == null ){
			return recordListNew;
		}
		if(!RcsDefault.COIN_FLG_0000.equals(configCoinFlg) && !RcsDefault.COIN_FLG_0001.equals(configCoinFlg)){
			return recordList;
		}
		for(SerialRecord serialRecord :recordList){
			if(configCoinFlg.equals(serialRecord.getCOIN_FLG()) ){
				recordListNew.add(serialRecord);
			}
		}
		return recordListNew;
	}
	
	
	/**
	 * 过滤出流水信息中整数信息
	 * @param recordList     流水表list
	 * @return
	 */
	public static List<SerialRecord> getIntegerList(List<SerialRecord> recordList){
		
		List<SerialRecord> recordListNew =  new ArrayList<SerialRecord>();
		
		if(recordList == null ){
			return recordListNew;
		}
		for(SerialRecord serialRecord :recordList){
			if( Tools.isNumeric(serialRecord.getTRAN_AMT())){
				recordListNew.add(serialRecord);
			}
		}
		return recordListNew;
	}
	
	
	/**
	 * 判断该list中，连续交易相隔时间<60分钟，交易次数>=5
	 * @param recordList         流水
	 * @param configDiffTime_ss     秒
	 * @param configTranTimes    交易次数
	 * @return
	 */
	public static boolean getB(List<SerialRecord> recordList,String configDiffTime_ss,String configTranTimes){
		if(recordList.size() <= 0) return false;
		//按时间排序
		Comparator comp = new Mycomparator();
		Collections.sort(recordList,comp);
		
		int c =1;
		String  minTime= "";
		for(int i =0;i< recordList.size();i++){
			minTime= recordList.get(i).getREGDT_TIME();
			String laterTime= DateUtil.getTimeLater(minTime, configDiffTime_ss);//"getMax(minTime)";
			for(int j=i+1;j< recordList.size();j++){
				String recordTime = recordList.get(j).getREGDT_TIME();
				if ( Long.valueOf(recordTime) > Long.valueOf(minTime) &&  Long.valueOf(recordTime) < Long.valueOf(laterTime)) {
					++c;
					if (c >= Integer.valueOf(configTranTimes)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	
	

	
}
