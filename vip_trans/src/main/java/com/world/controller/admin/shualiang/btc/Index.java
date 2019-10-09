package com.world.controller.admin.shualiang.btc;

import com.tenstar.HTTPTcp;
import com.world.cache.Cache;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "BTC刷量设置")
public class Index extends UserAction  {

	@Page(Viewer = "/admins/shualiang/autoSys.jsp") 
	public void index(){//因为编码问题，没时间解决，先这样
		autoSysBtc();
	}
	
	/**系统配置
	 */
	@Page(Viewer = "/admins/shualiang/autoSys.jsp" ) 
	public void autoSysBtc(){ 
		Market m = Market.getMarkeByName("btc_cny");
		if(m==null){ 
			json("",false,L("错误的市场"));
			log.error("无此币种 btc_cny");
			return;
		}
		request.setAttribute("serijavascripparam", m.toString());
		Object obj=Cache.Get("systemDefaultRobotShualiangConfigBTC");
		
		if(obj == null){
			request.setAttribute("rc", new com.tenstar.RobotShualiangConfig());
			return;
		}
		
		com.tenstar.RobotShualiangConfig rc=(com.tenstar.RobotShualiangConfig)HTTPTcp.StringToObject(obj.toString());
		request.setAttribute("rc", rc);
	}
}