package com.tangdi.risk.pack;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import tangdi.engine.context.Log;

import com.tangdi.risk.base.RCS_EXCEPT_TRAN_RULE;
import com.tangdi.risk.base.RCS_TRAN_RULE_PARAM;
import com.tangdi.risk.rule.Rule;
import com.tangdi.risk.rule.RuleParam;

public class PackRule {
	//获取封装规则信息的Map
	public Map<String, Rule> getRuleMap(Statement stmt){
		Map<String, Rule> ruleMap = new HashMap<String, Rule>();
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_CODE       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_NAME       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_TYPE       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.IS_ONLINE       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.EXCP_TYPE       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.COM_TYPE_NO     +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_DES        +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_LEVEL      +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_LEVEL_ITEM +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_START_DATE +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_END_DATE   +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.CREATE_NAME     +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.CREATE_DATE     +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.CREATE_DATETIME +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.UPDATE_NAME     +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.UPDATE_DATE     +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.UPDATE_DATETIME +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.RULE_VER        +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.BELONGSCOMPANY  +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.EXEC_RATE       +",";
			sQrySql += RCS_EXCEPT_TRAN_RULE.IS_USE          +" ";
			sQrySql += " FROM "+RCS_EXCEPT_TRAN_RULE.TABLE_NAME;
		ResultSet rs   = null;
		String sRuleId = null;
		Rule rule      = null;
		try {
			Log.info("查询SQL：%s", sQrySql);
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sRuleId = rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_CODE );
				Log.info("查询SQL结果sRuleId=%s", sRuleId);
				rule    = ruleMap.get(sRuleId);
				if(null == rule){
					rule = new Rule();
				}
				
				rule.setRULE_CODE      (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_CODE      ));
				rule.setRULE_NAME      (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_NAME      ));
				rule.setRULE_TYPE      (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_TYPE      ));
				rule.setIS_ONLINE      (rs.getString(RCS_EXCEPT_TRAN_RULE.IS_ONLINE      ));
				rule.setEXCP_TYPE      (rs.getString(RCS_EXCEPT_TRAN_RULE.EXCP_TYPE      ));
				rule.setCOM_TYPE_NO    (rs.getString(RCS_EXCEPT_TRAN_RULE.COM_TYPE_NO    ));
				rule.setRULE_DES       (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_DES       ));
				rule.setRULE_LEVEL     (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_LEVEL     ));
				rule.setRULE_LEVEL_ITEM(rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_LEVEL_ITEM));
				rule.setRULE_START_DATE(rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_START_DATE));
				rule.setRULE_END_DATE  (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_END_DATE  ));
				rule.setCREATE_NAME    (rs.getString(RCS_EXCEPT_TRAN_RULE.CREATE_NAME    ));
				rule.setCREATE_DATE    (rs.getString(RCS_EXCEPT_TRAN_RULE.CREATE_DATE    ));
				rule.setCREATE_DATETIME(rs.getString(RCS_EXCEPT_TRAN_RULE.CREATE_DATETIME));
				rule.setUPDATE_NAME    (rs.getString(RCS_EXCEPT_TRAN_RULE.UPDATE_NAME    ));
				rule.setUPDATE_DATE    (rs.getString(RCS_EXCEPT_TRAN_RULE.UPDATE_DATE    ));
				rule.setUPDATE_DATETIME(rs.getString(RCS_EXCEPT_TRAN_RULE.UPDATE_DATETIME));
				rule.setRULE_VER       (rs.getString(RCS_EXCEPT_TRAN_RULE.RULE_VER       ));
				rule.setBELONGSCOMPANY (rs.getString(RCS_EXCEPT_TRAN_RULE.BELONGSCOMPANY ));
				rule.setEXEC_RATE      (rs.getString(RCS_EXCEPT_TRAN_RULE.EXEC_RATE      ));
				rule.setIS_USE         (rs.getString(RCS_EXCEPT_TRAN_RULE.IS_USE         ));

				ruleMap.put(sRuleId, rule);
			}
			Log.info("CCCC:"+ruleMap);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs){
					rs.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return ruleMap;
	}
	
	//将参数信息封装至规则信息中
	public Map<String, Rule> getRuleMapAddParam(Statement stmt,Map<String, Rule> ruleMap){
		String 
			sQrySql = "SELECT ";
			sQrySql += RCS_TRAN_RULE_PARAM.RULE_CODE         +",";
			sQrySql += RCS_TRAN_RULE_PARAM.PARAM_ID          +",";
			sQrySql += RCS_TRAN_RULE_PARAM.PARAM_NAME        +",";
			sQrySql += RCS_TRAN_RULE_PARAM.PARAM_VALUE       +",";
			sQrySql += RCS_TRAN_RULE_PARAM.PARAM_TYPE        +" ";
			sQrySql += " FROM "+RCS_TRAN_RULE_PARAM.TABLE_NAME;
		ResultSet rs   = null;
		String sRuleId = null;
		Rule rule      = null;
		RuleParam rp   = null;
		try {
			Log.info("查询SQL：%s", sQrySql);
			rs = stmt.executeQuery(sQrySql);
			while(rs.next()){
				sRuleId = rs.getString(RCS_TRAN_RULE_PARAM.RULE_CODE);
				rule    = ruleMap.get(sRuleId);
				if(null == rule){
					rule = new Rule();
					rule.setRULE_CODE(sRuleId);
				}
				
				rp = new RuleParam();
				rp.setRULE_CODE  (rs.getString(RCS_TRAN_RULE_PARAM.RULE_CODE  ));
				rp.setPARAM_ID   (rs.getString(RCS_TRAN_RULE_PARAM.PARAM_ID   ));
				rp.setPARAM_NAME (rs.getString(RCS_TRAN_RULE_PARAM.PARAM_NAME ));
				rp.setPARAM_VALUE(rs.getString(RCS_TRAN_RULE_PARAM.PARAM_VALUE));
				rp.setPARAM_TYPE (rs.getString(RCS_TRAN_RULE_PARAM.PARAM_TYPE ));
				
				//将名称改成大写
				if(null != rs.getString(RCS_TRAN_RULE_PARAM.PARAM_NAME )){
					rp.setPARAM_NAME (rs.getString(RCS_TRAN_RULE_PARAM.PARAM_NAME ).toUpperCase());
				}
				
				rule.getParamList().add(rp);
				
				ruleMap.put(sRuleId, rule);
				
				Log.info("规则%s参数%s=%s", sRuleId,rs.getString(RCS_TRAN_RULE_PARAM.PARAM_NAME ),rs.getString(RCS_TRAN_RULE_PARAM.PARAM_VALUE ));
			}
			Log.info("DDDD:"+ruleMap);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs){
					rs.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return ruleMap;
	}
}
