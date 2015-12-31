package com.rcs.sustran;

import java.util.List;
import java.util.Map;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.annotations.Code;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.web.TdWebConstants;

import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.RcsDefault;
import com.util.RcsUtils;

/**
 * 可疑交易记录管理
 * 棠棣科技
 * @author 林元生
 */
@tangdi.engine.DB
public class SusTranMng {
	/**
	 * 当前页数
	 */
	@Data
	private String pageNum;
	/**
	 * 规则编码
	 */
	@Data
	private String RULE_CODE;
	/**
	 * 客户名称
	 */
	@Data
	private String USER_NAME;
	/**
	 * 客户名称
	 */
	@Data
	private String COMP_NAME;

	/**
	 * 规则编码数组
	 */
	@Data
	private String RULE_CODE_CODE;
	/**
	 * 交易流水号
	 */
	@Data
	private String TRAN_CODE;
	/**
	 * 规则名称
	 */
	@Data
	private String RULE_NAME;
	/**
	 * 规则描述
	 */
	@Data
	private String RULE_DES;
	/**
	 * 异常客户类型
	 */
	@Data
	private String CLIENT_TYPE;
	/**
	 * 客户编号
	 */
	@Data
	private String CLIENT_CODE;
	/**
	 * ID
	 */
	@Data
	private String ID;

	/**
	 * START_DATE
	 */
	@Data
	private String START_DATE;
	/**
	 * END_DATE
	 */
	@Data
	private String END_DATE;
	/**
	 * 可疑交易状态 0 可疑，1 已确认，2已释放
	 */
	@Data
	private String EXCEPT_TRAN_FLAG;
	/**
	 * 每页显示条数
	 */
	@Data
	private String NUMPERPAGE;
	/**
	 * 显示可选页数
	 */
	@Data
	private String PAGENUMSHOWN;
	/**
	 * 可疑交易记录生成日期
	 */
	@Data
	private String REGDT_DAY;
	/**
	 * 异常客户类型
	 */
	@Data
	private String ENTITY_TYPE;

	/**
	 * 可疑交易汇总查询
	 * @throws Exception
	 */
	@Code("540881")
	public void selectAll2() throws Exception {
		String sql = "select except.id,except.regdt_day,except.rule_code,rule.RULE_DES,except.entity_type,except.user_code,except.comp_code,";
		   sql += "(select count(*) from RCS_EXCEPT_TRAN_DETAIL_INFO where id = except.id) as SUMC,";
		   sql += "(select sum(serial_record.TRAN_AMT)";
		   sql += "    from RCS_EXCEPT_TRAN_DETAIL_INFO detail_info,RCS_TRAN_SERIAL_RECORD serial_record ";
		   sql += "    where detail_info.id = except.id ";
		   sql += "    and detail_info.tran_code = serial_record.id ";
		   sql += ")as SUMA ";
		   sql += "from RCS_EXCEPT_TRAN_INFO except,RCS_EXCEPT_TRAN_RULE rule ";
		   sql += "where except.Rule_Code = rule.Rule_Code ";
		   sql += "and except.EXCEPT_TRAN_FLAG ='"+RcsDefault.EXCEPT_TRAN_FLAG_1+"' ";
		   sql += "and except.USER_CODE !='-1' ";
		 
		//客户类型
		if (StringUtils.isNotEmpty(ENTITY_TYPE)) {
			sql += " and except.ENTITY_TYPE='" + this.ENTITY_TYPE + "'";
		}
		//客户编号
		if (StringUtils.isNotEmpty(CLIENT_CODE)) {
			sql += " and except.USER_CODE like '%" + this.CLIENT_CODE + "%' or except.COMP_CODE like '%"+this.CLIENT_CODE +"%'";
		}
		//时间范围    
		if (StringUtils.isNotEmpty(START_DATE)) {
			sql += " and except.regdt_day >= '" + START_DATE.replaceAll("-", "")+ "'";
		}
		//时间范围    END_DATE
		if (StringUtils.isNotEmpty(END_DATE)) {
			sql += " and except.regdt_day <= '" + END_DATE.replaceAll("-", "") + "'";
		}
		//规则编码
		Log.info("规则编码数组为 ：" +RULE_CODE_CODE);
		if (StringUtils.isNotEmpty(RULE_CODE_CODE)) {			
			String Sql_RULE_CODE_CODE = "";
			String str = this.RULE_CODE_CODE;
			String str1[] = str.split(";");
			for (int i = 0; i < str1.length; i++) {
				Sql_RULE_CODE_CODE += "'" + str1[i] + "'";
				if (i != str1.length - 1) {
					Sql_RULE_CODE_CODE += ",";
				}
			}
			sql += "and  except.rule_code in(" + Sql_RULE_CODE_CODE+ ")";
		}
		 
		sql +=" order by except.regdt_day desc,except.rule_code ";
		
		Log.info("打印查询sql语句 ：" + sql.toString());

		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		
		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql.toString());
		
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			int TOLCNT = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			Log.info("开始页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else {
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败");
		}

		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/sustran/susRuleList.jsp");
	}

	/**
	 * 根据id 修改异常交易状态 已确认  已释放
	 * @throws Exception
	 */
	@Code("540884")
	public void uplistExceptTran() throws Exception {
		String sExceptTranFlag = Etf.getChildValue("EXCEPT_TRAN_FLAG");// 0：释放为异常 2：释放为正常 
		String sId             = Etf.getChildValue("ID");//异常主表id,用于确定用户或商户信息
		String sUserCode       = null;
		String sUserAppFlag    = null;
		//更新异常交易状态
		String sSql = "UPDATE RCS_EXCEPT_TRAN_INFO SET EXCEPT_TRAN_FLAG='"
				+ sExceptTranFlag+ "',UPDATE_NAME = '"
				+ Etf.getChildValue("SESSIONS.UID") + "'," + "update_date='"
				+ DateUtil.getCurrentDate() + "'," + "update_datetime='"
				+ DateUtil.getCurrentDateTime() + "' " +
			  " where ID='"+ sId +"'";
		
		Log.info("打印查询sql语句 ：" + sSql);
		
		int iResult = Atc.ExecSql(null, sSql);
		if(0 != iResult){
			Atc.error("100001", "更新状态出错！");
			Atc.rollBackWork(null);
			return;
		}
		
		//查询用户ID
		sSql = "SELECT USER_CODE FROM RCS_EXCEPT_TRAN_INFO WHERE ID = '"+sId+"' ";
		iResult = Atc.ReadRecord(sSql);
		if(0 != iResult){
			Atc.error("100001", "查询用户状态出错！");
			Atc.rollBackWork(null);
			return;
		}
		
		//查询的是更新以后的用户对应的所有异常状态
		Map<String, List<Element>> exceptTranMap = RcsUtils.getExceptTrans(sId);
		List<Element> flag0Map                   = exceptTranMap.get(RcsDefault.EXCEPT_TRAN_FLAG_0);
		List<Element> flag1Map                   = exceptTranMap.get(RcsDefault.EXCEPT_TRAN_FLAG_1);
		
		sUserCode = Etf.getChildValue("USER_CODE");
		//根据释放状态更新不同的名单状态
		if(RcsDefault.EXCEPT_TRAN_FLAG_0.equals(sExceptTranFlag)){
			sUserAppFlag = RcsDefault.LIMIT_RISK_FLAG_1;
			//只有异常 且 没有可疑才可更新
			if((null==flag0Map || flag0Map.size()>=1) && (null==flag1Map || flag1Map.size()==0)){
				//判断是否可以自动更新
				if(RcsUtils.isCanUpdateUserRisk("0", sUserCode, sUserAppFlag, null)){
					//设置用户X名单
					RcsUtils.setUserRisk("0",sUserCode,sUserAppFlag,null);
					//记录X名单历史
					RcsUtils.addXList("0",sUserCode,sUserAppFlag,"1");
				}
			}
		}
		if(RcsDefault.EXCEPT_TRAN_FLAG_2.equals(sExceptTranFlag)){
			//没有任何异常 且 没有任何可疑 才可更新
			if((null==flag0Map || flag0Map.size()>=1) && (null==flag1Map || flag1Map.size()==0)){
				sUserAppFlag = RcsDefault.LIMIT_RISK_FLAG_1;
				//判断是否可以自动更新
				if(RcsUtils.isCanUpdateUserRisk("0", sUserCode, sUserAppFlag, null)){
					//设置用户X名单
					RcsUtils.setUserRisk("0",sUserCode,sUserAppFlag,null);
					//记录X名单历史
					RcsUtils.addXList("0",sUserCode,sUserAppFlag,"1");
				}
			}
			//没有任何异常 且 没有任何可疑 才可更新
			if((null==flag0Map || flag0Map.size()==0) && (null==flag1Map || flag1Map.size()==0)){
				sUserAppFlag = RcsDefault.LIMIT_RISK_FLAG_0;
				//判断是否可以自动更新
				if(RcsUtils.isCanUpdateUserRisk("0", sUserCode, sUserAppFlag, null)){
					//设置用户X名单
					RcsUtils.setUserRisk("0",sUserCode,sUserAppFlag,null);
					//记录X名单历史
					RcsUtils.addXList("0",sUserCode,sUserAppFlag,"1");
				}
			}
		}
		
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "操作成功");
		Etf.setChildValue("CALLBACKTYPE", "forward");
		Etf.setChildValue("FORWARDURL", "540881.tran");
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
	}

	/**
	 * 根据日期、规则编号、客户类型和客户名称确认释放可疑交易记录
	 * @throws Exception
	 */
	@Code("540886")
	public void uplistRule() throws Exception {
		Log.info("540886确认释放可疑交易记录开始");
		String sql = "update RCS_EXCEPT_TRAN_INFO set EXCEPT_TRAN_FLAG='"
				+ Etf.getChildValue("ID_EXCEPT_TRAN_FLAG") + "',update_name='"
				+ Etf.getChildValue("SESSIONS.USERNAME1") + "'," + "update_date='"
				+ DateUtil.getCurrentDate() + "'," + "update_datetime='"
				+ DateUtil.getCurrentDateTime() + "' " + " where REGDT_DAY='"
				+ Etf.getChildValue("ID_REGDT_DAY") + "' " + "and RULE_CODE='"
				+ Etf.getChildValue("ID_RULE_CODE") + "'"
				+ " and EXCEPT_TRAN_FLAG<>'"+ Etf.getChildValue("ID_EXCEPT_TRAN_FLAG") + "' " +
				" and warn_type='2' ";
		if (StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_TYPE"))) {
			sql += " and CLIENT_TYPE='" + Etf.getChildValue("ID_CLIENT_TYPE")+ "' ";
		}
		if (StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_CODE"))) {
			sql += " and CLIENT_CODE='" + Etf.getChildValue("ID_CLIENT_CODE")+ "' ";
		}
		Log.info("打印查询sql语句 ：" + sql);
		String sType = "上报";
		if ("1".endsWith(Etf.getChildValue("ID_EXCEPT_TRAN_FLAG")))
			sType = "释放";
		int result = Atc.ExecSql(null, sql);
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
		if (result == 0) {
			sql = "update rcs_x_list_sys t set "
					+ "t.x_type='"
					+ (Etf.getChildValue("ID_EXCEPT_TRAN_FLAG").equals("1") ? "1"
							: "3") + "', " + "update_name='"
					+ Etf.getChildValue("SESSIONS.USERNAME1") + "',"
					+ "update_date='" + DateUtil.getCurrentDate() + "',"
					+ "update_datetime='" + DateUtil.getCurrentDateTime()
					+ "' " + " where t.regdt_day='"
					+ Etf.getChildValue("ID_REGDT_DAY") + "' "
					+ " and t.rule_code ='" + Etf.getChildValue("ID_RULE_CODE")
					+ "' " + " and t.cas_type='2' ";
			if (StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_TYPE"))) {
				sql += " and t.user_type='"
						+ Etf.getChildValue("ID_CLIENT_TYPE") + "' ";
			}
			if (StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_CODE"))) {
				sql += " and t.client_code='"
						+ Etf.getChildValue("ID_CLIENT_CODE") + "' ";
			}
			Log.info("打印查询sql语句 ：" + sql);
			result = Atc.ExecSql(null, sql);
			String url = "540881.tran?PAGENUM=" + pageNum + "&CLIENT_TYPE="
					+ CLIENT_TYPE + "&USER_NAME=" + USER_NAME
					+ "&RULE_CODE_CODE=" + RULE_CODE_CODE + "&RULE_CODE_NAME="
					+ Etf.getChildValue("RULE_CODE_NAME") + "&START_DATE="
					+ RcsUtils.turnDateFormat(START_DATE) + "&END_DATE="
					+ RcsUtils.turnDateFormat(END_DATE);
			if (result == 0) {
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", sType + "成功");
				Etf.setChildValue("CALLBACKTYPE", "forward");
				Etf.setChildValue("FORWARDURL", url);
			} else if (result == 2) {
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", sType + "成功");
				Etf.setChildValue("CALLBACKTYPE", "forward");
				Etf.setChildValue("FORWARDURL", url);
			} else {
				Atc.error("200002", "系统错误");
				Atc.rollBackWork(null);
			}
		} else if (result == 2) {
			Atc.error("200005", sType + "失败，未找到记录！");
		} else {
			Atc.error("200002", "系统错误");
		}
	}
	
	
	@Code("sustranAtion")
	  public void confirmAtion() throws Exception
	  {
		  String sql = "select SEQ_ID AS SEQ_ID_ENT,ID AS ID_ENT,USER_CODE AS USER_CODE_ENT,"
		  		       + "EXCEPT_TRAN_FLAG AS EXCEPT_TRAN_FLAG_ENT,"
		  		       + "to_char(to_date(CONFIRM_TIME,'yyyyMMdd hh24miss'),'yyyy-MM-dd hh24:mi:ss') AS CONFIRM_TIME_ENT,"
		  		       + "CONFIRM_INF AS CONFIRM_INF_ENT,FILE1,FILE2 "
		  		     + "from RCS_CONFIRMATION_INFO   "
		  		     + "where ID ='"+this.ID +"'  order by CONFIRM_TIME desc";
		  int result = Atc.QueryInGroup(sql, "GRP", "NUM");     
		    if (result == -1) {
		      Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
		      Atc.error("200003", "系统错误");
		      return;
		    }else if(result == 2){
		      Etf.setChildValue("MsgTyp", "N");
			  Etf.setChildValue("RspCod", "081234");
			  Etf.setChildValue("RspMsg", "查询成功");
		    }else{
		      Etf.setChildValue("MsgTyp", "N");
			  Etf.setChildValue("RspCod", "000000");
			  Etf.setChildValue("RspMsg", "查询成功");	
		    }
		    Msg.set("_REQUESTATTR.FORWARDURL", "/WEB-INF/html/sustran/basisPage.jsp");
	  }
	
	
	 
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		RcsUtils.turnSpecialChar("USER_NAME,TRAN_CODE,RULE_NAME,RULE_DES",1);
		Object obj = ic.proceed();
		RcsUtils.turnSpecialChar("USER_NAME,TRAN_CODE,RULE_NAME,RULE_DES",0);
		GetOwnButton.getOwnButton();
		return obj;
	}
}
