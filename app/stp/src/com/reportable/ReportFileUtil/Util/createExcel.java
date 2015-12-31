package com.reportable.ReportFileUtil.Util;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.Region;

public class createExcel {
	
	public static final Map<String,String> map= new HashMap();
	
	public static String createEx(RBIF rbif,String fileName) throws Exception{
		 // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("交易详细");  
        sheet.addMergedRegion(new Region(0,(short)0,0,(short)2));
        sheet.addMergedRegion(new Region(1,(short)0,1,(short)2));
        

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        HSSFCell cell = null;
        
        cell = row.createCell((short) 0);  
        cell.setCellValue("交易详细");  
        //cell.setCellStyle(style);
        
        row = sheet.createRow(1);  
        cell = row.createCell((short) 0);  
        cell.setCellValue("生成时间:"+new SimpleDateFormat("yyyy-MM-dd ").format(new Date()));  
        //cell.setCellStyle(style);
        
        
        style.setBorderTop(HSSFCellStyle.ALIGN_LEFT);  
        style.setBorderLeft(HSSFCellStyle.ALIGN_LEFT); 
        style.setBorderRight(HSSFCellStyle.ALIGN_LEFT);
        style.setBorderBottom(HSSFCellStyle.ALIGN_LEFT);
        
        STIF stif = new STIF();
        Field[] fieldList = CreateXML.getField(stif);
        row = sheet.createRow(2);  
		for (int i = 0; i < fieldList.length; i++) {
			cell = row.createCell((short) i);  
	        cell.setCellValue(getVal(fieldList[i].getName()));  
	        cell.setCellStyle(style);
		}
		String fieldName = null;
		String fieldType = null;
		String fieldValue = null;
		List<STIF> list = rbif.getSTIFs();
        for (int i = 0; i < list.size(); i++)  
        {  
        	stif = new STIF();
        	stif = list.get(i);
            row = sheet.createRow((int) i + 3);  
            for (int j = 0; j < fieldList.length; j++) {
    			fieldName = fieldList[j].getName();
    			fieldType = fieldList[j].getType().getName();
				fieldValue = CreateXML.getFieldValue(stif, fieldName,fieldType).toString();
				cell = row.createCell((short) j);
				cell.setCellValue(fieldValue.equals("@I")?"":fieldValue);  
				cell.setCellStyle(style);
    		}          
            cell.setCellStyle(style);
        }  
        // 第六步，将文件存到指定位置  
        try  
        {  
        	fileName = fileName.split("\\.")[0]+".xls";
            FileOutputStream fout = new FileOutputStream(fileName);  
            wb.write(fout);  
            fout.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        } 
        
        return fileName;
	}
	
	public static String getVal(String name){
		String val= "";
		if(map == null || map.size()<1){
			map.put("CTNM","可疑主体姓名/名称");
			map.put("CITP","可疑主体身份证件/证明文件类型");
			map.put("CTID","可疑主体身份证件/证明文件号码");
			map.put("CBAT","可疑主体的银行账号种类");
			map.put("CBAC","可疑主体的银行账号");
			map.put("CABM","可疑主体银行账号的开户银行名称");
			map.put("CTAT","可疑主体的支付账户种类");
			map.put("CTAC","可疑主体的支付账户账号");
			map.put("CPIN","可疑主体所在支付机构的名称");
			map.put("CPBA","可疑主体所在支付机构的银行账号");
			map.put("CPBN","可疑主体所在支付机构的银行账号的开户银行名称");
			map.put("CTIP","可疑主体的交易IP地址");
			map.put("TSTM","交易时间");
			map.put("CTTP","货币资金转移方式");
			map.put("TSDR","资金收付标志");
			map.put("CRPP","资金用途");
			map.put("CRTP","交易币种");
			map.put("CRAT","交易金额");
			map.put("TCNM","交易对手姓名/名称");
			map.put("TSMI","交易对手特约商户编号");
			map.put("TCIT","交易对手证件/证明文件类型");
			map.put("TCID","交易对手证件/证明文件号码");
			map.put("TCAT","交易对手的银行账号种类");
			map.put("TCBA","交易对手的银行账号");
			map.put("TCBN","交易对手银行账号的开户银行名称");
			map.put("TCTT","交易对手的支付账户种类");
			map.put("TCTA","交易对手的支付账户号码");
			map.put("TCPN","交易对手所在支付机构的名称");
			map.put("TCPA","交易对手所在支付机构的银行账号");
			map.put("TPBN","交易对手所在支付机构银行账号的开户银行名称");
			map.put("TCIP","交易对手的交易IP地址");
			map.put("TMNM","交易商品名称");
			map.put("BPTC","银行与支付机构之间的业务交易编码");
			map.put("PMTC","支付机构与商户之间的业务交易编码");
			map.put("TICD","业务标识号");
		}
		val = map.get(name);
		return val;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getVal("TICD"));
	}
}
