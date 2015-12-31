package com.util;


public final class SqlUtil
{
	/**
	 * 自增长列 sequences，每次增长1
	 * 用于自增长id的插入，用法SEQ_ON_RCS.nextval
	 * eg:
	 * INSERT INTO emp VALUES   
			(SEQ_ON_RCS.nextval, 'LEWIS', 'CLERK',7902, SYSDATE, 1200, NULL, 20);   
	 */
	 public static final String SEQUENCES_ID_ADD1 ="SEQ_ON_RCS.nextval";  
	 //客户X名单系统生成历史记录表
	 public static final String SEQ_RCSXLISTSYS_ID_ADD1 ="SEQ_RCSXLISTSYS_ID.nextval";
	 
	 /**
	  * 菜单表自动递增SEQ：MENUID
	  */
	 public static final String SEQ_RCSMENUINFO_MENUID ="SEQ_RCSMENUINFO_MENUID.NEXTVAL";
	 
	 /**
	  * 角色表自动递增SEQ：ROLEID
	  */
	 public static final String SEQ_RCSROLEINFO_ROLEID ="SEQ_RCSROLEINFO_ROLEID.NEXTVAL";
	 
	 /**
	  * 用户表自动递增SEQ：ID
	  */
	 public static final String SEQ_RCSUSERINFO_ID ="SEQ_RCSUSERINFO_ID.NEXTVAL";
	 
}