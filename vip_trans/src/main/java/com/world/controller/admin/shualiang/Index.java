package com.world.controller.admin.shualiang;

import com.tenstar.HTTPTcp;
import com.tenstar.RobotShualiangConfig;
import com.world.controller.IndexServer;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "刷量管理")
public class Index extends UserAction  {

	private IndexServer server = new IndexServer();
	@Page(Viewer = "") 
	public void index(){//因为编码问题，没时间解决，先这样
	}
	
	
	
	
	/**系统配置
	 */
	@Page(Viewer = ".xml" ) 
	public void saveAutoSys(){ 
		Market m=Market.getMarkeByName(GetPrama(0));
		if(m==null){
			Write("",false,"错误的市场");
			return;
		} 
		try{
		int webid=m.webId; 
		
		RobotShualiangConfig rc=new RobotShualiangConfig();
	 
		int ktype = Integer.parseInt(request.getParameter("ktype"));
		
		if(ktype != 1 && ktype != 3 && ktype != 5 && ktype != 15 && ktype != 30)
			ktype = 5;
		
		rc.setWebId(webid);
		rc.setDfAmount(new Double(request.getParameter("dfAmount")));
		rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));
		rc.setRate(new Double(request.getParameter("rate")));
		rc.setKtype(ktype);
		rc.setStartWave(Integer.parseInt(request.getParameter("startWave")));
		rc.setEndWave(Integer.parseInt(request.getParameter("endWave")));
    try{
    	String rtn= server.saveAutoShualiangParams(rc,m);
	    if(rtn.equals("ok"))
	            Write(rtn,true,rtn+"");
	    else
	  	        Write(rtn,false,rtn+"");
		 
		 }catch(Exception ex2){
	        	Write("保存失败！",false,"");
	        }
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
}