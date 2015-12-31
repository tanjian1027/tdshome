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
	public void exportQueryCMBBank() throws Exception {//��ҳ��ʽ�������ղ�ѯ�ѽ��ʶ�����Ϣ
		Settle settle=Settle.class.newInstance();
		//��������
		int bankconn=settle.SetOptions("payment.ebank.cmbchina.com");
		if(bankconn!=0){
			String errormsg=settle.GetLastErr(bankconn);
			Log.info("----bankconn������վ=%s--------", new Object[] {bankconn+ errormsg });
			return;
		}
		
		//��ȡ�������е�������Ϣ
	    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("CMBMer.properties");   
	    Properties p = new Properties();   
	    try {   
	       p.load(inputStream);   
	    } catch (IOException e1) {   
	       e1.printStackTrace();   
	    }
	    
	    String filePath=p.getProperty("FileToPath");//�����ļ�·��
	    
		String StrBranchID=p.getProperty("StrBranchID");//�̻������д���,4λ
		String StrCoNo=p.getProperty("StrCoNo");//�̻����뼰����Ա����6λ����10λ��6λ��Ϊ�̻��ţ���ʾ�ù���Ա9999��¼��10λ��ǰ��6λΪ�̻��ţ�����4λΪ����Ա�š�
		String StrPwd=p.getProperty("StrPwd");//�̻�����
		Log.info("----LoginC=%s--------", new Object[] { StrBranchID+"\t"+StrCoNo+"\t"+ StrPwd});
		
		//ֻ�гɹ���¼�󣬲�ѯ�����ʵȷ������ܱ�����
		int islogin=settle.LoginC(StrBranchID, StrCoNo, StrPwd);
		if(islogin!=0){
			String errormsg=settle.GetLastErr(islogin);
			Log.info("----islogin�Ƿ��¼����=%s--------", new Object[] { islogin+errormsg});
			return;
		}
		
		String nowdate=getNowDate();//��ǰ����
		String beforeDate=getBeforeDate();//ǰһ������
		Log.info("----nowdate =%s beforeDate--------", new Object[] { nowdate+"\t"+beforeDate});
		String StartDate=beforeDate;
		String EndDate=nowdate;
		int Count=10;
		StringBuffer StrBuf=new StringBuffer();
		//��ҳ��ʽ�������ղ�ѯ�ѽ��ʶ�����Ϣ
		int queryint=settle.QuerySettledOrderByPage(StartDate, EndDate, Count, StrBuf);
		if(queryint!=0){
			String errormsg=settle.GetLastErr(queryint);
			Log.info("----queryint��ҳ��ʽ�������ղ�ѯ�ѽ��ʶ�����Ϣ%s--------", new Object[] { queryint +errormsg});
			return;
		}
		//��������ֵstringBuffer
		String str=StrBuf.toString();
		String[] arr=str.split("\n");
		int len=arr.length;
		String ss="֧����ˮ��ϸ"+"\n\n��������["+nowdate+"]	��������["+beforeDate+"]"+"\n";
		ss=ss+"����ʱ��"+"\t"+"��������"+"\t"+"���"+"\t"+"������"+"\t"+"����״̬"+"\t"+"������"+"\t"+"������"+"\t"+"������������"+"\t"+"��������ʱ��"+"\n";
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
				state="�ѽ���";
			}else if(statue.equals("1")){
				state="�ѳ���";
			}else if(statue.equals("2")){
				state="���ֽ���";
			}else if(statue.equals("3")){
				state="�˿��¼";
			}else if(statue.equals("5")){
				state="��Ч״̬";
			}else if(statue.equals("6")){
				state="δ֪״̬";
			}
			String type=sst[i].split("\t")[5];
			String typename="";
			if(type.equals("02")){
				typename="һ��ͨ";
			}else if(type.equals("03")){
				typename="���ÿ�";
			}else if(type.equals("04")){
				typename="�����еĿ�";
			}
			
			ss.replaceAll(statue, state);
			ss.replaceAll(type, typename);
		}
		*/
		Log.info("----ss.split('n') =%s --------", new Object[] { ss});
		String WebPath=this.getClass().getClassLoader().getResource(".").getPath();
		
		String FileToPath=filePath;//���������ļ�·��
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
					+ FileToPath + "/" + fileName + "�Ѵ�����" });
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
		Log.info("----�ɹ��������ж����ļ�:%s--------", new Object[] { fileName });
		
		settle.Logout();//�˳��ú���
		
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
