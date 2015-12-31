package com.rcs.excptranMng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import net.sf.excelutils.ExcelUtils;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.annotations.Code;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.log.ILog;
import tangdi.web.TdWebConstants;

import com.bean.ExceptTran;
import com.bean.ExceptTrantail;
import com.resource.ResourceFile;
import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.RcsDefault;
import com.util.RcsUtils;

/**
 * 异常交易记录管理
 * @author gz
 */
@tangdi.engine.DB
public class ExcpTranMng {
	/**
	 * 当前页数
	 */
	@Data
	private String pageNum;
	@Data
	private String EXCP_TYPE;
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
	//交易来源
	@Data
	private String TRAN_SOURCE;
	//交易类型
	@Data
	private String TRAN_TYPE;
	//交易方式
	@Data
	private String TRAN_CLIENT;
	
	
	@Data
	  private String CONFIRM_INF;
	
	@Data
	  private String FILE1;
	
	@Data
	  private String FILE2;
	

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
	private String ENTITY_TYPE;
	/**
	 * kehu编号
	 */
	@Data
	private String CLIENT_CODE;
	/**
	 * 用户编号
	 */
	@Data
	private String USER_CODE;
	/**
	 * 商户编号
	 */
	@Data
	private String COMP_CODE;
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
	 * 异常交易状态 0 可疑，1 已确认，2已释放
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
	 * 异常交易记录生成日期
	 */
	@Data
	private String REGDT_DAY;	
	
	/**
	 * 查询所有异常交易(异常交易库)
	 * @throws Exception
	 */
	@Code("540280")
	public void selectAll1() throws Exception {
		Msg.dump();
		Log.info(""+ Etf.getChildValue("RULE_CODE_NAME"));
		String sql = " select a.*,b.rule_name,b.RULE_LEVEL_ITEM from RCS_EXCEPT_TRAN_INFO a "; 
			   sql +=" left join"; 
			   sql +=" 	RCS_EXCEPT_TRAN_RULE  b "; 
			   sql +=" on a.rule_code = b.rule_code";
			   sql +=" where 1=1 ";
		
		//客户类型
		if (StringUtils.isNotEmpty(ENTITY_TYPE)) {
			sql += " and ENTITY_TYPE='" + this.ENTITY_TYPE + "'";
		}
		//客户编号
		if (StringUtils.isNotEmpty(CLIENT_CODE)) {
			String s = " and USER_CODE like '%_" + CLIENT_CODE + "_%' or COMP_CODE like '%_"+ CLIENT_CODE +"_%'";
			sql += s;
		}
		//时间范围    
		if (StringUtils.isNotEmpty(START_DATE)) {
			sql += " and regdt_day >= '" + START_DATE.replaceAll("-", "")+ "'";
		}
		//时间范围    END_DATE
		if (StringUtils.isNotEmpty(END_DATE)) {
			sql += " and regdt_day <= '" + END_DATE.replaceAll("-", "") + "'";
		}
        
		sql += " ORDER BY     regdt_day desc ";
		
		Log.info("打印查询sql语句 ：" + sql);
		if (StringUtils.isEmpty(pageNum)) {
			this.pageNum = "1";
		}

		NUMPERPAGE = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql);
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
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/exceptTranList.jsp");
	}
	/**
	 * 查询所有异常交易明细信息
	 * @throws Exception
	 */
	@Code("540283")
	public void selectAllDetail() throws Exception {
		String id = Etf.getChildValue("ID");
		String sql = "select b.*,c.ENTITY_TYPE, ";
			   sql += "(select user_name from Rcs_Tran_User_Info where user_code = b.user_code) USER_NAME2, ";
			   sql += "(select comp_name from Rcs_Tran_Comp_Info where comp_code = b.comp_code) COMP_NAME2 ";
			   sql += "from RCS_EXCEPT_TRAN_DETAIL_INFO a ";
			   sql += "left join ";
               sql += "RCS_TRAN_SERIAL_RECORD b on a.tran_code = b.id ";
               sql += "left join ";
               sql += "RCS_EXCEPT_TRAN_INFO c on a.id = c.id ";
               sql += "where a.id='"+id+"' ";
               sql += "ORDER BY b.id desc ";
		
		Log.info("打印查询sql语句 ：" + sql);
		if (StringUtils.isEmpty(pageNum)) {
			this.pageNum = "1";
		}

		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql);
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			int TOLCNT  = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			Log.info("开始页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else {
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败");
		}
		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/exceptRuleDetailList.jsp");
	}
	/**
	 * 查询所有异常交易明细信息
	 * @throws Exception
	 */
	@Code("540285")
	public void selectAllDetail2() throws Exception {
		
		String id = Etf.getChildValue("ID");
		Log.info(">>>>>id = "+ id);
		String sql = "select b.*,c.ENTITY_TYPE, ";
		   	sql += "(select user_name from Rcs_Tran_User_Info where user_code = b.user_code) USER_NAME2, ";
		   	sql += "(select comp_name from Rcs_Tran_Comp_Info where comp_code = b.comp_code) COMP_NAME2 ";
	    	sql += "from RCS_EXCEPT_TRAN_DETAIL_INFO a ";
			sql += "left join ";
	        sql += "RCS_TRAN_SERIAL_RECORD b on a.tran_code = b.id ";
	        sql += "left join ";
	        sql += "RCS_EXCEPT_TRAN_INFO c on a.id = c.id ";
	        sql += "where a.id='"+id+"' ";
		 
		//流水
		if (StringUtils.isNotEmpty(TRAN_CODE)) {
			sql += " and b.TRAN_CODE like '%" + TRAN_CODE + "%'";
		}
		//交易来源
		if (StringUtils.isNotEmpty(TRAN_SOURCE)) {
			sql += " and b.TRAN_SOURCE = '" + TRAN_SOURCE + "'";
		}
      
		//交易类型
		if (StringUtils.isNotEmpty(TRAN_TYPE)) {
			sql += " and b.TRAN_TYPE = '" + TRAN_TYPE + "'";
		}
		//交易方式
		if (StringUtils.isNotEmpty(TRAN_CLIENT)) {
			sql += " and b.TRAN_CLIENT = '" + TRAN_CLIENT + "'";
		}
		sql += " ORDER BY  b.TRAN_CODE desc ";
		
		Log.info("打印查询sql语句 ：" + sql);
		if (StringUtils.isEmpty(pageNum)) {
			this.pageNum = "1";
		}

		NUMPERPAGE = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql);
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
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/exceptRuleDetailList.jsp");
	}

	/**
	 * 异常交易汇总查询
	 * @throws Exception
	 */
	@Code("540281")
	public void selectAll2() throws Exception {
		//明细表tran_code 与交易流水表id 对应
		/*String sql = "select except.id,except.regdt_day,except.rule_code,rule.RULE_DES,except.entity_type,except.user_code,except.comp_code,";
		sql += "(select count(*) from RCS_EXCEPT_TRAN_DETAIL_INFO where id = except.id) as SUMC,";
		sql += "(select sum(serial_record.TRAN_AMT)";
		sql += "    from RCS_EXCEPT_TRAN_DETAIL_INFO detail_info,RCS_TRAN_SERIAL_RECORD serial_record ";
		sql += "    where detail_info.id = except.id ";
		sql += "    and detail_info.tran_code = serial_record.id ";
		sql += ")as SUMA ";
		sql += "from RCS_EXCEPT_TRAN_INFO except,RCS_EXCEPT_TRAN_RULE rule ";
		sql += "where except.Rule_Code = rule.Rule_Code ";
		sql += "and except.EXCEPT_TRAN_FLAG ='"+RcsDefault.EXCEPT_TRAN_FLAG_0+"' ";
		sql += "and except.USER_CODE !='-1' ";*/
		
		String sql="select a.*,b.SUMC,c.SUMA from ( select ";
		sql+=" except.id, except.regdt_day, except.rule_code,rule.RULE_DES,";
		sql+=" except.entity_type,except.user_code,except.comp_code    ";
		sql+=" from RCS_EXCEPT_TRAN_INFO except, RCS_EXCEPT_TRAN_RULE rule";
		sql+="  where except.Rule_Code = rule.Rule_Code and except.EXCEPT_TRAN_FLAG = '0' and except.USER_CODE != '-1'";
		sql+=" ) a, ";
		sql+="(select trim(id) id,count(*) AS SUMC  from RCS_EXCEPT_TRAN_DETAIL_INFO  group by id) b,";
		sql+=" (select trim(detail_info.id) id,sum(serial_record.TRAN_AMT) AS SUMA";
		sql+=" from RCS_EXCEPT_TRAN_DETAIL_INFO detail_info, RCS_TRAN_SERIAL_RECORD      serial_record";
		sql+="  where trim(detail_info.tran_code) = serial_record.id group by detail_info.id) c";
		sql+="    where a.id=b.id and a.id=c.id ";
	   
		//客户类型
		if (StringUtils.isNotEmpty(ENTITY_TYPE)) {
			sql += " and a.ENTITY_TYPE='" + this.ENTITY_TYPE + "'";
		}
		//时间范围    END_DATE
		if (StringUtils.isNotEmpty(END_DATE)) {
			sql += " and a.regdt_day <= '" + END_DATE.replaceAll("-", "") + "'";
		}
		//客户编号
		if (StringUtils.isNotEmpty(CLIENT_CODE)) {
			sql += " and ( a.USER_CODE like '%" + this.CLIENT_CODE + "%' or a.COMP_CODE like '%"+this.CLIENT_CODE +"%' )";
		}
		//时间范围    
		if (StringUtils.isNotEmpty(START_DATE)) {
			sql += " and a.regdt_day >= '" + START_DATE.replaceAll("-", "")+ "'";
		}
		
		if(StringUtils.isNotEmpty(START_DATE) && StringUtils.isNotEmpty(END_DATE)){
			String sStartDate = START_DATE.replaceAll("-", "");
			String sEndDate   = END_DATE.replaceAll("-", "");
			
			if(sStartDate.compareTo(sEndDate)>0){
				Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/ajaxrequest.jsp");
				Etf.setChildValue("RspCod", "000001");
				Etf.setChildValue("RspMsg", "起始时间大于终止时间！");
				return;
			}
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
			sql += "and  a.rule_code in(" + Sql_RULE_CODE_CODE+ ")";
		}
		 
		//sql +=" order by except.regdt_day desc,except.rule_code ";
		sql+="  order by a.regdt_day desc, a.rule_code";
		Log.info("打印查询sql语句 ：" + sql.toString());

		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		
		NUMPERPAGE = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
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
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/exceptRuleList.jsp");
	}

	/**
	 * 查询规则列表
	 * @throws Exception
	 */
	@Code("540282")
	public void selectAllRule() throws Exception {
		String sql = "select * from RCS_EXCEPT_TRAN_RULE a  where 1=1 ";
		
		//异常 或 可疑
		if(StringUtils.isNotEmpty(EXCP_TYPE)){
			sql += " and a.EXCP_TYPE='"+EXCP_TYPE+"'";
		}
		//规则code
		if(StringUtils.isNotEmpty(RULE_CODE)){
			sql += " and a.RULE_CODE like '%"+RULE_CODE+"%'";
		}
		//规则名称
		if(StringUtils.isNotEmpty(RULE_NAME)){
			sql += " and a.RULE_NAME like'%"+RULE_NAME+"%'";
		}
		//规则描述
		if (StringUtils.isNotEmpty(RULE_DES)) {
			sql += " and a.RULE_DES like '%"+RULE_DES+"%'";
		}	
		sql += " order by a.RULE_CODE,a.RULE_NAME ";
		int result = Atc.PagedQuery("1", "999", "REC", sql);
		if (result == -1) {
			Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
			Atc.error("200002", "系统错误");
			return;
		} else {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/ruleList.jsp");
	}
	
	/**
	 * 根据id 修改异常交易状态 已确认  已释放
	 * @throws Exception
	 */
	@Code("540284")
	public void uplistExceptTran2() throws Exception {
		String sNewTranFlag = Etf.getChildValue("EXCEPT_TRAN_FLAG");//1：确认为可疑 2：释放为正常 
		String sOldTranFlag = "";
		String sId          = Etf.getChildValue("ID");              //异常主表id,用于确定用户或商户信息
		String sEntifyType  = "";
		String sUCode       = "";
		
		String sQrySql = "SELECT EXCEPT_TRAN_FLAG,ENTITY_TYPE,USER_CODE,COMP_CODE from RCS_EXCEPT_TRAN_INFO where ID = '"+sId+"'";
		
		int iRes = Atc.ReadRecord(sQrySql);
		if(0 == iRes){
			//查询原异常交易状态
			sOldTranFlag = Etf.getChildValue("EXCEPT_TRAN_FLAG");
			//查询实体类型
			sEntifyType  = Etf.getChildValue("ENTITY_TYPE"); 
			if(RcsDefault.IS_USER.equals(sEntifyType)){
				sUCode = Etf.getChildValue("USER_CODE");
			}else if(RcsDefault.IS_COMP.equals(sEntifyType)){
				sUCode = Etf.getChildValue("COMP_CODE");
			}
			
			String	sSql  = "UPDATE RCS_EXCEPT_TRAN_INFO SET ";
					sSql += "EXCEPT_TRAN_FLAG = '"+sNewTranFlag                     +"',";
					sSql += "UPDATE_NAME = '"     +Etf.getChildValue("SESSIONS.UID")+"',";
					sSql += "UPDATE_DATE = '"     +DateUtil.getCurrentDate()        +"',";
					sSql += "UPDATE_DATETIME = '" +DateUtil.getCurrentDateTime()    +"' ";
					sSql += "WHERE ID = '"+ID+"'";
	
			Log.info("打印SQL语句：" + sSql);
			
			iRes = Atc.ExecSql(null, sSql);
			if(0 == iRes){
				Log.info("更新成功："+iRes);
				//查询的是更新以后的用户对应的所有异常状态
				Map<String, List<Element>> exceptTranMap = RcsUtils.getExceptTrans(sId);
				List<Element> flag0Map                   = exceptTranMap.get(RcsDefault.EXCEPT_TRAN_FLAG_0);
				List<Element> flag1Map                   = exceptTranMap.get(RcsDefault.EXCEPT_TRAN_FLAG_1);
				
				if(RcsDefault.EXCEPT_TRAN_FLAG_0.equals(sOldTranFlag)){
					//异常交易-->可疑交易  进入黑名单
					if(RcsDefault.EXCEPT_TRAN_FLAG_1.equals(sNewTranFlag)){
						Log.info("异常交易-->可疑交易  进入黑名单！");
						//设置用户X名单
						RcsUtils.setUserRisk(sEntifyType,sUCode,"2",null);
						//记录X名单历史
						RcsUtils.addXList(sEntifyType,sUCode,"2","2");
					}
					//异常交易-->正常交易  如果没有其他异常和可疑，则进入白名单
					if(RcsDefault.EXCEPT_TRAN_FLAG_2.equals(sNewTranFlag)){
						Log.info("异常交易-->正常交易  如果没有其他异常和可疑，则进入白名单！");
						//判断是否有其他异常和可疑交易
						if((null == flag0Map || flag0Map.size()==0) && (null == flag1Map || flag1Map.size()==0)){
							//判断是否可以自动更新
							if(RcsUtils.isCanUpdateUserRisk("0", sUCode, RcsDefault.LIMIT_RISK_FLAG_0, null)){
								//设置用户X名单
								RcsUtils.setUserRisk(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_0,null);
								//记录X名单历史
								RcsUtils.addXList(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_0,"1");
							}
						}
					}
				}else if(RcsDefault.EXCEPT_TRAN_FLAG_1.equals(sOldTranFlag)){
					//可疑交易-->异常交易  如果没有其他可疑，则进入灰名单
					if(RcsDefault.EXCEPT_TRAN_FLAG_0.equals(sNewTranFlag)){
						Log.info("可疑交易-->异常交易  如果没有其他可疑，则进入灰名单！");
						//判断是否有其他可疑交易
						if(null == flag1Map || flag1Map.size()==0){
							//设置用户X名单
							RcsUtils.setUserRisk(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_1,null);
							//记录X名单历史
							RcsUtils.addXList(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_1,"1");
						}
					}
					//可疑交易-->正常交易  如果没有其他异常和可疑，则进入白名单
					if(RcsDefault.EXCEPT_TRAN_FLAG_2.equals(sNewTranFlag)){
						Log.info("可疑交易-->正常交易  如果没有其他异常和可疑，则进入白名单！");
						//判断是否有其他异常和可疑交易
						if((null == flag0Map || flag0Map.size()==0) && (null == flag1Map || flag1Map.size()==0)){
							//设置用户X名单
							RcsUtils.setUserRisk(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_0,null);
							//记录X名单历史
							RcsUtils.addXList(sEntifyType,sUCode,RcsDefault.LIMIT_RISK_FLAG_0,"1");
						}
					}
				}
			}
		}
		
		
		 String inSql = "insert into RCS_CONFIRMATION_INFO(SEQ_ID,ID,USER_CODE,EXCEPT_TRAN_FLAG,CONFIRM_TIME,FILE1,FILE2,CONFIRM_INF) values(SEQ_CONFIRMATION_ID.nextval,'" + 
	      this.ID + "','" + Etf.getChildValue("SESSIONS.UID") + "','" + this.EXCEPT_TRAN_FLAG + 
	      "',to_char(sysdate,'yyyyMMddHH24miss'),'" +this.FILE1+"',"+ "'"+this.FILE2+"','"+this.CONFIRM_INF + "')";
	    int result = Atc.ExecSql(null, inSql);
		
		
		
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "操作成功");
		Etf.setChildValue("CALLBACKTYPE","forward");
		Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}
	
	/**
	 * 根据日期、规则编号、客户类型和客户名称确认释放异常交易记录
	 * @throws Exception
	 */
	@Code("540286")
	public void uplistRule() throws Exception {
		Log.info("540286确认释放异常交易记录开始");
		String sql = "update RCS_EXCEPT_TRAN_INFO set EXCEPT_TRAN_FLAG='"
				+ Etf.getChildValue("ID_EXCEPT_TRAN_FLAG") + "',update_name='"
				+ Etf.getChildValue("SESSIONS.USERNAME1") + "'," + "update_date='"
				+ DateUtil.getCurrentDate() + "'," + "update_datetime='"
				+ DateUtil.getCurrentDateTime() + "' " +
			  " where REGDT_DAY='"+Etf.getChildValue("ID_REGDT_DAY")+"' " +
			  		" and RULE_CODE='"+Etf.getChildValue("ID_RULE_CODE")+"'" +
			  		" and EXCEPT_TRAN_FLAG<>'"+Etf.getChildValue("ID_EXCEPT_TRAN_FLAG")+"' " +
			  		" and warn_type='1' ";
		if(StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_TYPE"))) {
			sql += " and CLIENT_TYPE='"+Etf.getChildValue("ID_CLIENT_TYPE")+"' ";
		}
		if(StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_CODE"))) {
			sql += " and CLIENT_CODE='"+Etf.getChildValue("ID_CLIENT_CODE")+"' ";
		}		
		Log.info("打印查询sql语句 ：" + sql);
		String sType="确认";
		if("1".endsWith(Etf.getChildValue("ID_EXCEPT_TRAN_FLAG"))) sType="释放";
		int result = Atc.ExecSql(null, sql);
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
		if (result == 0) {
			sql = "update rcs_x_list_sys t set " +
					"t.x_type='"+(Etf.getChildValue("ID_EXCEPT_TRAN_FLAG").equals("1")?"1":"3")+"', "
					+ "update_name='"+ Etf.getChildValue("SESSIONS.USERNAME1") + "'," 
					+ "update_date='"+ DateUtil.getCurrentDate() + "'," 
					+ "update_datetime='"+ DateUtil.getCurrentDateTime() + "' " +
					" where t.regdt_day='"+Etf.getChildValue("ID_REGDT_DAY")+"' " +
					" and t.rule_code ='"+Etf.getChildValue("ID_RULE_CODE")+"' " +
					" and t.cas_type='1' ";
			if(StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_TYPE"))) {
				sql += " and t.user_type='"+Etf.getChildValue("ID_CLIENT_TYPE")+"' ";
			}
			if(StringUtils.isNotEmpty(Etf.getChildValue("ID_CLIENT_CODE"))) {
				sql += " and t.client_code='"+Etf.getChildValue("ID_CLIENT_CODE")+"' ";
			}	
			Log.info("打印查询sql语句 ：" + sql);
			result = Atc.ExecSql(null, sql);
			String url ="540281.tran?PAGENUM="+pageNum+"&CLIENT_TYPE="+ENTITY_TYPE+"&USER_NAME="
			+USER_NAME+"&RULE_CODE_CODE="+RULE_CODE_CODE+"&RULE_CODE_NAME="+Etf.getChildValue("RULE_CODE_NAME")
			+"&START_DATE="+RcsUtils.turnDateFormat(START_DATE)+"&END_DATE="+RcsUtils.turnDateFormat(END_DATE);
			if(result==0) {
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", sType+"成功");
				Etf.setChildValue("CALLBACKTYPE", "forward");
				Etf.setChildValue("FORWARDURL", url);
			} else if (result == 2) {
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", sType+"成功");
				Etf.setChildValue("CALLBACKTYPE", "forward");
				Etf.setChildValue("FORWARDURL", url);
			} else {
				Atc.error("200002", "系统错误");
				Atc.rollBackWork(null);
			}
		} else if (result == 2) {
			Atc.error("200005", sType+"失败，未找到记录！");
		} else {
			Atc.error("200002", "系统错误");
		}
	}
	
	/**
	 * 查询所有异常交易明细信息_IP类型
	 * @throws Exception
	 */
	@Code("540287")
	public void selectAllDetail_IP() throws Exception {
		String id = Etf.getChildValue("ID");
		//历史交易包含当日和以前的交易汇总
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.* FROM ");
		sql.append("(");
		sql.append("select pri_id,user_code,to_char(to_date(curent_date,'yyyyMMddhh24miss'),'yyyy-MM-dd hh24:mi:ss') as CURENT_DATE,pay_type,pay_type_result,tcode,tcode_result,status,prdnumber,source,reqip,IPCOUNTRY||IPREGION||IPCITY as IPADDRESS from RCS_CURRCONT where curent_date > to_char(sysdate,'yyyyMMdd')||'000000' ");
		sql.append(" union all ");
		sql.append("select pri_id,user_code,to_char(to_date(curent_date,'yyyyMMddhh24miss'),'yyyy-MM-dd hh24:mi:ss') as CURENT_DATE,pay_type,pay_type_result,tcode,tcode_result,status,prdnumber,source,reqip,IPCOUNTRY||IPREGION||IPCITY as IPADDRESS from RCS_CURRCONT_HIS where curent_date <= to_char(sysdate,'yyyyMMdd')||'000000' ");
		sql.append(") a,RCS_EXCEPT_TRAN_DETAIL_INFO b ");
		sql.append(" WHERE a.PRI_ID = b.TRAN_CODE ");
		sql.append(" AND b.ID = "+id);
		sql.append(" order by a.CURENT_DATE desc");
		
		Log.info("打印查询sql语句 ：" + sql);
		if (StringUtils.isEmpty(pageNum)) {
			this.pageNum = "1";
		}

		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC", sql.toString());
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			int TOLCNT  = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			Log.info("开始页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else {
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败");
		}
		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptranMng/exceptRuleDetailList_IP.jsp");
	}
	
	/**
	 * 异常导出
	 * @param logger
	 * @throws Exception
	 */
	@Code("540289")
	public void excelOut(ILog logger) throws Exception {
		String sql =  "select a.id,a.regdt_day,a.rule_code,d.rule_name,a.entity_type,a.user_code,a.comp_code,x.sumA,c.sumc from RCS_EXCEPT_TRAN_INFO a";
		   sql += " left join                                                                                                  ";
		   sql += "(select regdt_day,rule_code  from  RCS_EXCEPT_TRAN_INFO group by regdt_day,rule_code) b                     ";
		   sql += "on a.regdt_day = b.regdt_day and a.rule_code =b.rule_code                                                   ";
		   sql += "left join																								   ";
		   sql += "(select id, count(*) as sumc from RCS_EXCEPT_TRAN_DETAIL_INFO group by id ) c on a.id = c.id                ";
		   sql += "left join                                                                                                   ";
		   sql += "(                                                                                                           ";
		   sql += "    select regdt_day,rule_code,sum(tran_amt)as sumA from                                                    ";
		   sql += "    ( select a.id,a.RULE_CODE,a.REGDT_DAY,b.tran_code,c.tran_amt  from RCS_EXCEPT_TRAN_INFO a               ";
		   sql += "      left join                                                                                             ";
		   sql += "      RCS_EXCEPT_TRAN_DETAIL_INFO b on a.id = b.id                                                          ";
		   sql += "      left join                                                                                             ";
		   sql += "      RCS_TRAN_SERIAL_RECORD c on b.tran_code = c.id order by id                                     ";
		   sql += "    ) m group by  rule_code,regdt_day                                                                       ";
		   sql += ")x on  a.rule_code = x.rule_code and a.regdt_day=x.regdt_day                                                ";
		   sql += "left join RCS_EXCEPT_TRAN_RULE d                                                                            ";
		   sql += "on a.rule_code = d.rule_code where EXCEPT_TRAN_FLAG='"+RcsDefault.EXCEPT_TRAN_FLAG_0+"'                     ";
		   
		   //客户类型
			if (StringUtils.isNotEmpty(ENTITY_TYPE)) {
				sql += " and ENTITY_TYPE='" + this.ENTITY_TYPE + "'";
			}
			//客户编号
			if (StringUtils.isNotEmpty(CLIENT_CODE)) {
				sql += " and USER_CODE like '%" + this.CLIENT_CODE + "%' or COMP_CODE like '%"+this.CLIENT_CODE +"%'";
			}
			//时间范围    
			if (StringUtils.isNotEmpty(START_DATE)) {
				sql += " and regdt_day >= '" + START_DATE.replaceAll("-", "")+ "'";
			}
			//时间范围    END_DATE
			if (StringUtils.isNotEmpty(END_DATE)) {
				sql += " and regdt_day <= '" + END_DATE.replaceAll("-", "") + "'";
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
				sql += "and  a.rule_code in(" + Sql_RULE_CODE_CODE+ ")";
			}
			 
			sql +=" order by a.regdt_day desc,a.rule_code ";
			
			Log.info("打印查询sql语句 ：" + sql.toString());
	
			if (!StringUtils.isNotEmpty(pageNum)) {
				this.pageNum = "1";
			}
			 
			Log.info("打印查询sql语句 ：" + sql);
			int result = Atc.QueryInGroup(sql,"REC", null);
			// 第二步 ： 导出数据
			if (result == 0) {
				List<?> orderList = getOrderList();
				ExcelUtils.addValue("orderList", orderList);
				//文件名
				String fileName = "ExcpTran_" + RcsUtils.getUID()+"_"+System.currentTimeMillis() + ".xls";
				//在对应文件夹下生成文件
				ResourceFile.FtpPut("ExcpTran", fileName);
				//给页面一个连接下载文件
				Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH")+ fileName);
			}	
	}

	private List<ExceptTran> getOrderList() {// 返回这个bean型的list
		List<?> list = Etf.childs("REC");
		Log.info("导出数据条数为 ： " + list.size());
		List<ExceptTran> orderList = new ArrayList<ExceptTran>();
		for (int i = 0; i < list.size(); i++) {
			ExceptTran o = new ExceptTran();
			Element em = (Element) list.get(i);
			//日期
			o.setRegdt_day(RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "REGDT_DAY")));
			//规则编号
			o.setRule_code(RcsUtils.getElementTextByKey(em, "RULE_CODE"));
			//规则名称
			o.setRule_name(RcsUtils.getElementTextByKey(em, "RULE_NAME"));
			//客户类型
			o.setClient_type(RcsUtils.getElementTextByKey(em, "ENTITY_TYPE").equals("0") ? "用户": "商户");
			//客户编号
			String userCode = RcsUtils.getElementTextByKey(em, "USER_CODE");
			String compCode = RcsUtils.getElementTextByKey(em, "COMP_CODE");
			o.setClient_code(RcsUtils.getElementTextByKey(em, "ENTITY_TYPE").equals("0") ? userCode:compCode );
			//总金额
			String sumA = RcsUtils.getElementTextByKey(em, "SUMA")+"";
			o.setSuma(sumA.trim().equals("")?"0.00":RcsUtils.turnAmtF2Y(sumA));
			//总条数
			String sumC = RcsUtils.getElementTextByKey(em, "SUMC")+"";
			o.setSumc(sumC.trim().equals("")?"0":sumC);
			
			if (RcsUtils.getElementTextByKey(em, "EXCEPT_TRAN_FLAG").equals("0")) {
				o.setExcept_tran_flag("可疑");
			} else if (RcsUtils.getElementTextByKey(em, "EXCEPT_TRAN_FLAG").equals("1")) {
				o.setExcept_tran_flag("已释放");
			} else if (RcsUtils.getElementTextByKey(em, "EXCEPT_TRAN_FLAG").equals("2")) {
				o.setExcept_tran_flag("已确认");
			}
			
			o.setUpdate_name(RcsUtils.getElementTextByKey(em, "UPDATE_NAME"));
			orderList.add(o);
		}
		return orderList;
	}
	
	/**
	 * 异常流水表导出
	 * @param logger
	 * @throws Exception
	 */
	@Code("540290")
	public void excelOutTrans() throws Exception {
		String id = Etf.getChildValue("ID");
		String sql = "select b.* from RCS_EXCEPT_TRAN_DETAIL_INFO a ";
               sql +=" left join                                     "; 
               sql +=" RCS_TRAN_SERIAL_RECORD b on  a.tran_code = b.id where a.id='"+id+"'";  
		 
		sql += " ORDER BY  b.id desc ";
		
		Log.info("打印查询sql语句 ：" + sql);
		int result = Atc.QueryInGroup(sql,"REC", null);
		// 第二步 ： 导出数据
		if (result == 0) {
			List<?> orderList = getOrderTransList();
			Log.info("size = "+ orderList);
			ExcelUtils.addValue("orderList", orderList);
			//文件名
			String fileName = "ExcpTran_" + RcsUtils.getUID()+"_"+System.currentTimeMillis() + ".xls";
			//在对应文件夹下生成文件
			ResourceFile.FtpPut("ExcpTranDeal", fileName);
			//给页面一个连接下载文件
			Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH")+ fileName);
		}	
	}

	private List<ExceptTrantail> getOrderTransList() {// 返回这个bean型的list
		List<?> list = Etf.childs("REC");
		Log.info("导出数据条数为 ： " + list.size());
		List<ExceptTrantail> orderList = new ArrayList<ExceptTrantail>();
		for (int i = 0; i < list.size(); i++) {
			ExceptTrantail o = new ExceptTrantail();
			Element em = (Element) list.get(i);
			
			String userCode = RcsUtils.getElementTextByKey(em, "USER_CODE");
			String compCode = RcsUtils.getElementTextByKey(em, "COMP_CODE");
			String userName = RcsUtils.getElementTextByKey(em, "USER_NAME");
			String compName = RcsUtils.getElementTextByKey(em, "COMP_NAME");
			
			//交易ID
			o.setID(RcsUtils.getElementTextByKey(em, "ID"));
			//交易来源
			o.setTRAN_SOURCE(RcsUtils.getElementTextByKey(em, "RAN_SOURCE").equals("00")?"线上":"线下");
			//客户类型
			o.setCLIENT_TYPE(RcsUtils.getElementTextByKey(em, "CLIENT_TYPE").equals("0")?"用户":"商户");
			//客户编号
			o.setUSER_CODE(RcsUtils.getElementTextByKey(em, "CLIENT_TYPE").equals("0")? userCode : compCode);
			//客户名称
			o.setUSER_NAME(RcsUtils.getElementTextByKey(em, "CLIENT_TYPE").equals("0")? userName : compName);
			//交易类型
			o.setTRAN_TYPE(RcsDefault.map_TRAN_TYPE.get(RcsUtils.getElementTextByKey(em, "TRAN_TYPE"))+"");
			//交易方式  
			o.setTRAN_CLIENT(RcsDefault.map_TRAN_CLIENT.get(RcsUtils.getElementTextByKey(em, "TRAN_CLIENT"))+"");
			//交易登记日期
			o.setREGDT_DAY(RcsUtils.getElementTextByKey(em, "REGDT_DAY"));
			orderList.add(o);
		}
		return orderList;
	}
	
	
	
	@Code("confirmAtion")
	  public void confirmAtion() throws Exception
	  {
		  String sql = "select SEQ_ID AS SEQ_ID_ENT,ID AS ID_ENT,USER_CODE AS USER_CODE_ENT,"
		  		       + "EXCEPT_TRAN_FLAG AS EXCEPT_TRAN_FLAG_ENT,"
		  		       + "to_char(to_date(CONFIRM_TIME,'yyyyMMdd hh24miss'),'yyyy-MM-dd hh24:mi:ss') AS CONFIRM_TIME_ENT,"
		  		       + "CONFIRM_INF AS CONFIRM_INF_ENT,FILE1,FILE2"
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
		    Msg.set("_REQUESTATTR.FORWARDURL", "/WEB-INF/html/excptranMng/basisPage.jsp");
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
