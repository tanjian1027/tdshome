import tangdi.engine.Fact;
import tangdi.engine.FactStatus;
import tangdi.engine.context.Log;
import java.util.*;
import java.io.*;
import tangdi.type.TypeCoercer;
import tangdi.context.Context;
import com.tangdi.risk.common.RcsDefault;
import com.tangdi.risk.rule.ExceptTran;
import com.tangdi.risk.rule.Mer;
import tangdi.engine.AbstractRuleSet;
import tangdi.engine.DRule;

public class mer_rule extends DRule {
  public static AbstractRuleSet createRuleSet() {
    AbstractRuleSet abs = new AbstractRuleSet();
    abs.addRule(new mer_rule_Rule1008());
    abs.addRule(new mer_rule_Rule1009());
    abs.addRule(new mer_rule_Rule1010());
    abs.addRule(new mer_rule_Rule1011());
    abs.addRule(new mer_rule_Rule1012());
    abs.addRule(new mer_rule_Rule1013());
    abs.addRule(new mer_rule_Rule1014());
    abs.addRule(new mer_rule_Rule1015());
    abs.addRule(new mer_rule_Rule1016());
    abs.addRule(new mer_rule_Rule1017());
    abs.addRule(new mer_rule_Rule1018());
    
    return abs;
    
  }
  
  public ExceptTran setExceTranAttr(final ExceptTran exTran, final Mer mer) {
    String _ruleID = this.getRuleID();
    exTran.setRULE_CODE(_ruleID);
    exTran.setTRAN_SOURCE(RcsDefault.TRAN_SOURCE_01);
    String _cOMP_CODE = mer.getCOMP_CODE();
    exTran.setUSER_CODE(_cOMP_CODE);
    String _replaceAll = this.sParamDate.replaceAll("-", "");
    String _replaceAll_1 = _replaceAll.replaceAll("/", "");
    exTran.setREGDT_DAY(_replaceAll_1);
    exTran.setENTITY_TYPE(RcsDefault.ENTITY_TYPE_1);
    exTran.setEXCEPT_TRAN_FLAG(RcsDefault.EXCEPT_TRAN_FLAG_0);
    return exTran;
  }
  
  public String getSParamDate() {
    TypeCoercer typeCoercer = (TypeCoercer)Context.getInstance(TypeCoercer.class);
    if(session.getGlobal("sParamDate")!=null){
        return typeCoercer.coerce(session.getGlobal("sParamDate"),String.class);
    }else{
        return this.sParamDate;
    }
    
  }
  
  public String sParamDate;
}
