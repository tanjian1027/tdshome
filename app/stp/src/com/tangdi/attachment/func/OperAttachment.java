package com.tangdi.attachment.func;

import javax.inject.Named;

import com.tangdi.attachment.bean.Attachment;
import com.tangdi.attachment.util.AttachmentUtil;

public class OperAttachment {
	/**
	 * @desc 合并RESP节点，将RESP下面的LOTTERY 合并至一个RESP下
	 * 并将剩余的RESP节点进行删除，只保留一个完整的RESP节点
	 * */
	@Named("QryAttachmentInfo")
	public static int qryFjInfo(
			@Named("pkid") String sPkid,
			@Named("tableName") String sTableName) {
		int iRes = 0;
		Attachment fj = new Attachment();
		fj.setPkId(sPkid);
		fj.setTableName(sTableName);
		
		AttachmentUtil.queryAttachment(fj);
		return iRes;
	}
	
	/**
	 * @desc 查询附件信息 按照类型进行分组
	 * */
	@Named("QryAttachmentListInfo")
	public static int qryFjListInfo(
			@Named("pkid") String sPkid,
			@Named("tableName") String sTableName) {
		int iRes = 0;
		Attachment fj = new Attachment();
		fj.setPkId(sPkid);
		fj.setTableName(sTableName);
		
		AttachmentUtil.queryAttachmentMuti(fj);
		return iRes;
	}
	
	/**
	 * @desc 更新附件 将ETF上的 FJ_ID_* 节点进行更新  针对主表未保存，已上传的附件使用
	 * */
	@Named("UpdAttachmentInfo")
	public static int updFjInfo(@Named("pkid") String sPkid) {
		int iRes = 0;
		AttachmentUtil.updateAttachment(sPkid);
		return iRes;
	}
	
	/**
	 * @desc 更新附件 将ETF上的 FJ_ID_* 节点进行更新  针对主表已保存，已上传的附件使用
	 * */
	@Named("ModAttachmentInfo")
	public static int updFjInfo(@Named("id") String sId,
			@Named("tableName") String sTableName,
			@Named("pkid") String sPkid,
			@Named("lx") String sLx,
			@Named("orderNum") String sOrderNum) {
		int iRes = 0;
		
		Attachment fj = new Attachment();
		fj.setId(sId);
		fj.setTableName(sTableName);
		fj.setPkId(sPkid);
		fj.setLx(sLx);
		fj.setOrderNum(sOrderNum);
		
		AttachmentUtil.updateAttachment(fj);
		
		return iRes;
	}
}
