import tangdi.engine.Fact;
import tangdi.engine.FactStatus;
import tangdi.engine.context.Log;
import java.util.*;
import java.io.*;
import tangdi.type.TypeCoercer;
import tangdi.context.Context;
import com.google.common.base.Objects;
import com.tangdi.risk.common.CalcData2;
import com.tangdi.risk.rule.ExceptTran;
import com.tangdi.risk.rule.Mer;
import java.util.Map;
import tangdi.engine.DRule;
import tangdi.engine.Session;
import tangdi.engine.SessionInterface;

public class mer_rule_Rule1015 extends mer_rule {
  public mer_rule_Rule1015() {
       this.setRuleID("Rule1015");
    
  }
  
  public void init(final Session session) {
    super.init(session);
    TypeCoercer typeCoercer = (TypeCoercer)Context.getInstance(TypeCoercer.class);
    this.sParamDate=getSParamDate();
    //设置规则参数
    Object objParam_X = session.getDRuleParam("Rule1015","X");
    if(objParam_X!=null){
       this.X = typeCoercer.coerce(objParam_X,String.class);
    }
    //设置规则内置属性
    Object objPro_enable = session.getDRuleParam("Rule1015","enable");
    if(objPro_enable!=null){
       this.setEnable(typeCoercer.coerce(objPro_enable,Boolean.class));
    }
    Object objPro_salience = session.getDRuleParam("Rule1015","salience");
    if(objPro_salience!=null){
       this.setSalience(typeCoercer.coerce(objPro_salience,Integer.class));
    }
    init_InnerProperty(this);
    this.setRuleDesc("上周累计退款金额>="+X+"");
    
  }
  
  public boolean rules_Mer_mer_it(final Mer it) {
    return true;
  }
  
  public void callRule(final Session session) {
    Log.info("---------start callDRule");
    for ( Fact<Mer> factmer : session.getWorkingMemory().getFact( Mer.class )){
       Mer mer = factmer.getObjFact();
       if (factmer.getFactStatus()!=FactStatus.DELETE && rules_Mer_mer_it(mer)) {
    		 session.registerFact(factmer);
             rules_Rule1015_action(session,mer);
    		 session.unRegisterFact(factmer);
       }
    }
    Log.info("---------end callDRule");
    
  }
  
  public void rules_Rule1015_action(final SessionInterface it, final Mer mer) {
    String _ruleID = this.getRuleID();
    ExceptTran exTran = CalcData2.getRule1014(mer, null, null, "week", null, this.X, _ruleID);
    boolean _notEquals = (!Objects.equal(null, exTran));
    if (_notEquals) {
      ExceptTran _setExceTranAttr = this.setExceTranAttr(exTran, mer);
      exTran = _setExceTranAttr;
      it.<ExceptTran>insert(exTran);
    }
  }
  
  private String X;
  
  public Map<?,?> getParams() {
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("X",this.X);
    return map;
    
  }
  
  public void init_InnerProperty(final DRule it) {
    
  }
}
