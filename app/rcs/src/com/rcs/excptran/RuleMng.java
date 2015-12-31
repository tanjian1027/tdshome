package com.rcs.excptran;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import tangdi.annotations.Code;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.log.ILog;
import tangdi.web.TdWebConstants;
import org.apache.commons.lang.StringUtils;
import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.RcsUtils;

/**
 * 风控线上异常交易规则管理
 * 棠棣科技
 * @author 刘军
 */
@tangdi.engine.DB
public class RuleMng {
	/**
	 * 规则编号
	 */
	@Data
	String RULE_CODE;
	
	/**
	 * 规则名称
	 */
	@Data
	String RULE_NAME;
	
	/**
	 * 规则版本号
	 */
	@Data
	String RULE_VER;
	
	/**
	 * 异常规则类型
	 */
	@Data
	String RULE_TYPE;
	/**
	 * 是否线上
	 */
	@Data
	String IS_ONLINE;
	/**
	 * 异常类型
	 */
	@Data
	String EXCP_TYPE;
	/**
	 * 一般预警项
	 */
	@Data
	String RULE_LEVEL_ITEM;
	
	/**
	 * 异常规则描述
	 */
	@Data
	String RULE_DES;
	
	/**
	 * 生效标志
	 */
	@Data
	String IS_USE;
	
	/**
	 * 阀值参数个数
	 */
	@Data
	String PARAMNUM;

	/**
	 * 参数值
	 */
	@Data
	String RULE_PARAM;
	
	/**
	 * 商户业务类型
	 */
	@Data
	String COM_TYPE_NO;
	
	/**
	 * 规则维护人
	 */
	@Data
	String UPDATE_NAME;
	@Data
	String CREATE_NAME;
	
	/**
	 * 规则维护日期
	 */
	@Data
	String UPDATE_DATE;
	@Data
	String CREATE_DATE;
	
	/**
	 * 规则维护时间
	 */
	@Data
	String UPDATE_DATETIME;
	@Data
	String CREATE_DATETIME;
	
	/**
	 * 当前页数
	 */
	@Data
	String pageNum;
	
	/**
	 * 每页的记录数
	 */
	@Data
	String NUMPERPAGE;
	
	/**
	 * 页面右下角显示的页数
	 */
	@Data
	String PAGENUMSHOWN;
	
	/**
	 * 规则编号
	 */
	@Data
	String RULE_CODE1;
	
	/**
	 * 规则描述
	 */
	@Data
	String RULE_DES1;
	
	/**
	 * 执行频率
	 */
	@Data
	String EXEC_RATE;
	
	/**
	 * 阀值参数个数
	 */
	@Data
	String paramNo;
	
	/**
	 * 配置参数值-是否显示商户业务类型
	 */
	@Data
	String IS_SHOW_COM_TYPE_NO;
	
	/**
	 * 配置参数值-是否显示等级规则
	 */
	@Data
	String IS_SHOW_RULE_LEVEL;
	
	/**
	 * 配置参数值-是否显示一般预警项
	 */
	@Data
	String IS_SHOW_RULE_LEVEL_ITEM;

	@Code("540100")
	public void testule(ILog logger) {
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/excptran/ruleTest.jsp");
	}
	/**
	 * 规则测试
	 * @param logger
	 */
	@Code("540100")
	public void RuleTest(ILog logger) {
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/excptran/ruleTest.jsp");
	}
	
	/**
	 *规则查询
	 * @param logger
	 * @throws Exception
	 */
	@Code("540101")
	public void findUsrRule(ILog logger) {
		Log.info("540101用户异常交易规则查询");
            
        StringBuffer sql = new StringBuffer(
        		"select * from RCS_EXCEPT_TRAN_RULE  where 1=1 ");//线上0线下1、用户0商户1
        //规则编号
		if (StringUtils.isNotEmpty(RULE_CODE)) {
			sql.append(" and RULE_CODE like '%" + RULE_CODE + "%'");
		}
		//规则名称
		if (StringUtils.isNotEmpty(RULE_NAME)) {
			sql.append(" and RULE_NAME like '%" + RULE_NAME + "%'");
		}
		//生效标志: 
		if (StringUtils.isNotEmpty(IS_USE)) {
			sql.append(" and IS_USE='" + IS_USE + "'");
		}
		//规则类型
		if (StringUtils.isNotEmpty(RULE_TYPE)) {
			sql.append(" and RULE_TYPE='" + RULE_TYPE + "'");
		}
		//是否线上
		if (StringUtils.isNotEmpty(IS_ONLINE)) {
			sql.append(" and IS_ONLINE='" + IS_ONLINE + "'");
		}
		sql.append(" order by IS_USE desc,to_number(substr(RULE_CODE,5)) ");
		
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		
		NUMPERPAGE = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql.toString());  		
		Log.info("sql=%s", sql);
		if(result == 0){
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			
			int TOLCNT = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			logger.info("开始页数=%s", (TOLCNT / perPage) * perPage + 1);

			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else {
			Etf.setChildValue("MsgTyp", "E");
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败");
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/excptran/findRule.jsp");
	}
	
	/**
	 * 添加规则
	 * @param logger
	 */
	@Code("540120")
	public void addRule_to_html(ILog logger) {
		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptran/addRule.jsp");
	}
	/**
	 * 添加规则到规则引擎
	 * @param logger
	 */
	@Code("540121")
	public void addRule(ILog logger) {
		Msg.dump();
		
		
		Log.info("规则编号:"+	RULE_CODE);
		Log.info("规则名称:"+	RULE_NAME);
		Log.info("规则类型:"+	RULE_TYPE);
		Log.info("是否线上:"+	IS_ONLINE);
		Log.info("异常类型:"+	EXCP_TYPE);
//		Log.info("商户业务类型:"+	COM_TYPE_NO);
		Log.info("规则描述:"+	RULE_DES);
//		Log.info("规则等级:"+	RULE_LEVEL);
//		Log.info("一般预警项:"+	RULE_LEVEL_ITEM);
//		Log.info("生效日期:"+	RULE_START_DATE);
//		Log.info("失效日期:"+	RULE_END_DATE);
		Log.info("是否可用:"+	IS_USE);

		//效验该编码是否已经存在
		//插入规则表
		//插入规则参数表
		String addsql = "insert into RCS_EXCEPT_TRAN_RULE"
		                 +"(RULE_CODE,RULE_NAME,RULE_TYPE,IS_ONLINE,EXCP_TYPE,"
				         +"RULE_DES,CREATE_NAME,CREATE_DATE,CREATE_DATETIME,UPDATE_NAME,UPDATE_DATE,UPDATE_DATETIME,"
		                 +"RULE_LEVEL_ITEM) values('"
		  	             + RULE_CODE+"','"
				         + RULE_NAME+"','"
		  	             + (RULE_TYPE ==null?"":RULE_TYPE)+"','"
				         + (IS_ONLINE ==null?"":IS_ONLINE)+"','"
		  	             + (EXCP_TYPE ==null?"":EXCP_TYPE)+"','"
					     + (RULE_DES  ==null?"":RULE_DES) +"','"
		  	             + Etf.getChildValue("SESSIONS.UID")+"','"
						 + DateUtil.getCurrentDate() + "','"
						 + DateUtil.getCurrentDateTime() + "','" // 创建
						 + Etf.getChildValue("SESSIONS.UID")+"','"
						 + DateUtil.getCurrentDate() + "','"
						 + DateUtil.getCurrentDateTime() + "','"
						 + (RULE_LEVEL_ITEM==null?"":RULE_LEVEL_ITEM)+"')";//一般预警项
		
		Log.info("addsql = "+addsql);
		
		int result1 = Atc.ExecSql(null, addsql);
		if(result1!= -1){
		//插入参数表
			for(int i=1;i<= Integer.valueOf(PARAMNUM);i++){
				
				Log.info("Etf.getChildValue(RULE_PARAM+i) ="+Etf.getChildValue("RULE_PARAM"+i) );
				Log.info("Etf.getChildValue(RULE_TYPE+i)  ="+Etf.getChildValue("RULE_TYPE"+i) );
				
				String addsql2 = "insert into RCS_TRAN_RULE_PARAM values('"
				                  + RULE_CODE+"','"+i+"','"+Etf.getChildValue("PARAM_NAME"+i)+"','" 
						          + (Etf.getChildValue("RULE_TYPE"+i).equals("元")?(Etf.getChildValue("RULE_PARAM"+i)+"00"):Etf.getChildValue("RULE_PARAM"+i)) 
						          +"','"+Etf.getChildValue("RULE_TYPE"+i)+"')";
				//Log.info("参数元时："+(Etf.getChildValue("RULE_PARAM"+i)+"00"));
				Log.info("addsql2 = " + addsql2);
				result1 = Atc.ExecSql(null, addsql2);
			}
		}
		//运营商是未启用状态 或主表没存在记录时才插入主表
			if (result1 !=0 ) {
				Atc.error("P0003", "添加失败！");
				return;
			}else{
				Etf.setChildValue("MsgTyp", "N");
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", "添加成功");
				Etf.setChildValue("FORWARDURL", "540101.tran");
				Etf.setChildValue("CALLBACKTYPE","forward");
			}
		
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}
        
	/**
	 *单个用户异常交易规则明细
	 * @param logger
	 * @throws Exception
	 */
	@Code("540102")
	@tangdi.interceptor.Log
	public void sigleUsrRuleDetail(ILog logger) throws Exception {
            Log.info("540102单个用户异常交易规则明细");
            String sql = "select * from RCS_EXCEPT_TRAN_RULE where rule_code = '" + RULE_CODE + "'";//查询是否存在该规则
            String sql2 = "select * from rcs_tran_rule_param where rule_code = '" + RULE_CODE + "'";//查询参数表中是否存在该规则
            
            logger.info("执行的sql语句是=%s", sql);
            int result = Atc.ReadRecord(sql);
            if (result == 0) {
            	int result2 = Atc.ReadRecord(sql2);
            	if(result2 == 0){	//如果规则有参数
    	    		logger.info("执行的sql语句是=%s", sql2);
    	    		int result1 = Atc.QueryInGroup(sql2.toString(), "REC", null);
    	    		if(result1 == 0){
    	    			Etf.setChildValue("MsgTyp", "N");
    	    			Etf.setChildValue("RspCod", "000000");
    	    			Etf.setChildValue("RspMsg", "查询成功");
    	    			
    	    		} else {
    	    			Etf.setChildValue("MsgTyp", "E");
    	    			Etf.setChildValue("RspCod", "200005");
    	    			Etf.setChildValue("RspMsg", "查询失败");
    	    			return;
    	    		}
            	} else{
	    			Etf.setChildValue("MsgTyp", "N");
	    			Etf.setChildValue("RspCod", "000000");
	    			Etf.setChildValue("RspMsg", "查询成功");
            	}
			} else if (result == 2) {
				Atc.error("200001", "记录未找到");
			} else if (result == -1) {
				Atc.error("200002", "系统错误");
				return;
			} else {
				Atc.error("200008", "系统出错");
				return;
			}
    		
    		Msg.dump();
    		Msg.set(TdWebConstants.WEB_FORWARD_NAME,
    				"/WEB-INF/html/excptran/RuleDetail.jsp");
	}
	
	/**
	 *规则修改
	 * @param logger
	 * @throws Exception
	 */
	@Code("540103")
	@tangdi.interceptor.Log
	public void beforeUptUsrRule(ILog logger) throws Exception {
        	String sql = "select * from RCS_EXCEPT_TRAN_RULE where rule_code = '" + RULE_CODE + "'";
        	String sql2 = "select * from rcs_tran_rule_param where rule_code = '" + RULE_CODE + "'";//查询参数表中是否存在该规则

    		int result = Atc.ReadRecord(sql);
    		if (result == 0) {
    			int result2 = Atc.ReadRecord(sql2);
    			if(result2 == 0){	//如果规则有参数
    	    		int result1 = Atc.QueryInGroup(sql2.toString(), "REC", null);
    	    		if(result1 == 0){
    	    			Etf.setChildValue("MsgTyp", "N");
    	    			Etf.setChildValue("RspCod", "000000");
    	    			Etf.setChildValue("RspMsg", "查询成功");
    	    		} else {
    	    			Etf.setChildValue("MsgTyp", "E");
    	    			Etf.setChildValue("RspCod", "200005");
    	    			Etf.setChildValue("RspMsg", "查询失败");
    	    			return;
    	    		}
    			} else{
	    			Etf.setChildValue("MsgTyp", "N");
	    			Etf.setChildValue("RspCod", "000000");
	    			Etf.setChildValue("RspMsg", "查询成功");
            	}	    		
    		}  
    		Msg.dump();
    		Msg.set(TdWebConstants.WEB_FORWARD_NAME,
    				"/WEB-INF/html/excptran/updateUsrRule.jsp");
	}
	
	@Code("540104")
	@tangdi.interceptor.Log
	public void updateRule(ILog logger) throws Exception {
		StringBuffer sql = new StringBuffer("update RCS_EXCEPT_TRAN_RULE set");
		
		if (StringUtils.isNotEmpty(this.IS_USE)) {
			sql.append(" IS_USE = '" + this.IS_USE + "',");
		}
		if (StringUtils.isNotEmpty(this.RULE_DES)) {
			sql.append(" RULE_DES = '" + this.RULE_DES + "',");
		}
		if (StringUtils.isNotEmpty(this.RULE_DES)) {
			sql.append(" EXEC_RATE = '" + this.EXEC_RATE + "',");
		}
		sql.append(" UPDATE_DATE='" + DateUtil.getCurrentDate() + "',");
		sql.append(" UPDATE_DATETIME='" + DateUtil.getCurrentDateTime() + "',");
		
		Log.info("执行的语句是=%s", sql.toString());
		int index = sql.lastIndexOf(",");
		sql = sql.replace(index, index + 1, " ");
		sql.append(" where RULE_CODE='" + this.RULE_CODE + "'");
		
		Log.info("执行的语句是=%s", sql.toString());
		int result = Atc.ExecSql(null, sql.toString());
		int k=0;
		if (result == 0) {
			//插入参数表(循环)
			for(int i=1;i<=Integer.parseInt(this.PARAMNUM);i++) {
				StringBuffer sql1 = new StringBuffer("update rcs_tran_rule_param set");			
					sql1.append(" PARAM_NAME='" + Etf.getChildValue("PARAM_NAME"+Integer.toString(i)) + "',");
					if(Etf.getChildValue("PARAM_TYPE"+Integer.toString(i)).equals(new String("元"))) {
						//sql1.append(" PARAM_VALUE='" + Double.toString(Double.parseDouble(Etf.getChildValue("PARAM_VALUE"+Integer.toString(i)))*100) + "'");//(Float.parseFloat(Etf.getChildValue("PARAM_VALUE"+Integer.toString(i)))*100)
						sql1.append(" PARAM_VALUE='" + RcsUtils.turnAmtY2F(Etf.getChildValue("PARAM_VALUE"+Integer.toString(i))) + "'");
					}else if(Etf.getChildValue("PARAM_TYPE"+Integer.toString(i)).equals(new String("次"))){
						sql1.append(" PARAM_VALUE='" + RcsUtils.formatDigits(Etf.getChildValue("PARAM_VALUE"+Integer.toString(i))) + "'");
					}else{
						sql1.append(" PARAM_VALUE='" + Etf.getChildValue("PARAM_VALUE"+Integer.toString(i)) + "'");
					}
					//sql1.append(" PARAM_TYPE='" + Etf.getChildValue("PARAM_TYPE"+Integer.toString(i)) + "'");
					sql1.append(" where RULE_CODE='" + this.RULE_CODE + "' and PARAM_ID='" + Integer.toString(i) + "'");
								
					Log.info("执行的语句是=%s", sql1.toString());
					int result1 = Atc.ExecSql(null, sql1.toString());
					
					if (result1 != 0) {
						k=result1;
						break;
					}
			}			
			if (k == 0) {
				Etf.setChildValue("MsgTyp", "N");
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", "修改成功");
				Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
				if(Integer.parseInt(this.RULE_TYPE) == 0){
					Etf.setChildValue("FORWARDURL", "540101.tran");
				}else if(Integer.parseInt(this.RULE_TYPE) == 1) {
					Etf.setChildValue("FORWARDURL", "540111.tran");
				}
				
			} else if (k == 2) {
				Atc.error("200004", "修改失败");
				Atc.rollBackWork(null);
			} else if (k == 3) {
				Atc.error("200006", "违反数据唯一性");
				Atc.rollBackWork(null);
			} else {
				Atc.error("200008", "系统出错");
				Atc.rollBackWork(null);
			}
		} else if (result == 2) {
			Atc.error("200004", "修改失败");
		} else if (result == 3) {
			Atc.error("200006", "违反数据唯一性");
		} else {
			Atc.error("200008", "系统出错");
		}		
		
		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
	}
	
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		String str_Param = "RULE_DES,RULE_DES1,RULE_CODE,RULE_NAME,PARAM_NAME,COM_TYPE_NO";
		for(int i=1;i<=50;i++) {
			str_Param = str_Param + (",PARAM_NAME" + Integer.toString(i));
		}
		RcsUtils.turnSpecialChar(str_Param,1);
		Object obj = ic.proceed();
		RcsUtils.turnSpecialChar(str_Param,0);
		GetOwnButton.getOwnButton();
		return obj;
	}
	
	
}
