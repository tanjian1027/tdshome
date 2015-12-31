package com.reportable.ReportFileUtil.Util ;

/**
 * 可疑交易信息
 * @author 施文
 *
 */
public class STIF
{
	String  CTNM	= null;
	String 	CITP 	= null;
	String 	CTID	= null;
	String	CBAT	= null ;	// 可疑主体的银行账号种�?
	String	CBAC	= null ;	// 可疑主体的银行账�?
	String	CABM	= null ;	// 可疑主体银行账号的开户银行名�?
	String	CTAT	= null ;	// 可疑主体的支付账户种�?
	String	CTAC	= null ;	// 可疑主体的支付账户账�?
	String	CPIN	= null ;	// 可疑主体�?��支付机构的名�?
	String	CPBA	= null ;	// 可疑主体�?��支付机构的银行账�?
	String	CPBN	= null ;	// 可疑主体�?��支付机构的银行账号的�?��银行名称
	String	CTIP	= null ;	// 可疑主体的交易IP地址
	String	TSTM	= null ;	// 交易时间
	String	CTTP	= null ;	// 货币资金转移方式
	String	TSDR	= null ;	// 资金收付标志
	String	CRPP	= null ;	// 资金用�?
	String	CRTP	= null ;	// 交易币种
	String	CRAT	= null ;	// 交易金额
	String	TCNM	= null ;	// 交易对手姓名/名称
	String	TSMI	= null ;	// 交易对手特约商户编号
	String	TCIT	= null ;	// 交易对手证件/证明文件类型
	String	TCID	= null ;	// 交易对手证件/证明文件号码
	String	TCAT	= null ;	// 交易对手的银行账号种�?
	String	TCBA	= null ;	// 交易对手的银行账�?
	String	TCBN	= null ;	// 交易对手银行账号的开户银行名�?
	String	TCTT	= null ;	// 交易对手的支付账户种�?
	String	TCTA	= null ;	// 交易对手的支付账户号�?
	String 	TCPN	= null ;	// 交易对手所在支付机构的名称
	String	TCPA	= null ;	// 交易对手�?��支付机构的银行账�?
	String	TPBN	= null ;	// 交易对手�?��支付机构银行账号的开户银行名�?
	String	TCIP	= null ;	// 交易对手的交易IP地址
	String	TMNM	= null ;	// 交易商品名称
	String	BPTC	= null ;	// 银行与支付机构之间的业务交易编码
	String	PMTC	= null ;	// 支付机构与商户之间的业务交易编码
	String	TICD	= null ;	// 业务标识�?
	public String getCBAT()
	{
		return CBAT ;
	}
	public void setCBAT(String cBAT)
	{
		CBAT = cBAT ;
	}
	public String getCBAC()
	{
		return CBAC ;
	}
	public void setCBAC(String cBAC)
	{
		CBAC = cBAC ;
	}
	public String getCABM()
	{
		return CABM ;
	}
	public void setCABM(String cABM)
	{
		CABM = cABM ;
	}
	public String getCTAT()
	{
		return CTAT ;
	}
	public void setCTAT(String cTAT)
	{
		CTAT = cTAT ;
	}
	public String getCTAC()
	{
		return CTAC ;
	}
	public void setCTAC(String cTAC)
	{
		CTAC = cTAC ;
	}
	public String getCPIN()
	{
		return CPIN ;
	}
	public void setCPIN(String cPIN)
	{
		CPIN = cPIN ;
	}
	public String getCPBA()
	{
		return CPBA ;
	}
	public void setCPBA(String cPBA)
	{
		CPBA = cPBA ;
	}
	public String getCPBN()
	{
		return CPBN ;
	}
	public void setCPBN(String cPBN)
	{
		CPBN = cPBN ;
	}
	public String getCTIP()
	{
		return CTIP ;
	}
	public void setCTIP(String cTIP)
	{
		CTIP = cTIP ;
	}
	public String getTSTM()
	{
		return TSTM ;
	}
	public void setTSTM(String tSTM)
	{
		TSTM = tSTM ;
	}
	public String getCTTP()
	{
		return CTTP ;
	}
	public void setCTTP(String cTTP)
	{
		CTTP = cTTP ;
	}
	public String getTSDR()
	{
		return TSDR ;
	}
	public void setTSDR(String tSDR)
	{
		TSDR = tSDR ;
	}
	public String getCRPP()
	{
		return CRPP ;
	}
	public void setCRPP(String cRPP)
	{
		CRPP = cRPP ;
	}
	public String getCRTP()
	{
		return CRTP ;
	}
	public void setCRTP(String cRTP)
	{
		CRTP = cRTP ;
	}
	public String getCRAT()
	{
		return CRAT ;
	}
	public void setCRAT(String cRAT)
	{
		CRAT = cRAT ;
	}
	public String getTCNM()
	{
		return TCNM ;
	}
	public void setTCNM(String tCNM)
	{
		TCNM = tCNM ;
	}
	public String getTSMI()
	{
		return TSMI ;
	}
	public void setTSMI(String tSMI)
	{
		TSMI = tSMI ;
	}
	public String getTCIT()
	{
		return TCIT ;
	}
	public void setTCIT(String tCIT)
	{
		TCIT = tCIT ;
	}
	public String getTCID()
	{
		return TCID ;
	}
	public void setTCID(String tCID)
	{
		TCID = tCID ;
	}
	public String getTCAT()
	{
		return TCAT ;
	}
	public void setTCAT(String tCAT)
	{
		TCAT = tCAT ;
	}
	public String getTCBA()
	{
		return TCBA ;
	}
	public void setTCBA(String tCBA)
	{
		TCBA = tCBA ;
	}
	public String getTCBN()
	{
		return TCBN ;
	}
	public void setTCBN(String tCBN)
	{
		TCBN = tCBN ;
	}
	public String getTCTT()
	{
		return TCTT ;
	}
	public void setTCTT(String tCTT)
	{
		TCTT = tCTT ;
	}
	public String getTCTA()
	{
		return TCTA ;
	}
	public void setTCTA(String tCTA)
	{
		TCTA = tCTA ;
	}
	public String getTCPA()
	{
		return TCPA ;
	}
	public void setTCPA(String tCPA)
	{
		TCPA = tCPA ;
	}
	public String getTPBN()
	{
		return TPBN ;
	}
	public void setTPBN(String tPBN)
	{
		TPBN = tPBN ;
	}
	public String getTCIP()
	{
		return TCIP ;
	}
	public void setTCIP(String tCIP)
	{
		TCIP = tCIP ;
	}
	public String getTMNM()
	{
		return TMNM ;
	}
	public void setTMNM(String tMNM)
	{
		TMNM = tMNM ;
	}
	public String getBPTC()
	{
		return BPTC ;
	}
	public void setBPTC(String bPTC)
	{
		BPTC = bPTC ;
	}
	public String getPMTC()
	{
		return PMTC ;
	}
	public void setPMTC(String pMTC)
	{
		PMTC = pMTC ;
	}
	public String getTICD()
	{
		return TICD ;
	}
	public void setTICD(String tICD)
	{
		TICD = tICD ;
	}
	public String getCTNM() {
		return CTNM;
	}
	public void setCTNM(String cTNM) {
		CTNM = cTNM;
	}
	public String getCITP() {
		return CITP;
	}
	public void setCITP(String cITP) {
		CITP = cITP;
	}
	public String getCTID() {
		return CTID;
	}
	public void setCTID(String cTID) {
		CTID = cTID;
	}
	public String getTCPN() {
		return TCPN;
	}
	public void setTCPN(String tCPN) {
		TCPN = tCPN;
	}
	
}
