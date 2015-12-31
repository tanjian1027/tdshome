package com.util;

import java.util.HashMap;
import java.util.Map;

public class RcsDefault {
	
	/** * 交易来源: 00线上 */
	public static final String TRAN_SOURCE_00="00";
	/** * 交易来源: 01线下 */
	public static final String TRAN_SOURCE_01="01";
	
	/** 用户未知编号，默认-1*/
	public static final String USER_CODE_DEFAULT="-1";
	/** 币种未知信息，默认0000*/
	public static final String COIN_FLG_DEFAULT="0000";
	/** 卡种未知信息，默认-1*/
	public static final String CARD_FLG_FLG_DEFAULT="-1";
	
	/** 限额表支付方式 00全部*/
	public static final String LIMIT_BUS_CLIENT_00="00";
	/** 限额表支付方式 01网银 */
	public static final String LIMIT_BUS_CLIENT_01="01";
	/** 限额表支付方式 02终端 */
	public static final String LIMIT_BUS_CLIENT_02="02";
	/** 限额表支付方式 03消费卡 */
	public static final String LIMIT_BUS_CLIENT_03="03";
	/** 限额表支付方式 04虚拟账户 */
	public static final String LIMIT_BUS_CLIENT_04="04";
	/** 限额表支付方式05快捷支付 */
	public static final String LIMIT_BUS_CLIENT_05="05";
	public static final Map map_TRAN_CLIENT;
	static{
		map_TRAN_CLIENT = new HashMap();
		map_TRAN_CLIENT.put("01","网银");
		map_TRAN_CLIENT.put("02","终端");
		map_TRAN_CLIENT.put("03","消费卡");
		map_TRAN_CLIENT.put("04","虚拟账户");
		map_TRAN_CLIENT.put("05","快捷支付");
		map_TRAN_CLIENT.put("","未知");
	}
	/**证件类型*/
	public static final Map map_PAPER_TYPE;
	static{
		map_PAPER_TYPE = new HashMap();
		map_PAPER_TYPE.put("0","身份证");
		map_PAPER_TYPE.put("1","护照");
		map_PAPER_TYPE.put("2","武警身份证");
		map_PAPER_TYPE.put("3","回乡证");
		map_PAPER_TYPE.put("","未知");
	}
	 /**交易类型*/
	public static final Map map_TRAN_TYPE;
	static{
		map_TRAN_TYPE = new HashMap();
		map_TRAN_TYPE.put("0", "其他");
		map_TRAN_TYPE.put("1", "充值");
		map_TRAN_TYPE.put("2", "消费");
		map_TRAN_TYPE.put("3", "撤销");
		map_TRAN_TYPE.put("4", "预授权完成");
		map_TRAN_TYPE.put("5", "预授权完成撤销");
		map_TRAN_TYPE.put("6", "转账(虚拟帐号间)");
		map_TRAN_TYPE.put("7", "提现");
		map_TRAN_TYPE.put("8", "退款");
	}
	/**1,涉恐  ；0，犯罪*/
	public static final Map map_TERR_TYPE;
	static{
		map_TERR_TYPE = new HashMap();
		map_TERR_TYPE.put("1", "涉恐");
		map_TERR_TYPE.put("0", "犯罪");
	}
    
	/** 认证状态0：未认证*/
	public static final String USER_APP_FLAG_0="0";
	/** 认证状态1 审核中 */
	public static final String USER_APP_FLAG_1="1";
	/** 认证状态2：已通过*/
	public static final String USER_APP_FLAG_2="2";
	/** 认证状态3未通过*/
	public static final String USER_APP_FLAG_3="3";
	/**0：白名单 默认*/
    public static final String LIMIT_RISK_FLAG_0="0";
    /**1：灰名单*/
    public static final String LIMIT_RISK_FLAG_1="1";	
    /**2：黑名单*/
    public static final String LIMIT_RISK_FLAG_2="2";	
    /**3：红名单 */
    public static final String LIMIT_RISK_FLAG_3="3";	
    /** 4 全部*/
    public static final String LIMIT_RISK_FLAG_4="4";
    /**商户交易限额表 :商户类型 LIMIT_TYPE   =1:所有商户，3指定商户*/
    public static final String LIMIT_TYPE_1="1";
    
    /**流水表id */
    public static final String RCS_TRAN_SERIAL_RECORD_ID = "SEQ_ON_RCS.nextval";
    /**流水表id */
    public static final String ID_RCS_X_LIST_SYS = "SEQ_ON_RCS.nextval";
    
    
    /**交易状态   0:异常*/
    public static final String EXCEPT_TRAN_FLAG_0="0";
    /**交易状态  1：确认为可疑 */
    public static final String EXCEPT_TRAN_FLAG_1="1";
    /**交易状态  2：释放为正常 */
    public static final String EXCEPT_TRAN_FLAG_2="2";
    
    /**商户等级:  0：无级别*/
    public static final String RISK_LEAVEL_0="0";
    /**商户等级:  1：一级*/
    public static final String RISK_LEAVEL_1="1";
    /**商户等级:  2：二级*/
    public static final String RISK_LEAVEL_2="2";
    /**商户等级:  3：三级*/
    public static final String RISK_LEAVEL_3="3";
    /**商户等级:  4:四级*/
    public static final String RISK_LEAVEL_4="4";
	/**
	 * 1 可用
	 */
	public static final String RCS_IS_USE="1"; 
	/**
	 * 0 不可用
	 */
	public static final String RCS_NOT_USE="0"; 
	/**
	 * 客户类型: 0用户 
	 */
	public static final String IS_USER="0"; 
	/**
	 * 客户类型: 1商户
	 */
	public static final String IS_COMP="1"; 
	
	/**
	 * 客户类型: 2企业
	 */
	public static final String IS_ENT="2"; 
	
	
	/**
	 * 数据字典中所属菜单模块编号
	 */
	public static final String DICT_USER_TRAN_TYPE="001";
	/**
	 * 数据字典中所属菜单模块编号
	 */
	public static final String DICT_COM_TYPE="002";
	/**
	 * 数据字典中商户业务类型
	 */
	public static final String DICT_MENU_MODULE="003";
	/**
	 * 数据字典中涉恐犯罪类型编号
	 */
	public static final String DICT_OFFENCE_TYPE="004";
	/**
	 * 数据字典中菜单级别类型编号
	 */
	public static final String DICT_MENU_TYPE="005";
	/**
	 * 合同证件类型
	 */
	public static final String DICT_MAT_TYPE="006";
	/**
	 * 商户交易类型
	 */
	public static final String DICT_COMP_TRAN_TYPE="007";
	/**
	 * 按钮类型
	 */
	public static final String DICT_BUTTON_TYPE="008";
	/**
	 * 支付平台数据源接口文件类型
	 */
	public static final String TRAN_FILE_TYPE=".xml";
	 
	/**
	 * 用户初始化密码
	 */
	public static final String RCS_USER_DEFUALT_PWD="12345678";
	/**
	 * 商户交易类型默认值：交易限额
	 */
	public static final String COMP_COMP_TYPE_DEFAULT="1"; 
	/**
	 * 商户业务类型默认值：其他
	 */
	public static final String COMP_BUS_CODE_DEFAULT="002023"; 
	/**
	 * 商户支付类型默认值：其他
	 */
	public static final String COMP_BUS_CLIENT_DEFAULT="002023001"; 
	/**
	 * 用户业务类型默认值：充值
	 */
	public static final String USER_BUS_CODE_DEFAULT="001001"; 
	/**
	 * 用户支付类型默认值：终端充值
	 */
	public static final String USER_BUS_CLIENT_DEFAULT="001001002"; 
 
	/**
	 * 风险等级默认值：一级
	 */
	public static final String RISK_LEAVEL_DEFAULT="1"; 
	/**
	 * 认证状态默认值：已认证
	 */
	public static final String USER_APP_FLAG_DEFAULT="1";

	/**
	 * 试用期状态默认值：非试用期
	 */
	public static final String IS_TYPE_DEFAULT="0";
	
}
