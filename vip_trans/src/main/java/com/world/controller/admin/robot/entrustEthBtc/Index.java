package com.world.controller.admin.robot.entrustEthBtc;

import com.tenstar.HTTPTcp;
import com.world.cache.Cache;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "ETH/BTC ENTRUST ROBOT")
public class Index extends UserAction  {

	@Page(Viewer = "/admins/robot/entrust/autoSysEthBtc.jsp") 
	public void index(){//因为编码问题，没时间解决，先这样
		autoSysEthBtc();
	}
	
	/**系统配置
	 */
	@Page(Viewer = "/admins/robot/entrust/autoSysEthBtc.jsp" ) 
	public void autoSysEthBtc(){ 
		Market m = Market.getMarkeByName("eth_btc");
		if(m==null){ 
			json("",false,L("错误的市场"));
			log.error("无此币种 eth_btc");
			return;
		}
		request.setAttribute("serijavascripparam",m.toString());
		Object obj=Cache.Get("systemDefaultRobotEntrustConfigETHBTC");
		
		if(obj == null){
			request.setAttribute("rc", new com.tenstar.RobotEntrustConfig());
			return;
		}
		
		com.tenstar.RobotEntrustConfig	rc=(com.tenstar.RobotEntrustConfig)HTTPTcp.StringToObject(obj.toString());
		request.setAttribute("rc", rc);
	}
}