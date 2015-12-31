package com.rcs.interfaces;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tangdi.ip.AddressUtils;
import com.tangdi.util.GetOwnButton;
import com.util.RcsUtils;

import tangdi.annotations.Code;
import tangdi.atc.Atc;
import tangdi.atc.TdETFAtc;
import tangdi.atc.TdFile;
import tangdi.engine.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.util.ResourceLocator;
import tangdi.web.TdWebConstants;

@DB
public class CurrentCont {
	public static Map<String, String> rcs_pay_type_maps;
	private static String DATE_FORMAT = "yyyyMMddHHmmss";
	
	static {
		rcs_pay_type_maps = new HashMap<String, String>();
		rcs_pay_type_maps.put("800005", "用户登录密码修改");
		rcs_pay_type_maps.put("800007", "用户支付密码修改");
		rcs_pay_type_maps.put("800011", "用户登录");
		rcs_pay_type_maps.put("800014", "虚拟账户充值");
		rcs_pay_type_maps.put("800015", "建立提现订单");
		rcs_pay_type_maps.put("800016", "用户转账");
		rcs_pay_type_maps.put("800019", "订单支付");
	}

	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		Msg.dump();
		Object obj = ic.proceed();
		GetOwnButton.getOwnButton();
		Msg.dump();
		return obj;
	}
	
	@Code("593724")
	public void importCurrentCont() throws Exception {
		//请求IP
		String sReqIp      = RcsUtils.getEtfVal("TERMCODE"     , "127.0.0.1");
		String sUserCode   = RcsUtils.getEtfVal("RCS_USER_CODE", "");
		String sPayType    = RcsUtils.getEtfVal("RCS_PAY_TYPE" , "");
		String sTCode      = RcsUtils.getEtfVal("RCS_TCODE"  , "");
		String sStatus     = RcsUtils.getEtfVal("RCS_STATUS" , "");
		String sPrdNo      = RcsUtils.getEtfVal("RCS_PRD_NO" , "");
		String sSource     = RcsUtils.getEtfVal("RCS_SOURCE" , "");
		String sCurentDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
		
		if(StringUtils.isEmpty(sUserCode)){
			Etf.setChildValue("RETURNRESULT"     , "0");
			Etf.setChildValue("RETURNRESULT_INFO", "用户信息丢失！");
			return;
		}
		
		//sReqIp = "210.22.153.30";
		AddressUtils.getIpAddress(sReqIp);
		
		//优化SQL 设置到ETF上
		Etf.setChildValue("TMP_CURENT_DATE", sCurentDate);
		
		//插入传入的风控信息
		int iResult = 0;
		String insertSql = "INSERT INTO RCS_CURRCONT(PRI_ID,USER_CODE,CURENT_DATE,PAY_TYPE,TCODE,STATUS,PRDNUMBER,";
		insertSql += " SOURCE,REQIP,IPCOUNTRY,IPAREA,IPREGION,IPCITY,IPCOUNTY,IPISP) VALUES(";
		insertSql += "SEQ_ON_RCS.NEXTVAL,";
		insertSql += "#{RCS_USER_CODE}  ,";
		insertSql += "#{TMP_CURENT_DATE},";
		insertSql += "#{RCS_PAY_TYPE}   ,";
		insertSql += "#{RCS_TCODE}      ,";
		insertSql += "#{RCS_STATUS}     ,";
		insertSql += "#{RCS_PRD_NO}     ,";
		insertSql += "#{RCS_SOURCE}     ,";
		insertSql += "#{TERMCODE}       ,";
		insertSql += "#{TEMP_IPCOUNTRY} ,";
		insertSql += "#{TEMP_IPAREA}    ,";
		insertSql += "#{TEMP_IPREGION}  ,";
		insertSql += "#{TEMP_IPCITY}    ,";
		insertSql += "#{TEMP_IPCOUNTY}  ,";
		insertSql += "#{TEMP_IPISP}";
		insertSql += ")";
		
		iResult = Atc.ExecSql(null, insertSql);
		if (0 != iResult) {
			Etf.setChildValue("RETURNRESULT"     , "0");
			Etf.setChildValue("RETURNRESULT_INFO", "插入风控信息失败");
			return;
		}
		
		Etf.setChildValue("RETURNRESULT", "1");
		Etf.setChildValue("RETURNRESULT_INFO", "风控信息插入成功");
		
		if("1".equals(sTCode)){
			int iLockLoginPwdNum = remainLockPwdNum(sUserCode, sTCode);
			Etf.setChildValue("RETURNRESULT", "1");
			Etf.setChildValue("RETURNRESULT_INFO", "登录密码错，您还剩"+iLockLoginPwdNum+"次机会！");
			//登录密码次数剩余0次，即提示锁定
			if(0==iLockLoginPwdNum){
				Etf.setChildValue("RETURNRESULT_INFO", "登录密码已锁定！");
			}
		}
		if("5".equals(sTCode)){
			int iLockPayPwdNum = remainLockPwdNum(sUserCode, "5");
			Etf.setChildValue("RETURNRESULT", "1");
			Etf.setChildValue("RETURNRESULT_INFO", "支付密码错，您还剩"+iLockPayPwdNum+"次机会！");
			//支付密码次数剩余0次，即提示锁定
			if(0==iLockPayPwdNum){
				Etf.setChildValue("RETURNRESULT_INFO", "支付密码已锁定！");
			}
		}
		
		/***判断是否为同IP异常登录***/
		String sDistance = "10";
		sReqIp      = RcsUtils.getEtfVal("TERMCODE"     , "127.0.0.1");
		sUserCode   = RcsUtils.getEtfVal("RCS_USER_CODE", "");
		sPayType    = RcsUtils.getEtfVal("RCS_PAY_TYPE" , "");
		sCurentDate = sCurentDate;
		//判断登录
		if("800011".equals(sPayType)){
			if("127.0.0.1".equals(sReqIp)){
				
			}else if(sReqIp.startsWith("192.168")){
				
			}else{
				
			}
			
			//查找当日同IP但不同用户的登录记录
			String sQueryExcepLogin = "select count(*) as TEMP from RCS_CURRCONT ";
			sQueryExcepLogin += " where USER_CODE != '"+sUserCode+"' ";
			sQueryExcepLogin += " and CURENT_DATE like '"+sCurentDate.substring(0, 8)+"%' ";
			sQueryExcepLogin += " and REQIP = '"+sReqIp+"' ";
			sQueryExcepLogin += " and PAY_TYPE = '800011' ";
			
			iResult = Atc.ReadRecord(sQueryExcepLogin);
			if(0==iResult && !"0".equals(Etf.getChildValue("TEMP"))){
				insertSql = "INSERT INTO RCS_CURRCONT(PRI_ID,USER_CODE,CURENT_DATE,PAY_TYPE,TCODE,STATUS,PRDNUMBER,";
				insertSql += " SOURCE,REQIP,IPCOUNTRY,IPAREA,IPREGION,IPCITY,IPCOUNTY,IPISP) VALUES(";
				insertSql += " SEQ_ON_RCS.NEXTVAL,";
				insertSql += "#{RCS_USER_CODE}  ,";
				insertSql += "#{TMP_CURENT_DATE},";
				insertSql += "#{RCS_PAY_TYPE}   ,";
				insertSql += "'同IP多账户登录异常' ,";
				insertSql += "'0'               ,";
				insertSql += "#{RCS_PRD_NO}     ,";
				insertSql += "#{RCS_SOURCE}     ,";
				insertSql += "#{TERMCODE}       ,";
				insertSql += "#{TEMP_IPCOUNTRY} ,";
				insertSql += "#{TEMP_IPAREA}    ,";
				insertSql += "#{TEMP_IPREGION}  ,";
				insertSql += "#{TEMP_IPCITY}    ,";
				insertSql += "#{TEMP_IPCOUNTY}  ,";
				insertSql += "#{TEMP_IPISP}";
				insertSql += ")";
				
				iResult = Atc.ExecSql(null, insertSql);
			}
		}
		/***判断是否为同IP异常登录***/
		
		
		/**实时邮件提醒**/
		if ("0".equals(sStatus)) {
		      Log.info("将实时异常交易信息进行邮件提醒！", new Object[0]);

		      List<Element> serviceManList = getServiceManInfo();

		      String sToAddress = "";
		      String sTitle = "";
		      String sContent = "";

		      String sServiceManName = "";
		      String sServiceManEmail = "";

		      String sPayTypesString = "";
		      if ("800005".equals(sPayType))
		        sPayTypesString = "用户登录密码修改";
		      else if ("800007".equals(sPayType))
		        sPayTypesString = "用户支付密码修改";
		      else if ("800011".equals(sPayType))
		        sPayTypesString = "用户登录";
		      else if ("800014".equals(sPayType))
		        sPayTypesString = "虚拟账户充值";
		      else if ("800015".equals(sPayType))
		        sPayTypesString = "建立提现订单";
		      else if ("800016".equals(sPayType))
		        sPayTypesString = "用户转账";
		      else if ("800019".equals(sPayType))
		        sPayTypesString = "订单支付";
		      else {
		        sPayTypesString = "系统业务";
		      }
		      sTitle = "实时异常交易报警--" + sCurentDate;
		      sContent = "尊敬的系统管理员： 风控系统 实时交易监控到 【用户编号：" + sUserCode + "  在进行" + sPayTypesString + " 交易时出错】可能为异常交易，为了降低系统风险，请及时给予处理。";

		      for (Element el2 : serviceManList) {
		        Log.info("维护人员信息：%s", new Object[] { el2.asXML() });

		        sServiceManName = RcsUtils.getElementTextByKey(el2, "USER_NAME");
		        sServiceManEmail = RcsUtils.getElementTextByKey(el2, "USER_EMAIL");
		        sToAddress = sServiceManEmail;

		        int iResult1 = TdMailSendSimple(sToAddress, sServiceManName, sTitle, sContent);
		        if (iResult1 == 0)
		          Log.info("发送邮件成功！", new Object[] { "" });
		        else
		          Log.info("发送邮件失败！", new Object[] { "" });
		      }
		    }
		
		/**实时邮件提醒**/
		
		
		
	}
	
	
	 public List<Element> getServiceManInfo()
	  {
	    List resList = new ArrayList();

	    String sSql = "SELECT USER_NAME,USER_EMAIL FROM RCS_SERVICE_MAN WHERE IS_USE = '1'";
	    int result = Atc.PagedQuery("1", "100000", "REC1", sSql);

	    if (result == 0) {
	      List<Element> tempList = Msg.childs("REC1");
	      for (Element el : tempList) {
	        resList.add(el);
	        Log.info("维护人员信息：%s", new Object[] { el.asXML() });
	      }
	    }
	    return resList;
	  }

	  public static int TdMailSendSimple(String EmTo, String EmToName, String EmTitle, String EmText)
	  {
	    int iRes = -9;
	    try {
	      iRes = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "G_SendEmail", null, null, (ResourceLocator)Msg.getInstance(ResourceLocator.class));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    if (-1 == iRes) {
	      Etf.setChildValue("RSPCOD", "000001");
	      Etf.setChildValue("RSPMSG", "参数错误");
	      return 1;
	    }if (2 == iRes) {
	      Etf.setChildValue("RSPCOD", "000001");
	      Etf.setChildValue("RSPMSG", "取XML配置父节点失败！");
	      return 1;
	    }if (iRes != 0) {
	      Etf.setChildValue("RSPCOD", "000001");
	      Etf.setChildValue("RSPMSG", "系统错误！");
	      return 1;
	    }

	    String EmFrom = Etf.getChildValue("G_EMFROM");
	    String EmUser = Etf.getChildValue("G_EMUSER");
	    String EmPwd = Etf.getChildValue("G_EMPWD");
	    String EmSmtp = Etf.getChildValue("G_EMSMTP");
	    String EmUserName = Etf.getChildValue("G_EMUSERNAME");

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

	    if (StringUtils.isEmpty(EmSmtp)) {
	      int k = EmFrom.indexOf("@") + 1;
	      EmSmtp = "smtp." + EmFrom.substring(k, EmFrom.length());
	    }
	    Properties props = new Properties();
	    props.put("mail.smtp.host", EmFrom);
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.quitwait", "false");

	    Session session = Session.getInstance(props);
	    session.setDebug(true);

	    Message newMessage = new MimeMessage(session);
	    try
	    {
	      newMessage.setFrom(new InternetAddress(EmFrom));

	      newMessage.setRecipient(Message.RecipientType.TO, 
	        new InternetAddress(EmTo));

	      newMessage.setSubject(EmTitle);

	      newMessage.setSentDate(new Date());

	      newMessage.setText(EmText);

	      Transport transport = session.getTransport("smtp");
	      transport.connect(EmSmtp, EmFrom, EmPwd);
	      transport.sendMessage(newMessage, newMessage.getAllRecipients());
	      transport.close();
	      Log.info("验证邮件发送成功!", new Object[0]);
	      return 0;
	    } catch (Exception localException) {
	      localException.printStackTrace();
	    }
	    return -1;
	  }
	
	
	
	/**
	 * @desc 检查用户的密码锁定状况
	 * @param sUserCode 用户号
	 * @param sTCode 密码类型 1:登录密码 5:支付密码
	 * */
	private boolean isLockPwd(String sUserCode,String sTCode) throws Exception{
		boolean bRes         = false;
		String sTCodeSucc    = "";
		String sSql          = "";
		String sErrorCount   = "5";
		String sLockMin      = "120";//锁定时间 分钟
		int iErrorCount      = 5;
		int iLockMin         = 120;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String sCurentDT     = sdf.format(new Date());
		int iResult          = 0;
		
		//读取配置文件
		iResult = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "G_CheckPwd", null, null, Msg.getInstance(ResourceLocator.class));
		if(0 != iResult){
			return false;
		}
		
		//配置文件中的错误次数
		sErrorCount = Etf.getChildValue("G_ERRORCOUNT");
		if(null != sErrorCount){
			try {
				iErrorCount = Integer.parseInt(sErrorCount);
			} catch (Exception e) {
				iErrorCount = 5;
			}
		}
		//配置文件中的间隔时间
		sLockMin = Etf.getChildValue("G_LOCK_MIN");
		if(null != sLockMin){
			try {
				iLockMin = Integer.parseInt(sLockMin);
			} catch (Exception e) {
				iLockMin = 120;
			}
		}
		
		if("1".equals(sTCode)){
			//登录密码成功的标志
			sTCodeSucc = "6";
		}
		
		if("5".equals(sTCode)){
			//支付密码成功的标志
			sTCodeSucc = "7";
		}
		
		Etf.setChildValue("TMP_USER_CODE" , sUserCode);
		Etf.setChildValue("TMP_TCODE"     , sTCode);
		Etf.setChildValue("TMP_TCODE_SUCC", sTCodeSucc);
		Etf.setChildValue("TMP_CURRENT_DT", sCurentDT);
		
		//按照倒序查询今日密码信息 按时间先后顺序
		sSql = "SELECT CURENT_DATE,TCODE FROM RCS_CURRCONT ";
		sSql += " WHERE USER_CODE = #{TMP_USER_CODE} ";
		sSql += " AND (TCODE = #{TMP_TCODE} OR TCODE = #{TMP_TCODE_SUCC}) ";
		sSql += " AND substr(CURENT_DATE,0,8) = substr(#{TMP_CURRENT_DT},0,8) ";
		sSql += " ORDER BY CURENT_DATE DESC ";
		
		//查询前 删除REC节点
		TdETFAtc.DeleteNode("REC");
		
		iResult = Atc.PagedQuery("1", "500", "REC", sSql);
		List<Element> elList = Etf.childs("REC");
		List<String> dtList  = new ArrayList<String>();
		for(Element el:elList){
			if(null==el.selectSingleNode("TCODE") || StringUtils.isEmpty(sTCodeSucc)){
				continue;
			}
			if(sTCodeSucc.equals(el.selectSingleNode("TCODE").getText())){
				break;
			}
			if(null != el.selectSingleNode("CURENT_DATE")){
				dtList.add(el.selectSingleNode("CURENT_DATE").getText());
			}
		}
		
		//将读取的内容进行按升序排序
		Collections.reverse(dtList);
		
		//锁定的开始时间
		String sLockDate = null;
		for(String sDate:dtList){
			Log.info("sDate：%s", sDate);
			if(null != sLockDate){
				Log.info("sDate：%s  sLockDate:%s", sDate,sLockDate);
				if(Long.parseLong(sDate)<Long.parseLong(sLockDate)){
					continue;
				}else{
					//遍历日期超过 锁定日期 则赋为空
					sLockDate = null;
				}
			}
			String[] arrRes = getSectionNum(dtList,sDate,iLockMin);
			Log.info("arrRes[1]：%s  arrRes[0]:%s", arrRes[1],arrRes[0]);
			//判断查询的结果是否大于锁定次数 大于则查找下一次锁定时间
			if(Integer.parseInt(arrRes[0])>=iErrorCount){
				//判断当前日期+时间段 是否超过当前日期
				Log.info("sDate:%s iLockMin:%s sCurentDT:%s",sDate,iLockMin,sCurentDT);
				if(isSupperCur(sDate, iLockMin, sCurentDT)){
					return true;
				}
				sLockDate = arrRes[1];
			}
		}
		return bRes;
	}
	
	/**
	 * @desc 检查用户剩余锁定次数
	 * @param sUserCode 用户号
	 * @param sTCode 密码类型 1:登录密码 5:支付密码
	 * @return 如果为有效次数
	 * */
	private int remainLockPwdNum(String sUserCode,String sTCode) throws Exception{
		int iRes             = Integer.MAX_VALUE;
		String sTCodeSucc    = "";
		String sSql          = "";
		String sErrorCount   = "5";
		String sLockMin      = "120";//锁定时间 分钟
		int iErrorCount      = 5;
		int iLockMin         = 120;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String sCurentDT     = sdf.format(new Date());
		int iResult          = 0;
		
		//读取配置文件
		iResult = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "G_CheckPwd", null, null, Msg.getInstance(ResourceLocator.class));
		if(0 != iResult){
			return iRes;
		}
		
		//配置文件中的错误次数
		sErrorCount = Etf.getChildValue("G_ERRORCOUNT");
		if(null != sErrorCount){
			try {
				iErrorCount = Integer.parseInt(sErrorCount);
			} catch (Exception e) {
				iErrorCount = 5;
			}
		}
		//配置文件中的间隔时间
		sLockMin = Etf.getChildValue("G_LOCK_MIN");
		if(null != sLockMin){
			try {
				iLockMin = Integer.parseInt(sLockMin);
			} catch (Exception e) {
				iLockMin = 120;
			}
		}
		
		if("1".equals(sTCode)){
			//登录密码成功的标志
			sTCodeSucc = "6";
		}
		
		if("5".equals(sTCode)){
			//支付密码成功的标志
			sTCodeSucc = "7";
		}
		
		Etf.setChildValue("TMP_USER_CODE" , sUserCode);
		Etf.setChildValue("TMP_TCODE"     , sTCode);
		Etf.setChildValue("TMP_TCODE_SUCC", sTCodeSucc);
		Etf.setChildValue("TMP_CURRENT_DT", sCurentDT);
		
		//按照倒序查询今日密码信息 按时间先后顺序
		sSql = "SELECT CURENT_DATE,TCODE FROM RCS_CURRCONT ";
		sSql += " WHERE USER_CODE = #{TMP_USER_CODE} ";
		sSql += " AND (TCODE = #{TMP_TCODE} OR TCODE = #{TMP_TCODE_SUCC}) ";
		sSql += " AND substr(CURENT_DATE,0,8) = substr(#{TMP_CURRENT_DT},0,8) ";
		sSql += " ORDER BY CURENT_DATE DESC ";
		
		//查询前 删除REC节点
		TdETFAtc.DeleteNode("REC");
		
		iResult = Atc.PagedQuery("1", "500", "REC", sSql);
		List<Element> elList = Etf.childs("REC");
		List<String> dtList  = new ArrayList<String>();
		for(Element el:elList){
			if(null==el.selectSingleNode("TCODE") || StringUtils.isEmpty(sTCodeSucc)){
				continue;
			}
			if(sTCodeSucc.equals(el.selectSingleNode("TCODE").getText())){
				break;
			}
			if(null != el.selectSingleNode("CURENT_DATE")){
				dtList.add(el.selectSingleNode("CURENT_DATE").getText());
			}
		}
		
		//将读取的内容进行按升序排序
		Collections.reverse(dtList);
		
		Log.info("查询的密码错误及正确列表：%s", dtList);
		
		//锁定的开始时间
		String sLockDate = null;
		for(String sDate:dtList){
			if(null != sLockDate){
				Log.info("sLockDate锁定日期：%s  sDate日期：%s", sLockDate,sDate);
				if(Long.parseLong(sDate)<Long.parseLong(sLockDate)){
					continue;
				}else{
					//遍历日期超过 锁定日期 则赋为空
					sLockDate = null;
				}
			}
			String[] arrRes = getSectionNum(dtList,sDate,iLockMin);
			//判断当前日期+时间段 是否超过当前日期
			Log.info("判断当前日期+时间段 是否超过当前日期：%s  %s  %s", sDate, iLockMin, sCurentDT);
			if(isSupperCur(sDate, iLockMin, sCurentDT)){
				//判断查询的结果是否大于锁定次数 大于则查找下一次锁定时间
				Log.info("arrRes[0]：%s  iErrorCount：%s", arrRes[0], iErrorCount);
				if(Integer.parseInt(arrRes[0])>=iErrorCount){
					return 0;
				}else{
					return iErrorCount - Integer.parseInt(arrRes[0]);
				}
			}
		}
		return iRes;
	}
	
	/**
	 * @desc 查找 传入时间 至 传入时间+时间段 的错误次数
 	 * */
	private String[] getSectionNum(List<String> dateList,String sStartDate,int iLockMin) throws Exception{
		String[] arrRes      = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date date            = sdf.parse(sStartDate);
		long lStartDate      = date.getTime();
		long lEndDate        = new Date(date.getTime()+iLockMin*60*1000).getTime();
		int iCount           = 0;
		for(String sDate:dateList){
			if(sdf.parse(sDate).getTime()>=lStartDate && sdf.parse(sDate).getTime()<lEndDate){
				iCount++;
			}
			//取第一个大于该区间的日志
			if(sdf.parse(sDate).getTime()>=lEndDate){
				arrRes[1] = sDate;
				break;
			}
		}
		arrRes[0] = iCount+"";
		Log.info("日期：%s  错误次数：%s", arrRes[1],arrRes[0]);
		return arrRes;
	}
	
	/**
	 * @desc 判断 传入时间+时间段 是否超过当前时间
 	 * */
	private boolean isSupperCur(String sStartDT,int iLockMin,String sCurrentDT)throws Exception{
		boolean bRes         = false;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date date1           = sdf.parse(sStartDT);
		Date date2           = sdf.parse(sCurrentDT);
		if((date1.getTime()+iLockMin*60*1000)>date2.getTime()){
			return true;
		}
		return bRes;
	}
	
	/*
	 * @desc 查询用户锁情况
	 * **/
	@Code("593728")
	public void queryCurrentCont() throws Exception {
		String sUserCode = Etf.getChildValue("RCS_USER_CODE") == null ? "": Etf.getChildValue("RCS_USER_CODE");
		if (StringUtils.isEmpty(sUserCode)) {
			Etf.setChildValue("RETURNRESULT"     , "0");
			Etf.setChildValue("RETURNRESULT_INFO", "传入报文参数缺失用户编码");
			return;
		}
		
		boolean bLockLoginPwd = isLockPwd(sUserCode, "1");
		boolean bLockPayPwd   = isLockPwd(sUserCode, "5");
		
		//都冻结
		if(bLockLoginPwd && bLockPayPwd){
			Etf.setChildValue("RETURNRESULT"     , "03");
			Etf.setChildValue("RETURNRESULT_INFO", "密码已冻结");
		}
		
		//登录密码冻结
		if(bLockLoginPwd){
			Etf.setChildValue("RETURNRESULT"     , "0");
			Etf.setChildValue("RETURNRESULT_INFO", "登录密码已冻结");
			return;
		}
		
		//支付密码冻结
		if(bLockPayPwd){
			Etf.setChildValue("RETURNRESULT"     , "3");
			Etf.setChildValue("RETURNRESULT_INFO", "支付密码已冻结");
			return;
		}
		
		Etf.setChildValue("RETURNRESULT"     , "1");
		Etf.setChildValue("RETURNRESULT_INFO", "没有锁存在");
	}
	
	/*
	 * @desc 查询用户锁剩余次数情况查询
	 * **/
	@Code("593732")
	public void queryCurrentRemainNum() throws Exception {
		String sUserCode = Etf.getChildValue("RCS_USER_CODE") == null ? "": Etf.getChildValue("RCS_USER_CODE");
		if (StringUtils.isEmpty(sUserCode)) {
			Etf.setChildValue("RETURN_COD", "000001");
			Etf.setChildValue("RETURN_MSG", "传入报文参数缺失用户编码");
			return;
		}
		
		int iLockLoginPwdNum = remainLockPwdNum(sUserCode, "1");
		int iLockPayPwdNum   = remainLockPwdNum(sUserCode, "5");
		
		Etf.setChildValue("RETURN_COD", "000000");
		Etf.setChildValue("RETURN_MSG", "查询成功");
		
		Etf.setChildValue("REMAINLOCKPWDNUM_LOGIN", iLockLoginPwdNum+"");
		Etf.setChildValue("REMAINLOCKPWDNUM_PAY"  , iLockPayPwdNum  +"");
	}
	
	/*
	 * @desc 用户密码解锁
	 * **/
	@Code("593730")
	public void initUnLockPwdPage() throws Exception {
		Msg.set(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/excptran/unLockPwd.jsp");
	}
	
	/*
	 * @desc 用户密码解锁
	 * **/
	@Code("593729")
	public void unLockUserPwd() throws Exception {
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
		
		String sUserCode = Etf.getChildValue("RCS_USER_CODE") == null ? "": Etf.getChildValue("RCS_USER_CODE");
		String sPwdType  = Etf.getChildValue("RCS_UNLOCK_TYPE") == null ? "": Etf.getChildValue("RCS_UNLOCK_TYPE");
		if (StringUtils.isEmpty(sUserCode)) {
			Etf.setChildValue("RspCod" , "000001");
			Etf.setChildValue("RspMsg" , "传入报文参数缺失用户编码");
			return;
		}
		
		int iResult       = 0;
		String sTCode     = "1";
		String sLastDTime = "20000101010101";
		String sLockMin   = "120";//锁定时间 分钟
		
		//读取配置文件
		iResult = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "G_CheckPwd", null, null, Msg.getInstance(ResourceLocator.class));
		if(0 != iResult){
			Etf.setChildValue("RspCod" , "000003");
			Etf.setChildValue("RspMsg" , "读取配置文件失败");
			return;
		}
        
		if(StringUtils.isNotEmpty(sPwdType)){
			if("LOGINPWD".equalsIgnoreCase(sPwdType)){//解锁登录密码
				sTCode = "1";
			}
			if("PAYPWD".equalsIgnoreCase(sPwdType)){//解锁支付密码
				sTCode = "5";
			}
		}
		
		//配置文件中的间隔时间
		if(StringUtils.isEmpty(Etf.getChildValue("G_LOCK_MIN"))){
			sLockMin = Etf.getChildValue("G_LOCK_MIN");
		}
		
		String sSql = "SELECT MAX(CURENT_DATE) as LAST_ERROT_DT FROM RCS_CURRCONT ";
		sSql += " WHERE USER_CODE = #{RCS_USER_CODE} ";
		sSql += " AND TCODE = '"+sTCode+"' ";
		
		iResult = Atc.ReadRecord(sSql);
		if(0 == iResult){
			if(null == Etf.getChildValue("LAST_ERROT_DT")){
				Etf.setChildValue("RspCod", "000000");
				Etf.setChildValue("RspMsg", "解锁成功！");
				return;
			}
			//最近的一个发生指定类型错误的时间
			sLastDTime = Etf.getChildValue("LAST_ERROT_DT");
		}
		
		Etf.setChildValue("TMP_CURENT_DATE", sLastDTime);
		
		//将原来锁定的登录密码 1 修改为 密码错误
		sSql = "UPDATE RCS_CURRCONT ";
		sSql += " SET TCODE = '密码错误' ";
		sSql += " WHERE USER_CODE = #{RCS_USER_CODE} ";
		sSql += " AND TCODE = '"+sTCode+"' ";
		sSql += " AND to_date(CURENT_DATE,'yyyy-mm-dd,hh24:mi:ss') >= to_date(#{TMP_CURENT_DATE},'yyyy-mm-dd,hh24:mi:ss') - interval '"+sLockMin+"' minute ";
		
		iResult = Atc.ExecSql(null, sSql);
		if(0 != iResult){
			Etf.setChildValue("RspCod", "000002");
			Etf.setChildValue("RspMsg", "解锁失败！");
			return;
		}
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "解锁成功！");
	}
	
	/*
	 * @desc 将昨天的交易结果 转移至历史表
	 * **/
	@Code("593731")
	public void tranToHistory() throws Exception {
		String sTableName    = "RCS_CURRCONT";
		String sTableNameHis = "RCS_CURRCONT_HIS";
		String sLastTime     = "20000101000000";
		String sCurrTime     = "20990101000000";
		int iResult          = 0;
		
		//查询实时交易历史记录中最后的日期
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MAX(CURENT_DATE) as LAST_CURENT_DATE ");
		sb.append("FROM "+sTableNameHis);
		
		iResult = Atc.ReadRecord(sb.toString());
		if(0 == iResult){
			if(StringUtils.isEmpty(Etf.getChildValue("LAST_CURENT_DATE"))){
				sLastTime = "19700101000000";
			}else{
				sLastTime = Etf.getChildValue("LAST_CURENT_DATE");
			}
		}else if(2 == iResult){
			
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "交易失败！");
			return;
		}
		
		//查询实时交易中的最后的日期
		sb = new StringBuffer();
		sb.append("SELECT MAX(CURENT_DATE) as CURENT_DATE ");
		sb.append("FROM "+sTableName);
		
		iResult = Atc.ReadRecord(sb.toString());
		if(0 == iResult){
			if(StringUtils.isEmpty(Etf.getChildValue("CURENT_DATE"))){
				sCurrTime = "19700101000000";
			}else{
				sCurrTime = Etf.getChildValue("CURENT_DATE");
			}
		}else if(2 == iResult){
			
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "交易失败！");
			return;
		}
		
		//将最新的数据更新至历史表
		sb = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(sTableNameHis+" ");
		sb.append("SELECT * FROM "+sTableName+" ");
		sb.append("WHERE CURENT_DATE > "+sLastTime+" ");
		sb.append("AND CURENT_DATE <= "+sCurrTime);
		
		iResult = Atc.ExecSql(null, sb.toString());
		if(0 != iResult){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "交易失败！");
			return;
		}
		
		//清除冗余数据 当前日期-两个小时 之前的数据清除
		sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(sTableName+" ");
		sb.append("WHERE CURENT_DATE <= to_char(to_date('"+sCurrTime+"','yyyyMMddhh24miss') - 1,'yyyyMMdd')||'220000'");
		
		iResult = Atc.ExecSql(null, sb.toString());
		if(0 != iResult){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "清理失败！");
			return;
		}
	}
}