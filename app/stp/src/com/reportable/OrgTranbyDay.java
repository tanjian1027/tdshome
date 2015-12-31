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
import com.bean.OrgTranBean;
import com.tangdi.util.GetOwnButton;
import com.util.Utils;
import com.util.Other;

@DB
public class OrgTranbyDay {
	
	/**
	 * 支付渠道名称
	 */
	@Data
	private String BANK_NAME;
	
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
	 * 日期
	 */
	@Data
	private String DATETIME;

	/**
	 * 支付订单号
	 */
	@Data
	private String PAYORDNO;

	/**
	 * 商品订单号
	 */
	@Data
	private String PRDORDNO;
	
	/**
	 * 状态，01-消费；02-退款；03-充值
	 */
	@Data
	private String STATUS;
	
	public String getSql(){
		//商城支付成功,每个支付机构每天一条记录 
		StringBuffer sql_pay = new StringBuffer("" + 
				" select n.bank_name, " + 
				" substr(a.orderTime,0,8) datetime, " + 
				" case when sum(b.mulamt) is null then sum(b.txamt) else sum(b.mulamt) end ordamt, " + 
				" count(*) numb " + 
				" from stpprdinf a " + 
				" left join stppayinf b " + 
				" on a.payordno = b.payordno " + 
				" left join   StpPayInf m  " + 
				" on trim(a.payordno) = trim(m.payordno) " + 
				" left join CoopBank n " + 
				" on substr(m.bankcod,0,2)=substr(TRIM(N.BANK_CODE),0,2) " + 
				" where a.prdordtype in ('0', '2') " + 
				" and b.ordstatus = '01' " + 
				" and m.paytype in ('01', '04') " + 
				" group by n.bank_name,substr(a.orderTime,0,8)" );
		//商城退款成功 ,每个支付机构每天一条记录 
		StringBuffer sql_prd = new StringBuffer("" + 
				" select n.bank_name, " + 
				" substr(a.orderTime,0,8) datetime,  " + 
				" case when sum(b.mulamt) is null then sum(b.rfamt) else sum(b.mulamt) end ordamt, " + 
				" count(*) numb " + 
				" from stpprdinf a " + 
				" left join stprefinf b " + 
				" on a.prdordno = b.oriprdordno " + 
				" left join   StpPayInf m  " + 
				" on trim(a.payordno) = trim(m.payordno) " + 
				" left join CoopBank n " + 
				" on substr(m.bankcod,0,2)=substr(TRIM(N.BANK_CODE),0,2) " + 
				" where a.prdordtype in ('0', '2') " + 
				" and a.ordstatus = '05' " + 
				" and m.paytype in ('01', '04') " + 
				" group by n.bank_name,substr(a.orderTime,0,8) " );
		//充值成功订单 ,每个支付机构每天一条记录 
		StringBuffer sql_rch = new StringBuffer("" +
				" SELECT C.BANK_NAME, " + 
				" substr(B.orderTime,0,8) datetime, " + 
				" sum(B.ORDAMT) ordamt, " + 
				" count(*) numb " + 
				" FROM STPPAYINF A " + 
				" LEFT JOIN STPPRDINF B " + 
				" ON (TRIM(A.PAYORDNO) = TRIM(B.PAYORDNO)) " + 
				" LEFT JOIN COOPBANK C " + 
				" ON substr(A.BANKCOD,0,2) = substr(TRIM(C.BANK_CODE),0,2) " + 
				" WHERE A.PAYTYPE = '01' " + 
				" AND B.ORDSTATUS = '01' " + 
				" AND (B.PRDORDTYPE = '1' OR B.PRDORDTYPE = '3') " + 
				" group by C.BANK_NAME,substr(B.orderTime,0,8)" ); 
		//汇总
		StringBuffer sql = new StringBuffer("select distinct t.datetime,t.bank_name,to_char(x.ordamt / 100,  999999990.99) as ordamt,x.numb,to_char(y.ordamt / 100,  999999990.99) as ordamt1,y.numb as numb1,to_char(z.ordamt / 100,  999999990.99) as ordamt2,z.numb as numb2 from " +
				" (" + sql_pay + " union " + sql_prd + " union " + sql_rch + ") t " +
				" left join (" + sql_pay + ")  x on x.datetime=t.datetime and x.bank_name=t.bank_name " +
 				" left join (" + sql_prd + ")  y on y.datetime=t.datetime and y.bank_name=t.bank_name " +
				" left join (" + sql_rch + ")  z on z.datetime=t.datetime and z.bank_name=t.bank_name " +
				" where 1=1  and t.datetime is not null " ); 

		if (StringUtils.isNotEmpty(BANK_NAME)) {
			sql.append(" and t.BANK_NAME like '%" +  BANK_NAME
					+ "%'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isEmpty(APPDAY_END)) {
			sql.append(" and t.datetime>='" + Utils.formmat10to8(APPDAY_START) 
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_END) && StringUtils.isEmpty(APPDAY_START)) {
			sql.append(" and t.datetime<='" + Utils.formmat10to8(APPDAY_END)  
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isNotEmpty(APPDAY_END)) {
			sql.append(" and t.datetime>='" + Utils.formmat10to8(APPDAY_START)
					+ "' and t.datetime<='" + Utils.formmat10to8(APPDAY_END)  
					+ "'");
		}
		sql.append(" order by t.datetime desc ");
		return sql.toString();
	}
	
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01005") 
	public void getList() throws Exception {
		Log.info("每个支付渠道每日交易情况统计，消费、退款、充值 成功笔数金额");
		String sql = getSql();
		Log.info("执行的SQL为：%s", sql);
		if (!StringUtils.isNotEmpty(pageNum)) {
			this.pageNum = "1";
		}
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
			StringBuffer total_Sql = new StringBuffer("select to_char(sum(j.ordamt), 999999990.99) SHJE,sum(j.numb) SHBS," +
					" to_char(sum(j.ordamt1), 999999990.99) TKJE,sum(j.numb1) TKBS," +
					" to_char(sum(j.ordamt2), 999999990.99) CZJE,sum(j.numb2) CZBS " +
					" from (").append(sql).append(") j");
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
				"/WEB-INF/html/reportable/orgtranbyday.jsp");
	}
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01050")
	public void getListInfo() throws Exception {
		Log.info("每个支付渠道每日 消费、退款、充值 明细");
		StringBuffer sql = null;
		if (StringUtils.isNotEmpty(STATUS)) {
			Etf.setChildValue("sta", STATUS);
			if(STATUS.equals("01")){
				sql = new StringBuffer("select a.prdordno,a.payordno,a.prdordtype,n.bank_name, " + 
						" a.orderTime  datetime,a.ordertime, " + 
						" case " + 
						" when b.mulamt is null then " + 
						" to_char(b.txamt / 100,  999999990.99) " + 
						" else " + 
						" to_char(b.mulamt / 100,  999999990.99) " + 
						" end ordamt  " + 
						" from stpprdinf a " + 
						" left join stppayinf b " + 
						" on a.payordno = b.payordno " + 
						" left join StpPayInf m " + 
						" on trim(a.payordno) = trim(m.payordno) " + 
						" left join CoopBank n " + 
						" on substr(m.bankcod, 0, 2) = substr(TRIM(N.BANK_CODE),0,2) " + 
						" where a.prdordtype in ('0', '2') " + 
						" and b.ordstatus = '01' " + 
						" and m.paytype in ('01', '04')  " +						
						" and (a.orderTime) like '" + DATETIME + "%' and n.bank_name = '" + BANK_NAME +"'");
			}else if(STATUS.equals("02")){
				sql = new StringBuffer("select a.prdordno,a.payordno,a.prdordtype,n.bank_name, " + 
						" a.orderTime datetime,a.ordertime, " + 
						" case " + 
						" when b.mulamt is null then " + 
						" to_char(b.rfamt / 100,  999999990.99) " + 
						" else " + 
						" to_char(b.mulamt / 100,  999999990.99) " + 
						" end ordamt " + 
						" from stpprdinf a " + 
						" left join stprefinf b " + 
						" on a.prdordno = b.oriprdordno " + 
						" left join StpPayInf m " + 
						" on trim(a.payordno) = trim(m.payordno) " + 
						" left join CoopBank n " + 
						" on substr(m.bankcod, 0, 2) = substr(TRIM(N.BANK_CODE),0,2) " + 
						" where a.prdordtype in ('0', '2') " + 
						" and a.ordstatus = '05' " + 
						" and m.paytype in ('01', '04') " +  
						" and (a.orderTime) like '" + DATETIME + "%' and n.bank_name = '" + BANK_NAME +"'");
			}else{
				sql = new StringBuffer("select a.payordno,a.prdordtype,a.prdordno,c.bank_name, b.orderTime datetime, b.ordertime, to_char(b.ordamt / 100,  999999990.99) ordamt " + 
						" from stppayinf a " + 
						" left join stpprdinf b " + 
						" on (trim(a.payordno) = trim(b.payordno)) " + 
						" left join coopbank c " + 
						" on substr(a.bankcod, 0, 2) = substr(TRIM(C.BANK_CODE),0,2) " + 
						" where a.paytype = '01' " + 
						" and b.ordstatus = '01' " + 
						" and (b.prdordtype = '1' or b.prdordtype = '3') " + 
						" and (b.orderTime) like '" + DATETIME + "%' and c.bank_name = '" + BANK_NAME +"'");
			}
			if (StringUtils.isNotEmpty(PAYORDNO)) {
				sql.append(" and a.payordno like '%" + PAYORDNO + "%'");
			}
			if (StringUtils.isNotEmpty(PRDORDNO)) {
				sql.append(" and a.prdordno like '%" + PRDORDNO + "%'");
			}
			Log.info("执行的SQL为：%s", sql);
	
		}
		else{
			Etf.setChildValue("RspCod", "200005");
			Etf.setChildValue("RspMsg", "获取当前状态失败！");
			Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/reportable/orginfobyday.jsp"); 
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
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
				"/WEB-INF/html/reportable/orginfobyday.jsp");
	}
	/**
	 * Excel导出
	 * 
	 * @throws Exception
	 */
	@Code("S02005") 
	public void doExport() throws Exception {
		String sql = getSql();
		/*总计*/
		StringBuffer total_Sql = new StringBuffer("select to_char(sum(nvl(j.ordamt,0)), 999999990.99) SHJE,sum(nvl(j.numb,0)) SHBS," +
				" to_char(sum(nvl(j.ordamt1,0)), 999999990.99) TKJE,sum(nvl(j.numb1,0)) TKBS," +
				" to_char(sum(nvl(j.ordamt2,0)), 999999990.99) CZJE,sum(nvl(j.numb2,0)) CZBS " +
				" from (").append(sql).append(") j");
		int result = Atc.QueryInGroup(sql.toString(), "REC", null);	
		if (result == 0||result == 2) {
			int result_all = Atc.ReadRecord(total_Sql.toString());
			if (result_all == 0||result_all == 2) {
				String[] str_all = new String[]{"合计","","￥"+Etf.getChildValue("SHJE"),Etf.getChildValue("SHBS"),"￥"+Etf.getChildValue("TKJE"),Etf.getChildValue("TKBS"),"￥"+Etf.getChildValue("CZJE"),Etf.getChildValue("CZBS")};
				List<List<String>> inverList = getTermList();
				Log.info("=====inverList "+inverList.size());
				ExcelUtils.addValue("inverList", inverList);
				String fileName = "OrgTranByDaySchedule_" + Utils.getCurrentDateTime() + ".xls";
				Utils.FtpPut("OrgTranByDaySchedule", fileName);
				Other o = new Other();
				String path = o.createExcel(inverList, new String[]{"交易日期","银行（支付渠道）名称","消费总金额","消费总笔数","退款总金额","退款总笔数","支付总金额","支付总笔数",},str_all, "银行每日交易汇总", "/home/payment/apache-tomcat-7.0.64/webapps/pay/dat/download/" + fileName);
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
		List orderList = new ArrayList<OrgTranBean>();
		for (int i = 0; i < list.size(); i++) {
			List<String> list2 = new ArrayList<String>();
			OrgTranBean sb = new OrgTranBean();
			Element em = (Element) list.get(i);
			sb.setDATETIME(Utils.getElementTextByKey(em, "DATETIME"));
			sb.setBANK_NAME(Utils.getElementTextByKey(em, "BANK_NAME"));
			sb.setORDAMT("￥"+Utils.getElementTextByKey(em, "ORDAMT"));
			sb.setNUMB(Utils.getElementTextByKey(em, "NUMB"));
			sb.setORDAMT1("￥"+Utils.getElementTextByKey(em, "ORDAMT1"));
			sb.setNUMB1(Utils.getElementTextByKey(em, "NUMB1"));
			sb.setORDAMT2("￥"+Utils.getElementTextByKey(em, "ORDAMT2"));
			sb.setNUMB2(Utils.getElementTextByKey(em, "NUMB2"));
			if(null==sb.getDATETIME()||""==sb.getDATETIME()||"".equals(sb.getDATETIME()))
				sb.setDATETIME("-");
			if(null==sb.getBANK_NAME()||""==sb.getBANK_NAME()||"".equals(sb.getBANK_NAME()))
				sb.setBANK_NAME("-");
			if(null==sb.getORDAMT()||"￥"==sb.getORDAMT()||"￥".equals(sb.getORDAMT()))
				sb.setORDAMT("￥0.00");
			if(null==sb.getNUMB()||""==sb.getNUMB()||"".equals(sb.getNUMB()))
				sb.setNUMB("0");
			if(null==sb.getORDAMT1()||"￥"==sb.getORDAMT1()||"￥".equals(sb.getORDAMT1()))
				sb.setORDAMT1("￥0.00");
			if(null==sb.getNUMB1()||""==sb.getNUMB1()||"".equals(sb.getNUMB1()))
				sb.setNUMB1("0");
			if(null==sb.getORDAMT2()||"￥"==sb.getORDAMT2()||"￥".equals(sb.getORDAMT2()))
				sb.setORDAMT2("￥0.00");
			if(null==sb.getNUMB2()||""==sb.getNUMB2()||"".equals(sb.getNUMB2()))
				sb.setNUMB2("0");
			
			list2.add(sb.getDATETIME());
			list2.add(sb.getBANK_NAME());
			list2.add(sb.getORDAMT());
			list2.add(sb.getNUMB());
			list2.add(sb.getORDAMT1());
			list2.add(sb.getNUMB1());
			list2.add(sb.getORDAMT2());
			list2.add(sb.getNUMB2());
			orderList.add(list2);
		}
		return orderList;
	}
}
