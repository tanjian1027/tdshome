package com.util;

import java.util.List;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

public class GetOwnButton {

	@Named("GetOwnButton")
	public static int getOwnButton(){
		try{
		String menu_id="";
		       menu_id = Etf.getChildValue("MENU_ID");
		if(StringUtils.isEmpty(menu_id)){
			String url = Etf.getChildValue("_REQUESTATTR.REQURL");
			Log.info("原始URL:%s", url);
			if(StringUtils.isEmpty(url)){
				Etf.setChildValue("CON_BUT_DEV", "0");
				Log.info("请求路径为空,默认下一个页面显示为系统初始化!");
				return 0;
			}
			int start = url.lastIndexOf("/")+1;
			int end = url.indexOf("menu_id=")-1;
			String truncateUrl="";
			if(end != -2){
				 truncateUrl=url.substring(start,end);
			}else{
				truncateUrl=url.substring(start);
			}
			Log.info("处理后的URL:%s", truncateUrl);
			//判断当前请求是是否来源一个按钮 如果是 就提示 不对按钮进行控制


			//防止该路径可以匹配多个MENU_ID 即操作员录入不精确导致对下一个页面控制混乱
			int reb =0;
			String sql0 = "select count(MENU_ID) as COUNT_MENU_ID from limmeninf where menu_url  = '" + truncateUrl + "' and node_type='menu'";
			reb = Atc.ReadRecord(sql0);
			if(reb != 0){
				Etf.setChildValue("CON_BUT_DEV", "0");
				Log.info("匹配菜单失败,默认下一个页面显示为系统初始化!");
				return -1;
			}
			int menu_count =  Integer.valueOf(Etf.getChildValue("COUNT_MENU_ID"));
			if(menu_count > 1){
				Etf.setChildValue("CON_BUT_DEV", "0");
				Log.info("匹配菜单总数过多,防止控制混乱,默认下一个页面显示为系统初始化!");
				return 0;
			}
			//仅获取菜单级别MENU_ID 如果通过匹配,MMENU 没有的话就不控制间接也过滤的按钮
			int res = 0;
			String sql1 = "select MENU_ID as L_MENU_ID from limmeninf where menu_url  = '" + truncateUrl + "' and node_type='menu'";
			res = Atc.ReadRecord(sql1);
			if(res !=0){
				if(res == 2){
					//判断该项操作是否是来源于按钮
					Etf.setChildValue("CON_BUT_DEV", "0");
					int rec=0;
					String sql4 = "select count(MENU_ID) as COUNT_BUTTON_ID from limmeninf where menu_url  = '" + truncateUrl + "' and node_type='button'";
					rec = Atc.ReadRecord(sql4);
					int button_count =  Integer.valueOf(Etf.getChildValue("COUNT_BUTTON_ID"));
					if(button_count > 0){
						Log.info("当前请求操作来源于按钮不控制,默认下一个页面显示为系统初始化!");
					}else{
						Log.info("系统中无匹配该请求URL的菜单项,默认下一个页面显示为系统初始化!");
					}
					return 0;
				}else{
					Etf.setChildValue("CON_BUT_DEV", "0");
					Log.info("获取请求路径对应的MENU_ID失败,默认下一个页面显示为系统初始化!");
					return -1;
				}
			}else{
				menu_id = Etf.getChildValue("L_MENU_ID");
			}
		}
		if(StringUtils.isEmpty(menu_id)){
			//如果通过请求路径还是没有找到MENU_ID
			Etf.setChildValue("CON_BUT_DEV", "0");
			Log.info("数据库中无此操作记录,请管理员及时录入路径: %s,默认下一个页面显示为系统初始化!", Etf.getChildValue("_REQUESTATTR.REQURL"));
			return -1;
		}
		//成功找到对应MENU_ID 在查询当前用户拥有该MENU_ID下面的按钮
		Log.info("成功查询到用menu_id:%s", menu_id);
		int ret = 0;
		String account = Etf.getChildValue("SESSIONS.USERACCOUNT");
		String user_id=Etf.getChildValue("SESSIONS.UID");
		String sql2 = "select BUTTON_EN_NAM,BUTTON_SHOW from limbutinf where button_id in ( select distinct button_id from limmeninf where menu_par_id ='"+menu_id+"' and node_type='button' "+
		              "and menu_id in (select menu_id from limmeninf where menu_id in (select menu_id from limumrele where account='"+account+"' and user_id ='"+user_id+"')))";
		       ret = Atc.QueryInGroup(sql2, "BUT_LST", null);
		       if(ret == 0){
		    	   Etf.setChildValue("CON_BUT_DEV", "1");
		    	   int j=0;
		    	   List butlist =Etf.childs("BUT_LST");
			        for (Object obj : butlist) {
			        	if(!StringUtils.isEmpty(obj.toString())){
			        	Element em = (Element)obj;
			        	if(em.element("BUTTON_EN_NAM")!= null && em.element("BUTTON_SHOW")!=null){
			        	  Etf.setChildValue (em.element("BUTTON_EN_NAM").getText(),em.element("BUTTON_SHOW").getText());
			        	j++;
			        	}
			        	}
			        }
			       Log.info("可控制的按钮个数为：%s",j);
                   return 0;
		       }else if (ret == 2) {
		    	   Etf.setChildValue("CON_BUT_DEV", "1");
				   Log.info("可控制的按钮个数为：0");
				   return 0;
				} else if (ret == -1) {
		    	   Etf.setChildValue("CON_BUT_DEV", "0");
				   Log.info("获取按钮集合失败,默认下一个页面显示为系统初始化!");
				   return -1;
				} else {
		    	   Etf.setChildValue("CON_BUT_DEV", "0");
				   Log.info("未知错误,默认下一个页面显示为系统初始化!");
				   return -1;
				}
		     }catch (Exception e) {
				Log.info("处理异常 %s", e);
				Etf.setChildValue("CON_BUT_DEV", "0");
				return -1;
			}
	}
	public static void main(String[] args) {
		String url="";
		int start = url.lastIndexOf("/")+1;
		int end = url.indexOf("menu_id=")-1;
		String truncateUrl="";
		if(end != -2){
			 truncateUrl=url.substring(start,end);
		}else{
			truncateUrl=url.substring(start);
		}
	}

}
