package com.tangdi.risk.common;

public class RcsDefault {

    /**交易状态   0:异常*/
    public static final String EXCEPT_TRAN_FLAG_0="0";
    /**交易状态  1：确认为可疑 */
    public static final String EXCEPT_TRAN_FLAG_1="1";
    /**交易状态  2：释放为正常 */
    public static final String EXCEPT_TRAN_FLAG_2="2";
    
	/** * 交易来源: 00线上 */
	public static final String TRAN_SOURCE_00="00";
	/** * 交易来源: 01线下 */
	public static final String TRAN_SOURCE_01="01";
	
	/** * 币种:0000 人民币 */
	public static final String COIN_FLG_0000="0000";
	/** * 币种:0001 外币  */
	public static final String COIN_FLG_0001="0001";
	
	/**  主体类型 0用户 */
	public static final String ENTITY_TYPE_0="0";
	/**  主体类型 1商户  */
	public static final String ENTITY_TYPE_1="1";
	/**  主体类型 2IP  */
	public static final String ENTITY_TYPE_2="2";
	
	/**  1:异常交易  */
	public static final String WARN_TYPE_1="1";
	/**  2:可疑交易 */
	public static final String WARN_TYPE_2="2"; 
	
	/**  卡种: 01 借记卡 */
	public static final String CARD_FLG_00="00"; 
	/** 卡种 :02 贷记卡 */
	public static final String CARD_FLG_01="01"; 
	
	/**  1 可用 */
	public static final String RCS_IS_USE="1"; 
	/**  0 不可用  */
	public static final String RCS_NOT_USE="0";
	
	/** 用户未知编号，默认-1*/
	public static final String USER_CODE_DEFAULT="-1";
	/** 币种未知信息，默认0000*/
	public static final String COIN_FLG_DEFAULT="0000";
	/** 卡种未知信息，默认-1*/
	public static final String CARD_FLG_FLG_DEFAULT="-1";
	/** IP未知编号，默认-1*/
	public static final String IP_DEFAULT="-1";
	
	/** 执行频率，1：天*/
	public static final String EXEC_RATE_1 = "1";
	/** 执行频率，2：周*/
	public static final String EXEC_RATE_2 = "2";
	/** 执行频率，3：月*/
	public static final String EXEC_RATE_3 = "3";
	/** 执行频率，4：季度*/
	public static final String EXEC_RATE_4 = "4";
	/** 执行频率，5：年*/
	public static final String EXEC_RATE_5 = "5";
	
	/** 规则引擎定义的两个文件*/
	public static final String[] ruleFiles = {"user_rule","mer_rule","ip_rule"};
}
