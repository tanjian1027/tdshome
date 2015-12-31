package com.tangdi.attachment.tran;

import java.io.File;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang.StringUtils;

import com.tangdi.attachment.bean.Attachment;
import com.tangdi.attachment.util.AttachmentUtil;
import com.tangdi.attachment.util.FileOperator;
import com.tangdi.attachment.util.FileUtil;
import com.tangdi.attachment.util.BaseUtil;
import com.tangdi.attachment.util.ImageUtil;

import tangdi.annotations.Code;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.engine.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.expr.functions.TdExpBasicFunctions;
import tangdi.web.TdWebConstants;

/**
 * @author Jason Meng
 */
@DB
public class AttachmentManager {
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

	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		//brefore
		Msg.dump();
		ic.proceed();
		//after
		Msg.dump();
		return null;
	}
	
	//@Code("800406")
	/**
	 * 打开附件上传页面
	 */
	@Code("ATTA01")
	public void initUpload() throws Exception{
		Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME,"/upload/upload.jsp");
	}
	
	//@Code("800407")
	/**
	 * 附件上传并保存服务器 单附件
	 */
	@Code("ATTA02")
	public void uploadSingle() throws Exception{
		Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/upload/uploadCallBack.jsp");
		//表名
		String sTableName   = Etf.getChildValue("TABLENAME");
		//外键ID
		String sPkId        = Etf.getChildValue("PKID");
		//类型
		String sLx          = Etf.getChildValue("LX");
		//附件序号
		String sOrderNum    = Etf.getChildValue("ORDERNUM");
		
		//不传序号 则自动生成
		if(null == sOrderNum){
			//直接到毫秒日期显示有问题HHmmss sss 所以用空格隔开 然后替换空格
			sOrderNum = BaseUtil.formatCurrentDateTime("HHmmss sss").replaceAll(" ", "")+TdExpBasicFunctions.RANDOM(6, "2");
		}
		
		String sFilePath    = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILEPATH");
		String sFileOldName = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILENAME");
		Log.info("上传附件位置："+sFilePath);
		Log.info("上传文件名称："+sFileOldName);
	    
		//生成目录名称
		String sTempDir  = BaseUtil.getIdBySeqName("TEMP_DIR_NUM");
		//存储服务器的文件名称
		String sDateTime = BaseUtil.getCurrentDateTime();
		//文件类型
		String sFileType = FileUtil.getFileType(sFilePath);
		//创建文件
		File fjFile = new File(sFilePath);
		//获得文件所在目录
		String sFileParent = fjFile.getParent();
		
		//目标文件夹
		String sTargetDir = sFileParent+File.separator+sTempDir+File.separator+sDateTime+"."+sFileType;
		FileUtil.createFolder(sFileParent+File.separator+sTempDir);
		//将上传附件移动至目标文件夹下
		FileOperator.moveFile(sFilePath, sTargetDir);
		
		if(null!=sTargetDir && sTargetDir.indexOf("\\")!=-1){
			sTargetDir = sTargetDir.replaceAll("\\\\", "/");
		}
		
		//将附件信息保存至数据库
		Attachment fj = new Attachment();
		fj.setId(BaseUtil.getIdBySeqName("ATTACHMENT_ID"));
		fj.setTableName(sTableName);
		fj.setPkId(sPkId);
		fj.setLx(sLx);
		fj.setOrderNum(sOrderNum);
		fj.setFjPath(sTargetDir);
		fj.setFjName(sFileOldName);
		
		AttachmentUtil attachmentUtil = new AttachmentUtil();
		
		//保存附件信息
		attachmentUtil.saveAttachment(fj);
		//如果PKID有值  则可认为是更新操作
		if(StringUtils.isNotEmpty(sPkId)){
			attachmentUtil.updateAttachment(fj);
		}
		
		Etf.setChildValue("TMP_FJNAME"     , "FJNAME_"     +sLx+"_"+sOrderNum);
		Etf.setChildValue("TMP_FJID"       , "FJID_"       +sLx+"_"+sOrderNum);
		Etf.setChildValue("TMP_FJSHOW"     , "FJSHOW_"     +sLx+"_"+sOrderNum);
		Etf.setChildValue("FJNAME"     , fj.getFjName());
		Etf.setChildValue("FJID"       , fj.getId());
		Etf.setChildValue("FJSHOW"     , AttachmentUtil.getRelPath(fj.getFjPath()));
		
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "交易成功!");
	}
	
	/**
	 * 附件上传并保存服务器(多附件)
	 */
	@Code("ATTA06")
	public void upload() throws Exception{
		//表名
		String sTableName   = Etf.getChildValue("TABLENAME");
		//外键ID
		String sPkId        = Etf.getChildValue("PKID");
		//类型
		String sLx          = Etf.getChildValue("LX");
		//附件序号
		String sOrderNum    = Etf.getChildValue("ORDERNUM");
		
		//不传序号 则自动生成
		if(null == sOrderNum){
			//直接到毫秒日期显示有问题HHmmss sss 所以用空格隔开 然后替换空格
			sOrderNum = BaseUtil.formatCurrentDateTime("HHmmss sss").replaceAll(" ", "")+TdExpBasicFunctions.RANDOM(6, "2");
			Etf.setChildValue("ORDERNUM", sOrderNum);
		}
		
		String sFilePath    = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILEPATH");
		String sFileOldName = Etf.getChildValue("_REQUESTATTR.UPLOADFILES.FILE.FILENAME");
		Log.info("上传附件位置："+sFilePath);
		Log.info("上传文件名称："+sFileOldName);
	    
		//生成目录名称
		String sTempDir  = BaseUtil.getIdBySeqName("TEMP_DIR_NUM");
		//存储服务器的文件名称
		String sDateTime = BaseUtil.getCurrentDateTime();
		//文件类型
		String sFileType = FileUtil.getFileType(sFilePath);
		//创建文件
		File fjFile = new File(sFilePath);
		//获得文件所在目录
		String sFileParent = fjFile.getParent();
		
		//目标文件夹
		String sTargetDir = sFileParent+File.separator+sTempDir+File.separator+sDateTime+"."+sFileType;
		FileUtil.createFolder(sFileParent+File.separator+sTempDir);
		//将上传附件移动至目标文件夹下
		FileOperator.moveFile(sFilePath, sTargetDir);
		
		if(null!=sTargetDir && sTargetDir.indexOf("\\")!=-1){
			sTargetDir = sTargetDir.replaceAll("\\\\", "/");
		}
		
		//将附件信息保存至数据库
		Attachment fj = new Attachment();
		fj.setId(BaseUtil.getIdBySeqName("ATTACHMENT_ID"));
		fj.setTableName(sTableName);
		fj.setPkId(sPkId);
		fj.setLx(sLx);
		fj.setOrderNum(sOrderNum);
		fj.setFjPath(sTargetDir);
		fj.setFjName(sFileOldName);
		
		AttachmentUtil upload = new AttachmentUtil();
		//保存附件信息
		upload.saveAttachment(fj);
		
		Etf.setChildValue("FJTABLE"    , fj.getTableName());
		Etf.setChildValue("FJLX"       , fj.getLx());
		Etf.setChildValue("FJORDERNUM" , fj.getOrderNum());
		Etf.setChildValue("FJNAME"     , fj.getFjName());
		Etf.setChildValue("FJID"       , fj.getId());
		Etf.setChildValue("FJSHOW"     , AttachmentUtil.getRelPath(fj.getFjPath()));
		Etf.setChildValue("FJABATEDATE", fj.getAbateDate());
		
		Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "Success!");
	}
	
	//@Code("800408")
	/**
	 * 附件预览
	 * @throws Exception
	 */
	@Code("ATTA03")
	public void showFJ() throws Exception{
		String sFjShow = Etf.getChildValue("PATH");
		String sSql    = "SELECT FJPATH FROM ATTACHMENT WHERE FJPATH like '%"+sFjShow+"'";
		
		int iResult = Atc.ReadRecord(sSql);
	    if(0!=iResult){
	    	return;
	    }
		
	    String sFjPath = Etf.getChildValue("FJPATH");
	    
	    sFjShow = AttachmentUtil.getRelPath(sFjPath);
	    
		if(FileUtil.isImageShow(FileUtil.getFileType(sFjPath))){
			int iMaxVal = 680;
			int iWidth  = ImageUtil.getImgWidth(sFjPath);
			int iHeight = ImageUtil.getImgHeight(sFjPath);
			
			if(iWidth>iMaxVal || iHeight>iMaxVal){
				if(iWidth>iHeight){
					iHeight = iMaxVal*iHeight/iWidth;
					iWidth  = iMaxVal;
				}else{
					iWidth  = iMaxVal*iWidth/iHeight;
					iHeight = iMaxVal;
				}
			}
			
			//设置附件路径至request中
			Etf.setChildValue("FJSHOW", sFjShow);
			Etf.setChildValue("WIDTH" , iWidth+"");
			Etf.setChildValue("HEIGHT", iHeight+"");
		}else if(FileUtil.isSwfShow(FileUtil.getFileType(sFjShow))){
			//设置附件路径至request中
			Etf.setChildValue("FJSHOW", sFjShow);
		}else if(FileUtil.isMediaShow(FileUtil.getFileType(sFjShow))){
			//设置附件路径至request中
			Etf.setChildValue("FJSHOW", sFjShow);
		}else{
			//设置附件路径至request中
			Etf.setChildValue("FJSHOW", "/upload/defaultShow.jpg");
			Etf.setChildValue("WIDTH" , "680");
			Etf.setChildValue("HEIGHT", "680");
		}
		
		//设置默认为图片类型，为了进行页面无预览提示  必须有个默认值
		Etf.setChildValue("FILETYPE", "ISNOSHOW");
		
		if(FileUtil.isImageShow(FileUtil.getFileType(sFjShow))){
			Etf.setChildValue("FILETYPE", "ISIMAGE");
		}
		
		if(FileUtil.isSwfShow(FileUtil.getFileType(sFjShow))){
			Etf.setChildValue("FILETYPE", "ISSWF");
		}
		
		if(FileUtil.isMediaShow(FileUtil.getFileType(sFjShow))){
			Etf.setChildValue("FILETYPE", "ISMEDIA");
		}
		
		Etf.setChildValue("FJSHOW", sFjShow);
	    Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/upload/img_Show.jsp");
	}
	
	/**
	 * 查询附件路径信息
	 * @throws Exception
	 */
	@Code("ATTA04")
	public void queryAttachmentPath() throws Exception{
		String sId  = Etf.getChildValue("ID");
	    String sSql = "SELECT FJPATH FROM ATTACHMENT WHERE ID = '"+sId+"'";
	    int iResult = Atc.ReadRecord(sSql);
	    if(0!=iResult){
	    	Etf.setChildValue("FJPATH", "");
	    }
	}
	
	/**
	 * 删除附件信息
	 * @throws Exception
	 */
	@Code("ATTA05")
	public void deleteAttachment() throws Exception{
		//Etf.setChildValue("_REQUESTATTR.FORWARDURL", "/ajaxrequest.jsp");
		
		String sId  = Etf.getChildValue("ID");
	    String sSql = "SELECT FJPATH FROM ATTACHMENT WHERE ID = '"+sId+"'";
	    int iResult = Atc.ReadRecord(sSql);
	    if(0!=iResult){
	    	Etf.setChildValue("RspCod", "000000");
			Etf.setChildValue("RspMsg", "交易成功!");
			return;
	    }
	    //先删除附件文件
	    String sFjPath = Etf.getChildValue("FJPATH");
	    File file      = new File(sFjPath);
	    if(file.isFile() && file.exists()){
	    	file.delete();
	    }
	    //删除附件数据库记录
	    sSql    = "DELETE FROM ATTACHMENT WHERE ID = '"+sId+"'";
	    iResult = Atc.ExecSql(null, sSql);
	    if(0!=iResult){
	    	Etf.setChildValue("RspCod", "000001");
			Etf.setChildValue("RspMsg", "交易失败!");
			return;
	    }
	    Etf.setChildValue("RspCod", "000000");
		Etf.setChildValue("RspMsg", "交易成功!");
	}
}
