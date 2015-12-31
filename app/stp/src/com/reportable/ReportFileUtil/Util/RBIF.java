package com.reportable.ReportFileUtil.Util ;

import java.util.ArrayList;
import java.util.List;


/**
 * 可疑交易报告基本信息
 * @author 施文
 *
 */
public class RBIF
{
	String	RINM	= null ;	// 报告机构名称
	String	FIRC	= null ;	// 报告机构�?��地区编码
	String 	FICD	= null ;	// 报告机构分支机构/网点代码
	String	RFSG	= null ;	// 报�?次数标志
	String	ORXN	= null ;	// 初次报�?的可疑交易报文名�?
	String	SSTM	= null ;	// 可疑交易处理情况
	String	STCR	= null ;	// 可疑交易特征
	String	SSDS	= null ;	// 可疑交易描述
	String	UDSI	= null ;	// 机构自定可疑交易标准编号
	String	SCTN	= null ;	// 可疑主体数量
	String	TTNM	= null ;	// 可疑交易数量
	List<CTIF> CTIFs = new ArrayList();		 // 可疑主体信息列表
	List<STIF> STIFs = new ArrayList(); 	//可以交易信息列表
	public String getRINM()
	{
		return RINM ;
	}
	public void setRINM(String rINM)
	{
		RINM = rINM ;
	}
	public String getFIRC()
	{
		return FIRC ;
	}
	public void setFIRC(String fIRC)
	{
		FIRC = fIRC ;
	}
	public String getRFSG()
	{
		return RFSG ;
	}
	public void setRFSG(String rFSG)
	{
		RFSG = rFSG ;
	}
	public String getORXN()
	{
		return ORXN ;
	}
	public void setORXN(String oRXN)
	{
		ORXN = oRXN ;
	}
	public String getSSTM()
	{
		return SSTM ;
	}
	public void setSSTM(String sSTM)
	{
		SSTM = sSTM ;
	}
	public String getSTCR()
	{
		return STCR ;
	}
	public void setSTCR(String sTCR)
	{
		STCR = sTCR ;
	}
	public String getSSDS()
	{
		return SSDS ;
	}
	public void setSSDS(String sSDS)
	{
		SSDS = sSDS ;
	}
	public String getUDSI()
	{
		return UDSI ;
	}
	public void setUDSI(String uDSI)
	{
		UDSI = uDSI ;
	}
	public String getSCTN()
	{
		return SCTN ;
	}
	public void setSCTN(String sCTN)
	{
		SCTN = sCTN ;
	}
	public String getTTNM()
	{
		return TTNM ;
	}
	public void setTTNM(String tTNM)
	{
		TTNM = tTNM ;
	}
	public List<CTIF> getCTIFs()
	{
		return CTIFs ;
	}
	public void setCTIFs(List<CTIF> cTIFs)
	{
		CTIFs = cTIFs ;
	}
	public List<STIF> getSTIFs()
	{
		return STIFs ;
	}
	public void setSTIFs(List<STIF> sTIFs)
	{
		STIFs = sTIFs ;
	}
	public String getFICD() {
		return FICD;
	}
	public void setFICD(String fICD) {
		FICD = fICD;
	}
	
	
}
