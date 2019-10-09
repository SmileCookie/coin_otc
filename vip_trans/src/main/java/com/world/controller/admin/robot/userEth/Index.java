package com.world.controller.admin.robot.userEth;

import com.tenstar.HTTPTcp;
import com.world.cache.Cache;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "ETH USER")
public class Index extends UserAction  {

	@Page(Viewer = "/admins/robot/autoUserEth.jsp" ) 
	public void index(){
		autoUserEth();
	}

	/**系统配置
	 */
	@Page(Viewer = "/admins/robot/autoUserEth.jsp" ) 
	public void autoUserEth(){
		Market m = Market.getMarkeByName("eth_cny");
		if(m==null){ 
			json("",false,L("错误的市场"));
			log.error("无此币种 eth_cny");
			return;
		}
		request.setAttribute("serijavascripparam", m.toString());
		
		int userId=userId();
		
		Object obj=Cache.Get("userAutoConfigETH"+userId);
		
		com.tenstar.UserConfig	 rc;
		if(obj!=null)
			rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
		else
			rc=new com.tenstar.UserConfig();
		request.setAttribute("rc", rc);
 
	}
}