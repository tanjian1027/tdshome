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
import com.bean.MerSeltBean;
import com.tangdi.util.GetOwnButton;
import com.util.Utils;
import com.util.Other;

@DB
public class MerSeltbyMon {
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
	 * 商品订单号
	 */
	@Data
	private String PRDORDNO;
	
	public String getSql(){
		StringBuffer sql = new StringBuffer("SELECT c.cust_id, " + 
				" c.cust_name, " + 
				" substr(a.STL_DATE, 1, 4)||'-'||substr(a.STL_DATE, 5, 2) datetime, " + 
				" to_char((sum(a.stl_amt)) / 100,  'FM9999,999,999,990.00') STL_AMT, " + 
				" to_char(sum(a.TOT_TXN_FEE) / 100,  'FM9999,999,999,990.00') STL_FEE, " + 
				" count(*) numb " + 
				" FROM ARP_BANK_PAY_STL a " + 
				" left join STPCUSINF c " + 
				" on a.MER_NO = c.cust_id " + 
				" WHERE a.pay_stl_status = '2' " );
		if (StringUtils.isNotEmpty(MERNO)) {
			sql.append(" and c.cust_id like '%" + MERNO + "%'");
		}
		if (StringUtils.isNotEmpty(MERCNAM)) {
			sql.append(" and c.cust_name like '%" + MERCNAM + "%'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isEmpty(APPDAY_END)) {
			sql.append(" and a.STL_DATE>='" + APPDAY_START.substring(0,4) + APPDAY_START.substring(5,7) + "00000000"  
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_END) && StringUtils.isEmpty(APPDAY_START)) {
			sql.append(" and a.STL_DATE<='" + APPDAY_END.substring(0,4) + APPDAY_END.substring(5,7) + "31235959"  
					+ "'");
		}
		if (StringUtils.isNotEmpty(APPDAY_START) && StringUtils.isNotEmpty(APPDAY_END)) {
			sql.append(" and a.STL_DATE>='" + APPDAY_START.substring(0,4) + APPDAY_START.substring(5,7) + "00000000"   
					+ "' and a.STL_DATE<='" + APPDAY_END.substring(0,4) + APPDAY_END.substring(5,7) + "31235959"  
					+ "'");
		}
		sql.append(" group by c.cust_id, c.cust_name, substr(a.STL_DATE, 1, 4)||'-'||substr(a.STL_DATE, 5, 2)");
		return sql.toString();
	}
	
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01009")
	public void getList() throws Exception {
		Log.info("每个商户每月结算信息：结算笔数、结算金额、结算手续费");
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
			StringBuffer total_Sql = new StringBuffer("select to_char(sum(g.STL_AMT),  'FM9999,999,999,990.00') JSJE,sum(g.numb) JSBS,to_char(sum(g.STL_FEE),  'FM9999,999,999,990.00') SFEE from (").append(sql).append(") g");
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
				"/WEB-INF/html/reportable/merseltbymon.jsp");
	}
	/**
	 * 列表页面
	 * 
	 * @throws Exception
	 */
	@Code("S01090")
	public void getListInfo() throws Exception {
		Log.info("商户每日消费、退款明细");
		String sql = "select t.MERNO,t.prdordno,t.ORDERTIME,to_char(t.ordamt / 100, 'FM9999,999,999,990.00') ordamt,t.PAYORDNO,t.PRDORDTYPE " +
				"from stpprdinf t where t.merno=#{MERNO} and t.ordertime like '"+ ORDERTIME +"%' and t.ordstatus=#{ORDSTATUS} ";
		if (StringUtils.isNotEmpty(PAYORDNO)) {
			sql += " and t.payordno like '%" + PAYORDNO + "%'";
		}
		if (StringUtils.isNotEmpty(PRDORDNO)) {
			sql += " and t.prdordno like '%" + PRDORDNO + "%'";
		}
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
				"/WEB-INF/html/reportable/merinfobyday.jsp");
	}
	/**
	 * Excel导出
	 * 
	 * @throws Exception
	 */
	@Code("S02009") 
	public void doExport() throws Exception {
		String sql = getSql();
		/*总计*/
		StringBuffer total_Sql = new StringBuffer("select to_char(sum(g.STL_AMT),  'FM9999,999,999,990.00') JSJE,sum(g.numb) JSBS,to_char(sum(g.STL_FEE),  'FM9999,999,999,990.00') SFEE from (").append(sql).append(") g");
		int result = Atc.QueryInGroup(sql.toString(), "REC", null);	
		if (result == 0||result == 2) {
			int result_all = Atc.ReadRecord(total_Sql.toString());
			if (result_all == 0||result_all == 2) {
				String[] str_all = new String[]{"合计","","","￥"+Etf.getChildValue("JSJE"),Etf.getChildValue("JSBS"),"￥"+Etf.getChildValue("SFEE")};
				List<List<String>> inverList = getTermList();
				Log.info("=====inverList "+inverList.size());
				ExcelUtils.addValue("inverList", inverList);
				String fileName = "MerSeltBeanSchedule_" + Utils.getCurrentDateTime() + ".xls";
				Other o = new Other();
				String path = o.createExcel(inverList, new String[]{"交易时间","商户编号","商户名称","结算总金额","手续费总金额","结算发起笔数"},str_all, "商户每月交易结算汇总", "/home/payment/apache-tomcat-7.0.64/webapps/pay/dat/download/" + fileName);
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
		List orderList = new ArrayList<MerSeltBean>();
		for (int i = 0; i < list.size(); i++) {
			List<String> list2 = new ArrayList<String>();
			MerSeltBean sb = new MerSeltBean();
			Element em = (Element) list.get(i);
			sb.setDATETIME(Utils.getElementTextByKey(em, "DATETIME"));
			sb.setCUST_ID(Utils.getElementTextByKey(em, "CUST_ID"));
			sb.setCUST_NAME(Utils.getElementTextByKey(em, "CUST_NAME"));
			sb.setSTL_AMT("￥"+Utils.getElementTextByKey(em, "STL_AMT"));
			sb.setSTL_FEE("￥"+Utils.getElementTextByKey(em, "STL_FEE"));
			sb.setNUMB(Utils.getElementTextByKey(em, "NUMB"));
			if(null==sb.getDATETIME()||""==sb.getDATETIME()||"".equals(sb.getDATETIME()))
				sb.setDATETIME("-");
			if(null==sb.getCUST_ID()||""==sb.getCUST_ID()||"".equals(sb.getCUST_ID()))
				sb.setCUST_ID("-");
			if(null==sb.getCUST_NAME()||""==sb.getCUST_NAME()||"".equals(sb.getCUST_NAME()))
				sb.setCUST_NAME("-");
			if(null==sb.getSTL_AMT()||"￥"==sb.getSTL_AMT()||"￥".equals(sb.getSTL_AMT()))
				sb.setSTL_AMT("￥0.00");
			if(null==sb.getSTL_FEE()||"￥"==sb.getSTL_FEE()||"￥".equals(sb.getSTL_FEE()))
				sb.setSTL_FEE("￥0.00");
			if(null==sb.getNUMB()||""==sb.getNUMB()||"".equals(sb.getNUMB()))
				sb.setNUMB("0");

			list2.add(sb.getDATETIME());
			list2.add(sb.getCUST_ID());
			list2.add(sb.getCUST_NAME());
			list2.add(sb.getSTL_AMT());
			list2.add(sb.getSTL_FEE());
			list2.add(sb.getNUMB());
			orderList.add(list2);
		}
		return orderList;
	}
}
