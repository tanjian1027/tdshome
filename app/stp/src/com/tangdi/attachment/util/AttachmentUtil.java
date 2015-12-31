package com.tangdi.attachment.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import com.tangdi.attachment.bean.Attachment;

import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

/**
 * @author Jason Meng
 * @version 1.0
 * @desc 上传附件工具类
 * */
public class AttachmentUtil {
	/**
	 * @desc 保存附件
	 * */
	public static void saveAttachment(Attachment fj){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//新加的数据一律设置为 不可用，都必须经过保存修改附件记录为可用
		String sSql = "INSERT INTO ATTACHMENT() VALUES()";
		sSql = addColAndVal(sSql, "ID"        , fj.getId());
		sSql = addColAndVal(sSql, "MODULENAME", fj.getModuleName());
		sSql = addColAndVal(sSql, "TABLENAME" , fj.getTableName());
		sSql = addColAndVal(sSql, "PKID"      , fj.getPkId());
		sSql = addColAndVal(sSql, "LX"        , fj.getLx());
		sSql = addColAndVal(sSql, "ORDERNUM"  , fj.getOrderNum());
		sSql = addColAndVal(sSql, "FJNAME"    , fj.getFjName());
		sSql = addColAndVal(sSql, "FJPATH"    , fj.getFjPath());
		sSql = addColAndVal(sSql, "FJO"       , fj.getFjo());
		sSql = addColAndVal(sSql, "FJT"       , sdf.format(new Date()));
		sSql = addColAndVal(sSql, "SFSX"      , "0");
		//替换",)"为")"
		sSql = sSql.replaceAll(",\\)", ")");
		
		int iResult = Atc.ExecSql(null, sSql);
		if(0 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "保存附件成功！");
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "保存附件失败！");
		}
	}
	
	/**
	 * @desc 更新附件信息
	 * 现只提供通过ID作为条件更新PKID
	 * 针对主表已保存，附件修改时使用
	 * */
	public static void updateAttachment(Attachment fj){
		String sSql = "SELECT * FROM ATTACHMENT WHERE ID = '"+fj.getId()+"' ";
		int iResult = Atc.ReadRecord(sSql);
		if(0 != iResult){
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "查询出现问题！");
			return;
		}
		
		//删除同一类型的旧附件
		String sTableName = fj.getTableName();
		String sPkid      = fj.getPkId();
		String sLx        = fj.getLx();
		String sOrderNum  = fj.getOrderNum();
		sSql = "DELETE FROM ATTACHMENT ";
		sSql += " WHERE TABLENAME = '"+sTableName+"' ";
		sSql += " AND PKID = '"+sPkid+"' ";
		sSql += " AND LX = '"+sLx+"' ";
		sSql += " AND ORDERNUM = '"+sOrderNum+"' ";
		sSql += " AND ID != '"+fj.getId()+"' ";
		
		//执行删除
		Atc.ExecSql(null, sSql);
		
		//将附件SFSX设置为启用
		sSql = "UPDATE ATTACHMENT SET PKID = '"+fj.getPkId()+"',SFSX = '1' ";
		sSql += " WHERE ID = '"+fj.getId()+"' ";
				
		iResult = Atc.ExecSql(null, sSql);
		if(0 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "更新附件成功！");
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "更新附件失败！");
		}
	}
	
	/**
	 * @desc 更新附件 将ETF上的 FJ_ID_* 节点进行更新  针对主表未保存，已上传的附件使用
	 * */
	public static void updateAttachment(String sPkid){
		Element elEtf        = Etf.peek();
		List<Element> elList = elEtf.elements();
		String sName         = null;
		String sVal          = null;
		String sIn           = "";
		for(Element el:elList){
			sName = el.getName();
			sVal  = el.getText();
			
			if(sName.startsWith("FJID_")){
				sIn += "'"+sVal+"',";
			}
		}
		
		if("".equals(sIn)){
			Log.info(sPkid+"记录没有附件需要更新！");
			return;
		}
		
		sIn = sIn.substring(0, sIn.length()-1);
		
		String sSql = "UPDATE ATTACHMENT ";
		sSql += " SET PKID = '"+sPkid+"',SFSX = '1' ";
		sSql += " WHERE ID IN("+sIn+") ";
		int iResult = Atc.ExecSql(null, sSql);
		if(0 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "更新附件成功！");
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "更新附件失败！");
		}
	}
	
	/**
	 * @desc 查询附件信息(多附件 具体OrderNum不确定)
	 * 现只提供通过PKID&TABLENAME进行查询
	 * */
	public static void queryAttachmentMuti(Attachment fj){
		String sRec = "ATTACHMENT";
		//查询该记录对应的所有附件类型
		String sSql = "SELECT DISTINCT(LX) DISTINCT_LX FROM ATTACHMENT WHERE PKID = '"+fj.getPkId()+"' AND TABLENAME = '"+fj.getTableName()+"' AND SFSX = '1'";
		int iResult = Atc.PagedQuery("1", "100", sRec, sSql);
		if(0 == iResult){
			List<Element> elfList = Etf.childs(sRec);
			String sLx            = null;
			String sFjPath        = null;
			String sShowRelPath   = null;
			String sQryFjByLx     = null;
			//遍历所有附件类型
			for(Element etf:elfList){
				if(null == etf.selectSingleNode("DISTINCT_LX")){
					continue;
				}
				sLx        = etf.selectSingleNode("DISTINCT_LX").getText();
				//对不同类型的附件 分别进行查询
				sQryFjByLx = "SELECT * FROM ATTACHMENT WHERE PKID = '"+fj.getPkId()+"' AND TABLENAME = '"+fj.getTableName()+"' AND SFSX = '1' AND LX = '"+sLx+"' ORDER BY FJT ASC ";
				iResult = Atc.PagedQuery("1", "100", sRec+"_"+sLx, sQryFjByLx);
				if(0 != iResult){
					continue;
				}
				//增加FJSHOW字段 预览附件使用
				List<Element> elfListFjByLx = Etf.childs(sRec+"_"+sLx);
				for(Element etfFjLx:elfListFjByLx){
					if(null == etfFjLx.selectSingleNode("FJPATH")){
						continue;
					}
					sFjPath      = etfFjLx.selectSingleNode("FJPATH").getText();
					sShowRelPath = getRelPath(sFjPath);
					etfFjLx.addElement("FJSHOW").setText(sShowRelPath);
				}
			}
		}else if(2 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "未查到附件信息！");
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "查询附件失败！");
		}
	}
	
	/**
	 * @desc 查询附件信息
	 * 现只提供通过PKID&TABLENAME进行查询
	 * */
	public static void queryAttachment(Attachment fj){
		String sRec = "ATTACHMENT";
		String sSql = "SELECT * FROM ATTACHMENT WHERE PKID = '"+fj.getPkId()+"' AND TABLENAME = '"+fj.getTableName()+"' AND SFSX = '1' ORDER BY FJT ASC ";
		int iResult = Atc.PagedQuery("1", "200", sRec, sSql);
		if(0 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "查询附件成功！");
			
			Element el            = Etf.peek();
			Node recNode          = el.selectSingleNode(sRec);
			List<Node> childNode  = recNode.selectNodes("*");
			List<Element> elfList = Etf.childs(sRec);
			String sNodeName  = null;
			String sVal       = null;
			String sLx        = null;
			String sOrderNum  = null;
			String sLx_Ord    = null;
			for(Element etf:elfList){
				if(null != etf.selectSingleNode("LX")){
					sLx = etf.selectSingleNode("LX").getText();
				}else{
					continue;
				}
				if(null != etf.selectSingleNode("ORDERNUM")){
					sOrderNum = etf.selectSingleNode("ORDERNUM").getText();
				}else{
					continue;
				}
				
				sLx_Ord = "_"+sLx+"_"+sOrderNum;
				
				for(Node node:childNode){
					sNodeName = node.getName();
					if(null == etf.selectSingleNode(sNodeName)){
						continue;
					}
					sVal = etf.selectSingleNode(sNodeName).getText();
					Etf.setChildValue(sNodeName+sLx_Ord, sVal);
					
					if("FJPATH".equals(sNodeName)){
						//设置显示的绝对路径 FJSHOW_SLX_SORDERNUM 
						String sShowRelPath = getRelPath(sVal);
						Log.info("DD:%s",sShowRelPath);
						if(StringUtils.isNotEmpty(sShowRelPath)){
							Etf.setChildValue("FJSHOW"+sLx_Ord, sShowRelPath);
						}
					}
				}
			}
		}else if(2 == iResult){
			Etf.setChildValue("RSPCOD", "000000");
			Etf.setChildValue("RSPMSG", "未查到附件信息！");
		}else{
			Etf.setChildValue("RSPCOD", "000001");
			Etf.setChildValue("RSPMSG", "查询附件失败！");
		}
	}
	
	/**
	 * @desc 删除附件信息
	 * 现只提供通过ID进行删除
	 * */
	public static void deleteAttachment(Attachment fj,String sDirPath){
		String sSql    = "SELECT FJPATH FROM ATTACHMENT WHERE ID = '"+fj.getPkId()+"' ";
		String sFjPath = null;
		int iResult    = Atc.ReadRecord(sSql);
		if(0 != iResult){
			return;
		}
		
		sFjPath = Etf.getChildValue("FJPATH");
		sSql    = "DELETE FROM ATTACHMENT WHERE ID = '"+fj.getPkId()+"' ";
		iResult = Atc.ExecSql(null, sSql);
		if(0 != iResult){
			return;
		}
		
		File file = new File(sDirPath+sFjPath);
		if(file.exists()){
			file.delete();
		}
	}

	public static String getRelPath(String sStr){
		String sRes = "";
		if(sStr.toLowerCase().indexOf("/"+"webapps"+"/") != -1){
			int iIndex = sStr.toLowerCase().indexOf("/"+"webapps"+"/");
			sRes = sStr.substring(iIndex+8);
		}
		if(sRes.indexOf("\\")!=-1){
			sRes = sRes.replaceAll("\\\\", "/");
		}
		return sRes;
	}
	
	/**
	 * @author Jason
	 * @desc 拼装插入SQL语句
	 * @param sSql eg:INSERT INTO TABLE() VALUES()
	 * @param sCol eg:NAME
	 * @param sVal eg:Jason
	 * @return INSERT INTO TABLE(NAME,) VALUES('Jason',)
	 * 添加列和值
	 * */
	public static String addColAndVal(String sSql,String sCol,String sVal){
		//替换sql中的第一个")" 为 COL,)
		sSql = sSql.replaceFirst("\\)", sCol+",)");
		//对非空字段添加单引号
		if(null!=sVal){
			sVal = "'"+sVal+"'";
		}
		//替换sql中的最后一个")" 为 VAL,)
		sSql = sSql.substring(0, sSql.length()-1)+sVal+",)";
		return sSql;
	}
}
