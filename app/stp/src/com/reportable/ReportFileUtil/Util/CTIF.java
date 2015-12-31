package com.reportable.ReportFileUtil.Util ;

/**
 * 可疑主体信息
 * 
 * @author 施文
 * 
 */
public class CTIF
{
	String	CTNM	= null ;	// 可疑主体姓名/名称
	String	SMID	= null ;	// 主体特约商户编号
	String	CITP	= null ;	// 可疑主体身份证件/证明文件类型
	String	CTID	= null ;	// 可疑主体身份证件/证明文件号码
	CCIF CCIF = null;
	String	CTVC	= null ;	// 可疑主体的职�?行业类别
	String	CRNM	= null ;	// 可疑主体的法定代表人姓名
	String	CRIT	= null ;	// 可疑主体的法定代表人身份证件类型
	String	CRID	= null ;	// 可疑主体的法定代表人身份证件号码

	public String getCTNM()
	{
		return CTNM ;
	}

	public void setCTNM(String cTNM)
	{
		CTNM = cTNM ;
	}

	public String getSMID()
	{
		return SMID ;
	}

	public void setSMID(String sMID)
	{
		SMID = sMID ;
	}

	public String getCITP()
	{
		return CITP ;
	}

	public void setCITP(String cITP)
	{
		CITP = cITP ;
	}

	public String getCTID()
	{
		return CTID ;
	}

	public void setCTID(String cTID)
	{
		CTID = cTID ;
	}

	public CCIF getCCIF()
	{
		return CCIF ;
	}

	public void setCCIF(CCIF cCIF)
	{
		CCIF = cCIF ;
	}

	public String getCTVC()
	{
		return CTVC ;
	}

	public void setCTVC(String cTVC)
	{
		CTVC = cTVC ;
	}

	public String getCRNM()
	{
		return CRNM ;
	}

	public void setCRNM(String cRNM)
	{
		CRNM = cRNM ;
	}

	public String getCRIT()
	{
		return CRIT ;
	}

	public void setCRIT(String cRIT)
	{
		CRIT = cRIT ;
	}

	public String getCRID()
	{
		return CRID ;
	}

	public void setCRID(String cRID)
	{
		CRID = cRID ;
	}

}
