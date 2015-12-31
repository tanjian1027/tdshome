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
import com.bean.MerTranBean;
import com.tangdi.util.GetOwnButton;
import com.util.Utils;
import com.util.Other;

@DB
public class MerTranbyMon {
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
	 * 订单支付/退款成功时间
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
	 * 区分01-消费，02-退款
	 */
	@Data
	private String STATUS;
	
	public String getSql(){
		//商品订单支付成功，每个商户每月一条记录 
		StringBuffer sql_pay = new StringBuffer("" + 
				" SELECT t.merno, " + 
				" sum(a.txamt) amt, " + 
				" substr(a.ActDat, 1, 6) datetime, " + 
				" count(*) numb " + 
				" from stpprdinf t " + 
				" left join stppayinf a " + 
				" on t.prdordno = a.prdordno " + 
				" where t.prdOrdType in ('0', '2') " + 
				" and a.ordstatus = '01' " + 
				" group by t.merno, substr(a.ActDat, 1, 6) ");
		//商品订单退款成功，每个商户每月一条记录 
		StringBuffer sql_prd = new StringBuffer("" + 
				" select n.merno, " + 
				" sum(b.rfamt) amt, " + 
				" substr(b.rfordtime, 1, 6) datetime, " + 
				" count(*) numb " + 
				" from stpprdinf n " + 
				" left join StpRefInf b " + 
				" on n.prdordno = b.oriprdordno " + 
				" where n.prdOrdType in ('0', '2') " + 
				" and b.ordstatus = '02' " + 
				" group by n.merno, substr(b.rfordtime, 1, 6) " );
		//汇总
		StringBuffer sql = new StringBuffer("select distinct x.merno,c.cust_name,c.cust_reg_date,x.datetime,to_char(y.amt / 100,  'FM9999,999,999,990.00') amt,y.numb,to_char(z.amt / 100,  'FM9999,999,999,990.00') amt1,z.numb numb1 from " +
				" (" + sql_pay + " union " + sql_prd + ") x " +
				" left join (" + sql_pay + ")  y on x.merno=y.merno and x.datetime=y.datetime " +
 				" left join (" + sql_prd + ")  z on x.merno=z.merno and x.datetime=z.datetime " +
 			    " left join STPCUSINF c on c.cust_id = x.merno " +
				" where 1=1  and x.datetime is not null " ); 
		if (StringUtils.isNotEmpty(MERNO)) {
			sql.append(" and x.merno like '%" + MERNO + "%'");
		}
		if (StringUtils.isNotEmpty(MERCNAM)) {
			sql.append(" and c.cust_name like '%" + MERCNAM + "%'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isEmpty(APPDAY_END)) {
			sql.append(" and x.datetime>='" + APPDAY_START.substring(0,4) + APPDAY_START.substring(5,7) 
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_END) && StringUtils.isEmpty(APPDAY_START)) {
			sql.append(" and x.datetime<='" + APPDAY_END.substring(0,4) + APPDAY_END.substring(5,7) 
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isNotEmpty(APPDAY_END)) {
			sql.append(" and x.datetime>='" + APPDAY_START.substring(0,4) + APPDAY_START.substring(5,7) 
					+ "' and x.datetime<='" + APPDAY_END.substring(0,4) + APPDAY_END.substring(5,7) 
					+ "'");
		}
		sql.append(" order by x.datetime desc ");
		return sql.toString();
	}
	
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01002")
	public void getList() throws Exception {
		Log.info("每个商户每月交易情况统计，支付成功笔数金额、退款笔数金额等汇总");
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
			StringBuffer total_Sql = new StringBuffer("select to_char(sum(g.amt),  'FM9999,999,999,990.00') XFJE,sum(g.numb) XFBS,to_char(sum(nvl(g.amt1,0)),  'FM9999,999,999,990.00') TKJE,sum(nvl(g.numb1,0)) TKBS from (").append(sql).append(") g");
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
				"/WEB-INF/html/reportable/mertranbymon.jsp");
	}
	
	/**
	 * Excel导出
	 * 
	 * @throws Exception
	 */
	@Code("S02002") 
	public void doExport() throws Exception {
		String sql = getSql();
		StringBuffer total_Sql = new StringBuffer("select to_char(sum(g.amt),  'FM9999,999,999,990.00') XFJE,sum(g.numb) XFBS,to_char(sum(nvl(g.amt1,0)),  'FM9999,999,999,990.00') TKJE,sum(nvl(g.numb1,0)) TKBS from (").append(sql).append(") g");
		int result =  Atc.QueryInGroup(sql.toString(), "REC", null);	
		if (result == 0||result == 2) {
			int result_all = Atc.ReadRecord(total_Sql.toString());
			if (result_all == 0||result_all == 2) {
				String[] str_all = new String[]{"合计","","","￥"+Etf.getChildValue("XFJE"),Etf.getChildValue("XFBS"),"￥"+Etf.getChildValue("TKJE"),Etf.getChildValue("TKBS")};
				List<List<String>> inverList = getTermList();
				Log.info("=====inverList "+inverList.size());
				ExcelUtils.addValue("inverList", inverList);
				String fileName = "MerTranByMonSchedule_" + Utils.getCurrentDateTime() + ".xls";
				Other o = new Other();
				String path = o.createExcel(inverList, new String[]{"商户编号","商户名称","交易月份","消费总金额","消费总笔数","退款总金额","退款总笔数","商户开户日期"},str_all, "商户每月交易汇总", "/home/payment/apache-tomcat-7.0.64/webapps/pay/dat/download/" + fileName);
				Log.info("=====path "+path);
				Msg.set("_REQUESTATTR.REDIRECTURL", "dat/download/" + fileName);//给页面一个连接下载文件
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
		List orderList = new ArrayList<MerTranBean>();
		for (int i = 0; i < list.size(); i++) {
			List<String> list2 = new ArrayList<String>();
			MerTranBean sb = new MerTranBean();
			Element em = (Element) list.get(i);
			sb.setMERNO(Utils.getElementTextByKey(em, "MERNO"));
			sb.setCUST_NAME(Utils.getElementTextByKey(em, "CUST_NAME"));
			sb.setDATETIME(Utils.getElementTextByKey(em, "DATETIME"));
			sb.setAMT("￥"+Utils.getElementTextByKey(em, "AMT"));
			sb.setNUMB(Utils.getElementTextByKey(em, "NUMB"));
			sb.setAMT1("￥"+Utils.getElementTextByKey(em, "AMT1"));
			sb.setNUMB1(Utils.getElementTextByKey(em, "NUMB1"));			
			sb.setCUST_REG_DATE(Utils.getElementTextByKey(em, "CUST_REG_DATE"));			
			if(null==sb.getMERNO()||""==sb.getMERNO()||"".equals(sb.getMERNO()))
				sb.setMERNO("-");
			if(null==sb.getCUST_NAME()||""==sb.getCUST_NAME()||"".equals(sb.getCUST_NAME()))
				sb.setCUST_NAME("-");
			if(null==sb.getDATETIME()||""==sb.getDATETIME()||"".equals(sb.getDATETIME()))
				sb.setDATETIME("-");
			if(null==sb.getAMT()||"￥"==sb.getAMT()||"￥".equals(sb.getAMT()))
				sb.setAMT("￥0.00");
			if(null==sb.getNUMB()||""==sb.getNUMB()||"".equals(sb.getNUMB()))
				sb.setNUMB("0");
			if(null==sb.getAMT1()||"￥"==sb.getAMT1()||"￥".equals(sb.getAMT1()))
				sb.setAMT1("￥0.00"); 
			if(null==sb.getNUMB1()||""==sb.getNUMB1()||"".equals(sb.getNUMB1()))
				sb.setNUMB1("0");
			if(null==sb.getCUST_REG_DATE()||""==sb.getCUST_REG_DATE()||"".equals(sb.getCUST_REG_DATE()))
				sb.setCUST_REG_DATE("-");
			
			list2.add(sb.getMERNO());
			list2.add(sb.getCUST_NAME());
			list2.add(sb.getDATETIME());
			list2.add(sb.getAMT());
			list2.add(sb.getNUMB());
			list2.add(sb.getAMT1());
			list2.add(sb.getNUMB1());
			list2.add(sb.getCUST_REG_DATE());
			orderList.add(list2);
		}
		return orderList;
	}
}
