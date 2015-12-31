package com.rcs.interfaces;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.apache.commons.lang.StringUtils;
import tangdi.annotations.Code;
import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;
import tangdi.expr.functions.TdExpBasicFunctions;
import com.util.RcsDefault;
import com.util.RcsUtils;
 
@tangdi.engine.DB
public class CthdInterFace {
	@AroundInvoke
	public Object around(InvocationContext ic) throws Exception {
		Msg.dump();
		Object obj = ic.proceed();
		Msg.dump();
		return obj;
	}
	
	
	/**
	 * 交易限额接入接口
	 * @throws Exception
	 */
	@Code("540503")
	public void importAmtLimit(){
		Log.info("交易限额接口调入……");
		String sAmt      = Etf.getChildValue("AMT");//本次交易金额
		String sUserCode = Etf.getChildValue("USER_CODE");//用户编码
		String sCompCode = Etf.getChildValue("COMP_CODE");//商户编码
		String sPayType  = Etf.getChildValue("PAY_TYPE"); //支付方式  01网银、02终端、03消费卡、04虚拟账户、05网银、06快捷支付
		String sDate     = Etf.getChildValue("DATE");//交易时间(即系统日期，可以自己取)
		String sCustType = Etf.getChildValue("CUST_TYPE");//客户类型 1用户 2 商户 0 两者
		String sTranType = Etf.getChildValue("TRAN_TYPE");//交易类型 0 其他 1 充值 2消费 3撤销 4预授权完成 5预授权完成撤销 6转账(虚拟帐号间) 7提现 8退款
		String balance   = Etf.getChildValue("BALANCE");//用户当前余额
		
		if (sTranType.equals("")){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "交易类型不能为空！");
			return;
		}
		
		Log.info("效验客户类型(1用户 2 商户 0 用户和商户两者  3企业 4 企业和商户两者  ) :"+sCustType);
		
		//判断用户是否存在
		if (sCustType.equals("1") && ToolsInterFaceBussess.isExistUser(sUserCode)) {
			//判断用户是否是黑名单
			String sUserRiskFlag = ToolsInterFaceBussess.getUserRiskFlag(sUserCode);
			if(sUserRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "用户:"+sUserCode+"为黑名单");
				return;
			}else{
				//封装用户限额信息
				LimitUserObj limitUser = ToolsInterFaceBussess.getUserLimitAmt(sUserCode,sUserRiskFlag,sPayType,sDate,sTranType);
				//判断用户限额设置
				Log.info("返回封装后的limitUser = "+limitUser);
				if(limitUser !=null && ToolsInterFaceBussess.isOutLimitUser(limitUser,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "用户:"+sUserCode+"通过");
					return;
				}
			}
		}else if (sCustType.equals("2") && ToolsInterFaceBussess.isExistComp(sCompCode)) {
			//查看该商户状态，若黑则不可交易
			String compRiskFlag = ToolsInterFaceBussess.getCompRiskFlag(sCompCode);
			if(compRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "商户:"+sCompCode+"为黑名单");
				return;
			}else{
				//封装商户限额信息
				LimitCompObj limitComp = ToolsInterFaceBussess.getCompLimitAmt(sCompCode,compRiskFlag,sPayType,sDate);
				//返回该商户对应限额设置
				Log.info("返回封装后的limitComp = "+limitComp);
				if(limitComp!= null && ToolsInterFaceBussess.isOutLimitComp(limitComp,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO"  , "商户:"+sCompCode+"通过");
					return;
				}
			}
		}else if (sCustType.equals("0") && ToolsInterFaceBussess.isExistUser(sUserCode) && ToolsInterFaceBussess.isExistComp(sCompCode) ) {
			Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>查看用户");
			//获得用户X名单状态
			String userRiskFlag = ToolsInterFaceBussess.getUserRiskFlag(sUserCode);
			Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>ToolsInterFaceBussess.getUserRiskFlag(userCode):"+userRiskFlag);
			//黑名单不能进行交易
			if(userRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "用户:"+sUserCode+"为黑名单");
				return;
			}else{
				Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>进入ToolsInterFaceBussess.getUserLimitAmt方法前");
				//返回该用户对应限额设置
				LimitUserObj limituser = ToolsInterFaceBussess.getUserLimitAmt(sUserCode,userRiskFlag,sPayType,sDate,sTranType);
				if(limituser !=null && ToolsInterFaceBussess.isOutLimitUser(limituser,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					//Etf.setChildValue("NO_INFO", "用户:"+userCode+"限额不通过");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "用户:"+sUserCode+"通过");
				}
			}
			
			Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>查看商户");
			String compRiskFlag = ToolsInterFaceBussess.getCompRiskFlag(sCompCode);
			if(compRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "商户:"+sCompCode+"为黑名单");
				return;
			}else{
				//返回该商户对应限额设置
				LimitCompObj limitComp = ToolsInterFaceBussess.getCompLimitAmt(sCompCode,compRiskFlag,sPayType,sDate);
				if(limitComp!= null && ToolsInterFaceBussess.isOutLimitComp(limitComp,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					//Etf.setChildValue("NO_INFO", "商户:"+compCode+"限额不通过");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "商户:"+sCompCode+"通过");
					return;
				}
			}
		}else if(sCustType.equals("3") && ToolsInterFaceBussess.isExistEnt(sUserCode)){
			//判断企业是否是黑名单
			String sUserRiskFlag = ToolsInterFaceBussess.getEntRiskFlag(sUserCode);
			if(sUserRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "企业:"+sUserCode+"为黑名单");
				return;
			}else{
				
				//封装企业限额信息
				LimitUserObj limitUser = ToolsInterFaceBussess.getEntLimitAmt(sUserCode,sUserRiskFlag,sPayType,sDate,sTranType);
				//判断企业限额设置
				Log.info("返回封装后的limitEnt = "+limitUser);
				if(limitUser !=null && ToolsInterFaceBussess.isOutLimitUser(limitUser,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "企业:"+sUserCode+"通过");
					return;
				}
				
				
			}
			
		}else if(sCustType.equals("4") && ToolsInterFaceBussess.isExistEnt(sUserCode)&& ToolsInterFaceBussess.isExistComp(sCompCode) ){
			//判断企业是否是黑名单
			String sUserRiskFlag = ToolsInterFaceBussess.getEntRiskFlag(sUserCode);
			if(sUserRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "企业:"+sUserCode+"为黑名单");
				return;
			}else{
				
				//封装企业限额信息
				LimitUserObj limitUser = ToolsInterFaceBussess.getEntLimitAmt(sUserCode,sUserRiskFlag,sPayType,sDate,sTranType);
				//判断企业限额设置
				Log.info("返回封装后的limitEnt = "+limitUser);
				if(limitUser !=null && ToolsInterFaceBussess.isOutLimitUser(limitUser,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "企业:"+sUserCode+"通过");
					
				}
			}
			
			Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>查看商户");
			String compRiskFlag = ToolsInterFaceBussess.getCompRiskFlag(sCompCode);
			if(compRiskFlag.equals(RcsDefault.LIMIT_RISK_FLAG_2)){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "商户:"+sCompCode+"为黑名单");
				return;
			}else{
				//返回该商户对应限额设置
				LimitCompObj limitComp = ToolsInterFaceBussess.getCompLimitAmt(sCompCode,compRiskFlag,sPayType,sDate);
				if(limitComp!= null && ToolsInterFaceBussess.isOutLimitComp(limitComp,sAmt)){
					Etf.setChildValue("PASS_ORNO", "0");
					//Etf.setChildValue("NO_INFO", "商户:"+compCode+"限额不通过");
					return;
				}else{
					Etf.setChildValue("PASS_ORNO", "1");
					Etf.setChildValue("NO_INFO", "商户:"+sCompCode+"通过");
					return;
				}
			}
			
		}else{
			return;
		}
	}
	
	/**
	 * 计算剩余交易限额接口
	 * @throws Exception
	 */
	@Code("540510")
	public void calcReaminLimitBalance(){
		String sAmt      = Etf.getChildValue("AMT");//本次交易金额
		String sUserCode = Etf.getChildValue("USER_CODE");//用户编码
		String sCompCode = Etf.getChildValue("COMP_CODE");//商户编码
		String sPayType  = Etf.getChildValue("PAY_TYPE"); //支付方式  01网银、02终端、03消费卡、04虚拟账户、05网银、06快捷支付
		String sDate     = Etf.getChildValue("DATE");//交易时间(即系统日期，可以自己取)
		String sCustType = Etf.getChildValue("CUST_TYPE");//客户类型 1用户 2 商户 0 两者
		String sTranType = Etf.getChildValue("TRAN_TYPE");//交易类型 0 其他 1 充值 2消费 3撤销 4预授权完成 5预授权完成撤销 6转账(虚拟帐号间) 7提现 8退款
		if (sTranType.equals("")){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "交易类型不能为空！");
			return;
		}
		
		Log.info("效验客户类型(1用户 2 商户 0 两者) :"+sCustType);
		
		//判断用户是否存在
		if (sCustType.equals("1") && ToolsInterFaceBussess.isExistUser(sUserCode)) {
			//判断用户是否是黑名单
			String sUserRiskFlag = ToolsInterFaceBussess.getUserRiskFlag(sUserCode);
			//封装用户限额信息
			LimitUserObj limitUser = ToolsInterFaceBussess.getUserLimitAmt(sUserCode,sUserRiskFlag,sPayType,sDate,sTranType);
			
			//设置用户限额余额
			ToolsInterFaceBussess.setRemainBalanceLimitUser(limitUser,sAmt);
		}else if (sCustType.equals("2") && ToolsInterFaceBussess.isExistComp(sCompCode)) {
			//查看该商户状态，若黑则不可交易
			String compRiskFlag = ToolsInterFaceBussess.getCompRiskFlag(sCompCode);
			//封装商户限额信息
			LimitCompObj limitComp = ToolsInterFaceBussess.getCompLimitAmt(sCompCode,compRiskFlag,sPayType,sDate);
			
			//设置商户限额余额
			ToolsInterFaceBussess.setRemainBalanceLimitComp(limitComp,sAmt);
		}else if (sCustType.equals("0") && ToolsInterFaceBussess.isExistUser(sUserCode) && ToolsInterFaceBussess.isExistComp(sCompCode) ) {
			//获得用户X名单状态
			String userRiskFlag = ToolsInterFaceBussess.getUserRiskFlag(sUserCode);
			//返回该用户对应限额设置
			LimitUserObj limitUser = ToolsInterFaceBussess.getUserLimitAmt(sUserCode,userRiskFlag,sPayType,sDate,sTranType);
			
			String compRiskFlag = ToolsInterFaceBussess.getCompRiskFlag(sCompCode);
			//返回该商户对应限额设置
			LimitCompObj limitComp = ToolsInterFaceBussess.getCompLimitAmt(sCompCode,compRiskFlag,sPayType,sDate);
			
			//封装用户限额信息
			ToolsInterFaceBussess.setRemainBalanceLimitUser(limitUser,sAmt);
			
			//设置商户限额余额
			ToolsInterFaceBussess.setRemainBalanceLimitComp(limitComp,sAmt);
		}
	}
	
	/**
	 * 用户信息接口
	 * @throws Exception
	 */
	@Code("540504")
	public void importUser() throws Exception {
		Log.info("用户信息接口调入……");
		
		String dType         = Etf.getChildValue("DTYPE");//操作类型
		String uCode         = Etf.getChildValue("UCODE");//用户编码
		String uName         = Etf.getChildValue("UNAME");//用户姓名
		String paperType     = Etf.getChildValue("PAPERTYPE");//证件类型   需要翻译成汉字
		String pCode         = Etf.getChildValue("PCODE");//证件号
		String paperRemaDate = Etf.getChildValue("PAPERREMDATE");//证件到期日期 
		String userAppFlag   = Etf.getChildValue("USERAPPFLAG");//认证状态  需要转换
		String isType        = Etf.getChildValue("ISTYPE");//是否试用期  1：试用期，0：非试用期
		String date          = Etf.getChildValue("DATE");//处理日期 
		String time          = Etf.getChildValue("TIME");//处理时间 
	    String address       = Etf.getChildValue("ADDRESS");//客户地址
		if(isType == null){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO"  , "输入字段为null");
			return ;
		}
		Log.info("操作类型(1新增，2修改，3删除)：" + dType);
		if("1".equals(dType)){ //添加用户
			String userSql = "INSERT INTO RCS_TRAN_USER_INFO() VALUES()";
			userSql = RcsUtils.addColAndVal(userSql, "USER_CODE"        , uCode);
			userSql = RcsUtils.addColAndVal(userSql, "USER_NAME"        , uName);
			userSql = RcsUtils.addColAndVal(userSql, "USER_APP_FLAG"    , userAppFlag);
			userSql = RcsUtils.addColAndVal(userSql, "IS_TYPE"          , isType);
			userSql = RcsUtils.addColAndVal(userSql, "RISK_FLAG"        , RcsDefault.LIMIT_RISK_FLAG_0);
			userSql = RcsUtils.addColAndVal(userSql, "PAPER_TYPE"       , paperType);
			userSql = RcsUtils.addColAndVal(userSql, "PAPER_CODE"       , pCode);
			userSql = RcsUtils.addColAndVal(userSql, "PAPER_REM_DATE"   , paperRemaDate);
			userSql = RcsUtils.addColAndVal(userSql, "REGISTER_DATE"    , date);
			userSql = RcsUtils.addColAndVal(userSql, "REGISTER_DATETIME", time);
			userSql = RcsUtils.addColAndVal(userSql, "IS_USE"           , "1");
			userSql = RcsUtils.addColAndVal(userSql, "TRAN_USER_CODE"   , uCode);
			userSql = RcsUtils.addColAndVal(userSql, "UPDATE_DATE"      , date);
			userSql = RcsUtils.addColAndVal(userSql, "UPDATE_DATETIME"  , time);
			userSql = RcsUtils.addColAndVal(userSql, "RISK_LEAVEL"      , RcsDefault.RISK_LEAVEL_0);
			
			//替换两个,)
			userSql = userSql.replaceAll(",\\)", ")");
			
			int countResult = Atc.ExecSql(null, userSql);
			
			Log.info("countResult = >>>"+countResult);
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else if("2".equals(dType)){
			String userSql = "update RCS_TRAN_USER_INFO ";
			userSql += "set ";
			userSql += "user_app_flag = '"+userAppFlag+"', ";
			userSql += "IS_TYPE       = '"+isType+"' ";
			if(null != paperRemaDate){
				userSql += ",PAPER_REM_DATE = '"+paperRemaDate+"' ";
			}
			userSql += "where USER_CODE='"+uCode+"'";
			int countResult = Atc.ExecSql(null, userSql);
            
            Log.info("countResult = >>"+countResult);
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}else if(countResult == 2){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			} 
		}else if("3".equals(dType)){ //删除用户
			String userSql = "update RCS_TRAN_USER_INFO set is_use='0' where user_code='"+uCode+"'";
			int countResult = Atc.ExecSql(null, userSql);
			
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else{
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
			return;
		}
		
		Etf.setChildValue("PASS_ORNO", "1");
		Etf.setChildValue("NO_INFO", "通过……");
		
		//如果犯罪分子匹配证件类型+证件号码 则认为是犯罪分子
        String usercheckSql = "SELECT * FROM RCS_OFFENCE_INFO t WHERE t.PAPER_TYPE ='"+paperType+"' and t.PAPER_CODE ='"+pCode+"'";
		
        int countResult = Atc.ExecSql(null, usercheckSql);
        Log.info("查询对应涉恐分子信息SQL返回值："+countResult);
        //用户名是恐怖分子
        if(countResult==0) {
        	String userupdateSql = "UPDATE RCS_TRAN_USER_INFO SET RISK_FLAG='2' WHERE USER_CODE = '"+uCode+"'";	
			countResult = Atc.ExecSql(null, userupdateSql);	
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}
			
        	String useraddSql = "INSERT INTO RCS_X_LIST_SYS(ID,USER_TYPE,CLIENT_CODE,X_TYPE)"
        		+" VALUES(SEQ_RCSXLISTSYS_ID.NEXTVAL,'"+userAppFlag +"','"+uCode+"','3')";
			countResult = Atc.ExecSql(null, useraddSql);	
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}
        } 
        if(countResult==-1) {
        	Etf.setChildValue("NO_INFO", "系统错误");
       	}
	}
	
	
	/**
	 * 企业信息接口
	 * @throws Exception
	 */
	@Code("540510")
	public void importEnterprise() throws Exception {
		Log.info("企业信息接口调入……");
		
		String dType         = Etf.getChildValue("DTYPE");//操作类型
		String eCode         = Etf.getChildValue("ECODE");//企业编码
		String eName         = Etf.getChildValue("ENAME");//企业名称
		String legPerson     = Etf.getChildValue("LEGPER");//企业法人
		String paperType     = Etf.getChildValue("PAPERTYPE");//证件类型   需要翻译成汉字
		String pCode         = Etf.getChildValue("PCODE");//证件号
		//String paperRemaDate = Etf.getChildValue("PAPERREMDATE");//证件到期日期 
		String userAppFlag   = Etf.getChildValue("USERAPPFLAG");//认证状态  需要转换
		String isType        = Etf.getChildValue("ISTYPE");//是否试用期  1：试用期，0：非试用期
		String date          = Etf.getChildValue("DATE");//处理日期 
		String time          = Etf.getChildValue("TIME");//处理时间 
		if(isType == null){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO"  , "输入字段为null");
			return ;
		}
		Log.info("操作类型(1新增，2修改，3删除)：" + dType);
		if("1".equals(dType)){ //添加用户
			String userSql = "INSERT INTO RCS_TRAN_ENT_INFO() VALUES()";
			userSql = RcsUtils.addColAndVal(userSql, "ENT_CODE"        , eCode);
			userSql = RcsUtils.addColAndVal(userSql, "ENT_NAME"       , eName);
			userSql = RcsUtils.addColAndVal(userSql, "LEGAL_PERSON"     , legPerson);
			userSql = RcsUtils.addColAndVal(userSql, "USER_APP_FLAG"    , userAppFlag);
			userSql = RcsUtils.addColAndVal(userSql, "IS_TYPE"          , isType);
			userSql = RcsUtils.addColAndVal(userSql, "RISK_FLAG"        , RcsDefault.LIMIT_RISK_FLAG_0);
			userSql = RcsUtils.addColAndVal(userSql, "PAPER_TYPE"       , paperType);
			userSql = RcsUtils.addColAndVal(userSql, "PAPER_CODE"       , pCode);
			//userSql = RcsUtils.addColAndVal(userSql, "PAPER_REM_DATE"   , paperRemaDate);
			userSql = RcsUtils.addColAndVal(userSql, "REGISTER_DATE"    , date);
			userSql = RcsUtils.addColAndVal(userSql, "REGISTER_DATETIME", time);
			userSql = RcsUtils.addColAndVal(userSql, "IS_USE"           , "1");
			userSql = RcsUtils.addColAndVal(userSql, "TRAN_ENT_CODE"   , eCode);
			userSql = RcsUtils.addColAndVal(userSql, "UPDATE_DATE"      , date);
			userSql = RcsUtils.addColAndVal(userSql, "UPDATE_DATETIME"  , time);
			userSql = RcsUtils.addColAndVal(userSql, "RISK_LEAVEL"      , RcsDefault.RISK_LEAVEL_0);
			
			//替换两个,)
			userSql = userSql.replaceAll(",\\)", ")");
			
			int countResult = Atc.ExecSql(null, userSql);
			
			Log.info("countResult = >>>"+countResult);
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else if("2".equals(dType)){
			String userSql = "update RCS_TRAN_ENT_INFO ";
			userSql += "set ";
			userSql += "user_app_flag = '"+userAppFlag+"', ";
			userSql += "IS_TYPE       = '"+isType+"' ";
			/*if(null != paperRemaDate){
				userSql += ",PAPER_REM_DATE = '"+paperRemaDate+"' ";
			}*/
			
			userSql += "where ENT_CODE='"+eCode+"'";
			int countResult = Atc.ExecSql(null, userSql);
            
            Log.info("countResult = >>"+countResult);
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}else if(countResult == 2){
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			} 
		}else if("3".equals(dType)){ //删除用户
			String userSql = "update RCS_TRAN_USER_INFO set is_use='0' where ENT_CODE='"+eCode+"'";
			int countResult = Atc.ExecSql(null, userSql);
			
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else{
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
			return;
		}
		
		Etf.setChildValue("PASS_ORNO", "1");
		Etf.setChildValue("NO_INFO", "通过……");
		
		//如果犯罪分子匹配证件类型+证件号码 则认为是犯罪分子
        String usercheckSql = "SELECT * FROM RCS_OFFENCE_INFO t WHERE t.PAPER_TYPE ='"+paperType+"' and t.PAPER_CODE ='"+pCode+"'";
		
        int countResult = Atc.ExecSql(null, usercheckSql);
        Log.info("查询对应涉恐分子信息SQL返回值："+countResult);
        //用户名是恐怖分子
        if(countResult==0) {
        	String userupdateSql = "UPDATE RCS_TRAN_ENT_INFO SET RISK_FLAG='2' WHERE ENT_CODE = '"+eCode+"'";	
			countResult = Atc.ExecSql(null, userupdateSql);	
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}
			
        	String useraddSql = "INSERT INTO RCS_X_LIST_SYS(ID,USER_TYPE,CLIENT_CODE,X_TYPE)"
        		+" VALUES(SEQ_RCSXLISTSYS_ID.NEXTVAL,'"+userAppFlag +"','"+eCode+"','3')";
			countResult = Atc.ExecSql(null, useraddSql);	
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			}
        } 
        if(countResult==-1) {
        	Etf.setChildValue("NO_INFO", "系统错误");
       	}
	}
	
	
	

	
	
	/**
	 * 商户信息接口
	 * @throws Exception
	 */
	@Code("540505")
	public void importComp() throws Exception {
		String dType    = Etf.getChildValue("DTYPE");//操作类型
		String cCode    = Etf.getChildValue("CCODE");//商户编码(不会更改)
		String cName    = Etf.getChildValue("CNAME");//商户名称
		String compMcc  = Etf.getChildValue("COMPMCC");
		String cType    = Etf.getChildValue("CTYPE");//商户类型
		String cDate    = Etf.getChildValue("CDATE");//合同签订日期(修改时送值)
		String mdSate   = Etf.getChildValue("MDATE");//合同到期日期(修改时送值)
		String bankCad  = Etf.getChildValue("BANKCAD");//开户行行号(修改时送值)
		String bankName = Etf.getChildValue("BANKNAME");//开户行行名(修改时送值)
		String pCad     = Etf.getChildValue("PCAD");//支付帐号
		String rEmark   = Etf.getChildValue("REMARK");//商户描述
		String date     = Etf.getChildValue("DATE");//处理日期
		String time     = Etf.getChildValue("TIME");//处理时间
	    String address  = Etf.getChildValue("ADDRESS");//客户地址
	    
		if(cCode == null || cCode.trim().equals("")){
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO"  , "商户编号不能为空");
			return;
		}
		
		Log.info("操作类型 :"+dType);
		if("1".equals(dType)){ //添加用户 
			Log.info("添加商户信息");
			String compSql = "INSERT INTO RCS_TRAN_COMP_INFO() VALUES()";
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_CODE"                , cCode);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_NAME"                , cName);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_SHOT_NAME"           , cName);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_MCC"                 , compMcc);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_TYPE"                , cType);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_CREATE_REGISTER_DATE", cDate);
		    compSql = RcsUtils.addColAndVal(compSql, "COMP_REMOVE_REGISTER_DATE", mdSate);
			compSql = RcsUtils.addColAndVal(compSql, "RISK_FLAG"                , RcsDefault.LIMIT_RISK_FLAG_0);
			compSql = RcsUtils.addColAndVal(compSql, "REGISTER_DATE"            , date);
			compSql = RcsUtils.addColAndVal(compSql, "REGISTER_DATETIME"        , time);
			compSql = RcsUtils.addColAndVal(compSql, "IS_USE"                   , "1");
			compSql = RcsUtils.addColAndVal(compSql, "CAD_BANK"                 , bankCad);
			compSql = RcsUtils.addColAndVal(compSql, "PAY_CAD"                  , pCad);
			compSql = RcsUtils.addColAndVal(compSql, "REMARK"                   , rEmark);
			compSql = RcsUtils.addColAndVal(compSql, "TRAN_COMP_CODE"           , cCode);
			compSql = RcsUtils.addColAndVal(compSql, "UPDATE_DATE"              , date);
			compSql = RcsUtils.addColAndVal(compSql, "UPDATE_DATETIME"          , time);
			compSql = RcsUtils.addColAndVal(compSql, "RISK_LEAVEL"              , RcsDefault.RISK_LEAVEL_0);
			
			//替换两个,)
			compSql = compSql.replaceAll(",\\)", ")");
				   
			int countResult = Atc.ExecSql(null, compSql);
			
			if(countResult != 0) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else if("2".equals(dType)){
			Log.info("修改商户信息");
			
			String upcompSql = "UPDATE RCS_TRAN_COMP_INFO SET COMP_NAME='"+cName+"',COMP_TYPE='"+cType+"',COMP_CREATE_REGISTER_DATE='"+cDate+"',COMP_REMOVE_REGISTER_DATE='"+mdSate+
					           "',CAD_BANK='"+(bankCad+bankName)+"' WHERE COMP_CODE='"+cCode+"'";
			
            int countResult = Atc.ExecSql(null, upcompSql);
			
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO", "未通过……，请重新发送");
				return;
			} 
		}else if("3".equals(dType)){
			Log.info("删除商户信息");
			String userSql = "UPDATE RCS_TRAN_COMP_INFO SET IS_USE='0' WHERE USER_CODE='"+cCode+"'";
			int countResult = Atc.ExecSql(null, userSql);
			
			if(countResult == -1) {
				Etf.setChildValue("PASS_ORNO", "0");
				Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
				return;
			} 
		}else{
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO"  , "未通过……，请重新发送");
			return;
		}
		
		Etf.setChildValue("PASS_ORNO", "1");
		Etf.setChildValue("NO_INFO"  , "通过……");
	}
	
	
	
	/**
	 * 实时交易接口
	 * @throws Exception
	 */
	@Code("540507")
	public void importTran() throws Exception {
		String dType       = Etf.getChildValue("DTYPE");//操作类型
		String tCode       = Etf.getChildValue("TCODE");//交易流水号
		String tranSource  = Etf.getChildValue("TRAN_SOURCE");//交易来源
		String cType       = Etf.getChildValue("CTYPE");//客户类型   0用户  1 商户
		String clientCode  = Etf.getChildValue("CLIENTCODE");//用户编号
		String clientName  = Etf.getChildValue("CLIENTNAME");//用户名称
		String bcNo        = Etf.getChildValue("BANK_CARD_NO");//银行卡号
		String bankDepNo   = Etf.getChildValue("BANK_DEP_NO");//用于判断卡bin信息:发卡行机构代码
		String bankCardLen = Etf.getChildValue("BANK_CARD_LEN");//主帐号长度
		String bankSignVal = Etf.getChildValue("BANK_SIGN_VAL");//发卡行标识取值
		String cardFlg     = Etf.getChildValue("CARD_FLG");//卡种
		String coinFlg     = Etf.getChildValue("COIN_FLG");//币种
		String tranType    = Etf.getChildValue("TRAN_TYPE");//交易类型    
		String tranClient  = Etf.getChildValue("TRAN_CLIENT");//交易方式
		String tAmt        = Etf.getChildValue("TAMT");//交易金额
		String tranStatus  = Etf.getChildValue("TRAN_STATUS");
		String cCode       = Etf.getChildValue("CCODE");//商户编码
		String cName       = Etf.getChildValue("CNAME");//商户名称
		String time        = Etf.getChildValue("REGDTTIME");//交易时间
		String date        = Etf.getChildValue("TIME");//处理日期
		
		//初始化
		if(clientCode.trim().equals("")){
			clientCode = RcsDefault.USER_CODE_DEFAULT;
		}
		if(cardFlg.trim().equals("")){
			cardFlg = RcsDefault.CARD_FLG_FLG_DEFAULT;
		}
		if(cardFlg.trim().equals("")){
			coinFlg = RcsDefault.COIN_FLG_DEFAULT;
		}
		 
		//只有成功时才发次交易 没有删除、修改操作
		if(dType.equals("1")){
			String insertSql = "INSERT INTO RCS_TRAN_SERIAL_RECORD(ID,) VALUES("+RcsDefault.RCS_TRAN_SERIAL_RECORD_ID+",)";
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_SOURCE"  , tranSource);
			insertSql = RcsUtils.addColAndVal(insertSql, "CLIENT_TYPE"  , cType);
			insertSql = RcsUtils.addColAndVal(insertSql, "USER_CODE"    , clientCode);
			insertSql = RcsUtils.addColAndVal(insertSql, "USER_NAME"    , clientName);
			insertSql = RcsUtils.addColAndVal(insertSql, "BANK_CARD_NO" , bcNo);
			insertSql = RcsUtils.addColAndVal(insertSql, "BANK_DEP_NO"  , bankDepNo);
			insertSql = RcsUtils.addColAndVal(insertSql, "BANK_CARD_LEN", bankCardLen);
			insertSql = RcsUtils.addColAndVal(insertSql, "BANK_SIGN_VAL", bankSignVal);
			insertSql = RcsUtils.addColAndVal(insertSql, "CARD_FLG"     , cardFlg);
			insertSql = RcsUtils.addColAndVal(insertSql, "COIN_FLG"     , coinFlg);
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_TYPE"    , tranType);
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_CLIENT"  , tranClient);
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_AMT"     , tAmt);
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_STATUS"  , tranStatus);
			insertSql = RcsUtils.addColAndVal(insertSql, "COMP_CODE"    , cCode);
			insertSql = RcsUtils.addColAndVal(insertSql, "COMP_NAME"    , cName);
			insertSql = RcsUtils.addColAndVal(insertSql, "REGDT_DAY"    , date);
			insertSql = RcsUtils.addColAndVal(insertSql, "REGDT_TIME"   , time);
			insertSql = RcsUtils.addColAndVal(insertSql, "TRAN_CODE"    , tCode);
			
			//替换两个,)
			insertSql = insertSql.replaceAll(",\\)", ")");		
					
			int result = Atc.ExecSql(null, insertSql);
			if (result != 0) {
				Etf.setChildValue("PASS_ORNO", "0");		
				Etf.setChildValue("NO_INFO", "未成功插入，请重新发送");
				return;
			}
		} else{
			Etf.setChildValue("PASS_ORNO", "0");
			Etf.setChildValue("NO_INFO", "操作类型错误");
			return;
		}
		Etf.setChildValue("PASS_ORNO", "1");
		Etf.setChildValue("NO_INFO", "通过");
	}
	
	/**
	 * 合同/证件到期接口
	 * @throws Exception
	 */
	@Code("540508")
	public void matTixing() throws Exception {
		Log.info("合同证件到期提醒接口");
		
		String cCode = Etf.getChildValue("CCODE");//客户编码 同支付平台客户编码
		boolean b    = ToolsInterFaceBussess.isExistComp(cCode);
		
		Log.info("是否存在该商户:"+cCode+",返回："+b);
		
		if( b == false){ 
			Etf.setChildValue("STATUS", "1");
			Etf.setChildValue("FTYPE","-");
			Etf.setChildValue("DAYS", "---");
			Etf.setChildValue("NO_INFO", "不存在该商户，请重新发送该商户信息："+cCode);
			return;
		}else{
			String days = ToolsInterFaceBussess.getDaysComp(cCode);
			if(Integer.valueOf(days)>0){
				Etf.setChildValue("FTYPE", "N");
			}else{
				Etf.setChildValue("FTYPE", "Y");
			}
			Etf.setChildValue("DAYS", days);
			Etf.setChildValue("STATUS", "1");
			Etf.setChildValue("NO_INFO", "");
			return;
		}
	}
	
	/**
	 * 风险等级接口
	 * @throws Exception
	 */
	@Code("540509")
	public void getRiskFlag() throws Exception {
		Log.info("风险等级接口");
		String cCode = Etf.getChildValue("CCODE");//编码商户
		
		boolean b = ToolsInterFaceBussess.isExistComp(cCode);
		if(b == false){ 
			Etf.setChildValue("STATUS", "1");
			Etf.setChildValue("GRADE","-");
			Etf.setChildValue("CTYPE", "1");
			Etf.setChildValue("NO_INFO", "不存在该商户，请重新发送该商户信息："+cCode);
			return;
		}
		
		//目前只有商户有等级设置
		String  riskLeavel = ToolsInterFaceBussess.getCompRiskFlag(cCode) ;
		Etf.setChildValue("CTYPE", "1");
		Etf.setChildValue("GRADE",riskLeavel); 
		Etf.setChildValue("STATUS", "1");
		Etf.setChildValue("NO_INFO", "");
		return;
	}
}
