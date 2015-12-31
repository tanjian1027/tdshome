package com.resource;


import java.io.FileOutputStream;

import net.sf.excelutils.ExcelUtils;

import org.apache.commons.lang.SystemUtils;

import com.util.RcsUtils;

import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.util.ResourceLocator;

public class ResourceFile{
  /**
   * excelMode 模板  此名字要和模板名字一致 ;
   * fileName  生成新文件，可以随便起名
   */
	public static void FtpPut(String excelMode, String fileName)throws Exception {
		// java项目所在路径
		String appPath = ((ResourceLocator) Msg.getInstance(ResourceLocator.class)).getRoot()+ SystemUtils.FILE_SEPARATOR;
		// 模板文件
		String configFile = appPath + "dat/" + excelMode + ".xls";
		// resin端存放生成文件路径
		String localDir =  RcsUtils.getCommParam("G_COMMParams", "G_APPLYNAME");
		Log.info("文件生生路径为:" + localDir);
		ExcelUtils.export(configFile, new FileOutputStream(localDir + fileName));// 模板文件生成到-->
	}
	
	/**
	 * excelMode 模板  此名字要和模板名字一致
	 * fileName  生成新文件，可以随便起名
	 */
	public static void FtpPut2(String excelMode, String fileName,String ExportUrl) throws Exception {
		//模板文件路径
		String config = ExportUrl + excelMode + ".xls";
		//文件生成件
		String localDir = RcsUtils.getCommParam("G_COMMParams", "G_APPLYNAME");
		ExcelUtils.export(config, new FileOutputStream(localDir + fileName));//模板文件生成到-->
	}
}