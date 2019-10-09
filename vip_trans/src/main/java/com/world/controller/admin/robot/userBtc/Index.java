package com.world.controller.admin.robot.userBtc;

import com.tenstar.HTTPTcp;
import com.world.cache.Cache;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "BTC USER")
public class Index extends UserAction  {

	@Page(Viewer = "/admins/robot/autoUser.jsp" ) 
	public void index(){
		autoUserBtc();
	}

	/**系统配置
	 */
	@Page(Viewer = "/admins/robot/autoUser.jsp" ) 
	public void autoUserBtc(){
		Market m = Market.getMarkeByName("btc_cny");
		if(m==null){ 
			json("",false,L("错误的市场"));
			log.error("无此币种 btc_cny");
			return;
		}
		
		request.setAttribute("serijavascripparam", m.toString());
		
		int userId=userId();
		
		Object obj=Cache.Get("userAutoConfigBTC"+userId);
		
		com.tenstar.UserConfig	 rc;
		if(obj!=null)
			rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
		else
			rc=new com.tenstar.UserConfig();
		request.setAttribute("rc", rc);
 
	}
}