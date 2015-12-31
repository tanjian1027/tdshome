package com.rcs.xlist;

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
import tangdi.engine.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.log.ILog;
import tangdi.web.TdWebConstants;

import com.bean.OffenceBean;
import com.resource.ResourceFile;
import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.RcsDefault;
import com.util.SqlUtil;
import com.util.RcsUtils;

@DB
public class CompBlackXList {
	/**
	 * 主键ID
	 */
	@Data
	String ID;
	
	/**
	 * 用户编码
	 */
	@Data
	String USER_CODE;

	/**
	 * 黑名单-企业 法人姓名
	 */
	@Data
	String TERR_NAME;

	/**
	 * 用户姓名
	 */
	@Data
	String USER_NAME;

	
	/**
	 * 性别
	 */
	@Data
	String TERR_SEX;
     
	/**
	 * 名单类型
	 */
	@Data
	String TERR_TYPE;

	/**
	 * 名单类型
	 */
	@Data
	String TERR_TNAME;

	
	/**
	 * 国籍
	 */
	@Data
	String TERR_COUNTRY;

	/**
	 * 证件类型
	 */
	@Data
	String PAPER_TYPE;

	/**
	 * 证件号
	 */
	@Data
	String PAPER_CODE;
	
	/**
	 * 创建人
	 */
	@Data
	String CREATE_NAME;

	/**
	 * 创建日期
	 */
	@Data
	String CREATE_DATE;

	/**
	 * 创建时间
	 */
	@Data
	String CREATE_DATETIME;
     
	/**
	 * 维护人
	 */
	@Data
	String UPDATE_NAME;

	/**
	 * 维护日期
	 */
	@Data
	String UPDATE_DATE;

	/**
	 * 维护时间
	 */
	@Data
	String UPDATE_DATETIME;
	
	/**
	 * 注册日期
	 */
	@Data
	String REGISTER_DATE;

	/**
	 * 当前页数
	 */
	@Data
	String pageNum;
	/**
	 * 每页显示条数
	 */
	@Data
	private String NUMPERPAGE;

	/**
	 *备注
	 */
	@Data
	String TERR_ADDINFO;
	
	/**
	 * 系统X名单类型
	 */
	@Data
	private String X_TYPE;
	/**
	 * 可疑状态  0 可疑，1 已确认，2已释放
	 */
	@Data
	 private String OFF_TYPE;
	
	/**
	 * 显示可选页数
	 */
	@Data
	private String PAGENUMSHOWN;

	/**
	 * 生成原因类型
	 */
	@Data
	private String CAS_TYPE;
	
	/**
	 * 客户编码
	 */
	@Data
	private String CLIENT_CODE;
	
	/**
	 * 黑名单-企业
	 */
	@Data
	private String OFF_ID;
	
	/**
	 * 客户类型 0：用户，1：企业
	 */
	@Data
	private String CUST_TYPE;
	/**
	 * 企业名称
	 */
	@Data
	private String MERCHANT_NAME;
	
	
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		//brefore
		ic.proceed();
		//after
		GetOwnButton.getOwnButton();
		return null;
	}
	
	/**
	 * 黑名单 企业管理
	 * @param logger
	 * @throws Exception
	 */
	@Code("540416")
	@tangdi.interceptor.Log
	public void findOffenceMng(ILog logger) throws Exception {
		StringBuffer sql = new StringBuffer("select * from RCS_OFFENCE_INFO a where CUST_TYPE='1' ");
		if (StringUtils.isNotEmpty(TERR_NAME)) {
			sql.append(" and a.TERR_NAME like '%" + TERR_NAME + "%'");
		}
		if (StringUtils.isNotEmpty(TERR_SEX)) {
			sql.append(" and a.TERR_SEX = '" + TERR_SEX + "'");
		}
		if (StringUtils.isNotEmpty(TERR_COUNTRY)) {
			sql.append(" and a.TERR_COUNTRY = '" + TERR_COUNTRY + "'");
		}
		if (StringUtils.isNotEmpty(TERR_TYPE)) {
			sql.append(" and a.TERR_TYPE= '" + TERR_TYPE + "'");
		}
		if (StringUtils.isNotEmpty(PAPER_TYPE)) {
			sql.append(" and a.PAPER_TYPE='" + PAPER_TYPE + "'");
		}
		if (StringUtils.isNotEmpty(PAPER_CODE)) {
			sql.append(" and a.PAPER_CODE like '%" + PAPER_CODE + "%'");
		}
		if (StringUtils.isNotEmpty(MERCHANT_NAME)) {
			sql.append(" and a.MERCHANT_NAME = '" + MERCHANT_NAME + "'");
		}
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");
		Log.info("sql=%s", sql);
		int result = Atc.PagedQuery(pageNum, NUMPERPAGE, "REC",sql.toString()+" order by PAPER_CODE");
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
			Etf.setChildValue("MsgTyp", "E");
			Etf.setChildValue("RspCod", "P0001");
			Etf.setChildValue("RspMsg", "查询失败");
		} 
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/xList/compBlackXList.jsp");
	}
	
	/**
	 *打开黑名单-企业增加
	 * @throws Exception
	 */
	@Code("540417")
	public void addLUser() throws Exception {
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "打开黑名单-企业增加");
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/xList/addCompBlack.jsp");
	}
	
	/**
	 *黑名单-企业添加
	 * @throws Exception
	 */
	@Code("540418")
	public void addOffence() throws Exception {
		String sSql = "SELECT SYSDATE FROM RCS_OFFENCE_INFO WHERE PAPER_TYPE = '"+PAPER_TYPE+"' AND PAPER_CODE = '"+PAPER_CODE+"'" +" AND CUST_TYPE = '"+CUST_TYPE+"'";
		int iResult = Atc.ReadRecord(sSql);
		if(iResult == 0){
			Atc.error("000001", "人员重复！");
			Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
			return;
		}
		
		String sql = "INSERT INTO RCS_OFFENCE_INFO(ID,) VALUES(SEQ_RCSUSERINFO_ID.NEXTVAL,)";
		sql = RcsUtils.addColAndVal(sql, "TERR_NAME"      , TERR_NAME);
		sql = RcsUtils.addColAndVal(sql, "TERR_SEX"       , TERR_SEX);
		sql = RcsUtils.addColAndVal(sql, "TERR_TYPE"      , TERR_TYPE);
		sql = RcsUtils.addColAndVal(sql, "TERR_COUNTRY"   , TERR_COUNTRY);
		sql = RcsUtils.addColAndVal(sql, "PAPER_TYPE"     , PAPER_TYPE);
		sql = RcsUtils.addColAndVal(sql, "PAPER_CODE"     , PAPER_CODE);
		sql = RcsUtils.addColAndVal(sql, "CUST_TYPE"      , CUST_TYPE);
		sql = RcsUtils.addColAndVal(sql, "MERCHANT_NAME"  , MERCHANT_NAME);
		
		sql = RcsUtils.addColAndVal(sql, "CREATE_NAME"    , Etf.getChildValue("SESSIONS.UID"));
		sql = RcsUtils.addColAndVal(sql, "CREATE_DATE"    , DateUtil.getCurrentDate());
		sql = RcsUtils.addColAndVal(sql, "CREATE_DATETIME", DateUtil.getCurrentDateTime());
		sql = RcsUtils.addColAndVal(sql, "UPDATE_NAME"    , Etf.getChildValue("SESSIONS.UID"));
		sql = RcsUtils.addColAndVal(sql, "UPDATE_DATE"    , DateUtil.getCurrentDate());
		sql = RcsUtils.addColAndVal(sql, "UPDATE_DATETIME", DateUtil.getCurrentDateTime());
		sql = RcsUtils.addColAndVal(sql, "TERR_ADDINFO"   , TERR_ADDINFO);
		
		sql = sql.replaceAll(",\\)", ")");
		
		Log.info("执行保存用户的sql语句是=%s", sql);
		int result = Atc.ExecSql(null, sql);
		if (result == 0) {
			Log.info("保存用户成功.....");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "增加涉恐分子成功");
			Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
			Etf.setChildValue("FORWARDURL", "540416.tran");
			Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
					"/ajaxrequest.jsp");
			Etf.setChildValue("CALLBACKTYPE","closeCurrent");
			return;
		} else if (result == 2) {
			Atc.error("200003", "修改的用户不存在");
		} else if (result == 3) {
			Atc.error("200006", "违反唯一性约束，登录用户名已经存在");
		} else {
			Atc.error("200002", "系统错误");
		}
		Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}

	/**
	 *黑名单-企业信息查询
	 * @param logger
	 * @throws Exception
	 */
	@Code("540419")
	@tangdi.interceptor.Log
	public void singleFindOffence(ILog logger) throws Exception {
        Log.info("540419黑名单-企业信息查询");
        String sql = "SELECT * FROM RCS_OFFENCE_INFO WHERE ID = '" + ID + "'";
		logger.info("执行的sql语句是=%s", sql);
		int result = Atc.ReadRecord(sql);
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else if (result == 2) {
			Atc.error("200001", "记录未找到");
		} else if (result == -1) {
			Atc.error("200002", "系统错误");
		} else {
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/xList/singleFindCompBlack.jsp");
	}
	
	/**
	 *黑名单-企业修改前查询
	 * @param logger
	 * @throws Exception
	 */
	@Code("540420")
	@tangdi.interceptor.Log
	public void beforeUptOffence(ILog logger) throws Exception {
        String sql = "select * from RCS_OFFENCE_INFO where ID = '" + ID + "'";
        Log.info("执行SQL语句=[%s]", sql);
		int result = Atc.ReadRecord(sql);
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
		} else if (result == 2) {
			Atc.error("200001", "记录未找到");
		} else if (result == -1) {
			Atc.error("200002", "系统错误");
		} else {
			Atc.error("200008", "系统错误");
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/offence/singleUptOffence.jsp");
	}	
	
	/**
	 * 黑名单-企业信息修改
	 * @throws Exception
	 */
	@Code("540421")
	@tangdi.interceptor.Log
	public void singleUptOffence () throws Exception {
		StringBuffer sql = new StringBuffer("update RCS_OFFENCE_INFO set");
		if (StringUtils.isNotEmpty(this.TERR_NAME)) {
			sql.append(" TERR_NAME='" + this.TERR_NAME + "',");
		}
		if (StringUtils.isNotEmpty(this.TERR_SEX)) {
			sql.append(" TERR_SEX='" + this.TERR_SEX + "',");
		}
		if (StringUtils.isNotEmpty(this.TERR_TNAME)) {
			sql.append(" TERR_TNAME='" + this.TERR_TNAME + "',");
		}
		if (StringUtils.isNotEmpty(this.TERR_TYPE)) {
			sql.append(" TERR_TYPE='" + this.TERR_TYPE + "',");
		}
		if (StringUtils.isNotEmpty(this.TERR_COUNTRY)) {
			sql.append(" TERR_COUNTRY='" + this.TERR_COUNTRY + "',");
		}
		if (StringUtils.isNotEmpty(this.PAPER_CODE)) {
			sql.append(" PAPER_CODE='" + this.PAPER_CODE + "',");
		}
		if (StringUtils.isNotEmpty(this.PAPER_TYPE)) {
			sql.append(" PAPER_TYPE='" + this.PAPER_TYPE + "',");
		}
			sql.append(" UPDATE_NAME='" + Etf.getChildValue("SESSIONS.UID")+ "',"); 
			sql.append(" UPDATE_DATE='" +DateUtil.getCurrentDate() + "',");
			sql.append(" UPDATE_DATETIME='" +DateUtil.getCurrentDateTime() + "',");
		if (StringUtils.isNotEmpty(this.TERR_ADDINFO)) {
			sql.append(" TERR_ADDINFO='" + this.TERR_ADDINFO + "',");
		}
		Log.info("执行的语句是=%s", sql.toString());
		int index = sql.lastIndexOf(",");
		sql = sql.replace(index, index + 1, " ");
		sql.append(" where ID='" + this.ID + "'");
		Log.info("执行的语句是=%s", sql.toString());
		int result = Atc.ExecSql(null, sql.toString());
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "修改成功");
			Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
			Etf.setChildValue("FORWARDURL", "540416.tran");
		} else if (result == 2) {
			Atc.error("200004", "修改失败");
		} else if (result == 3) {
			Atc.error("200006", "违反数据唯一性");
		} else {
			Atc.error("200008", "系统错误");
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
	}
	
	/**
	 *黑名单-企业信息删除
	 * @param logger
	 * @throws Exception
	 */
	@Code("540422")
	@tangdi.interceptor.Log
	public void delMerRule(ILog logger) throws Exception {
		String sql = "DELETE FROM RCS_OFFENCE_INFO WHERE ID='" + ID + "'";
		logger.info("执行的sql语句是=%s", sql);
		int result = Atc.ExecSql(null, sql);
		Log.info("sql=%s", sql);
		
		if (result == 0) {
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "删除成功");
			Etf.setChildValue("FORWARDURL", "540416.tran");
			Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
			return;
		} else if (result == 2) {
			Atc.error("200007", "删除失败");
		} else {
			Atc.error("200008", "系统错误");
		}
	}
    
	/**
	 * 获取犯罪分子名单类型
	 * @throws Exception
	 */
    @Code("540423")
    public void getAllOffence() throws Exception {
    	Log.info("获取获取犯罪分子名单类型开始");
	    Log.info("获取获取犯罪分子名单类型结束");
    }	    
    
    /**
	 *可疑黑名单-企业管理
	 * @param logger
	 * @throws Exception
	 */	 
	@Code("540424")
	public void susOffenceMng(ILog logger) throws Exception {
        StringBuffer sql = new StringBuffer("");
        sql.append(" select * from RCS_OFFENCE_USER a "+
        		   " left join RCS_TRAN_USER_INFO  b  on a.user_code=b.user_code "+
        		   " left join RCS_OFFENCE_INFO    c  on a.off_id = c.id" );
        
		if (StringUtils.isNotEmpty(USER_NAME)) {
			sql.append(" and b.USER_NAME like '%" + USER_NAME + "%'");
		}
		if (StringUtils.isNotEmpty(PAPER_CODE)) {
			sql.append(" and b.PAPER_CODE like '%" + PAPER_CODE + "%'");
		}
		if (StringUtils.isNotEmpty(TERR_TYPE)) {
			sql.append(" and a.TERR_TYPE='" + TERR_TYPE + "'");
		}
		if (StringUtils.isNotEmpty(OFF_TYPE)) { 
			sql.append(" and rou.OFF_TYPE='" + OFF_TYPE + "'");
		}
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
		NUMPERPAGE   = RcsUtils.getCommParam("G_COMMParams", "G_NUMPERPAGE");
		PAGENUMSHOWN = RcsUtils.getCommParam("G_COMMParams", "G_PAGENUMSHOWN");  		
		Log.info("sql=%s", sql);
		int result = Atc.PagedQuery(this.pageNum, NUMPERPAGE, "REC",sql.toString());
		if(result == 0){
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功");
			int TOLCNT = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt("19");
			logger.info("开始页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/xList/singleFindCompBlack.jsp");
	}
	/**
	 * 导出
	 * @param logger
	 * @throws Exception
	 */
	@Code("540424")
	public void excelExport(ILog logger) throws Exception {
		StringBuffer sql = new StringBuffer("select * from RCS_OFFENCE_INFO a where CUST_TYPE='1' ");
		if (StringUtils.isNotEmpty(TERR_NAME)) {
			sql.append(" and a.TERR_NAME like '%" + TERR_NAME + "%'");
		}
		if (StringUtils.isNotEmpty(TERR_SEX)) {
			sql.append(" and a.TERR_SEX = '" + TERR_SEX + "'");
		}
		if (StringUtils.isNotEmpty(TERR_COUNTRY)) {
			sql.append(" and a.TERR_COUNTRY = '" + TERR_COUNTRY + "'");
		}
		if (StringUtils.isNotEmpty(TERR_TYPE)) {
			sql.append(" and a.TERR_TYPE= '" + TERR_TYPE + "'");
		}
		if (StringUtils.isNotEmpty(PAPER_TYPE)) {
			sql.append(" and a.PAPER_TYPE='" + PAPER_TYPE + "'");
		}
		if (StringUtils.isNotEmpty(PAPER_CODE)) {
			sql.append(" and a.PAPER_CODE like '%" + PAPER_CODE + "%'");
		}
		
		int result =  Atc.QueryInGroup(sql.toString(), "REC", null);
		Log.info("sql=%s", sql);
		if (result == 0) {
			List orderList = getOrderList();
			ExcelUtils.addValue("orderList", orderList);
			String fileName = "OffenceList_" + RcsUtils.getUID()+"_"+System.currentTimeMillis() + ".xls";//增加登录用户id 防止同时下载文件被覆盖
			ResourceFile.FtpPut("OffenceList", fileName);
			Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH") + fileName);//给页面一个连接下载文件
		} else {
			Etf.setChildValue("MsgTyp", "E");
			Etf.setChildValue("RspCod", "P0001");
			Etf.setChildValue("RspMsg", "查询失败");
		} 
	}
	private List<OffenceBean> getOrderList() {// 返回这个bean型的list
		List list = Etf.childs("REC");
		
		Log.info("导出数据条数为 ： "+ list.size());
		
		List orderList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			OffenceBean o = new OffenceBean();
			Element em = (Element) list.get(i);
			
		    //黑名单-企业法人名称
		    o.setterrname(getElementText(em, "TERR_NAME"));
		    //性别
		    o.setterrsex(getElementText(em, "TERR_SEX").equals("1")?"男" :"女");
		    //涉恐犯罪类型
		    o.setterrtname(RcsDefault.map_TERR_TYPE.get(getElementText(em, "TERR_TYPE"))+"");
		    //国籍
		    o.setterrcountry(getElementText(em, "TERR_COUNTRY"));
		    //证件类型
		    o.setpapertype(getElementText(em, "PAPER_TYPE"));
		    //证件号
		    o.setpepercode(getElementText(em, "PAPER_CODE"));
		    
			orderList.add(o);
		}

		return orderList;
	}
	
	private static String getElementText(Element em, String key) {
	    Element e = em.element(key);
	    if (e == null)
	    	return "";
	    return e.getText();
	}

	/**
	 * 根据ID 修改 已确认  已释放
	 * @throws Exception
	 */
	@Code("540425")
	public void upOffenceTran() throws Exception {
		Log.info("USER_CODE  " + this.CLIENT_CODE);
		Log.info("1 已确认，2已释放"+this.X_TYPE);
		
		Log.info("OFF_TYPE='"+X_TYPE+"'");
		//修改此犯罪分子状态
		String sql ="update RCS_OFFENCE_USER set OFF_TYPE='"+X_TYPE+"',"
		            +"  UPDATE_NAME='"+Etf.getChildValue("SESSIONS.UID")
	                +"',UPDATE_DATE='"+ DateUtil.getCurrentDate()
	                +"',UPDATE_DATETIME='"+DateUtil.getCurrentDateTime()
	                +"' where USER_CODE='"+this.CLIENT_CODE+"' and OFF_ID='"+Etf.getChildValue("OFF_ID")+"'";
		
		int result = Atc.ExecSql(null,sql);
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/ajaxrequest.jsp");
		if (result == 0) {
			if(X_TYPE.equals("1"))  {//确认可以犯罪分子
				sql = "INSERT INTO RCS_X_LIST_SYS(ID,) VALUES("+SqlUtil.SEQ_RCSXLISTSYS_ID_ADD1+",)";
				sql = RcsUtils.addColAndVal(sql, "USER_TYPE"      , "0");
				sql = RcsUtils.addColAndVal(sql, "CLIENT_CODE"    , this.CLIENT_CODE);
				sql = RcsUtils.addColAndVal(sql, "X_TYPE"         , "2");
				sql = RcsUtils.addColAndVal(sql, "CAS_TYPE"       , "4");//1异常交易、2可疑交易，3预警交易、4涉恐犯罪
				sql = RcsUtils.addColAndVal(sql, "CREATE_NAME"    , Etf.getChildValue("SESSIONS.UID"));
				sql = RcsUtils.addColAndVal(sql, "CREATE_DATE"    , DateUtil.getCurrentDate());
				sql = RcsUtils.addColAndVal(sql, "CREATE_DATETIME", DateUtil.getCurrentDateTime());
				sql = RcsUtils.addColAndVal(sql, "UPDATE_NAME"    , Etf.getChildValue("SESSIONS.UID"));
				sql = RcsUtils.addColAndVal(sql, "UPDATE_DATE"    , DateUtil.getCurrentDate());
				sql = RcsUtils.addColAndVal(sql, "UPDATE_DATETIME", DateUtil.getCurrentDateTime());
				
				sql = sql.replaceAll(",\\)", ")");
				
				Log.info("打印查询sql语句2 ：" + sql);
				//确认犯罪分子的同时把该用户拉黑
				String upUserToBlackSql ="update  RCS_TRAN_USER_INFO set risk_flag='2' where user_code='"+CLIENT_CODE+"'";
				
				result = Atc.ExecSql(null, sql);
				result = Atc.ExecSql(null, upUserToBlackSql);
			}			
		}
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "用户操作成功！");
	}

	/**
	 *用户信息比对
	 * @param logger
	 * @throws Exception
	 */
	@Code("540426")
	@tangdi.interceptor.Log
	public void checkInfcontrast(ILog logger) throws Exception {
        Log.info("540426用户信息比对");
        String sql = "select tu.user_code,tu.user_name,tu.paper_type,tu.paper_code,tu.update_date," +
        		"    tu.register_date,ro.terr_name,ro.terr_country,ro.paper_code as ou_paper_code," +
        		"    ro.paper_type as ou_paper_type,ro.terr_sex,'' as terr_tname,rou.off_type" +
        		" from rcs_offence_user rou" +
        		"    inner join (select * from RCS_TRAN_USER_INFO) tu on tu.user_code = rou.user_code" +
        		"    inner join (select * from rcs_offence_info) ro on ro.id = rou.off_id " +
        		" WHERE rou.user_code = '"+Etf.getChildValue("USER_CODE")+"' ";
		logger.info("执行的sql语句是=%s", sql);
		int result = Atc.ReadRecord(sql);
		if (result == 0) {
			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "000000"); 
			Etf.setChildValue("RspMsg", "查询成功");
		} else if (result == 2) {
			Atc.error("200001", "记录未找到");
		} else if (result == -1) {
			Atc.error("200002", "系统错误");
		} else {
		}
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/offence/checkInfcontrast.jsp");
	}
}