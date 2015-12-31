package com.swtpay.stp.frovisiontxtexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.annotations.Code;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.atc.TdFile;

import tangdi.engine.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.expr.functions.TdExpBasicFunctions;
import tangdi.util.ResourceLocator;

/**
* 导出备付金文件
* @author dengwk e-mail:deng_wk@tangdi.com.cn
* @date:2015-6-17 下午14:39:05
* @version :1.0
*
*/
@DB
public class Provision {

	@Data
	String SysCod;
	
	public static  Map<String, HashMap<String, String>> m;
	public static String [] defaultTranType = new String[]{"1","2","3","4","5","6","7","8"};
	
	@Code("565100")
	@SuppressWarnings("unchecked")
	public void provisionTxtExport() throws Exception { 
		/**
		 * 1.获取日切时间 并取得前一天的日期
		 * 2.获取合作银行信息列表保存到静态map中
		 * 
		 */
		 if(!StringUtils.equals(SysCod, "Batch")){
			 Log.error("发起方式有误！");
			 return;
		 }
		 String yesterDay =TdExpBasicFunctions.CALCDATE(new String []{TdExpBasicFunctions.GETDATE(),"-","d","1"});
		 //获取银行列表信息
		 String sql0="SELECT BANK_CODE,BANK_NAME,BANK_REL_NO,LEG_REP FROM COOPBANK WHERE BANK_STATUS='0'";
		 int result = Atc.QueryInGroup(sql0, "BANK_LIST", null);
		 if(result==-1){
			 Log.error("获取合作银行信息失败！");
			 return;
		 }
		
		 List<Object> lst = Etf.childs("BANK_LIST");
		 m = new HashMap<String, HashMap<String,String>>();
		 for (Object obj : lst) {
			Element eRoot = (Element)obj;
			List<Element> list = eRoot.elements();
			HashMap<String, String> map = new HashMap<String, String>();
			String key="";
			for (Element ele : list) {
				if(ele.getName().equalsIgnoreCase("BANK_CODE")){ key = ele.getText();}
				map.put(ele.getName(), ele.getText());
			}
			m.put(key, map);
		}
		boolean tran = ReadXmlConfig("etc/public/STP_CONFIG.XML","ProvisionCfg");
		if(!tran){
			Log.error("系统配置文件缺失，交易中断！");
			return;
		}
		String [] a;
		String needTranType = Etf.getChildValue("NEED_EXPORT_TRAN_TYPE");
		
		if(StringUtils.isEmpty(needTranType)) a= defaultTranType;
		
		else{
			a= needTranType.split(",");
			if(null == a || a.length==0) a=defaultTranType;
		}
		StringBuffer sql_temp = new StringBuffer();
		
		//1.商户、用户 指定日期 充值成功
		if(Arrays.asList(a).contains("1")){
			sql_temp.append("SELECT A.PRDORDNO JNL_NO,A.CUST_ID,SUBSTR(A.ORDERTIME,1,8) TRAN_DATE,SUBSTR(A.ORDERTIME,9,6) TRAN_TIME,A.ORDAMT,NVL(A.TXNCOMAMT,'0') AS FEE,'CNY' AS CNY,"+
			"B.BANKCOD AS BNAK_CODE,'1' AS BIZ_TYPE,'C' AS CR,'' AS PRE_JNL_NO,A.ORDSTATUS,B.PAYTYPE FROM STPPRDINF A,STPPAYINF B WHERE A.PRDORDNO=B.PRDORDNO AND A.PAYORDNO=B.PAYORDNO"+
			" AND A.PRDORDTYPE IN ('1','3') AND A.ORDSTATUS='01'  AND  A.ORDERTIME >= '"+yesterDay+"000000' AND  A.ORDERTIME <='"+yesterDay+"235959' UNION ");
		}
		//2.用户 指定日期 消费成功
		if(Arrays.asList(a).contains("2")){
			sql_temp.append("SELECT A.PRDORDNO JNL_NO,A.CUST_ID,SUBSTR(A.ORDERTIME,1,8) TRAN_DATE,SUBSTR(A.ORDERTIME,9,6) TRAN_TIME,A.ORDAMT,NVL(A.TXNCOMAMT,'0') AS FEE,'CNY' AS CNY,"+
			"B.BANKCOD AS BNAK_CODE,'2' AS BIZ_TYPE,'D' AS CR,'' AS PRE_JNL_NO,A.ORDSTATUS,B.PAYTYPE FROM STPPRDINF A,STPPAYINF B WHERE A.PRDORDNO=B.PRDORDNO AND A.PAYORDNO=B.PAYORDNO"+
			" AND A.PRDORDTYPE IN ('0','2') AND A.ORDSTATUS IN ('01','12') AND A.ORDERTIME >= '"+yesterDay+"00000' AND  A.ORDERTIME <='"+yesterDay+"235959' UNION ");
		}
		//3.指定日期 转账成功（付款）
		if(Arrays.asList(a).contains("3")){
			sql_temp.append("SELECT A.TRAORDNO AS JNL_NO,A.traCusId CUST_ID,SUBSTR(A.TRATIME,1,8) TRAN_DATE,SUBSTR(A.TRATIME,9,6) TRAN_TIME,A.txAmt AS ORDAMT, FEE,'CNY' AS CNY,"+
					"'' AS BNAK_CODE,'3' AS BIZ_TYPE,'D' AS CR,'' AS PRE_JNL_NO,A.ORDSTATUS,"+
                    "'00' AS PAYTYPE FROM STPTRAINF A  WHERE A.ORDSTATUS = '01' AND A.TRATIME >= '"+yesterDay+"000000' AND  A.TRATIME <='"+yesterDay+"235959' UNION ");
		}
		//4.指定日期 转账成功（收款）
		if(Arrays.asList(a).contains("4")){
			sql_temp.append("SELECT A.TRAORDNO AS JNL_NO,A.getCusId AS CUST_ID,SUBSTR(A.TRATIME,1,8) TRAN_DATE,SUBSTR(A.TRATIME,9,6) TRAN_TIME,A.txAmt AS ORDAMT, FEE,'CNY' AS CNY,"+
                    "'' AS BNAK_CODE,'4' AS BIZ_TYPE,'C' AS CR,'' AS PRE_JNL_NO,A.ORDSTATUS,'00' AS PAYTYPE FROM STPTRAINF A  WHERE A.ORDSTATUS = '01' AND A.TRATIME >= '"+yesterDay+"000000' AND A.TRATIME <='"+yesterDay+"235959' UNION ");
		}
		//5.指定日期  提现成功
		if(Arrays.asList(a).contains("5")){
			sql_temp.append("SELECT CASORDNO AS JNL_NO,CUST_ID,SUBSTR(SUCDAT,1,8) TRAN_DATE,SUBSTR(SUCDAT,9,6) TRAN_TIME,TXAMT AS ORDAMT,FEE,'CNY' AS CNY,"+
			"'' AS BNAK_CODE, '5' AS BIZ_TYPE,'D' AS CR,'' AS PRE_JNL_NO,ORDSTATUS,'01' AS PAYTYPE"+
			" FROM STPCASINF WHERE ORDSTATUS = '02' AND SUCDAT >= '"+yesterDay+"000000' AND SUCDAT <='"+yesterDay+"235959' UNION ");
		}
		//6.指定日期 退款成功
		if(Arrays.asList(a).contains("6")){
			sql_temp.append("SELECT REFORDNO AS JNL_NO,CUST_ID,SUBSTR(RFREQDATE,1,8) TRAN_DATE,SUBSTR(RFREQDATE,9,6) TRAN_TIME,RFAMT AS ORDAMT,FEE,'CNY' AS CNY,"+
			"BANKCODE AS BNAK_CODE,'6' AS BIZ_TYPE,'C' AS CR,ORIPRDORDNO AS PRE_JNL_NO,ORDSTATUS,'01' AS PAYTYPE"+ 
			" FROM STPREFINF WHERE ORDSTATUS = '02' AND RFREQDATE >= '"+yesterDay+"000000' AND RFREQDATE <='"+yesterDay+"235959' UNION ");
		}
		//7.指定日期 调账成功（除平台账户）
		if(Arrays.asList(a).contains("7")){
			sql_temp.append("SELECT ADJUSTMENT_ID AS JNL_NO ,CUST_ID,SUBSTR(ADJUST_T,1,8) TRAN_DATE,SUBSTR(ADJUST_T,9,6) TRAN_TIME,ADJUST_AMT AS ORDAMT, '0' AS FEE,'CNY' AS CNY,"+
		    "'' AS BNAK_CODE,'7' AS BIZ_TYPE,DECODE(ADJUST_KIND,'1','C','D') AS CR,'' AS PRE_JNL_NO,STATUS AS ORDSTATUS,'00' AS PAYTYPE"+
            " FROM ARP_AC_ADJUSTMENT WHERE STATUS = '01'  AND CUST_ID  NOT LIKE '999999%' AND ADJUST_T >= '"+yesterDay+"000000' AND  ADJUST_T <='"+yesterDay+"235959' UNION ");
		}
		//8.指定日期  结算成功
		if(Arrays.asList(a).contains("8")){
			sql_temp.append("SELECT A.BATCH_NO AS JNL_NO,A.MER_NO,A.TX_DATE AS TRAN_DATE,A.TX_TIME AS TRAN_TIME ,"+
               "STL_AMT AS ORDAMT,TOT_TXN_FEE AS FEE,'CNY' AS CNY,REC_BANK_NAME AS BNAK_CODE ,'8' AS BIZ_TYPE,'D' AS CR,'' AS PRE_JNL_NO,"+
               "PAY_STL_STATUS AS ORDSTATUS,'01' AS PAYTYPE FROM ARP_BANK_PAY_STL A WHERE PAY_STL_STATUS = '2' AND STL_DATE = '"+yesterDay+"' UNION ");
		}
		String sql1=sql_temp.toString().trim();
		if(sql1.endsWith("UNION")){
			sql1=sql1.substring(0, sql1.length()-6);
		}
		System.out.println(sql1);
		int result1 = Atc.QueryInGroup(sql1, "TRAN_LIST", null);
		if(result1==-1){
			 Log.error("获取交易信息失败！");
			 return;
		}else{
			Log.info("查询成功！");
		}
		//生成txt文件
		String tmpPath = Provision.class.getClassLoader().getResource("").getPath(); 
	    String appPath = tmpPath.substring(0, tmpPath.indexOf("classes/")); //得到 /home/payment/tdshome/app/stp/
		String locPath  = appPath+Etf.getChildValue("LOCAL_DIR"); //模版文件放置位置 /home/payment/tdshome/app/stp/export/provision
		
		//判断目录是否存在
		File file1 =new File(locPath);
		if(!file1.exists()  || !file1.isDirectory()) { 
			file1.mkdirs();
			System.out.println(file1.mkdirs());
		}
		String filename="TRAN_"+yesterDay+".txt";
		String filePath =locPath+"/"+filename;
		
		File file2 =new File(filePath);
		if(!file2.exists()) file2.createNewFile();
		//写数据
		writeTxtFile(file2,"TRAN_LIST");
		
		//FTP上传
		String up_off=Etf.getChildValue("UP_OFF");
		if(!StringUtils.isEmpty(up_off) && up_off.equals("1")){
			Log.info("开始FTP上传...");
			String IpAdr=Etf.getChildValue("IpAdr");
			String Port=Etf.getChildValue("Port");
			String UsrNam=Etf.getChildValue("UsrNam");
			String UsrPwd=Etf.getChildValue("UsrPwd");
			String ObjDir=Etf.getChildValue("ObjDir");
			String LclDir=Etf.getChildValue("LOCAL_DIR");
			int upload_status= Atc.FtpPut(IpAdr, UsrNam, UsrPwd, ObjDir, filename , LclDir, filename, Port);
			if(upload_status == 0){
				Log.info("上传成功");
			}else{
				Log.info("上传失败");
		        return;
			}
		}else{
			Log.info("检查配置文件，不需要FTP上传文件。");
		}
		Log.info("交易正常结束");
	}
	
    /**
     * 读取系统配置文件
     * @param configFileUrl 配置文件路径
     * @param NodeName 需要解析的节点名称
     */
	public  static boolean ReadXmlConfig(String configFileUrl,String NodeName){
		int iResult = 1;
		try {
			iResult = TdFile.ReadXmlConfig(configFileUrl, NodeName, null, null, Msg.getInstance(ResourceLocator.class));
			if(0 != iResult){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param file  目标txt文件
	 * @param root  Etf节点
	 * @return  true 写入成 false 写入失败
	 */
	@SuppressWarnings("unchecked")
	public static boolean writeTxtFile(File file ,String root){
		FileOutputStream fos =null;
		OutputStreamWriter osw= null;
		BufferedWriter bw = null;
		try{
		fos = new FileOutputStream(file,true);
        osw = new OutputStreamWriter(fos);
        bw = new BufferedWriter(osw);

		List list = Etf.childs(root);
		StringBuffer sb;
		 for (Object obj : list) {
			 Element em = (Element)obj;
			 sb = new StringBuffer();
			 //交易流水号
			 sb.append(em.element("JNL_NO")==null?"":em.element("JNL_NO").getText());
			 sb.append("|");
			 //客户账号
			 sb.append(em.element("CUST_ID")==null?"":em.element("CUST_ID").getText());
			 sb.append("|");
			 //交易日期
			 sb.append(em.element("TRAN_DATE")==null?"":em.element("TRAN_DATE").getText());
			 sb.append("|");
			//交易时间
			 sb.append(em.element("TRAN_TIME")==null?"":em.element("TRAN_TIME").getText());
			 sb.append("|");
			 //交易金额
			 sb.append(em.element("ADJUST_AMT")==null?"0":em.element("ADJUST_AMT").getText());
			 sb.append("|");
			 //手续费金额
			 sb.append(em.element("FEE")==null?"0":em.element("FEE").getText());
			 sb.append("|");
			 //银行编号
			 String bnak_code = em.element("BNAK_CODE")==null?"":em.element("BNAK_CODE").getText();
			 String biz=em.element("BIZ_TYPE")==null?"":em.element("BIZ_TYPE").getText();
			 boolean bank=true;
			 if(StringUtils.isEmpty(bnak_code)){
				 if("5".equals(biz)|| "6".equals(biz) || "8".equals(biz)) bnak_code=Etf.getChildValue("A_BANK_CODE");
				 else bank=false;
			 }
			 
			 if(!bank){
				 sb.append("|");//银行编号
				 sb.append("|");//银行名称
				 sb.append("|");//银行账户名
				 sb.append("|");//银行账号
			 }else{
				 HashMap<String, String> map= m.get(bnak_code);
				 if(null== map){
					 sb.append("|");//银行编号
					 sb.append("|");//银行名称
					 sb.append("|");//银行账户名
					 sb.append("|");//银行账号
				 }
				 else{
					 sb.append(em.element("BNAK_CODE")==null?"0":em.element("BNAK_CODE").getText());
					 sb.append("|");
					 sb.append(map.get("BANK_NAME")==null?"":map.get("BANK_NAME"));
					 sb.append("|");
					 sb.append(map.get("LEG_REP")==null?"":map.get("LEG_REP"));
					 sb.append("|");
					 sb.append(map.get("BANK_REL_NO")==null?"":map.get("BANK_REL_NO"));
					 sb.append("|");
				 }
			 }
			 //业务类型
			 String biz_type = getBizType(em.element("BIZ_TYPE").getText(),em.element("PAYTYPE").getText());
			 
			 sb.append(biz_type==null?"":biz_type);
			 sb.append("|");
			 //借贷标记
			 sb.append(em.element("CR")==null?"":em.element("CR").getText());
			 sb.append("|");
			 //前期交易流水号
			 sb.append(em.element("PRE_JNL_NO")==null?"":em.element("PRE_JNL_NO").getText());
			 bw.write(sb.toString());
			 bw.newLine();
		 }
		 return true;
		 }catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			 try {
				 bw.flush();
				 bw.close();
		         osw.close();
		         fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static String getBizType(String bizType, String paytype) {
		//1.充值 2.消费 3.转账（付款） 4.转账（收款）5.提现 6.退款 7.调账 8.结算
		int i=Integer.parseInt(bizType);
		String biz_type="";
		switch (i) {
		case 1:
			biz_type=ProBusType.PBT_01.getCode();
			break;
		case 2:
			if(paytype.equals("00")) biz_type=ProBusType.PBT_01.getCode();
			else biz_type=ProBusType.PBT_51.getCode();
			break;
		case 3:
			biz_type=ProBusType.PBT_51.getCode();
			break;
		case 4:
			biz_type=ProBusType.PBT_51.getCode();
			break;
		case 5:
			biz_type=ProBusType.PBT_31.getCode();
			break;
		case 6:
			biz_type=ProBusType.PBT_31.getCode();
			break;
		case 7:
			biz_type=ProBusType.PBT_51.getCode();
			break;
		case 8:
			biz_type=ProBusType.PBT_31.getCode();
			break;
		default:
			break;
		}
		return biz_type;
	}
	
}
