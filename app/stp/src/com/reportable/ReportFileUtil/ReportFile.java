package com.reportable.ReportFileUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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

import com.reportable.ReportFileUtil.Util.CCIF;
import com.reportable.ReportFileUtil.Util.CTIF;
import com.reportable.ReportFileUtil.Util.CreateXML;
import com.reportable.ReportFileUtil.Util.FileUtiles;
import com.reportable.ReportFileUtil.Util.RBIF;
import com.reportable.ReportFileUtil.Util.STIF;
import com.reportable.ReportFileUtil.Util.createExcel;
import com.tangdi.des.DES;

@DB
public class ReportFile {
	
	@Data
	private String FileId;
	@Data
	private String downFileId;
	@Data
	private String pageNum;
	@Data
	private String NUMPERPAGE;
	@Data
	private String PAGENUMSHOWN;
	@Data
	private String fileType;
	@Data
	private String CTVC;
	@Data
	private String startDate;
	@Data
	private String endDate;
	@Data
	private String RINM; // 报告机构名称
	@Data
	private String FICD; //报告机构分支机构/网点代码
	@Data
	private String FIRC; // 报告机构�?��地区编码
	@Data
	private String RFSG; // 报�?次数标志
	@Data
	private String ORXN; // 初次报�?的可疑交易报文名�?
	@Data
	private String SSTM; // 可疑交易处理情况
	@Data
	private String STCR; // 可疑交易特征
	@Data
	private String SSDS; // 可疑交易描述
	@Data
	private String UDSI; // 机构自定可疑交易标准编号
	@Data
	private String CPBA; // 支付机构银行账号
	@Data
	private String CPBN; // 支付机构的银行账号的开户银行名称
	@Data
	private String FILENAME; // 文件名
	@Data
	private String FILETYPE; // 文件类型
	@Data
	private String CREATEFILESTARTDATE; // 创建文件开始时间
	@Data
	private String CREATEFILEENDDATE; // 创建文件结束时间

	/**
	 * 	S540802
	 * 跳转到添加页面
	 * @throws Exception
	 */
	@Code("S540802")
	public void getList() throws Exception {
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
				"/WEB-INF/html/reportable/ReportFileUtil/reportfile.jsp");
	}

	/**
	 * S540805
	 * 添加下载日志
	 */
	@Code("S540805")
	public void downNum() {
		System.out.println("-------下载记录-----");
		if(StringUtils.isEmpty(downFileId)){
			String sql= "update stp_reportFile_log set download_num = download_num + 1 where id = "+downFileId;
		}
	}
	
	/**
	 * 获取支付报文列表
	 */
	@Code("S540806")
	public void getDealsFileList(){
		StringBuffer reportFileSql = new StringBuffer(
				"select * from stp_reportfile_log where file_type = 'deals' ");
		if (StringUtils.isNotEmpty(FILENAME)) {
			reportFileSql.append(" and file_name like '%" + FILENAME + "%'");
		}
//		if (StringUtils.isNotEmpty(FILETYPE) && !"-1".equals(FILETYPE)) {
//			reportFileSql.append(" and file_type = '" + FILETYPE + "'");
//		}
//		if (StringUtils.isNotEmpty(CREATEFILESTARTDATE)
//				&& StringUtils.isNotEmpty(CREATEFILEENDDATE)) {
//			reportFileSql.append(" and create_file_time between '"
//					+ CREATEFILESTARTDATE + "000000' and '" + CREATEFILEENDDATE
//					+ "235959'");
//		}
		reportFileSql.append(" order by create_file_time desc");
		Log.info("sql=%s", new Object[] { reportFileSql });
		int req = Atc.PagedQuery(this.pageNum, this.NUMPERPAGE,
				"DEALFILELIST", reportFileSql.toString());
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/reportable/ReportFileUtil/fileList.jsp");
	}

	/**
	 * S540803
	 * 生成xml报告文件
	 * 
	 * @throws Exception
	 */
	@Code("S540803")
	public void createXml() throws Exception {
		System.out.println("生成报文开始");
		// 报文类型
		System.out.println("根据报文类型查询异常交易");
		int req = getDealSql(fileType);
		System.out.println("查询执行结果:"+req);
		if (req == 0) {
			RBIF rbif = new RBIF();
			rbif.setRINM(RINM); // 报告机构名称
			rbif.setFIRC(FIRC); // 报告机构�?��地区编码
			rbif.setRFSG(RFSG); // 报�?次数标志
			rbif.setORXN(ORXN); // 初次报�?的可疑交易报文名�?
			rbif.setSSTM(SSTM); // 可疑交易处理情况
			rbif.setSTCR(STCR); // 可疑交易特征
			rbif.setSSDS(SSDS); // 可疑交易描述
			rbif.setUDSI(UDSI); // 机构自定可疑交易标准编号
			rbif.setFICD(FICD);	// 报告机构分支机构/网点代码

			// 封装报文信息
			System.out.println("---------------封装报文信息-----------");
			rbif = getRbif(rbif, fileType);

			// 生成报文
			System.out.println("-----------开始生成报文--------------");
			CreateXML cx = new CreateXML();
			
			String fileName = cx.createNPS(rbif, fileType,FileId);

			System.out.println("--------------生成结束 记录日志---------------");
			addFileLog(fileName, fileType);

			System.out.println("---------------复制文件----------------");
		    // 读取XML配置文件中的数据，添加到ETF树上 
		    // 0 成功; -1参数错误;2 取XML配置父节点失败 
			int ret = tangdi.atc.TdFile.ReadXmlConfig("etc/public/STP_CONFIG.XML","FtpPutPay", null, null, null);
			if (ret != 0){
				Log.error("读取配置文件失败，返回值[%s]", ret);
				Etf.setChildValue("MsgTyp", "E");
				Etf.setChildValue("RspCod", "P0002");
				Etf.setChildValue("RspMsg", "查询失败");
				Etf.setChildValue("DWZ_STATUS_CODE", "300");
				Etf.setChildValue("DWZ_RSP_MSG", "查询失败");
				return ;
			}
			ret = com.tangdi.production.tdbase.util.FileFun.moveFile(Etf.getChildValue("LclDir"), fileName, Etf.getChildValue("ObjDir"));
			if (ret != 0){
				Log.error("移动文件失败，返回值[%s]", ret);
				Etf.setChildValue("MsgTyp", "E");
				Etf.setChildValue("RspCod", "P0002");
				Etf.setChildValue("RspMsg", "查询失败");
				Etf.setChildValue("DWZ_STATUS_CODE", "300");
				Etf.setChildValue("DWZ_RSP_MSG", "查询失败");
				return ;
			}
			
			if(!"behavior".equals(fileType)){
				System.out.println("---------------生成 xml 文件 --------------");
				fileName = createExcel.createEx(rbif, fileName);
				System.out.println("---------------复制文件----------------");
				ret = com.tangdi.production.tdbase.util.FileFun.moveFile(Etf.getChildValue("LclDir"), fileName, Etf.getChildValue("ObjDir"));
				if (ret != 0){
					Log.error("移动文件失败，返回值[%s]", ret);
					Etf.setChildValue("MsgTyp", "E");
					Etf.setChildValue("RspCod", "P0002");
					Etf.setChildValue("RspMsg", "查询失败");
					Etf.setChildValue("DWZ_STATUS_CODE", "300");
					Etf.setChildValue("DWZ_RSP_MSG", "查询失败");
					return ;
				}
			}

			Etf.setChildValue("MsgTyp", "N");
			Etf.setChildValue("RspCod", "00000");
			Etf.setChildValue("RspMsg", "报文生成完成");
			Etf.setChildValue("DWZ_STATUS_CODE", "200");
			Etf.setChildValue("DWZ_RSP_MSG", "报文生成完成");
			Etf.setChildValue("DWZ_CALLBACK_TYPE", "closeCurrent");
		} else if (req == 2) {
			Etf.setChildValue("MsgTyp", "M");
			Etf.setChildValue("RspCod", "P0001");
			Etf.setChildValue("RspMsg", "表中无记录");
			Etf.setChildValue("DWZ_STATUS_CODE", "400");
			Etf.setChildValue("DWZ_RSP_MSG", "无信息");
		} else {
			Etf.setChildValue("MsgTyp", "E");
			Etf.setChildValue("RspCod", "P0002");
			Etf.setChildValue("RspMsg", "查询失败");
			Etf.setChildValue("DWZ_STATUS_CODE", "300");
			Etf.setChildValue("DWZ_RSP_MSG", "数据库操作错误");
		}
		System.out.println("--------------生成结束-------------");
		//Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
				//"/WEB-INF/html/reportable/ReportFileUtil/reportfileList.jsp");
	}

	/**
	 * S540804
	 * 历史报文列表
	 */
	@Code("S540804")
	public void getReportFileList() {
		StringBuffer reportFileSql = new StringBuffer(
				"select * from stp_reportfile_log where 1=1");
		if (StringUtils.isNotEmpty(FILENAME)) {
			reportFileSql.append(" and file_name like '%" + FILENAME + "%'");
		}
		if (StringUtils.isNotEmpty(FILETYPE) && !"-1".equals(FILETYPE)) {
			reportFileSql.append(" and file_type = '" + FILETYPE + "'");
		}
		if (StringUtils.isNotEmpty(CREATEFILESTARTDATE)
				&& StringUtils.isNotEmpty(CREATEFILEENDDATE)) {
			reportFileSql.append(" and create_file_time between '"
					+ CREATEFILESTARTDATE + "000000' and '" + CREATEFILEENDDATE
					+ "235959'");
		}
		reportFileSql.append(" order by create_file_time desc");
		// this.NUMPERPAGE = "19";
		// this.PAGENUMSHOWN = "5";
		Log.info("sql=%s", new Object[] { reportFileSql });
		int req = Atc.PagedQuery(this.pageNum, this.NUMPERPAGE,
				"REPORTFILELIST", reportFileSql.toString());
		// if (req == 0) {
		// Etf.setChildValue("MsgTyp", "N");
		// Etf.setChildValue("RspCod", "00000");
		// Etf.setChildValue("RspMsg", "查询成功");
		// } else if (req == 2) {
		// Etf.setChildValue("MsgTyp", "M");
		// Etf.setChildValue("RspCod", "P0001");
		// Etf.setChildValue("RspMsg", "表中无记录");
		// Etf.setChildValue("DWZ_STATUS_CODE", "400");
		// Etf.setChildValue("DWZ_RSP_MSG", "无信息");
		// } else {
		// Etf.setChildValue("MsgTyp", "E");
		// Etf.setChildValue("RspCod", "P0002");
		// Etf.setChildValue("RspMsg", "查询失败");
		// Etf.setChildValue("DWZ_STATUS_CODE", "300");
		// Etf.setChildValue("DWZ_RSP_MSG", "数据库操作错误");
		// }

		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,
				"/WEB-INF/html/reportable/ReportFileUtil/reportfileList.jsp");
	}

	/**
	 * 报文日志生成
	 * 
	 * @param fileName
	 *            文件名称
	 * @param fileUrl
	 *            文件目录
	 * @param fileType
	 *            文件类型
	 * @return
	 */
	public String addFileLog(String fileUrl, String fileType) {
		String fileName = fileUrl.indexOf("/") > -1 ? fileUrl.substring(fileUrl
				.lastIndexOf("/") + 1) : fileUrl.substring(fileUrl
				.lastIndexOf("\\\\") + 1);
		String createFileUser = Etf.getChildValue("SESSIONS.UID");
		String createFileTime = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String sql = "insert into stp_reportfile_log "
				+ "values (reportfileseq.nextval,'" + fileName + "','"
				+ createFileTime + "','" + fileUrl + "','" + fileType + "',0,'"
				+ createFileUser + "')";

		int req = Atc.ExecSql(null, sql);
		// Log.info("报文日志生成SQL：%s(结果：%s)",sql,req);

		return null;
	}

	/**
	 * 获取交易类异常报文数据
	 * 
	 * @return 执行结果
	 */
	public int getDealSql(String fileType) {
		ResourceBundle rb = ResourceBundle.getBundle("ReportFile");
		StringBuffer sb = new StringBuffer();
		String dealRuleCode = rb.getString("deal_rule_code") == null ? "Rule1008"
				: rb.getString("deal_rule_code");
		sb.append("select * from RCS_EXCEPT_TRAN_INFO where regdt_day between '"
				+ startDate + "' and '" + endDate + "' and ");
		if (!"behavior".equals(fileType)) {
			if (dealRuleCode != null && dealRuleCode.indexOf(",") > -1) {
				String[] dealRuleCodes = dealRuleCode.split(",");
				for (int i = 0; i < dealRuleCodes.length; i++) {
					sb.append("rule_code = '" + dealRuleCodes[i] + "'");
					if (i < dealRuleCodes.length - 1) {
						sb.append(" or ");
					}
				}
			} else {
				sb.append("rule_code = '" + dealRuleCode + "'");
			}
		} else {
			if (dealRuleCode != null && dealRuleCode.indexOf(",") > -1) {
				String[] dealRuleCodes = dealRuleCode.split(",");
				for (int i = 0; i < dealRuleCodes.length; i++) {
					sb.append("rule_code != '" + dealRuleCodes[i] + "'");
					if (i < dealRuleCodes.length - 1) {
						sb.append(" and ");
					}
				}
			} else {
				sb.append("rule_code != '" + dealRuleCode + "'");
			}
		}
		System.out.println("异常交易sql>>>>"+sb.toString());
		int req = Atc.QueryInGroup(sb.toString(), "ReprotFile", null);

		return req;
	}

	/**
	 * 根据信息封装 基础信息类
	 * 
	 * @param rbif
	 * @return
	 * @throws Exception
	 */
	public RBIF getRbif(RBIF rbif, String fileType) throws Exception {

		// 可疑交易主体信息集合
		List<CTIF> ctifList = new ArrayList();
		// 可以交易信息集合
		List<STIF> stifList = new ArrayList();
		List<?> list = Etf.childs("ReprotFile");

		// 记录可以主体信息
		List<String> userCodeList = new ArrayList();
		// 遍历交易信息
		for (int i = 0; i < list.size(); i++) {
			Element emt = (Element) list.get(i);
			String userCode = emt.elementText("USER_CODE");
			String tranSource = emt.elementText("ENTITY_TYPE");
			// 验证是否已经记录了该主体的信息
			if (!userCodeList.contains(userCode)) {
				// 构建可疑主体信息
				ctifList = getCtifList(userCode, ctifList, tranSource);
				userCodeList.add(userCode);
			}
			// 可疑行为不需要查询交易信息
			if (!"behavior".equals(fileType)) {
				// 可疑交易信息
				stifList = getStifList(emt.elementText("ID"), stifList);
			}
		}
		rbif.setCTIFs(ctifList);
		rbif.setSTIFs(stifList);
		rbif.setSCTN(String.valueOf(ctifList.size()));
		rbif.setTTNM(String.valueOf(stifList.size()));

		return rbif;
	}

	/**
	 * 构建可疑详细信息
	 * 
	 * @param id
	 *            交易码
	 * @param stifList
	 *            可疑详细信息集合
	 * @return
	 * @throws Exception
	 */
	public List<STIF> getStifList(String id, List<STIF> stifList)
			throws Exception {
		String stifSql = "select  r.TRAN_CODE,r.TRAN_TYPE from RCS_EXCEPT_TRAN_DETAIL_INFO d inner join RCS_TRAN_SERIAL_RECORD r on d.tran_code = r.id  where d.id = '"
				+ id + "'";
		int req = Atc.QueryInGroup(stifSql, "_Stif", null);
		if (0 == req) {
			List<?> stifsList = Etf.childs("_Stif");
			for (int j = 0; j < stifsList.size(); j++) {
				// 创建可疑交易信息
				STIF stif = new STIF();
				Element stifEmt = (Element) stifsList.get(j);
				String tranCode = stifEmt.elementText("TRAN_CODE");
				String tranType = stifEmt.elementText("TRAN_TYPE");
				// 充值 || 消费
				if ("1".equals(tranType) || "2".equals(tranType)) {
					stifSql = "select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pa.ACTDAT,pa.PRDNAME,pa.TXAMT "
							+ " from stpprdinf pr "
							+ " left join stppayinf pa on pr.payordno = pa.payordno "
							+ " left join stpusrinf us on pa.cust_id = us.cust_id "
							+ " left join stpcusinf cu on us.cust_id = cu.cust_id"
							+ " left join stpbanktb tb on tb.cust_id = concat(cu.cust_id,'01') "
							+ " where pr.payordno = '"
							+ tranCode
							+ "' and rownum = 1 and tb.card_no is not null";
					int stifReq = Atc.ReadRecord(stifSql);
					if( stifReq != 0){
						stifSql = "select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pa.ACTDAT,pa.PRDNAME,pa.TXAMT "
								+ " from stpprdinf pr "
								+ " left join stppayinf pa on pr.payordno = pa.payordno "
								+ " left join stpusrinf us on pa.cust_id = us.cust_id "
								+ " left join stpcusinf cu on us.cust_id = cu.cust_id"
								+ " left join stpbanktb tb on tb.cust_id = cu.cust_id"
								+ " where pr.payordno = '"
								+ tranCode
								+ "' and rownum = 1 and tb.card_no is not null";
						stifReq = Atc.ReadRecord(stifSql);
					}
					if (stifReq == 0) {
					stif.setCTNM(Etf.getChildValue("CUST_NAME")); // 主体名称

					stif.setCITP(Etf.getChildValue("CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
									.toString().charAt(0))); // 证件类型
					stif.setCTID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO"))); // 证件号
					stif.setCBAT("02"); // 可疑主体的银行账号种??
					stif.setCBAC(DES.DES_DECRYPT(Etf.getChildValue("CARD_NO"))); // 可疑主体的银行账??
					stif.setCABM(Etf.getChildValue("BANKNAME")); // 可疑主体银行账号的开户银行名??
					stif.setCTAT("02"); // 可疑主体的支付账户种??
					stif.setCTAC(Etf.getChildValue("CUST_LOGIN")); // 可疑主体的支付账户账??
					stif.setCPIN(RINM); // 可疑主体????支付机构的名??
					stif.setCPBA(CPBA); // 可疑主体????支付机构的银行账??
					stif.setCPBN(CPBN); // 可疑主体????支付机构的银行账号的????银行名称
					stif.setCTIP(FileUtiles.getIp()); // 可疑主体的交易IP地址
					stif.setTSTM(Etf.getChildValue("ACTDAT")); // 交易时间
					stif.setCTTP("0000"); // 货币资金转移方式 目前只有网银 有特殊需求的时候在改
					stif.setTSDR("01"); // 资金收付标志
					stif.setCRPP(Etf.getChildValue("PRDNAME")); // 资金用??
					stif.setCRTP("CNY"); // 交易币种
					stif.setCRAT(Etf.getChildValue("TXAMT") == null ? ""
							: String.valueOf((Double.valueOf(Etf
									.getChildValue("TXAMT")) / 100))); // 交易金额
					stif.setTCNM(RINM); // 交易对手姓名/名称
					//stif.setTSMI(); // 交易对手特约商户编号
					stif.setTCIT(Etf.getChildValue("CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
									.toString().charAt(0))); // 交易对手证件/证明文件类型
				    stif.setTCID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO"))); // 交易对手证件/证明文件号码
					stif.setTCAT("02"); // 交易对手的银行账号种??
					stif.setTCBA(CPBA); // 交易对手的银行账??
					stif.setTCBN(CPBN); // 交易对手银行账号的开户银行名??
					stif.setTCTT("01"); // 交易对手的支付账户种??
					// stif.setTCTA(); // 交易对手的支付账户号??
					stif.setTCPN(RINM); // 交易对手所在支付机构的名称
					stif.setTCPA(CPBA); // 交易对手????支付机构的银行账??
					stif.setTPBN(CPBN); // 交易对手????支付机构银行账号的开户银行名??
					// stif.setTCIP(); // 交易对手的交易IP地址
					// stif.setTMNM(); // 交易商品名称
					stif.setBPTC(tranCode); // 银行与支付机构之间的业务交易编码
					stif.setPMTC(tranCode); // 支付机构与商户之间的业务交易编码
					stif.setTICD(tranCode); // 业务标识??
					}
				}
				// 转账	
				else if ("6".equals(tranType) ) {
					stifSql = " select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pr.TXAMT,pr.TRATIME ACTDAT,"
							+ "dcu.cust_name D_CUST_NAME,dcu.CUST_CRED_TYPE D_CUST_CRED_TYPE,dtb.CARD_NO D_CARD_NO,dtb.BANKNAME D_BANKNAME,dus.CUST_LOGIN D_CUST_LOGIN "
							+ " from stptrainf pr"
							+ " left join stpusrinf us on pr.cust_id = us.cust_id "
							+ " left join stpcusinf cu on us.cust_id = cu.cust_id "
							+ " left join stpbanktb tb on tb.cust_id = cu.cust_id "
							+ " left join stpusrinf dus on dus.cust_id = pr.to_cust_id "
							+ " left join stpcusinf dcu on dcu.cust_id = dus.cust_id "
							+ " left join stpbanktb dtb on concat(dus.cust_id,'01') = dtb.cust_id "
							+ " where pr.traordno = '"
							+ tranCode
							+ "' and rownum = 1 and tb.card_no is not null";
					int stifReq = Atc.ReadRecord(stifSql);
					if(stifReq != 0 ){
						stifSql = " select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pr.TXAMT,pr.TRATIME ACTDAT,"
								+ "dcu.cust_name D_CUST_NAME,dcu.CUST_CRED_TYPE D_CUST_CRED_TYPE,dtb.CARD_NO D_CARD_NO,dtb.BANKNAME D_BANKNAME,dus.CUST_LOGIN D_CUST_LOGIN "
								+ " from stptrainf pr"
								+ " left join stpusrinf us on pr.cust_id = us.cust_id "
								+ " left join stpcusinf cu on us.cust_id = cu.cust_id "
								+ " left join stpbanktb tb on tb.cust_id = cu.cust_id "
								+ " left join stpusrinf dus on dus.cust_id = pr.to_cust_id "
								+ " left join stpcusinf dcu on dcu.cust_id = dus.cust_id "
								+ " left join stpbanktb dtb on dus.cust_id = dtb.cust_id"
								+ " where pr.traordno = '"
								+ tranCode
								+ "' and rownum = 1 and tb.card_no is not null";
						stifReq = Atc.ReadRecord(stifSql);
					}
					if (stifReq == 0) {
					stif.setCTNM(Etf.getChildValue("CUST_NAME")); // 主体名称
					stif.setCITP(Etf.getChildValue("CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
									.toString().charAt(0))); // 证件类型
					stif.setCTID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO"))); // 证件号
					stif.setCBAT("02"); // 可疑主体的银行账号种??
					stif.setCBAC(DES.DES_DECRYPT(Etf.getChildValue("CARD_NO"))); // 可疑主体的银行账??
					stif.setCABM(Etf.getChildValue("BANKNAME")); // 可疑主体银行账号的开户银行名??
					stif.setCTAT("02"); // 可疑主体的支付账户种??
					stif.setCTAC(Etf.getChildValue("CUST_LOGIN")); // 可疑主体的支付账户账??
					stif.setCPIN(RINM); // 可疑主体????支付机构的名??
					stif.setCPBA(CPBA); // 可疑主体????支付机构的银行账??
					stif.setCPBN(CPBN); // 可疑主体????支付机构的银行账号的????银行名称
					stif.setCTIP(FileUtiles.getIp()); // 可疑主体的交易IP地址
					stif.setTSTM(Etf.getChildValue("ACTDAT")); // 交易时间
					stif.setCTTP("0000"); // 货币资金转移方式 目前只有网银 有特殊需求的时候在改
					stif.setTSDR("01"); // 资金收付标志
					stif.setCRPP("转账"); // 资金用??
					stif.setCRTP("CNY"); // 交易币种
					stif.setCRAT(Etf.getChildValue("TXAMT") == null ? ""
							: String.valueOf((Double.valueOf(Etf
									.getChildValue("TXAMT")) / 100))); // 交易金额
					stif.setTCNM(Etf.getChildValue("D_CUST_NAME")); // 交易对手姓名/名称
					// stif.setTSMI(); // 交易对手特约商户编号
					stif.setTCIT(Etf.getChildValue("D_CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("D_CUST_CRED_TYPE")
									.toString().charAt(0))); // 交易对手证件/证明文件类型
					stif.setTCID(DES.DES_DECRYPT(Etf.getChildValue("D_CUST_CRED_NO"))); // 交易对手证件/证明文件号码
					stif.setTCAT("02"); // 交易对手的银行账号种??
					stif.setTCBA(DES.DES_DECRYPT(Etf
							.getChildValue("D_CARD_NO"))); // 交易对手的银行账??
					stif.setTCBN(Etf.getChildValue("D_BANKNAME")); // 交易对手银行账号的开户银行名??
					stif.setTCTT("01"); // 交易对手的支付账户种??
					stif.setTCTA(Etf.getChildValue("D_CUST_LOGIN")); // 交易对手的支付账户号??
					stif.setTCPN(RINM); // 交易对手所在支付机构的名称
					stif.setTCPA(CPBA); // 交易对手????支付机构的银行账??
					stif.setTPBN(CPBN); // 交易对手????支付机构银行账号的开户银行名??
					stif.setTCIP(FileUtiles.getIp()); // 交易对手的交易IP地址
					// stif.setTMNM(); // 交易商品名称
					stif.setBPTC(tranCode); // 银行与支付机构之间的业务交易编码
					stif.setPMTC(tranCode); // 支付机构与商户之间的业务交易编码
					stif.setTICD(tranCode); // 业务标识??
					}
				}
				// 提现
				else if ("7".equals(tranType)) {
					stifSql = " select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pr.ACTDAT,pr.TXAMT,st.BANKPAYACNO,st.BANKPAYUSERNM,ly.enm_dat_des from stpcasinf pr "
							+ " left join stpusrinf us on pr.cust_id = us.cust_id "
							+ " left join stpcusinf cu on us.cust_id = cu.cust_id "
							+ " left join stpbanktb tb on tb.cust_id = concat(cu.cust_id,'01') "
							+ " left join VW_STPCASINF st on st.CASORDNO = pr.casordno "
							+ " left join lyxdicenm ly on ly.enm_dat_opt = st.BANKCODE "
							+ " where pr.casordno = '"
							+ tranCode
							+ "' and rownum = 1 and tb.card_no is not null";
					int stifReq = Atc.ReadRecord(stifSql);
					if( stifReq != 0){
						stifSql = " select cu.CUST_NAME,cu.CUST_CRED_TYPE,cu.CUST_CRED_NO,tb.CARD_NO,tb.BANKNAME,us.CUST_LOGIN,pr.ACTDAT,pr.TXAMT,st.BANKPAYACNO,st.BANKPAYUSERNM,ly.enm_dat_des from stpcasinf pr "
								+ " left join stpusrinf us on pr.cust_id = us.cust_id "
								+ " left join stpcusinf cu on us.cust_id = cu.cust_id "
								+ " left join stpbanktb tb on tb.cust_id = cu.cust_id "
								+ " left join VW_STPCASINF st on st.CASORDNO = pr.casordno "
								+ " left join lyxdicenm ly on ly.enm_dat_opt = st.BANKCODE "
								+ " where pr.casordno = '"
								+ tranCode
								+ "' and rownum = 1 and tb.card_no is not null";
						stifReq = Atc.ReadRecord(stifSql);
					}
					if (stifReq == 0) {
					stif.setCTNM(Etf.getChildValue("CUST_NAME")); // 主体名称
					stif.setCITP(Etf.getChildValue("CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
									.toString().charAt(0))); // 证件类型
					stif.setCTID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO"))); // 证件号
					stif.setCBAT("02"); // 可疑主体的银行账号种??
					stif.setCBAC(DES.DES_DECRYPT(Etf.getChildValue("CARD_NO"))); // 可疑主体的银行账??
					stif.setCABM(Etf.getChildValue("BANKNAME")); // 可疑主体银行账号的开户银行名??
					stif.setCTAT("02"); // 可疑主体的支付账户种??
					stif.setCTAC(Etf.getChildValue("CUST_LOGIN")); // 可疑主体的支付账户账??
					stif.setCPIN(RINM); // 可疑主体????支付机构的名??
					stif.setCPBA(CPBA); // 可疑主体????支付机构的银行账??
					stif.setCPBN(CPBN); // 可疑主体????支付机构的银行账号的????银行名称
					stif.setCTIP(FileUtiles.getIp()); // 可疑主体的交易IP地址
					stif.setTSTM(Etf.getChildValue("ACTDAT")); // 交易时间
					stif.setCTTP("0000"); // 货币资金转移方式 目前只有网银 有特殊需求的时候在改
					stif.setTSDR("01"); // 资金收付标志
					stif.setCRPP(Etf.getChildValue("PRDNAME")); // 资金用??
					stif.setCRTP("CNY"); // 交易币种
					stif.setCRAT(Etf.getChildValue("TXAMT") == null ? "":AMTDOT(Etf.getChildValue("TXAMT"))); // 交易金额
					stif.setTCNM(Etf.getChildValue("ENM_DAT_DES")); // 交易对手姓名/名称
					// stif.setTSMI(); // 交易对手特约商户编号
					stif.setTCIT(Etf.getChildValue("CUST_CRED_TYPE") == null ? ""
							: FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
									.toString().charAt(0))); // 交易对手证件/证明文件类型
					stif.setTCID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO"))); // 交易对手证件/证明文件号码
					stif.setTCAT("02"); // 交易对手的银行账号种??
					stif.setTCBA(DES.DES_DECRYPT(stifEmt
							.elementText("BANKPAYACNO"))); // 交易对手的银行账??
					stif.setTCBN(Etf.getChildValue("ENM_DAT_DES")); // 交易对手银行账号的开户银行名??
					// stif.setTCTT("01"); // 交易对手的支付账户种??
					// stif.setTCTA(); // 交易对手的支付账户号??
					// stif.setTCPN(RINM); // 交易对手所在支付机构的名称
					// stif.setTCPA(CPBA); // 交易对手????支付机构的银行账??
					// stif.setTPBN(CPBN); // 交易对手????支付机构银行账号的开户银行名??
					// stif.setTCIP(); // 交易对手的交易IP地址
					// stif.setTMNM(); // 交易商品名称
					stif.setBPTC(tranCode); // 银行与支付机构之间的业务交易编码
					stif.setPMTC(tranCode); // 支付机构与商户之间的业务交易编码
					stif.setTICD(tranCode); // 业务标识??
					}
				}
				// 退款
				else if ("8".equals(tranType)) {

				}
				if (null != stif.getTICD())
					stifList.add(stif);
			}
		}
		return stifList;
	}

	private String AMTDOT(String elementText) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 构建可疑主体信息
	 * 
	 * @param userCode
	 *            用户号
	 * @param ctifList
	 *            可疑主体集合
	 * @param TranSource
	 *            用户类型
	 * @return
	 * @throws Exception 
	 */
	public List<CTIF> getCtifList(String userCode, List<CTIF> ctifList,
			String TranSource) throws Exception {
		String ctifSql = "";
		// 可疑主体
		CTIF ctif = new CTIF();
		// 地址信息
		CCIF ccif = new CCIF();
		// 判断异常交易客户类型是用户还是商户 0 用户 1 商户
		if ("0".equals(TranSource)) {
			ctifSql = "select CUST_NAME,CUST_CRED_TYPE,CUST_CRED_NO,USR_ADDRESS,USR_MOBILE,USR_EMAIL from stpcusinf c inner join stpusrinf u on c.cust_id = u.cust_id where c.cust_id ='"
					+ userCode + "'";
			int req = Atc.ReadRecord(ctifSql);
			if (req == 0) {
				// 创建一个可疑主体
				ctif.setCTNM(Etf.getChildValue("CUST_NAME"));
				// ctif.setSMID(sMID) //目前没有特约商户
				ctif.setCITP(FileUtiles.getCITP(Etf.getChildValue("CUST_CRED_TYPE")
						.toString().charAt(0)));
				ctif.setCTID(DES.DES_DECRYPT(Etf.getChildValue("CUST_CRED_NO")));
				ccif.setCTAR(Etf.getChildValue("USR_ADDRESS"));
				ccif.setCCTL(Etf.getChildValue("USR_MOBILE"));
				ccif.setCEML(Etf.getChildValue("USR_EMAIL"));
				ctif.setCCIF(ccif);
				// 用户没有法人等信息
				ctifList.add(ctif);
			}
		} else {
			ctifSql = "select STORE_NAME,LAW_PERSON_CRET_TYPE,LAW_PERSON_CRET_NO,MER_ADDR,COMPAY_TEL_NO,COMPAY_EMAIL,BIZ_TYPE,MER_LAW_PERSON,MER_LAW_PERSON,LAW_PERSON_CRET_NO"
					+ " from stpcusinf c inner join stpmerinf m on c.cust_id = m.cust_id where c.cust_id ='"
					+ userCode + "'";
			int req = Atc.ReadRecord(ctifSql);
			if (req == 0) {
				// 创建一个可疑主体
				ctif.setCTNM(Etf.getChildValue("STORE_NAME"));
				// ctif.setSMID(sMID) //目前没有特约商户
				ctif.setCITP(FileUtiles.getCITP(Etf.getChildValue("LAW_PERSON_CRET_TYPE")
						.toString().charAt(0)));
				ctif.setCTID(DES.DES_DECRYPT(Etf.getChildValue("LAW_PERSON_CRET_NO")));
				// 地址信息
				ccif.setCTAR(Etf.getChildValue("MER_ADDR"));
				ccif.setCCTL(Etf.getChildValue("COMPAY_TEL_NO"));
				ccif.setCEML(Etf.getChildValue("COMPAY_EMAIL"));
				ctif.setCCIF(ccif);
				ctif.setCTVC(FileUtiles.getCTVC(Etf.getChildValue("BIZ_TYPE")));
				ctif.setCRNM(Etf.getChildValue("MER_LAW_PERSON"));
				ctif.setCRIT(FileUtiles.getCITP(Etf.getChildValue("LAW_PERSON_CRET_TYPE")
						.toString().charAt(0)));
				ctif.setCRID(DES.DES_DECRYPT(Etf.getChildValue("LAW_PERSON_CRET_NO")));
				ctifList.add(ctif);
			}
		}
		return ctifList;
	}


	public static void main(String[] args) throws Exception {
		
		System.out.println(DES.DES_DECRYPT("00a8225154ce97c21fcb1813dc010f714cfbfb306aa1b928"));
		
		String aa = "0000123";
		System.out.println(aa.substring(aa.length() - 4));
	}

}