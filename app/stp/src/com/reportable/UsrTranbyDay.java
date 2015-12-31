package com.reportable;

import java.util.ArrayList;
import java.util.List;
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
import tangdi.web.TdWebConstants;
import com.bean.UsrTranBean;
import com.tangdi.util.GetOwnButton;
import com.util.Utils;
import com.util.Other;

@DB
public class UsrTranbyDay {
	/**
	 * 商户号
	 */
	@Data
	private String MERNO;
	
	/**
	 * 商户名称
	 */
	@Data
	private String MERCNAM;
	
	/**
	 * 开始时间
	 */
	@Data
	private String APPDAY_START;
	
	/**
	 * 结束时间
	 */
	@Data
	private String APPDAY_END;

	/**
	 * 订单充值/提现/退款/转账/消费成功时间
	 */
	@Data
	private String DATETIME;
	
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
	/**
	 * 订单发生日期
	 */
	@Data
	private String ORDERTIME;

	/**
	 * 支付订单号
	 */
	@Data
	private String PAYORDNO;
	
	/**
	 * 退款订单号
	 */
	@Data
	private String REFORDNO;

	/**
	 * 商品订单号
	 */
	@Data
	private String PRDORDNO;

	/**
	 * 区分01-充值，02-提现，03-退款，04-转账，05-消费
	 */
	@Data
	private String STATUS;
	
	public String getSql(){
		StringBuffer sql = new StringBuffer(" select distinct z.ordertime datetime, " + 
				" x.ordamt, " + 
				" x.num, " + 
				" x.ordertime, " + 
				" y.ordamt1, " + 
				" y.num1, " + 
				" y.ordertime1, " + 
				" o.ordamt2, " + 
				" o.num2, " + 
				" o.ordertime2, " + 
				" p.ordamt3, " + 
				" p.num3, " + 
				" p.ordertime3, " + 
				" q.ordamt4, " + 
				" q.num4, " + 
				" q.ordertime4 " + 
				" from (select substr(e.actdat, 1, 8) ordertime, '提现' statu " + 
				" from STPCASINF e " + 
				" where e.ordstatus = '02' " + 
				" group by substr(e.actdat, 1, 8) " + 
				" union " + 
				" select substr(g.rfordtime, 1, 8) ordertime, '退款' statu " + 
				" from StpRefInf g " + 
				" where g.ordstatus = '02' " + 
				" group by substr(g.rfordtime, 1, 8) " + 
				" union " + 
				" select substr(h.tratime, 1, 8) ordertime, '转账' statu " + 
				" from StpTraInf h " +
				" where h.ordstatus = '01' " + 
				" group by substr(h.tratime, 1, 8) " + 
				" union " + 
				" select substr(t.orderTime, 0, 8) ordertime, '消费' statu " + 
				" from StpPrdInf t left join STPPAYINF a  on a.payordno=t.payordno " + 
				" where a.ordstatus = '01' " + 
				" and t.prdordtype = '0' " + 
				" group by t.cust_id, substr(t.orderTime, 0, 8) " + 
				" union " + 
				" select substr(c.orderTime, 1, 8) ordertime, '充值' statu " + 
				" from STPPAYINF b " + 
				" left join STPPRDINF c " + 
				" ON (TRIM(b.PAYORDNO) = TRIM(c.PAYORDNO)) " + 
				" where c.ordstatus = '01' " + 
				" and (c.PRDORDTYPE = '1' OR c.PRDORDTYPE = '3') " + 
				" group by substr(c.orderTime, 1, 8)) z " + 
				" left join (select count(*) num4, " + 
				" to_char(sum(t.ordamt) / 100, '99999999999990.99') ordamt4, " + 
				" substr(t.orderTime, 0, 8) ordertime4 " + 
				" from StpPrdInf t left join STPPAYINF a  on a.payordno=t.payordno " + 
				" where a.ordstatus = '01' " + 
				" and t.prdordtype = '0' " + 
				" group by substr(t.orderTime, 0, 8)) q " + 
				" on z.ordertime = q.ordertime4 " + 
				" left join (select count(*) num, " + 
				" to_char(sum(c.ordamt) / 100, '99999999999990.99') ordamt, " + 
				" substr(c.orderTime, 1, 8) ordertime " + 
				" from STPPAYINF b " + 
				" left join STPPRDINF c " + 
				" ON (TRIM(b.PAYORDNO) = TRIM(c.PAYORDNO)) " + 
				" where c.ordstatus = '01' " + 
				" and (c.PRDORDTYPE = '1' OR c.PRDORDTYPE = '3') " + 
				" group by substr(c.orderTime, 1, 8)) x " + 
				" on z.ordertime = x.ordertime " + 
				" left join (select count(*) num1, " + 
				" to_char(sum(e.txamt) / 100, '99999999999990.99') ordamt1, " + 
				" substr(e.actdat, 1, 8) ordertime1 " + 
				" from STPCASINF e " + 
				" where e.ordstatus = '02' " + 
				" group by substr(e.actdat, 1, 8)) y " + 
				" on z.ordertime = y.ordertime1 " + 
				" left join (select count(*) num2, " + 
				" to_char(sum(g.rfamt) / 100, '99999999999990.99') ordamt2, " + 
				" substr(g.rfordtime, 1, 8) ordertime2 " + 
				" from StpRefInf g " + 
				" where g.ordstatus = '02' " + 
				" group by substr(g.rfordtime, 1, 8)) o " + 
				" on z.ordertime = o.ordertime2 " + 
				" left join (select count(*) num3, " + 
				" to_char(sum(h.txamt) / 100, '99999999999990.99') ordamt3, " + 
				" substr(h.tratime, 1, 8) ordertime3 " + 
				" from StpTraInf h " + 
				" where h.ordstatus = '01' " + 
				" group by substr(h.tratime, 1, 8)) p " + 
				" on z.ordertime = p.ordertime3 " + 
				" where 1=1  and z.ordertime is not null " + 
				"" );
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isEmpty(APPDAY_END)) {
			sql.append(" and z.ordertime>='" + Utils.formmat10to8(APPDAY_START) 
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_END) && StringUtils.isEmpty(APPDAY_START)) {
			sql.append(" and z.ordertime<='" + Utils.formmat10to8(APPDAY_END)+"235959"
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isNotEmpty(APPDAY_END)) {
			sql.append(" and z.ordertime>='" + Utils.formmat10to8(APPDAY_START)
					+ "' and z.ordertime<='" + Utils.formmat10to8(APPDAY_END) + "235959"
					+ "'");
		}
		sql.append(" order by z.ordertime desc ");
		return sql.toString();
	}
	
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01003") 
	public void getList() throws Exception {
		Log.info("所有用户每日交易情况统计，充值、提现、退款、转账、消费 成功笔数金额");
		String sql = getSql();
		Log.info("执行的SQL为：%s", sql);
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
	//	NUMPERPAGE = Default.NUMPERPAGE;
		NUMPERPAGE = "19";
		PAGENUMSHOWN = "5"; 
		int result = Atc.PagedQuery(this.pageNum, NUMPERPAGE, "REC",sql.toString());
		if (GetOwnButton.getOwnButton() == 3) {
			Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/outl.jsp");
			return ;
		}
		if (result == 0) {
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功！");
			/*总计*/
			StringBuffer total_Sql = new StringBuffer("select to_char(sum(nvl(j.ordamt,0)), '99999999999990.99') CZJE,sum(nvl(j.num,0)) CZBS," +
					"to_char(sum(nvl(j.ordamt1,0)), '99999999999990.99') TXJE,sum(nvl(j.num1,0)) TXBS," +
					"to_char(sum(nvl(j.ordamt2,0)), '99999999999990.99') TKJE,sum(nvl(j.num2,0)) TKBS," +
					"to_char(sum(nvl(j.ordamt3,0)), '99999999999990.99') ZZJE,sum(nvl(j.num3,0)) ZZBS," +
					"to_char(sum(nvl(j.ordamt4,0)), '99999999999990.99') XFJE,sum(nvl(j.num4,0)) XFBS from (").append(sql).append(") j");
			Atc.ReadRecord(total_Sql.toString());
			int TOLCNT = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			Log.info("页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else if (result == -1) {
			Etf.setChildValue("RspCod", "200001");
			Etf.setChildValue("RspMsg", "系统错误，请联系管理员！");
		} else {
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败！"); 
		}
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
				"/WEB-INF/html/reportable/usrtranbyday.jsp");
	}
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01030")
	public void getListInfo() throws Exception {
		Log.info("用户充值、提现、消费、转账、退款明细");
		StringBuffer sql = null;
		String url = null;
		if (StringUtils.isNotEmpty(STATUS)) {
			Etf.setChildValue("sta", STATUS);
			if(STATUS.equals("01")){
				sql = new StringBuffer(" select to_char(c.ordamt / 100, '99999999999990.99') ordamt,b.payordno,c.prdordno,c.ordertime,b.cust_id,e.bank_name, " + 
						" (c.orderTime) datetime " + 
						" from STPPAYINF b " + 
						" left join STPPRDINF c " + 
						" ON b.PAYORDNO = c.PAYORDNO " + 
						" LEFT JOIN  COOPBANK e ON substr(b.BANKCOD,1,2)=substr(e.BANK_CODE,1,2)   " + 
						" where c.ordstatus = '01' " + 
						" and (c.PRDORDTYPE = '1' OR c.PRDORDTYPE = '3')" + 
						" and (c.orderTime) like '" + DATETIME + "%'"); 
				if (StringUtils.isNotEmpty(PAYORDNO)) {
					sql.append(" and b.payordno like '%" + PAYORDNO + "%'");
				}
				if (StringUtils.isNotEmpty(PRDORDNO)) {
					sql.append(" and c.prdordno like '%" + PRDORDNO + "%'");
				}
				url = "/WEB-INF/html/reportable/usrinfobyday_pay.jsp";
			}else if(STATUS.equals("02")){
				sql = new StringBuffer("select to_char(a.txamt / 100, '99999999999990.99') ordamt,a.casordno,a.cust_id,e.bank_name,a.bankpayusernm, " + 
						" a.actdat datetime " + 
						" from STPCASINF a " + 
						" LEFT JOIN  COOPBANK e ON substr(a.bankcode,1,2)=substr(e.BANK_CODE,1,2)   " + 
						" where a.ordstatus = '02'" + 
						" and a.actdat like '" + DATETIME + "%'");
				url = "/WEB-INF/html/reportable/usrinfobyday_cas.jsp";
			}else if(STATUS.equals("03")){
				sql = new StringBuffer(" select to_char(a.rfamt / 100, '99999999999990.99') ordamt,a.refordno,a.cust_id, " +
						" to_char(a.accamt / 100, '99999999999990.99') accamt,to_char(a.mulamt / 100, '99999999999990.99') mulamt, " + 
						" a.rfordtime datetime " + 
						" from StpRefInf a " + 
						" where a.ordstatus = '02' " + 
						" and a.rfordtime like '" + DATETIME + "%'");
				if (StringUtils.isNotEmpty(REFORDNO)) {
					sql.append(" and a.refordno like '%" + REFORDNO + "%'");
				}
				url = "/WEB-INF/html/reportable/usrinfobyday_ref.jsp";
			}else if(STATUS.equals("04")){
				sql = new StringBuffer("select to_char(h.txamt / 100, '99999999999990.99') ordamt,h.traordno,h.traCusId,h.getCusId,h.traCusNam,h.getCusNam, " + 
						" h.tratime datetime " + 
						" from StpTraInf h " + 
						" where h.ordstatus = '01' " + 
						" and h.tratime like '" + DATETIME + "%'");
				url = "/WEB-INF/html/reportable/usrinfobyday_rfs.jsp";
			}else if(STATUS.equals("05")){
				sql = new StringBuffer("select to_char(t.ordamt / 100, '99999999999990.99') ordamt,t.prdordno,t.merno,t.ordertime,t.prdname,t.cust_id, " + 
						" (t.orderTime) datetime " + 
						" from StpPrdInf t left join STPPAYINF a  on a.payordno=t.payordno " + 
						" where a.ordstatus = '01' " + 
						" and t.prdordtype = '0'  " + 
						" and (t.orderTime) like '" + DATETIME + "%'");
				if (StringUtils.isNotEmpty(PRDORDNO)) {
					sql.append(" and t.prdordno like '%" + PRDORDNO + "%'");
				}
				url = "/WEB-INF/html/reportable/usrinfobyday_prd.jsp";
			}else{
				Etf.setChildValue("RspCod", "200001");
				Etf.setChildValue("RspMsg", "系统错误，参数传递失败");
				return;
			}
			Log.info("执行的SQL为：%s", sql);
		}
		else{
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "获取当前状态失败！");
			Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,url); 
			return ;
		}
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
	//	NUMPERPAGE = Default.NUMPERPAGE;
		NUMPERPAGE = "19";
		PAGENUMSHOWN = "5"; 
		int result = Atc.PagedQuery(this.pageNum, NUMPERPAGE, "REC",sql.toString());
		if (GetOwnButton.getOwnButton() == 3) {
			Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/outl.jsp");
			return ;
		}
		if (result == 0) {
			Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "查询成功！");
			int TOLCNT = Integer.parseInt(Etf.getChildValue("TOLCNT"));
			int perPage = Integer.parseInt(NUMPERPAGE);
			Log.info("页数=%s", (TOLCNT / perPage) * perPage + 1);
			int startIndex = (TOLCNT / perPage) * perPage + 1;
			Etf.setChildValue("startIndex", String.valueOf(startIndex));
		} else if (result == -1) {
			Etf.setChildValue("RspCod", "200001");
			Etf.setChildValue("RspMsg", "系统错误，请联系管理员！");
		} else {
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "查询失败！"); 
		}
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,url); 
	}
	/**
	 * Excel导出
	 * 
	 * @throws Exception
	 */
	@Code("S02003") 
	public void doExport() throws Exception {
		String sql = getSql();/*总计*/
		StringBuffer total_Sql = new StringBuffer("select to_char(sum(nvl(j.ordamt,0)), 99999999999990.99) CZJE,sum(nvl(j.num,0)) CZBS," +
				"to_char(sum(nvl(j.ordamt1,0)), 99999999999990.99) TXJE,sum(nvl(j.num1,0)) TXBS," +
				"to_char(sum(nvl(j.ordamt2,0)), 99999999999990.99) TKJE,sum(nvl(j.num2,0)) TKBS," +
				"to_char(sum(nvl(j.ordamt3,0)), 99999999999990.99) ZZJE,sum(nvl(j.num3,0)) ZZBS," +
				"to_char(sum(nvl(j.ordamt4,0)), 99999999999990.99) XFJE,sum(nvl(j.num4,0)) XFBS from (").append(sql).append(") j");
		int result = Atc.QueryInGroup(sql.toString(), "REC", null);	
		if (result == 0||result == 2) {
			int result_all = Atc.ReadRecord(total_Sql.toString());
			if (result_all == 0||result_all == 2) {
				String[] str_all = new String[]{"合计","￥"+Etf.getChildValue("CZJE"),Etf.getChildValue("CZBS"),
						"￥"+Etf.getChildValue("TXJE"),Etf.getChildValue("TXBS"),
						"￥"+Etf.getChildValue("TKJE"),Etf.getChildValue("TKBS"),
						"￥"+Etf.getChildValue("ZZJE"),Etf.getChildValue("ZZBS"),
						"￥"+Etf.getChildValue("XFJE"),Etf.getChildValue("XFBS")};
				List<List<String>> inverList = getTermList();
				Log.info("=====inverList "+inverList.size());
				ExcelUtils.addValue("inverList", inverList);
				String fileName = "UsrTranByDaySchedule_" + Utils.getCurrentDateTime() + ".xls";
				Other o = new Other();
				String path = o.createExcel(inverList, new String[]{"交易日期","充值总金额","充值总笔数","提现总金额","提现总笔数","退款总金额","退款总笔数","转账总金额","转账总笔数","消费总金额","消费总笔数"},str_all, "用户每日交易汇总", "/home/payment/apache-tomcat-7.0.64/webapps/pay/dat/download/" + fileName);
				Log.info("=====path "+path);
				Msg.set("_REQUESTATTR.REDIRECTURL", "dat/download/" + fileName);//给页面一个连接下载文件\\
			}
		} else {
			Etf.setChildValue("RspCod", "200001");
			Etf.setChildValue("RspMsg", "系统错误，请联系管理员！");
			Etf.setChildValue("DWZ_FORWARD_URL", "/ajaxrequest.jsp");
			return;
		} 
	}
	/** 
	 * 导出文件
	 */
	private List<List<String>> getTermList() {
		// 返回这个bean型的list
		List list = Etf.childs("REC");
		List orderList = new ArrayList<UsrTranBean>();
		for (int i = 0; i < list.size(); i++) {
			List<String> list2 = new ArrayList<String>();
			UsrTranBean sb = new UsrTranBean();
			Element em = (Element) list.get(i);
			sb.setDATETIME(Utils.getElementTextByKey(em, "DATETIME"));
			sb.setORDAMT("￥"+Utils.getElementTextByKey(em, "ORDAMT"));
			sb.setNUM(Utils.getElementTextByKey(em, "NUM"));
			sb.setORDAMT1("￥"+Utils.getElementTextByKey(em, "ORDAMT1"));
			sb.setNUM1(Utils.getElementTextByKey(em, "NUM1"));
			sb.setORDAMT2("￥"+Utils.getElementTextByKey(em, "ORDAMT2"));
			sb.setNUM2(Utils.getElementTextByKey(em, "NUM2"));
			sb.setORDAMT3("￥"+Utils.getElementTextByKey(em, "ORDAMT3"));
			sb.setNUM3(Utils.getElementTextByKey(em, "NUM3"));
			sb.setORDAMT4("￥"+Utils.getElementTextByKey(em, "ORDAMT4"));
			sb.setNUM4(Utils.getElementTextByKey(em, "NUM4"));
			if(null==sb.getDATETIME()||""==sb.getDATETIME()||"".equals(sb.getDATETIME()))
				sb.setDATETIME("-");
			if(null==sb.getORDAMT()||"￥"==sb.getORDAMT()||"￥".equals(sb.getORDAMT()))
				sb.setORDAMT("￥0.00");
			if(null==sb.getNUM()||""==sb.getNUM()||"".equals(sb.getNUM()))
				sb.setNUM("0");
			if(null==sb.getORDAMT1()||"￥"==sb.getORDAMT1()||"￥".equals(sb.getORDAMT1()))
				sb.setORDAMT1("￥0.00");
			if(null==sb.getNUM1()||""==sb.getNUM1()||"".equals(sb.getNUM1()))
				sb.setNUM1("0");
			if(null==sb.getORDAMT2()||"￥"==sb.getORDAMT2()||"￥".equals(sb.getORDAMT2()))
				sb.setORDAMT2("￥0.00");
			if(null==sb.getNUM2()||""==sb.getNUM2()||"".equals(sb.getNUM2()))
				sb.setNUM2("0");
			if(null==sb.getORDAMT3()||"￥"==sb.getORDAMT3()||"￥".equals(sb.getORDAMT3()))
				sb.setORDAMT3("￥0.00");
			if(null==sb.getNUM3()||""==sb.getNUM3()||"".equals(sb.getNUM3()))
				sb.setNUM3("0");
			if(null==sb.getORDAMT4()||"￥"==sb.getORDAMT4()||"￥".equals(sb.getORDAMT4()))
				sb.setORDAMT4("￥0.00");
			if(null==sb.getNUM4()||""==sb.getNUM4()||"".equals(sb.getNUM4()))
				sb.setNUM4("0");
			
			list2.add(sb.getDATETIME());
			list2.add(sb.getORDAMT());
			list2.add(sb.getNUM());
			list2.add(sb.getORDAMT1());
			list2.add(sb.getNUM2());
			list2.add(sb.getORDAMT2());
			list2.add(sb.getNUM2());
			list2.add(sb.getORDAMT3());
			list2.add(sb.getNUM3());
			list2.add(sb.getORDAMT4());
			list2.add(sb.getNUM4());
			orderList.add(list2);
		}
		return orderList;
	}
}
