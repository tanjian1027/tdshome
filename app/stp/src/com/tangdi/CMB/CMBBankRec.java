package com.tangdi.CMB;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import tangdi.annotations.Code;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import cmb.netpayment.Settle;

public class CMBBankRec {

	@Code("563117")
	public void exportQueryCMBBank() throws Exception {//分页方式按交易日查询已结帐定单信息
		Settle settle=Settle.class.newInstance();
		//参数设置
		int bankconn=settle.SetOptions("payment.ebank.cmbchina.com");
		if(bankconn!=0){
			String errormsg=settle.GetLastErr(bankconn);
			Log.info("----bankconn银行网站=%s--------", new Object[] {bankconn+ errormsg });
			return;
		}
		
		//获取招商银行的配置信息
	    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("CMBMer.properties");   
	    Properties p = new Properties();   
	    try {   
	       p.load(inputStream);   
	    } catch (IOException e1) {   
	       e1.printStackTrace();   
	    }
	    
	    String filePath=p.getProperty("FileToPath");//导出文件路径
	    
		String StrBranchID=p.getProperty("StrBranchID");//商户开户行代码,4位
		String StrCoNo=p.getProperty("StrCoNo");//商户代码及操作员代码6位或者10位。6位则为商户号，表示用管理员9999登录。10位则前面6位为商户号，后面4位为操作员号。
		String StrPwd=p.getProperty("StrPwd");//商户密码
		Log.info("----LoginC=%s--------", new Object[] { StrBranchID+"\t"+StrCoNo+"\t"+ StrPwd});
		
		//只有成功登录后，查询、结帐等方法才能被调用
		int islogin=settle.LoginC(StrBranchID, StrCoNo, StrPwd);
		if(islogin!=0){
			String errormsg=settle.GetLastErr(islogin);
			Log.info("----islogin是否登录网银=%s--------", new Object[] { islogin+errormsg});
			return;
		}
		
		String nowdate=getNowDate();//当前日期
		String beforeDate=getBeforeDate();//前一天日期
		Log.info("----nowdate =%s beforeDate--------", new Object[] { nowdate+"\t"+beforeDate});
		String StartDate=beforeDate;
		String EndDate=nowdate;
		int Count=10;
		StringBuffer StrBuf=new StringBuffer();
		//分页方式按交易日查询已结帐定单信息
		int queryint=settle.QuerySettledOrderByPage(StartDate, EndDate, Count, StrBuf);
		if(queryint!=0){
			String errormsg=settle.GetLastErr(queryint);
			Log.info("----queryint分页方式按交易日查询已结帐定单信息%s--------", new Object[] { queryint +errormsg});
			return;
		}
		//解析返回值stringBuffer
		String str=StrBuf.toString();
		String[] arr=str.split("\n");
		int len=arr.length;
		String ss="支付流水明细"+"\n\n出单日期["+nowdate+"]	记账日期["+beforeDate+"]"+"\n";
		ss=ss+"交易时间"+"\t"+"处理日期"+"\t"+"金额"+"\t"+"订单号"+"\t"+"订单状态"+"\t"+"卡类型"+"\t"+"手续费"+"\t"+"银行受理日期"+"\t"+"银行受理时间"+"\n";
		for (int i=0;i<len;i++){
			if(i%9==0 && i!=0 ){
				ss=ss+"\n"+arr[i]+"\t";
			}else{
				if(i==(len-1)){
					ss=ss+arr[i];
				}else{
					ss=ss+arr[i]+"\t";
				}
			}
		} 
		Log.info("----ss =%s --------", new Object[] { ss});
		/*
		String[] sst=ss.split("\n");
		int countlen=ss.split("\n").length;
		for(int i=4;i<countlen;i++){
			String statue=sst[i].split("\t")[4];
			String state="";
			if(statue.equals("0")){
				state="已结账";
			}else if(statue.equals("1")){
				state="已撤销";
			}else if(statue.equals("2")){
				state="部分结账";
			}else if(statue.equals("3")){
				state="退款记录";
			}else if(statue.equals("5")){
				state="无效状态";
			}else if(statue.equals("6")){
				state="未知状态";
			}
			String type=sst[i].split("\t")[5];
			String typename="";
			if(type.equals("02")){
				typename="一卡通";
			}else if(type.equals("03")){
				typename="信用卡";
			}else if(type.equals("04")){
				typename="其他行的卡";
			}
			
			ss.replaceAll(statue, state);
			ss.replaceAll(type, typename);
		}
		*/
		Log.info("----ss.split('n') =%s --------", new Object[] { ss});
		String WebPath=this.getClass().getClassLoader().getResource(".").getPath();
		
		String FileToPath=filePath;//导出对账文件路径
		String fileName ="CMB."+ getBeforeDate() ;
		Log.info("----fileName=%s--------", new Object[] {  FileToPath + "/" + fileName });
		String fileNamePath = FileToPath + "/" + fileName;
		File filename = new File(fileNamePath);
		if (!filename.exists()) {
			try {
				filename.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.info("----fileNamePath=%s--------", new Object[] { WebPath
					+ FileToPath + "/" + fileName + "已创建！" });
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					fileOutputStream);
			byte[] bb = ss.toString().getBytes("UTF-8");
			bufferedOutputStream.write(bb);
			bufferedOutputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Msg.setTmp("FILENAME", fileName);
		Etf.setChildValue("FILENAME", fileName);
		Log.info("----成功生成银行对账文件:%s--------", new Object[] { fileName });
		
		settle.Logout();//退出该函数
		
	}
	
	public static String getNowDate() {
		String temp_str = "";
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat da = new SimpleDateFormat("yyyyMMdd");
		temp_str = da.format(date);

		return temp_str;
	}
	public static String getBeforeDate() {
		String temp_str = "";
		Calendar calendar = Calendar.getInstance();
		calendar.add(5, -1);
		Date date = calendar.getTime();
		SimpleDateFormat da = new SimpleDateFormat("yyyyMMdd");
		temp_str = da.format(date);

		return temp_str;
	}
}
