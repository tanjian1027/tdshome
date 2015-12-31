package com.rcs.limtAmt;

import java.util.ArrayList;
import java.util.List;
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
import tangdi.web.TdWebConstants;

import com.bean.RcsUserLimit;
import com.resource.ResourceFile;
import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.SqlUtil;
import com.util.RcsUtils;

@tangdi.engine.DB
public class Userlimt {
	/**
	 * 业务类型（用于查询此业务类型对应的所有业务类型）
	 */
	@Data
	String LIMIT_BUS_CODE;
	/**
	 * 支付方式
	 */
	@Data
	String LIMIT_BUS_CLIENT;
	/**
	 * 业务类型(支付类型) 编码  ，0 表示忽略支付方式
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
	//****begin_liuyanwei_20130402_add_用户限额添加-增加交易类型*********
	@Data
	String TRAN_TYPE;
	/**
	 * 所属交易类型
	 */
	//****begin_liuyanwei_20130402_add_用户限额添加-增加交易类型*********
	@Data
	String PARENT_CODE;
	/**
	 * 1:实名，2:非实名，3指定用户
	 */
	@Data
	String LIMIT_USER_TYPE;
	/**
	 * 名单状态
	 */
	@Data
	String LIMIT_RISK_FLAG;
	/**
	 * 限额表ID
	 */
	@Data
	String ID;
	/**
	 * 用户编码
	 */
	@Data
	String LIMIT_USER_CODE;
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
	 *    用户余额限额
	 */
	@Data
	String LIMIT_BALANCE_AMT;
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
	 * 卡号
	 */
	@Data
	private String CARD_CODE;
	/**
	 * 运营商CODE 
	 */
	@Data
	private String COMP_CODE;
	/**
	 * 用户单日最大使用的（不同）卡数
	 */
	@Data
	private String MAX_CARD_NUM;
	/**
	 * 不允许的金额格式(为整数时才做控制）
	 */
	@Data
	private String AMT_PATTERN_LIMIT;
	
	/**
	 * 信用卡单卡单笔最高限额
	 */
	@Data
	private String XY_CARD_ONCE_AMT;
	/**
	 * 借记卡单卡单笔最高限额
	 */
	@Data
	private String JJ_CARD_ONCE_AMT;
	/**
	 * 信用卡单卡日累计最高限额
	 */
	@Data
	private String XY_CARD_DAY_AMT;
	/**
	 * 借记卡单卡日累计最高限额
	 */
	@Data
	private String JJ_CARD_DAY_AMT;
	/**
	 * 信用卡两次卡（成功）交易时间间隔（单位秒）
	 */
	@Data
	private String XY_CARD_TXN_INTERVAL;
	
	/**
	 * 借记卡两次卡（成功）交易时间间隔（单位秒）
	 */
	@Data
	private String JJ_CARD_TXN_INTERVAL;
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
	
	@Code("599999")
	public void findTest() throws Exception {
		Msg.dump();
		Log.info("跑批打印……");
	}
	
	@Code("540240")
	public void find() throws Exception {
		//查询条件 “启用状态”
		String sqlAdd="";
		if(StringUtils.isNotEmpty(IS_USE)){
			sqlAdd += "and   IS_USE='" + this.IS_USE + "'";
		}
		//查询条件 “时间范围”
		String sDate ="";
		String eDate ="";
		if(StringUtils.isNotEmpty(LIMIT_START_DATE)){
			sDate = LIMIT_START_DATE.replaceAll("-", "");
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				if(Integer.valueOf(sDate) > Integer.valueOf(eDate)){
					//若查询开始时间大于结束时间,则不去查询数据直接返回
					Atc.error("P0003", "没有符合条件的记录");
					Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/userlimt.jsp");
					return;
				}
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
		//查询条件 “运营商”
		if(StringUtils.isNotEmpty(this.COMP_CODE)){
			sqlAdd += "and  COMP_CODE='" + this.COMP_CODE + "'";
		}
		
		Log.info("sqlAdd is : "+sqlAdd);
		//****begin_liuyanwei_20130402_updae_用户限额列表查询-增加交易类型********* 
		String sql = "select a.*  "
                +" from   RCS_USER_LIMIT a  where 1=1" + sqlAdd+" order by a.UPDATE_DATETIME DESC ";
		//****begin_liuyanwei_20130402_update_用户限额列表查询-增加交易类型*********
		Log.info("打印查询sql语句：" + sql );
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}		
		
		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
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
				Atc.error("P0003", "没有符合条件的记录");
			}
			if (result == 1) {
				Atc.error("P0002", "查询失败");
			} else {
				Atc.error("P0004", "系统错误");
			}
		}
		Msg.dump();
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/userlimt.jsp");
	}

	@Code("540241")
	public void del() throws Exception {
		Log.info("==========ID" + this.ID);
		String delsql = "delete  from RCS_USER_LIMIT  where id = '" + this.ID + "'";
		int result = Atc.ExecSql(null, delsql);
		if (result == 0) {
			Log.info("xxxxxxxxxxxxxxxxxxxxxx");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "删除成功");
			Etf.setChildValue("CALLBACKTYPE", "forward");
			Etf.setChildValue("FORWARDURL", "540240.tran");
			Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
			return;
		} else {
			Atc.error("P0007", "系统错误");
		}
	}

	@Code("540242")
	public void upd() throws Exception {
		Log.info("ID = "+Etf.getChildValue("ID"));
		String sql = "select a.* "
		    +" from  RCS_USER_LIMIT a where ID='"+Etf.getChildValue("ID")+"'";
		int re = Atc.ReadRecord(sql);
		
		if (re == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else {
			if (re == 2) {
				Atc.error("P0003", "没有符合条件的记录");
			}else {
				Atc.error("P0004", "系统错误");
			}
		}
		
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/limtAmt/userlimitDetail.jsp");
	}

	@Code("540243")
	public void add() throws Exception {
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/addUserLimit.jsp");
	}

	@Code("540244")
	public void toadd2() throws Exception {
		Log.info("用户code：" + this.LIMIT_USER_CODE);
		Log.info("用户状态：" + this.LIMIT_USER_TYPE);
		Log.info("名单状态：" + this.LIMIT_RISK_FLAG);
		Log.info("单笔最小金额：" + this.LIMIT_MIN_AMT);
		Log.info("单笔最大金额：" + this.LIMIT_MAX_AMT);
		Log.info("每日消费次数：" + this.LIMIT_DAY_TIMES);
		Log.info("每日消费限额：" + this.LIMIT_DAY_AMT);
		Log.info("每月消费次数：" + this.LIMIT_MONTH_TIMES);
		Log.info("每月消费限额：" + this.LIMIT_MONTH_AMT);
		Log.info("每年消费次数：" + this.LIMIT_YEAR_TIMES);
		Log.info("每年消费限额：" + this.LIMIT_YEAR_AMT);
        Log.info("修改人 ：" +  Etf.getChildValue("SESSIONS.USERNAME1"));
        Log.info("业务类型：" + this.DICT_CODE);
        Log.info("交易类型：" + this.TRAN_TYPE);
        Log.info("支付方式：" + this.PARENT_CODE);
		Log.info("运营商 ：" + this.COMP_CODE);
		Log.info("使用期限（时间）：" + this.LIMIT_START_TYPE);
		Log.info("开始time ：" + this.LIMIT_START_DATE);
		Log.info("结束time ：" +  this.LIMIT_END_DATE);
		Log.info("单用户日累计交易限额：" + this.LIMIT_DAY_AMT);
		Log.info("不允许金额格式：" + this.AMT_PATTERN_LIMIT);
		Log.info("单用户单日最多用卡数：" + this.MAX_CARD_NUM);
		Log.info("信用卡交易间隔时间不小于：" + this.XY_CARD_TXN_INTERVAL);
		Log.info("借记卡交易间隔时间不小于：" + this.JJ_CARD_TXN_INTERVAL);
		Log.info("信用卡单笔最大金额：" + this.XY_CARD_ONCE_AMT);
		Log.info("借记卡单笔最大金额：" + this.JJ_CARD_ONCE_AMT);
		Log.info("信用卡日累计最高金额：" + this.XY_CARD_DAY_AMT);
		Log.info("借记卡日累计最高金额：" + this.JJ_CARD_DAY_AMT);
		Log.info("用户余额限额：：" + this.LIMIT_BALANCE_AMT);
		Log.info("启用标志：" + this.IS_USE);
		Log.info("备注：" + this.LIMIT_ADDINFO);
		Log.info("运营商名称 ："+ RcsUtils.getCompName(this.COMP_CODE));
				
		//生效时间设定，若为空时设为永久
	    String date1 = DateUtil.formmat10to8(this.LIMIT_START_DATE);
		String date2 = DateUtil.formmat10to8(this.LIMIT_END_DATE);
		Log.info("开始time ：" + date1);
		Log.info("结束time ：" + date2);
		Log.info("LIMIT_START_TYPE ：" + LIMIT_START_TYPE);
		if (this.LIMIT_START_TYPE.equals("0")) {//0代表永久
			date1 = "19700101";
			date2 = "29991231";
		}
		//****begin_liuyanwei_20130402_add_用户限额添加-增加交易类型*********
		//判断SESSION是否过期
		if(null == Etf.getChildValue("SESSIONS.USERNAME1")){
			Atc.error("P0009", "SESSION已过期，请重新登录！");
			Etf.setChildValue("RspCode"        , "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG"    , "SESSION已过期，请重新登录！");
			return;
		}
		
		//判断交易类型是否为空
		if(this.TRAN_TYPE.equals("-1")){//未选择交易类型
			Atc.error("P0009", "请选择交易类型");
			Etf.setChildValue("RspCode", "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG", "抱歉！请选择交易类型！");
			return;
		}
		//判断同种用户类别同种交易类型同种支付方式的限额是否存在【实名及非实名】
		String sql1  = "select * from RCS_USER_LIMIT ";
			   sql1 += " where LIMIT_BUS_CLIENT = "+this.PARENT_CODE;
			   sql1 += " and TRAN_TYPE          = "+this.TRAN_TYPE;
			   sql1 += " and LIMIT_RISK_FLAG    = "+this.LIMIT_RISK_FLAG;
		
		if(this.LIMIT_USER_TYPE.equals("3")){//【个人用户】
            sql1 += " and LIMIT_USER_CODE = "+this.LIMIT_USER_CODE;
		}else if(this.LIMIT_USER_TYPE.equals("1")||this.LIMIT_USER_TYPE.equals("2")){
			sql1 += " and LIMIT_USER_TYPE = "+this.LIMIT_USER_TYPE;
		}
		
		//时间段是否重叠
		if(this.LIMIT_START_TYPE.equals("0")){//0代表永久
			sql1 += " and LIMIT_START_DATE = "+date1;
			sql1 += " and LIMIT_END_DATE =   "+date2;
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
		}else{
			Atc.error("P0011", "系统错误！");
			Etf.setChildValue("RspCode", "S0016");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG", "系统错误！");
			return;
		}
		//****  end_liuyanwei_20130402_add_用户限额添加-增加交易类型*********		
		
		String  sql2 = " insert into RCS_USER_LIMIT(id,limit_user_type,LIMIT_RISK_FLAG,LIMIT_USER_CODE,limit_min_amt,limit_max_amt,"
				 + "limit_day_times,limit_day_amt,limit_month_times,limit_month_amt,limit_year_times,limit_year_amt,limit_start_date," 
				 + "limit_end_date,create_name,create_date,create_datetime,update_name,update_date,"
				 + "update_datetime,is_use,limit_addinfo,limit_bus_code,limit_bus_client,max_card_num,amt_pattern_limit,"   //****  end_liuyanwei_20130402_updae_用户限额添加-增加交易类型*********	
				 + "xy_card_once_amt,xy_card_txn_interval,xy_card_day_amt,jj_card_day_amt,jj_card_once_amt,jj_card_txn_interval,comp_code,comp_name,limit_balance_amt,tran_type) values("
				 + SqlUtil.SEQUENCES_ID_ADD1 + ",'"
				 +this.LIMIT_USER_TYPE.trim()+"','"
				 +this.LIMIT_RISK_FLAG.trim()+"','"
			    + (this.LIMIT_USER_CODE ==null?"":this.LIMIT_USER_CODE)+ "','"
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
				+ (this.MAX_CARD_NUM==null?"":this.MAX_CARD_NUM)+"','"
				+ (this.AMT_PATTERN_LIMIT == null?"":this.AMT_PATTERN_LIMIT)+"','"
				+ (RcsUtils.turnAmtY2F(this.XY_CARD_ONCE_AMT))+"','"
				+ this.XY_CARD_TXN_INTERVAL+"','"
				+ (RcsUtils.turnAmtY2F(this.XY_CARD_DAY_AMT))+"','"
				+ (RcsUtils.turnAmtY2F(this.JJ_CARD_DAY_AMT))+"','"
				+ (RcsUtils.turnAmtY2F(this.JJ_CARD_ONCE_AMT))+"','"
				+ this.JJ_CARD_TXN_INTERVAL+"','" 
				+ this.COMP_CODE+"','"
				+ RcsUtils.getCompName(this.COMP_CODE) + "','"
				+ (RcsUtils.turnAmtY2F(this.LIMIT_BALANCE_AMT)== null?"":RcsUtils.turnAmtY2F(this.LIMIT_BALANCE_AMT))+ "','"//充值限额
				//****  end_liuyanwei_20130402_add_用户限额添加-增加交易类型*********	
				+ (this.TRAN_TYPE==null?"":this.TRAN_TYPE)+"' )";//交易类型
		
		Log.info("执行的sql语句是" + sql2);
		int result = Atc.ExecSql(null, sql2);
		Log.info("xxxxxxx");
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "限额规则成功");
			Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
			Etf.setChildValue("FORWARDURL", "540240.tran");
		} else if (result == 2) {
			Atc.error("P0003", "添加失败！");
		} else {
			Atc.error("200002", "系统错误！");
		}
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}

	@Code("540246")
	public void up_() throws Exception {
		Log.info("ID = " + this.ID);
		String seSql = "select a.*,a.tran_type as tran_type "
			         +" from RCS_USER_LIMIT a where id='"+this.ID.trim()+"'";

		int resu = Atc.ReadRecord(seSql);
		if (resu == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else {
			if (resu == -2) {
				Atc.error("P0001", "查询失败");
				return;
			}
			if (resu == -1) {
				Atc.error("P0008", "系统错误");
				return;
			}
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/upUserLimit.jsp");
	}

	@Code("540247")
	public void up_data() throws Exception {
		Log.info("ID = " + this.ID);
		String seSql = "select * from RCS_COMP_LIMIT where id= '" + this.ID+ "'";
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
	
	@Code("540249")
	public void update() throws Exception {
		Log.info("单笔最小金额：" + this.LIMIT_MIN_AMT);
		Log.info("单笔最大金额：" + this.LIMIT_MAX_AMT);
		Log.info("每日消费次数：" + this.LIMIT_DAY_TIMES);
		Log.info("每日消费限额：" + this.LIMIT_DAY_AMT);
		Log.info("每月消费次数：" + this.LIMIT_MONTH_TIMES);
		Log.info("每月消费限额：" + this.LIMIT_MONTH_AMT);
		Log.info("每年消费次数：" + this.LIMIT_YEAR_TIMES);
		Log.info("每年消费限额：" + this.LIMIT_YEAR_AMT);
        Log.info("修改人 ：" +  Etf.getChildValue("SESSIONS.USERNAME1"));
        Log.info("业务类型：" + this.DICT_CODE);
		Log.info("运营商 ：" + this.COMP_CODE);
		Log.info("使用期限（时间）：" + this.LIMIT_START_TYPE);
		Log.info("开始time ：" + this.LIMIT_START_DATE);
		Log.info("结束time ：" +  this.LIMIT_END_DATE);
		Log.info("单用户日累计交易限额：" + this.LIMIT_DAY_AMT);
		Log.info("不允许金额格式：" + this.AMT_PATTERN_LIMIT);
		Log.info("单用户单日最多用卡数：" + this.MAX_CARD_NUM);
		Log.info("信用卡交易间隔时间不小于：" + this.XY_CARD_TXN_INTERVAL);
		Log.info("借记卡交易间隔时间不小于：" + this.JJ_CARD_TXN_INTERVAL);
		Log.info("信用卡单笔最大金额：" + this.XY_CARD_ONCE_AMT);
		Log.info("借记卡单笔最大金额：" + this.JJ_CARD_ONCE_AMT);
		Log.info("信用卡日累计最高金额：" + this.XY_CARD_DAY_AMT);
		Log.info("借记卡日累计最高金额：" + this.JJ_CARD_DAY_AMT);
		Log.info("用户余额限额：：" + this.LIMIT_BALANCE_AMT);
		Log.info("启用标志：" + this.IS_USE);
		Log.info("备注：" + this.LIMIT_ADDINFO);
		
		if(null == Etf.getChildValue("SESSIONS.USERNAME1")){
			Atc.error("P0003", "SESSION失效，不能修改！");
		}else{
			String date1 = DateUtil.formmat10to8(this.LIMIT_START_DATE);
			String date2 = DateUtil.formmat10to8(this.LIMIT_END_DATE);

			Log.info("开始time ：" + date1);
			Log.info("结束time ：" + date2);
			if (this.LIMIT_START_TYPE.equals("0")) {//0代表永久
				date1 = "19700101";
				date2 = "29991231";
			}		
			
			String addsql = "update RCS_USER_LIMIT  set " +
					"limit_min_amt='" + (RcsUtils.turnAmtY2F(this.LIMIT_MIN_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_MIN_AMT))+ //单笔最小限额                           
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
					"is_use='"+this.IS_USE+"',"+
					//"limit_bus_code ='"+ (this.DICT_CODE==null?"":this.DICT_CODE)+ "',"+ //业务编码                                 
					//"limit_bus_client ='"+ (this.LIMIT_BUS_CLIENT==null?"":this.LIMIT_BUS_CLIENT)+ "'," //支付方式 
					"max_card_num='"+ (this.MAX_CARD_NUM==null?"":this.MAX_CARD_NUM)+"',"
					+"amt_pattern_limit='"+ (this.AMT_PATTERN_LIMIT==null?"":this.AMT_PATTERN_LIMIT)+"',"
					+"xy_card_once_amt='"+ ((RcsUtils.turnAmtY2F(this.XY_CARD_ONCE_AMT))==null?"":RcsUtils.turnAmtY2F(this.XY_CARD_ONCE_AMT))+"',"
					+"xy_card_txn_interval='"+ (this.XY_CARD_TXN_INTERVAL==null?"":this.XY_CARD_TXN_INTERVAL) +"',"
					+"xy_card_day_amt='"+ ((RcsUtils.turnAmtY2F(this.XY_CARD_DAY_AMT))==null?"":(RcsUtils.turnAmtY2F(this.XY_CARD_DAY_AMT)))+"',"
					+"jj_card_day_amt='"+ ((RcsUtils.turnAmtY2F(this.JJ_CARD_DAY_AMT))==null?"":(RcsUtils.turnAmtY2F(this.JJ_CARD_DAY_AMT)))+"',"
					+"jj_card_once_amt='"+((RcsUtils.turnAmtY2F(this.JJ_CARD_ONCE_AMT))==null?"":(RcsUtils.turnAmtY2F(this.JJ_CARD_ONCE_AMT)))+"',"
					+"LIMIT_BALANCE_AMT='" + (RcsUtils.turnAmtY2F(this.LIMIT_BALANCE_AMT)==null?"":RcsUtils.turnAmtY2F(this.LIMIT_BALANCE_AMT))+"',"  //
					+"jj_card_txn_interval='"+ (this.JJ_CARD_TXN_INTERVAL==null?"":this.JJ_CARD_TXN_INTERVAL)+"' " 
					+"where ID='" + this.ID + "'";

			Log.info("执行的sql语句是==== "+addsql );
			int result = Atc.ExecSql(null, addsql);

			if (result == 0) {
				Etf.setChildValue("MsgTyp", "N");
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", "修改成功");
				Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
				Etf.setChildValue("FORWARDURL", "540240.tran");
			} else if (result == 2) {
				Atc.error("P0003", "修改失败");
			} else if (result == 3) {
				Atc.error("P0002", "查询失败");
			} else {
				Atc.error("200002", "系统错误");
			}
		}
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}
	
	@Code("540259")
	public void excelOut() throws Exception {
		//查询条件 “启用状态”
		String sqlAdd="";
		if(StringUtils.isNotEmpty(IS_USE)){
			sqlAdd += "and   IS_USE='" + this.IS_USE + "'";
		}
		//查询条件 “时间范围”
		String sDate ="";
		String eDate ="";
		if(StringUtils.isNotEmpty(LIMIT_START_DATE)){
			sDate = LIMIT_START_DATE.replaceAll("-", "");
			if(StringUtils.isNotEmpty(LIMIT_END_DATE)){
				eDate = LIMIT_END_DATE.replaceAll("-", "");
				if(Integer.valueOf(sDate) > Integer.valueOf(eDate)){
					//若查询开始时间大于结束时间,则不去查询数据直接返回
					Atc.error("P0003", "没有符合条件的记录");
					Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/limtAmt/userlimt.jsp");
					return;
				}
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
		//查询条件 “运营商”
		if(StringUtils.isNotEmpty(this.COMP_CODE)){
			sqlAdd += "and  COMP_CODE='" + this.COMP_CODE + "'";
		}
		
		Log.info("sqlAdd is : "+sqlAdd);
		
		String sql = "select a.* from   RCS_USER_LIMIT a where 1=1" + sqlAdd;
		
		Log.info("打印查询sql语句：" + sql );
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		Log.info("打印导出sql语句："+ sql);
		int result =  Atc.QueryInGroup(sql, "REC", null);				
		// 第二步 ： 导出数据
		if(result == 0){				
			List orderList = getOrderList();		
			ExcelUtils.addValue("orderList", orderList);	
			String fileName = "LimitUserAmt_" + RcsUtils.getUID()+"_"+System.currentTimeMillis() + ".xls";//增加登录用户id 防止同时下载文件被覆盖
			ResourceFile.FtpPut("LimitUserAmt", fileName);
			Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH") + fileName);//给页面一个连接下载文件
		}
	}
	
	private List<RcsUserLimit> getOrderList() throws Exception {
		List list = Etf.childs("REC");
		Log.info("导出数据条数为 ： "+ list.size());
		Msg.dump();
		List orderList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			
			RcsUserLimit o = new RcsUserLimit();
			Element em = (Element) list.get(i);
			
			o.setLimitusertype(new Userlimt().getLIMIT_USER_TYPE(RcsUtils.getElementTextByKey(em,"LIMIT_USER_TYPE").toString()));
		    //支付账号
		    o.setLimitusercode(RcsUtils.getElementTextByKey(em, "LIMIT_USER_CODE").equals("")?"----":RcsUtils.getElementTextByKey(em, "LIMIT_USER_CODE"));
		    //生效时间
		    o.setLimitstartdate(RcsUtils.getElementTextByKey(em, "LIMIT_START_DATE").equals("19700101") ? "永久" : RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "LIMIT_START_DATE")));
		    //失效时间
		    o.setLimitenddate( RcsUtils.getElementTextByKey(em, "LIMIT_END_DATE").equals("29991231") ? "永久" : RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "LIMIT_END_DATE")));
	
		    //支付方式
		    o.setLimitbusclient(getLIMIT_BUS_CLIENT(RcsUtils.getElementTextByKey(em, "LIMIT_BUS_CLIENT")));
		    //当天消费金额
		    o.setLimitdayamt(RcsUtils.getElementTextByKey(em, "LIMIT_DAY_AMT"));
		    
		    o.setCreatename(RcsUtils.getElementTextByKey(em, "CREATE_NAME"));
		    o.setCreatedate(RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "CREATE_DATE")));
		    o.setUpdatename(RcsUtils.getElementTextByKey(em, "UPDATE_NAME"));
		    o.setUpdatedate(RcsUtils.turnDateFormat(RcsUtils.getElementTextByKey(em, "UPDATE_DATE")));
		    //启用状态
		    o.setIsuse(RcsUtils.getElementTextByKey(em, "IS_USE").equals("1")?"启用" :"未启用");
		    //名单状态
		    o.setLimitRiskFlag(RcsUtils.getUserRiskFlag(RcsUtils.getElementTextByKey(em, "LIMIT_RISK_FLAG")));
		    
			orderList.add(o);
		}
		return orderList;
	}
	
	//客户类型转换 code--->name
	private String getLIMIT_USER_TYPE(String typeName){
		//客户类型
		if(typeName.equals("1")){
			typeName="实名";
		}else if(typeName.equals("2")){
			typeName="非实名";
		}else if(typeName.equals("3")){
			typeName="指定用户";
		}else{
			typeName="----";
		}
		return typeName;
	}
	
	//支付方式转换 code--->name
	public static String getLIMIT_BUS_CLIENT(String code){
		if (code.equals("1"))
			return "网银";
		else if (code.equals("2"))
			return "终端 ";
		else if (code.equals("3"))
			return "消费卡";
		else if (code.equals("4"))
			return "虚拟积分";
		else if (code.equals("5"))
			return "混合";
		else
			return "";
	}
}
