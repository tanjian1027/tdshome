package com.tangdi.risk.base;
/*
 * @desc   映射数据库中的交易流水表
 * @author Jason Meng
 * */
public class RCS_EXCEPT_TRAN_RULE {
	public static final String TABLE_NAME      = "RCS_EXCEPT_TRAN_RULE";      //表名
	public static final String RULE_CODE       = "RULE_CODE";                 //规则编号    
	public static final String RULE_NAME       = "RULE_NAME";                 //规则名称    
	public static final String RULE_TYPE       = "RULE_TYPE";                 //规则类型    
	public static final String IS_ONLINE       = "IS_ONLINE";                 //是否线上    
	public static final String EXCP_TYPE       = "EXCP_TYPE";                 //异常类型    
	public static final String COM_TYPE_NO     = "COM_TYPE_NO";               //商户业务类型
	public static final String RULE_DES        = "RULE_DES";                  //规则描述    
	public static final String RULE_LEVEL      = "RULE_LEVEL";                //规则等级    
	public static final String RULE_LEVEL_ITEM = "RULE_LEVEL_ITEM";           //一般预警项  
	public static final String RULE_START_DATE = "RULE_START_DATE";           //生效日期    
	public static final String RULE_END_DATE   = "RULE_END_DATE";             //失效日期    
	public static final String CREATE_NAME     = "CREATE_NAME";               //创建人      
	public static final String CREATE_DATE     = "CREATE_DATE";               //创建日期    
	public static final String CREATE_DATETIME = "CREATE_DATETIME";           //创建时间    
	public static final String UPDATE_NAME     = "UPDATE_NAME";               //维护人      
	public static final String UPDATE_DATE     = "UPDATE_DATE";               //维护日期    
	public static final String UPDATE_DATETIME = "UPDATE_DATETIME";           //维护时间    
	public static final String RULE_VER        = "RULE_VER";                  //规则版本号  
	public static final String BELONGSCOMPANY  = "BELONGSCOMPANY";            //所属公司编码
	public static final String EXEC_RATE       = "EXEC_RATE";                 //执行频率
	public static final String IS_USE          = "IS_USE";                    //是否可用    
}
