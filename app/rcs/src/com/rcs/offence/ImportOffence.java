package com.rcs.offence;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.dom4j.Element;

import tangdi.annotations.Code;
import tangdi.atc.Atc;
import tangdi.atc.TdFile;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.log.ILog;
import tangdi.util.ResourceLocator;
import tangdi.web.TdWebConstants;

import com.tangdi.util.GetOwnButton;
import com.util.DateUtil;
import com.util.IdCardUtil;
import com.util.RcsUtils;

@tangdi.engine.DB
public class ImportOffence {
	private static Map<Integer, Map<String, String>> cellMap = new HashMap<Integer, Map<String,String>>();
	static{
		cellMap = new HashMap<Integer, Map<String,String>>();
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("女", "0");
		tempMap.put("男", "1");
		cellMap.put(1, tempMap);
		
		tempMap = new HashMap<String, String>();
		tempMap.put("犯罪", "0");
		tempMap.put("涉恐", "1");
		cellMap.put(2, tempMap);
		
		tempMap = new HashMap<String, String>();
		tempMap.put("身份证"     , "0");
		tempMap.put("护照"       , "1");
		tempMap.put("武警身份证"  , "2");
		tempMap.put("回乡证"     , "3");
		tempMap.put("户口本"     , "4");
		tempMap.put("企业营业执照", "9");
		tempMap.put("台胞证"     , "C");
		tempMap.put("外国公民护照", "D");
		tempMap.put("临时身份证"  , "F");
		tempMap.put("警官证"     , "G");
		tempMap.put("港澳身份证"  , "I");
		tempMap.put("勘查证件"    , "J");
		tempMap.put("其他"       , "Z");
		cellMap.put(4, tempMap);
	}
	
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		//brefore
		ic.proceed();
		//after
		GetOwnButton.getOwnButton();
		return null;
	}
	
	/**
	 *  恐怖分子模板下载
	 * @throws Exception
	 */
	@Code("540328")
	public void excelOut(ILog logger) throws Exception {
		Msg.set("_REQUESTATTR.REDIRECTURL", RcsUtils.getCommParam("G_COMMParams", "G_FORWARDPATH") + "Terrorist_Temp.xls");// 给页面一个连接下载文件
	}

	@Code("540325")
	public void touView(String[] args) {
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/WEB-INF/html/offence/addmoreuser.jsp");
	}
	
	/**
	 *导入涉恐犯罪分子信息
	 * @throws Exception 
	 * @throws Exception
	 */
	@Code("540326")
	public void importBatch(String[] args) throws Exception {
		Msg.dump();
	    String FILENAME = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILEPATH");
	    Log.info("上传附件位置："+FILENAME);
		String FILEOLDNAME = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILENAME");
	    Log.info("上传文件名称："+FILEOLDNAME);
	    
		Etf.setChildValue("FILENAME", "");
		Etf.setChildValue("MsgTyp"  , "N");
		Etf.setChildValue("RspCod"  , "000000");
		Etf.setChildValue("RspMsg"  , "上传成功");
		Etf.setChildValue("CALLBACKTYPE", "closeCurrent");
		
		Msg.set(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
		
		
		//读取配置文件
		int iResult = TdFile.ReadXmlConfig("etc/RCS_CONFIG.XML", "RcsGetPay", null, null, Msg.getInstance(ResourceLocator.class));
		if(0 != iResult){
			return ;
		}
		
		String appPath = ((ResourceLocator)Msg.getInstance(ResourceLocator.class)).getRoot() + SystemUtils.FILE_SEPARATOR;
		String IpAdr = Etf.getChildValue("IpAdr");
		String Port = Etf.getChildValue("Port");
		String UsrNam = Etf.getChildValue("UsrNam");
		String UsrPwd = Etf.getChildValue("UsrPwd");
		String ObjDir = Etf.getChildValue("ObjDir");
		String LclDir = Etf.getChildValue("LclDir");
		 Log.info("IpAdr："+IpAdr);
		 Log.info("Port："+Port);
		 Log.info("UsrNam："+UsrNam);
		 Log.info("UsrPwd："+UsrPwd);
		 Log.info("ObjDir："+ObjDir);
		 Log.info("LclDir："+LclDir);
		 iResult = Atc.FtpGet(IpAdr, UsrNam, UsrPwd, ObjDir, FILEOLDNAME, appPath+LclDir, FILEOLDNAME,Port);
		 
		 Log.info("Atc.FtpGet return :" + iResult);
		 FILENAME=appPath+LclDir+FILEOLDNAME;
		if(StringUtils.isEmpty(FILENAME)){
			Etf.setChildValue("RspCod"  , "000001");
			Etf.setChildValue("RspMsg"  , "上传失败！");
			return;
		}else{
			File file = new File(FILENAME);
			if(!file.exists()){
				Etf.setChildValue("RspCod"  , "000001");
				Etf.setChildValue("RspMsg"  , "上传失败！");
				return;
			}
		}
		
		if(!isValidTemplateFile(FILENAME)){
			Etf.setChildValue("RspCod"  , "000001");
			Etf.setChildValue("RspMsg"  , "附件不合法！");
			return ;
		}else{
			Log.info("上传附件格式合法！");
		}
		
		if(!isRepeat(FILENAME)){
			Etf.setChildValue("RspCod"  , "000001");
			Etf.setChildValue("RspMsg"  , "人员重复！");
			return ;
		}else{
			Log.info("上传没有重复内容！");
		}
		
		if(!isValidCertNo(FILENAME)){
			Etf.setChildValue("RspCod"  , "000001");
			Etf.setChildValue("RspMsg"  , "身份证号码错误！");
			return ;
		}else{
			Log.info("上传身份证号码正确！");
		}
		
		try {
			Log.info("读取xls");
			Log.info("读取xls path:%s",(FILENAME));
			FileInputStream finput =new FileInputStream(appPath+LclDir+FILEOLDNAME);
			POIFSFileSystem fs = new POIFSFileSystem(finput);
			HSSFWorkbook workbook = new HSSFWorkbook(fs); // 创建对Excel工作簿文件的引用
			int sheetSum = workbook.getNumberOfSheets();
			Log.info("sheet个数为 ："+sheetSum);
			for(int i=0;i < sheetSum;i++){
				Log.info("挨个sheet读取数据，目前读取第"+i+"个");
				HSSFSheet sheet = workbook.getSheetAt(i); // 创建对工作表的引用(表单)
				int rows = sheet.getPhysicalNumberOfRows();//获取表格的
				Log.info("行数为 ："+rows);
				List<String> listValues = new ArrayList<String>();
				for (int r = 1; r < rows; r++) { //循环遍历表格的行
					String value = "";
					HSSFRow row = sheet.getRow(r); //获取单元格中指定的行对象
					if (row != null) {
						int cells = row.getPhysicalNumberOfCells();//获取单元格中指定列对象
						for (int c = 0; c < cells; c++) { //循环遍历单元格中的列                  
							HSSFCell cell = row.getCell((short) c); //获取指定单元格中的列 
							if (cell != null) {
								if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) { //判断单元格的值是否为字符串类型
									Log.info("单元格值:"+c+":"+cell.getStringCellValue()+":"+getCodeVal(c,cell.getStringCellValue()));
									
									value += "'"+getCodeVal(c,cell.getStringCellValue()) + "',";
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) { //判断单元格的值是否为数字类型
									value += "'"+(int)cell.getNumericCellValue() + "',";
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) { //判断单元格的值是否为布尔类型
									value += "'"+cell.getStringCellValue() + "',";
								}
								//最后一列去掉","
								if(c == (cells-1)){
									if(!"".equals(value)){
										value = value.substring(0, value.length()-1);
									}
								}
							}
						}
					}
					if(!"".equals(value)){
						listValues.add(value);
						Log.info("增加值为 ："+value);
					}
				}
				Log.info("数据条数为 ："+listValues.size());
				for(int j = 0; j <listValues.size();j++){
					Log.info("--------------插入第"+i+"条数据=="+listValues.get(j).toString());
					String sql = "INSERT INTO RCS_OFFENCE_INFO(ID,TERR_NAME,TERR_SEX,TERR_TYPE,TERR_COUNTRY,PAPER_TYPE,PAPER_CODE) values( SEQ_RCSOFFENCEINFO_ID.nextval,"+listValues.get(j).toString()+")";
					Log.info("sql="+sql);
					Atc.ExecSql(null, sql);
					//int result = Atc.ExecSql(null, sql);
				}
			}
			
			//上传结束后，删除该次上传附件
			File file = new File(FILENAME);
			if(file.exists()){
				file.delete();
			}
			
		} catch (Exception e) {
			Log.error(e,"");
		}
	}
	
	private String getCodeVal(int c,HSSFCell cell){
		String sRes = null;
		if (cell != null) {
			if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) { //判断单元格的值是否为字符串类型
				sRes = getCodeVal(c,cell.getStringCellValue());
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) { //判断单元格的值是否为数字类型
				sRes = ""+(int)cell.getNumericCellValue();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) { //判断单元格的值是否为布尔类型
				sRes = "'"+cell.getStringCellValue() + "',";
			}
		}
		return sRes;
	}
	
	private String getCodeVal(int iCellIndex,String sText){
		String sRes = null;
		if(null != cellMap.get(iCellIndex)){
			sRes = cellMap.get(iCellIndex).get(sText);
		}
		if(null == sRes){
			sRes = sText;
		}
		return sRes;
	}
	
	
	
	
	/**
	 * 判断时候合法EXCEL文件
	 * @param sFileName
	 * @return
	 */
	private boolean isValidTemplateFile(String sFileName){
		try {
			Log.info("读取xls");
			FileInputStream finput = new FileInputStream(sFileName);
			POIFSFileSystem fs     = new POIFSFileSystem(finput);
			HSSFWorkbook workbook  = new HSSFWorkbook(fs); // 创建对Excel工作簿文件的引用
			int sheetSum = workbook.getNumberOfSheets();
			Log.info("sheet个数为 ："+sheetSum);
			
			if(sheetSum<=0){
				return false;
			}
			
			int iCountNum = 0;
			
			for(int i=0;i < sheetSum;i++){
				Log.info("挨个sheet读取数据，目前读取第"+i+"个");
				HSSFSheet sheet = workbook.getSheetAt(i); // 创建对工作表的引用(表单)
				int rows        = sheet.getPhysicalNumberOfRows();//获取表格的
				Log.info("行数为 ："+rows);
				
				if(rows<=1){
					continue;
				}
				
				for (int r = 1; r < rows; r++) { //循环遍历表格的行
					HSSFRow row = sheet.getRow(r); //获取单元格中指定的行对象
					if (row != null) {
						String sXm   = getCodeVal(0,row.getCell((short)0));//姓名
						String sXb   = getCodeVal(1,row.getCell((short)1));//性别
						String sSk   = getCodeVal(2,row.getCell((short)2));//涉恐类型
						String sGj   = getCodeVal(3,row.getCell((short)3));//国籍
						String sZj   = getCodeVal(4,row.getCell((short)4));//证件类型
						String sZjhm = getCodeVal(5,row.getCell((short)5));//证件号码
						
						Log.info("姓名：%s，性别：%s，涉恐：%s，国籍：%s，证件：%s，证件号码：%s", sXm,sXb,sSk,sGj,sZj,sZjhm);
						
						//不检测 国籍 字段
						if(StringUtils.isEmpty(sXm)
							&& StringUtils.isEmpty(sXb)
							&& StringUtils.isEmpty(sSk)
							&& StringUtils.isEmpty(sZj)
							&& StringUtils.isEmpty(sZjhm)){
							Log.info("所有信息为空：跳过改行！");
							continue;
						}
						
						//不检测 国籍 字段
						if(StringUtils.isEmpty(sXm)
							|| StringUtils.isEmpty(sXb)
							|| StringUtils.isEmpty(sSk)
							|| StringUtils.isEmpty(sZj)
							|| StringUtils.isEmpty(sZjhm)){
							return false;
						}
						
						iCountNum+=1;
					}
				}
			}
			Log.info("iCountNum：%s",String.valueOf(iCountNum));
			if(iCountNum==0){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info("解析上传文件出现错误。");
			return false;
		}
		return true;
	}
	
	
	
	
	
	/**
	 * 判断时候合法身份证号
	 * @param sFileName
	 * @return
	 */
	private boolean isValidCertNo(String sFileName){
		try {
			//Log.info("读取xls");
			FileInputStream finput = new FileInputStream(sFileName);
			POIFSFileSystem fs     = new POIFSFileSystem(finput);
			HSSFWorkbook workbook  = new HSSFWorkbook(fs); // 创建对Excel工作簿文件的引用
			int sheetSum = workbook.getNumberOfSheets();
			//Log.info("sheet个数为 ："+sheetSum);
			
			if(sheetSum<=0){
				return false;
			}
			
			for(int i=0;i < sheetSum;i++){
				//Log.info("挨个sheet读取数据，目前读取第"+i+"个");
				HSSFSheet sheet = workbook.getSheetAt(i); // 创建对工作表的引用(表单)
				int rows        = sheet.getPhysicalNumberOfRows();//获取表格的
				//Log.info("行数为 ："+rows);
				
				if(rows<=1){
					continue;
				}
				
				IdCardUtil icu = new IdCardUtil();
				
				for (int r = 1; r < rows; r++) { //循环遍历表格的行
					HSSFRow row = sheet.getRow(r); //获取单元格中指定的行对象
					if (row != null) {
						String sZj   = getCodeVal(4,row.getCell((short)4));//证件类型
						String sZjhm = getCodeVal(5,row.getCell((short)5));//证件号码
						Log.info("sZj:%s，sZjhm:%s。", sZj,sZjhm);
						//检测证件号码是否正确
						if(StringUtils.isNotEmpty(sZj) && StringUtils.isNotEmpty(sZjhm)){
							Log.info("证件类型:%s，证件号码:%s。", sZj,sZjhm);
							if("0".equals(sZj)){
								if(!"身份证号合法".equals(icu.IDCardValidate(sZjhm))){
									return false;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info("解析上传文件出现错误。");
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否重复
	 * @param sFileName
	 * @return
	 */
	private boolean isRepeat(String sFileName){
		try {
			//Log.info("读取xls");
			FileInputStream finput = new FileInputStream(sFileName);
			POIFSFileSystem fs     = new POIFSFileSystem(finput);
			HSSFWorkbook workbook  = new HSSFWorkbook(fs); // 创建对Excel工作簿文件的引用
			int sheetSum = workbook.getNumberOfSheets();
			//Log.info("sheet个数为 ："+sheetSum);
			
			System.out.println("sheet个数为 ："+sheetSum);
			if(sheetSum<=0){
				return false;
			}
			
			Map<String, String> zjAndHmMap = new HashMap<String, String>();
			
			for(int i=0;i < sheetSum;i++){
				//Log.info("挨个sheet读取数据，目前读取第"+i+"个");
				HSSFSheet sheet = workbook.getSheetAt(i); // 创建对工作表的引用(表单)
				int rows        = sheet.getPhysicalNumberOfRows();//获取表格的
				//Log.info("行数为 ："+rows);
				
				Log.info("行数为 ："+rows);
				if(rows<=1){
					continue;
				}
				
				for (int r = 1; r < rows; r++) { //循环遍历表格的行
					HSSFRow row = sheet.getRow(r); //获取单元格中指定的行对象
					if (row != null) {
						String sXm   = getCodeVal(0,row.getCell((short)0));//姓名
						String sXb   = getCodeVal(1,row.getCell((short)1));//性别
						String sSk   = getCodeVal(2,row.getCell((short)2));//涉恐类型
						String sGj   = getCodeVal(3,row.getCell((short)3));//国籍
						String sZj   = getCodeVal(4,row.getCell((short)4));//证件类型
						String sZjhm = getCodeVal(5,row.getCell((short)5));//证件号码
						
						//不检测 国籍 字段
						if(StringUtils.isEmpty(sXm)
							|| StringUtils.isEmpty(sXb)
							|| StringUtils.isEmpty(sSk)
							|| StringUtils.isEmpty(sZj)
							|| StringUtils.isEmpty(sZjhm)){
							continue;
						}
						
						if(null == zjAndHmMap.get(sZj+"_"+sZjhm)){
							zjAndHmMap.put(sZj+"_"+sZjhm,sXm);
						}else{
							continue;
						}
						
						String sSql = "SELECT SYSDATE FROM RCS_OFFENCE_INFO WHERE PAPER_TYPE = '"+sZj+"' AND PAPER_CODE = '"+sZjhm+"'";
						int iResult = Atc.ReadRecord(sSql);
						if(iResult == 0){
							return false;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info("解析上传文件出现错误。");
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		ImportOffence io = new ImportOffence();
//		System.out.println(io.isValidTemplateFile("C:/Users/Jason/Downloads/Terrorist_Temp.xls"));
		
		String FILENAME = "C:/Users/Jason/Desktop/Terrorist_Temp.xls";
		
		try {
			System.out.println("读取xls");
			FileInputStream finput =new FileInputStream(FILENAME);
			POIFSFileSystem fs = new POIFSFileSystem(finput);
			HSSFWorkbook workbook = new HSSFWorkbook(fs); // 创建对Excel工作簿文件的引用
			int sheetSum = workbook.getNumberOfSheets();
			System.out.println("sheet个数为 ："+sheetSum);
			for(int i=0;i < sheetSum;i++){
				System.out.println("挨个sheet读取数据，目前读取第"+i+"个");
				HSSFSheet sheet = workbook.getSheetAt(i); // 创建对工作表的引用(表单)
				int rows = sheet.getPhysicalNumberOfRows();//获取表格的
				System.out.println("行数为 ："+rows);
				List<String> listValues = new ArrayList<String>();
				for (int r = 1; r < rows; r++) { //循环遍历表格的行
					String value = "";
					HSSFRow row = sheet.getRow(r); //获取单元格中指定的行对象
					if (row != null) {
						int cells = row.getPhysicalNumberOfCells();//获取单元格中指定列对象
						for (int c = 0; c < cells; c++) { //循环遍历单元格中的列                  
							HSSFCell cell = row.getCell((short) c); //获取指定单元格中的列 
							if (cell != null) {
								if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) { //判断单元格的值是否为字符串类型
									System.out.println("单元格值:"+c+":"+cell.getStringCellValue()+":"+io.getCodeVal(c,cell.getStringCellValue()));
									
									value += "'"+io.getCodeVal(c,cell.getStringCellValue()) + "',";
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) { //判断单元格的值是否为数字类型
									value += "'"+(int)cell.getNumericCellValue() + "',";
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) { //判断单元格的值是否为布尔类型
									value += "'"+cell.getStringCellValue() + "',";
								}
								//最后一列去掉","
								if(c == (cells-1)){
									if(!"".equals(value)){
										value = value.substring(0, value.length()-1);
									}
								}
							}
						}
					}
					if(!"".equals(value)){
						listValues.add(value);
						System.out.println("增加值为 ："+value);
					}
				}
				
			}
			
		} catch (Exception e) {
			//Log.error(e,"");
			e.printStackTrace();
		}
	}
	
	/**用户与犯罪分子姓名比较，固定跑批任务
	 *
	 * @param logger
	 * @throws Exception
	 */
	@Code("541900")
	@tangdi.interceptor.Log
	public void UX00001(ILog logger) throws Exception {
		String sql1 = "select ID , TERR_NAME from RCS_OFFENCE_INFO";//恐怖名单表
		int result1 =  Atc.QueryInGroup(sql1, "REC1", null);
		
		String sql2 = "select USER_CODE, USER_NAME from RCS_TRAN_USER_INFO";//用户名单表
		int result2 =  Atc.QueryInGroup(sql2, "REC2", null);
		
		if(result1 == 0 && result2 == 0){		
			List<Element> list1 = Etf.childs("REC1");
			List<Element> list2 = Etf.childs("REC2");
			for(int i=0;i<list1.size();i++){
				Element em1 = (Element) list1.get(i);
				String name1 = RcsUtils.getElementTextByKey(em1, "TERR_NAME");
				String id = RcsUtils.getElementTextByKey(em1, "ID");
				Log.info("此恐怖名单表名："+name1);
				for(int j=0;j<list2.size();j++){
					Element em2 = (Element) list2.get(i);
					String name2 = RcsUtils.getElementTextByKey(em2, "TERR_NAME");
					String userId = RcsUtils.getElementTextByKey(em2, "USER_CODE");
					Log.info("此用户名单表"+name2);
					if(name1.equals(name2)){
						//插入涉恐用户表	RCS_OFFENCE_USER
						String sInsertSql = "INSERT INTO RCS_OFFENCE_USER() VALUES()";
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "USER_CODE"      , userId);
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "OFF_ID"         , id);
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "OFF_TYPE"       , "0");
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "CREATE_NAME"    , "system");
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "CREATE_DATE"    , DateUtil.getCurrentDate());
						sInsertSql = RcsUtils.addColAndVal(sInsertSql, "CREATE_DATETIME", DateUtil.getCurrentDateTime());
						
						sInsertSql = sInsertSql.replaceAll(",\\)", ")");
						
						Atc.ExecSql(null, sInsertSql);
						 
						//插入会名单表 客户X名单系统生成表	 RCS_X_LIST_SYS
						RcsUtils.setUserRisk("0", userId, "2", "");
					}
				}
			}
		}
		
		Msg.set("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
	}
}