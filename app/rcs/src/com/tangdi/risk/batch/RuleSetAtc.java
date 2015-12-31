package com.tangdi.risk.batch;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.annotations.Code;
import tangdi.atc.Atc;
import tangdi.atc.TdFile;
import tangdi.context.Context;
import tangdi.db.DBUtil;
import tangdi.engine.RULE;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.util.ResourceLocator;

import com.tangdi.risk.batch.QueryTable;
import com.tangdi.risk.common.RcsDefault;
import com.tangdi.risk.common.Tools;
import com.tangdi.risk.pack.PackIp;
import com.tangdi.risk.pack.PackMer;
import com.tangdi.risk.pack.PackRule;
import com.tangdi.risk.pack.PackUser;
import com.tangdi.risk.rule.ExceptTran;
import com.tangdi.risk.rule.IpAddress;
import com.tangdi.risk.rule.Mer;
import com.tangdi.risk.rule.Rule;
import com.tangdi.risk.rule.RuleParam;
import com.tangdi.risk.rule.User;
import com.util.MyAuthenticator;
import com.util.RcsUtils;

@tangdi.engine.DB
public class RuleSetAtc {
	private final static String G_EMFROM     = "G_EMFROM";
	private final static String G_EMUSERNAME = "G_EMUSERNAME";
	private final static String G_EMUSER     = "G_EMUSER";
	private final static String G_EMPWD      = "G_EMPWD";
	private final static String G_EMSMTP     = "G_EMSMTP";
	
	static Connection conn = null;
	static Statement stmt  = null;
	static DBUtil db       = null;
	
	@Code("540701")
	public int callPrepareData() {
		String sDate       = Etf.getChildValue("SDATE");//汇总日期
		String eDate       = Etf.getChildValue("EDATE");
		String sBatch_Days = Etf.getChildValue("BATCH_DAYS");
		
		if (sDate == null || "".equals(sDate.trim())) {
			sDate = Tools.getFormatDate(null);
		}
		
		if(StringUtils.isNotEmpty(sBatch_Days)){
			try {
				int iDays = Integer.parseInt(sBatch_Days);
				iDays = -iDays;
				sDate = Tools.getDayByVal(null, iDays);
			} catch (Exception e) {
				
			}
		}
		
		Log.info("汇总日期：" + sDate);

		try {
			db = Context.getInstance(DBUtil.class);
			conn = db.getConnection();
			stmt = conn.createStatement();

			String sStartDate = Tools.getDayByVal(Tools.getFormatDate(sDate), 0);
			Log.info("传入日期为%s！", sStartDate);
			Log.info("汇总数据开始！");
			QueryTable.exec(stmt, sStartDate, sStartDate);
			Log.info("汇总数据结束！");
		} catch (Exception e) {
			Log.error(e, "540701出现错误！");
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != db) {
					db.close();
				}
			} catch (Exception e2) {
				Log.error(e2,"关闭连接出错！");
				return -1;
			}
		}
		return 0;
	}
	
	@Code("540702")
	public int callRuleSet() {
		Msg.dump();
		Log.info("start fun callRuleSet() ……");

		String[] fileName = RcsDefault.ruleFiles;

		String sDate       = Etf.getChildValue("SDATE");
		String eDate       = Etf.getChildValue("EDATE");
		String sBatch_Days = Etf.getChildValue("BATCH_DAYS");
		
		if (sDate == null || "".equals(sDate.trim())) {
			sDate = Tools.getFormatDate(null);
		}
		
		if(StringUtils.isNotEmpty(sBatch_Days)){
			try {
				int iDays = Integer.parseInt(sBatch_Days);
				iDays = -iDays;
				sDate = Tools.getDayByVal(null, iDays);
			} catch (Exception e) {
				
			}
		}
		
		Log.info("规则引擎日期：" + sDate);
		
		try {
			db = Context.getInstance(DBUtil.class);
			conn = db.getConnection();
			Log.info("数据库信息 ："+conn);
			stmt = conn.createStatement();
			
			// 汇总数据
			String sStartDate = Tools.getDayByVal(Tools.getFormatDate(sDate), 0);
			
			PackUser pu = new PackUser();
			List<User> userList = pu.getUserList(stmt, sStartDate, null, null);
			Log.info("用户数量2 ："+userList.size());

			PackMer pm = new PackMer();
			List<Mer> merList = pm.getMerList(stmt, sStartDate, null, null);
			Log.info("商户数量2 ："+merList.size());
			
			Log.info("商户数量2后stmp ："+stmt);
			
			PackIp pi = new PackIp();
			List<IpAddress> ipList = pi.getIpList(stmt, sStartDate.replaceAll("-", ""), null, null);
			Log.info("IP数量2 ："+ipList.size());
			
			PackRule pr = new PackRule();
			Map<String, Rule> ruleMap = pr.getRuleMap(stmt);
			Log.info("AAAA:"+ruleMap);
			ruleMap = pr.getRuleMapAddParam(stmt, ruleMap);
			Log.info("BBBB:"+ruleMap);
			// 规则调用
			RULE.begin();
			Log.info("设置用户列表2！共%s条。", userList.size());
			// Set UserList
			RULE.setInput(userList);
			Log.info("设置商户列表2！共%s条。", merList.size());
			// Set MerList
			RULE.setInput(merList);
			Log.info("设置IP列表2！共%s条。", ipList.size());
			// Set IpList
			RULE.setInput(ipList);
			// Set global parameter
			RULE.setGlobal("sParamDate", sDate);
			// Set global rule information
			RULE.setGlobal("ruleMap", ruleMap);
			
			Log.info("异常规则MAP=%s", ruleMap);
			
			Rule rule = null;
			boolean bEnable = true;
			for (String sKeyVal : ruleMap.keySet()) {
				rule = ruleMap.get(sKeyVal);
				if (RcsDefault.RCS_IS_USE.equals(rule.getIS_USE())) {
					//根据执行频率设置 是否执行异常规则
					bEnable = isExec(rule.getEXEC_RATE(),sDate);
				}
				if (RcsDefault.RCS_NOT_USE.equals(rule.getIS_USE())) {
					bEnable = false;
				}
				
				Log.info("规则%s，启用状态=%s", rule.getRULE_CODE(),bEnable+"");
				
				// Set rule param is could use or not
				RULE.setDRuleParam(rule.getRULE_CODE(), "enable", bEnable);
				
				Log.info("参数列表：%s", rule.getParamList());
				
				for (RuleParam rp : rule.getParamList()) {
					
					Log.info("规则%s，参数设置：%s=%s", rule.getRULE_CODE(),rp.getPARAM_NAME(),rp.getPARAM_VALUE());
					
					// 将获得参数设置到对应的规则中
					RULE.setDRuleParam(rule.getRULE_CODE(), rp.getPARAM_NAME(),rp.getPARAM_VALUE());
				}
			} 

			RULE.callRuleSet(fileName);
			Log.info("规则调用结束！");

			List<ExceptTran> exceptTranList = RULE.getOutput(ExceptTran.class);
			Log.info("获得异常信息列表！共%s条。", exceptTranList.size());
			Log.info("保存异常信息至数据库！");
			QueryTable.insertExcept(exceptTranList);
			Log.info("将白名单中的异常交易用户|商户 修改为灰名单！");
			QueryTable.insertXName(sDate.replaceAll("/", ""));
			Log.info("将异常交易信息进行邮件提醒！");
			sendMail(exceptTranList,sDate,getExceptionCnt());
			RULE.end();
		} catch (Exception e) {
			Log.error(e,"540702出现异常！");
			return -1;
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != db) {
					db.close();
				}
			} catch (Exception e2) {
				Log.error(e2,"关闭连接出错！");
				return -1;
			}
		}
		return 0;
	}
	
	public void sendMail(List<ExceptTran> exceptTranList,String sDate,String cnt) throws Exception {
		if(null==exceptTranList || exceptTranList.size()==0){
			return;
		}
		//查找维护人员
		List<Element> serviceManList = getServiceManInfo();
		
		//读取邮件配置信息
		int iRes = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "G_SendEmail", null, null, Msg.getInstance(ResourceLocator.class));
		if(-1==iRes){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "参数错误");
			return;
		}else if(2==iRes){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "取XML配置父节点失败！");
			return;
		}else if(0!=iRes){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "系统错误！");
			return;
		}
		
		String sToAddress   = "",
			   sTitle       = "",
			   sContent     = "";
		
		String sServiceManName  = "",
		       sServiceManEmail = "";
		
		sTitle   = "异常交易提醒--"+sDate;
		//sContent = "风控系统通过跑批发现"+exceptTranList.size()+"条异常交易记录，为了降低系统风险，请及时给予处理。";
		sContent = "风控系统通过跑批发现"+cnt+"条异常交易记录，为了降低系统风险，请及时给予处理。";
		for(Element el2:serviceManList){
			Log.info("维护人员信息：%s", el2.asXML());
			
			sServiceManName  = RcsUtils.getElementTextByKey(el2, "USER_NAME" );
			sServiceManEmail = RcsUtils.getElementTextByKey(el2, "USER_EMAIL");
			sToAddress       = sServiceManEmail;
			
			int iResult = TdMailSendSimple(sToAddress,sServiceManName, sTitle, sContent);
			if(0==iResult){
				Log.info("发送邮件成功！", "");
			}else{
				Log.info("发送邮件失败！", "");
			}
		}
	}
	
	/**
	 * 查找维护人员信息
	 */
	public List<Element> getServiceManInfo(){
		List<Element> resList = new ArrayList<Element>();
		
		//查询状态为有效的维护人员信息
		String sSql = "SELECT USER_NAME,USER_EMAIL FROM RCS_SERVICE_MAN WHERE IS_USE = '1'";
		int result = Atc.PagedQuery("1", "100000", "REC1", sSql);
		
		if (result == 0) {
			List<Element> tempList = Msg.childs("REC1");
			for(Element el:tempList){
				resList.add(el);
				Log.info("维护人员信息：%s", el.asXML());
			}
		}
		return resList;
	}
	
	public String getExceptionCnt(){
		String cnt = "";
		String sql = "select count(*) AS CNT from ( select  except.id, except.regdt_day, except.rule_code,rule.RULE_DES, except.entity_type,except.user_code,except.comp_code     from RCS_EXCEPT_TRAN_INFO except, RCS_EXCEPT_TRAN_RULE rule  where except.Rule_Code = rule.Rule_Code and except.EXCEPT_TRAN_FLAG = '0' and except.USER_CODE != '-1' ) a, (select id,count(*) AS SUMC  from RCS_EXCEPT_TRAN_DETAIL_INFO  group by id) b, (select detail_info.id,sum(serial_record.TRAN_AMT) AS SUMA from RCS_EXCEPT_TRAN_DETAIL_INFO detail_info, RCS_TRAN_SERIAL_RECORD      serial_record  where detail_info.tran_code = serial_record.id group by detail_info.id) c    where a.id=b.id and a.id=c.id ";
		int result = Atc.ReadRecord(sql);
		if (result == 0) {
			cnt= Etf.getChildValue("CNT");
		}
		return cnt;
	}
	
	public static int TdMailSendSimple( 
			@Named("EmTo") String EmTo, 
			@Named("EmToName") String EmToName,
			@Named("EmTitle") String EmTitle, 
			@Named("EmText") String EmText
			){
		String EmFrom = Etf.getChildValue(G_EMFROM);
		String EmUser = Etf.getChildValue(G_EMUSER);
		String EmPwd  = Etf.getChildValue(G_EMPWD);
		String EmSmtp = Etf.getChildValue(G_EMSMTP);
		String EmUserName = Etf.getChildValue(G_EMUSERNAME);
		/*Log.info("EmFrom：%s", EmFrom);
		Log.info("EmUser：%s", EmUser);
		Log.info("EmPwd：%s", EmPwd);
		Log.info("EmSmtp：%s", EmSmtp);
		Log.info("EmUserName：%s", EmUserName);
		
		Log.info("EmTo：%s", EmTo);
		Log.info("EmToName：%s", EmToName);
		Log.info("EmTitle：%s", EmTitle);
		Log.info("EmText：%s", EmText);*/
		
		
		if (StringUtils.isEmpty(EmFrom)) {
			return 1;
		}
		if (StringUtils.isEmpty(EmUser)) {
			return 1;
		}
		if (StringUtils.isEmpty(EmPwd)) {
			return 1;
		}
		if (StringUtils.isEmpty(EmTo)) {
			return 1;
		}
		if (StringUtils.isEmpty(EmTitle)) {
			return 1;
		}
		if (StringUtils.isEmpty(EmText)) {
			return 1;
		}
		
		Properties props = new Properties();
		
		
		if (StringUtils.isEmpty(EmSmtp)) {
			int k = EmFrom.indexOf("@") + 1;
			EmSmtp = "smtp." + EmFrom.substring(k, EmFrom.length());
		}
		
		//set mail host:eg.mail.eettop.com
		props.put("mail.smtp.host", EmSmtp);
		props.put("mail.smtp.auth", "true");
		MyAuthenticator myauth = new MyAuthenticator(EmFrom, EmPwd);
	    Session sendMailSession = Session.getInstance(props, myauth);
		Message newMessage = new MimeMessage(sendMailSession);
		try {
			String sNick = MimeUtility.encodeText(EmUserName);
			//set mail send address,the page show this address.
			newMessage.setFrom(new InternetAddress(sNick+" <"+EmFrom+">"));
			
			newMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(EmTo));
			newMessage.setSubject(EmTitle);
			newMessage.setSentDate(new Date());
		
			Multipart mp     = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			StringBuffer sb  = new StringBuffer();
		  
			sb.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
			sb.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
		
			sb.append("<head>");
			sb.append("    <meta charset='UTF-8'>");
			sb.append("    <title>提醒邮件</title>");
			sb.append("</head>");
			sb.append("<body>");
			
			sb.append("<div class=WordSection1 style='layout-grid:15.6pt'>");
			
			sb.append("<p class=MsoNormal style='line-height:150%'><span style='font-family:新宋体'>");
			sb.append(EmToName);
			sb.append("，您好：<span lang=EN-US><o:p></o:p></span></span></p>");
			
			sb.append("<p class=MsoNormal style='text-indent:21.0pt;line-height:150%'>");
			sb.append("<span style='mso-bidi-font-size:10.5pt;line-height:150%;font-family:新宋体'>");
			sb.append(EmText);
			sb.append("<span lang=EN-US><o:p></o:p></span></span></p>");
			sb.append("</div>");
			
			sb.append("</body>");
			sb.append("</html>");
			mbp.setContent(sb.toString(), "text/html;charset=UTF-8");
			mp.addBodyPart(mbp);
			newMessage.setContent(mp);
			newMessage.saveChanges();
		
			Transport transport = sendMailSession.getTransport("smtp");
			
			//set send mail's user information.user name and pwd
			transport.connect(EmSmtp, EmUser, EmPwd);
			transport.sendMessage(newMessage, newMessage.getAllRecipients());
			transport.close();
		
			return 0;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return -1;
	}
	
	//判断规则是否需要执行
	public boolean isExec(String sExecRate,String sDate){
		boolean bRes = false;
		//频率不设置默认为天
		if(StringUtils.isEmpty(sExecRate) || RcsDefault.EXEC_RATE_1.equals(sExecRate)){
			return true;
		}
		
		Date date = new Date(sExecRate);
		
		//判断是否为周末
		if(RcsDefault.EXEC_RATE_2.equals(sExecRate)){
			if(RcsUtils.isEndWeek(date)){
				return true;
			}
		}
		
		//判断是否为月末
		if(RcsDefault.EXEC_RATE_3.equals(sExecRate)){
			if(RcsUtils.isEndMonth(date)){
				return true;
			}
		}
		
		//判断是否为季度末
		if(RcsDefault.EXEC_RATE_4.equals(sExecRate)){
			if(RcsUtils.isEndMonth3(date)){
				return true;
			}
		}
		
		//判断是否为年末
		if(RcsDefault.EXEC_RATE_5.equals(sExecRate)){
			if(RcsUtils.isEndYear(date)){
				return true;
			}
		}
		
		return bRes;
	}
	public static void main(String[] args){
		TdMailSendSimple("569123131@qq.com","发送邮件","发送邮件","发送邮件");
	}
}