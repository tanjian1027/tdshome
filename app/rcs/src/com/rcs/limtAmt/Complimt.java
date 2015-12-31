package com.rcs.limtAmt;

import java.util.ArrayList;
import java.util.List;

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
import com.bean.RcsCompLimit;
import com.resource.ResourceFile;
import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.SqlUtil;
import com.util.RcsUtils;

@tangdi.engine.DB
//商户结算限额设置 540860~~540879
public class Complimt {
	/**
	 * 1 交易限额  2 充值限额 3 结算限额
	 */
	@Data
	String LIMIT_USER_TYPE;
	/**
	 * 商户状态类型
	 */
	@Data
	String LIMIT_TYPE;
	/**
	 * 业务类型（用于查询此业务类型对应的所有业务类型）
	 */
	@Data
	String LIMIT_BUS_CODE;
	//支付方式
	@Data
	String LIMIT_BUS_CLIENT;
	/**
	 * 指定商户号
	 */
	@Data
	String LIMIT_COMP_CODE;
	/**
	 * LIMIT_USER_CODE_TYPE 0:所有用户   1：指定用户
	 */
	@Data
	String LIMIT_USER_CODE_TYPE;
	/**
	 * 1 为交易类型    2 为充值类型  3 为结算类型
	 */
	@Data
	String LIMIT_COMP_TYPE;
	/**
	 * 业务类型(支付类型) 编码 
	 */
	@Data
	String DICT_CODE;
	/**
	 * 业务类型（支付名称） 名称
	 */
	@Data
	String DICT_NAME;
	/**
	 * 所属业务类型
	 */
	@Data
	String PARENT_CODE;
	/**
	 * 所有客户 1 所有，2指定
	 */
	@Data
	String All_FLAG;
	/**
	 * 运营商编码
	 */
	@Data
	String COMP_CODE;
	/**
	 * 名单状态
	 */
	@Data
	String LIMIT_RISK_FLAG;
	/**
	 * 运营商编码
	 */
	@Data
	String COMP_NAME;
	
	/**
	 * 运营商等级
	 */
	@Data
	String COMP_LEVEL;
	/**
	 * 运营商该等级限额
	 */
	@Data
	String DAY_AMT;
	/**
	 * 限额表ID
	 */
	@Data
	String ID;
	/**
	 * 商户编码
	 */
	@Data
	String LIMIT_USER_CODE;
	/**
	 * 限额类型  1交易限额  2充值限额
	 */
	@Data
	String LIMIT_COMP_TYEP;
	/**
	 * 使用期限  永久 0，时间点 ：1
	 */
	@Data
	String LIMIT_START_TYPE;
	/**
	 * 开始日期
	 */
	@Data
	String LIMIT_START_DATE;
	/**
	 * 结束日期
	 */
	@Data
	String LIMIT_END_DATE;
	/**
	 *   单笔最小金额
	 */
	@Data
	String LIMIT_MIN_AMT;
	/**
	 *   单笔最大金额 
	 */
	@Data
	String LIMIT_MAX_AMT;
	/**
	 *   每日消费次数 
	 */
	@Data
	String LIMIT_DAY_TIMES;
	/**
	 *    每日消费限额 
	 */
	@Data
	String LIMIT_DAY_AMT;
	/**
	 *    每月消费次数 
	 */
	@Data
	String LIMIT_MONTH_TIMES;
	/**
	 *    每月消费限额
	 */
	@Data
	String LIMIT_MONTH_AMT;
	/**
	 *    每年消费次数 
	 */
	@Data
	String LIMIT_YEAR_TIMES;
	/**
	 *    每年消费限额
	 */
	@Data
	String LIMIT_YEAR_AMT;
	/**
	 * 是否可用
	 */
	@Data
	String IS_USE;
	/**
	 * 备注
	 */
	@Data
	String LIMIT_ADDINFO;
	/**
	 * 当前页数
	 */
	@Data
	private String pageNum;
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
	
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		//brefore
		ic.proceed();
		//after
		GetOwnButton.getOwnButton();
		return null;
	}
	
	@Code("540800")
	public void find() throws Exception {
		//查询条件 启用状态
		String sqlAdd = " ";
		if (StringUtils.isNotEmpty(this.IS_USE)) {
			sqlAdd += "and IS_USE='" + this.IS_USE + "'";
		}
		
		//查询条件 时间范围
		String sDate ="";
		String eDate ="";
		if(StringUtils.isNotEmpty(LIMIT_START_DATE)){
			sDate = LIMIT_START_DATE.replaceAll("-", "");
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				if(Integer.parseInt(eDate)>=Integer.parseInt(sDate)) {
					sqlAdd += " and (" +
							"(limit_start_date>='"+ sDate+"' and limit_start_date<='"+eDate+"')"+ 
						    " or (limit_start_date<'"+ sDate +"' and limit_end_date>='"+ sDate+"')" +
						    ")";
				} else {
					sqlAdd += " and limit_start_date>limit_end_date ";
				}
			} else {
				sqlAdd += " and (limit_start_date>='"+ sDate+"' or limit_end_date >= '"+ sDate+"')";
			}
		} else {
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				sqlAdd +=" and (limit_end_date <= '"+ eDate+"' or limit_start_date<='"+ eDate+"')";				
			}
		}
		
	   String sql  ="select a.* from RCS_COMP_LIMIT a  WHERE 1=1 "
                    + sqlAdd+" order by a.update_datetime ";
		Log.info("打印查询sql语句 ：" + sql);
		
		if (!StringUtils.isNotEmpty(pageNum)) {
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
			if (result == 2) {
				Atc.error("P0003", "供货商没有符合条件的记录");
			}
			if (result == 1) {
				Atc.error("P0002", "查询失败");
			} else {
				Atc.error("P0004", "系统错误");
			}
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/complimt.jsp");
	}
	
	//查询商户明细信息
	@Code("540801")
	public void findCompByID() throws Exception {
		Log.info("ID = " + Etf.getChildValue("ID"));
		String sql =  "select a.* from rcs_comp_limit a where id='"+Etf.getChildValue("ID")+"'";
		
		int re = Atc.ReadRecord(sql);
		if (re == 0) {
			
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else {
			if (re == 2) {
				Atc.error("P0003", "没有符合条件的记录");
			}
			else {
				Atc.error("P0004", "系统错误");
			}
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/limtAmt/complimitDetail.jsp");
	}
	
	
	  @Code("540861")
	  @tangdi.interceptor.Log
	  public void del(ILog logger) throws Exception { tangdi.engine.context.Log.info("==========ID" + this.ID, new Object[0]);
	    tangdi.engine.context.Log.info("==========COMP_CODE" + this.COMP_CODE, new Object[0]);

	    String delsql = "delete from   RCS_COMP_LIMIT where id = '" + this.ID + "'";
	    int result = Atc.ExecSql(null, delsql);

	    int result2 = Atc.ExecSql(null, "delete from RCS_COMP_LIMIT_PARAM where comp_code='" + this.COMP_CODE + "'");
	    tangdi.engine.context.Log.info("删除附属参数表：" + result2, new Object[0]);

	    if (result == 0) {
	      tangdi.engine.context.Log.info("xxxxxxxxxxxxxxxxxxxxxx", new Object[0]);
	      Etf.setChildValue("RspCod", "000000");
	      Etf.setChildValue("RspMsg", "删除成功");
	      Etf.setChildValue("CALLBACKTYPE", "forward");
	      Etf.setChildValue("FORWARDURL", "540800.tran");
	      Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	      return;
	    }if (result == 2)
	      Atc.error("P0006", "删除失败");
	    else
	      Atc.error("P0007", "系统错误");
	  }
	
	
	
	
	
	
	@Code("540862")
	public void upd(ILog logger) throws Exception {
		Log.info("ID" + this.ID);
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/complimt.jsp");
	}
	
	@Code("540863")
	//查数据字典商户业务类型
	public void add(ILog logger) throws Exception { 
		//查询业务类型 运营管理系统的业务类型
		String addsql = "select SEQ_NO as DICT_CODE,NAME as DICT_NAME from BizType order by  DICT_CODE ";
		int result = Atc.PagedQuery("1", "999", "REC", addsql);

		Log.info("查询客户x名单表所有数据 ……");

		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else {
			if (result == 2) {
				Atc.error("P0003", "供货商没有符合条件的记录");
			}
			if (result == 1) {
				Atc.error("P0002", "查询失败");
			} else {
				Atc.error("P0004", "系统出错");
			}
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/addcompLimit.jsp");
	}
	
	@Code("540864")
	//新增加限额规则
	public void toadd2(ILog logger) throws Exception {
		Log.info("商户状态类型：" + this.LIMIT_TYPE);
		Log.info("名单状态：" + this.LIMIT_RISK_FLAG);
		Log.info("指定客户号：" + this.LIMIT_COMP_CODE);
		Log.info("使用期限（时间）：" + this.LIMIT_START_TYPE);
		Log.info("开始time ：" + this.LIMIT_START_DATE);
		Log.info("结束time ：" + this.LIMIT_END_DATE);
		Log.info("单笔最小金额：" + this.LIMIT_MIN_AMT);
		Log.info("单笔最大金额：" + this.LIMIT_MAX_AMT);
		Log.info("每月消费次数：" + this.LIMIT_MONTH_TIMES);
		Log.info("每月消费限额：" + this.LIMIT_MONTH_AMT);
		Log.info("每年消费次数：" + this.LIMIT_YEAR_TIMES);
		Log.info("每年消费限额：" + this.LIMIT_YEAR_AMT);
        Log.info("修改人 ：" +  Etf.getChildValue("SESSIONS.USERNAME1"));
    	Log.info("业务类型：" + this.DICT_CODE);
		Log.info("支付方式：" + this.PARENT_CODE);
		Log.info("日累计交易次数上限：" + this.LIMIT_DAY_TIMES);
		Log.info("日累计交易限额上限：" + this.LIMIT_DAY_AMT);
		Log.info("运营商编码：" + this.COMP_CODE);
		Log.info("运营商全称 ：" + this.COMP_NAME);
		Log.info("运营商等级：" + this.COMP_LEVEL);
		Log.info("运营商等级限额：" + this.DAY_AMT);
		
		//检查SESSION是否失效
		if (null == Etf.getChildValue("SESSIONS.USERNAME1")) {
			Atc.error("P0010", "SESSION已失效，不能操作！");
			Etf.setChildValue("RspCode"        , "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG"    , "SESSION已失效，不能操作！");
			return;
		}
		
		//时间转换成数据库插入格式
		String date1 = DateUtil.formmat10to8(this.LIMIT_START_DATE);
		String date2 = DateUtil.formmat10to8(this.LIMIT_END_DATE);
		Log.info("开始time ：" + date1);
		Log.info("结束time ：" + date2);
		if (this.LIMIT_START_TYPE.equals("0")) {//0代表永久
			date1 = "19700101";
			date2 = "29991231";
		}
		Log.info("通过验证 检查是否已经设置限额…………" );
		
		//判断同种商户类别同种名单状态同种支付方式的限额是否存在
		String sql1  = "select * from RCS_COMP_LIMIT ";
			   sql1 += " where LIMIT_RISK_FLAG = "+this.LIMIT_RISK_FLAG;
			   sql1 += " and LIMIT_BUS_CLIENT  = "+this.PARENT_CODE;
		
		//LIMIT_TYPE 1全部 3指定
		if("3".equals(this.LIMIT_TYPE)){
			sql1 += " and LIMIT_TYPE = '3' ";
			sql1 += " and LIMIT_COMP_CODE = "+this.LIMIT_COMP_CODE;
		}else if("1".equals(this.LIMIT_TYPE)){
			sql1 += " and LIMIT_TYPE = '1' ";
		}
		
		//时间段是否重叠
		if(this.LIMIT_START_TYPE.equals("0")){//0代表永久
			sql1 += " and LIMIT_START_DATE = "+date1;
			sql1 += " and LIMIT_END_DATE   = "+date2;
		}else{
			sql1 += " and LIMIT_START_DATE != '19700101'";
			sql1 += " and NOT('"+date2+"' < LIMIT_START_DATE or '"+date1+"' > LIMIT_END_DATE)";
		}
		
		int resu = Atc.ReadRecord(sql1);
		if (resu == 0) {
			Atc.error("P0010", "此种限额已经存在");
			Etf.setChildValue("RspCode", "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG", "抱歉！此种限额已经存在！");
			return;
		} else if (resu == 2){
			//未查到冲突设置-->正常情况
		}else{
			Atc.error("P0011", "系统错误！");
			Etf.setChildValue("RspCode", "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG", "系统错误！");
			return;
		}
		
		Log.info("通过验证，没有冲突的设置！" );
		//插入主表
		String addsql1 = "insert into RCS_COMP_LIMIT(id,limit_type,LIMIT_RISK_FLAG,limit_comp_code,limit_min_amt,limit_max_amt,"
				+ "limit_day_times,limit_day_amt,limit_month_times,limit_month_amt,limit_year_times,limit_year_amt,limit_start_date," 
				+ "limit_end_date,create_name,create_date,create_datetime,update_name,update_date," +
				"update_datetime,is_use,limit_addinfo,limit_bus_code,limit_bus_client,comp_code,comp_name) values("
				+ SqlUtil.SEQUENCES_ID_ADD1 + ",'"
				+ this.LIMIT_TYPE.trim()+"','"
				+this.LIMIT_RISK_FLAG.trim()+"','"
				+ (this.LIMIT_COMP_CODE== null?"":this.LIMIT_COMP_CODE)+"','"
				+ (RcsUtils.turnAmtY2F(this.LIMIT_MIN_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_MIN_AMT))+ "','"//单笔最小限额
				+ (RcsUtils.turnAmtY2F(this.LIMIT_MAX_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_MAX_AMT)) + "','"//单笔最大限额
				+ (this.LIMIT_DAY_TIMES== null?"":this.LIMIT_DAY_TIMES) + "','"//每日次数
				+ (RcsUtils.turnAmtY2F(this.LIMIT_DAY_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_DAY_AMT))+ "','"//每日限额
				+ (this.LIMIT_MONTH_TIMES== null?"":this.LIMIT_MONTH_TIMES )+ "','"//每月次数
				+ (RcsUtils.turnAmtY2F(this.LIMIT_MONTH_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_MONTH_AMT))+ "','"//每月限额
				+ (this.LIMIT_YEAR_TIMES== null?"":this.LIMIT_YEAR_TIMES )+ "','"//每年次数
				+ (RcsUtils.turnAmtY2F(this.LIMIT_YEAR_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_YEAR_AMT))+ "','"//每年限额
				+ date1 + "','" //开始日期
				+ date2 + "','" //结束日期
				+ Etf.getChildValue("SESSIONS.USERNAME1")+"','"
				+ DateUtil.getCurrentDate() + "','"
				+ DateUtil.getCurrentDateTime() + "','" // 创建
			    + Etf.getChildValue("SESSIONS.USERNAME1")+"','"
				+ DateUtil.getCurrentDate() + "','"
				+ DateUtil.getCurrentDateTime() + "','" // 维护
			    + this.IS_USE.trim()+ "','"//是否可用
				+ (this.LIMIT_ADDINFO == null?"":this.LIMIT_ADDINFO)+ "','"
				+ (this.DICT_CODE == null?"":this.DICT_CODE)+ "','"//业务编码
				+ (this.PARENT_CODE== null?"":this.PARENT_CODE) + "','"
		        + (this.COMP_CODE==null?"":this.COMP_CODE) +"','"
				+ RcsUtils.getCompName(this.COMP_CODE) +"' )";
		
		Log.info("打印sql语句 ： " + addsql1);

		//运营商是未启用状态 或主表没存在记录时才插入主表
		if(!this.IS_USE.trim().equals("1") || this.exiteCompIsUse()== false){
			int result1 = Atc.ExecSql(null, addsql1);
			if (result1 !=0 ) {
				Atc.error("P0003", "添加失败！");
				return;
			}else{
				Etf.setChildValue("MsgTyp", "N");
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", "添加成功");
				Etf.setChildValue("FORWARDURL", "540800.tran");
				Etf.setChildValue("CALLBACKTYPE","closeCurrent");
			}
		}else{
			Log.info("主表已存在设置 不做插入动作……");
		}
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}
	
	@Code("540866")
	//跳转到修改页面  并查处所有业务类型
	public void up_(ILog logger) throws Exception {
		Log.info("ID = " + this.ID);
		Log.info("运营商编码 ：" + this.COMP_CODE);
		Log.info("业务类型：", this.LIMIT_BUS_CODE);
		Log.info("支付方式：", this.LIMIT_BUS_CLIENT);
		String seSql = "select a.* from RCS_COMP_LIMIT a "
				     +"  where id='"+this.ID.trim()+"'";
		
		int resu = Atc.ReadRecord(seSql);
		if (resu == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else {
			if (resu == -2) {
				Atc.error("P0001", "修改失败");
				return;
			}
			if (resu == -1) {
				Atc.error("P0008", "系统错误");
				return;
			}
		}		
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/upcompLimit.jsp");
	}

	@Code("540867")
	//查数据字典 对应业务类型的支付方式
	public void up_data(ILog logger) throws Exception {
		Log.info("ID= " + this.ID);

		String seSql = "select * from RCS_COMP_LIMIT where id= '" + this.ID
				+ "'";

		int resu = Atc.ExecSql(null, seSql);

		if (resu == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			String count = Etf.getChildValue("NMB");
			int count1 = Integer.parseInt(count);
			if (count1 >= 1) {
				Atc.error("P0003", "已经存在该分类名称");
				Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
				return;
			}
		} else {
			if (resu == -2) {
				Atc.error("P0001", "修改失败");
				return;
			}
			if (resu == -1) {
				Atc.error("P0008", "系统错误");
				return;
			}
		}

		String sql = "";

		int result = Atc.ExecSql(null, sql.toString());

		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "修改成功");
			Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
			Etf.setChildValue("FORWARDURL", "466004.tran");
		} else if (result == 2) {
			Atc.error("P0010", "修改失败");
		} else if (result == 3) {
			Atc.error("P0003", "违反数据唯一性");
		} else {
			Atc.error("P0011", "系统错误");
		}
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}

	@Code("540869")
	//查找所编辑字段的业务类型所对应的所有支付方式
	public void update(ILog logger) throws Exception {
		Log.info("使用期限（时间）：" + this.LIMIT_START_TYPE);
		Log.info("开始time ：" + this.LIMIT_START_DATE);
		Log.info("结束time ：" + this.LIMIT_END_DATE);
		Log.info("单笔最小金额：" + this.LIMIT_MIN_AMT);
		Log.info("单笔最大金额：" + this.LIMIT_MAX_AMT);
		Log.info("每月消费次数：" + this.LIMIT_MONTH_TIMES);
		Log.info("每月消费限额：" + this.LIMIT_MONTH_AMT);
		Log.info("每年消费次数：" + this.LIMIT_YEAR_TIMES);
		Log.info("每年消费限额：" + this.LIMIT_YEAR_AMT);
		Log.info("修改人 ：" +  Etf.getChildValue("SESSIONS.USERNAME1"));
    	Log.info("业务类型：" + this.DICT_CODE);
		Log.info("支付方式：" + this.PARENT_CODE);
		Log.info("日累计交易次数上限：" + this.LIMIT_DAY_TIMES);
		Log.info("日累计交易限额上限：" + this.LIMIT_DAY_AMT);
		Log.info("运营商编码：" + this.COMP_CODE);
		Log.info("运营商全称 ：" + this.COMP_NAME);
		Log.info("运营商等级：" + this.COMP_LEVEL);
		Log.info("运营商等级限额：" + this.DAY_AMT);
		
		//检查SESSION是否失效
		if (null == Etf.getChildValue("SESSIONS.USERNAME1")) {
			Atc.error("P0010", "SESSION已失效，不能操作！");
			Etf.setChildValue("RspCode"        , "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG"    , "SESSION已失效，不能操作！");
			return;
		}
		
		String date1 = DateUtil.formmat10to8(this.LIMIT_START_DATE);
		String date2 = DateUtil.formmat10to8(this.LIMIT_END_DATE);

		Log.info("开始time ：" + date1);
		Log.info("结束time ：" + date2);

		if (this.LIMIT_START_TYPE.equals("0")) {//0代表永久
			date1 = "19700101";
			date2 = "29991231";
		}
		
		String addsql = "update RCS_COMP_LIMIT  set " + 
				"limit_min_amt='" + (RcsUtils.turnAmtY2F(LIMIT_MIN_AMT)==null?"":RcsUtils.turnAmtY2F(LIMIT_MIN_AMT))+ //单笔最小限额                           
				"',limit_max_amt='" + (RcsUtils.turnAmtY2F(this.LIMIT_MAX_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_MAX_AMT))+   //单笔最大限额                         
				"',limit_day_times='" + (this.LIMIT_DAY_TIMES==null?"":this.LIMIT_DAY_TIMES)+   //每日次数                         
				"',limit_day_amt='" + (RcsUtils.turnAmtY2F(this.LIMIT_DAY_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_DAY_AMT))+   //每日限额                       
				"',limit_month_times='" + (this.LIMIT_MONTH_TIMES==null?"":this.LIMIT_MONTH_TIMES)+   //每月次数                         
				"',limit_month_amt='" + (RcsUtils.turnAmtY2F(this.LIMIT_MONTH_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_MONTH_AMT))+  //每月限额                       
				"',limit_year_times='" + (this.LIMIT_YEAR_TIMES==null?"":this.LIMIT_YEAR_TIMES)+   //每年次数                         
				"',limit_year_amt='" + (RcsUtils.turnAmtY2F(this.LIMIT_YEAR_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_YEAR_AMT))+  //每年限额
				"',LIMIT_start_date='" + date1+   //开始日期                         
				"',limit_end_date='" + date2+ //结束日期                           
				"',update_name='" +  Etf.getChildValue("SESSIONS.USERNAME1")  +"'," + 
				"update_date='" + DateUtil.getCurrentDate()+"',"+
				"update_datetime='" + DateUtil.getCurrentDateTime()+"'," +
				"limit_addinfo='" + (this.LIMIT_ADDINFO==null?"":this.LIMIT_ADDINFO) +"'," + 
				"is_use='"+this.IS_USE+"'"+
				" where ID='" + this.ID + "'";		

		logger.info("执行的sql语句是=%s", new Object[] { addsql.toString() });
		int result = Atc.ExecSql(null, addsql);
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "修改成功");
			Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
			Etf.setChildValue("FORWARDURL", "540800.tran");
		} else if (result == 2) {
			Atc.error("P0003", "添加失败");
		} else if (result == 3) {
			Atc.error("P0002", "查询失败");
		} else {
			Atc.error("200002", "系统错误");
		}
		
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}

	/**
	 * 导出
	 * @param logger
	 * @throws Exception
	 */
	
	@Code("540819")
	public void excelOut(ILog logger) throws Exception {
		//查询条件 启用状态
		String sqlAdd = " ";
		if (StringUtils.isNotEmpty(this.IS_USE)) {
			sqlAdd += "and IS_USE='" + this.IS_USE + "'";
		}
		//查询条件 时间范围
		String sDate ="";
		String eDate ="";
		if(StringUtils.isNotEmpty(LIMIT_START_DATE)){
			sDate = LIMIT_START_DATE.replaceAll("-", "");
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				if(Integer.parseInt(eDate)>=Integer.parseInt(sDate)) {
					sqlAdd += " and (" +
							"(limit_start_date>='"+ sDate+"' and limit_start_date<='"+eDate+"')"+ 
						    " or (limit_start_date<'"+ sDate +"' and limit_end_date>='"+ sDate+"')" +
						    ")";
				} else {
					sqlAdd += " and limit_start_date>limit_end_date ";
				}
			} else {
				sqlAdd += " and (limit_start_date>='"+ sDate+"' or limit_end_date >= '"+ sDate+"')";
			}
		} else {
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				sqlAdd +=" and (limit_end_date <= '"+ eDate+"' or limit_start_date<='"+ eDate+"')";				
			}
		}
		
	   String sql  ="select a.* from RCS_COMP_LIMIT a WHERE 1=1 "+sqlAdd;

		Log.info("打印查询sql语句 ：" + sql);
		int result =  Atc.QueryInGroup(sql, "REC", null);
		// 第二步 ： 导出数据
			if(result == 0){
				List orderList = getOrderList();
				ExcelUtils.addValue("orderList", orderList);
				String fileName = "LimitCompAmt_" + RcsUtils.getUID()+"_"+System.currentTimeMillis() + ".xls";//增加登录用户id 防止同时下载文件被覆盖
				ResourceFile.FtpPut("LimitCompAmt", fileName);
				Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH") + fileName);//给页面一个连接下载文件
			}
	}
	
	private List<RcsCompLimit> getOrderList() {// 返回这个bean型的list
		List list = Etf.childs("REC");
		List orderList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			RcsCompLimit o = new RcsCompLimit();
			Element em = (Element) list.get(i);
			//客户类型
		    o.setComptype(RcsUtils.getElementTextByKey(em, "LIMIT_TYPE").equals("0")?"所有商户":"指定商户");
		    //支付账号
		    o.setLimitusercode(RcsUtils.getElementTextByKey(em, "LIMIT_COMP_CODE"));
		    //生效时间
		    o.setLimitstartdate(RcsUtils.getElementTextByKey(em, "LIMIT_START_DATE").equals("19700101") ? "永久" : RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "LIMIT_START_DATE")));
		    //失效时间
		    o.setLimitenddate( RcsUtils.getElementTextByKey(em, "LIMIT_END_DATE").equals("29991231") ? "永久" : RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "LIMIT_END_DATE")));
		    
		    //支付方式
		    o.setLimitbusclient(Userlimt.getLIMIT_BUS_CLIENT(RcsUtils.getElementTextByKey(em, "LIMIT_BUS_CLIENT")));
		    //当天消费金额
		    o.setLimitdayamt(RcsUtils.getElementTextByKey(em, "LIMIT_DAY_AMT"));
		    
		    o.setCreatename(RcsUtils.getElementTextByKey(em, "CREATE_NAME"));
		    o.setCreatedate(RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "CREATE_DATE")));
		    o.setUpdatename(RcsUtils.getElementTextByKey(em, "UPDATE_NAME"));
		    o.setUpdatedate(RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "UPDATE_DATE")));
		    //启用状态
		    o.setIsuse(RcsUtils.getElementTextByKey(em, "IS_USE").equals("1")?"启用" :"未启用");
		    //状态
		    o.setLimitRiskFlag(RcsUtils.getUserRiskFlag(RcsUtils.getElementTextByKey(em, "LIMIT_RISK_FLAG")));
			orderList.add(o);
		}
		return orderList;
	}
	
	/**
	 * 是否存在已经启用的该商户信息
	 * 存在返回true
	 * 不存在返回false
	 * @return
	 */
	private boolean exiteCompIsUse() {
		String sql = "select * from rcs_comp_limit where is_use='1' and comp_code='"+ this.COMP_CODE + "'";
		int result = Atc.ReadRecord(sql);
		if(result ==2){
			return false;
		}else{
			return true;
		}
	}
}
