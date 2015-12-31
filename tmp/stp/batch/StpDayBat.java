import tangdi.Batch.AbstactBatch;
import tangdi.annotations.Data;
import tangdi.atc.Atc;
import tangdi.engine.Batch.BatchUtil;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.Batch.bean.Job;
import java.util.ArrayList;

/**
 * ϵͳ����
 */
public class StpDayBat extends AbstactBatch {
  @Data
  private String MsgType;
  
  @Data
  private String  RspCod;
  
  @Data
  private String  RspMsg;
  
  /**
   * ϵͳ����
   */
  private void init() {
    this.jobs = new ArrayList<Job>();
    	String[] depends0 = {};
    	jobs.add(new Job("SysDatChg",depends0,false));
    	String[] depends1 = {"SysDatChg"};
    	jobs.add(new Job("AutoSetAcc",depends1,false));
    	String[] depends2 = {"SysDatChg"};
    	jobs.add(new Job("PrdLosEff",depends2,false));
    	String[] depends3 = {"SysDatChg"};
    	jobs.add(new Job("UpdPubAct",depends3,false));
    	String[] depends4 = {"UpdPubAct"};
    	jobs.add(new Job("RecordAcBalHis",depends4,false));
    	String[] depends5 = {"SysDatChg"};
    	jobs.add(new Job("CredLosEff",depends5,false));
    	String[] depends6 = {"SysDatChg"};
    	jobs.add(new Job("UserBalMon",depends6,false));
    	String[] depends7 = {"SysDatChg"};
    	jobs.add(new Job("Balance",depends7,false));
    	
  }
  
  /**
   * ϵͳ����
   */
  public StpDayBat() {
    init();
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_SysDatChg() throws Exception {
    Log.info("\u03F5\u0373\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "990101", "150");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\u03F5\u0373\uFFFD\uFFFD\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_AutoSetAcc() throws Exception {
    Log.info("\uFFFD\u033B\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u047C\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "601012", "500");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\u033B\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u047C\uFFFD\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_PrdLosEff() throws Exception {
    Log.info("\uFFFD\uFFFD\uFFFD\uFFFD\u02A7\u0427 \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "990106", "350");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\uFFFD\uFFFD\uFFFD\u02A7\u0427 \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_UpdPubAct() throws Exception {
    Log.info("\uFFFD\uFFFD\uFFFD\u00B9\uFFFD\uFFFD\uFFFD\uFFFD\u02FB\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "990107", "500");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\uFFFD\uFFFD\u00B9\uFFFD\uFFFD\uFFFD\uFFFD\u02FB\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_RecordAcBalHis() throws Exception {
    Log.info("\uFFFD\u02FB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u02B7\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "990113", "500");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\u01FC\uFFFD\uFFFD\u02FB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u02B7\uFFFD\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_CredLosEff() throws Exception {
    Log.info("\u05A4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "990112", "500");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\u05A4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_UserBalMon() throws Exception {
    Log.info("\uFFFD\u00FB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "561100", "350");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\u00FB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  private void runJob_Balance() throws Exception {
    Log.info("\uFFFD\u037B\uFFFD\uFFFD\uFFFD\uEC68\uFFFD\uFFFD \uFFFD\uFFFD\u02BC\u05B4\uFFFD\uFFFD");
    Msg.dump();
    String _childValue = Etf.getChildValue("_JOBDATA_.SysCod");
    Etf.setChildValue("SysCod", _childValue);
    Atc.callService("stp", "700005", "300");
    Msg.dump();
    String rspcod = Etf.getChildValue("RspCod");
    Etf.setChildValue("_JOBDATA_.RspCod", rspcod);
    String _childValue_1 = Etf.getChildValue("RspMsg");
    Etf.setChildValue("_JOBDATA_.RspMsg", _childValue_1);
    boolean _equals = rspcod.equals("00000");
    boolean _not = (!_equals);
    if (_not) {
      String _childValue_2 = Etf.getChildValue("RspMsg");
      BatchUtil.jobFail(_childValue_2);
    }
    Log.info("\uFFFD\u037B\uFFFD\uFFFD\uFFFD\uEC68\uFFFD\uFFFD \u05B4\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD.");
  }
  
  /**
   * ϵͳ����
   */
  public void runJob(final String job) throws Exception {
    
    	
    	if(job.equals("SysDatChg")){
    	 runJob_SysDatChg();
    	}
    	if(job.equals("AutoSetAcc")){
    	 runJob_AutoSetAcc();
    	}
    	if(job.equals("PrdLosEff")){
    	 runJob_PrdLosEff();
    	}
    	if(job.equals("UpdPubAct")){
    	 runJob_UpdPubAct();
    	}
    	if(job.equals("RecordAcBalHis")){
    	 runJob_RecordAcBalHis();
    	}
    	if(job.equals("CredLosEff")){
    	 runJob_CredLosEff();
    	}
    	if(job.equals("UserBalMon")){
    	 runJob_UserBalMon();
    	}
    	if(job.equals("Balance")){
    	 runJob_Balance();
    	}
    	
  }
  
  /**
   * ϵͳ����
   */
  public String getName() {
    
    	return  "StpDayBat";
    	
  }
}
